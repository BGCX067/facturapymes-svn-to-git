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

import java.sql.ResultSet;
import jm.web.Addons;
import jm.fac.clas.Instalacion;
import jm.web.Archivo;

/**
 *
 * @author Jorge
 */
public class pdfCroquis extends HttpServlet {
    private String _ip = null;
    private int _puerto = 5432;
    private String _db = null;
    private String _usuario = null;
    private String _clave = null;
    private String _dir = null;

    public void init(ServletConfig config) throws ServletException
    {
        this._ip = config.getServletContext().getInitParameter("_IP");
        this._puerto = Integer.parseInt(config.getServletContext().getInitParameter("_PUERTO"));
        this._db = config.getServletContext().getInitParameter("_DB");
        this._dir = config.getServletContext().getInitParameter("_DIR"); 
    }

    class croquis{
        public croquis(){}

        public void imprimir(HttpServletResponse response, ResultSet RS, String id, String path){
            response.setContentType("application/pdf");
            response.setHeader("Pragma", "no-cache");
            response.setHeader("Expires", "Mon, 01 Jan 2001 00:00:01 GMT");
            response.setHeader("Cache-Control", "no-store");
            response.setHeader("Cache-Control", "must-revalidate");
            response.setHeader("Cache-Control", "no-cache");

            /* inicio PDF */
            Document document = new Document(PageSize.A4);// paso 1
            document.setMargins(0,0,50,0); /*Izquierda, derecha, tope, pie */

            try {
                PdfWriter writer = PdfWriter.getInstance(document, response.getOutputStream()); // paso 2

                document.open(); // paso 3

                writer.addJavaScript("this.print(false);", false); // Para enviar a la impresora automáticamente.

                /* todo el cuerpo del doc es el paso 4 */

                if(RS.next()){
                    Archivo archivo = new Archivo(_ip, _puerto, _db, _usuario, _clave);
                    path = path.substring(0, path.lastIndexOf("/"));
                    String croquis = path + "/img/" + archivo.getArchivo(_dir, "tbl_instalacion", id, "nombre_img", "croquis");
                    archivo.cerrar();

                    PdfPTable tbl_det = new PdfPTable(1);
                    Image imagen = null;
                    try{
                        imagen = Image.getInstance(croquis);
                        imagen.scaleAbsolute(500, 700);
                        PdfPCell celdaImg = new PdfPCell(imagen);
                        celdaImg.setBorderWidth(0);
                        celdaImg.setPadding(0);
                        tbl_det.addCell(celdaImg);
                    }catch(Exception e){
                        tbl_det.addCell("");
                    }

                    tbl_det.addCell(Addons.setCeldaPDF("NUMERO: " + (RS.getString("id_sucursal")!=null?RS.getString("id_sucursal"):"") + "-" + 
                            (RS.getString("num_instalacion")!=null?RS.getString("num_instalacion"):"") + 
                            "\nCEDULA: " + (RS.getString("ruc")!=null?RS.getString("ruc"):"") +
                            "         CLIENTE: " + (RS.getString("razon_social")!=null?RS.getString("razon_social"):"") +
                            "\nDIRECCION DE INSTALACION: " + (RS.getString("direccion_instalacion")!=null?RS.getString("direccion_instalacion"):"") +
                            "         TELEFONO: " + (RS.getString("telefono")!=null?RS.getString("telefono"):"")
                            
                            , Font.HELVETICA, 8, Font.BOLD, Element.ALIGN_LEFT, 0));

                    document.add(tbl_det);
                }

            }catch(IllegalStateException ie){
                ie.printStackTrace();
            }catch(DocumentException de){
                de.printStackTrace();
            }catch(Exception e){
                e.printStackTrace();
            }

            document.close(); // paso 5
            /* fin PDF */
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

        Instalacion objInstalacion = new Instalacion(this._ip, this._puerto, this._db, this._usuario, this._clave);
        ResultSet res = objInstalacion.getInstalacion(id) ;

        String path = String.valueOf(request.getRequestURL());

        croquis obj = new croquis();
        obj.imprimir(response, res, id, path);

        objInstalacion.cerrar();
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
