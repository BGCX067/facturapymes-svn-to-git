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

import java.sql.ResultSet;
import java.util.*;
import jm.adm.clas.Sucursal;
import jm.web.Addons;
import jm.web.DataBase;
import jm.web.DatosDinamicos;
import jm.web.Fecha;

/**
 *
 * @author Jorge
 */
public class ReporteXls {
    StringBuilder xls = null;
    int columnas = 1;
    int filas = 1;
    public void setEncabezado()
    {
        xls = new StringBuilder();
        this.xls.append("<?xml version=\"1.0\" encoding=\"iso-8859-1\"?>\n");
        this.xls.append("<?mso-application progid=\"Excel.Sheet\"?>\n");
        this.xls.append("<Workbook xmlns=\"urn:schemas-microsoft-com:office:spreadsheet\"\n");
        this.xls.append(" xmlns:o=\"urn:schemas-microsoft-com:office:office\"\n");
        this.xls.append(" xmlns:x=\"urn:schemas-microsoft-com:office:excel\"\n");
        this.xls.append(" xmlns:ss=\"urn:schemas-microsoft-com:office:spreadsheet\"\n");
        this.xls.append(" xmlns:html=\"http://www.w3.org/TR/REC-html40\">\n");
        this.xls.append(" <DocumentProperties xmlns=\"urn:schemas-microsoft-com:office:office\">\n");
        this.xls.append("  <Author>FACTURAPYMES</Author>\n");
        this.xls.append("  <LastAuthor>FACTURAPYMES</LastAuthor>\n");
        this.xls.append("  <Created>2010-03-05T18:37:44Z</Created>\n");
        this.xls.append("  <LastSaved>2010-03-05T18:40:43Z</LastSaved>\n");
        this.xls.append("  <Company>FACTURAPYMES</Company>\n");
        this.xls.append("  <Version>3.00</Version>\n");
        this.xls.append(" </DocumentProperties>\n");
        this.xls.append(" <ExcelWorkbook xmlns=\"urn:schemas-microsoft-com:office:excel\">\n");
        this.xls.append("  <WindowHeight>10200</WindowHeight>\n");
        this.xls.append("  <WindowWidth>16455</WindowWidth>\n");
        this.xls.append("  <WindowTopX>240</WindowTopX>\n");
        this.xls.append("  <WindowTopY>30</WindowTopY>\n");
        this.xls.append("  <ProtectStructure>False</ProtectStructure>\n");
        this.xls.append("  <ProtectWindows>False</ProtectWindows>\n");
        this.xls.append(" </ExcelWorkbook>\n");
        this.xls.append(" <Styles>\n");
        this.xls.append("  <Style ss:ID=\"Default\" ss:Name=\"Normal\">\n");
        this.xls.append("   <Alignment ss:Vertical=\"Bottom\"/>\n");
        this.xls.append("   <Borders/>\n");
        this.xls.append("   <Font ss:FontName=\"Calibri\" x:Family=\"Swiss\" ss:Size=\"11\" ss:Color=\"#000000\"/>\n");
        this.xls.append("   <Interior/>\n");
        this.xls.append("   <NumberFormat/>\n");
        this.xls.append("   <Protection/>\n");
        this.xls.append("  </Style>\n");
        this.xls.append("  <Style ss:ID=\"s62\">\n");
        this.xls.append("   <Alignment ss:Horizontal=\"Center\" ss:Vertical=\"Bottom\"/>\n");
        this.xls.append("   <Font ss:FontName=\"Calibri\" x:Family=\"Swiss\" ss:Size=\"11\" ss:Color=\"#000000\"\n");
        this.xls.append("    ss:Bold=\"1\"/>\n");
        this.xls.append("  </Style>\n");
        this.xls.append(" </Styles>\n");
        this.xls.append(" <Worksheet ss:Name=\"Hoja1\">\n");
        this.xls.append("  <Table ss:ExpandedColumnCount=\"<<COLUMNAS>>\" ss:ExpandedRowCount=\"<<FILAS>>\" x:FullColumns=\"1\"\n");
        this.xls.append("   x:FullRows=\"1\" ss:DefaultColumnWidth=\"60\" ss:DefaultRowHeight=\"15\">\n");
        this.xls.append("   <Column ss:AutoFitWidth=\"0\" ss:Width=\"49.5\"/>\n");
    }
    
    public void setCabeceraTabla(String [] cabTabla)
    {
        this.xls.append("   <Row ss:AutoFitHeight=\"0\">\n");
        for(int i=0; i<this.columnas; i++){
            this.xls.append("    <Cell ss:StyleID=\"s62\"><Data ss:Type=\"String\">"+cabTabla[i]+"</Data></Cell>\n");
        }
        this.xls.append("   </Row>\n");
    }
    
    public void setCabeceraTabla(List tr)
    {
        this.xls.append("   <Row ss:AutoFitHeight=\"0\">\n");
        Iterator it = tr.iterator();
        while(it.hasNext()){
            this.xls.append("    <Cell ss:StyleID=\"s62\"><Data ss:Type=\"String\">"+(String)it.next()+"</Data></Cell>\n");
        }
        this.xls.append("   </Row>\n");
    }

    public String lista(String subtitulo, String [] cabTabla, ResultSet registros, int numDesde)
    {
        this.setEncabezado();
        this.columnas = cabTabla.length;

        this.xls.append("   <Column ss:AutoFitWidth=\"0\" ss:Width=\"140.25\" ss:Span=\""+(this.columnas-2)+"\"/>\n");
        this.xls.append("   <Row ss:AutoFitHeight=\"0\">\n");
        this.xls.append("    <Cell ss:MergeAcross=\""+(this.columnas-1)+"\" ss:StyleID=\"s62\"><Data ss:Type=\"String\">"+subtitulo+"</Data></Cell>\n");
        this.xls.append("   </Row>\n");

        this.setCabeceraTabla(cabTabla);
        try{
            String tipo = "String";
            while(registros.next()){
                this.xls.append("   <Row ss:AutoFitHeight=\"0\">\n");
                this.xls.append("    <Cell><Data ss:Type=\"Number\">" + this.filas + "</Data></Cell>\n");
                for(int j=1; j<this.columnas; j++){
                    tipo = (j>=numDesde)?"Number":"String";
                    this.xls.append("    <Cell><Data ss:Type=\""+tipo+"\">" + ((registros.getString(j)!=null)?registros.getString(j):"") + "</Data></Cell>\n");
                }
                this.xls.append("   </Row>\n");
                this.filas++;
            }
            registros.close();
        }catch(Exception e){
            e.printStackTrace();
        }

        this.setPie();

        String xml = this.xls.toString();
        xml = xml.replace("<<COLUMNAS>>", String.valueOf(this.columnas));
        
        return xml.replace("<<FILAS>>", String.valueOf(this.filas+1));
    }

    public String lista(String subtitulo, String [] cabTabla, ResultSet registros, int numDesde, String sucursal)
    {
        this.setEncabezado();
        this.columnas = cabTabla.length;
        
        this.xls.append("   <Column ss:AutoFitWidth=\"0\" ss:Width=\"140.25\" ss:Span=\""+(this.columnas-2)+"\"/>\n");
        this.xls.append("   <Row ss:AutoFitHeight=\"0\">\n");
        this.xls.append("    <Cell ss:MergeAcross=\""+(this.columnas-1)+"\" ss:StyleID=\"s62\"><Data ss:Type=\"String\">"+subtitulo+"</Data></Cell>\n");
        this.xls.append("   </Row>\n");

        if(sucursal.compareTo("")!=0){
            this.xls.append("   <Row ss:AutoFitHeight=\"0\">\n");
            this.xls.append("    <Cell ss:MergeAcross=\""+(this.columnas-1)+"\" ss:StyleID=\"s62\"><Data ss:Type=\"String\">Sucursal: "+sucursal+"</Data></Cell>\n");
            this.xls.append("   </Row>\n");
        }
        
        this.setCabeceraTabla(cabTabla);
        try{
            String tipo = "String";
            while(registros.next()){
                this.xls.append("   <Row ss:AutoFitHeight=\"0\">\n");
                this.xls.append("    <Cell><Data ss:Type=\"Number\">" + this.filas + "</Data></Cell>\n");
                for(int j=1; j<this.columnas; j++){
                    tipo = (j>=numDesde)?"Number":"String";
                    this.xls.append("    <Cell><Data ss:Type=\""+tipo+"\">" + ((registros.getString(j)!=null)?registros.getString(j):"") + "</Data></Cell>\n");
                }
                this.xls.append("   </Row>\n");
                this.filas++;
            }
            registros.close();
        }catch(Exception e){
            e.printStackTrace();
        }
        
        this.setPie();

        String xml = this.xls.toString();
        xml = xml.replace("<<COLUMNAS>>", String.valueOf(this.columnas));
        
        return xml.replace("<<FILAS>>", String.valueOf(this.filas+2));
    }

