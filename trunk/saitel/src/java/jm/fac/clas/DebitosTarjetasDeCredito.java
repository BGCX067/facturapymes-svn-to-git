/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jm.fac.clas;

import java.sql.ResultSet;
import jm.web.DataBase;

/**
 *
 * @author desarrollo
 */
public class DebitosTarjetasDeCredito  extends DataBase{
    public DebitosTarjetasDeCredito(String m, int p, String db, String u, String c){
        super(m, p, db, u, c);
    }
    
    public boolean hayCuenta(String num_tarjeta, String id_instalacion, String periodo)
    {
        boolean ok = false;
        try{
            ResultSet res = this.consulta("SELECT * FROM tbl_documento_banco_tarjeta_credito where num_tarjeta='"+num_tarjeta+"' and id_instalacion="+id_instalacion+" and periodo='"+periodo.toLowerCase()+"';");
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
