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

package jm.adm.clas;
import java.sql.ResultSet;
import jm.web.DataBase;

/**
 *
 * @author Jorge
 */
public class Comun extends DataBase{
    public Comun(String m, int p, String db, String u, String c){
        super(m, p, db, u, c);
    }
    public ResultSet getGrupoComun(String grupo)
    {
        return this.consulta("SELECT id_comun,nombre FROM tbl_comun WHERE id_comun like'"+grupo.toUpperCase()+"%' order by nombre");
    }
    public ResultSet getComun(String grupo)
    {
        return this.consulta("SELECT * FROM tbl_comun WHERE id_comun like'"+grupo.toUpperCase()+"%' order by nombre;");
    }
    
    public ResultSet getArea(String grupo)
    {
        return this.consulta("SELECT * FROM tbl_area WHERE eliminado='"+grupo+"' order by area;");
    }
    public int getSecuancia(String grupo)
    {
        int num = 1;
        try{
            ResultSet res = this.consulta("select max(replace(id_comun, '"+grupo.toUpperCase()+"-', '')::int) from tbl_comun where id_comun like '"+grupo.toUpperCase()+"%';");
            if(res.next()){
                String sec = (res.getString(1)!=null) ? res.getString(1) : "0";
                num = Integer.valueOf(sec) + 1;
                res.close();
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return num;
    }
    public String getNuevo(String grupo)
    {
        String id_comun = "";
        try{
            ResultSet res = this.consulta("select max(replace(id_comun, '"+grupo.toUpperCase()+"-', '')::int) from tbl_comun WHERE id_comun like'"+grupo.toUpperCase()+"%';");
            if(res.next()){
                id_comun = (res.getString(1)!=null) ? res.getString(1) : "";
                res.close();
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return grupo + "-" + id_comun;
    }
    public boolean estaDuplicado(String id_com, String n)
    {
        ResultSet res = this.consulta("SELECT * FROM tbl_comun where lower(nombre)='"+n.toLowerCase()+"' and upper(id_comun) like'"+id_com.toUpperCase()+"%';");
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
    public boolean insertar(String id_comun, String nombre)
    {
        return this.ejecutar("INSERT INTO tbl_comun(id_comun,nombre) VALUES('"+id_comun.toUpperCase()+"', '"+nombre+"');");
    }
}