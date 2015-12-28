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
import jm.web.DataBase;
import jm.web.Fecha;

/**
 *
 * @author Jorge
 */
public class Area extends DataBase{
    public Area(String m, int p, String db, String u, String c){
        super(m, p, db, u, c);
    }
    public ResultSet getArea(String id)
    {
        return this.consulta("SELECT * FROM tbl_area where id_area="+id+";");
    }
    public ResultSet getAreas()
    {
        return this.consulta("SELECT id_area,area FROM tbl_area where eliminado=false order by area;");
    }

    public boolean estaDuplicado(String id, String area)
    {
        ResultSet res = this.consulta("SELECT * FROM tbl_area where area='"+area+"' and id_area<>"+id+";");
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
    public boolean insertar(String area)
    {
        return this.ejecutar("insert into tbl_area(area) values('"+area+"')");
    }
    
    public boolean actualizar(String id_area, String area)
    {
        return this.ejecutar("update tbl_area set area='"+area+"' where id_area="+id_area);
    }
    
}