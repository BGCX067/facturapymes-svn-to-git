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

package jm.adm.clas;
import java.sql.ResultSet;
import jm.web.DataBase;

public class TablaDepreciacion extends DataBase{
    public TablaDepreciacion(String m, int p, String db, String u, String c){
        super(m, p, db, u, c);
    }
    public ResultSet getDepreciacion(String id)
    {
        return this.consulta("SELECT * from vta_tabla_depreciacion WHERE id_tabla_depreciacion="+id+";");
    }
    public ResultSet getTablaDepreciaciones()
    {
        return this.consulta("SELECT id_tabla_depreciacion,depreciacion from vta_tabla_depreciacion order by depreciacion;");
    }
    public String getTablaDepreciacionesJSON()
    {
        String json = "";
        try{
            ResultSet rs = this.consulta("SELECT id_tabla_depreciacion,num_anios,porcentaje from vta_tabla_depreciacion order by id_tabla_depreciacion;");
            json = this.getJSON(rs);
            if(rs.next()){
                rs.close();
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return json;
    }
    public boolean estaDuplicado(String id, String lista)
    {
        ResultSet res = this.consulta("SELECT * FROM tbl_tabla_depreciacion where lower(depreciacion)='"+lista.toLowerCase()+"' and id_tabla_depreciacion<>"+id+";");
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
    public boolean insertar(String depreciacion, String id_plan_cuenta_grupo, String id_plan_cuenta_gasto, String id_plan_cuenta, String num_anios, String porcentaje)
    {
        return this.ejecutar("INSERT INTO tbl_tabla_depreciacion(depreciacion,id_plan_cuenta_grupo,id_plan_cuenta_gasto,id_plan_cuenta,num_anios,porcentaje) "
                + "VALUES('"+depreciacion+"', "+id_plan_cuenta_grupo+", "+id_plan_cuenta_gasto+", "+id_plan_cuenta+", "+num_anios+", "+porcentaje+");");
    }
    public boolean actualizar(String id, String depreciacion, String id_plan_cuenta_grupo, String id_plan_cuenta_gasto, String id_plan_cuenta, String num_anios, String porcentaje)
    {
        return this.ejecutar("UPDATE tbl_tabla_depreciacion SET depreciacion='"+depreciacion+"', id_plan_cuenta_grupo="+id_plan_cuenta_grupo+
                ", id_plan_cuenta_gasto="+id_plan_cuenta_gasto+", id_plan_cuenta="+id_plan_cuenta+", num_anios="+num_anios+
                ", porcentaje="+porcentaje+" WHERE id_tabla_depreciacion="+id+";");
    }
}