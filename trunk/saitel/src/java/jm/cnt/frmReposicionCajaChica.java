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
import jm.seg.clas.Rol;
import jm.web.Fecha;
import jm.adm.clas.Sucursal;
import jm.cnt.clas.Banco;
import jm.cnt.clas.ComprobanteEgreso;
import jm.cnt.clas.PlanCuenta;
import jm.cnt.clas.ReposicionCajaChica;
import jm.nom.clas.Empleado;
import jm.web.Addons;

/**
 *
 * @author Jorge
 */
public class frmReposicionCajaChica extends HttpServlet {
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

        ReposicionCajaChica objReposicionCajaChica = new ReposicionCajaChica(this._ip, this._puerto, this._db, usuario, clave);
        Rol objRol = new Rol(this._ip, this._puerto, this._db, usuario, clave);
        
        try {
            String resp_caja_chica_ci = "";
            String custodio = "";
            Empleado objEmpleado = new Empleado(this._ip, this._puerto, this._db, usuario, clave);
            try{
                ResultSet rsEmpleados = objEmpleado.getResponsableCajaChica(id_sucursal);
                if(rsEmpleados.next()){
                    resp_caja_chica_ci = rsEmpleados.getString("dni")!=null ? rsEmpleados.getString("dni") : "";
                    custodio = rsEmpleados.getString("empleado")!=null ? rsEmpleados.getString("empleado") : "";
                    rsEmpleados.close();
                }
            }catch(Exception e){
                e.printStackTrace();
            }finally{
                objEmpleado.cerrar();
            }

            String id_plan_cuenta_caja_chica = "";
            Sucursal objSucursal = new Sucursal(this._ip, this._puerto, this._db, usuario, clave);
            try{
                ResultSet rsSucursal = objSucursal.getSucursal(String.valueOf(id_sucursal));
                if(rsSucursal.next()){
                    id_plan_cuenta_caja_chica = rsSucursal.getString("id_plan_cuenta_caja_chica")!=null ? rsSucursal.getString("id_plan_cuenta_caja_chica") : "";
                    //custodio = rsSucursal.getString("empleado")!=null ? rsSucursal.getString("empleado") : "";
                    rsSucursal.close();
                }
            }catch(Exception e){
                e.printStackTrace();
            }finally{
                objSucursal.cerrar();
            }

            /*Configuracion conf = new Configuracion(this._ip, this._puerto, this._db, usuario, clave);
            String caja = conf.getValor("caja");
            conf.cerrar();*/


            String id = request.getParameter("id");
            int _altBody = 380;

            String id_comprobante_egreso = "-1";
            String num_reposicion_caja_chica = "";
            String fecha = Fecha.getFecha("SQL");
            String responsable=custodio;
            String saldo_caja = objReposicionCajaChica.getSaldoCajaChica(id_plan_cuenta_caja_chica);
            String periodo_desde = "";
            String hora_desde = "";
            //String periodo_hasta = objReposicionCajaChica.getPeriodoHasta(id) + " 23:59:59.999";
            String periodo_hasta = Fecha.getFecha("ISO") + " 23:59:59.999";
            float total_importe = 0;
            String total_reintegro = "0";
            boolean aprobado = false;
            String guardar = "";

            try{
                ResultSet rsReposicionCajaChica = objReposicionCajaChica.getFormulario(id);
                if(rsReposicionCajaChica.next()){
                    String hora_hasta = objReposicionCajaChica.getHoraHasta(id);
                    guardar = (rsReposicionCajaChica.getString("fecha")!=null) ? rsReposicionCajaChica.getString("fecha") : "";
                    id_comprobante_egreso = (rsReposicionCajaChica.getString("id_comprobante_egreso")!=null) ? rsReposicionCajaChica.getString("id_comprobante_egreso") : "-1";
                    num_reposicion_caja_chica = ((rsReposicionCajaChica.getString("id_sucursal")!=null) ? rsReposicionCajaChica.getString("id_sucursal") : "") + "-" + ((rsReposicionCajaChica.getString("num_reposicion_caja_chica")!=null) ? rsReposicionCajaChica.getString("num_reposicion_caja_chica") : "");
                    responsable = (rsReposicionCajaChica.getString("responsable")!=null) ? rsReposicionCajaChica.getString("responsable") : custodio;
                    fecha = (rsReposicionCajaChica.getString("fecha")!=null) ? Fecha.ISOaSQL(rsReposicionCajaChica.getString("fecha")) : fecha;
                    saldo_caja = (rsReposicionCajaChica.getString("saldo_caja")!=null) ? rsReposicionCajaChica.getString("saldo_caja") : saldo_caja;
                    periodo_desde = (rsReposicionCajaChica.getString("periodo_desde")!=null) ? rsReposicionCajaChica.getString("periodo_desde") : "";
                    hora_desde = (rsReposicionCajaChica.getString("hora")!=null) ? rsReposicionCajaChica.getString("hora") : "";
                    periodo_hasta = (rsReposicionCajaChica.getString("periodo_hasta")!=null) ? rsReposicionCajaChica.getString("periodo_hasta") + " " + hora_hasta : periodo_hasta;
                    aprobado = (rsReposicionCajaChica.getString("aprobado")!=null) ? rsReposicionCajaChica.getBoolean("aprobado") : false;
                    //total_importe = (rsReposicionCajaChica.getString("total_importe")!=null) ? rsReposicionCajaChica.getString("total_importe") : "0";
                    rsReposicionCajaChica.close();
                }
            }catch(Exception e){
                e.printStackTrace();
            }



            String html = "obj»vta1_html^fun»_objTab.seleccionar(0);cnt_setSon('th')^frm»";
            html += "<form id='forml' action='frmReposicionCajaChicaGuardar' onsubmit='return cnt_rep_caja_chicaGuardar(this)' autocomplete='off'>";
            html += "<input type='hidden' id='id' name='id' value='"+id+"' />";
            html += "<input type='hidden' id='imp' name='imp' value='0' />";

            html += "<div class='grupoTab' style='width:740px;'><div id='tabR0'>";
            html += "<a id='tab00' href='javascript:void(0);' class='jm_tabAct' onclick=\"_objTab.seleccionar(0);\">Reposici&oacute;n</a>";
            html += "<a id='tab01' href='javascript:void(0);' class='jm_tab' onclick=\"_objTab.seleccionar(1);cnt_setBanco();cnt_setSon('th');\">Comp. egreso</a>";
            html += "</div>";

            
            
            // PRIMER TAB
            
            
            
            html += "<div id='tabR1'>";
            html += "<div id='tab10' class='jm_tabHTML' style='display:block;width:730px;height:"+_altBody+"px;'>";

            html += "<table width='100%'><tr><td colspan='3' align='right'>Nro. "+num_reposicion_caja_chica+"</td></tr>";
            html += "<tr><td width='170'>Fecha: </td><td><input type='text' id='fecha' name='fecha' value='"+fecha+"' size='10' readOnly /></td>";
            html += "<td align='right'>Saldo en caja chica: <input type='text' id='saldo_caja' name='saldo_caja' value='"+saldo_caja+"' size='10' readOnly /></td></tr>";
            html += "<td>Per&iacute;odo comprendido entre: </td><td colspan='2'><input type='text' id='periodo_desde' name='periodo_desde' value='"+Fecha.ISOaSQL(periodo_desde)+"' size='10' readOnly />"
                    + " hasta <input type='text' id='periodo_hasta' name='periodo_hasta' value='"+Fecha.ISOaSQL(Fecha.truncFecha(periodo_hasta))+"' size='10' readOnly />"
                    + "<input type='button' value='...' onClick=\"SelectorFecha.crear('fecha', 'SQL');\" /></td></tr>";
            html += "<tr><td>Responsable del fondo: </td><td colspan='2'><input type='text' id='responsable' name='responsable' value='"+responsable+"' size='60' readOnly /></td></tr>";
            html += "</table>";

            html += "<table cellpadding='0' cellspacing='0'>" +
                    "<TH class='jm_TH' width='40'>Nro. DOC.</TH>" +
                    "<TH class='jm_TH' width='70'>FECHA</TH>" +
                    "<TH class='jm_TH' width='110'>CUENTA CONTABLE</TH>" +
                    "<TH class='jm_TH' width='110'>ENTREGADO A</TH>" +
                    "<TH class='jm_TH' width='160'>CONCEPTO</TH>" +
                    "<TH class='jm_TH' width='60'>IMPORTE ($)</TH>"; 
            if(!aprobado){        
                html += "<TH class='jm_TH' width='50'>APR.</TH>"+ 
                    "<TH class='jm_TH' width='100'>OBSERVACION</TH>"; 
            }   
            html += "</tr></table>"+
                    "<TABLE class='jm_tabla' cellspacing='1' cellpadding='0' id='tblCh'>";
            int j=0;
            try{
                ResultSet rsVales = objReposicionCajaChica.getValesCajaChica(id);
                while(rsVales.next()){
                    html += "<tr class='jm_filaPar'>";
                    html += "<td width='40'>"+(rsVales.getString("num_vale_caja")!=null ? rsVales.getString("num_vale_caja") : "")+"</td>";
                    html += "<td width='70'>"+(rsVales.getString("fecha")!=null ? Fecha.ISOaSQL(rsVales.getString("fecha")) : "")+"</td>";
                    html += "<td width='110'>"+(rsVales.getString("nombre_cuenta")!=null ? rsVales.getString("nombre_cuenta") : "")+"</td>";
                    html += "<td width='110'>"+(rsVales.getString("solicitante")!=null ? rsVales.getString("solicitante") : "")+"</td>";
                    html += "<td width='160'>"+(rsVales.getString("concepto")!=null ? rsVales.getString("concepto") : "")+"</dt>";
                    html += "<td width='60' align='right'>"+(rsVales.getString("importe")!=null ? rsVales.getString("importe") : "")+"</td>";
                    if(!aprobado){
                        html += "<td width='50'><label onclick=\"_('obs"+j+"').style.visibility='hidden'\"><input type='radio' id='ap"+j+"' name='ap"+j+"' value='si' />Si</label> &nbsp; "
                            + "<label onclick=\"_('obs"+j+"').style.visibility='visible';_('obs"+j+"').focus();\"><input type='radio' id='apb"+j+"' name='ap"+j+"' value='no' />No</label></td>";
                        html += "<td width='100'><textarea id='obs"+j+"' name='obs"+j+"' style=\"width:95px;height:50px;visibility:hidden\"></textarea>"
                            + "<input type='hidden' id='tipDoc"+j+"' name='tipDoc"+j+"' value='v' />"
                            + "<input type='hidden' id='id_num_val"+j+"' name='id_num_val"+j+"' value='"+(rsVales.getString("id_vale_caja")!=null ? rsVales.getString("id_vale_caja") : "")+"' /></td>";
                    }
                    html += "</tr>";
                    total_importe += rsVales.getString("importe")!=null ? rsVales.getFloat("importe") : 0;
                    j++;
                }
                rsVales.close();
            }catch(Exception e){}

            ComprobanteEgreso objCompEgre = new ComprobanteEgreso(this._ip, this._puerto, this._db, usuario, clave);
            try{
                ResultSet rsComp = objCompEgre.getComprobantesEgreso(id, periodo_desde + " " + hora_desde, periodo_hasta );
                while(rsComp.next()){
                    html += "<tr class='jm_filaPar'>";
                    html += "<td>"+(rsComp.getString("num_comprobante")!=null ? rsComp.getString("num_comprobante") : "")+"</td>";
                    html += "<td>"+(rsComp.getString("fecha_actual")!=null ? Fecha.ISOaSQL(rsComp.getString("fecha_actual")) : "")+"</td>";
                    html += "<td>PAGO EN EFECTIVO POR LIQUIDACION DE C/S, NOTA DE CREDITO O FACTURA</td>";
                    html += "<td>"+(rsComp.getString("orden_de")!=null ? rsComp.getString("orden_de") : "")+"</td>";
                    html += "<td>"+(rsComp.getString("detalle")!=null ? rsComp.getString("detalle") : "")+"</dt>";
                    html += "<td align='right'>"+(rsComp.getString("valor_son")!=null ? rsComp.getString("valor_son") : "0")+"</td>";
                    if(!aprobado){
                        html += "<td width='50'><label onclick=\"_('obs"+j+"').style.visibility='hidden'\"><input type='radio' id='ap"+j+"' name='ap"+j+"' value='si' />Si</label> &nbsp; "
                            + "<label onclick=\"_('obs"+j+"').style.visibility='visible';_('obs"+j+"').focus();\"><input type='radio' id='apb"+j+"' name='ap"+j+"' value='no' />No</label></td>";
                    
                        html += "<td><textarea id='obs"+j+"' name='obs"+j+"' style=\"width:95px;height:50px;visibility:hidden\"></textarea>"
                            + "<input type='hidden' id='tipDoc"+j+"' name='tipDoc"+j+"' value='l' />"
                            + "<input type='hidden' id='id_num_val"+j+"' name='id_num_val"+j+"' value='"+(rsComp.getString("id_comprobante_egreso")!=null ? rsComp.getString("id_comprobante_egreso") : "")+"' /></td>";
                    }
                    html += "</tr>";
                    total_importe += rsComp.getString("valor_son")!=null ? rsComp.getFloat("valor_son") : 0;
                    j++;
                }
                rsComp.close();
            }catch(Exception e){
                e.printStackTrace();
            }finally{
                objCompEgre.cerrar();
            }

            //total_importe = String.valueOf(Float.parseFloat(total_importe) + sum_comp);
            total_reintegro = String.valueOf(Addons.redondear(total_importe));

            html += "<tr class='jm_filaPar'><td colspan='5'>REINTEGRO SOLICITADO</td>"
                    + "<td style='text-align:right;'> $ "+total_reintegro+"</td><td colspan='2'></td></tr>";

            html += "</table>";


            html += "</div>";





            // SEGUNDO TAB


            html += "<div id='tab11' class='jm_tabHTML' style='display:none;width:730px;height:"+_altBody+"px;'>";

            ComprobanteEgreso objComprobanteEgreso = new ComprobanteEgreso(this._ip, this._puerto, this._db, usuario, clave);

            String fecha_comp = Fecha.getFecha("SQL");
            String num_compEgr = "";
            long num_cheque = objComprobanteEgreso.getNumCheque();
            String fecha_proceso = fecha_comp;
            String fecha_en_efectivo = "";
            String ci_ruc = resp_caja_chica_ci;
            String orden_de = custodio;
            String tipo_pago = "c";
            String cuenta = "";
            String id_banco = "";
            String banco = "";
            String total = "";
            
            String son = "";
            String detalle = "Reposición de fondo de caja chica. Responsable " + orden_de;
            String lugar = "";

            Banco objBanco = new Banco(this._ip, this._puerto, this._db, usuario, clave);
            ResultSet rsBancos = objBanco.getBancos();
            try{
                if(rsBancos.next()){
                    banco = rsBancos.getString("banco")!=null ? rsBancos.getString("banco") : "";
                }
            }catch(Exception ex){}
            String jsBancos = objBanco.getBancosJSON();
            objBanco.cerrar();

            ResultSet compEgr = objComprobanteEgreso.getComprobanteEgreso(id_comprobante_egreso);
            ResultSet compEgrDetalle = objComprobanteEgreso.getComprobanteEgresoDetalle(id_comprobante_egreso);
            if(id.compareTo("-1")!=0){
                try{
                    if(compEgr.next()){
                        num_compEgr = (compEgr.getString("num_comprobante")!=null) ? compEgr.getString("num_comprobante") : "";
                        fecha_proceso = (compEgr.getString("fecha_proceso")!=null) ? Fecha.ISOaSQL(compEgr.getString("fecha_proceso")) : fecha;
                        fecha_en_efectivo = (compEgr.getString("fecha_en_efectivo")!=null) ? Fecha.ISOaSQL(compEgr.getString("fecha_en_efectivo")) : "";
                        ci_ruc = (compEgr.getString("ci_ruc")!=null) ? compEgr.getString("ci_ruc") : "";
                        orden_de = (compEgr.getString("orden_de")!=null) ? compEgr.getString("orden_de") : "";
                        tipo_pago = (compEgr.getString("tipo_pago")!=null) ? compEgr.getString("tipo_pago") : "c";
                        num_cheque = (compEgr.getString("num_cheque")!=null) ? compEgr.getLong("num_cheque") : null;
                        cuenta = (compEgr.getString("cuenta")!=null) ? compEgr.getString("cuenta") : "";
                        id_banco = (compEgr.getString("id_banco")!=null) ? compEgr.getString("id_banco") : "";
                        banco = (compEgr.getString("banco")!=null) ? compEgr.getString("banco") : "";
                        total = (compEgr.getString("total")!=null) ? compEgr.getString("total") :  "0";
                        son = (compEgr.getString("son")!=null) ? compEgr.getString("son") : "";
                        detalle = (compEgr.getString("detalle")!=null) ? compEgr.getString("detalle") : "";
                        lugar = (compEgr.getString("lugar")!=null) ? compEgr.getString("lugar") : "";
                        compEgr.close();
                    }
                }catch(Exception e){
                    e.printStackTrace();
                }finally{
                    objComprobanteEgreso.cerrar();
                }
            }
            //String cobro = (tipo_pago.compareTo("c")==0) ? "block" : "none";

            PlanCuenta objPlanCuenta = new PlanCuenta(this._ip, this._puerto, this._db, usuario, clave);
            ResultSet cuentas = objPlanCuenta.getTablasAsignacion();
            ResultSet bancos = objPlanCuenta.getTablasCajaBancos();
            objPlanCuenta.cerrar();


            html += "<div><input type='hidden' id='fe' value='"+fecha_en_efectivo+"' />"+
                    //"<input type='hidden' id='caja_chica' value='"+id_plan_cuenta_caja_chica+"' />" +
                    //"<input type='hidden' id='chEnC' value='"+chequeEmitidoNoCobrado+"' />" +
                    "<div id=\"axBc\" style='display:none'>"+jsBancos+"</div>" +
                    "<table width='700'>" +
                    "<tr><td align='center'><strong>C O M P R O B A N T E &nbsp;&nbsp; D E &nbsp;&nbsp; E G R E S O</strong></td>" +
                    "<td align='right' width='100'>Nro. "+num_compEgr+"</td></tr>" +
                    "</table>" +

                    "<table width='100%'>" +

                    "<tr><td>Nombre: <span class='marca'>*</span> &nbsp;&nbsp; <input id='nb' name='nb' value='"+orden_de+"' type='text' size='50' maxlength='80' /></td>" +
                    "<td colspan='2'>CI/RUC: <span class='marca'>*</span> <input id='ru' name='ru' value='"+ci_ruc+"' type='text' size='20' maxlength='13' onkeypress=\"_numero(event);\" /></td></tr>" +

                    "<tr><td colspan='3'>Concepto: <span class='marca'>*</span> <input id='dt' name='dt' value='"+detalle+"' type='text' size='100' maxlength='200' /></td></tr>" +

                    "<tr><td colspan='2'>Tipo de Pago: <label><input type='radio' id='e0' name='fpg' disabled "+((tipo_pago.compareTo("e")==0)?"checked":"")+" onclick=\"_('pCh').style.display='none';cnt_setBanco();\" /> Efectivo </label> &nbsp;&nbsp;&nbsp;&nbsp;&nbsp; " +
                    "<label><input type='radio' id='e1' name='fpg' disabled "+((tipo_pago.compareTo("c")==0)?"checked":"")+" onclick=\"_('pCh').style.display='block';cnt_setBanco();\" /> Cheque </label></td></tr></table>" +


                    /*  cuando la forma de pago es cheque  */
                    "<table width='100%' id='pCh' style=\"display:block;\" >" +
                    "<tr><td width='300'>Banco: &nbsp;&nbsp;&nbsp; "+DatosDinamicos.combo(rsBancos, "bc1", id_banco, "cnt_setBanco()", 230)+
                    "<input type='hidden' id='bc' name='bc' value=\""+banco+"\" /></td>" +
                    "<td width='200'>Cheque #: <span class='marca'>*</span> <input id='ch' name='ch' value='"+num_cheque+"' type='text' size='15' maxlength='20' onkeypress=\"_numero(event);\"/></td>" +
                    "<td>Cuenta: &nbsp;&nbsp;<input id='ct' name='ct' value='"+cuenta+"' type='text' size='20' maxlength='30' onkeypress=\"_evaluar(event, '0123456789-');\" /></td><tr>" +

                    "<tr><td>Lugar: <span class='marca'>*</span> &nbsp;&nbsp;&nbsp;<input id='lu' name='lu' value='"+lugar+"' type='text' size='20' maxlength='30' onkeypress=\"_letra(event);\" /></td>" +
                    "<td colspan='2'>Fecha: &nbsp;&nbsp;&nbsp;<input id='fp' name='fp' type='text'size='10' value='"+fecha_proceso+"' readOnly />" +
                    "<input type='button' value='...' onClick=\"SelectorFecha.crear('fp', 'SQL');\" /></td></tr></table>" +



                    "<div style='width:650px;height:20px'>"+
                    //"<a href='javascript:void(0);' style='float:left;' onclick=\"cnt_addCuenta();cnt_setSon('th');cnt_setBanco();\">Insertar Cuenta</a> " +
                    "<div style='float:right;' class='marca' id='di'></div></div>" +
                    //"&nbsp;&nbsp;<div style=\"display:none\" id='ax'>"+ DatosDinamicos.combo(cuentas, "_Xx_", "", "", 485)+"</div>" +
                    "<table cellpadding='0' cellspacing='0'>" +
                    "<TH class='jm_TH' width='450'>CODIGO - CUENTA</TH>" +
                    "<TH class='jm_TH' width='90'>DEBE</TH>" +
                    "<TH class='jm_TH' width='90'>HABER</TH>" +
                    "<TH class='jm_TH' width='20'>&nbsp;&nbsp;</TH></tr></table>" +

                    "<DIV style='overflow:auto;width:730px;height:90px;'>" +
                    "<table class='jm_tabla'  cellspacing='1' cellpadding='0' id='tblD'>";

                try{
                    String id_plan_cuenta = "";
                    String debe = "";
                    String haber = "";
                    int i=0;
                    while(compEgrDetalle.next()){
                        id_plan_cuenta = (compEgrDetalle.getString("id_plan_cuenta")!=null) ? compEgrDetalle.getString("id_plan_cuenta") : "1";
                        debe = (compEgrDetalle.getString("debe")!=null) ? ((compEgrDetalle.getDouble("debe")>0)?compEgrDetalle.getString("debe"):"") : "";
                        haber = (compEgrDetalle.getString("haber")!=null) ? ((compEgrDetalle.getDouble("haber")>0)?compEgrDetalle.getString("haber"):"") : "";
                        html += "<tr id='r"+i+"' class='jm_filaPar'>";
                        html += "<td style='width:450px'>" + DatosDinamicos.combo(cuentas, "PC"+i, id_plan_cuenta, "", 435, "disabled='disabled'") + "</td>";
                        html += "<td style='width:90px' align='right'><input id='d"+i+"' type='text' class='inpTbl' size='9' onkeypress=\"_evaluar(event, '0123456789.');_('h"+i+"').value='';\" onkeyup=\"cnt_sumTotales();cnt_setSon('th');\" " +
                                "onfocus=\"this.select();\" value='"+debe+"' /></td>";
                        html += "<td style='width:90px' align='right'><input id='h"+i+"' type='text' class='inpTbl' size='9' onkeypress=\"_evaluar(event, '0123456789.');_('d"+i+"').value='';\" onkeyup=\"cnt_sumTotales();cnt_setSon('th');\" " +
                                "onfocus=\"this.select();\" value='"+haber+"' /></td>";
                        html += "<td style='width:20px'><div class='jm_elim' alt='Eliminar' title='Eliminar' onclick=\"_R('r"+i+"');cnt_sumTotales();cnt_setSon('th');\">&nbsp;</div></td>";
                        html += "</tr>";
                        i++;
                    }
                    if(i==0){
                        html += "<tr id='r0' class='jm_filaPar'>";
                        html += "<td style='width:450px'>" + DatosDinamicos.combo(cuentas, "PC0", id_plan_cuenta_caja_chica, "", 435, "disabled='disabled'") + "</td>";
                        html += "<td style='width:90px' align='right'><input id='d0' name='d0' type='text' class='inpTbl' size='9' onkeypress=\"_evaluar(event, '0123456789.');_('h0').value='';\" value='"+total_importe+"' onkeyup=\"cnt_sumTotales();cnt_setSon('th');\" " +
                                "onfocus=\"this.select();\" /></td>";
                        html += "<td style='width:90px' align='right'><input id='h0' name='h0' type='text' class='inpTbl' size='9' onkeypress=\"_evaluar(event, '0123456789.');_('d0').value='';\" onkeyup=\"cnt_sumTotales();cnt_setSon('th');\" " +
                                "onfocus=\"this.select();\" /></td>";
                        html += "<td style='width:20px'>&nbsp;</td>";
                        html += "</tr>";
                        html += "<tr id='r1' class='jm_filaPar'>";
                        html += "<td style='width:450px'>" + DatosDinamicos.combo(cuentas, "PC1", "", "", 435, "disabled='disabled'") + "</td>";
                        html += "<td style='width:90px' align='right'><input id='d1' name='d1' type='text' class='inpTbl' size='9' onkeypress=\"_evaluar(event, '0123456789.');_('h1').value='';\" onkeyup=\"cnt_sumTotales();cnt_setSon('th');\" " +
                                "onfocus=\"this.select();\" /></td>";
                        html += "<td style='width:90px' align='right'><input id='h1' name='h1' type='text' class='inpTbl' size='9' onkeypress=\"_evaluar(event, '0123456789.');_('d1').value='';\" value='"+total_importe+"' onkeyup=\"cnt_sumTotales();cnt_setSon('th');\" " +
                                "onfocus=\"this.select();\" /></td>";
                        html += "<td style='width:20px'>&nbsp;</td>";
                        html += "</tr>";
                    }


                    compEgrDetalle.close();
                    cuentas.close();
                    bancos.close();
                }catch(Exception e){
                    e.printStackTrace();
                }

            html += "</table></div>" +
                    "<table cellpadding='0' cellspacing='0'>" +
                    "<tr><td width='450' class='marca'>Son: <input id='sn' name='sn' Style='width:350px;' value='"+son+"' type='text' maxlength='200' /> &nbsp;" +
                    "TOTALES: &nbsp;</td>" +
                    "<td id='td' align='right' width='90' class='marca'>"+total_reintegro+"</td>" +
                    "<td id='th' align='right' width='90' class='marca'>"+total_reintegro+"</td>" +
                    "<td width='20'>&nbsp;&nbsp;</td></tr></table></div>";




            html += "</div>";


            if(guardar.compareTo("")==0){
                if(aprobado){
                    html += "<input type='submit' value='Guardar e imprimir' onclick='this.disabled=true' />";
                }else{
                    html += "<input type='button' value='Registrar aprobaciones' id='btApr' onclick=\"cnt_rep_caja_chicaAprobar(_('forml'))\" />";
                }
            }else{
                html += "<input type='button' value='Re-imprimir formulario' onclick=\"imprimir('pdfReposicionCajaChica?id="+id+"');\" />";
            }
            html += "</div></div></form>";

            out.print(html);
            
        } finally {
            objRol.cerrar();
            objReposicionCajaChica.cerrar();
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
