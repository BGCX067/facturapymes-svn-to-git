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

package jm.cnt.pdf;

import java.io.*;

import javax.servlet.*;
import javax.servlet.http.*;

import java.sql.ResultSet;
import java.sql.SQLException;
import jm.cnt.clas.PlanCuenta;
import jm.web.Archivo;
import jm.adm.clas.Configuracion;
import jm.Reporte;
/**
 *
 * @author Jorge
 */
public class pdfPlanCuentas extends HttpServlet {
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
        String usuario = (String)sesion.getAttribute("usuario");
        String clave = (String)sesion.getAttribute("clave");
        
        response.setContentType("application/pdf");
        response.setHeader("Pragma", "no-cache");
        response.setHeader("Expires", "Mon, 01 Jan 2001 00:00:01 GMT");
        response.setHeader("Cache-Control", "no-store");
        response.setHeader("Cache-Control", "must-revalidate");
        response.setHeader("Cache-Control", "no-cache");
        //PrintWriter out = response.getWriter();
        Configuracion conf = new Configuracion(this._ip, this._puerto, this._db, usuario, clave);

        PlanCuenta objPlanCuenta = new PlanCuenta(this._ip, this._puerto, this._db, usuario, clave);
        String path = String.valueOf(request.getRequestURL());
        path = path.substring(0, path.lastIndexOf("/"));
        Archivo archivo = new Archivo(this._ip, this._puerto, this._db, usuario, clave);
        String logo = path + "/img/" + archivo.getArchivo(this._dir, 1);

        String titulo = conf.getValor("razon_social");
        String ruc = conf.getValor("ruc");
        String subtitulo = "";
        String direccion = conf.getValor("dir_matriz");
        String rep_pie = conf.getValor("rep_pie");
        String [] cabTabla = null;
        float [] anchoTabla = null;
        ResultSet registros = null;
        try {
            registros = objPlanCuenta.consulta("select codigo_cuenta, nombre_cuenta, txt_tipo_cuenta from vta_plan_cuenta where id_plan_cuenta>1;");
            subtitulo = "Plan de Cuentas";
            Reporte reporte = new Reporte(true, logo, titulo, ruc, subtitulo, direccion, "", rep_pie);
            cabTabla = new String [] {"Nro.", "CÓDIGO", "CUENTA", "TIPO"};
            anchoTabla = new float[]{15f,55f,165f,30f};
            reporte.lista(response, cabTabla, anchoTabla, registros);
        } finally {
            archivo.cerrar();
            conf.cerrar();
            try{
                registros.close();
            }catch(SQLException e){
                e.printStackTrace();
            }
            objPlanCuenta.cerrar();
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
