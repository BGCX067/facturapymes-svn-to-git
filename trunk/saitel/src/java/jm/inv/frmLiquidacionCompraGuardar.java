/**
* @version 4.0
* @package YAKUSOFT.
* @author Jorge Washington Mueses Cevallos.
* @copyright Copyright (C) 2011 por Jorge Mueses. Todos los derechos reservados.
* @license http://www.gnu.org/copyleft/gpl.html GNU/GPL.
* YAKUSOFT es un software de libre distribuciÃ³n, que puede ser
* copiado y distribuido bajo los tÃ©rminos de la Licencia
* Attribution-NonCommercial-NoDerivs 3.0 Unported,
* de acuerdo con la publicada por la CREATIVE COMMONS CORPORATION.
*/

package jm.inv;

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
import jm.adm.clas.Iva;
import jm.adm.clas.Sucursal;
import jm.adm.clas.TablaImpuesto;
import jm.cnt.clas.ClavesContingenciaSRI;
import jm.inv.clas.LiquidacionCompra;
import jm.inv.clas.Producto;
import jm.inv.clas.Proveedor;
import jm.inv.clas.RetencionElectronica;
import jm.nom.clas.Empleado;
import jm.seg.clas.Auditoria;
import jm.web.Cadena;
import jm.web.Correo;
import jm.web.Fecha;
import jm.web.Matriz;

/**
 *
 * @author Jorge
 */
public class frmLiquidacionCompraGuardar extends HttpServlet {
    private String _ip = null;
    private int _puerto = 5432;
    private String _db = null;
    
    private String _DOCS_ELECTRONICOS = "";
    private String _WSENVIO = "https://cel.sri.gob.ec/comprobantes-electronicos-ws/RecepcionComprobantes?wsdl";
    private String _WSAUTORIZA = "https://cel.sri.gob.ec/comprobantes-electronicos-ws/AutorizacionComprobantes?wsdl";
    private String _SVR_MAIL = "";

    public void init(ServletConfig config) throws ServletException
    {
        this._ip = config.getServletContext().getInitParameter("_IP");
        this._puerto = Integer.parseInt(config.getServletContext().getInitParameter("_PUERTO"));
        this._db = config.getServletContext().getInitParameter("_DB");
        
        this._DOCS_ELECTRONICOS = config.getServletContext().getInitParameter("_DOCS_ELECTRONICOS");
        this._WSENVIO = config.getServletContext().getInitParameter("_WSENVIO");
        this._WSAUTORIZA = config.getServletContext().getInitParameter("_WSAUTORIZA");
        this._SVR_MAIL = config.getServletContext().getInitParameter("_SVR_MAIL");
    }
    /**
    * Processes requests for both HTTP <code>GET</code> and <code>POST</code> methods.
    * @param request servlet request
    * @param response servlet response
    */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        HttpSession sesion = request.getSession(true);
        String id_rol = (String)sesion.getAttribute("id_rol");
        int id_sucursal = (Integer)sesion.getAttribute("sucursal");
        String usuario = (String)sesion.getAttribute("usuario");
        String clave = (String)sesion.getAttribute("clave");
        String empleado = (String)sesion.getAttribute("empleado");

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
        String ruc = conf.getValor("ruc");
        String razon_social = conf.getValor("razon_social");
        String nombre_comercial = conf.getValor("nombre_comercial");
        String num_resolucion = conf.getValor("num_resolucion");
        String oblga_contabilidad = conf.getValor("oblga_contabilidad");
        String dir_matriz = conf.getValor("dir_matriz");
        String desc_comp = conf.getValor("desc_comp");
        
        Sucursal objSucursal = new Sucursal(this._ip, this._puerto, this._db, usuario, clave);
        String direccion_sucursal = objSucursal.getDireccion( String.valueOf(id_sucursal) );
        objSucursal.cerrar();

        Producto objProducto = new Producto(this._ip, this._puerto, this._db, usuario, clave);
        Iva objIva = new Iva(this._ip, this._puerto, this._db, usuario, clave);
        TablaImpuesto obTablaImpuesto = new TablaImpuesto(this._ip, this._puerto, this._db, usuario, clave);
        ResultSet rsImp = obTablaImpuesto.getTablaImpuestosVigente();
        String matImp[][] = Matriz.ResultSetAMatriz(rsImp);
        
        LiquidacionCompra objLiquidacionCompra = new LiquidacionCompra(this._ip, this._puerto, this._db, usuario, clave);

