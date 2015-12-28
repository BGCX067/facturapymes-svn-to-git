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

package jm.nom;

import java.io.*;

import javax.servlet.*;
import javax.servlet.http.*;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import jm.seg.clas.Rol;
import jm.nom.clas.Empleado;
import jm.nom.clas.Horario;
import jm.adm.clas.Comun;
import jm.adm.clas.Ubicacion;
import jm.adm.clas.Sucursal;
import jm.nom.clas.Cargo;
import jm.seg.clas.Usuario;
import jm.web.Archivo;
import jm.web.DatosDinamicos;
import jm.web.Fecha;
import jm.nom.clas.permisosLicencias;
import jm.web.Addons;

/**
 *
 * @author Jorge
 */
public class frmPermisoAdm extends HttpServlet {
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
        String id_rol_sesion = (String)sesion.getAttribute("id_rol");
        String usuario = (String)sesion.getAttribute("usuario");
        String clave = (String)sesion.getAttribute("clave");

        response.setContentType("text/html;charset=UTF-8");
        response.setHeader("Pragma", "no-cache");
        response.setHeader("Expires", "Mon, 01 Jan 2001 00:00:01 GMT");
        response.setHeader("Cache-Control", "no-store");
        response.setHeader("Cache-Control", "must-revalidate");
        response.setHeader("Cache-Control", "no-cache");
        PrintWriter out = response.getWriter();
        
        permisosLicencias objPermLic = new permisosLicencias(this._ip, this._puerto, this._db, usuario, clave);
        Sucursal objSuc = new Sucursal(this._ip, this._puerto, this._db, usuario, clave);
        Empleado objEmpleado = new Empleado(this._ip, this._puerto, this._db, usuario, clave);
        Comun objComun = new Comun(this._ip, this._puerto, this._db, usuario, clave);
        Ubicacion objUbicacion = new Ubicacion(this._ip, this._puerto, this._db, usuario, clave);
        Rol objRol = new Rol(this._ip, this._puerto, this._db, usuario, clave);
        Horario objHorario = new Horario(this._ip, this._puerto, this._db, usuario, clave);
        Usuario objUsuario = new Usuario(this._ip, this._puerto, this._db, usuario, clave);
        Cargo objCargo = new Cargo(this._ip, this._puerto, this._db, usuario, clave);
        
