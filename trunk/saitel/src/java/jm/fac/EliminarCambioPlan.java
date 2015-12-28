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
import java.util.ArrayList;
import java.util.List;

import javax.servlet.*;
import javax.servlet.http.*;
import jm.seg.clas.Auditoria;
import jm.web.DataBase;

/**
 * Servlet que elimina registros de una tabla de una base de datos.
 */
public class EliminarCambioPlan extends HttpServlet {

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
        String usuario = (String)sesion.getAttribute("usuario");
        String clave = (String)sesion.getAttribute("clave");
        
        response.setContentType("text/html;charset=UTF-8");
        response.setHeader("Pragma", "no-cache");
        response.setHeader("Expires", "Mon, 01 Jan 2001 00:00:01 GMT");
        response.setHeader("Cache-Control", "no-store");
        response.setHeader("Cache-Control", "must-revalidate");
        response.setHeader("Cache-Control", "no-cache");
        PrintWriter out = response.getWriter();

        String i = request.getParameter("i");
        String clave1 = request.getParameter("c");
        String res = "msg»Ha ocurrido un error inesperado, por favor, vuelva a intentarlo más tarde o " +
                    "contáctese con el administrador del sistema para mayor información.";
        DataBase db = new DataBase(this._ip, this._puerto, this._db, usuario, clave);
        try{
            String id_plan_actual = "";
            try{
                ResultSet rsCambiosPlanes = db.consulta("select id_plan_servicio from tbl_instalacion_cambio_plan "
                        + "where id_instalacion_cambio_plan<>"+clave1+" and eliminado=false "
                        + "and id_instalacion=(select id_instalacion from tbl_instalacion_cambio_plan where id_instalacion_cambio_plan="+clave1+") "
                        + "order by id_instalacion_cambio_plan desc");
                if(rsCambiosPlanes.next()){
                    id_plan_actual = rsCambiosPlanes.getString(1)!=null ? rsCambiosPlanes.getString(1) : "";
                    rsCambiosPlanes.close();
                }
            }catch(Exception ex){}
            if(id_plan_actual.compareTo("")==0){
                try{
                    ResultSet rsInstalacion = db.consulta("select id_plan_establecido from tbl_instalacion where id_instalacion="
                        + "(select id_instalacion from tbl_instalacion_cambio_plan where id_instalacion_cambio_plan="+clave1+")");
                    if(rsInstalacion.next()){
                        id_plan_actual = rsInstalacion.getString(1)!=null ? rsInstalacion.getString(1) : "";
                        rsInstalacion.close();
                    }
                }catch(Exception ex){}
            }


            List sql = new ArrayList();
            sql.add("update tbl_instalacion_cambio_plan set eliminado=true WHERE id_instalacion_cambio_plan="+clave1);
            sql.add("update tbl_instalacion set id_plan_actual="+id_plan_actual+" WHERE id_instalacion="+
                    "(select id_instalacion from tbl_instalacion_cambio_plan where id_instalacion_cambio_plan="+clave1+")");
            if(db.transacciones(sql)){
                Auditoria auditoria = new Auditoria(this._ip, this._puerto, this._db, usuario, clave);
                auditoria.setRegistro(request, "ELIMINACION DEL REGISTRO DE CODIGO " + clave1 + " EN LA TABLA cambio_plan");
                auditoria.cerrar();
                res = "fun»_R('rCP"+i+"');";
            }
        }catch(Exception e){
            System.out.print(e.getMessage());
        }
        try {
            out.print(res);
        } finally {
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
