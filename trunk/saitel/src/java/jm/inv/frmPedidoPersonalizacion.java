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

import jm.act.*;
import java.io.*;

import javax.servlet.*;
import javax.servlet.http.*;
import jm.web.DatosDinamicos;
import jm.web.Fecha;
import jm.seg.clas.Rol;
import java.sql.ResultSet;
import java.text.DecimalFormat;
import jm.act.clas.Activo;
import jm.adm.clas.Configuracion;
import jm.adm.clas.Documento;
import jm.seg.clas.Usuario;

/**
 *
 * @author Jorge
 */
public class frmPedidoPersonalizacion extends HttpServlet {
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
        String empleado = (String)sesion.getAttribute("empleado");
        String clave = (String)sesion.getAttribute("clave");
        int id_sucursal_sesion = (Integer)sesion.getAttribute("sucursal");

        response.setContentType("text/html;charset=UTF-8");
        response.setHeader("Pragma", "no-cache");
        response.setHeader("Expires", "Mon, 01 Jan 2001 00:00:01 GMT");
        response.setHeader("Cache-Control", "no-store");
        response.setHeader("Cache-Control", "must-revalidate");
        response.setHeader("Cache-Control", "no-cache");
        PrintWriter out = response.getWriter();

        String id = request.getParameter("id");
        
        Rol objRol = new Rol(this._ip, this._puerto, this._db, usuario, clave);

        Configuracion conf = new Configuracion(this._ip, this._puerto, this._db, usuario, clave);
        String ruc_empresa = conf.getValor("ruc");
        String razon_social_empresa = conf.getValor("razon_social");
        conf.cerrar();

        Usuario objUsuario = new Usuario(this._ip, this._puerto, this._db, usuario, clave);
        boolean lector = objUsuario.lector(usuario);
        objUsuario.cerrar();

        String observacion = "";
        try{
            Documento objDocumento = new Documento(this._ip, this._puerto, this._db, usuario, clave);
            ResultSet rsDoc = objDocumento.getDocumento("p");
            if(rsDoc.next()){
                observacion = rsDoc.getString("documento")!=null ? rsDoc.getString("documento") : "";
                rsDoc.close();
            }
            objDocumento.cerrar();
        }catch(Exception e){}

        Activo objActivo = new Activo(this._ip, this._puerto, this._db, usuario, clave);

