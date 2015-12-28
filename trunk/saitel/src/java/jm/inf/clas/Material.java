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

package jm.inf.clas;
import java.sql.ResultSet;
import jm.web.DataBase;

public class Material extends DataBase{
    public Material(String m, int p, String db, String u, String c){
        super(m, p, db, u, c);
    }
    public ResultSet getMaterial(String id)
    {
        return this.consulta("SELECT * from vta_material WHERE id_material="+id);
    }
    public ResultSet getMateriales(int id_sucursal)
    {
        return this.consulta("SELECT * from vta_material WHERE id_sucursal="+id_sucursal);
    }
    public boolean estaDuplicado(String id, String id_producto)
    {
        ResultSet res = this.consulta("SELECT * FROM tbl_material where id_producto="+id_producto+" and id_material<>"+id);
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
    public boolean insertar(String id_sucursal, String id_producto, String id_plan_cuenta_gasto)
    {
        boolean res = false;
        String pk = this.insert("INSERT INTO tbl_material(id_sucursal,id_producto,id_plan_cuenta_gasto) VALUES("+id_sucursal+", "+id_producto+", "+id_plan_cuenta_gasto+")");
        if(pk.compareTo("-1")!=0){
            res = true;
        }
        return res;
    }
    public boolean actualizar(String id, String id_sucursal, String id_producto, String id_plan_cuenta_gasto)
    {
        return this.ejecutar("UPDATE tbl_material SET id_sucursal="+id_sucursal+", id_producto="+id_producto+", id_plan_cuenta_gasto="+id_plan_cuenta_gasto+" WHERE id_material="+id);
    }

}