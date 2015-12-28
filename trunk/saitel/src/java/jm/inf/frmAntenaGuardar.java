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

package jm.inf;

import java.io.*;

import javax.servlet.*;
import javax.servlet.http.*;
import jm.inf.clas.Antena;
import jm.seg.clas.Auditoria;

/**
 *
 * @author Jorge
 */
public class frmAntenaGuardar extends HttpServlet {
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
        
        Antena objAntena = new Antena(this._ip, this._puerto, this._db, usuario, clave);
        
        try {
            String r = "msg»El nombre del antena ya existe.";
            String WHERE = request.getParameter("WHERE");
            String p = request.getParameter("p") != null ? request.getParameter("p") : "0";
            String id = request.getParameter("id");
            String id_nodo = request.getParameter("id_nodo");
            String antena_acoplada = request.getParameter("antena_acoplada");
            
            if(!objAntena.estaDuplicado(id, antena_acoplada)){
                r = "msg»Ha ocurrido un error inesperado, por favor, vuelva a intentarlo más tarde o " +
                    "contáctese con el administrador del sistema para mayor información.";
                if(id.compareTo("-1")==0){
                    if(objAntena.insertar(id_nodo, antena_acoplada)){
                        Auditoria auditoria = new Auditoria(this._ip, this._puerto, this._db, usuario, clave);
                        auditoria.setRegistro(request, "INGRESO DE NUEVA ANTENA DE TRASMISION ACOPLADA: "+antena_acoplada);
                        auditoria.cerrar();
                        r = "tbl»"+objAntena.paginar("vta_antena", "id_antena_acoplada,antena_acoplada", WHERE, Integer.parseInt(p), 100)+"^id»-1^msg»Información ingresada satisfactoriamente.";
                    }
                }else{
                    if(objAntena.actualizar(id, id_nodo, antena_acoplada)){
                        Auditoria auditoria = new Auditoria(this._ip, this._puerto, this._db, usuario, clave);
                        auditoria.setRegistro(request, "ACTUALIZACION DE LA INFORMACION DE ANTENA DE TRASMISION ACOPLADA: "+antena_acoplada);
                        auditoria.cerrar();
                        r = "tbl»"+objAntena.paginar("vta_antena", "id_antena_acoplada,antena_acoplada", WHERE, Integer.parseInt(p), 100)+"^msg»Información guardada satisfactoriamente.";
                    }
                }
            }

            out.print(r);
        } finally {
            objAntena.cerrar();
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