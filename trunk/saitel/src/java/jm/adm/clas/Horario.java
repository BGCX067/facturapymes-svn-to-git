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
import java.util.ArrayList;
import java.util.List;
import jm.web.DataBase;

public class Horario extends DataBase{
    public Horario(String m, int p, String db, String u, String c){
        super(m, p, db, u, c);
    }
    public ResultSet getHorario(String id)
    {
        return this.consulta("SELECT * from tab_horarios WHERE id_horario="+id+";");
    }
    public ResultSet getDetalleHorario(String id)
    {
        return this.consulta("SELECT * from vta_horariodetalle WHERE id_horario="+id+" order by dia;");
    }
    public boolean guardarHorario(String id_sucursal, String nombre, String modalidad, String eliminado, String dia, String horaentrada, String horasalida, String horaentrada1, String horasalida1, String horaentrada2, String horasalida2)
    {        
        List ejec= new ArrayList();
        String id=this.insert("insert into tab_horarios (id_sucursal, nombre, modalidad, eliminado) values ("+id_sucursal+",'"+nombre+"',"+modalidad+",'"+eliminado+"');");
        //return this.ejecutar("Update tbl_empleado set carnet='"+texto+"' where id_empleado="+id_empleado+";");
        if(id.compareTo("-1")!=0)
        {
            horaentrada = horaentrada.compareTo(":")!=0 ? "'"+horaentrada+"'" : "NULL";            horasalida = horasalida.compareTo(":")!=0 ? "'"+horasalida+"'" : "NULL";
            horaentrada1 = horaentrada1.compareTo(":")!=0 ? "'"+horaentrada1+"'" : "NULL";       horasalida1 = horasalida1.compareTo(":")!=0 ? "'"+horasalida1+"'" : "NULL";
            horaentrada2 = horaentrada2.compareTo(":")!=0 ? "'"+horaentrada2+"'" : "NULL";       horasalida2 = horasalida2.compareTo(":")!=0 ? "'"+horasalida2+"'" : "NULL";
            String[] carnetArray = dia.split(",");
            for (int j = 0; j<carnetArray.length; j++) {
                ejec.add("insert into tab_detallehorarios (id_horario, dia, horaentrada, horasalida, horaentrada1, horasalida1, horaentrada2, horasalida2) values ("+id+","+carnetArray[j]+","+horaentrada+","+horasalida+","+horaentrada1+","+horasalida1+","+horaentrada2+","+horasalida2+");");
            }
            if(!this.transacciones(ejec))
            {
                this.ejecutar("delete from tab_horarios where id_horario="+id+";");
                return false;
            }
            return true;
        }
        return false;
    }
    
    public boolean modificarHorario(String nombre, String modalidad, String eliminado, String id_horario)
    {
        return this.ejecutar("Update tab_horarios set nombre='"+nombre+"', modalidad="+modalidad+",eliminado="+eliminado+" where id_horario="+id_horario+";");
    }
    
    public boolean eliminarHorario(String id_detalle)
    {
        return this.ejecutar("Delete from tab_detallehorarios where id_detalle="+id_detalle+";");
    }
    
    public boolean modificarHorarioDetalle(String id_horario,String id_detalle, String horaentrada, String horasalida, String horaentrada1, String horasalida1, String horaentrada2, String horasalida2, String dias)
    {
        horaentrada = horaentrada.compareTo(":")!=0 ? "'"+horaentrada+"'" : "NULL";            horasalida = horasalida.compareTo(":")!=0 ? "'"+horasalida+"'" : "NULL";
        horaentrada1 = horaentrada1.compareTo(":")!=0 ? "'"+horaentrada1+"'" : "NULL";       horasalida1 = horasalida1.compareTo(":")!=0 ? "'"+horasalida1+"'" : "NULL";
        horaentrada2 = horaentrada2.compareTo(":")!=0 ? "'"+horaentrada2+"'" : "NULL";       horasalida2 = horasalida2.compareTo(":")!=0 ? "'"+horasalida2+"'" : "NULL";
        List ejec= new ArrayList();
        String[] carnetArray = dias.split(",");
        for (int j = 0; j<carnetArray.length; j++) {
            ResultSet id= this.consulta("SELECT * from vta_horariodetalle WHERE id_detalle="+id_detalle+" and dia="+carnetArray[j]+" order by dia;");
            if(this.getFilas(id)>0){
                ejec.add("Update tab_detallehorarios set horaentrada="+horaentrada+", horasalida="+horasalida+",horaentrada1="+horaentrada1+",horasalida1="+horasalida1+",horaentrada2="+horaentrada2+",horasalida2="+horasalida2+" where id_detalle="+id_detalle+";");
            }
            else{
                ejec.add("insert into tab_detallehorarios (id_horario, dia, horaentrada, horasalida, horaentrada1, horasalida1, horaentrada2, horasalida2) values ("+id_horario+","+carnetArray[j]+","+horaentrada+","+horasalida+","+horaentrada1+","+horasalida1+","+horaentrada2+","+horasalida2+");");
            }            
        }
        if(this.transacciones(ejec)){
            return true;
        }
        else{
            return false;
        }
        //return this.ejecutar("Update tab_detallehorarios set horaentrada="+horaentrada+", horasalida="+horasalida+",horaentrada1="+horaentrada1+",horasalida1="+horasalida1+",horaentrada2="+horaentrada2+",horasalida2="+horasalida2+" where id_detalle="+id_detalle+";");
    }
}
