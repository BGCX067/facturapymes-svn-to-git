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
import java.util.ArrayList;
import java.util.List;
import jm.web.Addons;
import jm.web.DataBase;
import jm.web.Matriz;

/**
 *
 * @author Jorge
 */
public class FacturaCompra extends DataBase{
    public FacturaCompra(String m, int p, String db, String u, String c){
        super(m, p, db, u, c);
    }
    public ResultSet getFactura(String id)
    {
        return this.consulta("SELECT * FROM vta_factura_compra where id_factura_compra="+id+";");
    }
    
    public ResultSet getFacturasPendientes(int id_sucursal, String idP)
    {
        return this.consulta("SELECT * FROM vta_factura_compra where id_sucursal="+id_sucursal+" and id_proveedor="+idP+" and deuda>0 and anulado=false;");
    }
    
    public ResultSet getFacturaDetalle(String id)
    {
        return this.consulta("select D.*, P.codigo, P.descripcion, I.porcentaje "
                + "from (tbl_factura_compra_detalle as D inner join tbl_producto as P on P.id_producto=D.id_producto) "
                + "inner join tbl_iva as I on I.id_iva=P.id_iva "
                + "where D.id_factura_compra="+id+";");
    }
      
    public ResultSet getPago(String idFP)
    {
        return this.consulta("SELECT * FROM tbl_factura_compra_pago where id_factura_compra_pago="+idFP);
    }
    
    public ResultSet getPagos(String idD)
    {
        return this.consulta("SELECT * FROM vta_factura_compra_pago where id_comprobante_egreso="+idD+" and factura_anulada=false and pago_anulado=false");
    }

