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

package jm.adm;

import java.io.*;

import javax.servlet.*;
import javax.servlet.http.*;
import jm.adm.clas.Bodega;
import java.sql.ResultSet;
import jm.adm.clas.Sucursal;
import jm.nom.clas.Empleado;
import jm.web.DatosDinamicos;

/**
 *
 * @author Jorge
 */
public class frmBodega extends HttpServlet {
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

        Bodega objBodega = new Bodega(this._ip, this._puerto, this._db, usuario, clave);

        try {
            int _altBody = 305;

            String id = request.getParameter("id");
            String id_sucursal = "1";
            String bodega = "";
            String ubicacion = "";
            String coresponsable = "";
            String id_responsable = "";
            boolean estado = true;

            if(id.compareTo("-1")!=0){
                try{
                    ResultSet rs = objBodega.getBodega(id);
                    if(rs.next()){
                        id_sucursal = (rs.getString("id_sucursal")!=null) ? rs.getString("id_sucursal") : "";
                        bodega = (rs.getString("bodega")!=null) ? rs.getString("bodega") : "";
                        id_responsable = (rs.getString("id_responsable")!=null) ? rs.getString("id_responsable") : "";
                        ubicacion = (rs.getString("ubicacion")!=null) ? rs.getString("ubicacion") : "";
                        coresponsable = (rs.getString("coresponsable")!=null) ? rs.getString("coresponsable") : "";
                        estado = (rs.getString("estado")!=null) ? rs.getBoolean("estado") : true;
                        rs.close();
                    }
                }catch(Exception e){
                    e.printStackTrace();
                }
            }
            
            Sucursal objSucursal = new Sucursal(this._ip, this._puerto, this._db, usuario, clave);
            ResultSet rsSucursales = objSucursal.getSucursales();
            objSucursal.cerrar();


            Empleado objEmpleado = new Empleado(this._ip, this._puerto, this._db, usuario, clave);
            ResultSet rsEmpleados = objEmpleado.getEmpleados(Integer.valueOf(id_sucursal));
            objEmpleado.cerrar();

            String html = "obj»d_21^foc»bodega^fun»_objTab.seleccionar(0);adm_setNavBodega("+id+");adm_getArticulos();^frm»";
            html += "<form action='frmBodegaGuardar' onsubmit='return adm_bodegaGuardar(this)' autocomplete='off'>";
            html += "<input type='hidden' id='id' name='id' value='"+id+"' />";

            html += "<div class='H3'>Formulario de Bodega o Almac&eacute;n</div>";

            html += "<div class='grupoTab' style='width:510px;'>"
                  + "<div id='tabR0'>"
                  + "<a id='tab00' href='javascript:void(0);' class='jm_tabAct' onclick='_objTab.seleccionar(0);'>Bodega</a>";
            if(id.compareTo("-1")!=0){
                html += "<a id='tab01' href='javascript:void(0);' class='jm_tab' onclick='_objTab.seleccionar(1);'>Estanter&iacute;a</a>";
                html += "<a id='tab02' href='javascript:void(0);' class='jm_tab' onclick='_objTab.seleccionar(2);'>Art&iacute;culos</a>";
            }
            html += "</div>";

            /* PRIMER TAB */
            html += "<div id='tabR1'>";
            html += "<div id='tab10' class='jm_tabHTML' style='display:block;width:500px;height:"+_altBody+"px;'>";

            html += "<table>";

            html += "<tr><td>Sucursal: <span class='marca'>*</span></td>";
            html += "<td>" + DatosDinamicos.combo(rsSucursales, "id_sucursal", id_sucursal, "nom_getEmpleados(this.value)", "", 335) + "</td></tr>";

            html += "<tr><td width='150'>Nombre de la bodega o almac&eacute;n: <span class='marca'>*</span></td>";
            html += "<td><input id='bodega' name='bodega' class='may' value='"+bodega+"' type='text' size='30' maxlength='60' onkeypress=\"_alfanumerico(event);\" onblur=\"this.value=this.value._trim();\" /></td></tr>";
            html += "<tr><td>Responsable: <span class='marca'>*</span></td>";
            html += "<td id='axRes'>" + DatosDinamicos.combo(rsEmpleados, "id_responsable", id_responsable, "", " - SELECCIONE UN EMPLEADO - ", 335) + "</td></tr>";
            html += "<tr valign='top'><td>Coresponsables: </td>";
            html += "<td><textarea id='coresponsable' name='coresponsable' rows='5' cols='50' onkeypress=\"_alfanumerico(event);\" onblur=\"this.value=this.value._trim();\">" + coresponsable + "</textarea> </td></tr>";

            html += "<tr><td>Ubicaci&oacute;n: <span class='marca'>*</span></td>";
            html += "<td><input id='ubicacion' name='ubicacion' type='text' value='"+ubicacion+"' size='30' maxlength='60' onkeypress=\"_alfanumerico(event);\" onblur=\"this.value=this.value._trim();\" /></td></tr>";

            html += "<tr><td>Estado: <span class='marca'>*</span></td>";
            html += "<td><input type='radio' id='estado0' name='estado' value='true' "+((estado) ? "checked" : "")+" /> Activo &nbsp;&nbsp;&nbsp;&nbsp; " +
                    "<input type='radio' id='estado1' name='estado' value='false' "+((!estado) ? "checked" : "")+" /> Inactivo</td></tr>";
            html += "</table>";

            html += "</div>";



            /* SEGUNDO TAB */


            html += "<div id='tab11' class='jm_tabHTML' style='display:none;width:500px;height:"+_altBody+"px;'>";


            html += "<p>&nbsp;&nbsp;<a href='javascript:void(0);' id='inPE' onclick=\"adm_estanteriaEditar(-1);\">Nueva estanter&iacute;a</a></p>";

            html += "<div id='axTblPE'><TABLE cellspacing='0' cellpadding='0'><TR>" +
                            "<TH class='jm_TH' width='120'>ESTANTERIA</TH>" +
                            "<TH class='jm_TH' width='80'>FILA</TH>" +
                            "<TH class='jm_TH' width='80'>COLUMNA</TH>" +
                            "<TH class='jm_TH' width='80'>NUM. NIVELES</TH>" +
                            "<TH class='jm_TH' width='20'>&nbsp;</TH></TR></TABLE>";
            html += "<DIV id='sll'>" +
            "<TABLE class='jm_tabla' cellspacing='1' cellpadding='0' id='tblPE'>";
            try{
                int i=0;
                String id_estanteria = "";
                String estanteria = "";
                String fila = "";
                String columna = "";
                String num_nivel = "";
                ResultSet rsEstanterias = objBodega.getEstanterias(id);
                while(rsEstanterias.next()){
                    id_estanteria = (rsEstanterias.getString("id_estanteria")!=null) ? rsEstanterias.getString("id_estanteria") : "";
                    estanteria = (rsEstanterias.getString("estanteria")!=null) ? rsEstanterias.getString("estanteria") : "";
                    fila = (rsEstanterias.getString("fila")!=null) ? rsEstanterias.getString("fila") : "";
                    columna = (rsEstanterias.getString("columna")!=null) ? rsEstanterias.getString("columna") : "";
                    num_nivel = (rsEstanterias.getString("num_nivel")!=null) ? rsEstanterias.getString("num_nivel") : "";
                    html += "<tr id='rTI"+i+"' valign='top' class='jm_filaPar' onclick=\"adm_estanteriaEditar("+id_estanteria+");\" style='cursor:pointer' onmouseover=\"this.className='jm_filaSobre'\" onmouseout=\"this.className='jm_filaPar'\">";
                    html += "<td width='121' style='cursor:pointer'>"+estanteria+"</td>";
                    html += "<td width='81' style='cursor:pointer'>"+fila+"</td>";
                    html += "<td width='81' style='cursor:pointer'>"+columna+"</td>";
                    html += "<td width='81' style='cursor:pointer'>"+num_nivel+"</td>";
                    html += "<td align='center' width='21'><input type='hidden' id='idE"+i+"' value='"+id_estanteria+"' />&nbsp;</td></tr>";
                    i++;
                }
            }catch(Exception e){e.printStackTrace();}

            html += "</table></div></div>";

            html += "</div>";




            /* TERCERO TAB */


            html += "<div id='tab12' class='jm_tabHTML' style='display:none;width:500px;height:"+_altBody+"px;'>";

            html += "<div style='height:40px' id='axHerraBod'>&nbsp;&nbsp;</div>";
            
            html += "<div id='axTblPE'><TABLE cellspacing='0' cellpadding='0'><TR>" +
                            "<TH class='jm_TH' width='105'>UBICACION</TH>" +
                            "<TH class='jm_TH' width='255'>CODIGO</TH>" +
                            "<TH class='jm_TH' width='200'>ARTICULO</TH>" +
                            "<TH class='jm_TH' width='70'>COSTO</TH>" +
                            "<TH class='jm_TH' width='20'>&nbsp;</TH></TR></TABLE>";
            html += "<div id='sl2'> </div>";


            html += "</div></div></div>";


            html += "<div><input type='submit' value='Guardar' /></div>";


            html += "</form>";

            out.print(html);

        }catch(Exception e){
            e.printStackTrace();
        } finally {
            objBodega.cerrar();
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
