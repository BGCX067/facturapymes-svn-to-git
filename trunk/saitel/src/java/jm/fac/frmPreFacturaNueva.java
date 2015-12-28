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
import java.sql.ResultSetMetaData;
import jm.fac.clas.Instalacion;
import jm.web.DatosDinamicos;
import jm.web.Fecha;

/**
 *
 * @author Jorge
 */
public class frmPreFacturaNueva extends HttpServlet {
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

        Instalacion objInstalacion = new Instalacion(this._ip, this._puerto, this._db, usuario, clave);

        try {
            int anio_ini = objInstalacion.getAnioMinInstalacion();
            int anio_actual = Fecha.getAnio();
            int anio = anio_actual;
            int mes = Fecha.getMes() - 1;
            if(mes == 0){
                mes = 12;
                anio--;
            }
            ResultSet rsInstalaciones = objInstalacion.getInstalaciones(String.valueOf(id_sucursal_sesion), anio, mes);
            String [][] meses = {{"1","enero"},{"2","febrero"},{"3","marzo"},{"4","abril"},
                                {"5","mayo"},{"6","junio"},{"7","julio"},{"8","agosto"},
                                {"9","septiembre"},{"10","octubre"},{"11","noviembre"},{"12","diciembre"}};
            
            

            String html = "obj»cmp_html^frm»";
            html += "<input type='hidden' id='aA' value='"+anio+"' />" +
                    "<input type='hidden' id='mA' value='"+mes+"' />";
            html += "<form action='frmPreFacturaNuevaGuardar' onsubmit='return fac_PreFacturaNuevaGuardar(this);' autocomplete='off'>"
                    + "<input type='hidden' id='id_instalaciones' name='id_instalaciones' value='' />";
            html += "<table width='650'>";
            html += "<tr><td colspan='2' class='H3'>Formulario de Nueva Pre-Factura</td></tr>";
            html += "<tr><td width='50'>A&ntilde;o: <span class='marca'>*</span></td>";
            html += "<td><select id='anio' name='anio' onchange='fac_getInstalls();'>";
            String sel=""; 
            for(int i=anio_ini; i<=anio_actual; i++){
                sel = (i==anio) ? "selected" : "";
                html += "<option "+sel+" value=\"" + i + "\">" + i + "</option>";
            }
            html += "</select></td></tr>";
            html += "<tr><td>Mes: <span class='marca'>*</span></td>";                
            html += "<td>" + DatosDinamicos.combo("mes", String.valueOf(mes), meses, "fac_getInstalls();") + "</td></tr>";
            html += "<tr><td colspan='2'>Instalaciones: <span class='marca'>*</span></td></tr>";
            //html += "<td id='axIns'>" + DatosDinamicos.combo(rsInstalaciones, "id_instalacion", "", "", "", 600) + "</td></tr>";
            html += "<tr><td colspan='2' id='axIns'><select id='id_instalacion' name='id_instalacion' multiple='multiple' size='10' style='width:660px'>";
            try{
                ResultSetMetaData mdata = rsInstalaciones.getMetaData();
                int lim = mdata.getColumnCount();
                String et="";
                rsInstalaciones.beforeFirst();
                while(rsInstalaciones.next()){
                    for(int i=2; i<=lim; i++){
                        et+=rsInstalaciones.getString(i)+" &nbsp; | &nbsp; ";
                    }
                    html+="<option value=\"" + rsInstalaciones.getString(1) + "\">" + et + "</option>";
                    et="";
                }
            }catch(Exception e){
                System.out.println(e.getMessage());
            }
            html += "</select></td></tr>";

            html += "<tr><td><input type='submit' value='Generar Pre-facturas' /></td>"
                    + "<td><input type='button' value='Imprimir lista PDF' onclick=\"imprimir('impReportePdf?op=44&fi=&ff=&op2=0&idSuc=&an='+_('anio').value+'&ms='+_('mes').value+'&idS=&WH=')\" /> "
                    + "&nbsp; <input type='button' value='Imprimir lista EXEL' onclick=\"imprimir('impReporteXls?op=44&fi=&ff=&op2=0&idSuc=&an='+_('anio').value+'&ms='+_('mes').value+'&idS=&WH=')\" /></td></tr>";
            html += "</table></form>";
            out.print(html);

            try{
                rsInstalaciones.close();
            }catch(Exception e){}

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
