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
import jm.adm.clas.Bodega;
import java.sql.ResultSet;
import jm.web.Fecha;

/**
 *
 * @author Jorge
 */
public class frmEstanteria extends HttpServlet {
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
            String id_bodega = request.getParameter("idB");
            String estanteria = "";
            String fila = "";
            String columna = "";
            String num_nivel = "1";

            if(id.compareTo("-1")!=0){
                Bodega objBodega = new Bodega(this._ip, this._puerto, this._db, usuario, clave);
                try{
                    ResultSet rs = objBodega.getEstanteria(id);
                    if(rs.next()){
                        estanteria = (rs.getString("estanteria")!=null) ? rs.getString("estanteria") : "";
                        fila = (rs.getString("fila")!=null) ? rs.getString("fila") : "";
                        columna = (rs.getString("columna")!=null) ? rs.getString("columna") : "";
                        num_nivel = (rs.getString("num_nivel")!=null) ? Fecha.ISOaSQL(rs.getString("num_nivel")) : "";
                        rs.close();
                    }
                }catch(Exception e){
                    e.printStackTrace();
                }finally{
                    objBodega.cerrar();
                }
            }

            String html = "obj»cmp_html^foc»estanteria^frm»";
            html += "<form action='frmEstanteriaGuardar' onsubmit='return adm_estanteriaGuardar(this)' autocomplete='off'>";
            html += "<input type='hidden' id='id_estanteria' name='id_estanteria' value='"+id+"' />";
            html += "<input type='hidden' id='id_bodega' name='id_bodega' value='"+id_bodega+"' />";

            html += "<div class='H3'>Formulario de Estanter&iacute;a</div>";

            html += "<table>";
            html += "<tr><td>Nombre de la estanter&iacute;a (percha): <span class='marca'>*</span></td>";
            html += "<td><input id='estanteria' name='estanteria' class='may' value='"+estanteria+"' type='text' size='30' maxlength='60' onkeypress=\"_alfanumerico(event);\" onblur=\"this.value=this.value._trim();\" /></td></tr>";
            html += "<tr valign='top'><td>Fila: <span class='marca'>*</span></td>";
            html += "<td><input type='text' id='fila' name='fila' size='10' maxlength='10' value='"+fila+"' onkeypress=\"_alfanumerico(event);\" /></td></tr>";
            html += "<tr><td>Columna: <span class='marca'>*</span></td>";
            html += "<td><input type='text' id='columna' name='columna' size='10' maxlength='10' value='"+columna+"' onkeypress='_alfanumerico(event);' /></td></tr>";
            html += "<tr><td>N&uacute;mero de niveles: <span class='marca'>*</span></td>";
            html += "<td><input type='text' id='num_nivel' name='num_nivel' size='10' value='"+num_nivel+"'  onkeypress='_numero(event)' /></td></tr>";

            html += "<tr><td colspan='2'><input type='submit' value='Guardar' /></td></tr>";

            html += "</table>";

            html += "</form>";

            out.print(html);

        }catch(Exception e){
            e.printStackTrace();
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
