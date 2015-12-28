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
import jm.inv.clas.NotaCreditoCompra;
import jm.seg.clas.Auditoria;

/**
 *
 * @author Jorge
 */
public class frmNotaCreditoGuardar extends HttpServlet {
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

        NotaCreditoCompra objNotaCreditoCompra = new NotaCreditoCompra(this._ip, this._puerto, this._db, usuario, clave);

        try {
            String r = "msg»Ha ocurrido un error inesperado, por favor, vuelva a intentarlo más tarde o " +
                "contáctese con el administrador del sistema para mayor información.";

            //String WHERE = request.getParameter("WHERE");
            String idFactura = request.getParameter("id");
            //String imprimir = request.getParameter("imp");
            //String id_comprobante_ingreso = request.getParameter("idCI");
            String ndSerie = request.getParameter("ncSerie");
            String ndNum = request.getParameter("ncNum");
            String autorizacion = request.getParameter("ncAut");
            String fecha_emision = request.getParameter("ncFeEm");
            String comprobante_modifica = request.getParameter("ncCoMo");
            String tipo = request.getParameter("tipo");
            String razon_modificacion = request.getParameter("ncRaMo");
            
            String subtotal = request.getParameter("ncsubt");
            String subtotal_0 = request.getParameter("ncsubt0");
            String subtotal_2 = request.getParameter("ncsubt2");
            String subtotal_6 = request.getParameter("ncsubt6");
            String descuento = request.getParameter("ncdt");
            String iva_2 = request.getParameter("nciva2");
            //String ir = request.getParameter("ncIr");
            String total = request.getParameter("nctt");
            //String IdPLIR = request.getParameter("ncIdPLIR");
            int tope = Integer.valueOf(request.getParameter("nctope"));

            subtotal_0 = (subtotal_0.compareTo("")!=0) ? subtotal_0 : "0";
            subtotal_2 = (subtotal_2.compareTo("")!=0) ? subtotal_2 : "0";
            subtotal_6 = (subtotal_6.compareTo("")!=0) ? subtotal_6 : "0";
            descuento = (descuento.compareTo("")!=0) ? descuento : "0";
            iva_2 = (iva_2.compareTo("")!=0) ? iva_2 : "0";

            String ids_factura_compra_detalle = "";
            String cantidades = "";
            String p_sts = "";
            String descs = "";
            String ivas = "";
            String totales = "";
            for(int i=0; i<=tope; i++){
                if(request.getParameter("chProd"+i)!=null){
                    if(request.getParameter("chProd"+i).compareTo("true")==0){
                        ids_factura_compra_detalle += request.getParameter("idFacVenDet"+i) + ",";
                        cantidades += request.getParameter("ncC"+i) + ",";
                        p_sts += request.getParameter("ncST"+i) + ",";
                        descs += request.getParameter("ncDes"+i) + ",";
                        ivas += request.getParameter("ncIv"+i) + ",";
                        totales += request.getParameter("ncT"+i) + ",";
                    }
                }
            }

            if(ids_factura_compra_detalle.compareTo("")!=0){
                ids_factura_compra_detalle = ids_factura_compra_detalle.substring(0, ids_factura_compra_detalle.length()-1);
                cantidades = cantidades.substring(0, cantidades.length()-1);
                p_sts = p_sts.substring(0, p_sts.length()-1);
                descs = descs.substring(0, descs.length()-1);
                ivas = ivas.substring(0, ivas.length()-1);
                totales = totales.substring(0, totales.length()-1);
            }

            String id_factura_id_compDiario = "-1:-1";
            id_factura_id_compDiario = objNotaCreditoCompra.insertar(id_sucursal, idFactura, usuario, ndSerie,
                    ndNum, autorizacion, fecha_emision, comprobante_modifica, tipo, razon_modificacion,
                    subtotal, subtotal_0, subtotal_2, subtotal_6, descuento, iva_2, total, ids_factura_compra_detalle,
                    cantidades, p_sts, descs, ivas, totales);
            if(id_factura_id_compDiario.compareTo("-1:-1") != 0){
                Auditoria auditoria = new Auditoria(this._ip, this._puerto, this._db, usuario, clave);
                auditoria.setRegistro(request, "REGISTRO DE NOTA DE CREDITO DE COMPRA: "+ndSerie+"-"+ndNum);
                auditoria.cerrar();
                r = "err»0^vta»cmp^msg»Nota de crédito registrada satisfactoriamente.";
            }else{
                r = "msg»" + objNotaCreditoCompra.getError();
            }

            out.print(r);

        } finally {
            conf.setValor("bloqueo_libros", "false");
            conf.cerrar();
            objNotaCreditoCompra.cerrar();
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
