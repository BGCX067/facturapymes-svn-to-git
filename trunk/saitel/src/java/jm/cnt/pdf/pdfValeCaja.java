/**
* @version 1.0
* @package FACTURAPYMES.
* @author Jorge Washington Mueses Cevallos.
* @copyright Copyright (C) 2010 por Jorge Mueses. Todos los derechos reservados.
* @license http://www.gnu.org/copyleft/gpl.html GNU/GPL.
* FACTURAPYMES! es un software de libre distribuciÃ³n, que puede ser
* copiado y distribuido bajo los tÃ©rminos de la Licencia PÃºblica
* General GNU, de acuerdo con la publicada por la Free Software
* Foundation, versiÃ³n 2 de la licencia o cualquier versiÃ³n posterior.
*/

package jm.cnt.pdf;

import java.io.*;

import javax.servlet.*;
import javax.servlet.http.*;

import com.lowagie.text.pdf.*;
import com.lowagie.text.*;

import jm.adm.clas.Configuracion;
import jm.web.Addons;
import jm.cnt.clas.ValeCaja;

import java.sql.ResultSet;
import java.sql.SQLException;
import jm.web.Fecha;

/**
 *
 * @author Jorge
 */
public class pdfValeCaja extends HttpServlet {
    private String _ip = null;
    private int _puerto = 5432;
    private String _db = null;
    private String _usuario = null;
    private String _clave = null;
    private String titulo = "";

    public void init(ServletConfig config) throws ServletException
    {
        this._ip = config.getServletContext().getInitParameter("_IP");
        this._puerto = Integer.parseInt(config.getServletContext().getInitParameter("_PUERTO"));
        this._db = config.getServletContext().getInitParameter("_DB");
    }

    class imValeCaja{
        public imValeCaja(){}

