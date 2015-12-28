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
import jm.adm.clas.Sucursal;
import jm.adm.clas.TablaImpuesto;
import jm.adm.clas.Configuracion;
import jm.inv.clas.Producto;
import jm.fac.clas.Cliente;
import jm.fac.clas.FacturaVenta;
import java.sql.ResultSet;
import jm.adm.clas.PuntoEmision;
import jm.adm.clas.Ubicacion;
import jm.cnt.clas.Banco;
//import jm.cnt.clas.ComprobanteIngreso;
import jm.inf.clas.Antena;
import jm.fac.clas.Instalacion;
import jm.fac.clas.PlanServicio;
import jm.fac.clas.Sector;
import jm.inv.clas.BaseImponible;
import jm.web.Addons;
import jm.web.Archivo;

/**
 *
 * @author Jorge
 */
public class frmInstalacion extends HttpServlet {
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
        int id_sucursal_sesion = (Integer)sesion.getAttribute("sucursal");
        int id_punto_emision = (Integer)sesion.getAttribute("id_punto_emision");
        String usuario = (String)sesion.getAttribute("usuario");
        String clave = (String)sesion.getAttribute("clave");
        StringBuffer a = request.getRequestURL();

        response.setContentType("text/html;charset=UTF-8");
        response.setHeader("Pragma", "no-cache");
        response.setHeader("Expires", "Mon, 01 Jan 2001 00:00:01 GMT");
        response.setHeader("Cache-Control", "no-store");
        response.setHeader("Cache-Control", "must-revalidate");
        response.setHeader("Cache-Control", "no-cache");
        PrintWriter out = response.getWriter();

        FacturaVenta objFactura = new FacturaVenta(this._ip, this._puerto, this._db, usuario, clave);

        Cliente objCliente = new Cliente(this._ip, this._puerto, this._db, usuario, clave);

        BaseImponible objBaseImponible = new BaseImponible(this._ip, this._puerto, this._db, usuario, clave);
        
        TablaImpuesto objTablaImpuesto = new TablaImpuesto(this._ip, this._puerto, this._db, usuario, clave);
        Rol objRol = new Rol(this._ip, this._puerto, this._db, usuario, clave);

        Sucursal objSuc = new Sucursal(this._ip, this._puerto, this._db, usuario, clave);
        ResultSet rsSucursales = objSuc.getSucursales();

        Configuracion conf = new Configuracion(this._ip, this._puerto, this._db, usuario, clave);
        String p_iva1 = conf.getValor("p_iva1");
        String id_sucursal_matriz = conf.getValor("id_sucursal_matriz");

        //Activo objActivo = new Activo(this._ip, this._puerto, this._db, usuario, clave);

        Instalacion objInstalacion = new Instalacion(this._ip, this._puerto, this._db, usuario, clave);

        //String funcion = "fac_setInstalacion();";
        String funcion = "";

        String id = request.getParameter("id");
        String _altBody = request.getParameter("altB");

        String idFactura = "-1";
        String id_comprobante_diario = "";
        String es_instalacion = "t";
        String num_instalacion = "";
        String id_sucursal = String.valueOf(id_sucursal_sesion);
        String id_cliente = "";
        String establecimiento = "001";
        String ruc = "";
        String razon_social = "";
        String direccion = "";
        String telefono = "";
        String movil_claro = "";
        String movil_movistar = "";
        String ciudad = "";
        String id_provincia = "";
        String id_ciudad = "";
        String id_parroquia = "";
        String id_sector = "";
        String direccion_instalacion = "";
        String fecha_instalacion = Fecha.getFecha("SQL");
        String ax_fecha_instalacion = "";
        String fecha_registro = Fecha.getFecha("SQL");
        boolean soloBodega=true;
        String ip = "";
        String ip_radio = "";
        String cobrar = "t";
        String motivo_no_cobrar = "";
        //String id_activo = "";
        String id_plan_contratado = "";
        String id_plan_actual = "";
        //String comparticion = "8";
        String receptor = "";
        String mac = "";
        String macp = "";
        String macg = "";
        String latitud = "";
        String longitud = "";
        String altura = "";
        String altura_antena = "";
        String id_personalizacion = "-1";
        String porcentaje_senal = "";
        String antena_acoplada = "";
        String id_plan_establecido = "";
        String ingreso_datos = "";
        String conformidad_velocidad = "t";
        String conformidad_instalacion = "";
        String conformidad_atencion = "";
        String estado_instalacion = "";
        String estado_servicio = "";
        String txt_estado_servicio = "";
        boolean anulado = false;
        boolean factura_anulada = false;
        String num_desinstalacion = "";
        String fecha_estado_r = "";
        String fecha_desinstalacion = "";
        String motivo_desinstalacion = "";
        String fecha_visita_instalacion = Fecha.getFecha("ISO");
        boolean generar_orden_trab = false;
        String motivo_no_instalacion = "";
        String fecha_terminado = "";
        String observacion_terminado = "ninguna";

        String radusername = "";
        String radclave = "";
        String deviceclave = "";
        boolean set_deviceclave = false;

        String id_plan_cuenta_caja_banco = "";

        Sector objSector = new Sector(this._ip, this._puerto, this._db, usuario, clave);
        ResultSet rsSectoresInstall = objSector.getSectoresInstalacion();
        String borrSect = objSector.getJSON(rsSectoresInstall);
        ResultSet rsSectores = objSector.getSectores(id_sucursal_sesion);

        String auxSector="";
        String id_producto = "-1";
        String codigo = "";
        String descripcion = "";
        //boolean tiene_iva = true;
        float costo_instalacion=0;
        try{
            if(rsSectores.next()){
                auxSector = rsSectores.getString("id_sector")!=null ? rsSectores.getString("id_sector") : "";
            }
        }catch(Exception e){}

        try{
            ResultSet rsSector = objSector.getSectorInstalacion(auxSector);
            if(rsSector.next()){
                id_producto = rsSector.getString("id_producto")!=null ? rsSector.getString("id_producto") : "-1";
                codigo = rsSector.getString("codigo")!=null ? rsSector.getString("codigo") : "";
                descripcion = rsSector.getString("descripcion")!=null ? rsSector.getString("descripcion") : "";
                //tiene_iva = rsSector.getString("tiene_iva")!=null ? rsSector.getBoolean("tiene_iva") : true;
                costo_instalacion = rsSector.getString("costo_instalacion")!=null ? rsSector.getFloat("costo_instalacion") : 0;
                rsSector.close();
            }
        }catch(Exception ex){}




        String borrInstall = objInstalacion.getNumInstalacionJSON();
        String borrIPs = objInstalacion.getIPsJSON();

