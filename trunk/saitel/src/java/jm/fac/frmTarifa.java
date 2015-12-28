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

package jm.fac;

import java.io.*;

import javax.servlet.*;
import javax.servlet.http.*;
import java.sql.ResultSet;
import jm.fac.clas.PlanServicio;
import jm.seg.clas.Rol;

/**
 *
 * @author Jorge
 */
public class frmTarifa extends HttpServlet {
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

        String id_plan_servicio = request.getParameter("idPS");

        Rol objRol = new Rol(this._ip, this._puerto, this._db, usuario, clave);

        PlanServicio objPlanServicio = new PlanServicio(this._ip, this._puerto, this._db, usuario, clave);

        try {
            String html = "obj»cmp_html^frm»";
            try{
                int i=0;
                String plan = "";
                String burst_limit = "";
                String max_limit = "";
                String burst_threshold = "";
                String down_burst_time = "";
                String limit_at = "";
                String up_burst_limit = "";
                String up_max_limit = "";
                String up_burst_threshold = "";
                String up_burst_time = "";
                String up_limit_at = "";
                ResultSet rsVelocidad = objPlanServicio.getVelocidad(id_plan_servicio);
                if(rsVelocidad.next()){
                    plan = (rsVelocidad.getString("plan")!=null) ? rsVelocidad.getString("plan") : "";
                    burst_limit = (rsVelocidad.getString("burst_limit")!=null) ? rsVelocidad.getString("burst_limit") : "";
                    max_limit = (rsVelocidad.getString("max_limit")!=null) ? rsVelocidad.getString("max_limit") : "";
                    burst_threshold = (rsVelocidad.getString("burst_threshold")!=null) ? rsVelocidad.getString("burst_threshold") : "";
                    down_burst_time = (rsVelocidad.getString("burst_time")!=null) ? rsVelocidad.getString("burst_time") : "";
                    limit_at = (rsVelocidad.getString("limit_at")!=null) ? rsVelocidad.getString("limit_at") : "";
                    up_burst_limit = (rsVelocidad.getString("up_burst_limit")!=null) ? rsVelocidad.getString("up_burst_limit") : "";
                    up_max_limit = (rsVelocidad.getString("up_max_limit")!=null) ? rsVelocidad.getString("up_max_limit") : "";
                    up_burst_threshold = (rsVelocidad.getString("up_burst_threshold")!=null) ? rsVelocidad.getString("up_burst_threshold") : "";
                    up_burst_time = (rsVelocidad.getString("up_burst_time")!=null) ? rsVelocidad.getString("up_burst_time") : "";
                    up_limit_at = (rsVelocidad.getString("up_limit_at")!=null) ? rsVelocidad.getString("up_limit_at") : "";
                    
                    html += "<div class='H3'>PLAN "+plan+" " + burst_limit+" Kbps</div>";
                    html += "<table><tr><td>BURST LIMIT: </td><td width='130'>"+up_burst_limit + " / " + burst_limit+"</td>";
                    html += "<td>MAX LIMIT: </td><td>"+up_max_limit+" / "+max_limit+"</td></tr>";
                    html += "<tr><td>BURST THRESHOLD: </td><td>"+up_burst_threshold+" / "+burst_threshold+"</td>";
                    html += "<td>BURST TIME: </td><td>"+up_burst_time+" / "+down_burst_time+"</td></tr>";
                    html += "<tr><td>LIMIT AT: </td><td>"+up_limit_at+" / "+limit_at+"</td></tr></table>";
                    i++;
                }
                rsVelocidad.close();

            }catch(Exception e){e.printStackTrace();}
            


            if(id_plan_servicio.compareTo("-1")==0 || (id_plan_servicio.compareTo("-1")!=0 && objRol.getAcceso(id_rol, "planEditar"))){
                html += "&nbsp;&nbsp;<a href='javascript:void(0);' id='TinRe' onclick=\"fac_tarifaNueva();\">Nueva tarifa vigente</a>";
            }
            html += "<input type='hidden' id='idPlanServicio' value='"+id_plan_servicio+"' />"
                    + "<TABLE cellspacing='0' cellpadding='0'><TR>" +
                    "<TH class='jm_TH' width='120'>VIGENTE DESDE</TH>" +
                    "<TH class='jm_TH' width='120'>HASTA</TH>" +
                    "<TH class='jm_TH' width='60'>VALOR</TH>"+
                    "<TH class='jm_TH' width='70'>&nbsp;</TH></TR></TABLE>";
            html += "<DIV style='overflow:auto;width:395px;height:175px;' id='Tsll'>" +
            "<TABLE class='jm_tabla' cellspacing='1' cellpadding='0' id='TtblI'>";
            try{
                int i = 0;
                String id_tarifa = "";
                String vigente_desde = "";
                String vigente_hasta = "";
                String costo_plan = "";
                ResultSet rsTarifas = objPlanServicio.getTarifas(id_plan_servicio);
                while(rsTarifas.next()){
                    id_tarifa = (rsTarifas.getString("id_tarifa")!=null) ? rsTarifas.getString("id_tarifa") : "";
                    vigente_desde = (rsTarifas.getString("sql_vigente_desde")!=null) ? rsTarifas.getString("sql_vigente_desde") : "";
                    vigente_hasta = (rsTarifas.getString("sql_vigente_hasta")!=null) ? rsTarifas.getString("sql_vigente_hasta") : "";
                    costo_plan = (rsTarifas.getString("costo_plan")!=null) ? rsTarifas.getString("costo_plan") : "";
                    if(id_plan_servicio.compareTo("-1")==0 || (id_plan_servicio.compareTo("-1")!=0 && objRol.getAcceso(id_rol, "planEditar"))){
                        html += "<tr id='TrTI"+i+"' valign='top' class='jm_filaPar'>";
                        html += "<td width='120'><input id='fIni"+i+"' type='text' size='9' value='"+vigente_desde+"' readonly />"
                                + "<input type='button' value='...' onClick=\"SelectorFecha.crear('fIni"+i+"', 'SQL', 'fac_tarifaEditar(event, "+i+");');\" /></td>";
                        html += "<td width='120'><input id='fFin"+i+"' type='text' size='9' value='"+vigente_hasta+"' onkeypress=\"_SUP(event);\" onkeyup=\"fac_tarifaEditar(event, "+i+");\" />"
                                + "<input type='button' value='...' onClick=\"SelectorFecha.crear('fFin"+i+"', 'SQL', 'fac_tarifaEditar(event, "+i+");');\" /></td>";
                        html += "<td width='60'><input id='tar"+i+"' type='text' size='6' value='"+costo_plan+"' onkeypress=\"_evaluar(event, '0123456789.');\" onkeyup=\"fac_tarifaEditar(event, "+i+");\" /></td>";
                        html += "<td align='center' width='70'><input type='hidden' id='idT"+i+"' value='"+id_tarifa+"' />" +
                                "<div class='jm_icoCon' id='Tg"+i+"' style='visibility:hidden;background-position: -545px 0px;' onclick='fac_tarifaGuardar("+i+");' title='guardar'>&nbsp;</div>" +
                                "<div class='jm_icoCon' id='Te"+i+"' style='background-position:-545px -18px;' onclick='fac_tarifaEliminar("+i+");' title='eliminar'>&nbsp;</div></td>" +
                                "</tr>";
                    }else{
                        html += "<tr id='TrTI"+i+"' valign='top' class='jm_filaPar'>";
                        html += "<td width='120'>"+vigente_desde+"</td>";
                        html += "<td width='120'>"+vigente_hasta+"</td>";
                        html += "<td width='60'>"+costo_plan+"</td>";
                        html += "<td align='center' width='70'><input type='hidden' id='idR"+i+"' value='"+id_tarifa+"' />&nbsp;</td></tr>";
                    }
                    i++;
                }
            }catch(Exception e){
                e.printStackTrace();
            }
            html += "</table></div>";

            out.print(html);

        } finally {
            objPlanServicio.cerrar();
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
