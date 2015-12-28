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
import jm.cnt.clas.ReposicionCajaChica;

import java.sql.ResultSet;
import java.sql.SQLException;
import jm.cnt.clas.ComprobanteEgreso;
import jm.web.Fecha;

/**
 *
 * @author Jorge
 */
public class pdfReposicionCajaChica extends HttpServlet {
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

    class imReposicionCajaChica{
        public imReposicionCajaChica(){}

        public void imprimir(HttpServletResponse response, String id){
            response.setContentType("application/pdf");
            response.setHeader("Pragma", "no-cache");
            response.setHeader("Expires", "Mon, 01 Jan 2001 00:00:01 GMT");
            response.setHeader("Cache-Control", "no-store");
            response.setHeader("Cache-Control", "must-revalidate");
            response.setHeader("Cache-Control", "no-cache");

            ResultSet rsRepCajaChica = null;
            ResultSet rsRepCajaChicaDetalle = null;
            ReposicionCajaChica objReposicionCajaChica = new ReposicionCajaChica(_ip, _puerto, _db, _usuario, _clave);
            try {
                rsRepCajaChica = objReposicionCajaChica.getFormulario(id);


                /* inicio PDF */
                Document document = new Document(PageSize.A4);// paso 1
                document.setMargins(0,0,50,80); /*Izquierda, derecha, tope, pie */
                try{
                    PdfWriter writer = PdfWriter.getInstance(document, response.getOutputStream()); // paso 2

                    document.open(); // paso 3

                    String periodo_desde = "";
                    String periodo_hasta = "";
                    String hora_desde = "";
                    if(rsRepCajaChica.next()){
                        String hora_hasta = objReposicionCajaChica.getHoraHasta(id);
                        
                        periodo_desde = (rsRepCajaChica.getString("periodo_desde")!=null) ? rsRepCajaChica.getString("periodo_desde") : "";
                        periodo_hasta = (rsRepCajaChica.getString("periodo_hasta")!=null) ? rsRepCajaChica.getString("periodo_hasta") + " " + hora_hasta : "";
                        hora_desde = (rsRepCajaChica.getString("hora")!=null) ? rsRepCajaChica.getString("hora") : "";
                        writer.addJavaScript("this.print(false);", false); // Para enviar a la impresora automÃ¡ticamente.

                        /* todo el cuerpo del doc es el paso 4 */

                        PdfPTable tbl_cab = new PdfPTable(1);
                        tbl_cab.addCell(Addons.setCeldaPDF(titulo, Font.HELVETICA, 14, Font.NORMAL, Element.ALIGN_CENTER, 0));
                        tbl_cab.addCell(Addons.setCeldaPDF("FORMULARIO DE REPOSICION DE CAJA CHICA", Font.HELVETICA, 14, Font.NORMAL, Element.ALIGN_CENTER, 0));
                        tbl_cab.addCell(Addons.setCeldaPDF("No.   " + ((rsRepCajaChica.getString("id_sucursal")!=null) ? rsRepCajaChica.getString("id_sucursal") : "") +
                                "-" + ((rsRepCajaChica.getString("num_reposicion_caja_chica")!=null) ? rsRepCajaChica.getString("num_reposicion_caja_chica") : ""), Font.HELVETICA, 12, Font.NORMAL, Element.ALIGN_RIGHT, 0));
                        document.add(tbl_cab);


                        document.add(new Paragraph(" "));

                        PdfPTable tbl_det = new PdfPTable(new float[]{30f,70f});

                        tbl_det.addCell(Addons.setCeldaPDF("Fecha: ", Font.HELVETICA, 10, Font.NORMAL, Element.ALIGN_LEFT, 0));
                        tbl_det.addCell(Addons.setCeldaPDF(((rsRepCajaChica.getString("fecha")!=null) ? Fecha.ISOaSQL(rsRepCajaChica.getString("fecha")) : ""), Font.HELVETICA, 10, Font.NORMAL, Element.ALIGN_LEFT, 0));

                        tbl_det.addCell(Addons.setCeldaPDF("Saldo en caja chica: ", Font.HELVETICA, 10, Font.NORMAL, Element.ALIGN_LEFT, 0));
                        tbl_det.addCell(Addons.setCeldaPDF(((rsRepCajaChica.getString("saldo_caja")!=null) ? Fecha.ISOaSQL(rsRepCajaChica.getString("saldo_caja")) : ""), Font.HELVETICA, 10, Font.NORMAL, Element.ALIGN_LEFT, 0));

                        tbl_det.addCell(Addons.setCeldaPDF("Período comprendido entre: ", Font.HELVETICA, 10, Font.NORMAL, Element.ALIGN_LEFT, 0));
                        tbl_det.addCell(Addons.setCeldaPDF(Fecha.ISOaSQL(periodo_desde) + " hasta " + Fecha.ISOaSQL(periodo_hasta), Font.HELVETICA, 10, Font.NORMAL, Element.ALIGN_LEFT, 0));

                        tbl_det.addCell(Addons.setCeldaPDF("Responsable del fondo: ", Font.HELVETICA, 10, Font.NORMAL, Element.ALIGN_LEFT, 0));
                        tbl_det.addCell(Addons.setCeldaPDF(((rsRepCajaChica.getString("responsable")!=null) ? Fecha.ISOaSQL(rsRepCajaChica.getString("responsable")) : ""), Font.HELVETICA, 10, Font.NORMAL, Element.ALIGN_LEFT, 0));

                        tbl_det.addCell(Addons.setFilaBlanco(2, 8));
                        document.add(tbl_det);
                    }

                    PdfPTable tbl_Vales = new PdfPTable(new float[]{20f,20f,40f,40f,60f,20f});
                    tbl_Vales.addCell(Addons.setCeldaPDF("Nro. VALE", Font.HELVETICA, 9, Font.NORMAL, Element.ALIGN_CENTER, 1));
                    tbl_Vales.addCell(Addons.setCeldaPDF("FECHA", Font.HELVETICA, 9, Font.NORMAL, Element.ALIGN_CENTER, 1));
                    tbl_Vales.addCell(Addons.setCeldaPDF("CUENTA CONTABLE", Font.HELVETICA, 9, Font.NORMAL, Element.ALIGN_CENTER, 1));
                    tbl_Vales.addCell(Addons.setCeldaPDF("ENTREGADO A", Font.HELVETICA, 9, Font.NORMAL, Element.ALIGN_CENTER, 1));
                    tbl_Vales.addCell(Addons.setCeldaPDF("CONCEPTO", Font.HELVETICA, 9, Font.NORMAL, Element.ALIGN_CENTER, 1));
                    tbl_Vales.addCell(Addons.setCeldaPDF("IMPORTE ($)", Font.HELVETICA, 9, Font.NORMAL, Element.ALIGN_CENTER, 1));
                    try{
                        ResultSet rsVales = objReposicionCajaChica.getValesCajaChica(id);
                        while(rsVales.next()){
                            tbl_Vales.addCell(Addons.setCeldaPDF((rsVales.getString("num_vale_caja")!=null ? rsVales.getString("num_vale_caja") : ""), Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_LEFT, 1));
                            tbl_Vales.addCell(Addons.setCeldaPDF((rsVales.getString("fecha")!=null ? Fecha.ISOaSQL(rsVales.getString("fecha")) : ""), Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_LEFT, 1));
                            tbl_Vales.addCell(Addons.setCeldaPDF((rsVales.getString("nombre_cuenta")!=null ? rsVales.getString("nombre_cuenta") : ""), Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_LEFT, 1));
                            tbl_Vales.addCell(Addons.setCeldaPDF((rsVales.getString("solicitante")!=null ? rsVales.getString("solicitante") : ""), Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_LEFT, 1));
                            tbl_Vales.addCell(Addons.setCeldaPDF((rsVales.getString("concepto")!=null ? rsVales.getString("concepto") : ""), Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_LEFT, 1));
                            tbl_Vales.addCell(Addons.setCeldaPDF((rsVales.getString("importe")!=null ? rsVales.getString("importe") : "0"), Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_RIGHT, 1));
                        }
                        rsVales.close();
                    }catch(Exception ex){
                        ex.printStackTrace();
                    }

                    tbl_Vales.addCell(Addons.setCeldaPDF("TOTAL IMPORTE VALES DE CAJA CHICA", Font.HELVETICA, 9, Font.NORMAL, Element.ALIGN_LEFT, 0, 3, 5));
                    tbl_Vales.addCell(Addons.setCeldaPDF(((rsRepCajaChica.getString("total_importe")!=null) ? rsRepCajaChica.getString("total_importe") : "0"), Font.HELVETICA, 9, Font.NORMAL, Element.ALIGN_RIGHT, 0));
                    document.add(tbl_Vales);

                    document.add(new Paragraph(" "));
                    
                    float sum_comp = 0;
                    ComprobanteEgreso objCompEgre = new ComprobanteEgreso(_ip, _puerto, _db, _usuario, _clave);
                    try{
                        ResultSet rsComp = objCompEgre.getComprobantesEgreso(id, periodo_desde + " " + hora_desde, periodo_hasta);
                        if(objCompEgre.getFilas(rsComp)>0){
                            PdfPTable tbl_Comp = new PdfPTable(new float[]{20f,20f,40f,60f,20f});
                            tbl_Comp.addCell(Addons.setCeldaPDF("Nro. COMP.", Font.HELVETICA, 9, Font.NORMAL, Element.ALIGN_CENTER, 1));
                            tbl_Comp.addCell(Addons.setCeldaPDF("FECHA", Font.HELVETICA, 9, Font.NORMAL, Element.ALIGN_CENTER, 1));
                            tbl_Comp.addCell(Addons.setCeldaPDF("A NOMBRE DE", Font.HELVETICA, 9, Font.NORMAL, Element.ALIGN_CENTER, 1));
                            tbl_Comp.addCell(Addons.setCeldaPDF("CONCEPTO", Font.HELVETICA, 9, Font.NORMAL, Element.ALIGN_CENTER, 1));
                            tbl_Comp.addCell(Addons.setCeldaPDF("VALOR ($)", Font.HELVETICA, 9, Font.NORMAL, Element.ALIGN_CENTER, 1));
                            while(rsComp.next()){
                                tbl_Comp.addCell(Addons.setCeldaPDF((rsComp.getString("num_comprobante")!=null ? rsComp.getString("num_comprobante") : ""), Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_LEFT, 1));
                                tbl_Comp.addCell(Addons.setCeldaPDF((rsComp.getString("fecha_actual")!=null ? Fecha.ISOaSQL(rsComp.getString("fecha_actual")) : ""), Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_LEFT, 1));
                                tbl_Comp.addCell(Addons.setCeldaPDF((rsComp.getString("orden_de")!=null ? rsComp.getString("orden_de") : ""), Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_LEFT, 1));
                                tbl_Comp.addCell(Addons.setCeldaPDF((rsComp.getString("detalle")!=null ? rsComp.getString("detalle") : ""), Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_LEFT, 1));
                                tbl_Comp.addCell(Addons.setCeldaPDF((rsComp.getString("valor_son")!=null ? rsComp.getString("valor_son") : "0"), Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_RIGHT, 1));
                                sum_comp += rsComp.getString("valor_son")!=null ? rsComp.getFloat("valor_son") : 0;
                            }
                            rsComp.close();
                            tbl_Comp.addCell(Addons.setCeldaPDF("TOTAL REINTEGRO COMPROBANTES DE EGRESO", Font.HELVETICA, 9, Font.NORMAL, Element.ALIGN_LEFT, 0, 3, 4));
                            tbl_Comp.addCell(Addons.setCeldaPDF(Addons.redondear(String.valueOf(sum_comp)), Font.HELVETICA, 9, Font.NORMAL, Element.ALIGN_RIGHT, 0));
                            document.add(tbl_Comp);
                        }
                    }catch(Exception ex){
                        ex.printStackTrace();
                    }finally{
                        objCompEgre.cerrar();
                    }
                    
                    
                    document.add(new Paragraph(" "));
                    PdfPTable tbl_tot = new PdfPTable(new float[]{85f,15f});
                    tbl_tot.addCell(Addons.setCeldaPDF("TOTAL REINTEGRO", Font.HELVETICA, 9, Font.NORMAL, Element.ALIGN_LEFT, 0));
                    tbl_tot.addCell(Addons.setCeldaPDF(((rsRepCajaChica.getString("total_reintegro")!=null) ? Fecha.ISOaSQL(rsRepCajaChica.getString("total_reintegro")) : "0"), Font.HELVETICA, 9, Font.NORMAL, Element.ALIGN_RIGHT, 0));
                    document.add(tbl_tot);
                    

                    document.add(new Paragraph(" "));
                    document.add(new Paragraph(" "));
                    document.add(new Paragraph(" "));


                    PdfPTable tbl_firmas = new PdfPTable(2);
                    tbl_firmas.addCell(Addons.setCeldaPDF("RESPONSABLE", Font.HELVETICA, 10, Font.NORMAL, Element.ALIGN_CENTER, 0));
                    tbl_firmas.addCell(Addons.setCeldaPDF("APROBADO POR", Font.HELVETICA, 10, Font.NORMAL, Element.ALIGN_CENTER, 0));
                    document.add(tbl_firmas);


                    rsRepCajaChica.close();
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
                    rsRepCajaChica.close();
                    rsRepCajaChicaDetalle.close();
                }catch(SQLException e){
                    e.printStackTrace();
                }
                objReposicionCajaChica.cerrar();
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

        imReposicionCajaChica objImReposicionCajaChica = new imReposicionCajaChica();
        objImReposicionCajaChica.imprimir(response, id);
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
