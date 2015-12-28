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
import jm.fac.clas.Instalacion;
import jm.fac.clas.PlanServicio;
//import jm.inf.clas.Radius;
import jm.seg.clas.Auditoria;

/**
 *
 * @author Jorge
 */
public class frmCambioPlanGuardar extends HttpServlet {
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
        
        Instalacion objInstalacion = new Instalacion(this._ip, this._puerto, this._db, usuario, clave);
        
        try {
            String r = "msg»La fecha de cambio debe tener por lo menos un mes de diferencia.";
            String id_cambio_plan = request.getParameter("idCP");
            String id_instalacion = request.getParameter("id");
            String id_plan_servicio = request.getParameter("idPlSvr");
            String fecha_cambio = request.getParameter("fchC");

            if(!objInstalacion.enConflictoCambioPlan(id_cambio_plan, id_instalacion, fecha_cambio)){
                r = "msg»Ha ocurrido un error inesperado, por favor, vuelva a intentarlo más tarde o " +
                    "contáctese con el administrador del sistema para mayor información.";

                String radusername = "";
                String num_instalacion = "";
                String id_sucursal = "";
                try{
                    ResultSet rsInstalacion = objInstalacion.getInstalacion(id_instalacion);
                    if(rsInstalacion.next()){
                        radusername = (rsInstalacion.getString("radusername")!=null) ? rsInstalacion.getString("radusername") : "";
                        id_sucursal = (rsInstalacion.getString("id_sucursal")!=null) ? rsInstalacion.getString("id_sucursal") : "";
                        num_instalacion = (rsInstalacion.getString("num_instalacion")!=null) ? rsInstalacion.getString("num_instalacion") : "";

                        rsInstalacion.close();
                    }
                }catch(Exception e){
                    e.printStackTrace();
                }

                if(id_cambio_plan.compareTo("-1")==0){
                    String i = request.getParameter("i");
                    String pk = objInstalacion.insertarCambioPlan(id_instalacion, id_plan_servicio, fecha_cambio);
                    if(pk.compareTo("-1")!=0){
                        
                        PlanServicio objPlanServicio = new PlanServicio(this._ip, this._puerto, this._db, usuario, clave);
                        String grupo = objPlanServicio.getSufPlan(id_plan_servicio);
                        objPlanServicio.cerrar();
                        /*Radius dbRadius = new Radius(this._ip, this._puerto, this._db, usuario, clave);
                        dbRadius.setGrupoRadusergroup(radusername, grupo);
                        dbRadius.cerrar();*/

                        Auditoria auditoria = new Auditoria(this._ip, this._puerto, this._db, usuario, clave);
                        auditoria.setRegistro(request, "REGISTRO DE NUEVO CAMBIO DE PLAN DE SERVICIO DE INTERNET PARA LA INSTALACION NRO. "+id_sucursal+"-"+num_instalacion);
                        auditoria.cerrar();
                        r = "fun»_('idCP"+i+"').value="+pk+";^msg»Información ingresada satisfactoriamente.";
                    }
                }else{
                    if(objInstalacion.actualizarCambioPlan(id_cambio_plan, id_instalacion, id_plan_servicio, fecha_cambio)){
                        Auditoria auditoria = new Auditoria(this._ip, this._puerto, this._db, usuario, clave);
                        auditoria.setRegistro(request, "ACTUALZACION DEL CAMBIO DE PLAN DE SERVICIO DE INTERNET PARA LA INSTALACION NRO: "+id_sucursal+"-"+num_instalacion);
                        auditoria.cerrar();
                        r = "msg»Información guardada satisfactoriamente.";
                    }
                }
            }

            out.print(r);
        } finally {
            objInstalacion.cerrar();
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