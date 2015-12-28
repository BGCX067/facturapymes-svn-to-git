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
import java.sql.ResultSet;

import javax.servlet.*;
import javax.servlet.http.*;
import jm.fac.clas.Instalacion;
import jm.fac.clas.PreFactura;
import jm.seg.clas.Auditoria;

/**
 *
 * @author Jorge
 */
public class frmAnticipoInternetNuevoGuardar extends HttpServlet {
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

        String r = "msg»Ha ocurrido un error inesperado, por favor, vuelva a intentarlo más tarde o " +
                "contáctese con el administrador del sistema para mayor información.";
        String id_instalacion = request.getParameter("id_instalacion");
        String fecha_ini = request.getParameter("fecha_ini");
        String fecha_fin = request.getParameter("fecha_fin");

        String ruc="";
        String ip="";
        Instalacion objInstalacion = new Instalacion(this._ip, this._puerto, this._db, usuario, clave);
        try{
            ResultSet rsInstalacion = objInstalacion.getInstalacion(id_instalacion);
            if(rsInstalacion.next()){
                ruc = (rsInstalacion.getString("ruc")!=null) ? rsInstalacion.getString("ruc") : "";
                ip = (rsInstalacion.getString("ip")!=null) ? rsInstalacion.getString("ip") : "";
                rsInstalacion.close();
            }
        }catch(Exception e){
            e.printStackTrace();
        }finally{
            objInstalacion.cerrar();
        }

        PreFactura objPreFactura = new PreFactura(this._ip, this._puerto, this._db, usuario, clave);
        try {
            if(objPreFactura.ingresarAnticipo(id_instalacion, fecha_ini, fecha_fin, null)){
                Auditoria auditoria = new Auditoria(this._ip, this._puerto, this._db, usuario, clave);
                auditoria.setRegistro(request, "REGISTRO DE ANTICIPO DEL PAGO DEL SERVICIO DE INTERNET PARA EL CLIENTE CON RUC "+ruc+" CON IP "+ip);
                auditoria.cerrar();
                r = "err»0^vta»cmp^msg»Anticipo registrado satisfactoriamente";
            }

            out.print(r);

        } finally {
            objPreFactura.cerrar();
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
