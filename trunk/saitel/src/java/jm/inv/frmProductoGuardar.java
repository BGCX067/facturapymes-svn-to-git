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
import jm.inv.clas.Producto;
import jm.seg.clas.Auditoria;

/**
 *
 * @author Jorge
 */
public class frmProductoGuardar extends HttpServlet {
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
            String r = "msg»El nombre de la producto ya existe.";
            String WHERE = request.getParameter("WHERE");
            String id = request.getParameter("id");
            String codigo = request.getParameter("c");
            String codigo_fabricante = request.getParameter("cf");
            String descripcion = request.getParameter("de");
            String tipo = request.getParameter("t");
            String unidad_medida = request.getParameter("um");
            String utilidad_min = request.getParameter("utilidad_min");
            String id_categoria = request.getParameter("idC");
            String stock_min = request.getParameter("stm");
            //String stock = request.getParameter("st");
            String precio_venta_servicio = request.getParameter("pv");
            String id_iva = request.getParameter("id_iva");
            String id_plan_cuenta_compra = request.getParameter("idPCC");
            String id_plan_cuenta_venta = request.getParameter("idPCV");
            String observacion = request.getParameter("o");

            String proveedores = "";
            int i=0;
            boolean ch = false;
            while(request.getParameter("ch"+i)!=null){
                ch = Boolean.valueOf(request.getParameter("ch"+i));
                if(ch){
                    proveedores += request.getParameter("idP"+i) + ",";
                }
                i++;
            }
            if(proveedores.compareTo("")!=0){
                proveedores = proveedores.substring(0, proveedores.length()-1);
            }

            String sucursales = "";
            //String listas_precios = "";
            String descuentos = "";
            String ubicaciones = "";
            String stocks = "";
            int stock = 0;
            i=0;
            ch = false;
            while(request.getParameter("s_ch"+i)!=null){
                ch = Boolean.valueOf(request.getParameter("s_ch"+i));
                if(ch){
                    sucursales += request.getParameter("s_idS"+i) + ",";
                    //listas_precios += request.getParameter("idLP"+i) + ",";
                    descuentos += request.getParameter("des"+i) + ",";
                    ubicaciones += request.getParameter("idUI"+i) + ",";
                    stocks += request.getParameter("skSc"+i) + ",";
                    stock += Integer.valueOf(request.getParameter("skSc"+i));
                }
                i++;
            }
            if(sucursales.compareTo("")!=0){
                sucursales = sucursales.substring(0, sucursales.length()-1);
                //listas_precios = listas_precios.substring(0, listas_precios.length()-1);
                descuentos = descuentos.substring(0, descuentos.length()-1);
                ubicaciones = ubicaciones.substring(0, ubicaciones.length()-1);
                stocks = stocks.substring(0, stocks.length()-1);
            }

            
            if(!objProducto.estaDuplicado(id, codigo)){
                r = "msg»Ha ocurrido un error inesperado, por favor, vuelva a intentarlo más tarde o " +
                    "contáctese con el administrador del sistema para mayor información.";
                if(id.compareTo("-1")==0){
                    if(objProducto.insertar(codigo, codigo_fabricante, descripcion, tipo, utilidad_min, unidad_medida, id_categoria, stock_min, stock, precio_venta_servicio,
                            id_iva, id_plan_cuenta_compra, id_plan_cuenta_venta, observacion, proveedores, sucursales, descuentos, ubicaciones, stocks,
                            false, id_sucursal_sesion)){
                        Auditoria auditoria = new Auditoria(this._ip, this._puerto, this._db, usuario, clave);
                        auditoria.setRegistro(request, "INGRESO DE NUEVO PRODUCTO: "+descripcion);
                        auditoria.cerrar();
                        r = "fun»inv_buscarProducto()^id»-1^msg»Información ingresada satisfactoriamente.";
                    }
                }else{
                    if(objProducto.actualizar(id, codigo, codigo_fabricante, descripcion, tipo, utilidad_min, unidad_medida, id_categoria, stock_min, stock,
                            precio_venta_servicio, id_iva, id_plan_cuenta_compra, id_plan_cuenta_venta, observacion, proveedores, sucursales,
                            descuentos, ubicaciones, stocks)){
                        Auditoria auditoria = new Auditoria(this._ip, this._puerto, this._db, usuario, clave);
                        auditoria.setRegistro(request, "ACTUALIZACION DE LA INFORMACION DEL PRODUCTO: "+descripcion);
                        auditoria.cerrar();
                        r = "fun»inv_buscarProducto()^msg»Información guardada satisfactoriamente.";
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
