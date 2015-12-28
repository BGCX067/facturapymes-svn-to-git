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
import java.sql.ResultSet;
import jm.web.DatosDinamicos;
import jm.web.Fecha;
import jm.adm.clas.Configuracion;
import jm.adm.clas.Sucursal;
import jm.cnt.clas.Banco;
import jm.seg.clas.Rol;
import jm.cnt.clas.PlanCuenta;
import jm.cnt.clas.ComprobanteEgreso;
import jm.inv.clas.FacturaCompra;
import jm.inv.clas.Proveedor;

/**
 *
 * @author Jorge
 */
public class frmComprobanteEgresoPago extends HttpServlet {
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
        String id_rol = (String)sesion.getAttribute("id_rol");
        int id_sucursal_sesion = (Integer)sesion.getAttribute("sucursal");
        String usuario = (String)sesion.getAttribute("usuario");
        String clave = (String)sesion.getAttribute("clave");

        response.setContentType("text/html;charset=UTF-8");
        response.setHeader("Pragma", "no-cache");
        response.setHeader("Expires", "Mon, 01 Jan 2001 00:00:01 GMT");
        response.setHeader("Cache-Control", "no-store");
        response.setHeader("Cache-Control", "must-revalidate");
        response.setHeader("Cache-Control", "no-cache");
        PrintWriter out = response.getWriter();

        String idFP = request.getParameter("id");    /* id_factura_compra_pago */
        String id_proveedor = request.getParameter("coP");
        String id = "-1";

        Configuracion conf = new Configuracion(this._ip, this._puerto, this._db, usuario, clave);
        String id_plan_cxp = conf.getValor("cxp");
        String id_sucursal_matriz = conf.getValor("id_sucursal_matriz");
        //String ciudad = conf.getValor("ciudad");
        //String conf_banco = conf.getValor("banco");
        //String conf_cuenta = conf.getValor("cuenta");
        conf.cerrar();

        Proveedor objProveedor = new Proveedor(this._ip, this._puerto, this._db, usuario, clave);
        try{
            ResultSet rsProveedor = objProveedor.getProveedor(id_proveedor);
            if(rsProveedor.next()){
                id_plan_cxp = rsProveedor.getString("id_plan_cuenta")!=null ? rsProveedor.getString("id_plan_cuenta") : id_plan_cxp;
                rsProveedor.close();
            }
        }catch(Exception e){
            e.printStackTrace();
        }finally{
            objProveedor.cerrar();
        }

        Sucursal objSucursal = new Sucursal(this._ip, this._puerto, this._db, usuario, clave);
        String caja_chica = objSucursal.getIdCajaChica(id_sucursal_sesion);
        ResultSet rsSucursales = objSucursal.getSucursales();
        objSucursal.cerrar();

        PlanCuenta objPlanCuenta = new PlanCuenta(this._ip, this._puerto, this._db, usuario, clave);
        ResultSet cuentas = objPlanCuenta.getTablasAsignacion();
        ResultSet bancos = objPlanCuenta.getTablasCajaBancosNotas(4);
        objPlanCuenta.cerrar();

        Rol objRol = new Rol(this._ip, this._puerto, this._db, usuario, clave);

        ComprobanteEgreso objComprobanteEgreso = new ComprobanteEgreso(this._ip, this._puerto, this._db, usuario, clave);

