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

package jm.act;

import java.io.*;

import javax.servlet.*;
import javax.servlet.http.*;
import jm.act.clas.Activo;
import java.sql.ResultSet;

/**
 *
 * @author Jorge
 */
public class getActivos extends HttpServlet {
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

        String texto = request.getParameter("txt");
        String funcion = request.getParameter("fun");
        String enter = request.getParameter("ent");
        String prefijo = request.getParameter("pf");
        String idBodEnt = request.getParameter("idBodEnt");
        String idActs = request.getParameter("idActs");

        Activo objActivo = new Activo(this._ip, this._puerto, this._db, usuario, clave);
        try {
            ResultSet rsActivos = enter.compareTo("13")==0 ? objActivo.getActivoFiltro(texto, idBodEnt, idActs) : 
                    (idBodEnt.compareTo("")!=0 ? objActivo.getActivosFiltro(texto, idBodEnt, idActs) : objActivo.getActivosFiltro(texto, idActs) );
            out.print("obj»ax^fun»"+funcion+"('"+objActivo.getJSON(rsActivos)+"', "+enter+", '"+prefijo+"')");
            try{
                rsActivos.close();
            }catch(Exception e){
                e.printStackTrace();
            }
        } finally {
            objActivo.cerrar();
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
