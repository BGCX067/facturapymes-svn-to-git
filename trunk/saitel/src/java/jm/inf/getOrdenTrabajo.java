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
import jm.inf.clas.OrdenTrabajo;
import java.sql.ResultSet;
import jm.web.Fecha;

/**
 *
 * @author Jorge
 */
public class getOrdenTrabajo extends HttpServlet {
    private String _ip = null;
    private int _puerto = 5432;
    private String _db = null;
    //private String _dir = null;

    public void init(ServletConfig config) throws ServletException
    {
        this._ip = config.getServletContext().getInitParameter("_IP");
        this._puerto = Integer.parseInt(config.getServletContext().getInitParameter("_PUERTO"));
        this._db = config.getServletContext().getInitParameter("_DB");
        //this._dir = config.getServletContext().getInitParameter("_DIR");
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

        try {
            String id = request.getParameter("id");
            String razon_social = "";
            String direccion_instalacion = "";
            String usuario_reporte="";
            String fecha_reporte="";
            String hora_reporte="";
            String fecha_cliente="";
            String hora_cliente="";
            String diagnostico_tecnico="";

            if(id.compareTo("-1")!=0){
                try{
                    ResultSet rsOrdenTrabajo = objOrdenTrabajo.getOrdenTrabajo(id);
                    if(rsOrdenTrabajo.next()){
                        razon_social = (rsOrdenTrabajo.getString("razon_social")!=null) ? rsOrdenTrabajo.getString("razon_social") : "";
                        direccion_instalacion = (rsOrdenTrabajo.getString("direccion_instalacion")!=null) ? rsOrdenTrabajo.getString("direccion_instalacion") : "";
                        usuario_reporte = (rsOrdenTrabajo.getString("usuario_reporte")!=null) ? rsOrdenTrabajo.getString("usuario_reporte") : "";
                        fecha_reporte = (rsOrdenTrabajo.getString("fecha_reporte")!=null) ? Fecha.ISOaSQL(rsOrdenTrabajo.getString("fecha_reporte")) : "";
                        hora_reporte = (rsOrdenTrabajo.getString("hora_reporte")!=null) ? rsOrdenTrabajo.getString("hora_reporte") : "";

                        fecha_cliente = (rsOrdenTrabajo.getString("fecha_cliente")!=null) ? Fecha.ISOaSQL(rsOrdenTrabajo.getString("fecha_cliente")) : "";
                        hora_cliente = (rsOrdenTrabajo.getString("hora_cliente")!=null) ? rsOrdenTrabajo.getString("hora_cliente") : "";
                        diagnostico_tecnico = (rsOrdenTrabajo.getString("diagnostico_tecnico")!=null) ? rsOrdenTrabajo.getString("diagnostico_tecnico") : "";
                        rsOrdenTrabajo.close();
                    }
                }catch(Exception e){
                    e.printStackTrace();
                }
            }

            out.println("obj»dv^frm»");
            out.println("<table width='100%' border='1'>");
            //out.println("<tr><td>Cliente: </td><td>"+razon_social+"</td></tr>");
            //out.println("<td>Usuario, fecha y hora de reporte: </td>");
            //out.println("<td>"+usuario_reporte+" en la fecha "+Fecha.ISOaSQL(fecha_reporte)+" a las "+hora_reporte+"</td></tr>");
            //if(fecha_cliente.compareTo("")!=0){
            //    out.println("<tr valign='top'><td style='font-size:9px'>Fecha para visita: </td>");
            //    out.println("<td style='font-size:9px'>"+fecha_cliente+" a las "+hora_cliente+"</td></tr>");
            //}
            out.println("<tr><td style='font-size:9px'>Cliente:</td><td style='font-size:9px'>"+razon_social+"</td></tr>");
            out.println("<tr><td style='font-size:9px'>Direcci&oacute;n:</td><td style='font-size:9px'>"+direccion_instalacion+"</td></tr>");
            out.println("<tr><td valign='top' style='font-size:9px'>Diagn&oacute;stico t&eacute;cnico: </td><td style='font-size:9px'>"+diagnostico_tecnico+"</td></tr>");
            out.println("</table>");
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