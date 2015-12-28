/**
** @version 1.0
** @package FACTURAPYMES.
* @author Jorge Washington Mueses Cevallos.
* @copyright Copyright (C) 2011 por Jorge Mueses. Todos los derechos reservados.
* @license http://www.gnu.org/copyleft/gpl.html GNU/GPL.
** FACTURAPYMES es un software de libre distribución, que puede ser
* copiado y distribuido bajo los términos de la Licencia
* Attribution-NonCommercial-NoDerivs 3.0 Unported,
* de acuerdo con la publicada por la CREATIVE COMMONS CORPORATION.
*/

package jm.nom.pdf;

import java.io.*;

import javax.servlet.*;
import javax.servlet.http.*;
import java.sql.ResultSet;

import jm.web.Archivo;
import jm.web.DataBase;
import jm.Reporte;
import jm.adm.clas.Configuracion;
import jm.adm.clas.Sucursal;

/**
 *
 * @author Jorge
 */
public class pdfCargos extends HttpServlet {
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

        String WHERE = request.getParameter("WHERE");
        //String id_sucursal = request.getParameter("id_Suc");

        Configuracion conf = new Configuracion(this._ip, this._puerto, this._db, usuario, clave);
        String path = String.valueOf(request.getRequestURL());
        path = path.substring(0, path.lastIndexOf("/"));
        Archivo archivo = new Archivo(this._ip, this._puerto, this._db, usuario, clave);
        String logo = path + "/img/" + archivo.getArchivo(this._dir, 1);

        String titulo = conf.getValor("razon_social");
        String ruc = conf.getValor("ruc");
        String direccion = conf.getValor("dir_matriz");
        String rep_pie = conf.getValor("rep_pie");

        Sucursal objSucursal = new Sucursal(this._ip, this._puerto, this._db, usuario, clave);
        String sucursal = objSucursal.getNombre("-0");
        objSucursal.cerrar();

        DataBase objDB = new DataBase(this._ip, this._puerto, this._db, usuario, clave);

        ResultSet registros = objDB.consulta("select area,cargo,sueldo from vta_cargo "+WHERE+" order by area,cargo");
        String subtitulo = "Listado de cargos por áreas (departamentos).";
        Reporte reporte = new Reporte(true, logo, titulo, ruc, subtitulo, direccion, sucursal,  rep_pie);
        String [] cabTabla = new String [] {"Nro.", "AREA", "CARGO", "SUELDO"};
        float [] anchoTabla = new float[]{15f,45f,45f,20f};
        reporte.lista(response, cabTabla, anchoTabla, registros);

        try{
            registros.close();
        }catch(Exception e){
            e.printStackTrace();
        }

        conf.cerrar();
        objDB.cerrar();
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
