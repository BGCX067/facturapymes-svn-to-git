/**
** @version 1.0
** @package FACTURAPYMES.
* @author Jorge Washington Mueses Cevallos.
* @copyright Copyright (C) 2011 por Jorge Mueses. Todos los derechos reservados.
* @license http://www.gnu.org/copyleft/gpl.html GNU/GPL.
** FACTURAPYMES es un software de libre distribuciÃ³n, que puede ser
* copiado y distribuido bajo los tÃ©rminos de la Licencia
* Attribution-NonCommercial-NoDerivs 3.0 Unported,
* de acuerdo con la publicada por la CREATIVE COMMONS CORPORATION.
*/

package jm.fac.xls;

import java.io.*;

import javax.servlet.*;
import javax.servlet.http.*;
import java.sql.ResultSet;

import jm.web.DataBase;
import jm.web.Fecha;

/**
 *
 * @author Jorge
 */
public class Debitos extends HttpServlet {
    private String _ip = null;
    private int _puerto = 5432;
    private String _db = null;

    public void init(ServletConfig config) throws ServletException
    {
        this._ip = config.getServletContext().getInitParameter("_IP");
        this._puerto = Integer.parseInt(config.getServletContext().getInitParameter("_PUERTO"));
        this._db = config.getServletContext().getInitParameter("_DB");
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
        
        if(request.getHeader("User-Agent").toLowerCase().contains("windows")){
            response.setContentType("application/vnd.ms-excel;");
            response.setHeader("Content-disposition", "inline; filename=reporte.xls;");
        }else{
            response.setContentType("text/xml;");
            response.setHeader("Content-disposition", "attachment; filename=reporte.ods;");
        }
        response.setHeader("Pragma", "no-cache");
        response.setHeader("Expires", "Mon, 01 Jan 2001 00:00:01 GMT");
        response.setHeader("Cache-Control", "no-store");
        response.setHeader("Cache-Control", "must-revalidate");
        response.setHeader("Cache-Control", "no-cache");
        PrintWriter out = response.getWriter();

        String comision = request.getParameter("comision");
        int anio = Integer.parseInt(request.getParameter("anio"));
        int mes = Integer.parseInt(request.getParameter("mes"));
        String fecha_ini = anio + "-" + mes + "-01";
        String fecha_fin = anio + "-" + mes + "-" + Fecha.getUltimoDiaMes(anio, mes);

        DataBase objDB = new DataBase(this._ip, this._puerto, this._db, usuario, clave);

        try{
            ResultSet registros = objDB.consulta("select total + "+comision+" as total, C.forma_pago, tipo_cuenta, num_cuenta, tipo_doc_debito, documento, cliente_debito, subtotal, autorizacion, num_factura "
                    + "from tbl_cliente as C inner join tbl_factura_venta as F on C.id_cliente=F.id_cliente "
                    + "where fecha_emision between '"+fecha_ini+"' and '"+fecha_fin+"' and F.deuda>0 and num_cuenta <>'' and C.forma_pago='CTA' and F.anulado=false");

            out.print("<?xml version=\"1.0\" encoding=\"iso-8859-1\"?>\n");
            out.print("<?mso-application progid=\"Excel.Sheet\"?>\n");
            out.print("<Workbook xmlns=\"urn:schemas-microsoft-com:office:spreadsheet\"\n");
            out.print(" xmlns:o=\"urn:schemas-microsoft-com:office:office\"\n");
            out.print(" xmlns:x=\"urn:schemas-microsoft-com:office:excel\"\n");
            out.print(" xmlns:ss=\"urn:schemas-microsoft-com:office:spreadsheet\"\n");
            out.print(" xmlns:html=\"http://www.w3.org/TR/REC-html40\">\n");
            out.print(" <DocumentProperties xmlns=\"urn:schemas-microsoft-com:office:office\">\n");
            out.print("  <Author>FACTURAPYMES</Author>\n");
            out.print("  <LastAuthor>FACTURAPYMES</LastAuthor>\n");
            out.print("  <Created>2010-03-05T18:37:44Z</Created>\n");
            out.print("  <LastSaved>2010-03-05T18:40:43Z</LastSaved>\n");
            out.print("  <Company>FACTURAPYMES</Company>\n");
            out.print("  <Version>3.00</Version>\n");
            out.print(" </DocumentProperties>\n");
            out.print(" <ExcelWorkbook xmlns=\"urn:schemas-microsoft-com:office:excel\">\n");
            out.print("  <WindowHeight>10200</WindowHeight>\n");
            out.print("  <WindowWidth>16455</WindowWidth>\n");
            out.print("  <WindowTopX>240</WindowTopX>\n");
            out.print("  <WindowTopY>30</WindowTopY>\n");
            out.print("  <ProtectStructure>False</ProtectStructure>\n");
            out.print("  <ProtectWindows>False</ProtectWindows>\n");
            out.print(" </ExcelWorkbook>\n");
            out.print(" <Styles>\n");
            out.print("  <Style ss:ID=\"Default\" ss:Name=\"Normal\">\n");
            out.print("   <Alignment ss:Vertical=\"Bottom\"/>\n");
            out.print("   <Borders/>\n");
            out.print("   <Font ss:FontName=\"Calibri\" x:Family=\"Swiss\" ss:Size=\"11\" ss:Color=\"#000000\"/>\n");
            out.print("   <Interior/>\n");
            out.print("   <NumberFormat/>\n");
            out.print("   <Protection/>\n");
            out.print("  </Style>\n");
            out.print("  <Style ss:ID=\"s62\">\n");
            out.print("   <Alignment ss:Horizontal=\"Center\" ss:Vertical=\"Bottom\"/>\n");
            out.print("   <Font ss:FontName=\"Calibri\" x:Family=\"Swiss\" ss:Size=\"11\" ss:Color=\"#000000\"\n");
            out.print("    ss:Bold=\"1\"/>\n");
            out.print("  </Style>\n");
            out.print("  <Style ss:ID=\"s63\">");
            out.print("  <NumberFormat ss:Format=\"0000000000000\"/>");
            out.print("  </Style>");
            out.print("  <Style ss:ID=\"s65\">");
            out.print("     <NumberFormat ss:Format=\"0000000000\"/>");
            out.print("  </Style>");
            out.print(" </Styles>\n");
            out.print(" <Worksheet ss:Name=\"Hoja1\">\n");


    //////////////////////////////////////////////////////////////////////////////////////////////////////////////

            String xls = "";
            int filas = 1;
            int columnas = 19;
            xls+="  <Table ss:ExpandedColumnCount=\""+String.valueOf(columnas)+"\" ss:ExpandedRowCount=\"<<FILAS>>\" x:FullColumns=\"1\"\n";
            xls+="   x:FullRows=\"1\" ss:DefaultColumnWidth=\"50\" ss:DefaultRowHeight=\"15\">\n";
            xls+="   <Column ss:AutoFitWidth=\"0\" ss:Width=\"49.5\"/>\n";

            try{
                String total = "0";
                String forma_pago = "";
                String tipo_cuenta = "";
                String num_cuenta = "";
                String tipo_doc_debito = "";
                String documento = "";
                String cliente_debito = "";
                String subtotal = "";
                String autorizacion = "";
                String num_factura = "";
                while(registros.next()){
                    total = registros.getString("total")!=null ? registros.getString("total").replace(".", "") : "0";
                    forma_pago = registros.getString("forma_pago")!=null ? registros.getString("forma_pago") : "CTA";
                    tipo_cuenta = registros.getString("tipo_cuenta")!=null ? registros.getString("tipo_cuenta") : "AHO";
                    num_cuenta = registros.getString("num_cuenta")!=null ? registros.getString("num_cuenta") : "";
                    tipo_doc_debito = registros.getString("tipo_doc_debito")!=null ? registros.getString("tipo_doc_debito") : "";
                    documento = registros.getString("documento")!=null ? registros.getString("documento") : "";
                    cliente_debito = registros.getString("cliente_debito")!=null ? registros.getString("cliente_debito") : "";
                    subtotal = registros.getString("subtotal")!=null ? registros.getString("subtotal").replace(".", "") : "0";
                    autorizacion = registros.getString("autorizacion")!=null ? registros.getString("autorizacion") : "";
                    num_factura = registros.getString("num_factura")!=null ? registros.getString("num_factura") : "";

                    xls+="   <Row ss:AutoFitHeight=\"0\">\n";
                    //xls+="    <Cell><Data ss:Type=\"Number\">" + filas + "</Data></Cell>\n";
                        xls+="    <Cell><Data ss:Type=\"String\">CO</Data></Cell>\n";
                        xls+="    <Cell><Data ss:Type=\"Number\">" + filas + "</Data></Cell>\n";
                        xls+="    <Cell><Data ss:Type=\"String\">USD</Data></Cell>\n";
                        xls+="    <Cell><Data ss:Type=\"Number\">" + total + "</Data></Cell>\n";
                        xls+="    <Cell><Data ss:Type=\"String\">" + forma_pago + "</Data></Cell>\n";
                        xls+="    <Cell><Data ss:Type=\"String\">" + tipo_cuenta + "</Data></Cell>\n";
                        xls+="    <Cell><Data ss:Type=\"Number\">" + num_cuenta + "</Data></Cell>\n";
                        xls+="    <Cell><Data ss:Type=\"String\">PAGO</Data></Cell>\n";
                        xls+="    <Cell><Data ss:Type=\"String\">" + tipo_doc_debito + "</Data></Cell>\n";
                        xls+="    <Cell ss:StyleID=\""+(documento.length()>10 ? "s63" : "s65")+"\"><Data ss:Type=\"Number\">" + documento + "</Data></Cell>\n";
                        xls+="    <Cell><Data ss:Type=\"String\">" + this.quitarEspeciales(cliente_debito) + "</Data></Cell>\n";
                        xls+="    <Cell><Data ss:Type=\"Number\">" + subtotal + "</Data></Cell>\n";
                        xls+="    <Cell><Data ss:Type=\"String\"> </Data></Cell>\n";
                        xls+="    <Cell><Data ss:Type=\"String\"> </Data></Cell>\n";
                        xls+="    <Cell><Data ss:Type=\"String\"> </Data></Cell>\n";
                        xls+="    <Cell><Data ss:Type=\"String\"> </Data></Cell>\n";
                        xls+="    <Cell><Data ss:Type=\"Number\">" + autorizacion + "</Data></Cell>\n";
                        xls+="    <Cell><Data ss:Type=\"Number\">" + num_factura + "</Data></Cell>\n";
                    xls+="   </Row>\n";
                    filas++;
                }
                registros.close();
            }catch(Exception e){
                e.printStackTrace();
            }
            xls = xls.replace("<<FILAS>>", String.valueOf(filas-1));
            out.print(xls);


    //////////////////////////////////////////////////////////////////////////////////////////////////////////////
            out.print("  </Table>\n");
            out.print("  <WorksheetOptions xmlns=\"urn:schemas-microsoft-com:office:excel\">\n");
            out.print("   <PageSetup>\n");
            out.print("    <Header x:Margin=\"0.3\"/>\n");
            out.print("    <Footer x:Margin=\"0.3\"/>\n");
            out.print("    <PageMargins x:Bottom=\"0.75\" x:Left=\"0.7\" x:Right=\"0.7\" x:Top=\"0.75\"/>\n");
            out.print("   </PageSetup>\n");
            out.print("   <Print>\n");
            out.print("    <ValidPrinterInfo/>\n");
            out.print("    <PaperSizeIndex>9</PaperSizeIndex>\n");
            out.print("    <HorizontalResolution>600</HorizontalResolution>\n");
            out.print("    <VerticalResolution>600</VerticalResolution>\n");
            out.print("   </Print>\n");
            out.print("   <Selected/>\n");
            out.print("   <Panes>\n");
            out.print("    <Pane>\n");
            out.print("     <Number>1</Number>\n");
            out.print("    </Pane>\n");
            out.print("   </Panes>\n");
            out.print("   <ProtectObjects>False</ProtectObjects>\n");
            out.print("   <ProtectScenarios>False</ProtectScenarios>\n");
            out.print("  </WorksheetOptions>\n");
            out.print(" </Worksheet>\n");
            out.print("</Workbook>\n");

        
            registros.close();
        }catch(Exception e){
            e.printStackTrace();
        }finally{
            objDB.cerrar();
        }
    }


    public String quitarEspeciales(String cad)
    {
        cad = cad.replace("&", " ");
        cad = cad.replace("ü", " ");
        cad = cad.replace("Ü", " ");
        cad = cad.replace("%", " ");
        cad = cad.replace("#", " ");
        cad = cad.replace("¿", " ");
        cad = cad.replace( "¡", " ");
        cad = cad.replace("\"", " ");
        cad = cad.replace("Ñ", "N");
        cad = cad.replace("ñ", "N");
        cad = cad.replace("Ú", "U");
        cad = cad.replace("Ó", "O");
        cad = cad.replace("Í", "I");
        cad = cad.replace("É", "E");
        cad = cad.replace("Á", "A");
        cad = cad.replace("ú", "U");
        cad = cad.replace("ó", "O");
        cad = cad.replace("í", "I");
        cad = cad.replace("é", "E");
        cad = cad.replace("á", "A");
        return cad;
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
