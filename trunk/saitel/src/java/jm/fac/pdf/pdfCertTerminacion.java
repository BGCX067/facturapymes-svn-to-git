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
import jm.adm.clas.Sucursal;
import jm.adm.clas.Documento;
import jm.web.Addons;
import jm.fac.clas.Instalacion;
import jm.web.Fecha;

/**
 *
 * @author Jorge
 */
public class pdfCertTerminacion extends HttpServlet {
    private String _ip = null;
    private int _puerto = 5432;
    private String _db = null;

    public void init(ServletConfig config) throws ServletException
    {
        this._ip = config.getServletContext().getInitParameter("_IP");
        this._puerto = Integer.parseInt(config.getServletContext().getInitParameter("_PUERTO"));
        this._db = config.getServletContext().getInitParameter("_DB");
    }

    class Suspension{
        public Suspension(){}

        public void imprimir(HttpServletResponse response, String fecha, String texto){
            response.setContentType("application/pdf");
            response.setHeader("Pragma", "no-cache");
            response.setHeader("Expires", "Mon, 01 Jan 2001 00:00:01 GMT");
            response.setHeader("Cache-Control", "no-store");
            response.setHeader("Cache-Control", "must-revalidate");
            response.setHeader("Cache-Control", "no-cache");

            /* inicio PDF */
            Document document = new Document(PageSize.A4);// paso 1
            document.setMargins(50,30,90,0); /*Izquierda, derecha, tope, pie */

            try {
                PdfWriter writer = PdfWriter.getInstance(document, response.getOutputStream()); // paso 2

                document.open(); // paso 3

                writer.addJavaScript("this.print(false);", false); // Para enviar a la impresora automáticamente.

                /* todo el cuerpo del doc es el paso 4 */

                PdfPTable tbl_det = new PdfPTable(1);
                tbl_det.addCell(Addons.setCeldaPDF(fecha, Font.HELVETICA, 10, Font.NORMAL, Element.ALIGN_RIGHT, 0));
                tbl_det.addCell(Addons.setFilaBlanco(1, 25));
                tbl_det.addCell(Addons.setCeldaPDF(texto, Font.HELVETICA, 10, Font.NORMAL, Element.ALIGN_JUSTIFIED, 0));
                document.add(tbl_det);
                //document.add(new Paragraph(texto));

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
        int id_sucursal_sesion = (Integer)sesion.getAttribute("sucursal");
        String usuario = (String)sesion.getAttribute("usuario");
        String clave = (String)sesion.getAttribute("clave");

        String id_instalacion = request.getParameter("id");
        String fecha = request.getParameter("Fch");
        String motivo = request.getParameter("mot");

        byte ptext[] = motivo.getBytes();
        String motivo_cod = new String(ptext, "UTF-8");


        Configuracion conf = new Configuracion(this._ip, this._puerto, this._db, usuario, clave);
        String rep_nombre = conf.getValor("rep_nombre");
        String rep_cargo = conf.getValor("rep_cargo");
        conf.cerrar();

        String ciudad = "";
        Sucursal objSucursal = new Sucursal(this._ip, this._puerto, this._db, usuario, clave);
        try{
            ResultSet res = objSucursal.getSucursal(String.valueOf(id_sucursal_sesion));
            if(res.next()){
                ciudad = res.getString("ciudad")!=null ? res.getString("ciudad") : "";
                res.close();
            }
        }catch(Exception e){
            e.printStackTrace();
        }finally{
            objSucursal.cerrar();
        }


        String documento = "";
        Documento objDocumento = new Documento(this._ip, this._puerto, this._db, usuario, clave);
        try{
            ResultSet res = objDocumento.getDocumento("t");
            if(res.next()){
                documento = res.getString("documento")!=null ? res.getString("documento") : "";
                res.close();
            }
        }catch(Exception e){
            e.printStackTrace();
        }finally{
            objDocumento.cerrar();
        }

        
        Instalacion objInstalacion = new Instalacion(this._ip, this._puerto, this._db, usuario, clave);
        if( objInstalacion.setNoInstalacion(id_instalacion, fecha, motivo_cod) ){
            String ip = "";
            String ruc = "";
            String cliente = "";
            String fecha_no_instalacion = "";
            try{
                ResultSet res = objInstalacion.getInstalacion(id_instalacion);
                if(res.next()){
                    ruc = res.getString("ruc")!=null ? res.getString("ruc") : "";
                    cliente = res.getString("razon_social")!=null ? res.getString("razon_social") : "";
                    ip = res.getString("ip")!=null ? res.getString("ip") : "";
                    fecha_no_instalacion = res.getString("fecha_no_instalacion")!=null ? res.getString("fecha_no_instalacion") : "";
                    motivo = res.getString("motivo_no_instalacion")!=null ? res.getString("motivo_no_instalacion") : "";
                    res.close();
                }
            }catch(Exception e){
                e.printStackTrace();
            }finally{
                objInstalacion.cerrar();
            }


            documento = documento.replace("<<REPRESENTANTE>>", rep_nombre);
            documento = documento.replace("<<CARGO>>", rep_cargo.toUpperCase());
            documento = documento.replace("<<FECHA>>", fecha);
            documento = documento.replace("<<CLIENTE>>", cliente);
            documento = documento.replace("<<CEDULA>>", ruc);
            documento = documento.replace("<<MOTIVO>>", motivo);

            documento += "\n\n\rIP: " + ip;

            Suspension obj = new Suspension();
            obj.imprimir(response, ciudad + ", " + Fecha.getFechaSolicitud(fecha_no_instalacion), documento);
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
