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
import java.util.ArrayList;
import java.util.List;
import jm.web.DataBase;

/**
 *
 * @author Jorge
 */
public class HojaRuta extends DataBase{
    public HojaRuta(String m, int p, String db, String u, String c){
        super(m, p, db, u, c);
    }
    public ResultSet getHojaRuta(String id)
    {
        return this.consulta("SELECT * FROM vta_hoja_ruta where id_hoja_ruta="+id);
    }

    public ResultSet getOrdenes(String id)
    {
        return this.consulta("SELECT * FROM vta_orden_trabajo where id_hoja_ruta="+id);
    }
    public ResultSet getOrdenes(int id_sucursal, String estado)
    {
        String where = "where id_sucursal="+id_sucursal;
        if(estado.compareTo("")!=0){
            where += " and estado in (" + estado + ")";
        }
        return this.consulta("SELECT * FROM vta_orden_trabajo " + where);
    }
    public ResultSet getOrdenesSolucionar(String id, String estado)
    {
        return this.consulta("SELECT * FROM vta_orden_trabajo where id_hoja_ruta="+id+" and estado in ('"+estado+"') order by prioridad desc");
    }
    public ResultSet getOrdenInstalacion(String id)
    {
        return this.consulta("SELECT O.*, I.id_sector, I.ip, I.id_plan_establecido, I.direccion_instalacion, I.mac, I.macp, I.macg, I.receptor, I.porcentaje_senal, I.antena_acoplada, I.latitud, I.longitud, I.altura, I.altura_antena, I.fecha_instalacion, I.estado_instalacion "
                + "FROM vta_orden_trabajo as O inner join tbl_instalacion as I on O.id_instalacion=I.id_instalacion where O.id_hoja_ruta="+id+" order by estado, prioridad, sector, fecha_reporte");
    }

    public ResultSet getOrdenesPendientes(int id_sucursal, String id)
    {
        return this.consulta("SELECT * FROM vta_orden_trabajo where (id_hoja_ruta is null or id_hoja_ruta<>"+id+") and id_sucursal="+id_sucursal+" and estado in ('1', '3')");
    }

    public ResultSet getOrdenesPendientes(int id_sucursal, String id, String where)
    {
        return this.consulta("SELECT * FROM vta_orden_trabajo where (id_hoja_ruta is null or id_hoja_ruta<>"+id+") and id_sucursal="+id_sucursal+" and estado in ('1', '3') "+where);
    }

    public ResultSet getFechasOrdenesPendientes(int id_sucursal, String id)
    {
        return this.consulta("SELECT distinct fecha_reporte, todatesql(fecha_reporte) FROM vta_orden_trabajo where (id_hoja_ruta is null or id_hoja_ruta<>"+id+") and id_sucursal="+id_sucursal+" and estado in ('1', '3') order by fecha_reporte");
    }

    public String getNumHojaRuta(int idSuc)
    {
        String r = "1";
        try{
            ResultSet rs = this.consulta("select case when max(num_hoja_ruta)>0 then max(num_hoja_ruta)+1 else 1 end from tbl_hoja_ruta where id_sucursal="+idSuc);
            if(rs.next()){
                r = rs.getString(1)!=null ? rs.getString(1) : "1";
                rs.close();
            }
            rs.close();
        }catch(Exception e){
            e.printStackTrace();
        }
        return r;
    }

    public String insertar(int id_sucursal, String num_hoja_ruta, String usuario, String id_tecnico_resp, String observacion, String id_ordenes_trabajo)
    {
        String id = this.insert("insert into tbl_hoja_ruta(id_sucursal, num_hoja_ruta, usuario, fecha, hora, id_tecnico_resp, observacion, estado_hoja) "
                + "values("+id_sucursal+", "+num_hoja_ruta+", '"+usuario+"', now(), now(), "+id_tecnico_resp+", '"+observacion+"', 1)");
        if(id.compareTo("-1")!=0){
            String usuario_tectico = "";
            try{
                ResultSet rs = this.consulta("select alias from tbl_empleado where id_empleado="+id_tecnico_resp);
                if(rs.next()){
                    usuario_tectico = rs.getString("alias")!=null ? rs.getString("alias") : "";
                    rs.close();
                }
            }catch(Exception e){
                e.printStackTrace();
            }

            String vec_id[] = id_ordenes_trabajo.split(",");
            List sql = new ArrayList();
            for(int i=0; i<vec_id.length; i++){
                sql.add("update tbl_orden_trabajo set id_empleado="+id_tecnico_resp+", id_hoja_ruta="+id+", usuario_realizacion='"+usuario_tectico+"', fecha_realizacion=now(), hora_realizacion=now(), estado='2' where id_orden_trabajo="+vec_id[i]);
            }
            if(!this.transacciones(sql)){
                this.ejecutar("delete from tbl_hoja_ruta where id_hoja_ruta="+id);
                id="-1";
            }
        }
        return id;
    }

    public boolean actualizar(String id_hoja_ruta, String id_tecnico_resp, String observacion, String id_ordenes_trabajo)
    {
        if(this.ejecutar("update tbl_hoja_ruta set id_tecnico_resp="+id_tecnico_resp+", observacion='"+observacion+"' where id_hoja_ruta="+id_hoja_ruta)){
            String usuario_tectico = "";
            try{
                ResultSet rs = this.consulta("select alias from tbl_empleado where id_empleado="+id_tecnico_resp);
                if(rs.next()){
                    usuario_tectico = rs.getString("alias")!=null ? rs.getString("alias") : "";
                    rs.close();
                }
            }catch(Exception e){
                e.printStackTrace();
            }
            
            String vec_id[] = id_ordenes_trabajo.split(",");
            List sql = new ArrayList();
            sql.add("update tbl_orden_trabajo set id_empleado=null, id_hoja_ruta=null, usuario_realizacion=null, fecha_realizacion=null, hora_realizacion=null, estado='1' where id_hoja_ruta="+id_hoja_ruta);
            for(int i=0; i<vec_id.length; i++){
                sql.add("update tbl_orden_trabajo set id_empleado="+id_tecnico_resp+", id_hoja_ruta="+id_hoja_ruta+", usuario_realizacion='"+usuario_tectico+"', fecha_realizacion=now(), hora_realizacion=now(), estado='2' where id_orden_trabajo="+vec_id[i]);
            }
            this.transacciones(sql);
            return true;
        }
        return false;
    }

    public boolean revision(String id_hoja_ruta, String usuario)
    {
        return this.ejecutar("update tbl_hoja_ruta set estado_hoja='2', fecha_revision=now(), hora_revision=now(), "
                + "usuario_revision='"+usuario+"' where id_hoja_ruta="+id_hoja_ruta);
    }

    public boolean solucionar(String id_hoja_ruta, String usuario)
    {
        return this.ejecutar("update tbl_hoja_ruta set estado_hoja='9', fecha_cierre=now(), hora_cierre=now(), "
                + "usuario_cierre='"+usuario+"' where id_hoja_ruta="+id_hoja_ruta);
    }

}