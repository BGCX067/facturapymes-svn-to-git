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

package jm.cnt;

import java.io.*;

import javax.servlet.*;
import javax.servlet.http.*;
import java.sql.ResultSet;
import jm.web.DatosDinamicos;
import jm.web.Fecha;
import jm.adm.clas.Sucursal;
import jm.cnt.clas.Banco;
import jm.seg.clas.Rol;
import jm.cnt.clas.PlanCuenta;
import jm.cnt.clas.ComprobanteEgreso;

/**
 *
 * @author Jorge
 */
public class frmComprobanteEgreso extends HttpServlet {
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
        int id_sucursal = (Integer)sesion.getAttribute("sucursal");
        String usuario = (String)sesion.getAttribute("usuario");
        String clave = (String)sesion.getAttribute("clave");

        response.setContentType("text/html;charset=UTF-8");
        response.setHeader("Pragma", "no-cache");
        response.setHeader("Expires", "Mon, 01 Jan 2001 00:00:01 GMT");
        response.setHeader("Cache-Control", "no-store");
        response.setHeader("Cache-Control", "must-revalidate");
        response.setHeader("Cache-Control", "no-cache");
        PrintWriter out = response.getWriter();

        String id = request.getParameter("id");

        Sucursal objSucursal = new Sucursal(this._ip, this._puerto, this._db, usuario, clave);
        String caja_chica = objSucursal.getIdCajaChica(id_sucursal);
        objSucursal.cerrar();

        PlanCuenta objPlanCuenta = new PlanCuenta(this._ip, this._puerto, this._db, usuario, clave);
        ResultSet cuentas = objPlanCuenta.getTablasAsignacion();
        ResultSet bancos = objPlanCuenta.getTablasCajaBancos();
        objPlanCuenta.cerrar();

        Rol objRol = new Rol(this._ip, this._puerto, this._db, usuario, clave);

