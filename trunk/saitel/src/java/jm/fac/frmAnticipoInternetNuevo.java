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

package jm.fac;

import java.io.*;

import javax.servlet.*;
import javax.servlet.http.*;
import jm.fac.clas.Instalacion;
import jm.web.Fecha;

/**
 *
 * @author Jorge
 */
public class frmAnticipoInternetNuevo extends HttpServlet {
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

        Instalacion objInstalacion = new Instalacion(this._ip, this._puerto, this._db, usuario, clave);

        try {
            int anio = Fecha.getAnio();
            int mes = Fecha.getMes();
            String fecha_ini = "01" + "/" + mes + "/" + anio;
            String fecha_fin = Fecha.getUltimoDiaMes(anio, mes)  + "/" + mes + "/" + anio;

            String html = "obj»cmp_html^foc»txtCli^frm»";
            html += "<form action='frmAnticipoInternetNuevoGuardar' onsubmit='return fac_frmAnticipoInternetNuevoGuardar(this);' autocomplete='off'>";
            html += "<input type='hidden' id='id_instalacion' name='id_instalacion' value='' />";
            html += "<table width='430'>";
            html += "<tr><td colspan='2' class='H3'>Formulario de Anticipo</td></tr>";
            html += "<tr><td colspan='2'>C&eacute;dula, nombres o IP: <input type='text' id='txtCli' onkeypress=\"var c=(_esN)?event.which:event.keyCode;if(c==13){fac_getIblInstalls();}\" /> "
                    + "<input type='button' value='filtrar' onclick='fac_getIblInstalls()' /></td></tr>";
            html += "<tr><td colspan='2' id='axTb'><hr /></td></tr>";

            html += "<tr><td>C&eacute;dula: <span class='marca'>*</span></td>";
            html += "<td id='axCi'>&nbsp;</td></tr>";
            html += "<tr><td>Cliente: <span class='marca'>*</span></td>";
            html += "<td id='axCl'>&nbsp;</td></tr>";
            html += "<tr><td>IP: <span class='marca'>*</span></td>";
            html += "<td id='axIP'>&nbsp;</td></tr>";

            html += "<tr><td>Desde el per&iacute;odo: <span class='marca'>*</span></td>";
            html += "<td><input type='text' id='fecha_ini' name='fecha_ini' value='"+fecha_ini+"' size='10' readOnly><input type='button' value='...' onClick=\"SelectorFecha.crear('fecha_ini', 'SQL');\" /></td></tr>";
            html += "<tr><td>Hasta: <span class='marca'>*</span></td>";
            html += "<td><input type='text' id='fecha_fin' name='fecha_fin' value='"+fecha_fin+"' size='10' readOnly><input type='button' value='...' onClick=\"SelectorFecha.crear('fecha_fin', 'SQL');\" /></td></tr>";
            html += "<tr><td colspan='2'><input type='button' value='Registrar Anticipo' onclick='fac_frmAnticipoInternetNuevoGuardar(this.form)' /></td></tr>";
            html += "</table></form>";
            out.print(html);

        } finally {
            objInstalacion.cerrar();
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