        if(id.compareTo("-1")!=0){
            funcion = "_('id_plan_contratado').disabled=true;";
            try{
                ResultSet rsInstalacion = objInstalacion.getInstalacion(id);
                if(rsInstalacion.next()){
                    idFactura = (rsInstalacion.getString("id_factura_venta")!=null) ? rsInstalacion.getString("id_factura_venta") : "-1";
                    id_comprobante_diario = (rsInstalacion.getString("id_comprobante_diario")!=null) ? rsInstalacion.getString("id_comprobante_diario") : "";
                    es_instalacion = (rsInstalacion.getString("es_instalacion")!=null) ? rsInstalacion.getString("es_instalacion") : "t";
                    num_instalacion = (rsInstalacion.getString("num_instalacion")!=null) ? rsInstalacion.getString("num_instalacion") : "";
                    id_sucursal = (rsInstalacion.getString("id_sucursal")!=null) ? rsInstalacion.getString("id_sucursal") : "";
                    id_cliente = (rsInstalacion.getString("id_cliente")!=null) ? rsInstalacion.getString("id_cliente") : "";
                    ruc = (rsInstalacion.getString("ruc")!=null) ? rsInstalacion.getString("ruc") : "";
                    razon_social = (rsInstalacion.getString("razon_social")!=null) ? rsInstalacion.getString("razon_social") : "";
                    direccion = (rsInstalacion.getString("direccion")!=null) ? rsInstalacion.getString("direccion") : "";
                    telefono = (rsInstalacion.getString("telefono")!=null) ? rsInstalacion.getString("telefono") : "";
                    movil_claro = (rsInstalacion.getString("movil_claro")!=null) ? rsInstalacion.getString("movil_claro") : "";
                    movil_movistar = (rsInstalacion.getString("movil_movistar")!=null) ? rsInstalacion.getString("movil_movistar") : "";
                    ciudad = (rsInstalacion.getString("ciudad")!=null) ? rsInstalacion.getString("ciudad") : "";
                    id_provincia = (rsInstalacion.getString("id_provincia")!=null) ? rsInstalacion.getString("id_provincia") : "";
                    id_ciudad = (rsInstalacion.getString("id_ciudad")!=null) ? rsInstalacion.getString("id_ciudad") : "";
                    id_parroquia = (rsInstalacion.getString("id_parroquia")!=null) ? rsInstalacion.getString("id_parroquia") : "";
                    auxSector = id_sector = (rsInstalacion.getString("id_sector")!=null) ? rsInstalacion.getString("id_sector") : "";
                    costo_instalacion = (rsInstalacion.getString("costo_instalacion")!=null) ? rsInstalacion.getFloat("costo_instalacion") : 0;
                    direccion_instalacion = (rsInstalacion.getString("direccion_instalacion")!=null) ? rsInstalacion.getString("direccion_instalacion") : "";
                    fecha_registro = (rsInstalacion.getString("fecha_registro")!=null) ? rsInstalacion.getString("fecha_registro") : "";
                    fecha_instalacion = (rsInstalacion.getString("sql_fecha_instalacion")!=null) ? rsInstalacion.getString("sql_fecha_instalacion") : "";
                    ax_fecha_instalacion = fecha_instalacion;
                    ip = (rsInstalacion.getString("ip")!=null) ? rsInstalacion.getString("ip") : "";
                    ip_radio = (rsInstalacion.getString("ip_radio")!=null) ? rsInstalacion.getString("ip_radio") : "";
                    id_plan_contratado = (rsInstalacion.getString("id_plan_contratado")!=null) ? rsInstalacion.getString("id_plan_contratado") : "";
                    id_plan_actual = (rsInstalacion.getString("id_plan_actual")!=null) ? rsInstalacion.getString("id_plan_actual") : "";
                    //comparticion = (rsInstalacion.getString("comparticion")!=null) ? rsInstalacion.getString("comparticion") : "";
                    cobrar = (rsInstalacion.getString("cobrar")!=null) ? rsInstalacion.getString("cobrar") : "t";
                    motivo_no_cobrar = (rsInstalacion.getString("motivo_no_cobrar")!=null) ? rsInstalacion.getString("motivo_no_cobrar") : "";

                    //id_activo = (rsInstalacion.getString("id_activo")!=null) ? rsInstalacion.getString("id_activo") : "";
                    receptor = (rsInstalacion.getString("receptor")!=null) ? rsInstalacion.getString("receptor") : "";
                    mac = (rsInstalacion.getString("mac")!=null) ? rsInstalacion.getString("mac") : "";
                    macp = (rsInstalacion.getString("macp")!=null) ? rsInstalacion.getString("macp") : "";
                    macg = (rsInstalacion.getString("macg")!=null) ? rsInstalacion.getString("macg") : "";

                    latitud = (rsInstalacion.getString("latitud")!=null) ? rsInstalacion.getString("latitud") : "";
                    longitud = (rsInstalacion.getString("longitud")!=null) ? rsInstalacion.getString("longitud") : "";
                    altura = (rsInstalacion.getString("altura")!=null) ? rsInstalacion.getString("altura") : "";
                    altura_antena = (rsInstalacion.getString("altura_antena")!=null) ? rsInstalacion.getString("altura_antena") : "";

                    id_personalizacion = (rsInstalacion.getString("id_activo_personalizacion")!=null) ? rsInstalacion.getString("id_activo_personalizacion") : "-1";
                    porcentaje_senal = (rsInstalacion.getString("porcentaje_senal")!=null) ? rsInstalacion.getString("porcentaje_senal") : "";
                    antena_acoplada = (rsInstalacion.getString("antena_acoplada")!=null) ? rsInstalacion.getString("antena_acoplada") : "";
                    id_plan_establecido = (rsInstalacion.getString("id_plan_establecido")!=null) ? rsInstalacion.getString("id_plan_establecido") : "";
                    ingreso_datos = (rsInstalacion.getString("ingreso_datos")!=null) ? rsInstalacion.getString("ingreso_datos") : "";
                    conformidad_velocidad = (rsInstalacion.getString("conformidad_velocidad")!=null) ? rsInstalacion.getString("conformidad_velocidad") : "";
                    conformidad_instalacion = (rsInstalacion.getString("conformidad_instalacion")!=null) ? rsInstalacion.getString("conformidad_instalacion") : "";
                    conformidad_atencion = (rsInstalacion.getString("conformidad_atencion")!=null) ? rsInstalacion.getString("conformidad_atencion") : "";
                    estado_instalacion = (rsInstalacion.getString("estado_instalacion")!=null) ? rsInstalacion.getString("estado_instalacion") : "";
                    estado_servicio = (rsInstalacion.getString("estado_servicio")!=null) ? rsInstalacion.getString("estado_servicio") : "";
                    txt_estado_servicio = (rsInstalacion.getString("txt_estado_servicio")!=null) ? rsInstalacion.getString("txt_estado_servicio") : "";
                    anulado = (rsInstalacion.getString("anulado")!=null) ? rsInstalacion.getBoolean("anulado") : false;
                    num_desinstalacion = (rsInstalacion.getString("num_desinstalacion")!=null) ? rsInstalacion.getString("num_desinstalacion") : "";
                    fecha_estado_r = (rsInstalacion.getString("fecha_estado_r")!=null) ? rsInstalacion.getString("fecha_estado_r") : "";
                    fecha_desinstalacion = (rsInstalacion.getString("fecha_desinstalacion")!=null) ? rsInstalacion.getString("fecha_desinstalacion") : "";
                    motivo_desinstalacion = (rsInstalacion.getString("motivo_desinstalacion")!=null) ? rsInstalacion.getString("motivo_desinstalacion") : "";
                    generar_orden_trab = (rsInstalacion.getString("generar_orden_trab")!=null) ? rsInstalacion.getBoolean("generar_orden_trab") : false;
                    fecha_visita_instalacion = (rsInstalacion.getString("fecha_visita_instalacion")!=null) ? rsInstalacion.getString("fecha_visita_instalacion") : "";
                    motivo_no_instalacion = (rsInstalacion.getString("motivo_no_instalacion")!=null) ? rsInstalacion.getString("motivo_no_instalacion") : "";
                            
                    fecha_terminado = (rsInstalacion.getString("fecha_terminado")!=null) ? rsInstalacion.getString("fecha_terminado") : "";
                    observacion_terminado = (rsInstalacion.getString("observacion_terminado")!=null) ? rsInstalacion.getString("observacion_terminado") : "ninguna";

                    radusername = (rsInstalacion.getString("radusername")!=null) ? rsInstalacion.getString("radusername") : "";
                    radclave = (rsInstalacion.getString("radclave")!=null) ? rsInstalacion.getString("radclave") : "ninguna";
                    deviceclave = (rsInstalacion.getString("deviceclave")!=null) ? rsInstalacion.getString("deviceclave") : "ninguna";
                    set_deviceclave = (rsInstalacion.getString("set_deviceclave")!=null) ? rsInstalacion.getBoolean("set_deviceclave") : false;

                    soloBodega = fecha_instalacion.compareTo("")==0 ? true: false;
                    fecha_instalacion = fecha_instalacion.compareTo("")!=0 ? fecha_instalacion : Fecha.getFecha("SQL");

                    rsSectores = objSector.getSectores( Integer.parseInt(id_sucursal) );
                    
                    rsInstalacion.close();
                }
            }catch(Exception e){
                e.printStackTrace();
            }
        }
        
        Ubicacion objUbicacion = new Ubicacion(this._ip, this._puerto, this._db, usuario, clave);
        ResultSet rsProvincias = objUbicacion.getUbicaciones("1");
        ResultSet rsCiudades = objUbicacion.getUbicaciones(id_provincia);
        ResultSet rsParroquias = objUbicacion.getUbicaciones(id_ciudad);
        objUbicacion.cerrar();
        
        //String parroquia = objInstalacion.getParroquia(id_parroquia);

        if(ingreso_datos.compareTo("")!=0){
            funcion += "_('id_plan_establecido').disabled=true;";
        }

        conformidad_velocidad = conformidad_velocidad.compareTo("t")==0 ? "TRUE" : "FALSE";

        PlanServicio objPlanServicio = new PlanServicio(this._ip, this._puerto, this._db, usuario, clave);
        ResultSet rsPlanes = objPlanServicio.getPlanes(auxSector);


        String [][] es_instalacion1 = {{"t","Instalaci&oacute;n"},{"f","Re-Instalaci&oacute;n"}};
        String [][] promocion1 = {{"no","No Aplica"},{"dt","D&eacute;bito con tarjeta de cr&eacute;dito (12 meses)"}};
        
        //String [][] comparticion1 = {{"1","1-1"},{"2","2-1"},{"3","3-1"},{"4","4-1"},{"5","5-1"},{"6","6-1"},{"7","7-1"},{"8","8-1"},{"9","9-1"},{"10","10-1"}};
        String [][] conformidad_velocidad1 = {{"TRUE","Si"},{"FALSE","No"}};
        String [][] conformidad1 = {{"e","Excelente"},{"b","Buena"},{"m","Mala"}};
        String [][] estado_instalacion1 = {{"i","Instalado"},{"e","Emitido"}};
        String [][] cobrar1 = {{"t","SI"},{"f","NO"}};
        String [][] estado_servicio1 = {{"p","Por instalar"},{"a","Activo"},{"c","Cortado"},{"s","Suspendido"},{"r", "Por retirar"},
                                        {"d","Saldado"},{"e", "Equipos devueltos"},{"t", "Terminado y saldado"},{"n", "Central de riesgo"}};

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



        Producto objProducto = new Producto(this._ip, this._puerto, this._db, usuario, clave);