        ComprobanteEgreso objComprobanteEgreso = new ComprobanteEgreso(this._ip, this._puerto, this._db, usuario, clave);
        try {
            String fecha = Fecha.getFecha("SQL");
            String num_compEgr = "";
            long num_cheque = objComprobanteEgreso.getNumCheque();
            String fecha_actual = fecha;
            String fecha_proceso = fecha;
            String fecha_en_efectivo = "";
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
            boolean bloqueo = false;
            boolean anulado = false;

            Banco objBanco = new Banco(this._ip, this._puerto, this._db, usuario, clave);
            ResultSet rsBancos = objBanco.getBancos();
            try{
                if(rsBancos.next()){
                    banco = rsBancos.getString("banco")!=null ? rsBancos.getString("banco") : "";
                }
            }catch(Exception ex){}
            String jsBancos = objBanco.getBancosJSON();
            objBanco.cerrar();

            ResultSet compEgr = objComprobanteEgreso.getComprobanteEgreso(id);
            ResultSet compEgrDetalle = objComprobanteEgreso.getComprobanteEgresoDetalle(id);
            if(id.compareTo("-1")!=0){
                try{
                    if(compEgr.next()){
                        num_compEgr = (compEgr.getString("num_comprobante")!=null) ? compEgr.getString("num_comprobante") : "";
                        fecha_actual = (compEgr.getString("fecha_actual")!=null) ? Fecha.ISOaSQL(compEgr.getString("fecha_actual")) : fecha;
                        fecha_proceso = (compEgr.getString("fecha_proceso")!=null) ? Fecha.ISOaSQL(compEgr.getString("fecha_proceso")) : fecha;
                        fecha_en_efectivo = (compEgr.getString("fecha_en_efectivo")!=null) ? Fecha.ISOaSQL(compEgr.getString("fecha_en_efectivo")) : "";
                        ci_ruc = (compEgr.getString("ci_ruc")!=null) ? compEgr.getString("ci_ruc") : "";
                        orden_de = (compEgr.getString("orden_de")!=null) ? compEgr.getString("orden_de") : "";
                        tipo_pago = (compEgr.getString("tipo_pago")!=null) ? compEgr.getString("tipo_pago") : "e";
                        num_cheque = (compEgr.getString("num_cheque")!=null) ? compEgr.getInt("num_cheque") : 0;
                        cuenta = (compEgr.getString("cuenta")!=null) ? compEgr.getString("cuenta") : "";
                        id_banco = (compEgr.getString("id_banco")!=null) ? compEgr.getString("id_banco") : "";
                        banco = (compEgr.getString("banco")!=null) ? compEgr.getString("banco") : "";
                        total = (compEgr.getString("total")!=null) ? compEgr.getString("total") : "0";
                        son = (compEgr.getString("son")!=null) ? compEgr.getString("son") : "";
                        detalle = (compEgr.getString("detalle")!=null) ? compEgr.getString("detalle") : "";
                        lugar = (compEgr.getString("lugar")!=null) ? compEgr.getString("lugar") : "";
                        bloqueo = (compEgr.getString("bloqueo")!=null) ? compEgr.getBoolean("bloqueo") : false;
                        anulado = (compEgr.getString("anulado")!=null) ? compEgr.getBoolean("anulado") : false;
                        compEgr.close();
                    }
                }catch(Exception e){
                    e.printStackTrace();
                }
            }
            String cobro = (tipo_pago.compareTo("c")==0) ? "block" : "none";


            out.println("obj»cmp_html^foc»nb^frm»<div><input type='hidden' id='fe' value='"+fecha_en_efectivo+"' />"+
                    "<input type='hidden' id='caj' value='"+caja_chica+"' />" +
                    //"<input type='hidden' id='chEnC' value='"+chequeEmitidoNoCobrado+"' />" +
                    "<div id=\"axBc\" style='display:none'>"+jsBancos+"</div>" +
                    "<table width='750'>" +
                    "<tr><td align='center'><strong>C O M P R O B A N T E &nbsp;&nbsp; D E &nbsp;&nbsp; E G R E S O</strong></td>" +
                    "<td align='right' width='100'>Nro. "+num_compEgr+"</td></tr>" +
                    "</table>" +

                    "<table width='100%'>" +

                    "<tr><td>Nombre: <span class='marca'>*</span> &nbsp;&nbsp; <input id='nb' value='"+orden_de+"' type='text' size='60' maxlength='80' onfocus=\"fac_cmbActivo('nb')\" onkeyup=\"cnt_getProveedores();\" /></td>" +
                    "<td colspan='2'>CI/RUC: <span class='marca'>*</span> <input id='ru' value='"+ci_ruc+"' type='text' size='20' maxlength='13' class='may' /></td></tr>" +

                    "<tr><td colspan='3'>Concepto: <span class='marca'>*</span> <input id='dt' value='"+detalle+"' type='text' size='100' maxlength='200' /></td></tr>" +

                    "<tr><td colspan='2'>Tipo de Pago: <label><input type='radio' id='e0' name='fp' "+((tipo_pago.compareTo("e")==0)?"checked":"")+" onclick=\"_('pCh').style.display='none';cnt_setBanco();\" /> Efectivo </label> &nbsp;&nbsp;&nbsp;&nbsp;&nbsp; " +
                    "<label><input type='radio' id='e1' name='fp' "+((tipo_pago.compareTo("c")==0)?"checked":"")+" onclick=\"_('pCh').style.display='block';cnt_setBanco();\" /> Cheque </label></td></tr></table>" +


                    /*  cuando la forma de pago es cheque  */
                    "<table width='100%' id='pCh' style=\"display:"+cobro+";\" >" +
                    "<tr><td width='300'>Banco: &nbsp;&nbsp;&nbsp; "+DatosDinamicos.combo(rsBancos, "bc1", id_banco, "cnt_setBanco()", 230)+
                    "<input type='hidden' id='bc' name='bc' value=\""+banco+"\" /></td>" +
                    "<td width='200'>Cheque #: <span class='marca'>*</span> <input id='ch' value='"+num_cheque+"' type='text' size='15' maxlength='20' onkeypress=\"_numero(event);\"/></td>" +
                    "<td>Cuenta: &nbsp;&nbsp;<input id='ct' value='"+cuenta+"' type='text' size='20' maxlength='30' onkeypress=\"_evaluar(event, '0123456789-');\" /></td><tr>" +

                    "<tr><td>Lugar: <span class='marca'>*</span> &nbsp;&nbsp;&nbsp;<input id='lu' value='"+lugar+"' type='text' size='20' maxlength='30' onkeypress=\"_letra(event);\" /></td>" +
                    "<td colspan='2'>Fecha: &nbsp;&nbsp;&nbsp;<input id='fp' type='text'size='10' value='"+fecha_proceso+"' readOnly />" +
                    "<input type='button' value='...' onClick=\"SelectorFecha.crear('fp', 'SQL');\" /></td></tr></table>" +




                    "<div><a href='javascript:void(0);' style='float:left;' onclick=\"cnt_addCuenta();cnt_setSon('th');\">Insertar Cuenta</a> " +
                    "<span style='float:right;' class='marca' id='di'></span></div>" +
                    "&nbsp;&nbsp;<div style=\"display:none\" id='ax'>"+ DatosDinamicos.combo(cuentas, "_Xx_", "", "", 535)+"</div>" +
                    "<table class='formulario' cellpadding='0' cellspacing='0'>" +
                    "<TH class='jm_TH' width='550'>CODIGO - CUENTA</TH>" +
                    "<TH class='jm_TH' width='90'>DEBE</TH>" +
                    "<TH class='jm_TH' width='90'>HABER</TH>" +
                    "<TH class='jm_TH' width='20'>&nbsp;&nbsp;</TH></tr></table>" +

                    "<DIV id='sll0' style='overflow:auto;width:775px;height:170px;'>" +
                    "<TABLE class='jm_tabla' cellspacing='1' cellpadding='0' id='tblD'>");

            try{
                String id_plan_cuenta = "";
                String debe = "";
                String haber = "";
                int i=0;
                while(compEgrDetalle.next()){
                    id_plan_cuenta = (compEgrDetalle.getString("id_plan_cuenta")!=null) ? compEgrDetalle.getString("id_plan_cuenta") : "1";
                    debe = (compEgrDetalle.getString("debe")!=null) ? ((compEgrDetalle.getDouble("debe")>0)?compEgrDetalle.getString("debe"):"") : "";
                    haber = (compEgrDetalle.getString("haber")!=null) ? ((compEgrDetalle.getDouble("haber")>0)?compEgrDetalle.getString("haber"):"") : "";
                    out.println("<tr id='r"+i+"' class='jm_filaPar'>");
                    out.println("<td width='550'>" + DatosDinamicos.combo(cuentas, "PC"+i, id_plan_cuenta, "", 535) + "</td>");
                    out.println("<td width='90' align='right'><input id='d"+i+"' type='text' class='inpTbl' size='9' onkeypress=\"_evaluar(event, '0123456789.');_('h"+i+"').value='';\" onkeyup=\"cnt_sumTotales();cnt_setSon('th');\" " +
                            "onfocus=\"this.select();\" value='"+debe+"' /></td>");
                    out.println("<td width='90' align='right'><input id='h"+i+"' type='text' class='inpTbl' size='9' onkeypress=\"_evaluar(event, '0123456789.');_('d"+i+"').value='';\" onkeyup=\"cnt_sumTotales();cnt_setSon('th');\" " +
                            "onfocus=\"this.select();\" value='"+haber+"' /></td>");
                    out.println("<td width='20' align='center'><div class='jm_icoCon' style='background-position:-545px -18px;float:right;' alt='Eliminar' title='Eliminar' onclick=\"_R('r"+i+"');cnt_sumTotales();cnt_setSon('th');\">&nbsp;</div></td>");
                    out.println("</tr>");
                    i++;
                }
                if(i==0){
                    out.println("<tr id='r0' class='jm_filaPar'>");
                    out.println("<td width='550'>" + DatosDinamicos.combo(cuentas, "PC0", caja_chica, "", 535) + "</td>");
                    out.println("<td width='90' align='right'><input id='d0' type='text' class='inpTbl' size='9' onkeypress=\"_evaluar(event, '0123456789.');_('h0').value='';\" onkeyup=\"cnt_sumTotales();cnt_setSon('th');\" " +
                            "onfocus=\"this.select();\" /></td>");
                    out.println("<td width='90' align='right'><input id='h0' type='text' class='inpTbl' size='9' onkeypress=\"_evaluar(event, '0123456789.');_('d0').value='';\" onkeyup=\"cnt_sumTotales();cnt_setSon('th');\" " +
                            "onfocus=\"this.select();\" /></td>");
                    out.println("<td width='20' align='center'>&nbsp;</td>");
                    out.println("</tr>");
                }


                compEgrDetalle.close();
                cuentas.close();
                bancos.close();
            }catch(Exception e){
                e.printStackTrace();
            }

            out.println("</table></div>" +
                    "<table class='formulario' cellpadding='0' cellspacing='0'>" +
                    "<tr><td width='550' class='marca'>Son: <input id='sn' Style='width:450px;' value='"+son+"' type='text' maxlength='200' /> &nbsp;" +
                    "TOTALES: &nbsp;</td>" +
                    "<td id='td' align='right' width='90' class='marca'> "+total+" </td>" +
                    "<td id='th' align='right' width='90' class='marca'> "+total+" </td>" +
                    "<td width='20'>&nbsp;&nbsp;</td></tr></table></div>");

            out.println("<hr><table class='jm_tabla' width='100%' cellspacing='0' cellpadding='0'><tr>");
            if(!anulado){
                if(!bloqueo){
                    long fecha_sistema = Fecha.getTimeStamp(Fecha.getFecha("SQL"));
                    long fecha_asiento = Fecha.getTimeStamp(fecha_actual);
                    if(id.compareTo("-1")!=0 && objRol.getAcceso(id_rol, "comprobanteAnular") && (fecha_sistema==fecha_asiento || (fecha_sistema!=fecha_asiento && tipo_pago.compareTo("c")==0))){
                        out.println("<td><input type='button' value='Anular Comprobante' onclick=\"cnt_anularDocumento('tbl_comprobante_egreso', "+id+");\" /></td>");
                    }
                    if(id.compareTo("-1")!=0 && tipo_pago.compareTo("c")==0 && objRol.getAcceso(id_rol, "comprobanteModNumCheque")){
                        out.println("<td align='center'><input type='button' value='Actualizar # de Cheque' onclick=\"cnt_comprobanteSetNumCheque("+id+");\" /></td>");
                    }
                    if(id.compareTo("-1")!=0 && tipo_pago.compareTo("c")==0){
                        out.println("<td><input type='button' style='float:right;' value='Imprimir Cheque' onclick=\"imprimir('pdfCheque?id="+id+"');\" /></td>");
                    }
                    /*if(id.compareTo("-1")==0 || ((id.compareTo("-1")!=0 && objRol.getAcceso(id_rol, "comprobanteEditar")) &&
                        ((fecha_en_efectivo.compareTo("")==0 && tipo_pago.compareTo("c")==0) || (fecha_en_efectivo.compareTo("")!=0 && tipo_pago.compareTo("e")==0)))){*/
                    if(id.compareTo("-1")==0){
                        out.println("<td><input type='button' style='float:left;' value='Guardar' onclick='cnt_comprobanteEgresoGuardar(0, "+id+")' /></td>");
                        out.println("<td align='right'> <input type='button' value='Guardar e Imprimir' onclick='cnt_comprobanteEgresoGuardar(1, "+id+")' /></td>");
                    }else{
                        out.println("<td><input type='button' value='Imprimir Comprobante' onclick=\"imprimir('pdfComprobanteEgreso?id="+id+"');\" /></td>");
                    }
                }else{
                    out.println("<td><input type='button' value='Imprimir Comprobante' onclick=\"imprimir('pdfComprobanteEgreso?id="+id+"');\" /></td>");
                    if(id.compareTo("-1")!=0 && tipo_pago.compareTo("c")==0 && objRol.getAcceso(id_rol, "comprobanteModNumCheque")){
                        out.println("<td align='center'><input type='button' value='Actualizar # de Cheque' onclick=\"cnt_comprobanteSetNumCheque("+id+");\" /></td>");
                    }
                    out.println("<td><input type='button' style='float:right;' value='Imprimir Cheque' onclick=\"imprimir('pdfCheque?id="+id+"');\" /></td>");
                }
            }else{
                out.println("<td><span class='marca'>COMPROBANTE DE EGRESO ANULADO</span></td>");
            }

            out.println("</tr></table>");

        } finally {
            objComprobanteEgreso.cerrar();
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
