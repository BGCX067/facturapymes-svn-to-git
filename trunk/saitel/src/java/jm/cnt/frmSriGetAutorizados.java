/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jm.cnt;

import ec.gob.sri.wsc.AutorizacionComprobantesWS;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.ResultSet;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import jm.web.Correo;
import jm.web.DataBase;
import jm.web.Xml;

/**
 *
 * @author jorge
 */
public class frmSriGetAutorizados extends HttpServlet {

    private String _ip = null;
    private int _puerto = 5432;
    private String _db = null;
    
    //private String _DOCS_ELECTRONICOS = "";
    private String _WSAUTORIZA = "https://cel.sri.gob.ec/comprobantes-electronicos-ws/AutorizacionComprobantes?wsdl";
    private String _SVR_MAIL = "";

    public void init(ServletConfig config) throws ServletException
    {
        this._ip = config.getServletContext().getInitParameter("_IP");
        this._puerto = Integer.parseInt(config.getServletContext().getInitParameter("_PUERTO"));
        this._db = config.getServletContext().getInitParameter("_DB");
        
        //this._DOCS_ELECTRONICOS = config.getServletContext().getInitParameter("_DOCS_ELECTRONICOS");
        this._WSAUTORIZA = config.getServletContext().getInitParameter("_WSAUTORIZA");
        this._SVR_MAIL = config.getServletContext().getInitParameter("_SVR_MAIL");
    }
    /**
     * Processes requests for both HTTP <code>GET</code> andThread.currentThread();
                        Thread.sleep(1 * 1000);
 <code>POST</code>
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
        
        DataBase objDataBase = new DataBase(this._ip, this._puerto, this._db, usuario, clave);
        
        String autorizacionXml = "";
        try{
            claves = "'" + claves.replace(",", "','") + "'";
            String documento = "Factura";
            String tabla = "tbl_factura_venta";
            String sql = "select id_factura_venta as id, serie_factura || '-' || num_factura as numero, vendedor as alias, clave_acceso "
                    + "from tbl_factura_venta where estado_documento='r' and clave_acceso in ("+claves+")";
            if(tipo_doc.compareTo("r")==0){
                documento = "Retenci&oacute;n";
                tabla = "tbl_retencion_compra";
                sql = "select id_retencion_compra as id, ret_num_serie || '-' || ret_num_retencion as numero, usuario as alias, clave_acceso "
                        + "from tbl_retencion_compra where estado_documento='r' and clave_acceso in ("+claves+")";
            }else if(tipo_doc.compareTo("c")==0){
                documento = "Nota de Cr&eacute;dito";
                tabla = "tbl_nota_credito_venta";
                sql = "select id_nota_credito_venta as id, serie_nota || '-' || num_nota as numero, usuario as alias, clave_acceso "
                        + "from tbl_nota_credito_venta where estado_documento='r' and clave_acceso in ("+claves+")";
            }
            
            try{
                ResultSet rs = objDataBase.consulta(sql);
                
                while(rs.next()){
                    String id_factura_retencion_nota = rs.getString("id")!=null ? rs.getString("id") : "";
                    String numero = rs.getString("numero")!=null ? rs.getString("numero") : "";
                    String alias = rs.getString("alias")!=null ? rs.getString("alias") : "";
                    String clave_acceso = rs.getString("clave_acceso")!=null ? rs.getString("clave_acceso") : "";
                    
                    String respuestaAutoriz = "";
                    //File ArchivoXML = new File(DirectorioConfiguracion.getRutaArchivoFirmado()+ File.separatorChar + clave_acceso + ".xml");
                    respuestaAutoriz = AutorizacionComprobantesWS.autorizarComprobanteIndividual(clave_acceso, clave_acceso + ".xml", this._WSAUTORIZA);

                    if (respuestaAutoriz.equals("AUTORIZADO")) {
                        autorizacionXml = AutorizacionComprobantesWS.getAutorizacionXml();
                        // obtengo en numero de autorizacion
                        Xml xml = new Xml();
                        xml.SetXml(autorizacionXml);
                        String numAutorizacion = xml.getValor("numeroAutorizacion");
                        objDataBase.ejecutar("update " + tabla + " set estado_documento='a', documento_xml='"+autorizacionXml+
                                "', numero_autorizacion='"+numAutorizacion+"', mensaje=null where clave_acceso='"+clave_acceso+"'");
                        
                        /*try{
                            prueba producto = new prueba_Impl();
                            pruebaPortType puerto = producto.getProductoPort();
                            //ip = puerto.obtener_ip(mac);
                        }catch(Exception e){
                            //msg = e.getMessage();
                        }*/
                        
                    }else{
                        if(respuestaAutoriz.contains("RECHAZADO") || respuestaAutoriz.contains("NO AUTORIZADO")){
                            objDataBase.ejecutar("update " + tabla + " set estado_documento='n', mensaje='"+
                                    respuestaAutoriz.replace("|", ".").replace("\n", " ").replace("\r", " ").replace("\t", " ")+"' where clave_acceso='"+clave_acceso+"'");
                            // anulacion del documento
                            /*if(tipo_doc.compareTo("f")==0){
                                try{
                                    ResultSet rs1 = objDataBase.consulta("select de_activo from tbl_factura_venta where id_factura_venta=" + id_factura_retencion_nota);
                                    if(rs1.next()){
                                        String de_activo = rs1.getString("de_activo")!=null ? rs1.getString("de_activo") : "";
                                        if(de_activo.compareTo("f")==0){
                                            objDataBase.consulta("select proc_anularFacturaVenta("+id_factura_retencion_nota+");");
                                        }
                                        if(de_activo.compareTo("t")==0){
                                            objDataBase.consulta("select proc_anularFacturaVentaActivo("+id_factura_retencion_nota+");");
                                        }
                                        rs1.close();
                                    }
                                }catch(Exception e){}
                            }*/
                            
                            String empleado[] = this.getEmpleado(objDataBase, alias);
                            StringBuilder mensaje = new StringBuilder();
                            mensaje.append("Estimado(a) ");
                            mensaje.append(empleado[0]);
                            mensaje.append("<br /><br />Se ha encontrado un inconveniente en la "+documento+" No. "+numero+". El SRI ha devuelto el siguiente mensaje de error: <br />");
                            mensaje.append(respuestaAutoriz);
                            mensaje.append(".<br/>Favor de corregir el error y vuelva a enviar el comprobante al SRI.");
                            mensaje.append("<br/><br/>Att.<br/>ASESOR VIRTUAL <br />SAITEL");
                            Correo.enviar(this._SVR_MAIL, "info-tributaria@saitel.ec", empleado[1], "contabilidad@saitel.ec", "sistemas@saitel.ec", "NOTIFICACION DE NO AUTORIZACION", mensaje, true);
                            
                        }
                        error += respuestaAutoriz + ";";
                    }
                }
                rs.close();
            }catch(Exception e){
                error = e.getMessage();
            }
            
            out.print("msg»"+error+"^fun»cnt_autorizacionesBuscar()");
            
        }finally{
            objDataBase.cerrar();
        }
    }
    
    private String[] getEmpleado(DataBase obj, String alias)
    {
        String res[] = new String[]{"Empleado","sistemas@saitel.ec"};
        try{
            ResultSet rs = obj.consulta("select nombre || ' ' || apellido as empleado, email from tbl_empleado where alias='"+alias+"'");
            if(rs.next()){
                res[0] = rs.getString("empleado")!=null ? rs.getString("empleado") : "";
                res[1] = rs.getString("email")!=null ? rs.getString("email") : "sistemas@saitel.ec";
                rs.close();
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return res;
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
