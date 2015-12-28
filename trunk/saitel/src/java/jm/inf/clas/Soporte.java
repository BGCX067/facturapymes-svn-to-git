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
public class Soporte extends DataBase{
    public Soporte(String m, int p, String db, String u, String c){
        super(m, p, db, u, c);
    }
    public ResultSet getSoporte(String id)
    {
        return this.consulta("SELECT * FROM vta_soporte where id_soporte="+id+";");
    }

    public String getNumSoporte(int idSuc)
    {
        String r = "1";
        try{
            ResultSet rs = this.consulta("select case when max(num_soporte)>0 then max(num_soporte)+1 else 1 end from tbl_soporte where id_sucursal="+idSuc);
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

    public String insertar(String id_instalacion, int id_sucursal, String num_soporte, String quien_llama,
            String telefono_llama, String alias_contesta, String problema, String diagnostico)
    {
        String pk = this.insert("INSERT INTO tbl_soporte(id_instalacion, id_sucursal, num_soporte, quien_llama, telefono_llama, "
                    + "alias_contesta, problema, diagnostico, fecha_llamada, hora_llamada) "
                + "VALUES("+id_instalacion+", "+id_sucursal+", "+num_soporte+", '"+quien_llama+"', '"+telefono_llama+
                "', '"+alias_contesta+"', '"+problema+"', '"+diagnostico+"', now()::date, now()::time);");
        /*if(pk.compareTo("-1")!=0){
            this.ejecutar("update tbl_instalacion set estado_servicio='c' where id_instalacion="+id_instalacion);
        }*/
        return pk;
    }
    public boolean actualizar(String id_soporte, String problema, String diagnostico)
    {
        return this.ejecutar("UPDATE tbl_soporte SET problema='"+problema+"', diagnostico='"+diagnostico+"' WHERE id_soporte="+id_soporte);
    }
    
    public boolean solucionar(String id, String id_instalacion, String alias_solucion, String recomendacion)
    {
        List sql = new ArrayList();
        sql.add("UPDATE tbl_soporte SET fecha_solucion=now()::date, hora_solucion=now()::time, alias_solucion='"+alias_solucion+
                "', recomendacion='"+recomendacion+"', estado='s' WHERE id_soporte="+id+";");
        //sql.add("update tbl_instalacion set estado_servicio='a' where id_instalacion="+id_instalacion);
        return this.transacciones(sql);
    }

}