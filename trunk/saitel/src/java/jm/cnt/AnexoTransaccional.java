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

package jm.cnt;

import java.io.*;

import javax.servlet.*;
import javax.servlet.http.*;
import java.sql.ResultSet;
import java.util.regex.*;
import jm.adm.clas.Configuracion;
import jm.web.Addons;
import jm.web.Fecha;
import jm.web.DataBase;

/**
 *
 * @author Jorge
 */
public class AnexoTransaccional extends HttpServlet {
    private String _ip = null;
    private int _puerto = 5432;
    private String _db = null;

    public void init(ServletConfig config) throws ServletException
    {
        this._ip = config.getServletContext().getInitParameter("_IP");
        this._puerto = Integer.parseInt(config.getServletContext().getInitParameter("_PUERTO"));
        this._db = config.getServletContext().getInitParameter("_DB");
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
        
        int anio = Integer.valueOf(request.getParameter("a"));
        int mes = Integer.valueOf(request.getParameter("m"));
        String mes1 = (mes<10) ? "0"+mes : String.valueOf(mes);
        int fin_mes = Fecha.getUltimoDiaMes(anio, mes);

        response.setContentType("text/xml;");
        response.setHeader("Content-disposition", "attachment; filename="+this._db.replace("db_", "")+"_AT_"+Addons.getMesSRI(mes)+anio+".xml;");
        response.setHeader("Pragma", "no-cache");
        response.setHeader("Expires", "Mon, 01 Jan 2001 00:00:01 GMT");
        response.setHeader("Cache-Control", "no-store");
        response.setHeader("Cache-Control", "must-revalidate");
        response.setHeader("Cache-Control", "no-cache");
        PrintWriter out = response.getWriter();


        Configuracion conf = new Configuracion(this._ip, this._puerto, this._db, usuario, clave);
        String ruc = conf.getValor("ruc");
        String razon_social = conf.getValor("razon_social").toUpperCase();
        if(razon_social.length()>=60){
            razon_social = razon_social.substring(0, 59);
        }


        DataBase objDB = new DataBase(this._ip, this._puerto, this._db, usuario, clave);


        Pattern pasaporte = Pattern.compile("[0-9a-zA-Z]{0,13}");
        Matcher m_pasaporte;
        try {
            out.println("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>");
            out.println("<iva>");
            out.println("<TipoIDInformante>R</TipoIDInformante>");
            out.println("<IdInformante>"+ruc+"</IdInformante>");
            out.println("<razonSocial>"+razon_social+"</razonSocial>");
            out.println("<Anio>"+anio+"</Anio>");
            out.println("<Mes>"+mes1+"</Mes>");
            
            int num_establecimientos = 0;
            float totales_ventas = 0;
            float total_notas_credito = 0;
            try{
                ResultSet rsVentas = objDB.consulta("select count(distinct substr(serie_factura, 0, 4)) as num_establecimientos, sum(subtotal) - sum(descuento) as ventas "
                        + "from vta_factura_venta where fecha_emision between '"+anio+"-"+mes+"-01' and '"+anio+"-"+mes+"-"+fin_mes+"' and anulado=false;");
                if(rsVentas.next()){
                    num_establecimientos = rsVentas.getString(1)!=null?rsVentas.getInt(1):0;
                    totales_ventas = rsVentas.getString(2)!=null?rsVentas.getInt(2):0;
                    rsVentas.close();
                }
            }catch(Exception ex){ 
                ex.printStackTrace(); 
            }
            try{
                ResultSet rsNotCred = objDB.consulta("select sum(valor) from tbl_nota_credito_venta "
                        + "where fecha_emision between '"+anio+"-"+mes+"-01' and '"+anio+"-"+mes+"-"+fin_mes+"' and anulado = false");
                if(rsNotCred.next()){
                    total_notas_credito = rsNotCred.getString(1)!=null?rsNotCred.getFloat(1):0;
                    rsNotCred.close();
                }
            }catch(Exception ex){ ex.printStackTrace(); }
            
            out.println("<numEstabRuc>"+Addons.rellenarCeros(num_establecimientos, 3)+"</numEstabRuc>");
            out.println("<totalVentas>" + Addons.truncar( Addons.redondear(totales_ventas - total_notas_credito), 2 ) + "</totalVentas>");
            out.println("<codigoOperativo>IVA</codigoOperativo>");

            
            
            
            
            
            
            /*     C O M P R A S  */
            
            
            
            
            

            try{
                String id_factura_compra = "0";
                //String tipo_documento = "";
                String serie = "-";
                double subtotal = 0.00;
                double descuento = 0.00;
                double baseImp = 0.00;
                double baseImp0 = 0.00;
                double subtotal_0 = 0.00;
                double iva_2 = 0.00;
                String codSustento = "02";
                String tpIdProv = "01";
                String id_retencion_compra = "-1";
                String ret_autorizacion = "000";
                String ret_num_serie = "000-000";
                String ret_num_retencion = "0";
                String ret_fecha_emision = "0000-00-00";
                ResultSet compras = null;
                ResultSet retenciones = null;
                ResultSet retencionesDetalle = null;
                float porcentaje_retencion = 30;
                String valor_retenido = "0.00";
                String air = "";
                String impuesto = "";

                out.println("<compras>");
                
                compras = objDB.consulta("select * from vta_factura_compra where anulado=false and contabilizado=true and fecha_compra " +
                "between '"+anio+"-"+mes+"-01' and '"+anio+"-"+mes+"-"+fin_mes+"' order by razon_social,num_factura;");
                while(compras.next()){
                    ruc =((compras.getString("ruc")!=null)?compras.getString("ruc").replace("-", ""):"");
                    id_factura_compra = (compras.getString("id_factura_compra")!=null)?compras.getString("id_factura_compra"):"0";
                    tpIdProv = (compras.getString("tipo_documento")!=null)?compras.getString("tipo_documento"):"01";
                    serie = (compras.getString("serie_factura")!=null)?compras.getString("serie_factura"):"-";
                    String vec_serie [] = serie.split("-");
                    subtotal = (compras.getString("subtotal")!=null)?compras.getFloat("subtotal"):0.00;
                    descuento = (compras.getString("descuento")!=null)?compras.getFloat("descuento"):0.00;
                    subtotal_0 = (compras.getString("subtotal_0")!=null)?compras.getFloat("subtotal_0"):0.00;
                    iva_2 = (compras.getString("iva_2")!=null)?compras.getFloat("iva_2"):0.00;
                    baseImp = Addons.redondear(subtotal-descuento-subtotal_0);
                    baseImp0 = Addons.redondear(subtotal_0-descuento);
                    m_pasaporte = pasaporte.matcher(ruc);

                    /*codSustento = "01";
                    //tpIdProv = "01"; // RUC
                    if(tpIdProv.compareTo("01")!=0){ // RUC
                        codSustento = "01";
                        //tpIdProv = "01";
                    }else {
                        codSustento = "02";
                        //tpIdProv = "02";
                    }*/

                    id_retencion_compra = "-1";
                    ret_autorizacion = "000";
                    ret_num_serie = "000-000";
                    ret_num_retencion = "0";
                    ret_fecha_emision = "0000-00-00";
                    try{
                        retenciones = objDB.consulta("select * from tbl_retencion_compra where anulado=false and documento='f' and id_factura_compra="+id_factura_compra);
                        if(retenciones.next()){
                            codSustento = (retenciones.getString("sustento_tributario")!=null) ? retenciones.getString("sustento_tributario") : "02";
                            id_retencion_compra = (retenciones.getString("id_retencion_compra")!=null)?retenciones.getString("id_retencion_compra"):"-1";
                            ret_autorizacion = (retenciones.getString("ret_autorizacion")!=null)?retenciones.getString("ret_autorizacion"):"000";
                            ret_num_serie = (retenciones.getString("ret_num_serie")!=null)?retenciones.getString("ret_num_serie"):"000-000";
                            ret_num_retencion = (retenciones.getString("ret_num_retencion")!=null)?retenciones.getString("ret_num_retencion"):"0";
                            ret_fecha_emision = Addons.toFechaSQL((retenciones.getString("ret_fecha_emision")!=null)?retenciones.getString("ret_fecha_emision"):"0000-00-00");
                            retenciones.close();
                        }
                    }catch(Exception e){
                        e.printStackTrace();
                    }

                    out.println("<detalleCompras>");
                    out.println("<codSustento>"+codSustento+"</codSustento>");
                    out.println("<tpIdProv>"+tpIdProv+"</tpIdProv>");
                    out.println("<idProv>"+ruc+"</idProv>");
                    out.println("<tipoComprobante>01</tipoComprobante>");
                    out.println("<fechaRegistro>"+Addons.toFechaSQL((compras.getString("fecha_compra")!=null)?compras.getString("fecha_compra"):"")+"</fechaRegistro>");
                    out.println("<establecimiento>"+((vec_serie[0].compareTo("")!=0)?vec_serie[0]:"000")+"</establecimiento>");
                    if(vec_serie.length>1){
                        out.println("<puntoEmision>"+((vec_serie[1].compareTo("")!=0)?vec_serie[1]:"000")+"</puntoEmision>");
                    }else{
                        out.println("<puntoEmision>000</puntoEmision>");
                    }
                    String autorizacion = ((compras.getString("autorizacion")!=null)?compras.getString("autorizacion"):"");
                    out.println("<secuencial>"+((compras.getString("num_factura")!=null)?compras.getString("num_factura"):"")+"</secuencial>");
                    out.println("<fechaEmision>"+Addons.toFechaSQL((compras.getString("fecha_compra")!=null)?compras.getString("fecha_compra"):"")+"</fechaEmision>");
                    out.println("<autorizacion>"+( autorizacion.length()>10 ? autorizacion.substring(0, 10) : autorizacion )+"</autorizacion>");

                    out.println("<baseNoGraIva>0.00</baseNoGraIva>");
                    if(iva_2>0){
                        out.println("<baseImponible>"+(subtotal_0>0 ? Addons.truncar(subtotal_0) : "0.00")+"</baseImponible>");
                        out.println("<baseImpGrav>"+Addons.truncar(baseImp)+"</baseImpGrav>");
                    }else{
                        out.println("<baseImponible>"+Addons.truncar(baseImp0)+"</baseImponible>");
                        out.println("<baseImpGrav>0.00</baseImpGrav>");
                    }
                    
                    
                    
                    /*  va mas abajo   */
                    String vec_serie_ret [] = ret_num_serie.split("-");
                    if(vec_serie_ret[0].compareTo("000")!=0){
                        porcentaje_retencion = 0;
                        valor_retenido = "0.00";
                        try{
                            retencionesDetalle = objDB.consulta("select I.codigo_impuesto, I.porcentaje_retencion, I.impuesto, D.base_imponible, D.valor_retenido "
                                    + "from tbl_retencion_compra_detalle as D inner join tbl_tabla_impuesto as I on I.id_tabla_impuesto=D.id_tabla_impuesto "
                                    + "where id_retencion_compra="+id_retencion_compra+";");
                            if(objDB.getFilas(retencionesDetalle)>0){
                                air = "<air>";
                                while(retencionesDetalle.next()){
                                    impuesto = retencionesDetalle.getString("impuesto")!=null?retencionesDetalle.getString("impuesto"):"";
                                    if(impuesto.compareTo("RENTA")==0){
                                        air += "<detalleAir>";
                                        air += "<codRetAir>"+((retencionesDetalle.getString("codigo_impuesto")!=null)?retencionesDetalle.getString("codigo_impuesto"):"")+"</codRetAir>";
                                        air += "<baseImpAir>"+((retencionesDetalle.getString("base_imponible")!=null)?retencionesDetalle.getString("base_imponible"):"")+"</baseImpAir>";
                                        air += "<porcentajeAir>"+((retencionesDetalle.getString("porcentaje_retencion")!=null)?retencionesDetalle.getString("porcentaje_retencion"):"")+"</porcentajeAir>";
                                        air += "<valRetAir>"+((retencionesDetalle.getString("valor_retenido")!=null)?retencionesDetalle.getString("valor_retenido"):"")+"</valRetAir>";
                                        air += "</detalleAir>";
                                    }else if(impuesto.compareTo("IVA")==0){
                                              porcentaje_retencion = retencionesDetalle.getString("porcentaje_retencion")!=null?retencionesDetalle.getFloat("porcentaje_retencion"):0;
                                              valor_retenido = retencionesDetalle.getString("valor_retenido")!=null?retencionesDetalle.getString("valor_retenido"):"";
                                    }
                                }
                                air += "</air>";
                            }
                            retencionesDetalle.close();
                        }catch(Exception e){
                            air = "";
                            e.printStackTrace();
                        }
                    }
                    
                    out.println("<montoIce>0.00</montoIce>");
                    out.println("<montoIva>"+Addons.truncar(iva_2)+"</montoIva>");
                    out.println("<valorRetBienes>"+(porcentaje_retencion==30?valor_retenido:"0.00")+"</valorRetBienes>");
                    out.println("<valorRetServicios>"+(porcentaje_retencion==70?valor_retenido:"0.00")+"</valorRetServicios>");
                    out.println("<valRetServ100>"+(porcentaje_retencion==100?valor_retenido:"0.00")+"</valRetServ100>");

                    out.println("<pagoExterior>");
                    out.println("<pagoLocExt>01</pagoLocExt>");
                    out.println("<paisEfecPago>NA</paisEfecPago>");
                    out.println("<aplicConvDobTrib>NA</aplicConvDobTrib>");
                    out.println("<pagExtSujRetNorLeg>NA</pagExtSujRetNorLeg>");
                    out.println("</pagoExterior>");
                    
                    if((baseImp+subtotal_0+iva_2)>1000){
                        out.println("<formasDePago>");
                        out.println("<formaPago>02</formaPago>");
                        out.println("</formasDePago>");
                    }
                    
                    
                    /*  viene de ariba   */
                    
                    if(vec_serie_ret[0].compareTo("000")!=0){
                        
                        out.println(air);
                    
                        out.println("<estabRetencion1>"+((vec_serie_ret[0].compareTo("")!=0)?vec_serie_ret[0]:"000")+"</estabRetencion1>");
                        if(vec_serie_ret.length>1){
                            out.println("<ptoEmiRetencion1>"+((vec_serie_ret[1].compareTo("")!=0)?vec_serie_ret[1]:"000")+"</ptoEmiRetencion1>");
                        }else{
                            out.println("<ptoEmiRetencion1>000</ptoEmiRetencion1>");
                        }
                        //out.println("<estabRetencion1>000</estabRetencion1>");
                        //out.println("<ptoEmiRetencion1>000</ptoEmiRetencion1>");
                        out.println("<secRetencion1>"+ret_num_retencion+"</secRetencion1>");
                        out.println("<autRetencion1>"+ret_autorizacion+"</autRetencion1>");
                        out.println("<fechaEmiRet1>"+Addons.toFechaSQL(ret_fecha_emision)+"</fechaEmiRet1>");
                    }

                    //out.println("<estabRetencion2>000</estabRetencion2>");
                    //out.println("<ptoEmiRetencion2>000</ptoEmiRetencion2>");
                    //out.println("<secRetencion2>0</secRetencion2>");
                    //out.println("<autRetencion2>000</autRetencion2>");
                    //out.println("<fechaEmiRet2>00/00/0000</fechaEmiRet2>");

                    //out.println("<docModificado>0</docModificado>");
                    //out.println("<estabModificado>000</estabModificado>");
                    //out.println("<ptoEmiModificado>000</ptoEmiModificado>");
                    //out.println("<secModificado>0</secModificado>");
                    //out.println("<autModificado>000</autModificado>");
                    
                    out.println("</detalleCompras>");
                    out.println();
                }
                compras.close();


                
                
                
                // liquidaciones de compras y servicios

                
                
                
                
                
                compras = objDB.consulta("select * from vta_liquidacion_compra where anulado=false and fecha_emision " +
                "between '"+anio+"-"+mes+"-01' and '"+anio+"-"+mes+"-"+fin_mes+"' order by razon_social,num_liquidacion;");
                while(compras.next()){
                    ruc =((compras.getString("ruc")!=null)?compras.getString("ruc").replace("-", ""):"");
                    id_factura_compra = (compras.getString("id_liquidacion_compra")!=null)?compras.getString("id_liquidacion_compra"):"0";
                    tpIdProv = (compras.getString("tipo_documento")!=null)?compras.getString("tipo_documento"):"01";
                    serie = (compras.getString("serie_liquidacion")!=null)?compras.getString("serie_liquidacion"):"-";
                    String vec_serie [] = serie.split("-");
                    subtotal = (compras.getString("subtotal")!=null)?compras.getFloat("subtotal"):0.00;
                    subtotal_0 = (compras.getString("iva_0")!=null)?compras.getFloat("iva_0"):0.00;
                    iva_2 = (compras.getString("iva_2")!=null)?compras.getFloat("iva_2"):0.00;
                    baseImp = Addons.redondear(subtotal-subtotal_0);
                    m_pasaporte = pasaporte.matcher(ruc);

                    //codSustento = tpIdProv;
                    //tpIdProv = "01"; // RUC
                    //if(tpIdProv.compareTo("01")!=0){ // RUC
                    //    codSustento = "01";
                        //tpIdProv = "01";
                    //}else {
                    //    codSustento = "02";
                        //tpIdProv = "02";
                    //}

                    id_retencion_compra = "-1";
                    ret_autorizacion = "000";
                    ret_num_serie = "000-000";
                    ret_num_retencion = "0";
                    ret_fecha_emision = "0000-00-00";
                    try{
                        retenciones = objDB.consulta("select * from tbl_retencion_compra where anulado=false and documento='l' and id_factura_compra="+id_factura_compra);
                        if(retenciones.next()){
                            codSustento = (retenciones.getString("sustento_tributario")!=null) ? retenciones.getString("sustento_tributario") : "02";
                            id_retencion_compra = (retenciones.getString("id_retencion_compra")!=null)?retenciones.getString("id_retencion_compra"):"-1";
                            ret_autorizacion = (retenciones.getString("ret_autorizacion")!=null)?retenciones.getString("ret_autorizacion"):"000";
                            ret_num_serie = (retenciones.getString("ret_num_serie")!=null)?retenciones.getString("ret_num_serie"):"000-000";
                            ret_num_retencion = (retenciones.getString("ret_num_retencion")!=null)?retenciones.getString("ret_num_retencion"):"0";
                            ret_fecha_emision = Addons.toFechaSQL((retenciones.getString("ret_fecha_emision")!=null)?retenciones.getString("ret_fecha_emision"):"0000-00-00");
                            retenciones.close();
                        }
                    }catch(Exception e){
                        e.printStackTrace();
                    }

                    out.println("<detalleCompras>");
                    out.println("<codSustento>"+codSustento+"</codSustento>");
                    out.println("<tpIdProv>"+tpIdProv+"</tpIdProv>");
                    out.println("<idProv>"+ruc+"</idProv>");
                    out.println("<tipoComprobante>03</tipoComprobante>");
                    out.println("<fechaRegistro>"+Addons.toFechaSQL((compras.getString("fecha_emision")!=null)?compras.getString("fecha_emision"):"")+"</fechaRegistro>");
                    out.println("<establecimiento>"+((vec_serie[0].compareTo("")!=0)?vec_serie[0]:"000")+"</establecimiento>");
                    if(vec_serie.length>1){
                        out.println("<puntoEmision>"+((vec_serie[1].compareTo("")!=0)?vec_serie[1]:"000")+"</puntoEmision>");
                    }else{
                        out.println("<puntoEmision>000</puntoEmision>");
                    }
                    out.println("<secuencial>"+((compras.getString("num_liquidacion")!=null)?compras.getString("num_liquidacion"):"")+"</secuencial>");
                    out.println("<fechaEmision>"+Addons.toFechaSQL((compras.getString("fecha_emision")!=null)?compras.getString("fecha_emision"):"")+"</fechaEmision>");
                    out.println("<autorizacion>"+((compras.getString("autorizacion")!=null)?compras.getString("autorizacion"):"")+"</autorizacion>");

                    out.println("<baseNoGraIva>0.00</baseNoGraIva>");
                    
                    //out.println("<baseImponible>"+Addons.truncar(subtotal)+"</baseImponible>");
                    if(iva_2>0){
                        out.println("<baseImponible>0.00</baseImponible>");
                        //out.println("<baseImponible>"+Addons.truncar(subtotal)+"</baseImponible>");
                        out.println("<baseImpGrav>"+Addons.truncar(baseImp)+"</baseImpGrav>");
                    }else{
                        out.println("<baseImponible>"+Addons.truncar(baseImp)+"</baseImponible>");
                        //out.println("<baseImponible>"+Addons.truncar(subtotal)+"</baseImponible>");
                        out.println("<baseImpGrav>0.00</baseImpGrav>");
                    }
                    
                    String vec_serie_ret [] = ret_num_serie.split("-");
                    if(vec_serie_ret[0].compareTo("000")!=0){
                        porcentaje_retencion = 0;
                        valor_retenido = "0.00";
                        try{
                            retencionesDetalle = objDB.consulta("select I.codigo_impuesto, I.porcentaje_retencion, I.impuesto, D.base_imponible, D.valor_retenido "
                                    + "from tbl_retencion_compra_detalle as D inner join tbl_tabla_impuesto as I on I.id_tabla_impuesto=D.id_tabla_impuesto "
                                    + "where id_retencion_compra="+id_retencion_compra+";");
                            if(objDB.getFilas(retencionesDetalle)>0){
                                air = "<air>";
                                while(retencionesDetalle.next()){
                                    impuesto = retencionesDetalle.getString("impuesto")!=null?retencionesDetalle.getString("impuesto"):"";
                                    if(impuesto.compareTo("RENTA")==0){
                                        air += "<detalleAir>";
                                        air += "<codRetAir>"+((retencionesDetalle.getString("codigo_impuesto")!=null)?retencionesDetalle.getString("codigo_impuesto"):"")+"</codRetAir>";
                                        air += "<baseImpAir>"+((retencionesDetalle.getString("base_imponible")!=null)?retencionesDetalle.getString("base_imponible"):"")+"</baseImpAir>";
                                        air += "<porcentajeAir>"+((retencionesDetalle.getString("porcentaje_retencion")!=null)?retencionesDetalle.getString("porcentaje_retencion"):"")+"</porcentajeAir>";
                                        air += "<valRetAir>"+((retencionesDetalle.getString("valor_retenido")!=null)?retencionesDetalle.getString("valor_retenido"):"")+"</valRetAir>";
                                        air += "</detalleAir>";
                                    }else if(impuesto.compareTo("IVA")==0){
                                              porcentaje_retencion = retencionesDetalle.getString("porcentaje_retencion")!=null?retencionesDetalle.getFloat("porcentaje_retencion"):0;
                                              valor_retenido = retencionesDetalle.getString("valor_retenido")!=null?retencionesDetalle.getString("valor_retenido"):"";
                                    }
                                }
                                air += "</air>";
                            }
                            retencionesDetalle.close();
                        }catch(Exception e){
                            air = "";
                            e.printStackTrace();
                        }
                    }
                    
                    out.println("<montoIce>0.00</montoIce>");
                    out.println("<montoIva>"+Addons.truncar(iva_2)+"</montoIva>");
                    out.println("<valorRetBienes>"+(porcentaje_retencion==30?valor_retenido:"0.00")+"</valorRetBienes>");
                    out.println("<valorRetServicios>"+(porcentaje_retencion==70?valor_retenido:"0.00")+"</valorRetServicios>");
                    out.println("<valRetServ100>"+(porcentaje_retencion==100?valor_retenido:"0.00")+"</valRetServ100>");
                    
                    out.println("<pagoExterior>");
                    out.println("<pagoLocExt>01</pagoLocExt>");
                    out.println("<paisEfecPago>NA</paisEfecPago>");
                    out.println("<aplicConvDobTrib>NA</aplicConvDobTrib>");
                    out.println("<pagExtSujRetNorLeg>NA</pagExtSujRetNorLeg>");
                    out.println("</pagoExterior>");
                    
                    if((baseImp+subtotal_0+iva_2)>1000){
                        out.println("<formasDePago>");
                        out.println("<formaPago>02</formaPago>");
                        out.println("</formasDePago>");
                    }

                    if(vec_serie_ret[0].compareTo("000")!=0){
                        
                        out.println(air);
                        
                        out.println("<estabRetencion1>"+((vec_serie_ret[0].compareTo("")!=0)?vec_serie_ret[0]:"000")+"</estabRetencion1>");
                        if(vec_serie_ret.length>1){
                            out.println("<ptoEmiRetencion1>"+((vec_serie_ret[1].compareTo("")!=0)?vec_serie_ret[1]:"000")+"</ptoEmiRetencion1>");
                        }else{
                            out.println("<ptoEmiRetencion1>000</ptoEmiRetencion1>");
                        }
                        //out.println("<estabRetencion1>000</estabRetencion1>");
                        //out.println("<ptoEmiRetencion1>000</ptoEmiRetencion1>");
                        out.println("<secRetencion1>"+ret_num_retencion+"</secRetencion1>");
                        out.println("<autRetencion1>"+ret_autorizacion+"</autRetencion1>");
                        out.println("<fechaEmiRet1>"+Addons.toFechaSQL(ret_fecha_emision)+"</fechaEmiRet1>");
                    }

                    //out.println("<estabRetencion2>000</estabRetencion2>");
                    //out.println("<ptoEmiRetencion2>000</ptoEmiRetencion2>");
                    //out.println("<secRetencion2>0</secRetencion2>");
                    //out.println("<autRetencion2>000</autRetencion2>");
                    //out.println("<fechaEmiRet2>00/00/0000</fechaEmiRet2>");

                    //out.println("<docModificado>0</docModificado>");
                    //out.println("<estabModificado>000</estabModificado>");
                    //out.println("<ptoEmiModificado>000</ptoEmiModificado>");
                    //out.println("<secModificado>0</secModificado>");
                    //out.println("<autModificado>000</autModificado>");
                    
                    out.println("</detalleCompras>");
                    out.println();
                }
                compras.close();



                
                
                

                // Notas de crédito


                
                
                
                
                
                compras = objDB.consulta("select * from vta_nota_credito_compra where anulado=false and fecha_emision " +
                "between '"+anio+"-"+mes+"-01' and '"+anio+"-"+mes+"-"+fin_mes+"' order by razon_social,num_nota;");
                String autorizacion_nota = "";
                while(compras.next()){
                    ruc =((compras.getString("ruc")!=null)?compras.getString("ruc").replace("-", ""):"");
                    id_factura_compra = (compras.getString("id_nota_credito_compra")!=null)?compras.getString("id_nota_credito_compra"):"0";
                    tpIdProv = (compras.getString("tipo_documento")!=null)?compras.getString("tipo_documento"):"01";
                    serie = (compras.getString("serie_nota")!=null)?compras.getString("serie_nota"):"-";
                    String vec_serie [] = serie.split("-");
                    subtotal = (compras.getString("valor")!=null)?compras.getFloat("valor"):0.00;
                    subtotal_0 = (compras.getString("iva_0")!=null)?compras.getFloat("iva_0"):0.00;
                    iva_2 = (compras.getString("iva_2")!=null)?compras.getFloat("iva_2"):0.00;
                    baseImp = Addons.redondear(subtotal-subtotal_0);
                    m_pasaporte = pasaporte.matcher(ruc);

                    //codSustento = tpIdProv;
                    //tpIdProv = "01"; // RUC
                    //if(tpIdProv.compareTo("01")==0){ // RUC
                    //    codSustento = "01";
                        //tpIdProv = "01";
                    //}else {
                    //    codSustento = "02";
                        //tpIdProv = "02";
                    //}

                    id_retencion_compra = "-1";
                    ret_autorizacion = "000";
                    ret_num_serie = "000-000";
                    ret_num_retencion = "0";
                    ret_fecha_emision = "0000-00-00";
                    try{
                        retenciones = objDB.consulta("select * from tbl_retencion_compra where anulado=false and documento='c' and id_factura_compra="+id_factura_compra);
                        if(retenciones.next()){
                            codSustento = (retenciones.getString("sustento_tributario")!=null) ? retenciones.getString("sustento_tributario") : "02";
                            id_retencion_compra = (retenciones.getString("id_retencion_compra")!=null)?retenciones.getString("id_retencion_compra"):"-1";
                            ret_autorizacion = (retenciones.getString("ret_autorizacion")!=null)?retenciones.getString("ret_autorizacion"):"000";
                            ret_num_serie = (retenciones.getString("ret_num_serie")!=null)?retenciones.getString("ret_num_serie"):"000-000";
                            ret_num_retencion = (retenciones.getString("ret_num_retencion")!=null)?retenciones.getString("ret_num_retencion"):"0";
                            ret_fecha_emision = Addons.toFechaSQL((retenciones.getString("ret_fecha_emision")!=null)?retenciones.getString("ret_fecha_emision"):"0000-00-00");
                            retenciones.close();
                        }
                    }catch(Exception e){
                        e.printStackTrace();
                    }

                    out.println("<detalleCompras>");
                    out.println("<codSustento>"+codSustento+"</codSustento>");
                    out.println("<tpIdProv>"+tpIdProv+"</tpIdProv>");
                    out.println("<idProv>"+ruc+"</idProv>");
                    out.println("<tipoComprobante>04</tipoComprobante>");
                    out.println("<fechaRegistro>"+Addons.toFechaSQL((compras.getString("fecha_emision")!=null)?compras.getString("fecha_emision"):"")+"</fechaRegistro>");
                    out.println("<establecimiento>"+((vec_serie[0].compareTo("")!=0)?vec_serie[0]:"000")+"</establecimiento>");
                    if(vec_serie.length>1){
                        out.println("<puntoEmision>"+((vec_serie[1].compareTo("")!=0)?vec_serie[1]:"000")+"</puntoEmision>");
                    }else{
                        out.println("<puntoEmision>000</puntoEmision>");
                    }
                    autorizacion_nota = ((compras.getString("autorizacion_nota")!=null)?compras.getString("autorizacion_nota"):"");
                    out.println("<secuencial>"+((compras.getString("num_nota")!=null)?compras.getString("num_nota"):"")+"</secuencial>");
                    out.println("<fechaEmision>"+Addons.toFechaSQL((compras.getString("fecha_emision")!=null)?compras.getString("fecha_emision"):"")+"</fechaEmision>");
                    out.println("<autorizacion>"+( autorizacion_nota.length()>10 ? autorizacion_nota.substring(0, 10) : autorizacion_nota )+"</autorizacion>");

                    out.println("<baseNoGraIva>"+Addons.truncar(subtotal_0)+"</baseNoGraIva>");
                    //out.println("<baseImponible>"+Addons.truncar(subtotal)+"</baseImponible>");
                    out.println("<baseImponible>0.00</baseImponible>");
                    out.println("<baseImpGrav>0.00</baseImpGrav>");
                    
                    if(iva_2>0){
                        out.println("<baseImponible>0.00</baseImponible>");
                        out.println("<baseImpGrav>"+Addons.truncar(baseImp)+"</baseImpGrav>");
                    }else{
                        out.println("<baseImponible>"+Addons.truncar(baseImp)+"</baseImponible>");
                        out.println("<baseImpGrav>0.00</baseImpGrav>");
                    }
                    
                    
                    String vec_serie_ret [] = ret_num_serie.split("-");
                    if((vec_serie_ret[0].compareTo("000")!=0)){
                        porcentaje_retencion = 0;
                        valor_retenido = "0.00";
                        try{
                            retencionesDetalle = objDB.consulta("select I.codigo_impuesto, I.porcentaje_retencion, I.impuesto, D.base_imponible, D.valor_retenido "
                                    + "from tbl_retencion_compra_detalle as D inner join tbl_tabla_impuesto as I on I.id_tabla_impuesto=D.id_tabla_impuesto "
                                    + "where id_retencion_compra="+id_retencion_compra+";");
                            if(objDB.getFilas(retencionesDetalle)>0){
                                air = "<air>";
                                while(retencionesDetalle.next()){
                                    impuesto = retencionesDetalle.getString("impuesto")!=null?retencionesDetalle.getString("impuesto"):"";
                                    if(impuesto.compareTo("RENTA")==0){
                                        air += "<detalleAir>";
                                        air += "<codRetAir>"+((retencionesDetalle.getString("codigo_impuesto")!=null)?retencionesDetalle.getString("codigo_impuesto"):"")+"</codRetAir>";
                                        air += "<baseImpAir>"+((retencionesDetalle.getString("base_imponible")!=null)?retencionesDetalle.getString("base_imponible"):"")+"</baseImpAir>";
                                        air += "<porcentajeAir>"+((retencionesDetalle.getString("porcentaje_retencion")!=null)?retencionesDetalle.getString("porcentaje_retencion"):"")+"</porcentajeAir>";
                                        air += "<valRetAir>"+((retencionesDetalle.getString("valor_retenido")!=null)?retencionesDetalle.getString("valor_retenido"):"")+"</valRetAir>";
                                        air += "</detalleAir>";
                                    }else if(impuesto.compareTo("IVA")==0){
                                              porcentaje_retencion = retencionesDetalle.getString("porcentaje_retencion")!=null?retencionesDetalle.getFloat("porcentaje_retencion"):0;
                                              valor_retenido = retencionesDetalle.getString("valor_retenido")!=null?retencionesDetalle.getString("valor_retenido"):"";
                                    }
                                }
                                air += "</air>";
                            }
                            retencionesDetalle.close();
                        }catch(Exception e){
                            air = "";
                            e.printStackTrace();
                        }
                    }
                    
                    out.println("<montoIce>0.00</montoIce>");
                    out.println("<montoIva>"+Addons.truncar(iva_2)+"</montoIva>");
                    out.println("<valorRetBienes>"+(porcentaje_retencion==30?valor_retenido:"0.00")+"</valorRetBienes>");
                    out.println("<valorRetServicios>"+(porcentaje_retencion==70?valor_retenido:"0.00")+"</valorRetServicios>");
                    out.println("<valRetServ100>"+(porcentaje_retencion==100?valor_retenido:"0.00")+"</valRetServ100>");
                    
                    out.println("<pagoExterior>");
                    out.println("<pagoLocExt>01</pagoLocExt>");
                    out.println("<paisEfecPago>NA</paisEfecPago>");
                    out.println("<aplicConvDobTrib>NA</aplicConvDobTrib>");
                    out.println("<pagExtSujRetNorLeg>NA</pagExtSujRetNorLeg>");
                    out.println("</pagoExterior>");
                    
                    if((baseImp+subtotal_0+iva_2)>1000){
                        out.println("<formasDePago>");
                        out.println("<formaPago>02</formaPago>");
                        out.println("</formasDePago>");
                    }

                    if((vec_serie_ret[0].compareTo("000")!=0)){
                        
                        out.println(air);

                        out.println("<estabRetencion1>"+((vec_serie_ret[0].compareTo("")!=0)?vec_serie_ret[0]:"000")+"</estabRetencion1>");
                        if(vec_serie_ret.length>1){
                            out.println("<ptoEmiRetencion1>"+((vec_serie_ret[1].compareTo("")!=0)?vec_serie_ret[1]:"000")+"</ptoEmiRetencion1>");
                        }else{
                            out.println("<ptoEmiRetencion1>000</ptoEmiRetencion1>");
                        }
                        //out.println("<estabRetencion1>000</estabRetencion1>");
                        //out.println("<ptoEmiRetencion1>000</ptoEmiRetencion1>");
                        out.println("<secRetencion1>"+ret_num_retencion+"</secRetencion1>");
                        out.println("<autRetencion1>"+( ret_autorizacion.length()>10 ? ret_autorizacion.substring(0, 10) : ret_autorizacion )+"</autRetencion1>");
                        out.println("<fechaEmiRet1>"+Addons.toFechaSQL(ret_fecha_emision)+"</fechaEmiRet1>");
                    }
                    //out.println("<estabRetencion2>000</estabRetencion2>");
                    //out.println("<ptoEmiRetencion2>000</ptoEmiRetencion2>");
                    //out.println("<secRetencion2>0</secRetencion2>");
                    //out.println("<autRetencion2>000</autRetencion2>");
                    //out.println("<fechaEmiRet2>00/00/0000</fechaEmiRet2>");

                    //out.println("<docModificado>0</docModificado>");
                    //out.println("<estabModificado>000</estabModificado>");
                    //out.println("<ptoEmiModificado>000</ptoEmiModificado>");
                    //out.println("<secModificado>0</secModificado>");
                    //out.println("<autModificado>000</autModificado>");
                    
                    out.println("</detalleCompras>");
                    out.println();
                }
                compras.close();


                // Notas de débito

/*
                compras = objDB.consulta("select * from vta_nota_debito_compra where anulado=false and fecha_emision " +
                "between '"+anio+"-"+mes+"-01' and '"+anio+"-"+mes+"-"+fin_mes+"' order by razon_social,num_nota;");
                while(compras.next()){
                    ruc =((compras.getString("ruc")!=null)?compras.getString("ruc").replace("-", ""):"");
                    id_factura_compra = (compras.getString("id_nota_debito_compra")!=null)?compras.getString("id_nota_debito_compra"):"0";
                    tpIdProv = (compras.getString("tipo_documento")!=null)?compras.getString("tipo_documento"):"01";
                    serie = (compras.getString("serie_nota")!=null)?compras.getString("serie_nota"):"-";
                    String vec_serie [] = serie.split("-");
                    subtotal = (compras.getString("valor")!=null)?compras.getFloat("valor"):0.00;
                    subtotal_0 = (compras.getString("iva_0")!=null)?compras.getFloat("iva_0"):0.00;
                    iva_2 = (compras.getString("iva_2")!=null)?compras.getString("iva_2"):"0.00";
                    baseImp = Addons.redondear(subtotal-subtotal_0);
                    m_pasaporte = pasaporte.matcher(ruc);

                    //codSustento = tpIdProv;
                    //tpIdProv = "01"; // RUC
                    //if(tpIdProv.compareTo("01")!=0){ // RUC
                    //    codSustento = "01";
                        //tpIdProv = "01";
                    //}else {
                    //    codSustento = "02";
                        //tpIdProv = "02";
                    //}

                    id_retencion_compra = "-1";
                    ret_autorizacion = "000";
                    ret_num_serie = "000-000";
                    ret_num_retencion = "0";
                    ret_fecha_emision = "0000-00-00";
                    try{
                        retenciones = objDB.consulta("select * from tbl_retencion_compra where anulado=false and documento='d' and id_factura_compra="+id_factura_compra);
                        if(retenciones.next()){
                            codSustento = (retenciones.getString("sustento_tributario")!=null) ? retenciones.getString("sustento_tributario") : "02";
                            id_retencion_compra = (retenciones.getString("id_retencion_compra")!=null)?retenciones.getString("id_retencion_compra"):"-1";
                            ret_autorizacion = (retenciones.getString("ret_autorizacion")!=null)?retenciones.getString("ret_autorizacion"):"000";
                            ret_num_serie = (retenciones.getString("ret_num_serie")!=null)?retenciones.getString("ret_num_serie"):"000-000";
                            ret_num_retencion = (retenciones.getString("ret_num_retencion")!=null)?retenciones.getString("ret_num_retencion"):"0";
                            ret_fecha_emision = Addons.toFechaSQL((retenciones.getString("ret_fecha_emision")!=null)?retenciones.getString("ret_fecha_emision"):"0000-00-00");
                            retenciones.close();
                        }
                    }catch(Exception e){
                        e.printStackTrace();
                    }

                    porcentaje_retencion = 0;
                    valor_retenido = "0.00";
                    
                    try{
                        retencionesDetalle = objDB.consulta("select I.codigo_impuesto, I.porcentaje_retencion, I.impuesto, D.base_imponible, D.valor_retenido "
                                + "from tbl_retencion_compra_detalle as D inner join tbl_tabla_impuesto as I on I.id_tabla_impuesto=D.id_tabla_impuesto "
                                + "where id_retencion_compra="+id_retencion_compra+";");
                        air = "<air>";
                        while(retencionesDetalle.next()){
                            impuesto = retencionesDetalle.getString("impuesto")!=null?retencionesDetalle.getString("impuesto"):"";
                            if(impuesto.compareTo("RENTA")==0){
                                air += "<detalleAir>";
                                air += "<codRetAir>"+((retencionesDetalle.getString("codigo_impuesto")!=null)?retencionesDetalle.getString("codigo_impuesto"):"")+"</codRetAir>";
                                air += "<baseImpAir>"+((retencionesDetalle.getString("base_imponible")!=null)?retencionesDetalle.getString("base_imponible"):"")+"</baseImpAir>";
                                air += "<porcentajeAir>"+((retencionesDetalle.getString("porcentaje_retencion")!=null)?retencionesDetalle.getString("porcentaje_retencion"):"")+"</porcentajeAir>";
                                air += "<valRetAir>"+((retencionesDetalle.getString("valor_retenido")!=null)?retencionesDetalle.getString("valor_retenido"):"")+"</valRetAir>";
                                air += "</detalleAir>";
                            }else if(impuesto.compareTo("IVA")==0){
                                      porcentaje_retencion = retencionesDetalle.getString("porcentaje_retencion")!=null?retencionesDetalle.getFloat("porcentaje_retencion"):0;
                                      valor_retenido = retencionesDetalle.getString("valor_retenido")!=null?retencionesDetalle.getString("valor_retenido"):"";
                            }
                        }
                        air += "</air>";
                        retencionesDetalle.close();
                    }catch(Exception e){e.printStackTrace();}
                    

                    out.println("<detalleCompras>");
                    out.println("<codSustento>"+codSustento+"</codSustento>");
                    out.println("<tpIdProv>"+tpIdProv+"</tpIdProv>");
                    out.println("<idProv>"+ruc+"</idProv>");
                    out.println("<tipoComprobante>05</tipoComprobante>");
                    out.println("<fechaRegistro>"+Addons.toFechaSQL((compras.getString("fecha_emision")!=null)?compras.getString("fecha_emision"):"")+"</fechaRegistro>");
                    out.println("<establecimiento>"+((vec_serie[0].compareTo("")!=0)?vec_serie[0]:"000")+"</establecimiento>");
                    if(vec_serie.length>1){
                        out.println("<puntoEmision>"+((vec_serie[1].compareTo("")!=0)?vec_serie[1]:"000")+"</puntoEmision>");
                    }else{
                        out.println("<puntoEmision>000</puntoEmision>");
                    }
                    out.println("<secuencial>"+((compras.getString("num_nota")!=null)?compras.getString("num_nota"):"")+"</secuencial>");
                    out.println("<fechaEmision>"+Addons.toFechaSQL((compras.getString("fecha_emision")!=null)?compras.getString("fecha_emision"):"")+"</fechaEmision>");
                    out.println("<autorizacion>"+((compras.getString("autorizacion_nota")!=null)?compras.getString("autorizacion_nota"):"")+"</autorizacion>");

                    out.println("<baseNoGraIva>"+Addons.truncar(subtotal_0)+"</baseNoGraIva>");
                    //out.println("<baseImponible>"+Addons.truncar(subtotal)+"</baseImponible>");
                    out.println("<baseImponible>0.00</baseImponible>");
                    out.println("<baseImpGrav>"+Addons.truncar(baseImp)+"</baseImpGrav>");
                    out.println("<montoIce>0.00</montoIce>");
                    out.println("<montoIva>"+Addons.truncar(iva_2)+"</montoIva>");
                    out.println("<valorRetBienes>"+(porcentaje_retencion==30?valor_retenido:"0.00")+"</valorRetBienes>");
                    out.println("<valorRetServicios>"+(porcentaje_retencion==70?valor_retenido:"0.00")+"</valorRetServicios>");
                    out.println("<valRetServ100>"+(porcentaje_retencion==100?valor_retenido:"0.00")+"</valRetServ100>");

                    out.println(air);
                    
                    out.println("<pagoExterior>");
                    out.println("<pagoLocExt>01</pagoLocExt>");
                    out.println("<paisEfecPago>NA</paisEfecPago>");
                    out.println("<aplicConvDobTrib>NA</aplicConvDobTrib>");
                    out.println("<pagExtSujRetNorLeg>NA</pagExtSujRetNorLeg>");
                    out.println("</pagoExterior>");

                    String vec_serie_ret [] = ret_num_serie.split("-");
                    out.println("<estabRetencion1>"+((vec_serie_ret[0].compareTo("")!=0)?vec_serie_ret[0]:"000")+"</estabRetencion1>");
                    if(vec_serie_ret.length>1){
                        out.println("<ptoEmiRetencion1>"+((vec_serie_ret[1].compareTo("")!=0)?vec_serie_ret[1]:"000")+"</ptoEmiRetencion1>");
                    }else{
                        out.println("<ptoEmiRetencion1>000</ptoEmiRetencion1>");
                    }
                    //out.println("<estabRetencion1>000</estabRetencion1>");
                    //out.println("<ptoEmiRetencion1>000</ptoEmiRetencion1>");
                    out.println("<secRetencion1>"+ret_num_retencion+"</secRetencion1>");
                    out.println("<autRetencion1>"+( ret_autorizacion.length()>10 ? ret_autorizacion.substring(0, 10) : ret_autorizacion )+"</autRetencion1>");
                    out.println("<fechaEmiRet1>"+Addons.toFechaSQL(ret_fecha_emision)+"</fechaEmiRet1>");

                    out.println("<estabRetencion2>000</estabRetencion2>");
                    out.println("<ptoEmiRetencion2>000</ptoEmiRetencion2>");
                    out.println("<secRetencion2>0</secRetencion2>");
                    out.println("<autRetencion2>000</autRetencion2>");
                    out.println("<fechaEmiRet2>00/00/0000</fechaEmiRet2>");

                    out.println("<docModificado>0</docModificado>");
                    out.println("<estabModificado>000</estabModificado>");
                    out.println("<ptoEmiModificado>000</ptoEmiModificado>");
                    out.println("<secModificado>0</secModificado>");
                    out.println("<autModificado>000</autModificado>");

                    out.println("</detalleCompras>");
                    out.println();
                }
                compras.close();
*/


                out.println("</compras>");
                out.println();
                
            }catch(Exception e){
                e.printStackTrace();
            }





            /*  V E N T A S  */




            try{
                String ci_ruc = "";
                String tipoId = "05";
                String subtotal = "0.00";
                String descuento = "0.00";
                String iva_2 = "0.00";
                String valor_retenido_iva = "0.00";
                String valor_retenido_renta = "0.00";
                String impuesto = "RENTA";
                String num_comp = "1";
                ResultSet ventas = null;
                ResultSet rsRetencion = null;
                
                out.println("<ventas>");

                //  facturas

                ventas = objDB.consulta("select ruc,count(ruc) as num_comprobantes, tipo_documento, sum(subtotal) as subtotal, sum(iva_2) as iva_2, sum(descuento) as descuento "
                        + "from vta_factura_venta "
                        + "where fecha_emision between '"+anio+"-"+mes+"-01' and '"+anio+"-"+mes+"-"+fin_mes+"' and anulado=false group by ruc, tipo_documento");
                while(ventas.next()){
                    num_comp = (ventas.getString("num_comprobantes")!=null)?ventas.getString("num_comprobantes"):"1";
                    tipoId = (ventas.getString("tipo_documento")!=null)?ventas.getString("tipo_documento"):"05";
                    ci_ruc = (ventas.getString("ruc")!=null)?ventas.getString("ruc").replace("-", ""):"";
                    subtotal = (ventas.getString("subtotal")!=null)?ventas.getString("subtotal"):"0.00";
                    descuento = (ventas.getString("descuento")!=null)?ventas.getString("descuento"):"0.00";
                    iva_2 = (ventas.getString("iva_2")!=null)?ventas.getString("iva_2"):"0.00";
                    m_pasaporte = pasaporte.matcher(ci_ruc);

                    valor_retenido_iva = "0.00";
                    valor_retenido_renta = "0.00";
                    try{
                        rsRetencion = objDB.consulta("select T.impuesto, sum(D.valor_retenido) as valor_retenido from (tbl_retencion_venta_detalle as D inner join tbl_retencion_venta as R on D.id_retencion_venta=R.id_retencion_venta) "
                                + "inner join tbl_tabla_impuesto as T on T.id_tabla_impuesto=D.id_tabla_impuesto "
                                + "where R.id_factura_venta in (select id_factura_venta from vta_factura_venta where ruc='"+ci_ruc+"' and fecha_emision "
                                + "between '"+anio+"-"+mes+"-01' and '"+anio+"-"+mes+"-"+fin_mes+"') and R.anulado=false group by T.impuesto");
                        while(rsRetencion.next()){
                            impuesto = rsRetencion.getString("impuesto")!=null ? rsRetencion.getString("impuesto") : "0.00";
                            if(impuesto.compareTo("RENTA")==0){
                                valor_retenido_renta = (rsRetencion.getString("valor_retenido")!=null) ? rsRetencion.getString("valor_retenido") : "0.00";
                            }else{
                                valor_retenido_iva = (rsRetencion.getString("valor_retenido")!=null) ? rsRetencion.getString("valor_retenido") : "0.00";
                            }
                        }
                        rsRetencion.close();
                    }catch(Exception e){
                        e.printStackTrace();
                    }
                    
                    out.println("<detalleVentas>");
                    out.println("<tpIdCliente>"+tipoId+"</tpIdCliente>");
                    out.println("<idCliente>"+ci_ruc+"</idCliente>");
                    out.println("<tipoComprobante>18</tipoComprobante>");
                    out.println("<numeroComprobantes>"+num_comp+"</numeroComprobantes>");
                    out.println("<baseNoGraIva>0.00</baseNoGraIva>");
                    out.println("<baseImponible>0.00</baseImponible>");
                    out.println("<baseImpGrav>"+ Addons.truncar( Float.valueOf(subtotal) - Float.valueOf(descuento) )+"</baseImpGrav>");
                    out.println("<montoIva>"+(iva_2)+"</montoIva>");
                    out.println("<valorRetIva>"+valor_retenido_iva+"</valorRetIva>");
                    out.println("<valorRetRenta>"+valor_retenido_renta+"</valorRetRenta>");
                    out.println("</detalleVentas>");
                    out.println();
                }
                ventas.close();

                
                
                
                

                //  notas de credito


                
                
                
                
                
                
                ventas = objDB.consulta("select ruc,count(ruc) as num_comprobantes, tipo_documento,sum(valor) as subtotal,sum(iva_2) as iva_2 "
                        + "from vta_nota_credito_venta where fecha_emision " +
                        "between '"+anio+"-"+mes+"-01' and '"+anio+"-"+mes+"-"+fin_mes+"' and anulado=false group by ruc, tipo_documento");
                while(ventas.next()){
                    num_comp = (ventas.getString("num_comprobantes")!=null)?ventas.getString("num_comprobantes"):"1";
                    tipoId = (ventas.getString("tipo_documento")!=null)?ventas.getString("tipo_documento"):"05";
                    ci_ruc = (ventas.getString("ruc")!=null)?ventas.getString("ruc").replace("-", ""):"";
                    subtotal = (ventas.getString("subtotal")!=null)?ventas.getString("subtotal"):"0.00";
                    iva_2 = (ventas.getString("iva_2")!=null)?ventas.getString("iva_2"):"0.00";
                    m_pasaporte = pasaporte.matcher(ci_ruc);

                    valor_retenido_iva = "0.00";
                    valor_retenido_renta = "0.00";
                    /*try{
                        rsRetencion = objDB.consulta("select T.impuesto, sum(D.valor_retenido) as valor_retenido from (tbl_retencion_venta_detalle as D inner join tbl_retencion_venta as R on D.id_retencion_venta=R.id_retencion_venta) "
                                + "inner join tbl_tabla_impuesto as T on T.id_tabla_impuesto=D.id_tabla_impuesto "
                                + "where R.id_factura_venta in (select id_factura_venta from vta_factura_venta where ruc='"+ci_ruc+"' and fecha_emision "
                                + "between '"+anio+"-"+mes+"-01' and '"+anio+"-"+mes+"-"+fin_mes+"') and R.anulado=false group by T.impuesto");
                        while(rsRetencion.next()){
                            impuesto = rsRetencion.getString("impuesto")!=null ? rsRetencion.getString("impuesto") : "0.00";
                            if(impuesto.compareTo("RENTA")==0){
                                valor_retenido_renta = (rsRetencion.getString("valor_retenido")!=null) ? rsRetencion.getString("valor_retenido") : "0.00";
                            }else{
                                valor_retenido_iva = (rsRetencion.getString("valor_retenido")!=null) ? rsRetencion.getString("valor_retenido") : "0.00";
                            }
                        }
                        rsRetencion.close();
                    }catch(Exception e){
                        e.printStackTrace();
                    }*/

                    out.println("<detalleVentas>");
                    out.println("<tpIdCliente>"+tipoId+"</tpIdCliente>");
                    out.println("<idCliente>"+ci_ruc+"</idCliente>");
                    out.println("<tipoComprobante>04</tipoComprobante>");
                    out.println("<numeroComprobantes>"+num_comp+"</numeroComprobantes>");
                    out.println("<baseNoGraIva>0.00</baseNoGraIva>");
                    out.println("<baseImponible>0.00</baseImponible>");
                    out.println("<baseImpGrav>"+(subtotal)+"</baseImpGrav>");
                    out.println("<montoIva>"+(iva_2)+"</montoIva>");
                    out.println("<valorRetIva>"+valor_retenido_iva+"</valorRetIva>");
                    out.println("<valorRetRenta>"+valor_retenido_renta+"</valorRetRenta>");
                    out.println("</detalleVentas>");
                    out.println();
                }
                ventas.close();



                //  notas de débito

/*
                ventas = objDB.consulta("select ruc,count(ruc) as num_comprobantes, tipo_documento,sum(valor) as subtotal,sum(iva_2) as iva_2 from vta_nota_debito_venta where fecha_emision " +
                "between '"+anio+"-"+mes+"-01' and '"+anio+"-"+mes+"-"+fin_mes+"' and anulado=false group by ruc, tipo_documento");
                while(ventas.next()){
                    num_comp = (ventas.getString("num_comprobantes")!=null)?ventas.getString("num_comprobantes"):"1";
                    tipoId = (ventas.getString("tipo_documento")!=null)?ventas.getString("tipo_documento"):"05";
                    ci_ruc = (ventas.getString("ruc")!=null)?ventas.getString("ruc").replace("-", ""):"";
                    subtotal = (ventas.getString("subtotal")!=null)?ventas.getString("subtotal"):"0.00";
                    iva_2 = (ventas.getString("iva_2")!=null)?ventas.getString("iva_2"):"0.00";
                    m_pasaporte = pasaporte.matcher(ci_ruc);
                    //tipoId = "04"; // RUC
                    //if(ci_ruc.length()==13 && ci_ruc.compareTo("9999999999999")!=0 && this.esNumero(ci_ruc)){ // RUC
                    //    tipoId = "04";
                    //}else if(ci_ruc.length()==10 && ci_ruc.compareTo("9999999999")!=0 && this.esNumero(ci_ruc)){ // CI
                    //            tipoId = "05";
                    //}else if(ci_ruc.compareTo("9999999999999")!=0 && m_pasaporte.find()){ // pasaportte
                    //            tipoId = "06";
                    //}else if(ci_ruc.length()==13 && ci_ruc.compareTo("9999999999999")==0){ // consumidor final
                    //            tipoId = "07";
                    //}

                    valor_retenido_iva = "0.00";
                    valor_retenido_renta = "0.00";
                    

                    out.println("<detalleVentas>");
                    out.println("<tpIdCliente>"+tipoId+"</tpIdCliente>");
                    out.println("<idCliente>"+ci_ruc+"</idCliente>");
                    out.println("<tipoComprobante>05</tipoComprobante>");
                    out.println("<numeroComprobantes>"+num_comp+"</numeroComprobantes>");
                    out.println("<baseNoGraIva>0.00</baseNoGraIva>");
                    out.println("<baseImponible>0.00</baseImponible>");
                    out.println("<baseImpGrav>"+(subtotal)+"</baseImpGrav>");
                    out.println("<montoIva>"+(iva_2)+"</montoIva>");
                    out.println("<valorRetIva>"+valor_retenido_iva+"</valorRetIva>");
                    out.println("<valorRetRenta>"+valor_retenido_renta+"</valorRetRenta>");
                    out.println("</detalleVentas>");
                    out.println();
                }
                ventas.close();
*/

                out.println("</ventas>");
                out.println();
                
            }catch(Exception e){e.printStackTrace();}


            
            
            
            
            /*      V E N T A S   P O R   E S T A B L E C I M I E N T O     */



            

            try{
                ResultSet ventasEst = objDB.consulta("select id_sucursal, substr(serie_factura, 0, 4) as establecimiento, sum(subtotal) - sum(descuento) as ventas "
                        + "from vta_factura_venta where fecha_emision between '"+anio+"-"+mes+"-01' and '"+anio+"-"+mes+"-"+fin_mes+"' and anulado=false "
                        + "group by id_sucursal, establecimiento order by establecimiento;");
                int id_sucursal = 0;
                float ventas = 0;
                float notas_credito = 0;
                if(ventasEst.next()){
                    ventasEst.beforeFirst();
                    out.println("<ventasEstablecimiento>");
                    while(ventasEst.next()){
                        id_sucursal = ventasEst.getString("id_sucursal")!=null?ventasEst.getInt("id_sucursal"):0;
                        ventas = ventasEst.getString("ventas")!=null?ventasEst.getFloat("ventas"):0;
                        
                        try{
                            ResultSet rsNotCred = objDB.consulta("select sum(valor) from tbl_nota_credito_venta "
                                    + "where id_sucursal="+id_sucursal+" and fecha_emision between '"+anio+"-"+mes+"-01' and '"+anio+"-"+mes+"-"+fin_mes+"' and anulado = false");
                            if(rsNotCred.next()){
                                notas_credito = rsNotCred.getString(1)!=null?rsNotCred.getFloat(1):0;
                                rsNotCred.close();
                            }
                        }catch(Exception ex){ ex.printStackTrace(); }
                        
                        out.println("<ventaEst>");
                        out.println("<codEstab>"+(ventasEst.getString("establecimiento")!=null?ventasEst.getString("establecimiento"):"")+"</codEstab>");
                        out.println("<ventasEstab>"+ Addons.truncar( Addons.redondear(ventas-notas_credito) ) + "</ventasEstab>");
                        out.println("</ventaEst>");
                        out.println();
                    }
                    out.println("</ventasEstablecimiento>");
                    out.println();
                    ventasEst.close();
                }
            }catch(Exception e){e.printStackTrace();}
            
            



            /*      A N U L A D O S     */



            

            try{
                ResultSet anulados = objDB.consulta("select * from tbl_documento_anulado where fecha_emision " +
                "between '"+anio+"-"+mes+"-01' and '"+anio+"-"+mes+"-"+fin_mes+"' order by tipo_comprobante,secuencial;");
                String serie = "-";
                if(anulados.next()){
                    anulados.beforeFirst();
                    out.println("<anulados>");
                    while(anulados.next()){
                        serie = ((anulados.getString("serie")!=null)?anulados.getString("serie"):"-");
                        String vec_serie [] = serie.split("-");
                        out.println("<detalleAnulados>");
                        out.println("<tipoComprobante>"+((anulados.getString("tipo_comprobante")!=null)? Addons.rellenarCeros(anulados.getString("tipo_comprobante"), 2) :"")+"</tipoComprobante>");
                        out.println("<establecimiento>"+vec_serie[0]+"</establecimiento>");
                        out.println("<puntoEmision>"+vec_serie[1]+"</puntoEmision>");
                        out.println("<secuencialInicio>"+((anulados.getString("secuencial")!=null)?anulados.getString("secuencial"):"")+"</secuencialInicio>");
                        out.println("<secuencialFin>"+((anulados.getString("secuencial")!=null)?anulados.getString("secuencial"):"")+"</secuencialFin>");
                        out.println("<autorizacion>"+((anulados.getString("autorizacion")!=null)?anulados.getString("autorizacion"):"")+"</autorizacion>");
                        out.println("</detalleAnulados>");
                        out.println();
                    }
                    out.println("</anulados>");
                    out.println();
                    anulados.close();
                }
            }catch(Exception e){e.printStackTrace();}

            out.print("</iva>");

        } finally {
            conf.cerrar();
            objDB.cerrar();
            out.close();
        }
    }

    private boolean esNumero(String cad)
    {
        try{
            long num = Long.parseLong(cad);
            return true;
        }catch(Exception e){
            return false;
        }
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
