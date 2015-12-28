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
import jm.adm.clas.Configuracion;
import jm.web.Fecha;

/**
 *
 * @author Jorge
 */
public class formatoCheque extends HttpServlet {
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

        Configuracion conf = new Configuracion(this._ip, this._puerto, this._db, usuario, clave);
        String v_orden_de[] = conf.getValor("chq_orden_de").split(":");
        String v_total[] = conf.getValor("chq_total").split(":");
        String v_son[] = conf.getValor("chq_son").split(":");
        String v_lugar_fecha[] = conf.getValor("chq_lugar_fecha").split(":");

        try {
            out.println("<html>");
            out.println("<head>");
            out.println("<title>FORMATO DE CHEQUE</title>");
            out.println("<script>var documento='ch';"+
                    "var id="+id+";" +
                    "var v_orden_de=Array("+v_orden_de[0]+", "+v_orden_de[1]+");" +
                    "var v_total=Array("+v_total[0]+", "+v_total[1]+");" +
                    "var v_son=Array("+v_son[0]+", "+v_son[1]+");" +
                    "var v_lugar_fecha=Array("+v_lugar_fecha[0]+", "+v_lugar_fecha[1]+");</script>");
            out.println("<SCRIPT type=\"text/javascript\" src=\"formato.js\"></SCRIPT>");
            out.println("<style>");
            out.println("BODY{background-image:url('img/cuadricula.gif');}");
            out.println("SPAN,TABLE{position:absolute;font-size:12px;font-family:Arial, Helvetica, sans-serif;padding:4px;}");
            out.println("#imp{font-family:Arial,Helvetica,sans-serif;font-size:11px;top:5px;float:right;}");
            out.println(".txt{background-color:#CEEFFF;border-color:#00A3F0;color:#FF5300;cursor:move;border-width:1px;border-style:solid;padding:3px;}");
            out.println("</style>");
            out.println("</head>");
            out.println("<body>");
            out.println("<button id='imp' onclick=\"window.open('pdfCheque?id=-2', '_blank', 'top=50,left=50,width=750,height=500,location=no');\">Imprimir cheque de prueba</button>");
            out.println("<span id='chq_orden_de' onmouseover=\"this.className='txt';\" onmouseout=\"this.className=''\" onmousedown=\"iniMover(event, 'chq_orden_de');\" title=\"Orden de\">Orden de... </span>");
            out.println("<span id='chq_total' onmouseover=\"this.className='txt';\" onmouseout=\"this.className=''\" onmousedown=\"iniMover(event, 'chq_total');\" title=\"Valor en n&uacute;mero\">100,00 </span>");
            out.println("<span id='chq_son' onmouseover=\"this.className='txt';\" onmouseout=\"this.className=''\" onmousedown=\"iniMover(event, 'chq_son');\" title=\"La suma de\">cien 00/100 </span>");
            out.println("<span id='chq_lugar_fecha' onmouseover=\"this.className='txt';\" onmouseout=\"this.className=''\" onmousedown=\"iniMover(event, 'chq_lugar_fecha');\" title=\"Ciudad, fecha\">Ibarra, &nbsp;&nbsp;&nbsp; "+Fecha.getFecha("SQL")+" </span>");
            out.println("</body>");
            out.println("</html>");
        } finally {
            conf.cerrar();
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
