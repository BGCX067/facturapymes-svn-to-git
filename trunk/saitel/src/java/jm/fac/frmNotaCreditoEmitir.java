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

//import ec.gov.informatica.firmadigital.FirmaDigital;
import ec.gob.sri.FirmaXadesBes;
import ec.gob.sri.wsc.ArchivoUtils;
import ec.gob.sri.wsc.AutorizacionComprobantesWS;
import ec.gob.sri.wsc.DirectorioConfiguracion;
import ec.gob.sri.wsc.EnvioComprobantesWS;
import java.io.*;
import java.sql.ResultSet;

import javax.servlet.*;
import javax.servlet.http.*;
import jm.adm.clas.Configuracion;
import jm.adm.clas.Sucursal;
import jm.cnt.clas.ClavesContingenciaSRI;
import jm.fac.clas.Cliente;
import jm.fac.clas.NotaCreditoElectronica;
import jm.fac.clas.NotaCreditoVenta;
import jm.seg.clas.Auditoria;
import jm.web.Cadena;
import jm.web.Fecha;

/**
 *
 * @author Jorge
 */
public class frmNotaCreditoEmitir extends HttpServlet {
    private String _ip = null;
    private int _puerto = 5432;
    private String _db = null;
    
    private String _DOCS_ELECTRONICOS = "";
    private String _WSENVIO = "https://cel.sri.gob.ec/comprobantes-electronicos-ws/RecepcionComprobantes?wsdl";
    private String _WSAUTORIZA = "https://cel.sri.gob.ec/comprobantes-electronicos-ws/AutorizacionComprobantes?wsdl";

    public void init(ServletConfig config) throws ServletException
    {
        this._ip = config.getServletContext().getInitParameter("_IP");
        this._puerto = Integer.parseInt(config.getServletContext().getInitParameter("_PUERTO"));
        this._db = config.getServletContext().getInitParameter("_DB");
        
        this._DOCS_ELECTRONICOS = config.getServletContext().getInitParameter("_DOCS_ELECTRONICOS");
    }
    /**
    * Processes requests for both HTTP <code>GET</code> and <code>POST</code> methods.
    * @param request servlet request
    * @param response servlet response
    */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        HttpSession sesion = request.getSession(true);
        int id_sucursal = (Integer)sesion.getAttribute("sucursal");
        int id_punto_emision = (Integer)sesion.getAttribute("id_punto_emision");
        String usuario = (String)sesion.getAttribute("usuario");
        String clave = (String)sesion.getAttribute("clave");

        response.setContentType("text/html;charset=UTF-8");
        response.setHeader("Pragma", "no-cache");
        response.setHeader("Expires", "Mon, 01 Jan 2001 00:00:01 GMT");
        response.setHeader("Cache-Control", "no-store");
        response.setHeader("Cache-Control", "must-revalidate");
        response.setHeader("Cache-Control", "no-cache");
        PrintWriter out = response.getWriter();
        
        Configuracion conf = new Configuracion(this._ip, this._puerto, this._db, usuario, clave);
        String bloqueo_libros = "";
        while(true){
            bloqueo_libros = conf.getValor("bloqueo_libros");
            if(bloqueo_libros.compareTo("false")==0) {
                conf.setValor("bloqueo_libros", "true");
                break;
            }
            try{
                Thread.sleep(500);
            }catch(InterruptedException e){
                e.printStackTrace();
            }
        }
        String clave_certificado = conf.getValor("clave_certificado");
        String ambiente = conf.getValor("ambiente");
        String tipoEmision = conf.getValor("tipo_emision"); // 1=normal    2=Indisponibilidad del sistema
        String ruc_empresa = conf.getValor("ruc");
        String razon_social_empresa = conf.getValor("razon_social");
        String nombre_comercial = conf.getValor("nombre_comercial");
        String num_resolucion = conf.getValor("num_resolucion");
        String oblga_contabilidad = conf.getValor("oblga_contabilidad");
        String dir_matriz = conf.getValor("dir_matriz");
        
