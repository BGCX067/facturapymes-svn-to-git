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

package jm;

import java.io.*;

import javax.servlet.*;
import javax.servlet.http.*;
import java.sql.ResultSet;
//import java.util.List;
import jm.adm.clas.Configuracion;
import jm.adm.clas.Sucursal;
import jm.nom.clas.Empleado;
import jm.web.Addons;
import jm.web.DataBase;
import jm.web.Fecha;

/**
 *
 * @author Jorge
 */
public class impReporteXls extends HttpServlet {
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
        int id_sucursal_sesion = (Integer)sesion.getAttribute("sucursal");
        String usuario = (String)sesion.getAttribute("usuario");
        String clave = (String)sesion.getAttribute("clave");

        if(request.getHeader("User-Agent").toLowerCase().contains("windows")){
            response.setContentType("application/vnd.ms-excel;");
            response.setHeader("Content-disposition", "inline; filename=reporte.xls;");
        }else{
            response.setContentType("text/xml;");
            response.setHeader("Content-disposition", "attachment; filename=reporte.ods;");
        }
        response.setHeader("Pragma", "no-cache");
        response.setHeader("Expires", "Mon, 01 Jan 2001 00:00:01 GMT");
        response.setHeader("Cache-Control", "no-store");
        response.setHeader("Cache-Control", "must-revalidate");
        response.setHeader("Cache-Control", "no-cache");

        PrintWriter out = response.getWriter();

        Configuracion conf = new Configuracion(this._ip, this._puerto, this._db, usuario, clave);
        String iva_vigente = conf.getValor("p_iva1");
        String id_sucursal_matriz = conf.getValor("id_sucursal_matriz");

