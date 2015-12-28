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
import jm.adm.clas.Documento;
import jm.fac.clas.Cliente;
import jm.web.Addons;
import jm.web.Fecha;

/**
 *
 * @author Jorge
 */
public class pdfAutorizacion extends HttpServlet {
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

        public void imprimir(HttpServletResponse response, String texto){
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
                //tbl_det.addCell(Addons.setCeldaPDF(fecha, Font.HELVETICA, 10, Font.NORMAL, Element.ALIGN_RIGHT, 0));
                //tbl_det.addCell(Addons.setFilaBlanco(1, 25));
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
        String usuario = (String)sesion.getAttribute("usuario");
        String clave = (String)sesion.getAttribute("clave");

        String id = request.getParameter("id");

        
        String ip = "";
        String ruc = "";
        String cliente = "";
        String forma_pago = "";
        String num_cuenta = "";
        String tipo_cuenta = "";
        String tarjeta_credito_caduca = "";
        String telefono = "";
        String movil_claro = "";
        String movil_movistar = "";
        int tipo_tarjeta_credito = 0;
        Cliente objCliente = new Cliente(this._ip, this._puerto, this._db, usuario, clave);
        try{
            ResultSet res = objCliente.getCliente(id);
            if(res.next()){
                ruc = res.getString("documento")!=null ? res.getString("documento") : "t";
                cliente = res.getString("cliente_debito")!=null ? res.getString("cliente_debito") : "";
                forma_pago = res.getString("forma_pago")!=null ? res.getString("forma_pago") : "";
                num_cuenta = res.getString("num_cuenta")!=null ? res.getString("num_cuenta") : "";
                tipo_cuenta = res.getString("tipo_cuenta")!=null ? res.getString("tipo_cuenta") : "";
                tarjeta_credito_caduca = res.getString("tarjeta_credito_caduca")!=null ? res.getString("tarjeta_credito_caduca") : "";
                telefono = res.getString("telefono")!=null ? res.getString("telefono") : "";
                movil_claro = res.getString("movil_claro")!=null ? res.getString("movil_claro") : "";
                movil_movistar = res.getString("movil_movistar")!=null ? res.getString("movil_movistar") : "";
                tipo_tarjeta_credito = res.getString("tipo_tarjeta_credito")!=null ? res.getInt("tipo_tarjeta_credito") : 0;
                ip = res.getString("ip")!=null ? res.getString("ip") : "";
                res.close();
            }
        }catch(Exception e){
            e.printStackTrace();
        }finally{
            objCliente.cerrar();
        }

        String documento = "";
        Documento objDocumento = new Documento(this._ip, this._puerto, this._db, usuario, clave);
        try{
            ResultSet res = objDocumento.getDocumento( forma_pago.compareTo("TAR")==0 ? "1" : "e" );
            if(res.next()){
                documento = res.getString("documento")!=null ? res.getString("documento") : "";
                res.close();
            }
        }catch(Exception e){
            e.printStackTrace();
        }finally{
            objDocumento.cerrar();
        }

        String [] tipo_tarjeta_credito1 = {" ", "Diners Club","Discover","Mastercard","Visa"};

        documento = documento.replace("<<FECHA>>", Fecha.getFechaSolicitud(Fecha.getFecha("ISO")));
        documento = documento.replace("<<CLIENTE>>", cliente);
        documento = documento.replace("<<TITULAR>>", cliente);
        documento = documento.replace("<<RUC>>", ruc);
        //documento = documento.replace("<<CLIENTE_FIRMA>>", Cadena.capital(cliente));
        documento = documento.replace("<<CUENTA>>", num_cuenta);
        documento = documento.replace("<<FECHA_CADUCIDAD>>", tarjeta_credito_caduca);
        if(telefono.compareTo("")!=0){
            documento = documento.replace("<<TELF_CONVENCIONAL>>", "Telf. Convencional " + telefono);
        }else{
            documento = documento.replace("<<TELF_CONVENCIONAL>>", "");
        }
        if(movil_claro.compareTo("")==0 && movil_movistar.compareTo("")==0){
            documento = documento.replace("<<TELF_CELULAR>>", "");
        }else{
            String movil = "";
            if(movil_claro.compareTo("")!=0){
                movil = movil_claro;
            }
            if(movil_movistar.compareTo("")!=0){
                movil = movil_claro.compareTo("")!=0 ? movil_claro + " / " + movil_movistar : movil_movistar;
            }
            documento = documento.replace("<<TELF_CELULAR>>", "Telf. Celular " + movil);
        }
        documento = documento.replace("<<TIPO_TARJETA>>", tipo_tarjeta_credito1[tipo_tarjeta_credito] );
        documento = documento.replace("<<TIPO>>", tipo_cuenta);
        documento = documento.replace("<<VALOR>>", "_________");
        

        //documento += "\n\n\rIP: " + ip;

        Suspension obj = new Suspension();
        obj.imprimir(response, documento);
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
