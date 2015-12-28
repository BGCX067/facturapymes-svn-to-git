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

package jm.fac;

import java.io.*;

import javax.servlet.*;
import javax.servlet.http.*;

/**
 *
 * @author Jorge
 */
public class verFtpLocal extends HttpServlet {
    private String _ftp = null;
    private String _ANEXOS = null;

    public void init(ServletConfig config) throws ServletException
    {
        this._ftp = config.getServletContext().getInitParameter("_FTP_PICHINCHA"); 
        this._ANEXOS = config.getServletContext().getInitParameter("_ANEXOS"); 
    }
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

        //this._ftp = "/home/jorge/Documentos/FTP/";
        
        try {
            out.print("obj»axFtpLoc^frm»<TABLE class='jm_tabla' cellspacing='1' cellpadding='0' width='100%'>");
            
            File f = new File(this._ftp);
            if(f.exists()){
                File ficheros[] = f.listFiles();
                for(int x=0;x<ficheros.length;x++){
                    if(ficheros[x].isFile()){
                        out.print("<tr class='jm_filaPar'><td width='12'><input id=\"chArchLoc"+x+"\" type=\"checkbox\" /></td>"
                                + "<td valign='middle'><input type='hidden' id='archLoc"+x+"' value='"+ficheros[x].getName()+"' />"
                                + "<a href=\"" + (this._ANEXOS + ficheros[x].getName()) + "\" target=\"_blank\">"+ficheros[x].getName()+"</a></td>"
                                + "<td width='12'><div class='elim' onclick=\"fac_remFicheroLocal('"+ficheros[x].getName()+"')\">&nbsp;</div></td></tr>");
                    }
                }
            }else{
                out.print("<tr><td>No se han encontrado archivos en la ruta "+this._ftp+"</td></tr>");
            }
            out.print("</table>");
            
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
