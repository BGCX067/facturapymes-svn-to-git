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

package jm.cnt;

import java.io.*;

import javax.servlet.*;
import javax.servlet.http.*;
import java.sql.ResultSet;
import jm.cnt.clas.LibroDiario;
import jm.web.Fecha;
/**
 *
 * @author Jorge
 */
public class tblLibroDiario extends HttpServlet {
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
        try {
            String p = request.getParameter("p") != null ? request.getParameter("p") : "0";
            String fecha_ini = request.getParameter("fi");
            String fecha_fin = request.getParameter("ff");
            String id_sucursal = request.getParameter("idSuc");
            String alto = request.getParameter("aB");
            int _altBody = Integer.parseInt(alto) - 220;

            LibroDiario objLibroDiario = new LibroDiario(this._ip, this._puerto, this._db, usuario, clave);
            ResultSet rsLibroDiario = null;
            int num_pags = 0;
            if(id_sucursal.compareTo("-0")==0){
                num_pags = objLibroDiario.getNumPags("SELECT count(*) FROM vta_libro_diario_mayor where fecha_asiento between '"+fecha_ini+"' and '"+fecha_fin+"'", 100);
                if(Integer.parseInt(p) > num_pags){
                    p = "0";
                }
                rsLibroDiario = objLibroDiario.getTransacciones(fecha_ini, fecha_fin, Integer.parseInt(p), 100);
            }else{
                num_pags = objLibroDiario.getNumPags("SELECT count(*) FROM vta_libro_diario_mayor where id_sucursal="+id_sucursal+" and fecha_asiento between '"+fecha_ini+"' and '"+fecha_fin+"'", 100);
                if(Integer.parseInt(p) > num_pags){
                    p = "0";
                }
                rsLibroDiario = objLibroDiario.getTransacciones(id_sucursal, fecha_ini, fecha_fin, Integer.parseInt(p), 100);
            }

            //String html = "obj»d_11^frm»NO SE HAN ENCONTRADO DATOS PARA MOSTRAR";
            String html = "obj»d_11^fun»cnt_setNavegacion("+num_pags+");^frm»<input type='hidden' id='imp' value='pdfLibroDiario' /><div id='auxCon' style='width:670px;'><TABLE cellspacing='0' cellpadding='0'>" +
                        "<TR><TH class='jm_TH' width='70'>FECHA</TH><TH class='jm_TH' width='130'>C&Oacute;DIGO</TH>" +
                        "<TH class='jm_TH' width='300'>DETALLE</TH><TH class='jm_TH' width='80'>DEBE</TH>" +
                        "<TH class='jm_TH' width='80'>HABER</TH></TR></TABLE>";
            html += "<DIV style='overflow:auto;width:685px;height:"+ _altBody +"px;'>" +
                        "<TABLE class='jm_tabla' cellspacing='1' cellpadding='0' id='tblCon'>";
            try{
                int i=0;
                String fil = "jm_filaPar";
                String num_asiento = "";
                String detalle = "";
                String aux = "";
                String auxDet = "";
                String debe = "";
                String haber = "";
                while(rsLibroDiario.next()){
                    num_asiento = (rsLibroDiario.getString("num_asiento")!=null) ? rsLibroDiario.getString("num_asiento") : "";
                    detalle = (rsLibroDiario.getString("detalle")!=null) ? rsLibroDiario.getString("detalle") : "";
                    debe = (rsLibroDiario.getString("debe")!=null) ? ((rsLibroDiario.getDouble("debe")>0)?rsLibroDiario.getString("debe"):"") : "";
                    haber = (rsLibroDiario.getString("haber")!=null) ? ((rsLibroDiario.getDouble("haber")>0)?rsLibroDiario.getString("haber"):"") : "";
                    if(num_asiento.compareTo(aux)!=0){
                        if(auxDet.compareTo("")!=0){
                            html += "<tr class='"+fil+"'>";
                            html += "<td width='70'>&nbsp;</td>";
                            html += "<td width='130'>&nbsp;</td>";
                            html += "<td> "+auxDet+"</td>";
                            html += "<td width='80'>&nbsp;</td>";
                            html += "<td width='80'>&nbsp;</td>";
                            html += "</tr>";
                        }
                        auxDet = detalle;

                        fil = (i%2==0) ? "jm_filaPar" : "jm_filaImp";
                        i++;

                        html += "<tr class='"+fil+"'>";
                        html += "<td width='70'>"+((rsLibroDiario.getString("fecha_asiento")!=null) ? Fecha.ISOaSQL(rsLibroDiario.getString("fecha_asiento")) : "")+"</td>";
                        html += "<td colspan='4' align='center'> -  "+num_asiento+"  - </td>";
                        html += "</tr>";
                        aux = num_asiento;
                    }
                    html += "<tr class='"+fil+"'>";
                    html += "<td width='70'>&nbsp;</td>";
                    html += "<td width='131'>"+((rsLibroDiario.getString("codigo_cuenta")!=null) ? rsLibroDiario.getString("codigo_cuenta") : "")+"</td>";
                    html += "<td width='301'>"+((rsLibroDiario.getString("nombre_cuenta")!=null) ? rsLibroDiario.getString("nombre_cuenta") : "")+"</td>";
                    html += "<td align='right' width='81'>"+debe+"</td>";
                    html += "<td align='right' width='81'>"+haber+"</td>";
                    html += "</tr>";
                }
                html += "<tr class='"+fil+"'>";
                html += "<td width='70'>&nbsp;</td>";
                html += "<td width='131'>&nbsp;</td>";
                html += "<td width='301'> "+auxDet+"</td>";
                html += "<td width='81'>&nbsp;</td>";
                html += "<td width='81'>&nbsp;</td>";
                html += "</tr>";

                rsLibroDiario.close();
            }catch(Exception e){
                e.printStackTrace();
            }finally{
                objLibroDiario.cerrar();
            }

            html += "</table></div></div>";
            
            out.print(html);
        } finally {
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
