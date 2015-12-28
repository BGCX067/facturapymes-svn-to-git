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

package jm.cnt.clas;
import java.sql.ResultSet;
import jm.web.DataBase;

public class ComprobanteIngreso extends DataBase{
    public ComprobanteIngreso(String m, int p, String db, String u, String c){
        super(m, p, db, u, c);
    }
    
    public int getNumComprobante()
    {
        int num = 1;
        try{
            ResultSet res = this.consulta("SELECT max(num_comprobante) FROM tbl_comprobante_ingreso;");
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

    public boolean contabilizar(String usuario)
    {
        boolean ok = false;
        try{
            ResultSet res = this.consulta("select contabilizarVentas('"+usuario+"')");
            if(res.next()){
                ok = (res.getString(1)!=null) ? res.getBoolean(1) : false;
                res.close();
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return ok;
    }
    
    public boolean sinContabilizar(String usuario)
    {
        try{
            ResultSet res = this.consulta("SELECT fecha_emision FROM tbl_factura_venta WHERE contabilizado=false and anulado=false and vendedor='"+usuario+"' "
                    + " union "
                    + "select fecha_actual as fecha_emision from tbl_comprobante_ingreso WHERE contabilizado=false and cajero='"+usuario+"'");
            if(this.getFilas(res)>0){
                return true;
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return false;
    }
    
    public ResultSet getComprobanteIngreso(String id)
    {
        return this.consulta("SELECT * FROM tbl_comprobante_ingreso where id_comprobante_ingreso="+id+";");
    }

    public ResultSet getComprobantesIngresoCheques(String desde, String hora, String cajero)
    {
        return this.consulta("SELECT CI.*, F.serie_factura || '-' || num_factura as numero_factura FROM "
                + "(tbl_comprobante_ingreso as CI inner join tbl_factura_venta_cobro as FC on CI.id_comprobante_ingreso=FC.id_comprobante_ingreso) "
                + "inner join tbl_factura_venta as F on F.id_factura_venta=FC.id_factura_venta "
                + "where CI.tipo_cobro='c' and (((CI.fecha_actual || ' ' || CI.hora)::timestamp >= '"+desde+" "+hora+"'::timestamp) or "
                + "((CI.fecha_en_efectivo || ' ' || CI.hora)::timestamp >='"+desde+" "+hora+"'::timestamp)) and CI.anulado=false and CI.cajero='"+cajero+"' order by CI.id_comprobante_ingreso");
    }

    public ResultSet getComprobantesIngreso(String tipo, String desde, String hora, String cajero)
    {
        return this.consulta("SELECT CI.*, F.serie_factura || '-' || num_factura as numero_factura FROM (tbl_comprobante_ingreso as CI inner join tbl_factura_venta_cobro as FC on CI.id_comprobante_ingreso=FC.id_comprobante_ingreso) "
                + "inner join tbl_factura_venta as F on F.id_factura_venta=FC.id_factura_venta "
                + "where CI.tipo_cobro in ("+tipo+") and (CI.fecha_actual || ' ' || CI.hora)::timestamp >= '"+desde+" "+hora+"'::timestamp and CI.anulado=false and CI.cajero='"+cajero+"' order by CI.id_comprobante_ingreso");
    }
    
    public ResultSet getCbros(String tipo, String desde, String hora, String cajero)
    {
        return this.consulta("SELECT CI.*, F.serie_factura || '-' || num_factura as numero_factura FROM (tbl_comprobante_ingreso as CI inner join tbl_factura_venta_cobro as FC on CI.id_comprobante_ingreso=FC.id_comprobante_ingreso) "
                + "inner join tbl_factura_venta as F on F.id_factura_venta=FC.id_factura_venta "
                + "where F.forma_pago='d' and CI.tipo_cobro in ("+tipo+") and (CI.fecha_actual || ' ' || CI.hora)::timestamp >= '"+desde+" "+hora+"'::timestamp and CI.anulado=false and CI.cajero='"+cajero+"' order by CI.id_comprobante_ingreso");
    }

    public ResultSet getComprobanteIngresoDetalle(String id)
    {
        return this.consulta("select PC.id_plan_cuenta, PC.codigo_cuenta, PC.nombre_cuenta, D.debe, D.haber " +
                "from tbl_comprobante_ingreso_detalle as D inner join tbl_plan_cuenta as PC on D.id_plan_cuenta=PC.id_plan_cuenta " +
                "where D.id_comprobante_ingreso="+id+" order by D.id_comprobante_ingreso_detalle;");
    }
    
    public ResultSet getChequesNoCobrados()
    {
        return this.consulta("SELECT * FROM tbl_comprobante_ingreso where (fecha_en_efectivo is null or fecha_en_efectivo::varchar = '') and tipo_cobro='c' and anulado=false;");
    }
    
    public String concatenarValores(String idsCuentas, String debe, String haber)
    {
        String param = "";
        String vecCuentas [] = idsCuentas.split(",");
        String vecDebe [] = debe.split(",");
        String vecHaber [] = haber.split(",");
        for(int i=0; i<vecCuentas.length; i++){
            param += "['"+vecCuentas[i]+"','"+vecDebe[i]+"','"+vecHaber[i]+"'],";				    
        }
        param = param.substring(0, param.length()-1);
        return "array["+param+"]";
    }
    
    public String concatenarValores(String id_retenciones, String bases_imponibles, String valores_retenidos, String codBI)
    {
        String param = "";
        String vecIdsRet [] = id_retenciones.split(",");
        String vecBasImp [] = bases_imponibles.split(",");
        String vecValRet [] = valores_retenidos.split(",");
        String vecCodBI [] = codBI.split(",");
        for(int i=0; i<vecIdsRet.length; i++){
            param += "['"+vecIdsRet[i]+"','"+vecBasImp[i]+"','"+vecValRet[i]+"','"+vecCodBI[i]+"'],";				    
        }
        param = param.substring(0, param.length()-1);
        return "array["+param+"]";
    }
    
    public String concatenarValores(String idsFacts, String abonos)
    {
        String param = "";
        String vecIdsFacts [] = idsFacts.split(",");
        String vecAbonos [] = abonos.split(",");
        for(int i=0; i<vecIdsFacts.length; i++){
            param += "['"+vecIdsFacts[i]+"','"+vecAbonos[i]+"'],";				    
        }
        param = param.substring(0, param.length()-1);
        return "array["+param+"]";
    }
    
    public int insertar(int id_sucursal, String usuario, long numero_doc, String fecha_proceso, String fecha_efectivo, String nombre, String tipo_cobro, String num_cheque,
            String banco, String num_comp_pago, String gastos_bancos, String total, String valor_son, String son, String detalle, String idsCuentas, String debe, String haber)
    {
        int num = -1;
        fecha_efectivo = (fecha_efectivo.compareTo("")==0) ? "NULL" : "'"+fecha_efectivo+"'";
        try{
            String param = this.concatenarValores(idsCuentas, debe, haber);
            ResultSet res = this.consulta("select proc_comprobanteIngreso("+id_sucursal+", '"+usuario+"', "+numero_doc+", '"+fecha_proceso+"', "+fecha_efectivo+", '"+nombre+
                "', '"+tipo_cobro+"', '"+num_cheque+"', '"+banco+"', '"+num_comp_pago+"', "+gastos_bancos+", "+total+", "+valor_son+", '"+son+"', '"+detalle+"', "+param+");");
            if(res.next()){
                num = (res.getString(1)!=null) ? res.getInt(1) : -1;
                res.close();
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return num;
    }
    
    /*public int actualizar(String id, String fecha_proceso, String fecha_efectivo, String nombre, String tipo_cobro, String num_cheque,
            String banco, String num_comp_pago, String gastos_bancos, String total, String son, String detalle, String idsCuentas, String debe, String haber)
    {
        int num = -1;
        fecha_efectivo = (fecha_efectivo.compareTo("")==0) ? "NULL" : "'"+fecha_efectivo+"'";
        try{
            String param = this.concatenarValores(idsCuentas, debe, haber);
            ResultSet res = this.consulta("select proc_editarComprobanteIngreso("+id+", '"+fecha_proceso+"', "+fecha_efectivo+", '"+nombre+
                "', '"+tipo_cobro+"', '"+num_cheque+"', '"+banco+"', '"+num_comp_pago+"', "+gastos_bancos+", "+total+", '"+son+"', '"+detalle+"', "+param+");");
            if(res.next()){
                num = (res.getString(1)!=null) ? res.getInt(1) : -1;
                res.close();
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return num;
    }*/

    public boolean anular(String id_comprobante)
    {
        boolean res = false;
        try{
            ResultSet registro = this.consulta("select proc_anularComprobanteIngreso("+id_comprobante+");");
            if(registro.next()){
                res = (registro.getString(1)!=null) ? registro.getBoolean(1) : false;
                registro.close();
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return res;
    }

    /* PARA COBROS  */



    
    public String insertar(int id_sucursal, int id_punto_emision, String usuario, String num_compIngr, String fecha_proceso, String fecha_efectivo, String nombre, String forma_pago,
            String num_cheque, String banco, String num_comp_pago, String gastos_bancos, String total, String valor_son, String son, String detalle, String idsCuentas, String debe, String haber,
            String ids_factura, String abonos, String documentos, String total_pago, String ret_num_serie, String ret_num_retencion, String ret_autorizacion, String ret_fecha_emision, 
            String ret_ejercicio_fiscal_mes, String ret_ejercicio_fiscal, String ret_impuesto_retenido, String id_factura_venta_afecta, String id_retenciones, String bases_imponibles, 
            String valores_retenidos, String codBI)
    {
        String num = "-1:-1";
        fecha_efectivo = (fecha_efectivo.compareTo("")==0) ? "NULL" : "'"+fecha_efectivo+"'";
        ret_fecha_emision = (ret_fecha_emision.compareTo("")==0) ? "NULL" : "'"+ret_fecha_emision+"'";
        id_factura_venta_afecta = (id_factura_venta_afecta.compareTo("")==0) ? "NULL" : id_factura_venta_afecta;
        try{
            String param = this.concatenarValores(idsCuentas, debe, haber);
            String paramAbonos = this.concatenarValores(ids_factura, abonos, documentos);
            String paramRetencion = this.concatenarValores(id_retenciones, bases_imponibles, valores_retenidos, codBI);
            ResultSet res = this.consulta("select proc_comprobanteIngresoCobro("+id_sucursal+", "+id_punto_emision+", '"+usuario+"', "+num_compIngr+", '"+fecha_proceso+"', "+fecha_efectivo+", '"+
                nombre+ "', '"+forma_pago+"', '"+num_cheque+"', '"+banco+"', '"+num_comp_pago+"', "+gastos_bancos+", "+total+", "+valor_son+", '"+son+"', '"+detalle+"', "+param+", "+paramAbonos+
                ", "+total_pago+", '"+ret_num_serie+"', '"+ret_num_retencion+"', '"+ret_autorizacion+"', "+ret_fecha_emision+
                ", '"+ret_ejercicio_fiscal_mes+"', "+ret_ejercicio_fiscal+", "+ret_impuesto_retenido+", "+id_factura_venta_afecta+", "+paramRetencion+");");
            if(res.next()){
                num = (res.getString(1)!=null) ? res.getString(1) : "-1:-1";
                res.close();
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return num;
    }
    
    public int actualizar(String id, String fecha_proceso, String fecha_efectivo, String nombre, String forma_pago, String num_cheque,
                        String banco, String total, String son, String detalle, String idsCuentas, String debe, String haber,
                        String ids_factura_pagos, String ids_factura, String abonos, String total_pago)
    {
        int num = -1;
        fecha_efectivo = (fecha_efectivo.compareTo("")==0) ? "NULL" : "'"+fecha_efectivo+"'";
        try{
            String param = this.concatenarValores(idsCuentas, debe, haber);
            String paramAbonos = this.concatenarValores(ids_factura_pagos, ids_factura, abonos);
            ResultSet res = this.consulta("select proc_editarComprobanteIngresoCobro("+id+", '"+fecha_proceso+"', "+fecha_efectivo+", '"+
                nombre+ "', '"+forma_pago+"', '"+num_cheque+"', '"+banco+"', "+total+", '"+son+"', '"+detalle+"', "+param+", "+paramAbonos+
                ", "+total_pago+");");
            if(res.next()){
                num = (res.getString(1)!=null) ? res.getInt(1) : -1;
                res.close();
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return num;
    }

    public boolean anularCobro(String id_factura_compra_pago)
    {
        boolean ok = false;
        try{
            ResultSet res = this.consulta("select proc_anularComprobanteIngresoCobro("+id_factura_compra_pago+");");
            if(res.next()){
                ok = (res.getString(1)!=null) ? res.getBoolean(1) : false;
                res.close();
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return ok;
    }

    public boolean efectivizarCheques(int id_sucursal, String ids_comprobantes, String fechas_efectivo)
    {
        boolean ok = false;
        try{
            String param = this.concatenarValores(ids_comprobantes, fechas_efectivo);
            ResultSet res = this.consulta("select proc_comprobanteIngresoEfectivizar("+id_sucursal+", "+param+");");
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
