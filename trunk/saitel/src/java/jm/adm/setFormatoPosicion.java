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
import jm.adm.clas.Configuracion;
import jm.adm.clas.PuntoEmision;
import jm.adm.clas.Sucursal;
import jm.seg.clas.Auditoria;

/**
 *
 * @author Jorge
 */
public class setFormatoPosicion extends HttpServlet {
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
        
        String r = "Ha ocurrido un error inesperado, por favor, vuelva a intentarlo más tarde o " +
                        "contáctese con el administrador del sistema para mayor información.";
        try {
            String id = request.getParameter("id");
            String doc = request.getParameter("doc");
            String campo = request.getParameter("c");
            String x = request.getParameter("x");
            String y = request.getParameter("y");
            r="Error al guardar posición.";
            if(doc.compareTo("ch")==0){
                Configuracion conf = new Configuracion(this._ip, this._puerto, this._db, usuario, clave);
                if(conf.setValor(campo, x+":"+y)){
                    Auditoria auditoria = new Auditoria(this._ip, this._puerto, this._db, usuario, clave);
                    auditoria.setRegistro(request, "MODIFICACION DEL FORMATO DE CHEQUES");
                    auditoria.cerrar();
                    r = "0";
                }
                conf.cerrar();
            }else if(doc.compareTo("f")==0){
                    PuntoEmision objPuntoEmision = new PuntoEmision(this._ip, this._puerto, this._db, usuario, clave);
                    if(campo.compareTo("frt_fac_anch_cant")!=0 && campo.compareTo("frt_fac_anch_descripcion")!=0 && campo.compareTo("frt_fac_anch_v_u")!=0 && campo.compareTo("frt_fac_anch_v_t")!=0){
                        if(objPuntoEmision.setFormatoValor(id, campo, x+":"+y)){
                            Auditoria auditoria = new Auditoria(this._ip, this._puerto, this._db, usuario, clave);
                            auditoria.setRegistro(request, "MODIFICACION DEL FORMATO DE FACTURAS");
                            auditoria.cerrar();
                            r = "0";
                        }
                    }
                    objPuntoEmision.cerrar();
            }else if(doc.compareTo("l")==0 || doc.compareTo("r")==0 || doc.compareTo("c")==0 || doc.compareTo("d")==0){
                if(doc.compareTo("l")==0){
                    doc = "LIQUIDACION DE COMPRAS Y SERVICIOS";
                }else if(doc.compareTo("r")==0){
                    doc = "COMPROBANTES DE RETENCION";
                }else if(doc.compareTo("c")==0){
                    doc = "NOTAS DE CREDITO";
                }else if(doc.compareTo("d")==0){
                    doc = "NOTAS DE DEBITO";
                }
                
                Sucursal objSucursal = new Sucursal(this._ip, this._puerto, this._db, usuario, clave);
                if((campo.compareTo("frt_liq_anch_cant")!=0 && campo.compareTo("frt_liq_anch_descripcion")!=0
                        && campo.compareTo("frt_liq_anch_v_u")!=0 && campo.compareTo("frt_liq_anch_v_t")!=0)
                        || campo.compareTo("frt_not_cre_anch_concepto")!=0 || campo.compareTo("frt_not_deb_anch_concepto")!=0){
                    if(objSucursal.setFormatoValor(id, campo, x+":"+y)){
                        Auditoria auditoria = new Auditoria(this._ip, this._puerto, this._db, usuario, clave);
                        auditoria.setRegistro(request, "MODIFICACION DEL FORMATO DE " + doc);
                        auditoria.cerrar();
                        r = "0";
                    }
                }
                objSucursal.cerrar();
            }
            
            out.print(r);
        } finally {
            out.print(r);
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