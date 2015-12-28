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

package jm.adm.clas;
import java.sql.ResultSet;
import jm.web.DataBase;

public class TablaImpuesto extends DataBase{
    public TablaImpuesto(String m, int p, String db, String u, String c){
        super(m, p, db, u, c);
    }

    public ResultSet getVigencia(String id)
    {
        return this.consulta("SELECT * FROM tbl_tabla_vigencia_impuesto where id_tabla_vigencia_impuesto="+id+";");
    }
    public boolean tablasVigentes(String id)
    {
        ResultSet res = this.consulta("SELECT * FROM tbl_tabla_vigencia_impuesto where (vigente_hasta is null or vigente_hasta::varchar='') and id_tabla_vigencia_impuesto<>"+id+";");
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
    public String getTablaVigente()
    {
        String id = "";
        try{
            ResultSet res = this.consulta("SELECT * FROM tbl_tabla_vigencia_impuesto where (vigente_hasta is null or vigente_hasta::varchar='');");
            if(res.next()){
                id = res.getString("id_tabla_vigencia_impuesto")!=null ? res.getString("id_tabla_vigencia_impuesto") : "";
                res.close();
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return id;
    }
    public boolean enConflicto(String id, String vigente_desde)
    {
        ResultSet res = this.consulta("SELECT * FROM tbl_tabla_vigencia_impuesto where '"+vigente_desde+"'<vigente_hasta and id_tabla_vigencia_impuesto<>"+id+";");
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
    public boolean editable(String id)
    {
        int numRegs = 0;
        try{
            ResultSet res = this.consulta("SELECT count(*) FROM tbl_tabla_vigencia_impuesto where vigente_hasta < now()::date and id_tabla_vigencia_impuesto="+id+";");
            if(res.next()){
                numRegs = (res.getString(1)!=null) ? res.getInt(1) : 0;
                res.close();
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return ((numRegs==0)?true:false);
    }
    public boolean insertar(String fecha_desde, String fecha_hasta)
    {
        String ff = (fecha_hasta.compareTo("")==0) ? "NULL" : "'"+fecha_hasta+"'";
        return this.ejecutar("INSERT INTO tbl_tabla_vigencia_impuesto(vigente_desde,vigente_hasta) VALUES('"+fecha_desde+"', "+ff+");");
    }
    public boolean actualizar(String id, String fecha_desde, String fecha_hasta)
    {
        String ff = (fecha_hasta.compareTo("")==0) ? "NULL" : "'"+fecha_hasta+"'";
        return this.ejecutar("UPDATE tbl_tabla_vigencia_impuesto SET vigente_desde='"+fecha_desde+"', vigente_hasta="+ff+" WHERE id_tabla_vigencia_impuesto="+id+";");
    }

    public ResultSet getImpuestos(String id)
    {
        return this.consulta("SELECT * FROM tbl_tabla_impuesto where id_tabla_vigencia_impuesto="+id+" and eliminado=false order by codigo_impuesto,id_tabla_impuesto;");
    }

    public ResultSet getTablaImpuestosVigente()
    {
        return this.consulta("SELECT I.id_tabla_impuesto, I.impuesto, I.codigo_impuesto, I.porcentaje_retencion FROM tbl_tabla_impuesto as I inner join tbl_tabla_vigencia_impuesto as V on I.id_tabla_vigencia_impuesto=V.id_tabla_vigencia_impuesto " +
                "where now() between V.vigente_desde and V.vigente_hasta or (now()>=V.vigente_desde and V.vigente_hasta is null) and I.eliminado=false order by I.codigo_impuesto;");
    }

    public ResultSet getTablaImpuestosVigentePlanCuantasVentas()
    {
        return this.consulta("SELECT I.id_tabla_impuesto, I.id_plan_cuenta_venta FROM tbl_tabla_impuesto as I inner join tbl_tabla_vigencia_impuesto as V on I.id_tabla_vigencia_impuesto=V.id_tabla_vigencia_impuesto " +
                "where now() between V.vigente_desde and V.vigente_hasta or (now()>=V.vigente_desde and V.vigente_hasta is null) and I.eliminado=false order by I.codigo_impuesto;");
    }

    public String getConceptoRetencion()
    {
        ResultSet rs = this.consulta("SELECT I.id_tabla_impuesto, I.concepto FROM tbl_tabla_impuesto as I inner join tbl_tabla_vigencia_impuesto as V on I.id_tabla_vigencia_impuesto=V.id_tabla_vigencia_impuesto " +
                "where now() between V.vigente_desde and V.vigente_hasta or (now()>=V.vigente_desde and V.vigente_hasta is null) and I.eliminado=false order by I.id_tabla_impuesto;");
        String tbl = this.getJSON(rs);
        try{
            rs.close();
        }catch(Exception e){
            e.printStackTrace();
        }
        return tbl;
    }

    public boolean retencionDuplicada(String id, String idImpuesto, String codigo)
    {
        ResultSet res = this.consulta("SELECT * FROM tbl_tabla_impuesto where id_tabla_vigencia_impuesto="+idImpuesto+" and upper(impuesto)='RENTA' and codigo_impuesto='"+codigo+"' and id_tabla_impuesto<>"+id+";");
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
    public String getIdCuentaCompras(String id)
    {
        String id_plan_cuenta_compra = "";
        try{
            ResultSet res = this.consulta("SELECT id_plan_cuenta_compra FROM tbl_tabla_impuesto where id_tabla_impuesto="+id);
            if(res.next()){
                id_plan_cuenta_compra = res.getString("id_plan_cuenta_compra")!=null ? res.getString("id_plan_cuenta_compra") : "";
                res.close();
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return id_plan_cuenta_compra;
    }
    public String getIdCuentaVentas(String id)
    {
        String id_plan_cuenta_compra = "";
        try{
            ResultSet res = this.consulta("SELECT id_plan_cuenta_venta FROM tbl_tabla_impuesto where id_tabla_impuesto="+id);
            if(res.next()){
                id_plan_cuenta_compra = res.getString("id_plan_cuenta_venta")!=null ? res.getString("id_plan_cuenta_venta") : "";
                res.close();
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return id_plan_cuenta_compra;
    }
    public boolean insertarRetencion(String idImpuesto, String impuesto, String codigo, String concepto, String porcentaje, String id_plan_cuenta_compra, String id_plan_cuenta_venta)
    {
        return this.ejecutar("INSERT INTO tbl_tabla_impuesto(id_tabla_vigencia_impuesto,impuesto,codigo_impuesto,concepto,porcentaje_retencion, id_plan_cuenta_compra, id_plan_cuenta_venta) " +
                "VALUES("+idImpuesto+", '"+impuesto+"', '"+codigo+"', '"+concepto+"', "+porcentaje+", "+id_plan_cuenta_compra+", "+id_plan_cuenta_venta+");");
    }
    public boolean actualizarRetencion(String id, String impuesto, String codigo, String concepto, String porcentaje, String id_plan_cuenta_compra, String id_plan_cuenta_venta)
    {
        return this.ejecutar("UPDATE tbl_tabla_impuesto SET impuesto='"+impuesto+"', codigo_impuesto='"+codigo+"', concepto='"+concepto+"', " +
                "porcentaje_retencion="+porcentaje+", id_plan_cuenta_compra="+id_plan_cuenta_compra+", id_plan_cuenta_venta="+id_plan_cuenta_venta+
                " WHERE id_tabla_impuesto="+id+";");
    }
    public String getUltimaInsercionRetencion()
    {
        try{
            ResultSet res = this.consulta("SELECT max(id_tabla_impuesto) as id_tabla_impuesto FROM tbl_tabla_impuesto;");
            if(res.next()){
                return ((res.getString("id_tabla_impuesto")!=null) ? res.getString("id_tabla_impuesto") : "-1");
            }
            res.close();
        }catch(Exception e){
            e.printStackTrace();
        }
        return "-1";
    }
}
