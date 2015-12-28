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

package jm;

import java.sql.ResultSet;
import jm.web.DataBase;

public class Robot extends DataBase{
    public Robot(String m, int p, String db, String u, String c){
        super(m, p, db, u, c);
    }

    public void generarEnFinMes()
    {
        this.consulta("select proc_robot();");
    }

    public void generarPrefacturas()
    {
        this.consulta("select proc_generarPreFacturas();");
    }
    
    public void generarComisiones()
    {
        this.consulta("select generarComisiones();");
    }

    public void generarPrefacturasFaltantes()
    {
        this.consulta("select proc_generarPrefacturasFaltantes();");
    }

    public void setInstalacionesPorRetirar(String periodo)
    {
        this.consulta("select proc_generarInstalacionesPorRetirar('"+periodo+"');");
    }

    public ResultSet getInstalacionesActivaciones()
    {
        return this.consulta("select I.radusername, substr( plan, 1, 4) || substr( split_part(solo_plan, ' ', 2), 1, 5) || substr( split_part(solo_plan, ' ', 3), 1, 4) || burst_limit::varchar "
                + "from tbl_instalacion as I inner join vta_plan_servicio as P  on I.id_plan_actual=P.id_plan_servicio where I.estado_servicio in ('a') and I.anulado=false");
    }
    
    public String getInstalacionesCortar()
    {
        String usrs = "";
        try{
            ResultSet rs = this.consulta("select radusername from tbl_instalacion where estado_servicio in ('c') and anulado=false");
            while(rs.next()){
                usrs += "'" + (rs.getString("radusername")!=null ? rs.getString("radusername") : "") + "',";
            }
            rs.close();
        }catch(Exception e){
            e.printStackTrace();
        }
        if(usrs.compareTo("")!=0){
            usrs = usrs.substring(0, usrs.length()-1);
        }
        return usrs;
    }
    
    public String getInstalacionesSuspender()
    {
        String usrs = "";
        try{
            ResultSet rs = this.consulta("select radusername from tbl_instalacion where estado_servicio in ('s') and anulado=false");
            while(rs.next()){
                usrs += "'" + (rs.getString("radusername")!=null ? rs.getString("radusername") : "") + "',";
            }
            rs.close();
        }catch(Exception e){
            e.printStackTrace();
        }
        if(usrs.compareTo("")!=0){
            usrs = usrs.substring(0, usrs.length()-1);
        }
        return usrs;
    }

}
