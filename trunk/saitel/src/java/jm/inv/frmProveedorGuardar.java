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

package jm.inv;

import java.io.*;

import javax.servlet.*;
import javax.servlet.http.*;
import jm.inv.clas.Proveedor;
import jm.seg.clas.Auditoria;

/**
 *
 * @author Jorge
 */
public class frmProveedorGuardar extends HttpServlet {
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
        int id_sucursal = (Integer)sesion.getAttribute("sucursal");
        String usuario = (String)sesion.getAttribute("usuario");
        String clave = (String)sesion.getAttribute("clave");
        
        response.setContentType("text/html;charset=UTF-8");
        response.setHeader("Pragma", "no-cache");
        response.setHeader("Expires", "Mon, 01 Jan 2001 00:00:01 GMT");
        response.setHeader("Cache-Control", "no-store");
        response.setHeader("Cache-Control", "must-revalidate");
        response.setHeader("Cache-Control", "no-cache");
        PrintWriter out = response.getWriter();
        
        Proveedor objProveedor = new Proveedor(this._ip, this._puerto, this._db, usuario, clave);
        
        try {
            String r = "msg»El número del establecimiento y RUC ya existe.";
            String WHERE = request.getParameter("WHERE");
            String p = request.getParameter("p") != null ? request.getParameter("p") : "0";
            String id = request.getParameter("id");
            String tipo_documento = request.getParameter("tipo_documento");
            String ruc = request.getParameter("ru").toUpperCase();
            String razon_social = request.getParameter("rs").toUpperCase();
            String id_plan_cuenta = request.getParameter("idPC");

            String aut_factura = request.getParameter("auF");
            String serie_factura = request.getParameter("seF");
            serie_factura = serie_factura.compareTo("")!=0 ? serie_factura : "001";
            String fecha_cad_factura = request.getParameter("caF");

            String direccion = request.getParameter("di").toUpperCase();
            String ciudad = request.getParameter("ci").toUpperCase();
            String pais = request.getParameter("pa").toUpperCase();
            String telefono = request.getParameter("te");
            String fax = request.getParameter("fa");
            String correo = request.getParameter("ma");
            String web = request.getParameter("we");
            String contacto = request.getParameter("co");
            String observacion = request.getParameter("ob");

            if(!objProveedor.estaDuplicado(id, serie_factura, ruc)){
                r = "msg»Ha ocurrido un error inesperado, por favor, vuelva a intentarlo más tarde o " +
                    "contáctese con el administrador del sistema para mayor información.";
                if(id.compareTo("-1")==0){
                    if(objProveedor.insertar(id_sucursal, tipo_documento, ruc, razon_social, id_plan_cuenta, aut_factura, serie_factura, fecha_cad_factura,
                            direccion, ciudad, pais, telefono, fax, correo, web, contacto, observacion)){
                        Auditoria auditoria = new Auditoria(this._ip, this._puerto, this._db, usuario, clave);
                        auditoria.setRegistro(request, "INGRESO DEL NUEVO PROVEEDOR: "+razon_social);
                        auditoria.cerrar();
                        r = "tbl»"+objProveedor.paginar("vta_proveedor", "id_proveedor,ruc,razon_social", WHERE, Integer.parseInt(p), 100)+"^id»-1^msg»Información ingresada satisfactoriamente.";
                    }
                }else{
                    if(objProveedor.actualizar(id, tipo_documento, ruc, razon_social, id_plan_cuenta, aut_factura, serie_factura, fecha_cad_factura,
                            direccion, ciudad, pais, telefono, fax, correo, web, contacto, observacion)){
                        Auditoria auditoria = new Auditoria(this._ip, this._puerto, this._db, usuario, clave);
                        auditoria.setRegistro(request, "ACTUALIZACION DE LA INFORMACION DEL PROVEEDOR: "+razon_social);
                        auditoria.cerrar();
                        r = "tbl»"+objProveedor.paginar("vta_proveedor", "id_proveedor,ruc,razon_social", WHERE, Integer.parseInt(p), 100)+"^msg»Información guardada satisfactoriamente.";
                    }
                }
            }

            out.print(r);
        } finally {
            objProveedor.cerrar();
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