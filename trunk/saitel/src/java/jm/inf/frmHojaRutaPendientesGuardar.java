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

package jm.inf;

import java.io.*;

import javax.servlet.*;
import javax.servlet.http.*;
import jm.fac.clas.Instalacion;
import jm.inf.clas.OrdenTrabajo;
import jm.inf.clas.Radius;
import jm.seg.clas.Auditoria;

/**
 *
 * @author Jorge
 */
public class frmHojaRutaPendientesGuardar extends HttpServlet {
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

        OrdenTrabajo objOrdenTrabajo = new OrdenTrabajo(this._ip, this._puerto, this._db, usuario, clave);

        String r = "msg»Ha ocurrido un error inesperado, por favor, vuelva a intentarlo más tarde o " +
                    "contáctese con el administrador del sistema para mayor información.";
        try {
            //String WHERE = request.getParameter("WHERE");
            //String p = request.getParameter("p") != null ? request.getParameter("p") : "0";
            //String id = request.getParameter("id"); // id de la hoja de ruta
            //String num_hoja_ruta = request.getParameter("num_hoja_ruta");

            String id_orden_trabajo = "";
            String numeros_orden = "";
            String revision = "";
            String motivo = "";
            String id_instalacion = "";
            String id_instalaciones = "";
            String tipo_trabajo = "";
            
            int i=0;
            while(request.getParameter("id_ortrs"+i) != null){
                id_orden_trabajo = request.getParameter("id_ortrs"+i);
                numeros_orden += request.getParameter("numero_orden"+i) + ", ";
                revision = request.getParameter("solRev"+i);
                motivo = request.getParameter("motiv"+i);
                objOrdenTrabajo.setEstadoOrden(id_orden_trabajo, revision, usuario, motivo);
                
                tipo_trabajo = request.getParameter("tipo_trabajo"+i);
                if(tipo_trabajo.compareTo("")==0){
                    id_instalacion = request.getParameter("id_instalacion"+i);
                    id_instalaciones += id_instalacion + ",";
                }
                i++;
            }
            if(id_instalaciones.compareTo("")!=0){
                id_instalaciones = id_instalaciones.substring(0, id_instalaciones.length()-1);
                Instalacion objInstalacion = new Instalacion(this._ip, this._puerto, this._db, usuario, clave);
                String radusernames = objInstalacion.getUsuariosFreeRadius(id_instalaciones);
                objInstalacion.cerrar();
                /*Radius dbRadius = new Radius(this._ip, this._puerto, this._db, usuario, clave);
                dbRadius.eliminaciones(radusernames);
                dbRadius.cerrar();*/
            }
            
            Auditoria auditoria = new Auditoria(this._ip, this._puerto, this._db, usuario, clave);
            auditoria.setRegistro(request, "REGISTRO DE OBSERVACIONES DE LAS ORDENES DE TRABAJO " + numeros_orden.substring(0, numeros_orden.length()-2));
            auditoria.cerrar();

            r = "err»0^vta»cmp^msg»Ordenes de trabajo revisadas satisfactoriamente.";

            /*HojaRuta objHojaRuta = new HojaRuta(this._ip, this._puerto, this._db, usuario, clave);
            if(objHojaRuta.revision(id, usuario)){
                
            }*/
            out.print(r);
            //objHojaRuta.cerrar();
        } finally {
            objOrdenTrabajo.cerrar();
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