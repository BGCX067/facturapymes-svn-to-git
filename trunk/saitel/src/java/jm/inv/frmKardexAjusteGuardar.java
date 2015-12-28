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
import java.sql.ResultSet;

import javax.servlet.*;
import javax.servlet.http.*;
import jm.adm.clas.Configuracion;
import jm.cnt.clas.ComprobanteDiario;
import jm.fac.clas.FacturaVenta;
import jm.inv.clas.Producto;
import jm.seg.clas.Auditoria;

/**
 *
 * @author Jorge
 */
public class frmKardexAjusteGuardar extends HttpServlet {
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
        
        try {
            String r = "msg»Ha ocurrido un error inesperado, por favor, vuelva a intentarlo más tarde o " +
                    "contáctese con el administrador del sistema para mayor información.";
            String idProducto = request.getParameter("idPr");
            String imprimir = request.getParameter("imp");
            String numero_nota = request.getParameter("nc");
            String fecha_proceso = request.getParameter("fp");
            String detalle = request.getParameter("dt");
            //String total = request.getParameter("td");
            
            String idsCuentas = "";
            String debe = "";
            String haber = "";
            String auxDebe = "";
            String auxHaber = "";
            for(int i=0; i<=50; i++){
                if(request.getParameter("PC"+i)!=null){
                    auxDebe = request.getParameter("d"+i)!=null ? (request.getParameter("d"+i).compareTo("")!=0 ? request.getParameter("d"+i) : "0") : "0";
                    auxHaber = request.getParameter("h"+i)!=null ? (request.getParameter("h"+i).compareTo("")!=0 ? request.getParameter("h"+i) : "0") : "0";
                    idsCuentas += request.getParameter("PC"+i) + ",";
                    debe += auxDebe + ",";
                    haber += auxHaber + ",";
                }
            }
            if(idsCuentas.compareTo("")!=0){
                idsCuentas = idsCuentas.substring(0, idsCuentas.length()-1);
                debe = debe.substring(0, debe.length()-1);
                haber = haber.substring(0, haber.length()-1);
            }
            
            
            String cantidad = request.getParameter("can");
            String valor_unitario = request.getParameter("vu");
            String valor_total = request.getParameter("vt");
            String ajuste = request.getParameter("ajuste");
            boolean ok = true;
            if(ajuste.compareTo("s")==0){
                FacturaVenta objFacturaVenta = new FacturaVenta(this._ip, this._puerto, this._db, usuario, clave);
                String prod_sin_stock = objFacturaVenta.verificarStock(id_sucursal, idProducto, cantidad);
                if(prod_sin_stock.compareTo("")!=0){
                    ok=false;
                    r = "msg»Stock del producto insuficiente para realizar ajuste de salida.";
                }
                objFacturaVenta.cerrar();
            }
            if(ok){
                ComprobanteDiario objComprobanteDiario = new ComprobanteDiario(this._ip, this._puerto, this._db, usuario, clave);
                int id_comprobante = -1;
                id_comprobante = objComprobanteDiario.insertar(id_sucursal, numero_nota, fecha_proceso, detalle, valor_total, idsCuentas, debe, haber,
                        idProducto, cantidad, valor_unitario, valor_total, ajuste);
                if(id_comprobante != -1){
                    Producto objProducto = new Producto(this._ip, this._puerto, this._db, usuario, clave);
                    ResultSet rsProducto = objProducto.getProducto(idProducto);
                    String descripcion = "0";
                    try{
                        if(rsProducto.next()){
                            descripcion = (rsProducto.getString("descripcion")!=null) ? rsProducto.getString("descripcion") : "";
                            rsProducto.close();
                        }
                    }catch(Exception e){
                        e.printStackTrace();
                    }finally{
                        objProducto.cerrar();
                    }

                    Auditoria auditoria = new Auditoria(this._ip, this._puerto, this._db, usuario, clave);
                    auditoria.setRegistro(request, "AJUSTE DE KARDEX DEL PRODUCTO "+descripcion+" POR CONCEPTO: "+detalle);
                    auditoria.cerrar();
                    r = "err»0^vta»cmp^fun»inv_verKardex("+idProducto+");"+(imprimir.compareTo("1")==0?"imprimir('pdfComprobanteDiario?id="+id_comprobante+"');":"")+"^msg»Información ingresada satisfactoriamente.";
                }
                objComprobanteDiario.cerrar();
            }
            
            out.print(r);
        } finally {
            conf.setValor("bloqueo_libros", "false");
            conf.cerrar();
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