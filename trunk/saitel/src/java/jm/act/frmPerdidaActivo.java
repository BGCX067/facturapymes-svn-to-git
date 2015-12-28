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
import java.sql.ResultSet;

import javax.servlet.*;
import javax.servlet.http.*;
import jm.act.clas.Activo;
import jm.seg.clas.Rol;
import jm.seg.clas.Usuario;


    // <editor-fold defaultstate='collapsed' desc='HttpServlet methods. Click on the + sign on the left to edit the code.'>
/**
 *
 * @author Jorge
 */
public class frmPerdidaActivo extends HttpServlet {
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
        //int id_sucursal = (Integer)sesion.getAttribute("sucursal");
        String usuario = (String)sesion.getAttribute("usuario");
        String clave = (String)sesion.getAttribute("clave");

        response.setContentType("text/html;charset=UTF-8");
        response.setHeader("Pragma", "no-cache");
        response.setHeader("Expires", "Mon, 01 Jan 2001 00:00:01 GMT");
        response.setHeader("Cache-Control", "no-store");
        response.setHeader("Cache-Control", "must-revalidate");
        response.setHeader("Cache-Control", "no-cache");
        PrintWriter out = response.getWriter();

        String id_activo_perdida = request.getParameter("id");
        boolean contabilizado = false;

        Rol objRol = new Rol(this._ip, this._puerto, this._db, usuario, clave);

        Usuario objUsuario = new Usuario(this._ip, this._puerto, this._db, usuario, clave);
        boolean lector = objUsuario.lector(usuario);

        try{
            out.print("obj»cmp_html^foc»busProd^frm»");

            out.print("<form id='formVenta' action='frmPerdidaActivoGuardar' onsubmit='return act_perdidaGuardar(this)' autocomplete='off'>");
            out.print("<input type='hidden' id='id_activo_perdida' name='id_activo_perdida' value='"+id_activo_perdida+"' />");
            out.print("<input type='hidden' id='id_activo' name='id_activo' value='-1' />");
            out.print("<input type='hidden' id='descripcion' name='descripcion' value='' />");
            out.print("<input type='hidden' id='perdida' name='perdida' value='0' />");

            out.print("<div class='H3'>P E R D I D A &nbsp;&nbsp; D E &nbsp;&nbsp; A C T I V O </div>");

            if(id_activo_perdida.compareTo("-1")==0){
                out.print("<div id='InAr'><div id='InAr'>B&uacute;squeda de activos: &nbsp; <input type='text' id='busProd' onfocus=\"this.value=''\" style='width:200px;border-color:red' onkeydown='_NoE(event)' onkeyup=\"act_getActivos(event, this.value, 'act_tblActivoPerdida');\" />"
                + " &nbsp; Lector de c&oacute;digo de barras <input type='checkbox' id='lector' "+(lector ? "checked='checked'" : "")+" onclick='inv_setLector()' /></div></div>" +
                "&nbsp;&nbsp;<div style=\"display:none\" id='ax'></div>");
                out.println("<div id='axAct'></div>");
            }else{
                Activo objActivo = new Activo(this._ip, this._puerto, this._db, usuario, clave);
                try{
                    ResultSet rs = objActivo.getVajaActivo(id_activo_perdida);
                    if(rs.next()){
                        out.println("<table width='100%'>");
                        out.println("<tr><td width='30%'>Usuario: </td><td width='70%'>"+(rs.getString("usuario")!=null ? rs.getString("usuario") : "")+"</td></tr>");
                        out.println("<tr><td>Fecha: </td><td>"+(rs.getString("fecha_registro")!=null ? rs.getString("fecha_registro") : "")+"</td></tr>");
                        out.println("<tr><td colspan='2'><hr /></td></tr>");
                        out.println("<tr><td>C&oacute;digo: </td><td>"+(rs.getString("codigo_activo")!=null ? rs.getString("codigo_activo") : "")+"</td></tr>");
                        out.println("<tr><td>Descripci&oacute;n: </td><td>"+(rs.getString("descripcion")!=null ? rs.getString("descripcion") : "")+"</td></tr>");
                        out.println("<tr><td>Valor de la compra: </td><td>"+(rs.getString("valor_compra")!=null ? rs.getString("valor_compra") : "")+"</td></tr>");
                        out.println("<tr><td>Valor depreciado: </td><td>"+(rs.getString("valor_depreciado")!=null ? rs.getString("valor_depreciado") : "")+"</td></tr>");
                        out.println("<tr><td>Valor p&eacute;rdida</td><td>"+(rs.getString("valor_perdida")!=null ? rs.getString("valor_perdida") : "")+"</td></tr>");
                        out.println("<tr><td>Motivo: </td><td>"+(rs.getString("motivo")!=null ? rs.getString("motivo") : "")+"</td></tr>");
                        out.println("</table>");
                        contabilizado = rs.getString("contabilizado")!=null ? rs.getBoolean("contabilizado") : false;
                        rs.close();
                    }
                }catch(Exception e){
                    e.printStackTrace();
                }finally{
                    objActivo.cerrar();
                }
            }

            out.println("<p>");
            if(id_activo_perdida.compareTo("-1")==0 && objRol.getAcceso(id_rol, "perdida_activo_registrar")){
                out.print("<input type='submit' id='grVaj' value='Guardar' />");
            }
            if(id_activo_perdida.compareTo("-1")!=0 && !contabilizado && objRol.getAcceso(id_rol, "perdida_activo_anular")){
                out.print("<input type='button' value='Anular' onclick='act_perdidaAnular("+id_activo_perdida+")' />");
            }
            out.print("</p></form>");

        } finally {
            objUsuario.cerrar();
            objRol.cerrar();
            out.close();
        }
    }
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
