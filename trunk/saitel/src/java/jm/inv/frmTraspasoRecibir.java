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

package jm.inv;

import java.io.*;
import java.sql.ResultSet;

import javax.servlet.*;
import javax.servlet.http.*;
import jm.inv.clas.Traspaso;
import jm.seg.clas.Auditoria;

/**
 *
 * @author Jorge
 */
public class frmTraspasoRecibir extends HttpServlet {
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

        String r = "msg»Ha ocurrido un error inesperado, por favor, vuelva a intentarlo más tarde o " +
                   "contáctese con el administrador del sistema para mayor información.";

        Traspaso objTraspaso = new Traspaso(this._ip, this._puerto, this._db, usuario, clave);
        
        try {
            String WHERE = request.getParameter("WHERE");
            String p = request.getParameter("p") != null ? request.getParameter("p") : "0";
            String id = request.getParameter("id");
            String usuario_recepcion = request.getParameter("usRe");
            String fecha_recepcion = request.getParameter("feRe");

            String id_traspaso_detalles = "";
            String cantidades = "";
            int i=0;
            while(request.getParameter("idTrD"+i)!=null){
                id_traspaso_detalles += request.getParameter("idTrD"+i) + ",";
                cantidades += request.getParameter("ctRec"+i) + ",";
                i++;
            }
            if(id_traspaso_detalles.compareTo("")!=0){
                id_traspaso_detalles = id_traspaso_detalles.substring(0, id_traspaso_detalles.length()-1);
                cantidades = cantidades.substring(0, cantidades.length()-1);
            }

            if(objTraspaso.recibir(id, usuario_recepcion, fecha_recepcion, id_traspaso_detalles, cantidades)){
                String num_traspaso="";
                try{
                    ResultSet rs_Traspaso = objTraspaso.getTraspaso(id);
                    if(rs_Traspaso.next()){
                        num_traspaso = rs_Traspaso.getString("num_traspaso")!=null ? rs_Traspaso.getString("num_traspaso") : "";
                        rs_Traspaso.close();
                    }
                }catch(Exception e){
                    e.printStackTrace();
                }

                Auditoria auditoria = new Auditoria(this._ip, this._puerto, this._db, usuario, clave);
                auditoria.setRegistro(request, "RECEPCION DEL TRASPASO Nro: "+num_traspaso);
                auditoria.cerrar();
                r = "err»0^vta»cmp^tbl»"+objTraspaso.paginar("vta_traspaso", "id_traspaso,num_traspaso,origen,usuario_origen,fecha_envio,recepcion,usuario_recepcion,fecha_recepcion", WHERE, Integer.parseInt(p), 100)+"^msg»Recepción del formulario de traspaso satisfactorio.";
            }

            out.print(r);
        } finally {
            objTraspaso.cerrar();
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
