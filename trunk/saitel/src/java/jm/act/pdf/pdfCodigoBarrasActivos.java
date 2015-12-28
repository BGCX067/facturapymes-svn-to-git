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
import java.text.DecimalFormat;
import java.text.NumberFormat;
import jm.act.clas.Activo;

/**
 *
 * @author Jorge
 */
public class pdfCodigoBarrasActivos extends HttpServlet {
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

    class imActivo extends PdfPageEventHelper{
        public imActivo(){}

        public void imprimir(HttpServletResponse response, String dni){
            response.setContentType("application/pdf");
            response.setHeader("Pragma", "no-cache");
            response.setHeader("Expires", "Mon, 01 Jan 2001 00:00:01 GMT");
            response.setHeader("Cache-Control", "no-store");
            response.setHeader("Cache-Control", "must-revalidate");
            response.setHeader("Cache-Control", "no-cache");

            Activo objActivo = new Activo(_ip, _puerto, _db, _usuario, _clave);
            String codigo = "";
            String descripcion = "";
            ResultSet rsActivos = objActivo.getActivos(dni);

            try {
                /* inicio PDF */
                
                Rectangle pageSize = new Rectangle(this.getConvertCmsToPoints(4.8f), this.getConvertCmsToPoints(2.4f)); //ancho y alto
                
                Document document = new Document(pageSize);// paso 1
                
                document.setMargins(5,5,0,0); /*Izquierda, derecha, tope, pie */
                try{
                    PdfWriter writer = PdfWriter.getInstance(document, response.getOutputStream()); // paso 2
                    //writer.setPageEvent(new pdfCodigoBarrasActivos.imActivo());

                    document.open(); // paso 3


                    writer.addJavaScript("this.print(false);", false); // Para enviar a la impresora automáticamente.

                    /* todo el cuerpo del doc es el paso 4 */

                    try{
                        while(rsActivos.next()){
                            codigo = (rsActivos.getString("codigo_activo")!=null) ? rsActivos.getString("codigo_activo") : "";
                            descripcion = (rsActivos.getString("descripcion")!=null) ? rsActivos.getString("descripcion") : "";
                            document.add(Addons.setParrafoPDF("ACTIVO FIJO - SAITEL", Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_CENTER));
                            document.add(this.getBarcode(writer, codigo));
                            document.add(Addons.setParrafoPDF("DESCRIPCION: "+descripcion, Font.HELVETICA, 6, Font.NORMAL, Element.ALIGN_LEFT));
                            document.newPage();
                        }
                        rsActivos.close();
                    }catch(Exception e){
                        e.printStackTrace();
                    }
                    
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
        
        private Image getBarcode(PdfWriter pdfWriter, String codigo) {
            PdfContentByte cimg = pdfWriter.getDirectContent();
            Barcode128 code128 = new Barcode128();
            code128.setCode(codigo);
            code128.setCodeType(Barcode128.CODE128);
            code128.setTextAlignment(Element.ALIGN_CENTER);
            Image image = code128.createImageWithBarcode(cimg, null, null);
            //float scaler = ((document.getPageSize().getWidth() - document.leftMargin()  - document.rightMargin() - 0) / image.getWidth()) * 70;
            //image.scalePercent(scaler);
            //image.scaleAbsolute(90f, 35f);
            image.scalePercent(70f);
            image.setAlignment(Element.ALIGN_CENTER);
            return image;
        }
        
        private float getConvertCmsToPoints(float cm) {
            return cm * 28.4527559067f;
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

        String dni = request.getParameter("dni");

        pdfCodigoBarrasActivos.imActivo objImActivo = new pdfCodigoBarrasActivos.imActivo();
        objImActivo.imprimir(response, dni);
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
