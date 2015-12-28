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
import jm.inf.clas.Antena;
import jm.web.Archivo;
import jm.web.DatosDinamicos;
import jm.web.Fecha;

/**
 *
 * @author Jorge
 */
public class frmOrdenTrabajoEditar extends HttpServlet {
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
        
        OrdenTrabajo objOrdenTrabajo = new OrdenTrabajo(this._ip, this._puerto, this._db, usuario, clave);
        
        Antena objAntena = new Antena(this._ip, this._puerto, this._db, usuario, clave);

        try {
            String altB = request.getParameter("altB");
            int _altBody = Integer.parseInt(altB);
            String id = request.getParameter("id");

            String ruc = "";
            String razon_social = "";
            String fecha_instalacion = "";
            String direccion = "";
            String telefono = "";
            String movil_claro = "";
            String movil_movistar = "";
            String ciudad = "";
            String sector = "";
            String direccion_instalacion = "";
            String plan = "";
            String ip = "";
            String txt_comparticion = "";
            String mac = "";
            String receptor = "";
            String deviceclave = "";
            String antena_acoplada = "";
            String txt_estado_servicio = "";

            String id_instalacion="";
            String id_sucursal=String.valueOf(id_sucursal_sesion);
            String num_orden="";
            String usuario_reporte="";
            String fecha_reporte="";
            String hora_reporte="";
            String tipo_trabajo="";
            
            String usuario_pendiente="";
            String fecha_pendiente="";
            String hora_pendiente="";
            String motivo_pendiente="";
            String cambio_estado="";

            //String usuario_cliente="";
            String fecha_cliente="";
            String hora_cliente="";
            
            String diagnostico_tecnico=Fecha.getDiaSemana() + " " + Fecha.getHora() + " ";
            String responsable="";
            String fecha_solucion="";
            String materiales="";
            String recomendacion="";
            String estado="1";
            String impresion="0";
            boolean set_deviceclave = false;
            //boolean anulado = false;

            if(id.compareTo("-1")!=0){
                try{
                    ResultSet rsOrdenTrabajo = objOrdenTrabajo.getOrdenTrabajo(id);
                    if(rsOrdenTrabajo.next()){
                        id_instalacion = (rsOrdenTrabajo.getString("id_instalacion")!=null) ? rsOrdenTrabajo.getString("id_instalacion") : "";
                        fecha_instalacion = (rsOrdenTrabajo.getString("fecha_instalacion")!=null) ? rsOrdenTrabajo.getString("fecha_instalacion") : "";
                        ruc = (rsOrdenTrabajo.getString("ruc")!=null) ? rsOrdenTrabajo.getString("ruc") : "";
                        razon_social = (rsOrdenTrabajo.getString("razon_social")!=null) ? rsOrdenTrabajo.getString("razon_social") : "";
                        direccion = (rsOrdenTrabajo.getString("direccion")!=null) ? rsOrdenTrabajo.getString("direccion") : "";
                        telefono = (rsOrdenTrabajo.getString("telefono")!=null) ? rsOrdenTrabajo.getString("telefono") : "";
                        movil_claro = (rsOrdenTrabajo.getString("movil_claro")!=null) ? rsOrdenTrabajo.getString("movil_claro") : "";
                        movil_movistar = (rsOrdenTrabajo.getString("movil_movistar")!=null) ? rsOrdenTrabajo.getString("movil_movistar") : "";
                        sector = (rsOrdenTrabajo.getString("sector")!=null) ? rsOrdenTrabajo.getString("sector") : "";
                        ciudad = (rsOrdenTrabajo.getString("ciudad")!=null) ? rsOrdenTrabajo.getString("ciudad") : "";
                        direccion_instalacion = (rsOrdenTrabajo.getString("direccion_instalacion")!=null) ? rsOrdenTrabajo.getString("direccion_instalacion") : "";
                        ip = (rsOrdenTrabajo.getString("ip")!=null) ? rsOrdenTrabajo.getString("ip") : "";
                        plan = (rsOrdenTrabajo.getString("plan")!=null) ? rsOrdenTrabajo.getString("plan") : "";
                        txt_comparticion = (rsOrdenTrabajo.getString("txt_comparticion")!=null) ? rsOrdenTrabajo.getString("txt_comparticion") : "";
                        mac = (rsOrdenTrabajo.getString("mac")!=null) ? rsOrdenTrabajo.getString("mac") : "";
                        receptor = (rsOrdenTrabajo.getString("receptor")!=null) ? rsOrdenTrabajo.getString("receptor") : "";
                        deviceclave = (rsOrdenTrabajo.getString("deviceclave")!=null) ? rsOrdenTrabajo.getString("deviceclave") : "";
                        antena_acoplada = (rsOrdenTrabajo.getString("antena_acoplada")!=null) ? rsOrdenTrabajo.getString("antena_acoplada") : "";
                        txt_estado_servicio = (rsOrdenTrabajo.getString("txt_estado_servicio")!=null) ? rsOrdenTrabajo.getString("txt_estado_servicio") : "";

                        id_sucursal = (rsOrdenTrabajo.getString("id_sucursal")!=null) ? rsOrdenTrabajo.getString("id_sucursal") : "";
                        num_orden = (rsOrdenTrabajo.getString("num_orden")!=null) ? rsOrdenTrabajo.getString("num_orden") : "";
                        usuario_reporte = (rsOrdenTrabajo.getString("usuario_reporte")!=null) ? rsOrdenTrabajo.getString("usuario_reporte") : "";
                        fecha_reporte = (rsOrdenTrabajo.getString("fecha_reporte")!=null) ? Fecha.ISOaSQL(rsOrdenTrabajo.getString("fecha_reporte")) : "";
                        hora_reporte = (rsOrdenTrabajo.getString("hora_reporte")!=null) ? rsOrdenTrabajo.getString("hora_reporte") : "";
                        tipo_trabajo = (rsOrdenTrabajo.getString("tipo_trabajo")!=null) ? rsOrdenTrabajo.getString("tipo_trabajo") : "";

                        usuario_pendiente = (rsOrdenTrabajo.getString("usuario_pendiente")!=null) ? rsOrdenTrabajo.getString("usuario_pendiente") : "";
                        fecha_pendiente = (rsOrdenTrabajo.getString("fecha_pendiente")!=null) ? Fecha.ISOaSQL(rsOrdenTrabajo.getString("fecha_pendiente")) : "";
                        hora_pendiente = (rsOrdenTrabajo.getString("hora_pendiente")!=null) ? rsOrdenTrabajo.getString("hora_pendiente") : "";
                        motivo_pendiente = (rsOrdenTrabajo.getString("motivo_pendiente")!=null) ? rsOrdenTrabajo.getString("motivo_pendiente") : "";
                        //usuario_cliente = (rsOrdenTrabajo.getString("usuario_cliente")!=null) ? rsOrdenTrabajo.getString("usuario_cliente") : "";
                        fecha_cliente = (rsOrdenTrabajo.getString("fecha_cliente")!=null) ? Fecha.ISOaSQL(rsOrdenTrabajo.getString("fecha_cliente")) : "";
                        hora_cliente = (rsOrdenTrabajo.getString("hora_cliente")!=null) ? rsOrdenTrabajo.getString("hora_cliente") : "";

                        diagnostico_tecnico = (rsOrdenTrabajo.getString("diagnostico_tecnico")!=null) ? rsOrdenTrabajo.getString("diagnostico_tecnico") : "";
                        diagnostico_tecnico += "\n" + Fecha.getDiaSemana() + " " + Fecha.getHora() + " ";
                        responsable = (rsOrdenTrabajo.getString("responsable")!=null) ? rsOrdenTrabajo.getString("responsable") : "";
                        fecha_solucion = (rsOrdenTrabajo.getString("fecha_solucion")!=null) ? rsOrdenTrabajo.getString("fecha_solucion") : "";
                        materiales = (rsOrdenTrabajo.getString("materiales")!=null) ? rsOrdenTrabajo.getString("materiales") : "";
                        recomendacion = (rsOrdenTrabajo.getString("recomendacion")!=null) ? rsOrdenTrabajo.getString("recomendacion") : "";
                        estado = (rsOrdenTrabajo.getString("estado")!=null) ? rsOrdenTrabajo.getString("estado") : "1";
                        cambio_estado = (rsOrdenTrabajo.getString("cambio_estado")!=null) ? rsOrdenTrabajo.getString("cambio_estado") : "";
                        impresion = (rsOrdenTrabajo.getString("impresion")!=null) ? rsOrdenTrabajo.getString("impresion") : "";
                        set_deviceclave = (rsOrdenTrabajo.getString("set_deviceclave")!=null) ? rsOrdenTrabajo.getBoolean("set_deviceclave") : false;
                        //anulado = (rsOrdenTrabajo.getString("anulado")!=null) ? rsOrdenTrabajo.getBoolean("anulado") : false;

                        rsOrdenTrabajo.close();
                    }
                }catch(Exception e){
                    e.printStackTrace();
                }
            }

            ResultSet rsTrabajos = objOrdenTrabajo.getTipos(fecha_instalacion);
            
            ResultSet rsAntenasAcopladas = objAntena.getAntenas();
            
            String [][] estado1 = {{"1","REPORTADO"},{"2","EN PROCESO"},{"3","PENDIENTE"}};

            String filtrar = id.compareTo("-1")==0 ? "<div class='jm_icoCon' id='icoEmp' style=\"background-position:-524px -14px;\" title='Buscar cliente' onclick=\"fac_getInstalacion()\">&nbsp;</div>" : "";

            String html = "obj»otr_html^foc»diagnostico_tecnico^fun»_objTab.seleccionar(0);^frm»";
            html += "<div class='H3' style='width:730px'>Formulario de Orden de Trabajo</div>";

            html += "<div class='grupoTab' style='width:710px;'><div id='tabR0'>"
                    + "<a id='tab00' href='javascript:void(0);' class='jm_tabAct' onclick='_objTab.seleccionar(0);'>Orden de Trabajo</a>";
            html += "<a id='tab01' href='javascript:void(0);' class='jm_tab' onclick='_objTab.seleccionar(1);'>Croquis</a>";
            if(id.compareTo("-1")!=0){
                html += "<a id='tab02' href='javascript:void(0);' class='jm_tab' onclick='_objTab.seleccionar(2);'>Pendiente</a>";
                html += "<a id='tab03' href='javascript:void(0);' class='jm_tab' onclick='_objTab.seleccionar(3);'>Soluci&oacute;n</a>";
            }
            html += "</div>";

            /* PRIMER TAB */
            html += "<div id='tabR1'>";
            html += "<div id='tab10' class='jm_tabHTML' style='display:block;width:700px;height:"+_altBody+"px;'>";

            html += "<form id='frmOT' action='frmOrdenTrabajoGuardar' onsubmit='return fac_trabajoGuardar(this)' autocomplete='off'>";
            html += "<input type='hidden' id='idIns' name='idIns' value='"+id_instalacion+"' />";
            html += "<input type='hidden' id='id_orden_trabajo' name='id_orden_trabajo' value='"+id+"' />";
            html += "<input type='hidden' id='num_orden' name='num_orden' value='"+num_orden+"' />";
            html += "<div id='ax' style='display:none'></div>";

            html += "<table width='100%'>";
            html += "<tr><td><div style='float:left;display:table-cell' width='15%'>C&eacute;dula o RUC: </div>"+filtrar+"</td><td id='ruc' width='35%'>"+ruc+"</td> <td width='15%'>Cliente: </td><td id='raSo' width='35%'>"+razon_social+"</td></tr>";
            html += "<tr><td>Sector:</td><td id='sec'>"+sector+"</td> <td>Cant&oacute;n o ciudad: </td><td id='ciu'>"+ciudad+"</td></tr>";
            html += "<tr><td>Direcci&oacute;n del cliente: </td><td id='dir'>"+direccion+"</td><td>Tel&eacute;fonos:</td><td id='tel'>"+telefono+" &nbsp; Claro: "+movil_claro+" &nbsp; Movistar: "+movil_movistar+"</td></tr>";
            html += "<tr><td>Direcci&oacute;n de instalaci&oacute;n:</td><td colspan='3' id='dirIns'>"+direccion_instalacion+"</td></tr>";
            html += "<tr><td>Plan actual:</td><td id='plan'>"+plan+"</td> <td>Compartici&oacute;n:</td><td id='com'>"+txt_comparticion+"</td></tr>";
            html += "<tr><td>Direcci&oacute;n IP:</td><td id='ip'>"+ip+"</td> <td>Receptor:</td><td id='rec'>"+receptor+" &nbsp;&nbsp;&nbsp; Clave: "+deviceclave+" <br />"
                    + "CLAVE: "+deviceclave+" &nbsp;&nbsp;  <label onclick=\"set_deviceClave("+id_instalacion+")\">Registrada<input type='checkbox' id='set_deviceclave' name='set_deviceclave' "+(set_deviceclave?"checked":"")+" /></label><br /></td></tr>";
            html += "<tr><td>Antena acoplada:</td><td id='anAc'>"+antena_acoplada+"</td> <td>Servicio:</td><td id='esSe'>"+txt_estado_servicio+"</td></tr></table>";
            html += "<hr />";
            html += "<table>";
            html += "<td>N&uacute;mero: </td>";
            html += "<td><div style='float:left'>"+id_sucursal+"-"+num_orden+"</div>"
                    + "<div style='float:right'>imp.: "+impresion+"</div></td></tr>";
            html += "<td>Usuario, fecha y hora de reporte: </td>";
            html += "<td>"+usuario_reporte+" en la fecha "+Fecha.ISOaSQL(fecha_reporte)+" a las "+hora_reporte+"</td></tr>";
            html += "<tr><td>Tipo de trabajo: </td>";
            html += "<td>" + DatosDinamicos.combo(rsTrabajos, "tipo_trabajo", tipo_trabajo, "", "", 200) + "</td></tr>";

            html += "<tr><td>Fecha y hora sugerida por el cliente para el trabajo: </td>";
            html += "<td><input id='fecha_cliente' name='fecha_cliente' type='text' size='10' maxlength='10' onkeypress=\"_SUP(event)\" value='"+fecha_cliente+"' />"
                    + "<input type='button' value='...' onClick=\"SelectorFecha.crear('fecha_cliente', 'SQL');\" /> &nbsp; "
                    + "hora: <input type='text' id='hora_cliente' name='hora_cliente' size='5' maxlength='5' value='"+hora_cliente+"' onkeypress=\"_evaluar(event, '0123456789:')\" /></td></tr>";

            html += "<tr><td valign='top'>Diagn&oacute;stico t&eacute;cnico: <span class='marca'>*</span></td>";
            html += "<td><textarea id='diagnostico_tecnico' name='diagnostico_tecnico' rows='3' cols='80'>"+diagnostico_tecnico+"</textarea></td></tr>";
            html += "<tr><td valign='top'>Comentario de revisiones: <span class='marca'>*</span></td>";
            html += "<td><textarea id='motivo_pendiente' name='motivo_pendiente' rows='3' cols='80'>"+motivo_pendiente+"</textarea></td></tr>";
            html += "<tr><td>T&eacute;cnico responsable: </td>";
            html += "<td>" + responsable + "</td></tr>";
            
            html += "<tr><td>Estado: </td>";
            html += "<td>" + DatosDinamicos.combo("estado", estado, estado1, "_('axCamEst').style.display='block'") + "</td></tr>";
            
            html += "</table>";
            
            html += "<table width='100%' id='axCamEst' style='display:"+(cambio_estado.compareTo("")==0?"none":"block")+"'>";
            html += "<tr><td valign='top'>Motivo de cambio de estado: <span class='marca'>*</span></td>";
            html += "<td><textarea id='cambio_estado' name='cambio_estado' rows='3' cols='80'>"+cambio_estado+"</textarea></td></tr>";
            html += "</table>";

            //if(id.compareTo("-1")==0){
            //    html += "<input type='submit' value='Guardar e imprimir orden' />";
            //}else{
                if(objRol.getAcceso(id_rol, "trabajoEditar") && estado.compareTo("9")!=0){
                    html += "<input type='submit' value='Guardar' /> &nbsp;&nbsp;&nbsp;&nbsp;&nbsp; ";
                }
                if(estado.compareTo("9")!=0){
                    html += "<input type='button' value='Re-imprimir orden' onclick=\"imprimir('pdfOrdenTrabajo?id="+id+"');\" />";
                }
            //}
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



            /* PENDIENTE -        TERCER TAB */



            
            html += "<div id='tab12' class='jm_tabHTML' style='display:none;width:700px;height:"+_altBody+"px;'>";

            //html += "<form action='frmOrdenTrabajoSolucionar' onsubmit='return fac_trabajoSolucionar(this)' autocomplete='off'>";
            html += "<table>";
            html += "<tr><td>Usuario: </td>";
            html += "<td>"+usuario_pendiente+"</td></tr>";
            html += "<tr><td>Fecha: </td>";
            html += "<td>"+Fecha.ISOaSQL(fecha_pendiente)+" a las "+hora_pendiente+"</td></tr>";
            html += "<tr><td>Motivo: </td>";
            html += "<td><textarea id='motivo_pendiente' name='motivo_pendiente' rows='5' cols='80'>"+motivo_pendiente+"</textarea></td></tr>";
            html += "</table>";
            /*if(id.compareTo("-1")!=0 && objRol.getAcceso(id_rol, "trabajoSolucion") && (estado.compareTo("1")==0 || estado.compareTo("2")==0)){
                html += "<input type='submit' value='Guardar Pendiente' />";
            }*/
            //html += "</form>";


            html += "</div>";



            /* CUARTO TAB */


            html += "<div id='tab13' class='jm_tabHTML' style='display:none;width:700px;height:"+_altBody+"px;'>";

            html += "<form action='frmOrdenTrabajoSolucionar' onsubmit='return fac_trabajoSolucionar(this)' autocomplete='off'>";
            html += "<input type='hidden' id='id' name='id' value='"+id+"' />";
            html += "<input type='hidden' id='antena_acoplada0' value='"+antena_acoplada+"' />";
            html += "<input type='hidden' id='mac_ant' name='mac_ant' value='"+mac+"' />";
            html += "<input type='hidden' id='idIns1' name='idIns1' value='"+id_instalacion+"' />";
            html += "<input type='hidden' id='num_orden1' name='num_orden1' value='"+num_orden+"' />";
            html += "<table><tr><td>Fecha de soluci&oacute;n: <span class='marca'>*</span></td>";
            html += "<td><input id='fecha_solucion' name='fecha_solucion' type='text' size='10' maxlength='10' value='"+Fecha.ISOaSQL(fecha_solucion)+"' readonly />"
                    + "<input type='button' value='...' onClick=\"SelectorFecha.crear('fecha_solucion', 'SQL');\" /></td></tr>";
            html += "<tr><td>Antena de trasmisi&oacute;n acoplada: <span class='marca'>*</span></td>";
            html += "<td><div>" + DatosDinamicos.combo(rsAntenasAcopladas, "antena_acoplada", antena_acoplada, "", "", 200) + "</div>";
            html += "<tr><td>Direcci&oacute;n MAC: <span class='marca'>*</span></td>";
            html += "<td><input type='text' id='mac' name='mac' value='"+mac+"' size='34' onfocus=\"fac_cmbActivo('mac')\" onkeyup=\"fac_getActivosBodega('mac', '')\" /></td></tr>";
            //html += "<td>" + DatosDinamicos.combo(rsMacActivos, "mac", mac, "", "", 250) + "</td></tr>";
            html += "<tr><td colspan='2'>Materiales utilizados: <br />";
            html += "<textarea id='materiales' name='materiales' rows='5' cols='80'>"+materiales+"</textarea></td></tr>";
            html += "<tr><td colspan='2'>Observaciones y recomendaciones: <span class='marca'>*</span><br />";
            html += "<textarea id='recomendacion' name='recomendacion' rows='5' cols='80'>"+recomendacion+"</textarea></td></tr>";

            html += "</table>";
            if(id.compareTo("-1")!=0 && objRol.getAcceso(id_rol, "trabajoSolucion") && (estado.compareTo("1")==0 || estado.compareTo("2")==0)){
                html += "<input type='submit' value='Guardar Soluci&oacute;n' />";
            }
            html += "</form>";

            html += "</div></div></div>";

            out.println(html);


        } finally {
            objAntena.cerrar();
            objRol.cerrar();
            objOrdenTrabajo.cerrar();
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