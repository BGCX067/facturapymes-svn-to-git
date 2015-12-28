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

public class Pedido extends DataBase{
    public Pedido(String m, int p, String db, String u, String c)
    {
        super(m, p, db, u, c);
    }
    public int getNumPedido()
    {
        int num = 1;
        try{
            ResultSet res = this.consulta("SELECT max(num_pedido) FROM tbl_pedido;");
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
    public int getNumPedidoActivo()
    {
        int num = 1;
        try{
            ResultSet res = this.consulta("SELECT max(num_pedido) FROM tbl_pedidoActivo;");
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
    public ResultSet getPedido(String id)
    {
            return this.consulta("SELECT * FROM vta_pedido where id_pedido="+id+";");
    }
    public ResultSet getPedidoDetalle(String id)
    {
        /*return this.consulta("select P.*, PD.id_pedido_detalle, PD.id_producto, PD.cantidad, PR.codigo, PR.descripcion " +
                "from (tbl_pedido as P inner join tbl_pedido_detalle as PD on P.id_pedido=PD.id_pedido) "+
                "inner join tbl_producto as PR on PR.id_producto=PD.id_producto " +
                "where P.id_pedido="+id+";");
        return this.consulta("select P.*, PD.id_pedido_detalle, PD.id_producto, PD.cantidad, PR.codigo_activo, PR.descripcion \n" +
"                from (tbl_pedido as P inner join tbl_pedido_detalle as PD on P.id_pedido=PD.id_pedido) \n" +
"                inner join tbl_activo as PR on PR.id_categoria=PD.id_producto \n" +
"                where P.id_pedido="+Integer.parseInt(id)+";");*/
        return this.consulta("select * from tbl_pedido_detalle p, vta_activo_arbol a where p.id_pedido="+id+" and p.id_producto=a.id_comun");
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
    public int insertar(String num_pedido, String id_sucursal, String usuario, String productos, String cantidades)
    {
        int num = -1;
        try{
            String param = this.concatenarValores(productos, cantidades);
            ResultSet res = this.consulta("select proc_pedido_activos("+num_pedido+", "+id_sucursal+", '"+usuario+"', "+param+");");
            if(res.next()){
                num = (res.getString(1)!=null) ? res.getInt(1) : -1;
                res.close();
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return num;
    }
    public boolean actualizar(String id, String num_pedido, String id_sucursal, String usuario, String productos, String cantidades)
    {
        boolean ok = false;
        try{
            String param = this.concatenarValores(productos, cantidades);
            ResultSet res = this.consulta("select proc_editarPedido("+id+", "+num_pedido+", "+id_sucursal+", '"+usuario+"', "+param+");");
            if(res.next()){
                ok = (res.getString(1)!=null) ? res.getBoolean(1) : false;
                res.close();
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return ok;
    }
    public boolean setRevisado(String id)
    {
        return this.ejecutar("update tbl_pedido set revisado=true where id_pedido="+id+";");
    }
    
}
