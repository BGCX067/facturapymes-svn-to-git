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

package jm.seg;

import java.io.*;

import javax.servlet.*;
import javax.servlet.http.*;
import jm.web.DatosDinamicos;
import jm.seg.clas.Usuario;
import jm.seg.clas.Rol;
import java.sql.ResultSet;
/**
 *
 * @author Jorge
 */
public class frmUsuario extends HttpServlet {
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
        
        Rol obj_rol = new Rol(this._ip, this._puerto, this._db, usuario, clave);
        try {
            String id = request.getParameter("id");
            String alias = "";
            String id_rol = "";
            String autenticacion_ip = "127.0.0.1 - 0:0:0:0:0:0:0:1";
            boolean estado = true;
            ResultSet roles = obj_rol.getRoles();
            if(id.compareTo("-1")!=0){
                Usuario usr = new Usuario(this._ip, this._puerto, this._db, usuario, clave);
                try{
                    ResultSet rsUsuario = usr.getUsuario(id);
                    if(rsUsuario.next()){
                        alias = (rsUsuario.getString("alias")!=null) ? rsUsuario.getString("alias") : "";
                        id_rol = (rsUsuario.getString("id_rol")!=null) ? rsUsuario.getString("id_rol") : "";
                        autenticacion_ip = (rsUsuario.getString("autenticacion_ip")!=null) ? rsUsuario.getString("autenticacion_ip") : "";
                        estado = (rsUsuario.getString("estado")!=null) ? rsUsuario.getBoolean("estado") : true;
                        rsUsuario.close();
                    }
                }catch(Exception e){
                    e.printStackTrace();
                }finally{
                    usr.cerrar();
                }
            }
            
            String html = "obj»d_21^foc»a^frm»";
            html += "<form action='frmUsuarioGuardar' onsubmit='return seg_usuarioGuardar(this)' autocomplete='off'>";
            html += "<input type='hidden' id='id' name='id' value='"+id+"' />";
            html += "<input type='hidden' id='ra' name='ra' value='"+id_rol+"' />";
            
            html += "<table><tr><td colspan='2' class='H3'>Formulario de Usuario</td></tr>";
            html += "<tr><td>Rol: <span class='marca'>*</span></td>";                
            html += "<td>"+DatosDinamicos.combo(roles, "r", id_rol, "")+"</td></tr>";
            html += "<tr><td>Usuario: <span class='marca'>*</span></td>";
            html += "<td><input id='a' name='a' type='text' size='30' maxlength='15' "+((id.compareTo("-1")!=0) ? "readOnly" : "")+" value='"+alias+"' onkeypress=\"_alfanumerico(event);\" onblur=\"this.value=this.value._trim();\" /></td></tr>";
            html += "<tr><td>Contrase&ntilde;a:</td>";
            html += "<td><input id='c' name='c' type='password' size='30' maxlength='25' /></td></tr>";
            html += "<tr><td>Confirme Contrase&ntilde;a:</td>";
            html += "<td><input id='c2' name='c2' type='password' size='30' maxlength='25'></td></tr>";
            html += "<tr><td nowrap>Pc's desde donde puede autenticarse: <span class='marca'>*</span></td>";
            html += "<td><input id='ip' name='ip' type='text' size='30' maxlength='600' value='" + autenticacion_ip + "' onkeypress=\"_evaluar(event, '0123456789:.- ');\" onblur=\"this.value=this.value._trim();\" /></td></tr>";
            html += "<tr><td>Estado: <span class='marca'>*</span></td>";
            html += "<td><label><input type='radio' id='e0' name='e' value='true' "+((estado) ? "checked" : "")+" /> Activo </label><br/>" +
                    "<label><input type='radio' id='e1' name='e' value='false' "+((!estado) ? "checked" : "")+" /> Inactivo </label></td></tr>";
            html += "<tr><td colspan='2'><input type='submit' value='Guardar' /></td></tr>";
            html += "</table></form>";
            out.print(html);
            
            roles.close();
        }catch(Exception e){
            e.printStackTrace();
        } finally { 
            obj_rol.cerrar();
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