    public String activosCustorios(String subtitulo, String [] cabTabla, ResultSet registros, int numDesde, String sucursal, DataBase objDB)
    {
        this.setEncabezado();
        this.columnas = cabTabla.length;

        this.xls.append("   <Column ss:AutoFitWidth=\"0\" ss:Width=\"140.25\" ss:Span=\""+(this.columnas-2)+"\"/>\n");
        this.xls.append("   <Row ss:AutoFitHeight=\"0\">\n");
        this.xls.append("    <Cell ss:MergeAcross=\""+(this.columnas-1)+"\" ss:StyleID=\"s62\"><Data ss:Type=\"String\">"+subtitulo+"</Data></Cell>\n");
        this.xls.append("   </Row>\n");

        if(sucursal.compareTo("")!=0){
            this.xls.append("   <Row ss:AutoFitHeight=\"0\">\n");
            this.xls.append("    <Cell ss:MergeAcross=\""+(this.columnas-1)+"\" ss:StyleID=\"s62\"><Data ss:Type=\"String\">Sucursal: "+sucursal+"</Data></Cell>\n");
            this.xls.append("   </Row>\n");
        }

        this.setCabeceraTabla(cabTabla);
        try{
            String persona_recibe = " ";
            String fecha = " ";
            String id_activo = "";
            
            while(registros.next()){
                this.xls.append("   <Row ss:AutoFitHeight=\"0\">\n");
                this.xls.append("    <Cell><Data ss:Type=\"Number\">" + this.filas + "</Data></Cell>\n");
                for(int j=1; j<4; j++){
                    this.xls.append("    <Cell><Data ss:Type=\"String\">" + ((registros.getString(j)!=null)?registros.getString(j):"") + "</Data></Cell>\n");
                }
                id_activo = (registros.getString("id_activo")!=null) ? registros.getString("id_activo") : "";
                try{
                    ResultSet rsCustodio = objDB.consulta("select persona_recibe, toDateSQL(max(fecha)) as fecha from tbl_activo_custodio as AC inner join tbl_activo_personalizacion as AP "
                        + "on AC.id_activo_personalizacion=AP.id_activo_personalizacion where AC.eliminado=false and AC.id_activo="+id_activo +
                        " group by persona_recibe,AP.id_activo_personalizacion order by AP.id_activo_personalizacion desc");
                    if(rsCustodio.next()){
                        persona_recibe = (rsCustodio.getString("persona_recibe")!=null) ? rsCustodio.getString("persona_recibe") : " ";
                        fecha = (rsCustodio.getString("fecha")!=null) ? rsCustodio.getString("fecha") : " ";
                        rsCustodio.close();
                    }
                }catch(Exception ex){
                    ex.printStackTrace();
                }
                this.xls.append("    <Cell><Data ss:Type=\"String\">" + persona_recibe + "</Data></Cell>\n");
                this.xls.append("    <Cell><Data ss:Type=\"String\">" + fecha + "</Data></Cell>\n");
                this.xls.append("   </Row>\n");
                this.filas++;
            }
            registros.close();
        }catch(Exception e){
            e.printStackTrace();
        }

        this.setPie();

        String xml = this.xls.toString();
        xml = xml.replace("<<COLUMNAS>>", String.valueOf(this.columnas));
        
        return xml.replace("<<FILAS>>", String.valueOf(this.filas+2));
    }
    
    public String ventas(ResultSet registros, String subtitulo, String sucursal, DataBase objDB, String iva_vigente)
    {
        String []cabTabla = new String [] {"Nro.", "VENDEDOR", "RUC", "RAZON SOCIAL", "Nro. FAC.", "F. EMI.", "F. PAGO", "IP", "SUBTOTAL", "DESC.", "SUB. 0", "IVA "+iva_vigente, "TOTAL", "DEUDA", "RETENCION"};
        this.setEncabezado();
        this.columnas = cabTabla.length;
        
        this.xls.append("   <Column ss:AutoFitWidth=\"0\" ss:Width=\"140.25\" ss:Span=\""+(this.columnas-2)+"\"/>\n");
        this.xls.append("   <Row ss:AutoFitHeight=\"0\">\n");
        this.xls.append("    <Cell ss:MergeAcross=\""+(this.columnas-1)+"\" ss:StyleID=\"s62\"><Data ss:Type=\"String\">"+subtitulo+"</Data></Cell>\n");
        this.xls.append("   </Row>\n");

        if(sucursal.compareTo("")!=0){
            this.xls.append("   <Row ss:AutoFitHeight=\"0\">\n");
            this.xls.append("    <Cell ss:MergeAcross=\""+(this.columnas-1)+"\" ss:StyleID=\"s62\"><Data ss:Type=\"String\">Sucursal: "+sucursal+"</Data></Cell>\n");
            this.xls.append("   </Row>\n");
        }
        
        this.setCabeceraTabla(cabTabla);
        try{
            ResultSet retencion = null;
            String id_factura_venta = "-1";
            String impuesto_retenido = "0";
            String tipo = "String";
            while(registros.next()){
                id_factura_venta = (registros.getString("id_factura_venta")!=null) ? registros.getString("id_factura_venta") : "-1";
                this.xls.append("   <Row ss:AutoFitHeight=\"0\">\n");
                this.xls.append("    <Cell><Data ss:Type=\"Number\">" + this.filas + "</Data></Cell>\n");
                for(int j=2; j<this.columnas; j++){
                    tipo = (j>=9)?"Number":"String";
                    this.xls.append("    <Cell><Data ss:Type=\""+tipo+"\">" + ((registros.getString(j)!=null)?registros.getString(j):"") + "</Data></Cell>\n");
                }

                try{
                    retencion = objDB.consulta("select ret_impuesto_retenido from tbl_retencion_venta where anulado=false and id_factura_venta="+id_factura_venta+";");
                    if(retencion.next()){
                        impuesto_retenido=(retencion.getString("ret_impuesto_retenido")!=null) ? retencion.getString("ret_impuesto_retenido") : "0";
                        retencion.close();
                    }
                }catch(Exception e){
                    e.printStackTrace();
                }
                this.xls.append("    <Cell><Data ss:Type=\"Number\">" + impuesto_retenido + "</Data></Cell>\n");

                this.xls.append("   </Row>\n");
                this.filas++;
            }
            registros.close();
        }catch(Exception e){
            e.printStackTrace();
        }
        
        this.setPie();

        String xml = this.xls.toString();
        xml = xml.replace("<<COLUMNAS>>", String.valueOf(this.columnas));
        
        return xml.replace("<<FILAS>>", String.valueOf(this.filas+2));
    }

    public String compras(String subtitulo, String [] cabTabla, ResultSet registros, DataBase objDB)
    {
        this.setEncabezado();
        this.columnas = cabTabla.length;

        this.xls.append("   <Column ss:AutoFitWidth=\"0\" ss:Width=\"140.25\" ss:Span=\""+(this.columnas-2)+"\"/>\n");
        this.xls.append("   <Row ss:AutoFitHeight=\"0\">\n");
        this.xls.append("    <Cell ss:MergeAcross=\""+(this.columnas-1)+"\" ss:StyleID=\"s62\"><Data ss:Type=\"String\">"+subtitulo+"</Data></Cell>\n");
        this.xls.append("   </Row>\n");

        this.setCabeceraTabla(cabTabla);
        try{
            String id_factura_compra = "";
            String valor = "";
            while(registros.next()){
                this.xls.append("   <Row ss:AutoFitHeight=\"0\">\n");
                this.xls.append("    <Cell><Data ss:Type=\"Number\">" + this.filas + "</Data></Cell>\n");
                for(int j=2; j<=6; j++){
                    this.xls.append("    <Cell><Data ss:Type=\"String\">" + ((registros.getString(j)!=null)?registros.getString(j):"") + "</Data></Cell>\n");
                }
                this.xls.append("    <Cell><Data ss:Type=\"Number\">" + ((registros.getString("subtotal")!=null)?registros.getString("subtotal"):"") + "</Data></Cell>\n");
                this.xls.append("    <Cell><Data ss:Type=\"Number\">" + ((registros.getString("descuento")!=null)?registros.getString("descuento"):"") + "</Data></Cell>\n");
                this.xls.append("    <Cell><Data ss:Type=\"Number\">" + ((registros.getString("subtotal_0")!=null)?registros.getString("subtotal_0"):"") + "</Data></Cell>\n");
                this.xls.append("    <Cell><Data ss:Type=\"Number\">" + ((registros.getString("iva_2")!=null)?registros.getString("iva_2"):"") + "</Data></Cell>\n");
                id_factura_compra = (registros.getString("id_factura_compra")!=null) ? registros.getString("id_factura_compra") : "";
                valor = "0";
                try{
                    ResultSet rs = objDB.consulta("select ret_impuesto_retenido from tbl_retencion_compra where id_factura_compra="+id_factura_compra+" and documento='f'");
                    if(rs.next()){
                        valor = (rs.getString("ret_impuesto_retenido")!=null) ? rs.getString("ret_impuesto_retenido") : "0";
                        rs.close();
                    }
                }catch(Exception e){
                    e.printStackTrace();
                }
                this.xls.append("    <Cell><Data ss:Type=\"Number\">" + valor + "</Data></Cell>\n");
                this.xls.append("    <Cell><Data ss:Type=\"Number\">" + ((registros.getString("total_compra")!=null)?registros.getString("total_compra"):"") + "</Data></Cell>\n");

                this.xls.append("   </Row>\n");
                this.filas++;
            }
            registros.close();
        }catch(Exception e){
            e.printStackTrace();
        }

        this.setPie();

        String xml = this.xls.toString();
        xml = xml.replace("<<COLUMNAS>>", String.valueOf(this.columnas));
        
        return xml.replace("<<FILAS>>", String.valueOf(this.filas+1));
    }

