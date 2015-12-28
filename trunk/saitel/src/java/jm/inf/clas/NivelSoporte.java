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

package jm.inf.clas;
import java.sql.ResultSet;
import jm.web.DataBase;

public class NivelSoporte extends DataBase{
    public NivelSoporte(String m, int p, String db, String u, String c){
        super(m, p, db, u, c);
    }
    public ResultSet getNivelSoporte(String id)
    {
        return this.consulta("SELECT * from tbl_nivel_soporte WHERE id_nivel_soporte="+id);
    }
    public ResultSet getNivelesSoporte()
    {
        return this.consulta("SELECT * from tbl_nivel_soporte order by id_nivel_soporte");
    }

}