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
import java.util.*;
import jm.web.DataBase;

/**
 *
 * @author Jorge
 */
public class Producto extends DataBase{
    public Producto(String m, int p, String db, String u, String c){
        super(m, p, db, u, c);
    }
    public ResultSet getProductos()
    {
        return this.consulta("SELECT id_producto, codigo, descripcion FROM vta_producto order by codigo;");
    }
    public ResultSet getBienes()
    {
        return this.consulta("SELECT id_producto, producto FROM vta_producto WHERE tipo='b' order by producto;");
    }
    public ResultSet getProductosImportacion()
    {
        return this.consulta("SELECT id_producto, codigo, descripcion, precio_costo FROM vta_producto where tipo in ('p', 'b') order by codigo;");
    }
    public ResultSet getPrecios(String id_producto)
    {
        return this.consulta("SELECT id_lista_precio, lista, utilidad from tbl_producto_precio where id_producto="+id_producto + " order by utilidad desc");
    }
    /* factura de compra */
    public ResultSet getProductos(String idProveedor)
    {
        return this.consulta("SELECT P.id_producto, P.codigo, P.descripcion, I.porcentaje " +
                "FROM (tbl_producto as P inner join tbl_proveedor_producto as PP on P.id_producto=PP.id_producto) "
                + "inner join tbl_iva as I on I.id_iva=P.id_iva " +
                "where PP.id_proveedor="+idProveedor+" order by P.descripcion;");
    }
    public ResultSet getProductoFiltro(String txt)
    {
        txt = txt.toLowerCase();
        return this.consulta("SELECT P.id_producto, P.codigo, P.descripcion, I.porcentaje " +
                "FROM tbl_producto as P inner join tbl_iva as I on I.id_iva=P.id_iva WHERE P.codigo='"+txt+"';");
    }
    public ResultSet getProductosFiltro(String txt)
    {
        txt = txt.toLowerCase();
        return this.consulta("SELECT P.id_producto, P.codigo, P.descripcion, I.porcentaje " +
                "FROM tbl_producto as P inner join tbl_iva as I on I.id_iva=P.id_iva " +
                "WHERE (lower(P.codigo) like '"+txt+"%' or lower(P.descripcion) like '%"+txt+"%') and eliminado=false order by P.descripcion limit 10 offset 0;");
    }

    /* pedidos, traspasos */
    public ResultSet getProductos(int id_sucursal)
    {
        return this.consulta("SELECT P.id_producto, P.codigo, P.descripcion, SP.stock_sucursal, P.precio_costo " +
                "FROM (vta_producto as P inner join tbl_sucursal_producto as SP on P.id_producto=SP.id_producto) " +
                "where SP.id_sucursal="+id_sucursal+" and tipo in ('p','b') order by P.codigo;");
    }
    public ResultSet getActivos(int id_sucursal)
    {
        return this.consulta("SELECT P.id_producto, P.codigo, P.descripcion, SP.stock_sucursal, P.precio_costo " +
                "FROM (vta_producto as P inner join tbl_sucursal_producto as SP on P.id_producto=SP.id_producto) " +
                "where SP.id_sucursal="+id_sucursal+" and tipo in ('p','b') order by P.codigo;");
    }

    /* pre-facturas de ISP */
    public ResultSet getProductoISP(int id_sucursal, String id)
    {
        return this.consulta("select P.id_producto, P.codigo, P.descripcion, SP.stock_sucursal, P.precio_costo, I.porcentaje, "+
                "max(case when tipo='s' then P.precio_venta_servicio else round((P.precio_costo + (P.precio_costo * PP.utilidad / 100)), 4) end),"+
                "SP.descuento, case when tipo='s' then '~' else '' end, round((P.precio_costo + (P.precio_costo * P.utilidad_min / 100)), 4) "+
                "FROM ((vta_producto as P inner join tbl_sucursal_producto as SP on P.id_producto=SP.id_producto) "+
                "inner join tbl_iva as I on I.id_iva=P.id_iva) "+
                "inner join tbl_producto_precio as PP on P.id_producto=PP.id_producto "+
                "where SP.id_sucursal="+id_sucursal+" and P.id_producto="+id+" "+
                "group by P.id_producto, P.codigo, P.descripcion, SP.stock_sucursal, P.precio_costo, I.porcentaje, case when tiene_iva then '~' else '' end, "+
                "SP.descuento, case when tipo='s' then '~' else '' end, round((P.precio_costo + (P.precio_costo * P.utilidad_min / 100)), 4) ");
    }
    
