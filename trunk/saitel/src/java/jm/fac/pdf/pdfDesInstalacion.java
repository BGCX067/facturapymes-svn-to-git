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
import jm.fac.clas.PlanServicio;
import jm.fac.clas.Sector;
import jm.web.Archivo;

/**
 *
 * @author Jorge
 */
public class pdfDesInstalacion extends HttpServlet {
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

    class PdfInstalacion{
        public PdfInstalacion(){}

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
                tbl_titulo.addCell(Addons.setCeldaPDF("\nDES-INSTALACION DE INTERNET INALAMBRICO", Font.HELVETICA, 14, Font.BOLD, Element.ALIGN_CENTER, 0));
                document.add(tbl_titulo);

                document.add(new Paragraph(" "));

                if(RS.next()){
                    String id_sector = RS.getString("id_sector")!=null?RS.getString("id_sector"):"-1";
                    String sector = "";
                    Sector objSector = new Sector(_ip, _puerto, _db, _usuario, _clave);
                    try{
                        ResultSet rsContrato = objSector.getSector(id_sector);
                        if(rsContrato.next()){
                            sector = rsContrato.getString("sector")!=null ? rsContrato.getString("sector") : "";
                            rsContrato.close();
                        }
                    }catch(Exception ec){
                        ec.printStackTrace();
                    }finally{
                        objSector.cerrar();
                    }

                    String id_plan_contratado = RS.getString("id_plan_establecido")!=null?RS.getString("id_plan_establecido"):"-1";
                    String plan = "";
                    PlanServicio objPlanServicio = new PlanServicio(_ip, _puerto, _db, _usuario, _clave);
                    try{
                        ResultSet rsPlanServicio = objPlanServicio.getVelocidad(id_plan_contratado);
                        if(rsPlanServicio.next()){
                            plan = rsPlanServicio.getString("plan")!=null ? rsPlanServicio.getString("plan") : "";
                            rsPlanServicio.close();
                        }
                    }catch(Exception ep){
                        ep.printStackTrace();
                    }finally{
                        objPlanServicio.cerrar();
                    }

                    PdfPTable tbl_det = new PdfPTable(new float[]{40f,60f});
                    tbl_det.addCell(Addons.setCeldaPDF("NUMERO", Font.HELVETICA, 10, Font.BOLD, Element.ALIGN_LEFT, 1));
                    tbl_det.addCell(Addons.setCeldaPDF((RS.getString("id_sucursal")!=null?RS.getString("id_sucursal"):"") + "-" + (RS.getString("num_desinstalacion")!=null?RS.getString("num_desinstalacion"):""), Font.HELVETICA, 10, Font.NORMAL, Element.ALIGN_LEFT, 1));

                    tbl_det.addCell(Addons.setCeldaPDF("CEDULA", Font.HELVETICA, 10, Font.BOLD, Element.ALIGN_LEFT, 1));
                    tbl_det.addCell(Addons.setCeldaPDF((RS.getString("ruc")!=null?RS.getString("ruc"):""), Font.HELVETICA, 10, Font.NORMAL, Element.ALIGN_LEFT, 1));

                    tbl_det.addCell(Addons.setCeldaPDF("CLIENTE", Font.HELVETICA, 10, Font.BOLD, Element.ALIGN_LEFT, 1));
                    tbl_det.addCell(Addons.setCeldaPDF((RS.getString("razon_social")!=null?RS.getString("razon_social"):""), Font.HELVETICA, 10, Font.NORMAL, Element.ALIGN_LEFT, 1));

                    tbl_det.addCell(Addons.setCeldaPDF("TELEFONO", Font.HELVETICA, 10, Font.BOLD, Element.ALIGN_LEFT, 1));
                    tbl_det.addCell(Addons.setCeldaPDF((RS.getString("telefono")!=null?RS.getString("telefono"):"") + 
                                    "    Claro:" + (RS.getString("movil_claro")!=null?RS.getString("movil_claro"):"") + 
                                    "    Movistar:" + (RS.getString("movil_movistar")!=null?RS.getString("movil_movistar"):""), Font.HELVETICA, 10, Font.NORMAL, Element.ALIGN_LEFT, 1));

                    tbl_det.addCell(Addons.setCeldaPDF("DIRECCION DEL CLIENTE", Font.HELVETICA, 10, Font.BOLD, Element.ALIGN_LEFT, 1));
                    tbl_det.addCell(Addons.setCeldaPDF((RS.getString("direccion")!=null?RS.getString("direccion"):""), Font.HELVETICA, 10, Font.NORMAL, Element.ALIGN_LEFT, 1));

                    tbl_det.addCell(Addons.setCeldaPDF("CIUDAD DEL CLIENTE", Font.HELVETICA, 10, Font.BOLD, Element.ALIGN_LEFT, 1));
                    tbl_det.addCell(Addons.setCeldaPDF((RS.getString("ciudad")!=null?RS.getString("ciudad"):""), Font.HELVETICA, 10, Font.NORMAL, Element.ALIGN_LEFT, 1));

                    tbl_det.addCell(Addons.setCeldaPDF("DIRECCION DE INSTALACION", Font.HELVETICA, 10, Font.BOLD, Element.ALIGN_LEFT, 1));
                    tbl_det.addCell(Addons.setCeldaPDF((RS.getString("direccion_instalacion")!=null?RS.getString("direccion_instalacion"):""), Font.HELVETICA, 10, Font.NORMAL, Element.ALIGN_LEFT, 1));

                    tbl_det.addCell(Addons.setCeldaPDF("FECHA DE DES-INSTALACION", Font.HELVETICA, 10, Font.BOLD, Element.ALIGN_LEFT, 1));
                    tbl_det.addCell(Addons.setCeldaPDF((RS.getString("fecha_desinstalacion")!=null?RS.getString("fecha_desinstalacion"):""), Font.HELVETICA, 10, Font.NORMAL, Element.ALIGN_LEFT, 1));

                    tbl_det.addCell(Addons.setCeldaPDF("SECTOR", Font.HELVETICA, 10, Font.BOLD, Element.ALIGN_LEFT, 1));
                    tbl_det.addCell(Addons.setCeldaPDF(sector, Font.HELVETICA, 10, Font.NORMAL, Element.ALIGN_LEFT, 1));

                    tbl_det.addCell(Addons.setCeldaPDF("DIRECCION IP", Font.HELVETICA, 10, Font.BOLD, Element.ALIGN_LEFT, 1));
                    tbl_det.addCell(Addons.setCeldaPDF((RS.getString("ip")!=null?RS.getString("ip"):""), Font.HELVETICA, 10, Font.NORMAL, Element.ALIGN_LEFT, 1));

                    tbl_det.addCell(Addons.setCeldaPDF("DIRECCION MAC", Font.HELVETICA, 10, Font.BOLD, Element.ALIGN_LEFT, 1));
                    tbl_det.addCell(Addons.setCeldaPDF((RS.getString("mac")!=null?RS.getString("mac"):""), Font.HELVETICA, 10, Font.NORMAL, Element.ALIGN_LEFT, 1));

                    tbl_det.addCell(Addons.setCeldaPDF("RECEPTOR DE SEÑAL", Font.HELVETICA, 10, Font.BOLD, Element.ALIGN_LEFT, 1));
                    tbl_det.addCell(Addons.setCeldaPDF((RS.getString("receptor")!=null?RS.getString("receptor"):""), Font.HELVETICA, 10, Font.NORMAL, Element.ALIGN_LEFT, 1));

                    tbl_det.addCell(Addons.setCeldaPDF("PORCENTAJE DE SEÑAL", Font.HELVETICA, 10, Font.BOLD, Element.ALIGN_LEFT, 1));
                    tbl_det.addCell(Addons.setCeldaPDF((RS.getString("porcentaje_senal")!=null?RS.getString("porcentaje_senal"):""), Font.HELVETICA, 10, Font.NORMAL, Element.ALIGN_LEFT, 1));

                    tbl_det.addCell(Addons.setCeldaPDF("ANTENA DE TRASMISION ACOPLADA", Font.HELVETICA, 10, Font.BOLD, Element.ALIGN_LEFT, 1));
                    tbl_det.addCell(Addons.setCeldaPDF((RS.getString("antena_acoplada")!=null?RS.getString("antena_acoplada"):""), Font.HELVETICA, 10, Font.NORMAL, Element.ALIGN_LEFT, 1));

                    tbl_det.addCell(Addons.setCeldaPDF("PLAN ESTABLECIDO", Font.HELVETICA, 10, Font.BOLD, Element.ALIGN_LEFT, 1));
                    tbl_det.addCell(Addons.setCeldaPDF(plan, Font.HELVETICA, 10, Font.NORMAL, Element.ALIGN_LEFT, 1));

                    tbl_det.addCell(Addons.setCeldaPDF("NIVEL DE COMPARTICION", Font.HELVETICA, 10, Font.BOLD, Element.ALIGN_LEFT, 1));
                    tbl_det.addCell(Addons.setCeldaPDF((RS.getString("comparticion")!=null?RS.getString("comparticion"):"") + " - 1", Font.HELVETICA, 10, Font.NORMAL, Element.ALIGN_LEFT, 1));

                    document.add(tbl_det);

                    document.add(new Paragraph(" "));
                    document.add(new Paragraph(" "));
                    document.add(new Paragraph(" "));

                    PdfPTable tbl_firma = new PdfPTable(1);
                    tbl_firma.addCell(Addons.setCeldaPDF("_______________________", Font.HELVETICA, 10, Font.NORMAL, Element.ALIGN_LEFT, 0));
                    tbl_firma.addCell(Addons.setCeldaPDF("FIRMA", Font.HELVETICA, 10, Font.NORMAL, Element.ALIGN_LEFT, 0));
                    document.add(tbl_firma);



                    document.newPage();



                    Archivo archivo = new Archivo(_ip, _puerto, _db, _usuario, _clave);
                    //path = path.substring(0, path.lastIndexOf("/"));
                    String croquis = path + "/img/" + archivo.getArchivo(_dir, "tbl_instalacion", id, "nombre_img", "croquis");
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
                            (RS.getString("num_desinstalacion")!=null?RS.getString("num_desinstalacion"):"") +
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

        Instalacion objInstalacion = new Instalacion(this._ip, this._puerto, this._db, this._usuario, this._clave);
        ResultSet res = objInstalacion.getInstalacion(id) ;

        String path = String.valueOf(request.getRequestURL());
        path = path.substring(0, path.lastIndexOf("/"));
        Archivo archivo = new Archivo(this._ip, this._puerto, this._db, this._usuario, this._clave);
        String logo = path + "/img/" + archivo.getArchivo(this._dir, 1);
        archivo.cerrar();

        PdfInstalacion obj = new PdfInstalacion();
        obj.imprimir(response, res, id, path, logo);

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
