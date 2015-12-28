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
import jm.adm.clas.Mensaje;
import jm.inf.clas.HojaRuta;
import jm.seg.clas.Auditoria;
import jm.web.Fecha;

/**
 *
 * @author Jorge
 */
public class frmHojaRutaGuardar extends HttpServlet {
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
        String empleado = (String)sesion.getAttribute("empleado");
        int id_sucursal_sesion = (Integer)sesion.getAttribute("sucursal");
        String usuario = (String)sesion.getAttribute("usuario");
        String clave = (String)sesion.getAttribute("clave");

        response.setContentType("text/html;charset=UTF-8");
        response.setHeader("Pragma", "no-cache");
        response.setHeader("Expires", "Mon, 01 Jan 2001 00:00:01 GMT");
        response.setHeader("Cache-Control", "no-store");
        response.setHeader("Cache-Control", "must-revalidate");
        response.setHeader("Cache-Control", "no-cache");
        PrintWriter out = response.getWriter();
        
        HojaRuta objHojaRuta = new HojaRuta(this._ip, this._puerto, this._db, usuario, clave);
        
        try {
            String r = "msg»Ha ocurrido un error inesperado, por favor, vuelva a intentarlo más tarde o " +
                    "contáctese con el administrador del sistema para mayor información.";
            String WHERE = request.getParameter("WHERE");
            String p = request.getParameter("p") != null ? request.getParameter("p") : "0";
            String id_hoja_ruta = request.getParameter("id");
            String id_tecnico_resp = request.getParameter("id_tecnico_resp");
            String observacion = request.getParameter("observacion");
            String imprimir = request.getParameter("imp");

            String id_ordenes_trabajo="";
            int i=0;
            while(request.getParameter("id_ot"+i)!=null){
                if(request.getParameter("asg"+i).compareTo("1")==0){
                    id_ordenes_trabajo += request.getParameter("id_ot"+i) + ",";
                }
                i++;
            }
            id_ordenes_trabajo = id_ordenes_trabajo.substring(0, id_ordenes_trabajo.length()-1);

            if(id_hoja_ruta.compareTo("-1")==0){
                String num_hoja_ruta = objHojaRuta.getNumHojaRuta(id_sucursal_sesion);
                id_hoja_ruta = objHojaRuta.insertar(id_sucursal_sesion, num_hoja_ruta, usuario, id_tecnico_resp, observacion, id_ordenes_trabajo);
                if(id_hoja_ruta.compareTo("-1")!=0){
                    Auditoria auditoria = new Auditoria(this._ip, this._puerto, this._db, usuario, clave);
                    auditoria.setRegistro(request, "INGRESO DE UNA NUEVA HOJA DE RUTA NRO. "+ id_sucursal_sesion + "-" +num_hoja_ruta);
                    auditoria.cerrar();

                    Mensaje objMensaje = new Mensaje(this._ip, this._puerto, this._db, usuario, clave);
                    objMensaje.setMensaje(usuario, empleado + " ha genreado una nueva hoja de ruta No. " + id_sucursal_sesion + "-" +num_hoja_ruta, "trabajo", id_hoja_ruta, Fecha.getFecha("ISO"), "0.3");
                    objMensaje.cerrar();

                    r = "err»0^vta»cmp^fun»fac_tarea();"+(imprimir.compareTo("1")==0?"imprimir('pdfHojaRuta?id="+id_hoja_ruta+"');":"")+
                            "^msg»Información ingresada satisfactoriamente.";
                }
            }else{
                if(objHojaRuta.actualizar(id_hoja_ruta, id_tecnico_resp, observacion, id_ordenes_trabajo)){
                    String num_hoja_ruta = request.getParameter("num_hoja_ruta");
                    Auditoria auditoria = new Auditoria(this._ip, this._puerto, this._db, usuario, clave);
                    auditoria.setRegistro(request, "ACTUALIZACION DE LA HOJA DE RUTA NRO. "+ id_sucursal_sesion + "-" +num_hoja_ruta);
                    auditoria.cerrar();
                    r = "err»0^vta»cmp"+(imprimir.compareTo("1")==0?"^fun»imprimir('pdfHojaRuta?id="+id_hoja_ruta+"');":"")+
                            "^tbl»"+objHojaRuta.paginar("vta_hoja_ruta", "id_hoja_ruta,fecha,tecnico,observacion", WHERE, Integer.parseInt(p), 100)+
                            "^msg»Información guardada satisfactoriamente.";
                }
            }

            out.print(r);
        } finally {
            objHojaRuta.cerrar();
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