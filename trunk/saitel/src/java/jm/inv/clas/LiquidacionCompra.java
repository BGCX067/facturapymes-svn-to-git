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
import jm.web.DataBase;
import jm.web.Fecha;

/**
 *
 * @author Jorge
 */
public class LiquidacionCompra extends DataBase{
    public LiquidacionCompra(String m, int p, String db, String u, String c){
        super(m, p, db, u, c);
    }
    public ResultSet getLiquidacion(String id)
    {
        return this.consulta("SELECT * FROM vta_liquidacion_compra where id_liquidacion_compra="+id);
    }

    public ResultSet getLiquidacionDetalle(String id)
    {
        return this.consulta("select * from vta_liquidacion_compra_detalle where id_liquidacion_compra="+id);
    }

    public long getNumLiquidacion(String id_sucursal)
    {
        int num = 1;
        try{
            ResultSet res = this.consulta("SELECT max(num_liquidacion) FROM tbl_liquidacion_compra WHERE id_sucursal="+id_sucursal);
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

    public ResultSet getLiquidacionesPendientes(String idC)
    {
        return this.consulta("SELECT * FROM vta_liquidacion_compra where id_proveedor="+idC+" and deuda>0 and anulado=false;");
    }

    public ResultSet getCobro(String idFC)
    {
        return this.consulta("SELECT * FROM tbl_liquidacion_compra_cobro where id_liquidacion_compra_cobro="+idFC);
    }

    public ResultSet getPagos(String idD)
    {
        return this.consulta("SELECT * FROM vta_liquidacion_compra_pago where id_comprobante_egreso="+idD);
    }

    public String verificarStock(String id_productos, String cantidades)
    {
        ResultSet rs = null;
        String codigo = "";
        int cantidad = 1;
        String vecProd [] = id_productos.split(",");
        String vecCant [] = cantidades.split(",");
        for(int i=0; i<vecProd.length; i++){
            try{
                rs = this.consulta("select codigo, stock - "+vecCant[i]+" from tbl_producto where id_producto="+vecProd[i]);
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

    public boolean LiquidacionDuplicada(String serie, String numero)
    {
        ResultSet res = this.consulta("SELECT * FROM tbl_liquidacion_compra where serie_liquidacion='"+serie+"' and num_liquidacion='"+numero+"' and anulado=false");
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

    public boolean LiquidacionHayEnMes(String id_proveedor, String fecha)
    {
        int anio = Fecha.datePart("anio", fecha);
        int mes = Fecha.datePart("mes", fecha);
        String fecha_ini = anio + "-" + mes + "-01";
        String fecha_fin = anio + "-" + mes + "-" + Fecha.getUltimoDiaMes(anio, mes);
        ResultSet res = this.consulta("SELECT * FROM tbl_liquidacion_compra where id_proveedor="+id_proveedor+" and "
                + "fecha_emision between '"+fecha_ini+"' and '"+fecha_fin+"' and anulado=false");
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

    public String concatenarValores(String id_articulos, String cantidades, String precios_unitarios,
            String subtotales, String ivas, String totales)
    {
        String param = "";
        String vecArti [] = id_articulos.split(",");
        String vecCant [] = cantidades.split(",");
        String vecPU [] = precios_unitarios.split(",");
        String vecSubt [] = subtotales.split(",");
        String vecIva [] = ivas.split(",");
        String vecTot [] = totales.split(",");
        for(int i=0; i<vecArti.length; i++){
            param += "['"+vecArti[i]+"','"+vecCant[i]+"','"+vecPU[i]+"','"+vecSubt[i]+"','"+vecIva[i]+"','"+vecTot[i]+"','FALSE'],";
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

    public String insertar(int id_sucursal, String usuario, String serie_liquidacion, String autorizacion, String num_liquidacion, String id_proveedor, String fecha_compra,
            String observacion, String subtotal, String subtotal_0, String subtotal_2, String iva_2, String total_pagado, String paramArtic, String sustento_tributario,
            String num_serie_ret, String autorizacion_ret, String num_retencion, String fecha_emision_ret, String ret_ejercicio_fiscal_mes, 
            String ejercicio_fiscal, String total_retenido, String paramRet, String paramAsiento, String id_rol)
    {
        String num = "-1:-1";
        try{
            ResultSet res = this.consulta("select liquidacionCompra("+id_sucursal+", '"+serie_liquidacion+"', '"+autorizacion+"', "+num_liquidacion+", "+
                    id_proveedor+", '"+fecha_compra+"', '"+observacion+"', "+subtotal+", "+subtotal_0+", "+subtotal_2+", "+iva_2+", "+total_pagado+
                    ", "+paramArtic+", '"+sustento_tributario+"', '"+num_serie_ret+"', '"+autorizacion_ret+"', "+num_retencion+", "+fecha_emision_ret+", '"+
                    ret_ejercicio_fiscal_mes+"', "+ejercicio_fiscal+", "+total_retenido+", "+paramRet+", "+paramAsiento+", "+id_rol+", '"+usuario+"');");
            if(res.next()){
                num = (res.getString(1)!=null) ? res.getString(1) : "-1:-1";
                res.close();
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return num;
    }
    
    public boolean actualizar(String id_retencion_compra, String sustento_tributario, String ret_ejercicio_fiscal_mes, 
            String ejercicio_fiscal, String total_retenido, String paramRet)
    {
        boolean ok = false;
        try{
            this.ejecutar("update tbl_retencion_compra set sustento_tributario='"+sustento_tributario+"', ret_ejercicio_fiscal="+
                ejercicio_fiscal+", ret_ejercicio_fiscal_mes='"+ret_ejercicio_fiscal_mes+"', ret_impuesto_retenido="+total_retenido+
                " where id_retencion_compra="+id_retencion_compra+" and documento='l'");
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

    public boolean anular(String idLiquidacion)
    {
        boolean ok = false;
        try{
            ResultSet res = this.consulta("select proc_anularLiquidacionCompra("+idLiquidacion+");");
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
        return this.consulta("select * from tbl_retencion_compra where anulado=false and id_factura_compra="+id_factura+" and documento='l';");
    }

    public ResultSet getLiquidacionRetencion(String id_retencion)
    {
        return this.consulta("select L.razon_social, L.ruc,L.direccion, L.serie_liquidacion as serie_factura,"
                + "L.num_liquidacion as num_factura, R.* from vta_liquidacion_compra as L inner join tbl_retencion_compra as R "
                + "on L.id_liquidacion_compra=R.id_factura_compra where R.id_retencion_compra="+id_retencion+" and R.documento='l';");
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
    
    public boolean setEstadoDocumento(String id_liquidacion_compra, String estado, String autorizacionXml, String numAutorizacion)
    {
        return this.ejecutar("update tbl_retencion_compra set estado_documento='"+estado+"', "
                + "documento_xml='"+autorizacionXml+"', numero_autorizacion='"+numAutorizacion+"' where id_factura_compra="+id_liquidacion_compra+" and documento='l'");
    }
    
    public boolean setEstadoDocumento(String id_liquidacion_compra, String estado, String claveAcceso, String autorizacionXml, String numAutorizacion)
    {
        return this.ejecutar("update tbl_retencion_compra set estado_documento='"+estado+"', clave_acceso='"+claveAcceso+"', "
                + "documento_xml='"+autorizacionXml+"', numero_autorizacion='"+numAutorizacion+"' "
                + "where id_factura_compra="+id_liquidacion_compra+" and documento='l'");
    }
    
}
