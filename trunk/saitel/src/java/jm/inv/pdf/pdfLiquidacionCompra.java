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

package jm.inv.pdf;

import java.io.*;

import javax.servlet.*;
import javax.servlet.http.*;

import com.lowagie.text.pdf.*;
import com.lowagie.text.*;

import jm.web.Addons;
import java.sql.ResultSet;
import java.sql.SQLException;
import jm.web.Fecha;
import jm.adm.clas.Sucursal;
import jm.inv.clas.LiquidacionCompra;

/**
 *
 * @author Jorge
 */
public class pdfLiquidacionCompra extends HttpServlet {
    private String _ip = null;
    private int _puerto = 5432;
    private String _db = null;
    private String _usuario = null;
    private String _clave = null;

    public void init(ServletConfig config) throws ServletException
    {
        this._ip = config.getServletContext().getInitParameter("_IP");
        this._puerto = Integer.parseInt(config.getServletContext().getInitParameter("_PUERTO"));
        this._db = config.getServletContext().getInitParameter("_DB");
    }

    class Factura{
        public Factura(){}

        public void imprimir(HttpServletResponse response, int id_sucursal, String id){
            response.setContentType("application/pdf");
            response.setHeader("Pragma", "no-cache");
            response.setHeader("Expires", "Mon, 01 Jan 2001 00:00:01 GMT");
            response.setHeader("Cache-Control", "no-store");
            response.setHeader("Cache-Control", "must-revalidate");
            response.setHeader("Cache-Control", "no-cache");



            Sucursal objSucursal = new Sucursal(_ip, _puerto, _db, _usuario, _clave);
            String frt_liq_razon_social = "23:92";
            String frt_liq_ci = "189:92";
            String frt_liq_fecha = "32:120";
            String frt_liq_direccion = "37:106";
            String frt_liq_detalle = "2:165";
            String frt_liq_subtotal = "209:315";
            String frt_liq_subtotal_0 = "209:343";
            String frt_liq_subtotal_2 = "209:357";
            String frt_liq_iva_2 = "209:370";
            String frt_liq_total = "209:383";
            int frt_liq_anch_cant = 34;
            int frt_liq_anch_descripcion = 125;
            int frt_liq_anch_v_u = 52;
            int frt_liq_anch_v_t = 47;

            try{
                ResultSet rsSucursal = objSucursal.getSucursal(String.valueOf(id_sucursal));
                if(rsSucursal.next()){
                    frt_liq_razon_social = rsSucursal.getString("frt_liq_razon_social")!=null ? rsSucursal.getString("frt_liq_razon_social") : "23:92";
                    frt_liq_ci = rsSucursal.getString("frt_liq_ci")!=null ? rsSucursal.getString("frt_liq_ci") : "189:92";
                    frt_liq_fecha = rsSucursal.getString("frt_liq_fecha")!=null ? rsSucursal.getString("frt_liq_fecha") : "32:120";
                    frt_liq_direccion = rsSucursal.getString("frt_liq_direccion")!=null ? rsSucursal.getString("frt_liq_direccion") : "37:106";
                    frt_liq_detalle = rsSucursal.getString("frt_liq_detalle")!=null ? rsSucursal.getString("frt_liq_detalle") : "2:165";
                    frt_liq_subtotal = rsSucursal.getString("frt_liq_subtotal")!=null ? rsSucursal.getString("frt_liq_subtotal") : "209:315";
                    frt_liq_subtotal_0 = rsSucursal.getString("frt_liq_iva_0")!=null ? rsSucursal.getString("frt_liq_iva_0") : "209:343";
                    frt_liq_subtotal_2 = rsSucursal.getString("frt_liq_subtotal_2")!=null ? rsSucursal.getString("frt_liq_subtotal_2") : "209:357";
                    frt_liq_iva_2 = rsSucursal.getString("frt_liq_iva_12")!=null ? rsSucursal.getString("frt_liq_iva_12") : "209:370";
                    frt_liq_total = rsSucursal.getString("frt_liq_total")!=null ? rsSucursal.getString("frt_liq_total") : "209:383";
                    frt_liq_anch_cant = rsSucursal.getString("frt_liq_anch_cant")!=null ? rsSucursal.getInt("frt_liq_anch_cant") : 34;
                    frt_liq_anch_descripcion = rsSucursal.getString("frt_liq_anch_descripcion")!=null ? rsSucursal.getInt("frt_liq_anch_descripcion") : 125;
                    frt_liq_anch_v_u = rsSucursal.getString("frt_liq_anch_v_u")!=null ? rsSucursal.getInt("frt_liq_anch_v_u") : 52;
                    frt_liq_anch_v_t = rsSucursal.getString("frt_liq_anch_v_t")!=null ? rsSucursal.getInt("frt_liq_anch_v_t") : 47;
                    rsSucursal.close();
                }
            }catch(Exception e){
                e.printStackTrace();
            }

            String v_senores[] = frt_liq_razon_social.split(":");
            String v_ci[] = frt_liq_ci.split(":");
            String v_fech_emi[] = frt_liq_fecha.split(":");
            String v_direccion[] = frt_liq_direccion.split(":");
            String v_detalle[] = frt_liq_detalle.split(":");
            String v_subtotal[] = frt_liq_subtotal.split(":");
            String v_subtotal_0[] = frt_liq_subtotal_0.split(":");
            String v_subtotal_2[] = frt_liq_subtotal_2.split(":");
            String v_iva_2[] = frt_liq_iva_2.split(":");
            String v_total[] = frt_liq_total.split(":");


            String ci = "";
            String razon_social = "";
            String fecha_emision = "";
            String direccion = "";
            String subtotal = "";
            String subtotal_0 = "";
            String subtotal_2 = "";
            String iva_2 = "";
            String total = "";


            ResultSet rsLiquidacionCompra = null;
            ResultSet rsFacturaDetalle = null;
            LiquidacionCompra objLiquidacionCompra = new LiquidacionCompra(_ip, _puerto, _db, _usuario, _clave);

            if(id.compareTo("-2")==0){
                ci = "9999999999";
                razon_social = "PROVEEDOR";
                fecha_emision = "01/01/2010";
                direccion = "Calle Ibarra Nro. x-xx";
                subtotal = "100.00";
                subtotal_0 = "0.00";
                subtotal_2 = "0.00";
                iva_2 = "12.00";
                total = "112.00";
            }else if(Integer.valueOf(id)>0){
                    try{
                        rsLiquidacionCompra = objLiquidacionCompra.getLiquidacion(id);
                        rsFacturaDetalle = objLiquidacionCompra.getLiquidacionDetalle(id);
                        if(rsLiquidacionCompra.next()){
                            ci = ((rsLiquidacionCompra.getString("ruc")!=null) ? rsLiquidacionCompra.getString("ruc") : "");
                            razon_social = ((rsLiquidacionCompra.getString("razon_social")!=null) ? rsLiquidacionCompra.getString("razon_social") : "");
                            fecha_emision = ((rsLiquidacionCompra.getString("fecha_emision")!=null) ? Fecha.ISOaSQL(rsLiquidacionCompra.getString("fecha_emision")) : "");
                            direccion = ((rsLiquidacionCompra.getString("direccion")!=null) ? rsLiquidacionCompra.getString("direccion") : "");
                            subtotal = ((rsLiquidacionCompra.getString("subtotal")!=null) ? rsLiquidacionCompra.getString("subtotal") : "0.00");
                            subtotal_0 = ((rsLiquidacionCompra.getString("subtotal_0")!=null) ? rsLiquidacionCompra.getString("subtotal_0") : "0.00");
                            subtotal_2 = ((rsLiquidacionCompra.getString("subtotal_2")!=null) ? rsLiquidacionCompra.getString("subtotal_2") : "0.00");
                            iva_2 = ((rsLiquidacionCompra.getString("iva_2")!=null) ? rsLiquidacionCompra.getString("iva_2") : "0.00");
                            total = ((rsLiquidacionCompra.getString("total_compra")!=null) ? rsLiquidacionCompra.getString("total_compra") : "0.00");
                            rsLiquidacionCompra.close();
                        }
                    }catch(Exception ie){
                        ie.printStackTrace();
                    }
            }


            try {
                /* inicio PDF */
                Document document = new Document(PageSize.A4);// paso 1
                document.setMargins(0,0,0,0); /*Izquierda, derecha, tope, pie */

                float ancho = PageSize.A4.getWidth();
                float alto = PageSize.A4.getHeight();


                PdfWriter writer = PdfWriter.getInstance(document, response.getOutputStream()); // paso 2

                document.open(); // paso 3

                writer.addJavaScript("this.print(false);", false); // Para enviar a la impresora automÃ¡ticamente.

                /* todo el cuerpo del doc es el paso 4 */

                PdfPTable tbl_ci = new PdfPTable(1);
                tbl_ci.setTotalWidth(ancho);
                tbl_ci.addCell(Addons.setCeldaPDF(ci, Font.HELVETICA, 10, Font.NORMAL, Element.ALIGN_LEFT, 0));
                tbl_ci.writeSelectedRows(0, -1, Float.valueOf(v_ci[0]), alto-Float.valueOf(v_ci[1]), writer.getDirectContent());

                PdfPTable tbl_razon_social = new PdfPTable(1);
                tbl_razon_social.setTotalWidth(ancho);
                tbl_razon_social.addCell(Addons.setCeldaPDF(razon_social, Font.HELVETICA, 10, Font.NORMAL, Element.ALIGN_LEFT, 0));
                tbl_razon_social.writeSelectedRows(0, -1, Float.valueOf(v_senores[0]), alto-Float.valueOf(v_senores[1]), writer.getDirectContent());

                PdfPTable tbl_fecha_emision = new PdfPTable(1);
                tbl_fecha_emision.setTotalWidth(ancho);
                tbl_fecha_emision.addCell(Addons.setCeldaPDF(fecha_emision, Font.HELVETICA, 10, Font.NORMAL, Element.ALIGN_LEFT, 0));
                tbl_fecha_emision.writeSelectedRows(0, -1, Float.valueOf(v_fech_emi[0]), alto-Float.valueOf(v_fech_emi[1]), writer.getDirectContent());

                PdfPTable tbl_direccion = new PdfPTable(1);
                tbl_direccion.setTotalWidth(ancho);
                tbl_direccion.addCell(Addons.setCeldaPDF(direccion, Font.HELVETICA, 10, Font.NORMAL, Element.ALIGN_LEFT, 0));
                tbl_direccion.writeSelectedRows(0, -1, Float.valueOf(v_direccion[0]), alto-Float.valueOf(v_direccion[1]), writer.getDirectContent());






                float anchos[] = {frt_liq_anch_cant, frt_liq_anch_descripcion, frt_liq_anch_v_u, frt_liq_anch_v_t};
                PdfPTable tbl_det = new PdfPTable(anchos);
                tbl_det.setTotalWidth(frt_liq_anch_cant + frt_liq_anch_descripcion + frt_liq_anch_v_u + frt_liq_anch_v_t);
                if(id.compareTo("-2")==0){
                    tbl_det.addCell(Addons.setCeldaPDF("2", Font.HELVETICA, 10, Font.NORMAL, Element.ALIGN_LEFT, 0));
                    tbl_det.addCell(Addons.setCeldaPDF("Producto A", Font.HELVETICA, 10, Font.NORMAL, Element.ALIGN_LEFT, 0));
                    tbl_det.addCell(Addons.setCeldaPDF("50", Font.HELVETICA, 10, Font.NORMAL, Element.ALIGN_RIGHT, 0));
                    tbl_det.addCell(Addons.setCeldaPDF("100", Font.HELVETICA, 10, Font.NORMAL, Element.ALIGN_RIGHT, 0));
                }else{
                    try{
                        while(rsFacturaDetalle.next()){
                            tbl_det.addCell(Addons.setCeldaPDF(((rsFacturaDetalle.getString("cantidad")!=null) ? rsFacturaDetalle.getString("cantidad") : ""), Font.HELVETICA, 10, Font.NORMAL, Element.ALIGN_LEFT, 0));
                            tbl_det.addCell(Addons.setCeldaPDF(((rsFacturaDetalle.getString("descripcion")!=null) ? rsFacturaDetalle.getString("descripcion") : ""), Font.HELVETICA, 10, Font.NORMAL, Element.ALIGN_LEFT, 0));
                            tbl_det.addCell(Addons.setCeldaPDF(((rsFacturaDetalle.getString("p_u")!=null) ? rsFacturaDetalle.getString("p_u") : ""), Font.HELVETICA, 10, Font.NORMAL, Element.ALIGN_RIGHT, 0));
                            tbl_det.addCell(Addons.setCeldaPDF(((rsFacturaDetalle.getString("p_st")!=null) ? rsFacturaDetalle.getString("p_st") : ""), Font.HELVETICA, 10, Font.NORMAL, Element.ALIGN_RIGHT, 0));
                        }
                    }catch(Exception e){
                        e.printStackTrace();
                    }
                }
                tbl_det.writeSelectedRows(0, -1, Float.valueOf(v_detalle[0]), alto-Float.valueOf(v_detalle[1]), writer.getDirectContent());




                PdfPTable tbl_subtotal = new PdfPTable(1);
                tbl_subtotal.setTotalWidth(60);
                tbl_subtotal.addCell(Addons.setCeldaPDF(subtotal, Font.HELVETICA, 10, Font.NORMAL, Element.ALIGN_RIGHT, 0));
                tbl_subtotal.writeSelectedRows(0, -1, Float.valueOf(v_subtotal[0]), alto-Float.valueOf(v_subtotal[1]), writer.getDirectContent());

                PdfPTable tbl_subtotal_0 = new PdfPTable(1);
                tbl_subtotal_0.setTotalWidth(60);
                tbl_subtotal_0.addCell(Addons.setCeldaPDF(subtotal_0, Font.HELVETICA, 10, Font.NORMAL, Element.ALIGN_RIGHT, 0));
                tbl_subtotal_0.writeSelectedRows(0, -1, Float.valueOf(v_subtotal_0[0]), alto-Float.valueOf(v_subtotal_0[1]), writer.getDirectContent());

                PdfPTable tbl_subtotal_2 = new PdfPTable(1);
                tbl_subtotal_2.setTotalWidth(60);
                tbl_subtotal_2.addCell(Addons.setCeldaPDF(subtotal_2, Font.HELVETICA, 10, Font.NORMAL, Element.ALIGN_RIGHT, 0));
                tbl_subtotal_2.writeSelectedRows(0, -1, Float.valueOf(v_subtotal_2[0]), alto-Float.valueOf(v_subtotal_2[1]), writer.getDirectContent());
                
                PdfPTable tbl_iva_2 = new PdfPTable(1);
                tbl_iva_2.setTotalWidth(60);
                tbl_iva_2.addCell(Addons.setCeldaPDF(iva_2, Font.HELVETICA, 10, Font.NORMAL, Element.ALIGN_RIGHT, 0));
                tbl_iva_2.writeSelectedRows(0, -1, Float.valueOf(v_iva_2[0]), alto-Float.valueOf(v_iva_2[1]), writer.getDirectContent());

                PdfPTable tbl_total = new PdfPTable(1);
                tbl_total.setTotalWidth(60);
                tbl_total.addCell(Addons.setCeldaPDF(total, Font.HELVETICA, 10, Font.NORMAL, Element.ALIGN_RIGHT, 0));
                tbl_total.writeSelectedRows(0, -1, Float.valueOf(v_total[0]), alto-Float.valueOf(v_total[1]), writer.getDirectContent());






                document.close(); // paso 5
                /* fin PDF */

            }catch(IllegalStateException ie){
                ie.printStackTrace();
            }catch(DocumentException e){
                e.printStackTrace();
            }catch(Exception e){
                e.printStackTrace();
            }finally{
                try{
                    rsLiquidacionCompra.close();
                    rsFacturaDetalle.close();
                }catch(SQLException e){
                    e.printStackTrace();
                }
                objSucursal.cerrar();
                objLiquidacionCompra.cerrar();
            }
        }

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
        this._usuario = (String)sesion.getAttribute("usuario");
        this._clave = (String)sesion.getAttribute("clave");

        //PrintWriter out = response.getWriter();

        String id = request.getParameter("id");

        Factura objFactura = new Factura();
        objFactura.imprimir(response, id_sucursal, id);
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
