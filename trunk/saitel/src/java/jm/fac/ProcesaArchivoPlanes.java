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
import java.sql.ResultSet;

import javax.servlet.*;
import javax.servlet.http.*;
import jm.fac.clas.Instalacion;
import jm.web.Archivo;

/**
 *
 * @author Jorge
 */
public class ProcesaArchivoPlanes extends HttpServlet {
    private String _ip = null;
    private int _puerto = 5432;
    private String _db = null;
    private String _dir = null;

    public void init(ServletConfig config) throws ServletException
    {
        this._ip = config.getServletContext().getInitParameter("_IP");
        this._puerto = Integer.parseInt(config.getServletContext().getInitParameter("_PUERTO"));
        this._db = config.getServletContext().getInitParameter("_DB");
        this._dir = config.getServletContext().getInitParameter("_DIR");
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

        Archivo archivo = new Archivo(this._ip, this._puerto, this._db, usuario, clave);
        archivo.setDirectorio(this._dir);
        try {
            String r = "<script language='javascript' type='text/javascript'>window.top.window.inv_finTransferencia('Ha ocurrido un error en el proceso de subida del archivo.', '');</script>";
            if(archivo.subir(request, 30, new String[]{"text/plain"} )){
                int err = 1;
                String msg = "Ha ocurrido un error en el procesamiento de los datos del archivo. Por favor, vuelva a intentarlo más tarde o " +
                    "contáctese con el administrador del sistema para mayor información.";
                try{
                    File file = archivo.getArchivo();
                    String lineas = "";
                    String linea = "";
                    BufferedReader buffer = new BufferedReader(new FileReader(file));
                    while ((linea = buffer.readLine()) != null) {
                        lineas += linea;
                    }
                    lineas = lineas.replace("\\", " ");
                    lineas = lineas.replace("\"", "");
                    lineas = lineas.replaceAll("=( )+", "=");
                    lineas = lineas.replaceAll("( )+", " ");
                    buffer.close();
                    
                    if(lineas.length()>0){
                        String comandos[] = lineas.split("add ");
                        Instalacion objInstalacion = new Instalacion(this._ip, this._puerto, this._db, usuario, clave);
                        ResultSet rs = objInstalacion.getInstalaciones();
                        String id_instalacion="";
                        String ip = "";
                        int i=0;
                        while(rs.next()){
                            id_instalacion = (rs.getString("id_instalacion")!=null) ? rs.getString("id_instalacion") : "";
                            ip = (rs.getString("ip")!=null) ? rs.getString("ip") : "";
                            i = this.contiene(comandos, ip);
                            if(i != -1){
                                objInstalacion.ejecutar("UPDATE tbl_instalacion SET svr_burst_limit='"+getParametro(comandos, "burst-limit", i)+"', "
                                        + "svr_burst_threshold='"+getParametro(comandos, "burst-threshold", i)+"', "
                                        + "svr_burst_time='"+getParametro(comandos, "burst-time", i)+"', "
                                        + "svr_direction='"+getParametro(comandos, "direction", i)+"', "
                                        + "svr_disabled='"+getParametro(comandos, "disabled", i)+"', "
                                        + "svr_dst_address='"+getParametro(comandos, "dst-address", i)+"', "
                                        + "svr_interface='"+getParametro(comandos, "interface", i)+"', "
                                        + "svr_limit_at='"+getParametro(comandos, "limit-at", i)+"', "
                                        + "svr_max_limit='"+getParametro(comandos, "max-limit", i)+"', "
                                        + "svr_name='"+getParametro(comandos, "name", i)+"', "
                                        + "svr_parent='"+getParametro(comandos, "parent", i)+"', "
                                        + "svr_priority='"+getParametro(comandos, "priority", i)+"', "
                                        + "svr_queue='"+getParametro(comandos, "queue", i)+"', "
                                        + "svr_total_queue='"+getParametro(comandos, "total-queue", i)+"' "
                                        + "WHERE id_instalacion="+id_instalacion);
                            }
                        }
                        try{
                            rs.close();
                        }catch(Exception ex){
                            ex.printStackTrace();
                        }finally{
                            objInstalacion.cerrar();
                        }
                        err = 0;
                        msg = "Actualización de información de transferencia de planes del servidor realizada satisfactoriamente.";
                    }
                }catch(Exception e){
                    e.printStackTrace();
                }
                r = "<script language='javascript' type='text/javascript'>window.top.window.fac_finTransferencia("+err+", '"+msg+"');</script>";
            }else{
                r = "<script language='javascript' type='text/javascript'>window.top.window.fac_finTransferencia(1, '"+archivo.getError()+"');</script>";
            }
            out.print(r);
        }catch(Exception e) {
            e.printStackTrace();
        } finally {
            archivo.cerrar();
            out.close();
        }
    }

    public String getParametro(String comandos[], String propiedad, int pos)
    {
        String res = "";
        try{
            String propiedades[] = comandos[pos].split(" ");
            int i=-1;
            int top = propiedades.length;
            for(i=0; i<top; i++){
                if(propiedades[i].contains(propiedad)){
                    break;
                }
            }
            if(i!=-1 && i<top){
                String aux[] = propiedades[i].split("=");
                res = aux[1];
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return res;
    }

    public int contiene(String vec[], String clave)
    {
        int r = -1;
        for(int i=0; i<vec.length; i++){
            if(vec[i].contains(clave)){
                r = i;
                break;
            }
        }
        return r;
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
