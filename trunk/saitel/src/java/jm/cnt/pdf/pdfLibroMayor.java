/**
* @version 3.0
* @package YAKUSOFT.
* @author Jorge Washington Mueses Cevallos.
* @copyright Copyright (C) 2010 por Jorge Mueses. Todos los derechos reservados.
* @license http://www.gnu.org/copyleft/gpl.html GNU/GPL.
* YAKUSOFT! es un software de libre distribución, que puede ser
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
import jm.cnt.clas.LibroMayor;
import jm.cnt.clas.PlanCuenta;

import java.sql.ResultSet;
import java.sql.SQLException;
import jm.web.Fecha;

/**
 *
 * @author Jorge
 */
public class pdfLibroMayor extends HttpServlet {
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

    class imLibroMayor extends PdfPageEventHelper{
        public imLibroMayor(){}

        public void imprimir(HttpServletResponse response, String id_sucursal, String id_plan_cuenta, String fecha_ini, String fecha_fin){
            response.setContentType("application/pdf");
            response.setHeader("Pragma", "no-cache");
            response.setHeader("Expires", "Mon, 01 Jan 2001 00:00:01 GMT");
            response.setHeader("Cache-Control", "no-store");
            response.setHeader("Cache-Control", "must-revalidate");
            response.setHeader("Cache-Control", "no-cache");

            String [] cabTabla = null;
            float [] anchoTabla = null;
            ResultSet rsLibroMayor = null;
            String codigo_cuenta = "";
            String nombre_cuenta = "";
            float sum_debe = 0;
            float sum_haber = 0;
            LibroMayor objLibroMayor = new LibroMayor(_ip, _puerto, _db, _usuario, _clave);
            try {
                PlanCuenta objPlanCuenta = new PlanCuenta(_ip, _puerto, _db, _usuario, _clave);
                ResultSet rsTablas = objPlanCuenta.getTabla(id_plan_cuenta);
                try{
                    if(rsTablas.next()){
                        codigo_cuenta = (rsTablas.getString("codigo_cuenta")!=null) ? rsTablas.getString("codigo_cuenta") : "";
                        nombre_cuenta = (rsTablas.getString("nombre_cuenta")!=null) ? rsTablas.getString("nombre_cuenta") : "";
                    }
                    rsTablas.close();
                }catch(Exception e){
                    e.printStackTrace();
                }finally{
                    objPlanCuenta.cerrar();
                }

                rsLibroMayor = objLibroMayor.getTransacciones(id_sucursal, id_plan_cuenta, fecha_ini, fecha_fin);
                cabTabla = new String [] {"FECHA", "# ASIENTO", "CONCEPTO", "DEBE", "HABER", "SALDO DEUDOR", "SALDO ACREEDOR"};
                anchoTabla = new float[]{40f,25f,130f,40f,40f,40f,40f};

                /* inicio PDF */
                Document document = new Document(PageSize.A4);// paso 1
                document.setMargins(0,0,50,80); /*Izquierda, derecha, tope, pie */
                try{
                    PdfWriter writer = PdfWriter.getInstance(document, response.getOutputStream()); // paso 2
                    writer.setPageEvent(new imLibroMayor());

                    document.open(); // paso 3

                    writer.addJavaScript("this.print(false);", false); // Para enviar a la impresora automáticamente.

                    /* todo el cuerpo del doc es el paso 4 */

                    document.add(Addons.setCabecera(logo, titulo, ruc, subtitulo, direccion, sucursal));


                    if(objLibroMayor.cuentaMovimiento(id_plan_cuenta)){
                        PdfPTable tbl_cuenta = new PdfPTable(1);
                        tbl_cuenta.addCell(Addons.setCeldaPDF(" ", Font.HELVETICA, 10, Font.NORMAL, Element.ALIGN_LEFT, 0));
                        tbl_cuenta.addCell(Addons.setCeldaPDF("CÓDIGO: " + codigo_cuenta, Font.HELVETICA, 10, Font.NORMAL, Element.ALIGN_LEFT, 0));
                        tbl_cuenta.addCell(Addons.setCeldaPDF("CUENTA: " + nombre_cuenta, Font.HELVETICA, 10, Font.NORMAL, Element.ALIGN_LEFT, 0));
                        document.add(tbl_cuenta);


                        document.add(Addons.setCabeceraTabla(cabTabla, anchoTabla));

                        PdfPTable tbl_det = new PdfPTable(anchoTabla);
                        try{
                            String debe = "";
                            String haber = "";
                            String sdebe = "";
                            String shaber = "";
                            while(rsLibroMayor.next()){
                                debe = (rsLibroMayor.getString("debe")!=null) ? ((rsLibroMayor.getDouble("debe")>0)?rsLibroMayor.getString("debe"):"") : "";
                                haber = (rsLibroMayor.getString("haber")!=null) ? ((rsLibroMayor.getDouble("haber")>0)?rsLibroMayor.getString("haber"):"") : "";
                                sdebe = (rsLibroMayor.getString("saldo_deudor")!=null) ? ((rsLibroMayor.getDouble("saldo_deudor")!=0)?rsLibroMayor.getString("saldo_deudor"):"") : "";
                                shaber = (rsLibroMayor.getString("saldo_acreedor")!=null) ? ((rsLibroMayor.getDouble("saldo_acreedor")!=0)?rsLibroMayor.getString("saldo_acreedor"):"") : "";
                                if(id_sucursal.compareTo("-0")!=0){
                                    sdebe = (rsLibroMayor.getString("suc_saldo_deudor")!=null) ? ((rsLibroMayor.getDouble("suc_saldo_deudor")!=0)?rsLibroMayor.getString("suc_saldo_deudor"):"") : "";
                                    shaber = (rsLibroMayor.getString("suc_saldo_acreedor")!=null) ? ((rsLibroMayor.getDouble("suc_saldo_acreedor")!=0)?rsLibroMayor.getString("suc_saldo_acreedor"):"") : "";
                                }
                                tbl_det.addCell(Addons.setCeldaPDF(((rsLibroMayor.getString("fecha_asiento")!=null) ? Fecha.ISOaSQL(rsLibroMayor.getString("fecha_asiento")) : ""), Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_CENTER, 1));
                                tbl_det.addCell(Addons.setCeldaPDF(((rsLibroMayor.getString("num_asiento")!=null) ? rsLibroMayor.getString("num_asiento") : ""), Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_CENTER, 1));
                                tbl_det.addCell(Addons.setCeldaPDF(((rsLibroMayor.getString("detalle")!=null) ? rsLibroMayor.getString("detalle") : ""), Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_LEFT, 1));
                                tbl_det.addCell(Addons.setCeldaPDF(debe, Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_RIGHT, 1));
                                tbl_det.addCell(Addons.setCeldaPDF(haber, Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_RIGHT, 1));
                                tbl_det.addCell(Addons.setCeldaPDF(sdebe, Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_RIGHT, 1));
                                tbl_det.addCell(Addons.setCeldaPDF(shaber, Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_RIGHT, 1));
                                sum_debe += debe.compareTo("")!=0 ? Float.parseFloat(debe) : 0;
                                sum_haber += haber.compareTo("")!=0 ? Float.parseFloat(haber) : 0;
                            }
                            tbl_det.addCell(Addons.setCeldaPDF("SUMAS", Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_LEFT, 1, 4, 3));
                            tbl_det.addCell(Addons.setCeldaPDF(String.valueOf(sum_debe), Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_LEFT, 1));
                            tbl_det.addCell(Addons.setCeldaPDF(String.valueOf(sum_haber), Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_LEFT, 1));
                            tbl_det.addCell(Addons.setCeldaPDF("", Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_LEFT, 1, 4, 2));

                            rsLibroMayor.close();
                        }catch(Exception e){
                            e.printStackTrace();
                        }
                        document.add(tbl_det);
                    }else{
                        document.add(new Paragraph("La cuenta no es de movimiento."));
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

            } finally {
                try{
                    rsLibroMayor.close();
                }catch(SQLException e){
                    e.printStackTrace();
                }
                objLibroMayor.cerrar();
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
        this.subtitulo = "MAYOR GENERAL";
        this.direccion = conf.getValor("dir_matriz");
        this.rep_pie = conf.getValor("rep_pie");
        conf.cerrar();

        String id_plan_cuenta = request.getParameter("idPC");
        String fecha_ini = request.getParameter("fi");
        String fecha_fin = request.getParameter("ff");
        String id_sucursal = request.getParameter("idSuc");
        Sucursal objSucursal = new Sucursal(this._ip, this._puerto, this._db, this._usuario, this._clave);
        this.sucursal = objSucursal.getNombre(id_sucursal);
        objSucursal.cerrar();

        imLibroMayor objImpLibroDiario = new imLibroMayor();
        objImpLibroDiario.imprimir(response, id_sucursal, id_plan_cuenta, fecha_ini, fecha_fin);
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