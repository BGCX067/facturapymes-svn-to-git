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

package jm.inv;

import jm.cnt.*;
import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;
import java.sql.ResultSet;
import jm.act.clas.Activo;
import jm.web.Fecha;
/**
 *
 * @author Jorge
 */
public class tblArbolActivos extends HttpServlet {
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
            //String p = request.getParameter("id") != null ? request.getParameter("id") : "0";
            //String alto = request.getParameter("aB");
            //int _altBody = Integer.parseInt(alto) - 220;

            Activo objActivos = new Activo(this._ip, this._puerto, this._db, usuario, clave);
            ResultSet rsActivos = objActivos.getActivosArbol();
            

            //String html = "obj»d_11^frm»NO SE HAN ENCONTRADO DATOS PARA MOSTRAR";
            String html = "obj»tblFilAct^frm»<input type='hidden' id='imp' value='pdfLibroDiario' /><div id='auxCon' style='width:400;'>";
            
                 //html += "<DIV style='overflow:auto;width:400;height:"+ _altBody +"px;'>" +
                   html += "<DIV style='overflow:auto;width:400;height:155px;'>" +
                        "<TABLE class='jm_tabla' cellspacing='1' cellpadding='0' id='tblD'>";
            try{
                int i=0;
                String fil = "jm_filaPar";
                String id_activo = "";
                String codigo_activo = "";
                String codigo_activom1 = "";
                String descripcion = "";
                String elementos = "";
                while(rsActivos.next()){
                    codigo_activom1 = (rsActivos.getString("codigo")!=null) ? rsActivos.getString("codigo") : "";
                    if(codigo_activom1.indexOf (codigo_activo) != -1)
                    {
                        html += "<tr class='"+fil+"' style='cursor:default;' title='Insertar en formulario' >";
                        html += "<td width='100'><b>"+codigo_activo+"</b></td>";
                        html += "<td width='190'><b>"+descripcion+"</b></td>";
                        html += "<td width='50'><b>"+elementos+"</b></td>";
                        html += "</tr>";
                    }
                    else
                    {
                        html += "<tr class='"+fil+"' style='cursor:default;' title='Insertar en formulario' onclick=\"addActivo('"+id_activo+"','"+codigo_activo+"','"+descripcion+"','"+elementos+"')\">";
                        html += "<td width='100'>"+codigo_activo+"</td>";
                        html += "<td width='190'>"+descripcion+"</td>";
                        html += "<td width='50'>"+elementos+"</td>";
                        html += "</tr>";
                    }
                    id_activo = (rsActivos.getString("id_comun")!=null) ? rsActivos.getString("id_comun") : "";
                    codigo_activo = codigo_activom1;
                    descripcion = rsActivos.getString("nombre");
                    elementos = (rsActivos.getString("numero_elementos")!=null) ? rsActivos.getString("numero_elementos") : "";
                }
                html += "<tr class='"+fil+"' style='cursor:default;' title='Insertar en formulario' onclick=\"addActivo('"+id_activo+"','"+codigo_activo+"','"+descripcion+"','"+elementos+"')\">";
                html += "<td width='100'>"+codigo_activo+"</td>";
                html += "<td width='190'>"+descripcion+"</td>";
                html += "<td width='50'>"+elementos+"</td>";
                html += "</tr>";

                rsActivos.close();
            }catch(Exception e){
                e.printStackTrace();
            }finally{
                objActivos.cerrar();
            }

            html += "</table></div></div>";
            
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
