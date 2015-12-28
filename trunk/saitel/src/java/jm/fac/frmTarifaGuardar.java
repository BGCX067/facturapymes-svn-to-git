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
import jm.fac.clas.PlanServicio;
import jm.seg.clas.Auditoria;

/**
 *
 * @author Jorge
 */
public class frmTarifaGuardar extends HttpServlet {
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
        
        response.setContentType("text/html;charset=UTF-8");
        response.setHeader("Pragma", "no-cache");
        response.setHeader("Expires", "Mon, 01 Jan 2001 00:00:01 GMT");
        response.setHeader("Cache-Control", "no-store");
        response.setHeader("Cache-Control", "must-revalidate");
        response.setHeader("Cache-Control", "no-cache");
        PrintWriter out = response.getWriter();
        
        PlanServicio objPlanServicio = new PlanServicio(this._ip, this._puerto, this._db, usuario, clave);
        
        try {
            String r = "msg»Ha ocurrido un error inesperado, por favor, vuelva a intentarlo más tarde o " +
                        "contáctese con el administrador del sistema para mayor información.";
            String id_plan_servicio = request.getParameter("id");
            String id_tarifa = request.getParameter("idT");
            String vigente_desde = request.getParameter("fi");
            String vigente_hasta = request.getParameter("ff");
            String costo_plan = request.getParameter("t");
            String obj = request.getParameter("obj");

            r = "msg»Existe un conflicto entre el rango de fechas de vigencia.";
            if(!objPlanServicio.tarifaDuplicada(id_plan_servicio, id_tarifa, vigente_desde, vigente_hasta)){
                r = "msg»Ha ocurrido un error inesperado, por favor, vuelva a intentarlo más tarde o " +
                        "contáctese con el administrador del sistema para mayor información.";
                if(id_tarifa.compareTo("-1")==0){
                    String pk = objPlanServicio.insertarTarifa(id_plan_servicio, vigente_desde, vigente_hasta, costo_plan);
                    if(pk.compareTo("-1")!=0){
                        Auditoria auditoria = new Auditoria(this._ip, this._puerto, this._db, usuario, clave);
                        auditoria.setRegistro(request, "INGRESO DE TARIFA DE CODIGO: "+id_tarifa+" EN LA TABLA DE TARIFAS");
                        auditoria.cerrar();
                        r = "fun»_('"+obj+"').value="+pk;
                    }
                }else{
                    if(objPlanServicio.actualizarTarifa(id_tarifa, vigente_desde, vigente_hasta, costo_plan)){
                        Auditoria auditoria = new Auditoria(this._ip, this._puerto, this._db, usuario, clave);
                        auditoria.setRegistro(request, "ACTUALIZACION DE TARIFA DE CODIGO "+id_tarifa+" EN LA TABLA DE TARIFAS");
                        auditoria.cerrar();
                        r = "";
                    }
                }
            }
            
            out.print(r);
        } finally {
            objPlanServicio.cerrar();
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