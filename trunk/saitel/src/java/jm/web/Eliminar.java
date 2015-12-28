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
//import jm.nom.clas.RolPago;
import jm.seg.clas.Auditoria;

/**
 * Servlet que elimina registros de una tabla de una base de datos.
 */
public class Eliminar extends HttpServlet {
   
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
        
        response.setContentType("application/text/plain");
        PrintWriter out = response.getWriter();
        
        String t = request.getParameter("t").replace("vta_", "tbl_");
        String cpk = request.getParameter("cpk");
        String c = request.getParameter("c");
        String claves [] = c.split(",");
        String res = "fun»_objT.insertar();";
        try{    
            for(int i=0; i<claves.length; i++){
                /*if(t.compareTo("tbl_rol_pago_detalle")==0){
                    String periodo = "";
                    RolPago objRolPago = new RolPago(this._ip, this._puerto, this._db, usuario, clave);
                    ResultSet rsRolPago = objRolPago.consulta("select periodo from tbl_rol_pago_detalle where id_rol_pago_detalle="+claves[i]+";");
                    try{
                        if(rsRolPago.next()){
                            periodo = rsRolPago.getString("periodo")!=null ? rsRolPago.getString("periodo") : "";
                            rsRolPago.close();
                        }
                        if(objRolPago.eliminar(claves[i], periodo)){
                            this.registrarAuditoria(request, t, cpk, claves[i]);
                        }
                    }catch(Exception e){
                        e.printStackTrace();
                    }
                    objRolPago.cerrar();
                }else{*/
                    this.eliminar(request, t, cpk, claves[i]);
                //}
            }
        }catch(Exception e){
            System.out.print(e.getMessage());
        }
        try {
            out.print(res);
        } finally { 
            out.close();
        }
    } 

    public boolean eliminar(HttpServletRequest request, String tabla, String cpk, String clave1)
    {
        DataBase db = new DataBase(this._ip, this._puerto, this._db, this._usuario, this._clave);
        if(db.ejecutar("DELETE FROM " + tabla + " WHERE " + cpk + "=" + clave1 + ";")){
            Auditoria auditoria = new Auditoria(this._ip, this._puerto, this._db, this._usuario, this._clave);
            auditoria.setRegistro(request, "ELIMINACION FISICA DEL REGISTRO DE CODIGO " + clave1 + " EN LA TABLA " + tabla.replace("tbl_", ""));
            auditoria.cerrar();
            db.cerrar();
            return true;
        }
        db.cerrar();
        return false;
    }

    public void registrarAuditoria(HttpServletRequest request, String tabla, String cpk, String clave1)
    {
        Auditoria auditoria = new Auditoria(this._ip, this._puerto, this._db, this._usuario, this._clave);
        auditoria.setRegistro(request, "ELIMINACION FISICA DEL REGISTRO DE CODIGO " + clave1 + " EN LA TABLA " + tabla.replace("tbl_", ""));
        auditoria.cerrar();
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
