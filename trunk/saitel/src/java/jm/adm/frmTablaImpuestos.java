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

package jm.adm;

import java.io.*;

import javax.servlet.*;
import javax.servlet.http.*;
import jm.adm.clas.TablaImpuesto;
import java.sql.ResultSet;
import jm.cnt.clas.PlanCuenta;
import jm.web.DatosDinamicos;
import jm.web.Fecha;

/**
 *
 * @author Jorge
 */
public class frmTablaImpuestos extends HttpServlet {
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

        String id = request.getParameter("id");
        int altB = Integer.parseInt(request.getParameter("altB"));
        String [][] tipo = {{"RENTA","RENTA"},{"IVA","IVA"}};

        PlanCuenta objPlanCuenta = new PlanCuenta(this._ip, this._puerto, this._db, usuario, clave);
        ResultSet cuentas = objPlanCuenta.getTablasAsignacion();

        TablaImpuesto objParametro = new TablaImpuesto(this._ip, this._puerto, this._db, usuario, clave);
        boolean editable = objParametro.editable(id);
        try {
            String fecha_vigencia_ini = Fecha.getAnio() + "-01-01";
            String fecha_vigencia_fin = "";
            if(id.compareTo("-1")!=0){
                try{
                    ResultSet rsvigencias = objParametro.getVigencia(id);
                    if(rsvigencias.next()){
                        fecha_vigencia_ini = (rsvigencias.getString("vigente_desde")!=null) ? Fecha.ISOaSQL(rsvigencias.getString("vigente_desde")) : fecha_vigencia_ini;
                        fecha_vigencia_fin = (rsvigencias.getString("vigente_hasta")!=null) ? Fecha.ISOaSQL(rsvigencias.getString("vigente_hasta")) : "";
                        rsvigencias.close();
                    }
                }catch(Exception e){
                    e.printStackTrace();
                }
            }

            out.print("obj»d_21^frm»<form action='frmTablaImpuestosGuardar' onsubmit='return adm_impuestoGuardar(this)' autocomplete='off'>");
            out.print("<div style=\"display:none\" id='ax'>"+ DatosDinamicos.combo(cuentas, "_Xx_", "", "", " SIN ASIGNACION ", 100)+"</div>");
            out.print("<input type='hidden' id='id' name='id' value='" + id + "'>");
            out.print("<table cellspacing='0' cellpadding='0'>");
            out.print("<tr><td colspan='2' class='H3' height='16'>Formulario de Tabla de Impuestos</td></tr>");
            out.print("<tr><td width='190'>Fecha de vigencia de inicio: <span class='marca'>*</span> </td>");
            out.print("<td width='529'><input id='fi' name='fi' type='text' size='10' readonly value='"+fecha_vigencia_ini+"' />" +
                    "<input type='button' value='...' onClick=\"SelectorFecha.crear('fi', 'SQL');\"/></td></tr>");
            out.print("<tr><td nowrap>Fecha de vigencia de t&eacute;rmino: </td>");
            out.print("<td nowrap> <input id='ff' name='ff' type='text' size='10' maxlength='10' onkeypress='_SUP(event);' value='"+fecha_vigencia_fin+"' />" +
                    "<input type='button' value='...' onClick=\"SelectorFecha.crear('ff', 'SQL');\"/></td></tr>");
            out.print("<tr><td><input type='submit' value='Guardar' /></td></tr></table></form>");

            if(id.compareTo("-1")!=0){
                try{
                    if(editable){
                        out.println("<hr />&nbsp;&nbsp;&nbsp;<a href='javascript:void(0);' id='inRe' onclick=\"adm_retencionNueva();\">Insertar Retenci&oacute;n</a>");
                    }
                    out.println("<div id='auxCon' style='width:654px;'>" +
                            "<TABLE cellspacing='0' cellpadding='0'><TR>" +
                            "<TH class='jm_TH' width='80'>IMP.</TH>" +
                            "<TH class='jm_TH' width='70'>C&Oacute;DIGO</TH>" +
                            "<TH class='jm_TH' width='160'>CONCEPTO</TH>" +
                            "<TH class='jm_TH' width='60'>% RET.</TH>" +
                            "<TH class='jm_TH' width='100'>CUENTA EN COMPRAS</TH>" +
                            "<TH class='jm_TH' width='100'>CUENTA EN VENTAS</TH>" +
                            "<TH class='jm_TH' width='70'>ACCIONES</TH></TR></TABLE>");

                    String html = "";
                    html += "<DIV style='overflow:auto;width:670px;height:"+(altB-350)+"px;' id='sll'>" +
                            "<TABLE class='jm_tabla' cellspacing='1' cellpadding='0' id='tblI'>";
                    int i=0;
                    String id_tabla_impuesto = "";
                    String impuesto = "";
                    String id_plan_cuenta_compra = "";
                    String id_plan_cuenta_venta = "";
                    ResultSet rsInpuestos = objParametro.getImpuestos(id);
                    while(rsInpuestos.next()){
                        id_tabla_impuesto = (rsInpuestos.getString("id_tabla_impuesto")!=null) ? rsInpuestos.getString("id_tabla_impuesto") : "";
                        impuesto = (rsInpuestos.getString("impuesto")!=null) ? rsInpuestos.getString("impuesto") : "";
                        id_plan_cuenta_compra = (rsInpuestos.getString("id_plan_cuenta_compra")!=null) ? rsInpuestos.getString("id_plan_cuenta_compra") : "";
                        id_plan_cuenta_venta = (rsInpuestos.getString("id_plan_cuenta_venta")!=null) ? rsInpuestos.getString("id_plan_cuenta_venta") : "";
                        if(editable){
                            html += "<tr id='rTI"+i+"' valign='top' class='jm_filaPar'>";
                            html += "<td width='80'>"+DatosDinamicos.combo("i"+i, impuesto, tipo, "adm_retencionEditar(event, "+i+");")+"</td>";
                            html += "<td width='70'><input id='c"+i+"' type='text' style='border:0px;' size='7' maxlength='5' " +
                                    "onkeypress=\"_evaluar(event, '0123456789ABCDEFGHIJKLMNÑOPQRSTUVWXYZ');\" onkeyup=\"adm_retencionEditar(event, "+i+");\" value='"+
                                    ((rsInpuestos.getString("codigo_impuesto")!=null) ? rsInpuestos.getString("codigo_impuesto") : "")+"' /></td>";
                            html += "<td width='160'><textarea id='d"+i+"' rows='1' style='width:155px;border:0px;' onblur=\"this.value=this.value._trim();\" onkeyup=\"adm_retencionEditar(event, "+i+");\" >"+
                                    ((rsInpuestos.getString("concepto")!=null) ? rsInpuestos.getString("concepto") : "")+"</textarea></td>";
                            html += "<td align='right' width='60'><input id='p"+i+"' type='text' class='inpTbl' size='6' maxlength='5' " +
                                    "onkeypress=\"_evaluar(event, '0123456789.');\" onkeyup=\"adm_retencionEditar(event, "+i+");\" value='"+
                                    ((rsInpuestos.getString("porcentaje_retencion")!=null) ? rsInpuestos.getString("porcentaje_retencion") : "")+"' /></td>";
                            html += "<td width='100'>"+DatosDinamicos.combo(cuentas, "idPcC"+i, id_plan_cuenta_compra, "adm_retencionEditar(event, "+i+");", " SIN ASIGNACION ", 100)+"</td>";
                            html += "<td width='100'>"+DatosDinamicos.combo(cuentas, "idPcV"+i, id_plan_cuenta_venta, "adm_retencionEditar(event, "+i+");", " SIN ASIGNACION ", 100)+"</td>";
                            html += "<td align='center' width='70'><input type='hidden' id='idR"+i+"' value='"+id_tabla_impuesto+"' />" +
                                    "<div class='jm_icoCon' id='g"+i+"' style='visibility:hidden;background-position: -545px 0px;' onclick='adm_retencionGuardar("+i+");' title='guardar'>&nbsp;</div>" +
                                    "<div class='jm_icoCon' id='e"+i+"' style='background-position:-545px -18px;' onclick='adm_retencionEliminar("+i+");' title='eliminar'>&nbsp;</div></td>" +
                                    "</tr>";
                        }else{
                            html += "<tr id='rTI"+i+"' valign='top' class='jm_filaPar'>";
                            html += "<td width='80'>"+DatosDinamicos.combo("i"+i, impuesto, tipo, "")+"</td>";
                            html += "<td width='70'><input id='c"+i+"' type='text' style='border:0px;' size='7' maxlength='5' " +
                                    "onkeypress=\"_evaluar(event, '0123456789-');\" value='"+
                                    ((rsInpuestos.getString("codigo_impuesto")!=null) ? rsInpuestos.getString("codigo_impuesto") : "")+"' /></td>";
                            html += "<td width='160'><textarea id='d"+i+"' rows='1' cols='31' style='width:155px;border:0px;' >"+
                                    ((rsInpuestos.getString("concepto")!=null) ? rsInpuestos.getString("concepto") : "")+"</textarea></td>";
                            html += "<td align='right' width='60'><input id='p"+i+"' type='text' class='inpTbl' size='6' maxlength='5' " +
                                    "onkeypress=\"_evaluar(event, '0123456789.');\" value='"+
                                    ((rsInpuestos.getString("porcentaje_retencion")!=null) ? rsInpuestos.getString("porcentaje_retencion") : "")+"' /></td>";
                            html += "<td width='100'>"+DatosDinamicos.combo(cuentas, "idPcC"+i, id_plan_cuenta_compra, "adm_retencionEditar(event, "+i+");", " SIN ASIGNACION ", 100)+"</td>";
                            html += "<td width='100'>"+DatosDinamicos.combo(cuentas, "idPcV"+i, id_plan_cuenta_venta, "adm_retencionEditar(event, "+i+");", " SIN ASIGNACION ", 100)+"</td>";
                            html += "<td align='center' width='70'><input type='hidden' id='idR"+i+"' value='"+id_tabla_impuesto+"' />&nbsp;</td>" +
                                    "</tr>";
                        }
                        i++;
                    }
                    html += "</table></div></div>";

                    out.print(html);
                    rsInpuestos.close();
                }catch(Exception e){
                    e.printStackTrace();
                }
            }
        } finally {
            objParametro.cerrar();
            objPlanCuenta.cerrar();
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
