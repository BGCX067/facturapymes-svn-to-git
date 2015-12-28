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
import jm.cnt.clas.ComprobanteIngreso;
import jm.web.Fecha;

/**
 *
 * @author Jorge
 */
public class tblChequesCobro extends HttpServlet {
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

        String alto = request.getParameter("altB");
        int _altBody = Integer.parseInt(alto) - 250;
        int i=0;
        
        ComprobanteIngreso objComprobanteIngreso = new ComprobanteIngreso(this._ip, this._puerto, this._db, usuario, clave);
        
        try {
            String html = "obj»d_11^frm»<form action='tblChequesCobrosGuardar' onsubmit='return cnt_chequesBancoGuardar(this)' autocomplete='off'>"
                    + "<input type='hidden' id='hoy' value='"+Fecha.getFecha("SQL")+"' />" +
                    "<TABLE cellspacing='0' cellpadding='0' width='610'>" +
                    "<TR><TH class='jm_TH' width='200'>NOMBRE</TH>" +
                    "<TH class='jm_TH' width='100'>BANCO</TH>" +
                    "<TH class='jm_TH' width='90'>NUM. CHE.</TH><TH class='jm_TH' width='80'>VALOR</TH>" +
                    "<TH class='jm_TH' width='130'>FECH. EFECTIVO</TH></TR></TABLE>";
            html += "<DIV style='overflow:auto;width:625px;height:"+ _altBody +"px;'>" +
                    "<TABLE class='jm_tabla' cellspacing='1' cellpadding='0' id='tblCon'>";

            try{
                ResultSet rsComprobanteIngresos = objComprobanteIngreso.getChequesNoCobrados();
                String fil = "jm_filaPar";
                String num_cheque = "";
                while(rsComprobanteIngresos.next()){
                    fil = (i%2==0) ? "jm_filaPar" : "jm_filaImp";
                    num_cheque = (rsComprobanteIngresos.getString("num_cheque")!=null) ? rsComprobanteIngresos.getString("num_cheque") : "";
                    html += "<tr class='"+fil+"' onmouseover=\"this.className='jm_filaSobre';\" onmouseout=\"this.className='"+fil+"';\">";
                    html += "<td width='200'><input type='hidden' id='idCo"+i+"' name='idCo"+i+"' value='"+((rsComprobanteIngresos.getString("id_comprobante_ingreso")!=null) ? rsComprobanteIngresos.getString("id_comprobante_ingreso") : "")+"' />"+
                            ((rsComprobanteIngresos.getString("nombre")!=null) ? rsComprobanteIngresos.getString("nombre") : "")+"</td>";
                    html += "<td width='101'>"+((rsComprobanteIngresos.getString("banco")!=null) ? rsComprobanteIngresos.getString("banco") : "")+"</td>";
                    html += "<td width='91'><input type='hidden' id='nCh"+i+"' name='nCh"+i+"' value='"+num_cheque+"' />"+num_cheque+"</td>";
                    html += "<td width='81'>"+((rsComprobanteIngresos.getString("valor_son")!=null) ? rsComprobanteIngresos.getString("valor_son") : "")+"</td>";
                    html += "<td width='131'><input id='fe"+i+"' name='fe"+i+"' type='text' size='10' readonly='readonly' onkeypress='_SUP(event)' value=''/>"
                            + "<input type='button' value='...' onClick=\"SelectorFecha.crear('fe"+i+"', 'SQL');\"/></td>";
                    html += "</tr>";
                    i++;
                }
                rsComprobanteIngresos.close();
            }catch(Exception e){
                e.printStackTrace();
            }

            html += "</table></div>";
            if(i>0){
                html += "<input type='submit' value='Confirmar cobros de cheques recibidos' />";
            }

            html += "</form>";

            out.print(html);
        } finally {
            objComprobanteIngreso.cerrar();
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
