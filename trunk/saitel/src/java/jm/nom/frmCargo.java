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

package jm.nom;

import java.io.*;

import javax.servlet.*;
import javax.servlet.http.*;
import java.sql.ResultSet;
import jm.nom.clas.Area;
import jm.nom.clas.Cargo;
import jm.web.DatosDinamicos;
/**
 *
 * @author Jorge
 */
public class frmCargo extends HttpServlet {
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
        
        Area obj_area = new Area(this._ip, this._puerto, this._db, usuario, clave);
        
        try {
            String id = request.getParameter("id");
            String cargo = "";
            String id_area = "";
            String sueldo = "0";
            
            if(id.compareTo("-1")!=0){
                Cargo obj_cargo = new Cargo(this._ip, this._puerto, this._db, usuario, clave);
                try{
                    ResultSet grupo = obj_cargo.getCargo(id);
                    if(grupo.next()){
                        cargo = (grupo.getString("cargo")!=null) ? grupo.getString("cargo") : "";
                        id_area = (grupo.getString("id_area")!=null) ? grupo.getString("id_area") : "";
                        sueldo = (grupo.getString("sueldo")!=null) ? grupo.getString("sueldo") : "0";
                        grupo.close();
                    }
                }catch(Exception e){
                    e.printStackTrace();
                }finally{
                    obj_cargo.cerrar();
                }
            }
            
            ResultSet rsAreas = obj_area.getAreas();
            
            String html = "obj»d_21^foc»cargo^frm»";
            html += "<form action='frmCargoGuardar' onsubmit='return nom_cargoGuardar(this)' autocomplete='off'>";
            html += "<input type='hidden' id='id' name='id' value='"+id+"' />";
            html += "<table><tr><td colspan='2' class='H3'>Formulario de Cargo</td></tr>";
            html += "<tr><td nowrap>Area: <span class='marca'>*</span></td>";                
            html += "<td>"+DatosDinamicos.combo(rsAreas, "id_area", id_area, "")+"</td>";
            html += "<tr><td nowrap>Cargo: <span class='marca'>*</span></td>";                
            html += "<td><input id='cargo' name='cargo' type='text' size='50' maxlength='60' value='"+cargo+"' onkeypress=\"_evaluar(event, 'abcdefghijklmnñopqrstuvwxyzABCDEFGHIJKLMNÑOPQRSTUVWXYZ_');\"  onblur=\"this.value=this.value._trim();\" /></td>";
            
            html += "<tr><td nowrap>Sueldo: <span class='marca'>*</span></td>";                
            html += "<td><input id='sueldo' name='sueldo' type='text' size='10' maxlength='10' value='"+sueldo+"' onkeypress=\"_evaluar(event, '0123456789.');\" /></td>";
            
            html += "<tr><td colspan='2'><input type='submit' value='Guardar' /></td></tr>";
            html += "</table></form>";
            out.print(html);
            
        } finally { 
            obj_area.cerrar();
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
