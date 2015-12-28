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
import jm.adm.clas.Configuracion;
import jm.inv.clas.Producto;
import jm.seg.clas.Auditoria;

/**
 *
 * @author Jorge
 */
public class frmProductoFactGuardar extends HttpServlet {
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
        int id_sucursal_sesion = (Integer)sesion.getAttribute("sucursal");
        String usuario = (String)sesion.getAttribute("usuario");
        String clave = (String)sesion.getAttribute("clave");

        response.setContentType("text/html;charset=UTF-8");
        response.setHeader("Pragma", "no-cache");
        response.setHeader("Expires", "Mon, 01 Jan 2001 00:00:01 GMT");
        response.setHeader("Cache-Control", "no-store");
        response.setHeader("Cache-Control", "must-revalidate");
        response.setHeader("Cache-Control", "no-cache");
        PrintWriter out = response.getWriter();
        
        Producto objProducto = new Producto(this._ip, this._puerto, this._db, usuario, clave);
        
        try {
            String r = "msg»El código de la producto ya existe.";
            String id = request.getParameter("id");
            String codigo = request.getParameter("c");
            String descripcion = request.getParameter("de");
            String tipo = request.getParameter("t");
            String utilidad_min = request.getParameter("utilidad_min");
            String unidad_medida = request.getParameter("um");
            String id_categoria = request.getParameter("idC");
            String stock_min = request.getParameter("stm");
            String id_iva = request.getParameter("id_iva");
            String id_plan_cuenta_compra = request.getParameter("idPCC");
            String id_plan_cuenta_venta = request.getParameter("idPCV");
            String proveedores = request.getParameter("idPr");

            Configuracion conf = new Configuracion(this._ip, this._puerto, this._db, usuario, clave);
            String id_sucursal_matriz = conf.getValor("id_sucursal_matriz");
            conf.cerrar();

            //String listas_precios = request.getParameter("idLP");
            String descuentos = request.getParameter("desc");
            String ubicaciones = request.getParameter("idUI");
            String stocks = "0";
            int stock = 0;
            boolean aMatriz = id_sucursal_sesion==Integer.parseInt(id_sucursal_matriz) ? false : true;

            if(!objProducto.estaDuplicado(id, codigo)){
                r = "msg»Ha ocurrido un error inesperado, por favor, vuelva a intentarlo más tarde o " +
                    "contáctese con el administrador del sistema para mayor información.";
                if(id.compareTo("-1")==0){
                    if(objProducto.insertar(codigo, "", descripcion, tipo, utilidad_min, unidad_medida, id_categoria, stock_min,stock, "0", id_iva,
                            id_plan_cuenta_compra, id_plan_cuenta_venta, "", proveedores, id_sucursal_matriz,  
                            descuentos, ubicaciones, stocks, aMatriz, id_sucursal_sesion)){
                        Auditoria auditoria = new Auditoria(this._ip, this._puerto, this._db, usuario, clave);
                        auditoria.setRegistro(request, "INGRESO DE NUEVO PRODUCTO: "+descripcion);
                        auditoria.cerrar();
                        r = "err»0^vta»vtPrd^fun»inv_getProductosProv('l');_('fc').focus();";
                    }
                }
            }

            out.print(r);
        } finally {
            objProducto.cerrar();
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