        try {
            String id = request.getParameter("id");
            String alto = request.getParameter("altB");
            int ancho = 450;
            int _altBody = Integer.parseInt(alto);

            String id_tipo = "";
            String id_empleado = "";
            String tipo = "";
            String numdias = "00";
            String numhoras = "00";
            String fechainicio = "";
            String forma_rec = "";
            String hora_inicio = "00";
            String num_minutos = "00";
            String descripcion = "";
            String permisoestado = "";
            String observaciones = "";
            String horaIni="0";
            String minutosIni="0";
            String estado="";
            //boolean sexo = true;
            

            ResultSet rsSucursales = objSuc.getSucursales();
            
            try{
                ResultSet rsPermisos = objPermLic.getPermisos(id);
                if(rsPermisos.next()){
                    numdias = (rsPermisos.getString("numdias")!=null) ? rsPermisos.getString("numdias") : "0";
                    tipo = (rsPermisos.getString("nombretp")!=null) ? rsPermisos.getString("nombretp") : "";
                    id_tipo = (rsPermisos.getString("forma_rec")!=null) ? rsPermisos.getString("forma_rec") : "";
                    id_empleado = (rsPermisos.getString("id_empleado")!=null) ? rsPermisos.getString("id_empleado") : "";
                    numhoras= (rsPermisos.getString("numhoras")!=null) ? rsPermisos.getString("numhoras") : "00";
                    fechainicio = (rsPermisos.getString("fechainicio")!=null) ? rsPermisos.getString("fechainicio") : "";
                    forma_rec = (rsPermisos.getString("formarecuperacion")!=null) ? rsPermisos.getString("formarecuperacion") : "";
                    //sexo = (rsPermisos.getString("sexo")!=null) ? rsPermisos.getBoolean("sexo") : false;
                    hora_inicio = (rsPermisos.getString("hora_inicio")!=null) ? rsPermisos.getString("hora_inicio") : "";
                    descripcion = (rsPermisos.getString("descripcion")!=null) ? rsPermisos.getString("descripcion") : "";
                    estado = (rsPermisos.getString("permisoestado")!=null) ? rsPermisos.getString("permisoestado") : "";
                    permisoestado = (rsPermisos.getString("estadopermiso")!=null) ? rsPermisos.getString("estadopermiso") : "";
                    observaciones = (rsPermisos.getString("observaciones")!=null) ? rsPermisos.getString("observaciones") : ""; 
                    num_minutos = (rsPermisos.getString("num_minutos")!=null) ? rsPermisos.getString("num_minutos") : "00"; 
                    horaIni=hora_inicio.substring(0, 2);
                    minutosIni=hora_inicio.substring(3, 5);
                    rsPermisos.close();
                }
            }catch(Exception e){
                e.printStackTrace();
            }


            ResultSet rsRoles = objRol.getRoles();
            
            String diasIngresados = "";
            int diasTotal = 0;
            String diasget= "";
            String diasPedidos = "";
            String horasPedidos = "";
            String minutosPedidos = "";
            String diasVacacionesTotal="";
            
            try{
                ResultSet rsDetallesPermisos = objPermLic.getDetallesPermisos(id_empleado);
                if(rsDetallesPermisos.next()){
                    diasIngresados = (rsDetallesPermisos.getString("diasvacaciones")!=null) ? rsDetallesPermisos.getString("diasvacaciones") : "0";
                    diasget = (rsDetallesPermisos.getString("meses")!=null) ? rsDetallesPermisos.getString("meses") : "0";
                    diasPedidos = (rsDetallesPermisos.getString("diaspedidos")!=null) ? rsDetallesPermisos.getString("diaspedidos") : "0";
                    horasPedidos= (rsDetallesPermisos.getString("horaspedidos")!=null) ? rsDetallesPermisos.getString("horaspedidos") : "0";
                    minutosPedidos = (rsDetallesPermisos.getString("minutospedidos")!=null) ? rsDetallesPermisos.getString("minutospedidos") : "0";
                    diasVacacionesTotal = (rsDetallesPermisos.getString("vacaciones")!=null) ? rsDetallesPermisos.getString("vacaciones") : "0";
                    rsDetallesPermisos.close();
                }
            }catch(Exception e){
                e.printStackTrace();
            }
            diasget=""+Double.parseDouble(diasget)*1.25;
            diasTotal=(int)Double.parseDouble(diasget);
            
                 if(Integer.parseInt(minutosPedidos)>60){
                    int masHoras=0;
                    masHoras=Integer.parseInt(minutosPedidos)/60;
                    horasPedidos=horasPedidos+masHoras;
                    minutosPedidos=""+Integer.parseInt(minutosPedidos)%60;
                }
                if(Integer.parseInt(horasPedidos)>7){
                    int masDias=0;
                    masDias=Integer.parseInt(horasPedidos)/7;
                    diasPedidos=diasPedidos+masDias;
                    horasPedidos=""+Integer.parseInt(horasPedidos)%7;
                }

            String html = "obj»d_21^foc»c^fun»_objTab.seleccionar(0);^frm»";
            html += "<div class='H3' style='width:"+ancho+"px'>Formulario de Empleado</div>";

            html += "<div class='grupoTab' style='width:"+(ancho)+"px;'><div id='tabR0'>"
                    + "<a id='tab00' href='javascript:void(0);' class='jm_tabAct' onclick='_objTab.seleccionar(0);'>Permiso</a>"
                    + "<a id='tab02' href='javascript:void(0);' class='jm_tab' onclick='_objTab.seleccionar(2);'>Documento Adjunto</a></div>";
            /*onclick='_objTab.seleccionar(0);' 2 3     */

            html += "<form action='frmPermisoAdmGuardar' onsubmit='return pyl_admPermisoGuardar(this)' autocomplete='off'>";
            html += "<input type='hidden' id='id' name='id' value='"+id+"' />";
            html += "<input type='hidden' id='id_empleado' name='id_empleado' value='"+id_empleado+"' />";

            /* PRIMER TAB */

            html += "<div id='tabR1'>";
            html += "<div id='tab10' class='jm_tabHTML' style='display:block;width:"+(ancho-30)+"px;height:"+_altBody+"px;'>";
            html += "<table>";
            html += "<tr><td nowrap><b>Tipo de Permiso: </b>"+tipo+"</td>";
            html+="<tr><td><b>Nº de Dias: </b>"+numdias+"</td>";
            html+= "</tr><td><b>Nº de Horas: </b>"+numhoras+":"+num_minutos+"</td>";
            html+= "</tr><tr><td>-----------------------------------------------</td></tr>";
            html += "<tr><td nowrap><b>Descripcion: </b>"+descripcion+"</td>";        
            html += "<tr><input type='hidden' id='diasPedidos' name='diasPedidos' hidden value='"+fechainicio+"' /><td><b>Fecha Inicial: </b>"+fechainicio+"</td>";
            html+="<tr><td><b>Hora Inicial: </b>"+horaIni+":"+minutosIni+"</td>";
            html+= "</tr><tr><td>-----------------------------------------------</td></tr>";
            html+="<tr><td><b>Forma de Recuperacion: </b>"+forma_rec+"</td>";
            if(id_tipo.compareTo("1")==0){
                html+="<tr><td><b>Nº Total de Dias de Vacaciones: </b>"+diasTotal+"</td>";
                /*html += "<tr><td nowrap><b>Nº de Dias Pedidos Actualmente: </b>"+(Integer.parseInt(diasVacacionesTotal)+Integer.parseInt(diasPedidos))+""
                            + " Dias - "+horasPedidos+" Horas - "+minutosPedidos+" Minutos</td></tr>";*/
                html += "<tr><td nowrap><b>Nº de Dias Restantes: </b>"+((diasTotal)-Integer.parseInt(diasVacacionesTotal)-Integer.parseInt(diasPedidos)-Integer.parseInt(diasIngresados)-1)+""
                            + " Dias - "+(7-Integer.parseInt(horasPedidos))+" Horas - "+(60-Integer.parseInt(minutosPedidos))+" Minutos</td></tr>";
            }
            ResultSet rsPermisoDetalle = objPermLic.getPermisosDetalles(id);
            if(id_tipo.compareTo("2")==0){                
                html += "<TABLE id=\"mytable\" cellspacing='0' cellpadding='0'>"
                        + "<TR>" +
                            "<TH class='jm_TH' width='100'>Dia/Mes/Año</TH>" +
                            "<TH class='jm_TH' width='100'>Hora Entrada</TH>" +
                            "<TH class='jm_TH' width='100'>Hora Salida</TH>" +
                            "<TH class='jm_TH' width='20'>&nbsp;</TH></TR>"
                        + "</TABLE>";
                   html += "<DIV style='overflow:auto;' id='sll'>" +
                                "<TABLE class='jm_tabla' cellspacing='1' cellpadding='0' id='tblPE'>";
                    int i=0;
                    String dia = "";
                    String horaEntrada = "";
                    String horaSalida = "";
                    String id_permisoDetalle = "";                    
                    try {
                        while(rsPermisoDetalle.next()){
                            id_permisoDetalle= (rsPermisoDetalle.getString("id_permiso")!=null) ? rsPermisoDetalle.getString("id_permiso") : "";
                            dia = (rsPermisoDetalle.getString("dia")!=null) ? rsPermisoDetalle.getString("dia") : "";
                            horaEntrada = (rsPermisoDetalle.getString("hora_entrada")!=null) ? rsPermisoDetalle.getString("hora_entrada") : "";
                            horaSalida = (rsPermisoDetalle.getString("hora_salida")!=null) ? rsPermisoDetalle.getString("hora_salida") : "";

                            html += "<tr id='rFO"+id_permisoDetalle+"' valign='top' class='jm_filaPar' style='cursor:pointer' onmouseover=\"this.className='jm_filaSobre'\" onmouseout=\"this.className='jm_filaPar'\">";
                            html += "<td width='100' style='cursor:pointer' \">"+dia+"</td>";
                            html += "<td width='100' style='cursor:pointer' \">"+horaEntrada+"</td>";
                            html += "<td width='100' style='cursor:pointer' \">"+horaSalida+"</td>";
                            html += "<td width='21' align='center'><div class='jm_icoCon' style='background-position:-545px -18px;' title='Eliminar'>&nbsp;</div></td></tr>";
                            i++;
                        }
                    } catch (SQLException ex) {
                        Logger.getLogger(frmEmpleado.class.getName()).log(Level.SEVERE, null, ex);
                    }
                   html += "</table></div>";                
            }
            html += "<tr><td>-----------------------------------------------<br></td></tr>";
            html += "<br><tr><td><b>Estado de Permiso:</b> "+permisoestado+"</td></tr>";
            if(estado.compareTo("s")==0){
                html += "<tr><td><label><input type='radio' id='permiso0' name='permiso' value='a' /> APROBADO</label>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; " +
                               "<label><input type='radio' id='permiso1' name='permiso' value='r' /> RECHAZADO</label></td></tr><br>";
                html += "<tr><td colspan='2'><input type='submit' value='Guardar' /></td></tr>";
            }
            html += "<tr><td nowrap><b><br>Descripcion: </b><br>";
            html += "<textarea id='descripcion' name='descripcion'>"+observaciones+"</textarea></td></tr>"; 

            html += "</table></div>";            
            
            
            /* Segundo TAB */
            html += "<div id='tab12' class='jm_tabHTML' style='display:none;width:"+(ancho-30)+"px;height:"+_altBody+"px;'><br>";
             html += "<fieldset style='background-color:#CCEBFF'>";
                html += "<legend style='background-color:#CCEBFF'>Documento Adjunto(Opcional): </legend>";
                
                Archivo archivo = new Archivo(this._ip, this._puerto, this._db, usuario, clave);
                String path = String.valueOf(request.getRequestURL());
                path = path.substring(0, path.lastIndexOf("/"));
                String foto = path + "/img/" + archivo.getArchivo(this._dir, "tbl_permiso", id, "nombre", "imagen");
                archivo.cerrar();

                String subir = (id.compareTo("-2")!=0) ? "adm_setImagenPermiso('tbl_permiso', 'nombre', 'imagen', '"+id+"');" : "";

                html +="<div class='grupo' style='width:382px;height:477px;cursor:pointer;' title='Haga click aqu&iacute; para insertar el croquis' onclick=\""+subir+"\">"
                        + "<img id='tagImg' src='"+foto+"' width='382px' height='477px' border='0' /></div>"
                        + "<div ><br>";
                
                html += "</fieldset>";
            html += "</div>";
            html += "</div></form></div>";
            out.println(html);

            try{
                rsSucursales.close();
                rsRoles.close();
                rsPermisoDetalle.close();
            }catch(Exception e){
                e.printStackTrace();
            }

            
        } finally {
            objSuc.cerrar();
            objUsuario.cerrar();
            objRol.cerrar();
            objCargo.cerrar();
            objComun.cerrar();
            objUbicacion.cerrar();
            objEmpleado.cerrar();
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
