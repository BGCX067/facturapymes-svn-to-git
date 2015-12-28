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
import jm.adm.clas.Iva;
import java.sql.ResultSet;
import jm.cnt.clas.PlanCuenta;
import jm.web.DatosDinamicos;
/**
 *
 * @author Jorge
 */
public class frmIva extends HttpServlet {
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
            String codigo = "";
            String concepto = "";
            String porcentaje = "";
            String id_plan_cuenta_comp_bien = "";
            String id_plan_cuenta_comp_servicio = "";
            String id_plan_cuenta_venta_bien = "";
            String id_plan_cuenta_venta_servicio = "";
            String id_plan_cuenta_venta_activo = "";
            if(id.compareTo("-1")!=0){
                Iva obj_Iva = new Iva(this._ip, this._puerto, this._db, usuario, clave);
                try{
                    ResultSet rsIva = obj_Iva.getIva(id);
                    if(rsIva.next()){
                        codigo = (rsIva.getString("codigo")!=null) ? rsIva.getString("codigo") : "";
                        concepto = (rsIva.getString("concepto")!=null) ? rsIva.getString("concepto") : "";
                        porcentaje = (rsIva.getString("porcentaje")!=null) ? rsIva.getString("porcentaje") : "";
                        id_plan_cuenta_comp_bien = (rsIva.getString("id_plan_cuenta_comp_bien")!=null) ? rsIva.getString("id_plan_cuenta_comp_bien") : "";
                        id_plan_cuenta_comp_servicio = (rsIva.getString("id_plan_cuenta_comp_servicio")!=null) ? rsIva.getString("id_plan_cuenta_comp_servicio") : "";
                        id_plan_cuenta_venta_bien = (rsIva.getString("id_plan_cuenta_venta_bien")!=null) ? rsIva.getString("id_plan_cuenta_venta_bien") : "";
                        id_plan_cuenta_venta_servicio = (rsIva.getString("id_plan_cuenta_venta_servicio")!=null) ? rsIva.getString("id_plan_cuenta_venta_servicio") : "";
                        id_plan_cuenta_venta_activo = (rsIva.getString("id_plan_cuenta_venta_activo")!=null) ? rsIva.getString("id_plan_cuenta_venta_activo") : "";
                        rsIva.close();
                    }
                }catch(Exception e){
                    e.printStackTrace();
                }finally{
                    obj_Iva.cerrar();
                }
            }
            
            PlanCuenta objPlanCuenta = new PlanCuenta(this._ip, this._puerto, this._db, usuario, clave);
            ResultSet rsCuentas = objPlanCuenta.getTablasAsignacion();
            objPlanCuenta.cerrar();

            String html = "obj»d_21^foc»codigo^frm»";
            html += "<form action='frmIvaGuardar' onsubmit='return adm_ivaGuardar(this)' autocomplete='off'>";
            html += "<input type='hidden' id='id' name='id' value='"+id+"' /><table>";
            html += "<tr><td colspan='2' class='H3'>Formulario de IVA</td></tr>";
            html += "<tr><td>C&oacute;digo SRI: <span class='marca'>*</span></td>";
            html += "<td><input id='codigo' name='codigo' type='text' size='30' maxlength='1' value='"+codigo+"' onkeypress=\"_numero(event);\" /></td>";
            html += "<tr><td nowrap>Concepto: <span class='marca'>*</span></td>";
            html += "<td><input id='concepto' name='concepto' type='text' size='30' maxlength='30' value='"+concepto+"' onblur=\"this.value=this.value._trim();\" /></td>";
            html += "<tr><td nowrap>% del impuesto gravado: <span class='marca'>*</span></td>";
            html += "<td><input id='porcentaje' name='porcentaje' type='text' size='30' maxlength='3' value='"+porcentaje+"' onkeypress=\"_numero(event);\" /></td>";
            html += "<tr><td nowrap>Compra de bienes:</td>" +
                    "<td>" + DatosDinamicos.combo(rsCuentas, "id_plan_cuenta_comp_bien", id_plan_cuenta_comp_bien, "", " SIN ASIGNACION ", 250) + "</td></tr>";
            html += "<tr><td nowrap>Compra de servicios:</td>" +
                    "<td>" + DatosDinamicos.combo(rsCuentas, "id_plan_cuenta_comp_servicio", id_plan_cuenta_comp_servicio, "", " SIN ASIGNACION ", 250) + "</td></tr>";
            html += "<tr><td nowrap>Venta de bienes:</td>" +
                    "<td>" + DatosDinamicos.combo(rsCuentas, "id_plan_cuenta_venta_bien", id_plan_cuenta_venta_bien, "", " SIN ASIGNACION ", 250) + "</td></tr>";
            html += "<tr><td nowrap>Ventas de servicios:</td>" +
                    "<td>" + DatosDinamicos.combo(rsCuentas, "id_plan_cuenta_venta_servicio", id_plan_cuenta_venta_servicio, "", " SIN ASIGNACION ", 250) + "</td></tr>";
            html += "<tr><td nowrap>Ventas de activos:</td>" +
                    "<td>" + DatosDinamicos.combo(rsCuentas, "id_plan_cuenta_venta_activo", id_plan_cuenta_venta_activo, "", " SIN ASIGNACION ", 250) + "</td></tr>";
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
