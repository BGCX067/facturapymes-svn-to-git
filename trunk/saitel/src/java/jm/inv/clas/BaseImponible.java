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

package jm.inv.clas;
import java.sql.ResultSet;
import jm.web.DataBase;

/**
 *
 * @author Jorge
 */
public class BaseImponible extends DataBase{
    public BaseImponible(String m, int p, String db, String u, String c){
        super(m, p, db, u, c);
    }
    public ResultSet getBasesImponibles()
    {
        return this.consulta("SELECT * FROM tbl_retencion_base_imponible order by codigo");
    }
    
    public ResultSet getBasesImponibles(String codigos)
    {
        return this.consulta("SELECT * FROM tbl_retencion_base_imponible where codigo in ("+codigos+") order by codigo");
    }

}