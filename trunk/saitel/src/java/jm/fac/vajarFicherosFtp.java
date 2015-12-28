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

package jm.fac;

import java.io.*;

import javax.servlet.*;
import javax.servlet.http.*;
import jm.adm.clas.Configuracion;
import jm.fac.clas.DebitosTarjetasDeCredito;
import jm.web.Sftp;

/**
 *
 * @author Jorge
 */
public class vajarFicherosFtp extends HttpServlet {
    private String _ip = null;
    private int _puerto = 5432;
    private String _db = null;
    private String _ftp = null;

    public void init(ServletConfig config) throws ServletException
    {
        this._ip = config.getServletContext().getInitParameter("_IP");
        this._puerto = Integer.parseInt(config.getServletContext().getInitParameter("_PUERTO"));
        this._db = config.getServletContext().getInitParameter("_DB");
        this._ftp = config.getServletContext().getInitParameter("_FTP_PICHINCHA"); 
    }
    /**
    * Processes requests for both HTTP <code>GET</code> and <code>POST</code> methods.
    * @param request servlet request
    * @param response servlet response
    */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        HttpSession sesion = request.getSession(true);
        String usuario = (String)sesion.getAttribute("usuario");
        String clave = (String)sesion.getAttribute("clave");
        
        response.setContentType("text/html;charset=UTF-8");
        response.setHeader("Pragma", "no-cache");
        response.setHeader("Expires", "Mon, 01 Jan 2001 00:00:01 GMT");
        response.setHeader("Cache-Control", "no-store");
        response.setHeader("Cache-Control", "must-revalidate");
        response.setHeader("Cache-Control", "no-cache");

        PrintWriter out = response.getWriter();

        //this._ftp = "/home/jorge/Documentos/FTP/";
        
        Configuracion conf = new Configuracion(this._ip, this._puerto, this._db, usuario, clave);
        String sftp_ip = conf.getValor("sftp_ip");
        String sftp_puerto = conf.getValor("sftp_puerto");
        String sftp_usuario = conf.getValor("sftp_usuario");
        String sftp_clave = conf.getValor("sftp_clave");
        
        DebitosTarjetasDeCredito onjTarCred = new DebitosTarjetasDeCredito(this._ip, this._puerto, this._db, usuario, clave);
        
        try {
            String axFicheros = request.getParameter("fichs");
            if(axFicheros.compareTo("")!=0){
                Sftp ftp = new Sftp();
                if(ftp.conectar(sftp_ip, Integer.parseInt(sftp_puerto), sftp_usuario, sftp_clave)){
                    //String ruta = ftp.pwd();
                    String ficheros[] = axFicheros.split(",");
                    String fichero[];
                    String linea = "";
                    
                    for(int i=0; i<ficheros.length; i++){
                        fichero = ficheros[i].split("/");
                        String patArchivo = this._ftp + fichero[fichero.length-1];
                        if(ftp.descargarArchivo(patArchivo, ficheros[i])){
                            String periodo = "";
                            File archivo = new File(patArchivo);
                            BufferedReader buffer = new BufferedReader(new FileReader(archivo));
                            while ((linea = buffer.readLine()) != null) {
                                String tipoRegistro = linea.substring(0, 1);
                                if(tipoRegistro.compareTo("1")==0){
                                    periodo = linea.substring(11, 15) + "-" + linea.substring(15, 17) + "-" + linea.substring(17, 19);
                                }
                                if(tipoRegistro.compareTo("2")==0){
                                    String numTarjeta = linea.substring(1, 21);
                                    long id_sucursal_instalacion = Long.parseLong( linea.substring(21, 31) );
                                    String id_instalacion = String.valueOf(id_sucursal_instalacion);
                                    id_instalacion = id_instalacion.substring(1, id_instalacion.length());
                                    String total = Integer.parseInt(linea.substring(55, 65)) + "." + linea.substring(65, 67);
                                    if(!onjTarCred.hayCuenta(numTarjeta, id_instalacion, periodo)){
                                        onjTarCred.ejecutar("insert into tbl_documento_banco_tarjeta_credito(num_tarjeta, id_instalacion, periodo, fecha_pago, hora_pago, total) "
                                        + "values('"+Long.parseLong(numTarjeta)+"', "+id_instalacion+", '"+periodo+"', now()::date, now()::time, "+total+");");
                                    }
                                }
                            }
                        }
                    }
                    out.print("^fun»fac_ftpLocalVer()");
                    ftp.desconectar();
                }
            }
        }catch(Exception e){
            e.printStackTrace();
        }finally {
            onjTarCred.cerrar();
            conf.cerrar();
            out.close();
        }
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
    * Handles the HTTP <code>GET</code> method.
    * @param request servlet request
    * @param response servlet response
    */
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
    * Handles the HTTP <code>POST</code> method.
    * @param request servlet request
    * @param response servlet response
    */
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
    * Returns a short description of the servlet.
    */
    public String getServletInfo() {
        return "Short description";
    }
    // </editor-fold>
}
