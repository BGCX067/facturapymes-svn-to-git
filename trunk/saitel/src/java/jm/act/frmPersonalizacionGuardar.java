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

package jm.act;

import java.io.*;

import javax.servlet.*;
import javax.servlet.http.*;
import jm.act.clas.Activo;
import jm.seg.clas.Auditoria;

/**
 *
 * @author Jorge
 */
public class frmPersonalizacionGuardar extends HttpServlet {
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

        Activo objActivo = new Activo(this._ip, this._puerto, this._db, usuario, clave);

        try {
            String r = "msg»Ha ocurrido un error inesperado, por favor, vuelva a intentarlo más tarde o " +
                "contáctese con el administrador del sistema para mayor información.";

            String WHERE = request.getParameter("WHERE");
            String p = request.getParameter("p") != null ? request.getParameter("p") : "0";
            String id = request.getParameter("id");
            String imprimir = request.getParameter("imp");
            String tipo_movimiento = request.getParameter("tpyMov");
            String num_documento = String.valueOf(objActivo.getNunDocumento());
            //String num_documento = request.getParameter("nd");
            String fecha = request.getParameter("fch");
            String idBodEnt = request.getParameter("idBodEnt");
            String idBodRec = request.getParameter("idBodRec");
            String bodega_entrega = request.getParameter("bodega_entrega");
            String bodega_recibe = request.getParameter("bodega_recibe");
            String ubicacion_entrega = request.getParameter("ubic_entrega")!=null ? request.getParameter("ubic_entrega") : "";
            String ubicacion_recibe = request.getParameter("ubic_recibe")!=null ? request.getParameter("ubic_recibe") : "";
            String dni_entrega = request.getParameter("dni_ent").toUpperCase();
            String dni_recibe = request.getParameter("dni_rec").toUpperCase();
            String persona_entrega = request.getParameter("perEnt");
            String persona_recibe = request.getParameter("perRec");
            String observacion = request.getParameter("obs");
            int tope = Integer.valueOf(request.getParameter("tope"));
            String gestion_envio = request.getParameter("g_envio");
            
            String id_activos = "";
            for(int i=0; i<=tope; i++){
                if(request.getParameter("idAc"+i)!=null){
                    id_activos += request.getParameter("idAc"+i) + ",";
                }
            }
            if(id_activos.compareTo("")!=0){
                id_activos = id_activos.substring(0, id_activos.length()-1);
            }

            if(tipo_movimiento.compareTo("1")==0){
                r = objActivo.ingresoCompra(id_activos);
            }else{
                r = "";
            }
            if(r.compareTo("")==0){
                r = "msg»Ha ocurrido un error inesperado, por favor, vuelva a intentarlo más tarde o " +
                "contáctese con el administrador del sistema para mayor información.";
                if(id.compareTo("-1")==0){
                    /*String id_doc = objActivo.insertarPersonalizacion(id_sucursal_sesion, num_documento, tipo_movimiento, 
                            fecha, idBodEnt, idBodRec, bodega_entrega, bodega_recibe, ubicacion_entrega, ubicacion_recibe,
                            dni_entrega, dni_recibe, persona_entrega, persona_recibe, observacion, id_activos);*/
                    String id_doc = objActivo.insertarPersonalizacionConGestion(id_sucursal_sesion, num_documento, tipo_movimiento, 
                            fecha, idBodEnt, idBodRec, bodega_entrega, bodega_recibe, ubicacion_entrega, ubicacion_recibe,
                            dni_entrega, dni_recibe, persona_entrega, persona_recibe, observacion, id_activos,gestion_envio);
                    if(id_doc.compareTo("-1") != 0){
                        Auditoria auditoria = new Auditoria(this._ip, this._puerto, this._db, usuario, clave);
                        auditoria.setRegistro(request, "REGISTRO DE DOCUMENTO DE PERSONALIZACION DE ACTIVOS Nro.: "+num_documento);
                        auditoria.cerrar();
                        r = "err»0^vta»cmp^fun»imprimir('pdfPersonalizacion?id="+id_doc+"');^tbl»"+
                            objActivo.paginar("vta_activo_personalizacion", "id_activo_personalizacion,num_documento,persona_entrega,persona_recibe,fecha,txt_tipo_movimiento", WHERE, Integer.parseInt(p), 100)+
                            "^msg»Documento registrado satisfactoriamente.";
                    }
                }else{
                    if(objActivo.actualizarPersonalizacion(id, id_sucursal_sesion, num_documento, tipo_movimiento, fecha, dni_entrega, dni_recibe,
                            persona_entrega, persona_recibe, observacion, id_activos)){
                        Auditoria auditoria = new Auditoria(this._ip, this._puerto, this._db, usuario, clave);
                        auditoria.setRegistro(request, "ACTUALIZACION DE DOCUMENTO DE PERSONALIZACION DE ACTIVOS Nro.: "+num_documento);
                        auditoria.cerrar();
                        r = "err»0^vta»cmp"+(imprimir.compareTo("1")==0?"^fun»imprimir('pdfPersonalizacion?id="+id+"');":"")+
                            "^tbl»"+objActivo.paginar("vta_activo_personalizacion", "id_activo_personalizacion,num_documento,persona_entrega,persona_recibe,fecha,txt_tipo_movimiento", WHERE, Integer.parseInt(p), 100)+
                            "^msg»Documento guardado satisfactoriamente.";
                    }
                }
            }

            out.print(r);

        } finally {
            objActivo.cerrar();
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