    public String liquidaciones(String subtitulo, String [] cabTabla, ResultSet registros, DataBase objDB)
    {
        this.setEncabezado();
        this.columnas = cabTabla.length;

        this.xls.append("   <Column ss:AutoFitWidth=\"0\" ss:Width=\"140.25\" ss:Span=\""+(this.columnas-2)+"\"/>\n");
        this.xls.append("   <Row ss:AutoFitHeight=\"0\">\n");
        this.xls.append("    <Cell ss:MergeAcross=\""+(this.columnas-1)+"\" ss:StyleID=\"s62\"><Data ss:Type=\"String\">"+subtitulo+"</Data></Cell>\n");
        this.xls.append("   </Row>\n");

        this.setCabeceraTabla(cabTabla);
        try{
            String id_liquidacion_compra = "";
            String valor = "";
            while(registros.next()){
                this.xls.append("   <Row ss:AutoFitHeight=\"0\">\n");
                this.xls.append("    <Cell><Data ss:Type=\"Number\">" + this.filas + "</Data></Cell>\n");
                for(int j=2; j<=6; j++){
                    this.xls.append("    <Cell><Data ss:Type=\"String\">" + ((registros.getString(j)!=null)?registros.getString(j):"") + "</Data></Cell>\n");
                }
                this.xls.append("    <Cell><Data ss:Type=\"Number\">" + ((registros.getString("subtotal")!=null)?registros.getString("subtotal"):"") + "</Data></Cell>\n");
                this.xls.append("    <Cell><Data ss:Type=\"Number\">" + ((registros.getString("subtotal_0")!=null)?registros.getString("subtotal_0"):"") + "</Data></Cell>\n");
                this.xls.append("    <Cell><Data ss:Type=\"Number\">" + ((registros.getString("iva_2")!=null)?registros.getString("iva_2"):"") + "</Data></Cell>\n");
                id_liquidacion_compra = (registros.getString("id_liquidacion_compra")!=null) ? registros.getString("id_liquidacion_compra") : "";
                valor = "0";
                try{
                    ResultSet rs = objDB.consulta("select ret_impuesto_retenido from tbl_retencion_compra where id_factura_compra="+id_liquidacion_compra+" and documento='f'");
                    if(rs.next()){
                        valor = (rs.getString("ret_impuesto_retenido")!=null) ? rs.getString("ret_impuesto_retenido") : "0";
                        rs.close();
                    }
                }catch(Exception e){
                    e.printStackTrace();
                }
                this.xls.append("    <Cell><Data ss:Type=\"Number\">" + valor + "</Data></Cell>\n");
                this.xls.append("    <Cell><Data ss:Type=\"Number\">" + ((registros.getString("total_compra")!=null)?registros.getString("total_compra"):"") + "</Data></Cell>\n");

                this.xls.append("   </Row>\n");
                this.filas++;
            }
            registros.close();
        }catch(Exception e){
            e.printStackTrace();
        }

        this.setPie();

        String xml = this.xls.toString();
        xml = xml.replace("<<COLUMNAS>>", String.valueOf(this.columnas));
        
        return xml.replace("<<FILAS>>", String.valueOf(this.filas+1));
    }

    public String notasVenta(String subtitulo, String [] cabTabla, ResultSet registros, DataBase objDB)
    {
        this.setEncabezado();
        this.columnas = cabTabla.length;

        this.xls.append("   <Column ss:AutoFitWidth=\"0\" ss:Width=\"140.25\" ss:Span=\""+(this.columnas-2)+"\"/>\n");
        this.xls.append("   <Row ss:AutoFitHeight=\"0\">\n");
        this.xls.append("    <Cell ss:MergeAcross=\""+(this.columnas-1)+"\" ss:StyleID=\"s62\"><Data ss:Type=\"String\">"+subtitulo+"</Data></Cell>\n");
        this.xls.append("   </Row>\n");

        this.setCabeceraTabla(cabTabla);
        try{
            String id_nota_venta_compra = "";
            String valor = "";
            while(registros.next()){
                this.xls.append("   <Row ss:AutoFitHeight=\"0\">\n");
                this.xls.append("    <Cell><Data ss:Type=\"Number\">" + this.filas + "</Data></Cell>\n");
                for(int j=2; j<=6; j++){
                    this.xls.append("    <Cell><Data ss:Type=\"String\">" + ((registros.getString(j)!=null)?registros.getString(j):"") + "</Data></Cell>\n");
                }
                id_nota_venta_compra = (registros.getString("id_nota_venta_compra")!=null) ? registros.getString("id_nota_venta_compra") : "";
                valor = "0";
                try{
                    ResultSet rs = objDB.consulta("select ret_impuesto_retenido from tbl_retencion_compra where id_factura_compra="+id_nota_venta_compra+" and documento='v'");
                    if(rs.next()){
                        valor = (rs.getString("ret_impuesto_retenido")!=null) ? rs.getString("ret_impuesto_retenido") : "0";
                        rs.close();
                    }
                }catch(Exception e){
                    e.printStackTrace();
                }
                this.xls.append("    <Cell><Data ss:Type=\"Number\">" + valor + "</Data></Cell>\n");
                this.xls.append("    <Cell><Data ss:Type=\"Number\">" + ((registros.getString("total_compra")!=null)?registros.getString("total_compra"):"") + "</Data></Cell>\n");

                this.xls.append("   </Row>\n");
                this.filas++;
            }
            registros.close();
        }catch(Exception e){
            e.printStackTrace();
        }

        this.setPie();

        String xml = this.xls.toString();
        xml = xml.replace("<<COLUMNAS>>", String.valueOf(this.columnas));
        
        return xml.replace("<<FILAS>>", String.valueOf(this.filas+1));
    }

    public String ingresosPeriodos(String subtitulo, String sucursal, ResultSet emisiones, ResultSet recaudos, ResultSet xcobrar, ResultSet instalaciones, ResultSet otros)
    {
        String []cabTabla = new String [] {"SUCURSAL", "PERIODO", "INSTALACIONES", "OTROS INGRESOS", "PRE-FACTURADO", "EMITIDO", "POR COBRAR", "SUMATORIA"};
        this.setEncabezado();
        this.columnas = cabTabla.length;

        this.xls.append("   <Column ss:AutoFitWidth=\"0\" ss:Width=\"140.25\" ss:Span=\""+(this.columnas-2)+"\"/>\n");
        this.xls.append("   <Row ss:AutoFitHeight=\"0\">\n");
        this.xls.append("    <Cell ss:MergeAcross=\""+(this.columnas-1)+"\" ss:StyleID=\"s62\"><Data ss:Type=\"String\">"+subtitulo+"</Data></Cell>\n");
        this.xls.append("   </Row>\n");

        if(sucursal.compareTo("")!=0){
            this.xls.append("   <Row ss:AutoFitHeight=\"0\">\n");
            this.xls.append("    <Cell ss:MergeAcross=\""+(this.columnas-1)+"\" ss:StyleID=\"s62\"><Data ss:Type=\"String\">Sucursal: "+sucursal+"</Data></Cell>\n");
            this.xls.append("   </Row>\n");
        }

        try{
            String[][] mat_emisiones = DatosDinamicos.ResultSetToMatriz(emisiones);
            String[][] mat_recaudos = DatosDinamicos.ResultSetToMatriz(recaudos);
            String[][] mat_xcobrar = DatosDinamicos.ResultSetToMatriz(xcobrar);
            String[][] mat_instalaciones = DatosDinamicos.ResultSetToMatriz(instalaciones);
            String[][] mat_otros = DatosDinamicos.ResultSetToMatriz(otros);

            this.setCabeceraTabla(cabTabla);
            float suma = 0;
            int pos = -1;
            for(int i=0; i<mat_emisiones.length; i++){
                this.xls.append("   <Row ss:AutoFitHeight=\"0\">\n");
                this.xls.append("    <Cell><Data ss:Type=\"String\">" + mat_emisiones[i][0] + "</Data></Cell>\n");
                this.xls.append("    <Cell><Data ss:Type=\"String\">" + mat_emisiones[i][1] + "</Data></Cell>\n");
                pos = DatosDinamicos.enMatrizSecuencialSucursal(mat_instalaciones, mat_emisiones[i][1], 1, mat_emisiones[i][0]);
                suma = Float.valueOf(pos!=-1 ? mat_instalaciones[pos][2] : "0");
                this.xls.append("    <Cell><Data ss:Type=\"Number\">" + (pos!=-1 ? mat_instalaciones[pos][2] : "0") + "</Data></Cell>\n");

                pos = DatosDinamicos.enMatrizSecuencialSucursal(mat_otros, mat_emisiones[i][1], 1, mat_emisiones[i][0]);
                suma += Float.valueOf(pos!=-1 ? mat_otros[pos][2] : "0");
                this.xls.append("    <Cell><Data ss:Type=\"Number\">" + (pos!=-1 ? mat_otros[pos][2] : "0") + "</Data></Cell>\n");

                this.xls.append("    <Cell><Data ss:Type=\"Number\">" + mat_emisiones[i][2] + "</Data></Cell>\n");

                pos = DatosDinamicos.enMatrizSecuencialSucursal(mat_recaudos, mat_emisiones[i][1], 1, mat_emisiones[i][0]);
                suma += Float.valueOf(pos!=-1 ? mat_recaudos[pos][2] : "0");
                this.xls.append("    <Cell><Data ss:Type=\"Number\">" + (pos!=-1 ? mat_recaudos[pos][2] : "0") + "</Data></Cell>\n");

                pos = DatosDinamicos.enMatrizSecuencialSucursal(mat_xcobrar, mat_emisiones[i][1], 1, mat_emisiones[i][0]);
                this.xls.append("    <Cell><Data ss:Type=\"Number\">" + (pos!=-1 ? mat_xcobrar[pos][2] : "0") + "</Data></Cell>\n");

                this.xls.append("    <Cell><Data ss:Type=\"Number\">" + Addons.redondear(String.valueOf(suma)) + "</Data></Cell>\n");
                this.xls.append("   </Row>\n");
                this.filas++;
            }
            
        }catch(Exception e){
            e.printStackTrace();
        }

        this.setPie();

        String xml = this.xls.toString();
        xml = xml.replace("<<COLUMNAS>>", String.valueOf(this.columnas));
        
        return xml.replace("<<FILAS>>", String.valueOf(this.filas+2));
    }

