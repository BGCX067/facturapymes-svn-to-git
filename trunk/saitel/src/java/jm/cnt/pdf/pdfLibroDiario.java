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

package jm.cnt.pdf;

import java.io.*;

import javax.servlet.*;
import javax.servlet.http.*;


import com.lowagie.text.pdf.*;
import com.lowagie.text.*;

import jm.adm.clas.Configuracion;
import jm.adm.clas.Sucursal;
import jm.web.Addons;
import jm.web.Archivo;
import jm.cnt.clas.LibroDiario;

import java.sql.ResultSet;
import java.sql.SQLException;
import jm.web.Fecha;

/**
 *
 * @author Jorge
 */
public class pdfLibroDiario extends HttpServlet {
    private String _ip = null;
    private int _puerto = 5432;
    private String _db = null;
    private String _usuario = null;
    private String _clave = null;

    private String logo = "";
    private String ruc = "";
    private String titulo = "";
    private String subtitulo = "";
    private String direccion = "";
    private String sucursal = "";
    private String rep_pie = "";
    private String _dir = null;

    public void init(ServletConfig config) throws ServletException
    {
        this._ip = config.getServletContext().getInitParameter("_IP");
        this._puerto = Integer.parseInt(config.getServletContext().getInitParameter("_PUERTO"));
        this._db = config.getServletContext().getInitParameter("_DB");
        this._dir = config.getServletContext().getInitParameter("_DIR");
    }

    class imLibroDiario extends PdfPageEventHelper{
        public imLibroDiario(){}

