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
import jm.web.DatosDinamicos;
import jm.web.Fecha;
import jm.seg.clas.Rol;
import jm.inv.clas.Traspaso;
import jm.inv.clas.Producto;
import jm.adm.clas.Sucursal;
import java.sql.ResultSet;
import jm.adm.clas.Configuracion;

/**
 *
 * @author Jorge
 */
public class frmTraspaso extends HttpServlet {
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
        int id_sucursal = (Integer)sesion.getAttribute("sucursal");
        String usuario = (String)sesion.getAttribute("usuario");
        String clave = (String)sesion.getAttribute("clave");

        response.setContentType("text/html;charset=UTF-8");
        response.setHeader("Pragma", "no-cache");
        response.setHeader("Expires", "Mon, 01 Jan 2001 00:00:01 GMT");
        response.setHeader("Cache-Control", "no-store");
        response.setHeader("Cache-Control", "must-revalidate");
        response.setHeader("Cache-Control", "no-cache");
        PrintWriter out = response.getWriter();

        String id = request.getParameter("id");

        Sucursal objSuc = new Sucursal(this._ip, this._puerto, this._db, usuario, clave);

        Producto objProducto = new Producto(this._ip, this._puerto, this._db, usuario, clave);

        Rol objRol = new Rol(this._ip, this._puerto, this._db, usuario, clave);

        Traspaso objTraspaso = new Traspaso(this._ip, this._puerto, this._db, usuario, clave);

        Configuracion conf = new Configuracion(this._ip, this._puerto, this._db, usuario, clave);
        String id_sucursal_matriz = conf.getValor("id_sucursal_matriz");
        conf.cerrar();

