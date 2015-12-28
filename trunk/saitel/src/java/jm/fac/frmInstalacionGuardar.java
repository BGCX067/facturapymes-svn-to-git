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

import javax.servlet.*;
import javax.servlet.http.*;
import jm.adm.clas.Configuracion;
import jm.adm.clas.Iva;
import jm.adm.clas.Mensaje;
import jm.adm.clas.Sucursal;
import jm.adm.clas.TablaImpuesto;
import jm.cnt.clas.ClavesContingenciaSRI;
import jm.fac.clas.Cliente;
import jm.fac.clas.Contrato;
import jm.fac.clas.DocumentoBanco;
import jm.fac.clas.FacturaElectronica;
import jm.fac.clas.FacturaVenta;
import jm.fac.clas.Instalacion;
import jm.inf.clas.OrdenTrabajo;
import jm.fac.clas.PlanServicio;
import jm.fac.clas.PreFactura;
//import jm.inf.clas.Radius;
import jm.inv.clas.Producto;
import jm.seg.clas.Auditoria;
import jm.web.Cadena;
import jm.web.Fecha;
import jm.web.Matriz;

/**
 *
 * @author Jorge
 */
public class frmInstalacionGuardar extends HttpServlet {
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
        int id_sucursal_sesion = (Integer)sesion.getAttribute("sucursal");
        String empleado = (String)sesion.getAttribute("empleado");
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
        String cxc = conf.getValor("cxc");
        String ruc_empresa = conf.getValor("ruc");
        String razon_social_empresa = conf.getValor("razon_social");
        String nombre_comercial = conf.getValor("nombre_comercial");
        String num_resolucion = conf.getValor("num_resolucion");
        String oblga_contabilidad = conf.getValor("oblga_contabilidad");
        String dir_matriz = conf.getValor("dir_matriz");
        String desc_venta = conf.getValor("desc_venta");
        

        Instalacion objInstalacion = new Instalacion(this._ip, this._puerto, this._db, usuario, clave);
        PlanServicio objPlanServicio = new PlanServicio(this._ip, this._puerto, this._db, usuario, clave);
        
        Producto objProducto = new Producto(this._ip, this._puerto, this._db, usuario, clave);
        
        Iva objIva = new Iva(this._ip, this._puerto, this._db, usuario, clave);
        
        ClavesContingenciaSRI objClavesSri = new ClavesContingenciaSRI(this._ip, this._puerto, this._db, usuario, clave);
        
        Sucursal objSucursal = new Sucursal(this._ip, this._puerto, this._db, usuario, clave);
        String direccion_sucursal = objSucursal.getDireccion( String.valueOf(id_sucursal_sesion) );
        objSucursal.cerrar();
        
        TablaImpuesto obTablaImpuesto = new TablaImpuesto(this._ip, this._puerto, this._db, usuario, clave);
        ResultSet rsImp = obTablaImpuesto.getTablaImpuestosVigente();
        String matImp[][] = Matriz.ResultSetAMatriz(rsImp);
        
        
        
        String error = "";

