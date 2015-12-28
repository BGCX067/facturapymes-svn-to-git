/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package jm.adm.clas;

import java.sql.ResultSet;
import jm.web.DataBase;

/**
 *
 * @author Desarrollo
 */
public class Mensaje extends DataBase {
    public Mensaje(String m, int p, String db, String u, String c){
        super(m, p, db, u, c);
    }

    public ResultSet getMensajes(String usuario)
    {
        return this.consulta("select * from tbl_mensaje where mostrar=true and now() between (fecha_activo_ini || ' ' || hora_activo_ini)::timestamp and (fecha_activo_fin || ' ' || hora_activo_fin)::timestamp "
                + "and id_mensaje not in ( select id_mensaje from tbl_mensaje_usuario where usuario='"+usuario+"' ) order by id_mensaje");
    }
    
    public boolean setMensaje(String usuarion, String mensaje, String modulo, String id_reg_modulo)
    {
        return this.ejecutar("insert into tbl_mensaje(usuario, mensaje, modulo, id_reg_modulo, fecha_activo_ini, fecha_activo_fin, hora_activo_fin, mostrar) "
                + "values('"+usuarion+"', '"+mensaje+"', '"+modulo+"', "+id_reg_modulo+", now(), now(), '23:59:59', true)");
    }

    public boolean setMensaje(String usuarion, String mensaje, String modulo, String id_reg_modulo, String fecha_activo_fin)
    {
        return this.ejecutar("insert into tbl_mensaje(usuario, mensaje, modulo, id_reg_modulo, fecha_activo_ini, fecha_activo_fin, hora_activo_fin, mostrar) "
                + "values('"+usuarion+"', '"+mensaje+"', '"+modulo+"', "+id_reg_modulo+", now(), '"+fecha_activo_fin+"', '23:59:59', true)");
    }
    public boolean setMensaje(String usuarion, String mensaje, String modulo, String id_reg_modulo, String fecha_activo_fin, String mas_hora_activo_fin)
    {
        return this.ejecutar("insert into tbl_mensaje(usuario, mensaje, modulo, id_reg_modulo, fecha_activo_ini, fecha_activo_fin, hora_activo_fin, mostrar) "
                + "values('"+usuarion+"', '"+mensaje+"', '"+modulo+"', "+id_reg_modulo+", now(), '"+fecha_activo_fin+"', (now() + '"+mas_hora_activo_fin+" hour'::interval)::time, true)");
    }

}