        try {
            int i=0;
            String id_comprobante_ingreso = "";
            String forma_pago = "e";
            String banco = "";
            String num_cheque = "";
            String num_comp_pago = "";
            String gastos_bancos = "0";
            String num_cuenta = "";
            String id_plan_cuenta_banco = "";
            String serie_factura = fac_num_serie;
            String autorizacion = fac_autorizacion;
            long num_factura = objFactura.getNumFactura(id_punto_emision);
            String fecha_emision = Fecha.getFecha("SQL");
            double subtotal = 0;
            String subtotal_0 = "0";
            String subtotal_2 = "0";
            double iva_2 = 0;
            String descuento = "0";
            //double iva_2 = 0;
            String total = "0";
            String observacion = "Ingresos por instalación Factura Nro. " + serie_factura + "-" + num_factura + ".";

            String id_retencion_venta = "-1";
            String ret_num_serie = "001-001";
            String ret_autorizacion = "";
            String ret_num_retencion = "";
            String ret_fecha_emision = Fecha.getFecha("SQL");
            int mes = Fecha.getMes();
            String ret_ejercicio_fiscal_mes = mes<10 ? "0" + mes : String.valueOf(mes);
            String ret_ejercicio_fiscal = String.valueOf(Fecha.getAnio());
            String ret_impuesto_retenido = "";
            boolean ret_anulado = false;

            ResultSet facturaDetalle = null;

            String ocultar = "hidden";
            String modificar = "";


            //String cobro = (forma_pago.compareTo("c")==0) ? "block" : "none";


            ocultar = (!anulado && estado_servicio.compareTo("e")!=0 && estado_servicio.compareTo("t")!=0) ? "visible": "hidden";
            boolean bloqueado = (!anulado && estado_servicio.compareTo("e")!=0 && estado_servicio.compareTo("d")!=0 && estado_servicio.compareTo("n")!=0 && estado_servicio.compareTo("t")!=0) ? false : true;






            out.print("obj»cmp_html^foc»ruc^fun»_objTab.seleccionar(0);"+funcion+"^frm»");

            out.print("<div class='grupoTab' style='width:760px;'><div id='tabR0'>");
            out.print("<a id='tab00' href='javascript:void(0);' class='jm_tabAct' onclick='_objTab.seleccionar(0);'>Instalaci&oacute;n</a>");
            if(id.compareTo("-1")!=0){
                out.print("<a id='tab01' href='javascript:void(0);' class='jm_tab' onclick='_objTab.seleccionar(1);'>Croquis</a>");
                if(objRol.getAcceso(id_rol, "postInstall")){
                    out.print("<a id='tab02' href='javascript:void(0);' class='jm_tab' onclick='_objTab.seleccionar(2);'>Post-Instalaci&oacute;n</a>");
                }
            }
            if(es_instalacion.compareTo("t")==0){
                out.print("<a id='tab03' href='javascript:void(0);' class='jm_tab' onclick='_objTab.seleccionar(3);'>Factura</a>");
                if(forma_pago.compareTo("d")!=0){
                    out.print("<a id='tab04' href='javascript:void(0);' class='jm_tab' onclick='_objTab.seleccionar(4);'>Retenci&oacute;n</a>");
                }
            }
            if(id.compareTo("-1")!=0){
                out.print("<a id='tab08' href='javascript:void(0);' class='jm_tab' onclick='_objTab.seleccionar(8);'>Cambio de Cliente</a>");
                out.print("<a id='tab05' href='javascript:void(0);' class='jm_tab' onclick='_objTab.seleccionar(5);'>Cambio de Planes</a>");
                out.print("<a id='tab06' href='javascript:void(0);' class='jm_tab' onclick='_objTab.seleccionar(6);'>Suspenciones</a>");
                out.print("<a id='tab07' href='javascript:void(0);' class='jm_tab' onclick='_objTab.seleccionar(7);'>DesInstalaci&oacute;n</a>");
            }
            out.print("</div>");

            out.print("<form id='formVenta' action='frmInstalacionGuardar' onsubmit='return fac_installGuardar(this)' autocomplete='off'>");
            out.print("<input type='hidden' id='idFactura' name='idFactura' value='"+idFactura+"' />"
                    + "<input type='hidden' id='idCI' name='idCI' value='"+id_comprobante_ingreso+"' />"
                    + "<input type='hidden' id='idRV' name='idRV' value='"+id_retencion_venta+"' />"
                    /*+ "<input type='hidden' id='tf' name='tf' value='p' />"                           tipo de factura  */
                    /*+ "<input type='hidden' id='idCl' name='idCl' value='"+id_cliente+"' />"*/
                    + "<input type='hidden' id='secDe' name='secDe' value='"+fac_sec_desde+"' />"
                    + "<input type='hidden' id='secHa' name='secHa' value='"+fac_sec_hasta+"' />"
                    + "<input type='hidden' id='feCad' name='feCad' value='"+fac_cad_libretin+"' />"
                    + "<input type='hidden' id='utSuc' name='utSuc' value='0' />"
                    + "<input type='hidden' id='utCli' name='utCli' value='0' />"
                    + "<input type='hidden' id='elect' name='elect' value='0' />"
                    + "<input type='hidden' id='saldo_doc' name='saldo_doc' value='0' />"
                    + "<input type='hidden' id='num_cuenta' name='num_cuenta' value='"+num_cuenta+"' />"
                    + "<input type='hidden' id='p_iva1' name='p_iva1' value='"+p_iva1+"' />"
                    + "<input type='hidden' id='son' name='son' value='' />"
                    + "<input type='hidden' id='tope' name='tope' value='0' />"
                    + "<input type='hidden' id='imp' name='imp' value='1' />");
            out.print("<input type='hidden' id='radusername_ant' name='radusername_ant' value='"+radusername+"' />");

            out.print("<div id='tabR1'>");






            /* INSTALACION  -       PRIMER TAB */






            out.print("<div id='tab10' class='jm_tabHTML' style='display:block;width:750px;height:"+_altBody+"px;'>");


            if(id.compareTo("-1")==0){
                out.print(" &nbsp;Establecimiento - C&eacute;dula o RUC:  <input type='text' id='estab' name='estab' size='2' maxlength='3' onkeypress='_numero(event)' value='"+establecimiento+"' /> - <span class='marca'>*</span> <input id='ruc' name='ruc' type='text' size='13' maxlength='13' value='"+ruc+"' class='may' onkeypress=\"_DNI(event);\" onkeyup=\"var c=(_esN)?event.which:event.keyCode;if(c==13){_inhibir(event);fac_getClienteInstall();}\" />");
                out.print(" &nbsp;<input type='button' value='Buscar' onclick='fac_getClienteInstall();'>");
            }else{
                out.print(" &nbsp;C&eacute;dula o RUC: &nbsp;&nbsp;"+ruc+"<input id='ruc' name='ruc' type='hidden' value='"+ruc+"' />");
            }

            //out.print("<form action='frmInstalacionGuardar' onsubmit='return fac_installGuardar(this)' autocomplete='off'>");
            out.print("<input type='hidden' id='id' name='id' value='"+id+"' />");
            out.print("<input type='hidden' id='id_personalizacion' name='id_personalizacion' value='"+id_personalizacion+"' />");
            out.print("<input type='hidden' id='borrInstall' value='"+borrInstall+"' />");
            out.print("<input type='hidden' id='borrIPs' value='"+borrIPs+"' />");
            out.print("<input type='hidden' id='borrSect' value='"+borrSect+"' />");
            out.print("<div style='display:none' id='borrCliente'></div>");

            out.print("<div id='datCliente'>");
            if(id.compareTo("-1")!=0){
                out.print("<input type='hidden' id='id_cliente' name='id_cliente' value='"+id_cliente+"' />");
                out.print("<table width='100%'><tr><td width='75'>Cliente: </td><td id='axRS'>"+razon_social+"</td>");
                out.print("<td>Cant&oacute;n: </td><td id='axCi'>"+ciudad+"</td></tr>");
                out.print("<tr><td>Tel&eacute;fonos:</td><td id='axTe'>"+telefono+" &nbsp;&nbsp;&nbsp; Claro:"+movil_claro+" &nbsp;&nbsp;&nbsp; Movistar:"+movil_movistar+"</td>");
                out.print("<td>Direcci&oacute;n: </td><td id='axDi'>"+direccion+"</td></tr></table>");

                out.print("<hr />");

                out.print("<table>");
                out.print("<tr><td>Tipo de registro: <span class='marca'>*</span></td>");
                out.print("<td>" + DatosDinamicos.combo("es_instalacion", es_instalacion, es_instalacion1, "_('bntFat').style.visibility=this.value=='t'?'visible':'hidden'") + 
                    //" &nbsp;&nbsp;&nbsp; Promoci&oacute;n: " + DatosDinamicos.combo("promocion", promocion, promocion1, "") + 
                    "</td></tr>");
                out.print("<tr><td>N&uacute;mero: <span class='marca'>*</span></td><td><span id='auxIdSuc'>"+id_sucursal+"</span>-<input id='num_instalacion' name='num_instalacion' type='text' size='10' value='"+num_instalacion+"' readonly onkeypress=\"_numero(event);\" /></td></tr>");

                if(Integer.valueOf(id_sucursal_matriz) == id_sucursal_sesion){
                     out.print("<tr><td>Sucursal: <span class='marca'>*</span></td><td>" + DatosDinamicos.combo(rsSucursales, "id_sucursal", id_sucursal, "fac_setInstalacion();fac_setSectores();", "", 100) + "</td></tr>");
                }else{
                    if(id.compareTo("-1")==0){
                        id_sucursal = String.valueOf(id_sucursal_sesion);
                    }
                    out.print("<tr><td>Sucursal: <span class='marca'>*</span></td><td><input type='hidden' id='id_sucursal' name='id_sucursal' value='"+id_sucursal+"' />"+objSuc.getNombre(id_sucursal)+"</td></tr>");
                }
                out.print("<tr><td>Emitir pre-facturas: <span class='marca'>*</span></td>");
                if(objRol.getAcceso(id_rol, "opPrefacturar")){
                    out.print("<td>" + DatosDinamicos.combo("cobrar", cobrar, cobrar1, "_('ax_cobrar').style.display=this.value=='t'?'none':'block'") + "</td></tr>");

                }else{
                    out.print("<td><input type='hidden' id='cobrar' name='cobrar' value='t' />"+(cobrar.compareTo("t")==0?"SI":"NO")+"</td></tr>");
                }
                out.print("<tr><td colspan='2'>");
                if(objRol.getAcceso(id_rol, "opPrefacturar")){
                    out.print("<table id='ax_cobrar' style=\"display:"+(cobrar.compareTo("t")==0?"none":"block")+"\"><tr><td>Motivo por no pre-facturar: <span class='marca'>*</span></td>");
                    out.print("<td><textarea id='motivo_no_cobrar' name='motivo_no_cobrar' rows='2' cols='70'>"+motivo_no_cobrar+"</textarea></td></tr></table>");
                }else{
                    out.print("<input type='hidden' id='motivo_no_cobrar' name='motivo_no_cobrar' value='' />");
                }
                out.print("</td></tr>");

                out.print("<tr><td>Provincia: </td>");
                out.print("<td><div id='cob0' style='display:table-cell'>" + DatosDinamicos.combo(rsProvincias, "prv", id_provincia, "_pX='getUbicacion(:cob2:,:prr:,:ci:,200);';_pY='getUbicacion(:cob2:,:prr:,:ci:,200);';getUbicacion('cob1','ci','prv',200);", "", 231) + "</div></td></tr>");
                out.print("<tr><td>Cant&oacute;n: </td>");
                /*out.print("<td nowrap><div id='cob1' style='float:left;display:table-cell'>" + DatosDinamicos.combo(rsCiudades, "ci", id_ciudad, "getUbicacion('cob2','prr','ci',200);", "", 200) + "</div>"
                        + "<div class='jm_icoCon' id='gr1' style='visibility:hidden;background-position: -545px 0px;' onclick=\"guardarUbicacion(null,'cob1','ci','gr1','add1',200,'prv');\" title='guardar'>&nbsp;</div>"
                        + "<div class='jm_icoCon' id='add1' style='background-position:-504px -14px;' onclick=\"insertarUbicacion('cob1','ci','gr1','add1',200,'prv');_pX='getUbicacion(:cob2:,:prr:,:ci:,200);';_pY='getUbicacion(:cob2:,:prr:,:ci:,200);';\" title='Insertar'>&nbsp;</div></td></tr>");*/
                out.print("<td nowrap><div id='cob1' style='float:left;display:table-cell'>" + DatosDinamicos.combo(rsCiudades, "ci", id_ciudad, "getUbicacion('cob2','prr','ci',200);", "", 200) + "</div></td></tr>");
                out.print("<tr><td>Parroquia: </td>");
                /*out.print("<td nowrap><div id='cob2' style='float:left;display:table-cell'>" + DatosDinamicos.combo(rsParroquias, "prr", id_parroquia, "", "", 200) + "</div>"
                        + "<div class='jm_icoCon' id='gr2' style='visibility:hidden;background-position: -545px 0px;' onclick=\"guardarUbicacion(null,'cob2','prr','gr2','add2',200,'ci');\" title='guardar'>&nbsp;</div>"
                        + "<div class='jm_icoCon' id='add2' style='background-position:-504px -14px;' onclick=\"insertarUbicacion('cob2','prr','gr2','add2',200,'ci');\" title='Insertar'>&nbsp;</div></td></tr>");*/
                out.print("<td nowrap><div id='cob2' style='float:left;display:table-cell'>" + DatosDinamicos.combo(rsParroquias, "prr", id_parroquia, "", "", 200) + "</div></td></tr>");


                out.print("<tr><td>Sector: <span class='marca'>*</span></td>");
                out.print("<td id='axIdSec'>" + DatosDinamicos.combo(rsSectores, "id_sector", id_sector, "fac_setPlanes();", " ", 200) + "</td></tr>");
                out.print("<tr><td>Costo de instalaci&oacute;n: </td>");
                out.print("<td><input id='costo_instalacion' name='costo_instalacion' type='text' size='15' readonly value='"+costo_instalacion+"' /></td></tr>");
                out.print("<tr><td>Plan actual vigente: <span class='marca'>*</span></td>");
                out.print("<td id='auxPlanes'>" + DatosDinamicos.combo(rsPlanes, "id_plan_contratado", id_plan_actual, "", " ", 200) + "</td></tr>");
                out.print("<tr><td>Direcci&oacute;n de instalaci&oacute;n: <span class='marca'>*</span></td>");
                out.print("<td><input id='direccion_instalacion' name='direccion_instalacion' type='text' class='may' size='80' maxlength='200' value='"+direccion_instalacion+"' onkeypress=\"_alfanumerico(event);\" onblur=\"this.value=this.value._trim();\" /></td></tr>");
                out.print("<tr><td>Direcci&oacute;n IP: <span class='marca'>*</span></td>");
                out.print("<td><input id='ip' name='ip' type='text' size='15' maxlength='15' value='"+ip+"' onkeyup=\"fac_cmbActivo('ip');fac_getIpsReutilizar();\" onkeypress=\"_evaluar(event, '0123456789.');\" />"
                        + "&nbsp;&nbsp;&nbsp; IP radio: <input id='ip_radio' name='ip_radio' type='text' size='15' maxlength='15' value='"+ip_radio+"' onkeypress=\"_evaluar(event, '0123456789.');\" /> </td></tr>");

                out.print("<tr><td>Usuario PPOE: </td>");
                out.print("<td><input type='text' class='may' id='radusername' name='radusername' size='34' value='"+radusername+"' onblur=\"this.value=this.value._trim();\" onkeypress=\"_evaluar(event, '0123456789ABCDEFabcdef')\" /> "
                        + " &nbsp;&nbsp;&nbsp; <input type='Button' value='Actualizar Usuario PPOE' onclick=\"fac_userActualizar();\" /></td></tr>");
                out.print("<tr><td>Clave PPOE: </td>");
                out.print("<td>"+radclave+"</td></tr>");
                out.print("<tr valign='top'><td>Clave dispositivo: </td>");
                out.print("<td>"+deviceclave+" &nbsp;&nbsp;  Registrada<input type='checkbox' id='set_deviceclave' name='set_deviceclave' "+(set_deviceclave?"checked":"")+" /> </td></tr>");
                

                out.print("<tr><td>Estado: <span class='marca'>*</span></td>");
                if(id.compareTo("-1")!=0){
                    if(objRol.getAcceso(id_rol, "setEstadoServicio")){
                        out.print("<td>" + DatosDinamicos.combo("estado_servicio", estado_servicio, estado_servicio1, "") + "</td></tr>");
                    }else{
                        out.print("<td><input type='hidden' id='estado_servicio' name='estado_servicio' value='"+estado_servicio+"' />"+txt_estado_servicio+"</td></tr>");
                    }
                }

                out.print("<tr><td colspan='2'>");

                if(!bloqueado){
                    if(id.compareTo("-1")!=0 && objRol.getAcceso(id_rol, "installImprimir")){
                        out.print(" <input type='button' value='Re-Imprimir Formulario de Instalaci&oacute;n' onclick=\"window.open('pdfInstalacion?id='+_('id').value, '_blank')\" />");
                    }

                    if(id.compareTo("-1")!=0 && objRol.getAcceso(id_rol, "installEditar")){
                        out.print(" &nbsp;&nbsp;&nbsp;&nbsp; <input type='Button' value='Actualizar Informaci&oacute;n' onclick=\"fac_installActualizar(_('formVenta'));\" />");
                    }
                }
                /*if(id.compareTo("-1")!=0 && objRol.getAcceso(id_rol, "installEditar")){
                    out.print(" <input type='submit' value='Guardar' />");
                }*/

                out.print("</td></tr>");
                out.print("</table>");
                //out.print("</form>");
            }
            out.print("</div>");
            
            out.print("</div>"); // del tab

            


            /* CROQUIS -        SEGUNDO TAB */



            Archivo archivo = new Archivo(this._ip, this._puerto, this._db, usuario, clave);
            String path = String.valueOf(request.getRequestURL());
            path = path.substring(0, path.lastIndexOf("/"));
            String croquis = path + "/img/" + archivo.getArchivo(this._dir, "tbl_instalacion", id, "nombre_img", "croquis");
            archivo.cerrar();

            String subir = (!bloqueado && id.compareTo("-1")!=0) ? "adm_setImagen('tbl_instalacion', 'nombre_img', 'croquis', "+id+");" : "";

            out.print("<div id='tab11' class='jm_tabHTML' style='display:none;width:750px;height:"+_altBody+"px;'>");

            out.print("<div class='grupo' style='width:352px;height:377px;cursor:pointer;float:left;' title='Haga click aqu&iacute; para insertar el croquis' onclick=\""+subir+"\">"
                    + "<img id='tagImg' src='"+croquis+"' width='350px' height='377px' border='0' /></div>");
            if(!bloqueado && id.compareTo("-1")!=0 && objRol.getAcceso(id_rol, "installImprimir")){
                out.print("<span> &nbsp; <input type='button' value='Imprimir Croquis' onclick=\"window.open('pdfCroquis?id='+_('id').value, '_blank')\" /></span>");
            }
            out.print("</div>");




            /* POST - INSTALACION           TERCER TAB */


            /*String json_productos = "";
            if(id.compareTo("-1")!=0){
                ResultSet rsProductos = objProducto.getProductosVenta(id_sucursal_sesion);
                json_productos = objProducto.getJSON(rsProductos);
                try{
                    rsProductos.close();
                }catch(Exception e){
                    e.printStackTrace();
                }
            }*/

            Antena objAntena = new Antena(this._ip, this._puerto, this._db, usuario, clave);
            ResultSet rsAntenasAcopladas = objAntena.getAntenas();
            objAntena.cerrar();
            
            /*Activo objActivo = new Activo(this._ip, this._puerto, this._db, usuario, clave);
            ResultSet rsMacActivos = null;
            if(soloBodega){
                rsMacActivos = objActivo.getMacActivosBodega(id_sucursal_sesion);
            }else{
                rsMacActivos = objActivo.getMacActivos(id_sucursal_sesion);
            }
            objActivo.cerrar();*/

            ResultSet rsMateriales = objInstalacion.getMateriales(id);

            String [][] latitud_orientacion1 = {{"N","N"},{"S","S"}};
            String latitud_hora = "";
            String latitud_minuto = "";
            String latitud_segundo = "";
            String latitud_orientacion = "";
            if(latitud.compareTo("")!=0){
                String vec[] = latitud.split(":");
                latitud_hora = vec[0];
                latitud_minuto = vec[1];
                latitud_segundo = vec[2];
                latitud_orientacion = vec[3];
            }

            String [][] longitud_orientacion1 = {{"E","E"},{"O","O"}};
            String longitud_hora = "";
            String longitud_minuto = "";
            String longitud_segundo = "";
            String longitud_orientacion = "";
            if(longitud.compareTo("")!=0){
                String vec[] = longitud.split(":");
                longitud_hora = vec[0];
                longitud_minuto = vec[1];
                longitud_segundo = vec[2];
                longitud_orientacion = vec[3];
            }

            out.print("<div id='tab12' class='jm_tabHTML' style='display:none;width:750px;height:"+_altBody+"px;'>");

            out.print("<input type='hidden' id='ingreso_datos' name='ingreso_datos' value='"+ingreso_datos+"' />");
            out.print("<input type='hidden' id='id_comprobante_diario' name='id_comprobante_diario' value='"+id_comprobante_diario+"' />");

            out.print("<table cellspacing='0' cellpadding='0'><tr valign='top'><td>");
            out.print("<fieldset><legend>Operador</legend><table>");
            /*out.print("<tr><td>Activo asignado: <span class='marca'>*</span></td>");
            out.print("<td>" + DatosDinamicos.combo(rsActivos, "id_activo", id_activo, "", "", 200) + "</td></tr>");*/
            out.print("<tr><td>Fecha de registro de post-instalaci&oacute;n: </td>");
            out.print("<td>"+ingreso_datos+"</td></tr>");
            out.print("<tr><td>Fecha de instalaci&oacute;n: </td>");
            out.print("<td><input id='fecha_instalacion' name='fecha_instalacion' type='text' size='10' maxlength='10' value='"+fecha_instalacion+"' readonly />"
                    + "<input type='button' value='...' onClick=\"SelectorFecha.crear('fecha_instalacion', 'SQL');\" /></td></tr>");
            out.print("<tr><td>Receptor de se&ntilde;al: <span class='marca'>*</span></td>");
            out.print("<td><input id='receptor' name='receptor' type='text' size='24' maxlength='40' value='"+receptor+"' onblur=\"this.value=this.value._trim();\" /></td></tr>");

            out.print("<tr><td>Porcentaje de se&ntilde;al: <span class='marca'>*</span></td>");
            out.print("<td><input id='porcentaje_senal' name='porcentaje_senal' type='text' size='24' maxlength='5' value='"+porcentaje_senal+"' onkeypress=\"_evaluar(event,'0123456789.')\" /></td></tr>");

            out.print("<tr><td>Plan establecido: <span class='marca'>*</span></td>");
            out.print("<td>" + DatosDinamicos.combo(rsPlanes, "id_plan_establecido", id_plan_establecido, "", "", 150) + "</td></tr>");
            out.print("<tr><td>Conformidad de la velocidad establecida: <span class='marca'>*</span></td>");
            out.print("<td>" + DatosDinamicos.combo("conformidad_velocidad", conformidad_velocidad, conformidad_velocidad1, "") + "</td></tr>");
            out.print("<tr><td>Conformidad de la adecuada instalaci&oacute;n: <span class='marca'>*</span></td>");
            out.print("<td>" + DatosDinamicos.combo("conformidad_instalacion", conformidad_instalacion, conformidad1, "") + "</td></tr>");
            out.print("<tr><td>Conformidad de la atenci&oacute;n recibida: <span class='marca'>*</span></td>");
            out.print("<td>" + DatosDinamicos.combo("conformidad_atencion", conformidad_atencion, conformidad1, "") + "</td></tr>");
            out.print("<tr><td>Estado de la instalaci&oacute;n: <span class='marca'>*</span></td>");
            out.print("<td>" + DatosDinamicos.combo("estado_instalacion", estado_instalacion, estado_instalacion1, "") + "</td></tr>");
            out.print("</table></fieldset></td><td>");

            out.print("<fieldset><legend>Registro de Enlace</legend>");
            out.print("<table>");
            out.print("<tr><td>Direcci&oacute;n MAC: </td>");
            out.print("<td><input class='may' type='text' id='mac' name='mac' value='"+mac+"' size='34' onfocus=\"fac_cmbActivo('mac')\" onkeyup=\"fac_getActivosBodega('mac', 'fac_getReceptor()', 0)\" /></td></tr>");
            out.print("<tr><td>Direcci&oacute;n MAC POE: </td>");
            out.print("<td><input class='may' type='text' id='macp' name='macp' value='"+macp+"' size='34' onfocus=\"fac_cmbActivo('macp')\" onkeyup=\"fac_getActivosBodega('macp', 'fac_getReceptor()', 0)\" /></td></tr>");
            out.print("<tr><td>Direcci&oacute;n MAC GRID: </td>");
            out.print("<td><input class='may' type='text' id='macg' name='macg' value='"+macg+"' size='34' onfocus=\"fac_cmbActivo('macg')\" onkeyup=\"fac_getActivosBodega('macg', 'fac_getReceptor()', 0)\" /></td></tr>");
            //out.print("<td>" + DatosDinamicos.combo(rsMacActivos, "mac", mac, "fac_getReceptor()", "", 200) + "</td></tr>");
            out.print("<tr><td>Antena de trasmisi&oacute;n acoplada: <span class='marca'>*</span></td>");
            out.print("<td>" + DatosDinamicos.combo(rsAntenasAcopladas, "antena_acoplada", antena_acoplada, "", "", 200) + "</td></tr>");
            out.print("<tr><td>Latitud: </td>");
            out.print("<td><input id='lat_h' name='lat_h' type='text' style='width:20px' maxlength='2' value='"+latitud_hora+"' onkeypress=\"_numero(event)\" /> º "
                    + "<input id='lat_m' name='lat_m' type='text' style='width:20px' maxlength='2' value='"+latitud_minuto+"' onkeypress=\"_numero(event)\" /> ' "
                    + "<input id='lat_s' name='lat_s' type='text' style='width:40px' maxlength='6' value='"+latitud_segundo+"' onkeypress=\"_evaluar(event, '0123456789.')\" /> '' " +
                    DatosDinamicos.combo("lat_o", latitud_orientacion, latitud_orientacion1, "") + "</td></tr>");
            out.print("<tr><td>longitud: </td>");
            out.print("<td><input id='lon_h' name='lon_h' type='text' style='width:20px' maxlength='2' value='"+longitud_hora+"' onkeypress=\"_numero(event)\" /> º "
                    + "<input id='lon_m' name='lon_m' type='text' style='width:20px' maxlength='2' value='"+longitud_minuto+"' onkeypress=\"_numero(event)\" /> ' "
                    + "<input id='lon_s' name='lon_s' type='text' style='width:40px' maxlength='6' value='"+longitud_segundo+"' onkeypress=\"_evaluar(event, '0123456789.')\" /> '' " +
                    DatosDinamicos.combo("lon_o", longitud_orientacion, longitud_orientacion1, "") + "</td></tr>");
            out.print("<tr><td>Altura: </td>");
            out.print("<td><input id='altura' name='altura' type='text' size='24' maxlength='20' value='"+altura+"' onkeypress=\"_evaluar(event, '0123456789m')\" /></td></tr>");
            out.print("<tr><td>Altura antena: </td>");
            out.print("<td><input id='altura_antena' name='altura_antena' type='text' size='24' maxlength='20' value='"+altura_antena+"' onkeypress=\"_evaluar(event, '0123456789m')\" /></td></tr>");
            out.print("</table><p>&nbsp;</p><br /></fieldset>");

            out.print("</td></tr></table>");


            out.print("<div id='InAr'><a href='javascript:void(0);' style='visibility:hidden;float:left;' onclick=\"fac_filtrarMaterial();\">Insertar Material</a></div>" +
                        "&nbsp;&nbsp;<div style=\"display:none\" id='ax'></div>");


            /*out.print("<table cellpadding='0' cellspacing='0'>" +
                    "<TH class='jm_TH' width='100'>C&Oacute;DIGO</TH>" +
                    "<TH class='jm_TH' width='225'>DESCRIPCI&Oacute;N</TH>" +
                    "<TH class='jm_TH' width='40'>CANT.</TH>" +
                    "<TH class='jm_TH' width='60'>P./U.</TH>" +
                    "<TH class='jm_TH' width='80'>TOTAL</TH>" +
                    "<TH class='jm_TH' width='20'>&nbsp;&nbsp;</TH></tr></table>");*/

            out.print("<DIV id='sll0' style='overflow:auto;width:552px;height:100px;'>" +
                    "<TABLE class='jm_tabla' cellspacing='1' cellpadding='0'><tbody id='tblD'>");

            String html="";
            try{
                String clase = "";
                i=0;
                while(rsMateriales.next()){
                    clase = i%2==0 ? "jm_filaPar" : "jm_filaImp";
                    String idPr = (rsMateriales.getString("id_producto")!=null) ? rsMateriales.getString("id_producto") : "";
                    String codigo_mat = (rsMateriales.getString("codigo")!=null) ? rsMateriales.getString("codigo") : "";
                    String description = (rsMateriales.getString("descripcion")!=null) ? rsMateriales.getString("descripcion") : "";
                    String cantidad = (rsMateriales.getString("cantidad")!=null) ? rsMateriales.getString("cantidad") : "";
                    String costo_unitario = (rsMateriales.getString("costo_unitario")!=null) ? rsMateriales.getString("costo_unitario") : "";
                    String costo_total = (rsMateriales.getString("costo_total")!=null) ? rsMateriales.getString("costo_total") : "";

                    html += "<tr class='"+clase+"' id='r"+i+"'>";
                    html += "<td width='100' id='cF"+i+"0'>"+codigo_mat+"</td>";
                    html += "<td width='226'>"+description+"<input type='hidden' id='idPr"+i+"' name='idPr"+i+"' value='"+idPr+"' /></td>";
                    html += "<td width='41'><input type='text' onkeypress='_numero(event)' size='2' id='ct"+i+"' name='ct"+i+"' value='"+cantidad+"' onkeyup='fac_calcTotales("+i+")' /></td>";
                    html += "<td width='61'><input type='text' size='5' id='pu"+i+"' name='pu"+i+"' value='"+costo_unitario+"' readonly style='text-align:right' /></td>";
                    html += "<td width='81'><input type='text' size='5' id='sbt"+i+"' name='sbt"+i+"' value='"+costo_total+"' readonly style='text-align:right' /></td>";
                    html += "<td width='21' align='center'><div class='jm_icoCon' style='background-position:-545px -18px;' title='Eliminar' onclick=\"_R('r"+i+"');fac_sumValores();\">&nbsp;</div></div></td>";
                    html += "</tr>";
                    i++;
                }
                facturaDetalle.close();
            }catch(Exception e){
                e.printStackTrace();
            }
            html += "</tbody></table></div>";

            out.print(html);

            /*  CUANDO ES INSTALACION  */
            if(!bloqueado && (objRol.getAcceso(id_rol, "postInstall") || objRol.getAcceso(id_rol, "postInstallEditar"))){
                out.println("<input type='button' value='Guardar' onclick=\"_('imp').value=0;fac_postInstallGuardar();\" />");
                //out.println("&nbsp;&nbsp;&nbsp;&nbsp; <input type='button' value='Guardar e Imprimir Comprobante' onclick=\"_('imp').value=1;fac_postInstallGuardar();\" />");
            }
            /*if(!anulado && es_instalacion.compareTo("f")==0){
                out.println("<input type='button' value='Imprimir personalizaci&oacute;n de entrega recepci&oacute;n de activo' onclick=\"imprimir('pdfPersonalizacion?id="+id+"&idAc='+_('id_activo').value);\" />");
            }*/

            out.print("</div>");




            /* FACTURA      -   cuarto tab */





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



            modificar="readonly";
            try{
                ResultSet rsFactura = objFactura.getFactura(idFactura);
                facturaDetalle = objFactura.getFacturaDetalle(idFactura);

                if(rsFactura.next()){
                    id_comprobante_ingreso = (rsFactura.getString("id_comprobante_ingreso")!=null) ? rsFactura.getString("id_comprobante_ingreso") : "";
                    id_cliente = (rsFactura.getString("id_cliente")!=null) ? rsFactura.getString("id_cliente") : "";
                    ruc = rsFactura.getString("ruc")!=null ? rsFactura.getString("ruc") : "";
                    razon_social = rsFactura.getString("razon_social")!=null ? rsFactura.getString("razon_social") : "";
                    telefono = (rsFactura.getString("telefono")!=null) ? rsFactura.getString("telefono") : "";
                    direccion = (rsFactura.getString("direccion")!=null) ? rsFactura.getString("direccion") : "";
                    forma_pago = (rsFactura.getString("forma_pago")!=null) ? rsFactura.getString("forma_pago") : "";
                    serie_factura = (rsFactura.getString("serie_factura")!=null) ? rsFactura.getString("serie_factura") : "001-001";
                    autorizacion = (rsFactura.getString("autorizacion")!=null) ? rsFactura.getString("autorizacion") : "";
                    num_factura = (rsFactura.getString("num_factura")!=null) ? rsFactura.getInt("num_factura") : 0;
                    fecha_emision = (rsFactura.getString("fecha_emision")!=null) ? Fecha.ISOaSQL(rsFactura.getString("fecha_emision")) : "";
                    subtotal = (rsFactura.getString("subtotal")!=null) ? rsFactura.getDouble("subtotal") : 0;
                    subtotal_0 = (rsFactura.getString("subtotal_0")!=null) ? rsFactura.getString("subtotal_0") : "0";
                    subtotal_2 = (rsFactura.getString("subtotal_2")!=null) ? rsFactura.getString("subtotal_2") : "0";
                    descuento = (rsFactura.getString("descuento")!=null) ? rsFactura.getString("descuento") : "0";
                    iva_2 = (rsFactura.getString("iva_2")!=null) ? rsFactura.getDouble("iva_2") : 0;
                    total = (rsFactura.getString("total")!=null) ? rsFactura.getString("total") : "0";
                    observacion = (rsFactura.getString("observacion")!=null) ? rsFactura.getString("observacion") : "";
                    factura_anulada = (rsFactura.getString("anulado")!=null) ? rsFactura.getBoolean("anulado") : true;
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

                }
            }catch(Exception e){
                e.printStackTrace();
            }


            try{
                ResultSet rsRetencion = objFactura.getRetencion(idFactura);
                if(rsRetencion.next()){
                    ret_anulado = (rsRetencion.getString("anulado")!=null) ? rsRetencion.getBoolean("anulado") : false;
                    if(!ret_anulado){
                        id_retencion_venta = (rsRetencion.getString("id_retencion_venta")!=null) ? rsRetencion.getString("id_retencion_venta") : "-1";
                        ret_num_serie = (rsRetencion.getString("ret_num_serie")!=null) ? rsRetencion.getString("ret_num_serie") : "001-001";
                        ret_autorizacion = (rsRetencion.getString("ret_autorizacion")!=null) ? rsRetencion.getString("ret_autorizacion") : "";
                        ret_num_retencion = (rsRetencion.getString("ret_num_retencion")!=null) ? rsRetencion.getString("ret_num_retencion") : "";
                        ret_fecha_emision = (rsRetencion.getString("ret_fecha_emision")!=null) ? Fecha.ISOaSQL(rsRetencion.getString("ret_fecha_emision")) : Fecha.getFecha("SQL");
                        ret_ejercicio_fiscal_mes = (rsRetencion.getString("ret_ejercicio_fiscal_mes")!=null) ? rsRetencion.getString("ret_ejercicio_fiscal_mes") : ret_ejercicio_fiscal_mes;
                        ret_ejercicio_fiscal = (rsRetencion.getString("ret_ejercicio_fiscal")!=null) ? rsRetencion.getString("ret_ejercicio_fiscal") : ret_ejercicio_fiscal;
                        ret_impuesto_retenido = (rsRetencion.getString("ret_impuesto_retenido")!=null) ? rsRetencion.getString("ret_impuesto_retenido") : "";
                        id_plan_cuenta_caja_banco = (rsRetencion.getString("id_plan_cuenta_caja_banco")!=null) ? rsRetencion.getString("id_plan_cuenta_caja_banco") : "";
                    }
                    rsRetencion.close();
                }
            }catch(Exception e){
                e.printStackTrace();
            }



            String cobro = (forma_pago.compareTo("c")==0) ? "block" : "none";
            String cobro_dep_tran = (forma_pago.compareTo("p")==0 || forma_pago.compareTo("t")==0 || forma_pago.compareTo("j")==0) ? "block" : "none";
            String cobro_trans = (forma_pago.compareTo("c")==0 || forma_pago.compareTo("t")==0 || forma_pago.compareTo("j")==0) ? "block" : "none";

            String [][] forma_pago1 = {{"e","Efectivo"},{"c","Cheque"},{"p","Dep&oacute;sito bancario"},{"t","Transferencia bancaria"},{"j","Tarjeta de cr&eacute;dito"},{"d","Cr&eacute;dito"}};

            out.print("<div id='tab13' class='jm_tabHTML' style='display:none;width:750px;height:"+_altBody+"px;'>");

            out.print("<table width='100%' cellpadding='0' cellspacing='0'>" +
                    "<tr><td align='right'>F A C T U R A &nbsp;&nbsp; D E &nbsp;&nbsp; I N S T A L A C I O N </td>" +
                    "<td align='right'>Nro. <span class='marca'>*</span> "+
                    "<input type='text' id='sf' name='sf' size='6' maxlength='7' value='"+serie_factura+"' "+modificar+" onkeypress=\"_evaluar(event, '0123456789-');\" onkeyup='fac_setDetalleInstall()' />-" +
                    "<input type='text' size='6' id='nf' name='nf' value='"+num_factura+"' onkeypress=\"_evaluar(event, '0123456789-');\" onkeyup='fac_setDetalleInstall()' /></td></tr>"+
                    "<tr><td colspan='2' align='right'>Autorizaci&oacute;n: <span class='marca'>*</span> <input type='text' size='10' maxlength='10' id='auf' name='auf' value='"+autorizacion+"' "+modificar+" onkeypress=\"_numero(event);\" /></td>"
                    + "</tr></table>");

            out.print("<table cellpadding='0' cellspacing='0'>"+
                    "<tr><td width='110'>CC/RUC: <span class='marca'>*</span></td>"+
                    "<td width='263'><input type='text' size='13' maxlength='13' id='rucf' name='rucf' value='"+ruc+"' readonly onkeydown='var c=(_esN)?event.which:event.keyCode;if(c==27){fac_setConFinal();}' onkeypress='_numero(event);' />");
                    /*+ "<div style='display:table-cell'><input type='checkbox' id='vf' checked='checked' /> verificar &nbsp;&nbsp; </div>"*/

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
                        "<div id='pOtr' class='jm_columna' style=\"width:370px;display:"+cobro_dep_tran+";\">No. de comprobante de pago: <span class='marca'>*</span> <input id='ncp' name='ncp' type='text' size='20' maxlength='20' value='"+num_comp_pago+"' onkeypress=\"_numero(event);\" "
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

                    "<DIV style='overflow:auto;width:746px;height:140px;'>" +
                    "<TABLE class='jm_tabla' cellspacing='1' cellpadding='0'><tbody>");

