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

package jm.nom.clas;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import jm.web.DataBase;
import jm.web.Fecha;

/**
 *
 * @author Jorge
 */
public class Empleado extends DataBase{
    public Empleado(String m, int p, String db, String u, String c){
        super(m, p, db, u, c);
    }
    public ResultSet getEmpleado(String id)
    {
        return this.consulta("SELECT * FROM vta_empleado where id_empleado="+id+";");
    }
    public ResultSet getCarnets(String id)
    {
        return this.consulta("select id_empleado, apellido, nombre, cargo, dni, tipo_sangre, carnet, foto, padre_nombre from vta_empleado where id_empleado in ("+id+");");
    }
    public ResultSet getFamiliar(String id)
    {
        return this.consulta("SELECT * FROM tbl_familia f, tbl_parentesco p where f.id_parentesco=p.id_parentesco and id_empleado="+id+";");
    }
    public ResultSet getInstruccion(String id)
    {
        return this.consulta("SELECT * FROM tab_formaciones where id_empleado="+id+";");
    }
    public String getId(String dni)
    {
        String id_empleado = "";
        try{
            ResultSet r = this.consulta("SELECT * FROM tbl_empleado where dni='"+dni+"'");
            if(r.next()){
                id_empleado = (r.getString("id_empleado")!=null) ? r.getString("id_empleado") : "";
                r.close();
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return id_empleado;
    }
    public ResultSet getEmpleados()
    {
        return this.consulta("SELECT id_empleado,empleado FROM vta_empleado order by empleado;");
    }
    public ResultSet getCajero(String cajero)
    {
        return this.consulta("SELECT E.nombre || ' ' || E.apellido as empleado, S.sucursal FROM tbl_empleado as E inner join tbl_sucursal as S on E.id_sucursal=S.id_sucursal where E.alias='"+cajero+"';");
    }
    public ResultSet getEmpleadosResponsables()
    {
        return this.consulta("SELECT id_empleado,empleado FROM vta_empleado where id_empleado in (select id_responsable from vta_bodega) order by empleado;");
    }
    public ResultSet getEmpleados(int id_sucursal)
    {
        return this.consulta("SELECT id_empleado,empleado FROM vta_empleado where id_sucursal="+id_sucursal+" order by empleado;");
    }
    public ResultSet getEmpleadosBodega(int id_sucursal)
    {
        //return this.consulta("SELECT id_empleado,empleado FROM vta_empleado where id_sucursal="+id_sucursal+" and id_empleado in (select id_responsable from tbl_bodega where estado=true) order by empleado;");
        return this.consulta("SELECT id_empleado,empleado \n" +
        "FROM vta_empleado \n" +
        "where id_sucursal="+id_sucursal+" \n" +
        "and id_empleado in (select id_responsable from tbl_bodega where estado=true and id_responsable not in(select id_tecnico_resp from tbl_hoja_ruta where estado_hoja not in ('9'))) \n" +
        "order by empleado;");
    }
    public ResultSet getEmpleadosBodegaNoDiponibles(int id_sucursal)
    {
        //return this.consulta("SELECT id_empleado,empleado FROM vta_empleado where id_sucursal="+id_sucursal+" and id_empleado in (select id_responsable from tbl_bodega where estado=true) order by empleado;");
        return this.consulta("SELECT id_empleado,empleado \n" +
        "FROM vta_empleado \n" +
        "where id_sucursal="+id_sucursal+" \n" +
        "and id_empleado in (select id_responsable from tbl_bodega where estado=true and id_responsable in(select id_tecnico_resp from tbl_hoja_ruta where estado_hoja not in ('9'))) \n" +
        "order by empleado;");
    }
    public ResultSet getEmpleadosPersonalizacion(String txt, String tipoMov)
    {
        String clientes = "";
        if(tipoMov.compareTo("6")==0 || tipoMov.compareTo("7")==0){
            //clientes = " union select ruc as dni, razon_social as empleado from tbl_cliente where id_cliente in(select id_cliente from tbl_instalacion where anulado=false and estado_servicio not in('t','e','r'))  and (lower(ruc) like '"+txt+"%' or lower(razon_social) like '%"+txt+"%')";
            clientes = " union select ruc as dni, razon_social as empleado from tbl_cliente where id_cliente in(select id_cliente from tbl_instalacion where lower(ruc) like '"+txt+"%' or lower(razon_social) like '%"+txt+"%')";
        }
        return this.consulta("SELECT dni,empleado FROM vta_empleado where lower(dni) like '"+txt+"%' or lower(empleado) like '%"+txt+"%' "+clientes+" order by empleado limit 10 offset 0");
    }
    public ResultSet getEmpleadosDepartamentos(int id_sucursal)
    {
        return this.consulta("SELECT id_empleado,empleado, '/ ' || departamento FROM vta_empleado where id_sucursal="+id_sucursal+" and estado=true order by departamento,empleado");
    }
    public ResultSet getareas(String estado)
    {
        return this.consulta("SELECT id_area, area from tbl_area where eliminado="+estado+" order by area");
    }
    public ResultSet getCargos(String estado)
    {
        return this.consulta("SELECT id_cargo, cargo from tbl_cargo where eliminado="+estado+" order by cargo");
    }
    public ResultSet getCargosId(String id_area)
    {
        return this.consulta("SELECT id_cargo, cargo from tbl_cargo where eliminado=false and id_area="+id_area+" order by cargo");
    }
    public ResultSet getResponsableCajaChica(int id_sucursal)
    {
        return this.consulta("SELECT E.* FROM vta_empleado as E inner join tbl_sucursal as S on S.id_responsable_caja_chica=E.id_empleado where S.id_sucursal="+id_sucursal+" and E.estado=true");
    }
    public ResultSet getEmpleadosActivos()
    {
        return this.consulta("SELECT dni,empleado FROM vta_empleado WHERE estado=true order by empleado");
    }
    public ResultSet getEmpleados(int anio, int mes)
    {
        String fin = anio + "-" + mes + "-" + Fecha.getUltimoDiaMes(anio, mes);
        return this.consulta("SELECT id_empleado,empleado FROM vta_empleado where id_empleado not in (select distinct id_empleado from tbl_rol_pago_detalle where periodo='"+fin+"');");
    }
    public ResultSet getEmpleadosTareas(int id_sucursal)
    {
        return this.consulta("SELECT distinct id_tecnico_resp,nombre || ' ' || apellido FROM tbl_empleado as E inner join tbl_hoja_ruta as HR on E.id_empleado=HR.id_tecnico_resp where HR.id_sucursal="+id_sucursal+" order by nombre || ' ' || apellido");
    }
    public String getNombre(long id)
    {
        String empleado = "";
        try{
            ResultSet r = this.consulta("SELECT nombre || ' ' || apellido as empleado FROM tbl_empleado where id_empleado="+id+";");
            if(r.next()){
                empleado = (r.getString("empleado")!=null) ? r.getString("empleado") : "";
                r.close();
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return empleado;
    }
    public String getMail(String alias)
    {
        String email = "sistemas@saitel.ec";
        try{
            ResultSet r = this.consulta("SELECT email FROM tbl_empleado where alias='"+alias+"';");
            if(r.next()){
                email = (r.getString("email")!=null) ? r.getString("email") : "sistemas@saitel.ec";
                r.close();
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return email;
    }
    public int getIdEmpleado(String alias)
    {
        int id = -1;
        try{
            ResultSet r = this.consulta("SELECT id_empleado FROM tbl_empleado where alias='"+alias+"'");
            if(r.next()){
                id = (r.getString("id_empleado")!=null) ? r.getInt("id_empleado") : -1;
                r.close();
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return id;
    }
    public String getNombre(String dni)
    {
        String empleado = "";
        try{
            ResultSet r = this.consulta("SELECT nombre || ' ' || apellido as empleado FROM tbl_empleado where dni='"+dni+"'");
            if(r.next()){
                empleado = (r.getString("empleado")!=null) ? r.getString("empleado") : "";
                r.close();
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return empleado;
    }
    public String getNombres(String alias)
    {
        String empleado = "Empleado";
        try{
            ResultSet r = this.consulta("SELECT nombre || ' ' || apellido as empleado FROM tbl_empleado where alias='"+alias+"'");
            if(r.next()){
                empleado = (r.getString("empleado")!=null) ? r.getString("empleado") : "Empleado";
                r.close();
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return empleado;
    }
    /*public String getEmpleadoJSON()
    {
        ResultSet rs = this.consulta("SELECT id_empleado,ruc,serie_factura,aut_factura,toDateSQL(fecha_cad_factura),direccion,ciudad,telefono,email,contacto " +
                "FROM vta_empleado order by id_empleado;");
        String tbl = this.getJSON(rs);
        try{
            rs.close();
        }catch(Exception e){
            e.printStackTrace();
        }
        return tbl;
    }*/
    public boolean estaDuplicado(String id, String dni)
    {
        ResultSet res = this.consulta("SELECT * FROM tbl_empleado where dni='"+dni+"' and id_empleado<>"+id+";");
        if(this.getFilas(res)>0){
            return true;
        }
        try{
            res.close();
        }catch(Exception e){
            e.printStackTrace();
        }
        return false;
    }
    public String insertar(String id_sucursal, String alias, String tipo_ident, String dni, String nombre, String apellido, String sexo, String fecha_nac, String estado_civil,
            String calle, String numero, String id_provincia, String id_ciudad, String id_parroquia, String sector, String telefono, String movil, String movil_movistar, String email, String nacionalidad, 
            String ac_no, String cedula_militar, String licencia_tipo, String lugar_nacimiento, String tipo_sangre, String carne_conadis, String discapacidad, String enfermedades,
            String fecha_ingreso, String banco, String cuenta, String estado, String id_rol, String id_cargo, String id_departamento, String carnet_iess, 
            String generar_rol, String periodo_14_sueldo, String cobra_14_mensual, String cobra_f_r, String id_horario)
    {
        fecha_nac = fecha_nac.compareTo("")!=0 ? "'"+fecha_nac+"'" : "NULL";
        return this.insert("INSERT INTO tbl_empleado(id_sucursal, alias, tipo_ident, dni, nombre, apellido, sexo, fecha_nac, estado_civil, calle, numero, "
            + "id_provincia, id_ciudad, id_parroquia, sector, telefono, movil, movil_movistar, email, nacionalidad, ac_no, cedula_militar, licencia_tipo, "
            + "lugar_nacimiento, tipo_sangre, carne_conadis, discapacidad, enfermedades, fecha_ingreso, banco, cuenta, estado, id_rol, id_cargo, id_departamento, "
            + "carnet_iess, generar_rol, periodo_14_sueldo, cobra_14_mensual, cobra_f_r, id_horario) " +
            "VALUES("+id_sucursal+",'"+alias+"',  '"+tipo_ident+"', '"+dni+"', '"+nombre+"', '"+apellido+"', "+sexo+", "+fecha_nac+", '"+estado_civil+
            "', '"+calle+"', '"+numero+"', "+id_provincia+", "+id_ciudad+", "+id_parroquia+", '"+sector+"', '"+telefono+"', '"+movil+"', '"+movil_movistar+"', '"+alias+
            "@saitel.ec', '"+nacionalidad+"', '"+ac_no+"', '"+cedula_militar+"', '"+licencia_tipo+"', '"+lugar_nacimiento+
            "', '"+tipo_sangre+"', '"+carne_conadis+"', '"+discapacidad+"', '"+enfermedades+"', '"+fecha_ingreso+"', '"+banco+
            "', '"+cuenta+"', "+estado+", "+id_rol+", "+id_cargo+", '"+id_departamento+"', '"+carnet_iess+
            "', "+generar_rol+", '"+periodo_14_sueldo+"', "+cobra_14_mensual+", "+cobra_f_r+", "+id_horario+");");
    }
    
    
    public boolean actualizar(String id, String id_sucursal, String alias, String tipo_ident, String dni, String nombre, String apellido, String sexo, String fecha_nac, String estado_civil,
            String calle, String numero, String id_provincia, String id_ciudad, String id_parroquia, String sector, String telefono, String movil, String movil_movistar, String email, String nacionalidad,
            String ac_no, String cedula_militar, String licencia_tipo, String lugar_nacimiento, String tipo_sangre, String carne_conadis, String discapacidad, String enfermedades,
            String fecha_ingreso, String banco, String cuenta, String estado, String id_rol, String id_cargo, String id_departamento, String carnet_iess, 
            String generar_rol,  String periodo_14_sueldo, String cobra_14_mensual, String cobra_f_r, String id_horario)
    {
        fecha_nac = fecha_nac.compareTo("")!=0 ? "'"+fecha_nac+"'" : "NULL";
        return this.ejecutar("UPDATE tbl_empleado SET id_sucursal="+id_sucursal+", alias='"+alias+"', tipo_ident='"+tipo_ident+"', dni='"+dni+"', nombre='"+nombre+"', apellido='"+apellido+
                "', sexo="+sexo+", fecha_nac="+fecha_nac+", estado_civil='"+estado_civil+"', calle='"+calle+"', numero='"+numero+
                "', id_provincia="+id_provincia+", id_ciudad="+id_ciudad+", id_parroquia="+id_parroquia+", sector='"+sector+"', telefono='"+telefono+"', movil='"+movil+"', movil_movistar='"+movil_movistar+"', email='"+email+
                "', nacionalidad='"+nacionalidad+"', ac_no='"+ac_no+"', cedula_militar='"+cedula_militar+"', licencia_tipo='"+licencia_tipo+"', lugar_nacimiento='"+lugar_nacimiento+"', "
                + "tipo_sangre='"+tipo_sangre+"', carne_conadis='"+carne_conadis+"', discapacidad='"+discapacidad+"', enfermedades='"+enfermedades+"', fecha_ingreso='"+fecha_ingreso+
                "', banco='"+banco+"', cuenta='"+cuenta+"', estado="+estado+", id_rol="+id_rol+", id_cargo="+id_cargo+", id_departamento='"+id_departamento+"', carnet_iess='"+carnet_iess+
                "', generar_rol="+generar_rol+", periodo_14_sueldo='"+periodo_14_sueldo+"', cobra_14_mensual="+cobra_14_mensual+", cobra_f_r="+cobra_f_r+", id_horario="+id_horario+" WHERE id_empleado="+id+";");
    }
    
    public boolean insertarfamilia(String id_empleado, String nombre, String direccion, String telefono, String f_nacimiento, String carnet_conadis, String discapacidad, String vive, String id_parentesco)
    {
        f_nacimiento = f_nacimiento.compareTo("")!=0 ? "'"+f_nacimiento+"'" : "NULL";
        return this.ejecutar("INSERT INTO tbl_familia(id_empleado, nombre, direccion, telefono, fecha_nacimiento, carnet_conadis, discapacidad, vive, id_parentesco)" +
            "VALUES("+id_empleado+",'"+nombre.toUpperCase()+"','"+direccion.toUpperCase()+"', '"+telefono+"', "+f_nacimiento+", '"+carnet_conadis+"', '"+discapacidad+"', '"+vive+"', "+id_parentesco+");");
    }
    
    public boolean actualizarfamilia(String id_empleado, String nombre, String direccion, String telefono, String f_nacimiento, String carnet_conadis, String discapacidad, String vive, String id_parentesco)
    {
        f_nacimiento = f_nacimiento.compareTo("")!=0 ? "'"+f_nacimiento+"'" : "NULL";
        return this.ejecutar("UPDATE tbl_familia SET nombre='"+nombre.toUpperCase()+"', direccion='"+direccion.toUpperCase()+"', telefono='"+telefono+"', fecha_nacimiento="+f_nacimiento+", carnet_conadis='"+carnet_conadis+"', discapacidad='"+discapacidad+
                "', vive='"+vive+"', id_parentesco="+id_parentesco+"  WHERE id_familia="+id_empleado+";");
    }
    public boolean insertarformacion(String id_empleado, String nombre_institucion, String titulo, String instruccion, String fecha)
    {
        fecha = fecha.compareTo("")!=0 ? "'"+fecha+"'" : "NULL";
        return this.ejecutar("INSERT INTO tab_formaciones(id_empleado, nombreinstitucion, titulo, instruccion, fecha)" +
            "VALUES("+id_empleado+",'"+nombre_institucion.toUpperCase()+"','"+titulo.toUpperCase()+"', '"+instruccion.toUpperCase()+"', "+fecha+");");
    }
    
    public boolean actualizarformacion(String id_formacion, String nombre_institucion, String titulo, String instruccion, String fecha)
    {
        fecha = fecha.compareTo("")!=0 ? "'"+fecha+"'" : "NULL";
        return this.ejecutar("UPDATE tab_formaciones SET nombreinstitucion='"+nombre_institucion.toUpperCase()+"', titulo='"+titulo.toUpperCase()+"', instruccion='"+instruccion.toUpperCase()+"', fecha="+fecha+" WHERE id_formacion="+id_formacion+";");
    }
    public boolean eliminarfamilia(String id_empleado_familia)
    {
        return this.ejecutar("Delete from tbl_familia where id_familia="+id_empleado_familia+";");
    }
    public boolean guardarCarnet(String id_empleado, String texto)
    {
        return this.ejecutar("Update tbl_empleado set carnet='"+texto+"' where id_empleado="+id_empleado+";");
    }
    public boolean eliminarformacion(String id_formacion_empleado)
    {
        return this.ejecutar("Delete from tab_formaciones where id_formacion="+id_formacion_empleado+";");
    }
    public boolean nuevoCargo(String id_empleado, String id_cargo, String usuario, String fecha_ingreso)
    {
        return this.ejecutar("insert into tbl_cargo_empleado (id_empleado, id_cargo, usuario, fecha_ingreso) values("+id_empleado+","+id_cargo+",'"+usuario+"','"+fecha_ingreso+"');");
    }
    public boolean actualizarCargo(String id_empleado, String usuario, String id_cargo)
    {
        List ejec= new ArrayList();
        ejec.add("update tbl_cargo_empleado set fecha_salida=date 'now()' where id_cargo_empleado=(select max(id_cargo_empleado) from tbl_cargo_empleado where id_empleado="+id_empleado+");");
        ejec.add("insert into tbl_cargo_empleado (id_empleado, id_cargo, usuario, fecha_ingreso) values("+id_empleado+","+id_cargo+",'"+usuario+"',date 'now');");
        return this.transacciones(ejec);
    }
    
    
    
    public boolean personalizacionesPendientes(String usuario)
    {
        try{
            ResultSet rs = this.consulta("select P.* from tbl_activo_personalizacion as P inner join tbl_empleado as E on P.dni_recibe=E.dni where E.alias='"+usuario+"' and P.aceptada=false");
            if(this.getFilas(rs)>0){
                return true;
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return false;
    }
    
}