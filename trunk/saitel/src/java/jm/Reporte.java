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

package jm;


import jm.web.*;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
        
import javax.servlet.http.*;

import com.lowagie.text.pdf.*;
import com.lowagie.text.*;
import java.awt.Color;

import jm.web.DataBase;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import jm.adm.clas.Sucursal;

/**
 *
 * @author Jorge
 */
public class Reporte extends PdfPageEventHelper{
    private DecimalFormat formatoNumero;
    private String logo = "";
    private String ruc = "";
    private String titulo = "";
    private String subtitulo = "";
    private String direccion = "";
    private String sucursal = "";
    private String rep_pie = "";
    private boolean vertical =true;
    
    public Reporte(boolean vertical, String logo, String titulo, String ruc, String subtitulo, String direccion, String sucursal, String rep_pie)
    {
        this.logo = logo;
        this.titulo = titulo;
        this.ruc = ruc;
        this.subtitulo = subtitulo;
        this.direccion = direccion;
        this.sucursal = sucursal;
        this.vertical = vertical;
        this.rep_pie = rep_pie;
        
        this.formatoNumero = new DecimalFormat("#############.##");
        DecimalFormatSymbols dfs = this.formatoNumero.getDecimalFormatSymbols();
        dfs.setDecimalSeparator('.');
        this.formatoNumero.setDecimalFormatSymbols(dfs);
    }
    
