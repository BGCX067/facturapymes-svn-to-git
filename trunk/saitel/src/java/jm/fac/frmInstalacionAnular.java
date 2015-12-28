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
import java.sql.ResultSet;
import jm.fac.clas.Instalacion;
import jm.inf.clas.Radius;
import jm.seg.clas.Auditoria;

/**
 *
 * @author Jorge
 */
public class frmInstalacionAnular extends HttpServlet {
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
        
        Instalacion objInstalacion = new Instalacion(this._ip, this._puerto, this._db, usuario, clave);
        
        try {
            String id = request.getParameter("id");
            String WHERE = request.getParameter("WHERE");
            String p = request.getParameter("p") != null ? request.getParameter("p") : "0";
            String r = "msg»Ha ocurrido un error inesperado, por favor, vuelva a intentarlo más tarde o " +
                "contáctese con el administrador del sistema para mayor información.";

            String id_sucursal = "";
            String num_instalacion = "";
            String radusername = "";
            try{
                ResultSet rsFactura = objInstalacion.getInstalacion(id);
                if(rsFactura.next()){
                    id_sucursal = (rsFactura.getString("id_sucursal")!=null) ? rsFactura.getString("id_sucursal") : "";
                    num_instalacion = (rsFactura.getString("num_instalacion")!=null) ? rsFactura.getString("num_instalacion") : "";
                    radusername = (rsFactura.getString("radusername")!=null) ? rsFactura.getString("radusername") : "";
                    rsFactura.close();
                }
            }catch(Exception e){
                e.printStackTrace();
            }

            if(objInstalacion.anular(id)){
                /*Radius dbRadius = new Radius(this._ip, this._puerto, this._db, usuario, clave);
                dbRadius.eliminar(radusername);
                dbRadius.cerrar();*/
                
                Auditoria auditoria = new Auditoria(this._ip, this._puerto, this._db, usuario, clave);
                auditoria.setRegistro(request, "ANULACION DEL FORMULARIO DE INSTALACION: "+id_sucursal+"-"+num_instalacion + " Y DE TODOS SUS DOCUMENTOS ADJUNTOS");
                auditoria.cerrar();
                r = "err»0^vta»cmp^tbl»"+objInstalacion.paginar("vta_instalacion", "id_instalacion,ruc,razon_social,ip,txt_estado_servicio", WHERE, Integer.parseInt(p), 100)+
                        "^msg»Se ha anulado el documento de instalación y todos sus documentos adjuntos.";
            }

            out.print(r);
        } finally {
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
