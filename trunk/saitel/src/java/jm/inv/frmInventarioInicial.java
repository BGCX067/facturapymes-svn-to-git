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

package jm.inv;

import java.io.*;

import javax.servlet.*;
import javax.servlet.http.*;
import java.sql.ResultSet;
import java.util.*;
import jm.web.Fecha;
import jm.adm.clas.Configuracion;
import jm.inv.clas.Producto;
import jm.web.Addons;

/**
 *
 * @author Jorge
 */
public class frmInventarioInicial extends HttpServlet {
    private String _ip = null;
    private int _puerto = 5432;
    private String _db = null;
    private String _dir = null;

    List prod = null;

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
        
        response.setContentType("text/html;charset=UTF-8");
        response.setHeader("Pragma", "no-cache");
        response.setHeader("Expires", "Mon, 01 Jan 2001 00:00:01 GMT");
        response.setHeader("Cache-Control", "no-store");
        response.setHeader("Cache-Control", "must-revalidate");
        response.setHeader("Cache-Control", "no-cache");
        PrintWriter out = response.getWriter();

        String archivoCVS = request.getParameter("arch");
        int altB = Integer.parseInt(request.getParameter("altB"));

        Configuracion conf = new Configuracion(this._ip, this._puerto, this._db, usuario, clave);
        boolean setInventarioInicial = Boolean.valueOf(conf.getValor("setInventarioInicial"));
        conf.cerrar();