    public boolean hayPagos(String idFactura)
    {
        boolean ok = false;
        try{
            ResultSet res = this.consulta("select * from tbl_factura_compra_pago where anulado=false and id_factura_compra="+idFactura);
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
    
    public static String[][] getCuentasDistintas(String mat[][])
    {
        String distintos[][]=null;
        int i=0;
        int pos=-1;
        while (mat[i][1] != null) {
            pos = Matriz.enMatriz(distintos, mat[i][0], 0);  
            if (pos == -1) {
                distintos = Matriz.poner(mat, new String[] {mat[i][0], mat[i][1], "0"});
            }else{
                distintos[pos][1] = String.valueOf( Double.parseDouble(distintos[pos][1]) + Double.parseDouble(mat[i][1]) );
            }
            i++;
        }
        return distintos;
    }
    
    public static String[] getIvasDistintos(String mat[][], String id_plan_cuenta, String tipo)
    {
        int i=0;
        float sum_ivas = 0;
        while (mat[i][1] != null) {
            if (mat[i][1].compareTo(tipo)==0) {
                sum_ivas += Float.parseFloat(mat[i][5]);
            }
            i++;
        }
        String distintos[] = new String[] {id_plan_cuenta, String.valueOf(sum_ivas), "0"};
        return distintos;
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
            param += "['"+vecArti[i]+"','"+vecCant[i]+"','"+vecPU[i]+"','"+vecSubt[i]+"','"+vecDes[i]+"','"+vecIva[i]+"','"+vecTot[i]+"','FALSE'],";				    
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
    
    public boolean facturaDuplicada(String id, String id_proveedor, String serie, String numero)
    {
        ResultSet res = this.consulta("SELECT * FROM tbl_factura_compra where id_proveedor="+id_proveedor+" and serie_factura='"+serie+"' and num_factura='"+numero+
                "' and anulado=false and id_factura_compra<>"+id);
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
    
    public boolean retencionDuplicada(String id, String serie, String numero)
    {
        if(Long.parseLong(numero)>0){
            ResultSet res = this.consulta("SELECT * FROM tbl_retencion_compra where ret_num_serie='"+serie+"' and ret_num_retencion="+numero+
                    " and anulado=false and id_factura_compra<>"+id);
            if(this.getFilas(res)>0){
                return true;
            }
            try{
                res.close();
            }catch(Exception e){
                e.printStackTrace();
            }
        }
        return false;
    }

    public String insertar(int id_sucursal, String serie_factura, String autorizacion, String num_factura, String id_proveedor, String fecha_compra, String fecha_ven_factura,
                        String observacion, String subtotal, String subtotal_0, String subtotal_2, String subtotal_6, String descuento, String iva_2, float propina, String total_pagado, 
                        String paramArtic, String sustento_tributario, String num_serie_ret, String autorizacion_ret, String num_retencion, String fecha_emision_ret, String ret_ejercicio_fiscal_mes, String ejercicio_fiscal, 
                        String total_retenido, String paramRet, String paramAsiento, String id_rol, String xmlFirmado)
    {
        String num = "-1:-1";
        try{
            ResultSet res = this.consulta("select facturaCompra("+id_sucursal+", '"+serie_factura+"', '"+autorizacion+"', "+num_factura+", "+id_proveedor+", '"+fecha_compra+"', " +
                "'"+fecha_ven_factura+"', '"+observacion+"', "+subtotal+", "+subtotal_0+", "+subtotal_2+", "+subtotal_6+", "+descuento+", "+iva_2+", "+propina+", "+total_pagado+", "+paramArtic+", " +
                "'"+sustento_tributario+"', '"+num_serie_ret+"', '"+autorizacion_ret+"', "+num_retencion+", "+fecha_emision_ret+", '"+ret_ejercicio_fiscal_mes+"', "+ejercicio_fiscal+
                ", "+total_retenido+", "+paramRet+", "+paramAsiento+", "+id_rol+", '"+xmlFirmado+"');");
            if(res.next()){
                num = (res.getString(1)!=null) ? res.getString(1) : "-1:-1";
                res.close();
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return num;
    }

    public String contabilizar(String id_factura, int id_sucursal, String serie_factura, String autorizacion, String num_factura, String id_proveedor, String fecha_compra, String fecha_ven_factura,
                        String observacion, String subtotal, String subtotal_0, String subtotal_2, String subtotal_6, String descuento, String iva_2, float propina, String total_pagado, 
                        String paramArtic, String sustento_tributario, String num_serie_ret, String autorizacion_ret, String num_retencion, String fecha_emision_ret, String ret_ejercicio_fiscal_mes, String ejercicio_fiscal, 
                        String total_retenido, String paramRet, String paramAsiento, String id_rol, String xmlFirmado)
    {
        String num = "-1:-1";
        try{
            ResultSet res = this.consulta("select facturaCompraContabilizar("+id_factura+", "+id_sucursal+", '"+serie_factura+"', '"+autorizacion+"', "+num_factura+", "+id_proveedor+", '"+fecha_compra+"', " +
                "'"+fecha_ven_factura+"', '"+observacion+"', "+subtotal+", "+subtotal_0+", "+subtotal_2+", "+subtotal_6+", "+descuento+", "+iva_2+", "+propina+", "+total_pagado+", "+paramArtic+", " +
                "'"+sustento_tributario+"', '"+num_serie_ret+"', '"+autorizacion_ret+"', "+num_retencion+", "+fecha_emision_ret+", '"+ret_ejercicio_fiscal_mes+"', "+ejercicio_fiscal+
                ", "+total_retenido+", "+paramRet+", "+paramAsiento+", "+id_rol+", '"+xmlFirmado+"');");
            if(res.next()){
                num = (res.getString(1)!=null) ? res.getString(1) : "-1:-1";
                res.close();
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return num;
    }

    public String guardar(int id_sucursal, String serie_factura, String autorizacion, String num_factura, String id_proveedor, String fecha_compra, String fecha_ven_factura,
                        String observacion, String subtotal, String subtotal_0, String subtotal_2, String subtotal_6, String descuento, String iva_2, float propina, String total_pagado, 
                        String paramArtic, String num_serie_ret, String autorizacion_ret, String num_retencion, String fecha_emision_ret, String ret_ejercicio_fiscal_mes, String ejercicio_fiscal, 
                        String total_retenido, String paramRet)
    {
        double cxp = Addons.redondear(Float.valueOf(total_pagado) - Float.valueOf(total_retenido));
        String id_factura_compra = this.insert("INSERT INTO tbl_factura_compra(id_sucursal,id_proveedor,serie_factura,autorizacion,num_factura,fecha_ven_factura,"
                + "fecha,fecha_compra,subtotal,subtotal_0,subtotal_2,subtotal_6,descuento,iva_2,propina,total_compra,cxp,deuda,observacion, contabilizado) "
                + "VALUES("+id_sucursal+", "+id_proveedor+", '"+serie_factura+"', '"+autorizacion+"', "+num_factura+", '"+fecha_ven_factura+
                "', now(), '"+fecha_compra+"', "+subtotal+", "+subtotal_0+", "+subtotal_2+", "+subtotal_6+", "+descuento+", "+iva_2+
                ", "+propina+", "+total_pagado+", "+cxp+", 0, '"+observacion+"', false);");
        
        if(id_factura_compra.compareTo("-1")!=0){
            try{
                List sql = new ArrayList();
                paramArtic = paramArtic.replace("'", "").replace("],[", "_;_");

                String matArtic[] = paramArtic.split("_;_");
                for(int i=0; i<matArtic.length; i++){
                    String vecArti[] = matArtic[i].replace("[", "").replace("]", "").split(",");
                    sql.add("insert into tbl_factura_compra_detalle(id_factura_compra, id_producto, cantidad, p_u, p_st, descuento, iva, total) "
                            + "values("+id_factura_compra+", "+vecArti[0]+", "+vecArti[1]+", "+vecArti[2]+", "+vecArti[3]+", "+vecArti[4]+", "+vecArti[5]+", "+vecArti[6]+");");
                }
                boolean reg_factura = this.transacciones(sql);
                /*if(reg_factura){
                    if(fecha_emision_ret.compareTo("NULL")!=0 && total_retenido.compareTo("")!=0 && total_retenido.compareTo("0")!=0){
                        String id_retencion = this.insert("INSERT INTO tbl_retencion_compra(id_factura_compra, ret_num_serie, ret_autorizacion, ret_num_retencion, ret_fecha_emision, ret_ejercicio_fiscal, ret_impuesto_retenido, documento) "
                                + "VALUES("+id_factura_compra+", '"+num_serie_ret+"', '"+autorizacion_ret+"', "+num_retencion+", "+fecha_emision_ret+", "+ejercicio_fiscal+", "+total_retenido+", 'f')");
                        sql.clear();
                        String vecRet [] = id_retenciones.split(",");
                        String vecBI [] = bases_imponibles.split(",");
                        String vecVal [] = valores_retenidos.split(",");
                        for(int i=0; i<vecRet.length; i++){
                            sql.add("INSERT INTO tbl_retencion_compra_detalle(id_retencion_compra, id_tabla_impuesto, base_imponible, valor_retenido) "
                                    + "values("+id_retencion+", "+vecRet[i]+", "+vecBI[i]+", "+vecVal[i]+");");
                        }
                        this.transacciones(sql);
                    }
                }*/
            }catch(Exception e){
                this.ejecutar("delete from tbl_factura_compra where id_factura_compra="+id_factura_compra);
                this.setError(e.getMessage());
                return "-1";
            }
            
        }
        return id_factura_compra;
    }

    public boolean actualizar(String id_factura_compra, String serie_factura, String autorizacion, String num_factura, String id_proveedor, String fecha_compra, String fecha_ven_factura,
                        String observacion, String subtotal, String subtotal_0, String subtotal_2, String subtotal_6, String descuento, String iva_2, float propina, String total_pagado, 
                        String paramArtic, String num_serie_ret, String autorizacion_ret, String num_retencion, String fecha_emision_ret, String ret_ejercicio_fiscal_mes, String ejercicio_fiscal, 
                        String total_retenido, String paramRet)
    {
        double cxp = Addons.redondear(Float.valueOf(total_pagado) - Float.valueOf(total_retenido));
        /*String id_retencion_compra = "";
        try{
            ResultSet rs = this.consulta("select id_retencion_compra from tbl_factura_compra where id_factura_compra="+id_factura_compra);
            if(rs.next()){
                id_retencion_compra = rs.getString("id_retencion_compra") != null ? rs.getString("id_retencion_compra") : "";
                rs.close();
            }
        }catch(Exception e){}*/
        List sql = new ArrayList();
        sql.add("UPDATE tbl_factura_compra SET id_proveedor="+id_proveedor+", serie_factura='"+serie_factura+"', autorizacion='"+autorizacion+"', "
                + "num_factura="+num_factura+", fecha_ven_factura='"+fecha_ven_factura+"', fecha_compra='"+fecha_compra+"', subtotal="+subtotal+
                ", subtotal_0="+subtotal_0+", subtotal_2="+subtotal_2+", subtotal_6="+subtotal_6+", descuento="+descuento+
                ", iva_2="+iva_2+", propina="+propina+", total_compra="+total_pagado+", cxp="+cxp+", deuda=0, "
                + "observacion='"+observacion+"', contabilizado=false WHERE id_factura_compra="+id_factura_compra+";");
        
        sql.add("DELETE FROM tbl_factura_compra_detalle WHERE id_factura_compra="+id_factura_compra+";");
        if(id_factura_compra.compareTo("-1")!=0){
            paramArtic = paramArtic.replace("'", "").replace("],[", "_;_");

            String matArtic[] = paramArtic.split("_;_");
            for(int i=0; i<matArtic.length; i++){
                String vecArti[] = matArtic[i].replace("[", "").replace("]", "").split(",");
                sql.add("insert into tbl_factura_compra_detalle(id_factura_compra, id_producto, cantidad, p_u, p_st, descuento, iva, total) "
                        + "values("+id_factura_compra+", "+vecArti[0]+", "+vecArti[1]+", "+vecArti[2]+", "+vecArti[3]+", "+vecArti[4]+", "+vecArti[5]+", "+vecArti[6]+");");
            }
            /*if(fecha_emision_ret.compareTo("NULL")!=0 && total_retenido.compareTo("")!=0 && total_retenido.compareTo("0")!=0){
                if(id_retencion_compra.compareTo("")==0){
                    id_retencion_compra = this.insert("INSERT INTO tbl_retencion_compra(id_factura_compra, ret_num_serie, ret_autorizacion, ret_num_retencion, ret_fecha_emision, ret_ejercicio_fiscal, ret_impuesto_retenido, documento) "
                            + "VALUES("+id_factura_compra+", '"+num_serie_ret+"', '"+autorizacion_ret+"', "+num_retencion+", "+fecha_emision_ret+", "+ejercicio_fiscal+", "+total_retenido+", 'f')");
                }else{
                    sql.add("UPDATE tbl_retencion_compra SET ret_num_serie='"+num_serie_ret+"', ret_autorizacion='"+autorizacion_ret+
                        "', ret_num_retencion="+num_retencion+", ret_fecha_emision='"+fecha_emision_ret+"', ret_ejercicio_fiscal="+ejercicio_fiscal+
                        ", ret_impuesto_retenido="+total_retenido+" WHERE id_retencion_compra="+id_retencion_compra+";");
                }
                sql.add("DELETE FROM tbl_retencion_compra_detalle WHERE id_retencion_compra="+id_retencion_compra+";");
                String vecRet [] = id_retenciones.split(",");
                String vecBI [] = bases_imponibles.split(",");
                String vecVal [] = valores_retenidos.split(",");
                for(int i=0; i<vecRet.length; i++){
                    sql.add("INSERT INTO tbl_retencion_compra_detalle(id_retencion_compra, id_tabla_impuesto, base_imponible, valor_retenido) "
                            + "values("+id_retencion_compra+", "+vecRet[i]+", "+vecBI[i]+", "+vecVal[i]+");");
                }
            }*/
            return this.transacciones(sql);
        }
        return false;
    }
    
    /*
    public String actualizar(int id_sucursal, String id, String id_comprobante_diario, String serie_factura, String autorizacion, String num_factura, String id_proveedor, String fecha_compra, String fecha_ven_factura,
                        String observacion, String subtotal, String descuento, String iva_0, String iva_12, String total_pagado, 
                        String id_articulos, String cantidades, String precios_unitarios, String subtotales, String descuentos, String ivas, String totales,
                        String num_serie_ret, String autorizacion_ret, String num_retencion, String fecha_emision_ret, String ejercicio_fiscal, String total_retenido, 
                        String id_retenciones, String bases_imponibles, String valores_retenidos, String id_rol)
    {
        String num = "-1:-1";
        try{
            String paramArtic = this.concatenarValores(id_articulos, cantidades, precios_unitarios, subtotales, descuentos, ivas, totales);
            String paramRet = this.concatenarValores(id_retenciones, bases_imponibles, valores_retenidos);
            ResultSet res = this.consulta("select proc_editarFacturaCompra("+id_sucursal+", "+id+", "+id_comprobante_diario+", '"+serie_factura+"','"+autorizacion+"', "+num_factura+", "+id_proveedor+", '"+fecha_compra+"', " +
                "'"+fecha_ven_factura+"', '"+observacion+"', "+subtotal+", "+descuento+", "+iva_0+", "+iva_12+", "+total_pagado+", "+paramArtic+", " +
                "'"+num_serie_ret+"', '"+autorizacion_ret+"', "+num_retencion+", "+fecha_emision_ret+", "+ejercicio_fiscal+", "+total_retenido+", "+paramRet+", "+id_rol+");");
            if(res.next()){
                num = (res.getString(1)!=null) ? res.getString(1) : "-1:-1";
                res.close();
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return num;
    }*/

    /* ACTIVOS 
    public String insertar(int id_sucursal, String serie_factura, String autorizacion, String num_factura, String id_proveedor, String fecha_compra, String fecha_ven_factura,
                        String observacion, String subtotal, String descuento, String iva_0, String iva_12, String total_pagado,
                        String id_articulos, String cantidades, String precios_unitarios, String subtotales, String descuentos, String ivas, String totales,
                        String num_serie_ret, String autorizacion_ret, String num_retencion, String fecha_emision_ret, String ejercicio_fiscal, String total_retenido,
                        String id_retenciones, String bases_imponibles, String valores_retenidos, String id_rol,
                        String id_empleado, String codigo_activos, String descripciones, String depresiaciones, String categorias, String ubicaciones, String costos)
    {
        String num = "-1:-1:-1";
        try{
            String paramArtic = this.concatenarValores(id_articulos, cantidades, precios_unitarios, subtotales, descuentos, ivas, totales);
            String paramRet = this.concatenarValores(id_retenciones, bases_imponibles, valores_retenidos);
            String paramAct = this.concatenarValores(codigo_activos, descripciones, depresiaciones, categorias, ubicaciones, costos);
            ResultSet res = this.consulta("select proc_facturaActivo("+id_sucursal+", '"+serie_factura+"', '"+autorizacion+"', "+num_factura+", "+id_proveedor+", '"+fecha_compra+"', " +
                "'"+fecha_ven_factura+"', '"+observacion+"', "+subtotal+", "+descuento+", "+iva_0+", "+iva_12+", "+total_pagado+", "+paramArtic+", " +
                "'"+num_serie_ret+"', '"+autorizacion_ret+"', "+num_retencion+", "+fecha_emision_ret+", "+ejercicio_fiscal+", "+total_retenido+", "+paramRet+", "+id_rol+
                ","+id_empleado+", "+paramAct+");");
            if(res.next()){
                num = (res.getString(1)!=null) ? res.getString(1) : "-1:-1:-1";
                res.close();
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return num;
    }

    public String actualizar(int id_sucursal, String id, String id_comprobante_diario, String serie_factura, String autorizacion, String num_factura, String id_proveedor, String fecha_compra, String fecha_ven_factura,
                        String observacion, String subtotal, String descuento, String iva_0, String iva_12, String total_pagado,
                        String id_articulos, String cantidades, String precios_unitarios, String subtotales, String descuentos, String ivas, String totales,
                        String num_serie_ret, String autorizacion_ret, String num_retencion, String fecha_emision_ret, String ejercicio_fiscal, String total_retenido,
                        String id_retenciones, String bases_imponibles, String valores_retenidos, String id_rol,
                        String id_empleado, String codigo_activos, String descripciones, String depresiaciones, String categorias, String ubicaciones, String costos)
    {
        String num = "-1:-1:-1";
        try{
            String paramArtic = this.concatenarValores(id_articulos, cantidades, precios_unitarios, subtotales, descuentos, ivas, totales);
            String paramRet = this.concatenarValores(id_retenciones, bases_imponibles, valores_retenidos);
            String paramAct = this.concatenarValores(codigo_activos, descripciones, depresiaciones, categorias, ubicaciones, costos);
            ResultSet res = this.consulta("select proc_editarFacturaActivo("+id_sucursal+", "+id+", "+id_comprobante_diario+", '"+serie_factura+"','"+autorizacion+"', "+num_factura+", "+id_proveedor+", '"+fecha_compra+"', " +
                "'"+fecha_ven_factura+"', '"+observacion+"', "+subtotal+", "+descuento+", "+iva_0+", "+iva_12+", "+total_pagado+", "+paramArtic+", " +
                "'"+num_serie_ret+"', '"+autorizacion_ret+"', "+num_retencion+", "+fecha_emision_ret+", "+ejercicio_fiscal+", "+total_retenido+", "+paramRet+", "+id_rol+
                ", "+id_empleado+", "+paramAct+");");
            if(res.next()){
                num = (res.getString(1)!=null) ? res.getString(1) : "-1:-1:-1";
                res.close();
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return num;
    }
    */
    public boolean anular(String idFactura, String desc_kardex)
    {
        boolean ok = false;
        try{
            ResultSet res = this.consulta("select proc_anularFacturaCompra("+idFactura+", "+desc_kardex+");");
            if(res.next()){
                ok = (res.getString(1)!=null) ? res.getBoolean(1) : false;
                res.close();
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return ok;
    } 

    public boolean anular(String idFactura)
    {
        boolean ok = false;
        try{
            ResultSet res = this.consulta("select proc_anularFacturaActivo("+idFactura+")");
            if(res.next()){
                ok = (res.getString(1)!=null) ? res.getBoolean(1) : false;
                res.close();
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return ok;
    }



        /*  ACTIVOS */

    public ResultSet getFacturaDetalleActivos(String id)
    {
        return this.consulta("select * from tbl_factura_compra_activo_detalle where id_factura_compra="+id+" and de_activo=true");
    }

    public ResultSet getFacturaDetalleProdsServs(String id)
    {
        return this.consulta("select D.*,P.codigo,P.descripcion as descripcion_p, P.tiene_iva from tbl_factura_compra_activo_detalle as D inner join tbl_producto as P on P.id_producto=D.codigos_series::int where D.id_factura_compra="+id+" and D.de_activo=false");
    }

    public ResultSet getFacturaPersonalizacion(String id)
    {
        return this.consulta("select * from tbl_factura_compra_personalizacion where id_factura_compra="+id);
    }

    public boolean anularCompraActivos(String idFactura)
    {
        boolean ok = false;
        try{
            ResultSet res = this.consulta("select proc_anularFacturaCompraActivo("+idFactura+");");
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

    public ResultSet getRetencion(String id_factura)
    {
        return this.consulta("select * from tbl_retencion_compra where anulado=false and id_factura_compra="+id_factura+" and documento='f';");
    }

    public int getNumRetencionSaltado(String serie_retencion)
    {
        int num = 0;
        try{
            ResultSet res = this.consulta("SELECT min(num_retencion) FROM tbl_retencion_compra_numero where serie_retencion='"+serie_retencion+"' and consumido=false");
            if(res.next()){
                num = (res.getString(1)!=null) ? res.getInt(1) : 0;
                res.close();
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return num;
    }
    
    public int getNumRetencion(String id_sucursal)
    {
        int num = 1;
        try{
            ResultSet res = this.consulta("SELECT max(ret_num_retencion) FROM tbl_retencion_compra where id_sucursal="+id_sucursal);
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

    public String getClaveAcceso(String id_retencion)
    {
        String num = "0";
        try{
            ResultSet res = this.consulta("SELECT clave_acceso FROM tbl_retencion_compra WHERE id_retencion_compra="+id_retencion);
            if(res.next()){
                num = (res.getString(1)!=null) ? res.getString(1) : "0";
                res.close();
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return num;
    }
    
    public ResultSet getFacturaRetencion(String id_retencion)
    {
        return this.consulta("select * from vta_factura_compra_retencion where id_retencion_compra="+id_retencion+";");
    }

    public ResultSet getRetencionDetalle(String id_retencion)
    {
        return this.consulta("select TR.*, R.id_retencion_compra_detalle, R.id_retencion_compra, R.base_imponible, R.valor_retenido, BI.campo_valor, "
                + "R.codigo, R.id_tabla_impuesto " +
                "from (tbl_retencion_compra_detalle as R inner join tbl_tabla_impuesto as TR on TR.id_tabla_impuesto=R.id_tabla_impuesto) " + 
                "inner join tbl_retencion_base_imponible as BI on BI.codigo=R.codigo " +
                "where R.id_retencion_compra="+id_retencion+";");
    }

    /*public boolean setClaveAcceso(String claveAcceso)
    {
        return this.ejecutar("update tbl_retencion_compra set clave_acceso='"+claveAcceso+"'");
    }*/
    
    public boolean setNumeroAutorizacion(String num_autorizacion)
    {
        return this.ejecutar("update tbl_retencion_compra set numero_autorizacion='"+num_autorizacion+"'");
    }
    
    public boolean anularRetencion(String idFactura, String id_retencion)
    {
        boolean ok = false;
        try{
            ResultSet res = this.consulta("select proc_anularRetencionEmitida("+idFactura+", "+id_retencion+");");
            if(res.next()){
                ok = (res.getString(1)!=null) ? res.getBoolean(1) : false;
                res.close();
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return ok;
    }

    public String getTipoDocumento(String id_retencion_compra)
    {
        String num = "";
        try{
            ResultSet res = this.consulta("SELECT documento FROM tbl_retencion_compra WHERE id_retencion_compra="+id_retencion_compra);
            if(res.next()){
                num = (res.getString(1)!=null) ? res.getString(1) : "";
                res.close();
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return num;
    }
    
    public boolean setEstadoDocumento(String id_liquidacion_compra, String estado, String autorizacionXml, String numAutorizacion)
    {
        return this.ejecutar("update tbl_retencion_compra set estado_documento='"+estado+"', documento_xml='"+autorizacionXml+
            "', numero_autorizacion='"+numAutorizacion+"' where id_factura_compra="+id_liquidacion_compra+" and documento='f'");
    }
    
    public boolean setEstadoDocumento(String id_liquidacion_compra, String documento, String estado, String claveAcceso, String autorizacionXml, String numAutorizacion)
    {
        return this.ejecutar("update tbl_retencion_compra set estado_documento='"+estado+"', clave_acceso='"+claveAcceso+"', "
                + "documento_xml='"+autorizacionXml+"', numero_autorizacion='"+numAutorizacion+"' "
                + "where id_factura_compra="+id_liquidacion_compra+" and documento='"+documento+"' and documento<>'a'");
    }
    
    public boolean setClaveAcceso(String id_liquidacion_compra, String claveAcceso)
    {
        return this.ejecutar("update tbl_retencion_compra set clave_acceso='"+claveAcceso+"' "
                + "where id_factura_compra="+id_liquidacion_compra+" and documento='f'");
    }
    
    public boolean actualizar(String id_retencion_compra, String ret_num_retencion, String ret_fecha_emision, String sustento_tributario, 
            String ret_ejercicio_fiscal_mes, String ejercicio_fiscal, String total_retenido, String paramRet)
    {
        boolean ok = false;
        try{
            this.ejecutar("update tbl_retencion_compra set ret_num_retencion="+ret_num_retencion+", ret_fecha_emision="+ret_fecha_emision+", sustento_tributario='"+sustento_tributario+"', ret_ejercicio_fiscal="+
                ejercicio_fiscal+", ret_ejercicio_fiscal_mes='"+ret_ejercicio_fiscal_mes+"', ret_impuesto_retenido="+total_retenido+
                " where id_retencion_compra="+id_retencion_compra+" and documento<>'a'");
            List sql = new ArrayList();
            sql.add("delete from tbl_retencion_compra_detalle where id_retencion_compra="+id_retencion_compra+";");
            paramRet = paramRet.replace("'],['", ";").replace("'", "");
            String matriz [] = paramRet.split(";");
            for(int i=0; i<matriz.length; i++){
                String retencion[] = matriz[i].split(",");
                sql.add("INSERT INTO tbl_retencion_compra_detalle(id_retencion_compra, id_tabla_impuesto, base_imponible, valor_retenido, codigo) "
                    + "values("+id_retencion_compra+", "+retencion[0].replace("[", "").trim()+", "+retencion[1].trim()+", "+retencion[2].trim()+
                    ", "+retencion[3].replace("]", "").trim()+" );");
            }
            return this.transacciones(sql);
        }catch(Exception e){
            e.printStackTrace();
        }
        return ok;
    }
    
    
    
    /* IMPORTACIONES */

    public String insertar(int id_sucursal, String idDau, String serie_factura, String autorizacion, String num_factura, String id_proveedor, String fecha_compra, String fecha_ven_factura,
                        String observacion, String subtotal, String descuento, String iva_0, String iva_12, String total_pagado,
                        String id_articulos, String cantidades, String precios_unitarios, String subtotales, String descuentos, String ivas, String totales,
                        String num_serie_ret, String autorizacion_ret, String num_retencion, String fecha_emision_ret, String ret_ejercicio_fiscal_mes, String ejercicio_fiscal, String total_retenido,
                        String id_retenciones, String bases_imponibles, String valores_retenidos, String id_rol)
    {
        String num = "-1:-1";
        try{
            String paramArtic = this.concatenarValores(id_articulos, cantidades, precios_unitarios, subtotales, descuentos, ivas, totales);
            String paramRet = this.concatenarValores(id_retenciones, bases_imponibles, valores_retenidos);
            ResultSet res = this.consulta("select proc_facturaCostosImportacion("+id_sucursal+", "+idDau+", '"+serie_factura+"', '"+autorizacion+"', "+num_factura+", "+id_proveedor+", '"+fecha_compra+"', " +
                "'"+fecha_ven_factura+"', '"+observacion+"', "+subtotal+", "+descuento+", "+iva_0+", "+iva_12+", "+total_pagado+", "+paramArtic+", " +
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

    public String actualizar(int id_sucursal, String id, String idDau, String id_comprobante_diario, String serie_factura, String autorizacion, String num_factura, String id_proveedor, String fecha_compra, String fecha_ven_factura,
                        String observacion, String subtotal, String descuento, String iva_0, String iva_12, String total_pagado,
                        String id_articulos, String cantidades, String precios_unitarios, String subtotales, String descuentos, String ivas, String totales,
                        String num_serie_ret, String autorizacion_ret, String num_retencion, String fecha_emision_ret, String ejercicio_fiscal, String total_retenido,
                        String id_retenciones, String bases_imponibles, String valores_retenidos, String id_rol)
    {
        String num = "-1:-1";
        try{
            String paramArtic = this.concatenarValores(id_articulos, cantidades, precios_unitarios, subtotales, descuentos, ivas, totales);
            String paramRet = this.concatenarValores(id_retenciones, bases_imponibles, valores_retenidos);
            ResultSet res = this.consulta("select proc_editarFacturaCostosImportacion("+id_sucursal+", "+id+", "+idDau+", "+id_comprobante_diario+", '"+serie_factura+"','"+autorizacion+"', "+num_factura+", "+id_proveedor+", '"+fecha_compra+"', " +
                "'"+fecha_ven_factura+"', '"+observacion+"', "+subtotal+", "+descuento+", "+iva_0+", "+iva_12+", "+total_pagado+", "+paramArtic+", " +
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
}