    public String cierreCaja(String subtitulo, int id_sucursal, String fecha, DataBase objDB)
    {
        ResultSet ventas = objDB.consulta("select vendedor,txt_forma_pago,sum(total) as total from vta_factura_venta where id_sucursal="+id_sucursal+" and anulado=false and fecha_emision='"+fecha+"' group by vendedor,txt_forma_pago order by vendedor");
        ResultSet retencion = null;

        this.setEncabezado();
        this.columnas = 2;

        this.xls.append("   <Column ss:AutoFitWidth=\"0\" ss:Width=\"140.25\" ss:Span=\""+(this.columnas-2)+"\"/>\n");
        this.xls.append("   <Row ss:AutoFitHeight=\"0\">\n");
        this.xls.append("    <Cell ss:MergeAcross=\""+(this.columnas-1)+"\" ss:StyleID=\"s62\"><Data ss:Type=\"String\">"+subtitulo+"</Data></Cell>\n");
        this.xls.append("   </Row>\n");

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
                this.xls.append("   <Row ss:AutoFitHeight=\"0\">\n");
                this.xls.append("    <Cell ss:MergeAcross=\""+(this.columnas-1)+"\" ss:StyleID=\"s62\"><Data ss:Type=\"String\">VENTAS</Data></Cell>\n");
                this.xls.append("   </Row>\n");
                while(ventas.next()){
                    vendedor = ventas.getString("vendedor")!=null ? ventas.getString("vendedor") : "";
                    forma_pago = ventas.getString("txt_forma_pago")!=null ? ventas.getString("txt_forma_pago") : "";
                    total = ventas.getString("total")!=null ? ventas.getFloat("total") : 0;
                    if(aux.compareTo(vendedor)!=0){
                        if(aux.compareTo("")!=0){
                            this.xls.append("   <Row ss:AutoFitHeight=\"0\">\n");
                            this.xls.append("    <Cell><Data ss:Type=\"String\">TOTAL</Data></Cell>\n");
                            this.xls.append("    <Cell><Data ss:Type=\"Number\">" + String.valueOf(ingresos) + "</Data></Cell>\n");
                            this.xls.append("   </Row>\n");

                            this.xls.append("   <Row ss:AutoFitHeight=\"0\">\n");
                            this.xls.append("    <Cell><Data ss:Type=\"String\">EN CAJA</Data></Cell>\n");
                            this.xls.append("    <Cell><Data ss:Type=\"Number\">" + String.valueOf(efectivo - ret) + "</Data></Cell>\n");
                            this.xls.append("   </Row>\n");
                        }
                        this.xls.append("   <Row ss:AutoFitHeight=\"0\">\n");
                        this.xls.append("    <Cell ss:MergeAcross=\""+(this.columnas-1)+"\" ss:StyleID=\"s62\"><Data ss:Type=\"String\">" + vendedor + "</Data></Cell>\n");
                        this.xls.append("   </Row>\n");
                        
                        /* retenciones */
                        try{
                            retencion = objDB.consulta("select sum(R.ret_impuesto_retenido) as total from tbl_factura_venta as F inner join tbl_retencion_venta as R "
                            + "on R.id_factura_venta=F.id_factura_venta where id_sucursal="+id_sucursal+" and F.anulado=false and R.anulado=false and F.vendedor='"+vendedor+"' and ret_fecha_emision='"+fecha+"'");
                            if(retencion.next()){
                                ret = retencion.getString("total")!=null ? retencion.getFloat("total") : 0;
                                this.xls.append("   <Row ss:AutoFitHeight=\"0\">\n");
                                this.xls.append("    <Cell><Data ss:Type=\"String\">RETENCIONES</Data></Cell>\n");
                                this.xls.append("    <Cell><Data ss:Type=\"Number\">" + String.valueOf(ret) + "</Data></Cell>\n");
                                this.xls.append("   </Row>\n");
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
                    this.xls.append("   <Row ss:AutoFitHeight=\"0\">\n");
                    this.xls.append("    <Cell><Data ss:Type=\"String\">"+forma_pago+"</Data></Cell>\n");
                    this.xls.append("    <Cell><Data ss:Type=\"Number\">" + String.valueOf(total) + "</Data></Cell>\n");
                    this.xls.append("   </Row>\n");
                    this.filas++;
                }
                this.xls.append("   <Row ss:AutoFitHeight=\"0\">\n");
                this.xls.append("    <Cell><Data ss:Type=\"String\">TOTAL</Data></Cell>\n");
                this.xls.append("    <Cell><Data ss:Type=\"Number\">" + String.valueOf(ingresos) + "</Data></Cell>\n");
                this.xls.append("   </Row>\n");

                this.xls.append("   <Row ss:AutoFitHeight=\"0\">\n");
                this.xls.append("    <Cell><Data ss:Type=\"String\">EN CAJA</Data></Cell>\n");
                this.xls.append("    <Cell><Data ss:Type=\"Number\">" + String.valueOf(efectivo - ret) + "</Data></Cell>\n");
                this.xls.append("   </Row>\n");
                
                /* gastos */
                this.xls.append("   <Row ss:AutoFitHeight=\"0\">\n");
                this.xls.append("    <Cell ss:MergeAcross=\""+(this.columnas-1)+"\" ss:StyleID=\"s62\"><Data ss:Type=\"String\">PAGOS</Data></Cell>\n");
                this.xls.append("   </Row>\n");
                String tipo_pago = "";
                float total_efectivo_pago = 0;
                float total_egreso = 0;
                try{
                    ResultSet pagos = objDB.consulta("select txt_tipo_pago,sum(abono) as total from vta_factura_compra_pago as f inner join tbl_comprobante_egreso as C on F.id_comprobante_egreso=C.id_comprobante_egreso where C.id_sucursal="+id_sucursal+" and fecha_pago='"+fecha+"' group by txt_tipo_pago");
                    while(pagos.next()){
                        tipo_pago = pagos.getString("txt_tipo_pago")!=null ? pagos.getString("txt_tipo_pago") : "";
                        float pago = pagos.getString("total")!=null ? pagos.getFloat("total") : 0;
                        this.xls.append("   <Row ss:AutoFitHeight=\"0\">\n");
                        this.xls.append("    <Cell><Data ss:Type=\"String\">"+tipo_pago+"</Data></Cell>\n");
                        this.xls.append("    <Cell><Data ss:Type=\"Number\">" + String.valueOf(pago) + "</Data></Cell>\n");
                        this.xls.append("   </Row>\n");
                        total_egreso += pago;
                        if(tipo_pago.compareTo("EFECTIVO")==0){
                            total_efectivo_pago += pago;
                            //total_caja -= total;
                        }
                        this.filas++;
                    }
                    pagos.close();
                }catch(Exception e){}

                this.xls.append("   <Row ss:AutoFitHeight=\"0\">\n");
                this.xls.append("    <Cell ss:MergeAcross=\""+(this.columnas-1)+"\" ss:StyleID=\"s62\"><Data ss:Type=\"String\">TOTALES</Data></Cell>\n");
                this.xls.append("   </Row>\n");

                this.xls.append("   <Row ss:AutoFitHeight=\"0\">\n");
                this.xls.append("    <Cell><Data ss:Type=\"String\">TOTAL VENTAS</Data></Cell>\n");
                this.xls.append("    <Cell><Data ss:Type=\"Number\">" + String.valueOf(total_ingresos) + "</Data></Cell>\n");
                this.xls.append("   </Row>\n");

                this.xls.append("   <Row ss:AutoFitHeight=\"0\">\n");
                this.xls.append("    <Cell><Data ss:Type=\"String\">TOTAL PAGOS</Data></Cell>\n");
                this.xls.append("    <Cell><Data ss:Type=\"Number\">" + String.valueOf(total_egreso) + "</Data></Cell>\n");
                this.xls.append("   </Row>\n");

                this.xls.append("   <Row ss:AutoFitHeight=\"0\">\n");
                this.xls.append("    <Cell><Data ss:Type=\"String\">TOTAL EN CAJA</Data></Cell>\n");
                this.xls.append("    <Cell><Data ss:Type=\"Number\">" + String.valueOf(total_efectivo - total_ret - total_efectivo_pago) + "</Data></Cell>\n");
                this.xls.append("   </Row>\n");

                ventas.close();
                retencion.close();
                
            }catch(Exception e){
                e.printStackTrace();
            }
        
        this.setPie();

        String xml = this.xls.toString();
        xml = xml.replace("<<COLUMNAS>>", String.valueOf(this.columnas));
        
        return xml.replace("<<FILAS>>", String.valueOf(this.filas+12));
    }

    public String ingresosEgresos(String subtitulo, int id_sucursal, String fecha, DataBase objDB, String banco, String caja)
    {
        ResultSet ventas = objDB.consulta("select CASE tipo_cobro WHEN 'e' THEN 'EFECTIVO' WHEN 'c' THEN 'CHEQUE' ELSE '' END AS txt_tipo_cobro,"
                + "sum(total) as total from vta_comprobante_ingreso where id_sucursal="+id_sucursal+" and anulado=false and fecha_actual='"+fecha+"' group by txt_tipo_cobro");
        ResultSet retencion = null;

        this.setEncabezado();
        this.columnas = 2;

        this.xls.append("   <Column ss:AutoFitWidth=\"0\" ss:Width=\"140.25\" ss:Span=\""+(this.columnas-2)+"\"/>\n");
        this.xls.append("   <Row ss:AutoFitHeight=\"0\">\n");
        this.xls.append("    <Cell ss:MergeAcross=\""+(this.columnas-1)+"\" ss:StyleID=\"s62\"><Data ss:Type=\"String\">"+subtitulo+"</Data></Cell>\n");
        this.xls.append("   </Row>\n");

        try{
                float total_ingresos = 0;
                float total_efectivo = 0;
                float ret = 0;
                float total_ret = 0;
                String forma_pago = "";
                float total = 0;
                this.xls.append("   <Row ss:AutoFitHeight=\"0\">\n");
                this.xls.append("    <Cell ss:MergeAcross=\""+(this.columnas-1)+"\" ss:StyleID=\"s62\"><Data ss:Type=\"String\">INGRESOS</Data></Cell>\n");
                this.xls.append("   </Row>\n");
                while(ventas.next()){
                    forma_pago = ventas.getString("txt_tipo_cobro")!=null ? ventas.getString("txt_tipo_cobro") : "";
                    total = ventas.getString("total")!=null ? ventas.getFloat("total") : 0;
                    total_ingresos += total;
                    if(forma_pago.compareTo("EFECTIVO")==0){
                        total_efectivo += total;
                    }
                    this.xls.append("   <Row ss:AutoFitHeight=\"0\">\n");
                    this.xls.append("    <Cell><Data ss:Type=\"String\">"+forma_pago+"</Data></Cell>\n");
                    this.xls.append("    <Cell><Data ss:Type=\"Number\">" + String.valueOf(total) + "</Data></Cell>\n");
                    this.xls.append("   </Row>\n");
                    this.filas++;
                }
                /* retenciones */
                try{
                    retencion = objDB.consulta("select sum(R.ret_impuesto_retenido) as total from tbl_factura_venta as F inner join tbl_retencion_venta as R "
                    + "on R.id_factura_venta=F.id_factura_venta where id_sucursal="+id_sucursal+" and F.anulado=false and R.anulado=false and ret_fecha_emision='"+fecha+"'");
                    if(retencion.next()){
                        ret = retencion.getString("total")!=null ? retencion.getFloat("total") : 0;
                        this.xls.append("   <Row ss:AutoFitHeight=\"0\">\n");
                        this.xls.append("    <Cell><Data ss:Type=\"String\">RETENCIONES</Data></Cell>\n");
                        this.xls.append("    <Cell><Data ss:Type=\"Number\">" + String.valueOf(ret) + "</Data></Cell>\n");
                        this.xls.append("   </Row>\n");
                        total_ret += ret;
                    }
                }catch(Exception ex){}

                /*this.xls.append("   <Row ss:AutoFitHeight=\"0\">\n");
                this.xls.append("    <Cell><Data ss:Type=\"String\">TOTAL</Data></Cell>\n");
                this.xls.append("    <Cell><Data ss:Type=\"Number\">" + String.valueOf(ingresos) + "</Data></Cell>\n");
                this.xls.append("   </Row>\n");*/
                
                /* gastos */
                this.xls.append("   <Row ss:AutoFitHeight=\"0\">\n");
                this.xls.append("    <Cell ss:MergeAcross=\""+(this.columnas-1)+"\" ss:StyleID=\"s62\"><Data ss:Type=\"String\">EGRESOS</Data></Cell>\n");
                this.xls.append("   </Row>\n");
                String tipo_pago = "";
                float total_efectivo_pago = 0;
                float total_egreso = 0;
                try{
                    ResultSet pagos = objDB.consulta("select CASE tipo_pago WHEN 'e' THEN 'EFECTIVO' WHEN 'c' THEN 'CHEQUE' ELSE '' END AS txt_tipo_pago,sum(total) as total "
                            + "from tbl_comprobante_egreso where id_sucursal="+id_sucursal+" and anulado=false and fecha_actual='"+fecha+"' group by txt_tipo_pago");
                    while(pagos.next()){
                        tipo_pago = pagos.getString("txt_tipo_pago")!=null ? pagos.getString("txt_tipo_pago") : "";
                        float pago = pagos.getString("total")!=null ? pagos.getFloat("total") : 0;
                        this.xls.append("   <Row ss:AutoFitHeight=\"0\">\n");
                        this.xls.append("    <Cell><Data ss:Type=\"String\">"+tipo_pago+"</Data></Cell>\n");
                        this.xls.append("    <Cell><Data ss:Type=\"Number\">" + String.valueOf(pago) + "</Data></Cell>\n");
                        this.xls.append("   </Row>\n");
                        total_egreso += pago;
                        if(tipo_pago.compareTo("EFECTIVO")==0){
                            total_efectivo_pago += pago;
                        }
                        this.filas++;
                    }
                    pagos.close();
                }catch(Exception e){}

                this.xls.append("   <Row ss:AutoFitHeight=\"0\">\n");
                this.xls.append("    <Cell ss:MergeAcross=\""+(this.columnas-1)+"\" ss:StyleID=\"s62\"><Data ss:Type=\"String\">TOTALES</Data></Cell>\n");
                this.xls.append("   </Row>\n");

                this.xls.append("   <Row ss:AutoFitHeight=\"0\">\n");
                this.xls.append("    <Cell><Data ss:Type=\"String\">TOTAL INGRESOS</Data></Cell>\n");
                this.xls.append("    <Cell><Data ss:Type=\"Number\">" + String.valueOf(total_ingresos) + "</Data></Cell>\n");
                this.xls.append("   </Row>\n");

                this.xls.append("   <Row ss:AutoFitHeight=\"0\">\n");
                this.xls.append("    <Cell><Data ss:Type=\"String\">TOTAL EGRESOS</Data></Cell>\n");
                this.xls.append("    <Cell><Data ss:Type=\"Number\">" + String.valueOf(total_egreso) + "</Data></Cell>\n");
                this.xls.append("   </Row>\n");

                this.xls.append("   <Row ss:AutoFitHeight=\"0\">\n");
                this.xls.append("    <Cell><Data ss:Type=\"String\">INGRESOS - EGRESOS - RETENCIONES</Data></Cell>\n");
                this.xls.append("    <Cell><Data ss:Type=\"Number\">" + String.valueOf(total_ingresos - total_egreso - total_ret) + "</Data></Cell>\n");
                this.xls.append("   </Row>\n");

                this.xls.append("   <Row ss:AutoFitHeight=\"0\">\n");
                this.xls.append("    <Cell><Data ss:Type=\"String\">INGRESOS (EFECTIVO) - EGRESOS (EFECTIVO) - RETENCIONES</Data></Cell>\n");
                this.xls.append("    <Cell><Data ss:Type=\"Number\">" + String.valueOf(total_efectivo - total_efectivo_pago - total_ret) + "</Data></Cell>\n");
                this.xls.append("   </Row>\n");

                ventas.close();
                retencion.close();

                /* transferencias al banco */
                try{
                    this.xls.append("   <Row ss:AutoFitHeight=\"0\">\n");
                    this.xls.append("    <Cell ss:MergeAcross=\""+(this.columnas-1)+"\" ss:StyleID=\"s62\"><Data ss:Type=\"String\">COMPROBANTES DE DIARIO CON BANCOS</Data></Cell>\n");
                    this.xls.append("   </Row>\n");

                    ResultSet diarios = objDB.consulta("select distinct C.detalle, C.total from tbl_comprobante_diario as C inner join tbl_comprobante_diario_detalle as D "
                    + "on C.id_comprobante_diario=D.id_comprobante_diario where C.id_sucursal="+id_sucursal+" and C.anulado=false and fecha_actual='"+fecha+"' "
                    + "and D.id_plan_cuenta in ("+banco+","+caja+")");
                    while(diarios.next()){
                        ret = diarios.getString("total")!=null ? diarios.getFloat("total") : 0;
                        this.xls.append("   <Row ss:AutoFitHeight=\"0\">\n");
                        this.xls.append("    <Cell><Data ss:Type=\"String\">"+(diarios.getString("detalle")!=null ? diarios.getString("detalle") : "")+"</Data></Cell>\n");
                        this.xls.append("    <Cell><Data ss:Type=\"Number\">" + String.valueOf(ret) + "</Data></Cell>\n");
                        this.xls.append("   </Row>\n");
                        this.filas++;
                    }
                    diarios.close();
                }catch(Exception ex){}

            }catch(Exception e){
                e.printStackTrace();
            }

        this.setPie();

        String xml = this.xls.toString();
        xml = xml.replace("<<COLUMNAS>>", String.valueOf(this.columnas));
        
        return xml.replace("<<FILAS>>", String.valueOf(this.filas+9));
    }

    public String balanceGeneral(ResultSet registros, String subtitulo, String sucursal)
    {
        this.setEncabezado();
        String datos[][] = Addons.calcularTotalesBalance(registros, 0, 0);
        Addons.ordenamientoQuicksort(datos, 0, datos.length-1, 2);
        this.columnas = Addons.maxNivelCodigos(datos)+2;
        
        this.xls.append("   <Column ss:AutoFitWidth=\"0\" ss:Width=\"140.25\" ss:Span=\""+(this.columnas-2)+"\"/>\n");
        this.xls.append("   <Row ss:AutoFitHeight=\"0\">\n");
        this.xls.append("    <Cell ss:MergeAcross=\""+(this.columnas-1)+"\" ss:StyleID=\"s62\"><Data ss:Type=\"String\">"+subtitulo+"</Data></Cell>\n");
        this.xls.append("   </Row>\n");
        
        if(sucursal.compareTo("")!=0){
            this.xls.append("   <Row ss:AutoFitHeight=\"0\">\n");
            this.xls.append("    <Cell ss:MergeAcross=\""+(this.columnas-1)+"\" ss:StyleID=\"s62\"><Data ss:Type=\"String\">Sucursal: "+sucursal+"</Data></Cell>\n");
            this.xls.append("   </Row>\n");
        }

        
        try{
            float anchosCols[] = new float[this.columnas];
            anchosCols[0] = 80f;
            anchosCols[1] = 200f;
            for(int c=2; c<this.columnas; c++){
                anchosCols[c] = 65f;
            }
            
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
                        if(nivel==1){
                            if(datos[i][3].toUpperCase().indexOf("PASIVO")>=0 || datos[i][3].toUpperCase().indexOf("PATRIMONIO")>=0){
                                sumPasPat += Double.valueOf(datos[i][4]);
                            }
                        }
                        this.xls.append("   <Row ss:AutoFitHeight=\"0\">\n");
                        this.xls.append("    <Cell><Data ss:Type=\"String\">" + datos[i][2] + "</Data></Cell>\n");
                        this.xls.append("    <Cell><Data ss:Type=\"String\">" + datos[i][3] + "</Data></Cell>\n");
                        k=2;
                        numCols = this.columnas - 2 - nivel;
                        for(int j=0; j<numCols; j++){
                            this.xls.append("    <Cell><Data ss:Type=\"String\"></Data></Cell>\n");
                            k++;
                        }
                        this.xls.append("    <Cell><Data ss:Type=\"Number\">" + valor + "</Data></Cell>\n");
                        k++;
                        for(int j=k; j<this.columnas; j++){
                            this.xls.append("    <Cell><Data ss:Type=\"String\"></Data></Cell>\n");
                        }
                        this.xls.append("   </Row>\n");
                        this.filas++;
                    }
                }
                registros.close();
            }catch(Exception e){
                e.printStackTrace();
            }
            this.xls.append("   <Row ss:AutoFitHeight=\"0\">\n");
            this.xls.append("    <Cell ss:MergeAcross=\""+(this.columnas-2)+"\"><Data ss:Type=\"String\">TOTAL PASIVOS MÁS PATRIMONIO</Data></Cell>\n");
            this.xls.append("    <Cell><Data ss:Type=\"Number\">" + Addons.redondear(sumPasPat) + "</Data></Cell>\n");
            this.xls.append("   </Row>\n");
            
            
        }catch(Exception e){
            e.printStackTrace();
        }
            
