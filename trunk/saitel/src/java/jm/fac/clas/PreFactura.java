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

package jm.fac.clas;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import jm.web.DataBase;
import jm.web.Fecha;

/**
 *
 * @author Jorge
 */
public class PreFactura extends DataBase{
    public PreFactura(String m, int p, String db, String u, String c){
        super(m, p, db, u, c);
    }
    public boolean calcularPreFactura(String idF)
    {
        boolean r = false;
        try{
            ResultSet res = this.consulta("select proc_calcularPreFactura("+idF+", false);");
            if(res.next()){
                r = (res.getString(1)!=null) ? res.getBoolean(1) : false;
                res.close();
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return r;
    }

    public ResultSet getPreFactura(String idF)
    {
        return this.consulta("SELECT *, (periodo + '1 month'::interval)::date as periodo_suspension FROM vta_prefactura WHERE id_prefactura="+idF+";");
    }

    public String getUltimoPeriodo()
    {
        String periodo_suspension = Fecha.getFecha("SQL");
        try{
            ResultSet res = this.consulta("SELECT max(periodo) FROM tbl_prefactura");
            if(res.next()){
                periodo_suspension = (res.getString(1)!=null) ? res.getString(1) : periodo_suspension;
                res.close();
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return periodo_suspension;
    }

    public ResultSet getPreFacturaRubros(String id_instalacion, String periodo)
    {
        String periodo_ini = "";
        String periodo_fin = "";
        if(periodo.indexOf("-")>0){
            String vec[] = periodo.split("-");
            periodo_ini = vec[0] + "-" + vec[1] + "-01";
            periodo_fin = vec[0] + "-" + vec[1] + "-" + Fecha.getUltimoDiaMes(Integer.parseInt(vec[0]), Integer.parseInt(vec[1]));
        }else{
            String vec[] = periodo.split("/");
            periodo_ini = vec[2] + "-" + vec[1] + "-01";
            periodo_fin = vec[2] + "-" + vec[1] + "-" + Fecha.getUltimoDiaMes(Integer.parseInt(vec[2]), Integer.parseInt(vec[1]));
        }
        return this.consulta("SELECT * FROM vta_instalacion_rubro WHERE id_instalacion="+id_instalacion+" and periodo_cobro between '"+periodo_ini+"' and '"+periodo_fin+"';");
    }

    public ResultSet getPlan(String idPlan, String periodo)
    {
        return this.consulta("select * from vta_plan_tarifa where id_plan_servicio="+idPlan+" and '"+periodo+"' between vigente_desde and vigente_hasta");
    }

    public String concatenarValores(String id_articulos, String cantidades, String precios_costo, String precios_unitarios,
            String subtotales, String descuentos, String ivas, String totales, String descripcion)
    {
        String param = "";
        String vecArti [] = id_articulos.split(",");
        String vecCant [] = cantidades.split(",");
        String vecPC [] = precios_costo.split(",");
        String vecPU [] = precios_unitarios.split(",");
        String vecSubt [] = subtotales.split(",");
        String vecDes [] = descuentos.split(",");
        String vecIva [] = ivas.split(",");
        String vecTot [] = totales.split(",");
        String vecDescMas [] = descripcion.split(",");
        for(int i=0; i<vecArti.length; i++){
            param += "['"+vecArti[i]+"','"+vecCant[i]+"','"+vecPU[i]+"','"+vecSubt[i]+"','"+vecDes[i]+"','"+vecIva[i]+"','"+vecTot[i]+"', '"+vecDescMas[i]+"','"+vecPC[i]+"'],";
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

    public String verificarStock(int id_sucursal, String id_productos, String cantidades)
    {
        ResultSet rs = null;
        String codigo = "";
        int cantidad = 1;
        String vecProd [] = id_productos.split(",");
        String vecCant [] = cantidades.split(",");
        for(int i=0; i<vecProd.length; i++){
            try{
                rs = this.consulta("select P.codigo, SP.stock_sucursal - "+vecCant[i]+" "
                        + "from vta_producto as P inner join tbl_sucursal_producto as SP on P.id_producto=SP.id_producto "
                        + "where SP.id_sucursal="+id_sucursal+" and SP.id_producto="+vecProd[i]);
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

    public boolean actualizar(String id_prefactura, String id_instalacion, String periodo, String id_productos, String cantidades,
                              String precios_unitarios, String subtotales, String descuentos, String ivas, String totales)
    {
        String periodo_ini = "";
        String periodo_fin = "";
        if(periodo.indexOf("-")>0){
            String vec[] = periodo.split("-");
            periodo_ini = vec[0] + "-" + vec[1] + "-01";
            periodo_fin = vec[0] + "-" + vec[1] + "-" + Fecha.getUltimoDiaMes(Integer.parseInt(vec[0]), Integer.parseInt(vec[1]));
        }else{
            String vec[] = periodo.split("/");
            periodo_ini = vec[2] + "-" + vec[1] + "-01";
            periodo_fin = vec[2] + "-" + vec[1] + "-" + Fecha.getUltimoDiaMes(Integer.parseInt(vec[2]), Integer.parseInt(vec[1]));
        }
        List sql = new ArrayList();
        String vecProd [] = id_productos.split(",");
        String vecCat [] = cantidades.split(",");
        String vecPU [] = precios_unitarios.split(",");
        String vecSubt [] = subtotales.split(",");
        String vecDes [] = descuentos.split(",");
        String vecIva [] = ivas.split(",");
        String vecTot [] = totales.split(",");
        sql.add("update tbl_prefactura set dias_conexion="+vecCat[0]+" where id_prefactura="+id_prefactura);
        sql.add("delete from tbl_instalacion_rubro where id_instalacion="+id_instalacion+" and periodo_cobro between '"+periodo_ini+"' and '"+periodo_fin+"';");
        for(int i=1; i<vecProd.length; i++){
            sql.add("insert into tbl_instalacion_rubro(id_prefactura, id_instalacion, periodo_cobro, id_producto, cantidad, p_u, p_st, descuento, iva, total) "
               + "values("+id_prefactura+", "+id_instalacion+", '"+periodo+"', "+vecProd[i]+", "+vecCat[i]+", "+vecPU[i]+", "+vecSubt[i]+", "+vecDes[i]+", "+vecIva[i]+", "+vecTot[i]+");");
        }
        return this.transacciones(sql);
    }


    public boolean facturaDuplicada(String serie, String numero)
    {
        ResultSet res = this.consulta("SELECT * FROM tbl_factura_venta where serie_factura='"+serie+"' and num_factura="+numero);
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


    public String emitir(int id_sucursal, int id_punto_emision, String id_prefactura, String id_instalacion, String usuario, String serie_factura, String num_factura, 
            String autorizacion, String ruc, String forma_pago, String banco, String num_cheque, String num_comp_pago, String gastos_bancos, 
            String id_plan_cuenta_banco, String son, String concepto, String subtotal, String subtotal_2, String subtotal_0, String descuento, 
            String iva_2, String total, String paramArtic, String ret_num_serie, String ret_num_retencion, String ret_autorizacion, 
            String ret_fecha_emision, String ret_ejercicio_fiscal_mes, String ret_ejercicio_fiscal, String ret_impuesto_retenido, 
            String paramRet, String paramAsiento, String xmlFirmado, String dias_conexion, String ids_productos, String cantidades, 
            String preciosUnitarios, String descuentos, String subtotales, String ivas, String totales)
    {
        String idFact = "-1";
        try{
            String fecha_prefactura = "";
            String periodo = "";
            int anio = Fecha.getAnio();
            int mes= Fecha.getMes();
            try{
                ResultSet rsPreFact = this.consulta("select fecha_prefactura, periodo from tbl_prefactura where id_prefactura="+id_prefactura);
                if(rsPreFact.next()){
                    fecha_prefactura = rsPreFact.getString("fecha_prefactura")!=null ? rsPreFact.getString("fecha_prefactura") : "";
                    periodo = rsPreFact.getString("periodo")!=null ? rsPreFact.getString("periodo") : "";
                    anio = Fecha.datePart("anio", periodo);
                    mes = Fecha.datePart("mes", periodo);
                }
            }catch(Exception e){
                e.printStackTrace();
            }
            
            
            String id_cliente = "";
            String razon_social = "";
            String direccion = "";
            String telefono = "";
            //String direccion_instalacion = "";
            String ip = "";
            String radusername = "";
            try{
                ResultSet rsInstal = this.consulta("select C.id_cliente, razon_social, direccion, telefono, direccion_instalacion, ip::varchar, radusername "
                    + "from tbl_instalacion as I inner join tbl_cliente as C on I.id_cliente=C.id_cliente where I.id_instalacion=" + id_instalacion);
                if(rsInstal.next()){
                    id_cliente = rsInstal.getString("id_cliente")!=null ? rsInstal.getString("id_cliente") : "";
                    razon_social = rsInstal.getString("razon_social")!=null ? rsInstal.getString("razon_social") : "";
                    direccion = rsInstal.getString("direccion")!=null ? rsInstal.getString("direccion") : "";
                    telefono = rsInstal.getString("telefono")!=null ? rsInstal.getString("telefono") : "";
                    //direccion_instalacion = rsInstal.getString("direccion_instalacion")!=null ? rsInstal.getString("direccion_instalacion") : "";
                    ip = rsInstal.getString("ip")!=null ? rsInstal.getString("ip") : "";
                    radusername = rsInstal.getString("radusername")!=null ? rsInstal.getString("radusername") : "";
                }
            }catch(Exception e){
                e.printStackTrace();
            }
            
            ret_fecha_emision = ret_fecha_emision.compareTo("")!=0 ? "'"+ret_fecha_emision+"'" : "NULL";
            ResultSet res = this.consulta("select facturaVenta("+id_sucursal+", "+id_punto_emision+", "+id_cliente+", '"+usuario+"', '"+serie_factura+
                    "', "+num_factura+", '"+autorizacion+"', '"+ruc+"', '"+razon_social+"', now()::date, '"+direccion+
                    "', '"+telefono+"', '"+forma_pago+"', '"+banco+"', '"+num_cheque+"', '"+num_comp_pago+"', "+gastos_bancos+
                    ", "+id_plan_cuenta_banco+", '"+son+"', '"+concepto+"', "+subtotal+", "+subtotal_2+", "+subtotal_0+", "+descuento+
                    ", "+iva_2+", "+total+", "+paramArtic+", '"+ret_num_serie+"', '"+ret_num_retencion+"', '"+ret_autorizacion+"', "+ret_fecha_emision+
                    ", '"+ret_ejercicio_fiscal_mes+"', "+ret_ejercicio_fiscal+", "+ret_impuesto_retenido+", "+paramRet+", "+paramAsiento+", '"+xmlFirmado+"');");  /* 33 param */
            if(res.next()){
                String idFactComp = (res.getString(1)!=null) ? res.getString(1) : "-1:-1";
                String vecFactComp[] = idFactComp.split(":");
                idFact = vecFactComp[0];
                if(idFact.compareTo("-1")!=0){
                    this.ejecutar("update tbl_factura_venta set id_instalacion="+id_instalacion+", ip='"+ip+"', radusername='"+radusername+"' where id_factura_venta="+idFact+";");
                    this.ejecutar("update tbl_prefactura set id_factura_venta="+idFact+", fecha_emision=now()::date, es_fact_impago=false, dias_conexion="+dias_conexion+" where id_prefactura="+id_prefactura+";");
                    this.ejecutar("UPDATE tbl_instalacion SET estado_servicio='a' where estado_servicio in ('c', 'n') and id_instalacion="+id_instalacion+" and "+id_instalacion+" not in "
                 + "(select distinct id_instalacion from tbl_instalacion_suspension where eliminado=false and now()::date between fecha_inicio and fecha_termino);");
                    /*   sistema juridico   */
                    this.ejecutar("update tbl_gestion_cobranzas set gestion_final='PAGAN Y CONTINUAN' where id_gestion="+id_prefactura+";");
                    
                    if(forma_pago.compareTo("d")==0){
                        this.ejecutar("update tbl_instalacion set estado_servicio='c' where estado_servicio='a' and id_instalacion="+id_instalacion+" and id_instalacion in "
                                + "(select P.id_instalacion from tbl_prefactura as P inner join tbl_factura_venta as F on P.id_factura_venta=F.id_factura_venta "
                                + "where getFechaSuspensionCreditos(fecha_prefactura) < now()::date and forma_pago='d' and deuda>0) and id_instalacion not in "
                                + "(select distinct id_instalacion from tbl_anticipo_internet where now()::date between fecha_ini and fecha_fin);");
                    }
                    
                    List sql = new ArrayList();
                    sql.add("delete from tbl_instalacion_rubro where id_instalacion="+id_instalacion+" and periodo_cobro between '"+anio + "-" + mes + "-01' and '"+anio + "-" + mes + "-"+ Fecha.getUltimoDiaMes(anio, mes) +"';");
                    String vec_ids_productos[] = ids_productos.split(",");
                    String vec_cantidades[] = cantidades.split(",");
                    String vec_p_u[] = preciosUnitarios.split(",");
                    String vec_descuentos[] = descuentos.split(",");
                    String vec_subtotales[] = subtotales.split(",");
                    String vec_ivas[] = ivas.split(",");
                    String vec_totales[] = totales.split(",");
                    for(int i=1; i<vec_ids_productos.length; i++){
                        sql.add("insert into tbl_instalacion_rubro(id_prefactura, id_instalacion, periodo_cobro, id_producto, cantidad, p_u, p_st, descuento, iva, total) "
                                + "values("+id_prefactura+", "+id_instalacion+", '"+periodo+"', "+vec_ids_productos[i]+", "+vec_cantidades[i]+", "+vec_p_u[i]+", "+
                                vec_subtotales[i]+", "+vec_descuentos[i]+", "+vec_ivas[i]+", "+vec_totales[i]+");");
                    }
                    this.transacciones(sql);
                    
                }
                
                
                
                res.close();
            }
            /*ret_fecha_emision = ret_fecha_emision.compareTo("")!=0 ? "'"+ret_fecha_emision+"'" : "NULL";
            String paramProductos = this.concatenarValores(id_productos, cantidades, precios_costo, precios_unitarios, subtotales, descuentos, ivas, totales, descripcion);
            String paramRetencion = this.concatenarValores(id_retenciones, bases_imponibles, valores_retenidos);
            ResultSet res = this.consulta("select proc_emitirFactura("+id_sucursal+", "+id_punto_emision+", "+id_prefactura+", '"+usuario+"', '"+serie_factura+
                    "', "+num_factura+", '"+autorizacion+"', '"+ruc+"', '"+forma_pago+"', '"+banco+"', '"+num_cheque+"', '"+num_comp_pago+"', "+gastos_bancos+
                    ", "+id_plan_cuenta_banco+", '"+son+ "', "+subtotal+", "+descuento+", "+iva_0+", "+iva_12+", "+total+", "+paramProductos+
                    ", '"+ret_num_serie+"', '"+ret_num_retencion+"', '"+ret_autorizacion+"', "+ret_fecha_emision+
                    ", "+ret_ejercicio_fiscal+", "+ret_impuesto_retenido+", "+paramRetencion+", '"+concepto+"');");
            if(res.next()){
                idFact = (res.getString(1)!=null) ? res.getString(1) : "-1";
                res.close();
            }*/
        }catch(Exception e){
            e.printStackTrace();
        }
        return idFact;
    }

    public boolean activarEmision(String idI, String periodo)
    {
        try{
            String vec[] = periodo.indexOf("-")>0 ? periodo.split("-") : periodo.split("/");
            String anio = periodo.indexOf("-")>0 ? vec[0] : vec[2];
            ResultSet res = this.consulta("SELECT count(*) FROM tbl_prefactura where id_instalacion='"+idI+"' and periodo<'"+anio+"-"+vec[1]+"-01' and fecha_emision is null;");
            if(res.next()){
                int mesDeudas = (res.getString(1)!=null) ? res.getInt(1) : 0;
                if(mesDeudas > 0){
                    return false;
                }
                res.close();
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return true;
    }
    
    public boolean generarPreFactura(String anio, String mes, String id_instalacion)
    {
        boolean ok = false;
        try{
            ResultSet res = this.consulta("select proc_generarPreFactura("+anio+", "+mes+", "+id_instalacion+");");
            if(res.next()){
                ok = (res.getString(1)!=null) ? res.getBoolean(1) : false;
                res.close();
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return ok;
    }

    public boolean ingresarAnticipo(String id_instalacion, String fecha_ini, String fecha_fin)
    {
        return this.ejecutar("insert into tbl_anticipo_internet(id_instalacion, fecha_ini, fecha_fin) "
                + "values("+id_instalacion+", '"+fecha_ini+"', '"+fecha_fin+"');");
    }
    
    public boolean ingresarAnticipo(String id_instalacion, String fecha_ini, String fecha_fin, String id_factura)
    {
        return this.ejecutar("insert into tbl_anticipo_internet(id_instalacion, fecha_ini, fecha_fin, id_factura_venta) "
                + "values("+id_instalacion+", '"+fecha_ini+"', '"+fecha_fin+"', "+id_factura+");");
    }
    
    
    
    
    
    
    /*  ABONOS  */
    
    

    public ResultSet getAbonos(String id_prefactura)
    {
        return this.consulta("select A.*, F.serie_factura || '-' || F.num_factura as numero_factura "
                + "from tbl_abono as A inner join tbl_factura_venta as F on F.id_factura_venta=A.id_factura_venta "
                + "where A.id_prefactura="+id_prefactura);
    }
    
    
}
