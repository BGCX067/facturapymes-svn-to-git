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
import jm.fac.clas.PlanServicio;
import jm.inf.clas.Radius;
import jm.seg.clas.Auditoria;

/**
 *
 * @author Jorge
 */
public class frmVelocidadGuardar extends HttpServlet {
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
        String usuario = (String)sesion.getAttribute("usuario");
        String clave = (String)sesion.getAttribute("clave");
        
        response.setContentType("text/html;charset=UTF-8");
        response.setHeader("Pragma", "no-cache");
        response.setHeader("Expires", "Mon, 01 Jan 2001 00:00:01 GMT");
        response.setHeader("Cache-Control", "no-store");
        response.setHeader("Cache-Control", "must-revalidate");
        response.setHeader("Cache-Control", "no-cache");
        PrintWriter out = response.getWriter();
        
        PlanServicio objPlanServicio = new PlanServicio(this._ip, this._puerto, this._db, usuario, clave);
            
        //Radius dbRadius = new Radius(this._ip, this._puerto, this._db, usuario, clave);
        
        try {
            String r = "msg»Ha ocurrido un error inesperado, por favor, vuelva a intentarlo más tarde o " +
                        "contáctese con el administrador del sistema para mayor información.";
            String id = request.getParameter("id");
            String id_plan_servicio = request.getParameter("idPS");
            String burst_limit = request.getParameter("brLi");
            String login_time = request.getParameter("loTi");
            String i = request.getParameter("i");

            r = "msg»Burst Limit duplicado.";
            if(!objPlanServicio.velocidadDuplicada(id, id_plan_servicio, burst_limit)){
                r = "msg»Ha ocurrido un error inesperado, por favor, vuelva a intentarlo más tarde o " +
                        "contáctese con el administrador del sistema para mayor información.";
                if(id_plan_servicio.compareTo("-1")==0){
                    String pk = objPlanServicio.insertarVelocidad(id, burst_limit, login_time);
                    if(pk.compareTo("-1")!=0){
                        
                        String plan = objPlanServicio.getSufPlan(pk);
                        String cadPlan = objPlanServicio.getRadPlan(pk);
                        //dbRadius.crearPlan(plan, cadPlan, "20000", login_time);
                        
                        Auditoria auditoria = new Auditoria(this._ip, this._puerto, this._db, usuario, clave);
                        auditoria.setRegistro(request, "INGRESO DE BURST LIMIT "+burst_limit+" DENTRO DEL PLAN "+id);
                        auditoria.cerrar();

                        r = "fun»fac_postVelocidadGuardar('"+objPlanServicio.getRadPlanJSON(pk)+"', "+i+");_('idPS"+i+"').value="+pk;
                    }
                }else{
                    String plan = objPlanServicio.getSufPlan(id_plan_servicio);
                    
                    if(objPlanServicio.actualizarVelocidad(id_plan_servicio, burst_limit, login_time)){
                        Auditoria auditoria = new Auditoria(this._ip, this._puerto, this._db, usuario, clave);
                        auditoria.setRegistro(request, "ACTUALIZACION DE BURST LIMIT "+burst_limit+" DENTRO DEL PLAN" + id);
                        auditoria.cerrar();
                        
                        // actiualiza clientes nueva velocidad en el freeradius

                        /*String msg = "^msg»Ha ocurrido un error al registrar el cambio de velocidad de usuarios en el servidor FREE RADIUS.";
                        try{
                            //Instalacion objInstalacion = new Instalacion(this._ip, this._puerto, this._db, this._usuario, this._clave);
                            //String usuarios = objInstalacion.getUsrFreeRadius(id_plan_servicio);
                            //objInstalacion.cerrar();

                            //String value = objPlanServicio.getRadPlan(id_plan_servicio);
                            String cadPlan = objPlanServicio.getRadPlan(id_plan_servicio);
                            String plan_nuevo = objPlanServicio.getSufPlan(id_plan_servicio);
                            
                            if(dbRadius.actualizarPlan(plan, plan_nuevo, cadPlan, login_time)){
                                msg = "";
                            }
                        }catch(Exception ex){
                            ex.printStackTrace();
                        }*/
                        
                        r = "fun»fac_postVelocidadGuardar('"+objPlanServicio.getRadPlanJSON(id_plan_servicio)+"', "+i+");";
                    }
                }
            }
            
            out.print(r);
        } finally {
            //dbRadius.cerrar();
            objPlanServicio.cerrar();
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