            html="";
            try{
                String clase = "";
                i=0;
                while(facturaDetalle.next()){
                    clase = i%2==0 ? "jm_filaPar" : "jm_filaImp";
                    html += "<tr class='"+clase+"'>";
                    html += "<td width='100'>"+((facturaDetalle.getString("codigo")!=null) ? facturaDetalle.getString("codigo") : "")+"</td>";
                    html += "<td width='226'>"+((facturaDetalle.getString("descripcion")!=null) ? facturaDetalle.getString("descripcion") : "")+"</td>";
                    html += "<td width='41'>"+((facturaDetalle.getString("cantidad")!=null) ? facturaDetalle.getString("cantidad") : "")+"</td>";
                    html += "<td width='61' align='right'>"+((facturaDetalle.getString("p_u")!=null) ? facturaDetalle.getString("p_u") : "")+"</td>";
                    html += "<td width='71' align='right'>"+((facturaDetalle.getString("p_st")!=null) ? facturaDetalle.getString("p_st") : "")+"</td>";
                    html += "<td width='61' align='right'>"+((facturaDetalle.getString("descuento")!=null) ? facturaDetalle.getString("descuento") : "")+"</td>";
                    html += "<td width='61' align='right'>"+((facturaDetalle.getString("iva")!=null) ? facturaDetalle.getString("iva") : "")+"</td>";
                    html += "<td width='81' align='right'>"+((facturaDetalle.getString("total")!=null) ? facturaDetalle.getString("total") : "")+"</td>";
                    html += "<td width='21' align='center'>&nbsp;</div></td>";
                    html += "</tr>";
                    i++;
                }
                facturaDetalle.close();
            }catch(Exception e){
                e.printStackTrace();
            }

