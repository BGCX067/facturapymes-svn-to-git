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

/**
 *
 * @author Jorge
 */
public class Creditos extends HttpServlet {
   
    /** 
    * Processes requests for both HTTP <code>GET</code> and <code>POST</code> methods.
    * @param request servlet request
    * @param response servlet response
    */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();
        try {
            String html = "obj»credito_html^frm»<table width='100%' align='center'>";
            html += "<tr align='center'>";
            html += "  <td align='center' bgcolor='#FFFFFF' width='110'><img src='img/fp_logo_fp.jpg'></td>";
            html += "  <td><div style='font-family: Verdana, Arial, Helvetica, sans-serif;color:#000033;font-size:24px;'><strong>&nbsp;Facturapymes </strong></div>";
            html += "    <div style='font-family: Verdana, Arial, Helvetica, sans-serif;color:#003366;font-size:24px;'>&nbsp;Versi&oacute;n 1.0</div></td>";
            html += "</tr>";
            html += "<tr>";
            html += "  <td colspan='2' style='font-family: Verdana, Arial, Helvetica, sans-serif;color:#000033;color:#000000;font-size:12px;'>";
            html += "    <p><br><strong>DESARROLLADOR:</strong> Jorge W. Mueses Cevallos.</p>";
            html += "    <p style='font-size:10px;color:#0000FF;'>Teléfono: (593) (06) 2 545 151 <br>";
            html += "    Móvil: (593) 095204832<br>";
            html += "    Mail: jorge_mueses@yahoo.com</p>";
            html += "  </td>";
            html += "</tr>";
            html += "<tr>";
            html += "  <td colspan='2'>";
            html += "<fieldset><div style='text-align:center'><a rel='license' href='http://creativecommons.org/licenses/by-nc-nd/3.0/' target='_blank'>"
                    + "<img alt='Licencia Creative Commons' style='border-width:0' src='img/88x31.png' />"
                    + "</a>"
                    + "<br />Esta obra est&aacute; bajo una <a rel='license' href='http://creativecommons.org/licenses/by-nc-nd/3.0/' target='_blank'>licencia Creative Commons Atribuci&oacute;n-NoComercial-SinDerivadas 3.0 Unported</a>.</div>"
                    + "<br /><a href='license.txt' target='_blank'>Mirar texto localmente</a></fieldset>";
            html += "  </td>";
            html += "</tr>";
            html += "<tr><td align='right' colspan='2'><input type='button' value='Aceptar' onclick=\"_R('bloq_credito');_R('credito');\" /></td></tr>";
            html += "</table>";
            out.println(html);
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
