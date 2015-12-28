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

import javax.servlet.*;
import javax.servlet.http.*;
import jm.adm.clas.Sucursal;
import java.sql.ResultSet;
import jm.cnt.clas.ReposicionCajaChica;

/**
 *
 * @author Jorge
 */
public class frmAlertaMinCajaChica extends HttpServlet {
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
        int id_sucursal = (Integer)sesion.getAttribute("sucursal");
        String usuario = (String)sesion.getAttribute("usuario");
        String clave = (String)sesion.getAttribute("clave");
        
        response.setContentType("text/html;charset=UTF-8");
        response.setHeader("Pragma", "no-cache");
        response.setHeader("Expires", "Mon, 01 Jan 2001 00:00:01 GMT");
        response.setHeader("Cache-Control", "no-store");
        response.setHeader("Cache-Control", "must-revalidate");
        response.setHeader("Cache-Control", "no-cache");
        PrintWriter out = response.getWriter();

        String html = "";

        Sucursal objSucursal = new Sucursal(this._ip, this._puerto, this._db, usuario, clave);
        ReposicionCajaChica objReposicionCajaChica = new ReposicionCajaChica(this._ip, this._puerto, this._db, usuario, clave);
        try {
            String id_sucursal1 = String.valueOf(id_sucursal);
            String id_plan_cuenta_caja_chica = "";
            float fondo_caja_chica = 1;
            float min_caja_chica = 0;
            try{
                ResultSet rs = objSucursal.getSucursal(id_sucursal1);
                if(rs.next()){
                    id_plan_cuenta_caja_chica = (rs.getString("id_plan_cuenta_caja_chica")!=null) ? rs.getString("id_plan_cuenta_caja_chica") : "";
                    fondo_caja_chica = (rs.getString("fondo_caja_chica")!=null) ? rs.getFloat("fondo_caja_chica") : 1;
                    min_caja_chica = (rs.getString("min_caja_chica")!=null) ? rs.getFloat("min_caja_chica") : 0;
                    rs.close();
                }
            }catch(Exception e){
                e.printStackTrace();
            }

            float minimo = (min_caja_chica * fondo_caja_chica) / 100;
            String saldo_caja_chica = objReposicionCajaChica.getSaldoCajaChica(id_plan_cuenta_caja_chica);
            if(Float.parseFloat(saldo_caja_chica) <= minimo){
                html = "msg»Tiene saldo mínimo en caja chica.";
            }

            out.print(html);

        }catch(Exception e){
            e.printStackTrace();
        } finally {
            objSucursal.cerrar();
            objReposicionCajaChica.cerrar();
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
