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

/**
 *
 * @author Jorge
 */
public class LibroDiario extends DataBase{
    public LibroDiario(String m, int p, String db, String u, String c){
        super(m, p, db, u, c);
    }
    public int getNumPags(String sql, int fxp)
    {
        int numPags = 0;
        try{
            ResultSet rPag = this.consulta(sql);
            if(rPag.next()){
                numPags = ( (rPag.getString(1)!=null?rPag.getInt(1):1) - 1 )/fxp;
                rPag.close();
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return numPags;
    }
    public ResultSet getTransacciones(String fi, String ff, long p, int fxp)
    {
        return this.consulta("SELECT * FROM vta_libro_diario_mayor where fecha_asiento between '"+fi+"' and '"+ff+"' LIMIT "+fxp+" OFFSET "+(fxp*p)+";");
    }
    public ResultSet getTransacciones(String id_sucursal, String fi, String ff, long p, int fxp)
    {
        return this.consulta("SELECT * FROM vta_libro_diario_mayor where id_sucursal="+id_sucursal+" and fecha_asiento between '"+fi+"' and '"+ff+"' LIMIT "+fxp+" OFFSET "+(fxp*p)+";");
    }
    public boolean reversarAsiento(String num_asiento, String tabla, String campo, String clave)
    {
        boolean res = false;
        try{
            ResultSet ok = this.consulta("select proc_reversarAsiento("+num_asiento+", '"+tabla+"', '"+campo+"', "+clave+");");
            if(ok.next()){
                res = (ok.getString(1)!=null) ? ok.getBoolean(1) : false;
                ok.close();
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return res;
    }
    public boolean movimientoInventario(String id)
    {
        try{
            ResultSet res = this.consulta("SELECT sum(debe) FROM vta_libro_diario_mayor where id_plan_cuenta="+id+" and lower(detalle) like '%inventario inicial%';");
            if(res.next()){
                float debe = (res.getString(1)!=null) ? res.getFloat(1) : 0;
                res.close();
                if(debe>0){
                    return true;
                }
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return false;
    }
}