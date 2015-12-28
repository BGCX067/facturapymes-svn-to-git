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

package jm.cnt.pdf;

import java.io.*;

import javax.servlet.*;
import javax.servlet.http.*;

import com.lowagie.text.pdf.*;
import com.lowagie.text.*;

import jm.web.Addons;
import jm.cnt.clas.ComprobanteEgreso;

import java.sql.ResultSet;
import java.sql.SQLException;
import jm.adm.clas.Configuracion;
import jm.web.Cadena;
import jm.web.Fecha;

/**
 *
 * @author Jorge
 */
public class pdfCheque extends HttpServlet {
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

    class Cheque{
        public Cheque(){}

        public void imprimir(HttpServletResponse response, String id){
            response.setContentType("application/pdf");
            response.setHeader("Pragma", "no-cache");
            response.setHeader("Expires", "Mon, 01 Jan 2001 00:00:01 GMT");
            response.setHeader("Cache-Control", "no-store");
            response.setHeader("Cache-Control", "must-revalidate");
            response.setHeader("Cache-Control", "no-cache");

            Configuracion conf = new Configuracion(_ip, _puerto, _db, _usuario, _clave);
            String v_orden_de[] = conf.getValor("chq_orden_de").split(":");
            String v_total[] = conf.getValor("chq_total").split(":");
            String v_son[] = conf.getValor("chq_son").split(":");
            String v_lugar_fecha[] = conf.getValor("chq_lugar_fecha").split(":");
            conf.cerrar();

            ResultSet rsComprobanteEgreso = null;
            ComprobanteEgreso objComprobanteEgreso = new ComprobanteEgreso(_ip, _puerto, _db, _usuario, _clave);
            try {
                
                String orden_de = "Orden de...";
                String son = "cien 00/100";
                String lugar = "Loja";
                String fecha = Fecha.getFecha("SQL");
                String valor_son = "100,00";
                try{
                    if(id.compareTo("-2")!=0){
                        rsComprobanteEgreso = objComprobanteEgreso.getComprobanteEgreso(id);
                        if(rsComprobanteEgreso.next()){
                            orden_de = ((rsComprobanteEgreso.getString("orden_de")!=null) ? rsComprobanteEgreso.getString("orden_de") : "");
                            son = ((rsComprobanteEgreso.getString("son")!=null) ? rsComprobanteEgreso.getString("son") : "");
                            lugar = ((rsComprobanteEgreso.getString("lugar")!=null) ? rsComprobanteEgreso.getString("lugar") : "");
                            fecha = ((rsComprobanteEgreso.getString("fecha_proceso")!=null) ? rsComprobanteEgreso.getString("fecha_proceso") : "");
                            valor_son = ((rsComprobanteEgreso.getString("valor_son")!=null) ? rsComprobanteEgreso.getString("valor_son") : "0");
                        }
                    }
                }catch(Exception e){
                    e.printStackTrace();
                }
                /* inicio PDF */
                Document document = new Document(PageSize.A5);// paso 1
                document.setMargins(0,0,0,0); /*Izquierda, derecha, tope, pie */
                
                float ancho = PageSize.A5.getWidth();
                float alto = PageSize.A5.getHeight();
                
                try{
                    PdfWriter writer = PdfWriter.getInstance(document, response.getOutputStream()); // paso 2
                    document.open(); // paso 3

                    writer.addJavaScript("this.print(false);", false); // Para enviar a la impresora automáticamente.

                    /* todo el cuerpo del doc es el paso 4 */

                    PdfPTable tbl_orden_de = new PdfPTable(1);
                    tbl_orden_de.setTotalWidth(ancho);
                    tbl_orden_de.addCell(Addons.setCeldaPDF( Cadena.html_decode(orden_de), Font.HELVETICA, 10, Font.NORMAL, Element.ALIGN_LEFT, 0));
                    tbl_orden_de.writeSelectedRows(0, -1, Float.valueOf(v_orden_de[0]), alto-Float.valueOf(v_orden_de[1]), writer.getDirectContent());

                    PdfPTable tbl_total = new PdfPTable(1);
                    tbl_total.setTotalWidth(ancho);
                    tbl_total.addCell(Addons.setCeldaPDF(valor_son, Font.HELVETICA, 10, Font.NORMAL, Element.ALIGN_LEFT, 0));
                    tbl_total.writeSelectedRows(0, -1, Float.valueOf(v_total[0]), alto-Float.valueOf(v_total[1]), writer.getDirectContent());

                    PdfPTable tbl_son = new PdfPTable(1);
                    tbl_son.setTotalWidth(ancho);
                    tbl_son.addCell(Addons.setCeldaPDF(son, Font.HELVETICA, 10, Font.NORMAL, Element.ALIGN_LEFT, 0));
                    tbl_son.writeSelectedRows(0, -1, Float.valueOf(v_son[0]), alto-Float.valueOf(v_son[1]), writer.getDirectContent());

                    PdfPTable tbl_lugar_fecha = new PdfPTable(1);
                    tbl_lugar_fecha.setTotalWidth(ancho);
                    tbl_lugar_fecha.addCell(Addons.setCeldaPDF(lugar + ",     " + fecha, Font.HELVETICA, 10, Font.NORMAL, Element.ALIGN_LEFT, 0));
                    tbl_lugar_fecha.writeSelectedRows(0, -1, Float.valueOf(v_lugar_fecha[0]), alto-Float.valueOf(v_lugar_fecha[1]), writer.getDirectContent());

                    /*PdfPTable tbl_cheque = new PdfPTable(new float[]{105f,80f});
                    tbl_cheque.setWidthPercentage(89);

                    tbl_cheque.addCell(Addons.setCeldaPDF(orden_de, Font.HELVETICA, 10, Font.NORMAL, Element.ALIGN_LEFT, 0));
                    tbl_cheque.addCell(Addons.setCeldaPDF(total, Font.HELVETICA, 10, Font.NORMAL, Element.ALIGN_LEFT, 0));

                    document.add(tbl_cheque);


                    document.add(new Paragraph("          " + son, new Font(Font.HELVETICA, 10, Font.NORMAL)));

                    document.add(new Paragraph(" "));

                    document.add(new Paragraph(lugar + ",     " + Addons.getTextFecha(fecha), new Font(Font.HELVETICA, 10, Font.NORMAL)));
                     */

                    rsComprobanteEgreso.close();
                }catch(IllegalStateException ie){
                    ie.printStackTrace();
                }catch(DocumentException e){
                    e.printStackTrace();
                }catch(Exception e){
                    e.printStackTrace();
                }


                document.close(); // paso 5
                /* fin PDF */

            } finally {
                try{
                    rsComprobanteEgreso.close();
                }catch(SQLException e){
                    e.printStackTrace();
                }
                objComprobanteEgreso.cerrar();
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
        this._usuario = (String)sesion.getAttribute("usuario");
        this._clave = (String)sesion.getAttribute("clave");
        
        //PrintWriter out = response.getWriter();

        String id = request.getParameter("id");

        Cheque objCheque = new Cheque();
        objCheque.imprimir(response, id);
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
