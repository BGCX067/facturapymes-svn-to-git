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

import com.jcraft.jsch.ChannelSftp.LsEntry;
import java.io.*;
import java.util.Vector;

import javax.servlet.*;
import javax.servlet.http.*;
import jm.adm.clas.Configuracion;
import jm.web.Sftp;

/**
 *
 * @author Jorge
 */
public class verFtpRemoto extends HttpServlet {
    private String _ip = null;
    private int _puerto = 5432;
    private String _db = null;
    private int i = 0;

    public void init(ServletConfig config) throws ServletException
    {
        this._ip = config.getServletContext().getInitParameter("_IP");
        this._puerto = Integer.parseInt(config.getServletContext().getInitParameter("_PUERTO"));
        this._db = config.getServletContext().getInitParameter("_DB");
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

        Configuracion conf = new Configuracion(this._ip, this._puerto, this._db, usuario, clave);
        String sftp_ip = conf.getValor("sftp_ip");
        String sftp_puerto = conf.getValor("sftp_puerto");
        String sftp_usuario = conf.getValor("sftp_usuario");
        String sftp_clave = conf.getValor("sftp_clave");
        
        out.print("obj»axFtpRem^frm»");
        
        try {
            Sftp ftp = new Sftp();
            if(ftp.conectar(sftp_ip, Integer.parseInt(sftp_puerto), sftp_usuario, sftp_clave)){
                //String ruta = ftp.pwd();
                String ruta = "/SAITEL/";
                
                Vector subRuta = ftp.listar(ruta);
                for(int x=0; x<subRuta.size(); x++){
                    Object obj = subRuta.elementAt(x);
                    if (obj instanceof LsEntry) {
                        String nombreLargo = ((LsEntry) obj).getLongname();
                        if(nombreLargo.substring(0, 1).compareTo("d")==0){
                            String fichero = ((LsEntry) obj).getFilename();
                            out.print("<div class='grupo' style='height:18px;'>"+fichero+"</div>");
                            out.print("<div>"+this.listar(ftp, ruta+fichero) +"</div><br />");
                        }
                    }
                }
                
                out.print("<div>"+this.listar(ftp, ruta) +"</div><br />");
                
                ftp.desconectar();
            }else{
                out.print("<div>No se ha establecido conexi&oacute;n con el servidor FTP remoto</div>");
            }
        }catch(Exception e){
            e.printStackTrace();
        }finally {
            conf.cerrar();
            out.close();
        }
    }
    
    private String listar(Sftp ftp, String ruta)
    {
        String html = "<TABLE class='jm_tabla' cellspacing='0' cellpadding='0' width='100%'>";
        Vector ficheros = ftp.listar(ruta);
        for(int x=0; x<ficheros.size(); x++){
            Object obj = ficheros.elementAt(x);
            if (obj instanceof LsEntry) {
                String nombreLargo = ((LsEntry) obj).getLongname();
                if(nombreLargo.substring(0, 1).compareTo("d")!=0){
                    String fichero = ((LsEntry) obj).getFilename();
                    html += "<tr class='jm_filaPar'><td width='12'><input id=\"chArchRem"+this.i+"\" type=\"checkbox\" /></td>"
                        + "<td valign='middle'><input type='hidden' id='archRem"+this.i+"' value='"+ruta+"/"+fichero+"' />"+fichero+"</td>"
                        + "<td width='12'><div class='elim' onclick=\"fac_remFicheroRemoto('"+ruta+"/"+fichero+"')\">&nbsp;</div></td></tr>";
                    this.i++;
                }
            }
        }
        html += "</table>";
        return html;
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
