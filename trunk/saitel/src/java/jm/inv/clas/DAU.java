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

public class DAU extends DataBase{
    public DAU(String m, int p, String db, String u, String c)
    {
        super(m, p, db, u, c);
    }
    public ResultSet getDau(String id)
    {
        return this.consulta("SELECT * FROM tbl_dau where id_dau="+id+";");
    }

    public ResultSet getDauDetalle(String id)
    {
        return this.consulta("SELECT D.*, P.codigo, P.descripcion, P.precio_costo FROM tbl_dau_detalle as D inner join tbl_producto as P on D.id_producto=P.id_producto "
                + "where id_dau="+id+";");
    }

    public ResultSet getFacturas(String idDau)
    {
        return this.consulta("SELECT F.* FROM (vta_factura_compra as F inner join tbl_factura_importacion as FI on F.id_factura_compra=FI.id_factura_compra) "
                + "inner join tbl_dau as D on D.id_dau=FI.id_dau where FI.id_dau="+idDau+";");
    }

    public String insertar(int id_sucursal, String num_dau, String tipo_dau, String num_orden, String anio, String aduana,
                        String codigo_02, String regimen, String codigo_03, String fecha_tx,
                        String hora_tx, String refrendo, String importador, String tipo, String doc_id,
                        String ciudad, String direccion, String telefono, String nivel_comercial, String declarante,
                        String codigo_12, String almacen_deposito, String codigo_13, String fob, String flete, String seguro,
                        String ajuste, String cif, String valor_aduana, String valor_em, String der_especifico, String ice,
                        String iva, String antidumping, String der_consular, String sobretiempo_petr,
                        String ad_barril_crudo, String tasa_modernizacion, String tasa_control, String tasa_almacenaje,
                        String multa, String interes, String imp_salida_divisa, String fodinfa, String corpei, String otros,
                        String salvaguarda, String total_autoliquidacion, String observacion)
    {
        String num = "-1:-1";
        try{
            ResultSet res = this.consulta("select proc_importacion("+id_sucursal+", '"+num_dau+"', '"+tipo_dau+"', '"+num_orden+"', '"+
                        anio+"', '"+aduana+"', '"+codigo_02+"', '"+regimen+"', '"+codigo_03+"', '"+fecha_tx+"', '"+hora_tx+"', '"+
                        refrendo+"', '"+importador+"', '"+tipo+"', '"+doc_id+"', '"+ciudad+"', '"+direccion+"', '"+telefono+"', '"+
                        nivel_comercial+"', '"+declarante+"', '"+codigo_12+"', '"+almacen_deposito+"', '"+codigo_13+"', "+fob+", "+flete+", "+seguro+", "+
                        ajuste+", "+cif+", "+valor_aduana+", "+valor_em+", "+der_especifico+", "+ice+", "+iva+", "+antidumping+", "+
                        der_consular+", "+sobretiempo_petr+", "+ad_barril_crudo+", "+tasa_modernizacion+", "+tasa_control+", "+
                        tasa_almacenaje+", "+multa+", "+interes+", "+imp_salida_divisa+", "+fodinfa+", "+corpei+", "+otros+", "+
                        salvaguarda+", "+total_autoliquidacion+", '"+observacion+"');");
            if(res.next()){
                num = (res.getString(1)!=null) ? res.getString(1) : "-1:-1";
                res.close();
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return num;
    }

    public String actualizar(String id, String num_dau, String tipo_dau, String num_orden, String anio, String aduana,
                        String codigo_02, String regimen, String codigo_03, String fecha_tx,
                        String hora_tx, String refrendo, String importador, String tipo, String doc_id,
                        String ciudad, String direccion, String telefono, String nivel_comercial, String declarante,
                        String codigo_12, String almacen_deposito, String codigo_13, String fob, String flete, String seguro,
                        String ajuste, String cif, String valor_aduana, String valor_em, String der_especifico, String ice,
                        String iva, String antidumping, String der_consular, String sobretiempo_petr,
                        String ad_barril_crudo, String tasa_modernizacion, String tasa_control, String tasa_almacenaje,
                        String multa, String interes, String imp_salida_divisa, String fodinfa, String corpei, String otros,
                        String salvaguarda, String total_autoliquidacion, String observacion)
    {
        String num = "-1";
        try{
            ResultSet res = this.consulta("select proc_editarImportacion("+id+", '"+num_dau+"', '"+tipo_dau+"', '"+num_orden+"', '"+
                        anio+"', '"+aduana+"', '"+codigo_02+"', '"+regimen+"', '"+codigo_03+"', '"+
                        fecha_tx+"', '"+hora_tx+"', '"+refrendo+"', '"+importador+"', '"+tipo+"', '"+doc_id+"', '"+ciudad+"', '"+direccion+"', '"+telefono+"', '"+
                        nivel_comercial+"', '"+declarante+"', '"+codigo_12+"', '"+almacen_deposito+"', '"+codigo_13+"', "+fob+", "+flete+", "+seguro+", "+
                        ajuste+", "+cif+", "+valor_aduana+", "+valor_em+", "+der_especifico+", "+ice+", "+iva+", "+antidumping+", "+
                        der_consular+", "+sobretiempo_petr+", "+ad_barril_crudo+", "+tasa_modernizacion+", "+tasa_control+", "+
                        tasa_almacenaje+", "+multa+", "+interes+", "+imp_salida_divisa+", "+fodinfa+", "+corpei+", "+otros+", "+
                        salvaguarda+", "+total_autoliquidacion+", '"+observacion+"');");
            if(res.next()){
                num = (res.getString(1)!=null) ? res.getString(1) : "-1";
                res.close();
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return num;
    }

    public boolean insertarDetalle(String idDau, String id_productos, String porcentajes, String asignaciones, String cantidades, String costos_unitarios)
    {
        List sql = new ArrayList();
        sql.add("DELETE FROM tbl_dau_detalle WHERE id_dau="+idDau);

        String vecProductos[] = id_productos.split(",");
        String vecPorcentajes[] = porcentajes.split(",");
        String vecAsignaciones[] = asignaciones.split(",");
        String vecCantidades[] = cantidades.split(",");
        String vecCostos_unitarios[] = costos_unitarios.split(",");
        for(int i=0; i<vecProductos.length; i++){
            sql.add("INSERT INTO tbl_dau_detalle(id_dau, id_producto, cantidad, porcent_asignacion, valor_asignacion, costo_unitario) "
            + "values("+idDau+", "+vecProductos[i]+", "+vecCantidades[i]+", "+vecPorcentajes[i]+", "+vecAsignaciones[i]+", "+vecCostos_unitarios[i]+");");
        }

        return this.transacciones(sql);
    }

    public int cerrarImportacion(String idDau)
    {
        int num = -1;
        try{
            ResultSet res = this.consulta("select proc_importacionCerrar("+idDau+");");
            if(res.next()){
                num = (res.getString(1)!=null) ? res.getInt(1) : -1;
                res.close();
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return num;
    }

    public boolean anular(String idDau)
    {
        boolean ok = false;
        try{
            ResultSet res = this.consulta("select proc_anularImportacion("+idDau+");");
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
