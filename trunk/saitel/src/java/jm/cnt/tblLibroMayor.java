/**
* @version 3.0
* @package YAKUSOFT.
* @author Jorge Washington Mueses Cevallos.
* @copyright Copyright (C) 2010 por Jorge Mueses. Todos los derechos reservados.
* @license http://www.gnu.org/copyleft/gpl.html GNU/GPL.
* YAKUSOFT! es un software de libre distribución, que puede ser
* copiado y distribuido bajo los términos de la Licencia Pública
* General GNU, de acuerdo con la publicada por la Free Software
* Foundation, versión 2 de la licencia o cualquier versión posterior.
*/

package jm.cnt;

import java.io.*;

import javax.servlet.*;
import javax.servlet.http.*;
import java.sql.ResultSet;
import jm.cnt.clas.LibroMayor;
import jm.cnt.clas.PlanCuenta;
import jm.web.Addons;
import jm.web.Fecha;

/**
 *
 * @author Jorge
 */
public class tblLibroMayor extends HttpServlet {
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
        try {
            String id_plan_cuenta = request.getParameter("idPC");
            String alto = request.getParameter("aB");
            int _altBody = Integer.parseInt(alto) - 272;
            int i=0;
            String fil = "jm_filaPar";
            String codigo_cuenta = "";
            String nombre_cuenta = "";
            String html = "obj»axLibMay^frm»La cuenta no es de movimiento.";
            
            PlanCuenta objPlanCuenta = new PlanCuenta(this._ip, this._puerto, this._db, usuario, clave);
            ResultSet rsTablas = objPlanCuenta.getTabla(id_plan_cuenta);
            try{
                if(rsTablas.next()){
                    codigo_cuenta = (rsTablas.getString("codigo_cuenta")!=null) ? rsTablas.getString("codigo_cuenta") : "";
                    nombre_cuenta = (rsTablas.getString("nombre_cuenta")!=null) ? rsTablas.getString("nombre_cuenta") : "";
                }
                rsTablas.close();
            }catch(Exception e){
                e.printStackTrace();
            }finally{
                objPlanCuenta.cerrar();
            }
            
            LibroMayor objLibroMayor = new LibroMayor(this._ip, this._puerto, this._db, usuario, clave);
            if(objLibroMayor.cuentaMovimiento(id_plan_cuenta)){
                html += "obj»axLibMay^fun»cnt_getMayor()^frm»<div class='H5' style='width:712px;text-align:left;'>"+
                        "<input type='hidden' id='idPlanCuenta' value='"+id_plan_cuenta+"' />"+
                        "C&Oacute;DIGO: "+codigo_cuenta+"<br />CUENTA: "+nombre_cuenta+"</div>" +
                        "<TABLE width='714' cellspacing='0' cellpadding='0'>" +
                        "<TR><TH class='jm_TH' width='75'>FECHA</TH><TH class='jm_TH' width='55'># AS.</TH>" +
                        "<TH class='jm_TH' width='250'>CONCEPTO</TH><TH class='jm_TH' width='80'>DEBE</TH>" +
                        "<TH class='jm_TH' width='80'>HABER</TH><TH class='jm_TH' width='80'>SALDO DEUDOR</TH>" +
                        "<TH class='jm_TH' width='80'>SALDO ACREEDOR</TH></TR></TABLE>";
                html += "<div id='axDatMay' style='overflow:auto;width:730px;height:"+ _altBody +"px;'></div>";
            }

            out.print(html);
        } finally {
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
