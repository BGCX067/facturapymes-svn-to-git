/**
** @version 1.0
** @package FACTURAPYMES.
* @author Jorge Washington Mueses Cevallos.
* @copyright Copyright (C) 2011 por Jorge Mueses. Todos los derechos reservados.
* @license http://www.gnu.org/copyleft/gpl.html GNU/GPL.
** FACTURAPYMES es un software de libre distribución, que puede ser
* copiado y distribuido bajo los términos de la Licencia
* Attribution-NonCommercial-NoDerivs 3.0 Unported,
* de acuerdo con la publicada por la CREATIVE COMMONS CORPORATION.
*/

package jm.act;

import java.io.*;

import javax.servlet.*;
import javax.servlet.http.*;
import java.sql.ResultSet;
import jm.adm.clas.Bodega;
import jm.adm.clas.Comun;
import jm.adm.clas.Iva;
import jm.web.DatosDinamicos;
import jm.web.Fecha;
import jm.seg.clas.Rol;
import jm.adm.clas.TablaImpuesto;
import jm.adm.clas.Sucursal;
import jm.adm.clas.TablaDepreciacion;
import jm.inv.clas.BaseImponible;
//import jm.cnt.clas.PlanCuenta;
import jm.inv.clas.Producto;
import jm.inv.clas.FacturaCompra;
import jm.web.Xml;
//import jm.inv.clas.NotaCreditoCompra;
//import jm.inv.clas.NotaDebitoCompra;

/**
 *
 * @author Jorge
 */
public class frmFacturaActivo extends HttpServlet {
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
        String usuario = (String)sesion.getAttribute("usuario");
        String clave = (String)sesion.getAttribute("clave");
        String id_rol = (String)sesion.getAttribute("id_rol");
        int id_sucursal = (Integer)sesion.getAttribute("sucursal");

        response.setContentType("text/html;charset=UTF-8");
        response.setHeader("Pragma", "no-cache");
        response.setHeader("Expires", "Mon, 01 Jan 2001 00:00:01 GMT");
        response.setHeader("Cache-Control", "no-store");
        response.setHeader("Cache-Control", "must-revalidate");
        response.setHeader("Cache-Control", "no-cache");
        PrintWriter out = response.getWriter();

        String idFactura = request.getParameter("id");
        String accion = request.getParameter("act");
        String fact_xml = request.getParameter("xml");
        
        //this._dir = "/home/desarrollo/";
        Xml xml = new Xml(this._dir + fact_xml);
        int _altBody = 450;
        int _ancho = 760;

        FacturaCompra objFactura = new FacturaCompra(this._ip, this._puerto, this._db, usuario, clave);

        TablaImpuesto objTablaImpuesto = new TablaImpuesto(this._ip, this._puerto, this._db, usuario, clave);
        Rol objRol = new Rol(this._ip, this._puerto, this._db, usuario, clave);

        /*Configuracion conf = new Configuracion(this._ip, this._puerto, this._db, usuario, clave);
        String p_iva1 = conf.getValor("p_iva1");*/

        Bodega objBodega = new Bodega(this._ip, this._puerto, this._db, usuario, clave);
        ResultSet rsBodegas = objBodega.getBodegasResponsables();
        String id_bodega = "1";
        
        BaseImponible objBaseImponible = new BaseImponible(this._ip, this._puerto, this._db, usuario, clave);
        

        //String id_nota_credito_compra = "";

        String suc_ret_num_serie = "";
        String suc_ret_sec_desde = "";
        String suc_ret_sec_hasta = "";
        String suc_ret_cad_libretin = "";
        String suc_ret_autorizacion = "";
        try{
            Sucursal objSucursal = new Sucursal(this._ip, this._puerto, this._db, usuario, clave);
            ResultSet rs = objSucursal.getSucursal(String.valueOf(id_sucursal));
            if(rs.next()){
                suc_ret_num_serie = (rs.getString("ret_num_serie")!=null) ? rs.getString("ret_num_serie") : suc_ret_num_serie;
                suc_ret_sec_desde = (rs.getString("ret_sec_desde")!=null) ? rs.getString("ret_sec_desde") : "";
                suc_ret_sec_hasta = (rs.getString("ret_sec_hasta")!=null) ? rs.getString("ret_sec_hasta") : "";
                suc_ret_cad_libretin = (rs.getString("ret_cad_libretin")!=null) ? rs.getString("ret_cad_libretin") : "";
                suc_ret_autorizacion = (rs.getString("ret_autorizacion")!=null) ? rs.getString("ret_autorizacion") : suc_ret_autorizacion;
                rs.close();
            }
            objSucursal.cerrar();
        }catch(Exception e){
            e.printStackTrace();
        }
        
        Iva objIva = new Iva(this._ip, this._puerto, this._db, usuario, clave);
        ResultSet rsIvas = objIva.getIvasActivos();

        Producto objProducto = new Producto(this._ip, this._puerto, this._db, usuario, clave);

