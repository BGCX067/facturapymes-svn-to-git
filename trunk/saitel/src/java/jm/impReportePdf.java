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
import java.sql.SQLException;
import jm.adm.clas.Configuracion;
import jm.adm.clas.Sucursal;
import jm.nom.clas.Empleado;
import jm.web.Addons;
import jm.web.Archivo;
import jm.web.DataBase;
import jm.web.Fecha;


/**
 *
 * @author Jorge
 */
public class impReportePdf extends HttpServlet {
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
        int id_sucursal_sesion = (Integer)sesion.getAttribute("sucursal");
        String usuario = (String)sesion.getAttribute("usuario");
        String clave = (String)sesion.getAttribute("clave");

        response.setContentType("application/pdf");
        response.setHeader("Pragma", "no-cache");
        response.setHeader("Expires", "Mon, 01 Jan 2001 00:00:01 GMT");
        response.setHeader("Cache-Control", "no-store");
        response.setHeader("Cache-Control", "must-revalidate");
        response.setHeader("Cache-Control", "no-cache");
        //PrintWriter out = response.getWriter();

        Configuracion conf = new Configuracion(this._ip, this._puerto, this._db, usuario, clave);
        String id_sucursal_matriz = conf.getValor("id_sucursal_matriz");

        DataBase objDB = new DataBase(this._ip, this._puerto, this._db, usuario, clave);

        String path = String.valueOf(request.getRequestURL());
        path = path.substring(0, path.lastIndexOf("/"));
        Archivo archivo = new Archivo(this._ip, this._puerto, this._db, usuario, clave);
        String logo = path + "/img/" + archivo.getArchivo(this._dir, 1);
        //String logo = "/home/desarrollo/Documentos/saitel/web/img/logo.jpg";

        String id_sucursal = request.getParameter("idSuc");
        Sucursal objSucursal = new Sucursal(this._ip, this._puerto, this._db, usuario, clave);
        String nombre_sucursal = objSucursal.getNombre(id_sucursal);

        String titulo = conf.getValor("razon_social");
        String ruc = conf.getValor("ruc");
        String subtitulo = "";
        String direccion = conf.getValor("dir_matriz");
        String rep_pie = conf.getValor("rep_pie");
        String iva_vigente = conf.getValor("p_iva1");

