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

import jm.nom.*;
import java.io.*;

import javax.servlet.*;
import javax.servlet.http.*;
import jm.adm.clas.Horario;
import jm.seg.clas.Auditoria;

/**
 *
 * @author Jorge
 */
public class frmHorarioGuardar extends HttpServlet {
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
        
        Horario obj_Horario = new Horario(this._ip, this._puerto, this._db, usuario, clave);
        try {
            String idHorario = request.getParameter("idHorario");
            String idDetalle = request.getParameter("idDetalle");
            String sucursal = request.getParameter("sucursal");
            String nombre_horario = request.getParameter("nombre_horario");
            String horario_antes = request.getParameter("horario_antes");
            String modalidad = request.getParameter("modalidad");
            String em = request.getParameter("em");
            String sm = request.getParameter("sm");
            String et = request.getParameter("et");
            String st = request.getParameter("st");
            String en = request.getParameter("en");
            String sn = request.getParameter("sn");
            String dias = request.getParameter("dias");
            String estado = request.getParameter("estado");
             String r = "";
             
            r = "msgÂ»Guardado Correctamente";
            if(idHorario.compareTo("-1")==0&&idDetalle.compareTo("-1")==0)
            {
                if(obj_Horario.guardarHorario(sucursal, nombre_horario,modalidad,estado,dias,em,sm,et,st,en,sn)){
                    Auditoria auditoria = new Auditoria(this._ip, this._puerto, this._db, usuario, clave);
                    auditoria.setRegistro(request, "NUEVO HORARIO GUARDADO: "+nombre_horario);
                    auditoria.cerrar();
                }else{
                    r = "msg»" + obj_Horario.getError();
                }                
            }
            
            if(idHorario.compareTo("-1")!=0&&idDetalle.compareTo("-1")==0)
            {
                if(obj_Horario.modificarHorario(nombre_horario,modalidad,estado,idHorario)){
                    Auditoria auditoria = new Auditoria(this._ip, this._puerto, this._db, usuario, clave);
                    auditoria.setRegistro(request, "HORARIO MODIFICADO: "+nombre_horario);
                    auditoria.cerrar();
                }else{
                    r = "msg»" + obj_Horario.getError();
                }                
            }
            if(idHorario.compareTo("-1")!=0&&(idDetalle.compareTo("-1")!=0||dias.length()>0))
            {
                if(obj_Horario.modificarHorarioDetalle(idHorario,idDetalle,em,sm,et,st,en,sn,dias)){
                    Auditoria auditoria = new Auditoria(this._ip, this._puerto, this._db, usuario, clave);
                    auditoria.setRegistro(request, "DIA DEL HORARIO MODIFICADO: "+nombre_horario);
                    auditoria.cerrar();
                }else{
                    r = "msg»" + obj_Horario.getError();
                }                
            }
            out.print(r);
        } finally {
            obj_Horario.cerrar();
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
