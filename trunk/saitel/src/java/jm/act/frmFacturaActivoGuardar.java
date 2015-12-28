/**
** @version 1.0
** @package FACTURAPYMES.
* @author Jorge Washington Mueses Cevallos.
* @copyright Copyright (C) 2011 por Jorge Mueses. Todos los derechos reservados.
* @license http://www.gnu.org/copyleft/gpl.html GNU/GPL.
** FACTURAPYMES es un software de libre distribución, que puede ser
* copiado y distribuido bajo los términos de la Licencia
* Attribution-NonCommercial-NoDerivs 3.0 Unported,
* de acuerdo con la publicada por la CREATIVE COMMONS CORPORATION.
*/

package jm.act;

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
import jm.act.clas.Activo;
import jm.adm.clas.Configuracion;
import jm.adm.clas.Iva;
import jm.adm.clas.Sucursal;
import jm.adm.clas.TablaDepreciacion;
import jm.adm.clas.TablaImpuesto;
import jm.cnt.clas.ClavesContingenciaSRI;
import jm.inv.clas.FacturaCompra;
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
public class frmFacturaActivoGuardar extends HttpServlet {
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
        String cuenta_propina = conf.getValor("propina");

        FacturaCompra objFacturaCompra = new FacturaCompra(this._ip, this._puerto, this._db, usuario, clave);
        Activo objActivo = new Activo(this._ip, this._puerto, this._db, usuario, clave);

        Producto objProducto = new Producto(this._ip, this._puerto, this._db, usuario, clave);
        Iva objIva = new Iva(this._ip, this._puerto, this._db, usuario, clave);
        TablaDepreciacion objTablaDepreciacion = new TablaDepreciacion(this._ip, this._puerto, this._db, usuario, clave);
        TablaImpuesto obTablaImpuesto = new TablaImpuesto(this._ip, this._puerto, this._db, usuario, clave);
        ResultSet rsImp = obTablaImpuesto.getTablaImpuestosVigente();
        String matImp[][] = Matriz.ResultSetAMatriz(rsImp);
        
        ClavesContingenciaSRI objClavesSri = new ClavesContingenciaSRI(this._ip, this._puerto, this._db, usuario, clave);
        RetencionElectronica objRE = new RetencionElectronica();
        
        Empleado objEmpleado = new Empleado(this._ip, this._puerto, this._db, usuario, clave);
        String mailPara = objEmpleado.getMail(usuario);
        
        Sucursal objSucursal = new Sucursal(this._ip, this._puerto, this._db, usuario, clave);
        String direccion_sucursal = objSucursal.getDireccion( String.valueOf(id_sucursal) );
        objSucursal.cerrar();
        
        
        
        String error = "";
        
