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

public class ValeCaja extends DataBase{
    public ValeCaja(String m, int p, String db, String u, String c)
    {
        super(m, p, db, u, c);
    }
    public long getNumValeCaja(int id_sucursal)
    {
        int num = 1;
        try{
            ResultSet res = this.consulta("SELECT max(num_vale_caja) FROM tbl_vale_caja WHERE id_sucursal="+id_sucursal);
            if(res.next()){
                num = (res.getString(1)!=null) ? res.getInt(1) : 0;
                num++;
                res.close();
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return num;
    }
    public ResultSet getValeCaja(String id)
    {
        return this.consulta("SELECT * FROM tbl_vale_caja where id_vale_caja="+id);
    }
    public int insertar(int id_sucursal, String num_vale_caja, String custodio, String fecha, String concepto,
            String importe, String la_suma_de, String solicitante, String id_plan_cuenta_gasto)
    {
        int num = -1;
        try{
            ResultSet res = this.consulta("select proc_valeCaja("+id_sucursal+", "+num_vale_caja+", '"+custodio+"', '"+fecha+"', '"+concepto+
                    "', "+importe+", '"+la_suma_de+"', '"+solicitante+"', "+id_plan_cuenta_gasto+")");
            if(res.next()){
                num = (res.getString(1)!=null) ? res.getInt(1) : -1;
                res.close();
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return num;
    }
    public boolean anular(String id)
    {
        boolean num = false;
        try{
            ResultSet res = this.consulta("select proc_valeCajaAnular("+id+")");
            if(res.next()){
                num = (res.getString(1)!=null) ? res.getBoolean(1) : false;
                res.close();
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return num;
    }

}
