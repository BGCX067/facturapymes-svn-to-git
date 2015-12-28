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

package jm.inv;

import java.io.*;

import javax.servlet.*;
import javax.servlet.http.*;
import jm.seg.clas.Rol;
import jm.inv.clas.Proveedor;
import java.sql.ResultSet;
import jm.adm.clas.Configuracion;
import jm.cnt.clas.PlanCuenta;
import jm.web.DatosDinamicos;
import jm.web.Fecha;

/**
 *
 * @author Jorge
 */
public class frmProveedor extends HttpServlet {
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
        
        Proveedor objProveedor = new Proveedor(this._ip, this._puerto, this._db, usuario, clave);
        Rol objRol = new Rol(this._ip, this._puerto, this._db, usuario, clave);
        
        try {
            Configuracion conf = new Configuracion(this._ip, this._puerto, this._db, usuario, clave);
            String cxp = conf.getValor("cxp");
            conf.cerrar();

            String id = request.getParameter("id");
            String accion = request.getParameter("act");
            String documento = request.getParameter("doc");

            String id_plan_cuenta = cxp;
            String tipo_documento = "01";
            String ruc = "";
            String razon_social = "";
            String aut_factura = "";
            String serie_factura = "001-001";
            String fecha_cad_factura = "";
            String direccion = "";
            String ciudad = "";
            String pais = "ECUADOR";
            String telefono = "";
            String fax = "";
            String email = "";
            String web = "";
            String contacto = "";
            String observacion = "";
            if(id.compareTo("-1")!=0){
                try{
                    ResultSet rsProveedor = objProveedor.getProveedor(id);
                    if(rsProveedor.next()){
                        id_plan_cuenta = (rsProveedor.getString("id_plan_cuenta")!=null) ? rsProveedor.getString("id_plan_cuenta") : cxp;
                        tipo_documento = (rsProveedor.getString("tipo_documento")!=null) ? rsProveedor.getString("tipo_documento") : "";
                        ruc = (rsProveedor.getString("ruc")!=null) ? rsProveedor.getString("ruc") : "";
                        razon_social = (rsProveedor.getString("razon_social")!=null) ? rsProveedor.getString("razon_social") : "";
                        aut_factura = (rsProveedor.getString("aut_factura")!=null) ? rsProveedor.getString("aut_factura") : "";
                        serie_factura = (rsProveedor.getString("serie_factura")!=null) ? rsProveedor.getString("serie_factura") : "001-001";
                        fecha_cad_factura = (rsProveedor.getString("fecha_cad_factura")!=null) ? Fecha.ISOaSQL(rsProveedor.getString("fecha_cad_factura")) : "";
                        direccion = (rsProveedor.getString("direccion")!=null) ? rsProveedor.getString("direccion") : "";
                        ciudad = (rsProveedor.getString("ciudad")!=null) ? rsProveedor.getString("ciudad") : "";
                        pais = (rsProveedor.getString("pais")!=null) ? rsProveedor.getString("pais") : "Ecuador";
                        telefono = (rsProveedor.getString("telefono")!=null) ? rsProveedor.getString("telefono") : "";
                        fax = (rsProveedor.getString("fax")!=null) ? rsProveedor.getString("fax") : "";
                        email = (rsProveedor.getString("email")!=null) ? rsProveedor.getString("email") : "";
                        web = (rsProveedor.getString("web")!=null) ? rsProveedor.getString("web") : "";
                        contacto = (rsProveedor.getString("contacto")!=null) ? rsProveedor.getString("contacto") : "";
                        observacion = (rsProveedor.getString("observacion")!=null) ? rsProveedor.getString("observacion") : "";
                        rsProveedor.close();
                    }
                }catch(Exception e){
                    e.printStackTrace();
                }
            }

            PlanCuenta objPlanCuenta = new PlanCuenta(this._ip, this._puerto, this._db, usuario, clave);
            ResultSet rsCuentas = objPlanCuenta.getTablasAsignacion();
            objPlanCuenta.cerrar();

            String [][] tipo_documento1 = {{"01","RUC"},{"02","C&eacute;dula"},{"03","Pasaporte"}};

            String obj = (accion.compareTo("frmProveedorGuardar")==0) ? "d_21" : "vtPrv_html";
            String html = "obj»"+obj+"^foc»ru^frm»";
            html += "<form action='"+accion+"' onsubmit='return inv_proveedorGuardar(this)' autocomplete='off'>";
            html += "<input type='hidden' id='id' name='id' value='"+id+"' />";
            html += "<input type='hidden' id='doc' name='doc' value='"+documento+"' />";
            html += "<table><tr><td colspan='2' class='H3'>Formulario de Proveedor</td></tr>";
            html += "<tr><td>Tipo de documento: <span class='marca'>*</span></td>";
            html += "<td>" + DatosDinamicos.combo("tipo_documento", tipo_documento, tipo_documento1, "") + "</td></tr>";
            html += "<tr><td>CI / RUC: <span class='marca'>*</span></td>";
            html += "<td nowrap><input id='ru' name='ru' type='text' size='25' maxlength='13' class='may' value='"+ruc+"' onkeypress=\"_DNI(event)\" /> " +
                    " <input type='checkbox' id='vf' checked='checked' disabled /> verificar</td></tr>";
            html += "<tr><td>Raz&oacute;n Social: <span class='marca'>*</span></td>";
            html += "<td><input id='rs' name='rs' class='may' type='text' size='40' maxlength='100' value='"+razon_social+"' onblur=\"this.value=this.value._trim();\" /></td></tr>";

            html += "<tr><td>Cuenta por pagar: <span class='marca'>*</span></td>";
            html += "<td>"+DatosDinamicos.combo(rsCuentas, "idPC", id_plan_cuenta, "", 240)+"</td></tr>";

            html += "<tr><td>Autorizaci&oacute;n facturas: </td>";
            html += "<td><input id='auF' name='auF' type='text' size='40' maxlength='10' value='"+aut_factura+"' onkeypress=\"_numero(event);\" /></td></tr>";
            html += "<tr><td>Serie facturas: </td>";
            html += "<td><input id='seF' name='seF' type='text' size='40' maxlength='7' value='"+serie_factura+"' onkeypress=\"_evaluar(event, '0123456789-');\" /></td></tr>";
            html += "<tr><td nowrap>Fecha de caducidad del facturero: </td>";
            html += "<td><input id='caF' name='caF' type='text' size='25' readonly='readonly' value='"+fecha_cad_factura+"' /> " +
                    "<input type='button' value='...' onClick=\"SelectorFecha.crear('caF', 'SQL');\"/></td></tr>";

            html += "<tr><td>Direcci&oacute;n: <span class='marca'>*</span></td>";
            html += "<td><input id='di' name='di' type='text' class='may' size='40' maxlength='80' value='"+direccion+"' onkeypress=\"_alfanumerico(event);\" onblur=\"this.value=this.value._trim();\" /></td></tr>";
            html += "<tr><td>Ciudad: <span class='marca'>*</span></td>";
            html += "<td><input id='ci' name='ci' class='may' type='text' size='40' maxlength='30' value='"+ciudad+"' onkeypress=\"_letra(event);\" onblur=\"this.value=this.value._trim();\" /></td></tr>";
            html += "<tr><td>Pa&iacute;s: <span class='marca'>*</span></td>";
            html += "<td><input id='pa' name='pa' class='may' type='text' size='40' maxlength='30' value='"+pais+"' onkeypress=\"_letra(event);\" onblur=\"this.value=this.value._trim();\" /></td></tr>";
            html += "<tr><td>Tel&eacute;fono:</td>";
            html += "<td><input id='te' name='te' type='text' size='40' maxlength='30' value='"+telefono+"' onkeypress=\"_evaluar(event, '0123456789-/ ');\" onblur=\"this.value=this.value._trim();\" /></td></tr>";
            html += "<tr><td>Fax:</td>";
            html += "<td><input id='fa' name='fa' type='text' size='40' maxlength='30' value='"+fax+"' onkeypress=\"_evaluar(event, '0123456789-/ ');\" onblur=\"this.value=this.value._trim();\" /></td></tr>";
            html += "<tr><td>E-Mail:</td>";
            html += "<td><input id='ma' name='ma' type='text' size='40' maxlength='50' value='"+email+"' onblur=\"this.value=this.value._trim();\" /></td></tr>";
            html += "<tr><td>Web:</td>";
            html += "<td><input id='we' name='we' type='text' size='40' maxlength='50' value='"+web+"' onblur=\"this.value=this.value._trim();\" /></td></tr>";
            html += "<tr><td>Contacto:</td>";
            html += "<td><input id='co' name='co' type='text' size='40' maxlength='70' value='"+contacto+"' onkeypress=\"_letra(event);\" onblur=\"this.value=this.value._trim();\" /></td></tr>";
            html += "<tr><td>Observaci&oacute;n:</td>";
            html += "<td><textarea id='ob' name='ob' rows='2' cols='25' onblur=\"this.value=this.value._trim();\">"+observacion+"</textarea></td></tr>";

            if(id.compareTo("-1")==0 || (id.compareTo("-1")!=0 && objRol.getAcceso(id_rol, "proveedorEditar"))){
                html += "<tr><td colspan='2'><input type='submit' value='Guardar' /></td></tr>";
            }
            html += "</table></form>";
            out.println(html);

        } finally {
            objRol.cerrar();
            objProveedor.cerrar();
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