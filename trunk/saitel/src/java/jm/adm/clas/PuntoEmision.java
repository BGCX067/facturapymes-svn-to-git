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

public class PuntoEmision extends DataBase{
    public PuntoEmision(String m, int p, String db, String u, String c){
        super(m, p, db, u, c);
    }
    public ResultSet getPuntoEmision(String id)
    {
        return this.consulta("SELECT * from tbl_punto_emision WHERE id_punto_emision="+id);
    }
    public ResultSet getPuntosEmision(String id_sucursal)
    {
        return this.consulta("SELECT id_punto_emision,punto_emision,ip_computador from vta_punto_emision where id_sucursal="+id_sucursal+" order by punto_emision");
    }
    public int getIdPuntoEmision(String usuario_caja)
    {
        int id = -1;
        try{
            ResultSet res = this.consulta("SELECT id_punto_emision FROM tbl_punto_emision WHERE usuario_caja = '"+usuario_caja+"'");
            if(res.next()){
                id = res.getString(1)!=null ? res.getInt(1) : -1;
                res.close();
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return id;
    }
    public boolean estaDuplicado(String id, String punto_emision)
    {
        ResultSet res = this.consulta("SELECT * FROM tbl_punto_emision where lower(punto_emision)='"+punto_emision.toLowerCase()+"' and id_punto_emision<>"+id);
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
    public boolean ipDuplicada(String id, String ip)
    {
        String vec_ip[] = ip.split("-");
        String w = "";
        for(int i=0; i<vec_ip.length; i++){
            w += "usuario_caja = '"+vec_ip[i].trim()+"' or ";
        }
        if(w.compareTo("")!=0){
            w = "(" + w.substring(0, w.length()-3) + ")";
        }
        ResultSet r = this.consulta("SELECT * FROM tbl_punto_emision WHERE "+w+" and id_punto_emision<>"+id);
        if(this.getFilas(r)>0){
            return true;
        }
        try{
            r.close();
        }catch(Exception e){
            e.printStackTrace();
        }
        return false;
    }
    public String getIdCaja(int id_punto_emision)
    {
        String id = "-1";
        try{
            ResultSet res = this.consulta("SELECT id_plan_cuenta_caja FROM tbl_punto_emision WHERE id_punto_emision="+id_punto_emision);
            if(res.next()){
                id = res.getString(1)!=null ? res.getString(1) : "-1";
                res.close();
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return id;
    }
    public String getIdCaja(String cajero)
    {
        String id = "-1";
        try{
            ResultSet res = this.consulta("SELECT id_plan_cuenta_caja FROM tbl_punto_emision WHERE usuario_caja='"+cajero+"'");
            if(res.next()){
                id = res.getString(1)!=null ? res.getString(1) : "-1";
                res.close();
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return id;
    }
    public String getIdDiferenciaCaja(int id_punto_emision)
    {
        String id = "-1";
        try{
            ResultSet res = this.consulta("SELECT id_plan_cuenta_diferencia_caja FROM tbl_punto_emision WHERE id_punto_emision="+id_punto_emision);
            if(res.next()){
                id = res.getString(1)!=null ? res.getString(1) : "-1";
                res.close();
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return id;
    }
    public String insertar(String id_sucursal, String punto_emision, String usuario_caja, String id_plan_cuenta_caja, String id_plan_cuenta_diferencia_caja, 
            String fac_se, String fac_des, String fac_has, String fac_cad, String fac_aut, String num_fact_inicial)
    {
        return this.insert("INSERT INTO tbl_punto_emision(id_sucursal,punto_emision,usuario_caja,id_plan_cuenta_caja,id_plan_cuenta_diferencia_caja,fac_num_serie,fac_sec_desde,fac_sec_hasta,fac_cad_facturero,fac_autorizacion,num_fact_inicial) " +
                "VALUES("+id_sucursal+", '"+punto_emision+"', '"+usuario_caja+"', "+id_plan_cuenta_caja+", "+id_plan_cuenta_diferencia_caja+", '"+fac_se+"', '"+fac_des+"', '"+fac_has+"', '"+fac_cad+"', '"+fac_aut+"', "+num_fact_inicial+")");
    }
    public boolean actualizar(String id, String punto_emision, String usuario_caja, String id_plan_cuenta_caja, String id_plan_cuenta_diferencia_caja, String fac_se, String fac_des, String fac_has,
            String fac_cad, String fac_aut, String num_fact_inicial)
    {
        return this.ejecutar("UPDATE tbl_punto_emision SET punto_emision='"+punto_emision+"', usuario_caja='"+usuario_caja+"', id_plan_cuenta_caja='"+id_plan_cuenta_caja+"', id_plan_cuenta_diferencia_caja='"+id_plan_cuenta_diferencia_caja+"', fac_num_serie='"+fac_se+"', "
                + "fac_sec_desde='"+fac_des+"', fac_sec_hasta='"+fac_has+"', fac_cad_facturero='"+fac_cad+"', fac_autorizacion='"+fac_aut+"', num_fact_inicial="+num_fact_inicial+" "
                + "WHERE id_punto_emision="+id);
    }
    public boolean setFormatoValor(String id, String campo, String valor)
    {
        return this.ejecutar("UPDATE tbl_punto_emision SET "+campo+"='"+valor+"' WHERE id_punto_emision="+id);
    }
    public String tblPuntosEmision(String id_sucursal)
    {
        String html = "<TABLE cellspacing='0' cellpadding='0'><TR>" +
                        "<TH class='jm_TH' width='120'>P. DE EMISION</TH>" +
                        "<TH class='jm_TH' width='100'>USUARIO</TH>" +
                        "<TH class='jm_TH' width='100'>FACTURAS</TH>" +
                        "<TH class='jm_TH' width='20'>&nbsp;</TH></TR></TABLE>";
        html += "<DIV style='overflow:auto;width:360px;height:250px;' id='sll'>" +
        "<TABLE class='jm_tabla' cellspacing='1' cellpadding='0' id='tblI'>";
        try{
            int i=0;
            String id_punto_emision = "";
            String punto_emision = "";
            String ip_computador = "";
            ResultSet rsPuntos = this.getPuntosEmision(id_sucursal);
            while(rsPuntos.next()){
                id_punto_emision = (rsPuntos.getString("id_punto_emision")!=null) ? rsPuntos.getString("id_punto_emision") : "";
                punto_emision = (rsPuntos.getString("punto_emision")!=null) ? rsPuntos.getString("punto_emision") : "";
                ip_computador = (rsPuntos.getString("ip_computador")!=null) ? rsPuntos.getString("ip_computador") : "";
                html += "<tr id='rTI"+i+"' valign='top' class='jm_filaPar'>";
                html += "<td width='120' style='cursor:pointer' onclick=\"adm_PuntoEmisionEditar("+id_punto_emision+");\">"+punto_emision+"</td>";
                html += "<td width='100' style='cursor:pointer' onclick=\"adm_PuntoEmisionEditar("+id_punto_emision+");\">"+ip_computador+"</td>";
                html += "<td align='center' width='100'><a href='formatoFactura?id="+id_punto_emision+"' target='_blank'>modificar formato</a></td>";
                html += "<td align='center' width='20'><input type='hidden' id='idR"+i+"' value='"+id_punto_emision+"' />&nbsp;</td></tr>";
                i++;
            }
        }catch(Exception e){e.printStackTrace();}

        html += "</table>";

        return html;
    }
}