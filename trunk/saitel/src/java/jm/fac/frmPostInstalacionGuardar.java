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
import java.sql.ResultSet;

import javax.servlet.*;
import javax.servlet.http.*;
import jm.adm.clas.Configuracion;
import jm.fac.clas.Instalacion;
import jm.fac.clas.PlanServicio;
import jm.inf.clas.Radius;
import jm.seg.clas.Auditoria;

/**
 *
 * @author Jorge
 */
public class frmPostInstalacionGuardar extends HttpServlet {
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

        response.setContentType("text/html;charset=UTF-8");
        response.setHeader("Pragma", "no-cache");
        response.setHeader("Expires", "Mon, 01 Jan 2001 00:00:01 GMT");
        response.setHeader("Cache-Control", "no-store");
        response.setHeader("Cache-Control", "must-revalidate");
        response.setHeader("Cache-Control", "no-cache");
        PrintWriter out = response.getWriter();

        Auditoria auditoria = new Auditoria(this._ip, this._puerto, this._db, usuario, clave);
        Configuracion conf = new Configuracion(this._ip, this._puerto, this._db, usuario, clave);
        Instalacion objInstalacion = new Instalacion(this._ip, this._puerto, this._db, usuario, clave);

        try {
            String r = "msg»Ha ocurrido un error inesperado, por favor, vuelva a intentarlo más tarde o " +
                "contáctese con el administrador del sistema para mayor información.";

            String id = request.getParameter("id");
            String id_cliente = request.getParameter("id_cliente");

            String imprimir = request.getParameter("imp");
            String id_comprobante_diario = request.getParameter("id_comprobante_diario");
            String ingreso_datos = request.getParameter("ingreso_datos");
            String fecha_instalacion = request.getParameter("fecha_instalacion");
            String receptor = request.getParameter("receptor");
            String mac = request.getParameter("mac").toUpperCase();
            String macp = request.getParameter("macp").toUpperCase();
            String macg = request.getParameter("macg").toUpperCase();

            String id_personalizacion = request.getParameter("id_personalizacion");
            String porcentaje_senal = request.getParameter("porcentaje_senal");
            String antena_acoplada = request.getParameter("antena_acoplada");
            String id_plan_establecido = request.getParameter("id_plan_establecido");
            String conformidad_velocidad = request.getParameter("conformidad_velocidad");
            String conformidad_instalacion = request.getParameter("conformidad_instalacion");
            String conformidad_atencion = request.getParameter("conformidad_atencion");
            String estado_instalacion = request.getParameter("estado_instalacion");
            String num_instalacion = request.getParameter("num_instalacion");
            String ruc = request.getParameter("ruc");
            String id_sucursal = request.getParameter("id_sucursal");
            String altura = request.getParameter("altura");
            String altura_antena = request.getParameter("altura_antena");

            String lat_h = request.getParameter("lat_h");
            String lat_m = request.getParameter("lat_m");
            String lat_s = request.getParameter("lat_s");
            String lat_o = request.getParameter("lat_o");
            String lon_h = request.getParameter("lon_h");
            String lon_m = request.getParameter("lon_m");
            String lon_s = request.getParameter("lon_s");
            String lon_o = request.getParameter("lon_o");
            String latitud = lat_h + ":" + lat_m + ":" + lat_s + ":"  + lat_o;
            String longitud = lon_h + ":" + lon_m + ":" + lon_s + ":"  + lon_o;

            String radusername = "";
            try{
                ResultSet rsInstalacion = objInstalacion.getInstalacion(id);
                if(rsInstalacion.next()){
                    radusername = (rsInstalacion.getString("radusername")!=null) ? rsInstalacion.getString("radusername") : "";
                    rsInstalacion.close();
                }
            }catch(Exception e){
                e.printStackTrace();
            }
                
                if(ingreso_datos.compareTo("")==0){
                   /* if(id_activo.compareTo("-1")!=0){
                        String num_documento = String.valueOf(objActivo.getNunDocumento());
                        id_personalizacion = objActivo.insertarPersonalizacion(id_sucursal_sesion, num_documento, "e", fecha_instalacion,
                                ruc_empresa, dni_recibe, empresa, persona_recibe, observacion, id_activo);
                        if(id_personalizacion.compareTo("-1") != 0){
                            auditoria.setRegistro(request, "REGISTRO DE DOCUMENTO DE PERSONALIZACION DE ACTIVOS Nro.: "+num_documento);
                        }
                    }else{
                        msg = "No se ha podido registrar la personalización del activo ya que el código del activo no se encuentra registrado dentro del módulo de activos fijos.";
                    }*/
                    boolean ok = objInstalacion.insertarPostInstalacion(id, id_sucursal_sesion, fecha_instalacion, receptor, mac, "", id_personalizacion,
                            porcentaje_senal, antena_acoplada, id_plan_establecido, conformidad_velocidad, conformidad_instalacion, conformidad_atencion,
                            estado_instalacion, latitud, longitud, altura, altura_antena);
                    if(ok){
                        PlanServicio objPlanServicio = new PlanServicio(this._ip, this._puerto, this._db, usuario, clave);
                        String value = objPlanServicio.getSufPlan(id_plan_establecido);
                        objPlanServicio.cerrar();
                        /*Radius dbRadius = new Radius(this._ip, this._puerto, this._db, usuario, clave);
                        dbRadius.setPlan(radusername, value);
                        dbRadius.cerrar();*/

                        auditoria.setRegistro(request, "REGISTRO DE INFORMACION DE POST-INSTALACION Nro.: "+id_sucursal+"-"+num_instalacion);
                        r = "err»0^vta»cmp^msg»Registro de post-instalación realizado satisfactoriamente.";
                    }
                }else{
                    /*if(id_activo.compareTo("-1")!=0){
                        if(id_personalizacion.compareTo("-1") == 0){
                            String num_documento = String.valueOf(objActivo.getNunDocumento());
                            id_personalizacion = objActivo.insertarPersonalizacion(id_sucursal_sesion, num_documento, "e", fecha_instalacion,
                                    ruc_empresa, dni_recibe, empresa, persona_recibe, observacion, id_activo);
                            if(id_personalizacion.compareTo("-1") != 0){
                                auditoria.setRegistro(request, "REGISTRO DE DOCUMENTO DE PERSONALIZACION DE ACTIVOS Nro.: "+num_documento);
                            }
                        }else{
                            String num_documento = String.valueOf(objActivo.getNunDocumento());
                            boolean ok = objActivo.actualizarPersonalizacion(id_personalizacion, id_sucursal_sesion, num_documento, "e", fecha_instalacion,
                                    ruc_empresa, dni_recibe, empresa, persona_recibe, observacion, id_activo);
                            if(ok){
                                auditoria.setRegistro(request, "ACTUALIZACION DE DOCUMENTO DE PERSONALIZACION DE ACTIVOS Nro.: "+num_documento);
                            }
                        }
                    }else{
                        msg = "No se ha podido registrar la personalización del activo ya que el código del activo no se encuentra registrado dentro del módulo de activos fijos.";
                    }*/
                    if(objInstalacion.actualizarPostInstalacion(id, fecha_instalacion, receptor, mac, id_personalizacion, porcentaje_senal, antena_acoplada,
                       id_plan_establecido, conformidad_velocidad, conformidad_instalacion, conformidad_atencion, estado_instalacion, latitud, longitud, altura, altura_antena)){
                        auditoria.setRegistro(request, "ACTUALIZACION DEL INFORMACION DE POST-INSTALACION Nro.: "+id_sucursal+"-"+num_instalacion);
                        r = (imprimir.compareTo("1")==0?"fun»imprimir('pdfComprobanteDiario?id="+id_comprobante_diario+"');":"")+
                            "^msg»Información guardada satisfactoriamente.";
                    }

                    
                }

                /*objActivo.cerrar();
            }else{
                r = "msg»Stock insuficiente. El stock del producto de código: " + prod_sin_stock + " ha disminuido por una venta realizada desde otra caja ubicada en la sucursal.";
            }*/
            


            out.print(r);

        } finally {
            objInstalacion.cerrar();
            auditoria.cerrar();
            conf.cerrar();
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
