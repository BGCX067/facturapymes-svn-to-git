/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jm.cnt;

import ec.gob.sri.FirmaXadesBes;
import ec.gob.sri.wsc.DirectorioConfiguracion;
import ec.gob.sri.wsc.EnvioComprobantesWS;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.ResultSet;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import jm.adm.clas.Configuracion;
import jm.adm.clas.Sucursal;
import jm.cnt.clas.ClavesContingenciaSRI;
import jm.fac.clas.FacturaElectronica;
import jm.fac.clas.FacturaVenta;
import jm.web.Cadena;
import jm.web.Matriz;

/**
 *
 * @author jorge
 */
public class frmSriSetClavesEnviarDocs extends HttpServlet {

    private String _ip = null;
    private int _puerto = 5432;
    private String _db = null;
    
    private String _DOCS_ELECTRONICOS = "";
    private String _WSENVIO = "https://cel.sri.gob.ec/comprobantes-electronicos-ws/RecepcionComprobantes?wsdl";
    String certificado = "";
    String rutaSalida = "";
    
    private String clave_certificado = "";
    private String ambiente = "";
    private String tipoEmision = "2";
    private String ruc_empresa = "";
    private String razon_social_empresa = "";
    private String nombre_comercial = "";
    private String num_resolucion = "";
    private String oblga_contabilidad = "";
    private String dir_matriz = "";
    
    FacturaVenta objFacturaVenta = null;
    ClavesContingenciaSRI objClavesSri = null;

