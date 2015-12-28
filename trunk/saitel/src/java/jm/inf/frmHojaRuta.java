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
import jm.inf.clas.HojaRuta;
import jm.inf.clas.OrdenTrabajo;
import jm.nom.clas.Empleado;
import jm.seg.clas.Rol;
import jm.web.Addons;
import jm.web.DatosDinamicos;
import jm.web.Fecha;
import jm.web.Matriz;

/**
 *
 * @author Desarrollo
 */
public class frmHojaRuta extends HttpServlet {
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
        String id_rol = (String)sesion.getAttribute("id_rol");
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
        String estado = request.getParameter("est");

        
        
        
        
        if(id.compareTo("-1")==0){
            estado = "0";
        }

        int _alto = 410;
        int _ancho = Integer.parseInt( request.getParameter("anch") ) - 60;
        int anchoPanel = _ancho/2 - 40;
        String _altBody = "245";

        Rol objRol = new Rol(this._ip, this._puerto, this._db, usuario, clave);

        HojaRuta objHojaRuta = new HojaRuta(this._ip, this._puerto, this._db, usuario, clave);
        
        Empleado objEmpleado = new Empleado(this._ip, this._puerto, this._db, usuario, clave);
        
        try {
            ResultSet rsEmpleados = objEmpleado.getEmpleadosBodega(id_sucursal);
            ResultSet rsEmpleadosNoDisponibles = objEmpleado.getEmpleadosBodegaNoDiponibles(id_sucursal);
            String num_hoja_ruta = "";
            String alias = usuario;
            String fecha = Fecha.getFecha("SQL");
            String hora = Fecha.getHora();
            String id_tecnico_resp = "";
            String observacion = "ninguna";
            boolean anulado = false;
            if(id.compareTo("-1")!=0){
                try{
                    ResultSet rs = objHojaRuta.getHojaRuta(id);
                    if(rs.next()){
                        num_hoja_ruta = rs.getString("num_hoja_ruta")!=null ? rs.getString("num_hoja_ruta") : "";
                        alias = rs.getString("usuario")!=null ? rs.getString("usuario") : "";
                        fecha = rs.getString("fecha")!=null ? Fecha.ISOaSQL(rs.getString("fecha")) : "";
                        hora = rs.getString("hora")!=null ? rs.getString("hora") : "";
                        id_tecnico_resp = rs.getString("id_tecnico_resp")!=null ? rs.getString("id_tecnico_resp") : "";
                        observacion = rs.getString("observacion")!=null ? rs.getString("observacion") : "";
                        anulado = rs.getString("anulado")!=null ? rs.getBoolean("anulado") : false;
                        rs.close();
                    }
                }catch(Exception e){
                    e.printStackTrace();
                }
            }
            


            String activo = (objRol.getAcceso(id_rol, "hojaRutaImprimir") ) ? "_('id_tecnico_resp').disabled=true;" : "";


            out.println("obj»cmp_html^foc»id_tecnico_resp^fun»_objTab.seleccionar("+(estado.compareTo("9")==0?2:estado)+");"+activo+"^frm»");

            out.println("<div class='grupoTab' style='width:"+(_ancho+10)+"px;'>");
            out.println("<div id='tabR0'>");
            if(estado.compareTo("0")==0){
                out.println("<a id='tab00' href='javascript:void(0);' class='jm_tabAct' onclick='_objTab.seleccionar(0);'>Generar</a>");
            }
            if(id.compareTo("-1")!=0 && estado.compareTo("1")==0 && objRol.getAcceso(id_rol, "hojaRutaRevisar")){
                out.println("<a id='tab01' href='javascript:void(0);' class='jm_tabAct' onclick='_objTab.seleccionar(1);'>Revisar</a>");
            }
            if(id.compareTo("-1")!=0 && estado.compareTo("3")==0 && objRol.getAcceso(id_rol, "hojaRutaRevisar")){
                out.println("<a id='tab03' href='javascript:void(0);' class='jm_tabAct' onclick='_objTab.seleccionar(3);'>En observaci&oacute;n</a>");
            }
            if(id.compareTo("-1")!=0 && (estado.compareTo("2")==0 || estado.compareTo("9")==0) && objRol.getAcceso(id_rol, "hojaRutaSolucion")){
                out.println("<a id='tab02' href='javascript:void(0);' class='jm_tabAct' onclick='_objTab.seleccionar(2);'>Solucionar</a>");
            }
            out.println("</div>");




            
            out.println("<div id='tabR1'>");
            
            /* PRIMER TAB */


            OrdenTrabajo objOrdenTrabajo = new OrdenTrabajo(this._ip, this._puerto, this._db, usuario, clave);
            ResultSet rsTrabajos = objOrdenTrabajo.getTipos();
            objOrdenTrabajo.cerrar();

            ResultSet rsFechasOrdenes = objHojaRuta.getFechasOrdenesPendientes(id_sucursal, id);

            if(estado.compareTo("0")==0){

                out.println("<div id='tab10' class='jm_tabHTML' style='display:block;width:"+_ancho+"px;height:"+_alto+"px;'>");

                out.println("<form action='frmHojaRutaGuardar' onsubmit='return fac_trabajoTareaGuardar(this)' autocomplete='off'>");
                out.println("<input type='hidden' id='id' name='id' value='"+id+"' />");
                out.println("<input type='hidden' id='num_hoja_ruta' name='num_hoja_ruta' value='"+num_hoja_ruta+"' />");
                out.println("<input type='hidden' id='imp' name='imp' value='0' />");

                out.println("<table width='100%'>");
                out.println("<tr><td>Usuario: </td>");
                out.println("<td colspan='3'>"+alias+"</td></tr>");
                out.println("<tr><td>Responsable: <span class='marca'>*</span></td>");
                out.println("<td>"+DatosDinamicos.combogroup(rsEmpleados, "id_tecnico_resp", id_tecnico_resp, "")+
                        DatosDinamicos.combogroup2(rsEmpleadosNoDisponibles, "id_tecnico_resp", id_tecnico_resp, "")+"</optgroup></select></td>");
                out.println("<td>Fecha: "+ fecha + "</td>");
                out.println("<td>Hora: "+ hora + "</td></tr>");
                out.println("<tr><td valign='top'>Observaci&oacute;n: <span class='marca'>*</span></td>");
                out.println("<td colspan='3'><textarea id='observacion' name='observacion' style='width:680px;height:25px' onfocus='this.select();'>"+observacion+"</textarea></td></tr>");
                out.println("</table>");


                if(!objRol.getAcceso(id_rol, "hojaRutaImprimir")){

                    out.println("<table border='0' cellspacing='0' cellpadding='0'>");
                    out.println("<tr style='height:20px'><th style='background-color:#F1E2C7;'>Ordenes de Trabajo Asignadas </th>");
                    out.println("<td>&nbsp;</td>");
                    out.println("<th style='background-color:#F1E2C7;'>Ordenes de trabajo reportadas y pendientes</th></tr>");
                    
                    out.println("<tr valign='top' class='jm_filaPar'><td>");
                    
                    out.println("<div style=\"overflow:auto;width:"+anchoPanel+"px;border:1px solid #000000\">");
                    out.println("<table cellspacing='0' cellpadding='0' width='1135'><tr>"
                            + "<th class='jm_TH' style='width:22px'>&nbsp;</th>"
                            + "<th class='jm_TH' style='width:70px'>No.</th>"
                            + "<th class='jm_TH' style='width:120px'>PLAN</th>"
                            + "<th class='jm_TH' style='width:120px'>TIPO</th>"
                            + "<th class='jm_TH' style='width:90px'>F. REPORTE</th>"
                            + "<th class='jm_TH' style='width:100px'>CANTON</th>"
                            + "<th class='jm_TH' style='width:100px'>PARROQUIA</th>"
                            + "<th class='jm_TH' style='width:200px'>DIRECCION</th>"
                            + "<th class='jm_TH' style='width:300px'>DIAGNOSTICO</th>"
                            + "</tr></table>");
                    out.println("<div class='columna' onmouseover=\"_('tab10').scrollLeft=0\" style='overflow:auto;width:1155px;height:"+ _altBody +"px;'>");
                    out.println("<div class='jm_tabla' id='tblAs'>");
                    int i=0;
                    try{
                        ResultSet rsHROrdenes = objHojaRuta.getOrdenes(id);
                        String clase = "jm_filaPar";
                        while(rsHROrdenes.next()){
                            clase = i%2==0 ? "jm_filaPar" : "jm_filaImp";
                            out.println("<div id='rot"+i+"' class='jm_fila "+clase+"'><label>");
                            out.println("<div class='jm_columna caja "+clase+"' style='width:22px'><input id='ch_ot"+i+"' name='ch_ot"+i+"' type='checkbox'><input type='hidden' id='asg"+i+"' name='asg"+i+"' value='1' />"
                                    + "<input id='id_ot"+i+"' name='id_ot"+i+"' type='hidden' value='"+ ((rsHROrdenes.getString("id_orden_trabajo")!=null) ? rsHROrdenes.getString("id_orden_trabajo") : "") + "'></div>");
                            out.println("<div class='jm_columna caja "+clase+"' style='width:70px'>" + ((rsHROrdenes.getString("numero_orden")!=null) ? rsHROrdenes.getString("numero_orden") : "") + "</div>");
                            out.println("<div class='jm_columna caja "+clase+"' style='width:120px' id='axtpo"+i+"'>" + ((rsHROrdenes.getString("plan")!=null) ? rsHROrdenes.getString("plan") : "") + "</div>");
                            out.println("<div class='jm_columna caja "+clase+"' style='width:120px'>" + ((rsHROrdenes.getString("txt_tipo_trabajo")!=null) ? rsHROrdenes.getString("txt_tipo_trabajo") : "") + "</div>");
                            out.println("<div class='jm_columna caja "+clase+"' style='width:90px'>" + ((rsHROrdenes.getString("fecha_reporte")!=null) ? Fecha.ISOaSQL(rsHROrdenes.getString("fecha_reporte")) : "") + "</div>");
                            out.println("<div class='jm_columna caja "+clase+"' style='width:100px'>" + ((rsHROrdenes.getString("ciudad")!=null) ? rsHROrdenes.getString("ciudad") : "") + "</div>");
                            out.println("<div class='jm_columna caja "+clase+"' style='width:100px'>" + ((rsHROrdenes.getString("parroquia")!=null) ? rsHROrdenes.getString("parroquia") : "") + "</div>");
                            out.println("<div class='jm_columna caja "+clase+"' style='width:200px'>" + ((rsHROrdenes.getString("direccion_instalacion")!=null) ? rsHROrdenes.getString("direccion_instalacion") : "") + "</div>");
                            out.println("<div class='jm_columna caja "+clase+"' style='width:300px'>" + ((rsHROrdenes.getString("diagnostico_tecnico")!=null) ? rsHROrdenes.getString("diagnostico_tecnico") : "") + "</div>");
                            out.println("</label></div>");
                            i++;
                        }
                    }catch(Exception e){
                        e.printStackTrace();
                    }
                    out.println("</div>");
                    out.println("</div>");
                    out.println("</div>");
                    out.println("</td>");
                    
                    out.println("<td width='65' valign='middle'>");
                    out.println("<p> &nbsp;&nbsp; <input type='button' value='&nbsp; << &nbsp;' onclick=\"fac_setOrdenesRuta(1);\" /></p>");
                    out.println("<p> &nbsp;&nbsp; <input type='button' value='&nbsp; >> &nbsp;' onclick=\"fac_setOrdenesRuta(0);\" /></p>");
                    out.println("</td>");
                    
                    out.println("<td>");
                    
                    out.println("<div style=\"overflow:auto;width:"+anchoPanel+"px;border:1px solid #000000\">");
                    out.println("<table cellspacing='0' cellpadding='0' width='1135'><tr>"
                            + "<th class='jm_TH' style='width:22px'>&nbsp;</th>"
                            + "<th class='jm_TH' style='width:70px'>No.</th>"
                            + "<th class='jm_TH' style='width:120px'>PLAN</th>"
                            + "<th class='jm_TH' style='width:120px'>" + DatosDinamicos.combo(rsTrabajos, "fil_tipo_trabajo", "", "fac_filOrdener("+id+", "+i+", 't')", " TODO ", 110) + "</th>"
                            + "<th class='jm_TH' style='width:90px'>" + DatosDinamicos.combo(rsFechasOrdenes, "fil_fecha_reporte", "", "fac_filOrdener("+id+", "+i+", 'f')", " TODO ", 80) + "</th>"
                            + "<th class='jm_TH' style='width:100px'>CANTON</th>"
                            + "<th class='jm_TH' style='width:100px'>PARROQUIA</th>"
                            + "<th class='jm_TH' style='width:200px'>DIRECCION</th>"
                            + "<th class='jm_TH' style='width:300px'>DIAGNOSTICO</th>"
                            + "</tr></table>");
                    out.println("<div class='columna' onmouseover=\"_('tab10').scrollLeft=1000\" style='overflow:auto;width:1155px;height:"+ _altBody +"px;'>");
                    out.println("<div class='jm_tabla' id='tblPe'>");
                    try{
                        ResultSet rsOrdenes = objHojaRuta.getOrdenesPendientes(id_sucursal, id);
                        String clase = "jm_filaPar";
                        String color_estado = "";
                        while(rsOrdenes.next()){
                            clase = i%2==0 ? "jm_filaPar" : "jm_filaImp";
                            color_estado = (rsOrdenes.getString("estado")!=null) ? rsOrdenes.getString("estado") : "1";
                            if(color_estado.compareTo("3")==0){
                                clase = "jm_filaEsp";
                            }
                            out.println("<div id='rot"+i+"' class='jm_fila "+clase+"'><label>");
                            out.println("<div class='jm_columna caja "+clase+"' style='width:22px'><input id='ch_ot"+i+"' name='ch_ot"+i+"' type='checkbox'><input type='hidden' id='asg"+i+"' name='asg"+i+"' value='0' />"
                                    + "<input id='id_ot"+i+"' name='id_ot"+i+"' type='hidden' value='"+ ((rsOrdenes.getString("id_orden_trabajo")!=null) ? rsOrdenes.getString("id_orden_trabajo") : "") + "'></div>");
                            out.println("<div class='jm_columna caja "+clase+"' style='width:70px'>" + ((rsOrdenes.getString("numero_orden")!=null) ? rsOrdenes.getString("numero_orden") : "") + "</div>");
                            out.println("<div class='jm_columna caja "+clase+"' style='width:120px' id='axtpo"+i+"'>" + ((rsOrdenes.getString("plan")!=null) ? rsOrdenes.getString("plan") : "") + "</div>");
                            out.println("<div class='jm_columna caja "+clase+"' style='width:120px'>" + ((rsOrdenes.getString("txt_tipo_trabajo")!=null) ? rsOrdenes.getString("txt_tipo_trabajo") : "") + "</div>");
                            out.println("<div class='jm_columna caja "+clase+"' style='width:90px'>" + ((rsOrdenes.getString("fecha_reporte")!=null) ? Fecha.ISOaSQL(rsOrdenes.getString("fecha_reporte")) : "") + "</div>");
                            out.println("<div class='jm_columna caja "+clase+"' style='width:100px'>" + ((rsOrdenes.getString("ciudad")!=null) ? rsOrdenes.getString("ciudad") : "") + "</div>");
                            out.println("<div class='jm_columna caja "+clase+"' style='width:100px'>" + ((rsOrdenes.getString("parroquia")!=null) ? rsOrdenes.getString("parroquia") : "") + "</div>");
                            out.println("<div class='jm_columna caja "+clase+"' style='width:200px'>" + ((rsOrdenes.getString("direccion_instalacion")!=null) ? rsOrdenes.getString("direccion_instalacion") : "") + "</div>");
                            out.println("<div class='jm_columna caja "+clase+"' style='width:300px'>" + ((rsOrdenes.getString("diagnostico_tecnico")!=null) ? rsOrdenes.getString("diagnostico_tecnico") : "") + "</div>");
                            out.println("</label></div>");
                            i++;
                        }
                    }catch(Exception e){
                        e.printStackTrace();
                    }
                    out.println("</div></div>");
                    out.println("</div>");
                    
                    out.println("</td></tr></table>");

                    if(!anulado){
                        if((id.compareTo("-1")==0 || objRol.getAcceso(id_rol, "hojaRutaEditar"))){
                            out.println("<input type='submit' id='btnGr' value='Guardar' onclick=\"_('imp').value=0;this.disabled=_('btnGrIm').disabled=true;\" />");
                            out.println(" &nbsp; <input type='submit' id='btnGrIm' value='Guardar e Imprimir' onclick=\"_('imp').value=1;this.disabled=_('btnGr').disabled=true;\" />");
                        }
                        if(id.compareTo("-1")!=0){
                            out.println(" &nbsp; <input type='button' value='Imprimir' onclick=\"imprimir('pdfHojaRuta?id="+id+"');\" />");
                        }
                    }

                }else{

                    out.println("<table cellspacing='0' cellpadding='0'><tr>"
                            + "<th class='jm_TH' style='width:22px'>#</th>"
                            + "<th class='jm_TH' style='width:70px'>No.</th>"
                            + "<th class='jm_TH' style='width:90px'>TIPO</th>"
                            + "<th class='jm_TH' style='width:90px'>F. REPORTE</th>"
                            + "<th class='jm_TH' style='width:90px'>CANTON</th>"
                            + "<th class='jm_TH' style='width:190px'>DIRECCION</th>"
                            + "<th class='jm_TH' style='width:250px'>DIAGNOSTICO</th>"
                            + "</tr></table>");
                    out.println("<div class='columna' onmouseover=\"_('tab10').scrollLeft=0\" style='overflow:auto;width:840px;height:270px;'>");
                    out.println("<div class='jm_tabla' id='tblAs'>");
                    try{
                        ResultSet rsHROrdenes = objHojaRuta.getOrdenes(id);
                        String clase = "jm_filaPar";
                        int i=1;
                        while(rsHROrdenes.next()){
                            clase = i%2==0 ? "jm_filaPar" : "jm_filaImp";
                            out.println("<div class='jm_fila "+clase+"'>");
                            out.println("<div class='jm_columna caja "+clase+"' style='width:22px'>"+i+"</div>");
                            out.println("<div class='jm_columna caja "+clase+"' style='width:70px'>" + ((rsHROrdenes.getString("numero_orden")!=null) ? rsHROrdenes.getString("numero_orden") : "") + "</div>");
                            out.println("<div class='jm_columna caja "+clase+"' style='width:90px'>" + ((rsHROrdenes.getString("txt_tipo_trabajo")!=null) ? rsHROrdenes.getString("txt_tipo_trabajo") : "") + "</div>");
                            out.println("<div class='jm_columna caja "+clase+"' style='width:90px'>" + ((rsHROrdenes.getString("fecha_reporte")!=null) ? Fecha.ISOaSQL(rsHROrdenes.getString("fecha_reporte")) : "") + "</div>");
                            out.println("<div class='jm_columna caja "+clase+"' style='width:90px'>" + ((rsHROrdenes.getString("ciudad")!=null) ? rsHROrdenes.getString("ciudad") : "") + "</div>");
                            out.println("<div class='jm_columna caja "+clase+"' style='width:190px'>" + ((rsHROrdenes.getString("direccion_instalacion")!=null) ? rsHROrdenes.getString("direccion_instalacion") : "") + "</div>");
                            out.println("<div class='jm_columna caja "+clase+"' style='width:250px'>" + ((rsHROrdenes.getString("diagnostico_tecnico")!=null) ? rsHROrdenes.getString("diagnostico_tecnico") : "") + "</div>");
                            out.println("</div>");
                            i++;
                        }
                    }catch(Exception e){
                        e.printStackTrace();
                    }
                    out.println("</div></div>");

                    if(!anulado){
                        if(estado.compareTo("9")!=0 && id.compareTo("-1")!=0){
                            out.println(" &nbsp; <input type='button' value='Imprimir' onclick=\"imprimir('pdfHojaRuta?id="+id+"');\" />");
                        }
                    }

                }

                out.println("</form>");

                out.println("</div>");

            }


            /*  segundo tab    */


            String [][] revision1 = {{"1","Regresar a reportada"},{"3","Transferir a observaci&oacute;n"},{"2","Solucionar"}};

            if(id.compareTo("-1")!=0 && estado.compareTo("1")==0 && objRol.getAcceso(id_rol, "hojaRutaRevisar")){

                out.println("<div id='tab11' class='jm_tabHTML' style='display:none;width:"+_ancho+"px;height:"+_alto+"px;'>");

                out.println("<form action='frmHojaRutaRevisionGuardar' onsubmit='return _AJAX.enviarForm(this)' autocomplete='off'>");
                out.println("<input type='hidden' id='id' name='id' value='"+id+"' />");
                out.println("<input type='hidden' id='num_hoja_ruta' name='num_hoja_ruta' value='"+num_hoja_ruta+"' />");
                
                out.println("<table cellspacing='0' cellpadding='0'><tr>"
                            + "<th class='jm_TH' style='width:22px'>#</th>"
                            + "<th class='jm_TH' style='width:70px'>No.</th>"
                            + "<th class='jm_TH' style='width:90px'>TIPO</th>"
                            + "<th class='jm_TH' style='width:90px'>SECTOR</th>"
                            + "<th class='jm_TH' style='width:190px'>DIRECCION</th>"
                            + "<th class='jm_TH' style='width:160px'>REVISION</th>"
                            + "<th class='jm_TH' style='width:160px'>MOTIVO</th>"
                            + "</tr></table>");
                out.println("<div class='columna' style='overflow:auto;width:820px;height:370px;'>");
                out.println("<div class='jm_tabla' id='tblAs'>");
                try{
                    ResultSet rsHROrdenes = objHojaRuta.getOrdenes(id);
                    String clase = "jm_filaPar";
                    int i=0;
                    while(rsHROrdenes.next()){
                        String motivo_pendiente = (rsHROrdenes.getString("motivo_pendiente")!=null) ? rsHROrdenes.getString("motivo_pendiente") : "";
                        String usuario_realizacion = (rsHROrdenes.getString("usuario_realizacion")!=null) ? rsHROrdenes.getString("usuario_realizacion") : "";
                        clase = i%2==0 ? "jm_filaPar" : "jm_filaImp";
                        out.println("<div class='jm_fila "+clase+"'>");
                        out.println("<div class='jm_columna caja "+clase+"' style='width:22px;height:45px;'>"+i+
                                "<input id='id_ots"+i+"' name='id_ots"+i+"' type='hidden' value='"+ ((rsHROrdenes.getString("id_orden_trabajo")!=null) ? rsHROrdenes.getString("id_orden_trabajo") : "") + "' /></div>");
                        out.println("<div class='jm_columna caja "+clase+"' style='width:70px;height:45px;'>" + ((rsHROrdenes.getString("numero_orden")!=null) ? rsHROrdenes.getString("numero_orden") : "") + "</div>");
                        out.println("<div class='jm_columna caja "+clase+"' style='width:90px;height:45px;'>" + ((rsHROrdenes.getString("txt_tipo_trabajo")!=null) ? rsHROrdenes.getString("txt_tipo_trabajo") : "") + "</div>");
                        out.println("<div class='jm_columna caja "+clase+"' style='width:90px;height:45px;'>" + ((rsHROrdenes.getString("sector")!=null) ? rsHROrdenes.getString("sector") : "") + "</div>");
                        out.println("<div class='jm_columna caja "+clase+"' style='width:190px;height:45px;'>" + ((rsHROrdenes.getString("direccion_instalacion")!=null) ? rsHROrdenes.getString("direccion_instalacion") : "") + "</div>");
                        out.println("<div class='jm_columna caja "+clase+"' style='width:160px;height:45px;'>" + DatosDinamicos.combo("rev"+i, "2", revision1, "fac_setMotivo("+i+")") + "</div>");
                        out.println("<div class='jm_columna caja "+clase+"' style='width:160px;height:45px;'><textarea id='mot"+i+"' name='mot"+i+"' style=\"width:150px;height:35px;visibility:hidden\">"+ 
                                usuario_realizacion + " " + Fecha.getFecha("SQL") + " .- " + motivo_pendiente + "</textarea></div>");
                        out.println("</div>");
                        i++;
                    }
                }catch(Exception e){
                    e.printStackTrace();
                }
                out.println("</div></div>");
                if(!anulado){
                    if(estado.compareTo("9")!=0 && id.compareTo("-1")!=0){
                        out.println(" &nbsp; <input type='submit' value='Registrar revisiones' />");
                    }
                }

                out.println("</form>");

                out.println("</div>");
            }




            /*  tercer tab    */


            String [][] solicionRevisiones = {{"3","En observaci&oacute;n"},{"1","Regresar a reportada"},{"9","Solucionar"}};

            if(id.compareTo("-1")!=0 && estado.compareTo("3")==0 && objRol.getAcceso(id_rol, "hojaRutaRevisar")){

                out.println("<div id='tab13' class='jm_tabHTML' style='display:none;width:"+_ancho+"px;height:"+_alto+"px;'>");

                out.println("<form action='frmHojaRutaPendientesGuardar' onsubmit='return _AJAX.enviarForm(this)' autocomplete='off'>");
                //out.println("<input type='hidden' id='id' name='id' value='"+id+"' />");
                //out.println("<input type='hidden' id='num_hoja_ruta' name='num_hoja_ruta' value='"+num_hoja_ruta+"' />");

                out.println("<table cellspacing='0' cellpadding='0'><tr>"
                            + "<th class='jm_TH' style='width:22px'>#</th>"
                            + "<th class='jm_TH' style='width:70px'>No.</th>"
                            + "<th class='jm_TH' style='width:90px'>TIPO</th>"
                            + "<th class='jm_TH' style='width:90px'>SECTOR</th>"
                            + "<th class='jm_TH' style='width:190px'>DIRECCION</th>"
                            + "<th class='jm_TH' style='width:160px'>SOLUCION</th>"
                            + "<th class='jm_TH' style='width:160px'>MOTIVO</th>"
                            + "</tr></table>");
                out.println("<div class='columna' style='overflow:auto;width:820px;height:370px;'>");
                out.println("<div class='jm_tabla' id='tblAs'>");
                try{
                    ResultSet rsHROrdenes = objHojaRuta.getOrdenes(id_sucursal, "'3'");
                    String clase = "jm_filaPar";
                    String numero_orden = "";
                    String id_instalacion = "";
                    String tipo_trabajo = "";
                    int i=0;
                    while(rsHROrdenes.next()){
                        numero_orden = ((rsHROrdenes.getString("numero_orden")!=null) ? rsHROrdenes.getString("numero_orden") : "");
                        id_instalacion = ((rsHROrdenes.getString("id_instalacion")!=null) ? rsHROrdenes.getString("id_instalacion") : "");
                        tipo_trabajo = ((rsHROrdenes.getString("tipo_trabajo")!=null) ? rsHROrdenes.getString("tipo_trabajo") : "");
                        clase = i%2==0 ? "jm_filaPar" : "jm_filaImp";
                        out.println("<div class='jm_fila "+clase+"'>");
                        out.println("<div class='jm_columna caja "+clase+"' style='width:22px;height:45px;'>"+i+
                                "<input id='id_ortrs"+i+"' name='id_ortrs"+i+"' type='hidden' value='"+ ((rsHROrdenes.getString("id_orden_trabajo")!=null) ? rsHROrdenes.getString("id_orden_trabajo") : "") + "' />"
                                +"<input id='numero_orden"+i+"' name='numero_orden"+i+"' type='hidden' value='"+ numero_orden + "' />"
                                +"<input id='id_instalacion"+i+"' name='id_instalacion"+i+"' type='hidden' value='"+ id_instalacion + "' />"
                                +"<input id='tipo_trabajo"+i+"' name='tipo_trabajo"+i+"' type='hidden' value='"+ tipo_trabajo + "' />"
                                + "</div>");
                        out.println("<div class='jm_columna caja "+clase+"' style='width:70px;height:45px;'>" + numero_orden + "</div>");
                        out.println("<div class='jm_columna caja "+clase+"' style='width:90px;height:45px;'>" + ((rsHROrdenes.getString("txt_tipo_trabajo")!=null) ? rsHROrdenes.getString("txt_tipo_trabajo") : "") + "</div>");
                        out.println("<div class='jm_columna caja "+clase+"' style='width:90px;height:45px;'>" + ((rsHROrdenes.getString("sector")!=null) ? rsHROrdenes.getString("sector") : "") + "</div>");
                        out.println("<div class='jm_columna caja "+clase+"' style='width:190px;height:45px;'>" + ((rsHROrdenes.getString("direccion_instalacion")!=null) ? rsHROrdenes.getString("direccion_instalacion") : "") + "</div>");
                        out.println("<div class='jm_columna caja "+clase+"' style='width:160px;height:45px;'>" + DatosDinamicos.combo("solRev"+i, "3", solicionRevisiones, "") + "</div>");
                        out.println("<div class='jm_columna caja "+clase+"' style='width:160px;height:45px;'><textarea id='motiv"+i+"' name='motiv"+i+"' style=\"width:150px;height:35px;\">" + ((rsHROrdenes.getString("motivo_pendiente")!=null) ? rsHROrdenes.getString("motivo_pendiente") : "") + "</textarea></div>");
                        out.println("</div>");
                        i++;
                    }
                }catch(Exception e){
                    e.printStackTrace();
                }
                out.println("</div></div>");
                if(!anulado){
                    if(estado.compareTo("9")!=0 && id.compareTo("-1")!=0){
                        out.println(" &nbsp; <input type='submit' value='Guardar' />");
                    }
                }

                out.println("</form>");

                out.println("</div>");
            }




            /*  tercer tab    */

            /*Bodega objBodega = new Bodega(this._ip, this._puerto, this._db, usuario, clave);
            String id_bodega = objBodega.getIdBodega(id_tecnico_resp);
            objBodega.cerrar();*/
            if(!objEmpleado.personalizacionesPendientes(usuario)){
                if(id.compareTo("-1")!=0 && (estado.compareTo("2")==0 || estado.compareTo("9")==0) && objRol.getAcceso(id_rol, "hojaRutaSolucion")){

                    out.println("<div id='tab12' class='jm_tabHTML' style='display:none;width:"+_ancho+"px;height:"+_alto+"px;'>");

                    out.println("<div>No. de hoja de ruta: "+num_hoja_ruta+"</div>");

                    out.println("<table cellspacing='0' cellpadding='0'><tr>"
                                + "<th class='jm_TH' style='width:22px'>#</th>"
                                + "<th class='jm_TH' style='width:70px'>No.</th>"
                                + "<th class='jm_TH' style='width:90px'>TIPO</th>"
                                + "<th class='jm_TH' style='width:90px'>SECTOR</th>"
                                + "<th class='jm_TH' style='width:240px'>DIRECCION</th>"
                                + "<th class='jm_TH' style='width:300px'>DIAGNOSTICO TECNICO</th>"
                                + "</tr></table>");
                    out.println("<div class='columna' style='overflow:auto;width:850px;height:370px;'>");
                    out.println("<div class='jm_tabla' id='tblAs'>");
                    try{
                        ResultSet rsHROrdenes = objHojaRuta.getOrdenesSolucionar(id, estado);
                        String clase = "jm_filaPar";
                        String numero_orden = "";
                        String onC = "";
                        int i=0;
                        while(rsHROrdenes.next()){
                            String id_orden_trabajo = (rsHROrdenes.getString("id_orden_trabajo")!=null) ? rsHROrdenes.getString("id_orden_trabajo") : "";
                            numero_orden = ((rsHROrdenes.getString("numero_orden")!=null) ? rsHROrdenes.getString("numero_orden") : "");
                            clase = i%2==0 ? "jm_filaPar" : "jm_filaImp";
                            onC = estado.compareTo("2")==0 ? "onclick=\"inf_ordenTrabajoSolucionar("+id_orden_trabajo+", "+i+")\"" : "";
                            out.println("<div id='rotSol"+i+"' class='jm_fila "+clase+"' style=\"cursor:pointer\" "+onC+" >");
                            out.println("<div class='jm_columna caja "+clase+"' style='width:22px;height:45px;'>"+(i+1)+
                                    "<input id='id_ortrs"+i+"' name='id_ortrs"+i+"' type='hidden' value='"+ ((rsHROrdenes.getString("id_orden_trabajo")!=null) ? rsHROrdenes.getString("id_orden_trabajo") : "") + "' />"
                                    +"<input id='numero_orden"+i+"' name='numero_orden"+i+"' type='hidden' value='"+ numero_orden + "' />"
                                    + "</div>");
                            out.println("<div class='jm_columna caja "+clase+"' style='width:70px;height:45px;'>" + numero_orden + "</div>");
                            out.println("<div class='jm_columna caja "+clase+"' style='width:90px;height:45px;'>" + ((rsHROrdenes.getString("txt_tipo_trabajo")!=null) ? rsHROrdenes.getString("txt_tipo_trabajo") : "") + "</div>");
                            out.println("<div class='jm_columna caja "+clase+"' style='width:90px;height:45px;'>" + ((rsHROrdenes.getString("sector")!=null) ? rsHROrdenes.getString("sector") : "") + "</div>");
                            out.println("<div class='jm_columna caja "+clase+"' style='width:240px;height:45px;'>" + ((rsHROrdenes.getString("direccion_instalacion")!=null) ? rsHROrdenes.getString("direccion_instalacion") : "") + "</div>");
                            out.println("<div class='jm_columna caja "+clase+"' style='width:300px;height:45px;'>" + ((rsHROrdenes.getString("diagnostico_tecnico")!=null) ? rsHROrdenes.getString("diagnostico_tecnico") : "") + "</div>");
                            out.println("</div>");
                            i++;
                        }
                    }catch(Exception e){
                        e.printStackTrace();
                    }
                    out.println("</div></div>");




                    /*
                     * String [][] latitud_orientacion1 = {{"N","N"},{"S","S"}};
                    String [][] longitud_orientacion1 = {{"E","E"},{"O","O"}};
                    String [][] conformidad1 = {{"e","Excelente"},{"b","Buena"},{"m","Mala"}};
                    String [][] estado_instalacion1 = {{"i","Instalado"},{"e","Emitido"}};

                    out.println("<form action='frmHojaRutaSolucionGuardar' onsubmit='return fac_trabajoTareaSolucionar(this)' autocomplete='off'>");
                    out.println("<input type='hidden' id='id' name='id' value='"+id+"' />");
                    out.println("<input type='hidden' id='num_hoja_ruta' name='num_hoja_ruta' value='"+num_hoja_ruta+"' />");
                    out.println("<input type='hidden' id='id_tecnico_resp1' name='id_tecnico_resp1' value='"+id_tecnico_resp+"' />");
                    out.println("<input id='id_bodega' name='id_bodega' type='hidden' value='"+ id_bodega + "' />");
                    out.println("<table cellspacing='0' width='2340' cellpadding='0'><tr>"
                            + "<th class='jm_TH' style='width:22px'>&nbsp;</th>"
                            + "<th class='jm_TH' style='width:60px'>No.</th>"
                            + "<th class='jm_TH' style='width:80px'>SOLUCION</th>"
                            + "<th class='jm_TH' style='width:80px'>CONF. DEL TRABAJO</th>"
                            + "<th class='jm_TH' style='width:80px'>CONF. DE ATENCION</th>"
                            + "<th class='jm_TH' style='width:200px'>MATERIALES</th>"
                            + "<th class='jm_TH' style='width:150px'>RECOMENDACIONES</th>"
                            + "<th class='jm_TH' style='width:90px'>SECTOR</th>"
                            + "<th class='jm_TH' style='width:80px'>IP</th>"
                            + "<th class='jm_TH' style='width:90px'>PLAN</th>"
                            + "<th class='jm_TH' style='width:150px'>DIRECCION</th>"
                            + "<th class='jm_TH' style='width:100px'>MACs ANT.</th>"
                            + "<th class='jm_TH' style='width:90px'>RECEPTOR DE SEÑAL ANT.</th>"
                            + "<th class='jm_TH' style='width:100px'>MACs NUEVAS</th>"
                            + "<th class='jm_TH' style='width:90px'>RECEPTOR DE SEÑAL NUEVO</th>"
                            + "<th class='jm_TH' style='width:60px'>% DE SEÑAL</th>"
                            + "<th class='jm_TH' style='width:90px'>ANTENA ACOPLADA</th>"
                            + "<th class='jm_TH' style='width:180px'>LATITUD</th>"
                            + "<th class='jm_TH' style='width:180px'>LONGITUD</th>"
                            + "<th class='jm_TH' style='width:60px'>ALTITUD</th>"
                            + "<th class='jm_TH' style='width:60px'>ALTITUD ANTENA</th>"
                            + "<th class='jm_TH' style='width:130px'>FECHA INSTALACION</th>"
                            + "<th class='jm_TH' style='width:75px'>ESTADO</th>"
                            + "</tr></table>");
                    out.println("<div class='columna' onmouseover=\"_('tab10').scrollLeft=0\" style='overflow:auto;width:2390px;height:345px;'>");
                    out.println("<div class='jm_tabla' id='tblAs'>");
                    try{
                        Sector objSector = new Sector(this._ip, this._puerto, this._db, usuario, clave);
                        ResultSet rsSectores = objSector.getSectores(id_sucursal);

                        PlanServicio objPlanServicio = new PlanServicio(this._ip, this._puerto, this._db, usuario, clave);
                        ResultSet rsPlanes = objPlanServicio.getPlanesTodos();

                        Antena objAntena = new Antena(this._ip, this._puerto, this._db, usuario, clave);
                        ResultSet rsAntenasAcopladas = objAntena.getAntenas();

                        Material objMaterial = new Material(this._ip, this._puerto, this._db, usuario, clave);
                        ResultSet rsMateriales = objMaterial.getMateriales(id_sucursal);


                        String clase = "jm_filaPar";
                        int i=0;
                        String id_orden_trabajo = "";
                        String numero_orden = "";
                        String txt_tipo_trabajo = "";
                        String tipo_trabajo = "";
                        String ax_materiales = "";
                        boolean solucionado = false;
                        String conformidad = "e";
                        String atencion = "e";
                        String recomendacion = "";
                        String id_sector = "";
                        String ip = "";
                        String mac = "";
                        String macp = "";
                        String macg = "";
                        String receptor = "";
                        String porcentaje_senal = "";
                        String antena_acoplada = "";
                        String latitud = "";
                        String longitud = "";
                        String altura = "";
                        String altura_antena = "";
                        String fecha_instalacion = "";
                        String estado_instalacion = "";

                        if(estado.compareTo("9")==0){
                            ResultSet rsHROrdenes = objHojaRuta.getOrdenInstalacion(id);
                            while(rsHROrdenes.next()){
                                id_orden_trabajo = (rsHROrdenes.getString("id_orden_trabajo")!=null) ? rsHROrdenes.getString("id_orden_trabajo") : "";
                                numero_orden = (rsHROrdenes.getString("numero_orden")!=null) ? rsHROrdenes.getString("numero_orden") : "";
                                solucionado = (rsHROrdenes.getString("solucionado")!=null) ? rsHROrdenes.getBoolean("solucionado") : false;
                                conformidad = (rsHROrdenes.getString("conformidad")!=null) ? rsHROrdenes.getString("conformidad") : "e";
                                atencion = (rsHROrdenes.getString("atencion")!=null) ? rsHROrdenes.getString("atencion") : "e";
                                recomendacion = (rsHROrdenes.getString("recomendacion")!=null) ? rsHROrdenes.getString("recomendacion") : "";
                                id_sector = (rsHROrdenes.getString("id_sector")!=null) ? rsHROrdenes.getString("id_sector") : "";
                                ip = (rsHROrdenes.getString("ip")!=null) ? rsHROrdenes.getString("ip") : "";
                                mac = (rsHROrdenes.getString("mac")!=null) ? rsHROrdenes.getString("mac") : "";
                                macp = (rsHROrdenes.getString("macp")!=null) ? rsHROrdenes.getString("macp") : "";
                                macg = (rsHROrdenes.getString("macg")!=null) ? rsHROrdenes.getString("macg") : "";
                                receptor = (rsHROrdenes.getString("receptor")!=null) ? rsHROrdenes.getString("receptor") : "";
                                porcentaje_senal = (rsHROrdenes.getString("porcentaje_senal")!=null) ? rsHROrdenes.getString("porcentaje_senal") : "";
                                antena_acoplada = (rsHROrdenes.getString("antena_acoplada")!=null) ? rsHROrdenes.getString("antena_acoplada") : "";
                                latitud = (rsHROrdenes.getString("latitud")!=null) ? rsHROrdenes.getString("latitud") : "";
                                longitud = (rsHROrdenes.getString("longitud")!=null) ? rsHROrdenes.getString("longitud") : "";
                                altura = (rsHROrdenes.getString("altura")!=null) ? rsHROrdenes.getString("altura") : "";
                                altura_antena = (rsHROrdenes.getString("altura_antena")!=null) ? rsHROrdenes.getString("altura_antena") : "";
                                fecha_instalacion = (rsHROrdenes.getString("fecha_instalacion")!=null) ? Fecha.ISOaSQL(rsHROrdenes.getString("fecha_instalacion")) : "";
                                estado_instalacion = (rsHROrdenes.getString("estado_instalacion")!=null) ? rsHROrdenes.getString("estado_instalacion") : "";

                                txt_tipo_trabajo = (rsHROrdenes.getString("txt_tipo_trabajo")!=null) ? rsHROrdenes.getString("txt_tipo_trabajo") : "";
                                tipo_trabajo = (rsHROrdenes.getString("tipo_trabajo")!=null) ? rsHROrdenes.getString("tipo_trabajo") : "";

                                ResultSet rsCants = objMaterial.consulta("select id_material, cantidad from tbl_orden_trabajo_material where id_orden_trabajo="+id_orden_trabajo);
                                String materiales = this.setMateriales(rsMateriales, rsCants);

                                String latitud_hora = "";
                                String latitud_minuto = "";
                                String latitud_segundo = "";
                                if(latitud.compareTo("")!=0){
                                    String vec[] = latitud.split(":");
                                    latitud_hora = vec[0];
                                    latitud_minuto = vec[1];
                                    latitud_segundo = vec[2];
                                }

                                String longitud_hora = "";
                                String longitud_minuto = "";
                                String longitud_segundo = "";
                                if(longitud.compareTo("")!=0){
                                    String vec[] = longitud.split(":");
                                    longitud_hora = vec[0];
                                    longitud_minuto = vec[1];
                                    longitud_segundo = vec[2];
                                }

                                clase = i%2==0 ? "jm_filaPar" : "jm_filaImp";

                                out.println("<div id='rotSol"+i+"' class='jm_fila "+clase+"' title='" + numero_orden + " &nbsp; " + txt_tipo_trabajo + "'>");
                                out.println("<div class='jm_columna caja1 "+clase+"' style='width:20px'>&nbsp;</div>");
                                out.println("<div class='jm_columna caja1 "+clase+"' style='width:60px'><input type='hidden' id='numero_orden"+i+"' name='numero_orden"+i+"' value='"+numero_orden+"' />" + numero_orden + "</div>");

                                out.println("<div class='jm_columna caja1 "+clase+"' style='width:80px'>"
                                        + "<label><input type='radio' id='solucionado0"+i+"' name='solucionado"+i+"' value='true' "+(solucionado?"checked":"")+" />Si</label> &nbsp; "
                                        + "<label><input type='radio' id='solucionado1"+i+"' name='solucionado"+i+"' value='false' "+(!solucionado?"checked":"")+" />No</label></div>");
                                out.println("<div class='jm_columna caja1 "+clase+"' style='width:80px'>" + DatosDinamicos.combo("conformidad"+i, conformidad, conformidad1, "") + "</div>");
                                out.println("<div class='jm_columna caja1 "+clase+"' style='width:80px'>" + DatosDinamicos.combo("atencion"+i, atencion, conformidad1, "") + "</div>");

                                ax_materiales = materiales.replace("<<X>>", String.valueOf(i));
                                out.println("<div class='jm_columna caja1 "+clase+"' style='width:200px;overflow:auto'>"+ax_materiales+"</div>");

                                out.println("<div class='jm_columna caja1 "+clase+"' style='width:150px'><textarea id='recomendacion"+i+"' name='recomendacion"+i+"' style='width:145px;height:46px' onfocus='this.select();'>"+recomendacion+"</textarea></div>");

                                out.println("<div class='jm_columna caja1 "+clase+"' style='width:90px'>" + DatosDinamicos.combo(rsSectores, "id_sector"+i, id_sector, "", " SIN CAMBIO ", 85) + "</div>");
                                out.println("<div class='jm_columna caja1 "+clase+"' style='width:80px'>"+ip+"</div>");
                                out.println("<div class='jm_columna caja1 "+clase+"' style='width:90px'>" + DatosDinamicos.combo(rsPlanes, "id_plan_actual"+i, "", "", " SIN CAMBIO ", 85) + "</div>");
                                out.println("<div class='jm_columna caja1 "+clase+"' style='width:150px'><textarea id='direccion"+i+"' name='direccion"+i+"' style='width:145px;height:46px' onfocus='this.select();'></textarea></div>");

                                out.println("<div class='jm_columna caja1 "+clase+"' style='width:100px' id='axtmac_ant"+i+"'>"+(tipo_trabajo.compareTo("4")==0 ? mac+"<br />"+macp+"<br />"+macg : "")+"</div>");
                                out.println("<div class='jm_columna caja1 "+clase+"' style='width:90px'>"+(tipo_trabajo.compareTo("4")==0 ? receptor : "")+"</div>");

                                out.println("<div class='jm_columna caja1 "+clase+"' style='width:100px' id='axtmac_act"+i+"'>"+(tipo_trabajo.compareTo("4")!=0 ? mac+"<br />"+macp+"<br />"+macg : "")+"</div>");
                                out.println("<div class='jm_columna caja1 "+clase+"' style='width:90px'>"+(tipo_trabajo.compareTo("4")!=0 ? receptor : "")+"</div>");


                                out.println("<div class='jm_columna caja1 "+clase+"' style='width:60px'><input id='senal"+i+"' name='senal"+i+"' type='text' style='width:55px' maxlength='5' onkeypress=\"_evaluar(event,'0123456789.')\" value='"+porcentaje_senal+"' /></div>");
                                out.println("<div class='jm_columna caja1 "+clase+"' style='width:90px'>" + DatosDinamicos.combo(rsAntenasAcopladas, "antena_acoplada"+i, antena_acoplada, "", " SIN CAMBIO ", 85) + "</div>");

                                out.println("<div class='jm_columna caja1 "+clase+"' style='width:180px'>"
                                        + "<input id='lat_h"+i+"' name='lat_h"+i+"' value='"+latitud_hora+"' type='text' style='width:20px' maxlength='2' onkeypress=\"_numero(event)\" /> º "
                                        + "<input id='lat_m"+i+"' name='lat_m"+i+"' value='"+latitud_minuto+"' type='text' style='width:20px' maxlength='2' onkeypress=\"_numero(event)\" /> ' "
                                        + "<input id='lat_s"+i+"' name='lat_s"+i+"' value='"+latitud_segundo+"' type='text' style='width:40px' maxlength='6' onkeypress=\"_evaluar(event, '0123456789.')\" /> '' " +
                                        DatosDinamicos.combo("lat_o"+i, "", latitud_orientacion1, "") + "</div>");

                                out.println("<div class='jm_columna caja1 "+clase+"' style='width:180px'>"
                                        + "<input id='lon_h"+i+"' name='lon_h"+i+"' type='text' value='"+longitud_hora+"' style='width:20px' maxlength='2' onkeypress=\"_numero(event)\" /> º "
                                        + "<input id='lon_m"+i+"' name='lon_m"+i+"' type='text' value='"+longitud_minuto+"' style='width:20px' maxlength='2' onkeypress=\"_numero(event)\" /> ' "
                                        + "<input id='lon_s"+i+"' name='lon_s"+i+"' type='text' value='"+longitud_segundo+"' style='width:40px' maxlength='6' onkeypress=\"_evaluar(event, '0123456789.')\" /> '' " +
                                        DatosDinamicos.combo("lon_o"+i, "", longitud_orientacion1, "") + "</div>");
                                out.println("<div class='jm_columna caja1 "+clase+"' style='width:60px'><input id='altura"+i+"' name='altura"+i+"' value='"+altura+"' type='text' style='width:53px' maxlength='20' onkeypress=\"_evaluar(event, '0123456789m')\" /></div>");
                                out.println("<div class='jm_columna caja1 "+clase+"' style='width:60px'><input id='altura_antena"+i+"' name='altura_antena"+i+"' value='"+altura_antena+"' type='text' style='width:53px' maxlength='20' onkeypress=\"_evaluar(event, '0123456789m')\" /></div>");

                                if(tipo_trabajo.compareTo("3")==0){
                                    out.println("<div class='jm_columna caja1 "+clase+"' style='width:130px'><input id='fecha_instalacion"+i+"' name='fecha_instalacion"+i+"' value='"+fecha_instalacion+"' type='text' size='10' maxlength='10' value='"+Fecha.getFecha("SQL")+"' onkeypress=\"_evaluar(event, '0123456789/')\" />"
                                        + "<input type='button' value='...' onClick=\"SelectorFecha.crear('fecha_instalacion"+i+"', 'SQL');\" /></div>");
                                    out.println("<div class='jm_columna caja1 "+clase+"' style='width:75px'>" + DatosDinamicos.combo("estado_instalacion"+i, estado_instalacion, estado_instalacion1, "") + "</div>");
                                }else{
                                    out.println("<div class='jm_columna caja1 "+clase+"' style='width:130px'>&nbsp;</div>");
                                    out.println("<div class='jm_columna caja1 "+clase+"' style='width:75px'>&nbsp;</div>");
                                }
                                out.println("</div>");
                                i++;
                            }
                        }else{
                            String materiales = this.setMateriales(rsMateriales);
                            ResultSet rsHROrdenes = objHojaRuta.getOrdenesSolucionar(id);
                            while(rsHROrdenes.next()){
                                numero_orden = (rsHROrdenes.getString("numero_orden")!=null) ? rsHROrdenes.getString("numero_orden") : "";
                                txt_tipo_trabajo = (rsHROrdenes.getString("txt_tipo_trabajo")!=null) ? rsHROrdenes.getString("txt_tipo_trabajo") : "";
                                tipo_trabajo = (rsHROrdenes.getString("tipo_trabajo")!=null) ? rsHROrdenes.getString("tipo_trabajo") : "";
                                clase = i%2==0 ? "jm_filaPar" : "jm_filaImp";
                                out.println("<div id='rotSol"+i+"' class='jm_fila "+clase+"' title='" + numero_orden + " &nbsp; " + txt_tipo_trabajo + "'>");
                                out.println("<div class='jm_columna caja1 "+clase+"' style='width:20px'><input id='ch_ots"+i+"' name='ch_ots"+i+"' type='checkbox' checked='checked'>"
                                        + "<input id='id_ots"+i+"' name='id_ots"+i+"' type='hidden' value='"+ ((rsHROrdenes.getString("id_orden_trabajo")!=null) ? rsHROrdenes.getString("id_orden_trabajo") : "") + "' />"
                                        + "<input id='id_ints"+i+"' name='id_ints"+i+"' type='hidden' value='"+ ((rsHROrdenes.getString("id_instalacion")!=null) ? rsHROrdenes.getString("id_instalacion") : "") + "' />"
                                        + "<input id='tip_tra"+i+"' name='tip_tra"+i+"' type='hidden' value='"+ ((rsHROrdenes.getString("tipo_trabajo")!=null) ? rsHROrdenes.getString("tipo_trabajo") : "") + "' />"
                                        + "<input id='id_cli"+i+"' name='id_cli"+i+"' type='hidden' value='"+ ((rsHROrdenes.getString("id_cliente")!=null) ? rsHROrdenes.getString("id_cliente") : "") + "' />"
                                        + "<input id='ruc"+i+"' name='ruc"+i+"' type='hidden' value='"+ ((rsHROrdenes.getString("ruc")!=null) ? rsHROrdenes.getString("ruc") : "") + "' />"
                                        + "<input id='raso"+i+"' name='raso"+i+"' type='hidden' value='"+ ((rsHROrdenes.getString("razon_social")!=null) ? rsHROrdenes.getString("razon_social") : "") + "' />"
                                        + "<input type='hidden' id='numero_orden"+i+"' name='numero_orden"+i+"' value='"+numero_orden+"' />"
                                        + "</div>");
                                out.println("<div class='jm_columna caja1 "+clase+"' style='width:60px'>" + numero_orden + "</div>");

                                out.println("<div class='jm_columna caja1 "+clase+"' style='width:80px'>"
                                        + "<label><input type='radio' id='solucionado0"+i+"' name='solucionado"+i+"' value='true' checked />Si</label> &nbsp; "
                                        + "<label><input type='radio' id='solucionado1"+i+"' name='solucionado"+i+"' value='false' />No</label></div>");
                                out.println("<div class='jm_columna caja1 "+clase+"' style='width:80px'>" + DatosDinamicos.combo("conformidad"+i, "", conformidad1, "_('g"+i+"').style.visibility='visible'") + "</div>");
                                out.println("<div class='jm_columna caja1 "+clase+"' style='width:80px'>" + DatosDinamicos.combo("atencion"+i, "", conformidad1, "_('g"+i+"').style.visibility='visible'") + "</div>");
                                ax_materiales = materiales.replace("<<X>>", String.valueOf(i));
                                out.println("<div class='jm_columna caja1 "+clase+"' style='width:200px;overflow:auto'>"+ax_materiales+"</div>");

                                out.println("<div class='jm_columna caja1 "+clase+"' style='width:150px'><textarea id='recomendacion"+i+"' name='recomendacion"+i+"' style='width:145px;height:46px' onfocus='this.select();' onkeyup=\"_('g"+i+"').style.visibility='visible'\"></textarea></div>");

                                out.println("<div class='jm_columna caja1 "+clase+"' style='width:90px'>" + DatosDinamicos.combo(rsSectores, "id_sector"+i, "", "_('g"+i+"').style.visibility='visible'", " SIN CAMBIO ", 85) + "</div>");
                                out.println("<div class='jm_columna caja1 "+clase+"' style='width:80px'><input id='ip"+i+"' name='ip"+i+"' type='text' style='width:75px' maxlength='15' onkeypress=\"_evaluar(event, '0123456789.');_('g"+i+"').style.visibility='visible';\" /></div>");
                                out.println("<div class='jm_columna caja1 "+clase+"' style='width:90px'>" + DatosDinamicos.combo(rsPlanes, "id_plan_actual"+i, "", "_('g"+i+"').style.visibility='visible'", " SIN CAMBIO ", 85) + "</div>");
                                out.println("<div class='jm_columna caja1 "+clase+"' style='width:150px'><textarea id='direccion"+i+"' name='direccion"+i+"' style='width:145px;height:46px' onfocus='this.select();' onkeyup=\"_('g"+i+"').style.visibility='visible'\"></textarea></div>");

                                out.println("<div class='jm_columna caja1 "+clase+"' style='width:100px' id='axtmac_ant"+i+"'>"
                                        + "<input class='may' type='text' id='mac_ant"+i+"' name='mac_ant"+i+"' style='width:95px' maxlength='34' onkeyup=\"fac_cmbActivo('mac_ant"+i+"');fac_getActivosBodega('mac_ant"+i+"', 'fac_getReceptor("+i+", 1)', 1);_('g"+i+"').style.visibility='visible';\" />"
                                        + "<input class='may' type='text' id='mac_antp"+i+"' name='mac_antp"+i+"' style='width:95px' maxlength='34' onkeyup=\"fac_cmbActivo('mac_antp"+i+"');fac_getActivosBodega('mac_antp"+i+"', 'fac_getReceptor("+i+", 1)', 1);_('g"+i+"').style.visibility='visible';\" />"
                                        + "<input class='may' type='text' id='mac_antg"+i+"' name='mac_antg"+i+"' style='width:95px' maxlength='34' onfocus=\"\" onkeyup=\"fac_cmbActivo('mac_antg"+i+"');fac_getActivosBodega('mac_antg"+i+"', 'fac_getReceptor("+i+", 1)', 1);_('g"+i+"').style.visibility='visible';\" />"
                                        //+ "<textarea class='may' type='text' id='mac_antp"+i+"' name='mac_antp"+i+"' style='width:95px;height:27px' onkeyup=\"fac_cmbActivo('mac_antp"+i+"');fac_getActivosBodega('mac_antp"+i+"', '', 1);_('g"+i+"').style.visibility='visible';\" ></textarea>"
                                        + "</div>");
                                out.println("<div class='jm_columna caja1 "+clase+"' style='width:90px'><input id='receptor_ant"+i+"' name='receptor_ant"+i+"' type='text' style='width:85px' maxlength='40' onkeyup=\"_('g"+i+"').style.visibility='visible'\" /></div>");
                                out.println("<div class='jm_columna caja1 "+clase+"' style='width:100px' id='axtmac_act"+i+"'>"
                                        + "<input class='may' type='text' id='mac_act"+i+"' name='mac_act"+i+"' style='width:95px' maxlength='34' onfocus=\"\" onkeyup=\"fac_cmbActivo('mac_act"+i+"');fac_getActivosBodega('mac_act"+i+"', 'fac_getReceptor("+i+", 2)', 2);_('g"+i+"').style.visibility='visible';\" />"
                                        + "<input class='may' type='text' id='mac_actp"+i+"' name='mac_actp"+i+"' style='width:95px' maxlength='34' onfocus=\"\" onkeyup=\"fac_cmbActivo('mac_actp"+i+"');fac_getActivosBodega('mac_actp"+i+"', 'fac_getReceptor("+i+", 2)', 2);_('g"+i+"').style.visibility='visible';\" />"
                                        + "<input class='may' type='text' id='mac_actg"+i+"' name='mac_actg"+i+"' style='width:95px' maxlength='34' onfocus=\"\" onkeyup=\"fac_cmbActivo('mac_actg"+i+"');fac_getActivosBodega('mac_actg"+i+"', 'fac_getReceptor("+i+", 2)', 2);_('g"+i+"').style.visibility='visible';\" />"
                                        + "</div>");
                                out.println("<div class='jm_columna caja1 "+clase+"' style='width:90px'><input id='receptor_act"+i+"' name='receptor_act"+i+"' type='text' style='width:85px' maxlength='40' onkeyup=\"_('g"+i+"').style.visibility='visible'\" /></div>");


                                out.println("<div class='jm_columna caja1 "+clase+"' style='width:60px'><input id='senal"+i+"' name='senal"+i+"' type='text' style='width:55px' maxlength='5' onkeypress=\"_evaluar(event,'0123456789.');_('g"+i+"').style.visibility='visible';\" /></div>");
                                //out.println("<div class='jm_columna caja1 "+clase+"' style='width:90px'>" + DatosDinamicos.combo(rsAntenasAcopladas, "antena_acoplada"+i, "", "", " SIN CAMBIO ", 85) + "</div>");
                                out.println("<div class='jm_columna caja1 "+clase+"' style='width:90px'>"
                                        + "<input class='may' type='text' id='antena_acoplada"+i+"' name='antena_acoplada"+i+"' style='width:85px' maxlength='34' onfocus=\"\" onkeyup=\"fac_getAntenas("+i+");_('g"+i+"').style.visibility='visible';\" /></div>");

                                out.println("<div class='jm_columna caja1 "+clase+"' style='width:180px'><input id='lat_h"+i+"' name='lat_h"+i+"' type='text' style='width:20px' maxlength='2' onkeypress=\"_numero(event);_('g"+i+"').style.visibility='visible';\" /> º "
                                        + "<input id='lat_m"+i+"' name='lat_m"+i+"' type='text' style='width:20px' maxlength='2' onkeypress=\"_numero(event);_('g"+i+"').style.visibility='visible';\" /> ' "
                                        + "<input id='lat_s"+i+"' name='lat_s"+i+"' type='text' style='width:40px' maxlength='6' onkeypress=\"_evaluar(event, '0123456789.');_('g"+i+"').style.visibility='visible';\" /> '' " +
                                        DatosDinamicos.combo("lat_o"+i, "", latitud_orientacion1, "") + "</div>");
                                out.println("<div class='jm_columna caja1 "+clase+"' style='width:180px'><input id='lon_h"+i+"' name='lon_h"+i+"' type='text' style='width:20px' maxlength='2' onkeypress=\"_numero(event);_('g"+i+"').style.visibility='visible';\" /> º "
                                        + "<input id='lon_m"+i+"' name='lon_m"+i+"' type='text' style='width:20px' maxlength='2' onkeypress=\"_numero(event;_('g"+i+"').style.visibility='visible';)\" /> ' "
                                        + "<input id='lon_s"+i+"' name='lon_s"+i+"' type='text' style='width:40px' maxlength='6' onkeypress=\"_evaluar(event, '0123456789.');_('g"+i+"').style.visibility='visible';\" /> '' " +
                                        DatosDinamicos.combo("lon_o"+i, "", longitud_orientacion1, "") + "</div>");
                                out.println("<div class='jm_columna caja1 "+clase+"' style='width:60px'><input id='altura"+i+"' name='altura"+i+"' type='text' style='width:53px' maxlength='20' onkeypress=\"_evaluar(event, '0123456789m');_('g"+i+"').style.visibility='visible';_('g"+i+"').style.visibility='visible';\" /></div>");
                                out.println("<div class='jm_columna caja1 "+clase+"' style='width:60px'><input id='altura_antena"+i+"' name='altura_antena"+i+"' type='text' style='width:53px' maxlength='20' onkeypress=\"_evaluar(event, '0123456789m');_('g"+i+"').style.visibility='visible';;_('g"+i+"').style.visibility='visible';\" /></div>");

                                if(tipo_trabajo.compareTo("3")==0){
                                    out.println("<div class='jm_columna caja1 "+clase+"' style='width:130px'><input id='fecha_instalacion"+i+"' name='fecha_instalacion"+i+"' type='text' size='10' maxlength='10' value='"+Fecha.getFecha("SQL")+"' onkeypress=\"_evaluar(event, '0123456789/');_('g"+i+"').style.visibility='visible';\" />"
                                        + "<input type='button' value='...' onClick=\"SelectorFecha.crear('fecha_instalacion"+i+"', 'SQL');\" /></div>");
                                    out.println("<div class='jm_columna caja1 "+clase+"' style='width:75px'>" + DatosDinamicos.combo("estado_instalacion"+i, "", estado_instalacion1, "_('g"+i+"').style.visibility='visible';") + "</div>");
                                }else{
                                    out.println("<div class='jm_columna caja1 "+clase+"' style='width:130px'>&nbsp;</div>");
                                    out.println("<div class='jm_columna caja1 "+clase+"' style='width:75px'>&nbsp;</div>");
                                }
                                out.println("<div class='jm_columna caja1 "+clase+"' style='width:22px'>"
                                        + "<div class='jm_icoCon' id='g"+i+"' style='visibility:hidden;background-position: -545px 0px;' onclick='fac_ordenTrabajoGuardar("+i+");' title='guardar'>&nbsp;</div>"
                                        + "</div>");

                                out.println("</div>");
                                i++;
                            }

                            objMaterial.cerrar();
                        }
                    }catch(Exception e){
                        e.printStackTrace();
                    }
                    out.println("</div></div>");

                    if(!anulado){
                        if(estado.compareTo("9")!=0 && id.compareTo("-1")!=0){
                            //out.println(" &nbsp; <input type='button' value='Cambiar a reportadas ordenes de trabajo no solucionadas' onclick='fac_noSolucionar()' /> &nbsp;&nbsp;&nbsp;");
                            out.println(" &nbsp; <input type='submit' value='Registrar soluciones' />");
                        }
                    }
                    out.println("</form>");*/

                    out.println("</div>");
                }
            }else{
                out.println("<div id='tab12' class='jm_tabHTML' style='display:none;width:"+_ancho+"px;height:"+_alto+"px;'>");

                    out.println("<div>Por favor, acepte todas las personalizaciones a su nombre antes de continuar</div>");
                out.println("</div>");
            }

            out.println("</div></div>");


        } finally {
            objEmpleado.cerrar();
            objRol.cerrar();
            objHojaRuta.cerrar();
            out.close();
        }
    } 

    public String setMateriales(ResultSet rs)
    {
        String id_material = "";
        String html = "<table>";
        try{
            int j=0;
            rs.beforeFirst();
            while(rs.next()){
                id_material = rs.getString("id_material")!=null ? rs.getString("id_material") : "";
                html += "<tr valign='top'>";
                html += "<td><input type='hidden' id='id_material<<X>>"+j+"' name='id_material<<X>>"+j+"' value='"+id_material+"' /></td>";
                        //+ "<input type='checkbox' id='chMat<<X>>"+j+"' name='chMat<<X>>"+j+"' /></td>";
                html += "<td><input type='text' id='cantidad<<X>>"+j+"' name='cantidad<<X>>"+j+"' onkeyup=\"_('g<<X>>').style.visibility='visible';\" style='width:25px' maxlength='9' /></td>";
                html += "<td>"+(rs.getString("descripcion")!=null ? rs.getString("descripcion") : "")+"</td>";
                html += "</tr>";
                j++;
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        html += "</table>";
        return html;
    }

    public String setMateriales(ResultSet rs, ResultSet rsCantidades)
    {
        String matCant[][] = Matriz.ResultSetAMatriz(rsCantidades);
        String id_material = "";
        String html = "<table>";
        try{
            int fil=0;
            String cantidad = "";
            int j = 0;
            rs.beforeFirst();
            while(rs.next()){
                id_material = rs.getString("id_material")!=null ? rs.getString("id_material") : "";
                cantidad = "";
                fil = Matriz.enMatriz(matCant, id_material, 0);
                if(fil >= 0){
                    cantidad = matCant[fil][1];
                }
                html += "<tr valign='top'>";
                html += "<td><input type='hidden' id='id_material<<X>>"+j+"' name='id_material<<X>>"+j+"' value='"+id_material+"' /></td>";
                        //+ "<input type='checkbox' id='chMat<<X>>"+j+"' name='chMat<<X>>"+j+"' /></td>";
                html += "<td><input type='text' id='cantidad<<X>>"+j+"' name='cantidad<<X>>"+j+"' style='width:25px' maxlength='9' value='"+cantidad+"' /></td>";
                html += "<td>"+(rs.getString("descripcion")!=null ? rs.getString("descripcion") : "")+"</td>";
                html += "</tr>";
                j++;
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        html += "</table>";
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
