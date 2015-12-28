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

public class Usuario extends DataBase{
    public Usuario(String m, int p, String db, String u, String c){
        super(m, p, db, u, c);
    }
    public ResultSet autenticar(String u, String c)
    {
        ResultSet r = this.consulta("SELECT * FROM vta_usuario_rol WHERE alias='"+u+"' and clave=md5('"+c+"');");
        return r;
    }
    public ResultSet nuevaClave(String u)
    {
        ResultSet r = this.consulta("SELECT * FROM vta_usuario WHERE alias='"+u+"';");
        return r;
    }     
    public boolean cambiarClave(String u, String c)
    {
        List sql = new ArrayList();
        sql.add("UPDATE tbl_usuario SET clave=md5('"+c+"') WHERE alias='"+u+"';");
        sql.add("alter user "+u+" password '"+c+"';");
        return this.transacciones(sql); 
    }
    public ResultSet getUsuario(String a)
    {
        return this.consulta("SELECT * from tbl_usuario WHERE alias='"+a+"';");        
    }
    public ResultSet getUsuarios()
    {
        return this.consulta("SELECT alias,alias as alias1 from tbl_usuario where estado=true order by alias");
    }
    public boolean setSesion(String u, String s)
    {
        return this.ejecutar("UPDATE tbl_usuario SET sesion='"+s+"' WHERE alias='"+u+"';");        
    }
    public boolean lector(String a)
    {
        boolean lect = false;
        try{
            ResultSet r = this.consulta("SELECT * FROM tbl_usuario where alias='"+a+"';");
            if(r.next()){
                lect = (r.getString("lector")!=null) ? r.getBoolean("lector") : false;
                r.close();
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return lect;
    }
    public boolean setLector(String u, String l)
    {
        return this.ejecutar("UPDATE tbl_usuario SET lector="+l+" WHERE alias='"+u+"';");
    }
    public String getSesion(String a)
    {
        try{
            ResultSet r = this.consulta("SELECT * FROM tbl_usuario where alias='"+a+"';");
            if(r.next()){
                String sesion = (r.getString("sesion")!=null) ? r.getString("sesion") : "";
                r.close();
                return sesion;
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return "";
    }
    public boolean estaDuplicado(String a)
    {
        ResultSet res = this.consulta("SELECT * FROM tbl_usuario where lower(alias)='"+a.toLowerCase()+"';");
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
    public boolean insertar(String a, String c, String r, String ip, String e, String rol)
    {
        List sql = new ArrayList();
        if(r.compareTo("1")==0){
            sql.add("create user "+a+" password '"+a+"' SUPERUSER CREATEROLE VALID UNTIL 'infinity';");
        }else{
            sql.add("create user "+a+" password '"+a+"' NOCREATEDB NOCREATEUSER VALID UNTIL 'infinity';");
        }
        sql.add("alter group "+rol+" add user "+a+";");
        sql.add("INSERT INTO tbl_usuario(alias,clave,id_rol,autenticacion_ip,estado,sesion) " +
                "VALUES('"+a+"', md5('"+c+"'), "+r+", '"+ip+"', "+e+", false);");
        return this.transacciones(sql);   
    }
    public boolean actualizar(String a, String c, String r, String ip, String e, String rol, String rol_ant)
    {
        List sql = new ArrayList();
        if(rol.compareTo(rol_ant)!=0){
            sql.add("alter group "+rol_ant+" drop user "+a+";");
            sql.add("alter group "+rol.toLowerCase()+" add user "+a+";");
        }
        if(c.compareTo("")!=0){
            sql.add("UPDATE tbl_usuario SET id_rol="+r+", autenticacion_ip='"+ip+"', estado="+e+", clave=md5('"+c+"') WHERE alias='"+a+"';");
            if(r.compareTo("1")==0){
                sql.add("alter user "+a+" SUPERUSER CREATEROLE password '"+c+"';");
            }else{
                sql.add("alter user "+a+" NOSUPERUSER NOCREATEROLE password '"+c+"';");
            }
        }else{
            sql.add("UPDATE tbl_usuario SET id_rol="+r+", autenticacion_ip='"+ip+"', estado="+e+" WHERE alias='"+a+"';");
            if(r.compareTo("1")==0){
                sql.add("alter user "+a+" SUPERUSER CREATEROLE;");
            }else{
                sql.add("alter user "+a+" NOSUPERUSER NOCREATEROLE;");
            }
        }
        return this.transacciones(sql);  
    }

    public String getPosAyuda(String a)
    {
        String valor = "769:72";
        try{
            ResultSet r = this.consulta("SELECT pos_ayuda FROM tbl_usuario where alias='"+a+"';");
            if(r.next()){
                valor = (r.getString("pos_ayuda")!=null) ? r.getString("pos_ayuda") : "769:72";
                r.close();
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return valor;
    }
    public boolean setPosAyuda(String alias, String valores)
    {
        return this.ejecutar("UPDATE tbl_usuario SET pos_ayuda='"+valores+"' WHERE alias='"+alias+"';");
    }
    public boolean getDisplayAyuda(String a)
    {
        boolean valor = true;
        try{
            ResultSet r = this.consulta("SELECT display_ayuda FROM tbl_usuario where alias='"+a+"';");
            if(r.next()){
                valor = (r.getString("display_ayuda")!=null) ? r.getBoolean("display_ayuda") : true;
                r.close();
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return valor;
    }
    public boolean setDisplayAyuda(String alias, String valor)
    {
        return this.ejecutar("UPDATE tbl_usuario SET display_ayuda="+valor+" WHERE alias='"+alias+"';");
    }
}