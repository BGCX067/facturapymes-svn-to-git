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
public class NotaDebitoCompra extends DataBase{
    public NotaDebitoCompra(String m, int p, String db, String u, String c){
        super(m, p, db, u, c);
    }
    public ResultSet getNotaDebito(String id)
    {
        return this.consulta("SELECT * FROM tbl_nota_debito_compra where id_nota_debito_compra="+id);
    }

    public ResultSet getNotaDebitoFactura(String id)
    {
        return this.consulta("SELECT * FROM tbl_nota_debito_compra where id_factura_compra="+id);
    }

    public ResultSet getNotaDebitoDetalle(String id)
    {
        return this.consulta("SELECT P.codigo, P.descripcion, N.* FROM tbl_producto as P inner join tbl_nota_debito_compra_detalle as N on P.id_producto=N.id_producto "
                + "where N.id_nota_debito_compra="+id);
    }

    public ResultSet getNotasPendientes(String idP)
    {
        return this.consulta("SELECT * FROM tbl_nota_debito_compra where id_proveedor="+idP+" and deuda>0 and anulado=false");
    }

    public long getNumNotaDebito()
    {
        int num = 1;
        try{
            ResultSet res = this.consulta("SELECT max(num_nota) FROM tbl_nota_debito_compra");
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

    public boolean NotaDuplicada(String serie, String numero)
    {
        ResultSet res = this.consulta("SELECT * FROM tbl_nota_debito_compra where serie_nota='"+serie+"' and num_nota="+numero+" and anulado=false");
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

    public String verificarStock(int id_sucursal, String id_productos, String cantidades)
    {
        ResultSet rs = null;
        String codigo = "";
        int cantidad = 1;
        String vecProd [] = id_productos.split(",");
        String vecCant [] = cantidades.split(",");
        for(int i=0; i<vecProd.length; i++){
            try{
                rs = this.consulta("select P.codigo, SP.stock_sucursal - "+vecCant[i]+" "
                        + "from vta_producto as P inner join tbl_sucursal_producto as SP on P.id_producto=SP.id_producto "
                        + "where SP.id_sucursal="+id_sucursal+" and SP.id_producto="+vecProd[i]);
                if(rs.next()){
                    cantidad = rs.getString(2)!=null ? rs.getInt(2) : 1;
                    if(cantidad < 0){
                        codigo = rs.getString("codigo")!=null ? rs.getString("codigo") : "";
                        break;
                    }
                }
            }catch(Exception e){
                e.printStackTrace();
            }
        }
        try{
            rs.close();
        }catch(Exception e){
            e.printStackTrace();
        }
        return codigo;
    }

    public String concatenarValores(String id_articulos, String cantidades, String precios_unitarios,
            String subtotales, String descuentos, String ivas, String totales)
    {
        String param = "";
        String vecArti [] = id_articulos.split(",");
        String vecCant [] = cantidades.split(",");
        String vecPU [] = precios_unitarios.split(",");
        String vecSubt [] = subtotales.split(",");
        String vecDes [] = descuentos.split(",");
        String vecIva [] = ivas.split(",");
        String vecTot [] = totales.split(",");
        for(int i=0; i<vecArti.length; i++){
            param += "['"+vecArti[i]+"','"+vecCant[i]+"','"+vecPU[i]+"','"+vecSubt[i]+"','"+vecDes[i]+"','"+vecIva[i]+"','"+vecTot[i]+"'],";
        }
        param = param.substring(0, param.length()-1);
        return "array["+param+"]";
    }

    public String insertar(int id_sucursal, String idFactura, String usuario, String ndSerie, String ndNum, String autorizacion, 
            String fecha_emision, String comprobante_modifica, 
            String razon_modificacion, String valor, String iva_0, String iva_12, String total,
            String id_productos, String cantidades, String precios_unitarios,
            String subtotales, String descuentos, String ivas, String totales)
    {
        String num = "-1:-1";
        String paramProductos = this.concatenarValores(id_productos, cantidades, precios_unitarios, subtotales, descuentos, ivas, totales);
        try{
            ResultSet res = this.consulta("select proc_notaDebitoCompra("+id_sucursal+", "+idFactura+", '"+usuario+
                    "', '"+ndSerie+"', "+ndNum+", '"+autorizacion+"', '"+fecha_emision+ "', '"+comprobante_modifica+
                    "', '"+razon_modificacion+"', "+valor+", "+iva_0+", "+iva_12+", "+total+", "+paramProductos+");");
            if(res.next()){
                num = (res.getString(1)!=null) ? res.getString(1) : "-1:-1";
                res.close();
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return num;
    }

    public boolean anular(String idNotaDebito)
    {
        boolean ok = false;
        try{
            ResultSet res = this.consulta("select proc_anularNotaDebitoCompra("+idNotaDebito+");");
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
