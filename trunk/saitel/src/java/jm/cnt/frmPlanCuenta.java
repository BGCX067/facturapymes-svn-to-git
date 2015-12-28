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

package jm.cnt;

import java.io.*;

import javax.servlet.*;
import javax.servlet.http.*;
import jm.web.DatosDinamicos;
import jm.cnt.clas.PlanCuenta;
import java.sql.ResultSet;
import jm.seg.clas.Rol;

/**
 *
 * @author Jorge
 */
public class frmPlanCuenta extends HttpServlet {
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
        PlanCuenta objPlanCuenta = new PlanCuenta(this._ip, this._puerto, this._db, usuario, clave);

        try {
            String id = request.getParameter("id");
            String id_padre = "1";
            String codigo_cuenta = "";
            String nombre_cuenta = "";
            String tipo_cuenta = "-1";
            String campo_form_103 = "";
            String campo_form_104 = "";
            String [][] tipo_cuenta1 = {{"-1","Agrupaci&oacute;n"},{"0","Activo"},{"1","Pasivo"}};
            
            ResultSet tablas = objPlanCuenta.getTablas();
            if(id.compareTo("-1")!=0){
                try{
                    ResultSet tabla = objPlanCuenta.getTabla(id);
                    if(tabla.next()){
                        id_padre = (tabla.getString("id_padre")!=null) ? tabla.getString("id_padre") : "1";
                        codigo_cuenta = (tabla.getString("codigo_cuenta")!=null) ? tabla.getString("codigo_cuenta") : "";
                        nombre_cuenta = (tabla.getString("nombre_cuenta")!=null) ? tabla.getString("nombre_cuenta") : "";
                        tipo_cuenta = (tabla.getString("tipo_cuenta")!=null) ? tabla.getString("tipo_cuenta") : "-1";
                        campo_form_103 = (tabla.getString("campo_form_103")!=null) ? tabla.getString("campo_form_103") : "";
                        campo_form_104 = (tabla.getString("campo_form_104")!=null) ? tabla.getString("campo_form_104") : "";
                        tabla.close();
                    }
                }catch(Exception e){
                    e.printStackTrace();
                }
            }

            String html = "obj»d_21^foc»codigo_cuenta^frm»";
            html += "<form action='frmPlanCuentaGuardar' onsubmit='return cnt_planCuentaGuardar(this)' autocomplete='off'>";
            html += "<input type='hidden' id='id' name='id' value='"+id+"' />";
            html += "<table><tr><td colspan='2' class='H3'>Formulario de Plan de Cuentas</td></tr>";
            html += "<tr><td nowrap>Cuenta de agrupaci&oacute;n:</td>";
            html += "<td>"+DatosDinamicos.combo(tablas, "id_padre", id_padre, "cnt_setCodigo()", 270)+"</td></tr>";
            html += "<tr><td>C&oacute;digo: <span class='marca'>*</span></td>";
            html += "<td><input id='codigo_cuenta' name='codigo_cuenta' type='text' size='40' maxlength='50' onkeypress=\"_evaluar(event, '0123456789.');\" value='"+codigo_cuenta+"' /></td></tr>";
            html += "<tr><td>Nombre: <span class='marca'>*</span></td>";
            html += "<td><input id='nombre_cuenta' name='nombre_cuenta' type='text' size='40' maxlength='200' value='"+nombre_cuenta+"' onkeypress=\"_alfanumerico(event);\" onblur=\"this.value=this.value._trim();\" /></td></tr>";
            html += "<tr><td>Tipo:</td>";
            html += "<td>"+DatosDinamicos.combo("tipo_cuenta", tipo_cuenta, tipo_cuenta1, "")+"</td></tr>";
            html += "<tr><td nowrap>Campo formulario 103: </td>";
            html += "<td><input id='campo_form_103' name='campo_form_103' type='text' size='4' maxlength='4' onkeypress=\"_numero(event);\" value='"+campo_form_103+"' /></td></tr>";
            html += "<tr><td>Campo formulario 104: </td>";
            html += "<td><input id='campo_form_104' name='campo_form_104' type='text' size='4' maxlength='4' onkeypress=\"_numero(event);\" value='"+campo_form_104+"' /></td></tr>";
            if(id.compareTo("-1")==0 || (id.compareTo("-1")!=0 && objRol.getAcceso(id_rol, "planCuentaEditar"))){
                html += "<tr><td colspan='2'><input type='submit' value='Guardar' /></td></tr>";
            }
            html += "</table></form>";
            out.println(html);

            tablas.close();
            objPlanCuenta.cerrar();
        }catch(Exception e){
            e.printStackTrace();
        } finally {
            objPlanCuenta.cerrar();
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