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

/**
 *
 * @author Jorge
 */
public class Contrato extends DataBase{
    public Contrato(String m, int p, String db, String u, String c){
        super(m, p, db, u, c);
    }
    public ResultSet getContratoCliente(String idCliente)
    {
        return this.consulta("SELECT * FROM vta_contrato where id_cliente="+idCliente+";");
    }

    public ResultSet getContrato(String id_contrato)
    {
        return this.consulta("SELECT * FROM vta_contrato where id_contrato="+id_contrato+";");
    }

    public String getNumContratosJSON()
    {
        ResultSet rs = this.consulta("select S.id_sucursal, case when max(C.num_contrato)>0 then max(C.num_contrato)+1 else 1 end "
                + "from tbl_sucursal as S left outer join tbl_contrato as C on S.id_sucursal=C.id_sucursal group by S.id_sucursal;");
        String tbl = this.getJSON(rs);
        try{
            rs.close();
        }catch(Exception e){
            e.printStackTrace();
        }
        return tbl;
    }

    public String insertar(String num_contrato, String id_cliente, String id_sucursal, String fecha_contrato, String fecha_termino, 
            String ruc_representante, String representante, String contrato)
    {
        return this.insert("INSERT INTO tbl_contrato(num_contrato, id_cliente, id_sucursal, fecha_contrato, fecha_termino, ruc_representante, representante, contrato) "
                + "VALUES("+num_contrato+", "+id_cliente+", "+id_sucursal+", '"+fecha_contrato+"', '"+fecha_termino+"', '"+ruc_representante+"', '"+representante+"', '"+contrato+"');");
    }

    public boolean actualizar(String id, String num_contrato, String id_cliente, String id_sucursal, String fecha_contrato, String fecha_termino, 
            String representante, String ruc_representante, String contrato)
    {
        return this.ejecutar("UPDATE tbl_contrato SET num_contrato="+num_contrato+", id_cliente="+id_cliente+", id_sucursal="+id_sucursal+
                ", fecha_contrato='"+fecha_contrato+"', fecha_termino='"+fecha_termino+"', ruc_representante='"+ruc_representante+
                "', representante='"+representante+"', contrato='"+contrato+"' WHERE id_contrato="+id+";");
    }

    public boolean anular(String id)
    {
        return this.ejecutar("UPDATE tbl_contrato SET anulado=true WHERE id_contrato="+id+";");
    }
}