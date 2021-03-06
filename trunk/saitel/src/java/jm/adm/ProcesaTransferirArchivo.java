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

package jm.adm;

import java.io.*;
 
import javax.servlet.*;
import javax.servlet.http.*;
import jm.web.Archivo;

/**
 *
 * @author Jorge
 */
public class ProcesaTransferirArchivo extends HttpServlet {
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
        String tabla = (String)sesion.getAttribute("tabla");
        String campoNombre = (String)sesion.getAttribute("campoNombre");
        String campoBytea = (String)sesion.getAttribute("campoBytea");
        String usuario = (String)sesion.getAttribute("usuario");
        String clave = (String)sesion.getAttribute("clave");
        
        String clave1 = (String)sesion.getAttribute("clave1");

        response.setContentType("text/html;charset=UTF-8");
        response.setHeader("Pragma", "no-cache"); 
        response.setHeader("Expires", "Mon, 01 Jan 2001 00:00:01 GMT"); 
        response.setHeader("Cache-Control", "no-store"); 
        response.setHeader("Cache-Control", "must-revalidate"); 
        response.setHeader("Cache-Control", "no-cache");
        PrintWriter out = response.getWriter();
        
        Archivo archivo = new Archivo(this._ip, this._puerto, this._db, usuario, clave);
        archivo.setDirectorio(this._dir);
        //archivo.setDirectorio("/home/desarrollo/Descargas/");

        /*String tabla = request.getParameter("tabla");
        String campoNombre = request.getParameter("campoNombre");
        String campoBytea = request.getParameter("campoBytea");
        String clave = request.getParameter("clave");*/

        try {
            String r = "<script language='javascript' type='text/javascript'>window.top.window.finTransferencia('Ha ocurrido un error en el proceso de subida del archivo.', '');</script>";
                        
            if(archivo.subir(request, 1, new String[]{"image/jpeg","image/pjpeg"} )){
                String imagen = archivo.getNombreArchivo();
                File archivoSubido = archivo.getArchivo();
                if(archivo.setArchivoDB(tabla, campoNombre, campoBytea, clave1, imagen, archivoSubido)){
                    r = "<script language='javascript' type='text/javascript'>window.top.window.finTransferencia(0, '"+imagen+"');</script>";
                    sesion.setAttribute("tabla", "");
                    sesion.setAttribute("campoNombre", "");
                    sesion.setAttribute("campoBytea", "");
                    sesion.setAttribute("clave1", "");
                }else{
                    r = "<script language='javascript' type='text/javascript'>window.top.window.finTransferencia('"+archivo.getError()+"', '');</script>"; 
                }
            }else{
                r = "<script language='javascript' type='text/javascript'>window.top.window.finTransferencia('"+archivo.getError()+"', '');</script>"; 
            }
            Thread.sleep(100);
            out.print(r);
        }catch(Exception e) {
            e.printStackTrace();
        } finally { 
            archivo.cerrar();
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
