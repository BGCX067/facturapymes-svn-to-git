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

package jm.fac;

import java.io.*;

import javax.servlet.*;
import javax.servlet.http.*;
import jm.fac.clas.Sector;
import java.sql.ResultSet;
import jm.adm.clas.Configuracion;
import jm.adm.clas.Sucursal;
import jm.fac.clas.PlanServicio;
import jm.inv.clas.Producto;
import jm.seg.clas.Rol;
import jm.web.DatosDinamicos;

/**
 *
 * @author Jorge
 */
public class frmSector extends HttpServlet {
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

        Sector obj_Sector = new Sector(this._ip, this._puerto, this._db, usuario, clave);

        Producto obj_Producto = new Producto(this._ip, this._puerto, this._db, usuario, clave);
        ResultSet rsProductos = obj_Producto.getProductos();

        /*PlanCuenta objPlanCuenta = new PlanCuenta(this._ip, this._puerto, this._db, usuario, clave);
        ResultSet cuentas = objPlanCuenta.getTablasAsignacion();*/

        PlanServicio objPlanServicio = new PlanServicio(this._ip, this._puerto, this._db, usuario, clave);

        try {
            String id = request.getParameter("id");
            String id_sucursal = String.valueOf(id_sucursal_sesion);
            String sector = "";
            String costo_instalacion = "0";
            String costo_instalacion_fibra = "0";
            //String costo_reinstalacion = "0";
            String id_producto = "";
            //String id_plan_cuenta = "";

            if(id.compareTo("-1")!=0){
                try{
                    ResultSet rsSector = obj_Sector.getSector(id);
                    if(rsSector.next()){
                        id_sucursal = (rsSector.getString("id_sucursal")!=null) ? rsSector.getString("id_sucursal") : "";
                        sector = (rsSector.getString("sector")!=null) ? rsSector.getString("sector") : "";
                        costo_instalacion = (rsSector.getString("costo_instalacion")!=null) ? rsSector.getString("costo_instalacion") : "0";
                        costo_instalacion_fibra = (rsSector.getString("costo_instalacion_fibra")!=null) ? rsSector.getString("costo_instalacion_fibra") : "0";
                        //costo_reinstalacion = (rsSector.getString("costo_instalacion")!=null) ? rsSector.getString("costo_instalacion") : "0";
                        id_producto = (rsSector.getString("id_producto")!=null) ? rsSector.getString("id_producto") : "";
                        //id_plan_cuenta = (rsSector.getString("id_plan_cuenta")!=null) ? rsSector.getString("id_plan_cuenta") : "";
                        rsSector.close();
                    }
                }catch(Exception e){
                    e.printStackTrace();
                }
            }

            ResultSet rsSectorPlanes = objPlanServicio.getPlanesISP(id);

            String html = "obj»d_21^foc»sector^frm»";
            html += "<form action='frmSectorGuardar' onsubmit='return fac_sectorGuardar(this)' autocomplete='off'>";
            html += "<input type='hidden' id='id' name='id' value='"+id+"' /><table>";
            html += "<tr><td colspan='2' class='H3'>Formulario de Sector</td></tr>";
            if(Integer.valueOf(id_sucursal_matriz) == id_sucursal_sesion){
                html += "<tr><td>Sucursal: <span class='marca'>*</span></td>";
                html += "<td>" + DatosDinamicos.combo(rsSucursales, "id_sucursal", id_sucursal, "", "", 250) + "</td></tr>";
            }else{
                html += "<tr><td colspan='2'><input type='hidden' id='id_sucursal' name='id_sucursal' value='"+id_sucursal_sesion+"' /></td>";
            }
            html += "<tr><td>Sector: <span class='marca'>*</span></td>";
            html += "<td><input id='sector' name='sector' type='text' size='30' maxlength='80' value='"+sector+"' onkeypress=\"_alfanumerico(event);\" onblur=\"this.value=this.value._trim();\" /></td>";
            
            html += "<tr><td nowrap>Costo de instalaci&oacute;n con antena: <span class='marca'>*</span></td>";
            html += "<td><input id='costo_instalacion' name='costo_instalacion' type='text' size='30' maxlength='9' value='"+costo_instalacion+"' onkeypress=\"_evaluar(event, '0123456789.');\" /></td>";
            
            html += "<tr><td nowrap>Costo de instalaci&oacute;n con fibra: <span class='marca'>*</span></td>";
            html += "<td><input id='costo_instalacion_fibra' name='costo_instalacion_fibra' type='text' size='30' maxlength='9' value='"+costo_instalacion_fibra+"' onkeypress=\"_evaluar(event, '0123456789.');\" /></td>";
            
            html += "<tr><td>Servicio: <span class='marca'>*</span></td>";
            html += "<td>" + DatosDinamicos.combo(rsProductos, "id_producto", id_producto, "", "", 250) + "</td></tr>";
            /*html += "<tr><td>Cuenta contable de ingresos:</td>";
            html += "<td>" + DatosDinamicos.combo(cuentas, "id_plan_cuenta", id_plan_cuenta, "", "", 250) + "</td></tr>";*/

            html += "<tr valign='top'><td>Planes:</td>";
            html += "<td><div style='overflow:auto;width:250px;height:"+(altB-360)+"px;'><table>";
            try{
                int i=0;
                String id_plan = "";
                ResultSet rsPlanes = objPlanServicio.getPlanes();
                while(rsPlanes.next()){
                    id_plan = rsPlanes.getString("id_plan_isp")!=null ? rsPlanes.getString("id_plan_isp") : "";
                    html += "<tr><td width='20'><input type='hidden' id='id_plan"+i+"' name='id_plan"+i+"' value='"+id_plan+"' />"
                            + "<input type='checkbox' id='activo"+i+"' name='activo"+i+"' "+(obj_Sector.enSectorPlanes(rsSectorPlanes, id_plan)?"checked":"")+" /></td>"
                            + "<td width='200'>"+(rsPlanes.getString("plan")!=null ? rsPlanes.getString("plan") : "")+"</td></tr>";
                    i++;
                }
            }catch(Exception e){
                e.printStackTrace();
            }
            html += "</table></div></td></tr>";

            if(id.compareTo("-1")==0 || (id.compareTo("-1")!=0 && objRol.getAcceso(id_rol, "sectorEditar"))){
                html += "<tr><td colspan='2'><input type='submit' value='Guardar' /></td></tr>";
            }

            html += "</table></form>";
            out.print(html);

        } finally {
            obj_Sucursal.cerrar();
            obj_Producto.cerrar();
            objPlanServicio.cerrar();
            obj_Sector.cerrar();
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
