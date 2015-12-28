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
import java.net.URL;

import javax.servlet.*;
import javax.servlet.http.*;
import jm.seg.clas.Rol;
import jm.inf.clas.Soporte;
import java.sql.ResultSet;
import jm.adm.clas.Configuracion;
import jm.inf.clas.OrdenTrabajo;
import jm.inf.clas.Antena;
import jm.web.Archivo;
import jm.web.DatosDinamicos;
import jm.web.Fecha;

/**
 *
 * @author Jorge
 */
public class frmSoporte extends HttpServlet {
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

        //Comun objComun = new Comun(this._ip, this._puerto, this._db, usuario, clave);
        //ResultSet rsTrabajos = objComun.getComun("TRB");

        OrdenTrabajo objOrdenTrabajo = new OrdenTrabajo(this._ip, this._puerto, this._db, usuario, clave);

        Rol objRol = new Rol(this._ip, this._puerto, this._db, usuario, clave);


//////////////* CONSEGUIR DATOS DEL AIR CONTROL */
        Configuracion conf = new Configuracion(this._ip, this._puerto, this._db, usuario, clave);
        String airC_ip = conf.getValor("airC_ip");
        String airC_puerto = conf.getValor("airC_puerto");
        String airC_usuario = conf.getValor("airC_usuario");
        String airC_clave = conf.getValor("airC_clave");

        String url = "http://"+airC_ip+":"+airC_puerto+"/api/devicegroup/All%20Devices?auth.user="+airC_usuario+"&auth.password="+airC_clave;
        URL pagina = new URL(url);
        String axAirControl = "";
        try{
            BufferedReader in = new BufferedReader(new InputStreamReader(pagina.openStream()));
            String entrada;
            while ((entrada = in.readLine()) != null){
                axAirControl += entrada;
            }
            in.close();
        }catch(Exception e){
            e.printStackTrace();
        }
//////////////////////////////////////////////////////////////////////////////////////////////////////////


        Antena objAntena = new Antena(this._ip, this._puerto, this._db, usuario, clave);

        Soporte objSoporte = new Soporte(this._ip, this._puerto, this._db, usuario, clave);

