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
import jm.cnt.clas.Banco;
import jm.cnt.clas.ComprobanteEgreso;
import jm.seg.clas.Auditoria;
import jm.web.Fecha;

/**
 *
 * @author Jorge
 */
public class frmComprobanteEgresoGuardar extends HttpServlet {
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
        
        ComprobanteEgreso objComprobanteEgreso = new ComprobanteEgreso(this._ip, this._puerto, this._db, usuario, clave);
        try {
            String r = "msg»Ha ocurrido un error inesperado, por favor, vuelva a intentarlo más tarde o " +
                    "contáctese con el administrador del sistema para mayor información.";
            
            String id = request.getParameter("id");
            String guardar = request.getParameter("g");
            long num_compEgr = objComprobanteEgreso.getNumComprobante();
            String fecha_proceso = request.getParameter("f");
            String fecha_efectivo = request.getParameter("fe");
            String ci_ruc = request.getParameter("ru");
            String orden_de = request.getParameter("nb");
            String id_banco = request.getParameter("id_bc");
            String banco = request.getParameter("bc");
            String num_cheque = request.getParameter("ch");
            String cuenta = request.getParameter("ct");
            String total = request.getParameter("t");
            String valor_son = request.getParameter("vs");
            String son = request.getParameter("sn");
            String detalle = request.getParameter("c");
            String lugar = request.getParameter("lu");
            String tp = request.getParameter("tp");
            String idsCuentas = request.getParameter("pc");
            String debe = request.getParameter("d");
            String haber = request.getParameter("h");
            
            int id_comprobante = -1;
            String tipo_pago = "c";
            String comp_editar = "cnt_comprobanteEditar(_PK_);";
            if(tp.toLowerCase().compareTo("true")==0){
                comp_editar = "";
                tipo_pago = "e";
                num_cheque = "0";
                banco = "";
                fecha_efectivo = Fecha.getFecha("ISO");    /* cuando es el pago en efectivo se efectiviza en ese momento */
                lugar = "";
            }

            if(!objComprobanteEgreso.chequeDuplicado(id, num_cheque, id_banco)){
                if(id.compareTo("-1")==0){
                    id_comprobante = objComprobanteEgreso.insertar(id_sucursal, num_compEgr, fecha_proceso, fecha_efectivo, ci_ruc, orden_de, tipo_pago, num_cheque, cuenta,
                            banco, total, valor_son, son, detalle, lugar, idsCuentas, debe, haber, id_banco);
                    if(id_comprobante != -1){
                        if(tipo_pago.compareTo("c")==0){
                            Banco objBanco = new Banco(this._ip, this._puerto, this._db, usuario, clave);
                            objBanco.actualizar(id_banco, num_cheque, lugar);
                            objBanco.cerrar();
                        }
                        Auditoria auditoria = new Auditoria(this._ip, this._puerto, this._db, usuario, clave);
                        auditoria.setRegistro(request, "INGRESO DE NUEVO COMPROBANTE DE EGRESO CON NUMERO: "+num_compEgr+" POR CONCEPTO: "+detalle);
                        auditoria.cerrar();
                        if(comp_editar.compareTo("")!=0){
                            comp_editar = comp_editar.replace("_PK_", String.valueOf(id_comprobante));
                        }
                        r = "err»0^vta»cmp^fun»cnt_filtrarComprobantes();"+comp_editar+(guardar.compareTo("1")==0?"imprimir('pdfComprobanteEgreso?id="+id_comprobante+"');":"")+"^msg»Información ingresada satisfactoriamente.";
                    }
                /*}else{
                    id_comprobante = objComprobanteEgreso.actualizar(id, fecha_proceso, fecha_efectivo, ci_ruc, orden_de, tipo_pago, num_cheque, cuenta,
                            banco, total, valor_son, son, detalle, lugar, idsCuentas, debe, haber, id_banco);
                    if(id_comprobante != -1){
                        Auditoria auditoria = new Auditoria(this._ip, this._puerto, this._db, usuario, clave);
                        auditoria.setRegistro(request, "ACTUALIZACION DEL COMPROBANTE DE EGRESO NUMERO: "+num_compEgr+" POR CONCEPTO: "+detalle);
                        auditoria.cerrar();
                        if(comp_editar.compareTo("")!=0){
                            comp_editar = comp_editar.replace("_PK_", id);
                        }
                        r = "err»0^vta»cmp^fun»cnt_filtrarComprobantes();"+comp_editar+(guardar.compareTo("1")==0?"imprimir('pdfComprobanteEgreso?id="+id+"');":"")+"^msg»Información guardada satisfactoriamente.";
                    }*/
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
