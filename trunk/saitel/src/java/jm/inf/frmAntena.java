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
import java.sql.ResultSet;
import jm.inf.clas.Antena;
import jm.inf.clas.Nodo;
import jm.seg.clas.Rol;
import jm.web.DatosDinamicos;

/**
 *
 * @author Jorge
 */
public class frmAntena extends HttpServlet {
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
        String id_rol = (String)sesion.getAttribute("id_rol");
        String usuario = (String)sesion.getAttribute("usuario");
        String clave = (String)sesion.getAttribute("clave");

        response.setContentType("text/html;charset=UTF-8");
        response.setHeader("Pragma", "no-cache");
        response.setHeader("Expires", "Mon, 01 Jan 2001 00:00:01 GMT");
        response.setHeader("Cache-Control", "no-store");
        response.setHeader("Cache-Control", "must-revalidate");
        response.setHeader("Cache-Control", "no-cache");
        PrintWriter out = response.getWriter();

        Rol objRol = new Rol(this._ip, this._puerto, this._db, usuario, clave);

        /*Sucursal obj_Sucursal = new Sucursal(this._ip, this._puerto, this._db, usuario, clave);
        ResultSet rsSucursales = obj_Sucursal.getSucursales();*/
        Nodo objNodo = new Nodo(this._ip, this._puerto, this._db, usuario, clave);
        ResultSet rsNodos = objNodo.getNodos();

        Antena objAntena = new Antena(this._ip, this._puerto, this._db, usuario, clave);

        try {
            String id = request.getParameter("id");
            //String id_sucursal = String.valueOf(id_sucursal_sesion);
            String id_nodo = "";
            String antena_acoplada = "";

            if(id.compareTo("-1")!=0){
                try{
                    ResultSet rsAntena = objAntena.getAntena(id);
                    if(rsAntena.next()){
                        //id_sucursal = (rsAntena.getString("id_sucursal")!=null) ? rsAntena.getString("id_sucursal") : "";
                        id_nodo = (rsAntena.getString("id_nodo")!=null) ? rsAntena.getString("id_nodo") : "";
                        antena_acoplada = (rsAntena.getString("antena_acoplada")!=null) ? rsAntena.getString("antena_acoplada") : "";
                        rsAntena.close();
                    }
                }catch(Exception e){
                    e.printStackTrace();
                }
            }


            String html = "obj»d_21^foc»antena_acoplada^frm»";
            html += "<form action='frmAntenaGuardar' onsubmit='return inf_antenaGuardar(this)' autocomplete='off'>";
            html += "<input type='hidden' id='id' name='id' value='"+id+"' /><table>";
            html += "<tr><td colspan='2' class='H3'>Formulario de Antena Acoplada</td></tr>";
            /*html += "<tr><td>Sucursal: <span class='marca'>*</span></td>";
            html += "<td>" + DatosDinamicos.combo(rsSucursales, "id_sucursal", id_sucursal, "", "", 250) + "</td></tr>";*/
            html += "<tr><td>Nodo: <span class='marca'>*</span></td>";
            html += "<td>" + DatosDinamicos.combo(rsNodos, "id_nodo", id_nodo, "", "", 250) + "</td></tr>";
            html += "<tr><td>Antena acoplada: <span class='marca'>*</span></td>";
            html += "<td><input id='antena_acoplada' name='antena_acoplada' type='text' size='30' maxlength='80' value='"+antena_acoplada+"' onkeypress=\"_alfanumerico(event);\" onblur=\"this.value=this.value._trim();\" /></td>";
            
            if(id.compareTo("-1")==0 || (id.compareTo("-1")!=0 && objRol.getAcceso(id_rol, "antenaEditar"))){
                html += "<tr><td colspan='2'><input type='submit' value='Guardar' /></td></tr>";
            }

            html += "</table></form>";
            out.print(html);

        } finally {
            objAntena.cerrar();
            objNodo.cerrar();
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
