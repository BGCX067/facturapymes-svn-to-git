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
import jm.web.Fecha;

/**
 *
 * @author Jorge
 */
public class Cierre extends DataBase{
    public Cierre(String m, int p, String db, String u, String c){
        super(m, p, db, u, c);
    }
    public String getPrimerCierre()
    {
        String fecha = "";
        try{
            ResultSet res = this.consulta("SELECT min(fecha_cierre) FROM tbl_cierre;");
            if(res.next()){
                fecha = (res.getString(1)!=null) ? res.getString(1) : "";
                res.close();
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return fecha;
    }
    public boolean ejecutarCierre()
    {
        boolean ok = false;
        try{
            ResultSet res = this.consulta("select proc_ejecutarcierre();");
            if(res.next()){
                ok = (res.getString(1)!=null) ? res.getBoolean(1) : false;
                res.close();
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return ok;
    }
    /*public int getMinAnioCierre()
    {
        int anio =  Fecha.getAnio();
        try{
            ResultSet res = this.consulta("select min(date_part('year', fecha_cierre)) from tbl_cierre;");
            if(res.next()){
                anio = (res.getString(1)!=null) ? res.getInt(1) : Fecha.getAnio();
                res.close();
            }
            res.close();
        }catch(Exception e){
            e.printStackTrace();
        }
        return anio;
    }*/
}