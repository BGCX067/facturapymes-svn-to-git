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
import jm.adm.clas.PuntoEmision;
import java.sql.ResultSet;
import jm.cnt.clas.PlanCuenta;
import jm.seg.clas.Usuario;
import jm.web.DatosDinamicos;
import jm.web.Fecha;

/**
 *
 * @author Jorge
 */
public class frmPuntoEmision extends HttpServlet {
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
            String idSuc = request.getParameter("idSuc");
            int id_sucursal = Integer.parseInt(idSuc);
            String punto_emision = "";
            String usuario_caja = "";
            String id_plan_cuenta_caja = "";
            String id_plan_cuenta_diferencia_caja = "";
            String fac_num_serie = "001-001";
            String fac_sec_desde = "";
            String fac_sec_hasta = "";
            String fac_cad_facturero = Fecha.getFecha("SQL");
            String fac_autorizacion = "";
            String num_fact_inicial = "0";
            //boolean estado = true;

            if(id.compareTo("-1")!=0){
                PuntoEmision objPuntoEmision = new PuntoEmision(this._ip, this._puerto, this._db, usuario, clave);
                try{
                    ResultSet rs = objPuntoEmision.getPuntoEmision(id);
                    if(rs.next()){
                        //id_sucursal = (rs.getString("id_sucursal")!=null) ? rs.getInt("id_sucursal") : 1;
                        punto_emision = (rs.getString("punto_emision")!=null) ? rs.getString("punto_emision") : "";
                        usuario_caja = (rs.getString("usuario_caja")!=null) ? rs.getString("usuario_caja") : "";
                        id_plan_cuenta_caja = (rs.getString("id_plan_cuenta_caja")!=null) ? rs.getString("id_plan_cuenta_caja") : "";
                        id_plan_cuenta_diferencia_caja = (rs.getString("id_plan_cuenta_diferencia_caja")!=null) ? rs.getString("id_plan_cuenta_diferencia_caja") : "";
                        fac_num_serie = (rs.getString("fac_num_serie")!=null) ? rs.getString("fac_num_serie") : "";
                        fac_sec_desde = (rs.getString("fac_sec_desde")!=null) ? rs.getString("fac_sec_desde") : "";
                        fac_sec_hasta = (rs.getString("fac_sec_hasta")!=null) ? rs.getString("fac_sec_hasta") : "";
                        fac_cad_facturero = (rs.getString("fac_cad_facturero")!=null) ? Fecha.ISOaSQL(rs.getString("fac_cad_facturero")) : "";
                        fac_autorizacion = (rs.getString("fac_autorizacion")!=null) ? rs.getString("fac_autorizacion") : "";
                        num_fact_inicial = (rs.getString("num_fact_inicial")!=null) ? rs.getString("num_fact_inicial") : "0";
                        //estado = (rs.getString("estado")!=null) ? rs.getBoolean("estado") : true;
                        rs.close();
                    }
                }catch(Exception e){
                    e.printStackTrace();
                }finally{
                    objPuntoEmision.cerrar();
                }
            }

            Usuario objUsuario = new Usuario(this._ip, this._puerto, this._db, usuario, clave);
            ResultSet rsUsuarios = objUsuario.getUsuarios();
            objUsuario.cerrar();

            PlanCuenta objPlanCuenta = new PlanCuenta(this._ip, this._puerto, this._db, usuario, clave);
            ResultSet rsCuentas = objPlanCuenta.getTablasAsignacion();
            objPlanCuenta.cerrar();

            String html = "obj»cmp_html^foc»punto_emision^frm»";
            html += "<form action='frmPuntoEmisionGuardar' onsubmit='return adm_puntoEmisionGuardar(this)' autocomplete='off'>";
            html += "<input type='hidden' id='id_punto_emision' name='id_punto_emision' value='"+id+"' />";
            html += "<input type='hidden' id='idSucursal' name='idSucursal' value='"+id_sucursal+"' />";

            html += "<div class='H3'>Formulario de Punto de Emisi&oacute;n</div>";

