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

package jm.nom.clas;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import jm.web.DataBase;
import jm.web.Fecha;

/**
 *
 * @author Jorge
 */
public class permisosLicencias extends DataBase{
    public permisosLicencias(String m, int p, String db, String u, String c){
        super(m, p, db, u, c);
    }
    public ResultSet getPermisos(String id)
    {
        return this.consulta("SELECT * FROM vta_empleado_permiso where id_permiso="+id+";");
    }
    public ResultSet getPermisosDeta(String id)
    {
        return this.consulta("SELECT * FROM vta_empleado_permiso where id_empleado="+id+";");
    }
    public ResultSet getEmpleadoDetalles(String id)
    {
        return this.consulta("SELECT *,(select email from tbl_empleado e where id_cargo=4 and e.id_sucursal=em.id_sucursal) as emailJefe,"
                + "(select nombre||' '||apellido from tbl_empleado e where id_cargo=4 and e.id_sucursal=em.id_sucursal) as nombrejefe FROM tbl_empleado em where id_empleado="+id+";");
    }
    public ResultSet getPermisosDetalles(String id)
    {
        return this.consulta("SELECT * FROM tab_diasrecuperados where id_permiso="+id+";");
    }
    public ResultSet getVacaciones(String id)
    {
        return this.consulta("SELECT * FROM vta_empleado_vacaciones where id_empleado_vacaciones="+id+" order by fecha_pedido;");
    }
    public ResultSet getVacacionesDetalles(String id)
    {
        return this.consulta("SELECT * FROM vta_empleado_vacaciones where id_empleado="+id+" order by fecha_pedido;");
    }
    public ResultSet getTipoPermiso(String estado)
    {
        return this.consulta("SELECT id_tipo, nombre, descripcion FROM tab_tipopermiso where eliminado="+estado+";");
    }
    public ResultSet getTiposPermisos(String id)
    {
        return this.consulta("SELECT * FROM tab_tipopermiso where id_tipo="+id+";");
    }
    public ResultSet getDetallesPermisos(String id)
    {
        return this.consulta("select e.id_empleado, e.nombre, e.fecha_ingreso, e.diasvacaciones,\n" +
        " e.fecha_ingreso, date_part('year', now()) - date_part('year', e.fecha_ingreso) as num_anios,\n" +
        "(DATE_PART('month', now()) - DATE_PART('month', e.fecha_ingreso))+(date_part('year', now()) - date_part('year', e.fecha_ingreso))*12::int as meses,\n" +
        "DATE_PART('day', now() - e.fecha_ingreso) as dias,\n" +
        "(select sum(numdias)from vta_empleado_permiso where id_empleado=e.id_empleado and permisoestado='a' and forma_rec='1') as diaspedidos,\n" +
        "(select sum(numhoras) from vta_empleado_permiso where id_empleado=e.id_empleado and permisoestado='a' and forma_rec='1') as horaspedidos,\n" +
        "(select sum(num_minutos) from vta_empleado_permiso where id_empleado=e.id_empleado and permisoestado='a' and forma_rec='1') as minutospedidos,\n"+       
        "(select sum(numdias) from tbl_empleado_vacaciones where estado='a' and id_empleado=e.id_empleado) as vacaciones\n" +
        "from tbl_empleado e where e.id_empleado="+id+";");
    }
    public String getMesesVacaciones(String id)
    {
        String num_meses = "";
        try{
            ResultSet r = this.consulta("select c.sucursal, e.nombre || ' ' || e.apellido as empleado, e.fecha_ingreso, date_part('year', now()) - date_part('year', e.fecha_ingreso) as num_anios,\n" +
                                                "(DATE_PART('month', now()) - DATE_PART('month', e.fecha_ingreso))+(date_part('year', now()) - date_part('year', e.fecha_ingreso))*12::int as meses,\n" +
                                                "DATE_PART('day', now() - e.fecha_ingreso) as di\n" +
                                                "from vta_empleado e\n" +
                                                "JOIN vta_sucursal c ON c.id_sucursal = e.id_sucursal \n" +
                                                "where id_empleado="+id+"");
            if(r.next()){
                num_meses = (r.getString("meses")!=null) ? r.getString("meses") : "";
                r.close();
            }

        }catch(Exception e){
            e.printStackTrace();
        }
        return num_meses;
    }

