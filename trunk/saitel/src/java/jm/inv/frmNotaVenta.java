/**
* @version 1.0
* @package NotaVentaPYMES.
* @author Jorge Washington Mueses Cevallos.
* @copyright Copyright (C) 2010 por Jorge Mueses. Todos los derechos reservados.
* @license http://www.gnu.org/copyleft/gpl.html GNU/GPL.
* NotaVentaPYMES! es un software de libre distribución, que puede ser
* copiado y distribuido bajo los términos de la Licencia Pública
* General GNU, de acuerdo con la publicada por la Free Software
* Foundation, versión 2 de la licencia o cualquier versión posterior.
*/

package jm.inv;

import java.io.*;

import javax.servlet.*;
import javax.servlet.http.*;
import jm.web.DatosDinamicos;
import jm.web.Fecha;
import jm.seg.clas.Rol;
import jm.adm.clas.Sucursal;
import jm.adm.clas.TablaImpuesto;
import jm.adm.clas.Configuracion;
import jm.inv.clas.Proveedor;
import jm.inv.clas.Producto;
import jm.inv.clas.NotaVenta;
import java.sql.ResultSet;
import jm.inv.clas.BaseImponible;

/**
 *
 * @author Jorge
 */
public class frmNotaVenta extends HttpServlet {
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

        String idNotaVenta = request.getParameter("id");
        //String accion = request.getParameter("act");
        //String _altBody = request.getParameter("altB");
        String _altBody = "350";

        NotaVenta objNotaVenta = new NotaVenta(this._ip, this._puerto, this._db, usuario, clave);

        Proveedor objProveedor = new Proveedor(this._ip, this._puerto, this._db, usuario, clave);
        ResultSet rsProveedores = objProveedor.getProveedores();
        
        BaseImponible objBaseImponible = new BaseImponible(this._ip, this._puerto, this._db, usuario, clave);

        TablaImpuesto objTablaImpuesto = new TablaImpuesto(this._ip, this._puerto, this._db, usuario, clave);
        Rol objRol = new Rol(this._ip, this._puerto, this._db, usuario, clave);

        //PlanCuenta objPlanCuenta = new PlanCuenta(this._ip, this._puerto, this._db, usuario, clave);
        //ResultSet cuentas = objPlanCuenta.getTablasAsignacion();

        Configuracion conf = new Configuracion(this._ip, this._puerto, this._db, usuario, clave);
        String p_iva1 = conf.getValor("p_iva1");
        conf.cerrar();

        String id_nota_credito_compra = "";

        String suc_ret_num_serie = "";
        String suc_ret_sec_desde = "";
        String suc_fac_sec_hasta = "";
        String suc_ret_cad_libretin = "";
        String suc_ret_autorizacion = "";
        Sucursal objSucursal = new Sucursal(this._ip, this._puerto, this._db, usuario, clave);
        try{
            ResultSet rs = objSucursal.getSucursal(String.valueOf(id_sucursal));
            if(rs.next()){
                suc_ret_num_serie = (rs.getString("ret_num_serie")!=null) ? rs.getString("ret_num_serie") : suc_ret_num_serie;
                suc_ret_sec_desde = (rs.getString("ret_sec_desde")!=null) ? rs.getString("ret_sec_desde") : "";
                suc_fac_sec_hasta = (rs.getString("ret_sec_hasta")!=null) ? rs.getString("ret_sec_hasta") : "";
                suc_ret_cad_libretin = (rs.getString("ret_cad_libretin")!=null) ? rs.getString("ret_cad_libretin") : "";
                suc_ret_autorizacion = (rs.getString("ret_autorizacion")!=null) ? rs.getString("ret_autorizacion") : suc_ret_autorizacion;
                rs.close();
            }
        }catch(Exception e){
            e.printStackTrace();
        }finally{
            objSucursal.cerrar();
        }

        Producto objProducto = new Producto(this._ip, this._puerto, this._db, usuario, clave);

