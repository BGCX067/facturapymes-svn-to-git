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

package jm.fac.pdf;

import java.io.*;
import java.sql.ResultSet;

import javax.servlet.*;
import javax.servlet.http.*;
import jm.fac.clas.Instalacion;
import jm.web.Fecha;

/**
 *
 * @author Jorge
 */
public class ActivacionesListas extends HttpServlet {
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
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        HttpSession sesion = request.getSession(true);
        String usuario = (String)sesion.getAttribute("usuario");
        String clave = (String)sesion.getAttribute("clave");
        
        response.setContentType("text/plain;");
        response.setHeader("Content-disposition", "attachment; filename=activaciones_"+Fecha.getFecha("ISO")+"_"+Fecha.getHora()+".txt;");
        response.setHeader("Pragma", "no-cache");
        response.setHeader("Expires", "Mon, 01 Jan 2001 00:00:01 GMT");
        response.setHeader("Cache-Control", "no-store");
        response.setHeader("Cache-Control", "must-revalidate");
        response.setHeader("Cache-Control", "no-cache");
        PrintWriter out = response.getWriter();
        
        Instalacion objInstalacion = new Instalacion(this._ip, this._puerto, this._db, usuario, clave);
        
        try {
            String idSuc = request.getParameter("idSuc");

            String txt = "/ip firewall address-list\n";
            txt += "remove [find]\n";
            try{
                String cliente = "";
                String ip = "";
                //String txt_sucursal = "";
                //String ax_txt_sucursal = "";
                String plan = "";
                ResultSet res = objInstalacion.getActivacionesServidor(idSuc);
                while(res.next()){
                    cliente = res.getString("razon_social")!=null ? res.getString("razon_social") : "";
                    ip = res.getString("ip")!=null ? res.getString("ip") : "";
                    plan = res.getString("plan")!=null ? res.getString("plan") : "";
                    /*txt_sucursal = res.getString("txt_sucursal")!=null ? res.getString("txt_sucursal") : "";
                    if(txt_sucursal.compareTo(ax_txt_sucursal)!=0){
                        txt += txt_sucursal + "\n";
                        ax_txt_sucursal = txt_sucursal;
                    }*/
                    txt += "add address="+ip+" comment=\""+cliente+"\" \\\nlist=activos\n";
                    if(plan.indexOf("nocturno")>=0){
                        txt += "add address="+ip+" comment=\""+cliente+"\" \\\nlist=nocturnos\n";
                    }
                }
                res.close();
            }catch(Exception e){
                e.printStackTrace();
            }

            out.print(txt);
        } finally {
            objInstalacion.cerrar();
            out.close();
        }
    }


    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Returns a short description of the servlet.
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}
