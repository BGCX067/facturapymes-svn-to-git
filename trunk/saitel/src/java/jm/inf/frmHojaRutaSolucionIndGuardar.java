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
import jm.adm.clas.Documento;
import jm.fac.clas.Cliente;
import jm.fac.clas.FacturaVenta;
import jm.fac.clas.Instalacion;
import jm.inf.clas.OrdenTrabajo;
import jm.inf.clas.Radius;
import jm.seg.clas.Auditoria;
import jm.web.DataBase;
import jm.web.Fecha;

/**
 *
 * @author Jorge
 */
public class frmHojaRutaSolucionIndGuardar extends HttpServlet {
    private String _ip = null;
    private int _puerto = 5432;
    private String _db = null;
    private String _usuario = null;
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
        int id_sucursal = (Integer)sesion.getAttribute("sucursal");
        this._usuario = (String)sesion.getAttribute("usuario");
        this._clave = (String)sesion.getAttribute("clave");

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

        String observacion = "";
        Documento objDocumento = new Documento(this._ip, this._puerto, this._db, this._usuario, this._clave);
        try{
            ResultSet rsDoc = objDocumento.getDocumento("p");
            if(rsDoc.next()){
                observacion = rsDoc.getString("documento")!=null ? rsDoc.getString("documento") : "";
                rsDoc.close();
            }
        }catch(Exception e){
            e.printStackTrace();
        }finally{
            objDocumento.cerrar();
        }

        Activo objActivo = new Activo(this._ip, this._puerto, this._db, this._usuario, this._clave);
        long num_documento = objActivo.getNunDocumento();
        //String num_documento = sec_num_documento);

        Bodega objBodega = new Bodega(this._ip, this._puerto, this._db, this._usuario, this._clave);

        Auditoria auditoria = new Auditoria(this._ip, this._puerto, this._db, this._usuario, this._clave);

        Instalacion objInstalacion = new Instalacion(this._ip, this._puerto, this._db, this._usuario, this._clave);

        /*Configuracion conf = new Configuracion(this._ip, this._puerto, this._db, this._usuario, this._clave);
        String ruc_empresa = conf.getValor("ruc");
        String empresa = conf.getValor("razon_social");
        conf.cerrar();*/

        String axInstall = "";

        Cliente objCliente = new Cliente(this._ip, this._puerto, this._db, this._usuario, this._clave);

        OrdenTrabajo objOrdenTrabajo = new OrdenTrabajo(this._ip, this._puerto, this._db, this._usuario, this._clave);

