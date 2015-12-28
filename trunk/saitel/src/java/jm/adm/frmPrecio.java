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

package jm.adm;

import java.io.*;

import javax.servlet.*;
import javax.servlet.http.*;
import jm.adm.clas.ListaPrecio;
import java.sql.ResultSet;
/**
 *
 * @author Jorge
 */
public class frmPrecio extends HttpServlet {
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
        try {
            String id = request.getParameter("id");
            String lista = "";
            String utilidad = "30";

            if(id.compareTo("-1")!=0){
                ListaPrecio obj_ListaPrecio = new ListaPrecio(this._ip, this._puerto, this._db, usuario, clave);
                try{
                    ResultSet rsLista = obj_ListaPrecio.getLista(id);
                    if(rsLista.next()){
                        lista = (rsLista.getString("lista")!=null) ? rsLista.getString("lista") : "";
                        utilidad = (rsLista.getString("utilidad")!=null) ? rsLista.getString("utilidad") : "";
                        rsLista.close();
                    }
                }catch(Exception e){
                    e.printStackTrace();
                }finally{
                    obj_ListaPrecio.cerrar();
                }
            }

            String html = "obj»d_21^foc»lista^frm»";
            html += "<form action='frmPrecioGuardar' onsubmit='return adm_precioGuardar(this)' autocomplete='off'>";
            html += "<input type='hidden' id='id' name='id' value='"+id+"' /><table>";
            html += "<tr><td colspan='2' class='H3'>Formulario de Lista de Precio</td></tr>";
            html += "<tr><td>Nombre: <span class='marca'>*</span></td>";
            html += "<td><input id='lista' name='lista' type='text' size='30' maxlength='30' value='"+lista+"' onkeypress=\"_alfanumerico(event);\" onblur=\"this.value=this.value._trim();\" /></td>";
            html += "<tr><td nowrap>Utilidad: <span class='marca'>*</span></td>";
            html += "<td><input id='utilidad' name='utilidad' type='text' size='30' maxlength='30' value='"+utilidad+"' onkeypress=\"_evaluar(event, '0123456789.');\" /></td>";
            html += "<tr><td colspan='2'><input type='submit' value='Guardar' /></td></tr>";
            html += "</table></form>";
            out.print(html);

        } finally {
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
