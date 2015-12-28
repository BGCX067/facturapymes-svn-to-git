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
public class frmPlanServicioGuardar extends HttpServlet {
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
            String r = "msg»El nombre del plan ya existe.";
            String WHERE = request.getParameter("WHERE");
            String p = request.getParameter("p") != null ? request.getParameter("p") : "0";
            String id = request.getParameter("id");
            String id_producto = request.getParameter("id_producto");
            String id_producto_anticipo = request.getParameter("id_producto_anticipo");
            String plan = request.getParameter("plan");
            String comparticion = request.getParameter("comparticion");
            String id_nivel_soporte = request.getParameter("id_nivel_soporte");
            String div_max_limit = request.getParameter("div_max_limit");
            String div_burst_threshold = request.getParameter("div_burst_threshold");
            String burst_time = request.getParameter("burst_time");
            String sim_subida = request.getParameter("sim_subida");

            if(!objPlanServicio.estaDuplicado(id, plan)){
                r = "msg»Ha ocurrido un error inesperado, por favor, vuelva a intentarlo más tarde o " +
                    "contáctese con el administrador del sistema para mayor información.";
                if(id.compareTo("-1")==0){
                    String pk = objPlanServicio.insertar(id_producto, id_producto_anticipo, plan, comparticion, id_nivel_soporte, 
                            div_max_limit, div_burst_threshold, burst_time, sim_subida);
                    if(pk.compareTo("-1")!=0){
                        Auditoria auditoria = new Auditoria(this._ip, this._puerto, this._db, usuario, clave);
                        auditoria.setRegistro(request, "INGRESO DEL NUEVO PLAN DE SERVICIO ISP: "+plan);
                        auditoria.cerrar();
                        r = "tbl»"+objPlanServicio.paginar("vta_plan_isp", "id_plan_isp,plan,comparticion,div_max_limit,div_burst_threshold,burst_time,sim_subida,id_nivel_soporte", WHERE, Integer.parseInt(p), 100)+
                            "^fun»fac_planEditar("+pk+")"+
                            "^msg»Información ingresada satisfactoriamente.";
                    }
                }else{
                    try{
                        String id_plan_servicio = "";
                        String SufPlan = "";
                        ResultSet rs = objPlanServicio.getVelocidades(id);
                        while(rs.next()){
                            id_plan_servicio = rs.getString("id_plan_servicio")!=null ? rs.getString("id_plan_servicio") : "";
                            SufPlan = objPlanServicio.getSufPlan(id_plan_servicio);
                            //dbRadius.eliminarPlan(SufPlan);
                        }
                        rs.close();
                    }catch(Exception e){
                        e.printStackTrace();
                    }
                    
                    if(objPlanServicio.actualizar(id, id_producto, id_producto_anticipo, plan, comparticion, id_nivel_soporte,
                            div_max_limit, div_burst_threshold, burst_time, sim_subida)){
                        
                        try{
                            /* creacion nuevamenteo de los planes */
                            Instalacion objInstalacion = new Instalacion(this._ip, this._puerto, this._db, usuario, clave);
                            
                            String id_plan_servicio = "";
                            String login_time = "";
                            String SufPlan = "";
                            //String radusername = "";
                            ResultSet rs = objPlanServicio.getVelocidades(id);
                            while(rs.next()){
                                id_plan_servicio = rs.getString("id_plan_servicio")!=null ? rs.getString("id_plan_servicio") : "0";
                                login_time = rs.getString("login_time")!=null ? rs.getString("login_time") : "0";
                                SufPlan = objPlanServicio.getSufPlan(id_plan_servicio);
                                String cadPlan = objPlanServicio.getRadPlan(id_plan_servicio);
                                //dbRadius.crearPlan(SufPlan, cadPlan, "20000", login_time);
                                
                                /* creacion de planes para cleintes que no tienen para este plan 
                                ResultSet rsPPOE = objInstalacion.getUsuariosNoRegistrados();
                                while(rsPPOE.next()){
                                    radusername = rsPPOE.getString("radusername")!=null ? rsPPOE.getString("radusername") : "";
                                    dbRadius.crear(radusername, SufPlan);
                                }
                                rsPPOE.close();*/
                            }
                            rs.close();
                            
                            objInstalacion.cerrar();
                        }catch(Exception e){
                            e.printStackTrace();
                        }
                        
                        Auditoria auditoria = new Auditoria(this._ip, this._puerto, this._db, usuario, clave);
                        auditoria.setRegistro(request, "ACTUALIZACION DE LA INFORMACION DEL PLAN DE SERVICIO ISP: "+plan);
                        auditoria.cerrar();
                        r = "tbl»"+objPlanServicio.paginar("vta_plan_isp", "id_plan_isp,plan,comparticion,div_max_limit,div_burst_threshold,burst_time,sim_subida,id_nivel_soporte", WHERE, Integer.parseInt(p), 100)+"^msg»Información guardada satisfactoriamente.";
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