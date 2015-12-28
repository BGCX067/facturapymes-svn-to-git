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
import java.sql.ResultSet;
import java.sql.SQLException;
import jm.web.Fecha;
import jm.adm.clas.Sucursal;
import jm.fac.clas.NotaCreditoVenta;

/**
 *
 * @author Jorge
 */
public class pdfNotaCredito extends HttpServlet {
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

    class NotaCredito{
        public NotaCredito(){}

        public void imprimir(HttpServletResponse response, int id_sucursal, String id){
            response.setContentType("application/pdf");
            response.setHeader("Pragma", "no-cache");
            response.setHeader("Expires", "Mon, 01 Jan 2001 00:00:01 GMT");
            response.setHeader("Cache-Control", "no-store");
            response.setHeader("Cache-Control", "must-revalidate");
            response.setHeader("Cache-Control", "no-cache");


            String frt_not_cre_ruc = "189:92";
            String frt_not_cre_razon_social = "23:92";
            String frt_not_cre_fecha = "32:120";
            String frt_not_cre_modifica = "37:106";
            String frt_not_cre_concepto = "2:165";
            String frt_not_cre_valor = "209:315";
            String frt_not_cre_subtotal_0 = "209:343";
            String frt_not_cre_iva_2 = "209:357";
            String frt_not_cre_total = "209:370";
            int frt_not_cre_anch_concepto = 34;
            Sucursal objSucursal = new Sucursal(_ip, _puerto, _db, _usuario, _clave);
            try{
                ResultSet rsSucursal = objSucursal.getSucursal(String.valueOf(id_sucursal));
                if(rsSucursal.next()){
                    frt_not_cre_razon_social = rsSucursal.getString("frt_not_cre_razon_social")!=null ? rsSucursal.getString("frt_not_cre_razon_social") : "23:92";
                    frt_not_cre_ruc = rsSucursal.getString("frt_not_cre_ruc")!=null ? rsSucursal.getString("frt_not_cre_ruc") : "189:92";
                    frt_not_cre_fecha = rsSucursal.getString("frt_not_cre_fecha")!=null ? rsSucursal.getString("frt_not_cre_fecha") : "32:120";
                    frt_not_cre_modifica = rsSucursal.getString("frt_not_cre_modifica")!=null ? rsSucursal.getString("frt_not_cre_modifica") : "37:106";
                    frt_not_cre_concepto = rsSucursal.getString("frt_not_cre_concepto")!=null ? rsSucursal.getString("frt_not_cre_concepto") : "2:165";
                    frt_not_cre_valor = rsSucursal.getString("frt_not_cre_valor")!=null ? rsSucursal.getString("frt_not_cre_valor") : "209:315";
                    frt_not_cre_subtotal_0 = rsSucursal.getString("frt_not_cre_iva_0")!=null ? rsSucursal.getString("frt_not_cre_iva_0") : "209:343";
                    frt_not_cre_iva_2 = rsSucursal.getString("frt_not_cre_iva_12")!=null ? rsSucursal.getString("frt_not_cre_iva_12") : "209:357";
                    frt_not_cre_total = rsSucursal.getString("frt_not_cre_total")!=null ? rsSucursal.getString("frt_not_cre_total") : "209:370";
                    frt_not_cre_anch_concepto = rsSucursal.getString("frt_not_cre_anch_concepto")!=null ? rsSucursal.getInt("frt_not_cre_anch_concepto") : 34;
                    rsSucursal.close();
                }
            }catch(Exception e){
                e.printStackTrace();
            }finally{
                objSucursal.cerrar();
            }

            String v_senores[] = frt_not_cre_razon_social.split(":");
            String v_ci[] = frt_not_cre_ruc.split(":");
            String v_fech_emi[] = frt_not_cre_fecha.split(":");
            String v_modifica[] = frt_not_cre_modifica.split(":");
            String v_detalle[] = frt_not_cre_concepto.split(":");
            String v_valor[] = frt_not_cre_valor.split(":");
            String v_subtotal_0[] = frt_not_cre_subtotal_0.split(":");
            String v_iva_2[] = frt_not_cre_iva_2.split(":");
            String v_total[] = frt_not_cre_total.split(":");


            String ci = "";
            String razon_social = "";
            String fecha_emision = "";
            String modifica = "";
            String concepto = "";
            String valor = "";
            String subtotal_0 = "";
            String iva_2 = "";
            String total = "";


            ResultSet rsNotaCredito = null;
            ResultSet rsNotaCreditoDetalle = null;
            NotaCreditoVenta objNotaCredito = new NotaCreditoVenta(_ip, _puerto, _db, _usuario, _clave);

            if(id.compareTo("-2")==0){
                ci = "9999999999";
                razon_social = "RAZON SOCIAL";
                fecha_emision = "01/01/2010";
                modifica = "Factura No. 000-000-0";
                concepto = "RAZON DE LA MODIFICACION";
                valor = "100.00";
                subtotal_0 = "0.00";
                iva_2 = "12.00";
                total = "112.00";
            }else if(Integer.valueOf(id)>0){
                
                    try{
                        rsNotaCredito = objNotaCredito.getNotaCredito(id);
                        if(rsNotaCredito.next()){
                            ci = ((rsNotaCredito.getString("ruc")!=null) ? rsNotaCredito.getString("ruc") : "");
                            razon_social = ((rsNotaCredito.getString("razon_social")!=null) ? rsNotaCredito.getString("razon_social") : "");
                            fecha_emision = ((rsNotaCredito.getString("fecha_emision")!=null) ? Fecha.ISOaSQL(rsNotaCredito.getString("fecha_emision")) : "");
                            modifica = ((rsNotaCredito.getString("documento_modifica")!=null) ? rsNotaCredito.getString("documento_modifica") : "");
                            concepto = ((rsNotaCredito.getString("concepto")!=null) ? rsNotaCredito.getString("concepto") : "");
                            valor = ((rsNotaCredito.getString("valor")!=null) ? rsNotaCredito.getString("valor") : "0.00");
                            subtotal_0 = ((rsNotaCredito.getString("subtotal_0")!=null) ? rsNotaCredito.getString("subtotal_0") : "0.00");
                            iva_2 = ((rsNotaCredito.getString("iva_2")!=null) ? rsNotaCredito.getString("iva_2") : "0.00");
                            total = ((rsNotaCredito.getString("total")!=null) ? rsNotaCredito.getString("total") : "0.00");
                            rsNotaCredito.close();
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

                PdfPTable tbl_modifica = new PdfPTable(1);
                tbl_modifica.setTotalWidth(ancho);
                tbl_modifica.addCell(Addons.setCeldaPDF(modifica, Font.HELVETICA, 10, Font.NORMAL, Element.ALIGN_LEFT, 0));
                tbl_modifica.writeSelectedRows(0, -1, Float.valueOf(v_modifica[0]), alto-Float.valueOf(v_modifica[1]), writer.getDirectContent());






                float anchos[] = {frt_not_cre_anch_concepto};
                PdfPTable tbl_det = new PdfPTable(anchos);
                tbl_det.setTotalWidth(frt_not_cre_anch_concepto);
                if(id.compareTo("-2")==0){
                    tbl_det.addCell(Addons.setCeldaPDF("RAZON DE LA MODIFICACION", Font.HELVETICA, 10, Font.NORMAL, Element.ALIGN_LEFT, 0));
                }else{
                    tbl_det.addCell(Addons.setCeldaPDF(concepto, Font.HELVETICA, 10, Font.NORMAL, Element.ALIGN_LEFT, 0));
                }
                tbl_det.writeSelectedRows(0, -1, Float.valueOf(v_detalle[0]), alto-Float.valueOf(v_detalle[1]), writer.getDirectContent());




                PdfPTable tbl_valor = new PdfPTable(1);
                tbl_valor.setTotalWidth(60);
                tbl_valor.addCell(Addons.setCeldaPDF(valor, Font.HELVETICA, 10, Font.NORMAL, Element.ALIGN_RIGHT, 0));
                tbl_valor.writeSelectedRows(0, -1, Float.valueOf(v_valor[0]), alto-Float.valueOf(v_valor[1]), writer.getDirectContent());

                PdfPTable tbl_subtotal_0 = new PdfPTable(1);
                tbl_subtotal_0.setTotalWidth(60);
                tbl_subtotal_0.addCell(Addons.setCeldaPDF(subtotal_0, Font.HELVETICA, 10, Font.NORMAL, Element.ALIGN_RIGHT, 0));
                tbl_subtotal_0.writeSelectedRows(0, -1, Float.valueOf(v_subtotal_0[0]), alto-Float.valueOf(v_subtotal_0[1]), writer.getDirectContent());

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
                    rsNotaCredito.close();
                    rsNotaCreditoDetalle.close();
                }catch(SQLException e){
                    e.printStackTrace();
                }
                objNotaCredito.cerrar();
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

        NotaCredito objNotaCredito = new NotaCredito();
        objNotaCredito.imprimir(response, id_sucursal, id);
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
