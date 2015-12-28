/**
* @version 4.0
* @package YAKUSOFT.
* @author Jorge Washington Mueses Cevallos.
* @copyright Copyright (C) 2011 por Jorge Mueses. Todos los derechos reservados.
* @license http://www.gnu.org/copyleft/gpl.html GNU/GPL.
* YAKUSOFT es un software de libre distribuciÃ³n, que puede ser
* copiado y distribuido bajo los tÃ©rminos de la Licencia
* Attribution-NonCommercial-NoDerivs 3.0 Unported,
* de acuerdo con la publicada por la CREATIVE COMMONS CORPORATION.
*/

package jm.inv;

import java.io.*;

import javax.servlet.*;
import javax.servlet.http.*;
import java.sql.ResultSet;
import jm.web.DatosDinamicos;
import jm.web.Fecha;
import jm.seg.clas.Rol;
import jm.adm.clas.Sucursal;
import jm.adm.clas.TablaImpuesto;
import jm.inv.clas.BaseImponible;
import jm.inv.clas.FacturaCompra;
import jm.inv.clas.Producto;
import jm.inv.clas.LiquidacionCompra;
import jm.inv.clas.Proveedor;

/**
 *
 * @author Jorge
 */
public class frmLiquidacionCompra extends HttpServlet {
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
        
       String id_sucursal = String.valueOf(id_sucursal_sesion);

        response.setContentType("text/html;charset=UTF-8");
        response.setHeader("Pragma", "no-cache");
        response.setHeader("Expires", "Mon, 01 Jan 2001 00:00:01 GMT");
        response.setHeader("Cache-Control", "no-store");
        response.setHeader("Cache-Control", "must-revalidate");
        response.setHeader("Cache-Control", "no-cache");
        PrintWriter out = response.getWriter();

        String id = request.getParameter("id");
        String _altBody = "380";

        LiquidacionCompra objLiquidacionCompra = new LiquidacionCompra(this._ip, this._puerto, this._db, usuario, clave);

        Rol objRol = new Rol(this._ip, this._puerto, this._db, usuario, clave);

        //Configuracion conf = new Configuracion(this._ip, this._puerto, this._db, usuario, clave);
        //String p_iva1 = conf.getValor("p_iva1");

        String suc_ret_num_serie = "";
        String suc_ret_sec_desde = "0";
        String suc_ret_sec_hasta = "0";
        String suc_ret_cad_libretin = "";
        String suc_ret_autorizacion = "";
        
        String serie_liquidacion = "";
        String liq_sec_desde = "0";
        String liq_sec_hasta = "0";
        String liq_cad_libretin = "";
        String liq_autorizacion = "";
        Sucursal objSucursal = new Sucursal(this._ip, this._puerto, this._db, usuario, clave);
        try{
            ResultSet rs = objSucursal.getSucursal(id_sucursal);
            if(rs.next()){
                suc_ret_num_serie = (rs.getString("ret_num_serie")!=null) ? rs.getString("ret_num_serie") : suc_ret_num_serie;
                suc_ret_sec_desde = (rs.getString("ret_sec_desde")!=null) ? rs.getString("ret_sec_desde") : "0";
                suc_ret_sec_hasta = (rs.getString("ret_sec_hasta")!=null) ? rs.getString("ret_sec_hasta") : "0";
                suc_ret_cad_libretin = (rs.getString("ret_cad_libretin")!=null) ? rs.getString("ret_cad_libretin") : "";
                suc_ret_autorizacion = (rs.getString("ret_autorizacion")!=null) ? rs.getString("ret_autorizacion") : suc_ret_autorizacion;
                
                serie_liquidacion = (rs.getString("liq_num_serie")!=null) ? rs.getString("liq_num_serie") : "";
                liq_sec_desde = (rs.getString("liq_sec_desde")!=null) ? rs.getString("liq_sec_desde") : "0";
                liq_sec_hasta = (rs.getString("liq_sec_hasta")!=null) ? rs.getString("liq_sec_hasta") : "0";
                liq_cad_libretin = (rs.getString("liq_cad_libretin")!=null) ? Fecha.ISOaSQL(rs.getString("liq_cad_libretin")) : "";
                liq_autorizacion = (rs.getString("liq_autorizacion")!=null) ? rs.getString("liq_autorizacion") : "";
                rs.close();
            }
        }catch(Exception e){
            e.printStackTrace();
        }finally{
            objSucursal.cerrar();
        }

