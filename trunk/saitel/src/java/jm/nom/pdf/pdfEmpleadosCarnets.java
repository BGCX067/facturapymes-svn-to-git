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

package jm.nom.pdf;

import java.io.*;

import javax.servlet.*;
import javax.servlet.http.*;

import com.lowagie.text.pdf.*;
import com.lowagie.text.*;

import jm.web.Addons;
import jm.nom.clas.Empleado;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import jm.Reporte;
import jm.web.Archivo;
import jm.web.Fecha;
import jm.nom.clas.Carnet;

/**
 *
 * @author Jorge
 */
public class pdfEmpleadosCarnets extends HttpServlet {
    private String _ip = null;
    private int _puerto = 5432;
    private String _db = null;
    private String _usuario = null;
    private String _clave = null;
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
        //int id_punto_emision = (Integer)sesion.getAttribute("id_punto_emision");
        this._usuario = (String)sesion.getAttribute("usuario");
        this._clave = (String)sesion.getAttribute("clave");

        //PrintWriter out = response.getWriter();

        String ids = request.getParameter("id");
        String path = String.valueOf(request.getRequestURL());
        path = path.substring(0, path.lastIndexOf("/"));
        
        
        ResultSet rsCarnets = null;
            Empleado objEmpleado = new Empleado(_ip, _puerto, _db, _usuario, _clave);
            //FacturaVenta objFacturaVenta = new FacturaVenta(_ip, _puerto, _db, _usuario, _clave);
            rsCarnets = objEmpleado.getCarnets(ids);
        /*Factura objFactura = new Factura();
        objFactura.imprimir(id_punto_emision, response, id);*/
        
        Archivo archivo = new Archivo(this._ip, this._puerto, this._db, this._usuario, this._clave);
        path = path.substring(0, path.lastIndexOf("/"));
        try{
            while(rsCarnets.next()){
                String id= rsCarnets.getString("id_empleado")!=null ? rsCarnets.getString("id_empleado") : "";
                String foto = path + "/img/" + archivo.getArchivo(this._dir, "tbl_empleado", id, "padre_nombre", "foto");
                
            }
            rsCarnets.beforeFirst();
        }catch(Exception e){
            e.printStackTrace();
        }
        archivo.cerrar();
            
        
        
        Carnet objCarnet = new Carnet(_dir);
        objCarnet.imprimir(response, rsCarnets, path);
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
