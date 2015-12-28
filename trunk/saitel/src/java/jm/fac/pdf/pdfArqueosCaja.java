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

package jm.fac.pdf;

import java.io.*;

import javax.servlet.*;
import javax.servlet.http.*;
import java.sql.ResultSet;
import java.sql.SQLException;
import jm.Reporte;
import jm.adm.clas.Configuracion;
import jm.adm.clas.Sucursal;
import jm.web.Archivo;
import jm.web.DataBase;


/**
 *
 * @author Jorge
 */
public class pdfArqueosCaja extends HttpServlet {
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
        int id_sucursal_sesion = (Integer)sesion.getAttribute("sucursal");
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

        DataBase objDB = new DataBase(this._ip, this._puerto, this._db, usuario, clave);

        String path = String.valueOf(request.getRequestURL());
        path = path.substring(0, path.lastIndexOf("/"));
        Archivo archivo = new Archivo(this._ip, this._puerto, this._db, usuario, clave);
        String logo = path + "/img/" + archivo.getArchivo(this._dir, 1);

        //String id_sucursal = request.getParameter("idSuc");
        Sucursal objSucursal = new Sucursal(this._ip, this._puerto, this._db, usuario, clave);
        String nombre_sucursal = objSucursal.getNombre(String.valueOf(id_sucursal_sesion));

        String titulo = conf.getValor("razon_social");
        String ruc = conf.getValor("ruc");
        String subtitulo = "";
        String direccion = conf.getValor("dir_matriz");
        String rep_pie = conf.getValor("rep_pie");

        String [] cabTabla = null;
        float [] anchoTabla = null;
        ResultSet registros = null;
        Reporte reporte = null;
        try {
            String WHERE = request.getParameter("WHERE")!=null ? " "+request.getParameter("WHERE") : "";
            //String fecha_ini = request.getParameter("fi");
            //String fecha_fin = request.getParameter("ff");

            registros = objDB.consulta("select cajero,num_documento,toDateSql(fecha),hora,total from tbl_arqueo_caja "+WHERE);
            subtitulo = "Arqueos de caja.";
            reporte = new Reporte(true, logo, titulo, ruc, subtitulo, direccion, nombre_sucursal, rep_pie);
            cabTabla = new String [] {"Nro.", "RESPONSABLE", "Nro. DOC.", "FECHA", "HORA", "IMPORTE"};
            anchoTabla = new float[]{25f,100f,50f,80f,60f,70f};
            reporte.sumLista(response, cabTabla, anchoTabla, registros);

        } finally {
            try{
                registros.close();
            }catch(SQLException e){
                e.printStackTrace();
            }
            archivo.cerrar();
            conf.cerrar();
            objSucursal.cerrar();
            objDB.cerrar();
            //out.close();
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
