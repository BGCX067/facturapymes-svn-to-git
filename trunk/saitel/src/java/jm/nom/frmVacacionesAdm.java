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
public class frmVacacionesAdm extends HttpServlet {
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

            String id_empleado = "";
            String fechaInicio = "";
            String fechaFin = "00";
            String numDias = "00";
            String numSolicitud = "";
            String fechaPedido = "";
            String observaciones = "";
            String usuarioRespuesta = "";
            String FechaRespuesta = "";
            String permisoestado = "";
            String estado = "";

            //boolean sexo = true;
            

            ResultSet rsSucursales = objSuc.getSucursales();
            
            try{
                ResultSet rsPermisos = objPermLic.getVacaciones(id);
                if(rsPermisos.next()){
                    id_empleado = (rsPermisos.getString("id_empleado")!=null) ? rsPermisos.getString("id_empleado") : "";
                    fechaInicio = (rsPermisos.getString("fecha_inicio")!=null) ? rsPermisos.getString("fecha_inicio") : "";
                    fechaFin = (rsPermisos.getString("fecha_fin")!=null) ? rsPermisos.getString("fecha_fin") : "";
                    numDias = (rsPermisos.getString("numdias")!=null) ? rsPermisos.getString("numdias") : "";
                    numSolicitud= (rsPermisos.getString("num_solicitud")!=null) ? rsPermisos.getString("num_solicitud") : "";
                    fechaPedido = (rsPermisos.getString("fecha_pedido")!=null) ? rsPermisos.getString("fecha_pedido") : "";
                    observaciones = (rsPermisos.getString("observacion")!=null) ? rsPermisos.getString("observacion") : "";
                    //sexo = (rsPermisos.getString("sexo")!=null) ? rsPermisos.getBoolean("sexo") : false;
                    usuarioRespuesta = (rsPermisos.getString("usuario_respuesta")!=null) ? rsPermisos.getString("usuario_respuesta") : "";
                    FechaRespuesta = (rsPermisos.getString("fecha_respuesta")!=null) ? rsPermisos.getString("fecha_respuesta") : "";
                    permisoestado = (rsPermisos.getString("estado_solicitud")!=null) ? rsPermisos.getString("estado_solicitud") : "";
                    estado = (rsPermisos.getString("estado")!=null) ? rsPermisos.getString("estado") : "";
                    rsPermisos.close();
                }
            }catch(Exception e){
                e.printStackTrace();
            }

            String html = "obj»d_21^foc»c^fun»_objTab.seleccionar(0);^frm»";
            html += "<div class='H3' style='width:"+ancho+"px'>Formulario de Pedido de Vacaciones</div>";

            html += "<div class='grupoTab' style='width:"+(ancho)+"px;'><div id='tabR0'>"
                    + "<a id='tab00' href='javascript:void(0);' class='jm_tabAct' onclick='_objTab.seleccionar(0);'>Administración de Vacaciones</a></div>";
            /*onclick='_objTab.seleccionar(0);' 2 3     */

            html += "<form action='frmVacacionesAdmGuardar' onsubmit='return pyl_admVacacionesGuardar(this)' autocomplete='off'>";
            html += "<input type='hidden' id='id' name='id' value='"+id+"' />";
            html += "<input type='hidden' id='id_empleado' name='id_empleado' value='"+id_empleado+"' />";

            /* PRIMER TAB */

            html += "<div id='tabR1'>";
            html += "<div id='tab10' class='jm_tabHTML' style='display:block;width:"+(ancho-30)+"px;height:"+_altBody+"px;'>";
            html += "<table>";
            html += "<tr><input type='hidden' id='fechaInicio' name='fechaInicio' value='"+fechaInicio+"' /><td nowrap><b>Fecha Inicio: </b>"+fechaInicio+"</td></tr>";
            html += "<tr><td nowrap><b>Fecha Fin: </b>"+fechaFin+"</td></tr>";
            html += "<tr><td nowrap><b>Numero de Dias: </b>"+numDias+"</td></tr>";
            html += "<tr><td nowrap><b>Numero de Solicitud: </b>"+numSolicitud+"</td></tr>";
            html += "<tr><td nowrap>----------------------</td></tr>";
            html += "<tr><td nowrap><b>Fecha Pedido: </b>"+fechaPedido+"</td></tr>";
            html += "<tr><td nowrap><b>Estado: </b>"+permisoestado+"</td></tr>";
            html += "<tr><td nowrap><b>Observaciones: </b><br>";
            html += "<textarea id='descripcion' name='descripcion' maxlength=\"40\">"+observaciones+"</textarea></td></tr>"; 
            if(FechaRespuesta.compareTo("")!=0){
                html += "<tr><td nowrap><b>Fecha de Respuesta: </b>"+FechaRespuesta+"</td></tr>";
                html += "<tr><td nowrap><b>Usuario Respuesta: </b>"+usuarioRespuesta+"</td></tr>";
            }
            
            if(estado.compareTo("s")==0){
                html += "<tr><td><label><input type='radio' id='permiso0' name='permiso' value='a' /> APROBADO</label>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; " +
                               "<label><input type='radio' id='permiso1' name='permiso' value='r' /> RECHAZADO</label></td></tr><br>";
                html += "<tr><td colspan='2'><input type='submit' value='Guardar' /></td></tr>";
            }
            

            html += "</table></div>";            

            html += "</div></form></div>";
            out.println(html);

            try{
                rsSucursales.close();
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
