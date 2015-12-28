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

package jm.seg.clas;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import jm.web.DataBase;

public class Rol extends DataBase{
    public Rol(String m, int p, String db, String u, String c){
        super(m, p, db, u, c);
    }
    public ResultSet getRoles()
    {
        return this.consulta("SELECT * FROM vta_rol;");
    }
    public ResultSet getParentesco()
    {
        return this.consulta("SELECT * FROM tbl_parentesco;");
    }
    public ResultSet getRol(String id)
    {
        return this.consulta("SELECT * FROM vta_rol where id_rol="+id+";");
    }
    public String getNombre(String id)
    {
        String rol = "";
        try{
            ResultSet rs = this.consulta("SELECT * FROM tbl_rol where id_rol="+id+";");
            if(rs.next()){
                rol = rs.getString("rol")!=null ? rs.getString("rol") : "";
                rs.close();
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return rol;
    }
    public ResultSet getCargos()
    {
        return this.consulta("SELECT * FROM vta_rol where rol<>'administradores';");
    }
    public boolean estaDuplicado(String id, String r)
    {
        ResultSet res = this.consulta("SELECT * FROM tbl_rol where lower(rol)='"+r.toLowerCase()+"' and id_rol<>"+id+";");
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
    public boolean insertar(String rol)
    {
        List sql = new ArrayList();
        sql.add("create group "+rol.toLowerCase()+";");
        
        try{
            String privilegios = "";
            String tabla = "";
            boolean seleccion = false;
            boolean isercion = false;
            boolean actualizacion = false;
            boolean borrar = false;
            ResultSet rs = this.consulta("select * from tbl_catalogo order by tabla");
            while(rs.next()){
                tabla = rs.getString("tabla")!=null ? rs.getString("tabla") : "";
                seleccion = rs.getString("seleccion")!=null ? rs.getBoolean("seleccion") : false;
                isercion = rs.getString("isercion")!=null ? rs.getBoolean("isercion") : false;
                actualizacion = rs.getString("actualizacion")!=null ? rs.getBoolean("actualizacion") : false;
                borrar = rs.getString("borrar")!=null ? rs.getBoolean("borrar") : false;
                
                if(seleccion){
                    privilegios = "SELECT";
                }
                if(isercion){
                    privilegios += privilegios.compareTo("")!=0 ? ", INSERT" : "INSERT";
                }
                if(actualizacion){
                    privilegios += privilegios.compareTo("")!=0 ? ", UPDATE" : "UPDATE";
                }
                if(borrar){
                    privilegios += privilegios.compareTo("")!=0 ? ", DELETE" : "DELETE";
                }
                
                sql.add("GRANT " + privilegios + " ON " + tabla + " TO " + rol.toLowerCase() + ";");
            }
            rs.close();
        }catch(Exception e){
            e.printStackTrace();
        }
        
        sql.add("INSERT INTO tbl_rol(rol) VALUES('"+rol+"');");
        sql.add("select restablecerPrivilegios();");
        return this.transacciones(sql);        
    }
    public boolean actualizar(String id, String rol_ant, String rol)
    {
        if(rol.compareTo("administradores")!=0){
            List sql = new ArrayList();
            sql.add("ALTER ROLE "+rol_ant+" RENAME TO "+rol+";");
            sql.add("UPDATE tbl_rol SET rol='"+rol+"' WHERE id_rol="+id+";");
            return this.transacciones(sql); 
        }
        return true;
    }
    public ResultSet getMenus(String id)
    {
        return this.consulta("select id_pagina,txt_modulo,descripcion from vta_pagina where id_pagina not in(select id_pagina from vta_rol_priv_pagina WHERE id_rol=" + id + ") order by modulo,id_pagina;");
    }
    public ResultSet getPrivilegios(String id)
    {
        return this.consulta("select id_pagina,txt_modulo,descripcion from vta_rol_priv_pagina where id_rol=" + id + " order by modulo,id_pagina;");
    }    
    public ResultSet concederPriv(String id_rol, String id_pa)
    {
        return this.consulta("INSERT INTO tbl_privilegio VALUES(" + id_rol + ", "  + id_pa + ");");
    }
    public ResultSet revocarPriv(String id_rol, String id_pa)
    {
        return this.consulta("DELETE FROM tbl_privilegio WHERE id_rol=" + id_rol + " and id_pagina="  + id_pa + ";");
    }
    public ResultSet getAccesos(String id)
    {
        return this.consulta("select P.pagina from tbl_pagina as P inner join tbl_privilegio as PR on P.id_pagina=PR.id_pagina WHERE PR.id_rol=" + id + " order by pagina;");
    }
    public boolean getAcceso(String id, String pag)
    {
        try{
            ResultSet res = this.consulta("select P.pagina from tbl_pagina as P inner join tbl_privilegio as PR on P.id_pagina=PR.id_pagina WHERE PR.id_rol=" + id + " and pagina='"+pag+"';");
            if(this.getFilas(res)>0){
                return true;
            }
            res.close();
        }catch(Exception e){
            e.printStackTrace();
        }
        return false;
    }
}
