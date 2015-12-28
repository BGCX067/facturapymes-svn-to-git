/**
** @version 1.0
** @package FACTURAPYMES.
* @author Jorge Washington Mueses Cevallos.
* @copyright Copyright (C) 2011 por Jorge Mueses. Todos los derechos reservados.
* @license http://www.gnu.org/copyleft/gpl.html GNU/GPL.
** FACTURAPYMES es un software de libre distribuciÃ³n, que puede ser
* copiado y distribuido bajo los tÃ©rminos de la Licencia
* Attribution-NonCommercial-NoDerivs 3.0 Unported,
* de acuerdo con la publicada por la CREATIVE COMMONS CORPORATION.
*/

package jm.cnt;

import java.io.*;

import javax.servlet.*;
import javax.servlet.http.*;
import jm.cnt.clas.DocumentoAnulado;
import jm.seg.clas.Rol;
import jm.web.DatosDinamicos;
import jm.web.Fecha;
import java.sql.ResultSet;

/**
 *
 * @author Jorge
 */
public class frmDocumentoAnulado extends HttpServlet {
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
        
        try {
            String id = request.getParameter("id");
            String fecha_emision = Fecha.getFecha("SQL");
            String tipo_comprobante = "";
            String serie = "001-001";
            String secuencial = "";
            String autorizacion = "";

            if(id.compareTo("-1")!=0){
                DocumentoAnulado obj_DocumentoAnulado = new DocumentoAnulado(this._ip, this._puerto, this._db, usuario, clave);
                try{
                    ResultSet rsDoc = obj_DocumentoAnulado.getDocumentoAnulado(id);
                    if(rsDoc.next()){
                        fecha_emision = (rsDoc.getString("fecha_emision")!=null) ? rsDoc.getString("fecha_emision") : "";
                        tipo_comprobante = (rsDoc.getString("tipo_comprobante")!=null) ? rsDoc.getString("tipo_comprobante") : "";
                        serie = (rsDoc.getString("serie")!=null) ? rsDoc.getString("serie") : "";
                        secuencial = (rsDoc.getString("secuencial")!=null) ? rsDoc.getString("secuencial") : "";
                        autorizacion = (rsDoc.getString("autorizacion")!=null) ? rsDoc.getString("autorizacion") : "";
                        rsDoc.close();
                    }
                }catch(Exception e){
                    e.printStackTrace();
                }finally{
                    obj_DocumentoAnulado.cerrar();
                }
            }

            String [][] documentos = {{"1","Factura"},{"3","Liquidaci&oacute;n de compra/servicio"},{"4","Nota de cr&eacute;dito"},
                            {"5","Nota de d&eacute;bito"},{"7","Retenci&oacute;n"}};  /*  {"2","Nota de venta"}  */

            String html = "obj»d_21^foc»secuencial^frm»";
            html += "<form action='frmDocumentoAnuladoGuardar' onsubmit='return cnt_anuladoGuardar(this)' autocomplete='off'>";
            html += "<input type='hidden' id='id' name='id' value='"+id+"' />";
            html += "<table><tr><td colspan='2' class='H3'>Formulario de Documento Anulado</td></tr>";
            html += "<tr><td>Fecha de emisi&oacute;n: <span class='marca'>*</span></td>";
            html += "<td><input type='text' id='fecha_emision' name='fecha_emision' value='"+fecha_emision+"' size='10' readonly /><input type='button' value='...' onClick=\"SelectorFecha.crear('fecha_emision', 'SQL');\" /></td></tr>";
            html += "<tr><td nowrap>Tipo de documento: <span class='marca'>*</span></td>";
            html += "<td>"+DatosDinamicos.combo("tipo_comprobante", tipo_comprobante, documentos, "")+"</td></tr>";
            html += "<tr><td nowrap>Serie: <span class='marca'>*</span></td>";
            html += "<td><input id='serie' name='serie' type='text' size='15' maxlength='7' value='"+serie+"' onkeypress=\"_evaluar(event, '0123456789-');\" /></td></tr>";
            html += "<tr><td nowrap>Secuencial: <span class='marca'>*</span></td>";
            html += "<td><input id='secuencial' name='secuencial' type='text' size='15' maxlength='20' value='"+secuencial+"' onkeypress=\"_numero(event);\" /></td></tr>";
            html += "<tr><td nowrap>Autorizaci&oacute;n: <span class='marca'>*</span></td>";
            html += "<td><input id='autorizacion' name='autorizacion' type='text' size='37' maxlength='37' value='"+autorizacion+"' onkeypress=\"_numero(event);\" /></td></tr>";
            if(id.compareTo("-1")==0 || (id.compareTo("-1")!=0 && objRol.getAcceso(id_rol, "anuladoEditar"))){
                html += "<tr><td colspan='2'><input type='submit' value='Guardar' /></td></tr>";
            }
            html += "</table>";
            html += "</form>";
            out.print(html);

        } finally {
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
