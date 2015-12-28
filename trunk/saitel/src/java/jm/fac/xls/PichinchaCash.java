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

package jm.fac.xls;

import java.io.*;

import javax.servlet.*;
import javax.servlet.http.*;
import java.sql.ResultSet;
import jm.web.Addons;

import jm.web.DataBase;

/**
 *
 * @author Jorge
 */
public class PichinchaCash extends HttpServlet {
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
            response.setHeader("Content-disposition", "inline; filename=cash_management_pichncha.xls;");
        }else{
            response.setContentType("text/xml;");
            response.setHeader("Content-disposition", "attachment; filename=cash_management_pichncha.ods;");
        }
        response.setHeader("Pragma", "no-cache");
        response.setHeader("Expires", "Mon, 01 Jan 2001 00:00:01 GMT");
        response.setHeader("Cache-Control", "no-store");
        response.setHeader("Cache-Control", "must-revalidate");
        response.setHeader("Cache-Control", "no-cache");
        PrintWriter out = response.getWriter();
        
        String comision = request.getParameter("comision");

        DataBase objDB = new DataBase(this._ip, this._puerto, this._db, usuario, clave);

        try{
            StringBuilder xls = new StringBuilder();
            try{
                objDB.consulta("select proc_calcularprefacturaspendientes();");
                
                ResultSet registros = objDB.consulta("select 'CO' as co, id_sucursal::varchar || id_instalacion::varchar as id_instalacion, "
                    + "'USD' as usd, total+"+comision+" as total, 'REC' as rec, txt_periodo as periodo, "
                    + "case tipo_documento when '04' then 'R' when '05' then 'C' else 'P' end as tipo_doc, ruc, razon_social "
                    + "from vta_prefactura "
                    + "order by id_sucursal");

                String co = "";
                String id_instalacion = "";
                String usd = "";
                String total = "";
                String rec = "";
                String periodo = "";
                String tipo_doc = "";
                String ruc = "";
                String razon_social = "";
                while(registros.next()){
                    co = registros.getString("co")!=null ? registros.getString("co").replace(".", "") : "CO";
                    id_instalacion = registros.getString("id_instalacion")!=null ? registros.getString("id_instalacion") : "";
                    usd = registros.getString("usd")!=null ? registros.getString("usd") : "USD";
                    total = registros.getString("total")!=null ? registros.getString("total") : "0";
                    rec = registros.getString("rec")!=null ? registros.getString("rec") : "REC";
                    periodo = registros.getString("periodo")!=null ? registros.getString("periodo") : "";
                    tipo_doc = registros.getString("tipo_doc")!=null ? registros.getString("tipo_doc") : "";
                    ruc = registros.getString("ruc")!=null ? registros.getString("ruc") : "";
                    razon_social = registros.getString("razon_social")!=null ? registros.getString("razon_social") : "";

                    xls.append(co);
                    xls.append("\t");
                    xls.append(id_instalacion);
                    xls.append("\t");
                    xls.append(usd);
                    xls.append("\t");
                    xls.append(Addons.rellenarCeros( total, 2).replace(".", "") );
                    xls.append("\t");
                    xls.append(rec);
                    xls.append("\t \t \t");
                    xls.append(periodo);
                    xls.append("\t");
                    xls.append(tipo_doc);
                    xls.append("\t");
                    xls.append(ruc);
                    xls.append("\t");
                    xls.append( this.quitarEspeciales(razon_social) );
                    xls.append("\n");
                }
                registros.close();
            }catch(Exception e){
                e.printStackTrace();
            }
            out.print(xls.toString());        
            
        }catch(Exception e){
            e.printStackTrace();
        }finally{
            objDB.cerrar();
        }
    }


    public String quitarEspeciales(String cad)
    {
        cad = cad.replace("&", " ");
        cad = cad.replace("ü", " ");
        cad = cad.replace("Ü", " ");
        cad = cad.replace("%", " ");
        cad = cad.replace("#", " ");
        cad = cad.replace("¿", " ");
        cad = cad.replace( "¡", " ");
        cad = cad.replace("\"", " ");
        cad = cad.replace("Ñ", "N");
        cad = cad.replace("ñ", "N");
        cad = cad.replace("Ú", "U");
        cad = cad.replace("Ó", "O");
        cad = cad.replace("Í", "I");
        cad = cad.replace("É", "E");
        cad = cad.replace("Á", "A");
        cad = cad.replace("ú", "U");
        cad = cad.replace("ó", "O");
        cad = cad.replace("í", "I");
        cad = cad.replace("é", "E");
        cad = cad.replace("á", "A");
        return cad;
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
