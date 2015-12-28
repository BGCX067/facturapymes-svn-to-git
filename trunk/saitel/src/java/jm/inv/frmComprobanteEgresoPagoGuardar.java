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
import jm.cnt.clas.ComprobanteEgreso;
import jm.seg.clas.Auditoria;
import jm.web.Fecha;

/**
 *
 * @author Jorge
 */
public class frmComprobanteEgresoPagoGuardar extends HttpServlet {
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
        //int id_sucursal = (Integer)sesion.getAttribute("sucursal");
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
        
        ComprobanteEgreso objComprobanteEgreso = new ComprobanteEgreso(this._ip, this._puerto, this._db, usuario, clave);

        try {
            String r = "msg^Ha ocurrido un error inesperado, por favor, vuelva a intentarlo más tarde o " +
                    "contáctese con el administrador del sistema para mayor información.";
            String id = request.getParameter("id");
            String id_proveedor = request.getParameter("coP");
            String guardar = request.getParameter("g");
            String fecha_proceso = request.getParameter("f");
            String fecha_efectivo = request.getParameter("fe");
            String id_sucursal = request.getParameter("id_sucursal");
            String detalle = request.getParameter("c");
            String total = request.getParameter("t");
            String forma_pago = request.getParameter("fp");
            String idsCuentas = request.getParameter("pc");
            String debe = request.getParameter("d");
            String haber = request.getParameter("h");
            //String ids_factura_pagos = request.getParameter("idFP");
            String ids_factura = request.getParameter("idF");
            String abonos = request.getParameter("ab");
            String documentos = request.getParameter("doc");
            String total_pago = request.getParameter("tp");
            long num_compEgr = objComprobanteEgreso.getNumComprobante();
            String ci_ruc = request.getParameter("ru");
            String orden_de = request.getParameter("nb");
            String id_banco = request.getParameter("id_bc");
            String num_cheque = request.getParameter("ch");
            String cuenta = request.getParameter("ct");
            String banco = request.getParameter("bc");
            String son = request.getParameter("sn");
            String lugar = request.getParameter("lu");

            if(forma_pago.compareTo("e")==0){
                num_cheque = "0";
                banco = "";
                fecha_efectivo = Fecha.getFecha("ISO");
                lugar = "";
            }

            if(!objComprobanteEgreso.chequeDuplicado(id, num_cheque, id_banco)){
                if(id.compareTo("-1")==0){
                    String id_factura_pago_id_egreso = objComprobanteEgreso.ingresarPago(Integer.parseInt(id_sucursal), String.valueOf(num_compEgr), fecha_proceso, fecha_efectivo, ci_ruc, orden_de, forma_pago, num_cheque, cuenta,
                            banco, total, son, detalle, lugar, idsCuentas, debe, haber, ids_factura, abonos, documentos, total_pago, id_banco);
                    if(id_factura_pago_id_egreso.compareTo("-1:-1") != 0){
                        String vec_id_factura_pago_id_egreso [] = id_factura_pago_id_egreso.split(":");
                        Auditoria auditoria = new Auditoria(this._ip, this._puerto, this._db, usuario, clave);
                        auditoria.setRegistro(request, "INGRESO DE NUEVO COMPROBANTE DE EGRESO CON NUMERO: "+num_compEgr+" POR CONCEPTO: "+detalle);
                        r = "err»0^vta»vta_nc^fun»inv_buscarPagos();inv_proveedorPagar("+id_proveedor+");inv_facturaPagoEditar("+vec_id_factura_pago_id_egreso[0]+");"+(guardar.compareTo("1")==0?"imprimir('pdfComprobanteEgreso?id="+vec_id_factura_pago_id_egreso[1]+"');":"")+
                                "^msg»Información ingresada satisfactoriamente.";
                        auditoria.cerrar();
                    }else{
                        r = "msg»" + objComprobanteEgreso.getError();
                    }
                }
            }else{
                r = "msg»Número de cheque repetido.";
            }

            out.print(r);

        } finally {
            conf.setValor("bloqueo_libros", "false");
            conf.cerrar();
            objComprobanteEgreso.cerrar();
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
