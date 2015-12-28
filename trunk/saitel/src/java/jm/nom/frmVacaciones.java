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
public class frmVacaciones extends HttpServlet {
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
            String fechainicio = "";
            String numDias = "";
            String id_empleado = objPermLic.getId(usuario);
            //boolean sexo = true;
            

            ResultSet rsSucursales = objSuc.getSucursales();
            ResultSet rsPermisosLicencias = objPermLic.getTipoPermiso("false");
            
            if(id.compareTo("-1")!=0){
                try{
                    ResultSet rsPermisos = objPermLic.getVacaciones(id);
                    if(rsPermisos.next()){
                        id_tipo = (rsPermisos.getString("id_empleado_vacaciones")!=null) ? rsPermisos.getString("id_empleado_vacaciones") : "";
                        fechainicio= (rsPermisos.getString("fecha_inicio")!=null) ? rsPermisos.getString("fecha_inicio") : "";
                        numDias = (rsPermisos.getString("numdias")!=null) ? rsPermisos.getString("numdias") : "";
                        rsPermisos.close();
                    }
                }catch(Exception e){
                    e.printStackTrace();
                }
            }

            ResultSet rsDepartamentos = objComun.getArea("false");
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
                diasTotal=(diasTotal)-Integer.parseInt(diasVacacionesTotal)-Integer.parseInt(diasPedidos)-Integer.parseInt(diasIngresados);
                
                String html = "obj»d_21^foc»c^fun»_objTab.seleccionar(0);^frm»";
            html += "<div class='H3' style='width:"+ancho+"px'>Formulario de Pedido de Vacaciones</div>";

            html += "<div class='grupoTab' style='width:"+(ancho)+"px;'><div id='tabR0'>"
                    + "<a id='tab00' href='javascript:void(0);' class='jm_tabAct' onclick='_objTab.seleccionar(0);'>Vacaciones</a></div>";
            /*onclick='_objTab.seleccionar(0);' 2 3     */

            html += "<form action='frmVacacionesGuardar' onsubmit='return pyl_VacacionesGuardar(this)' autocomplete='off'>";
            html += "<input type='hidden' id='id' name='id' value='"+id+"' />";
            html += "<input type='hidden' id='id_empleado' name='id_empleado' value='"+id_empleado+"' />";

            /* PRIMER TAB */
            html += "<div id='tabR1'>";
            html += "<div id='tab10' class='jm_tabHTML' style='display:block;width:"+(ancho-30)+"px;height:"+_altBody+"px;'>";
            html += "<table>";
            html += "<tr><td>Fecha Inicial: <span class='marca'>*</span></td>";
            html += "<td nowrap><input id='fecha_permiso' name='fecha_permiso' type='text' size='10' value='"+fechainicio+"' onkeypress=\"_SUP(event);\"/> "
                    + "<input type='button' value='...' onClick=\"SelectorFecha.crear('fecha_permiso', 'SQL');\" /></td></tr>";
            html+="<tr><td>Nº de Dias: </td>"
                    + "<td><input type='number' id='nDias' name='nDias' style='width:40px' max="+(diasTotal)+" min=0 value="+numDias+" ></td></tr>";
            html+="<tr><td>Dias Disponibles: </td>"
            + "<td><input type='hidden' id='Disponible' name='Disponible' value="+diasTotal+">"+(diasTotal)+"</td></tr>";
            html += "<tr><td colspan='2'><input type='submit' value='Guardar' /></td></tr>";
            html += "</table></div>";
            
            html += "</div></form></div>";
            out.println(html);

            try{
                rsSucursales.close();
                rsRoles.close();
                rsDepartamentos.close();
                rsPermisosLicencias.close();
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
