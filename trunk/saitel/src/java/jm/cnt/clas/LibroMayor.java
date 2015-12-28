/**	
* @version 3.0
* @package YAKUSOFT.
* @author Jorge Washington Mueses Cevallos.	
* @copyright Copyright (C) 2010 por Jorge Mueses. Todos los derechos reservados.
* @license http://www.gnu.org/copyleft/gpl.html GNU/GPL.
* YAKUSOFT! es un software de libre distribución, que puede ser
* copiado y distribuido bajo los términos de la Licencia Pública
* General GNU, de acuerdo con la publicada por la Free Software
* Foundation, versión 2 de la licencia o cualquier versión posterior.
*/

package jm.cnt.clas;
import java.sql.ResultSet;
import jm.web.DataBase;

/**
 *
 * @author Jorge
 */
public class LibroMayor extends DataBase{
    public LibroMayor(String m, int p, String db, String u, String c){
        super(m, p, db, u, c);
    }
    public ResultSet getTransacciones(String id_sucursal, String id_plan_cuenta, String fi, String ff)
    {
        if(id_sucursal.compareTo("-0")!=0){
            return this.consulta("SELECT * FROM vta_libro_diario_mayor where id_sucursal="+id_sucursal+" and id_plan_cuenta="+id_plan_cuenta+" and fecha_asiento between '"+fi+"' and '"+ff+"';");
        }
        return this.consulta("SELECT * FROM vta_libro_diario_mayor where id_plan_cuenta="+id_plan_cuenta+" and fecha_asiento between '"+fi+"' and '"+ff+"';");
    }
    public boolean cuentaMovimiento(String id)
    {
        try{
            ResultSet res = this.consulta("SELECT tipo_cuenta FROM tbl_plan_cuenta where id_plan_cuenta="+id);
            if(res.next()){
                int tipo = res.getString("tipo_cuenta")!=null ? res.getInt("tipo_cuenta") : -1;
                res.close();
                if(tipo>=0){
                    return true;
                }
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return false;
    }
}