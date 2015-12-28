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

package jm.act;

import ec.gob.sri.FirmaXadesBes;
import ec.gob.sri.wsc.ArchivoUtils;
import ec.gob.sri.wsc.AutorizacionComprobantesWS;
import ec.gob.sri.wsc.DirectorioConfiguracion;
import ec.gob.sri.wsc.EnvioComprobantesWS;
import java.io.*;
import java.sql.ResultSet;

import javax.servlet.*;
import javax.servlet.http.*;
import jm.act.clas.Activo;
import jm.adm.clas.Configuracion;
import jm.adm.clas.Sucursal;
import jm.cnt.clas.ClavesContingenciaSRI;
import jm.fac.clas.Cliente;
import jm.fac.clas.DocumentoBanco;
import jm.fac.clas.FacturaElectronica;
import jm.fac.clas.FacturaVenta;
import jm.seg.clas.Auditoria;
import jm.web.Cadena;
import jm.web.Fecha;

/**
 *
 * @author Jorge
 */
public class frmFacturaVentaActivoGuardar extends HttpServlet {
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
        int id_sucursal = (Integer)sesion.getAttribute("sucursal");
        int id_punto_emision = (Integer)sesion.getAttribute("id_punto_emision");

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
        String cuenta_propina = conf.getValor("propina");
        String ambiente = conf.getValor("ambiente");
        String tipoEmision = conf.getValor("tipo_emision"); // 1=normal    2=Indisponibilidad del sistema
        String ruc_empresa = conf.getValor("ruc");
        String razon_social_empresa = conf.getValor("razon_social");
        String nombre_comercial = conf.getValor("nombre_comercial");
        String num_resolucion = conf.getValor("num_resolucion");
        String oblga_contabilidad = conf.getValor("oblga_contabilidad");
        String dir_matriz = conf.getValor("dir_matriz");
        String desc_comp = conf.getValor("desc_comp");
        
        Sucursal objSucursal = new Sucursal(this._ip, this._puerto, this._db, usuario, clave);
        String direccion_sucursal = objSucursal.getDireccion( String.valueOf(id_sucursal) );
        objSucursal.cerrar();

        FacturaVenta objFacturaVenta = new FacturaVenta(this._ip, this._puerto, this._db, usuario, clave);
        
        FacturaElectronica objFE = new FacturaElectronica();
        
        ClavesContingenciaSRI objClavesSri = new ClavesContingenciaSRI(this._ip, this._puerto, this._db, usuario, clave);
        
        Activo objActivo = new Activo(this._ip, this._puerto, this._db, usuario, clave);

        String error = "";
        
