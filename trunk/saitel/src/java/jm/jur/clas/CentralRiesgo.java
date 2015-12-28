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

package jm.jur.clas;
import java.sql.ResultSet;
import jm.web.DataBase;

/**
 *
 * @author Jorge
 */
public class CentralRiesgo extends DataBase{
    public CentralRiesgo(String m, int p, String db, String u, String c){
        super(m, p, db, u, c);
    }
    public ResultSet getCentralRiesgo(String id)
    {
        return this.consulta("SELECT * FROM tbl_central_riesgo WHERE id_central_riesgo="+id);
    }

    public boolean estaDuplicado(String id, String id_cliente)
    {
        ResultSet res = this.consulta("SELECT * FROM tbl_central_riesgo where id_cliente="+id_cliente+"' and id_central_riesgo <> "+id + " and eliminado=false");
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

    public boolean insertar(String id_cliente, String observacion, String bloqueado)
    {
        return this.ejecutar("INSERT INTO tbl_central_riesgo(id_cliente,fecha_registro,observacion,bloqueado) VALUES("+id_cliente+", now(), '"+observacion+"', "+bloqueado+");");
    }

    public boolean actualizar(String id, String id_cliente, String observacion, String bloqueado)
    {
        return this.ejecutar("UPDATE tbl_central_riesgo SET id_cliente="+id_cliente+", ubicacion='"+observacion+"', bloqueado="+bloqueado+" where id_central_riesgo="+id);
    }
}