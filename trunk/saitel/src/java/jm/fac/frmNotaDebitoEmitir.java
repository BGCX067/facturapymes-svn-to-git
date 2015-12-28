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
import jm.fac.clas.NotaDebitoVenta;
import jm.seg.clas.Auditoria;

/**
 *
 * @author Jorge
 */
public class frmNotaDebitoEmitir extends HttpServlet {
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
        
        NotaDebitoVenta objNotaDebitoVenta = new NotaDebitoVenta(this._ip, this._puerto, this._db, usuario, clave);

        try {
            String r = "msg»Ha ocurrido un error inesperado, por favor, vuelva a intentarlo más tarde o " +
                "contáctese con el administrador del sistema para mayor información.";

            //String WHERE = request.getParameter("WHERE");
            String idFactura = request.getParameter("idFactura");
            //String imprimir = request.getParameter("imp");
            //String id_comprobante_ingreso = request.getParameter("idCI");
            String ndSerie = request.getParameter("ndSerie");
            String ndNum = request.getParameter("ndNum");
            String autorizacion = request.getParameter("ndAut");
            String ruc = request.getParameter("ndRuc").toUpperCase();
            String razon_social = request.getParameter("ndRaSo").toUpperCase();
            String fecha_emision = request.getParameter("ndFeEm");
            String comprobante_modifica = request.getParameter("ndCoMo");
            String forma_pago = request.getParameter("ndFoPa");
            String banco = request.getParameter("ndbco");
            String num_cheque = request.getParameter("ndchq");
            String num_comp_pago = request.getParameter("ndncp");
            String gastos_bancos = request.getParameter("ndgb");
            String id_plan_cuenta_banco = request.getParameter("ndidPcBc").compareTo("")!=0 ? request.getParameter("ndidPcBc") : "0";
            String son = request.getParameter("ndson");
            String razon_modificacion = request.getParameter("ndRaMo");
            String valor_modificacion = request.getParameter("ndVaMo");
            String iva_0 = request.getParameter("ndIva0");
            String iva_12 = request.getParameter("ndIva12");
            String total = request.getParameter("ndTotal");
            int tope = Integer.valueOf(request.getParameter("ndtope"));

            iva_0 = (iva_0.compareTo("")!=0) ? iva_0 : "0";
            iva_12 = (iva_12.compareTo("")!=0) ? iva_12 : "0";
            gastos_bancos = (gastos_bancos.compareTo("")!=0) ? gastos_bancos : "0";

            String id_productos = "";
            String descripciones = "";
            String cantidades = "";
            String precios_costo = "";
            String precios_unitarios = "";
            String subtotales = "";
            String descuentos = "";
            String ivas = "";
            String totales = "";

            for(int i=0; i<=tope; i++){
                if(request.getParameter("ndidPr"+i)!=null){
                    id_productos += request.getParameter("ndidPr"+i) + ",";
                    descripciones += request.getParameter("ndcpt"+i) + ",";
                    cantidades += request.getParameter("ndct"+i) + ",";
                    precios_costo += request.getParameter("ndp_c"+i) + ",";
                    precios_unitarios += request.getParameter("ndpu"+i) + ",";
                    subtotales += request.getParameter("ndsbt"+i) + ",";
                    descuentos += request.getParameter("nddes"+i) + ",";
                    ivas += request.getParameter("ndv"+i) + ",";
                    totales += request.getParameter("ndtt"+i) + ",";
                }
            }

            if(id_productos.compareTo("")!=0){
                id_productos = id_productos.substring(0, id_productos.length()-1);
                descripciones = descripciones.substring(0, descripciones.length()-1);
                cantidades = cantidades.substring(0, cantidades.length()-1);
                precios_costo = precios_costo.substring(0, precios_costo.length()-1);
                precios_unitarios = precios_unitarios.substring(0, precios_unitarios.length()-1);
                subtotales = subtotales.substring(0, subtotales.length()-1);
                descuentos = descuentos.substring(0, descuentos.length()-1);
                ivas = ivas.substring(0, ivas.length()-1);
                totales = totales.substring(0, totales.length()-1);

                String prod_sin_stock = objNotaDebitoVenta.verificarStock(id_sucursal, id_productos, cantidades);
                if(prod_sin_stock.compareTo("")==0){
                    String id_factura_id_compDiario = "-1:-1";
                    if(!objNotaDebitoVenta.NotaDuplicada(ndSerie, ndNum)){
                        id_factura_id_compDiario = objNotaDebitoVenta.insertar(id_sucursal, id_punto_emision, idFactura, usuario, ndSerie, ndNum, autorizacion, ruc,
                                razon_social, fecha_emision, comprobante_modifica, forma_pago, banco, num_cheque, num_comp_pago, gastos_bancos,
                                id_plan_cuenta_banco, son, razon_modificacion, valor_modificacion, iva_0, iva_12, total,
                                id_productos, descripciones, cantidades, precios_costo, precios_unitarios,
                                subtotales, descuentos, ivas, totales);
                        if(id_factura_id_compDiario.compareTo("-1:-1") != 0){
                            Auditoria auditoria = new Auditoria(this._ip, this._puerto, this._db, usuario, clave);
                            auditoria.setRegistro(request, "REGISTRO DE NOTA DE DEBITO EN VENTA: "+ndSerie+"-"+ndNum);
                            auditoria.cerrar();
                            String vec[] = id_factura_id_compDiario.split(":");
                            r = "err»0^vta»cmp^fun»imprimir('pdfNotaDebito?id="+vec[0]+"');";
                        }
                    }else{
                        r = "msg»El número de nota de débito "+ndSerie+"-"+ndNum+" ya ha sido emitida.";
                    }
                }else{
                    r = "msg»Stock insuficiente. El stock del producto de código: " + prod_sin_stock + " ha disminuido por una venta realizada desde otra caja ubicada en la sucursal.";
                }
            }


            out.print(r);

        } finally {
            conf.setValor("bloqueo_libros", "false");
            conf.cerrar();
            objNotaDebitoVenta.cerrar();
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
