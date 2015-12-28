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

package jm.fac.clas;
import java.sql.ResultSet;
import jm.web.DataBase;

/**
 *
 * @author Jorge
 */
public class NotaDebitoVenta extends DataBase{
    public NotaDebitoVenta(String m, int p, String db, String u, String c){
        super(m, p, db, u, c);
    }
    public ResultSet getNotaDebito(String id)
    {
        return this.consulta("SELECT * FROM tbl_nota_debito_venta where id_nota_debito_venta="+id);
    }

    public ResultSet getNotaDebitoFactura(String id)
    {
        return this.consulta("SELECT * FROM tbl_nota_debito_venta where id_factura_venta="+id);
    }

    public ResultSet getNotaDebitoDetalle(String id)
    {
        return this.consulta("SELECT P.codigo, N.* FROM tbl_producto as P inner join tbl_nota_debito_venta_detalle as N on P.id_producto=N.id_producto "
                + "where N.id_nota_debito_venta="+id);
    }

    public ResultSet getNotasPendientes(String idC)
    {
        return this.consulta("SELECT * FROM tbl_nota_debito_venta where id_cliente="+idC+" and deuda>0 and anulado=false");
    }

    public long getNumNotaDebito(int id_sucursal)
    {
        int num = 1;
        try{
            ResultSet res = this.consulta("SELECT max(num_nota) FROM tbl_nota_debito_venta where id_sucursal="+id_sucursal);
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
        ResultSet res = this.consulta("SELECT * FROM tbl_nota_debito_venta where serie_nota='"+serie+"' and num_nota="+numero+" and anulado=false");
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

    public String concatenarValores(String id_articulos, String descripciones, String cantidades, String precios_costo, String precios_unitarios,
            String subtotales, String descuentos, String ivas, String totales)
    {
        String param = "";
        String vecArti [] = id_articulos.split(",");
        String vecCant [] = cantidades.split(",");
        String vecPC [] = precios_costo.split(",");
        String vecPU [] = precios_unitarios.split(",");
        String vecSubt [] = subtotales.split(",");
        String vecDes [] = descuentos.split(",");
        String vecIva [] = ivas.split(",");
        String vecTot [] = totales.split(",");
        String vecDescrip [] = descripciones.split(",");
        for(int i=0; i<vecArti.length; i++){
            param += "['"+vecArti[i]+"','"+vecCant[i]+"','"+vecPU[i]+"','"+vecSubt[i]+"','"+vecDes[i]+"','"+vecIva[i]+"','"+vecTot[i]+"','"+vecDescrip[i]+"','"+vecPC[i]+"'],";
        }
        param = param.substring(0, param.length()-1);
        return "array["+param+"]";
    }
    
    public String insertar(int id_sucursal, int id_punto_emision, String idFactura, String usuario, String ndSerie, String ndNum, String autorizacion, String ruc,
            String razon_social, String fecha_emision, String comprobante_modifica, String forma_pago, String banco, String num_cheque, String num_comp_pago, String gastos_bancos,
            String id_plan_cuenta_banco, String son, String razon_modificacion, String valor, String iva_0, String iva_12, String total,
            String id_productos, String descripciones, String cantidades, String precios_costo, String precios_unitarios,
            String subtotales, String descuentos, String ivas, String totales)
    {
        String num = "-1:-1";
        String paramProductos = this.concatenarValores(id_productos, descripciones, cantidades, precios_costo, precios_unitarios, subtotales, descuentos, ivas, totales);
        try{
            ResultSet res = this.consulta("select proc_notaDebitoVenta("+id_sucursal+", "+id_punto_emision+", "+idFactura+", '"+usuario+
                    "', '"+ndSerie+"', "+ndNum+", '"+autorizacion+"', '"+ruc+"', '"+razon_social+"', '"+fecha_emision+
                    "', '"+comprobante_modifica+"', '"+forma_pago+"', '"+banco+"', '"+num_cheque+"', '"+num_comp_pago+"', "+gastos_bancos+
                    ", "+id_plan_cuenta_banco+", '"+son+"', '"+razon_modificacion+"', "+valor+", "+iva_0+", "+iva_12+", "+total+", "+paramProductos+");");
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
            ResultSet res = this.consulta("select proc_anularNotaDebitoVenta("+idNotaDebito+");");
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
