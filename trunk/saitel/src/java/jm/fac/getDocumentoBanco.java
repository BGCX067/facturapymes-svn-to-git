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

package jm.fac;

import java.io.*;
import java.sql.ResultSet;

import javax.servlet.*;
import javax.servlet.http.*;
import jm.fac.clas.DocumentoBanco;

/**
 *
 * @author Jorge
 */
public class getDocumentoBanco extends HttpServlet {
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

        String doc = request.getParameter("doc");
        String foPa = request.getParameter("foPa");
        DocumentoBanco objDocumentoBanco = new DocumentoBanco(this._ip, this._puerto, this._db, usuario, clave);
        try {
            String res = "-1";
            String valor = "0";
            if(foPa.compareTo("n")==0){
                String num_cuenta = request.getParameter("cta");
                float monto = objDocumentoBanco.getDebitosBanco(doc, num_cuenta); // del archivo de debitos del banco
                valor = res = String.valueOf(monto);
            }else{
                float monto = objDocumentoBanco.getDocumentoBanco(doc); // del archivo del banco
                if(monto > 0){
                    if(objDocumentoBanco.hayDocumento(doc)){
                        valor = res = String.valueOf(objDocumentoBanco.getSaldoDocumento(doc));
                    }else{
                        objDocumentoBanco.insertar(usuario, doc, String.valueOf(monto));
                        valor = res = String.valueOf(monto);
                    }
                }else{
                    if(objDocumentoBanco.hayDocumento(doc)){
                        valor = res = String.valueOf(objDocumentoBanco.getSaldoDocumento(doc));
                    }else{
                        res = "-1";
                    }
                }
            }
            
            String html = "";
            try{
                ResultSet rs = objDocumentoBanco.getPagosDocumentoBanco(doc);  
                html += "<table width='500' border='1'><tr><th>FECHA</th><th>CLIENTE</th><th>VALOR</th></tr>";
                while(rs.next()){
                    html += "<tr><td>"+(rs.getString("fecha_actual")!=null?rs.getString("fecha_actual"):"")+"</td>"
                            + "<td>"+(rs.getString("nombre")!=null?rs.getString("nombre"):"")+"</td>"
                            + "<td>"+(rs.getString("valor_son")!=null?rs.getString("valor_son"):"")+"</td></tr>";
                    
                    rs.close();
                }
                html += "</table>";
            }catch(Exception e){
                e.printStackTrace();
            }
            out.print("obj»axDocs^fun»fac_postGetSaldoDoc("+res+", "+valor+")^frm»"+html);

        } finally {
            objDocumentoBanco.cerrar();
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
