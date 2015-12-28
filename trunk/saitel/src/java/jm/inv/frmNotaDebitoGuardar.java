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
import jm.inv.clas.NotaDebitoCompra;
import jm.seg.clas.Auditoria;

/**
 *
 * @author Jorge
 */
public class frmNotaDebitoGuardar extends HttpServlet {
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

        NotaDebitoCompra objNotaDebitoCompra = new NotaDebitoCompra(this._ip, this._puerto, this._db, usuario, clave);

        try {
            String r = "msg»Ha ocurrido un error inesperado, por favor, vuelva a intentarlo más tarde o " +
                "contáctese con el administrador del sistema para mayor información.";

            //String WHERE = request.getParameter("WHERE");
            String idFactura = request.getParameter("id");
            //String imprimir = request.getParameter("imp");
            //String id_comprobante_ingreso = request.getParameter("idCI");
            String ndSerie = request.getParameter("ndSerie");
            String ndNum = request.getParameter("ndNum");
            String autorizacion = request.getParameter("ndAut");
            String fecha_emision = request.getParameter("ndFeEm");
            String comprobante_modifica = request.getParameter("ndCoMo");
            //String tipo = request.getParameter("tipo");

            String razon_modificacion = request.getParameter("ndRaMo");
            String valor_modificacion = request.getParameter("ndVaMo");
            //String descuento = request.getParameter("ndDesc");
            String iva_0 = request.getParameter("ndiv0");
            String iva_12 = request.getParameter("ndiv12");
            //String ir = request.getParameter("ndIr");
            String total = request.getParameter("ndtt");
            //String IdPLIR = request.getParameter("ndIdPLIR");
            int tope = Integer.valueOf(request.getParameter("ndtope"));

            iva_0 = (iva_0.compareTo("")!=0) ? iva_0 : "0";
            iva_12 = (iva_12.compareTo("")!=0) ? iva_12 : "0";

            String ids_productos = "";
            String cantidades = "";
            String p_uni = "";
            String p_sts = "";
            String descs = "";
            String ivas = "";
            String totales = "";
            for(int i=0; i<=tope; i++){
                if(request.getParameter("ndidA"+i)!=null){
                    ids_productos += request.getParameter("ndidA"+i) + ",";
                    cantidades += request.getParameter("ndc"+i) + ",";
                    p_uni += request.getParameter("ndpu"+i) + ",";
                    p_sts += request.getParameter("ndst"+i) + ",";
                    descs += request.getParameter("ndd"+i) + ",";
                    ivas += request.getParameter("ndv"+i) + ",";
                    totales += request.getParameter("ndt"+i) + ",";
                }
            }

            if(ids_productos.compareTo("")!=0){
                ids_productos = ids_productos.substring(0, ids_productos.length()-1);
                cantidades = cantidades.substring(0, cantidades.length()-1);
                p_uni = p_uni.substring(0, p_uni.length()-1);
                p_sts = p_sts.substring(0, p_sts.length()-1);
                descs = descs.substring(0, descs.length()-1);
                ivas = ivas.substring(0, ivas.length()-1);
                totales = totales.substring(0, totales.length()-1);
            }

            String id_factura_id_compDiario = "-1:-1";
            id_factura_id_compDiario = objNotaDebitoCompra.insertar(id_sucursal, idFactura, usuario, ndSerie,
                    ndNum, autorizacion, fecha_emision, comprobante_modifica, razon_modificacion,
                    valor_modificacion, iva_0, iva_12, total, ids_productos,
                    cantidades, p_uni, p_sts, descs, ivas, totales);
            if(id_factura_id_compDiario.compareTo("-1:-1") != 0){
                Auditoria auditoria = new Auditoria(this._ip, this._puerto, this._db, usuario, clave);
                auditoria.setRegistro(request, "REGISTRO DE NOTA DE DEBITO DE COMPRA: "+ndSerie+"-"+ndNum);
                auditoria.cerrar();
                r = "err»0^vta»cmp^msg»Nota de débito registrada satisfactoriamente.";
            }

            out.print(r);

        } finally {
            conf.setValor("bloqueo_libros", "false");
            conf.cerrar();
            objNotaDebitoCompra.cerrar();
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
