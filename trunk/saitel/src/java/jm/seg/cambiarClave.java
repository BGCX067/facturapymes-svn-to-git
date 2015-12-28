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

/**
 *
 * @author Administrador
 */
public class cambiarClave extends HttpServlet {
   
    /** 
    * Processes requests for both HTTP <code>GET</code> and <code>POST</code> methods.
    * @param request servlet request
    * @param response servlet response
    */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        response.setHeader("Pragma", "no-cache"); 
        response.setHeader("Expires", "Mon, 01 Jan 2001 00:00:01 GMT"); 
        response.setHeader("Cache-Control", "no-store"); 
        response.setHeader("Cache-Control", "must-revalidate"); 
        response.setHeader("Cache-Control", "no-cache");
        
        PrintWriter out = response.getWriter();
        try {
            String r = "obj»vta_clave_html^foc»r_clave^frm»<form action='cambiarClaveGuardar' onsubmit='return establecerClave(this)' autocomplete='off'>"
                    + "<table align='center'>";
            r += "<tr><td>Nueva contrase&ntilde;a:</td>";
            r += "<td><input type='password' id='r_clave' name='r_clave' size='15' maxlength='25' /></td></tr>";
            r += "<tr><td>Confirme nueva contrase&ntilde;a:</td>";
            r += "<td><input type='password' id='r_clave2' name='r_clave2' size='15' maxlength='25' /></td></tr>";
            r += "<tr><td align=center colspan='2'><input type='submit' value='Guardar' /></td></tr>";
            r += "</table></form>";
            out.print(r);
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