        String r = "msg»Ha ocurrido un error inesperado, por favor, vuelva a intentarlo más tarde o " +
                    "contáctese con el administrador del sistema para mayor información.";
        try {
            String WHERE = request.getParameter("WHERE");
            String p = request.getParameter("p") != null ? request.getParameter("p") : "0";
            String pos = request.getParameter("pos"); // posicion de la orden de trabjao
            String id_tecnico_resp = request.getParameter("id_empleado");
            String bodega_movil[] = objBodega.getBodegaResponsable(id_tecnico_resp);

            
            String numero_orden = request.getParameter("numero_orden");
            String id_orden_trabajo = request.getParameter("id_ot");
            String id_instalacion = request.getParameter("id_ints");
            String tipo_trabajo = request.getParameter("tip_tra");
            //String id_cliente = request.getParameter("id_cli");
            String ruc = request.getParameter("ruc");
            String razon_social = request.getParameter("raso");
            String solucionado = request.getParameter("solucionado");
            String conformidad = request.getParameter("conformidad");
            String atencion = request.getParameter("atencion");
            String recomendacion = request.getParameter("recomendacion");
            String id_sector = request.getParameter("id_sector");
            String ip = request.getParameter("ip");
            String id_plan_actual = request.getParameter("id_plan_actual");
            String direccion = request.getParameter("direccion");
            String mac_ant = request.getParameter("mac_ant0").toUpperCase();
            String receptor_ant = request.getParameter("receptor_ant0");
            String mac_nuevo = request.getParameter("mac_act0").toUpperCase();
            String receptor_nuevo = request.getParameter("receptor_act0");
            String set_deviceclave = request.getParameter("set_deviceclave");
            
            String porcentaje_senal = request.getParameter("senal");
            String antena_acoplada = request.getParameter("antena_acoplada");
            String lat_h = request.getParameter("lat_h");
            String lat_m = request.getParameter("lat_m");
            String lat_s = request.getParameter("lat_s");
            String lat_o = request.getParameter("lat_o");
            String lon_h = request.getParameter("lon_h");
            String lon_m = request.getParameter("lon_m");
            String lon_s = request.getParameter("lon_s");
            String lon_o = request.getParameter("lon_o");
            String altura = request.getParameter("altura");
            String altura_antena = request.getParameter("altura_antena");

            String latitud = lat_h + ":" + lat_m + ":" + lat_s + ":"  + lat_o;
            String longitud = lon_h + ":" + lon_m + ":" + lon_s + ":"  + lon_o;

            boolean ok = false;
            int j = 0;
            String materiales = "";
            String cantidades = "";
            while(request.getParameter("id_material"+j)!=null){
                if(request.getParameter("cantidad"+j).compareTo("")!=0){
                    materiales += request.getParameter("id_material"+j) + ",";
                    cantidades += request.getParameter("cantidad"+j) + ",";
                }
                j++;
            }
            if(materiales.compareTo("")!=0){
                materiales = materiales.substring(0, materiales.length()-1);
                cantidades = cantidades.substring(0, cantidades.length()-1);
            }
            
            String macs_nuevas = mac_nuevo.compareTo("")!=0 ? mac_nuevo+"," : "";
            String idsActivos = mac_nuevo.compareTo("")!=0 ? objActivo.getIdActivo(mac_nuevo)+"," : "";
            String macs_retiradas = mac_ant.compareTo("")!=0 ? mac_ant+"," : "";
            String idsActivosRet = mac_ant.compareTo("")!=0 ? objActivo.getIdActivo(mac_ant)+"," : "";
            String codigo_activo = "";
            for(int i=0; i<50; i++){
                if(request.getParameter("codAct0"+i)!=null){
                    codigo_activo = request.getParameter("codAct0"+i);
                    macs_nuevas += codigo_activo + ",";
                    idsActivos += objActivo.getIdActivo(codigo_activo) + ",";
                }
                if(request.getParameter("codActRet0"+i)!=null){
                    codigo_activo = request.getParameter("codActRet0"+i);
                    macs_retiradas += codigo_activo + ",";
                    idsActivosRet += objActivo.getIdActivo(codigo_activo) + ",";
                }
            }
            if(macs_nuevas.compareTo("")!=0){
                macs_nuevas = macs_nuevas.substring(0, macs_nuevas.length()-1);
                idsActivos = idsActivos.substring(0, idsActivos.length()-1);
            }
            if(macs_retiradas.compareTo("")!=0){
                macs_retiradas = macs_retiradas.substring(0, macs_retiradas.length()-1);
                idsActivosRet = idsActivosRet.substring(0, idsActivosRet.length()-1);
            }


            if(tipo_trabajo.compareTo("3")==0){ // instalacion
                String idActivo = objActivo.getIdActivo(mac_nuevo);
                String idBodEnt = objActivo.getIdBodegaActivo(mac_nuevo);
                String bodegaEnt = objBodega.getNombre(idBodEnt);
                String ubicacion_entrega = objActivo.getUltimaUbicacion(idActivo);
                String empleado[] = this.getEmpleado(id_orden_trabajo);
                //String id_personalizacion = "-1";
                String id_personalizacion = objActivo.insertarPersonalizacion(id_sucursal, String.valueOf(num_documento), "6", Fecha.getFecha("ISO"),
                        idBodEnt, "", bodegaEnt, "", ubicacion_entrega, empleado[2], empleado[0], ruc, empleado[1], razon_social, observacion, idsActivos);
                if(id_personalizacion.compareTo("-1") != 0){
                    r = "fun»_R('rotSol"+pos+"');_R('ppp');^msg»Personalizacón de instalación realizada con éxito sin registro de post-instalación.";
                    auditoria.setRegistro(request, "REGISTRO DE DOCUMENTO DE PERSONALIZACION DE ACTIVOS Nro.: "+num_documento);
                    String fecha_instalacion = request.getParameter("fecha_instalacion");
                    String estado_instalacion = request.getParameter("estado_instalacion");
                    
                    if(!objInstalacion.insertarPostInstalacion(id_instalacion, id_sucursal, fecha_instalacion, receptor_nuevo, mac_nuevo, idsActivos, 
                      id_personalizacion, porcentaje_senal, antena_acoplada, id_plan_actual, "true", conformidad, atencion,
                      estado_instalacion, latitud, longitud, altura, altura_antena) ){
                        r += "fun»_R('rotSol"+pos+"');_R('ppp');^msg»Registro de post-instalación y personalizacón de activos realizada con éxito.";
                        axInstall += numero_orden + ", ";
                    }else{
                        ok=true;
                    }
                    
                }

                
                

            }else if(tipo_trabajo.compareTo("4")==0){ //desinstalacion

                
                
                
                     String radusername = "";
                     try{
                        ResultSet rsFactura = objInstalacion.getInstalacion(id_instalacion);
                        if(rsFactura.next()){
                            radusername = (rsFactura.getString("radusername")!=null) ? rsFactura.getString("radusername") : "";
                            rsFactura.close();
                        }
                     }catch(Exception e){
                        e.printStackTrace();
                     }
                     /*Radius dbRadius = new Radius(this._ip, this._puerto, this._db, this._usuario, this._clave);
                     dbRadius.eliminar(radusername);
                     dbRadius.cerrar();*/
                     
                     
                     boolean sin_deuda = false;
                     FacturaVenta objFactura = new FacturaVenta(this._ip, this._puerto, this._db, this._usuario, this._clave);
                     if(!objFactura.getPreFacturasPendientes(id_instalacion)){   //  si no tiene deuda
                         sin_deuda = true;
                     }
                            
                     if(idsActivosRet.compareTo("")!=0){
                         
                         if(objActivo.existe(mac_ant)){
                            String estado = "e";
                            if(sin_deuda){ 
                                estado = "t";
                            }  
                            if(objInstalacion.desInstalar(id_instalacion, recomendacion, estado, idsActivosRet)){
                                r = "fun»_R('rotSol"+pos+"');_R('ppp');^msg»Des-instalación realizada con éxito.";
                                String idActivo = objActivo.getIdActivo(mac_ant);
                                String idBodEnt = objActivo.getIdBodegaActivo(mac_ant);
                                String bodegaEnt = objBodega.getNombre(idBodEnt);
                                String ubicacion_entrega = objActivo.getUltimaUbicacion(idActivo);
                                String id_personalizacion = objActivo.insertarPersonalizacion(id_sucursal, String.valueOf(num_documento), "7", Fecha.getFecha("ISO"),
                                       idBodEnt, bodega_movil[0], bodegaEnt, bodega_movil[1], ubicacion_entrega, bodega_movil[2], ruc, bodega_movil[3], razon_social, bodega_movil[4], observacion, idsActivosRet);
                                if(id_personalizacion.compareTo("-1") != 0){
                                    ok = true;
                                    r = "fun»_R('rotSol"+pos+"');_R('ppp');^msg»Des-instalación y personalización de activos realizada con éxito.";
                                    auditoria.setRegistro(request, "REGISTRO DE DOCUMENTO DE PERSONALIZACION DE ACTIVOS Nro.: "+num_documento);
                                    num_documento++;
                                }
                            }
                             
                        }else{
                             r = "msg»Mac del equipo no se encuentra registrada en el sistema. Proceda a registrarlo antes de solucionar la orden de trabajo";
                        }
                         
                     }else{
                         if(sin_deuda){
                            objInstalacion.desInstalar(id_instalacion, recomendacion, "d", "");
                         }else{
                            objInstalacion.desInstalar(id_instalacion, recomendacion, "r", "");
                         }
                         ok = true;
                     }
                     
                     

                     
                     
            }else{    //    revision general y demas
                
                
                objInstalacion.actualizar(id_instalacion, id_orden_trabajo, id_sector, direccion, ip, mac_ant, idsActivosRet, receptor_ant, mac_nuevo, idsActivos, receptor_nuevo, porcentaje_senal, antena_acoplada, latitud, longitud, altura, altura_antena);
                r = "fun»_R('rotSol"+pos+"');_R('ppp');^msg»Actualización de datos del registro de instalación realizada con éxito.";
                boolean ok_ret = false;
                boolean ok_int = false;
                ok = true;
                if(idsActivosRet.compareTo("")!=0){  // retiro del equipo del cliente
                     String idActivo = objActivo.getIdActivo(mac_ant);
                     String idBodEnt = objActivo.getIdBodegaActivo(mac_ant);
                     String bodegaEnt = objBodega.getNombre(idBodEnt);
                     String ubicacion_entrega = objActivo.getUltimaUbicacion(idActivo);
                     
                     String id_personalizacion = objActivo.insertarPersonalizacion(id_sucursal, String.valueOf(num_documento), "7", Fecha.getFecha("ISO"),
                            idBodEnt, bodega_movil[0], bodegaEnt, bodega_movil[1], ubicacion_entrega, bodega_movil[2], ruc, bodega_movil[3], razon_social, bodega_movil[4], observacion, idsActivosRet);
                     if(id_personalizacion.compareTo("-1") != 0){
                         ok_ret = true;
                         r = "fun»_R('rotSol"+pos+"');_R('ppp');^msg»Actualización de datos del registro de instalación y personalización de retiro del equipo del cliente realizada con éxito.";
                         auditoria.setRegistro(request, "REGISTRO DE DOCUMENTO DE PERSONALIZACION DE ACTIVOS Nro.: "+num_documento);
                         num_documento++;
                     }
                 }
                
                 if(idsActivos.compareTo("")!=0){    // cambio - entrega del equipo al cliente
                     String idActivo = objActivo.getIdActivo(mac_nuevo);
                     String idBodEnt = objActivo.getIdBodegaActivo(mac_nuevo);
                     String bodegaEnt = objBodega.getNombre(idBodEnt);
                     String ubicacion_entrega = objActivo.getUltimaUbicacion(idActivo);
                     String empleado[] = this.getEmpleado(id_orden_trabajo);
                     String id_personalizacion = objActivo.insertarPersonalizacion(id_sucursal, String.valueOf(num_documento), "6", Fecha.getFecha("ISO"),
                            idBodEnt, "", bodegaEnt, "", ubicacion_entrega, empleado[2], empleado[0], ruc, empleado[1], razon_social, observacion, idsActivos);
                     if(id_personalizacion.compareTo("-1") != 0){
                         ok_int = true;
                         r = "fun»_R('rotSol"+pos+"');_R('ppp');^msg»Actualización de datos del registro de instalación y personalización del equipo al cliente realizada con éxito.";
                         auditoria.setRegistro(request, "REGISTRO DE DOCUMENTO DE PERSONALIZACION DE ACTIVOS Nro.: "+num_documento);
                         num_documento++;
                     }
                 }
                 
                if(ok_ret && ok_int){
                    r = "fun»_R('rotSol"+pos+"');_R('ppp');^msg»Actualización de datos del registro de instalación y personalización del equipo al cliente realizada con éxito.";
                }
            }

            if(ok){
                if(!objOrdenTrabajo.solucionar(id_sucursal, id_orden_trabajo, this._usuario, Fecha.getFecha("SQL"), Fecha.getHora(), solucionado, 
                        conformidad, atencion, recomendacion, materiales, cantidades, set_deviceclave)){
                    r = "msg»"+objOrdenTrabajo.getError();
                    //r = "msg»Ha ocurrido un error en el registro de la solución de la orden de trabajo. Por favor contáctese con el administrador del sistema para mayor información";
                }else{
                    r = "fun»_R('rotSol"+pos+"');_R('ppp');^msg»Orden de trabajo registrada satisfactoriamente.";
                }
            }else{
                r = "msg»"+objActivo.getError();
            }

            out.print(r);
        } finally {
            auditoria.cerrar();
            objInstalacion.cerrar();
            objOrdenTrabajo.cerrar();
            objActivo.cerrar();
            objBodega.cerrar();
            objCliente.cerrar();
            out.close();
        }
    }

    public String[] getEmpleado(String id_orden_trabajo)
    {
        String res[] = new String[]{"","",""};
        DataBase objDataBase = new DataBase(this._ip, this._puerto, this._db, this._usuario, this._clave);
        try{
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
        }catch(Exception e){
            e.printStackTrace();
        }finally{
            objDataBase.cerrar();
        }
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