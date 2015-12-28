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

import jm.adm.clas.Configuracion;
import jm.web.Addons;
import jm.inv.clas.Pedido;

import java.sql.ResultSet;
import java.sql.SQLException;
import jm.web.Fecha;

/**
 *
 * @author Jorge
 */
public class pdfPedido extends HttpServlet {
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

    class imPedido{
        public imPedido(){}

        public void imprimir(HttpServletResponse response, String id){
            response.setContentType("application/pdf");
            response.setHeader("Pragma", "no-cache");
            response.setHeader("Expires", "Mon, 01 Jan 2001 00:00:01 GMT");
            response.setHeader("Cache-Control", "no-store");
            response.setHeader("Cache-Control", "must-revalidate");
            response.setHeader("Cache-Control", "no-cache");

            String [] cabTabla = null;
            float [] anchoTabla = null;
            ResultSet rsPedido = null;
            ResultSet rsPedidoDetalle = null;
            Pedido objPedido = new Pedido(_ip, _puerto, _db, _usuario, _clave);
            try {
                rsPedido = objPedido.getPedido(id);
                rsPedidoDetalle = objPedido.getPedidoDetalle(id);

                cabTabla = new String [] {"CODIGO", "DESCRIPCION", "CANTIDAD"};
                anchoTabla = new float[]{55f,145f,40f};

                /* inicio PDF */
                Document document = new Document(PageSize.A4);// paso 1
                document.setMargins(0,0,50,80); /*Izquierda, derecha, tope, pie */
                try{
                    PdfWriter writer = PdfWriter.getInstance(document, response.getOutputStream()); // paso 2

                    document.open(); // paso 3

                    if(rsPedido.next()){

                        writer.addJavaScript("this.print(false);", false); // Para enviar a la impresora automáticamente.

                        /* todo el cuerpo del doc es el paso 4 */

                        //document.add(Addons.setCabecera(logo, titulo, ruc, "", direccion, ""));

                        PdfPTable tbl_cab = new PdfPTable(1);
                        tbl_cab.addCell(Addons.setCeldaPDF(titulo, Font.HELVETICA, 14, Font.NORMAL, Element.ALIGN_CENTER, 0));
                        tbl_cab.addCell(Addons.setCeldaPDF("ORDEN DE PEDIDO DE MERCADERIA", Font.HELVETICA, 14, Font.NORMAL, Element.ALIGN_CENTER, 0));
                        tbl_cab.addCell(Addons.setCeldaPDF("No.   " + ((rsPedido.getString("num_pedido")!=null) ? rsPedido.getString("num_pedido") : ""), Font.HELVETICA, 12, Font.NORMAL, Element.ALIGN_RIGHT, 0));
                        document.add(tbl_cab);



                        PdfPTable tbl_encab = new PdfPTable(1);

                        tbl_encab.addCell(Addons.setCeldaPDF("Sucursal: " + ((rsPedido.getString("sucursal")!=null) ? rsPedido.getString("sucursal") : ""), Font.HELVETICA, 10, Font.NORMAL, Element.ALIGN_LEFT, 0));
                        tbl_encab.addCell(Addons.setCeldaPDF("Usuario: " + ((rsPedido.getString("usuario")!=null) ? rsPedido.getString("usuario") : ""), Font.HELVETICA, 10, Font.NORMAL, Element.ALIGN_LEFT, 0));
                        tbl_encab.addCell(Addons.setCeldaPDF("Fecha del pedido: " + ((rsPedido.getString("fecha_pedido")!=null) ? Fecha.ISOaSQL(rsPedido.getString("fecha_pedido")) : ""), Font.HELVETICA, 10, Font.NORMAL, Element.ALIGN_LEFT, 0));

                        document.add(tbl_encab);


                        document.add(Addons.setCabeceraTabla(cabTabla, anchoTabla));

                        PdfPTable tbl_det = new PdfPTable(anchoTabla);
                        try{
                            while(rsPedidoDetalle.next()){
                                tbl_det.addCell(Addons.setCeldaPDF(((rsPedidoDetalle.getString("codigo")!=null) ? rsPedidoDetalle.getString("codigo") : ""), Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_LEFT, 1));
                                tbl_det.addCell(Addons.setCeldaPDF(((rsPedidoDetalle.getString("descripcion")!=null) ? rsPedidoDetalle.getString("descripcion") : ""), Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_LEFT, 1));
                                tbl_det.addCell(Addons.setCeldaPDF(((rsPedidoDetalle.getString("cantidad")!=null) ? rsPedidoDetalle.getString("cantidad") : ""), Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_LEFT, 1));
                            }

                            rsPedidoDetalle.close();
                        }catch(Exception e){
                            e.printStackTrace();
                        }
                        document.add(tbl_det);
                    }


                    document.add(new Paragraph(" "));
                    document.add(new Paragraph(" "));
                    document.add(new Paragraph(" "));


                    PdfPTable tbl_firmas = new PdfPTable(2);
                    tbl_firmas.addCell(Addons.setCeldaPDF("RESPONSABLE DEL PEDIDO", Font.HELVETICA, 10, Font.NORMAL, Element.ALIGN_CENTER, 0));
                    tbl_firmas.addCell(Addons.setCeldaPDF("AUTORIZADO", Font.HELVETICA, 10, Font.NORMAL, Element.ALIGN_CENTER, 0));
                    document.add(tbl_firmas);


                    rsPedido.close();
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
                    rsPedido.close();
                    rsPedidoDetalle.close();
                }catch(SQLException e){
                    e.printStackTrace();
                }
                objPedido.cerrar();
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

        imPedido objImTraspaso = new imPedido();
        objImTraspaso.imprimir(response, id);
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
