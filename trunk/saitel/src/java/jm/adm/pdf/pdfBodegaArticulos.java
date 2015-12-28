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

package jm.adm.pdf;

import java.io.*;

import javax.servlet.*;
import javax.servlet.http.*;
import jm.adm.clas.Configuracion;
import java.sql.ResultSet;
import jm.Reporte;
import jm.adm.clas.Bodega;
import jm.adm.clas.Sucursal;
import jm.web.Archivo;

/**
 *
 * @author Jorge
 */
public class pdfBodegaArticulos extends HttpServlet {
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
        
        //PrintWriter out = response.getWriter();
        
        String id = request.getParameter("id");
        
        Configuracion conf = new Configuracion(this._ip, this._puerto, this._db, usuario, clave);
        String titulo = conf.getValor("razon_social");
        String ruc = conf.getValor("ruc");
        String direccion = conf.getValor("dir_matriz");
        String rep_pie = conf.getValor("rep_pie");
        conf.cerrar();

        String path = String.valueOf(request.getRequestURL());
        path = path.substring(0, path.lastIndexOf("/"));
        Archivo archivo = new Archivo(this._ip, this._puerto, this._db, usuario, clave);
        String logo = path + "/img/" + archivo.getArchivo(this._dir, 1);
        archivo.cerrar();

        Bodega objBodega = new Bodega(this._ip, this._puerto, this._db, usuario, clave);
        String id_sucursal = "";
        String bodega = "";
        String ubicacion = "";
        String coresponsable = "";
        try{
            ResultSet rs = objBodega.getBodega(id);
            if(rs.next()){
                id_sucursal = (rs.getString("id_sucursal")!=null) ? rs.getString("id_sucursal") : "";
                bodega = (rs.getString("bodega")!=null) ? rs.getString("bodega") : "";
                ubicacion = (rs.getString("ubicacion")!=null) ? rs.getString("ubicacion") : "";
                coresponsable = (rs.getString("coresponsable")!=null) ? rs.getString("coresponsable") : "";
                rs.close();
            }
        }catch(Exception e){
            e.printStackTrace();
        }

        String responsable = objBodega.getResponsable(id);

        Sucursal objSucursal = new Sucursal(this._ip, this._puerto, this._db, usuario, clave);
        String nombre_sucursal = objSucursal.getNombre(id_sucursal);
        objSucursal.cerrar();

        String informacion = nombre_sucursal + ".\nUBICACION: " + ubicacion + ".\n";
        informacion += "RESPONSABLE: " + responsable + ".\n";
        informacion += "CORESPONSABLES: " + coresponsable + ".";

        ResultSet registros = objBodega.getArticulos(id);
        String subtitulo = "Artículos que se encuentran en la bodega " + bodega;
        Reporte reporte = new Reporte(true, logo, titulo, ruc, subtitulo, direccion, informacion, rep_pie);
        String cabTabla[] = new String [] {"Nro.", "UBICACION", "CODIGO", "ARTICULO"};
        float anchoTabla[] = new float[]{25f,80f,80f,160f};
        reporte.lista(response, cabTabla, anchoTabla, registros);
        
        objBodega.cerrar();
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
