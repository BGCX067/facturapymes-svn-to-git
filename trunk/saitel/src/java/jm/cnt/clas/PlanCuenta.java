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

package jm.cnt.clas;
import java.sql.ResultSet;
import jm.web.DataBase;

public class PlanCuenta extends DataBase{
    public PlanCuenta(String m, int p, String db, String u, String c){
        super(m, p, db, u, c);
    }

    public ResultSet getTablasAsignacion()
    {
        return this.consulta("SELECT id_plan_cuenta,nombre_cuenta,codigo_cuenta FROM tbl_plan_cuenta where id_plan_cuenta not in (select distinct id_padre from tbl_plan_cuenta) order by codigo_cuenta,nombre_cuenta;");
    }

    public ResultSet getTablas()
    {
        return this.consulta("SELECT id_plan_cuenta,codigo_cuenta,nombre_cuenta FROM tbl_plan_cuenta WHERE eliminado=false order by codigo_cuenta;");
    }

    public ResultSet getTablas(String grupo)
    {
        String cuentas = "";
        String vector[] = grupo.split(",");
        for(int i=0; i<vector.length; i++){
            if(i==0){
                cuentas += " and (codigo_cuenta like '"+vector[i]+"%'";
            }
            cuentas += " or codigo_cuenta like '"+vector[i]+"%'";
            if(i==vector.length-1){
                cuentas += ")";
            }
        }
        return this.consulta("SELECT id_plan_cuenta,codigo_cuenta,nombre_cuenta FROM tbl_plan_cuenta where id_plan_cuenta not in (select distinct id_padre from tbl_plan_cuenta)"+cuentas+" order by codigo_cuenta;");
    }

    public ResultSet getTablasCajaBancos()
    {
        return this.consulta("SELECT id_plan_cuenta,codigo_cuenta,nombre_cuenta FROM tbl_plan_cuenta where id_plan_cuenta not in (select distinct id_padre from tbl_plan_cuenta) and (lower(nombre_cuenta) like '%caja%' or lower(nombre_cuenta) like '%banco%' or lower(nombre_cuenta) like '%cheque%') order by codigo_cuenta;");
    }

    public ResultSet getTablasCajaBancosNotas(int grupo)
    {
        return this.consulta("SELECT id_plan_cuenta,codigo_cuenta,nombre_cuenta FROM tbl_plan_cuenta " +
                "where id_plan_cuenta not in (select distinct id_padre from tbl_plan_cuenta) " +
                "and ((lower(nombre_cuenta) like '%caja%' or lower(nombre_cuenta) like '%banco%') " +
                "or (codigo_cuenta like '"+grupo+"%' and (lower(nombre_cuenta) like '%descuento%' or lower(nombre_cuenta) like '%devoluciÃ³n%'))) " +
                "order by codigo_cuenta;");
    }

    public ResultSet getTablasVista()
    {
        return this.consulta("SELECT * FROM vta_plan_cuenta where id_plan_cuenta>1;");
    }

    public ResultSet getTabla(String id)
    {
        return this.consulta("SELECT * FROM tbl_plan_cuenta where id_plan_cuenta="+id+";");
    }
    public boolean estaDuplicado(String id, String cod, String nom)
    {
        ResultSet res = this.consulta("SELECT * FROM tbl_plan_cuenta where (codigo_cuenta='"+cod+"' or nombre_cuenta='"+nom+"') and id_plan_cuenta<>"+id+";");
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
    public boolean insertar(String idP, String cod, String nom, String tipo, String campo_form_103, String campo_form_104)
    {
        return this.ejecutar("INSERT INTO tbl_plan_cuenta(id_padre,codigo_cuenta,nombre_cuenta,tipo_cuenta,fecha_creacion,fecha_modificacion,campo_form_103,campo_form_104) " +
                "VALUES("+idP+", '"+cod+"', '"+nom+"', "+tipo+", now(), now(), '"+campo_form_103+"', '"+campo_form_104+"');");
    }
    public boolean actualizar(String id, String idP, String cod, String nom, String tipo, String campo_form_103, String campo_form_104)
    {
        return this.ejecutar("UPDATE tbl_plan_cuenta SET id_padre="+idP+", codigo_cuenta='"+cod+"', nombre_cuenta='"+nom+"', tipo_cuenta="+tipo+", fecha_modificacion=now(), " +
                "campo_form_103='"+campo_form_103+"', campo_form_104='"+campo_form_104+"' WHERE id_plan_cuenta="+id+";");
    }
}