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
import jm.inf.clas.NivelSoporte;
import jm.fac.clas.PlanServicio;
import jm.inv.clas.Producto;
import jm.seg.clas.Rol;
import jm.web.DatosDinamicos;

/**
 *
 * @author Jorge
 */
public class frmPlanServicio extends HttpServlet {
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

        Producto obj_Producto = new Producto(this._ip, this._puerto, this._db, usuario, clave);
        ResultSet rsProductos = obj_Producto.getProductos();
        
        Rol objRol = new Rol(this._ip, this._puerto, this._db, usuario, clave);

        PlanServicio objPlanServicio = new PlanServicio(this._ip, this._puerto, this._db, usuario, clave);

        /*PlanCuenta objPlanCuenta = new PlanCuenta(this._ip, this._puerto, this._db, usuario, clave);
        ResultSet cuentas = objPlanCuenta.getTablasAsignacion();*/

        NivelSoporte objNivelSoporte = new NivelSoporte(this._ip, this._puerto, this._db, usuario, clave);
        ResultSet rsNivelSoporte = objNivelSoporte.getNivelesSoporte();

        String alto = request.getParameter("altB");
        int _altBody = Integer.parseInt(alto);

        try {
            String id = request.getParameter("id");
            String plan = "";
            String id_producto = "";
            String id_producto_anticipo = "";
            String comparticion = "";
            String id_nivel_soporte = "";
            String div_max_limit = "";
            String div_burst_threshold = "";
            String burst_time = "";
            String sim_subida = "";
            if(id.compareTo("-1")!=0){
                try{
                    ResultSet rsplan = objPlanServicio.getPlan(id);
                    if(rsplan.next()){
                        plan = (rsplan.getString("plan")!=null) ? rsplan.getString("plan") : "";
                        id_producto = (rsplan.getString("id_producto")!=null) ? rsplan.getString("id_producto") : "";
                        id_producto_anticipo = (rsplan.getString("id_producto_anticipo")!=null) ? rsplan.getString("id_producto_anticipo") : "";
                        comparticion = (rsplan.getString("comparticion")!=null) ? rsplan.getString("comparticion") : "";
                        id_nivel_soporte = (rsplan.getString("id_nivel_soporte")!=null) ? rsplan.getString("id_nivel_soporte") : "";
                        div_max_limit = (rsplan.getString("div_max_limit")!=null) ? rsplan.getString("div_max_limit") : "";
                        div_burst_threshold = (rsplan.getString("div_burst_threshold")!=null) ? rsplan.getString("div_burst_threshold") : "";
                        burst_time = (rsplan.getString("burst_time")!=null) ? rsplan.getString("burst_time") : "";
                        sim_subida = (rsplan.getString("sim_subida")!=null) ? rsplan.getString("sim_subida") : "";
                        rsplan.close();
                    }
                }catch(Exception e){
                    e.printStackTrace();
                }
            }

            String [][] comparticion1 = {{"1.0","1-1"},{"1.5","1.5-1"},{"2.0","2-1"},{"2.5","2.5-1"},
            {"3.0","3-1"},{"3.5","3.5-1"},{"4.0","4-1"},{"4.5","4.5-1"},{"5.0","5-1"},{"5.5","5.5-1"},
            {"6.0","6-1"},{"6.5","6.5-1"},{"7.0","7-1"},{"7.5","7.5-1"},{"8.0","8-1"},{"8.5","8.5-1"},
            {"9.0","9-1"},{"9.5","9.5-1"},{"10.0","10-1"}};


            String html = "obj»d_21^foc»plan^fun»_objTab.seleccionar(0);^frm»";
            html += "<div class='H3' style='width:480px'>Formulario de Plan de Servicio ISP</div>";

            html += "<div class='grupoTab' style='width:460px;'><div id='tabR0'>"
                    + "<a id='tab00' href='javascript:void(0);' class='jm_tabAct' onclick='_objTab.seleccionar(0);'>Plan</a>";
            if(id.compareTo("-1")!=0){
                 html += "<a id='tab01' href='javascript:void(0);' class='jm_tab' onclick='_objTab.seleccionar(1);'>Velocidades</a>";
            }
            html += "</div>";

            html += "<form action='frmPlanServicioGuardar' onsubmit='return fac_planGuardar(this)' autocomplete='off'>";
            html += "<input type='hidden' id='id' name='id' value='"+id+"' />";

            /* PRIMER TAB */
            html += "<div id='tabR1'>";
            html += "<div id='tab10' class='jm_tabHTML' style='display:block;width:450px;height:"+_altBody+"px;'>";
            
            html += "<table><tr><td>Plan: <span class='marca'>*</span></td>";
            html += "<td><input id='plan' name='plan' type='text' size='44' maxlength='80' value='"+plan+"' onkeypress=\"_alfanumerico(event);\" onblur=\"this.value=this.value._trim();\" /></td></tr>";
            html += "<tr><td>Compartici&oacute;n: <span class='marca'>*</span></td>";
            html += "<td>" + DatosDinamicos.combo("comparticion", comparticion, comparticion1, "") + "</td></tr>";
            html += "<tr><td>Servicio: <span class='marca'>*</span></td>";
            html += "<td>" + DatosDinamicos.combo(rsProductos, "id_producto", id_producto, "", "", 250) + "</td></tr>";
            html += "<tr><td>Servicio de anticipo: <span class='marca'>*</span></td>";
            html += "<td>" + DatosDinamicos.combo(rsProductos, "id_producto_anticipo", id_producto_anticipo, "", "", 250) + "</td></tr>";
            html += "<tr><td>Nivel de soporte: <span class='marca'>*</span></td>";
            html += "<td>" + DatosDinamicos.combo(rsNivelSoporte, "id_nivel_soporte", id_nivel_soporte, "", "", 250) + "</td></tr>";
            html += "<tr><td>Divisor Max limit: <span class='marca'>*</span></td>";
            html += "<td><input id='div_max_limit' name='div_max_limit' type='text' size='44' maxlength='10' value='"+div_max_limit+"' onkeypress=\"_evaluar(event, '0123456789.');\" /></td></tr>";
            html += "<tr><td>Divisor Burst Threshold: <span class='marca'>*</span></td>";
            html += "<td><input id='div_burst_threshold' name='div_burst_threshold' type='text' size='44' maxlength='10' value='"+div_burst_threshold+"' onkeypress=\"_evaluar(event, '0123456789.');\" /></td></tr>";
            html += "<tr><td>Burst Time: <span class='marca'>*</span></td>";
            html += "<td><input id='burst_time' name='burst_time' type='text' size='44' maxlength='10' value='"+burst_time+"' onkeypress=\"_numero(event);\" /></td></tr>";
            html += "<tr><td>Simetr&iacute;a de Subida: <span class='marca'>*</span></td>";
            html += "<td><input id='sim_subida' name='sim_subida' type='text' size='44' maxlength='10' value='"+sim_subida+"' onkeypress=\"_evaluar(event, '0123456789.');\" /></td></tr>";
            html += "</table>";
            html += "</div>";

            /* SEGUNDO TAB */
            html += "<div id='tab11' class='jm_tabHTML' style='display:none;width:450px;height:"+_altBody+"px;'>";
            if(id.compareTo("-1")==0 || (id.compareTo("-1")!=0 && objRol.getAcceso(id_rol, "planEditar"))){
                html += "&nbsp;&nbsp;<a href='javascript:void(0);' id='inRe' onclick=\"fac_velocidadNueva();\">Nueva velocidad</a>";
            }
            html += "<TABLE cellspacing='0' cellpadding='0'><TR>" +
                            "<TH class='jm_TH' width='70'>BURST LIMIT</TH>" +
                            "<TH class='jm_TH' width='70'>MAX LIMIT</TH>" +
                            "<TH class='jm_TH' width='80'>BURST THRESHOLD</TH>"+
                            "<TH class='jm_TH' width='60'>BURST TIME</TH>"+
                            "<TH class='jm_TH' width='70'>LIMIT AT</TH>"+
                            "<TH class='jm_TH' width='60'>&nbsp;</TH></TR></TABLE>";
            html += "<DIV style='overflow:auto;width:435px;height:"+(_altBody-35)+"px;' id='sll'>" +
            "<TABLE class='jm_tabla' cellspacing='1' cellpadding='0' id='tblI'>";
            try{
                int i=0;
                String id_plan_servicio = "";
                String burst_limit = "";
                String max_limit = "";
                String burst_threshold = "";
                String down_burst_time = "";
                String limit_at = "";
                String up_max_limit = "";
                String up_burst_threshold = "";
                String up_burst_time = "";
                String up_limit_at = "";
                String edit = "readonly";
                if(id.compareTo("-1")==0 || (id.compareTo("-1")!=0 && objRol.getAcceso(id_rol, "planEditar"))){
                    edit = "";
                }
                ResultSet rsVelocidad = objPlanServicio.getVelocidades(id);
                while(rsVelocidad.next()){
                    id_plan_servicio = (rsVelocidad.getString("id_plan_servicio")!=null) ? rsVelocidad.getString("id_plan_servicio") : "";
                    burst_limit = (rsVelocidad.getString("burst_limit")!=null) ? rsVelocidad.getString("burst_limit") : "";
                    max_limit = (rsVelocidad.getString("max_limit")!=null) ? rsVelocidad.getString("max_limit") : "";
                    burst_threshold = (rsVelocidad.getString("burst_threshold")!=null) ? rsVelocidad.getString("burst_threshold") : "";
                    down_burst_time = (rsVelocidad.getString("burst_time")!=null) ? rsVelocidad.getString("burst_time") : "";
                    limit_at = (rsVelocidad.getString("limit_at")!=null) ? rsVelocidad.getString("limit_at") : "";
                    up_max_limit = (rsVelocidad.getString("up_max_limit")!=null) ? rsVelocidad.getString("up_max_limit") : "";
                    up_burst_threshold = (rsVelocidad.getString("up_burst_threshold")!=null) ? rsVelocidad.getString("up_burst_threshold") : "";
                    up_burst_time = (rsVelocidad.getString("up_burst_time")!=null) ? rsVelocidad.getString("up_burst_time") : "";
                    up_limit_at = (rsVelocidad.getString("up_limit_at")!=null) ? rsVelocidad.getString("up_limit_at") : "";
                    html += "<tr id='rTI"+i+"' valign='top' class='jm_filaPar' style='cursor:pointer' onmouseover=\"this.className='jm_filaSobre'\" onmouseout=\"this.className='jm_filaPar'\" >";
                    html += "<td width='71'><input id='brLi"+i+"' type='text' size='5' maxlength='6' value='"+burst_limit+"' "+edit+" onkeypress=\"_numero(event);_NoE(event);\" onkeyup=\"fac_velocidadEditar(event, "+i+");\" /></td>";
                    html += "<td width='71' id='mxLi"+i+"' onclick=\"adm_tarifaVer("+i+")\" >"+up_max_limit+" / "+max_limit+"</td>";
                    html += "<td width='81' id='brTh"+i+"' onclick=\"adm_tarifaVer("+i+")\" >"+up_burst_threshold+" / "+burst_threshold+"</td>";
                    html += "<td width='61' id='brTi"+i+"' onclick=\"adm_tarifaVer("+i+")\" >"+up_burst_time+" / "+down_burst_time+"</td>";
                    html += "<td width='71' id='lmAt"+i+"' onclick=\"adm_tarifaVer("+i+")\" >"+up_limit_at+" / "+limit_at+"</td>";
                    html += "<td align='center' width='60'><input type='hidden' id='idPS"+i+"' value='"+id_plan_servicio+"' />" +
                            "<div class='jm_icoCon' id='g"+i+"' style='visibility:hidden;background-position: -545px 0px;' onclick='fac_velocidadGuardar("+i+");' title='guardar'>&nbsp;</div>" +
                            //"<div class='jm_icoCon' id='e"+i+"' style='background-position:-545px -18px;' onclick='fac_velocidadEliminar("+i+");' title='eliminar'>&nbsp;</div></td>" +
                            "</tr>";
                    i++;
                }
                rsVelocidad.close();
                
            }catch(Exception e){e.printStackTrace();}
            html += "</table></div>";

            html += "</div>";


            
            if(id.compareTo("-1")==0 || (id.compareTo("-1")!=0 && objRol.getAcceso(id_rol, "planEditar"))){
                html += "<input type='submit' value='Guardar' />";
            }
            html += "</div></form></div>";


            out.print(html);

        } finally {
            obj_Producto.cerrar();
            objNivelSoporte.cerrar();
            //objPlanCuenta.cerrar();
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
