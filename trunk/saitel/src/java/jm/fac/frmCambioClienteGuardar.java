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
import jm.fac.clas.Cliente;
import jm.fac.clas.Instalacion;
import jm.fac.clas.PlanServicio;
import jm.inf.clas.Radius;
import jm.seg.clas.Auditoria;
import jm.web.Fecha;

/**
 *
 * @author Jorge
 */
public class frmCambioClienteGuardar extends HttpServlet {
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
        
        Instalacion objInstalacion = new Instalacion(this._ip, this._puerto, this._db, usuario, clave);
        PlanServicio objPlanServicio = new PlanServicio(this._ip, this._puerto, this._db, usuario, clave);
        
        try {
            String r = "msg»Ha ocurrido un error inesperado, por favor, vuelva a intentarlo más tarde o " +
                    "contáctese con el administrador del sistema para mayor información.";
            String WHERE = request.getParameter("WHERE");
            String p = request.getParameter("p") != null ? request.getParameter("p") : "0";
            String id_instalacion_cliente = request.getParameter("idInsCli");
            String id_cliente_nuevo = request.getParameter("idCliNue");
            String id_instalacion = request.getParameter("idIns");
            String fecha_cambio = Fecha.getFecha("SQL");

            //if(!objInstalacion.enConflictoCambioPlan(id_cambio_plan, id_instalacion, fecha_cambio)){
                String ruc = "";
                String num_instalacion = "";
                String id_sucursal = "";
                String id_plan_contratado = "";
                String radusername_ant = "";
                try{
                    ResultSet rsInstalacion = objInstalacion.getInstalacion(id_instalacion);
                    if(rsInstalacion.next()){
                        id_sucursal = (rsInstalacion.getString("id_sucursal")!=null) ? rsInstalacion.getString("id_sucursal") : "";
                        num_instalacion = (rsInstalacion.getString("num_instalacion")!=null) ? rsInstalacion.getString("num_instalacion") : "";
                        id_plan_contratado = (rsInstalacion.getString("id_plan_contratado")!=null) ? rsInstalacion.getString("id_plan_contratado") : "";
                        radusername_ant = (rsInstalacion.getString("radusername")!=null) ? rsInstalacion.getString("radusername") : "";
                        rsInstalacion.close();
                    }
                }catch(Exception e){
                    e.printStackTrace();
                }
                
                Cliente objCliente = new Cliente(this._ip, this._puerto, this._db, usuario, clave);
                try{
                    ResultSet rsCliente = objCliente.getCliente(id_cliente_nuevo);
                    if(rsCliente.next()){
                        ruc = (rsCliente.getString("ruc")!=null) ? rsCliente.getString("ruc") : "";
                        rsCliente.close();
                    }
                }catch(Exception e){
                    e.printStackTrace();
                }finally{
                    objCliente.cerrar();
                }
                
                String radusername = ruc+"_"+id_sucursal+"_"+num_instalacion;

                if(id_instalacion_cliente.compareTo("-1")==0){
                    String pk = objInstalacion.insertarCambioCliente(id_instalacion, id_cliente_nuevo, fecha_cambio, radusername);
                    if(pk.compareTo("-1")!=0){
                        
                        String plan = objPlanServicio.getSufPlan(id_plan_contratado);

                        String msg = "";
                        /*String msg = "Error al realizar el cambio en el freeradius.";
                        Radius dbRadius = new Radius(this._ip, this._puerto, this._db, usuario, clave);
                        if(dbRadius.cambioRazonSocial(radusername_ant, radusername, plan)){
                            msg = "Información ingresada satisfactoriamente.";
                        }
                        dbRadius.cerrar();*/
                                
                        Auditoria auditoria = new Auditoria(this._ip, this._puerto, this._db, usuario, clave);
                        auditoria.setRegistro(request, "REGISTRO DE NUEVO CAMBIO DE RAZON SOCIAL DEL SERVICIO DE INTERNET PARA LA INSTALACION NRO. "+id_sucursal+"-"+num_instalacion);
                        auditoria.cerrar();
                        r = "err»0^vta»cmp1^fun»_R('bloq_cmp');_R('cmp');imprimir('pdfCambioCliente?id="+pk+"');^msg»" + msg +
                                "^tbl»"+objInstalacion.paginar("vta_instalacion", "id_instalacion,ruc,razon_social,ip,txt_estado_servicio", WHERE, Integer.parseInt(p), 100);
                    }
                /*}else{
                    if(objInstalacion.actualizarCambioPlan(id_instalacion, id_cliente_nuevo)){
                        Auditoria auditoria = new Auditoria(this._ip, this._puerto, this._db, usuario, clave);
                        auditoria.setRegistro(request, "ACTUALZACION DEL CAMBIO DE PLAN DE SERVICIO DE INTERNET PARA LA INSTALACION NRO: "+id_sucursal+"-"+num_instalacion);
                        auditoria.cerrar();
                        r = "msg»Información guardada satisfactoriamente.";
                    }*/
                }
            //}

            out.print(r);
        } finally {
            objPlanServicio.cerrar();
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