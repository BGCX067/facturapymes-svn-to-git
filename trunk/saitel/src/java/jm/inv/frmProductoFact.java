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
import java.sql.ResultSet;
import jm.seg.clas.Rol;
import jm.adm.clas.Comun;
import jm.adm.clas.Configuracion;
import jm.adm.clas.Iva;
import jm.adm.clas.Sucursal;
import jm.cnt.clas.PlanCuenta;
import jm.web.DatosDinamicos;

/**
 *
 * @author Jorge
 */
public class frmProductoFact extends HttpServlet {
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
        
        Configuracion conf = new Configuracion(this._ip, this._puerto, this._db, usuario, clave);
        Sucursal objSucursal = new Sucursal(this._ip, this._puerto, this._db, usuario, clave);
        Comun objComun = new Comun(this._ip, this._puerto, this._db, usuario, clave);
        Rol objRol = new Rol(this._ip, this._puerto, this._db, usuario, clave);
        Iva objIva = new Iva(this._ip, this._puerto, this._db, usuario, clave);
        ResultSet rsIvas = objIva.getIvas();
        
        String p_iva1 = conf.getValor("p_iva1") + "%";
        String p_iva2 = conf.getValor("p_iva2") + "%";
        
        try {
            String id = request.getParameter("id");
            String idP = request.getParameter("idP");

            String [][] tipo_producto1 = {{"p","Producto"},{"s","Servicio"}};

            String id_sucursal = conf.getValor("id_sucursal_matriz");

            ResultSet rsCategorias = objComun.getComun("CAT-");
            ResultSet rsUbicaciones = objComun.getComun("UIP-");

            PlanCuenta objPlanCuenta = new PlanCuenta(this._ip, this._puerto, this._db, usuario, clave);
            ResultSet rsCuentas = objPlanCuenta.getTablasAsignacion();
            objPlanCuenta.cerrar();

            /*ListaPrecio obj_ListaPrecio = new ListaPrecio(this._ip, this._puerto, this._db, usuario, clave);
            ResultSet rsListas = obj_ListaPrecio.getListas();*/

            String html = "obj»vtPrd_html^foc»c^frm»";
            html += "<div class='H3' style='width:350px'>Formulario de Producto</div>";

            html += "<form action='frmProductoFactGuardar' onsubmit='return inv_frmProductoGuardar(this)' autocomplete='off'>";
            html += "<input type='hidden' id='id' name='id' value='"+id+"' />";
            html += "<input type='hidden' id='idPr' name='idPr' value='"+idP+"' />";
            
            html += "<table width='345'><tr><td>C&oacute;digo: <span class='marca'>*</span> </td>";
            html += "<td nowrap><input id='c' name='c' type='text' size='35' maxlength='30' value='' onkeydown='_NoE(event)' onkeypress=\"_alfanumerico(event);\" onblur=\"this.value=this.value._trim();\" /></td></tr>";
            html += "<tr><td>Descripci&oacute;n: <span class='marca'>*</span> </td>";
            html += "<td nowrap><input id='de' name='de' type='text' size='35' maxlength='80' value='' onblur=\"this.value=this.value._trim();\" /></td></tr>";
            html += "<tr><td nowrap>Tipo: <span class='marca'>*</span> </td>";
            html += "<td>" + DatosDinamicos.combo("t", "p", tipo_producto1, "") + "</td></tr>";
            html += "<tr><td nowrap>Unidad de medida: <span class='marca'>*</span> </td>";
            html += "<td><input id='um' name='um' type='text' size='35' maxlength='20' value='unidades' onkeypress=\"_alfanumerico(event);\" onblur=\"this.value=this.value._trim();\" /></td></tr>";

            html += "<tr><td nowrap>% de utilidad m&iacute;nimo: <span class='marca'>*</span> </td>";
            html += "<td><input id='utilidad_min' name='utilidad_min' type='text' size='35' maxlength='20' value='5' onkeypress=\"_evaluar(event, '0123456789.');\" /></td></tr>";

            html += "<tr><td nowrap>Stock m&iacute;nimo: <span class='marca'>*</span> </td>";
            html += "<td><input id='stm' name='stm' type='text' size='35' maxlength='20' value='0' onkeypress='_numero(event);' /></td></tr>";

            html += "<tr><td>Categor&iacute;a: <span class='marca'>*</span></td>";
            html += "<td><div id='cob0' style='float:left;display:table-cell'>"+DatosDinamicos.combo(rsCategorias, "idC", "", "", "", 150)+"</div>"
                    + "<div class='jm_icoCon' id='gr0' style='visibility:hidden;background-position: -545px 0px;' onclick=\"guardarComun(null,'cob0','idC','gr0','add0',150,'CAT');\" title='guardar'>&nbsp;</div>"
                    + "<div class='jm_icoCon' id='add0' style='background-position:-504px -14px;' onclick=\"insertarComun('cob0','idC','gr0','add0',150,'CAT');\" title='Insertar'>&nbsp;</div></td></tr>";

            html += "<tr><td nowrap>IVA gravado: </td>";
            html += "<td>" + DatosDinamicos.combo(rsIvas, "id_iva", "", "") + "</td></tr>";

            html += "<tr><td>Cuenta en compras: <span class='marca'>*</span></td>";
            html += "<td>"+DatosDinamicos.combo(rsCuentas, "idPCC", "24", "", 200)+"</td></tr>";
            html += "<tr><td>Cuenta en ventas: <span class='marca'>*</span></td>";
            html += "<td>"+DatosDinamicos.combo(rsCuentas, "idPCV", "91", "", 200)+"</td></tr>";

            html += "<tr><td nowrap>Sucursal: </td>";
            html += "<td>"+objSucursal.getNombre(id_sucursal)+"</td></tr>";
            /*html += "<tr valign='top'><td>P.V.:</td>";
            html += "<td>"+DatosDinamicos.combo(rsListas, "idLP", "", "", "", 150)+"</td></tr>";*/
            html += "<tr><td nowrap>Descuento: </td>";
            html += "<td><input id='desc' name='desc' type='text' size='35' maxlength='20' value='0' onkeypress='_numero(event);' /></td></tr>";
            html += "<tr valign='top'><td>Ubicaci&oacute;n:</td>";
            html += "<td>"+DatosDinamicos.combo(rsUbicaciones, "idUI", "", "", "", 150)+"</td></tr>";
            html += "</table>";


            if(id.compareTo("-1")==0 || (id.compareTo("-1")!=0 && objRol.getAcceso(id_rol, "productoEditar"))){
                html += "<input type='submit' value='Guardar' />";
            }
            html += "</form>";
            out.println(html);

            try{
                rsCuentas.close();
                rsUbicaciones.close();
                rsCategorias.close();
            }catch(Exception e){
                e.printStackTrace();
            }

        } finally {
            objIva.cerrar();
            conf.cerrar();
            objSucursal.cerrar();
            objRol.cerrar();
            objComun.cerrar();
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