        ClavesContingenciaSRI objClavesSri = new ClavesContingenciaSRI(this._ip, this._puerto, this._db, usuario, clave);
        RetencionElectronica objRE = new RetencionElectronica();
        
        Empleado objEmpleado = new Empleado(this._ip, this._puerto, this._db, usuario, clave);
        String mailPara = objEmpleado.getMail(usuario);
        
        try {
            String r = "msg»Ha ocurrido un error inesperado, por favor, vuelva a intentarlo más tarde o " +
                "contáctese con el administrador del sistema para mayor información.";

            String WHERE = request.getParameter("WHERE");
            String p = request.getParameter("p") != null ? request.getParameter("p") : "0";
            String id = request.getParameter("id");
            String id_retencion_compra = request.getParameter("idRC");
            String imprimir = request.getParameter("imp");
            String serie_liquidacion = request.getParameter("sf");
            String autorizacion = request.getParameter("auf");
            String num_liquidacion = request.getParameter("nf");
            String id_proveedor = request.getParameter("coP");
            String fecha_compra = request.getParameter("fem");
            String observacion = request.getParameter("ob");
            String subtotal = request.getParameter("subt");
            String subtotal_2 = request.getParameter("subt2");
            String subtotal_0 = request.getParameter("subt0");
            String iva_2 = request.getParameter("iva2");
            String total_pagado = request.getParameter("tt");
            String doc_electronico = request.getParameter("elect");
            int tope = 0;
            try{
                tope = Integer.valueOf(request.getParameter("tope"));
            }catch(Exception e){}

            subtotal_0 = (subtotal_0.compareTo("")!=0) ? subtotal_0 : "0";
            subtotal_2 = (subtotal_2.compareTo("")!=0) ? subtotal_2 : "0";
            iva_2 = (iva_2.compareTo("")!=0) ? iva_2 : "0";
            
            
            
            /*  retenciones */
            String sustento_tributario = request.getParameter("sustento_tributario");
            String num_serie_ret = request.getParameter("sr");
            String autorizacion_ret = request.getParameter("autr");
            String num_retencion = request.getParameter("nr");
            String fecha_emision_ret = request.getParameter("fre");
            String ret_ejercicio_fiscal_mes = request.getParameter("efm");
            String ejercicio_fiscal = request.getParameter("ef");
            String total_retenido = request.getParameter("tvr");
            fecha_emision_ret = (fecha_emision_ret.compareTo("")!=0) ? "'"+fecha_emision_ret+"'" : "NULL";
            
            String numDocSustento = serie_liquidacion.replace("-", "") + Cadena.setSecuencial(num_liquidacion);
            
            
            /* actualizo datos de la factura del proveedor */
            Proveedor objProveedor = new Proveedor(this._ip, this._puerto, this._db, usuario, clave);
            String cxp = "";
            String prov_tipo_documento = "";
            String prov_ruc = "";
            String prov_razon_social = "";
            try{
                ResultSet rs = objProveedor.consulta("SELECT id_plan_cuenta, ruc, razon_social, "
                        + "case tipo_documento when '01' then '04' when '02' then '05' else '06' end as tipo_documento "
                        + "FROM tbl_proveedor where id_proveedor="+id_proveedor);
                if(rs.next()){
                    cxp = rs.getString("id_plan_cuenta")!=null ? rs.getString("id_plan_cuenta") : "";
                    prov_tipo_documento = rs.getString("tipo_documento")!=null ? rs.getString("tipo_documento") : "";
                    prov_ruc = rs.getString("ruc")!=null ? rs.getString("ruc") : "";
                    prov_razon_social = rs.getString("razon_social")!=null ? rs.getString("razon_social") : "";
                }
            }catch(Exception e){
                e.printStackTrace();
            }
            objProveedor.cerrar();
            
            
            
           
            String matParamAsientoAx[][] = null;
            String paramArtic = "";
            for(int i=0; i<=tope; i++){
                if(request.getParameter("idA"+i)!=null){
                    String id_producto = request.getParameter("idA"+i);
                    String subt = request.getParameter("st"+i);
                    String iva = request.getParameter("v"+i);
                    String tipo = "";
                    String id_iva = "2";
                    String id_plan_cuenta_compra = "";
                    try{
                        ResultSet rs = objProducto.getProducto(id_producto);
                        if(rs.next()){
                            tipo = rs.getString("tipo")!=null ? rs.getString("tipo") : "";
                            id_iva = rs.getString("id_iva")!=null ? rs.getString("id_iva") : "2";
                            id_plan_cuenta_compra = rs.getString("id_plan_cuenta_compra")!=null ? rs.getString("id_plan_cuenta_compra") : "";
                            rs.close();
                        }
                    }catch(Exception e){}
                    
                    matParamAsientoAx = Matriz.poner(matParamAsientoAx, new String[]{id_plan_cuenta_compra, subt, "0"});
                    
                    String cuenta = tipo.compareTo("s")==0 ? "id_plan_cuenta_comp_servicio" : "id_plan_cuenta_comp_bien";
                    String id_cuenta_iva = objIva.getIva(id_iva, cuenta);
                    if(Float.parseFloat(iva)>0){
                        matParamAsientoAx = Matriz.poner(matParamAsientoAx, new String[]{id_cuenta_iva, iva, "0"});
                    }
                    
                    paramArtic += "['"+id_producto+"', '"+request.getParameter("c"+i)+"', '"+request.getParameter("pu"+i)+"', '"+subt+
                            "', '0', '"+iva+"', '"+request.getParameter("t"+i)+"', '"+tipo+"'],";
                }
            }
            if(paramArtic.compareTo("")!=0){
                paramArtic = paramArtic.substring(0, paramArtic.length()-1);
            }

            
            
            /* retenciones */
                       
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
                    String cuenta = obTablaImpuesto.getIdCuentaCompras(idR);
                    matParamAsientoAx = Matriz.poner(matParamAsientoAx, new String[]{cuenta, "0", vr});
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
                num_serie_ret = "";
                autorizacion_ret = "";
                num_retencion = "0";
                fecha_emision_ret = "NULL";
                ret_ejercicio_fiscal_mes = "";
                ejercicio_fiscal = "NULL";
                total_retenido = "0";
            }
            
            
            float total = Float.parseFloat(total_pagado) - Float.parseFloat(total_retenido);
            matParamAsientoAx = Matriz.poner(matParamAsientoAx, new String[]{cxp, "0", String.valueOf(total)});
            
            

