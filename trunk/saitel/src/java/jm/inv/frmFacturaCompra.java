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
import jm.web.DatosDinamicos;
import jm.web.Fecha;
import jm.seg.clas.Rol;
import jm.adm.clas.Sucursal;
import jm.adm.clas.TablaImpuesto;
import jm.inv.clas.Proveedor;
import jm.inv.clas.Producto;
import jm.inv.clas.FacturaCompra;
import java.sql.ResultSet;
import jm.inv.clas.BaseImponible;
import jm.inv.clas.NotaCreditoCompra;
import jm.web.Xml;

/**
 *
 * @author Jorge
 */
public class frmFacturaCompra extends HttpServlet {
    private String _ip = null;
    private int _puerto = 5432;
    private String _db = null;
    private String _dir = null;

    public void init(ServletConfig config) throws ServletException
    {
        this._ip = config.getServletContext().getInitParameter("_IP");
        this._puerto = Integer.parseInt(config.getServletContext().getInitParameter("_PUERTO"));
        this._db = config.getServletContext().getInitParameter("_DB");
        this._dir = config.getServletContext().getInitParameter("_DIR");
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

        String idFactura = request.getParameter("id");
        idFactura = idFactura!=null ? idFactura : "-1";
        String accion = request.getParameter("act");
        String fact_xml = request.getParameter("xml");
        String _altBody = "445";

        //this._dir = "/home/desarrollo/";
        Xml xml = new Xml(this._dir + fact_xml);   // para cargar una factura electronica
        
        
        FacturaCompra objFactura = new FacturaCompra(this._ip, this._puerto, this._db, usuario, clave);

        Proveedor objProveedor = new Proveedor(this._ip, this._puerto, this._db, usuario, clave);
        ResultSet rsProveedores = objProveedor.getProveedores();

        TablaImpuesto objTablaImpuesto = new TablaImpuesto(this._ip, this._puerto, this._db, usuario, clave);
        Rol objRol = new Rol(this._ip, this._puerto, this._db, usuario, clave);

        //PlanCuenta objPlanCuenta = new PlanCuenta(this._ip, this._puerto, this._db, usuario, clave);
        //ResultSet cuentas = objPlanCuenta.getTablasAsignacion();
        
        BaseImponible objBaseImponible = new BaseImponible(this._ip, this._puerto, this._db, usuario, clave);

        /*Configuracion conf = new Configuracion(this._ip, this._puerto, this._db, usuario, clave);
        String p_iva1 = conf.getValor("p_iva1");
        conf.cerrar();*/

        String id_nota_credito_compra = "";

        String suc_ret_num_serie = "";
        String suc_ret_sec_desde = "0";
        String suc_fac_sec_hasta = "0";
        String suc_ret_cad_libretin = "";
        String suc_ret_autorizacion = "";
        Sucursal objSucursal = new Sucursal(this._ip, this._puerto, this._db, usuario, clave);
        try{
            ResultSet rs = objSucursal.getSucursal(String.valueOf(id_sucursal));
            if(rs.next()){
                suc_ret_num_serie = (rs.getString("ret_num_serie")!=null) ? rs.getString("ret_num_serie") : suc_ret_num_serie;
                suc_ret_sec_desde = (rs.getString("ret_sec_desde")!=null) ? rs.getString("ret_sec_desde") : "0";
                suc_fac_sec_hasta = (rs.getString("ret_sec_hasta")!=null) ? rs.getString("ret_sec_hasta") : "0";
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
            String id_sucursal_factura = "1";
            String id_comprobante_diario = "";
            String id_proveedor = "-0";
            String razon_social = "";
            String serie_factura = "001-001";
            String autorizacion = "";
            String num_factura = "";
            String fecha_ven_factura = "";
            String fecha = Fecha.getFecha("SQL");
            String fecha_compra = "";
            String subtotal = "0";
            String descuento = "0";
            String subtotal_0 = "0";
            String subtotal_2 = "0";
            String subtotal_6 = "0";
            String iva_2 = "0";
            String propina = "0";
            String total_compra = "0";
            String observacion = "";
            float deuda = 0;
            boolean contabilizado = false;
            boolean anulado = false;

            String sustento_tributario = "";
            String id_retencion_compra = "-1";
            String ret_num_serie = suc_ret_num_serie;
            String clave_acceso = "";
            String ret_autorizacion = suc_ret_autorizacion;
            
            String ret_num_retencion = String.valueOf(objFactura.getNumRetencion(String.valueOf(id_sucursal)));
            if(id_sucursal==1 || id_sucursal==3){
                int ax_ret_num_retencion = objFactura.getNumRetencionSaltado(suc_ret_num_serie);
                ret_num_retencion = ax_ret_num_retencion>0 ? String.valueOf(ax_ret_num_retencion) : ret_num_retencion;
            }
            
            String ret_fecha_emision = "";
            String ret_ejercicio_fiscal = String.valueOf(Fecha.getAnio());
            int mes = Fecha.getMes();
            String ret_ejercicio_fiscal_mes = mes<10 ? "0" + mes : String.valueOf(mes);
            String ret_impuesto_retenido = "";
            boolean ret_anulado = false;
            boolean emitido_ret = false;
            String estado_documento = "";

            String comboProductos = "";
            ResultSet facturaDetalle = null;

            String ocultar = "hidden";

            
            if(fact_xml != null){
                try {
                    razon_social = xml.getValor("razonSocial");
                    String ruc = xml.getValor("ruc");
                    id_proveedor = objProveedor.getId(ruc);
                    serie_factura = xml.getValor("estab") + "-" + xml.getValor("ptoEmi");
                    autorizacion = xml.getValor("claveAcceso");
                    num_factura = String.valueOf( Long.parseLong(xml.getValor("secuencial")) );
                    fecha_ven_factura = Fecha.getFecha("SQL");
                    fecha_compra = xml.getValor("fechaEmision");
                    subtotal = xml.getValor("totalSinImpuestos");
                    descuento = xml.getValor("totalDescuento");
                    propina = xml.getValor("propina");
                    total_compra = xml.getValor("importeTotal");
                    
                    for(int k=0; k<xml.getNumNodos("totalImpuesto"); k++){
                        String codigoPorcentaje = xml.getValor("codigoPorcentaje", k);
                        if(codigoPorcentaje.compareTo("0")==0){
                            subtotal_0 = xml.getValor("baseImponible", k);
                        }
                        if(codigoPorcentaje.compareTo("2")==0){
                            subtotal_2 = xml.getValor("baseImponible", k);
                            iva_2 = xml.getValor("valor", k);
                        }
                        if(codigoPorcentaje.compareTo("6")==0){
                            subtotal_6 = xml.getValor("baseImponible", k);
                        }
                    }
                    
                }catch(Exception e){
                    e.printStackTrace();
                }
            }
            
            
            
            
            if(idFactura.compareTo("-1")!=0){
                ocultar = "visible";
                try{
                    ResultSet rsFactura = objFactura.getFactura(idFactura);
                    facturaDetalle = objFactura.getFacturaDetalle(idFactura);
                    if(rsFactura.next()){
                        id_sucursal_factura = (rsFactura.getString("id_sucursal")!=null) ? rsFactura.getString("id_sucursal") : "1";
                        id_comprobante_diario = (rsFactura.getString("id_comprobante_diario")!=null) ? rsFactura.getString("id_comprobante_diario") : "";
                        id_proveedor = (rsFactura.getString("id_proveedor")!=null) ? rsFactura.getString("id_proveedor") : "-0";
                        razon_social = (rsFactura.getString("razon_social")!=null) ? rsFactura.getString("razon_social") : "";
                        serie_factura = (rsFactura.getString("serie_factura")!=null) ? rsFactura.getString("serie_factura") : "001-001";
                        autorizacion = (rsFactura.getString("autorizacion")!=null) ? rsFactura.getString("autorizacion") : "";
                        num_factura = (rsFactura.getString("num_factura")!=null) ? rsFactura.getString("num_factura") : "";
                        fecha_ven_factura = (rsFactura.getString("fecha_ven_factura")!=null) ? Fecha.ISOaSQL(rsFactura.getString("fecha_ven_factura")) : "";
                        fecha = (rsFactura.getString("fecha")!=null) ? Fecha.ISOaSQL(rsFactura.getString("fecha")) : "";
                        fecha_compra = (rsFactura.getString("fecha_compra")!=null) ? Fecha.ISOaSQL(rsFactura.getString("fecha_compra")) : "";
                        subtotal = (rsFactura.getString("subtotal")!=null) ? rsFactura.getString("subtotal") : "";
                        descuento = (rsFactura.getString("descuento")!=null) ? rsFactura.getString("descuento") : "";
                        subtotal_0 = (rsFactura.getString("subtotal_0")!=null) ? rsFactura.getString("subtotal_0") : "";
                        subtotal_2 = (rsFactura.getString("subtotal_2")!=null) ? rsFactura.getString("subtotal_2") : "";
                        subtotal_6 = (rsFactura.getString("subtotal_6")!=null) ? rsFactura.getString("subtotal_6") : "";
                        iva_2 = (rsFactura.getString("iva_2")!=null) ? rsFactura.getString("iva_2") : "";
                        propina = (rsFactura.getString("propina")!=null) ? rsFactura.getString("propina") : "";
                        total_compra = (rsFactura.getString("total_compra")!=null) ? rsFactura.getString("total_compra") : "";
                        observacion = (rsFactura.getString("observacion")!=null) ? rsFactura.getString("observacion") : "";
                        contabilizado = (rsFactura.getString("contabilizado")!=null) ? rsFactura.getBoolean("contabilizado") : false;
                        deuda = (rsFactura.getString("deuda")!=null) ? rsFactura.getFloat("deuda") : 0;
                        anulado = (rsFactura.getString("anulado")!=null) ? rsFactura.getBoolean("anulado") : false;
                        rsFactura.close();
                    }
                }catch(Exception e){
                    e.printStackTrace();
                }

                try{
                    ResultSet rsRetencion = objFactura.getRetencion(idFactura);
                    if(rsRetencion.next()){
                        ret_anulado = (rsRetencion.getString("anulado")!=null) ? rsRetencion.getBoolean("anulado") : false;
                        if(!ret_anulado){
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

            String html="";


            out.print("obj»cmp_html^foc»busProv^fun»_objTab.seleccionar(0);"+(fact_xml!=null && id_proveedor.compareTo("-0")!=0 ? "_('InAr').style.visibility='visible';" : "")+"inv_setDetalleCompra();^frm»");

            out.print("<div class='grupoTab' style='width:770px;'><div id='tabR0'>"
                    + "<a id='tab00' href='javascript:void(0);' class='jm_tabAct' onclick='_objTab.seleccionar(0);'>Factura</a>"
                    + "<a id='tab01' href='javascript:void(0);' class='jm_tab' onclick='_objTab.seleccionar(1);'>Retenci&oacute;n</a>");
            if(idFactura.compareTo("-1")!=0 && contabilizado){
                //if(deuda == 0){
                    out.print("<a id='tab02' href='javascript:void(0);' class='jm_tab' onclick='_objTab.seleccionar(2);'>Nota Cr&eacute;dito</a>");
                //}
                //out.print("<a id='tab03' href='javascript:void(0);' class='jm_tab' onclick='_objTab.seleccionar(3);'>Nota D&eacute;bito</a>");
            }
            out.print("</div>");

            boolean descargar_kardex = accion.compareTo("frmFacturaImportacionGuardar")==0 ? false : true;

            out.print("<form id='frmCompra' action='"+accion+"' onsubmit='return inv_compraGuardar(this)' autocomplete='off'>");
            out.print("<input type='hidden' id='id' name='id' value='"+idFactura+"' />"
                    + "<input type='hidden' id='id_sucursal_doc' name='id_sucursal_doc' value='"+id_sucursal_factura+"' />"
                    + "<input type='hidden' id='idCD' name='idCD' value='"+id_comprobante_diario+"' />"
                    + "<input type='hidden' id='idRC' name='idRC' value='"+id_retencion_compra+"' />"
                    + "<input type='hidden' id='secDe' name='secDe' value='"+suc_ret_sec_desde+"' />"
                    + "<input type='hidden' id='secHa' name='secHa' value='"+suc_fac_sec_hasta+"' />"
                    + "<input type='hidden' id='feCad' name='feCad' value='"+suc_ret_cad_libretin+"' />"
                    + "<input type='hidden' id='funct' name='funct' value='inv_buscarCompra()' />"
                    + "<input type='hidden' id='tope' name='tope' value='0' />"
                    + "<input type='hidden' id='coP' name='coP' value='"+id_proveedor+"' />"
                    //+ "<input type='hidden' id='p_iva1' name='p_iva1' value='"+p_iva1+"' />"
                    + "<input type='hidden' id='tpyDoc' value='f' />"
                    + "<input type='hidden' id='contabilizar' name='contabilizar' value='false' />"
                    + "<input type='hidden' id='gr' name='gr' value='0' />"
                    + "<input type='hidden' id='elect' name='elect' value='0' />");

            if(accion.compareTo("frmFacturaImportacionGuardar")==0){
                String idDau = request.getParameter("idDau");
                out.print("<input type='hidden' id='idDau' name='idDau' value='"+idDau+"' />");
            }

            out.print("<div id='tabR1'>");

            /* primer tab */

            out.print("<div id='tab10' class='jm_tabHTML' style='display:block;width:760px;height:"+_altBody+"px;'>");

            out.print("<div id='mSg0' style=\"display:none\" class='msg'>&nbsp;</div>" +
                    "<table width='740' cellpadding='0' cellspacing='0'>" +
                    "<tr><td colspan='3'></td><td colspan='3'><span id='axMsP'></span></td></tr>"+
                    "<tr><td align='center' colspan='4'>F A C T U R A &nbsp;&nbsp; D E &nbsp;&nbsp; C O M P R A </td>" +
                    "<td align='right'>Nro. <span class='marca'>*</span></td>" +
                    "<td><input type='text' id='sf' name='sf' size='6' maxlength='7' value='"+serie_factura+"' onkeypress=\"_evaluar(event, '0123456789-');\" onkeyup='inv_setDetalleCompra()' />-" +
                    "<input type='text' size='6' id='nf' name='nf' value='"+num_factura+"' onkeypress=\"_evaluar(event, '0123456789-');\" onkeyup='inv_setDetalleCompra()' /></td></tr>" +

                    "<tr><td>Proveedor: <span class='marca'>*</span></td>" +
                    "<td colspan='3'><div style='float:left;display:table-cell' id='aux_coP'>"+
                    "<input type='text' id='busProv' style='width:250px;' value='"+razon_social+"' onfocus='this.select()' onkeyup=\"inv_getProveedores(this.value);\" /></div>");

            if(idFactura.compareTo("-1")==0){
                out.print("<div style='float:left;display:table-cell'> &nbsp;&nbsp; </div>"
                    + "<div class='jm_icoCon' style='background-position:-291px -139px;' title='Nuevo Proveedor' onclick=\"inv_getFrmProveedor('f')\">&nbsp;</div>");
            
                out.print("<div style='float:left;display:table-cell'> &nbsp;&nbsp;&nbsp;&nbsp; </div>"
                    + "<div class='jm_icoCon' style='background-position:-314px -139px;' title='Nuevo Producto' onclick=\"inv_getFrmProducto()\">&nbsp;</div>");
                
                out.print("<div style='float:left;display:table-cell'> &nbsp;&nbsp;&nbsp;&nbsp; </div>"
                    + "<div class='jm_icoCon' style='background-position:-547px -180px;' title='Cargar factura electr&oacute;nica' onclick=\"inv_subirFacturaProveedor()\">&nbsp;</div>");
            }

            out.print("</td>" +
                    "<td align='right'>Autorizaci&oacute;n: </td><td><input type='text' size='20' maxlength='39' id='auf' name='auf' value='"+autorizacion+"' onkeypress=\"_numero(event);\" /></td>" +
                    "</tr>" +

                    "<tr><td>Fecha de compra: <span class='marca'>*</span> </td><td colspan='3'><input id='fc' name='fc' type='text'size='9' value='"+fecha_compra+"' readOnly />" +
                    "<input type='button' value='...' onClick=\"SelectorFecha.crear('fc', 'SQL');\" /></td>" +
                    "<td>Fecha de validez: <span class='marca'>*</span> </td><td><input id='fv' name='fv' type='text'size='9' value='"+fecha_ven_factura+"' readOnly />" +
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

            if(fact_xml != null){
                try {
                    i=0;
                    html = "";
                    for(int k=0; k<xml.getNumNodos("detalle"); k++){
                        String codigoPrincipal = xml.getValor("codigoPrincipal", k);
                        String codigoAuxiliar = xml.getValor("codigoAuxiliar", k);
                        ResultSet rsDet = objProducto.getProducto(codigoPrincipal, codigoAuxiliar);
                        if(objProducto.getFilas(rsDet)>0){
                            if(rsDet.next()){
                                String id_producto = rsDet.getString("id_producto")!=null ? rsDet.getString("id_producto") : "";
                                String codigo = rsDet.getString("codigo")!=null ? rsDet.getString("codigo") : "";
                                String descripcion = rsDet.getString("descripcion")!=null ? rsDet.getString("descripcion") : "";
                                String porcentaje = rsDet.getString("porcentaje")!=null ? rsDet.getString("porcentaje") : "";
                                
                                float st = Float.parseFloat( xml.getValor("precioTotalSinImpuesto", k) );
                                float des = Float.parseFloat( xml.getValor("descuento", k) );
                                float iva = Float.parseFloat( xml.getValor("impuesto", "valor", k) );
                                
                                html += "<tr id='r"+i+"' name='r"+i+"' class='jm_filaPar'>";
                                html += "<td width='230'><input type='hidden' id='idA"+i+"' name='idA"+i+"' value='"+id_producto+"'/>"
                                        + "<input type='hidden' id='pi"+i+"' name='pi"+i+"' value='"+porcentaje+"'/>"
                                        + ""+codigo+" &nbsp; "
                                        + ""+descripcion+"</td>";
                                html += "<td width='50' align='right'><input id='c"+i+"' name='c"+i+"' type='text' class='inpTbl' size='5' onkeypress=\"_numero(event);\" onkeyup=\"inv_sumSubTotal('', "+i+");inv_sumTotales('');\" " +
                                        "onfocus=\"this.select();\" value='"+xml.getValor("cantidad", k)+"' /></td>";
                                html += "<td width='81' align='right'><input id='pu"+i+"' name='pu"+i+"' type='text' class='inpTbl' size='8' onkeypress=\"_evaluar(event, '0123456789.');\" onkeyup=\"inv_sumSubTotal('', "+i+");inv_sumTotales('');\" " +
                                        "onfocus=\"this.select();\" value='"+xml.getValor("precioUnitario", k)+"' /></td>";
                                html += "<td width='81' align='right'><input id='st"+i+"' name='st"+i+"' type='text' class='inpTbl' size='8' onkeypress=\"_evaluar(event, '0123456789.');\" onkeyup=\"inv_sumTotales('');\" " +
                                        "onfocus=\"this.select();\" value='"+st+"' /></td>";
                                html += "<td width='71' align='right'><input id='d"+i+"' name='d"+i+"' type='text' class='inpTbl' size='7' onkeypress=\"_evaluar(event, '0123456789.');\" onkeyup=\"inv_sumTotales('');\" " +
                                        "onfocus=\"this.select();\" value='"+des+"' /></td>";
                                html += "<td width='71' align='right'><input id='v"+i+"' name='v"+i+"' type='text' class='inpTbl' size='7' onkeypress=\"_evaluar(event, '0123456789.');\" onkeyup=\"inv_sumTotales('');\" " +
                                        "onfocus=\"this.select();\" value='"+iva+"' /></td>";
                                html += "<td width='91' align='right'><input id='t"+i+"' name='t"+i+"' type='text' class='inpTbl' size='9' onkeypress=\"_evaluar(event, '0123456789.');\" onkeyup=\"inv_sumTotales('');\" " +
                                        "onfocus=\"this.select();\" value='"+(st-des+iva)+"' /></td>";
                                html += "<td width='21' align='center'><div class='jm_icoCon' style='background-position:-545px -18px;float:right;' title='Eliminar' onclick=\"_R('r"+i+"');inv_sumTotales('');\">&nbsp;</div></td>";
                                html += "</tr>";
                                i++;
                            }
                        }
                    }
                    out.print(html);
                }catch(Exception e){
                    e.printStackTrace();
                }
            }
            
            try{
                html = "";
                i=0;
                while(facturaDetalle.next()){
                    String id_producto = (facturaDetalle.getString("id_producto")!=null) ? facturaDetalle.getString("id_producto") : "";
                    String porcentaje = (facturaDetalle.getString("porcentaje")!=null) ? facturaDetalle.getString("porcentaje") : "";
                    html += "<tr id='r"+i+"' name='r"+i+"' class='jm_filaPar'>";
                    html += "<td width='230'><input type='hidden' id='idA"+i+"' name='idA"+i+"' value='"+id_producto+"'/>"
                            + "<input type='hidden' id='pi"+i+"' name='pi"+i+"' value='"+porcentaje+"'/>"
                            + ""+((facturaDetalle.getString("codigo")!=null) ? facturaDetalle.getString("codigo") : "")+" &nbsp; "
                            + ""+((facturaDetalle.getString("descripcion")!=null) ? facturaDetalle.getString("descripcion") : "")+"</td>";
                    html += "<td width='50' align='right'><input id='c"+i+"' name='c"+i+"' type='text' class='inpTbl' size='5' onkeypress=\"_numero(event);\" onkeyup=\"inv_sumSubTotal('', "+i+");inv_sumTotales('');\" " +
                            "onfocus=\"this.select();\" value='"+((facturaDetalle.getString("cantidad")!=null) ? facturaDetalle.getString("cantidad") : "")+"' /></td>";
                    html += "<td width='81' align='right'><input id='pu"+i+"' name='pu"+i+"' type='text' class='inpTbl' size='8' onkeypress=\"_evaluar(event, '0123456789.');\" onkeyup=\"inv_sumSubTotal('', "+i+");inv_sumTotales('');\" " +
                            "onfocus=\"this.select();\" value='"+((facturaDetalle.getString("p_u")!=null) ? facturaDetalle.getString("p_u") : "")+"' /></td>";
                    html += "<td width='81' align='right'><input id='st"+i+"' name='st"+i+"' type='text' class='inpTbl' size='8' onkeypress=\"_evaluar(event, '0123456789.');\" onkeyup=\"inv_sumTotales('');\" " +
                            "onfocus=\"this.select();\" value='"+((facturaDetalle.getString("p_st")!=null) ? facturaDetalle.getString("p_st") : "")+"' /></td>";
                    html += "<td width='71' align='right'><input id='d"+i+"' name='d"+i+"' type='text' class='inpTbl' size='7' onkeypress=\"_evaluar(event, '0123456789.');\" onkeyup=\"inv_sumTotales('');\" " +
                            "onfocus=\"this.select();\" value='"+((facturaDetalle.getString("descuento")!=null) ? facturaDetalle.getString("descuento") : "")+"' /></td>";
                    html += "<td width='71' align='right'><input id='v"+i+"' name='v"+i+"' type='text' class='inpTbl' size='7' onkeypress=\"_evaluar(event, '0123456789.');\" onkeyup=\"inv_sumTotales('');\" " +
                            "onfocus=\"this.select();\" value='"+((facturaDetalle.getString("iva")!=null) ? facturaDetalle.getString("iva") : "")+"' /></td>";
                    html += "<td width='91' align='right'><input id='t"+i+"' name='t"+i+"' type='text' class='inpTbl' size='9' onkeypress=\"_evaluar(event, '0123456789.');\" onkeyup=\"inv_sumTotales('');\" " +
                            "onfocus=\"this.select();\" value='"+((facturaDetalle.getString("total")!=null) ? facturaDetalle.getString("total") : "")+"' /></td>";
                    html += "<td width='21' align='center'><div class='jm_icoCon' style='background-position:-545px -18px;float:right;' title='Eliminar' onclick=\"_R('r"+i+"');inv_sumTotales('');\">&nbsp;</div></td>";
                    html += "</tr>";
                    i++;
                }
                out.print(html);
            }catch(Exception e){
                e.printStackTrace();
            }

            out.print("</tbody></table></div>" +
                    "<table cellpadding='0' cellspacing='0'>" +
                    "<tr><td width='440'> Concepto .- <span class='marca'>*</span></td>" +
                    "<td width='100'>SUBTOTAL: <span class='marca'>*</span> </td>" +
                    "<td align='right' width='150'> <input type='text' id='subt' name='subt' value='"+subtotal+"' style='text-align:right' onkeypress=\"_evaluar(event, '0123456789.');\" onkeyup=\"inv_sumRetTotales();\" /></td></tr>" +

                    "<tr><td rowspan='7' valign='top'><textarea id='ob' name='ob' cols='40' rows='3' onblur=\"this.value=this.value._trim();\">"+observacion+"</textarea></td>" +
                    "<td>SUBTOTAL 12%: </td>" +
                    "<td align='right' class='jm_filaPar'> <input type='text' id='subt2' name='subt2' value='"+subtotal_2+"' style='text-align:right' onkeypress=\"_evaluar(event, '0123456789.');\" /></td></tr>" +
                    
                    "<tr><td>SUBTOTAL 0%: </td>" +
                    "<td align='right' class='jm_filaPar'> <input type='text' id='subt0' name='subt0' value='"+subtotal_0+"' style='text-align:right' onkeypress=\"_evaluar(event, '0123456789.');\" /></td></tr>" +

                    "<tr><td>SUBTOTAL NO OBJETO DE IVA: </td>" +
                    "<td align='right' class='jm_filaPar'> <input type='text' id='subt6' name='subt6' value='"+subtotal_6+"' style='text-align:right' onkeypress=\"_evaluar(event, '0123456789.');\" /></td></tr>" +
                    
                    "<tr><td>DESCUENTO: </td>" +
                    "<td align='right' class='jm_filaPar'> <input type='text' id='dt' name='dt' value='"+descuento+"' style='text-align:right' onkeypress=\"_evaluar(event, '0123456789.');\" /></td></tr>" +

                    "<tr><td>12% IVA: </td>" +
                    "<td align='right' class='jm_filaPar'> <input type='text' id='iva2' name='iva2' value='"+iva_2+"' style='text-align:right' onkeypress=\"_evaluar(event, '0123456789.');\" /></td></tr>" +

                    "<tr><td>PROPINA: </td>" +
                    "<td align='right' class='jm_filaPar'> <input type='text' id='propina' name='propina' value='"+propina+"' style='text-align:right' onkeypress=\"_evaluar(event, '0123456789.');\" onkeyup=\"inv_sumTotales('');\" /></td></tr>" +
                    
                    "<tr><td>TOTAL: <span class='marca'>*</span> </td>" +
                    "<td align='right' class='jm_filaPar'> <input type='text' id='tt' name='tt' value='"+total_compra+"' style='text-align:right' onkeypress=\"_evaluar(event, '0123456789.');\" /></td></tr>" +

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


            out.println("<div id='tab11' class='jm_tabHTML' style='display:none;width:760px;height:"+_altBody+"px;'>");


            if((ret_num_retencion.compareTo("")==0 && objRol.getAcceso(id_rol, "compRetCompNueva")) || (ret_num_retencion.compareTo("")!=0 && objRol.getAcceso(id_rol, "compRetCompEditar"))){

                ResultSet rsBasesImponibles = objBaseImponible.getBasesImponibles();
                ResultSet rsTablaImpuestos = objTablaImpuesto.getTablaImpuestosVigente();

                out.println("<table width='740' cellpadding='0' cellspacing='0'>" +
                        "<tr><td align='center' width='450'> C O M P R O B A N T E &nbsp;&nbsp; D E &nbsp;&nbsp; R E T E N C I &Oacute; N</td>" +
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

                        "<div id='InRe' style='visibility:"+(( idFactura.compareTo("-1")!=0 && (estado_documento.compareTo("n")==0 || estado_documento.compareTo("")==0)) ? "visible" : "hidden")+"'>"
                                + "<a href='javascript:void(0);' style='float:left;' onclick=\"inv_addRetencion();\">Insertar Retenci&oacute;n</a></div>" +
                        "&nbsp;&nbsp;<div style=\"display:none\" id='axBI'>"+ DatosDinamicos.combo(rsBasesImponibles, "9a0", "9b0", "", "inv_sumRetSubTotales(9d0,1);inv_sumRetTotales();", "inv_sumRetSubTotales(9d0,1);inv_sumRetTotales();", "", 165) +"</div>"
                        + "<div style=\"display:none\" id='axCR'>"+ DatosDinamicos.combo(rsTablaImpuestos, "9a0", "9b0", "inv_setMsg('axMsR');", "inv_setConcepto(9c0);inv_sumRetSubTotales(9d0,1);inv_sumRetTotales();", "inv_setConcepto(9c0);inv_sumRetSubTotales(9d0,1);inv_sumRetTotales();", "", 285) +"</div>" +
                        "<div style=\"display:none\" id='axC'>"+ objTablaImpuesto.getConceptoRetencion() +"</div>" +
                        "<table cellpadding='0' cellspacing='0'>" +
                        "<TH class='jm_TH' width='170'>CAMPO BASE IMPONIBLE</TH>" +
                        "<TH class='jm_TH' width='290'>IMPUESTO - C&Oacute;DIGO - % DE RETENCI&Oacute;N</TH>" +
                        "<TH class='jm_TH' width='120'>BASE IMPONIBLE</TH>" +
                        "<TH class='jm_TH' width='120'>VALOR RETENIDO</TH>" +
                        "<TH class='jm_TH' width='20'>&nbsp;</TH></tr></table>" +

                        "<DIV id='sll1' style='overflow:auto;width:760px;height:165px;'>" +
                        "<TABLE class='jm_tabla' cellspacing='1' cellpadding='0' id='tblR'>");

                try{
                    ResultSet retencionDetalle = objFactura.getRetencionDetalle(id_retencion_compra);
                    String codigo = "";
                    String id_tabla_impuesto = "";
                    /*String campo_valor = "";
                    String impuesto = "";
                    String codigo_impuesto = "";
                    String porcentaje_retencion = "";*/
                    i=0;
                    html = "";
                    while(retencionDetalle.next()){
                        codigo = (retencionDetalle.getString("codigo")!=null) ? retencionDetalle.getString("codigo") : "";
                        id_tabla_impuesto = (retencionDetalle.getString("id_tabla_impuesto")!=null) ? retencionDetalle.getString("id_tabla_impuesto") : "";
                        /*campo_valor = (retencionDetalle.getString("campo_valor")!=null) ? retencionDetalle.getString("campo_valor") : "";
                        impuesto = (retencionDetalle.getString("impuesto")!=null) ? retencionDetalle.getString("impuesto") : "";
                        codigo_impuesto = (retencionDetalle.getString("codigo_impuesto")!=null) ? retencionDetalle.getString("codigo_impuesto") : "";
                        porcentaje_retencion = (retencionDetalle.getString("porcentaje_retencion")!=null) ? retencionDetalle.getString("porcentaje_retencion") : "";*/
                        html += "<tr id='rRe"+i+"' class='jm_filaPar'>";
                        html += "<td width='170'>"+ DatosDinamicos.combo(rsBasesImponibles, "codi"+i, codigo, "", "inv_sumRetSubTotales("+i+",1);inv_sumRetTotales();", "inv_sumRetSubTotales("+i+",1);inv_sumRetTotales();", "", 165) +"</td>";
                        html += "<td width='291'>"+ DatosDinamicos.combo(rsTablaImpuestos, "idR"+i, id_tabla_impuesto, "inv_setMsg('axMsR');", "inv_setConcepto("+i+");inv_sumRetSubTotales("+i+",1);inv_sumRetTotales();", "_('bi"+i+"').focus();", "", 285) +"</td>";
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
                        "<tr><td width='570' align='right'>TOTAL RETENCI&Oacute;N: </td>" +
                        "<td align='right'><span id='axMsR'></span>&nbsp;<input type='text' id='tvr' name='tvr' value='"+ret_impuesto_retenido+"' style='text-align:right' onkeypress=\"_evaluar(event, '0123456789.');\" /></td>" +
                        "</tr></table>");

                try{
                    rsTablaImpuestos.close();
                }catch(Exception e){
                    e.printStackTrace();
                }
            }
            out.print("</div>");





            /*           NOTAS DE CREDITO                */



            out.println("<div id='tab12' class='jm_tabHTML' style='display:none;width:760px;height:"+_altBody+"px;'>");

            if(idFactura.compareTo("-1")!=0){
                NotaCreditoCompra objNotaCredito = new NotaCreditoCompra(this._ip, this._puerto, this._db, usuario, clave);


                String nc_serie_nota = "001-001";
                String nc_num_nota = "";
                String nc_autorizacion_nota = "";
                //String nc_ci = ruc;
                //String nc_razon_social = razon_social;
                String nc_fecha_emision = Fecha.getFecha("SQL");
                String nc_modifica = "Factura No. "+serie_factura+"-"+num_factura;
                String nc_concepto = "Devolución de ";
                String nc_tipo = "v";
                String nc_subtotal = "0";
                String nc_subtotal_2 = "0";
                String nc_subtotal_6 = "0";
                String nc_descuento = "0";
                String nc_subtotal_0 = "0";
                String nc_iva_2 = "0";
                //String nc_ir = "0";
                //String nc_id_plan_cuenta_ir = "58";
                String nc_total = "0";
                try{
                    ResultSet rsNotaCredito = objNotaCredito.getNotaCreditoFactura(idFactura);
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
                        nc_subtotal = ((rsNotaCredito.getString("valor")!=null) ? rsNotaCredito.getString("valor") : "0.00");
                        nc_subtotal_2 = ((rsNotaCredito.getString("subtotal_2")!=null) ? rsNotaCredito.getString("subtotal_2") : "0.00");
                        nc_subtotal_6 = ((rsNotaCredito.getString("subtotal_6")!=null) ? rsNotaCredito.getString("subtotal_6") : "0.00");
                        nc_descuento = ((rsNotaCredito.getString("descuento")!=null) ? rsNotaCredito.getString("descuento") : "0.00");
                        nc_subtotal_0 = ((rsNotaCredito.getString("subtotal_0")!=null) ? rsNotaCredito.getString("subtotal_0") : "0.00");
                        nc_iva_2 = ((rsNotaCredito.getString("iva_2")!=null) ? rsNotaCredito.getString("iva_2") : "0.00");
                        nc_total = ((rsNotaCredito.getString("total")!=null) ? rsNotaCredito.getString("total") : "0.00");
                        //nc_ir = ((rsNotaCredito.getString("ir")!=null) ? rsNotaCredito.getString("ir") : "0.00");
                        //nc_id_plan_cuenta_ir = ((rsNotaCredito.getString("id_plan_cuenta_ir")!=null) ? rsNotaCredito.getString("id_plan_cuenta_ir") : "");
                        rsNotaCredito.close();
                    }
                }catch(Exception ie){
                    ie.printStackTrace();
                }


                out.println("<input type='hidden' id='nctope' name='nctope' value='0' />"
                        + "<table width='740' cellpadding='0' cellspacing='0'>" +
                    "<tr><td width='500'></td><td colspan='2'></td></tr>"+
                    "<tr><td align='center'> N O T A &nbsp;&nbsp; D E &nbsp;&nbsp; C R &Eacute; D I T O</td>" +
                    "<td align='right'> Nro. <input type='text' id='ncSerie' name='ncSerie' size='6' maxlength='7' value='"+nc_serie_nota+"' onkeypress=\"_evaluar(event, '0123456789-');\" />-" +
                    "<input type='text' size='6' id='ncNum' name='ncNum' value='"+nc_num_nota+"' onkeypress=\"_numero(event);\" /><br />"
                    + "Autorizacion: <input type='text' size='20' id='ncAut' name='ncAut' value='"+nc_autorizacion_nota+"' onkeypress=\"_numero(event);\" /></td>"+
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
                    //"<label><input type='radio' id='e2' name='tipo' value='r' "+(nc_tipo.compareTo("r")==0 ? "checked" : "r")+" /> Error en mayor valor en la factura</label></td>"+
                    "</tr>" + 
                        
                    "<tr><td>Tipo de modificaci&oacute;n: <span class='marca'>*</span></td>" + 
                    "<td colspan='3'><input type='text' size='90' id='ncRaMo' name='ncRaMo' value='"+nc_concepto+"' /></td></tr>" + 
                    "</table>");


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
                        facturaDetalle.beforeFirst();
                        i=0;
                        html = "";
                        while(facturaDetalle.next()){
                            cantidad = ((facturaDetalle.getString("cantidad")!=null) ? facturaDetalle.getString("cantidad") : "0");
                            ncPu = ((facturaDetalle.getString("p_u")!=null) ? facturaDetalle.getString("p_u") : "0");
                            ncST = ((facturaDetalle.getString("p_st")!=null) ? facturaDetalle.getString("p_st") : "0");
                            ncDes = ((facturaDetalle.getString("descuento")!=null) ? facturaDetalle.getString("descuento") : "0");
                            ncIva = ((facturaDetalle.getString("iva")!=null) ? facturaDetalle.getString("iva") : "0");
                            ncT = ((facturaDetalle.getString("total")!=null) ? facturaDetalle.getString("total") : "0");
                            html += "<tr id='ncr"+i+"' name='ncr"+i+"' class='jm_filaPar'>";
                            html += "<td width='21' align='center'><input type='checkbox' id='chProd"+i+"' name='chProd"+i+"' onclick=\"fac_setDetNotaCredito()\" />"+
                            "<input type='hidden' id='idFacVenDet"+i+"' name='idFacVenDet"+i+"' value='"+((facturaDetalle.getString("id_factura_compra_detalle")!=null) ? facturaDetalle.getString("id_factura_compra_detalle") : "")+"' />"
                                    + "<input type='hidden' id='axNcC"+i+"' value='"+cantidad+"' />"
                                    + "<input type='hidden' id='axncST"+i+"' value='"+ncST+"' />"
                                    + "<input type='hidden' id='axncDes"+i+"' value='"+ncDes+"' />"
                                    + "<input type='hidden' id='axncIva"+i+"' value='"+ncIva+"' />"
                                    + "<input type='hidden' id='axncT"+i+"' value='"+ncT+"' /></td>";
                            html += "<td width='100' id='cF"+i+"0'>"+((facturaDetalle.getString("codigo")!=null) ? facturaDetalle.getString("codigo") : "")+"</td>";
                            html += "<td width='226'>"+((facturaDetalle.getString("descripcion")!=null) ? facturaDetalle.getString("descripcion") : "")+"</td>";
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
                        facturaDetalle.close();
                    }else{
                        ResultSet rsNotaCreditoDetalle = objNotaCredito.getNotaCreditoFacturaDetalle(id_nota_credito_compra);
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
                    "<tr align='right'><td width='630'>SUBTOTAL: </td>" +
                    "<td><input type='text' style='text-align:right' size='13' maxlength='50' id='ncsubt' name='ncsubt' value='"+nc_subtotal+"' onkeypress=\"_evaluar(event, '0123456789.');\" /></td></tr>"+

                    "<tr align='right'><td>subtotal 12%: </td>" +
                    "<td><input type='text' style='text-align:right' size='13' maxlength='50' id='ncsubt2' name='ncsubt2' value='"+nc_subtotal_2+"' onkeypress=\"_evaluar(event, '0123456789.');\" /></td></tr>"+
                        
                    "<tr align='right'><td>SUBTOTAL 0%: </td>" +
                    "<td align='right' class='jm_filaPar'> <input type='text' size='13' id='ncsubt0' name='ncsubt0' value='"+nc_subtotal_0+"' style='text-align:right' onkeypress=\"_evaluar(event, '0123456789.');\" /></td></tr>" +

                    "<tr align='right'><td>SUBTOTAL NO OBJETO DE IVA: </td>" +
                    "<td align='right' class='jm_filaPar'> <input type='text' size='13' id='ncsubt6' name='ncsubt6' value='"+nc_subtotal_6+"' style='text-align:right' onkeypress=\"_evaluar(event, '0123456789.');\" /></td></tr>" +
                    
                    "<tr align='right'><td>DESCUENTO: </td>" +
                    "<td align='right' class='jm_filaPar'> <input type='text' size='13' id='ncdt' name='ncdt' value='"+nc_descuento+"' style='text-align:right' onkeypress=\"_evaluar(event, '0123456789.');\" /></td></tr>" +

                    "<tr align='right'><td>12% IVA: </td>" +
                    "<td align='right' class='jm_filaPar'> <input type='text' size='13' id='nciva2' name='nciva2' value='"+nc_iva_2+"' style='text-align:right' onkeypress=\"_evaluar(event, '0123456789.');\" /></td></tr>" +

                    "<tr align='right'><td>VALOR TOTAL</td>" +
                    "<td><input type='text' style='text-align:right' size='13' maxlength='50' id='nctt' name='nctt' value='"+nc_total+"' onkeypress=\"_evaluar(event, '0123456789.');\" /></td></tr>");

                    if(!anulado && id_nota_credito_compra.compareTo("")==0){
                        out.print("<tr class='jm_filaPar'><td colspan='2' align='right'><input type='button' value='Guardar nota de cr&eacute;dito' onclick=\"inv_notaCredito(_('frmCompra'))\" /></td></tr>");
                    }

                    out.print("</table>");
                    
                objNotaCredito.cerrar();
            }

            out.print("</div>");


            
            
            
            
            

            /*           NOTAS DE DEBITO                */


            
            
            
            
            
/*
            out.println("<div id='tab13' class='jm_tabHTML' style='display:none;width:760px;height:"+_altBody+"px;'>");

            if(idFactura.compareTo("-1")!=0){
                NotaDebitoCompra objNotaDebito = new NotaDebitoCompra(this._ip, this._puerto, this._db, usuario, clave);

                String id_nota_debito_compra = "";
                String nd_serie_nota = "";
                String nd_num_nota = "";
                String nd_autorizacion_nota = "";
                String nd_fecha_emision = Fecha.getFecha("SQL");
                String nd_modifica = "Factura No. "+serie_factura+"-"+num_factura;
                String nd_concepto = "Nota de débito por ";
                String nd_valor = "0";
                String nd_iva_0 = "0";
                String nd_iva_12 = "0";
                String nd_total = "0";
                try{
                    ResultSet rsNotaDebito = objNotaDebito.getNotaDebitoFactura(idFactura);
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
                
                objNotaDebito.cerrar();
                
            }

            out.print("</div>");
*/




            out.println("<table class='jm_tabla' width='100%' cellspacing='0' cellpadding='0'><tr>");
            if(!anulado){
                if(id_nota_credito_compra.compareTo("")==0){
                    //long fecha_sistema = Fecha.getTimeStamp(Fecha.getFecha("SQL"));
                    //long fecha_asiento = Fecha.getTimeStamp(fecha);
                    /*if(idFactura.compareTo("-1")!=0 && objRol.getAcceso(id_rol, "compRetCompImprimir") && emitido_ret){
                        out.println("<td><input type='button' value='Imprimir Retenci&oacute;n' onclick=\"imprimir('pdfComprobanteRetencion?id="+id_retencion_compra+"&doc=f');\" /></td>");
                    }
                    if(idFactura.compareTo("-1")!=0 && (objRol.getAcceso(id_rol, "compRetCompAnular") || objRol.getAcceso(id_rol, "importaFactRetAnular")) && emitido_ret && fecha_sistema==fecha_asiento){
                        out.println("<td align='right'><input type='button' value='Anular Retenci&oacute;n' onclick='inv_retencionAnular("+idFactura+", "+id_retencion_compra+");' /></td>");
                    }*/
                    //if(idFactura.compareTo("-1")!=0 && objRol.getAcceso(id_rol, "compraAnular")  && contabilizado && deuda>0){
                    if(idFactura.compareTo("-1")!=0 && objRol.getAcceso(id_rol, "compraAnular")  && contabilizado){
                        out.println("<td><input type='button' value='Anular Factura' onclick='inv_compraAnular("+idFactura+", "+descargar_kardex+");' /></td>");
                    }
                    /*if(!objFactura.hayPagos(idFactura) && (idFactura.compareTo("-1")==0 ||
                      (idFactura.compareTo("-1")!=0 && (objRol.getAcceso(id_rol, "compraEditar") || objRol.getAcceso(id_rol, "importaFactEditar"))))){*/
                    if(!contabilizado){
                        if(objRol.getAcceso(id_rol, "compraGuardar")){
                            out.println("<td align='left'><input type='submit' value='Guardar sin Contabilizar' onclick=\"_('gr').value=2;_('contabilizar').value=false;\" /></td>");
                        }
                        if(idFactura.compareTo("-1")!=0 && objRol.getAcceso(id_rol, "compraContabilizar")){
                            out.println("<td align='right'><input id='btn1' type='button' value='Contabilizar e Inventariar' onclick=\"_('gr').value=0;_('contabilizar').value=true;inv_compraGuardar(_('frmCompra'));this.disabled=true;\" /></td>");
                            //out.println("<td align='right'><input id='btn2' type='button' value='Contabilizar e Inventariar y Emitir Comprobante Electr&oacute;nico' onclick=\"_('gr').value=0;_('contabilizar').value=true;inv_compraGuardar(_('frmCompra'));this.disabled=true;\" /></td>");
                        }
                        /*if(idFactura.compareTo("-1")==0 && objRol.getAcceso(id_rol, "compraContabilizar")){
                            out.println("<td align='right'><input id='btn2' type='button' value='Contabilizar, Inventariar' onclick=\"_('gr').value=1;_('contabilizar').value=true;inv_compraGuardar(_('frmCompra'));\" /></td>");
                            //out.println("<td align='right'><input id='btn2' type='button' value='Contabilizar, Inventariar y Emitir Comprobante Electr&oacute;nico' onclick=\"_('gr').value=1;_('contabilizar').value=true;inv_compraGuardar(_('frmCompra'));\" /></td>");
                        }*/
                    }
                    if(idFactura.compareTo("-1")!=0 && estado_documento.compareTo("n")==0){
                        out.println("<td align='right'><input type='checkbox' id='genNewClave' name='genNewClave' /> <span class='marca'>Generar nueva clave de acceso </span>&nbsp;"
                                + "<input type='submit' value='Actualizar Retencio&oacute;n' onmousedown=\"_('frmCompra').action='frmRetencionActualizar';\" onclick=\"this.disabled=true;\" /></td>");
                    }
                }else{
                    out.println("<td><span class='marca'>FACTURA MODIFICADA POR NOTA DE CREDITO</span></td>");
                }
            }else{
                out.println("<td><span class='marca'>FACTURA DE COMPRA ANULADA</span></td>");
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
            //objPlanCuenta.cerrar();
            objProducto.cerrar();
            objProveedor.cerrar();
            objTablaImpuesto.cerrar();
            objRol.cerrar();
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
