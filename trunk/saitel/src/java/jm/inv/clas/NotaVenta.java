/**
* @version 1.0
* @package NotaVentaPYMES.
* @author Jorge Washington Mueses Cevallos.
* @copyright Copyright (C) 2010 por Jorge Mueses. Todos los derechos reservados.
* @license http://www.gnu.org/copyleft/gpl.html GNU/GPL.
* NotaVentaPYMES! es un software de libre distribución, que puede ser
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
public class NotaVenta extends DataBase{
    public NotaVenta(String m, int p, String db, String u, String c){
        super(m, p, db, u, c);
    }
    public ResultSet getNotaVenta(String id)
    {
        return this.consulta("SELECT * FROM vta_nota_venta_compra where id_nota_venta_compra="+id+";");
    }

    public ResultSet getNotaVentasPendientes(String idP)
    {
        return this.consulta("SELECT * FROM vta_nota_venta_compra where id_proveedor="+idP+" and deuda>0 and anulado=false;");
    }

    public ResultSet getNotaVentaDetalle(String id)
    {
        return this.consulta("select D.*,P.codigo,P.descripcion from tbl_nota_venta_compra_detalle as D inner join tbl_producto as P on P.id_producto=D.id_producto where D.id_nota_venta_compra="+id+";");
    }

    public ResultSet getPago(String idFP)
    {
        return this.consulta("SELECT * FROM vta_factura_compra_pago where id_factura_compra_pago="+idFP);
    }

    public ResultSet getPagos(String idD)
    {
        return this.consulta("SELECT * FROM vta_nota_venta_compra_pago where id_comprobante_egreso="+idD+" and NotaVenta_anulada=false and pago_anulado=false");
    }

    public boolean hayPagos(String idNotaVenta)
    {
        boolean ok = false;
        try{
            ResultSet res = this.consulta("select * from tbl_nota_venta_compra_pago where anulado=false and id_NotaVenta_compra="+idNotaVenta);
            if(this.getFilas(res)>0){
                ok = true;
            }
            res.close();
        }catch(Exception e){
            ok = false;
            e.printStackTrace();
        }
        return ok;
    }

    public String concatenarValores(String id_articulos, String cantidades, String precios_unitarios, String totales)
    {
        String param = "";
        String vecArti [] = id_articulos.split(",");
        String vecCant [] = cantidades.split(",");
        String vecPU [] = precios_unitarios.split(",");
        String vecTot [] = totales.split(",");
        for(int i=0; i<vecArti.length; i++){
            param += "['"+vecArti[i]+"','"+vecCant[i]+"','"+vecPU[i]+"','"+vecTot[i]+"','0','0','"+vecTot[i]+"','FALSE'],";
        }
        param = param.substring(0, param.length()-1);
        return "array["+param+"]";
    }

    public String concatenarValores(String codigo_activos, String descripciones, String depresiaciones, String categorias, String ubicaciones, String costos)
    {
        String param = "";
        String vecCod [] = codigo_activos.split(",");
        String vecDes [] = descripciones.split(",");
        String vecDep [] = depresiaciones.split(",");
        String vecCat [] = categorias.split(",");
        String vecUbi [] = ubicaciones.split(",");
        String vecCos [] = costos.split(",");
        for(int i=0; i<vecCod.length; i++){
            param += "['"+vecCod[i]+"','"+vecDes[i]+"','"+vecDep[i]+"','"+vecCat[i]+"','"+vecUbi[i]+"','"+vecCos[i]+"'],";
        }
        param = param.substring(0, param.length()-1);
        return "array["+param+"]";
    }

    public String concatenarValores(String id_retenciones, String bases_imponibles, String valores_retenidos)
    {
        String param = "";
        String vecRet [] = id_retenciones.split(",");
        String vecBI [] = bases_imponibles.split(",");
        String vecVal [] = valores_retenidos.split(",");
        for(int i=0; i<vecRet.length; i++){
            param += "['"+vecRet[i]+"','"+vecBI[i]+"','"+vecVal[i]+"'],";
        }
        param = param.substring(0, param.length()-1);
        return "array["+param+"]";
    }

    public boolean retencionDuplicada(String id, String serie, String numero)
    {
        ResultSet res = this.consulta("SELECT * FROM tbl_retencion_compra where ret_num_serie='"+serie+"' and ret_num_retencion="+numero+
                " and anulado=false and id_NotaVenta_compra<>"+id);
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

    public String insertar(int id_sucursal, String serie_NotaVenta, String autorizacion, String num_NotaVenta, String id_proveedor, String fecha_compra, String fecha_ven_NotaVenta,
                        String observacion, String total_pagado, String id_articulos, String cantidades, String precios_unitarios, String totales,
                        String sustento_tributario, String num_serie_ret, String autorizacion_ret, String num_retencion, String fecha_emision_ret, String ejercicio_fiscal, 
                        String ejercicio_fiscal_mes, String total_retenido, String id_retenciones, String bases_imponibles, String valores_retenidos)
    {
        String num = "-1:-1";
        try{
            String paramArtic = this.concatenarValores(id_articulos, cantidades, precios_unitarios, totales);
            String paramRet = this.concatenarValores(id_retenciones, bases_imponibles, valores_retenidos);
            ResultSet res = this.consulta("select proc_notaVentaCompra("+id_sucursal+", '"+serie_NotaVenta+"', '"+autorizacion+"', "+num_NotaVenta+
                    ", "+id_proveedor+", '"+fecha_compra+"', '"+fecha_ven_NotaVenta+"', '"+observacion+"', "+total_pagado+", "+paramArtic+", " +
                    "'"+sustento_tributario+"', '"+num_serie_ret+"', '"+autorizacion_ret+"', "+num_retencion+", "+fecha_emision_ret+", "+ejercicio_fiscal+", '"+ejercicio_fiscal_mes+
                    "', "+total_retenido+", "+paramRet+");");
            if(res.next()){
                num = (res.getString(1)!=null) ? res.getString(1) : "-1:-1";
                res.close();
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return num;
    }

    public String actualizar(int id_sucursal, String id, String id_comprobante_diario, String serie_NotaVenta, String autorizacion, String num_NotaVenta, String id_proveedor, String fecha_compra, String fecha_ven_NotaVenta,
                        String observacion, String subtotal, String descuento, String iva_0, String iva_12, String total_pagado,
                        String id_articulos, String cantidades, String precios_unitarios, String subtotales, String descuentos, String ivas, String totales,
                        String num_serie_ret, String autorizacion_ret, String num_retencion, String fecha_emision_ret, String ejercicio_fiscal, String total_retenido,
                        String id_retenciones, String bases_imponibles, String valores_retenidos, String id_rol)
    {
        String num = "-1:-1";
        try{
            String paramArtic = this.concatenarValores(id_articulos, cantidades, precios_unitarios, totales);
            String paramRet = this.concatenarValores(id_retenciones, bases_imponibles, valores_retenidos);
            ResultSet res = this.consulta("select proc_editarNotaVentaCompra("+id_sucursal+", "+id+", "+id_comprobante_diario+", '"+serie_NotaVenta+"','"+autorizacion+"', "+num_NotaVenta+", "+id_proveedor+", '"+fecha_compra+"', " +
                "'"+fecha_ven_NotaVenta+"', '"+observacion+"', "+subtotal+", "+descuento+", "+iva_0+", "+iva_12+", "+total_pagado+", "+paramArtic+", " +
                "'"+num_serie_ret+"', '"+autorizacion_ret+"', "+num_retencion+", "+fecha_emision_ret+", "+ejercicio_fiscal+", "+total_retenido+", "+paramRet+", "+id_rol+");");
            if(res.next()){
                num = (res.getString(1)!=null) ? res.getString(1) : "-1:-1";
                res.close();
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return num;
    }

    public boolean anular(String idNotaVenta)
    {
        boolean ok = false;
        try{
            ResultSet res = this.consulta("select proc_anularNotaVenta("+idNotaVenta+")");
            if(res.next()){
                ok = (res.getString(1)!=null) ? res.getBoolean(1) : false;
                res.close();
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return ok;
    }


    /* RETENCIONES  */

    public ResultSet getRetencion(String id_factura_compra)
    {
        return this.consulta("select * from tbl_retencion_compra where anulado=false and id_factura_compra="+id_factura_compra+" and documento='v';");
    }

    public int getNumRetencion()
    {
        int num = 1;
        try{
            ResultSet res = this.consulta("SELECT max(ret_num_retencion) FROM tbl_retencion_compra;");
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

    public ResultSet getNotaVentaRetencion(String id_retencion)
    {
        return this.consulta("select N.razon_social, N.ruc, N.direccion, N.serie_nota as serie_factura,"
                + "N.num_nota_venta as num_factura, R.* from vta_nota_venta_compra as N inner join tbl_retencion_compra as R "
                + "on N.id_nota_venta_compra=R.id_factura_compra where R.id_retencion_compra="+id_retencion+" and R.documento='v';");
    }

    public ResultSet getRetencionDetalle(String id_retencion)
    {
        return this.consulta("select TR.*, R.id_retencion_compra_detalle, R.id_retencion_compra, R.base_imponible, R.valor_retenido, BI.campo_valor " +
                "from (tbl_retencion_compra_detalle as R inner join tbl_tabla_impuesto as TR on TR.id_tabla_impuesto=R.id_tabla_impuesto) "
                + "inner join tbl_retencion_base_imponible as BI on BI.codigo=R.codigo " +
                "where R.id_retencion_compra="+id_retencion+";");
    }

    public boolean anularRetencion(String idNotaVenta, String id_retencion)
    {
        boolean ok = false;
        try{
            ResultSet res = this.consulta("select proc_anularRetencionEmitida("+idNotaVenta+", "+id_retencion+");");
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