            html += "<table>";
            html += "<tr><td>Nombre del punto de emisi&oacute;n: <span class='marca'>*</span></td>";
            html += "<td><input id='punto_emision' name='punto_emision' class='may' value='"+punto_emision+"' type='text' size='30' maxlength='60' onkeypress=\"_alfanumerico(event);\" onblur=\"this.value=this.value._trim();\" /></td></tr>";
            html += "<tr valign='top'><td>Usuario de la caja: <span class='marca'>*</span></td>";
            html += "<td>" + DatosDinamicos.combo(rsUsuarios, "usuario_caja", usuario_caja, "", " SIN USUARIO ", 335) + " </td></tr>";
            html += "<tr><td>Cuenta de caja: <span class='marca'>*</span></td>";
            html += "<td>" + DatosDinamicos.combo(rsCuentas, "id_plan_cuenta_caja", id_plan_cuenta_caja, "", " SIN ASIGNACION ", 335) + "</td></tr>";
            html += "<tr><td>Cuenta de diferencias de caja: <span class='marca'>*</span></td>";
            html += "<td>" + DatosDinamicos.combo(rsCuentas, "id_plan_cuenta_diferencia_caja", id_plan_cuenta_diferencia_caja, "", " SIN ASIGNACION ", 335) + "</td></tr>";
            html += "<tr><td>N&uacute;mero de serie del facturero: <span class='marca'>*</span></td>";
            html += "<td><input type='text' id='fac_num_serie' name='fac_num_serie' size='10' maxlength='7' value='"+fac_num_serie+"' onkeypress=\"_evaluar(event, '0123456789-');\" /></td></tr>";
            html += "<tr><td>Secuencial desde: <span class='marca'>*</span></td>";
            html += "<td><input type='text' id='fac_sec_desde' name='fac_sec_desde' size='10' maxlength='10' value='"+fac_sec_desde+"' onkeypress='_numero(event);' />"
                    + " hasta: <span class='marca'>*</span> <input type='text' id='fac_sec_hasta' name='fac_sec_hasta' size='10' maxlength='10' value='"+fac_sec_hasta+"' onkeypress='_numero(event);' /></td></tr>";
            html += "<tr><td>Fecha de caducidad del facturero: <span class='marca'>*</span></td>";
            html += "<td><input type='text' id='fac_cad_facturero' name='fac_cad_facturero' size='10' value='"+fac_cad_facturero+"' readonly /><input type='button' value='...' onClick=\"SelectorFecha.crear('fac_cad_facturero', 'SQL');\" /> </td></tr>";
            html += "<tr><td>No. autorizaci&oacute;n del facturero: <span class='marca'>*</span></td>";
            html += "<td><input type='text' id='fac_autorizacion' name='fac_autorizacion' size='10' maxlength='10' value='"+fac_autorizacion+"' onkeypress='_numero(event);' /></td></tr>";
            html += "<tr><td>N&uacute;mero de factura emitido actual inicial: <span class='marca'>*</span></td>";
            html += "<td><input type='text' id='num_fact_inicial' name='num_fact_inicial' size='10' maxlength='7' value='"+num_fact_inicial+"' onkeypress=\"_numero(event);\" /></td></tr>";
            /*html += "<tr><td>Estado: <span class='marca'>*</span></td>";
            html += "<td><input type='radio' id='estado0' name='estado' value='true' "+((estado) ? "checked" : "")+" /> Activo &nbsp;&nbsp;&nbsp;&nbsp; " +
                    "<input type='radio' id='estado1' name='estado' value='false' "+((!estado) ? "checked" : "")+" /> Inactivo</td></tr>";*/
            
            html += "<tr><td colspan='2'><input type='submit' value='Guardar' />";
            if(id.compareTo("-1")!=0){
                html += " &nbsp;&nbsp;&nbsp;&nbsp; <input type='button' value='Formato de facturas' onclick=\"window.open('formatoFactura?id="+id+"','_blank','top=5,left=5,width=750');\" />";
            }
            html += "</td></tr>";

            html += "</table>";

            html += "</div>";

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