        this.setPie();

        String xml = this.xls.toString();
        xml = xml.replace("<<COLUMNAS>>", String.valueOf(this.columnas));
        
        return xml.replace("<<FILAS>>", String.valueOf(this.filas+2));
    } 
    
    public String balanceResultados(ResultSet registros, String subtitulo, String sucursal)
    {
        this.setEncabezado();
        String datos[][] = Addons.calcularTotalesBalance(registros, 0, 0);
        Addons.ordenamientoQuicksort(datos, 0, datos.length-1, 2);
        this.columnas = Addons.maxNivelCodigos(datos)+2;
        
        
        this.xls.append("   <Column ss:AutoFitWidth=\"0\" ss:Width=\"140.25\" ss:Span=\""+(this.columnas-2)+"\"/>\n");
        this.xls.append("   <Row ss:AutoFitHeight=\"0\">\n");
        this.xls.append("    <Cell ss:MergeAcross=\""+(this.columnas-1)+"\" ss:StyleID=\"s62\"><Data ss:Type=\"String\">"+subtitulo+"</Data></Cell>\n");
        this.xls.append("   </Row>\n");

        if(sucursal.compareTo("")!=0){
            this.xls.append("   <Row ss:AutoFitHeight=\"0\">\n");
            this.xls.append("    <Cell ss:MergeAcross=\""+(this.columnas-1)+"\" ss:StyleID=\"s62\"><Data ss:Type=\"String\">Sucursal: "+sucursal+"</Data></Cell>\n");
            this.xls.append("   </Row>\n");
        }
        
        try{
            float anchosCols[] = new float[this.columnas];
            anchosCols[0] = 80f;
            anchosCols[1] = 200f;
            for(int c=2; c<this.columnas; c++){
                anchosCols[c] = 65f;
            }
            
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
                        if(nivel==1){
                            if(datos[i][3].toUpperCase().indexOf("INGRESO")>=0){
                                total_ingresos = Double.valueOf(datos[i][4]);
                            }
                            if(datos[i][3].toUpperCase().indexOf("GASTO")>=0 || datos[i][3].toUpperCase().indexOf("EGRESO")>=0){
                                total_gastos = Double.valueOf(datos[i][4]);
                            }
                        }
                        this.xls.append("   <Row ss:AutoFitHeight=\"0\">\n");
                        this.xls.append("    <Cell><Data ss:Type=\"String\">" + datos[i][2] + "</Data></Cell>\n");
                        this.xls.append("    <Cell><Data ss:Type=\"String\">" + datos[i][3] + "</Data></Cell>\n");
                        k=2;
                        numCols = this.columnas - 2 - nivel;
                        for(int j=0; j<numCols; j++){
                            this.xls.append("    <Cell><Data ss:Type=\"String\"></Data></Cell>\n");
                            k++;
                        }
                        this.xls.append("    <Cell><Data ss:Type=\"Number\">" + valor + "</Data></Cell>\n");
                        k++;
                        for(int j=k; j<this.columnas; j++){
                            this.xls.append("    <Cell><Data ss:Type=\"String\"></Data></Cell>\n");
                        }
                        this.xls.append("   </Row>\n");
                        this.filas++;
                    }
                }
                registros.close();
            }catch(Exception e){
                e.printStackTrace();
            }
            this.xls.append("   <Row ss:AutoFitHeight=\"0\">\n");
            this.xls.append("    <Cell ss:MergeAcross=\""+(this.columnas-2)+"\"><Data ss:Type=\"String\">RESULTADO DEL EJERCICIO</Data></Cell>\n");
            this.xls.append("    <Cell><Data ss:Type=\"Number\">" + Addons.redondear(total_ingresos-total_gastos) + "</Data></Cell>\n");
            this.xls.append("   </Row>\n");
            
            
        }catch(Exception e){
            e.printStackTrace();
        }
            
        this.setPie();

        String xml = this.xls.toString();
        xml = xml.replace("<<COLUMNAS>>", String.valueOf(this.columnas));
        
        return xml.replace("<<FILAS>>", String.valueOf(this.filas+2));
    }

    public String RPLiquidacionIndividual(ResultSet rsRolPago, String subtitulo, String sucursal, DataBase objDB, Sucursal objSucursal)
    {
        this.setEncabezado();
        this.columnas = 6;

        this.xls.append("   <Column ss:AutoFitWidth=\"0\" ss:Width=\"140.25\" ss:Span=\""+(this.columnas-2)+"\"/>\n");
        this.xls.append("   <Row ss:AutoFitHeight=\"0\">\n");
        this.xls.append("    <Cell ss:MergeAcross=\""+(this.columnas-1)+"\" ss:StyleID=\"s62\"><Data ss:Type=\"String\">"+subtitulo+"</Data></Cell>\n");
        this.xls.append("   </Row>\n");

        if(sucursal.compareTo("")!=0){
            this.xls.append("   <Row ss:AutoFitHeight=\"0\">\n");
            this.xls.append("    <Cell ss:MergeAcross=\""+(this.columnas-1)+"\" ss:StyleID=\"s62\"><Data ss:Type=\"String\">Sucursal: "+sucursal+"</Data></Cell>\n");
            this.xls.append("   </Row>\n");
        }

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

        String rubro = "";
        String valor = "";
        try{
            while(rsRolPago.next()){
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
                    
                this.xls.append("   <Row ss:AutoFitHeight=\"0\">\n");
                this.xls.append("    <Cell><Data ss:Type=\"String\">Nombres: </Data></Cell>\n");
                this.xls.append("    <Cell><Data ss:Type=\"String\">"+empleado+"</Data></Cell>\n");
                this.xls.append("    <Cell><Data ss:Type=\"String\"> </Data></Cell>\n");
                this.xls.append("    <Cell><Data ss:Type=\"String\"> </Data></Cell>\n");
                this.xls.append("    <Cell><Data ss:Type=\"String\">Período: </Data></Cell>\n");
                this.xls.append("    <Cell><Data ss:Type=\"String\">"+periodo+"</Data></Cell>\n");
                this.xls.append("   </Row>\n");

                this.xls.append("   <Row ss:AutoFitHeight=\"0\">\n");
                this.xls.append("    <Cell><Data ss:Type=\"String\">Sucursal: </Data></Cell>\n");
                this.xls.append("    <Cell><Data ss:Type=\"String\">"+objSucursal.getNombre(id_sucursal)+"</Data></Cell>\n");
                this.xls.append("    <Cell><Data ss:Type=\"String\">Departamento: </Data></Cell>\n");
                this.xls.append("    <Cell><Data ss:Type=\"String\">"+departamento+"</Data></Cell>\n");
                this.xls.append("    <Cell><Data ss:Type=\"String\">Cargo: </Data></Cell>\n");
                this.xls.append("    <Cell><Data ss:Type=\"String\">"+rol+"</Data></Cell>\n");
                this.xls.append("   </Row>\n");

                this.xls.append("   <Row ss:AutoFitHeight=\"0\">\n");
                this.xls.append("    <Cell><Data ss:Type=\"String\">Tiempo laborado: </Data></Cell>\n");
                this.xls.append("    <Cell><Data ss:Type=\"String\">"+dias_laborados+"</Data></Cell>\n");
                this.xls.append("    <Cell><Data ss:Type=\"String\">H.E. 50%: </Data></Cell>\n");
                this.xls.append("    <Cell><Data ss:Type=\"String\">"+num_horas_50+"</Data></Cell>\n");
                this.xls.append("    <Cell><Data ss:Type=\"String\">H.E. 100%: </Data></Cell>\n");
                this.xls.append("    <Cell><Data ss:Type=\"String\">"+num_horas_100+"</Data></Cell>\n");
                this.xls.append("   </Row>\n");

                /* INGRESOS */
                
                this.xls.append("   <Row ss:AutoFitHeight=\"0\">\n");
                this.xls.append("    <Cell><Data ss:Type=\"String\">INGRESOS</Data></Cell>\n");
                this.xls.append("    <Cell><Data ss:Type=\"String\">VALOR</Data></Cell>\n");
                this.xls.append("    <Cell><Data ss:Type=\"String\"> </Data></Cell>\n");
                this.xls.append("    <Cell><Data ss:Type=\"String\"> </Data></Cell>\n");
                this.xls.append("    <Cell><Data ss:Type=\"String\"> </Data></Cell>\n");
                this.xls.append("    <Cell><Data ss:Type=\"String\"> </Data></Cell>\n");
                this.xls.append("   </Row>\n");

                this.xls.append("   <Row ss:AutoFitHeight=\"0\">\n");
                this.xls.append("    <Cell><Data ss:Type=\"String\">Sueldo básico unificado</Data></Cell>\n");
                this.xls.append("    <Cell><Data ss:Type=\"String\">"+sbu+"</Data></Cell>\n");
                this.xls.append("    <Cell><Data ss:Type=\"String\"> </Data></Cell>\n");
                this.xls.append("    <Cell><Data ss:Type=\"String\"> </Data></Cell>\n");
                this.xls.append("    <Cell><Data ss:Type=\"String\"> </Data></Cell>\n");
                this.xls.append("    <Cell><Data ss:Type=\"String\"> </Data></Cell>\n");
                this.xls.append("   </Row>\n");

                this.xls.append("   <Row ss:AutoFitHeight=\"0\">\n");
                this.xls.append("    <Cell><Data ss:Type=\"String\">Horas extras 50%</Data></Cell>\n");
                this.xls.append("    <Cell><Data ss:Type=\"String\">"+ing_total_50+"</Data></Cell>\n");
                this.xls.append("    <Cell><Data ss:Type=\"String\"> </Data></Cell>\n");
                this.xls.append("    <Cell><Data ss:Type=\"String\"> </Data></Cell>\n");
                this.xls.append("    <Cell><Data ss:Type=\"String\"> </Data></Cell>\n");
                this.xls.append("    <Cell><Data ss:Type=\"String\"> </Data></Cell>\n");
                this.xls.append("   </Row>\n");

                this.xls.append("   <Row ss:AutoFitHeight=\"0\">\n");
                this.xls.append("    <Cell><Data ss:Type=\"String\">Horas extras 100%</Data></Cell>\n");
                this.xls.append("    <Cell><Data ss:Type=\"String\">"+ing_total_100+"</Data></Cell>\n");
                this.xls.append("    <Cell><Data ss:Type=\"String\"> </Data></Cell>\n");
                this.xls.append("    <Cell><Data ss:Type=\"String\"> </Data></Cell>\n");
                this.xls.append("    <Cell><Data ss:Type=\"String\"> </Data></Cell>\n");
                this.xls.append("    <Cell><Data ss:Type=\"String\"> </Data></Cell>\n");
                this.xls.append("   </Row>\n");

                try{
                    ResultSet rsIngresosEmpleado = objDB.consulta("SELECT * FROM vta_rol_pago_rubro_empleado where tipo=true and anulado=false and id_empleado="+id_empleado+
                    " and periodo between '"+fecha_ini+"' and '"+fecha_fin+"';");
                    while(rsIngresosEmpleado.next()){
                        rubro = (rsIngresosEmpleado.getString("rubro")!=null) ? rsIngresosEmpleado.getString("rubro") : "";
                        valor = (rsIngresosEmpleado.getString("valor")!=null) ? rsIngresosEmpleado.getString("valor") : "0.00";
                        this.xls.append("   <Row ss:AutoFitHeight=\"0\">\n");
                        this.xls.append("    <Cell><Data ss:Type=\"String\">"+rubro+"</Data></Cell>\n");
                        this.xls.append("    <Cell><Data ss:Type=\"String\">"+valor+"</Data></Cell>\n");
                        this.xls.append("    <Cell><Data ss:Type=\"String\"> </Data></Cell>\n");
                        this.xls.append("    <Cell><Data ss:Type=\"String\"> </Data></Cell>\n");
                        this.xls.append("    <Cell><Data ss:Type=\"String\"> </Data></Cell>\n");
                        this.xls.append("    <Cell><Data ss:Type=\"String\"> </Data></Cell>\n");
                        this.xls.append("   </Row>\n");
                        this.filas++;
                    }
                    rsIngresosEmpleado.close();
                }catch(Exception e){
                    e.printStackTrace();
                }

                this.xls.append("   <Row ss:AutoFitHeight=\"0\">\n");
                this.xls.append("    <Cell><Data ss:Type=\"String\">TOTAL INGRESOS</Data></Cell>\n");
                this.xls.append("    <Cell><Data ss:Type=\"String\">"+total_ingreso+"</Data></Cell>\n");
                this.xls.append("    <Cell><Data ss:Type=\"String\"> </Data></Cell>\n");
                this.xls.append("    <Cell><Data ss:Type=\"String\"> </Data></Cell>\n");
                this.xls.append("    <Cell><Data ss:Type=\"String\"> </Data></Cell>\n");
                this.xls.append("    <Cell><Data ss:Type=\"String\"> </Data></Cell>\n");
                this.xls.append("   </Row>\n");



                /* EGRESOS */


                this.xls.append("   <Row ss:AutoFitHeight=\"0\">\n");
                this.xls.append("    <Cell><Data ss:Type=\"String\">EGRESOS</Data></Cell>\n");
                this.xls.append("    <Cell><Data ss:Type=\"String\">VALOR</Data></Cell>\n");
                this.xls.append("    <Cell><Data ss:Type=\"String\"> </Data></Cell>\n");
                this.xls.append("    <Cell><Data ss:Type=\"String\"> </Data></Cell>\n");
                this.xls.append("    <Cell><Data ss:Type=\"String\"> </Data></Cell>\n");
                this.xls.append("    <Cell><Data ss:Type=\"String\"> </Data></Cell>\n");
                this.xls.append("   </Row>\n");

                this.xls.append("   <Row ss:AutoFitHeight=\"0\">\n");
                this.xls.append("    <Cell><Data ss:Type=\"String\">Aporte IESS</Data></Cell>\n");
                this.xls.append("    <Cell><Data ss:Type=\"String\">"+egr_iess+"</Data></Cell>\n");
                this.xls.append("    <Cell><Data ss:Type=\"String\"> </Data></Cell>\n");
                this.xls.append("    <Cell><Data ss:Type=\"String\"> </Data></Cell>\n");
                this.xls.append("    <Cell><Data ss:Type=\"String\"> </Data></Cell>\n");
                this.xls.append("    <Cell><Data ss:Type=\"String\"> </Data></Cell>\n");
                this.xls.append("   </Row>\n");

                try{
                    ResultSet rsIngresosEmpleado = objDB.consulta("SELECT * FROM vta_rol_pago_rubro_empleado where tipo=false and anulado=false and id_empleado="+id_empleado+
                    " and periodo between '"+fecha_ini+"' and '"+fecha_fin+"';");
                    while(rsIngresosEmpleado.next()){
                        rubro = (rsIngresosEmpleado.getString("rubro")!=null) ? rsIngresosEmpleado.getString("rubro") : "";
                        valor = (rsIngresosEmpleado.getString("valor")!=null) ? rsIngresosEmpleado.getString("valor") : "0.00";
                        this.xls.append("   <Row ss:AutoFitHeight=\"0\">\n");
                        this.xls.append("    <Cell><Data ss:Type=\"String\">"+rubro+"</Data></Cell>\n");
                        this.xls.append("    <Cell><Data ss:Type=\"String\">"+valor+"</Data></Cell>\n");
                        this.xls.append("    <Cell><Data ss:Type=\"String\"> </Data></Cell>\n");
                        this.xls.append("    <Cell><Data ss:Type=\"String\"> </Data></Cell>\n");
                        this.xls.append("    <Cell><Data ss:Type=\"String\"> </Data></Cell>\n");
                        this.xls.append("    <Cell><Data ss:Type=\"String\"> </Data></Cell>\n");
                        this.xls.append("   </Row>\n");
                        this.filas++;
                    }
                    rsIngresosEmpleado.close();
                }catch(Exception e){
                    e.printStackTrace();
                }

                this.xls.append("   <Row ss:AutoFitHeight=\"0\">\n");
                this.xls.append("    <Cell><Data ss:Type=\"String\">TOTAL EGRESOS</Data></Cell>\n");
                this.xls.append("    <Cell><Data ss:Type=\"String\">"+total_egreso+"</Data></Cell>\n");
                this.xls.append("    <Cell><Data ss:Type=\"String\"> </Data></Cell>\n");
                this.xls.append("    <Cell><Data ss:Type=\"String\"> </Data></Cell>\n");
                this.xls.append("    <Cell><Data ss:Type=\"String\"> </Data></Cell>\n");
                this.xls.append("    <Cell><Data ss:Type=\"String\"> </Data></Cell>\n");
                this.xls.append("   </Row>\n");

                this.xls.append("   <Row ss:AutoFitHeight=\"0\">\n");
                this.xls.append("    <Cell><Data ss:Type=\"String\">VALOR A RECIBIR</Data></Cell>\n");
                this.xls.append("    <Cell><Data ss:Type=\"String\">"+total_pagar+"</Data></Cell>\n");
                this.xls.append("    <Cell><Data ss:Type=\"String\"> </Data></Cell>\n");
                this.xls.append("    <Cell><Data ss:Type=\"String\"> </Data></Cell>\n");
                this.xls.append("    <Cell><Data ss:Type=\"String\"> </Data></Cell>\n");
                this.xls.append("    <Cell><Data ss:Type=\"String\"> </Data></Cell>\n");
                this.xls.append("   </Row>\n");


                this.xls.append("   <Row ss:AutoFitHeight=\"0\">\n");
                this.xls.append("    <Cell ss:MergeAcross=\""+(this.columnas-1)+"\" ss:StyleID=\"s62\"><Data ss:Type=\"String\"> </Data></Cell>\n");
                this.xls.append("   </Row>\n");
                this.xls.append("   <Row ss:AutoFitHeight=\"0\">\n");
                this.xls.append("    <Cell ss:MergeAcross=\""+(this.columnas-1)+"\" ss:StyleID=\"s62\"><Data ss:Type=\"String\"> </Data></Cell>\n");
                this.xls.append("   </Row>\n");
                this.xls.append("   <Row ss:AutoFitHeight=\"0\">\n");
                this.xls.append("    <Cell ss:MergeAcross=\""+(this.columnas-1)+"\" ss:StyleID=\"s62\"><Data ss:Type=\"String\"> </Data></Cell>\n");
                this.xls.append("   </Row>\n");

                this.xls.append("   <Row ss:AutoFitHeight=\"0\">\n");
                this.xls.append("    <Cell ss:MergeAcross=\""+(this.columnas-1)+"\" ss:StyleID=\"s62\"><Data ss:Type=\"String\">______________________________</Data></Cell>\n");
                this.xls.append("   </Row>\n");
                this.xls.append("   <Row ss:AutoFitHeight=\"0\">\n");
                this.xls.append("    <Cell ss:MergeAcross=\""+(this.columnas-1)+"\" ss:StyleID=\"s62\"><Data ss:Type=\"String\">Recibí conforme</Data></Cell>\n");
                this.xls.append("   </Row>\n");
                this.xls.append("   <Row ss:AutoFitHeight=\"0\">\n");
                this.xls.append("    <Cell ss:MergeAcross=\""+(this.columnas-1)+"\" ss:StyleID=\"s62\"><Data ss:Type=\"String\">C.C. "+dni+"</Data></Cell>\n");
                this.xls.append("   </Row>\n");

                this.xls.append("   <Row ss:AutoFitHeight=\"0\">\n");
                this.xls.append("    <Cell ss:MergeAcross=\""+(this.columnas-1)+"\" ss:StyleID=\"s62\"><Data ss:Type=\"String\"> </Data></Cell>\n");
                this.xls.append("   </Row>\n");
                this.xls.append("   <Row ss:AutoFitHeight=\"0\">\n");
                this.xls.append("    <Cell ss:MergeAcross=\""+(this.columnas-1)+"\" ss:StyleID=\"s62\"><Data ss:Type=\"String\"> </Data></Cell>\n");
                this.xls.append("   </Row>\n");

                this.filas += 20;
            }
            rsRolPago.close();
        }catch(Exception e){
            e.printStackTrace();
        }

        this.setPie();

        String xml = this.xls.toString();
        xml = xml.replace("<<COLUMNAS>>", String.valueOf(this.columnas));
        
        return xml.replace("<<FILAS>>", String.valueOf(this.filas+2));
    }

    public String activosPersonalizadosClientes(String subtitulo, String [] cabTabla, ResultSet registros, DataBase objDB)
    {
        this.setEncabezado();
        this.columnas = cabTabla.length;

        this.xls.append("   <Column ss:AutoFitWidth=\"0\" ss:Width=\"140.25\" ss:Span=\""+(this.columnas-2)+"\"/>\n");
        this.xls.append("   <Row ss:AutoFitHeight=\"0\">\n");
        this.xls.append("    <Cell ss:MergeAcross=\""+(this.columnas-1)+"\" ss:StyleID=\"s62\"><Data ss:Type=\"String\">"+subtitulo+"</Data></Cell>\n");
        this.xls.append("   </Row>\n");

        this.setCabeceraTabla(cabTabla);
        try{
            String codigo_activo = " ";
            String persona_recibe = " ";
            
            while(registros.next()){
                this.xls.append("   <Row ss:AutoFitHeight=\"0\">\n");

                String num_personalizacion = (registros.getString("num_personalizacion")!=null) ? registros.getString("num_personalizacion") : "";
                try{
                    ResultSet rsCustodio = objDB.consulta("select * from tbl_activo_personalizacion where num_documento="+num_personalizacion);
                    if(rsCustodio.next()){
                        persona_recibe = (rsCustodio.getString("persona_recibe")!=null) ? rsCustodio.getString("persona_recibe") : " ";
                        rsCustodio.close();
                    }
                }catch(Exception ex){
                    ex.printStackTrace();
                }
                if(persona_recibe.indexOf("&")>=0){
                    persona_recibe = persona_recibe.replace("&", "y");
                }
                this.xls.append("    <Cell><Data ss:Type=\"String\">" + persona_recibe  + "</Data></Cell>\n");
                
                codigo_activo = (registros.getString("codigo_activo")!=null) ? registros.getString("codigo_activo") : "";
                this.xls.append("    <Cell><Data ss:Type=\"String\">" + codigo_activo + "</Data></Cell>\n");
                this.xls.append("   </Row>\n");
                this.filas++;
            }
            registros.close();
        }catch(Exception e){
            e.printStackTrace();
        }

        this.setPie();

        String xml = this.xls.toString();
        xml = xml.replace("<<COLUMNAS>>", String.valueOf(this.columnas));
        
        return xml.replace("<<FILAS>>", String.valueOf(this.filas+1));
    }
    
    
    public String correos(ResultSet registros, String subtitulo)
    {
        this.setEncabezado();
        this.columnas = 2;

        this.xls.append("   <Column ss:AutoFitWidth=\"0\" ss:Width=\"140.25\" ss:Span=\""+(this.columnas-2)+"\"/>\n");
        this.xls.append("   <Row ss:AutoFitHeight=\"0\">\n");
        this.xls.append("    <Cell ss:MergeAcross=\""+(this.columnas-1)+"\" ss:StyleID=\"s62\"><Data ss:Type=\"String\">"+subtitulo+"</Data></Cell>\n");
        this.xls.append("   </Row>\n");

        this.setCabeceraTabla(new String [] {"SECTOR", "CORREOS ELECTRONICOS"});
        try{
            String aux = "";
            String columna1 = "";
            String columna2 = "";
            boolean uno = true;
            while(registros.next()){
                columna1 = (registros.getString(1)!=null) ? registros.getString(1) : "";
                columna2 += ((registros.getString(2)!=null) ? registros.getString(2) : "") + ", ";
                if(aux.compareTo(columna1)!=0 && !uno){
                    this.xls.append("   <Row ss:AutoFitHeight=\"0\">\n");
                    this.xls.append("    <Cell><Data ss:Type=\"String\">" + columna1 + "</Data></Cell>\n");
                    this.xls.append("    <Cell><Data ss:Type=\"String\">" + columna2.substring(0, columna2.length()-2) + "</Data></Cell>\n");
                    this.xls.append("   </Row>\n");
                    aux = columna1;
                   columna2 = "";
                }
                uno = false;
                this.filas++;
            }
            registros.close();
        }catch(Exception e){
            e.printStackTrace();
        }

        this.setPie();

        String xml = this.xls.toString();
        xml = xml.replace("<<COLUMNAS>>", String.valueOf(this.columnas));
        
        return xml.replace("<<FILAS>>", String.valueOf(this.filas));
    }

    public String truncar(float num)
    {
        if(num>0){
            num = num + 0.0009f;
        }
        String cad2 = String.valueOf(num).replace(".", ":");
        String cad[] = cad2.split(":");
        String res = "";
        if(cad.length>1){
            cad[1] += "000";
            res = cad[1].substring(0,2);
        }
        return cad[0]+"."+res;
    }
    
    public void setPie()
    {
        this.xls.append("  </Table>\n");
        this.xls.append("  <WorksheetOptions xmlns=\"urn:schemas-microsoft-com:office:excel\">\n");
        this.xls.append("   <PageSetup>\n");
        this.xls.append("    <Header x:Margin=\"0.3\"/>\n");
        this.xls.append("    <Footer x:Margin=\"0.3\"/>\n");
        this.xls.append("    <PageMargins x:Bottom=\"0.75\" x:Left=\"0.7\" x:Right=\"0.7\" x:Top=\"0.75\"/>\n");
        this.xls.append("   </PageSetup>\n");
        this.xls.append("   <Print>\n");
        this.xls.append("    <ValidPrinterInfo/>\n");
        this.xls.append("    <PaperSizeIndex>9</PaperSizeIndex>\n");
        this.xls.append("    <HorizontalResolution>600</HorizontalResolution>\n");
        this.xls.append("    <VerticalResolution>600</VerticalResolution>\n");
        this.xls.append("   </Print>\n");
        this.xls.append("   <Selected/>\n");
        this.xls.append("   <Panes>\n");
        this.xls.append("    <Pane>\n");
        this.xls.append("     <Number>1</Number>\n");
        this.xls.append("    </Pane>\n");
        this.xls.append("   </Panes>\n");
        this.xls.append("   <ProtectObjects>False</ProtectObjects>\n");
        this.xls.append("   <ProtectScenarios>False</ProtectScenarios>\n");
        this.xls.append("  </WorksheetOptions>\n");
        this.xls.append(" </Worksheet>\n");
        /*this.xls.append(" <Worksheet ss:Name=\"Hoja2\">\n");
        this.xls.append("  <Table ss:ExpandedColumnCount=\"1\" ss:ExpandedRowCount=\"1\" x:FullColumns=\"1\"\n");
        this.xls.append("   x:FullRows=\"1\" ss:DefaultColumnWidth=\"60\" ss:DefaultRowHeight=\"15\">\n");
        this.xls.append("  </Table>\n");
        this.xls.append("  <WorksheetOptions xmlns=\"urn:schemas-microsoft-com:office:excel\">\n");
        this.xls.append("   <PageSetup>\n");
        this.xls.append("    <Header x:Margin=\"0.3\"/>\n");
        this.xls.append("    <Footer x:Margin=\"0.3\"/>\n");
        this.xls.append("    <PageMargins x:Bottom=\"0.75\" x:Left=\"0.7\" x:Right=\"0.7\" x:Top=\"0.75\"/>\n");
        this.xls.append("   </PageSetup>\n");
        this.xls.append("   <ProtectObjects>False</ProtectObjects>\n");
        this.xls.append("   <ProtectScenarios>False</ProtectScenarios>\n");
        this.xls.append("  </WorksheetOptions>\n");
        this.xls.append(" </Worksheet>\n");
        this.xls.append(" <Worksheet ss:Name=\"Hoja3\">\n");
        this.xls.append("  <Table ss:ExpandedColumnCount=\"1\" ss:ExpandedRowCount=\"1\" x:FullColumns=\"1\"\n");
        this.xls.append("   x:FullRows=\"1\" ss:DefaultColumnWidth=\"60\" ss:DefaultRowHeight=\"15\">\n");
        this.xls.append("  </Table>\n");
        this.xls.append("  <WorksheetOptions xmlns=\"urn:schemas-microsoft-com:office:excel\">\n");
        this.xls.append("   <PageSetup>\n");
        this.xls.append("    <Header x:Margin=\"0.3\"/>\n");
        this.xls.append("    <Footer x:Margin=\"0.3\"/>\n");
        this.xls.append("    <PageMargins x:Bottom=\"0.75\" x:Left=\"0.7\" x:Right=\"0.7\" x:Top=\"0.75\"/>\n");
        this.xls.append("   </PageSetup>\n");
        this.xls.append("   <ProtectObjects>False</ProtectObjects>\n");
        this.xls.append("   <ProtectScenarios>False</ProtectScenarios>\n");
        this.xls.append("  </WorksheetOptions>\n");
        this.xls.append(" </Worksheet>\n");*/
        this.xls.append("</Workbook>\n");
    }
}