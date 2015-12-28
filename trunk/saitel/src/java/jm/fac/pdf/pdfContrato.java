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
import jm.adm.clas.Configuracion;
import jm.web.Addons;
import jm.fac.clas.Contrato;
import jm.web.Cadena;

/**
 *
 * @author Jorge
 */
public class pdfContrato extends HttpServlet {
    private String _ip = null;
    private int _puerto = 5432;
    private String _db = null;
    private String num_contrato = "";
    private String _dir = null;

    public void init(ServletConfig config) throws ServletException
    {
        this._ip = config.getServletContext().getInitParameter("_IP");
        this._puerto = Integer.parseInt(config.getServletContext().getInitParameter("_PUERTO"));
        this._db = config.getServletContext().getInitParameter("_DB");
        this._dir = config.getServletContext().getInitParameter("_DIR"); 
    }

    class PdfContrato extends PdfPageEventHelper{
        public PdfContrato(){}

        public void imprimir(HttpServletResponse response, String contrato, String datosFirma[]){
            response.setContentType("application/pdf");
            response.setHeader("Pragma", "no-cache");
            response.setHeader("Expires", "Mon, 01 Jan 2001 00:00:01 GMT");
            response.setHeader("Cache-Control", "no-store");
            response.setHeader("Cache-Control", "must-revalidate");
            response.setHeader("Cache-Control", "no-cache");

            //_dir = "/home/desarrollo/Documentos/saitel/web/img/";
            
            /* inicio PDF */
            Document document = new Document(PageSize.A4);// paso 1
            document.setMargins(30,0,130,30); /*Izquierda, derecha, tope, pie */

            try {
                PdfWriter writer = PdfWriter.getInstance(document, response.getOutputStream()); // paso 2
                writer.setPageEvent(new PdfContrato());

                document.open(); // paso 3

                writer.addJavaScript("this.print(false);", false); // Para enviar a la impresora automáticamente.

                /* todo el cuerpo del doc es el paso 4 */

                PdfPTable tbl_det = new PdfPTable(1);
                tbl_det.addCell(Addons.setCeldaPDF(contrato, Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_JUSTIFIED, 0));
                document.add(tbl_det);

                document.add(new Paragraph(" "));
                document.add(new Paragraph(" "));
                document.add(new Paragraph(" "));
                document.add(new Paragraph(" "));

                PdfPTable tbl_firma = new PdfPTable(2);
                tbl_firma.addCell(Addons.setCeldaPDF("__________________________", Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_LEFT, 0));
                tbl_firma.addCell(Addons.setCeldaPDF("__________________________", Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_LEFT, 0));

                for(int i=0;i<datosFirma.length; i++){
                    tbl_firma.addCell(Addons.setCeldaPDF(datosFirma[i], Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_LEFT, 0));
                }
                document.add(tbl_firma);
                
                
                

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

        public void onStartPage(PdfWriter writer, Document document)
        {
            try{
                PdfPTable encabezado = new PdfPTable(2);
                encabezado.setTotalWidth(document.right() - document.left()-120);
                PdfPCell logoCel = Addons.setLogo(_dir+"logo.jpg", 200, 60);
                if(logoCel!=null){
                    encabezado.addCell(logoCel);
                }
                encabezado.addCell(Addons.setCeldaPDF(" ", Font.HELVETICA, 9, Font.BOLD, Element.ALIGN_RIGHT, 0));
                encabezado.addCell(Addons.setCeldaPDF(" ", Font.HELVETICA, 9, Font.BOLD, Element.ALIGN_RIGHT, 0));
                encabezado.addCell(Addons.setCeldaPDF("Contrato Nro. "+num_contrato, Font.HELVETICA, 9, Font.BOLD, Element.ALIGN_RIGHT, 0));
                encabezado.writeSelectedRows(0, -1, 80, document.top()+80, writer.getDirectContent());
                
                
                PdfContentByte cb = writer.getDirectContent();
                cb.setLineWidth(2);
                cb.moveTo(60, document.top()+10);
                cb.lineTo(document.right() - document.left()-58, document.top()+10);
            }catch(Exception e) {
                throw new ExceptionConverter(e);
            }
            
        }

        public void onEndPage(PdfWriter writer, Document document)
        {
            try{
                Image img = Addons.setMarcaAgua(_dir+"logo_agua.jpg", 681, 206);
                if(img != null){
                    document.add(img);
                }
            }catch(Exception e) {
                throw new ExceptionConverter(e);
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
        String usuario = (String)sesion.getAttribute("usuario");
        String clave = (String)sesion.getAttribute("clave");
        
        //PrintWriter out = response.getWriter();

        String id = request.getParameter("id");

        Contrato objContrato = new Contrato(this._ip, this._puerto, this._db, usuario, clave);
        String texto = "";
        String num = "";
        String id_sucursal = "";
        String cedula = "";
        String cliente = "";
        //String ruc_representante = "";
        String representante = "";
        try{
            ResultSet res = objContrato.getContrato(id);
            if(res.next()){
                id_sucursal = res.getString("id_sucursal")!=null ? res.getString("id_sucursal") : "";
                num = res.getString("num_contrato")!=null ? res.getString("num_contrato") : "";
                texto = res.getString("contrato")!=null ? res.getString("contrato") : "";
                cedula = res.getString("ruc")!=null ? res.getString("ruc") : "";
                cliente = res.getString("razon_social")!=null ? res.getString("razon_social") : "";
                //ruc_representante = res.getString("ruc_representante")!=null ? res.getString("ruc_representante") : "";
                representante = res.getString("representante")!=null ? res.getString("representante") : "";
                res.close();
            }
        }catch(Exception e){
            e.printStackTrace();
        }finally{
            objContrato.cerrar();
        }

        this.num_contrato = id_sucursal + "-" + num;

        String datosFirma[] = new String[6];
        Configuracion conf = new Configuracion(this._ip, this._puerto, this._db, usuario, clave);
        datosFirma[0] = conf.getValor("rep_cargo").toUpperCase();
        datosFirma[1] = representante.compareTo("")!=0 ? cliente.toUpperCase() : "EL SUBSCRIPTOR";
        datosFirma[2] = conf.getValor("rep_nombre");
        datosFirma[3] = representante.compareTo("")!=0 ? Cadena.capital(representante) : Cadena.capital(cliente);
        datosFirma[4] = "R.U.C.: " + conf.getValor("ruc");
        datosFirma[5] = (cedula.length()==13 ? "R.U.C.: " : "C.I.: ") + cedula;
        conf.cerrar();

        PdfContrato obj = new PdfContrato();
        obj.imprimir(response, texto, datosFirma);
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