        DataBase objDB = new DataBase(this._ip, this._puerto, this._db, usuario, clave);
        String path = String.valueOf(request.getRequestURL());
        path = path.substring(0, path.lastIndexOf("/"));
        String subtitulo = "";
        String xls = "";
        /*String sql = "";
        String w = "";*/
        String [] cabTabla = null;
        ResultSet registros = null;
        ReporteXls reporte = new ReporteXls();
        try {
            String WHERE = request.getParameter("WH")!=null ? " "+request.getParameter("WH") : "";
            int op = Integer.parseInt(request.getParameter("op"));
            //int op2 = Integer.parseInt(request.getParameter("op2"));
            //String id_combos = request.getParameter("idS");
            String fecha_ini = request.getParameter("fi");
            String fecha_fin = request.getParameter("ff");
            String id_sucursal = request.getParameter("idSuc");
            Sucursal objSucursal = new Sucursal(this._ip, this._puerto, this._db, usuario, clave);
            String nombre_sucursal = objSucursal.getNombre(id_sucursal);
            objSucursal.cerrar();
            int numDesde = 50;

            switch(op){
                case 1:
                    String sucursal = id_sucursal.compareTo("-0")!=0 ? "id_sucursal="+id_sucursal+" and" : "";
                    registros = objDB.consulta("select codigo_cuenta, nombre_cuenta, sum(debe) as sum_debe, sum(haber) as sum_haber, " +
                            "case when tipo_cuenta=0 then sum(debe)-sum(haber) else 0.00 end as saldo_deudor, case when tipo_cuenta=1 then sum(haber)-sum(debe) else 0.00 end as saldo_aceedor, tipo_cuenta " +
                            "from vta_libro_diario_mayor where "+sucursal+" fecha_asiento between '"+fecha_ini+"' and '"+fecha_fin+"' " +
                            "group by codigo_cuenta,nombre_cuenta,tipo_cuenta order by codigo_cuenta;");
                    subtitulo = "BALANCE DE COMPROBACIÓN. Del "+Addons.fechaAl(fecha_ini)+"  al  "+Addons.fechaAl(fecha_fin)+".";
                    cabTabla = new String [] {"Nro.", "CODIGO", "CUENTA", "SUMA DEBE", "SUMA HABER", "SALDO DEUDOR", "SALDO ACREEDOR"};
                    numDesde = 3;
                break;

                case 5:
                    registros = objDB.consulta("select ruc, razon_social, direccion, ciudad, pais, telefono, email, contacto from vta_proveedor"+WHERE);
                    subtitulo = "Lista de proveedores.";
                    cabTabla = new String [] {"Nro.", "RUC", "CLIENTE", "DIRECCION", "CIUDAD", "PAIS", "TELEFONO", "EMAIL", "CONTACTO"};
                    numDesde = 9;
                break;

                case 6:
                    subtitulo = "Lista de Productos.";
                    if(fecha_ini.compareTo("vta_producto")==0){
                        registros = objDB.consulta("select categoria,codigo,descripcion,unidad_medida,stock,precio_costo from vta_producto "+WHERE+" ORDER BY categoria,codigo;");
                        cabTabla = new String [] {"Nro.", "CATEGORIA", "CODIGO", "DESCRIPCION", "U. MEDIDA", "STOCK", "P. COSTO"};
                        numDesde = 5;
                    }
                    if(fecha_ini.compareTo("vta_sucursal_producto")==0){
                        registros = objDB.consulta("select categoria,codigo,descripcion,unidad_medida,stock,precio_costo,precio_venta,descuento from vta_sucursal_producto "+WHERE+" ORDER BY categoria,codigo;");
                        cabTabla = new String [] {"Nro.", "CATEGORIA", "CODIGO", "DESCRIPCION", "U. MEDIDA", "STOCK", "P. COSTO", "P. VENTA", "% DESC."};
                        numDesde = 7;
                    }
                break;

                case 7:
                    registros = objDB.consulta("select ruc, razon_social, direccion, ciudad, pais, telefono, movil_claro, movil_movistar, email from vta_cliente"+WHERE);
                    subtitulo = "Lista de clientes.";
                    cabTabla = new String [] {"Nro.", "RUC", "RAZON SOCIAL", "DIRECCION", "CIUDAD", "PAIS", "TELEFONO", "CLARO", "MOVISTAR", "EMAIL"};
                    numDesde = 10;
                break;

                case 10:
                    registros = objDB.consulta("select departamento,rol,dni,empleado,txt_sexo,txt_estado_civil,direccion,edad,telefono,movil,movil_movistar,email,ciudad,nacionalidad,toDateSQL(fecha_ingreso),banco,cuenta from vta_empleado"+WHERE+" order by departamento,rol,empleado");
                    subtitulo = "Lista de empleados.";
                    cabTabla = new String [] {"Nro.", "DEPARTAMENTO", "CARGO", "IDENTIFICACION", "NOMBRES", "GENERO", "ESTADO CIVIL", "DIRECCION", "EDAD", "TELF.", "CLARO", "MOVISTAR", "EMAIL", "CIUDAD", "NACIONALIDAD", "FECHA DE INGRESO", "BANCO", "CUENTA"};
                    numDesde = 22;
                break;

                /*case 12:
                    registros = objDB.consulta("select departamento,empleado,rol,toDateSQL(periodo),ciudad,total_ingreso,total_egreso,total_pagar from vta_rol_pago_detalle"+WHERE+" order by departamento,rol,empleado");
                    subtitulo = "Roles de Pagos: Liquidación General.";
                    cabTabla = new String [] {"Nro.", "DEPARTAMENTO", "EMPLEADO.", "CARGO", "PERIODO", "CIUDAD", "INGRESOS", "EGRESOS", "LIQUIDO"};
                    numDesde = 7;
                break;

                case 13:
                    registros = objDB.consulta("select departamento,rol,empleado,toDateSQL(periodo),ciudad,total_ingreso,vacaciones,trece_sueldo,catorce_sueldo,total_sociales,sbu,personal_iess,patronal_iess,iece,secap,fondo_reserva,total_patronales from vta_rol_pago_detalle"+WHERE+" order by departamento,rol,empleado");
                    subtitulo = "Provisiones Sociales.";
                    cabTabla = new String [] {"Nro.", "DEPARTAMENTO", "CARGO", "EMPLEADO", "PERIODO", "CIUDAD", "ING.", "VACACACIOES", "13ER SUELDO", "14TO SUELDO", "TOTAL", "SBU", "AP. PERS. IESS", "AP. PAT. IESS", "0.5% IECE", "0.5% SECAP", "FONDO RESERVA", "TOTAL"};
                    numDesde = 7;
                break;*/

                case 14:
                    registros = objDB.consulta("select sucursal,categoria,codigo_activo,descripcion,ubicacion,marca, serie, modelo, num_partes, color,toDateSQL(fecha_compra),valor_compra from vta_activo"+WHERE+" order by sucursal,categoria,codigo_activo");
                    subtitulo = "Activos Fijos.";
                    cabTabla = new String [] {"Nro.", "SUCURSAL", "CATEGORIA", "CODIGO", "DESCRIPCION", "UBICACION", "MARCA", "SERIE", "MODELO", "Nro. PARTES", "COLOR", "FECHA COMPRA", "VALOR COMPRA"};
                    numDesde = 12;
                break;

                case 15:
                    registros = objDB.consulta("select sucursal,categoria,codigo_activo,descripcion,ubicacion,toDateSQL(fecha_compra),valor_compra,valor_depreciado,dep_anual,dep_mensual from vta_activo"+WHERE+" order by sucursal,categoria,codigo_activo");
                    subtitulo = "Depreciaciones de Activos Fijos.";
                    cabTabla = new String [] {"Nro.", "SUCURSAL", "CATEGORIA", "CODIGO", "DESCRIPCION", "UBICACION", "FECHA COMPRA", "VALOR COMPRA", "VALOR DEPRECIADO", "DEP. ANUAL", "DEP. MENSUAL"};
                    numDesde = 7;
                break;

                case 16:
                    registros = objDB.consulta("select A.codigo_activo, A.descripcion, P.id_sucursal || '-' || P.num_documento, dni_entrega,persona_entrega, dni_recibe, persona_recibe, fecha, txt_tipo_movimiento "
                            + "from (tbl_activo as A inner join tbl_activo_custodio as C on A.id_activo=C.id_activo) inner join vta_activo_personalizacion as P on C.id_activo_personalizacion=P.id_activo_personalizacion "
                            + WHERE+" and A.eliminado=false and P.anulado=false order by P.id_sucursal,A.descripcion, P.fecha;");
                    subtitulo = "Movimientos de activos y custodios.";
                    cabTabla = new String [] {"Nro.", "CODIGO", "DESCRIPCION", "NRO. DOC.", "CED. ENTREGA", "ENTREGA", "CED. RECIBE", "RECIBE", "FECHA", "MOVIMIENTO"};
                    numDesde = 10;
                break;

                case 17:
                    registros = objDB.consulta("select P.plan, V.burst_limit, T.costo_plan, '1 - ' || P.div_max_limit as comparticion, P.id_nivel_soporte "
                            + "from (tbl_plan_isp as P inner join tbl_plan_servicio as V on P.id_plan_isp=V.id_plan_isp) "
                            + "inner join tbl_tarifa as T on T.id_plan_servicio=V.id_plan_servicio "
                            + "where P.eliminado=false and V.eliminado=false "
                            + "order by P.div_max_limit desc, P.plan, V.burst_limit::int");
                    subtitulo = "Planes de Servicio.";
                    cabTabla = new String [] {"Nro.", "PLAN", "VELOCIDAD", "COSTO", "COMPARTICION", "NIVEL SOPORTE"};
                    numDesde = 6;
                break;

                case 18:
                    registros = objDB.consulta("select sector,costo_instalacion from vta_sector"+WHERE+" order by sector,costo_instalacion");
                    subtitulo = "Sectores.";
                    cabTabla = new String [] {"Nro.", "SECTOR", "COSTO INSTALACION"};
                    numDesde = 2;
                break;

                case 19:
                    registros = objDB.consulta("select C.ruc, C.razon_social, toDateSQL(T.fecha_termino), C.direccion, C.ciudad, C.telefono, C.email, C.contacto "
                            + "from vta_cliente as C inner join tbl_contrato as T on C.id_cliente=T.id_cliente "
                            + "where T.anulado=false and fecha_termino between now()::date and (now() + '15 day'::interval)::date order by T.fecha_termino, C.razon_social;");
                    subtitulo = "Lista de clientes por terminar vigencia de contratos de servicio de Internet.";
                    cabTabla = new String [] {"Nro.", "RUC", "CLIENTE", "TERMINO DE CONTRATO", "DIRECCION", "CIUDAD", "TELEFONO", "EMAIL", "CONTACTO"};
                    numDesde = 10;
                break;

                case 20:
                    registros = objDB.consulta("select numero_instalacion, ruc, razon_social, edad, direccion_instalacion, P.provincia, ciudad, PQ.parroquia, sector, ip, plan, txt_comparticion, "
                            + "receptor, antena_acoplada, txt_estado_servicio from (vta_instalacion as I inner join vta_provincia as P on P.id_provincia=I.id_provincia) "
                            + "left outer join vta_parroquia as PQ on I.id_parroquia=PQ.id_parroquia " 
                            + WHERE + " order by numero_instalacion, estado_servicio, razon_social;");
                    subtitulo = "Listado de instalaciones de servicio de Internet.";
                    cabTabla = new String [] {"Nro.", "# INS.", "RUC", "CLIENTE", "EDAD", "DIR. INSTALACION", "PROVINCIA", "CIUDAD", "PARROQUIA", "SECTOR", "IP", "PLAN", "COMP.", "RECEPTOR", "ANT. ACOPLADA", "ESTADO"};
                    numDesde = 16;
                break;

                case 21:
                    registros = objDB.consulta("select numero_instalacion, ruc, razon_social, ip, plan, txt_comparticion, sector, direccion_instalacion, toDateSQL(fecha_instalacion) from vta_instalacion where anulado=false and ingreso_datos is null order by numero_instalacion,fecha_instalacion,razon_social;");
                    subtitulo = "Listado de instalaciones pendientes.";
                    cabTabla = new String [] {"Nro.", "# INS.", "RUC", "CLIENTE", "IP", "PLAN", "COMP.", "SECTOR", "DIRECCION", "F. INST."};
                    numDesde = 10;
                break;

                case 22:
                    registros = objDB.consulta("select numero_instalacion, ruc, razon_social, ip, sector, direccion_instalacion, porcentaje_senal, txt_conformidad_velocidad, "
                            + "txt_conformidad_instalacion, txt_conformidad_atencion, txt_estado_instalacion from vta_instalacion where anulado=false order by numero_instalacion,sector,razon_social;");
                    subtitulo = "Conformidad del servicio de instalación.";
                    cabTabla = new String [] {"Nro.", "# INS.", "RUC", "CLIENTE", "IP", "SECTOR", "DIRECCION", "% SEÑAL", "CONF. VEL.", "CONF. INST.", "CONF. ATE.", "ESTADO"};
                    numDesde = 12;
                break;

                case 23:
                    registros = objDB.consulta("select sector, id_sucursal || '-' || num_desinstalacion, ruc, razon_social, direccion_instalacion, telefono, ip, fecha_desinstalacion "
                            + "from vta_instalacion where anulado=false and estado_servicio='r' order by sector,razon_social;");
                    subtitulo = "Listado de clientes por retirar equipos instalados.";
                    cabTabla = new String [] {"Nro.", "SECTOR", "# DESINS.", "RUC", "CLIENTE", "DIRECCION", "TELEFONO", "IP", "F. DESINT."};
                    numDesde = 9;
                break;

                case 24:
                    registros = objDB.consulta("select sector, numero_instalacion, ruc, razon_social, ip, plan, txt_estado_servicio, burst_limit, tx_max_limit, svr_burst_limit, svr_burst_threshold, "
                            + "svr_burst_time, svr_direction, svr_disabled, svr_dst_address, svr_interface, svr_limit_at, svr_max_limit, svr_parent, svr_priority, svr_queue, svr_total_queue "
                            + "from vta_instalacion where anulado=false and (lower(burst_limit)<>lower(svr_burst_limit) or burst_limit is null or svr_burst_limit is null) order by sector,razon_social");
                    subtitulo = "Verificación de planes de servicio de Internet.";
                    cabTabla = new String [] {"Nro.", "#", "NRO. INS.", "RUC", "CLIENTE", "IP", "PLAN", "ESTADO SERVICIO", "BURST LIMIT PLAN", "MAX LIMIT PLAN",
                    "BURST LIMIT", "BURST THRESOLD", "BURST TIME", "DIRECTION", "DISABLED", "DST ADDRES", "INTERFACE", "LIMIT AT", "MAX LIMIT", "PARENT",
                    "PRIORITY", "QUEUE", "TOTAL QUEUE"};
                break;

                case 26:
                    registros = objDB.consulta("select vendedor, numero_factura, ruc, razon_social, txt_forma_pago, subtotal, iva_2, total from vta_factura_venta_all "
                            + "where anulado=false and id_sucursal="+id_sucursal_sesion+" and fecha_emision between '"+fecha_ini+"' and '"+fecha_fin+"' and id_factura_venta in "
                            + "(select distinct id_factura_venta from tbl_prefactura where fecha_emision between '"+fecha_ini+"' and '"+fecha_fin+"') order by vendedor, forma_pago;");
                    subtitulo = "Reacudo desde "+fecha_ini+" hasta "+fecha_fin+" agrupado.";
                    cabTabla = new String [] {"Nro.", "VENDEDOR", "Nro. FACT.", "RUC", "CLIENTE", "F. PAGO", "SUBT.", "IVA "+iva_vigente+"%", "TOTAL"};
                    numDesde = 6;
                break;

                case 27:
                    //objDB.consulta("select proc_calcularPreFacturasPendientes();");
                    String ini = request.getParameter("bfi");
                    String fin = request.getParameter("bff");
                    registros = objDB.consulta("select txt_periodo, sector, ruc, razon_social, direccion_instalacion, telefono, ip, plan, txt_estado_servicio, total from vta_prefactura "
                            + "where id_sucursal="+id_sucursal+" and fecha_emision is null and estado_servicio in ('a', 's', 'c', 'r', 'e', 'n') and periodo between '"+ini+"' and '"+fin+"' order by periodo,sector,razon_social;");
                    subtitulo = "Valores por recaudar desde la fecha "+ini+" hasta "+fin+".";
                    cabTabla = new String [] {"Nro.", "#", "SECTOR", "RUC", "CLIENTE", "DIRECCION", "TELEFONO", "IP", "PLAN", "ESTADO", "TOTAL"};
                    numDesde = 10;
                break;

                case 28:
                    registros = objDB.consulta("select sector, txt_periodo, ruc, razon_social, ip, plan, dias_conexion, valor_internet from vta_prefactura "
                            + "where id_sucursal="+id_sucursal_sesion+" and getFechaSuspension(fecha_prefactura) <= now()::date and fecha_emision is null order by sector,periodo,razon_social;");
                    subtitulo = "Listado de clientes a suspender el servicio por mora.";
                    cabTabla = new String [] {"Nro.", "SECTOR", "PERIODO", "RUC", "CLIENTE", "IP", "PLAN", "# DIAS", "VALOR INTERNET"};
                    numDesde = 7;
                break;

                case 29:
                    registros = objDB.consulta("select txt_estado, sector, id_sucursal || '-' || num_orden, ruc, razon_social, direccion_instalacion, telefono, ip, todatesql(fecha_reporte), txt_tipo_trabajo, responsable "
                            + "from vta_orden_trabajo"+WHERE+" order by estado,sector,razon_social;");
                    subtitulo = "Listado de ordenes de trabajo.";
                    cabTabla = new String [] {"Nro.", "#", "SECTOR", "# ORDEN", "RUC", "CLIENTE", "DIRECCION", "TELEFONO", "IP", "F. REP.", "TIPO TRABAJO", "RESPONSABLE"};
                    numDesde = 12;
                break;

                case 30:
                    registros = objDB.consulta("select ruc, razon_social, sector, id_sucursal || '-' || num_soporte, txt_fecha_llamada, alias_contesta, problema, diagnostico, txt_fecha_solucion, alias_solucion, recomendacion "
                            + "from vta_soporte"+WHERE+";");
                    subtitulo = "Listado de soportes del servicio de Internet.";
                    cabTabla = new String [] {"Nro.", "RUC", "CLIENTE", "SECTOR", "# SOP.", "FECHA LLAMADA", "CONTESTA", "PROBLEMA", "DIAGNOSTICO", "FECHA SOLUCION", "SOLUCIONA", "RECOMENDACIONES"};
                break;

                case 31:
                    registros = objDB.consulta("select receptor, mac, ip from tbl_instalacion where (anulado=true or estado_servicio in ('t','e') ) and ip not in "
                            + "(select ip from tbl_instalacion where estado_servicio not in ('t','e') and anulado=false) and id_sucursal="+id_sucursal_sesion+" order by ip");
                    subtitulo = "Listado de direcciones IPs a reutilizar.";
                    cabTabla = new String [] {"Nro.", "RECEPTOR", "MAC", "IP"};
                break;

                case 32:
                    registros = objDB.consulta("SELECT razon_social, numero_factura, toDateSQL(fecha_compra), cxp, deuda, observacion FROM vta_factura_compra where deuda>0 and anulado=false order by razon_social, fecha_compra");
                    subtitulo = "Facturas pendientes de pagar.";
                    cabTabla = new String [] {"Nro.", "RAZON SOCIAL", "NRO. FACT.", "FECHA COMPRA", "CxP", "SALDO", "CONCEPTO"};
                break;

                case 33:
                    registros = objDB.consulta("SELECT vendedor, razon_social, numero_factura, toDateSQL(fecha_emision), observacion, cxc, deuda FROM vta_factura_venta where deuda>0 and anulado=false order by vendedor, razon_social, fecha_emision");
                    subtitulo = "Facturas pendientes de cobrar.";
                    cabTabla = new String [] {"Nro.", "CAJA", "CLIENTE", "NRO. FACT.", "FECHA EMISION", "CONCEPTO", "CxC", "SALDO"};
                break;

                case 34:
                    registros = objDB.consulta("select sector, numero_instalacion, ruc, razon_social, ip, plan, plan_burst_limit "
                            + "from vta_instalacion where anulado=false and estado_servicio not in ('t','e') order by sector,razon_social;");
                    subtitulo = "Planes de servicio de Internet.";
                    cabTabla = new String [] {"Nro.", "SECTOR", "NRO. INS.", "RUC", "CLIENTE", "IP", "PLAN", "BURST LIMIT"};
                break;

                case 35:
                    String estado = request.getParameter("est");
                    String idSector = request.getParameter("idSec");
                    String idAntena = request.getParameter("idAnt");
                    String idPlanServicio = request.getParameter("idPlSe");
                    String w = estado.compareTo("-0")!=0 ? " and estado='"+estado+"'" : "";
                    w += (idSector.compareTo("-0")!=0 && idSector.compareTo("")!=0) ? " and id_sector="+idSector : "";
                    w += (idAntena.compareTo("-0")!=0 && idAntena.compareTo("")!=0) ? " and antena_acoplada='"+idAntena+"'" : "";
                    w += (idPlanServicio.compareTo("-0")!=0 && idPlanServicio.compareTo("")!=0) ? " and id_plan_actual="+idPlanServicio : "";
                    registros = objDB.consulta("select txt_estado, ruc, razon_social, ip, id_sucursal || '-' || num_soporte, txt_fecha_llamada, alias_contesta, problema, txt_fecha_solucion, alias_solucion, recomendacion "
                            + "from vta_soporte where fecha_llamada between '"+fecha_ini+"' and '"+fecha_fin+"' and id_sucursal="+id_sucursal+" "+w+" order by txt_estado, fecha_llamada, hora_llamada");
                    subtitulo = "Soportes del servicio de Internet General desde la fecha "+fecha_ini+" hasta "+fecha_fin+".";
                    cabTabla = new String [] {"Nro.", "ESTADO", "RUC", "CLIENTE", "IP", "# SOP.", "F. LLAMADA", "CONTESTA", "PROBLEMA", "F. SOLUCION", "SOLUCIONA", "RECOMENDACIONES"};
                break;

                case 36:
                    String orden = request.getParameter("idCmb");
                    String anio = request.getParameter("an");
                    String mes = request.getParameter("ms");
                    String fini = anio + "-" + mes + "-01";
                    String ffin = anio + "-" + mes + "-" + Fecha.getUltimoDiaMes(Integer.parseInt(anio), Integer.parseInt(mes));
                    registros = objDB.consulta("select ruc, razon_social, sector, ip, plan, txt_periodo, txt_estado_servicio, dias_conexion, valor_internet, subtotal, iva_2, total from vta_prefactura_todas "
                            + "where id_sucursal="+id_sucursal_sesion+" and fecha_emision is not null and periodo between '"+fini+"' and '"+ffin+"' order by "+orden);
                    subtitulo = "Recaudación del período " + Fecha.getTxtPeriodo(Integer.parseInt(anio), Integer.parseInt(mes)) + ".";
                    cabTabla = new String [] {"Nro.", "RUC", "CLIENTE", "SECTOR", "IP", "PLAN", "PERIODO", "ESTADO", "# DIAS", "VALOR INTERNET", "SUBT.", "IVA "+iva_vigente+"%", "TOTAL"};
                break;

                case 37:
                    String periodo = request.getParameter("pd");
                    int an = Fecha.datePart("anio", periodo);
                    int ms = Fecha.datePart("mes", periodo);
                    registros = objDB.consulta("select ruc, razon_social, sector, ip, plan, fecha_ini, fecha_fin from vta_anticipo_internet "
                            + "where id_sucursal="+id_sucursal_sesion+" and '"+periodo+"' between fecha_ini and fecha_fin order by fecha_ini");
                    subtitulo = "Servicios de Internet con pago anticipado para el período " + Fecha.getTxtPeriodo(an, ms) + ".";
                    cabTabla = new String [] {"Nro.", "RUC", "CLIENTE", "SECTOR", "IP", "PLAN", "FECHA INICIO", "FECHA FIN"};
                break;

                case 38:
                    registros = objDB.consulta("select distinct I.ruc, I.razon_social, I.sector, I.ip, I.plan, getAnioMes(P.periodo), toDateSQL(fecha_emision), I.txt_estado_servicio, P.dias_conexion, P.valor_internet "
                            + "from tbl_prefactura as P inner join vta_instalacion as I on I.id_instalacion=P.id_instalacion "
                            + "where I.id_sucursal="+id_sucursal_sesion+" and P.periodo = (select max(periodo) from tbl_prefactura) and P.fecha_emision is not null order by I.razon_social");
                    subtitulo = "Recaudación del último período.";
                    cabTabla = new String [] {"Nro.", "RUC", "CLIENTE", "SECTOR", "IP", "PLAN", "PERIODO", "EMISION", "ESTADO", "# DIAS", "VALOR INTERNET"};
                break;

                case 39:
                    cabTabla = new String [] {"Nro.", "CATEGORIA", "CODIGO", "DESCRIPCION", "CUSTORIO", "FECHA"};
                    if(fecha_ini.compareTo("s")==0){
                        subtitulo = "Activos que se encuentran en el cliente SAITEL";
                        registros = objDB.consulta("select categoria, codigo_activo, descripcion, id_activo from vta_activo "
                                + "where "+(id_sucursal.compareTo("-0")!=0 ? "id_sucursal="+id_sucursal+" and " : "")+
                                " codigo_activo not in (select distinct mac from tbl_instalacion where anulado=false and estado_servicio not in ('t','e') and mac<>'') order by categoria");
                        op=1001;
                    }else{
                        registros = objDB.consulta("select A.categoria, A.codigo_activo, A.descripcion, I.razon_social, toDateSQL(I.fecha_instalacion) from vta_activo as A inner join vta_instalacion as I on A.codigo_activo=I.mac "
                            + "where "+(id_sucursal.compareTo("-0")!=0 ? "A.id_sucursal="+id_sucursal+" and " : "")+" I.anulado=false and I.estado_servicio not in ('t','e') order by A.categoria");
                        subtitulo = "Activos que se encuentran en el cliente";
                    }
                break;

                case 40:
                    registros = objDB.consulta("SELECT razon_social,numero_factura,txt_num_cheque,toDateSQL(fecha_pago),abono,txt_tipo_pago FROM vta_factura_compra_pago where fecha_pago between '"+fecha_ini+"' and '"+fecha_fin+"' order by txt_tipo_pago, fecha_pago, razon_social");
                    subtitulo = "Pagos realizados desde la fecha "+fecha_ini+" hasta "+fecha_fin+".";
                    cabTabla = new String [] {"Nro.", "RAZON SOCIAL", "No. FACT.", "No. CHEQUE", "F. PAGO", "VALOR", "F.PAGO"};
                break;

                case 43:
                    String tt1 = "el cliente";
                    String sql = "select A.categoria, count(A.codigo_activo) from vta_activo as A inner join vta_instalacion as I on A.codigo_activo=I.mac "
                            + "where "+(id_sucursal.compareTo("-0")!=0 ? "A.id_sucursal="+id_sucursal+" and " : "")+" I.anulado=false and I.estado_servicio not in ('t','e') group by A.categoria order by A.categoria";
                    if(fecha_fin.compareTo("s")==0){
                        tt1 = "SAITEL";
                        sql = "select categoria, count(codigo_activo) from vta_activo where "+(id_sucursal.compareTo("-0")!=0 ? "id_sucursal="+id_sucursal+" and " : "")+" codigo_activo not in (select distinct mac from tbl_instalacion where anulado=false and estado_servicio not in ('t','e') and mac<>'') group by categoria order by categoria";
                    }

                    registros = objDB.consulta(sql);
                    subtitulo = "Activos que se encuentran en "+tt1 +" consolidado";
                    cabTabla = new String [] {"Nro.", "CATEGORIA", "Nro. ACTIVOS"};
                break;

                case 44:
                    anio = request.getParameter("an");
                    mes = request.getParameter("ms");
                    fecha_ini = anio + "-" + mes + "-01";
                    fecha_fin = anio + "-" + mes + "-" + Fecha.getUltimoDiaMes(Integer.parseInt(anio), Integer.parseInt(mes));
                    registros = objDB.consulta("SELECT distinct id_sucursal || '-' ||  num_instalacion, ruc, razon_social, toDateSQL(fecha_instalacion), ip, txt_estado_servicio FROM vta_instalacion where id_sucursal="+id_sucursal_sesion+" and id_instalacion not in "
                    + "(select id_instalacion from tbl_prefactura where periodo between '"+fecha_ini+"' and '"+fecha_fin+"') and anulado=false and estado_servicio in ('a','s','c') order by razon_social;");

                    subtitulo = "Lista de instalaciones sin prefactura para el período " + Fecha.getTxtPeriodo(Integer.parseInt(anio), Integer.parseInt(mes));
                    cabTabla = new String [] {"Nro.", "# INSTALACION", "RUC", "RAZON SOCIAL", "F. INSTALACION", "IP", "ESTADO"};
                break;

                case 45:
                    String dni = request.getParameter("an");
                    Empleado objEmpleado = new Empleado(this._ip, this._puerto, this._db, usuario, clave);
                    registros = objDB.consulta("select A.categoria, A.codigo_activo, A.descripcion, toDateSQL(C.fecha) from (vta_activo as A inner join tbl_activo_custodio as AC on A.id_activo=AC.id_activo) "+
                          "inner join tbl_activo_personalizacion as C on C.id_activo_personalizacion=AC.id_activo_personalizacion where AC.actual=true and AC.eliminado=false and C.dni_recibe='"+dni+"' order by A.categoria, A.codigo_activo");
                    subtitulo = "Activos que se encuentran custodiados por " + objEmpleado.getNombre(dni);
                    cabTabla = new String [] {"Nro.", "CATEGORIA", "CODIGO", "DESCRIPCION", "FECHA RECEPCION"};
                    objEmpleado.cerrar();
                break;

                case 46:
                    registros = objDB.consulta("select C.persona_recibe, A.categoria, count(A.categoria) from (vta_activo as A inner join tbl_activo_custodio as AC on A.id_activo=AC.id_activo) "+
                          "inner join tbl_activo_personalizacion as C on C.id_activo_personalizacion=AC.id_activo_personalizacion where AC.actual=true and AC.eliminado=false and "
                          + "C.dni_recibe in (SELECT dni FROM tbl_empleado) group by C.persona_recibe,A.categoria order by C.persona_recibe, A.categoria");
                    subtitulo = "Activos en custodios consolidado";
                    cabTabla = new String [] {"Nro.", "#", "CATEGORIA", "Nro. ACTIVOS"};
                break;

                case 48:
                    registros = objDB.consulta("select numero_instalacion, ruc, razon_social, ip, plan, txt_comparticion, sector, direccion_instalacion, toDateSQL(fecha_terminado) from vta_instalacion where anulado=false and fecha_terminado > '"+fecha_ini+"' order by numero_instalacion,fecha_terminado,razon_social");
                    subtitulo = "Listado de instalaciones termiadas desde la fecha "+fecha_ini+".";
                    cabTabla = new String [] {"Nro.", "# INS.", "RUC", "CLIENTE", "IP", "PLAN", "COMP.", "SECTOR", "DIRECCION", "F. DESINST."};
                break;

                case 49:
                    registros = objDB.consulta("select numero_instalacion, ruc, razon_social, ip, plan, txt_comparticion, sector, direccion_instalacion, motivo_no_cobrar "
                            + "from vta_instalacion where anulado=false and estado_servicio not in ('t','e') and cobrar=false order by numero_instalacion,razon_social");
                    subtitulo = "Direcciones IPs no pre-facturbles.";
                    cabTabla = new String [] {"Nro.", "# INS.", "RUC", "CLIENTE", "IP", "PLAN", "COMP.", "SECTOR", "DIRECCION", "MOTIVO"};
                break;

                case 50:
                    registros = objDB.consulta("select ruc, razon_social, ip, plan, fecha_registro, txt_estado_servicio from vta_instalacion "
                            + "where estado_servicio not in ('e','t') and anulado=false and fecha_instalacion is null "
                            + "and id_sucursal="+id_sucursal+" order by numero_instalacion, estado_servicio, razon_social");
                    subtitulo = "Post-instalaciones pendientes al "+fecha_fin+".";
                    cabTabla = new String [] {"Nro.", "RUC", "CLIENTE", "IP", "PLAN", "FECHA REGISTRO", "ESTADO"};
                break;

                case 51:
                    String fech_ini = request.getParameter("pd");
                    String fech_fin = request.getParameter("bff");
                    registros = objDB.consulta("select C.ruc, C.razon_social, serie_factura || '-' || num_factura, serie_nota || '-' || num_nota as num_nota, N.fecha_emision, cxp "
                            + "from (tbl_cliente as C inner join tbl_factura_venta as F on C.id_cliente=F.id_cliente) "
                            + "inner join tbl_nota_credito_venta as N on N.id_factura_venta=F.id_factura_venta "
                            + "where N.fecha_emision between '"+fech_ini+"' and '"+fech_fin+"' and N.anulado=false order by num_nota, C.razon_social");
                    subtitulo = "Notas de crédito desde la fecha "+fech_ini+" hasta "+fech_fin+".";
                    cabTabla = new String [] {"Nro.", "RUC", "CLIENTE", "# FACTURA", "# NOTA CREDITO", "F. EMISION", "VALOR"};
                break;
                    
                case 52:
                    registros = objDB.consulta("select ruc, razon_social, cliente_debito, num_cuenta, S.sucursal "
                            + "from vta_cliente as C inner join tbl_sucursal as S on S.id_sucursal=C.id_sucursal "
                            + "where num_cuenta<>'' and num_cuenta is not null and id_cliente in "
                            + "(select id_cliente from tbl_instalacion where estado_servicio in ('a', 'c', 'r') and fecha_instalacion is not null ) "
                            + "order by S.sucursal, razon_social");
                    subtitulo = "Clientes con convenio de débito.";
                    cabTabla = new String [] {"Nro.", "RUC", "RAZON SOCIAL", "CLIENTE DEBITO", "NUMERO CUENTA", "SUCURSAL"};
                break;
            
                case 53:
                    registros = objDB.consulta("select ruc, razon_social, carne_conadis, edad "
                            + "from vta_cliente where (carne_conadis<>'' and carne_conadis is not null) or (edad >= 65) order by razon_social");
                    subtitulo = "Clientes con capacidades especiales y tercera edad.";
                    cabTabla = new String [] {"Nro.", "RUC", "RAZON SOCIAL", "CARNE CONADIS", "EDAD"};
                break;
                    
                case 54:
                    registros = objDB.consulta("select ruc, razon_social, direccion_instalacion, txt_estado_servicio, plan, T.costo_plan "
                            + "from vta_instalacion as I inner join tbl_tarifa as T on I.id_plan_actual=T.id_plan_servicio "
                            + "where estado_servicio in ('a','s','c','r') "
                            + "order by txt_estado_servicio, razon_social;");
                    subtitulo = "Listado de instalaciones por estado del servicio.";
                    cabTabla = new String [] {"Nro.", "RUC", "RAZON SOCIAL", "DIRECCION", "ESTADO", "PLAN DEL SERVICIO", "COSTO DEL PLAN"};
                break; 
                    
                case 56:
                     periodo = request.getParameter("pd1");
                    an = Fecha.datePart("anio", periodo);
                    ms = Fecha.datePart("mes", periodo);
                    registros = objDB.consulta("select ruc, razon_social, sector, ip, plan, fecha_solicitud, fecha_inicio, case when tipo='d' then 'Definitiva' else (tiempo+1)::varchar end, usuario_solicitud "
                            + "from vta_instalacion as I inner join tbl_instalacion_suspension as S on I.id_instalacion=S.id_instalacion "
                            + "where I.id_sucursal="+id_sucursal_sesion+" and eliminado=false and '"+periodo+"' between S.fecha_inicio and S.fecha_termino order by S.fecha_inicio");
                    subtitulo = "Servicios de Internet suspendido para el período " + Fecha.getTxtPeriodo(an, ms) + ".";
                    cabTabla = new String [] {"Nro.", "RUC", "CLIENTE", "SECTOR", "IP", "PLAN", "FECHA DE SOLICITUD", "FECHA INICIO", "TIEMPO", "USUARIO"};
                break;    
                
            }


            if(op==2){
                    if(id_sucursal.compareTo("-0")!=0){
                        registros = objDB.consulta("select P.id_plan_cuenta, P.id_padre, P.codigo_cuenta, P.nombre_cuenta, case P.tipo_cuenta when 0 then D.suc_saldo_deudor when 1 then D.suc_saldo_acreedor else 0 end " +
                            "from tbl_plan_cuenta as P left outer join tbl_libro_diario_mayor as D on P.id_plan_cuenta=D.id_plan_cuenta " +
                            "where (P.tipo_cuenta=-1 or (id_sucursal="+id_sucursal + " and D.fecha_asiento<='"+fecha_fin+"' and (D.debe>0 or D.haber>0) and " +
                            "D.id_libro_diario_mayor in (select max(id_libro_diario_mayor) from vta_libro_diario_mayor where id_sucursal="+id_sucursal + " and fecha_asiento<='"+fecha_fin+"' and (debe>0 or haber>0) group by codigo_cuenta))) " +
                            "and (P.codigo_cuenta like '1%' or P.codigo_cuenta like '2%' or P.codigo_cuenta like '3%') or P.id_plan_cuenta=1 " +
                            "ORDER BY P.id_padre,P.codigo_cuenta;");
                    }else{
                        registros = objDB.consulta("select P.id_plan_cuenta, P.id_padre, P.codigo_cuenta, P.nombre_cuenta, case P.tipo_cuenta when 0 then D.saldo_deudor when 1 then D.saldo_acreedor else 0 end " +
                            "from tbl_plan_cuenta as P left outer join tbl_libro_diario_mayor as D on P.id_plan_cuenta=D.id_plan_cuenta " +
                            "where (P.tipo_cuenta=-1 or (D.fecha_asiento<='"+fecha_fin+"' and (D.debe>0 or D.haber>0) and " +
                            "D.id_libro_diario_mayor in (select max(id_libro_diario_mayor) from vta_libro_diario_mayor where fecha_asiento<='"+fecha_fin+"' and (debe>0 or haber>0) group by codigo_cuenta))) " +
                            "and (P.codigo_cuenta like '1%' or P.codigo_cuenta like '2%' or P.codigo_cuenta like '3%') or P.id_plan_cuenta=1 " +
                            "ORDER BY P.id_padre,P.codigo_cuenta;");
                    }
                    subtitulo = "BALANCE GENERAL. Al "+Addons.fechaAl(fecha_ini)+".";
                    xls = reporte.balanceGeneral(registros, subtitulo, nombre_sucursal);
             }else if(op==3){
                        registros = objDB.consulta("select P.id_plan_cuenta, P.id_padre, P.codigo_cuenta, P.nombre_cuenta, case P.tipo_cuenta when 0 then sum(D.debe)-sum(D.haber) when 1 then sum(D.haber)-sum(D.debe) else 0.00 end " +
                            "from tbl_plan_cuenta as P left outer join tbl_libro_diario_mayor as D on P.id_plan_cuenta=D.id_plan_cuenta " +
                            "where (P.tipo_cuenta=-1 or D.fecha_asiento between '"+fecha_ini+"' and '"+fecha_fin+"') and (P.codigo_cuenta like '4%' or P.codigo_cuenta like '5%') or P.id_plan_cuenta=1 " +
                            "group by P.codigo_cuenta,P.nombre_cuenta,P.id_plan_cuenta,P.id_padre,P.tipo_cuenta " +
                            "ORDER BY P.id_padre,P.codigo_cuenta;");
                        subtitulo = "ESTADO DE RESULTADOS. Del "+Addons.fechaAl(fecha_ini)+"  al  "+Addons.fechaAl(fecha_fin)+".";
                        xls = reporte.balanceResultados(registros, subtitulo, nombre_sucursal);
             }else if(op==8){
                        registros = objDB.consulta("select id_factura_venta,vendedor,ruc,razon_social,numero_factura,fecha_emision,case when anulado then 'ANULADA' else txt_forma_pago end,ip,subtotal,descuento,subtotal_0,iva_2,total,deuda from vta_factura_venta"+WHERE+" and anulado=false order by vendedor,id_factura_venta");
                        subtitulo = "Reporte de facturas de ventas. Del "+Addons.fechaAl(fecha_ini)+"  al  "+Addons.fechaAl(fecha_fin)+".";
                        xls = reporte.ventas(registros, subtitulo, nombre_sucursal, objDB, iva_vigente);
             }else if(op==11){
                        registros = objDB.consulta("SELECT *,toDateSQL(periodo) as periodo_sql FROM vta_rol_pago_detalle"+WHERE);
                        subtitulo = "Roles de Pagos: Liquidación Individual.";
                        xls = reporte.RPLiquidacionIndividual(registros, subtitulo, nombre_sucursal, objDB, objSucursal);
             }else if(op==25){
                        registros = objDB.consulta("select sector, email from vta_instalacion where anulado=false and email::varchar<>'' order by sector;");
                        subtitulo = "Cadena de correos electrónicos de clientes.";
                        xls = reporte.correos(registros, subtitulo);
             }else if(op==41){
                        String fecha = request.getParameter("idCmb");
                        subtitulo = "Cierre de caja del "+Addons.fechaAl(fecha)+".";
                        xls = reporte.cierreCaja(subtitulo, id_sucursal_sesion, fecha, objDB);
             }else if(op==42){
                        String banco = conf.getValor("bancos");
                        String caja = conf.getValor("caja");
                        String fecha1 = request.getParameter("idCmb");
                        subtitulo = "Ingresos y egresos del "+Addons.fechaAl(fecha1)+".";
                        xls = reporte.ingresosEgresos(subtitulo, id_sucursal_sesion, fecha1, objDB, banco, caja);
             }else if(op==47){
                        fecha_ini = request.getParameter("bfi");
                        fecha_fin = Fecha.getFecha("SQL");
                        ResultSet emisiones = objDB.consulta("select txt_sucursal, getaniomes(date_trunc('month', periodo)::date), sum(total) from vta_prefactura_todas where id_sucursal="+id_sucursal_sesion+" and periodo between '"+fecha_ini+"' and '"+fecha_fin+"' group by txt_sucursal, date_trunc('month', periodo) order by txt_sucursal,date_trunc('month', periodo)");

                        ResultSet recaudos = objDB.consulta("select txt_sucursal, getaniomes(date_trunc('month', periodo)::date), sum(total) from vta_prefactura_todas where id_sucursal="+id_sucursal_sesion+" and fecha_emision between '"+fecha_ini+"' and '"+fecha_fin+"' group by txt_sucursal, date_trunc('month', periodo) order by txt_sucursal,date_trunc('month', periodo)");

                        ResultSet xcobrar = objDB.consulta("select txt_sucursal, getaniomes(date_trunc('month', periodo)::date), sum(total) from vta_prefactura_todas where id_sucursal="+id_sucursal_sesion+" and fecha_emision is null and periodo between '"+fecha_ini+"' and '"+fecha_fin+"' group by txt_sucursal, date_trunc('month', periodo) order by txt_sucursal,date_trunc('month', periodo)");

                        ResultSet instalaciones = objDB.consulta("select txt_sucursal, getaniomes(date_trunc('month', fecha_registro)::date), sum(costo_instalacion) from vta_instalacion "
                            + "where fecha_registro between '"+fecha_ini+"' and '"+fecha_fin+"' and id_sucursal="+id_sucursal_sesion+" and anulado=false and es_instalacion=true "
                            + "group by txt_sucursal, date_trunc('month', fecha_registro) order by txt_sucursal,date_trunc('month', fecha_registro)"); // instalaciones

                        ResultSet otros = objDB.consulta("select txt_sucursal, getaniomes(date_trunc('month', fecha_emision)::date), sum(total) from vta_factura_venta "
                                + "where id_sucursal="+id_sucursal_sesion+" and fecha_emision between '"+fecha_ini+"' and '"+fecha_fin+"' and anulado=false and "
                                + "id_factura_venta not in (select P.id_factura_venta from tbl_prefactura as P inner join tbl_instalacion as I on P.id_instalacion=I.id_instalacion "
                                + "where P.periodo between '"+fecha_ini+"' and '"+fecha_fin+"' and P.id_factura_venta > 0 "
                                + "union "
                                + "select id_factura_venta from tbl_instalacion where fecha_registro between '"+fecha_ini+"' and '"+fecha_fin+"' and anulado=false and id_factura_venta > 0) group by txt_sucursal, date_trunc('month', fecha_emision) order by txt_sucursal,date_trunc('month', fecha_emision)"); // otros ingresos

                        if(Integer.parseInt(id_sucursal_matriz) == id_sucursal_sesion){
                            emisiones = objDB.consulta("select txt_sucursal, getaniomes(date_trunc('month', periodo)::date), sum(total) from vta_prefactura_todas where periodo between '"+fecha_ini+"' and '"+fecha_fin+"' group by txt_sucursal, date_trunc('month', periodo) order by txt_sucursal,date_trunc('month', periodo)");

                            recaudos = objDB.consulta("select txt_sucursal, getaniomes(date_trunc('month', periodo)::date), sum(total) from vta_prefactura_todas where fecha_emision between '"+fecha_ini+"' and '"+fecha_fin+"' group by txt_sucursal, date_trunc('month', periodo) order by txt_sucursal,date_trunc('month', periodo)");

                            xcobrar = objDB.consulta("select txt_sucursal, getaniomes(date_trunc('month', periodo)::date), sum(total) from vta_prefactura_todas where fecha_emision is null and periodo between '"+fecha_ini+"' and '"+fecha_fin+"' group by txt_sucursal, date_trunc('month', periodo) order by txt_sucursal,date_trunc('month', periodo)");

                            instalaciones = objDB.consulta("select txt_sucursal, getaniomes(date_trunc('month', fecha_registro)::date), sum(costo_instalacion) from vta_instalacion "
                                + "where fecha_registro between '"+fecha_ini+"' and '"+fecha_fin+"' and anulado=false and es_instalacion=true "
                                + "group by txt_sucursal, date_trunc('month', fecha_registro) order by txt_sucursal,date_trunc('month', fecha_registro)"); // instalaciones

                            otros = objDB.consulta("select txt_sucursal, getaniomes(date_trunc('month', fecha_emision)::date), sum(total) from vta_factura_venta "
                                    + "where fecha_emision between '"+fecha_ini+"' and '"+fecha_fin+"' and anulado=false and "
                                    + "id_factura_venta not in (select P.id_factura_venta from tbl_prefactura as P inner join tbl_instalacion as I on P.id_instalacion=I.id_instalacion "
                                    + "where P.periodo between '"+fecha_ini+"' and '"+fecha_fin+"' and P.id_factura_venta > 0 "
                                    + "union "
                                    + "select id_factura_venta from tbl_instalacion where fecha_registro between '"+fecha_ini+"' and '"+fecha_fin+"' and anulado=false and id_factura_venta > 0) group by txt_sucursal, date_trunc('month', fecha_emision) order by txt_sucursal,date_trunc('month', fecha_emision)"); // otros ingresos
                        }

                        subtitulo = "Consolidado de instalaciones, servicio de Internet y otros ingresos desde "+fecha_ini+".";
                        xls = reporte.ingresosPeriodos(subtitulo, nombre_sucursal, emisiones, recaudos, xcobrar, instalaciones, otros);
              }else if(op==55){
                        registros = objDB.consulta("select A.codigo_activo, max(P.num_documento) as num_personalizacion " +
                        "from ((tbl_activo as A inner join tbl_activo_custodio as C on A.id_activo=C.id_activo) " +
                        "inner join tbl_activo_personalizacion as P on P.id_activo_personalizacion=C.id_activo_personalizacion) " +
                        "where A.codigo_activo in (select distinct mac from tbl_instalacion where anulado=false) " + 
                        "and P.dni_recibe in (select ruc from tbl_cliente) " +
                        "group by A.codigo_activo " +
                        "order by A.codigo_activo");
                        subtitulo = "Equipos con últimas personalizaciones en clientes.";
                        cabTabla = new String [] {"RAZON SOCIAL", "EQUIPO"};
                        xls = reporte.activosPersonalizadosClientes(subtitulo, cabTabla, registros, objDB);
              }else if(op==1001){
                        xls = reporte.activosCustorios(subtitulo, cabTabla, registros, numDesde, nombre_sucursal, objDB);
                }else{
                        xls = reporte.lista(subtitulo, cabTabla, registros, numDesde, nombre_sucursal);
                }

            out.print(xls);

        } finally {
            conf.cerrar();
            try{
                registros.close();
            }catch(Exception e){
                e.printStackTrace();
            }
            objDB.cerrar();
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
