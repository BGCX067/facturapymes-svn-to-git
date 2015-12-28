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
import javax.servlet.http.HttpServletRequest;
import jm.web.DataBase;
import jm.web.Fecha;

/**
 *
 * @author Jorge
 */
public class Cargo extends DataBase{
    public Cargo(String m, int p, String db, String u, String c){
        super(m, p, db, u, c);
    }
    public ResultSet getCargo(String id)
    {
        return this.consulta("SELECT C.*, E.sueldo FROM vta_cargo as C inner join tbl_escala_salarial as E on C.id_cargo=E.id_cargo "
                + "where vigencia_hasta is null and C.id_cargo="+id+";");
    }
    public ResultSet getCargos()
    {
        return this.consulta("SELECT id_cargo,cargo FROM tbl_cargo where eliminado=false order by cargo;");
    }
    
    public ResultSet getCargosxAreas()
    {
        return this.consulta("SELECT C.*, E.sueldo FROM vta_cargo as C inner join tbl_escala_salarial as E on C.id_cargo=E.id_cargo "
                + "where vigencia_hasta is null and eliminado=false order by area,cargo;");
    }

    public boolean estaDuplicado(String id, String dni)
    {
        ResultSet res = this.consulta("SELECT * FROM tbl_cargo where cargo='"+dni+"' and id_cargo<>"+id+";");
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
    
    public boolean insertar(String cargo, String id_area, String usuario, String sueldo)
    {
        String id_cargo = this.insert("insert into tbl_cargo(cargo, id_area) values('"+cargo+"', "+id_area+")");
        if(id_cargo.compareTo("-1")!=0){
            String fecha_ini = Fecha.getFecha("ISO");
            try{
            ResultSet rs = this.consulta("select distinct vigencia_desde from tbl_escala_salarial where vigencia_hasta is null");
            if(rs.next()){
                fecha_ini = rs.getString("vigencia_desde")!=null ? rs.getString("vigencia_desde") : fecha_ini;
                rs.close();
            }
            }catch(Exception e){
                e.printStackTrace();
            }
            return this.ejecutar("insert into tbl_escala_salarial(id_cargo, usuario, vigencia_desde, sueldo) "
                    + "values("+id_cargo+", '"+usuario+"', '"+fecha_ini+"', "+sueldo+");");
        }
        return false;
        
    }
    
    public boolean actualizar(String id_cargo, String cargo, String id_area, String sueldo)
    {
        List sql = new ArrayList();
        sql.add("update tbl_cargo set cargo='"+cargo+"', id_area="+id_area+" where id_cargo="+id_cargo+";");
        sql.add("update tbl_escala_salarial set sueldo="+sueldo+" where id_cargo="+id_cargo+" and vigencia_hasta is null;");
        return this.transacciones(sql);
        
    }
    
    public boolean setIncrementoSueldos(HttpServletRequest request, String usuario, String fecha_ini)
    {
        List sql = new ArrayList();
        int i=0;
        sql.add("update tbl_escala_salarial set vigencia_hasta=('"+fecha_ini+"'::date - '1 day'::interval)::date where vigencia_hasta is null;");
        while(request.getParameter("id_cargo"+i)!=null){
            String id_cargo = request.getParameter("id_cargo"+i);
            String sueldo = request.getParameter("sueldo"+i);
            sql.add("insert into tbl_escala_salarial(id_cargo, usuario, vigencia_desde, sueldo) "
                    + "values("+id_cargo+", '"+usuario+"', '"+fecha_ini+"', "+sueldo+");");
            i++;
        }
        
        return this.transacciones(sql);
    }
    
}