        try {
            String r = "msg»Ha ocurrido un error inesperado, por favor, vuelva a intentarlo más tarde o " +
                "contáctese con el administrador del sistema para mayor información.";

            String WHERE = request.getParameter("WHERE");
            String p = request.getParameter("p") != null ? request.getParameter("p") : "0";
            String id = request.getParameter("idFactura");
            String imprimir = request.getParameter("imp");
            //String id_comprobante_ingreso = request.getParameter("idCI");
            String doc_electronico = request.getParameter("elect");
            String serie_factura = request.getParameter("sf");
            String num_factura = request.getParameter("nf");
            String autorizacion = request.getParameter("auf");
            String ruc = request.getParameter("ruc").toUpperCase();
            String fecha_emision = request.getParameter("fe");
            String razon_social = request.getParameter("raSo").toUpperCase();
            String telefono = request.getParameter("tel");
            String direccion = request.getParameter("dir").toUpperCase();
            String forma_pago = request.getParameter("foPa");
            String banco = request.getParameter("bco");
            String num_cheque = request.getParameter("chq");
            String num_comp_pago = request.getParameter("ncp");
            String gastos_bancos = request.getParameter("gb");
            String id_plan_cuenta_banco = request.getParameter("idPcBc").compareTo("")!=0 ? request.getParameter("idPcBc") : "0";
            String son = request.getParameter("son");
            String observacion = request.getParameter("ob");
            String subtotal = request.getParameter("subt");
            String subtotal_0 = request.getParameter("subt0");
            String subtotal_2 = request.getParameter("subt2");
            String subtotal_6 = "0";
            //String descuento = request.getParameter("dt");
            String iva_2 = request.getParameter("iva2");
            String total = request.getParameter("tt");
            int tope = Integer.valueOf(request.getParameter("tope"));
            String saldo_doc = request.getParameter("saldo_doc");
            String genNewClave = request.getParameter("genNewClave");

            //descuento = (descuento.compareTo("")!=0) ? descuento : "0";
            subtotal_0 = (subtotal_0.compareTo("")!=0) ? subtotal_0 : "0";
            subtotal_2 = (subtotal_2.compareTo("")!=0) ? subtotal_2 : "0";
            iva_2 = (iva_2.compareTo("")!=0) ? iva_2 : "0";
            gastos_bancos = (gastos_bancos.compareTo("")!=0) ? gastos_bancos : "0";

            String id_productos = "";
            String descripciones = "";
            String cantidades = "";
            String precios_costo = "";
            String precios_unitarios = "";
            String subtotales = "";
            String descuentos = "";
            String ivas = "";
            String totales = "";

            for(int i=0; i<=tope; i++){
                if(request.getParameter("idPr"+i)!=null){
                    id_productos += request.getParameter("idAc"+i) + ",";
                    descripciones += request.getParameter("cpt"+i) + ",";
                    cantidades += request.getParameter("c"+i) + ",";
                    precios_costo += request.getParameter("p_c"+i) + ",";
                    precios_unitarios += request.getParameter("pu"+i) + ",";
                    subtotales += request.getParameter("st"+i) + ",";
                    descuentos +=  "0,";
                    ivas += request.getParameter("v"+i) + ",";
                    totales += request.getParameter("t"+i) + ",";
                }
            }

            //   RETENCIONES

            String ret_num_serie = request.getParameter("sr");
            String ret_num_retencion = request.getParameter("nr");
            String ret_autorizacion = request.getParameter("aur");
            String ret_fecha_emision = request.getParameter("fer");
            String ret_impuesto_retenido = request.getParameter("tvr");
            String ret_ejercicio_fiscal = request.getParameter("ef");
            String ret_ejercicio_fiscal_mes = request.getParameter("efm");
            
            //String id_plan_cuenta_caja_banco = request.getParameter("idPcCajBans");

            String codBI = "";
            String id_retenciones = "";
            String bases_imponibles = "";
            String valores_retenidos = "";
            if(forma_pago.compareTo("d")!=0){
                for(int i=0; i<10; i++){
                    if(request.getParameter("idR"+i)!=null){
                        codBI += request.getParameter("codi"+i) + ",";
                        id_retenciones += request.getParameter("idR"+i) + ",";
                        bases_imponibles += request.getParameter("bi"+i) + ",";
                        valores_retenidos += request.getParameter("vr"+i) + ",";
                    }
                }
                if(id_retenciones.compareTo("")!=0){
                    codBI = codBI.substring(0, codBI.length()-1);
                    id_retenciones = id_retenciones.substring(0, id_retenciones.length()-1);
                    bases_imponibles = bases_imponibles.substring(0, bases_imponibles.length()-1);
                    valores_retenidos = valores_retenidos.substring(0, valores_retenidos.length()-1);
                }else{
                    ret_num_retencion = "";
                    ret_autorizacion = "";
                    ret_fecha_emision = "";
                    ret_impuesto_retenido = "0";
                    ret_ejercicio_fiscal_mes = "";
                    ret_ejercicio_fiscal = "0";
                }
            }else{
                ret_num_retencion = "";
                ret_autorizacion = "";
                ret_fecha_emision = "";
                ret_impuesto_retenido = "0";
                ret_ejercicio_fiscal_mes = "";
                ret_ejercicio_fiscal = "0";
            }

            Cliente objCliente = new Cliente(this._ip, this._puerto, this._db, usuario, clave);
            String tipo_documento_cliente = "05";
            try{
                ResultSet rsCliente = objCliente.getClientePorRuc(ruc);
                if(rsCliente.next()){
                    tipo_documento_cliente = rsCliente.getString("tipo_documento")!=null ? rsCliente.getString("tipo_documento") : "05";
                    rsCliente.close();
                }
            }catch(Exception e){
                e.printStackTrace();
            }finally{
                objCliente.cerrar();
            }
            
            boolean ok = true;
            String xmlFirmado = "";
            String estadoDocumento = "";
            String certificado = this._DOCS_ELECTRONICOS + "certificado.p12";
            String rutaSalida = this._DOCS_ELECTRONICOS + "firmados";
            String claveAcceso = "";
            String autorizacionXml = "";
            String respuestaAutoriz = "";
            String nombre_archivo = "factura_" + ruc + "_" + serie_factura + "-" + num_factura + ".xml";

            if(id_productos.compareTo("")!=0){
                id_productos = id_productos.substring(0, id_productos.length()-1);
                descripciones = descripciones.substring(0, descripciones.length()-1);
                cantidades = cantidades.substring(0, cantidades.length()-1);
                precios_costo = precios_costo.substring(0, precios_costo.length()-1);
                precios_unitarios = precios_unitarios.substring(0, precios_unitarios.length()-1);
                subtotales = subtotales.substring(0, subtotales.length()-1);
                descuentos = descuentos.substring(0, descuentos.length()-1);
                ivas = ivas.substring(0, ivas.length()-1);
                totales = totales.substring(0, totales.length()-1);

                String id_factura_id_compDiario = "-1:-1";
                if(id.compareTo("-1")==0){
                    
                    if(!objFacturaVenta.facturaDuplicada(serie_factura, num_factura)){

                        if(saldo_doc.compareTo("")!=0 && saldo_doc.compareTo("0")!=0){
                            DocumentoBanco objDocumentoBanco = new DocumentoBanco(this._ip, this._puerto, this._db, usuario, clave);
                            if(objDocumentoBanco.hayDocumento(num_comp_pago)){
                                objDocumentoBanco.actualizar(num_comp_pago, total);
                            }else{
                                float saldo = Float.parseFloat(saldo_doc) - (Float.parseFloat(total) - Float.parseFloat(ret_impuesto_retenido));
                                objDocumentoBanco.insertar(usuario, num_comp_pago, saldo_doc, saldo);
                            }
                            objDocumentoBanco.cerrar();
                        }

                        if(doc_electronico.compareTo("0")==0){
                            ok = false;
                            String vecSerie[] = serie_factura.split("-");
                            claveAcceso = objFE.getClaveAcceso(Fecha.getFecha("SQL"), "01", ruc_empresa, ambiente, vecSerie[0]+vecSerie[1], Cadena.setSecuencial(num_factura), tipoEmision);
                            if(tipoEmision.compareTo("2")==0){  //   por indisponibilidad del sistema
                                claveAcceso = objClavesSri.getSigClave(Fecha.getFecha("SQL"), "01", ruc_empresa, ambiente, tipoEmision);
                            }
                            objFE.generarXml(claveAcceso, ambiente, tipoEmision, razon_social_empresa, nombre_comercial, ruc_empresa, "01", vecSerie[0], vecSerie[1], 
                                    Cadena.setSecuencial(num_factura), dir_matriz, Cadena.setFecha(fecha_emision), direccion_sucursal, num_resolucion, oblga_contabilidad, 
                                    tipo_documento_cliente, "PRUEBAS SERVICIO DE RENTAS INTERNAS", ruc, subtotal, "0", subtotal_0, subtotal_2, iva_2, total, 
                                    id_productos, descripciones, cantidades, precios_unitarios, descuentos, subtotales, ivas, direccion);
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
                                                ok=true;
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
                                                        //ok = false;
                                                    }
                                                }


                                                //ArchivoXML.delete();
                                            } else if (respuestaRecepcion.getEstado().equals("DEVUELTA")) {
                                                estadoDocumento = "d";
                                                error = EnvioComprobantesWS.obtenerMensajeRespuesta(respuestaRecepcion);

                                                String dirRechazados = DirectorioConfiguracion.getRutaArchivoNoAutorizado() + File.separator + "devueltos";

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
                            id_factura_id_compDiario = objActivo.insertar(id_sucursal, id_punto_emision, usuario, serie_factura, num_factura, autorizacion, ruc,
                                    razon_social, fecha_emision, direccion, telefono, forma_pago, banco, num_cheque, num_comp_pago, gastos_bancos, id_plan_cuenta_banco,
                                    son, observacion, subtotal, subtotal_0, subtotal_2, subtotal_6, iva_2, total, id_productos, descripciones, cantidades, precios_costo, precios_unitarios,
                                    subtotales, ivas, totales, ret_num_serie, ret_num_retencion, ret_autorizacion, ret_fecha_emision, ret_ejercicio_fiscal_mes, ret_ejercicio_fiscal,
                                    ret_impuesto_retenido, codBI, id_retenciones, bases_imponibles, valores_retenidos, xmlFirmado);
                            if(id_factura_id_compDiario.compareTo("-1:-1") != 0){
                                String vecFactComp[] = id_factura_id_compDiario.split(":");
                                String id_factura = vecFactComp[0];
                                objFacturaVenta.setDocumentoElectronico(id_factura, estadoDocumento, claveAcceso, autorizacionXml, respuestaAutoriz);
                                
                                Auditoria auditoria = new Auditoria(this._ip, this._puerto, this._db, usuario, clave);
                                auditoria.setRegistro(request, "EMISION DE LA FACTURA DE VENTA: "+serie_factura+"-"+num_factura);
                                auditoria.cerrar();
                                String vec[] = id_factura_id_compDiario.split(":");
                                r = "err»0^vta»cmp^fun»imprimir('pdfPersonalizacion?id="+vec[1]+"');"+(imprimir.compareTo("1")==0?"imprimir('pdfFacturaVenta?id="+vec[0]+"');":"")+
                                    "^tbl»"+objActivo.paginar("vta_factura_venta", "id_factura_venta,ruc,razon_social,numero_factura,toDateSQL(fecha_emision),total,txt_anulado,txt_estado_documento", WHERE, Integer.parseInt(p), 100);
                            }else{
                                r = "msg»"+objFacturaVenta.getError();
                            }
                        }
                    }else{
                        r = "msg»El número de factura "+serie_factura+"-"+num_factura+" ya ha sido emitido.";
                    }
                    
                    
                    
                    
                }else{ //   EDICION
                    
                    
                    
                    if(saldo_doc.compareTo("")!=0 && saldo_doc.compareTo("0")!=0){
                        DocumentoBanco objDocumentoBanco = new DocumentoBanco(this._ip, this._puerto, this._db, usuario, clave);
                        if(objDocumentoBanco.hayDocumento(num_comp_pago)){
                            objDocumentoBanco.actualizar(num_comp_pago, total);
                        }else{
                            float saldo = Float.parseFloat(saldo_doc) - (Float.parseFloat(total) - Float.parseFloat(ret_impuesto_retenido));
                            objDocumentoBanco.insertar(usuario, num_comp_pago, saldo_doc, saldo);
                        }
                        objDocumentoBanco.cerrar();
                    }

                    if(doc_electronico.compareTo("0")==0){
                        ok = false;
                        claveAcceso = objActivo.getClaveAcceso(id);
                        String vecSerie[] = serie_factura.split("-");
                        
                        if(genNewClave.compareTo("true")==0){
                            claveAcceso = objFE.getClaveAcceso(Fecha.getFecha("SQL"), "01", ruc_empresa, ambiente, vecSerie[0]+vecSerie[1], Cadena.setSecuencial(num_factura), tipoEmision);
                            if(tipoEmision.compareTo("2")==0){  //   por indisponibilidad del sistema
                                claveAcceso = objClavesSri.getSigClave(Fecha.getFecha("SQL"), "01", ruc_empresa, ambiente, tipoEmision);
                            }
                        }
                        
                        objFE.generarXml(claveAcceso, ambiente, tipoEmision, razon_social_empresa, nombre_comercial, ruc_empresa, "01", vecSerie[0], vecSerie[1], 
                                Cadena.setSecuencial(num_factura), dir_matriz, Cadena.setFecha(fecha_emision), direccion_sucursal, num_resolucion, oblga_contabilidad, 
                                tipo_documento_cliente, "PRUEBAS SERVICIO DE RENTAS INTERNAS", ruc, subtotal, "0", subtotal_0, subtotal_2, iva_2, total, 
                                id_productos, descripciones, cantidades, precios_unitarios, descuentos, subtotales, ivas, direccion);
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
                                            ok=true;
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
                                                    //ok = false;
                                                }
                                            }


