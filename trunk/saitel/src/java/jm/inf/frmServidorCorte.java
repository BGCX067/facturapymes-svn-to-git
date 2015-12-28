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
import jm.adm.clas.Sucursal;
import jm.inf.clas.ServidorCorte;
import jm.seg.clas.Rol;
import jm.web.DatosDinamicos;

/**
 *
 * @author Jorge
 */
public class frmServidorCorte extends HttpServlet {
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
        String usuario = (String)sesion.getAttribute("usuario");
        String clave = (String)sesion.getAttribute("clave");

        response.setContentType("text/html;charset=UTF-8");
        response.setHeader("Pragma", "no-cache");
        response.setHeader("Expires", "Mon, 01 Jan 2001 00:00:01 GMT");
        response.setHeader("Cache-Control", "no-store");
        response.setHeader("Cache-Control", "must-revalidate");
        response.setHeader("Cache-Control", "no-cache");
        PrintWriter out = response.getWriter();

        Rol objRol = new Rol(this._ip, this._puerto, this._db, usuario, clave);

        Sucursal obj_Sucursal = new Sucursal(this._ip, this._puerto, this._db, usuario, clave);
        ResultSet rsSucursales = obj_Sucursal.getSucursales();
        
        

        ServidorCorte objServidorCorte = new ServidorCorte(this._ip, this._puerto, this._db, usuario, clave);

        try {
            String id = request.getParameter("id");
            String id_sucursal = "";
            String subredes = "";
            String servidor = "";
            String puerto = "26";
            String usuario_ftp = "";
            String clave_ftp = "";
            boolean estado = true;
            long megas_corporativo = 0;
            long megas_small = 0;
            long megas_residencial = 0;
            long megas_nocturno = 0;
            if(id.compareTo("-1")!=0){
                try{
                    ResultSet rsAntena = objServidorCorte.getServidorCorte(id);
                    if(rsAntena.next()){
                        id_sucursal = (rsAntena.getString("id_sucursal")!=null) ? rsAntena.getString("id_sucursal") : "";
                        subredes = (rsAntena.getString("subredes")!=null) ? rsAntena.getString("subredes") : "";
                        servidor = (rsAntena.getString("servidor")!=null) ? rsAntena.getString("servidor") : "";
                        puerto = (rsAntena.getString("puerto")!=null) ? rsAntena.getString("puerto") : "26";
                        usuario_ftp = (rsAntena.getString("usuario")!=null) ? rsAntena.getString("usuario") : "";
                        clave_ftp = (rsAntena.getString("clave")!=null) ? rsAntena.getString("clave") : "";
                        estado = (rsAntena.getString("estado")!=null) ? rsAntena.getBoolean("estado") : true;
                        
                        megas_corporativo = (rsAntena.getString("megas_corporativo")!=null) ? rsAntena.getLong("megas_corporativo") : 0;
                        megas_small = (rsAntena.getString("megas_small")!=null) ? rsAntena.getLong("megas_small") : 0;
                        megas_residencial = (rsAntena.getString("megas_residencial")!=null) ? rsAntena.getLong("megas_residencial") : 0;
                        megas_nocturno = (rsAntena.getString("megas_nocturno")!=null) ? rsAntena.getLong("megas_nocturno") : 0;
                        rsAntena.close();
                    }
                }catch(Exception e){
                    e.printStackTrace();
                }
            }


            String html = "obj»d_21^foc»subredes^frm»";
            html += "<form action='frmServidorCorteGuardar' onsubmit='return inf_servidoresCorteGuardar(this)' autocomplete='off'>";
            html += "<input type='hidden' id='id' name='id' value='"+id+"' /><table>";
            
            html += "<tr><td colspan='2' class='H3'>Formulario de Servidor de Cortes</td></tr>";
            
            html += "<tr><td>Sucursal: <span class='marca'>*</span></td>";
            html += "<td>" + DatosDinamicos.combo(rsSucursales, "id_sucursal", id_sucursal, "", "", 274) + "</td></tr>";
            
            html += "<tr><td valign='top'>Subredes: <span class='marca'>*</span></td>";
            html += "<td><textarea id='subredes' name='subredes' rows='6' cols='40' onkeypress=\"_validar(event, '0123456789./ ');\" >"+subredes+"</textarea></td></tr>";
            
            html += "<tr><td colspan='2'><hr /></td></tr>";
            
            html += "<tr><td>Servidor (IP): <span class='marca'>*</span></td>";
            html += "<td><input id='servidor' name='servidor' type='text' size='30' maxlength='80' value='"+servidor+"' onkeypress=\"_validar(event, '0123456789.');\" /></td></tr>";
            
            html += "<tr><td>Puerto: <span class='marca'>*</span></td>";
            html += "<td><input id='puerto' name='puerto' type='text' size='30' maxlength='6' value='"+puerto+"' onkeypress=\"_numero(event);\" /></td></tr>";
            
            html += "<tr><td>Usuario: <span class='marca'>*</span></td>";
            html += "<td><input id='usuario' name='usuario' type='text' size='30' maxlength='80' value='"+usuario_ftp+"' onkeypress=\"_alfanumerico(event);\" /></td></tr>";
            
            html += "<tr><td>Contrase&ntilde;a: <span class='marca'>*</span></td>";
            html += "<td><input id='clave' name='clave' type='text' size='30' maxlength='80' value='"+clave_ftp+"' onkeypress=\"_alfanumerico(event);\" /></td></tr>";            
            
            html += "<tr><td>Estado: <span class='marca'>*</span></td>";
            html += "<td><label><input type='radio' id='e0' name='estado' value='true' "+((estado) ? "checked" : "")+" /> Habilitado </label><br/>" +
                    "<label><input type='radio' id='e1' name='estado' value='false' "+((!estado) ? "checked" : "")+" /> Des-habilitado </label></td></tr>";
            
            html += "<tr><td>Megas Corporativos:</td>";
            html += "<td>"+megas_corporativo+"</td></tr>";            
            html += "<tr><td>Megas Smalls:</td>";
            html += "<td>"+megas_small+"</td></tr>";  
            html += "<tr><td>Megas Residenciales:</td>";
            html += "<td>"+megas_residencial+"</td></tr>"; 
            html += "<tr><td>Megas Nocturnos:</td>";
            html += "<td>"+megas_nocturno+"</td></tr>";
            html += "<tr><td>Total megas:</td>";
            html += "<td>"+(megas_corporativo + megas_small + megas_residencial + megas_nocturno)+"</td></tr>"; 
            
            //if(id.compareTo("-1")==0 || (id.compareTo("-1")!=0 && objRol.getAcceso(id_rol, "antenaEditar"))){
                html += "<tr><td colspan='2'><input type='submit' value='Guardar' /></td></tr>";
            //}

            html += "</table></form>";
            out.print(html);

        } finally {
            obj_Sucursal.cerrar();
            objServidorCorte.cerrar();
            objRol.cerrar();
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