        Producto objProducto = new Producto(this._ip, this._puerto, this._db, usuario, clave);
        try {
            try{
                out.println("obj»d_11^foc»c0^frm»"
                    //+ "<form action='frmInventarioInicialGuardar' onsubmit='return inv_invInicialGuardar(this);' autocomplete='off'>"
                    + "<div class='H3' style='width:620px;'>Inventario Inicial</div>"
                    + "<table width='620'><tr><td>Fecha: <input id='f' type='text' readonly='readonly' size='10' maxlength='10' value='"+Fecha.getFecha("ISO")+"' />"
                    + "<input type='button' value='...' onClick=\"SelectorFecha.crear('f', 'ISO');\"/></td>"
                    + "<td align='center'><input type='button' value='Asignar valores desde un archivo CSV' onclick='inv_setArchivoCVS()' /></td>");

                if(setInventarioInicial){
                    out.println("<td align='right'><input type='submit' value='Guardar Inventario Inicial' onclick='inv_invInicialGuardar()' /></td>");
                }
                
                out.println("</tr></table>");

                out.println("<TABLE cellspacing='0' cellpadding='0' width='620'><TR>" +
                            "<TH class='jm_TH' width='110'>C&Oacute;DIGO</TH>" +
                            "<TH class='jm_TH' width='300'>DESCRIPCI&Oacute;N</TH>" +
                            "<TH class='jm_TH' width='60'>CANTIDAD</TH>" +
                            "<TH class='jm_TH' width='70'>COSTO UNITARIO</TH>" +
                            "<TH class='jm_TH' width='70'>COSTO TOTAL</TH></TR></TABLE>");

                int i=0;
                String fil = "jm_filaPar";
                out.println("<DIV style='overflow:auto;width:635px;height:"+(altB-320)+"px;' id='sll'>" +
                        "<TABLE class='jm_tabla' cellspacing='1' cellpadding='0' id='tblI'>");

                //this._dir = "D:/WARES/";
                ResultSet rsProductos = objProducto.getProductos();
                float total=0;
                if(archivoCVS.compareTo("")!=0){
                    try{
                        File archivo = new File(this._dir + archivoCVS);
                        String linea = null;
                        BufferedReader buffer = new BufferedReader(new FileReader(archivo));
                        this.prod = new ArrayList();
                        while ((linea = buffer.readLine()) != null) {
                            this.prod.add(linea);
                        }
                        buffer.close();
                    }catch(Exception e){
                        e.printStackTrace();
                    }
                    
                    String codigo = "";
                    int cant = 0;
                    float v_u = 0;
                    float v_t = 0;
                    while(rsProductos.next()){
                        codigo = (rsProductos.getString("codigo")!=null) ? rsProductos.getString("codigo") : "";
                        String vecProd[] = this.getProducto(codigo);
                        cant = 0;
                        v_u = 0;
                        if(vecProd!=null){
                            if(vecProd.length==3){
                                cant = (vecProd[1].compareTo("")!=0) ? Integer.valueOf(vecProd[1]) : 0;
                                v_u = (vecProd[2].compareTo("")!=0) ? Float.valueOf(vecProd[2].replace(",", ".")) : 0;
                            }
                        }
                        v_t = cant * v_u;
                        total += v_t;
                        
                        fil = (i%2==0) ? "jm_filaPar" : "jm_filaImp";
                        out.print("<tr class='"+fil+"' valign='top'>");
                        out.print("<td width='110'>"+codigo+"</td>");
                        out.print("<td width='300'><input type='hidden' id='id"+i+"' name='id"+i+"' value='"+((rsProductos.getString("id_producto")!=null) ? rsProductos.getString("id_producto") : "")+"' />"
                                    + ((rsProductos.getString("descripcion")!=null) ? rsProductos.getString("descripcion") : "")+"</td>");
                        out.print("<td width='60'><input id='c"+i+"' name='c"+i+"' type='text' size='6' maxlength='8' " +
                                    "onkeypress=\"_numero(event);\" onkeyup=\"inv_setTotal("+i+");\" value='"+cant+"' /></td>");
                        out.print("<td width='70'><input id='vu"+i+"' name='vu"+i+"' type='text' style='text-align:right' size='8' maxlength='13' " +
                                    "onkeypress=\"_evaluar(event, '0123456789.');\" onkeyup=\"inv_setTotal("+i+");\" value='"+v_u+"' /></td>");
                        out.print("<td width='70' id='vt"+i+"' align='right'>"+Addons.redondear(v_t)+"</td></tr>");
                        i++;
                    }

                }else{
                    
                    while(rsProductos.next()){
                        fil = (i%2==0) ? "jm_filaPar" : "jm_filaImp";
                        out.print("<tr class='"+fil+"' valign='top'>");
                        out.print("<td width='110'>"+((rsProductos.getString("codigo")!=null) ? rsProductos.getString("codigo") : "")+"</td>");
                        out.print("<td width='300'><input type='hidden' id='id"+i+"' name='id"+i+"' value='"+((rsProductos.getString("id_producto")!=null) ? rsProductos.getString("id_producto") : "")+"' />"
                                    + ((rsProductos.getString("descripcion")!=null) ? rsProductos.getString("descripcion") : "")+"</td>");
                        out.print("<td width='60'><input id='c"+i+"' name='c"+i+"' type='text' size='6' maxlength='8' " +
                                    "onkeypress=\"_numero(event);\" onkeyup=\"inv_setTotal("+i+");\" /></td>");
                        out.print("<td width='70'><input id='vu"+i+"' name='vu"+i+"' type='text' style='text-align:right' size='8' maxlength='13' " +
                                    "onkeypress=\"_evaluar(event, '0123456789.');\" onkeyup=\"inv_setTotal("+i+");\" /></td>");
                        out.print("<td width='70' id='vt"+i+"' align='right'></td></tr>");
                        i++;
                    }
                }

                out.print("</table></div>");
                out.print("<div style='width:616px;text-align:right'>INVENTARIO: &nbsp; <span id='t'> "+Addons.redondear(total)+"</span></div>");
                
                //out.print("</form>");

                rsProductos.close();
            }catch(Exception e){
                e.printStackTrace();
            }
        } finally {
            objProducto.cerrar();
            out.close();
        }
    }

    public String[] getProducto(String codigo)
    {
        if(this.prod!=null){
            String item = "";
            Iterator it = this.prod.iterator();
            while(it.hasNext()){
                item = (String)it.next();
                if(item.indexOf(codigo)==0){
                    return item.split(";");
                }
            }
        }
        return null;
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
