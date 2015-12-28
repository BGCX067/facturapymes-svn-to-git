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
import jm.adm.clas.Mensaje;
import jm.inf.clas.Soporte;
import jm.seg.clas.Auditoria;
import jm.web.Fecha;

/**
 *
 * @author Jorge
 */
public class frmSoporteGuardar extends HttpServlet {
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
        
        Soporte objSoporte = new Soporte(this._ip, this._puerto, this._db, usuario, clave);
        
        try {
            String r = "msg»Ha ocurrido un error inesperado, por favor, vuelva a intentarlo más tarde o " +
                    "contáctese con el administrador del sistema para mayor información.";
            String id_soporte = request.getParameter("id_soporte");
            String num_soporte = request.getParameter("num_soporte");
            String WHERE = request.getParameter("WHERE");
            String p = request.getParameter("p") != null ? request.getParameter("p") : "0";
            String imprimir = request.getParameter("imp");
            String id_instalacion = request.getParameter("idIn");
            String quien_llama = request.getParameter("quien_llama");
            String telefono_llama = request.getParameter("telefono_llama");
            String problema = request.getParameter("problema");
            String diagnostico = request.getParameter("diagnostico");

            if(id_soporte.compareTo("-1")==0){
                num_soporte = objSoporte.getNumSoporte(id_sucursal_sesion);
                String pk = objSoporte.insertar(id_instalacion, id_sucursal_sesion, num_soporte, quien_llama, telefono_llama, usuario, problema, diagnostico);
                if(pk.compareTo("-1")!=0){
                    Auditoria auditoria = new Auditoria(this._ip, this._puerto, this._db, usuario, clave);
                    auditoria.setRegistro(request, "INGRESO DEL NUEVO SOPORTE DE SERVICIO NRO. "+ id_sucursal_sesion + "-" +num_soporte);
                    auditoria.cerrar();
                    r = "err»0^vta»cmp"+(imprimir.compareTo("1")==0 ? "^fun»imprimir('pdfSoporte?id="+pk+"');" : "")+
                        "^tbl»"+objSoporte.paginar("vta_soporte", "id_soporte,txt_fecha_llamada,txt_fecha_solucion,txt_estado", WHERE, Integer.parseInt(p), 100)+
                        "^msg»Información ingresada satisfactoriamente.";

                    Mensaje objMensaje = new Mensaje(this._ip, this._puerto, this._db, usuario, clave);
                    objMensaje.setMensaje(usuario, empleado + " ha registrado el soporte No. " + id_sucursal_sesion + "-" +num_soporte, "soporte", id_soporte, Fecha.getFecha("ISO"), "0.3");
                    objMensaje.cerrar();
                }
            }else{
                if(objSoporte.actualizar(id_soporte, problema, diagnostico)){
                    Auditoria auditoria = new Auditoria(this._ip, this._puerto, this._db, usuario, clave);
                    auditoria.setRegistro(request, "ACTUALIZACION DEL SOPORTE DE SERVICIO NRO. "+ id_sucursal_sesion + "-" +num_soporte);
                    auditoria.cerrar();
                    r = "err»0^vta»cmp^msg»Información actualizada satisfactoriamente.";
                }
            }

            out.print(r);
        } finally {
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