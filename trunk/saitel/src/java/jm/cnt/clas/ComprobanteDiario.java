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

public class ComprobanteDiario extends DataBase{
    public ComprobanteDiario(String m, int p, String db, String u, String c)
    {
        super(m, p, db, u, c);
    }
    public int getNumComprobante()
    {
        int num = 1;
        try{
            ResultSet res = this.consulta("SELECT max(num_comprobante) FROM tbl_comprobante_diario;");
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
    public ResultSet getComprobanteDiario(String id)
    {
        return this.consulta("SELECT * FROM tbl_comprobante_diario where id_comprobante_diario="+id+";");
    }
    public ResultSet getComprobanteDiarioDetalle(String id)
    {
        return this.consulta("select PC.id_plan_cuenta, PC.codigo_cuenta, PC.nombre_cuenta, L.debe, L.haber " +
                "from tbl_libro_diario_mayor as L inner join tbl_plan_cuenta as PC on L.id_plan_cuenta=PC.id_plan_cuenta " +
                "where L.num_asiento=(select distinct num_asiento from tbl_comprobante_diario where id_comprobante_diario="+id+") " +
                "order by L.id_libro_diario_mayor;");
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
    public int insertar(int id_sucursal, long numero_nota, String fecha_proceso, String detalle, String total, String idsCuentas, String debe, String haber)
    {
        int num = -1;
        try{
            String param = this.concatenarValores(idsCuentas, debe, haber);
            ResultSet res = this.consulta("select proc_comprobanteDiario("+id_sucursal+", "+numero_nota+", '"+fecha_proceso+"', '"+detalle+"', "+total+", "+param+");");
            if(res.next()){
                num = (res.getString(1)!=null) ? res.getInt(1) : -1;
                res.close();
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return num;
    }
    /*public int actualizar(String id, String fecha_proceso, String detalle, String total, String idsCuentas, String debe, String haber)
    {
        int num = -1;
        try{
            String param = this.concatenarValores(idsCuentas, debe, haber);
            ResultSet res = this.consulta("select proc_editarComprobanteDiario("+id+", '"+fecha_proceso+"', '"+detalle+"', "+total+", "+param+");");
            if(res.next()){
                num = (res.getString(1)!=null) ? res.getInt(1) : -1;
                res.close();
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return num;
    }*/
    
    
    /* PARA EL MODULO DE COBROS */
    
    public int insertar(String numero_nota, String fecha_proceso, String detalle, String total, String idsCuentas, String debe, String haber,
            String idFact, String serie_factura, String num_factura, String autorizacion)
    {
        int num = -1;
        try{
            String param = this.concatenarValores(idsCuentas, debe, haber);
            ResultSet res = this.consulta("select proc_comprobanteDiario("+numero_nota+", '"+fecha_proceso+"', '"+detalle+"', "+total+", "+param+", "+
                idFact+", '"+serie_factura+"', "+num_factura+", '"+autorizacion+"');");
            if(res.next()){
                num = (res.getString(1)!=null) ? res.getInt(1) : -1;
                res.close();
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return num;
    }    
    
    
    /* PARA EL MODULO DE PAGOS */
    
    /*public int insertar(String numero_nota, String fecha_proceso, String detalle, String total, String idsCuentas, String debe, String haber,
            String idsFact, String abonos, String tot_pago, String id_nota_debito)
    {
        int num = -1;
        try{
            String param = this.concatenarValores(idsCuentas, debe, haber);
            String paramAbonos = this.concatenarValores(idsFact, abonos);
            ResultSet res = this.consulta("select proc_notaContabilidad("+numero_nota+", '"+fecha_proceso+"', '"+detalle+"', "+total+", "+param+", "+
                paramAbonos+", "+tot_pago+", "+id_nota_debito+");");
            if(res.next()){
                num = (res.getString(1)!=null) ? res.getInt(1) : -1;
                res.close();
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return num;
    }    
    public int actualizar(String id, String fecha_proceso, String detalle, String total, String idsCuentas, String debe, String haber,
            String idsFactPagos, String idsFact, String abonos, String tot_pago)
    {
        int num = -1;
        try{
            String param = this.concatenarValores(idsCuentas, debe, haber);
            String paramAbonos = this.concatenarValores(idsFactPagos, idsFact, abonos);
            ResultSet res = this.consulta("select proc_editarNotaContabilidad("+id+", '"+fecha_proceso+"', '"+detalle+"', "+total+", "+param+", "+
                paramAbonos+", "+tot_pago+");");
            if(res.next()){
                num = (res.getString(1)!=null) ? res.getInt(1) : -1;
                res.close();
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return num;
    }    
    public boolean reversarPago(String id_factura_compra_pago, String id_nota_debito)
    {
        boolean ok = false;
        try{
            ResultSet res = this.consulta("select proc_reversarNotaContabilidadPago("+id_factura_compra_pago+", "+id_nota_debito+");");
            if(res.next()){
                ok = (res.getString(1)!=null) ? res.getBoolean(1) : false;
                res.close();
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return ok;
    }*/

    /* PARA AJUSTE DEL KARDEX */
    public int insertar(int id_sucursal, String numero_nota, String fecha_proceso, String detalle, String total, String idsCuentas, 
            String debe, String haber, String idProducto, String cantidad, String valor_unitario, String valor_total, String ajuste)
    {
        int num = -1;
        try{
            String param = this.concatenarValores(idsCuentas, debe, haber);
            ResultSet res = this.consulta("select proc_kardexAjuste("+id_sucursal+", "+numero_nota+", '"+fecha_proceso+"', '"+detalle+"', "+total+", "+param+
                ", "+idProducto+", "+cantidad+", "+valor_unitario+", "+valor_total+", '"+ajuste+"');");
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
