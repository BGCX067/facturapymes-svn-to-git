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
import jm.adm.clas.Mensaje;
import jm.fac.clas.Instalacion;
import jm.inf.clas.OrdenTrabajo;
import jm.seg.clas.Auditoria;
import jm.web.Fecha;

/**
 *
 * @author Jorge
 */
public class frmDesInstalacionGuardar extends HttpServlet {
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
        String usuario = (String)sesion.getAttribute("usuario");
        String clave = (String)sesion.getAttribute("clave");
        int id_sucursal_sesion = (Integer)sesion.getAttribute("sucursal");
        String empleado = (String)sesion.getAttribute("empleado");

        response.setContentType("text/html;charset=UTF-8");
        response.setHeader("Pragma", "no-cache");
        response.setHeader("Expires", "Mon, 01 Jan 2001 00:00:01 GMT");
        response.setHeader("Cache-Control", "no-store");
        response.setHeader("Cache-Control", "must-revalidate");
        response.setHeader("Cache-Control", "no-cache");
        PrintWriter out = response.getWriter();

        Instalacion objInstalacion = new Instalacion(this._ip, this._puerto, this._db, usuario, clave);
        OrdenTrabajo objOrdenTrabajo = new OrdenTrabajo(this._ip, this._puerto, this._db, usuario, clave);
        
        try {
            String r = "msg»Ha ocurrido un error inesperado, por favor, vuelva a intentarlo más tarde o " +
                "contáctese con el administrador del sistema para mayor información.";

            String id = request.getParameter("id");
            String id_cliente = request.getParameter("id_cliente");

            String WHERE = request.getParameter("WHERE");
            String p = request.getParameter("p") != null ? request.getParameter("p") : "0";
            String id_sucursal = request.getParameter("id_sucursal");
            String num_desinstalacion = objInstalacion.getNumDesInstalacion(id_sucursal);
            String fecha_desinstalacion = request.getParameter("fecha_desinstalacion");
            String motivo_desinstalacion = request.getParameter("motivo_desinstalacion");
            String generar_orden_trab = request.getParameter("generar_orden_trab");
            

            if(id.compareTo("-1")!=0){
                if(objInstalacion.porRetirar(id, generar_orden_trab, id_cliente, num_desinstalacion, fecha_desinstalacion, motivo_desinstalacion)){
                    Auditoria auditoria = new Auditoria(this._ip, this._puerto, this._db, usuario, clave);
                    auditoria.setRegistro(request, "REGISTRO DE FORMULARIO DE DESINSTALACION NRO.: "+id_sucursal+"-"+num_desinstalacion);
                    auditoria.cerrar();

                    if(generar_orden_trab.toLowerCase().compareTo("true")==0){
                        
                        String num_orden = objOrdenTrabajo.getNumOrden(id_sucursal_sesion);
                        String id_orden_trabajo = objOrdenTrabajo.insertar(id, id_sucursal_sesion, num_orden, "4", usuario, "", "", motivo_desinstalacion);
                        if(id_orden_trabajo.compareTo("-1")!=0){
                            auditoria.setRegistro(request, "INGRESO DE UNA NUEVA ORDEN DE TRABAJO NRO. "+ id_sucursal_sesion + "-" +num_orden);

                            Mensaje objMensaje = new Mensaje(this._ip, this._puerto, this._db, usuario, clave);
                            objMensaje.setMensaje(usuario, empleado + " ha generado una nueva orden de trabajo No. " + num_orden, "trabajo", id_orden_trabajo, Fecha.getFecha("ISO"), "0.3");
                            objMensaje.cerrar();
                        }
                        
                    }
                    /*r = "err»0^vta»cmp^fun»imprimir('pdfDesInstalacion?id="+id+"');"+
                            "^tbl»"+objInstalacion.paginar("vta_instalacion", "id_instalacion,ruc,razon_social,ip,txt_estado_servicio", WHERE, Integer.parseInt(p), 100);*/
                    r = "err»0^vta»cmp^tbl»"+objInstalacion.paginar("vta_instalacion", "id_instalacion,ruc,razon_social,ip,txt_estado_servicio", WHERE, Integer.parseInt(p), 100);
                }
            }

            out.print(r);

        } finally {
            objOrdenTrabajo.cerrar();
            objInstalacion.cerrar();
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
