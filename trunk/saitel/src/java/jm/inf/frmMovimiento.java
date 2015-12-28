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

package jm.inf;

import java.io.*;

import javax.servlet.*;
import javax.servlet.http.*;
import jm.fac.clas.DocumentoBanco;
import jm.seg.clas.Rol;
import jm.web.Fecha;
import java.sql.ResultSet;

/**
 *
 * @author Jorge
 */
public class frmMovimiento extends HttpServlet {
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
            String fecha = Fecha.getFecha("SQL");
            String concepto = "DEPOSITO";
            String documento = "";
            String monto = "";
            boolean de_banco = false;
            if(id.compareTo("-1")!=0){
                DocumentoBanco obj_DocumentoBanco = new DocumentoBanco(this._ip, this._puerto, this._db, usuario, clave);
                try{
                    ResultSet rsDoc = obj_DocumentoBanco.getMovDocumentoBanco(id);
                    if(rsDoc.next()){
                        fecha = (rsDoc.getString("fecha")!=null) ? rsDoc.getString("fecha") : "";
                        concepto = (rsDoc.getString("concepto")!=null) ? rsDoc.getString("concepto") : "";
                        documento = (rsDoc.getString("documento")!=null) ? rsDoc.getString("documento") : "";
                        monto = (rsDoc.getString("monto")!=null) ? rsDoc.getString("monto") : "";
                        de_banco = (rsDoc.getString("de_banco")!=null) ? rsDoc.getBoolean("de_banco") : false;
                        rsDoc.close();
                    }
                }catch(Exception e){
                    e.printStackTrace();
                }finally{
                    obj_DocumentoBanco.cerrar();
                }
            }


            String html = "obj»d_21^foc»documento^frm»";
            html += "<form action='frmMovimientoGuardar' onsubmit='return fac_movGuardar(this)' autocomplete='off'>";
            html += "<input type='hidden' id='id' name='id' value='"+id+"' />";
            html += "<table><tr><td colspan='2' class='H3'>Formulario de Movimiento</td></tr>";
            html += "<tr><td>Fecha: <span class='marca'>*</span></td>";
            html += "<td><input type='text' id='fecha' name='fecha' value='"+fecha+"' size='10' readonly /><input type='button' value='...' onClick=\"SelectorFecha.crear('fecha', 'SQL');\" /></td></tr>";
            html += "<tr><td nowrap>Concepto: <span class='marca'>*</span></td>";
            html += "<td><input id='concepto' name='concepto' type='text' size='40' maxlength='40' value='"+concepto+"' /></td></tr>";
            html += "<tr><td nowrap>Documento: <span class='marca'>*</span></td>";
            html += "<td><input id='documento' name='documento' type='text' size='15' maxlength='20' value='"+documento+"' onkeypress=\"_evaluar(event, '0123456789.');\" /></td></tr>";
            html += "<tr><td nowrap>Monto: <span class='marca'>*</span></td>";
            html += "<td><input id='monto' name='monto' type='text' size='15' maxlength='20' value='"+monto+"' onkeypress=\"_evaluar(event, '0123456789.');\" /></td></tr>";
            if(!de_banco && (id.compareTo("-1")==0 || (id.compareTo("-1")!=0 && objRol.getAcceso(id_rol, "docBancoMovEditar"))) ){
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
