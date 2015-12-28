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
import jm.inf.clas.Nodo;
import jm.seg.clas.Auditoria;

/**
 *
 * @author Jorge
 */
public class frmNodoGuardar extends HttpServlet {
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
        int id_sucursal = (Integer)sesion.getAttribute("sucursal");
        String usuario = (String)sesion.getAttribute("usuario");
        String clave = (String)sesion.getAttribute("clave");

        response.setContentType("text/html;charset=UTF-8");
        response.setHeader("Pragma", "no-cache");
        response.setHeader("Expires", "Mon, 01 Jan 2001 00:00:01 GMT");
        response.setHeader("Cache-Control", "no-store");
        response.setHeader("Cache-Control", "must-revalidate");
        response.setHeader("Cache-Control", "no-cache");
        PrintWriter out = response.getWriter();
        
        Nodo objNodo = new Nodo(this._ip, this._puerto, this._db, usuario, clave);
        
        try {
            String r = "msg»El nombre del Nodo ya existe.";
            String WHERE = request.getParameter("WHERE");
            String p = request.getParameter("p") != null ? request.getParameter("p") : "0";
            String id_nodo = request.getParameter("id");
            String cod_cont = request.getParameter("cod_cont");
            String nodo = request.getParameter("nodo").toUpperCase();
            String cod_reg = request.getParameter("cod_reg");
            String tipo = request.getParameter("tipo");
            String tipo_otra = request.getParameter("tipo_otra");
            String altura_msnm = request.getParameter("altura_msnm");
            String altura_estr = request.getParameter("altura_estr");
            String id_provincia = request.getParameter("id_provincia");
            String id_canton = request.getParameter("id_canton");
            String localidad = request.getParameter("localidad");
            String puesta_tierra = request.getParameter("puesta_tierra");
            String pararrayos = request.getParameter("pararrayos");
            String otros = request.getParameter("otros");
            String linea_comercial = request.getParameter("linea_comercial");
            String generador = request.getParameter("generador");
            String banco_baterias = request.getParameter("banco_baterias");
            String respaldo = request.getParameter("respaldo");
            String resp_generador = request.getParameter("resp_generador");
            String resp_baterias = request.getParameter("resp_baterias");
            String resp_ups = request.getParameter("resp_ups");
            String resp_otro = request.getParameter("resp_otro");
            String propietario = request.getParameter("propietario");

            String ci = request.getParameter("ci");
            String custodio = request.getParameter("custodio");
            String sexo = request.getParameter("sexo");
            String sector = request.getParameter("sector");
            String direccion = request.getParameter("direccion");
            String telefono = request.getParameter("telefono");
            String movil = request.getParameter("movil");
            String email = request.getParameter("email");
            String banco = request.getParameter("banco");
            String cuenta = request.getParameter("cuenta");

            String lat_h = request.getParameter("lat_h");
            String lat_m = request.getParameter("lat_m");
            String lat_s = request.getParameter("lat_s");
            String lat_o = request.getParameter("lat_o");
            String lon_h = request.getParameter("lon_h");
            String lon_m = request.getParameter("lon_m");
            String lon_s = request.getParameter("lon_s");
            String lon_o = request.getParameter("lon_o");
            String latitud = lat_h + ":" + lat_m + ":" + lat_s + ":"  + lat_o;
            String longitud = lon_h + ":" + lon_m + ":" + lon_s + ":"  + lon_o;


            if(!objNodo.estaDuplicado(id_nodo, nodo)){
                r = "msg»Ha ocurrido un error inesperado, por favor, vuelva a intentarlo más tarde o " +
                    "contáctese con el administrador del sistema para mayor información.";
                if(id_nodo.compareTo("-1")==0){
                    if(objNodo.insertar(id_sucursal, cod_cont, nodo, cod_reg, tipo, tipo_otra, altura_msnm, altura_estr,
                            id_provincia, id_canton, localidad, latitud, longitud, puesta_tierra, pararrayos, otros, linea_comercial,
                            generador, banco_baterias, respaldo, resp_generador, resp_baterias, resp_ups, resp_otro, propietario,
                            ci, custodio, sexo, sector, direccion, telefono, movil, email, banco, cuenta)){
                        Auditoria auditoria = new Auditoria(this._ip, this._puerto, this._db, usuario, clave);
                        auditoria.setRegistro(request, "INGRESO DE NUEVO NODO: "+nodo);
                        auditoria.cerrar();
                        r = "err»0^vta»vta1^tbl»"+objNodo.paginar("vta_nodo", "id_nodo,nodo,cod_reg", WHERE, Integer.parseInt(p), 100)+"^id»-1^msg»Información ingresada satisfactoriamente.";
                    }
                }else{
                    if(objNodo.actualizar(id_nodo, id_sucursal, cod_cont, nodo, cod_reg, tipo, tipo_otra, altura_msnm, altura_estr,
                            id_provincia, id_canton, localidad, latitud, longitud, puesta_tierra, pararrayos, otros, linea_comercial,
                            generador, banco_baterias, respaldo, resp_generador, resp_baterias, resp_ups, resp_otro, propietario,
                            ci, custodio, sexo, sector, direccion, telefono, movil, email, banco, cuenta)){
                        Auditoria auditoria = new Auditoria(this._ip, this._puerto, this._db, usuario, clave);
                        auditoria.setRegistro(request, "ACTUALIZACION DE LA INFORMACION DE NODO: "+nodo);
                        auditoria.cerrar();
                        r = "err»0^vta»vta1^tbl»"+objNodo.paginar("vta_nodo", "id_nodo,nodo,cod_reg", WHERE, Integer.parseInt(p), 100)+"^msg»Información guardada satisfactoriamente.";
                    }
                }
            }

            out.print(r);
        } finally {
            objNodo.cerrar();
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