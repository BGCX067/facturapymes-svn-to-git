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

package jm.cnt;

import java.io.*;

import javax.servlet.*;
import javax.servlet.http.*;
import jm.adm.clas.Configuracion;
import jm.cnt.clas.ComprobanteIngreso;
import jm.fac.clas.DocumentoBanco;
import jm.seg.clas.Auditoria;

/**
 *
 * @author Jorge
 */
public class frmComprobanteIngresoGuardar extends HttpServlet {
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
        
        ComprobanteIngreso objComprobanteIngreso = new ComprobanteIngreso(this._ip, this._puerto, this._db, usuario, clave);
        
        try {
            String r = "msg»Ha ocurrido un error inesperado, por favor, vuelva a intentarlo más tarde o " +
                    "contáctese con el administrador del sistema para mayor información.";

            String id = request.getParameter("id");
            String guardar = request.getParameter("g");
            long numero_doc = objComprobanteIngreso.getNumComprobante();
            String fecha_proceso = request.getParameter("f");
            String nombre = request.getParameter("nb");
            String num_cheque = request.getParameter("ch");
            String banco = request.getParameter("bc");
            String num_comp_pago = request.getParameter("ncp");
            String gastos_bancos = request.getParameter("gb");
            String total = request.getParameter("t");
            String valor_son = request.getParameter("vs");
            String son = request.getParameter("sn");
            String detalle = request.getParameter("c");
            String fecha_efectivo = request.getParameter("fe");
            String forma_pago = request.getParameter("tp");
            String idsCuentas = request.getParameter("pc");
            String debe = request.getParameter("d");
            String haber = request.getParameter("h");
            String saldo_doc = request.getParameter("saldo_doc");
            
            switch(forma_pago.charAt(0)){
                case 'e':   /* efectivo */
                    banco = "";
                    num_cheque = "";
                    num_comp_pago = "";
                    gastos_bancos = "0";
                    fecha_efectivo = fecha_proceso;
                case 'c':   /* cheque */
                    num_comp_pago = "";
                    gastos_bancos = "0";
                    fecha_efectivo = "";
                break;
                case 'p':   /* deposito bancario */
                    num_cheque = "";
                    banco = "";
                    gastos_bancos = "0";
                    fecha_efectivo = fecha_proceso;
                break;
                case 't':
                case 'j':   /* transferencia */
                    num_cheque = "";
                    banco = "";
                    fecha_efectivo = fecha_proceso;
                break;
            }

            int id_comprobante = -1;
            if(id.compareTo("-1")==0){

                if(saldo_doc.compareTo("")!=0 && saldo_doc.compareTo("0")!=0){
                    DocumentoBanco objDocumentoBanco = new DocumentoBanco(this._ip, this._puerto, this._db, usuario, clave);
                    if(objDocumentoBanco.hayDocumento(num_comp_pago)){
                        objDocumentoBanco.actualizar(num_comp_pago, saldo_doc);
                    }else{
                         float saldo = Float.parseFloat(saldo_doc) - (Float.parseFloat(total));
                        objDocumentoBanco.insertar(usuario, num_comp_pago, saldo_doc, saldo);
                    }
                    objDocumentoBanco.cerrar();
                }

                id_comprobante = objComprobanteIngreso.insertar(id_sucursal, usuario, numero_doc, fecha_proceso, fecha_efectivo, nombre, forma_pago, num_cheque,
                        banco, num_comp_pago, gastos_bancos, total, valor_son, son, detalle, idsCuentas, debe, haber);
                if(id_comprobante != -1){
                    Auditoria auditoria = new Auditoria(this._ip, this._puerto, this._db, usuario, clave);
                    auditoria.setRegistro(request, "INGRESO DE NUEVO COMPROBANTE DE INGRESO CON NUMERO: "+numero_doc+" POR CONCEPTO: "+detalle);
                    auditoria.cerrar();
                    r = "err»0^vta»cmp^fun»cnt_filtrarComprobantes();"+(guardar.compareTo("1")==0?"imprimir('pdfComprobanteIngreso?id="+id_comprobante+"');":"")+"^msg»Información ingresada satisfactoriamente.";
                }
            /*}else{
                id_comprobante = objComprobanteIngreso.actualizar(id, fecha_proceso, fecha_efectivo, nombre, forma_pago, num_cheque,
                        banco, num_comp_pago, gastos_bancos, total, son, detalle, idsCuentas, debe, haber);
                if(id_comprobante != -1){
                    Auditoria auditoria = new Auditoria(this._ip, this._puerto, this._db, usuario, clave);
                    auditoria.setRegistro(request, "ACTUALIZACION DEL COMPROBANTE DE INGRESO NUMERO: "+numero_doc+" POR CONCEPTO: "+detalle);
                    auditoria.cerrar();
                    r = "err»0^vta»cmp^fun»cnt_filtrarComprobantes();"+(guardar.compareTo("1")==0?"imprimir('pdfComprobanteIngreso?id="+id+"');":"")+"^msg»Información guardada satisfactoriamente.";
                }*/
            }

            out.print(r);
        } finally {
            conf.setValor("bloqueo_libros", "false");
            conf.cerrar();
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
