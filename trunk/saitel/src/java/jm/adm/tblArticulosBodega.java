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

package jm.adm;

import java.io.*;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

import javax.servlet.*;
import javax.servlet.http.*;
import jm.adm.clas.Bodega;

/**
 *
 * @author Jorge
 */
public class tblArticulosBodega extends HttpServlet {
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
        
        Bodega objBodega = new Bodega(this._ip, this._puerto, this._db, usuario, clave);
        
        String id_bodega = request.getParameter("idB");
        int  p = request.getParameter("p") != null ? Integer.parseInt( request.getParameter("p") ) : 0;
        
        String t = "tbl_bodega_activo as BA inner join tbl_activo as A on BA.id_activo=A.id_activo";
        String c = "BA.ubicacion, A.codigo_activo, A.descripcion, A.valor_compra";    
        String w = "where BA.id_bodega="+id_bodega+" and A.eliminado=false and A.id_activo not in (select id_activo from tbl_activo_perdida) order by A.codigo_activo";
        
        
        try {
            String html = "<TABLE class='jm_tabla' cellspacing='1' cellpadding='0' id='tblPE'>";
            Connection con = objBodega.getConexion();
            long numPags = 0;
            long fxp = 100;
            try{
                Statement st = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
                ResultSet rPag = st.executeQuery(objBodega.decodificarURI("SELECT count(*) FROM " + t +" "+ w.replaceAll("order by .*", "")));
                if(rPag.next()){
                    numPags = ( (rPag.getString(1)!=null?rPag.getLong(1):1) - 1 )/fxp;
                    rPag.close();
                }
                
                if(p > numPags){
                    p = 0;
                }
                ResultSet rsEstanterias = st.executeQuery(objBodega.decodificarURI("SELECT "+c+" FROM "+t+" "+w+" LIMIT "+fxp+" OFFSET "+(fxp*p)+";"));
            
                int i=0;
                String ubicacion = "";
                String codigo_activo = "";
                String descripcion = "";
                String valor_compra = "";
                while(rsEstanterias.next()){
                    ubicacion = (rsEstanterias.getString("ubicacion")!=null) ? rsEstanterias.getString("ubicacion") : "";
                    codigo_activo = (rsEstanterias.getString("codigo_activo")!=null) ? rsEstanterias.getString("codigo_activo") : "";
                    descripcion = (rsEstanterias.getString("descripcion")!=null) ? rsEstanterias.getString("descripcion") : "";
                    valor_compra = (rsEstanterias.getString("valor_compra")!=null) ? rsEstanterias.getString("valor_compra") : "";
                    html += "<tr valign='top' class='jm_filaPar' onmouseover=\"this.className='jm_filaSobre'\" onmouseout=\"this.className='jm_filaPar'\">";
                    html += "<td width='136' style='cursor:pointer'>"+ubicacion+"</td>";
                    html += "<td width='256' style='cursor:pointer'>"+codigo_activo+"</td>";
                    html += "<td width='201' style='cursor:pointer'>"+descripcion+"</td>";
                    html += "<td width='71' style='cursor:pointer'>"+valor_compra+"</td>";
                    html += "<td align='center' width='21'>&nbsp;</td></tr>";
                    i++;
                }
                
                rsEstanterias.close();
                st.close();
            }catch(Exception e){
                con.close();
                e.printStackTrace();
            }

            html += "</table>";

            out.print("obj»sl2^fun»_('axd_nav_pags').innerHTML=axNpg="+numPags+";^frm»"+html);

        }catch(Exception e){
            e.printStackTrace();
        } finally {
            objBodega.cerrar();
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
