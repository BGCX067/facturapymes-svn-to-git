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

import java.sql.ResultSet;
import jm.act.clas.Activo;
import jm.adm.clas.Configuracion;
import jm.web.Addons;
import jm.web.Archivo;

/**
 *
 * @author Jorge
 */
public class pdfPersonalizacion extends HttpServlet {
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

    class Movimiento{
        public Movimiento(){}

        public void imprimir(HttpServletResponse response, ResultSet cab, ResultSet det, String logo, String ruc, String razon_social, String dir_matriz){
            response.setContentType("application/pdf");
            response.setHeader("Pragma", "no-cache");
            response.setHeader("Expires", "Mon, 01 Jan 2001 00:00:01 GMT");
            response.setHeader("Cache-Control", "no-store");
            response.setHeader("Cache-Control", "must-revalidate");
            response.setHeader("Cache-Control", "no-cache");

            /* inicio PDF */
            Document document = new Document(PageSize.A4);// paso 1
            document.setMargins(0,0,50,30); /*Izquierda, derecha, tope, pie */

            try {
                PdfWriter writer = PdfWriter.getInstance(document, response.getOutputStream()); // paso 2
                //writer.setPageEvent(new Movimiento());

                document.open(); // paso 3

                writer.addJavaScript("this.print(false);", false); // Para enviar a la impresora automáticamente.

                /* todo el cuerpo del doc es el paso 4 */

                PdfPTable encabezado = new PdfPTable(new float[]{80f,400f});
                PdfPTable tbl_encab = new PdfPTable(1);
                Image imagelogo = null;
                try{
                    imagelogo = Image.getInstance(logo);
                    imagelogo.scaleAbsolute(70, 70);
                    PdfPCell celdaImg = new PdfPCell(imagelogo);
                    celdaImg.setBorderWidth(0);
                    celdaImg.setPadding(0);
                    encabezado.addCell(celdaImg);
                }catch(Exception e){
                    encabezado.addCell("");
                }
                tbl_encab.addCell(Addons.setCeldaPDF(razon_social, Font.HELVETICA, 13, Font.BOLD, Element.ALIGN_CENTER, 0));
                tbl_encab.addCell(Addons.setCeldaPDF(dir_matriz, Font.HELVETICA, 11, Font.NORMAL, Element.ALIGN_CENTER, 0));
                if(ruc.compareTo("")!=0){
                    tbl_encab.addCell(Addons.setCeldaPDF("RUC: "+ruc, Font.HELVETICA, 11, Font.NORMAL, Element.ALIGN_CENTER, 0));
                }
                tbl_encab.addCell(Addons.setCeldaPDF("FORMULARIO DE MOVIMIENTO DE ACTIVOS", Font.HELVETICA, 10, Font.NORMAL, Element.ALIGN_CENTER, 0));

                encabezado.addCell(Addons.setCeldaPDF(tbl_encab, Element.ALIGN_CENTER, 0));

                encabezado.addCell(Addons.setFilaBlanco(2, 4));

                document.add(encabezado);


                String observacion = "";
                String persona_entrega = "";
                String persona_recibe = "";
                Double gestion_envio = 0.0;
                try{
                    if(cab.next()){
                        observacion = (cab.getString("observacion")!=null) ? cab.getString("observacion") : "";
                        persona_entrega = (cab.getString("persona_entrega")!=null) ? cab.getString("persona_entrega") : "";
                        persona_recibe = (cab.getString("persona_recibe")!=null) ? cab.getString("persona_recibe") : "";
                        gestion_envio = (cab.getString("gestion_envio")!=null) ? cab.getDouble("gestion_envio") : 0.0;
                        PdfPTable tbl_cab = new PdfPTable(4);
                        tbl_cab.addCell(Addons.setCeldaPDF("Fecha: " + ((cab.getString("fecha")!=null) ? cab.getString("fecha") : ""), Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_LEFT, 0, 3, 2));
                        tbl_cab.addCell(Addons.setCeldaPDF("Nro. " + ((cab.getString("num_documento")!=null) ? cab.getString("num_documento") : ""), Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_RIGHT, 0, 3, 2));

                        tbl_cab.addCell(Addons.setCeldaPDF("Tipo de movimiento (Traslado): " + ((cab.getString("txt_tipo_movimiento")!=null) ? cab.getString("txt_tipo_movimiento") : ""), Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_LEFT, 0, 3, 4));

                        tbl_cab.addCell(Addons.setFilaBlanco(4, 10));  /* colspan, alto */

                        tbl_cab.addCell(Addons.setCeldaPDF("ENTREGA", Font.HELVETICA, 8, Font.BOLD, Element.ALIGN_CENTER, 1, 3, 2));
                        tbl_cab.addCell(Addons.setCeldaPDF("RECIBE", Font.HELVETICA, 8, Font.BOLD, Element.ALIGN_CENTER, 1, 3, 2));

                        String tipo = (cab.getString("tipo_movimiento")!=null) ? cab.getString("tipo_movimiento") : "";
                        if(tipo.compareTo("2")!=0){
                            tbl_cab.addCell(Addons.setCeldaPDF("Bodega: ", Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_LEFT, 1));
                            tbl_cab.addCell(Addons.setCeldaPDF((cab.getString("bodega_entrega")!=null) ? cab.getString("bodega_entrega") : "", Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_LEFT, 1));
                            tbl_cab.addCell(Addons.setCeldaPDF("Bodega: ", Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_LEFT, 1));
                            tbl_cab.addCell(Addons.setCeldaPDF((cab.getString("bodega_recibe")!=null) ? cab.getString("bodega_recibe") : "", Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_LEFT, 1));

                            tbl_cab.addCell(Addons.setCeldaPDF("Ubicación: ", Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_LEFT, 1));
                            tbl_cab.addCell(Addons.setCeldaPDF((cab.getString("ubicacion_entrega")!=null) ? cab.getString("ubicacion_entrega") : "", Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_LEFT, 1));
                            tbl_cab.addCell(Addons.setCeldaPDF("Ubicación: ", Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_LEFT, 1));
                            tbl_cab.addCell(Addons.setCeldaPDF((cab.getString("ubicacion_recibe")!=null) ? cab.getString("ubicacion_recibe") : "", Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_LEFT, 1));
                        }

                        tbl_cab.addCell(Addons.setCeldaPDF("Documento de Identidad: ", Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_LEFT, 1));
                        tbl_cab.addCell(Addons.setCeldaPDF((cab.getString("dni_entrega")!=null) ? cab.getString("dni_entrega") : "", Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_LEFT, 1));
                        tbl_cab.addCell(Addons.setCeldaPDF("Documento de Identidad: ", Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_LEFT, 1));
                        tbl_cab.addCell(Addons.setCeldaPDF((cab.getString("dni_recibe")!=null) ? cab.getString("dni_recibe") : "", Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_LEFT, 1));

                        tbl_cab.addCell(Addons.setCeldaPDF("Responsable actual: ", Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_LEFT, 1));
                        tbl_cab.addCell(Addons.setCeldaPDF(persona_entrega, Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_LEFT, 1));
                        tbl_cab.addCell(Addons.setCeldaPDF("Nuevo Responsable: ", Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_LEFT, 1));
                        tbl_cab.addCell(Addons.setCeldaPDF(persona_recibe, Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_LEFT, 1));

                        document.add(tbl_cab);
                    }
                }catch(Exception e){
                    e.printStackTrace();
                }

                document.add(new Paragraph(" "));
                
                try{
                    Double total=0.0;
                    Double precio_tot=0.0;
                    Double tot_sub=0.0;
                    Double tot_iva=0.0;
                    Double valor_dep=0.0;
                    Double total_dep=0.0;
                    PdfPTable tbl_det = new PdfPTable(new float[]{20f,30f,10f,10f,10f,10f});
                    tbl_det.addCell(Addons.setCeldaPDF("INFORMACIÓN BÁSICA DE LOS ACTIVOS FIJOS", Font.HELVETICA, 8, Font.BOLD, Element.ALIGN_LEFT, 0, 3, 6));
                    tbl_det.addCell(Addons.setCeldaPDF("CÓDIGO", Font.HELVETICA, 8, Font.BOLD, Element.ALIGN_CENTER, 1));
                    tbl_det.addCell(Addons.setCeldaPDF("DESCRIPCIÓN", Font.HELVETICA, 8, Font.BOLD, Element.ALIGN_CENTER, 1));
                    tbl_det.addCell(Addons.setCeldaPDF("SUBTOTAL", Font.HELVETICA, 8, Font.BOLD, Element.ALIGN_CENTER, 1));
                    tbl_det.addCell(Addons.setCeldaPDF("IVA", Font.HELVETICA, 8, Font.BOLD, Element.ALIGN_CENTER, 1));
                    tbl_det.addCell(Addons.setCeldaPDF("PRECIO TOTAL", Font.HELVETICA, 8, Font.BOLD, Element.ALIGN_CENTER, 1));
                    tbl_det.addCell(Addons.setCeldaPDF("COSTO REAL", Font.HELVETICA, 8, Font.BOLD, Element.ALIGN_CENTER, 1));
                    while(det.next()){
                        tbl_det.addCell(Addons.setCeldaPDF((det.getString("codigo_activo")!=null) ? det.getString("codigo_activo") : "", Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_LEFT, 1));
                        tbl_det.addCell(Addons.setCeldaPDF((det.getString("descripcion")!=null) ? det.getString("descripcion") : "", Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_LEFT, 1));
                        tbl_det.addCell(Addons.setCeldaPDF((det.getString("valor_compra")!=null) ? det.getString("valor_compra") : "", Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_RIGHT, 1));
                        tbl_det.addCell(Addons.setCeldaPDF((det.getString("iva")!=null) ? Addons.redondear(det.getString("iva")) : "", Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_RIGHT, 1));
                        
                        precio_tot=Double.parseDouble(det.getString("valor_compra"))+Double.parseDouble(det.getString("iva"));
                        tbl_det.addCell(Addons.setCeldaPDF(String.valueOf(Addons.redondear(precio_tot)), Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_RIGHT, 1));
                        
                        valor_dep=Double.parseDouble(det.getString("valor_compra"))-Double.parseDouble(det.getString("valor_depreciado"));
                        tbl_det.addCell(Addons.setCeldaPDF(String.valueOf(Addons.redondear(valor_dep)), Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_RIGHT, 1));
                        
                        total_dep+=valor_dep;
                        tot_sub+=Double.parseDouble(det.getString("valor_compra"));
                        tot_iva+=Double.parseDouble(det.getString("iva"));
                        total+=Double.parseDouble(det.getString("valor_compra"))+Double.parseDouble(det.getString("iva"));
                    }
                    tbl_det.addCell(Addons.setCeldaPDF("GESTION DE ENVIO", Font.HELVETICA, 8, Font.BOLD, Element.ALIGN_LEFT, 1,3,2));
                    tbl_det.addCell(Addons.setCeldaPDF(String.valueOf(Addons.redondear(gestion_envio)), Font.HELVETICA, 8, Font.BOLD, Element.ALIGN_RIGHT, 1));
                    tbl_det.addCell(Addons.setCeldaPDF(String.valueOf(Addons.redondear(gestion_envio*0.12)), Font.HELVETICA, 8, Font.BOLD, Element.ALIGN_RIGHT, 1));
                    tbl_det.addCell(Addons.setCeldaPDF(String.valueOf(Addons.redondear(gestion_envio)+(gestion_envio*0.12)), Font.HELVETICA, 8, Font.BOLD, Element.ALIGN_RIGHT, 1));
                    tbl_det.addCell(Addons.setCeldaPDF("0.0", Font.HELVETICA, 8, Font.BOLD, Element.ALIGN_RIGHT, 1));
                    
                    tot_sub+=gestion_envio;
                    tot_iva+=gestion_envio*0.12;
                    total+=gestion_envio+(gestion_envio*0.12);
                       
                    tbl_det.addCell(Addons.setCeldaPDF("TOTAL", Font.HELVETICA, 8, Font.BOLD, Element.ALIGN_LEFT, 1,3,2));
                    tbl_det.addCell(Addons.setCeldaPDF(String.valueOf(Addons.redondear(tot_sub)), Font.HELVETICA, 8, Font.BOLD, Element.ALIGN_RIGHT, 1));
                    tbl_det.addCell(Addons.setCeldaPDF(String.valueOf(Addons.redondear(tot_iva)), Font.HELVETICA, 8, Font.BOLD, Element.ALIGN_RIGHT, 1));
                    tbl_det.addCell(Addons.setCeldaPDF(String.valueOf(Addons.redondear(total)), Font.HELVETICA, 8, Font.BOLD, Element.ALIGN_RIGHT, 1));
                    tbl_det.addCell(Addons.setCeldaPDF(String.valueOf(Addons.redondear(total_dep)), Font.HELVETICA, 8, Font.BOLD, Element.ALIGN_RIGHT, 1));
                    document.add(tbl_det);
                }catch(Exception e){
                    e.printStackTrace();
                }                

                document.add(new Paragraph(" "));
                
                PdfPTable tbl_obs = new PdfPTable(1);
                tbl_obs.addCell(Addons.setCeldaPDF("CLÁUSULA DE COMPROMISO", Font.HELVETICA, 8, Font.BOLD, Element.ALIGN_LEFT, 0));
                tbl_obs.addCell(Addons.setCeldaPDF(observacion, Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_JUSTIFIED, 1));
                document.add(tbl_obs);

                document.add(new Paragraph(" "));
                document.add(new Paragraph(" "));
                document.add(new Paragraph(" "));
                
                PdfPTable tbl_firmas = new PdfPTable(3);
                tbl_firmas.addCell(Addons.setCeldaPDF("___________________________", Font.HELVETICA, 8, Font.BOLD, Element.ALIGN_LEFT, 0));
                tbl_firmas.addCell(Addons.setCeldaPDF("___________________________", Font.HELVETICA, 8, Font.BOLD, Element.ALIGN_LEFT, 0));
                tbl_firmas.addCell(Addons.setCeldaPDF("___________________________", Font.HELVETICA, 8, Font.BOLD, Element.ALIGN_LEFT, 0));
                
                tbl_firmas.addCell(Addons.setCeldaPDF("ENTREGA", Font.HELVETICA, 8, Font.BOLD, Element.ALIGN_LEFT, 0));
                tbl_firmas.addCell(Addons.setCeldaPDF("RECIBE", Font.HELVETICA, 8, Font.BOLD, Element.ALIGN_LEFT, 0));
                tbl_firmas.addCell(Addons.setCeldaPDF("AUTORIZA", Font.HELVETICA, 8, Font.BOLD, Element.ALIGN_LEFT, 0));
                
                tbl_firmas.addCell(Addons.setCeldaPDF(persona_entrega, Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_LEFT, 0));
                tbl_firmas.addCell(Addons.setCeldaPDF(persona_recibe, Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_LEFT, 0));
                tbl_firmas.addCell(Addons.setCeldaPDF(" ", Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_LEFT, 0));
                
                document.add(tbl_firmas);

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

        /*public void onStartPage(PdfWriter writer, Document document)
        {
            Addons.setEncabezado(writer, document, "Contrato Nro. "+num_documento);
        }*/

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

        Configuracion conf = new Configuracion(this._ip, this._puerto, this._db, usuario, clave);
        String ruc = conf.getValor("ruc");
        String razon_social = conf.getValor("razon_social");
        String dir_matriz = conf.getValor("dir_matriz");

        String path = String.valueOf(request.getRequestURL());
        path = path.substring(0, path.lastIndexOf("/"));
        Archivo archivo = new Archivo(this._ip, this._puerto, this._db, usuario, clave);
        String logo = path + "/img/" + archivo.getArchivo(this._dir, 1);

        Activo objActivo = new Activo(this._ip, this._puerto, this._db, usuario, clave);
        ResultSet rsDocumento = objActivo.getDocumento(id);
        boolean aceptada = false;
        ResultSet rsDocumentoDetalle = null;
        try{
            if(rsDocumento.next()){
                aceptada = rsDocumento.getString("aceptada")!=null ? rsDocumento.getBoolean("aceptada") : false;
            }
            rsDocumento.beforeFirst();
        }catch(Exception e){
            e.printStackTrace();
        }
        if(!aceptada){
            rsDocumentoDetalle = objActivo.getDocumentoDetalleTmp(id);
        }else{
            rsDocumentoDetalle = objActivo.getDocumentoDetalle(id);
        }

        Movimiento obj = new Movimiento();
        obj.imprimir(response, rsDocumento, rsDocumentoDetalle, logo, ruc, razon_social, dir_matriz);

        try{
            rsDocumento.close();
            rsDocumentoDetalle.close();
        }catch(Exception e){
            e.printStackTrace();
        }finally{
            objActivo.cerrar();
            conf.cerrar();
            objActivo.cerrar();
        }
        
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
