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
public class EliminarTarifa extends HttpServlet {

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

        String id_plan_servicio = request.getParameter("idPlanServicio");
        String clave1 = request.getParameter("c");
        Rol objRol = new Rol(this._ip, this._puerto, this._db, usuario, clave);
        DataBase db = new DataBase(this._ip, this._puerto, this._db, usuario, clave);
        String res = "msg»Ha ocurrido un error inesperado, por favor, vuelva a intentarlo más tarde o " +
                        "contáctese con el administrador del sistema para mayor información.";
        try {
            try{
                String html = "";
                if(db.ejecutar("delete from tbl_tarifa WHERE id_tarifa="+clave1)){
                    Auditoria auditoria = new Auditoria(this._ip, this._puerto, this._db, usuario, clave);
                    auditoria.setRegistro(request, "ELIMINACION DEL REGISTRO DE CODIGO " + clave1+ " EN LA TABLA DE COSTOS DE TARIFAS");
                    auditoria.cerrar();

                    PlanServicio objPlanServicio = new PlanServicio(this._ip, this._puerto, this._db, usuario, clave);
                    try{
                        int i = 0;
                        String id_tarifa = "";
                        String vigente_desde = "";
                        String vigente_hasta = "";
                        String costo_plan = "";
                        ResultSet rsTarifas = objPlanServicio.getTarifas(id_plan_servicio);
                        while(rsTarifas.next()){
                            id_tarifa = (rsTarifas.getString("id_tarifa")!=null) ? rsTarifas.getString("id_tarifa") : "";
                            vigente_desde = (rsTarifas.getString("sql_vigente_desde")!=null) ? rsTarifas.getString("sql_vigente_desde") : "";
                            vigente_hasta = (rsTarifas.getString("sql_vigente_hasta")!=null) ? rsTarifas.getString("sql_vigente_hasta") : "";
                            costo_plan = (rsTarifas.getString("costo_plan")!=null) ? rsTarifas.getString("costo_plan") : "";
                            if(id_plan_servicio.compareTo("-1")==0 || (id_plan_servicio.compareTo("-1")!=0 && objRol.getAcceso(id_rol, "planEditar"))){
                                html += "<tr id='TrTI"+i+"' valign='top' class='jm_filaPar'>";
                                html += "<td width='120'><input id='fIni"+i+"' type='text' size='9' value='"+vigente_desde+"' readonly />"
                                        + "<input type='button' value='...' onClick=\"SelectorFecha.crear('fIni"+i+"', 'SQL', 'fac_tarifaEditar(event, "+i+");');\" /></td>";
                                html += "<td width='120'><input id='fFin"+i+"' type='text' size='9' value='"+vigente_hasta+"' onkeypress=\"_SUP(event);\" onkeyup=\"fac_tarifaEditar(event, "+i+");\" />"
                                        + "<input type='button' value='...' onClick=\"SelectorFecha.crear('fFin"+i+"', 'SQL', 'fac_tarifaEditar(event, "+i+");');\" /></td>";
                                html += "<td width='60'><input id='tar"+i+"' type='text' size='6' value='"+costo_plan+"' onkeypress=\"_evaluar(event, '0123456789.');\" onkeyup=\"fac_tarifaEditar(event, "+i+");\" /></td>";
                                html += "<td align='center' width='70'><input type='hidden' id='idT"+i+"' value='"+id_tarifa+"' />" +
                                        "<div class='jm_icoCon' id='Tg"+i+"' style='visibility:hidden;background-position: -545px 0px;' onclick='fac_tarifaGuardar("+i+");' title='guardar'>&nbsp;</div>" +
                                        "<div class='jm_icoCon' id='Te"+i+"' style='background-position:-545px -18px;' onclick='fac_tarifaEliminar("+i+");' title='eliminar'>&nbsp;</div></td>" +
                                        "</tr>";
                            }else{
                                html += "<tr id='TrTI"+i+"' valign='top' class='jm_filaPar'>";
                                html += "<td width='120'>"+vigente_desde+"</td>";
                                html += "<td width='120'>"+vigente_hasta+"</td>";
                                html += "<td width='60'>"+costo_plan+"</td>";
                                html += "<td align='center' width='70'><input type='hidden' id='idR"+i+"' value='"+id_tarifa+"' />&nbsp;</td></tr>";
                            }
                            i++;
                        }
                    }catch(Exception e){
                        e.printStackTrace();
                    }finally{
                        objPlanServicio.cerrar();
                    }

                    res = "obj»TtblI^frm»"+html;
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
