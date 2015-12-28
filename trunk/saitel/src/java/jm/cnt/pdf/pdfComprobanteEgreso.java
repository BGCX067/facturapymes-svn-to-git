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
import jm.cnt.clas.ComprobanteEgreso;

import java.sql.ResultSet;
import java.sql.SQLException;
import jm.web.Cadena;
import jm.web.Fecha;

/**
 *
 * @author Jorge
 */
public class pdfComprobanteEgreso extends HttpServlet {
    private String _ip = null;
    private int _puerto = 5432;
    private String _db = null;
    private String _usuario = null;
    private String _clave = null;

    private String titulo = "";
    private String rep_nombre = "";
    private String rep_cargo = "";
    private String apr_nombre = "";
    private String apr_cargo = "";

    public void init(ServletConfig config) throws ServletException
    {
        this._ip = config.getServletContext().getInitParameter("_IP");
        this._puerto = Integer.parseInt(config.getServletContext().getInitParameter("_PUERTO"));
        this._db = config.getServletContext().getInitParameter("_DB");
    }

    class imComprobanteEgreso{
        public imComprobanteEgreso(){}

        public void imprimir(HttpServletResponse response, String id){
            response.setContentType("application/pdf");
            response.setHeader("Pragma", "no-cache");
            response.setHeader("Expires", "Mon, 01 Jan 2001 00:00:01 GMT");
            response.setHeader("Cache-Control", "no-store");
            response.setHeader("Cache-Control", "must-revalidate");
            response.setHeader("Cache-Control", "no-cache");

            String [] cabTabla = null;
            float [] anchoTabla = null;
            ResultSet rsComprobanteEgreso = null;
            ResultSet rsComprobanteEgresoDetalle = null;
            ComprobanteEgreso objComprobanteEgreso = new ComprobanteEgreso(_ip, _puerto, _db, _usuario, _clave);
            try {
                rsComprobanteEgreso = objComprobanteEgreso.getComprobanteEgreso(id);
                rsComprobanteEgresoDetalle = objComprobanteEgreso.getComprobanteEgresoDetalle(id);

                cabTabla = new String [] {"CÓDIGO", "CUENTA", "DÉBITO ($)", "CRÉDITO ($)"};
                anchoTabla = new float[]{55f,145f,40f,40f};

                /* inicio PDF */
                Document document = new Document(PageSize.A4);// paso 1
                document.setMargins(0,0,50,80); /*Izquierda, derecha, tope, pie */
                try{
                    PdfWriter writer = PdfWriter.getInstance(document, response.getOutputStream()); // paso 2

                    document.open(); // paso 3

                    if(rsComprobanteEgreso.next()){

                        writer.addJavaScript("this.print(false);", false); // Para enviar a la impresora automáticamente.

                        /* todo el cuerpo del doc es el paso 4 */

                        String banco = ((rsComprobanteEgreso.getString("banco")!=null) ? rsComprobanteEgreso.getString("banco") : "");
                        String num_cheque = ((rsComprobanteEgreso.getString("num_cheque")!=null) ? rsComprobanteEgreso.getString("num_cheque") : "");
                        String orden_de = ((rsComprobanteEgreso.getString("orden_de")!=null) ? rsComprobanteEgreso.getString("orden_de") : "");
                        String valor_son = ((rsComprobanteEgreso.getString("valor_son")!=null) ? rsComprobanteEgreso.getString("valor_son") : "");
                        String son = ((rsComprobanteEgreso.getString("son")!=null) ? rsComprobanteEgreso.getString("son") : "");
                        String lugar = ((rsComprobanteEgreso.getString("lugar")!=null) ? rsComprobanteEgreso.getString("lugar") : "");
                        String fecha = ((rsComprobanteEgreso.getString("fecha_proceso")!=null) ? Fecha.ISOaSQL(rsComprobanteEgreso.getString("fecha_proceso")) : "");
                        String total = ((rsComprobanteEgreso.getString("total")!=null) ? rsComprobanteEgreso.getString("total") : "0");

                        /* IMPRESION DE COPIA DEL CHEQUE  */


                        String tipo_pago = (rsComprobanteEgreso.getString("tipo_pago")!=null) ? rsComprobanteEgreso.getString("tipo_pago") : "";
                        if(tipo_pago.compareTo("c")==0){
                            PdfPTable tbl_cheque = new PdfPTable(new float[]{30f,145f,50f});

                            tbl_cheque.addCell(Addons.setCeldaPDF(" ", Font.HELVETICA, 9, Font.NORMAL, Element.ALIGN_LEFT, 0, 2, 3));

                            tbl_cheque.addCell(Addons.setCeldaPDF("BANCO " + banco.toUpperCase(), Font.HELVETICA, 9, Font.NORMAL, Element.ALIGN_LEFT, 0, 2, 2));
                            tbl_cheque.addCell(Addons.setCeldaPDF("CHEQUE No.  " + num_cheque, Font.HELVETICA, 9, Font.NORMAL, Element.ALIGN_RIGHT, 0));

                            tbl_cheque.addCell(Addons.setCeldaPDF(" ", Font.HELVETICA, 9, Font.NORMAL, Element.ALIGN_LEFT, 0, 2, 3));

                            tbl_cheque.addCell(Addons.setCeldaPDF("PAGUESE A", Font.HELVETICA, 9, Font.NORMAL, Element.ALIGN_LEFT, 0, 2, 3));

                            tbl_cheque.addCell(Addons.setCeldaPDF("LA ORDEN DE:   " + Cadena.html_decode(orden_de), Font.HELVETICA, 9, Font.NORMAL, Element.ALIGN_LEFT, 0, 2, 2));
                            tbl_cheque.addCell(Addons.setCeldaPDF("US. $        " + valor_son, Font.HELVETICA, 9, Font.NORMAL, Element.ALIGN_RIGHT, 0));

                            tbl_cheque.addCell(Addons.setCeldaPDF(" ", Font.HELVETICA, 9, Font.NORMAL, Element.ALIGN_LEFT, 0, 2, 3));

                            tbl_cheque.addCell(Addons.setCeldaPDF("LA SUMA DE:   " + son + "    US. DÓLARES", Font.HELVETICA, 9, Font.NORMAL, Element.ALIGN_LEFT, 0, 2, 3));

                            tbl_cheque.addCell(Addons.setCeldaPDF(" ", Font.HELVETICA, 9, Font.NORMAL, Element.ALIGN_LEFT, 0, 2, 3));

                            tbl_cheque.addCell(Addons.setCeldaPDF(lugar + ",  " + Addons.getTextFecha(fecha), Font.HELVETICA, 9, Font.NORMAL, Element.ALIGN_LEFT, 0, 2, 3));
                            tbl_cheque.addCell(Addons.setCeldaPDF("CIUDAD,  FECHA", Font.HELVETICA, 9, Font.NORMAL, Element.ALIGN_LEFT, 0, 2, 3));

                            tbl_cheque.addCell(Addons.setCeldaPDF(" ", Font.HELVETICA, 9, Font.NORMAL, Element.ALIGN_LEFT, 0, 2, 3));

                            PdfPTable tbl_rectangulo = new PdfPTable(1);
                            tbl_rectangulo.addCell(Addons.setCeldaPDF(tbl_cheque, Element.ALIGN_LEFT, 1));
                            document.add(tbl_rectangulo);

                            document.add(new Paragraph(" "));
                            document.add(new Paragraph(" "));
                        }


                        /*  impresion del documento  */



                        PdfPTable tbl_cab = new PdfPTable(1);
                        tbl_cab.addCell(Addons.setCeldaPDF(titulo, Font.HELVETICA, 14, Font.NORMAL, Element.ALIGN_CENTER, 0));
                        tbl_cab.addCell(Addons.setCeldaPDF("COMPROBANTE DE EGRESO", Font.HELVETICA, 14, Font.NORMAL, Element.ALIGN_CENTER, 0));
                        tbl_cab.addCell(Addons.setCeldaPDF("No.   " + ((rsComprobanteEgreso.getString("num_comprobante")!=null) ? rsComprobanteEgreso.getString("num_comprobante") : ""), Font.HELVETICA, 12, Font.NORMAL, Element.ALIGN_RIGHT, 0));
                        document.add(tbl_cab);

                        PdfPTable tbl_fechas = new PdfPTable(2);

                        tbl_fechas.addCell(Addons.setCeldaPDF("NOMBRE: " + Cadena.html_decode(orden_de), Font.HELVETICA, 10, Font.NORMAL, Element.ALIGN_LEFT, 0));
                        tbl_fechas.addCell(Addons.setCeldaPDF("CC/RUC: " + ((rsComprobanteEgreso.getString("ci_ruc")!=null) ? rsComprobanteEgreso.getString("ci_ruc") : ""), Font.HELVETICA, 10, Font.NORMAL, Element.ALIGN_LEFT, 0));

                        if(tipo_pago.compareTo("c")==0){
                            tbl_fechas.addCell(Addons.setCeldaPDF("BANCO: " + banco, Font.HELVETICA, 10, Font.NORMAL, Element.ALIGN_LEFT, 0));
                            tbl_fechas.addCell(Addons.setCeldaPDF("CHEQUE #: " + num_cheque, Font.HELVETICA, 10, Font.NORMAL, Element.ALIGN_LEFT, 0));

                            tbl_fechas.addCell(Addons.setCeldaPDF("CUENTA: " + ((rsComprobanteEgreso.getString("cuenta")!=null) ? rsComprobanteEgreso.getString("cuenta") : ""), Font.HELVETICA, 10, Font.NORMAL, Element.ALIGN_LEFT, 0));
                            tbl_fechas.addCell(Addons.setCeldaPDF("LUGAR: " + lugar, Font.HELVETICA, 10, Font.NORMAL, Element.ALIGN_LEFT, 0));
                        }

                        tbl_fechas.addCell(Addons.setCeldaPDF("FECHA: " + fecha, Font.HELVETICA, 10, Font.NORMAL, Element.ALIGN_LEFT, 0, 2, 2));
                        document.add(tbl_fechas);

                        PdfPTable tbl_concepto = new PdfPTable(new float[]{30f,145f});
                        tbl_concepto.addCell(Addons.setCeldaPDF("CONCEPTO: ", Font.HELVETICA, 10, Font.NORMAL, Element.ALIGN_LEFT, 0));
                        tbl_concepto.addCell(Addons.setCeldaPDF(((rsComprobanteEgreso.getString("detalle")!=null) ? Cadena.html_decode(rsComprobanteEgreso.getString("detalle")) : ""), Font.HELVETICA, 10, Font.NORMAL, Element.ALIGN_JUSTIFIED, 0));
                        document.add(tbl_concepto);


                        document.add(Addons.setCabeceraTabla(cabTabla, anchoTabla));

                        PdfPTable tbl_det = new PdfPTable(anchoTabla);
                        try{
                            String debe = "";
                            String haber = "";
                            while(rsComprobanteEgresoDetalle.next()){
                                debe = (rsComprobanteEgresoDetalle.getString("debe")!=null) ? ((rsComprobanteEgresoDetalle.getDouble("debe")>0)?rsComprobanteEgresoDetalle.getString("debe"):"") : "";
                                haber = (rsComprobanteEgresoDetalle.getString("haber")!=null) ? ((rsComprobanteEgresoDetalle.getDouble("haber")>0)?rsComprobanteEgresoDetalle.getString("haber"):"") : "";
                                tbl_det.addCell(Addons.setCeldaPDF(((rsComprobanteEgresoDetalle.getString("codigo_cuenta")!=null) ? rsComprobanteEgresoDetalle.getString("codigo_cuenta") : ""), Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_LEFT, 1));
                                tbl_det.addCell(Addons.setCeldaPDF(((rsComprobanteEgresoDetalle.getString("nombre_cuenta")!=null) ? rsComprobanteEgresoDetalle.getString("nombre_cuenta") : ""), Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_LEFT, 1));
                                tbl_det.addCell(Addons.setCeldaPDF(debe, Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_RIGHT, 1));
                                tbl_det.addCell(Addons.setCeldaPDF(haber, Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_RIGHT, 1));
                            }

                            tbl_det.addCell(Addons.setCeldaPDF("TOTALES:  ", Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_RIGHT, 1, 2, 2));
                            tbl_det.addCell(Addons.setCeldaPDF(total, Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_RIGHT, 1));
                            tbl_det.addCell(Addons.setCeldaPDF(total, Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_RIGHT, 1));

                            tbl_det.addCell(Addons.setCeldaPDF("SON:  " + son, Font.HELVETICA, 10, Font.NORMAL, Element.ALIGN_LEFT, 0, 2, 4));

                            rsComprobanteEgresoDetalle.close();
                        }catch(Exception e){
                            e.printStackTrace();
                        }
                        document.add(tbl_det);
                    }


                    document.add(new Paragraph(" "));
                    document.add(new Paragraph(" "));
                    document.add(new Paragraph(" "));


                    PdfPTable tbl_firmas = new PdfPTable(3);
                    tbl_firmas.addCell(Addons.setCeldaPDF("ELABORADO", Font.HELVETICA, 10, Font.NORMAL, Element.ALIGN_LEFT, 0));
                    tbl_firmas.addCell(Addons.setCeldaPDF("CONTABILIZADO", Font.HELVETICA, 10, Font.NORMAL, Element.ALIGN_LEFT, 0));
                    tbl_firmas.addCell(Addons.setCeldaPDF("APROBADO", Font.HELVETICA, 10, Font.NORMAL, Element.ALIGN_LEFT, 0));

                    tbl_firmas.addCell(Addons.setCeldaPDF(" ", Font.HELVETICA, 10, Font.NORMAL, Element.ALIGN_LEFT, 0));
                    tbl_firmas.addCell(Addons.setCeldaPDF(" ", Font.HELVETICA, 10, Font.NORMAL, Element.ALIGN_LEFT, 0));
                    tbl_firmas.addCell(Addons.setCeldaPDF(rep_nombre, Font.HELVETICA, 10, Font.NORMAL, Element.ALIGN_LEFT, 0));

                    tbl_firmas.addCell(Addons.setCeldaPDF(" ", Font.HELVETICA, 10, Font.NORMAL, Element.ALIGN_LEFT, 0));
                    tbl_firmas.addCell(Addons.setCeldaPDF(" ", Font.HELVETICA, 10, Font.NORMAL, Element.ALIGN_LEFT, 0));
                    tbl_firmas.addCell(Addons.setCeldaPDF(rep_cargo, Font.HELVETICA, 10, Font.NORMAL, Element.ALIGN_LEFT, 0));

                    document.add(tbl_firmas);


                    
                    document.add(new Paragraph(" "));
                    document.add(new Paragraph(" "));
                    document.add(new Paragraph(" "));

                    PdfPTable tbl_firmas1 = new PdfPTable(3);

                    if(apr_nombre.compareTo("")!=0){
                        tbl_firmas1.addCell(Addons.setCeldaPDF("APROBADO", Font.HELVETICA, 10, Font.NORMAL, Element.ALIGN_LEFT, 0));
                        tbl_firmas1.addCell(Addons.setCeldaPDF("RECIBÍ CONFORME", Font.HELVETICA, 10, Font.NORMAL, Element.ALIGN_LEFT, 0));
                        tbl_firmas1.addCell(Addons.setCeldaPDF(" ", Font.HELVETICA, 10, Font.NORMAL, Element.ALIGN_LEFT, 0));

                        tbl_firmas1.addCell(Addons.setCeldaPDF(apr_nombre, Font.HELVETICA, 10, Font.NORMAL, Element.ALIGN_LEFT, 0));
                        tbl_firmas1.addCell(Addons.setCeldaPDF("CC/RUC: ", Font.HELVETICA, 10, Font.NORMAL, Element.ALIGN_LEFT, 0));
                        tbl_firmas1.addCell(Addons.setCeldaPDF(" ", Font.HELVETICA, 10, Font.NORMAL, Element.ALIGN_LEFT, 0));

                        tbl_firmas1.addCell(Addons.setCeldaPDF(apr_cargo, Font.HELVETICA, 10, Font.NORMAL, Element.ALIGN_LEFT, 0));
                        tbl_firmas1.addCell(Addons.setCeldaPDF(" ", Font.HELVETICA, 10, Font.NORMAL, Element.ALIGN_LEFT, 0));
                        tbl_firmas1.addCell(Addons.setCeldaPDF(" ", Font.HELVETICA, 10, Font.NORMAL, Element.ALIGN_LEFT, 0));
                    }else{
                        tbl_firmas1.addCell(Addons.setCeldaPDF("RECIBÍ CONFORME", Font.HELVETICA, 10, Font.NORMAL, Element.ALIGN_LEFT, 0, 2, 32));

                        tbl_firmas1.addCell(Addons.setCeldaPDF("CC/RUC: ", Font.HELVETICA, 10, Font.NORMAL, Element.ALIGN_LEFT, 0, 2, 3));
                    }

                    document.add(tbl_firmas1);


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
                    rsComprobanteEgresoDetalle.close();
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
        Configuracion conf = new Configuracion(this._ip, this._puerto, this._db, this._usuario , this._clave);
        this.titulo = conf.getValor("razon_social");
        this.rep_nombre = conf.getValor("rep_nombre");
        this.rep_cargo = conf.getValor("rep_cargo");
        this.apr_nombre = conf.getValor("apr2_nombre");
        this.apr_cargo = conf.getValor("apr2_cargo");
        
        conf.cerrar();

        String id = request.getParameter("id");

        imComprobanteEgreso objImComprobanteEgreso = new imComprobanteEgreso();
        objImComprobanteEgreso.imprimir(response, id);
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