        public void imprimir(HttpServletResponse response, String id){
            response.setContentType("application/pdf");
            response.setHeader("Pragma", "no-cache");
            response.setHeader("Expires", "Mon, 01 Jan 2001 00:00:01 GMT");
            response.setHeader("Cache-Control", "no-store");
            response.setHeader("Cache-Control", "must-revalidate");
            response.setHeader("Cache-Control", "no-cache");

            ResultSet rsValeCaja = null;
            ResultSet rsValeCajaDetalle = null;
            ValeCaja objValeCaja = new ValeCaja(_ip, _puerto, _db, _usuario, _clave);
            try {
                rsValeCaja = objValeCaja.getValeCaja(id);


                /* inicio PDF */
                Document document = new Document(PageSize.A4);// paso 1
                document.setMargins(0,0,50,80); /*Izquierda, derecha, tope, pie */
                try{
                    PdfWriter writer = PdfWriter.getInstance(document, response.getOutputStream()); // paso 2

                    document.open(); // paso 3

                    if(rsValeCaja.next()){

                        writer.addJavaScript("this.print(false);", false); // Para enviar a la impresora automÃ¡ticamente.

                        /* todo el cuerpo del doc es el paso 4 */

                        PdfPTable tbl_cab = new PdfPTable(1);
                        tbl_cab.addCell(Addons.setCeldaPDF(titulo, Font.HELVETICA, 14, Font.NORMAL, Element.ALIGN_CENTER, 0));
                        tbl_cab.addCell(Addons.setCeldaPDF("VALE DE CAJA CHICA", Font.HELVETICA, 14, Font.NORMAL, Element.ALIGN_CENTER, 0));
                        tbl_cab.addCell(Addons.setCeldaPDF("No.   " + ((rsValeCaja.getString("id_sucursal")!=null) ? rsValeCaja.getString("id_sucursal") : "") +
                                "-" + ((rsValeCaja.getString("num_vale_caja")!=null) ? rsValeCaja.getString("num_vale_caja") : ""), Font.HELVETICA, 12, Font.NORMAL, Element.ALIGN_RIGHT, 0));
                        document.add(tbl_cab);

                        document.add(new Paragraph(" "));

                        PdfPTable tbl_det = new PdfPTable(new float[]{20f,80f});
                        tbl_det.addCell(Addons.setCeldaPDF("Importe: $ " + ((rsValeCaja.getString("importe")!=null) ? Fecha.ISOaSQL(rsValeCaja.getString("importe")) : ""), Font.HELVETICA, 10, Font.NORMAL, Element.ALIGN_RIGHT, 0, 3, 2));

                        tbl_det.addCell(Addons.setCeldaPDF("Fecha: ", Font.HELVETICA, 10, Font.NORMAL, Element.ALIGN_LEFT, 0));
                        tbl_det.addCell(Addons.setCeldaPDF(((rsValeCaja.getString("fecha")!=null) ? Fecha.ISOaSQL(rsValeCaja.getString("fecha")) : ""), Font.HELVETICA, 10, Font.NORMAL, Element.ALIGN_LEFT, 0));

                        tbl_det.addCell(Addons.setCeldaPDF("Custodio: ", Font.HELVETICA, 10, Font.NORMAL, Element.ALIGN_LEFT, 0));
                        tbl_det.addCell(Addons.setCeldaPDF(((rsValeCaja.getString("custodio")!=null) ? Fecha.ISOaSQL(rsValeCaja.getString("custodio")) : ""), Font.HELVETICA, 10, Font.NORMAL, Element.ALIGN_LEFT, 0));

                        tbl_det.addCell(Addons.setCeldaPDF("La suma de: ", Font.HELVETICA, 10, Font.NORMAL, Element.ALIGN_LEFT, 0));
                        tbl_det.addCell(Addons.setCeldaPDF(((rsValeCaja.getString("la_suma_de")!=null) ? Fecha.ISOaSQL(rsValeCaja.getString("la_suma_de")) : ""), Font.HELVETICA, 10, Font.NORMAL, Element.ALIGN_LEFT, 0));

                        tbl_det.addCell(Addons.setCeldaPDF("Sirvase entregar a: ", Font.HELVETICA, 10, Font.NORMAL, Element.ALIGN_LEFT, 0));
                        tbl_det.addCell(Addons.setCeldaPDF(((rsValeCaja.getString("solicitante")!=null) ? Fecha.ISOaSQL(rsValeCaja.getString("solicitante")) : ""), Font.HELVETICA, 10, Font.NORMAL, Element.ALIGN_LEFT, 0));

                        tbl_det.addCell(Addons.setCeldaPDF("Concepto: ", Font.HELVETICA, 10, Font.NORMAL, Element.ALIGN_LEFT, 0));
                        tbl_det.addCell(Addons.setCeldaPDF(((rsValeCaja.getString("concepto")!=null) ? rsValeCaja.getString("concepto") : ""), Font.HELVETICA, 10, Font.NORMAL, Element.ALIGN_JUSTIFIED, 0));
                        document.add(tbl_det);
                    }


                    document.add(new Paragraph(" "));
                    document.add(new Paragraph(" "));
                    document.add(new Paragraph(" "));


                    PdfPTable tbl_firmas = new PdfPTable(2);
                    tbl_firmas.addCell(Addons.setCeldaPDF("APROBADO POR", Font.HELVETICA, 10, Font.NORMAL, Element.ALIGN_CENTER, 0));
                    tbl_firmas.addCell(Addons.setCeldaPDF("RECIBIDO POR", Font.HELVETICA, 10, Font.NORMAL, Element.ALIGN_CENTER, 0));
                    document.add(tbl_firmas);


                    rsValeCaja.close();
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
                    rsValeCaja.close();
                    rsValeCajaDetalle.close();
                }catch(SQLException e){
                    e.printStackTrace();
                }
                objValeCaja.cerrar();
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
        Configuracion conf = new Configuracion(this._ip, this._puerto, this._db, this._usuario, this._clave);
        this.titulo = conf.getValor("razon_social");
        conf.cerrar();

        String id = request.getParameter("id");

        imValeCaja objImValeCaja = new imValeCaja();
        objImValeCaja.imprimir(response, id);
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
