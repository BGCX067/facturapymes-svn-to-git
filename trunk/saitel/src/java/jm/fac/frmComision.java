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
import java.sql.ResultSet;
import jm.fac.clas.Instalacion;
import jm.fac.clas.PlanServicio;
import jm.web.Addons;

/**
 *
 * @author Jorge
 */
public class frmComision extends HttpServlet {
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
        String id_rol = (String)sesion.getAttribute("id_rol");
        int id_sucursal_sesion = (Integer)sesion.getAttribute("sucursal");
        String usuario = (String)sesion.getAttribute("usuario");
        String clave = (String)sesion.getAttribute("clave");

        response.setContentType("text/html;charset=UTF-8");
        response.setHeader("Pragma", "no-cache");
        response.setHeader("Expires", "Mon, 01 Jan 2001 00:00:01 GMT");
        response.setHeader("Cache-Control", "no-store");
        response.setHeader("Cache-Control", "must-revalidate");
        response.setHeader("Cache-Control", "no-cache");
        PrintWriter out = response.getWriter();

        String id_instalacion_comision = request.getParameter("id");
        int altB = Integer.parseInt(request.getParameter("altB"));

        PlanServicio objPlanServicio = new PlanServicio(this._ip, this._puerto, this._db, usuario, clave);
        
        Instalacion objInstalacion = new Instalacion(this._ip, this._puerto, this._db, usuario, clave);
        try {
            String html = "obj»d_21^frm»";
            html += "<table border='1' width><tr>";
            html += "<th width='20'>#</th>";
            html += "<th width='80'>Factura</th>";
            html += "<th width='70'>Deuda</th>";
            html += "<th width='200'>Cliente</th>";
            html += "<th width='140'>Plan</th>";
            html += "<th width='60'>Tarifa</th>";
            html += "<th width='80'>Fecha Registro</th>";
            html += "<th width='80'>Fecha instalaci&oacute;n</th>";
            html += "<th width='80'>Fecha registro t&eacute;cnico</th>";
            html += "</tr></table>";

            html += "<div style='overflow:auto;width:890px;height:"+(altB-240)+"px;'><table border='1'>";
            float suma = 0;
            try{
                int i=0;
                ResultSet rsInstalaciones = objInstalacion.getInstalacionesComision(id_instalacion_comision);
                while(rsInstalaciones.next()){
                    String id_plan_actual = rsInstalaciones.getString("id_plan_actual")!=null ? rsInstalaciones.getString("id_plan_actual") : "0";
                    suma += objPlanServicio.getCostoPlan(id_plan_actual);
                    html += "<tr>";
                    html += "<td width='20'>"+(i+1)+"</td>";
                    html += "<td width='80'>"+(rsInstalaciones.getString("num_factura")!=null ? rsInstalaciones.getString("num_factura") : "")+"</td>";
                    html += "<td width='70' align='right'>"+(rsInstalaciones.getString("deuda")!=null ? rsInstalaciones.getString("deuda") : "")+"</td>";
                    html += "<td width='200'>"+(rsInstalaciones.getString("razon_social")!=null ? rsInstalaciones.getString("razon_social") : "")+"</td>";
                    html += "<td width='140'>"+(rsInstalaciones.getString("plan")!=null ? rsInstalaciones.getString("plan") : "")+"</td>";
                    html += "<td width='60' align='right'>"+objPlanServicio.getCostoPlan(id_plan_actual)+"</td>";
                    html += "<td width='80'>"+(rsInstalaciones.getString("fecha_registro")!=null ? rsInstalaciones.getString("fecha_registro") : "")+"</td>";
                    html += "<td width='80'>"+(rsInstalaciones.getString("fecha_instalacion")!=null ? rsInstalaciones.getString("fecha_instalacion") : "")+"</td>";
                    html += "<td width='80'>"+(rsInstalaciones.getString("ingreso_datos")!=null ? rsInstalaciones.getString("ingreso_datos") : "")+"</td>";
                    html += "</tr>";
                    i++;
                }
            }catch(Exception e){
                e.printStackTrace();
            }
            html += "<tr>";
            html += "<td colspan='5' width='510' align='right'>TOTAL: </td>";
            html += "<td width='60' align='right'>"+Addons.redondear(suma)+"</td>";
            html += "<td colspan='3' width='240'>&nbsp;</td>";
            html += "</tr>";
            html += "</table></div>";

            out.print(html);

        } finally {
            objPlanServicio.cerrar();
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
