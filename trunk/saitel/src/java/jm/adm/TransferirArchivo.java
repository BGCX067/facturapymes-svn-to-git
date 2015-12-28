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

/**
 *
 * @author Jorge
 */
public class TransferirArchivo extends HttpServlet {
   
    /** 
    * Processes requests for both HTTP <code>GET</code> and <code>POST</code> methods.
    * @param request servlet request
    * @param response servlet response
    */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        HttpSession sesion = request.getSession(true);

        response.setContentType("text/html;charset=UTF-8");
        response.setHeader("Pragma", "no-cache"); 
        response.setHeader("Expires", "Mon, 01 Jan 2001 00:00:01 GMT"); 
        response.setHeader("Cache-Control", "no-store"); 
        response.setHeader("Cache-Control", "must-revalidate"); 
        response.setHeader("Cache-Control", "no-cache");
        PrintWriter out = response.getWriter();

        String tabla = request.getParameter("t");
        String campoNombre = request.getParameter("cN");
        String campoBytea = request.getParameter("cB");
        String clave = request.getParameter("pk");
        
        sesion.setAttribute("tabla", tabla);
        sesion.setAttribute("campoNombre", campoNombre);
        sesion.setAttribute("campoBytea", campoBytea);
        sesion.setAttribute("clave1", clave);
        
        try {
            out.println("obj»vta_subir_html^frm»<form action='ProcesaTransferirArchivo' method='post' enctype='multipart/form-data' target='procesaTransferencia' onsubmit='return iniciaTransferencia();' >" +
                    "<p id='cargando' style='display:none;'><br/>Tranfiriendo archivo... &nbsp;<img src='img/subiendo.gif' /></p>" +
                    "<div id='frmArchivo' align='center'><br/>" +
                    "Archivo de Imagen (*.jpg) <br/>" +
                    "Tama&ntilde;o: 1 Mb m&aacute;ximo<br/>"+ 
                    /*"<input type='hidden' id='tabla' name='tabla' value='"+tabla+"' />" +
                    "<input type='hidden' id='campoNombre' name='campoNombre' value='"+campoNombre+"' />" +
                    "<input type='hidden' id='campoBytea' name='campoBytea' value='"+campoBytea+"' />" +
                    "<input type='hidden' id='clave' name='clave' value='"+clave+"' />" +*/
                    "<input id='archivo' name='archivo' type='file' size='30' /><br/><br/>" +
                    "<input type='submit' name='submitBtn' value='Tranferir' />" +
                    "</div>" +
                    "<iframe name='procesaTransferencia' src='#' style='width:0;height:0;border:0px solid #fff;'></iframe>" +
                    "</form>");
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
