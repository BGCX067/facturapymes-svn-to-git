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
public class pdfOrdenTrabajo extends HttpServlet {
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

    class PdfOrdenTrabajo{
        public PdfOrdenTrabajo(){}

        public void imprimir(HttpServletResponse response, ResultSet RS, String id, String path, String logo){
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

                PdfPTable tbl_titulo = new PdfPTable(new float[]{20f,80f});
                try{
                    tbl_titulo.addCell(Addons.setLogo(logo, 70, 70));
                }catch(Exception e){
                    tbl_titulo.addCell("");
                }
                

                if(RS.next()){
                    tbl_titulo.addCell(Addons.setCeldaPDF("\nORDEN DE TRABAJO DE INTERNET INALAMBRICO\n" +
                            (RS.getString("txt_tipo_trabajo")!=null?RS.getString("txt_tipo_trabajo").toUpperCase():""), Font.HELVETICA, 14, Font.BOLD, Element.ALIGN_CENTER, 0));
                    document.add(tbl_titulo);

                    document.add(new Paragraph(" "));

                    PdfPTable tbl_det = new PdfPTable(new float[]{30f,70f});
                    tbl_det.addCell(Addons.setCeldaPDF("NUMERO", Font.HELVETICA, 9, Font.BOLD, Element.ALIGN_LEFT, 1));
                    tbl_det.addCell(Addons.setCeldaPDF((RS.getString("id_sucursal")!=null?RS.getString("id_sucursal"):"") + "-" + (RS.getString("num_orden")!=null?RS.getString("num_orden"):""), Font.HELVETICA, 9, Font.NORMAL, Element.ALIGN_LEFT, 1));

                    tbl_det.addCell(Addons.setCeldaPDF("ESTADO DEL SERVICIO", Font.HELVETICA, 9, Font.BOLD, Element.ALIGN_LEFT, 1));
                    tbl_det.addCell(Addons.setCeldaPDF((RS.getString("txt_estado_servicio")!=null?RS.getString("txt_estado_servicio"):""), Font.HELVETICA, 9, Font.BOLD, Element.ALIGN_LEFT, 1));
                    
                    tbl_det.addCell(Addons.setCeldaPDF("CEDULA", Font.HELVETICA, 9, Font.BOLD, Element.ALIGN_LEFT, 1));
                    tbl_det.addCell(Addons.setCeldaPDF((RS.getString("ruc")!=null?RS.getString("ruc"):""), Font.HELVETICA, 9, Font.NORMAL, Element.ALIGN_LEFT, 1));

                    tbl_det.addCell(Addons.setCeldaPDF("CLIENTE", Font.HELVETICA, 9, Font.BOLD, Element.ALIGN_LEFT, 1));
                    tbl_det.addCell(Addons.setCeldaPDF((RS.getString("razon_social")!=null?RS.getString("razon_social"):""), Font.HELVETICA, 9, Font.NORMAL, Element.ALIGN_LEFT, 1));

                    tbl_det.addCell(Addons.setCeldaPDF("TELEFONOS:", Font.HELVETICA, 9, Font.BOLD, Element.ALIGN_LEFT, 1));
                    tbl_det.addCell(Addons.setCeldaPDF((RS.getString("telefono")!=null?RS.getString("telefono"):"")+ 
                                    "    Claro:" + (RS.getString("movil_claro")!=null?RS.getString("movil_claro"):"") + 
                                    "    Movistar:" + (RS.getString("movil_movistar")!=null?RS.getString("movil_movistar"):""), Font.HELVETICA, 9, Font.NORMAL, Element.ALIGN_LEFT, 1));
/*
                    tbl_det.addCell(Addons.setCeldaPDF("DIRECCION DEL CLIENTE", Font.HELVETICA, 9, Font.BOLD, Element.ALIGN_LEFT, 1));
                    tbl_det.addCell(Addons.setCeldaPDF((RS.getString("direccion")!=null?RS.getString("direccion"):""), Font.HELVETICA, 9, Font.NORMAL, Element.ALIGN_LEFT, 1));
*/
                    tbl_det.addCell(Addons.setCeldaPDF("CIUDAD DE INSTALACION", Font.HELVETICA, 9, Font.BOLD, Element.ALIGN_LEFT, 1));
                    tbl_det.addCell(Addons.setCeldaPDF((RS.getString("ciudad")!=null?RS.getString("ciudad"):""), Font.HELVETICA, 9, Font.NORMAL, Element.ALIGN_LEFT, 1));

                    tbl_det.addCell(Addons.setCeldaPDF("DIRECCION DE INSTALACION", Font.HELVETICA, 9, Font.BOLD, Element.ALIGN_LEFT, 1));
                    tbl_det.addCell(Addons.setCeldaPDF((RS.getString("direccion_instalacion")!=null?RS.getString("direccion_instalacion"):""), Font.HELVETICA, 9, Font.NORMAL, Element.ALIGN_LEFT, 1));

                    tbl_det.addCell(Addons.setCeldaPDF("SECTOR", Font.HELVETICA, 9, Font.BOLD, Element.ALIGN_LEFT, 1));
                    tbl_det.addCell(Addons.setCeldaPDF((RS.getString("sector")!=null?RS.getString("sector"):""), Font.HELVETICA, 9, Font.NORMAL, Element.ALIGN_LEFT, 1));
                    
                    tbl_det.addCell(Addons.setCeldaPDF("PLAN ACTUAL", Font.HELVETICA, 9, Font.BOLD, Element.ALIGN_LEFT, 1));
                    tbl_det.addCell(Addons.setCeldaPDF((RS.getString("plan")!=null?RS.getString("plan"):""), Font.HELVETICA, 9, Font.NORMAL, Element.ALIGN_LEFT, 1));

                    tbl_det.addCell(Addons.setCeldaPDF("NIVEL DE COMPARTICION", Font.HELVETICA, 9, Font.BOLD, Element.ALIGN_LEFT, 1));
                    tbl_det.addCell(Addons.setCeldaPDF((RS.getString("txt_comparticion")!=null?RS.getString("txt_comparticion"):""), Font.HELVETICA, 9, Font.NORMAL, Element.ALIGN_LEFT, 1));
                    
                    tbl_det.addCell(Addons.setCeldaPDF("DIRECCION IP", Font.HELVETICA, 9, Font.BOLD, Element.ALIGN_LEFT, 1));
                    tbl_det.addCell(Addons.setCeldaPDF((RS.getString("ip")!=null?RS.getString("ip"):""), Font.HELVETICA, 9, Font.NORMAL, Element.ALIGN_LEFT, 1));

                    
                    tbl_det.addCell(Addons.setCeldaPDF("USUARIO PPOE", Font.HELVETICA, 9, Font.BOLD, Element.ALIGN_LEFT, 1));
                    tbl_det.addCell(Addons.setCeldaPDF((RS.getString("radusername")!=null?RS.getString("radusername"):""), Font.HELVETICA, 9, Font.NORMAL, Element.ALIGN_LEFT, 1));

                    tbl_det.addCell(Addons.setCeldaPDF("CLAVE PPOE", Font.HELVETICA, 9, Font.BOLD, Element.ALIGN_LEFT, 1));
                    tbl_det.addCell(Addons.setCeldaPDF((RS.getString("radclave")!=null?RS.getString("radclave"):""), Font.HELVETICA, 9, Font.NORMAL, Element.ALIGN_LEFT, 1));

                    tbl_det.addCell(Addons.setCeldaPDF("CLAVE DISPOSITIVO", Font.HELVETICA, 9, Font.BOLD, Element.ALIGN_LEFT, 1));
                    tbl_det.addCell(Addons.setCeldaPDF((RS.getString("deviceclave")!=null?RS.getString("deviceclave"):""), Font.HELVETICA, 9, Font.NORMAL, Element.ALIGN_LEFT, 1));
                            
                            
                    tbl_det.addCell(Addons.setCeldaPDF("RECEPTOR DE SEÑAL", Font.HELVETICA, 9, Font.BOLD, Element.ALIGN_LEFT, 1));
                    tbl_det.addCell(Addons.setCeldaPDF((RS.getString("receptor")!=null?RS.getString("receptor"):""), Font.HELVETICA, 9, Font.NORMAL, Element.ALIGN_LEFT, 1));

                    tbl_det.addCell(Addons.setCeldaPDF("ANTENA ACOPLADA", Font.HELVETICA, 9, Font.BOLD, Element.ALIGN_LEFT, 1));
                    tbl_det.addCell(Addons.setCeldaPDF((RS.getString("antena_acoplada")!=null?RS.getString("antena_acoplada"):""), Font.HELVETICA, 9, Font.NORMAL, Element.ALIGN_LEFT, 1));

                    tbl_det.addCell(Addons.setCeldaPDF("FECHA DE REPORTE", Font.HELVETICA, 9, Font.BOLD, Element.ALIGN_LEFT, 1));
                    tbl_det.addCell(Addons.setCeldaPDF((RS.getString("fecha_reporte")!=null?Fecha.ISOaSQL(RS.getString("fecha_reporte")):""), Font.HELVETICA, 9, Font.NORMAL, Element.ALIGN_LEFT, 1));
                    
                    tbl_det.addCell(Addons.setCeldaPDF("FECHA DE REALIZACION", Font.HELVETICA, 9, Font.BOLD, Element.ALIGN_LEFT, 1));
                    tbl_det.addCell(Addons.setCeldaPDF((RS.getString("fecha_realizacion")!=null?Fecha.ISOaSQL(RS.getString("fecha_realizacion")):""), Font.HELVETICA, 9, Font.NORMAL, Element.ALIGN_LEFT, 1));

                    //tbl_det.addCell(Addons.setCeldaPDF("TIPO DE TRABAJO", Font.HELVETICA, 9, Font.BOLD, Element.ALIGN_LEFT, 1));
                    //tbl_det.addCell(Addons.setCeldaPDF((RS.getString("txt_tipo_trabajo")!=null?RS.getString("txt_tipo_trabajo"):""), Font.HELVETICA, 9, Font.NORMAL, Element.ALIGN_LEFT, 1));
                    
                    tbl_det.addCell(Addons.setCeldaPDF("RESPONSABLE", Font.HELVETICA, 9, Font.BOLD, Element.ALIGN_LEFT, 1));
                    tbl_det.addCell(Addons.setCeldaPDF((RS.getString("responsable")!=null?RS.getString("responsable"):""), Font.HELVETICA, 9, Font.NORMAL, Element.ALIGN_LEFT, 1));

                    tbl_det.addCell(Addons.setCeldaPDF("DIAGNOSTICO TECNICO", Font.HELVETICA, 9, Font.BOLD, Element.ALIGN_LEFT, 1, 3, 2));
                    tbl_det.addCell(Addons.setCeldaPDF((RS.getString("diagnostico_tecnico")!=null?RS.getString("diagnostico_tecnico"):""), Font.HELVETICA, 9, Font.NORMAL, Element.ALIGN_JUSTIFIED, 1, 3, 2));

                    tbl_det.addCell(Addons.setCeldaPDF("DESCRIPCION DE MATERIALES UTILIZADOS", Font.HELVETICA, 9, Font.BOLD, Element.ALIGN_LEFT, 1, 3, 2));
                    tbl_det.addCell(Addons.setCeldaPDF("\n \n \n \n \n \n \n ", Font.HELVETICA, 9, Font.NORMAL, Element.ALIGN_LEFT, 1, 3, 2));

                    tbl_det.addCell(Addons.setCeldaPDF("OBSERVACIONES Y RECOMENDACIONES", Font.HELVETICA, 9, Font.BOLD, Element.ALIGN_LEFT, 1, 3, 2));
                    tbl_det.addCell(Addons.setCeldaPDF("\n \n \n \n \n ", Font.HELVETICA, 9, Font.NORMAL, Element.ALIGN_LEFT, 1, 3, 2));

                    tbl_det.addCell(Addons.setCeldaPDF("INCONVENIENTE SOLUCIONADO", Font.HELVETICA, 9, Font.BOLD, Element.ALIGN_LEFT, 1));
                    tbl_det.addCell(Addons.setCeldaPDF(" ___ SI \n ___ NO\n ", Font.HELVETICA, 9, Font.NORMAL, Element.ALIGN_LEFT, 1));

                    tbl_det.addCell(Addons.setCeldaPDF("CONFORME CON EL TRABAJO REALIZADO", Font.HELVETICA, 9, Font.BOLD, Element.ALIGN_LEFT, 1));
                    tbl_det.addCell(Addons.setCeldaPDF(" ___ SI \n ___ NO\n ", Font.HELVETICA, 9, Font.NORMAL, Element.ALIGN_LEFT, 1));

                    tbl_det.addCell(Addons.setCeldaPDF("CONFORME CON LA ATENCION RECIBIDA", Font.HELVETICA, 9, Font.BOLD, Element.ALIGN_LEFT, 1));
                    tbl_det.addCell(Addons.setCeldaPDF(" ___ SI \n ___ NO\n ", Font.HELVETICA, 9, Font.NORMAL, Element.ALIGN_LEFT, 1));

                    document.add(tbl_det);

                    document.add(new Paragraph(" "));
                    document.add(new Paragraph(" "));
                    document.add(new Paragraph(" "));

                    PdfPTable tbl_firma = new PdfPTable(3);
                    tbl_firma.addCell(Addons.setCeldaPDF("_______________________", Font.HELVETICA, 10, Font.NORMAL, Element.ALIGN_LEFT, 0));
                    tbl_firma.addCell(Addons.setCeldaPDF("_______________________", Font.HELVETICA, 10, Font.NORMAL, Element.ALIGN_LEFT, 0));
                    tbl_firma.addCell(Addons.setCeldaPDF("_______________________", Font.HELVETICA, 10, Font.NORMAL, Element.ALIGN_LEFT, 0));
                    tbl_firma.addCell(Addons.setCeldaPDF("REALIZADO", Font.HELVETICA, 9, Font.NORMAL, Element.ALIGN_LEFT, 0));
                    tbl_firma.addCell(Addons.setCeldaPDF("APROBADO", Font.HELVETICA, 9, Font.NORMAL, Element.ALIGN_LEFT, 0));
                    tbl_firma.addCell(Addons.setCeldaPDF("CLIENTE", Font.HELVETICA, 9, Font.NORMAL, Element.ALIGN_LEFT, 0));
                    document.add(tbl_firma);



                    document.newPage();


                    String id_instalacion = RS.getString("id_instalacion")!=null?RS.getString("id_instalacion"):"";
                    Archivo archivo = new Archivo(_ip, _puerto, _db, _usuario, _clave);
                    //path = path.substring(0, path.lastIndexOf("/"));
                    String croquis = path + "/img/" + archivo.getArchivo(_dir, "tbl_instalacion", id_instalacion, "nombre_img", "croquis");
                    archivo.cerrar();

                    PdfPTable tbl_croquis = new PdfPTable(1);
                    Image imagen = null;
                    try{
                        imagen = Image.getInstance(croquis);
                        imagen.scaleAbsolute(500, 700);
                        PdfPCell celdaImg = new PdfPCell(imagen);
                        celdaImg.setBorderWidth(0);
                        celdaImg.setPadding(0);
                        tbl_croquis.addCell(celdaImg);
                        tbl_croquis.addCell(Addons.setCeldaPDF("NUMERO: " + (RS.getString("id_sucursal")!=null?RS.getString("id_sucursal"):"") + "-" +
                            (RS.getString("num_orden")!=null?RS.getString("num_orden"):"") +
                            "\nCEDULA: " + (RS.getString("ruc")!=null?RS.getString("ruc"):"") +
                            "         CLIENTE: " + (RS.getString("razon_social")!=null?RS.getString("razon_social"):"") +
                            "\nDIRECCION DE INSTALACION: " + (RS.getString("direccion_instalacion")!=null?RS.getString("direccion_instalacion"):"") +
                            "         TELEFONO: " + (RS.getString("telefono")!=null?RS.getString("telefono"):"")

                            , Font.HELVETICA, 8, Font.BOLD, Element.ALIGN_LEFT, 0));

                        document.add(tbl_croquis);
                    }catch(Exception e){
                        //tbl_croquis.addCell(" ");
                    }

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

        OrdenTrabajo objOrdenTrabajo = new OrdenTrabajo(this._ip, this._puerto, this._db, this._usuario, this._clave);
        ResultSet res = objOrdenTrabajo.getOrdenTrabajo(id);
        objOrdenTrabajo.impresion(id);

        String path = String.valueOf(request.getRequestURL());
        path = path.substring(0, path.lastIndexOf("/"));
        Archivo archivo = new Archivo(this._ip, this._puerto, this._db, this._usuario, this._clave);
        String logo = path + "/img/" + archivo.getArchivo(this._dir, 1);
        archivo.cerrar();

        PdfOrdenTrabajo obj = new PdfOrdenTrabajo();
        obj.imprimir(response, res, id, path, logo);

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
