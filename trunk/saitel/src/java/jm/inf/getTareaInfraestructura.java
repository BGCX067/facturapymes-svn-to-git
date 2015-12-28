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

import javax.servlet.*;
import javax.servlet.http.*;
import jm.inf.clas.OrdenTrabajo;
import java.sql.ResultSet;

/**
 *
 * @author Jorge
 */
public class getTareaInfraestructura extends HttpServlet {
    private String _ip = null;
    private int _puerto = 5432;
    private String _db = null;
    //private String _dir = null;

    public void init(ServletConfig config) throws ServletException
    {
        this._ip = config.getServletContext().getInitParameter("_IP");
        this._puerto = Integer.parseInt(config.getServletContext().getInitParameter("_PUERTO"));
        this._db = config.getServletContext().getInitParameter("_DB");
        //this._dir = config.getServletContext().getInitParameter("_DIR");
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
        String usuario = (String)sesion.getAttribute("usuario");
        String clave = (String)sesion.getAttribute("clave");
        
        response.setContentType("text/html;charset=UTF-8");
        response.setHeader("Pragma", "no-cache");
        response.setHeader("Expires", "Mon, 01 Jan 2001 00:00:01 GMT");
        response.setHeader("Cache-Control", "no-store");
        response.setHeader("Cache-Control", "must-revalidate");
        response.setHeader("Cache-Control", "no-cache");
        PrintWriter out = response.getWriter();

        String id_orden_trabajo = request.getParameter("id");
        String id_tarea_grupo = request.getParameter("idTa");
        String id_nodo = request.getParameter("idNodo");
        
        OrdenTrabajo objOrdenTrabajo = new OrdenTrabajo(this._ip, this._puerto, this._db, usuario, clave);
        ResultSet rsTarAsig =  objOrdenTrabajo.getTareasAsignadas(id_orden_trabajo);
        
        try {
            int i=0;
            String id_tarea = "";
            String id_tarea_padre = "";
            String tarea = "";
            ResultSet rsTareas = objOrdenTrabajo.getTiposTareas( Integer.parseInt(id_tarea_grupo), id_sucursal, id_nodo );
            out.print("obj»"+id_tarea_grupo+"^frm»");
            while(rsTareas.next()){
                id_tarea = (rsTareas.getString("id_tarea")!=null ? rsTareas.getString("id_tarea") : "");
                id_tarea_padre = (rsTareas.getString("id_tarea_padre")!=null ? rsTareas.getString("id_tarea_padre") : "");
                tarea = (rsTareas.getString("tarea")!=null ? rsTareas.getString("tarea") : "");
                out.print("<div><input type=\"hidden\" id=\"id_tarea"+id_tarea_padre+i+"\" name=\"id_tarea"+id_tarea_padre+i+"\" value=\""+id_tarea+"\" />"
                        + "<label><input type=\"checkbox\" "+(this.asignado(rsTarAsig, id_tarea) ? "checked" : "")+" id=\"ch"+id_tarea_padre+i+"\" name=\"ch"+id_tarea_padre+i+"\" />"+tarea+"</label></div>");
                i++;
            }
            rsTareas.close();
        }catch(Exception e){
                e.printStackTrace();
        } finally {
            objOrdenTrabajo.cerrar();
            out.close();
        }
    }

    public boolean asignado(ResultSet rs, String clave)
    {
        boolean ok = false;
        try{
            rs.beforeFirst();
            while(rs.next()){
                String id_tarea = rs.getString("id_tarea")!=null ? rs.getString("id_tarea") : "";
                if(clave.compareTo(id_tarea)==0){
                    return true;
                }
            }
            rs.beforeFirst();
        }catch(Exception e){
            e.printStackTrace();
        }
        return ok;
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