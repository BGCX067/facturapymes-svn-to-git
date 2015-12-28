/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jm.cnt;

import ec.gob.sri.wsc.DirectorioConfiguracion;
import ec.gob.sri.wsc.EnvioComprobantesWS;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.ResultSet;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import jm.fac.clas.FacturaVenta;

/**
 *
 * @author jorge
 */
public class frmSriEnviarDocs extends HttpServlet {

    private String _ip = null;
    private int _puerto = 5432;
    private String _db = null;
    
    private String _DOCS_ELECTRONICOS = "";
    private String _WSENVIO = "https://cel.sri.gob.ec/comprobantes-electronicos-ws/RecepcionComprobantes?wsdl";

    public void init(ServletConfig config) throws ServletException
    {
        this._ip = config.getServletContext().getInitParameter("_IP");
        this._puerto = Integer.parseInt(config.getServletContext().getInitParameter("_PUERTO"));
        this._db = config.getServletContext().getInitParameter("_DB");
        
        this._DOCS_ELECTRONICOS = config.getServletContext().getInitParameter("_DOCS_ELECTRONICOS");
        this._WSENVIO = config.getServletContext().getInitParameter("_WSENVIO");
    }
    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
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
        response.setHeader("Pragma", "no-cache");
        response.setHeader("Expires", "Mon, 01 Jan 2001 00:00:01 GMT");
        response.setHeader("Cache-Control", "no-store");
        response.setHeader("Cache-Control", "must-revalidate");
        response.setHeader("Cache-Control", "no-cache");
        PrintWriter out = response.getWriter();
        
        String tipo_doc = request.getParameter("td");
        String claves = request.getParameter("pks");
        
        String error = "";
        
        FacturaVenta objFacturaVenta = new FacturaVenta(this._ip, this._puerto, this._db, usuario, clave);
        
        try{
            String tabla = "tbl_factura_venta";
            String numero_doc = "id_factura_venta as id_documento, serie_factura || '-' || num_factura as numero";
            String pk_documento = "id_factura_venta";
            if(tipo_doc.compareTo("r")==0){
                tabla = "tbl_retencion_compra";
                numero_doc = "id_retencion_compra as id_documento, ret_num_serie || '-' || ret_num_retencion as numero";
                pk_documento = "id_retencion_compra";
            }else if(tipo_doc.compareTo("c")==0){
                tabla = "tbl_nota_credito_venta";
                numero_doc = "id_nota_credito_venta as id_documento, serie_nota || '-' || num_nota as numero";
                pk_documento = "id_nota_credito_venta";
            }
            
            try{
                claves = "'" + claves.replace(",", "','") + "'";
                ResultSet rs = objFacturaVenta.consulta("select "+numero_doc+", clave_acceso, documento_xml from "+tabla
                    + " where estado_documento<>'a' and clave_acceso in ("+claves+")");
                while(rs.next()){
                    String clave_acceso = rs.getString("clave_acceso")!=null ? rs.getString("clave_acceso") : "";
                    String numero = rs.getString("numero")!=null ? rs.getString("numero") : "";
                    String id_documento = rs.getString("id_documento")!=null ? rs.getString("id_documento") : "";
                    
                    String rutaArchivoFirmado = DirectorioConfiguracion.getRutaArchivoFirmado();
                    ec.gob.sri.comprobantes.ws.RespuestaSolicitud respuestaRecepcion = new ec.gob.sri.comprobantes.ws.RespuestaSolicitud();
                    File ArchivoXML = new File(rutaArchivoFirmado+ File.separatorChar + clave_acceso + ".xml");

                    respuestaRecepcion = EnvioComprobantesWS.obtenerRespuestaEnvio(ArchivoXML, clave_acceso, this._WSENVIO);
                    String estado = respuestaRecepcion.getEstado();
                    if(estado.equals("RECIBIDA")){
                        objFacturaVenta.ejecutar("update " + tabla + " set estado_documento='r' where clave_acceso='"+clave_acceso+
                                "' and "+pk_documento+"="+id_documento);
                    }else {
                        String respuesta = EnvioComprobantesWS.obtenerMensajeRespuesta(respuestaRecepcion);
                        if (estado.equals("DEVUELTA")) {
                            objFacturaVenta.ejecutar("update " + tabla + " set estado_documento='n', mensaje='"+respuesta.replace("\n", ". ").replace("\r", ". ").replace("\t", " ")+"' where clave_acceso='"+clave_acceso+
                                "' and "+pk_documento+"="+id_documento);
                            error += "Error en documento No. " + numero + ". " + respuesta + "; ";
                        }else{
                            error = estado;
                            break;
                        }
                    }
                }
                rs.close();
            }catch(Exception e){
                error = e.getMessage();
            }
            
            out.print("msg»"+error+"^fun»cnt_autorizacionesBuscar()");
            
        }finally{
            objFacturaVenta.cerrar();
        }
    }

    
    
    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
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
     *
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
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}
