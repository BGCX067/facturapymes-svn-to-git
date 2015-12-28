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
import jm.adm.clas.Mensaje;

/**
 * Servlet que consigue los datos de una tabla en formato JSON.
 */
public class getNotificaciones extends HttpServlet {

    private String _ip = null;
    private int _puerto = 1433;
    private String _db = null;

    public void init(ServletConfig config) throws ServletException
    {
        this._ip = config.getServletContext().getInitParameter("_IP");
        this._puerto = Integer.parseInt(config.getServletContext().getInitParameter("_PUERTO"));
        this._db = config.getServletContext().getInitParameter("_DB");
    }

/**
 * Funci�n que retorna al cliente los datos de una tabla filtrada en formato JSON.
 * @param request servlet request
 * @param response servlet response
 * @return una cadena de respuesta al cliente.
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
        
        Mensaje objMensaje = new Mensaje(this._ip, this._puerto, this._db, usuario, clave);
        ResultSet rs = objMensaje.getMensajes(usuario);
        try {
            if(objMensaje.getFilas(rs)>0){
                String id = "0";
                out.print("obj»d_notfCuerpo^fun»not_mostrar('");
                try{
                    while(rs.next()){
                        id = (rs.getString("id_mensaje")!=null ? rs.getString("id_mensaje") : "0");
                        out.print("<div id=\"id_notf"+id+"\" class=\"jm_fila d_notfMsg\">"
                                + "<div style=\"float:left;width:145px;padding:7px;text-align:justify;\">"+(rs.getString("mensaje")!=null ? rs.getString("mensaje") : "&nbsp;")+"</div>"
                                + "<div style=\"float:left;width:15px;padding-top:7px;\"><img src=\"img/cerrar2.gif\" style=\"cursor:pointer;\" onclick=\"not_ocultar("+id+")\" alt=\"no volver a mostrar este aviso\" title=\"no volver a mostrar este aviso\" /></div>"
                                + "</div>"
                                + "<div id=\"id_notf_sep"+id+"\" class=\"jm_fila d_notfMsg\"><div> <hr class=\"d_notSep\" /> </div></div>");
                    }
                    rs.close();
                }catch(Exception e){
                    e.printStackTrace();
                }
                out.print("');");
            }
        } finally {
            objMensaje.cerrar();
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