        try {
            String altB = request.getParameter("altB");
            String fun = request.getParameter("fun");
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
            String ip_radio = "";
            String txt_comparticion = "";
            String receptor = "";
            String deviceclave = "";
            String antena_acoplada = "";
            String txt_estado_servicio = "";

            String id_instalacion="";
            String id_sucursal=String.valueOf(id_sucursal_sesion);
            String num_soporte="";
            String txt_fecha_llamada=Fecha.getFecha("SQL") + "  " + Fecha.getHora();
            String alias_contesta = usuario;
            String alias_solucion = "";
            String quien_llama="";
            String telefono_llama="";
            String problema="no navega, lento o se corta";
            String diagnostico="";
            String txt_fecha_solucion="";
            String recomendacion="";
            String estado="r";
            boolean set_deviceclave = false;
            String foco = "quien_llama";
            if(id.compareTo("-1")!=0){
                foco = "diagnostico";
                fun = "";
                try{
                    ResultSet rsSoporte = objSoporte.getSoporte(id);
                    if(rsSoporte.next()){
                        id_instalacion = (rsSoporte.getString("id_instalacion")!=null) ? rsSoporte.getString("id_instalacion") : "";
                        fecha_instalacion = (rsSoporte.getString("fecha_instalacion")!=null) ? rsSoporte.getString("fecha_instalacion") : "";
                        ruc = (rsSoporte.getString("ruc")!=null) ? rsSoporte.getString("ruc") : "";
                        razon_social = (rsSoporte.getString("razon_social")!=null) ? rsSoporte.getString("razon_social") : "";
                        direccion = (rsSoporte.getString("direccion")!=null) ? rsSoporte.getString("direccion") : "";
                        telefono = (rsSoporte.getString("telefono")!=null) ? rsSoporte.getString("telefono") : "";
                        movil_claro = (rsSoporte.getString("movil_claro")!=null) ? rsSoporte.getString("movil_claro") : "";
                        movil_movistar = (rsSoporte.getString("movil_movistar")!=null) ? rsSoporte.getString("movil_movistar") : "";
                        
                        sector = (rsSoporte.getString("sector")!=null) ? rsSoporte.getString("sector") : "";
                        ciudad = (rsSoporte.getString("ciudad")!=null) ? rsSoporte.getString("ciudad") : "";
                        direccion_instalacion = (rsSoporte.getString("direccion_instalacion")!=null) ? rsSoporte.getString("direccion_instalacion") : "";
                        ip = (rsSoporte.getString("ip")!=null) ? rsSoporte.getString("ip") : "";
                        ip_radio = (rsSoporte.getString("ip_radio")!=null) ? rsSoporte.getString("ip_radio") : "";
                        plan = (rsSoporte.getString("plan")!=null) ? rsSoporte.getString("plan") : "";
                        txt_comparticion = (rsSoporte.getString("txt_comparticion")!=null) ? rsSoporte.getString("txt_comparticion") : "";
                        receptor = (rsSoporte.getString("receptor")!=null) ? rsSoporte.getString("receptor") : "";
                        deviceclave = (rsSoporte.getString("deviceclave")!=null) ? rsSoporte.getString("deviceclave") : "";
                        antena_acoplada = (rsSoporte.getString("antena_acoplada")!=null) ? rsSoporte.getString("antena_acoplada") : "";
                        txt_estado_servicio = (rsSoporte.getString("txt_estado_servicio")!=null) ? rsSoporte.getString("txt_estado_servicio") : "";

                        id_sucursal = (rsSoporte.getString("id_sucursal")!=null) ? rsSoporte.getString("id_sucursal") : "";
                        num_soporte = (rsSoporte.getString("num_soporte")!=null) ? rsSoporte.getString("num_soporte") : "";
                        alias_contesta = (rsSoporte.getString("alias_contesta")!=null) ? rsSoporte.getString("alias_contesta") : "";
                        alias_solucion = (rsSoporte.getString("alias_solucion")!=null) ? rsSoporte.getString("alias_solucion") : "";
                        txt_fecha_llamada = (rsSoporte.getString("txt_fecha_llamada")!=null) ? rsSoporte.getString("txt_fecha_llamada") : "";
                        quien_llama = (rsSoporte.getString("quien_llama")!=null) ? rsSoporte.getString("quien_llama") : "";
                        telefono_llama = (rsSoporte.getString("telefono_llama")!=null) ? rsSoporte.getString("telefono_llama") : "";
                        problema = (rsSoporte.getString("problema")!=null) ? rsSoporte.getString("problema") : "";
                        diagnostico = (rsSoporte.getString("diagnostico")!=null) ? rsSoporte.getString("diagnostico") : "";
                        diagnostico += "\n"+Fecha.getDiaSemana() + " " + Fecha.getHora() + " ";
                        txt_fecha_solucion = (rsSoporte.getString("txt_fecha_solucion")!=null) ? rsSoporte.getString("txt_fecha_solucion") : "";
                        recomendacion = (rsSoporte.getString("recomendacion")!=null) ? rsSoporte.getString("recomendacion") : "";
                        estado = (rsSoporte.getString("estado")!=null) ? rsSoporte.getString("estado") : "r";
                        set_deviceclave = (rsSoporte.getString("set_deviceclave")!=null) ? rsSoporte.getBoolean("set_deviceclave") : false;

                        rsSoporte.close();
                    }
                }catch(Exception e){
                    e.printStackTrace();
                }
            }

            ResultSet rsTrabajos = objOrdenTrabajo.getTipos(fecha_instalacion);
            
            ResultSet rsAntenasAcopladas = objAntena.getAntenas();


            String html = "obj»cmp_html^foc»"+foco+"^fun»_objTab.seleccionar(0);"+fun+"^frm»";
            html += "<div class='H3' style='width:730px'>Formulario de Soporte</div>";

            html += "<div class='grupoTab' style='width:710px;'><div id='tabR0'>"
                    + "<a id='tab00' href='javascript:void(0);' class='jm_tabAct' onclick='_objTab.seleccionar(0);'>Soporte</a>";
            html += "<a id='tab01' href='javascript:void(0);' class='jm_tab' onclick='_objTab.seleccionar(1);'>Croquis</a>";
            if(id.compareTo("-1")!=0){
                html += "<a id='tab02' href='javascript:void(0);' class='jm_tab' onclick='_objTab.seleccionar(2);'>Soluci&oacute;n</a>";
            }
            html += "</div>";

            /* PRIMER TAB */
            html += "<div id='tabR1'>";
            html += "<div id='tab10' class='jm_tabHTML' style='display:block;width:700px;height:"+_altBody+"px;'>";

            html += "<form id='frmOT' action='frmSoporteGuardar' onsubmit='return fac_soporteGuardar(this)' autocomplete='off'>";
            html += "<input type='hidden' id='idIn' name='idIn' value='"+id_instalacion+"' />";
            html += "<input type='hidden' id='diagnostico_ant' name='diagnostico_ant' value='"+diagnostico+"' />";
            html += "<input type='hidden' id='id_soporte' name='id_soporte' value='"+id+"' />";
            html += "<input type='hidden' id='imp' name='imp' value='0' />";
            
            html += "<table width='100%'>";
            html += "<tr><td><div style='float:left;display:table-cell' width='15%'>C&eacute;dula o RUC: </div></td><td id='ruc' width='35%'>"+ruc+"</td> <td width='15%'>Cliente: </td><td id='raSo' width='35%'>"+razon_social+"</td></tr>";
            html += "<tr><td>Sector:</td><td id='sec'>"+sector+"</td> <td>Cant&oacute;n o ciudad: </td><td id='ciu'>"+ciudad+"</td></tr>";
            html += "<tr><td>Direcci&oacute;n del cliente: </td><td id='dir'>"+direccion+"</td><td>Tel&eacute;fonos:</td>"
                    + "<td id='tel'>"+telefono+"</td></tr>";
            html += "<tr><td>M&oacute;vil claro: </td>"
                    + "<td><input type=\"text\" id=\"movil_claro\" size=\"10\" maxlength=\"10\" value=\""+movil_claro+"\" onkeypress=\"_numero(event);setMovilCliente(event, 'movil_claro');\" /></td>"
                    + "<td>M&oacute;vil movistar:</td>"
                    + "<td><input type=\"text\" id=\"movil_movistar\" size=\"10\" maxlength=\"10\" value=\""+movil_movistar+"\" onkeypress=\"_numero(event);setMovilCliente(event, 'movil_movistar');\" /></td></tr>";
            html += "<tr><td>Direcci&oacute;n de instalaci&oacute;n:</td><td colspan='3' id='dirIns'>"+direccion_instalacion+"</td></tr>";
            html += "<tr><td>Plan actual:</td><td id='plan'>"+plan+"</td> <td>Compartici&oacute;n:</td><td id='com'>"+txt_comparticion+"</td></tr>";
            html += "<tr valign='top'><td>Direcci&oacute;n IP:</td><td id='ip'>"+ip+""
                    + "IP Radio: "+ip_radio+"</td> "
                    + "<td>Receptor:</td><td id='rec'>"+receptor+"<br />"
                    + "CLAVE: "+deviceclave+"  &nbsp;&nbsp; "
                    + "<label onclick=\"set_deviceClave("+id_instalacion+")\">Registrada<input type='checkbox' id='set_deviceclave' name='set_deviceclave' "+(set_deviceclave?"checked":"")+" /></label><br /></td></tr>";
            html += "<tr><td>Antena acoplada:</td><td id='anAc'>"+antena_acoplada+"</td> <td>Servicio:</td><td id='esSe'>"+txt_estado_servicio+"</td></tr></table>";
            html += "<hr />";
            html += "<table>";
            html += "<td>N&uacute;mero: </td>";
            html += "<td>"+id_sucursal+"-<input id='num_soporte' name='num_soporte' type='text' size='10' readonly value='"+num_soporte+"' /></td></tr>";
            html += "<td>Usuario, fecha y hora de reporte: </td>";
            html += "<td>"+alias_contesta + ", " + txt_fecha_llamada+"</td></tr>";
            html += "<td>Persona que llama: <span class='marca'>*</span></td>";
            html += "<td><input id='quien_llama' name='quien_llama' type='text' size='40' onkeypress='_letra(event)' value='"+quien_llama+"' /> &nbsp; ";
            html += "Tel&eacute;fono: <span class='marca'>*</span> ";
            html += "<input id='telefono_llama' name='telefono_llama' type='text' size='10' onkeypress='_numero(event)' value='"+telefono_llama+"' /></td></tr>";
            html += "<tr valign='top'><td>Problema: <span class='marca'>*</span></td>";
            html += "<td><textarea id='problema' name='problema' rows='3' cols='80'>"+problema+"</textarea></td></tr>";
            html += "<tr valign='top'><td>Diagn&oacute;stico t&eacute;cnico: </td>";
            html += "<td><textarea id='diagnostico' name='diagnostico' rows='3' cols='80'>"+diagnostico+"</textarea></td></tr>";
            html += "</table>";

            if(id.compareTo("-1")==0){
                html += "<input type='submit' value='Guardar' onmousedown=\"_('imp').value=0;\" /> &nbsp;&nbsp;&nbsp; ";
                //html += "<input type='submit' value='Guardar e imprimir soporte' onmousedown=\"_('imp').value=1;\" />";
            }else{
                if(estado.compareTo("r")==0){
                    html += "<input type='submit' value='Guardar' onmousedown=\"_('imp').value=0;\" /> &nbsp;&nbsp;&nbsp; ";
                    //html += "<input type='button' value='Re-imprimir soporte' onclick=\"imprimir('pdfSoporte?id="+id+"');\" />";
                }
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

            html += "<div class='grupo' style='width:352px;height:377px;' title='Croquis'>"
                    + "<img id='tagImg' src='"+croquis+"' width='350px' height='377px' border='0' /></div>";

            html += "</div>";


            /* TERCER TAB */


            if(txt_fecha_solucion.compareTo("")==0){
                txt_fecha_solucion = Fecha.getFecha("SQL") + " " + Fecha.getHora();
            }
            html += "<div id='tab12' class='jm_tabHTML' style='display:none;width:700px;height:"+_altBody+"px;'>";

            html += "<form action='frmSoporteSolucionar' onsubmit='return fac_soporteSolucionar(this)' autocomplete='off'>";
            html += "<input type='hidden' id='id' name='id' value='"+id+"' />";
            html += "<input type='hidden' id='idIns1' name='idIns1' value='"+id_instalacion+"' />";
            html += "<input type='hidden' id='num_soporte1' name='num_soporte1' value='"+num_soporte+"' />";
            html += "<input type='hidden' id='problema1' name='problema1' value='"+problema+"' />";
            html += "<input type='hidden' id='antena_acoplada0' value='"+antena_acoplada+"' />";
            html += "<table><tr><td width='150'>Usuario, Fecha y hora de soluci&oacute;n: </td>";
            html += "<td>"+ (alias_solucion.compareTo("")!=0 ? alias_solucion : usuario) + ", " + txt_fecha_solucion+"</td></tr>";
            html += "<tr><td>Antena de trasmisi&oacute;n acoplada: <span class='marca'>*</span></td>";
            html += "<td>" + DatosDinamicos.combo(rsAntenasAcopladas, "antena_acoplada", antena_acoplada, "", "", 200) + "</td></tr>";
            html += "<tr><td colspan='2'>Observaciones y recomendaciones para el cliente: <span class='marca'>*</span><br />";
            html += "<textarea id='recomendacion' name='recomendacion' rows='5' cols='100'>"+recomendacion+"</textarea></td></tr>";
            if(id.compareTo("-1")!=0 && objRol.getAcceso(id_rol, "soporteSolucion") && estado.compareTo("r")==0){
                html += "<td>Generar Orden de Trabajo: </td>";
                html += "<td><input id='odTra' name='odTra' type='checkbox' onclick='fac_genOrdenTrabajo()' /></td></tr>";
            }
            html += "</table>";

            html += "<table width='100%' id='axTOT' style='display:none'>";
            
            html += "<tr><td>Tipo de trabajo: </td>";
            html += "<td>" + DatosDinamicos.combo(rsTrabajos, "tipo_trabajo", "", "", "", 200) + "</td></tr>";
            /*html += "<td><div  id='cob0' style='float:left;display:table-cell'>" + DatosDinamicos.combo(rsTrabajos, "tipo_trabajo", "", "", "", 200) + "</div>"
                    + "<div class='jm_icoCon' id='gr0' style='visibility:hidden;background-position: -545px 0px;' onclick=\"guardarComun(null,'cob0','idC','gr0','add0',150,'TRB');\" title='guardar'>&nbsp;</div>"
                    + "<div class='jm_icoCon' id='add0' style='background-position:-504px -14px;' onclick=\"insertarComun('cob0','idC','gr0','add0',150,'TRB');\" title='Insertar'>&nbsp;</div></td></tr>";*/
            html += "<tr><td>Fecha y hora sugerida por el cliente para el trabajo: </td>";
            html += "<td><input id='fecha_cliente' name='fecha_cliente' type='text' size='10' maxlength='10' onkeypress=\"_SUP(event)\" />"
                    + "<input type='button' value='...' onClick=\"SelectorFecha.crear('fecha_cliente', 'SQL');\" /> &nbsp; "
                    + "hora: <input type='text' id='hora_cliente' name='hora_cliente' size='5' maxlength='5' onkeypress=\"_evaluar(event, '0123456789:')\" /></td></tr>";
            html += "<tr><td valign='top'>Diagn&oacute;stico t&eacute;cnico: <span class='marca'>*</span></td>";
            html += "<td><textarea id='diagnostico_tecnico' name='diagnostico_tecnico' rows='3' cols='80'>"+diagnostico+"</textarea></td></tr>";
            /*
            html += "<td>Fecha: </td>";
            html += "<td><input id='fecha_reporte' name='fecha_reporte' type='text' size='10' readonly value='"+Fecha.getFecha("SQL")+"' /></td></tr>";
            html += "<tr><td>Tipo de trabajo: </td>";
            html += "<td><div  id='cob0' style='float:left;display:table-cell'>" + DatosDinamicos.combo(rsTrabajos, "tipo_trabajo", "", "", "", 200) + "</div>"
                    + "<div class='jm_icoCon' id='gr0' style='visibility:hidden;background-position: -545px 0px;' onclick=\"guardarComun(null,'cob0','idC','gr0','add0',150,'TRB');\" title='guardar'>&nbsp;</div>"
                    + "<div class='jm_icoCon' id='add0' style='background-position:-504px -14px;' onclick=\"insertarComun('cob0','idC','gr0','add0',150,'TRB');\" title='Insertar'>&nbsp;</div></td></tr>";
            html += "<tr><td>Fecha de realizaci&oacute;n: </td>";
            html += "<td><input id='fecha_realizacion' name='fecha_realizacion' type='text' size='10' maxlength='10' value='"+Fecha.getFecha("SQL")+"' readonly />"
                    + "<input type='button' value='...' onClick=\"SelectorFecha.crear('fecha_realizacion', 'SQL');\" /></td></tr>";
            html += "<tr><td>Responsable del trabajo: </td>";
            html += "<td>" + DatosDinamicos.combo(rsEmpleados, "id_empleado", "", "", "", 200) + "</td></tr>";
            html += "<tr><td valign='top'>Diagn&oacute;stico t&eacute;cnico: <span class='marca'>*</span></td>";
            html += "<td><textarea id='diagnostico_tecnico' name='diagnostico_tecnico' rows='3' cols='80'>"+diagnostico+"</textarea></td></tr>";
            */
            html += "</table>";

            if(id.compareTo("-1")!=0 && objRol.getAcceso(id_rol, "soporteSolucion") && estado.compareTo("r")==0){
                html += "<input id='btn' type='submit' value='Guardar Soluci&oacute;n' />";
            }
            html += "</form>";

            html += "</div></div></div>";

            out.println(html);


        } finally {
            objAntena.cerrar();
            conf.cerrar();
            objOrdenTrabajo.cerrar();
            objRol.cerrar();
            objSoporte.cerrar();
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