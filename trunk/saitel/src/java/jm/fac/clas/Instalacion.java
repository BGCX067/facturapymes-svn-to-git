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
import java.sql.Connection;
import java.sql.Statement;
import java.sql.ResultSet;
import java.util.List;
import java.util.ArrayList;
import jm.web.DataBase;
import jm.web.Fecha;
import jm.web.Matriz;


/**
 *
 * @author Jorge
 */
public class Instalacion extends DataBase{
    public Instalacion(String m, int p, String db, String u, String c){
        super(m, p, db, u, c);
    }

    public ResultSet getInstalacion(String id)
    {
        return this.consulta("SELECT *, toDateSQL(fecha_instalacion) as sql_fecha_instalacion FROM vta_instalacion where id_instalacion="+id);
    }

    public ResultSet getInstalaciones(String idSucursal, int anio, int mes)
    {
        String fecha_ini = anio + "-" + mes + "-01";
        String fecha_fin = anio + "-" + mes + "-" + Fecha.getUltimoDiaMes(anio, mes);
        return this.consulta("SELECT distinct id_instalacion, id_sucursal || '-' ||  num_instalacion, ruc, razon_social, toDateSQL(fecha_instalacion), ip, txt_estado_servicio FROM vta_instalacion where id_sucursal="+idSucursal+" and id_instalacion not in "
                + "(select id_instalacion from tbl_prefactura where periodo between '"+fecha_ini+"' and '"+fecha_fin+"') and anulado=false and estado_servicio in ('a','s','c') order by razon_social;");
    }

    public ResultSet getInstalaciones(int idSucursal, String where)
    {
        return this.consulta("SELECT distinct id_instalacion, id_sucursal || '-' ||  num_instalacion, ruc, razon_social, ip "
                + "FROM vta_instalacion where "+where+" and id_sucursal="+idSucursal+" and anulado=false;");
    }

    public ResultSet getInstalaciones()
    {
        return this.consulta("SELECT * FROM tbl_instalacion where anulado=false");
    }

    public String getInstalacionJSON(String id)
    {
        ResultSet rs = this.consulta("SELECT id_instalacion, ruc, razon_social, sector, ciudad, direccion, telefono, direccion_instalacion, "
                + "plan, txt_comparticion, ip, receptor, antena_acoplada, txt_estado_servicio, id_sucursal, movil_claro, movil_movistar, deviceclave, "
                + "set_deviceclave, ip_radio FROM vta_instalacion where id_instalacion="+id);
        String tbl = this.getJSON(rs);
        try{
            rs.close();
        }catch(Exception e){
            e.printStackTrace();
        }
        return tbl;
    }

    public String getInstalacionesJSON()
    {
        ResultSet rs = this.consulta("SELECT id_instalacion, ruc, razon_social, sector, ciudad, direccion, telefono, direccion_instalacion, "
                + "plan, txt_comparticion, ip, receptor, antena_acoplada, txt_estado_servicio, id_sucursal FROM vta_instalacion where anulado=false");
        String tbl = this.getJSON(rs);
        try{
            rs.close();
        }catch(Exception e){
            e.printStackTrace();
        }
        return tbl;
    }
    
    /*public String getParroquia(String id_parroquia)
    {
        String parroquia = "";
        try{
            ResultSet rs = this.consulta("SELECT * FROM tbl_ubicacion where id_ubicacion="+id_parroquia);
            if(rs.next()){
                parroquia = rs.getString("id_parroquia")!=null ? rs.getString("id_parroquia") : "";
                rs.close();
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return parroquia;
    }
    /*public String getInstalacionesJSON(String w)
    {
        ResultSet rs = this.consulta("SELECT id_instalacion, ruc, razon_social, sector, ciudad, direccion, telefono, direccion_instalacion, "
                + "plan, txt_comparticion, ip, receptor, antena_acoplada, txt_estado_servicio, id_sucursal FROM vta_soporte " + w);
        String tbl = this.getJSON(rs);
        try{
            rs.close();
        }catch(Exception e){
            e.printStackTrace();
        }
        return tbl;
    }*/

    public boolean ipDisponible(String id, int id_sucursal, String ip)
    {
        ResultSet res = this.consulta("select distinct ip from tbl_instalacion where ip='"+ip+"' "
                        + "and estado_servicio in ('p', 'a','s','c','r','d','n') and anulado=false "
                        + "and id_sucursal="+id_sucursal+" and anulado=false and id_instalacion<>"+id);
        if(this.getFilas(res)>0){
            return false;
        }
        if(ip.indexOf("255")>=0){
            return false;
        }
        try{
            res.close();
        }catch(Exception e){
            e.printStackTrace();
        }
        return true;
    }
    
    public List getIpsDisponibles(int id_sucursal)
    {
        ResultSet rsIps = this.consulta("select distinct ip from tbl_instalacion where ip::varchar like '192.168.%' and anulado=false "
                        + "and id_sucursal="+id_sucursal+" order by ip");
        
        ResultSet rsIpsEstado = this.consulta("select distinct ip, estado_servicio from tbl_instalacion "
                + "where ip::varchar like '192.168.%' and anulado=false and id_sucursal="+id_sucursal+" order by ip");
        String matIps[][] = Matriz.ResultSetAMatriz(rsIpsEstado);
        List<String> ips = new ArrayList();
        try{
            rsIps.beforeFirst();
            int i=0;
            while(rsIps.next()){
                String ip = rsIps.getString("ip")!=null ? rsIps.getString("ip") : "";
                if(!this.enMatriz(matIps, ip)){
                    ips.add(ip);
                    if(i>30){
                        break;
                    }
                    i++;
                }
            }
            rsIps.close();
        }catch(Exception e){
            e.printStackTrace();
        }
        return ips;
    }
    