        try {
            String num_documento = String.valueOf(objActivo.getNunDocumento());
            int id_sucursal = id_sucursal_sesion;
            String tipo_movimiento = "3";
            String dni_entrega = "";
            String persona_entrega = "";
            String bodega_entrega = "";
            String ubicacion_entrega = "";
            String ubicacion_recibe = "";
            String dni_recibe = "";
            String persona_recibe = "";
            String bodega_recibe = "";
            String g_envio = "";
            String fecha = Fecha.getFecha("SQL");
            boolean anulado = false;
            boolean aceptada = false;
            
            String id_pedido = "";
            String id_producto = "";
            String cantidad = "";
            if(id.compareTo("-1")!=0){
                try{
                    ResultSet rsDocumento = objActivo.getPedido(id,usuario);
                    if(rsDocumento.next()){
                        /*num_documento = (rsDocumento.getString("num_documento")!=null) ? rsDocumento.getString("num_documento") : "";
                        tipo_movimiento = (rsDocumento.getString("tipo_movimiento")!=null) ? rsDocumento.getString("tipo_movimiento") : "";
                        id_sucursal = (rsDocumento.getString("id_sucursal")!=null) ? rsDocumento.getInt("id_sucursal") : id_sucursal;
                        dni_entrega = (rsDocumento.getString("dni_entrega")!=null) ? rsDocumento.getString("dni_entrega") : "";
                        persona_entrega = (rsDocumento.getString("persona_entrega")!=null) ? Fecha.ISOaSQL(rsDocumento.getString("persona_entrega")) : "";*/
                        bodega_entrega = (rsDocumento.getString("us")!=null) ? rsDocumento.getString("us") : "";
                        /*bodega_recibe = (rsDocumento.getString("bodega_recibe")!=null) ? rsDocumento.getString("bodega_recibe") : "";*/
                        bodega_recibe = (rsDocumento.getString("nombre")!=null) ? rsDocumento.getString("nombre") : "";
                        /*ubicacion_entrega = (rsDocumento.getString("ubicacion_entrega")!=null) ? rsDocumento.getString("ubicacion_entrega") : "";
                        ubicacion_recibe = (rsDocumento.getString("ubicacion_recibe")!=null) ? rsDocumento.getString("ubicacion_recibe") : "";
                        dni_recibe = (rsDocumento.getString("dni_recibe")!=null) ? rsDocumento.getString("dni_recibe") : "";
                        persona_recibe = (rsDocumento.getString("persona_recibe")!=null) ? rsDocumento.getString("persona_recibe") : "";
                        fecha = (rsDocumento.getString("fecha")!=null) ? Fecha.ISOaSQL(rsDocumento.getString("fecha")) : "";
                        observacion = (rsDocumento.getString("observacion")!=null) ? rsDocumento.getString("observacion") : "";
                        anulado = (rsDocumento.getString("anulado")!=null) ? rsDocumento.getBoolean("anulado") : false;
                        aceptada = (rsDocumento.getString("aceptada")!=null) ? rsDocumento.getBoolean("aceptada") : false;*/
                        rsDocumento.close();
                    }
                }catch(Exception e){
                    e.printStackTrace();
                }
            }

            //String [][] tipo_movimiento1 = {{"2","Interpersonal"},{"3","Entre bodegas"},{"4","De bodega a personal"},
            //                                {"5","De personal a bodega"},{"6","De bodega a cliente"},{"7","De cliente a bodega"}};
            
            String [][] tipo_movimiento1 = {{"1","Compra"},{"2","Interpersonal"},{"3","Entre bodegas"},{"4","De bodega a personal"},
                                            {"5","De personal a bodega"},{"6","De bodega a cliente"},{"7","De cliente a bodega"},
                                            {"9","De bodega a infraestructura"}, {"10","De infraestructura a bodega"}};

            //String [][] tipo_movimiento1 = {{"1","Compra"},{"2","Interpersonal"},{"3","Entre bodegas"},{"4","De bodega a personal"},
              //                              {"5","De personal a bodega"},{"6","De bodega a cliente"},{"7","De cliente a bodega"},{"8","Venta"}};

            /*String json_Activos = "";
            ResultSet rsActivos = objActivo.getActivos(id_sucursal_sesion);
            json_Activos = objActivo.getJSON(rsActivos);
            try{
                rsActivos.close();
            }catch(Exception e){
                e.printStackTrace();
            }
            
            String visible="visible";
            String readOnly="";
            if(tipo_movimiento.compareTo("c")==0){
                visible="hidden";
                readOnly="readOnly";
            }*/


            out.print("obj»cmp_html^foc»bodega_entrega^frm»"
                    + "<form action='frmPersonalizacionGuardar' onsubmit='return act_personalizacionGuardar(this);' autocomplete='off'>"+
                    "<input id='id' name='id' type='hidden' value='-1' />" +
                    "<input id='idBodEnt' name='idBodEnt' type='hidden' value='' />" +
                    "<input id='idBodRec' name='idBodRec' type='hidden' value='' />" +
                    "<input id='rucEmp' name='rucEmp' type='hidden' value='"+ruc_empresa+"' />" +
                    "<input id='raSoEmp' name='raSoEmp' type='hidden' value='"+razon_social_empresa+"' />" +
                    "<input id='imp' name='imp' type='hidden' value='1' />" +
                    "<input id='tope' name='tope' type='hidden' value='0' />" +

                    "<table width='795'><tr><td align='center' colspan='2'><strong>FORMULARIO DE MOVIMIENTO DE ACTIVOS POR PEDIDO</strong></td>" +
                    "<tr><td>Fecha: <input type='text' id='fch' name='fch' value='"+fecha+"' size='10' readOnly><input type='button' value='...' onClick=\"SelectorFecha.crear('fch', 'SQL');\" /></td>" +
                    "<td align='right'>Nro. <span class='marca'>*</span> <input id='nd' name='nd' "+((id.compareTo("-1")!=0)?"readonly":"")+" value='"+num_documento+"' type='text' size='6' onkeypress='_numero(event);' /></td></tr>" +
                    "</table>" +

                    "<fieldset style='width:775px'>Tipo de movimiento (Traslado): " + DatosDinamicos.combo("tpyMov", tipo_movimiento, tipo_movimiento1, "act_setTipoMov()") + "</fieldset>" +

                    "<table cellspacing='0' cellpadding='0'>" +
                    "<tr><th style='background-color:#F4EAD5;' width='395'> ENTREGA</th>"
                    + "<th style='background-color:#CCEBFF;' width='395'> RECIBE</th></tr></table>");

            out.print("<table cellspacing='0' cellpadding='0' id='axBod'><tr><td style='background-color:#F4EAD5;' width='110'>Bodega: </td>"+
                    "<td style='background-color:#F4EAD5;' width='285' id='ax_bodega_entrega'><input id='bodega_entrega' name='bodega_entrega' type='text' size='40' maxlength='60' value='"+bodega_entrega+"' class='may' onkeydown='_NoE(event)' onkeyup=\"act_getBodega(this.value, 'bodega_entrega');\" />"
                    + "<div id='axUbEnt'>"+ubicacion_entrega+"</div></td>" +
                    "<td style='background-color:#CCEBFF;' width='110'>Bodega: </td>"+
                    "<td style='background-color:#CCEBFF;' width='285' id='ax_bodega_recibe'><input id='bodega_recibe' name='bodega_recibe' type='text' size='40' maxlength='60' value='"+bodega_recibe+"' class='may' onkeydown='_NoE(event)' onkeyup=\"act_getBodega(this.value, 'bodega_recibe');\" />"
                    + "<div id='axUbRec'>"+ubicacion_recibe+"</div></td></tr></table>");
            
            out.print("<table cellspacing='0' cellpadding='0'><tr><td style='background-color:#F4EAD5;' width='110'>D.N.I.: <span class='marca'>*</span></td>"+
                    "<td style='background-color:#F4EAD5;' width='285' id='ax_dni_ent'><input id='dni_ent' name='dni_ent' type='text' size='40' maxlength='13' value='"+dni_entrega+"' class='may' onkeydown='_NoE(event)' onkeypress='_DNI(event);' onkeyup=\"act_getEmpleado(this.value, 'dni_ent');\" /></td>" +
                    "<td style='background-color:#CCEBFF;' width='110'>D.N.I.: <span class='marca'>*</span></td>"+
                    "<td style='background-color:#CCEBFF;' width='285' id='ax_dni_rec' ><input id='dni_rec' name='dni_rec' type='text' size='40' maxlength='13' value='"+dni_recibe+"' class='may' onkeydown='_NoE(event)' onkeypress='_DNI(event);' onkeyup=\"act_getEmpleado(this.value, 'dni_rec');\" /></td></tr>" +
                    "<tr><td style='background-color:#F4EAD5;'>Responsable actual: </td>"+
                    "<td style='background-color:#F4EAD5;'><input id='perEnt' name='perEnt' type='text' size='40' maxlength='100' value='"+persona_entrega+"' readonly /></td>"+
                    "<td style='background-color:#CCEBFF;'>Nuevo Responsable: </td>"+
                    "<td style='background-color:#CCEBFF;'><input id='perRec' name='perRec' type='text' size='40' maxlength='100' value='"+persona_recibe+"' readonly /></td></tr>" +
                    "</table><br />");

            if(!anulado && id.compareTo("-1")==0 || (id.compareTo("-1")!=0 && objRol.getAcceso(id_rol, "personalEditar"))){
                out.print("<div><div id='InAr'>B&uacute;squeda de activos: &nbsp; <input type='text' id='busProd' onfocus=\"this.value=''\" style='width:310px;border-color:red' onkeydown='_NoE(event)' onkeyup=\"act_getActivos(event, this.value, 'act_tblActivos', '');\" />"
                        + " &nbsp; Lector de c&oacute;digo de barras <input type='checkbox' id='lector' "+(lector ? "checked='checked'" : "")+" onclick='inv_setLector()' />" +
                        " &nbsp; Gestion de Envio <input type='text' id='g_envio' name='g_envio'  value='"+g_envio+"' style='width:90px;border-color:blue' onkeypress=\"_evaluar(event,'1234567890.');\"/>" +
                    "<span style='float:right;' class='marca' id='di'></span></div>" +
                    "&nbsp;&nbsp;<div style=\"display:none\" id='ax'></div>");
            }else{
                out.print("<br />");
            }

            out.print("<table id='cab0' cellpadding='0' cellspacing='0'><tr>" +
                    "<TH class='jm_TH' width='150'>CODIGO</TH>" +
                    "<TH class='jm_TH' width='300'>DESCRIPCION</TH>" +
                    "<TH class='jm_TH' width='70'>SUBTOTAL</TH>" +
                    "<TH class='jm_TH' width='70'>IVA</TH>" +
                    "<TH class='jm_TH' width='70'>PRECIO TOTAL</TH>" +
                    "<TH class='jm_TH' width='70'>COSTO REAL</TH>" +
                    "<TH class='jm_TH' width='20'>&nbsp;</TH></tr></table>" +

                    "<DIV id='sll0' style='overflow:auto;width:785px;height:115px;'>" +
                    "<TABLE class='jm_tabla' cellspacing='1' cellpadding='0'><tbody id='tblD'>");
            
            try{
                /*ResultSet rsDocumentoDetalle = null;
                if(aceptada){
                    rsDocumentoDetalle = objActivo.getDocumentoDetalle(id);
                }else{
                    rsDocumentoDetalle = objActivo.getDocumentoDetalleTmp(id);
                }*/
                ResultSet rsPedidoDetalle = objActivo.getPedidoDetalle(id);
                String id_activo = "";
                String codigo = "";
                String descripcion = "";
                String valor_compra = "";
                String iva = "";
                String valor_depreciado = "";
                int i=0;
                String clase = "";
                while(rsPedidoDetalle.next()){
                    id_pedido = (rsPedidoDetalle.getString("id_pedido")!=null) ? rsPedidoDetalle.getString("id_pedido") : "";
                    id_producto = (rsPedidoDetalle.getString("id_producto")!=null) ? rsPedidoDetalle.getString("id_producto") : "";
                    cantidad = (rsPedidoDetalle.getString("cantidad")!=null) ? rsPedidoDetalle.getString("cantidad") : "";
                    ResultSet rsPedidoActivo = objActivo.getPedidoActivo(id_producto,usuario,cantidad);
                    while(rsPedidoActivo.next()){
                        clase = i%2==0 ? "jm_filaPar" : "jm_filaImp";
                        DecimalFormat df = new DecimalFormat("0.00"); 
                        id_activo = (rsPedidoActivo.getString("id_activo")!=null) ? rsPedidoActivo.getString("id_activo") : "";
                        codigo = (rsPedidoActivo.getString("codigo_activo")!=null) ? rsPedidoActivo.getString("codigo_activo") : "";
                        descripcion = (rsPedidoActivo.getString("descripcion")!=null) ? rsPedidoActivo.getString("descripcion") : "";
                        valor_compra = (rsPedidoActivo.getString("valor_compra")!=null) ? rsPedidoActivo.getString("valor_compra") : "";
                        iva = (rsPedidoActivo.getString("iva")!=null) ? rsPedidoActivo.getString("iva") : "";
                        valor_depreciado = (rsPedidoActivo.getString("valor_depreciado")!=null) ? rsPedidoActivo.getString("valor_depreciado") : "";
                        out.print("<tr id='r"+i+"' class='"+clase+"'>");
                        out.print("<td width='200' id='cF"+i+"0'>"+codigo+"</td>");
                        out.print("<td width='300'>"+descripcion+"<input type='hidden' id='idAc"+i+"' name='idAc"+i+"' value='"+id_activo+"' /></td>");
                    out.print("<td width='71'>"+valor_compra+"<input type='hidden' id='ValCom"+i+"' name='ValCom"+i+"' value='"+id_activo+"' /></td>");
                    out.print("<td width='71'>"+df.format(Double.parseDouble(iva))+"</td>");
                    out.print("<td width='71'>"+df.format(Double.parseDouble(valor_compra)+(Double.parseDouble(iva)))+"</td>");
                    out.print("<td width='71'>"+(df.format(Double.parseDouble(valor_compra)-Double.parseDouble(valor_depreciado)))+"<input type='hidden' id='ValDep"+i+"' name='ValDep"+i+"' value='"+id_activo+"' /></td>");
                    out.print("<td width='21' align='center'><div class='jm_icoCon' style='background-position:-545px -18px;' title='Eliminar' onclick=\"_R('r"+i+"');\">&nbsp;</div></td>");
                        out.print("</tr>");
                        i++;
                    }
                    try{
                        rsPedidoActivo.close();
                    }catch(Exception e){
                        e.printStackTrace();
                    }
                }

                try{
                    rsPedidoDetalle.close();
                }catch(Exception e){
                    e.printStackTrace();
                }
            }catch(Exception e){
                e.printStackTrace();
            }

            out.print("</tbody></table></div>");

            out.print("<br/><div style='text-align:center'>CLAUSULA DE COMPROMISO</div>");
            out.print("<div><textarea id='obs' name='obs' style='width:790px;height:60px'>"+observacion+"</textarea></div>");

            out.print("<table class='jm_tabla' width='98%' cellspacing='0' cellpadding='0'><tr>");
            out.print("<td><input type='submit' value='Guardar' onclick=\"_('imp').value='1';\" /></td>");

            /*if(!anulado){
                //if(id.compareTo("-1")==0 || (id.compareTo("-1")!=0 && objRol.getAcceso(id_rol, "personalEditar"))){
                if(id.compareTo("-1")==0){
                    out.print("<td><input type='submit' value='Guardar' onclick=\"_('imp').value='0';\" /></td>");
                    //out.print("<td><input type='submit' value='Guardar e Imprimir Documento' onclick=\"_('imp').value='1';\" /></td>");
                }else{
                    //if(persona_recibe.toLowerCase().compareTo(empleado.toLowerCase())==0 && !aceptada){
                    if(!aceptada){
                        out.print("<td><input type='button' value='Aceptar personalizaci&oacute;n' onclick=\"act_personalizacionAceptar("+id+")\" /></td>");
                    }
                    
                    out.print("<td align='right'><input type='button' value='Imprimir documento' onclick=\"imprimir('pdfPersonalizacion?id="+id+"')\" /></td>");
                }
                /*if(id.compareTo("-1")!=0 && objRol.getAcceso(id_rol, "personalAnular")){
                    out.print("<td align='right'><input type='button' value='Anular documento' onclick=\"act_anularPersonalizacion("+id+");\" /></td>");
                }*/
            /*}else{
                out.print("<td><span class='marca'>DOCUMENTO ANULADO</span></td>");
            }*/
            out.print("</tr></table></form>");

        } finally {
            objActivo.cerrar();
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
