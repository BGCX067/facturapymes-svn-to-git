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
import jm.fac.clas.Instalacion;
import jm.fac.clas.PlanServicio;
import jm.inf.clas.Radius;
import jm.seg.clas.Auditoria;

/**
 *
 * @author Jorge
 */
public class radActualizarUsuario extends HttpServlet {
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
        //Radius dbRadius = new Radius(this._ip, this._puerto, this._db, usuario, clave);
        
        try {
            String r = "msg»Ha ocurrido un error inesperado, por favor, vuelva a intentarlo más tarde o " +
                            "contáctese con el administrador del sistema para mayor información.";
            String id = request.getParameter("id");
            String usuario_nuevo = request.getParameter("user");
            String usuario_ant = request.getParameter("user_ant");
            //int i=0;

            usuario_nuevo = usuario_nuevo.replace(" ", "").toUpperCase();

            String id_plan_actual = "";
            try{
                ResultSet rsInstalacion = objInstalacion.getInstalacion(id);
                if(rsInstalacion.next()){
                    id_plan_actual = (rsInstalacion.getString("id_plan_actual")!=null) ? rsInstalacion.getString("id_plan_actual") : "";
                    rsInstalacion.close();
                }
            }catch(Exception e){
                e.printStackTrace();
            }

            
            
            if(!objInstalacion.hayRadUserName(id, usuario_nuevo)){
                if(objInstalacion.setRadusername(id, usuario_nuevo)){
                    boolean ok = false;

                    PlanServicio objPlanServicio = new PlanServicio(this._ip, this._puerto, this._db, usuario, clave);
                    String plan = objPlanServicio.getSufPlan(id_plan_actual);
                    objPlanServicio.cerrar();

                    /*if(!dbRadius.hayGrupoRadusergroup(usuario_ant)){
                        if(dbRadius.crear(usuario_nuevo, plan)){
                            ok = true;
                        }
                    }else{
                        if(dbRadius.actualizarUsuario(usuario_nuevo, usuario_ant)){
                            ok = true;
                        }
                    }*/

                    if(ok){

                        Auditoria auditoria = new Auditoria(this._ip, this._puerto, this._db, usuario, clave);
                        auditoria.setRegistro(request, "ACTUALIZACION DEL USUARIO " + usuario_ant + " AL " + usuario_nuevo + " EN EL FREERADIUS");
                        auditoria.cerrar();

                        r = "fun»_('radusername_ant').value='"+usuario_nuevo+"';";
                    }
                }
            }else{
                r = "msg»Usuario FreeRadius ya existe.";
            }

            out.print(r);
        } finally {
            objInstalacion.cerrar();
            //dbRadius.cerrar();
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
