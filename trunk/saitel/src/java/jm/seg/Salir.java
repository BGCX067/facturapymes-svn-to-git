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
import javax.servlet.*;
import javax.servlet.http.*;
import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.http.HttpSession;
import jm.seg.clas.Auditoria;

public class Salir extends HttpServlet 
{
    private String _ip = null;
    private int _puerto = 5432;
    private String _db = null;
    
    public void init(ServletConfig config) throws ServletException
    {
        super.init(config);
        this._ip = config.getServletContext().getInitParameter("_IP");
        this._puerto = Integer.parseInt(config.getServletContext().getInitParameter("_PUERTO"));
        this._db = config.getServletContext().getInitParameter("_DB");
    }

    public void processRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
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

        Auditoria auditoria = new Auditoria(this._ip, this._puerto, this._db, usuario, clave);
        auditoria.setRegistro(request, "CIERRE DE SESION DEL USUARIO: "+ sesion.getAttribute("usuario"));
        auditoria.cerrar();

        sesion.removeAttribute("id_rol");
        sesion.removeAttribute("usuario");
        sesion.removeAttribute("titulo");
        sesion.removeAttribute("ip");
        sesion.removeAttribute("sucursal");
        sesion.invalidate();
        try{
            request.logout();
            response.sendRedirect("index.jsp");
        }catch(Exception e){
            out.print("<h2>Usted ha salido del sistema. Haga click <a href=\"\">aqu&iacute;</a> para ir a la p&aacute;gina de autenticaci&oacute;n.</h2>");
            e.getMessage();
        }finally {
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
}