        try {
            int i=0;
            String id_sucursal_factura = "1";
            String id_comprobante_diario = "";
            String id_proveedor = "-0";
            String razon_social = "";
            String serie_factura = "";
            String autorizacion = "";
            String num_factura = "";
            String fecha_ven_factura = "";
            String fecha = Fecha.getFecha("SQL");
            String fecha_compra = "";
            String subtotal = "";
            String subtotal_0 = "0";
            String subtotal_2 = "0";
            String subtotal_6 = "0";
            String iva_2 = "0";
            String propina = "0";
            String descuento = "";
            String total_compra = "";
            String observacion = "";
            //float deuda = 0;
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
            String estado_documento = "";

            //String comboProductos = "";
            //String [][] tipo_factura1 = {{"p","Productos"},{"s","Servicios"},{"b","Bienes"}};

            String ocultar = "hidden";
            String id_bodega1 = "";         
            if(idFactura.compareTo("-1")!=0){
                ocultar = "visible";
                try{
                    ResultSet rsFactura = objFactura.getFactura(idFactura);
                    if(rsFactura.next()){
                        id_sucursal_factura = (rsFactura.getString("id_sucursal")!=null) ? rsFactura.getString("id_sucursal") : "1";
                        id_comprobante_diario = (rsFactura.getString("id_comprobante_diario")!=null) ? rsFactura.getString("id_comprobante_diario") : "";
                        id_bodega1 = (rsFactura.getString("id_bodega")!=null) ? rsFactura.getString("id_bodega") : "";
                        id_proveedor = (rsFactura.getString("id_proveedor")!=null) ? rsFactura.getString("id_proveedor") : "-0";
                        razon_social = (rsFactura.getString("razon_social")!=null) ? rsFactura.getString("razon_social") : "";
                        serie_factura = (rsFactura.getString("serie_factura")!=null) ? rsFactura.getString("serie_factura") : "";
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
                        //deuda = (rsFactura.getString("deuda")!=null) ? rsFactura.getFloat("deuda") : 0;
                        anulado = (rsFactura.getString("anulado")!=null) ? rsFactura.getBoolean("anulado") : false;
                        rsFactura.close();
                    }
                }catch(Exception e){
                    e.printStackTrace();
                }
                
                /*try{
                    Activo objActivo = new Activo(this._ip, this._puerto, this._db, usuario, clave);
                    ResultSet rsPers = objActivo.getPersonalizacionFact(idFactura);
                    if(rsPers.next()){
                        //recepcion = (rsPers.getString("persona_recibe")!=null) ? rsPers.getString("persona_recibe") : "";
                        rsPers.close();
                    }
                }catch(Exception exb){
                    exb.printStackTrace();
                }*/

                
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
            
            Comun objComun = new Comun(this._ip, this._puerto, this._db, usuario, clave);
            ResultSet rsCategorias = objComun.getComun("CAT");
            objComun.cerrar();

            TablaDepreciacion obj_TablaDepreciacion = new TablaDepreciacion(this._ip, this._puerto, this._db, usuario, clave);
            ResultSet rsDepreciacion = obj_TablaDepreciacion.getTablaDepreciaciones();
            obj_TablaDepreciacion.cerrar();
            
            id_bodega = id_bodega1.compareTo("")!=0 ? id_bodega1 : id_bodega;

            ResultSet rsEstanterias = objBodega.getUbicaciones(Integer.parseInt(id_bodega));
            

            String html="";

            //out.print("obj»vta1_html^foc»razon_social^fun»_objTab.seleccionar(0);_('id_bodega').disabled="+(idFactura.compareTo("-1")==0?"false":"true")+";^frm»");
            out.print("obj»vta1_html^foc»razon_social^fun»_objTab.seleccionar(0);^frm»");


            //boolean descargar_kardex = accion.compareTo("frmFacturaImportacionGuardar")==0 ? false : true;

            out.print("<form id='frmCompra' action='frmFacturaActivoGuardar' onsubmit='return act_compraGuardar(this)' autocomplete='off'>");
            out.print("<input type='hidden' id='id' name='id' value='"+idFactura+"' />"
                    + "<input type='hidden' id='id_sucursal_doc' name='id_sucursal_doc' value='"+id_sucursal_factura+"' />"
                    + "<input type='hidden' id='idCD' name='idCD' value='"+id_comprobante_diario+"' />"
                    + "<input type='hidden' id='idRC' name='idRC' value='"+id_retencion_compra+"' />"
                    + "<input type='hidden' id='secDe' name='secDe' value='"+suc_ret_sec_desde+"' />"
                    + "<input type='hidden' id='secHa' name='secHa' value='"+suc_ret_sec_hasta+"' />"
                    + "<input type='hidden' id='feCad' name='feCad' value='"+suc_ret_cad_libretin+"' />"
                    + "<input type='hidden' id='coP' name='coP' value='"+id_proveedor+"' />"
                    + "<input type='hidden' id='topeA' name='topeA' value='0' />"
                    + "<input type='hidden' id='topeP' name='topeP' value='0' />"
                    + "<input type='hidden' id='funct' name='funct' value='act_compraBuscar()' />"
                    //+ "<input type='hidden' id='p_iva1' name='p_iva1' value='"+p_iva1+"' />"
                    + "<input type='hidden' id='tpyDoc' value='f' />"
                    + "<input type='hidden' id='contabilizar' name='contabilizar' value='false' />"
                    + "<input type='hidden' id='imp' name='imp' value='1' />"
                    + "<input type='hidden' id='elect' name='elect' value='0' />");

            out.print("<div class='grupoTab' style='width:"+(_ancho+10)+"px;'><div id='tabR0'>"
                    + "<a id='tab00' href='javascript:void(0);' class='jm_tabAct' onclick='_objTab.seleccionar(0);'>Factura</a>"
                    + "<a id='tab01' href='javascript:void(0);' class='jm_tab' onclick='_objTab.seleccionar(1);'>Retenci&oacute;n</a>");
            /*if(idFactura.compareTo("-1")!=0){
                if(deuda == 0){
                    out.print("<a id='tab02' href='javascript:void(0);' class='jm_tab' onclick='_objTab.seleccionar(2);'>Nota Cr&eacute;dito</a>");
                }
                out.print("<a id='tab03' href='javascript:void(0);' class='jm_tab' onclick='_objTab.seleccionar(3);'>Nota D&eacute;bito</a>");
            }*/
            out.print("</div>");

            out.print("<div id='tabR1'>");
            
            
            
            

            /* primer tab */

            
            
            
            
            
            out.print("<div id='tab10' class='jm_tabHTML' style='display:block;width:"+(_ancho)+"px;height:"+_altBody+"px;'>");

            out.print("<div id='mSg0' style=\"display:none\" class='msg'>&nbsp;</div>" +
                    "<table width='740' cellpadding='0' cellspacing='0'>" +
                    "<tr><td colspan='3'></td><td colspan='3'><span id='axMsP'></span></td></tr>"+
                    "<tr><td align='center' colspan='4'>F A C T U R A &nbsp;&nbsp; D E &nbsp;&nbsp; C O M P R A </td>" +
                    "<td align='right'>Nro. <span class='marca'>*</span></td>" +
                    "<td><input type='text' id='sf' name='sf' size='6' maxlength='7' value='"+serie_factura+"' onkeypress=\"_evaluar(event, '0123456789-');\" onkeyup='inv_setDetalleCompra()' />-" +
                    "<input type='text' size='10' id='nf' name='nf' value='"+num_factura+"' onkeypress=\"_evaluar(event, '0123456789-');\" onkeyup='inv_setDetalleCompra()' /></td></tr>" +

                    "<tr><td>Proveedor: <span class='marca'>*</span></td>" +
                    "<td colspan='3'><div style='float:left;display:table-cell' id='aux_coP'>"+
                    "<input type='text' id='busProv' style='width:250px;' value='"+razon_social+"' onfocus='this.select()' onkeyup=\"inv_getProveedores(this.value);\" /></div>");

            if(idFactura.compareTo("-1")==0){
                out.print("<div style='float:left;display:table-cell'> &nbsp;&nbsp; </div>"
                    + "<div class='jm_icoCon' style='background-position:-291px -138px;' title='Nuevo Proveedor' onclick=\"inv_getFrmProveedor('f')\">&nbsp;</div>");
            }

            out.print("</td>" +
                    "<td align='right'>Autorizaci&oacute;n: </td><td><input type='text' size='10' maxlength='39' id='auf' name='auf' value='"+autorizacion+"' onkeypress=\"_numero(event);\" /></td>" +
                    "</tr>" +

                    "<tr><td>Fecha de compra: <span class='marca'>*</span> </td><td colspan='3'><input id='fc' name='fc' type='text'size='9' value='"+fecha_compra+"' readOnly />" +
                    "<input type='button' value='...' onClick=\"SelectorFecha.crear('fc', 'SQL');\" /></td>" +
                    "<td>Fecha de validez: <span class='marca'>*</span> </td><td><input id='fv' name='fv' type='text'size='9' value='"+fecha_ven_factura+"' readOnly />" +
                    "<input type='button' value='...' onClick=\"SelectorFecha.crear('fv', 'SQL');\" /></td></tr>"+

                    "<tr><td colspan='6'>Recepci&oacute;n en bodega: &nbsp; "+DatosDinamicos.combo(rsBodegas, "id_bodega", id_bodega, "act_getEstanterias(this.value)") +"</td></tr>" +

                    "<tr><td colspan='6'><a href='javascript:void(0)' onclick='act_addActivos()'>Insertar grupo de activos</a><div style=\"display:none\" id='axp'></div>"
                    + "<div id='axDep' style='display:none'>"+DatosDinamicos.combo(rsDepreciacion, "_Xx_", "", "", 148)+"</div>"
                    + "<div id='axUbi' style='display:none'>"+DatosDinamicos.combo(rsEstanterias, "_Xx_", "", "", 88)+"</div>"
                    + "<div id='axPIva' style='display:none'>"+DatosDinamicos.combo(rsIvas, "_Xx_", "", "inv_sumSubTotal('A', _POS_);act_sumTotales('A');", 88)+"</div>"
                    + "<div id='axCat' style='display:none'>"+DatosDinamicos.combo(rsCategorias, "_Xx_", "", "", 88)+"</div></td>" +
                    "</tr>" +
                    "</table>" +


                    "<DIV style='overflow:auto;width:733px;height:240px;'>"+
                    "<table id='cab0' cellpadding='0' cellspacing='0' width='1400'>" +
                    "<TH class='jm_TH' width='230'>CODIGOS | SERIES</TH>" +
                    "<TH class='jm_TH' width='230'>DESCRIPCION</TH>" +
                    "<TH class='jm_TH' width='160'>DEPRECIACION</TH>" +
                    "<TH class='jm_TH' width='100'>CATEGORIA <div class='jm_icoCon' id='add0' style='background-position:-504px -14px;' onclick=\"act_frmCategoria();\" title='Insertar'>&nbsp;</div></TH>" +
                    "<TH class='jm_TH' width='100'>UBICACION</TH>" +
                    "<TH class='jm_TH' width='70'>CANT.</TH>" +
                    "<TH class='jm_TH' width='80'>P./U.</TH>" +
                    "<TH class='jm_TH' width='80'>SUBTOT.</TH>" +
                    "<TH class='jm_TH' width='70'>DESC.</TH>" +
                    "<TH class='jm_TH' width='100'>% IVA</TH>" +
                    "<TH class='jm_TH' width='70'>IVA</TH>" +
                    "<TH class='jm_TH' width='90'>TOTAL</TH>" +
                    "<TH class='jm_TH' width='20'>&nbsp;&nbsp;</TH></tr></table>" +

                    "<DIV id='sllA' style='overflow:auto;width:1425px;height:205px;'>" +
                    "<div class='jm_tabla' id='tblA'>");

            try{
                ResultSet facturaDetalle = objFactura.getFacturaDetalleActivos(idFactura);
                i=0;
                String id_tabla_depreciacion = "";
                String id_categoria = "";
                String codigos_series = "";
                String descripcion = "";
                String ubicacion = "";
                String cantidad = "";
                String p_u = "";
                String p_st = "";
                String adescuento = "";
                String p_iva = "";
                String iva = "";
                String total = "";
                while(facturaDetalle.next()){
                    id_tabla_depreciacion = (facturaDetalle.getString("id_tabla_depreciacion")!=null) ? facturaDetalle.getString("id_tabla_depreciacion") : "";
                    id_categoria = (facturaDetalle.getString("id_categoria")!=null) ? facturaDetalle.getString("id_categoria") : "";
                    codigos_series = ((facturaDetalle.getString("codigos_series")!=null) ? facturaDetalle.getString("codigos_series") : "");
                    descripcion = ((facturaDetalle.getString("descripcion")!=null) ? facturaDetalle.getString("descripcion") : "");
                    ubicacion = ((facturaDetalle.getString("ubicacion")!=null) ? facturaDetalle.getString("ubicacion") : "");
                    cantidad = ((facturaDetalle.getString("cantidad")!=null) ? facturaDetalle.getString("cantidad") : "");
                    p_u = ((facturaDetalle.getString("p_u")!=null) ? facturaDetalle.getString("p_u") : "");
                    p_st = ((facturaDetalle.getString("p_st")!=null) ? facturaDetalle.getString("p_st") : "");
                    adescuento = ((facturaDetalle.getString("descuento")!=null) ? facturaDetalle.getString("descuento") : "");
                    p_iva = ((facturaDetalle.getString("p_iva")!=null) ? facturaDetalle.getString("p_iva") : "");
                    iva = ((facturaDetalle.getString("iva")!=null) ? facturaDetalle.getString("iva") : "");
                    total = ((facturaDetalle.getString("total")!=null) ? facturaDetalle.getString("total") : "");
                    html += "<div id='Ar"+i+"' class='jm_fila'>";
                    html += "<div class='jm_columna caja' style='width:223px'><textarea id=\"cod"+i+"\" name=\"cod"+i+"\" style=\"width:215px;height:28px\" ondblclick=\"act_frmCodigos("+i+")\">"+codigos_series+"</textarea></div>";
                    html += "<div class='jm_columna caja' style='width:228px' id='axDes"+i+"'><input id=\"des"+i+"\" name=\"des"+i+"\" type=\"text\" style='width:220px;' onkeyup=\"act_getDescripcion(this.value, "+i+");\" value=\""+descripcion+"\" /></div>";
                    html += "<div class='jm_columna caja' style='width:158px'>"+DatosDinamicos.combo(rsDepreciacion, "idDep"+i, id_tabla_depreciacion, "", 148)+"</div>";
                    html += "<div class='jm_columna caja' style='width:98px'>"+DatosDinamicos.combo(rsCategorias, "idCat"+i, id_categoria, "", 88)+"</div>";
                    html += "<div class='jm_columna caja' style='width:98px'>"+DatosDinamicos.combo(rsEstanterias, "ub"+i, ubicacion, "", 88)+"</div>";
                    html += "<div class='jm_columna der' style='width:68px'><input id='Ac"+i+"' name='Ac"+i+"' readonly type='text' class='inpTbl' size='7' value=\""+cantidad+"\" onkeyup=\"inv_sumSubTotal('A', \""+i+"\");act_sumTotales('');\" /></div>";
                    html += "<div class='jm_columna der' style='width:78px'><input id='Apu"+i+"' name='Apu"+i+"' type='text' class='inpTbl' size='9' value=\""+p_u+"\" onkeypress=\"_evaluar(event, '0123456789.');\" onfocus='this.select();' onkeyup=\"inv_sumSubTotal('A', "+i+");act_sumTotales('A');\" /></div>";
                    html += "<div class='jm_columna der' style='width:78px'><input id='Ast"+i+"' name='Ast"+i+"' type='text' class='inpTbl' size='9' value=\""+p_st+"\" onkeypress=\"_evaluar(event, '0123456789.');\" onfocus='this.select();' onkeyup=\"act_sumTotales('A');\" /></div>";
                    html += "<div class='jm_columna der' style='width:68px'><input id='Ad"+i+"' name='Ad"+i+"' type='text' class='inpTbl' size='7' value='0' value=\""+adescuento+"\" onkeypress=\"_evaluar(event, '0123456789.');\" onfocus='this.select();' onkeyup=\"inv_sumSubTotal('A', "+i+");act_sumTotales('A');\" /></div>";
                    html += "<div class='jm_columna caja' style='width:98px'>"+DatosDinamicos.combo(rsIvas, "Api"+i, p_iva, "", 88)+"</div>";
                    html += "<div class='jm_columna der' style='width:68px'><input id='Av"+i+"' name='Av"+i+"' type='text' class='inpTbl' size='7' value=\""+iva+"\" onkeypress=\"_evaluar(event, '0123456789.');\" onfocus='this.select();' onkeyup=\"act_setIva("+i+");act_sumTotales('A');\" /></div>";
                    html += "<div class='jm_columna der' style='width:88px'><input id='At"+i+"' name='At"+i+"' type='text' class='inpTbl' size='9' value=\""+total+"\"  onkeypress=\"_evaluar(event, '0123456789.');\" onfocus='this.select();' onkeyup=\"act_sumTotales('A');\" /></div>";
                    html += "<div class='jm_columna der' style='width:20px'><div class='jm_icoCon' style='background-position:-545px -18px;float:right;' title='Eliminar' onclick=\"_R('Ar"+i+"');act_sumTotales('A');\">&nbsp;</div></div>";
                    html += "</div>";
                    i++;
                }

                out.print(html);

            }catch(Exception e){
                e.printStackTrace();
            }

            out.print("</div></div></div>"
                    + "<table><tr><td colspan='6'><div id='InAr'>B&uacute;squeda de productos o servicios: &nbsp; <input type='text' id='busProd' onfocus=\"this.value=''\" style='width:350px;border-color:red' onkeyup=\"inv_getProductos(event, this.value, 'act_tblProductos');\" /></div>" +
                    "&nbsp;&nbsp;<div style=\"display:none\" id='axp'></div></td>" +
                    "<div style=\"display:none\" id='ax'></div></td>" +
                    "</tr>" +
                    "</table>"+
                    "<table cellpadding='0' cellspacing='0'>" +
                    "<TH class='jm_TH' width='230'>C&Oacute;DIGO - DESCRIPCI&Oacute;N</TH>" +
                    "<TH class='jm_TH' width='50'>CANT.</TH>" +
                    "<TH class='jm_TH' width='80'>P./U.</TH>" +
                    "<TH class='jm_TH' width='80'>SUBTOT.</TH>" +
                    "<TH class='jm_TH' width='70'>DESC.</TH>" +
                    "<TH class='jm_TH' width='70'>IVA</TH>" +
                    "<TH class='jm_TH' width='90'>TOTAL</TH>" +
                    "<TH class='jm_TH' width='20'>&nbsp;&nbsp;</TH></tr></table>" +

                    "<DIV id='sll0' style='overflow:auto;width:720px;height:80px;'>" +
                    "<TABLE class='jm_tabla' cellspacing='1' cellpadding='0'><tbody id='tblD'>");

            try{
                i=0;
                html="";
                ResultSet facturaDetalle = objFactura.getFacturaDetalleProdsServs(idFactura);
                String id_producto = "";
                //String p_iva = "";
                //boolean tiene_iva = false;
                while(facturaDetalle.next()){
                    String porcentaje = (facturaDetalle.getString("p_iva")!=null) ? facturaDetalle.getString("p_iva") : "";
                    id_producto = (facturaDetalle.getString("codigos_series")!=null) ? facturaDetalle.getString("codigos_series") : "";
                    //p_iva = ((facturaDetalle.getString("p_iva")!=null) ? facturaDetalle.getString("p_iva") : "");
                    html += "<tr id='r"+i+"' name='r"+i+"' class='jm_filaPar'>";
                    html += "<td width='230' id='codP"+i+"'>"
                            + "<input type='hidden' id='idA"+i+"' name='idA"+i+"' value='"+id_producto+"'/>"
                            + "<input type='hidden' id='pi"+i+"' name='pi"+i+"' value='"+porcentaje+"'/>"+
                            ((facturaDetalle.getString("codigo")!=null) ? facturaDetalle.getString("codigo") : "")+" &nbsp; "
                            + ((facturaDetalle.getString("descripcion_p")!=null) ? facturaDetalle.getString("descripcion_p") : "") + "</td>";
                    html += "<td width='50' align='right'><input id='c"+i+"' name='c"+i+"' type='text' class='inpTbl' size='5' onkeypress=\"_numero(event);\" onfocus='this.select();' onkeyup=\"inv_sumSubTotal('', "+i+");act_sumTotales('');\" " +
                            "onfocus=\"this.select();\" value='"+((facturaDetalle.getString("cantidad")!=null) ? facturaDetalle.getString("cantidad") : "")+"' /></td>";
                    html += "<td width='81' align='right'><input id='pu"+i+"' name='pu"+i+"' type='text' class='inpTbl' size='8' onkeypress=\"_evaluar(event, '0123456789.');\" onkeyup=\"inv_sumSubTotal('', "+i+");act_sumTotales();\" " +
                            "onfocus=\"this.select();\" value='"+((facturaDetalle.getString("p_u")!=null) ? facturaDetalle.getString("p_u") : "")+"' /></td>";
                    html += "<td width='81' align='right'><input id='st"+i+"' name='st"+i+"' type='text' class='inpTbl' size='8' onkeypress=\"_evaluar(event, '0123456789.');\" onkeyup=\"act_sumTotales('');\" " +
                            "onfocus=\"this.select();\" value='"+((facturaDetalle.getString("p_st")!=null) ? facturaDetalle.getString("p_st") : "")+"' /></td>";
                    html += "<td width='71' align='right'><input id='d"+i+"' name='d"+i+"' type='text' class='inpTbl' size='7' onkeypress=\"_evaluar(event, '0123456789.');\" onkeyup=\"inv_sumSubTotal('', "+i+");inv_sumTotales('');\" " +
                            "onfocus=\"this.select();\" value='"+((facturaDetalle.getString("descuento")!=null) ? facturaDetalle.getString("descuento") : "")+"' /></td>";
                    html += "<td width='71' align='right'><input id='v"+i+"' name='v"+i+"' type='text' class='inpTbl' size='7' onkeypress=\"_evaluar(event, '0123456789.');\" onkeyup=\"act_sumTotales('');\" " +
                            "onfocus=\"this.select();\" value='"+((facturaDetalle.getString("iva")!=null) ? facturaDetalle.getString("iva") : "")+"' /></td>";
                    html += "<td width='91' align='right'><input id='t"+i+"' name='t"+i+"' type='text' class='inpTbl' size='9' onkeypress=\"_evaluar(event, '0123456789.');\" onkeyup=\"act_sumTotales('');\" " +
                            "onfocus=\"this.select();\" value='"+((facturaDetalle.getString("total")!=null) ? facturaDetalle.getString("total") : "")+"' /></td>";
                    html += "<td width='21' align='center'><div class='jm_icoCon' style='background-position:-545px -18px;float:right;' title='Eliminar' onclick=\"_R('r"+i+"');act_sumTotales('');\">&nbsp;</div></td>";
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
                    "<td align='right' class='jm_filaPar'> <input type='text' id='propina' name='propina' value='"+propina+"' style='text-align:right' onkeypress=\"_evaluar(event, '0123456789.');\" onkeyup=\"act_sumTotales('');\" /></td></tr>" +
                    
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

            ResultSet rsBasesImponibles = objBaseImponible.getBasesImponibles();

            out.println("<div id='tab11' class='jm_tabHTML' style='display:none;width:"+(_ancho)+"px;height:"+_altBody+"px;'>");


            if((ret_num_retencion.compareTo("")==0 && objRol.getAcceso(id_rol, "compRetCompNueva")) || (ret_num_retencion.compareTo("")!=0 && objRol.getAcceso(id_rol, "compRetCompEditar"))){

                ResultSet rsTablaImpuestos = objTablaImpuesto.getTablaImpuestosVigente();

                out.println("<table width='740' cellpadding='0' cellspacing='0'>" +
                        "<tr><td align='center'> C O M P R O B A N T E &nbsp;&nbsp; D E &nbsp;&nbsp; R E T E N C I &Oacute; N</td>" +
                        "<td align='right' width='170'> Nro. <input type='text' id='sr' name='sr' size='6' maxlength='7' value='"+ret_num_serie+"' onkeypress=\"_evaluar(event, '0123456789-');\" />-" +
                        "<input type='text' size='6' id='nr' name='nr' value='"+ret_num_retencion+"' onkeypress=\"_numero(event);\" /></td></tr>"
                        + "<tr><td colspan='2' align='right'>Clave Acceso: <input type='text' size='49' id='clAc' name='clAc' value='"+clave_acceso+"' readonly />"
                        + "<br />Autorizaci&oacute;n: <input type='text' size='49' id='autr' name='autr' value='"+ret_autorizacion+"' readonly /></td></tr>" +
                        "</table>" +
                        
                        "<table width='740' cellpadding='0' cellspacing='0'>"
                        + "<tr><td>Fecha de Emisi&oacute;n: <input id='fre' name='fre' type='text' size='9' value='"+ret_fecha_emision+"' onkeypress=\"_SUP(event);\" />" +
                        "<input type='button' value='...' onClick=\"SelectorFecha.crear('fre', 'SQL');\" /></td>" +
                        "<td>Ejercicio Fiscal: " + DatosDinamicos.combo("efm", ret_ejercicio_fiscal_mes, ret_ejercicio_fiscal_mes1, "") + "<input id='ef' name='ef' type='text'size='4' maxlength='4' value='"+ret_ejercicio_fiscal+"' onkeypress=\"_numero(event)\" /></td></tr>" +
                        "<tr><td colspan='2'>Sustento Tributario: " + DatosDinamicos.combo("sustento_tributario", sustento_tributario, sustento_tributario1, "") + "</td></tr>" +
                        "</table>" +

                        "<div id='InRe' style='visibility:"+(( idFactura.compareTo("-1")!=0 && (estado_documento.compareTo("n")==0 || estado_documento.compareTo("")==0) ) ? "visible" : "hidden")+"'>"
                        + "<a href='javascript:void(0);' style='float:left;' onclick=\"inv_addRetencion();\">Insertar Retenci&oacute;n</a></div>"
                        + "<div style=\"display:none\" id='axBI'>"+ DatosDinamicos.combo(rsBasesImponibles, "9a0", "9b0", "", "inv_sumRetSubTotales(9d0,1);inv_sumRetTotales();", "inv_sumRetSubTotales(9d0,1);inv_sumRetTotales();", "", 165) +"</div>" +
                        "&nbsp;&nbsp;<div style=\"display:none\" id='axCR'>"+ DatosDinamicos.combo(rsTablaImpuestos, "9a0", "9b0", "inv_setMsg('axMsR');", "inv_setConcepto(9c0);inv_sumRetSubTotales(9d0,1);inv_sumRetTotales();", "_('9e0').focus();", "", 295) +"</div>" +
                        "<div style=\"display:none\" id='axC'>"+ objTablaImpuesto.getConceptoRetencion() +"</div>" +
                        "<table cellpadding='0' cellspacing='0'>" +
                        "<TH class='jm_TH' width='170'>CAMPO BASE IMPONIBLE</TH>" +
                        "<TH class='jm_TH' width='300'>IMPUESTO - C&Oacute;DIGO - % DE RETENCI&Oacute;N</TH>" +
                        "<TH class='jm_TH' width='120'>BASE IMPONIBLE</TH>" +
                        "<TH class='jm_TH' width='120'>VALOR RETENIDO</TH>" +
                        "<TH class='jm_TH' width='20'>&nbsp;&nbsp;</TH></tr></table>" +

                        "<DIV id='sll1' style='overflow:auto;width:760px;height:115px;'>" +
                        "<div class='tabla' id='tblR'>");

                try{
                    ResultSet retencionDetalle = objFactura.getRetencionDetalle(id_retencion_compra);
                    String codigo = "";
                    String id_tabla_impuesto = "";
                    i=0;
                    html = "";
                    while(retencionDetalle.next()){
                        codigo = (retencionDetalle.getString("codigo")!=null) ? retencionDetalle.getString("codigo") : "";
                        id_tabla_impuesto = (retencionDetalle.getString("id_tabla_impuesto")!=null) ? retencionDetalle.getString("id_tabla_impuesto") : "1";
                        html += "<div id='rRe"+i+"' class='fila'>";
                        html += "<div class='jm_columna caja' style='width:170px'>"+ DatosDinamicos.combo(rsBasesImponibles, "codi"+i, codigo, "", "inv_sumRetSubTotales("+i+",1);inv_sumRetTotales();", "inv_sumRetSubTotales("+i+",1);inv_sumRetTotales();", "", 165) +"</div>";
                        html += "<div class='jm_columna caja' style='width:300px'>" + DatosDinamicos.combo(rsTablaImpuestos, "idR"+i, id_tabla_impuesto, "inv_setMsg('axMsR');", "inv_setConcepto("+String.valueOf(i)+");", "inv_sumRetSubTotales("+i+");inv_sumRetTotales();_('bi"+i+"').focus();", "", 295) + "</div>";
                        html += "<div class='jm_columna der' style='width:120px'><input id='bi"+i+"' name='bi"+i+"' type='text' class='inpTbl' size='15' onkeypress=\"_evaluar(event, '0123456789.');\" onkeyup=\"inv_sumRetSubTotales("+i+");inv_sumRetTotales();\" " +
                                "onfocus=\"this.select();\" value='"+((retencionDetalle.getString("base_imponible")!=null) ? retencionDetalle.getString("base_imponible") : "")+"' /></div>";
                        html += "<div class='jm_columna der' style='width:120px'><input id='vr"+i+"' name='vr"+i+"' type='text' class='inpTbl' size='15' onkeypress=\"_evaluar(event, '0123456789.');\" onkeyup=\"inv_sumRetTotales();\" " +
                                "onfocus=\"this.select();\" value='"+((retencionDetalle.getString("valor_retenido")!=null) ? retencionDetalle.getString("valor_retenido") : "")+"' /></div>";
                        html += "<div class='jm_columna der' style='width:20px'><div class='jm_icoCon' style='background-position:-545px -18px;float:right;' title='Eliminar' onclick=\"_R('rRe"+i+"');inv_sumRetTotales();\">&nbsp;</div></div>";
                        html += "</div>";
                        i++;
                    }
                    retencionDetalle.close();
                }catch(Exception e){
                    e.printStackTrace();
                }

                out.print(html);


                out.println("</div></div>"+
                        "<table cellpadding='0' cellspacing='0'>" +
                        "<tr><td width='564' align='right'>TOTAL RETENCI&Oacute;N: </td>" +
                        "<td align='right'><span id='axMsR'></span> &nbsp;<input type='text' id='tvr' name='tvr' value='"+ret_impuesto_retenido+"' style='text-align:right' onkeypress=\"_evaluar(event, '0123456789.');\" /></td>" +
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
            out.println("<div id='tab12' class='jm_tabHTML' style='display:none;width:"+(_ancho)+"px;height:"+_altBody+"px;'>");

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
                String nc_valor = "0";
                String nc_desc = "0";
                String nc_iva_0 = "0";
                String nc_iva_12 = "0";
                String nc_ir = "0";
                String nc_id_plan_cuenta_ir = "58";
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
                    + "Autorizacion: <input type='text' size='10' id='ncAut' name='ncAut' value='"+nc_autorizacion_nota+"' onkeypress=\"_numero(event);\" /></td>"+
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
                            html += "<td width='220'>"+((facturaDetalle.getString("descripcion")!=null) ? facturaDetalle.getString("descripcion") : "")+"</td>";
                            html += "<td width='50' align='right'><input id='ncC"+i+"' name='ncC"+i+"' type='text' class='inpTbl' size='3' onkeypress=\"_evaluar(event, '0123456789.');\" "
                             + "onkeyup=\"fac_setDetNotaCredito();\" onfocus=\"this.select();\" value='"+cantidad+"' /></td>";
                             html += "<td width='60' align='right'><input id='ncPu"+i+"' name='ncPu"+i+"' type='text' class='inpTbl' size='5' readonly " +
                            "onfocus=\"this.select();\" value='"+ncPu+"' /></td>";
                            html += "<td width='70' align='right'><input id='ncST"+i+"' name='ncST"+i+"' type='text' class='inpTbl' size='5' onkeypress=\"_evaluar(event, '0123456789.');\" " +
                                    "onfocus=\"this.select();\" value='"+ncST+"' /></td>";
                            html += "<td width='60' align='right'><input id='ncDes"+i+"' name='ncDes"+i+"' type='text' class='inpTbl' size='5' onkeypress=\"_evaluar(event, '0123456789.');\" " +
                                    "onfocus=\"this.select();\" value='"+ncDes+"' /></td>";
                            html += "<td width='60' align='right'><input id='ncIv"+i+"' name='ncIv"+i+"' type='text' class='inpTbl' size='5' onkeypress=\"_evaluar(event, '0123456789.');\" " +
                                    "onfocus=\"this.select();\" value='"+ncIva+"' /></td>";
                            html += "<td width='80' align='right'><input id='ncT"+i+"' name='ncT"+i+"' type='text' class='inpTbl' size='6' onkeypress=\"_evaluar(event, '0123456789.');\" " +
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
                            html += "<td width='20' align='center'></td>";
                            html += "<td width='100' id='cF"+i+"0'>"+((rsNotaCreditoDetalle.getString("codigo")!=null) ? rsNotaCreditoDetalle.getString("codigo") : "")+"</td>";
                            html += "<td width='220'>"+((rsNotaCreditoDetalle.getString("descripcion")!=null) ? rsNotaCreditoDetalle.getString("descripcion") : "")+"</td>";
                            html += "<td width='40'>"+((rsNotaCreditoDetalle.getString("cantidad")!=null) ? rsNotaCreditoDetalle.getString("cantidad") : "")+"</td>";
                            html += "<td width='60' align='right'>"+((rsNotaCreditoDetalle.getString("p_u")!=null) ? rsNotaCreditoDetalle.getString("p_u") : "")+"</td>";
                            html += "<td width='70' align='right' id=\"ncST"+i+"\">"+((rsNotaCreditoDetalle.getString("p_st")!=null) ? rsNotaCreditoDetalle.getString("p_st") : "")+"</td>";
                            html += "<td width='60' align='right' id=\"ncDes"+i+"\">"+((rsNotaCreditoDetalle.getString("descuento")!=null) ? rsNotaCreditoDetalle.getString("descuento") : "")+"</td>";
                            html += "<td width='60' align='right' id=\"ncIva"+i+"\">"+((rsNotaCreditoDetalle.getString("iva")!=null) ? rsNotaCreditoDetalle.getString("iva") : "")+"</td>";
                            html += "<td width='80' align='right' id=\"ncT"+i+"\">"+((rsNotaCreditoDetalle.getString("total")!=null) ? rsNotaCreditoDetalle.getString("total") : "")+"</td>";
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
                    DatosDinamicos.combo(rsCuentas, "ncIdPLIR", nc_id_plan_cuenta_ir, "", 300)+"</td></tr>");

                    if(!anulado && id_nota_credito_compra.compareTo("")==0){
                        out.print("<tr class='jm_filaPar'><td colspan='2' align='right'><input type='button' value='Guardar nota de cr&eacute;dito' onclick=\"inv_notaCredito(_('frmCompra'))\" /></td></tr>");
                    }

                    out.print("</table>");

            }

            out.print("</div>");
*/


            /*           NOTAS DE DEBITO                */


/*
            out.println("<div id='tab13' class='jm_tabHTML' style='display:none;width:"+(_ancho)+"px;height:"+_altBody+"px;'>");

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
                    + "Autorizacion: <input type='text' size='10' id='ndAut' name='ndAut' value='"+nd_autorizacion_nota+"' onkeypress=\"_numero(event);\" /></td>"+
                    "</tr></table>" +

                    "<table width='740' cellpadding='0' cellspacing='0'>"+
                    "<tr><td>Fecha de emisi&oacute;n: </td>"+
                    "<td><input type='text' size='10' maxlength='10' id='ndFeEm' name='ndFeEm' value='"+nd_fecha_emision+"' readonly onkeypress=\"_numero(event);\" /><input type='button' value='...' onClick=\"SelectorFecha.crear('ndFeEm', 'SQL');\" /></td>" +
                    "<td>Comprobante que modifica: </td>"+
                    "<td><input type='text' size='30' maxlength='50' id='ndCoMo' name='ndCoMo' value='"+nd_modifica+"' readonly onkeypress=\"_numero(event);\" /></td>" +
                    "</tr>"+


                    "<tr><td colspan='4'>"+
                    "B&uacute;squeda de productos o servicios: &nbsp; <input type='text' id='busProd' onfocus=\"this.value=''\" style='width:350px;border-color:red' onkeyup=\"inv_getProductos(event, this.value, 'inv_tblProductos', 'nd');\" />" +
                    "</td>" +
                    "</tr>");

            out.print("</table>" +


                    "<table id='ndcab0' cellpadding='0' cellspacing='0'><tr>" +
                    "<TH class='jm_TH' width='230'>C&Oacute;DIGO - DESCRIPCI&Oacute;N</TH>" +
                    "<TH class='jm_TH' width='50'>CANT.</TH>" +
                    "<TH class='jm_TH' width='80'>P./U.</TH>" +
                    "<TH class='jm_TH' width='80'>SUBTOT.</TH>" +
                    "<TH class='jm_TH' width='70'>DESC.</TH>" +
                    "<TH class='jm_TH' width='70'>IVA</TH>" +
                    "<TH class='jm_TH' width='90'>TOTAL</TH>" +
                    "<TH class='jm_TH' width='20'>&nbsp;&nbsp;</TH></tr></table>" +

                    "<DIV id='ndsll0' style='overflow:auto;width:760px;height:120px;'>" +
                    "<div class='tabla' id='ndtblD'>");

                try{
                    i=0;
                    html="";
                    ResultSet rsNotaDebitoDetalle = objNotaDebito.getNotaDebitoDetalle(id_nota_debito_compra);
                    while(rsNotaDebitoDetalle.next()){
                        html += "<div id='r"+i+"' class='fila'>";
                        html += "<div class='columna caja' style='width:230px'>"+((rsNotaDebitoDetalle.getString("codigo")!=null) ? rsNotaDebitoDetalle.getString("codigo") : "")+" &nbsp; "
                                + ""+((rsNotaDebitoDetalle.getString("descripcion")!=null) ? rsNotaDebitoDetalle.getString("descripcion") : "")+"</div>";
                        html += "<div class='columna der' style='width:50px'>"+((rsNotaDebitoDetalle.getString("cantidad")!=null) ? rsNotaDebitoDetalle.getString("cantidad") : "")+"</div>";
                        html += "<div class='columna der' style='width:80px'>"+((rsNotaDebitoDetalle.getString("p_u")!=null) ? rsNotaDebitoDetalle.getString("p_u") : "")+"</div>";
                        html += "<div class='columna der' style='width:80px'>"+((rsNotaDebitoDetalle.getString("p_st")!=null) ? rsNotaDebitoDetalle.getString("p_st") : "")+"</div>";
                        html += "<div class='columna der' style='width:70px'>"+((rsNotaDebitoDetalle.getString("descuento")!=null) ? rsNotaDebitoDetalle.getString("descuento") : "")+"</div>";
                        html += "<div class='columna der' style='width:70px'>"+((rsNotaDebitoDetalle.getString("iva")!=null) ? rsNotaDebitoDetalle.getString("iva") : "")+"</div>";
                        html += "<div class='columna der' style='width:90px'>"+((rsNotaDebitoDetalle.getString("total")!=null) ? rsNotaDebitoDetalle.getString("total") : "")+"</div>";
                        html += "<div class='columna der' style='width:20px'>&nbsp;</div>";
                        html += "</div>";
                        i++;
                    }

                    out.print(html);

                    rsNotaDebitoDetalle.close();
                }catch(Exception e){
                    e.printStackTrace();
                }

                out.print("</div></div>");


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
                    out.print("<tr class='jm_filaPar'><td colspan='2' align='right'><input type='button' value='Guardar nota de d&eacute;bito' onclick=\"inv_notaDebito(_('frmCompra'))\" /></td></tr>");
                }
                out.print("</table>");
            }

            out.print("</div>");
*/




            out.println("<table class='jm_tabla' width='100%' cellspacing='0' cellpadding='0'><tr>");
            if(!anulado){
                //long fecha_sistema = Fecha.getTimeStamp(Fecha.getFecha("SQL"));
                //long fecha_asiento = Fecha.getTimeStamp(fecha);
                /*if(idFactura.compareTo("-1")!=0 && objRol.getAcceso(id_rol, "compRetCompImprimir") && i>0){
                    out.println("<td><input type='button' value='Imprimir Retenci&oacute;n' onclick=\"imprimir('pdfComprobanteRetencion?id="+id_retencion_compra+"&doc=f');\" /></td>");
                }
                if(idFactura.compareTo("-1")!=0 && (objRol.getAcceso(id_rol, "compRetCompAnular") || objRol.getAcceso(id_rol, "importaFactRetAnular")) && i>0 && fecha_sistema==fecha_asiento){
                    out.println("<td align='right'><input type='button' value='Anular Retenci&oacute;n' onclick='inv_retencionAnular("+idFactura+", "+id_retencion_compra+");' /></td>");
                }
*/
                if(idFactura.compareTo("-1")!=0 && (objRol.getAcceso(id_rol, "comprarActivosAnular") && contabilizado)){
                    out.println("<td><input type='button' value='Anular Factura' onclick='act_compraAnular("+idFactura+");' /></td>");
                }

                /*if(!objFactura.hayPagos(idFactura) && (idFactura.compareTo("-1")==0 ||
                  (idFactura.compareTo("-1")!=0 && (objRol.getAcceso(id_rol, "compraEditar") || objRol.getAcceso(id_rol, "importaFactEditar"))))){*/
                /*if(idFactura.compareTo("-1")==0){
                    out.println("<td align='right'><input type='submit' value='Guardar' onclick=\"_('imp').value=0;\" /></td>");
                    out.println("<td align='right'><input type='submit' value='Guardar e Imprimir Comprobante de Diario' onclick=\"_('imp').value=1;\" /></td>");
                }*/
                if(!contabilizado){
                    if(objRol.getAcceso(id_rol, "compraGuardar")){
                        out.println("<td><input type='submit' value='Guardar sin Contabilizar' onclick=\"_('imp').value=2;_('contabilizar').value=false;\" /></td>");
                    }
                    if(idFactura.compareTo("-1")!=0 && objRol.getAcceso(id_rol, "compraContabilizar")){
                        out.println("<td align='right'><input id='btn1' type='button' value='Contabilizar y/o Inventariar' onclick=\"_('imp').value=1;_('contabilizar').value=true;act_compraGuardar(_('frmCompra'));this.disabled=true;\" /></td>");
                    }
                    /*if(idFactura.compareTo("-1")==0 && objRol.getAcceso(id_rol, "compraContabilizar")){
                        out.println("<td align='right'><input type='button' value='Guardar, Contabilizar y/o Inventariar' onclick=\"_('imp').value=1;_('contabilizar').value=true;act_compraGuardar(_('frmCompra'));this.disabled=true;\" /></td>");
                    }*/
                }
                if( idFactura.compareTo("-1")!=0 && estado_documento.compareTo("n")==0){
                    out.println("<td align='right'>"
                            + "<input type='checkbox' id='genNewClave' name='genNewClave' /> <span class='marca'>Generar nueva clave de acceso </span>&nbsp;"
                            + "<input type='submit' value='Actualizar Retencio&oacute;n' onmousedown=\"_('frmCompra').action='frmRetencionActualizar';\" onclick=\"this.disabled=true;\" />"
                            + "</td>");
                }
            }else{
                out.println("<td><span class='marca'>FACTURA DE COMPRA DE ACTIVOS ANULADA</span></td>");
            }
            out.print("</tr></table>");



            out.print("</div></div></form>");

        } finally {
            objIva.cerrar();
            objBaseImponible.cerrar();
            objProducto.cerrar();
            objTablaImpuesto.cerrar();
            objBodega.cerrar();
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
