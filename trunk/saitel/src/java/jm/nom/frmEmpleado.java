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

package jm.nom;

import java.io.*;

import javax.servlet.*;
import javax.servlet.http.*;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import jm.seg.clas.Rol;
import jm.nom.clas.Empleado;
import jm.nom.clas.Horario;
import jm.adm.clas.Comun;
import jm.adm.clas.Ubicacion;
import jm.adm.clas.Sucursal;
import jm.nom.clas.Cargo;
import jm.seg.clas.Usuario;
import jm.web.Archivo;
import jm.web.DatosDinamicos;
import jm.web.Fecha;

/**
 *
 * @author Jorge
 */
public class frmEmpleado extends HttpServlet {
    private String _ip = null;
    private int _puerto = 5432;
    private String _db = null;
    private String _dir = null;

    public void init(ServletConfig config) throws ServletException
    {
        this._ip = config.getServletContext().getInitParameter("_IP");
        this._puerto = Integer.parseInt(config.getServletContext().getInitParameter("_PUERTO"));
        this._db = config.getServletContext().getInitParameter("_DB");
        this._dir = config.getServletContext().getInitParameter("_DIR");
    }
    /**
    * Processes requests for both HTTP <code>GET</code> and <code>POST</code> methods.
    * @param request servlet request
    * @param response servlet response
    */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        HttpSession sesion = request.getSession(true);
        String id_rol_sesion = (String)sesion.getAttribute("id_rol");
        String usuario = (String)sesion.getAttribute("usuario");
        String clave = (String)sesion.getAttribute("clave");

        response.setContentType("text/html;charset=UTF-8");
        response.setHeader("Pragma", "no-cache");
        response.setHeader("Expires", "Mon, 01 Jan 2001 00:00:01 GMT");
        response.setHeader("Cache-Control", "no-store");
        response.setHeader("Cache-Control", "must-revalidate");
        response.setHeader("Cache-Control", "no-cache");
        PrintWriter out = response.getWriter();
        
        Sucursal objSuc = new Sucursal(this._ip, this._puerto, this._db, usuario, clave);
        Empleado objEmpleado = new Empleado(this._ip, this._puerto, this._db, usuario, clave);
        Comun objComun = new Comun(this._ip, this._puerto, this._db, usuario, clave);
        Ubicacion objUbicacion = new Ubicacion(this._ip, this._puerto, this._db, usuario, clave);
        Rol objRol = new Rol(this._ip, this._puerto, this._db, usuario, clave);
        Horario objHorario = new Horario(this._ip, this._puerto, this._db, usuario, clave);
        Usuario objUsuario = new Usuario(this._ip, this._puerto, this._db, usuario, clave);
        Cargo objCargo = new Cargo(this._ip, this._puerto, this._db, usuario, clave);
        
