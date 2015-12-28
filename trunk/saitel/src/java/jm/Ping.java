/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package jm;

import ec.sai.ws.ComandoService;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.xml.ws.WebServiceRef;
import jm.adm.clas.Sucursal;

/**
 *
 * @author Desarrollo
 */
public class Ping extends HttpServlet {
    @WebServiceRef(wsdlLocation = "WEB-INF/wsdl/localhost_8080/SAITEL_WS/comandoService.wsdl")
    private ComandoService service;
    
    private String _ip = null;
    private int _puerto = 5432;
    private String _db = null;
    
    private String _clave = null;

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
        
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();
        String ip = request.getParameter("ip");
        String obj = request.getParameter("obj");
        String salida = "";

        Sucursal objSucursal = new Sucursal(this._ip, this._puerto, this._db, usuario, clave);
        try {
            String ip_servidor = objSucursal.getIPServidor(ip);
            if(ip_servidor.compareTo("")!=0){
                try {
                    ec.sai.ws.ComandoService service = new ec.sai.ws.ComandoService();
                    ec.sai.ws.Comando port = service.getComandoPort();
                    salida = port.ping(ip);
                } catch (Exception ex) {
                    salida = ex.getMessage();
                }
            }else{
                try {
                    ProcessBuilder pb = new ProcessBuilder("ping", ip);
                    pb.redirectErrorStream(true);
                    Process proceso = pb.start();
                    InputStream is = proceso.getInputStream();
                    InputStreamReader isr = new InputStreamReader(is);
                    BufferedReader br = new BufferedReader(isr);
                    String line;
                    while ((line = br.readLine()) != null) {
                        salida += line + "<br />";
                    }
                }catch(Exception e){
                    salida = e.getMessage();
                    e.printStackTrace();
                }
            }

            out.print("obj»"+obj+"^frm»"+salida);

        }catch(Exception e){
            e.printStackTrace();
        } finally {
            objSucursal.cerrar();
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

    private String ping(java.lang.String ip) {
        ec.sai.ws.Comando port = service.getComandoPort();
        return port.ping(ip);
    }



}