            if(html.compareTo("")==0){
                subtotal = Addons.redondear(costo_instalacion / 1.12);
                iva_2 = Addons.redondear(costo_instalacion - subtotal);
                total = String.valueOf(costo_instalacion);
                html += "<tr class='jm_filaPar'>";
                html += "<td width='100' id='cD00'>"+codigo+"</td>";
                html += "<td width='226' id='dSF01'>"+descripcion+"</td>";
                html += "<td width='41'><input type='text' size='2' id='cnt0' name='cnt0' readonly style='text-align:right' value='1' /></td>";
                html += "<td width='61' align='right'><input type='text' size='5' id='Fpu0' name='Fpu0' readonly style='text-align:right' value='"+subtotal+"' /></td>";
                html += "<td width='71' align='right'><input type='text' size='5' id='Fsbt0' name='Fsbt0' readonly style='text-align:right' value='"+subtotal+"' /></td>";
                html += "<td width='61' align='right'><input type='text' size='5' id='Fdes0' name='Fdes0' readonly style='text-align:right' value='0' /></td>";
                html += "<td width='61' align='right'><input type='text' size='5' id='Fv0' name='Fv0' readonly style='text-align:right' value='"+iva_2+"' /></td>";
                html += "<td width='81' align='right'><input type='text' size='8' id='Ftt0' name='Ftt0' readonly style='text-align:right' value='"+costo_instalacion+"' /></td>";
                html += "<td width='21' align='center'><input type='hidden' id='FidPr0' name='FidPr0' value='"+id_producto+"' />&nbsp;</div></td>";
                html += "</tr>";
            }

