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

import ec.gob.sri.FirmaXadesBes;
import ec.gob.sri.wsc.ArchivoUtils;
import ec.gob.sri.wsc.AutorizacionComprobantesWS;
import ec.gob.sri.wsc.DirectorioConfiguracion;
import ec.gob.sri.wsc.EnvioComprobantesWS;
import java.io.*;
import java.sql.ResultSet;
import java.util.Calendar;

import javax.servlet.*;
import javax.servlet.http.*;
//import javax.xml.ws.WebServiceRef;
import jm.Robot;
import jm.adm.clas.Configuracion;
import jm.adm.clas.Iva;
import jm.adm.clas.Sucursal;
import jm.adm.clas.TablaImpuesto;
import jm.cnt.clas.ClavesContingenciaSRI;
import jm.fac.clas.Cliente;
import jm.fac.clas.DocumentoBanco;
import jm.fac.clas.FacturaElectronica;
import jm.fac.clas.FacturaVenta;
import jm.fac.clas.Instalacion;
import jm.fac.clas.PreFactura;
import jm.inf.clas.Radius;
import jm.inv.clas.Producto;
import jm.seg.clas.Auditoria;
import jm.web.Cadena;
import jm.web.Fecha;
import jm.web.Matriz;

/**
 *
 * @author Jorge
 */
public class frmPreFacturaGuardar extends HttpServlet {
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
        this._WSENVIO = config.getServletContext().getInitParameter("_WSENVIO");
        this._WSAUTORIZA = config.getServletContext().getInitParameter("_WSAUTORIZA");
        
        //System.setProperty("javax.net.ssl.keyStore", "/usr/lib/jvm/java-7-openjdk-amd64/jre/lib/security/cacerts");
        //System.setProperty("javax.net.ssl.keyStorePassword", "changeit");
        //System.setProperty("javax.net.ssl.keyStoreType", "pkcs12");
        //System.setProperty("javax.net.ssl.trustStore", "/usr/lib/jvm/java-7-openjdk-amd64/jre/lib/security/cacerts");
        //System.setProperty("javax.net.ssl.trustStorePassword", "changeit");
        //System.setProperty("sun.security.ssl.allowUnsafeRenegotiation", "true");
        
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
        /*String bloqueo_libros = "";
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
        }*/
        
        String ambiente = conf.getValor("ambiente");
        String tipoEmision = conf.getValor("tipo_emision"); // 1=normal    2=Indisponibilidad del sistema
        String clave_certificado = conf.getValor("clave_certificado");
        String cxc = conf.getValor("cxc");
        String ruc_empresa = conf.getValor("ruc");
        String razon_social_empresa = conf.getValor("razon_social");
        String nombre_comercial = conf.getValor("nombre_comercial");
        String num_resolucion = conf.getValor("num_resolucion");
        String oblga_contabilidad = conf.getValor("oblga_contabilidad");
        String dir_matriz = conf.getValor("dir_matriz");
        String desc_venta = conf.getValor("desc_venta");
        
        
        
        PreFactura objPreFactura = new PreFactura(this._ip, this._puerto, this._db, usuario, clave);
        DocumentoBanco objDocumentoBanco = new DocumentoBanco(this._ip, this._puerto, this._db, usuario, clave);
        
        
        Producto objProducto = new Producto(this._ip, this._puerto, this._db, usuario, clave);
        
        Iva objIva = new Iva(this._ip, this._puerto, this._db, usuario, clave);
        
        Sucursal objSucursal = new Sucursal(this._ip, this._puerto, this._db, usuario, clave);
        String direccion_sucursal = objSucursal.getDireccion( String.valueOf(id_sucursal) );
        objSucursal.cerrar();
        
        TablaImpuesto obTablaImpuesto = new TablaImpuesto(this._ip, this._puerto, this._db, usuario, clave);
        ResultSet rsImp = obTablaImpuesto.getTablaImpuestosVigente();
        String matImp[][] = Matriz.ResultSetAMatriz(rsImp);
        
        
        ClavesContingenciaSRI objClavesSri = new ClavesContingenciaSRI(this._ip, this._puerto, this._db, usuario, clave);
        
