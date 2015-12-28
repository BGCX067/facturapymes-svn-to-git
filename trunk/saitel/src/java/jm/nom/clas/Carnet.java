/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jm.nom.clas;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.Image;
import com.lowagie.text.PageSize;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfPageEventHelper;
import com.lowagie.text.pdf.PdfWriter;
import java.awt.Color;
import java.io.IOException;
import java.sql.ResultSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.http.HttpServletResponse;
import jm.nom.pdf.pdfEmpleadosCarnets;
import jm.web.Addons;

/**
 *
 * @author SoulGael
 */
  public  class Carnet  extends PdfPageEventHelper{
      String _dir="";
        public Carnet( String dir){
        this._dir=dir;
        }
        
        public void imprimir(HttpServletResponse response, ResultSet rsCarnets, String path){
            response.setContentType("application/pdf");
            response.setHeader("Pragma", "no-cache");
            response.setHeader("Expires", "Mon, 01 Jan 2001 00:00:01 GMT");
            response.setHeader("Cache-Control", "no-store");
            response.setHeader("Cache-Control", "must-revalidate");
            response.setHeader("Cache-Control", "no-cache");

            

            /*try{
                rsCarnets = objEmpleado.getCarnets(id);
                if(rsCarnets.next()){
                    id_sucursal = ((rsCarnets.getString("id_sucursal")!=null) ? rsCarnets.getString("id_sucursal") : "");
                    id_instalacion = ((rsCarnets.getString("id_instalacion")!=null) ? rsCarnets.getString("id_instalacion") : "");
                    serie_factura = ((rsCarnets.getString("serie_factura")!=null) ? rsCarnets.getString("serie_factura") : "");
                    num_factura = ((rsCarnets.getString("num_factura")!=null) ? rsCarnets.getString("num_factura") : "");
                    razon_social = ((rsCarnets.getString("razon_social")!=null) ? rsCarnets.getString("razon_social") : "CONSUMIDOR FINAL");
                    fecha_emision = ((rsCarnets.getString("fecha_emision")!=null) ? Fecha.ISOaSQL(rsCarnets.getString("fecha_emision")) : "");
                    direccion = ((rsCarnets.getString("direccion")!=null) ? rsCarnets.getString("direccion") : "");
                    vendedor = ((rsCarnets.getString("vendedor")!=null) ? rsCarnets.getString("vendedor") : "");
                    total = ((rsCarnets.getString("total")!=null) ? rsCarnets.getString("total") : "0.00");
                    de_activo = ((rsCarnets.getString("de_activo")!=null) ? rsCarnets.getBoolean("de_activo") : false);
                    rsCarnets.close();
                }
            }catch(Exception ie){
                ie.printStackTrace();
            }*/
            try {
                /* inicio PDF */
                Document document = new Document(PageSize.A4.rotate());// paso 1
                document.setMargins(-47,0,38,0); /*Izquierda, derecha, tope, pie */
                
                PdfWriter writer = PdfWriter.getInstance(document, response.getOutputStream()); // paso 2
                writer.setPageEvent(new Carnet(this._dir));
                document.open(); // paso 3

                //writer.addJavaScript("this.print(false);", false); // Para enviar a la impresora automáticamente.

                /* todo el cuerpo del doc es el paso 4 */

                //PdfPTable tbl = new PdfPTable(new float[]{40f,60f});
                PdfPTable tbl = new PdfPTable(4);
                int i=0;
                try{
                    //rsCarnets = objEmpleado.getCarnets(id);
                    while(rsCarnets.next()){
                        PdfPTable tbl1 = new PdfPTable(1);
                        PdfPTable tbl2 = new PdfPTable(new float[]{90f,5f,5f});
                        String foto=((rsCarnets.getString("padre_nombre")!=null) ? rsCarnets.getString("padre_nombre") : "");
                       //_dir="C:\\Users\\SoulGael\\Documents\\NetBeansProjects\\trunk\\saitel\\build\\web\\img\\luis.jpg";
                       
                       tbl1.addCell(Addons.setCeldaPDFBottom(" ", Font.HELVETICA, 10, Font.NORMAL,Element.ALIGN_LEFT,0,26,0));
                       tbl1.addCell(Addons.setLogoCarnet(_dir + foto,79,103));
                       //tbl1.addCell(Addons.setLogoCarnet(_dir,79,103));
                        String nombre=((rsCarnets.getString("nombre")!=null) ? rsCarnets.getString("nombre") : "");
                        String apellidos=((rsCarnets.getString("apellido")!=null) ? rsCarnets.getString("apellido") : "");
                        String cargo=((rsCarnets.getString("cargo")!=null) ? rsCarnets.getString("cargo") : "");;
                        String[] nombresArray = nombre.split(" ");
                        String[] apellidosArray = apellidos.split(" ");
                        String n_completos = nombresArray[0]+" "+apellidosArray[0];
                        String CI=((rsCarnets.getString("dni")!=null) ? rsCarnets.getString("dni") : "");;
                        String sangre=((rsCarnets.getString("tipo_sangre")!=null) ? rsCarnets.getString("tipo_sangre") : "");;
                        String carnet=((rsCarnets.getString("carnet")!=null) ? rsCarnets.getString("carnet") : "");
                        //String carnet="A,B,C,D,E,F,G";
                        Color A=Color.WHITE;Color B=Color.WHITE;Color C=Color.WHITE;Color D=Color.WHITE;Color E=Color.WHITE;Color F=Color.WHITE;Color G=Color.WHITE;
                        
                        String[] carnetArray = carnet.split(",");
                        for (int j = 0; j<carnetArray.length; j++) {
                            if(carnetArray[j].equals("A")){
                                A=Color.ORANGE;
                                j++;
                                if(j>carnetArray.length-1)
                                    j=carnetArray.length-1;
                            }
                            if(carnetArray[j].equals("B")){
                                B=Color.ORANGE;
                                j++;
                                if(j>carnetArray.length-1)
                                    j=carnetArray.length-1;
                            }
                            if(carnetArray[j].equals("C")){
                                C=Color.ORANGE;;
                                j++;
                                if(j>carnetArray.length-1)
                                    j=carnetArray.length-1;
                            }
                            if(carnetArray[j].equals("D")){
                                D=Color.ORANGE;;
                                j++;
                                if(j>carnetArray.length-1)
                                    j=carnetArray.length-1;
                            }
                            if(carnetArray[j].equals("E")){
                                E= Color.ORANGE;;
                                j++;
                                if(j>carnetArray.length-1)
                                    j=carnetArray.length-1;
                            }
                            if(carnetArray[j].equals("F")){
                                F= Color.ORANGE;;
                                j++;
                                if(j>carnetArray.length-1)
                                    j=carnetArray.length-1;
                            }
                            if(carnetArray[j].equals("G")){
                                G=Color.ORANGE;;
                                j++;
                                if(j>carnetArray.length-1)
                                    j=carnetArray.length-1;
                            }
                            break;
                        }
                        
                        tbl1.addCell(Addons.setCeldaPDFBottom(" ", Font.ITALIC, 7, Font.BOLD,Element.ALIGN_CENTER,0,0,0));
                        tbl1.addCell(Addons.setCeldaPDFBottom(n_completos, Font.ITALIC, 13, Font.BOLD,Element.ALIGN_CENTER,0,0,0));
                        tbl1.addCell(Addons.setCeldaPDFBottom(cargo, Font.ITALIC, 12, Font.BOLD,Element.ALIGN_CENTER,0,0,0));
                        tbl1.addCell(Addons.setCeldaPDFBottom(CI, Font.ITALIC, 12, Font.BOLD,Element.ALIGN_CENTER,0,0,0));
                        tbl1.addCell(Addons.setCeldaPDFBottom(sangre, Font.ITALIC, 12, Font.BOLD,Element.ALIGN_CENTER,0,0,0));
                        
                       tbl2.addCell(Addons.setCeldaPDFCarnet(" ", Font.ITALIC, 7, Font.BOLD,Element.ALIGN_LEFT,0,0,3));tbl2.addCell(Addons.setCeldaPDFCarnet(" ", Font.ITALIC, 7, Font.BOLD,Element.ALIGN_LEFT,0,0,3));
                       tbl2.addCell(Addons.setCeldaPDFCarnet(" ", Font.ITALIC, 7, Font.BOLD,Element.ALIGN_LEFT,0,0,3));
                       tbl2.addCell(Addons.setCeldaPDFCarnet("  LICENCIA INTERNA DE ", Font.ITALIC, 9, Font.BOLD,Element.ALIGN_CENTER,0,0,3));
                       tbl2.addCell(Addons.setCeldaPDFCarnet("  PREVENCION DE RIESGOS", Font.ITALIC, 9, Font.BOLD,Element.ALIGN_CENTER,0,0,3));
                       tbl2.addCell(Addons.setCeldaPDFCarnet("   Con la inducción recibida y aprobada usted", Font.ITALIC, 8, Font.BOLD,Element.ALIGN_LEFT,0,0,3));
                       tbl2.addCell(Addons.setCeldaPDFCarnet("   está autorizado para:", Font.ITALIC, 8, Font.BOLD,Element.ALIGN_LEFT,0,0,3));
                       tbl2.addCell(Addons.setCeldaPDFCarnet(" ", Font.ITALIC, 8, Font.BOLD,Element.ALIGN_LEFT,0,0,3));
                       tbl2.addCell(Addons.setCeldaPDFCarnet("      a. Trabajo de Oficina.", Font.ITALIC, 8, Font.BOLD,Element.ALIGN_LEFT,0,2,0));
                       tbl2.addCell(Addons.setCeldaPDFCarnetOpciones(" ", Font.ITALIC, 8, Font.BOLD,Element.ALIGN_CENTER,1,2,0,A));
                       tbl2.addCell(Addons.setCeldaPDFCarnet(" ", Font.ITALIC, 8, Font.BOLD,Element.ALIGN_LEFT,0,2,0));
                       
                       tbl2.addCell(Addons.setCeldaPDFCarnet("      b. Trabajo de Marketing y propaganda", Font.ITALIC, 8, Font.BOLD,Element.ALIGN_LEFT,0,2,0));
                       tbl2.addCell(Addons.setCeldaPDFCarnetOpciones(" ", Font.ITALIC, 8, Font.BOLD,Element.ALIGN_CENTER,1,2,0,B));
                       tbl2.addCell(Addons.setCeldaPDFCarnet(" ", Font.ITALIC, 8, Font.BOLD,Element.ALIGN_LEFT,0,2,0));
                       
                       tbl2.addCell(Addons.setCeldaPDFCarnet("      c. Trabajo de mantenimiento y limpieza", Font.ITALIC, 8, Font.BOLD,Element.ALIGN_LEFT,0,2,0));
                       tbl2.addCell(Addons.setCeldaPDFCarnetOpciones(" ", Font.ITALIC, 8, Font.BOLD,Element.ALIGN_CENTER,1,2,0,C));
                       tbl2.addCell(Addons.setCeldaPDFCarnet(" ", Font.ITALIC, 8, Font.BOLD,Element.ALIGN_LEFT,0,2,0));
                       
                       tbl2.addCell(Addons.setCeldaPDFCarnet("      d. Trabajo de instalaciones menores a los ", Font.ITALIC, 8, Font.BOLD,Element.ALIGN_LEFT,0,0,0));
                       tbl2.addCell(Addons.setCeldaPDFCarnet(" ", Font.ITALIC, 8, Font.BOLD,Element.ALIGN_CENTER,0,0,2));
                       tbl2.addCell(Addons.setCeldaPDFCarnet("      ocho metros sobre el nivel.", Font.ITALIC, 8, Font.BOLD,Element.ALIGN_LEFT,0,0,0));
                       tbl2.addCell(Addons.setCeldaPDFCarnetOpciones(" ", Font.ITALIC, 8, Font.BOLD,Element.ALIGN_CENTER,1,0,0,D));
                       tbl2.addCell(Addons.setCeldaPDFCarnet(" ", Font.ITALIC, 8, Font.BOLD,Element.ALIGN_LEFT,0,2,0));
                       tbl2.addCell(Addons.setCeldaPDFCarnet("      (Domicilios, postes, cableado)", Font.ITALIC, 8, Font.BOLD,Element.ALIGN_LEFT,0,2,0));
                       tbl2.addCell(Addons.setCeldaPDFCarnet(" ", Font.ITALIC, 8, Font.BOLD,Element.ALIGN_CENTER,0,2,2));
                       
                       tbl2.addCell(Addons.setCeldaPDFCarnet("      e. Trabajo de instalaciones, antenas,", Font.ITALIC, 8, Font.BOLD,Element.ALIGN_LEFT,0,0,0));
                       tbl2.addCell(Addons.setCeldaPDFCarnet(" ", Font.ITALIC, 8, Font.BOLD,Element.ALIGN_CENTER,0,0,2));
                       tbl2.addCell(Addons.setCeldaPDFCarnet("      infraestructura mayores a los ocho", Font.ITALIC, 8, Font.BOLD,Element.ALIGN_LEFT,0,0,0));
                       tbl2.addCell(Addons.setCeldaPDFCarnetOpciones(" ", Font.ITALIC, 8, Font.BOLD,Element.ALIGN_CENTER,1,0,0,E));
                       tbl2.addCell(Addons.setCeldaPDFCarnet(" ", Font.ITALIC, 8, Font.BOLD,Element.ALIGN_LEFT,0,2,0));
                       tbl2.addCell(Addons.setCeldaPDFCarnet("      metros sobre el nivel", Font.ITALIC, 8, Font.BOLD,Element.ALIGN_LEFT,0,2,0));
                       tbl2.addCell(Addons.setCeldaPDFCarnet(" ", Font.ITALIC, 8, Font.BOLD,Element.ALIGN_CENTER,0,2,2));
                       
                       tbl2.addCell(Addons.setCeldaPDFCarnet("      f. Planes de emergencia y contingencia", Font.ITALIC, 8, Font.BOLD,Element.ALIGN_LEFT,0,2,0));
                       tbl2.addCell(Addons.setCeldaPDFCarnetOpciones(" ", Font.ITALIC, 8, Font.BOLD,Element.ALIGN_CENTER,1,2,0,F));
                       tbl2.addCell(Addons.setCeldaPDFCarnet(" ", Font.ITALIC, 8, Font.BOLD,Element.ALIGN_LEFT,0,2,0));
                       
                       tbl2.addCell(Addons.setCeldaPDFCarnet("      g. Planes de rescate", Font.ITALIC, 8, Font.BOLD,Element.ALIGN_LEFT,0,2,0));
                       tbl2.addCell(Addons.setCeldaPDFCarnetOpciones(" ", Font.ITALIC, 8, Font.BOLD,Element.ALIGN_CENTER,1,2,0,G));
                       tbl2.addCell(Addons.setCeldaPDFCarnet(" ", Font.ITALIC, 8, Font.BOLD,Element.ALIGN_LEFT,0,2,0));
                       
                       tbl2.addCell(Addons.setCeldaPDFCarnet(" ", Font.ITALIC, 8, Font.BOLD,Element.ALIGN_LEFT,0,0,3));tbl2.addCell(Addons.setCeldaPDFCarnet(" ", Font.ITALIC, 8, Font.BOLD,Element.ALIGN_LEFT,0,0,3));
                       tbl2.addCell(Addons.setCeldaPDFCarnet(" ", Font.ITALIC, 10, Font.BOLD,Element.ALIGN_LEFT,0,0,3));
                       tbl2.addCell(Addons.setCeldaPDFCarnet("Ing. FREDDY ROSERO", Font.ITALIC, 8, Font.BOLD,Element.ALIGN_CENTER,0,0,3,Color.BLUE));
                       tbl2.addCell(Addons.setCeldaPDFCarnet("GERENTE", Font.ITALIC, 8, Font.BOLD,Element.ALIGN_CENTER,0,0,3,Color.BLUE));
                       tbl2.addCell(Addons.setCeldaPDFCarnet("Fecha de Expedición: Mayo 2015", Font.ITALIC, 5, Font.BOLD,Element.ALIGN_CENTER,0,0,3,Color.BLUE));
                       tbl2.addCell(Addons.setCeldaPDFCarnet("Fecha de Caducidad: Mayo 2016", Font.ITALIC, 5, Font.BOLD,Element.ALIGN_CENTER,0,0,3,Color.BLUE));
                       /* tbl1.addCell(Addons.setCeldaPDF(((rsCarnets.getString("nombres")!=null) ? rsCarnets.getString("nombres") : ""), Font.HELVETICA, 10, Font.NORMAL, Element.ALIGN_LEFT, 1));
                        tbl1.addCell(Addons.setCeldaPDF(((rsCarnets.getString("rol")!=null) ? rsCarnets.getString("rol") : ""), Font.HELVETICA, 10, Font.NORMAL, Element.ALIGN_LEFT, 1));
                        
                        tbl1.addCell(Addons.setCeldaPDF(((rsCarnets.getString("dni")!=null) ? rsCarnets.getString("dni") : ""), Font.HELVETICA, 10, Font.NORMAL, Element.ALIGN_LEFT, 1));
                        tbl1.addCell(Addons.setCeldaPDF(((rsCarnets.getString("tipo_sangre")!=null) ? rsCarnets.getString("tipo_sangre") : ""), Font.HELVETICA, 10, Font.NORMAL, Element.ALIGN_LEFT, 1));
                        
                        String idcarnet=(rsCarnets.getString("id_empleado")!=null) ? rsCarnets.getString("id_empleado") : "";*/
                        
                        tbl.addCell(Addons.setCeldaPDF(tbl1, 0, 0));
                        tbl.addCell(Addons.setCeldaPDF(tbl2, 0, 0));
                        if(i%2!=0)
                        {
                            tbl1.addCell(Addons.setCeldaPDF(" ", Font.ITALIC, 10, Font.BOLD,Element.ALIGN_LEFT,0,16,3));
                        }
                        i++;
                    }
                    if(i%2!=0)
                    {
                        tbl.addCell(" ");
                        tbl.addCell(" ");
                    }
                    
                    /*Archivo archivo = new Archivo(_ip, _puerto, _db, _usuario, _clave);
                        //path = path.substring(0, path.lastIndexOf("/"));
                        String carnet = path + "/img/" + archivo.getArchivo(_dir, "vta_empleado", idcarnet, "padre_nombre", "foto");
                        archivo.cerrar();
                        Image imagen = null;
                        try{
                            imagen = Image.getInstance(carnet);
                            imagen.scaleAbsolute(50, 70);
                            PdfPCell celdaImg = new PdfPCell(imagen);
                            celdaImg.setBorderWidth(0);
                            celdaImg.setPadding(0);
                            tbl.addCell(celdaImg);
                            document.add(tbl1);
                        }catch(Exception e){
                            tbl.addCell(" ");
                        }*/
                    
                }catch(Exception e){
                    e.printStackTrace();
                }
                document.add(tbl);

                
                
                document.close(); // paso 5
                /* fin PDF */

            }catch(IllegalStateException ie){
                ie.printStackTrace();
            }catch(DocumentException e){
                e.printStackTrace();
            }catch(Exception e){
                e.printStackTrace();
            }
        }
        
        public void onEndPage(PdfWriter writer, Document document)
    {
            try {
                //_dir="C:\\Users\\SoulGael\\Documents\\NetBeansProjects\\trunk\\saitel\\build\\web\\img\\";
                Image imagelogo = Image.getInstance(_dir+"fondocarnet.jpg");
                imagelogo.scaleAbsolute(860, 625);//ancho, alto
                imagelogo.setAbsolutePosition(0, 10);
                document.add(imagelogo);
            } catch (DocumentException ex) {
                Logger.getLogger(pdfEmpleadosCarnets.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(pdfEmpleadosCarnets.class.getName()).log(Level.SEVERE, null, ex);
            }
    }
    }
    
