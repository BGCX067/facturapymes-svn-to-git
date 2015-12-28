/**
** @version 1.0
** @package FACTURAPYMES.
* @author Jorge Washington Mueses Cevallos.
* @copyright Copyright (C) 2011 por Jorge Mueses. Todos los derechos reservados.
* @license http://www.gnu.org/copyleft/gpl.html GNU/GPL.
** FACTURAPYMES es un software de libre distribuciÃ³n, que puede ser
* copiado y distribuido bajo los tÃ©rminos de la Licencia
* Attribution-NonCommercial-NoDerivs 3.0 Unported,
* de acuerdo con la publicada por la CREATIVE COMMONS CORPORATION.
*/

package jm.adm.clas;
import java.sql.ResultSet;
import jm.web.DataBase;

public class ListaPrecio extends DataBase{
    public ListaPrecio(String m, int p, String db, String u, String c){
        super(m, p, db, u, c);
    }
    public ResultSet getLista(String id)
    {
        return this.consulta("SELECT * from tbl_producto_precio WHERE id_lista_precio="+id+";");
    }
    public ResultSet getListas()
    {
        return this.consulta("SELECT id_lista_precio,lista from tbl_producto_precio;");
    }
    public ResultSet getPrecios(String id_producto)
    {
        return this.consulta("SELECT * from tbl_producto_precio where id_producto="+id_producto+" order by utilidad desc");
    }
    public ResultSet getListasVentas()
    {
        return this.consulta("SELECT id_lista_precio,lista,utilidad from tbl_producto_precio order by utilidad desc;");
    }
    public String getListasJSON()
    {
        ResultSet rs = this.consulta("SELECT id_lista_precio,lista,utilidad from tbl_producto_precio;");
        return this.getJSON(rs);
    }
    public boolean estaDuplicado(String id, String lista)
    {
        ResultSet res = this.consulta("SELECT * FROM tbl_producto_precio where lower(lista)='"+lista.toLowerCase()+"' and id_lista_precio<>"+id+";");
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
    public boolean bajoUtilidadMinima(String id, float utilidad)
    {
        float utilidad_min = 0;
        try{
            ResultSet res = this.consulta("SELECT utilidad_min FROM tbl_producto where id_producto="+id);
            if(res.next()){
                utilidad_min = res.getString("utilidad_min")!=null ? res.getFloat("utilidad_min") : 0;
            }
            res.close();
        }catch(Exception e){
            e.printStackTrace();
        }
        if(utilidad_min > utilidad){
            return true;
        }
        return false;
    }
    public boolean utilidadDuplicada(String id, String id_producto, String utilidad)
    {
        ResultSet res = this.consulta("SELECT * FROM tbl_producto_precio where utilidad="+utilidad+" and id_producto="+id_producto+" and id_lista_precio<>"+id+";");
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
    public boolean insertar(String id_producto, String lista, String utilidad)
    {
        return this.ejecutar("INSERT INTO tbl_producto_precio(id_producto,lista,utilidad) VALUES("+id_producto+", '"+lista+"', "+utilidad+");");
    }
    public boolean actualizar(String id, String lista, String utilidad)
    {
        return this.ejecutar("UPDATE tbl_producto_precio SET lista='"+lista+"', utilidad="+utilidad+" WHERE id_lista_precio="+id+";");
    }
    public boolean masUnaLista(String id)
    {
        ResultSet res = this.consulta("SELECT * FROM tbl_producto_precio where id_producto=(select id_producto from tbl_producto_precio where id_lista_precio="+id+")");
        if(this.getFilas(res)>1){
            return true;
        }
        try{
            res.close();
        }catch(Exception e){
            e.printStackTrace();
        }
        return false;
    }
    public boolean eliminar(String id)
    {
        return this.ejecutar("DELETE FROM tbl_producto_precio WHERE id_lista_precio="+id+";");
    }
}