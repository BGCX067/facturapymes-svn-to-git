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

package jm.fac.clas;
import java.sql.ResultSet;
import jm.web.DataBase;
import jm.web.Fecha;

/**
 *
 * @author Jorge
 */
public class CashManagementPichincha extends DataBase{
    public CashManagementPichincha(String m, int p, String db, String u, String c){
        super(m, p, db, u, c);
    }

    public boolean hayCuenta(String id_sucursal, String id_instalacion, String periodo)
    {
        boolean ok = false;
        try{
            ResultSet res = this.consulta("SELECT * FROM tbl_documento_banco_cash where id_sucursal="+id_sucursal+" and id_instalacion="+id_instalacion+" and lower(periodo)='"+periodo.toLowerCase()+"';");
            if(this.getFilas(res)>0){
                ok = true;
            }
            res.close();
        }catch(Exception e){
            e.printStackTrace();
        }
        return ok;
    }

}