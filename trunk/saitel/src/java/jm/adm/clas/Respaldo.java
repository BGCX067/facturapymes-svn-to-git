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

import jm.web.Fecha;

import java.io.*;
   
/**
 *
 * @author Jorge
 */
public class Respaldo {
    private String path_dump;
    private String path_respaldo;
    private String DB;
    private String usuario;
    private String clave;
    private String nom_archivo = "";
    
    public Respaldo()
    {       
    }
    
    public Respaldo(String path_dump, String path_respaldo, String DB, String usuario, String clave)
    {       
        this.path_dump = path_dump;
        this.path_respaldo = path_respaldo;
        this.DB = DB;
        this.usuario = usuario;
        this.clave = clave;
    }
    
    public String getNombreArchivo()
    {
        return this.nom_archivo;
    }
    
    public boolean respaldar()
    {
        try {
            String path = this.path_respaldo+"db_isp_"+Fecha.getFecha("ISO")+"_"+Fecha.getHora().replace(":", "-")+".backup";
            ProcessBuilder pb = new ProcessBuilder(this.path_dump, "-F", "c", "-b", "-w", "-f", path, this.DB);
            pb.environment().put("PGPASSWORD", this.clave);
            pb.redirectErrorStream(true);
            Process proceso = pb.start();
            InputStream is = proceso.getInputStream();
            InputStreamReader isr = new InputStreamReader(is);
            BufferedReader br = new BufferedReader(isr);
            String line;
            while ((line = br.readLine()) != null) {
                this.nom_archivo += line;
            }
            if(this.nom_archivo.compareTo("")==0){
                this.nom_archivo = path;
            }else{
                return false;
            }
        }catch(Exception e){
            e.printStackTrace();
            this.nom_archivo = e.getMessage();
            return false;
        }
        return true;
    }
    
    public boolean respaldar(String p_dump, String p_respaldo, String db, String u, String p)
    {
        try {
            String path = p_respaldo+db+"_"+Fecha.getFecha("ISO")+"_"+Fecha.getHora().replace(":", "-")+".backup";
            /*
             VERSIONES 8.3 O INFERIORES 
             C:\Program Files (x86)\PostgreSQL\8.3\bin\pg_dump.exe -h localhost -p 5432 -U postgres -F c -b -D -v -f "C:\Users\Jorge\Desktop\yakusoft3.backup" yakusoft3   
             
             VERSION 8.4
             C:\Program Files (x86)\PostgreSQL\8.4\bin\pg_dump.exe --host localhost --port 5432 --username postgres --format custom --blobs --verbose --file "C:\Users\Jorge\Desktop\yakusoft3.backup" yakusoft3
             */
            
            ProcessBuilder pb = new ProcessBuilder(p_dump, "-F", "c", "-b", "-f", path, "-U", u, db);
            pb.environment().put("PGPASSWORD", p);
            pb.redirectErrorStream(true);
            Process proceso = pb.start();
            InputStream is = proceso.getInputStream();
            InputStreamReader isr = new InputStreamReader(is);
            BufferedReader br = new BufferedReader(isr);
            String line;
            while ((line = br.readLine()) != null) {
                this.nom_archivo += line;
            }
            if(this.nom_archivo.compareTo("")==0){
                this.nom_archivo = path;
            }else{
                return false;
            }
        }catch(Exception e){
            e.printStackTrace();
            this.nom_archivo = e.getMessage();
            return false;
        }
        return true;
    }
}
