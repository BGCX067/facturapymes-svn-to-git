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

package jm.adm;

import java.io.*;

import javax.servlet.*;
import javax.servlet.http.*;
import jm.web.DatosDinamicos;
import java.sql.ResultSet;
import jm.adm.clas.Ubicacion;
import jm.seg.clas.Rol;

/**
 *
 * @author Jorge
 */
public class frmUbicacion extends HttpServlet {
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
        String id_rol = (String)sesion.getAttribute("id_rol");
        String usuario = (String)sesion.getAttribute("usuario");
        String clave = (String)sesion.getAttribute("clave");
        
        response.setContentType("text/html;charset=UTF-8");
        response.setHeader("Pragma", "no-cache");
        response.setHeader("Expires", "Mon, 01 Jan 2001 00:00:01 GMT");
        response.setHeader("Cache-Control", "no-store");
        response.setHeader("Cache-Control", "must-revalidate");
        response.setHeader("Cache-Control", "no-cache");
        PrintWriter out = response.getWriter();

        Rol objRol = new Rol(this._ip, this._puerto, this._db, usuario, clave);
        Ubicacion objUbicacion = new Ubicacion(this._ip, this._puerto, this._db, usuario, clave);

        try {
            String id = request.getParameter("id");
            String id_padre = "1";
            String ubicacion = "";
            
            ResultSet rsUbicaciones = objUbicacion.getUbicaciones();
            if(id.compareTo("-1")!=0){
                try{
                    ResultSet rs = objUbicacion.getUbicacion(id);
                    if(rs.next()){
                        id_padre = (rs.getString("id_padre")!=null) ? rs.getString("id_padre") : "1";
                        ubicacion = (rs.getString("ubicacion")!=null) ? rs.getString("ubicacion") : "";
                        rs.close();
                    }
                }catch(Exception e){
                    e.printStackTrace();
                }
            }

            String html = "obj»d_21^foc»ubicacion^frm»";
            html += "<form action='frmUbicacionGuardar' onsubmit='return adm_ubicacionGuardar(this)' autocomplete='off'>";
            html += "<input type='hidden' id='id' name='id' value='"+id+"' />";
            html += "<table><tr><td colspan='2' class='H3'>Formulario de Ubicaciones</td></tr>";
            html += "<tr><td nowrap>Ubicaci&oacute;n a la que pertenece:</td>";
            html += "<td>"+DatosDinamicos.combo(rsUbicaciones, "id_padre", id_padre, "", 270)+"</td></tr>";
            html += "<tr><td>Ubicaci&oacute;n (cant&oacute;n - parroquia): <span class='marca'>*</span></td>";
            html += "<td><input id='ubicacion' name='ubicacion' type='text' size='40' maxlength='50' value='"+ubicacion+"' /></td></tr>";

            if(id.compareTo("-1")==0 || (id.compareTo("-1")!=0 && objRol.getAcceso(id_rol, "planCuentaEditar"))){
                html += "<tr><td colspan='2'><input type='submit' value='Guardar' /></td></tr>";
            }
            html += "</table></form>";
            out.println(html);

            rsUbicaciones.close();
            objUbicacion.cerrar();
        }catch(Exception e){
            e.printStackTrace();
        } finally {
            objUbicacion.cerrar();
            objRol.cerrar();
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