        FacturaCompra objFacturaCompra = new FacturaCompra(this._ip, this._puerto, this._db, usuario, clave);
        try {
            String fecha = Fecha.getFecha("SQL");
            long num_compEgr = objComprobanteEgreso.getNumComprobante();
            long num_cheque = objComprobanteEgreso.getNumCheque();
            String fecha_actual = fecha;
            String fecha_proceso = fecha;
            String id_sucursal = "";
            String ci_ruc = "";
            String orden_de = "";
            String tipo_pago = "e";
            String cuenta = "";
            String id_banco = "";
            String banco = "";
            String total = "0";
            String son = "";
            String detalle = "";
            String lugar = "";
            String fecha_en_efectivo = "";
            boolean anulado = false;
            String id_documento = "";
            int numPagos = 0;

            Banco objBanco = new Banco(this._ip, this._puerto, this._db, usuario, clave);
            ResultSet rsBancos = objBanco.getBancos();
            try{
                if(rsBancos.next()){
                    banco = rsBancos.getString("banco")!=null ? rsBancos.getString("banco") : "";
                }
            }catch(Exception ex){}
            String jsBancos = objBanco.getBancosJSON();
            objBanco.cerrar();


            out.println("obj»vta_nc_html^foc»nb"+(idFP.compareTo("-1")==0?"^fun»inv_setDocumentoPago();":"")+"^frm»<div>");


            if(idFP.compareTo("-1")!=0){
                
                try{
                    String id_factura_compra = "";

                    ResultSet rsPago = objFacturaCompra.getPago(idFP);
                    numPagos = objFacturaCompra.getFilas(rsPago);
                    if(rsPago.next()){
                        id = (rsPago.getString("id_comprobante_egreso")!=null) ? rsPago.getString("id_comprobante_egreso") : "-1";
                        id_documento = (rsPago.getString("id_comprobante_egreso")!=null) ? rsPago.getString("id_comprobante_egreso") : "";
                        id_factura_compra = (rsPago.getString("id_factura_compra")!=null) ? rsPago.getString("id_factura_compra") : "";
                        rsPago.close();
                    }

                    String idProveedor = "";
                    ResultSet rsFacturaCompra = objFacturaCompra.getFactura(id_factura_compra);
                    if(rsFacturaCompra.next()){
                        idProveedor = rsFacturaCompra.getString("id_proveedor")!=null ? rsFacturaCompra.getString("id_proveedor") : "";
                        rsFacturaCompra.close();
                    }

                    ResultSet compEgr = objComprobanteEgreso.getComprobanteEgreso(id);
                    if(compEgr.next()){
                        num_compEgr = (compEgr.getString("num_comprobante")!=null) ? compEgr.getInt("num_comprobante") : num_compEgr;
                        fecha_actual = (compEgr.getString("fecha_actual")!=null) ? Fecha.ISOaSQL(compEgr.getString("fecha_actual")) : fecha;
                        fecha_proceso = (compEgr.getString("fecha_proceso")!=null) ? Fecha.ISOaSQL(compEgr.getString("fecha_proceso")) : fecha;
                        fecha_en_efectivo = (compEgr.getString("fecha_en_efectivo")!=null) ? Fecha.ISOaSQL(compEgr.getString("fecha_en_efectivo")) : "";
                        id_sucursal = (compEgr.getString("id_sucursal")!=null) ? compEgr.getString("id_sucursal") : "";
                        ci_ruc = (compEgr.getString("ci_ruc")!=null) ? compEgr.getString("ci_ruc") : "";
                        orden_de = (compEgr.getString("orden_de")!=null) ? compEgr.getString("orden_de") : "";
                        tipo_pago = (compEgr.getString("tipo_pago")!=null) ? compEgr.getString("tipo_pago") : "e";
                        num_cheque = (compEgr.getString("num_cheque")!=null) ? compEgr.getInt("num_cheque") : num_cheque;
                        cuenta = (compEgr.getString("cuenta")!=null) ? compEgr.getString("cuenta") : "";
                        banco = (compEgr.getString("banco")!=null) ? compEgr.getString("banco") : "";
                        total = (compEgr.getString("total")!=null) ? compEgr.getString("total") : "0";
                        son = (compEgr.getString("son")!=null) ? compEgr.getString("son") : "";
                        detalle = (compEgr.getString("detalle")!=null) ? compEgr.getString("detalle") : "";
                        lugar = (compEgr.getString("lugar")!=null) ? compEgr.getString("lugar") : "";
                        anulado = (compEgr.getString("anulado")!=null) ? compEgr.getBoolean("anulado") : false;
                        compEgr.close();
                    }


                    try{
                        out.println("<input type='hidden' id='coP' value='"+idProveedor+"' />"
                                + "<table cellpadding='0' cellspacing='0'>" +
                            "<TH class='jm_TH' width='110'># FAC. / LIQU.</TH>" +
                            "<TH class='jm_TH' width='80'>F. COMPRA</TH>" +
                            "<TH class='jm_TH' width='90'>CxP</TH>" +
                            "<TH class='jm_TH' width='90'>DEUDA</TH>" +
                            "<TH class='jm_TH' width='90'>PAGO</TH>" +
                            "<TH class='jm_TH' width='90'>SALDO</TH></tr></table>" +

                            "<DIV style='overflow:auto;width:570px;height:85px;'>" +
                            "<TABLE class='jm_tabla' cellspacing='1' cellpadding='0'>");
                        ResultSet rsPagos = objFacturaCompra.getPagos(id_documento);
                        int i=0;
                        float deuda=0;
                        float abono=0;
                        double deuda_ant = 0;
                        while(rsPagos.next()){
                            deuda = (rsPagos.getString("deuda")!=null) ? rsPagos.getFloat("deuda") : 0;
                            abono = (rsPagos.getString("abono")!=null) ? rsPagos.getFloat("abono") : 0;
                            deuda_ant =  Math.round((deuda+abono) * Math.pow(10, 2)) / Math.pow(10, 2);
                            out.println("<tr class='jm_filaPar'>");
                            out.println("<td width='110'><input type='hidden' id='idF"+i+"' value='" + ((rsPagos.getString("id_factura_compra")!=null) ? rsPagos.getString("id_factura_compra") : "") + "' />" +
                                    "<input type='hidden' id='idFP"+i+"' value='" + ((rsPagos.getString("id_factura_compra_pago")!=null) ? rsPagos.getString("id_factura_compra_pago") : "") + "' />" +
                                    ((rsPagos.getString("numero_factura")!=null) ? rsPagos.getString("numero_factura") : "") + "</td>");
                            out.println("<td width='81'>" + ((rsPagos.getString("fecha_compra")!=null) ? Fecha.ISOaSQL(rsPagos.getString("fecha_compra")) : "") + "</td>");
                            out.println("<td width='91' align='right'>" + ((rsPagos.getString("cxp")!=null) ? rsPagos.getString("cxp") : "") + "</td>");
                            out.println("<td width='91' align='right'><input id='de"+i+"' type='text' class='inpTbl' size='9' readonly value='"+deuda_ant+"' /></td>");
                            out.println("<td width='91' align='right'><input id='ab"+i+"' type='text' class='inpTbl' size='9' onkeypress=\"_evaluar(event, '0123456789.');\" onkeyup=\"inv_sumTotalPagoEd();\" " +
                                    "onfocus=\"this.select();\" value='"+abono+"' /></td>");
                            out.println("<td width='91' align='right'><input id='sa"+i+"' type='text' class='inpTbl' size='9' readonly value='"+deuda+"' /></td>");
                            out.println("</tr>");
                            //total += deuda;
                            i++;
                        }
                        out.println("</table></div><hr />");
                    }catch(Exception e){
                        out.println("</div><hr />");
                        e.printStackTrace();
                    }
                }catch(Exception e){
                    e.printStackTrace();
                }
            }



            String cobro = (tipo_pago.compareTo("c")==0) ? "block" : "none";

            out.println("<input type='hidden' id='fe' value='"+fecha_en_efectivo+"' />"+
                    "<input type='hidden' id='cxp' value='"+id_plan_cxp+"' />" +
                    "<input type='hidden' id='caj' value='"+caja_chica+"' />" +
                    "<div id=\"axBc\" style='display:none'>"+jsBancos+"</div>" +
                    "<table width='750'>" +
                    "<tr><td align='center'><strong>C O M P R O B A N T E &nbsp;&nbsp; D E &nbsp;&nbsp; E G R E S O</strong></td>" +
                    "<td align='right' width='100'>Nro. "+num_compEgr+"</td></tr>" +
                    "</table>" +

                    "<table width='100%'>" +

                        "<tr><td>Nombre: <span class='marca'>*</span> &nbsp;&nbsp; <input id='nb' value='"+orden_de+"' type='text' size='60' maxlength='80' /></td>" +
                        "<td>CI/RUC: <span class='marca'>*</span> <input id='ru' value='"+ci_ruc+"' type='text' size='20' maxlength='13' onkeypress=\"_numero(event);\" /></td></tr>" +

                        "<tr><td colspan='2'>Concepto: <span class='marca'>*</span> <input id='dt' value='"+detalle+"' type='text' size='100' maxlength='200' /></td></tr>");

                        out.println("<tr><td>");
                        if(id.compareTo("-1")==0){
                            out.println("Forma de Pago: <label><input type='radio' id='e0' name='fp' "+((tipo_pago.compareTo("e")==0)?"checked":"")+" onclick=\"inv_setDetalle()\" /> Efectivo </label> &nbsp;&nbsp;&nbsp;&nbsp;&nbsp; ");
                            out.println("<label style=\"visibility:"+(id_sucursal_matriz.compareTo(String.valueOf(id_sucursal_sesion))==0 ? "visible" : "hidden")+"\"><input type='radio' id='e1' name='fp' "+((tipo_pago.compareTo("c")==0)?"checked":"")+" onclick=\"inv_setDetalle()\" /> Cheque </label> &nbsp;&nbsp;&nbsp;&nbsp;&nbsp; ");
                            /*out.println("<input type='radio' id='e2' name='fp' "+((tipo_pago.compareTo("n")==0)?"checked":"")+" onclick=\"cnt_setCuenta();inv_setDetalle()\" /> Nota de C&eacute;dito : &nbsp;" +
                            DatosDinamicos.combo(rsNotas, "idNC", String.valueOf(num_cheque), "") );*/
                        }else{
                            out.println("&nbsp;");
                        }
                        out.println("</td><td>");
                        if(id_sucursal_matriz.compareTo(String.valueOf(id_sucursal_sesion))==0){
                            out.println("Sucursal: " + DatosDinamicos.combo(rsSucursales, "id_sucursal", id_sucursal, "", "", 140));
                        }else{
                            out.println("&nbsp;<input type='hidden' id='id_sucursal' name='id_sucursal' value='"+id_sucursal_sesion+"' />");
                        }
                        out.println("</td></tr>");

                    out.println("</table>" +


                    /*  cuando la forma de pago es cheque  */
                    "<table width='100%' id='pCh' style=\"display:"+cobro+";\" >" +
                    "<tr><td width='300'>Banco: &nbsp;&nbsp;&nbsp; "+DatosDinamicos.combo(rsBancos, "bc1", id_banco, "cnt_setBanco()", 230)+
                    "<input type='hidden' id='bc' name='bc' value=\""+banco+"\" /></td>" +
                    "<td width='200'>Cheque #: <span class='marca'>*</span> <input id='ch' value='"+num_cheque+"' type='text' size='15' maxlength='20' onkeypress=\"_numero(event);\"/></td>" +
                    "<td>Cuenta: &nbsp;&nbsp;<input id='ct' value='"+cuenta+"' type='text' size='20' maxlength='30' onkeypress=\"_evaluar(event, '0123456789-');\" /></td><tr>" +

                    "<tr><td>Lugar: <span class='marca'>*</span> &nbsp;&nbsp;&nbsp;<input id='lu' value='"+lugar+"' type='text' size='20' maxlength='30' /></td>" +
                    "<td colspan='2'>Fecha: &nbsp;&nbsp;&nbsp;<input id='fp' type='text'size='10' value='"+fecha_proceso+"' readOnly />" +
                    "<input type='button' value='...' onClick=\"SelectorFecha.crear('fp', 'SQL');\" /></td></tr></table>" +

                    /*"<div><a href='javascript:void(0);' style='float:left;' onclick=\"con_addCuenta();con_setSon('th');\">Insertar Cuenta</a> " +*/
                    "<div>" +
                    "<span style='float:right;' class='marca' id='di'></span></div>" +
                    "&nbsp;&nbsp;<div style=\"display:none\" id='ax'>"+ DatosDinamicos.combo(cuentas, "_Xx_", "", "", 535)+"</div>" +
                    "<table cellpadding='0' cellspacing='0'>" +
                    "<TH class='jm_TH' width='550'>CODIGO - CUENTA</TH>" +
                    "<TH class='jm_TH' width='90'>DEBE</TH>" +
                    "<TH class='jm_TH' width='90'>HABER</TH>" +
                    "<TH class='jm_TH' width='20'>&nbsp;&nbsp;</TH></tr></table>" +

                    "<DIV style='overflow:auto;width:775px;height:100px;'>" +
                    "<TABLE class='jm_tabla' cellspacing='1' cellpadding='0' id='tblD'>");

            try{
                String id_plan_cuenta = "";
                String debe = "";
                String haber = "";
                int i=0;
                ResultSet compEgrDetalle = objComprobanteEgreso.getComprobanteEgresoDetalle(id);
                while(compEgrDetalle.next()){
                    id_plan_cuenta = (compEgrDetalle.getString("id_plan_cuenta")!=null) ? compEgrDetalle.getString("id_plan_cuenta") : "1";
                    debe = (compEgrDetalle.getString("debe")!=null) ? ((compEgrDetalle.getDouble("debe")>0)?compEgrDetalle.getString("debe"):"") : "";
                    haber = (compEgrDetalle.getString("haber")!=null) ? ((compEgrDetalle.getDouble("haber")>0)?compEgrDetalle.getString("haber"):"") : "";
                    out.println("<tr id='r"+i+"' class='jm_filaPar'>");
                    out.println("<td style='width:550px'>" + DatosDinamicos.combo(cuentas, "PC"+i, id_plan_cuenta, "", 535, "disabled='disabled'") + "</td>");
                    out.println("<td style='width:90px'><input id='d"+i+"' type='text' class='inpTbl' size='9' onkeypress=\"_evaluar(event, '0123456789.');\" onkeyup=\"con_sumTotales();con_setSon('th');\" " +
                            "onfocus=\"this.select();\" value='"+debe+"' /></td>");
                    out.println("<td style='width:90px'><input id='h"+i+"' type='text' class='inpTbl' size='9' onkeypress=\"_evaluar(event, '0123456789.');\" onkeyup=\"con_sumTotales();con_setSon('th');\" " +
                            "onfocus=\"this.select();\" value='"+haber+"' /></td>");

                    out.println("<td style='width:20px'>&nbsp;</td>");
                    //out.println("<div class='columna der' style='width:20px'><div class='jm_elim' alt='Eliminar' title='Eliminar' onclick=\"_R('r"+i+"');con_sumTotales();con_setSon('th');\">&nbsp;</div></div>");

                    out.println("</tr>");
                    i++;
                }

                compEgrDetalle.close();
                cuentas.close();
                bancos.close();
            }catch(Exception e){
                e.printStackTrace();
            }

            out.println("</table></div>" +
                    "<div style='display:none' id='tp'>"+total+"</div>" +
                    "<table cellpadding='0' cellspacing='0'>" +
                    "<tr><td align='right' width='550' class='marca'>Son: <input id='sn' Style='width:450px;' value='"+son+"' type='text' maxlength='200' /> &nbsp;" +
                    "TOTALES: &nbsp;</td>" +
                    "<td id='td' align='right' width='90' class='marca'> "+total+" </td>" +
                    "<td id='th' align='right' width='90' class='marca'> "+total+" </td>" +
                    "<td width='20'>&nbsp;&nbsp;</td></tr></table></div>");

            out.println("<hr /><table class='jm_tabla' width='100%' cellspacing='0' cellpadding='0'><tr>");
            if(!anulado){
                //if(fecha_en_efectivo.compareTo("")==0){
                    //long fecha_sistema = Fecha.getTimeStamp(Fecha.getFecha("SQL"));
                    //long fecha_asiento = Fecha.getTimeStamp(fecha_actual);
                    if(id.compareTo("-1")!=0 && objRol.getAcceso(id_rol, "pagoAnular") && numPagos==1){
                        out.println("<td><input type='button' value='Anular Comprobante' onclick=\"if(confirm('¿Está seguro de anular el pago?')){cnt_anularDocumento('tbl_factura_compra_pago', '"+idFP+"&idC="+id+"&p=c');}\" /></td>");
                    }
                    if(id.compareTo("-1")!=0 && tipo_pago.compareTo("c")==0){
                        out.println("<td><input type='button' style='float:right;' value='Imprimir Cheque' onclick=\"imprimir('pdfCheque?id="+id+"');\" /></td>");
                    }
                    /*if(id.compareTo("-1")==0 || ((id.compareTo("-1")!=0 && objRol.getAcceso(id_rol, "pagoEditar")) &&
                        ((fecha_en_efectivo.compareTo("")==0 && tipo_pago.compareTo("c")==0) || (fecha_en_efectivo.compareTo("")!=0 && tipo_pago.compareTo("e")==0)))){*/
                    if(id.compareTo("-1")==0){
                        out.println("<td><input type='button' style='float:left;' value='Guardar' onclick='inv_pagoGuardar(0, "+id+")' /></td>");
                        out.println("<td align='right'> <input type='button' value='Guardar e Imprimir' onclick='inv_pagoGuardar(1, "+id+")' /></td>");
                    }
                //}
            }else{
                out.println("<td><span class='marca'>COMPROBANTE DE EGRESO ANULADO</span></td>");
            }

            out.println("</tr></table>");

        } finally {
            objComprobanteEgreso.cerrar();
            objFacturaCompra.cerrar();
            objRol.cerrar();
            out.close();
        }
    }


    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
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