    public boolean enMatriz(String mat[][], String ip)
    {
        if(mat!=null){
            boolean b = false;
            for(int i=0; i<mat.length; i++){
                if(mat[i][0].compareTo(ip)==0){
                    if(mat[i][1].compareTo("p")==0 || mat[i][1].compareTo("a")==0 || mat[i][1].compareTo("s")==0 
                            || mat[i][1].compareTo("c")==0 || mat[i][1].compareTo("r")==0 || mat[i][1].compareTo("d")==0 
                            || mat[i][1].compareTo("n")==0){
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public String getMailInstalacion(String id)
    {
        String mail = "";
        try{
            ResultSet rs = this.consulta("SELECT email FROM vta_instalacion where id_instalacion="+id+";");
            if(rs.next()){
                mail = rs.getString("email")!=null ? rs.getString("email") : "";
                rs.close();
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return mail;
    }

    public ResultSet getAntenasAcopladas()
    {
        return this.consulta("SELECT distinct antena_acoplada, antena_acoplada FROM tbl_instalacion order by antena_acoplada");
    }

    public boolean hayPreFacturasGeneradas(int anio, int mes)
    {
        String fecha_ini = anio + "-" + mes + "-01";
        String fecha_fin = anio + "-" + mes + "-" + Fecha.getUltimoDiaMes(anio, mes);
        try{
            ResultSet res = this.consulta("SELECT count(id_prefactura) FROM tbl_prefactura WHERE fecha_prefactura between '"+fecha_ini+"' and '"+fecha_fin+"';");
            if(res.next()){
                String num = (res.getString(1)!=null) ? res.getString(1) : "0";
                if(Long.parseLong(num)>0){
                    return true;
                }
                res.close();
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return false;
    }

    public int getAnioMinInstalacion()
    {
        int anio = Fecha.getAnio();
        try{
            ResultSet res = this.consulta("select min(date_part('year', fecha_registro)) from tbl_instalacion;");
            if(res.next()){
                anio = (res.getString(1)!=null) ? res.getInt(1) : anio;
                res.close();
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return anio;
    }

    public ResultSet getMateriales(String id)
    {
        return this.consulta("SELECT * FROM vta_instalacion_material where id_instalacion="+id+";");
    }

    public String getNumInstalacionJSON()
    {
        ResultSet rs = this.consulta("select S.id_sucursal, case when max(I.num_instalacion)>0 then max(I.num_instalacion)+1 else 1 end "
                + "from tbl_sucursal as S left outer join tbl_instalacion as I on S.id_sucursal=I.id_sucursal group by S.id_sucursal;");
        String tbl = this.getJSON(rs);
        try{
            rs.close();
        }catch(Exception e){
            e.printStackTrace();
        }
        return tbl;
    }

    public String getNumInstalacion(int idSuc)
    {
        String r = "1";
        try{
            ResultSet rs = this.consulta("select case when max(num_instalacion)>0 then max(num_instalacion)+1 else 1 end from tbl_instalacion where id_sucursal="+idSuc);
            if(rs.next()){
                r = rs.getString(1)!=null ? rs.getString(1) : "1";
                rs.close();
            }
            rs.close();
        }catch(Exception e){
            e.printStackTrace();
        }
        return r;
    }
    
    public String getNumOrdenServicio(int idSuc)
    {
        String r = "1";
        try{
            ResultSet rs = this.consulta("select case when max(num_orden_servicio)>0 then max(num_orden_servicio)+1 else 1 end from tbl_instalacion where id_sucursal="+idSuc);
            if(rs.next()){
                r = rs.getString(1)!=null ? rs.getString(1) : "1";
                rs.close();
            }
            rs.close();
        }catch(Exception e){
            e.printStackTrace();
        }
        return r;
    }
    
    public String getIPsJSON()
    {
        ResultSet rs = this.consulta("select S.id_sucursal, S.ip_inicio, int8ToInet( max( replace(inet_out(ip + 1)::varchar, '.', '')::int8 )) "
                + "from tbl_sucursal as S left outer join tbl_instalacion as I on S.id_sucursal=I.id_sucursal group by S.id_sucursal, S.ip_inicio;");
        String tbl = this.getJSON(rs);
        try{
            rs.close();
        }catch(Exception e){
            e.printStackTrace();
        }
        return tbl;
    }
    
    public String getSigIpSucursal(int id_sucursal)
    {
        String ip = "";
        try{
            ResultSet rs = this.consulta("select S.id_sucursal, S.ip_inicio, int8ToInet( max( replace(inet_out(ip + 1)::varchar, '.', '')::int8 )) as ip "
                + "from tbl_sucursal as S left outer join tbl_instalacion as I on S.id_sucursal=I.id_sucursal where S.id_sucursal="+id_sucursal+
                " group by S.id_sucursal, S.ip_inicio;");
            if(rs.next()){
                ip = rs.getString("ip")!=null ? rs.getString("ip") : "";
                String vecIp[] = ip.replace(".", ":") .split(":");
                if(vecIp[3].compareTo("255")==0){
                    ip = vecIp[0] + "." + vecIp[1] + "." + (Integer.parseInt(vecIp[2])+1 ) + ".2";
                }
                rs.close();
            }
        }catch(Exception e){
            e.printStackTrace();
            return "";
        }
        return ip;
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
            param += "['"+vecArti[i]+"','"+vecCant[i]+"','"+vecPU[i]+"','"+vecSubt[i]+"','"+vecDes[i]+"','"+vecIva[i]+"','"+vecTot[i]+"', ''],";
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

    public String insertar(String id_contrato, String num_instalacion, String id_sucursal, String id_provincia, String id_ciudad, String id_parroquia, 
            String id_sector, String tipo_instalacion, String costo_instalacion, String direccion_instalacion, String ip, String ip_radio, String id_plan_contratado, String es_instalacion, 
            String cobrar, String motivo_no_cobrar, String radusername, String radclave, String deviceclave,

            int id_sucursal_venta, int id_punto_emision, String id_cliente, String usuario, String serie_factura, String num_factura, String autorizacion, 
            String ruc,String razon_social, String fecha_emision, String direccion, String telefono, String forma_pago, String banco, String num_cheque, 
            String num_comp_pago, String gastos_bancos, String id_plan_cuenta_banco,String son, String observacion, String subtotal, String subtotal_2, 
            String subtotal_0, String descuento, String iva_2, String total, String paramArtic, String ret_num_serie, String ret_num_retencion, 
            String ret_autorizacion, String ret_fecha_emision, String ret_ejercicio_fiscal_mes, String ret_ejercicio_fiscal, String ret_impuesto_retenido, 
            String paramRet, String paramAsiento, String xmlFirmado)
    {
        
        String id_factura_id_Instalacion = "-1:-1";
        Connection con = this.getConexion();
        try{
            String num_orden_servicio = this.getNumOrdenServicio(id_sucursal_venta);
            con.setAutoCommit(false);
            Statement st = con.createStatement();
            ip_radio = ip_radio.compareTo("")!=0 ? "'" + ip_radio + "'" : "NULL";
            String sql = "INSERT INTO tbl_instalacion(num_instalacion, num_orden_servicio, id_sucursal, id_cliente, id_provincia, id_ciudad, id_parroquia, id_sector, tipo_instalacion, costo_instalacion, direccion_instalacion, "
                + "id_contrato, fecha_instalacion, ip, ip_radio, id_plan_contratado, id_plan_establecido, id_plan_actual, es_instalacion, cobrar, motivo_no_cobrar, estado_instalacion, radusername, radclave, deviceclave, alias) "
                + "VALUES("+num_instalacion+", "+num_orden_servicio+", "+id_sucursal+", "+id_cliente+", "+id_provincia+", "+id_ciudad+", "+id_parroquia+", "+id_sector+", '"+tipo_instalacion+"', "+costo_instalacion+", '"+direccion_instalacion+"', "+id_contrato+
                ", NULL, '"+ip+"', "+ip_radio+", "+id_plan_contratado+", "+id_plan_contratado+", "+id_plan_contratado+", "+es_instalacion+", "+cobrar+", '"+motivo_no_cobrar+"', 'e', '"+radusername+"', '"+radclave+"', '"+deviceclave+"', '"+usuario+"');";
            if(st.executeUpdate(this.decodificarURI(sql), Statement.RETURN_GENERATED_KEYS) > 0){
                String pk = "-1";
                ResultSet rs = st.getGeneratedKeys();
                if(rs.next()){
                    pk = rs.getString(1) != null ? rs.getString(1) : "-1";
                    rs.close();
                }
                
                if(es_instalacion.compareTo("TRUE")==0){
                    ret_fecha_emision = ret_fecha_emision.compareTo("")!=0 ? "'"+ret_fecha_emision+"'" : "NULL";
                    ResultSet res = this.consulta("select facturaVenta("+id_sucursal+", "+id_punto_emision+", "+id_cliente+", '"+usuario+"', '"+serie_factura+
                    "', "+num_factura+", '"+autorizacion+"', '"+ruc+"', '"+razon_social+"', '"+fecha_emision+"', '"+direccion+
                    "', '"+telefono+"', '"+forma_pago+"', '"+banco+"', '"+num_cheque+"', '"+num_comp_pago+"', "+gastos_bancos+
                    ", "+id_plan_cuenta_banco+", '"+son+"', '"+observacion+"', "+subtotal+", "+subtotal_2+", "+subtotal_0+", "+descuento+
                    ", "+iva_2+", "+total+", "+paramArtic+", '"+ret_num_serie+"', '"+ret_num_retencion+"', '"+ret_autorizacion+"', "+ret_fecha_emision+
                    ", '"+ret_ejercicio_fiscal_mes+"', "+ret_ejercicio_fiscal+", "+ret_impuesto_retenido+", "+paramRet+", "+paramAsiento+", '"+xmlFirmado+"');");
                    String resFactVent = "-1";
                    if(res.next()){
                        resFactVent = (res.getString(1)!=null) ? res.getString(1) : "-1";
                        res.close();
                    }

                    if(resFactVent.compareTo("-1") != 0){
                        id_factura_id_Instalacion = resFactVent + ":" + pk;
                        String vec[] = resFactVent.split(":");
                        st.executeUpdate("UPDATE tbl_instalacion SET id_factura_venta="+vec[0]+" WHERE id_instalacion="+pk+";");
                        st.executeUpdate("UPDATE tbl_factura_venta SET id_instalacion="+pk+", ip='"+ip+"' WHERE id_factura_venta="+vec[0]+";");
                        con.commit();
                    }else{
                        id_factura_id_Instalacion = "-1:-1";
                        con.rollback();
                        this.ejecutar("delete from tbl_instalacion where id_instalacion="+pk);
                    }
                }else{
                    id_factura_id_Instalacion = "-1:" + pk;
                    con.commit();
                }
            }
            st.close();
        }catch(Exception e){
            this.setError(e.getMessage());
            e.printStackTrace();
            id_factura_id_Instalacion = "-1:-1";
            try {
                con.rollback();
            }
            catch (Exception se) {
                se.printStackTrace();
            }
        }
        
        return id_factura_id_Instalacion;
    }

    public boolean tieneDeuda(String id, String id_cliente, String estado)
    {
        if(estado.compareTo("a")==0){
            try{
                ResultSet res = this.consulta("SELECT * FROM vta_prefactura where getFechaSuspension(fecha_prefactura) <= now()::date and fecha_emision is null and id_instalacion="+id+";");
                if(this.getFilas(res)>0){
                    return true;
                }else{
                    res = this.consulta("SELECT * FROM vta_cliente where id_cliente="+id_cliente+" and credito>0;");
                    if(this.getFilas(res)>0){
                        return true;
                    }
                }
                res.close();
            }catch(Exception e){
                e.printStackTrace();
            }
        }
        return false;
    }
     
    
    public boolean hayRadUserName(String id, String radUserName)
    {
        ResultSet res = this.consulta("SELECT * FROM tbl_instalacion where radusername='"+radUserName+"' and estado_servicio not in ('t','e') and anulado=false and id_instalacion<>"+id+";");
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

    public boolean setRadusername(String id_instalacion, String radusername)
    {
        return this.ejecutar("update tbl_instalacion set radusername='"+radusername+"' where id_instalacion="+id_instalacion);
    }
    
    public boolean actualizar(String id_instalacion, String id_sector)
    {
        return this.ejecutar("update tbl_instalacion set id_sector="+id_sector+" where id_instalacion="+id_instalacion);
    }
    
    public boolean actualizar(String id_instalacion, String id_provincia, String id_ciudad, String id_parroquia, 
            String id_sector, String direccion_instalacion, String ip, String cobrar, String set_deviceclave)
    {
        return this.ejecutar("update tbl_instalacion set id_provincia="+id_provincia+", id_ciudad="+id_ciudad+
                ", id_parroquia="+id_parroquia+", id_sector="+id_sector+", direccion_instalacion='"+direccion_instalacion+
                "', ip='"+ip+"', cobrar="+cobrar+", set_deviceclave="+set_deviceclave+" where id_instalacion="+id_instalacion);
    }
    
    public boolean actualizar(String id_instalacion, String id_sucursal, String id_provincia, String id_ciudad, String id_parroquia, 
            String id_sector, String direccion_instalacion, String ip, String cobrar, String motivo_no_cobrar, String estado_servicio, 
            String set_deviceclave)
    {
        return this.ejecutar("update tbl_instalacion set id_sucursal="+id_sucursal+", id_provincia="+id_provincia+", id_ciudad="+id_ciudad+
                ", id_parroquia="+id_parroquia+", id_sector="+id_sector+", direccion_instalacion='"+direccion_instalacion+"', ip='"+ip+
                "', cobrar="+cobrar+", motivo_no_cobrar='"+motivo_no_cobrar+"', estado_servicio='"+estado_servicio+
                "', set_deviceclave="+set_deviceclave+" where id_instalacion="+id_instalacion);
    }
    
    public boolean actualizar(String id_instalacion, String id_orden_trabajo, String id_sector, String direccion_instalacion, String ip, String mac_ant, String idsActivosRet, 
            String receptor_ant, String mac_nuevo, String idsActivos, String receptor_nuevo, String porcentaje_senal, String antena_acoplada, String latitud, String longitud, String altura, String altura_antena)
    {
        String set = "";
        if(id_sector.compareTo("")!=0 && id_sector.compareTo("-0")!=0){
            set += "id_sector="+id_sector;
        }
        if(direccion_instalacion.compareTo("")!=0){
            set += (set.compareTo("")!=0?",":"") + "direccion_instalacion='"+direccion_instalacion+"'";
        }
        if(ip.compareTo("")!=0){
            set += (set.compareTo("")!=0?",":"") + "ip='"+ip+"'";
        }
        /*if(id_plan_actual.compareTo("")!=0 && id_plan_actual.compareTo("-0")!=0){
            set += (set.compareTo("")!=0?",":"") + "id_plan_actual="+id_plan_actual;
        }*/
        if(mac_nuevo.compareTo("")!=0){
            set += (set.compareTo("")!=0?",":"") + "mac='"+mac_nuevo+"'";
        }
        if(receptor_nuevo.compareTo("")!=0){
            set += (set.compareTo("")!=0?",":"") + "receptor='"+receptor_nuevo+"'";
        }
        if(porcentaje_senal.compareTo("")!=0){
            set += (set.compareTo("")!=0?",":"") + "porcentaje_senal="+porcentaje_senal;
        }
        if(antena_acoplada.compareTo("")!=0){
            set += (set.compareTo("")!=0?",":"") + "antena_acoplada='"+antena_acoplada+"'";
        }
        if(latitud.compareTo("")!=0){
            set += (set.compareTo("")!=0?",":"") + "latitud='"+latitud+"'";
        }
        if(longitud.compareTo("")!=0){
            set += (set.compareTo("")!=0?",":"") + "longitud='"+longitud+"'";
        }
        if(altura.compareTo("")!=0){
            set += (set.compareTo("")!=0?",":"") + "altura='"+altura+"'";
        }
        if(altura_antena.compareTo("")!=0){
            set += (set.compareTo("")!=0?",":"") + "altura_antena='"+altura_antena+"'";
        }
        List sql = new ArrayList();
        sql.add("insert into tbl_ot_solucion(id_instalacion,id_orden_trabajo,porcentaje_senal_ant, porcentaje_senal_act, ip_ant, ip_act, mac_ant, mac_act, direccion_ant, direccion_act, receptor_ant, receptor_act, id_sector_ant, id_sector_act, antena_acoplada_ant, antena_acoplada_act, latitud_ant, latitud_act, longitud_ant, longitud_act, altura_ant, altura_act, altura_antena_ant, altura_antena_act) "
                + "select id_instalacion, "+id_orden_trabajo+", porcentaje_senal, "+porcentaje_senal+", ip, '"+ip+"', '"+mac_ant+"', '"+mac_nuevo+"', direccion_instalacion, '"+direccion_instalacion+"', '"+receptor_ant+"', '"+receptor_nuevo+"', id_sector, '"+id_sector+"', antena_acoplada, '"+antena_acoplada+"', latitud, '"+latitud+"', longitud, '"+longitud+"', altura, '"+altura+"', altura_antena, '"+altura_antena+"' from tbl_instalacion where id_instalacion="+id_instalacion+";");
        String codigo_ant[] = idsActivosRet.split(",");
        for(int i=0; i<codigo_ant.length; i++){
            sql.add("DELETE FROM tbl_instalacion_activo WHERE id_activo="+codigo_ant[i]+";");
        }
        String codigo[] = idsActivos.split(",");
        for(int i=0; i<codigo.length; i++){
            sql.add("insert into tbl_instalacion_activo(id_instalacion, id_activo) values("+id_instalacion+", "+codigo[i]+");");
        }
        if(set.compareTo("")!=0){
            sql.add("update tbl_instalacion set "+set+" where id_instalacion="+id_instalacion+";");
        }
        return this.transacciones(sql);
    }
    
    public boolean actualizar(String id_instalacion, String id_sector, String direccion_instalacion, String ip, String id_plan_establecido, String mac,
            String receptor, String porcentaje_senal, String antena_acoplada, String latitud, String longitud, String altura, String altura_antena,
            String fecha_instalacion, String conformidad_velocidad, String conformidad_instalacion,String conformidad_atencion, String estado_instalacion)
    {
        return this.ejecutar("update tbl_instalacion set id_sector="+id_sector+", direccion_instalacion='"+direccion_instalacion+"', ip='"+ip+"', id_plan_establecido="+id_plan_establecido+", id_plan_actual="+id_plan_establecido+", mac='"+mac+
                "', receptor='"+receptor+"', porcentaje_senal="+porcentaje_senal+", antena_acoplada='"+antena_acoplada+"', latitud='"+latitud+"', longitud='"+longitud+"', altura='"+altura+
                "', altura_antena='"+altura_antena+"', fecha_instalacion='"+fecha_instalacion+"', conformidad_velocidad="+conformidad_velocidad+", conformidad_instalacion='"+conformidad_instalacion+"', conformidad_atencion='"+conformidad_atencion+
                "', estado_instalacion='"+estado_instalacion+"' where id_instalacion="+id_instalacion);

        /*return this.ejecutar("update tbl_instalacion set id_sector="+id_sector+", direccion_instalacion='"+direccion_instalacion+"', ip='"+ip+"', id_plan_establecido="+id_plan_establecido+", id_plan_actual="+id_plan_establecido+", mac='"+mac+
                "', receptor='"+receptor+"', porcentaje_senal="+porcentaje_senal+", antena_acoplada='"+antena_acoplada+"', latitud='"+latitud+"', longitud='"+longitud+"', altura='"+altura+
                "', altura_antena='"+altura_antena+"', fecha_instalacion='"+fecha_instalacion+"', conformidad_velocidad="+conformidad_velocidad+", conformidad_instalacion='"+conformidad_instalacion+"', conformidad_atencion='"+conformidad_atencion+
                "', estado_instalacion='"+estado_instalacion+"' where id_instalacion="+id_instalacion);*/
    }

    public boolean setEquipo(String id_instalacion, String mac, String receptor)
    {
        return this.ejecutar("update tbl_instalacion set mac='"+mac+"', receptor='"+receptor+"' where id_instalacion="+id_instalacion);
    }

    
    public boolean anular(String id)
    {
        boolean ok = false;
        try{
            ResultSet res = this.consulta("select proc_instalacionAnular("+id+");");
            if(res.next()){
                ok = (res.getString(1)!=null) ? res.getBoolean(1) : false;
                res.close();
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return ok;
    }


    /* POST INSTALACION */


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
    public String concatenarValores(String id_articulos, String cantidades, String precios_unitarios,
            String subtotales)
    {
        String param = "";
        String vecArti [] = id_articulos.split(",");
        String vecCant [] = cantidades.split(",");
        String vecPU [] = precios_unitarios.split(",");
        String vecSubt [] = subtotales.split(",");
        for(int i=0; i<vecArti.length; i++){
            param += "['"+vecArti[i]+"','"+vecCant[i]+"','"+vecPU[i]+"','"+vecSubt[i]+"'],";
        }
        param = param.substring(0, param.length()-1);
        return "array["+param+"]";
    }
    public boolean insertarPostInstalacion(String id_instalacion, int id_sucursal, String fecha_instalacion, String receptor, String mac, String codsMateriales, 
                String id_personalizacion, String porcentaje_senal,String antena_acoplada, String id_plan_establecido,
                String conformidad_velocidad, String conformidad_instalacion,String conformidad_atencion, String estado_instalacion,
                //float costo_materiales, String id_productos, String cantidades, String precios_unitarios, String subtotales,
                String latitud, String longitud, String altura, String altura_antena)
    {
        /*String num = "-1";
        try{
            String paramProductos = this.concatenarValores(id_productos, cantidades, precios_unitarios, subtotales);
            ResultSet res = this.consulta("select proc_postInstalacion("+id_instalacion+", "+id_sucursal+", '"+fecha_instalacion+"', '"+receptor+"', '"+mac+
                    "', "+id_personalizacion+", "+porcentaje_senal+", '"+antena_acoplada+"', "+id_plan_establecido+", '"+conformidad_velocidad+"', '"+
                    conformidad_instalacion+"', '"+conformidad_atencion+"', '"+estado_instalacion+"', "+costo_materiales+", "+paramProductos+
                    ", '"+latitud+"', '"+longitud+"', '"+altura+"', '"+altura_antena+"');");
            if(res.next()){
                num = (res.getString(1)!=null) ? res.getString(1) : "-1";
                res.close();
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return num;*/
        List sql = new ArrayList();
        String planes = id_plan_establecido.compareTo("-0")!=0 ? ",id_plan_establecido="+id_plan_establecido+",id_plan_actual="+id_plan_establecido : "";
        sql.add("update tbl_instalacion set fecha_instalacion='"+fecha_instalacion+"', receptor='"+receptor+"', mac='"+mac+"', id_activo_personalizacion="+id_personalizacion+","
                + "porcentaje_senal="+porcentaje_senal+", antena_acoplada='"+antena_acoplada+"' " + planes
                + ",conformidad_velocidad="+conformidad_velocidad+", conformidad_instalacion='"+conformidad_instalacion+"',"
                + "conformidad_atencion='"+conformidad_atencion+"', estado_instalacion='"+estado_instalacion+"', "
                + "ingreso_datos=now()::date, estado_servicio='a', latitud='"+latitud+"', longitud='"+longitud+"',"
                + "altura='"+altura+"', altura_antena='"+altura_antena+"' where id_instalacion="+id_instalacion);
        String codigo[] = codsMateriales.split(",");
        for(int i=0; i<codigo.length; i++){
            if(codigo[i].compareTo("")!=0){
                sql.add("insert into tbl_instalacion_activo(id_instalacion, id_activo) values("+id_instalacion+", "+codigo[i]+");");
            }
        }
        return this.transacciones(sql);
    }
    
    public boolean actualizarPostInstalacion(String id_instalacion, String fecha_instalacion, String receptor, String mac,
                String id_personalizacion, String porcentaje_senal, String antena_acoplada, String id_plan_establecido,
                String conformidad_velocidad, String conformidad_instalacion, String conformidad_atencion, String estado_instalacion,
                //float costo_materiales, String id_productos, String cantidades, String precios_unitarios, String subtotales,
                String latitud, String longitud, String altura, String altura_antena)
    {
        /*boolean ok = false;
        try{
            String paramProductos = this.concatenarValores(id_productos, cantidades, precios_unitarios, subtotales);
            ResultSet res = this.consulta("select proc_editarPostInstalacion("+id_instalacion+", '"+fecha_instalacion+"', '"+receptor+"', '"+mac+
                    "', "+id_personalizacion+", "+porcentaje_senal+", '"+antena_acoplada+"', "+id_plan_establecido+", '"+conformidad_velocidad+"', '"+
                    conformidad_instalacion+"', '"+conformidad_atencion+"', '"+estado_instalacion+"', "+costo_materiales+", "+paramProductos+
                    ", '"+latitud+"', '"+longitud+"', '"+altura+"', '"+altura_antena+"');");
            if(res.next()){
                ok = (res.getString(1)!=null) ? res.getBoolean(1) : false;
                res.close();
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return ok;*/
        return this.ejecutar("update tbl_instalacion set receptor='"+receptor+"', mac='"+mac+"', id_activo_personalizacion="+id_personalizacion+","
                + "porcentaje_senal="+porcentaje_senal+", antena_acoplada='"+antena_acoplada+"',"
                + "conformidad_velocidad="+conformidad_velocidad+", conformidad_instalacion='"+conformidad_instalacion+"',"
                + "conformidad_atencion='"+conformidad_atencion+"', estado_instalacion='"+estado_instalacion+"', "
                + "latitud='"+latitud+"', longitud='"+longitud+"',"
                + "altura='"+altura+"', altura_antena='"+altura_antena+"' where id_instalacion="+id_instalacion);
    }

    public boolean setAntenaAcoplada(String id, String antena)
    {
        return this.ejecutar("update tbl_instalacion set antena_acoplada='"+antena+"' where id_instalacion="+id);
    }
    public boolean setAntenaMacReceptor(String id, String antena, String mac, String receptor)
    {
        return this.ejecutar("update tbl_instalacion set antena_acoplada='"+antena+"', mac='"+mac+"', receptor='"+receptor+"' where id_instalacion="+id);
    }
        /*  CAMBIOS DE RAZON SOCIAL  */

    public ResultSet getCambioCliente(String id)
    {
        return this.consulta("SELECT * FROM vta_instalacion_cambio_cliente where id_instalacion_cliente="+id+";");
    }
    public ResultSet getCambioClientes(String id)
    {
        return this.consulta("SELECT * FROM vta_instalacion_cambio_cliente where id_instalacion="+id+";");
    }
    /*public boolean enConflictoCambioCliente(String id_cambio_cliente, String id_instalacion, String fecha_cambio)
    {
        ResultSet res = this.consulta("SELECT * FROM tbl_instalacion_cliente where '"+fecha_cambio+"' between fecha and (fecha + (1 month)::interval)::date "
                + "and eliminado=false and id_instalacion="+id_instalacion+" and id_instalacion_cliente<>"+id_cambio_cliente+";");
        if(this.getFilas(res)>0){
            return true;
        }
        try{
            res.close();
        }catch(Exception e){
            e.printStackTrace();
        }
        return false;
    }*/
    public String insertarCambioCliente(String id_instalacion, String id_cliente, String fecha_cambio, String usuario)
    {
        String id="-1";
        try{
            ResultSet res = this.consulta("SELECT id_cliente, id_contrato FROM tbl_instalacion where id_instalacion="+id_instalacion+"");
            if(res.next()){
                String id_cliente_actual = res.getString("id_cliente")!=null ? res.getString("id_cliente") : "";
                String id_contrato = res.getString("id_contrato")!=null ? res.getString("id_contrato") : "";
                if(id_cliente_actual.compareTo("")!=0){
                    
                    id = this.insert("insert into tbl_instalacion_cliente(id_instalacion, id_cliente, fecha) values("+id_instalacion+", "+id_cliente_actual+", '"+fecha_cambio+"');");
                    if(id.compareTo("-1")!=0){
                        if(!this.ejecutar("update tbl_instalacion set id_cliente="+id_cliente+", radusername='"+usuario+"' where id_instalacion="+id_instalacion)){
                            this.ejecutar("delete from tbl_instalacion_cliente where id_instalacion_cliente="+id);
                        }
                    }
                    
                    try{
                        ResultSet rs = this.consulta("select * from tbl_instalacion where id_cliente="+id_cliente_actual+" and estado_servicio not in ('r','t') and anulado=false");
                        if(this.getFilas(rs)==0){
                            this.ejecutar("update tbl_contrato set terminado=true where id_contrato="+id_contrato);
                        }
                    }catch(Exception ex){
                        ex.printStackTrace();
                    }
                    
                }
                res.close();
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return id;
    }
    /*public boolean actualizarCambioCliente(String id_instalacion, String id_cliente)
    {
        return this.ejecutar("update tbl_instalacion set id_cliente="+id_cliente+" where id_instalacion="+id_instalacion+";");
    }*/


    /*  CAMBIOS DE PLAN  */

    public ResultSet getCambioPlan(String id)
    {
        return this.consulta("SELECT * FROM vta_instalacion_cambio_plan where id_instalacion_cambio_plan="+id+";");
    }
    public ResultSet getCambioPlanes(String id)
    {
        return this.consulta("SELECT * FROM vta_instalacion_cambio_plan where id_instalacion="+id+" order by id_instalacion_cambio_plan desc");
    }
    public boolean enConflictoCambioPlan(String id_cambio_plan, String id_instalacion, String fecha_cambio)
    {
        ResultSet res = this.consulta("SELECT * FROM tbl_instalacion_cambio_plan where '"+fecha_cambio+"' between fecha_cambio and (fecha_cambio + (1 month)::interval)::date "
                + "and eliminado=false and id_instalacion="+id_instalacion+" and id_instalacion_cambio_plan<>"+id_cambio_plan+";");
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
    public String insertarCambioPlan(String id_instalacion, String id_plan_servicio, String fecha_cambio)
    {
        String pk = this.insert("insert into tbl_instalacion_cambio_plan(id_instalacion, id_plan_servicio, fecha_cambio) "
                    + "values("+id_instalacion+", "+id_plan_servicio+", '"+fecha_cambio+"');");
        if(pk.compareTo("-1")!=0){
            this.ejecutar("update tbl_instalacion_cambio_plan set vigente=false where vigente=true and id_instalacion="+id_instalacion);
            if(!this.ejecutar("update tbl_instalacion set id_plan_actual="+id_plan_servicio+" where id_instalacion="+id_instalacion)){
                this.ejecutar("delete from tbl_instalacion_cambio_plan where id_instalacion_cambio_plan="+pk);
                pk="-1";
            }
        }
        return pk;
    }
    public boolean actualizarCambioPlan(String id_instalacion_cambio_plan, String id_instalacion, String id_plan_servicio, String fecha_cambio)
    {
        List sql = new ArrayList();
        sql.add("update tbl_instalacion_cambio_plan set id_plan_servicio="+id_plan_servicio+" where id_instalacion_cambio_plan="+id_instalacion_cambio_plan+";");
        try{
            ResultSet rs = this.consulta("select max(id_instalacion_cambio_plan) from tbl_instalacion_cambio_plan "
                    + "where id_instalacion="+id_instalacion+" and eliminado=false");
            if(rs.next()){
                String id_icp = rs.getString(1)!=null ? rs.getString(1) : "";
                if(id_icp.compareTo(id_instalacion_cambio_plan)==0){
                    sql.add("update tbl_instalacion set id_plan_actual="+id_plan_servicio+" where id_instalacion="+id_instalacion);
                }
                rs.close();
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return this.transacciones(sql);
    }

    /*  SUSPENCIONES  */

    public ResultSet getSuspension(String id)
    {
        return this.consulta("SELECT * FROM vta_instalacion_suspension where id_instalacion_suspension="+id+";");
    }
    public ResultSet getSuspensiones(String id)
    {
        return this.consulta("SELECT * FROM vta_instalacion_suspension where id_instalacion="+id+";");
    }
    public ResultSet getSuspensionesServidor()
    {
        return this.consulta("SELECT distinct razon_social, ip, txt_sucursal, id_sucursal, estado_servicio FROM vta_instalacion "
                + "where estado_servicio in ('p', 's', 'c', 'r', 'd', 'n') order by id_sucursal");
    }
    public ResultSet getActivacionesServidor()
    {
        return this.consulta("SELECT distinct razon_social, ip, txt_sucursal, id_sucursal, estado_servicio, lower(plan) as plan FROM vta_instalacion "
                + "where estado_servicio='a' order by id_sucursal");
    }
    public ResultSet getActivacionesServidor(String idSuc)
    {
        return this.consulta("SELECT distinct razon_social, ip, txt_sucursal, id_sucursal, estado_servicio, lower(plan) as plan FROM vta_instalacion "
                + "where estado_servicio in ('p', 'a') and id_sucursal="+idSuc+" order by id_sucursal");
    }
    public ResultSet getColasServidorSucursal(String idSuc)
    {
        return this.consulta("SELECT distinct razon_social || ' ' || id_instalacion as razon_social, ip, P.*, case P.comparticion when 1 then 2 when 3 then 3 when 8 then 8 else 8 end as prioridad, I.plan "
                + "FROM vta_instalacion as I inner join vta_plan_servicio as P on I.id_plan_actual=P.id_plan_servicio "
                + "where estado_servicio in ('p', 'a') and I.id_sucursal="+idSuc+";");
    }
    
    public ResultSet getColasServidor(String subred, String id_sucursal)
    {
        String ip = "127.0.0.1";
        //String mascara = "24";
        if(subred.contains("/") && subred.length()>0){
            String vecRed[] = subred.split("/");
            ip = vecRed[0].trim();
            //mascara = vecRed[1].trim();
        }else if(!subred.contains("/") && subred.length()>0){
                    ip = subred.trim();
              }
        String octetos[] = ip.replace(".", ";").split(";");
        String ipRed = "";
        for(int i=0; i<octetos.length-1; i++){
            ipRed += octetos[i] + ".";
        }
        if(ipRed.compareTo("")==0){
            ipRed = "127.0.0.";
        }
        
        return this.consulta("SELECT distinct razon_social || ' ' || id_instalacion as razon_social, ip, P.*, "
                + "case P.comparticion when 1 then 2 when 3 then 3 when 8 then 8 else 8 end as prioridad, I.plan "
                + "FROM vta_instalacion as I inner join vta_plan_servicio as P on I.id_plan_actual=P.id_plan_servicio "
                + "where estado_servicio in ('p', 'a') and ip::varchar like '"+ipRed+"%' and I.id_sucursal="+id_sucursal+" order by ip;");
    }
    
    public boolean enConflictoSuspension(String id_suspension, String id_instalacion, String fecha_inicio, String fecha_termino)
    {
        ResultSet res = this.consulta("SELECT * FROM tbl_instalacion_suspension where ('"+fecha_inicio+"' between fecha_inicio and fecha_termino or '"+fecha_termino+
                "' between fecha_inicio and fecha_termino) and eliminado=false and id_instalacion="+id_instalacion+" and id_instalacion_suspension<>"+id_suspension+";");
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
    public boolean haySuspencion(String id_instalacion, String fecha_inicio, String fecha_termino)
    {
        try{
            ResultSet res = this.consulta("SELECT * FROM tbl_instalacion_suspension where ('"+fecha_inicio+"' between fecha_inicio and fecha_termino or '"+fecha_termino+
                    "' between fecha_inicio and fecha_termino) and eliminado=false and id_instalacion="+id_instalacion);
            if(this.getFilas(res)>0){
                return true;
            }
            res.close();
        }catch(Exception e){
            e.printStackTrace();
        }
        return false;
    }
    public String insertarSuspension(String id_instalacion, String usuario, String tipo, String fecha_inicio, String fecha_termino, int tiempo)
    {
        return this.insert("insert into tbl_instalacion_suspension(id_instalacion, usuario_solicitud, tipo, fecha_inicio, fecha_termino, tiempo) "
                + "values("+id_instalacion+", '"+usuario+"', '"+tipo+"', '"+fecha_inicio+"', '"+fecha_termino+"', "+tiempo+");");
    }
    public boolean anularSuspension(String id_suspension, String usuario, String fecha_reactivacion, int num_meses)
    {
        String sql = "";
        if(num_meses==0){
            sql = "update tbl_instalacion_suspension set reac_usuario_solicitud='"+usuario+"', reac_fecha_reactivacion='"+fecha_reactivacion+
                    "', reac_fecha_solicitud=now(), eliminado=true where id_instalacion_suspension="+id_suspension;
        }else{
            num_meses--;
            sql = "update tbl_instalacion_suspension set reac_usuario_solicitud='"+usuario+"', reac_fecha_reactivacion='"+fecha_reactivacion+
                    "', reac_fecha_solicitud=now(), fecha_termino = fecha_inicio + '"+num_meses+" month'::interval, tiempo="+num_meses+
                    " where id_instalacion_suspension="+id_suspension;
        }
        return this.ejecutar(sql);
    }
    public boolean actualizarSuspension(String id_suspension, String tipo, String fecha_inicio, String fecha_termino, int tiempo)
    {
        return this.ejecutar("update tbl_instalacion_suspension set fecha_inicio='"+fecha_inicio+"', fecha_termino='"+fecha_termino+
                "', tiempo="+tiempo+", tipo='"+tipo+"' where id_instalacion_suspension="+id_suspension+";");
    }

    /*  DES - INSTALACION    */

    public String getNumDesInstalacion(String idSuc)
    {
        String num = "1";
        try{
            ResultSet rs = this.consulta("select case when max(num_instalacion)>0 then max(num_instalacion)+1 else 1 end "
                + "from tbl_instalacion where id_sucursal="+idSuc+";");
            if(rs.next()){
                num = rs.getString(1)!=null ? rs.getString(1) : "1";
                rs.close();
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return num;
    }
    public boolean setNoInstalacion(String id_instalacion, String fecha_visita_instalacion, String motivo_no_instalacion)
    {
        ResultSet rs = this.consulta("SELECT * FROM tbl_instalacion where id_instalacion="+id_instalacion+" and fecha_visita_instalacion is not null");
        try{
            if(this.getFilas(rs)==0){
                return this.ejecutar("update tbl_instalacion set fecha_visita_instalacion='"+fecha_visita_instalacion+"', fecha_no_instalacion=now(), motivo_no_instalacion='"+motivo_no_instalacion+"' where id_instalacion="+id_instalacion);
            }else{
                return this.ejecutar("update tbl_instalacion set fecha_visita_instalacion='"+fecha_visita_instalacion+"', motivo_no_instalacion='"+motivo_no_instalacion+"' where id_instalacion="+id_instalacion);
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return false;
    }
    public boolean porRetirar(String id_instalacion, String generar_orden_trab, String id_cliente, String num_desinstalacion, String fecha_desinstalacion, String motivo_desinstalacion)
    {
        boolean ok = false;
        ok = this.ejecutar("UPDATE tbl_instalacion SET num_desinstalacion="+num_desinstalacion+", generar_orden_trab="+generar_orden_trab+", fecha_desinstalacion='"+fecha_desinstalacion+
                "', motivo_desinstalacion='"+motivo_desinstalacion+"', estado_servicio='r', fecha_estado_r=now()::date WHERE id_instalacion="+id_instalacion);
        if(ok){
            this.ejecutar("UPDATE tbl_orden_trabajo SET fecha_solucion=now(), recomendacion='POR DES-INSTALACION DESDE ADMINISTRACION', estado='3' WHERE estado='1' and id_instalacion="+id_instalacion);
            try{
                ResultSet rs = this.consulta("select * from tbl_instalacion where id_cliente="+id_cliente+" and estado_servicio not in ('r','e','t') and anulado=false");
                if(this.getFilas(rs)==0){
                    ResultSet rsContrato = this.consulta("select id_contrato from tbl_instalacion where id_instalacion="+id_instalacion);
                    if(rsContrato.next()){
                        String id_contrato = rsContrato.getString("id_contrato")!=null ? rsContrato.getString("id_contrato") : "";
                        if(id_contrato.compareTo("")!=0){
                            this.ejecutar("update tbl_contrato set terminado=true where id_contrato="+id_contrato);
                        }
                    }
                }
            }catch(Exception e){
                e.printStackTrace();
            }
        }
        return ok;
    }
    
    public boolean desInstalar(String id_instalacion, String observacion_terminado, String estado, String codsMateriales)
    {
        List sql = new ArrayList();
        
        estado = estado.compareTo("r")==0 ? "null" : estado;
        sql.add("UPDATE tbl_instalacion SET observacion_terminado='"+observacion_terminado+"', estado_servicio='"+estado+"', fecha_terminado=now()::date WHERE id_instalacion="+id_instalacion);
        sql.add("UPDATE tbl_orden_trabajo SET fecha_solucion=now(), recomendacion='POR DES-INSTALACION DESDE ADMINISTRACION', estado='9' WHERE id_instalacion="+id_instalacion+";");
        String codigo[] = codsMateriales.split(",");
        for(int i=0; i<codigo.length; i++){
            if(codigo[i].compareTo("")!=0){
                sql.add("DELETE FROM tbl_instalacion_activo WHERE id_activo="+codigo[i]+";");
            }
        }
        return this.transacciones(sql);
    }

    
    //  COMISIONES
    
    public ResultSet getInstalacionesComision(String id_instalacion_comision)
    {
        return this.consulta("select I.id_plan_actual, I.razon_social, I.plan, I.fecha_registro, I.fecha_instalacion, "
                + "I.ingreso_datos, F.serie_factura || '-' || F.num_factura as num_factura, F.deuda "
                + "FROM (vta_instalacion as I inner join tbl_instalacion_comision as C on C.id_sucursal=I.id_sucursal) "
                + "left outer join tbl_factura_venta as F on F.id_factura_venta=I.id_factura_venta "
                + "where c.id_instalacion_comision="+id_instalacion_comision+" and I.anulado=false and I.cobrar=true and "
                + "I.ingreso_datos between C.fecha_inicio and C.fecha_termino "
                + "order by I.fecha_registro;");
    }
    
    
    
    
    //  FREE RADIUS

    public String getUsrFreeRadius(String id_plan_servicio)
    {
        String usuarios="";
        try{
            ResultSet rs = this.consulta("select radusername from tbl_instalacion where estado_servicio not in ('t','e') and id_plan_actual in ("+id_plan_servicio+")");
            while(rs.next()){
                usuarios += (rs.getString("radusername")!=null ? "'" + rs.getString("radusername") + "'," : "");
            }
            if(usuarios.compareTo("")!=0){
                usuarios = usuarios.substring(0, usuarios.length()-1);
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return usuarios;
    }
    
    public String getUsuariosFreeRadius(String id_instalaciones)
    {
        String usuarios="";
        try{
            ResultSet rs = this.consulta("select radusername from tbl_instalacion where id_instalacion in ("+id_instalaciones+")");
            while(rs.next()){
                usuarios += (rs.getString("radusername")!=null ? "'" + rs.getString("radusername") + "'," : "");
            }
            if(usuarios.compareTo("")!=0){
                usuarios = usuarios.substring(0, usuarios.length()-1);
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return usuarios;
    }
    
    public ResultSet getUsuariosNoRegistrados()
    {
        return this.consulta("SELECT * from tbl_instalacion as I where estado_servicio not in ('t', 'e') and "
                + "radusername not in (select username from radusergroup);");
    }
  
    public boolean setDeviceClave(String id_instalacion)
    {
        return this.ejecutar("update tbl_instalacion set set_deviceclave = not set_deviceclave where id_instalacion = " + id_instalacion);
    }
    
}