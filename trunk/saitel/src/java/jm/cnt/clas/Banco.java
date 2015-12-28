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

public class Banco extends DataBase{
    public Banco(String m, int p, String db, String u, String c){
        super(m, p, db, u, c);
    }

    public ResultSet getBanco(String id)
    {
        return this.consulta("SELECT * FROM tbl_banco where id_banco="+id);
    }

    public ResultSet getBancos()
    {
        return this.consulta("SELECT id_banco,banco FROM vta_banco");
    }

    public ResultSet getBancosIngresos()
    {
        return this.consulta("SELECT id_plan_cuenta,banco FROM vta_banco");
    }

    public String getBancosJSON()
    {
        ResultSet rsBancos = this.consulta("SELECT id_banco,id_plan_cuenta,banco,num_cuenta,num_cheque,lugar FROM vta_banco");
        return this.getJSON(rsBancos);
    }

    public boolean insertar(String idP, String banco, String num_cuenta, String num_cheque)
    {
        return this.ejecutar("INSERT INTO tbl_banco(id_plan_cuenta,banco,num_cuenta,num_cheque) " +
                "VALUES("+idP+", '"+banco+"', '"+num_cuenta+"', "+num_cheque+")");
    }

    public boolean actualizar(String id, String idP, String banco, String num_cuenta, String num_cheque)
    {
        return this.ejecutar("UPDATE tbl_banco SET id_plan_cuenta="+idP+", banco='"+banco+"', num_cuenta='"+num_cuenta+"', num_cheque="+num_cheque+" WHERE id_banco="+id);
    }

    public boolean actualizar(String id, String num_cheque, String lugar)
    {
        long num_ch = Long.parseLong(num_cheque) + 1;
        return this.ejecutar("UPDATE tbl_banco SET num_cheque="+num_ch+", lugar='"+lugar+"' WHERE id_banco="+id);
    }
}