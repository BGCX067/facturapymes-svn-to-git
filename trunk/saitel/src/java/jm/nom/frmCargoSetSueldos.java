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
import jm.nom.clas.Cargo;
import jm.web.Fecha;
/**
 *
 * @author Jorge
 */
public class frmCargoSetSueldos extends HttpServlet {
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
        
        String altB = request.getParameter("altB");
        
        Cargo obj_cargo = new Cargo(this._ip, this._puerto, this._db, usuario, clave);
        
        try {
            String html = "obj»d_21^foc»cargo^frm»";
            html += "<form action='frmCargoSetSueldosGuardar' onsubmit='return nom_cargoSetSueldosGuardar(this)' autocomplete='off'>";
           // html += "<input type='hidden' id='id' name='id' value='"+id+"' />";
            html += "<h3 class='H3'>Formulario de Incremento de sueldos</h3>";
            
            html += "<p>Fecha de inicio de ingremento de sueldo: "
                    + "<input type='text' id='fecha_ini' name='fecha_ini' size='10' value='"+Fecha.getFecha("SQL")+"' readonly />"
                    + "<input type='button' value='...' onClick=\"SelectorFecha.crear('fecha_ini', 'SQL', '');\" /></p>";
            
            html += "<table class=\"jm_tabla\" cellspacing=\"1\" cellpadding=\"3\">";
            html += "<tr><th width='150' class='jm_TH'>AREA</th>";                
            html += "<th width='180' class='jm_TH'>CARGO</th>";
            html += "<th width='70' class='jm_TH'>SUELDO</th>";
            html += "<th width='80' class='jm_TH'>%<input type='text'  style='width:40px' id='porInc' name='porInc' value='0' onkeyup=\"nom_cargoSetIncSueldos()\" /></th></tr>";
            html += "</table>";
            
            html += "<div style='overflow:auto;width:535px;height:"+altB+"px;'><table class=\"jm_tabla\" cellspacing=\"1\" cellpadding=\"3\">";
            try{
                int i=0;
                ResultSet cargos = obj_cargo.getCargosxAreas();
                while(cargos.next()){
                    String id_cargo = cargos.getString("id_cargo")!=null ? cargos.getString("id_cargo") : "0";
                    String sueldo = cargos.getString("sueldo")!=null ? cargos.getString("sueldo") : "0";
                    html += "<tr class='"+(i%2==0?"jm_filaImp":"jm_filaPar")+"'><td width='150'>"+(cargos.getString("area")!=null ? cargos.getString("area") : "")+"</td>"; 
                    html += "<td width='182'>"+(cargos.getString("cargo")!=null ? cargos.getString("cargo") : "")+"</td>"; 
                    html += "<td width='72' align='right'><input type='hidden' id='id_cargo"+i+"' name='id_cargo"+i+"' value='"+id_cargo+"' />"
                            + "<input type='hidden' id='sueldo_ant"+i+"' name='sueldo_ant"+i+"' value='"+sueldo+"' />"+sueldo+"</td>"; 
                    html += "<td width='82' align='right'><input type='text' style='width:40px' id='sueldo"+i+"' name='sueldo"+i+"' value='"+sueldo+"' readonly /></td></tr>"; 
                    i++;
                }
                cargos.close();
            }catch(Exception e){
                e.printStackTrace();
            }
            
            html += "</table></div>";
            
            html += "<div><input type='submit' value='Guardar' /></div>";
            html += "</form>";
            out.print(html);
            
        } finally { 
            obj_cargo.cerrar();
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
