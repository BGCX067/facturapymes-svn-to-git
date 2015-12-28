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
import jm.cnt.clas.ComprobanteIngreso;
import jm.fac.clas.DocumentoBanco;
import jm.seg.clas.Auditoria;
import jm.web.Fecha;

/**
 *
 * @author Jorge
 */
public class frmComprobanteIngresoCobroGuardar extends HttpServlet {
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
        
        /*Configuracion conf = new Configuracion(this._ip, this._puerto, this._db, usuario, clave);
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
        }*/

        ComprobanteIngreso objComprobanteIngreso = new ComprobanteIngreso(this._ip, this._puerto, this._db, usuario, clave);

        try {
            String r = "msg^Ha ocurrido un error inesperado, por favor, vuelva a intentarlo más tarde o " +
                    "contáctese con el administrador del sistema para mayor información.";
            String id = request.getParameter("id");
            String id_cliente = request.getParameter("id_cli");
            String imprimir = request.getParameter("imp");
            String fecha_proceso = request.getParameter("fp");
            String fecha_efectivo = request.getParameter("fe");
            String detalle = request.getParameter("dt");
            String total = request.getParameter("toPa");
            String forma_pago = request.getParameter("foPa");
            String ids_factura = request.getParameter("idFs");
            String idsCuentas = request.getParameter("pcs");
            String debe = request.getParameter("ds");
            String haber = request.getParameter("hs");
            //String id_factura_venta_cobros = request.getParameter("idFP");
            String abonos = request.getParameter("abs");
            String documentos = request.getParameter("doc");
            //String total_pago = request.getParameter("tp");
            long num_compIngr = objComprobanteIngreso.getNumComprobante();
            String orden_de = request.getParameter("nb");
            String num_cheque = request.getParameter("ch");
            String banco = request.getParameter("bc");
            String num_comp_pago = request.getParameter("ncp");
            String gastos_bancos = request.getParameter("gb");
            String valor_son = request.getParameter("vs");
            String son = request.getParameter("sn");
            String saldo_doc = request.getParameter("saldo_doc");

            if(forma_pago.compareTo("e")==0){
                num_cheque = "0";
                banco = "";
                fecha_efectivo = Fecha.getFecha("ISO");
            }

            //   RETENCIONES

            String ret_num_serie = request.getParameter("sr");
            String ret_num_retencion = request.getParameter("nr");
            String ret_autorizacion = request.getParameter("aur");
            String ret_fecha_emision = request.getParameter("fer");
            String ret_impuesto_retenido = request.getParameter("tvr");
            String ret_ejercicio_fiscal = request.getParameter("ef");
            String ret_ejercicio_fiscal_mes = request.getParameter("efm");
            String id_factura_venta_afecta = request.getParameter("id_factura_venta");

            String id_retenciones = "";
            String bases_imponibles = "";
            String valores_retenidos = "";
            String codBI = "";
            if(forma_pago.compareTo("d")!=0){
                for(int i=0; i<100; i++){
                    if(request.getParameter("idR"+i)!=null){
                        id_retenciones += request.getParameter("idR"+i) + ",";
                        bases_imponibles += request.getParameter("bi"+i) + ",";
                        valores_retenidos += request.getParameter("vr"+i) + ",";
                        codBI += request.getParameter("codi"+i) + ",";
                    }
                }
                if(id_retenciones.compareTo("")!=0){
                    id_retenciones = id_retenciones.substring(0, id_retenciones.length()-1);
                    bases_imponibles = bases_imponibles.substring(0, bases_imponibles.length()-1);
                    valores_retenidos = valores_retenidos.substring(0, valores_retenidos.length()-1);
                    codBI = codBI.substring(0, codBI.length()-1);
                }else{
                    ret_num_retencion = "";
                    ret_autorizacion = "";
                    ret_fecha_emision = "";
                    ret_impuesto_retenido = "0";
                    ret_ejercicio_fiscal = "0";
                }
            }else{
                ret_num_retencion = "";
                ret_autorizacion = "";
                ret_fecha_emision = "";
                ret_impuesto_retenido = "0";
                ret_ejercicio_fiscal = "0";
            }

            if(id.compareTo("-1")==0){
                String id_factura_cobro_id_ingreso = objComprobanteIngreso.insertar(id_sucursal, id_punto_emision, usuario, String.valueOf(num_compIngr), fecha_proceso, fecha_efectivo, orden_de,
                        forma_pago, num_cheque, banco, num_comp_pago, gastos_bancos, total, valor_son, son, detalle, idsCuentas, debe, haber, ids_factura, abonos, documentos, total,
                        ret_num_serie, ret_num_retencion, ret_autorizacion, ret_fecha_emision, ret_ejercicio_fiscal_mes, ret_ejercicio_fiscal,
                        ret_impuesto_retenido, id_factura_venta_afecta, id_retenciones, bases_imponibles, valores_retenidos, codBI);
                if(id_factura_cobro_id_ingreso.compareTo("-1:-1") != 0){
                    
                    if(saldo_doc.compareTo("")!=0 && saldo_doc.compareTo("0")!=0){
                        DocumentoBanco objDocumentoBanco = new DocumentoBanco(this._ip, this._puerto, this._db, usuario, clave);
                        if(objDocumentoBanco.hayDocumento(num_comp_pago)){
                            objDocumentoBanco.actualizar(num_comp_pago, valor_son);
                        }else{
                            float saldo = Float.parseFloat(saldo_doc) - (Float.parseFloat(valor_son) - Float.parseFloat(ret_impuesto_retenido));
                            objDocumentoBanco.insertar(usuario, num_comp_pago, saldo_doc, saldo);
                        }
                        objDocumentoBanco.cerrar();
                    }
                    
                    Auditoria auditoria = new Auditoria(this._ip, this._puerto, this._db, usuario, clave);
                    auditoria.setRegistro(request, "INGRESO DE NUEVO COMPROBANTE DE EGRESO CON NUMERO: "+num_compIngr+" POR CONCEPTO: "+detalle);
                    auditoria.cerrar();
                    String vec_id_factura_pago_id_ingreso [] = id_factura_cobro_id_ingreso.split(":");
                    r = "err»0^vta»vta_nc^fun»fac_buscarCobros();fac_clienteCobrar("+id_cliente+");fac_facturaCobroEditar("+vec_id_factura_pago_id_ingreso[0]+");"+(imprimir.compareTo("1")==0?"imprimir('pdfComprobanteIngreso?id="+vec_id_factura_pago_id_ingreso[1]+"');":"")+"^msg»Información ingresada satisfactoriamente.";
                }else{
                    r = "msg»"+objComprobanteIngreso.getError();
                }
            /*}else{
                int id_factura_venta_cobro = objComprobanteIngreso.actualizar(id, fecha_proceso, fecha_efectivo, orden_de, forma_pago, num_cheque, 
                        banco, total, son, detalle, idsCuentas, debe, haber, id_factura_venta_cobros, ids_factura, abonos, total_pago);
                if(id_factura_venta_cobro != -1){
                    Auditoria auditoria = new Auditoria(this._ip, this._puerto, this._db, usuario, clave);
                    auditoria.setRegistro(request, "ACTUALIZACION DEL COMPROBANTE DE EGRESO NUMERO: "+num_compIngr+" POR CONCEPTO: "+detalle);
                    auditoria.cerrar();
                    r = "err»0^vta»vta_nc^fun»fac_buscarCobros();fac_facturaCobroEditar("+id_factura_venta_cobro+");"+(guardar.compareTo("1")==0?"imprimir('pdfComprobanteIngreso?id="+id+"');":"")+"^msg»Información guardada satisfactoriamente.";
                }*/
            }

            out.print(r);

        } finally {
            /*conf.setValor("bloqueo_libros", "false");
            conf.cerrar();*/
            objComprobanteIngreso.cerrar();
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
