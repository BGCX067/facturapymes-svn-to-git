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
import jm.inv.clas.NotaVenta;
import jm.inv.clas.Proveedor;
import jm.seg.clas.Auditoria;

/**
 *
 * @author Jorge
 */
public class frmNotaVentaGuardar extends HttpServlet {
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
        
        Configuracion conf = new Configuracion(this._ip, this._puerto, this._db, usuario, clave);
        String bloqueo_libros = "";
        while(true){
            bloqueo_libros = conf.getValor("bloqueo_libros");
            if(bloqueo_libros.compareTo("false")==0) {
                conf.setValor("bloqueo_libros", "true");
                break;
            }
            try{
                Thread.sleep(500);
            }catch(InterruptedException e){
                e.printStackTrace();
            }
        }

        NotaVenta objNotaVenta = new NotaVenta(this._ip, this._puerto, this._db, usuario, clave);

        try {
            String r = "msg»Ha ocurrido un error inesperado, por favor, vuelva a intentarlo más tarde o " +
                "contáctese con el administrador del sistema para mayor información.";

            String WHERE = request.getParameter("WHERE");
            String p = request.getParameter("p") != null ? request.getParameter("p") : "0";
            String id = request.getParameter("id");
            String guardar = request.getParameter("gr");
            //String id_comprobante_diario = request.getParameter("idCD");
            String serie_factura = request.getParameter("sf");
            String autorizacion = request.getParameter("auf");
            String num_factura = request.getParameter("nf");
            String id_proveedor = request.getParameter("coP");
            String fecha_compra = request.getParameter("fc");
            String fecha_ven_factura = request.getParameter("fv");
            String observacion = request.getParameter("ob");
            String total_pagado = request.getParameter("tt");
            int tope = Integer.valueOf(request.getParameter("tope"));

            String id_articulos = "";
            String cantidades = "";
            String precios_unitarios = "";
            String totales = "";

            for(int i=0; i<=tope; i++){
                if(request.getParameter("idA"+i)!=null){
                    id_articulos += request.getParameter("idA"+i) + ",";
                    cantidades += request.getParameter("c"+i) + ",";
                    precios_unitarios += request.getParameter("pu"+i) + ",";
                    totales += request.getParameter("t"+i) + ",";
                }
            }
            if(id_articulos.compareTo("")!=0){
                id_articulos = id_articulos.substring(0, id_articulos.length()-1);
                cantidades = cantidades.substring(0, cantidades.length()-1);
                precios_unitarios = precios_unitarios.substring(0, precios_unitarios.length()-1);
                totales = totales.substring(0, totales.length()-1);
            }

            /* retenciones */
            String sustento_tributario = request.getParameter("sustento_tributario");
            String num_serie_ret = request.getParameter("sr");
            String autorizacion_ret = request.getParameter("autr");
            String num_retencion = request.getParameter("nr");
            String fecha_emision_ret = request.getParameter("fre");
            String ejercicio_fiscal = request.getParameter("ef");
            String ejercicio_fiscal_mes = request.getParameter("efm");
            String total_retenido = request.getParameter("tvr");
            fecha_emision_ret = (fecha_emision_ret.compareTo("")!=0) ? "'"+fecha_emision_ret+"'" : "NULL";

            String id_retenciones = "";
            String bases_imponibles = "";
            String valores_retenidos = "";
            for(int i=0; i<10; i++){
                if(request.getParameter("idR"+i)!=null){
                    id_retenciones += request.getParameter("idR"+i) + ",";
                    bases_imponibles += request.getParameter("bi"+i) + ",";
                    valores_retenidos += request.getParameter("vr"+i) + ",";
                }
            }
            if(id_retenciones.compareTo("")!=0){
                id_retenciones = id_retenciones.substring(0, id_retenciones.length()-1);
                bases_imponibles = bases_imponibles.substring(0, bases_imponibles.length()-1);
                valores_retenidos = valores_retenidos.substring(0, valores_retenidos.length()-1);
            }else{
                num_serie_ret = "";
                autorizacion_ret = "";
                num_retencion = "0";
                fecha_emision_ret = "NULL";
                ejercicio_fiscal = "NULL";
                total_retenido = "0";
            }

            /* actualizo datos de la factura del proveedor */
            Proveedor objProveedor = new Proveedor(this._ip, this._puerto, this._db, usuario, clave);
            objProveedor.actualizar(id_proveedor, autorizacion, serie_factura, fecha_ven_factura);
            objProveedor.cerrar();

            String id_factura_id_compDiario = "-1:-1";
            if(!objNotaVenta.retencionDuplicada(id, num_serie_ret, num_retencion)){
                if(id.compareTo("-1")==0){
                    id_factura_id_compDiario = objNotaVenta.insertar(id_sucursal, serie_factura, autorizacion, num_factura, id_proveedor, fecha_compra, fecha_ven_factura,
                            observacion, total_pagado, id_articulos, cantidades,precios_unitarios, totales, 
                            sustento_tributario, num_serie_ret, autorizacion_ret, num_retencion, fecha_emision_ret,
                            ejercicio_fiscal, ejercicio_fiscal_mes, total_retenido, id_retenciones, bases_imponibles, valores_retenidos);
                    if(id_factura_id_compDiario.compareTo("-1:-1") != 0){
                        Auditoria auditoria = new Auditoria(this._ip, this._puerto, this._db, usuario, clave);
                        auditoria.setRegistro(request, "INGRESO DE NUEVA FACTURA DE COMPRA: "+serie_factura+"-"+num_factura);
                        auditoria.cerrar();
                        String vec[] = id_factura_id_compDiario.split(":");
                        r = "err»0^vta»cmp^fun»inv_nota_ventaEditar("+vec[0]+");"+(guardar.compareTo("1")==0?"imprimir('pdfComprobanteDiario?id="+vec[1]+"');":"")+
                            "^tbl»"+objNotaVenta.paginar("vta_nota_venta_compra", "id_nota_venta_compra,razon_social,numero_nota_venta,toDateSQL(fecha_compra),total_compra,txt_anulado", WHERE, Integer.parseInt(p), 100)+
                            "^msg»Nota de Venta de compra registrada satisfactoriamente.";
                    }
                /*}else{
                    id_factura_id_compDiario = objNotaVenta.actualizar(id_sucursal, id, id_comprobante_diario, serie_factura, autorizacion, num_factura, id_proveedor, fecha_compra, fecha_ven_factura,
                            observacion, subtotal, descuento, iva_0, iva_12, total_pagado, id_articulos, cantidades,
                            precios_unitarios, subtotales, descuentos, ivas, totales, num_serie_ret, autorizacion_ret, num_retencion, fecha_emision_ret,
                            ejercicio_fiscal, total_retenido, id_retenciones, bases_imponibles, valores_retenidos, id_rol);
                    if(id_factura_id_compDiario.compareTo("-1:-1") != 0){
                        Auditoria auditoria = new Auditoria(this._ip, this._puerto, this._db, usuario, clave);
                        auditoria.setRegistro(request, "ACTUALIZACION DE LA INFORMACION DE LA FACTURA DE COMPRA: "+serie_factura+"-"+num_factura);
                        auditoria.cerrar();
                        String vec[] = id_factura_id_compDiario.split(":");
                        r = "err»0^vta»cmp^fun»inv_compraEditar("+vec[0]+");"+(guardar.compareTo("1")==0?"imprimir('pdfComprobanteDiario?id="+vec[1]+"');":"")+
                            "^tbl»"+objNotaVenta.paginar("vta_factura_compra", "id_factura_compra,razon_social,numero_factura,toDateSQL(fecha_compra),total_compra", WHERE, Integer.parseInt(p), 100)+
                            "^msg»Factura de compra guardada satisfactoriamente.";
                    }*/
                }

            }else{
                r = "msg»La retención con número "+num_serie_ret+"-"+num_retencion+" ya ha sido emitida.";
            }
            out.print(r);
        } finally {
            conf.setValor("bloqueo_libros", "false");
            conf.cerrar();
            objNotaVenta.cerrar();
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
