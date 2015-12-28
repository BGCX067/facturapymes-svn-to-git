/**
* @version 1.0
* @package FACTURAPYMES.
* @author Jorge Washington Mueses Cevallos.
* @copyright Copyright (C) 2010 por Jorge Mueses. Todos los derechos reservados.
* @license http://www.gnu.org/copyleft/gpl.html GNU/GPL.
* FACTURAPYMES! es un software de libre distribución, que puede ser
* copiado y distribuido bajo los términos de la Licencia Pública
* General GNU, de acuerdo con la publicada por la Free Software
* Foundation, versión 2 de la licencia o cualquier versión posterior.
*/

package jm.inv;

import java.io.*;

import javax.servlet.*;
import javax.servlet.http.*;
import jm.adm.clas.Configuracion;
import jm.inv.clas.Proveedor;
import jm.inv.clas.FacturaCompra;
import java.sql.ResultSet;
import jm.fac.clas.NotaCreditoVenta;
import jm.inv.clas.LiquidacionCompra;
import jm.web.Fecha;
import jm.inv.clas.NotaDebitoCompra;
import jm.inv.clas.NotaVenta;
import jm.web.Addons;

/**
 *
 * @author Jorge
 */
public class frmProveedorPago extends HttpServlet {
    private String _ip = null;
    private int _puerto = 5432;
    private String _db = null;

    public void init(ServletConfig config) throws ServletException
    {
        this._ip = config.getServletContext().getInitParameter("_IP");
        this._puerto = Integer.parseInt(config.getServletContext().getInitParameter("_PUERTO"));
        this._db = config.getServletContext().getInitParameter("_DB");
    }
    /**
    * Processes requests for both HTTP <code>GET</code> and <code>POST</code> methods.
    * @param request servlet request
    * @param response servlet response
    */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        HttpSession sesion = request.getSession(true);
        String usuario = (String)sesion.getAttribute("usuario");
        String clave = (String)sesion.getAttribute("clave");
        int id_sucursal = (Integer)sesion.getAttribute("sucursal");
        
        response.setContentType("text/html;charset=UTF-8");
        response.setHeader("Pragma", "no-cache");
        response.setHeader("Expires", "Mon, 01 Jan 2001 00:00:01 GMT");
        response.setHeader("Cache-Control", "no-store");
        response.setHeader("Cache-Control", "must-revalidate");
        response.setHeader("Cache-Control", "no-cache");
        PrintWriter out = response.getWriter();

        String idProveedor = request.getParameter("id");
        int altoBody = Integer.parseInt(request.getParameter("aB"));

        Configuracion conf = new Configuracion(this._ip, this._puerto, this._db, usuario, clave);
        //String cxp = conf.getValor("cxp");
        //String caja = conf.getValor("caja");
        //String bancos = conf.getValor("bancos");
        //String chequeEmitidoNoCobrado = conf.getValor("chequeEmitidoNoCobrado");
        String id_sucursal_matriz = conf.getValor("id_sucursal_matriz");
        conf.cerrar();

        Proveedor objProveedor = new Proveedor(this._ip, this._puerto, this._db, usuario, clave);