        FacturaElectronica objFE = new FacturaElectronica();
        
        
        try {
            String r = "msg»Dirección IP ya se encuentra asignada.";
            String WHERE = request.getParameter("WHERE");
            String p = request.getParameter("p") != null ? request.getParameter("p") : "0";

            String id = request.getParameter("id");
            String doc_electronico = request.getParameter("elect");
            String id_cliente = request.getParameter("id_cliente");
            String imprimir = request.getParameter("imp");
            //String id_comprobante_ingreso = request.getParameter("idCI");
            String serie_factura = request.getParameter("sf");
            String num_factura = request.getParameter("nf");
            String autorizacion = request.getParameter("auf");
            String ruc = request.getParameter("ruc");
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
            String subtotal_2 = request.getParameter("subt2");
            String subtotal_0 = request.getParameter("subt0");
            String descuento = request.getParameter("dt");
            String iva_2 = request.getParameter("iva2");
            String total = request.getParameter("tt");
            String saldo_doc = request.getParameter("saldo_doc");

            descuento = (descuento.compareTo("")!=0) ? descuento : "0";
            subtotal_0 = (subtotal_0.compareTo("")!=0) ? subtotal_0 : "0";
            iva_2 = (iva_2.compareTo("")!=0) ? iva_2 : "0";
            gastos_bancos = (gastos_bancos.compareTo("")!=0) ? gastos_bancos : "0";

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
            
            
            String id_sucursal = request.getParameter("id_sucursal");
            String num_instalacion = objInstalacion.getNumInstalacion(Integer.parseInt(id_sucursal));
            String id_provincia = request.getParameter("prv");
            String id_ciudad = request.getParameter("ci");
            String id_parroquia = request.getParameter("prr");
            String id_sector = request.getParameter("id_sector");
            String tipo_instalacion = request.getParameter("tipo_instalacion");
            String direccion_instalacion = request.getParameter("direccion_instalacion");

            String ip = request.getParameter("ip");
            String ip_radio = request.getParameter("ip_radio");
            String id_plan_contratado = request.getParameter("id_plan_contratado");
            String costo_instalacion = request.getParameter("costo_instalacion");
            String es_instalacion = request.getParameter("es_instalacion");
            es_instalacion = es_instalacion.compareTo("t")==0 ? "TRUE" : "FALSE";
            String cobrar = request.getParameter("cobrar");
            cobrar = cobrar.compareTo("t")==0 ? "TRUE" : "FALSE";
            String motivo_no_cobrar = request.getParameter("motivo_no_cobrar");
            String set_deviceclave = request.getParameter("set_deviceclave");
           // set_deviceclave = set_deviceclave.compareTo("on")==0 ? "TRUE" : "FALSE";


            
            
            String matParamAsientoAx[][] = null;
            
            String ret_num_serie = request.getParameter("sr");
            String ret_num_retencion = request.getParameter("nr");
            String ret_autorizacion = request.getParameter("aur");
            String ret_fecha_emision = request.getParameter("fer");
            String ret_impuesto_retenido = request.getParameter("tvr");
            String ret_ejercicio_fiscal_mes = request.getParameter("efm");
            String ret_ejercicio_fiscal = request.getParameter("ef");
            
            matParamAsientoAx = Matriz.poner(matParamAsientoAx, new String[]{cxc, total, "0"});
            
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
                    String cuenta = obTablaImpuesto.getIdCuentaVentas(idR);
                    matParamAsientoAx = Matriz.poner(matParamAsientoAx, new String[]{cuenta, vr, "0"});
                    paramRet += "['"+idR+"', '"+bi+"', '"+vr+"', '"+codBI+"'],";
                }
            }
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
            String paramArtic = "";
            for(int i=0; i<=5; i++){
                if(request.getParameter("FidPr"+i)!=null){
                    String id_producto = request.getParameter("FidPr"+i);
                    String subt = request.getParameter("Fsbt"+i);
                    String iva = request.getParameter("Fv"+i);
                    
                    ids_productos += id_producto + ",";
                    //descripciones += request.getParameter("cpt"+i) + ",";
                    cantidades_prod += request.getParameter("cnt"+i) + ",";
                    preciosUnitarios += request.getParameter("Fpu"+i) + ",";
                    descuentos += request.getParameter("Fdes"+i) + ",";
                    subtotales += subt + ",";
                    ivas += iva + ",";
                    
                    String tipo = "";
                    String id_iva = "2";
                    String id_plan_cuenta_venta = "";
                    String descripcion = "";
                    try{
                        ResultSet rs = objProducto.getProducto(id_producto);
                        if(rs.next()){
                            tipo = rs.getString("tipo")!=null ? rs.getString("tipo") : "";
                            id_iva = rs.getString("id_iva")!=null ? rs.getString("id_iva") : "2";
                            id_plan_cuenta_venta = rs.getString("id_plan_cuenta_venta")!=null ? rs.getString("id_plan_cuenta_venta") : "";
                            descripcion = rs.getString("descripcion")!=null ? rs.getString("descripcion") : "";
                            rs.close();
                        }
                    }catch(Exception e){}
                    descripciones += descripcion + ",";
                    
                    matParamAsientoAx = Matriz.poner(matParamAsientoAx, new String[]{id_plan_cuenta_venta, "0", subt});
                    
                    String cuenta = tipo.compareTo("s")==0 ? "id_plan_cuenta_venta_servicio" : "id_plan_cuenta_venta_bien";
                    String id_cuenta_iva = objIva.getIva(id_iva, cuenta);
                    matParamAsientoAx = Matriz.poner(matParamAsientoAx, new String[]{id_cuenta_iva, "0", iva});
                    
                    paramArtic += "['"+id_producto+"', '"+request.getParameter("cnt"+i)+"', '"+request.getParameter("Fpu"+i)+"', '"+subt+
                            "', '"+request.getParameter("Fdes"+i)+"', '"+iva+"', '"+request.getParameter("Ftt"+i)+"', '"+descripcion+
                            "', '"+request.getParameter("Fpu"+i)+"', '"+tipo+"'],";
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
            String nombre_archivo = "factura_" + ruc + "_" + serie_factura + "-" + num_factura + ".xml";
            

            
            String id_contrato = "";
            Contrato objContrato = new Contrato(this._ip, this._puerto, this._db, usuario, clave);
            try{
                ResultSet rsContrato = objContrato.getContratoCliente(id_cliente);
                if(rsContrato.next()){
                    id_contrato = rsContrato.getString("id_contrato")!=null ? rsContrato.getString("id_contrato") : "";
                    rsContrato.close();
                }
            }catch(Exception ec){
                ec.printStackTrace();
            }finally{
                objContrato.cerrar();
            }

            if(objInstalacion.ipDisponible(id, id_sucursal_sesion, ip)){
                boolean bandera = true;
                
                r = "msg»Ha ocurrido un error inesperado, por favor, vuelva a intentarlo más tarde o " +
                    "contáctese con el administrador del sistema para mayor información.";
                String id_factura_id_Instalacion = "-1:-1";
                if(id.compareTo("-1")==0){
                    PreFactura objPreFactura = new PreFactura(this._ip, this._puerto, this._db, usuario, clave);
                    if(es_instalacion.compareTo("TRUE")==0){
                        if(objPreFactura.facturaDuplicada(serie_factura, num_factura)){
                            bandera = false;
                        }
                    }
                    objPreFactura.cerrar();

                    if(bandera){
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
                            claveAcceso = objFE.getClaveAcceso(Fecha.getFecha("SQL"), "01", ruc_empresa, ambiente, vecSerie[0]+vecSerie[1], Cadena.setSecuencial(num_factura), tipoEmision);
                            if(tipoEmision.compareTo("2")==0){  //   por indisponibilidad del sistema
                                claveAcceso = objClavesSri.getSigClave(Fecha.getFecha("SQL"), "01", ruc_empresa, ambiente, tipoEmision);
                            }
                            String xml = objFE.generarXml(claveAcceso, ambiente, tipoEmision, razon_social_empresa, nombre_comercial, ruc_empresa, "01", vecSerie[0], vecSerie[1], 
                                    Cadena.setSecuencial(num_factura), dir_matriz, Cadena.setFecha(fecha_emision), direccion_sucursal, num_resolucion, oblga_contabilidad, 
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
                                                error = "Fallo al acceder al WSDL en: https://celcer.sri.gob.ec/comprobantes-electronicos-ws/RecepcionComprobantes?wsdl";
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
                            String radusername = ruc+"_"+id_sucursal+"_"+num_instalacion;
                            String radclave = Cadena.getRandomClave(10);
                            id_factura_id_Instalacion = objInstalacion.insertar(id_contrato, num_instalacion, id_sucursal, id_provincia, id_ciudad, id_parroquia, 
                                    id_sector, tipo_instalacion, costo_instalacion, direccion_instalacion, ip, ip_radio, id_plan_contratado, es_instalacion, cobrar, motivo_no_cobrar, 
                                    radusername, radclave, Cadena.getRandomClave(10), Integer.parseInt(id_sucursal), id_punto_emision, id_cliente, usuario, serie_factura, num_factura, autorizacion, ruc,
                                    razon_social, fecha_emision, direccion, telefono, forma_pago, banco, num_cheque, num_comp_pago, gastos_bancos, id_plan_cuenta_banco,
                                    son, observacion, subtotal, subtotal_2, subtotal_0, descuento, iva_2, total, "array[" + paramArtic + "]", ret_num_serie, ret_num_retencion, 
                                    ret_autorizacion, ret_fecha_emision, ret_ejercicio_fiscal_mes, ret_ejercicio_fiscal, ret_impuesto_retenido, 
                                    "array[" + paramRet + "]::varchar[]", "array[" + paramAsiento + "]", xmlFirmado);
                            if(id_factura_id_Instalacion.compareTo("-1:-1") != 0){

                                String vec[] = id_factura_id_Instalacion.split(":");
                                String id_factura = vec[0];
                                FacturaVenta objFacturaVenta = new FacturaVenta(this._ip, this._puerto, this._db, usuario, clave);
                                objFacturaVenta.setDocumentoElectronico(id_factura, estadoDocumento, claveAcceso, autorizacionXml, respuestaAutoriz);
                                /*if(respuestaAutoriz.equals("RECHAZADO") || respuestaAutoriz.equals("NO AUTORIZADO")){
                                    objFacturaVenta.anular(id_factura);
                                }*/
                                objFacturaVenta.cerrar();

                                Auditoria auditoria = new Auditoria(this._ip, this._puerto, this._db, usuario, clave);
                                auditoria.setRegistro(request, "INGRESO DEL NUEVA INSTALACION Nro. "+id_sucursal+"-"+num_instalacion);

                                OrdenTrabajo objOrdenTrabajo = new OrdenTrabajo(this._ip, this._puerto, this._db, usuario, clave);
                                String num_orden = objOrdenTrabajo.getNumOrden(id_sucursal_sesion);
                                String id_orden_trabajo = objOrdenTrabajo.insertar(vec[1], id_sucursal_sesion, num_orden, "3", usuario, "", "", "INSTALACION");
                                String editar_orden = "";
                                if(id_orden_trabajo.compareTo("-1")!=0){
                                    editar_orden = "fac_trabajoEditar("+id_orden_trabajo+");";
                                    auditoria.setRegistro(request, "INGRESO DE UNA NUEVA ORDEN DE TRABAJO NRO. "+ id_sucursal_sesion + "-" +num_orden);

                                    Mensaje objMensaje = new Mensaje(this._ip, this._puerto, this._db, usuario, clave);
                                    objMensaje.setMensaje(usuario, empleado + " ha generado una nueva orden de trabajo No. " + num_orden, "trabajo", id_orden_trabajo, Fecha.getFecha("ISO"), "0.3");
                                    objMensaje.cerrar();

                                }
                                objOrdenTrabajo.cerrar();
                                auditoria.cerrar();

                                String msg = "^msg»Ha ocurrido un error al registrar al nuevo usuario en el servidor FREE RADIUS.";
                                try{
                                    String plan = objPlanServicio.getSufPlan(id_plan_contratado);

                                    /*Radius dbRadius = new Radius(this._ip, this._puerto, this._db, usuario, clave);
                                    if(dbRadius.crear(radusername, plan)){
                                        msg = "";
                                    }
                                    dbRadius.cerrar();*/
                                }catch(Exception ex){
                                    ex.printStackTrace();
                                }
                                r = "err»0^vta»cmp^fun»fac_installEditar("+vec[1]+");"+editar_orden+(imprimir.compareTo("1")==0?"imprimir('pdfFacturaVenta?id="+vec[0]+"');":"")+
                                    "^tbl»"+objInstalacion.paginar("vta_instalacion", "id_instalacion,ruc,razon_social,ip,txt_estado_servicio", WHERE, Integer.parseInt(p), 100)+msg;
                            }else{
                                r = "msg»"+objInstalacion.getError();
                            }
                        }else{
                            r = "msg»Ha ocurrido un error en el proceso de generación de la factura electrónica.";
                        }
                    }else{
                        r = "msg»El número de factura "+serie_factura+"-"+num_factura+" ya ha sido emitido.";
                    }
                }else{
                    String estado_servicio = request.getParameter("estado_servicio");
                    ok = false;
                    if(!objInstalacion.tieneDeuda(id, id_cliente, estado_servicio)){
                        ok = objInstalacion.actualizar(id, id_sucursal, id_provincia, id_ciudad, id_parroquia, id_sector, direccion_instalacion, ip, cobrar, motivo_no_cobrar, estado_servicio, set_deviceclave);
                            
                        /*Radius dbRadius = new Radius(this._ip, this._puerto, this._db, usuario, clave);
                        String radusername = request.getParameter("radusername");
                        String msg = "^msg»No se ha podido cambiar el grupo en el servidor FREE RADIUS.";

                        String grupo = "suspendidos";
                        if(estado_servicio.compareTo("c")==0){
                            grupo = "cortados";
                        }
                        if(estado_servicio.compareTo("a")==0){
                            grupo = objPlanServicio.getSufPlan(id_plan_contratado);
                        }

                        if(dbRadius.setGrupoRadusergroup(radusername, grupo)){
                            msg = "Actualización de información realizada satidfactoriamente.";
                        }
                        
                        if(estado_servicio.compareTo("e")==0 || estado_servicio.compareTo("d")==0 || estado_servicio.compareTo("t")==0 || estado_servicio.compareTo("n")==0){
                            dbRadius.eliminar(radusername);
                        }
                        
                        dbRadius.cerrar();*/
                    }else{
                        ok = objInstalacion.actualizar(id, id_provincia, id_ciudad, id_parroquia, id_sector, direccion_instalacion, ip, cobrar, set_deviceclave);
                        r = "msg»No se ha actualizado el estado del servicio ya que el cliente posee una prefactura o una cuenta por pagar.";
                    }
                    if(ok){
                        Auditoria auditoria = new Auditoria(this._ip, this._puerto, this._db, usuario, clave);
                        auditoria.setRegistro(request, "ACTUALIZACION DE INFORMACION DE INSTALACION Nro. "+id_sucursal+"-"+num_instalacion);
                        auditoria.cerrar();
                        r = "tbl»"+objInstalacion.paginar("vta_instalacion", "id_instalacion,ruc,razon_social,ip,txt_estado_servicio", WHERE, Integer.parseInt(p), 100)+
                                "^msg»Actualización de información realizada satidfactoriamente.";
                    }
                }
            }

            out.print(r);
        } finally {
            conf.setValor("bloqueo_libros", "false");
            conf.cerrar();
            objClavesSri.cerrar();
            objInstalacion.cerrar();
            objPlanServicio.cerrar();
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