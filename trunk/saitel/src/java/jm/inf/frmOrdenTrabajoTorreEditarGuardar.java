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
import java.sql.ResultSet;

import javax.servlet.*;
import javax.servlet.http.*;
import jm.adm.clas.Mensaje;
import jm.inf.clas.OrdenTrabajo;
import jm.seg.clas.Auditoria;
import jm.web.Fecha;

/**
 *
 * @author Jorge
 */
public class frmOrdenTrabajoTorreEditarGuardar extends HttpServlet {
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
        String empleado = (String)sesion.getAttribute("empleado");
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
        
        OrdenTrabajo objOrdenTrabajo = new OrdenTrabajo(this._ip, this._puerto, this._db, usuario, clave);
        
        try {
            String r = "msg»Ha ocurrido un error inesperado, por favor, vuelva a intentarlo más tarde o " +
                    "contáctese con el administrador del sistema para mayor información.";
            String WHERE = request.getParameter("WHERE");
            String p = request.getParameter("p") != null ? request.getParameter("p") : "0";
            //String id_instalacion = request.getParameter("idIns");
            String id_orden_trabajo = request.getParameter("id_orden_trabajo");
            String id_nodo = request.getParameter("id_nodo");
            String fecha_cliente = request.getParameter("fecha_cliente");
            String hora_cliente = request.getParameter("hora_cliente");
            String id_empleado = request.getParameter("id_empleado");
            String diagnostico_tecnico = request.getParameter("diagnostico_tecnico");
            

            String ids_tareas = "";
            try{
                ResultSet rs = objOrdenTrabajo.getContTiposTareas();
                while(rs.next()){
                    String id_tarea_padre = rs.getString("id_tarea_padre")!=null ? rs.getString("id_tarea_padre") : "";
                    int num_tareas = rs.getString(2)!=null ? rs.getInt(2) : 0;
                    for(int i=0; i<num_tareas; i++){
                        String ch = request.getParameter("ch"+id_tarea_padre+i);
                        if(ch!=null){
                            if(ch.toLowerCase().compareTo("true")==0){
                                ids_tareas += request.getParameter("id_tarea"+id_tarea_padre+i) + ",";
                            }
                        }
                    }
                }
                rs.close();
            }catch(Exception ex){
                ex.printStackTrace();
            }
            if(ids_tareas.compareTo("")!=0){
                ids_tareas = ids_tareas.substring(0, ids_tareas.length()-1);
            }
            
            if(id_orden_trabajo.compareTo("-1")==0){
                if(ids_tareas.compareTo("")!=0){
                    String num_orden = objOrdenTrabajo.getNumOrden(id_sucursal_sesion);
                    id_orden_trabajo = objOrdenTrabajo.insertarInfraestructura(id_sucursal_sesion, num_orden, id_nodo, usuario, fecha_cliente, hora_cliente, diagnostico_tecnico, id_empleado, ids_tareas);
                    if(id_orden_trabajo.compareTo("-1")!=0){
                        Auditoria auditoria = new Auditoria(this._ip, this._puerto, this._db, usuario, clave);
                        auditoria.setRegistro(request, "INGRESO DE UNA NUEVA ORDEN DE TRABAJO NRO. "+ id_sucursal_sesion + "-" +num_orden);
                        auditoria.cerrar();

                        r = "err»0^vta»otr^msg»Información ingresada satisfactoriamente.";

                        Mensaje objMensaje = new Mensaje(this._ip, this._puerto, this._db, usuario, clave);
                        objMensaje.setMensaje(usuario, empleado + " ha generado una nueva orden de trabajo No. " + num_orden, "trabajo", id_orden_trabajo, Fecha.getFecha("ISO"), "0.3");
                        objMensaje.cerrar();
                    }
                }else{
                    r = "msg»Debe seleccioar por lo menos una tarea.";
                }
            }else{
                if(objOrdenTrabajo.actualizarInfraestructura(id_orden_trabajo, id_nodo, fecha_cliente, hora_cliente, diagnostico_tecnico, id_empleado, ids_tareas)){
                    String num_orden = request.getParameter("num_orden");
                    Auditoria auditoria = new Auditoria(this._ip, this._puerto, this._db, usuario, clave);
                    auditoria.setRegistro(request, "ACTUALIZACION DE LA ORDEN DE TRABAJO NRO. "+ id_sucursal_sesion + "-" +num_orden);
                    auditoria.cerrar();
                    r = "err»0^vta»otr^msg»Información guardada satisfactoriamente.";
                }
            }
            
            out.print(r);
        } finally {
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