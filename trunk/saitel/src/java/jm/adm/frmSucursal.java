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

package jm.adm;

import java.io.*;

import javax.servlet.*;
import javax.servlet.http.*;
import jm.adm.clas.Sucursal;
import java.sql.ResultSet;
import jm.adm.clas.Bodega;
import jm.adm.clas.PuntoEmision;
import jm.cnt.clas.PlanCuenta;
import jm.nom.clas.Empleado;
import jm.web.DatosDinamicos;
import jm.web.Fecha;

/**
 *
 * @author Jorge
 */
public class frmSucursal extends HttpServlet {
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
        try {
            int _altBody = 305;
            
            String id = request.getParameter("id");
            String sucursal = "";
            String ubicacion = "";
            String ciudad = "Ibarra";
            String id_bodega_cliente = "";
            String ips_computador = "127.0.0.1 - 0:0:0:0:0:0:0:1";
            String ips_red = "192.168.";
            String ip_servidor = "";
            String ip_inicio = "192.168.0.0";
            String fondo_caja_chica = "200";
            String min_caja_chica = "30";
            String id_plan_cuenta_caja_chica = "";
            String id_responsable_caja_chica = "";
            //String id_plan_cuenta_gasto = "";
            
            String liq_num_serie = "";
            String liq_sec_desde = "";
            String liq_sec_hasta = "";
            String liq_cad_libretin = "";
            String liq_autorizacion = "";
            String ret_num_serie = "";
            String ret_sec_desde = "";
            String ret_sec_hasta = "";
            String ret_cad_libretin = "";
            String ret_autorizacion = "";
            String not_cre_num_serie = "";
            String not_cre_sec_desde = "";
            String not_cre_sec_hasta = "";
            String not_cre_cad_libretin = "";
            String not_cre_autorizacion = "";
            String not_deb_num_serie = "";
            String not_deb_sec_desde = "";
            String not_deb_sec_hasta = "";
            String not_deb_cad_libretin = "";
            String not_deb_autorizacion = "";
            boolean estado = true;

            //ListaPrecio obj_ListaPrecio = new ListaPrecio(this._ip, this._puerto, this._db, usuario, clave);
            //ResultSet precios = obj_ListaPrecio.getListas();

            if(id.compareTo("-1")!=0){
                Sucursal objSucursal = new Sucursal(this._ip, this._puerto, this._db, usuario, clave);
                try{
                    ResultSet rs = objSucursal.getSucursal(id);
                    if(rs.next()){
                        sucursal = (rs.getString("sucursal")!=null) ? rs.getString("sucursal") : "";
                        ubicacion = (rs.getString("ubicacion")!=null) ? rs.getString("ubicacion") : "";
                        ciudad = (rs.getString("ciudad")!=null) ? rs.getString("ciudad") : "";
                        id_bodega_cliente = (rs.getString("id_bodega_cliente")!=null) ? rs.getString("id_bodega_cliente") : "";
                        ips_computador = (rs.getString("ips_computador")!=null) ? rs.getString("ips_computador") : "";
                        ips_red = (rs.getString("ips_red")!=null) ? rs.getString("ips_red") : "";
                        ip_servidor = (rs.getString("ip_servidor")!=null) ? rs.getString("ip_servidor") : "";
                        ip_inicio = (rs.getString("ip_inicio")!=null) ? rs.getString("ip_inicio") : "";
                        fondo_caja_chica = (rs.getString("fondo_caja_chica")!=null) ? rs.getString("fondo_caja_chica") : "";
                        min_caja_chica = (rs.getString("min_caja_chica")!=null) ? rs.getString("min_caja_chica") : "";
                        id_plan_cuenta_caja_chica = (rs.getString("id_plan_cuenta_caja_chica")!=null) ? rs.getString("id_plan_cuenta_caja_chica") : "";
                        id_responsable_caja_chica = (rs.getString("id_responsable_caja_chica")!=null) ? rs.getString("id_responsable_caja_chica") : "";
                        //id_plan_cuenta_gasto = (rs.getString("id_plan_cuenta_gasto")!=null) ? rs.getString("id_plan_cuenta_gasto") : "";

                        liq_num_serie = (rs.getString("liq_num_serie")!=null) ? rs.getString("liq_num_serie") : "";
                        liq_sec_desde = (rs.getString("liq_sec_desde")!=null) ? rs.getString("liq_sec_desde") : "";
                        liq_sec_hasta = (rs.getString("liq_sec_hasta")!=null) ? rs.getString("liq_sec_hasta") : "";
                        liq_cad_libretin = (rs.getString("liq_cad_libretin")!=null) ? Fecha.ISOaSQL(rs.getString("liq_cad_libretin")) : "";
                        liq_autorizacion = (rs.getString("liq_autorizacion")!=null) ? rs.getString("liq_autorizacion") : "";

                        ret_num_serie = (rs.getString("ret_num_serie")!=null) ? rs.getString("ret_num_serie") : "";
                        ret_sec_desde = (rs.getString("ret_sec_desde")!=null) ? rs.getString("ret_sec_desde") : "";
                        ret_sec_hasta = (rs.getString("ret_sec_hasta")!=null) ? rs.getString("ret_sec_hasta") : "";
                        ret_cad_libretin = (rs.getString("ret_cad_libretin")!=null) ? Fecha.ISOaSQL(rs.getString("ret_cad_libretin")) : "";
                        ret_autorizacion = (rs.getString("ret_autorizacion")!=null) ? rs.getString("ret_autorizacion") : "";

                        not_cre_num_serie = (rs.getString("not_cre_num_serie")!=null) ? rs.getString("not_cre_num_serie") : "";
                        not_cre_sec_desde = (rs.getString("not_cre_sec_desde")!=null) ? rs.getString("not_cre_sec_desde") : "";
                        not_cre_sec_hasta = (rs.getString("not_cre_sec_hasta")!=null) ? rs.getString("not_cre_sec_hasta") : "";
                        not_cre_cad_libretin = (rs.getString("not_cre_cad_libretin")!=null) ? Fecha.ISOaSQL(rs.getString("not_cre_cad_libretin")) : "";
                        not_cre_autorizacion = (rs.getString("not_cre_autorizacion")!=null) ? rs.getString("not_cre_autorizacion") : "";

                        not_deb_num_serie = (rs.getString("not_deb_num_serie")!=null) ? rs.getString("not_deb_num_serie") : "";
                        not_deb_sec_desde = (rs.getString("not_deb_sec_desde")!=null) ? rs.getString("not_deb_sec_desde") : "";
                        not_deb_sec_hasta = (rs.getString("not_deb_sec_hasta")!=null) ? rs.getString("not_deb_sec_hasta") : "";
                        not_deb_cad_libretin = (rs.getString("not_deb_cad_libretin")!=null) ? Fecha.ISOaSQL(rs.getString("not_deb_cad_libretin")) : "";
                        not_deb_autorizacion = (rs.getString("not_deb_autorizacion")!=null) ? rs.getString("not_deb_autorizacion") : "";

                        estado = (rs.getString("estado")!=null) ? rs.getBoolean("estado") : true;
                        rs.close();
                    }
                    
                }catch(Exception e){
                    e.printStackTrace();
                }finally{
                    objSucursal.cerrar();
                }
            }

            Bodega objBodega = new Bodega(this._ip, this._puerto, this._db, usuario, clave);
            ResultSet rsBodegas = objBodega.getBodegas();
            objBodega.cerrar();

            String html = "obj»d_21^foc»sucursal^fun»_objTab.seleccionar(0);^frm»";
            html += "<form action='frmSucursalGuardar' onsubmit='return adm_sucursalGuardar(this)' autocomplete='off'>";
            html += "<input type='hidden' id='id' name='id' value='"+id+"' />";
            
            html += "<div class='H3'>Formulario de Sucursal</div>";

            html += "<div class='grupoTab' style='width:510px;'>"
                  + "<div id='tabR0'>"
                  + "<a id='tab00' href='javascript:void(0);' class='jm_tabAct' onclick='_objTab.seleccionar(0);'>Sucursal</a>";
            html += "<a id='tab01' href='javascript:void(0);' class='jm_tab' onclick='_objTab.seleccionar(1);'>Configuraciones</a>";
            if(id.compareTo("-1")!=0){
                html += "<a id='tab02' href='javascript:void(0);' class='jm_tab' onclick='_objTab.seleccionar(2);'>Puntos de emisi&oacute;n</a>";
            }
            html += "</div>";

            /* PRIMER TAB */
            html += "<div id='tabR1'>";
            html += "<div id='tab10' class='jm_tabHTML' style='display:block;width:500px;height:"+_altBody+"px;'>";

            html += "<table>";
            html += "<tr><td width='150'>Nombre de la sucursal: <span class='marca'>*</span></td>";
            html += "<td><input id='sucursal' name='sucursal' class='may' value='"+sucursal+"' type='text' size='30' maxlength='60' onkeypress=\"_alfanumerico(event);\" onblur=\"this.value=this.value._trim();\" /></td></tr>";
            html += "<tr><td>Direcci&oacute;n: <span class='marca'>*</span></td>";
            html += "<td><input id='ubicacion' name='ubicacion' class='may' type='text' value='"+ubicacion+"' size='30' maxlength='60' onkeypress=\"_alfanumerico(event);\" onblur=\"this.value=this.value._trim();\" /></td></tr>";
            html += "<tr><td>Ciudad: <span class='marca'>*</span></td>";
            html += "<td><input id='ciudad' name='ciudad' type='text' value='"+ciudad+"' size='30' maxlength='60' onkeypress=\"_alfanumerico(event);\" onblur=\"this.value=this.value._trim();\" /></td></tr>";

            html += "<tr><td>Bodega para clientes: <span class='marca'>*</span></td>";
            html += "<td>" + DatosDinamicos.combo(rsBodegas, "id_bodega_cliente", id_bodega_cliente, "", " - NO ASIGNADA - ", 300) + "</td></tr>";

            html += "<tr valign='top'><td>Direcciones IP's de los computadores que se encuentran en la sucursal: </td>";
            html += "<td><textarea id='ips_computador' name='ips_computador' rows='5' cols='50' onkeypress=\"_evaluar(event, '0123456789:.- ');\" onblur=\"this.value=this.value._trim();\">" + ips_computador + "</textarea> </td></tr>";
            
            html += "<tr valign='top'><td>Direcciones de RED de la sucursal: </td>";
            html += "<td><textarea id='ips_red' name='ips_red' rows='5' cols='50' onkeypress=\"_evaluar(event, '0123456789:.- ');\" onblur=\"this.value=this.value._trim();\">" + ips_red + "</textarea> </td></tr>";
            
            html += "<tr><td>Direcci&oacute;n IP del servidor que ejecutar&aacute; el comando PING: <span class='marca'>*</span></td>";
            html += "<td><input id='ip_servidor' name='ip_servidor' type='text' size='30' maxlength='15' value='" + ip_servidor + "' onkeypress=\"_evaluar(event, '0123456789.');\" onblur=\"this.value=this.value._trim();\"></td></tr>";

            html += "<tr><td>Direcci&oacute;n IP de inicio: <span class='marca'>*</span></td>";
            html += "<td><input id='ip_inicio' name='ip_inicio' type='text' size='30' maxlength='15' value='" + ip_inicio + "' onkeypress=\"_evaluar(event, '0123456789.');\" onblur=\"this.value=this.value._trim();\"></td></tr>";

            html += "<tr><td>Estado: <span class='marca'>*</span></td>";
            html += "<td><input type='radio' id='estado0' name='estado' value='true' "+((estado) ? "checked" : "")+" /> Activo &nbsp;&nbsp;&nbsp;&nbsp; " +
                    "<input type='radio' id='estado1' name='estado' value='false' "+((!estado) ? "checked" : "")+" /> Inactivo</td></tr>";
            html += "</table>";
            
            html += "</div>";

            
            /* SEGUNDO TAB */

            PlanCuenta objPlanCuenta = new PlanCuenta(this._ip, this._puerto, this._db, usuario, clave);
            ResultSet rsCuentas = objPlanCuenta.getTablasAsignacion();
            objPlanCuenta.cerrar();

            Empleado objEmpleado = new Empleado(this._ip, this._puerto, this._db, usuario, clave);
            ResultSet rsEmpleados = objEmpleado.getEmpleadosDepartamentos(Integer.valueOf(id));
            objEmpleado.cerrar();

            html += "<div id='tab11' class='jm_tabHTML' style='display:none;width:500px;height:"+_altBody+"px;'>";

            if(id.compareTo("-1")!=0){
                html += "<div><input type='button' value='Fto. Liquidaciones' onclick=\"window.open('formatoLiquidacion?id="+id+"','_blank','top=5,left=5,width=750');\" />"
                     + " &nbsp;<input type='button' value='Fto. Retenciones' onclick=\"window.open('formatoRetencion?id="+id+"','_blank','top=5,left=5,width=750');\" />"
                     + " &nbsp;<input type='button' value='Fto. Not. cr&eacute;dito' onclick=\"window.open('formatoNotaCredito?id="+id+"','_blank','top=5,left=5,width=750');\" />"
                     + " &nbsp;<input type='button' value='Fto. Not. d&eacute;bito' onclick=\"window.open('formatoNotaDebito?id="+id+"','_blank','top=5,left=5,width=750');\" /></div>";
            }
            
            html += "<table>";
            html += "<tr><td>Fondo de caja Chica: <span class='marca'>*</span></td>";
            html += "<td><input id='fondo_caja_chica' name='fondo_caja_chica' type='text' size='10' maxlength='10' value='"+fondo_caja_chica+"' onkeypress=\"_numero(event);\" /></td>";
            html += "<tr><td>M&iacute;nimo en caja chica: <span class='marca'>*</span></td>";
            html += "<td><input id='min_caja_chica' name='min_caja_chica' type='text' size='10' maxlength='2' value='"+min_caja_chica+"' onkeypress=\"_evaluar(event, '0123456789.');\" /> %</td>";
            
            html += "<tr><td colspan='2'>Cuenta de caja chica: &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; " + DatosDinamicos.combo(rsCuentas, "id_plan_cuenta_caja_chica", id_plan_cuenta_caja_chica, "", 335) + "</td></tr>";
            html += "<tr><td colspan='2'>Responsable de caja chica: " + DatosDinamicos.combo(rsEmpleados, "id_responsable_caja_chica", id_responsable_caja_chica, "", " - SELECCIONE UN EMPLEADO - ", 335) + "</td></tr>";
            
            //html += "<tr><td colspan='2'>Cuenta de gasto de materiales: " + DatosDinamicos.combo(rsCuentas, "id_plan_cuenta_gasto", id_plan_cuenta_gasto, "", 315) + "</td></tr>";

            html += "<tr><td colspan='2'><hr /></td></tr>";
            html += "<tr><td>N&uacute;mero de serie de liquidaciones: </td>";
            html += "<td><input type='text' id='liq_num_serie' name='liq_num_serie' size='10' maxlength='7' value='"+liq_num_serie+"' onkeypress=\"_evaluar(event, '0123456789-');\" /></td></tr>";
            html += "<tr><td>Secuencial desde: </td>";
            html += "<td><input type='text' id='liq_sec_desde' name='liq_sec_desde' size='10' maxlength='10' value='"+liq_sec_desde+"' onkeypress='_numero(event);' />"
                 + " hasta <input type='text' id='liq_sec_hasta' name='liq_sec_hasta' size='10' maxlength='10' value='"+liq_sec_hasta+"' onkeypress='_numero(event);' /></td></tr>";
            html += "<tr><td>Fecha de caducidad del libret&iacute;n de liquidaciones: </td>";
            html += "<td><input type='text' id='liq_cad_libretin' name='liq_cad_libretin' size='10' value='"+liq_cad_libretin+"' readonly /><input type='button' value='...' onClick=\"SelectorFecha.crear('liq_cad_libretin', 'SQL');\" /> </td></tr>";
            html += "<tr><td>No. autorizaci&oacute;n del libret&iacute;n de liquidaciones: </td>";
            html += "<td><input type='text' id='liq_autorizacion' name='liq_autorizacion' size='10' maxlength='10' value='"+liq_autorizacion+"' onkeypress='_numero(event);' /></td></tr>";

            html += "<tr><td colspan='2'><hr /></td></tr>";

            html += "<tr><td>N&uacute;mero de serie de retenciones: </td>";
            html += "<td><input type='text' id='ret_num_serie' name='ret_num_serie' size='10' maxlength='7' value='"+ret_num_serie+"' onkeypress=\"_evaluar(event, '0123456789-');\" /></td></tr>";
            html += "<tr><td>Secuencial desde: </td>";
            html += "<td><input type='text' id='ret_sec_desde' name='ret_sec_desde' size='10' maxlength='10' value='"+ret_sec_desde+"' onkeypress='_numero(event);' />"
                 + " hasta <input type='text' id='ret_sec_hasta' name='ret_sec_hasta' size='10' maxlength='10' value='"+ret_sec_hasta+"' onkeypress='_numero(event);' /></td></tr>";
            html += "<tr><td nowrap>Fecha de caducidad del libret&iacute;n de retenciones: </td>";
            html += "<td><input type='text' id='ret_cad_libretin' name='ret_cad_libretin' size='10' value='"+ret_cad_libretin+"' onkeypress='_SUP(event);' /><input type='button' value='...' onClick=\"SelectorFecha.crear('ret_cad_libretin', 'SQL');\" /> </td></tr>";
            html += "<tr><td>No. autorizaci&oacute;n del libret&iacute;n de retenciones: </td>";
            html += "<td><input type='text' id='ret_autorizacion' name='ret_autorizacion' size='10' maxlength='10' value='"+ret_autorizacion+"' onkeypress='_numero(event);' /></td></tr>";
            
            html += "<tr><td colspan='2'><hr /></td></tr>";

            html += "<tr><td>N&uacute;mero de serie de notas de cr&eacute;dito: </td>";
            html += "<td><input type='text' id='not_cre_num_serie' name='not_cre_num_serie' size='10' maxlength='7' value='"+not_cre_num_serie+"' onkeypress=\"_evaluar(event, '0123456789-');\" /></td></tr>";
            html += "<tr><td>Secuencial desde: </td>";
            html += "<td><input type='text' id='not_cre_sec_desde' name='not_cre_sec_desde' size='10' maxlength='10' value='"+not_cre_sec_desde+"' onkeypress='_numero(event);' />"
                 + " hasta <input type='text' id='not_cre_sec_hasta' name='not_cre_sec_hasta' size='10' maxlength='10' value='"+not_cre_sec_hasta+"' onkeypress='_numero(event);' /></td></tr>";
            html += "<tr><td nowrap>Fecha de caducidad del libret&iacute;n de notas de cr&eacute;dito: </td>";
            html += "<td><input type='text' id='not_cre_cad_libretin' name='not_cre_cad_libretin' size='10' value='"+not_cre_cad_libretin+"' onkeypress='_SUP(event);' /><input type='button' value='...' onClick=\"SelectorFecha.crear('not_cre_cad_libretin', 'SQL');\" /> </td></tr>";
            html += "<tr><td>No. autorizaci&oacute;n del libret&iacute;n de notas de cr&eacute;dito: </td>";
            html += "<td><input type='text' id='not_cre_autorizacion' name='not_cre_autorizacion' size='10' maxlength='10' value='"+not_cre_autorizacion+"' onkeypress='_numero(event);' /></td></tr>";

            html += "<tr><td colspan='2'><hr /></td></tr>";

            html += "<tr><td>N&uacute;mero de serie de notas de d&eacute;bito: </td>";
            html += "<td><input type='text' id='not_deb_num_serie' name='not_deb_num_serie' size='10' maxlength='7' value='"+not_deb_num_serie+"' onkeypress=\"_evaluar(event, '0123456789-');\" /></td></tr>";
            html += "<tr><td>Secuencial desde: </td>";
            html += "<td><input type='text' id='not_deb_sec_desde' name='not_deb_sec_desde' size='10' maxlength='10' value='"+not_deb_sec_desde+"' onkeypress='_numero(event);' />"
                 + " hasta <input type='text' id='not_deb_sec_hasta' name='not_deb_sec_hasta' size='10' maxlength='10' value='"+not_deb_sec_hasta+"' onkeypress='_numero(event);' /></td></tr>";
            html += "<tr><td nowrap>Fecha de caducidad del libret&iacute;n de notas de d&eacute;bito: </td>";
            html += "<td><input type='text' id='not_deb_cad_libretin' name='not_deb_cad_libretin' size='10' value='"+not_deb_cad_libretin+"' onkeypress='_SUP(event);' /><input type='button' value='...' onClick=\"SelectorFecha.crear('not_deb_cad_libretin', 'SQL');\" /> </td></tr>";
            html += "<tr><td>No. autorizaci&oacute;n del libret&iacute;n de notas de d&eacute;bito: </td>";
            html += "<td><input type='text' id='not_deb_autorizacion' name='not_deb_autorizacion' size='10' maxlength='10' value='"+not_deb_autorizacion+"' onkeypress='_numero(event);' /></td></tr>";

            html += "</table>";
            

            html += "</div>";


            /* TERCER TAB */
            

            html += "<div id='tab12' class='jm_tabHTML' style='display:none;width:500px;height:"+_altBody+"px;'>";

            
            html += "<p>&nbsp;&nbsp;<a href='javascript:void(0);' id='inPE' onclick=\"adm_PuntoEmisionEditar(-1);\">Nuevo punto de emisi&oacute;n</a></p>";
            
            html += "<div id='axTblPE'><TABLE cellspacing='0' cellpadding='0'><TR>" +
                            "<TH class='jm_TH' width='120'>P. DE EMISION</TH>" +
                            "<TH class='jm_TH' width='100'>USUARIO</TH>" +
                            "<TH class='jm_TH' width='100'>FACTURAS</TH>" +
                            "<TH class='jm_TH' width='20'>&nbsp;</TH></TR></TABLE>";
            html += "<DIV style='overflow:auto;width:360px;height:"+(_altBody-55)+"px;' id='sll'>" +
            "<TABLE class='jm_tabla' cellspacing='1' cellpadding='0' id='tblPE'>";
            
            PuntoEmision objPuntoEmision = new PuntoEmision(this._ip, this._puerto, this._db, usuario, clave);
            try{
                int i=0;
                String id_punto_emision = "";
                String punto_emision = "";
                String ip_computador = "";
                ResultSet rsPuntos = objPuntoEmision.getPuntosEmision(id);
                while(rsPuntos.next()){
                    id_punto_emision = (rsPuntos.getString("id_punto_emision")!=null) ? rsPuntos.getString("id_punto_emision") : "";
                    punto_emision = (rsPuntos.getString("punto_emision")!=null) ? rsPuntos.getString("punto_emision") : "";
                    ip_computador = (rsPuntos.getString("ip_computador")!=null) ? rsPuntos.getString("ip_computador") : "";
                    html += "<tr id='rTI"+i+"' valign='top' class='jm_filaPar' style='cursor:pointer' onmouseover=\"this.className='jm_filaSobre'\" onmouseout=\"this.className='jm_filaPar'\">";
                    html += "<td width='120' style='cursor:pointer' onclick=\"adm_PuntoEmisionEditar("+id_punto_emision+");\">"+punto_emision+"</td>";
                    html += "<td width='100' style='cursor:pointer' onclick=\"adm_PuntoEmisionEditar("+id_punto_emision+");\">"+ip_computador+"</td>";
                    html += "<td align='center' width='100'><a href='formatoFactura?id="+id_punto_emision+"' target='_blank'>modificar formato</a></td>";
                    html += "<td align='center' width='20'><input type='hidden' id='idR"+i+"' value='"+id_punto_emision+"' />&nbsp;</td></tr>";
                    i++;
                }
            }catch(Exception e){
                e.printStackTrace();
            }finally{
                objPuntoEmision.cerrar();
            }

            html += "</table></div>";

            
            html += "</div></div></div>";
            

            html += "<div><input type='submit' value='Guardar' /></div>";


            html += "</form>";
            
            out.print(html);

        }catch(Exception e){
            e.printStackTrace();
        } finally {
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
