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
import jm.web.Fecha;

/**
 *
 * @author Jorge
 */
public class OrdenTrabajo extends DataBase{
    public OrdenTrabajo(String m, int p, String db, String u, String c){
        super(m, p, db, u, c);
    }
    public ResultSet getOrdenTrabajo(String id)
    {
        return this.consulta("SELECT * FROM vta_orden_trabajo where id_orden_trabajo="+id);
    }
    public ResultSet getOrdenTrabajoInfra(String id)
    {
        return this.consulta("SELECT * FROM vta_orden_trabajo_infra where id_orden_trabajo="+id);
    }
    public ResultSet getOrdenesTrabajo(String id)
    {
        return this.consulta("SELECT * FROM vta_orden_trabajo where id_hoja_ruta="+id+" order by prioridad desc");
    }
    public String getOrdenesTrabajoPrefactura(int id)
    {
        String r = "No debe";
        try{
            ResultSet rs = this.consulta("SELECT 'Debe el mes de: '||get_mes(periodo)||' Subtotal: '||subtotal||' Iva: '||iva_2||' Total: '||total FROM tbl_prefactura where id_instalacion="+id+" and fecha_emision is null");
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
    public String getNumOrden(int idSuc)
    {
        String r = "1";
        try{
            ResultSet rs = this.consulta("select case when max(num_orden)>0 then max(num_orden)+1 else 1 end from tbl_orden_trabajo where id_sucursal="+idSuc);
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
    public String insertar(String id_instalacion, int id_sucursal_sesion, String num_orden, String tipo_trabajo,
            String usuario_reporte, String fecha_cliente, String hora_cliente, String diagnostico_tecnico)
    {
        String usuario_cliente = "";
        if(fecha_cliente.compareTo("")!=0){
            usuario_cliente = usuario_reporte;
        }
        fecha_cliente = fecha_cliente.compareTo("")!=0 ? "'" + fecha_cliente + "'" : "NULL";
        hora_cliente = hora_cliente.compareTo("")!=0 ? "'" + hora_cliente + "'" : "NULL";
        return this.insert("INSERT INTO tbl_orden_trabajo(id_instalacion, id_sucursal, num_orden, tipo_trabajo, usuario_reporte, fecha_reporte, hora_reporte, usuario_cliente, fecha_cliente, hora_cliente, diagnostico_tecnico, estado, tipo) "
                + "VALUES("+id_instalacion+", "+id_sucursal_sesion+", "+num_orden+", '"+tipo_trabajo+"', '"+usuario_reporte+"', now()::date, now()::time, '"+usuario_cliente+"', "+fecha_cliente+", "+hora_cliente+", '"+diagnostico_tecnico+"', '1', 'c');");
        /*if(pk.compareTo("-1")!=0){
            this.ejecutar("update tbl_instalacion set estado_servicio='c' where id_instalacion="+id_instalacion);
        }
        return pk;*/
    }
    public boolean actualizar(String id, String tipo_trabajo, String usuario_cliente, String fecha_cliente, String hora_cliente,
            String diagnostico_tecnico, String estado, String cambio_estado)
    {
        fecha_cliente = fecha_cliente.compareTo("")!=0 ? "'" + fecha_cliente + "'" : "NULL";
        hora_cliente = hora_cliente.compareTo("")!=0 ? "'" + hora_cliente + "'" : "NULL";
        return this.ejecutar("UPDATE tbl_orden_trabajo SET tipo_trabajo='"+tipo_trabajo+"', usuario_cliente='"+usuario_cliente+"', fecha_cliente="+fecha_cliente+
                ", hora_cliente="+hora_cliente+", diagnostico_tecnico='"+diagnostico_tecnico+"', estado='"+estado+"', cambio_estado='' WHERE id_orden_trabajo="+id);
    }
    public boolean setEstadoOrden(String ids_ordenes, String estado, String usuario, String motivo)
    {
        String set = "";
        if(estado.compareTo("1")==0){
            set = ", id_hoja_ruta=null, motivo_pendiente='"+motivo+"'";
        }
        if(estado.compareTo("3")==0){
            set = ", usuario_pendiente='"+usuario+"', fecha_pendiente=now(), hora_pendiente=now(), id_hoja_ruta=null, motivo_pendiente='"+motivo+"'";
        }
        return this.ejecutar("UPDATE tbl_orden_trabajo SET estado='"+estado+"' "+set+" WHERE id_orden_trabajo="+ids_ordenes);
    }
    public boolean setEstadoOrdenes(String ids_ordenes, String estado)
    {
        return this.ejecutar("UPDATE tbl_orden_trabajo SET estado='"+estado+"' WHERE id_orden_trabajo in ("+ids_ordenes+")");
    }
    public String tareas(String usuario, String id_tecnico_resp, String observacion, String fecha_realizacion, String hora_realizacion, String ids_ordenes)
    {
        String id_hoja_ruta = this.insert("insert into tbl_hoja_ruta(usuario, id_tecnico_resp, observacion) values('"+usuario+"', "+id_tecnico_resp+", '"+observacion+"')");
        if(id_hoja_ruta.compareTo("-1")!=0){
            List sql = new ArrayList();
            String vec_ids_ordenes[] = ids_ordenes.split(",");
            String usuario_realizacion = ""; // sale del tecnico responsable
            try{
                ResultSet rs = this.consulta("select alias from tbl_empleado where id_empleado="+id_tecnico_resp);
                if(rs.next()){
                    usuario_realizacion = rs.getString("alias")!=null ? rs.getString("alias") : "";
                    rs.close();
                }
            }catch(Exception e){
                e.printStackTrace();
            }
            for(int i=0; i<vec_ids_ordenes.length; i++){
                sql.add("UPDATE tbl_orden_trabajo SET usuario_realizacion='"+usuario_realizacion+"', fecha_realizacion='"+fecha_realizacion+
                    "', hora_realizacion='"+hora_realizacion+"', id_empleado="+id_tecnico_resp+", id_hoja_ruta="+id_hoja_ruta+" WHERE id_orden_trabajo="+vec_ids_ordenes[i]);
            }
            if(!this.transacciones(sql)){
                this.ejecutar("delete from tbl_hoja_ruta where id_hoja_ruta="+id_hoja_ruta);
                id_hoja_ruta = "-1";
            }
        }
        return id_hoja_ruta;
    }
    public boolean impresion(String id)
    {
        return this.ejecutar("UPDATE tbl_orden_trabajo SET impresion=impresion+1 WHERE id_orden_trabajo="+id);
    }
    public boolean impresiones(String id)
    {
        return this.ejecutar("UPDATE tbl_orden_trabajo SET impresion=impresion+1 WHERE id_hoja_ruta="+id);
    }
    public String concatenarValores(String id_materiales, String cantidades)
    {
        String param = "";
        String vecMat [] = id_materiales.split(",");
        String vecCan [] = cantidades.split(",");
        for(int i=0; i<vecMat.length; i++){
            param += "['"+vecMat[i]+"','"+vecCan[i]+"'],";
        }
        param = param.substring(0, param.length()-1);
        return "array["+param+"]";
    }
    public boolean noSolucionar(String id_ordenes)
    {
        return this.ejecutar("update tbl_orden_trabajo set id_empleado=null, id_hoja_ruta=null, usuario_realizacion=null, fecha_realizacion=null, hora_realizacion=null, estado='1' where id_orden_trabajo in ("+id_ordenes+")");
    }
    public boolean solucionar(int id_sucursal, String id, String usuario_solucion, String fecha_solucion, String hora_solucion,
            String solucionado, String conformidad, String atencion, String recomendacion, String materiales, String cantidades, String set_deviceclave)
    {
        /*List sql = new ArrayList();
        sql.add("UPDATE tbl_orden_trabajo SET usuario_solucion='"+usuario_solucion+"', fecha_solucion='"+fecha_solucion+"', hora_solucion='"+
                hora_solucion+"', solucionado="+solucionado+", conformidad='"+conformidad+"', atencion='"+atencion+"', recomendacion='"+recomendacion+"'"
                + ", estado='3' WHERE id_orden_trabajo="+id);
        return this.transacciones(sql);*/
        boolean ok = false;
        try{
            String paramMateriales = this.concatenarValores(materiales, cantidades);
            ResultSet res = this.consulta("select proc_orden_trabajo_solucion("+id_sucursal+", "+id+", '"+usuario_solucion+"', '"+fecha_solucion+"', '"+hora_solucion+"', "+solucionado+
                    ", '"+conformidad+"', '"+atencion+"', '"+recomendacion+"', "+paramMateriales+", "+set_deviceclave+")");
            if(res.next()){
                ok = (res.getString(1)!=null) ? res.getBoolean(1) : false;
                res.close();
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return ok;
    }
    /*   temporalmente compatibilidad con version anterior   */
    public boolean solucionar(String id, String id_instalacion, String fecha_solucion, String materiales, String recomendacion)
    {
        fecha_solucion = fecha_solucion.compareTo("")!=0 ? fecha_solucion : Fecha.getFecha("ISO");
        List sql = new ArrayList();
        sql.add("UPDATE tbl_orden_trabajo SET fecha_solucion='"+fecha_solucion+"', materiales='"+materiales+"', recomendacion='"+recomendacion+"'"
                + ", estado='9' WHERE id_orden_trabajo="+id);
        //sql.add("update tbl_instalacion set estado_servicio='a' where id_instalacion="+id_instalacion);
        return this.transacciones(sql);
    }

    /*  tipos de ordenes de trabajo */

    public ResultSet getTipos()
    {
        return this.consulta("SELECT id_orden_trabajo_tipo, orden_trabajo FROM tbl_orden_trabajo_tipo order by prioridad");
    }
    public ResultSet getTipos(String fecha_instalacion)
    {
        if(fecha_instalacion.compareTo("")==0){
            return this.consulta("SELECT id_orden_trabajo_tipo, orden_trabajo FROM tbl_orden_trabajo_tipo where lower(orden_trabajo) like 'instalac%'");
        }
        return this.consulta("SELECT id_orden_trabajo_tipo, orden_trabajo FROM tbl_orden_trabajo_tipo order by prioridad");
    }
    
    public ResultSet getTiposTareas(int id_padre, int id_sucursal)
    {
        return this.consulta("SELECT * FROM vta_tarea where id_tarea_padre="+id_padre+" and id_sucursal in (0, "+id_sucursal+") order by " + (id_padre == 1 ? "id_tarea" : "tarea") );
    }
    
    public ResultSet getTiposTareas(int id_padre, int id_sucursal, String id_nodo)
    {
        return this.consulta("SELECT * FROM vta_tarea where id_tarea_padre="+id_padre+" and id_sucursal in (0, "+id_sucursal+") and id_nodo in (0, "+id_nodo+") order by " + (id_padre == 1 ? "id_tarea" : "tarea") );
    }
    
    public ResultSet getContTiposTareas()
    {
        return this.consulta("SELECT id_tarea_padre, count(id_tarea_padre) FROM vta_tarea where id_tarea_padre>0 group by id_tarea_padre order by id_tarea_padre");
    }
    
    public ResultSet getTareasAsignadas(String id)
    {
        return this.consulta("SELECT * FROM tbl_orden_trabajo_tarea where id_orden_trabajo="+id+" order by id_tarea");
    }
    
    public ResultSet getTareasOrden(String id)
    {
        return this.consulta("SELECT O.*, T.tarea FROM (tbl_orden_trabajo_tarea as O inner join tbl_tarea as T on T.id_tarea=O.id_tarea) "
                + "where O.id_orden_trabajo="+id+" order by T.id_tarea");
    }
    
    public String insertarInfraestructura(int id_sucursal_sesion, String num_orden, String id_nodo,
            String usuario_reporte, String fecha_cliente, String hora_cliente, String diagnostico_tecnico, String id_empleado, String ids_tareas)
    {
        String usuario_cliente = "";
        if(fecha_cliente.compareTo("")!=0){
            usuario_cliente = usuario_reporte;
        }
        fecha_cliente = fecha_cliente.compareTo("")!=0 ? "'" + fecha_cliente + "'" : "NULL";
        hora_cliente = hora_cliente.compareTo("")!=0 ? "'" + hora_cliente + "'" : "NULL";
        String pk = this.insert("INSERT INTO tbl_orden_trabajo(id_instalacion, id_sucursal, num_orden, id_nodo, tipo_trabajo, usuario_reporte, fecha_reporte, hora_reporte, usuario_cliente, fecha_cliente, hora_cliente, diagnostico_tecnico, id_empleado, estado, tipo) "
                + "VALUES(null, "+id_sucursal_sesion+", "+num_orden+", "+id_nodo+",'9', '"+usuario_reporte+"', now()::date, now()::time, '"+usuario_cliente+"', "+fecha_cliente+", "+hora_cliente+", '"+diagnostico_tecnico+"', "+id_empleado+", '1', 'i');");
        if(pk.compareTo("-1")!=0){
            String tarea[] = ids_tareas.split(",");
            for(int i=0; i<tarea.length; i++){
                this.ejecutar("insert into tbl_orden_trabajo_tarea(id_orden_trabajo, id_tarea) values("+pk+", "+tarea[i]+")");
            }
        }
        return pk;
    }
    
    public boolean actualizarInfraestructura(String id_orden_trabajo, String id_nodo, String fecha_cliente, String hora_cliente, String diagnostico_tecnico, String id_empleado, String ids_tareas)
    {
        fecha_cliente = fecha_cliente.compareTo("")!=0 ? "'" + fecha_cliente + "'" : "NULL";
        hora_cliente = hora_cliente.compareTo("")!=0 ? "'" + hora_cliente + "'" : "NULL";
        List sql = new ArrayList();
        sql.add("update tbl_orden_trabajo set id_nodo="+id_nodo+", fecha_cliente="+fecha_cliente+", hora_cliente="+hora_cliente+
                ", diagnostico_tecnico='"+diagnostico_tecnico+"', id_empleado="+id_empleado+" where id_orden_trabajo="+id_orden_trabajo);
        
        if(ids_tareas.compareTo("")!=0){
            sql.add("delete from tbl_orden_trabajo_tarea where id_orden_trabajo="+id_orden_trabajo);
            String tarea[] = ids_tareas.split(",");
            for(int i=0; i<tarea.length; i++){
                sql.add("insert into tbl_orden_trabajo_tarea(id_orden_trabajo, id_tarea) values("+id_orden_trabajo+", "+tarea[i]+")");
            }
        }
        return this.transacciones(sql);
    }
    
    public String concatenarValores(String id_tareas, String solucionados, String observaciones, String macs_nuevas, String macs_retiradas)
    {
        String param = "";
        String vecCods [] = id_tareas.split(";");
        String vecSol [] = solucionados.split(";");
        String vecObs [] = observaciones.split(";");
        String vecMacN [] = macs_nuevas.split(";");
        String vecMacR [] = macs_retiradas.split(";");
        for(int i=0; i<vecCods.length; i++){
            param += "['"+vecCods[i]+"','"+vecSol[i]+"','"+vecObs[i]+"','"+vecMacN[i]+"','"+vecMacR[i]+"'],";
        }
        param = param.substring(0, param.length()-1);
        return "array["+param+"]";
    }
    
    public String solucionarInfraestructura(String id_orden_trabajo, String usuario_solucion, String fecha_solucion, 
            String id_tareas, String solucionados, String observaciones, String macs_nuevas, String macs_retiradas)
    {
        String res = "";
        try{
            String macs = this.concatenarValores(id_tareas, solucionados, observaciones, macs_nuevas, macs_retiradas);
            ResultSet rs = this.consulta("select proc_solucionarOrdenTrabajoInfra("+id_orden_trabajo+
                    ", '"+usuario_solucion+"', '"+fecha_solucion+"', "+macs+")");
            if(rs.next()){
                res = rs.getString(1)!=null ? rs.getString(1) : "";
                rs.close();
            }
        }catch(Exception e){
            e.printStackTrace();
            res = e.getMessage();
        }
        return res;
    }
    
}