    public String getDiasVacacionesId(String id)
    {
        String num_anios = "";
        try{
            ResultSet r = this.consulta("select c.sucursal, e.nombre || ' ' || e.apellido as empleado, e.fecha_ingreso, date_part('year', now()) - date_part('year', e.fecha_ingreso) as num_anios,\n" +
                                                "(DATE_PART('month', now()) - DATE_PART('month', e.fecha_ingreso))+(date_part('year', now()) - date_part('year', e.fecha_ingreso))*12::int as meses,\n" +
                                                "DATE_PART('day', now() - e.fecha_ingreso) as di\n" +
                                                "from vta_empleado e\n" +
                                                "JOIN vta_sucursal c ON c.id_sucursal = e.id_sucursal \n" +
                                                "where id_empleado="+id+"");
            if(r.next()){
                num_anios = (r.getString("num_anios")!=null) ? r.getString("num_anios") : "";
                r.close();
            }
            if(Integer.parseInt(num_anios)>4){
                num_anios=""+((Integer.parseInt(num_anios)-4)+15);
            }
            else{
                num_anios="15";
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return num_anios;
    }
    public String getMaxSolicitud()
    {
        String num = "";
        try{
            ResultSet r = this.consulta("select max(num_solicitud) as num from tbl_empleado_vacaciones");
            if(r.next()){
                num = (r.getString("num")!=null) ? r.getString("num") : "";
                r.close();
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return num;
    }
    public String insertar(String id_tipo, String nDias, String nHoras, String nMinutos, String fecha_permiso, String recuperac, String hInicial, String descripcion, String usuario)
    {
        id_tipo = id_tipo.compareTo("-0")!=0 ? id_tipo : "NULL";
        nDias = id_tipo.compareTo("NULL")==0 ? nDias : id_tipo;
        String id_empleado=getId(usuario);
        return this.insert("INSERT INTO tbl_permiso(id_tipo, id_empleado, numdias, numhoras, fechafin, fechainicio, aprobado, forma_rec, hora_inicio, hora_fin, "
            + "descripcion, estado, num_minutos, fecha_pedido) " +
            "VALUES("+id_tipo+","+id_empleado+", "+nDias+", "+nHoras+", (CAST('"+fecha_permiso+"' AS DATE) + CAST('"+nDias+" days' AS INTERVAL)), '"+fecha_permiso+"', false, "+recuperac+", '"+hInicial+"',"
                + "(cast( '"+hInicial+"' as time)+cast(' "+nHoras+" hour' as interval)+cast(' "+nMinutos+" minutes' as interval)), '"+descripcion+"', 's', '"+nMinutos+"', date 'now()');");
    }
    public boolean insertarVacaciones(String id_empleado, String fecha_permiso, String nDias)
    {
        String num_solicitud=getMaxSolicitud();
        num_solicitud=""+(Integer.parseInt(num_solicitud)+1);
        return this.ejecutar("INSERT INTO tbl_empleado_vacaciones(id_empleado, fecha_inicio, fecha_fin, numdias,estado,num_solicitud,fecha_pedido) " +
            "VALUES("+id_empleado+",'"+fecha_permiso+"', (CAST('"+fecha_permiso+"' AS DATE) + CAST('"+nDias+" days' AS INTERVAL)), "+nDias+",'s', "+num_solicitud+",date 'now()' );");
    }
    public boolean insertarTipoPermiso(String nombre, String descripcion, String numdias, String eliminado)
    {
        return this.ejecutar("INSERT INTO tab_tipopermiso(nombre, descripcion, numdias, eliminado) " +
            "VALUES('"+nombre+"','"+descripcion+"', "+numdias+", "+eliminado+");");
    }
    public boolean actualizarTipoPermiso(String id, String nombre, String descripcion, String numdias, String eliminado)
    {
        return this.ejecutar("Update tab_tipopermiso set nombre='"+nombre+"', descripcion='"+descripcion+"', numdias="+numdias+", eliminado="+eliminado+" where id_tipo="+id+";");
    }
    public boolean actualizarPermiso(String id, String estado, String descripcion)
    {
        return this.ejecutar("Update tbl_permiso set estado='"+estado+"', observaciones='"+descripcion+"' where id_permiso="+id+";");
    }
    public boolean actualizarVacaciones(String id, String estado, String descripcion, String Usuario)
    {
        return this.ejecutar("Update tbl_empleado_vacaciones set estado='"+estado+"', observacion='"+descripcion+"', usuario_respuesta='"+Usuario+"', fecha_respuesta=date 'now()' where id_empleado_vacaciones="+id+";");
    }
    public boolean insertarTipo(String id_permiso, String [][] matriz)
    {
        List ejec= new ArrayList();
        for(int i = 0; i < matriz.length; i++) {
            ejec.add("INSERT INTO tab_diasrecuperados(id_permiso, dia, hora_entrada, hora_salida) values ("+id_permiso+",'"+matriz[i][0]+"','"+matriz[i][1]+"','"+matriz[i][2]+"');");
        }        
        return this.transacciones(ejec);
    }
   public boolean eliminarPermiso(String id_permiso)
    {
        return this.ejecutar("Delete from tbl_permiso where id_permiso="+id_permiso+";");
    }
    public String getId(String alias)
    {
        String id_empleado = "";
        try{
            ResultSet r = this.consulta("SELECT * FROM tbl_empleado where alias='"+alias+"'");
            if(r.next()){
                id_empleado = (r.getString("id_empleado")!=null) ? r.getString("id_empleado") : "";
                r.close();
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return id_empleado;
    }
    public String getDiasTipoPermiso(String id)
    {
        String numdias = "";
        try{
            ResultSet r = this.consulta("SELECT * FROM tab_tipopermiso where id_tipo="+id+"");
            if(r.next()){
                numdias = (r.getString("numdias")!=null) ? r.getString("numdias") : "";
                r.close();
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return numdias;
    }

    public String getUsuario(String id)
    {
        String alias = "";
        try{
            ResultSet r = this.consulta("SELECT * FROM vta_empleado where id_empleado='"+id+"'");
            if(r.next()){
                alias = (r.getString("alias")!=null) ? r.getString("alias") : "";
                r.close();
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return alias;
    }

    public String getNombre(String usuario)
    {
        String empleado = "";
        try{
            ResultSet r = this.consulta("SELECT nombre || ' ' || apellido as empleado FROM tbl_empleado where alias='"+usuario+"';");
            if(r.next()){
                empleado = (r.getString("empleado")!=null) ? r.getString("empleado") : "";
                r.close();
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return empleado;
    }
    
}