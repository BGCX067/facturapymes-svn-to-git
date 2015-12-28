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

package jm.fac.pdf;

import java.io.*;

import javax.servlet.*;
import javax.servlet.http.*;

import com.lowagie.text.pdf.*;
import com.lowagie.text.*;

import jm.web.Addons;
import jm.fac.clas.FacturaVenta;
import java.sql.ResultSet;
import java.sql.SQLException;
import jm.adm.clas.PuntoEmision;
import jm.web.Fecha;

/**
 *
 * @author Jorge
 */
public class pdfFacturaVenta extends HttpServlet {
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

        public void imprimir(int id_punto_emision, HttpServletResponse response, String id){
            response.setContentType("application/pdf");
            response.setHeader("Pragma", "no-cache");
            response.setHeader("Expires", "Mon, 01 Jan 2001 00:00:01 GMT");
            response.setHeader("Cache-Control", "no-store");
            response.setHeader("Cache-Control", "must-revalidate");
            response.setHeader("Cache-Control", "no-cache");

            String frt_fac_ruc = "189:92";
            String frt_fac_razon_social = "23:92";
            String frt_fac_fecha = "32:120";
            String frt_fac_direccion = "37:106";
            String frt_fac_telefono = "207:106";
            String frt_fac_detalle = "2:165";
            float frt_fac_anch_cant = 34;
            float frt_fac_anch_descripcion = 125;
            float frt_fac_anch_v_u = 52;
            float frt_fac_anch_v_t = 47;
            String frt_fac_subtotal = "209:315";
            String frt_fac_descuento = "209:329";
            String frt_fac_subtotal_2 = "20:20";
            String frt_fac_subtotal_0 = "209:343";
            String frt_fac_iva_2 = "209:357";
            String frt_fac_total = "209:370";
            PuntoEmision objPuntoEmision = new PuntoEmision(_ip, _puerto, _db, _usuario, _clave);
            try{
                ResultSet rs = objPuntoEmision.getPuntoEmision(String.valueOf(id_punto_emision));
                if(rs.next()){
                    frt_fac_ruc = (rs.getString("frt_fac_ruc")!=null) ? rs.getString("frt_fac_ruc") : "189:92";
                    frt_fac_razon_social = (rs.getString("frt_fac_razon_social")!=null) ? rs.getString("frt_fac_razon_social") : "23:92";
                    frt_fac_fecha = (rs.getString("frt_fac_fecha")!=null) ? rs.getString("frt_fac_fecha") : "32:120";
                    frt_fac_direccion = (rs.getString("frt_fac_direccion")!=null) ? rs.getString("frt_fac_direccion") : "37:106";
                    frt_fac_telefono = (rs.getString("frt_fac_telefono")!=null) ? rs.getString("frt_fac_telefono") : "207:106";
                    frt_fac_detalle = (rs.getString("frt_fac_detalle")!=null) ? rs.getString("frt_fac_detalle") : "2:165";
                    frt_fac_anch_cant = (rs.getString("frt_fac_anch_cant")!=null) ? rs.getFloat("frt_fac_anch_cant") : 34;
                    frt_fac_anch_descripcion = (rs.getString("frt_fac_anch_descripcion")!=null) ? rs.getFloat("frt_fac_anch_descripcion") : 125;
                    frt_fac_anch_v_u = (rs.getString("frt_fac_anch_v_u")!=null) ? rs.getFloat("frt_fac_anch_v_u") : 52;
                    frt_fac_anch_v_t = (rs.getString("frt_fac_anch_v_t")!=null) ? rs.getFloat("frt_fac_anch_v_t") : 47;
                    frt_fac_subtotal = (rs.getString("frt_fac_subtotal")!=null) ? rs.getString("frt_fac_subtotal") : "209:315";
                    frt_fac_descuento = (rs.getString("frt_fac_descuento")!=null) ? rs.getString("frt_fac_descuento") : "209:329";
                    frt_fac_subtotal_2 = (rs.getString("frt_fac_subtotal_2")!=null) ? rs.getString("frt_fac_subtotal_2") : "20:20";
                    frt_fac_subtotal_0 = (rs.getString("frt_fac_subtotal_0")!=null) ? rs.getString("frt_fac_subtotal_0") : "209:343";
                    frt_fac_iva_2 = (rs.getString("frt_fac_iva_2")!=null) ? rs.getString("frt_fac_iva_2") : "209:357";
                    frt_fac_total = (rs.getString("frt_fac_total")!=null) ? rs.getString("frt_fac_total") : "209:370";
                    rs.close();
                }
            }catch(Exception e){
                e.printStackTrace();
            }finally{
                objPuntoEmision.cerrar();
            }

            String v_ruc[] = frt_fac_ruc.split(":");
            String v_senores[] = frt_fac_razon_social.split(":");
            String v_fech_emi[] = frt_fac_fecha.split(":");
            String v_direccion[] = frt_fac_direccion.split(":");
            String v_telefono[] = frt_fac_telefono.split(":");
            String v_detalle[] = frt_fac_detalle.split(":");
            String v_subtotal[] = frt_fac_subtotal.split(":");
            String v_descuento[] = frt_fac_descuento.split(":");
            String v_subtotal2[] = frt_fac_subtotal_2.split(":");
            String v_subtotal0[] = frt_fac_subtotal_0.split(":");
            String v_iva2[] = frt_fac_iva_2.split(":");
            String v_total[] = frt_fac_total.split(":");



            String id_sucursal = "";
            String id_instalacion = "";
            String ruc = "";
            String razon_social = "";
            String fecha_emision = "";
            String direccion = "";
            String telefono = "";
            String movil_claro = "";
            String movil_movistar = "";
            String subtotal = "";
            String descuento = "";
            String subtotal_2 = "";
            String subtotal_0 = "";
            String iva_2 = "";
            String total = "";
            boolean de_activo = false;

            ResultSet rsFacturaVenta = null;
            ResultSet rsFacturaDetalle = null;
            FacturaVenta objFacturaVenta = new FacturaVenta(_ip, _puerto, _db, _usuario, _clave);

            if(id.compareTo("-2")==0){
                ruc = "9999999999999";
                razon_social = "CONSUMIDOR FINAL";
                fecha_emision = "01/01/2010";
                direccion = "Calle Ibarra Nro. x-xx";
                telefono = "062999999";
                movil_movistar = "062999999";
                subtotal = "100.00";
                descuento = "0.00";
                subtotal_2 = "0.00";
                subtotal_0 = "0.00";
                iva_2 = "12.00";
                total = "112.00";
            }else if(Integer.valueOf(id)>0){
                    try{
                        rsFacturaVenta = objFacturaVenta.getFactura(id);
                        if(rsFacturaVenta.next()){
                            id_sucursal = ((rsFacturaVenta.getString("id_sucursal")!=null) ? rsFacturaVenta.getString("id_sucursal") : "");
                            id_instalacion = ((rsFacturaVenta.getString("id_instalacion")!=null) ? rsFacturaVenta.getString("id_instalacion") : "");
                            ruc = ((rsFacturaVenta.getString("ruc")!=null) ? rsFacturaVenta.getString("ruc") : "9999999999999");
                            razon_social = ((rsFacturaVenta.getString("razon_social")!=null) ? rsFacturaVenta.getString("razon_social") : "CONSUMIDOR FINAL");
                            fecha_emision = ((rsFacturaVenta.getString("fecha_emision")!=null) ? Fecha.ISOaSQL(rsFacturaVenta.getString("fecha_emision")) : "");
                            direccion = ((rsFacturaVenta.getString("direccion")!=null) ? rsFacturaVenta.getString("direccion") : "");
                            telefono = ((rsFacturaVenta.getString("telefono")!=null) ? rsFacturaVenta.getString("telefono") : "");
                            movil_claro = ((rsFacturaVenta.getString("movil_claro")!=null) ? rsFacturaVenta.getString("movil_claro") : "");
                            movil_movistar = ((rsFacturaVenta.getString("movil_movistar")!=null) ? rsFacturaVenta.getString("movil_movistar") : "");
                            subtotal = ((rsFacturaVenta.getString("subtotal")!=null) ? rsFacturaVenta.getString("subtotal") : "0.00");
                            descuento = ((rsFacturaVenta.getString("descuento")!=null) ? rsFacturaVenta.getString("descuento") : "0.00");
                            subtotal_2 = ((rsFacturaVenta.getString("subtotal_2")!=null) ? rsFacturaVenta.getString("subtotal_2") : "0.00");
                            subtotal_0 = ((rsFacturaVenta.getString("subtotal_0")!=null) ? rsFacturaVenta.getString("subtotal_0") : "0.00");
                            iva_2 = ((rsFacturaVenta.getString("iva_2")!=null) ? rsFacturaVenta.getString("iva_2") : "0.00");
                            total = ((rsFacturaVenta.getString("total")!=null) ? rsFacturaVenta.getString("total") : "0.00");
                            de_activo = ((rsFacturaVenta.getString("de_activo")!=null) ? rsFacturaVenta.getBoolean("de_activo") : false);
                            rsFacturaVenta.close();
                        }
                    }catch(Exception ie){
                        ie.printStackTrace();
                    }
            }

            telefono = telefono.compareTo("")!=0 ? telefono : (movil_claro.compareTo("")!=0 ? movil_claro : movil_movistar);

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

                PdfPTable tbl_ruc = new PdfPTable(1);
                tbl_ruc.setTotalWidth(ancho);
                tbl_ruc.addCell(Addons.setCeldaPDF(ruc, Font.HELVETICA, 10, Font.NORMAL, Element.ALIGN_LEFT, 0));
                tbl_ruc.writeSelectedRows(0, -1, Float.valueOf(v_ruc[0]), alto-Float.valueOf(v_ruc[1]), writer.getDirectContent());

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

                PdfPTable tbl_telefono = new PdfPTable(1);
                tbl_telefono.setTotalWidth(ancho);
                tbl_telefono.addCell(Addons.setCeldaPDF(telefono, Font.HELVETICA, 10, Font.NORMAL, Element.ALIGN_LEFT, 0));
                tbl_telefono.writeSelectedRows(0, -1, Float.valueOf(v_telefono[0]), alto-Float.valueOf(v_telefono[1]), writer.getDirectContent());




            

                float anchos[] = {frt_fac_anch_cant, frt_fac_anch_descripcion, frt_fac_anch_v_u, frt_fac_anch_v_t};
                PdfPTable tbl_det = new PdfPTable(anchos);
                tbl_det.setTotalWidth(frt_fac_anch_cant + frt_fac_anch_descripcion + frt_fac_anch_v_u + frt_fac_anch_v_t);
                if(id.compareTo("-2")==0){
                    tbl_det.addCell(Addons.setCeldaPDF("2", Font.HELVETICA, 10, Font.NORMAL, Element.ALIGN_LEFT, 0));
                    tbl_det.addCell(Addons.setCeldaPDF("Producto A", Font.HELVETICA, 10, Font.NORMAL, Element.ALIGN_LEFT, 0));
                    tbl_det.addCell(Addons.setCeldaPDF("50", Font.HELVETICA, 10, Font.NORMAL, Element.ALIGN_RIGHT, 0));
                    tbl_det.addCell(Addons.setCeldaPDF("100", Font.HELVETICA, 10, Font.NORMAL, Element.ALIGN_RIGHT, 0));
                }else{
                    try{
                        if(de_activo){
                            rsFacturaDetalle = objFacturaVenta.getFacturaDetalleActivo(id);
                        }else{
                            rsFacturaDetalle = objFacturaVenta.getFacturaDetalle(id);
                        }
                        while(rsFacturaDetalle.next()){
                            tbl_det.addCell(Addons.setCeldaPDF(((rsFacturaDetalle.getString("cantidad")!=null) ? rsFacturaDetalle.getString("cantidad") : ""), Font.HELVETICA, 10, Font.NORMAL, Element.ALIGN_LEFT, 0));
                            tbl_det.addCell(Addons.setCeldaPDF(((rsFacturaDetalle.getString("descripcion_mas")!=null) ? rsFacturaDetalle.getString("descripcion_mas") : ""), Font.HELVETICA, 10, Font.NORMAL, Element.ALIGN_LEFT, 0));
                            tbl_det.addCell(Addons.setCeldaPDF(((rsFacturaDetalle.getString("p_u")!=null) ? rsFacturaDetalle.getString("p_u") : ""), Font.HELVETICA, 10, Font.NORMAL, Element.ALIGN_RIGHT, 0));
                            tbl_det.addCell(Addons.setCeldaPDF(((rsFacturaDetalle.getString("p_st")!=null) ? rsFacturaDetalle.getString("p_st") : ""), Font.HELVETICA, 10, Font.NORMAL, Element.ALIGN_RIGHT, 0));
                        }
                        if(id_instalacion.compareTo("")!=0){
                            String pagos = "\n\n\n\n"
                                    + "PAGOS EN BANCO PICHINCHA Y MI VECINO, SU CODIGO ES: " + id_sucursal + id_instalacion+"\n"
                                    + "CODIGO DE LA EMPRESA: 22686\n" 
                                    + "PARA EVITAR CORTES DEL SERVICIO, LOS PAGOS LOS PUEDE REALIZAR DESDE EL 22 DE CADA MES HASTA EL DIA 4 deL SIGUIENTE MES";
                            
                            tbl_det.addCell(Addons.setCeldaPDF(" ", Font.HELVETICA, 10, Font.NORMAL, Element.ALIGN_LEFT, 0));
                            tbl_det.addCell(Addons.setCeldaPDF(pagos, Font.HELVETICA, 10, Font.NORMAL, Element.ALIGN_LEFT, 0));
                            tbl_det.addCell(Addons.setCeldaPDF(" ", Font.HELVETICA, 10, Font.NORMAL, Element.ALIGN_RIGHT, 0));
                            tbl_det.addCell(Addons.setCeldaPDF(" ", Font.HELVETICA, 10, Font.NORMAL, Element.ALIGN_RIGHT, 0));
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

                PdfPTable tbl_descuento = new PdfPTable(1);
                tbl_descuento.setTotalWidth(60);
                tbl_descuento.addCell(Addons.setCeldaPDF(descuento, Font.HELVETICA, 10, Font.NORMAL, Element.ALIGN_RIGHT, 0));
                tbl_descuento.writeSelectedRows(0, -1, Float.valueOf(v_descuento[0]), alto-Float.valueOf(v_descuento[1]), writer.getDirectContent());

                PdfPTable tbl_subtotal_2 = new PdfPTable(1);
                tbl_subtotal_2.setTotalWidth(60);
                tbl_subtotal_2.addCell(Addons.setCeldaPDF(subtotal_2, Font.HELVETICA, 10, Font.NORMAL, Element.ALIGN_RIGHT, 0));
                tbl_subtotal_2.writeSelectedRows(0, -1, Float.valueOf(v_subtotal2[0]), alto-Float.valueOf(v_subtotal2[1]), writer.getDirectContent());
                
                PdfPTable tbl_subtotal_0 = new PdfPTable(1);
                tbl_subtotal_0.setTotalWidth(60);
                tbl_subtotal_0.addCell(Addons.setCeldaPDF(subtotal_0, Font.HELVETICA, 10, Font.NORMAL, Element.ALIGN_RIGHT, 0));
                tbl_subtotal_0.writeSelectedRows(0, -1, Float.valueOf(v_subtotal0[0]), alto-Float.valueOf(v_subtotal0[1]), writer.getDirectContent());

                PdfPTable tbl_iva_2 = new PdfPTable(1);
                tbl_iva_2.setTotalWidth(60);
                tbl_iva_2.addCell(Addons.setCeldaPDF(iva_2, Font.HELVETICA, 10, Font.NORMAL, Element.ALIGN_RIGHT, 0));
                tbl_iva_2.writeSelectedRows(0, -1, Float.valueOf(v_iva2[0]), alto-Float.valueOf(v_iva2[1]), writer.getDirectContent());

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
                    rsFacturaVenta.close();
                    rsFacturaDetalle.close();
                }catch(SQLException e){
                    e.printStackTrace();
                }
                objFacturaVenta.cerrar();
            }
        }

    }

