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
import java.sql.ResultSet;

import javax.servlet.*;
import javax.servlet.http.*;
import jm.fac.clas.Instalacion;

/**
 *
 * @author Jorge
 */
public class getTblInstalaciones extends HttpServlet {
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

        String fun = request.getParameter("fun");
        String txt = request.getParameter("txt");
        Instalacion objInstalacion = new Instalacion(this._ip, this._puerto, this._db, usuario, clave);
        try {
            String where = "(ip::varchar like '"+txt+"%' or lower(ruc) like '"+txt.toLowerCase()+"%' or lower(razon_social) like '%"+txt.toLowerCase()+"%') and estado_servicio in('a','c','s')";
            ResultSet rs = objInstalacion.getInstalaciones(id_sucursal_sesion, where);
            String html = "<div style='position:absolute;z-index:20' id='ax'><table cellpadding='0' cellspacing='0'><tr><TH class='jm_TH' width='90'>CEDULA</TH><TH class='jm_TH' width='200'>CLIENTE</TH><TH class='jm_TH' width='100'>IP</TH></tr></table>";
            html += "<div id='tblFil' style='overflow:auto;width:420px;height:130px;background-color:#91C3DE;border: 1px solid #CCCCCC'>";

            try{
                String fi = "";
                int i=0;
                html += "<TABLE class='jm_tabla' cellspacing='1' cellpadding='2'>";
                while(rs.next()){
                    fi = (i%2!=0) ? "jm_filaPar" : "jm_filaImp";
                    html += "<tr class='"+fi+"' style='cursor:pointer;' title='Insertar en formulario' onclick=\""+fun+"("+i+")\">"
                            + "<td width='88'><span id='ax_ci"+i+"'>"+(rs.getString("ruc")!=null ? rs.getString("ruc") : "")+"</span>"
                            + "<span id='ax_idIns"+i+"' style='display:none'>"+(rs.getString("id_instalacion")!=null ? rs.getString("id_instalacion") : "0")+"</span></td>"
                            + "<td width='198' id='ax_cl"+i+"'>"+(rs.getString("razon_social")!=null ? rs.getString("razon_social") : "")+"</td>"
                            + "<td width='98' id='ax_ip"+i+"'>"+(rs.getString("ip")!=null ? rs.getString("ip") : "")+"</td></tr>";
                    i++;
                }
                html += "</table>";
                rs.close();
            }catch(Exception e){
                e.printStackTrace();
            }
            html += "</div></div>";
            out.print("obj»axTb^frm»"+html);
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
