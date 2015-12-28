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
import java.sql.ResultSet;

import javax.servlet.*;
import javax.servlet.http.*;
import jm.act.clas.Activo;
import jm.adm.clas.Configuracion;
import jm.adm.clas.Documento;
import jm.fac.clas.Cliente;
import jm.fac.clas.Instalacion;
import jm.inf.clas.OrdenTrabajo;
import jm.seg.clas.Auditoria;

/**
 *
 * @author Jorge
 */
public class frmOrdenTrabajoSolucionar extends HttpServlet {
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
        
        OrdenTrabajo objOrdenTrabajo = new OrdenTrabajo(this._ip, this._puerto, this._db, usuario, clave);
        Activo objActivo = new Activo(this._ip, this._puerto, this._db, usuario, clave);
        
        try {
            String r = "msg»Ha ocurrido un error inesperado, por favor, vuelva a intentarlo más tarde o " +
                    "contáctese con el administrador del sistema para mayor información.";
            String WHERE = request.getParameter("WHERE");
            String p = request.getParameter("p") != null ? request.getParameter("p") : "0";
            String id = request.getParameter("id");
            String id_instalacion = request.getParameter("idIns1");
            String num_orden = request.getParameter("num_orden1");
            String fecha_solucion = request.getParameter("fecha_solucion");
            String materiales = request.getParameter("materiales");
            String recomendacion = request.getParameter("recomendacion");
            String antena_acoplada = request.getParameter("antena_acoplada");
            String mac_ant = request.getParameter("mac_ant");
            String mac = request.getParameter("mac");

            if(objOrdenTrabajo.solucionar(id, id_instalacion, fecha_solucion, materiales, recomendacion)){
                Auditoria auditoria = new Auditoria(this._ip, this._puerto, this._db, usuario, clave);

                
                String receptor = objActivo.getDescripcion(mac);

                /*Instalacion objInstalacion = new Instalacion(this._ip, this._puerto, this._db, usuario, clave);
                objInstalacion.setAntenaMacReceptor(id_instalacion, antena_acoplada, mac, receptor);
                String id_cliente = "";
                try{
                    ResultSet rsInatalacion = objInstalacion.getInstalacion(id_instalacion);
                    if(rsInatalacion.next()){
                        id_cliente = rsInatalacion.getString("id_cliente")!=null ? rsInatalacion.getString("id_cliente") : "";
                    }
                }catch(Exception e){
                    e.printStackTrace();
                }finally{
                    objInstalacion.cerrar();
                }*/

                if(mac_ant.compareTo(mac)!=0){
                    /*Configuracion conf = new Configuracion(this._ip, this._puerto, this._db, usuario, clave);
                    String ruc_empresa = conf.getValor("ruc");
                    String empresa = conf.getValor("razon_social");
                    conf.cerrar();

                    String id_activo_ant = objActivo.getIdActivo(mac_ant);
                    String id_activo = objActivo.getIdActivoBodega(id_instalacion, mac);

                    Cliente objCliente = new Cliente(this._ip, this._puerto, this._db, usuario, clave);
                    ResultSet rsCliente = objCliente.getCliente(id_cliente);
                    String dni_recibe = "";
                    String persona_recibe = "";
                    try{
                        if(rsCliente.next()){
                            dni_recibe = rsCliente.getString("ruc")!=null ? rsCliente.getString("ruc") : "";
                            persona_recibe = rsCliente.getString("razon_social")!=null ? rsCliente.getString("razon_social") : "";
                            rsCliente.close();
                        }
                    }catch(Exception e){
                        e.printStackTrace();
                    }finally{
                        objCliente.cerrar();
                    }

                    String observacion = "";
                    Documento objDocumento = new Documento(this._ip, this._puerto, this._db, usuario, clave);
                    try{
                        ResultSet rsDoc = objDocumento.getDocumento("p");
                        if(rsDoc.next()){
                            observacion = rsDoc.getString("documento")!=null ? rsDoc.getString("documento") : "";
                            rsDoc.close();
                        }
                    }catch(Exception e){
                        e.printStackTrace();
                    }finally{
                        objDocumento.cerrar();
                    }*/

                    // ingreso del activo a bodega 
                    long sec_num_documento = objActivo.getNunDocumento();
                    String num_documento = String.valueOf(sec_num_documento);
                    //String id_personalizacion = objActivo.insertarPersonalizacion(id_sucursal_sesion, num_documento, "e", Fecha.getFecha("ISO"),
                    //        dni_recibe, ruc_empresa, persona_recibe, empresa, observacion, id_activo_ant);
                    //if(id_personalizacion.compareTo("-1") != 0){
                    //    auditoria.setRegistro(request, "REGISTRO DE DOCUMENTO DE PERSONALIZACION DE ACTIVOS Nro.: "+num_documento);
                    //}

                    // salida del activo de bodega hacia el cliente 
                    num_documento = String.valueOf(sec_num_documento+1);
                    //id_personalizacion = objActivo.insertarPersonalizacion(id_sucursal_sesion, num_documento, "e", Fecha.getFecha("ISO"),
                    //        ruc_empresa, dni_recibe, empresa, persona_recibe, observacion, id_activo);
                    //if(id_personalizacion.compareTo("-1") != 0){
                    //    auditoria.setRegistro(request, "REGISTRO DE DOCUMENTO DE PERSONALIZACION DE ACTIVOS Nro.: "+num_documento);
                    //}
                    
                }

                auditoria.setRegistro(request, "REGISTRO DE SOLUCION DE ORDEN DE TRABAJO NRO. "+ id_sucursal_sesion + "-" +num_orden);
                auditoria.cerrar();
                r = "err»0^vta»otr^tbl»"+objOrdenTrabajo.paginar("vta_orden_trabajo", "id_orden_trabajo,numero_orden,txt_tipo_trabajo,razon_social,toDateSQL(fecha_reporte),sector,direccion_instalacion,diagnostico_tecnico", WHERE, Integer.parseInt(p), 100)+
                    "^msg»Información guardada satisfactoriamente.";
            }

            out.print(r);
        } finally {
            objActivo.cerrar();
            objOrdenTrabajo.cerrar();
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