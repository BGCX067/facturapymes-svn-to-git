/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jm.inv;

import ec.gob.sri.FirmaXadesBes;
import ec.gob.sri.wsc.AutorizacionComprobantesWS;
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
import jm.adm.clas.TablaImpuesto;
import jm.cnt.clas.ClavesContingenciaSRI;
import jm.inv.clas.FacturaCompra;
import jm.inv.clas.Proveedor;
import jm.inv.clas.RetencionElectronica;
import jm.nom.clas.Empleado;
import jm.seg.clas.Auditoria;
import jm.web.Cadena;
import jm.web.Correo;
import jm.web.Matriz;

/**
 *
 * @author jorge
 */
public class frmRetencionActualizar extends HttpServlet {

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
        String id_rol = (String)sesion.getAttribute("id_rol");
        //int id_sucursal = (Integer)sesion.getAttribute("sucursal");
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
        
        String WHERE = request.getParameter("WHERE");
        String p = request.getParameter("p") != null ? request.getParameter("p") : "0";
        String id_factura = request.getParameter("id");
        String id_sucursal = request.getParameter("id_sucursal_doc");
        String serie_factura = request.getParameter("sf");
        String num_factura = request.getParameter("nf");
        String doc_electronico = request.getParameter("elect");
        String id_proveedor = request.getParameter("coP");
        String id_retencion_compra = request.getParameter("idRC");
        String sustento_tributario = request.getParameter("sustento_tributario");
        String num_serie_ret = request.getParameter("sr");
        //String autorizacion_ret = request.getParameter("autr");
        String num_retencion = request.getParameter("nr");
        String fecha_emision_ret = request.getParameter("fre");
        String ret_ejercicio_fiscal_mes = request.getParameter("efm");
        String ejercicio_fiscal = request.getParameter("ef");
        String total_retenido = request.getParameter("tvr");
        String funcion = request.getParameter("funct");
        String genNewClave = request.getParameter("genNewClave");
        total_retenido = (total_retenido.compareTo("")!=0) ? total_retenido : "0";
        fecha_emision_ret = (fecha_emision_ret.compareTo("")!=0) ? "'"+fecha_emision_ret+"'" : "NULL";
        
        String numDocSustento = serie_factura.replace("-", "") + Cadena.setSecuencial(num_factura);
        
        Configuracion conf = new Configuracion(this._ip, this._puerto, this._db, usuario, clave);
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
        
        RetencionElectronica objRE = new RetencionElectronica();
        
        Empleado objEmpleado = new Empleado(this._ip, this._puerto, this._db, usuario, clave);
        String mailPara = objEmpleado.getMail(usuario);
        
        Sucursal objSucursal = new Sucursal(this._ip, this._puerto, this._db, usuario, clave);
        String direccion_sucursal = objSucursal.getDireccion( id_sucursal );
        objSucursal.cerrar();
        
        TablaImpuesto obTablaImpuesto = new TablaImpuesto(this._ip, this._puerto, this._db, usuario, clave);
        ResultSet rsImp = obTablaImpuesto.getTablaImpuestosVigente();
        String matImp[][] = Matriz.ResultSetAMatriz(rsImp);
            
        ClavesContingenciaSRI objClavesSri = new ClavesContingenciaSRI(this._ip, this._puerto, this._db, usuario, clave);
        
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

        String autorizacion_ret = "";
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
                //String cuenta = obTablaImpuesto.getIdCuentaCompras(idR);
                //matParamAsientoAx = Matriz.poner(matParamAsientoAx, new String[]{cuenta, "0", vr});
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
        
        boolean ok = false;
        String estadoDocumento = "g";
        String certificado = this._DOCS_ELECTRONICOS + "certificado.p12";
        String rutaSalida = this._DOCS_ELECTRONICOS + "firmados";
        String claveAcceso = "";
        String autorizacionXml = "";
        String respuestaAutoriz = "";
        String numAutorizacion = "";
        String error="";
        String r = "";
        
        try {
            if(doc_electronico.compareTo("0")==0 && paramRet.compareTo("")!=0){
                ok = false;
                claveAcceso = objFacturaCompra.getClaveAcceso(id_retencion_compra);
                String vecSerie[] = num_serie_ret.split("-");
                
                if(genNewClave.compareTo("true")==0){
                    claveAcceso = objRE.getClaveAcceso(Cadena.setFecha(fecha_emision_ret), "07", ruc, ambiente, vecSerie[0]+vecSerie[1], Cadena.setSecuencial(num_retencion), tipoEmision);
                    if(tipoEmision.compareTo("2")==0){  //   por indisponibilidad del sistema
                        claveAcceso = objClavesSri.getSigClave(Cadena.setFecha(fecha_emision_ret), "07", ruc, ambiente, tipoEmision);
                    }
                    objFacturaCompra.setClaveAcceso(id_factura, claveAcceso);
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
                boolean actOk = objFacturaCompra.actualizar(id_retencion_compra, num_retencion, fecha_emision_ret, sustento_tributario, 
                        ret_ejercicio_fiscal_mes, ejercicio_fiscal, total_retenido, paramRet);
                if(actOk){
                    String documento = objFacturaCompra.getTipoDocumento(id_retencion_compra);
                    objFacturaCompra.setEstadoDocumento(id_factura, documento, estadoDocumento, claveAcceso, autorizacionXml, numAutorizacion);

                    Auditoria auditoria = new Auditoria(this._ip, this._puerto, this._db, usuario, clave);
                    auditoria.setRegistro(request, "ACTUALIZACION DE RETENCION DE DE COMPRA: "+num_serie_ret+"-"+num_retencion);
                    auditoria.cerrar();

                    r = "err»0^vta»vta1^fun»_R('cmp');_R('bloq_cmp');"+funcion+"^msg»Retención de compra actualizada satisfactoriamente.";
                }else{
                    r = "msg»" + objFacturaCompra.getError();
                }
            }
            
            out.print(r);
        }finally{
            obTablaImpuesto.cerrar();
            conf.cerrar();
            objFacturaCompra.cerrar();
            objEmpleado.cerrar();
            objClavesSri.cerrar();
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
