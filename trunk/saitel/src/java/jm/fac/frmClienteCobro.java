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

package jm.fac;

import java.io.*;

import javax.servlet.*;
import javax.servlet.http.*;
import jm.adm.clas.Configuracion;
import jm.fac.clas.Cliente;
import jm.fac.clas.FacturaVenta;
import java.sql.ResultSet;
import jm.fac.clas.NotaDebitoVenta;
import jm.inv.clas.NotaCreditoCompra;
import jm.web.Addons;
import jm.web.Fecha;

/**
 *
 * @author Jorge
 */
public class frmClienteCobro extends HttpServlet {
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
        
        response.setContentType("text/html;charset=UTF-8");
        response.setHeader("Pragma", "no-cache");
        response.setHeader("Expires", "Mon, 01 Jan 2001 00:00:01 GMT");
        response.setHeader("Cache-Control", "no-store");
        response.setHeader("Cache-Control", "must-revalidate");
        response.setHeader("Cache-Control", "no-cache");
        PrintWriter out = response.getWriter();

        String idCliente = request.getParameter("id");
        int altoBody = Integer.parseInt(request.getParameter("aB"))-355;

        Configuracion conf = new Configuracion(this._ip, this._puerto, this._db, usuario, clave);
        //String cxc = conf.getValor("cxc");
        //String caja = conf.getValor("caja");
        String bancos = conf.getValor("bancos");
        String chequeRecibidoNoCobrado = conf.getValor("chequeRecibidoNoCobrado");
        conf.cerrar();

        Cliente objCliente = new Cliente(this._ip, this._puerto, this._db, usuario, clave);

