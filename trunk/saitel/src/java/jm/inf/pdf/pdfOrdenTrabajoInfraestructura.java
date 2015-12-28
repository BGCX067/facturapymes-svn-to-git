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

package jm.inf.pdf;

import java.io.*;

import javax.servlet.*;
import javax.servlet.http.*;

import com.lowagie.text.pdf.*;
import com.lowagie.text.*;

import java.sql.ResultSet;
import jm.web.Addons;
import jm.inf.clas.OrdenTrabajo;
import jm.web.Archivo;
import jm.web.Fecha;

/**
 *
 * @author Jorge
 */
public class pdfOrdenTrabajoInfraestructura extends HttpServlet {
    private String _ip = null;
    private int _puerto = 5432;
    private String _db = null;
    private String _dir = null;

    public void init(ServletConfig config) throws ServletException
    {
        this._ip = config.getServletContext().getInitParameter("_IP");
        this._puerto = Integer.parseInt(config.getServletContext().getInitParameter("_PUERTO"));
        this._db = config.getServletContext().getInitParameter("_DB");
        this._dir = config.getServletContext().getInitParameter("_DIR");
    }

    class PdfOrdenTrabajo{
        public PdfOrdenTrabajo(){}

        public void imprimir(HttpServletResponse response, ResultSet RS, ResultSet resTar,  String id, String path, String logo){
            response.setContentType("application/pdf");
            response.setHeader("Pragma", "no-cache");
            response.setHeader("Expires", "Mon, 01 Jan 2001 00:00:01 GMT");
            response.setHeader("Cache-Control", "no-store");
            response.setHeader("Cache-Control", "must-revalidate");
            response.setHeader("Cache-Control", "no-cache");

            /* inicio PDF */
            Document document = new Document(PageSize.A4.rotate());// paso 1
            document.setMargins(0,0,50,0); /*Izquierda, derecha, tope, pie */

            try {
                PdfWriter writer = PdfWriter.getInstance(document, response.getOutputStream()); // paso 2

                document.open(); // paso 3

                writer.addJavaScript("this.print(false);", false); // Para enviar a la impresora automáticamente.

                /* todo el cuerpo del doc es el paso 4 */


                if(RS.next()){
                    PdfPTable tbl_titulo = new PdfPTable(new float[]{20f,80f});
                    try{
                        tbl_titulo.addCell(Addons.setLogo(logo, 70, 70));
                    }catch(Exception e){
                        tbl_titulo.addCell("");
                    }
                    
                    tbl_titulo.addCell(Addons.setCeldaPDF("\nORDEN DE TRABAJO DE \n" +
                            (RS.getString("txt_tipo_trabajo")!=null?RS.getString("txt_tipo_trabajo").toUpperCase():""), Font.HELVETICA, 14, Font.BOLD, Element.ALIGN_CENTER, 0));
                    document.add(tbl_titulo);

                    document.add(new Paragraph(" "));

                    PdfPTable tbl_det = new PdfPTable(new float[]{30f,70f});
                    tbl_det.addCell(Addons.setCeldaPDF("NUMERO", Font.HELVETICA, 9, Font.BOLD, Element.ALIGN_LEFT, 1));
                    tbl_det.addCell(Addons.setCeldaPDF((RS.getString("id_sucursal")!=null?RS.getString("id_sucursal"):"") + "-" + (RS.getString("num_orden")!=null?RS.getString("num_orden"):""), Font.HELVETICA, 9, Font.NORMAL, Element.ALIGN_LEFT, 1));

                    tbl_det.addCell(Addons.setCeldaPDF("FECHA DE REPORTE", Font.HELVETICA, 9, Font.BOLD, Element.ALIGN_LEFT, 1));
                    tbl_det.addCell(Addons.setCeldaPDF((RS.getString("fecha_reporte")!=null?Fecha.ISOaSQL(RS.getString("fecha_reporte")):""), Font.HELVETICA, 9, Font.NORMAL, Element.ALIGN_LEFT, 1));
                    
                    tbl_det.addCell(Addons.setCeldaPDF("FECHA DE REALIZACION", Font.HELVETICA, 9, Font.BOLD, Element.ALIGN_LEFT, 1));
                    tbl_det.addCell(Addons.setCeldaPDF((RS.getString("fecha_realizacion")!=null?Fecha.ISOaSQL(RS.getString("fecha_realizacion")):""), Font.HELVETICA, 9, Font.NORMAL, Element.ALIGN_LEFT, 1));

                    tbl_det.addCell(Addons.setCeldaPDF("RESPONSABLE", Font.HELVETICA, 9, Font.BOLD, Element.ALIGN_LEFT, 1));
                    tbl_det.addCell(Addons.setCeldaPDF((RS.getString("responsable")!=null?RS.getString("responsable"):""), Font.HELVETICA, 9, Font.NORMAL, Element.ALIGN_LEFT, 1));

                    tbl_det.addCell(Addons.setCeldaPDF("DIAGNOSTICO TECNICO", Font.HELVETICA, 9, Font.BOLD, Element.ALIGN_LEFT, 1, 3, 2));
                    tbl_det.addCell(Addons.setCeldaPDF((RS.getString("diagnostico_tecnico")!=null?RS.getString("diagnostico_tecnico"):""), Font.HELVETICA, 9, Font.NORMAL, Element.ALIGN_JUSTIFIED, 1, 3, 2));
                    
                    document.add(tbl_det);
                    
                    try{
                        PdfPTable tbl_det1 = new PdfPTable(new float[]{50f, 30f, 90f, 90f});
                        tbl_det1.addCell(Addons.setCeldaPDF("TAREA", Font.HELVETICA, 9, Font.BOLD, Element.ALIGN_CENTER, 1, 3));
                        tbl_det1.addCell(Addons.setCeldaPDF("SOLUCIONADO", Font.HELVETICA, 9, Font.BOLD, Element.ALIGN_CENTER, 1, 3));
                        tbl_det1.addCell(Addons.setCeldaPDF("EQUIPOS / MATERIALES", Font.HELVETICA, 9, Font.BOLD, Element.ALIGN_CENTER, 1, 3));
                        tbl_det1.addCell(Addons.setCeldaPDF("OBSERVACION", Font.HELVETICA, 9, Font.BOLD, Element.ALIGN_CENTER, 1, 3));
                        while(resTar.next()){
                            tbl_det1.addCell(Addons.setCeldaPDF((resTar.getString("tarea")!=null?resTar.getString("tarea"):"") + " \n \n \n \n ", Font.HELVETICA, 9, Font.NORMAL, Element.ALIGN_JUSTIFIED, 1, 3));
                            tbl_det1.addCell(Addons.setCeldaPDF("___ SI  ___ NO \n \n \n ", Font.HELVETICA, 9, Font.NORMAL, Element.ALIGN_LEFT, 1, 3));
                            tbl_det1.addCell(Addons.setCeldaPDF("\n \n \n \n \n ", Font.HELVETICA, 9, Font.NORMAL, Element.ALIGN_LEFT, 1, 3));
                            tbl_det1.addCell(Addons.setCeldaPDF("\n \n \n \n \n ", Font.HELVETICA, 9, Font.NORMAL, Element.ALIGN_LEFT, 1, 3));
                        }
                        document.add(tbl_det1);
                    }catch(Exception ex){
                        ex.printStackTrace();
                    }                    
                    
                    /*
                     * tbl_det.addCell(Addons.setCeldaPDF("OBSERVACIONES Y RECOMENDACIONES", Font.HELVETICA, 9, Font.BOLD, Element.ALIGN_LEFT, 1, 3, 2));
                    tbl_det.addCell(Addons.setCeldaPDF("\n \n \n \n \n ", Font.HELVETICA, 9, Font.NORMAL, Element.ALIGN_LEFT, 1, 3, 2));

                    tbl_det.addCell(Addons.setCeldaPDF("INCONVENIENTE SOLUCIONADO", Font.HELVETICA, 9, Font.BOLD, Element.ALIGN_LEFT, 1));
                    tbl_det.addCell(Addons.setCeldaPDF(" ___ SI \n ___ NO\n ", Font.HELVETICA, 9, Font.NORMAL, Element.ALIGN_LEFT, 1));
                    */
                    

                    document.add(new Paragraph(" "));
                    document.add(new Paragraph(" "));
                    document.add(new Paragraph(" "));

                    PdfPTable tbl_firma = new PdfPTable(2);
                    tbl_firma.addCell(Addons.setCeldaPDF("_______________________", Font.HELVETICA, 10, Font.NORMAL, Element.ALIGN_LEFT, 0));
                    tbl_firma.addCell(Addons.setCeldaPDF("_______________________", Font.HELVETICA, 10, Font.NORMAL, Element.ALIGN_LEFT, 0));
                    tbl_firma.addCell(Addons.setCeldaPDF("REALIZADO", Font.HELVETICA, 9, Font.NORMAL, Element.ALIGN_LEFT, 0));
                    tbl_firma.addCell(Addons.setCeldaPDF("APROBADO", Font.HELVETICA, 9, Font.NORMAL, Element.ALIGN_LEFT, 0));
                    document.add(tbl_firma);

                }else{
                    document.add(new Paragraph("NO SE HAN ENCONTRADO DATOS PARA MOSTRAR"));
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
        String usuario = (String)sesion.getAttribute("usuario");
        String clave = (String)sesion.getAttribute("clave");
        
        //PrintWriter out = response.getWriter();

        String id = request.getParameter("id");

        OrdenTrabajo objOrdenTrabajo = new OrdenTrabajo(this._ip, this._puerto, this._db, usuario, clave);
        ResultSet res = objOrdenTrabajo.getOrdenTrabajoInfra(id);
        ResultSet resTar = objOrdenTrabajo.getTareasOrden(id);
        objOrdenTrabajo.impresion(id);

        String path = String.valueOf(request.getRequestURL());
        path = path.substring(0, path.lastIndexOf("/"));
        Archivo archivo = new Archivo(this._ip, this._puerto, this._db, usuario, clave);
        String logo = path + "/img/" + archivo.getArchivo(this._dir, 1);
        archivo.cerrar();

        PdfOrdenTrabajo obj = new PdfOrdenTrabajo();
        obj.imprimir(response, res, resTar, id, path, logo);

        objOrdenTrabajo.cerrar();
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
