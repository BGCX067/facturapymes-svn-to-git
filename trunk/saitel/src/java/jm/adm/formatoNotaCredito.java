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
public class formatoNotaCredito extends HttpServlet {
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
        String frt_not_cre_ruc = "189:92";
        String frt_not_cre_razon_social = "23:92";
        String frt_not_cre_fecha = "32:120";
        String frt_not_cre_modifica = "37:106";
        String frt_not_cre_concepto = "2:165";
        String frt_not_cre_valor = "209:315";
        String frt_not_cre_iva_0 = "209:343";
        String frt_not_cre_iva_12 = "209:357";
        String frt_not_cre_total = "209:370";
        int frt_not_cre_anch_concepto = 225;
        int anchos = 225;

        try{
            ResultSet rsSucursal = objSucursal.getSucursal(id);
            if(rsSucursal.next()){
                frt_not_cre_razon_social = rsSucursal.getString("frt_not_cre_razon_social")!=null ? rsSucursal.getString("frt_not_cre_razon_social") : "23:92";
                frt_not_cre_ruc = rsSucursal.getString("frt_not_cre_ruc")!=null ? rsSucursal.getString("frt_not_cre_ruc") : "189:92";
                frt_not_cre_fecha = rsSucursal.getString("frt_not_cre_fecha")!=null ? rsSucursal.getString("frt_not_cre_fecha") : "32:120";
                frt_not_cre_modifica = rsSucursal.getString("frt_not_cre_modifica")!=null ? rsSucursal.getString("frt_not_cre_modifica") : "37:106";
                frt_not_cre_concepto = rsSucursal.getString("frt_not_cre_concepto")!=null ? rsSucursal.getString("frt_not_cre_concepto") : "2:165";
                frt_not_cre_valor = rsSucursal.getString("frt_not_cre_valor")!=null ? rsSucursal.getString("frt_not_cre_valor") : "209:315";
                frt_not_cre_iva_0 = rsSucursal.getString("frt_not_cre_iva_0")!=null ? rsSucursal.getString("frt_not_cre_iva_0") : "209:343";
                frt_not_cre_iva_12 = rsSucursal.getString("frt_not_cre_iva_12")!=null ? rsSucursal.getString("frt_not_cre_iva_12") : "209:357";
                frt_not_cre_total = rsSucursal.getString("frt_not_cre_total")!=null ? rsSucursal.getString("frt_not_cre_total") : "209:370";
                frt_not_cre_anch_concepto = rsSucursal.getString("frt_not_cre_anch_concepto")!=null ? rsSucursal.getInt("frt_not_cre_anch_concepto") : 225;
                anchos = frt_not_cre_anch_concepto + 42;
                rsSucursal.close();
            }
        }catch(Exception e){
            e.printStackTrace();
        }

        String v_senores[] = frt_not_cre_razon_social.split(":");
        String v_ruc[] = frt_not_cre_ruc.split(":");
        String v_fech_emi[] = frt_not_cre_fecha.split(":");
        String v_direccion[] = frt_not_cre_modifica.split(":");
        String v_detalle[] = frt_not_cre_concepto.split(":");
        String v_subtotal[] = frt_not_cre_valor.split(":");
        String v_iva_0[] = frt_not_cre_iva_0.split(":");
        String v_iva_12[] = frt_not_cre_iva_12.split(":");
        String v_total[] = frt_not_cre_total.split(":");

