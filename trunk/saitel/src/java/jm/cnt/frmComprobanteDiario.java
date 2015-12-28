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

package jm.cnt;

import java.io.*;

import javax.servlet.*;
import javax.servlet.http.*;
import jm.web.DatosDinamicos;
import jm.web.Fecha;
import jm.seg.clas.Rol;
import jm.cnt.clas.PlanCuenta;
import jm.cnt.clas.ComprobanteDiario;
import java.sql.ResultSet;

/**
 *
 * @author Jorge
 */
public class frmComprobanteDiario extends HttpServlet {
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

        String id = request.getParameter("id");

        PlanCuenta objPlanCuenta = new PlanCuenta(this._ip, this._puerto, this._db, usuario, clave);
        ResultSet cuentas = objPlanCuenta.getTablasAsignacion();
        objPlanCuenta.cerrar();

        Rol objRol = new Rol(this._ip, this._puerto, this._db, usuario, clave);

        ComprobanteDiario objComprobanteDiario = new ComprobanteDiario(this._ip, this._puerto, this._db, usuario, clave);
        try {
            String fecha = Fecha.getFecha("SQL");
            String num_comprobante = "";
            //String num_asiento = "";
            String fecha_actual = fecha;
            String fecha_proceso = fecha;
            String total = "0";
            String detalle = "";
            boolean bloqueo = false;
            boolean anulado = false;
            ResultSet nota = objComprobanteDiario.getComprobanteDiario(id);
            ResultSet notaDetalle = objComprobanteDiario.getComprobanteDiarioDetalle(id);
            if(id.compareTo("-1")!=0){
                try{
                    if(nota.next()){
                        num_comprobante = (nota.getString("num_comprobante")!=null) ? nota.getString("num_comprobante") : "";
                        //num_asiento = (nota.getString("num_asiento")!=null) ? nota.getString("num_asiento") : "";
                        fecha_actual = (nota.getString("fecha_actual")!=null) ? Fecha.ISOaSQL(nota.getString("fecha_actual")) : fecha;
                        fecha_proceso = (nota.getString("fecha_proceso")!=null) ? Fecha.ISOaSQL(nota.getString("fecha_proceso")) : fecha;
                        total = (nota.getString("total")!=null) ? nota.getString("total") : "0";
                        detalle = (nota.getString("detalle")!=null) ? nota.getString("detalle") : "";
                        bloqueo = (nota.getString("bloqueo")!=null) ? nota.getBoolean("bloqueo") : false;
                        anulado = (nota.getString("anulado")!=null) ? nota.getBoolean("anulado") : false;
                        nota.close();
                    }
                }catch(Exception e){
                    e.printStackTrace();
                }
            }
            out.println("obj»cmp_html^foc»dt^frm»<div><table width='560'>" +
                    "<tr><td align='center'><strong>C O M P R O B A N T E &nbsp;&nbsp; D E &nbsp;&nbsp; D I A R I O</strong></td>" +
                    "<td align='right' width='100'>Nro. "+num_comprobante+"</td></tr>" +
                    "</table>" +

                    "<table width='100%'>" +

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

                    "<DIV id='sll0' style='overflow:auto;width:575px;height:195px;'>" +
                    "<TABLE class='jm_tabla' cellspacing='1' cellpadding='0' id='tblD'>");

            try{
                String id_plan_cuenta = "";
                String debe = "";
                String haber = "";
                int i=0;
                while(notaDetalle.next()){
                    id_plan_cuenta = (notaDetalle.getString("id_plan_cuenta")!=null) ? notaDetalle.getString("id_plan_cuenta") : "1";
                    debe = (notaDetalle.getString("debe")!=null) ? ((notaDetalle.getDouble("debe")>0)?notaDetalle.getString("debe"):"") : "";
                    haber = (notaDetalle.getString("haber")!=null) ? ((notaDetalle.getDouble("haber")>0)?notaDetalle.getString("haber"):"") : "";
                    out.println("<tr id='r"+i+"' class='jm_filaPar'>");
                    out.println("<td width='350'>" + DatosDinamicos.combo(cuentas, "PC"+i, id_plan_cuenta, "", 335) + "</td>");
                    out.println("<td width='90' align='right'><input id='d"+i+"' name='d"+i+"' type='text' class='inpTbl' size='9' onkeypress=\"_evaluar(event, '0123456789.');_('h"+i+"').value='';\" onkeyup=\"cnt_sumTotales();\" " +
                            "onfocus=\"this.select();\" value='"+debe+"' /></td>");
                    out.println("<td width='90' align='right'><input id='h"+i+"' name='h"+i+"' type='text' class='inpTbl' size='9' onkeypress=\"_evaluar(event, '0123456789.');_('d"+i+"').value='';\" onkeyup=\"cnt_sumTotales();\" " +
                            "onfocus=\"this.select();\" value='"+haber+"' /></td>");
                    out.println("<td width='20' align='center'><div class='jm_icoCon' style='background-position:-545px -18px;' title='Eliminar' onclick=\"_R('r"+i+"');cnt_sumTotales();\">&nbsp;</div></td>");
                    out.println("</tr>");
                    i++;
                }
                if(i==0){
                    out.println("<tr id='r0' class='jm_filaPar'>");
                        out.println("<td width='350'>" + DatosDinamicos.combo(cuentas, "PC0", "", "", 335) + "</td>");
                        out.println("<td width='90' align='right'><input id='d0' name='d0' type='text' class='inpTbl' size='9' onkeypress=\"_evaluar(event, '0123456789.');_('h0').value='';\" onkeyup=\"cnt_sumTotales();\" " +
                                "onfocus=\"this.select();\" /></td>");
                        out.println("<td width='90' align='right'><input id='h0' name='h0' type='text' class='inpTbl' size='9' onkeypress=\"_evaluar(event, '0123456789.');_('d0').value='';\" onkeyup=\"cnt_sumTotales();\" " +
                                "onfocus=\"this.select();\" /></td>");
                        out.println("<td width='20' align='center'><div class='jm_icoCon' style='background-position:-545px -18px;' title='Eliminar' onclick=\"_R('r0');cnt_sumTotales();\">&nbsp;</div></td>");
                        out.println("</tr>");
                }

                notaDetalle.close();
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
            if(!anulado){
                if(!bloqueo){
                    long fecha_sistema = Fecha.getTimeStamp(Fecha.getFecha("SQL"));
                    long fecha_asiento = Fecha.getTimeStamp(fecha_actual);
                    if(id.compareTo("-1")!=0 && objRol.getAcceso(id_rol, "comprobanteAnular") && fecha_sistema==fecha_asiento){
                        out.println("<td><input type='button' value='Anular Comprobante' onclick=\"cnt_anularDocumento('tbl_comprobante_diario', "+id+");\" /></td>");
                    }                    
                    //if(id.compareTo("-1")==0 || (id.compareTo("-1")!=0 && objRol.getAcceso(id_rol, "comprobanteEditar"))){
                    if(id.compareTo("-1")==0){
                        out.println("<td><input type='button' value='Guardar' onclick='cnt_comprobanteDiarioGuardar(0, "+id+")' /></td>");
                        out.println("<td align='right'><input type='button' value='Guardar e Imprimir' onclick='cnt_comprobanteDiarioGuardar(1, "+id+")' /></td>");
                    }else{
                        out.println("<td><input type='button' value='Imprimir' onclick=\"imprimir('pdfComprobanteDiario?id="+id+"');\" /></td>");
                    }
                }else{
                    out.println("<td><input type='button' value='Imprimir' onclick=\"imprimir('pdfComprobanteDiario?id="+id+"');\" /></td>");
                }
            }else{
                out.println("<td><span class='marca'>COMPROBANTE DE DIARIO ANULADO</span></td>");
            }

            out.println("</tr></table>");
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
