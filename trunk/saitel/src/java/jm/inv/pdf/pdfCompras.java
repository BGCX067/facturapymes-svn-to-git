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

package jm.inv.pdf;

import java.io.*;

import javax.servlet.*;
import javax.servlet.http.*;

import jm.adm.clas.Configuracion;
import jm.web.Addons;

import java.sql.ResultSet;
import jm.Reporte;
import jm.inv.clas.Proveedor;
import jm.web.Archivo;
import jm.web.DataBase;

/**
 *
 * @author Jorge
 */
public class pdfCompras extends HttpServlet {
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

        //String id = request.getParameter("id");
        String fecha_ini = request.getParameter("fini");
        String fecha_fin = request.getParameter("ffin");
        String id_proveedor = request.getParameter("id_prov");
        String WHERE = "";

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

        DataBase objDB = new DataBase(this._ip, this._puerto, this._db, usuario, clave);

        String subtitulo = "Reporte de facturas de compras. \n Del "+Addons.fechaAl(fecha_ini)+"  al  "+Addons.fechaAl(fecha_fin);
        if(id_proveedor.compareTo("-0")!=0 && id_proveedor.compareTo("")!=0){
            Proveedor objProveedor = new Proveedor(this._ip, this._puerto, this._db, usuario, clave);
            subtitulo += ", del proveedor " + objProveedor.getNombre(id_proveedor);
            WHERE = " and id_proveedor="+id_proveedor;
            objProveedor.cerrar();
        }

        ResultSet registros = objDB.consulta("select id_factura_compra,ruc,razon_social,numero_factura,autorizacion,"
                + "toDateSQL(fecha_compra),subtotal,descuento,subtotal_0,iva_2,total_compra "
                + "from vta_factura_compra as F "
                + "where F.anulado=false and F.contabilizado=true and fecha_compra between '"+fecha_ini+"' and '"+fecha_fin+"' "+WHERE+" order by fecha,F.id_factura_compra");
        String cabTabla[] = new String [] {"Nro.", "RUC", "RAZON SOCIAL", "No. FACTURA", "AUT.", "FECHA EMISION", "SUBT.", "DESC.", "SUBT. 0%", "IVA 12%", "RET.", "TOTAL"};
        float anchoTabla[] = new float[]{25f,70f,160f,80f,60f,50f,40f,30f,30f,30f,30f,40f};
        
        Reporte reporte = new Reporte(false, logo, titulo, ruc, subtitulo, direccion, "", rep_pie);
        reporte.compras(response, cabTabla, anchoTabla, registros, objDB);
        
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