    public void lista(HttpServletResponse response, String [] cabTabla, float [] anchoTabla, ResultSet registros)
    {
        /* inicio PDF */     
        Rectangle orientacion = (this.vertical) ? PageSize.A4 : PageSize.A4.rotate();
        Document document = new Document(orientacion);// paso 1
        document.setMargins(0,0,50,80); /*Izquierda, derecha, tope, pie */
        try{
            PdfWriter writer = PdfWriter.getInstance(document, response.getOutputStream()); // paso 2
            writer.setPageEvent(new Reporte(this.vertical, this.logo, this.titulo, this.ruc, this.subtitulo, this.direccion, this.sucursal, this.rep_pie));
            
            
            document.open(); // paso 3
            
            /* todo el cuerpo del doc es el paso 4 */
            
            document.add(Addons.setCabecera(this.logo, this.titulo, this.ruc, this.subtitulo, this.direccion, this.sucursal));
            
            document.add(Addons.setCabeceraTabla(cabTabla, anchoTabla));
            
            ResultSetMetaData mdata = registros.getMetaData();      
            int numCols = mdata.getColumnCount();
            PdfPTable tbl_det = new PdfPTable(anchoTabla);
            int num = 1;
            try{
                while(registros.next()){
                    tbl_det.addCell(Addons.setCeldaPDF(String.valueOf(num), Font.TIMES_ROMAN, 8, Font.NORMAL, Element.ALIGN_CENTER, 1));
                    for(int i=1; i<=numCols; i++){
                        tbl_det.addCell(Addons.setCeldaPDF(((registros.getString(i)!=null) ? registros.getString(i) : ""), Font.TIMES_ROMAN, 8, Font.NORMAL, Element.ALIGN_LEFT, 1));
                    }
                    num++;
                }
                registros.close();
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
    }
       
    public void lista(HttpServletResponse response, String [] cabTabla, float [] anchoTabla, ResultSet registros, int lim)
    {
        /* inicio PDF */     
        Rectangle orientacion = (this.vertical) ? PageSize.A4 : PageSize.A4.rotate();
        Document document = new Document(orientacion);// paso 1
        document.setMargins(0,0,50,80); /*Izquierda, derecha, tope, pie */
        try{
            PdfWriter writer = PdfWriter.getInstance(document, response.getOutputStream()); // paso 2
            writer.setPageEvent(new Reporte(this.vertical, this.logo, this.titulo, this.ruc, this.subtitulo, this.direccion, this.sucursal, this.rep_pie));
            document.open(); // paso 3
            
            /* todo el cuerpo del doc es el paso 4 */
            
            document.add(Addons.setCabecera(this.logo, this.titulo, this.ruc, this.subtitulo, this.direccion, this.sucursal));
            
            document.add(Addons.setCabeceraTabla(cabTabla, anchoTabla));
            
            PdfPTable tbl_det = new PdfPTable(anchoTabla);
            int num = 1;
            int alineacion = Element.ALIGN_LEFT;
            try{
                while(registros.next()){
                    tbl_det.addCell(Addons.setCeldaPDF(String.valueOf(num), Font.TIMES_ROMAN, 8, Font.NORMAL, Element.ALIGN_CENTER, 1));
                    for(int i=1; i<=lim; i++){
                        //alineacion = (i==lim) ? Element.ALIGN_RIGHT : Element.ALIGN_LEFT;
                        tbl_det.addCell(Addons.setCeldaPDF(((registros.getString(i)!=null) ? registros.getString(i) : ""), Font.TIMES_ROMAN, 8, Font.NORMAL, alineacion, 1));
                    }
                    num++;
                }
                registros.close();
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
    }

    public void lista(HttpServletResponse response, String [] cabTabla, float [] anchoTabla, ResultSet registros, char sumDesde)
    {
        int sumarDesde = Integer.parseInt(""+sumDesde);
        int num_sumas = cabTabla.length - sumarDesde;
        float sumas[] = new float[num_sumas];
        float valor = 0;
        int pos=0;
        /* inicio PDF */
        Rectangle orientacion = (this.vertical) ? PageSize.A4 : PageSize.A4.rotate();
        Document document = new Document(orientacion);// paso 1
        document.setMargins(0,0,50,80); /*Izquierda, derecha, tope, pie */
        try{
            PdfWriter writer = PdfWriter.getInstance(document, response.getOutputStream()); // paso 2
            writer.setPageEvent(new Reporte(this.vertical, this.logo, this.titulo, this.ruc, this.subtitulo, this.direccion, this.sucursal, this.rep_pie));


            document.open(); // paso 3

            /* todo el cuerpo del doc es el paso 4 */

            document.add(Addons.setCabecera(this.logo, this.titulo, this.ruc, this.subtitulo, this.direccion, this.sucursal));

            document.add(Addons.setCabeceraTabla(cabTabla, anchoTabla));

            ResultSetMetaData mdata = registros.getMetaData();
            int numCols = mdata.getColumnCount();
            PdfPTable tbl_det = new PdfPTable(anchoTabla);
            int num = 1;
            try{
                while(registros.next()){
                    tbl_det.addCell(Addons.setCeldaPDF(String.valueOf(num), Font.TIMES_ROMAN, 8, Font.NORMAL, Element.ALIGN_CENTER, 1));
                    pos=0;
                    for(int i=1; i<=numCols; i++){
                        if(i<sumarDesde){
                            tbl_det.addCell(Addons.setCeldaPDF(((registros.getString(i)!=null) ? registros.getString(i) : ""), Font.TIMES_ROMAN, 8, Font.NORMAL, Element.ALIGN_LEFT, 1));
                        }else{
                            valor = (registros.getString(i)!=null) ? registros.getFloat(i) : 0;
                            tbl_det.addCell(Addons.setCeldaPDF(String.valueOf(valor), Font.TIMES_ROMAN, 8, Font.NORMAL, Element.ALIGN_RIGHT, 1));
                            sumas[pos] = Float.parseFloat(this.formatoNumero.format(sumas[pos])) + valor;
                            pos++;
                        }
                    }
                    num++;
                }

                tbl_det.addCell(Addons.setCeldaPDF("TOTALES:  ", Font.TIMES_ROMAN, 8, Font.NORMAL, Element.ALIGN_RIGHT, 0, 4, sumarDesde));
                for(int j=0; j<sumas.length; j++){
                    tbl_det.addCell(Addons.setCeldaPDF(Addons.truncar(sumas[j]), Font.TIMES_ROMAN, 8, Font.NORMAL, Element.ALIGN_RIGHT, 0));
                }

                registros.close();
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
    }

    public void sumLista(HttpServletResponse response, String [] cabTabla, float [] anchoTabla, ResultSet registros)
    {
        /* inicio PDF */
        Rectangle orientacion = (this.vertical) ? PageSize.A4 : PageSize.A4.rotate();
        Document document = new Document(orientacion);// paso 1
        document.setMargins(0,0,50,80); /*Izquierda, derecha, tope, pie */
        try{
            PdfWriter writer = PdfWriter.getInstance(document, response.getOutputStream()); // paso 2
            writer.setPageEvent(new Reporte(this.vertical, this.logo, this.titulo, this.ruc, this.subtitulo, this.direccion, this.sucursal, this.rep_pie));
            document.open(); // paso 3

            /* todo el cuerpo del doc es el paso 4 */

            document.add(Addons.setCabecera(this.logo, this.titulo, this.ruc, this.subtitulo, this.direccion, this.sucursal));

            document.add(Addons.setCabeceraTabla(cabTabla, anchoTabla));

            ResultSetMetaData mdata = registros.getMetaData();
            int numCols = mdata.getColumnCount();
            PdfPTable tbl_det = new PdfPTable(anchoTabla);
            int num = 1;
            //int num2 = 1;
            float suma_total = 0;
            float consumo = 0;
            int alineacion = Element.ALIGN_LEFT;
            try{
                while(registros.next()){
                    tbl_det.addCell(Addons.setCeldaPDF(String.valueOf(num), Font.TIMES_ROMAN, 8, Font.NORMAL, Element.ALIGN_CENTER, 1));
                    //tbl_det.addCell(Addons.setCeldaPDF(String.valueOf(num2), Font.TIMES_ROMAN, 8, Font.NORMAL, Element.ALIGN_CENTER, 1));
                    for(int i=1; i<=numCols; i++){
                        alineacion = (i==numCols) ? Element.ALIGN_RIGHT : Element.ALIGN_LEFT;
                        tbl_det.addCell(Addons.setCeldaPDF(((registros.getString(i)!=null) ? registros.getString(i) : ""), Font.TIMES_ROMAN, 8, Font.NORMAL, alineacion, 1));
                    }
                    consumo = (registros.getString(numCols)!=null) ? registros.getFloat(numCols) : 0;
                    suma_total = Float.parseFloat(this.formatoNumero.format(suma_total)) + consumo;
                    num++;
                    //num2++;
                }
                tbl_det.addCell(Addons.setCeldaPDF("TOTAL:  "+ Addons.truncar(suma_total), Font.TIMES_ROMAN, 10, Font.NORMAL, Element.ALIGN_RIGHT, 0, 5, numCols+2));

                registros.close();
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
    }

    public void listaAgrupada(HttpServletResponse response, String [] cabTabla, float [] anchoTabla, ResultSet registros)
    {
        /* inicio PDF */     
        Rectangle orientacion = (this.vertical) ? PageSize.A4 : PageSize.A4.rotate();
        Document document = new Document(orientacion);// paso 1
        document.setMargins(0,0,50,80); /*Izquierda, derecha, tope, pie */
        try{
            PdfWriter writer = PdfWriter.getInstance(document, response.getOutputStream()); // paso 2
            writer.setPageEvent(new Reporte(this.vertical, this.logo, this.titulo, this.ruc, this.subtitulo, this.direccion, this.sucursal, this.rep_pie));
            document.open(); // paso 3
            
            /* todo el cuerpo del doc es el paso 4 */
            
            document.add(Addons.setCabecera(this.logo, this.titulo, this.ruc, this.subtitulo, this.direccion, this.sucursal));
            
            document.add(Addons.setCabeceraTabla(cabTabla, anchoTabla));
            
            ResultSetMetaData mdata = registros.getMetaData();      
            int numCols = mdata.getColumnCount();
            PdfPTable tbl_det = new PdfPTable(anchoTabla);
            int num = 1;
            int num2 = 1;
            String aux = "";
            String columna1 = "";
            try{
                while(registros.next()){
                    columna1 = (registros.getString(1)!=null) ? registros.getString(1) : "";
                    if(aux.compareTo(columna1)!=0){
                       tbl_det.addCell(Addons.setCeldaPDF(columna1, Font.TIMES_ROMAN, 8, Font.NORMAL, Element.ALIGN_LEFT, 1, 3, numCols+1)); 
                       aux = columna1;
                       num2 = 1;
                    }
                    tbl_det.addCell(Addons.setCeldaPDF(String.valueOf(num), Font.TIMES_ROMAN, 8, Font.NORMAL, Element.ALIGN_CENTER, 1));
                    tbl_det.addCell(Addons.setCeldaPDF(String.valueOf(num2), Font.TIMES_ROMAN, 8, Font.NORMAL, Element.ALIGN_CENTER, 1));
                    for(int i=2; i<=numCols; i++){
                        tbl_det.addCell(Addons.setCeldaPDF(((registros.getString(i)!=null) ? registros.getString(i) : ""), Font.TIMES_ROMAN, 8, Font.NORMAL, Element.ALIGN_LEFT, 1));
                    }
                    num++;
                    num2++;
                }
                registros.close();
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
    }

    public void activosCustorios(HttpServletResponse response, String [] cabTabla, float [] anchoTabla, ResultSet registros, DataBase objDB)
    {
        /* inicio PDF */
        Rectangle orientacion = (this.vertical) ? PageSize.A4 : PageSize.A4.rotate();
        Document document = new Document(orientacion);// paso 1
        document.setMargins(0,0,50,80); /*Izquierda, derecha, tope, pie */
        try{
            PdfWriter writer = PdfWriter.getInstance(document, response.getOutputStream()); // paso 2
            writer.setPageEvent(new Reporte(this.vertical, this.logo, this.titulo, this.ruc, this.subtitulo, this.direccion, this.sucursal, this.rep_pie));
            document.open(); // paso 3

            /* todo el cuerpo del doc es el paso 4 */

            document.add(Addons.setCabecera(this.logo, this.titulo, this.ruc, this.subtitulo, this.direccion, this.sucursal));

            document.add(Addons.setCabeceraTabla(cabTabla, anchoTabla));

            ResultSetMetaData mdata = registros.getMetaData();
            int numCols = mdata.getColumnCount();
            PdfPTable tbl_det = new PdfPTable(anchoTabla);
            int num = 1;
            int num2 = 1;
            String aux = "";
            String columna1 = "";
            String persona_recibe = "";
            String fecha = "";
            String id_activo = "";
            try{
                while(registros.next()){
                    columna1 = (registros.getString(1)!=null) ? registros.getString(1) : "";
                    if(aux.compareTo(columna1)!=0){
                       tbl_det.addCell(Addons.setCeldaPDF(columna1, Font.TIMES_ROMAN, 8, Font.NORMAL, Element.ALIGN_LEFT, 1, 3, numCols+2));
                       aux = columna1;
                       num2 = 1;
                    }
                    tbl_det.addCell(Addons.setCeldaPDF(String.valueOf(num), Font.TIMES_ROMAN, 8, Font.NORMAL, Element.ALIGN_CENTER, 1));
                    tbl_det.addCell(Addons.setCeldaPDF(String.valueOf(num2), Font.TIMES_ROMAN, 8, Font.NORMAL, Element.ALIGN_CENTER, 1));
                    for(int i=2; i<numCols; i++){
                        tbl_det.addCell(Addons.setCeldaPDF(((registros.getString(i)!=null) ? registros.getString(i) : ""), Font.TIMES_ROMAN, 8, Font.NORMAL, Element.ALIGN_LEFT, 1));
                    }
                    id_activo = (registros.getString("id_activo")!=null) ? registros.getString("id_activo") : "";
                    try{
                        ResultSet rsCustodio = objDB.consulta("select persona_recibe, toDateSQL(max(fecha)) as fecha from tbl_activo_custodio as AC inner join tbl_activo_personalizacion as AP "
                            + "on AC.id_activo_personalizacion=AP.id_activo_personalizacion where AC.eliminado=false and AC.id_activo="+id_activo +
                            " group by persona_recibe,AP.id_activo_personalizacion order by AP.id_activo_personalizacion desc");
                        if(rsCustodio.next()){
                            persona_recibe = (rsCustodio.getString("persona_recibe")!=null) ? rsCustodio.getString("persona_recibe") : "";
                            fecha = (rsCustodio.getString("fecha")!=null) ? rsCustodio.getString("fecha") : "";
                            rsCustodio.close();
                        }
                    }catch(Exception ex){
                        ex.printStackTrace();
                    }
                    tbl_det.addCell(Addons.setCeldaPDF(persona_recibe, Font.TIMES_ROMAN, 8, Font.NORMAL, Element.ALIGN_LEFT, 1));
                    tbl_det.addCell(Addons.setCeldaPDF(fecha, Font.TIMES_ROMAN, 8, Font.NORMAL, Element.ALIGN_LEFT, 1));
                    num++;
                    num2++;
                }
                registros.close();
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
    }
    
    public void ventas(HttpServletResponse response, ResultSet registros, DataBase objDB, String iva_vigente)
    {
        String []cabTabla = new String [] {"Nro.", "RUC", "RAZON SOCIAL", "Nro. FAC.", "F. EMI.", "F. PAGO", "IP", "SUBTOTAL", "DESC.", "SUB. 0", "IVA "+iva_vigente, "TOTAL", "DEUDA", "RETENCION"};
        float []anchoTabla = new float[]{20f,55f,90f,60f,45f,40f,60f,35f,30f,35f,35f,35f,35f,35f};
                    
        /* inicio PDF */
        Rectangle orientacion = (this.vertical) ? PageSize.A4 : PageSize.A4.rotate();
        Document document = new Document(orientacion);// paso 1
        document.setMargins(0,0,50,80); /*Izquierda, derecha, tope, pie */
        try{
            PdfWriter writer = PdfWriter.getInstance(document, response.getOutputStream()); // paso 2
            writer.setPageEvent(new Reporte(this.vertical, this.logo, this.titulo, this.ruc, this.subtitulo, this.direccion, this.sucursal, this.rep_pie));
            document.open(); // paso 3

            /* todo el cuerpo del doc es el paso 4 */

            document.add(Addons.setCabecera(this.logo, this.titulo, this.ruc, this.subtitulo, this.direccion, this.sucursal));

            document.add(Addons.setCabeceraTabla(cabTabla, anchoTabla));

            ResultSetMetaData mdata = registros.getMetaData();
            int numCols = mdata.getColumnCount();
            PdfPTable tbl_det = new PdfPTable(anchoTabla);
            int num = 1;
            int num2 = 1;
            String aux = "";
            String id_factura_venta = "-1";
            String columna2 = "";
            float suma_parcial[] = {0,0,0,0,0,0,0};
            float suma_total[] = {0,0,0,0,0,0,0};
            float valor [] = {0,0,0,0,0,0,0};
            int pos=0;
            ResultSet retencion = null;
            
            try{
                while(registros.next()){
                    id_factura_venta = (registros.getString("id_factura_venta")!=null) ? registros.getString("id_factura_venta") : "-1";
                    columna2 = (registros.getString(2)!=null) ? registros.getString(2) : "";
                    pos=0;
                    if(aux.compareTo(columna2)!=0){
                        if(num>1){
                            tbl_det.addCell(Addons.setCeldaPDF("SUBTOTALES:  ", Font.TIMES_ROMAN, 8, Font.NORMAL, Element.ALIGN_RIGHT, 0, 4, 7));
                            for(int j=0; j<suma_parcial.length; j++){
                                tbl_det.addCell(Addons.setCeldaPDF(Addons.truncar(suma_parcial[j]), Font.TIMES_ROMAN, 8, Font.NORMAL, Element.ALIGN_RIGHT, 0));
                                suma_parcial[j] = 0;
                            }
                        }
                        tbl_det.addCell(Addons.setCeldaPDF(columna2, Font.TIMES_ROMAN, 8, Font.NORMAL, Element.ALIGN_LEFT, 1, 3, numCols+1));
                        aux = columna2;
                        num2 = 1;
                    }
                    //tbl_det.addCell(Addons.setCeldaPDF(String.valueOf(num), Font.TIMES_ROMAN, 8, Font.NORMAL, Element.ALIGN_CENTER, 1));
                    tbl_det.addCell(Addons.setCeldaPDF(String.valueOf(num2), Font.TIMES_ROMAN, 8, Font.NORMAL, Element.ALIGN_CENTER, 1));
                    String ax_anulada = "";
                    for(int i=3; i<=numCols; i++){
                        if(i<=8){
                            tbl_det.addCell(Addons.setCeldaPDF(((registros.getString(i)!=null) ? registros.getString(i) : ""), Font.TIMES_ROMAN, 8, Font.NORMAL, Element.ALIGN_LEFT, 1));
                        }else{
                            ax_anulada = (registros.getString("anulada")!=null) ? registros.getString("anulada") : "";
                            valor[pos] = (registros.getString(i)!=null) ? registros.getFloat(i) : 0;
                            tbl_det.addCell(Addons.setCeldaPDF(String.valueOf(valor[pos]), Font.TIMES_ROMAN, 8, Font.NORMAL, Element.ALIGN_RIGHT, 1));
                            if(ax_anulada.compareTo("ANULADA")!=0){
                                suma_parcial[pos] = Float.parseFloat(this.formatoNumero.format(suma_parcial[pos])) + valor[pos];
                                suma_total[pos] = Float.parseFloat(this.formatoNumero.format(suma_total[pos])) + valor[pos];
                            }
                            pos++;
                        }
                    }

                    valor[6]=0;
                    try{
                        retencion = objDB.consulta("select ret_impuesto_retenido from tbl_retencion_venta where anulado=false and id_factura_venta="+id_factura_venta+";");
                        if(retencion.next()){
                            valor[6]=(retencion.getString("ret_impuesto_retenido")!=null) ? retencion.getFloat("ret_impuesto_retenido") : 0;
                            retencion.close();
                        }
                    }catch(Exception e){
                        e.printStackTrace();
                    }
                    tbl_det.addCell(Addons.setCeldaPDF(String.valueOf(valor[6]), Font.TIMES_ROMAN, 8, Font.NORMAL, Element.ALIGN_RIGHT, 1));
                    suma_parcial[6] = Float.parseFloat(this.formatoNumero.format(suma_parcial[6])) + valor[6];
                    suma_total[6] = Float.parseFloat(this.formatoNumero.format(suma_total[6])) + valor[6];

                    num++;
                    num2++;
                }
                tbl_det.addCell(Addons.setCeldaPDF("SUBTOTALES:  ", Font.TIMES_ROMAN, 8, Font.NORMAL, Element.ALIGN_RIGHT, 0, 4, 7));
                for(int j=0; j<suma_parcial.length; j++){
                    tbl_det.addCell(Addons.setCeldaPDF(Addons.truncar(suma_parcial[j]), Font.TIMES_ROMAN, 8, Font.NORMAL, Element.ALIGN_RIGHT, 0));
                }
                tbl_det.addCell(Addons.setCeldaPDF("TOTALES:  ", Font.TIMES_ROMAN, 9, Font.NORMAL, Element.ALIGN_RIGHT, 0, 4, 7));
                for(int j=0; j<suma_total.length; j++){
                    tbl_det.addCell(Addons.setCeldaPDF(Addons.truncar(suma_total[j]), Font.TIMES_ROMAN, 8, Font.NORMAL, Element.ALIGN_RIGHT, 0));
                }

                registros.close();
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
    }

    public void compras(HttpServletResponse response, String [] cabTabla, float [] anchoTabla, ResultSet registros, DataBase objDB)
    {
        //int sumarDesde = 6;
        //int num_sumas = cabTabla.length - sumarDesde;
        float sumas[] = new float[6];
        float valor = 0;
        /* inicio PDF */
        Rectangle orientacion = (this.vertical) ? PageSize.A4 : PageSize.A4.rotate();
        Document document = new Document(orientacion);// paso 1
        document.setMargins(0,0,50,80); /*Izquierda, derecha, tope, pie */
        try{
            PdfWriter writer = PdfWriter.getInstance(document, response.getOutputStream()); // paso 2
            writer.setPageEvent(new Reporte(this.vertical, this.logo, this.titulo, this.ruc, this.subtitulo, this.direccion, this.sucursal, this.rep_pie));


            document.open(); // paso 3

            /* todo el cuerpo del doc es el paso 4 */

            document.add(Addons.setCabecera(this.logo, this.titulo, this.ruc, this.subtitulo, this.direccion, this.sucursal));

            document.add(Addons.setCabeceraTabla(cabTabla, anchoTabla));

            PdfPTable tbl_det = new PdfPTable(anchoTabla);
            int num = 1;
            String id_factura_compra = "";
            try{
                while(registros.next()){
                    tbl_det.addCell(Addons.setCeldaPDF(String.valueOf(num), Font.TIMES_ROMAN, 8, Font.NORMAL, Element.ALIGN_CENTER, 1));
                    for(int i=2; i<=6; i++){
                        tbl_det.addCell(Addons.setCeldaPDF(((registros.getString(i)!=null) ? registros.getString(i) : ""), Font.TIMES_ROMAN, 8, Font.NORMAL, Element.ALIGN_LEFT, 1));
                    }
                    valor = (registros.getString("subtotal")!=null) ? registros.getFloat("subtotal") : 0;
                    tbl_det.addCell(Addons.setCeldaPDF(String.valueOf(valor), Font.TIMES_ROMAN, 8, Font.NORMAL, Element.ALIGN_RIGHT, 1));
                    sumas[0] = Float.parseFloat(this.formatoNumero.format(sumas[0])) + valor;

                    valor = (registros.getString("descuento")!=null) ? registros.getFloat("descuento") : 0;
                    tbl_det.addCell(Addons.setCeldaPDF(String.valueOf(valor), Font.TIMES_ROMAN, 8, Font.NORMAL, Element.ALIGN_RIGHT, 1));
                    sumas[1] = Float.parseFloat(this.formatoNumero.format(sumas[1])) + valor;

                    valor = (registros.getString("subtotal_0")!=null) ? registros.getFloat("subtotal_0") : 0;
                    tbl_det.addCell(Addons.setCeldaPDF(String.valueOf(valor), Font.TIMES_ROMAN, 8, Font.NORMAL, Element.ALIGN_RIGHT, 1));
                    sumas[2] = Float.parseFloat(this.formatoNumero.format(sumas[2])) + valor;

                    valor = (registros.getString("iva_2")!=null) ? registros.getFloat("iva_2") : 0;
                    tbl_det.addCell(Addons.setCeldaPDF(String.valueOf(valor), Font.TIMES_ROMAN, 8, Font.NORMAL, Element.ALIGN_RIGHT, 1));
                    sumas[3] = Float.parseFloat(this.formatoNumero.format(sumas[3])) + valor;

                    id_factura_compra = (registros.getString("id_factura_compra")!=null) ? registros.getString("id_factura_compra") : "";
                    valor = 0;
                    try{
                        ResultSet rs = objDB.consulta("select ret_impuesto_retenido from tbl_retencion_compra where id_factura_compra="+id_factura_compra+" and documento='f'");
                        if(rs.next()){
                            valor = (rs.getString("ret_impuesto_retenido")!=null) ? rs.getFloat("ret_impuesto_retenido") : 0;
                            rs.close();
                        }
                    }catch(Exception e){
                        e.printStackTrace();
                    }

                    tbl_det.addCell(Addons.setCeldaPDF(String.valueOf(valor), Font.TIMES_ROMAN, 8, Font.NORMAL, Element.ALIGN_RIGHT, 1));
                    sumas[4] = Float.parseFloat(this.formatoNumero.format(sumas[4])) + valor;

                    valor = (registros.getString("total_compra")!=null) ? registros.getFloat("total_compra") : 0;
                    tbl_det.addCell(Addons.setCeldaPDF(String.valueOf(valor), Font.TIMES_ROMAN, 8, Font.NORMAL, Element.ALIGN_RIGHT, 1));
                    sumas[5] = Float.parseFloat(this.formatoNumero.format(sumas[5])) + valor;
                    
                    num++;
                }

                tbl_det.addCell(Addons.setCeldaPDF("TOTALES:  ", Font.TIMES_ROMAN, 8, Font.NORMAL, Element.ALIGN_RIGHT, 0, 4, 6));
                for(int j=0; j<sumas.length; j++){
                    tbl_det.addCell(Addons.setCeldaPDF(Addons.truncar(sumas[j]), Font.TIMES_ROMAN, 8, Font.NORMAL, Element.ALIGN_RIGHT, 0));
                }

                registros.close();
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
    }

    public void liquidaciones(HttpServletResponse response, String [] cabTabla, float [] anchoTabla, ResultSet registros, DataBase objDB)
    {
        //int sumarDesde = 6;
        //int num_sumas = cabTabla.length - sumarDesde;
        float sumas[] = new float[5];
        float valor = 0;
        /* inicio PDF */
        Rectangle orientacion = (this.vertical) ? PageSize.A4 : PageSize.A4.rotate();
        Document document = new Document(orientacion);// paso 1
        document.setMargins(0,0,50,80); /*Izquierda, derecha, tope, pie */
        try{
            PdfWriter writer = PdfWriter.getInstance(document, response.getOutputStream()); // paso 2
            writer.setPageEvent(new Reporte(this.vertical, this.logo, this.titulo, this.ruc, this.subtitulo, this.direccion, this.sucursal, this.rep_pie));


            document.open(); // paso 3

            /* todo el cuerpo del doc es el paso 4 */

            document.add(Addons.setCabecera(this.logo, this.titulo, this.ruc, this.subtitulo, this.direccion, this.sucursal));

            document.add(Addons.setCabeceraTabla(cabTabla, anchoTabla));

            PdfPTable tbl_det = new PdfPTable(anchoTabla);
            int num = 1;
            String id_liquidacion_compra = "";
            try{
                while(registros.next()){
                    tbl_det.addCell(Addons.setCeldaPDF(String.valueOf(num), Font.TIMES_ROMAN, 8, Font.NORMAL, Element.ALIGN_CENTER, 1));
                    for(int i=2; i<=6; i++){
                        tbl_det.addCell(Addons.setCeldaPDF(((registros.getString(i)!=null) ? registros.getString(i) : ""), Font.TIMES_ROMAN, 8, Font.NORMAL, Element.ALIGN_LEFT, 1));
                    }
                    valor = (registros.getString("subtotal")!=null) ? registros.getFloat("subtotal") : 0;
                    tbl_det.addCell(Addons.setCeldaPDF(String.valueOf(valor), Font.TIMES_ROMAN, 8, Font.NORMAL, Element.ALIGN_RIGHT, 1));
                    sumas[0] = Float.parseFloat(this.formatoNumero.format(sumas[0])) + valor;

                    valor = (registros.getString("subtotal_0")!=null) ? registros.getFloat("subtotal_0") : 0;
                    tbl_det.addCell(Addons.setCeldaPDF(String.valueOf(valor), Font.TIMES_ROMAN, 8, Font.NORMAL, Element.ALIGN_RIGHT, 1));
                    sumas[1] = Float.parseFloat(this.formatoNumero.format(sumas[1])) + valor;

                    valor = (registros.getString("iva_2")!=null) ? registros.getFloat("iva_2") : 0;
                    tbl_det.addCell(Addons.setCeldaPDF(String.valueOf(valor), Font.TIMES_ROMAN, 8, Font.NORMAL, Element.ALIGN_RIGHT, 1));
                    sumas[2] = Float.parseFloat(this.formatoNumero.format(sumas[2])) + valor;

                    id_liquidacion_compra = (registros.getString("id_liquidacion_compra")!=null) ? registros.getString("id_liquidacion_compra") : "";
                    valor = 0;
                    try{
                        ResultSet rs = objDB.consulta("select ret_impuesto_retenido from tbl_retencion_compra where id_factura_compra="+id_liquidacion_compra+" and documento='l'");
                        if(rs.next()){
                            valor = (rs.getString("ret_impuesto_retenido")!=null) ? rs.getFloat("ret_impuesto_retenido") : 0;
                            rs.close();
                        }
                    }catch(Exception e){
                        e.printStackTrace();
                    }

                    tbl_det.addCell(Addons.setCeldaPDF(String.valueOf(valor), Font.TIMES_ROMAN, 8, Font.NORMAL, Element.ALIGN_RIGHT, 1));
                    sumas[3] = Float.parseFloat(this.formatoNumero.format(sumas[3])) + valor;

                    valor = (registros.getString("total_compra")!=null) ? registros.getFloat("total_compra") : 0;
                    tbl_det.addCell(Addons.setCeldaPDF(String.valueOf(valor), Font.TIMES_ROMAN, 8, Font.NORMAL, Element.ALIGN_RIGHT, 1));
                    sumas[4] = Float.parseFloat(this.formatoNumero.format(sumas[4])) + valor;

                    num++;
                }

                tbl_det.addCell(Addons.setCeldaPDF("TOTALES:  ", Font.TIMES_ROMAN, 8, Font.NORMAL, Element.ALIGN_RIGHT, 0, 4, 6));
                for(int j=0; j<sumas.length; j++){
                    tbl_det.addCell(Addons.setCeldaPDF(Addons.truncar(sumas[j]), Font.TIMES_ROMAN, 8, Font.NORMAL, Element.ALIGN_RIGHT, 0));
                }

                registros.close();
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
    }

    public void notasVenta(HttpServletResponse response, String [] cabTabla, float [] anchoTabla, ResultSet registros, DataBase objDB)
    {
        //int sumarDesde = 6;
        //int num_sumas = cabTabla.length - sumarDesde;
        float sumas[] = new float[2];
        float valor = 0;
        /* inicio PDF */
        Rectangle orientacion = (this.vertical) ? PageSize.A4 : PageSize.A4.rotate();
        Document document = new Document(orientacion);// paso 1
        document.setMargins(0,0,50,80); /*Izquierda, derecha, tope, pie */
        try{
            PdfWriter writer = PdfWriter.getInstance(document, response.getOutputStream()); // paso 2
            writer.setPageEvent(new Reporte(this.vertical, this.logo, this.titulo, this.ruc, this.subtitulo, this.direccion, this.sucursal, this.rep_pie));


            document.open(); // paso 3

            /* todo el cuerpo del doc es el paso 4 */

            document.add(Addons.setCabecera(this.logo, this.titulo, this.ruc, this.subtitulo, this.direccion, this.sucursal));

            document.add(Addons.setCabeceraTabla(cabTabla, anchoTabla));

            PdfPTable tbl_det = new PdfPTable(anchoTabla);
            int num = 1;
            String id_nota_venta_compra = "";
            try{
                while(registros.next()){
                    tbl_det.addCell(Addons.setCeldaPDF(String.valueOf(num), Font.TIMES_ROMAN, 8, Font.NORMAL, Element.ALIGN_CENTER, 1));
                    for(int i=2; i<=6; i++){
                        tbl_det.addCell(Addons.setCeldaPDF(((registros.getString(i)!=null) ? registros.getString(i) : ""), Font.TIMES_ROMAN, 8, Font.NORMAL, Element.ALIGN_LEFT, 1));
                    }
                    
                    id_nota_venta_compra = (registros.getString("id_nota_venta_compra")!=null) ? registros.getString("id_nota_venta_compra") : "";
                    valor = 0;
                    try{
                        ResultSet rs = objDB.consulta("select ret_impuesto_retenido from tbl_retencion_compra where id_factura_compra="+id_nota_venta_compra+" and documento='v'");
                        if(rs.next()){
                            valor = (rs.getString("ret_impuesto_retenido")!=null) ? rs.getFloat("ret_impuesto_retenido") : 0;
                            rs.close();
                        }
                    }catch(Exception e){
                        e.printStackTrace();
                    }

                    tbl_det.addCell(Addons.setCeldaPDF(String.valueOf(valor), Font.TIMES_ROMAN, 8, Font.NORMAL, Element.ALIGN_RIGHT, 1));
                    sumas[0] = Float.parseFloat(this.formatoNumero.format(sumas[0])) + valor;

                    valor = (registros.getString("total_compra")!=null) ? registros.getFloat("total_compra") : 0;
                    tbl_det.addCell(Addons.setCeldaPDF(String.valueOf(valor), Font.TIMES_ROMAN, 8, Font.NORMAL, Element.ALIGN_RIGHT, 1));
                    sumas[1] = Float.parseFloat(this.formatoNumero.format(sumas[1])) + valor;

                    num++;
                }

                tbl_det.addCell(Addons.setCeldaPDF("TOTALES:  ", Font.TIMES_ROMAN, 8, Font.NORMAL, Element.ALIGN_RIGHT, 0, 4, 6));
                for(int j=0; j<sumas.length; j++){
                    tbl_det.addCell(Addons.setCeldaPDF(Addons.truncar(sumas[j]), Font.TIMES_ROMAN, 8, Font.NORMAL, Element.ALIGN_RIGHT, 0));
                }

                registros.close();
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
    }

    public void cierreCaja(HttpServletResponse response, int id_sucursal_sesion, String fecha, DataBase objDB)
    {
        /* inicio PDF */
        Rectangle orientacion = (this.vertical) ? PageSize.A4 : PageSize.A4.rotate();
        Document document = new Document(orientacion);// paso 1
        document.setMargins(0,0,50,80); /*Izquierda, derecha, tope, pie */
        try{
            PdfWriter writer = PdfWriter.getInstance(document, response.getOutputStream()); // paso 2
            writer.setPageEvent(new Reporte(this.vertical, this.logo, this.titulo, this.ruc, this.subtitulo, this.direccion, this.sucursal, this.rep_pie));
            document.open(); // paso 3

            /* todo el cuerpo del doc es el paso 4 */

            document.add(Addons.setCabecera(this.logo, this.titulo, this.ruc, this.subtitulo, this.direccion, this.sucursal));

            
            ResultSet ventas = objDB.consulta("select vendedor,txt_forma_pago,sum(total) as total from vta_factura_venta where id_sucursal="+id_sucursal_sesion+" and anulado=false and fecha_emision='"+fecha+"' group by vendedor,txt_forma_pago order by vendedor");
            ResultSet retencion = null;

            PdfPTable tbl_det = new PdfPTable(new float[]{70f,40f,120f});

            try{
                String aux = "";
                float ingresos = 0;
                float total_ingresos = 0;
                float efectivo = 0;
                float total_efectivo = 0;
                float ret = 0;
                float total_ret = 0;
                String vendedor = "";
                String forma_pago = "";
                float total = 0;
                tbl_det.addCell(Addons.setCeldaPDF("VENTAS", Font.TIMES_ROMAN, 8, Font.BOLD, Element.ALIGN_LEFT, 0, Color.cyan, 3, 3));
                while(ventas.next()){
                    vendedor = ventas.getString("vendedor")!=null ? ventas.getString("vendedor") : "";
                    forma_pago = ventas.getString("txt_forma_pago")!=null ? ventas.getString("txt_forma_pago") : "";
                    total = ventas.getString("total")!=null ? ventas.getFloat("total") : 0;
                    if(aux.compareTo(vendedor)!=0){
                        if(aux.compareTo("")!=0){
                            tbl_det.addCell(Addons.setCeldaPDF("       TOTAL ", Font.TIMES_ROMAN, 8, Font.NORMAL, Element.ALIGN_LEFT, 0));
                            tbl_det.addCell(Addons.setCeldaPDF(String.valueOf(ingresos), Font.TIMES_ROMAN, 8, Font.NORMAL, Element.ALIGN_RIGHT, 0));
                            tbl_det.addCell(Addons.setCeldaPDF(" ", Font.TIMES_ROMAN, 8, Font.NORMAL, Element.ALIGN_LEFT, 0));

                            tbl_det.addCell(Addons.setCeldaPDF("       EN CAJA ", Font.TIMES_ROMAN, 8, Font.NORMAL, Element.ALIGN_LEFT, 0));
                            tbl_det.addCell(Addons.setCeldaPDF(String.valueOf(efectivo - ret), Font.TIMES_ROMAN, 8, Font.NORMAL, Element.ALIGN_RIGHT, 0));
                            tbl_det.addCell(Addons.setCeldaPDF(" ", Font.TIMES_ROMAN, 8, Font.NORMAL, Element.ALIGN_LEFT, 0));
                            tbl_det.addCell(Addons.setCeldaPDF("___________________________________________________________________________________________________________", Font.TIMES_ROMAN, 8, Font.NORMAL, Element.ALIGN_LEFT, 0, 0, 3));
                        }
                        tbl_det.addCell(Addons.setCeldaPDF(vendedor, Font.TIMES_ROMAN, 8, Font.BOLD, Element.ALIGN_LEFT, 0, 4, 3));
                        
                        /* retenciones */
                        try{
                            retencion = objDB.consulta("select sum(R.ret_impuesto_retenido) as total from tbl_factura_venta as F inner join tbl_retencion_venta as R "
                            + "on R.id_factura_venta=F.id_factura_venta where id_sucursal="+id_sucursal_sesion+" and F.anulado=false and R.anulado=false and F.vendedor='"+vendedor+"' and ret_fecha_emision='"+fecha+"'");
                            if(retencion.next()){
                                ret = retencion.getString("total")!=null ? retencion.getFloat("total") : 0;
                                tbl_det.addCell(Addons.setCeldaPDF("       RETENCIONES ", Font.TIMES_ROMAN, 8, Font.NORMAL, Element.ALIGN_LEFT, 0));
                                tbl_det.addCell(Addons.setCeldaPDF(String.valueOf(ret), Font.TIMES_ROMAN, 8, Font.NORMAL, Element.ALIGN_RIGHT, 0));
                                tbl_det.addCell(Addons.setCeldaPDF(" ", Font.TIMES_ROMAN, 8, Font.NORMAL, Element.ALIGN_LEFT, 0));
                                ingresos += ret;
                                total_ret += ret;
                            }
                        }catch(Exception ex){}
                        

                        aux = vendedor;
                        ingresos = 0;
                        efectivo = 0;
                    }
                    ingresos += total;
                    total_ingresos += total;
                    if(forma_pago.compareTo("EFECTIVO")==0){
                        efectivo += total;
                        total_efectivo += total;
                        //total_caja += total;
                    }
                    tbl_det.addCell(Addons.setCeldaPDF("       "+forma_pago, Font.TIMES_ROMAN, 8, Font.NORMAL, Element.ALIGN_LEFT, 0));
                    tbl_det.addCell(Addons.setCeldaPDF(String.valueOf(total), Font.TIMES_ROMAN, 8, Font.NORMAL, Element.ALIGN_RIGHT, 0));
                    tbl_det.addCell(Addons.setCeldaPDF(" ", Font.TIMES_ROMAN, 8, Font.NORMAL, Element.ALIGN_LEFT, 0));
                }

                tbl_det.addCell(Addons.setCeldaPDF("       TOTAL ", Font.TIMES_ROMAN, 8, Font.NORMAL, Element.ALIGN_LEFT, 0));
                tbl_det.addCell(Addons.setCeldaPDF(String.valueOf(ingresos), Font.TIMES_ROMAN, 8, Font.NORMAL, Element.ALIGN_RIGHT, 0));
                tbl_det.addCell(Addons.setCeldaPDF(" ", Font.TIMES_ROMAN, 8, Font.NORMAL, Element.ALIGN_LEFT, 0));

                tbl_det.addCell(Addons.setCeldaPDF("       EN CAJA ", Font.TIMES_ROMAN, 8, Font.NORMAL, Element.ALIGN_LEFT, 0));
                tbl_det.addCell(Addons.setCeldaPDF(String.valueOf(efectivo - ret), Font.TIMES_ROMAN, 8, Font.NORMAL, Element.ALIGN_RIGHT, 0));
                tbl_det.addCell(Addons.setCeldaPDF(" ", Font.TIMES_ROMAN, 8, Font.NORMAL, Element.ALIGN_LEFT, 0));


                tbl_det.addCell(Addons.setFilaBlanco(3, 10));
                
                /* gastos */
                tbl_det.addCell(Addons.setCeldaPDF("PAGOS", Font.TIMES_ROMAN, 8, Font.BOLD, Element.ALIGN_LEFT, 0, Color.cyan, 3, 3));
                String tipo_pago = "";
                float total_efectivo_pago = 0;
                float total_egreso = 0;
                try{
                    ResultSet pagos = objDB.consulta("select txt_tipo_pago,sum(abono) as total from vta_factura_compra_pago as f inner join tbl_comprobante_egreso as C on F.id_comprobante_egreso=C.id_comprobante_egreso where C.id_sucursal="+id_sucursal_sesion+" and fecha_pago='"+fecha+"' group by txt_tipo_pago");
                    while(pagos.next()){
                        tipo_pago = pagos.getString("txt_tipo_pago")!=null ? pagos.getString("txt_tipo_pago") : "";
                        float pago = pagos.getString("total")!=null ? pagos.getFloat("total") : 0;
                        tbl_det.addCell(Addons.setCeldaPDF("   "+tipo_pago, Font.TIMES_ROMAN, 8, Font.NORMAL, Element.ALIGN_LEFT, 0));
                        tbl_det.addCell(Addons.setCeldaPDF(String.valueOf(pago), Font.TIMES_ROMAN, 8, Font.NORMAL, Element.ALIGN_RIGHT, 0));
                        tbl_det.addCell(Addons.setCeldaPDF(" ", Font.TIMES_ROMAN, 8, Font.NORMAL, Element.ALIGN_LEFT, 0));
                        total_egreso += pago;
                        if(tipo_pago.compareTo("EFECTIVO")==0){
                            total_efectivo_pago += pago;
                            //total_caja -= total;
                        }
                    }
                    pagos.close();
                }catch(Exception e){}

                tbl_det.addCell(Addons.setFilaBlanco(3, 10));
                tbl_det.addCell(Addons.setCeldaPDF("TOTALES", Font.TIMES_ROMAN, 8, Font.BOLD, Element.ALIGN_LEFT, 0, Color.cyan, 3, 3));

                tbl_det.addCell(Addons.setCeldaPDF("   TOTAL VENTAS ", Font.TIMES_ROMAN, 8, Font.NORMAL, Element.ALIGN_LEFT, 0));
                tbl_det.addCell(Addons.setCeldaPDF(String.valueOf(total_ingresos), Font.TIMES_ROMAN, 8, Font.NORMAL, Element.ALIGN_RIGHT, 0));
                tbl_det.addCell(Addons.setCeldaPDF(" ", Font.TIMES_ROMAN, 8, Font.NORMAL, Element.ALIGN_LEFT, 0));

                tbl_det.addCell(Addons.setCeldaPDF("   TOTAL PAGOS ", Font.TIMES_ROMAN, 8, Font.NORMAL, Element.ALIGN_LEFT, 0));
                tbl_det.addCell(Addons.setCeldaPDF(String.valueOf(total_egreso), Font.TIMES_ROMAN, 8, Font.NORMAL, Element.ALIGN_RIGHT, 0));
                tbl_det.addCell(Addons.setCeldaPDF(" ", Font.TIMES_ROMAN, 8, Font.NORMAL, Element.ALIGN_LEFT, 0));

                tbl_det.addCell(Addons.setCeldaPDF("   TOTAL EN CAJA ", Font.TIMES_ROMAN, 8, Font.NORMAL, Element.ALIGN_LEFT, 0));
                tbl_det.addCell(Addons.setCeldaPDF(String.valueOf(total_efectivo - total_ret - total_efectivo_pago), Font.TIMES_ROMAN, 8, Font.NORMAL, Element.ALIGN_RIGHT, 0));
                tbl_det.addCell(Addons.setCeldaPDF(" ", Font.TIMES_ROMAN, 8, Font.NORMAL, Element.ALIGN_LEFT, 0));

                ventas.close();
                retencion.close();
                
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
    }

    public void ingresosPeriodos(HttpServletResponse response, ResultSet emisiones, ResultSet recaudos, ResultSet xcobrar, ResultSet instalaciones, ResultSet otros)
    {
        /* inicio PDF */
        Rectangle orientacion = (this.vertical) ? PageSize.A4 : PageSize.A4.rotate();
        Document document = new Document(orientacion);// paso 1
        document.setMargins(0,0,50,80); /*Izquierda, derecha, tope, pie */
        try{
            PdfWriter writer = PdfWriter.getInstance(document, response.getOutputStream()); // paso 2
            writer.setPageEvent(new Reporte(this.vertical, this.logo, this.titulo, this.ruc, this.subtitulo, this.direccion, this.sucursal, this.rep_pie));


            document.open(); // paso 3

            /* todo el cuerpo del doc es el paso 4 */

            float[] anchoTabla = new float[]{25f,15f,15f,15f,15f,15f,20f};
                    
            document.add(Addons.setCabecera(this.logo, this.titulo, this.ruc, this.subtitulo, this.direccion, this.sucursal));

            /*PdfPTable tbl_tit = new PdfPTable(1);
            tbl_tit.addCell(Addons.setCeldaPDF(" ", Font.TIMES_ROMAN, 8, Font.NORMAL, Element.ALIGN_LEFT, 0));
            tbl_tit.addCell(Addons.setCeldaPDF("SERVIVIO DE INTERNET", Font.TIMES_ROMAN, 10, Font.NORMAL, Element.ALIGN_LEFT, 0));
            document.add(tbl_tit);*/

            document.add(Addons.setCabeceraTabla(new String [] {"PERIODO", "INSTALACIONES", "OTROS INGRESOS", "PRE-FACTURADO", "EMITIDO", "POR COBRAR", "SUMATORIA"}, anchoTabla));
            
            String[][] mat_emisiones = DatosDinamicos.ResultSetToMatriz(emisiones);
            String[][] mat_recaudos = DatosDinamicos.ResultSetToMatriz(recaudos);
            String[][] mat_xcobrar = DatosDinamicos.ResultSetToMatriz(xcobrar);
            String[][] mat_instalaciones = DatosDinamicos.ResultSetToMatriz(instalaciones);
            String[][] mat_otros = DatosDinamicos.ResultSetToMatriz(otros);

            PdfPTable tbl_det = new PdfPTable(anchoTabla);
            int pos = -1;
            float suma = 0;
            String matriz = "";
            for(int i=0; i<mat_emisiones.length; i++){
                if(matriz.compareTo(mat_emisiones[i][0])!=0){
                    tbl_det.addCell(Addons.setCeldaPDF(mat_emisiones[i][0], Font.TIMES_ROMAN, 8, Font.NORMAL, Element.ALIGN_CENTER, 1, 4, 7));
                    matriz = mat_emisiones[i][0];
                }

                tbl_det.addCell(Addons.setCeldaPDF(mat_emisiones[i][1], Font.TIMES_ROMAN, 8, Font.NORMAL, Element.ALIGN_LEFT, 1));

                pos = DatosDinamicos.enMatrizSecuencialSucursal(mat_instalaciones, mat_emisiones[i][1], 1, mat_emisiones[i][0]);
                suma = Float.valueOf(pos!=-1 ? mat_instalaciones[pos][2] : "0");
                tbl_det.addCell(Addons.setCeldaPDF(pos!=-1 ? mat_instalaciones[pos][2] : "0", Font.TIMES_ROMAN, 8, Font.NORMAL, Element.ALIGN_RIGHT, 1));

                pos = DatosDinamicos.enMatrizSecuencialSucursal(mat_otros, mat_emisiones[i][1], 1, mat_emisiones[i][0]);
                suma += Float.valueOf(pos!=-1 ? mat_otros[pos][2] : "0");
                tbl_det.addCell(Addons.setCeldaPDF(pos!=-1 ? mat_otros[pos][2] : "0", Font.TIMES_ROMAN, 8, Font.NORMAL, Element.ALIGN_RIGHT, 1));

                tbl_det.addCell(Addons.setCeldaPDF(mat_emisiones[i][2], Font.TIMES_ROMAN, 8, Font.NORMAL, Element.ALIGN_RIGHT, 1));

                pos = DatosDinamicos.enMatrizSecuencialSucursal(mat_recaudos, mat_emisiones[i][1], 1, mat_emisiones[i][0]);
                suma += Float.valueOf(pos!=-1 ? mat_recaudos[pos][2] : "0");
                tbl_det.addCell(Addons.setCeldaPDF(pos!=-1 ? mat_recaudos[pos][2] : "0", Font.TIMES_ROMAN, 8, Font.NORMAL, Element.ALIGN_RIGHT, 1));
                
                pos = DatosDinamicos.enMatrizSecuencialSucursal(mat_xcobrar, mat_emisiones[i][1], 1, mat_emisiones[i][0]);
                tbl_det.addCell(Addons.setCeldaPDF(pos!=-1 ? mat_xcobrar[pos][2] : "0", Font.TIMES_ROMAN, 8, Font.NORMAL, Element.ALIGN_RIGHT, 1));

                tbl_det.addCell(Addons.setCeldaPDF(Addons.redondear(String.valueOf(suma)), Font.TIMES_ROMAN, 8, Font.NORMAL, Element.ALIGN_RIGHT, 1));
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
    }

    public void ingresosEgresos(HttpServletResponse response, int id_sucursal, String fecha, DataBase objDB, String banco, String caja)
    {
        /* inicio PDF */
        Rectangle orientacion = (this.vertical) ? PageSize.A4 : PageSize.A4.rotate();
        Document document = new Document(orientacion);// paso 1
        document.setMargins(0,0,50,80); /*Izquierda, derecha, tope, pie */
        try{
            PdfWriter writer = PdfWriter.getInstance(document, response.getOutputStream()); // paso 2
            writer.setPageEvent(new Reporte(this.vertical, this.logo, this.titulo, this.ruc, this.subtitulo, this.direccion, this.sucursal, this.rep_pie));
            document.open(); // paso 3

            /* todo el cuerpo del doc es el paso 4 */

            document.add(Addons.setCabecera(this.logo, this.titulo, this.ruc, this.subtitulo, this.direccion, this.sucursal));


            ResultSet ventas = objDB.consulta("select CASE tipo_cobro WHEN 'e' THEN 'EFECTIVO' WHEN 'c' THEN 'CHEQUE' ELSE '' END AS txt_tipo_cobro,"
                + "sum(total) as total from vta_comprobante_ingreso where id_sucursal="+id_sucursal+" and anulado=false and fecha_actual='"+fecha+"' group by txt_tipo_cobro");
            ResultSet retencion = null;

            PdfPTable tbl_det = new PdfPTable(new float[]{130f,40f,60f});

            try{
                float total_ingresos = 0;
                float total_efectivo = 0;
                float ret = 0;
                float total_ret = 0;
                String forma_pago = "";
                float total = 0;
                tbl_det.addCell(Addons.setCeldaPDF("INGRESOS", Font.TIMES_ROMAN, 8, Font.BOLD, Element.ALIGN_LEFT, 0, Color.cyan, 3, 3));
                while(ventas.next()){
                    forma_pago = ventas.getString("txt_tipo_cobro")!=null ? ventas.getString("txt_tipo_cobro") : "";
                    total = ventas.getString("total")!=null ? ventas.getFloat("total") : 0;
                    
                    total_ingresos += total;
                    if(forma_pago.compareTo("EFECTIVO")==0){
                        total_efectivo += total;
                    }
                    tbl_det.addCell(Addons.setCeldaPDF("   "+forma_pago, Font.TIMES_ROMAN, 8, Font.NORMAL, Element.ALIGN_LEFT, 0));
                    tbl_det.addCell(Addons.setCeldaPDF(String.valueOf(total), Font.TIMES_ROMAN, 8, Font.NORMAL, Element.ALIGN_RIGHT, 0));
                    tbl_det.addCell(Addons.setCeldaPDF(" ", Font.TIMES_ROMAN, 8, Font.NORMAL, Element.ALIGN_LEFT, 0));
                }

                /* retenciones */
                try{
                    retencion = objDB.consulta("select sum(R.ret_impuesto_retenido) as total from tbl_factura_venta as F inner join tbl_retencion_venta as R "
                    + "on R.id_factura_venta=F.id_factura_venta where id_sucursal="+id_sucursal+" and F.anulado=false and R.anulado=false and ret_fecha_emision='"+fecha+"'");
                    if(retencion.next()){
                        ret = retencion.getString("total")!=null ? retencion.getFloat("total") : 0;
                        tbl_det.addCell(Addons.setCeldaPDF("   RETENCIONES ", Font.TIMES_ROMAN, 8, Font.NORMAL, Element.ALIGN_LEFT, 0));
                        tbl_det.addCell(Addons.setCeldaPDF(String.valueOf(ret), Font.TIMES_ROMAN, 8, Font.NORMAL, Element.ALIGN_RIGHT, 0));
                        tbl_det.addCell(Addons.setCeldaPDF(" ", Font.TIMES_ROMAN, 8, Font.NORMAL, Element.ALIGN_LEFT, 0));
                        total_ret += ret;
                    }
                }catch(Exception ex){}

                /*tbl_det.addCell(Addons.setCeldaPDF("   TOTAL ", Font.TIMES_ROMAN, 8, Font.NORMAL, Element.ALIGN_LEFT, 0));
                tbl_det.addCell(Addons.setCeldaPDF(String.valueOf(ingresos), Font.TIMES_ROMAN, 8, Font.NORMAL, Element.ALIGN_RIGHT, 0));
                tbl_det.addCell(Addons.setCeldaPDF(" ", Font.TIMES_ROMAN, 8, Font.NORMAL, Element.ALIGN_LEFT, 0));*/

                tbl_det.addCell(Addons.setFilaBlanco(3, 10));

                /* gastos */
                tbl_det.addCell(Addons.setCeldaPDF("EGRESOS", Font.TIMES_ROMAN, 8, Font.BOLD, Element.ALIGN_LEFT, 0, Color.cyan, 3, 3));
                String tipo_pago = "";
                float total_efectivo_pago = 0;
                float total_egreso = 0;
                try{
                    ResultSet pagos = objDB.consulta("select CASE tipo_pago WHEN 'e' THEN 'EFECTIVO' WHEN 'c' THEN 'CHEQUE' ELSE '' END AS txt_tipo_pago,sum(total) as total "
                            + "from tbl_comprobante_egreso where id_sucursal="+id_sucursal+" and anulado=false and fecha_actual='"+fecha+"' group by txt_tipo_pago");
                    while(pagos.next()){
                        tipo_pago = pagos.getString("txt_tipo_pago")!=null ? pagos.getString("txt_tipo_pago") : "";
                        float pago = pagos.getString("total")!=null ? pagos.getFloat("total") : 0;
                        tbl_det.addCell(Addons.setCeldaPDF("   "+tipo_pago, Font.TIMES_ROMAN, 8, Font.NORMAL, Element.ALIGN_LEFT, 0));
                        tbl_det.addCell(Addons.setCeldaPDF(String.valueOf(pago), Font.TIMES_ROMAN, 8, Font.NORMAL, Element.ALIGN_RIGHT, 0));
                        tbl_det.addCell(Addons.setCeldaPDF(" ", Font.TIMES_ROMAN, 8, Font.NORMAL, Element.ALIGN_LEFT, 0));
                        total_egreso += pago;
                        if(tipo_pago.compareTo("EFECTIVO")==0){
                            total_efectivo_pago += pago;
                        }
                    }
                    pagos.close();
                }catch(Exception e){}

                tbl_det.addCell(Addons.setFilaBlanco(3, 10));
                tbl_det.addCell(Addons.setCeldaPDF("TOTALES", Font.TIMES_ROMAN, 8, Font.BOLD, Element.ALIGN_LEFT, 0, Color.cyan, 3, 3));

                tbl_det.addCell(Addons.setCeldaPDF("   TOTAL INGRESOS ", Font.TIMES_ROMAN, 8, Font.NORMAL, Element.ALIGN_LEFT, 0));
                tbl_det.addCell(Addons.setCeldaPDF(String.valueOf(total_ingresos), Font.TIMES_ROMAN, 8, Font.NORMAL, Element.ALIGN_RIGHT, 0));
                tbl_det.addCell(Addons.setCeldaPDF(" ", Font.TIMES_ROMAN, 8, Font.NORMAL, Element.ALIGN_LEFT, 0));

                tbl_det.addCell(Addons.setCeldaPDF("   TOTAL EGRESOS ", Font.TIMES_ROMAN, 8, Font.NORMAL, Element.ALIGN_LEFT, 0));
                tbl_det.addCell(Addons.setCeldaPDF(String.valueOf(total_egreso), Font.TIMES_ROMAN, 8, Font.NORMAL, Element.ALIGN_RIGHT, 0));
                tbl_det.addCell(Addons.setCeldaPDF(" ", Font.TIMES_ROMAN, 8, Font.NORMAL, Element.ALIGN_LEFT, 0));

                tbl_det.addCell(Addons.setCeldaPDF("   INGRESOS - EGRESOS - RETENCIONES ", Font.TIMES_ROMAN, 8, Font.NORMAL, Element.ALIGN_LEFT, 0));
                tbl_det.addCell(Addons.setCeldaPDF(String.valueOf(total_ingresos - total_egreso - total_ret), Font.TIMES_ROMAN, 8, Font.NORMAL, Element.ALIGN_RIGHT, 0));
                tbl_det.addCell(Addons.setCeldaPDF(" ", Font.TIMES_ROMAN, 8, Font.NORMAL, Element.ALIGN_LEFT, 0));

                tbl_det.addCell(Addons.setCeldaPDF("   INGRESOS (EFECTIVO) - EGRESOS (EFECTIVO) - RETENCIONES ", Font.TIMES_ROMAN, 8, Font.NORMAL, Element.ALIGN_LEFT, 0));
                tbl_det.addCell(Addons.setCeldaPDF(String.valueOf(total_efectivo - total_efectivo_pago - total_ret), Font.TIMES_ROMAN, 8, Font.NORMAL, Element.ALIGN_RIGHT, 0));
                tbl_det.addCell(Addons.setCeldaPDF(" ", Font.TIMES_ROMAN, 8, Font.NORMAL, Element.ALIGN_LEFT, 0));

                ventas.close();
                retencion.close();


                /* transferencias al banco */
                try{
                    tbl_det.addCell(Addons.setFilaBlanco(3, 10));
                    tbl_det.addCell(Addons.setCeldaPDF("COMPROBANTES DE DIARIO CON BANCOS", Font.TIMES_ROMAN, 8, Font.BOLD, Element.ALIGN_LEFT, 0, Color.cyan, 3, 3));

                    ResultSet diarios = objDB.consulta("select distinct C.detalle, C.total from tbl_comprobante_diario as C inner join tbl_comprobante_diario_detalle as D "
                    + "on C.id_comprobante_diario=D.id_comprobante_diario where C.id_sucursal="+id_sucursal+" and C.anulado=false and fecha_actual='"+fecha+"' "
                    + "and D.id_plan_cuenta in ("+banco+","+caja+")");
                    while(diarios.next()){
                        ret = diarios.getString("total")!=null ? diarios.getFloat("total") : 0;
                        tbl_det.addCell(Addons.setCeldaPDF((diarios.getString("detalle")!=null ? diarios.getString("detalle") : ""), Font.TIMES_ROMAN, 8, Font.NORMAL, Element.ALIGN_LEFT, 0));
                        tbl_det.addCell(Addons.setCeldaPDF(String.valueOf(ret), Font.TIMES_ROMAN, 8, Font.NORMAL, Element.ALIGN_RIGHT, 0));
                        tbl_det.addCell(Addons.setCeldaPDF(" ", Font.TIMES_ROMAN, 8, Font.NORMAL, Element.ALIGN_LEFT, 0));
                    }
                    diarios.close();
                }catch(Exception ex){}

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
    }
    
    public void tabular(HttpServletResponse response, String [] cabTabla, float [] anchoTabla, ResultSet registros)
    {
        /* inicio PDF */     
        Rectangle orientacion = (this.vertical) ? PageSize.A4 : PageSize.A4.rotate();
        Document document = new Document(orientacion);// paso 1
        document.setMargins(0,0,50,80); /*Izquierda, derecha, tope, pie */
        try{
            PdfWriter writer = PdfWriter.getInstance(document, response.getOutputStream()); // paso 2
            writer.setPageEvent(new Reporte(this.vertical, this.logo, this.titulo, this.ruc, this.subtitulo, this.direccion, this.sucursal, this.rep_pie));
            document.open(); // paso 3
            
            /* todo el cuerpo del doc es el paso 4 */
            
            document.add(Addons.setCabecera(this.logo, this.titulo, this.ruc, this.subtitulo, this.direccion, this.sucursal));
            
            document.add(Addons.setCabeceraTabla(cabTabla, anchoTabla));
            
            ResultSetMetaData mdata = registros.getMetaData();      
            int numCols = mdata.getColumnCount();
            PdfPTable tbl_det = new PdfPTable(anchoTabla);
            int num = 1;
            int num2 = 1;
            String aux = "";
            String columna1 = "";
            float suma_parcial = 0;
            float suma_total = 0;
            float consumo = 0;
            int alineacion = Element.ALIGN_LEFT;
            try{
                while(registros.next()){
                    columna1 = (registros.getString(1)!=null) ? registros.getString(1) : "";
                    if(aux.compareTo(columna1)!=0){
                        if(num>1){
                            tbl_det.addCell(Addons.setCeldaPDF("SUBTOTAL:  "+ Addons.truncar(suma_parcial), Font.TIMES_ROMAN, 9, Font.NORMAL, Element.ALIGN_RIGHT, 0, 4, numCols+2)); 
                        }
                        tbl_det.addCell(Addons.setCeldaPDF(columna1, Font.TIMES_ROMAN, 8, Font.NORMAL, Element.ALIGN_LEFT, 1, 3, numCols+1)); 
                        aux = columna1;
                        suma_parcial = 0;
                        num2 = 1;
                    }
                    tbl_det.addCell(Addons.setCeldaPDF(String.valueOf(num), Font.TIMES_ROMAN, 8, Font.NORMAL, Element.ALIGN_CENTER, 1));
                    tbl_det.addCell(Addons.setCeldaPDF(String.valueOf(num2), Font.TIMES_ROMAN, 8, Font.NORMAL, Element.ALIGN_CENTER, 1));
                    for(int i=2; i<=numCols; i++){
                        alineacion = (i==numCols) ? Element.ALIGN_RIGHT : Element.ALIGN_LEFT;
                        tbl_det.addCell(Addons.setCeldaPDF(((registros.getString(i)!=null) ? registros.getString(i) : ""), Font.TIMES_ROMAN, 8, Font.NORMAL, alineacion, 1));
                    }
                    consumo = (registros.getString(numCols)!=null) ? registros.getFloat(numCols) : 0;
                    
                    suma_parcial = Float.parseFloat(this.formatoNumero.format(suma_parcial)) + consumo;
                    suma_total = Float.parseFloat(this.formatoNumero.format(suma_total)) + consumo;
                    num++;
                    num2++;
                }
                tbl_det.addCell(Addons.setCeldaPDF("SUBTOTAL:  "+ Addons.truncar(suma_parcial), Font.TIMES_ROMAN, 9, Font.NORMAL, Element.ALIGN_RIGHT, 0, 4, numCols+2)); 
                tbl_det.addCell(Addons.setCeldaPDF("TOTAL:  "+ Addons.truncar(suma_total), Font.TIMES_ROMAN, 10, Font.NORMAL, Element.ALIGN_RIGHT, 0, 5, numCols+2)); 
                
                registros.close();
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
    }
    
    public void tabular(HttpServletResponse response, String [] cabTabla, float [] anchoTabla, ResultSet registros, int sumarDesde)
    {
        /* inicio PDF */
        Rectangle orientacion = (this.vertical) ? PageSize.A4 : PageSize.A4.rotate();
        Document document = new Document(orientacion);// paso 1
        document.setMargins(0,0,50,80); /*Izquierda, derecha, tope, pie */
        try{
            PdfWriter writer = PdfWriter.getInstance(document, response.getOutputStream()); // paso 2
            writer.setPageEvent(new Reporte(this.vertical, this.logo, this.titulo, this.ruc, this.subtitulo, this.direccion, this.sucursal, this.rep_pie));
            document.open(); // paso 3

            /* todo el cuerpo del doc es el paso 4 */

            document.add(Addons.setCabecera(this.logo, this.titulo, this.ruc, this.subtitulo, this.direccion, this.sucursal));

            document.add(Addons.setCabeceraTabla(cabTabla, anchoTabla));

            ResultSetMetaData mdata = registros.getMetaData();
            int numCols = mdata.getColumnCount();
            PdfPTable tbl_det = new PdfPTable(anchoTabla);
            int num = 1;
            int num2 = 1;
            String aux = "";
            String columna1 = "";
            int num_sumas = cabTabla.length - sumarDesde;
            float suma_parcial[] = new float[num_sumas];
            float suma_total[] = new float[num_sumas];
            float valor [] = new float[num_sumas];
            int pos=0;
            try{
                while(registros.next()){
                    columna1 = (registros.getString(1)!=null) ? registros.getString(1) : "";
                    pos=0;
                    if(aux.compareTo(columna1)!=0){
                        if(num>1){
                            tbl_det.addCell(Addons.setCeldaPDF("SUBTOTALES:  ", Font.TIMES_ROMAN, 8, Font.NORMAL, Element.ALIGN_RIGHT, 0, 4, sumarDesde));
                            for(int j=0; j<suma_parcial.length; j++){
                                tbl_det.addCell(Addons.setCeldaPDF(Addons.truncar(suma_parcial[j]), Font.TIMES_ROMAN, 8, Font.NORMAL, Element.ALIGN_RIGHT, 0));
                                suma_parcial[j] = 0;
                            }
                        }
                        tbl_det.addCell(Addons.setCeldaPDF(columna1, Font.TIMES_ROMAN, 8, Font.NORMAL, Element.ALIGN_LEFT, 1, 3, numCols+2));
                        aux = columna1;
                        num2 = 1;
                    }
                    tbl_det.addCell(Addons.setCeldaPDF(String.valueOf(num), Font.TIMES_ROMAN, 8, Font.NORMAL, Element.ALIGN_CENTER, 1));
                    tbl_det.addCell(Addons.setCeldaPDF(String.valueOf(num2), Font.TIMES_ROMAN, 8, Font.NORMAL, Element.ALIGN_CENTER, 1));
                    for(int i=2; i<=numCols; i++){
                        if(i<sumarDesde){
                            tbl_det.addCell(Addons.setCeldaPDF(((registros.getString(i)!=null) ? registros.getString(i) : ""), Font.TIMES_ROMAN, 8, Font.NORMAL, Element.ALIGN_LEFT, 1));
                        }else{
                            valor[pos] = (registros.getString(i)!=null) ? registros.getFloat(i) : 0;
                            tbl_det.addCell(Addons.setCeldaPDF(String.valueOf(valor[pos]), Font.TIMES_ROMAN, 8, Font.NORMAL, Element.ALIGN_RIGHT, 1));
                            suma_parcial[pos] = Float.parseFloat(this.formatoNumero.format(suma_parcial[pos])) + valor[pos];
                            suma_total[pos] = Float.parseFloat(this.formatoNumero.format(suma_total[pos])) + valor[pos];
                            pos++;
                        }
                    }
                    num++;
                    num2++;
                }
                tbl_det.addCell(Addons.setCeldaPDF("SUBTOTALES:  ", Font.TIMES_ROMAN, 8, Font.NORMAL, Element.ALIGN_RIGHT, 0, 4, sumarDesde));
                for(int j=0; j<suma_parcial.length; j++){
                    tbl_det.addCell(Addons.setCeldaPDF(Addons.truncar(suma_parcial[j]), Font.TIMES_ROMAN, 8, Font.NORMAL, Element.ALIGN_RIGHT, 0));
                }

                tbl_det.addCell(Addons.setCeldaPDF("TOTALES:  ", Font.TIMES_ROMAN, 8, Font.NORMAL, Element.ALIGN_RIGHT, 0, 4, sumarDesde));
                for(int j=0; j<suma_total.length; j++){
                    tbl_det.addCell(Addons.setCeldaPDF(Addons.truncar(suma_total[j]), Font.TIMES_ROMAN, 8, Font.NORMAL, Element.ALIGN_RIGHT, 0));
                }

                registros.close();
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
    }
    
    public void tabulares(HttpServletResponse response, String [] cabTabla, float [] anchoTabla, ResultSet registros)
    {
        /* inicio PDF */     
        Rectangle orientacion = (this.vertical) ? PageSize.A4 : PageSize.A4.rotate();
        Document document = new Document(orientacion);// paso 1
        document.setMargins(0,0,50,80); /*Izquierda, derecha, tope, pie */
        try{
            PdfWriter writer = PdfWriter.getInstance(document, response.getOutputStream()); // paso 2
            writer.setPageEvent(new Reporte(this.vertical, this.logo, this.titulo, this.ruc, this.subtitulo, this.direccion, this.sucursal, this.rep_pie));
            document.open(); // paso 3
            
            /* todo el cuerpo del doc es el paso 4 */
            
            document.add(Addons.setCabecera(this.logo, this.titulo, this.ruc, this.subtitulo, this.direccion, this.sucursal));
            
            document.add(Addons.setCabeceraTabla(cabTabla, anchoTabla));
            
            ResultSetMetaData mdata = registros.getMetaData();      
            int numCols = mdata.getColumnCount();
            PdfPTable tbl_det = new PdfPTable(anchoTabla);
            int num = 1;
            int num2 = 1;
            String aux = "";
            String columna1 = "";
            float suma_parcial[] = {0,0,0,0,0,0,0,0,0,0,0};
            float suma_total[] = {0,0,0,0,0,0,0,0,0,0,0};
            float valor [] = {0,0,0,0,0,0,0,0,0,0,0};
            int pos=0;
            try{
                while(registros.next()){
                    columna1 = (registros.getString(1)!=null) ? registros.getString(1) : "";
                    pos=0;
                    if(aux.compareTo(columna1)!=0){
                        if(num>1){
                            tbl_det.addCell(Addons.setCeldaPDF("SUBTOTALES:  ", Font.TIMES_ROMAN, 9, Font.NORMAL, Element.ALIGN_RIGHT, 0, 4, 5));
                            for(int j=0; j<suma_parcial.length; j++){
                                tbl_det.addCell(Addons.setCeldaPDF(Addons.truncar(suma_parcial[j]), Font.TIMES_ROMAN, 8, Font.NORMAL, Element.ALIGN_RIGHT, 0));
                                suma_parcial[j] = 0;
                            }
                        }
                        tbl_det.addCell(Addons.setCeldaPDF(columna1, Font.TIMES_ROMAN, 8, Font.NORMAL, Element.ALIGN_LEFT, 1, 3, numCols+1)); 
                        aux = columna1;
                        num2 = 1;
                    }
                    tbl_det.addCell(Addons.setCeldaPDF(String.valueOf(num), Font.TIMES_ROMAN, 8, Font.NORMAL, Element.ALIGN_CENTER, 1));
                    tbl_det.addCell(Addons.setCeldaPDF(String.valueOf(num2), Font.TIMES_ROMAN, 8, Font.NORMAL, Element.ALIGN_CENTER, 1));
                    for(int i=2; i<=numCols; i++){
                        if(i<5){
                            tbl_det.addCell(Addons.setCeldaPDF(((registros.getString(i)!=null) ? registros.getString(i) : ""), Font.TIMES_ROMAN, 8, Font.NORMAL, Element.ALIGN_LEFT, 1));
                        }else{
                            valor[pos] = (registros.getString(i)!=null) ? registros.getFloat(i) : 0;
                            tbl_det.addCell(Addons.setCeldaPDF(String.valueOf(valor[pos]), Font.TIMES_ROMAN, 8, Font.NORMAL, Element.ALIGN_RIGHT, 1));
                            suma_parcial[pos] = Float.parseFloat(this.formatoNumero.format(suma_parcial[pos])) + valor[pos];
                            suma_total[pos] = Float.parseFloat(this.formatoNumero.format(suma_total[pos])) + valor[pos];
                            pos++;
                        }
                    }
                    
                    num++;
                    num2++;
                }
                tbl_det.addCell(Addons.setCeldaPDF("SUBTOTALES:  ", Font.TIMES_ROMAN, 9, Font.NORMAL, Element.ALIGN_RIGHT, 0, 4, 5));
                for(int j=0; j<suma_parcial.length; j++){
                    tbl_det.addCell(Addons.setCeldaPDF(Addons.truncar(suma_parcial[j]), Font.TIMES_ROMAN, 8, Font.NORMAL, Element.ALIGN_RIGHT, 0));
                }
                tbl_det.addCell(Addons.setCeldaPDF("TOTALES:  ", Font.TIMES_ROMAN, 10, Font.NORMAL, Element.ALIGN_RIGHT, 0, 5, 5));
                for(int j=0; j<suma_total.length; j++){
                    tbl_det.addCell(Addons.setCeldaPDF(Addons.truncar(suma_total[j]), Font.TIMES_ROMAN, 8, Font.NORMAL, Element.ALIGN_RIGHT, 0));
                }
                
                registros.close();
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
    }
    
    public void balanceComprobacion(HttpServletResponse response, String [] cabTabla, float [] anchoTabla, ResultSet registros)
    {
        /* inicio PDF */     
        Rectangle orientacion = (this.vertical) ? PageSize.A4 : PageSize.A4.rotate();
        Document document = new Document(orientacion);// paso 1
        document.setMargins(0,0,50,60); /*Izquierda, derecha, tope, pie */
        try{
            double sum[] = new double[4];
            
            PdfWriter writer = PdfWriter.getInstance(document, response.getOutputStream()); // paso 2
            writer.setPageEvent(new Reporte(this.vertical, this.logo, this.titulo, this.ruc, this.subtitulo, this.direccion, this.sucursal, this.rep_pie));
            
            
            document.open(); // paso 3
            
            /* todo el cuerpo del doc es el paso 4 */
            
            document.add(Addons.setCabecera(this.logo, this.titulo, this.ruc, this.subtitulo, this.direccion, this.sucursal));
            
            document.add(Addons.setCabeceraTablaBalance(cabTabla, anchoTabla));
            
            PdfPTable tbl_det = new PdfPTable(anchoTabla);
            int num = 1;
            String valor = "";
            int tipo_cuenta = 0;
            try{
                while(registros.next()){
                    tbl_det.addCell(Addons.setCeldaPDF(String.valueOf(num), Font.TIMES_ROMAN, 8, Font.NORMAL, Element.ALIGN_CENTER, 1));
                    tbl_det.addCell(Addons.setCeldaPDF(((registros.getString("codigo_cuenta")!=null) ? registros.getString("codigo_cuenta") : ""), Font.TIMES_ROMAN, 8, Font.NORMAL, Element.ALIGN_LEFT, 1));
                    tbl_det.addCell(Addons.setCeldaPDF(((registros.getString("nombre_cuenta")!=null) ? registros.getString("nombre_cuenta") : ""), Font.TIMES_ROMAN, 8, Font.NORMAL, Element.ALIGN_LEFT, 1));
                    valor = (registros.getString("sum_debe")!=null) ? registros.getString("sum_debe") : "0.00";
                    sum[0] += Double.valueOf(valor);
                    tbl_det.addCell(Addons.setCeldaPDF(((Double.valueOf(valor)>0) ? valor : ""), Font.TIMES_ROMAN, 8, Font.NORMAL, Element.ALIGN_RIGHT, 1));
                    valor = (registros.getString("sum_haber")!=null) ? registros.getString("sum_haber") : "0.00";
                    sum[1] += Double.valueOf(valor);
                    tbl_det.addCell(Addons.setCeldaPDF(((Double.valueOf(valor)>0) ? valor : ""), Font.TIMES_ROMAN, 8, Font.NORMAL, Element.ALIGN_RIGHT, 1));
                    
                    tipo_cuenta = (registros.getString("tipo_cuenta")!=null) ? registros.getInt("tipo_cuenta") : 0;
                    
                    valor = (registros.getString("saldo_deudor")!=null) ? registros.getString("saldo_deudor") : "0.00";
                    sum[2] += Double.valueOf(valor);
                    tbl_det.addCell(Addons.setCeldaPDF(((tipo_cuenta==0) ? valor : ""), Font.TIMES_ROMAN, 8, Font.NORMAL, Element.ALIGN_RIGHT, 1));
                    valor = (registros.getString("saldo_aceedor")!=null) ? registros.getString("saldo_aceedor") : "0.00";
                    sum[3] += Double.valueOf(valor);
                    tbl_det.addCell(Addons.setCeldaPDF(((tipo_cuenta==1) ? valor : ""), Font.TIMES_ROMAN, 8, Font.NORMAL, Element.ALIGN_RIGHT, 1));
                    
                    num++;
                }
                registros.close();
            }catch(Exception e){
                e.printStackTrace();
            }
            tbl_det.addCell(Addons.setCeldaPDF("TOTALES", Font.TIMES_ROMAN, 8, Font.NORMAL, Element.ALIGN_RIGHT, 1, 3, 3));
            for(int i=0; i<sum.length; i++){
                tbl_det.addCell(Addons.setCeldaPDF(Addons.truncar(Addons.redondear(sum[i])), Font.TIMES_ROMAN, 8, Font.NORMAL, Element.ALIGN_RIGHT, 1));
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
    }
    
    public void balanceGeneral(HttpServletResponse response, ResultSet registros)
    {
        String datos[][] = Addons.calcularTotalesBalance(registros, 0, 0);
        Addons.ordenamientoQuicksort(datos, 0, datos.length-1, 2);
        
        /* inicio PDF */     
        Rectangle orientacion = (this.vertical) ? PageSize.A4 : PageSize.A4.rotate();
        Document document = new Document(orientacion);// paso 1
        document.setMargins(0,0,50,60); /*Izquierda, derecha, tope, pie */
        try{
            PdfWriter writer = PdfWriter.getInstance(document, response.getOutputStream()); // paso 2
            writer.setPageEvent(new Reporte(this.vertical, this.logo, this.titulo, this.ruc, this.subtitulo, this.direccion, this.sucursal, this.rep_pie));
            
            
            document.open(); // paso 3
            
            /* todo el cuerpo del doc es el paso 4 */
            
            document.add(Addons.setCabecera(this.logo, this.titulo, this.ruc, this.subtitulo, this.direccion, this.sucursal));
            
            int columnas = Addons.maxNivelCodigos(datos)+2;
            float anchosCols[] = new float[columnas];
            anchosCols[0] = 80f;
            anchosCols[1] = 200f;
            for(int i=2; i<columnas; i++){
                anchosCols[i] = 65f;
            }
            
            PdfPTable tbl_det = new PdfPTable(anchosCols);
            
            int numCols = 0;
            int nivel = 0;
            int k=2;
            double sumPasPat = 0;
            double valor = 0;
            try{
                for(int i=1; i<datos.length; i++){
                    valor = Addons.redondear(Double.valueOf(datos[i][4]));
                    if(valor != 0){
                        nivel = Integer.parseInt(datos[i][5]);
                        int fuente = Font.NORMAL;
                        if(nivel==1){
                            fuente = Font.BOLD;
                            if(datos[i][3].toUpperCase().indexOf("PASIVO")>=0 || datos[i][3].toUpperCase().indexOf("PATRIMONIO")>=0){
                                sumPasPat += Double.valueOf(datos[i][4]);
                            }
                        }
                        tbl_det.addCell(Addons.setCeldaPDF(datos[i][2], Font.TIMES_ROMAN, 8, fuente, Element.ALIGN_LEFT, 1));
                        tbl_det.addCell(Addons.setCeldaPDF(datos[i][3], Font.TIMES_ROMAN, 8, fuente, Element.ALIGN_LEFT, 1));
                        k=2;
                        numCols = columnas - 2 - nivel;
                        for(int j=0; j<numCols; j++){
                            tbl_det.addCell(Addons.setCeldaPDF(" ", Font.TIMES_ROMAN, 8, fuente, Element.ALIGN_LEFT, 1));
                            k++;
                        }
                        tbl_det.addCell(Addons.setCeldaPDF(Addons.truncar( Addons.redondear(valor) ), Font.TIMES_ROMAN, 8, fuente, Element.ALIGN_RIGHT, 1));
                        k++;
                        for(int j=k; j<columnas; j++){
                            tbl_det.addCell(Addons.setCeldaPDF(" ", Font.TIMES_ROMAN, 8, fuente, Element.ALIGN_LEFT, 1));
                        }
                    }
                }
                registros.close();
            }catch(Exception e){
                e.printStackTrace();
            }
            
            tbl_det.addCell(Addons.setCeldaPDF("TOTAL PASIVOS MÁS PATRIMONIO ", Font.TIMES_ROMAN, 8, Font.BOLD, Element.ALIGN_LEFT, 1, 3, columnas-1));
            tbl_det.addCell(Addons.setCeldaPDF(Addons.truncar(sumPasPat), Font.TIMES_ROMAN, 8, Font.BOLD, Element.ALIGN_RIGHT, 1));
            document.add(tbl_det);
            
            document.add(new Paragraph(" "));
            document.add(new Paragraph(" "));
            document.add(new Paragraph(" "));
            
            PdfPTable tbl_firmas = new PdfPTable(2);
            tbl_firmas.addCell(Addons.setCeldaPDF("GERENTE", Font.TIMES_ROMAN, 8, Font.NORMAL, Element.ALIGN_CENTER, 0));
            tbl_firmas.addCell(Addons.setCeldaPDF("CONTADOR", Font.TIMES_ROMAN, 8, Font.NORMAL, Element.ALIGN_CENTER, 0));
            document.add(tbl_firmas);
            
        }catch(IllegalStateException ie){
            ie.printStackTrace();
        }catch(DocumentException e){
            e.printStackTrace();
        }catch(Exception e){
            e.printStackTrace();
        }
        
        document.close(); // paso 5
        /* fin PDF */        
    }
    
    public void balanceResultados(HttpServletResponse response, ResultSet registros)
    {
        String datos[][] = Addons.calcularTotalesBalance(registros, 0, 0);
        Addons.ordenamientoQuicksort(datos, 0, datos.length-1, 2);
        
        /* inicio PDF */     
        Rectangle orientacion = (this.vertical) ? PageSize.A4 : PageSize.A4.rotate();
        Document document = new Document(orientacion);// paso 1
        document.setMargins(0,0,50,60); /*Izquierda, derecha, tope, pie */
        try{
            PdfWriter writer = PdfWriter.getInstance(document, response.getOutputStream()); // paso 2
            writer.setPageEvent(new Reporte(this.vertical, this.logo, this.titulo, this.ruc, this.subtitulo, this.direccion, this.sucursal, this.rep_pie));
            
            
            document.open(); // paso 3
            
            /* todo el cuerpo del doc es el paso 4 */
            
            document.add(Addons.setCabecera(this.logo, this.titulo, this.ruc, this.subtitulo, this.direccion, this.sucursal));
            
            int columnas = Addons.maxNivelCodigos(datos)+2;
            float anchosCols[] = new float[columnas];
            anchosCols[0] = 80f;
            anchosCols[1] = 200f;
            for(int i=2; i<columnas; i++){
                anchosCols[i] = 65f;
            }
            
            PdfPTable tbl_det = new PdfPTable(anchosCols);
            
            int numCols = 0;
            int nivel = 0;
            int k=2;
            double total_ingresos = 0;
            double total_gastos = 0;
            double valor = 0;
            
            try{
                for(int i=1; i<datos.length; i++){
                    valor = Addons.redondear(Double.valueOf(datos[i][4]));
                    if(valor != 0){
                        nivel = Integer.parseInt(datos[i][5]);
                        int fuente = Font.NORMAL;
                        if(nivel==1){
                            fuente = Font.BOLD;
                            if(datos[i][3].toUpperCase().indexOf("INGRESO")>=0){
                                total_ingresos = Double.valueOf(datos[i][4]);
                            }
                            if(datos[i][3].toUpperCase().indexOf("GASTO")>=0 || datos[i][3].toUpperCase().indexOf("EGRESO")>=0){
                                total_gastos = Double.valueOf(datos[i][4]);
                            }
                        }
                        tbl_det.addCell(Addons.setCeldaPDF(datos[i][2], Font.TIMES_ROMAN, 8, fuente, Element.ALIGN_LEFT, 1));
                        tbl_det.addCell(Addons.setCeldaPDF(datos[i][3], Font.TIMES_ROMAN, 8, fuente, Element.ALIGN_LEFT, 1));
                        k=2;
                        numCols = columnas - 2 - nivel;
                        for(int j=0; j<numCols; j++){
                            tbl_det.addCell(Addons.setCeldaPDF(" ", Font.TIMES_ROMAN, 8, fuente, Element.ALIGN_LEFT, 1));
                            k++;
                        }
                        tbl_det.addCell(Addons.setCeldaPDF(Addons.truncar(valor), Font.TIMES_ROMAN, 8, fuente, Element.ALIGN_RIGHT, 1));
                        k++;
                        for(int j=k; j<columnas; j++){
                            tbl_det.addCell(Addons.setCeldaPDF(" ", Font.TIMES_ROMAN, 8, fuente, Element.ALIGN_LEFT, 1));
                        }
                    }
                }
                registros.close();
            }catch(Exception e){
                e.printStackTrace();
            }
            
            tbl_det.addCell(Addons.setCeldaPDF("RESULTADO DEL EJERCICIO ", Font.TIMES_ROMAN, 8, Font.BOLD, Element.ALIGN_LEFT, 1, 3, columnas-1));
            tbl_det.addCell(Addons.setCeldaPDF(Addons.truncar(Addons.redondear(total_ingresos-total_gastos)), Font.TIMES_ROMAN, 8, Font.BOLD, Element.ALIGN_RIGHT, 1));
            document.add(tbl_det);
            
            document.add(new Paragraph(" "));
            document.add(new Paragraph(" "));
            document.add(new Paragraph(" "));
            
            PdfPTable tbl_firmas = new PdfPTable(2);
            tbl_firmas.addCell(Addons.setCeldaPDF("GERENTE", Font.TIMES_ROMAN, 8, Font.NORMAL, Element.ALIGN_CENTER, 0));
            tbl_firmas.addCell(Addons.setCeldaPDF("CONTADOR", Font.TIMES_ROMAN, 8, Font.NORMAL, Element.ALIGN_CENTER, 0));
            document.add(tbl_firmas);
            
        }catch(IllegalStateException ie){
            ie.printStackTrace();
        }catch(DocumentException e){
            e.printStackTrace();
        }catch(Exception e){
            e.printStackTrace();
        }
        
        document.close(); // paso 5
        /* fin PDF */        
    }

    public void importaciones(HttpServletResponse response, ResultSet registros, DataBase objDB)
    {
        /* inicio PDF */
        Rectangle orientacion = (this.vertical) ? PageSize.A4 : PageSize.A4.rotate();
        Document document = new Document(orientacion);// paso 1
        document.setMargins(0,0,50,80); /*Izquierda, derecha, tope, pie */
        try{
            PdfWriter writer = PdfWriter.getInstance(document, response.getOutputStream()); // paso 2
            writer.setPageEvent(new Reporte(this.vertical, this.logo, this.titulo, this.ruc, this.subtitulo, this.direccion, this.sucursal, this.rep_pie));

            document.open(); // paso 3

            /* todo el cuerpo del doc es el paso 4 */

            document.add(Addons.setCabecera(this.logo, this.titulo, this.ruc, this.subtitulo, this.direccion, this.sucursal));

            
            try{
                String []cabTabla = new String [] {"#", "Nro. FACTURA", "RAZON SOCIAL", "F. EMISION", "SUBTOTAL", "DESCUENTO", "IVA", "TOTAL"};
                float []anchoTabla = new float[]{15f,80f,160f,60f,70f,70f,70f,70f};
                ResultSet facturas = null;
                String id_dau = "-1";
                int num = 1;
                while(registros.next()){
                    /* MAESTRO */
                    PdfPTable tbl_maestro = new PdfPTable(new float[]{35f,140f,50f,140f});
                    id_dau = (registros.getString("id_dau")!=null) ? registros.getString("id_dau") : "-1";

                    tbl_maestro.addCell(Addons.setCeldaPDF("ADUANA - CONTRIBUYENTE", Font.TIMES_ROMAN, 8, Font.BOLD, Element.ALIGN_LEFT, 1, 3, 4));

                    tbl_maestro.addCell(Addons.setCeldaPDF("Número: ", Font.TIMES_ROMAN, 8, Font.NORMAL, Element.ALIGN_LEFT, 1));
                    tbl_maestro.addCell(Addons.setCeldaPDF(((registros.getString("num_dau")!=null) ? registros.getString("num_dau") : ""), Font.TIMES_ROMAN, 8, Font.NORMAL, Element.ALIGN_LEFT, 1));
                    tbl_maestro.addCell(Addons.setCeldaPDF("Fecha/Hora TX: ", Font.TIMES_ROMAN, 8, Font.NORMAL, Element.ALIGN_LEFT, 1));
                    tbl_maestro.addCell(Addons.setCeldaPDF(((registros.getString("fecha_tx_sql")!=null) ? registros.getString("fecha_tx_sql") : "") + " - "
                            +((registros.getString("hora_tx")!=null) ? registros.getString("hora_tx") : ""), Font.TIMES_ROMAN, 8, Font.NORMAL, Element.ALIGN_LEFT, 1));

                    tbl_maestro.addCell(Addons.setCeldaPDF("Importador: ", Font.TIMES_ROMAN, 8, Font.NORMAL, Element.ALIGN_LEFT, 1));
                    tbl_maestro.addCell(Addons.setCeldaPDF(((registros.getString("importador")!=null) ? registros.getString("importador") : ""), Font.TIMES_ROMAN, 8, Font.NORMAL, Element.ALIGN_LEFT, 1));
                    tbl_maestro.addCell(Addons.setCeldaPDF("Dirección: ", Font.TIMES_ROMAN, 8, Font.NORMAL, Element.ALIGN_LEFT, 1));
                    tbl_maestro.addCell(Addons.setCeldaPDF(((registros.getString("direccion")!=null) ? registros.getString("direccion") : ""), Font.TIMES_ROMAN, 8, Font.NORMAL, Element.ALIGN_LEFT, 1));
                    document.add(tbl_maestro);


                    PdfPTable tbl_valoresAduana = new PdfPTable(6);
                    tbl_valoresAduana.addCell(Addons.setCeldaPDF("VALORES EN ADUANA", Font.TIMES_ROMAN, 8, Font.BOLD, Element.ALIGN_LEFT, 1, 3, 6));

                    tbl_valoresAduana.addCell(Addons.setCeldaPDF("Fob USD", Font.TIMES_ROMAN, 8, Font.NORMAL, Element.ALIGN_LEFT, 1));
                    tbl_valoresAduana.addCell(Addons.setCeldaPDF("Flete USD", Font.TIMES_ROMAN, 8, Font.NORMAL, Element.ALIGN_LEFT, 1));
                    tbl_valoresAduana.addCell(Addons.setCeldaPDF("Seguro USD", Font.TIMES_ROMAN, 8, Font.NORMAL, Element.ALIGN_LEFT, 1));
                    tbl_valoresAduana.addCell(Addons.setCeldaPDF("Ajuste USD", Font.TIMES_ROMAN, 8, Font.NORMAL, Element.ALIGN_LEFT, 1));
                    tbl_valoresAduana.addCell(Addons.setCeldaPDF("CIF USD", Font.TIMES_ROMAN, 8, Font.NORMAL, Element.ALIGN_LEFT, 1));
                    tbl_valoresAduana.addCell(Addons.setCeldaPDF("Valor Aduana USD", Font.TIMES_ROMAN, 8, Font.NORMAL, Element.ALIGN_LEFT, 1));

                    tbl_valoresAduana.addCell(Addons.setCeldaPDF(((registros.getString("fob")!=null) ? registros.getString("fob") : "0"), Font.TIMES_ROMAN, 8, Font.NORMAL, Element.ALIGN_LEFT, 1));
                    tbl_valoresAduana.addCell(Addons.setCeldaPDF(((registros.getString("flete")!=null) ? registros.getString("flete") : "0"), Font.TIMES_ROMAN, 8, Font.NORMAL, Element.ALIGN_LEFT, 1));
                    tbl_valoresAduana.addCell(Addons.setCeldaPDF(((registros.getString("seguro")!=null) ? registros.getString("seguro") : "0"), Font.TIMES_ROMAN, 8, Font.NORMAL, Element.ALIGN_LEFT, 1));
                    tbl_valoresAduana.addCell(Addons.setCeldaPDF(((registros.getString("ajuste")!=null) ? registros.getString("ajuste") : "0"), Font.TIMES_ROMAN, 8, Font.NORMAL, Element.ALIGN_LEFT, 1));
                    tbl_valoresAduana.addCell(Addons.setCeldaPDF(((registros.getString("cif")!=null) ? registros.getString("cif") : "0"), Font.TIMES_ROMAN, 8, Font.NORMAL, Element.ALIGN_LEFT, 1));
                    tbl_valoresAduana.addCell(Addons.setCeldaPDF(((registros.getString("valor_aduana")!=null) ? registros.getString("valor_aduana") : "0"), Font.TIMES_ROMAN, 8, Font.NORMAL, Element.ALIGN_LEFT, 1));
                    document.add(tbl_valoresAduana);

                    
                    PdfPTable tbl_autoliquidacion = new PdfPTable(2);
                    float valor_em = (registros.getString("valor_em")!=null) ? registros.getFloat("valor_em") : 0;
                    float der_especifico = (registros.getString("der_especifico")!=null) ? registros.getFloat("der_especifico") : 0;
                    float ice = (registros.getString("ice")!=null) ? registros.getFloat("ice") : 0;
                    float iva = (registros.getString("iva")!=null) ? registros.getFloat("iva") : 0;
                    float antidumping = (registros.getString("antidumping")!=null) ? registros.getFloat("antidumping") : 0;
                    float der_consular = (registros.getString("der_consular")!=null) ? registros.getFloat("der_consular") : 0;
                    float sobretiempo_petr = (registros.getString("sobretiempo_petr")!=null) ? registros.getFloat("sobretiempo_petr") : 0;
                    float ad_barril_crudo = (registros.getString("ad_barril_crudo")!=null) ? registros.getFloat("ad_barril_crudo") : 0;
                    float tasa_modernizacion = (registros.getString("tasa_modernizacion")!=null) ? registros.getFloat("tasa_modernizacion") : 0;
                    float tasa_control = (registros.getString("tasa_control")!=null) ? registros.getFloat("tasa_control") : 0;
                    float tasa_almacenaje = (registros.getString("tasa_almacenaje")!=null) ? registros.getFloat("tasa_almacenaje") : 0;
                    float multa = (registros.getString("multa")!=null) ? registros.getFloat("multa") : 0;
                    float interes = (registros.getString("interes")!=null) ? registros.getFloat("interes") : 0;
                    float imp_salida_divisa = (registros.getString("imp_salida_divisa")!=null) ? registros.getFloat("imp_salida_divisa") : 0;
                    float fodinfa = (registros.getString("fodinfa")!=null) ? registros.getFloat("fodinfa") : 0;
                    float corpei = (registros.getString("corpei")!=null) ? registros.getFloat("corpei") : 0;
                    float otros = (registros.getString("otros")!=null) ? registros.getFloat("otros") : 0;
                    float salvaguarda = (registros.getString("salvaguarda")!=null) ? registros.getFloat("salvaguarda") : 0;
                    float total_autoliquidacion = (registros.getString("total_autoliquidacion")!=null) ? registros.getFloat("total_autoliquidacion") : 0;

                    tbl_autoliquidacion.addCell(Addons.setCeldaPDF("AUTOLIQUIDACION DE TRIBUTOS", Font.TIMES_ROMAN, 8, Font.BOLD, Element.ALIGN_LEFT, 1, 3, 2));

                    tbl_autoliquidacion.addCell(Addons.setCeldaPDF("CONCEPTO", Font.TIMES_ROMAN, 8, Font.NORMAL, Element.ALIGN_CENTER, 1));
                    tbl_autoliquidacion.addCell(Addons.setCeldaPDF("CANTIDAD A PAGAR", Font.TIMES_ROMAN, 8, Font.NORMAL, Element.ALIGN_CENTER, 1));

                    if(valor_em>0){
                        tbl_autoliquidacion.addCell(Addons.setCeldaPDF("AD / VALOREM", Font.TIMES_ROMAN, 8, Font.NORMAL, Element.ALIGN_LEFT, 1));
                        tbl_autoliquidacion.addCell(Addons.setCeldaPDF(String.valueOf(valor_em), Font.TIMES_ROMAN, 8, Font.NORMAL, Element.ALIGN_RIGHT, 1));
                    }
                    if(der_especifico>0){
                        tbl_autoliquidacion.addCell(Addons.setCeldaPDF("DER. ESPECIFICO", Font.TIMES_ROMAN, 8, Font.NORMAL, Element.ALIGN_LEFT, 1));
                        tbl_autoliquidacion.addCell(Addons.setCeldaPDF(String.valueOf(der_especifico), Font.TIMES_ROMAN, 8, Font.NORMAL, Element.ALIGN_RIGHT, 1));
                    }
                    if(ice>0){
                        tbl_autoliquidacion.addCell(Addons.setCeldaPDF("IMP. CONSUMO ESPECIAL", Font.TIMES_ROMAN, 8, Font.NORMAL, Element.ALIGN_LEFT, 1));
                        tbl_autoliquidacion.addCell(Addons.setCeldaPDF(String.valueOf(ice), Font.TIMES_ROMAN, 8, Font.NORMAL, Element.ALIGN_RIGHT, 1));
                    }
                    if(iva>0){
                        tbl_autoliquidacion.addCell(Addons.setCeldaPDF("IMP. VALOR AGREGADO", Font.TIMES_ROMAN, 8, Font.NORMAL, Element.ALIGN_LEFT, 1));
                        tbl_autoliquidacion.addCell(Addons.setCeldaPDF(String.valueOf(iva), Font.TIMES_ROMAN, 8, Font.NORMAL, Element.ALIGN_RIGHT, 1));
                    }
                    if(antidumping>0){
                        tbl_autoliquidacion.addCell(Addons.setCeldaPDF("DERECHO ANTIDUMPING", Font.TIMES_ROMAN, 8, Font.NORMAL, Element.ALIGN_LEFT, 1));
                        tbl_autoliquidacion.addCell(Addons.setCeldaPDF(String.valueOf(antidumping), Font.TIMES_ROMAN, 8, Font.NORMAL, Element.ALIGN_RIGHT, 1));
                    }
                    if(der_consular>0){
                        tbl_autoliquidacion.addCell(Addons.setCeldaPDF("DERECHOS CONSULARES", Font.TIMES_ROMAN, 8, Font.NORMAL, Element.ALIGN_LEFT, 1));
                        tbl_autoliquidacion.addCell(Addons.setCeldaPDF(String.valueOf(der_consular), Font.TIMES_ROMAN, 8, Font.NORMAL, Element.ALIGN_RIGHT, 1));
                    }
                    if(sobretiempo_petr>0){
                        tbl_autoliquidacion.addCell(Addons.setCeldaPDF("SOBRETIEMPO PETROLEO", Font.TIMES_ROMAN, 8, Font.NORMAL, Element.ALIGN_LEFT, 1));
                        tbl_autoliquidacion.addCell(Addons.setCeldaPDF(String.valueOf(sobretiempo_petr), Font.TIMES_ROMAN, 8, Font.NORMAL, Element.ALIGN_RIGHT, 1));
                    }
                    if(ad_barril_crudo>0){
                        tbl_autoliquidacion.addCell(Addons.setCeldaPDF("ADICIONAL POR BARRIL DE CRUDO", Font.TIMES_ROMAN, 8, Font.NORMAL, Element.ALIGN_LEFT, 1));
                        tbl_autoliquidacion.addCell(Addons.setCeldaPDF(String.valueOf(ad_barril_crudo), Font.TIMES_ROMAN, 8, Font.NORMAL, Element.ALIGN_RIGHT, 1));
                    }
                    if(tasa_modernizacion>0){
                        tbl_autoliquidacion.addCell(Addons.setCeldaPDF("TASA MODERNIZACION", Font.TIMES_ROMAN, 8, Font.NORMAL, Element.ALIGN_LEFT, 1));
                        tbl_autoliquidacion.addCell(Addons.setCeldaPDF(String.valueOf(tasa_modernizacion), Font.TIMES_ROMAN, 8, Font.NORMAL, Element.ALIGN_RIGHT, 1));
                    }
                    if(tasa_control>0){
                        tbl_autoliquidacion.addCell(Addons.setCeldaPDF("TASA DE CONTROL", Font.TIMES_ROMAN, 8, Font.NORMAL, Element.ALIGN_LEFT, 1));
                        tbl_autoliquidacion.addCell(Addons.setCeldaPDF(String.valueOf(tasa_control), Font.TIMES_ROMAN, 8, Font.NORMAL, Element.ALIGN_RIGHT, 1));
                    }
                    if(tasa_almacenaje>0){
                        tbl_autoliquidacion.addCell(Addons.setCeldaPDF("TASA ALMACENAJE", Font.TIMES_ROMAN, 8, Font.NORMAL, Element.ALIGN_LEFT, 1));
                        tbl_autoliquidacion.addCell(Addons.setCeldaPDF(String.valueOf(tasa_almacenaje), Font.TIMES_ROMAN, 8, Font.NORMAL, Element.ALIGN_RIGHT, 1));
                    }
                    if(multa>0){
                        tbl_autoliquidacion.addCell(Addons.setCeldaPDF("MULTAS (ART. 89 Y 91)", Font.TIMES_ROMAN, 8, Font.NORMAL, Element.ALIGN_LEFT, 1));
                        tbl_autoliquidacion.addCell(Addons.setCeldaPDF(String.valueOf(multa), Font.TIMES_ROMAN, 8, Font.NORMAL, Element.ALIGN_RIGHT, 1));
                    }
                    if(interes>0){
                        tbl_autoliquidacion.addCell(Addons.setCeldaPDF("INTERESES", Font.TIMES_ROMAN, 8, Font.NORMAL, Element.ALIGN_LEFT, 1));
                        tbl_autoliquidacion.addCell(Addons.setCeldaPDF(String.valueOf(interes), Font.TIMES_ROMAN, 8, Font.NORMAL, Element.ALIGN_RIGHT, 1));
                    }
                    if(imp_salida_divisa>0){
                        tbl_autoliquidacion.addCell(Addons.setCeldaPDF("IMP. A LA SALIDA DE DIVISAS", Font.TIMES_ROMAN, 8, Font.NORMAL, Element.ALIGN_LEFT, 1));
                        tbl_autoliquidacion.addCell(Addons.setCeldaPDF(String.valueOf(imp_salida_divisa), Font.TIMES_ROMAN, 8, Font.NORMAL, Element.ALIGN_RIGHT, 1));
                    }
                    if(fodinfa>0){
                        tbl_autoliquidacion.addCell(Addons.setCeldaPDF("FODINFA", Font.TIMES_ROMAN, 8, Font.NORMAL, Element.ALIGN_LEFT, 1));
                        tbl_autoliquidacion.addCell(Addons.setCeldaPDF(String.valueOf(fodinfa), Font.TIMES_ROMAN, 8, Font.NORMAL, Element.ALIGN_RIGHT, 1));
                    }
                    if(corpei>0){
                        tbl_autoliquidacion.addCell(Addons.setCeldaPDF("CORPEI", Font.TIMES_ROMAN, 8, Font.NORMAL, Element.ALIGN_LEFT, 1));
                        tbl_autoliquidacion.addCell(Addons.setCeldaPDF(String.valueOf(corpei), Font.TIMES_ROMAN, 8, Font.NORMAL, Element.ALIGN_RIGHT, 1));
                    }
                    if(otros>0){
                        tbl_autoliquidacion.addCell(Addons.setCeldaPDF("OTROS", Font.TIMES_ROMAN, 8, Font.NORMAL, Element.ALIGN_LEFT, 1));
                        tbl_autoliquidacion.addCell(Addons.setCeldaPDF(String.valueOf(otros), Font.TIMES_ROMAN, 8, Font.NORMAL, Element.ALIGN_RIGHT, 1));
                    }
                    if(salvaguarda>0){
                        tbl_autoliquidacion.addCell(Addons.setCeldaPDF("SALVAGUARDA", Font.TIMES_ROMAN, 8, Font.NORMAL, Element.ALIGN_LEFT, 1));
                        tbl_autoliquidacion.addCell(Addons.setCeldaPDF(String.valueOf(salvaguarda), Font.TIMES_ROMAN, 8, Font.NORMAL, Element.ALIGN_RIGHT, 1));
                    }
                    tbl_autoliquidacion.addCell(Addons.setCeldaPDF("TOTAL AUTOLIQUIDACION", Font.TIMES_ROMAN, 8, Font.BOLD, Element.ALIGN_RIGHT, 1));
                    tbl_autoliquidacion.addCell(Addons.setCeldaPDF(String.valueOf(total_autoliquidacion), Font.TIMES_ROMAN, 8, Font.NORMAL, Element.ALIGN_RIGHT, 1));
                    document.add(tbl_autoliquidacion);

                    
                    /* DETALLE */
                    PdfPTable tbl_fact = new PdfPTable(1);
                    tbl_fact.addCell(Addons.setCeldaPDF(" ", Font.TIMES_ROMAN, 8, Font.BOLD, Element.ALIGN_LEFT, 0));
                    tbl_fact.addCell(Addons.setCeldaPDF("FACTURAS ADJUNTAS", Font.TIMES_ROMAN, 8, Font.BOLD, Element.ALIGN_LEFT, 0));
                    document.add(tbl_fact);
                    document.add(Addons.setCabeceraTabla(cabTabla, anchoTabla));
                    PdfPTable tbl_det = new PdfPTable(anchoTabla);
                    num = 1;
                    try{
                        facturas = objDB.consulta("select F.numero_factura, F.razon_social, toDateSQL(F.fecha_compra), F.subtotal, F.descuento, F.iva_12, F.total_compra "
                                + "from vta_factura_compra as F inner join tbl_factura_importacion as FI on F.id_factura_compra=FI.id_factura_compra "
                                + "where FI.id_dau="+id_dau);
                        while(facturas.next()){
                            tbl_det.addCell(Addons.setCeldaPDF(String.valueOf(num), Font.TIMES_ROMAN, 8, Font.NORMAL, Element.ALIGN_CENTER, 1));
                            for(int i=1; i<=7; i++){
                                tbl_det.addCell(Addons.setCeldaPDF(((facturas.getString(i)!=null) ? facturas.getString(i) : ""), Font.TIMES_ROMAN, 8, Font.NORMAL, (i<=3?Element.ALIGN_LEFT:Element.ALIGN_RIGHT), 1));
                            }
                            num++;
                        }
                        registros.close();
                    }catch(Exception e){
                        e.printStackTrace();
                    }
                    document.add(tbl_det);

                    
                    document.add(new Paragraph(" "));

                }

                facturas.close();
                registros.close();
            }catch(Exception e){
                e.printStackTrace();
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
    }

    public void RPLiquidacionIndividual(HttpServletResponse response, ResultSet rsRolPago, DataBase objDB, Sucursal objSucursal)
    {
        /* inicio PDF */
        Rectangle orientacion = (this.vertical) ? PageSize.A4 : PageSize.A4.rotate();
        Document document = new Document(orientacion);// paso 1
        document.setMargins(0,0,50,80); /*Izquierda, derecha, tope, pie */
        try{
            PdfWriter writer = PdfWriter.getInstance(document, response.getOutputStream()); // paso 2
            writer.setPageEvent(new Reporte(this.vertical, this.logo, this.titulo, this.ruc, this.subtitulo, this.direccion, this.sucursal, this.rep_pie));

            document.open(); // paso 3

            /* todo el cuerpo del doc es el paso 4 */


            String id_sucursal = "";
            String dni = "";
            String id_empleado = "";
            String empleado = "";
            String periodo = "";
            String departamento = "";
            String rol = "";
            String dias_laborados = "";
            String num_horas_50 = "";
            String num_horas_100 = "";
            String sbu = "";
            String ing_total_50 = "";
            String ing_total_100 = "";
            String total_ingreso = "";
            String egr_iess = "";
            String total_egreso = "";
            String total_pagar = "";
            try{
                boolean ban = false;
                while(rsRolPago.next()){
                    if(ban){
                        document.newPage();
                    }
                    ban = true;

                    id_sucursal = (rsRolPago.getString("id_sucursal")!=null) ? rsRolPago.getString("id_sucursal") : "";
                    dni = (rsRolPago.getString("dni")!=null) ? rsRolPago.getString("dni") : "";
                    id_empleado = (rsRolPago.getString("id_empleado")!=null) ? rsRolPago.getString("id_empleado") : "";
                    empleado = (rsRolPago.getString("empleado")!=null) ? rsRolPago.getString("empleado") : "";
                    periodo = (rsRolPago.getString("periodo_sql")!=null) ? rsRolPago.getString("periodo_sql") : "";
                    departamento = (rsRolPago.getString("departamento")!=null) ? Fecha.ISOaSQL(rsRolPago.getString("departamento")) : "";
                    rol = (rsRolPago.getString("rol")!=null) ? rsRolPago.getString("rol") : "";
                    dias_laborados = (rsRolPago.getString("dias_laborados")!=null) ? rsRolPago.getString("dias_laborados") : "";
                    num_horas_50 = (rsRolPago.getString("num_horas_50")!=null) ? rsRolPago.getString("num_horas_50") : "";
                    num_horas_100 = (rsRolPago.getString("num_horas_100")!=null) ? rsRolPago.getString("num_horas_100") : "";
                    sbu = (rsRolPago.getString("sbu")!=null) ? rsRolPago.getString("sbu") : "";
                    ing_total_50 = (rsRolPago.getString("ing_total_50")!=null) ? rsRolPago.getString("ing_total_50") : "";
                    ing_total_100 = (rsRolPago.getString("ing_total_100")!=null) ? rsRolPago.getString("ing_total_100") : "";
                    total_ingreso = (rsRolPago.getString("total_ingreso")!=null) ? rsRolPago.getString("total_ingreso") : "";
                    egr_iess = (rsRolPago.getString("egr_iess")!=null) ? rsRolPago.getString("egr_iess") : "";
                    total_egreso = (rsRolPago.getString("total_egreso")!=null) ? rsRolPago.getString("total_egreso") : "";
                    total_pagar = (rsRolPago.getString("total_pagar")!=null) ? rsRolPago.getString("total_pagar") : "";
                    int anio = Fecha.datePart("anio", periodo);
                    int mes = Fecha.datePart("mes", periodo);
                    String fecha_ini = anio + "-" + mes + "-01";
                    String fecha_fin = anio + "-" + mes + "-" + Fecha.getUltimoDiaMes(anio, mes);


                    /* MAESTRO */
                    PdfPTable tbl_maestro = new PdfPTable(3);

                    tbl_maestro.addCell(Addons.setCeldaPDF(this.titulo, Font.TIMES_ROMAN, 10, Font.BOLD, Element.ALIGN_CENTER, 0, 5, 3));

                    tbl_maestro.addCell(Addons.setCeldaPDF("Rol de Pago: Liquidación individual", Font.TIMES_ROMAN, 8, Font.NORMAL, Element.ALIGN_LEFT, 0, 3, 3));

                    tbl_maestro.addCell(Addons.setCeldaPDF("Nombres: " + empleado, Font.TIMES_ROMAN, 8, Font.NORMAL, Element.ALIGN_LEFT, 0, 3, 2));
                    tbl_maestro.addCell(Addons.setCeldaPDF("Período: " + periodo, Font.TIMES_ROMAN, 8, Font.NORMAL, Element.ALIGN_LEFT, 0));

                    tbl_maestro.addCell(Addons.setCeldaPDF("Sucursal: " + objSucursal.getNombre(id_sucursal), Font.TIMES_ROMAN, 8, Font.NORMAL, Element.ALIGN_LEFT, 0));
                    tbl_maestro.addCell(Addons.setCeldaPDF("Departamento: " + departamento, Font.TIMES_ROMAN, 8, Font.NORMAL, Element.ALIGN_LEFT, 0));
                    tbl_maestro.addCell(Addons.setCeldaPDF("Cargo: " + rol, Font.TIMES_ROMAN, 8, Font.NORMAL, Element.ALIGN_LEFT, 0));

                    tbl_maestro.addCell(Addons.setCeldaPDF("Tiempo laborado: " + dias_laborados, Font.TIMES_ROMAN, 8, Font.NORMAL, Element.ALIGN_LEFT, 0));
                    tbl_maestro.addCell(Addons.setCeldaPDF("H.E. 50%: " + num_horas_50, Font.TIMES_ROMAN, 8, Font.NORMAL, Element.ALIGN_LEFT, 0));
                    tbl_maestro.addCell(Addons.setCeldaPDF("H.E. 100%: " + num_horas_100, Font.TIMES_ROMAN, 8, Font.NORMAL, Element.ALIGN_LEFT, 0));

                    tbl_maestro.addCell(Addons.setCeldaPDF(" ", Font.TIMES_ROMAN, 6, Font.NORMAL, Element.ALIGN_LEFT, 0, 1, 3));
                    document.add(tbl_maestro);


                    /* DETALLE */

                        /* ingresos  */
                    PdfPTable tbl_ingresos = new PdfPTable(new float[]{3,1});
                    tbl_ingresos.addCell(Addons.setCeldaPDF("INGRESOS", Font.TIMES_ROMAN, 8, Font.BOLD, Element.ALIGN_CENTER, 1));
                    tbl_ingresos.addCell(Addons.setCeldaPDF("VALOR", Font.TIMES_ROMAN, 8, Font.BOLD, Element.ALIGN_CENTER, 1));

                    tbl_ingresos.addCell(Addons.setCeldaPDF("Sueldo básico unificado", Font.TIMES_ROMAN, 8, Font.NORMAL, Element.ALIGN_LEFT, 1));
                    tbl_ingresos.addCell(Addons.setCeldaPDF("+" + sbu, Font.TIMES_ROMAN, 8, Font.NORMAL, Element.ALIGN_RIGHT, 1));

                    tbl_ingresos.addCell(Addons.setCeldaPDF("Horas extras 50%", Font.TIMES_ROMAN, 8, Font.NORMAL, Element.ALIGN_LEFT, 1));
                    tbl_ingresos.addCell(Addons.setCeldaPDF("+" + ing_total_50, Font.TIMES_ROMAN, 8, Font.NORMAL, Element.ALIGN_RIGHT, 1));

                    tbl_ingresos.addCell(Addons.setCeldaPDF("Horas extras 100%", Font.TIMES_ROMAN, 8, Font.NORMAL, Element.ALIGN_LEFT, 1));
                    tbl_ingresos.addCell(Addons.setCeldaPDF("+" + ing_total_100, Font.TIMES_ROMAN, 8, Font.NORMAL, Element.ALIGN_RIGHT, 1));
                    try{
                        ResultSet rsIngresosEmpleado = objDB.consulta("SELECT * FROM vta_rol_pago_rubro_empleado where tipo=true and anulado=false and id_empleado="+id_empleado+
                        " and periodo between '"+fecha_ini+"' and '"+fecha_fin+"';");
                        while(rsIngresosEmpleado.next()){
                            tbl_ingresos.addCell(Addons.setCeldaPDF(((rsIngresosEmpleado.getString("rubro")!=null) ? rsIngresosEmpleado.getString("rubro") : ""), Font.TIMES_ROMAN, 8, Font.NORMAL, Element.ALIGN_LEFT, 1));
                            tbl_ingresos.addCell(Addons.setCeldaPDF("+" + ((rsIngresosEmpleado.getString("valor")!=null) ? rsIngresosEmpleado.getString("valor") : "0.00"), Font.TIMES_ROMAN, 8, Font.NORMAL, Element.ALIGN_RIGHT, 1));
                        }
                        rsIngresosEmpleado.close();
                    }catch(Exception e){
                        e.printStackTrace();
                    }


                        /* egresos  */
                    PdfPTable tbl_egresos = new PdfPTable(new float[]{3,1});
                    tbl_egresos.addCell(Addons.setCeldaPDF("EGRESOS", Font.TIMES_ROMAN, 8, Font.BOLD, Element.ALIGN_CENTER, 1));
                    tbl_egresos.addCell(Addons.setCeldaPDF("VALOR", Font.TIMES_ROMAN, 8, Font.BOLD, Element.ALIGN_CENTER, 1));

                    tbl_egresos.addCell(Addons.setCeldaPDF("Aporte IESS", Font.TIMES_ROMAN, 8, Font.NORMAL, Element.ALIGN_LEFT, 1));
                    tbl_egresos.addCell(Addons.setCeldaPDF("-" + egr_iess, Font.TIMES_ROMAN, 8, Font.NORMAL, Element.ALIGN_RIGHT, 1));
                    try{
                        ResultSet rsIngresosEmpleado = objDB.consulta("SELECT * FROM vta_rol_pago_rubro_empleado where tipo=false and anulado=false and id_empleado="+id_empleado+
                        " and periodo between '"+fecha_ini+"' and '"+fecha_fin+"';");
                        while(rsIngresosEmpleado.next()){
                            tbl_egresos.addCell(Addons.setCeldaPDF(((rsIngresosEmpleado.getString("rubro")!=null) ? rsIngresosEmpleado.getString("rubro") : ""), Font.TIMES_ROMAN, 8, Font.NORMAL, Element.ALIGN_LEFT, 1));
                            tbl_egresos.addCell(Addons.setCeldaPDF("-" + ((rsIngresosEmpleado.getString("valor")!=null) ? rsIngresosEmpleado.getString("valor") : "0.00"), Font.TIMES_ROMAN, 8, Font.NORMAL, Element.ALIGN_RIGHT, 1));
                        }
                        rsIngresosEmpleado.close();
                    }catch(Exception e){
                        e.printStackTrace();
                    }
                    

                    
                    PdfPTable tbl_totales = new PdfPTable(new float[]{3,1,3,1});
                    tbl_totales.addCell(Addons.setCeldaPDF("TOTAL INGRESOS", Font.TIMES_ROMAN, 8, Font.NORMAL, Element.ALIGN_LEFT, 0));
                    tbl_totales.addCell(Addons.setCeldaPDF("$ " + total_ingreso, Font.TIMES_ROMAN, 8, Font.NORMAL, Element.ALIGN_RIGHT, 0));
                    tbl_totales.addCell(Addons.setCeldaPDF("TOTAL EGRESOS", Font.TIMES_ROMAN, 8, Font.NORMAL, Element.ALIGN_LEFT, 0));
                    tbl_totales.addCell(Addons.setCeldaPDF("$ " + total_egreso, Font.TIMES_ROMAN, 8, Font.NORMAL, Element.ALIGN_RIGHT, 0));

                    tbl_totales.addCell(Addons.setCeldaPDF("VALOR A RECIBIR:     $ " + total_pagar, Font.TIMES_ROMAN, 8, Font.NORMAL, Element.ALIGN_LEFT, 0, 3, 4));
                    

                    PdfPTable tbl_det = new PdfPTable(2);
                    tbl_det.addCell(Addons.setCeldaPDF(tbl_ingresos, Element.ALIGN_LEFT, 0));
                    tbl_det.addCell(Addons.setCeldaPDF(tbl_egresos, Element.ALIGN_LEFT, 0));
                    document.add(tbl_det);



                    document.add(tbl_totales);



                    document.add(new Paragraph(" "));
                    document.add(new Paragraph(" "));

                    PdfPTable tbl_firma = new PdfPTable(1);
                    tbl_firma.addCell(Addons.setCeldaPDF("______________________", Font.TIMES_ROMAN, 8, Font.NORMAL, Element.ALIGN_LEFT, 0));
                    tbl_firma.addCell(Addons.setCeldaPDF("Recibí conforme", Font.TIMES_ROMAN, 8, Font.NORMAL, Element.ALIGN_LEFT, 0));
                    tbl_firma.addCell(Addons.setCeldaPDF("C.C.  " + dni, Font.TIMES_ROMAN, 8, Font.NORMAL, Element.ALIGN_LEFT, 0));
                    document.add(tbl_firma);
                }

            }catch(Exception e){
                e.printStackTrace();
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
    }

    public void RPLiquidacionResumen(HttpServletResponse response, String [] cabTabla, float [] anchoTabla, ResultSet registros, int sumarDesde)
    {
        /* inicio PDF */
        Rectangle orientacion = (this.vertical) ? PageSize.A4 : PageSize.A4.rotate();
        Document document = new Document(orientacion);// paso 1
        document.setMargins(0,0,50,80); /*Izquierda, derecha, tope, pie */
        try{
            PdfWriter writer = PdfWriter.getInstance(document, response.getOutputStream()); // paso 2
            writer.setPageEvent(new Reporte(this.vertical, this.logo, this.titulo, this.ruc, this.subtitulo, this.direccion, this.sucursal, this.rep_pie));
            document.open(); // paso 3

            /* todo el cuerpo del doc es el paso 4 */

            document.add(Addons.setCabecera(this.logo, this.titulo, this.ruc, this.subtitulo, this.direccion, this.sucursal));

            document.add(Addons.setCabeceraTabla(cabTabla, anchoTabla));

            ResultSetMetaData mdata = registros.getMetaData();
            int numCols = mdata.getColumnCount();
            PdfPTable tbl_det = new PdfPTable(anchoTabla);
            int num = 1;
            int num2 = 1;
            String aux = "";
            String columna1 = "";
            int num_sumas = cabTabla.length - sumarDesde;
            float suma_parcial[] = new float[num_sumas];
            float suma_total[] = new float[num_sumas];
            float valor [] = new float[num_sumas];
            int pos=0;
            try{
                while(registros.next()){
                    columna1 = (registros.getString(1)!=null) ? registros.getString(1) : "";
                    pos=0;
                    if(aux.compareTo(columna1)!=0){
                        if(num>1){
                            tbl_det.addCell(Addons.setCeldaPDF("SUBTOTALES:  ", Font.TIMES_ROMAN, 8, Font.NORMAL, Element.ALIGN_RIGHT, 0, 4, sumarDesde));
                            for(int j=0; j<suma_parcial.length-1; j++){
                                tbl_det.addCell(Addons.setCeldaPDF(Addons.truncar(suma_parcial[j]), Font.TIMES_ROMAN, 8, Font.NORMAL, Element.ALIGN_RIGHT, 0));
                                suma_parcial[j] = 0;
                            }
                            tbl_det.addCell(Addons.setCeldaPDF(" ", Font.TIMES_ROMAN, 8, Font.NORMAL, Element.ALIGN_RIGHT, 0));
                        }
                        tbl_det.addCell(Addons.setCeldaPDF(columna1, Font.TIMES_ROMAN, 8, Font.NORMAL, Element.ALIGN_LEFT, 1, 3, numCols+2));
                        aux = columna1;
                        num2 = 1;
                    }
                    tbl_det.addCell(Addons.setCeldaPDF(String.valueOf(num), Font.TIMES_ROMAN, 8, Font.NORMAL, Element.ALIGN_CENTER, 1));
                    tbl_det.addCell(Addons.setCeldaPDF(String.valueOf(num2), Font.TIMES_ROMAN, 8, Font.NORMAL, Element.ALIGN_CENTER, 1));
                    for(int i=2; i<=numCols; i++){
                        if(i<sumarDesde){
                            tbl_det.addCell(Addons.setCeldaPDF(((registros.getString(i)!=null) ? registros.getString(i) : ""), Font.TIMES_ROMAN, 8, Font.NORMAL, Element.ALIGN_LEFT, 1));
                        }else{
                            valor[pos] = (registros.getString(i)!=null) ? registros.getFloat(i) : 0;
                            tbl_det.addCell(Addons.setCeldaPDF(String.valueOf(valor[pos]), Font.TIMES_ROMAN, 8, Font.NORMAL, Element.ALIGN_RIGHT, 1));
                            suma_parcial[pos] = Float.parseFloat(this.formatoNumero.format(suma_parcial[pos])) + valor[pos];
                            suma_total[pos] = Float.parseFloat(this.formatoNumero.format(suma_total[pos])) + valor[pos];
                            pos++;
                        }
                    }
                    tbl_det.addCell(Addons.setCeldaPDF(" ", Font.TIMES_ROMAN, 9, Font.NORMAL, Element.ALIGN_RIGHT, 1));

                    num++;
                    num2++;
                }
                tbl_det.addCell(Addons.setCeldaPDF("SUBTOTALES:  ", Font.TIMES_ROMAN, 8, Font.NORMAL, Element.ALIGN_RIGHT, 0, 4, sumarDesde));
                for(int j=0; j<suma_parcial.length-1; j++){
                    tbl_det.addCell(Addons.setCeldaPDF(Addons.truncar(suma_parcial[j]), Font.TIMES_ROMAN, 8, Font.NORMAL, Element.ALIGN_RIGHT, 0));
                }
                tbl_det.addCell(Addons.setCeldaPDF(" ", Font.TIMES_ROMAN, 8, Font.NORMAL, Element.ALIGN_RIGHT, 0));
                
                tbl_det.addCell(Addons.setCeldaPDF("TOTALES:  ", Font.TIMES_ROMAN, 8, Font.NORMAL, Element.ALIGN_RIGHT, 0, 4, sumarDesde));
                for(int j=0; j<suma_total.length-1; j++){
                    tbl_det.addCell(Addons.setCeldaPDF(Addons.truncar(suma_total[j]), Font.TIMES_ROMAN, 8, Font.NORMAL, Element.ALIGN_RIGHT, 0));
                }
                tbl_det.addCell(Addons.setCeldaPDF(" ", Font.TIMES_ROMAN, 8, Font.NORMAL, Element.ALIGN_RIGHT, 0));

                registros.close();
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
    }

    public void correos(HttpServletResponse response, ResultSet registros)
    {
        /* inicio PDF */
        Rectangle orientacion = (this.vertical) ? PageSize.A4 : PageSize.A4.rotate();
        Document document = new Document(orientacion);// paso 1
        document.setMargins(0,0,50,80); /*Izquierda, derecha, tope, pie */
        try{
            PdfWriter writer = PdfWriter.getInstance(document, response.getOutputStream()); // paso 2
            writer.setPageEvent(new Reporte(this.vertical, this.logo, this.titulo, this.ruc, this.subtitulo, this.direccion, this.sucursal, this.rep_pie));
            document.open(); // paso 3

            /* todo el cuerpo del doc es el paso 4 */

            document.add(Addons.setCabecera(this.logo, this.titulo, this.ruc, this.subtitulo, this.direccion, this.sucursal));


            PdfPTable tbl_det = new PdfPTable(new float[]{20,80});
            String aux = "";
            String columna1 = "";
            String columna2 = "";
            boolean uno = true;
            try{
                tbl_det.addCell(Addons.setCeldaPDF("SECTOR", Font.TIMES_ROMAN, 8, Font.BOLD, Element.ALIGN_CENTER, 1));
                tbl_det.addCell(Addons.setCeldaPDF("CORREOS ELECTRONICOS", Font.TIMES_ROMAN, 8, Font.BOLD, Element.ALIGN_CENTER, 1));
                while(registros.next()){
                    columna1 = (registros.getString(1)!=null) ? registros.getString(1) : "";
                    columna2 += ((registros.getString(2)!=null) ? registros.getString(2) : "") + ", ";
                    if(aux.compareTo(columna1)!=0 && !uno){
                       tbl_det.addCell(Addons.setCeldaPDF(columna1, Font.TIMES_ROMAN, 8, Font.NORMAL, Element.ALIGN_LEFT, 1));
                       tbl_det.addCell(Addons.setCeldaPDF(columna2.substring(0, columna2.length()-2), Font.TIMES_ROMAN, 8, Font.NORMAL, Element.ALIGN_LEFT, 1));
                       aux = columna1;
                       columna2 = "";
                    }
                    uno = false;
                }
                registros.close();
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
    }

    
    public void activosPersonalizadosClientes(HttpServletResponse response, String [] cabTabla, float [] anchoTabla, ResultSet registros, DataBase objDB)
    {
        /* inicio PDF */
        Rectangle orientacion = (this.vertical) ? PageSize.A4 : PageSize.A4.rotate();
        Document document = new Document(orientacion);// paso 1
        document.setMargins(0,0,50,80); /*Izquierda, derecha, tope, pie */
        try{
            PdfWriter writer = PdfWriter.getInstance(document, response.getOutputStream()); // paso 2
            writer.setPageEvent(new Reporte(this.vertical, this.logo, this.titulo, this.ruc, this.subtitulo, this.direccion, this.sucursal, this.rep_pie));


            document.open(); // paso 3

            /* todo el cuerpo del doc es el paso 4 */

            document.add(Addons.setCabecera(this.logo, this.titulo, this.ruc, this.subtitulo, this.direccion, this.sucursal));

            document.add(Addons.setCabeceraTabla(cabTabla, anchoTabla));

            PdfPTable tbl_det = new PdfPTable(anchoTabla);
            try{
                while(registros.next()){
                    String persona_recibe="";
                    String num_personalizacion = (registros.getString("num_personalizacion")!=null) ? registros.getString("num_personalizacion") : "";
                    try{
                        ResultSet rs = objDB.consulta("select * from tbl_activo_personalizacion where num_documento="+num_personalizacion);
                        if(rs.next()){
                            persona_recibe = (rs.getString("persona_recibe")!=null) ? rs.getString("persona_recibe") : "";
                            rs.close();
                        }
                    }catch(Exception e){
                        e.printStackTrace();
                    }
                    tbl_det.addCell(Addons.setCeldaPDF(String.valueOf(persona_recibe), Font.TIMES_ROMAN, 8, Font.NORMAL, Element.ALIGN_LEFT, 1));
                    
                    String mac = (registros.getString("codigo_activo")!=null) ? registros.getString("codigo_activo") : "";
                    tbl_det.addCell(Addons.setCeldaPDF(String.valueOf(mac), Font.TIMES_ROMAN, 8, Font.NORMAL, Element.ALIGN_LEFT, 1));
                }

                registros.close();
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
    }
    
    
    public void onStartPage(PdfWriter writer, Document document)
    {
        Addons.setEncabezado(writer, document, this.subtitulo);
    }
    
    public void onEndPage(PdfWriter writer, Document document)
    {
        Addons.setPie(writer, document, this.rep_pie);
    }
}
