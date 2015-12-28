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

package jm.adm;

import java.io.*;
import java.sql.ResultSet;

import javax.servlet.*;
import javax.servlet.http.*;
import jm.adm.clas.Sucursal;

/**
 *
 * @author Jorge
 */
public class formatoRetencion extends HttpServlet {
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

        String id = request.getParameter("id");

        Sucursal objSucursal = new Sucursal(this._ip, this._puerto, this._db, usuario, clave);
        String frt_ret_razon_social = "88:138";
        String frt_ret_ruc = "87:156";
        String frt_ret_fecha = "407:138";
        String frt_ret_num_comp = "478:156";
        String frt_ret_direccion = "96:180";
        String frt_ret_tipo = "480:180";
        String frt_ret_detalle = "24:245";
        String frt_ret_total = "512:363";
        try{
            ResultSet rsSucursal = objSucursal.getSucursal(id);
            if(rsSucursal.next()){
                frt_ret_razon_social = rsSucursal.getString("frt_ret_razon_social")!=null ? rsSucursal.getString("frt_ret_razon_social") : "10:10";
                frt_ret_ruc = rsSucursal.getString("frt_ret_ruc")!=null ? rsSucursal.getString("frt_ret_ruc") : "10:10";
                frt_ret_fecha = rsSucursal.getString("frt_ret_fecha")!=null ? rsSucursal.getString("frt_ret_fecha") : "10:10";
                frt_ret_num_comp = rsSucursal.getString("frt_ret_num_comp")!=null ? rsSucursal.getString("frt_ret_num_comp") : "10:10";
                frt_ret_direccion = rsSucursal.getString("frt_ret_direccion")!=null ? rsSucursal.getString("frt_ret_direccion") : "10:10";
                frt_ret_tipo = rsSucursal.getString("frt_ret_tipo")!=null ? rsSucursal.getString("frt_ret_tipo") : "10:10";
                frt_ret_detalle = rsSucursal.getString("frt_ret_detalle")!=null ? rsSucursal.getString("frt_ret_detalle") : "10:10";
                frt_ret_total = rsSucursal.getString("frt_ret_total")!=null ? rsSucursal.getString("frt_ret_total") : "10:10";
                rsSucursal.close();
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        
        String v_senores[] = frt_ret_razon_social.split(":");
        String v_ruc[] = frt_ret_ruc.split(":");
        String v_fech_emi[] = frt_ret_fecha.split(":");
        String v_num_comp[] = frt_ret_num_comp.split(":");
        String v_direccion[] = frt_ret_direccion.split(":");
        String v_tipo[] = frt_ret_tipo.split(":");
        String v_detalle[] = frt_ret_detalle.split(":");
        String v_total[] = frt_ret_total.split(":");
        try {
            out.println("<html>");
            out.println("<head>");
            out.println("<title>FORMATO DE RETENCION</title>"); 
            out.println("<script>var documento='r';"+
                    "var id="+id+";" +
                    "var v_senores=Array("+v_senores[0]+", "+v_senores[1]+");" +
                    "var v_ruc=Array("+v_ruc[0]+", "+v_ruc[1]+");" +
                    "var v_fech_emi=Array("+v_fech_emi[0]+", "+v_fech_emi[1]+");" +
                    "var v_num_comp=Array("+v_num_comp[0]+", "+v_num_comp[1]+");" +
                    "var v_direccion=Array("+v_direccion[0]+", "+v_direccion[1]+");" +
                    "var v_tipo=Array("+v_tipo[0]+", "+v_tipo[1]+");" +
                    "var v_detalle=Array("+v_detalle[0]+", "+v_detalle[1]+");" +
                    "var v_total=Array("+v_total[0]+", "+v_total[1]+");</script>");
            out.println("<SCRIPT type=\"text/javascript\" src=\"formato.js\"></SCRIPT>"); 
            out.println("<style>");
            out.println("BODY{background-image:url('img/cuadricula.gif');}");
            out.println("SPAN,TABLE{position:absolute;font-size:12px;font-family:Arial, Helvetica, sans-serif;padding:4px;}");
            out.println("#imp{font-family:Arial,Helvetica,sans-serif;font-size:11px;top:5px;float:right;}");
            out.println(".txt{background-color:#CEEFFF;border-color:#00A3F0;color:#FF5300;cursor:move;border-width:1px;border-style:solid;padding:3px;}");
            out.println("</style>");
            out.println("</head>");
            out.println("<body>");
            out.println("<button id='imp' onclick=\"window.open('pdfComprobanteRetencion?id=-2', '_blank', 'top=50,left=50,width=750,height=500,location=no');\">Imprimir comprobante de prueba</button>");
            out.println("<span id='frt_ret_razon_social' onmouseover=\"this.className='txt';\" onmouseout=\"this.className=''\" onmousedown=\"iniMover(event, 'frt_ret_razon_social');\" title=\"nombre / raz&oacute;n social\">CONSUMIDOR FINAL </span>");
            out.println("<span id='frt_ret_ruc' onmouseover=\"this.className='txt';\" onmouseout=\"this.className=''\" onmousedown=\"iniMover(event, 'frt_ret_ruc');\" title=\"RUC\" alt=\"RUC\">9999999999999 </span>");
            out.println("<span id='frt_ret_fecha' onmouseover=\"this.className='txt';\" onmouseout=\"this.className=''\" onmousedown=\"iniMover(event, 'frt_ret_fecha');\" title=\"fecha de emisi&oacute;n\">01/01/2010 </span>");
            out.println("<span id='frt_ret_num_comp' onmouseover=\"this.className='txt';\" onmouseout=\"this.className=''\" onmousedown=\"iniMover(event, 'frt_ret_num_comp');\" title=\"N&uacute;m. de comprobante de venta\">001-001-000001 </span>");
            out.println("<span id='frt_ret_direccion' onmouseover=\"this.className='txt';\" onmouseout=\"this.className=''\" onmousedown=\"iniMover(event, 'frt_ret_direccion');\" title=\"direcci&oacute;n\">Calle Ibarra Nro. 0-00 </span>");
            out.println("<span id='frt_ret_tipo' onmouseover=\"this.className='txt';\" onmouseout=\"this.className=''\" onmousedown=\"iniMover(event, 'frt_ret_tipo');\" title=\"tipo de comprobante\">FACTURA </span>");
            out.println("<table width='490' id='frt_ret_detalle' onmouseover=\"this.className='txt';\" onmouseout=\"this.className=''\" onmousedown=\"iniMover(event, 'frt_ret_detalle');\">" +
                    "<tr align='center'><td align='left'>2010</td><td>100</td><td>RENTA</td><td>304</td><td>8 %</td><td align='right'>8.00</td></tr>" +
                    "<tr align='center'><td align='left'>2010</td><td>12</td><td>IVA</td><td> - </td><td>70 %</td><td align='right'>8.40</td></tr></table>");
            out.println("<span id='frt_ret_total' onmouseover=\"this.className='txt';\" onmouseout=\"this.className=''\" onmousedown=\"iniMover(event, 'frt_ret_total');\" title=\"total de la retenci&oacute;n\"> 16.40 </span>");
            out.println("</body>");
            out.println("</html>");
        } finally {
            objSucursal.cerrar();
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
