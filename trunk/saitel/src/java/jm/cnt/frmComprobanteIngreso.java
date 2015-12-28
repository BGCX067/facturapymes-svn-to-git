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
import jm.web.DatosDinamicos;
import jm.web.Fecha;
import jm.seg.clas.Rol;
import jm.cnt.clas.PlanCuenta;
import jm.cnt.clas.ComprobanteIngreso;
import java.sql.ResultSet;
import jm.adm.clas.Configuracion;
import jm.cnt.clas.Banco;

/**
 *
 * @author Jorge
 */
public class frmComprobanteIngreso extends HttpServlet {
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


        Configuracion conf = new Configuracion(this._ip, this._puerto, this._db, usuario, clave);
        //String caja = conf.getValor("caja");
        //String chequeRecibidoNoCobrado = conf.getValor("chequeRecibidoNoCobrado");
        String gastos_bancos1 = conf.getValor("gastos_bancos");
        conf.cerrar();

        PlanCuenta objPlanCuenta = new PlanCuenta(this._ip, this._puerto, this._db, usuario, clave);
        ResultSet cuentas = objPlanCuenta.getTablasAsignacion();
        ResultSet bancos = objPlanCuenta.getTablasCajaBancos();
        objPlanCuenta.cerrar();

        Rol objRol = new Rol(this._ip, this._puerto, this._db, usuario, clave);
        
        Banco objBanco = new Banco(this._ip, this._puerto, this._db, usuario, clave);
        ResultSet rsBancos = objBanco.getBancosIngresos();