        try {
            out.println("<html>");
            out.println("<head>");
            out.println("<title>FORMATO DE NOTA DE CREDITO</title>");
            out.println("<script>var documento='c';"+
                    "var id="+id+";" +
                    "var v_senores=Array("+v_senores[0]+", "+v_senores[1]+");" +
                    "var v_ci=Array("+v_ruc[0]+", "+v_ruc[1]+");" +
                    "var v_fech_emi=Array("+v_fech_emi[0]+", "+v_fech_emi[1]+");" +
                    "var v_modifica=Array("+v_direccion[0]+", "+v_direccion[1]+");" +
                    "var v_concepto=Array("+v_detalle[0]+", "+v_detalle[1]+");" +
                    "var v_valor=Array("+v_subtotal[0]+", "+v_subtotal[1]+");" +
                    "var v_iva_0=Array("+v_iva_0[0]+", "+v_iva_0[1]+");" +
                    "var v_iva_12=Array("+v_iva_12[0]+", "+v_iva_12[1]+");" +
                    "var v_total=Array("+v_total[0]+", "+v_total[1]+");</script>");
            out.println("<SCRIPT type=\"text/javascript\" src=\"formato.js\"></SCRIPT>");
            out.println("<style>");
            out.println("BODY{background-image:url('img/cuadricula.gif');}");
            out.println("SPAN,TABLE{position:absolute;font-size:12px;font-family:Arial, Helvetica, sans-serif;padding:4px;}");
            out.println("#imp{font-family:Arial,Helvetica,sans-serif;font-size:11px;top:5px;float:right;}");
            out.println(".txt{background-color:#CEEFFF;cursor:move;color:#FF5300;border:1px solid #00A3F0;padding:3px;}");
            out.println(".txt1{background-color:#CEEFFF;cursor:move;color:#FF5300;border:1px solid #00A3F0;}");
            out.println(".txt2{border:1px solid transparent;text-align:right;width:60px;}");
            out.println(".txt3{background-color:#CEEFFF;cursor:move;color:#FF5300;border:1px solid #00A3F0;text-align:right;width:60px;}");
            out.println(".red{cursor:w-resize;background-color:#9BB6C6;}");
            out.println("</style>");
            out.println("</head>");
            out.println("<body>");
            out.println("<button id='imp' onclick=\"window.open('pdfNotaCredito?id=-2', '_blank', 'top=5,left=50,width=750,height=500,location=no');\">Imprimir nota de cr&eacute;dito de prueba</button>");
            out.println("<span id='frt_not_cre_razon_social' onmouseover=\"this.className='txt';\" onmouseout=\"this.className=''\" onmousedown=\"iniMover(event, 'frt_not_cre_razon_social');\" title=\"nombre / raz&oacute;n social\">RAZON SOCIAL</span>");
            out.println("<span id='frt_not_cre_ruc' onmouseover=\"this.className='txt';\" onmouseout=\"this.className=''\" onmousedown=\"iniMover(event, 'frt_not_cre_ruc');\" title=\"ci o ruc\">9999999999 </span>");
            out.println("<span id='frt_not_cre_fecha' onmouseover=\"this.className='txt';\" onmouseout=\"this.className=''\" onmousedown=\"iniMover(event, 'frt_not_cre_fecha');\" title=\"fecha de emisi&oacute;n\">01/01/2010 </span>");
            out.println("<span id='frt_not_cre_modifica' onmouseover=\"this.className='txt';\" onmouseout=\"this.className=''\" onmousedown=\"iniMover(event, 'frt_not_cre_modifica');\" title=\"comprobante que modifica\">Factura No. 000-000-0 </span>");
            out.println("<table height='50' cellpadding='0' cellspacing='0' style='width:"+anchos+"px;' id='frt_not_cre_concepto' "
                    + "onmouseover=\"this.className='txt1';_('r1').className='red';\" onmouseout=\"this.className=_('r1').className='';\">"+
                    "<tr valign='top'>" +
                    "<td id='frt_not_cre_anch_concepto' style='width:"+frt_not_cre_anch_concepto+"px;' onmousedown=\"iniMover(event, 'frt_not_cre_concepto');\">RAZON DE LA MODIFICACION</td>"
                    + "<td id='r1' width='10' onmousedown=\"iniRedimensionar('frt_not_cre_anch_concepto')\">&nbsp;</td>"+
                    "</tr></table>");
            out.println("<span id='frt_not_cre_valor' class='txt2' onmouseover=\"this.className='txt3';\" onmouseout=\"this.className='txt2'\" onmousedown=\"iniMover(event, 'frt_not_cre_valor');\" title=\"valor\"> 100,00 </span>");
            out.println("<span id='frt_not_cre_iva_0' class='txt2' onmouseover=\"this.className='txt3';\" onmouseout=\"this.className='txt2'\" onmousedown=\"iniMover(event, 'frt_not_cre_iva_0');\" title=\"IVA 0%\"> 0,00 </span>");
            out.println("<span id='frt_not_cre_iva_12' class='txt2' onmouseover=\"this.className='txt3';\" onmouseout=\"this.className='txt2'\" onmousedown=\"iniMover(event, 'frt_not_cre_iva_12');\" title=\"IVA 12%\"> 12,00 </span>");
            out.println("<span id='frt_not_cre_total' class='txt2' onmouseover=\"this.className='txt3';\" onmouseout=\"this.className='txt2'\" onmousedown=\"iniMover(event, 'frt_not_cre_total');\" title=\"total\"> 112,00 </span>");
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
