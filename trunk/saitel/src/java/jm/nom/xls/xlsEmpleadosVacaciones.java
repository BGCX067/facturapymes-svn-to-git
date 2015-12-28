/**
** @version 1.0
** @package FACTURAPYMES.
* @author Jorge Washington Mueses Cevallos.
* @copyright Copyright (C) 2011 por Jorge Mueses. Todos los derechos reservados.
* @license http://www.gnu.org/copyleft/gpl.html GNU/GPL.
** FACTURAPYMES es un software de libre distribución, que puede ser
* copiado y distribuido bajo los términos de la Licencia
* Attribution-NonCommercial-NoDerivs 3.0 Unported,
* de acuerdo con la publicada por la CREATIVE COMMONS CORPORATION.
*/

package jm.nom.xls;

import java.io.*;

import javax.servlet.*;
import javax.servlet.http.*;
import java.sql.ResultSet;

import jm.web.DataBase;
import jm.ReporteXls;
import jm.web.Fecha;

/**
 *
 * @author Jorge
 */
public class xlsEmpleadosVacaciones extends HttpServlet {
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
        
        //if(request.getHeader("User-Agent").toLowerCase().contains("windows")){
            response.setContentType("application/vnd.ms-excel;");
            response.setHeader("Content-disposition", "inline; filename=reporte.xls;");
        /*}else{
            response.setContentType("text/xml;");
            response.setHeader("Content-disposition", "attachment; filename=reporte.ods;");
        }*/
        response.setHeader("Pragma", "no-cache");
        response.setHeader("Expires", "Mon, 01 Jan 2001 00:00:01 GMT");
        response.setHeader("Cache-Control", "no-store");
        response.setHeader("Cache-Control", "must-revalidate");
        response.setHeader("Cache-Control", "no-cache");
        PrintWriter out = response.getWriter();

        //String WHERE = request.getParameter("WHERE");

        DataBase objDB = new DataBase(this._ip, this._puerto, this._db, usuario, clave);

        int anio = Fecha.getAnio();
        int mes = Fecha.getMes();
        int ultDia = Fecha.getUltimoDiaMes(anio, mes);
        String WHERE = "where estado=true and eliminado=false and fechaVacacion( (fecha_ingreso - ('4 month'::interval))::date ) between '"+(anio + "-" + mes)+"-01' and '"+(anio + "-" + mes + "-" + ultDia)+"'";
        ResultSet registros = objDB.consulta("select departamento,dni,empleado,txt_sexo,telefono,movil,email,fecha_ingreso,fechaVacacion(fecha_ingreso) from vta_empleado "+WHERE+" order by departamento,empleado");
        String subtitulo = "Listaso de empleados próximos a salida a vacaciones.";
        ReporteXls reporte = new ReporteXls();
        String [] cabTabla = new String [] {"Nro.", "#", "DNI", "NOMBRES", "GENERO", "TELF.", "MOVIL", "EMAIL", "INGRESO", "VACACION"};
        String xls = reporte.lista(subtitulo, cabTabla, registros, 11);

        out.print(xls);

        try{
            registros.close();
        }catch(Exception e){
            e.printStackTrace();
        }

        objDB.cerrar();
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
