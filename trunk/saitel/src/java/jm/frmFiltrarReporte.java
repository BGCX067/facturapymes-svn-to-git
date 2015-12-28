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

import java.io.*;

import javax.servlet.*;
import javax.servlet.http.*;
import java.sql.ResultSet;
import jm.web.DataBase;
import jm.web.DatosDinamicos;
import jm.web.Fecha;

/**
 *
 * @author Jorge
 */
public class frmFiltrarReporte extends HttpServlet {
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
        
        response.setContentType("text/html;charset=UTF-8");
        response.setHeader("Pragma", "no-cache"); 
        response.setHeader("Expires", "Mon, 01 Jan 2001 00:00:01 GMT"); 
        response.setHeader("Cache-Control", "no-store"); 
        response.setHeader("Cache-Control", "must-revalidate"); 
        response.setHeader("Cache-Control", "no-cache");
        PrintWriter out = response.getWriter();
        
        DataBase objDB = new DataBase(this._ip, this._puerto, this._db, usuario, clave);
        
        try {
            int op = Integer.parseInt(request.getParameter("op"));
            String ini_mes = String.valueOf(Fecha.getAnio()) + "-" + String.valueOf(Fecha.getMes()) + "-01";
            String hoy_mes = Fecha.getFecha("ISO");
            String html = "";
            html += "<input type='hidden' id='op' value='" + op + "'>";
            html += "<table class='formulario' width='100%'>";
            switch(op){
                case 1:
                    html += "<tr><td align='center' colspan='2' class='subTitulo'>Lista de contribuyentes</td></tr>";
                break;
                case 2:
                    html += "<tr><td align='center' colspan='2' class='subTitulo'>Lista de contribuyentes con sus respectivos medidores</td></tr>";
                break;
                case 3:
                    html += "<tr><td align='center' colspan='2' class='subTitulo'>Novedades de medidores</td></tr>";
                break;
                case 4:
                    html += "<tr><td align='center' colspan='2' class='subTitulo'>Consumo de macro-medidores</td></tr>";     
                break;
                case 5:
                    html += "<tr><td align='center' colspan='2' class='subTitulo'>Impresión de copia de factura</td></tr>";
                break;
                case 6:
                     html += "<tr><td align='center' colspan='2' class='subTitulo'>Consumos de agua</td></tr>";    
                break;
                case 7:
                     html += "<tr><td align='center' colspan='2' class='subTitulo'>Reacudo diario</td></tr>";
                break;
                case 8:
                     html += "<tr><td align='center' colspan='2' class='subTitulo'>Valores recaudados mensuales</td></tr>";    
                break;
                case 9:
                     html += "<tr><td align='center' colspan='2' class='subTitulo'>Valores recaudados por sectores agrupado por per&iacute;odo facturado</td></tr>";    
                break;
                case 10:
                     html += "<tr><td align='center' colspan='2' class='subTitulo'>Detalle de rubros recaudados</td></tr>";    
                break;
                case 11:
                     html += "<tr><td align='center' colspan='2' class='subTitulo'>Detalle de valores recaudados por otros rubros</td></tr>";   
                break;
                case 12:
                     html += "<tr><td align='center' colspan='2' class='subTitulo'>Detalle de valores recaudados por descuentos</td></tr>";     
                break;
                case 13:
                     html += "<tr><td align='center' colspan='2' class='subTitulo'>Valores por recaudar mensuales</td></tr>";
                break;
                case 14:
                     html += "<tr><td align='center' colspan='2' class='subTitulo'>Lista de Proveedores</td></tr>";
                break;
                case 15:
                     html += "<tr><td align='center' colspan='2' class='subTitulo'>Lista de Art&iacute;culos</td></tr>";
                break;
                case 17:
                     html += "<tr><td align='center' colspan='2' class='subTitulo'>Listado de facturas por proveedor</td></tr>";
                break;
                case 18:
                     html += "<tr><td align='center' colspan='2' class='subTitulo'>Listado de compras por proveedor</td></tr>";
                break;
                case 19:
                     html += "<tr><td align='center' colspan='2' class='subTitulo'>Listado de materiales comprados</td></tr>";
                break;
                case 20:
                     html += "<tr><td align='center' colspan='2' class='subTitulo'>Listado de notas de cr&eacute;dito</td></tr>";
                break;
                case 21:
                     html += "<tr><td align='center' colspan='2' class='subTitulo'>Balance de Comprobaci&oacute;n</td></tr>";
                break;
                case 22:
                     html += "<tr><td align='center' colspan='2' class='subTitulo'>Balance General</td></tr>";
                break;
                case 23:
                     html += "<tr><td align='center' colspan='2' class='subTitulo'>Estado de Resultados</td></tr>";
                break;
            }
            
            
            html += "<tr><td colspan='2'>&nbsp;</td></tr>";
            
            
            switch(op){
                case 9:
                    ResultSet sectores = objDB.consulta("select * from vta_sector;"); 
                    html += "<tr><td>Sector: </td><td>"+DatosDinamicos.combo(sectores, "idS", "", "")+"</td></tr>";
                    try{
                        sectores.close();
                    }catch(Exception e){
                        e.printStackTrace();
                    }
                break; 
                case 11:
                    ResultSet rubros = objDB.consulta("select id_rubro,rubro from tbl_rubro;"); 
                    html += "<tr><td>Rubro: </td><td>"+DatosDinamicos.combo(rubros, "idS", "", "")+"</td></tr>";
                    try{
                        rubros.close();
                    }catch(Exception e){
                        e.printStackTrace();
                    }
                break; 
                case 12:
                    ResultSet descuentos = objDB.consulta("select id_descuento,descuento from tbl_descuento;"); 
                    html += "<tr><td>Descuento: </td><td>"+DatosDinamicos.combo(descuentos, "idS", "", "")+"</td></tr>";
                    try{
                        descuentos.close();
                    }catch(Exception e){
                        e.printStackTrace();
                    }
                break; 
            }
            switch(op){
                case 2:
                    String [][] sectores = {{"0","Sectores de los medidores"},{"1","Sectores de los contribuyentes"}};
                    html += "<tr><td>Agrupado por: </td><td>" + DatosDinamicos.combo("op2", "", sectores, "") + "</td></tr>"; 
                break;
                
                case 3:
                    String [][] novedades = {{"0","Reportadas"},{"1","Solucionadas"}};
                    html += "<tr><td>Novedades:</td><td>" + DatosDinamicos.combo("op2", "", novedades, "") + "</td></tr>"; 
                    html += "<tr><td>Desde:</td>";                
                    html += "<td><input id='fi' type='text' size='10' readonly='readonly' value='"+ini_mes+"'>" +
                            "<input type='button' value='...' onClick=\"SelectorFecha.crear('fi', 'ISO');\"/></td></tr>";
                    html += "<tr><td>Hasta:</td>";                
                    html += "<td><input id='ff' type='text' size='10' readonly='readonly' value='"+hoy_mes+"'>" +
                            "<input type='button' value='...' onClick=\"SelectorFecha.crear('ff', 'ISO');\"/></td></tr>";
                break;
                case 6:
                case 10:
                case 11:
                case 12:
                    String [][] agrupado = {{"0","Per&iacute;odo facturado"},{"1","Sectores"},{"2","Meses"}};
                    html += "<tr><td>Agrupado por:</td><td>" + DatosDinamicos.combo("op2", "", agrupado, "") + "</td></tr>"; 
                    html += "<tr><td>Desde:</td>";                
                    html += "<td><input id='fi' type='text' size='10' readonly='readonly' value='"+ini_mes+"'>" +
                            "<input type='button' value='...' onClick=\"SelectorFecha.crear('fi', 'ISO');\"/></td></tr>";
                    html += "<tr><td>Hasta:</td>";                
                    html += "<td><input id='ff' type='text' size='10' readonly='readonly' value='"+hoy_mes+"'>" +
                            "<input type='button' value='...' onClick=\"SelectorFecha.crear('ff', 'ISO');\"/></td></tr>";
                break;
                case 7:
                    String [][] agrupado2 = {{"0","Per&iacute;odo facturado"},{"1","Sectores"}};
                    html += "<tr><td>Agrupado por:</td><td>" + DatosDinamicos.combo("op2", "", agrupado2, "") + "</td></tr>"; 
                    html += "<tr><td>Desde:</td>";                
                    html += "<td><input id='fi' type='text' size='10' readonly='readonly' value='"+hoy_mes+"'>" +
                            "<input type='button' value='...' onClick=\"SelectorFecha.crear('fi', 'ISO');\"/></td></tr>";
                break;
                case 8:
                case 13:
                    String [][] agrupado3 = {{"0","Per&iacute;odo facturado"},{"1","Sectores"}};
                    html += "<tr><td>Agrupado por:</td><td>" + DatosDinamicos.combo("op2", "", agrupado3, "") + "</td></tr>"; 
                    html += "<tr><td>Desde:</td>";                
                    html += "<td><input id='fi' type='text' size='10' readonly='readonly' value='"+ini_mes+"'>" +
                            "<input type='button' value='...' onClick=\"SelectorFecha.crear('fi', 'ISO');\"/></td></tr>";
                    html += "<tr><td>Hasta:</td>";                
                    html += "<td><input id='ff' type='text' size='10' readonly='readonly' value='"+hoy_mes+"'>" +
                            "<input type='button' value='...' onClick=\"SelectorFecha.crear('ff', 'ISO');\"/></td></tr>";
                break;
                case 4:
                case 9:
                case 17:
                case 18:
                case 19:    
                case 20:
                case 21:
                case 23:    
                    html += "<tr><td>Desde:</td>";                
                    html += "<td><input id='fi' type='text' size='10' readonly='readonly' value='"+ini_mes+"'>" +
                            "<input type='button' value='...' onClick=\"SelectorFecha.crear('fi', 'ISO');\"/></td></tr>";
                    html += "<tr><td>Hasta:</td>";                
                    html += "<td><input id='ff' type='text' size='10' readonly='readonly' value='"+hoy_mes+"'>" +
                            "<input type='button' value='...' onClick=\"SelectorFecha.crear('ff', 'ISO');\"/></td></tr>";
                break;
                case 16:
                    ResultSet provedores = objDB.consulta("select id_proveedor,razon_social from vta_proveedor;"); 
                    html += "<tr><td>Proveedor: </td><td>"+DatosDinamicos.combo(provedores, "idS", "", "", " - TODOS - ")+"</td></tr>";
                    try{
                        provedores.close();
                    }catch(Exception e){
                        e.printStackTrace();
                    }
                break;
                case 22:
                    html += "<tr><td>Hasta la fecha:</td>";                
                    html += "<td><input id='fi' type='text' size='10' readonly='readonly' value='"+hoy_mes+"'>" +
                            "<input type='button' value='...' onClick=\"SelectorFecha.crear('fi', 'ISO');\"/></td></tr>";                    
                break;
            }
            
            html += "<tr><td>Tipo de archivo: </td>";
            
            if(op==5){
                html += "<td><input type='radio' id='x' name='x' checked /> PDF </td></tr>";
                html += "<tr><td colspan='2'><br><input type='button' value='Imprimir' onclick=\"com_reporteImprimir(" + op + ");\" /></td></tr>";
            }else{
                html += "<td><input type='radio' id='x' name='x' checked /> PDF &nbsp;&nbsp;&nbsp;&nbsp;&nbsp; <input type='radio' id='x2' name='x' /> Excel</td></tr>";
                html += "<tr><td colspan='2'><br><input type='button' value='Imprimir' onclick=\"com_reporteImprimir(" + op + ");\" /></td></tr>";
            }
            
            html += "</table>";
            out.println(html);
            
            
        } finally { 
            objDB.cerrar();
            out.close();
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