        ComprobanteIngreso objComprobanteIngreso = new ComprobanteIngreso(this._ip, this._puerto, this._db, usuario, clave);
        try {
            String caja = "";
            String fecha = Fecha.getFecha("SQL");
            String num_compIngr = "";
            String fecha_en_efectivo = "";
            String fecha_actual = fecha;
            String fecha_proceso = fecha;
            String nombre = "";
            String tipo_cobro = "e";
            String num_cheque = "";
            String banco = "";
            String num_comp_pago = "";
            String gastos_bancos = "0";
            String total = "0";
            String son = "";
            String detalle = "";
            boolean bloqueo = false;
            boolean anulado = false;
            ResultSet compIngr = objComprobanteIngreso.getComprobanteIngreso(id);
            ResultSet compIngrDetalle = objComprobanteIngreso.getComprobanteIngresoDetalle(id);
            if(id.compareTo("-1")!=0){
                try{
                    if(compIngr.next()){
                        num_compIngr = (compIngr.getString("num_comprobante")!=null) ? compIngr.getString("num_comprobante") : "";
                        fecha_en_efectivo = (compIngr.getString("fecha_en_efectivo")!=null) ? Fecha.ISOaSQL(compIngr.getString("fecha_en_efectivo")) : "";
                        fecha_actual = (compIngr.getString("fecha_actual")!=null) ? Fecha.ISOaSQL(compIngr.getString("fecha_actual")) : fecha;
                        fecha_proceso = (compIngr.getString("fecha_proceso")!=null) ? Fecha.ISOaSQL(compIngr.getString("fecha_proceso")) : fecha;
                        nombre = (compIngr.getString("nombre")!=null) ? compIngr.getString("nombre") : "";
                        tipo_cobro = (compIngr.getString("tipo_cobro")!=null) ? compIngr.getString("tipo_cobro") : "e";
                        num_cheque = (compIngr.getString("num_cheque")!=null) ? compIngr.getString("num_cheque") : "";
                        banco = (compIngr.getString("banco")!=null) ? compIngr.getString("banco") : "";
                        num_comp_pago = (compIngr.getString("num_comp_pago")!=null) ? compIngr.getString("num_comp_pago") : "";
                        gastos_bancos = (compIngr.getString("gastos_bancos")!=null) ? compIngr.getString("gastos_bancos") : "";
                        total = (compIngr.getString("total")!=null) ? compIngr.getString("total") : "0";
                        son = (compIngr.getString("son")!=null) ? compIngr.getString("son") : "";
                        detalle = (compIngr.getString("detalle")!=null) ? compIngr.getString("detalle") : "";
                        bloqueo = (compIngr.getString("bloqueo")!=null) ? compIngr.getBoolean("bloqueo") : false;
                        anulado = (compIngr.getString("anulado")!=null) ? compIngr.getBoolean("anulado") : false;
                        compIngr.close();
                    }
                }catch(Exception e){
                    e.printStackTrace();
                }
            }
            
            String cobro_cheque = (tipo_cobro.compareTo("c")==0) ? "block" : "none";
            String cobro_dep_tran = (tipo_cobro.compareTo("p")==0 || tipo_cobro.compareTo("t")==0 || tipo_cobro.compareTo("j")==0 || tipo_cobro.compareTo("n")==0) ? "block" : "none";
            String cobro_trans = (tipo_cobro.compareTo("t")==0 || tipo_cobro.compareTo("j")==0 || tipo_cobro.compareTo("n")==0) ? "block" : "none";

            String [][] forma_pago1 = {{"e","Efectivo"},{"c","Cheque"},{"p","Dep&oacute;sito bancario"},{"t","Transferencia bancaria"},{"j","Tarjeta de cr&eacute;dito"},{"n","Convenio de d&eacute;bito"}};

            out.println("obj»cmp_html^foc»nb^frm»<div>"
                    + "<input type='hidden' id='fe' value='"+fecha_en_efectivo+"' />" +
                    "<input type='hidden' id='caj' value='"+caja+"' />" +
                    //"<input type='hidden' id='chRnC' value='"+chequeRecibidoNoCobrado+"' />" +
                    "<input type='hidden' id='gas_ban' value='"+gastos_bancos1+"' />"+ 
                    "<input type='hidden' id='saldo_doc' name='saldo_doc' value='0' />" +
                    "<div><table width='750'>" +
                    "<tr><td align='center'><strong>C O M P R O B A N T E &nbsp;&nbsp; D E &nbsp;&nbsp; I N G R E S O</strong></td>" +
                    "<td align='right' width='100'>Nro. "+num_compIngr+"</td></tr>" +
                    "</table>" +

                    "<table width='100%'>" +

                    "<tr><td width='400'>Nombre: <span class='marca'>*</span> &nbsp;&nbsp; <input id='nb' value='"+nombre+"' type='text' size='60' maxlength='60' onblur=\"this.value=this.value._trim();\" /></td>" +
                    "<td>Fecha: <input id='fp' type='text' size='10' value='"+fecha_proceso+"' readOnly />" +
                    "<input type='button' value='...' onClick=\"SelectorFecha.crear('fp', 'SQL');\" /></td></tr>" +

                    "<tr><td colspan='2'>Concepto: <span class='marca'>*</span> <input id='dt' value='"+detalle+"' type='text' size='100' maxlength='200' onblur=\"this.value=this.value._trim();\" /> </td></tr>" +

                    "<tr><td colspan='2'>Forma de pago: " + DatosDinamicos.combo("foPa", tipo_cobro, forma_pago1, "cnt_setFormaPago(this);cnt_setBanco(1);") + "</td></tr>" +

                    "</table>" +

                    /*  cuando la forma de pago es cheque  */
                    "<table width='100%' id='pCh' style=\"display:"+cobro_cheque+";\" >" +
                    "<tr><td width='250'>Banco: <span class='marca'>*</span> <input id='bc' type='text' size='20' maxlength='50' value='"+banco+"' onkeypress=\"_letra(event);\" /></td>" +
                    "<td width='250'>Cheque #: <span class='marca'>*</span> <input id='ch' value='"+num_cheque+"' type='text' size='20' maxlength='30' onkeypress=\"_numero(event);\"/></td></tr>"+
                    "</table>" +
                    /*  cuando la forma de pago es transferencia, deposito  */
                    "<table id='pOtr' width='100%' style=\"display:"+cobro_dep_tran+";\" >" +
                    "<tr><td style='white-space:nowrap'>No. de comprobante de pago: <span class='marca'>*</span> <input id='ncp' type='text' size='20' maxlength='20' value='"+num_comp_pago+"' "
                    + "onkeydown=\"_NoE()\" onkeypress=\"_numero(event);\" onkeyup=\"var c=(_esN)?event.which:event.keyCode;if(c==13){fac_getSaldoDoc(this.value);}\" onblur=\"fac_getSaldoDoc(this.value);\" /></td>" +
                    "<td id='pTra' style='white-space:nowrap;display:"+cobro_trans+";'>Gastos bancarios: <span class='marca'>*</span> <input id='gb' type='text' size='20' value='"+gastos_bancos+"' onkeypress=\"_evaluar(event, '0123456789.');\" onkeyup=\"_('d1').value=this.value;cnt_sumTotales();\" /></td></tr>" +
                    "</table>" +


                    "<div><a href='javascript:void(0);' style='float:left;' onclick=\"cnt_addCuenta();cnt_setSon('td');\">Insertar Cuenta</a> " +
                    "<span style='float:right;' class='marca' id='di'></span></div>" +
                    "&nbsp;&nbsp;<div style=\"display:none\" id='ax'>"+ DatosDinamicos.combo(cuentas, "_Xx_", "", "", 535)+"</div>" +
                    "&nbsp;&nbsp;<div style=\"display:none\" id='axb'>"+ DatosDinamicos.combo(rsBancos, "_Xx_", "", "", 535)+"</div>" +
                    "<table class='formulario' cellpadding='0' cellspacing='0'>" +
                    "<TH class='jm_TH' width='550'>CODIGO - CUENTA</TH>" +
                    "<TH class='jm_TH' width='90'>DEBE</TH>" +
                    "<TH class='jm_TH' width='90'>HABER</TH>" +
                    "<TH class='jm_TH' width='20'>&nbsp;&nbsp;</TH></tr></table>" +

                    "<DIV id='sll0' style='overflow:auto;width:775px;height:165px;'>" +
                    "<TABLE class='jm_tabla' cellspacing='1' cellpadding='0' id='tblD'>");

            try{
                String id_plan_cuenta = "";
                String debe = "";
                String haber = "";
                int i=0;
                while(compIngrDetalle.next()){
                    id_plan_cuenta = (compIngrDetalle.getString("id_plan_cuenta")!=null) ? compIngrDetalle.getString("id_plan_cuenta") : "1";
                    debe = (compIngrDetalle.getString("debe")!=null) ? ((compIngrDetalle.getDouble("debe")>0)?compIngrDetalle.getString("debe"):"") : "";
                    haber = (compIngrDetalle.getString("haber")!=null) ? ((compIngrDetalle.getDouble("haber")>0)?compIngrDetalle.getString("haber"):"") : "";
                    out.println("<tr id='r"+i+"' class='jm_filaPar'>");
                    out.println("<td id='f0"+i+"' width='550'>" + DatosDinamicos.combo(cuentas, "PC"+i, id_plan_cuenta, "", 535) + "</td>");
                    out.println("<td width='90' align='right'><input id='d"+i+"' type='text' class='inpTbl' size='9' onkeypress=\"_evaluar(event, '0123456789.');_('h"+i+"').value='';\" onkeyup=\"cnt_sumTotales();cnt_setSon('td');\" " +
                            "onfocus=\"this.select();\" value='"+debe+"' /></td>");
                    out.println("<td width='90' align='right'><input id='h"+i+"' type='text' class='inpTbl' size='9' onkeypress=\"_evaluar(event, '0123456789.');_('d"+i+"').value='';\" onkeyup=\"cnt_sumTotales();cnt_setSon('td');\" " +
                            "onfocus=\"this.select();\" value='"+haber+"' /></td>");
                    out.println("<td width='20' align='center'><div class='jm_icoCon' style='background-position:-545px -18px;float:right;' alt='Eliminar' title='Eliminar' onclick=\"_R('r"+i+"');cnt_sumTotales();cnt_setSon('td');\">&nbsp;</div></td>");
                    out.println("</tr>");
                    i++;
                }
                if(i==0){
                    out.println("<tr id='r0' class='jm_filaPar'>");
                        out.println("<td id='f00' width='550'>" + DatosDinamicos.combo(cuentas, "PC0", "", "", 535) + "</td>");
                        out.println("<td width='90' align='right'><input id='d0' type='text' class='inpTbl' size='9' onkeypress=\"_evaluar(event, '0123456789.');\" onkeyup=\"cnt_sumTotales();cnt_setSon('td');\" " +
                                "onfocus=\"this.select();\" /></td>");
                        out.println("<td width='90' align='right'><input id='h0' type='text' class='inpTbl' size='9' readonly " +
                                "onfocus=\"this.select();\" /></td>");
                        out.println("<td width='20' align='center'>&nbsp;</td>");
                        out.println("</tr>");
                }

                compIngrDetalle.close();
                cuentas.close();
                bancos.close();
            }catch(Exception e){
                e.printStackTrace();
            }

            out.println("</table></div>" +
                    "<table cellpadding='0' cellspacing='0'>" +
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
                    if(id.compareTo("-1")!=0 && objRol.getAcceso(id_rol, "comprobanteAnular") && fecha_sistema==fecha_asiento){
                        out.println("<td><input type='button' value='Anular Comprobante' onclick=\"cnt_anularDocumento('tbl_comprobante_ingreso', "+id+");\" /></td>");
                    }
                    //if(id.compareTo("-1")==0 || ((id.compareTo("-1")!=0 && objRol.getAcceso(id_rol, "comprobanteEditar")) &&
                    if(id.compareTo("-1")==0){
                        out.println("<td><input type='button' value='Guardar' onclick='cnt_comprobanteIngresoGuardar(0, "+id+");' /></td>");
                        out.println("<td align='right'><input type='button' value='Guardar e Imprimir' onclick='cnt_comprobanteIngresoGuardar(1, "+id+");' /></td>");
                    }else{
                        out.println("<td><input type='button' value='Imprimir' onclick=\"imprimir('pdfComprobanteIngreso?id="+id+"');\" /></td>");
                    }
                }else{
                    out.println("<td><input type='button' value='Imprimir' onclick=\"imprimir('pdfComprobanteIngreso?id="+id+"');\" /></td>");
                }
            }else{
                out.println("<td><span class='marca'>COMPROBANTE DE INGRESO ANULADO</span></td>");
            }
            out.println("</tr></table>");

        } finally {
            objComprobanteIngreso.cerrar();
            objBanco.cerrar();
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
