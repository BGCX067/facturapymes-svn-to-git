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
import jm.web.Correo;
import jm.web.Sftp;

/**
 *
 * @author Jorge
 */
public class subirFicherosFtp extends HttpServlet {
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
        
        try {
            String axFicheros = request.getParameter("fichs");
            if(axFicheros.compareTo("")!=0){
                Sftp ftp = new Sftp();
                if(ftp.conectar(sftp_ip, Integer.parseInt(sftp_puerto), sftp_usuario, sftp_clave)){
                    //String ruta = ftp.pwd();
                    String ficheros[] = axFicheros.split(",");
                    for(int i=0; i<ficheros.length; i++){
                        if(ficheros[i].indexOf("Diners") >=0){
                            ftp.subirArchivo(this._ftp+ficheros[i], "/SAITEL/Diners/"+ficheros[i]);
                        }
                        if(ficheros[i].indexOf("Discover") >=0){
                            ftp.subirArchivo(this._ftp+ficheros[i], "/SAITEL/Discover/"+ficheros[i]);
                        }
                        if(ficheros[i].indexOf("Mastercard") >=0){
                            ftp.subirArchivo(this._ftp+ficheros[i], "/SAITEL/Mastercard/"+ficheros[i]);
                        }
                        if(ficheros[i].indexOf("Visa") >=0){
                            ftp.subirArchivo(this._ftp+ficheros[i], "/SAITEL/Visa/"+ficheros[i]);
                        }
                    }
                    StringBuilder mensaje = new StringBuilder();
                    mensaje.append("Saludos cordiales;\n");
                    mensaje.append("Sr. Zapata.\n");
                    mensaje.append("Informo que se ha subido los archivos de convenio de debito con tarjeta de crédito con fecha 09 de septiembre del 2014 de la empresa SOLUCIONES AVANZADAS INFORMATICAS Y TELECOMUNICACIONES (SAITEL)  con RUC número 1091728857001. Se solicita realiar el proceso de acreditación a la cuenta corriente 3449563104 del banco Pichincha a nombre de SAITEL.\n\n");
                    mensaje.append("Atentamente;\n");
                    mensaje.append("SAITEL\n");
                    mensaje.append("Dpto. Financiero.");
                    
                    Correo.enviar("smtp.saitel.ec", "contabilidad@saitel.ec", "jorge_mueses@yahoo.com", "jorge_mueses@hotmail.com", "", "CONVENIOS DE DEBITO CON TARJETA DE CREDITO SAITEL", mensaje, false);
                    
                    out.print("^fun»fac_ftpRemotoVer()");
                    ftp.desconectar();
                }
            }
        }finally {
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