                                            //ArchivoXML.delete();
                                        } else if (respuestaRecepcion.getEstado().equals("DEVUELTA")) {
                                            estadoDocumento = "d";
                                            error = EnvioComprobantesWS.obtenerMensajeRespuesta(respuestaRecepcion);

                                            String dirRechazados = DirectorioConfiguracion.getRutaArchivoNoAutorizado() + File.separator + "devueltos";

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
                                        error = ex.getMessage();
                                        //Logger.getLogger(WsClienteSRI.class.getName()).log(Level.SEVERE, null, ex);
                                    }
                                }else{
                                    autorizacionXml = this.getStringFromFile(this._DOCS_ELECTRONICOS + "firmados/" + claveAcceso + ".xml");
                                    ok = true;
                                }
                            }
                        }
                    }
                    r = "msg»"+error;
                    
                    if(ok){
                        String id_personalizacion = objActivo.anular(id, "true");
                        if(id_personalizacion.compareTo("-1")!=0){
                            String id_activo_personalizacion = objActivo.actualizar(id, id_sucursal, id_punto_emision, usuario, serie_factura, num_factura, autorizacion, ruc,
                                    razon_social, fecha_emision, direccion, telefono, forma_pago, banco, num_cheque, num_comp_pago, gastos_bancos, id_plan_cuenta_banco,
                                    son, observacion, subtotal, subtotal_0, subtotal_2, subtotal_6, iva_2, total, id_productos, descripciones, cantidades, precios_costo, precios_unitarios,
                                    subtotales, ivas, totales, ret_num_serie, ret_num_retencion, ret_autorizacion, ret_fecha_emision, ret_ejercicio_fiscal_mes, ret_ejercicio_fiscal,
                                    ret_impuesto_retenido, codBI, id_retenciones, bases_imponibles, valores_retenidos, xmlFirmado);
                            if(id_activo_personalizacion.compareTo("-1") != 0){
                                objFacturaVenta.setEstadoDocumento(id, estadoDocumento, autorizacionXml, respuestaAutoriz);

                                Auditoria auditoria = new Auditoria(this._ip, this._puerto, this._db, usuario, clave);
                                auditoria.setRegistro(request, "EDICION DE LA FACTURA DE VENTA: "+serie_factura+"-"+num_factura);
                                auditoria.cerrar();
                                //String vec[] = id_factura_id_compDiario.split(":");
                                r = "err»0^vta»cmp^fun»imprimir('pdfPersonalizacion?id="+id_activo_personalizacion+"');"+(imprimir.compareTo("1")==0?"imprimir('pdfFacturaVenta?id="+id+"');":"")+
                                    "^tbl»"+objActivo.paginar("vta_factura_venta", "id_factura_venta,ruc,razon_social,numero_factura,toDateSQL(fecha_emision),total,txt_anulado,txt_estado_documento", WHERE, Integer.parseInt(p), 100);
                            }else{
                                r = "msg»"+objFacturaVenta.getError();
                            }
                        }else{
                            r = "msg»"+objFacturaVenta.getError();
                        }
                        
                    }
                    
                }

            }


            out.print(r);

        } finally {
            conf.setValor("bloqueo_libros", "false");
            conf.cerrar();
            objFacturaVenta.cerrar();
            objClavesSri.cerrar();
            objActivo.cerrar();
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
