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

package jm.inf;

import java.io.*;
import java.sql.ResultSet;

import javax.servlet.*;
import javax.servlet.http.*;
import jm.act.clas.Activo;
import jm.adm.clas.Bodega;
import jm.adm.clas.Configuracion;
import jm.adm.clas.Documento;
import jm.fac.clas.Cliente;
import jm.inf.clas.HojaRuta;
import jm.fac.clas.Instalacion;
import jm.inf.clas.OrdenTrabajo;
import jm.seg.clas.Auditoria;
import jm.web.DataBase;
import jm.web.Fecha;

/**
 *
 * @author Jorge
 */
public class frmHojaRutaSolucionGuardar extends HttpServlet {
    private String _ip = null;
    private int _puerto = 5432;
    private String _db = null;
    
    private String _clave = null;

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
        //int id_sucursal = (Integer)sesion.getAttribute("sucursal");
        String usuario = (String)sesion.getAttribute("usuario");
        String clave = (String)sesion.getAttribute("clave");

        response.setContentType("text/html;charset=UTF-8");
        response.setHeader("Pragma", "no-cache");
        response.setHeader("Expires", "Mon, 01 Jan 2001 00:00:01 GMT");
        response.setHeader("Cache-Control", "no-store");
        response.setHeader("Cache-Control", "must-revalidate");
        response.setHeader("Cache-Control", "no-cache");
        PrintWriter out = response.getWriter();

        
//      PARA LA PERSONALIZACIONE DE ACTIVOS
        /*Configuracion conf = new Configuracion(this._ip, this._puerto, this._db, usuario, clave);
        String ruc_empresa = conf.getValor("ruc");
        String empresa = conf.getValor("razon_social");
        conf.cerrar();*/
/*
        String observacion = "";
        try{
            Documento objDocumento = new Documento(this._ip, this._puerto, this._db, usuario, clave);
            ResultSet rsDoc = objDocumento.getDocumento("p");
            if(rsDoc.next()){
                observacion = rsDoc.getString("documento")!=null ? rsDoc.getString("documento") : "";
                rsDoc.close();
            }
            objDocumento.cerrar();
        }catch(Exception e){}
        
        Activo objActivo = new Activo(this._ip, this._puerto, this._db, usuario, clave);
        long num_documento = objActivo.getNunDocumento();
        //String num_documento = sec_num_documento);

        Bodega objBodega = new Bodega(this._ip, this._puerto, this._db, usuario, clave);
        
        Auditoria auditoria = new Auditoria(this._ip, this._puerto, this._db, usuario, clave);

        Instalacion objInstalacion = new Instalacion(this._ip, this._puerto, this._db, usuario, clave);

        Configuracion conf = new Configuracion(this._ip, this._puerto, this._db, usuario, clave);
        String ruc_empresa = conf.getValor("ruc");
        String empresa = conf.getValor("razon_social");
        conf.cerrar();
        
        String axInstall = "";

        Cliente objCliente = new Cliente(this._ip, this._puerto, this._db, usuario, clave);

        OrdenTrabajo objOrdenTrabajo = new OrdenTrabajo(this._ip, this._puerto, this._db, usuario, clave);
*/
        String r = "msg»Ha ocurrido un error inesperado, por favor, vuelva a intentarlo más tarde o " +
                    "contáctese con el administrador del sistema para mayor información.";
        try {
         /*   String WHERE = request.getParameter("WHERE");
            String p = request.getParameter("p") != null ? request.getParameter("p") : "0";
            String id = request.getParameter("id"); // id de la hoja de ruta
            String num_hoja_ruta = request.getParameter("num_hoja_ruta");
            String id_tecnico_resp = request.getParameter("id_tecnico_resp1");
            String bodega_movil[] = objBodega.getBodegaResponsable(id_tecnico_resp);
            
            
            String id_orden_trabajo = "";
            String numero_orden = "";
            String id_instalacion = "";
            String tipo_trabajo = "";
            //String id_cliente = "";
            String ruc = "";
            String razon_social = "";
            String ids_ot_pendientes = "";
            String solucionado = "";
            String conformidad = "";
            String atencion = "";
            String recomendacion = "";
            String id_sector = "";
            String ip = "";
            String id_plan_actual = "";
            String direccion = "";
            String mac_ant = "";
            String mac_antp = "";
            String mac_antg = "";
            String receptor_ant = "";
            String mac_nuevo = "";
            String mac_nuevop = "";
            String mac_nuevog = "";
            String receptor_nuevo = "";
            String porcentaje_senal = "";
            String antena_acoplada = "";
            String lat_h = "";
            String lat_m = "";
            String lat_s = "";
            String lat_o = "";
            String lon_h = "";
            String lon_m = "";
            String lon_s = "";
            String lon_o = "";
            String altura = "";
            String altura_antena = "";
            String latitud = "";
            String longitud = "";
            String fecha_instalacion = "";
            String estado_instalacion = "";
            String materiales = "";
            String cantidades = "";
            int i=0;
            int j = 0;
            while(request.getParameter("id_ots"+i) != null){
                String ch_ots = request.getParameter("ch_ots"+i);
                if(ch_ots.compareTo("on")==0 || ch_ots.compareTo("true")==0){
                    numero_orden = request.getParameter("numero_orden"+i);
                    id_orden_trabajo = request.getParameter("id_ots"+i);
                    id_instalacion = request.getParameter("id_ints"+i);
                    tipo_trabajo = request.getParameter("tip_tra"+i);
                    //id_cliente = request.getParameter("id_cli"+i);
                    ruc = request.getParameter("ruc"+i);
                    razon_social = request.getParameter("raso"+i);
                    solucionado = request.getParameter("solucionado"+i);
                    conformidad = request.getParameter("conformidad"+i);
                    atencion = request.getParameter("atencion"+i);
                    recomendacion = request.getParameter("recomendacion"+i);
                    id_sector = request.getParameter("id_sector"+i);
                    ip = request.getParameter("ip"+i);
                    id_plan_actual = request.getParameter("id_plan_actual"+i);
                    direccion = request.getParameter("direccion"+i);
                    mac_ant = request.getParameter("mac_ant"+i).toUpperCase();
                    mac_antp = request.getParameter("mac_antp"+i).toUpperCase();
                    mac_antg = request.getParameter("mac_antg"+i).toUpperCase();

                    receptor_ant = request.getParameter("receptor_ant"+i);
                    mac_nuevo = request.getParameter("mac_act"+i).toUpperCase();
                    mac_nuevop = request.getParameter("mac_actp"+i).toUpperCase();
                    mac_nuevog = request.getParameter("mac_actg"+i).toUpperCase();

                    receptor_nuevo = request.getParameter("receptor_act"+i);
                    porcentaje_senal = request.getParameter("senal"+i);
                    antena_acoplada = request.getParameter("antena_acoplada"+i);
                    lat_h = request.getParameter("lat_h"+i);
                    lat_m = request.getParameter("lat_m"+i);
                    lat_s = request.getParameter("lat_s"+i);
                    lat_o = request.getParameter("lat_o"+i);
                    lon_h = request.getParameter("lon_h"+i);
                    lon_m = request.getParameter("lon_m"+i);
                    lon_s = request.getParameter("lon_s"+i);
                    lon_o = request.getParameter("lon_o"+i);
                    altura = request.getParameter("altura"+i);
                    altura_antena += request.getParameter("altura_antena"+i);
                    
                    latitud = lat_h + ":" + lat_m + ":" + lat_s + ":"  + lat_o;
                    longitud = lon_h + ":" + lon_m + ":" + lon_s + ":"  + lon_o;

                    j = 0;
                    materiales = "";
                    cantidades = "";
                    while(request.getParameter("id_material"+i+j)!=null){
                        if(request.getParameter("cantidad"+i+j).compareTo("")!=0){
                            materiales += request.getParameter("id_material"+i+j) + ",";
                            cantidades += request.getParameter("cantidad"+i+j) + ",";
                        }
                        j++;
                    }
                    if(materiales.compareTo("")!=0){
                        materiales = materiales.substring(0, materiales.length()-1);
                        cantidades = cantidades.substring(0, cantidades.length()-1);
                    }


                    if(tipo_trabajo.compareTo("3")==0){ // instalacion

                        String idActivo = objActivo.getIdActivo(mac_nuevo);
                        if(mac_nuevop.compareTo("")!=0){
                            idActivo += "," + objActivo.getIdActivo(mac_nuevop);
                        }
                        if(mac_nuevog.compareTo("")!=0){
                            idActivo += "," + objActivo.getIdActivo(mac_nuevog);
                        }
                        
                        String idBodEnt = objActivo.getIdBodegaActivo(mac_nuevo);
                        String bodegaEnt = objBodega.getNombre(idBodEnt);
                        String ubicacion_entrega = objActivo.getUltimaUbicacion(idActivo);
                        String empleado[] = this.getEmpleado(id_orden_trabajo);
                        //String id_personalizacion = "-1";
                        String id_personalizacion = objActivo.insertarPersonalizacion(id_sucursal, String.valueOf(num_documento), "6", Fecha.getFecha("ISO"),
                                idBodEnt, "", bodegaEnt, "", ubicacion_entrega, empleado[2], empleado[0], ruc, empleado[1], razon_social, observacion, idActivo);
                        if(id_personalizacion.compareTo("-1") != 0){
                            num_documento++;
                            auditoria.setRegistro(request, "REGISTRO DE DOCUMENTO DE PERSONALIZACION DE ACTIVOS Nro.: "+num_documento);
                        }
                        
                        fecha_instalacion = request.getParameter("fecha_instalacion"+i);
                        estado_instalacion = request.getParameter("estado_instalacion"+i);
                        //objInstalacion.actualizar(id_instalacion, id_sector, direccion, ip, id_plan_actual, mac_nuevo, receptor_nuevo, porcentaje_senal, antena_acoplada, latitud, longitud, altura, altura_antena, fecha_instalacion, solucionado, conformidad, atencion, estado_instalacion);
                        if(!objInstalacion.insertarPostInstalacion(id_instalacion, id_sucursal, fecha_instalacion, receptor_nuevo, mac_nuevo, mac_nuevop, mac_nuevog, id_personalizacion,
                            porcentaje_senal, antena_acoplada, id_plan_actual, "true", conformidad, atencion,
                            estado_instalacion, latitud, longitud, altura, altura_antena) ){
                            axInstall += numero_orden + ", ";
                        }

                        
                    }else if(tipo_trabajo.compareTo("4")==0){ //desinstalacion

                             if(mac_ant.compareTo("")!=0){
                                 //objInstalacion.desInstalar(id_instalacion, recomendacion, "t");
                                 if(objInstalacion.desInstalar(id_instalacion, recomendacion, "t")){
                                     String idActivo = objActivo.getIdActivo(mac_ant);
                                     if(mac_antp.compareTo("")!=0){
                                        idActivo += "," + objActivo.getIdActivo(mac_antp);
                                     }
                                     if(mac_antg.compareTo("")!=0){
                                        idActivo += "," + objActivo.getIdActivo(mac_antg);
                                     }
                                     String idBodEnt = objActivo.getIdBodegaActivo(mac_ant);
                                     String bodegaEnt = objBodega.getNombre(idBodEnt);
                                     String ubicacion_entrega = objActivo.getUltimaUbicacion(idActivo);
                                     String id_personalizacion = objActivo.insertarPersonalizacion(id_sucursal, String.valueOf(num_documento), "7", Fecha.getFecha("ISO"),
                                            idBodEnt, bodega_movil[0], bodegaEnt, bodega_movil[1], ubicacion_entrega, bodega_movil[2], ruc, bodega_movil[3], razon_social, bodega_movil[4], observacion, idActivo);
                                     if(id_personalizacion.compareTo("-1") != 0){
                                          num_documento++;
                                          auditoria.setRegistro(request, "REGISTRO DE DOCUMENTO DE PERSONALIZACION DE ACTIVOS Nro.: "+num_documento);
                                     }
                                 }
                             }else{
                                 objInstalacion.desInstalar(id_instalacion, recomendacion, "e");
                             }
                    
                    }else{    //    revision general y demas
                        objInstalacion.actualizar(id_instalacion, id_orden_trabajo, id_sector, direccion, ip, id_plan_actual, mac_ant, mac_antp, mac_antg, receptor_ant, mac_nuevo, mac_nuevop, mac_nuevog, receptor_nuevo, porcentaje_senal, antena_acoplada, latitud, longitud, altura, altura_antena);
                        
                        if(mac_ant.compareTo("")!=0){  // retiro del equipo del cliente
                             String idActivo = objActivo.getIdActivo(mac_ant);
                             if(mac_antp.compareTo("")!=0){
                                idActivo += "," + objActivo.getIdActivo(mac_antp);
                             }
                             if(mac_antg.compareTo("")!=0){
                                idActivo += "," + objActivo.getIdActivo(mac_antg);
                             }

                             String idBodEnt = objActivo.getIdBodegaActivo(mac_ant);
                             String bodegaEnt = objBodega.getNombre(idBodEnt);
                             String ubicacion_entrega = objActivo.getUltimaUbicacion(idActivo);
                             String id_personalizacion = objActivo.insertarPersonalizacion(id_sucursal, String.valueOf(num_documento), "7", Fecha.getFecha("ISO"),
                                    idBodEnt, bodega_movil[0], bodegaEnt, bodega_movil[1], ubicacion_entrega, bodega_movil[2], ruc, bodega_movil[3], razon_social, bodega_movil[4], observacion, idActivo);
                             if(id_personalizacion.compareTo("-1") != 0){
                                  num_documento++;
                                  auditoria.setRegistro(request, "REGISTRO DE DOCUMENTO DE PERSONALIZACION DE ACTIVOS Nro.: "+num_documento);
                             }
                         }
                         if(mac_nuevo.compareTo("")!=0){    // cambio - entrega del equipo al cliente
                             String idActivo = objActivo.getIdActivo(mac_nuevo);
                             if(mac_nuevop.compareTo("")!=0){
                                 idActivo += "," + objActivo.getIdActivo(mac_nuevop);
                             }
                             if(mac_nuevog.compareTo("")!=0){
                                 idActivo += "," + objActivo.getIdActivo(mac_nuevog);
                             }

                             String idBodEnt = objActivo.getIdBodegaActivo(mac_nuevo);
                             String bodegaEnt = objBodega.getNombre(idBodEnt);
                             String ubicacion_entrega = objActivo.getUltimaUbicacion(idActivo);
                             String empleado[] = this.getEmpleado(id_orden_trabajo);
                             String id_personalizacion = objActivo.insertarPersonalizacion(id_sucursal, String.valueOf(num_documento), "6", Fecha.getFecha("ISO"),
                                    idBodEnt, "", bodegaEnt, "", ubicacion_entrega, empleado[2], empleado[0], ruc, empleado[1], razon_social, observacion, idActivo);
                             if(id_personalizacion.compareTo("-1") != 0){
                                  num_documento++;
                                  auditoria.setRegistro(request, "REGISTRO DE DOCUMENTO DE PERSONALIZACION DE ACTIVOS Nro.: "+num_documento);
                             }
                         }

                    }

                    objOrdenTrabajo.solucionar(id_sucursal, id_orden_trabajo, usuario, Fecha.getFecha("SQL"), Fecha.getHora(), solucionado, conformidad, atencion, recomendacion, materiales, cantidades);
                        
                }else{
                    ids_ot_pendientes += request.getParameter("id_ots"+i) + ",";
                }
                i++;
            }

            if(ids_ot_pendientes.compareTo("")!=0){
                ids_ot_pendientes = ids_ot_pendientes.substring(0, ids_ot_pendientes.length()-1);
                objOrdenTrabajo.setEstadoOrdenes(ids_ot_pendientes, "1");
            }

            HojaRuta objHojaRuta = new HojaRuta(this._ip, this._puerto, this._db, usuario, clave);
            if(objHojaRuta.solucionar(id, usuario)){
                auditoria.setRegistro(request, "REGISTRO DE SOLUCION DE LA HOJA DE RUTA NRO. " + num_hoja_ruta);
                String msg = "Hoja de ruta solucionada satisfactoriamente.";
                if(axInstall.compareTo("")!=0){
                    msg = "Las ordenes de trabajo de instalaciones siguientes " + axInstall.substring(0, axInstall.length()-2)+ " no se pudo registrar la post-instalación.";
                }
                r = "err»0^vta»cmp^tbl»"+objOrdenTrabajo.paginar("vta_hoja_ruta", "id_hoja_ruta,toDateSQL(fecha),tecnico,observacion", WHERE, Integer.parseInt(p), 100)+
                    "^msg»"+msg;
            }*/
            out.print(r);
            //objHojaRuta.cerrar();
        } finally {
            /*auditoria.cerrar();
            objInstalacion.cerrar();
            objOrdenTrabajo.cerrar();
            objActivo.cerrar();
            objBodega.cerrar();
            objCliente.cerrar();*/
            out.close();
        }
    }

    public String[] getEmpleado(String id_orden_trabajo)
    {
        String res[] = new String[]{"","",""};
        /*try{
            DataBase objDataBase = new DataBase(this._ip, this._puerto, this._db, usuario, clave);
            ResultSet rs = objDataBase.consulta("select dni, nombre || ' ' || apellido as empleado, I.direccion_instalacion "
                    + "from (tbl_empleado as E inner join tbl_orden_trabajo as OT on E.id_empleado=OT.id_empleado) "
                    + "inner join tbl_instalacion as I on I.id_instalacion=OT.id_instalacion "
                    + "where OT.id_orden_trabajo="+id_orden_trabajo);
            if(rs.next()){
                res[0] = rs.getString("dni")!=null ? rs.getString("dni") : "";
                res[1] = rs.getString("empleado")!=null ? rs.getString("empleado") : "";
                res[2] = rs.getString("direccion_instalacion")!=null ? rs.getString("direccion_instalacion") : "";
                res.clone();
            }
            objDataBase.cerrar();
        }catch(Exception e){
            e.printStackTrace();
        }*/
        return res;
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