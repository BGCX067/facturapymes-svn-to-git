/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jm.web;

import java.sql.ResultSet;
import java.util.Date;
import javax.ejb.LocalBean;
import javax.ejb.Schedule;
import javax.ejb.Stateless;
import jm.Robot;
import jm.adm.clas.Configuracion;
import jm.inf.clas.Radius;

/**
 *
 * @author desarrollo
 */
@Stateless
@LocalBean
public class Autoejecucion {
    private String _ip = "127.0.0.1";
    private int _puerto= 5432;
    private String _db = "db_isp";
    private String usuario = "us_isp";
    private String clave = "";
    
    @Schedule(dayOfMonth="20", hour="00", minute="00", second="00")
    public void ejecutar()
    {
        /*Configuracion conf = new Configuracion(this._ip, this._puerto, this._db, usuario, clave);
        int dia_cortes = Integer.parseInt(conf.getValor("dia_cortes"));

        int hoy = Fecha.getDia();
        String dia_semana = Fecha.getDiaSemana(dia_cortes);
        if(dia_semana.compareTo("sábado")==0){
            hoy = hoy - 2;
        }
        if(dia_semana.compareTo("domingo")==0){
            hoy--;
        }
        
        Robot objRobot = new Robot(this._ip, this._puerto, this._db, usuario, clave);
        objRobot.generarEnFinMes();
        objRobot.generarPrefacturas();
        objRobot.generarPrefacturasFaltantes();
        objRobot.setInstalacionesPorRetirar(Fecha.getFecha("ISO"));
        
        Radius dbRadius = new Radius(this._ip, this._puerto, this._db, usuario, clave);
        ResultSet rsActivados = objRobot.getInstalacionesActivaciones();
        String usuariosActivados[][] = Matriz.ResultSetAMatriz(rsActivados);
        dbRadius.setActivaciones(usuariosActivados);
        if(hoy >= dia_cortes){
            String usuariosCortados = objRobot.getInstalacionesCortar();
            dbRadius.setCortes(usuariosCortados);
        }
        String usuariosSuspendidos = objRobot.getInstalacionesSuspender();
        dbRadius.setSuspenciones(usuariosSuspendidos);
        
        dbRadius.cerrar();
        objRobot.cerrar();
        conf.cerrar();*/
    }
}