        try {
            ResultSet rsSucursales = objSuc.getSucursales();

            int num_traspaso = objTraspaso.getNumTraspaso();
            String id_suc_origen = String.valueOf(id_sucursal);
            String usuario_origen = usuario;
            String fecha_envio = Fecha.getFecha("SQL");
            String id_suc_recepcion = "";
            String usuario_recepcion = "";
            String fecha_recepcion = "";
            String fecha_recepcion1 = "";
            String nombre_suc_recepcion = "";
            ResultSet rsTraspaso = objTraspaso.getTraspaso(id);
            ResultSet rsTraspasoDetalle = objTraspaso.getTraspasoDetalle(id);
            if(id.compareTo("-1")!=0){
                try{
                    if(rsTraspaso.next()){
                        num_traspaso = (rsTraspaso.getString("num_traspaso")!=null) ? rsTraspaso.getInt("num_traspaso") : num_traspaso;
                        id_suc_origen = (rsTraspaso.getString("id_suc_origen")!=null) ? rsTraspaso.getString("id_suc_origen") : "";
                        usuario_origen = (rsTraspaso.getString("usuario_origen")!=null) ? rsTraspaso.getString("usuario_origen") : "";
                        fecha_envio = (rsTraspaso.getString("fecha_envio")!=null) ? Fecha.ISOaSQL(rsTraspaso.getString("fecha_envio")) : "";
                        id_suc_recepcion = (rsTraspaso.getString("id_suc_recepcion")!=null) ? rsTraspaso.getString("id_suc_recepcion") : "";
                        usuario_recepcion = (rsTraspaso.getString("usuario_recepcion")!=null) ? rsTraspaso.getString("usuario_recepcion") : "";
                        fecha_recepcion = (rsTraspaso.getString("fecha_recepcion")!=null) ? Fecha.ISOaSQL(rsTraspaso.getString("fecha_recepcion")) : "";
                        fecha_recepcion1 = fecha_recepcion;
                        if(fecha_recepcion.compareTo("")==0){
                            usuario_recepcion = usuario;
                            fecha_recepcion = Fecha.getFecha("SQL");
                        }

                        nombre_suc_recepcion = objSuc.getNombre(id_suc_recepcion);

                        rsTraspaso.close();
                    }
                    objTraspaso.cerrar();
                    
                }catch(Exception e){
                    e.printStackTrace();
                }
            }

            String json_productos = "";
            if(id.compareTo("-1")==0){
                ResultSet rsProductos = objProducto.getProductos(id_sucursal);
                json_productos = objProducto.getJSON(rsProductos);
                try{
                    rsProductos.close();
                }catch(Exception e){
                    e.printStackTrace();
                }
            }

            
            out.print("obj»cmp_html^foc»dt^frm»"
                    + "<form action='frmTraspasoGuardar' onsubmit='return inv_traspasoGuardar(this);' autocomplete='off'>"+
                    "<input id='frm' name='frm' type='hidden' value='t' />" +
                    "<input id='impr' name='impr' type='hidden' value='0' />" +
                    "<input id='numProd' name='numProd' type='hidden' value='1' />" +
                    "<input id='id' name='id' type='hidden' value='"+id+"' />" +
                    "<table width='560'><tr><td align='center'><strong>ORDEN DE TRASPASO DE MERCADERIA</strong></td>" +
                    "<td align='right' width='100'>Nro. <span class='marca'>*</span> <input id='nt' name='nt' "+((id.compareTo("-1")!=0)?"readonly":"")+" value='"+num_traspaso+"' type='text' size='6' onkeypress='_numero(event);' /></td></tr>" +
                    "</table>" +

                    "<table cellspacing='0' cellpadding='0' width='560'>" +
                    "<tr><th style='background-color:#F4EAD5;'>ORIGEN</th><th style='background-color:#CCEBFF;'>RECEPCION</th></tr>");

                if(Integer.valueOf(id_sucursal_matriz) == id_sucursal && id.compareTo("-1")==0){
                    out.print("<tr><td style='background-color:#F4EAD5;'>Sucursal: "+DatosDinamicos.combo(rsSucursales, "or", id_suc_origen, "", "", 140)+"</td>");
                }else{
                    out.print("<tr><td style='background-color:#F4EAD5;'>Sucursal: <input id='or' name='or' type='hidden' value='"+id_suc_origen+"' />"+objSuc.getNombre(id_suc_origen)+"</td>");
                }
            
                    
            out.print("<td style='background-color:#CCEBFF;'>Sucursal: "+(nombre_suc_recepcion.compareTo("")!=0 ? nombre_suc_recepcion : DatosDinamicos.combo(rsSucursales, "re", id_suc_recepcion, "", "", 140))+"</td></tr>" +
                    "<tr><td style='background-color:#F4EAD5;'>Usuario: <input style='background-color:#F4EAD5;border:0px;' id='usOr' name='usOr' type='text' size='20' value='"+usuario_origen+"' readOnly /></td>" +
                    "<td style='background-color:#CCEBFF;'>Usuario: <input style='background-color:#CCEBFF;border:0px;' id='usRe' name='usRe' type='text' size='20' value='"+usuario_recepcion+"' readOnly /></td></tr>" +
                    "<tr><td style='background-color:#F4EAD5;'>Fecha: <input style='background-color:#F4EAD5;border:0px;' id='feOr' name='feOr' type='text' size='20' value='"+fecha_envio+"' readOnly /></td>" +
                    "<td style='background-color:#CCEBFF;'>Fecha: <input style='background-color:#CCEBFF;border:0px;' id='feRe' name='feRe' type='text' size='20' value='"+fecha_recepcion+"' readOnly /></td></tr>" +
                    
                    "</table>");

            if(id.compareTo("-1")==0){
                out.println("<div><a href='javascript:void(0);' style='float:left;' onclick=\"filtrarProducto();\">Insertar Producto</a> " +
                    "<span style='float:right;' class='marca' id='di'></span></div>" +
                    "&nbsp;&nbsp;<div style=\"display:none\" id='ax'>"+json_productos+"</div>");
            }else{
                out.println("<br />");
            }
            
            out.print("<table cellpadding='0' cellspacing='0'><tr>" +
                    "<TH class='jm_TH' width='110'>CODIGO</TH>" +
                    "<TH class='jm_TH' width='300'>DESCRIPCION</TH>" +
                    "<TH class='jm_TH' width='60'>CANTIDAD</TH>"+ 
                    "<TH class='jm_TH' width='60'>VALOR U</TH>"+ 
                    "<TH class='jm_TH' width='20'>&nbsp;</TH></tr></table>" +

                    "<DIV style='overflow:auto;width:575px;height:195px;'>" +
                    "<TABLE class='jm_tabla' cellspacing='1' cellpadding='0'><tbody id='tblD'>");

            try{
                String id_traspaso_detalle = "";
                String codigo = "";
                String descripcion = "";
                String cant_enviada = "";
                String cant_recibida = "";
                int i=0;
                String clase = "";
                while(rsTraspasoDetalle.next()){
                    clase = i%2==0 ? "jm_filaPar" : "jm_filaImp";
                    id_traspaso_detalle = (rsTraspasoDetalle.getString("id_traspaso_detalle")!=null) ? rsTraspasoDetalle.getString("id_traspaso_detalle") : "";
                    codigo = (rsTraspasoDetalle.getString("codigo")!=null) ? rsTraspasoDetalle.getString("codigo") : "";
                    descripcion = (rsTraspasoDetalle.getString("descripcion")!=null) ? rsTraspasoDetalle.getString("descripcion") : "";
                    cant_enviada = (rsTraspasoDetalle.getString("cant_enviada")!=null) ? rsTraspasoDetalle.getString("cant_enviada") : "";
                    cant_recibida = (rsTraspasoDetalle.getString("cant_recibida")!=null) ? rsTraspasoDetalle.getString("cant_recibida") : "";
                    out.println("<tr id='r"+i+"' class='"+clase+"'>");
                    out.println("<td width='110' id='cF"+i+"0'>"+codigo+"</td>");
                    out.println("<td width='301'><input type='hidden' id='idTrD"+i+"' value='"+id_traspaso_detalle+"' /> "+descripcion+"</td>");
                    out.println("<td width='61'>"+cant_enviada+"</td>");
                    out.println("<td width='61'><input id='ctRec"+i+"' name='ctRec"+i+"' type='text' size='5' onkeypress='_numero(event);' value='"+(fecha_recepcion1.compareTo("")==0 ? cant_enviada: cant_recibida)+"' /></td>");
                    //out.println("<td width='21' align='center'><div class='jm_icoCon' style='background-position:-545px -18px;' title='Eliminar' onclick=\"_R('r"+i+"');\">&nbsp;</div></td>");
                    out.println("<td width='21'>&nbsp;</td>");
                    out.println("</tr>");
                    i++;
                }
                rsTraspasoDetalle.close();
            }catch(Exception e){
                e.printStackTrace();
            }

            out.print("</tbody></table></div>");

            out.println("<table class='jm_tabla' width='560' cellspacing='0' cellpadding='0'><tr>");

            if(id.compareTo("-1")!=0 && objRol.getAcceso(id_rol, "traspasoRecibir") && fecha_recepcion1.compareTo("")==0 && id_sucursal==Integer.parseInt(id_suc_recepcion)){
                out.println("<td><input type='button' value='Recibir Traspaso' onclick=\"inv_traspasoRecibir("+id+");\" /></td>");
            }
            if(id.compareTo("-1")==0){
                out.println("<td><input type='submit' value='Enviar' onclick=\"_('impr').value='0';\" /></td>");
                out.println("<td align='right'><input type='submit' value='Enviar e Imprimir' onclick=\"_('impr').value='1';\" /></td>");
            }else{
                out.println("<td><input type='button' value='Re-imprimir Traspaso' onclick=\"imprimir('pdfTraspaso?id="+id+"');\" /></td>");
            }

            try{
                rsSucursales.close();
            }catch(Exception e){
                e.printStackTrace();
            }

            out.println("</tr></table></form>");
        } finally {
            objProducto.cerrar();
            objSuc.cerrar();
            objTraspaso.cerrar();
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