        Sucursal objSucursal = new Sucursal(this._ip, this._puerto, this._db, usuario, clave);
        String direccion_sucursal = objSucursal.getDireccion( String.valueOf(id_sucursal) );
        objSucursal.cerrar();
        
        NotaCreditoVenta objNotaCreditoVenta = new NotaCreditoVenta(this._ip, this._puerto, this._db, usuario, clave);

        ClavesContingenciaSRI objClavesSri = new ClavesContingenciaSRI(this._ip, this._puerto, this._db, usuario, clave);
        NotaCreditoElectronica objNCE = new NotaCreditoElectronica();
        String error = "";
        
        try {
            String r = "msg»Ha ocurrido un error inesperado, por favor, vuelva a intentarlo más tarde o " +
                "contáctese con el administrador del sistema para mayor información.";

            //String WHERE = request.getParameter("WHERE");
            String idFactura = request.getParameter("idFactura");
            //String imprimir = request.getParameter("imp");
            //String id_comprobante_ingreso = request.getParameter("idCI");
            String fechaEmisionDocSustento = request.getParameter("fe");
            String doc_electronico = request.getParameter("elect");
            String id_cliente = request.getParameter("idCl");
            String ndSerie = request.getParameter("ncSerie");
            String ndNum = request.getParameter("ncNum");
            String autorizacion = request.getParameter("ncAut");
            String ruc = request.getParameter("ncRuc").toUpperCase();
            String razon_social = request.getParameter("ncRaSo").toUpperCase();
            String fecha_emision = request.getParameter("ncFeEm");
            String comprobante_modifica = request.getParameter("ncCoMo");
            String tipo = request.getParameter("tipo");
            
            String razon_modificacion = request.getParameter("ncRaMo");
            //String valor_modificacion = request.getParameter("ncVaMo");
            
            String subtotal = request.getParameter("ncsubt");
            String subtotal_0 = request.getParameter("ncsubt0");
            String subtotal_2 = request.getParameter("ncsubt2");
            String subtotal_6 = request.getParameter("ncsubt6");
            String descuento = request.getParameter("ncdt");
            String iva_2 = request.getParameter("nciva2");
            
            //String iva_0 = request.getParameter("ncIva0");
            String total = request.getParameter("nctt");
            int tope = Integer.valueOf(request.getParameter("nctope"));

            subtotal_0 = (subtotal_0.compareTo("")!=0) ? subtotal_0 : "0";
            subtotal_6 = (subtotal_6.compareTo("")!=0) ? subtotal_6 : "0";
            descuento = (descuento.compareTo("")!=0) ? descuento : "0";
            iva_2 = (iva_2.compareTo("")!=0) ? iva_2 : "0";
            subtotal_2 = (Float.parseFloat(subtotal_2)>0) ? subtotal_2 : (Float.parseFloat(iva_2)>0 ? subtotal : "0");
            
            
            
            Cliente objCliente = new Cliente(this._ip, this._puerto, this._db, usuario, clave);
            String tipo_documento_cliente = "05";
            try{
                ResultSet rsCliente = objCliente.getCliente(id_cliente);
                if(rsCliente.next()){
                    tipo_documento_cliente = rsCliente.getString("tipo_documento")!=null ? rsCliente.getString("tipo_documento") : "05";
                    rsCliente.close();
                }
            }catch(Exception e){
                e.printStackTrace();
            }finally{
                objCliente.cerrar();
            }
            
            

            String ids_factura_venta_detalle = "";
            String codigos = "";
            String descripciones = "";
            String cantidades = "";
            String preciosUnitarios = "";
            String p_sts = "";
            String descs = "";
            String ivas = "";
            String totales = "";
            for(int i=0; i<=tope; i++){
                if(request.getParameter("chProd"+i)!=null){
                    if(request.getParameter("chProd"+i).compareTo("true")==0){
                        ids_factura_venta_detalle += request.getParameter("idFacVenDet"+i) + ",";
                        codigos += request.getParameter("ncCodigo"+i) + ",";
                        descripciones += request.getParameter("ncDescrip"+i) + ",";
                        cantidades += request.getParameter("ncC"+i) + ",";
                        preciosUnitarios += request.getParameter("ncPu"+i) + ",";
                        p_sts += request.getParameter("ncST"+i) + ",";
                        descs += request.getParameter("ncDes"+i) + ",";
                        ivas += request.getParameter("ncIv"+i) + ",";
                        totales += request.getParameter("ncT"+i) + ",";
                    }
                }
            }

            if(ids_factura_venta_detalle.compareTo("")!=0){
                ids_factura_venta_detalle = ids_factura_venta_detalle.substring(0, ids_factura_venta_detalle.length()-1);
                codigos = codigos.substring(0, codigos.length()-1);
                descripciones = descripciones.substring(0, descripciones.length()-1);
                cantidades = cantidades.substring(0, cantidades.length()-1);
                preciosUnitarios = preciosUnitarios.substring(0, preciosUnitarios.length()-1);
                p_sts = p_sts.substring(0, p_sts.length()-1);
                descs = descs.substring(0, descs.length()-1);
                ivas = ivas.substring(0, ivas.length()-1);
                totales = totales.substring(0, totales.length()-1);
            }

            boolean ok = true;
            String estadoDocumento = "";
            String certificado = this._DOCS_ELECTRONICOS + "certificado.p12";
            String rutaSalida = this._DOCS_ELECTRONICOS + "firmados";
            String claveAcceso = "";
            String autorizacionXml = "";
            String respuestaAutoriz = "";
            if(doc_electronico.compareTo("0")==0){
                
                /*FacturaVenta objFactura = new FacturaVenta(this._ip, this._puerto, this._db, usuario, clave);
                try{
                    ResultSet facturaDetalle = objFactura.getFacturaDetalle(idFactura);
                    while(facturaDetalle.next()){
                        ids_productos += ((facturaDetalle.getString("descripcion_mas")!=null) ? facturaDetalle.getString("descripcion_mas") : "") + ",";
                        descripciones += ((facturaDetalle.getString("descripcion_mas")!=null) ? facturaDetalle.getString("descripcion_mas") : "") + ",";
                        cantidades_prod += ((facturaDetalle.getString("cantidad")!=null) ? facturaDetalle.getString("cantidad") : "") + ",";
                        preciosUnitarios += ((facturaDetalle.getString("p_u")!=null) ? facturaDetalle.getString("p_u") : "") + ",";
                        descuentos += ((facturaDetalle.getString("descuento")!=null) ? facturaDetalle.getString("descuento") : "") + ",";
                        subtotales += ((facturaDetalle.getString("descripcion_mas")!=null) ? facturaDetalle.getString("descripcion_mas") : "") + ",";
                    }
                }catch(Exception e) {
                    
                }finally{
                    objFactura.cerrar();
                }*/
                
                
                String vecModifica[] = comprobante_modifica.replace("Factura No. ", "").split("-");
                comprobante_modifica = vecModifica[0] + "-" + vecModifica[1] + "-" + Cadena.setSecuencial(vecModifica[2]); 
                ok = false;
                String vecSerie[] = ndSerie.split("-");
                claveAcceso = objNCE.getClaveAcceso(Cadena.setFecha(fecha_emision), "04", ruc_empresa, ambiente, vecSerie[0]+vecSerie[1], Cadena.setSecuencial(ndNum), tipoEmision);
                if(tipoEmision.compareTo("2")==0){  //   por indisponibilidad del sistema
                    claveAcceso = objClavesSri.getSigClave(Fecha.getFecha("SQL"), "04", ruc_empresa, ambiente, tipoEmision);
                }
                objNCE.generarXml(claveAcceso, ambiente, tipoEmision, razon_social_empresa, nombre_comercial, ruc_empresa, "04", vecSerie[0], vecSerie[1], 
                        Cadena.setSecuencial(ndNum), dir_matriz, Cadena.setFecha(fecha_emision), direccion_sucursal, tipo_documento_cliente, 
                        razon_social, ruc, num_resolucion, oblga_contabilidad, "01", comprobante_modifica, 
                        fechaEmisionDocSustento, subtotal, total, subtotal_0, subtotal_2, iva_2, (tipo.compareTo("v")==0?"DEVOLUCIÓN":"DESCUENTO"), 
                        codigos, descripciones, cantidades, preciosUnitarios, descs, p_sts, ivas);
                
                String documentoXml = this._DOCS_ELECTRONICOS + "generados/" + claveAcceso + ".xml";
                objNCE.salvar(documentoXml);
                error = objNCE.getError();

                if(error.compareTo("")==0){
                    estadoDocumento = "g";
                    String archivoSalida = claveAcceso + ".xml";
                    FirmaXadesBes firmaDigital = new FirmaXadesBes(certificado, clave_certificado, documentoXml, rutaSalida, archivoSalida);
                    firmaDigital.execute();
                    error = firmaDigital.getError();

                    if(error.compareTo("")==0){
                        estadoDocumento = "f";

                        if(tipoEmision.compareTo("1")==0){  //   emision normal
                            ec.gob.sri.comprobantes.ws.RespuestaSolicitud respuestaRecepcion = new ec.gob.sri.comprobantes.ws.RespuestaSolicitud();
                            try {

                                File ArchivoXML = new File(DirectorioConfiguracion.getRutaArchivoFirmado()+ File.separatorChar + claveAcceso + ".xml");

                                respuestaRecepcion = EnvioComprobantesWS.obtenerRespuestaEnvio(ArchivoXML, claveAcceso, this._WSENVIO);
                                if (respuestaRecepcion.getEstado().equals("RECIBIDA")) {
                                    estadoDocumento = "r";
                                    ok = true;
                                    Thread.currentThread();
                                    Thread.sleep(1 * 1000);

                                    respuestaAutoriz = AutorizacionComprobantesWS.autorizarComprobanteIndividual(claveAcceso, ArchivoXML.getName(), this._WSAUTORIZA);

                                    if (respuestaAutoriz.equals("AUTORIZADO")) {
                                        estadoDocumento = "a";
                                        autorizacionXml = AutorizacionComprobantesWS.getAutorizacionXml();
                                    }else{
                                       if(respuestaAutoriz.contains("RECHAZADO") || respuestaAutoriz.contains("NO AUTORIZADO")){
                                            estadoDocumento = "n";
                                            error = respuestaAutoriz;
                                            ok=false;
                                        }
                                    }

                                    //ArchivoXML.delete();
                                } else if (respuestaRecepcion.getEstado().equals("DEVUELTA")) {
                                    estadoDocumento = "d";
                                    error = EnvioComprobantesWS.obtenerMensajeRespuesta(respuestaRecepcion);

                                    String dirRechazados = DirectorioConfiguracion.getRutaArchivoNoAutorizado() + File.separator + "rechazados";

                                    ArchivoUtils.anadirMotivosRechazo(ArchivoXML, respuestaRecepcion);

                                    File rechazados = new File(dirRechazados);
                                    if (!rechazados.exists()) {
                                        new File(dirRechazados).mkdir();
                                    }

                                    if (!ArchivoUtils.copiarArchivo(ArchivoXML, rechazados.getPath() + File.separator + ArchivoXML.getName())) {
                                        System.out.println("Error al mover archivo a carpeta rechazados");
                                    //} else {
                                        //claveAcceso.delete();
                                    }
                                }else{
                                    error = "Fallo al acceder al WSDL en: https://celcer.sri.gob.ec/comprobantes-electronicos-ws/RecepcionComprobantes?wsdl";
                                }


                            } catch (Exception ex) {
                                //Logger.getLogger(WsClienteSRI.class.getName()).log(Level.SEVERE, null, ex);

                                estadoDocumento = "g";
                                claveAcceso = objClavesSri.getSigClave(Fecha.getFecha("SQL"), "04", ruc_empresa, ambiente, tipoEmision);
                                documentoXml = this._DOCS_ELECTRONICOS + "generados/" + claveAcceso + ".xml";
                                objNCE.salvar(documentoXml);
                                error = objNCE.getError();

                                archivoSalida = claveAcceso + ".xml";
                                firmaDigital = new FirmaXadesBes(certificado, clave_certificado, documentoXml, rutaSalida, archivoSalida);
                                firmaDigital.execute();
                                error = firmaDigital.getError();

                                if(error.compareTo("")==0){
                                    estadoDocumento = "f";
                                    autorizacionXml = this.getStringFromFile(this._DOCS_ELECTRONICOS + "firmados/" + claveAcceso + ".xml");
                                    //objClavesSri.setConsumida(claveAcceso, ambiente);
                                    ok = true;
                                }
                                //error = "Error al tratar de enviar el comprobante hacia el SRI. ";
                            }
                        }else{
                            autorizacionXml = this.getStringFromFile(this._DOCS_ELECTRONICOS + "firmados/" + claveAcceso + ".xml");
                            //objClavesSri.setConsumida(claveAcceso, ambiente);
                            ok = true;
                        }
                    }
                }
            }
            r = "msg»"+error;

            if(ok){
                String id_factura_id_compDiario = "-1:-1";
                if(!objNotaCreditoVenta.NotaDuplicada(ndSerie, ndNum)){
                    id_factura_id_compDiario = objNotaCreditoVenta.insertar(id_sucursal, id_punto_emision, idFactura, usuario, ndSerie,
                            ndNum, autorizacion, ruc, razon_social, fecha_emision, comprobante_modifica, tipo, razon_modificacion,
                            subtotal, subtotal_0, subtotal_2, subtotal_6, descuento, iva_2, total, ids_factura_venta_detalle, 
                            cantidades, p_sts, descs, ivas, totales);
                    if(id_factura_id_compDiario.compareTo("-1:-1") != 0){
                        
                        String vec[] = id_factura_id_compDiario.split(":");
                        objNotaCreditoVenta.setEstadoDocumento(vec[0], estadoDocumento, claveAcceso, autorizacionXml);
                        
                        Auditoria auditoria = new Auditoria(this._ip, this._puerto, this._db, usuario, clave);
                        auditoria.setRegistro(request, "REGISTRO DE NOTA DE CREDITO DE VENTA: "+ndSerie+"-"+ndNum);
                        auditoria.cerrar();
                        
                        r = "err»0^vta»cmp^fun»imprimir('pdfNotaCredito?id="+vec[0]+"');";
                    }else{
                        r = "msg»" + objNotaCreditoVenta.getError();
                    }
                }else{
                    r = "msg»El número de nota de crédito "+ndSerie+"-"+ndNum+" ya ha sido emitida.";
                }
            }
            
            out.print(r);
            
        }catch(Exception e){
            out.print("msg»" + e.getMessage() );
        } finally {
            conf.setValor("bloqueo_libros", "false");
            conf.cerrar();
            objNotaCreditoVenta.cerrar();
            out.close();
        }
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
    
    private byte[] getBytesFromFile(File file) throws IOException {
        InputStream is = new FileInputStream(file);
        long length = file.length();
        byte[] bytes = new byte[(int) length];
        int offset = 0;
        int numRead = 0;
        while (offset < bytes.length && (numRead = is.read(bytes, offset, bytes.length - offset)) >= 0) {
            offset += numRead;
        }
        if (offset < bytes.length) {
            throw new IOException("Could not completely read file " + file.getName());
        }
        is.close();
        return bytes;
    }
    
    private void writeToFile(String name, byte[] data) throws Exception {
        File someFile = new File(name);
        FileOutputStream fos = new FileOutputStream(someFile);
        fos.write(data);
        fos.flush();
        fos.close();
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
