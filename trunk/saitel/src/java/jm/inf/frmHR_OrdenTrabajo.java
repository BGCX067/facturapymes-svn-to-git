/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package jm.inf;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.ResultSet;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import jm.adm.clas.Bodega;
import jm.fac.clas.PlanServicio;
import jm.fac.clas.Sector;
import jm.inf.clas.Material;
import jm.inf.clas.OrdenTrabajo;
import jm.seg.clas.Rol;
import jm.web.DatosDinamicos;
import jm.web.Fecha;

/**
 *
 * @author Desarrollo
 */
public class frmHR_OrdenTrabajo extends HttpServlet {
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
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        HttpSession sesion = request.getSession(true);
        //String id_rol = (String)sesion.getAttribute("id_rol");
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

        String id = request.getParameter("idOrTr");
        String i = request.getParameter("i");

        Rol objRol = new Rol(this._ip, this._puerto, this._db, usuario, clave);
        
        Sector objSector = new Sector(this._ip, this._puerto, this._db, usuario, clave);
        ResultSet rsSectores = objSector.getSectores(id_sucursal);
        

        PlanServicio objPlanServicio = new PlanServicio(this._ip, this._puerto, this._db, usuario, clave);
        ResultSet rsPlanes = objPlanServicio.getPlanesTodos();
        
        
        OrdenTrabajo objOrdenTrabajo = new OrdenTrabajo(this._ip, this._puerto, this._db, usuario, clave);
        
        String [][] latitud_orientacion1 = {{"N","N"},{"S","S"}};
        String [][] longitud_orientacion1 = {{"E","E"},{"O","O"}};
        String [][] conformidad1 = {{"e","Excelente"},{"b","Buena"},{"m","Mala"}};
        String [][] estado_instalacion1 = {{"i","Instalado"},{"e","Emitido"}};
            
