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
import jm.inf.clas.Nodo;
import jm.nom.clas.Empleado;
import jm.web.DatosDinamicos;
import jm.web.Fecha;

/**
 *
 * @author Jorge
 */
public class frmOrdenTrabajoTorreEditar extends HttpServlet {
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
        ResultSet rsTareas = objOrdenTrabajo.getTiposTareas(1, 0);
        
        Empleado objEmpleado = new Empleado(this._ip, this._puerto, this._db, usuario, clave);
        
        Nodo objNodo = new Nodo(this._ip, this._puerto, this._db, usuario, clave);

        try {
            String altB = request.getParameter("altB");
            int _altBody = Integer.parseInt(altB);
            String id = request.getParameter("id");

            String mac = "";
            String antena_acoplada = "";

            String id_instalacion="";
            String id_sucursal=String.valueOf(id_sucursal_sesion);
            String num_orden="";
            String usuario_reporte="";
            String fecha_reporte="";
            String hora_reporte="";

            //String usuario_cliente="";
            String fecha_cliente="";
            String hora_cliente="";
            
            String diagnostico_tecnico=Fecha.getDiaSemana() + " " + Fecha.getHora() + " ";
            String id_empleado = "";
            String responsable="";
            String fecha_solucion="";
            String materiales="";
            String recomendacion="";
            String estado="1";
            String impresion="0";
            String id_nodo = "";

            if(id.compareTo("-1")!=0){
                try{
                    ResultSet rsOrdenTrabajo = objOrdenTrabajo.getOrdenTrabajoInfra(id);
                    if(rsOrdenTrabajo.next()){
                        id_sucursal = (rsOrdenTrabajo.getString("id_sucursal")!=null) ? rsOrdenTrabajo.getString("id_sucursal") : "";
                        num_orden = (rsOrdenTrabajo.getString("num_orden")!=null) ? rsOrdenTrabajo.getString("num_orden") : "";
                        usuario_reporte = (rsOrdenTrabajo.getString("usuario_reporte")!=null) ? rsOrdenTrabajo.getString("usuario_reporte") : "";
                        fecha_reporte = (rsOrdenTrabajo.getString("fecha_reporte")!=null) ? Fecha.ISOaSQL(rsOrdenTrabajo.getString("fecha_reporte")) : "";
                        hora_reporte = (rsOrdenTrabajo.getString("hora_reporte")!=null) ? rsOrdenTrabajo.getString("hora_reporte") : "";

                        //usuario_cliente = (rsOrdenTrabajo.getString("usuario_cliente")!=null) ? rsOrdenTrabajo.getString("usuario_cliente") : "";
                        fecha_cliente = (rsOrdenTrabajo.getString("fecha_cliente")!=null) ? Fecha.ISOaSQL(rsOrdenTrabajo.getString("fecha_cliente")) : "";
                        hora_cliente = (rsOrdenTrabajo.getString("hora_cliente")!=null) ? rsOrdenTrabajo.getString("hora_cliente") : "";

                        id_empleado = (rsOrdenTrabajo.getString("id_empleado")!=null) ? rsOrdenTrabajo.getString("id_empleado") : "";
                        diagnostico_tecnico = (rsOrdenTrabajo.getString("diagnostico_tecnico")!=null) ? rsOrdenTrabajo.getString("diagnostico_tecnico") : "";
                        diagnostico_tecnico += "\n" + Fecha.getDiaSemana() + " " + Fecha.getHora() + " ";
                        responsable = (rsOrdenTrabajo.getString("responsable")!=null) ? rsOrdenTrabajo.getString("responsable") : "";
                        fecha_solucion = (rsOrdenTrabajo.getString("fecha_solucion")!=null) ? rsOrdenTrabajo.getString("fecha_solucion") : "";
                        fecha_solucion = fecha_solucion.compareTo("")!=0 ? fecha_solucion : Fecha.getFecha("SQL");
                        materiales = (rsOrdenTrabajo.getString("materiales")!=null) ? rsOrdenTrabajo.getString("materiales") : "";
                        recomendacion = (rsOrdenTrabajo.getString("recomendacion")!=null) ? rsOrdenTrabajo.getString("recomendacion") : "";
                        estado = (rsOrdenTrabajo.getString("estado")!=null) ? rsOrdenTrabajo.getString("estado") : "1";
                        impresion = (rsOrdenTrabajo.getString("impresion")!=null) ? rsOrdenTrabajo.getString("impresion") : "";
                        id_nodo = (rsOrdenTrabajo.getString("id_nodo")!=null) ? rsOrdenTrabajo.getString("id_nodo") : "";

                        rsOrdenTrabajo.close();
                    }
                }catch(Exception e){
                    e.printStackTrace();
                }
            }

            
            ResultSet rsEmpleados = objEmpleado.getEmpleadosBodega(id_sucursal_sesion);

            
            ResultSet rsNodos = objNodo.getNodos();
            

            String html = "obj»otr_html^foc»diagnostico_tecnico^fun»_objTab.seleccionar(0);^frm»";
            html += "<div class='H3' style='width:730px'>Formulario de Orden de Trabajo</div>";

            html += "<div class='grupoTab' style='width:710px;'><div id='tabR0'>"
                    + "<a id='tab00' href='javascript:void(0);' class='jm_tabAct' onclick='_objTab.seleccionar(0);'>Orden de Trabajo</a>";
            if(id.compareTo("-1")!=0 && objRol.getAcceso(id_rol, "trabajoSolucion")){
                html += "<a id='tab03' href='javascript:void(0);' class='jm_tab' onclick='_objTab.seleccionar(3);'>Soluci&oacute;n</a>";
            }
            html += "</div>";

            
            
            
            html += "<div id='tabR1'>";
            
            /* PRIMER TAB */
            
            html += "<div id='tab10' class='jm_tabHTML' style='display:block;width:700px;height:"+_altBody+"px;'>";

            html += "<form id='frmOT' action='frmOrdenTrabajoTorreEditarGuardar' onsubmit='return fac_trabajoGuardar(this)' autocomplete='off'>";
            html += "<input type='hidden' id='idIns' name='idIns' value='"+id_instalacion+"' />";
            html += "<input type='hidden' id='id_orden_trabajo' name='id_orden_trabajo' value='"+id+"' />";
            html += "<input type='hidden' id='num_orden' name='num_orden' value='"+num_orden+"' />";
            
            html += "<div>Nodo: "+DatosDinamicos.combo(rsNodos, "id_nodo", id_nodo, "")+"</div>";
            
            html += "<div id='ax' style='display:none'></div>";

            html += "<div style=\"overflow:auto;width:700px;height:160px;border:1px solid #CCC\">";
            try{
                while(rsTareas.next()){
                    String id_tarea = (rsTareas.getString("id_tarea")!=null ? rsTareas.getString("id_tarea") : "");
                    String tarea = (rsTareas.getString("tarea")!=null ? rsTareas.getString("tarea") : "");
                    html += "<div><a href=\"javascript:void(0)\" onclick=\"inf_getTareas("+id+", "+id_tarea+")\" class=\"tarea\">"+tarea+"</a></div>";
                    html += "<div id=\""+id_tarea+"\" style=\"margin-left:5px\"></div>";
                }
            }catch(Exception ex){
                ex.printStackTrace();
            }
            
            html += "</div>";
            html += "<hr />";
            html += "<table>";
            html += "<tr><td>N&uacute;mero: </td>";
            html += "<td><div style='float:left'>"+id_sucursal+"-"+num_orden+"</div>"
                    + "<div style='float:right'>imp.: "+impresion+"</div></td></tr>";
            html += "<tr><td>Usuario, fecha y hora de reporte: </td>";
            html += "<td>"+usuario_reporte+" en la fecha "+Fecha.ISOaSQL(fecha_reporte)+" a las "+hora_reporte+"</td></tr>";
            html += "<tr><td>Tipo de trabajo: </td>";
            html += "<td>INFRAESTRUCTURA</td></tr>";

            html += "<tr><td>Fecha y hora sugerida para el trabajo: </td>";
            html += "<td><input id='fecha_cliente' name='fecha_cliente' type='text' size='10' maxlength='10' onkeypress=\"_SUP(event)\" value='"+fecha_cliente+"' />"
                    + "<input type='button' value='...' onClick=\"SelectorFecha.crear('fecha_cliente', 'SQL');\" /> &nbsp; "
                    + "hora: <input type='text' id='hora_cliente' name='hora_cliente' size='5' maxlength='5' value='"+hora_cliente+"' onkeypress=\"_evaluar(event, '0123456789:')\" /></td></tr>";

            html += "<tr><td valign='top'>Diagn&oacute;stico t&eacute;cnico: <span class='marca'>*</span></td>";
            html += "<td><textarea id='diagnostico_tecnico' name='diagnostico_tecnico' rows='3' cols='80'>"+diagnostico_tecnico+"</textarea></td></tr>";
            
            html += "<tr><td>T&eacute;cnico responsable: </td>";
            html += "<td>"+DatosDinamicos.combo(rsEmpleados, "id_empleado", id_empleado, "")+"</td></tr>";
            html += "</table>";

            //if(id.compareTo("-1")==0){
            //    html += "<input type='submit' value='Guardar e imprimir orden' />";
            //}else{
                if(objRol.getAcceso(id_rol, "trabajoEditar") && estado.compareTo("9")!=0){
                    html += "<input type='submit' value='Guardar' /> &nbsp;&nbsp;&nbsp;&nbsp;&nbsp; ";
                }
                if(estado.compareTo("9")!=0){
                    html += "<input type='button' value='Re-imprimir orden' onclick=\"imprimir('pdfOrdenTrabajoInfraestructura?id="+id+"');\" />";
                }
            //}
            html += "</form>";
            html += "</div>";







            /* PENDIENTE -        TERCER TAB */



            /*
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
            if(id.compareTo("-1")!=0 && objRol.getAcceso(id_rol, "trabajoSolucion") && (estado.compareTo("1")==0 || estado.compareTo("2")==0)){
                html += "<input type='submit' value='Guardar Pendiente' />";
            }
            //html += "</form>";


            html += "</div>";
*/


            /* CUARTO TAB */


            html += "<div id='tab13' class='jm_tabHTML' style='display:none;width:700px;height:"+_altBody+"px;'>";

            html += "<form action='frmOrdenTrabajoTorreSolucionar' onsubmit='return inf_trabajoInfraSolucionar(this)' autocomplete='off'>";
            html += "<input type='hidden' id='id' name='id' value='"+id+"' />";
            html += "<input type='hidden' id='idIns' name='idIns' value='"+id_instalacion+"' />";
            html += "<input type='hidden' id='num_orden1' name='num_orden1' value='"+num_orden+"' />";
            
            html += "<table><tr><td>Fecha de soluci&oacute;n: <span class='marca'>*</span></td>";
            html += "<td><input id='fecha_solucion' name='fecha_solucion' type='text' size='10' maxlength='10' value='"+fecha_solucion+"' readonly />"
                    + "<input type='button' value='...' onClick=\"SelectorFecha.crear('fecha_solucion', 'SQL');\" /></td></tr>";
            html += "</table>";
            
            
            
            ResultSet resTar = objOrdenTrabajo.getTareasOrden(id);
            try{
                int i=0;
                html += "<table cellspacing='0' cellpadding='0' style='width:955px;'><tr>"
                        + "<th class='jm_TH' style='width:140px'>TAREA</th>"
                        + "<th class='jm_TH' style='width:100px'>SOLUCIONADO</th>"
                        + "<th class='jm_TH' style='width:265px'>EQUIPOS / MATERIALES</th>"
                        + "<th class='jm_TH' style='width:265px'>EQUIPOS / MATERIALES RETIRADOS</th>"
                        + "<th class='jm_TH' style='width:180px'>OBSERVACION</th>"
                        + "</tr></table>";

                html += "<div class='columna' style='overflow:auto;width:960px;height:300px;'>";
                html += "<div class='jm_tabla' id='tblAs'>";
                while(resTar.next()){
                    Boolean solucionado = resTar.getString("solucionado")!=null?resTar.getBoolean("solucionado"):false;
                    String equipos_nuevos = resTar.getString("equipos_nuevos")!=null?resTar.getString("equipos_nuevos"):"";
                    String equipos_retirados = resTar.getString("equipos_retirados")!=null?resTar.getString("equipos_retirados"):"";
                    String observacion_tarea = resTar.getString("observacion_tarea")!=null?resTar.getString("observacion_tarea"):"";
                    String clase = i%2==0 ? "jm_filaPar" : "jm_filaImp";
                    html += "<div class='jm_fila "+clase+"'>";
                    html += "<div class='jm_columna caja' style='width:140px;height:70px;'><input type='hidden' id='id_tarea"+i+"' name='id_tarea"+i+"' value=\""+(resTar.getString("id_tarea")!=null?resTar.getString("id_tarea"):"")+"\" />"+
                            (resTar.getString("tarea")!=null?resTar.getString("tarea"):"")+"</div>";
                    
                    html += "<div class='jm_columna caja' style='width:100px;height:70px;' id='axtmac_ant"+i+"'><label onclick=\"_('mac"+i+"').style.visibility=_('mac_ret"+i+"').style.visibility=_('obs"+i+"').style.visibility='visible'\"><input type='radio' id='sol0"+i+"' name='sol"+i+"' value='true' "+(solucionado ? "checked" : "")+" />Si</label> &nbsp; "
                                    + "<label onclick=\"_('mac"+i+"').value=_('obs"+i+"').value=_('macs"+i+"').innerHTML=_('macs_ret"+i+"').innerHTML='';_('mac"+i+"').style.visibility=_('mac_ret"+i+"').style.visibility=_('obs"+i+"').style.visibility='hidden';\" ><input type='radio' id='sol1"+i+"' name='sol"+i+"' value='false'"+(!solucionado ? "checked" : "")+" />No</label></div>";
                    
                    html += "<div class='jm_columna caja' style=\"width:265px;height:70px;\"><input style=\"visibility:hidden\" class='may' onfocus=\"this.value=''\" type='text' id='mac"+i+"' name='mac"+i+"' style='width:95px' maxlength='34' onkeyup=\"fac_cmbActivo('mac"+i+"', 300);inf_getEquiposBodega(this.value, "+i+", 'macs"+i+"', 0);\" />"
                            + "<div id='macs"+i+"' style='margin-left:3px;overflow:auto;width:260px;height:45px;'>"+equipos_nuevos+"</div></div>";
                    
                    html += "<div class='jm_columna caja' style=\"width:265px;height:70px;\"><input style=\"visibility:hidden\" class='may' onfocus=\"this.value=''\" type='text' id='mac_ret"+i+"' name='mac_ret"+i+"' style='width:95px' maxlength='34' onkeyup=\"fac_cmbActivo('mac_ret"+i+"', 300);inf_getEquiposBodega(this.value, "+i+", 'macs_ret"+i+"', 1);\" />"
                            + "<div id='macs_ret"+i+"' style='margin-left:3px;overflow:auto;width:260px;height:45px;'>"+equipos_retirados+"</div></div>";
                    
                    html += "<div class='jm_columna caja' style='width:160px;height:70px;'><textarea id='obs"+i+"' name='obs"+i+"' style=\"width:145px;height:60px;visibility:hidden\" onfocus='this.select();'>"+observacion_tarea+"</textarea></div>"
                            + "</div>";
                    i++;
                }
                html += "</div>";
                html += "</div>";
                resTar.close();
            }catch(Exception ex){
                ex.printStackTrace();
            }      
            
            if(id.compareTo("-1")!=0 && objRol.getAcceso(id_rol, "trabajoSolucion") && (estado.compareTo("1")==0 )){
                html += "<input type='submit' value='Guardar Soluci&oacute;n' />";
            }
            html += "</form>";

            html += "</div></div>";

            out.println(html);


        } finally {
            objNodo.cerrar();
            objEmpleado.cerrar();
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