        try {
            int i=0;
            String id_comprobante_diario = "";
            String id_proveedor = "";
            String razon_social = "";
            String serie_nota = "001-001";
            String autorizacion = "";
            String num_nota_venta = "";
            String fecha_ven_nota = "";
            String fecha = Fecha.getFecha("SQL");
            String fecha_compra = "";
            String total_compra = "";
            String observacion = "";
            boolean anulado = false;

            String sustento_tributario = "";
            String id_retencion_compra = "-1";
            String ret_num_serie = suc_ret_num_serie;
            String ret_autorizacion = suc_ret_autorizacion;
            String ret_num_retencion = String.valueOf(objNotaVenta.getNumRetencion());
            String ret_fecha_emision = "";
            int mes = Fecha.getMes();
            String ret_ejercicio_fiscal_mes = mes<10 ? "0" + mes : String.valueOf(mes);
            String ret_ejercicio_fiscal = String.valueOf(Fecha.getAnio());
            String ret_impuesto_retenido = "";
            boolean ret_anulado = false;
            boolean emitido_ret = false;

            //String comboProductos = "";
            //String [][] tipo_NotaVenta1 = {{"p","Productos"},{"s","Servicios"},{"b","Bienes"}};
            ResultSet NotaVentaDetalle = null;

            String ocultar = "hidden";

            if(idNotaVenta.compareTo("-1")!=0){
                ocultar = "visible";
                try{
                    ResultSet rsNotaVenta = objNotaVenta.getNotaVenta(idNotaVenta);
                    NotaVentaDetalle = objNotaVenta.getNotaVentaDetalle(idNotaVenta);
                    if(rsNotaVenta.next()){
                        id_comprobante_diario = (rsNotaVenta.getString("id_comprobante_diario")!=null) ? rsNotaVenta.getString("id_comprobante_diario") : "";
                        id_proveedor = (rsNotaVenta.getString("id_proveedor")!=null) ? rsNotaVenta.getString("id_proveedor") : "";
                        razon_social = (rsNotaVenta.getString("razon_social")!=null) ? rsNotaVenta.getString("razon_social") : "";
                        serie_nota = (rsNotaVenta.getString("serie_nota")!=null) ? rsNotaVenta.getString("serie_nota") : "001-001";
                        autorizacion = (rsNotaVenta.getString("autorizacion")!=null) ? rsNotaVenta.getString("autorizacion") : "";
                        num_nota_venta = (rsNotaVenta.getString("num_nota_venta")!=null) ? rsNotaVenta.getString("num_nota_venta") : "";
                        fecha_ven_nota = (rsNotaVenta.getString("fecha_ven_nota")!=null) ? Fecha.ISOaSQL(rsNotaVenta.getString("fecha_ven_nota")) : "";
                        fecha = (rsNotaVenta.getString("fecha")!=null) ? Fecha.ISOaSQL(rsNotaVenta.getString("fecha")) : "";
                        fecha_compra = (rsNotaVenta.getString("fecha_compra")!=null) ? Fecha.ISOaSQL(rsNotaVenta.getString("fecha_compra")) : "";
                        total_compra = (rsNotaVenta.getString("total_compra")!=null) ? rsNotaVenta.getString("total_compra") : "";
                        observacion = (rsNotaVenta.getString("observacion")!=null) ? rsNotaVenta.getString("observacion") : "";
                        anulado = (rsNotaVenta.getString("anulado")!=null) ? rsNotaVenta.getBoolean("anulado") : false;
                        rsNotaVenta.close();
                    }
                }catch(Exception e){
                    e.printStackTrace();
                }

                try{
                    ResultSet rsRetencion = objNotaVenta.getRetencion(idNotaVenta);
                    if(rsRetencion.next()){
                        ret_anulado = (rsRetencion.getString("anulado")!=null) ? rsRetencion.getBoolean("anulado") : false;
                        if(!ret_anulado){
                            sustento_tributario = (rsRetencion.getString("sustento_tributario")!=null) ? rsRetencion.getString("sustento_tributario") : "";
                            id_retencion_compra = (rsRetencion.getString("id_retencion_compra")!=null) ? rsRetencion.getString("id_retencion_compra") : "-1";
                            ret_num_serie = (rsRetencion.getString("ret_num_serie")!=null) ? rsRetencion.getString("ret_num_serie") : "001-001";
                            ret_autorizacion = (rsRetencion.getString("ret_autorizacion")!=null) ? rsRetencion.getString("ret_autorizacion") : "";
                            ret_num_retencion = (rsRetencion.getString("ret_num_retencion")!=null) ? rsRetencion.getString("ret_num_retencion") : "";
                            ret_fecha_emision = (rsRetencion.getString("ret_fecha_emision")!=null) ? Fecha.ISOaSQL(rsRetencion.getString("ret_fecha_emision")) : Fecha.getFecha("ISO");
                            ret_ejercicio_fiscal = (rsRetencion.getString("ret_ejercicio_fiscal")!=null) ? rsRetencion.getString("ret_ejercicio_fiscal") : ret_ejercicio_fiscal;
                            ret_ejercicio_fiscal_mes = (rsRetencion.getString("ret_ejercicio_fiscal_mes")!=null) ? rsRetencion.getString("ret_ejercicio_fiscal_mes") : "";
                            ret_impuesto_retenido = (rsRetencion.getString("ret_impuesto_retenido")!=null) ? rsRetencion.getString("ret_impuesto_retenido") : "";
                        }
                        rsRetencion.close();
                    }
                }catch(Exception e){
                    e.printStackTrace();
                }
            }

            String html="";


            out.print("obj»vta1_html^foc»busProv^fun»_objTab.seleccionar(0);^frm»");

            out.print("<div class='grupoTab' style='width:770px;'><div id='tabR0'>"
                    + "<a id='tab00' href='javascript:void(0);' class='jm_tabAct' onclick='_objTab.seleccionar(0);'>Nota de Venta</a>"
                    + "<a id='tab01' href='javascript:void(0);' class='jm_tab' onclick='_objTab.seleccionar(1);'>Retenci&oacute;n</a>");
            /*if(idNotaVenta.compareTo("-1")!=0){
                if(deuda == 0){
                    out.print("<a id='tab02' href='javascript:void(0);' class='jm_tab' onclick='_objTab.seleccionar(2);'>Nota Cr&eacute;dito</a>");
                }
                out.print("<a id='tab03' href='javascript:void(0);' class='jm_tab' onclick='_objTab.seleccionar(3);'>Nota D&eacute;bito</a>");
            }*/
            out.print("</div>");


            out.print("<form id='frmCompra' action='frmNotaVentaGuardar' onsubmit='return inv_nota_ventaGuardar(this)' autocomplete='off'>");
            out.print("<input type='hidden' id='id' name='id' value='"+idNotaVenta+"' />"
                    + "<input type='hidden' id='idCD' name='idCD' value='"+id_comprobante_diario+"' />"
                    + "<input type='hidden' id='idRC' name='idRC' value='"+id_retencion_compra+"' />"
                    + "<input type='hidden' id='retSecDes' name='retSecDes' value='"+suc_ret_sec_desde+"' />"
                    + "<input type='hidden' id='retSecHas' name='retSecHas' value='"+suc_fac_sec_hasta+"' />"
                    + "<input type='hidden' id='retFeCad' name='retFeCad' value='"+suc_ret_cad_libretin+"' />"
                    + "<input type='hidden' id='tope' name='tope' value='0' />"
                    + "<input type='hidden' id='coP' name='coP' value='-0' />"
                    + "<input type='hidden' id='p_iva1' name='p_iva1' value='"+p_iva1+"' />"
                    + "<input type='hidden' id='tpyDoc' value='v' />"
                    + "<input type='hidden' id='gr' name='gr' value='1' />");


            out.print("<div id='tabR1'>");

            /* primer tab */

            out.print("<div id='tab10' class='jm_tabHTML' style='display:block;width:760px;height:"+_altBody+"px;'>");

            out.print("<div id='mSg0' style=\"display:none\" class='msg'>&nbsp;</div>" +
                    "<table width='740' cellpadding='0' cellspacing='0'>" +
                    "<tr><td colspan='3'></td><td colspan='3'><span id='axMsP'></span></td></tr>"+
                    "<tr><td align='center' colspan='4'>N O T A &nbsp;&nbsp; D E &nbsp;&nbsp; V E N T A </td>" +
                    "<td align='right'>Nro. <span class='marca'>*</span></td>" +
                    "<td><input type='text' id='sf' name='sf' size='6' maxlength='7' value='"+serie_nota+"' onkeypress=\"_evaluar(event, '0123456789-');\" onkeyup='inv_setDetalleCompra()' />-" +
                    "<input type='text' size='6' id='nf' name='nf' value='"+num_nota_venta+"' onkeypress=\"_evaluar(event, '0123456789-');\" onkeyup='inv_setDetalleCompra()' /></td></tr>" +

                    "<tr><td>Proveedor: <span class='marca'>*</span></td>" +
                    "<td colspan='3'><div style='float:left;display:table-cell' id='aux_coP'>"+
                    "<input type='text' id='busProv' style='width:250px;' value='"+razon_social+"' onfocus='this.select()' onkeyup=\"inv_getProveedores(this.value);\" /></div>");

            if(idNotaVenta.compareTo("-1")==0){
                out.print("<div style='float:left;display:table-cell'> &nbsp;&nbsp; </div>"
                    + "<div class='jm_icoCon' style='background-position:-291px -138px;' title='Nuevo Proveedor' onclick=\"inv_getFrmProveedor('f')\">&nbsp;</div>");
            }
            out.print("<div style='float:left;display:table-cell'> &nbsp;&nbsp;&nbsp;&nbsp; </div>"
                    + "<div class='jm_icoCon' style='background-position:-314px -138px;' title='Nuevo Producto' onclick=\"inv_getFrmProducto()\">&nbsp;</div>");


            out.print("</td>" +
                    "<td align='right'>Autorizaci&oacute;n: </td><td><input type='text' size='10' maxlength='10' id='auf' name='auf' value='"+autorizacion+"' onkeypress=\"_numero(event);\" /></td>" +
                    "</tr>" +

                    "<tr><td>Fecha de compra: <span class='marca'>*</span> </td><td colspan='3'><input id='fc' name='fc' type='text'size='9' value='"+fecha_compra+"' readOnly />" +
                    "<input type='button' value='...' onClick=\"SelectorFecha.crear('fc', 'SQL');\" /></td>" +
                    "<td>Fecha de validez: <span class='marca'>*</span> </td><td><input id='fv' name='fv' type='text'size='9' value='"+fecha_ven_nota+"' readOnly />" +
                    "<input type='button' value='...' onClick=\"SelectorFecha.crear('fv', 'SQL');\" /></td></tr>" +

                    "<tr><td colspan='6'><div id='InAr' style='visibility:"+ocultar+"'>B&uacute;squeda de productos o servicios: &nbsp; <input type='text' id='busProd' onfocus=\"this.value=''\" style='width:350px;border-color:red' onkeyup=\"inv_getProductos(event, this.value, 'inv_tblProductos');\" /></div>" +
                    "&nbsp;&nbsp;<div style=\"display:none\" id='axp'></div></td>" +
                    "<div style=\"display:none\" id='ax'></div></td>" +
                    "</tr>" +
                    "</table>" +


                    "<table cellpadding='0' cellspacing='0'>" +
                    "<TH class='jm_TH' width='230'>C&Oacute;DIGO - DESCRIPCI&Oacute;N</TH>" +
                    "<TH class='jm_TH' width='50'>CANT.</TH>" +
                    "<TH class='jm_TH' width='80'>P./U.</TH>" +
                    "<TH class='jm_TH' width='80'>SUBTOT.</TH>" +
                    "<TH class='jm_TH' width='70' id='cabDes'>DESC.</TH>" +
                    "<TH class='jm_TH' width='70'>IVA</TH>" +
                    "<TH class='jm_TH' width='90'>TOTAL</TH>" +
                    "<TH class='jm_TH' width='20'>&nbsp;&nbsp;</TH></tr></table>" +

                    "<DIV id='sll0' style='overflow:auto;width:720px;height:160px;'>" +
                    "<TABLE class='jm_tabla' cellspacing='1' cellpadding='0'><tbody id='tblD'>");

            try{

                i=0;
                while(NotaVentaDetalle.next()){
                    html += "<tr id='r"+i+"' name='r"+i+"' class='jm_filaPar'>";
                    html += "<td width='230'>"+((NotaVentaDetalle.getString("codigo")!=null) ? NotaVentaDetalle.getString("codigo") : "")+" &nbsp; "
                            + ""+((NotaVentaDetalle.getString("descripcion")!=null) ? NotaVentaDetalle.getString("descripcion") : "")+"</td>";
                    html += "<td width='50' align='right'><input id='c"+i+"' name='c"+i+"' type='text' class='inpTbl' size='5' onkeypress=\"_numero(event);\" onkeyup=\"inv_sumSubTotal("+i+");inv_sumTotales();\" " +
                            "onfocus=\"this.select();\" value='"+((NotaVentaDetalle.getString("cantidad")!=null) ? NotaVentaDetalle.getString("cantidad") : "")+"' /></td>";
                    html += "<td width='81' align='right'><input id='pu"+i+"' name='pu"+i+"' type='text' class='inpTbl' size='8' onkeypress=\"_evaluar(event, '0123456789.');\" onkeyup=\"inv_sumSubTotal("+i+");inv_sumTotales();\" " +
                            "onfocus=\"this.select();\" value='"+((NotaVentaDetalle.getString("p_u")!=null) ? NotaVentaDetalle.getString("p_u") : "")+"' /></td>";
                    html += "<td width='81' align='right'><input id='st"+i+"' name='st"+i+"' type='text' class='inpTbl' size='8' onkeypress=\"_evaluar(event, '0123456789.');\" onkeyup=\"inv_sumTotales();\" " +
                            "onfocus=\"this.select();\" value='"+((NotaVentaDetalle.getString("total")!=null) ? NotaVentaDetalle.getString("total") : "")+"' /></td>";
                    html += "<td width='71' align='right'>0.00</td>";
                    html += "<td width='71' align='right'>0.00</td>";
                    html += "<td width='91' align='right'><input id='t"+i+"' name='t"+i+"' type='text' class='inpTbl' size='9' onkeypress=\"_evaluar(event, '0123456789.');\" onkeyup=\"inv_sumTotales();\" " +
                            "onfocus=\"this.select();\" value='"+((NotaVentaDetalle.getString("total")!=null) ? NotaVentaDetalle.getString("total") : "")+"' /></td>";
                    html += "<td width='21' align='center'><div class='jm_icoCon' style='background-position:-545px -18px;float:right;' title='Eliminar' onclick=\"_R('r"+i+"');inv_sumTotales();\">&nbsp;</div></td>";
                    html += "</tr>";
                    i++;
                }
                out.print(html);
            }catch(Exception e){
                e.printStackTrace();
            }

            out.print("</tbody></table></div>" +
                    "<table cellpadding='0' cellspacing='0'>" +
                    "<tr><td width='430'> Concepto .- <span class='marca'>*</span> <textarea id='ob' name='ob' cols='40' rows='2'>"+observacion+"</textarea></td>" +
                    "<td width='80'>TOTAL: <span class='marca'>*</span> </td>" +
                    "<td align='right' width='150'> <input type='text' id='tt' name='tt' value='"+total_compra+"' style='text-align:right' onkeypress=\"_evaluar(event, '0123456789.');\" onkeyup=\"inv_sumRetTotales();\" /></td></tr>" +

                    "<td width='20'>&nbsp;&nbsp;</td></tr>");

            out.println("</table>");
            out.print("</div>");


            
            
            
            
            
            

            /*  COMPROBANTE DE RETENCION  */

            
            
            
            String [][] sustento_tributario1 = {
                {"01","01-Crédito Tributario para declaración de IVA (Servicios y bienes distintos de inventarios y activos fijos)"},
                {"02","02-Costo o Gasto para declaración de Imp. a la Renta (Servicios y bienes distintos de inventarios y activos fijos)"},
                {"03","03-Activo Fijo - Crédito Tributario para declaración de IVA"},
                {"04","04-Activo Fijo - Costo o Gasto para declaración de Imp. a la Renta"},
                {"05","05-Liquidación de gastos de viaje, hospedaje y alimentación (a nombre de empleados y no de la empresa)"},
                {"06","06-Inventario - Crédito Tributario para declaración de IVA"},
                {"07","07-Inventario - Costo o Gasto para declaración de Imp. a la Renta"},
                {"08","08-Valor pagado para solicitar Reembolso de Gastos (Intermediario)"},
                {"09","09-Reembolso por Siniestros"},
                {"10","10-Distribución de Dividendos, Beneficios o Utilidades"},
                {"00","00-Casos especiales cuyo sustento no aplica en las opciones anteriores"}
            };
            
            

            ResultSet rsBasesImponibles = objBaseImponible.getBasesImponibles("9");
            String [][] ret_ejercicio_fiscal_mes1 = {{"01","Enero"},{"02","Febrero"},{"03","Marzo"},{"04","Abril"},{"05","Mayo"},{"06","Junio"},
                {"07","Julio"},{"08","Agosto"},{"09","Septiembre"},{"10","Octubre"},{"11","Noviembre"},{"12","Diciembre"}};
            

            out.println("<div id='tab11' class='jm_tabHTML' style='display:none;width:760px;height:"+_altBody+"px;'>");


            if((ret_num_retencion.compareTo("")==0 && objRol.getAcceso(id_rol, "compRetCompNueva")) || (ret_num_retencion.compareTo("")!=0 && objRol.getAcceso(id_rol, "compRetCompEditar"))){

                ResultSet rsTablaImpuestos = objTablaImpuesto.getTablaImpuestosVigente();

                out.println(
                        "<table width='740' cellpadding='0' cellspacing='0'>" +
                        "<tr><td width='300'></td><td colspan='2'><span id='axMsR'></span></td></tr>"+
                        "<tr><td align='center' colspan='2'> C O M P R O B A N T E &nbsp;&nbsp; D E &nbsp;&nbsp; R E T E N C I &Oacute; N</td>" +
                        "<td align='right'> Nro. <input type='text' id='sr' name='sr' size='6' maxlength='7' value='"+ret_num_serie+"' onkeypress=\"_evaluar(event, '0123456789-');\" />-" +
                        "<input type='text' size='6' id='nr' name='nr' value='"+ret_num_retencion+"' onkeypress=\"_numero(event);\" /></td></tr>" +

                        "<tr><td>Fecha de Emisi&oacute;n: <input id='fre' name='fre' type='text' size='9' value='"+ret_fecha_emision+"' onkeypress=\"_SUP(event);\" />" +
                        "<input type='button' value='...' onClick=\"SelectorFecha.crear('fre', 'SQL');\" /></td>" +
                        "<td>Ejercicio Fiscal: " + DatosDinamicos.combo("efm", ret_ejercicio_fiscal_mes, ret_ejercicio_fiscal_mes1, "") + "<input id='ef' name='ef' type='text'size='4' maxlength='4' value='"+ret_ejercicio_fiscal+"' onkeypress=\"_numero(event)\" /></td>" +
                        "<td align='right'>Autorizaci&oacute;n: <input type='text' size='10' maxlength='10' id='autr' name='autr' value='"+ret_autorizacion+"' onkeypress=\"_numero(event);\" /></td></tr>" +
                        "<tr><td colspan='3'>Sustento Tributario: " + DatosDinamicos.combo("sustento_tributario", sustento_tributario, sustento_tributario1, "") + "</td></tr>" +
                        "</table>" +

                        "<div id='InRe' style='visibility:"+ocultar+"'><a href='javascript:void(0);' style='float:left;' onclick=\"inv_addRetencion();\">Insertar Retenci&oacute;n</a></div>"
                        + "<div style=\"display:none\" id='axBI'>"+ DatosDinamicos.combo(rsBasesImponibles, "9a0", "9b0", "", "inv_sumRetSubTotales(9d0,1);inv_sumRetTotales();", "inv_sumRetSubTotales(9d0,1);inv_sumRetTotales();", "", 165) +"</div>" +
                        "&nbsp;&nbsp;<div style=\"display:none\" id='axCR'>"+ DatosDinamicos.combo(rsTablaImpuestos, "9a0", "9b0", "inv_setMsg('axMsR');", "inv_setConcepto(9c0);inv_sumRetSubTotales(9d0,1);inv_sumRetTotales();", "_('9e0').focus();", "", 295) +"</div>" +
                        "<div style=\"display:none\" id='axC'>"+ objTablaImpuesto.getConceptoRetencion() +"</div>" +
                        "<table cellpadding='0' cellspacing='0'>" +
                        "<TH class='jm_TH' width='170'>CAMPO BASE IMPONIBLE</TH>" +
                        "<TH class='jm_TH' width='300'>IMPUESTO - C&Oacute;DIGO - % DE RETENCI&Oacute;N</TH>" +
                        "<TH class='jm_TH' width='120'>BASE IMPONIBLE</TH>" +
                        "<TH class='jm_TH' width='120'>VALOR RETENIDO</TH>" +
                        "<TH class='jm_TH' width='20'>&nbsp;&nbsp;</TH></tr></table>" +

                        "<DIV id='sll1' style='overflow:auto;width:760px;height:105px;'>" +
                        "<TABLE class='jm_tabla' cellspacing='1' cellpadding='0' id='tblR'>");

                try{
                    ResultSet retencionDetalle = objNotaVenta.getRetencionDetalle(id_retencion_compra);
                    String campo_valor = "";
                    String impuesto = "";
                    String codigo_impuesto = "";
                    String porcentaje_retencion = "";
                    i=0;
                    html = "";
                    while(retencionDetalle.next()){
                        campo_valor = (retencionDetalle.getString("campo_valor")!=null) ? retencionDetalle.getString("campo_valor") : "";
                        impuesto = (retencionDetalle.getString("impuesto")!=null) ? retencionDetalle.getString("impuesto") : "";
                        codigo_impuesto = (retencionDetalle.getString("codigo_impuesto")!=null) ? retencionDetalle.getString("codigo_impuesto") : "";
                        porcentaje_retencion = (retencionDetalle.getString("porcentaje_retencion")!=null) ? retencionDetalle.getString("porcentaje_retencion") : "";
                        html += "<tr id='rRe"+i+"' class='jm_filaPar'>";
                        html += "<td width='170'>" + campo_valor + "</td>";
                        html += "<td width='300'>" + impuesto + " &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; " +
                                codigo_impuesto + " &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; " +
                                porcentaje_retencion + "%</td>";
                        html += "<td width='121' align='right'><input id='bi"+i+"' name='bi"+i+"' type='text' class='inpTbl' size='20' onkeypress=\"_evaluar(event, '0123456789.');\" onkeyup=\"inv_sumRetSubTotales("+i+");inv_sumRetTotales();\" " +
                                "onfocus=\"this.select();\" value='"+((retencionDetalle.getString("base_imponible")!=null) ? retencionDetalle.getString("base_imponible") : "")+"' /></td>";
                        html += "<td width='121' align='right'><input id='vr"+i+"' name='vr"+i+"' type='text' class='inpTbl' size='20' onkeypress=\"_evaluar(event, '0123456789.');\" onkeyup=\"inv_sumRetTotales();\" " +
                                "onfocus=\"this.select();\" value='"+((retencionDetalle.getString("valor_retenido")!=null) ? retencionDetalle.getString("valor_retenido") : "")+"' /></td>";
                        //html += "<td width='21' align='center'><div class='jm_icoCon' style='background-position:-545px -18px;float:right;' title='Eliminar' onclick=\"_R('rRe"+i+"');inv_sumRetTotales();\">&nbsp;</div></td>";
                        html += "<td width='21' align='center'>&nbsp;</td>";
                        html += "</tr>";
                        i++;
                        emitido_ret = true;
                    }
                    retencionDetalle.close();
                }catch(Exception e){
                    e.printStackTrace();
                }

                out.print(html);


                out.println("</table></div>"+
                        "<table cellpadding='0' cellspacing='0'>" +
                        "<tr><td colspan='2' width='454' align='right'>TOTAL RETENCI&Oacute;N: </td>" +
                        "<td align='right'> &nbsp;<input type='text' id='tvr' name='tvr' value='"+ret_impuesto_retenido+"' style='text-align:right' onkeypress=\"_evaluar(event, '0123456789.');\" /></td>" +
                        "</tr></table>");

                try{
                    rsTablaImpuestos.close();
                }catch(Exception e){
                    e.printStackTrace();
                }
            }
            out.print("</div>");





            /*           NOTAS DE CREDITO                */


/*
            out.println("<div id='tab12' class='jm_tabHTML' style='display:none;width:760px;height:"+_altBody+"px;'>");

            if(idNotaVenta.compareTo("-1")!=0){
                NotaCreditoCompra objNotaCredito = new NotaCreditoCompra(this._ip, this._puerto, this._db, usuario, clave);


                String nc_serie_nota = "001-001";
                String nc_num_nota = "";
                String nc_autorizacion_nota = "";
                //String nc_ci = ruc;
                //String nc_razon_social = razon_social;
                String nc_fecha_emision = Fecha.getFecha("SQL");
                String nc_modifica = "NotaVenta No. "+serie_NotaVenta+"-"+num_NotaVenta;
                String nc_concepto = "Devolución de ";
                String nc_tipo = "v";
                String nc_valor = "0";
                String nc_desc = "0";
                String nc_iva_0 = "0";
                String nc_iva_12 = "0";
                String nc_ir = "0";
                String nc_id_plan_cuenta_ir = "58";
                String nc_total = "0";
                try{
                    ResultSet rsNotaCredito = objNotaCredito.getNotaCreditoNotaVenta(idNotaVenta);
                    if(rsNotaCredito.next()){
                        id_nota_credito_compra = ((rsNotaCredito.getString("id_nota_credito_compra")!=null) ? rsNotaCredito.getString("id_nota_credito_compra") : "");
                        nc_num_nota = ((rsNotaCredito.getString("num_nota")!=null) ? rsNotaCredito.getString("num_nota") : "");
                        //nc_ci = ((rsNotaCredito.getString("ruc")!=null) ? rsNotaCredito.getString("ruc") : "");
                        //nc_razon_social = ((rsNotaCredito.getString("razon_social")!=null) ? rsNotaCredito.getString("razon_social") : "");
                        nc_autorizacion_nota = ((rsNotaCredito.getString("autorizacion_nota")!=null) ? rsNotaCredito.getString("autorizacion_nota") : "");
                        nc_fecha_emision = ((rsNotaCredito.getString("fecha_emision")!=null) ? Fecha.ISOaSQL(rsNotaCredito.getString("fecha_emision")) : "");
                        nc_modifica = ((rsNotaCredito.getString("documento_modifica")!=null) ? rsNotaCredito.getString("documento_modifica") : "");
                        nc_concepto = ((rsNotaCredito.getString("concepto")!=null) ? rsNotaCredito.getString("concepto") : "");
                        nc_tipo = ((rsNotaCredito.getString("tipo")!=null) ? rsNotaCredito.getString("tipo") : "");
                        nc_valor = ((rsNotaCredito.getString("valor")!=null) ? rsNotaCredito.getString("valor") : "0.00");
                        nc_desc = ((rsNotaCredito.getString("descuento")!=null) ? rsNotaCredito.getString("descuento") : "0.00");
                        nc_iva_0 = ((rsNotaCredito.getString("iva_0")!=null) ? rsNotaCredito.getString("iva_0") : "0.00");
                        nc_iva_12 = ((rsNotaCredito.getString("iva_12")!=null) ? rsNotaCredito.getString("iva_12") : "0.00");
                        nc_total = ((rsNotaCredito.getString("total")!=null) ? rsNotaCredito.getString("total") : "0.00");
                        nc_ir = ((rsNotaCredito.getString("ir")!=null) ? rsNotaCredito.getString("ir") : "0.00");
                        nc_id_plan_cuenta_ir = ((rsNotaCredito.getString("id_plan_cuenta_ir")!=null) ? rsNotaCredito.getString("id_plan_cuenta_ir") : "");
                        rsNotaCredito.close();
                    }
                }catch(Exception ie){
                    ie.printStackTrace();
                }


                out.println("<input type='hidden' id='nctope' name='nctope' value='0' />"
                        + "<table width='740' cellpadding='0' cellspacing='0'>" +
                    "<tr><td width='550'></td><td colspan='2'></td></tr>"+
                    "<tr><td align='center'> N O T A &nbsp;&nbsp; D E &nbsp;&nbsp; C R &Eacute; D I T O</td>" +
                    "<td align='right'> Nro. <input type='text' id='ncSerie' name='ncSerie' size='6' maxlength='7' value='"+nc_serie_nota+"' onkeypress=\"_evaluar(event, '0123456789-');\" />-" +
                    "<input type='text' size='6' id='ncNum' name='ncNum' value='"+nc_num_nota+"' onkeypress=\"_numero(event);\" /><br />"
                    + "Autorizacion: <input type='text' size='6' id='ncAut' name='ncAut' value='"+nc_autorizacion_nota+"' onkeypress=\"_numero(event);\" /></td>"+
                    "</tr></table>" +

                    "<table width='740' cellpadding='0' cellspacing='0'>"+

                    "<tr><td>Fecha de emisi&oacute;n: </td>"+
                    "<td><input type='text' size='10' maxlength='10' id='ncFeEm' name='ncFeEm' value='"+nc_fecha_emision+"' readonly onkeypress=\"_numero(event);\" /><input type='button' value='...' onClick=\"SelectorFecha.crear('ncFeEm', 'SQL');\" /></td>" +
                    "<td>Comprobante que modifica: </td>"+
                    "<td><input type='text' size='30' maxlength='50' id='ncCoMo' name='ncCoMo' value='"+nc_modifica+"' readonly onkeypress=\"_numero(event);\" /></td>" +
                    "</tr>"+

                    "<tr><td>Tipo de modificaci&oacute;n: <span class='marca'>*</span></td>"+
                    "<td colspan='3'><label><input type='radio' id='e0' name='tipo' value='v' "+(nc_tipo.compareTo("v")==0 ? "checked" : "")+" /> Devoluci&oacute;n</label> &nbsp; "+
                    "<label><input type='radio' id='e1' name='tipo' value='s' "+(nc_tipo.compareTo("s")==0 ? "checked" : "s")+" /> Descuento</label> &nbsp; "+
                    //"<label><input type='radio' id='e2' name='tipo' value='r' "+(nc_tipo.compareTo("r")==0 ? "checked" : "r")+" /> Error en mayor valor en la NotaVenta</label></td>"+
                    "</tr>");


                out.println("<br /><table cellpadding='0' cellspacing='0'>"+
                    "<TH class='jm_TH' width='20'>&nbsp;&nbsp;</TH>" +
                    "<TH class='jm_TH' width='100'>C&Oacute;DIGO</TH>" +
                    "<TH class='jm_TH' width='225'>DESCRIPCI&Oacute;N</TH>" +
                    "<TH class='jm_TH' width='40'>CANT.</TH>" +
                    "<TH class='jm_TH' width='60'>P./U.</TH>" +
                    "<TH class='jm_TH' width='70'>SUBTOT.</TH>" +
                    "<TH class='jm_TH' width='60'>DESC.</TH>" +
                    "<TH class='jm_TH' width='60'>IVA</TH>" +
                    "<TH class='jm_TH' width='80'>TOTAL</TH>" +
                    "</tr></table>" +

                    "<DIV id='sll0' style='overflow:auto;width:760px;height:145px;'>" +
                    "<TABLE class='jm_tabla' cellspacing='1' cellpadding='0'><tbody id='nctblD'>");

                html="";
                try{
                    String clase = "";
                    if(id_nota_credito_compra.compareTo("")==0){
                        String cantidad = "0";
                        String ncPu = "0";
                        String ncST = "0";
                        String ncDes = "0";
                        String ncIva = "0";
                        String ncT = "0";
                        NotaVentaDetalle.beforeFirst();
                        i=0;
                        html = "";
                        while(NotaVentaDetalle.next()){
                            cantidad = ((NotaVentaDetalle.getString("cantidad")!=null) ? NotaVentaDetalle.getString("cantidad") : "0");
                            ncPu = ((NotaVentaDetalle.getString("p_u")!=null) ? NotaVentaDetalle.getString("p_u") : "0");
                            ncST = ((NotaVentaDetalle.getString("p_st")!=null) ? NotaVentaDetalle.getString("p_st") : "0");
                            ncDes = ((NotaVentaDetalle.getString("descuento")!=null) ? NotaVentaDetalle.getString("descuento") : "0");
                            ncIva = ((NotaVentaDetalle.getString("iva")!=null) ? NotaVentaDetalle.getString("iva") : "0");
                            ncT = ((NotaVentaDetalle.getString("total")!=null) ? NotaVentaDetalle.getString("total") : "0");
                            html += "<tr id='ncr"+i+"' name='ncr"+i+"' class='jm_filaPar'>";
                            html += "<td width='21' align='center'><input type='checkbox' id='chProd"+i+"' name='chProd"+i+"' onclick=\"fac_setDetNotaCredito()\" />"+
                            "<input type='hidden' id='idFacVenDet"+i+"' name='idFacVenDet"+i+"' value='"+((NotaVentaDetalle.getString("id_NotaVenta_compra_detalle")!=null) ? NotaVentaDetalle.getString("id_NotaVenta_compra_detalle") : "")+"' />"
                                    + "<input type='hidden' id='axNcC"+i+"' value='"+cantidad+"' />"
                                    + "<input type='hidden' id='axncST"+i+"' value='"+ncST+"' />"
                                    + "<input type='hidden' id='axncDes"+i+"' value='"+ncDes+"' />"
                                    + "<input type='hidden' id='axncIva"+i+"' value='"+ncIva+"' />"
                                    + "<input type='hidden' id='axncT"+i+"' value='"+ncT+"' /></td>";
                            html += "<td width='100' id='cF"+i+"0'>"+((NotaVentaDetalle.getString("codigo")!=null) ? NotaVentaDetalle.getString("codigo") : "")+"</td>";
                            html += "<td width='226'>"+((NotaVentaDetalle.getString("descripcion")!=null) ? NotaVentaDetalle.getString("descripcion") : "")+"</td>";
                            html += "<td width='50' align='right'><input id='ncC"+i+"' name='ncC"+i+"' type='text' class='inpTbl' size='3' onkeypress=\"_evaluar(event, '0123456789.');\" "
                             + "onkeyup=\"fac_setDetNotaCredito();\" onfocus=\"this.select();\" value='"+cantidad+"' /></td>";
                             html += "<td width='61' align='right'><input id='ncPu"+i+"' name='ncPu"+i+"' type='text' class='inpTbl' size='5' readonly " +
                            "onfocus=\"this.select();\" value='"+ncPu+"' /></td>";
                            html += "<td width='71' align='right'><input id='ncST"+i+"' name='ncST"+i+"' type='text' class='inpTbl' size='5' onkeypress=\"_evaluar(event, '0123456789.');\" " +
                                    "onfocus=\"this.select();\" value='"+ncST+"' /></td>";
                            html += "<td width='61' align='right'><input id='ncDes"+i+"' name='ncDes"+i+"' type='text' class='inpTbl' size='5' onkeypress=\"_evaluar(event, '0123456789.');\" " +
                                    "onfocus=\"this.select();\" value='"+ncDes+"' /></td>";
                            html += "<td width='61' align='right'><input id='ncIv"+i+"' name='ncIv"+i+"' type='text' class='inpTbl' size='5' onkeypress=\"_evaluar(event, '0123456789.');\" " +
                                    "onfocus=\"this.select();\" value='"+ncIva+"' /></td>";
                            html += "<td width='81' align='right'><input id='ncT"+i+"' name='ncT"+i+"' type='text' class='inpTbl' size='6' onkeypress=\"_evaluar(event, '0123456789.');\" " +
                                    "onfocus=\"this.select();\" value='"+ncT+"' /></td>";
                            html += "</tr>";
                            i++;
                        }
                        NotaVentaDetalle.close();
                    }else{
                        ResultSet rsNotaCreditoDetalle = objNotaCredito.getNotaCreditoNotaVentaDetalle(id_nota_credito_compra);
                        while(rsNotaCreditoDetalle.next()){
                            clase = i%2==0 ? "jm_filaPar" : "jm_filaImp";
                            html += "<tr id='ncr"+i+"' class='"+clase+"'>";
                            html += "<td width='21' align='center'></td>";
                            html += "<td width='100' id='cF"+i+"0'>"+((rsNotaCreditoDetalle.getString("codigo")!=null) ? rsNotaCreditoDetalle.getString("codigo") : "")+"</td>";
                            html += "<td width='226'>"+((rsNotaCreditoDetalle.getString("descripcion")!=null) ? rsNotaCreditoDetalle.getString("descripcion") : "")+"</td>";
                            html += "<td width='41'>"+((rsNotaCreditoDetalle.getString("cantidad")!=null) ? rsNotaCreditoDetalle.getString("cantidad") : "")+"</td>";
                            html += "<td width='61' align='right'>"+((rsNotaCreditoDetalle.getString("p_u")!=null) ? rsNotaCreditoDetalle.getString("p_u") : "")+"</td>";
                            html += "<td width='71' align='right' id=\"ncST"+i+"\">"+((rsNotaCreditoDetalle.getString("p_st")!=null) ? rsNotaCreditoDetalle.getString("p_st") : "")+"</td>";
                            html += "<td width='61' align='right' id=\"ncDes"+i+"\">"+((rsNotaCreditoDetalle.getString("descuento")!=null) ? rsNotaCreditoDetalle.getString("descuento") : "")+"</td>";
                            html += "<td width='61' align='right' id=\"ncIva"+i+"\">"+((rsNotaCreditoDetalle.getString("iva")!=null) ? rsNotaCreditoDetalle.getString("iva") : "")+"</td>";
                            html += "<td width='81' align='right' id=\"ncT"+i+"\">"+((rsNotaCreditoDetalle.getString("total")!=null) ? rsNotaCreditoDetalle.getString("total") : "")+"</td>";
                            html += "</tr>";
                            i++;
                        }
                        rsNotaCreditoDetalle.close();
                    }
                }catch(Exception e){
                    e.printStackTrace();
                }

                out.print(html);
                out.print("</tbody></table></div>");


                out.print("<table cellpadding='0' cellspacing='0'>" +
                    "<TH class='jm_TH' width='590'>RAZON DE LA MODIFICACION</TH>" +
                    "<TH class='jm_TH' width='120'>VALOR DE LA MODIFICACION</TH></tr>"+
                    "</table>"+

                    "<TABLE class='jm_tabla' cellspacing='0' cellpadding='0'>"+
                    "<tr class='jm_filaPar' align='right'><td width='590'><textarea id='ncRaMo' name='ncRaMo' style=\"width:585px;height:17px\" onkeypress=\"_alfanumerico(event);\">"+nc_concepto+"</textarea></td>" +
                    "<td width='120'><input type='text' style='text-align:right' size='13' maxlength='50' id='ncVaMo' name='ncVaMo' value='"+nc_valor+"' onkeypress=\"_evaluar(event, '0123456789.');\" /></td></tr>"+

                    "<tr class='jm_filaPar' align='right'><td>DESCUENTO%</td>" +
                    "<td><input type='text' style='text-align:right' size='13' maxlength='50' id='ncDesc' name='ncDesc' value='"+nc_desc+"' onkeypress=\"_evaluar(event, '0123456789.');\" /></td></tr>"+

                    "<tr class='jm_filaPar' align='right'><td>IVA 12%</td>" +
                    "<td><input type='text' style='text-align:right' size='13' maxlength='50' id='ncIva12' name='ncIva12' value='"+nc_iva_12+"' onkeypress=\"_evaluar(event, '0123456789.');\" /></td></tr>"+

                    "<tr class='jm_filaPar' align='right'><td>IVA 0%</td>" +
                    "<td><input type='text' style='text-align:right' size='13' maxlength='50' id='ncIva0' name='ncIva0' value='"+nc_iva_0+"' onkeypress=\"_evaluar(event, '0123456789.');\" /></td></tr>"+

                    "<tr class='jm_filaPar' align='right'><td>VALOR TOTAL</td>" +
                    "<td><input type='text' style='text-align:right' size='13' maxlength='50' id='ncTotal' name='ncTotal' value='"+nc_total+"' onkeypress=\"_evaluar(event, '0123456789.');\" /></td></tr>"+

                    "<tr class='jm_filaPar' align='right'><td>PROPORCIONAL CORRESPONDIENTE DE LA RETENCION DEL IR</td>" +
                    "<td><input type='text' style='text-align:right' size='13' maxlength='50' id='ncIr' name='ncIr' value='"+nc_ir+"' onkeypress=\"_evaluar(event, '0123456789.');\" onkeyup=\"fac_setDetNotaCredito()\" /></td></tr>"+

                    "<tr class='jm_filaPar' align='right'><td colspan='2'>CUENTA CONTABLE DEL IR: " +
                    DatosDinamicos.combo(cuentas, "ncIdPLIR", nc_id_plan_cuenta_ir, "", 300)+"</td></tr>");

                    if(!anulado && id_nota_credito_compra.compareTo("")==0){
                        out.print("<tr class='jm_filaPar'><td colspan='2' align='right'><input type='button' value='Guardar nota de cr&eacute;dito' onclick=\"inv_notaCredito(_('frmCompra'))\" /></td></tr>");
                    }

                    out.print("</table>");

            }

            out.print("</div>");
*/


            /*           NOTAS DE DEBITO                */


/*
            out.println("<div id='tab13' class='jm_tabHTML' style='display:none;width:760px;height:"+_altBody+"px;'>");

            if(idNotaVenta.compareTo("-1")!=0){
                NotaDebitoCompra objNotaDebito = new NotaDebitoCompra(this._ip, this._puerto, this._db, usuario, clave);

                String id_nota_debito_compra = "";
                String nd_serie_nota = "";
                String nd_num_nota = "";
                String nd_autorizacion_nota = "";
                String nd_fecha_emision = Fecha.getFecha("SQL");
                String nd_modifica = "NotaVenta No. "+serie_NotaVenta+"-"+num_NotaVenta;
                String nd_concepto = "Nota de débito por ";
                String nd_valor = "0";
                String nd_iva_0 = "0";
                String nd_iva_12 = "0";
                String nd_total = "0";
                try{
                    ResultSet rsNotaDebito = objNotaDebito.getNotaDebitoNotaVenta(idNotaVenta);
                    if(rsNotaDebito.next()){
                        id_nota_debito_compra = ((rsNotaDebito.getString("id_nota_debito_compra")!=null) ? rsNotaDebito.getString("id_nota_debito_compra") : "");
                        nd_serie_nota = ((rsNotaDebito.getString("serie_nota")!=null) ? rsNotaDebito.getString("serie_nota") : "");
                        nd_num_nota = ((rsNotaDebito.getString("num_nota")!=null) ? rsNotaDebito.getString("num_nota") : "0");
                        //nd_ci = ((rsNotaDebito.getString("ruc")!=null) ? rsNotaDebito.getString("ruc") : "");
                        //nd_razon_social = ((rsNotaDebito.getString("razon_social")!=null) ? rsNotaDebito.getString("razon_social") : "");
                        nd_autorizacion_nota = ((rsNotaDebito.getString("autorizacion_nota")!=null) ? rsNotaDebito.getString("autorizacion_nota") : "");
                        nd_fecha_emision = ((rsNotaDebito.getString("fecha_emision")!=null) ? Fecha.ISOaSQL(rsNotaDebito.getString("fecha_emision")) : "");
                        nd_modifica = ((rsNotaDebito.getString("documento_modifica")!=null) ? rsNotaDebito.getString("documento_modifica") : "");
                        nd_concepto = ((rsNotaDebito.getString("concepto")!=null) ? rsNotaDebito.getString("concepto") : "");
                        nd_valor = ((rsNotaDebito.getString("valor")!=null) ? rsNotaDebito.getString("valor") : "0.00");
                        nd_iva_0 = ((rsNotaDebito.getString("iva_0")!=null) ? rsNotaDebito.getString("iva_0") : "0.00");
                        nd_iva_12 = ((rsNotaDebito.getString("iva_12")!=null) ? rsNotaDebito.getString("iva_12") : "0.00");
                        nd_total = ((rsNotaDebito.getString("total")!=null) ? rsNotaDebito.getString("total") : "0.00");
                        rsNotaDebito.close();
                    }
                }catch(Exception ie){
                    ie.printStackTrace();
                }


                out.println("<input type='hidden' id='ndson' name='ndson' value='' />"
                        + "<input type='hidden' id='ndtope' name='ndtope' value='0' />"+
                    "<table width='740' cellpadding='0' cellspacing='0'>" +
                    "<tr><td width='550'></td><td colspan='2'></td></tr>"+
                    "<tr><td align='center'> N O T A &nbsp;&nbsp; D E &nbsp;&nbsp; D &Eacute; B I T O</td>" +
                    "<td align='right'> Nro. <input type='text' id='ndSerie' name='ndSerie' size='6' maxlength='7' value='"+nd_serie_nota+"' onkeypress=\"_evaluar(event, '0123456789-');\" />-" +
                    "<input type='text' size='6' id='ndNum' name='ndNum' value='"+nd_num_nota+"' onkeypress=\"_numero(event);\" /><br />"
                    + "Autorizacion: <input type='text' size='6' id='ndAut' name='ndAut' value='"+nd_autorizacion_nota+"' onkeypress=\"_numero(event);\" /></td>"+
                    "</tr></table>" +

                    "<table width='740' cellpadding='0' cellspacing='0'>"+
                    "<tr><td>Fecha de emisi&oacute;n: </td>"+
                    "<td><input type='text' size='10' maxlength='10' id='ndFeEm' name='ndFeEm' value='"+nd_fecha_emision+"' readonly onkeypress=\"_numero(event);\" /><input type='button' value='...' onClick=\"SelectorFecha.crear('ndFeEm', 'SQL');\" /></td>" +
                    "<td>Comprobante que modifica: </td>"+
                    "<td><input type='text' size='30' maxlength='50' id='ndCoMo' name='ndCoMo' value='"+nd_modifica+"' readonly onkeypress=\"_numero(event);\" /></td>" +
                    "</tr>"+


                    "<tr><td colspan='4'>"+
                    "<a href='javascript:void(0);' style='float:left;' onclick=\"inv_addProducto('nd');\">Insertar Producto</a>" +
                    "</td>" +
                    "</tr>");

            out.print("</table>" +


                    "<table cellpadding='0' cellspacing='0'>" +
                    //"<TH class='jm_TH' width='100'></TH>" +
                    "<TH class='jm_TH' width='230'>C&Oacute;DIGO - DESCRIPCI&Oacute;N</TH>" +
                    "<TH class='jm_TH' width='50'>CANT.</TH>" +
                    "<TH class='jm_TH' width='80'>P./U.</TH>" +
                    "<TH class='jm_TH' width='80'>SUBTOT.</TH>" +
                    "<TH class='jm_TH' width='70'>DESC.</TH>" +
                    "<TH class='jm_TH' width='70'>IVA</TH>" +
                    "<TH class='jm_TH' width='90'>TOTAL</TH>" +
                    "<TH class='jm_TH' width='20'>&nbsp;&nbsp;</TH></tr></table>" +

                    "<DIV id='ndsll0' style='overflow:auto;width:760px;height:120px;'>" +
                    "<TABLE class='jm_tabla' cellspacing='1' cellpadding='0'><tbody id='ndtblD'>");

                html="";
                try{
                    ResultSet rsNotaDebitoDetalle = objNotaDebito.getNotaDebitoDetalle(id_nota_debito_compra);
                    String clase = "";
                    i=0;
                    while(rsNotaDebitoDetalle.next()){
                        clase = i%2==0 ? "jm_filaPar" : "jm_filaImp";
                        html += "<tr id='ndr"+i+"' class='"+clase+"'>";
                        //html += "<td width='100' id='ndcF"+i+"0'>"+((rsNotaDebitoDetalle.getString("codigo")!=null) ? rsNotaDebitoDetalle.getString("codigo") : "")+"</td>";
                        html += "<td width='230'>"+((rsNotaDebitoDetalle.getString("codigo")!=null) ? rsNotaDebitoDetalle.getString("codigo") : "") + " " +
                                ((rsNotaDebitoDetalle.getString("descripcion")!=null) ? rsNotaDebitoDetalle.getString("descripcion") : "")+"</td>";
                        html += "<td width='51'>"+((rsNotaDebitoDetalle.getString("cantidad")!=null) ? rsNotaDebitoDetalle.getString("cantidad") : "")+"</td>";
                        html += "<td width='81' align='right'>"+((rsNotaDebitoDetalle.getString("p_u")!=null) ? rsNotaDebitoDetalle.getString("p_u") : "")+"</td>";
                        html += "<td width='81' align='right'>"+((rsNotaDebitoDetalle.getString("p_st")!=null) ? rsNotaDebitoDetalle.getString("p_st") : "")+"</td>";
                        html += "<td width='71' align='right'>"+((rsNotaDebitoDetalle.getString("descuento")!=null) ? rsNotaDebitoDetalle.getString("descuento") : "")+"</td>";
                        html += "<td width='71' align='right'>"+((rsNotaDebitoDetalle.getString("iva")!=null) ? rsNotaDebitoDetalle.getString("iva") : "")+"</td>";
                        html += "<td width='91' align='right'>"+((rsNotaDebitoDetalle.getString("total")!=null) ? rsNotaDebitoDetalle.getString("total") : "")+"</td>";
                        html += "<td width='21' align='center'>&nbsp;</td>";
                        html += "</tr>";
                        i++;
                    }
                    rsNotaDebitoDetalle.close();
                }catch(Exception e){
                    e.printStackTrace();
                }

                out.print(html);

                out.print("</tbody></table></div>");


                out.print("<table cellpadding='0' cellspacing='0'>" +
                "<TH class='jm_TH' width='590'>RAZON DE LA MODIFICACION</TH>" +
                "<TH class='jm_TH' width='120'>VALOR DE LA MODIFICACION</TH></tr>"+
                "</table>"+

                "<TABLE class='jm_tabla' cellspacing='0' cellpadding='0'>"+
                "<tr class='jm_filaPar' align='right'><td  width='590'><textarea id='ndRaMo' name='ndRaMo' style=\"width:585px;height:17px\" onkeypress=\"_alfanumerico(event);\">"+nd_concepto+"</textarea></td>" +
                "<td width='120'><input type='text' style='text-align:right' size='13' maxlength='50' id='ndst' name='ndVaMo' value='"+nd_valor+"' onkeypress=\"_evaluar(event, '0123456789.');\" /></td></tr>"+

                "<tr class='jm_filaPar' align='right'><td>IVA 12%</td>" +
                "<td><input type='text' style='text-align:right' size='13' maxlength='50' id='ndiv12' name='ndiv12' value='"+nd_iva_12+"' onkeypress=\"_evaluar(event, '0123456789.');\" /></td></tr>"+

                "<tr class='jm_filaPar' align='right'><td>IVA 0%</td>" +
                "<td><input type='text' style='text-align:right' size='13' maxlength='50' id='ndiv0' name='ndiv0' value='"+nd_iva_0+"' onkeypress=\"_evaluar(event, '0123456789.');\" /></td></tr>"+

                "<tr class='jm_filaPar' align='right'><td>VALOR TOTAL</td>" +
                "<td><input type='text' style='text-align:right' size='13' maxlength='50' id='ndtt' name='ndtt' value='"+nd_total+"' onkeypress=\"_evaluar(event, '0123456789.');\" /></td></tr>");

                if(!anulado && id_nota_debito_compra.compareTo("")==0){
                    out.print("<tr class='jm_filaPar'><td colspan='2' align='right'><input type='button' value='Guardar nota de d&eacute;bito' onclick=\"fac_notaDebito(_('frmCompra'))\" /></td></tr>");
                }
                out.print("</table>");
            }

            out.print("</div>");
*/




            out.println("<table class='jm_tabla' width='100%' cellspacing='0' cellpadding='0'><tr>");
            if(!anulado){
                if(id_nota_credito_compra.compareTo("")==0){
                    long fecha_sistema = Fecha.getTimeStamp(Fecha.getFecha("SQL"));
                    long fecha_asiento = Fecha.getTimeStamp(fecha);
                    if(idNotaVenta.compareTo("-1")!=0 && objRol.getAcceso(id_rol, "compRetCompImprimir") && emitido_ret){
                        out.println("<td><input type='button' value='Imprimir Retenci&oacute;n' onclick=\"imprimir('pdfComprobanteRetencion?id="+id_retencion_compra+"&doc=v');\" /></td>");
                    }
                    /*if(idNotaVenta.compareTo("-1")!=0 && (objRol.getAcceso(id_rol, "compRetCompAnular") || objRol.getAcceso(id_rol, "importaFactRetAnular")) && emitido_ret && fecha_sistema==fecha_asiento){
                        out.println("<td align='right'><input type='button' value='Anular Retenci&oacute;n' onclick='inv_retencionAnular("+idNotaVenta+", "+id_retencion_compra+");' /></td>");
                    }*/
                    if(idNotaVenta.compareTo("-1")!=0 && (objRol.getAcceso(id_rol, "compraAnular") || objRol.getAcceso(id_rol, "importaFactAnular")) && fecha_sistema==fecha_asiento){
                        out.println("<td><input type='button' value='Anular NotaVenta' onclick='inv_nota_ventaAnular("+idNotaVenta+");' /></td>");
                    }
                    /*if(!objNotaVenta.hayPagos(idNotaVenta) && (idNotaVenta.compareTo("-1")==0 ||
                      (idNotaVenta.compareTo("-1")!=0 && (objRol.getAcceso(id_rol, "compraEditar") || objRol.getAcceso(id_rol, "importaFactEditar"))))){*/
                    if(idNotaVenta.compareTo("-1")==0){
                        out.println("<td align='right'><input type='submit' value='Guardar' onclick=\"_('gr').value=0;\" /></td>");
                        out.println("<td align='right'><input type='submit' value='Guardar e Imprimir Comprobante de Diario' onclick=\"_('gr').value=1;\" /></td>");
                    }
                }else{
                    out.println("<td><span class='marca'>NotaVenta MODIFICADA POR NOTA DE CREDITO</span></td>");
                }
            }else{
                out.println("<td><span class='marca'>NotaVenta DE COMPRA ANULADA</span></td>");
            }
            out.print("</tr></table>");



            out.print("</div></form></div>");

            try{
                rsProveedores.close();
            }catch(Exception e){
                e.printStackTrace();
            }
        } finally {
            objBaseImponible.cerrar();
            objProducto.cerrar();
            objProveedor.cerrar();
            objTablaImpuesto.cerrar();
            objRol.cerrar();
            objNotaVenta.cerrar();
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
