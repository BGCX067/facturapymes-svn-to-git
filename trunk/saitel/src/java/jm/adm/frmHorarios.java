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
import jm.adm.clas.TablaDepreciacion;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import jm.cnt.clas.PlanCuenta;
import jm.adm.clas.Horario;
import jm.adm.clas.Sucursal;
import jm.web.DatosDinamicos;
/**
 *
 * @author Jorge
 */
public class frmHorarios extends HttpServlet {
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
        
        Sucursal objSucursal = new Sucursal(this._ip, this._puerto, this._db, usuario, clave);
        ResultSet rsSucursales = objSucursal.getSucursales();
        objSucursal.cerrar();

        
                try {
            String id = request.getParameter("id");
            String id_sucursal = "";
            String nombre = "";
            String sucursal = "1";
            boolean modalidad = true;
            String dias = "";
            boolean estado = true;
            String porcentaje = "0";

            if(id.compareTo("-1")!=0){
                Horario obj_Horario = new Horario(this._ip, this._puerto, this._db, usuario, clave);
                try{
                    ResultSet rsLista = obj_Horario.getHorario(id);
                    if(rsLista.next()){
                        nombre = (rsLista.getString("nombre")!=null) ? rsLista.getString("nombre") : "";
                        id_sucursal = (rsLista.getString("id_sucursal")!=null) ? rsLista.getString("id_sucursal") : "1";
                        modalidad = (rsLista.getString("modalidad")!=null) ? rsLista.getBoolean("modalidad") : false;
                        estado = (rsLista.getString("eliminado")!=null) ? rsLista.getBoolean("eliminado") : false;
                        rsLista.close();
                    }
                    ResultSet rsListaDia = obj_Horario.getDetalleHorario(id);
                    while(rsListaDia.next()){
                        dias += ((rsListaDia.getString("dia")!=null) ? rsListaDia.getString("dia") : "")+",";
                    }
                    dias=dias.substring(0,dias.length()-1);
                    rsListaDia.close();
                 }catch(Exception e){
                    e.printStackTrace();
                }finally{
                    obj_Horario.cerrar();
                }
            }


            String html = "obj»d_21^foc»lista^frm»";
            html += "<input type='hidden' id='id' name='id' value='"+id+"' /><table>";
            html += "<tr><td colspan='2' class='H3'>Formulario de Horarios</td></tr>";
            html += "<tr><td>Sucursal: <span class='marca'>*</span></td>";
            html += "<td>" + DatosDinamicos.combo(rsSucursales, "id_sucursal", id_sucursal, "", "", 335) + "</td></tr>";
            html += "<tr><td>Nombre del Horario: <span class='marca'>*</span></td>";
            html += "<td><input id='n_horario' name='n_horario' type='text' size='43' maxlength='60' value='"+nombre+"' required onkeypress=\"_alfanumerico(event);\" onblur=\"this.value=this.value._trim();\" /></td>";
            html += "<td><input id='n_horario_antes' name='n_horario_antes' type='hidden'  value='"+nombre+"' /></td>";
            html += "<tr><td>Modalidad: <span class='marca'>*</span></td>";
            html += "<td><input type='radio' id='modalidad0' name='modalidad' value='1' "+((modalidad) ? "checked" : "")+" /> 5/2 &nbsp;&nbsp;&nbsp;&nbsp;&nbsp; " +
                    "<input type='radio' id='modalidad1' name='modalidad' value='2' "+((!modalidad) ? "checked" : "")+" /> 14/7</td></tr>";
            html+= "<tr><td>Aplica para: <span class='marca'>*</span></td>";
            String[] diasArray = dias.split(",");
                for (int j = 0; j<diasArray.length; j++) {
                    if(diasArray[j].equals("1")){
                        html+= "<td><label><input type=\"checkbox\" id=\"1\" name=\"dia\" value=\"1\" disabled> Lunes</label>";
                        j++;
                        if(j>diasArray.length-1)
                            j=diasArray.length-1;
                    }
                    else{
                        html+= "<td><label><input type=\"checkbox\" id=\"1\" name=\"dia\" value=\"1\" checked> Lunes</label>";
                    }
                    if(diasArray[j].equals("2")){
                        html+= "<label><input type=\"checkbox\" id=\"2\" name=\"dia\" value=\"2\" disabled> Martes</label>";
                        j++;
                        if(j>diasArray.length-1)
                            j=diasArray.length-1;
                    }
                    else{
                        html+= "<label><input type=\"checkbox\" id=\"2\" name=\"dia\" value=\"2\" checked> Martes</label>";
                    }
                    if(diasArray[j].equals("3")){
                        html+= "<label><input type=\"checkbox\" id=\"3\" name=\"dia\" value=\"3\" disabled> Miercoles</label><br>";
                        j++;
                        if(j>diasArray.length-1)
                            j=diasArray.length-1;
                    }
                    else{
                        html+= "<label><input type=\"checkbox\" id=\"3\" name=\"dia\" value=\"3\" checked> Miercoles</label><br>";
                    }
                    if(diasArray[j].equals("4")){
                        html+= "<label><input type=\"checkbox\" id=\"4\" name=\"dia\" value=\"4\" disabled> Jueves</label>";
                        j++;
                        if(j>diasArray.length-1)
                            j=diasArray.length-1;
                    }
                    else{
                        html+= "<label><input type=\"checkbox\" id=\"4\" name=\"dia\" value=\"4\" checked> Jueves</label>";
                    }
                    if(diasArray[j].equals("5")){
                        html+= "<label><input type=\"checkbox\" id=\"5\" name=\"dia\" value=\"5\" disabled> Viernes</label>";
                        j++;
                        if(j>diasArray.length-1)
                            j=diasArray.length-1;
                    }
                    else{
                        html+= "<label><input type=\"checkbox\" id=\"5\" name=\"dia\" value=\"5\" checked> Viernes</label>";
                    }
                    if(diasArray[j].equals("6")){
                        html+= "<label><input type=\"checkbox\" id=\"6\" name=\"dia\" value=\"6\" disabled> Sabado</label>";
                        j++;
                        if(j>diasArray.length-1)
                            j=diasArray.length-1;
                    }
                    else{
                        html+= "<label><input type=\"checkbox\" id=\"6\" name=\"dia\" value=\"6\"> Sabado</label>";
                    }
                    if(diasArray[j].equals("7")){
                        html+= "<label><input type=\"checkbox\" id=\"7\" name=\"dia\" value=\"7\" disabled> Domingo</label>";
                        j++;
                        if(j>diasArray.length-1)
                            j=diasArray.length-1;
                    }
                    else{
                        html+= "<label><input type=\"checkbox\" id=\"7\" name=\"dia\" value=\"7\"> Domingo</label>";
                    }
                    break;
                }
                            html+= "<br><br></tr>";
            html+="<tr><td>Hora de Entrada (mañana): </td>"
                    + "<td><input type='number' id='hem' name='hem' style='width:40px' max=23 min=0 value='8'>  :  <input type='number' name='hem1' id='hem1' style='width:40px' max=59 min=0 value='30'>";
            html+="&nbsp;&nbsp;&nbsp;&nbsp;Hora de Salida (mañana): "
                    + "<input type='number' id='hsm' name='hsm' style='width:40px' max=23 min=0 value='13'>  :  <input type='number' name='hsm1' id='hsm1' style='width:40px' max=59 min=0 value='30'><br><br></td></tr>";
            html+="";
            html+="<tr><td>Hora de Entrada (tarde): </td>"
                    + "<td><input type='number' id='het' name='het' style='width:40px' max=23 min=0 value='15'>  :  <input type='number' name='het1' id='het1' style='width:40px' max=59 min=0 value='0'>";
            html+="&nbsp;&nbsp;&nbsp;&nbsp;Hora de Salida (tarde):&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"
                    + "<input type='number' id='hst' name='hst' style='width:40px' max=23 min=0 value='18'>  :  <input type='number' name='hst1' id='hst1' style='width:40px' max=59 min=0 value='0'><br><br></td></tr>";
            
            html+="<tr><td>Hora de Entrada (noche): </td>"
                    + "<td><input type='number' id='hen' name='hen' style='width:40px' max=23 min=0>  :  <input type='number' name='hen1' id='hen1' style='width:40px' max=59 min=0>";
            html+="&nbsp;&nbsp;&nbsp;&nbsp;Hora de Salida (noche):&nbsp;&nbsp;&nbsp;&nbsp;"
                    + "<input type='number' id='hsn' name='hsn' style='width:40px' max=23 min=0>  :  <input type='number' name='hsn1' id='hsn1' style='width:40px' max=59 min=0><br><br></td></tr>";
            
            html += "<td><input type='radio' id='estado0' name='estado' value='true' "+((estado) ? "checked" : "")+" /> Activo &nbsp;&nbsp;&nbsp;&nbsp;&nbsp; " +
                    "<input type='radio' id='estado1' name='estado' value='false' "+((!estado) ? "checked" : "")+" /> Inactivo</td></tr>";
            html += "</table>";
            html += "<fieldset>";
                html += "<legend>DATOS</legend>";
                html += "<div id='axTblPE'><TABLE cellspacing='0' cellpadding='0'><TR>" +
                            "<TH class='jm_TH' width='66'>Dia</TH>" +
                            "<TH class='jm_TH' width='66'>H. Entrada (maÃ±ana)</TH>" +
                            "<TH class='jm_TH' width='66'>H. Salida (maÃ±ana)</TH>" +
                            "<TH class='jm_TH' width='66'>H. Entrada (tarde)</TH>" +
                            "<TH class='jm_TH' width='66'>H. Salida (tarde)</TH>" +
                            "<TH class='jm_TH' width='66'>H. Entrada (noche)</TH>" +
                            "<TH class='jm_TH' width='66'>H. Salida (noche)</TH>" +
                            "<TH class='jm_TH' width='20'>&nbsp;</TH></TR></TABLE>";
                html += "<DIV style='overflow:auto;width:500px;height:130px;' id='sll'>" +
                "<TABLE class='jm_tabla' cellspacing='1' cellpadding='0' id='tblPE'>";
                
                int i=0;
                String id_detalle = "";
                String dia_ = "";
                String dia_detalle= "";
                String horaentrada = "";
                String horasalida = "";
                String horaentrada1= "";
                String horasalida1 = "";
                String horaentrada2 = "";
                String horasalida2 = "";
                Horario obj_Horario = new Horario(this._ip, this._puerto, this._db, usuario, clave);
                ResultSet rsLista = obj_Horario.getDetalleHorario(id);

            try {
                while(rsLista.next()){
                    id_detalle = (rsLista.getString("id_detalle")!=null) ? rsLista.getString("id_detalle") : "";
                    dia_ = (rsLista.getString("dia")!=null) ? rsLista.getString("dia") : "";
                    horaentrada = (rsLista.getString("horaentrada")!=null) ? rsLista.getString("horaentrada") : "";
                    horasalida = (rsLista.getString("horasalida")!=null) ? rsLista.getString("horasalida") : "";
                    horaentrada1 = (rsLista.getString("horaentrada1")!=null) ? rsLista.getString("horaentrada1") : "";
                    horasalida1 = (rsLista.getString("horasalida1")!=null) ? rsLista.getString("horasalida1") : "";
                    horaentrada2 = (rsLista.getString("horaentrada2")!=null) ? rsLista.getString("horaentrada2") : "";
                    horasalida2 = (rsLista.getString("horasalida2")!=null) ? rsLista.getString("horasalida2") : "";
                    dia_detalle = (rsLista.getString("dia_semana")!=null) ? rsLista.getString("dia_semana") : "";
                    html += "<tr id='rTI"+id_detalle+"' valign='top' class='jm_filaPar' style='cursor:pointer' onmouseover=\"this.className='jm_filaSobre'\" onmouseout=\"this.className='jm_filaPar'\">";
                    html += "<td width='67' style='cursor:pointer' onclick=\"adm_setHorario("+id_detalle+");\"><input id='adm_idDet"+id_detalle+"' name='adm_idDet"+id_detalle+"' type='hidden' value='"+dia_+"' />"+dia_detalle+"</td>";
                    html += "<td width='67' style='cursor:pointer' onclick=\"adm_setHorario("+id_detalle+");\"><input id='adm_hem"+id_detalle+"' name='adm_hem"+id_detalle+"' type='hidden' value='"+horaentrada+"' />"+horaentrada+"</td>";
                    html += "<td width='67' style='cursor:pointer' onclick=\"adm_setHorario("+id_detalle+");\"><input id='adm_hsm"+id_detalle+"' name='adm_hsm"+id_detalle+"' type='hidden' value='"+horasalida+"' />"+horasalida+"</td>";
                    html += "<td width='67' style='cursor:pointer' onclick=\"adm_setHorario("+id_detalle+");\"><input id='adm_het"+id_detalle+"' name='adm_het"+id_detalle+"' type='hidden' value='"+horaentrada1+"' />"+horaentrada1+"</td>";
                    html += "<td width='67' style='cursor:pointer' onclick=\"adm_setHorario("+id_detalle+");\"><input id='adm_hst"+id_detalle+"' name='adm_hst"+id_detalle+"' type='hidden' value='"+horasalida1+"' />"+horasalida1+"</td>";
                    html += "<td width='67' style='cursor:pointer' onclick=\"adm_setHorario("+id_detalle+");\"><input id='adm_hen"+id_detalle+"' name='adm_hen"+id_detalle+"' type='hidden' value='"+horaentrada2+"' />"+horaentrada2+"</td>";
                    html += "<td width='67' style='cursor:pointer' onclick=\"adm_setHorario("+id_detalle+");\"><input id='adm_hsn"+id_detalle+"' name='adm_hsn"+id_detalle+"' type='hidden' value='"+horasalida2+"' />"+horasalida2+"</td>";
                    html += "<td width='21' align='center'><div class='jm_icoCon' style='background-position:-545px -18px;' title='Eliminar' onclick=\"adm_setHorarioEliminar("+id_detalle+");\">&nbsp;</div></td>";
                    /*html += "<td align='center' width='100'><a href='formatoFactura?id="+id_punto_emision+"' target='_blank'>modificar formato</a></td>";
                    html += "<td align='center' width='20'><input type='hidden' id='idR"+i+"' value='"+id_punto_emision+"' />&nbsp;</td></tr>";*/
                    i++;
                }
                rsLista.close();    
            } catch (SQLException ex) {
                Logger.getLogger(frmHorarios.class.getName()).log(Level.SEVERE, null, ex);
            }
            finally {                    
                obj_Horario.cerrar();
            }
                html += "<input id='id_horario' name='id_horario' type='hidden' value="+id+">";
                html += "<input id='id_detalle' name='id_detalle' type='hidden' value='-1'>";
                html += "</table></div>";
                
                html += "</fieldset>";
            html += "<input type='submit' value='Guardar' onclick='adm_guardarHorario()'/>";

            out.print(html);

        } finally {
            //objPlanCuenta.cerrar();
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
