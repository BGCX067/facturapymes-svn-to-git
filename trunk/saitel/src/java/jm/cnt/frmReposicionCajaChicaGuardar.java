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
import jm.cnt.clas.ComprobanteEgreso;
import jm.cnt.clas.ReposicionCajaChica;
import jm.seg.clas.Auditoria;

/**
 *
 * @author Jorge
 */
public class frmReposicionCajaChicaGuardar extends HttpServlet {
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
        ReposicionCajaChica objReposicionCajaChica = new ReposicionCajaChica(this._ip, this._puerto, this._db, usuario, clave);
        
        try {
            String r = "msg»Ha ocurrido un error inesperado, por favor, vuelva a intentarlo más tarde o " +
                    "contáctese con el administrador del sistema para mayor información.";
            String id = request.getParameter("id");
            //String imprimir = request.getParameter("imprimir");
            String WHERE = request.getParameter("WHERE");
            String p = request.getParameter("p") != null ? request.getParameter("p") : "0";
            String responsable = request.getParameter("responsable");
            String fecha = request.getParameter("fecha");
            String saldo_caja = request.getParameter("saldo_caja");
            String id_banco = request.getParameter("bc1");
            String periodo_hasta = request.getParameter("periodo_hasta");

            long num_compEgr = objComprobanteEgreso.getNumComprobante();
            String fecha_proceso = request.getParameter("fp");
            String ci_ruc = request.getParameter("ru");
            String orden_de = request.getParameter("nb");
            String banco = request.getParameter("bc");
            String num_cheque = request.getParameter("ch");
            String cuenta = request.getParameter("ct");
            String total = request.getParameter("d0");   /* el debe de la primera cuenta */
            String son = request.getParameter("sn");
            String detalle = request.getParameter("dt");
            String lugar = request.getParameter("lu");
            String idCuenta0 = request.getParameter("PC0");
            String idCuenta1 = request.getParameter("PC1");
            //String debe = request.getParameter("d");
            //String haber = request.getParameter("h");

            if(!objComprobanteEgreso.chequeDuplicado(id, num_cheque, id_banco)){
                String id_comprobante = objReposicionCajaChica.reponer(id, id_sucursal, responsable, fecha, saldo_caja, id_banco, periodo_hasta,
                        num_compEgr, fecha_proceso, "", ci_ruc, orden_de, "c", num_cheque, cuenta,
                        banco, total, son, detalle, lugar, idCuenta0, idCuenta1);
                if(id_comprobante.compareTo("-1")!=0){
                    Auditoria auditoria = new Auditoria(this._ip, this._puerto, this._db, usuario, clave);
                    auditoria.setRegistro(request, "REGISTRO DE REPOSICION DE CAJA CHICA");
                    auditoria.cerrar();
                    r = "err»0^vta»vta1^fun»imprimir('pdfReposicionCajaChica?id="+id+"');imprimir('pdfComprobanteEgreso?id="+id_comprobante+"');"
                    + "^tbl»"+objReposicionCajaChica.paginar("tbl_reposicion_caja_chica", "id_reposicion_caja_chica,num_reposicion_caja_chica,toDateSQL(periodo_desde),toDateSQL(periodo_hasta),total_importe", WHERE, Integer.parseInt(p), 100)
                    + "^msg»Información ingresada satisfactoriamente.";
                }
            }else{
                r = "msg»Número de cheque repetido.";
            }
            
            out.print(r);
        } finally {
            conf.setValor("bloqueo_libros", "false");
            conf.cerrar();
            objComprobanteEgreso.cerrar();
            objReposicionCajaChica.cerrar();
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