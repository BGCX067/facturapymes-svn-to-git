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

package jm.cnt.pdf;

import java.io.*;
import java.sql.ResultSet;


import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import jm.Reporte;
import jm.adm.clas.Configuracion;
import jm.cnt.clas.ComprobanteEgreso;
import jm.web.Archivo;

/**
 *
 * @author Jorge
 */
public class pdfChequesConciliados extends HttpServlet {
    private String _ip = null;
    private int _puerto = 5432;
    private String _db = null;
    private String _dir = null;

    public void init(ServletConfig config) throws ServletException
    {
        this._ip = config.getServletContext().getInitParameter("_IP");
        this._puerto = Integer.parseInt(config.getServletContext().getInitParameter("_PUERTO"));
        this._db = config.getServletContext().getInitParameter("_DB");
        this._dir = config.getServletContext().getInitParameter("_DIR");
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
        
        //PrintWriter out = response.getWriter();
        
        String fini = request.getParameter("fini");
        String ffin = request.getParameter("ffin");
        
        Configuracion conf = new Configuracion(this._ip, this._puerto, this._db, usuario, clave);
        String titulo = conf.getValor("razon_social");
        String ruc = conf.getValor("ruc");
        String direccion = conf.getValor("dir_matriz");
        String rep_pie = conf.getValor("rep_pie");
        conf.cerrar();

        String path = String.valueOf(request.getRequestURL());
        path = path.substring(0, path.lastIndexOf("/"));
        Archivo archivo = new Archivo(this._ip, this._puerto, this._db, usuario, clave);
        String logo = path + "/img/" + archivo.getArchivo(this._dir, 1);
        archivo.cerrar();

        ComprobanteEgreso objComprobanteEgreso = new ComprobanteEgreso(this._ip, this._puerto, this._db, usuario, clave);
        
        ResultSet registros = objComprobanteEgreso.consulta("SELECT num_comprobante, ci_ruc, orden_de, banco, cuenta, num_cheque, valor_son, todatesql(fecha_actual), todatesql(fecha_proceso), todatesql(fecha_en_efectivo) "
                + "FROM tbl_comprobante_egreso "
                + "where tipo_pago='c' and anulado=false and fecha_en_efectivo between '"+fini+"' and '"+ffin+"';");
        String subtitulo = "Cheques Conciliados desde "+fini+" hasta "+ffin;
        Reporte reporte = new Reporte(false, logo, titulo, ruc, subtitulo, direccion, "", rep_pie);
        String cabTabla[] = new String [] {"Nro.", "No. COMP.", "RUC", "ORDEN DE", "BANCO", "CUENTA", "NUM. CHE.", "VALOR", "F. EMISON", "POST-FECHA", "F. EFECTIVO"};
        float anchoTabla[] = new float[]{20f,30f,50f,100f,60f,40f,40f,40f,40f,40f,40f};
        reporte.lista(response, cabTabla, anchoTabla, registros);
        
        objComprobanteEgreso.cerrar();
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