    public void init(ServletConfig config) throws ServletException
    {
        this._ip = config.getServletContext().getInitParameter("_IP");
        this._puerto = Integer.parseInt(config.getServletContext().getInitParameter("_PUERTO"));
        this._db = config.getServletContext().getInitParameter("_DB");
        
        this._DOCS_ELECTRONICOS = config.getServletContext().getInitParameter("_DOCS_ELECTRONICOS");
        this._WSENVIO = config.getServletContext().getInitParameter("_WSENVIO");
        
        this.certificado = this._DOCS_ELECTRONICOS + "certificado.p12";
        this.rutaSalida = this._DOCS_ELECTRONICOS + "firmados";
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
        
        
        Configuracion conf = new Configuracion(this._ip, this._puerto, this._db, usuario, clave);
        this.clave_certificado = conf.getValor("clave_certificado");
        this.ambiente = conf.getValor("ambiente");
        this.tipoEmision = conf.getValor("tipo_emision"); // 1=normal    2=Indisponibilidad del sistema
        this.ruc_empresa = conf.getValor("ruc");
        this.razon_social_empresa = conf.getValor("razon_social");
        this.nombre_comercial = conf.getValor("nombre_comercial");
        this.num_resolucion = conf.getValor("num_resolucion");
        this.oblga_contabilidad = conf.getValor("oblga_contabilidad");
        this.dir_matriz = conf.getValor("dir_matriz");
        
        Sucursal objSucursal = new Sucursal(this._ip, this._puerto, this._db, usuario, clave);
        ResultSet rsDirSucursales = objSucursal.getDirecciones();
        String matDirSucursales[][] = Matriz.ResultSetAMatriz(rsDirSucursales);
        
        this.objClavesSri = new ClavesContingenciaSRI(this._ip, this._puerto, this._db, usuario, clave);
        
        this.objFacturaVenta = new FacturaVenta(this._ip, this._puerto, this._db, usuario, clave);
        
        try{
            claves = "'" + claves.replace(",", "','") + "'";
            ResultSet rs = null;
            String tabla = "tbl_factura_venta";
            if(tipo_doc.compareTo("f")==0){
                rs = this.setClavesFacturas(claves, matDirSucursales);
            }else if(tipo_doc.compareTo("r")==0){ 
                tabla = "tbl_retencion_compra";
                //rs = this.setClavesRetenciones(claves, matDirSucursales);
            }else if(tipo_doc.compareTo("c")==0){
                tabla = "tbl_nota_credito_venta";
                //rs = this.setClavesFacturas(claves, matDirSucursales);
            }
            
            try{
                while(rs.next()){
                    String clave_acceso = rs.getString("clave_acceso")!=null ? rs.getString("clave_acceso") : "";
                    String numero = rs.getString("numero")!=null ? rs.getString("numero") : "";
                    
                    String rutaArchivoFirmado = DirectorioConfiguracion.getRutaArchivoFirmado();
                    ec.gob.sri.comprobantes.ws.RespuestaSolicitud respuestaRecepcion = new ec.gob.sri.comprobantes.ws.RespuestaSolicitud();
                    File ArchivoXML = new File(rutaArchivoFirmado+ File.separatorChar + clave_acceso + ".xml");

                    respuestaRecepcion = EnvioComprobantesWS.obtenerRespuestaEnvio(ArchivoXML, clave_acceso, this._WSENVIO);
                    String estado = respuestaRecepcion.getEstado();
                    if(estado.equals("RECIBIDA")){
                        this.objFacturaVenta.ejecutar("update " + tabla + " set estado_documento='r' where clave_acceso='"+clave_acceso+"'");
                    }else{ 
                        String respuesta = EnvioComprobantesWS.obtenerMensajeRespuesta(respuestaRecepcion);
                        objFacturaVenta.ejecutar("update " + tabla + " set estado_documento='n', mensaje='"+respuesta.replace("\n", ". ").replace("\r", ". ").replace("\t", " ")+
                                "' where clave_acceso='"+clave_acceso+"'");
                        if (estado.equals("DEVUELTA")) {
                            error += "Error en documento No. " + numero + ". " + EnvioComprobantesWS.obtenerMensajeRespuesta(respuestaRecepcion) + "; ";
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
            objSucursal.cerrar();
            conf.cerrar();
            this.objClavesSri.cerrar();
            this.objFacturaVenta.cerrar();
        }
    }

    public ResultSet setClavesFacturas(String claves, String matDirSucursales[][])
    {
        String ids_factura_venta = "";
        FacturaElectronica objFE = new FacturaElectronica();
        
        try{
            ResultSet rs = this.objFacturaVenta.consulta("select * from vta_factura_venta where estado_documento<>'a' and clave_acceso in ("+claves+")");
            while(rs.next()){
                String id_factura_venta = rs.getString("id_factura_venta")!=null ? rs.getString("id_factura_venta") : "0";
                String id_sucursal = rs.getString("txt_sucursal")!=null ? rs.getString("id_sucursal") : "0";
                String serie_factura = rs.getString("serie_factura")!=null ? rs.getString("serie_factura") : "000-000";
                String num_factura = rs.getString("num_factura")!=null ? rs.getString("num_factura") : "0";
                String fecha_emision = rs.getString("fecha_emision")!=null ? rs.getString("fecha_emision") : "";
                String tipo_documento_cliente = rs.getString("tipo_documento")!=null ? rs.getString("tipo_documento") : "05";
                String razon_social = rs.getString("razon_social")!=null ? rs.getString("razon_social") : "";
                String direccion = rs.getString("direccion")!=null ? rs.getString("direccion") : "";
                String ruc = rs.getString("ruc")!=null ? rs.getString("ruc") : "";
                String subtotal = rs.getString("subtotal")!=null ? rs.getString("subtotal") : "0";
                String subtotal_2 = rs.getString("subtotal_2")!=null ? rs.getString("subtotal_2") : "0";
                String subtotal_0 = rs.getString("subtotal_0")!=null ? rs.getString("subtotal_0") : "0";
                String descuento = rs.getString("descuento")!=null ? rs.getString("descuento") : "0";
                String iva_2 = rs.getString("iva_2")!=null ? rs.getString("iva_2") : "0";
                String total = rs.getString("total")!=null ? rs.getString("total") : "0";
                
                ids_factura_venta += id_factura_venta + ",";
                
                String ids_productos = "";
                String descripciones = "";
                String cantidades_prod = "";
                String preciosUnitarios = "";
                String descuentos = "";
                String subtotales = "";
                String ivas = "";
                try{
                    ResultSet rs2 = this.objFacturaVenta.getFacturaDetalle(id_factura_venta);
                    while(rs2.next()){
                        ids_productos += rs2.getString("id_producto")!=null ? rs2.getString("id_producto")+"," : "";
                        descripciones += rs2.getString("descripcion_mas")!=null ? rs2.getString("descripcion_mas")+"," : "";
                        cantidades_prod += rs2.getString("cantidad")!=null ? rs2.getString("cantidad")+"," : "";
                        preciosUnitarios += rs2.getString("p_u")!=null ? rs2.getString("p_u")+"," : "";
                        descuentos += rs2.getString("descuento")!=null ? rs2.getString("descuento")+"," : "";
                        subtotales += rs2.getString("p_st")!=null ? rs2.getString("p_st")+"," : "";
                        ivas += rs2.getString("iva")!=null ? rs2.getString("iva")+"," : "";
                    }
                    if(ids_productos.compareTo("")!=0){
                        ids_productos = ids_productos.substring(0, ids_productos.length()-1);
                        descripciones = descripciones.substring(0, descripciones.length()-1);
                        cantidades_prod = cantidades_prod.substring(0, cantidades_prod.length()-1);
                        preciosUnitarios = preciosUnitarios.substring(0, preciosUnitarios.length()-1);
                        descuentos = descuentos.substring(0, descuentos.length()-1);
                        subtotales = subtotales.substring(0, subtotales.length()-1);
                        ivas = ivas.substring(0, ivas.length()-1);
                    }
                }catch(Exception ex){
                    ex.printStackTrace();
                }
                
                objFE.limpiar();
            
                String vecSerie[] = serie_factura.split("-");
                String claveAcceso = objFE.getClaveAcceso(Cadena.setFecha(fecha_emision), "01", ruc_empresa, ambiente, vecSerie[0]+vecSerie[1], Cadena.setSecuencial(num_factura), "1");
                if(tipoEmision.compareTo("2")==0){  //   por indisponibilidad del sistema
                    claveAcceso = this.objClavesSri.getSigClave(Cadena.setFecha(fecha_emision), "01", ruc_empresa, ambiente, tipoEmision);
                }
                
                String direccion_sucursal = dir_matriz;
                int pos = Matriz.enMatriz(matDirSucursales, id_sucursal, 0);
                if(pos>=0){
                    direccion_sucursal = matDirSucursales[pos][1];
                }
                
                objFE.generarXml(claveAcceso, ambiente, tipoEmision, razon_social_empresa, nombre_comercial, ruc_empresa, "01", vecSerie[0], vecSerie[1], 
                        Cadena.setSecuencial(num_factura), dir_matriz, Cadena.setFecha(fecha_emision), direccion_sucursal, num_resolucion, oblga_contabilidad, 
                        tipo_documento_cliente, razon_social, ruc, subtotal, descuento, subtotal_0, subtotal_2, iva_2, total, 
                        ids_productos, descripciones, cantidades_prod, preciosUnitarios, descuentos, subtotales, ivas, direccion);
                String documentoXml = this._DOCS_ELECTRONICOS + "generados/" + claveAcceso + ".xml";
                objFE.salvar(documentoXml);
                String error = objFE.getError();
                
                if(error.compareTo("")==0){
                    String archivoSalida = claveAcceso + ".xml";
                    FirmaXadesBes firmaDigital = new FirmaXadesBes(certificado, clave_certificado, documentoXml, rutaSalida, archivoSalida);
                    firmaDigital.execute();
                    error = firmaDigital.getError();

                    if(error.compareTo("")==0){
                        String autorizacionXml = this.getStringFromFile(this._DOCS_ELECTRONICOS + "firmados/" + claveAcceso + ".xml");
                        this.objFacturaVenta.setClaveDocumento(id_factura_venta, "f", claveAcceso, autorizacionXml);
                    }
                }
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        
        String sql = "select serie_factura || '-' || num_factura as numero, clave_acceso, documento_xml "
                + "from tbl_factura_venta "
                + "where estado_documento<>'a' and id_factura_venta in ("+ids_factura_venta.substring(0, ids_factura_venta.length()-1)+")";
        
        return this.objFacturaVenta.consulta(sql);
    }
    
    public ResultSet setClavesRetenciones(String claves, String matDirSucursales[][])
    {
        String ids_factura_venta = "";
        FacturaElectronica objFE = new FacturaElectronica();
        
        try{
            ResultSet rs = this.objFacturaVenta.consulta("select * from vta_factura_venta where estado_documento<>'a' and clave_acceso in ("+claves+")");
            while(rs.next()){
                String id_factura_venta = rs.getString("id_factura_venta")!=null ? rs.getString("id_factura_venta") : "0";
                String id_sucursal = rs.getString("txt_sucursal")!=null ? rs.getString("id_sucursal") : "0";
                String serie_factura = rs.getString("serie_factura")!=null ? rs.getString("serie_factura") : "000-000";
                String num_factura = rs.getString("num_factura")!=null ? rs.getString("num_factura") : "0";
                String fecha_emision = rs.getString("fecha_emision")!=null ? rs.getString("fecha_emision") : "";
                String tipo_documento_cliente = rs.getString("tipo_documento")!=null ? rs.getString("tipo_documento") : "05";
                String razon_social = rs.getString("razon_social")!=null ? rs.getString("razon_social") : "";
                String direccion = rs.getString("direccion")!=null ? rs.getString("direccion") : "";
                String ruc = rs.getString("ruc")!=null ? rs.getString("ruc") : "";
                String subtotal = rs.getString("subtotal")!=null ? rs.getString("subtotal") : "0";
                String subtotal_2 = rs.getString("subtotal_2")!=null ? rs.getString("subtotal_2") : "0";
                String subtotal_0 = rs.getString("subtotal_0")!=null ? rs.getString("subtotal_0") : "0";
                String descuento = rs.getString("descuento")!=null ? rs.getString("descuento") : "0";
                String iva_2 = rs.getString("iva_2")!=null ? rs.getString("iva_2") : "0";
                String total = rs.getString("total")!=null ? rs.getString("total") : "0";
                
                ids_factura_venta += id_factura_venta + ",";
                
                String ids_productos = "";
                String descripciones = "";
                String cantidades_prod = "";
                String preciosUnitarios = "";
                String descuentos = "";
                String subtotales = "";
                String ivas = "";
                try{
                    ResultSet rs2 = this.objFacturaVenta.getFacturaDetalle(id_factura_venta);
                    while(rs2.next()){
                        ids_productos += rs2.getString("id_producto")!=null ? rs2.getString("id_producto")+"," : "";
                        descripciones += rs2.getString("descripcion_mas")!=null ? rs2.getString("descripcion_mas")+"," : "";
                        cantidades_prod += rs2.getString("cantidad")!=null ? rs2.getString("cantidad")+"," : "";
                        preciosUnitarios += rs2.getString("p_u")!=null ? rs2.getString("p_u")+"," : "";
                        descuentos += rs2.getString("descuento")!=null ? rs2.getString("descuento")+"," : "";
                        subtotales += rs2.getString("p_st")!=null ? rs2.getString("p_st")+"," : "";
                        ivas += rs2.getString("iva")!=null ? rs2.getString("iva")+"," : "";
                    }
                    if(ids_productos.compareTo("")!=0){
                        ids_productos = ids_productos.substring(0, ids_productos.length()-1);
                        descripciones = descripciones.substring(0, descripciones.length()-1);
                        cantidades_prod = cantidades_prod.substring(0, cantidades_prod.length()-1);
                        preciosUnitarios = preciosUnitarios.substring(0, preciosUnitarios.length()-1);
                        descuentos = descuentos.substring(0, descuentos.length()-1);
                        subtotales = subtotales.substring(0, subtotales.length()-1);
                        ivas = ivas.substring(0, ivas.length()-1);
                    }
                }catch(Exception ex){
                    ex.printStackTrace();
                }
                
            
                String vecSerie[] = serie_factura.split("-");
                String claveAcceso = objFE.getClaveAcceso(Cadena.setFecha(fecha_emision), "01", ruc_empresa, ambiente, vecSerie[0]+vecSerie[1], Cadena.setSecuencial(num_factura), "1");
                if(tipoEmision.compareTo("2")==0){  //   por indisponibilidad del sistema
                    claveAcceso = this.objClavesSri.getSigClave(Cadena.setFecha(fecha_emision), "01", ruc_empresa, ambiente, tipoEmision);
                }
                
                String direccion_sucursal = dir_matriz;
                int pos = Matriz.enMatriz(matDirSucursales, id_sucursal, 0);
                if(pos>=0){
                    direccion_sucursal = matDirSucursales[pos][1];
                }
                
                objFE.generarXml(claveAcceso, ambiente, tipoEmision, razon_social_empresa, nombre_comercial, ruc_empresa, "01", vecSerie[0], vecSerie[1], 
                        Cadena.setSecuencial(num_factura), dir_matriz, Cadena.setFecha(fecha_emision), direccion_sucursal, num_resolucion, oblga_contabilidad, 
                        tipo_documento_cliente, razon_social, ruc, subtotal, descuento, subtotal_0, subtotal_2, iva_2, total, 
                        ids_productos, descripciones, cantidades_prod, preciosUnitarios, descuentos, subtotales, ivas, direccion);
                String documentoXml = this._DOCS_ELECTRONICOS + "generados/" + claveAcceso + ".xml";
                objFE.salvar(documentoXml);
                String error = objFE.getError();
                
                if(error.compareTo("")==0){
                    String archivoSalida = claveAcceso + ".xml";
                    FirmaXadesBes firmaDigital = new FirmaXadesBes(certificado, clave_certificado, documentoXml, rutaSalida, archivoSalida);
                    firmaDigital.execute();
                    error = firmaDigital.getError();

                    if(error.compareTo("")==0){
                        String autorizacionXml = this.getStringFromFile(this._DOCS_ELECTRONICOS + "firmados/" + claveAcceso + ".xml");
                        this.objFacturaVenta.setClaveDocumento(id_factura_venta, "f", claveAcceso, autorizacionXml);
                    }
                }
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        
        String sql = "select serie_factura || '-' || num_factura as numero, clave_acceso, documento_xml "
                + "from tbl_factura_venta "
                + "where estado_documento<>'a' and id_factura_venta in ("+ids_factura_venta.substring(0, ids_factura_venta.length()-1)+")";
        
        return this.objFacturaVenta.consulta(sql);
    }
    
    
    
    
    
    private String getStringFromFile(String archivo) throws IOException 
    {
        File file = new File(archivo);
        FileReader fr = new FileReader (file);
        BufferedReader br = new BufferedReader(fr);
        StringBuilder cadXml = new StringBuilder();
        String linea;
        while((linea=br.readLine())!=null){
            cadXml.append(linea);
        }
        return cadXml.toString();
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