        FacturaCompra objFactura = new FacturaCompra(this._ip, this._puerto, this._db, usuario, clave);
        ResultSet rsFactura = objFactura.getFacturasPendientes(id_sucursal, idProveedor);
        try {
            ResultSet rsProveedor = objProveedor.getProveedor(idProveedor);
            String ruc = "";
            String razon_social = "";
            String direccion = "";
            String telefono = "";
            String contacto = "";
            try{
                if(rsProveedor.next()){
                        ruc = (rsProveedor.getString("ruc")!=null) ? rsProveedor.getString("ruc") : "";
                        razon_social = (rsProveedor.getString("razon_social")!=null) ? rsProveedor.getString("razon_social") : "";
                        direccion = (rsProveedor.getString("direccion")!=null) ? rsProveedor.getString("direccion") : "";
                        telefono = (rsProveedor.getString("telefono")!=null) ? rsProveedor.getString("telefono") : "";
                        contacto = (rsProveedor.getString("contacto")!=null) ? rsProveedor.getString("contacto") : "";
                        rsProveedor.close();
                    }
            }catch(Exception e){
                e.printStackTrace();
            }


            out.println("obj»d_21^frm»"+ 
                    //"<input type='hidden' id='cxp' value='"+cxp+"' />" +
                    //"<input type='hidden' id='caj' value='"+caja+"' />" +
                    //"<input type='hidden' id='banc' value='"+bancos+"' />" +
                    //"<input type='hidden' id='chEnC' value='"+chequeEmitidoNoCobrado+"' />" +
                    "<input type='hidden' id='coP' value='"+idProveedor+"' />"+

                    "<table width='575'>" +
                    "<tr><td colspan='4' class='H3'>PAGOS</td></tr>" +
                    "<tr><td width='80'>Raz&oacute;n Social: </td><td width='300' id='ras'>"+razon_social+"</td>" +
                    "<td width='50'>RUC: </td><td width='120' id='ruc'>"+ruc+"</td></tr>" +
                    "<tr><td>Contacto: </td><td>"+contacto+"</td>" +
                    "<td>Tel&eacute;fono: </td><td>"+telefono+"</td></tr>" +
                    "<tr><td>Direcci&oacute;n: </td><td colspan='3'>"+direccion+"</td></tr>" +
                    "</table>" +

                    "<table cellpadding='0' cellspacing='0' width='572'>" +
                    "<TH class='jm_TH' width='20'>&nbsp;</TH>" +
                    "<TH class='jm_TH' width='110'># DOC.</TH>" +
                    "<TH class='jm_TH' width='80'>F. COMPRA</TH>" +
                    "<TH class='jm_TH' width='90'>CxP</TH>" +
                    "<TH class='jm_TH' width='90'>DEUDA</TH>" +
                    "<TH class='jm_TH' width='90'>ABONO</TH>" +
                    "<TH class='jm_TH' width='90'>SALDO</TH></tr>"
                    + "</table>" +

                    "<DIV style='overflow:auto;width:585px;height:"+(altoBody-355)+"px;'>" +
                    "<TABLE class='jm_tabla' cellspacing='1' cellpadding='0'>");

            float total = 0;
            float deuda = 0;
            int i=0;

            if(id_sucursal_matriz.compareTo(String.valueOf(id_sucursal))==0){
                try{
                    while(rsFactura.next()){
                        deuda = (rsFactura.getString("deuda")!=null) ? rsFactura.getFloat("deuda") : 0;
                        out.println("<tr class='jm_filaPar'>");
                        out.println("<td width='21'>F</td>");
                        out.println("<td width='110'><input type='hidden' id='doc"+i+"' value='f' />"
                                + "<input type='hidden' id='idF"+i+"' value='" + ((rsFactura.getString("id_factura_compra")!=null) ? rsFactura.getString("id_factura_compra") : "") + "' />" +
                                ((rsFactura.getString("serie_factura")!=null) ? rsFactura.getString("serie_factura") : "") + "-" +
                                ((rsFactura.getString("num_factura")!=null) ? rsFactura.getString("num_factura") : "") + "</td>");
                        out.println("<td width='81'>" + ((rsFactura.getString("fecha_compra")!=null) ? Fecha.ISOaSQL(rsFactura.getString("fecha_compra")) : "") + "</td>");
                        out.println("<td width='91' align='right'>" + ((rsFactura.getString("cxp")!=null) ? rsFactura.getString("cxp") : "") + "</td>");
                        out.println("<td width='91' align='right'><input id='de"+i+"' type='text' class='inpTbl' size='9' readonly value='"+deuda+"' /></td>");
                        out.println("<td width='91' align='right'><input id='ab"+i+"' type='text' class='inpTbl' size='9' onkeypress=\"_evaluar(event, '0123456789.');\" onkeyup=\"inv_sumTotalPago();\" " +
                                "onfocus=\"this.select();\" value='"+deuda+"' /></td>");
                        out.println("<td width='91' align='right'><input id='sa"+i+"' type='text' class='inpTbl' size='9' readonly value='0' /></td>");
                        out.println("</tr>");
                        total += deuda;
                        i++;
                    }
                }catch(Exception e){
                    e.printStackTrace();
                }
            }



             /*  LIQUIDACIONES DE COMPRAS Y SERVICIOS  */
            LiquidacionCompra objLiquidacionCompra = new LiquidacionCompra(this._ip, this._puerto, this._db, usuario, clave);
            ResultSet rsLiqCompra = objLiquidacionCompra.getLiquidacionesPendientes(idProveedor);
            try{
                while(rsLiqCompra.next()){
                    deuda = (rsLiqCompra.getString("deuda")!=null) ? rsLiqCompra.getFloat("deuda") : 0;
                    out.println("<tr class='jm_filaPar'>");
                    out.println("<td width='21'>L</td>");
                    out.println("<td width='110'><input type='hidden' id='doc"+i+"' value='l' />"
                            + "<input type='hidden' id='idF"+i+"' value='" + ((rsLiqCompra.getString("id_liquidacion_compra")!=null) ? rsLiqCompra.getString("id_liquidacion_compra") : "") + "' />" +
                            ((rsLiqCompra.getString("serie_liquidacion")!=null) ? rsLiqCompra.getString("serie_liquidacion") : "") + "-" +
                            ((rsLiqCompra.getString("num_liquidacion")!=null) ? rsLiqCompra.getString("num_liquidacion") : "") + "</td>");
                    out.println("<td width='81'>" + ((rsLiqCompra.getString("fecha_emision")!=null) ? Fecha.ISOaSQL(rsLiqCompra.getString("fecha_emision")) : "") + "</td>");
                    out.println("<td width='91' align='right'>" + ((rsLiqCompra.getString("cxp")!=null) ? rsLiqCompra.getString("cxp") : "") + "</td>");
                    out.println("<td width='91' align='right'><input id='de"+i+"' type='text' class='inpTbl' size='9' readonly value='"+deuda+"' /></td>");
                    out.println("<td width='91' align='right'><input id='ab"+i+"' type='text' class='inpTbl' size='9' onkeypress=\"_evaluar(event, '0123456789.');\" onkeyup=\"inv_sumTotalPago();\" " +
                            "onfocus=\"this.select();\" value='"+deuda+"' /></td>");
                    out.println("<td width='91' align='right'><input id='sa"+i+"' type='text' class='inpTbl' size='9' readonly value='0' /></td>");
                    out.println("</tr>");
                    total += deuda;
                    i++;
                }
            }catch(Exception e){
                e.printStackTrace();
            }finally{
                objLiquidacionCompra.cerrar();
            }


            /*  NOTAS DE VENTA  */
            NotaVenta objNotaVenta = new NotaVenta(this._ip, this._puerto, this._db, usuario, clave);
            ResultSet rsNotasVenta = objNotaVenta.getNotaVentasPendientes(idProveedor);
            try{
                while(rsNotasVenta.next()){
                    deuda = (rsNotasVenta.getString("deuda")!=null) ? rsNotasVenta.getFloat("deuda") : 0;
                    out.println("<tr class='jm_filaPar'>");
                    out.println("<td width='21'>NV</td>");
                    out.println("<td width='110'><input type='hidden' id='doc"+i+"' value='v' />"
                            + "<input type='hidden' id='idF"+i+"' value='" + ((rsNotasVenta.getString("id_nota_venta_compra")!=null) ? rsNotasVenta.getString("id_nota_venta_compra") : "") + "' />" +
                            ((rsNotasVenta.getString("serie_nota")!=null) ? rsNotasVenta.getString("serie_nota") : "") + "-" +
                            ((rsNotasVenta.getString("num_nota_venta")!=null) ? rsNotasVenta.getString("num_nota_venta") : "") + "</td>");
                    out.println("<td width='81'>" + ((rsNotasVenta.getString("fecha_compra")!=null) ? Fecha.ISOaSQL(rsNotasVenta.getString("fecha_compra")) : "") + "</td>");
                    out.println("<td width='91' align='right'>" + ((rsNotasVenta.getString("cxp")!=null) ? rsNotasVenta.getString("cxp") : "") + "</td>");
                    out.println("<td width='91' align='right'><input id='de"+i+"' type='text' class='inpTbl' size='9' readonly value='"+deuda+"' /></td>");
                    out.println("<td width='91' align='right'><input id='ab"+i+"' type='text' class='inpTbl' size='9' onkeypress=\"_evaluar(event, '0123456789.');\" onkeyup=\"inv_sumTotalPago();\" " +
                            "onfocus=\"this.select();\" value='"+deuda+"' /></td>");
                    out.println("<td width='91' align='right'><input id='sa"+i+"' type='text' class='inpTbl' size='9' readonly value='0' /></td>");
                    out.println("</tr>");
                    total += deuda;
                    i++;
                }
            }catch(Exception e){
                e.printStackTrace();
            }finally{
                objNotaVenta.cerrar();
            }


            /*  NOTAS DE DEBITO EN COMPRAS  */
            NotaDebitoCompra objNotaDebito = new NotaDebitoCompra(this._ip, this._puerto, this._db, usuario, clave);
            ResultSet rsNotaDebito = objNotaDebito.getNotasPendientes(idProveedor);
            try{
                while(rsNotaDebito.next()){
                    deuda = (rsNotaDebito.getString("deuda")!=null) ? rsNotaDebito.getFloat("deuda") : 0;
                    out.println("<tr class='jm_filaPar'>");
                    out.println("<td width='21'>ND</td>");
                    out.println("<td width='110'><input type='hidden' id='doc"+i+"' value='d' />"
                            + "<input type='hidden' id='idF"+i+"' value='" + ((rsNotaDebito.getString("id_nota_debito_compra")!=null) ? rsNotaDebito.getString("id_nota_debito_compra") : "") + "' />" +
                            ((rsNotaDebito.getString("serie_nota")!=null) ? rsNotaDebito.getString("serie_nota") : "") + "-" +
                            ((rsNotaDebito.getString("num_nota")!=null) ? rsNotaDebito.getString("num_nota") : "") + "</td>");
                    out.println("<td width='81'>" + ((rsNotaDebito.getString("fecha_emision")!=null) ? Fecha.ISOaSQL(rsNotaDebito.getString("fecha_emision")) : "") + "</td>");
                    out.println("<td width='91' align='right'>" + ((rsNotaDebito.getString("cxp")!=null) ? rsNotaDebito.getString("cxp") : "") + "</td>");
                    out.println("<td width='91' align='right'><input id='de"+i+"' type='text' class='inpTbl' size='9' readonly value='"+deuda+"' /></td>");
                    out.println("<td width='91' align='right'><input id='ab"+i+"' type='text' class='inpTbl' size='9' onkeypress=\"_evaluar(event, '0123456789.');\" onkeyup=\"inv_sumTotalPago();\" " +
                            "onfocus=\"this.select();\" value='"+deuda+"' /></td>");
                    out.println("<td width='91' align='right'><input id='sa"+i+"' type='text' class='inpTbl' size='9' readonly value='0' /></td>");
                    out.println("</tr>");
                    total += deuda;
                    i++;
                }
            }catch(Exception e){
                e.printStackTrace();
            }finally{
                objNotaDebito.cerrar();
            }


            /*  NOTAS DE CREDITO EN VENTAS POR DEVILUCIONES */
            NotaCreditoVenta objNotaCredito = new NotaCreditoVenta(this._ip, this._puerto, this._db, usuario, clave);
            ResultSet rsNotaCredito = objNotaCredito.getNotasCreditoPendientes(idProveedor);
            try{
                while(rsNotaCredito.next()){
                    deuda = (rsNotaCredito.getString("deuda")!=null) ? rsNotaCredito.getFloat("deuda") : 0;
                    out.println("<tr class='jm_filaPar'>");
                    out.println("<td width='21'>NC</td>");
                    out.println("<td width='110'><input type='hidden' id='doc"+i+"' value='c' />"
                            + "<input type='hidden' id='idF"+i+"' value='" + ((rsNotaCredito.getString("id_nota_credito_venta")!=null) ? rsNotaCredito.getString("id_nota_credito_venta") : "") + "' />" +
                            ((rsNotaCredito.getString("serie_nota")!=null) ? rsNotaCredito.getString("serie_nota") : "") + "-" +
                            ((rsNotaCredito.getString("num_nota")!=null) ? rsNotaCredito.getString("num_nota") : "") + "</td>");
                    out.println("<td width='81'>" + ((rsNotaCredito.getString("fecha_emision")!=null) ? Fecha.ISOaSQL(rsNotaCredito.getString("fecha_emision")) : "") + "</td>");
                    out.println("<td width='91' align='right'>" + ((rsNotaCredito.getString("cxp")!=null) ? rsNotaCredito.getString("cxp") : "") + "</td>");
                    out.println("<td width='91' align='right'><input id='de"+i+"' type='text' class='inpTbl' size='9' readonly value='"+deuda+"' /></td>");
                    out.println("<td width='91' align='right'><input id='ab"+i+"' type='text' class='inpTbl' size='9' onkeypress=\"_evaluar(event, '0123456789.');\" onkeyup=\"inv_sumTotalPago();\" " +
                            "onfocus=\"this.select();\" value='"+deuda+"' /></td>");
                    out.println("<td width='91' align='right'><input id='sa"+i+"' type='text' class='inpTbl' size='9' readonly value='0' /></td>");
                    out.println("</tr>");
                    total += deuda;
                    i++;
                }
            }catch(Exception e){
                e.printStackTrace();
            }finally{
                objNotaCredito.cerrar();
            }


            out.println("</table></div>" +
                    "<table cellpadding='0' cellspacing='0' width='575'>" +
                    "<tr><td colspan='2' align='right'>SALDOS: </td>" +
                    "<td align='right' id='tp' width='90'>"+Addons.truncar(total)+"</td>" +
                    "<td align='right' id='ts' width='90'>0</td></tr>"
                    + "</table>" +

                    "<hr align='left' width='575'>" +

                    "<table cellpadding='0' cellspacing='0' width='575'><tr class='jm_tabla'>" +
                    /*"<td><input type='radio' id='e0' name='fp' checked /> Efectivo</td>" +
                    "<td><input type='radio' id='e1' name='fp' /> Efectivo o Cheque </td>" +
                    "<td nowrap><input type='radio' id='e2' name='fp' /> Nota de D&eacute;bito </td>" +*/
                    "<td align='right'><input type='button' value='Generar Comprobante' onclick='inv_getDocumentoPago();' /></td></tr>");

            out.println("</table>");

        } finally {
            objProveedor.cerrar();
            objFactura.cerrar();
            out.close();
        }
    }

    // <editor-fold defaultstate='collapsed' desc='HttpServlet methods. Click on the + sign on the left to edit the code.'>
    /**
    * Handles the HTTP <code>GET</code> method.
    * @param request servlet request
    * @param response servlet response
    */
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
    * Handles the HTTP <code>POST</code> method.
    * @param request servlet request
    * @param response servlet response
    */
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
    * Returns a short description of the servlet.
    */
    public String getServletInfo() {
        return "Short description";
    }
    // </editor-fold>
}