    /* para el anticipo internet */
    public ResultSet getProductoAnticipo(String id)
    {
        return this.consulta("select P.id_producto, P.codigo, P.descripcion, SP.stock_sucursal, P.precio_costo, I.porcentaje, "+
                "max(case when tipo='s' then P.precio_venta_servicio else round((P.precio_costo + (P.precio_costo * PP.utilidad / 100)), 4) end) as precio_venta,"+
                "SP.descuento, case when tipo='s' then '~' else '' end as de_servicio, round((P.precio_costo + (P.precio_costo * P.utilidad_min / 100)), 4) as ut_min "+
                "FROM ((vta_producto as P inner join tbl_sucursal_producto as SP on P.id_producto=SP.id_producto) "+
                "inner join tbl_iva as I on I.id_iva=P.id_iva) "+
                "inner join tbl_producto_precio as PP on P.id_producto=PP.id_producto "+
                "where P.id_producto="+id+" "+
                "group by P.id_producto, P.codigo, P.descripcion, SP.stock_sucursal, P.precio_costo, I.porcentaje, "+
                "SP.descuento, case when tipo='s' then '~' else '' end, round((P.precio_costo + (P.precio_costo * P.utilidad_min / 100)), 4) ");
    }

    /* facturas de ventas */
    public ResultSet getProductoVenta(int id_sucursal, String txt)
    {
        return this.consulta("select P.id_producto, P.codigo, P.descripcion, SP.stock_sucursal, P.precio_costo, I.porcentaje, "+
                "max(case when tipo='s' then P.precio_venta_servicio else round((P.precio_costo + (P.precio_costo * PP.utilidad / 100)), 4) end),"+
                "SP.descuento, case when tipo='s' then '~' else '' end, round((P.precio_costo + (P.precio_costo * P.utilidad_min / 100)), 4) "+
                "FROM ((vta_producto as P inner join tbl_sucursal_producto as SP on P.id_producto=SP.id_producto) "+
                "inner join tbl_iva as I on I.id_iva=P.id_iva) "+
                "inner join tbl_producto_precio as PP on P.id_producto=PP.id_producto "+
                "where SP.id_sucursal="+id_sucursal+" and P.tipo<>'g' and (P.codigo = '"+txt+"' or P.codigo_fabricante = '"+txt+"') "+
                "group by P.id_producto, P.codigo, P.descripcion, SP.stock_sucursal, P.precio_costo, I.porcentaje, "+
                "SP.descuento, case when tipo='s' then '~' else '' end, round((P.precio_costo + (P.precio_costo * P.utilidad_min / 100)), 4) ");
    }
    public ResultSet getProductosVenta(int id_sucursal, String txt)
    {
        txt = txt.toLowerCase();
        return this.consulta("select P.id_producto, P.codigo, P.descripcion, SP.stock_sucursal, P.precio_costo, I.porcentaje, "+
                "max(case when tipo='s' then P.precio_venta_servicio else round((P.precio_costo + (P.precio_costo * PP.utilidad / 100)), 4) end),"+
                "SP.descuento, case when tipo='s' then '~' else '' end, round((P.precio_costo + (P.precio_costo * P.utilidad_min / 100)), 4) "+
                "FROM ((vta_producto as P inner join tbl_sucursal_producto as SP on P.id_producto=SP.id_producto) "+
                "inner join tbl_iva as I on I.id_iva=P.id_iva) "+
                "inner join tbl_producto_precio as PP on P.id_producto=PP.id_producto "+
                "where SP.id_sucursal="+id_sucursal+" and P.tipo<>'g' and "+
                "(lower(P.codigo) like '"+txt+"%' or lower(P.codigo_fabricante) like '"+txt+"%' or lower(P.descripcion) like '%"+txt+"%') "+
                "group by P.id_producto, P.codigo, P.descripcion, SP.stock_sucursal, P.precio_costo, I.porcentaje, "+
                "SP.descuento, case when tipo='s' then '~' else '' end, round((P.precio_costo + (P.precio_costo * P.utilidad_min / 100)), 4) "+
                "order by P.codigo limit 10 offset 0");
    }
    /*public ResultSet getProductosVenta(int id_sucursal)
    {
        return this.consulta("select P.id_producto, P.codigo, P.descripcion, SP.stock_sucursal, P.precio_costo, case when tiene_iva then '~' else '' end, "+
                "case when tipo='s' then P.precio_venta_servicio else SP.precio_venta end, SP.descuento, case when tipo='s' then '~' else '' end, " +
                "U.utilidad FROM (vta_producto as P inner join tbl_sucursal_producto as SP on P.id_producto=SP.id_producto) "+
                "inner join tbl_lista_precio as U on SP.id_lista_precio=U.id_lista_precio " +
                "where SP.id_sucursal="+id_sucursal+" order by P.codigo;");
    }*/
    public ResultSet getProducto(String id)
    {
        return this.consulta("SELECT P.*, I.porcentaje FROM vta_producto as P inner join tbl_iva as I on I.id_iva=P.id_iva where id_producto="+id+";");
    }
    public boolean estaDuplicado(String id, String c)
    {
        ResultSet res = this.consulta("SELECT * FROM tbl_producto where codigo='"+c+"' and id_producto<>"+id+";");
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
    public ResultSet getProducto(String c1, String c2)
    {
        return this.consulta("SELECT id_producto, codigo, descripcion, I.porcentaje "
                + "FROM tbl_producto as P inner join tbl_iva as I on I.id_iva=P.id_iva "
                + "where (codigo='"+c1+"' or codigo_fabricante='"+c1+"') or (codigo='"+c2+"' or codigo_fabricante='"+c2+"')");

    }
    public boolean insertar(String codigo, String codigo_fabricante, String descripcion, String tipo, String utilidad_min, String unidad_medida, String id_categoria, 
            String stock_min, float stock, String precio_venta_servicio, String id_iva, String id_plan_cuenta_compra, String id_plan_cuenta_venta,
            String observacion, String proveedores, String sucursales, String descuentos, String ubicaciones, 
            String stocks, boolean aMatriz, int id_sucursal_sesion)
    {
        String id_producto = this.insert("INSERT INTO tbl_producto(codigo, codigo_fabricante, descripcion, tipo, utilidad_min, unidad_medida, id_categoria, "
               + "stock_min, stock, precio_venta_servicio, id_iva, id_plan_cuenta_compra, id_plan_cuenta_venta, observacion) " +
               "VALUES('"+codigo+"', '"+codigo_fabricante+"', '"+descripcion+"', '"+tipo+"', "+utilidad_min+", '"+unidad_medida+"', '"+id_categoria+"', "
               +stock_min+", "+stock+", "+precio_venta_servicio+", '"+id_iva+"', "+id_plan_cuenta_compra+", "+id_plan_cuenta_venta+", '"+observacion+"');");
        
        if(id_producto.compareTo("-1")!=0){
            List sql = new ArrayList();
            sql.add("INSERT INTO tbl_producto_precio(id_producto,lista,utilidad) VALUES("+id_producto+", 'CONSUMIDOR FINAL', "+(tipo.compareTo("s")==0?utilidad_min:"30")+")");
            if(proveedores.compareTo("")!=0){
                String vec[] = proveedores.split(",");
                for(int i=0; i<vec.length; i++){
                    sql.add("INSERT INTO tbl_proveedor_producto(id_proveedor, id_producto) values("+vec[i]+", "+id_producto+");");
                }
            }

            //if(tipo.compareTo("b")!=0){
                if(sucursales.compareTo("")!=0){
                    String vecSuc[] = sucursales.split(",");
                    //String vecLisPre[] = listas_precios.split(",");
                    String vecDesc[] = descuentos.split(",");
                    String vecUbic[] = ubicaciones.split(",");
                    String vecStocks[] = stocks.split(",");
                    for(int i=0; i<vecSuc.length; i++){
                        sql.add("INSERT INTO tbl_sucursal_producto(id_sucursal, id_producto, descuento, id_ubicacion, stock_sucursal) "
                                + "values("+vecSuc[i]+", "+id_producto+", "+vecDesc[i]+", '"+vecUbic[i]+"', "+vecStocks[i]+");");
                        if(aMatriz){
                            sql.add("INSERT INTO tbl_sucursal_producto(id_sucursal, id_producto, descuento, id_ubicacion, stock_sucursal) "
                                + "values("+id_sucursal_sesion+", "+id_producto+", "+vecDesc[i]+", '"+vecUbic[i]+"', "+vecStocks[i]+");");
                        }
                    }
                }
            //}
            
            return this.transacciones(sql);
        }
        return false;
    }
    public boolean actualizar(String id, String codigo, String codigo_fabricante, String descripcion, String tipo, String utilidad_min, String unidad_medida,
            String id_categoria, String stock_min, int stock, String precio_venta_servicio, String id_iva, String id_plan_cuenta_compra, String id_plan_cuenta_venta,
            String observacion, String proveedores, String sucursales, String descuentos, String ubicaciones, String stocks)
    {
        List sql = new ArrayList();

        if(proveedores.compareTo("")!=0){
            sql.add("DELETE FROM tbl_proveedor_producto WHERE id_producto="+id+";");
            String vec[] = proveedores.split(",");
            for(int i=0; i<vec.length; i++){
                sql.add("INSERT INTO tbl_proveedor_producto(id_proveedor, id_producto) values("+vec[i]+", "+id+");");
            }
        }
        
        sql.add("DELETE FROM tbl_sucursal_producto WHERE id_producto="+id+";");

        //if(tipo.compareTo("b")!=0){
            if(sucursales.compareTo("")!=0){
                String vecSuc[] = sucursales.split(",");
                //String vecLisPre[] = listas_precios.split(",");
                String vecDesc[] = descuentos.split(",");
                String vecUbic[] = ubicaciones.split(",");
                String vecStocks[] = stocks.split(",");
                for(int i=0; i<vecSuc.length; i++){
                    sql.add("INSERT INTO tbl_sucursal_producto(id_sucursal, id_producto, descuento, id_ubicacion, stock_sucursal) "
                            + "values("+vecSuc[i]+", "+id+", "+vecDesc[i]+", '"+vecUbic[i]+"', "+vecStocks[i]+");");
                }
            }
        //}
        /* porque el trigger de la tabla producto actualiza en la tabla tbl_sucursal_producto */
        sql.add("UPDATE tbl_producto SET codigo='"+codigo+"', codigo_fabricante='"+codigo_fabricante+"', descripcion='"+descripcion+"', tipo='"+tipo+"', "
                + "utilidad_min="+utilidad_min+", unidad_medida='"+unidad_medida+"', id_categoria='"+id_categoria+"', stock_min="+stock_min+", "
                + "stock="+stock+", precio_venta_servicio="+precio_venta_servicio+", id_iva='"+id_iva+"', id_plan_cuenta_compra="+id_plan_cuenta_compra+
                ", id_plan_cuenta_venta="+id_plan_cuenta_venta+", observacion='"+observacion+"' WHERE id_producto="+id+";");

        return this.transacciones(sql);
    }

    public boolean aplicarDescuento(String idSuc, String idPds, String descuento)
    {
        String w = " WHERE id_producto in ("+idPds+")";
        if(idSuc.compareTo("-0")!=0){
            w += " and id_sucursal="+idSuc;
        }
        return this.ejecutar("UPDATE tbl_sucursal_producto SET descuento="+descuento+w);
    }



    public ResultSet getKardex(String id, String fecha_ini, String fecha_fin)
    {
        return this.consulta("SELECT * FROM tbl_kardex where id_producto="+id+" and fecha between '"+fecha_ini+"' and '"+fecha_fin+"' order by id_kardex;");
    }

    public ResultSet getKardexSucursal(String idSuc, String id, String fecha_ini, String fecha_fin)
    {
        return this.consulta("SELECT * FROM tbl_kardex_sucursal where id_sucursal="+idSuc+" and id_producto="+id+" and fecha between '"+fecha_ini+"' and '"+fecha_fin+"' order by id_kardex_sucursal;");
    }

    public ResultSet getCodigoProveedores(String id)
    {
        return this.consulta("SELECT P.id_proveedor FROM tbl_proveedor as P inner join tbl_proveedor_producto as PA on " +
                "P.id_proveedor=PA.id_proveedor where PA.id_producto="+id+" order by P.id_proveedor;");
    }

    public ResultSet getCodigoSucursales(String id)
    {
        return this.consulta("SELECT id_sucursal, descuento, id_ubicacion, stock_sucursal FROM vta_sucursal_producto where id_producto="+id+" order by id_sucursal;");
    }

    public String concatenarValores(String idsArt, String cant, String vu, String vt)
    {
        String param = "";
        String vecproductos [] = idsArt.split(",");
        String cantidad [] = cant.split(",");
        String valorUnitario [] = vu.split(",");
        String valorTotal [] = vt.split(",");
        for(int i=0; i<vecproductos.length; i++){
            param += "['"+vecproductos[i]+"','"+cantidad[i]+"','"+valorUnitario[i]+"','"+valorTotal[i]+"'],";
        }
        param = param.substring(0, param.length()-1);
        return "array["+param+"]";
    }

    public int setInventarioInicial(String fecha, String inv, String idsArt, String cant, String vu, String vt)
    {
        int num = -1;
        try{
            String param = this.concatenarValores(idsArt, cant, vu, vt);
            ResultSet res = this.consulta("select proc_setInventarioInicial('"+fecha+"', '"+inv+"', "+param+");");
            if(res.next()){
                num = (res.getString(1)!=null) ? res.getInt(1) : -1;
                res.close();
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return num;
    }

}