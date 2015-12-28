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
import jm.adm.clas.Configuracion;
import jm.inv.clas.Producto;
import jm.seg.clas.Rol;
import jm.web.Fecha;

/**
 *
 * @author Jorge
 */
public class frmKardex extends HttpServlet {
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

        Rol objRol = new Rol(this._ip, this._puerto, this._db, usuario, clave);
        Producto objProducto = new Producto(this._ip, this._puerto, this._db, usuario, clave);
        
        try {
            String fecha = Fecha.getFecha("SQL");
            String id = request.getParameter("id");
            String id_sucursal = request.getParameter("idSuc");
            String fecha_ini = request.getParameter("fi")!=null ? request.getParameter("fi") : fecha;
            String fecha_fin = request.getParameter("ff")!=null ? request.getParameter("ff") : fecha;
            int altoBody = Integer.parseInt(request.getParameter("altB"));

            String codigo = "";
            String descripcion = "";
            String unidad_medida = "";
            String stock_min = "";
            String stock = "";
            
            
            ResultSet rsProducto = objProducto.getProducto(id);
            try{
                if(rsProducto.next()){
                    codigo = (rsProducto.getString("codigo")!=null) ? rsProducto.getString("codigo") : "";
                    descripcion = (rsProducto.getString("descripcion")!=null) ? rsProducto.getString("descripcion") : "";
                    unidad_medida = (rsProducto.getString("unidad_medida")!=null) ? rsProducto.getString("unidad_medida") : "";
                    stock_min = (rsProducto.getString("stock_min")!=null) ? rsProducto.getString("stock_min") : "";
                    stock = (rsProducto.getString("stock")!=null) ? rsProducto.getString("stock") : "";
                    rsProducto.close();
                }
            }catch(Exception e){
                e.printStackTrace();
            }

            int ancho_ajuste = 60;

            Configuracion conf = new Configuracion(this._ip, this._puerto, this._db, usuario, clave);
            String metodo_costeo = conf.getValor("metodo_costeo");
            conf.cerrar();

            String metodo_costeo1 = metodo_costeo;
            if(metodo_costeo1.compareTo("PROM")==0){
                metodo_costeo1 = "PROMEDIO PONDERADO";
            }
            if(metodo_costeo1.compareTo("COST")==0){
                metodo_costeo1 = "AL COSTO";
                ancho_ajuste = 0;
            }

            out.print("obj»d_21^frm»");
            
            
            out.print("<table><tr><td colspan='6' class='H3'>Kardex de Producto</td></tr>");
            
            out.print("<tr><td colspan='4'>Fecha desde: <input type='text' id='fi' value='"+Fecha.ISOaSQL(fecha_ini)+"' size='10' readOnly><input type='button' value='...' onClick=\"SelectorFecha.crear('fi', 'SQL');\" />"
                    + " &nbsp; hasta: <input type='text' id='ff' value='"+Fecha.ISOaSQL(fecha_fin)+"' size='10' readOnly><input type='button' value='...' onClick=\"SelectorFecha.crear('ff', 'SQL');\" />"
                    + " &nbsp; <input type='button' value='Actualizar' onclick=\"inv_verKardex(" + id + ");\" /></td>");
            if(objRol.getAcceso(id_rol, "kardexAjuste")){
                out.print("<td><input type='button' value='Ajuste de kardex' onclick=\"inv_kardexAjuste(" + id + ");\" /></td>");
            }
            out.print("<td><input style='float:right' type='button' value='Imprimir' onclick=\"inv_impKardex(" + id + ");\" /></td></tr>");

            out.print("<tr><td colspan='6'><hr /></td></tr>");
            out.print("<tr><td width='50'>Unidad: </td><td width='210'>"+unidad_medida+"</td>");
            out.print("<td width='60'>M&eacute;todo: </td><td width='210'>"+metodo_costeo1+"</td>");
            out.print("<td width='60'>M&iacute;nimo: </td><td width='"+(210-ancho_ajuste)+"'>"+stock_min+"</td></tr>");
            out.print("<tr><td>C&oacute;digo: </td><td>"+codigo+"</td>");
            out.print("<td>Producto: </td><td>"+descripcion+"</td>");
            out.print("<td>Stock: </td><td>"+stock+"</td></tr></table>");

            out.println("" +
                        "<TABLE cellspacing='0' cellpadding='0'><TR>" +
                        "<TH class='jm_TH' rowspan='2' width='70'>FECHA</TH>" +
                        "<TH class='jm_TH' rowspan='2' width='200'>DETALLE</TH>" +
                        "<TH class='jm_TH' colspan='3'>ENTRADAS</TH>" +
                        "<TH class='jm_TH' colspan='3'>SALIDAS</TH>" +
                        "<TH class='jm_TH' colspan='3'>SALDOS</TH>");
            if(metodo_costeo.compareTo("COST")==0){
                    out.println("<TH class='jm_TH' rowspan='2' width='60'>AJUSTE</TH>");
            }
            out.println("</TR><TR>" +
                    "<TH class='jm_TH' width='60'>C.</TH>" +
                    "<TH class='jm_TH' width='60'>V.U.</TH>" +
                    "<TH class='jm_TH' width='60'>V.T.</TH>" +
                    "<TH class='jm_TH' width='60'>C.</TH>" +
                    "<TH class='jm_TH' width='60'>V.U.</TH>" +
                    "<TH class='jm_TH' width='60'>V.T.</TH>" +
                    "<TH class='jm_TH' width='60'>C.</TH>" +
                    "<TH class='jm_TH' width='60'>V.U.</TH>" +
                    "<TH class='jm_TH' width='60'>V.T.</TH>" +
                    "</TR></TABLE>");

            
            out.println("<DIV style='overflow:auto;width:"+850+"px;height:"+(altoBody-360)+"px;'>" +
                    "<TABLE class='jm_tabla' cellspacing='1' cellpadding='0' id='tblI'>");

            try{
                String html = "";
                int i=0;
                ResultSet rsKardex = null;
                if(id_sucursal.compareTo("-0")!=0){
                    rsKardex = objProducto.getKardexSucursal(id_sucursal, id, fecha_ini, fecha_fin);
                }else{
                    rsKardex = objProducto.getKardex(id, fecha_ini, fecha_fin);
                }

                String cantidad = "";
                String costo_unitario = "";
                String costo_total = "";
                boolean es_entrada = true;
                while(rsKardex.next()){
                    cantidad = (rsKardex.getString("cantidad")!=null) ? rsKardex.getString("cantidad") : "";
                    costo_unitario = (rsKardex.getString("costo_unitario")!=null) ? rsKardex.getString("costo_unitario") : "";
                    costo_total = (rsKardex.getString("costo_total")!=null) ? rsKardex.getString("costo_total") : "";
                    es_entrada = (rsKardex.getString("es_entrada")!=null) ? rsKardex.getBoolean("es_entrada") : false;
                    html += "<tr valign='top' class='jm_filaPar'>";
                    html += "<td width='70'>"+((rsKardex.getString("fecha")!=null) ? rsKardex.getString("fecha") : "")+"</td>";
                    html += "<td width='200'>"+((rsKardex.getString("detalle")!=null) ? rsKardex.getString("detalle") : "")+"</td>";
                    html += "<td width='61' align='center'>"+((es_entrada) ? cantidad : "")+"</td>";
                    html += "<td width='61' align='right'>"+((es_entrada) ? costo_unitario : "")+"</td>";
                    html += "<td width='61' align='right'>"+((es_entrada) ? costo_total : "")+"</td>";
                    html += "<td width='61' align='center'>"+((!es_entrada) ? cantidad : "")+"</td>";
                    html += "<td width='61' align='right'>"+((!es_entrada) ? costo_unitario : "")+"</td>";
                    html += "<td width='61' align='right'>"+((!es_entrada) ? costo_total : "")+"</td>";
                    html += "<td width='61' align='center'>"+((rsKardex.getString("saldo_cantidad")!=null) ? rsKardex.getString("saldo_cantidad") : "")+"</td>";
                    html += "<td width='61' align='right'>"+((rsKardex.getString("saldo_costo_unitario")!=null) ? rsKardex.getString("saldo_costo_unitario") : "")+"</td>";
                    html += "<td width='61' align='right'>"+((rsKardex.getString("saldo_costo_total")!=null) ? rsKardex.getString("saldo_costo_total") : "")+"</td>";
                    if(metodo_costeo.compareTo("COST")==0){
                        html += "<td width='61' align='right'>"+((rsKardex.getString("precio_unitario_ajuste")!=null) ? rsKardex.getString("precio_unitario_ajuste") : "")+"</td>";
                    }
                    i++;
                }
                
                out.print(html);
                rsKardex.close();
            }catch(Exception e){
                e.printStackTrace();
            }

            out.println("</table></div>");

        } finally {
            objProducto.cerrar();
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
