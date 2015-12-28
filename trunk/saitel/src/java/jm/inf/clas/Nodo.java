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

public class Nodo extends DataBase{
    public Nodo(String m, int p, String db, String u, String c){
        super(m, p, db, u, c);
    }
    public ResultSet getNodo(String id)
    {
        return this.consulta("SELECT * from tbl_nodo WHERE id_nodo="+id);
    }
    public ResultSet getNodos()
    {
        return this.consulta("SELECT id_nodo, nodo from vta_nodo");
    }
    public boolean estaDuplicado(String id, String nodo)
    {
        ResultSet res = this.consulta("SELECT * FROM tbl_nodo where lower(nodo)='"+nodo.toLowerCase()+"' and id_nodo<>"+id);
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
    public boolean insertar(int id_sucursal, String cod_cont, String nodo, String cod_reg, String tipo, String tipo_otra, String altura_msnm, String altura_estr,
            String id_provincia, String id_canton, String localidad, String latitud, String longitud, String puesta_tierra, String pararrayos, String otros, String linea_comercial,
            String generador, String banco_baterias, String respaldo, String resp_generador, String resp_baterias, String resp_ups, String resp_otro, String propietario,
            String ci, String custodio, String sexo, String sector, String direccion, String telefono, String movil, String email, String banco, String cuenta)
    {
        boolean res = false;
        String pk = this.insert("INSERT INTO tbl_nodo(id_sucursal, cod_cont, nodo, cod_reg, tipo, tipo_otra, altura_msnm, altura_estr,"+
                    "id_provincia, id_canton, localidad, latitud, longitud, puesta_tierra, pararrayos, otros, linea_comercial,"+
                    "generador, banco_baterias, respaldo, resp_generador, resp_baterias, resp_ups, resp_otro, propietario,"+
                    "ci, custodio, sexo, sector, direccion, telefono, movil, email, banco, cuenta) "+ 
                    "VALUES("+id_sucursal+", '"+cod_cont+"', '"+nodo+"', '"+cod_reg+"', '"+tipo+"', '"+tipo_otra+"', "+altura_msnm+", "+altura_estr+","+
                    id_provincia+", "+id_canton+", '"+localidad+"', '"+latitud+"', '"+longitud+"', "+puesta_tierra+", "+pararrayos+", '"+otros+"', "+linea_comercial+","+
                    generador+", "+banco_baterias+", "+respaldo+", "+resp_generador+", "+resp_baterias+", "+resp_ups+", '"+resp_otro+"', '"+propietario+"','"+
                    ci+"', '"+custodio+"', "+sexo+", '"+sector+"', '"+direccion+"', '"+telefono+"', '"+movil+"', '"+email+"', '"+banco+"', '"+cuenta+"');");
        if(pk.compareTo("-1")!=0){
            res = true;
        }
        return res;
    }
    public boolean actualizar(String id, int id_sucursal, String cod_cont, String nodo, String cod_reg, String tipo, String tipo_otra, String altura_msnm, String altura_estr,
            String id_provincia, String id_canton, String localidad, String latitud, String longitud, String puesta_tierra, String pararrayos, String otros, String linea_comercial,
            String generador, String banco_baterias, String respaldo, String resp_generador, String resp_baterias, String resp_ups, String resp_otro, String propietario,
            String ci, String custodio, String sexo, String sector, String direccion, String telefono, String movil, String email, String banco, String cuenta)
    {
        return this.ejecutar("UPDATE tbl_nodo SET id_sucursal="+id_sucursal+", cod_cont='"+cod_cont+"', nodo='"+nodo+"', cod_reg='"+cod_reg+"', tipo='"+tipo+"', tipo_otra='"+tipo_otra+"', altura_msnm="+altura_msnm+", altura_estr="+altura_estr+", "+
                    "id_provincia="+id_provincia+", id_canton="+id_canton+", localidad='"+localidad+"', latitud='"+latitud+"', longitud='"+longitud+"', puesta_tierra="+puesta_tierra+", pararrayos="+pararrayos+", otros='"+otros+"', linea_comercial="+linea_comercial+", "+
                    "generador="+generador+", banco_baterias="+banco_baterias+", respaldo="+respaldo+", resp_generador="+resp_generador+", resp_baterias="+resp_baterias+", resp_ups="+resp_ups+", resp_otro='"+resp_otro+"', propietario='"+propietario+"', "+
                    "ci='"+ci+"', custodio='"+custodio+"', sexo="+sexo+", sector='"+sector+"', direccion='"+direccion+"', telefono='"+telefono+"', movil='"+movil+"', email='"+email+"', banco='"+banco+"', cuenta='"+cuenta+"' where id_nodo="+id);
    }

}