    class ReciboCaja{
        ReciboCaja(){}
        
        public void imprimir(int id_punto_emision, HttpServletResponse response, String id){
            response.setContentType("application/pdf");
            response.setHeader("Pragma", "no-cache");
            response.setHeader("Expires", "Mon, 01 Jan 2001 00:00:01 GMT");
            response.setHeader("Cache-Control", "no-store");
            response.setHeader("Cache-Control", "must-revalidate");
            response.setHeader("Cache-Control", "no-cache");

            String id_sucursal = "";
            String id_instalacion = "";
            String serie_factura = "";
            String num_factura = "";
            String razon_social = "";
            String fecha_emision = "";
            String direccion = "";
            String vendedor = "";
            String total = "";
            boolean de_activo = false;

            ResultSet rsFacturaVenta = null;
            ResultSet rsFacturaDetalle = null;
            FacturaVenta objFacturaVenta = new FacturaVenta(_ip, _puerto, _db, _usuario, _clave);

            try{
                rsFacturaVenta = objFacturaVenta.getFactura(id);
                if(rsFacturaVenta.next()){
                    id_sucursal = ((rsFacturaVenta.getString("id_sucursal")!=null) ? rsFacturaVenta.getString("id_sucursal") : "");
                    id_instalacion = ((rsFacturaVenta.getString("id_instalacion")!=null) ? rsFacturaVenta.getString("id_instalacion") : "");
                    serie_factura = ((rsFacturaVenta.getString("serie_factura")!=null) ? rsFacturaVenta.getString("serie_factura") : "");
                    num_factura = ((rsFacturaVenta.getString("num_factura")!=null) ? rsFacturaVenta.getString("num_factura") : "");
                    razon_social = ((rsFacturaVenta.getString("razon_social")!=null) ? rsFacturaVenta.getString("razon_social") : "CONSUMIDOR FINAL");
                    fecha_emision = ((rsFacturaVenta.getString("fecha_emision")!=null) ? Fecha.ISOaSQL(rsFacturaVenta.getString("fecha_emision")) : "");
                    direccion = ((rsFacturaVenta.getString("direccion")!=null) ? rsFacturaVenta.getString("direccion") : "");
                    vendedor = ((rsFacturaVenta.getString("vendedor")!=null) ? rsFacturaVenta.getString("vendedor") : "");
                    total = ((rsFacturaVenta.getString("total")!=null) ? rsFacturaVenta.getString("total") : "0.00");
                    de_activo = ((rsFacturaVenta.getString("de_activo")!=null) ? rsFacturaVenta.getBoolean("de_activo") : false);
                    rsFacturaVenta.close();
                }
            }catch(Exception ie){
                ie.printStackTrace();
            }


            try {
                /* inicio PDF */
                Document document = new Document(PageSize.A4);// paso 1
                document.setMargins(0,0,0,0); /*Izquierda, derecha, tope, pie */


                PdfWriter writer = PdfWriter.getInstance(document, response.getOutputStream()); // paso 2

                document.open(); // paso 3

                writer.addJavaScript("this.print(false);", false); // Para enviar a la impresora automáticamente.

                /* todo el cuerpo del doc es el paso 4 */

                PdfPTable tbl = new PdfPTable(1);
                tbl.addCell(Addons.setCeldaPDF("SOLUCIONES AVANZADAS INFORMATICAS Y TELECOMUNICACIONES SAITEL", Font.HELVETICA, 10, Font.NORMAL, Element.ALIGN_CENTER, 0));
                tbl.addCell(Addons.setCeldaPDF("\nFactura #: " + serie_factura + "-" + num_factura, Font.HELVETICA, 10, Font.NORMAL, Element.ALIGN_LEFT, 0));
                tbl.addCell(Addons.setCeldaPDF("Cliente: "+razon_social, Font.HELVETICA, 10, Font.NORMAL, Element.ALIGN_LEFT, 0));
                //tbl.addCell(Addons.setCeldaPDF("Dirección: "+direccion, Font.HELVETICA, 10, Font.NORMAL, Element.ALIGN_LEFT, 0));
                tbl.addCell(Addons.setCeldaPDF("Fecha pago: "+fecha_emision, Font.HELVETICA, 10, Font.NORMAL, Element.ALIGN_LEFT, 0));
                
                try{
                    if(de_activo){
                        rsFacturaDetalle = objFacturaVenta.getFacturaDetalleActivo(id);
                    }else{
                        rsFacturaDetalle = objFacturaVenta.getFacturaDetalle(id);
                    }
                    while(rsFacturaDetalle.next()){
                        tbl.addCell(Addons.setCeldaPDF(((rsFacturaDetalle.getString("descripcion_mas")!=null) ? rsFacturaDetalle.getString("descripcion_mas") : ""), Font.HELVETICA, 10, Font.NORMAL, Element.ALIGN_LEFT, 0));
                    }
                }catch(Exception e){
                    e.printStackTrace();
                }
                
                tbl.addCell(Addons.setCeldaPDF("Valor pago: "+total, Font.HELVETICA, 10, Font.NORMAL, Element.ALIGN_LEFT, 0));
                tbl.addCell(Addons.setCeldaPDF("Cajero(a): "+vendedor, Font.HELVETICA, 10, Font.NORMAL, Element.ALIGN_LEFT, 0));

                if(id_instalacion.compareTo("")!=0){
                    tbl.addCell(Addons.setCeldaPDF("\nPAGOS EN BANCO PICHINCHA Y MI VECINO, SU CODIGO ES: " + id_sucursal + id_instalacion+"\n"
                    + "CODIGO DE LA EMPRESA: 22686\n" 
                    + "PARA EVITAR CORTES DEL SERVICIO, LOS PAGOS LOS PUEDE REALIZAR DESDE EL 22 DE CADA MES HASTA EL DIA 4 deL SIGUIENTE MES", Font.HELVETICA, 10, Font.NORMAL, Element.ALIGN_LEFT, 0));
                }
                
                tbl.addCell(Addons.setCeldaPDF("\nPARA REVISAR E IMPRIMIR SU FACTURA DIRIJASE A LA SIGUIENTE DIRECCION WEB: http://saitel.ec/pag/electronico.html", Font.HELVETICA, 10, Font.NORMAL, Element.ALIGN_LEFT, 0));
                
                document.add(tbl);
                
                
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
                    rsFacturaVenta.close();
                    rsFacturaDetalle.close();
                }catch(SQLException e){
                    e.printStackTrace();
                }
                objFacturaVenta.cerrar();
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
        int id_punto_emision = (Integer)sesion.getAttribute("id_punto_emision");
        this._usuario = (String)sesion.getAttribute("usuario");
        this._clave = (String)sesion.getAttribute("clave");

        //PrintWriter out = response.getWriter();

        String id = request.getParameter("id");

        /*Factura objFactura = new Factura();
        objFactura.imprimir(id_punto_emision, response, id);*/
        
        ReciboCaja objReciboCaja = new ReciboCaja();
        objReciboCaja.imprimir(id_punto_emision, response, id);
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
