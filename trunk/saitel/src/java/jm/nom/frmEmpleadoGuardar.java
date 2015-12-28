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
import jm.nom.clas.Empleado;
import jm.seg.clas.Auditoria;
import jm.seg.clas.Rol;
import jm.seg.clas.Usuario;

/**
 *
 * @author Jorge
 */
public class frmEmpleadoGuardar extends HttpServlet {
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
        
        Empleado objEmpleado = new Empleado(this._ip, this._puerto, this._db, usuario, clave);
        Rol objRol = new Rol(this._ip, this._puerto, this._db, usuario, clave);
        Usuario objUsr = new Usuario(this._ip, this._puerto, this._db, usuario, clave);
        
        try {
            String r = "msg»El documento nacional de identificación (DNI) del empleado ya existe.";
            String WHERE = request.getParameter("WHERE");
            String p = request.getParameter("p") != null ? request.getParameter("p") : "0";
            String id = request.getParameter("id");
            String id_sucursal = request.getParameter("id_sucursal");
            String alias = request.getParameter("alias");
            String tipo_ident = request.getParameter("tipo_ident");
            String dni = request.getParameter("dni").toUpperCase();
            String nombre = request.getParameter("nombre").toUpperCase();
            String apellido = request.getParameter("apellido").toUpperCase();
            String sexo = request.getParameter("sexo");
            String fecha_nac = request.getParameter("fecha_nac");
            String lugar_nacimiento = request.getParameter("lugar_nacimiento");
            String estado_civil = request.getParameter("estado_civil");
            String calle = request.getParameter("calle").toUpperCase();
            String numero = request.getParameter("numero");
            String id_provincia = request.getParameter("id_provincia");
            String id_ciudad = request.getParameter("id_ciudad");
            String id_parroquia = request.getParameter("id_parroquia");
            String sector = request.getParameter("sector");
            String telefono = request.getParameter("telefono");
            String movil = request.getParameter("movil");
            String movil_movistar = request.getParameter("movil_movistar");
            String email = request.getParameter("email");
            String nacionalidad = request.getParameter("nacionalidad").toUpperCase();
            
            String ac_no = request.getParameter("ac_no");
            String cedula_militar = request.getParameter("cedula_militar");
            String licencia_tipo = request.getParameter("licencia_tipo");
            String tipo_sangre = request.getParameter("tipo_sangre");
            String carne_conadis = request.getParameter("carne_conadis");
            String discapacidad = request.getParameter("discapacidad");
            String enfermedades = request.getParameter("enfermedades");

            String fecha_ingreso = request.getParameter("fecha_ing");
            String carnet_iess = request.getParameter("carnet_iess");
            String generar_rol = request.getParameter("generar_rol");
            String carga_familiar = request.getParameter("carga_familiar");            
            String periodo_14_sueldo = request.getParameter("periodo_14_sueldo");
            String cobra_14_mensual = request.getParameter("cobra_14_mensual");
            String cobra_f_r = request.getParameter("cobra_f_r");
            String banco = request.getParameter("banco");
            String cuenta = request.getParameter("cuenta");
            String estado = request.getParameter("estado");
            String id_rol = request.getParameter("id_rol");
            String id_cargo = request.getParameter("id_cargo");
            //String id_departamento = request.getParameter("id_departamento");
            //String sueldo = request.getParameter("sueldo");
            String id_departamento = request.getParameter("id_cargo");
            //String observacion = request.getParameter("observacion");
            
            String padre_nombre = request.getParameter("padre_nombre");
            String padre_apellido = request.getParameter("padre_apellido");
            String padre_dir_trabajo = request.getParameter("padre_dir_trabajo");
            String padre_telefono = request.getParameter("padre_telefono");
            
            String madre_nombre = request.getParameter("madre_nombre");
            String madre_apellido = request.getParameter("madre_apellido");
            String madre_dir_trabajo = request.getParameter("madre_dir_trabajo");
            String madre_telefono = request.getParameter("madre_telefono");
            
            String coyg_nombre = request.getParameter("coyg_nombre");
            String coyg_apellido = request.getParameter("coyg_apellido");
            String coyg_dir_trabajo = request.getParameter("coyg_dir_trabajo");
            String coyg_telefono = request.getParameter("coyg_telefono");
            String id_horario = request.getParameter("id_Horario");
            
            String id_empleado_f = request.getParameter("id_fa");
            
            String nombre_f = request.getParameter("padre_nombre");
            String direccion_f = request.getParameter("padre_dir_trabajo");
            String telefono_f = request.getParameter("padre_telefono");
            
            String f_nacimiento_f = request.getParameter("fecha_nac_familia");
            String id_parentesco_f = request.getParameter("id_parentesco");
            String carnet_conadis_f = request.getParameter("carne_conadis_familia");
            String discapacidad_f = request.getParameter("discapacidad_familia");
            String vive_f = request.getParameter("vive");
            String estado_f = request.getParameter("est");
            
            String nombre_institucion = request.getParameter("nombre_institucion");
            String instruccion = request.getParameter("instruccion");
            String titulo_obtenido = request.getParameter("titulo_ob");
            String fecha = request.getParameter("fecha_for");
            String estado_formacion = request.getParameter("estado_formacion");
            String id_formacion = request.getParameter("id_formacion");
            String cargo_antes = request.getParameter("nom_cargo");
            String autenticacion_ip = "127.0.0.1 - 0:0:0:0:0:0:0:1";
            String rol = objRol.getNombre(id_rol);
            
            if(!objEmpleado.estaDuplicado(id, dni)){
                r = "msg»Ha ocurrido un error inesperado, por favor, vuelva a intentarlo más tarde o " +
                    "contáctese con el administrador del sistema para mayor información.";
                if(id.compareTo("-1")==0){                    
                    //Usuario
                    String p_nombre="";
                    String s_nombre="";
                    String p_apellido="";
                    for(int j=0;;j++)
                    {
                        if(nombre.charAt(j)==' ')
                        {
                            s_nombre=nombre.substring((j+1), nombre.length());
                            p_nombre=nombre.substring(0,j);
                            break;
                        }
                    }
                    for(int j=0;;j++)
                    {
                        if(apellido.charAt(j)==' ')
                        {
                            p_apellido=apellido.substring(0, j);
                            break;
                        }
                    }
                    p_nombre=p_nombre.toLowerCase();
                    s_nombre=s_nombre.toLowerCase();
                    p_apellido=p_apellido.toLowerCase();
                    //alias=p_nombre.substring(0,1)+""+s_nombre.substring(0,1)+""+p_apellido;
                    for(int j=1;j<nombre.length();j++)
                    {
                        if(!objUsr.estaDuplicado(alias)){
                            if(objUsr.insertar(alias, alias, id_rol, autenticacion_ip, estado, rol)){
                                Auditoria auditoria = new Auditoria(this._ip, this._puerto, this._db, usuario, clave);
                                auditoria.setRegistro(request, "INGRESO DEL NUEVO USUARIO: "+alias);
                                auditoria.cerrar();
                            }
                            break;
                        }
                        else
                        {
                            alias=p_nombre.substring(0,j)+s_nombre.substring(0,(j-1))+p_apellido;
                        }
                    }
                    
                    id=objEmpleado.insertar(id_sucursal, alias, tipo_ident, dni, nombre, apellido, sexo, fecha_nac, estado_civil, calle, numero, id_provincia,
                            id_ciudad, id_parroquia, sector, telefono, movil, movil_movistar, email, nacionalidad, ac_no, cedula_militar, licencia_tipo, lugar_nacimiento, 
                            tipo_sangre, carne_conadis, discapacidad, enfermedades, fecha_ingreso, banco, cuenta, estado, id_rol, id_cargo, id_departamento, carnet_iess,
                            generar_rol, periodo_14_sueldo, cobra_14_mensual, cobra_f_r, id_horario);
                    if (id.compareTo("-1")!=0){
                        Auditoria auditoria = new Auditoria(this._ip, this._puerto, this._db, usuario, clave);
                        auditoria.setRegistro(request, "INGRESO DE NUEVO EMPLEADO: "+nombre+" "+apellido);
                        auditoria.cerrar();
                        r = "tbl»"+objEmpleado.paginar("vta_empleado", "id_empleado,departamento,rol,dni,empleado", WHERE, Integer.parseInt(p), 100)+"^id»-1^msg»Información ingresada satisfactoriamente.";
                    }
                     if(objEmpleado.nuevoCargo(id, id_cargo, usuario, fecha_ingreso)){
                        Auditoria auditoria = new Auditoria(this._ip, this._puerto, this._db, usuario, clave);
                        auditoria.setRegistro(request, "NUEVO CARGO DEL EMPLEADO: "+nombre+" "+apellido);
                        auditoria.cerrar();
                        r = "tbl»"+objEmpleado.paginar("vta_empleado", "id_empleado,departamento,rol,dni,empleado", WHERE, Integer.parseInt(p), 100)+"^id»-1^msg»Información ingresada satisfactoriamente.";
                    }
                
                }else{
                    
                    if(objEmpleado.actualizar(id, id_sucursal, alias, tipo_ident, dni, nombre, apellido, sexo, fecha_nac, estado_civil, calle, numero, id_provincia,
                            id_ciudad, id_parroquia, sector, telefono, movil, movil_movistar, email, nacionalidad, ac_no, cedula_militar, licencia_tipo, lugar_nacimiento, 
                            tipo_sangre, carne_conadis, discapacidad, enfermedades, fecha_ingreso, banco, cuenta, estado, id_rol, id_cargo, id_departamento, carnet_iess,
                            generar_rol, periodo_14_sueldo, cobra_14_mensual, cobra_f_r, id_horario)){
                        Auditoria auditoria = new Auditoria(this._ip, this._puerto, this._db, usuario, clave);
                        auditoria.setRegistro(request, "ACTUALIZACION DE LA INFORMACION DEL EMPLEADO: "+nombre+" "+apellido);
                        
                        //Familia
                        if(estado_f.compareTo("-1")==0&&nombre_f.compareTo("")!=0){
                            if(objEmpleado.insertarfamilia(id, nombre_f, direccion_f, telefono_f, f_nacimiento_f, carnet_conadis_f, discapacidad_f, vive_f, id_parentesco_f)){
                                auditoria.setRegistro(request, "INGRESO DE NUEVA FAMILIA DEL EMPLEADO: "+nombre);
                                //r = "tbl»"+objEmpleado.paginar("vta_empleado", "id_empleado,departamento,rol,dni,empleado", WHERE, Integer.parseInt(p), 100)+"^id»-1^msg»Información ingresada satisfactoriamente.";
                            }
                        }
                        if(estado_f.compareTo("1")==0&&nombre_f.compareTo("")!=0){
                            if(objEmpleado.actualizarfamilia(id_empleado_f, nombre_f, direccion_f, telefono_f, f_nacimiento_f, carnet_conadis_f, discapacidad_f, vive_f, id_parentesco_f)){
                                auditoria.setRegistro(request, "ACTUALIZACION DE LA INFORMACION DE LA FAMILIA DEL EMPLEADO: "+nombre);
                                //r = "tbl»"+objEmpleado.paginar("vta_empleado", "id_empleado,departamento,rol,dni,empleado", WHERE, Integer.parseInt(p), 100)+"^msg»Información guardada satisfactoriamente.";
                            }else{
                                r = "msg»" + objEmpleado.getError();
                            }
                        }
                        
                        //Formacion
                        if(estado_formacion.compareTo("-2")==0&&nombre_institucion.compareTo("")!=0){
                            if(objEmpleado.insertarformacion(id, nombre_institucion, titulo_obtenido, instruccion, fecha)){
                                auditoria.setRegistro(request, "INGRESO DE NUEVA FORMACION DEL EMPLEADO: "+nombre);
                                //r = "tbl»"+objEmpleado.paginar("vta_empleado", "id_empleado,departamento,rol,dni,empleado", WHERE, Integer.parseInt(p), 100)+"^id»-1^msg»Información ingresada satisfactoriamente.";
                            }
                            else
                            {
                                r = "msg»" + objEmpleado.getError();
                            }
                        }
                        if(estado_formacion.compareTo("2")==0&&nombre_institucion.compareTo("")!=0){
                            if(objEmpleado.actualizarformacion(id_formacion, nombre_institucion, titulo_obtenido, instruccion, fecha)){
                                auditoria.setRegistro(request, "ACTUALIZACION DE LA INFORMACION DE LA FORMACION DEL EMPLEADO: "+nombre);
                                //r = "tbl»"+objEmpleado.paginar("vta_empleado", "id_empleado,departamento,rol,dni,empleado", WHERE, Integer.parseInt(p), 100)+"^msg»Información guardada satisfactoriamente.";
                            }else{
                                r = "msg»" + objEmpleado.getError();
                            }
                        }
                        
                        if(cargo_antes.compareTo(id_cargo)!=0)
                        {
                            if(objEmpleado.actualizarCargo(id,usuario,id_cargo)){
                                auditoria.setRegistro(request, "CARGO ACTUALIZADO DEL EMPLEADO: "+nombre+" "+apellido);
                            }
                            else{
                                r = "msg»" + objEmpleado.getError();
                            }
                        }
                        auditoria.cerrar();
                        r = "tbl»"+objEmpleado.paginar("vta_empleado", "id_empleado,departamento,rol,dni,empleado", WHERE, Integer.parseInt(p), 100)+"^msg»Información guardada satisfactoriamente.";
                    }
                }
            }

            out.print(r);
        } finally {
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
