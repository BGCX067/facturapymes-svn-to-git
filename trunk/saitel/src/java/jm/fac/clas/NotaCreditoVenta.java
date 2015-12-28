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
public class NotaCreditoVenta extends DataBase{
    public NotaCreditoVenta(String m, int p, String db, String u, String c){
        super(m, p, db, u, c);
    }
    public ResultSet getNotaCredito(String id)
    {
        return this.consulta("SELECT * FROM tbl_nota_credito_venta where id_nota_credito_venta="+id);
    }

    public ResultSet getNotaCreditoFactura(String id)
    {
        return this.consulta("SELECT * FROM tbl_nota_credito_venta where anulado=false and id_factura_venta="+id);
    }

    public ResultSet getNotaCreditoFacturaDetalle(String id)
    {
        return this.consulta("SELECT D.*, P.codigo FROM (tbl_factura_venta_detalle as D inner join tbl_nota_credito_venta_detalle as CD "
                + "on D.id_factura_venta_detalle=CD.id_factura_venta_detalle) inner join tbl_producto as P on P.id_producto=D.id_producto"
                + " where CD.id_nota_credito_venta="+id);
    }

    public ResultSet getNotasCreditoPendientes(String idP)
    {
        return this.consulta("SELECT * FROM tbl_nota_credito_venta where id_proveedor="+idP+" and deuda>0 and anulado=false;");
    }

    public long getNumNotaCredito(int id_sucursal)
    {
        int num = 1;
        try{
            ResultSet res = this.consulta("SELECT max(num_nota) FROM tbl_nota_credito_venta where id_sucursal="+id_sucursal);
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

    public boolean setNumNota(String id_nota_credito, String num)
    {
        return this.ejecutar("update tbl_nota_credito_venta set num_nota="+num+" where id_nota_credito_venta="+id_nota_credito);
    }
    
    public boolean NotaDuplicada(String serie, String numero)
    {
        ResultSet res = this.consulta("SELECT * FROM tbl_nota_credito_venta where serie_nota='"+serie+"' and num_nota="+numero+" and anulado=false");
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
    
    public String insertar(int id_sucursal, int id_punto_emision, String idFactura, String usuario, String ndSerie, String ndNum, String autorizacion, String ruc,
            String razon_social, String fecha_emision, String comprobante_modifica, String tipo, String razon_modificacion, String subtotal, String subtotal_0, 
            String subtotal_2, String subtotal_6, String descuento, String iva_2, String total, String ids_factura_venta_detalle, String cantidades, 
            String p_sts, String descs, String ivas, String totales)
    {
        String num = "-1:-1";
        try{
            String paramArtic = this.concatenarValores(ids_factura_venta_detalle, cantidades, p_sts, descs, ivas, totales);
            ResultSet res = this.consulta("select notaCreditoVenta("+id_sucursal+", "+id_punto_emision+", "+idFactura+", '"+usuario+
                    "', '"+ndSerie+"', "+ndNum+", '"+autorizacion+"', '"+ruc+"', '"+razon_social+"', '"+fecha_emision+
                    "', '"+comprobante_modifica+"', '"+tipo+"', '"+razon_modificacion+"', "+subtotal+", "+subtotal_0+", "+subtotal_2+
                    ", "+subtotal_6+", "+descuento+", "+iva_2+", "+total+", "+paramArtic+");");
            if(res.next()){
                num = (res.getString(1)!=null) ? res.getString(1) : "-1:-1";
                res.close();
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return num;
    }

    public boolean setEstadoDocumento(String id_nota_credito_venta, String estado, String claveAcceso, String autorizacionXml)
    {
        return this.ejecutar("update tbl_nota_credito_venta set estado_documento='"+estado+"', clave_acceso='"+claveAcceso+"', documento_xml='"+autorizacionXml+"' where id_nota_credito_venta="+id_nota_credito_venta);
    }
    
    public boolean anular(String idNotaCredito)
    {
        boolean ok = false;
        try{
            ResultSet res = this.consulta("select proc_anularNotaCreditoVenta("+idNotaCredito+");");
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
