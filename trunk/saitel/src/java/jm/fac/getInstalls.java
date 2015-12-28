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
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import jm.fac.clas.Instalacion;

/**
 *
 * @author Jorge
 */
public class getInstalls extends HttpServlet {
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
        int id_sucursal_sesion = (Integer)sesion.getAttribute("sucursal");
        String usuario = (String)sesion.getAttribute("usuario");
        String clave = (String)sesion.getAttribute("clave");

        response.setContentType("text/html;charset=UTF-8");
        response.setHeader("Pragma", "no-cache");
        response.setHeader("Expires", "Mon, 01 Jan 2001 00:00:01 GMT");
        response.setHeader("Cache-Control", "no-store");
        response.setHeader("Cache-Control", "must-revalidate");
        response.setHeader("Cache-Control", "no-cache");
        PrintWriter out = response.getWriter();

        Instalacion objInstalacion = new Instalacion(this._ip, this._puerto, this._db, usuario, clave);

        try {
            int anio = Integer.parseInt(request.getParameter("a"));
            int mes = Integer.parseInt(request.getParameter("m"));

            String html = "obj»axIns^frm»";
            if(objInstalacion.hayPreFacturasGeneradas(anio, mes)){
                ResultSet rsInstalaciones = objInstalacion.getInstalaciones(String.valueOf(id_sucursal_sesion), anio, mes);
                //html += DatosDinamicos.combo(rsInstalaciones, "id_instalacion", "", "", "", 300);
                html += "<select id='id_instalacion' name='id_instalacion' multiple='multiple' size='10' style='width:660px'>";
                try{
                    ResultSetMetaData mdata = rsInstalaciones.getMetaData();
                    int lim = mdata.getColumnCount();
                    String et="";
                    rsInstalaciones.beforeFirst();
                    while(rsInstalaciones.next()){
                        for(int i=2; i<=lim; i++){
                            et+=rsInstalaciones.getString(i)+" &nbsp;";
                        }
                        html+="<option value=\"" + rsInstalaciones.getString(1) + "\">" + et + "</option>";
                        et="";
                    }
                }catch(Exception e){
                    System.out.println(e.getMessage());
                }
                html += "</select>";
                try{
                    rsInstalaciones.close();
                }catch(Exception e){}
            }else{
                html += "<select id='id_instalacion' name='id_instalacion' style='width:390px;'></select>";
            }
            
            out.print(html);

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
