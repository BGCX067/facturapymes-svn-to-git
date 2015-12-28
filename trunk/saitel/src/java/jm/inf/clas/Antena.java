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

public class Antena extends DataBase{
    public Antena(String m, int p, String db, String u, String c){
        super(m, p, db, u, c);
    }
    public ResultSet getAntena(String id)
    {
        return this.consulta("SELECT * from tbl_antena WHERE id_antena_acoplada="+id);
    }    
    public ResultSet getAntenas()
    {
        return this.consulta("SELECT antena_acoplada as id_antena_acoplada, antena_acoplada from vta_antena");
    }
    public ResultSet getAntenas(int id_suc)
    {
        return this.consulta("SELECT antena_acoplada as id_antena_acoplada, antena_acoplada from vta_antena where id_sucursal="+id_suc);
    }
    public ResultSet getAntenas(int id_suc, String txt)
    {
        return this.consulta("SELECT antena_acoplada as id_antena_acoplada, antena_acoplada from vta_antena where id_sucursal="+id_suc+" and lower(antena_acoplada) like '"+txt.toLowerCase()+"%' limit 10 offset 0");
    }
    public ResultSet getAntenas(String id_nodo)
    {
        return this.consulta("SELECT * from vta_antena where id_nodo="+id_nodo);
    }
    public boolean estaDuplicado(String id, String antena)
    {
        ResultSet res = this.consulta("SELECT * FROM vta_antena where lower(antena_acoplada)='"+antena.toLowerCase()+"' and id_antena_acoplada<>"+id);
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
    public boolean insertar(String id_nodo, String antena_acoplada)
    {
        boolean res = false;
        String pk = this.insert("INSERT INTO tbl_antena(id_nodo,antena_acoplada) VALUES("+id_nodo+", '"+antena_acoplada+"');");
        if(pk.compareTo("-1")!=0){
            res = true;
        }
        return res;
    }
    public boolean actualizar(String id, String id_nodo, String antena_acoplada)
    {
        return this.ejecutar("UPDATE tbl_antena SET id_nodo="+id_nodo+", antena_acoplada='"+antena_acoplada+"' where id_antena_acoplada="+id);
    }

}