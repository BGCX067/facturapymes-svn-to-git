/**
* @version 3.0
* @package YAKUSOFT.
* @author Jorge Washington Mueses Cevallos.
* @copyright Copyright (C) 2010 por Jorge Mueses. Todos los derechos reservados.
* @license http://www.gnu.org/copyleft/gpl.html GNU/GPL.
* YAKUSOFT! es un software de libre distribución, que puede ser
* copiado y distribuido bajo los términos de la Licencia Pública
* General GNU, de acuerdo con la publicada por la Free Software
* Foundation, versión 2 de la licencia o cualquier versión posterior.
*/

package jm.cnt;

import java.io.*;
import java.sql.Connection;

import javax.servlet.*;
import javax.servlet.http.*;
import java.sql.ResultSet;
import java.sql.Statement;
import jm.cnt.clas.LibroMayor;
import jm.web.Addons;
import jm.web.Fecha;

/**
 *
 * @author Jorge
 */
public class tblPagLibroMayor extends HttpServlet {
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
            String id_plan_cuenta = request.getParameter("idPC");
            String fecha_ini = request.getParameter("fi");
            String fecha_fin = request.getParameter("ff");
            String id_sucursal = request.getParameter("idSuc");
            int  p = request.getParameter("p") != null ? Integer.parseInt( request.getParameter("p") ) : 0;
            
            int i=0;
            String fil = "jm_filaPar";
            float sum_debe = 0;
            float sum_haber = 0;
            String html = "";
            long numPags = 0;
            long fxp = 100;
            
            
            LibroMayor objLibroMayor = new LibroMayor(this._ip, this._puerto, this._db, usuario, clave);
            html += "<TABLE class='jm_tabla' cellspacing='1' cellpadding='0' id='tblCon'>";

            try{
                String t = "vta_libro_diario_mayor";
                String c = "fecha_asiento, num_asiento, detalle, debe, haber, saldo_deudor, saldo_acreedor, suc_saldo_deudor, suc_saldo_acreedor";    
                String w = "where id_plan_cuenta="+id_plan_cuenta+" and fecha_asiento between '"+fecha_ini+"' and '"+fecha_fin+"'";
                if(id_sucursal.compareTo("-0")!=0){
                    w = "where id_sucursal="+id_sucursal+" and id_plan_cuenta="+id_plan_cuenta+" and fecha_asiento between '"+fecha_ini+"' and '"+fecha_fin+"'";
               }
        
                Connection con = objLibroMayor.getConexion();
                Statement st = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
                ResultSet rPag = st.executeQuery(objLibroMayor.decodificarURI("SELECT count(*) FROM " + t +" "+ w.replaceAll("order by .*", "")));
                if(rPag.next()){
                    numPags = ( (rPag.getString(1)!=null?rPag.getLong(1):1) - 1 )/fxp;
                    rPag.close();
                }
                
                if(p > numPags){
                    p = 0;
                }
                ResultSet rsLibroMayor = st.executeQuery(objLibroMayor.decodificarURI("SELECT "+c+" FROM "+t+" "+w+" LIMIT "+fxp+" OFFSET "+(fxp*p)+";"));
            
                String debe = "";
                String haber = "";
                String sdebe = "";
                String shaber = "";
                while(rsLibroMayor.next()){
                    fil = (i%2==0) ? "jm_filaPar" : "jm_filaImp";
                    i++;
                    debe = (rsLibroMayor.getString("debe")!=null) ? ((rsLibroMayor.getDouble("debe")>0)?rsLibroMayor.getString("debe"):"") : "";
                    haber = (rsLibroMayor.getString("haber")!=null) ? ((rsLibroMayor.getDouble("haber")>0)?rsLibroMayor.getString("haber"):"") : "";
                    sdebe = (rsLibroMayor.getString("saldo_deudor")!=null) ? ((rsLibroMayor.getDouble("saldo_deudor")!=0)?rsLibroMayor.getString("saldo_deudor"):"") : "";
                    shaber = (rsLibroMayor.getString("saldo_acreedor")!=null) ? ((rsLibroMayor.getDouble("saldo_acreedor")!=0)?rsLibroMayor.getString("saldo_acreedor"):"") : "";
                    if(id_sucursal.compareTo("-0")!=0){
                        sdebe = (rsLibroMayor.getString("suc_saldo_deudor")!=null) ? ((rsLibroMayor.getDouble("suc_saldo_deudor")!=0)?rsLibroMayor.getString("suc_saldo_deudor"):"") : "";
                        shaber = (rsLibroMayor.getString("suc_saldo_acreedor")!=null) ? ((rsLibroMayor.getDouble("suc_saldo_acreedor")!=0)?rsLibroMayor.getString("suc_saldo_acreedor"):"") : "";
                    }
                    html += "<tr class='"+fil+"' onmouseover=\"this.className='jm_filaSobre';\" onmouseout=\"this.className='"+fil+"';\">";
                    html += "<td width='75'>"+((rsLibroMayor.getString("fecha_asiento")!=null) ? Fecha.ISOaSQL(rsLibroMayor.getString("fecha_asiento")) : "")+"</td>";
                    html += "<td width='56'>"+((rsLibroMayor.getString("num_asiento")!=null) ? rsLibroMayor.getString("num_asiento") : "")+"</td>";
                    html += "<td width='251'>"+((rsLibroMayor.getString("detalle")!=null) ? rsLibroMayor.getString("detalle") : "")+"</td>";
                    html += "<td align='right' width='81'>"+debe+"</td>";
                    html += "<td align='right' width='81'>"+haber+"</td>";
                    html += "<td align='right' width='81'>"+sdebe+"</td>";
                    html += "<td align='right' width='81'>"+shaber+"</td>";
                    html += "</tr>";
                    sum_debe += debe.compareTo("")!=0 ? Float.parseFloat(debe) : 0;
                    sum_haber += haber.compareTo("")!=0 ? Float.parseFloat(haber) : 0;
                }
                fil = (i%2==0) ? "jm_filaPar" : "jm_filaImp";
                html += "<tr class='"+fil+"' style='height:20px' onmouseover=\"this.className='jm_filaSobre';\" onmouseout=\"this.className='"+fil+"';\">";
                html += "<td colspan='3' width='382'> &nbsp; S U M A S </td>";
                html += "<td align='right' width='81'>"+Addons.redondear(sum_debe)+"</td>";
                html += "<td align='right' width='81'>"+Addons.redondear(sum_haber)+"</td>";
                html += "<td colspan='2' width='162'>&nbsp;</td>";
                html += "</tr>";

                rsLibroMayor.close();
            }catch(Exception e){
                e.printStackTrace();
            }finally{
                objLibroMayor.cerrar();
            }

            html += "</table>";

            out.print("obj»axDatMay^fun»_('axd_nav_pags').innerHTML=axNpg="+numPags+";^frm»"+html);
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
