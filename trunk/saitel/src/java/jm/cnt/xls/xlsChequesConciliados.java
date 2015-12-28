/**
** @version 1.0
** @package FACTURAPYMES.
* @author Jorge Washington Mueses Cevallos.
* @copyright Copyright (C) 2011 por Jorge Mueses. Todos los derechos reservados.
* @license http://www.gnu.org/copyleft/gpl.html GNU/GPL.
** FACTURAPYMES es un software de libre distribuciÃ³n, que puede ser
* copiado y distribuido bajo los tÃ©rminos de la Licencia
* Attribution-NonCommercial-NoDerivs 3.0 Unported,
* de acuerdo con la publicada por la CREATIVE COMMONS CORPORATION.
*/

package jm.cnt.xls;

import java.io.*;

import javax.servlet.*;
import javax.servlet.http.*;
import java.sql.ResultSet;

import jm.web.DataBase;
import jm.ReporteXls;

/**
 *
 * @author Jorge
 */
public class xlsChequesConciliados extends HttpServlet {
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
        
        if(request.getHeader("User-Agent").toLowerCase().contains("windows")){
            response.setContentType("application/vnd.ms-excel;");
            response.setHeader("Content-disposition", "inline; filename=ChequesConciliados.xls;");
        }else{
            response.setContentType("text/xml;");
            response.setHeader("Content-disposition", "attachment; filename=ChequesConciliados.xml;");
        }
        response.setHeader("Pragma", "no-cache");
        response.setHeader("Expires", "Mon, 01 Jan 2001 00:00:01 GMT");
        response.setHeader("Cache-Control", "no-store");
        response.setHeader("Cache-Control", "must-revalidate");
        response.setHeader("Cache-Control", "no-cache");
        PrintWriter out = response.getWriter();

        String fini = request.getParameter("fini");
        String ffin = request.getParameter("ffin");
        
        DataBase objDB = new DataBase(this._ip, this._puerto, this._db, usuario, clave);

        String subtitulo = "Cheques Conciliados desde "+fini+" hasta "+ffin;

        ResultSet registros = objDB.consulta("SELECT num_comprobante, ci_ruc, replace(orden_de, '&', 'y'), banco, cuenta, num_cheque, valor_son, todatesql(fecha_actual), todatesql(fecha_proceso), todatesql(fecha_en_efectivo) "
                + "FROM tbl_comprobante_egreso "
                + "where tipo_pago='c' and anulado=false and fecha_en_efectivo between '"+fini+"' and '"+ffin+"';");
        String cabTabla[] = new String [] {"#", "No. COMPROBANTE", "RUC", "ORDEN DE", "BANCO", "CUENTA", "NUM. CHE.", "VALOR", "F. EMISON", "POST-FECHA", "F. EFECTIVO"};
        ReporteXls reporte = new ReporteXls();

        String xls = reporte.lista(subtitulo, cabTabla, registros, 12);

        out.print(xls);

        try{
            registros.close();
        }catch(Exception e){
            e.printStackTrace();
        }finally{
            objDB.cerrar();
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
