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
import jm.cnt.clas.Banco;
import java.sql.ResultSet;
import jm.cnt.clas.PlanCuenta;
import jm.seg.clas.Rol;

/**
 *
 * @author Jorge
 */
public class frmBanco extends HttpServlet {
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
        Banco objBanco = new Banco(this._ip, this._puerto, this._db, usuario, clave);
        
        try {
            String id = request.getParameter("id");

            String id_plan_cuenta = "1";
            String banco = "";
            String num_cuenta = "";
            String num_cheque = "1";

            if(id.compareTo("-1")!=0){
                try{
                    ResultSet rsBanco = objBanco.getBanco(id);
                    if(rsBanco.next()){
                        id_plan_cuenta = (rsBanco.getString("id_plan_cuenta")!=null) ? rsBanco.getString("id_plan_cuenta") : "1";
                        banco = (rsBanco.getString("banco")!=null) ? rsBanco.getString("banco") : "";
                        num_cuenta = (rsBanco.getString("num_cuenta")!=null) ? rsBanco.getString("num_cuenta") : "";
                        num_cheque = (rsBanco.getString("num_cheque")!=null) ? rsBanco.getString("num_cheque") : "1";
                        rsBanco.close();
                    }
                    objBanco.cerrar();
                }catch(Exception e){
                    e.printStackTrace();
                }
            }

            PlanCuenta objPlanCuenta = new PlanCuenta(this._ip, this._puerto, this._db, usuario, clave);
            ResultSet tablas = objPlanCuenta.getTablasAsignacion();
            objPlanCuenta.cerrar();

            String html = "obj»d_21^foc»codigo_cuenta^frm»";
            html += "<form action='frmBancoGuardar' onsubmit='return cnt_bancoGuardar(this)' autocomplete='off'>";
            html += "<input type='hidden' id='id' name='id' value='"+id+"' />";
            html += "<table><tr><td colspan='2' class='H3'>Formulario de Cuenta Bancaria</td></tr>";
            html += "<tr><td nowrap>Tabla contable:</td>";
            html += "<td>"+DatosDinamicos.combo(tablas, "id_plan_cuenta", id_plan_cuenta, "", 270)+"</td>";
            html += "<tr><td>Entidad financiara: <span class='marca'>*</span></td>";
            html += "<td><input id='banco' name='banco' type='text' size='40' maxlength='200' value='"+banco+"' onkeypress=\"_alfanumerico(event);\" /></td></tr>";
            html += "<tr><td>N&uacute;mero de cuenta: <span class='marca'>*</span></td>";
            html += "<td><input id='num_cuenta' name='num_cuenta' type='text' size='40' maxlength='50' onkeypress=\"_evaluar(event, '0123456789-');\" value='"+num_cuenta+"'></td></tr>";
            html += "<tr><td nowrap>N&uacute;mero de cheque (inicial): </td>";
            html += "<td><input id='num_cheque' name='num_cheque' type='text' size='40' onkeypress=\"_numero(event);\" value='"+num_cheque+"'></td></tr>";
            if(id.compareTo("-1")==0 || (id.compareTo("-1")!=0 && objRol.getAcceso(id_rol, "bancoEditar"))){
                html += "<tr><td colspan='2'><input type='submit' value='Guardar' /></td></tr>";
            }
            html += "</table></form>";
            out.print(html);

            tablas.close();
        }catch(Exception e){
            e.printStackTrace();
        } finally {
            objBanco.cerrar();
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