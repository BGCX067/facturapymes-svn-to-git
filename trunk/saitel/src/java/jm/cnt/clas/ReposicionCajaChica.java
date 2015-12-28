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
import java.util.ArrayList;
import java.util.List;
import jm.web.DataBase;

public class ReposicionCajaChica extends DataBase{
    public ReposicionCajaChica(String m, int p, String db, String u, String c){
        super(m, p, db, u, c);
    }
    public boolean cerrada(String id)
    {
        boolean ok = true;
        try{
            ResultSet rs = this.consulta("select * from tbl_reposicion_caja_chica where id_reposicion_caja_chica="+id+" and fecha is null");
            if(this.getFilas(rs)>0){
                ok = false;
            }
            rs.close();
        }catch(Exception e){
            e.printStackTrace();
        }
        return ok;
    }
    public ResultSet getFormulario(String id)
    {
        return this.consulta("SELECT * FROM tbl_reposicion_caja_chica where id_reposicion_caja_chica="+id);
    }
    public String getSaldoCajaChica(String id_plan_cuenta)
    {
        String num = "0";
        try{
            ResultSet res = this.consulta("SELECT saldo_deudor from tbl_libro_diario_mayor where id_libro_diario_mayor="
                    + "(select max(id_libro_diario_mayor) from tbl_libro_diario_mayor where id_plan_cuenta="+id_plan_cuenta+")");
            if(res.next()){
                num = (res.getString(1)!=null) ? res.getString(1) : "0";
                res.close();
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return num;
    }
    public String getPeriodoHasta(String id_rep_caja_chica)
    {
        String num = "0";
        try{
            ResultSet res = this.consulta("SELECT max(fecha) from tbl_vale_caja where id_reposicion_caja_chica="+id_rep_caja_chica);
            if(res.next()){
                num = (res.getString(1)!=null) ? res.getString(1) : "0";
                res.close();
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return num;
    }
    public String getHoraHasta(String id_rep_caja_chica)
    {
        String num = "23:59:59.999";
        try{
            ResultSet res = this.consulta("SELECT hora from tbl_reposicion_caja_chica where id_reposicion_caja_chica = "
              + "(select min(id_reposicion_caja_chica) from tbl_reposicion_caja_chica where id_reposicion_caja_chica>"+id_rep_caja_chica+")");
            if(res.next()){
                num = (res.getString(1)!=null) ? res.getString(1) : "23:59:59.999";
                res.close();
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return num;
    }
    public ResultSet getValesCajaChica(String id)
    {
        return this.consulta("SELECT V.*, P.nombre_cuenta FROM (tbl_vale_caja as V inner join tbl_plan_cuenta as P on P.id_plan_cuenta=V.id_plan_cuenta_gasto) "
                + "inner join tbl_reposicion_caja_chica as R on R.id_reposicion_caja_chica=V.id_reposicion_caja_chica "
                + "where V.anulado=false and R.id_reposicion_caja_chica="+id+" order by V.id_vale_caja");
    }
    public long pagoUnico(String id)
    {
        long id_factura_compra_pago=0;
        try{
            ResultSet rs = this.consulta("select * from tbl_factura_compra_pago where id_comprobante_egreso="+id);
            if(this.getFilas(rs)==1){
                if(rs.next()){
                    id_factura_compra_pago = rs.getString("id_factura_compra_pago")!=null ? rs.getLong("id_factura_compra_pago") : 0;
                }
            }
            rs.close();
        }catch(Exception e){
            id_factura_compra_pago = 0;
            e.printStackTrace();
        }
        return id_factura_compra_pago;
    }
    public boolean aprobar(String id, String ids_vales, String aprobaciones, String observaciones, String tipDocs)
    {
        List sql = new ArrayList();
        sql.add("update tbl_reposicion_caja_chica set aprobado=true where id_reposicion_caja_chica="+id+";");
        String vec_ids_vales[] = ids_vales.split(",");
        String vec_aprobaciones[] = aprobaciones.split(",");
        String vec_observaciones[] = observaciones.split(",");
        String vec_tipDocs[] = tipDocs.split(",");
        for(int i=0; i<vec_ids_vales.length; i++){
            if(vec_aprobaciones[i].compareTo("no")==0){
                if(vec_tipDocs[i].compareTo("v")==0){
                    sql.add("select proc_valecajaanular("+vec_ids_vales[i]+");");
                    sql.add("update tbl_vale_caja set motivo_anulacion='"+vec_observaciones[i]+"' where id_vale_caja="+vec_ids_vales[i]+";");
                }
                if(vec_tipDocs[i].compareTo("l")==0){
                    long id_factura_compra_pago = this.pagoUnico(vec_ids_vales[i]);
                    if(id_factura_compra_pago > 0){
                        sql.add("select proc_anularcomprobanteegresopago("+id_factura_compra_pago+");");
                        sql.add("update tbl_factura_compra_pago set motivo_anulacion='"+vec_observaciones[i]+"' where id_factura_compra_pago="+vec_ids_vales[i]+";");
                    }
                }
            }
        }
        return this.transacciones(sql);
    }
    public String reponer(String id_rep_caja_chica, int id_sucursal,  String responsable, String fecha, String saldo_caja, String id_banco,
            String  periodo_hasta, long num_compEgr, String fecha_proceso, String fecha_efectivo, String ci_ruc,
            String orden_de, String forma_pago, String num_cheque, String cuenta, String banco, String total,
            String son, String detalle, String lugar, String idCuenta0, String idCuenta1)
    {
        String num = "-1";
        fecha_efectivo = (fecha_efectivo.compareTo("")==0) ? "NULL" : "'"+fecha_efectivo+"'";
        try{
            String param = "array[['"+idCuenta0+"', '"+total+"', '0'],['"+idCuenta1+"', '0', '"+total+"']]";
            ResultSet res = this.consulta("select proc_reposicionCajaChica("+id_rep_caja_chica+", "+id_sucursal+", '"+responsable+"', '"+fecha+"', "+saldo_caja+
                    ", "+id_banco+", '"+periodo_hasta+"', "+num_compEgr+", '"+fecha_proceso+"', "+fecha_efectivo+", '"+ci_ruc+"', '"+orden_de+"', '"+
                forma_pago+"', "+num_cheque+", '"+cuenta+"', '"+banco+"', "+total+", '"+son+"', '"+detalle+"', '"+lugar+"', "+param+");");
            if(res.next()){
                num = (res.getString(1)!=null) ? res.getString(1) : "-1";
                res.close();
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return num;
    }
}