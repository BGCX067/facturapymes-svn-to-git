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
import jm.cnt.clas.PlanCuenta;
import jm.adm.clas.Configuracion;
import jm.adm.clas.Sucursal;
import jm.inf.clas.Material;
import jm.seg.clas.Rol;
import jm.web.DatosDinamicos;

/**
 *
 * @author Jorge
 */
public class frmMaterial extends HttpServlet {
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
        int id_sucursal_sesion = (Integer)sesion.getAttribute("sucursal");
        String usuario = (String)sesion.getAttribute("usuario");
        String clave = (String)sesion.getAttribute("clave");

        response.setContentType("text/html;charset=UTF-8");
        response.setHeader("Pragma", "no-cache");
        response.setHeader("Expires", "Mon, 01 Jan 2001 00:00:01 GMT");
        response.setHeader("Cache-Control", "no-store");
        response.setHeader("Cache-Control", "must-revalidate");
        response.setHeader("Cache-Control", "no-cache");
        PrintWriter out = response.getWriter();

        int altB = Integer.parseInt(request.getParameter("altB"));

        Configuracion conf = new Configuracion(this._ip, this._puerto, this._db, usuario, clave);
        String id_sucursal_matriz = conf.getValor("id_sucursal_matriz");
        conf.cerrar();

        Rol objRol = new Rol(this._ip, this._puerto, this._db, usuario, clave);

        Sucursal obj_Sucursal = new Sucursal(this._ip, this._puerto, this._db, usuario, clave);
        ResultSet rsSucursales = obj_Sucursal.getSucursales();


        PlanCuenta objPlanCuenta = new PlanCuenta(this._ip, this._puerto, this._db, usuario, clave);
        ResultSet cuentas = objPlanCuenta.getTablas("5");

        Material objMaterial = new Material(this._ip, this._puerto, this._db, usuario, clave);

        try {
            String id = request.getParameter("id");
            String id_sucursal = String.valueOf(id_sucursal_sesion);
            String id_producto = "";
            String id_plan_cuenta_gasto = "";
            String descripcion = "";

            if(id.compareTo("-1")!=0){
                try{
                    ResultSet rsMaterial = objMaterial.getMaterial(id);
                    if(rsMaterial.next()){
                        id_sucursal = (rsMaterial.getString("id_sucursal")!=null) ? rsMaterial.getString("id_sucursal") : "";
                        id_producto = (rsMaterial.getString("id_producto")!=null) ? rsMaterial.getString("id_producto") : "";
                        id_plan_cuenta_gasto = (rsMaterial.getString("id_plan_cuenta_gasto")!=null) ? rsMaterial.getString("id_plan_cuenta_gasto") : "";
                        descripcion = (rsMaterial.getString("descripcion")!=null) ? rsMaterial.getString("descripcion") : "";
                        rsMaterial.close();
                    }
                }catch(Exception e){
                    e.printStackTrace();
                }
            }

            String html = "obj»d_21^foc»txtBus^frm»";
            html += "<form action='frmMaterialGuardar' onsubmit='return fac_materialGuardar(this)' autocomplete='off'>";
            html += "<input type='hidden' id='id' name='id' value='"+id+"' />"
                    + "<input type='hidden' id='id_producto' name='id_producto' value='"+id_producto+"' />";
            
            html += "<table><tr><td colspan='2' class='H3'>Formulario de Material</td></tr>";
            if(Integer.valueOf(id_sucursal_matriz) == id_sucursal_sesion){
                html += "<tr><td>Sucursal: <span class='marca'>*</span></td>";
                html += "<td>" + DatosDinamicos.combo(rsSucursales, "id_sucursal", id_sucursal, "", "", 250) + "</td></tr>";
            }else{
                html += "<tr><td colspan='2'><input type='hidden' id='id_sucursal' name='id_sucursal' value='"+id_sucursal_sesion+"' /></td>";
            }
            html += "<tr><td>Producto: <span id='axMat' class='marca'>*</span> </td>";
            html += "<td><input id='txtBus' name='txtBus' type='text' value='"+descripcion+"' style='width:245px' onfocus=\"this.select();\" onkeyup=\"fac_getMateriales('fac_putProductos')\" /></td></tr>";
            html += "<tr><td>Cuenta contable de gasto: <span class='marca'>*</span></td>";
            html += "<td>" + DatosDinamicos.combo(cuentas, "id_plan_cuenta_gasto", id_plan_cuenta_gasto, "", "", 250) + "</td></tr>";

            html += "<tr><td colspan='2'><input type='submit' value='Guardar' /></td></tr>";

            html += "</table></form>";
            out.print(html);

        } finally {
            objPlanCuenta.cerrar();
            obj_Sucursal.cerrar();
            objMaterial.cerrar();
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
