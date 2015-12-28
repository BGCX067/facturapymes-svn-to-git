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
import jm.web.DatosDinamicos;
import jm.web.Fecha;
import jm.seg.clas.Rol;
import jm.adm.clas.TablaImpuesto;
import jm.inv.clas.Producto;
import jm.fac.clas.Cliente;
import jm.fac.clas.FacturaVenta;
import java.sql.ResultSet;
import jm.adm.clas.Configuracion;
import jm.adm.clas.PuntoEmision;
import jm.adm.clas.Sucursal;
import jm.cnt.clas.Banco;
import jm.fac.clas.NotaCreditoVenta;
import jm.fac.clas.PreFactura;
//import jm.fac.clas.NotaDebitoVenta;
import jm.inv.clas.BaseImponible;
import jm.seg.clas.Usuario;

/**
 *
 * @author Jorge
 */
public class frmFacturaVenta extends HttpServlet {
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
        int id_punto_emision = (Integer)sesion.getAttribute("id_punto_emision");
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
        String _altBody = request.getParameter("altB");
        String idPreFactura = request.getParameter("kPrFa");

        FacturaVenta objFactura = new FacturaVenta(this._ip, this._puerto, this._db, usuario, clave);

        Cliente objCliente = new Cliente(this._ip, this._puerto, this._db, usuario, clave);
        
        BaseImponible objBaseImponible = new BaseImponible(this._ip, this._puerto, this._db, usuario, clave);

        TablaImpuesto objTablaImpuesto = new TablaImpuesto(this._ip, this._puerto, this._db, usuario, clave);
        Rol objRol = new Rol(this._ip, this._puerto, this._db, usuario, clave);

        Configuracion conf = new Configuracion(this._ip, this._puerto, this._db, usuario, clave);
        String dia_cortes_creditos = conf.getValor("dia_cortes_creditos");
        float p_iva0 = Integer.parseInt(conf.getValor("p_iva1"));
        conf.cerrar();

        Usuario objUsuario = new Usuario(this._ip, this._puerto, this._db, usuario, clave);
        boolean lector = objUsuario.lector(usuario);
        objUsuario.cerrar();
        
        Producto objProducto = new Producto(this._ip, this._puerto, this._db, usuario, clave);
        
        String id_nota_credito_venta = "";
        
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
        Sucursal objSucursal = new Sucursal(this._ip, this._puerto, this._db, usuario, clave);
        try{
            ResultSet rs = objSucursal.getSucursal(String.valueOf(id_sucursal));
            if(rs.next()){
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
            }
        }catch(Exception ex){
            ex.printStackTrace();
        }finally{
            objSucursal.cerrar();
        }


        String fac_num_serie = "";
        String fac_sec_desde = "";
        String fac_sec_hasta = "";
        String fac_cad_libretin = "";
        String fac_autorizacion = "";
        String id_plan_cuenta_caja = "";
        PuntoEmision objPuntoEmision = new PuntoEmision(this._ip, this._puerto, this._db, usuario, clave);
        try{
            ResultSet rs = objPuntoEmision.getPuntoEmision(String.valueOf(id_punto_emision));
            if(rs.next()){
                fac_num_serie = (rs.getString("fac_num_serie")!=null) ? rs.getString("fac_num_serie") : "";
                fac_sec_desde = (rs.getString("fac_sec_desde")!=null) ? rs.getString("fac_sec_desde") : "";
                fac_sec_hasta = (rs.getString("fac_sec_hasta")!=null) ? rs.getString("fac_sec_hasta") : "";
                fac_cad_libretin = (rs.getString("fac_cad_facturero")!=null) ? rs.getString("fac_cad_facturero") : "";
                fac_autorizacion = (rs.getString("fac_autorizacion")!=null) ? rs.getString("fac_autorizacion") : "";
                id_plan_cuenta_caja = (rs.getString("id_plan_cuenta_caja")!=null) ? rs.getString("id_plan_cuenta_caja") : "";
                rs.close();
            }
        }catch(Exception e){
            e.printStackTrace();
        }finally{
            objPuntoEmision.cerrar();
        }


