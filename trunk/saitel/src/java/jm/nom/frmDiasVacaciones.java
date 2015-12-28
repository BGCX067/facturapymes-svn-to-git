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
import jm.web.Matriz;

/**
 *
 * @author Jorge
 */
public class frmDiasVacaciones extends HttpServlet {
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
        Usuario objUsuario = new Usuario(this._ip, this._puerto, this._db, usuario, clave);
        Cargo objCargo = new Cargo(this._ip, this._puerto, this._db, usuario, clave);
        
        try {
            String id = request.getParameter("id");
            String alto = request.getParameter("altB");
            int ancho = 450;
            int _altBody = Integer.parseInt(alto);

            String dias_ingresados = "0";
            String fecha_ingreso = "0";
            String nombre = "0";
            String apellido = "0";
            //String [][] matriz= objPermLic.getDiasVacacionesId(id);
            //boolean sexo = true;
            

            ResultSet rsSucursales = objSuc.getSucursales();            

            try{
                ResultSet rsPermisos = objPermLic.getEmpleadoDetalles(id);
                if(rsPermisos.next()){
                    dias_ingresados = (rsPermisos.getString("diasvacaciones")!=null) ? rsPermisos.getString("diasvacaciones") : "0"; 
                    fecha_ingreso = (rsPermisos.getString("fecha_ingreso")!=null) ? rsPermisos.getString("fecha_ingreso") : "0"; 
                    nombre = (rsPermisos.getString("nombre")!=null) ? rsPermisos.getString("nombre") : "0"; 
                    apellido = (rsPermisos.getString("apellido")!=null) ? rsPermisos.getString("apellido") : "0"; 
                    rsPermisos.close();
                }
            }catch(Exception e){
                e.printStackTrace();
            }

            int diasTotal = 0;
            String diasget= "";
            String meses = "";
            String diasPedidos = "";
            String horasPedidos = "";
            String minutosPedidos = "";;
            String diasVacacionesTotal="";
            
            try{
                ResultSet rsDetallesPermisos = objPermLic.getDetallesPermisos(id);
                if(rsDetallesPermisos.next()){
                    diasget = (rsDetallesPermisos.getString("meses")!=null) ? rsDetallesPermisos.getString("meses") : "0";
                    meses = (rsDetallesPermisos.getString("meses")!=null) ? rsDetallesPermisos.getString("meses") : "0";
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
                    + "<a id='tab00' href='javascript:void(0);' class='jm_tabAct' >Reporte</a>"
                    + "</div>";
            /*onclick='_objTab.seleccionar(0);' 2 3     */

            html += "<form name='formPermisos' action='frmEmpleadoGuardar' onsubmit='return nom_permisoBuscar(this)' autocomplete='off'>";
            html += "<input type='hidden' id='id' name='id' value='"+id+"' style=\"width:10px;height:15px\"/>";

            /* PRIMER TAB */
            html += "<div id='tabR1'>";
            html += "<div id='tab10' class='jm_tabHTML' style='display:block;width:"+(ancho-30)+"px;height:"+_altBody+"px;'>";
            html += "<b>"+nombre+" "+apellido+"</b><br>";
            html += "Dias Ingresados Manualmente: "+dias_ingresados;//+"<nput id='diasIngresado' name='diasIngresado' type='text' class='may' size='2' value='"+dias_ingresados+"' /><br><br>";
            html +="<br><b>Fecha de Ingreso: </b>"+fecha_ingreso;
            html +="<br><br><b>Detalles</b>";
            html += "<div id='axTblPE'>"
                        + "<TABLE id=\"mytable\" cellspacing='0' cellpadding='0'>"
                        + "<TR>" +
                            "<TH class='jm_TH' width='100'>Tipo</TH>" +
                            "<TH class='jm_TH' width='100'>Fecha Pedido</TH>" +
                            "<TH class='jm_TH' width='100'>N. Dias</TH>" +
                            "<TH class='jm_TH' width='100'>N. Horas</TH>" +
                            "<TH class='jm_TH' width='100'>Estado</TH>" +
                            "<TH class='jm_TH' width='20'>&nbsp;</TH></TR>"
                        + "</TABLE>";
               html += "<DIV style='overflow:auto;' id='sll'>" +
                            "<TABLE class='jm_tabla' cellspacing='1' cellpadding='0' id='tblPE'>";
                int i=0;
                String idTipo = "";
                String Tipo = "";
                String fechaPedido = "";
                String numDias = "";
                String numHoras = "";
                String estado = "";
                ResultSet rsVacacionesDetalle = objPermLic.getVacacionesDetalles(id);
                try {
                    while(rsVacacionesDetalle.next()){
                        idTipo= (rsVacacionesDetalle.getString("id_empleado_vacaciones")!=null) ? rsVacacionesDetalle.getString("id_empleado_vacaciones") : "";
                        fechaPedido = (rsVacacionesDetalle.getString("fecha_pedido")!=null) ? rsVacacionesDetalle.getString("fecha_pedido") : "";
                        numDias = (rsVacacionesDetalle.getString("numdias")!=null) ? rsVacacionesDetalle.getString("numdias") : "";
                        estado = (rsVacacionesDetalle.getString("observacion")!=null) ? rsVacacionesDetalle.getString("observacion") : "";
                        
                        html += "<tr id='rFO"+idTipo+"' valign='top' class='jm_filaPar' style='cursor:pointer' onmouseover=\"this.className='jm_filaSobre'\" onmouseout=\"this.className='jm_filaPar'\">";
                        html += "<td width='100' style='cursor:pointer' \">Vacaciones</td>";
                        html += "<td width='100' style='cursor:pointer' \">"+fechaPedido+"</td>";
                        html += "<td width='100' style='cursor:pointer' \">"+numDias+"</td>";
                        html += "<td width='100' style='cursor:pointer' \">0</td>";
                        html += "<td width='100' style='cursor:pointer' \">"+estado+"</td>";
                        html += "<td width='21' align='center'><div class='jm_icoCon' style='background-position:-545px -18px;' title='Eliminar'>&nbsp;</div></td>";
                        /*html += "<td align='center' width='100'><a href='formatoFactura?id="+id_punto_emision+"' target='_blank'>modificar formato</a></td>";
                        html += "<td align='center' width='20'><input type='hidden' id='idR"+i+"' value='"+id_punto_emision+"' />&nbsp;</td></tr>";*/
                        i++;
                    }
                } catch (SQLException ex) {
                    Logger.getLogger(frmEmpleado.class.getName()).log(Level.SEVERE, null, ex);
                }
                ResultSet rsEmpleadoPermiso = objPermLic.getPermisosDeta(id);
                try {
                    while(rsEmpleadoPermiso.next()){
                        idTipo= (rsEmpleadoPermiso.getString("id_permiso")!=null) ? rsEmpleadoPermiso.getString("id_permiso") : "";
                        Tipo= (rsEmpleadoPermiso.getString("formarecuperacion")!=null) ? rsEmpleadoPermiso.getString("formarecuperacion") : "";
                        fechaPedido = (rsEmpleadoPermiso.getString("fecha_pedido")!=null) ? rsEmpleadoPermiso.getString("fecha_pedido") : "";
                        numDias = (rsEmpleadoPermiso.getString("numdias")!=null) ? rsEmpleadoPermiso.getString("numdias") : "";
                        numHoras = (rsEmpleadoPermiso.getString("numhoras")!=null) ? rsEmpleadoPermiso.getString("numhoras") : "";
                        estado = (rsEmpleadoPermiso.getString("estadopermiso")!=null) ? rsEmpleadoPermiso.getString("estadopermiso") : "";
                        
                        html += "<tr id='rFO"+idTipo+"' valign='top' class='jm_filaPar' style='cursor:pointer' onmouseover=\"this.className='jm_filaSobre'\" onmouseout=\"this.className='jm_filaPar'\">";
                        html += "<td width='100' style='cursor:pointer' \">"+Tipo+"</td>";
                        html += "<td width='100' style='cursor:pointer' \">"+fechaPedido+"</td>";
                        html += "<td width='100' style='cursor:pointer' \">"+numDias+"</td>";
                        html += "<td width='100' style='cursor:pointer' \">"+numHoras+"</td>";
                        html += "<td width='100' style='cursor:pointer' \">"+estado+"</td>";
                        html += "<td width='21' align='center'><div class='jm_icoCon' style='background-position:-545px -18px;' title='Eliminar'>&nbsp;</div></td>";
                        /*html += "<td align='center' width='100'><a href='formatoFactura?id="+id_punto_emision+"' target='_blank'>modificar formato</a></td>";
                        html += "<td align='center' width='20'><input type='hidden' id='idR"+i+"' value='"+id_punto_emision+"' />&nbsp;</td></tr>";*/
                        i++;
                    }
                } catch (SQLException ex) {
                    Logger.getLogger(frmEmpleado.class.getName()).log(Level.SEVERE, null, ex);
                }
               html += "</table></div><br>";

            html +="<b>Dias Calculados por el sistema (+): "+diasTotal+"</b><br>";
            html +="<b>Vacaciones Ingresados Manualmente (-): "+dias_ingresados+"</b><br>";
            html +="<b>Dias Pedidos con Cargo a Vacaciones (-): "+diasPedidos+"</b><br>";
            html +="<b>Horas Pedidos con Cargo a Vacaciones (-): "+horasPedidos+"</b><br>";
            html +="<b>Dias de Vacaciones (-): "+diasVacacionesTotal+"</b><br>";
            double total=(diasTotal-Integer.parseInt(dias_ingresados)-Integer.parseInt(diasPedidos)-Integer.parseInt(diasVacacionesTotal));
            html +="<b>Total (=): "+total+"</b><br>";
            html += "</table></div>";
            html += "</div></form></div>";
            out.println(html);

            try{
                rsSucursales.close();
                rsVacacionesDetalle.close();
                rsEmpleadoPermiso.close();
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
