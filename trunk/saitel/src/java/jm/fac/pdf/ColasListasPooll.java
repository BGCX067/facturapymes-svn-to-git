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

package jm.fac.pdf;

import java.io.*;
import java.sql.ResultSet;

import javax.servlet.*;
import javax.servlet.http.*;
import jm.fac.clas.Instalacion;
import jm.web.Fecha;

/**
 *
 * @author Jorge
 */
public class ColasListasPooll extends HttpServlet {
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
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        HttpSession sesion = request.getSession(true);
        String usuario = (String)sesion.getAttribute("usuario");
        String clave = (String)sesion.getAttribute("clave");
        
        response.setContentType("text/plain;");
        response.setHeader("Content-disposition", "attachment; filename=colas_"+Fecha.getFecha("ISO")+"_"+Fecha.getHora()+".txt;");
        response.setHeader("Pragma", "no-cache");
        response.setHeader("Expires", "Mon, 01 Jan 2001 00:00:01 GMT");
        response.setHeader("Cache-Control", "no-store");
        response.setHeader("Cache-Control", "must-revalidate");
        response.setHeader("Cache-Control", "no-cache");
        PrintWriter out = response.getWriter();
        
        Instalacion objInstalacion = new Instalacion(this._ip, this._puerto, this._db, usuario, clave);
        
        try {
            String idSuc = request.getParameter("idSuc");

            String txt = "/queue simple\n";
            txt += "remove [find]\n";
            
            try{
                String cliente = "";
                String ip = "";
                String burst_limit = "";
                String burst_threshold = "";
                String up_burst_threshold = "";
                String burst_time = "";
                String up_burst_time = "";
                //String limit_at = "";
                String up_limit_at = "";
                String max_limit = "";
                String prioridad = "";
                String plan = "";
                long ancho_resi = 0;
                long ancho_small = 0;
                long ancho_noct = 0;
                long ancho_corp = 0;
                
                
                ResultSet res = objInstalacion.getColasServidorSucursal(idSuc);
                while(res.next()){
                    float ax_burst_limit = res.getString("burst_limit")!=null ? res.getFloat("burst_limit") : 0;
                    plan = res.getString("plan")!=null ? res.getString("plan") : "";
                    if(plan.toUpperCase().indexOf("RESIDENCIAL")>=0){
                        ancho_resi +=  ax_burst_limit;
                    }else if(plan.toUpperCase().indexOf("SMALL")>=0){
                            ancho_small += ax_burst_limit;
                    }else if(plan.toUpperCase().indexOf("CORPORATIVO")>=0){
                            ancho_corp += ax_burst_limit;
                    }else if(plan.toUpperCase().indexOf("NOCTURNO")>=0){
                            ancho_noct += ax_burst_limit;
                    }
                }
                
                ancho_resi = ancho_resi/1024/20;
                ancho_small = ancho_small/1024/4;
                ancho_corp = ancho_corp/1024/2;
                ancho_noct = ancho_noct/1024/20;
                
                /*txt += "add max-limit="+ancho_resi+"M/"+ancho_resi+"M name=\"Residencial Total\" \\\n";
                txt += "priority=2/2 total-priority=2\n";
                txt += "add max-limit="+ancho_small+"M/"+ancho_small+"M name=\"Small Total\" \\\n";
                txt += "priority=2/2 total-priority=2\n";
                txt += "add max-limit="+ancho_noct+"M/"+ancho_noct+"M name=\"Nocturno Total\" \\\n";
                txt += "priority=2/2 total-priority=2\n";
                txt += "add max-limit="+ancho_corp+"M/"+ancho_corp+"M name=\"Corporativo Total\" \\\n";
                txt += "priority=2/2 total-priority=2\n";*/
                
                res.beforeFirst();
                while(res.next()){
                    cliente = res.getString("razon_social")!=null ? res.getString("razon_social") : "";
                    ip = res.getString("ip")!=null ? res.getString("ip") : "";
                    burst_limit = res.getString("burst_limit")!=null ? res.getString("burst_limit") : "";
                    burst_threshold = res.getString("burst_threshold")!=null ? res.getString("burst_threshold")+"k" : "";
                    up_burst_threshold = res.getString("up_burst_threshold")!=null ? res.getString("up_burst_threshold")+"k" : "";
                    burst_time = res.getString("burst_time")!=null ? res.getString("burst_time") : "";
                    up_burst_time = res.getString("up_burst_time")!=null ? res.getString("up_burst_time") : "";
                    //limit_at = res.getString("limit_at")!=null ? res.getString("limit_at")+"k" : "";
                    //up_limit_at = res.getString("up_limit_at")!=null ? res.getString("up_limit_at")+"k" : "";
                    max_limit = res.getString("max_limit")!=null ? res.getString("max_limit") : "";
                    prioridad = res.getString("prioridad")!=null ? res.getString("prioridad") : "";
                    plan = res.getString("plan")!=null ? res.getString("plan") : "";

                    if(plan.toUpperCase().indexOf("CORPORATIVO")>=0){
                        txt += "add max-limit="+max_limit+"k/"+max_limit+"k name=\""+cliente+"\" \\\n";
                        txt += "priority="+prioridad+"/"+prioridad+" target="+ip+"/32 total-priority="+prioridad+"\n";

                    }else if(plan.toUpperCase().indexOf("RESIDENCIAL")>=0){
                        txt += "add max-limit="+burst_limit+"k/"+burst_limit+"k name=\""+cliente+"\" \\\n";
                        txt += "priority="+prioridad+"/"+prioridad+" target="+ip+"/32 total-priority="+prioridad+"\n";
                    }else if(plan.toUpperCase().indexOf("SMALL")>=0){
                        txt += "add max-limit="+burst_limit+"k/"+burst_limit+"k name=\""+cliente+"\" \\\n";
                        txt += "priority="+prioridad+"/"+prioridad+" target="+ip+"/32 total-priority="+prioridad+"\n";
                    }else if(plan.toUpperCase().indexOf("NOCTURNO")>=0){
                        txt += "add max-limit="+burst_limit+"k/"+burst_limit+"k name=\""+cliente+"\" \\\n";
                        txt += "priority="+prioridad+"/"+prioridad+" target="+ip+"/32 total-priority="+prioridad+"\n";
                    }
                    
                }
                res.close();
            }catch(Exception e){
                e.printStackTrace();
            }

            out.print(txt);
        } finally {
            objInstalacion.cerrar();
            out.close();
        }
    }


    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Returns a short description of the servlet.
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}