        int i=0;
        try {
            String id_comprobante_diario = "";
            String id_sucursal_factura = "-1";
            String id_cliente = "1";
            String ruc = "9999999999999";
            String razon_social = "CONSUMIDOR FINAL";
            String telefono = "";
            String direccion = "";
            String forma_pago = "e";
            String banco = "";
            String num_cheque = "";
            String num_comp_pago = "";
            String gastos_bancos = "0";
            String num_cuenta = "";
            String id_plan_cuenta_banco = "";
            String serie_factura = fac_num_serie;
            String clave_acceso = "";
            String autorizacion = fac_autorizacion;
            long num_factura = objFactura.getNumFactura(id_punto_emision);
            String fecha_emision = Fecha.getFecha("SQL");
            String subtotal = "0";
            String subtotal_0 = "0";
            String subtotal_2 = "0";
            String iva_2 = "0";
            String iva_0 = "0";
            String descuento = "0";
            String total = "0";
            float deuda = 0;
            String observacion = "Venta de mercadería Fc. " + serie_factura + "-" + num_factura + " a CONSUMIDOR FINAL.";
            boolean anulado = false;
            String estado_documento = "g";

            String id_retencion_venta = "-1";
            String ret_num_serie = "001-001";
            String ret_autorizacion = "";
            String ret_num_retencion = "";
            String ret_fecha_emision = Fecha.getFecha("SQL");
            String ret_ejercicio_fiscal = String.valueOf(Fecha.getAnio());
            int mes = Fecha.getMes();
            String ret_ejercicio_fiscal_mes = mes<10 ? "0" + mes : String.valueOf(mes);
            String ret_impuesto_retenido = "";
            //String id_plan_cuenta_caja_banco = "";
            boolean ret_anulado = false;

            ResultSet facturaDetalle = null;

            String ocultar = "visible";

            ResultSet rsConsunidorFinal = objCliente.getConsumidorFinal();
            try{
                if(rsConsunidorFinal.next()){
                    id_cliente = rsConsunidorFinal.getString("id_cliente")!=null ? rsConsunidorFinal.getString("id_cliente") : "1";
                    ruc = rsConsunidorFinal.getString("ruc")!=null ? rsConsunidorFinal.getString("ruc") : "9999999999999";
                    razon_social = rsConsunidorFinal.getString("razon_social")!=null ? rsConsunidorFinal.getString("razon_social") : "CONSUMIDOR FINAL";
                }
                rsConsunidorFinal.close();
            }catch(Exception e){
                e.printStackTrace();
            }

            Banco objBanco = new Banco(this._ip, this._puerto, this._db, usuario, clave);
            ResultSet rsBancos = objBanco.getBancosIngresos();
            objBanco.cerrar();

            
            
            String id_instalacion="";
            String id_plan_actual = "-1";
            String txt_periodo = "";
            String plan = "";
            String id_producto = "-1";
            String id_producto_anticipo = "";
            if(idPreFactura.compareTo("undefined")!=0){
                PreFactura objPreFactura = new PreFactura(this._ip, this._puerto, this._db, usuario, clave);
                try{
                    ResultSet rsPreFactura = objPreFactura.getPreFactura(idPreFactura);
                    if(rsPreFactura.next()){
                        id_instalacion = (rsPreFactura.getString("id_instalacion")!=null) ? rsPreFactura.getString("id_instalacion") : "";
                        id_cliente = (rsPreFactura.getString("id_cliente")!=null) ? rsPreFactura.getString("id_cliente") : "";
                        
                        ruc = (rsPreFactura.getString("ruc")!=null) ? rsPreFactura.getString("ruc") : "";
                        razon_social = (rsPreFactura.getString("razon_social")!=null) ? rsPreFactura.getString("razon_social") : "";
                        direccion = (rsPreFactura.getString("direccion")!=null) ? rsPreFactura.getString("direccion") : "";
                        telefono = (rsPreFactura.getString("telefono")!=null) ? rsPreFactura.getString("telefono") : "";
                        /*subtotal = (rsPreFactura.getString("subtotal")!=null) ? rsPreFactura.getString("subtotal") : "0";
                        subtotal_0 = (rsPreFactura.getString("subtotal_0")!=null) ? rsPreFactura.getString("subtotal_0") : "0";
                        subtotal_2 = (rsPreFactura.getString("subtotal_2")!=null) ? rsPreFactura.getString("subtotal_2") : "0";
                        iva_2 = (rsPreFactura.getString("iva_2")!=null) ? rsPreFactura.getString("iva_2") : "0";
                        descuento = (rsPreFactura.getString("descuento")!=null) ? rsPreFactura.getString("descuento") : "0";*/
                        num_cuenta = (rsPreFactura.getString("num_cuenta")!=null) ? rsPreFactura.getString("num_cuenta") : "";
                        id_producto = (rsPreFactura.getString("id_producto")!=null) ? rsPreFactura.getString("id_producto") : "";
                        id_plan_actual = (rsPreFactura.getString("id_plan_actual")!=null) ? rsPreFactura.getString("id_plan_actual") : "-1";
                        //periodo = (rsPreFactura.getString("periodo")!=null) ? rsPreFactura.getString("periodo") : "";
                        id_producto_anticipo = (rsPreFactura.getString("id_producto_anticipo")!=null) ? rsPreFactura.getString("id_producto_anticipo") : "";
                        plan = (rsPreFactura.getString("plan")!=null) ? rsPreFactura.getString("plan") : "";
                        txt_periodo = (rsPreFactura.getString("txt_periodo")!=null) ? rsPreFactura.getString("txt_periodo") : "";
                        
                        observacion = "anticipo de servicio Internet plan "+plan+" Mbps del período "+txt_periodo+" con factura Nro. " + serie_factura + "-" + num_factura + " a " + razon_social;
                        
                        //desc_3_edad = (rsPreFactura.getString("desc_3_edad")!=null) ? rsPreFactura.getString("desc_3_edad") : "0";
                        /*carne_conadis = (rsPreFactura.getString("carne_conadis")!=null) ? rsPreFactura.getString("carne_conadis") : "";
                        edad = (rsPreFactura.getString("edad")!=null) ? rsPreFactura.getString("edad") : "";
                        
                        movil_claro = (rsPreFactura.getString("movil_claro")!=null) ? rsPreFactura.getString("movil_claro") : "";
                        movil_movistar = (rsPreFactura.getString("movil_movistar")!=null) ? rsPreFactura.getString("movil_movistar") : "";
                        
                        

                        id_sector = (rsPreFactura.getString("id_sector")!=null) ? rsPreFactura.getString("id_sector") : "";
                        //sector = (rsPreFactura.getString("sector")!=null) ? rsPreFactura.getString("sector") : "";
                        direccion_instalacion = (rsPreFactura.getString("direccion_instalacion")!=null) ? rsPreFactura.getString("direccion_instalacion") : "";
                        id_producto = (rsPreFactura.getString("id_producto")!=null) ? rsPreFactura.getString("id_producto") : "";
                        dias_conexion = (rsPreFactura.getString("dias_conexion")!=null) ? rsPreFactura.getString("dias_conexion") : "";
                        valor_internet = (rsPreFactura.getString("valor_internet")!=null) ? rsPreFactura.getString("valor_internet") : "";
                        iva_internet = (rsPreFactura.getString("iva_internet")!=null) ? rsPreFactura.getString("iva_internet") : "";
                        subt_internet = (rsPreFactura.getString("total_internet")!=null) ? rsPreFactura.getString("total_internet") : "";
                        //subt_internet = Addons.redondear(valor_internet / (p_iva1 / 100 + 1));
                        //iva_internet = Addons.redondear(valor_internet - subt_internet);
                        //periodo_suspension = (rsPreFactura.getString("periodo_suspension")!=null) ? rsPreFactura.getString("periodo_suspension") : "";
                        
                        meses_impagos = (rsPreFactura.getString("meses_impagos")!=null) ? rsPreFactura.getString("meses_impagos") : "0";
                        total_pagar = (rsPreFactura.getString("total")!=null) ? rsPreFactura.getString("total") : "0";
                        estado_servicio = (rsPreFactura.getString("estado_servicio")!=null) ? rsPreFactura.getString("estado_servicio") : "";*/
                        rsPreFactura.close();
                    }
                }catch(Exception e){
                    e.printStackTrace();
                }finally{
                    objPreFactura.cerrar();
                }
                
            }
            
            
            
            
            
            if(idFactura.compareTo("-1")!=0){
                try{
                    ResultSet rsFactura = objFactura.getFactura(idFactura);

                    if(rsFactura.next()){
                        id_comprobante_diario = (rsFactura.getString("id_comprobante_diario")!=null) ? rsFactura.getString("id_comprobante_diario") : "";
                        id_sucursal_factura = (rsFactura.getString("id_sucursal")!=null) ? rsFactura.getString("id_sucursal") : "-1";
                        id_cliente = (rsFactura.getString("id_cliente")!=null) ? rsFactura.getString("id_cliente") : "";
                        ruc = rsFactura.getString("ruc")!=null ? rsFactura.getString("ruc") : "";
                        razon_social = rsFactura.getString("razon_social")!=null ? rsFactura.getString("razon_social") : "";
                        telefono = (rsFactura.getString("telefono")!=null) ? rsFactura.getString("telefono") : "";
                        direccion = (rsFactura.getString("direccion")!=null) ? rsFactura.getString("direccion") : "";
                        forma_pago = (rsFactura.getString("forma_pago")!=null) ? rsFactura.getString("forma_pago") : "";
                        serie_factura = (rsFactura.getString("serie_factura")!=null) ? rsFactura.getString("serie_factura") : "001-001";
                        clave_acceso = (rsFactura.getString("clave_acceso")!=null) ? rsFactura.getString("clave_acceso") : "";
                        autorizacion = (rsFactura.getString("numero_autorizacion")!=null) ? rsFactura.getString("numero_autorizacion") : "";
                        num_factura = (rsFactura.getString("num_factura")!=null) ? rsFactura.getInt("num_factura") : 0;
                        fecha_emision = (rsFactura.getString("fecha_emision")!=null) ? Fecha.ISOaSQL(rsFactura.getString("fecha_emision")) : "";
                        subtotal = (rsFactura.getString("subtotal")!=null) ? rsFactura.getString("subtotal") : "0";
                        subtotal_2 = (rsFactura.getString("subtotal_2")!=null) ? rsFactura.getString("subtotal_2") : "0";
                        subtotal_0 = (rsFactura.getString("subtotal_0")!=null) ? rsFactura.getString("subtotal_0") : "0";
                        iva_2 = (rsFactura.getString("iva_2")!=null) ? rsFactura.getString("iva_2") : "0";
                        iva_0 = (rsFactura.getString("iva_0")!=null) ? rsFactura.getString("iva_0") : "0";
                        descuento = (rsFactura.getString("descuento")!=null) ? rsFactura.getString("descuento") : "0";
                        total = (rsFactura.getString("total")!=null) ? rsFactura.getString("total") : "0";
                        deuda = (rsFactura.getString("deuda")!=null) ? rsFactura.getFloat("deuda") : 0;
                        observacion = (rsFactura.getString("observacion")!=null) ? rsFactura.getString("observacion") : "";
                        anulado = (rsFactura.getString("anulado")!=null) ? rsFactura.getBoolean("anulado") : false;
                        estado_documento = (rsFactura.getString("estado_documento")!=null) ? rsFactura.getString("estado_documento") : "g";
                        rsFactura.close();
                        
                        try{
                            //ComprobanteIngreso objComprobanteIngreso = new ComprobanteIngreso(this._ip, this._puerto, this._db, usuario, clave);
                            //ResultSet compIngr = objComprobanteIngreso.getComprobanteIngreso(id_comprobante_ingreso);
                            ResultSet compIngr = objFactura.getCobroComprobante(idFactura);
                            if(compIngr.next()){
                                num_cheque = (compIngr.getString("num_cheque")!=null) ? compIngr.getString("num_cheque") : "";
                                banco = (compIngr.getString("banco")!=null) ? compIngr.getString("banco") : "";
                                num_comp_pago = (compIngr.getString("num_comp_pago")!=null) ? compIngr.getString("num_comp_pago") : "";
                                gastos_bancos = (compIngr.getString("gastos_bancos")!=null) ? compIngr.getString("gastos_bancos") : "";
                                id_plan_cuenta_banco = (compIngr.getString("id_plan_cuenta_banco")!=null) ? compIngr.getString("id_plan_cuenta_banco") : "";
                                compIngr.close();
                            }
                            //objComprobanteIngreso.cerrar();
                        }catch(Exception e){
                            e.printStackTrace();
                        }
                        
                    facturaDetalle = objFactura.getFacturaDetalleEdicion(idFactura, id_sucursal_factura); 
                    
                    }
                    
                }catch(Exception e){
                    e.printStackTrace();
                }
                

                try{
                    ResultSet rsRetencion = objFactura.getRetencion(idFactura);
                    if(rsRetencion.next()){
                        //ret_anulado = (rsRetencion.getString("anulado")!=null) ? rsRetencion.getBoolean("anulado") : false;
                        //if(!ret_anulado){
                            id_retencion_venta = (rsRetencion.getString("id_retencion_venta")!=null) ? rsRetencion.getString("id_retencion_venta") : "-1";
                            ret_num_serie = (rsRetencion.getString("ret_num_serie")!=null) ? rsRetencion.getString("ret_num_serie") : "001-001";
                            ret_autorizacion = (rsRetencion.getString("ret_autorizacion")!=null) ? rsRetencion.getString("ret_autorizacion") : "";
                            ret_num_retencion = (rsRetencion.getString("ret_num_retencion")!=null) ? rsRetencion.getString("ret_num_retencion") : "";
                            ret_fecha_emision = (rsRetencion.getString("ret_fecha_emision")!=null) ? Fecha.ISOaSQL(rsRetencion.getString("ret_fecha_emision")) : Fecha.getFecha("SQL");
                            ret_ejercicio_fiscal = (rsRetencion.getString("ret_ejercicio_fiscal")!=null) ? rsRetencion.getString("ret_ejercicio_fiscal") : ret_ejercicio_fiscal;
                            ret_ejercicio_fiscal_mes = (rsRetencion.getString("ret_ejercicio_fiscal_mes")!=null) ? rsRetencion.getString("ret_ejercicio_fiscal_mes") : ret_ejercicio_fiscal_mes;
                            ret_impuesto_retenido = (rsRetencion.getString("ret_impuesto_retenido")!=null) ? rsRetencion.getString("ret_impuesto_retenido") : "";
                            //id_plan_cuenta_caja_banco = (rsRetencion.getString("id_plan_cuenta_caja_banco")!=null) ? rsRetencion.getString("id_plan_cuenta_caja_banco") : "";
                        //}
                        rsRetencion.close();
                    }
                }catch(Exception e){
                    e.printStackTrace();
                }
            }

            String cobro = (forma_pago.compareTo("c")==0) ? "block" : "none";
            String cobro_dep_tran = (forma_pago.compareTo("p")==0 || forma_pago.compareTo("t")==0 || forma_pago.compareTo("j")==0) ? "block" : "none";
            String cobro_trans = (forma_pago.compareTo("c")==0 || forma_pago.compareTo("t")==0 || forma_pago.compareTo("j")==0) ? "block" : "none";


            String [][] forma_pago1 = {{"e","Efectivo"},{"c","Cheque"},{"p","Dep&oacute;sito bancario"},
                {"h","Cash Management Pichincha"},
                {"t","Transferencia bancaria"},{"j","Tarjeta de cr&eacute;dito"},{"d","Cr&eacute;dito"}};

            String [][] ret_ejercicio_fiscal_mes1 = {{"01","Enero"},{"02","Febrero"},{"03","Marzo"},{"04","Abril"},{"05","Mayo"},{"06","Junio"},
                {"07","Julio"},{"08","Agosto"},{"09","Septiembre"},{"10","Octubre"},{"11","Noviembre"},{"12","Diciembre"}};

            out.print("obj»cmp_html^fun»_objTab.seleccionar(0);^foc»busProd^frm»");

            out.print("<div class='grupoTab' style='width:770px;'><div id='tabR0'>"
                    + "<a id='tab00' href='javascript:void(0);' class='jm_tabAct' onclick='_objTab.seleccionar(0);'>Factura</a>");
            
            //if(idFactura.compareTo("-1")!=0 && (ret_num_retencion.compareTo("")==0 && objRol.getAcceso(id_rol, "compRetVentaNueva")) || (ret_num_retencion.compareTo("")!=0 && objRol.getAcceso(id_rol, "compRetVentaEditar"))){
            if(!ret_anulado){
                out.print("<a id='tab01' href='javascript:void(0);' class='jm_tab' onclick='_objTab.seleccionar(1);'>Retenci&oacute;n</a>");
            }
            if(idFactura.compareTo("-1")!=0 && ruc.compareTo("9999999999999")!=0){
                if(deuda == 0){
                    out.print("<a id='tab02' href='javascript:void(0);' class='jm_tab' onclick='_objTab.seleccionar(2);'>Nota Cr&eacute;dito</a>");
                }
                //out.print("<a id='tab03' href='javascript:void(0);' class='jm_tab' onclick='_objTab.seleccionar(3);'>Nota D&eacute;bito</a>");
            }
            out.print("</div>");

            out.print("<form id='formVenta' action='frmFacturaVentaGuardar' onsubmit='return fac_ventaGuardar(this)' autocomplete='off'>");
            out.print("<input type='hidden' id='idFactura' name='idFactura' value='"+idFactura+"' />"
                    + "<input type='hidden' id='idCD' name='idCD' value='"+id_comprobante_diario+"' />"
                    + "<input type='hidden' id='idRV' name='idRV' value='"+id_retencion_venta+"' />"
                    + "<input type='hidden' id='tf' name='tf' value='p' />"                          /* tipo de factura  */
                    + "<input type='hidden' id='idCl' name='idCl' value='"+id_cliente+"' />"
                    + "<input type='hidden' id='secDe' name='secDe' value='"+fac_sec_desde+"' />"
                    + "<input type='hidden' id='secHa' name='secHa' value='"+fac_sec_hasta+"' />"
                    + "<input type='hidden' id='feCad' name='feCad' value='"+fac_cad_libretin+"' />"
                    
                    + "<input type='hidden' id='ncSecDe' name='secDe' value='"+not_cre_sec_desde+"' />"
                    + "<input type='hidden' id='ncSecHa' name='secHa' value='"+not_cre_sec_hasta+"' />"
                    + "<input type='hidden' id='ncFeCad' name='feCad' value='"+not_cre_cad_libretin+"' />"
                    
                    + "<input type='hidden' id='ndSecDe' name='secDe' value='"+not_deb_sec_desde+"' />"
                    + "<input type='hidden' id='ndSecHa' name='secHa' value='"+not_deb_sec_hasta+"' />"
                    + "<input type='hidden' id='ndFeCad' name='feCad' value='"+not_deb_cad_libretin+"' />"
                    
                    + "<input type='hidden' id='id_instalacion' name='id_instalacion' value='"+id_instalacion+"' />"
                    + "<input type='hidden' id='idPreFactura' name='idPreFactura' value='"+idPreFactura+"' />"
                    + "<input type='hidden' id='utSuc' name='utSuc' value='0' />"
                    + "<input type='hidden' id='utCli' name='utCli' value='-1' />"
                    //+ "<input type='hidden' id='p_iva1' name='p_iva1' value='"+p_iva1+"' />"
                    + "<input type='hidden' id='son' name='son' value='' />"
                    + "<input type='hidden' id='tope' name='tope' value='0' />"
                    + "<input type='hidden' id='saldo_doc' name='saldo_doc' value='"+total+"' />"
                    + "<input type='hidden' id='num_cuenta' name='num_cuenta' value='"+num_cuenta+"' />"
                    + "<input type='hidden' id='subtotal_ant' name='subtotal_ant' value='"+subtotal+"' />"
                    + "<input type='hidden' id='iva_2_ant' name='iva_2_ant' value='"+iva_2+"' />"
                    + "<input type='hidden' id='total_ant' name='total_ant' value='"+total+"' />"
                    + "<input type='hidden' id='forma_pago_ant' name='forma_pago_ant' value='"+forma_pago+"' />"
                    + "<input type='hidden' id='elect' name='elect' value='0' />"
                    + "<input type='hidden' id='imp' name='imp' value='1' />");

            out.print("<div id='tabR1'>");

            /* primer tab */

            
            out.print("<div id='tab10' class='jm_tabHTML' style='display:block;width:760px;height:"+_altBody+"px;'>");

            out.print("<div style=\"display:none\" id='axCl'></div>" +  /* pongo los datos del cliente buscado */
                    "<table width='100%' cellpadding='0' cellspacing='0'>" +
                    "<tr><td align='right'>F A C T U R A &nbsp;&nbsp; D E &nbsp;&nbsp; V E N T A </td>" +
                    "<td align='right'>Nro. <span class='marca'>*</span> "+ 
                    "<input type='text' id='sf' name='sf' size='6' maxlength='7' value='"+serie_factura+"' readonly onkeypress=\"_evaluar(event, '0123456789-');\" onkeyup='fac_setDetalle()' />-" +
                    "<input type='text' size='6' id='nf' name='nf' value='"+num_factura+"' onkeypress=\"_evaluar(event, '0123456789-');\" onkeyup='fac_setDetalle()' "+(idFactura.compareTo("-1")==0 ? "readonly" : "")+" /></td></tr>"+
                    "<tr><td colspan='2' align='right'>Clave Acceso: <input type='text' size='49' id='clAc' name='clAc' value='"+clave_acceso+"' readonly />"
                    + "<br />Autorizaci&oacute;n: <input type='text' size='49' id='auf' name='auf' value='"+autorizacion+"' readonly /></td>"
                    + "</tr></table>");

            out.print("<table cellpadding='0' cellspacing='0'>"+
                    "<tr><td width='110'>CC/RUC: <span class='marca'>*</span></td>"+
                    "<td width='263'><div style='float:left;display:table-cell'><input type='text' size='13' maxlength='13' id='ruc' name='ruc' value='"+ruc+"' class='may' readonly onkeydown='var c=(_esN)?event.which:event.keyCode;if(c==27){fac_setConFinal();}' onkeypress='_DNI(event)' /></div>");
                    /*+ "<div style='display:table-cell'><input type='checkbox' id='vf' checked='checked' /> verificar &nbsp;&nbsp; </div>"*/
                    
                    if(idFactura.compareTo("-1")==0){
                        out.print("<div style='float:left;display:table-cell'> &nbsp;&nbsp; </div>"
                        + "<div class='jm_icoCon' style='background-position:-524px -14px;' title='Buscar cliente' onclick=\"fac_getFrmCliente()\">&nbsp;</div>"
                        + "<div style='float:left;display:table-cell'> &nbsp;&nbsp;&nbsp;&nbsp; </div>"
                        + "<div class='jm_icoCon' style='background-position:-291px -138px;' title='Nuevo cliente' onclick=\"fac_addCliente(false)\">&nbsp;</div>");
                    }

            out.print("</td>"+ 
                    "<td width='110'>Fecha: </td>"+
                    "<td width='263'><input type='text' size='10' maxlength='10' id='fe' name='fe' value='"+fecha_emision+"' readonly /></td>" +
                    "</tr>" +

                    "<tr><td>Se&ntilde;or(es): <span class='marca'>*</span></td>"+
                    "<td><input type='text' size='40' maxlength='100' id='raSo' class='may' name='raSo' value='"+razon_social+"' readonly onkeypress=\"_alfanumerico(event);\" /></td>" +
                    "<td>Tel&eacute;fono: </td>"+
                    "<td><input type='text' size='10' maxlength='30' id='tel' name='tel' value='"+telefono+"' readonly onkeypress=\"_numero(event);\" /></td>" +
                    "</tr>" +

                    "<tr><td>Direcci&oacute;n: </td>"+
                    "<td><input type='text' size='40' maxlength='80' id='dir' class='may' name='dir' value='"+direccion+"' readonly onkeypress=\"_alfanumerico(event);\" /></td>" +
                    "<td>Forma de pago: </td>"+
                    "<td>" + DatosDinamicos.combo("foPa", forma_pago, forma_pago1, "cnt_setFormaPago(this);") + "</td>" +
                    "</tr>"+ 
                    
                    "<tr><td colspan='4'>"+

                    "<div class='jm_tabla' style='background-color:#FFFFFF'>"+

                    "<div id='pCh' class='jm_fila' style=\"display:"+cobro+";\">"+
                        "<div class='jm_columna' style='width:108px'>Banco: <span class='marca'>*</span></div><div class='jm_columna' style='width:260px'><input id='bco' name='bco' type='text' size='20' maxlength='50' value='"+banco+"' onkeypress=\"_letra(event);\" /></div>" +
                        "<div class='jm_columna' style='width:108px'>Cheque #: <span class='marca'>*</span></div><div class='jm_columna'><input id='chq' name='chq' value='"+num_cheque+"' type='text' size='20' maxlength='30' onkeypress=\"_numero(event);\"/></div>" +
                    "</div>"+

                    /*  cuando la forma de pago es transferencia, deposito  */
                    "<div class='jm_fila'>"+
                        "<div id='pOtr' class='jm_columna' style=\"width:370px;display:"+cobro_dep_tran+";\">No. de comprobante de pago: <span class='marca'>*</span> <input id='ncp' name='ncp' type='text' size='20' maxlength='20' value='"+num_comp_pago+"' "
                        + "onkeydown=\"_NoE()\" onkeypress=\"_numero(event);\" onkeyup=\"var c=(_esN)?event.which:event.keyCode;if(c==13){fac_getSaldoDoc(this.value);}\" onblur=\"fac_getSaldoDoc(this.value);\" /></div>" +
                        "<div id='pTra' class='jm_columna' style='display:"+cobro_dep_tran+";'>Gastos bancarios: <span class='marca'>*</span> &nbsp;&nbsp; <input id='gb' name='gb' type='text' size='10' value='"+gastos_bancos+"' onkeypress=\"_evaluar(event, '0123456789.');\" /></div>"+
                    "</div>" +
                    
                    "<div class='jm_fila'>"
                    + "<div class='jm_columna' id='axDocs'></div>"
                    + "</div>" +

                    "<div id='pBan' class='jm_fila' style='display:"+cobro_trans+";'>"+
                        "<div class='jm_columna' style='width:108px'>Cuenta bancaria:<span class='marca'>*</span> &nbsp;</div><div class='jm_columna'>"+ DatosDinamicos.combo(rsBancos, "idPcBc", id_plan_cuenta_banco, "")+"</div>"+
                    "</div>" +
                    "</div>"+

                    "</td></tr>" +

                    "<tr><td colspan='4'>");
                    if(estado_documento.compareTo("a")!=0){
                        out.print("<div id='InAr'>B&uacute;squeda de productos o servicios: &nbsp; <input type='text' id='busProd' onfocus=\"this.value=''\" style='width:300px;border-color:red' onkeypress=\"_NoE(event)\" onkeyup=\"fac_getProductos(event, this.value, 'fac_tblProductos', '', 700);\" />"
                                + " &nbsp; Lector de c&oacute;digo de barras <input type='checkbox' id='lector' "+(lector ? "checked='checked'" : "")+" onclick='inv_setLector()' /></div>" +
                        "&nbsp;&nbsp;<div style=\"display:none\" id='ax'></div>");
                    }
            out.print("</td></tr>" +
                    "</table>" +

                    "<DIV style='overflow:auto;width:733px;height:175px;'>"+
                    "<table cellpadding='0' cellspacing='0' width='850'><tr>" +
                    "<TH class='jm_TH' width='100'>C&Oacute;DIGO</TH>" +
                    "<TH class='jm_TH' width='225'>DESCRIPCI&Oacute;N</TH>" +
                    "<TH class='jm_TH' width='60'>CANT.</TH>" +
                    "<TH class='jm_TH' width='60'>P./U.</TH>" +
                    "<TH class='jm_TH' width='70'>SUBTOT.</TH>" +
                    "<TH class='jm_TH' width='60'>DESC.</TH>" +
                    "<TH class='jm_TH' width='60'>IVA</TH>" +
                    "<TH class='jm_TH' width='80'>TOTAL</TH>" +
                    "<TH class='jm_TH' width='100'>PRECIO</TH>" +
                    "<TH class='jm_TH' width='20'>&nbsp;&nbsp;</TH></tr></table>" +

                    "<DIV id='sll0' style='overflow:auto;width:880px;height:140px;'>" +
                    "<div class='jm_tabla' id='tblD'>");

            String html="";
            
            
            
            if(idPreFactura.compareTo("undefined")!=0){
                String codigo = "";
                String stk0 = "0";
                String p_c0 = "0";
                //String p_iva0 = "12";
                String p_v0 = "0";
                String p_des0 = "0";
                String d_srv0 = "~";
                String ut_min0 = "0";
                ResultSet rsAnticipo = objProducto.getProductoAnticipo(id_producto_anticipo);
                String jsAnticipo = objProducto.getJSON(rsAnticipo);
                try{
                    ResultSet rsProd = objProducto.getProductoISP(id_sucursal, id_producto);
                    if(rsProd.next()){
                        codigo = rsProd.getString("codigo")!=null ? rsProd.getString("codigo") : "";
                        stk0 = rsProd.getString("stock_sucursal")!=null ? rsProd.getString("stock_sucursal") : "0";
                        p_c0 = rsProd.getString("precio_costo")!=null ? rsProd.getString("precio_costo") : "0";
                        //t_iva0 = rsProd.getString(5)!=null ? rsProd.getString(5) : "~";
                        p_v0 = rsProd.getString("precio_venta")!=null ? rsProd.getString("precio_venta") : "0";
                        p_des0 = rsProd.getString("descuento")!=null ? rsProd.getString("descuento") : "0";
                        //d_srv0 = rsProd.getString(8)!=null ? rsProd.getString(8) : "~";
                        ut_min0 = rsProd.getString("ut_min")!=null ? rsProd.getString("ut_min") : "0";
                        rsProd.close();
                    }
                }catch(Exception e){
                    e.printStackTrace();
                }
                
                html += "<div class='jm_fila' id='r0'>"
                     +"<div class='jm_columna caja' style='width:100px;height:38px;' id='cF00'>"+codigo+"</div>" +
                        "<div class='jm_columna caja' style='width:225px;height:38px;'><textarea id='cpt0' name='cpt0' style='width:215px;' rows='1'>ANTICIPO DE SERVICIO DE INTERNET PLAN "+plan+" Mbps PERIODO FACTURADO "+txt_periodo+" ~</textarea>"+
                        "<input type='hidden' id='idPr0' name='idPr0' value='"+id_producto+"' />"+
                        "<input type='hidden' id='stk0' name='stk0' value='"+stk0+"' />"+
                        "<input type='hidden' id='p_c0' name='p_c0' value='"+p_c0+"' />"+
                        "<input type='hidden' id='p_iva0' value='"+p_iva0+"' />"+
                        "<input type='hidden' id='p_v0' value='"+p_v0+"' />"+
                        "<input type='hidden' id='p_des0' value='"+p_des0+"' />"+
                        "<input type='hidden' id='d_srv0' value='"+d_srv0+"' />"+
                        "<input type='hidden' id='ut_min0' value='"+ut_min0+"' />"+
                        "<div id='util0' style='display:none'></div></div>"+
                        "<div class='jm_columna caja' style='width:60px;height:38px;'><input type='text' onkeypress=\"_evaluar(event,'0123456789.')\" size='5' id='c0' name='c0' value='1' onkeyup=\"fac_calcTotales('', 0)\" /></div>"+
                        "<div class='jm_columna der' style='width:60px;height:38px;'><input type='text' onkeypress=\"_evaluar(event,'0123456789.')\" size='6' id='pu0' name='pu0' style='text-align:right' value='0' onkeyup=\"fac_calcTotales('', 0, 1)\" onblur=\"fac_calcTotales('', 0, 2)\" /></div>" +
                        "<div class='jm_columna der' style='width:70px;height:38px;'><input type='text' size='5' id='st0' name='st0' readonly style='text-align:right' value='0' /></div>" +
                        "<div class='jm_columna der' style='width:60px;height:38px;'><input type='text' size='5' id='d0' name='d0' value='0' readonly style='text-align:right' /></div>" +
                        "<div class='jm_columna der' style='width:60px;height:38px;'><input type='text' size='5' id='v0' name='v0' value='0' readonly style='text-align:right' /></div>" +
                        "<div class='jm_columna der' style='width:80px;height:38px;'><input type='text' size='8' id='t0' name='t0' style='text-align:right' value='0' readonly /></div>"+
                        "<div class='jm_columna der' style='width:100px;height:38px;' id='axLiPr0'></div>"+
                        "<div class='jm_columna der' style='width:20px;height:38px;'><div id='e0'>&nbsp;</div></div>"
                        + "</div>";
            }
            
            
            
            try{
                String clase = "";
                i=0;
                while(facturaDetalle.next()){
                    id_producto = (facturaDetalle.getString("id_producto")!=null) ? facturaDetalle.getString("id_producto") : "";
                    //String stock_sucursal = (facturaDetalle.getString("stock_sucursal")!=null) ? facturaDetalle.getString("stock_sucursal") : "0";
                    String precio_costo = (facturaDetalle.getString("precio_costo")!=null) ? facturaDetalle.getString("precio_costo") : "0";
                    String porcentaje_iva = (facturaDetalle.getString("porcentaje_iva")!=null) ? facturaDetalle.getString("porcentaje_iva") : "12";
                    String cant = (facturaDetalle.getString("cantidad")!=null) ? facturaDetalle.getString("cantidad") : "0";
                    String deta = (facturaDetalle.getString("descripcion_mas")!=null) ? facturaDetalle.getString("descripcion_mas") : "";
                    String p_u = (facturaDetalle.getString("p_u")!=null) ? facturaDetalle.getString("p_u") : "0";
                    String p_st = (facturaDetalle.getString("p_st")!=null) ? facturaDetalle.getString("p_st") : "0";
                    String p_descuento =  (facturaDetalle.getString("p_descuento")!=null) ? facturaDetalle.getString("p_descuento") : "0";
                    String desc =  (facturaDetalle.getString("descuento")!=null) ? facturaDetalle.getString("descuento") : "0";
                    String iva = (facturaDetalle.getString("iva")!=null) ? facturaDetalle.getString("iva") : "0";
                    String tot = (facturaDetalle.getString("total")!=null) ? facturaDetalle.getString("total") : "0";
                    //String de_servicio = (facturaDetalle.getString("de_servicio")!=null) ? facturaDetalle.getString("de_servicio") : "~";
                    String costo_minimo = (facturaDetalle.getString("costo_minimo")!=null) ? facturaDetalle.getString("costo_minimo") : "0";
                    //String id_lista_precio = (facturaDetalle.getString("id_lista_precio")!=null) ? facturaDetalle.getString("id_lista_precio") : "0";
                    
                    ResultSet rsLiPr = objProducto.getPrecios(id_producto);
                    clase = i%2==0 ? "jm_filaPar" : "jm_filaImp";
                    
                    html += "<div id='r"+i+"' class='jm_fila "+clase+"'>";
                    html += "<div class='jm_columna caja' style='width:100px' id='cF"+i+"0'>"+((facturaDetalle.getString("codigo")!=null) ? facturaDetalle.getString("codigo") : "")+"</div>";
                    html += "<div class='jm_columna caja' style='width:225px'><textarea id='cpt"+i+"' name='cpt"+i+"' style='width:215px;' rows='1'>"+deta+"</textarea>"+
                        "<input type='hidden' id='idPr"+i+"' name='idPr"+i+"' value='"+id_producto+"' />"+
                        "<input type='hidden' id='stk"+i+"' name='stk"+i+"' value='1' />"+
                        "<input type='hidden' id='p_c"+i+"' name='p_c"+i+"' value='"+precio_costo+"' />"+
                        "<input type='hidden' id='p_iva"+i+"' value='"+porcentaje_iva+"' />"+
                        "<input type='hidden' id='p_v"+i+"' value='"+p_u+"' />"+
                        "<input type='hidden' id='p_des"+i+"' value='"+p_descuento+"' />"+
                        "<input type='hidden' id='d_srv"+i+"' value='0' />"+
                        "<input type='hidden' id='ut_min"+i+"' value='"+costo_minimo+"' />"+
                        "<div id='util"+i+"' style='display:none'></div></div>";
                    html += "<div class='jm_columna caja' style='width:60px'><input type='text' onkeypress=\"_evaluar(event,'0123456789.')\" size='5' id='c"+i+"' name='c"+i+"' value='"+cant+"' onkeyup=\"fac_calcTotales('', "+i+")\" /></div>";
                    html += "<div class='jm_columna der' style='width:60px'><input type='text' onkeypress=\"_evaluar(event,'0123456789.')\" size='6' id='pu"+i+"' name='pu"+i+"' value='"+p_u+"' style='text-align:right' onkeyup=\"fac_calcTotales('', "+i+", 1)\" onblur=\"fac_calcTotales('', "+i+", 2)\" /></div>";
                    html += "<div class='jm_columna der' style='width:70px'><input type='text' size='5' id='st"+i+"' name='st"+i+"' value='"+p_st+"' readonly style='text-align:right' /></div>";
                    html += "<div class='jm_columna der' style='width:60px'><input type='text' size='5' id='d"+i+"' name='d"+i+"' value='"+desc+"' style='text-align:right' onkeyup=\"fac_setDescuento('', "+i+")\" /></div>";
                    html += "<div class='jm_columna der' style='width:60px'><input type='text' size='5' id='v"+i+"' name='v"+i+"' value='"+iva+"' readonly style='text-align:right' /></div>";
                    html += "<div class='jm_columna der' style='width:80px'><input type='text' size='8' id='t"+i+"' name='t"+i+"' value='"+tot+"' style='text-align:right' onkeyup=\"inv_calcSUBT('', "+i+");fac_sumValores('', "+i+");\" onblur=\"fac_verifUtil('', "+i+")\" /></div>";
                    //html += "<div class='jm_columna der' style='width:100px;height:38px;' id='axLiPr"+i+"'>"+DatosDinamicos.combo(rsLiPr, "LiPr"+i, id_lista_precio, "fac_calcTotales('', "+i+")", 96)+"</div>";
                    html += "<div class='jm_columna der' style='width:20px;height:38px;'><div class='jm_icoCon' id='e"+i+"' style='background-position:-545px -18px;' onclick=\"_R('r"+i+"');fac_sumValores('');\" title='eliminar'>&nbsp;</div></div>";
                    html += "</div>";
                    i++;
                }
            }catch(Exception e){
                e.printStackTrace();
            }

            out.print(html);

            out.print("</div></div></div>" +
                    "<table width='710' cellpadding='0' cellspacing='0'>" +
                    "<tr><td width='480'> Concepto .- <span class='marca'>*</span></td>" +
                    "<td width='80'>SUBTOTAL: <span class='marca'>*</span> </td>" +
                    "<td align='right' width='120'> <input type='text' id='subt' name='subt' size='15' value='"+subtotal+"' style='text-align:right' readonly /></td></tr>" +

                    "<tr><td rowspan='5' valign='top'><textarea id='ob' name='ob' cols='40' rows='3' onblur=\"this.value=this.value._trim();\">"+observacion+"</textarea></td>" +
                    "<td>SUBTOTAL 12%: </td>" +
                    "<td align='right' class='jm_filaPar'> <input type='text' id='subt2' name='subt2' size='15' value='"+subtotal_2+"' style='text-align:right' readonly /></td></tr>" +
                    
                    "<tr><td>SUBTOTAL 0%: </td>" +
                    "<td align='right' class='jm_filaPar'> <input type='text' id='subt0' name='subt0' size='15' value='"+subtotal_0+"' style='text-align:right' readonly /></td></tr>" +
                    
                    "<tr><td>DESCUENTO: </td>" +
                    "<td align='right' class='jm_filaPar'> <input type='text' id='dt' name='dt' size='15' value='"+descuento+"' style='text-align:right' readonly /></td></tr>" +

                    "<tr><td>12% IVA: </td>" +
                    "<td align='right' class='jm_filaPar'> <input type='text' id='iva2' name='iva2' size='15' value='"+iva_2+"' style='text-align:right' readonly /></td></tr>" +

                    "<tr><td>TOTAL: <span class='marca'>*</span> </td>" +
                    "<td align='right' class='jm_filaPar'> <input type='text' id='tt' name='tt' size='15' value='"+total+"' style='text-align:right' readonly /></td></tr>");

            out.println("</table>");
            out.print("</div>");




            
            /*  COMPROBANTE DE RETENCION  */



            /*String bancos[][] = DatosDinamicos.ResultSetToMatriz(rsBancos);
            int filas = bancos.length+1;
            String caja_bancos[][] = new String[filas][2];
            caja_bancos[0][0] = "";
            caja_bancos[0][1] = "";
            if(id_plan_cuenta_caja.compareTo("")!=0){
                caja_bancos[0][0] = id_plan_cuenta_caja;
                caja_bancos[0][1] = "Caja";
            }
            for(int x=0; x<bancos.length; x++){
                caja_bancos[x+1][0] = bancos[x][0];
                caja_bancos[x+1][1] = bancos[x][1];
            }*/
            

            boolean tiene_retencion = false;
            //if(idFactura.compareTo("-1")!=0 && (ret_num_retencion.compareTo("")==0 && objRol.getAcceso(id_rol, "compRetVentaNueva")) || (ret_num_retencion.compareTo("")!=0 && objRol.getAcceso(id_rol, "compRetVentaEditar"))){

                out.println("<div id='tab11' class='jm_tabHTML' style='display:none;width:760px;height:"+_altBody+"px;'>");
                
                ResultSet rsBasesImponibles = objBaseImponible.getBasesImponibles();
                ResultSet rsTablaImpuestos = objTablaImpuesto.getTablaImpuestosVigente();

                out.println("<table width='740' cellpadding='0' cellspacing='0'>" +
                        "<tr><td width='300'></td><td colspan='2'><span id='axMsR'></span></td></tr>"+
                        "<tr><td align='center' colspan='2'> C O M P R O B A N T E &nbsp;&nbsp; D E &nbsp;&nbsp; R E T E N C I &Oacute; N</td>" +
                        "<td align='right'> Nro. <input type='text' id='sr' name='sr' size='6' maxlength='7' value='"+ret_num_serie+"' onkeypress=\"_evaluar(event, '0123456789-');\" />-" +
                        "<input type='text' size='6' id='nr' name='nr' value='"+ret_num_retencion+"' onkeypress=\"_numero(event);\" /></td></tr>" +

                        "<tr><td>Fecha de Emisi&oacute;n: <input id='fer' name='fer' type='text' size='9' value='"+ret_fecha_emision+"' onkeypress=\"_SUP(event);\" />" +
                        "<input type='button' value='...' onClick=\"SelectorFecha.crear('fer', 'SQL');\" /></td>" +
                        "<td>Ejercicio Fiscal: " + DatosDinamicos.combo("efm", ret_ejercicio_fiscal_mes, ret_ejercicio_fiscal_mes1, "") + "<input id='ef' name='ef' type='text'size='4' maxlength='4' value='"+ret_ejercicio_fiscal+"' onkeypress=\"_numero(event)\" /></td>" +
                        "<td align='right'>Autorizaci&oacute;n: <input type='text' size='10' maxlength='39' id='aur' name='aur' value='"+ret_autorizacion+"' onkeypress=\"_numero(event);\" /></td></tr>" +
                        
                        //"<tr><td colspan='3'>Aplicaci&oacute;n contable: "+ DatosDinamicos.combo("idPcCajBans", id_plan_cuenta_caja_banco, caja_bancos, "")+"</td></tr>"+

                        "</table>" +

                        "<div id='InRe'><a href='javascript:void(0);' style='float:left;' onclick=\"inv_addRetencion();\">Insertar Retenci&oacute;n</a></div>" +
                        "&nbsp;&nbsp;<div style=\"display:none\" id='axBI'>"+ DatosDinamicos.combo(rsBasesImponibles, "9a0", "9b0", "", "inv_sumRetSubTotales(9d0,1);inv_sumRetTotales();", "inv_sumRetSubTotales(9d0,1);inv_sumRetTotales();", "", 165) +"</div>"
                        + "<div style=\"display:none\" id='axCR'>"+ DatosDinamicos.combo(rsTablaImpuestos, "9a0", "9b0", "inv_setMsg('axMsR');", "inv_setConcepto(9c0);inv_sumRetSubTotales(9d0,1);inv_sumRetTotales();", "_('9e0').focus();", "", 285) +"</div>" +
                        "<div style=\"display:none\" id='axC'>"+ objTablaImpuesto.getConceptoRetencion() +"</div>" +
                        "<table cellpadding='0' cellspacing='0'>" + 
                        "<TH class='jm_TH' width='170'>CAMPO BASE IMPONIBLE</TH>" +
                        "<TH class='jm_TH' width='290'>IMPUESTO - C&Oacute;DIGO - % DE RETENCI&Oacute;N</TH>" +
                        "<TH class='jm_TH' width='120'>BASE IMPONIBLE</TH>" +
                        "<TH class='jm_TH' width='120'>VALOR RETENIDO</TH>" +
                        "<TH class='jm_TH' width='20'>&nbsp;&nbsp;</TH></tr></table>" +

                        "<DIV id='sll1' style='overflow:auto;width:760px;height:155px;'>" +
                        "<TABLE class='jm_tabla' cellspacing='1' cellpadding='0' id='tblR'>");

                try{
                    ResultSet retencionDetalle = objFactura.getRetencionDetalle(id_retencion_venta);
                    String campo_valor = "";
                    String impuesto = "";
                    String codigo_impuesto = "";
                    String porcentaje_retencion = "";
                    html = "";
                    i=0;
                    while(retencionDetalle.next()){
                        tiene_retencion = true;
                        campo_valor = (retencionDetalle.getString("campo_valor")!=null) ? retencionDetalle.getString("campo_valor") : "";
                        impuesto = (retencionDetalle.getString("impuesto")!=null) ? retencionDetalle.getString("impuesto") : "";
                        codigo_impuesto = (retencionDetalle.getString("codigo_impuesto")!=null) ? retencionDetalle.getString("codigo_impuesto") : "";
                        porcentaje_retencion = (retencionDetalle.getString("porcentaje_retencion")!=null) ? retencionDetalle.getString("porcentaje_retencion") : "";
                        html += "<tr id='rRe"+i+"' class='jm_filaPar'>";
                        html += "<td width='170'>" + campo_valor + "</td>";
                        html += "<td width='290'>" + impuesto + " &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; " +
                                codigo_impuesto + " &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; " +
                                porcentaje_retencion + "</td>";
                        html += "<td width='121' align='right'><input id='bi"+i+"' name='bi"+i+"' type='text' class='inpTbl' size='20' onkeypress=\"_evaluar(event, '0123456789.');\" onkeyup=\"inv_sumRetSubTotales("+i+");inv_sumRetTotales();\" " +
                                "onfocus=\"this.select();\" value='"+((retencionDetalle.getString("base_imponible")!=null) ? retencionDetalle.getString("base_imponible") : "")+"' /></td>";
                        html += "<td width='121' align='right'><input id='vr"+i+"' name='vr"+i+"' type='text' class='inpTbl' size='20' onkeypress=\"_evaluar(event, '0123456789.');\" onkeyup=\"inv_sumRetTotales();\" " +
                                "onfocus=\"this.select();\" value='"+((retencionDetalle.getString("valor_retenido")!=null) ? retencionDetalle.getString("valor_retenido") : "")+"' /></td>";
                        html += "<td width='21' align='center'><div class='jm_icoCon' style='background-position:-545px -16px;float:right;' title='Eliminar' onclick=\"_R('rRe"+i+"');inv_sumRetTotales();\">&nbsp;</div></td>";
                        html += "</tr>";
                        i++;
                    }
                    retencionDetalle.close();
                }catch(Exception e){
                    e.printStackTrace();
                }

                out.print(html);


                out.println("</table></div><table cellpadding='0' cellspacing='0'>" +
                        "<tr><td colspan='2' width='570' align='right'>TOTAL RETENCI&Oacute;N: </td>" +
                        "<td align='right'> &nbsp;<input type='text' id='tvr' name='tvr' value='"+ret_impuesto_retenido+"' style='text-align:right' onkeypress=\"_evaluar(event, '0123456789.');\" /></td>" +
                        "</tr></table>");

                try{
                    rsTablaImpuestos.close();
                }catch(Exception e){
                    e.printStackTrace();
                }

                out.print("</div>");
                
            //}





           /*           NOTAS DE CREDITO                */



            out.println("<div id='tab12' class='jm_tabHTML' style='display:none;width:760px;height:"+_altBody+"px;'>");
           
            if(idFactura.compareTo("-1")!=0){
                
                NotaCreditoVenta objNotaCredito = new NotaCreditoVenta(this._ip, this._puerto, this._db, usuario, clave);

                String nc_serie_nota = not_cre_num_serie;
                long nc_num_nota = objNotaCredito.getNumNotaCredito(id_sucursal);
                String nc_clave_acceso = "";
                String nc_autorizacion_nota = not_cre_autorizacion;
                String nc_ci = ruc;
                String nc_razon_social = razon_social;
                String nc_fecha_emision = Fecha.getFecha("SQL");
                String nc_modifica = "Factura No. "+serie_factura+"-"+num_factura;
                String nc_tipo = "v";
                String nc_concepto = "Devolución de ";
                String nc_valor = "0";
                String nc_desc = "0";
                String nc_subtotal_2 = "0";
                String nc_subtotal_0 = "0";
                String nc_subtotal_6 = "0";
                String nc_iva_2 = "0";
                String nc_total = "0";
                try{
                    ResultSet rsNotaCredito = objNotaCredito.getNotaCreditoFactura(idFactura);
                    if(rsNotaCredito.next()){
                        id_nota_credito_venta = ((rsNotaCredito.getString("id_nota_credito_venta")!=null) ? rsNotaCredito.getString("id_nota_credito_venta") : "");
                        nc_serie_nota = ((rsNotaCredito.getString("serie_nota")!=null) ? rsNotaCredito.getString("serie_nota") : "");
                        nc_num_nota = ((rsNotaCredito.getString("num_nota")!=null) ? rsNotaCredito.getLong("num_nota") : 0);
                        nc_ci = ((rsNotaCredito.getString("ruc")!=null) ? rsNotaCredito.getString("ruc") : "");
                        nc_razon_social = ((rsNotaCredito.getString("razon_social")!=null) ? rsNotaCredito.getString("razon_social") : "");
                        nc_clave_acceso = ((rsNotaCredito.getString("clave_acceso")!=null) ? rsNotaCredito.getString("clave_acceso") : "");
                        nc_autorizacion_nota = ((rsNotaCredito.getString("autorizacion_nota")!=null) ? rsNotaCredito.getString("autorizacion_nota") : "");
                        nc_fecha_emision = ((rsNotaCredito.getString("fecha_emision")!=null) ? Fecha.ISOaSQL(rsNotaCredito.getString("fecha_emision")) : "");
                        nc_modifica = ((rsNotaCredito.getString("documento_modifica")!=null) ? rsNotaCredito.getString("documento_modifica") : "");
                        nc_tipo = ((rsNotaCredito.getString("tipo")!=null) ? rsNotaCredito.getString("tipo") : "");
                        nc_concepto = ((rsNotaCredito.getString("concepto")!=null) ? rsNotaCredito.getString("concepto") : "");
                        nc_valor = ((rsNotaCredito.getString("valor")!=null) ? rsNotaCredito.getString("valor") : "0.00");
                        nc_desc = ((rsNotaCredito.getString("descuento")!=null) ? rsNotaCredito.getString("descuento") : "0.00");
                        nc_subtotal_2 = ((rsNotaCredito.getString("subtotal_2")!=null) ? rsNotaCredito.getString("subtotal_2") : "0.00");
                        nc_subtotal_0 = ((rsNotaCredito.getString("subtotal_0")!=null) ? rsNotaCredito.getString("subtotal_0") : "0.00");
                        nc_subtotal_6 = ((rsNotaCredito.getString("subtotal_6")!=null) ? rsNotaCredito.getString("subtotal_6") : "0.00");
                        nc_iva_2 = ((rsNotaCredito.getString("iva_2")!=null) ? rsNotaCredito.getString("iva_2") : "0.00");
                        nc_total = ((rsNotaCredito.getString("total")!=null) ? rsNotaCredito.getString("total") : "0.00");
                        rsNotaCredito.close();
                    }
                }catch(Exception ie){
                    ie.printStackTrace();
                }
                

                out.println("<input type='hidden' id='nctope' name='nctope' value='0' />"
                        + "<table width='740' cellpadding='0' cellspacing='0'>" +
                    "<tr><td></td><td colspan='2'></td></tr>"+
                    "<tr><td align='center' width='400'> N O T A &nbsp;&nbsp; D E &nbsp;&nbsp; C R &Eacute; D I T O</td>" +
                    "<td align='right' width='500'> Nro. <input type='text' id='ncSerie' name='ncSerie' size='6' maxlength='7' value='"+nc_serie_nota+"' onkeypress=\"_evaluar(event, '0123456789-');\" />-" +
                    "<input type='text' size='6' id='ncNum' name='ncNum' value='"+nc_num_nota+"' onkeypress=\"_numero(event);\" />"
                    + "<br />Clave Acceso: <input type='text' size='49' id='ncClAc' name='ncClAc' value='"+nc_clave_acceso+"' readonly />"
                    + "<br />Autorizacion: <input type='text' size='49' id='ncAut' name='ncAut' value='"+nc_autorizacion_nota+"' readonly /></td>"+ 
                    "</tr></table>" +

                    "<table width='740' cellpadding='0' cellspacing='0'>"+ 
                    "<tr><td>Se&ntilde;or(es): <span class='marca'>*</span></td>"+
                    "<td><input type='text' size='40' maxlength='100' id='ncRaSo' name='ncRaSo' class='may' value='"+nc_razon_social+"' readonly onkeypress=\"_alfanumerico(event);\" /></td>" +
                    "<td>Fecha de emisi&oacute;n: </td>"+
                    "<td><input type='text' size='10' maxlength='10' id='ncFeEm' name='ncFeEm' value='"+nc_fecha_emision+"' readonly onkeypress=\"_numero(event);\" /></td>" +
                    "</tr>"+
                    "<tr><td>R.U.C. / C.I.: <span class='marca'>*</span></td>"+
                    "<td><input type='text' size='40' maxlength='13' id='ncRuc' name='ncRuc' class='may' value='"+nc_ci+"' readonly onkeypress=\"_alfanumerico(event);\" /></td>" +
                    "<td>Comprobante que modifica: </td>"+
                    "<td><input type='text' size='30' maxlength='50' id='ncCoMo' name='ncCoMo' value='"+nc_modifica+"' readonly onkeypress=\"_numero(event);\" /></td>" +
                    "</tr>"+
                    "<tr><td>Tipo de modificaci&oacute;n: <span class='marca'>*</span></td>"+
                    "<td colspan='3'><label><input type='radio' id='e0' name='tipo' value='v' "+(nc_tipo.compareTo("v")==0 ? "checked" : "")+" /> Devoluci&oacute;n</label> &nbsp; "+
                    "<label><input type='radio' id='e1' name='tipo' value='s' "+(nc_tipo.compareTo("s")==0 ? "checked" : "")+" /> Descuento</label> &nbsp; "+
                    //"<label><input type='radio' id='e2' name='tipo' value='r' "+(nc_tipo.compareTo("r")==0 ? "checked" : "")+" /> Error en mayor valor en la factura</label></td>"+
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

                    "<DIV id='sll0' style='overflow:auto;width:746px;height:145px;'>" +
                    "<TABLE class='jm_tabla' cellspacing='1' cellpadding='0'><tbody id='nctblD'>");

                html="";
                try{
                    String clase = "";
                    if(id_nota_credito_venta.compareTo("")==0){
                        String codigo = "";
                        String descripcion_mas = "";
                        String cantidad = "0";
                        String ncPu = "0";
                        String ncST = "0";
                        String ncDes = "0";
                        String ncIva = "0";
                        String ncT = "0";
                        i=0;
                        facturaDetalle.beforeFirst();
                        while(facturaDetalle.next()){
                            codigo = ((facturaDetalle.getString("codigo")!=null) ? facturaDetalle.getString("codigo") : "");
                            descripcion_mas = ((facturaDetalle.getString("descripcion_mas")!=null) ? facturaDetalle.getString("descripcion_mas") : "");
                            cantidad = ((facturaDetalle.getString("cantidad")!=null) ? facturaDetalle.getString("cantidad") : "0");
                            ncPu = ((facturaDetalle.getString("p_u")!=null) ? facturaDetalle.getString("p_u") : "0");
                            ncST = ((facturaDetalle.getString("p_st")!=null) ? facturaDetalle.getString("p_st") : "0");
                            ncDes = ((facturaDetalle.getString("descuento")!=null) ? facturaDetalle.getString("descuento") : "0");
                            ncIva = ((facturaDetalle.getString("iva")!=null) ? facturaDetalle.getString("iva") : "0");
                            ncT = ((facturaDetalle.getString("total")!=null) ? facturaDetalle.getString("total") : "0");
                            clase = i%2==0 ? "jm_filaPar" : "jm_filaImp";
                            html += "<tr id='ncr"+i+"' class='"+clase+"'>";
                            html += "<td width='21' align='center'><input type='checkbox' id='chProd"+i+"' name='chProd"+i+"' onclick=\"fac_setDetNotaCredito()\" />"+
                            "<input type='hidden' id='idFacVenDet"+i+"' name='idFacVenDet"+i+"' value='"+((facturaDetalle.getString("id_factura_venta_detalle")!=null) ? facturaDetalle.getString("id_factura_venta_detalle") : "")+"' />"
                                    + "<input id='ncIdProd"+i+"' name='ncIdProd"+i+"' type='hidden' value=\""+descripcion_mas+"\" />"
                                    + "<input type='hidden' id='axNcC"+i+"' value='"+cantidad+"' />"
                                    + "<input type='hidden' id='axncST"+i+"' value='"+ncST+"' />"
                                    + "<input type='hidden' id='axncDes"+i+"' value='"+ncDes+"' />"
                                    + "<input type='hidden' id='axncIva"+i+"' value='"+ncIva+"' />"
                                    + "<input type='hidden' id='axncT"+i+"' value='"+ncT+"' /></td>";
                            html += "<td width='100' id='cF"+i+"0'><input id='ncCodigo"+i+"' name='ncCodigo"+i+"' type='hidden' value=\""+codigo+"\" />"+codigo+"</td>";
                            html += "<td width='226'><input id='ncDescrip"+i+"' name='ncDescrip"+i+"' type='hidden' value=\""+descripcion_mas+"\" />"+descripcion_mas+"</td>";
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
                        ResultSet rsNotaCreditoDetalle = objNotaCredito.getNotaCreditoFacturaDetalle(id_nota_credito_venta);
                        while(rsNotaCreditoDetalle.next()){
                            clase = i%2==0 ? "jm_filaPar" : "jm_filaImp";
                            html += "<tr id='ncr"+i+"' class='"+clase+"'>";
                            html += "<td width='21' align='center'></td>";
                            html += "<td width='100' id='cF"+i+"0'>"+((rsNotaCreditoDetalle.getString("codigo")!=null) ? rsNotaCreditoDetalle.getString("codigo") : "")+"</td>";
                            html += "<td width='226'>"+((rsNotaCreditoDetalle.getString("descripcion_mas")!=null) ? rsNotaCreditoDetalle.getString("descripcion_mas") : "")+"</td>";
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
                }finally{
                    objNotaCredito.cerrar();
                }

                out.print(html);
                out.print("</tbody></table></div>");
                

                out.print("<table cellpadding='0' cellspacing='0'>"+
                    
                    "<tr class='jm_filaPar' align='right'><td width='630'>SUBT.</td>"+ 
                    "<td width='120'><input type='text' style='text-align:right' size='13' maxlength='50' id='ncsubt' name='ncsubt' value='"+nc_valor+"' onkeypress=\"_evaluar(event, '0123456789.');\" /></td></tr>"+

                    "<tr class='jm_filaPar' align='right'><td>SUBT. 12%</td>" +
                    "<td><input type='text' style='text-align:right' size='13' maxlength='50' id='ncsubt2' name='ncsubt2' value='"+nc_subtotal_2+"' onkeypress=\"_evaluar(event, '0123456789.');\" /></td></tr>"+
                    
                    "<tr class='jm_filaPar' align='right'><td>SUBT. 0%</td>" +
                    "<td><input type='text' style='text-align:right' size='13' maxlength='50' id='ncsubt0' name='ncsubt0' value='"+nc_subtotal_0+"' onkeypress=\"_evaluar(event, '0123456789.');\" /></td></tr>"+
                    
                    "<tr align='right'><td>SUBTOTAL NO OBJETO DE IVA: </td>" +
                    "<td align='right' class='jm_filaPar'> <input type='text' size='13' id='ncsubt6' name='ncsubt6' value='"+nc_subtotal_6+"' style='text-align:right' onkeypress=\"_evaluar(event, '0123456789.');\" /></td></tr>" +
                        
                    "<tr class='jm_filaPar' align='right'><td>DESCUENTO %</td>" +
                    "<td><input type='text' style='text-align:right' size='13' maxlength='50' id='ncdt' name='ncdt' value='"+nc_desc+"' onkeypress=\"_evaluar(event, '0123456789.');\" /></td></tr>"+

                    "<tr class='jm_filaPar' align='right'><td>IVA 12%</td>" +
                    "<td><input type='text' style='text-align:right' size='13' maxlength='50' id='nciva2' name='nciva2' value='"+nc_iva_2+"' onkeypress=\"_evaluar(event, '0123456789.');\" /></td></tr>"+                    

                    "<tr class='jm_filaPar' align='right'><td>VALOR TOTAL</td>" +
                    "<td><input type='text' style='text-align:right' size='13' maxlength='50' id='nctt' name='nctt' value='"+nc_total+"' onkeypress=\"_evaluar(event, '0123456789.');\" /></td></tr>");

                    if(id_nota_credito_venta.compareTo("")==0){
                        if(!anulado){
                            out.print("<tr class='jm_filaPar'><td colspan='2' align='right'><input type='button' value='Emitir nota de cr&eacute;dito' onclick=\"fac_notaCreditoEmitir(_('formVenta'))\" /></td></tr>");
                        }
                    }else{
                        out.print("<tr class='jm_filaPar'>");
                        String colS = "colspan='2'";
                        /*if(objRol.getAcceso(id_rol, "notaCreditoAnular") ){
                            out.print("<td align='right'><input type='button' value='Anular nota de cr&eacute;dito' onclick=\"fac_notaCreditoAnular("+id_nota_credito_venta+");\" /> &nbsp; </td>");
                            colS = "";
                        }*/
                        if(objRol.getAcceso(id_rol, "notaCreditoVentaNumero") ){
                            out.print("<td align='right'><input type='button' value='Actualizar n&uacute;mero' onclick=\"fac_setNumNotaCredito("+id_nota_credito_venta+");\" /> &nbsp; </td>");
                            colS = "";
                        }
                        out.print("<td align='right' "+colS+"><input type='button' value='Re-imprimir nota de cr&eacute;dito' onclick=\"imprimir('pdfNotaCredito?id="+id_nota_credito_venta+"');\" /></td>");
                        out.print("</tr>");
                    }

                    out.print("</table>");

            }

            out.print("</div>");



            /*           NOTAS DE DEBITO                */

/*

            out.println("<div id='tab13' class='jm_tabHTML' style='display:none;width:760px;height:"+_altBody+"px;'>");
            
            if(idFactura.compareTo("-1")!=0){
                
                NotaDebitoVenta objNotaDebito = new NotaDebitoVenta(this._ip, this._puerto, this._db, usuario, clave);

                String id_nota_debito_venta = "";
                String nd_serie_nota = not_deb_num_serie;
                long nd_num_nota = objNotaDebito.getNumNotaDebito(id_sucursal);
                String nd_autorizacion_nota = not_deb_autorizacion;
                String nd_ci = ruc;
                String nd_razon_social = razon_social;
                String nd_fecha_emision = Fecha.getFecha("SQL");
                String nd_modifica = "Factura No. "+serie_factura+"-"+num_factura;
                String nd_concepto = "";
                String nd_valor = "0";
                String nd_subtotal_0 = "0";
                String nd_iva_2 = "0";
                String nd_total = "0";
                try{
                    ResultSet rsNotaDebito = objNotaDebito.getNotaDebitoFactura(idFactura);
                    if(rsNotaDebito.next()){
                        id_nota_debito_venta = ((rsNotaDebito.getString("id_nota_debito_venta")!=null) ? rsNotaDebito.getString("id_nota_debito_venta") : "");
                        nd_serie_nota = ((rsNotaDebito.getString("serie_nota")!=null) ? rsNotaDebito.getString("serie_nota") : "");
                        nd_num_nota = ((rsNotaDebito.getString("num_nota")!=null) ? rsNotaDebito.getLong("num_nota") : 0);
                        nd_ci = ((rsNotaDebito.getString("ruc")!=null) ? rsNotaDebito.getString("ruc") : "");
                        nd_razon_social = ((rsNotaDebito.getString("razon_social")!=null) ? rsNotaDebito.getString("razon_social") : "");
                        nd_autorizacion_nota = ((rsNotaDebito.getString("autorizacion_nota")!=null) ? rsNotaDebito.getString("autorizacion_nota") : "");
                        nd_fecha_emision = ((rsNotaDebito.getString("fecha_emision")!=null) ? Fecha.ISOaSQL(rsNotaDebito.getString("fecha_emision")) : "");
                        nd_modifica = ((rsNotaDebito.getString("documento_modifica")!=null) ? rsNotaDebito.getString("documento_modifica") : "");
                        nd_concepto = ((rsNotaDebito.getString("concepto")!=null) ? rsNotaDebito.getString("concepto") : "");
                        nd_valor = ((rsNotaDebito.getString("valor")!=null) ? rsNotaDebito.getString("valor") : "0.00");
                        nd_subtotal_0 = ((rsNotaDebito.getString("subtotal_0")!=null) ? rsNotaDebito.getString("subtotal_0") : "0.00");
                        nd_iva_2 = ((rsNotaDebito.getString("iva_2")!=null) ? rsNotaDebito.getString("iva_2") : "0.00");
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
                    "<tr><td>Se&ntilde;or(es): <span class='marca'>*</span></td>"+
                    "<td><input type='text' size='40' maxlength='100' id='ndRaSo' name='ndRaSo' class='may' value='"+nd_razon_social+"' readonly onkeypress=\"_alfanumerico(event);\" /></td>" +
                    "<td>Fecha de emisi&oacute;n: </td>"+
                    "<td><input type='text' size='10' maxlength='10' id='ndFeEm' name='ndFeEm' value='"+nd_fecha_emision+"' readonly onkeypress=\"_numero(event);\" /></td>" +
                    "</tr>"+
                    "<tr><td>R.U.C. / C.I.: <span class='marca'>*</span></td>"+
                    "<td><input type='text' size='40' maxlength='13' id='ndRuc' name='ndRuc' class='may' value='"+nd_ci+"' readonly onkeypress=\"_alfanumerico(event);\" /></td>" +
                    "<td>Comprobante que modifica: </td>"+
                    "<td><input type='text' size='30' maxlength='50' id='ndCoMo' name='ndCoMo' value='"+nd_modifica+"' readonly onkeypress=\"_numero(event);\" /></td>" +
                    "</tr>"+                    
                    "</table>"+


                    "<tr><td>Forma de pago: </td>"+
                    "<td colspan='3'>" + DatosDinamicos.combo("ndFoPa", forma_pago, forma_pago1, "cnt_setFormaPago(this, 'nd');") + "</td>" +
                    "</tr>"+

                    "<tr><td colspan='4'>"+

                    "<div class='jm_tabla' style='background-color:#FFFFFF'>"+

                    "<div id='ndpCh' class='jm_fila' style=\"display:"+cobro+";\">"+
                        "<div class='jm_columna' style='width:108px'>Banco: <span class='marca'>*</span></div><div class='jm_columna' style='width:260px'><input id='ndbco' name='ndbco' type='text' size='20' maxlength='50' value='"+banco+"' onkeypress=\"_letra(event);\" /></div>" +
                        "<div class='jm_columna' style='width:108px'>Cheque #: <span class='marca'>*</span></div><div class='jm_columna'><input id='ndchq' name='ndchq' value='"+num_cheque+"' type='text' size='20' maxlength='30' onkeypress=\"_numero(event);\"/></div>" +
                    "</div>"+

                    //  cuando la forma de pago es transferencia, deposito  
                    "<div class='jm_fila'>"+
                        "<div id='ndpOtr' class='jm_columna' style=\"width:370px;display:"+cobro_dep_tran+";\">No. de comprobante de pago: <span class='marca'>*</span> <input id='ndncp' name='ndncp' type='text' size='20' maxlength='20' value='"+num_comp_pago+"' onkeypress=\"_numero(event);\" /></div>" +
                        "<div id='ndpTra' class='jm_columna' style='display:"+cobro_dep_tran+";'>Gastos bancarios: <span class='marca'>*</span> &nbsp;&nbsp; <input id='ndgb' name='ndgb' type='text' size='10' value='"+gastos_bancos+"' onkeypress=\"_evaluar(event, '0123456789.');\" /></div>"+
                    "</div>" +

                    "<div id='ndpBan' class='jm_fila' style='display:"+cobro_trans+";'>"+
                        "<div class='jm_columna' style='width:108px'>Cuenta bancaria:<span class='marca'>*</span> &nbsp;</div><div class='jm_columna'>"+ DatosDinamicos.combo(rsBancos, "ndidPcBc", id_plan_cuenta_banco, "")+"</div>"+
                    "</div>" +
                    "</div>"+

                    "</td></tr>" +

                    "<tr><td colspan='4'>");
                    if(idFactura.compareTo("-1")!=0){
                        out.print("<div id='InAr'><a href='javascript:void(0);' style='float:left;' onclick=\"fac_filtrarProducto('nd');\">Insertar Producto</a></div>" +
                        "&nbsp;&nbsp;<div style=\"display:none\" id='ax'></div>");
                    }else{
                        out.println("&nbsp;");
                    }
            out.print("</td></tr>" +
                    "</table>" +


                    "<table cellpadding='0' cellspacing='0'>" +
                    "<TH class='jm_TH' width='100'>C&Oacute;DIGO</TH>" +
                    "<TH class='jm_TH' width='225'>DESCRIPCI&Oacute;N</TH>" +
                    "<TH class='jm_TH' width='40'>CANT.</TH>" +
                    "<TH class='jm_TH' width='60'>P./U.</TH>" +
                    "<TH class='jm_TH' width='70'>SUBTOT.</TH>" +
                    "<TH class='jm_TH' width='60'>DESC.</TH>" +
                    "<TH class='jm_TH' width='60'>IVA</TH>" +
                    "<TH class='jm_TH' width='80'>TOTAL</TH>" +
                    "<TH class='jm_TH' width='20'>&nbsp;&nbsp;</TH></tr></table>" +

                    "<DIV id='sll0' style='overflow:auto;width:746px;height:120px;'>" +
                    "<TABLE class='jm_tabla' cellspacing='1' cellpadding='0'><tbody id='ndtblD'>");

                html="";
                try{
                    ResultSet rsNotaDebitoDetalle = objNotaDebito.getNotaDebitoDetalle(id_nota_debito_venta);
                    String clase = "";
                    i=0;
                    while(rsNotaDebitoDetalle.next()){
                        clase = i%2==0 ? "jm_filaPar" : "jm_filaImp";
                        html += "<tr id='ndr"+i+"' class='"+clase+"'>";
                        html += "<td width='100' id='ndcF"+i+"0'>"+((rsNotaDebitoDetalle.getString("codigo")!=null) ? rsNotaDebitoDetalle.getString("codigo") : "")+"</td>";
                        html += "<td width='226'>"+((rsNotaDebitoDetalle.getString("descripcion_mas")!=null) ? rsNotaDebitoDetalle.getString("descripcion_mas") : "")+"</td>";
                        html += "<td width='41'>"+((rsNotaDebitoDetalle.getString("cantidad")!=null) ? rsNotaDebitoDetalle.getString("cantidad") : "")+"</td>";
                        html += "<td width='61' align='right'>"+((rsNotaDebitoDetalle.getString("p_u")!=null) ? rsNotaDebitoDetalle.getString("p_u") : "")+"</td>";
                        html += "<td width='71' align='right'>"+((rsNotaDebitoDetalle.getString("p_st")!=null) ? rsNotaDebitoDetalle.getString("p_st") : "")+"</td>";
                        html += "<td width='61' align='right'>"+((rsNotaDebitoDetalle.getString("descuento")!=null) ? rsNotaDebitoDetalle.getString("descuento") : "")+"</td>";
                        html += "<td width='61' align='right'>"+((rsNotaDebitoDetalle.getString("iva")!=null) ? rsNotaDebitoDetalle.getString("iva") : "")+"</td>";
                        html += "<td width='81' align='right'>"+((rsNotaDebitoDetalle.getString("total")!=null) ? rsNotaDebitoDetalle.getString("total") : "")+"</td>";
                        html += "<td width='21' align='center'>&nbsp;</td>";
                        html += "</tr>";
                        i++;
                    }
                    rsNotaDebitoDetalle.close();
                }catch(Exception e){
                    e.printStackTrace();
                }finally{
                    objNotaDebito.cerrar();
                }

                out.print(html);

                out.print("</tbody></table></div>");


                out.print("<table cellpadding='0' cellspacing='0'>" +
                "<TH class='jm_TH' width='590'>RAZON DE LA MODIFICACION</TH>" +
                "<TH class='jm_TH' width='120'>VALOR DE LA MODIFICACION</TH></tr>"+
                "</table>"+

                "<TABLE class='jm_tabla' cellspacing='0' cellpadding='0'>"+
                "<tr class='jm_filaPar' align='right'><td  width='590'><textarea id='ndRaMo' name='ndRaMo' style=\"width:585px;height:17px\" onkeypress=\"_alfanumerico(event);\">"+nd_concepto+"</textarea></td>" +
                "<td width='120'><input type='text' style='text-align:right' size='13' maxlength='50' id='ndVaMo' name='ndVaMo' value='"+nd_valor+"' onkeypress=\"_evaluar(event, '0123456789.');\" /></td></tr>"+

                "<tr class='jm_filaPar' align='right'><td>IVA 12%</td>" +
                "<td><input type='text' style='text-align:right' size='13' maxlength='50' id='ndIva12' name='ndIva12' value='"+nd_iva_2+"' onkeypress=\"_evaluar(event, '0123456789.');\" /></td></tr>"+

                "<tr class='jm_filaPar' align='right'><td>IVA 0%</td>" +
                "<td><input type='text' style='text-align:right' size='13' maxlength='50' id='ndIva0' name='ndIva0' value='"+nd_subtotal_0+"' onkeypress=\"_evaluar(event, '0123456789.');\" /></td></tr>"+

                "<tr class='jm_filaPar' align='right'><td>VALOR TOTAL</td>" +
                "<td><input type='text' style='text-align:right' size='13' maxlength='50' id='ndTotal' name='ndTotal' value='"+nd_total+"' onkeypress=\"_evaluar(event, '0123456789.');\" /></td></tr>");

                if(id_nota_debito_venta.compareTo("")==0){
                    if(!anulado){
                        out.print("<tr class='jm_filaPar'><td colspan='2' align='right'><input type='button' value='Emitir nota de d&eacute;bito' onclick=\"fac_notaDebitoEmitir(_('formVenta'))\" /></td></tr>");
                    }
                }else{
                    out.print("<tr class='jm_filaPar'><td colspan='2' align='right'><input type='button' value='Re-imprimir Nota de d&eacute;bito' onclick=\"imprimir('pdfNotaDebito?id="+id_nota_debito_venta+"');\" /></td></tr>");
                }
                out.print("</table>");
                
            }

            out.print("</div>");
*/



            /*  BOTONES  */


            out.println("<table class='jm_tabla' width='100%' cellspacing='0' cellpadding='0'><tr>");
            //if(!anulado){
                if(id_nota_credito_venta.compareTo("")==0){
                    long fecha_sistema = Fecha.getTimeStamp(Fecha.getFecha("SQL"));
                    //long fecha_asiento = Fecha.getTimeStamp(fecha_emision);
                    //long fecha_lim_ret = Fecha.getTimeStamp(Fecha.get5DiasLaborables(fecha_emision));
                    String fecha_limite = objFactura.getFechaPlazoRetenciones(fecha_emision, dia_cortes_creditos);
                    long fecha_lim_ret = Fecha.getTimeStamp(fecha_limite);

                    if(idFactura.compareTo("-1")!=0 && objRol.getAcceso(id_rol, "compRetVentaNueva") && !tiene_retencion && fecha_sistema < fecha_lim_ret){
                        out.println("<td><input type='button' value='Guardar Retenci&oacute;n' onclick=\"fac_retencionGardar();\" /></td>");
                    }
                    /*if(idFactura.compareTo("-1")!=0 && objRol.getAcceso(id_rol, "compRetVentaAnular") && i>0 && fecha_sistema==fecha_asiento){
                        out.println("<td><input type='button' value='Anular Retenci&oacute;n' onclick='fac_retencionAnular("+id_retencion_venta+");' /></td>");
                    }*/
                    if(!anulado && idFactura.compareTo("-1")!=0){
                        out.println("<td align='right'><input type='button' value='Re-imprimir Factura' onclick=\"imprimir('pdfFacturaVenta?id="+idFactura+"');\" /></td>");
                    }
                    if(idFactura.compareTo("-1")!=0 && objRol.getAcceso(id_rol, "ventaAnular")){                        
                        //if(fecha_sistema==fecha_asiento || objFactura.fechaAnulacion(idFactura)){
                            out.println("<td align='right'><input type='button' value='Anular Factura' onclick='fac_ventaAnular("+idFactura+");' /></td>");
                        //}
                    }
                    if(estado_documento.compareTo("n")==0 && !anulado){
                        out.println("<td align='right'>");
                        if(idFactura.compareTo("-1")!=0){
                            out.println("<input type='checkbox' id='genNewClave' name='genNewClave' /> <span class='marca'>Generar nueva clave de acceso </span>&nbsp;");
                        }
                        out.println("<input type='submit' value='Guardar' onclick=\"_('imp').value=0;this.disabled=true;\" />");
                        out.println("</td>");
                    }
                    if(idFactura.compareTo("-1")==0){    
                        out.println("<td align='right'><input type='submit' value='Guardar e Imprimir' onclick=\"_('imp').value=1;this.disabled=true;\" /></td>");
                    }
                }else{
                    out.println("<td><span class='marca'>FACTURA MODIFICADA POR NOTA DE CREDITO</span></td>");
                }
            /*}else{
                out.println("<td><span class='marca'>FACTURA DE COMPRA ANULADA</span></td>");
            }*/
            out.print("</tr></table>");



            out.print("</div></form></div>");

        } finally {
            objProducto.cerrar();
            objCliente.cerrar();
            objBaseImponible.cerrar();
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
