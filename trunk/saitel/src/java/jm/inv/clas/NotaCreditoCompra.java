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
public class NotaCreditoCompra extends DataBase{
    public NotaCreditoCompra(String m, int p, String db, String u, String c){
        super(m, p, db, u, c);
    }
    public ResultSet getNotaCredito(String id)
    {
        return this.consulta("SELECT * FROM tbl_nota_credito_compra where id_nota_credito_compra="+id);
    }

    public ResultSet getNotaCreditoFactura(String id)
    {
        return this.consulta("SELECT * FROM tbl_nota_credito_compra where id_factura_compra="+id);
    }

    public ResultSet getNotaCreditoFacturaDetalle(String id)
    {
        return this.consulta("SELECT CD.*, P.codigo, P.descripcion, D.p_u FROM (tbl_factura_compra_detalle as D inner join tbl_nota_credito_compra_detalle as CD "
                + "on D.id_factura_compra_detalle=CD.id_factura_compra_detalle) inner join tbl_producto as P on P.id_producto=D.id_producto"
                + " where CD.id_nota_credito_compra="+id);
    }

    public ResultSet getNotasCreditoPendientes(String idC)
    {
        return this.consulta("SELECT * FROM tbl_nota_credito_compra where id_cliente="+idC+" and deuda>0 and anulado=false;");
    }

    public String concatenarValores(String id_articulos, String cantidades, 
            String subtotales, String descuentos, String ivas, String totales)
    {
        String param = "";
        String vecArti [] = id_articulos.split(",");
        String vecCant [] = cantidades.split(",");
        String vecSubt [] = subtotales.split(",");
        String vecDes [] = descuentos.split(",");
        String vecIva [] = ivas.split(",");
        String vecTot [] = totales.split(",");
        for(int i=0; i<vecArti.length; i++){
            param += "['"+vecArti[i]+"','"+vecCant[i]+"','"+vecSubt[i]+"','"+vecDes[i]+"','"+vecIva[i]+"','"+vecTot[i]+"','FALSE'],";
        }
        param = param.substring(0, param.length()-1);
        return "array["+param+"]";
    }
    
    public String insertar(int id_sucursal, String idFactura, String usuario, String ndSerie, String ndNum, String autorizacion,
            String fecha_emision, String comprobante_modifica, String tipo, String razon_modificacion, String subtotal, 
            String subtotal_0, String subtotal_2, String subtotal_6, String descuento, String iva_2, String total, 
            String ids_factura_venta_detalle, String cantidades, String p_sts, String descs, String ivas, String totales)
    {
        String num = "-1:-1";
        try{
            String paramArtic = this.concatenarValores(ids_factura_venta_detalle, cantidades, p_sts, descs, ivas, totales);
            ResultSet res = this.consulta("select notaCreditoCompra("+id_sucursal+", "+idFactura+", '"+usuario+
                    "', '"+ndSerie+"', "+ndNum+", '"+autorizacion+"', '"+fecha_emision+"', '"+comprobante_modifica+
                    "', '"+tipo+"', '"+razon_modificacion+"', "+subtotal+", "+subtotal_0+", "+subtotal_2+", "+subtotal_6+
                    ", "+descuento+", "+iva_2+", "+total+", "+paramArtic+");");
            if(res.next()){
                num = (res.getString(1)!=null) ? res.getString(1) : "-1:-1";
                res.close();
            }
        }catch(Exception e){
            this.setError(e.getMessage());
        }
        return num;
    }

    public boolean anular(String idNotaCredito)
    {
        boolean ok = false;
        try{
            ResultSet res = this.consulta("select proc_anularNotaCreditoCompra("+idNotaCredito+");");
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