        try {
            String id_instalacion = "";
            String id_cliente = "";
            String ruc = "";
            String razon_social = "";
            String numero_orden = "";
            String txt_tipo_trabajo = "";
            String tipo_trabajo = "";
            String id_empleado = "";
            String direccion_instalacion = "";
            //String ax_materiales = "";
            String id_sector = "";
            String id_plan_actual = "";
            String ip = "";
            String mac = "";
            String receptor = "";
            String porcentaje_senal = "";
            String antena_acoplada = "";
            String latitud = "";
            String longitud = "";
            String altura = "";
            String altura_antena = "";
            String deviceclave = "";

            try{
                ResultSet rsHROrdenes = objOrdenTrabajo.getOrdenTrabajo(id);
                if(rsHROrdenes.next()){
                    id_instalacion = (rsHROrdenes.getString("id_instalacion")!=null) ? rsHROrdenes.getString("id_instalacion") : "";
                    id_cliente = (rsHROrdenes.getString("id_cliente")!=null) ? rsHROrdenes.getString("id_cliente") : "";
                    ruc = (rsHROrdenes.getString("ruc")!=null) ? rsHROrdenes.getString("ruc") : "";
                    razon_social = (rsHROrdenes.getString("razon_social")!=null) ? rsHROrdenes.getString("razon_social") : "";
                    numero_orden = (rsHROrdenes.getString("numero_orden")!=null) ? rsHROrdenes.getString("numero_orden") : "";
                    id_sector = (rsHROrdenes.getString("id_sector")!=null) ? rsHROrdenes.getString("id_sector") : "";
                    id_plan_actual = (rsHROrdenes.getString("id_plan_actual")!=null) ? rsHROrdenes.getString("id_plan_actual") : "";
                    txt_tipo_trabajo = (rsHROrdenes.getString("txt_tipo_trabajo")!=null) ? rsHROrdenes.getString("txt_tipo_trabajo") : "";
                    tipo_trabajo = (rsHROrdenes.getString("tipo_trabajo")!=null) ? rsHROrdenes.getString("tipo_trabajo") : "";
                    id_empleado = (rsHROrdenes.getString("id_empleado")!=null) ? rsHROrdenes.getString("id_empleado") : "";
                    direccion_instalacion = (rsHROrdenes.getString("direccion_instalacion")!=null) ? rsHROrdenes.getString("direccion_instalacion") : "";
                    ip = (rsHROrdenes.getString("ip")!=null) ? rsHROrdenes.getString("ip") : "";
                    mac = (rsHROrdenes.getString("mac")!=null) ? rsHROrdenes.getString("mac") : "";
                    receptor = (rsHROrdenes.getString("receptor")!=null) ? rsHROrdenes.getString("receptor") : "";
                    porcentaje_senal = (rsHROrdenes.getString("porcentaje_senal")!=null) ? rsHROrdenes.getString("porcentaje_senal") : "";
                    antena_acoplada = (rsHROrdenes.getString("antena_acoplada")!=null) ? rsHROrdenes.getString("antena_acoplada") : "";
                    latitud = (rsHROrdenes.getString("latitud")!=null) ? rsHROrdenes.getString("latitud") : "";
                    longitud = (rsHROrdenes.getString("longitud")!=null) ? rsHROrdenes.getString("longitud") : "";
                    altura = (rsHROrdenes.getString("altura")!=null) ? rsHROrdenes.getString("altura") : "";
                    altura_antena = (rsHROrdenes.getString("altura_antena")!=null) ? rsHROrdenes.getString("altura_antena") : "";
                    deviceclave = (rsHROrdenes.getString("deviceclave")!=null) ? rsHROrdenes.getString("deviceclave") : "";
                }
            }catch(Exception e){
                e.printStackTrace();
            }
            
            String latitud_hora = "";
            String latitud_minuto = "";
            String latitud_segundo = "";
            String latitud_orientacion = "";
            if(latitud.compareTo("")!=0){
                String vec[] = latitud.split(":");
                latitud_hora = vec[0];
                latitud_minuto = vec[1];
                latitud_segundo = vec[2];
                latitud_orientacion = vec[3];
            }

            String longitud_hora = "";
            String longitud_minuto = "";
            String longitud_segundo = "";
            String longitud_orientacion = "";
            if(longitud.compareTo("")!=0){
                String vec[] = longitud.split(":");
                longitud_hora = vec[0];
                longitud_minuto = vec[1];
                longitud_segundo = vec[2];
                longitud_orientacion = vec[3];
            }
            
            
            Bodega objBodega = new Bodega(this._ip, this._puerto, this._db, usuario, clave);
            String id_bodega = objBodega.getIdBodega(id_empleado);
            objBodega.cerrar();
            
            Material objMaterial = new Material(this._ip, this._puerto, this._db, usuario, clave);
            ResultSet rsMateriales = objMaterial.getMateriales(id_sucursal);
            objMaterial.cerrar();
            
            out.println("obj»ppp_html^foc»recomendacion"+(tipo_trabajo.compareTo("3")!=0 ?"^fun»_('id_plan_actual').disabled=true" : "") +"^frm»");
                        
            out.println("<form action='frmHojaRutaSolucionIndGuardar' onsubmit='return fac_ordenTrabajoGuardar(this)' autocomplete='off'>");
            out.println("<input type='hidden' id='id' name='id' value='"+id+"' />");
            out.println("<input type='hidden' id='id_empleado' name='id_empleado' value='"+id_empleado+"' />");

            out.println("<input id='id_ot' name='id_ot' type='hidden' value='"+ id + "' />"
                    + "<input id='id_ints' name='id_ints' type='hidden' value='"+ id_instalacion + "' />"
                    + "<input id='tip_tra' name='tip_tra' type='hidden' value='"+ tipo_trabajo + "' />"
                    + "<input id='id_cli' name='id_cli' type='hidden' value='"+ id_cliente + "' />"
                    + "<input id='id_bodega' name='id_bodega' type='hidden' value='"+ id_bodega + "' />"
                    + "<input id='ruc' name='ruc' type='hidden' value='"+ ruc + "' />"
                    + "<input id='raso' name='raso' type='hidden' value='"+ razon_social + "' />"
                    + "<input id='pos' name='pos' type='hidden' value='"+ i + "' />"
                    + "<input id='numero_orden' name='numero_orden' type='hidden' value='"+numero_orden+"' />");

            out.println("<table>");
            out.println("<tr><td>TIPO DE ORDEN DE TRABJO: </td><td><div style='float:left'>" + txt_tipo_trabajo + "</div><div style='float:right;font-size:14px'>No." + numero_orden + "</div></td></tr>");
            out.println("<tr><td>SOLUCIONADO: </td><td>"
                            + "<label><input type='radio' id='solucionado0' name='solucionado' value='true' checked />Si</label> &nbsp; "
                            + "<label><input type='radio' id='solucionado1' name='solucionado' value='false' />No</label>"
                            + "</td></tr>");
            out.println("<tr><td>CONFORMIDAD DEL TRABAJO: </td><td>" + DatosDinamicos.combo("conformidad", "m", conformidad1, "") + "</td></tr>");
            out.println("<tr><td>CONFORMIDAD DE ATENCION: </td><td>" + DatosDinamicos.combo("atencion", "m", conformidad1, "") + "</td></tr>");
            
            out.println("<tr><td valign=\"top\">MATERIALES UTILIZADOS: </td><td>"+this.setMateriales(rsMateriales)+"</td></tr>");
            
            out.println("<tr valign=\"top\"><td>RECOMENDACIONES: </td><td><textarea id='recomendacion' name='recomendacion' style='width:480px;height:36px' onfocus='this.select();'></textarea></td></tr>");
            out.println("<tr><td>SECTOR: </td><td>" + DatosDinamicos.combo(rsSectores, "id_sector", id_sector, "", " SIN CAMBIO ") + "</td></tr>");
            out.println("<tr><td>IP: </td><td><input id='ip' name='ip' type='text' size='80' maxlength='15' value=\""+ip+"\" /></td></tr>");
            out.println("<tr><td>PLAN: </td><td>" + DatosDinamicos.combo(rsPlanes, "id_plan_actual", id_plan_actual, "", " SIN CAMBIO ") + "</td></tr>");
            out.println("<tr><td>DIRECCION: </td><td><input id='direccion' name='direccion' size='80' onfocus='this.select();' value=\""+direccion_instalacion+"\" /></td></tr>");
            out.println("<tr><td>MAC ACTUAL: </td><td>"+mac+"</td></tr>");
            out.println("<tr><td>RECEPTOR DE SEÑAL ACTUAL: </td><td>"+receptor+"</td></tr>");
            
            String readonly = tipo_trabajo.compareTo("3")==0 ? "readonly" : "";
            out.println("<tr><td>MAC RETIRADA: </td><td id='axtmac_ant0'><input class='may' type='text' id='mac_ant0' name='mac_ant0' maxlength='30' size='50' "+readonly+" onkeyup=\"fac_cmbActivo('mac_ant0');fac_getActivosBodega('mac_ant0', 'fac_getReceptor(0, 1)', 1);\" />");
            if(tipo_trabajo.compareTo("3")!=0){
                out.println("<input type='button' value='verificar existencia del equipo' onclick=\"inf_getActivo()\" />");
            }
            out.print("</td></tr>");
            out.println("<tr><td>RECEPTOR DE SEÑAL RETIRADO: </td><td><input id='receptor_ant0' name='receptor_ant0' type='text' size='80' maxlength='40' "+readonly+" /></td></tr>");
            out.println("<tr><td>EQUIPOS RETIRADOS: </td><td>"
                    + "<input class='may' onfocus=\"this.value=''\" type='text' id='mac_ret0' name='mac_ret0' maxlength='60' size='80' "+readonly+" onkeyup=\"fac_cmbActivo('mac_ret0', 300);inf_getEquiposBodega(this.value, 0, 'macs_ret0', 1);\" />"
                    + "<div id='macs_ret0' style='margin-left:3px;'></div>"
                    + "</td></tr>");

            readonly = tipo_trabajo.compareTo("4")==0 ? "readonly" : "";
            out.println("<tr><td>MAC NUEVA: </td><td id='axtmac_act0'><input class='may' type='text' id='mac_act0' name='mac_act0' maxlength='30' size='30' "+readonly+" onkeyup=\"fac_cmbActivo('mac_act0');fac_getActivosBodega('mac_act0', 'fac_getReceptor(0, 2);inf_getMacsComplemento(0,2);', 2);\" />"
                    + " &nbsp;&nbsp;  CLAVE: "+deviceclave+"  &nbsp;&nbsp;  <label>Registrada<input type='checkbox' id='set_deviceclave' name='set_deviceclave' /></label><br /></td></tr>");
            out.println("<tr><td>RECEPTOR DE SEÑAL INSTALADO: </td><td><input id='receptor_act0' name='receptor_act0' type='text' size='80' maxlength='40' "+readonly+" /></td></tr>");
            out.println("<tr><td>EQUIPOS UTILIZADOS: </td><td>"
                    + "<input class='may' onfocus=\"this.value=''\" type='text' id='mac0' name='mac0' maxlength='60' size='80' "+readonly+" onkeyup=\"fac_cmbActivo('mac0', 500);inf_getEquiposBodega(this.value, 0, 'macs0', 2);\" />"
                    + "<div id='macs0' style='margin-left:3px;'></div>"
                    + "</td></tr>");

            out.println("<tr><td>% DE SEÑAL: </td><td><input id='senal' name='senal' type='text' maxlength='5' size='80' value='"+porcentaje_senal+"' /></td></tr>");
            out.println("<tr><td>ANTENA ACOPLADA: </td><td><input class='may' type='text' id='antena_acoplada' name='antena_acoplada' size='80' maxlength='34' onkeyup=\"fac_getAntenas('');\" value='"+antena_acoplada+"' /></td></tr>");
            out.println("<tr><td>LATITUD: </td><td>"
                    + "<input id='lat_h' name='lat_h' type='text' style='width:20px' maxlength='2' onkeypress=\"_numero(event);\" value=\""+latitud_hora+"\" /> º "
                            + "<input id='lat_m' name='lat_m' type='text' style='width:20px' maxlength='2' onkeypress=\"_numero(event);\" value=\""+latitud_minuto+"\" /> ' "
                            + "<input id='lat_s' name='lat_s' type='text' style='width:40px' maxlength='6' onkeypress=\"_evaluar(event, '0123456789.');\" value=\""+latitud_segundo+"\" /> '' " +
                            DatosDinamicos.combo("lat_o", latitud_orientacion, latitud_orientacion1, "") 
                            + "</td></tr>");
            out.println("<tr><td>LONGITUD: </td><td>"
                    + "<input id='lon_h' name='lon_h' type='text' style='width:20px' maxlength='2' onkeypress=\"_numero(event);\" value=\""+longitud_hora+"\" /> º "
                            + "<input id='lon_m' name='lon_m' type='text' style='width:20px' maxlength='2' onkeypress=\"_numero(event;)\" value=\""+longitud_minuto+"\" /> ' "
                            + "<input id='lon_s' name='lon_s' type='text' style='width:40px' maxlength='6' onkeypress=\"_evaluar(event, '0123456789.');\" value=\""+longitud_segundo+"\" /> '' " +
                            DatosDinamicos.combo("lon_o", longitud_orientacion, longitud_orientacion1, "")
                            + "</td></tr>");
            out.println("<tr><td>ALTITUD (m.s.n.m.): </td><td><input id='altura' name='altura' type='text' maxlength='20' size='80' onkeypress=\"_evaluar(event, '0123456789m');\" value='"+altura+"' /></td></tr>");
            out.println("<tr><td>ALTITUD ANTENA (m.): </td><td><input id='altura_antena' name='altura_antena' type='text' maxlength='20' size='80' onkeypress=\"_evaluar(event, '0123456789m');\" value='"+altura_antena+"' /></td></tr>");
            if(tipo_trabajo.compareTo("3")==0){
                out.println("<tr><td>FECHA INSTALACION: </td><td>"
                        + "<input id='fecha_instalacion' name='fecha_instalacion' type='text' size='10' maxlength='10' value='"+Fecha.getFecha("SQL")+"' onkeypress=\"_evaluar(event, '0123456789/');\" />"
                        + "<input type='button' value='...' onClick=\"SelectorFecha.crear('fecha_instalacion', 'SQL');\" />"
                        + "</td></tr>");
                out.println("<tr><td>ESTADO: </td><td>" + DatosDinamicos.combo("estado_instalacion", "", estado_instalacion1, "") + "</td></tr>");
            }
            out.println("</table>");
            
            out.println("<input type='submit' value='Registrar soluci&oacute;n' />");
            
            out.println("</form>");
                
        } finally {
            objPlanServicio.cerrar();
            objSector.cerrar();
            objRol.cerrar();
            objOrdenTrabajo.cerrar();
            out.close();
        }
    } 
    
    
    public String setMateriales(ResultSet rs)
    {
        String id_material = "";
        String html = "<fieldset><table>";
        try{
            int j=0;
            rs.beforeFirst();
            while(rs.next()){
                id_material = rs.getString("id_material")!=null ? rs.getString("id_material") : "";
                html += "<tr valign='top'>";
                html += "<td><input type='hidden' id='id_material"+j+"' name='id_material"+j+"' value='"+id_material+"' /></td>";
                html += "<td><input type='text' id='cantidad"+j+"' name='cantidad"+j+"' style='width:25px' maxlength='9' onkeypress=\"_numero(event)\" /></td>";
                html += "<td>"+(rs.getString("descripcion")!=null ? rs.getString("descripcion") : "")+"</td>";
                html += "</tr>";
                j++;
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        html += "</table></fieldset>";
        return html;
    }


    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /** 
     * Handles the HTTP <code>GET</code> method.
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        processRequest(request, response);
    } 

    /** 
     * Handles the HTTP <code>POST</code> method.
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        processRequest(request, response);
    }

    /** 
     * Returns a short description of the servlet.
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}
