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
import jm.seg.clas.Rol;
import jm.act.clas.Activo;
import jm.adm.clas.Bodega;
import jm.adm.clas.Comun;
import jm.adm.clas.Sucursal;
import jm.adm.clas.TablaDepreciacion;
import jm.web.DatosDinamicos;
import jm.web.Fecha;

/**
 *
 * @author Jorge
 */
public class frmActivoInfraestructura extends HttpServlet {
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
        try {
            String mac = request.getParameter("mac");
            String obj = request.getParameter("obj");
            String id_instalacion = request.getParameter("idIns");

            String id= "-1";
            String codigo_activo = "";
            String id_tabla_depreciacion = "";
            String descripcion = "";
            String id_sucursal = "";
            String id_categoria = "";
            String id_bodega = "";
            String ubicacion = "";
            String fecha_compra = Fecha.getFecha("SQL");
            String marca = "";
            String serie = "";
            String modelo = "";
            String num_partes = "1";
            String color = "";
            String valor_compra = "";
            String valor_depreciado = "0";
            String num_meses = "0";
            String estado = "b";
            String observacion = "";

            Activo objActivo = new Activo(this._ip, this._puerto, this._db, usuario, clave);
            try{
                ResultSet rsActivo = objActivo.getActivo(mac);
                if(rsActivo.next()){
                    id = (rsActivo.getString("id_activo")!=null) ? rsActivo.getString("id_activo") : "-1";
                    codigo_activo = (rsActivo.getString("codigo_activo")!=null) ? rsActivo.getString("codigo_activo") : "";
                    id_tabla_depreciacion = (rsActivo.getString("id_tabla_depreciacion")!=null) ? rsActivo.getString("id_tabla_depreciacion") : "";
                    descripcion = (rsActivo.getString("descripcion")!=null) ? rsActivo.getString("descripcion") : "";
                    id_sucursal = (rsActivo.getString("id_sucursal")!=null) ? rsActivo.getString("id_sucursal") : "";
                    id_categoria = (rsActivo.getString("id_categoria")!=null) ? rsActivo.getString("id_categoria") : "";
                    id_bodega = (rsActivo.getString("id_bodega")!=null) ? rsActivo.getString("id_bodega") : "";
                    ubicacion = (rsActivo.getString("ubicacion")!=null) ? rsActivo.getString("ubicacion") : "";
                    fecha_compra = (rsActivo.getString("fecha_compra")!=null) ? rsActivo.getString("fecha_compra") : "";
                    marca = (rsActivo.getString("marca")!=null) ? rsActivo.getString("marca") : "";
                    serie = (rsActivo.getString("serie")!=null) ? rsActivo.getString("serie") : "";
                    modelo = (rsActivo.getString("modelo")!=null) ? rsActivo.getString("modelo") : "";
                    num_partes = (rsActivo.getString("num_partes")!=null) ? rsActivo.getString("num_partes") : "";
                    color = (rsActivo.getString("color")!=null) ? rsActivo.getString("color") : "";
                    valor_compra = (rsActivo.getString("valor_compra")!=null) ? rsActivo.getString("valor_compra") : "";
                    valor_depreciado = (rsActivo.getString("valor_depreciado")!=null) ? rsActivo.getString("valor_depreciado") : "";
                    num_meses = (rsActivo.getString("num_meses")!=null) ? rsActivo.getString("num_meses") : "";
                    estado = (rsActivo.getString("estado")!=null) ? rsActivo.getString("estado") : "";
                    observacion = (rsActivo.getString("observacion")!=null) ? rsActivo.getString("observacion") : "";
                    rsActivo.close();
                }
            }catch(Exception e){
                e.printStackTrace();
            }
            

            //Configuracion conf = new Configuracion(this._ip, this._puerto, this._db, usuario, clave);
            //String id_sucursal_matriz = conf.getValor("id_sucursal_matriz");
            //conf.cerrar();

            Sucursal objSucursal = new Sucursal(this._ip, this._puerto, this._db, usuario, clave);
            ResultSet rsSucursales = objSucursal.getSucursales();

            Bodega objBodega = new Bodega(this._ip, this._puerto, this._db, usuario, clave);
            //ResultSet rsBodegas = objBodega.getBodegas();
            //if(Integer.parseInt(id_sucursal_matriz) != id_sucursal_sesion){
            ResultSet rsBodegas = objBodega.getBodegas(id_sucursal_sesion);
            //}
            if(id_bodega.compareTo("")==0){
                try{
                    if(rsBodegas.next()){
                        id_bodega = (rsBodegas.getString("id_bodega")!=null) ? rsBodegas.getString("id_bodega") : "";
                    }
                }catch(Exception e){
                    e.printStackTrace();
                }
            }
            ResultSet rsEstanterias = objBodega.getEstanterias(Integer.valueOf(id_bodega));


            Comun objComun = new Comun(this._ip, this._puerto, this._db, usuario, clave);
            ResultSet rsCategorias = objComun.getComun("CAT-");

            TablaDepreciacion obj_TablaDepreciacion = new TablaDepreciacion(this._ip, this._puerto, this._db, usuario, clave);
            ResultSet rsDepreciacion = obj_TablaDepreciacion.getTablaDepreciaciones();
            String jsonDepresiaciones = obj_TablaDepreciacion.getTablaDepreciacionesJSON();

            String [][] estado1 = {{"B","Bueno"},{"M","Malo"},{"P","P&eacute;simo"}};

            String html = "obj»"+obj+"^foc»codigo^frm»";
            html += "<div class='H3' style='width:450px'>Formulario de Activo</div>";

            html += "<form action='frmActivoGuardar' onsubmit='return act_activoGuardar(this)' autocomplete='off'>";
            html += "<input type='hidden' id='id' name='id' value='"+id+"' />";
            html += "<input type='hidden' id='idIns' name='idIns' value='"+id_instalacion+"' />";
            html += "<input type='hidden' id='tipo_pers' name='tipo_pers' value='6' />";
            

            html += "<table><tr><td>C&oacute;digo: <span class='marca'>*</span> </td>";
            html += "<td nowrap><input class='may' id='codigo' name='codigo' type='text' size='35' maxlength='30' value='"+codigo_activo+"' onblur=\"this.value=this.value._trim();\" /></td></tr>";
            html += "<tr><td>Descripci&oacute;n: <span class='marca'>*</span> </td>";
            html += "<td nowrap><input id='descripcion' name='descripcion' type='text' size='35' maxlength='60' value='"+descripcion+"' onkeypress=\"_alfanumerico(event);\" onblur=\"this.value=this.value._trim();\" /></td></tr>";
            html += "<tr><td>Depreciaci&oacute;n: </td>";
            html += "<td>"+DatosDinamicos.combo(rsDepreciacion, "id_tabla_depreciacion", id_tabla_depreciacion, "act_calcDepresiado()", "", 200)+
                    "<div id='axDep' style='display:none'>"+jsonDepresiaciones+"</div></td></tr>";
            html += "<tr><td>Sucursal: <span class='marca'>*</span> </td>";
            html += "<td>"+DatosDinamicos.combo(rsSucursales, "id_sucursal", id_sucursal, "", "", 200)+"</td></tr>";
            html += "<tr><td>Categor&iacute;a: <span class='marca'>*</span></td>";
            html += "<td><div id='cob0' style='float:left;display:table-cell'>"+DatosDinamicos.combo(rsCategorias, "id_categoria", id_categoria, "", "", 150)+"</div>"
                    + "<div class='jm_icoCon' id='gr0' style='visibility:hidden;background-position: -545px 0px;' onclick=\"guardarComun(null,'cob0','id_categoria','gr0','add0',150,'CAT');\" title='guardar'>&nbsp;</div>"
                    + "<div class='jm_icoCon' id='add0' style='background-position:-504px -14px;' onclick=\"insertarComun('cob0','id_categoria','gr0','add0',150,'CAT');\" title='Insertar'>&nbsp;</div></td></tr>";
            html += "<tr><td>Bodega - almac&eacute;n: <span class='marca'>*</span> </td>";
            html += "<td>"+DatosDinamicos.combo(rsBodegas, "id_bodega", id_bodega, "act_getEstanterias(this.value, 'ubicacion')", "", 200)+"</td></tr>";
            html += "<tr><td nowrap>Ubicaci&oacute;n: <span class='marca'>*</span> </td>";
            html += "<td id='axUbi'>"+DatosDinamicos.combo(rsEstanterias, "ubicacion", ubicacion, "", "", 200)+"</td></tr>";
            html += "<tr><td nowrap>Fecha de compra: <span class='marca'>*</span> </td>";
            html += "<td><input type='text' id='fecha_compra' name='fecha_compra' value='"+fecha_compra+"' size='10' readOnly><input type='button' value='...' onClick=\"SelectorFecha.crear('fecha_compra', 'SQL', 'act_calcDepresiado()');\" /></td></tr>";
            html += "<tr><td nowrap>Marca: </td>";
            html += "<td><input id='marca' name='marca' type='text' size='35' maxlength='40' value='"+marca+"' onkeypress=\"_alfanumerico(event);\" onblur=\"this.value=this.value._trim();\" /></td></tr>";
            html += "<tr><td nowrap>Serie: </td>";
            html += "<td><input id='serie' name='serie' type='text' size='35' maxlength='50' value='"+serie+"' onkeypress=\"_alfanumerico(event);\" onblur=\"this.value=this.value._trim();\" /></td></tr>";
            html += "<tr><td nowrap>Modelo: </td>";
            html += "<td><input id='modelo' name='modelo' type='text' size='35' maxlength='50' value='"+modelo+"' onkeypress=\"_alfanumerico(event);\" onblur=\"this.value=this.value._trim();\" /></td></tr>";
            html += "<tr><td nowrap>N&uacute;mero de partes: </td>";
            html += "<td><input id='num_partes' name='num_partes' type='text' size='35' maxlength='3' value='"+num_partes+"' onkeypress='_numero(event);' /></td></tr>";
            html += "<tr><td nowrap>Color: </td>";
            html += "<td><input id='color' name='color' type='text' size='35' maxlength='20' value='"+color+"' onkeypress=\"_alfanumerico(event);\" onblur=\"this.value=this.value._trim();\" /></td></tr>";
            html += "<tr><td nowrap>Valor de la compra: <span class='marca'>*</span> </td>";
            html += "<td><input id='valor_compra' name='valor_compra' type='text' size='35' maxlength='20' value='"+valor_compra+"' onkeypress=\"_evaluar(event,'0123456789.');\" onkeyup=\"act_calcDepresiado()\" /></td></tr>";
            html += "<tr><td nowrap>Valor depreciado: </td>";
            html += "<td><input id='valor_depreciado' name='valor_depreciado' type='text' size='35' maxlength='20' value='"+valor_depreciado+"' onkeypress=\"_evaluar(event,'0123456789.');\" /></td></tr>";
            html += "<tr><td nowrap>Nro. de meses depreciados: </td>";
            html += "<td><input id='num_meses' name='num_meses' type='text' size='35' maxlength='3' value='"+num_meses+"' onkeypress='_numero(event);' /></td></tr>";
            html += "<tr><td nowrap>Estado: <span class='marca'>*</span> </td>";
            html += "<td>" + DatosDinamicos.combo("estado", estado, estado1, "") + "</td></tr>";
            html += "<tr valign='top'><td>Observaci&oacute;n:</td>";
            html += "<td><textarea id='observacion' name='observacion' rows='2' cols='25' onblur=\"this.value=this.value._trim();\" >"+observacion+"</textarea></td></tr>";
            html += "</table>";

            Rol objRol = new Rol(this._ip, this._puerto, this._db, usuario, clave);
            if(id.compareTo("-1")==0){
                html += "<input type='submit' value='Guardar' />";
            }
            if(obj.compareTo("ppp1")==0){
                html += " &nbsp;&nbsp;&nbsp; <input type='button' value='Cerrar' onclick=\"_R('ppp1');\" />";
            }
            html += "</form>";
            out.println(html);

            try{
                rsSucursales.close();
                rsDepreciacion.close();
                rsCategorias.close();
            }catch(Exception e){
                e.printStackTrace();
            }

            objBodega.cerrar();
            objRol.cerrar();
            objSucursal.cerrar();
            objComun.cerrar();
            obj_TablaDepreciacion.cerrar();
            objActivo.cerrar();
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
