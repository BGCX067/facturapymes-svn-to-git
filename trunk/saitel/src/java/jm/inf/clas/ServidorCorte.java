/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jm.inf.clas;

import java.sql.ResultSet;
import jm.web.DataBase;

/**
 *
 * @author jorge
 */
public class ServidorCorte extends DataBase {
    public ServidorCorte(String m, int p, String db, String u, String c){
        super(m, p, db, u, c);
    }
    public ResultSet getServidorCorte(String id)
    {
        return this.consulta("SELECT * from tbl_servidor_ftp WHERE id_servidor_ftp="+id);
    }  
    public ResultSet getServidoes()
    {
        return this.consulta("SELECT * from tbl_servidor_ftp where estado=true");
    }
    public boolean estaDuplicado(String id, String ip)
    {
        ResultSet res = this.consulta("SELECT * FROM tbl_servidor_ftp where servidor='"+ip+"' and id_servidor_ftp<>"+id);
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
    public boolean insertar(String id_sucursal, String servidor, int puerto, String subredes, String usuario, String clave, String estado)
    {
        boolean res = false;
        String pk = this.insert("INSERT INTO tbl_servidor_ftp(id_sucursal, servidor, puerto, subredes, usuario, clave, estado) "
                + "VALUES("+id_sucursal+", '"+servidor+"', "+puerto+", '"+subredes+"', '"+usuario+"', '"+clave+"', "+estado+");");
        if(pk.compareTo("-1")!=0){
            res = true;
        }
        return res;
    }
    public boolean actualizar(String id, String id_sucursal, String servidor, int puerto, String subredes, String usuario, String clave, String estado)
    {
        return this.ejecutar("UPDATE tbl_servidor_ftp SET id_sucursal="+id_sucursal+", servidor='"+servidor+"', puerto="+puerto+
                ", subredes='"+subredes+"', usuario='"+usuario+"', clave='"+clave+"', estado="+estado+" where id_servidor_ftp="+id);
    }
    
    public boolean actualizarMegas(String id_servidor_ftp, long ancho_corp, long ancho_small, long ancho_resi, long ancho_noct)
    {
        return this.ejecutar("UPDATE tbl_servidor_ftp SET megas_corporativo="+ancho_corp+", megas_small="+ancho_small+
                ", megas_residencial="+ancho_resi+", megas_nocturno="+ancho_noct+" where id_servidor_ftp="+id_servidor_ftp);
    }
    
}