            String matParamAsiento[][] = Matriz.suprimirDuplicados(matParamAsientoAx, 0);
            
            String paramAsiento = "";
            for(int i=0; i<matParamAsiento.length; i++){
                paramAsiento += "['"+matParamAsiento[i][0]+"', '"+matParamAsiento[i][1]+"', '"+matParamAsiento[i][2]+"'],";
            }
            if(paramAsiento.compareTo("")!=0){
                paramAsiento = paramAsiento.substring(0, paramAsiento.length()-1);
            }
            
            String error = "";
            boolean ok = true;
            String estadoDocumento = "";
            String certificado = this._DOCS_ELECTRONICOS + "certificado.p12";
            String rutaSalida = this._DOCS_ELECTRONICOS + "firmados";
            String claveAcceso = "";
            String autorizacionXml = "";
            String respuestaAutoriz = "";
            String numAutorizacion = "";
            String id_liquidacion_id_compDiario = "-1:-1";
            
            if(id.compareTo("-1")==0){
                if(!objLiquidacionCompra.LiquidacionDuplicada(serie_liquidacion, num_liquidacion)){
                    if(!objLiquidacionCompra.LiquidacionHayEnMes(id_proveedor, fecha_compra)){
                        r = "msg»Ha ocurrido un error inesperado, por favor, vuelva a intentarlo más tarde o " +
                            "contáctese con el administrador del sistema para mayor información.";

                        if(doc_electronico.compareTo("0")==0 && paramRet.compareTo("")!=0){
                            ok = false;
                            String vecSerie[] = num_serie_ret.split("-");
                            claveAcceso = objRE.getClaveAcceso(Cadena.setFecha(fecha_emision_ret), "07", ruc, ambiente, vecSerie[0]+vecSerie[1], Cadena.setSecuencial(num_retencion), tipoEmision);
                            if(tipoEmision.compareTo("2")==0){  //   por indisponibilidad del sistema
                                claveAcceso = objClavesSri.getSigClave(Cadena.setFecha(fecha_emision_ret), "07", ruc, ambiente, tipoEmision);
                            }
                            objRE.generarXml(claveAcceso, ambiente, tipoEmision, razon_social, nombre_comercial, ruc, "07", vecSerie[0], vecSerie[1], Cadena.setSecuencial(num_retencion), dir_matriz,
                                    Cadena.setFecha(fecha_emision_ret), direccion_sucursal, num_resolucion, oblga_contabilidad, prov_tipo_documento, prov_razon_social, prov_ruc, 
                                    ret_ejercicio_fiscal_mes+"/"+ejercicio_fiscal, codigos, codsRetencion, basesImponibles, porcentajesRet, valoresRet, sustentos, numDocSustento );
                            String documentoXml = this._DOCS_ELECTRONICOS + "generados/" + claveAcceso + ".xml";
                            objRE.salvar(documentoXml);
                            error = objRE.getError();

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
                                                    // obtengo en numero de autorizacion
                                                    objRE.SetXml(autorizacionXml);
                                                    numAutorizacion = objRE.getValor("numeroAutorizacion");
                                                }else{
                                                    if(respuestaAutoriz.contains("RECHAZADO") || respuestaAutoriz.contains("NO AUTORIZADO")){
                                                        estadoDocumento = "n";
                                                        error = respuestaAutoriz;
                                                        StringBuilder mensaje = new StringBuilder();
                                                        mensaje.append("Estimado(a) ");
                                                        mensaje.append(empleado);
                                                        mensaje.append("<br /><br />Se ha encontrado un inconveniente en la emisi&oacute;n de la Retenci&oacute;n No. "+num_serie_ret+"-"+num_retencion+". El SRI ha devuelto el siguiente mensaje de error: <br />");
                                                        mensaje.append(error);
                                                        mensaje.append(".<br/><br />Favor de corregir el error y vuelva a enviar el comprobante al SRI.");
                                                        mensaje.append("<br/><br/>Att.<br/>ASESOR VIRTUAL <br />SAITEL");
                                                        Correo.enviar(this._SVR_MAIL, "info-tributaria@saitel.ec", mailPara, "contabilidad@saitel.ec", "", "NOTIFICACION DE NO AUTORIZACION", mensaje, true);
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
                                            claveAcceso = objClavesSri.getSigClave(Cadena.setFecha(fecha_emision_ret), "07", ruc, ambiente, tipoEmision);
                                            documentoXml = this._DOCS_ELECTRONICOS + "generados/" + claveAcceso + ".xml";
                                            objRE.salvar(documentoXml);
                                            error = objRE.getError();

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
                            id_liquidacion_id_compDiario = objLiquidacionCompra.insertar(id_sucursal, usuario, serie_liquidacion, autorizacion, num_liquidacion, id_proveedor, fecha_compra,
                                 observacion, subtotal, subtotal_0, subtotal_2, iva_2, total_pagado, "array[" + paramArtic + "]", sustento_tributario, num_serie_ret, autorizacion_ret, num_retencion, 
                                 fecha_emision_ret, ret_ejercicio_fiscal_mes, ejercicio_fiscal, total_retenido, "array[" + paramRet + "]::varchar[]", "array[" + paramAsiento + "]", id_rol);
                            if(id_liquidacion_id_compDiario.compareTo("-1:-1") != 0){
                                String vec[] = id_liquidacion_id_compDiario.split(":");

                                objLiquidacionCompra.setEstadoDocumento(vec[0], estadoDocumento, claveAcceso, autorizacionXml, numAutorizacion);

                                Auditoria auditoria = new Auditoria(this._ip, this._puerto, this._db, usuario, clave);
                                auditoria.setRegistro(request, "INGRESO DE NUEVA LIQUIDACION DE COMPRA O SERVICIO: "+serie_liquidacion+"-"+num_liquidacion);
                                auditoria.cerrar();

                                r = "err»0^vta»vta1^fun»imprimir('pdfLiquidacionCompra?id="+vec[0]+"');"+(imprimir.compareTo("1")==0?"imprimir('pdfComprobanteDiario?id="+vec[1]+"');":"")+
                                    "^tbl»"+objLiquidacionCompra.paginar("vta_liquidacion_compra", "id_liquidacion_compra,razon_social,numero_liquidacion,toDateSQL(fecha_emision),total_compra,txt_anulado,txt_estado_documento,mensaje", WHERE, Integer.parseInt(p), 100)+
                                    "^msg»liquidación de compra registrada satisfactoriamente.";
                            }else{
                                r = "msg»" + objLiquidacionCompra.getError();
                            }
                        }
                    }else{
                        r = "msg»Ya se ha emitido una liquidación de compra o servicio para el proveedor seleccionado en éste mes.";
                    }
                }else{
                    r = "msg»La liquidación de compra o servicio con número "+serie_liquidacion+"-"+num_liquidacion+" ya ha sido emitida.";
                }
                
            }else{   // cuando es edicion
                
                if(doc_electronico.compareTo("0")==0 && paramRet.compareTo("")!=0){
                    ok = false;
                    claveAcceso = objLiquidacionCompra.getClaveAcceso(id_retencion_compra);
                    String vecSerie[] = num_serie_ret.split("-");
                    
                    objRE.generarXml(claveAcceso, ambiente, tipoEmision, razon_social, nombre_comercial, ruc, "07", vecSerie[0], vecSerie[1], Cadena.setSecuencial(num_retencion), dir_matriz,
                            Cadena.setFecha(fecha_emision_ret), direccion_sucursal, num_resolucion, oblga_contabilidad, prov_tipo_documento, prov_razon_social, prov_ruc, 
                            ret_ejercicio_fiscal_mes+"/"+ejercicio_fiscal, codigos, codsRetencion, basesImponibles, porcentajesRet, valoresRet, sustentos, numDocSustento );
                    String documentoXml = this._DOCS_ELECTRONICOS + "generados/" + claveAcceso + ".xml";
                    objRE.salvar(documentoXml);
                    error = objRE.getError();

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
                                            // obtengo en numero de autorizacion
                                            objRE.SetXml(autorizacionXml);
                                            numAutorizacion = objRE.getValor("numeroAutorizacion");
                                        }else{
                                            if(respuestaAutoriz.contains("RECHAZADO") || respuestaAutoriz.contains("NO AUTORIZADO")){
                                                estadoDocumento = "n";
                                                error = respuestaAutoriz;
                                                StringBuilder mensaje = new StringBuilder();
                                                mensaje.append("Estimado(a) ");
                                                mensaje.append(empleado);
                                                mensaje.append("<br /><br />Se ha encontrado un inconveniente en la emisi&oacute;n de la Retenci&oacute;n No. "+num_serie_ret+"-"+num_retencion+". El SRI ha devuelto el siguiente mensaje de error: <br />");
                                                mensaje.append(error);
                                                mensaje.append(".<br/>Favor de corregir el error y vuelva a enviar el comprobante al SRI.");
                                                mensaje.append("<br/><br/>Att.<br/>ASESOR VIRTUAL <br />SAITEL");
                                                Correo.enviar(this._SVR_MAIL, "info-tributaria@saitel.ec", mailPara, "contabilidad@saitel.ec", "", "NOTIFICACION DE NO AUTORIZACION", mensaje, true);
                                                //ok=false;
                                            }
                                        }

                                        //ArchivoXML.delete();
                                    } else if (respuestaRecepcion.getEstado().equals("DEVUELTA")) {
                                        error = EnvioComprobantesWS.obtenerMensajeRespuesta(respuestaRecepcion);
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
                    boolean actOk = objLiquidacionCompra.actualizar(id_retencion_compra, sustento_tributario, ret_ejercicio_fiscal_mes, 
                            ejercicio_fiscal, total_retenido, paramRet);
                    if(actOk){
                        objLiquidacionCompra.setEstadoDocumento(id, estadoDocumento, claveAcceso, autorizacionXml, numAutorizacion);

                        Auditoria auditoria = new Auditoria(this._ip, this._puerto, this._db, usuario, clave);
                        auditoria.setRegistro(request, "ACTUALIZACION DE RETENCION DE COMPRA: "+serie_liquidacion+"-"+num_liquidacion);
                        auditoria.cerrar();

                        r = "err»0^vta»vta1^tbl»"+objLiquidacionCompra.paginar("vta_liquidacion_compra", "id_liquidacion_compra,razon_social,numero_liquidacion,toDateSQL(fecha_emision),total_compra,txt_anulado,txt_estado_documento,mensaje", WHERE, Integer.parseInt(p), 100)+
                            "^msg»liquidación de compra actualizada satisfactoriamente.";
                    }else{
                        r = "msg»" + objLiquidacionCompra.getError();
                    }
                }
            }
            
            out.print(r);
            
        }catch(Exception e){
            e.printStackTrace();
        }finally {
            conf.setValor("bloqueo_libros", "false");
            conf.cerrar();
            objLiquidacionCompra.cerrar();
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
