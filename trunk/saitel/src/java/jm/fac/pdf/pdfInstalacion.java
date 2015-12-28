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
import jm.fac.clas.Instalacion;
import jm.fac.clas.PlanServicio;
import jm.fac.clas.Sector;
import jm.web.Archivo;
import jm.web.Cadena;
import jm.web.Fecha;

/**
 *
 * @author Jorge
 */
public class pdfInstalacion extends HttpServlet {
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

        public void imprimir(HttpServletResponse response, ResultSet RS, String id, String path, String logo, String cargo, String representante, String ruc){
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

                String radusername = "";
                String radclave = "";
                String deviceclave = "";
                if(RS.next()){
                    String fecha_instalacion = (RS.getString("fecha_instalacion")!=null?RS.getString("fecha_instalacion"):"");
                    String fecha_registro = (RS.getString("fecha_registro")!=null?RS.getString("fecha_registro"):"");
                    String id_sector = RS.getString("id_sector")!=null?RS.getString("id_sector"):"-1";
                    radusername = RS.getString("radusername")!=null ? RS.getString("radusername") : "";
                    radclave = RS.getString("radclave")!=null ? RS.getString("radclave") : "";
                    deviceclave = RS.getString("deviceclave")!=null ? RS.getString("deviceclave") : "";
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

                    String id_plan_contratado = RS.getString("id_plan_contratado")!=null?RS.getString("id_plan_contratado"):"-1";
                    String plan = "";
                    String burst_limit = "";
                    String max_limit = "";
                    String costo_plan = "";
                    String nivel_soporte = "";
                    PlanServicio objPlanServicio = new PlanServicio(_ip, _puerto, _db, _usuario, _clave);
                    
                    try{
                        ResultSet rsPlanServicio = objPlanServicio.getVelocidad(id_plan_contratado);
                        if(rsPlanServicio.next()){
                            plan = rsPlanServicio.getString("plan")!=null ? rsPlanServicio.getString("plan") : "";
                            burst_limit = rsPlanServicio.getString("burst_limit")!=null ? rsPlanServicio.getString("burst_limit") : "";
                            max_limit = rsPlanServicio.getString("max_limit")!=null ? rsPlanServicio.getString("max_limit") : "";
                            nivel_soporte = rsPlanServicio.getString("texto")!=null ? rsPlanServicio.getString("texto") : "";
                            rsPlanServicio.close();
                        }
                    }catch(Exception ep){}

                    try{
                        ResultSet rsTarifa = objPlanServicio.getTarifaVigente(id_plan_contratado, fecha_registro);
                        if(rsTarifa.next()){
                            costo_plan = rsTarifa.getString("costo_plan")!=null ? rsTarifa.getString("costo_plan") : "";
                            rsTarifa.close();
                        }
                    }catch(Exception ep){
                        ep.printStackTrace();
                    }finally{
                        objPlanServicio.cerrar();
                    }


                    /* ORDEN de servicio */
                    

                    PdfPTable tbl_titulo = new PdfPTable(new float[]{20f,80f});
                    try{
                        tbl_titulo.addCell(Addons.setLogo(logo, 70, 70));
                    }catch(Exception e){
                        tbl_titulo.addCell("");
                    }
                    tbl_titulo.addCell(Addons.setCeldaPDF("\nACUERDO DE PRESTACIÓN DE SERVICIOS", Font.HELVETICA, 14, Font.BOLD, Element.ALIGN_LEFT, 0));
                    document.add(tbl_titulo);

                    document.add(new Paragraph(" "));

                    PdfPTable tbl_det = new PdfPTable(new float[]{40f,60f});
                    tbl_det.addCell(Addons.setCeldaPDF("NÚMERO", Font.HELVETICA, 10, Font.BOLD, Element.ALIGN_LEFT, 1));
                    tbl_det.addCell(Addons.setCeldaPDF((RS.getString("id_sucursal")!=null?RS.getString("id_sucursal"):"") + "-" + (RS.getString("num_orden_servicio")!=null?RS.getString("num_orden_servicio"):""), Font.HELVETICA, 10, Font.NORMAL, Element.ALIGN_LEFT, 1));

                    tbl_det.addCell(Addons.setCeldaPDF("CÉDULA", Font.HELVETICA, 10, Font.BOLD, Element.ALIGN_LEFT, 1));
                    tbl_det.addCell(Addons.setCeldaPDF((RS.getString("ruc")!=null?RS.getString("ruc"):""), Font.HELVETICA, 10, Font.NORMAL, Element.ALIGN_LEFT, 1));

                    tbl_det.addCell(Addons.setCeldaPDF("CLIENTE", Font.HELVETICA, 10, Font.BOLD, Element.ALIGN_LEFT, 1));
                    tbl_det.addCell(Addons.setCeldaPDF((RS.getString("razon_social")!=null?RS.getString("razon_social"):""), Font.HELVETICA, 10, Font.NORMAL, Element.ALIGN_LEFT, 1));

                    tbl_det.addCell(Addons.setCeldaPDF("TELÉFONO", Font.HELVETICA, 10, Font.BOLD, Element.ALIGN_LEFT, 1));
                    tbl_det.addCell(Addons.setCeldaPDF((RS.getString("telefono")!=null?RS.getString("telefono"):"") + 
                                    "    Claro:" + (RS.getString("movil_claro")!=null?RS.getString("movil_claro"):"") + 
                                    "    Movistar:" + (RS.getString("movil_movistar")!=null?RS.getString("movil_movistar"):""), Font.HELVETICA, 10, Font.NORMAL, Element.ALIGN_LEFT, 1));

                    tbl_det.addCell(Addons.setCeldaPDF("DIRECCIÓN DEL CLIENTE", Font.HELVETICA, 10, Font.BOLD, Element.ALIGN_LEFT, 1));
                    tbl_det.addCell(Addons.setCeldaPDF((RS.getString("direccion")!=null?RS.getString("direccion"):""), Font.HELVETICA, 10, Font.NORMAL, Element.ALIGN_LEFT, 1));

                    tbl_det.addCell(Addons.setCeldaPDF("CIUDAD DEL CLIENTE", Font.HELVETICA, 10, Font.BOLD, Element.ALIGN_LEFT, 1));
                    tbl_det.addCell(Addons.setCeldaPDF((RS.getString("ciudad")!=null?RS.getString("ciudad"):""), Font.HELVETICA, 10, Font.NORMAL, Element.ALIGN_LEFT, 1));

                    tbl_det.addCell(Addons.setCeldaPDF("DIRECCIÓN DE INSTALACIÓN", Font.HELVETICA, 10, Font.BOLD, Element.ALIGN_LEFT, 1));
                    tbl_det.addCell(Addons.setCeldaPDF((RS.getString("direccion_instalacion")!=null?RS.getString("direccion_instalacion"):""), Font.HELVETICA, 10, Font.NORMAL, Element.ALIGN_LEFT, 1));

                    tbl_det.addCell(Addons.setCeldaPDF("COSTO DE INSTALACIÓN", Font.HELVETICA, 10, Font.BOLD, Element.ALIGN_LEFT, 1));
                    tbl_det.addCell(Addons.setCeldaPDF((RS.getString("costo_instalacion")!=null?RS.getString("costo_instalacion"):""), Font.HELVETICA, 10, Font.NORMAL, Element.ALIGN_LEFT, 1));

                    tbl_det.addCell(Addons.setCeldaPDF("FECHA DE ORDEN DE INSTALACIÓN", Font.HELVETICA, 10, Font.BOLD, Element.ALIGN_LEFT, 1));
                    tbl_det.addCell(Addons.setCeldaPDF(Fecha.ISOaSQL(fecha_registro), Font.HELVETICA, 10, Font.NORMAL, Element.ALIGN_LEFT, 1));

                    tbl_det.addCell(Addons.setCeldaPDF("SECTOR", Font.HELVETICA, 10, Font.BOLD, Element.ALIGN_LEFT, 1));
                    tbl_det.addCell(Addons.setCeldaPDF(sector, Font.HELVETICA, 10, Font.NORMAL, Element.ALIGN_LEFT, 1));

                    tbl_det.addCell(Addons.setCeldaPDF("DIRECCIÓN IP", Font.HELVETICA, 10, Font.BOLD, Element.ALIGN_LEFT, 1));
                    tbl_det.addCell(Addons.setCeldaPDF((RS.getString("ip")!=null?RS.getString("ip"):""), Font.HELVETICA, 10, Font.NORMAL, Element.ALIGN_LEFT, 1));

                    tbl_det.addCell(Addons.setCeldaPDF("PLAN CONTRATADO", Font.HELVETICA, 10, Font.BOLD, Element.ALIGN_LEFT, 1));
                    tbl_det.addCell(Addons.setCeldaPDF(plan, Font.HELVETICA, 10, Font.NORMAL, Element.ALIGN_LEFT, 1));

                    tbl_det.addCell(Addons.setCeldaPDF("VELOCIDAD DE ACCESO CONTRATADA", Font.HELVETICA, 10, Font.BOLD, Element.ALIGN_LEFT, 1));
                    tbl_det.addCell(Addons.setCeldaPDF(burst_limit + " Kbps", Font.HELVETICA, 10, Font.NORMAL, Element.ALIGN_LEFT, 1));
                    
                    tbl_det.addCell(Addons.setCeldaPDF("VELOCIDAD MÍNIMA EFECTIVA", Font.HELVETICA, 10, Font.BOLD, Element.ALIGN_LEFT, 1));
                    tbl_det.addCell(Addons.setCeldaPDF(max_limit + " Kbps", Font.HELVETICA, 10, Font.NORMAL, Element.ALIGN_LEFT, 1));
                    
                    float comparticion = (RS.getString("comparticion")!=null?RS.getFloat("comparticion"): 8);
                    tbl_det.addCell(Addons.setCeldaPDF("CONDICIÓN DEL CANAL", Font.HELVETICA, 10, Font.BOLD, Element.ALIGN_LEFT, 1));
                    tbl_det.addCell(Addons.setCeldaPDF((comparticion==1 ? "No compartido" : "Compartido"), Font.HELVETICA, 10, Font.NORMAL, Element.ALIGN_LEFT, 1));
                    
                    tbl_det.addCell(Addons.setCeldaPDF("NIVEL DE COMPARTICIÓN", Font.HELVETICA, 10, Font.BOLD, Element.ALIGN_LEFT, 1));
                    tbl_det.addCell(Addons.setCeldaPDF((RS.getString("comparticion")!=null?RS.getString("comparticion"):"") + " - 1", Font.HELVETICA, 10, Font.NORMAL, Element.ALIGN_LEFT, 1));

                    tbl_det.addCell(Addons.setCeldaPDF("VALOR MENSUAL A PAGAR", Font.HELVETICA, 10, Font.BOLD, Element.ALIGN_LEFT, 1));
                    tbl_det.addCell(Addons.setCeldaPDF(costo_plan, Font.HELVETICA, 10, Font.NORMAL, Element.ALIGN_LEFT, 1));

                    tbl_det.addCell(Addons.setCeldaPDF("DISPONIBILIDAD DEL SERVICIO", Font.HELVETICA, 10, Font.BOLD, Element.ALIGN_LEFT, 1));
                    tbl_det.addCell(Addons.setCeldaPDF("96% mensual", Font.HELVETICA, 10, Font.NORMAL, Element.ALIGN_LEFT, 1));
                    
                    tbl_det.addCell(Addons.setCeldaPDF("NIVEL DE SOPORTE", Font.HELVETICA, 10, Font.BOLD, Element.ALIGN_LEFT, 1));
                    tbl_det.addCell(Addons.setCeldaPDF(nivel_soporte, Font.HELVETICA, 10, Font.NORMAL, Element.ALIGN_LEFT, 1));

                    document.add(tbl_det);

                    document.add(new Paragraph(" "));
                    document.add(new Paragraph(" "));
                    document.add(new Paragraph(" "));

                    PdfPTable tbl_firma = new PdfPTable(2);
                    tbl_firma.addCell(Addons.setCeldaPDF("__________________________", Font.HELVETICA, 10, Font.NORMAL, Element.ALIGN_LEFT, 0));
                    tbl_firma.addCell(Addons.setCeldaPDF("__________________________", Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_LEFT, 0));
                    tbl_firma.addCell(Addons.setCeldaPDF(cargo, Font.HELVETICA, 10, Font.NORMAL, Element.ALIGN_LEFT, 0));
                    tbl_firma.addCell(Addons.setCeldaPDF("EL SUBSCRIPTOR", Font.HELVETICA, 10, Font.NORMAL, Element.ALIGN_LEFT, 0));
                    tbl_firma.addCell(Addons.setCeldaPDF(representante, Font.HELVETICA, 10, Font.NORMAL, Element.ALIGN_LEFT, 0));
                    tbl_firma.addCell(Addons.setCeldaPDF(Cadena.capital(RS.getString("razon_social")!=null?RS.getString("razon_social"):""), Font.HELVETICA, 10, Font.NORMAL, Element.ALIGN_LEFT, 0));
                    tbl_firma.addCell(Addons.setCeldaPDF(ruc, Font.HELVETICA, 10, Font.NORMAL, Element.ALIGN_LEFT, 0));
                    tbl_firma.addCell(Addons.setCeldaPDF((RS.getString("ruc")!=null?RS.getString("ruc"):""), Font.HELVETICA, 10, Font.NORMAL, Element.ALIGN_LEFT, 0));
                    document.add(tbl_firma);



                    /*  Instalacion  */
/*
                    document.newPage();


                    PdfPTable tbl_titulo1 = new PdfPTable(new float[]{25f,75f});
                    try{
                        tbl_titulo1.addCell(Addons.setLogo(logo, 70, 70));
                    }catch(Exception e){
                        tbl_titulo1.addCell("");
                    }
                    tbl_titulo1.addCell(Addons.setCeldaPDF("\nINSTALACION DE INTERNET INALAMBRICO", Font.HELVETICA, 14, Font.BOLD, Element.ALIGN_LEFT, 0));
                    document.add(tbl_titulo1);

                    document.add(new Paragraph(" "));

                    PdfPTable tbl_det1 = new PdfPTable(new float[]{40f,60f});
                    tbl_det1.addCell(Addons.setCeldaPDF("NUMERO", Font.HELVETICA, 10, Font.BOLD, Element.ALIGN_LEFT, 1));
                    tbl_det1.addCell(Addons.setCeldaPDF((RS.getString("id_sucursal")!=null?RS.getString("id_sucursal"):"") + "-" + (RS.getString("num_instalacion")!=null?RS.getString("num_instalacion"):""), Font.HELVETICA, 10, Font.NORMAL, Element.ALIGN_LEFT, 1));

                    tbl_det1.addCell(Addons.setCeldaPDF("CEDULA", Font.HELVETICA, 10, Font.BOLD, Element.ALIGN_LEFT, 1));
                    tbl_det1.addCell(Addons.setCeldaPDF((RS.getString("ruc")!=null?RS.getString("ruc"):""), Font.HELVETICA, 10, Font.NORMAL, Element.ALIGN_LEFT, 1));

                    tbl_det1.addCell(Addons.setCeldaPDF("CLIENTE", Font.HELVETICA, 10, Font.BOLD, Element.ALIGN_LEFT, 1));
                    tbl_det1.addCell(Addons.setCeldaPDF((RS.getString("razon_social")!=null?RS.getString("razon_social"):""), Font.HELVETICA, 10, Font.NORMAL, Element.ALIGN_LEFT, 1));

                    tbl_det1.addCell(Addons.setCeldaPDF("TELEFONO", Font.HELVETICA, 10, Font.BOLD, Element.ALIGN_LEFT, 1));
                    tbl_det1.addCell(Addons.setCeldaPDF((RS.getString("telefono")!=null?RS.getString("telefono"):""), Font.HELVETICA, 10, Font.NORMAL, Element.ALIGN_LEFT, 1));

                    tbl_det1.addCell(Addons.setCeldaPDF("DIRECCION DEL CLIENTE", Font.HELVETICA, 10, Font.BOLD, Element.ALIGN_LEFT, 1));
                    tbl_det1.addCell(Addons.setCeldaPDF((RS.getString("direccion")!=null?RS.getString("direccion"):""), Font.HELVETICA, 10, Font.NORMAL, Element.ALIGN_LEFT, 1));

                    tbl_det1.addCell(Addons.setCeldaPDF("CIUDAD DEL CLIENTE", Font.HELVETICA, 10, Font.BOLD, Element.ALIGN_LEFT, 1));
                    tbl_det1.addCell(Addons.setCeldaPDF((RS.getString("ciudad")!=null?RS.getString("ciudad"):""), Font.HELVETICA, 10, Font.NORMAL, Element.ALIGN_LEFT, 1));

                    tbl_det1.addCell(Addons.setCeldaPDF("DIRECCION DE INSTALACION", Font.HELVETICA, 10, Font.BOLD, Element.ALIGN_LEFT, 1));
                    tbl_det1.addCell(Addons.setCeldaPDF((RS.getString("direccion_instalacion")!=null?RS.getString("direccion_instalacion"):""), Font.HELVETICA, 10, Font.NORMAL, Element.ALIGN_LEFT, 1));

                    tbl_det1.addCell(Addons.setCeldaPDF("COSTO DE INSTALACION", Font.HELVETICA, 10, Font.BOLD, Element.ALIGN_LEFT, 1));
                    tbl_det1.addCell(Addons.setCeldaPDF((RS.getString("costo_instalacion")!=null?RS.getString("costo_instalacion"):""), Font.HELVETICA, 10, Font.NORMAL, Element.ALIGN_LEFT, 1));

                    tbl_det1.addCell(Addons.setCeldaPDF("FECHA DE ORDEN DE INSTALACION", Font.HELVETICA, 10, Font.BOLD, Element.ALIGN_LEFT, 1));
                    tbl_det1.addCell(Addons.setCeldaPDF(Fecha.ISOaSQL(fecha_registro), Font.HELVETICA, 10, Font.NORMAL, Element.ALIGN_LEFT, 1));

                    tbl_det1.addCell(Addons.setCeldaPDF("FECHA DE INSTALACION REAL", Font.HELVETICA, 10, Font.BOLD, Element.ALIGN_LEFT, 1));
                    tbl_det1.addCell(Addons.setCeldaPDF(" ", Font.HELVETICA, 10, Font.NORMAL, Element.ALIGN_LEFT, 1));

                    tbl_det1.addCell(Addons.setCeldaPDF("SECTOR", Font.HELVETICA, 10, Font.BOLD, Element.ALIGN_LEFT, 1));
                    tbl_det1.addCell(Addons.setCeldaPDF(sector, Font.HELVETICA, 10, Font.NORMAL, Element.ALIGN_LEFT, 1));

                    tbl_det1.addCell(Addons.setCeldaPDF("DIRECCION IP", Font.HELVETICA, 10, Font.BOLD, Element.ALIGN_LEFT, 1));
                    tbl_det1.addCell(Addons.setCeldaPDF((RS.getString("ip")!=null?RS.getString("ip"):""), Font.HELVETICA, 10, Font.NORMAL, Element.ALIGN_LEFT, 1));

                    tbl_det1.addCell(Addons.setCeldaPDF("DIRECCION MAC", Font.HELVETICA, 10, Font.BOLD, Element.ALIGN_LEFT, 1));
                    tbl_det1.addCell(Addons.setCeldaPDF((RS.getString("mac")!=null?RS.getString("mac"):""), Font.HELVETICA, 10, Font.NORMAL, Element.ALIGN_LEFT, 1));


                    tbl_det1.addCell(Addons.setCeldaPDF("USUARIO PPOE", Font.HELVETICA, 10, Font.BOLD, Element.ALIGN_LEFT, 1));
                    tbl_det1.addCell(Addons.setCeldaPDF(radusername, Font.HELVETICA, 10, Font.NORMAL, Element.ALIGN_LEFT, 1));

                    tbl_det1.addCell(Addons.setCeldaPDF("CLAVE PPOE", Font.HELVETICA, 10, Font.BOLD, Element.ALIGN_LEFT, 1));
                    tbl_det1.addCell(Addons.setCeldaPDF(radclave, Font.HELVETICA, 10, Font.NORMAL, Element.ALIGN_LEFT, 1));

                    tbl_det1.addCell(Addons.setCeldaPDF("CLAVE DISPOSITIVO", Font.HELVETICA, 10, Font.BOLD, Element.ALIGN_LEFT, 1));
                    tbl_det1.addCell(Addons.setCeldaPDF(deviceclave, Font.HELVETICA, 10, Font.NORMAL, Element.ALIGN_LEFT, 1));


                    tbl_det1.addCell(Addons.setCeldaPDF("RECEPTOR DE SEÑAL", Font.HELVETICA, 10, Font.BOLD, Element.ALIGN_LEFT, 1));
                    tbl_det1.addCell(Addons.setCeldaPDF((RS.getString("receptor")!=null?RS.getString("receptor"):""), Font.HELVETICA, 10, Font.NORMAL, Element.ALIGN_LEFT, 1));

                    tbl_det.addCell(Addons.setCeldaPDF("PORCENTAJE DE SEÑAL", Font.HELVETICA, 10, Font.BOLD, Element.ALIGN_LEFT, 1));
                    tbl_det.addCell(Addons.setCeldaPDF(" ", Font.HELVETICA, 10, Font.NORMAL, Element.ALIGN_LEFT, 1));

                    tbl_det1.addCell(Addons.setCeldaPDF("ANTENA DE TRASMISION ACOPLADA", Font.HELVETICA, 10, Font.BOLD, Element.ALIGN_LEFT, 1));
                    tbl_det1.addCell(Addons.setCeldaPDF(" ", Font.HELVETICA, 10, Font.NORMAL, Element.ALIGN_LEFT, 1));

                    tbl_det1.addCell(Addons.setCeldaPDF("PLAN CONTRATADO", Font.HELVETICA, 10, Font.BOLD, Element.ALIGN_LEFT, 1));
                    tbl_det1.addCell(Addons.setCeldaPDF(plan, Font.HELVETICA, 10, Font.NORMAL, Element.ALIGN_LEFT, 1));

                    tbl_det1.addCell(Addons.setCeldaPDF("NIVEL DE COMPARTICION", Font.HELVETICA, 10, Font.BOLD, Element.ALIGN_LEFT, 1));
                    tbl_det1.addCell(Addons.setCeldaPDF((RS.getString("comparticion")!=null?RS.getString("comparticion"):"") + " - 1", Font.HELVETICA, 10, Font.NORMAL, Element.ALIGN_LEFT, 1));

                    tbl_det1.addCell(Addons.setCeldaPDF("PLAN ESTABLECIDO", Font.HELVETICA, 10, Font.BOLD, Element.ALIGN_LEFT, 1));
                    tbl_det1.addCell(Addons.setCeldaPDF(" ", Font.HELVETICA, 10, Font.NORMAL, Element.ALIGN_LEFT, 1));

                    tbl_det1.addCell(Addons.setCeldaPDF("CONFORMIDAD DE VELOCIDAD ESTABLECIDA", Font.HELVETICA, 10, Font.BOLD, Element.ALIGN_LEFT, 1));
                    tbl_det1.addCell(Addons.setCeldaPDF(" ___ SI \n ___ NO\n ", Font.HELVETICA, 10, Font.NORMAL, Element.ALIGN_LEFT, 1));

                    tbl_det1.addCell(Addons.setCeldaPDF("CONFORMIDAD DE LA ADECUADA INSTALACION", Font.HELVETICA, 10, Font.BOLD, Element.ALIGN_LEFT, 1));
                    tbl_det1.addCell(Addons.setCeldaPDF(" ___ Excelente \n ___ Buena \n ___ Mala\n ", Font.HELVETICA, 10, Font.NORMAL, Element.ALIGN_LEFT, 1));

                    tbl_det1.addCell(Addons.setCeldaPDF("CONFORMIDAD DE LA ATENCION RECIBIDA", Font.HELVETICA, 10, Font.BOLD, Element.ALIGN_LEFT, 1));
                    tbl_det1.addCell(Addons.setCeldaPDF(" ___ Excelente \n ___ Buena \n ___ Mala\n ", Font.HELVETICA, 10, Font.NORMAL, Element.ALIGN_LEFT, 1));

                    tbl_det1.addCell(Addons.setCeldaPDF("ESTADO DE LA INSTALACION", Font.HELVETICA, 10, Font.BOLD, Element.ALIGN_LEFT, 1));
                    tbl_det1.addCell(Addons.setCeldaPDF(" ___ Emitido \n ___ Instalado\n ", Font.HELVETICA, 10, Font.NORMAL, Element.ALIGN_LEFT, 1));

                    tbl_det1.addCell(Addons.setCeldaPDF("DESCRIPCION DE MATERIALES UTILIZADOS", Font.HELVETICA, 10, Font.BOLD, Element.ALIGN_LEFT, 1, 3, 2));
                    tbl_det1.addCell(Addons.setCeldaPDF("\n \n \n \n \n ", Font.HELVETICA, 10, Font.NORMAL, Element.ALIGN_LEFT, 1, 3, 2));

                    document.add(tbl_det1);

                    document.add(new Paragraph(" "));
                    document.add(new Paragraph(" "));
                    document.add(new Paragraph(" "));

                    PdfPTable tbl_firma1 = new PdfPTable(1);
                    tbl_firma1.addCell(Addons.setCeldaPDF("_______________________", Font.HELVETICA, 10, Font.NORMAL, Element.ALIGN_LEFT, 0));
                    tbl_firma1.addCell(Addons.setCeldaPDF("FIRMA", Font.HELVETICA, 10, Font.NORMAL, Element.ALIGN_LEFT, 0));
                    
                    document.add(tbl_firma1);



                    // croquis


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
                            (RS.getString("num_instalacion")!=null?RS.getString("num_instalacion"):"") +
                            "\nCEDULA: " + (RS.getString("ruc")!=null?RS.getString("ruc"):"") +
                            "         CLIENTE: " + (RS.getString("razon_social")!=null?RS.getString("razon_social"):"") +
                            "\nDIRECCION DE INSTALACION: " + (RS.getString("direccion_instalacion")!=null?RS.getString("direccion_instalacion"):"") +
                            "         TELEFONO: " + (RS.getString("telefono")!=null?RS.getString("telefono"):"")

                            , Font.HELVETICA, 8, Font.BOLD, Element.ALIGN_LEFT, 0));

                        document.add(tbl_croquis);
                    }catch(Exception e){
                        //tbl_croquis.addCell(" ");
                    }
*/
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
        ResultSet res = objInstalacion.getInstalacion(id);

        String path = String.valueOf(request.getRequestURL());
        path = path.substring(0, path.lastIndexOf("/"));
        Archivo archivo = new Archivo(this._ip, this._puerto, this._db, this._usuario, this._clave);
        String logo = path + "/img/" + archivo.getArchivo(this._dir, 1);

        Configuracion conf = new Configuracion(this._ip, this._puerto, this._db, this._usuario, this._clave);
        String cargo = conf.getValor("rep_cargo").toUpperCase();
        String representante = conf.getValor("rep_nombre");
        String ruc = conf.getValor("ruc");
        conf.cerrar();
        
        PdfInstalacion obj = new PdfInstalacion();
        obj.imprimir(response, res, id, path, logo, cargo, representante, ruc);

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
