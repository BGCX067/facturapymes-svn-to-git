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
import jm.cnt.clas.PlanCuenta;
import jm.cnt.clas.ComprobanteDiario;
import java.sql.ResultSet;
import jm.inv.clas.Producto;

/**
 *
 * @author Jorge
 */
public class frmKardexAjuste extends HttpServlet {
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

        String idProducto = request.getParameter("id");

        PlanCuenta objPlanCuenta = new PlanCuenta(this._ip, this._puerto, this._db, usuario, clave);
        ResultSet cuentas = objPlanCuenta.getTablasAsignacion();
        objPlanCuenta.cerrar();

        Producto objProducto = new Producto(this._ip, this._puerto, this._db, usuario, clave);
        ResultSet rsProducto = objProducto.getProducto(idProducto);
        String precio_costo = "0";
        try{
            if(rsProducto.next()){
                precio_costo = (rsProducto.getString("precio_costo")!=null) ? rsProducto.getString("precio_costo") : "";
                rsProducto.close();
            }
        }catch(Exception e){
            e.printStackTrace();
        }finally{
            objProducto.cerrar();
        }

        Rol objRol = new Rol(this._ip, this._puerto, this._db, usuario, clave);

        ComprobanteDiario objComprobanteDiario = new ComprobanteDiario(this._ip, this._puerto, this._db, usuario, clave);
        try {
            String fecha = Fecha.getFecha("SQL");
            int num_comprobante = objComprobanteDiario.getNumComprobante();
            String fecha_actual = fecha;
            String fecha_proceso = fecha;
            String total = "0";
            String detalle = "";
            
            String [][] ajuste = {{"s","Salida"},{"e","Entrada"}};

            out.println("obj»cmp_html^foc»dt^frm»"
                    + "<form action='frmKardexAjusteGuardar' onsubmit='return inv_kardexAjusteGuardar(this)' autocomplete='off'>"
                    + "<div><input type='hidden' id='typ' value='d' />"
                    + "<div><input type='hidden' id='imp' name='imp' value='1' />"
                    + "<div><input type='hidden' id='idPr' name='idPr' value='"+idProducto+"' />"
                    + "<table width='560'>" +
                    "<tr><td align='center'><strong>C O M P R O B A N T E &nbsp;&nbsp; D E &nbsp;&nbsp; D I A R I O</strong></td>" +
                    "<td align='right' width='100'>Nro. <span class='marca'>*</span> <input id='nc' name='nc' value='"+num_comprobante+"' type='text' size='6' onkeypress='_numero(event);' /></td></tr>" +
                    "</table>" +

                    "<table width='100%'>" +
                    
                    "<tr><td>Cantidad <span class='marca'>*</span> : <input id='can' name='can' value='1' type='text' size='10' maxlength='10' onkeyup=\"_('vt').value=_RD(parseInt(_('can').value) * parseFloat(_('vu').value))\" /></td>"
                    + "<td>Valor Unitario <span class='marca'>*</span> : <input id='vu' name='vu' value='"+precio_costo+"' type='text' size='10' maxlength='10' onkeyup=\"_('vt').value=_RD(parseInt(_('can').value) * parseFloat(_('vu').value))\" /></td></tr>" +
                    "<tr><td>Valor Total <span class='marca'>*</span> : <input id='vt' name='vt' value='"+precio_costo+"' type='text' size='10' maxlength='10' /></td>"
                    + "<td>Tipo de ajuste: " + DatosDinamicos.combo("ajuste", "", ajuste, "") + "</td></tr>" +
                    "<tr><td colspan='2'><hr /></td></tr>" +

                    "<tr><td>Fecha actual: "+fecha_actual+"</td>" +
                    "<td align='right'>Fecha de proceso: <input id='fp' name='fp' type='text' size='10' value='"+fecha_proceso+"' readOnly />" +
                    "<input type='button' value='...' onClick=\"SelectorFecha.crear('fp', 'SQL');\" /></td></tr>" +

                    "<tr><td colspan='2'>Concepto: <span class='marca'>*</span> <input id='dt' name='dt' value='"+detalle+"' type='text' size='80' maxlength='200' /></td></tr>" +

                    "</table>" +

                    "<div><a href='javascript:void(0);' style='float:left;' onclick=\"cnt_addCuenta();\">Insertar Cuenta</a> " +
                    "<span style='float:right;' class='marca' id='di'></span></div>" +
                    "&nbsp;&nbsp;<div style=\"display:none\" id='ax'>"+ DatosDinamicos.combo(cuentas, "_Xx_", "", "", 335)+"</div>" +
                    "<table cellpadding='0' cellspacing='0'>" +
                    "<TH class='jm_TH' width='350'>CODIGO - CUENTA</TH>" +
                    "<TH class='jm_TH' width='90'>DEBE</TH>" +
                    "<TH class='jm_TH' width='90'>HABER</TH>" +
                    "<TH class='jm_TH' width='20'>&nbsp;&nbsp;</TH></tr></table>" +

                    "<DIV style='overflow:auto;width:560px;height:100px;'>" +
                    "<TABLE class='jm_tabla' cellspacing='1' cellpadding='0' id='tblD'>");

            try{
                out.println("<tr id='r0' class='jm_filaPar'>");
                    out.println("<td width='350'>" + DatosDinamicos.combo(cuentas, "PC0", "", "", 335) + "</td>");
                    out.println("<td width='90' align='right'><input id='d0' name='d0' type='text' class='inpTbl' size='9' onkeypress=\"_evaluar(event, '0123456789.');_('h0').value='';\" onkeyup=\"cnt_sumTotales();\" " +
                            "onfocus=\"this.select();\" /></td>");
                    out.println("<td width='90' align='right'><input id='h0' name='h0' type='text' class='inpTbl' size='9' onkeypress=\"_evaluar(event, '0123456789.');_('d0').value='';\" onkeyup=\"cnt_sumTotales();\" " +
                            "onfocus=\"this.select();\" /></td>");
                    out.println("<td width='20' align='center'><div class='jm_icoCon' style='background-position:-545px -18px;' title='Eliminar' onclick=\"_R('r0');cnt_sumTotales();\">&nbsp;</div></td>");
                    out.println("</tr>");

                cuentas.close();
            }catch(Exception e){
                e.printStackTrace();
            }

            out.println("</table></div>" +
                    "<table cellpadding='0' cellspacing='0'>" +
                    "<tr><td align='right' width='350' class='marca'>TOTALES: &nbsp;&nbsp;</td>" +
                    "<td id='td' name='td' align='right' width='90' class='marca'> "+total+" </td>" +
                    "<td id='th' name='th' align='right' width='90' class='marca'> "+total+" </td>" +
                    "<td width='20'>&nbsp;&nbsp;</td></tr></table></div>");

            out.println("<hr><table class='jm_tabla' width='100%' cellspacing='0' cellpadding='0'><tr>");
            out.println("<td><input type='submit' value='Guardar' onclick=\"_('imp').value=0;inv_kardexAjusteGuardar();\" /></td>");
            out.println("<td align='right'><input type='submit' value='Guardar e Imprimir' onclick=\"_('imp').value=1;inv_kardexAjusteGuardar();\" /></td>");

            out.println("</tr></table></form>");
        } finally {
            objComprobanteDiario.cerrar();
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
