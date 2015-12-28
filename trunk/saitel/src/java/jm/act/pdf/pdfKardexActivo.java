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

package jm.act.pdf;

import java.io.*;

import javax.servlet.*;
import javax.servlet.http.*;

import com.lowagie.text.pdf.*;
import com.lowagie.text.*;
import java.awt.Color;

import jm.adm.clas.Configuracion;
import jm.web.Addons;

import java.sql.ResultSet;
import jm.act.clas.Activo;

/**
 *
 * @author Jorge
 */
public class pdfKardexActivo extends HttpServlet {
    private String _ip = null;
    private int _puerto = 5432;
    private String _db = null;
    
    private String _usuario = null;
    private String _clave = null;

    private String titulo = "";
    private String rep_pie = "";

    public void init(ServletConfig config) throws ServletException
    {
        this._ip = config.getServletContext().getInitParameter("_IP");
        this._puerto = Integer.parseInt(config.getServletContext().getInitParameter("_PUERTO"));
        this._db = config.getServletContext().getInitParameter("_DB");
    }

    class imKardex extends PdfPageEventHelper{
        public imKardex(){}

        public void imprimir(HttpServletResponse response, String id, String fecha_ini, String fecha_fin){
            response.setContentType("application/pdf");
            response.setHeader("Pragma", "no-cache");
            response.setHeader("Expires", "Mon, 01 Jan 2001 00:00:01 GMT");
            response.setHeader("Cache-Control", "no-store");
            response.setHeader("Cache-Control", "must-revalidate");
            response.setHeader("Cache-Control", "no-cache");

            Activo objActivo = new Activo(_ip, _puerto, _db, _usuario, _clave);
            String codigo = "";
            String descripcion = "";
            String marca = "";
            String serie = "";
            String valor_compra = "";
            String valor_depreciado = "";
            ResultSet rsActivo = objActivo.getActivo(id);
            try{
                if(rsActivo.next()){
                    codigo = (rsActivo.getString("codigo_activo")!=null) ? rsActivo.getString("codigo_activo") : "";
                    descripcion = (rsActivo.getString("descripcion")!=null) ? rsActivo.getString("descripcion") : "";
                    marca = (rsActivo.getString("marca")!=null) ? rsActivo.getString("marca") : "";
                    serie = (rsActivo.getString("serie")!=null) ? rsActivo.getString("serie") : "";
                    valor_compra = (rsActivo.getString("valor_compra")!=null) ? rsActivo.getString("valor_compra") : "";
                    valor_depreciado = (rsActivo.getString("valor_depreciado")!=null) ? rsActivo.getString("valor_depreciado") : "";
                    rsActivo.close();
                }
            }catch(Exception e){
                e.printStackTrace();
            }

            try {
                /* inicio PDF */
                Document document = new Document(PageSize.A4.rotate());// paso 1
                document.setMargins(0,0,50,80); /*Izquierda, derecha, tope, pie */
                try{
                    PdfWriter writer = PdfWriter.getInstance(document, response.getOutputStream()); // paso 2
                    writer.setPageEvent(new imKardex());

                    document.open(); // paso 3


                    writer.addJavaScript("this.print(false);", false); // Para enviar a la impresora automáticamente.

                    /* todo el cuerpo del doc es el paso 4 */

                    PdfPTable tbl_cab = new PdfPTable(1);
                    tbl_cab.addCell(Addons.setCeldaPDF(titulo, Font.HELVETICA, 14, Font.NORMAL, Element.ALIGN_CENTER, 0));
                    tbl_cab.addCell(Addons.setCeldaPDF("Kardex de Activo", Font.HELVETICA, 14, Font.NORMAL, Element.ALIGN_CENTER, 0));
                    document.add(tbl_cab);

                    document.add(new Paragraph(" "));

                    PdfPTable tbl_articulo = new PdfPTable(new float[]{30f,120f,20f,70f,40f,30f});

                    tbl_articulo.addCell(Addons.setCeldaPDF("Código: ", Font.HELVETICA, 10, Font.NORMAL, Element.ALIGN_LEFT, 0));
                    tbl_articulo.addCell(Addons.setCeldaPDF(codigo, Font.HELVETICA, 10, Font.NORMAL, Element.ALIGN_LEFT, 0));
                    tbl_articulo.addCell(Addons.setCeldaPDF("Marca: ", Font.HELVETICA, 10, Font.NORMAL, Element.ALIGN_LEFT, 0));
                    tbl_articulo.addCell(Addons.setCeldaPDF(marca, Font.HELVETICA, 10, Font.NORMAL, Element.ALIGN_LEFT, 0));
                    tbl_articulo.addCell(Addons.setCeldaPDF("Valor compra: ", Font.HELVETICA, 10, Font.NORMAL, Element.ALIGN_LEFT, 0));
                    tbl_articulo.addCell(Addons.setCeldaPDF(valor_compra, Font.HELVETICA, 10, Font.NORMAL, Element.ALIGN_LEFT, 0));

                    tbl_articulo.addCell(Addons.setCeldaPDF("Descripción: ", Font.HELVETICA, 10, Font.NORMAL, Element.ALIGN_LEFT, 0));
                    tbl_articulo.addCell(Addons.setCeldaPDF(descripcion, Font.HELVETICA, 10, Font.NORMAL, Element.ALIGN_LEFT, 0));
                    tbl_articulo.addCell(Addons.setCeldaPDF("Serie: ", Font.HELVETICA, 10, Font.NORMAL, Element.ALIGN_LEFT, 0));
                    tbl_articulo.addCell(Addons.setCeldaPDF(serie, Font.HELVETICA, 10, Font.NORMAL, Element.ALIGN_LEFT, 0));
                    tbl_articulo.addCell(Addons.setCeldaPDF("Valor depreciado: ", Font.HELVETICA, 10, Font.NORMAL, Element.ALIGN_LEFT, 0));
                    tbl_articulo.addCell(Addons.setCeldaPDF(valor_depreciado, Font.HELVETICA, 10, Font.NORMAL, Element.ALIGN_LEFT, 0));

                    document.add(tbl_articulo);

                    PdfPTable cabTabla0 = new PdfPTable(new float[]{40f,40f,80f,30f,25f,80f});
                    cabTabla0.addCell(Addons.setCeldaPDF("SUCURSAL", Font.HELVETICA, 9, Font.BOLD, Element.ALIGN_CENTER, 1, Color.cyan));
                    cabTabla0.addCell(Addons.setCeldaPDF("BODEGA", Font.HELVETICA, 9, Font.BOLD, Element.ALIGN_CENTER, 1, Color.cyan));
                    cabTabla0.addCell(Addons.setCeldaPDF("UBICACION", Font.HELVETICA, 9, Font.BOLD, Element.ALIGN_CENTER, 1, Color.cyan));
                    cabTabla0.addCell(Addons.setCeldaPDF("No. Doc.", Font.HELVETICA, 9, Font.BOLD, Element.ALIGN_CENTER, 1, Color.cyan));
                    cabTabla0.addCell(Addons.setCeldaPDF("FECHA", Font.HELVETICA, 9, Font.BOLD, Element.ALIGN_CENTER, 1, Color.cyan));
                    cabTabla0.addCell(Addons.setCeldaPDF("DETALLE", Font.HELVETICA, 9, Font.BOLD, Element.ALIGN_CENTER, 1, Color.cyan));
                    document.add(cabTabla0);


                    PdfPTable tbl_det = new PdfPTable(new float[]{40f,40f,80f,30f,25f,80f});
                    try{
                        ResultSet rsKardex = objActivo.getKardex(id);
                        while(rsKardex.next()){
                            tbl_det.addCell(Addons.setCeldaPDF(((rsKardex.getString("sucursal")!=null) ? rsKardex.getString("sucursal") : ""), Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_LEFT, 1));
                            tbl_det.addCell(Addons.setCeldaPDF(((rsKardex.getString("bodega")!=null) ? rsKardex.getString("bodega") : ""), Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_LEFT, 1));
                            tbl_det.addCell(Addons.setCeldaPDF(((rsKardex.getString("ubicacion")!=null) ? rsKardex.getString("ubicacion") : ""), Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_LEFT, 1));
                            tbl_det.addCell(Addons.setCeldaPDF(((rsKardex.getString("num_documento")!=null) ? rsKardex.getString("num_documento") : ""), Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_LEFT, 1));
                            tbl_det.addCell(Addons.setCeldaPDF(((rsKardex.getString("fecha")!=null) ? rsKardex.getString("fecha") : ""), Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_CENTER, 1));
                            tbl_det.addCell(Addons.setCeldaPDF(((rsKardex.getString("detalle")!=null) ? rsKardex.getString("detalle") : ""), Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_LEFT, 1));
                        }
                        rsKardex.close();
                    }catch(Exception e){
                        e.printStackTrace();
                    }

                    document.add(tbl_det);


                }catch(IllegalStateException ie){
                    ie.printStackTrace();
                }catch(DocumentException e){
                    e.printStackTrace();
                }catch(Exception e){
                    e.printStackTrace();
                }


                document.close(); // paso 5
                /* fin PDF */

            }catch(Exception e){
                e.printStackTrace();
            }finally {
                objActivo.cerrar();
            }
        }

        public void onEndPage(PdfWriter writer, Document document)
        {
            Addons.setPie(writer, document, rep_pie);
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
        //String fecha_ini = request.getParameter("fi");
        //String fecha_fin = request.getParameter("ff");
        String fecha_ini = "";
        String fecha_fin = "";

        Configuracion conf = new Configuracion(this._ip, this._puerto, this._db, this._usuario, this._clave);
        this.titulo = conf.getValor("razon_social");
        this.rep_pie = conf.getValor("rep_pie");
        conf.cerrar();

        imKardex objImKardex = new imKardex();
        objImKardex.imprimir(response, id, fecha_ini, fecha_fin);
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
