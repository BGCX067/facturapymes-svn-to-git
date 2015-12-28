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
import java.sql.ResultSet;

import javax.servlet.*;
import javax.servlet.http.*;
import jm.seg.clas.Auditoria;

/**
 * Servlet que elimina registros de una tabla de una base de datos.
 */
public class EliminacionLog extends HttpServlet {
    private DataBase db = null;
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
 * Funci�n que gestiona la eliminaci�n de registros de una tabla.
 * @param request servlet request
 * @param response servlet response
 * @return una cadena de respuesta al cliente.
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
        
        String t = request.getParameter("t").replace("vta_", "tbl_");
        String cpk = request.getParameter("cpk");
        String c = request.getParameter("c");
        String claves [] = c.split(",");
        String res = "fun»_objT.insertar();";
        
        this.db = new DataBase(this._ip, this._puerto, this._db, this._usuario, this._clave);
        try{    
            if(t.compareTo("tbl_usuario_rol")==0){
                t = "tbl_usuario";
            }
            
            for(int i=0; i<claves.length; i++){
                if((t.compareTo("tbl_usuario")==0 && claves[i].compareTo("administrador")==0) ||
                   (t.compareTo("tbl_rol")==0 && claves[i].compareTo("1")==0)){
                    res += "^msg»El perfil administradores y el usuario administrador no pueden ser eliminados";
                    continue;
                }
                if(t.compareTo("tbl_rol")==0){
                    this.eliminar(request, "tbl_usuario", "id_rol", claves[i]);
                }
                if(t.compareTo("tbl_area")==0){
                    if(this.existe("tbl_cargo", cpk, claves[i])){
                        res += "^msg»Una de la/las áreas o departamento contiene cargos, mueva toda referencia de cargos para poder eliminar. ";
                        continue;
                    }
                }
                if(t.compareTo("tbl_cargo")==0){
                    if(this.existe("tbl_empleado", cpk, claves[i])){
                        res += "^msg»Una de el/los cargos contiene empleados, mueva toda referencia de empleados para poder eliminar. ";
                        continue;
                    }
                }
                this.eliminar(request, t, cpk, claves[i]);
            }            
            
        }catch(Exception e){
            System.out.print(e.getMessage());
        }finally{
            this.db.cerrar();
        }
        
        
        try {
            out.print(res);
        } finally { 
            out.close();
        }
    } 

    public boolean eliminar(HttpServletRequest request, String tabla, String cpk, String clave1)
    {
        if(this.db.ejecutar("UPDATE " + tabla + " SET eliminado=true WHERE " + cpk + "='" + clave1 + "';")){
            Auditoria auditoria = new Auditoria(this._ip, this._puerto, this._db, this._usuario, this._clave);
            auditoria.setRegistro(request, "ELIMINACION DEL REGISTRO DE CODIGO " + clave1 + " EN LA TABLA " + tabla.replace("tbl_", ""));
            auditoria.cerrar();
            return true;
        }
        return false;
    }
    
    public boolean existe(String tabla, String cpk, String clave1)
    {
        ResultSet rs = this.db.consulta("SELECT * FROM " + tabla + " WHERE " + cpk + "='" + clave1 + "' and eliminado=false;");
        if(this.db.getFilas(rs)>0){
            return true;
        }
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
