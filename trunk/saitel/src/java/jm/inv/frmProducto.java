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
import jm.inv.clas.Proveedor;
import jm.inv.clas.Producto;
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
public class frmProducto extends HttpServlet {
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
        
        Producto objproducto = new Producto(this._ip, this._puerto, this._db, usuario, clave);
        Comun objComun = new Comun(this._ip, this._puerto, this._db, usuario, clave);
        Rol objRol = new Rol(this._ip, this._puerto, this._db, usuario, clave);
        Iva objIva = new Iva(this._ip, this._puerto, this._db, usuario, clave);
        ResultSet rsIvas = objIva.getIvas();
        
        try {
            String id = request.getParameter("id");
            String alto = request.getParameter("altB");
            int _altBody = Integer.parseInt(alto);
            
            String codigo = "";
            String codigo_fabricante = "";
            String descripcion = "";
            String tipo = "p";
            String unidad_medida = "unidades";
            String utilidad_min = "5";
            String id_categoria = "";
            String stock_min = "1";
            String stock = "0";
            String precio_venta_servicio = "0";
            String id_iva = "2";
            String id_plan_cuenta_compra = "24";
            String id_plan_cuenta_venta = "91";
            String observacion = "";

            String [][] tipo_producto1 = {{"p","Producto"},{"s","Servicio"}};
            
            //String [][] id_iva1 = {{"1",p_iva0},{"2",p_iva2},{"9","No objeto del IVA"}};

            if(id.compareTo("-1")!=0){
                try{
                    ResultSet rsproducto = objproducto.getProducto(id);
                    if(rsproducto.next()){
                        codigo = (rsproducto.getString("codigo")!=null) ? rsproducto.getString("codigo") : "";
                        codigo_fabricante = (rsproducto.getString("codigo_fabricante")!=null) ? rsproducto.getString("codigo_fabricante") : "";
                        descripcion = (rsproducto.getString("descripcion")!=null) ? rsproducto.getString("descripcion") : "";
                        tipo = (rsproducto.getString("tipo")!=null) ? rsproducto.getString("tipo") : "p";
                        unidad_medida = (rsproducto.getString("unidad_medida")!=null) ? rsproducto.getString("unidad_medida") : "unidades";
                        utilidad_min = (rsproducto.getString("utilidad_min")!=null) ? rsproducto.getString("utilidad_min") : "";
                        id_categoria = (rsproducto.getString("id_categoria")!=null) ? rsproducto.getString("id_categoria") : "";
                        stock_min = (rsproducto.getString("stock_min")!=null) ? rsproducto.getString("stock_min") : "";
                        stock = (rsproducto.getString("stock")!=null) ? rsproducto.getString("stock") : "";
                        precio_venta_servicio = (rsproducto.getString("precio_venta_servicio")!=null) ? rsproducto.getString("precio_venta_servicio") : "0";
                        id_iva = (rsproducto.getString("id_iva")!=null) ? rsproducto.getString("id_iva") : "";
                        id_plan_cuenta_compra = (rsproducto.getString("id_plan_cuenta_compra")!=null) ? rsproducto.getString("id_plan_cuenta_compra") : "";
                        id_plan_cuenta_venta = (rsproducto.getString("id_plan_cuenta_venta")!=null) ? rsproducto.getString("id_plan_cuenta_venta") : "";
                        observacion = (rsproducto.getString("observacion")!=null) ? rsproducto.getString("observacion") : "";
                        rsproducto.close();
                    }
                }catch(Exception e){
                    e.printStackTrace();
                }
            }


            ResultSet rsCategorias = objComun.getComun("CAT-");
            ResultSet rsUbicaciones = objComun.getComun("UIP-");

            PlanCuenta objPlanCuenta = new PlanCuenta(this._ip, this._puerto, this._db, usuario, clave);
            ResultSet rsCuentas = objPlanCuenta.getTablasAsignacion();
            objPlanCuenta.cerrar();

            String html = "obj»d_21^foc»c^fun»_objTab.seleccionar(0);^frm»";
            html += "<div class='H3' style='width:480px'>Formulario de Producto</div>";
            
            html += "<div class='grupoTab' style='width:460px;'><div id='tabR0'>"
                    + "<a id='tab00' href='javascript:void(0);' class='jm_tabAct' onclick='_objTab.seleccionar(0);'>Producto</a>"
                    + "<a id='tab01' href='javascript:void(0);' class='jm_tab' onclick='_objTab.seleccionar(1);'>Sucursales</a>"
                    + "<a id='tab02' href='javascript:void(0);' class='jm_tab' onclick='_objTab.seleccionar(2);'>Proveedores</a>";
            if(id.compareTo("-1")!=0){
                html += "<a id='tab03' href='javascript:void(0);' class='jm_tab' onclick='_objTab.seleccionar(3);'>Listas de Precios</a>";
            }
            html += "</div>";

            html += "<form action='frmProductoGuardar' onsubmit='return inv_productoGuardar(this)' autocomplete='off'>";
            html += "<input type='hidden' id='id' name='id' value='"+id+"' />";

            /* PRIMER TAB */
            html += "<div id='tabR1'>";
            /* PRIMER TAB */
            html += "<div id='tab10' class='jm_tabHTML' style='display:block;width:450px;height:"+_altBody+"px;'>";
            html += "<table><tr><td>C&oacute;digo: <span class='marca'>*</span> </td>";
            html += "<td nowrap><input id='c' name='c' type='text' size='35' maxlength='30' value='"+codigo+"' onkeydown='_NoE(event)' onkeypress=\"_alfanumerico(event);\" onblur=\"this.value=this.value._trim();\" /></td></tr>";
            html += "<tr><td>C&oacute;digo fabricante: </td>";
            html += "<td nowrap><input id='cf' name='cf' type='text' size='35' maxlength='30' value='"+codigo_fabricante+"' onkeydown='_NoE(event)' onblur=\"this.value=this.value._trim();\" /></td></tr>";
            html += "<tr><td>Descripci&oacute;n: <span class='marca'>*</span> </td>";
            html += "<td nowrap><input id='de' name='de' type='text' size='35' maxlength='80' value=\""+descripcion+"\" onblur=\"this.value=this.value._trim();\" /></td></tr>";
            html += "<tr><td nowrap>Tipo: <span class='marca'>*</span> </td>";
            html += "<td>" + DatosDinamicos.combo("t", tipo, tipo_producto1, "inv_setServicio()") + "</td></tr>";
            html += "<tr><td nowrap>Unidad de medida: <span class='marca'>*</span> </td>";
            html += "<td><input id='um' name='um' type='text' size='35' maxlength='20' value='"+unidad_medida+"' onkeypress=\"_alfanumerico(event);\" onblur=\"this.value=this.value._trim();\" /></td></tr>";

            html += "<tr><td nowrap>% de utilidad m&iacute;nimo: <span class='marca'>*</span> </td>";
            html += "<td><input id='utilidad_min' name='utilidad_min' type='text' size='35' maxlength='20' value='"+utilidad_min+"' onkeypress=\"_evaluar(event, '0123456789.');\" /></td></tr>";

            html += "<tr><td nowrap>Stock m&iacute;nimo: <span class='marca'>*</span> </td>";
            html += "<td><input id='stm' name='stm' type='text' size='35' maxlength='20' value='"+stock_min+"' onkeypress='_numero(event);' /></td></tr>";
            html += "<tr><td nowrap>Stock: <span class='marca'>*</span> </td>";
            html += "<td><input id='st' name='st' readonly type='text' size='35' maxlength='20' value='"+stock+"' onkeypress='_numero(event);' /></td></tr>";

            html += "<tr><td>Categor&iacute;a: <span class='marca'>*</span></td>";
            html += "<td><div id='cob0' style='float:left;display:table-cell'>"+DatosDinamicos.combo(rsCategorias, "idC", id_categoria, "", "", 150)+"</div>"
                    + "<div class='jm_icoCon' id='gr0' style='visibility:hidden;background-position: -545px 0px;' onclick=\"guardarComun(null,'cob0','idC','gr0','add0',150,'CAT');\" title='guardar'>&nbsp;</div>"
                    + "<div class='jm_icoCon' id='add0' style='background-position:-504px -14px;' onclick=\"insertarComun('cob0','idC','gr0','add0',150,'CAT');\" title='Insertar'>&nbsp;</div></td></tr>";
            /*html += "<tr><td>Ubicaci&oacute;n: <span class='marca'>*</span></td>";
            html += "<td><div id='cob1' style='display:table-cell'>"+DatosDinamicos.combo(rsUbicaciones, "idUI", id_ubicacion, "", "", 150)+"</div>"
                    + "<div class='jm_icoCon' id='gr1' style='visibility:hidden;background-position: -545px 3px;' onclick=\"guardarComun(null,'cob1','idUI','gr1','add1',150,'UIP');\" title='guardar'>&nbsp;</div>"
                    + "<div class='jm_icoCon' id='add1' style='background-position:-504px -14px;' onclick=\"insertarComun('cob1','idUI','gr1','add1',150,'UIP');\" title='Insertar'>&nbsp;</div></td></tr>";
            */
            String visible = tipo.compareTo("s")==0 ? "visible" : "hidden";
            html += "<tr id='axPV' style='visibility:"+visible+"'><td>Precio de venta: </td>";
            html += "<td><input id='pv' name='pv' type='text' size='35' maxlength='20' value='"+precio_venta_servicio+"' onkeypress=\"_evaluar(event, '0123456789.');\" /></td></tr>";
            html += "<tr><td nowrap>Tipo de IVA: </td>";
            html += "<td>" + DatosDinamicos.combo(rsIvas, "id_iva", id_iva, "") + "</td></tr>";
            html += "<tr><td>Cuenta en compras: <span class='marca'>*</span></td>";
            html += "<td>"+DatosDinamicos.combo(rsCuentas, "idPCC", id_plan_cuenta_compra, "", 240)+"</td></tr>";
            html += "<tr><td>Cuenta en ventas: <span class='marca'>*</span></td>";
            html += "<td>"+DatosDinamicos.combo(rsCuentas, "idPCV", id_plan_cuenta_venta, "", 240)+"</td></tr>";
            html += "<tr valign='top'><td>Observaci&oacute;n:</td>";
            html += "<td><textarea id='o' name='o' rows='2' cols='25' onblur=\"this.value=this.value._trim();\" >"+observacion+"</textarea></td></tr>";
            html += "</table></div>";

            
            /* SEGUNDO TAB */
            html += "<div id='tab11' class='jm_tabHTML' style='display:none;width:450px;height:"+_altBody+"px;'>";
            html += "<TABLE cellspacing='0' cellpadding='0' width='425'><TR>" +
                            "<TH class='jm_TH' width='23'>&nbsp;</TH>" +
                            "<TH class='jm_TH' width='118'>SUCURSAL</TH>" +
                            //"<TH class='jm_TH' width='73'>P.V.</TH>"+
                            "<TH class='jm_TH' width='63'>% DESC.</TH>" +
                            "<TH class='jm_TH' width='73'><div style='float:left;display:table-cell'>UBIC. </div> <div class='jm_icoCon' style='background-position:-504px -14px;' onclick=\"inv_insertarUbicacion();\" title='Insertar ubicaci&oacute;n'>&nbsp;</div></TH>" +
                            "<TH class='jm_TH' width='63'>STOCK</TH></TR></TABLE>";
                
            html += "<table cellspacing='0' cellpadding='0' width='425'>";
            Sucursal objSuc = new Sucursal(this._ip, this._puerto, this._db, usuario, clave);
            try{
                ResultSet rsSucursales = objSuc.getSucursales();
                ResultSet rsCodigos = objproducto.getCodigoSucursales(id);
                String tblCodigos [][] = DatosDinamicos.ResultSetToMatriz(rsCodigos);
                String id_sucursal = "";
                String sucursal = "";
                int i=0;
                int p = 0;
                while(rsSucursales.next()){
                    id_sucursal = (rsSucursales.getString("id_sucursal")!=null) ? rsSucursales.getString("id_sucursal") : "";
                    sucursal = (rsSucursales.getString("sucursal")!=null) ? rsSucursales.getString("sucursal") : "";
                    p = DatosDinamicos.enMatriz(tblCodigos, id_sucursal, 0);
                    html += "<tr valign='top'><td width='25'><input id='s_ch"+i+"' name='s_ch"+i+"' type='checkbox' "+(id.compareTo("-1")==0 ? "checked" : ((p>=0)?"checked":"") )+" /></td>" +
                            "<td width='120'><input type='hidden' id='s_idS"+i+"' name='s_idS"+i+"' value='"+id_sucursal+"' />"+sucursal+"</td>"+
                            //"<td width='75'>"+DatosDinamicos.combo(rsListas, "idLP"+i, ((p>=0)?tblCodigos[p][1]:""), "", "", 70)+"</td>"+
                            "<td width='65'><input type='text' id='des"+i+"' name='des"+i+"' size='5' maxlenght='5' onkeypress=\"_evaluar(event,'0123456789.');\" value='"+((p>=0)?tblCodigos[p][1]:"0")+"' /></td>"+
                            "<td width='75'><div id='cobu"+i+"' style='display:table-cell'>"+DatosDinamicos.combo(rsUbicaciones, "idUI"+i, ((p>=0)?tblCodigos[p][3]:""), "", "", 70)+"</div></td>"+
                            "<td width='65'><input type='text' id='skSc"+i+"' name='skSc"+i+"' size='5' maxlenght='6' "+(tipo.compareTo("p")==0?"readonly":"")+" onkeypress='_numero(event)' value='"+((p>=0)?tblCodigos[p][3]:"0")+"' /></td></tr>";
                    i++;
                }
                rsCodigos.close();
                rsSucursales.close();
            }catch(Exception e){
                e.printStackTrace();
            }finally{
                objSuc.cerrar();
            }
            html += "</table></div>";


            /* TERCER TAB */
            html += "<div id='tab12' class='jm_tabHTML' style='display:none;width:450px;height:"+_altBody+"px;'>";
            html += "<table cellspacing='0' cellpadding='0'>";
            Proveedor objProveedor = new Proveedor(this._ip, this._puerto, this._db, usuario, clave);
            try{
                ResultSet rsProveedores = objProveedor.getProveedores();
                ResultSet rsCodigos = objproducto.getCodigoProveedores(id);
                String tblProveedores [][] = DatosDinamicos.ResultSetToMatriz(rsCodigos);
                String id_proveedor = "";
                String razon_social = "";
                int i=0;
                while(rsProveedores.next()){
                    id_proveedor = (rsProveedores.getString("id_proveedor")!=null) ? rsProveedores.getString("id_proveedor") : "";
                    razon_social = (rsProveedores.getString("razon_social")!=null) ? rsProveedores.getString("razon_social") : "";
                    html += "<tr><td><input id='ch"+i+"' name='ch"+i+"' type='checkbox' "+((DatosDinamicos.enMatriz(tblProveedores, id_proveedor, 0)>=0)?"checked":"")+" /></td>" +
                            "<td><input type='hidden' id='idP"+i+"' name='idP"+i+"' value='"+id_proveedor+"' />"+razon_social+"</td></tr>";
                    i++;
                }
                rsCodigos.close();
                rsProveedores.close();
            }catch(Exception e){
                e.printStackTrace();
            }finally{
                objProveedor.cerrar();
            }
            html += "</table></div>";


            /* CUARTO TAB */
            html += "<div id='tab13' class='jm_tabHTML' style='display:none;width:450px;height:"+_altBody+"px;'>";

            html += "<input type='button' value='Nueva Lista' onclick='inv_productoPrecio(-1)' />";
            html += "<TABLE cellspacing='0' cellpadding='0'><TR>" +
                            "<TH class='jm_TH' width='200'>NOMBRE</TH>" +
                            "<TH class='jm_TH' width='80'>% UTILIDAD</TH>" +
                            "<TH class='jm_TH' width='20'>&nbsp;</TH></TR></TABLE>";
            html += "<div id='axTblLP'><table cellspacing='0' cellpadding='0'>";
            try{
                //ListaPrecio obj_ListaPrecio = new ListaPrecio(this._ip, this._puerto, this._db, usuario, clave);
                ResultSet rsPrecios = objproducto.getPrecios(id);
                String id_lista_precio = "";
                String lista = "";
                String utilidad = "";
                int i=0;
                while(rsPrecios.next()){
                    id_lista_precio = (rsPrecios.getString("id_lista_precio")!=null) ? rsPrecios.getString("id_lista_precio") : "";
                    lista = (rsPrecios.getString("lista")!=null) ? rsPrecios.getString("lista") : "";
                    utilidad = (rsPrecios.getString("utilidad")!=null) ? rsPrecios.getString("utilidad") : "";
                    html += "<tr id='rLP"+i+"' valign='top' style='cursor:pointer'>" +
                            "<td width='200' onclick=\"inv_productoPrecio("+id_lista_precio+")\"><input type='hidden' id='idLP"+i+"' name='idLP"+i+"' value='"+id_lista_precio+"' />"+
                            "<input type='text' id='lista"+i+"' name='lista"+i+"' size='35' maxlenght='30' value='"+lista+"' /></td>"+
                            "<td width='80' onclick=\"inv_productoPrecio("+id_lista_precio+")\"><input type='text' id='utilidad"+i+"' name='utilidad"+i+"' size='7' maxlenght='7' onkeypress=\"_evaluar(event,'0123456789.');\" value='"+utilidad+"' /></td>"+
                            "<td width='20'><div class='jm_icoCon' id='e"+i+"' style='background-position:-545px -18px;' onclick=\"inv_productoPrecioEliminar("+id_lista_precio+", "+i+");\" title='eliminar'>&nbsp;</div></td></tr>";
                    i++;
                }
                rsPrecios.close();
            }catch(Exception e){
                e.printStackTrace();
            }
            html += "</table></div>";
            html += "</div>";


            if(id.compareTo("-1")==0 || (id.compareTo("-1")!=0 && objRol.getAcceso(id_rol, "productoEditar"))){
                html += "<input type='submit' value='Guardar' />";
            }
            html += "</div></form></div>";
            out.println(html);

            try{
                rsCuentas.close();
                //rsListas.close();
                rsUbicaciones.close();
                rsCategorias.close();
            }catch(Exception e){
                e.printStackTrace();
            }

            
        } finally {
            objIva.cerrar();
            objRol.cerrar();
            objComun.cerrar();
            objproducto.cerrar();
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