        TablaImpuesto objTablaImpuesto = new TablaImpuesto(this._ip, this._puerto, this._db, usuario, clave);
        
        BaseImponible objBaseImponible = new BaseImponible(this._ip, this._puerto, this._db, usuario, clave);

        Proveedor objProveedor = new Proveedor(this._ip, this._puerto, this._db, usuario, clave);
        //ResultSet rsProveedores = objProveedor.getProveedores();

        Producto objProducto = new Producto(this._ip, this._puerto, this._db, usuario, clave);

        FacturaCompra objFactura = new FacturaCompra(this._ip, this._puerto, this._db, usuario, clave);

        try {
            int i=0;
            String id_sucursal_retencion = "0";
            String id_comprobante_diario = "";
            String id_proveedor = "-0";
            String razon_social = "";
            String autorizacion = liq_autorizacion;
            long num_liquidacion = objLiquidacionCompra.getNumLiquidacion(id_sucursal);
            String fecha_emision = Fecha.getFecha("SQL");
            String subtotal = "0";
            String subtotal_0 = "0";
            String subtotal_2 = "0";
            String iva_2 = "0";
            String total_compra = "0";
            String observacion = "Registro de liquidación de compra o servicio No. " + serie_liquidacion + "-" + num_liquidacion + " de";
            boolean anulado = false;

            String sustento_tributario = "";
            String id_retencion_compra = "-1";
            String ret_num_serie = suc_ret_num_serie;
            String clave_acceso = "";
            String ret_autorizacion = suc_ret_autorizacion;
            
            String ret_num_retencion = String.valueOf(objFactura.getNumRetencion(id_sucursal));
            if(id_sucursal_sesion==1 || id_sucursal_sesion==3){
                int ax_ret_num_retencion = objFactura.getNumRetencionSaltado(suc_ret_num_serie);
                ret_num_retencion = ax_ret_num_retencion>0 ? String.valueOf(ax_ret_num_retencion) : ret_num_retencion;
            }
            
            String ret_fecha_emision = "";
            int mes = Fecha.getMes();
            String ret_ejercicio_fiscal_mes = mes<10 ? "0" + mes : String.valueOf(mes);
            String ret_ejercicio_fiscal = String.valueOf(Fecha.getAnio());
            String ret_impuesto_retenido = "";
            boolean ret_anulado = false;
            boolean emitido_ret = false;
            String estado_documento = "";

            ResultSet facturaDetalle = null;
            String ocultar = "hidden";
            String lectura = "";
            if(id.compareTo("-1")!=0){
                lectura = "readonly";
                try{
                    ResultSet rsFactura = objLiquidacionCompra.getLiquidacion(id);
                    facturaDetalle = objLiquidacionCompra.getLiquidacionDetalle(id);
                    if(rsFactura.next()){
                        id_comprobante_diario = (rsFactura.getString("id_comprobante_diario")!=null) ? rsFactura.getString("id_comprobante_diario") : "";
                        id_proveedor = (rsFactura.getString("id_proveedor")!=null) ? rsFactura.getString("id_proveedor") : "-0";
                        razon_social = (rsFactura.getString("razon_social")!=null) ? rsFactura.getString("razon_social") : "";
                        serie_liquidacion = (rsFactura.getString("serie_liquidacion")!=null) ? rsFactura.getString("serie_liquidacion") : "";
                        autorizacion = (rsFactura.getString("autorizacion")!=null) ? rsFactura.getString("autorizacion") : "";
                        num_liquidacion = (rsFactura.getString("num_liquidacion")!=null) ? rsFactura.getInt("num_liquidacion") : 0;
                        fecha_emision = (rsFactura.getString("fecha_emision")!=null) ? Fecha.ISOaSQL(rsFactura.getString("fecha_emision")) : "";
                        subtotal = (rsFactura.getString("subtotal")!=null) ? rsFactura.getString("subtotal") : "0";
                        subtotal_0 = (rsFactura.getString("subtotal_0")!=null) ? rsFactura.getString("subtotal_0") : "0";
                        subtotal_2 = (rsFactura.getString("subtotal_2")!=null) ? rsFactura.getString("subtotal_2") : "0";
                        iva_2 = (rsFactura.getString("iva_2")!=null) ? rsFactura.getString("iva_2") : "0";
                        total_compra = (rsFactura.getString("total_compra")!=null) ? rsFactura.getString("total_compra") : "0";
                        observacion = (rsFactura.getString("observacion")!=null) ? rsFactura.getString("observacion") : "";
                        estado_documento = (rsFactura.getString("estado_documento")!=null) ? rsFactura.getString("estado_documento") : "";
                        anulado = (rsFactura.getString("anulado")!=null) ? rsFactura.getBoolean("anulado") : false;
                        rsFactura.close();
                    }
                }catch(Exception e){
                    e.printStackTrace();
                }

                try{
                    ResultSet rsRetencion = objLiquidacionCompra.getRetencion(id);
                    if(rsRetencion.next()){
                        ret_anulado = (rsRetencion.getString("anulado")!=null) ? rsRetencion.getBoolean("anulado") : false;
                        if(!ret_anulado){
                            id_sucursal_retencion = (rsRetencion.getString("id_sucursal")!=null) ? rsRetencion.getString("id_sucursal") : "0";
                            sustento_tributario = (rsRetencion.getString("sustento_tributario")!=null) ? rsRetencion.getString("sustento_tributario") : "";
                            id_retencion_compra = (rsRetencion.getString("id_retencion_compra")!=null) ? rsRetencion.getString("id_retencion_compra") : "-1";
                            ret_num_serie = (rsRetencion.getString("ret_num_serie")!=null) ? rsRetencion.getString("ret_num_serie") : "001-001";
                            clave_acceso = (rsRetencion.getString("clave_acceso")!=null) ? rsRetencion.getString("clave_acceso") : "";
                            ret_autorizacion = (rsRetencion.getString("numero_autorizacion")!=null) ? rsRetencion.getString("numero_autorizacion") : "";
                            ret_num_retencion = (rsRetencion.getString("ret_num_retencion")!=null) ? rsRetencion.getString("ret_num_retencion") : "";
                            ret_fecha_emision = (rsRetencion.getString("ret_fecha_emision")!=null) ? Fecha.ISOaSQL(rsRetencion.getString("ret_fecha_emision")) : Fecha.getFecha("ISO");
                            ret_ejercicio_fiscal = (rsRetencion.getString("ret_ejercicio_fiscal")!=null) ? rsRetencion.getString("ret_ejercicio_fiscal") : ret_ejercicio_fiscal;
                            ret_ejercicio_fiscal_mes = (rsRetencion.getString("ret_ejercicio_fiscal_mes")!=null) ? rsRetencion.getString("ret_ejercicio_fiscal_mes") : ret_ejercicio_fiscal_mes;
                            ret_impuesto_retenido = (rsRetencion.getString("ret_impuesto_retenido")!=null) ? rsRetencion.getString("ret_impuesto_retenido") : "";
                            estado_documento = (rsRetencion.getString("estado_documento")!=null) ? rsRetencion.getString("estado_documento") : "";
                        }
                        rsRetencion.close();
                    }
                }catch(Exception e){
                    e.printStackTrace();
                }

            }

            String [][] ret_ejercicio_fiscal_mes1 = {{"01","Enero"},{"02","Febrero"},{"03","Marzo"},{"04","Abril"},{"05","Mayo"},{"06","Junio"},
                {"07","Julio"},{"08","Agosto"},{"09","Septiembre"},{"10","Octubre"},{"11","Noviembre"},{"12","Diciembre"}};

            out.print("obj»vta1_html^foc»busProv^fun»_objTab.seleccionar(0);^frm»");

            out.print("<div class='grupoTab' style='width:750px;'><div id='tabR0'>"
                    + "<a id='tab00' href='javascript:void(0);' class='jm_tabAct' onclick='_objTab.seleccionar(0);'>Liquidaci&oacute;n</a>"
                    + "<a id='tab01' href='javascript:void(0);' class='jm_tab' onclick='_objTab.seleccionar(1);'>Retenci&oacute;n</a>");
            out.print("</div>");

            out.print("<form id='frmLicCom' action='frmLiquidacionCompraGuardar' onsubmit='return inv_liquidacionGuardar(this)' autocomplete='off'>");
            out.print("<input type='hidden' id='id' name='id' value='"+id+"' />"
                    + "<input type='hidden' id='id_sucursal_doc' name='id_sucursal_doc' value='"+id_sucursal_retencion+"' />"
                    + "<input type='hidden' id='idCD' name='idCD' value='"+id_comprobante_diario+"' />"
                    + "<input type='hidden' id='secDes' name='secDes' value='"+liq_sec_desde+"' />"
                    + "<input type='hidden' id='secHas' name='secHas' value='"+liq_sec_hasta+"' />"
                    + "<input type='hidden' id='feCad' name='feCad' value='"+liq_cad_libretin+"' />"
                    + "<input type='hidden' id='retSecDes' name='retSecDes' value='"+suc_ret_sec_desde+"' />"
                    + "<input type='hidden' id='retSecHas' name='retSecHas' value='"+suc_ret_sec_hasta+"' />"
                    + "<input type='hidden' id='retFeCad' name='retFeCad' value='"+suc_ret_cad_libretin+"' />"
                    + "<input type='hidden' id='fv' name='fv' value='0' />"
                    + "<input type='hidden' id='tope' name='tope' value='0' />"
                    + "<input type='hidden' id='coP' name='coP' value='"+id_proveedor+"' />"
                    //+ "<input type='hidden' id='p_iva1' name='p_iva1' value='"+p_iva1+"' />"
                    + "<input type='hidden' id='tpyDoc' value='l' />"
                    + "<input type='hidden' id='elect' name='elect' value='0' />"
                    + "<input type='hidden' id='idRC' name='idRC' value='"+id_retencion_compra+"' />"
                    + "<input type='hidden' id='imp' name='imp' value='1' />");

            out.print("<div id='tabR1'>");

            /* primer tab */

            out.print("<div id='tab10' class='jm_tabHTML' style='display:block;width:740px;height:"+_altBody+"px;'>");


            out.print("<div id='mSg0' style=\"display:none\" class='msg'>&nbsp;</div>" +
                    "<div style=\"display:none\" id='axP'>"+ objProveedor.getProveedorJSON() +"</div>" +
                    "<table width='670' cellpadding='0' cellspacing='0'>" +
                    "<tr><td colspan='2'></td><td colspan='2'><span id='axMsP'></span></td></tr>"+
                    "<tr><td align='right' colspan='2'>L I Q U I D A C I O N &nbsp;&nbsp; D E &nbsp;&nbsp; C O M P R A S &nbsp;&nbsp; O &nbsp;&nbsp; S E R V I C I O S</td>" +
                    "<td align='right'>Nro. <span class='marca'>*</span></td>" +
                    "<td><input type='text' id='sf' name='sf' size='6' maxlength='7' value='"+serie_liquidacion+"' "+lectura+" onkeypress=\"_evaluar(event, '0123456789-');\" onkeyup='inv_setDetalleCompra()' />-" +
                    "<input type='text' size='6' id='nf' name='nf' value='"+num_liquidacion+"' "+lectura+" onkeypress=\"_evaluar(event, '0123456789-');\" onkeyup='inv_setDetalleCompra()' /></td></tr>" +

                    "<tr><td>Proveedor: <span class='marca'>*</span></td>" +
                    "<td><div style='float:left;display:table-cell' id='aux_coP'>"+
                    "<input type='text' id='busProv' style='width:250px;' value='"+razon_social+"' "+lectura+" onfocus='this.select()' onkeyup=\"inv_getProveedores(this.value);\" /></div>");

            if(id.compareTo("-1")==0){
                out.print("<div style='float:left;display:table-cell'> &nbsp;&nbsp; </div>"
                    + "<div class='jm_icoCon' style='background-position:-291px -138px;' title='Nuevo Proveedor' onclick=\"inv_getFrmProveedor('l')\">&nbsp;</div>");
            
                out.print("<div style='float:left;display:table-cell'> &nbsp;&nbsp;&nbsp;&nbsp; </div>"
                    + "<div class='jm_icoCon' style='background-position:-314px -138px;' title='Nuevo Producto' onclick=\"inv_getFrmProducto()\">&nbsp;</div>");
            }

            out.print("</td>" +
                    "<td align='right'>Autorizaci&oacute;n: </td><td><input type='text' size='10' maxlength='10' id='auf' name='auf' value='"+autorizacion+"' "+lectura+" onkeypress=\"_numero(event);\" /></td>" +
                    "</tr>" +

                    "<tr><td>Fecha de compra: <span class='marca'>*</span> </td><td colspan='3'><input id='fem' name='fem' type='text' size='9' value='"+fecha_emision+"' readOnly />" +
                    "</td>" +


                    "<tr><td colspan='6'><div id='InAr'>B&uacute;squeda de productos o servicios: &nbsp; <input type='text' id='busProd' onfocus=\"this.value=''\" style='width:350px;border-color:red' onkeyup=\"inv_getProductos(event, this.value, 'inv_tblProductos');\" /></div>" +
                    "&nbsp;&nbsp;<div style=\"display:none\" id='axp'></div></td>" +
                    "<div style=\"display:none\" id='ax'></div></td>" +
                    "</tr>" +
                    "</table>" +


                    "<table cellpadding='0' cellspacing='0'><tr>" +
                    "<TH class='jm_TH' width='230'>C&Oacute;DIGO - DESCRIPCI&Oacute;N</TH>" +
                    "<TH class='jm_TH' width='50'>CANT.</TH>" +
                    "<TH class='jm_TH' width='80'>P./U.</TH>" +
                    "<TH class='jm_TH' width='80'>SUBTOT.</TH>" +
                    "<TH class='jm_TH' width='70'>IVA</TH>" +
                    "<TH class='jm_TH' width='90'>total_compra</TH>" +
                    "<TH class='jm_TH' width='20'>&nbsp;&nbsp;</TH></tr></table>" +

                    "<DIV id='sll0' style='overflow:auto;width:660px;height:160px;'>" +
                    "<TABLE class='jm_tabla' cellspacing='1' cellpadding='0' id='tblD'>");

            try{
                i=0;
                String html = "";
                while(facturaDetalle.next()){
                    String pi = (facturaDetalle.getString("porcentaje")!=null) ? facturaDetalle.getString("porcentaje") : "";
                    html += "<tr id='r"+i+"' class='jm_filaPar'>";
                    html += "<td style='width:230px'>"+((facturaDetalle.getString("codigo")!=null) ? facturaDetalle.getString("codigo") : "")+" &nbsp; "
                            + ""+((facturaDetalle.getString("descripcion")!=null) ? facturaDetalle.getString("descripcion") : "")+""
                            + "<input type='hidden' id='pi"+i+"' name='pi"+i+"' value='"+pi+"'/></td>";
                    html += "<td style='width:50px'><input id='c"+i+"' name='c"+i+"' type='text' class='inpTbl' style='width:48px' onkeypress=\"_evaluar(event, '0123456789.');\" onkeyup=\"inv_sumSubTotal('', "+i+");inv_sumTotales('');\" "+lectura+" " +
                            "onfocus=\"this.select();\" value='"+((facturaDetalle.getString("cantidad")!=null) ? facturaDetalle.getString("cantidad") : "")+"' /></td>";
                    html += "<td style='width:80px'><input id='pu"+i+"' name='pu"+i+"' type='text' class='inpTbl' style='width:78px' onkeypress=\"_evaluar(event, '0123456789.');\" onkeyup=\"inv_sumSubTotal('', "+i+");inv_sumTotales('');\" "+lectura+" " +
                            "onfocus=\"this.select();\" value='"+((facturaDetalle.getString("p_u")!=null) ? facturaDetalle.getString("p_u") : "")+"' /></td>";
                    html += "<td style='width:80px'><input id='st"+i+"' name='st"+i+"' type='text' class='inpTbl' style='width:78px' onkeypress=\"_evaluar(event, '0123456789.');\" onkeyup=\"inv_sumTotales('');\" "+lectura+" " +
                            "onfocus=\"this.select();\" value='"+((facturaDetalle.getString("p_st")!=null) ? facturaDetalle.getString("p_st") : "")+"' /></td>";
                    html += "<td style='width:70px'><input id='v"+i+"' name='v"+i+"' type='text' class='inpTbl' style='width:68px' onkeypress=\"_evaluar(event, '0123456789.');\" "+lectura+" " +
                            "onfocus=\"this.select();\" value='"+((facturaDetalle.getString("iva")!=null) ? facturaDetalle.getString("iva") : "")+"' /></td>";
                    html += "<td style='width:90px'><input id='t"+i+"' name='t"+i+"' type='text' class='inpTbl' style='width:88px' onkeypress=\"_evaluar(event, '0123456789.');\" "+lectura+" " +
                            "onfocus=\"this.select();\" value='"+((facturaDetalle.getString("total")!=null) ? facturaDetalle.getString("total") : "")+"' /></td>";
                    html += "<td style='width:20px'>&nbsp;</td>";
                    html += "</tr>";
                    i++;
                }

                out.print(html);

                facturaDetalle.close();
            }catch(Exception e){
                e.printStackTrace();
            }

            out.print("</table></div>" +
                    "<table cellpadding='0' cellspacing='0'>" +
                    "<tr><td width='365'> Concepto .- <span class='marca'>*</span></td>" +
                    "<td width='90'>SUBTOTAL: <span class='marca'>*</span> </td>" +
                    "<td align='right' width='150'> <input type='text' id='subt' name='subt' value='"+subtotal+"' "+lectura+" style='text-align:right' onkeypress=\"_evaluar(event, '0123456789.');\" onkeyup=\"inv_sumRettotal_compraes();\" /></td></tr>" +

                    "<tr><td rowspan='4' valign='top'><textarea id='ob' name='ob' cols='40' rows='2' "+lectura+">"+observacion+"</textarea></td>" +
                    "<td>SUBTOTAL 12%: </td>" +
                    "<td align='right' class='jm_filaPar'> <input type='text' id='subt2' name='subt2' value='"+subtotal_2+"' "+lectura+" style='text-align:right' onkeypress=\"_evaluar(event, '0123456789.');\" /></td></tr>" +

                    "<tr><td>SUBTOTAL 0%: </td>" +
                    "<td align='right' class='jm_filaPar'> <input type='text' id='subt0' name='subt0' value='"+subtotal_0+"' "+lectura+" style='text-align:right' onkeypress=\"_evaluar(event, '0123456789.');\" /></td></tr>" +

                    "<tr><td>12% IVA: </td>" +
                    "<td align='right' class='jm_filaPar'> <input type='text' id='iva2' name='iva2' value='"+iva_2+"' "+lectura+" style='text-align:right' onkeypress=\"_evaluar(event, '0123456789.');\" onkeyup=\"inv_sumRettotal_compraes();\" /></td></tr>" +

                    "<tr><td>TOTAL: <span class='marca'>*</span> </td>" +
                    "<td align='right' class='jm_filaPar'> <input type='text' id='tt' name='tt' value='"+total_compra+"' "+lectura+" style='text-align:right' onkeypress=\"_evaluar(event, '0123456789.');\" /></td></tr>" +

                    "</tr>");

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
            

            out.println("<div id='tab11' class='jm_tabHTML' style='display:none;width:740px;height:"+_altBody+"px;'>");


            //if((ret_num_retencion.compareTo("")==0 && objRol.getAcceso(id_rol, "compRetCompNueva")) ){
                String html = "";
                ResultSet rsBasesImponibles = objBaseImponible.getBasesImponibles("1,2,4,7,9");
                ResultSet rsTablaImpuestos = objTablaImpuesto.getTablaImpuestosVigente();

                out.println(
                        "<table width='730' cellpadding='0' cellspacing='0'>" +
                        
                        "<tr><td align='center'> C O M P R O B A N T E &nbsp;&nbsp; D E &nbsp;&nbsp; R E T E N C I &Oacute; N</td>" +
                        "<td align='right'> Nro. <input type='text' id='sr' name='sr' size='6' maxlength='7' value='"+ret_num_serie+"' onkeypress=\"_evaluar(event, '0123456789-');\" />-" +
                        "<input type='text' size='6' id='nr' name='nr' value='"+ret_num_retencion+"' onkeypress=\"_numero(event);\" /></td></tr>"
                        + "<tr><td colspan='2' align='right'>Clave Acceso: <input type='text' size='49' id='clAc' name='clAc' value='"+clave_acceso+"' readonly />"
                        + "<br />Autorizaci&oacute;n: <input type='text' size='49' id='autr' name='autr' value='"+ret_autorizacion+"' readonly /></td></tr>" +

                        "<tr><td>Fecha de Emisi&oacute;n: <input id='fre' name='fre' type='text' size='9' value='"+ret_fecha_emision+"' onkeypress=\"_SUP(event);\" />" +
                        "<input type='button' value='...' onClick=\"SelectorFecha.crear('fre', 'SQL');\" /></td>" +
                        "<td>Ejercicio Fiscal: " + DatosDinamicos.combo("efm", ret_ejercicio_fiscal_mes, ret_ejercicio_fiscal_mes1, "") + "<input id='ef' name='ef' type='text'size='4' maxlength='4' value='"+ret_ejercicio_fiscal+"' onkeypress=\"_numero(event)\" /></td>" +
                        "</tr>" +
                        "<tr><td colspan='2'>Sustento Tributario: " + DatosDinamicos.combo("sustento_tributario", sustento_tributario, sustento_tributario1, "") + "</td></tr>" +
                        "</table>" +

                        "<div id='InRe' style='visibility:"+(( id.compareTo("-1")!=0 && estado_documento.compareTo("n")==0) ? "visible" : "hidden")+"'>"
                        + "<a href='javascript:void(0);' style='float:left;' onclick=\"inv_addRetencion();\">Insertar Retenci&oacute;n</a></div>"
                        + "<div style=\"display:none\" id='axBI'>"+ DatosDinamicos.combo(rsBasesImponibles, "9a0", "9b0", "", "inv_sumRetSubTotales(9d0,1);inv_sumRetTotales();", "inv_sumRetSubTotales(9d0,1);inv_sumRetTotales();", "", 165) +"</div>" +
                        "&nbsp;&nbsp;<div style=\"display:none\" id='axCR'>"+ DatosDinamicos.combo(rsTablaImpuestos, "9a0", "9b0", "inv_setMsg('axMsR');", "inv_setConcepto(9c0);inv_sumRetSubTotales(9d0,1);inv_sumRetTotales();", "_('9e0').focus();", "", 295) +"</div>" +
                        "<div style=\"display:none\" id='axC'>"+ objTablaImpuesto.getConceptoRetencion() +"</div>" +
                        "<table cellpadding='0' cellspacing='0' width='740'>" +
                        "<TH class='jm_TH' width='170'>CAMPO BASE IMPONIBLE</TH>" +
                        "<TH class='jm_TH' width='300'>IMPUESTO - C&Oacute;DIGO - % DE RETENCI&Oacute;N</TH>" +
                        "<TH class='jm_TH' width='120'>BASE IMPONIBLE</TH>" +
                        "<TH class='jm_TH' width='120'>VALOR RETENIDO</TH>" +
                        "<TH class='jm_TH' width='20'>&nbsp;</TH></tr></table>" +

                        "<DIV id='sll1' style='overflow:auto;width:760px;height:105px;'>" +
                        "<TABLE class='jm_tabla' cellspacing='1' cellpadding='0' id='tblR'>");

                try{
                    ResultSet retencionDetalle = objFactura.getRetencionDetalle(id_retencion_compra);
                    String codigo = "";
                    String id_tabla_impuesto = "";
                    //String campo_valor = "";
                    //String impuesto = "";
                    //String codigo_impuesto = "";
                    //String porcentaje_retencion = "";
                    i=0;
                    while(retencionDetalle.next()){
                        codigo = (retencionDetalle.getString("codigo")!=null) ? retencionDetalle.getString("codigo") : "";
                        id_tabla_impuesto = (retencionDetalle.getString("id_tabla_impuesto")!=null) ? retencionDetalle.getString("id_tabla_impuesto") : "";
                        //campo_valor = (retencionDetalle.getString("campo_valor")!=null) ? retencionDetalle.getString("campo_valor") : "";
                        //impuesto = (retencionDetalle.getString("impuesto")!=null) ? retencionDetalle.getString("impuesto") : "";
                        //codigo_impuesto = (retencionDetalle.getString("codigo_impuesto")!=null) ? retencionDetalle.getString("codigo_impuesto") : "";
                        //porcentaje_retencion = (retencionDetalle.getString("porcentaje_retencion")!=null) ? retencionDetalle.getString("porcentaje_retencion") : "";
                        html += "<tr id='rRe"+i+"' class='jm_filaPar'>";
                        html += "<td width='170'>"+ DatosDinamicos.combo(rsBasesImponibles, "codi"+i, codigo, "", "inv_sumRetSubTotales("+i+",1);inv_sumRetTotales();", "inv_sumRetSubTotales("+i+",1);inv_sumRetTotales();", "", 165) +"</td>";
                        html += "<td width='300'>"+ DatosDinamicos.combo(rsTablaImpuestos, "idR"+i, id_tabla_impuesto, "inv_setMsg('axMsR');", "inv_setConcepto("+i+");inv_sumRetSubTotales("+i+",1);inv_sumRetTotales();", "_('bi"+i+"').focus();", "", 295) +"</td>";
                        html += "<td width='121' align='right'><input id='bi"+i+"' name='bi"+i+"' type='text' class='inpTbl' size='15' onkeypress=\"_evaluar(event, '0123456789.');\" onkeyup=\"inv_sumRetSubTotales("+i+");inv_sumRetTotales();\" " +
                                "onfocus=\"this.select();\" value='"+((retencionDetalle.getString("base_imponible")!=null) ? retencionDetalle.getString("base_imponible") : "")+"' /></td>";
                        html += "<td width='121' align='right'><input id='vr"+i+"' name='vr"+i+"' type='text' class='inpTbl' size='15' onkeypress=\"_evaluar(event, '0123456789.');\" onkeyup=\"inv_sumRetTotales();\" " +
                                "onfocus=\"this.select();\" value='"+((retencionDetalle.getString("valor_retenido")!=null) ? retencionDetalle.getString("valor_retenido") : "")+"' /></td>";
                        html += "<td width='21' align='center'><div class='jm_icoCon' style='background-position:-545px -18px;float:right;' title='Eliminar' onclick=\"_R('rRe"+i+"');inv_sumRetTotales();\">&nbsp;</div></td>";
                        //html += "<td width='21' align='center'>&nbsp;</td>";
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
                        "<td align='right'><span id='axMsR'></span>&nbsp;<input type='text' id='tvr' name='tvr' value='"+ret_impuesto_retenido+"' style='text-align:right' onkeypress=\"_evaluar(event, '0123456789.');\" /></td>" +
                        "</tr></table>");

                try{
                    rsTablaImpuestos.close();
                }catch(Exception e){
                    e.printStackTrace();
                }
            //}
            out.print("</div>");



            /*  BOTONES  */


            out.println("<table class='jm_tabla' width='700' cellspacing='0' cellpadding='0'><tr>");
            if(!anulado){
                long fecha_sistema = Fecha.getTimeStamp(Fecha.getFecha("SQL"));
                long fecha_asiento = Fecha.getTimeStamp(fecha_emision);
                /*if(id.compareTo("-1")!=0 && objRol.getAcceso(id_rol, "compRetCompImprimir") && emitido_ret){
                    out.println("<td><input type='button' value='Imprimir Retenci&oacute;n' onclick=\"imprimir('pdfComprobanteRetencion?id="+id_retencion_compra+"&doc=l');\" /></td>");
                }*/
                if(id.compareTo("-1")!=0 && objRol.getAcceso(id_rol, "liquidacionAnular")){
                    out.println("<td align='right'><input type='button' value='Re-imprimir Liquidaci&oacute;n' onclick=\"imprimir('pdfLiquidacionCompra?id="+id+"');\" /></td>");
                    if(fecha_sistema==fecha_asiento && estado_documento.compareTo("f")==0){
                        out.println("<td align='right'><input type='button' value='Anular Liquidaci&oacute;n' onclick='inv_liquidacionAnular("+id+");this.disabled=true;' /></td>");
                    }
                }
                
                if(id.compareTo("-1")!=0 && estado_documento.compareTo("n")==0){
                    out.println("<td align='right'><input type='checkbox' id='genNewClave' name='genNewClave' /> <span class='marca'>Generar nueva clave de acceso </span>&nbsp;"
                            + "<input type='submit' value='Actualizar Retencio&oacute;n' onmousedown=\"_('frmLicCom').action='frmRetencionActualizar';\" onclick=\"this.disabled=true;\" /></td>");
                } 
                if(id.compareTo("-1")==0){
                    out.println("<td align='right'><input type='submit' value='Guardar e Imprimir' onmousedown=\"_('imp').value=0;\" onclick=\"this.disabled=true;\" /></td>");
                }
                    
            }else{
                out.println("<td><span class='marca'>LIQUIDACION DE COMPRA/SERVICIO ANULADA</span></td>");
            }
            out.print("</tr></table>");



            out.print("</div></div></form></div>");

        } finally {
            //conf.cerrar();
            objBaseImponible.cerrar();
            objTablaImpuesto.cerrar();
            objFactura.cerrar();
            objProducto.cerrar();
            objProveedor.cerrar();
            objRol.cerrar();
            objLiquidacionCompra.cerrar();
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
