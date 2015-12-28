/**
** @version 1.0
** @package FACTURAPYMES.
* @author Jorge Washington Mueses Cevallos.
* @copyright Copyright (C) 2011 por Jorge Mueses. Todos los derechos reservados.
* @license http://www.gnu.org/copyleft/gpl.html GNU/GPL.
** FACTURAPYMES es un software de libre distribuciÃ³n, que puede ser
* copiado y distribuido bajo los tÃ©rminos de la Licencia
* Attribution-NonCommercial-NoDerivs 3.0 Unported,
* de acuerdo con la publicada por la CREATIVE COMMONS CORPORATION.
*/

package jm.inv.xls;

import java.io.*;

import javax.servlet.*;
import javax.servlet.http.*;
import java.sql.ResultSet;

import jm.web.DataBase;
import jm.ReporteXls;
import jm.inv.clas.Proveedor;
import jm.web.Addons;

/**
 *
 * @author Jorge
 */
public class xlsLiquidaciones extends HttpServlet {
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
        
        if(request.getHeader("User-Agent").toLowerCase().contains("windows")){
            response.setContentType("application/vnd.ms-excel;");
            response.setHeader("Content-disposition", "inline; filename=reporte.xls;");
        }else{
            response.setContentType("text/xml;");
            response.setHeader("Content-disposition", "attachment; filename=reporte.ods;");
        }
        response.setHeader("Pragma", "no-cache");
        response.setHeader("Expires", "Mon, 01 Jan 2001 00:00:01 GMT");
        response.setHeader("Cache-Control", "no-store");
        response.setHeader("Cache-Control", "must-revalidate");
        response.setHeader("Cache-Control", "no-cache");
        PrintWriter out = response.getWriter();

        String fecha_ini = request.getParameter("fini");
        String fecha_fin = request.getParameter("ffin");
        String id_proveedor = request.getParameter("id_prov");
        String WHERE = "";

        DataBase objDB = new DataBase(this._ip, this._puerto, this._db, usuario, clave);

        String subtitulo = "Reporte de liquidaciones de compras y servicios. \n Del "+Addons.fechaAl(fecha_ini)+"  al  "+Addons.fechaAl(fecha_fin);
        if(id_proveedor.compareTo("-0")!=0 && id_proveedor.compareTo("")!=0){
            Proveedor objProveedor = new Proveedor(this._ip, this._puerto, this._db, usuario, clave);
            subtitulo += ", del proveedor " + objProveedor.getNombre(id_proveedor);
            WHERE = " and id_proveedor="+id_proveedor;
            objProveedor.cerrar();
        }

        ResultSet registros = objDB.consulta("select id_liquidacion_compra,ruc,razon_social,numero_liquidacion,autorizacion,toDateSQL(fecha_emision),subtotal,subtotal_0,iva_2,total_compra "
                + "from vta_liquidacion_compra as F "
                + "where F.anulado=false and fecha_emision between '"+fecha_ini+"' and '"+fecha_fin+"' "+WHERE+" order by fecha_emision,F.id_liquidacion_compra");
        String cabTabla[] = new String [] {"Nro.", "RUC", "RAZON SOCIAL", "No. FACTURA", "AUT.", "FECHA EMISION", "SUBT.", "SUBT. 0%", "IVA 12%", "RET.", "TOTAL"};
        ReporteXls reporte = new ReporteXls();

        String xls = reporte.liquidaciones(subtitulo, cabTabla, registros, objDB);

        out.print(xls);

        try{
            registros.close();
        }catch(Exception e){
            e.printStackTrace();
        }

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
