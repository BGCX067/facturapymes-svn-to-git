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
import java.awt.Color;

import jm.adm.clas.Configuracion;
import jm.adm.clas.Sucursal;
import jm.web.Addons;
import jm.inv.clas.Producto;

import java.sql.ResultSet;
import java.sql.SQLException;
import jm.web.Fecha;

/**
 *
 * @author Jorge
 */
public class pdfKardex extends HttpServlet {
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

        public void imprimir(HttpServletResponse response, String id_sucursal, String sucursal, String id, String fecha_ini, String fecha_fin, String metodo_costeo){
            response.setContentType("application/pdf");
            response.setHeader("Pragma", "no-cache");
            response.setHeader("Expires", "Mon, 01 Jan 2001 00:00:01 GMT");
            response.setHeader("Cache-Control", "no-store");
            response.setHeader("Cache-Control", "must-revalidate");
            response.setHeader("Cache-Control", "no-cache");

            Producto objProducto = new Producto(_ip, _puerto, _db, _usuario, _clave);
            ResultSet rsKardex = null;
            if(id_sucursal.compareTo("-0")!=0){
                rsKardex = objProducto.getKardexSucursal(id_sucursal, id, fecha_ini, fecha_fin);
            }else{
                rsKardex = objProducto.getKardex(id, fecha_ini, fecha_fin);
            }
            
            ResultSet rsProducto = objProducto.getProducto(id);
            
            try {
                /* inicio PDF */
                Document document = new Document(PageSize.A4.rotate());// paso 1
                document.setMargins(0,0,50,80); /*Izquierda, derecha, tope, pie */
                try{
                    PdfWriter writer = PdfWriter.getInstance(document, response.getOutputStream()); // paso 2
                    writer.setPageEvent(new imKardex());

                    document.open(); // paso 3

                    if(rsProducto.next()){

                        writer.addJavaScript("this.print(false);", false); // Para enviar a la impresora automáticamente.

                        /* todo el cuerpo del doc es el paso 4 */

                        PdfPTable tbl_cab = new PdfPTable(1);
                        tbl_cab.addCell(Addons.setCeldaPDF(titulo, Font.HELVETICA, 14, Font.NORMAL, Element.ALIGN_CENTER, 0));
                        tbl_cab.addCell(Addons.setCeldaPDF("SUCURSAL: "+sucursal, Font.HELVETICA, 14, Font.NORMAL, Element.ALIGN_CENTER, 0));
                        tbl_cab.addCell(Addons.setCeldaPDF("Kardex de Producto", Font.HELVETICA, 14, Font.NORMAL, Element.ALIGN_CENTER, 0));
                        document.add(tbl_cab);

                        document.add(new Paragraph(" "));

                        int cols = 11;
                        String metodo_costeo1 = "";
                        if(metodo_costeo.compareTo("PROM")==0){
                            metodo_costeo1 = "PROMEDIO PONDERADO";
                        }
                        if(metodo_costeo.compareTo("COST")==0){
                            metodo_costeo1 = "AL COSTO";
                            cols = 12;
                        }

                        PdfPTable tbl_articulo = new PdfPTable(new float[]{40f,100f,40f,240f,40f,40f});

                        tbl_articulo.addCell(Addons.setCeldaPDF("Unidad: ", Font.HELVETICA, 10, Font.NORMAL, Element.ALIGN_LEFT, 0));
                        tbl_articulo.addCell(Addons.setCeldaPDF(((rsProducto.getString("unidad_medida")!=null) ? rsProducto.getString("unidad_medida") : ""), Font.HELVETICA, 10, Font.NORMAL, Element.ALIGN_LEFT, 0));
                        tbl_articulo.addCell(Addons.setCeldaPDF("Método: ", Font.HELVETICA, 10, Font.NORMAL, Element.ALIGN_LEFT, 0));
                        tbl_articulo.addCell(Addons.setCeldaPDF(metodo_costeo1, Font.HELVETICA, 10, Font.NORMAL, Element.ALIGN_LEFT, 0));
                        tbl_articulo.addCell(Addons.setCeldaPDF("Mínimo: ", Font.HELVETICA, 10, Font.NORMAL, Element.ALIGN_LEFT, 0));
                        tbl_articulo.addCell(Addons.setCeldaPDF(((rsProducto.getString("stock_min")!=null) ? rsProducto.getString("stock_min") : ""), Font.HELVETICA, 10, Font.NORMAL, Element.ALIGN_LEFT, 0));

                        tbl_articulo.addCell(Addons.setCeldaPDF("Código: ", Font.HELVETICA, 10, Font.NORMAL, Element.ALIGN_LEFT, 0));
                        tbl_articulo.addCell(Addons.setCeldaPDF(((rsProducto.getString("codigo")!=null) ? rsProducto.getString("codigo") : ""), Font.HELVETICA, 10, Font.NORMAL, Element.ALIGN_LEFT, 0));
                        tbl_articulo.addCell(Addons.setCeldaPDF("Producto: ", Font.HELVETICA, 10, Font.NORMAL, Element.ALIGN_LEFT, 0));
                        tbl_articulo.addCell(Addons.setCeldaPDF(((rsProducto.getString("descripcion")!=null) ? rsProducto.getString("descripcion") : ""), Font.HELVETICA, 10, Font.NORMAL, Element.ALIGN_LEFT, 0));
                        tbl_articulo.addCell(Addons.setCeldaPDF("Stock: ", Font.HELVETICA, 10, Font.NORMAL, Element.ALIGN_LEFT, 0));
                        tbl_articulo.addCell(Addons.setCeldaPDF(((rsProducto.getString("stock")!=null) ? rsProducto.getString("stock") : ""), Font.HELVETICA, 10, Font.NORMAL, Element.ALIGN_LEFT, 0));

                        document.add(tbl_articulo);



                        PdfPTable cabTabla0 = this.getTabla(cols);
                        cabTabla0.addCell(Addons.setCeldaPDF(" ", Font.HELVETICA, 9, Font.BOLD, Element.ALIGN_CENTER, 1, Color.cyan, 2, 2));
                        cabTabla0.addCell(Addons.setCeldaPDF("ENTRADAS", Font.HELVETICA, 9, Font.BOLD, Element.ALIGN_CENTER, 1, Color.cyan, 2, 3));
                        cabTabla0.addCell(Addons.setCeldaPDF("SALIDAS", Font.HELVETICA, 9, Font.BOLD, Element.ALIGN_CENTER, 1, Color.cyan, 2, 3));
                        cabTabla0.addCell(Addons.setCeldaPDF("SALDOS", Font.HELVETICA, 9, Font.BOLD, Element.ALIGN_CENTER, 1, Color.cyan, 2, 3));
                        if(metodo_costeo.compareTo("COST")==0){
                            cabTabla0.addCell(Addons.setCeldaPDF(" ", Font.HELVETICA, 9, Font.BOLD, Element.ALIGN_CENTER, 1, Color.cyan));
                        }

                        cabTabla0.addCell(Addons.setCeldaPDF("FECHA", Font.HELVETICA, 9, Font.BOLD, Element.ALIGN_CENTER, 1, Color.cyan));
                        cabTabla0.addCell(Addons.setCeldaPDF("DETALLE", Font.HELVETICA, 9, Font.BOLD, Element.ALIGN_CENTER, 1, Color.cyan));
                        cabTabla0.addCell(Addons.setCeldaPDF("C.", Font.HELVETICA, 9, Font.BOLD, Element.ALIGN_CENTER, 1, Color.cyan));
                        cabTabla0.addCell(Addons.setCeldaPDF("V.U.", Font.HELVETICA, 9, Font.BOLD, Element.ALIGN_CENTER, 1, Color.cyan));
                        cabTabla0.addCell(Addons.setCeldaPDF("V.T.", Font.HELVETICA, 9, Font.BOLD, Element.ALIGN_CENTER, 1, Color.cyan));
                        cabTabla0.addCell(Addons.setCeldaPDF("C.", Font.HELVETICA, 9, Font.BOLD, Element.ALIGN_CENTER, 1, Color.cyan));
                        cabTabla0.addCell(Addons.setCeldaPDF("V.U.", Font.HELVETICA, 9, Font.BOLD, Element.ALIGN_CENTER, 1, Color.cyan));
                        cabTabla0.addCell(Addons.setCeldaPDF("V.T.", Font.HELVETICA, 9, Font.BOLD, Element.ALIGN_CENTER, 1, Color.cyan));
                        cabTabla0.addCell(Addons.setCeldaPDF("C.", Font.HELVETICA, 9, Font.BOLD, Element.ALIGN_CENTER, 1, Color.cyan));
                        cabTabla0.addCell(Addons.setCeldaPDF("V.U.", Font.HELVETICA, 9, Font.BOLD, Element.ALIGN_CENTER, 1, Color.cyan));
                        cabTabla0.addCell(Addons.setCeldaPDF("V.T.", Font.HELVETICA, 9, Font.BOLD, Element.ALIGN_CENTER, 1, Color.cyan));
                        if(metodo_costeo.compareTo("COST")==0){
                            cabTabla0.addCell(Addons.setCeldaPDF("AJUSTE", Font.HELVETICA, 9, Font.BOLD, Element.ALIGN_CENTER, 1, Color.cyan));
                        }

                        document.add(cabTabla0);


                        PdfPTable tbl_det = this.getTabla(cols);
                        try{
                            String cantidad = "";
                            String costo_unitario = "";
                            String costo_total = "";
                            boolean es_entrada = true;
                            while(rsKardex.next()){
                                cantidad = (rsKardex.getString("cantidad")!=null) ? rsKardex.getString("cantidad") : "";
                                costo_unitario = (rsKardex.getString("costo_unitario")!=null) ? rsKardex.getString("costo_unitario") : "";
                                costo_total = (rsKardex.getString("costo_total")!=null) ? rsKardex.getString("costo_total") : "";
                                es_entrada = (rsKardex.getString("es_entrada")!=null) ? rsKardex.getBoolean("es_entrada") : false;
                                tbl_det.addCell(Addons.setCeldaPDF(((rsKardex.getString("fecha")!=null) ? Fecha.ISOaSQL(rsKardex.getString("fecha")) : ""), Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_CENTER, 1));
                                tbl_det.addCell(Addons.setCeldaPDF(((rsKardex.getString("detalle")!=null) ? rsKardex.getString("detalle") : ""), Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_LEFT, 1));
                                tbl_det.addCell(Addons.setCeldaPDF(((es_entrada) ? cantidad : ""), Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_CENTER, 1));
                                tbl_det.addCell(Addons.setCeldaPDF(((es_entrada) ? costo_unitario : ""), Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_RIGHT, 1));
                                tbl_det.addCell(Addons.setCeldaPDF(((es_entrada) ? costo_total : ""), Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_RIGHT, 1));
                                tbl_det.addCell(Addons.setCeldaPDF(((!es_entrada) ? cantidad : ""), Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_CENTER, 1));
                                tbl_det.addCell(Addons.setCeldaPDF(((!es_entrada) ? costo_unitario : ""), Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_RIGHT, 1));
                                tbl_det.addCell(Addons.setCeldaPDF(((!es_entrada) ? costo_total : ""), Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_RIGHT, 1));
                                tbl_det.addCell(Addons.setCeldaPDF(((rsKardex.getString("saldo_cantidad")!=null) ? rsKardex.getString("saldo_cantidad") : ""), Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_CENTER, 1));
                                tbl_det.addCell(Addons.setCeldaPDF(((rsKardex.getString("saldo_costo_unitario")!=null) ? rsKardex.getString("saldo_costo_unitario") : ""), Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_RIGHT, 1));
                                tbl_det.addCell(Addons.setCeldaPDF(((rsKardex.getString("saldo_costo_total")!=null) ? rsKardex.getString("saldo_costo_total") : ""), Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_RIGHT, 1));
                                if(metodo_costeo.compareTo("COST")==0){
                                    tbl_det.addCell(Addons.setCeldaPDF(((rsKardex.getString("precio_unitario_ajuste")!=null) ? rsKardex.getString("precio_unitario_ajuste") : ""), Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_RIGHT, 1));
                                }
                            }
                            rsKardex.close();
                        }catch(Exception e){
                            e.printStackTrace();
                        }

                        document.add(tbl_det);
                        
                    }


                    rsProducto.close();
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
                    rsProducto.close();
                    rsKardex.close();
                }catch(SQLException e){
                    e.printStackTrace();
                }
                objProducto.cerrar();
            }
        }

        public PdfPTable getTabla(int cols)
        {
            if(cols==12){
                return new PdfPTable(new float[]{60f,150f,30f,60f,60f,30f,60f,60f,30f,60f,60f,50f});
            }
            return new PdfPTable(new float[]{60f,150f,30f,60f,60f,30f,60f,60f,30f,60f,60f});
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
        String fecha_ini = request.getParameter("fi");
        String fecha_fin = request.getParameter("ff");
        String id_sucursal = request.getParameter("idSuc");
        
        Configuracion conf = new Configuracion(this._ip, this._puerto, this._db, this._usuario, this._clave);
        this.titulo = conf.getValor("razon_social");
        this.rep_pie = conf.getValor("rep_pie");
        String metodo_costeo = conf.getValor("metodo_costeo");
        conf.cerrar();
        
        Sucursal objSuc = new Sucursal(this._ip, this._puerto, this._db, this._usuario, this._clave);
        String sucursal = objSuc.getNombre(id_sucursal);
        objSuc.cerrar();

        imKardex objImKardex = new imKardex();
        objImKardex.imprimir(response, id_sucursal, sucursal, id, fecha_ini, fecha_fin, metodo_costeo);
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