            out.print(html);

            out.print("</tbody></table></div>" +
                    "<table width='710' cellpadding='0' cellspacing='0'>" +
                    "<tr><td width='515'> Concepto .- <span class='marca'>*</span></td>" +
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


            String bancos[][] = DatosDinamicos.ResultSetToMatriz(rsBancos);
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
            }
            
            String [][] ret_ejercicio_fiscal_mes1 = {{"01","Enero"},{"02","Febrero"},{"03","Marzo"},{"04","Abril"},{"05","Mayo"},{"06","Junio"},
                {"07","Julio"},{"08","Agosto"},{"09","Septiembre"},{"10","Octubre"},{"11","Noviembre"},{"12","Diciembre"}};


            i=0;
            if(forma_pago.compareTo("d")!=0){

                out.println("<div id='tab14' class='jm_tabHTML' style='display:none;width:750px;height:"+_altBody+"px;'>");

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
                        "<td align='right'>Autorizaci&oacute;n: <input type='text' size='10' maxlength='10' id='aur' name='aur' value='"+ret_autorizacion+"' onkeypress=\"_numero(event);\" /></td></tr>" +

                        //"<tr><td colspan='3'>Aplicaci&oacute;n contable: "+ DatosDinamicos.combo("idPcCajBans", id_plan_cuenta_caja_banco, caja_bancos, "")+"</td></tr>"+

                        "</table>");

                if(!bloqueado){
                    out.println("<div id='InRe'><a href='javascript:void(0);' style='float:left;' onclick=\"inv_addRetencion();\">Insertar Retenci&oacute;n</a></div>");
                }else{
                    out.println("<div id='InRe'>&nbsp;</div>");
                }

                out.println("<div style=\"display:none\" id='axBI'>"+ DatosDinamicos.combo(rsBasesImponibles, "9a0", "9b0", "", "inv_sumRetSubTotales(9d0,1);inv_sumRetTotales();", "inv_sumRetSubTotales(9d0,1);inv_sumRetTotales();", "", 165) +"</div>"
                        + "&nbsp;&nbsp;<div style=\"display:none\" id='axCR'>"+ DatosDinamicos.combo(rsTablaImpuestos, "9a0", "9b0", "inv_setMsg('axMsR');", "inv_setConcepto(9c0);inv_sumRetSubTotales(9d0,1);inv_sumRetTotales();", "_('9e0').focus();", "", 295) +"</div>" +
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
                    String id_tabla_impuesto = "";
                    String campo_valor = "";
                    html = "";
                    while(retencionDetalle.next()){
                        id_tabla_impuesto = (retencionDetalle.getString("id_tabla_impuesto")!=null) ? retencionDetalle.getString("id_tabla_impuesto") : "1";
                        campo_valor = (retencionDetalle.getString("campo_valor")!=null) ? retencionDetalle.getString("campo_valor") : "";
                        html += "<tr id='rRe"+i+"' class='jm_filaPar'>";
                        html += "<td width='170'>" + campo_valor + "</td>";
                        html += "<td width='290'>" + DatosDinamicos.combo(rsTablaImpuestos, "idR"+i, id_tabla_impuesto, "inv_setMsg('axMsR');", "inv_setConcepto("+String.valueOf(i)+");", "inv_sumRetSubTotales("+i+");inv_sumRetTotales();_('bi"+i+"').focus();", "", 295) + "</td>";
                        html += "<td width='121' align='right'><input id='bi"+i+"' name='bi"+i+"' type='text' class='inpTbl' size='20' onkeypress=\"_evaluar(event, '0123456789.');\" onkeyup=\"inv_sumRetSubTotales("+i+");inv_sumRetTotales();\" " +
                                "onfocus=\"this.select();\" value='"+((retencionDetalle.getString("base_imponible")!=null) ? retencionDetalle.getString("base_imponible") : "")+"' /></td>";
                        html += "<td width='121' align='right'><input id='vr"+i+"' name='vr"+i+"' type='text' class='inpTbl' size='20' onkeypress=\"_evaluar(event, '0123456789.');\" onkeyup=\"inv_sumRetTotales();\" " +
                                "onfocus=\"this.select();\" value='"+((retencionDetalle.getString("valor_retenido")!=null) ? retencionDetalle.getString("valor_retenido") : "")+"' /></td>";
                        html += "<td width='21' align='center'><div class='jm_icoCon' style='visibility:"+ocultar+";background-position:-545px -16px;float:right;' title='Eliminar' onclick=\"_R('rRe"+i+"');inv_sumRetTotales();\">&nbsp;</div></td>";
                        html += "</tr>";
                        i++;
                    }
                    retencionDetalle.close();
                }catch(Exception e){
                    e.printStackTrace();
                }