        try {
            String id = request.getParameter("id");
            String alto = request.getParameter("altB");
            int ancho = 550;
            int _altBody = Integer.parseInt(alto);

            String tipo_ident = "";
            String dni = "";
            String cedula_militar = "";
            String nombre = "";
            String apellido = "";
            boolean sexo = true;
            String fecha_nac = "";
            String edad = "";
            String lugar_nacimiento = "";
            String estado_civil = "s";
            String id_provincia = "210";
            String id_ciudad = "21001";
            String id_parroquia = "2100101";
            String sector = "";
            String calle = "";
            String numero = "";
            String licencia_tipo = "";
            String telefono = "";
            String movil = "";
            String movil_movistar = "";
            String email = "";
            String nacionalidad = "ECUATORIANA";
            String carne_conadis = "";
            String discapacidad = "";
            String tipo_sangre = "";
            String enfermedades = "";
            
            String id_sucursal = "";
            String alias = "";
            String ac_no = "";
            boolean generar_rol = true;
            String fecha_ingreso = Fecha.getFecha("SQL");
            String id_rol = "";
            String id_cargo = "";
            String id_departamento = "";
            String id_Horario = "";
            /*String sueldo = "";*/
            String carnet_iess = "";
            String periodo_14_sueldo = "8";
            boolean cobra_f_r = false;
            boolean cobra_14_mensual = false;
            String banco = "";
            String cuenta = "";
            String carnet = "";
            boolean estado = true;

            String observacion = "";
            String carga_familiar = "0";
            String padre_nombre = "";
            String padre_apellido = "";
            String padre_dir_trabajo = "";
            String padre_telefono = "";
            String madre_nombre = "";
            String madre_apellido = "";
            String madre_dir_trabajo = "";
            String madre_telefono = "";            
            String coyg_nombre = "";
            String coyg_apellido = "";
            String coyg_dir_trabajo = "";
            String coyg_telefono = "";

            ResultSet rsSucursales = objSuc.getSucursales();
            
            if(id.compareTo("-1")!=0){
                try{
                    ResultSet rsEmpleado = objEmpleado.getEmpleado(id);
                    if(rsEmpleado.next()){
                        id_sucursal = (rsEmpleado.getString("id_sucursal")!=null) ? rsEmpleado.getString("id_sucursal") : "";
                        id_Horario= (rsEmpleado.getString("id_horario")!=null) ? rsEmpleado.getString("id_horario") : "";
                        alias = (rsEmpleado.getString("alias")!=null) ? rsEmpleado.getString("alias") : "";
                        tipo_ident = (rsEmpleado.getString("tipo_ident")!=null) ? rsEmpleado.getString("tipo_ident") : "";
                        dni = (rsEmpleado.getString("dni")!=null) ? rsEmpleado.getString("dni") : "";
                        cedula_militar = (rsEmpleado.getString("cedula_militar")!=null) ? rsEmpleado.getString("cedula_militar") : "";
                        ac_no = (rsEmpleado.getString("ac_no")!=null) ? rsEmpleado.getString("ac_no") : "";
                        nombre = (rsEmpleado.getString("nombre")!=null) ? rsEmpleado.getString("nombre") : "";
                        apellido = (rsEmpleado.getString("apellido")!=null) ? rsEmpleado.getString("apellido") : "";
                        sexo = (rsEmpleado.getString("sexo")!=null) ? rsEmpleado.getBoolean("sexo") : false;
                        tipo_sangre = (rsEmpleado.getString("tipo_sangre")!=null) ? rsEmpleado.getString("tipo_sangre") : "O+";
                        fecha_nac = (rsEmpleado.getString("fecha_nac")!=null) ? rsEmpleado.getString("fecha_nac") : "";
                        edad = (rsEmpleado.getString("edad")!=null) ? rsEmpleado.getString("edad") : "";
                        lugar_nacimiento = (rsEmpleado.getString("lugar_nacimiento")!=null) ? rsEmpleado.getString("lugar_nacimiento") : "";
                        estado_civil = (rsEmpleado.getString("estado_civil")!=null) ? rsEmpleado.getString("estado_civil") : "";
                        calle = (rsEmpleado.getString("calle")!=null) ? rsEmpleado.getString("calle") : "";
                        numero = (rsEmpleado.getString("numero")!=null) ? rsEmpleado.getString("numero") : "";
                        id_provincia = (rsEmpleado.getString("id_provincia")!=null) ? rsEmpleado.getString("id_provincia") : "210";
                        id_ciudad = (rsEmpleado.getString("id_ciudad")!=null) ? rsEmpleado.getString("id_ciudad") : "21004";
                        id_parroquia = (rsEmpleado.getString("id_parroquia")!=null) ? rsEmpleado.getString("id_parroquia") : "2100401";
                        sector = (rsEmpleado.getString("sector")!=null) ? rsEmpleado.getString("sector") : "";
                        licencia_tipo = (rsEmpleado.getString("licencia_tipo")!=null) ? rsEmpleado.getString("licencia_tipo") : "";
                        telefono = (rsEmpleado.getString("telefono")!=null) ? rsEmpleado.getString("telefono") : "";
                        movil = (rsEmpleado.getString("movil")!=null) ? rsEmpleado.getString("movil") : "";
                        movil_movistar = (rsEmpleado.getString("movil_movistar")!=null) ? rsEmpleado.getString("movil_movistar") : "";
                        email = (rsEmpleado.getString("email")!=null) ? rsEmpleado.getString("email") : "";
                        nacionalidad = (rsEmpleado.getString("nacionalidad")!=null) ? rsEmpleado.getString("nacionalidad") : "";
                        carne_conadis = (rsEmpleado.getString("carne_conadis")!=null) ? rsEmpleado.getString("carne_conadis") : "";
                        discapacidad = (rsEmpleado.getString("discapacidad")!=null) ? rsEmpleado.getString("discapacidad") : "";
                        enfermedades = (rsEmpleado.getString("enfermedades")!=null) ? rsEmpleado.getString("enfermedades") : "";
                        
                        generar_rol = (rsEmpleado.getString("generar_rol")!=null) ? rsEmpleado.getBoolean("generar_rol") : true;
                        /*sueldo = (rsEmpleado.getString("sueldo")!=null) ? rsEmpleado.getString("sueldo") : "";*/
                        carnet_iess = (rsEmpleado.getString("carnet_iess")!=null) ? rsEmpleado.getString("carnet_iess") : "";
                        periodo_14_sueldo = (rsEmpleado.getString("periodo_14_sueldo")!=null) ? rsEmpleado.getString("periodo_14_sueldo") : "8";
                        cobra_f_r = (rsEmpleado.getString("cobra_f_r")!=null) ? rsEmpleado.getBoolean("cobra_f_r") : false;
                        cobra_14_mensual = (rsEmpleado.getString("cobra_14_mensual")!=null) ? rsEmpleado.getBoolean("cobra_14_mensual") : false;
                        
                        banco = (rsEmpleado.getString("banco")!=null) ? rsEmpleado.getString("banco") : "";
                        cuenta = (rsEmpleado.getString("cuenta")!=null) ? rsEmpleado.getString("cuenta") : "";
                        estado = (rsEmpleado.getString("estado")!=null) ? rsEmpleado.getBoolean("estado") : true;
                        fecha_ingreso = (rsEmpleado.getString("fecha_ingreso")!=null) ? rsEmpleado.getString("fecha_ingreso") : "";
                        id_rol = (rsEmpleado.getString("id_rol")!=null) ? rsEmpleado.getString("id_rol") : "";
                        id_cargo = (rsEmpleado.getString("id_cargo")!=null) ? rsEmpleado.getString("id_cargo") : "";
                        id_departamento = (rsEmpleado.getString("departamento")!=null) ? rsEmpleado.getString("departamento") : "";
                        carnet = (rsEmpleado.getString("carnet")!=null) ? rsEmpleado.getString("carnet") : "";
                        
                        
                        /*observacion = (rsEmpleado.getString("observacion")!=null) ? rsEmpleado.getString("observacion") : "";
                        carga_familiar = (rsEmpleado.getString("carga_familiar")!=null) ? rsEmpleado.getString("carga_familiar") : "";
                        padre_nombre = (rsEmpleado.getString("padre_nombre")!=null) ? rsEmpleado.getString("padre_nombre") : "";
                        padre_apellido = (rsEmpleado.getString("padre_apellido")!=null) ? rsEmpleado.getString("padre_apellido") : "";
                        padre_dir_trabajo = (rsEmpleado.getString("padre_dir_trabajo")!=null) ? rsEmpleado.getString("padre_dir_trabajo") : "";
                        padre_telefono = (rsEmpleado.getString("padre_telefono")!=null) ? rsEmpleado.getString("padre_telefono") : "";
                        
                        madre_nombre = (rsEmpleado.getString("madre_nombre")!=null) ? rsEmpleado.getString("madre_nombre") : "";
                        madre_apellido = (rsEmpleado.getString("madre_apellido")!=null) ? rsEmpleado.getString("madre_apellido") : "";
                        madre_dir_trabajo = (rsEmpleado.getString("madre_dir_trabajo")!=null) ? rsEmpleado.getString("madre_dir_trabajo") : "";
                        madre_telefono = (rsEmpleado.getString("madre_telefono")!=null) ? rsEmpleado.getString("madre_telefono") : "";
                        
                        coyg_nombre = (rsEmpleado.getString("coyg_nombre")!=null) ? rsEmpleado.getString("coyg_nombre") : "";
                        coyg_apellido = (rsEmpleado.getString("coyg_apellido")!=null) ? rsEmpleado.getString("coyg_apellido") : "";
                        coyg_dir_trabajo = (rsEmpleado.getString("coyg_dir_trabajo")!=null) ? rsEmpleado.getString("coyg_dir_trabajo") : "";
                        coyg_telefono = (rsEmpleado.getString("coyg_telefono")!=null) ? rsEmpleado.getString("coyg_telefono") : "";*/
                        
                        rsEmpleado.close();
                    }
                }catch(Exception e){
                    e.printStackTrace();
                }
            }


            String [][] tipo_ident1 = {{"2","C&eacute;dula"},{"3","Pasaporte"}};
            String [][] estado_civil1 = {{"s","Soltero(a)"},{"c","Casado(a)"},{"d","Divorciado(a)"},{"v","Viudo(a)"},{"u","Uni&oacute;n Libre"}};
            String [][] pago_14_sueldo1 = {{"3","Marzo (Costa)"},{"8","Agosto (Sierra)"}};
            String [][] tipo_sangre1 = {{"",""},{"A+","A+"},{"B+","B+"},{"AB+","AB+"},{"O+","O+"},
                                        {"A-","A-"},{"B-","B-"},{"AB-","AB-"},{"O-","O-"}};

            ResultSet rsDepartamentos = objComun.getArea("false");

            ResultSet rsProvincias = objUbicacion.getUbicaciones("1");
            ResultSet rsCiudades = objUbicacion.getUbicaciones(id_provincia);
            ResultSet rsParroquias = objUbicacion.getUbicaciones(id_ciudad);

            ResultSet rsRoles = objRol.getRoles();
            ResultSet rsParentesco = objRol.getParentesco();
            ResultSet rsCargos = objCargo.getCargos();

            ResultSet rsUsuarios = objUsuario.getUsuarios();
            ResultSet rsHorario = objHorario.getHorario("true");

            String html = "obj»d_21^foc»c^fun»_objTab.seleccionar(0);^frm»";
            html += "<div class='H3' style='width:"+ancho+"px'>Formulario de Empleado</div>";

            html += "<div class='grupoTab' style='width:"+(ancho)+"px;'><div id='tabR0'>"
                    + "<a id='tab00' href='javascript:void(0);' class='jm_tabAct' onclick='_objTab.seleccionar(0);'>Datos Personales</a>"
                    + "<a id='tab02' href='javascript:void(0);' class='jm_tab' onclick='_objTab.seleccionar(2);'>Rol de Pago</a>"
                    + "<a id='tab01' href='javascript:void(0);' class='jm_tab' onclick='_objTab.seleccionar(1);'>Familiares</a>"
                    + "<a id='tab03' href='javascript:void(0);' class='jm_tab' onclick='_objTab.seleccionar(3);'>Formaci&oacute;n</a>"
                    + "<a id='tab04' href='javascript:void(0);' class='jm_tab' onclick='_objTab.seleccionar(4);'>Carnet de Ident.</a></div>";

            html += "<form action='frmEmpleadoGuardar' onsubmit='return nom_empleadoGuardar(this)' autocomplete='off'>";
            html += "<input type='hidden' id='id' name='id' value='"+id+"' />";

            /* PRIMER TAB */
            html += "<div id='tabR1'>";
            /* PRIMER TAB */
            html += "<div id='tab10' class='jm_tabHTML' style='display:block;width:"+(ancho-30)+"px;height:"+_altBody+"px;'>";
            html += "<table>";
            html += "<tr><td nowrap>Tipo de Identificaci&oacute;n: <span class='marca'>*</span> </td>";
            html += "<td>" + DatosDinamicos.combo("tipo_ident", tipo_ident, tipo_ident1, "") + "</td></tr>";
            html += "<tr><td>DNI: <span class='marca'>*</span> </td>";
            html += "<td nowrap><input id='dni' name='dni' type='text' size='20' maxlength='13' value='"+dni+"' class='may' onkeypress=\"_DNI(event)\" /> "
                    + "<input type='checkbox' id='vf' checked='checked' /> verificar</td></tr>";
            html += "<tr><td>Cedula militar:</td>";
            html += "<td nowrap><input id='cedula_militar' name='cedula_militar' type='text' size='20' maxlength='13' value='"+cedula_militar+"' onkeypress=\"_numero(event);\" /></td></tr>";
            
            html += "<tr><td>Nombres: <span class='marca'>*</span> </td>";
            html += "<td nowrap><input id='nombre' name='nombre' class='may' type='text' size='35' maxlength='40' value='"+nombre+"' onkeypress=\"_alfanumerico(event);\" onkeyup='nom_usuario()' onblur=\"this.value=this.value._trim();\" /></td></tr>";
            html += "<tr><td>Apellidos: <span class='marca'>*</span> </td>";
            html += "<td nowrap><input id='apellido' name='apellido' class='may' type='text' size='35' maxlength='40' value='"+apellido+"' onkeypress=\"_alfanumerico(event);\" onkeyup='nom_usuario()' onblur=\"this.value=this.value._trim();\" /></td></tr>";
            html += "<tr><td nowrap>Usuario del sistema asociado: </td>";
            /*usuario automatico*/
            html += "<td><input id='alias' name='alias' type='text' size='30' maxlength='40' value='"+alias+"' disabled /></td></tr>";
            html += "<tr><td>Sexo: <span class='marca'>*</span></td>";
            html += "<td><input type='radio' id='sexo0' name='sexo' value='true' "+((sexo) ? "checked" : "")+" /> Masculino &nbsp;&nbsp;&nbsp;&nbsp;&nbsp; " +
                    "<input type='radio' id='sexo1' name='sexo' value='false' "+((!sexo) ? "checked" : "")+" /> Femenino</td></tr>";
            
            html += "<tr><td>Fecha de nacimiento: </td>";
            html += "<td nowrap><input id='fecha_nac' name='fecha_nac' type='text' size='10' value='"+fecha_nac+"' onkeypress=\"_SUP(event);\" /> "
                    + "<input type='button' value='...' onClick=\"SelectorFecha.crear('fecha_nac', 'SQL');\" /></td></tr>";
            html += "<tr><td nowrap>Edad: </td>";
            html += "<td>"+edad+" a&ntilde;os</td></tr>";
            html += "<tr><td nowrap>Lugar de nacimiento: </td>";
            html += "<td><input id='lugar_nacimiento' name='lugar_nacimiento' type='text' class='may' size='35' maxlength='40' value='"+lugar_nacimiento+"' onkeypress=\"_alfanumerico(event);\" /></td></tr>";
            
            html += "<tr><td nowrap>Estado civil: <span class='marca'>*</span> </td>";
            html += "<td>" + DatosDinamicos.combo("estado_civil", estado_civil, estado_civil1, "") + "</td></tr>";
            html += "<tr><td nowrap>Provincia: <span class='marca'>*</span> </td>";
            html += "<td>"+DatosDinamicos.combo(rsProvincias, "id_provincia", id_provincia, "nom_setUbicacion(this.value, 'id_ciudad', 'auxCiudad', '1')", "", 150)+"</td></tr>";
            html += "<tr><td>Ciudad: <span class='marca'>*</span></td>";
            html += "<td id='auxCiudad'>"+DatosDinamicos.combo(rsCiudades, "id_ciudad", id_ciudad, "nom_setUbicacion(this.value, 'id_parroquia', 'auxParr', '')", "", 150)+"</td></tr>";
            html += "<tr><td>Parroquia: <span class='marca'>*</span></td>";
            html += "<td id='auxParr'>"+DatosDinamicos.combo(rsParroquias, "id_parroquia", id_parroquia, "", "", 150)+"</td></tr>";
            html += "<tr><td nowrap>Sector: </td>";
            html += "<td><input id='sector' name='sector' type='text' class='may' size='35' value='"+sector+"' onkeypress=\"_alfanumerico(event);\" /></td></tr>";
            html += "<tr><td nowrap>Calle: <span class='marca'>*</span> </td>";
            html += "<td><input id='calle' name='calle' type='text' class='may' size='35' value='"+calle+"' onkeypress=\"_alfanumerico(event);\" onblur=\"this.value=this.value._trim();\" /></td></tr>";
            html += "<tr><td nowrap>Numero: <span class='marca'>*</span> </td>";
            html += "<td><input id='numero' name='numero' type='text' size='35' maxlength='10' value='"+numero+"' onkeypress=\"_evaluar(event, '0123456789-');\" /></td></tr>";
            
            html += "<tr><td nowrap>Licencia tipo: </td>";
            html += "<td><input id='licencia_tipo' name='licencia_tipo' type='text' class='may' size='10' maxlength='2' value='"+licencia_tipo+"' /></td></tr>";
            
            html += "<tr><td nowrap>Tel&eacute;fono: </td>";
            html += "<td><input id='telefono' name='telefono' type='text' size='35' maxlength='10' value='"+telefono+"' onkeypress=\"_numero(event);\" /></td></tr>";
            html += "<tr><td nowrap>M&oacute;vil claro: </td>";
            html += "<td><input id='movil' name='movil' type='text' size='35' maxlength='10' value='"+movil+"' onkeypress=\"_numero(event);\" /></td></tr>";
            html += "<tr><td nowrap>M&oacute;vil movistar: </td>";
            html += "<td><input id='movil_movistar' name='movil_movistar' type='text' size='35' maxlength='10' value='"+movil_movistar+"' onkeypress=\"_numero(event);\" /></td></tr>";
            
            html += "<tr><td nowrap>E-Mail: </td>";
            html += "<td><input id='email' name='email' type='text' size='35' maxlength='40' value='"+email+"' /></td></tr>";
            html += "<tr><td nowrap>Nacionalidad: </td>";
            html += "<td><input id='nacionalidad' name='nacionalidad' class='may' type='text' size='35' maxlength='9' value='"+nacionalidad+"' onkeypress=\"_letra(event);\" onblur=\"this.value=this.value._trim();\" /></td></tr>";
            
            html += "<tr><td nowrap>Carn&eacute; CONADIS: </td>";
            html += "<td><input id='carne_conadis' name='carne_conadis' type='text' size='35' maxlength='13' value='"+carne_conadis+"' /></td></tr>";
            html += "<tr><td nowrap>Discapacidad: </td>";
            html += "<td><input id='discapacidad' name='discapacidad' type='text' size='35' maxlength='100' value='"+discapacidad+"' /></td></tr>";
            
            html += "<tr><td nowrap>Tipo de sangre: <span class='marca'>*</span> </td>";
            html += "<td>" + DatosDinamicos.combo("tipo_sangre", tipo_sangre, tipo_sangre1, "") + "</td></tr>";
            html += "<tr><td nowrap>Enfermedades: </td>";
            html += "<td><textarea id='enfermedades' name='enfermedades' >"+enfermedades+"</textarea></td></tr>";

            html += "</table></div>";


            /* SEGUNDO TAB */
            html += "<div id='tab12' class='jm_tabHTML' style='display:none;width:"+(ancho-30)+"px;height:"+_altBody+"px;'>";
            html += "<table><tr><td nowrap>Sucursal: <span class='marca'>*</span> </td>";
            html += "<td>"+DatosDinamicos.combo(rsSucursales, "id_sucursal", id_sucursal, "nom_setHorario(this.value, 'id_Horario', 'auxHorario', '')", "", 150)+"</td></tr>";
            /*html += "<td>" + DatosDinamicos.combo(rsSucursales, "id_sucursal", id_sucursal, "", "") + "</td></tr>";*/
            
            /*html += "<td>" + DatosDinamicos.combo(rsUsuarios, "alias", alias, "", " SIN USUARIO ") + "</td></tr>";*/
            html += "<tr><td>Ac No. del biom&eacute;trico: <span class='marca'>*</span> </td>";
            html += "<td nowrap><input id='ac_no' name='ac_no' type='text' size='30' maxlength='13' value='"+ac_no+"' /></td></tr>";
            
            html += "<tr><td>Generar rol de pago: <span class='marca'>*</span></td>";
            html += "<td><label><input type='radio' id='generar_rol0' name='generar_rol' value='true' "+((generar_rol) ? "checked" : "")+" />Si</label> &nbsp;&nbsp; " +
                    "<label><input type='radio' id='generar_rol1' name='generar_rol' value='false' "+((!generar_rol) ? "checked" : "")+" />No</label></td></tr>";
            html += "<tr><td>Fecha de ingreso: <span class='marca'>*</span> </td>";
            html += "<td nowrap><input id='fecha_ing' name='fecha_ing' type='text' size='10' value='"+fecha_ingreso+"' readonly /> "
                    + "<input type='button' value='...' onClick=\"SelectorFecha.crear('fecha_ing', 'SQL');\" /></td></tr>";
            html += "<tr><td nowrap>Rol de acuerdo a funciones en el sistema: <span class='marca'>*</span> </td>";
            html += "<td>"+DatosDinamicos.combo(rsRoles, "id_rol", id_rol, "", "", 150)+"</td></tr>";
            html += "<tr><td nowrap>Cargo: <span class='marca'>*</span><input id='nom_cargo' name='nom_cargo' type='hidden' value='"+id_cargo+"' /> </td>";
            html += "<td>"+DatosDinamicos.combo(rsCargos, "id_cargo", id_cargo, "", "", 150)+"</td></tr>";
            html += "<tr><td>Area: <span class='marca'>*</span> </td>";
            html += "<td><input id='id_depar' name='area' type='text' size='30' maxlength='2' value='"+id_departamento+"' disabled /></td></tr>";
            html += "<tr><td>Horario: <span class='marca'>*</span></td>";
            html += "<td id='auxHorario'>"+DatosDinamicos.combo(rsHorario, "id_Horario", id_Horario, "", "", 150)+"</td></tr>";
            /*html += "<tr><td>Horario: <span class='marca'>*</span> </td>";
            html += "<td>"+DatosDinamicos.combo(rsHorario, "id_horario", id_rol, "", "", 150)+"</td></tr>";
            html += "<td><div id='cob0' style='float:left;display:table-cell'>"+DatosDinamicos.combo(rsDepartamentos, "id_departamento", id_departamento, "", "", 150)+"</div></td></tr>";
                    + "<div class='jm_icoCon' id='gr0' style='visibility:hidden;background-position: -545px 0px;' onclick=\"guardarComun(null,'cob0','id_departamento','gr0','add0',150,'DEP');\" title='guardar'>&nbsp;</div>"
                    + "<div class='jm_icoCon' id='add0' style='background-position:-504px -14px;' onclick=\"insertarComun('cob0','id_departamento','gr0','add0',150,'DEP');\" title='Insertar'>&nbsp;</div></td></tr>";
            html += "<tr><td nowrap>Carga familiar: </td>";
            html += "<td><input id='carga_familiar' name='carga_familiar' type='text' size='30' maxlength='2' value='"+carga_familiar+"' onkeypress=\"_numero(event);\" /></td></tr>";
            
            html += "<tr><td nowrap>Sueldo b&aacute;sico unificado: <span class='marca'>*</span> </td>";
            html += "<td><input id='sueldo' name='sueldo' type='text' size='30' maxlength='10' value='"+sueldo+"' onkeypress=\"_evaluar(event,'0123456789.');\" /></td></tr>";*/
            html += "<tr><td nowrap>N&uacute;mero de carn&eacute; del IESS: </td>";
            html += "<td><input id='carnet_iess' name='carnet_iess' type='text' size='30' maxlength='10' value='"+carnet_iess+"' onkeypress=\"_evaluar(event,'0123456789-');\" /></td></tr>";
            html += "<tr><td>Pago del d&eacute;cimo cuarto sueldo: <span class='marca'>*</span> </td>";
            html += "<td>" + DatosDinamicos.combo("periodo_14_sueldo", periodo_14_sueldo, pago_14_sueldo1, "") + "</td></tr>";
            html += "<tr><td>Cobra fondos de reserva en el rol de pagos: <span class='marca'>*</span></td>";
            html += "<td><label><input type='radio' id='cobra_f_r0' name='cobra_f_r' value='true' "+((cobra_f_r) ? "checked" : "")+" />Si</label> &nbsp;&nbsp; " +
                    "<label><input type='radio' id='cobra_f_r1' name='cobra_f_r' value='false' "+((!cobra_f_r) ? "checked" : "")+" />No</label></td></tr>";
            html += "<tr><td>Cobra 14 sueldo mensualmente: <span class='marca'>*</span></td>";
            html += "<td><label><input type='radio' id='cobra_14_mensual0' name='cobra_14_mensual' value='true' "+((cobra_14_mensual) ? "checked" : "")+" />Si</label> &nbsp;&nbsp; " +
                    "<label><input type='radio' id='cobra_14_mensual1' name='cobra_14_mensual' value='false' "+((!cobra_14_mensual) ? "checked" : "")+" />No</label></td></tr>";
            
            html += "<tr><td nowrap>Banco: </td>";
            html += "<td><input id='banco' name='banco' type='text' size='30' maxlength='30' value='"+banco+"' onkeypress=\"_alfanumerico(event);\" onblur=\"this.value=this.value._trim();\" /></td></tr>";
            html += "<tr><td nowrap>Cuenta: </td>";
            html += "<td><input id='cuenta' name='cuenta' type='text' size='30' maxlength='25' value='"+cuenta+"' onkeypress=\"_evaluar(event,'0123456789.-');\" /></td></tr>";
            /*html += "<tr valign='top'><td>Observaci&oacute;n:</td>";
            html += "<td><textarea id='observacion' name='observacion' rows='2' cols='25' onblur=\"this.value=this.value._trim();\" >"+observacion+"</textarea></td></tr>";*/
            html += "<tr><td>Estado: <span class='marca'>*</span></td>";
            html += "<td><input type='radio' id='estado0' name='estado' value='true' "+((estado) ? "checked" : "")+" /> Activo &nbsp;&nbsp;&nbsp;&nbsp;&nbsp; " +
                    "<input type='radio' id='estado1' name='estado' value='false' "+((!estado) ? "checked" : "")+" /> Inactivo</td></tr>";
            html += "</table></div>";
            
            
            
            /* TERCER TAB */
            
            html += "<div id='tab11' class='jm_tabHTML' style='display:none;width:"+(ancho-30)+"px;height:"+_altBody+"px;'>";
            
                html += "<fieldset style='background-color:#CCEBFF'>";
                html += "<legend style='background-color:#CCEBFF'>DATOS DE LA FAMILIA</legend>";
                
                html += "<table width='100%'>";
                html += "<tr><td nowrap>Nombres: </td>";
                html += "<td><input id='padre_nombre' class='may' name='padre_nombre' type='text' size='35' maxlength='40'  onkeypress=\"_alfanumerico(event);\" /></td></tr>";
                /*html += "<tr><td nowrap>Apellidos: </td>";
                html += "<td><input id='padre_apellido' name='padre_apellido' type='text' size='35' maxlength='40' value='"+padre_apellido+"' onkeypress=\"_alfanumerico(event);\" /></td></tr>";*/
                html += "<tr><td nowrap>Direcci&oacute;n de trabajo: </td>";
                html += "<td><input id='padre_dir_trabajo' name='padre_dir_trabajo' class='may' type='text' size='35' maxlength='100' onkeypress=\"_alfanumerico(event);\" /></td></tr>";
                html += "<tr><td nowrap>Tel&eacute;fono: </td>";
                html += "<td><input id='padre_telefono' name='padre_telefono' type='text' size='35' maxlength='30' onkeypress=\"_evaluar(event, '0123456789 /');\" /></td></tr>";
                html += "<tr><td nowrap>Fecha de nacimiento: </td>";
                html += "<td nowrap><input id='fecha_nac_familia' name='fecha_nac_familia' type='text' size='10' onkeypress=\"_SUP(event);\" /> "
                    + "<input type='button' value='...' onClick=\"SelectorFecha.crear('fecha_nac_familia', 'SQL');\" /></td></tr>";
                
                html += "<tr><td nowrap>Parentesco: <span class='marca'>*</span> </td>";
                html += "<td>"+DatosDinamicos.combo(rsParentesco, "id_parentesco", "", "", "", 100)+"</td></tr>";
                html += "<tr><td nowrap>Carn&eacute; CONADIS: </td>";
                html += "<td><input id='carne_conadis_familia' name='carne_conadis_familia' type='text' size='35' maxlength='13' /></td></tr>";
                html += "<tr><td nowrap>Discapacidad: </td>";
                html += "<td><input id='discapacidad_familia' name='discapacidad_familia' type='text' size='35' maxlength='100' /></td></tr>";
                html += "<tr><td>Vive?: <span class='marca'>*</span></td>";
                html += "<td><label><input type='radio' id='vive0' name='vive' value='true' checked />Si</label> &nbsp;&nbsp; "
                     +   "<label><input type='radio' id='vive1' name='vive' value='false' />No</label>"
                     +  "<input type='button' value='Nuevo' style='float:right' onclick='nom_nuevofamilia()'/><input type='submit' value='Guardar' style='float:right'/></td></tr>";
            
                html += "</table>";
                html += "</fieldset>";
                
                html += "<fieldset>";
                html += "<legend>DATOS</legend>";
                html += "<div id='axTblPE'><TABLE cellspacing='0' cellpadding='0'><TR>" +
                            "<TH class='jm_TH' width='100'>Nombres</TH>" +
                            "<TH class='jm_TH' width='100'>Direccion</TH>" +
                            "<TH class='jm_TH' width='100'>Telefono</TH>" +
                            "<TH class='jm_TH' width='100'>F. de Nacimiento</TH>" +
                            /*"<TH class='jm_TH' width='100'>Carnet Conadis</TH>" +
                            "<TH class='jm_TH' width='100'>Discapacidad</TH>" +*/
                            "<TH class='jm_TH' width='100'>Parentesco</TH>" +
                            "<TH class='jm_TH' width='20'>&nbsp;</TH></TR></TABLE>";
                html += "<DIV style='overflow:auto;width:500px;height:150px;' id='sll'>" +
                "<TABLE class='jm_tabla' cellspacing='1' cellpadding='0' id='tblPE'>";
                
                int i=0;
                String f_id = "";
                String f_nombres = "";
                String f_direccion = "";
                String f_telefono = "";
                String f_nacimiento= "";
                String f_carnet = "";
                String f_discapacidad = "";
                String f_vive = "";
                String f_parentesco = "";
                String idd_parentesco = "";
                ResultSet rsEmpleadoFamiliar = objEmpleado.getFamiliar(id);
                try {
                    while(rsEmpleadoFamiliar.next()){
                        f_id = (rsEmpleadoFamiliar.getString("id_familia")!=null) ? rsEmpleadoFamiliar.getString("id_familia") : "";
                        f_nombres = (rsEmpleadoFamiliar.getString("nombre")!=null) ? rsEmpleadoFamiliar.getString("nombre") : "";
                        f_direccion = (rsEmpleadoFamiliar.getString("direccion")!=null) ? rsEmpleadoFamiliar.getString("direccion") : "";
                        f_telefono = (rsEmpleadoFamiliar.getString("telefono")!=null) ? rsEmpleadoFamiliar.getString("telefono") : "";
                        f_nacimiento = (rsEmpleadoFamiliar.getString("fecha_nacimiento")!=null) ? rsEmpleadoFamiliar.getString("fecha_nacimiento") : "";
                        f_carnet = (rsEmpleadoFamiliar.getString("carnet_conadis")!=null) ? rsEmpleadoFamiliar.getString("carnet_conadis") : "";
                        f_discapacidad = (rsEmpleadoFamiliar.getString("discapacidad")!=null) ? rsEmpleadoFamiliar.getString("discapacidad") : "";
                        f_vive = (rsEmpleadoFamiliar.getString("vive")!=null) ? rsEmpleadoFamiliar.getString("vive") : "";
                        f_parentesco = (rsEmpleadoFamiliar.getString("parentesco")!=null) ? rsEmpleadoFamiliar.getString("parentesco") : "";
                        idd_parentesco = (rsEmpleadoFamiliar.getString("id_parentesco")!=null) ? rsEmpleadoFamiliar.getString("id_parentesco") : "";
                        html += "<tr id='rTI"+f_id+"' valign='top' class='jm_filaPar' style='cursor:pointer' onmouseover=\"this.className='jm_filaSobre'\" onmouseout=\"this.className='jm_filaPar'\">";
                        html += "<td width='101' style='cursor:pointer' onclick=\"nom_setfamilia("+f_id+");\"><input id='nom_fami"+f_id+"' name='nom_fami"+f_id+"' type='hidden' value='"+f_nombres+"' />"+f_nombres+"</td>";
                        html += "<td width='101' style='cursor:pointer' onclick=\"nom_setfamilia("+f_id+");\"><input id='nom_dir"+f_id+"' name='nom_dir"+f_id+"' type='hidden' value='"+f_direccion+"' />"+f_direccion+"</td>";
                        html += "<td width='101' style='cursor:pointer' onclick=\"nom_setfamilia("+f_id+");\"><input id='nom_telf"+f_id+"' name='nom_telf"+f_id+"' type='hidden' value='"+f_telefono+"' />"+f_telefono+"</td>";
                        html += "<td width='101' style='cursor:pointer' onclick=\"nom_setfamilia("+f_id+");\"><input id='nom_nac"+f_id+"' name='nom_nac"+f_id+"' type='hidden' value='"+f_nacimiento+"' />"+f_nacimiento+"</td>";
                        html += "<input id='nom_carnet"+f_id+"' name='nom_carnet"+f_id+"' type='hidden' value='"+f_carnet+"' />";
                        html += "<input id='nom_disca"+f_id+"' name='nom_disca"+f_id+"' type='hidden' value='"+f_discapacidad+"' />";
                        html += "<input id='nom_vive"+f_id+"' name='nom_vive"+f_id+"' type='hidden' value='"+f_vive+"' />";
                        html += "<td width='101' style='cursor:pointer' onclick=\"nom_setfamilia("+f_id+");\"><input id='nom_pare"+f_id+"' name='nom_pare"+f_id+"' type='hidden' />"+f_parentesco+"</td>";
                        html += "<td width='21' align='center'><div class='jm_icoCon' style='background-position:-545px -18px;' title='Eliminar' onclick=\"nom_empleadoFamiliaEliminar("+f_id+");\">&nbsp;</div></td>";
                        /*html += "<td align='center' width='100'><a href='formatoFactura?id="+id_punto_emision+"' target='_blank'>modificar formato</a></td>";
                        html += "<td align='center' width='20'><input type='hidden' id='idR"+i+"' value='"+id_punto_emision+"' />&nbsp;</td></tr>";*/
                        i++;
                    }
                } catch (SQLException ex) {
                    Logger.getLogger(frmEmpleado.class.getName()).log(Level.SEVERE, null, ex);
                }
                
                html += "<input id='est' name='est' type='hidden' value='-1' />";
                html += "<input id='id_fa' name='id_fa' type='hidden' />";
                html += "</table></div>";
                
                html += "</fieldset>";
                
                /*html += "<fieldset>";
                html += "<legend>DATOS DE LA MADRE</legend>";
                html += "<table>";
                html += "<tr><td nowrap>Nombres: </td>";
                html += "<td><input id='madre_nombre' name='madre_nombre' type='text' size='35' maxlength='40' value='"+madre_nombre+"' onkeypress=\"_alfanumerico(event);\" /></td></tr>";
                html += "<tr><td nowrap>Apellidos: </td>";
                html += "<td><input id='madre_apellido' name='madre_apellido' type='text' size='35' maxlength='40' value='"+madre_apellido+"' onkeypress=\"_alfanumerico(event);\" /></td></tr>";
                html += "<tr><td nowrap>Direcci&oacute;n de trabajo: </td>";
                html += "<td><input id='madre_dir_trabajo' name='madre_dir_trabajo' type='text' size='35' maxlength='100' value='"+madre_dir_trabajo+"' onkeypress=\"_alfanumerico(event);\" /></td></tr>";
                html += "<tr><td nowrap>Tel&eacute;fono: </td>";
                html += "<td><input id='madre_telefono' name='madre_telefono' type='text' size='35' maxlength='30' value='"+madre_telefono+"' onkeypress=\"_evaluar(event, '0123456789 /');\" /></td></tr>";
                html += "</table>";
                html += "</fieldset>";
                
                html += "<fieldset style='background-color:#CCEBFF'>";
                html += "<legend style='background-color:#CCEBFF'>DATOS DEL/LA CONYUGE</legend>";
                html += "<table>";
                html += "<tr><td nowrap>Nombres: </td>";
                html += "<td><input id='coyg_nombre' name='coyg_nombre' type='text' size='35' maxlength='40' value='"+coyg_nombre+"' onkeypress=\"_alfanumerico(event);\" /></td></tr>";
                html += "<tr><td nowrap>Apellidos: </td>";
                html += "<td><input id='coyg_apellido' name='coyg_apellido' type='text' size='35' maxlength='40' value='"+coyg_apellido+"' onkeypress=\"_alfanumerico(event);\" /></td></tr>";
                html += "<tr><td nowrap>Direcci&oacute;n de trabajo: </td>";
                html += "<td><input id='coyg_dir_trabajo' name='coyg_dir_trabajo' type='text' size='35' maxlength='100' value='"+coyg_dir_trabajo+"' onkeypress=\"_alfanumerico(event);\" /></td></tr>";
                html += "<tr><td nowrap>Tel&eacute;fono: </td>";
                html += "<td><input id='coyg_telefono' name='coyg_telefono' type='text' size='35' maxlength='30' value='"+coyg_telefono+"' onkeypress=\"_evaluar(event, '0123456789 /');\" /></td></tr>";
                html += "</table>";
                html += "</fieldset>";*/
            
            html += "</div>";
            

            /* CUARTO TAB */
            html += "<div id='tab13' class='jm_tabHTML' style='display:none;width:"+(ancho-30)+"px;height:"+_altBody+"px;'>";
             html += "<fieldset style='background-color:#CCEBFF'>";
                html += "<legend style='background-color:#CCEBFF'>FORMACION ACADEMICA</legend>";
                
                html += "<table width='100%'>";
                html += "<tr><td nowrap>Nombre de la Institución: </td>";
                html += "<td><input id='nombre_institucion' class='may' name='nombre_institucion' type='text' size='35' maxlength='40'  onkeypress=\"_alfanumerico(event);\" /></td></tr>";
                html += "<tr><td nowrap>Instruccion:</td>";
                html += "<td><select id='instruccion' name='instruccion'>"
                        + "<option value='Primaria'>Primaria</option>"
                        + "<option value='Secundaria'>Secundaria</option>"
                        + "<option value='Nivel Tecnico o Tecnologico Superior'>Nivel Tecnico o Tecnologico Superior</option>"
                        + "<option value='Tercer Nivel'>Tercer Nivel</option>"
                        + "<option value='Cuarto Nivel'>Cuarto Nivel</option>"
                        + "</select></td></tr>";
                html += "<tr><td nowrap>Título Obtenido : </td>";
                html += "<td><input id='titulo_ob' name='titulo_ob' class='may' type='text' size='35' maxlength='100' onkeypress=\"_alfanumerico(event);\" /></td></tr>";
                html += "<tr><td nowrap>Fecha: </td>";
                html += "<td><input id='fecha_for' name='fecha_for' type='text' size='10' onkeypress=\"_SUP(event);\" />"
                    + "<input type='button' value='...' onClick=\"SelectorFecha.crear('fecha_for', 'SQL');\" />"
                        + "<input type='button' value='Nuevo' style='float:right' onclick='nom_nuevoFormacion()'/><input type='submit' value='Guardar' style='float:right'/></td></tr>";
                html += "</table>";
                html += "</fieldset>";
                
                html += "<fieldset>";
                html += "<legend>DATOS</legend>";
                html += "<div id='axTblPE'><TABLE cellspacing='0' cellpadding='0'><TR>" +
                            "<TH class='jm_TH' width='160'>Nombre de la Institucion</TH>" +
                            "<TH class='jm_TH' width='120'>Instruccion</TH>" +
                            "<TH class='jm_TH' width='120'>Titulo Obtenido</TH>" +
                            "<TH class='jm_TH' width='100'>Fecha</TH>" +
                            "<TH class='jm_TH' width='20'>&nbsp;</TH></TR></TABLE>";
                html += "<DIV style='overflow:auto;width:500px;height:150px;' id='sll'>" +
                "<TABLE class='jm_tabla' cellspacing='1' cellpadding='0' id='tblPE'>";
                
                i=0;
                String nombre_institucion = "";
                String titulo = "";
                String instruccion = "";
                String fecha = "";
                String id_formacion = "";
                ResultSet rsEmpleadoIntruccion = objEmpleado.getInstruccion(id);;
                try {
                    while(rsEmpleadoIntruccion.next()){
                        nombre_institucion = (rsEmpleadoIntruccion.getString("nombreinstitucion")!=null) ? rsEmpleadoIntruccion.getString("nombreinstitucion") : "";
                        titulo = (rsEmpleadoIntruccion.getString("titulo")!=null) ? rsEmpleadoIntruccion.getString("titulo") : "";
                        instruccion = (rsEmpleadoIntruccion.getString("instruccion")!=null) ? rsEmpleadoIntruccion.getString("instruccion") : "";
                        fecha = (rsEmpleadoIntruccion.getString("fecha")!=null) ? rsEmpleadoIntruccion.getString("fecha") : "";
                        id_formacion= (rsEmpleadoIntruccion.getString("id_formacion")!=null) ? rsEmpleadoIntruccion.getString("id_formacion") : "";
                        html += "<tr id='rFO"+id_formacion+"' valign='top' class='jm_filaPar' style='cursor:pointer' onmouseover=\"this.className='jm_filaSobre'\" onmouseout=\"this.className='jm_filaPar'\">";
                        html += "<td width='160' style='cursor:pointer' onclick=\"nom_setFormacion("+id_formacion+");\"><input id='nom_ins"+id_formacion+"' name='nom_ins"+id_formacion+"' type='hidden' value='"+nombre_institucion+"' />"+nombre_institucion+"</td>";
                        html += "<td width='120' style='cursor:pointer' onclick=\"nom_setFormacion("+id_formacion+");\"><input id='titu"+id_formacion+"' name='titu"+id_formacion+"' type='hidden' value='"+titulo+"' />"+titulo+"</td>";
                        html += "<td width='120' style='cursor:pointer' onclick=\"nom_setFormacion("+id_formacion+");\"><input id='instrucci"+id_formacion+"' name='instrucci"+id_formacion+"' type='hidden' value='"+instruccion+"' />"+instruccion+"</td>";
                        html += "<td width='100' style='cursor:pointer' onclick=\"nom_setFormacion("+id_formacion+");\"><input id='fecha_titulo"+id_formacion+"' name='fecha_titulo"+id_formacion+"' type='hidden' value='"+fecha+"' />"+fecha+"</td>";
                        html += "<td width='21' align='center'><div class='jm_icoCon' style='background-position:-545px -18px;' title='Eliminar' onclick=\"nom_empleadoFormacionEliminar("+id_formacion+");\">&nbsp;</div></td>";
                        /*html += "<td align='center' width='100'><a href='formatoFactura?id="+id_punto_emision+"' target='_blank'>modificar formato</a></td>";
                        html += "<td align='center' width='20'><input type='hidden' id='idR"+i+"' value='"+id_punto_emision+"' />&nbsp;</td></tr>";*/
                        i++;
                    }
                } catch (SQLException ex) {
                    Logger.getLogger(frmEmpleado.class.getName()).log(Level.SEVERE, null, ex);
                }
                
                html += "<input id='estado_formacion' name='estado_formacion' type='hidden' value='-2' />";
                html += "<input id='id_formacion' name='id_formacion' type='hidden' />";
                html += "</table></div>";
                
                html += "</fieldset>";
            
            html += "</div>";
            
            /* QUINTO TAB */
            html += "<div id='tab14' class='jm_tabHTML' style='display:none;width:"+(ancho-30)+"px;height:"+_altBody+"px;'>";
             html += "<fieldset style='background-color:#CCEBFF'>";
                html += "<legend style='background-color:#CCEBFF'>Esta Autorizado para: </legend>";
                
                Archivo archivo = new Archivo(this._ip, this._puerto, this._db, usuario, clave);
                String path = String.valueOf(request.getRequestURL());
                path = path.substring(0, path.lastIndexOf("/"));
                String foto = path + "/img/" + archivo.getArchivo(this._dir, "tbl_empleado", id, "dni", "foto");
                archivo.cerrar();

                String subir = (id.compareTo("-1")!=0) ? "adm_setImagen('tbl_empleado', 'padre_nombre', 'foto', '"+id+"');" : "";

                html +="<div class='grupo' style='width:152px;height:177px;cursor:pointer;float:right;' title='Haga click aqu&iacute; para insertar el croquis' onclick=\""+subir+"\">"
                        + "<img id='tagImg' src='"+foto+"' width='150px' height='177px' border='0' /></div>"
                        + "<div ><br>";
                String[] carnetArray = carnet.split(",");
                for (int j = 0; j<carnetArray.length; j++) {
                    if(carnetArray[j].equals("A")){
                        html+= "<label><input type=\"checkbox\" id=\"A\" name=\"identificacion\" value=\"A\" checked> A. Trabajo de Oficina</label><br>";
                        j++;
                        if(j>carnetArray.length-1)
                            j=carnetArray.length-1;
                    }
                    else{
                        html+= "<label><input type=\"checkbox\" id=\"A\" name=\"identificacion\" value=\"A\"> A. Trabajo de Oficina</label><br>";
                    }
                    if(carnetArray[j].equals("B")){
                        html+= "<label><input type=\"checkbox\" id=\"B\" name=\"identificacion\" value=\"B\" checked> B.Trabajo de Marketing y propaganda.</label><br>";
                        j++;
                        if(j>carnetArray.length-1)
                            j=carnetArray.length-1;
                    }
                    else{
                        html+= "<label><input type=\"checkbox\" id=\"B\" name=\"identificacion\" value=\"B\" > B.Trabajo de Marketing y propaganda.</label><br>";

                    }
                    if(carnetArray[j].equals("C")){
                        html+= "<label><input type=\"checkbox\" id=\"C\" name=\"identificacion\" value=\"C\" checked> C. Trabajo de Mantenimiento y Limpieza</label><br>";
                        j++;
                        if(j>carnetArray.length-1)
                            j=carnetArray.length-1;
                    }
                    else{
                        html+= "<label><input type=\"checkbox\" id=\"C\" name=\"identificacion\" value=\"C\" > C. Trabajo de Mantenimiento y Limpieza</label><br>";
                    }
                    if(carnetArray[j].equals("D")){
                        html+= "<input type=\"checkbox\" id=\"D\" name=\"identificacion\" value=\"D\" checked> D. Trabajo de Instalaciones Menores a los 8 metros sobre el nivel (Domicilios, postes, cableado)</label><br>";
                        j++;
                        if(j>carnetArray.length-1)
                            j=carnetArray.length-1;
                    }
                    else{
                        html+= "<label><input type=\"checkbox\" id=\"D\" name=\"identificacion\" value=\"D\" > D. Trabajo de Instalaciones Menores a los 8 metros sobre el nivel (Domicilios, postes, cableado)</label><br>";
                    }
                    if(carnetArray[j].equals("E")){
                        html+= "<label><input type=\"checkbox\" id=\"E\" name=\"identificacion\" value=\"E\" checked> E. Trabajo ed Instalaciones, Antenas, Infraestructuras mayores a los 8 metros sobre el nivel</label><br>";
                        j++;
                        if(j>carnetArray.length-1)
                            j=carnetArray.length-1;
                    }
                    else{
                        html+= "<label><input type=\"checkbox\" id=\"E\" name=\"identificacion\" value=\"E\" > E. Trabajo ed Instalaciones, Antenas, Infraestructuras mayores a los 8 metros sobre el nivel</label><br>";
                    }
                    if(carnetArray[j].equals("F")){
                        html+= "<label><input type=\"checkbox\" id=\"F\" name=\"identificacion\" value=\"F\" checked> F. Planes de Emergencia y Contingencia</label><br>";
                        j++;
                        if(j>carnetArray.length-1)
                            j=carnetArray.length-1;
                    }
                    else{
                        html+= "<label><input type=\"checkbox\" id=\"F\" name=\"identificacion\" value=\"F\" > F. Planes de Emergencia y Contingencia</label><br>";
                    }
                    if(carnetArray[j].equals("G")){
                        html+= "<label><input type=\"checkbox\" id=\"G\" name=\"identificacion\" value=\"G\" checked> G. Planes de Rescate</label><br>";
                        j++;
                        if(j>carnetArray.length-1)
                            j=carnetArray.length-1;
                    }
                    else{
                        html+= "<label><input type=\"checkbox\" id=\"G\" name=\"identificacion\" value=\"G\" > G. Planes de Rescate</label><br>";
                    }
                    break;
                }
                       html+= "<input type='button' value='Guardar' onclick='nom_nuevoCarnet("+id+")'/>";
                       html+= "</div>";
                
                html += "</fieldset>";
            html += "</div>";
            
            
            if(id.compareTo("-1")==0 || (id.compareTo("-1")!=0 && objRol.getAcceso(id_rol_sesion, "empleadoEditar"))){
                html += "<input type='submit' value='Guardar' />";
            }
            html += "</div></form></div>";
            out.println(html);

            try{
                rsSucursales.close();
                rsRoles.close();
                rsDepartamentos.close();
                rsProvincias.close();
                rsCiudades.close();
            }catch(Exception e){
                e.printStackTrace();
            }

            
        } finally {
            objSuc.cerrar();
            objUsuario.cerrar();
            objRol.cerrar();
            objCargo.cerrar();
            objComun.cerrar();
            objUbicacion.cerrar();
            objEmpleado.cerrar();
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
