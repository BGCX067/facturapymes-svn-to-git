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

public class ComprobanteEgreso extends DataBase{
    public ComprobanteEgreso(String m, int p, String db, String u, String c){
        super(m, p, db, u, c);
    }
    
    public long getNumComprobante()
    {
        long num = 1;
        try{
            ResultSet res = this.consulta("SELECT max(num_comprobante) FROM tbl_comprobante_egreso;");
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

    public boolean chequeDuplicado(String id, String nch, String id_banco)
    {
        if(nch.compareTo("0")!=0){
            ResultSet res = this.consulta("SELECT * FROM tbl_comprobante_egreso where num_cheque="+nch+" and id_banco="+id_banco+" and id_comprobante_egreso<>"+id);
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

    public long getNumCheque()
    {
        long num = 1;
        try{
            ResultSet res = this.consulta("SELECT max(num_cheque) from tbl_comprobante_egreso;");
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

    public boolean setNumCheque(String id, String num_cheque)
    {
        return this.ejecutar("update tbl_comprobante_egreso set num_cheque="+num_cheque+" where id_comprobante_egreso="+id);
    }

    public ResultSet getComprobanteEgreso(String id)
    {
        return this.consulta("SELECT * FROM tbl_comprobante_egreso where id_comprobante_egreso="+id+";");
    }

    public ResultSet getComprobantesEgreso(String id, String desde, String hasta)
    {
        return this.consulta("SELECT * FROM tbl_comprobante_egreso as E inner join tbl_comprobante_egreso_reposicion as R on E.id_comprobante_egreso=R.id_comprobante_egreso where anulado=false and tipo_pago='e' and id_reposicion_caja_chica="+id);
        //return this.consulta("SELECT * FROM tbl_comprobante_egreso where anulado=false and tipo_pago='e' and (fecha_actual || ' ' || hora)::timestamp > '"+desde+"'::timestamp and (fecha_actual || ' ' || hora)::timestamp < '"+hasta+"'");
    }

    public ResultSet getComprobanteEgresoDetalle(String id)
    {
        return this.consulta("select PC.id_plan_cuenta, PC.codigo_cuenta, PC.nombre_cuenta, D.debe, D.haber " +
                "from tbl_comprobante_egreso_detalle as D inner join tbl_plan_cuenta as PC on D.id_plan_cuenta=PC.id_plan_cuenta " +
                "where D.id_comprobante_egreso="+id+" order by D.id_comprobante_egreso_detalle;");
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

    public String concatenarValoresAbonos(String idsFacts, String abonos, String documentos)
    {
        String param = "";
        String vecIdsFacts [] = idsFacts.split(",");
        String vecAbonos [] = abonos.split(",");
        String vecDocs [] = documentos.split(",");
        for(int i=0; i<vecIdsFacts.length; i++){
            param += "['"+vecIdsFacts[i]+"','"+vecAbonos[i]+"','"+vecDocs[i]+"'],";
        }
        param = param.substring(0, param.length()-1);
        return "array["+param+"]";
    }
    
    public int insertar(int id_sucursal, long numero_comp_egr, String fecha_proceso, String fecha_efectivo, String ci_ruc, String orden_de, String tipo_pago, String num_cheque, String cuenta,
             String banco, String total, String valor_son, String son, String detalle, String lugar, String idsCuentas, String debe, String haber, String id_banco)
    {
        int num = -1;
        fecha_efectivo = (fecha_efectivo.compareTo("")==0) ? "NULL" : "'"+fecha_efectivo+"'";
        try{
            String param = this.concatenarValores(idsCuentas, debe, haber);
            ResultSet res = this.consulta("select proc_comprobanteEgreso("+id_sucursal+", "+numero_comp_egr+", '"+fecha_proceso+"', "+fecha_efectivo+", '"+ci_ruc+"', '"+
                orden_de+"', '"+tipo_pago+"', "+num_cheque+", "+"'"+cuenta+"', '"+banco+"', "+total+", "+valor_son+", '"+son+"', '"+detalle+"', '"+lugar+"', "+
                id_banco+", "+param+");");
            if(res.next()){
                num = (res.getString(1)!=null) ? res.getInt(1) : -1;
                res.close();
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return num;
    }
    
    /*public int actualizar(String id, String fecha_proceso, String fecha_efectivo, String ci_ruc, String orden_de, String tipo_pago, String num_cheque, String cuenta,
             String banco, String total, String valor_son, String son, String detalle, String lugar, String idsCuentas, String debe, String haber, String id_banco)
    {
        int num = -1;
        fecha_efectivo = (fecha_efectivo.compareTo("")==0) ? "NULL" : "'"+fecha_efectivo+"'";
        try{
            String param = this.concatenarValores(idsCuentas, debe, haber);
            ResultSet res = this.consulta("select proc_editarComprobanteEgreso("+id+", '"+fecha_proceso+"', "+fecha_efectivo+", '"+ci_ruc+"', '"+
                orden_de+"', '"+tipo_pago+"', "+num_cheque+", '"+cuenta+"', '"+banco+"', "+total+", "+valor_son+", '"+son+"', '"+detalle+"', '"+lugar+"', "+
                id_banco+", "+param+");");
            if(res.next()){
                num = (res.getString(1)!=null) ? res.getInt(1) : -1;
                res.close();
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return num;
    }*/

    public ResultSet getChequesNoCobrados()
    {
        return this.consulta("SELECT * FROM tbl_comprobante_egreso where (fecha_en_efectivo is null or fecha_en_efectivo::varchar = '') and tipo_pago='c' and anulado=false;");
    }

    public boolean efectivizarCheques(int id_sucursal, String ids_cheque_no_cobrado, String fechas_efectivo)
    {
        boolean ok = false;
        try{
            String param = this.concatenarValores(ids_cheque_no_cobrado, fechas_efectivo);
            ResultSet res = this.consulta("select proc_comprobanteEgresoEfectivizar("+id_sucursal+", "+param+");");
            if(res.next()){
                ok = (res.getString(1)!=null) ? res.getBoolean(1) : false;
                res.close();
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return ok;
    }

    public boolean anular(String id_comprobante)
    {
        boolean res = false;
        try{
            ResultSet registro = this.consulta("select proc_anularComprobanteEgreso("+id_comprobante+");");
            if(registro.next()){
                res = (registro.getString(1)!=null) ? registro.getBoolean(1) : false;
                registro.close();
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return res;
    }

    /*   PARA EL MODULO DE PAGOS  */
    public String ingresarPago(int id_sucursal, String numero_comp_egr, String fecha_proceso, String fecha_efectivo, String ci_ruc, String orden_de, String tipo_pago, String num_cheque, String cuenta,
             String banco, String total, String son, String detalle, String lugar, String idsCuentas, String debe, String haber,
             String idsFact, String abonos, String documentos, String tot_pago, String id_banco)
    {
        String num = "-1:-1";
        fecha_efectivo = (fecha_efectivo.compareTo("")==0) ? "NULL" : "'"+fecha_efectivo+"'";
        try{
            String param = this.concatenarValores(idsCuentas, debe, haber);
            String paramAbonos = this.concatenarValores(idsFact, abonos, documentos);            
            ResultSet res = this.consulta("select proc_pagoProveedor("+id_sucursal+", "+numero_comp_egr+", '"+fecha_proceso+"', "+fecha_efectivo+", '"+ci_ruc+"', '"+orden_de+"', '"+
                tipo_pago+"', "+num_cheque+", '"+cuenta+"', '"+banco+"', "+total+", '"+son+"', '"+detalle+"', '"+lugar+"', "+id_banco+", "+param+", "+paramAbonos+", "+tot_pago+");");
            if(res.next()){
                num = (res.getString(1)!=null) ? res.getString(1) : "-1:-1";
                res.close();
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return num;
    }
    
    /*public int actualizar(String id, String fecha_proceso, String fecha_efectivo, String ci_ruc, String orden_de, String tipo_pago, String num_cheque, String cuenta,
             String banco, String total, String son, String detalle, String lugar, String idsCuentas, String debe, String haber,
             String idsFactPagos, String idsFact, String abonos, String tot_pago)
    {
        int num = -1;
        fecha_efectivo = (fecha_efectivo.compareTo("")==0) ? "NULL" : "'"+fecha_efectivo+"'";
        try{
            String param = this.concatenarValores(idsCuentas, debe, haber);
            String paramAbonos = this.concatenarValores(idsFactPagos, idsFact, abonos);
            ResultSet res = this.consulta("select proc_editarComprobanteEgreso("+id+", '"+fecha_proceso+"', "+fecha_efectivo+", '"+ci_ruc+"', '"+orden_de+"', '"+tipo_pago+"', "+num_cheque+", " +
                "'"+cuenta+"', '"+banco+"', "+total+", '"+son+"', '"+detalle+"', '"+lugar+"', "+param+", "+paramAbonos+", "+tot_pago+");");
            if(res.next()){
                num = (res.getString(1)!=null) ? res.getInt(1) : -1;
                res.close();
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return num;
    }*/
    
    public boolean anularPago(String id_factura_compra_pago)
    {
        boolean ok = false;
        try{
            ResultSet res = this.consulta("select proc_anularComprobanteEgresoPago("+id_factura_compra_pago+");");
            if(res.next()){
                ok = (res.getString(1)!=null) ? res.getBoolean(1) : false;
                res.close();
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return ok;
    }

    /*   PARA EL MODULO DE NOMINA  */
    public String anticipoRolPago(int id_sucursal, String numero_comp_egr, String fecha_proceso, String fecha_efectivo, String idEmp,
            String valor, String forma_pago, String banco, String cuenta, String num_cheque, String lugar, String son, String detalle,
            String idsCuentas, String debe, String haber)
    {
        String num = "-1:-1";
        fecha_efectivo = (fecha_efectivo.compareTo("")==0) ? "NULL" : "'"+fecha_efectivo+"'";
        try{
            String param = this.concatenarValores(idsCuentas, debe, haber);
            ResultSet res = this.consulta("select proc_anticipoEmpleado("+id_sucursal+", "+numero_comp_egr+", '"+fecha_proceso+"', "+fecha_efectivo+", "+idEmp+", "+valor+", '"+
                forma_pago+"', '"+banco+"', "+"'"+cuenta+"', "+num_cheque+", '"+lugar+"', '"+son+"', '"+detalle+"', "+param+");");
            if(res.next()){
                num = (res.getString(1)!=null) ? res.getString(1) : "-1:-1";
                res.close();
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return num;
    }

    public int edicionAnticipoRolPago(String id, String numero_comp_egr, String fecha_proceso, String fecha_efectivo, String idEmp,
            String valor, String forma_pago, String banco, String cuenta, String num_cheque, String lugar, String son, String detalle,
            String idsCuentas, String debe, String haber)
    {
        int num = -1;
        fecha_efectivo = (fecha_efectivo.compareTo("")==0) ? "NULL" : "'"+fecha_efectivo+"'";
        try{
            String param = this.concatenarValores(idsCuentas, debe, haber);
            ResultSet res = this.consulta("select proc_editarAnticipoEmpleado("+id+", "+numero_comp_egr+", '"+fecha_proceso+"', "+fecha_efectivo+", "+idEmp+", "+valor+", '"+
                forma_pago+"', '"+banco+"', "+"'"+cuenta+"', "+num_cheque+", '"+lugar+"', '"+son+"', '"+detalle+"', "+param+");");
            if(res.next()){
                num = (res.getString(1)!=null) ? res.getInt(1) : -1;
                res.close();
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return num;
    }

    public boolean anularAnticipoRolPago(String id_rol_pago_rubro_empleado)
    {
        boolean ok = false;
        try{
            ResultSet res = this.consulta("select proc_anularAnticipoEmpleado("+id_rol_pago_rubro_empleado+");");
            if(res.next()){
                ok = (res.getString(1)!=null) ? res.getBoolean(1) : false;
                res.close();
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return ok;
    }

    public String cancelarRolPago(int id_sucursal, String numero_comp_egr, String fecha_proceso, String fecha_efectivo, String periodo,
            String valor, String forma_pago, String banco, String cuenta, String num_cheque, String lugar, String son, String detalle,
            String idsCuentas, String debe, String haber)
    {
        String num = "-1:-1";
        fecha_efectivo = (fecha_efectivo.compareTo("")==0) ? "NULL" : "'"+fecha_efectivo+"'";
        try{
            String param = this.concatenarValores(idsCuentas, debe, haber);
            ResultSet res = this.consulta("select proc_cancelarRolPago("+id_sucursal+", "+numero_comp_egr+", '"+fecha_proceso+"', "+fecha_efectivo+", '"+periodo+"', "+valor+", '"+
                forma_pago+"', '"+banco+"', "+"'"+cuenta+"', "+num_cheque+", '"+lugar+"', '"+son+"', '"+detalle+"', "+param+");");
            if(res.next()){
                num = (res.getString(1)!=null) ? res.getString(1) : "-1:-1";
                res.close();
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return num;
    }

    /*public int edicionCancelarRolPago(String id, String numero_comp_egr, String fecha_proceso, String fecha_efectivo, String periodo,
            String valor, String forma_pago, String banco, String cuenta, String num_cheque, String lugar, String son, String detalle,
            String idsCuentas, String debe, String haber)
    {
        int num = -1;
        fecha_efectivo = (fecha_efectivo.compareTo("")==0) ? "NULL" : "'"+fecha_efectivo+"'";
        try{
            String param = this.concatenarValores(idsCuentas, debe, haber);
            ResultSet res = this.consulta("select proc_editarAnticipoEmpleado("+id+", "+numero_comp_egr+", '"+fecha_proceso+"', "+fecha_efectivo+", '"+periodo+"', "+valor+", '"+
                forma_pago+"', '"+banco+"', "+"'"+cuenta+"', "+num_cheque+", '"+lugar+"', '"+son+"', '"+detalle+"', "+param+");");
            if(res.next()){
                num = (res.getString(1)!=null) ? res.getInt(1) : -1;
                res.close();
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return num;
    }*/

}