                out.print(html);


                out.println("</table></div><table cellpadding='0' cellspacing='0'>" +
                        "<tr><td colspan='2' width='454' align='right'>TOTAL RETENCI&Oacute;N: </td>" +
                        "<td align='right'> &nbsp;<input type='text' id='tvr' name='tvr' value='"+ret_impuesto_retenido+"' style='text-align:right' onkeypress=\"_evaluar(event, '0123456789.');\" /></td>" +
                        "</tr></table>");

                try{
                    rsTablaImpuestos.close();
                }catch(Exception e){
                    e.printStackTrace();
                }

                out.print("</div>");

            }





           /*    CAMBIO DE RAZÓN SOCIAL    -      */







            out.print("<div id='tab18' class='jm_tabHTML' style='display:none;width:750px;height:"+_altBody+"px;'>");

            if(!bloqueado && id.compareTo("-1")!=0 && objRol.getAcceso(id_rol, "cambioClienteNuevo") && ax_fecha_instalacion.compareTo("")!=0){
                out.print("<div><a href='javascript:void(0);' id='inCP' onclick=\"fac_cambioClienteEditar(-1);\">Registrar Cambio de Raz&oacute;n Social</a></div>");
            }

            //out.print("<div style=\"display:none\" id='ax0'>"+ DatosDinamicos.combo(rsPlanes, "_Xx_", "", "fac_cambioClienteEditar(_Ii_);", "", 180)+"</div>");
            out.print("<table cellpadding='0' cellspacing='0'>" +
                        "<TH class='jm_TH' width='110'>CEDULA</TH>" +
                        "<TH class='jm_TH' width='350'>CLIENTE ANTERIOR</TH>" +
                        "<TH class='jm_TH' width='100'>FECHA</TH>" +
                        "<TH class='jm_TH' width='40'>&nbsp;</TH></tr></table>" +

                        "<DIV id='sllCP' style='overflow:auto;width:625px;height:350px;'>" +
                        "<TABLE class='jm_tabla' cellspacing='1' cellpadding='0' id='tblCC'>");
            try{
                ResultSet rsCambioPlanes = objInstalacion.getCambioClientes(id);
                String id_instalacion_cliente = "-1";
                String ruc_anterior = "";
                String cliente_anterior = "";
                String fecha_cambio = "";
                String editarCliente = "";
                html = "";
                i=0;
                while(rsCambioPlanes.next()){
                    id_instalacion_cliente = (rsCambioPlanes.getString("id_instalacion_cliente")!=null) ? rsCambioPlanes.getString("id_instalacion_cliente") : "-1";
                    ruc_anterior = (rsCambioPlanes.getString("ruc")!=null) ? rsCambioPlanes.getString("ruc") : "";
                    cliente_anterior = (rsCambioPlanes.getString("razon_social")!=null) ? rsCambioPlanes.getString("razon_social") : "";
                    fecha_cambio = (rsCambioPlanes.getString("fecha")!=null) ? rsCambioPlanes.getString("fecha") : "";
                    editarCliente = (!bloqueado && objRol.getAcceso(id_rol, "cambioClienteEditar")) ? " onclick='fac_cambioClienteEditar("+i+");'" : "";

                    html += "<tr id='rCC"+i+"' class='jm_filaPar'>";
                    html += "<td width='110' "+editarCliente+">"+ruc_anterior+"</td>";
                    html += "<td width='351' "+editarCliente+">"+cliente_anterior+"</td>";
                    html += "<td width='101' "+editarCliente+">"+fecha_cambio+"</td>";
                    html += "<td width='41'>";
                    if(!bloqueado && i==0){
                        html += "<div class='jm_icoCon' id='ci"+i+"' style='background-position: -314px -159px;' onclick=\"imprimir('pdfCambioCliente?id="+id_instalacion_cliente+"')\" title='imprimir solicitud'>&nbsp;</div>";
                        /*if(objRol.getAcceso(id_rol, "cambioClienteEliminar")){
                            html += "<div class='jm_icoCon' id='ce"+i+"' style='background-position:-545px -18px;float:right;' title='Eliminar' onclick=\"fac_cambioClienteEliminar("+id_instalacion_cliente+");\">&nbsp;</div>";
                        }*/
                    }
                    html += "</td></tr>";
                    i++;
                }
                out.print(html);
                rsCambioPlanes.close();
            }catch(Exception e){
                e.printStackTrace();
            }
            out.print("</table></div>");

            out.print("</div>");







            /*     CAMBIO DE PLANES   -     CUARTO TAB */







            out.print("<div id='tab15' class='jm_tabHTML' style='display:none;width:750px;height:"+_altBody+"px;'>");

            if(!bloqueado && id.compareTo("-1")!=0 && objRol.getAcceso(id_rol, "cambioPlanNuevo") && ax_fecha_instalacion.compareTo("")!=0){
                out.print("<div><a href='javascript:void(0);' id='inCP' onclick=\"fac_cambioPlanNuevo();\">Registrar Cambio de Plan</a></div>");
            }

            out.print("<div style=\"display:none\" id='ax0'>"+ DatosDinamicos.combo(rsPlanes, "_Xx_", "", "fac_cambioPlanEditar(_Ii_);", "", 180)+"</div>");
            out.print("<table cellpadding='0' cellspacing='0'>" +
                        "<TH class='jm_TH' width='200'>PLAN</TH>" +
                        "<TH class='jm_TH' width='150'>FECHA</TH>" +
                        "<TH class='jm_TH' width='60'>&nbsp;</TH></tr></table>" +

                        "<DIV id='sllCP' style='overflow:auto;width:435px;height:355px;'>" +
                        "<TABLE class='jm_tabla' cellspacing='1' cellpadding='0' id='tblCP'>");
            try{
                ResultSet rsCambioPlanes = objInstalacion.getCambioPlanes(id);
                String id_instalacion_cambio_plan = "-1";
                String fecha_cambio = "";
                String id_plan_servicio = "";
                html = "";
                i=0;
                while(rsCambioPlanes.next()){
                    id_instalacion_cambio_plan = (rsCambioPlanes.getString("id_instalacion_cambio_plan")!=null) ? rsCambioPlanes.getString("id_instalacion_cambio_plan") : "-1";
                    fecha_cambio = (rsCambioPlanes.getString("fecha_cambio")!=null) ? rsCambioPlanes.getString("fecha_cambio") : "";
                    id_plan_servicio = (rsCambioPlanes.getString("id_plan_servicio")!=null) ? rsCambioPlanes.getString("id_plan_servicio") : "";
                    html += "<tr id='rCP"+i+"' class='jm_filaPar'>";
                    html += "<td width='200'><input type='hidden' id='idCP"+i+"' value='"+id_instalacion_cambio_plan+"' />"
                            + DatosDinamicos.combo(rsPlanes, "idPlSvr"+i, id_plan_servicio, "fac_cambioPlanEditar("+i+");", "", 180) + "</td>";
                    html += "<td width='151'><input id='fchC"+i+"' type='text' size='10' maxlength='10' value='"+fecha_cambio+"' readonly /></td>";
                    //html += "<input type='button' value='...' onClick=\"SelectorFecha.crear('fchC"+i+"', 'SQL', 'fac_cambioPlanEditar("+i+");');\" /></td>";
                    html += "<td width='61'>";
                    if(!bloqueado){
                        if(objRol.getAcceso(id_rol, "cambioPlanNuevo") || objRol.getAcceso(id_rol, "cambioPlanEditar")){
                            html += "<div class='jm_icoCon' id='cg"+i+"' style='visibility:hidden;background-position: -545px 0px;' onclick='fac_cambioPlanGuardar("+i+");' title='guardar'>&nbsp;</div>";
                        }
                        html += "<div class='jm_icoCon' id='ci"+i+"' style='background-position: -314px -159px;' onclick='fac_cambioPlanImprimir("+i+");' title='imprimir solicitud'>&nbsp;</div>";
                        if(objRol.getAcceso(id_rol, "cambioPlanEliminar")){
                            html += "<div class='jm_icoCon' id='ce"+i+"' style='background-position:-545px -18px;float:right;' title='Eliminar' onclick=\"fac_cambioPlanEliminar("+i+");\">&nbsp;</div>";
                        }
                    }
                    html += "</td></tr>";
                    i++;
                }
                out.print(html);
                rsCambioPlanes.close();
            }catch(Exception e){
                e.printStackTrace();
            }
            out.print("</table></div>");

            out.print("</div>");





            /*     SUSPENCIONES   -     QUINTO TAB */




            //String [][] tipo1 = {{"t","Temporal"},{"d","Definitiva"}};
            String [][] tiempo1 = {{"0","un mes"},{"1","dos meses"},{"2","tres meses"},{"-1","definitiva"}};

            out.print("<div id='tab16' class='jm_tabHTML' style='display:none;width:750px;height:"+_altBody+"px;'>");

            if(!bloqueado && id.compareTo("-1")!=0 && objRol.getAcceso(id_rol, "suspensionNueva") && ax_fecha_instalacion.compareTo("")!=0){
                out.print("<div><a href='javascript:void(0);' id='inSus' onclick=\"fac_suspensionNueva();\">Registrar Suspensi&oacute;n</a></div>");
            }

            out.print("<table cellpadding='0' cellspacing='0'><tr>" +
                        "<TH class='jm_TH' width='100'>USUARIO</TH>" +
                        "<TH class='jm_TH' width='150'>INICIO</TH>" +
                        "<TH class='jm_TH' width='100'>TIEMPO</TH>" +
                        "<TH class='jm_TH' width='150'>F. ANULACION</TH>" +
                        "<TH class='jm_TH' width='100'>U. ANULACION</TH>" +
                        "<TH class='jm_TH' width='60'>SUSP.</TH>"
                        + "<TH class='jm_TH' width='50'>ANUL.</TH></tr></table>" +

                        "<DIV id='sll' style='overflow:auto;width:750px;height:355px;'>" +
                        "<TABLE class='jm_tabla' cellspacing='1' cellpadding='0' id='tblS'>");
            try{
                ResultSet rsSuspensiones = objInstalacion.getSuspensiones(id);
                String id_instalacion_suspension = "-1";
                String usuario_solicitud = "";
                String fecha_inicio = "";
                String tiempo = "";
                String reac_fecha_reactivacion = "";
                String reac_usuario_solicitud = "";
                String editar = "";
                boolean eliminado = false;
                html = "";
                i=0;
                while(rsSuspensiones.next()){
                    id_instalacion_suspension = (rsSuspensiones.getString("id_instalacion_suspension")!=null) ? rsSuspensiones.getString("id_instalacion_suspension") : "-1";
                    usuario_solicitud = (rsSuspensiones.getString("usuario_solicitud")!=null) ? rsSuspensiones.getString("usuario_solicitud") : "";
                    fecha_inicio = (rsSuspensiones.getString("fecha_inicio")!=null) ? Fecha.ISOaSQL(rsSuspensiones.getString("fecha_inicio")) : "";
                    tiempo = (rsSuspensiones.getString("tiempo")!=null) ? rsSuspensiones.getString("tiempo") : "1";
                    reac_fecha_reactivacion = (rsSuspensiones.getString("reac_fecha_reactivacion")!=null) ? rsSuspensiones.getString("reac_fecha_reactivacion") : "";
                    reac_usuario_solicitud = (rsSuspensiones.getString("reac_usuario_solicitud")!=null) ? rsSuspensiones.getString("reac_usuario_solicitud") : "";
                    eliminado = (rsSuspensiones.getString("eliminado")!=null) ? rsSuspensiones.getBoolean("eliminado") : false;
                    editar = tiempo.compareTo("-1")==0 ? "" : "fac_suspensionEditar("+i+");";
                    html += "<tr id='rS"+i+"' class='jm_filaPar'>";
                    html += "<td width='101'>"+usuario_solicitud+"</td>";
                    html += "<td width='151'><input type='hidden' id='idSus"+i+"' value='"+id_instalacion_suspension+"' />"
                            + "<input id='fchI"+i+"' type='text' size='10' maxlength='10' value='"+fecha_inicio+"' readonly />"
                    + "<input type='button' value='...' onClick=\"SelectorFecha.crear('fchI"+i+"', 'SQL', '"+editar+"');\" /></td>";
                    html += "<td width='101'>" + DatosDinamicos.combo("tmp"+i, tiempo, tiempo1, editar) + "</td>";
                    
                    html += "<td width='151'><input id='fchR"+i+"' type='text' size='10' maxlength='10' value='"+reac_fecha_reactivacion+"' readonly />";
                    if(tiempo.compareTo("-1")!=0){        
                        html += "<input type='button' value='...' onClick=\"SelectorFecha.crear('fchR"+i+"', 'SQL', 'fac_suspensionAnular("+i+");');\" /></td>";
                    }
                    html += "<td width='101'>"+reac_usuario_solicitud+"</td>";
                    
                    html += "<td width='61'>";
                    if(estado_servicio.compareTo("e")!=0 && estado_servicio.compareTo("t")!=0){
                        if(objRol.getAcceso(id_rol, "suspensionNueva") || objRol.getAcceso(id_rol, "suspensionEditar")){
                            html += "<div class='jm_icoCon' id='g"+i+"' style='visibility:hidden;background-position: -545px 0px;' onclick='fac_suspensionGuardar("+i+");' title='guardar'>&nbsp;</div>";
                        }
                        html += "<div class='jm_icoCon' id='i"+i+"' style='background-position: -314px -159px;' onclick='fac_suspensionImprimir("+i+");' title='imprimir solicitud'>&nbsp;</div>";
                        if(objRol.getAcceso(id_rol, "suspensionEliminar") && !eliminado){
                            html += "<div class='jm_icoCon' id='e"+i+"' style='background-position:-545px -18px;float:right;' title='Eliminar' onclick=\"fac_suspensionEliminar("+i+");\">&nbsp;</div>";
                        }
                    }
                    html += "</td>";
                    html += "<td width='51'>";
                    if(estado_servicio.compareTo("e")!=0 && estado_servicio.compareTo("t")!=0){
                        if(objRol.getAcceso(id_rol, "suspensionNueva") || objRol.getAcceso(id_rol, "suspensionEditar")){
                            html += "<div class='jm_icoCon' id='a"+i+"' style='visibility:hidden;background-position: -480px -208px;' onclick='fac_suspensionAnularGuarar("+i+");' title='guardar'>&nbsp;</div>";
                        }
                        if(reac_fecha_reactivacion.compareTo("")!=0){
                            html += "<div class='jm_icoCon' id='i1"+i+"' style='background-position: -314px -159px;' onclick='fac_suspensionAnulacionImprimir("+i+");' title='imprimir solicitud de anulación'>&nbsp;</div>";
                        }
                    }
                    html += "</td></tr>";
                    i++;
                }
                out.print(html);
                rsSuspensiones.close();
            }catch(Exception e){
                e.printStackTrace();
            }
            out.print("</table></div>");

            out.print("</div>");




            /*     DES-INSTALACION   -   TERMINACION   -     SEXTO TAB */



            if(num_desinstalacion.compareTo("")==0){
                num_desinstalacion = objInstalacion.getNumDesInstalacion(id_sucursal);
            }
            if(fecha_desinstalacion.compareTo("")==0){
                fecha_desinstalacion = Fecha.getFecha("SQL");
            }

            out.print("<div id='tab17' class='jm_tabHTML' style='display:none;width:750px;height:"+_altBody+"px;'>");

            out.print("<table><tr><td>Nro. de Des-Instalaci&oacute;n: </td>"
                    + "<td>"+id_sucursal+"-<input id='num_desinstalacion' name='num_desinstalacion' type='text' size='10' value='"+num_desinstalacion+"' onkeypress=\"_numero(event);\" /></td></tr>");
            out.print("<tr><td>Fecha de Des-Instalaci&oacute;n: <span class='marca'>*</span></td>");
            out.print("<td><input id='fecha_desinstalacion' name='fecha_desinstalacion' type='text' size='10' maxlength='10' value='"+fecha_desinstalacion+"' readonly />"
                    + "<input type='button' value='...' onClick=\"SelectorFecha.crear('fecha_desinstalacion', 'SQL');\" /></td></tr>");
            out.print("<tr valign='top'><td>Generar orden de trabajo: </td>"
                    + "<td><input type='checkbox' id='generar_orden_trab' name='generar_orden_trab' "+(generar_orden_trab?"checked":"")+" /></td></tr>");
            out.print("<tr valign='top'><td>Motivo: <span class='marca'>*</span></td>"
                    + "<td><textarea rors='8' cols='80' id='motivo_desinstalacion' name='motivo_desinstalacion'>"+motivo_desinstalacion+"</textarea></td></tr>");

            if(!anulado && (estado_servicio.compareTo("r")==0 || estado_servicio.compareTo("e")==0 || estado_servicio.compareTo("t")==0)){
                out.print("<tr valign='top'><td>Observaci&oacute;n post-desinstalaci&oacute;n: <span class='marca'>*</span></td>"
                    + "<td><textarea rors='8' cols='80' id='observacion_terminado' name='observacion_terminado'>"+observacion_terminado+"</textarea></td></tr>");
            }
            if(!anulado && (estado_servicio.compareTo("t")==0 || estado_servicio.compareTo("e")==0)){
                out.print("<tr><td colspan='2'> <hr /> </td></tr>");
                out.print("<tr><td>Fecha de env&iacute;o a ejecuci&oacute;n de orden de instalaci&oacute;n: <span class='marca'>*</span></td>");
                out.print("<td><input id='fecha_visita_instalacion' name='fecha_visita_instalacion' type='text' size='10' maxlength='10' value='"+fecha_visita_instalacion+"' readonly />"
                    + "<input type='button' value='...' onClick=\"SelectorFecha.crear('fecha_visita_instalacion', 'ISO');\" /></td></tr>");
                out.print("<tr valign='top'><td>Motivo de la no instalaci&oacute;n: <span class='marca'>*</span></td>"
                    + "<td><textarea rors='8' cols='80' id='motivo_no_instalacion' name='motivo_no_instalacion'>"+motivo_no_instalacion+"</textarea></td></tr>");
                out.println("<tr><td colspan='2'><input type='button' value='Imprimir Certificado de Terminaci&oacute;n de Instalaci&oacute;n' onclick=\"fac_no_instalacion("+id+");\" /></td></tr>");
            }

            out.print("</table>");

            if(!anulado && fecha_estado_r.compareTo("")==0 && objRol.getAcceso(id_rol, "desInstall")){
                out.println("<input id='bntDesI' type='button' value='Registrar Formulario de DesInstalaci&oacute;n' onclick=\"fac_desInstallGuardar();\" />");
            }
            if(!anulado && estado_servicio.compareTo("r")==0 && objRol.getAcceso(id_rol, "desInstall")){
                out.println("<input id='bntDesI' type='button' value='Re-Imprimir Formulario de DesInstalaci&oacute;n' onclick=\"imprimir('pdfDesInstalacion?id="+id+"');\" /> &nbsp;&nbsp;&nbsp;&nbsp; ");
                out.println("<input id='bntTrDI' type='button' value='Terminar DesInstalaci&oacute;n' onclick=\"fac_desInstallTerminar();\" />");
            }


            out.print("</div>");




            /*  BOTONES  */


            out.println("<table class='jm_tabla' width='100%' cellspacing='0' cellpadding='0'><tr>");
            if(!anulado){
                long fecha_sistema = Fecha.getTimeStamp(Fecha.getFecha("SQL"));
                long fecha_asiento = Fecha.getTimeStamp(fecha_registro);
                /*if(idFactura.compareTo("-1")!=0 && (objRol.getAcceso(id_rol, "compRetVentaNueva") || objRol.getAcceso(id_rol, "compRetVentaEditar"))){
                    out.println("<td><input type='button' value='Guardar Retenci&oacute;n' onclick=\"fac_retencionGardar("+id_retencion_venta+");\" /></td>");
                }
                if(idFactura.compareTo("-1")!=0 && objRol.getAcceso(id_rol, "compRetVentaAnular") && i>0 && fecha_sistema==fecha_asiento){
                    out.println("<td><input type='button' value='Anular Retenci&oacute;n' onclick='fac_retencionAnular("+id_retencion_venta+");' /></td>");
                }*/
                if(id.compareTo("-1")!=0 && objRol.getAcceso(id_rol, "installAnular")){
                    if(!bloqueado && es_instalacion.compareTo("t")==0 && !factura_anulada){
                        out.println("<td align='right'><input type='button' value='Re-Imprimir Factura' onclick=\"imprimir('pdfFacturaVenta?id="+idFactura+"');\" /></td>");
                    }
                    /*if(fecha_terminado.compareTo("")==0 && (fecha_sistema==fecha_asiento || es_instalacion.compareTo("t")!=0)){
                        out.println("<td align='right'><input type='button' id='btnAnr' value='Anular Instalaci&oacute;n' onclick='fac_instalacionAnular("+id+");' /></td>");
                    }*/
                }
                if(id.compareTo("-1")==0){
                    out.println("<td align='right'><input type='submit' value='Guardar' onclick=\"_('imp').value=0;\" /></td>");
                    out.println("<td align='right'><input type='submit' id='bntFat' value='Guardar e Imprimir Factura' onclick=\"_('imp').value=1;\" /></td>");
                }
            }else{
                out.println("<td><span class='marca'>FORMULARIO DE INSTALACION ANULADO</span></td>");
            }
            out.print("</tr></table>");



            out.print("</div></form></div>");

        } finally {
            objBaseImponible.cerrar();
            objPlanServicio.cerrar();
            objSector.cerrar();
            objProducto.cerrar();
            objCliente.cerrar();
            objTablaImpuesto.cerrar();
            objRol.cerrar();
            objFactura.cerrar();
            conf.cerrar();
            objSuc.cerrar();
            objInstalacion.cerrar();
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
