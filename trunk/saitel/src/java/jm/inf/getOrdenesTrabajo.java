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

package jm.inf;

import java.io.*;

import javax.servlet.*;
import javax.servlet.http.*;
import java.sql.ResultSet;
import jm.inf.clas.HojaRuta;
import jm.web.Fecha;

/**
 *
 * @author Jorge
 */
public class getOrdenesTrabajo extends HttpServlet {
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

        String id = request.getParameter("id");
        String tp_tr = request.getParameter("tp_tr");
        String fecha = request.getParameter("fecha");
        int i = Integer.parseInt(request.getParameter("i"));

        HojaRuta objHojaRuta = new HojaRuta(this._ip, this._puerto, this._db, usuario, clave);
        
        try {
            String html = "";
            try{
                String where = "";
                if(tp_tr.compareTo("-0")!=0){
                    where += " and tipo_trabajo='"+tp_tr+"'";
                }
                if(fecha.compareTo("-0")!=0){
                    where += " and fecha_reporte='"+fecha+"'";
                }
                ResultSet rsOrdenes = objHojaRuta.getOrdenesPendientes(id_sucursal, id, where);
                String clase = "jm_filaPar";
                String color_estado = "";
                while(rsOrdenes.next()){
                    clase = i%2==0 ? "jm_filaPar" : "jm_filaImp";
                    color_estado = (rsOrdenes.getString("estado")!=null) ? rsOrdenes.getString("estado") : "1";
                    if(color_estado.compareTo("3")==0){
                        clase = "jm_filaEsp";
                    }
                    html += "<div id='rot"+i+"' class='jm_fila "+clase+"'><label>";
                    html += "<div class='jm_columna caja "+clase+"' style='width:22px'><input id='ch_ot"+i+"' name='ch_ot"+i+"' type='checkbox'><input type='hidden' id='asg"+i+"' name='asg"+i+"' value='0' />"
                            + "<input id='id_ot"+i+"' name='id_ot"+i+"' type='hidden' value='"+ ((rsOrdenes.getString("id_orden_trabajo")!=null) ? rsOrdenes.getString("id_orden_trabajo") : "") + "'></div>";
                    html += "<div class='jm_columna caja "+clase+"' style='width:70px' onmouseover='fac_setOrden("+i+")' onmouseout=\"_R('dv')\">" + ((rsOrdenes.getString("numero_orden")!=null) ? rsOrdenes.getString("numero_orden") : "") + "</div>";
                    html += "<div class='jm_columna caja "+clase+"' style='width:120px' id='axtpo"+i+"'>" + ((rsOrdenes.getString("plan")!=null) ? rsOrdenes.getString("plan") : "") + "</div>";
                    html += "<div class='jm_columna caja "+clase+"' style='width:120px'>" + ((rsOrdenes.getString("txt_tipo_trabajo")!=null) ? rsOrdenes.getString("txt_tipo_trabajo") : "") + "</div>";
                    html += "<div class='jm_columna caja "+clase+"' style='width:90px'>" + ((rsOrdenes.getString("fecha_reporte")!=null) ? Fecha.ISOaSQL(rsOrdenes.getString("fecha_reporte")) : "") + "</div>";
                    html += "<div class='jm_columna caja "+clase+"' style='width:100px'>" + ((rsOrdenes.getString("sector")!=null) ? rsOrdenes.getString("sector") : "") + "</div>";
                    html += "<div class='jm_columna caja "+clase+"' style='width:200px'>" + ((rsOrdenes.getString("direccion_instalacion")!=null) ? rsOrdenes.getString("direccion_instalacion") : "") + "</div>";
                    html += "<div class='jm_columna caja "+clase+"' style='width:300px'>" + ((rsOrdenes.getString("diagnostico_tecnico")!=null) ? rsOrdenes.getString("diagnostico_tecnico") : "") + "</div>";
                    html += "</label></div>";
                    i++;
                }
            }catch(Exception e){
                e.printStackTrace();
            }

            out.println("obj»tblPe^frm»"+html);
        } finally {
            objHojaRuta.cerrar();
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