        String error = "";

        FacturaVenta objFacturaVenta = new FacturaVenta(this._ip, this._puerto, this._db, usuario, clave);
        
        FacturaElectronica objFE = new FacturaElectronica();
        
        String r = "msg»Ha ocurrido un error inesperado, por favor, vuelva a intentarlo más tarde o " +
                "contáctese con el administrador del sistema para mayor información.";
        
        try {
            String WHERE = request.getParameter("WHERE");
            String p = request.getParameter("p") != null ? request.getParameter("p") : "0";
            String modulo = request.getParameter("modulo");
            String emitir = request.getParameter("emitir");
            String id_prefactura = request.getParameter("idPF");
            String doc_electronico = request.getParameter("elect");
            String id_instalacion = request.getParameter("idIn");
            String id_cliente = request.getParameter("id_cliente");
            String txtPeriodo = request.getParameter("txtPer");
            String periodo = request.getParameter("Per");
            String serie_factura = request.getParameter("sFa");
            String num_factura = request.getParameter("nFa");
            String autorizacion = request.getParameter("aut");
            String ruc = request.getParameter("ruc");
            /*String fecha_emision = request.getParameter("fe");
            String razon_social = request.getParameter("raSo").toUpperCase();
            String telefono = request.getParameter("tel");
            String direccion = request.getParameter("dir").toUpperCase();*/
            String forma_pago = request.getParameter("foPa");
            String banco = request.getParameter("bc");
            String num_cheque = request.getParameter("chq");
            String num_comp_pago = request.getParameter("ncp");
            String gastos_bancos = request.getParameter("gb");
            String id_plan_cuenta_banco = request.getParameter("idPcBc").compareTo("")!=0 ? request.getParameter("idPcBc") : "0";
            String son = request.getParameter("son");
            //String observacion = request.getParameter("ob");
            String subtotal = request.getParameter("subt");
            String subtotal_2 = request.getParameter("subt2");
            String subtotal_0 = request.getParameter("subt0");
            String descuento = request.getParameter("dt");
            String iva_2 = request.getParameter("iva2");
            String total = request.getParameter("tt");
            int tope = Integer.valueOf(request.getParameter("tope"));
            String saldo_doc = request.getParameter("saldo_doc");
            String concepto = request.getParameter("concepto");
            
            String anticipo_ini = request.getParameter("anticipo_ini");
            String num_meses = request.getParameter("num_meses");
            String continua_servicio = request.getParameter("continua_servicio");

            descuento = (descuento.compareTo("")!=0) ? descuento : "0";
            subtotal_0 = (subtotal_0.compareTo("")!=0) ? subtotal_0 : "0";
            iva_2 = (iva_2.compareTo("")!=0) ? iva_2 : "0";
            subtotal_2 = (Float.parseFloat(subtotal_2)>0) ? subtotal_2 : (Float.parseFloat(iva_2)>0 ? subtotal : "0");
            gastos_bancos = (gastos_bancos.compareTo("")!=0) ? gastos_bancos : "0";
            float ax_total = Float.parseFloat(total);
            
            
            Cliente objCliente = new Cliente(this._ip, this._puerto, this._db, usuario, clave);
            String num_cuenta = "";
            String tipo_documento_cliente = "05";
            String razon_social = "";
            String direccion = "";
            try{
                ResultSet rsCliente = objCliente.getCliente(id_cliente);
                if(rsCliente.next()){
                    num_cuenta = rsCliente.getString("num_cuenta")!=null ? rsCliente.getString("num_cuenta") : "";
                    tipo_documento_cliente = rsCliente.getString("tipo_documento")!=null ? rsCliente.getString("tipo_documento") : "05";
                    razon_social = rsCliente.getString("razon_social")!=null ? rsCliente.getString("razon_social") : "";
                    direccion = rsCliente.getString("direccion")!=null ? rsCliente.getString("direccion") : "";
                    rsCliente.close();
                }
            }catch(Exception e){
                e.printStackTrace();
            }finally{
                objCliente.cerrar();
            }
            

            
            //String id_productos = "";
            //String cantidades = "";
            
            
            String matParamAsientoAx[][] = null;
            
            String ret_num_serie = request.getParameter("sr");
            String ret_num_retencion = request.getParameter("nr");
            String ret_autorizacion = request.getParameter("aur");
            String ret_fecha_emision = request.getParameter("fer");
            String ret_impuesto_retenido = request.getParameter("tvr");
            String ret_ejercicio_fiscal_mes = request.getParameter("efm");
            String ret_ejercicio_fiscal = request.getParameter("ef");
            
            
            String paramRet = "";
            String codigos = "";
            String codsRetencion = "";
            String basesImponibles = "";
            String porcentajesRet = "";
            String valoresRet = "";
            String sustentos = "";
            for(int i=0; i<10; i++){
                if(request.getParameter("idR"+i)!=null){
                    String codBI = request.getParameter("codi"+i);
                    String idR = request.getParameter("idR"+i);
                    String bi = request.getParameter("bi"+i);
                    String vr = request.getParameter("vr"+i);
                    codigos += codBI + ",";
                    int pos = Matriz.enMatriz(matImp, idR, 0);
                    codsRetencion += matImp[pos][2] + ",";
                    basesImponibles += bi + ",";
                    porcentajesRet += matImp[pos][3] + ",";
                    valoresRet += vr + ",";
                    sustentos += "01,";
                    ax_total -= Float.parseFloat(vr);
                    String cuenta = obTablaImpuesto.getIdCuentaVentas(idR);
                    matParamAsientoAx = Matriz.poner(matParamAsientoAx, new String[]{cuenta, vr, "0"});
                    paramRet += "['"+idR+"', '"+bi+"', '"+vr+"', '"+codBI+"'],";
                }
            }
            
            matParamAsientoAx = Matriz.poner(matParamAsientoAx, new String[]{cxc, String.valueOf(ax_total), "0"});
            
            if(paramRet.compareTo("")!=0){
                paramRet = paramRet.substring(0, paramRet.length()-1);
                codigos = codigos.substring(0, codigos.length()-1);
                codsRetencion = codsRetencion.substring(0, codsRetencion.length()-1);
                basesImponibles = basesImponibles.substring(0, basesImponibles.length()-1);
                porcentajesRet = porcentajesRet.substring(0, porcentajesRet.length()-1);
                valoresRet = valoresRet.substring(0, valoresRet.length()-1);
                sustentos = sustentos.substring(0, sustentos.length()-1);
            }else{
                ret_num_serie = "";
                ret_autorizacion = "";
                ret_num_retencion = "0";
                ret_fecha_emision = "";
                ret_ejercicio_fiscal_mes = "";
                ret_ejercicio_fiscal = "NULL";
                ret_impuesto_retenido = "0";
            }
            
            if(Float.parseFloat(descuento)>0){
                matParamAsientoAx = Matriz.poner(matParamAsientoAx, new String[]{desc_venta, descuento, "0"});
            }

            //  productos
            
            String ids_productos = "";
            String descripciones = "";
            String cantidades_prod = "";
            String preciosUnitarios = "";
            String descuentos = "";
            String subtotales = "";
            String ivas = "";
            String totales = "";
            String paramArtic = "";
            String dias_conexion = "0";
            for(int i=0; i<=tope; i++){
                if(request.getParameter("idPr"+i)!=null){
                    String id_producto = request.getParameter("idPr"+i);
                    String cantidad = request.getParameter("c"+i);
                    String subt = request.getParameter("st"+i);
                    String iva = request.getParameter("v"+i);
                    String totalpr = request.getParameter("t"+i);
                    
                    if(dias_conexion.compareTo("0")==0){
                        dias_conexion = cantidad;
                    }
                    
                    ids_productos += id_producto + ",";
                    descripciones += request.getParameter("cpt"+i) + ",";
                    cantidades_prod += cantidad + ",";
                    preciosUnitarios += request.getParameter("pu"+i) + ",";
                    descuentos += request.getParameter("d"+i) + ",";
                    subtotales += subt + ",";
                    ivas += iva + ",";
                    totales += totalpr + ",";
                    
                    String tipo = "";
                    String id_iva = "2";
                    String id_plan_cuenta_venta = "";
                    try{
                        ResultSet rs = objProducto.getProducto(id_producto);
                        if(rs.next()){
                            tipo = rs.getString("tipo")!=null ? rs.getString("tipo") : "";
                            id_iva = rs.getString("id_iva")!=null ? rs.getString("id_iva") : "2";
                            id_plan_cuenta_venta = rs.getString("id_plan_cuenta_venta")!=null ? rs.getString("id_plan_cuenta_venta") : "";
                            rs.close();
                        }
                    }catch(Exception e){}
                    
                    matParamAsientoAx = Matriz.poner(matParamAsientoAx, new String[]{id_plan_cuenta_venta, "0", subt});
                    
                    String cuenta = tipo.compareTo("s")==0 ? "id_plan_cuenta_venta_servicio" : "id_plan_cuenta_venta_bien";
                    String id_cuenta_iva = objIva.getIva(id_iva, cuenta);
                    matParamAsientoAx = Matriz.poner(matParamAsientoAx, new String[]{id_cuenta_iva, "0", iva});
                    
                    paramArtic += "['"+id_producto+"', '"+cantidad+"', '"+request.getParameter("pu"+i)+"', '"+subt+
                            "', '"+request.getParameter("d"+i)+"', '"+iva+"', '"+totalpr+"', '"+request.getParameter("cpt"+i)+
                            "', '"+request.getParameter("p_c"+i)+"', '"+tipo+"'],";
                }
            }
            if(paramArtic.compareTo("")!=0){
                paramArtic = paramArtic.substring(0, paramArtic.length()-1);
                ids_productos = ids_productos.substring(0, ids_productos.length()-1);
                descripciones = descripciones.substring(0, descripciones.length()-1);
                cantidades_prod = cantidades_prod.substring(0, cantidades_prod.length()-1);
                preciosUnitarios = preciosUnitarios.substring(0, preciosUnitarios.length()-1);
                descuentos = descuentos.substring(0, descuentos.length()-1);
                subtotales = subtotales.substring(0, subtotales.length()-1);
                ivas = ivas.substring(0, ivas.length()-1);
                totales = totales.substring(0, totales.length()-1);
            }
            
            String matParamAsiento[][] = Matriz.suprimirDuplicados(matParamAsientoAx, 0);
            String paramAsiento = "";
            for(int i=0; i<matParamAsiento.length; i++){
                paramAsiento += "['"+matParamAsiento[i][0]+"', '"+matParamAsiento[i][1]+"', '"+matParamAsiento[i][2]+"'],";
            }
            if(paramAsiento.compareTo("")!=0){
                paramAsiento = paramAsiento.substring(0, paramAsiento.length()-1);
            }
            
            
            boolean ok = true;

            String prod_sin_stock = objPreFactura.verificarStock(id_sucursal, ids_productos, cantidades_prod);
            if(prod_sin_stock.compareTo("")==0){

                //if(emitir.compareTo("1")==0){

                    if(!objPreFactura.facturaDuplicada(serie_factura, num_factura)){

                        if(saldo_doc.compareTo("")!=0 && saldo_doc.compareTo("0")!=0){
                            if(objDocumentoBanco.hayDocumento(num_comp_pago)){
                               objDocumentoBanco.actualizar(num_comp_pago, total);
                            }else{
                               float saldo = Float.parseFloat(saldo_doc) - (Float.parseFloat(total) - Float.parseFloat(ret_impuesto_retenido));
                               objDocumentoBanco.insertar(usuario, num_comp_pago, saldo_doc, saldo);
                            }
                        }

                        String xmlFirmado = "";
                        String estadoDocumento = "";
                        String certificado = this._DOCS_ELECTRONICOS + "certificado.p12";
                        String rutaSalida = this._DOCS_ELECTRONICOS + "firmados";
                        String claveAcceso = "";
                        String autorizacionXml = "";
                        String respuestaAutoriz = "";
                        if(doc_electronico.compareTo("0")==0){
                            ok = false;
                            String vecSerie[] = serie_factura.split("-");
                            
                            //do{
                                claveAcceso = objFE.getClaveAcceso(Fecha.getFecha("SQL"), "01", ruc_empresa, ambiente, vecSerie[0]+vecSerie[1], Cadena.setSecuencial(num_factura), tipoEmision);
                                if(tipoEmision.compareTo("2")==0){  //   por indisponibilidad del sistema
                                    claveAcceso = objClavesSri.getSigClave(Fecha.getFecha("SQL"), "01", ruc_empresa, ambiente, tipoEmision);
                                }
                            //}while(objFacturaVenta.claveDuplicada(claveAcceso));
                            
                            objFE.generarXml(claveAcceso, ambiente, tipoEmision, razon_social_empresa, nombre_comercial, ruc_empresa, "01", vecSerie[0], vecSerie[1], 
                                    Cadena.setSecuencial(num_factura), dir_matriz, Fecha.getFecha("SQL"), direccion_sucursal, num_resolucion, oblga_contabilidad, 
                                    tipo_documento_cliente, razon_social, ruc, subtotal, descuento, subtotal_0, subtotal_2, iva_2, total, 
                                    ids_productos, descripciones, cantidades_prod, preciosUnitarios, descuentos, subtotales, ivas, direccion);
                            String documentoXml = this._DOCS_ELECTRONICOS + "generados/" + claveAcceso + ".xml";
                            objFE.salvar(documentoXml);
                            error = objFE.getError();

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
                                                        //ok=false;
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
                                                error = "Fallo al acceder al WSDL en: " + this._WSENVIO;
                                            }


                                        } catch (Exception ex) {
                                            //Logger.getLogger(WsClienteSRI.class.getName()).log(Level.SEVERE, null, ex);

                                            estadoDocumento = "g";
                                            claveAcceso = objClavesSri.getSigClave(Fecha.getFecha("SQL"), "01", ruc_empresa, ambiente, tipoEmision);
                                            documentoXml = this._DOCS_ELECTRONICOS + "generados/" + claveAcceso + ".xml";
                                            objFE.salvar(documentoXml);
                                            error = objFE.getError();

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
                            String id_factura = objPreFactura.emitir(id_sucursal, id_punto_emision, id_prefactura, id_instalacion, usuario, serie_factura, num_factura, autorizacion, ruc,
                                    forma_pago, banco, num_cheque, num_comp_pago, gastos_bancos, id_plan_cuenta_banco, son, concepto, subtotal, subtotal_2, subtotal_0, 
                                    descuento, iva_2, total, "array[" + paramArtic + "]", ret_num_serie, ret_num_retencion, ret_autorizacion, ret_fecha_emision, 
                                    ret_ejercicio_fiscal_mes, ret_ejercicio_fiscal, ret_impuesto_retenido, "array[" + paramRet + "]::varchar[]", "array[" + paramAsiento + "]", 
                                    xmlFirmado, dias_conexion, ids_productos, cantidades_prod, preciosUnitarios, descuentos, subtotales, ivas, totales);

                            if(id_factura.compareTo("-1")!=0){
                                
                                objFacturaVenta.setDocumentoElectronico(id_factura, estadoDocumento, claveAcceso, autorizacionXml, respuestaAutoriz);
                                /*if(respuestaAutoriz.equals("RECHAZADO") || respuestaAutoriz.equals("NO AUTORIZADO")){
                                    objFacturaVenta.anular(id_factura);
                                }*/
                                

                                if(anticipo_ini.compareTo("")!=0){
                                    if(Integer.parseInt(num_meses)>0){
                                        int num_meses1 = Integer.parseInt(num_meses) - 1;
                                        String anticipo_fin = anticipo_ini;
                                        if(num_meses1>=1){
                                            anticipo_fin = Fecha.add(anticipo_ini, Calendar.MONTH, num_meses1);
                                        }
                                        int anio = Fecha.datePart("anio", anticipo_fin);
                                        int mes = Fecha.datePart("mes", anticipo_fin);
                                        anticipo_fin = Fecha.getUltimoDiaMes(anio, mes) + "/" + mes + "/" + anio;
                                        if(objPreFactura.ingresarAnticipo(id_instalacion, anticipo_ini, anticipo_fin, id_factura)){
                                            Auditoria auditoria = new Auditoria(this._ip, this._puerto, this._db, usuario, clave);
                                            auditoria.setRegistro(request, "REGISTRO DE ANTICIPO DEL PAGO DEL SERVICIO DE INTERNET PARA EL CLIENTE CON RUC "+ruc+" CON NUMERO DE FACTURA "+serie_factura+"-"+num_factura);
                                            auditoria.cerrar();
                                        }
                                    }
                                }
                                
                                /*if(continua_servicio.compareTo("on")==0){
                                    OrdenTrabajo objOrdenTrabajo = new OrdenTrabajo(this._ip, this._puerto, this._db, usuario, clave);
                                    objOrdenTrabajo.solucionar(id_sucursal, id_orden_trabajo, this._usuario, Fecha.getFecha("SQL"), Fecha.getHora(), solucionado, 
                                        conformidad, atencion, recomendacion, materiales, cantidades, set_deviceclave);
                                }*/

                                Auditoria auditoria = new Auditoria(this._ip, this._puerto, this._db, usuario, clave);
                                auditoria.setRegistro(request, "EMISION DE LA FACTURA NRO. "+serie_factura+"-"+num_factura+" CLIENTE CON RUC: "+ruc+" PARA EL PERIODO "+txtPeriodo);
                                auditoria.cerrar();

                                Robot objRobot = new Robot(this._ip, this._puerto, this._db, usuario, clave);
                                objRobot.generarPrefacturasFaltantes();
                                objRobot.cerrar();

                                /*String rad_db = conf.getValor("rad_db");
                                String rad_ip = conf.getValor("rad_ip");
                                String rad_puerto = conf.getValor("rad_puerto");
                                String rad_usuario = conf.getValor("rad_usuario");
                                String rad_clave = conf.getValor("rad_clave");

                                String radusername = "";
                                String radclave = "";
                                String estado_servicio = "";
                                Instalacion objInstalacion = new Instalacion(this._ip, this._puerto, this._db, usuario, clave);
                                try{
                                    ResultSet rsInstalacion = objInstalacion.getInstalacion(id_instalacion);
                                    if(rsInstalacion.next()){
                                        radusername = (rsInstalacion.getString("radusername")!=null) ? rsInstalacion.getString("radusername") : "";
                                        radclave = (rsInstalacion.getString("radclave")!=null) ? rsInstalacion.getString("radclave") : "";
                                        estado_servicio = (rsInstalacion.getString("estado_servicio")!=null) ? rsInstalacion.getString("estado_servicio") : "";
                                        rsInstalacion.close();
                                    }
                                }catch(Exception e){
                                    e.printStackTrace();
                                }finally{
                                    objInstalacion.cerrar();
                                }


                                if(estado_servicio.toLowerCase().compareTo("a")==0){
                                    Radius dbRadius = new Radius(rad_ip, Integer.parseInt(rad_puerto), rad_db, rad_usuario, rad_clave);
                                    dbRadius.setClave(radusername, radclave);
                                    dbRadius.cerrar();
                                }*/

                                r = "fun»_('d_21').innerHTML='';imprimir('pdfFacturaVenta?id="+id_factura+"');";
                                
                                String tbl = "{tbl:[";
                                if(modulo.compareTo("0")==0){        
                                    tbl = objPreFactura.paginar("vta_prefactura", "id_prefactura,ruc,razon_social,ip,txt_periodo", WHERE, Integer.parseInt(p), 100);
                                }
                                if(modulo.compareTo("1")==0){   
                                    tbl = objPreFactura.paginar("vta_documento_banco_cash", "id_prefactura,ruc,razon_social,txt_periodo,fecha_pago,hora_pago,total", WHERE, Integer.parseInt(p), 100);
                                }
                                if(modulo.compareTo("2")==0){   
                                    objPreFactura.ejecutar("update tbl_documento_banco_tarjeta_credito set factura_emitida=true where num_tarjeta='"+num_cuenta+"' and id_instalacion="+id_instalacion);
                                    tbl = objPreFactura.paginar("vta_documento_banco_tarjeta_credito", "id_prefactura,ruc,razon_social,num_tarjeta,txt_periodo,fecha_pago,total,total_tarjeta", "", Integer.parseInt(p), 100);
                                }
                                
                                if(tbl.compareTo("{tbl:[")!=0 && tbl.compareTo("{tbl:[]}")!=0  && tbl.compareTo("{tbl:]}")!=0){
                                    r += "^tbl»"+tbl;
                                }else{
                                    r += "_('d_11').innerHTML='';";
                                }
                            }else{
                                r = "msg»" + objPreFactura.getError();
                            }
                        } 
                        
                    }else{
                        r = "msg»El número de factura "+serie_factura+"-"+num_factura+" ya ha sido emitido.";
                    }

                /*}else{
                    if(objPreFactura.actualizar(id, id_instalacion, periodo, id_productos, cantidades, precios_unitarios, subtotales, descuentos, ivas, totales)){
                        Auditoria auditoria = new Auditoria(this._ip, this._puerto, this._db, usuario, clave);
                        auditoria.setRegistro(request, "ACTUALIZACION DE LA PRE-FACTURA DEL CLIENTE CON RUC: "+ruc+" PARA EL PERIODO "+txtPeriodo);
                        auditoria.cerrar();
                        r = "msg»Información actualizada satisfactoriamente.";
                    }
                }*/

            }else{
                r = "msg»Stock insuficiente. El stock del producto de código: " + prod_sin_stock + " ha disminuido por una venta realizada desde otra caja ubicada en la sucursal.";
            }

        }catch(Exception e){
            r = "msg»" + e.getMessage();
        } finally {
            //conf.setValor("bloqueo_libros", "false");
            conf.cerrar();
            objFacturaVenta.cerrar();
            objDocumentoBanco.cerrar();
            objPreFactura.cerrar();
            objClavesSri.cerrar();
        }
        
        try{
            out.print(r);
        }finally {
            out.close();
        }
    }

    private String getStringFromFile(String archivo) throws IOException 
    {
        File file = new File(archivo);
        FileReader fr = new FileReader(file);
        BufferedReader br = new BufferedReader(fr);
        StringBuilder cadXml = new StringBuilder();
        //String cadXml = "";
        String linea;
        while((linea=br.readLine())!=null){
            cadXml.append(linea);
            //cadXml += linea;
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

    /*private RespuestaSolicitud validarComprobante(byte[] xml) {
        
        //System.setProperty("javax.net.ssl.keyStore", "/usr/lib/jvm/java-7-openjdk-amd64/jre/lib/security/cacerts");
        //System.setProperty("javax.net.ssl.keyStorePassword", "changeit");
        //System.setProperty("javax.net.ssl.keyStoreType", "pkcs12");
        //System.setProperty("javax.net.ssl.trustStore", "/usr/lib/jvm/java-7-openjdk-amd64/jre/lib/security/cacerts");
        //System.setProperty("javax.net.ssl.trustStorePassword", "changeit");
        //System.setProperty("sun.security.ssl.allowUnsafeRenegotiation", "true");
        
        //System.setProperty("java.protocol.handler.pkgs", "com.sun.net.ssl.internal.www.protocol" );
        //Security.addProvider(new com.sun.net.ssl.internal.ssl.Provider());
        //System.setProperty("javax.net.ssl.keyStore", "/usr/lib/jvm/java-7-openjdk-amd64/jre/lib/security/cacerts");
        //System.setProperty("javax.net.ssl.keyStorePassword","changeit");
        //System.setProperty("javax.net.ssl.trustStore","/usr/lib/jvm/java-7-openjdk-amd64/jre/lib/security/cacerts");
        //System.setProperty("javax.net.ssl.trustStorePassword","changeit");
        //System.setProperty("javax.net.debug","all");
        
        
        ec.gob.sri.RecepcionComprobantes port = service.getRecepcionComprobantesPort();
        return port.validarComprobante(xml);
    }*/
    
}
