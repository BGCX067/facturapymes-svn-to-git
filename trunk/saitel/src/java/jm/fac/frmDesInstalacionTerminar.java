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

package jm.fac;

import java.io.*;
import java.sql.ResultSet;

import javax.servlet.*;
import javax.servlet.http.*;
import jm.fac.clas.Instalacion;
import jm.inf.clas.Radius;
import jm.seg.clas.Auditoria;

/**
 *
 * @author Jorge
 */
public class frmDesInstalacionTerminar extends HttpServlet {
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
        //int id_sucursal_sesion = (Integer)sesion.getAttribute("sucursal");
        String usuario = (String)sesion.getAttribute("usuario");
        String clave = (String)sesion.getAttribute("clave");
        
        response.setContentType("text/html;charset=UTF-8");
        response.setHeader("Pragma", "no-cache");
        response.setHeader("Expires", "Mon, 01 Jan 2001 00:00:01 GMT");
        response.setHeader("Cache-Control", "no-store");
        response.setHeader("Cache-Control", "must-revalidate");
        response.setHeader("Cache-Control", "no-cache");
        PrintWriter out = response.getWriter();

        String id = request.getParameter("id");
        String WHERE = request.getParameter("WHERE");
        String p = request.getParameter("p") != null ? request.getParameter("p") : "0";
        String id_sucursal = request.getParameter("id_sucursal");
        String num_desinstalacion = request.getParameter("num_desinstalacion");
        String observacion_terminado = request.getParameter("observacion_terminado");
        //String id_cliente = request.getParameter("id_cliente");
        //String mac = request.getParameter("mac");

        Instalacion objInstalacion = new Instalacion(this._ip, this._puerto, this._db, usuario, clave);
        
        String radusername = "";
        try{
            ResultSet rsInstalacion = objInstalacion.getInstalacion(id);
            if(rsInstalacion.next()){
                radusername = (rsInstalacion.getString("radusername")!=null) ? rsInstalacion.getString("radusername") : "";
                rsInstalacion.close();
            }
        }catch(Exception e){
            e.printStackTrace();
        }

        try {
            String r = "msg»Ha ocurrido un error inesperado, por favor, vuelva a intentarlo más tarde o " +
                "contáctese con el administrador del sistema para mayor información.";

            if(id.compareTo("-1")!=0){
                if(objInstalacion.desInstalar(id, observacion_terminado, "t", "")){
                    String msg = "Confirmación de registro de desinstalación realizado satisfactoriamente.";
                    
                    /*Radius dbRadius = new Radius(this._ip, this._puerto, this._db, usuario, clave);
                    if(!dbRadius.eliminar(radusername)){
                        msg = "Ha ocurrido un error al tratar de eliminar el usuario "+radusername+" en el FREERADIUS.";
                    }
                    dbRadius.cerrar();*/

                    Auditoria auditoria = new Auditoria(this._ip, this._puerto, this._db, usuario, clave);
                    auditoria.setRegistro(request, "CONFIRMACION DE REGISTRO DE DESINSTALACION NRO.: "+id_sucursal+"-"+num_desinstalacion);

                    /*Activo objActivo = new Activo(this._ip, this._puerto, this._db, usuario, clave);
                    String id_activo = objActivo.getIdActivo(mac);
                    objActivo.cerrar();

                    Configuracion conf = new Configuracion(this._ip, this._puerto, this._db, usuario, clave);
                    String ruc_empresa = conf.getValor("ruc");
                    String empresa = conf.getValor("razon_social");
                    conf.cerrar();

                    SCliente objCliente = new Cliente(this._ip, this._puerto, this._db, usuario, clave);
                    ResultSet rsCliente = objCliente.getCliente(id_cliente);
                    String dni_entrega = "";
                    String persona_entrega = "";
                    try{
                        if(rsCliente.next()){
                            dni_entrega = rsCliente.getString("ruc")!=null ? rsCliente.getString("ruc") : "";
                            persona_entrega = rsCliente.getString("razon_social")!=null ? rsCliente.getString("razon_social") : "";
                            rsCliente.close();
                        }
                    }catch(Exception e){
                        e.printStackTrace();
                    }finally{
                      objCliente.cerrar();
                    }

                    tring observacion = "";
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
                    }

                    if(id_activo.compareTo("-1")!=0){
                        String num_documento = String.valueOf(objActivo.getNunDocumento());
                        String id_personalizacion = objActivo.insertarPersonalizacion(id_sucursal_sesion, num_documento, "e", Fecha.getFecha("ISO"),
                                dni_entrega, ruc_empresa, persona_entrega, empresa, observacion, id_activo);
                        if(id_personalizacion.compareTo("-1") != 0){
                            auditoria.setRegistro(request, "REGISTRO DE DOCUMENTO DE PERSONALIZACION DE ACTIVOS Nro.: "+num_documento);
                        }
                    }*/

                    auditoria.cerrar();
                    r = "err»0^vta»cmp^msg»"+msg+
                            "^tbl»"+objInstalacion.paginar("vta_instalacion", "id_instalacion,ruc,razon_social,ip,txt_estado_servicio", WHERE, Integer.parseInt(p), 100);
                }
            }

            out.print(r);

        } finally {
            objInstalacion.cerrar();
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
