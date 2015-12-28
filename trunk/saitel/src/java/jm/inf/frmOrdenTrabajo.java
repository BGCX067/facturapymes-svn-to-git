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

package jm.inf;

import java.io.*;

import javax.servlet.*;
import javax.servlet.http.*;
import jm.seg.clas.Rol;
import jm.inf.clas.OrdenTrabajo;
import java.sql.ResultSet;
import jm.fac.clas.Instalacion;
import jm.web.Archivo;
import jm.web.DatosDinamicos;
import jm.web.Fecha;

/**
 *
 * @author Jorge
 */
public class frmOrdenTrabajo extends HttpServlet {
    private String _ip = null;
    private int _puerto = 5432;
    private String _db = null;
    private String _dir = null;

    public void init(ServletConfig config) throws ServletException
    {
        this._ip = config.getServletContext().getInitParameter("_IP");
        this._puerto = Integer.parseInt(config.getServletContext().getInitParameter("_PUERTO"));
        this._db = config.getServletContext().getInitParameter("_DB");
        this._dir = config.getServletContext().getInitParameter("_DIR");
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

        Rol objRol = new Rol(this._ip, this._puerto, this._db, usuario, clave);

        

        try {
            String altB = request.getParameter("altB");
            int _altBody = Integer.parseInt(altB);
            String id_instalacion = request.getParameter("id");

            String ruc = "";
            String razon_social = "";
            String direccion = "";
            String telefono = "";
            String movil_claro = "";
            String movil_movistar = "";
            String ciudad = "";
            String sector = "";
            String direccion_instalacion = "";
            String fecha_instalacion = "";
            String plan = "";
            String ip = "";
            String txt_comparticion = "";
            //String mac = "";
            String receptor = "";
            String deviceclave = "";
            String antena_acoplada = "";
            String txt_estado_servicio = "";
            boolean set_deviceclave = false;

            String id_sucursal=String.valueOf(id_sucursal_sesion);

            Instalacion objInstalacion = new Instalacion(this._ip, this._puerto, this._db, usuario, clave);
            try{
                ResultSet rs = objInstalacion.getInstalacion(id_instalacion);
                if(rs.next()){
                    id_instalacion = (rs.getString("id_instalacion")!=null) ? rs.getString("id_instalacion") : "";
                    fecha_instalacion = (rs.getString("fecha_instalacion")!=null) ? rs.getString("fecha_instalacion") : "";
                    ruc = (rs.getString("ruc")!=null) ? rs.getString("ruc") : "";
                    razon_social = (rs.getString("razon_social")!=null) ? rs.getString("razon_social") : "";
                    direccion = (rs.getString("direccion")!=null) ? rs.getString("direccion") : "";
                    telefono = (rs.getString("telefono")!=null) ? rs.getString("telefono") : "";
                    movil_claro = (rs.getString("movil_claro")!=null) ? rs.getString("movil_claro") : "";
                    movil_movistar = (rs.getString("movil_movistar")!=null) ? rs.getString("movil_movistar") : "";
                    sector = (rs.getString("sector")!=null) ? rs.getString("sector") : "";
                    ciudad = (rs.getString("ciudad")!=null) ? rs.getString("ciudad") : "";
                    direccion_instalacion = (rs.getString("direccion_instalacion")!=null) ? rs.getString("direccion_instalacion") : "";
                    ip = (rs.getString("ip")!=null) ? rs.getString("ip") : "";
                    plan = (rs.getString("plan")!=null) ? rs.getString("plan") : "";
                    txt_comparticion = (rs.getString("txt_comparticion")!=null) ? rs.getString("txt_comparticion") : "";
                    //mac = (rs.getString("mac")!=null) ? rs.getString("mac") : "";
                    receptor = (rs.getString("receptor")!=null) ? rs.getString("receptor") : "";
                    deviceclave = (rs.getString("deviceclave")!=null) ? rs.getString("deviceclave") : "";
                    antena_acoplada = (rs.getString("antena_acoplada")!=null) ? rs.getString("antena_acoplada") : "";
                    txt_estado_servicio = (rs.getString("txt_estado_servicio")!=null) ? rs.getString("txt_estado_servicio") : "";
                    set_deviceclave = (rs.getString("set_deviceclave")!=null) ? rs.getBoolean("set_deviceclave") : false;
                    rs.close();
                }
            }catch(Exception e){
                e.printStackTrace();
            }finally{
                objInstalacion.cerrar();
            }
            
            OrdenTrabajo objOrdenTrabajo = new OrdenTrabajo(this._ip, this._puerto, this._db, usuario, clave);
            ResultSet rsTrabajos = objOrdenTrabajo.getTipos(fecha_instalacion);
            

            String html = "obj»otr_html^foc»diagnostico_tecnico^fun»_objTab.seleccionar(0);^frm»";
            html += "<div class='H3' style='width:730px'>Formulario de Orden de Trabajo</div>";

            html += "<div class='grupoTab' style='width:710px;'><div id='tabR0'>"
                    + "<a id='tab00' href='javascript:void(0);' class='jm_tabAct' onclick='_objTab.seleccionar(0);'>Orden de Trabajo</a>";
            html += "<a id='tab01' href='javascript:void(0);' class='jm_tab' onclick='_objTab.seleccionar(1);'>Croquis</a>";
            html += "</div>";

            /* PRIMER TAB */
            html += "<div id='tabR1'>";
            html += "<div id='tab10' class='jm_tabHTML' style='display:block;width:700px;height:"+_altBody+"px;'>";

            html += "<form id='frmOT' action='frmOrdenTrabajoGuardar' onsubmit='return fac_trabajoGuardar(this)' autocomplete='off'>";
            html += "<input type='hidden' id='idIns' name='idIns' value='"+id_instalacion+"' />";
            html += "<input type='hidden' id='id_orden_trabajo' name='id_orden_trabajo' value='-1' />";
            html += "<table width='100%'>";
            html += "<tr><td><div style='float:left;display:table-cell' width='15%'>C&eacute;dula o RUC: </div></td><td id='ruc' width='35%'>"+ruc+"</td> <td width='15%'>Cliente: </td><td id='raSo' width='35%'>"+razon_social+"</td></tr>";
            html += "<tr><td>Sector:</td><td id='sec'>"+sector+"</td> <td>Cant&oacute;n o ciudad: </td><td id='ciu'>"+ciudad+"</td></tr>";
            html += "<tr><td>Direcci&oacute;n del cliente: </td><td id='dir'>"+direccion+"</td><td>Tel&eacute;fonos:</td><td id='tel'>"+telefono+" &nbsp; Claro: "+movil_claro+" &nbsp; Movistar: "+movil_movistar+"</td></tr>";
            html += "<tr><td>Direcci&oacute;n de instalaci&oacute;n:</td><td colspan='3' id='dirIns'>"+direccion_instalacion+"</td></tr>";
            html += "<tr><td>Plan actual:</td><td id='plan'>"+plan+"</td> <td>Compartici&oacute;n:</td><td id='com'>"+txt_comparticion+"</td></tr>";
            html += "<tr><td>Direcci&oacute;n IP:</td><td id='ip'>"+ip+"</td> <td>Receptor:</td><td id='rec'>"+receptor+" &nbsp;&nbsp;&nbsp; Clave: "+deviceclave+" <br />"
                    + "CLAVE: "+deviceclave+" &nbsp;&nbsp;  <label onclick=\"set_deviceClave("+id_instalacion+")\">Registrada<input type='checkbox' id='set_deviceclave' name='set_deviceclave' "+(set_deviceclave?"checked":"")+" /></label><br /></td></tr>";
            html += "<tr><td>Antena acoplada:</td><td id='anAc'>"+antena_acoplada+"</td> <td>Servicio:</td><td id='esSe'>"+txt_estado_servicio+"</td></tr></table>";
            html += "<hr />";
            html += "<table>";
            html += "<td width='150'>N&uacute;mero: </td>";
            html += "<td>"+id_sucursal+"-</td></tr>";
            html += "<tr><td>Tipo de trabajo: </td>";
            html += "<td>" + DatosDinamicos.combo(rsTrabajos, "tipo_trabajo", "", "_('ax_cmbDir').style.display=this.value==2?'block':'none'", "", 200) + "</td></tr>";
            /*html += "<td><div  id='cob0' style='float:left;display:table-cell'>" + DatosDinamicos.combo(rsTrabajos, "tipo_trabajo", "", "", "", 200) + "</div>"
                    + "<div class='jm_icoCon' id='gr0' style='visibility:hidden;background-position: -545px 0px;' onclick=\"guardarComun(null,'cob0','idC','gr0','add0',150,'TRB');\" title='guardar'>&nbsp;</div>"
                    + "<div class='jm_icoCon' id='add0' style='background-position:-504px -14px;' onclick=\"insertarComun('cob0','idC','gr0','add0',150,'TRB');\" title='Insertar'>&nbsp;</div></td></tr>";*/
            /*
            html += "<tr><td colspan='2'><table id='ax_cmbDir' style=\"display:none\" cellspacing='0' cellpadding='0'>";
            html += "<tr><td width='153'>Nueva direcci&oacute;n: </td>";
            html += "<td><input id='direccion_cambio' name='direccion_cambio' size='80' maxlength='200' /></td></tr>";
            html += "</table></td></tr>";
            */
            html += "<tr><td>Fecha y hora sugerida por el cliente para el trabajo: </td>";
            html += "<td><input id='fecha_cliente' name='fecha_cliente' type='text' size='10' maxlength='10' onkeypress=\"_SUP(event)\" />"
                    + "<input type='button' value='...' onClick=\"SelectorFecha.crear('fecha_cliente', 'SQL');\" /> &nbsp; "
                    + "hora: <input type='text' id='hora_cliente' name='hora_cliente' size='5' maxlength='5' onkeypress=\"_evaluar(event, '0123456789:')\" /></td></tr>";
            html += "<tr><td valign='top'>Diagn&oacute;stico t&eacute;cnico: <span class='marca'>*</span></td>";
            html += "<td><textarea id='diagnostico_tecnico' name='diagnostico_tecnico' rows='3' cols='80'>"+Fecha.getDiaSemana() + " " + Fecha.getHora()+" </textarea></td></tr>";
            html += "</table>";

            if(objRol.getAcceso(id_rol, "trabajoNuevo") && fecha_instalacion.compareTo("")!=0){
                html += "<input type='submit' value='Guardar' />";
            }

            html += "</form>";
            html += "</div>";




            /* CROQUIS -        SEGUNDO TAB */



            Archivo archivo = new Archivo(this._ip, this._puerto, this._db, usuario, clave);
            String path = String.valueOf(request.getRequestURL());
            path = path.substring(0, path.lastIndexOf("/"));
            String croquis = path + "/img/" + archivo.getArchivo(this._dir, "tbl_instalacion", id_instalacion, "nombre_img", "croquis");
            archivo.cerrar();

            html += "<div id='tab11' class='jm_tabHTML' style='display:none;width:700px;height:"+_altBody+"px;'>";

            html += "<div class='grupo' id='tagImg' style='width:352px;height:377px;' title='Croquis'>"
                    + "<img src='"+croquis+"' width='350px' height='377px' border='0' />"
                    + "</div>";

            html += "</div>";

            html += "</div></div></div>";

            out.println(html);

            objOrdenTrabajo.cerrar();

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