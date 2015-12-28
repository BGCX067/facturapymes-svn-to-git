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

package jm.nom;

import java.io.*;

import javax.servlet.*;
import javax.servlet.http.*;
import java.sql.ResultSet;
import jm.nom.clas.Area;
import jm.nom.clas.Cargo;
import jm.nom.clas.permisosLicencias;
import jm.web.DatosDinamicos;
/**
 *
 * @author Jorge
 */
public class frmTipoPermiso extends HttpServlet {
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
        
        permisosLicencias objPermisosLicencias = new permisosLicencias(this._ip, this._puerto, this._db, usuario, clave);
        
        try {
            String id = request.getParameter("id");
            String descripcion = "";
            String id_tipo = "";
            String nombre = "";
            String numdias = "0";
            boolean estado = true;
            
            if(id.compareTo("-1")!=0){
                try{
                    ResultSet grupo = objPermisosLicencias.getTiposPermisos(id);
                    if(grupo.next()){
                        id_tipo = (grupo.getString("id_tipo")!=null) ? grupo.getString("id_tipo") : "";
                        nombre = (grupo.getString("nombre")!=null) ? grupo.getString("nombre") : "";
                        descripcion = (grupo.getString("descripcion")!=null) ? grupo.getString("descripcion") : "";
                        numdias = (grupo.getString("numdias")!=null) ? grupo.getString("numdias") : "0";
                        estado = (grupo.getString("eliminado")!=null) ? grupo.getBoolean("eliminado") : true;
                        grupo.close();
                    }
                }catch(Exception e){
                    e.printStackTrace();
                }finally{
                    objPermisosLicencias.cerrar();
                }
            }
                        
            String html = "obj»d_21^foc»nombre^frm»";
            html += "<form action='frmTipoPermisoGuardar' onsubmit='return pyl_tipoPermisosGuardar(this)' autocomplete='off'>";
            html += "<input type='hidden' id='id' name='id' value='"+id+"' />";
            html += "<table><tr><td colspan='2' class='H3'>Formulario de Tipo de Permisos</td></tr>";
            html += "<tr><td nowrap>Nombre: <span class='marca'>*</span></td>";
            html += "<td><input id='nombre' name='nombre' type='text' size='50' maxlength='60' value='"+nombre+"' onblur=\"this.value=this.value._trim();\" /></td>";
            html += "<tr><td nowrap>Descripcion: <span class='marca'>*</span></td>";                
            html += "<td><input id='descripcion' name='descripcion' type='text' size='50' maxlength='60' value='"+descripcion+"' onblur=\"this.value=this.value._trim();\" /></td>";
            html += "<tr><td nowrap>Numero de Dias: <span class='marca'>*</span></td>";                
            html += "<td><input id='numdias' name='numdias' type='text' size='50' maxlength='60' value='"+numdias+"' onkeypress=\"  onblur=\"this.value=this.value._trim();\" /></td>";
            html += "<tr><td>Estado: <span class='marca'>*</span></td>";
            html += "<td><input type='radio' id='estado0' name='estado' value='false' "+((!estado) ? "checked" : "")+" /> Activo &nbsp;&nbsp;&nbsp;&nbsp;&nbsp; " +
                    "<input type='radio' id='estado1' name='estado' value='true' "+((estado) ? "checked" : "")+" /> Inactivo</td></tr>";
            html += "<tr><td colspan='2'><input type='submit' value='Guardar' /></td></tr>";
            html += "</table></form>";
            out.print(html);
            
        } finally { 
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
