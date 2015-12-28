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
import jm.inv.clas.FacturaCompra;
import java.sql.ResultSet;
import java.sql.SQLException;
import jm.adm.clas.Sucursal;
import jm.inv.clas.LiquidacionCompra;
import jm.inv.clas.NotaVenta;
import jm.web.Fecha;

/**
 *
 * @author Jorge
 */
public class pdfComprobanteRetencion extends HttpServlet {
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

    class Retencion{
        public Retencion(){}

        public void imprimir(int id_sucursal, HttpServletResponse response, String id, String doc){
            response.setContentType("application/pdf");
            response.setHeader("Pragma", "no-cache");
            response.setHeader("Expires", "Mon, 01 Jan 2001 00:00:01 GMT");
            response.setHeader("Cache-Control", "no-store");
            response.setHeader("Cache-Control", "must-revalidate");
            response.setHeader("Cache-Control", "no-cache");


            String frt_ret_razon_social = "88:138";
            String frt_ret_ruc = "87:156";
            String frt_ret_fecha = "407:138";
            String frt_ret_num_comp = "478:156";
            String frt_ret_direccion = "96:180";
            String frt_ret_tipo = "480:180";
            String frt_ret_detalle = "24:245";
            String frt_ret_total = "512:363";
            Sucursal objSucursal = new Sucursal(_ip, _puerto, _db, _usuario, _clave);
            try{
                ResultSet rs = objSucursal.getSucursal(String.valueOf(id_sucursal));
                if(rs.next()){
                    frt_ret_razon_social = (rs.getString("frt_ret_razon_social")!=null) ? rs.getString("frt_ret_razon_social") : "88:138";
                    frt_ret_ruc = (rs.getString("frt_ret_ruc")!=null) ? rs.getString("frt_ret_ruc") : "87:156";
                    frt_ret_fecha = (rs.getString("frt_ret_fecha")!=null) ? rs.getString("frt_ret_fecha") : "407:138";
                    frt_ret_num_comp = (rs.getString("frt_ret_num_comp")!=null) ? rs.getString("frt_ret_num_comp") : "478:156";
                    frt_ret_direccion = (rs.getString("frt_ret_direccion")!=null) ? rs.getString("frt_ret_direccion") : "96:180";
                    frt_ret_tipo = (rs.getString("frt_ret_tipo")!=null) ? rs.getString("frt_ret_tipo") : "480:180";
                    frt_ret_detalle = (rs.getString("frt_ret_detalle")!=null) ? rs.getString("frt_ret_detalle") : "24:245";
                    frt_ret_total = (rs.getString("frt_ret_total")!=null) ? rs.getString("frt_ret_total") : "512:363";
                    rs.close();
                }
            }catch(Exception e){
                e.printStackTrace();
            }finally{
                objSucursal.cerrar();
            }

            String v_senores[] = frt_ret_razon_social.split(":");
            String v_ruc[] = frt_ret_ruc.split(":");
            String v_fech_emi[] = frt_ret_fecha.split(":");
            String v_num_comp[] = frt_ret_num_comp.split(":");
            String v_direccion[] = frt_ret_direccion.split(":");
            String v_tipo[] = frt_ret_tipo.split(":");
            String v_detalle[] = frt_ret_detalle.split(":");
            String v_total[] = frt_ret_total.split(":");




            String razon_social = "";
            String fecha_emision_ret = "";
            String ruc = "";
            String direccion = "";
            String serie_factura = "";
            String num_factura = "";
            String ejercicio_fiscal = "";
            String impuesto_retenido = "";
            String documento = "f";

            ResultSet rsFacturaCompra = null;
            ResultSet rsFacturaRetencion = null;
            FacturaCompra objFacturaCompra = new FacturaCompra(_ip, _puerto, _db, _usuario, _clave);
            LiquidacionCompra objLiquidacionCompra = new LiquidacionCompra(_ip, _puerto, _db, _usuario, _clave);
            NotaVenta objNotaVenta = new NotaVenta(_ip, _puerto, _db, _usuario, _clave);

            if(id.compareTo("-2")==0){
                razon_social = "Usuario Final";
                fecha_emision_ret = "01/01/2010";
                ruc = "9999999999999";
                direccion = "Calle Ibarra Nro. x-xx";
                serie_factura = "001-001";
                num_factura = "000000";
                ejercicio_fiscal = "2010";
                impuesto_retenido = "16.40";
                documento = "f";
            }else if(Integer.valueOf(id)>0){
                    try{
                        if(doc.compareTo("f")==0){
                            rsFacturaCompra = objFacturaCompra.getFacturaRetencion(id);
                        }else if(doc.compareTo("l") == 0){
                                    rsFacturaCompra = objLiquidacionCompra.getLiquidacionRetencion(id);
                        }else if(doc.compareTo("v") == 0){
                                    rsFacturaCompra = objNotaVenta.getNotaVentaRetencion(id);
                            }
                        rsFacturaRetencion = objFacturaCompra.getRetencionDetalle(id);
                        if(rsFacturaCompra.next()){
                            razon_social = ((rsFacturaCompra.getString("razon_social")!=null) ? rsFacturaCompra.getString("razon_social") : "");
                            razon_social = (razon_social.length()>40)? razon_social.substring(0, 40)+"..." : razon_social;
                            fecha_emision_ret = ((rsFacturaCompra.getString("ret_fecha_emision")!=null) ? Fecha.ISOaSQL(rsFacturaCompra.getString("ret_fecha_emision")) : "");
                            ruc = ((rsFacturaCompra.getString("ruc")!=null) ? rsFacturaCompra.getString("ruc") : "");
                            direccion = ((rsFacturaCompra.getString("direccion")!=null) ? rsFacturaCompra.getString("direccion") : "");
                            direccion = (direccion.length()>37)? direccion.substring(0, 37)+"..." : direccion;
                            serie_factura = ((rsFacturaCompra.getString("serie_factura")!=null) ? rsFacturaCompra.getString("serie_factura") : "");
                            num_factura = ((rsFacturaCompra.getString("num_factura")!=null) ? rsFacturaCompra.getString("num_factura") : "");
                            ejercicio_fiscal = ((rsFacturaCompra.getString("ret_ejercicio_fiscal")!=null) ? rsFacturaCompra.getString("ret_ejercicio_fiscal") : "");
                            impuesto_retenido = ((rsFacturaCompra.getString("ret_impuesto_retenido")!=null) ? rsFacturaCompra.getString("ret_impuesto_retenido") : "");
                            documento = ((rsFacturaCompra.getString("documento")!=null) ? rsFacturaCompra.getString("documento") : "f");
                            rsFacturaCompra.close();
                        }
                    }catch(Exception ie){
                        ie.printStackTrace();
                    }
            }
            documento = documento.compareTo("f")==0 ? "Factura" : (documento.compareTo("l")==0 ? "Liquidación" : "Nota de Venta");

            try {
                /* inicio PDF */
                Document document = new Document(PageSize.A4);// paso 1
                document.setMargins(0,0,0,0); /*Izquierda, derecha, tope, pie */

                float ancho = PageSize.A4.getWidth();
                float alto = PageSize.A4.getHeight();


                PdfWriter writer = PdfWriter.getInstance(document, response.getOutputStream()); // paso 2

                document.open(); // paso 3

                writer.addJavaScript("this.print(false);", false); // Para enviar a la impresora automáticamente.

                /* todo el cuerpo del doc es el paso 4 */


                PdfPTable tbl_razon_social = new PdfPTable(1);
                tbl_razon_social.setTotalWidth(ancho);
                tbl_razon_social.addCell(Addons.setCeldaPDF(razon_social, Font.HELVETICA, 10, Font.NORMAL, Element.ALIGN_LEFT, 0));
                tbl_razon_social.writeSelectedRows(0, -1, Float.valueOf(v_senores[0]), alto-Float.valueOf(v_senores[1]), writer.getDirectContent());

                PdfPTable tbl_fecha_emision_ret = new PdfPTable(1);
                tbl_fecha_emision_ret.setTotalWidth(ancho);
                tbl_fecha_emision_ret.addCell(Addons.setCeldaPDF(fecha_emision_ret, Font.HELVETICA, 10, Font.NORMAL, Element.ALIGN_LEFT, 0));
                tbl_fecha_emision_ret.writeSelectedRows(0, -1, Float.valueOf(v_fech_emi[0]), alto-Float.valueOf(v_fech_emi[1]), writer.getDirectContent());

                PdfPTable tbl_ruc = new PdfPTable(1);
                tbl_ruc.setTotalWidth(ancho);
                tbl_ruc.addCell(Addons.setCeldaPDF(ruc, Font.HELVETICA, 10, Font.NORMAL, Element.ALIGN_LEFT, 0));
                tbl_ruc.writeSelectedRows(0, -1, Float.valueOf(v_ruc[0]), alto-Float.valueOf(v_ruc[1]), writer.getDirectContent());

                PdfPTable tbl_tipo = new PdfPTable(1);
                tbl_tipo.setTotalWidth(ancho);
                tbl_tipo.addCell(Addons.setCeldaPDF(documento, Font.HELVETICA, 10, Font.NORMAL, Element.ALIGN_LEFT, 0));
                tbl_tipo.writeSelectedRows(0, -1, Float.valueOf(v_tipo[0]), alto-Float.valueOf(v_tipo[1]), writer.getDirectContent());

                PdfPTable tbl_direccion = new PdfPTable(1);
                tbl_direccion.setTotalWidth(ancho);
                tbl_direccion.addCell(Addons.setCeldaPDF(direccion, Font.HELVETICA, 10, Font.NORMAL, Element.ALIGN_LEFT, 0));
                tbl_direccion.writeSelectedRows(0, -1, Float.valueOf(v_direccion[0]), alto-Float.valueOf(v_direccion[1]), writer.getDirectContent());

                PdfPTable tbl_num_comp = new PdfPTable(1);
                tbl_num_comp.setTotalWidth(ancho);
                tbl_num_comp.addCell(Addons.setCeldaPDF(serie_factura+"-"+num_factura, Font.HELVETICA, 10, Font.NORMAL, Element.ALIGN_LEFT, 0));
                tbl_num_comp.writeSelectedRows(0, -1, Float.valueOf(v_num_comp[0]), alto-Float.valueOf(v_num_comp[1]), writer.getDirectContent());



                PdfPTable tbl_det = new PdfPTable(6);
                tbl_det.setTotalWidth(476f);
                if(id.compareTo("-2")==0){
                    tbl_det.addCell(Addons.setCeldaPDF("2010", Font.HELVETICA, 10, Font.NORMAL, Element.ALIGN_CENTER, 0));
                    tbl_det.addCell(Addons.setCeldaPDF("100", Font.HELVETICA, 10, Font.NORMAL, Element.ALIGN_CENTER, 0));
                    tbl_det.addCell(Addons.setCeldaPDF("RENTA", Font.HELVETICA, 10, Font.NORMAL, Element.ALIGN_CENTER, 0));
                    tbl_det.addCell(Addons.setCeldaPDF("304", Font.HELVETICA, 10, Font.NORMAL, Element.ALIGN_CENTER, 0));
                    tbl_det.addCell(Addons.setCeldaPDF("8", Font.HELVETICA, 10, Font.NORMAL, Element.ALIGN_CENTER, 0));
                    tbl_det.addCell(Addons.setCeldaPDF("8.00", Font.HELVETICA, 10, Font.NORMAL, Element.ALIGN_RIGHT, 0));
                    tbl_det.addCell(Addons.setCeldaPDF("2010", Font.HELVETICA, 10, Font.NORMAL, Element.ALIGN_CENTER, 0));
                    tbl_det.addCell(Addons.setCeldaPDF("12", Font.HELVETICA, 10, Font.NORMAL, Element.ALIGN_CENTER, 0));
                    tbl_det.addCell(Addons.setCeldaPDF("IVA", Font.HELVETICA, 10, Font.NORMAL, Element.ALIGN_CENTER, 0));
                    tbl_det.addCell(Addons.setCeldaPDF("-", Font.HELVETICA, 10, Font.NORMAL, Element.ALIGN_CENTER, 0));
                    tbl_det.addCell(Addons.setCeldaPDF("70", Font.HELVETICA, 10, Font.NORMAL, Element.ALIGN_CENTER, 0));
                    tbl_det.addCell(Addons.setCeldaPDF("8.40", Font.HELVETICA, 10, Font.NORMAL, Element.ALIGN_RIGHT, 0));
                }else{
                    try{
                        while(rsFacturaRetencion.next()){
                            tbl_det.addCell(Addons.setCeldaPDF(ejercicio_fiscal, Font.HELVETICA, 10, Font.NORMAL, Element.ALIGN_LEFT, 0));
                            tbl_det.addCell(Addons.setCeldaPDF(((rsFacturaRetencion.getString("base_imponible")!=null) ? rsFacturaRetencion.getString("base_imponible") : ""), Font.HELVETICA, 10, Font.NORMAL, Element.ALIGN_CENTER, 0));
                            tbl_det.addCell(Addons.setCeldaPDF(((rsFacturaRetencion.getString("impuesto")!=null) ? rsFacturaRetencion.getString("impuesto") : ""), Font.HELVETICA, 10, Font.NORMAL, Element.ALIGN_CENTER, 0));
                            tbl_det.addCell(Addons.setCeldaPDF(((rsFacturaRetencion.getString("codigo_impuesto")!=null) ? rsFacturaRetencion.getString("codigo_impuesto") : ""), Font.HELVETICA, 10, Font.NORMAL, Element.ALIGN_CENTER, 0));
                            tbl_det.addCell(Addons.setCeldaPDF(((rsFacturaRetencion.getString("porcentaje_retencion")!=null) ? rsFacturaRetencion.getString("porcentaje_retencion") : ""), Font.HELVETICA, 10, Font.NORMAL, Element.ALIGN_CENTER, 0));
                            tbl_det.addCell(Addons.setCeldaPDF(((rsFacturaRetencion.getString("valor_retenido")!=null) ? rsFacturaRetencion.getString("valor_retenido") : ""), Font.HELVETICA, 10, Font.NORMAL, Element.ALIGN_RIGHT, 0));
                        }
                    }catch(Exception e){
                        e.printStackTrace();
                    }
                }
                tbl_det.writeSelectedRows(0, -1, Float.valueOf(v_detalle[0]), alto-Float.valueOf(v_detalle[1]), writer.getDirectContent());


                PdfPTable tbl_total = new PdfPTable(1);
                tbl_total.setTotalWidth(ancho);
                tbl_total.addCell(Addons.setCeldaPDF(impuesto_retenido, Font.HELVETICA, 10, Font.NORMAL, Element.ALIGN_LEFT, 0));
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
                    rsFacturaCompra.close();
                    rsFacturaRetencion.close();
                }catch(SQLException e){
                    e.printStackTrace();
                }
                objFacturaCompra.cerrar();
                objLiquidacionCompra.cerrar();
                objNotaVenta.cerrar();
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
        String doc = request.getParameter("doc");

        Retencion objRetencion = new Retencion();
        objRetencion.imprimir(id_sucursal, response, id, doc);
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
