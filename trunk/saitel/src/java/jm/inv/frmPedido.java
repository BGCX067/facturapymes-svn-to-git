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

import com.sun.org.apache.xerces.internal.dom.NodeImpl;
import java.io.*;

import javax.servlet.*;
import javax.servlet.http.*;
import jm.web.Fecha;
import jm.seg.clas.Rol;
import jm.inv.clas.Pedido;
import jm.inv.clas.Producto;
import jm.adm.clas.Sucursal;
import java.sql.ResultSet;

/**
 *
 * @author Jorge
 */
public class frmPedido extends HttpServlet {
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

        String id = request.getParameter("id");

        Sucursal objSuc = new Sucursal(this._ip, this._puerto, this._db, usuario, clave);

        Producto objProducto = new Producto(this._ip, this._puerto, this._db, usuario, clave);

        Rol objRol = new Rol(this._ip, this._puerto, this._db, usuario, clave);

        Pedido objPedido = new Pedido(this._ip, this._puerto, this._db, usuario, clave);

        /*Configuracion conf = new Configuracion(this._ip, this._puerto, this._db, usuario, clave);
        String id_sucursal_matriz = conf.getValor("id_sucursal_matriz");
        conf.cerrar();*/

        try {
            ResultSet rsSucursales = objSuc.getSucursales();

            int num_pedido = objPedido.getNumPedido();
            String alias = usuario;
            String fecha_pedido = Fecha.getFecha("SQL");
            String id_sucursal = String.valueOf(id_sucursal_sesion);
            ResultSet rsPedido = objPedido.getPedido(id);
            ResultSet rsPedidoDetalle = objPedido.getPedidoDetalle(id);
            if(id.compareTo("-1")!=0){
                try{
                    if(rsPedido.next()){
                        num_pedido = (rsPedido.getString("num_pedido")!=null) ? rsPedido.getInt("num_pedido") : num_pedido;
                        id_sucursal = (rsPedido.getString("id_sucursal")!=null) ? rsPedido.getString("id_sucursal") : "";
                        alias = (rsPedido.getString("usuario")!=null) ? rsPedido.getString("usuario") : "";
                        fecha_pedido = (rsPedido.getString("fecha_pedido")!=null) ? Fecha.ISOaSQL(rsPedido.getString("fecha_pedido")) : "";
                        rsPedido.close();
                    }

                    objPedido.setRevisado(id);

                    objPedido.cerrar();
                }catch(Exception e){
                    e.printStackTrace();
                }
            }


            String json_productos = "";
            if(id.compareTo("-1")==0){
                ResultSet rsProductos = objProducto.getProductos(id_sucursal_sesion);
                json_productos = objProducto.getJSON(rsProductos);
                try{
                    rsProductos.close();
                }catch(Exception e){
                    e.printStackTrace();
                }
            }


            out.print("fun»inv_buscarPedidos()^obj»cmp_html^foc»dt^frm»"
                    + "<form action='frmPedidoGuardar' onsubmit='return inv_pedidoGuardar(this);' autocomplete='off'>"+
                    "<input id='frm' name='frm' type='hidden' value='p' />" +
                    "<input id='impr' name='impr' type='hidden' value='0' />" +
                    "<input id='numProd' name='numProd' type='hidden' value='1' />" +
                    "<input id='id' name='id' type='hidden' value='"+id+"' />" +
                    "<table width='510'><tr><td align='center'><strong>ORDEN DE PEDIDO DE MERCADERIA</strong></td>" +
                    "<td align='right' width='100'>Nro. <span class='marca'>*</span> <input id='np' name='np' "+((id.compareTo("-1")!=0)?"readonly":"")+" value='"+num_pedido+"' type='text' size='6' onkeypress='_numero(event);' /></td></tr>" +
                    "</table>" +

                    "<table cellspacing='0' cellpadding='0' width='510'>");

                    /*if(Integer.valueOf(id_sucursal_matriz) == id_sucursal_sesion){
                        out.print("<tr><td>Sucursal: "+DatosDinamicos.combo(rsSucursales, "suc", id_sucursal, "", "", 140)+"</td></tr>");
                    }else{*/
                        out.print("<tr><td>Sucursal: <input id='suc' name='suc' type='hidden' size='40' value='"+id_sucursal+"' />"+objSuc.getNombre(id_sucursal)+"</td></tr>");
                    //}
                    out.print("<tr><td>Usuario: <input style='border:0px;' id='usr' name='usr' type='text' size='40' value='"+alias+"' readOnly /></td></tr>" +
                    "<tr><td>Fecha del pedido: <input style='border:0px;' id='fePe' name='fePe' type='text' size='20' value='"+fecha_pedido+"' readOnly /></td></tr>" +

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
                    "<TH class='jm_TH' width='210'>DESCRIPCION</TH>" +
                    "<TH class='jm_TH' width='60'>CANT.</TH>"+
                    "<TH class='jm_TH' width='60'>PRECIO U</TH>"+
                    "<TH class='jm_TH' width='40'>TOTAL</TH>"+
                    "<TH class='jm_TH' width='30'>&nbsp;</TH></tr></table>" +

                    "<DIV style='overflow:auto;width:515px;height:195px;'>" +
                    "<TABLE class='jm_tabla' cellspacing='1' cellpadding='0'><tbody id='tblD'>");

            try{
                String id_pedido_detalle = "";
                String codigo = "";
                String descripcion = "";
                String cantidad = "";
                int i=0;
                String clase = "";
                while(rsPedidoDetalle.next()){
                    clase = i%2==0 ? "jm_filaPar" : "jm_filaImp";
                    id_pedido_detalle = (rsPedidoDetalle.getString("id_pedido_detalle")!=null) ? rsPedidoDetalle.getString("id_pedido_detalle") : "";
                    codigo = (rsPedidoDetalle.getString("codigo")!=null) ? rsPedidoDetalle.getString("codigo") : "";
                    descripcion = (rsPedidoDetalle.getString("descripcion")!=null) ? rsPedidoDetalle.getString("descripcion") : "";
                    cantidad = (rsPedidoDetalle.getString("cantidad")!=null) ? rsPedidoDetalle.getString("cantidad") : "";
                    out.println("<tr id='r"+i+"' class='"+clase+"'>");
                    out.println("<td width='110' id='cF"+i+"0'>"+codigo+"</td>");
                    out.println("<td width='301'><input type='hidden' id='idPeD"+i+"' value='"+id_pedido_detalle+"' /> "+descripcion+"</td>");
                    out.println("<td width='61'>"+cantidad+"</td>");
                    //out.println("<td width='21' align='center'><div class='jm_icoCon' style='background-position:-545px -18px;' title='Eliminar' onclick=\"_R('r"+i+"');\">&nbsp;</div></td>");
                    out.println("<td width='21'>&nbsp;</td>");
                    out.println("</tr>");
                    i++;
                }

                rsPedidoDetalle.close();
            }catch(Exception e){
                e.printStackTrace();
            }

            out.print("</tbody></table></div>");

            out.println("<table class='jm_tabla' width='510' cellspacing='0' cellpadding='0'><tr>");

            if(id.compareTo("-1")==0){
                out.println("<td><input type='submit' value='Enviar' onclick=\"_('impr').value='0';\" /></td>");
                out.println("<td align='right'><input type='submit' value='Enviar e Imprimir' onclick=\"_('impr').value='1';\" /></td>");
            }else{
                out.println("<td><input type='button' value='Re-imprimir Pedido' onclick=\"imprimir('pdfPedido?id="+id+"');\" /></td>");
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
            objPedido.cerrar();
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
