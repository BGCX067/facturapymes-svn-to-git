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

package jm.fac;

import java.io.*;

import javax.servlet.*;
import javax.servlet.http.*;
import jm.adm.clas.Configuracion;
import jm.fac.clas.FacturaVenta;
import jm.seg.clas.Auditoria;

/**
 *
 * @author Jorge
 */
public class frmRetencionVentaGuardar extends HttpServlet {
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
        int id_punto_emision = (Integer)sesion.getAttribute("id_punto_emision");
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
        
        FacturaVenta objFacturaVenta = new FacturaVenta(this._ip, this._puerto, this._db, usuario, clave);

        try {
            String r = "msg»Ha ocurrido un error inesperado, por favor, vuelva a intentarlo más tarde o " +
                "contáctese con el administrador del sistema para mayor información.";

            String id_factura = request.getParameter("idFactura");
            String id_retencion_venta = request.getParameter("idRV");

            String serie_factura = request.getParameter("sf");
            String num_factura = request.getParameter("nf");

            String ret_num_serie = request.getParameter("sr");
            String ret_num_retencion = request.getParameter("nr");
            String ret_autorizacion = request.getParameter("aur");
            String ret_fecha_emision = request.getParameter("fer");
            String ret_impuesto_retenido = request.getParameter("tvr");
            String ret_ejercicio_fiscal = request.getParameter("ef");
            //String id_plan_cuenta_caja_banco = request.getParameter("idPcCajBans");

            String id_retenciones = "";
            String bases_imponibles = "";
            String valores_retenidos = "";
            for(int i=0; i<100; i++){
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

                if(id_retencion_venta.compareTo("-1")==0){
                    if(objFacturaVenta.insertarRetencion(id_factura, id_sucursal, id_punto_emision, ret_num_serie, ret_num_retencion, ret_autorizacion,
                            ret_fecha_emision, ret_ejercicio_fiscal, ret_impuesto_retenido, id_retenciones, bases_imponibles, 
                            valores_retenidos)){
                        Auditoria auditoria = new Auditoria(this._ip, this._puerto, this._db, usuario, clave);
                        auditoria.setRegistro(request, "REGISTRO DE COMPROBANTE DE RETENCION: "+ret_num_serie+"-"+ret_num_retencion+" PERTENECIENTE A LA FACTURA FACTURA DE VENTA: "+serie_factura+"-"+num_factura);
                        auditoria.cerrar();
                        r = "err»0^vta»cmp^msg»Comprobante de retención registrado satisfactoriamente.";
                    }
                }
                /*else{
                    if(objFacturaVenta.actualizarRetencion(id_retencion_venta, id_factura, ret_num_serie, ret_num_retencion, ret_autorizacion,
                            ret_fecha_emision, ret_ejercicio_fiscal, ret_impuesto_retenido, id_retenciones, bases_imponibles, valores_retenidos)){
                        Auditoria auditoria = new Auditoria(this._ip, this._puerto, this._db, usuario, clave);
                        auditoria.setRegistro(request, "ACTUALIZACION DE LA INFORMACION DEL COMPROBANTE DE RETENCION: "+ret_num_serie+"-"+ret_num_retencion+" PERTENECIENTE A LA FACTURA FACTURA DE VENTA: "+serie_factura+"-"+num_factura);
                        auditoria.cerrar();
                        r = "err»0^vta»cmp^msg»Comprobante de retención actualizado satisfactoriamente.";
                    }
                }*/
            }

            out.print(r);
        } finally {
            conf.setValor("bloqueo_libros", "false");
            conf.cerrar();
            objFacturaVenta.cerrar();
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
