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
public class Horario extends DataBase{
    public Horario(String m, int p, String db, String u, String c){
        super(m, p, db, u, c);
    }
    public ResultSet getCargo(String id)
    {
        return this.consulta("SELECT * FROM tbl_cargo where id_cargo="+id+";");
    }
    public ResultSet getHorario(String estado)
    {
        return this.consulta("SELECT id_horario, nombre FROM tab_horarios where eliminado='"+estado+"' and id_sucursal=1;");
    }
    public ResultSet getHorarioCombo (String sucursal)
    {
        return this.consulta("SELECT id_horario, nombre FROM tab_horarios where eliminado='false' and id_sucursal='"+sucursal+"';");
    }
    
}