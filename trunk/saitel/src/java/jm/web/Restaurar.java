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

package jm.web;

import java.io.*;

import javax.servlet.*;
import javax.servlet.http.*;
import jm.seg.clas.Auditoria;

/**
 *
 * @author Jorge
 */
public class Restaurar extends HttpServlet {
    private String _ip = null;
    private int _puerto = 1433;
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
        this._usuario = (String)sesion.getAttribute("usuario");
        this._clave = (String)sesion.getAttribute("clave");
        
        response.setContentType("text/html;charset=UTF-8");
        response.setHeader("Pragma", "no-cache"); 
        response.setHeader("Expires", "Mon, 01 Jan 2001 00:00:01 GMT"); 
        response.setHeader("Cache-Control", "no-store"); 
        response.setHeader("Cache-Control", "must-revalidate"); 
        response.setHeader("Cache-Control", "no-cache");
        PrintWriter out = response.getWriter();
        
        String tabla = request.getParameter("t");
        String cpk = tabla.replace("tbl", "id");
        String c = request.getParameter("c");
        String funcion = request.getParameter("f");
        String claves [] = c.split(",");
        String res = "fun»"+funcion;
        try{    
            if(tabla.compareTo("tbl_usuario")==0){
                cpk = "alias";
            }
            for(int i=0; i<claves.length; i++){
                this.restaurar(request, tabla, cpk, claves[i]);
            }
        }catch(Exception e){
            res = "err»1^msg»"+e.getMessage();
        }
        try {
            out.print(res);
        } finally { 
            out.close();
        }
    } 

    public boolean restaurar(HttpServletRequest request, String tabla, String cpk, String clave1)
    {
        DataBase db = new DataBase(this._ip, this._puerto, this._db, this._usuario, this._clave);
        if(db.ejecutar("UPDATE " + tabla + " SET eliminado=false WHERE " + cpk + "='" + clave1 + "';")){
            Auditoria auditoria = new Auditoria(this._ip, this._puerto, this._db, this._usuario, this._clave);
            auditoria.setRegistro(request, "RESTAURACION DEL REGISTRO DE CODIGO " + clave1 + " EN LA TABLA " + tabla.replace("tbl_", ""));
            auditoria.cerrar();
            db.cerrar();
            return true;
        }
        db.cerrar();
        return false;
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
