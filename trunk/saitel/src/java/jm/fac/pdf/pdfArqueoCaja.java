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

import jm.adm.clas.Configuracion;
import java.sql.ResultSet;
import jm.web.Addons;
import jm.web.Fecha;
import jm.fac.clas.ArqueoCaja;
import jm.nom.clas.Empleado;

/**
 *
 * @author Jorge
 */
public class pdfArqueoCaja extends HttpServlet {
    private String _ip = null;
    private int _puerto = 5432;
    private String _db = null;
    //private String _dir = null;

    public void init(ServletConfig config) throws ServletException
    {
        this._ip = config.getServletContext().getInitParameter("_IP");
        this._puerto = Integer.parseInt(config.getServletContext().getInitParameter("_PUERTO"));
        this._db = config.getServletContext().getInitParameter("_DB");
        //this._dir = config.getServletContext().getInitParameter("_DIR");
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

        response.setContentType("application/pdf");
        response.setHeader("Pragma", "no-cache");
        response.setHeader("Expires", "Mon, 01 Jan 2001 00:00:01 GMT");
        response.setHeader("Cache-Control", "no-store");
        response.setHeader("Cache-Control", "must-revalidate");
        response.setHeader("Cache-Control", "no-cache");

        //PrintWriter out = response.getWriter();

        Configuracion conf = new Configuracion(this._ip, this._puerto, this._db, usuario, clave);
        String titulo = conf.getValor("razon_social");
        conf.cerrar();

        String path = String.valueOf(request.getRequestURL());
        path = path.substring(0, path.lastIndexOf("/"));
        /*Archivo archivo = new Archivo(this._ip, this._puerto, this._db, usuario, clave);
        String logo = path + "/img/" + archivo.getArchivo(this._dir, 1);
        archivo.cerrar();*/

        ArqueoCaja objArqueoCaja = new ArqueoCaja(this._ip, this._puerto, this._db, usuario, clave);
        
        Empleado objEmpleado = new Empleado(this._ip, this._puerto, this._db, usuario, clave);

        String id = request.getParameter("id");
        //int i=0;

        String num_documento = "0";
        String fecha = Fecha.getFecha("SQL");
        String hora = Fecha.getHora();
        String cajero = "";
        String saldo_caja = "0";
        String num_cheques = "0";
        String valor_cheques = "0";
        String num_comp_pagos = "0";
        String valor_comp_pagos = "0";
        String num_retenciones = "0";
        String valor_retenciones = "0";
        String valor_efectivo = "0";
        String total_caja = "0";
        String diferencia = "0";
        String tipo_diferencia = "0";
        //boolean anulado = false;

        if(id.compareTo("-1")!=0){
            try{
                ResultSet rsArqueoCaja = objArqueoCaja.getArqueoCaja(id);
                if(rsArqueoCaja.next()){
                    num_documento = (rsArqueoCaja.getString("num_documento")!=null) ? rsArqueoCaja.getString("num_documento") : "0";
                    fecha = (rsArqueoCaja.getString("fecha")!=null) ? rsArqueoCaja.getString("fecha") : "";
                    hora = (rsArqueoCaja.getString("Hora")!=null) ? rsArqueoCaja.getString("Hora") : "";
                    cajero = (rsArqueoCaja.getString("cajero")!=null) ? rsArqueoCaja.getString("cajero") : "";
                    saldo_caja = (rsArqueoCaja.getString("saldo_caja")!=null) ? rsArqueoCaja.getString("saldo_caja") : "";
                    num_cheques = (rsArqueoCaja.getString("num_cheques")!=null) ? rsArqueoCaja.getString("num_cheques") : "0.00";
                    valor_cheques = (rsArqueoCaja.getString("valor_cheques")!=null) ? rsArqueoCaja.getString("valor_cheques") : "0.00";
                    num_comp_pagos = (rsArqueoCaja.getString("num_comp_pagos")!=null) ? rsArqueoCaja.getString("num_comp_pagos") : "0.00";
                    valor_comp_pagos = (rsArqueoCaja.getString("valor_comp_pagos")!=null) ? rsArqueoCaja.getString("valor_comp_pagos") : "0.00";
                    num_retenciones = (rsArqueoCaja.getString("num_retenciones")!=null) ? rsArqueoCaja.getString("num_retenciones") : "0";
                    valor_retenciones = (rsArqueoCaja.getString("valor_retenciones")!=null) ? rsArqueoCaja.getString("valor_retenciones") : "0.00";
                    
                    valor_efectivo = (rsArqueoCaja.getString("valor_efectivo")!=null) ? rsArqueoCaja.getString("valor_efectivo") : "0.00";
                    total_caja = (rsArqueoCaja.getString("total")!=null) ? rsArqueoCaja.getString("total") : "0.00";
                    diferencia = (rsArqueoCaja.getString("diferencia")!=null) ? rsArqueoCaja.getString("diferencia") : "0.00";
                    tipo_diferencia = (rsArqueoCaja.getString("tipo_diferencia")!=null) ? rsArqueoCaja.getString("tipo_diferencia") : "";
                    //anulado = (rsArqueoCaja.getString("anulado")!=null) ? rsArqueoCaja.getBoolean("anulado") : false;
                    rsArqueoCaja.close();
                }
            }catch(Exception e){
                e.printStackTrace();
            }
        }
        
        
        String sucursal = "";
        String empleado = "";
        try{
            ResultSet rs = objEmpleado.getCajero(cajero);
            if(rs.next()){
                sucursal = rs.getString("sucursal")!=null ? rs.getString("sucursal") : "";
                empleado = rs.getString("empleado")!=null ? rs.getString("empleado") : "";
            }
        }catch(Exception e){
            e.printStackTrace();
        }




        /* inicio PDF */
        Document doc = new Document(PageSize.A4);// paso 1

        /* tambien establesco el alto del tope para la primera impresion de la factura */

        float ancho = PageSize.A4.getWidth();
        float alto = PageSize.A4.getHeight();

        try{
            PdfWriter writer = PdfWriter.getInstance(doc, response.getOutputStream()); // paso 2

            doc.open(); // paso 3


            // Para enviar a la impresora automÃ¡ticamente.

            writer.addJavaScript("this.print(false);", false);

            /* todo el cuerpo del doc es el paso 4 */


            PdfPTable tbl_titulo = new PdfPTable(1);
            tbl_titulo.addCell(Addons.setCeldaPDF(titulo, Font.HELVETICA, 14, Font.NORMAL, Element.ALIGN_CENTER, 0));
            tbl_titulo.addCell(Addons.setCeldaPDF("ARQUEO DE CAJA\n\n", Font.HELVETICA, 14, Font.BOLD, Element.ALIGN_CENTER, 0));
            tbl_titulo.addCell(Addons.setCeldaPDF("No.   " + num_documento + "\n", Font.HELVETICA, 12, Font.NORMAL, Element.ALIGN_RIGHT, 0));
            tbl_titulo.addCell(Addons.setCeldaPDF("Sucursal: " + sucursal, Font.HELVETICA, 10, Font.NORMAL, Element.ALIGN_LEFT, 0));
            tbl_titulo.addCell(Addons.setCeldaPDF("Usuario de caja: " + cajero, Font.HELVETICA, 10, Font.NORMAL, Element.ALIGN_LEFT, 0));
            tbl_titulo.addCell(Addons.setCeldaPDF("Responsable de caja: " + empleado, Font.HELVETICA, 10, Font.NORMAL, Element.ALIGN_LEFT, 0));
            doc.add(tbl_titulo);


            PdfPTable tbl_det = new PdfPTable(2);

            tbl_det.addCell(Addons.setCeldaPDF("Fecha: "+fecha, Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_LEFT, 0));
            tbl_det.addCell(Addons.setCeldaPDF("Hora: "+hora, Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_LEFT, 0));

            tbl_det.addCell(Addons.setFilaBlanco(3, 10));

            doc.add(tbl_det);


            /* efectivo */
            try{
                String denominacion = "";
                String cantidad = "";
                String total = "";
                ResultSet rsArqueoEfectivo = objArqueoCaja.getArqueoCajaEfectivo(id);
                if(objArqueoCaja.getFilas(rsArqueoEfectivo)>0){
                    doc.add(Addons.setCabeceraTabla(new String [] {"DENOMINACION", "CANTIDAD", "TOTAL ($)"}, new float[]{60f,20f,20f}));
                    PdfPTable tbl_efectivo = new PdfPTable(new float [] {60f,20f,20f});
                    while(rsArqueoEfectivo.next()){
                        denominacion = (rsArqueoEfectivo.getString("denominacion")!=null) ? rsArqueoEfectivo.getString("denominacion") : "";
                        cantidad = (rsArqueoEfectivo.getString("cantidad")!=null) ? rsArqueoEfectivo.getString("cantidad") : "";
                        total = (rsArqueoEfectivo.getString("total")!=null) ? rsArqueoEfectivo.getString("total") : "";
                        tbl_efectivo.addCell(Addons.setCeldaPDF(denominacion, Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_LEFT, 1));
                        tbl_efectivo.addCell(Addons.setCeldaPDF(cantidad, Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_CENTER, 1));
                        tbl_efectivo.addCell(Addons.setCeldaPDF(total, Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_RIGHT, 1));
                    }
                    tbl_efectivo.addCell(Addons.setFilaBlanco(3, 10));
                    doc.add(tbl_efectivo);
                }
                rsArqueoEfectivo.close();
            }catch(Exception e){
                e.printStackTrace();
            }




            /* cheque */
            try{
                String efectivizado = "";
                String banco = "";
                String num_cheque = "";
                String valor = "";
                ResultSet rsArqueoCheque = objArqueoCaja.getArqueoCajaCheque(id);
                if(objArqueoCaja.getFilas(rsArqueoCheque)>0){
                    doc.add(Addons.setCabeceraTabla(new String [] {"BANCO", "Nro. CHEQUE", "VALOR ($)", "EFECTIVIZADO"}, new float[]{60f,30f,20f,15f}));
                    PdfPTable tbl_cheque = new PdfPTable(new float [] {60f,30f,20f,15f});
                    while(rsArqueoCheque.next()){
                        efectivizado = (rsArqueoCheque.getString("efectivizado")!=null) ? rsArqueoCheque.getString("efectivizado") : "";
                        banco = (rsArqueoCheque.getString("banco")!=null) ? rsArqueoCheque.getString("banco") : "";
                        num_cheque = (rsArqueoCheque.getString("num_cheque")!=null) ? rsArqueoCheque.getString("num_cheque") : "";
                        valor = (rsArqueoCheque.getString("valor")!=null) ? rsArqueoCheque.getString("valor") : "";
                        tbl_cheque.addCell(Addons.setCeldaPDF(banco, Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_LEFT, 1));
                        tbl_cheque.addCell(Addons.setCeldaPDF(num_cheque, Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_LEFT, 1));
                        tbl_cheque.addCell(Addons.setCeldaPDF(valor, Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_RIGHT, 1));
                        tbl_cheque.addCell(Addons.setCeldaPDF(efectivizado, Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_RIGHT, 1));
                    }
                    tbl_cheque.addCell(Addons.setFilaBlanco(4, 10));
                    doc.add(tbl_cheque);
                }
                rsArqueoCheque.close();
            }catch(Exception e){
                e.printStackTrace();
            }



            /* otros documentos */
            try{
                String num_comp_pago = "";
                String valor = "";
                ResultSet rsArqueoDocs = objArqueoCaja.getArqueoCajaOtros(id);
                if(objArqueoCaja.getFilas(rsArqueoDocs)>0){
                    doc.add(Addons.setCabeceraTabla(new String [] {"Nro. COMP. PAGO", "VALOR ($)"}, new float[]{70f,30f}));
                    PdfPTable tbl_otros = new PdfPTable(new float [] {70f,30f});
                    while(rsArqueoDocs.next()){
                        num_comp_pago = (rsArqueoDocs.getString("num_comp_pago")!=null) ? rsArqueoDocs.getString("num_comp_pago") : "";
                        valor = (rsArqueoDocs.getString("valor")!=null) ? rsArqueoDocs.getString("valor") : "0";
                        tbl_otros.addCell(Addons.setCeldaPDF(num_comp_pago, Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_LEFT, 1));
                        tbl_otros.addCell(Addons.setCeldaPDF(valor, Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_RIGHT, 1));
                    }
                    tbl_otros.addCell(Addons.setFilaBlanco(3, 10));
                    doc.add(tbl_otros);
                }
                rsArqueoDocs.close();
            }catch(Exception e){
                e.printStackTrace();
            }


            /* retenciones */
            try{
                String num_retencion = "";
                String valor_retencion = "";
                ResultSet rsArqueoRets = objArqueoCaja.getArqueoCajaRetenciones(id);
                if(objArqueoCaja.getFilas(rsArqueoRets)>0){
                    doc.add(Addons.setCabeceraTabla(new String [] {"Nro. RETENCION", "VALOR ($)"}, new float[]{70f,30f}));
                    PdfPTable tbl_otros = new PdfPTable(new float [] {70f,30f});
                    while(rsArqueoRets.next()){
                        num_retencion = (rsArqueoRets.getString("num_retencion")!=null) ? rsArqueoRets.getString("num_retencion") : "";
                        valor_retencion = (rsArqueoRets.getString("valor_retencion")!=null) ? rsArqueoRets.getString("valor_retencion") : "0";
                        tbl_otros.addCell(Addons.setCeldaPDF(num_retencion, Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_LEFT, 1));
                        tbl_otros.addCell(Addons.setCeldaPDF(valor_retencion, Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_RIGHT, 1));
                    }
                    tbl_otros.addCell(Addons.setFilaBlanco(3, 10));
                    doc.add(tbl_otros);
                }
                rsArqueoRets.close();
            }catch(Exception e){
                e.printStackTrace();
            }


            PdfPTable tbl_totales = new PdfPTable(new float [] {60f,40f});
            tbl_totales.addCell(Addons.setFilaBlanco(3, 5));

            tbl_totales.addCell(Addons.setCeldaPDF("Saldo en caja actual:", Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_LEFT, 1));
            tbl_totales.addCell(Addons.setCeldaPDF(saldo_caja, Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_RIGHT, 1));

            tbl_totales.addCell(Addons.setCeldaPDF("Total monedas y billetes:", Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_LEFT, 1));
            tbl_totales.addCell(Addons.setCeldaPDF(valor_efectivo, Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_RIGHT, 1));

            tbl_totales.addCell(Addons.setCeldaPDF("Total cheques ("+num_cheques+"):", Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_LEFT, 1));
            tbl_totales.addCell(Addons.setCeldaPDF(valor_cheques, Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_RIGHT, 1));

            tbl_totales.addCell(Addons.setCeldaPDF("Total comprobantes de pago ("+num_comp_pagos+"):", Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_LEFT, 1));
            tbl_totales.addCell(Addons.setCeldaPDF(valor_comp_pagos, Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_RIGHT, 1));

            tbl_totales.addCell(Addons.setCeldaPDF("Total retenciones ("+num_retenciones+"):", Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_LEFT, 1));
            tbl_totales.addCell(Addons.setCeldaPDF(valor_retenciones, Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_RIGHT, 1));

            tbl_totales.addCell(Addons.setCeldaPDF("Total en caja:", Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_LEFT, 1));
            tbl_totales.addCell(Addons.setCeldaPDF(total_caja, Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_RIGHT, 1));

            tbl_totales.addCell(Addons.setCeldaPDF("Diferencia:        " + (tipo_diferencia.compareTo("f")==0 ? "faltante" : (tipo_diferencia.compareTo("s")==0 ? "sobrante" : "")), Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_LEFT, 1));
            tbl_totales.addCell(Addons.setCeldaPDF(diferencia, Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_RIGHT, 1));

            doc.add(tbl_totales);


            doc.add(new Paragraph(" "));
            doc.add(new Paragraph(" "));
            doc.add(new Paragraph(" "));


            PdfPTable tbl_firmas = new PdfPTable(3);
            tbl_firmas.addCell(Addons.setCeldaPDF("RESPONSABLE", Font.HELVETICA, 10, Font.NORMAL, Element.ALIGN_CENTER, 0));
            tbl_firmas.addCell(Addons.setCeldaPDF("VERIFICADO", Font.HELVETICA, 10, Font.NORMAL, Element.ALIGN_CENTER, 0));
            tbl_firmas.addCell(Addons.setCeldaPDF("AUTORIZADO", Font.HELVETICA, 10, Font.NORMAL, Element.ALIGN_CENTER, 0));
            doc.add(tbl_firmas);

        }catch(IllegalStateException ie){
            ie.printStackTrace();
        }catch(DocumentException e){
            e.printStackTrace();
        }finally{
            objEmpleado.cerrar();
            objArqueoCaja.cerrar();
        }

        doc.close(); // paso 5
        /* fin PDF */
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