        public void imprimir(HttpServletResponse response, String id_sucursal, String fecha_ini, String fecha_fin){
            response.setContentType("application/pdf");
            response.setHeader("Pragma", "no-cache");
            response.setHeader("Expires", "Mon, 01 Jan 2001 00:00:01 GMT");
            response.setHeader("Cache-Control", "no-store");
            response.setHeader("Cache-Control", "must-revalidate");
            response.setHeader("Cache-Control", "no-cache");

            String [] cabTabla = null;
            float [] anchoTabla = null;
            ResultSet rsLibroDiario = null;
            LibroDiario objLibroDiario = new LibroDiario(_ip, _puerto, _db, _usuario, _clave);
            try {
                int num_pags = 0;
                if(id_sucursal.compareTo("-0")==0){
                    num_pags = objLibroDiario.getNumPags("SELECT count(*) FROM vta_libro_diario_mayor where fecha_asiento between '"+fecha_ini+"' and '"+fecha_fin+"'", 100);
                    rsLibroDiario = objLibroDiario.getTransacciones(fecha_ini, fecha_fin, 0, 10000);
                }else{
                    num_pags = objLibroDiario.getNumPags("SELECT count(*) FROM vta_libro_diario_mayor where id_sucursal="+id_sucursal+" and fecha_asiento between '"+fecha_ini+"' and '"+fecha_fin+"'", 100);
                    rsLibroDiario = objLibroDiario.getTransacciones(id_sucursal, fecha_ini, fecha_fin, 0, num_pags * 100);
                }

                cabTabla = new String [] {"FECHA", "CÓDIGO", "DETALLE", "DEBE", "HABER"};
                anchoTabla = new float[]{35f,55f,145f,40f,40f};

                /* inicio PDF */
                Document document = new Document(PageSize.A4);// paso 1
                document.setMargins(0,0,50,80); /*Izquierda, derecha, tope, pie */
                try{
                    PdfWriter writer = PdfWriter.getInstance(document, response.getOutputStream()); // paso 2
                    writer.setPageEvent(new imLibroDiario());

                    document.open(); // paso 3

                    writer.addJavaScript("this.print(false);", false); // Para enviar a la impresora automáticamente.

                    /* todo el cuerpo del doc es el paso 4 */

                    document.add(Addons.setCabecera(logo, titulo, ruc, subtitulo, direccion, sucursal));

                    document.add(Addons.setCabeceraTabla(cabTabla, anchoTabla));

                    PdfPTable tbl_det = new PdfPTable(anchoTabla);
                    String num_asiento = "";
                    String detalle = "";
                    String aux = "";
                    String auxDet = "";
                    try{
                        String debe = "";
                        String haber = "";
                        while(rsLibroDiario.next()){
                            num_asiento = (rsLibroDiario.getString("num_asiento")!=null) ? rsLibroDiario.getString("num_asiento") : "";
                            detalle = (rsLibroDiario.getString("detalle")!=null) ? rsLibroDiario.getString("detalle") : "";
                            debe = (rsLibroDiario.getString("debe")!=null) ? ((rsLibroDiario.getDouble("debe")>0)?rsLibroDiario.getString("debe"):"") : "";
                            haber = (rsLibroDiario.getString("haber")!=null) ? ((rsLibroDiario.getDouble("haber")>0)?rsLibroDiario.getString("haber"):"") : "";
                            if(num_asiento.compareTo(aux)!=0){
                                if(auxDet.compareTo("")!=0){
                                    tbl_det.addCell(Addons.setCeldaPDF("", Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_LEFT, 1));
                                    tbl_det.addCell(Addons.setCeldaPDF("", Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_LEFT, 1));
                                    tbl_det.addCell(Addons.setCeldaPDF(auxDet, Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_LEFT, 1));
                                    tbl_det.addCell(Addons.setCeldaPDF("", Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_LEFT, 1));
                                    tbl_det.addCell(Addons.setCeldaPDF("", Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_LEFT, 1));
                                }
                                auxDet = detalle;
                                tbl_det.addCell(Addons.setCeldaPDF(((rsLibroDiario.getString("fecha_asiento")!=null) ? Fecha.ISOaSQL(rsLibroDiario.getString("fecha_asiento")) : ""), Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_LEFT, 1));
                                tbl_det.addCell(Addons.setCeldaPDF("- "+num_asiento+" -", Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_CENTER, 1, 4, 4));
                                aux = num_asiento;
                            }
                            tbl_det.addCell(Addons.setCeldaPDF("", Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_LEFT, 1));
                            tbl_det.addCell(Addons.setCeldaPDF(((rsLibroDiario.getString("codigo_cuenta")!=null) ? rsLibroDiario.getString("codigo_cuenta") : ""), Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_LEFT, 1));
                            tbl_det.addCell(Addons.setCeldaPDF(((rsLibroDiario.getString("nombre_cuenta")!=null) ? rsLibroDiario.getString("nombre_cuenta") : ""), Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_LEFT, 1));
                            tbl_det.addCell(Addons.setCeldaPDF(debe, Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_RIGHT, 1));
                            tbl_det.addCell(Addons.setCeldaPDF(haber, Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_RIGHT, 1));
                        }
                        tbl_det.addCell(Addons.setCeldaPDF("", Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_LEFT, 1));
                        tbl_det.addCell(Addons.setCeldaPDF("", Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_LEFT, 1));
                        tbl_det.addCell(Addons.setCeldaPDF(auxDet, Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_LEFT, 1));
                        tbl_det.addCell(Addons.setCeldaPDF("", Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_LEFT, 1));
                        tbl_det.addCell(Addons.setCeldaPDF("", Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_LEFT, 1));

                        rsLibroDiario.close();
                    }catch(Exception e){
                        e.printStackTrace();
                    }
                    document.add(tbl_det);

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
                    rsLibroDiario.close();
                }catch(SQLException e){
                    e.printStackTrace();
                }
                objLibroDiario.cerrar();
            }
        }

        public void onStartPage(PdfWriter writer, Document document)
        {
            Addons.setEncabezado(writer, document, subtitulo);
        }

        public void onEndPage(PdfWriter writer, Document document)
        {
            Addons.setPie(writer, document, rep_pie);
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

        String path = String.valueOf(request.getRequestURL());
        path = path.substring(0, path.lastIndexOf("/"));
        Archivo archivo = new Archivo(this._ip, this._puerto, this._db, this._usuario, this._clave);
        this.logo = path + "/img/" + archivo.getArchivo(this._dir, 1);
        archivo.cerrar();

        this.titulo = conf.getValor("razon_social");
        this.ruc = conf.getValor("ruc");
        this.subtitulo = "LIBRO DIARIO";
        this.direccion = conf.getValor("dir_matriz");
        this.rep_pie = conf.getValor("rep_pie");
        conf.cerrar();

        String fecha_ini = request.getParameter("fi");
        String fecha_fin = request.getParameter("ff");
        String id_sucursal = request.getParameter("idSuc");
        Sucursal objSucursal = new Sucursal(this._ip, this._puerto, this._db, this._usuario, this._clave);
        this.sucursal = objSucursal.getNombre(id_sucursal);
        objSucursal.cerrar();

        imLibroDiario objImpLibroDiario = new imLibroDiario();
        objImpLibroDiario.imprimir(response, id_sucursal, fecha_ini, fecha_fin);
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
