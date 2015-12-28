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

package jm.cnt;

import java.io.*;
import java.sql.ResultSet;

import javax.servlet.*;
import javax.servlet.http.*;
import jm.cnt.clas.ComprobanteEgreso;
import jm.web.DatosDinamicos;

/**
 *
 * @author Jorge
 */
public class getProveedoresEgresos extends HttpServlet {
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

        String texto = request.getParameter("txt");
        
        ComprobanteEgreso objComprobanteEgreso = new ComprobanteEgreso(this._ip, this._puerto, this._db, usuario, clave);
        String combo="";
        try{
            ResultSet rs = objComprobanteEgreso.consulta("(SELECT distinct dni as ruc, nombre || ' ' || apellido as razon_social FROM tbl_empleado where lower(nombre || ' ' || apellido) like '"+texto.toLowerCase()+"%' "
                    + "union "
                    + "select ruc, razon_social from tbl_proveedor where lower(razon_social) like '"+texto.toLowerCase()+"%%') order by razon_social limit 20 offset 0");
            while(rs.next()){
                String ruc = rs.getString("ruc")!=null? rs.getString("ruc") : "";
                String razon_social = rs.getString("razon_social")!=null? rs.getString("razon_social") : "";
                combo+="<div onclick=\"_Fch=true;fac_cmbActivoRem();_('ru').value='"+ruc+"';_('nb').value='"+razon_social+"';\" onmouseover=\"this.className='jm_filaImp'\" onmouseout=\"this.className=''\">"+ruc+" &nbsp; "+razon_social+"</div>";
            }
            rs.close();
        }catch(Exception e){
            e.printStackTrace();
        }
        
        
        try {
            out.print("obj»Fech_^frm»"+combo);
        } finally {
            objComprobanteEgreso.cerrar();
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