        try {
            String r = "msg»Ha ocurrido un error inesperado, por favor, vuelva a intentarlo más tarde o " +
                "contáctese con el administrador del sistema para mayor información.";

            String WHERE = request.getParameter("WHERE");
            String p = request.getParameter("p") != null ? request.getParameter("p") : "0";
            String id = request.getParameter("id");
            String imprimir = request.getParameter("imp");
            String doc_electronico = request.getParameter("elect");
            //String id_comprobante_diario = request.getParameter("idCD");
            String serie_factura = request.getParameter("sf");
            String autorizacion = request.getParameter("auf");
            String num_factura = request.getParameter("nf");
            String id_proveedor = request.getParameter("coP");
            String fecha_compra = request.getParameter("fc");
            String fecha_ven_factura = request.getParameter("fv");
            String id_bodega = request.getParameter("id_bodega");
            String observacion = request.getParameter("ob");
            String subtotal = request.getParameter("subt");
            String subtotal_0 = request.getParameter("subt0");
            String subtotal_2 = request.getParameter("subt2");
            String subtotal_6 = request.getParameter("subt6");
            String descuento = request.getParameter("dt");
            String iva_2 = request.getParameter("iva2");
            float propina = Float.parseFloat( request.getParameter("propina") );
            String total_pagado = request.getParameter("tt");
            int topeA = Integer.valueOf(request.getParameter("topeA"));
            int topeP = Integer.valueOf(request.getParameter("topeP"));
            String contabilizar = request.getParameter("contabilizar");

            // retenciones 
            String sustento_tributario = request.getParameter("sustento_tributario");
            String num_serie_ret = request.getParameter("sr");
            String autorizacion_ret = request.getParameter("autr");
            String num_retencion = request.getParameter("nr");
            String fecha_emision_ret = request.getParameter("fre");
            String ret_ejercicio_fiscal_mes = request.getParameter("efm");
            String ejercicio_fiscal = request.getParameter("ef");
            String total_retenido = request.getParameter("tvr");
            total_retenido = (total_retenido.compareTo("")!=0) ? total_retenido : "0";
            fecha_emision_ret = (fecha_emision_ret.compareTo("")!=0) ? "'"+fecha_emision_ret+"'" : "NULL";
            
            descuento = (descuento.compareTo("")!=0) ? descuento : "0";
            subtotal_0 = (subtotal_0.compareTo("")!=0) ? subtotal_0 : "0";
            iva_2 = (iva_2.compareTo("")!=0) ? iva_2 : "0";

            String numDocSustento = serie_factura.replace("-", "") + Cadena.setSecuencial(num_factura);
            
            String matParamAsientoAx[][] = null;
            //float ivas_prod = 0;
            //float ivas_serv = 0;
            //float ivas_bien = 0;
            //float ivas_gasto = 0;
            String paramArtic = "";
            for(int i=0; i<=topeP; i++){
                if(request.getParameter("idA"+i)!=null){
                    String id_producto = request.getParameter("idA"+i);
                    String subt = request.getParameter("st"+i);
                    String iva = request.getParameter("v"+i);
                    String tipo = "";
                    String id_iva = "2";
                    String porcentaje = "12";
                    String id_plan_cuenta_compra = "";
                    try{
                        ResultSet rs = objProducto.getProducto(id_producto);
                        if(rs.next()){
                            tipo = rs.getString("tipo")!=null ? rs.getString("tipo") : "";
                            id_iva = rs.getString("id_iva")!=null ? rs.getString("id_iva") : "2";
                            porcentaje = rs.getString("porcentaje")!=null ? rs.getString("porcentaje") : "12";
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
                    
                    /*if(tipo.compareTo("p")==0){
                        ivas_prod += Float.parseFloat(iva);
                    }
                    if(tipo.compareTo("s")==0){
                        ivas_serv += Float.parseFloat(iva);
                    }
                    if(tipo.compareTo("b")==0){
                        ivas_bien += Float.parseFloat(iva);
                    }
                    if(tipo.compareTo("g")==0){
                        ivas_gasto += Float.parseFloat(iva);
                    }*/
                    paramArtic += "['"+id_producto+"', '"+request.getParameter("c"+i)+"', '"+request.getParameter("pu"+i)+"', '"+subt+
                            "', '"+request.getParameter("d"+i)+"', '"+iva+"', '"+request.getParameter("t"+i)+
                            "', 'FALSE', '-', '0', 'CAT-0', '-', '0', '"+porcentaje+"'],";
                }
            }
            
            String cod_activos = "";
            for(int i=0; i<=topeA; i++){
                if(request.getParameter("cod"+i)!=null){
                    String id_tabla_depreciacion = request.getParameter("idDep"+i);
                    String subt = request.getParameter("Ast"+i);
                    String iva = request.getParameter("Av"+i);
                    String id_plan_cuenta_grupo = "";
                    try{
                        ResultSet rs = objTablaDepreciacion.getDepreciacion(id_tabla_depreciacion);
                        if(rs.next()){
                            id_plan_cuenta_grupo = rs.getString("id_plan_cuenta_grupo")!=null ? rs.getString("id_plan_cuenta_grupo") : "";
                            rs.close();
                        }
                    }catch(Exception e){
                        e.printStackTrace();
                    }
                    
                    matParamAsientoAx = Matriz.poner(matParamAsientoAx, new String[]{id_plan_cuenta_grupo, subt, "0"});
                    
                    //String cuenta = tipo.compareTo("s")==0 ? "id_plan_cuenta_comp_servicio" : "id_plan_cuenta_comp_bien";
                    String id_cuenta_iva = objIva.getIdPCIva(request.getParameter("Api"+i), "id_plan_cuenta_comp_bien");
                    if(Float.parseFloat(iva)>0){
                        matParamAsientoAx = Matriz.poner(matParamAsientoAx, new String[]{id_cuenta_iva, iva, "0"});
                    }
                    
                    cod_activos += request.getParameter("cod"+i) + ",";
                    paramArtic += "['"+request.getParameter("cod"+i).toUpperCase()+"', '"+request.getParameter("Ac"+i)+"', '"+request.getParameter("Apu"+i)+
                        "', '"+subt+"', '"+request.getParameter("Ad"+i)+"', '"+iva+
                        "', '"+request.getParameter("At"+i)+"', 'FALSE', '"+request.getParameter("des"+i)+"', '"+id_tabla_depreciacion+
                        "', '"+request.getParameter("idCat"+i)+"', '"+(request.getParameter("ub"+i).compareTo("")!=0 ? request.getParameter("ub"+i) : "-")+
                        "', '1', '"+request.getParameter("Api"+i)+"'],";
                }
            }
            
            if(paramArtic.compareTo("")!=0){
                paramArtic = paramArtic.substring(0, paramArtic.length()-1);
                cod_activos = cod_activos.substring(0, cod_activos.length()-1);
            }

            

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
            objProveedor.actualizar(id_proveedor, autorizacion, serie_factura, fecha_ven_factura);
            objProveedor.cerrar();
            
            
            
            if(propina>0){
                matParamAsientoAx = Matriz.poner(matParamAsientoAx, new String[]{cuenta_propina, String.valueOf(propina), "0"});
            }
            /*if(ivas_prod>0){
                matParamAsientoAx = Matriz.poner(matParamAsientoAx, new String[]{iva_comp, String.valueOf(ivas_prod), "0"});
            }
            if(ivas_serv>0){
                matParamAsientoAx = Matriz.poner(matParamAsientoAx, new String[]{iva_comp_serv, String.valueOf(ivas_serv), "0"});
            }
            if(ivas_bien>0){
                matParamAsientoAx = Matriz.poner(matParamAsientoAx, new String[]{iva_comp_bien, String.valueOf(ivas_bien), "0"});
            }
            if(ivas_gasto>0){
                matParamAsientoAx = Matriz.poner(matParamAsientoAx, new String[]{iva_comp_gasto, String.valueOf(ivas_gasto), "0"});
            }*/
            
            float total = Float.parseFloat(total_pagado) - Float.parseFloat(total_retenido) - propina;
            matParamAsientoAx = Matriz.poner(matParamAsientoAx, new String[]{cxp, "0", String.valueOf(total)});
            
            if( Float.parseFloat(descuento)>0 ){
                matParamAsientoAx = Matriz.poner(matParamAsientoAx, new String[]{desc_comp, "0", descuento});
            }
            
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
            
            String matParamAsiento[][] = Matriz.suprimirDuplicados(matParamAsientoAx, 0);
            
            String paramAsiento = "";
            for(int i=0; i<matParamAsiento.length; i++){
                if( matParamAsiento[i][1].compareTo("0")!=0 || matParamAsiento[i][2].compareTo("0")!=0 ){
                    paramAsiento += "['"+matParamAsiento[i][0]+"', '"+matParamAsiento[i][1]+"', '"+matParamAsiento[i][2]+"'],";
                }
            }
            if(paramAsiento.compareTo("")!=0){
                paramAsiento = paramAsiento.substring(0, paramAsiento.length()-1);
            }
            
            
            boolean ok = true;
            
            String id_factura_id_compDiario = "-1:-1:-1";
            String numAutorizacion ="";

            if(!objFacturaCompra.retencionDuplicada(id, num_serie_ret, num_retencion)){

                String cods_repetidos = objActivo.codsDuplicados(cod_activos);
                if(cods_repetidos.compareTo("-1")==0){
                    if(id.compareTo("-1")==0 && contabilizar.compareTo("true")==0){  // nuevo y contabilizar
                        /*
                        String estadoDocumento = "";
                        String certificado = this._DOCS_ELECTRONICOS + "certificado.p12";
                        String rutaSalida = this._DOCS_ELECTRONICOS + "firmados";
                        String claveAcceso = "";
                        String autorizacionXml = "";
                        String respuestaAutoriz = "";
                        if(doc_electronico.compareTo("0")==0){
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
                                                        mensaje.append(".<br/>Favor de corregir el error y vuelva a enviar el comprobante al SRI.");
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
                                                objClavesSri.setConsumida(claveAcceso, ambiente);
                                                StringBuilder mensaje = new StringBuilder();
                                                mensaje.append("Estimado(a) ");
                                                mensaje.append(empleado);
                                                mensaje.append("<br /><br />Se ha encontrado un inconveniente en la emisi&oacute;n de la Retenci&oacute;n No. "+num_serie_ret+"-"+num_retencion+". El SRI ha devuelto el siguiente mensaje de error: <br />");
                                                mensaje.append(error);
                                                mensaje.append(".<br/>Favor de corregir el error y vuelva a enviar el comprobante al SRI.");
                                                mensaje.append("<br/><br/>Att.<br/>ASESOR VIRTUAL <br />SAITEL");
                                                Correo.enviar(this._SVR_MAIL, "info-tributaria@saitel.ec", mailPara, "contabilidad@saitel.ec", "", "NOTIFICACION DE NO AUTORIZACION", mensaje, true);
                                                //ok = true;
                                            }
                                            //error = "Error al tratar de enviar el comprobante hacia el SRI. ";
                                        }
                                    }else{
                                        autorizacionXml = this.getStringFromFile(this._DOCS_ELECTRONICOS + "firmados/" + claveAcceso + ".xml");
                                        objClavesSri.setConsumida(claveAcceso, ambiente);
                                        ok = true;
                                    }
                                }
                            }
                        }
                        r = "msg»"+error;

                        if(ok){
                            id_factura_id_compDiario = objActivo.insertar(id_sucursal, serie_factura, autorizacion, num_factura, id_proveedor, fecha_compra, fecha_ven_factura,
                                    observacion, subtotal, subtotal_0, subtotal_2, subtotal_6, descuento, iva_2, propina, total_pagado, "array[" + paramArtic + "]", sustento_tributario, num_serie_ret, autorizacion_ret, 
                                    num_retencion, fecha_emision_ret, ret_ejercicio_fiscal_mes, ejercicio_fiscal, total_retenido, "array[" + paramRet + "]::varchar[]", "array[" + paramAsiento + "]", id_rol, id_bodega, autorizacionXml);
                            if(id_factura_id_compDiario.compareTo("-1:-1:-1") != 0){
                                
                                String vec[] = id_factura_id_compDiario.split(":");
                                
                                objFacturaCompra.setEstadoDocumento(vec[0], estadoDocumento, claveAcceso, autorizacionXml);
                                
                                Auditoria auditoria = new Auditoria(this._ip, this._puerto, this._db, usuario, clave);
                                auditoria.setRegistro(request, "INGRESO DE NUEVA FACTURA DE COMPRA: "+serie_factura+"-"+num_factura);
                                auditoria.cerrar();
                                r = "err»0^vta»cmp^fun»act_compraEditar("+vec[0]+");imprimir('pdfPersonalizacion?id="+vec[2]+"');"+(imprimir.compareTo("1")==0?"imprimir('pdfComprobanteDiario?id="+vec[1]+"');":"")+
                                    "^tbl»"+objFacturaCompra.paginar("vta_factura_compra", "id_factura_compra,razon_social,numero_factura,toDateSQL(fecha_compra),total_compra,txt_anulado", WHERE, Integer.parseInt(p), 100)+
                                    "^msg»Factura de compra registrada satisfactoriamente";
                            }else{
                                r = "msg»"+objFacturaCompra.getError();
                            }
                        }
                        */
                        
                    }else{  //solo guardar
                        if(id.compareTo("-1")!=0 && contabilizar.compareTo("true")==0){  /*  no nuevo y contabilizar */
                            
                            String estadoDocumento = "";
                            String certificado = this._DOCS_ELECTRONICOS + "certificado.p12";
                            String rutaSalida = this._DOCS_ELECTRONICOS + "firmados";
                            String claveAcceso = "";
                            String autorizacionXml = "";
                            String respuestaAutoriz = "";
                            if(doc_electronico.compareTo("0")==0){
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
                                                            mensaje.append(".<br/>Favor de corregir el error y vuelva a enviar el comprobante al SRI.");
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
                                id_factura_id_compDiario = objActivo.contabilizar(id, id_sucursal, serie_factura, autorizacion, num_factura, id_proveedor, fecha_compra, fecha_ven_factura,
                                        observacion, subtotal, subtotal_0, subtotal_2, subtotal_6, descuento, iva_2, propina, total_pagado, "array[" + paramArtic + "]", sustento_tributario, num_serie_ret, autorizacion_ret, 
                                        num_retencion, fecha_emision_ret, ret_ejercicio_fiscal_mes, ejercicio_fiscal, total_retenido, "array[" + paramRet + "]::varchar[]", 
                                        "array[" + paramAsiento + "]", id_rol, id_bodega, autorizacionXml);
                                if(id_factura_id_compDiario.compareTo("-1:-1:-1") != 0){
                                    
                                    String vec[] = id_factura_id_compDiario.split(":");
                                    //String documento = objFacturaCompra.getTipoDocumento(id);
                                    objFacturaCompra.setEstadoDocumento(vec[0], "f", estadoDocumento, claveAcceso, autorizacionXml, numAutorizacion);
                                    
                                    Auditoria auditoria = new Auditoria(this._ip, this._puerto, this._db, usuario, clave);
                                    auditoria.setRegistro(request, "CONTABILIZACION DE FACTURA DE COMPRA: "+serie_factura+"-"+num_factura);
                                    auditoria.cerrar();
                                    r = "err»0^vta»vta1^fun»act_compraEditar("+vec[0]+");imprimir('pdfPersonalizacion?id="+vec[2]+"');"+(imprimir.compareTo("1")==0?"imprimir('pdfComprobanteDiario?id="+vec[1]+"');":"")+
                                        "^tbl»"+objActivo.paginar("vta_factura_compra_retencion", "id_factura_compra,razon_social,numero_factura,toDateSQL(fecha_compra),total_compra,txt_estado_documento,mensaje", WHERE, Integer.parseInt(p), 100)+
                                        "^msg»Factura de compra registrada satisfactoriamente.";
                                }else{
                                    r = "msg»" + objActivo.getError();
                                }
                            }
                            
                        }else{
                            if(id.compareTo("-1")==0 && contabilizar.compareTo("false")==0){  /* nuevo y no contabilizar */
                                String id_factura_compra = objActivo.guardar(id_sucursal, serie_factura, autorizacion, num_factura, id_proveedor, fecha_compra, fecha_ven_factura,
                                    observacion, subtotal, subtotal_0, subtotal_2, subtotal_6, descuento, iva_2, propina, total_pagado, paramArtic, num_serie_ret, autorizacion_ret, 
                                    num_retencion, fecha_emision_ret, ret_ejercicio_fiscal_mes, ejercicio_fiscal, total_retenido, paramRet, id_bodega);
                                if(id_factura_compra.compareTo("-1")!=0){
                                    Auditoria auditoria = new Auditoria(this._ip, this._puerto, this._db, usuario, clave);
                                    auditoria.setRegistro(request, "REGISTRO DE FACTURA DE COMPRA: "+serie_factura+"-"+num_factura);
                                    auditoria.cerrar();
                                    r = "err»0^vta»vta1^fun»act_compraEditar("+id_factura_compra+");^tbl»"+
                                            objFacturaCompra.paginar("vta_factura_compra_retencion", "id_factura_compra,razon_social,numero_factura,toDateSQL(fecha_compra),total_compra,txt_estado_documento,mensaje", WHERE, Integer.parseInt(p), 100)+
                                        "^msg»Factura de compra guardada satisfactoriamente.";
                                }else{
                                    r = "msg»" + objActivo.getError();
                                }
                            }else{ /* no nuevo y no contabilizar */
                                if(objActivo.actualizar(id, id_sucursal, serie_factura, autorizacion, num_factura, id_proveedor, fecha_compra, fecha_ven_factura,
                                    observacion, subtotal, subtotal_0, subtotal_2, subtotal_6, descuento, iva_2, propina, total_pagado, paramArtic, num_serie_ret, autorizacion_ret, 
                                    num_retencion, fecha_emision_ret, ret_ejercicio_fiscal_mes, ejercicio_fiscal, total_retenido, paramRet, id_bodega)){
                                    Auditoria auditoria = new Auditoria(this._ip, this._puerto, this._db, usuario, clave);
                                    auditoria.setRegistro(request, "ACTUALIZACION DE FACTURA DE COMPRA: "+serie_factura+"-"+num_factura);
                                    auditoria.cerrar();
                                    r = "tbl»"+objFacturaCompra.paginar("vta_factura_compra_retencion", "id_factura_compra,razon_social,numero_factura,toDateSQL(fecha_compra),total_compra,txt_estado_documento,mensaje", WHERE, Integer.parseInt(p), 100)+
                                        "^msg»Factura de compra guardada satisfactoriamente.";
                                }else{
                                    r = "msg»" + objActivo.getError();
                                }
                            }
                        }
                    }

                }else{
                    r = "msg»Los códigos de los activos "+cods_repetidos+" ya han sido asignados.";
                }

            }else{
                r = "msg»La retención con número "+num_serie_ret+"-"+num_retencion+" ya ha sido emitida.";
            }

            out.print(r);
        } finally {
            conf.setValor("bloqueo_libros", "false");
            conf.cerrar();
            objEmpleado.cerrar();
            objTablaDepreciacion.cerrar();
            objFacturaCompra.cerrar();
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
