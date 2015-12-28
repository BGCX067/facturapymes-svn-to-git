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

import java.io.*;
import java.sql.ResultSet;

import javax.servlet.*;
import javax.servlet.http.*;
import jm.adm.clas.Ubicacion;
import jm.web.DatosDinamicos;
import jm.seg.clas.Auditoria;

/**
 *
 * @author Jorge
 */
public class setUbicacion extends HttpServlet {
    private String _ip = null;
    private int _puerto = 5432;
    private String _db = null;

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

        Ubicacion objUbicacion = new Ubicacion(this._ip, this._puerto, this._db, usuario, clave);
        try {
            String objeto = request.getParameter("obj");
            String id_combo = request.getParameter("comb");
            String id_padre = request.getParameter("pdr");
            String nombre = request.getParameter("nom");
            int ancho = request.getParameter("an")!=null ? Integer.valueOf(request.getParameter("an")) : 100;
            String onChange = request.getParameter("onCh")!=null ? request.getParameter("onCh") : "";
            String funcion = request.getParameter("fun")!=null ? request.getParameter("fun") : "";

            ResultSet rslista = objUbicacion.getUbicaciones(id_padre);
            String r = "obj»"+objeto+"^frm»"+DatosDinamicos.combo(rslista, id_combo, "", onChange, "", ancho)+"^msg»El nuevo ítem ya existe en la lista.";
            if(!objUbicacion.estaDuplicado(id_padre,nombre)){
                r = "obj»"+objeto+"^frm»"+DatosDinamicos.combo(rslista, id_combo, "", onChange, "", ancho)+
                        "^msg»Ha ocurrido un error inesperado, por favor, vuelva a intentarlo más tarde o " +
                        "contáctese con el administrador del sistema para mayor información.";
                String secuencial = objUbicacion.getSecuancia(id_padre);
                if(objUbicacion.insertar(secuencial, id_padre, nombre)){
                    rslista = objUbicacion.getUbicaciones(id_padre);
                    Auditoria auditoria = new Auditoria(this._ip, this._puerto, this._db, usuario, clave);
                    auditoria.setRegistro(request, "INGRESO DE UN NUEVO ITEM: "+nombre.toUpperCase()+" EN LA LISTA DE UBICACIONES");
                    auditoria.cerrar();
                    r = "obj»"+objeto+"^frm»"+DatosDinamicos.combo(rslista, id_combo, secuencial, onChange, "", ancho);
                    if(funcion.compareTo("")!=0 && funcion.compareTo("0")!=0){
                        r += "^fun»"+funcion;
                    }
                }

                try{
                    rslista.close();
                }catch(Exception e){
                    e.printStackTrace();
                }
            }

            out.print(r);
        } finally {
            objUbicacion.cerrar();
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
