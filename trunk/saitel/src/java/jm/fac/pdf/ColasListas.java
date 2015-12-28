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
public class ColasListas extends HttpServlet {
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
                
                ResultSet res = objInstalacion.getColasServidorSucursal(idSuc);
                while(res.next()){
                    cliente = res.getString("razon_social")!=null ? res.getString("razon_social") : "";
                    ip = res.getString("ip")!=null ? res.getString("ip") : "";
                    burst_limit = res.getString("burst_limit")!=null ? res.getString("burst_limit")+"k" : "";
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
                        //burst_limit = limit_at = up_limit_at = "unlimited";
                        //burst_threshold = up_burst_threshold = "unlimited";
                        //burst_time = up_burst_time = "0";
                        //prioridad = "3";
                    }else{
                        txt += "add burst-limit="+burst_limit+"/"+burst_limit+" burst-threshold="+burst_threshold+"/"+up_burst_threshold+" burst-time="+burst_time+"s/"+up_burst_time+"s \\\n";
                        txt += "max-limit="+max_limit+"k/"+max_limit+"k name=\""+cliente+"\" \\\n";
                        txt += "priority="+prioridad+"/"+prioridad+" target="+ip+"/32 total-priority="+prioridad+"\n";
                    }
                    /*if(plan.indexOf("nocturno")>=0){
                        txt += "add address="+ip+" comment=\""+cliente+"\" \\\nlist=nocturnos\n";
                    }*/
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
