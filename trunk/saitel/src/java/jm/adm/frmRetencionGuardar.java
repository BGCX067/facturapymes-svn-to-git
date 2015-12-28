/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package jm.adm;

import java.io.*;

import javax.servlet.*;
import javax.servlet.http.*;
import jm.adm.clas.TablaImpuesto;
import jm.seg.clas.Auditoria;

/**
 *
 * @author Jorge
 */
public class frmRetencionGuardar extends HttpServlet {
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
        
        TablaImpuesto objTablaImpuesto = new TablaImpuesto(this._ip, this._puerto, this._db, usuario, clave);
        try {
            String r = "msg»El código de la retención ya existe.";
            String idImpuesto = request.getParameter("id");
            String idRetencion = request.getParameter("idR");
            String impuesto = request.getParameter("i");
            String codigo = request.getParameter("c");
            String concepto = request.getParameter("d");
            String porcentaje = request.getParameter("p");
            String id_plan_cuenta_compra = request.getParameter("idPcC");
            String id_plan_cuenta_venta = request.getParameter("idPcV");
            String obj = request.getParameter("obj");

            if(!objTablaImpuesto.retencionDuplicada(idRetencion, idImpuesto, codigo)){
                r = "msg»Ha ocurrido un error inesperado, por favor, vuelva a intentarlo más tarde o " +
                        "contáctese con el administrador del sistema para mayor información.";
                if(idRetencion.compareTo("-1")==0){
                    if(objTablaImpuesto.insertarRetencion(idImpuesto, impuesto, codigo, concepto, porcentaje, id_plan_cuenta_compra, id_plan_cuenta_venta)){
                        Auditoria auditoria = new Auditoria(this._ip, this._puerto, this._db, usuario, clave);
                        auditoria.setRegistro(request, "INGRESO DE RETENCION DE CODIGO: "+codigo+" EN LA TABLA DE IMPUESTOS");
                        auditoria.cerrar();
                        r = "fun»_('"+obj+"').value="+objTablaImpuesto.getUltimaInsercionRetencion();
                    }
                }else{
                    if(objTablaImpuesto.actualizarRetencion(idRetencion, impuesto, codigo, concepto, porcentaje, id_plan_cuenta_compra, id_plan_cuenta_venta)){
                        Auditoria auditoria = new Auditoria(this._ip, this._puerto, this._db, usuario, clave);
                        auditoria.setRegistro(request, "ACTUALIZACION DE RETENCION DE CODIGO "+codigo+" EN LA TABLA DE IMPUESTOS");
                        auditoria.cerrar();
                        //r = "msg»Información guardada satisfactoriamente.";
                        r = "";
                    }
                }
            }
            
            out.print(r);
        } finally {
            objTablaImpuesto.cerrar();
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