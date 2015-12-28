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
import jm.fac.clas.PlanServicio;
import jm.seg.clas.Auditoria;
import jm.seg.clas.Rol;
import jm.web.DataBase;

/**
 * Servlet que elimina registros de una tabla de una base de datos.
 */
public class EliminarVelocidad extends HttpServlet {

    private String _ip = null;
    private int _puerto = 1433;
    private String _db = null;

    public void init(ServletConfig config) throws ServletException
    {
        this._ip = config.getServletContext().getInitParameter("_IP");
        this._puerto = Integer.parseInt(config.getServletContext().getInitParameter("_PUERTO"));
        this._db = config.getServletContext().getInitParameter("_DB");
    }


/**
 * Funci�n que gestiona la eliminaci�n de registros de una tabla.
 * @param request servlet request
 * @param response servlet response
 * @return una cadena de respuesta al cliente.
*/
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        HttpSession sesion = request.getSession(true);
        String id_rol = (String)sesion.getAttribute("id_rol");
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
        String id_plan_servicio = request.getParameter("idPS");
        String velocidad = request.getParameter("brLi");

        Rol objRol = new Rol(this._ip, this._puerto, this._db, usuario, clave);
        DataBase db = new DataBase(this._ip, this._puerto, this._db, usuario, clave);
        String res = "msg»Ha ocurrido un error inesperado, por favor, vuelva a intentarlo más tarde o " +
                        "contáctese con el administrador del sistema para mayor información.";
        try {
            try{
                String html = "";
                if(db.ejecutar("update tbl_plan_servicio set eliminado=true where id_plan_servicio="+id_plan_servicio)){

                    PlanServicio objPlanServicio = new PlanServicio(this._ip, this._puerto, this._db, usuario, clave);
                    String plan = "";
                    try{
                        ResultSet rsplan = objPlanServicio.getPlan(id);
                        if(rsplan.next()){
                            plan = (rsplan.getString("plan")!=null) ? rsplan.getString("plan") : "";
                            rsplan.close();
                        }
                    }catch(Exception e){
                        e.printStackTrace();
                    }

                    Auditoria auditoria = new Auditoria(this._ip, this._puerto, this._db, usuario, clave);
                    auditoria.setRegistro(request, "ELIMINACION DEL BURST LIMIT " + velocidad + " DEL PLAN "+plan);
                    auditoria.cerrar();
                    
                    try{
                        int i=0;
                        String burst_limit = "";
                        String max_limit = "";
                        String burst_threshold = "";
                        String down_burst_time = "";
                        String limit_at = "";
                        String up_max_limit = "";
                        String up_burst_threshold = "";
                        String up_burst_time = "";
                        String up_limit_at = "";
                        String edit = "readonly";
                        if(id.compareTo("-1")==0 || (id.compareTo("-1")!=0 && objRol.getAcceso(id_rol, "planEditar"))){
                            edit = "";
                        }
                        
                        ResultSet rsVelocidad = objPlanServicio.getVelocidades(id);
                        while(rsVelocidad.next()){
                            burst_limit = (rsVelocidad.getString("burst_limit")!=null) ? rsVelocidad.getString("burst_limit") : "";
                            max_limit = (rsVelocidad.getString("max_limit")!=null) ? rsVelocidad.getString("max_limit") : "";
                            burst_threshold = (rsVelocidad.getString("burst_threshold")!=null) ? rsVelocidad.getString("burst_threshold") : "";
                            down_burst_time = (rsVelocidad.getString("burst_time")!=null) ? rsVelocidad.getString("burst_time") : "";
                            limit_at = (rsVelocidad.getString("limit_at")!=null) ? rsVelocidad.getString("limit_at") : "";
                            up_max_limit = (rsVelocidad.getString("up_max_limit")!=null) ? rsVelocidad.getString("up_max_limit") : "";
                            up_burst_threshold = (rsVelocidad.getString("up_burst_threshold")!=null) ? rsVelocidad.getString("up_burst_threshold") : "";
                            up_burst_time = (rsVelocidad.getString("up_burst_time")!=null) ? rsVelocidad.getString("up_burst_time") : "";
                            up_limit_at = (rsVelocidad.getString("up_limit_at")!=null) ? rsVelocidad.getString("up_limit_at") : "";
                            html += "<tr id='rTI"+i+"' valign='top' class='jm_filaPar' style='cursor:pointer' onmouseover=\"this.className='jm_filaSobre'\" onmouseout=\"this.className='jm_filaPar'\" >";
                            html += "<td width='71' onclick=\"adm_tarifaVer("+i+")\" ><input id='brLi"+i+"' type='text' size='5' maxlength='6' value='"+burst_limit+"' "+edit+" onkeypress=\"_numero(event);_NoE(event);\" onkeyup=\"fac_velocidadEditar(event, "+i+");\" /></td>";
                            html += "<td width='71' id='mxLi"+i+"' onclick=\"adm_tarifaVer("+i+")\" >"+up_max_limit+" / "+max_limit+"</td>";
                            html += "<td width='81' id='brTh"+i+"' onclick=\"adm_tarifaVer("+i+")\" >"+up_burst_threshold+" / "+burst_threshold+"</td>";
                            html += "<td width='61' id='brTi"+i+"' onclick=\"adm_tarifaVer("+i+")\" >"+up_burst_time+" / "+down_burst_time+"</td>";
                            html += "<td width='71' id='lmAt"+i+"' onclick=\"adm_tarifaVer("+i+")\" >"+up_limit_at+" / "+limit_at+"</td>";
                            html += "<td align='center' width='60'><input type='hidden' id='idPS"+i+"' value='"+id_plan_servicio+"' />" +
                                    "<div class='jm_icoCon' id='g"+i+"' style='visibility:hidden;background-position: -545px 0px;' onclick='fac_velocidadGuardar("+i+");' title='guardar'>&nbsp;</div>" +
                                    "<div class='jm_icoCon' id='e"+i+"' style='background-position:-545px -18px;' onclick='fac_velocidadEliminar("+i+");' title='eliminar'>&nbsp;</div></td>" +
                                    "</tr>";
                            i++;
                        }
                        rsVelocidad.close();
                    }catch(Exception e){
                        e.printStackTrace();
                    }finally{
                        objPlanServicio.cerrar();
                    }
                    
                    res = "obj»tblI^frm»"+html;
                }
            }catch(Exception e){
                System.out.print(e.getMessage());
            }

            out.print(res);
        } finally {
            objRol.cerrar();
            db.cerrar();
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