        FacturaVenta objFactura = new FacturaVenta(this._ip, this._puerto, this._db, usuario, clave);
        ResultSet rsFactura = objFactura.getFacturasPendientes(idCliente);
        try {
            ResultSet rsCliente = objCliente.getCliente(idCliente);
            String ruc = "";
            String razon_social = "";
            String direccion = "";
            String telefono = "";
            String contacto = "";
            String cxc = "";
            String num_cuenta = "";
            try{
                if(rsCliente.next()){
                        ruc = (rsCliente.getString("ruc")!=null) ? rsCliente.getString("ruc") : "";
                        razon_social = (rsCliente.getString("razon_social")!=null) ? rsCliente.getString("razon_social") : "";
                        direccion = (rsCliente.getString("direccion")!=null) ? rsCliente.getString("direccion") : "";
                        telefono = (rsCliente.getString("telefono")!=null) ? rsCliente.getString("telefono") : "";
                        contacto = (rsCliente.getString("contacto")!=null) ? rsCliente.getString("contacto") : "";
                        cxc = (rsCliente.getString("id_plan_cuenta")!=null) ? rsCliente.getString("id_plan_cuenta") : "";
                        num_cuenta = (rsCliente.getString("num_cuenta")!=null) ? rsCliente.getString("num_cuenta") : "";
                        rsCliente.close();
                    }
                    objCliente.cerrar();
            }catch(Exception e){
                e.printStackTrace();
            }


            out.println("obj»d_21^frm»"+ 
                    "<input type='hidden' id='banc' value='"+bancos+"' />" +
                    "<input type='hidden' id='chRnC' value='"+chequeRecibidoNoCobrado+"' />"+
                    "<input type='hidden' id='cxc' value='"+cxc+"' />" +
                    "<input type='hidden' id='coC' value='"+idCliente+"' />"+
                    "<input type='hidden' id='num_cuenta' value='"+num_cuenta+"' />"+

                    "<table width='575'>" +
                    "<tr><td colspan='4' class='H3'>COBROS</td></tr>" +
                    "<tr><td width='80'>Raz&oacute;n Social: </td><td width='300' id='ras'>"+razon_social+"</td>" +
                    "<td width='50'>RUC: </td><td width='120' id='ruc'>"+ruc+"</td></tr>" +
                    "<tr><td>Contacto: </td><td>"+contacto+"</td>" +
                    "<td>Tel&eacute;fono: </td><td>"+telefono+"</td></tr>" +
                    "<tr><td>Direcci&oacute;n: </td><td colspan='3'>"+direccion+"</td></tr>" +
                    "</table>" +

                    "<table cellpadding='0' cellspacing='0' width='582'>" +
                    "<TH class='jm_TH' width='20'>&nbsp;</TH>" +
                    "<TH class='jm_TH' width='110'># FACTURA</TH>" +
                    "<TH class='jm_TH' width='80'>EMISION</TH>" +
                    "<TH class='jm_TH' width='90'>CxC</TH>" +
                    "<TH class='jm_TH' width='90'>DEUDA</TH>" +
                    "<TH class='jm_TH' width='90'>ABONO</TH>" +
                    "<TH class='jm_TH' width='90'>SALDO</TH></tr>"
                    + "</table>" +

                    "<DIV style='overflow:auto;width:600px;height:"+(altoBody)+"px;'>" +
                    "<TABLE class='jm_tabla' cellspacing='1' cellpadding='0'>");

            float total = 0;
            float deuda = 0;
            int i=0;
            try{
                while(rsFactura.next()){
                    deuda = (rsFactura.getString("deuda")!=null) ? rsFactura.getFloat("deuda") : 0;
                    out.println("<tr class='jm_filaPar'>");
                    out.println("<td width='20'>F<input type='hidden' id='doc"+i+"' value='F' />"
                            + "<input type='hidden' id='idF"+i+"' value='" + ((rsFactura.getString("id_factura_venta")!=null) ? rsFactura.getString("id_factura_venta") : "") + "' /></td>");
                    out.println("<td width='111' id='axSNFact"+i+"'>"
                            + ((rsFactura.getString("serie_factura")!=null) ? rsFactura.getString("serie_factura") : "") + "-" +
                            ((rsFactura.getString("num_factura")!=null) ? rsFactura.getString("num_factura") : "") + "</td>");
                    out.println("<td width='81'>" + ((rsFactura.getString("fecha_emision")!=null) ? Fecha.ISOaSQL(rsFactura.getString("fecha_emision")) : "") + "</td>");
                    out.println("<td width='91' align='right'>" + ((rsFactura.getString("cxc")!=null) ? rsFactura.getString("cxc") : "") + "</td>");
                    out.println("<td width='91' align='right'><input id='de"+i+"' type='text' class='inpTbl' size='8' readonly value='"+deuda+"' /></td>");
                    out.println("<td width='91' align='right'><input id='ab"+i+"' type='text' class='inpTbl' size='8' onkeypress=\"_evaluar(event, '0123456789.');\" onkeyup=\"inv_sumTotalPago();\" " +
                            "onfocus=\"this.select();\" value='"+deuda+"' /></td>");
                    out.println("<td width='91' align='right'><input type='hidden' id='idF"+i+"' value='" + ((rsFactura.getString("id_factura_venta")!=null) ? rsFactura.getString("id_factura_venta") : "") + "' />"
                            + "<input id='sa"+i+"' type='text' class='inpTbl' size='8' readonly value='0' /></td>");
                    out.println("</tr>");
                    total += deuda;
                    i++;
                }
            }catch(Exception e){
                e.printStackTrace();
            }



            /*  NOTAS DE DEBITO EN VENTA   */
            NotaDebitoVenta objNotaDebitoVenta = new NotaDebitoVenta(this._ip, this._puerto, this._db, usuario, clave);
            ResultSet rsNotDebito = objNotaDebitoVenta.getNotasPendientes(idCliente);
            try{
                while(rsNotDebito.next()){
                    deuda = (rsNotDebito.getString("deuda")!=null) ? rsNotDebito.getFloat("deuda") : 0;
                    out.println("<tr class='jm_filaPar'>");
                    out.println("<td width='20'>ND<input type='hidden' id='doc"+i+"' value='d' />"
                            + "<input type='hidden' id='idF"+i+"' value='" + ((rsNotDebito.getString("id_nota_debito_venta")!=null) ? rsNotDebito.getString("id_nota_debito_venta") : "") + "' /></td>");
                    out.println("<td width='111'>" +
                            ((rsNotDebito.getString("serie_nota")!=null) ? rsNotDebito.getString("serie_nota") : "") + "-" +
                            ((rsNotDebito.getString("num_nota")!=null) ? rsNotDebito.getString("num_nota") : "") + "</td>");
                    out.println("<td width='81'>" + ((rsNotDebito.getString("fecha_emision")!=null) ? Fecha.ISOaSQL(rsNotDebito.getString("fecha_emision")) : "") + "</td>");
                    out.println("<td width='91' align='right'>" + ((rsNotDebito.getString("cxc")!=null) ? rsNotDebito.getString("cxc") : "") + "</td>");
                    out.println("<td width='91' align='right'><input id='de"+i+"' type='text' class='inpTbl' size='8' readonly value='"+deuda+"' /></td>");
                    out.println("<td width='91' align='right'><input id='ab"+i+"' type='text' class='inpTbl' size='8' onkeypress=\"_evaluar(event, '0123456789.');\" onkeyup=\"inv_sumTotalPago();\" " +
                            "onfocus=\"this.select();\" value='"+deuda+"' /></td>");
                    out.println("<td width='91' align='right'><input id='sa"+i+"' type='text' class='inpTbl' size='8' readonly value='0' /></td>");
                    out.println("</tr>");
                    total += deuda;
                    i++;
                }
            }catch(Exception e){
                e.printStackTrace();
            }finally{
                objNotaDebitoVenta.cerrar();
            }
            

            /*  NOTAS DE CREDITO EN COMPRAS   */
            NotaCreditoCompra objCreditoCompra = new NotaCreditoCompra(this._ip, this._puerto, this._db, usuario, clave);
            ResultSet rsCreditoCompra = objCreditoCompra.getNotasCreditoPendientes(idCliente);
            try{
                while(rsCreditoCompra.next()){
                    deuda = (rsCreditoCompra.getString("deuda")!=null) ? rsCreditoCompra.getFloat("deuda") : 0;
                    out.println("<tr class='jm_filaPar'>");
                    out.println("<td width='20'>NC<input type='hidden' id='doc"+i+"' value='c' />"
                            + "<input type='hidden' id='idF"+i+"' value='" + ((rsCreditoCompra.getString("id_nota_credito_compra")!=null) ? rsCreditoCompra.getString("id_nota_credito_compra") : "") + "' /></td>");
                    out.println("<td width='111'>" +
                            ((rsCreditoCompra.getString("serie_nota")!=null) ? rsCreditoCompra.getString("serie_nota") : "") + "-" +
                            ((rsCreditoCompra.getString("num_nota")!=null) ? rsCreditoCompra.getString("num_nota") : "") + "</td>");
                    out.println("<td width='81'>" + ((rsCreditoCompra.getString("fecha_emision")!=null) ? Fecha.ISOaSQL(rsCreditoCompra.getString("fecha_emision")) : "") + "</td>");
                    out.println("<td width='91' align='right'>" + ((rsCreditoCompra.getString("cxc")!=null) ? rsCreditoCompra.getString("cxc") : "") + "</td>");
                    out.println("<td width='91' align='right'><input id='de"+i+"' type='text' class='inpTbl' size='8' readonly value='"+deuda+"' /></td>");
                    out.println("<td width='91' align='right'><input id='ab"+i+"' type='text' class='inpTbl' size='8' onkeypress=\"_evaluar(event, '0123456789.');\" onkeyup=\"inv_sumTotalPago();\" " +
                            "onfocus=\"this.select();\" value='"+deuda+"' /></td>");
                    out.println("<td width='91' align='right'><input id='sa"+i+"' type='text' class='inpTbl' size='8' readonly value='0' /></td>");
                    out.println("</tr>");
                    total += deuda;
                    i++;
                }
            }catch(Exception e){
                e.printStackTrace();
            }finally{
                objCreditoCompra.cerrar();
            }





            out.println("</table></div>" +
                    "<table cellpadding='0' cellspacing='0' width='575'>" +
                    "<tr><td align='right'>SALDOS: </td>" +
                    "<td align='right' id='tp' width='90'>"+Addons.truncar(total)+"</td>" +
                    "<td align='right' id='ts' width='90'>0</td></tr>"
                    + "</table>" +

                    "<hr />" +

                    "<table cellpadding='0' cellspacing='0' width='575'><tr class='jm_tabla'>" +
                    /*"<td><input type='radio' id='e0' name='fp' checked /> Efectivo</td>" +
                    "<td><input type='radio' id='e1' name='fp' /> Efectivo o Cheque </td>" +
                    "<td nowrap><input type='radio' id='e2' name='fp' /> Nota de D&eacute;bito </td>" +*/
                    "<td align='right'><input type='button' value='Generar Comprobante de Ingreso' onclick='fac_getDocumentoCobro();' /></td></tr>");

            out.println("</table>");

        } finally {
            objCliente.cerrar();
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