        /*String sql = "";
        String w = "";*/
        String [] cabTabla = null;
        float [] anchoTabla = null;
        ResultSet registros = null;
        Reporte reporte = null;
        try {
            String WHERE = request.getParameter("WH")!=null ? " "+request.getParameter("WH") : "";
            int op = Integer.parseInt(request.getParameter("op"));
            /*int op2 = Integer.parseInt(request.getParameter("op2"));
            String id_combos = request.getParameter("idS");*/
            String fecha_ini = request.getParameter("fi");
            String fecha_fin = request.getParameter("ff");

            switch(op){
                case 1:
                    String sucursal = id_sucursal.compareTo("-0")!=0 ? "id_sucursal="+id_sucursal+" and" : "";
                    registros = objDB.consulta("select codigo_cuenta, nombre_cuenta, sum(debe) as sum_debe, sum(haber) as sum_haber, " +
                            "case when tipo_cuenta=0 then sum(debe)-sum(haber) else 0.00 end as saldo_deudor, case when tipo_cuenta=1 then sum(haber)-sum(debe) else 0.00 end as saldo_aceedor, tipo_cuenta " +
                            "from vta_libro_diario_mayor where "+sucursal+" fecha_asiento between '"+fecha_ini+"' and '"+fecha_fin+"' " +
                            "group by codigo_cuenta,nombre_cuenta,tipo_cuenta order by codigo_cuenta;");
                    subtitulo = "BALANCE DE COMPROBACIÓN. \n Del "+Addons.fechaAl(fecha_ini)+"  al  "+Addons.fechaAl(fecha_fin)+".";
                    reporte = new Reporte(false, logo, titulo, ruc, subtitulo, direccion, nombre_sucursal, rep_pie);
                    cabTabla = new String [] {"Nro.", "CODIGO", "CUENTA", "DEBE", "HABER", "DEUDOR", "ACREEDOR"};
                    anchoTabla = new float[]{25f,80f,170f,80f,80f,80f,80f};
                    reporte.balanceComprobacion(response, cabTabla, anchoTabla, registros);
                break;

                case 2:
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
                    subtitulo = "BALANCE GENERAL. \n Al "+Addons.fechaAl(fecha_fin)+".";
                    reporte = new Reporte(false, logo, titulo, ruc, subtitulo, direccion, nombre_sucursal, rep_pie);
                    reporte.balanceGeneral(response, registros);
                break;

                case 3:
                    registros = objDB.consulta("select P.id_plan_cuenta, P.id_padre, P.codigo_cuenta, P.nombre_cuenta, case P.tipo_cuenta when 0 then sum(D.debe)-sum(D.haber) when 1 then sum(D.haber)-sum(D.debe) else 0.00 end " +
                            "from tbl_plan_cuenta as P left outer join tbl_libro_diario_mayor as D on P.id_plan_cuenta=D.id_plan_cuenta " +
                            "where (P.tipo_cuenta=-1 or D.fecha_asiento between '"+fecha_ini+"' and '"+fecha_fin+"') and (P.codigo_cuenta like '4%' or P.codigo_cuenta like '5%') or P.id_plan_cuenta=1 " +
                            "group by P.codigo_cuenta,P.nombre_cuenta,P.id_plan_cuenta,P.id_padre,P.tipo_cuenta " +
                            "ORDER BY P.id_padre,P.codigo_cuenta;");
                    subtitulo = "ESTADO DE RESULTADOS. \n Del "+Addons.fechaAl(fecha_ini)+"  al  "+Addons.fechaAl(fecha_fin)+".";
                    reporte = new Reporte(false, logo, titulo, ruc, subtitulo, direccion, nombre_sucursal, rep_pie);
                    reporte.balanceResultados(response, registros);
                break;

                case 5:
                    registros = objDB.consulta("select ruc, razon_social, direccion, ciudad, pais, telefono, email, contacto from vta_proveedor"+WHERE);
                    subtitulo = "Lista de proveedores.";
                    reporte = new Reporte(false, logo, titulo, ruc, subtitulo, direccion, nombre_sucursal, rep_pie);
                    cabTabla = new String [] {"Nro.", "RUC", "RAZON SOCIAL", "DIRECCION", "CIUDAD", "PAIS", "TELEFONO", "EMAIL", "CONTACTO"};
                    anchoTabla = new float[]{25f,80f,120f,120f,60f,60f,60f,100f,120f};
                    reporte.lista(response, cabTabla, anchoTabla, registros);
                break;

                case 6:
                    subtitulo = "Lista de Productos.";
                    if(fecha_ini.compareTo("vta_producto")==0){
                        registros = objDB.consulta("select categoria,codigo,descripcion,unidad_medida,stock,precio_costo from vta_producto "+WHERE+" ORDER BY categoria,codigo;");
                        reporte = new Reporte(false, logo, titulo, ruc, subtitulo, direccion, nombre_sucursal, rep_pie);
                        cabTabla = new String [] {"Nro.", "#", "CODIGO", "DESCRIPCION", "U. MEDIDA", "STOCK", "P. COSTO"};
                        anchoTabla = new float[]{25f,25f,70f,180f,60f,40f,60f};
                    }
                    if(fecha_ini.compareTo("vta_sucursal_producto")==0){
                        registros = objDB.consulta("select categoria,codigo,descripcion,unidad_medida,stock,precio_costo,precio_venta,descuento from vta_sucursal_producto "+WHERE+" ORDER BY categoria,codigo;");
                        reporte = new Reporte(false, logo, titulo, ruc, subtitulo, direccion, nombre_sucursal, rep_pie);
                        cabTabla = new String [] {"Nro.", "#", "CODIGO", "DESCRIPCION", "U. MEDIDA", "STOCK", "P. COSTO", "P. VENTA", "% DESC."};
                        anchoTabla = new float[]{25f,25f,70f,180f,60f,40f,60f,60f,60f};
                    }
                    reporte.listaAgrupada(response, cabTabla, anchoTabla, registros);
                break;

                case 7:
                    registros = objDB.consulta("select ruc, razon_social, direccion, ciudad, pais, telefono, movil_claro, movil_movistar, email from vta_cliente"+WHERE);
                    subtitulo = "Lista de clientes.";
                    reporte = new Reporte(false, logo, titulo, ruc, subtitulo, direccion, nombre_sucursal, rep_pie);
                    cabTabla = new String [] {"Nro.", "RUC", "RAZON SOCIAL", "DIRECCION", "CIUDAD", "PAIS", "TELEFONO", "CLARO", "MOVISTAR", "EMAIL"};
                    anchoTabla = new float[]{25f,75f,120f,120f,60f,60f,65f,65f,65f,120f};
                    reporte.lista(response, cabTabla, anchoTabla, registros);
                break;

                case 8:
                    registros = objDB.consulta("select id_factura_venta,vendedor,ruc,razon_social,numero_factura,fecha_emision,case when anulado then 'ANULADA' else txt_forma_pago end as anulada,ip,subtotal,descuento,subtotal_0,iva_2,total,deuda from vta_factura_venta "+WHERE+" and anulado=false order by vendedor,id_factura_venta");
                    subtitulo = "Reporte de facturas de ventas. \n Del "+Addons.fechaAl(fecha_ini)+"  al  "+Addons.fechaAl(fecha_fin)+".";
                    reporte = new Reporte(false, logo, titulo, ruc, subtitulo, direccion, nombre_sucursal, rep_pie);
                    reporte.ventas(response, registros, objDB, iva_vigente);
                break;

                case 9:
                    registros = objDB.consulta("select *, toDateSQL(fecha_tx) as fecha_tx_sql from tbl_dau"+WHERE);
                    subtitulo = "Importaciones.";
                    reporte = new Reporte(true, logo, titulo, ruc, subtitulo, direccion, "", rep_pie);
                    reporte.importaciones(response, registros, objDB);
                break;

                case 10:
                    registros = objDB.consulta("select departamento,dni,empleado,txt_sexo,txt_estado_civil,direccion,edad,telefono,movil,movil_movistar,ciudad,nacionalidad from vta_empleado"+WHERE+" order by departamento,rol,empleado");
                    subtitulo = "Lista de empleados.";
                    reporte = new Reporte(false, logo, titulo, ruc, subtitulo, direccion, nombre_sucursal, rep_pie);
                    cabTabla = new String [] {"Nro.", "#", "IDENTIF.", "NOMBRES", "GENERO", "ESTADO CIVIL", "DIRECCION", "EDAD", "TELF.", "CLARO", "MOVISTAR", "CIUDAD", "NACIONALIDAD"};
                    anchoTabla = new float[]{20f,20f,55f,100f,55f,65f,100f,20f,45f,45f,45f,50f,70f};
                    reporte.listaAgrupada(response, cabTabla, anchoTabla, registros);
                break;

                /*case 11:
                    registros = objDB.consulta("SELECT *,toDateSQL(periodo) as periodo_sql FROM vta_rol_pago_detalle"+WHERE);
                    subtitulo = "Roles de Pagos: Liquidación Individual";
                    reporte = new Reporte(true, logo, titulo, ruc, subtitulo, direccion, "", rep_pie);
                    reporte.RPLiquidacionIndividual(response, registros, objDB, objSucursal);
                break;

                case 12:
                    registros = objDB.consulta("select departamento,empleado,rol,toDateSQL(periodo),ciudad,total_ingreso,total_egreso,total_pagar from vta_rol_pago_detalle"+WHERE+" order by departamento,rol,empleado");
                    subtitulo = "Roles de Pagos: Liquidación General.";
                    reporte = new Reporte(false, logo, titulo, ruc, subtitulo, direccion, nombre_sucursal, rep_pie);
                    cabTabla = new String [] {"Nro.", "#", "EMPLEADO.", "CARGO", "PERIODO", "CIUDAD", "INGRESOS", "EGRESOS", "LIQUIDO", "FIRMA"};
                    anchoTabla = new float[]{20f,20f,150f,50f,45f,60f,45f,45f,45f,60f};
                    reporte.RPLiquidacionResumen(response, cabTabla, anchoTabla, registros, 6);
                break;

                case 13:
                    registros = objDB.consulta("select departamento,rol,empleado,toDateSQL(periodo),ciudad,total_ingreso,vacaciones,trece_sueldo,catorce_sueldo,total_sociales,sbu,personal_iess,patronal_iess,iece,secap,fondo_reserva,total_patronales from vta_rol_pago_detalle"+WHERE+" order by departamento,rol,empleado");
                    subtitulo = "Provisiones Sociales.";
                    reporte = new Reporte(false, logo, titulo, ruc, subtitulo, direccion, nombre_sucursal, rep_pie);
                    cabTabla = new String [] {"Nro.", "#", "CARGO", "EMPLEADO", "PERIODO", "CIUDAD", "ING.", "VACACACIOES", "13ER SUELDO", "14TO SUELDO", "TOTAL", "SBU", "AP. PERS. IESS", "AP. PAT. IESS", "0.5% IECE", "0.5% SECAP", "FONDO RESERVA", "TOTAL"};
                    anchoTabla = new float[]{15f,15f,55f,110f,55f,50f,40f,30f,30f,30f,40f,40f,35f,35f,35f,35f,40f,40f};
                    reporte.tabular(response, cabTabla, anchoTabla, registros, 6);
                break;*/

                case 14:
                    registros = objDB.consulta("select sucursal,categoria,codigo_activo,descripcion,ubicacion,marca, serie, modelo, num_partes, color,toDateSQL(fecha_compra),valor_compra from vta_activo"+WHERE+" order by sucursal,categoria,codigo_activo");
                    subtitulo = "Activos Fijos.";
                    reporte = new Reporte(false, logo, titulo, ruc, subtitulo, direccion, nombre_sucursal, rep_pie);
                    cabTabla = new String [] {"Nro.", "#", "CATEGORIA", "CODIGO", "DESCRIPCION", "UBICACION", "MARCA", "SERIE", "MODELO", "Nro. PARTES", "COLOR", "FECHA COMPRA", "VALOR COMPRA"};
                    anchoTabla = new float[]{15f,15f,55f,55f,110f,50f,50f,50f,50f,30f,50f,45f,40f};
                    reporte.tabular(response, cabTabla, anchoTabla, registros, 12);
                break;

                case 15:
                    registros = objDB.consulta("select sucursal,categoria,codigo_activo,descripcion,ubicacion,toDateSQL(fecha_compra),valor_compra,valor_depreciado,dep_anual,dep_mensual from vta_activo"+WHERE+" order by sucursal,categoria,codigo_activo");
                    subtitulo = "Depreciaciones de Activos Fijos.";
                    reporte = new Reporte(false, logo, titulo, ruc, subtitulo, direccion, nombre_sucursal, rep_pie);
                    cabTabla = new String [] {"Nro.", "#", "CATEGORIA", "CODIGO", "DESCRIPCION", "UBICACION", "FECHA COMPRA", "VALOR COMPRA", "VALOR DEPRECIADO", "DEP. ANUAL", "DEP. MENSUAL"};
                    anchoTabla = new float[]{15f,15f,55f,55f,110f,50f,50f,50f,50f,50f,50f};
                    reporte.tabular(response, cabTabla, anchoTabla, registros, 7);
                break;

                case 16:
                    registros = objDB.consulta("select 'CODIGO: ' || A.codigo_activo || '        DESCRIPCION: ' || A.descripcion, P.id_sucursal || '-' || P.num_documento, dni_entrega,persona_entrega, dni_recibe, persona_recibe, fecha, txt_tipo_movimiento "
                            + "from (tbl_activo as A inner join tbl_activo_custodio as C on A.id_activo=C.id_activo) inner join vta_activo_personalizacion as P on C.id_activo_personalizacion=P.id_activo_personalizacion "
                            + WHERE+" and A.eliminado=false and P.anulado=false order by P.id_sucursal,A.descripcion, P.fecha;");
                    subtitulo = "Movimientos de activos y custodios.";
                    reporte = new Reporte(false, logo, titulo, ruc, subtitulo, direccion, nombre_sucursal, rep_pie);
                    cabTabla = new String [] {"Nro.", "#", "NRO. DOC.", "CED. ENTREGA", "ENTREGA", "CED. RECIBE", "RECIBE", "FECHA", "MOVIMIENTO"};
                    anchoTabla = new float[]{20f,15f,40f,50f,110f,50f,110f,40f,60f};
                    reporte.listaAgrupada(response, cabTabla, anchoTabla, registros);
                break;

                case 17:
                    registros = objDB.consulta("select P.plan, V.burst_limit, T.costo_plan, '1 - ' || P.div_max_limit as comparticion, P.id_nivel_soporte "
                            + "from (tbl_plan_isp as P inner join tbl_plan_servicio as V on P.id_plan_isp=V.id_plan_isp) "
                            + "inner join tbl_tarifa as T on T.id_plan_servicio=V.id_plan_servicio "
                            + "where P.eliminado=false and V.eliminado=false "
                            + "order by P.div_max_limit desc, P.plan, V.burst_limit::int");
                    subtitulo = "Planes de Servicio.";
                    reporte = new Reporte(true, logo, titulo, ruc, subtitulo, direccion, nombre_sucursal, rep_pie);
                    cabTabla = new String [] {"Nro.", "PLAN", "VELOCIDAD", "COSTO", "COMPARTICION", "NIVEL SOPORTE"};
                    anchoTabla = new float[]{25f,150f,80f,89f,90f,90f};
                    reporte.lista(response, cabTabla, anchoTabla, registros);
                break;

                case 18:
                    registros = objDB.consulta("select sector,costo_instalacion from vta_sector"+WHERE+" order by sector,costo_instalacion");
                    subtitulo = "Sectores.";
                    reporte = new Reporte(true, logo, titulo, ruc, subtitulo, direccion, nombre_sucursal, rep_pie);
                    cabTabla = new String [] {"Nro.", "SECTOR", "COSTO INSTALACION"};
                    anchoTabla = new float[]{10f,70f,30f};
                    reporte.lista(response, cabTabla, anchoTabla, registros);
                break;

                case 19:
                    registros = objDB.consulta("select C.ruc, C.razon_social, toDateSQL(T.fecha_termino), C.direccion, C.ciudad, C.telefono, C.email, C.contacto from vta_cliente as C inner join tbl_contrato as T on C.id_cliente=T.id_cliente "
                            + "where T.anulado=false and fecha_termino between now()::date and (now() + '15 day'::interval)::date order by T.fecha_termino, C.razon_social;");
                    subtitulo = "Lista de clientes por terminar vigencia de contratos de servicio de Internet.";
                    reporte = new Reporte(false, logo, titulo, ruc, subtitulo, direccion, nombre_sucursal, rep_pie);
                    cabTabla = new String [] {"Nro.", "RUC", "CLIENTE", "TERMINO DE CONTRATO", "DIRECCION", "CIUDAD", "TELEFONO", "EMAIL", "CONTACTO"};
                    anchoTabla = new float[]{25f,80f,120f,65f,120f,60f,60f,100f,120f};
                    reporte.lista(response, cabTabla, anchoTabla, registros);
                break;

                case 20:
                    registros = objDB.consulta("select numero_instalacion, ruc, razon_social, direccion_instalacion, ip, plan, txt_comparticion, receptor, antena_acoplada, txt_estado_servicio from vta_instalacion "+WHERE+" order by numero_instalacion, estado_servicio, razon_social;");
                    subtitulo = "Listado de instalaciones de servicio de Internet.";
                    reporte = new Reporte(false, logo, titulo, ruc, subtitulo, direccion, nombre_sucursal, rep_pie);
                    cabTabla = new String [] {"#", "# INS.", "RUC", "CLIENTE", "DIR. INSTALACION", "IP", "PLAN", "COMP.", "RECEPTOR", "ANT. ACOPLADA", "ESTADO"};
                    anchoTabla = new float[]{25f,50f,70f,120f,130f,80f,100f,40f,80f,50F,50f};
                    reporte.lista(response, cabTabla, anchoTabla, registros);
                break;

                case 21:
                    registros = objDB.consulta("select numero_instalacion, ruc, razon_social, ip, plan, txt_comparticion, sector, direccion_instalacion, toDateSQL(fecha_instalacion) from vta_instalacion where anulado=false and ingreso_datos is null order by numero_instalacion,fecha_instalacion,razon_social;");
                    subtitulo = "Listado de instalaciones pendientes.";
                    reporte = new Reporte(false, logo, titulo, ruc, subtitulo, direccion, nombre_sucursal, rep_pie);
                    cabTabla = new String [] {"Nro.", "# INS.", "RUC", "CLIENTE", "IP", "PLAN", "COMP.", "SECTOR", "DIRECCION", "F. INST."};
                    anchoTabla = new float[]{25f,40f,70f,120f,80f,110f,40f,80f,120f,50f};
                    reporte.lista(response, cabTabla, anchoTabla, registros);
                break;

                case 22:
                    registros = objDB.consulta("select numero_instalacion, ruc, razon_social, ip, sector, direccion_instalacion, porcentaje_senal, txt_conformidad_velocidad, "
                            + "txt_conformidad_instalacion, txt_conformidad_atencion, txt_estado_instalacion from vta_instalacion where anulado=false order by numero_instalacion,sector,razon_social;");
                    subtitulo = "Conformidad del servicio de instalación.";
                    reporte = new Reporte(false, logo, titulo, ruc, subtitulo, direccion, nombre_sucursal, rep_pie);
                    cabTabla = new String [] {"Nro.", "# INS.", "RUC", "CLIENTE", "IP", "SECTOR", "DIRECCION", "% SEÑAL", "CONF. VEL.", "CONF. INST.", "CONF. ATE.", "ESTADO"};
                    anchoTabla = new float[]{25f,40f,80f,120f,85f,95f,120f,40f,40f,70f,70f,60f};
                    reporte.lista(response, cabTabla, anchoTabla, registros);
                break;

                case 23:
                    registros = objDB.consulta("select sector, id_sucursal || '-' || num_desinstalacion, ruc, razon_social, direccion_instalacion, telefono, ip, fecha_desinstalacion "
                            + "from vta_instalacion where anulado=false and estado_servicio='r' order by sector,razon_social;");
                    subtitulo = "Listado de clientes por retirar equipos instalados.";
                    reporte = new Reporte(false, logo, titulo, ruc, subtitulo, direccion, nombre_sucursal, rep_pie);
                    cabTabla = new String [] {"Nro.", "SECTOR", "# DESINS.", "RUC", "CLIENTE", "DIRECCION", "TELEFONO", "IP", "F. DESINT."};
                    anchoTabla = new float[]{25f,60f,40f,60f,110f,110f,50f,60f,40f};
                    reporte.lista(response, cabTabla, anchoTabla, registros);
                break;

                case 24:
                    registros = objDB.consulta("select sector, numero_instalacion, ruc, razon_social, ip, plan, txt_estado_servicio, burst_limit, tx_max_limit, svr_burst_limit, svr_burst_threshold, "
                            + "svr_burst_time, svr_direction, svr_disabled, svr_dst_address, svr_interface, svr_limit_at, svr_max_limit, svr_parent, svr_priority, svr_queue, svr_total_queue "
                            + "from vta_instalacion where anulado=false and (lower(burst_limit)<>lower(svr_burst_limit) or burst_limit is null or svr_burst_limit is null) order by sector,razon_social");
                    subtitulo = "Verificación de planes de servicio de Internet.";
                    reporte = new Reporte(false, logo, titulo, ruc, subtitulo, direccion, nombre_sucursal, rep_pie);
                    cabTabla = new String [] {"Nro.", "#", "NRO. INS.", "RUC", "CLIENTE", "IP", "PLAN", "ESTADO SERVICIO", "BURST LIMIT PLAN", "MAX LIMIT PLAN",
                    "BURST LIMIT", "BURST THRESOLD", "BURST TIME", "DIRECTION", "DISABLED", "DST ADDRES", "INTERFACE", "LIMIT AT", "MAX LIMIT", "PARENT",
                    "PRIORITY", "QUEUE", "TOTAL QUEUE"};
                    anchoTabla = new float[]{20f,15f,30f,60f,110f,60f,60f,50f,40f,40f,40f,40f,40f,40f,40f,40f,40f,40f,40f,40f,40f,40f,40f};
                    reporte.listaAgrupada(response, cabTabla, anchoTabla, registros);
                break;

                case 25:
                    registros = objDB.consulta("select sector, email from vta_instalacion where anulado=false and email::varchar<>'' order by sector;");
                    subtitulo = "Cadena de correos electrónicos de clientes.";
                    reporte = new Reporte(true, logo, titulo, ruc, subtitulo, direccion, nombre_sucursal, rep_pie);
                    reporte.correos(response, registros);
                break;

                case 26:
                    registros = objDB.consulta("select vendedor, numero_factura, ruc, razon_social, txt_forma_pago, subtotal, iva_2, total from vta_factura_venta_all "
                            + "where anulado=false and id_sucursal="+id_sucursal_sesion+" and fecha_emision between '"+fecha_ini+"' and '"+fecha_fin+"' and id_factura_venta in "
                            + "(select distinct id_factura_venta from tbl_prefactura where fecha_emision between '"+fecha_ini+"' and '"+fecha_fin+"') order by vendedor, forma_pago;");
                    subtitulo = "Reacudo desde "+fecha_ini+" hasta "+fecha_fin+" agrupado por recaudador(a).";
                    reporte = new Reporte(true, logo, titulo, ruc, subtitulo, direccion, nombre_sucursal, rep_pie);
                    cabTabla = new String [] {"Nro.", "#", "Nro. FACT.", "RUC", "CLIENTE", "F. PAGO", "SUBT.", "IVA "+iva_vigente+"%", "TOTAL"};
                    anchoTabla = new float[]{25f,20f,60f,70f,120f,60f,50f,40f,60f};
                    reporte.tabular(response, cabTabla, anchoTabla, registros, 6);
                break;

                case 27:
                    //objDB.consulta("select proc_calcularPreFacturasPendientes();");
                    String ini = request.getParameter("bfi");
                    String fin = request.getParameter("bff");
                    registros = objDB.consulta("select txt_periodo, sector, ruc, razon_social, direccion_instalacion, telefono, ip, plan, txt_estado_servicio, total from vta_prefactura "
                            + "where id_sucursal="+id_sucursal+" and fecha_emision is null and estado_servicio in ('a', 's', 'c', 'r', 'e', 'n') and periodo between '"+ini+"' and '"+fin+"' order by periodo,sector,razon_social;");
                    subtitulo = "Valores por recaudar desde la fecha "+ini+" hasta "+fin+".";
                    reporte = new Reporte(false, logo, titulo, ruc, subtitulo, direccion, nombre_sucursal, rep_pie);
                    cabTabla = new String [] {"Nro.", "#", "SECTOR", "RUC", "CLIENTE", "DIRECCION", "TELEFONO", "IP", "PLAN", "ESTADO", "TOTAL"};
                    anchoTabla = new float[]{25f,20f,60f,65f,120f,120f,50f,65f,60f,45f,40f};
                    reporte.tabular(response, cabTabla, anchoTabla, registros, 10);
                break;

                case 28:
                    registros = objDB.consulta("select sector, txt_periodo, ruc, razon_social, ip, plan, dias_conexion, valor_internet from vta_prefactura "
                            + "where id_sucursal="+id_sucursal_sesion+" and getFechaSuspension(fecha_prefactura) <= now()::date and fecha_emision is null order by sector,periodo,razon_social;");
                    subtitulo = "Listado de clientes a suspender el servicio por mora, agrupado por sectores.";
                    reporte = new Reporte(true, logo, titulo, ruc, subtitulo, direccion, nombre_sucursal, rep_pie);
                    cabTabla = new String [] {"Nro.", "#", "PERIODO", "RUC", "CLIENTE", "IP", "PLAN", "# DIAS", "VALOR INTERNET"};
                    anchoTabla = new float[]{25f,20f,70f,70f,120f,60f,70,40f,60f};
                    reporte.listaAgrupada(response, cabTabla, anchoTabla, registros);
                break;

                case 29:
                    registros = objDB.consulta("select txt_estado, sector, id_sucursal || '-' || num_orden, ruc, razon_social, direccion_instalacion, telefono, ip, todatesql(fecha_reporte), txt_tipo_trabajo, responsable "
                            + "from vta_orden_trabajo"+WHERE+" order by estado,sector,razon_social;");
                    subtitulo = "Listado de ordenes de trabajo desde la fecha "+fecha_ini+" hasta "+fecha_fin+".";
                    reporte = new Reporte(false, logo, titulo, ruc, subtitulo, direccion, nombre_sucursal, rep_pie);
                    cabTabla = new String [] {"Nro.", "#", "SECTOR", "# ORDEN", "RUC", "CLIENTE", "DIRECCION", "TELEFONO", "IP", "F. REP.", "TIPO TRABAJO", "RESPONSABLE"};
                    anchoTabla = new float[]{20f,20f,60f,40f,60f,110f,110f,50f,50f,50f,100f,100f};
                    reporte.listaAgrupada(response, cabTabla, anchoTabla, registros);
                break;

                case 30:
                    registros = objDB.consulta("select 'CEDULA: ' || ruc || "
                            + "'\nCLIENTE: ' || razon_social || "
                            + "'\nSECTOR: ' || sector  as txt_cliente, "
                            + "id_sucursal || '-' || num_soporte, txt_fecha_llamada, alias_contesta, problema, diagnostico, txt_fecha_solucion, alias_solucion, recomendacion "
                            + "from vta_soporte"+WHERE+";");
                    subtitulo = "Listado de soportes del servicio de Internet Individual desde la fecha "+fecha_ini+" hasta "+fecha_fin+".";
                    reporte = new Reporte(false, logo, titulo, ruc, subtitulo, direccion, nombre_sucursal, rep_pie);
                    cabTabla = new String [] {"Nro.", "#", "# SOP.", "FECHA LLAMADA", "CONTESTA", "PROBLEMA", "DIAGNOSTICO", "FECHA SOLUCION", "SOLUCIONA", "RECOMENDACIONES"};
                    anchoTabla = new float[]{15f,15f,30f,50f,50f,110f,110f,50f,50f,110f};
                    reporte.listaAgrupada(response, cabTabla, anchoTabla, registros);
                break;

                case 31:
                    registros = objDB.consulta("select receptor, mac, ip from tbl_instalacion where (anulado=true or estado_servicio in ('t','e')) and ip not in "
                            + "(select ip from tbl_instalacion where estado_servicio not in ('t','e') and anulado=false) and id_sucursal="+id_sucursal_sesion+" order by ip");
                    subtitulo = "Listado de direcciones IPs a reutilizar.";
                    reporte = new Reporte(true, logo, titulo, ruc, subtitulo, direccion, nombre_sucursal, rep_pie);
                    cabTabla = new String [] {"Nro.", "RECEPTOR", "MAC", "IP"};
                    anchoTabla = new float[]{25f,90f,110f,70f};
                    reporte.lista(response, cabTabla, anchoTabla, registros);
                break;

                case 32:
                    registros = objDB.consulta("SELECT razon_social, numero_factura, toDateSQL(fecha_compra), cxp, deuda, observacion FROM vta_factura_compra where deuda>0 and anulado=false order by razon_social, fecha_compra");
                    subtitulo = "Facturas pendientes de pagar.";
                    reporte = new Reporte(true, logo, titulo, ruc, subtitulo, direccion, nombre_sucursal, rep_pie);
                    cabTabla = new String [] {"Nro.", "RAZON SOCIAL", "NRO. FACT.", "FECHA COMPRA", "CxP", "SALDO", "CONCEPTO"};
                    anchoTabla = new float[]{20f,120f,70f,50f,40f,40f,160f};
                    reporte.lista(response, cabTabla, anchoTabla, registros);
                break;

                case 33:
                    registros = objDB.consulta("SELECT vendedor, razon_social, numero_factura, toDateSQL(fecha_emision), observacion, cxc, deuda FROM vta_factura_venta where deuda>0 and anulado=false order by vendedor, razon_social, fecha_emision");
                    subtitulo = "Facturas pendientes de cobrar.";
                    reporte = new Reporte(true, logo, titulo, ruc, subtitulo, direccion, nombre_sucursal, rep_pie);
                    cabTabla = new String [] {"Nro.", "#", "CLIENTE", "NRO. FACT.", "FECHA EMISION", "CONCEPTO", "CxC", "SALDO"};
                    anchoTabla = new float[]{20f,15f,120f,70f,50f,160f,40f,40f};
                    reporte.tabular(response, cabTabla, anchoTabla, registros, 6);
                break;

                case 34:
                    registros = objDB.consulta("select sector, numero_instalacion, ruc, razon_social, ip, plan, plan_burst_limit "
                            + "from vta_instalacion where anulado=false and estado_servicio not in ('t','e') order by sector,razon_social;");
                    subtitulo = "Planes de servicio de Internet.";
                    reporte = new Reporte(false, logo, titulo, ruc, subtitulo, direccion, nombre_sucursal, rep_pie);
                    cabTabla = new String [] {"Nro.", "#", "NRO. INS.", "RUC", "CLIENTE", "IP", "PLAN", "BURST LIMIT"};
                    anchoTabla = new float[]{20f,20f,30f,60f,110f,60f,60f,40f};
                    reporte.listaAgrupada(response, cabTabla, anchoTabla, registros);
                break;

                case 35:
                    String estado = request.getParameter("est");
                    String idSector = request.getParameter("idSec");
                    String idAntena = request.getParameter("idAnt");
                    String idPlanServicio = request.getParameter("idPlSe");
                    String w = (estado.compareTo("-0")!=0 && estado.compareTo("")!=0) ? " and estado='"+estado+"'" : "";
                    w += (idSector.compareTo("-0")!=0 && idSector.compareTo("")!=0) ? " and id_sector="+idSector : "";
                    w += (idAntena.compareTo("-0")!=0 && idAntena.compareTo("")!=0) ? " and antena_acoplada='"+idAntena+"'" : "";
                    w += (idPlanServicio.compareTo("-0")!=0 && idPlanServicio.compareTo("")!=0) ? " and id_plan_actual="+idPlanServicio : "";
                    registros = objDB.consulta("select txt_estado, ruc, razon_social, ip, id_sucursal || '-' || num_soporte, txt_fecha_llamada, alias_contesta, problema, txt_fecha_solucion, alias_solucion, recomendacion "
                            + "from vta_soporte where fecha_llamada between '"+fecha_ini+"' and '"+fecha_fin+"' and id_sucursal="+id_sucursal+" "+w+" order by txt_estado, fecha_llamada, hora_llamada");
                    subtitulo = "Soportes del servicio de Internet General desde la fecha "+fecha_ini+" hasta "+fecha_fin+".";
                    reporte = new Reporte(false, logo, titulo, ruc, subtitulo, direccion, nombre_sucursal, rep_pie);
                    cabTabla = new String [] {"Nro.", "#", "RUC", "CLIENTE", "IP", "# SOP.", "F. LLAMADA", "CONTESTA", "PROBLEMA", "F. SOLUCION", "SOLUCIONA", "RECOMENDACIONES"};
                    anchoTabla = new float[]{15f,15f,60f,110f,70f,40f,50f,50f,110f,50f,50f,110f};
                    reporte.listaAgrupada(response, cabTabla, anchoTabla, registros);
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
                    reporte = new Reporte(false, logo, titulo, ruc, subtitulo, direccion, nombre_sucursal, rep_pie);
                    cabTabla = new String [] {"Nro.", "RUC", "CLIENTE", "SECTOR", "IP", "PLAN", "PERIODO", "ESTADO", "# DIAS", "VALOR INTERNET", "SUBT.", "IVA "+iva_vigente+"%", "TOTAL"};
                    anchoTabla = new float[]{25f,70f,120f,40f,60f,60f,60f,40f,40f,50f,50f,40f,60f};
                    reporte.sumLista(response, cabTabla, anchoTabla, registros);
                break;

                case 37:
                    String periodo = request.getParameter("pd");
                    int an = Fecha.datePart("anio", periodo);
                    int ms = Fecha.datePart("mes", periodo);
                    registros = objDB.consulta("select ruc, razon_social, sector, ip, plan, fecha_ini, fecha_fin from vta_anticipo_internet "
                            + "where id_sucursal="+id_sucursal_sesion+" and '"+periodo+"' between fecha_ini and fecha_fin order by fecha_ini");
                    subtitulo = "Servicios de Internet con pago anticipado para el período " + Fecha.getTxtPeriodo(an, ms) + ".";
                    reporte = new Reporte(true, logo, titulo, ruc, subtitulo, direccion, nombre_sucursal, rep_pie);
                    cabTabla = new String [] {"Nro.", "RUC", "CLIENTE", "SECTOR", "IP", "PLAN", "FECHA INICIO", "FECHA FIN"};
                    anchoTabla = new float[]{25f,55f,90f,60f,60f,60f,45f,45f};
                    reporte.lista(response, cabTabla, anchoTabla, registros);
                break;

                case 38:
                    registros = objDB.consulta("select distinct I.ruc, I.razon_social, I.sector, I.ip, I.plan, getAnioMes(P.periodo), toDateSQL(fecha_emision), I.txt_estado_servicio, P.dias_conexion, P.valor_internet "
                            + "from tbl_prefactura as P inner join vta_instalacion as I on I.id_instalacion=P.id_instalacion "
                            + "where I.id_sucursal="+id_sucursal_sesion+" and P.periodo = (select max(periodo) from tbl_prefactura) and P.fecha_emision is not null order by I.razon_social");
                    subtitulo = "Recaudación del último período.";
                    reporte = new Reporte(false, logo, titulo, ruc, subtitulo, direccion, nombre_sucursal, rep_pie);
                    cabTabla = new String [] {"Nro.", "RUC", "CLIENTE", "SECTOR", "IP", "PLAN", "PERIODO", "EMISION", "ESTADO", "# DIAS", "VALOR INTERNET"};
                    anchoTabla = new float[]{25f,55f,120f,40f,60f,60f,60f,40f,45f,30f,50f};
                    reporte.lista(response, cabTabla, anchoTabla, registros);
                break;

                case 39:
                    String suc = id_sucursal.compareTo("-0")!=0 ? "A.id_sucursal="+id_sucursal+" and " : "";
                    if(fecha_ini.compareTo("s")==0){
                        registros = objDB.consulta("select categoria, codigo_activo, descripcion, id_activo from vta_activo "
                                + "where "+(id_sucursal.compareTo("-0")!=0 ? "id_sucursal="+id_sucursal+" and " : "")+
                                " codigo_activo not in (select distinct mac from tbl_instalacion where anulado=false and estado_servicio not in ('t','e') and mac<>'') order by categoria");
                        subtitulo = "Activos que se encuentran en SAITEL";
                        reporte = new Reporte(true, logo, titulo, ruc, subtitulo, direccion, nombre_sucursal, rep_pie);
                        cabTabla = new String [] {"Nro.", "#", "CODIGO", "DESCRIPCION", "CUSTORIO", "FECHA"};
                        anchoTabla = new float[]{25f,20f,100f,130f,130f,50f};
                        reporte.activosCustorios(response, cabTabla, anchoTabla, registros, objDB);
                    }else{
                        registros = objDB.consulta("select A.categoria, A.codigo_activo, A.descripcion, I.razon_social, toDateSQL(I.fecha_instalacion) from vta_activo as A inner join vta_instalacion as I on A.codigo_activo=I.mac "
                                + "where "+(id_sucursal.compareTo("-0")!=0 ? "A.id_sucursal="+id_sucursal+" and " : "")+" I.anulado=false and I.estado_servicio not in ('t','e') order by A.categoria");
                        subtitulo = "Activos que se encuentran en el cliente";
                        reporte = new Reporte(true, logo, titulo, ruc, subtitulo, direccion, nombre_sucursal, rep_pie);
                        cabTabla = new String [] {"Nro.", "#", "CODIGO", "DESCRIPCION", "CUSTORIO", "FECHA"};
                        anchoTabla = new float[]{25f,20f,100f,130f,130f,50f};
                        reporte.listaAgrupada(response, cabTabla, anchoTabla, registros);
                    }
                break;

                case 40:
                    registros = objDB.consulta("SELECT razon_social,numero_factura,txt_num_cheque,toDateSQL(fecha_pago),abono,txt_tipo_pago FROM vta_factura_compra_pago where fecha_pago between '"+fecha_ini+"' and '"+fecha_fin+"' order by txt_tipo_pago, fecha_pago, razon_social");
                    subtitulo = "Pagos realizados desde la fecha "+fecha_ini+" hasta "+fecha_fin+".";
                    reporte = new Reporte(true, logo, titulo, ruc, subtitulo, direccion, nombre_sucursal, rep_pie);
                    cabTabla = new String [] {"Nro.", "RAZON SOCIAL", "No. FACT.", "No. CHEQUE", "F. PAGO", "VALOR", "F.PAGO"};
                    anchoTabla = new float[]{20f,120f,70f,50f,40f,40f,60f};
                    reporte.lista(response, cabTabla, anchoTabla, registros);
                break;

                case 41:
                    String fecha = request.getParameter("idCmb");
                    subtitulo = "Cierre de caja del "+Addons.fechaAl(fecha)+".";
                    reporte = new Reporte(true, logo, titulo, ruc, subtitulo, direccion, nombre_sucursal, rep_pie);
                    reporte.cierreCaja(response, id_sucursal_sesion, fecha, objDB);
                break;

                case 42:
                    String banco = conf.getValor("bancos");
                    String caja = conf.getValor("caja");
                    String fecha1 = request.getParameter("idCmb");
                    subtitulo = "Ingresos y egresos del "+Addons.fechaAl(fecha1)+".";
                    reporte = new Reporte(true, logo, titulo, ruc, subtitulo, direccion, nombre_sucursal, rep_pie);
                    reporte.ingresosEgresos(response, id_sucursal_sesion, fecha1, objDB, banco, caja);
                break;

                case 43:
                    String tt1 = "el cliente";
                    String sql = "select A.categoria, count(A.codigo_activo) from vta_activo as A inner join vta_instalacion as I on A.codigo_activo=I.mac "
                            + "where "+(id_sucursal.compareTo("-0")!=0 ? "A.id_sucursal="+id_sucursal+" and " : "")+
                            " I.anulado=false and I.estado_servicio not in ('t','e') group by A.categoria order by A.categoria";
                    if(fecha_fin.compareTo("s")==0){
                        tt1 = "SAITEL";
                        sql = "select categoria, count(codigo_activo) from vta_activo "
                                + "where "+(id_sucursal.compareTo("-0")!=0 ? "id_sucursal="+id_sucursal+" and " : "")+
                                " codigo_activo not in (select distinct mac from tbl_instalacion where anulado=false and estado_servicio not in ('t','e') and mac<>'') group by categoria order by categoria";
                    }

                    registros = objDB.consulta(sql);
                    subtitulo = "Activos que se encuentran en "+tt1 +" consolidado";
                    reporte = new Reporte(true, logo, titulo, ruc, subtitulo, direccion, nombre_sucursal, rep_pie);
                    cabTabla = new String [] {"Nro.", "CATEGORIA", "Nro. ACTIVOS"};
                    anchoTabla = new float[]{15f,85f,15f};
                    reporte.lista(response, cabTabla, anchoTabla, registros);
                break;

                case 44:
                    anio = request.getParameter("an");
                    mes = request.getParameter("ms");
                    fecha_ini = anio + "-" + mes + "-01";
                    fecha_fin = anio + "-" + mes + "-" + Fecha.getUltimoDiaMes(Integer.parseInt(anio), Integer.parseInt(mes));
                    registros = objDB.consulta("SELECT distinct id_sucursal || '-' ||  num_instalacion, ruc, razon_social, toDateSQL(fecha_instalacion), ip, txt_estado_servicio FROM vta_instalacion where id_sucursal="+id_sucursal_sesion+" and id_instalacion not in "
                    + "(select id_instalacion from tbl_prefactura where periodo between '"+fecha_ini+"' and '"+fecha_fin+"') and anulado=false and estado_servicio in ('a','s','c') order by razon_social;");

                    subtitulo = "Lista de instalaciones sin prefactura para el período " + Fecha.getTxtPeriodo(Integer.parseInt(anio), Integer.parseInt(mes));
                    reporte = new Reporte(true, logo, titulo, ruc, subtitulo, direccion, nombre_sucursal, rep_pie);
                    cabTabla = new String [] {"Nro.", "# INSTALACION", "RUC", "RAZON SOCIAL", "F. INSTALACION", "IP", "ESTADO"};
                    anchoTabla = new float[]{15f,40f,50f,120f,35f,50f,40f};
                    reporte.lista(response, cabTabla, anchoTabla, registros);
                break;

                case 45:
                    String dni = request.getParameter("an");
                    Empleado objEmpleado = new Empleado(this._ip, this._puerto, this._db, usuario, clave);
                    registros = objDB.consulta("select A.categoria, A.codigo_activo, A.descripcion, toDateSQL(C.fecha) from (vta_activo as A inner join tbl_activo_custodio as AC on A.id_activo=AC.id_activo) "+
                          "inner join tbl_activo_personalizacion as C on C.id_activo_personalizacion=AC.id_activo_personalizacion where AC.actual=true and AC.eliminado=false and C.dni_recibe='"+dni+"' order by A.categoria, A.codigo_activo");
                    subtitulo = "Activos que se encuentran custodiados por " + objEmpleado.getNombre(dni);
                    reporte = new Reporte(true, logo, titulo, ruc, subtitulo, direccion, nombre_sucursal, rep_pie);
                    cabTabla = new String [] {"Nro.", "#", "CODIGO", "DESCRIPCION", "FECHA RECEPCION"};
                    anchoTabla = new float[]{25f,20f,100f,130f,50f};
                    reporte.listaAgrupada(response, cabTabla, anchoTabla, registros);
                    objEmpleado.cerrar();
                break;

                case 46:
                    registros = objDB.consulta("select C.persona_recibe, A.categoria, count(A.categoria) from (vta_activo as A inner join tbl_activo_custodio as AC on A.id_activo=AC.id_activo) "+
                          "inner join tbl_activo_personalizacion as C on C.id_activo_personalizacion=AC.id_activo_personalizacion where AC.actual=true and AC.eliminado=false and "
                          + "C.dni_recibe in (SELECT dni FROM tbl_empleado) group by C.persona_recibe,A.categoria order by C.persona_recibe, A.categoria");
                    subtitulo = "Activos en custodios consolidado";
                    reporte = new Reporte(true, logo, titulo, ruc, subtitulo, direccion, nombre_sucursal, rep_pie);
                    cabTabla = new String [] {"Nro.", "#", "CATEGORIA", "Nro. ACTIVOS"};
                    anchoTabla = new float[]{25f,20f,100f,30f};
                    reporte.listaAgrupada(response, cabTabla, anchoTabla, registros);
                break;

                case 47:
                    //objDB.consulta("select proc_calcularPreFacturasPendientes();");

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
                    reporte = new Reporte(true, logo, titulo, ruc, subtitulo, direccion, "EMPRESA", rep_pie);
                    reporte.ingresosPeriodos(response, emisiones, recaudos, xcobrar, instalaciones, otros);
                break;

                case 48:
                    registros = objDB.consulta("select numero_instalacion, ruc, razon_social, ip, plan, txt_comparticion, sector, direccion_instalacion, toDateSQL(fecha_terminado) from vta_instalacion where anulado=false and fecha_terminado > '"+fecha_ini+"' order by numero_instalacion,fecha_terminado,razon_social");
                    subtitulo = "Listado de instalaciones termiadas desde la fecha "+fecha_ini+".";
                    reporte = new Reporte(false, logo, titulo, ruc, subtitulo, direccion, nombre_sucursal, rep_pie);
                    cabTabla = new String [] {"Nro.", "# INS.", "RUC", "CLIENTE", "IP", "PLAN", "COMP.", "SECTOR", "DIRECCION", "F. DESINST."};
                    anchoTabla = new float[]{25f,40f,70f,120f,80f,110f,40f,80f,120f,50f};
                    reporte.lista(response, cabTabla, anchoTabla, registros);
                break;

                case 49:
                    registros = objDB.consulta("select numero_instalacion, ruc, razon_social, ip, plan, txt_comparticion, sector, direccion_instalacion, motivo_no_cobrar "
                            + "from vta_instalacion where anulado=false and estado_servicio not in ('t','e') and cobrar=false order by numero_instalacion,razon_social");
                    subtitulo = "Direcciones IPs no pre-facturbles.";
                    reporte = new Reporte(false, logo, titulo, ruc, subtitulo, direccion, nombre_sucursal, rep_pie);
                    cabTabla = new String [] {"Nro.", "# INS.", "RUC", "CLIENTE", "IP", "PLAN", "COMP.", "SECTOR", "DIRECCION", "MOTIVO"};
                    anchoTabla = new float[]{25f,40f,70f,120f,80f,110f,40f,80f,120f, 120f};
                    reporte.lista(response, cabTabla, anchoTabla, registros);
                break;

                case 50:
                    registros = objDB.consulta("select ruc, razon_social, ip, plan, fecha_registro, txt_estado_servicio from vta_instalacion "
                            + "where estado_servicio not in ('e','t') and anulado=false and fecha_instalacion is null "
                            + "and id_sucursal="+id_sucursal+" order by numero_instalacion, estado_servicio, razon_social");
                    subtitulo = "Post-instalaciones pendientes al "+fecha_fin+".";
                    reporte = new Reporte(false, logo, titulo, ruc, subtitulo, direccion, nombre_sucursal, rep_pie);
                    cabTabla = new String [] {"Nro.", "RUC", "CLIENTE", "IP", "PLAN", "FECHA REGISTRO", "ESTADO"};
                    anchoTabla = new float[]{25f,70f,120f,80f,110f,80f,120f};
                    reporte.lista(response, cabTabla, anchoTabla, registros);
                break;
                
                case 51:
                    String fech_ini = request.getParameter("pd");
                    String fech_fin = request.getParameter("bff");
                    registros = objDB.consulta("select C.ruc, C.razon_social, serie_factura || '-' || num_factura, serie_nota || '-' || num_nota as num_nota, N.fecha_emision, cxp "
                            + "from (tbl_cliente as C inner join tbl_factura_venta as F on C.id_cliente=F.id_cliente) "
                            + "inner join tbl_nota_credito_venta as N on N.id_factura_venta=F.id_factura_venta "
                            + "where N.fecha_emision between '"+fech_ini+"' and '"+fech_fin+"' and N.anulado=false order by num_nota, C.razon_social");
                    subtitulo = "Notas de crédito desde la fecha "+fech_ini+" hasta "+fech_fin+".";
                    reporte = new Reporte(false, logo, titulo, ruc, subtitulo, direccion, nombre_sucursal, rep_pie);
                    cabTabla = new String [] {"Nro.", "RUC", "CLIENTE", "# FACTURA", "# NOTA CREDITO", "F. EMISION", "VALOR"};
                    anchoTabla = new float[]{25f,70f,120f,80f,80f,50f,40f};
                    reporte.lista(response, cabTabla, anchoTabla, registros);
                break;
                    
                case 52:
                    registros = objDB.consulta("select ruc, razon_social, cliente_debito, num_cuenta, S.sucursal "
                            + "from vta_cliente as C inner join tbl_sucursal as S on S.id_sucursal=C.id_sucursal "
                            + "where num_cuenta<>'' and num_cuenta is not null and id_cliente in "
                            + "(select id_cliente from tbl_instalacion where estado_servicio in ('a', 'c', 'r') and fecha_instalacion is not null ) "
                            + "order by S.sucursal, razon_social");
                    subtitulo = "Clientes con convenio de débito.";
                    reporte = new Reporte(false, logo, titulo, ruc, subtitulo, direccion, nombre_sucursal, rep_pie);
                    cabTabla = new String [] {"Nro.", "RUC", "RAZON SOCIAL", "CLIENTE DEBITO", "NUMERO CUENTA", "SUCURSAL"};
                    anchoTabla = new float[]{25f,50f,120f,120f,40f,60f};
                    reporte.lista(response, cabTabla, anchoTabla, registros);
                break;
            
                case 53:
                    registros = objDB.consulta("select ruc, razon_social, carne_conadis, edad "
                            + "from vta_cliente where (carne_conadis<>'' and carne_conadis is not null) or (edad >= 65) order by razon_social");
                    subtitulo = "Clientes con capacidades especiales y tercera edad.";
                    reporte = new Reporte(false, logo, titulo, ruc, subtitulo, direccion, nombre_sucursal, rep_pie);
                    cabTabla = new String [] {"Nro.", "RUC", "RAZON SOCIAL", "CARNE CONADIS", "EDAD"};
                    anchoTabla = new float[]{25f,60f,120f,50f,50f};
                    reporte.lista(response, cabTabla, anchoTabla, registros);
                break;
                    
                    
                case 54:
                    registros = objDB.consulta("select ruc, razon_social, direccion_instalacion, txt_estado_servicio, plan, T.costo_plan "
                            + "from vta_instalacion as I inner join tbl_tarifa as T on I.id_plan_actual=T.id_plan_servicio "
                            + "where estado_servicio in ('a','s','c','r') "
                            + "order by txt_estado_servicio, razon_social;");
                    subtitulo = "Listado de instalaciones por estado del servicio.";
                    reporte = new Reporte(false, logo, titulo, ruc, subtitulo, direccion, nombre_sucursal, rep_pie);
                    cabTabla = new String [] {"Nro.", "RUC", "RAZON SOCIAL", "DIRECCION", "ESTADO", "PLAN DEL SERVICIO", "COSTO DEL PLAN"};
                    anchoTabla = new float[]{25f,40f,100f,120f, 40f, 50f, 30f};
                    reporte.lista(response, cabTabla, anchoTabla, registros);
                break; 
                    
                case 55:
                    registros = objDB.consulta("select A.codigo_activo, max(P.num_documento) as num_personalizacion " +
                        "from ((tbl_activo as A inner join tbl_activo_custodio as C on A.id_activo=C.id_activo) " +
                        "inner join tbl_activo_personalizacion as P on P.id_activo_personalizacion=C.id_activo_personalizacion) " +
                        "where A.codigo_activo in (select distinct mac from tbl_instalacion where anulado=false) " +
                        "and P.dni_recibe in (select ruc from tbl_cliente) " +
                        "group by A.codigo_activo " +
                        "order by A.codigo_activo");
                    subtitulo = "Equipos con últimas personalizaciones en clientes.";
                    reporte = new Reporte(true, logo, titulo, ruc, subtitulo, direccion, nombre_sucursal, rep_pie);
                    cabTabla = new String [] {"RAZON SOCIAL", "EQUIPO"};
                    anchoTabla = new float[]{100f, 30f};
                    reporte.activosPersonalizadosClientes(response, cabTabla, anchoTabla, registros, objDB);
                break; 
                    
                case 56:
                    periodo = request.getParameter("pd1");
                    an = Fecha.datePart("anio", periodo);
                    ms = Fecha.datePart("mes", periodo);
                    registros = objDB.consulta("select ruc, razon_social, sector, ip, plan, fecha_solicitud, fecha_inicio, case when tipo='d' then 'Definitiva' else (tiempo+1)::varchar end, usuario_solicitud "
                            + "from vta_instalacion as I inner join tbl_instalacion_suspension as S on I.id_instalacion=S.id_instalacion "
                            + "where I.id_sucursal="+id_sucursal_sesion+" and eliminado=false and '"+periodo+"' between S.fecha_inicio and S.fecha_termino order by S.fecha_inicio");
                    subtitulo = "Servicios de Internet suspendido para el período " + Fecha.getTxtPeriodo(an, ms) + ".";
                    reporte = new Reporte(false, logo, titulo, ruc, subtitulo, direccion, nombre_sucursal, rep_pie);
                    cabTabla = new String [] {"Nro.", "RUC", "CLIENTE", "SECTOR", "IP", "PLAN", "FECHA DE SOLICITUD", "FECHA INICIO", "TIEMPO", "USUARIO"};
                    anchoTabla = new float[]{20f,50f,90f,60f,55f,60f,45f,45f,45f,45f};
                    reporte.lista(response, cabTabla, anchoTabla, registros);
                break;
                        
            }

        } finally {
            archivo.cerrar();
            conf.cerrar();
            objSucursal.cerrar();
            try{
                registros.close();
            }catch(SQLException e){
                e.printStackTrace();
            }
            objDB.cerrar();
            //out.close();
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
