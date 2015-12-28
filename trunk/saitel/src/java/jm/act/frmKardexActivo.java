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

package jm.act;

import java.io.*;

import javax.servlet.*;
import javax.servlet.http.*;
import java.sql.ResultSet;
import jm.act.clas.Activo;
import jm.seg.clas.Rol;

/**
 *
 * @author Jorge
 */
public class frmKardexActivo extends HttpServlet {
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
        Activo objActivo = new Activo(this._ip, this._puerto, this._db, usuario, clave);
        try {
            String id = request.getParameter("id");

            String codigo = "";
            String descripcion = "";
            String marca = "";
            String serie = "";
            String valor_compra = "";
            String valor_depreciado = "";

            ResultSet rsActivo = objActivo.getActivo(id);
            try{
                if(rsActivo.next()){
                    codigo = (rsActivo.getString("codigo_activo")!=null) ? rsActivo.getString("codigo_activo") : "";
                    descripcion = (rsActivo.getString("descripcion")!=null) ? rsActivo.getString("descripcion") : "";
                    marca = (rsActivo.getString("marca")!=null) ? rsActivo.getString("marca") : "";
                    serie = (rsActivo.getString("serie")!=null) ? rsActivo.getString("serie") : "";
                    valor_compra = (rsActivo.getString("valor_compra")!=null) ? rsActivo.getString("valor_compra") : "";
                    valor_depreciado = (rsActivo.getString("valor_depreciado")!=null) ? rsActivo.getString("valor_depreciado") : "";
                    rsActivo.close();
                }
            }catch(Exception e){
                e.printStackTrace();
            }


            out.print("obj»d_21^frm»");


            out.print("<table width='800'><tr><td colspan='6' class='H3'>Kardex de Activo</td></tr>");

            out.print("<tr><td width='60'>C&oacute;digo: </td><td width='400'>"+codigo+"</td>");
            out.print("<td width='40'>Marca: </td><td width='130'>"+marca+"</td>");
            out.print("<td width='100'>Valor compra: </td><td width='70'>"+valor_compra+"</td></tr>");
            out.print("<tr><td>Descripci&oacute;n: </td><td>"+descripcion+"</td>");
            out.print("<td>Serie: </td><td>"+serie+"</td>");
            out.print("<td>Valor depreciado: </td><td>"+valor_depreciado+"</td></tr></table>");

            out.println("<TABLE cellspacing='0' cellpadding='0'><TR>" +
                        "<TH class='jm_TH' width='100'>SUCURSAL</TH>" +
                        "<TH class='jm_TH' width='100'>BODEGA</TH>" +
                        "<TH class='jm_TH' width='200'>UBICACION</TH>" +
                        "<TH class='jm_TH' width='60'>No. Doc.</TH>" +
                        "<TH class='jm_TH' width='60'>FECHA</TH>" +
                        "<TH class='jm_TH' width='270'>DETALLE</TH>"+
                        "</TR></TABLE>");


            out.println("<DIV style='overflow:auto;width:825px;height:300px;'>" +
                    "<TABLE class='jm_tabla' cellspacing='1' cellpadding='0' id='tblI'>");

            try{
                String html = "";
                int i=0;
                ResultSet rsKardex = objActivo.getKardex(id);
                while(rsKardex.next()){
                    html += "<tr valign='top' class='jm_filaPar'>";
                    html += "<td width='101'>"+((rsKardex.getString("sucursal")!=null) ? rsKardex.getString("sucursal") : "")+"</td>";
                    html += "<td width='101'>"+((rsKardex.getString("bodega")!=null) ? rsKardex.getString("bodega") : "")+"</td>";
                    html += "<td width='201'>"+((rsKardex.getString("ubicacion")!=null) ? rsKardex.getString("ubicacion") : "")+"</td>";
                    html += "<td width='61'>"+((rsKardex.getString("num_documento")!=null) ? rsKardex.getString("num_documento") : "")+"</td>";
                    html += "<td width='61'>"+((rsKardex.getString("fecha")!=null) ? rsKardex.getString("fecha") : "")+"</td>";
                    html += "<td width='271'>"+((rsKardex.getString("detalle")!=null) ? rsKardex.getString("detalle") : "")+"</td>";
                    i++;
                }

                out.print(html);
                rsKardex.close();
            }catch(Exception e){
                e.printStackTrace();
            }

            out.println("</table></div>"
                    + "<input type='button' value='Imprimir' onclick=\"imprimir('pdfKardexActivo?id="+id+"')\" />");

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
