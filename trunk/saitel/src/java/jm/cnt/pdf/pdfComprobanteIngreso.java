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

import jm.adm.clas.Configuracion;
import jm.web.Addons;
import jm.cnt.clas.ComprobanteIngreso;

import java.sql.ResultSet;
import java.sql.SQLException;
import jm.web.Fecha;

/**
 *
 * @author Jorge
 */
public class pdfComprobanteIngreso extends HttpServlet {
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

    class imComprobanteIngreso{
        public imComprobanteIngreso(){}

        public void imprimir(HttpServletResponse response, String id){
            response.setContentType("application/pdf");
            response.setHeader("Pragma", "no-cache");
            response.setHeader("Expires", "Mon, 01 Jan 2001 00:00:01 GMT");
            response.setHeader("Cache-Control", "no-store");
            response.setHeader("Cache-Control", "must-revalidate");
            response.setHeader("Cache-Control", "no-cache");

            String [] cabTabla = null;
            float [] anchoTabla = null;
            ResultSet rsComprobanteIngreso = null;
            ResultSet rsComprobanteIngresoDetalle = null;
            ComprobanteIngreso objComprobanteIngreso = new ComprobanteIngreso(_ip, _puerto, _db, _usuario, _clave);
            try {
                rsComprobanteIngreso = objComprobanteIngreso.getComprobanteIngreso(id);
                rsComprobanteIngresoDetalle = objComprobanteIngreso.getComprobanteIngresoDetalle(id);

                cabTabla = new String [] {"CÓDIGO", "CUENTA", "DÉBITO ($)", "CRÉDITO ($)"};
                anchoTabla = new float[]{55f,145f,40f,40f};

                /* inicio PDF */
                Document document = new Document(PageSize.A4);// paso 1
                document.setMargins(0,0,50,80); /*Izquierda, derecha, tope, pie */
                try{
                    PdfWriter writer = PdfWriter.getInstance(document, response.getOutputStream()); // paso 2

                    document.open(); // paso 3

                    if(rsComprobanteIngreso.next()){

                        writer.addJavaScript("this.print(false);", false); // Para enviar a la impresora automáticamente.

                        /* todo el cuerpo del doc es el paso 4 */

                        PdfPTable tbl_cab = new PdfPTable(1);
                        tbl_cab.addCell(Addons.setCeldaPDF(titulo, Font.HELVETICA, 14, Font.NORMAL, Element.ALIGN_CENTER, 0));
                        tbl_cab.addCell(Addons.setCeldaPDF("COMPROBANTE DE INGRESO", Font.HELVETICA, 14, Font.NORMAL, Element.ALIGN_CENTER, 0));
                        tbl_cab.addCell(Addons.setCeldaPDF("No.   " + ((rsComprobanteIngreso.getString("num_comprobante")!=null) ? rsComprobanteIngreso.getString("num_comprobante") : ""), Font.HELVETICA, 12, Font.NORMAL, Element.ALIGN_RIGHT, 0));
                        document.add(tbl_cab);

                        PdfPTable tbl_fechas = new PdfPTable(2);
                        tbl_fechas.addCell(Addons.setCeldaPDF("FECHA ACTUAL: " + ((rsComprobanteIngreso.getString("fecha_actual")!=null) ? Fecha.ISOaSQL(rsComprobanteIngreso.getString("fecha_actual")) : ""), Font.HELVETICA, 10, Font.NORMAL, Element.ALIGN_LEFT, 0));
                        tbl_fechas.addCell(Addons.setCeldaPDF("FECHA DE PROCESO: " + ((rsComprobanteIngreso.getString("fecha_proceso")!=null) ? Fecha.ISOaSQL(rsComprobanteIngreso.getString("fecha_proceso")) : ""), Font.HELVETICA, 10, Font.NORMAL, Element.ALIGN_LEFT, 0));

                        tbl_fechas.addCell(Addons.setCeldaPDF("NOMBRE: " + ((rsComprobanteIngreso.getString("nombre")!=null) ? rsComprobanteIngreso.getString("nombre") : ""), Font.HELVETICA, 10, Font.NORMAL, Element.ALIGN_LEFT, 0, 2, 2));

                        String tipo_cobro = (rsComprobanteIngreso.getString("tipo_cobro")!=null) ? rsComprobanteIngreso.getString("tipo_cobro") : "";
                        if(tipo_cobro.compareTo("c")==0){
                            tbl_fechas.addCell(Addons.setCeldaPDF("CHEQUE #: " + ((rsComprobanteIngreso.getString("num_cheque")!=null) ? rsComprobanteIngreso.getString("num_cheque") : ""), Font.HELVETICA, 10, Font.NORMAL, Element.ALIGN_LEFT, 0));
                            tbl_fechas.addCell(Addons.setCeldaPDF("BANCO: " + ((rsComprobanteIngreso.getString("banco")!=null) ? rsComprobanteIngreso.getString("banco") : ""), Font.HELVETICA, 10, Font.NORMAL, Element.ALIGN_LEFT, 0));
                        }

                        document.add(tbl_fechas);

                        PdfPTable tbl_concepto = new PdfPTable(new float[]{30f,145f});
                        tbl_concepto.addCell(Addons.setCeldaPDF("CONCEPTO: ", Font.HELVETICA, 10, Font.NORMAL, Element.ALIGN_LEFT, 0));
                        tbl_concepto.addCell(Addons.setCeldaPDF(((rsComprobanteIngreso.getString("detalle")!=null) ? rsComprobanteIngreso.getString("detalle") : ""), Font.HELVETICA, 10, Font.NORMAL, Element.ALIGN_JUSTIFIED, 0));
                        document.add(tbl_concepto);


                        document.add(Addons.setCabeceraTabla(cabTabla, anchoTabla));

                        PdfPTable tbl_det = new PdfPTable(anchoTabla);
                        try{
                            String debe = "";
                            String haber = "";
                            while(rsComprobanteIngresoDetalle.next()){
                                debe = (rsComprobanteIngresoDetalle.getString("debe")!=null) ? ((rsComprobanteIngresoDetalle.getDouble("debe")>0)?rsComprobanteIngresoDetalle.getString("debe"):"") : "";
                                haber = (rsComprobanteIngresoDetalle.getString("haber")!=null) ? ((rsComprobanteIngresoDetalle.getDouble("haber")>0)?rsComprobanteIngresoDetalle.getString("haber"):"") : "";
                                tbl_det.addCell(Addons.setCeldaPDF(((rsComprobanteIngresoDetalle.getString("codigo_cuenta")!=null) ? rsComprobanteIngresoDetalle.getString("codigo_cuenta") : ""), Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_LEFT, 1));
                                tbl_det.addCell(Addons.setCeldaPDF(((rsComprobanteIngresoDetalle.getString("nombre_cuenta")!=null) ? rsComprobanteIngresoDetalle.getString("nombre_cuenta") : ""), Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_LEFT, 1));
                                tbl_det.addCell(Addons.setCeldaPDF(debe, Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_RIGHT, 1));
                                tbl_det.addCell(Addons.setCeldaPDF(haber, Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_RIGHT, 1));
                            }

                            tbl_det.addCell(Addons.setCeldaPDF("TOTALES:  ", Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_RIGHT, 1, 2, 2));
                            tbl_det.addCell(Addons.setCeldaPDF(((rsComprobanteIngreso.getString("total")!=null) ? rsComprobanteIngreso.getString("total") : "0"), Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_RIGHT, 1));
                            tbl_det.addCell(Addons.setCeldaPDF(((rsComprobanteIngreso.getString("total")!=null) ? rsComprobanteIngreso.getString("total") : "0"), Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_RIGHT, 1));

                            tbl_det.addCell(Addons.setCeldaPDF("SON:  " + ((rsComprobanteIngreso.getString("son")!=null) ? rsComprobanteIngreso.getString("son") : ""), Font.HELVETICA, 10, Font.NORMAL, Element.ALIGN_LEFT, 0, 2, 4));

                            rsComprobanteIngresoDetalle.close();
                        }catch(Exception e){
                            e.printStackTrace();
                        }
                        document.add(tbl_det);
                    }


                    document.add(new Paragraph(" "));
                    document.add(new Paragraph(" "));
                    document.add(new Paragraph(" "));


                    PdfPTable tbl_firmas = new PdfPTable(2);
                    tbl_firmas.addCell(Addons.setCeldaPDF("ELABORADO", Font.HELVETICA, 10, Font.NORMAL, Element.ALIGN_CENTER, 0));
                    tbl_firmas.addCell(Addons.setCeldaPDF("CONTABILIZADO", Font.HELVETICA, 10, Font.NORMAL, Element.ALIGN_CENTER, 0));
                    document.add(tbl_firmas);


                    rsComprobanteIngreso.close();
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
                    rsComprobanteIngreso.close();
                    rsComprobanteIngresoDetalle.close();
                }catch(SQLException e){
                    e.printStackTrace();
                }
                objComprobanteIngreso.cerrar();
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

        imComprobanteIngreso objImComprobanteIngreso = new imComprobanteIngreso();
        objImComprobanteIngreso.imprimir(response, id);
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
