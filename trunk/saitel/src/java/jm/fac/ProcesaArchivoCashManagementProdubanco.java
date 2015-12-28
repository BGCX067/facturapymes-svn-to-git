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
import java.util.ArrayList;
import java.util.List;

import javax.servlet.*;
import javax.servlet.http.*;
import jm.fac.clas.CashManagementPichincha;
import jm.web.Archivo;

/**
 *
 * @author Jorge
 */
public class ProcesaArchivoCashManagementProdubanco extends HttpServlet {
    private String _ip = null;
    private int _puerto = 5432;
    private String _db = null;
    private String _dir = null;

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

        //this._dir = "/home/jorge/";

        Archivo archivo = new Archivo(this._ip, this._puerto, this._db, usuario, clave);
        archivo.setDirectorio(this._dir);
        try {
            String r = "<script language='javascript' type='text/javascript'>window.top.window.inv_finTransferencia('Ha ocurrido un error en el proceso de subida del archivo.', '');</script>";
            if(archivo.subir(request, 30, new String[]{"text/plain", "text/csv", "application/vnd.ms-excel"} )){
                int err = 1;
                String msg = "Ha ocurrido un error en el procesamiento de los datos del archivo. Por favor, vuelva a intentarlo más tarde o " +
                    "contáctese con el administrador del sistema para mayor información.";
                CashManagementPichincha objCash = new CashManagementPichincha(this._ip, this._puerto, this._db, usuario, clave);
                try{
                    List sql = new ArrayList();
                    File file = archivo.getArchivo();
                    String linea = "";
                    String contrapartida = "";
                    String id_sucursal = "";
                    String id_instalacion = "";
                    String periodo = "";
                    String anio = "";
                    String mes = "";
                    String dia = "";
                    String hora = "";
                    String minuto = "";
                    String segundo = "";
                    String fecha_pago = "";
                    String hora_pago = "";
                    BufferedReader buffer = new BufferedReader(new FileReader(file));
                    while ((linea = buffer.readLine()) != null) {
                        linea = linea.replaceAll("  +", "\t");
                        String cols[] = linea.split("\t");
                        if(cols.length > 5){
                            if(cols[5].indexOf("PROCESO OK")>=0){
                                contrapartida = cols[0].substring( 24, cols[0].length() );
                                id_sucursal = contrapartida.substring( 0, 1 );
                                id_instalacion = contrapartida.substring( 1, contrapartida.length() );
                                periodo = cols[4].trim();
                                dia = cols[5].substring( 0, 2 );
                                mes = cols[5].substring( 2, 4);
                                anio = cols[5].substring( 4, 8 );
                                hora = cols[5].substring( 8, 10 );
                                minuto = cols[5].substring( 10, 12 );
                                segundo = cols[5].substring( 12, 14 );
                                fecha_pago = anio + "-" + mes + "-" + dia;
                                hora_pago = hora + ":" + minuto + ":" + segundo;
                                if(!objCash.hayCuenta(id_sucursal, id_instalacion, periodo)){
                                    sql.add("insert into tbl_documento_banco_cash(id_sucursal, id_instalacion, periodo, fecha_pago, hora_pago, banco) "
                                    + "values("+id_sucursal+", "+id_instalacion+", '"+periodo+"', '"+fecha_pago+"', '"+hora_pago+"', 'PRODUBANCO');");
                                }
                            }
                        }
                    }
                    if(objCash.transacciones(sql)){
                        err = 0;
                        msg = "Subida del archivo del banco realizada satisfactoriamente.";
                    }else{
                        err = 1;
                        msg = objCash.getError();
                    }
                    buffer.close();
                }catch(Exception e){
                    err = 1;
                    msg = e.getMessage();
                }finally{
                    objCash.cerrar();
                }
                r = "<script language='javascript' type='text/javascript'>window.top.window.fac_ProdubancoCashBuscar();window.top.window.fac_finTransferencia("+err+", '"+
                        msg.replace("\n", ". ").replace("\t", ". ").replace("'", "").replace("\"", "").replace("(", "").replace(")", "") +"');</script>";
            }else{
                r = "<script language='javascript' type='text/javascript'>window.top.window.fac_finTransferencia(1, '"+archivo.getError()+"');</script>";
            }
            out.print(r);
        }catch(Exception e) {
            e.printStackTrace();
        } finally {
            archivo.cerrar();
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
