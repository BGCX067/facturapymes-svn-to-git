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

public class Traspaso extends DataBase{
    public Traspaso(String m, int p, String db, String u, String c)
    {
        super(m, p, db, u, c);
    }
    public int getNumTraspaso()
    {
        int num = 1;
        try{
            ResultSet res = this.consulta("SELECT max(num_traspaso) FROM tbl_traspaso;");
            if(res.next()){
                num = (res.getString(1)!=null) ? res.getInt(1) : 0;
                num++;
                res.close();
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return num;
    }
    public ResultSet getTraspaso(String id)
    {
        return this.consulta("SELECT * FROM vta_traspaso where id_traspaso="+id+";");
    }
    public ResultSet getTraspasoDetalle(String id)
    {
        return this.consulta("select T.*, TD.id_traspaso_detalle, TD.id_producto, TD.cant_enviada, TD.cant_recibida, P.codigo, P.descripcion " +
                "from (tbl_traspaso as T inner join tbl_traspaso_detalle as TD on T.id_traspaso=TD.id_traspaso) "+ 
                "inner join tbl_producto as P on P.id_producto=TD.id_producto " +
                "where T.id_traspaso="+id+";");
    }
    public String concatenarValores(String productos, String cantidades)
    {
        String param = "";
        String vecProductos [] = productos.split(",");
        String vecCantidades [] = cantidades.split(",");
        for(int i=0; i<vecProductos.length; i++){
            param += "['"+vecProductos[i]+"','"+vecCantidades[i]+"'],";
        }
        param = param.substring(0, param.length()-1);
        return "array["+param+"]";
    }
    public int insertar(String num_traspaso, String origen, String usuario_origen, String fecha_envio, String recepcion, String productos, String cantidades)
    {
        int num = -1;
        try{
           String param = this.concatenarValores(productos, cantidades);
            ResultSet res = this.consulta("select proc_traspaso("+num_traspaso+", "+origen+", '"+usuario_origen+"', '"+fecha_envio+"', "+recepcion+", "+param+");");
            if(res.next()){
                num = (res.getString(1)!=null) ? res.getInt(1) : -1;
                res.close();
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return num;
    }
    public boolean actualizar(String id, String num_traspaso, String origen, String usuario_origen, String fecha_envio, String recepcion, String productos, String cantidades)
    {
        boolean ok = false;
        try{
            String param = this.concatenarValores(productos, cantidades);
            ResultSet res = this.consulta("select proc_editarTraspaso("+id+", "+num_traspaso+", "+origen+", '"+usuario_origen+"', '"+fecha_envio+"', "+recepcion+", "+param+");");
            if(res.next()){
                ok = (res.getString(1)!=null) ? res.getBoolean(1) : false;
                res.close();
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return ok;
    }

    public boolean recibir(String id, String usuario_recepcion, String fecha_recepcion, String productos, String cantidades)
    {
        boolean ok = false;
        try{
            String param = this.concatenarValores(productos, cantidades);
            ResultSet res = this.consulta("select proc_recibirTraspaso("+id+", '"+usuario_recepcion+"', '"+fecha_recepcion+"', "+param+");");
            if(res.next()){
                ok = (res.getString(1)!=null) ? res.getBoolean(1) : false;
                res.close();
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return ok;
    }
}
