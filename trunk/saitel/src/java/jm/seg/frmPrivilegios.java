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

import java.io.*;

import javax.servlet.*;
import javax.servlet.http.*;
import jm.seg.clas.Rol;
import java.sql.ResultSet;

/**
 *
 * @author Jorge
 */
public class frmPrivilegios extends HttpServlet {
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
        
        String id = request.getParameter("id");
        String alto = request.getParameter("altB");
        int _altBody = Integer.parseInt(alto) - 250;
        
        Rol objRol = new Rol(this._ip, this._puerto, this._db, usuario, clave);
        
        String r = "obj»d_21^frm»";
        try {
            ResultSet rsRol = objRol.getRol(id);
            ResultSet rsMenus = objRol.getMenus(id);
            ResultSet rsPrivilegios = objRol.getPrivilegios(id);
            String modulo = "";
            String aux = "";
            if(rsRol.next()){
                r += "<div class='H3'>Gesti&oacute;n de Privilegios para el Rol: " + ((rsRol.getString("rol")!=null) ? rsRol.getString("rol") : "") + "</div>";
                r += "<table border='0' cellspacing='0' cellpadding='0'>";
                r += "<tr><th>Lista de privilegios </th>";
                r += "<td>&nbsp;</td>";
                r += "<th>Privilegios Concedidos</th></tr>";
                r += "<tr valign='top' class='jm_filaPar'><td><div class='columna' style='overflow:auto;width:280px;height:"+ _altBody +"px;'><table width='100%'>";
                int i=0;
                while(rsMenus.next()){
                    modulo = (rsMenus.getString("txt_modulo")!=null) ? rsMenus.getString("txt_modulo") : "";
                    if(modulo.compareTo(aux)!=0){
                        r += "<tr><td class='jm_TH jm_filaSobre' colspan='2'>"+modulo+"</td></tr>";
                        aux = modulo;
                    }
                    r += "<tr valign='top'><td><input id='ch_p" + i + "' type='checkbox'><input id='id_p" + i + "' type='hidden' value='"+ ((rsMenus.getString("id_pagina")!=null) ? rsMenus.getString("id_pagina") : "") + "'></td>";
                    r += "<td style='text-align:justify'>" + ((rsMenus.getString("descripcion")!=null) ? rsMenus.getString("descripcion") : "") + "</td></tr>";
                    i++;
                } 
                r += "</table></div></td>"; 
                r += "<td width='65' valign='middle'>"; 
                r += "<p> &nbsp;&nbsp; <input type='button' value='Conceder' onclick=\"seg_setPrivilegio(1);\" /></p>";
                r += "<p> &nbsp;&nbsp; <input type='button' value='&nbsp;Revocar&nbsp;' onclick=\"seg_setPrivilegio(0);\" /></p>";
                r += "</td><td>";        
                r += "<div class='columna' style='overflow:auto;width:280px;height:"+ _altBody +"px;'><table width='100%'>";
                i=0;
                while(rsPrivilegios.next()){
                    modulo = (rsPrivilegios.getString("txt_modulo")!=null) ? rsPrivilegios.getString("txt_modulo") : "";
                    if(modulo.compareTo(aux)!=0){
                        r += "<tr><td class='jm_TH jm_filaSobre' colspan='2'>"+modulo+"</td></tr>";
                        aux = modulo;
                    }
                    r += "<tr valign='top'><td><input id='ch_p_as" + i + "' type='checkbox'><input id='id_p_as" + i + "' type='hidden' value='"+ ((rsPrivilegios.getString("id_pagina")!=null) ? rsPrivilegios.getString("id_pagina") : "") + "'></td>";
                    r += "<td style='text-align:justify'>" + ((rsPrivilegios.getString("descripcion")!=null) ? rsPrivilegios.getString("descripcion") : "") + "</td></tr>";
                    i++;
                } 
                r += "</table></div></td></tr></table>";
                r += "<input id='idR' type='hidden' value='" + id + "'>";
                rsRol.close();
            }
            out.print(r);
            
            rsMenus.close();
            rsPrivilegios.close();
            
        }catch(Exception e){
            e.printStackTrace();
        } finally { 
            objRol.cerrar();
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
