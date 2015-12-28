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
import jm.adm.clas.Ubicacion;
import jm.inf.clas.Antena;
import jm.inf.clas.Nodo;
import jm.seg.clas.Rol;
import jm.web.DatosDinamicos;

/**
 *
 * @author Jorge
 */
public class frmNodo extends HttpServlet {
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

        int _anchBody = 740;
        int _altBody = 440;
        
        Rol objRol = new Rol(this._ip, this._puerto, this._db, usuario, clave);

        /*Sucursal obj_Sucursal = new Sucursal(this._ip, this._puerto, this._db, usuario, clave);
        ResultSet rsSucursales = obj_Sucursal.getSucursales();*/
        Nodo objNodo = new Nodo(this._ip, this._puerto, this._db, usuario, clave);

        Ubicacion objUbicacion = new Ubicacion(this._ip, this._puerto, this._db, usuario, clave);

        Antena objAntena = new Antena(this._ip, this._puerto, this._db, usuario, clave);

        try {
            String id = request.getParameter("id");
            //String id_sucursal = String.valueOf(id_sucursal_sesion);

            String cod_cont = "";
            String nodo = "";
            String cod_reg = "";
            String tipo = "";
            String tipo_otra = "";
            String altura_msnm = "";
            String altura_estr = "";
            String id_provincia = "210";
            String id_canton = "";
            String localidad = "";
            String latitud = "";
            String longitud = "";
            boolean puesta_tierra = true;
            boolean pararrayos = true;
            String otros = "";
            boolean linea_comercial = true;
            boolean generador = false;
            boolean banco_baterias = false;
            boolean respaldo = false;
            boolean resp_generador = false;
            boolean resp_baterias = false;
            boolean resp_ups = false;
            String resp_otro = "";
            String propietario = "";

            String ci = "";
            String custodio = "";
            boolean sexo = true;
            String sector = "";
            String direccion = "";
            String telefono = "";
            String movil = "";
            String email = "";
            String banco = "";
            String cuenta = "";

            if(id.compareTo("-1")!=0){
                try{
                    ResultSet rsNodo = objNodo.getNodo(id);
                    if(rsNodo.next()){
                        //id_sucursal = (rsAntena.getString("id_sucursal")!=null) ? rsAntena.getString("id_sucursal") : "";

                        cod_cont = (rsNodo.getString("cod_cont")!=null) ? rsNodo.getString("cod_cont") : "";
                        nodo = (rsNodo.getString("nodo")!=null) ? rsNodo.getString("nodo") : "";
                        cod_reg = (rsNodo.getString("cod_reg")!=null) ? rsNodo.getString("cod_reg") : "";
                        tipo = (rsNodo.getString("tipo")!=null) ? rsNodo.getString("tipo") : "";
                        tipo_otra = (rsNodo.getString("tipo_otra")!=null) ? rsNodo.getString("tipo_otra") : "";
                        altura_msnm = (rsNodo.getString("altura_msnm")!=null) ? rsNodo.getString("altura_msnm") : "";
                        altura_estr = (rsNodo.getString("altura_estr")!=null) ? rsNodo.getString("altura_estr") : "";
                        id_provincia = (rsNodo.getString("id_provincia")!=null) ? rsNodo.getString("id_provincia") : "";
                        id_canton = (rsNodo.getString("id_canton")!=null) ? rsNodo.getString("id_canton") : "";
                        localidad = (rsNodo.getString("localidad")!=null) ? rsNodo.getString("localidad") : "";
                        latitud = (rsNodo.getString("latitud")!=null) ? rsNodo.getString("latitud") : "";
                        longitud = (rsNodo.getString("longitud")!=null) ? rsNodo.getString("longitud") : "";
                        puesta_tierra = (rsNodo.getString("puesta_tierra")!=null) ? rsNodo.getBoolean("puesta_tierra") : true;
                        pararrayos = (rsNodo.getString("pararrayos")!=null) ? rsNodo.getBoolean("pararrayos") : true;
                        otros = (rsNodo.getString("otros")!=null) ? rsNodo.getString("otros") : "";
                        linea_comercial = (rsNodo.getString("linea_comercial")!=null) ? rsNodo.getBoolean("linea_comercial") : true;
                        generador = (rsNodo.getString("generador")!=null) ? rsNodo.getBoolean("generador") : false;
                        banco_baterias = (rsNodo.getString("banco_baterias")!=null) ? rsNodo.getBoolean("banco_baterias") : false;
                        respaldo = (rsNodo.getString("respaldo")!=null) ? rsNodo.getBoolean("respaldo") : false;
                        resp_generador = (rsNodo.getString("resp_generador")!=null) ? rsNodo.getBoolean("resp_generador") : false;
                        resp_baterias = (rsNodo.getString("resp_baterias")!=null) ? rsNodo.getBoolean("resp_baterias") : false;
                        resp_ups = (rsNodo.getString("resp_ups")!=null) ? rsNodo.getBoolean("resp_ups") : false;
                        resp_otro = (rsNodo.getString("resp_otro")!=null) ? rsNodo.getString("resp_otro") : "";
                        propietario = (rsNodo.getString("propietario")!=null) ? rsNodo.getString("propietario") : "";

                        ci = (rsNodo.getString("ci")!=null) ? rsNodo.getString("ci") : "";
                        custodio = (rsNodo.getString("custodio")!=null) ? rsNodo.getString("custodio") : "";
                        sexo = (rsNodo.getString("sexo")!=null) ? rsNodo.getBoolean("sexo") : true;
                        sector = (rsNodo.getString("sector")!=null) ? rsNodo.getString("sector") : "";
                        direccion = (rsNodo.getString("direccion")!=null) ? rsNodo.getString("direccion") : "";
                        telefono = (rsNodo.getString("telefono")!=null) ? rsNodo.getString("telefono") : "";
                        movil = (rsNodo.getString("movil")!=null) ? rsNodo.getString("movil") : "";
                        email = (rsNodo.getString("email")!=null) ? rsNodo.getString("email") : "";
                        banco = (rsNodo.getString("banco")!=null) ? rsNodo.getString("banco") : "";
                        cuenta = (rsNodo.getString("cuenta")!=null) ? rsNodo.getString("cuenta") : "";
                        rsNodo.close();
                    }
                }catch(Exception e){
                    e.printStackTrace();
                }
            }

            String [][] tipo1 = {{"Torre Autosoportada","Torre Autosoportada"},
                                 {"Torre no Autosoportada","Torre no Autosoportada"},
                                 {"Mástil","M&aacute;stil"},
                                 {"Empotramiento en una edificación","Empotramiento en una edificaci&oacute;n"},
                                 {"Otras (Adjuntar breve descripción)","Otras (Adjuntar breve descripci&oacute;n)"}};

            ResultSet rsProvincias = objUbicacion.getUbicaciones("1");
            ResultSet rsCiudades = objUbicacion.getUbicaciones(id_provincia);

            String [][] latitud_orientacion1 = {{"N","N"},{"S","S"}};
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

            String [][] longitud_orientacion1 = {{"E","E"},{"O","O"}};
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

            String html = "obj»vta1_html^foc»nodo^fun»_objTab.seleccionar(0);^frm»";
            html += "<div class='grupoTab' style='width:"+(_anchBody+10)+"px;'><div id='tabR0'>"
                    + "<a id='tab00' href='javascript:void(0);' class='jm_tabAct' onclick='_objTab.seleccionar(0);'>Estructura</a>"
                    + "<a id='tab01' href='javascript:void(0);' class='jm_tab' onclick='_objTab.seleccionar(1);'>Custodio</a>"
                    + "<a id='tab02' href='javascript:void(0);' class='jm_tab' onclick='_objTab.seleccionar(2);'>Dispositivos</a>";
            html += "</div>";
            
            html += "<form action='frmNodoGuardar' onsubmit='return inf_nodoGuardar(this)' autocomplete='off'>";
            html += "<input type='hidden' id='id' name='id' value='"+id+"' />";

            html += "<div id='tabR1'>";

            /* primer tab */

            html += "<div id='tab10' class='jm_tabHTML' style='display:block;width:"+_anchBody+"px;height:"+_altBody+"px;'>";

            html += "<table border='1'>";
            html += "<tr align='center'>"
                        + "<td rowspan='2' colspan='5'><h3>FORMULARIO PARA INFORMACION DE LA INFRAESTRUCTURA DEL SISTEMA DE RADIOCOMUNICACIONES</h3></td>"
                        + "<td width='170'>RC - 2A <br />Elab.: DGGER</td>"
                    + "</tr>"
                    + "<tr>"
                        + "<td>1) Cod. Cont.: <input id='cod_cont' name='cod_cont' type='text' size='8' maxlength='20' value='"+cod_cont+"' onblur=\"this.value=this.value._trim();\" /></td>"
                    + "</tr>"
                    + "<tr>"
                        + "<td colspan='6'>2) ESTRUCTURA: &nbsp;&nbsp; <input id='nodo' name='nodo' class='may' type='text' size='30' maxlength='30' value='"+nodo+"' onblur=\"this.value=this.value._trim();\" /></td>"
                    + "</tr>"
                    + "<tr>"
                        + "<td colspan='3'>TIPO DE ESTRUCTURA DE SOPORTE: "+DatosDinamicos.combo("tipo", tipo, tipo1, "inf_setTipo()")
                            + "<input id='tipo_otra' name='tipo_otra' style='display:none' type='text' size='70' maxlength='160' value='"+tipo_otra+"' onblur=\"this.value=this.value._trim();\" /></td>"
                        + "<td colspan='3'>ALTURA DE LA ESTRUCTURA s.n.m.(m): <input id='altura_msnm' name='altura_msnm' type='text' size='5' maxlength='10' value='"+altura_msnm+"' onkeypress=\"_evaluar(event, '0123456789.')\" /></td>"
                    + "</tr>"
                    + "<tr>"
                        + "<td colspan='3'>CODIGO DE REGISTRO DE LA ESTRUCTURA: <input id='cod_reg' name='cod_reg' type='text' size='8' maxlength='20' value='"+cod_reg+"' onblur=\"this.value=this.value._trim();\" /></td>"
                        + "<td colspan='3'>ALTURA DE LA ESTRUCTURA (BASE-CIMA)(m): <input id='altura_estr' name='altura_estr' type='text' size='5' maxlength='10' value='"+altura_estr+"' onkeypress=\"_evaluar(event, '0123456789.')\" /></td>"
                    + "</tr>"
                    + "<tr>"
                        + "<td colspan='6'>3) UBICACION DE LA ESTRUCTURA:</td>"
                    + "</tr>"
                    + "<tr>"
                        + "<td>PROVINCIA</td>"
                        + "<td>CIUDAD / CANTON</td>"
                        + "<td colspan='2'>LOCALIDAD / CALLE Y No.</td>"
                        + "<td colspan='2'>UBICACION GEOGRAFICA <br /> LATITUD (S/N) LONGITUD (W)</td>"
                    + "</tr>"
                    + "<tr>"
                        + "<td>"+DatosDinamicos.combo(rsProvincias, "id_provincia", id_provincia, "nom_setUbicacion(this.value, 'id_ciudad', 'auxCiudad', '');", "", 150)+"</td>"
                        + "<td id='auxCiudad'>"+DatosDinamicos.combo(rsCiudades, "id_canton", id_canton, "", "", 150)+"</td>"
                        + "<td colspan='2'><input id='localidad' name='localidad' type='text' size='35' maxlength='160' value='"+localidad+"' onblur=\"this.value=this.value._trim();\" /></td>"
                        + "<td colspan='2'>"
                        + "<input id='lat_h' name='lat_h' type='text' style='width:20px' maxlength='2' value='"+latitud_hora+"' onkeypress=\"_numero(event)\" /> º "
                        + "<input id='lat_m' name='lat_m' type='text' style='width:20px' maxlength='2' value='"+latitud_minuto+"' onkeypress=\"_numero(event)\" /> ' "
                        + "<input id='lat_s' name='lat_s' type='text' style='width:40px' maxlength='6' value='"+latitud_segundo+"' onkeypress=\"_evaluar(event, '0123456789.')\" /> '' " +
                        DatosDinamicos.combo("lat_o", latitud_orientacion, latitud_orientacion1, "")
                        + "<input id='lon_h' name='lon_h' type='text' style='width:20px' maxlength='2' value='"+longitud_hora+"' onkeypress=\"_numero(event)\" /> º "
                        + "<input id='lon_m' name='lon_m' type='text' style='width:20px' maxlength='2' value='"+longitud_minuto+"' onkeypress=\"_numero(event)\" /> ' "
                        + "<input id='lon_s' name='lon_s' type='text' style='width:40px' maxlength='6' value='"+longitud_segundo+"' onkeypress=\"_evaluar(event, '0123456789.')\" /> '' " +
                        DatosDinamicos.combo("lon_o", longitud_orientacion, longitud_orientacion1, "")
                    + "</td>"
                    + "</tr>"
                    + "<tr>"
                        + "<td colspan='6'>4) PROTECCIONES ELECTRICAS A INSTALAR EN LA ESTRUCTURA:</td>"
                    + "</tr>"
                    + "<tr>"
                        + "<td colspan='3'>PUESTA A TIERRA: &nbsp;&nbsp;&nbsp; SI<input type='radio' id='puesta_tierra0' name='puesta_tierra' value='true' "+((puesta_tierra) ? "checked" : "")+" /> &nbsp;&nbsp; " +
                          "NO<input type='radio' id='puesta_tierra1' name='puesta_tierra' value='false' "+((!puesta_tierra) ? "checked" : "")+" /></td>"
                        + "<td colspan='3'>PARARRAYOS: &nbsp;&nbsp;&nbsp; SI<input type='radio' id='pararrayos0' name='pararrayos' value='true' "+((pararrayos) ? "checked" : "")+" /> &nbsp;&nbsp; " +
                          "NO<input type='radio' id='pararrayos1' name='pararrayos' value='false' "+((!pararrayos) ? "checked" : "")+" /></td>"
                    + "</tr>"
                    + "<tr valign='top'>"
                        + "<td colspan='6' valign='top'>OTROS (Describa): <textarea id='otros' name='otros' style=\"width:600px;height:30px\" onblur=\"this.value=this.value._trim();\">"+otros+"</textarea></td>"
                    + "</tr>"
                    + "<tr>"
                        + "<td colspan='6'>5) TIPO DE FUENTE DE ENERGIA A UTILIZAR:</td>"
                    + "</tr>"
                    + "<tr>"
                        + "<td>LINEA COMERCIAL &nbsp; <input type='checkbox' id='linea_comercial' name='linea_comercial' "+(linea_comercial ? "checked='checked'" : "")+" /></td>"
                        + "<td>GENERADOR  &nbsp; <input type='checkbox' id='generador' name='generador' "+(generador ? "checked='checked'" : "")+" /></td>"
                        + "<td colspan='2'>BANCO DE BATERIAS  &nbsp; <input type='checkbox' id='banco_baterias' name='banco_baterias' "+(banco_baterias ? "checked='checked'" : "")+" /></td>"
                        + "<td colspan='2'>EXISTE RESPALDO:  SI<input type='radio' id='respaldo0' name='respaldo' value='true' "+((respaldo) ? "checked" : "")+" /> " +
                          "NO<input type='radio' id='respaldo1' name='respaldo' value='false' "+((!respaldo) ? "checked" : "")+" /></td>"
                    + "</tr>"
                    + "<tr>"
                        + "<td colspan='6'>TIPO DE RESPALDO</td>"
                    + "</tr>"
                    + "<tr>"
                        + "<td>GENERADOR  &nbsp; <input type='checkbox' id='resp_generador' name='resp_generador' "+(resp_generador ? "checked='checked'" : "")+" /></td>"
                        + "<td>BANCO DE BATERIAS  &nbsp; <input type='checkbox' id='resp_baterias' name='resp_baterias' "+(resp_baterias ? "checked='checked'" : "")+" /></td>"
                        + "<td colspan='2'>UPS  &nbsp; <input type='checkbox' id='resp_ups' name='resp_ups' "+(resp_ups ? "checked='checked'" : "")+" /></td>"
                        + "<td colspan='2'>OTRO: <input id='resp_otro' name='resp_otro' type='text' size='20' maxlength='60' value='"+resp_otro+"' onblur=\"this.value=this.value._trim();\" /></td>"
                    + "</tr>"
                    + "<tr>"
                        + "<td colspan='6'>6) PROPIETARIO DE LA ESTRUCTURA: <input id='propietario' name='propietario' type='text' size='90' maxlength='160' value='"+propietario+"' onblur=\"this.value=this.value._trim();\" /> </td>"
                    + "</tr>";
            
            html += "</table></div>";



            /*  segundo tab  */



            html += "<div id='tab11' class='jm_tabHTML' style='display:none;width:"+_anchBody+"px;height:"+_altBody+"px;'>";


            html += "<table>";
            html += "<tr><td>C&eacute;dula: </td>";
            html += "<td nowrap><input id='ci' name='ci' type='text' size='25' maxlength='13' class='may' value='"+ci+"' onkeypress=\"_DNI(event)\" /> " +
                    " <input type='checkbox' id='vf' /> verificar</td></tr>";
            html += "<tr><td>Custodio: </td>";
            html += "<td><input id='custodio' name='custodio' class='may' type='text' size='40' maxlength='60' value='"+custodio+"' onblur=\"this.value=this.value._trim();\" /></td></tr>";
            html += "<tr><td>Sexo: </td>";
            html += "<td><input type='radio' id='sexo0' name='sexo' value='true' "+((sexo) ? "checked" : "")+" /> Masculino &nbsp;&nbsp;&nbsp;&nbsp;&nbsp; " +
                    "<input type='radio' id='sexo1' name='sexo' value='false' "+((!sexo) ? "checked" : "")+" /> Femenino</td></tr>";
            html += "<tr><td>Sector:</td>";
            html += "<td><input id='sector' name='sector' type='text' size='40' maxlength='60' value='"+sector+"' onkeypress=\"_alfanumerico(event);\" onblur=\"this.value=this.value._trim();\" /></td></tr>";
            html += "<tr><td>Direcci&oacute;n:</td>";
            html += "<td><input id='direccion' name='direccion' type='text' size='40' maxlength='120' value='"+direccion+"' onblur=\"this.value=this.value._trim();\" /></td></tr>";
            html += "<tr><td>Tel&eacute;fonos:</td>";
            html += "<td><input id='telefono' name='telefono' type='text' size='40' maxlength='50' value='"+telefono+"' onkeypress=\"_evaluar(event, '0123456789-/ ');\" onblur=\"this.value=this.value._trim();\" /></td></tr>";
            html += "<tr><td>Movil:</td>";
            html += "<td><input id='movil' name='movil' type='text' size='40' maxlength='10' value='"+movil+"' onkeypress=\"_evaluar(event, '0123456789-/ ');\" onblur=\"this.value=this.value._trim();\" /></td></tr>";
            html += "<tr><td>E-Mail:</td>";
            html += "<td><input id='email' name='email' type='text' size='40' maxlength='40' value='"+email+"' onblur=\"this.value=this.value._trim();\" /></td></tr>";
            html += "<tr><td>Banco:</td>";
            html += "<td><input id='banco' name='banco' type='text' size='40' maxlength='30' value='"+banco+"' onblur=\"this.value=this.value._trim();\" /></td></tr>";
            html += "<tr><td>Cuenta:</td>";
            html += "<td><input id='banco' name='cuenta' type='text' size='40' maxlength='25' value='"+cuenta+"' onblur=\"this.value=this.value._trim();\" /></td></tr>";
            
            html += "</table>";

            html += "</div>";




            /*  tercer tab  */

            
            ResultSet rsAntenas = objAntena.getAntenas(id);
            

            html += "<div id='tab12' class='jm_tabHTML' style='display:none;width:"+_anchBody+"px;height:"+_altBody+"px;'>";


            html += "<table class='jm_tabla' cellspacing='1' cellpadding='0'><tr><TH class='jm_TH' width='400'>ANTENA ACOPLADA</TH></tr>";
            try{
                while(rsAntenas.next()){
                    html += "<tr class='jm_filaPar'>";
                    html += "<td width='400'>"+((rsAntenas.getString("antena_acoplada")!=null) ? rsAntenas.getString("antena_acoplada") : "")+"</td>";
                    html += "</tr>";
                }
            }catch(Exception e){
                e.printStackTrace();
            }
            html += "</table>";

            html += "</div>";



            if(id.compareTo("-1")==0 || (id.compareTo("-1")!=0 && objRol.getAcceso(id_rol, "nodoEditar"))){
                html += "<input type='submit' value='Guardar' />";
            }

            html += "</div></form></div>";

            
            out.print(html);

        } finally {
            objAntena.cerrar();
            objUbicacion.cerrar();
            objNodo.cerrar();
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
