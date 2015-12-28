<%-- 
    Document   : correo
    Created on : 25-may-2015, 9:12:38
    Author     : SoulGael
--%>

<%@page import="jm.web.Archivo"%>
<%@page import="jm.adm.clas.Configuracion"%>
<%@page import="jm.inf.clas.Radius"%>
<%@page import="java.util.ArrayList"%>
<%@page import="java.util.List"%>
<%@page import="jm.web.Correo"%>
<%@page import="jm.seg.frmGeneraPdf"%>
<%@ page import = "jm.web.DataBase"%>
<%@ page import = "java.sql.ResultSet"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Envio de Correos Saitel</title>
    </head>
    <body>
        <h1>Envio de Correos</h1>
        <% 
            String ip = config.getServletContext().getInitParameter("_IP");
            int puerto = Integer.parseInt(config.getServletContext().getInitParameter("_PUERTO"));
            String db = config.getServletContext().getInitParameter("_DB");
            String dir = config.getServletContext().getInitParameter("_DIR");
            String usuario = config.getServletContext().getInitParameter("_USUARIO");
            String clave = config.getServletContext().getInitParameter("_CLAVE");
            String servidor_mail = "190.152.90.50";
            
            DataBase db_isp = new DataBase(ip, puerto, db, usuario, clave);
            
            String fecha = request.getParameter("fe");
            
            frmGeneraPdf pdf= new frmGeneraPdf();
            
            ResultSet rsC = db_isp.consulta("select c.razon_social, c.email, fv.fecha_emision, fv.documento_xml, fv.total ,clave_acceso, fv.serie_factura || '-' || fv.num_factura,id_factura_venta "
                    + "from tbl_factura_venta fv,tbl_cliente c "
                    + "where fv.id_cliente=c.id_cliente and c.email not in ('') and fv.estado_documento='a' and fecha_emision='"+fecha+"' order by fv.clave_acceso");
            if(db_isp.getFilas(rsC)==0){
                out.print("No hay Facturas que enviar a Clientes<br>");
            }
            else{
                String xml = "";
                try{
                    while(rsC.next()){
                        String nombres=rsC.getString(1);
                        String email=rsC.getString(2);
                        String fecha_emision=rsC.getString(3);
                        String documento_xml=rsC.getString(4);
                        String total=rsC.getString(5);
                        String clave_acceso=rsC.getString(6);
                        String serie_factura=rsC.getString(7);

                        Archivo archivo = new Archivo(ip, puerto, db, usuario, clave);
                        String path = String.valueOf(request.getRequestURL());
                        //path = path.substring(0, path.lastIndexOf("/"));
                        documento_xml=pdf.quitarTildes(documento_xml);
                        xml = dir + archivo.getArchivoXml(dir, "tbl_factura_venta", clave_acceso, "clave_acceso", documento_xml);
                        archivo.cerrar();

                        pdf.GenerarFactura(xml,dir,clave_acceso);

                        List adjuntos=new ArrayList();
                        adjuntos.add(xml+".xml");
                        adjuntos.add(xml+".pdf");
                        StringBuilder mensaje=new StringBuilder();

                        mensaje.append("<i>Estimado Cliente.</i><br>");
                        mensaje.append("<b>"+nombres+"</b><br><br>");
                        mensaje.append("Con el propósito de brindarle un mejor servicio, SAITEL cambió sus facturas físicas por electrónicas, lo que le permitirá contar con información "
                                + "inmediata sobre los valores facturados y fechas límites de pago. Con esta medida además, contribuimos a la preservación del medio ambiente.<br>"
                                +"El archivo adjunto corresponde a la Factura Electrónica, tributaria y legalmente válida para las declaraciones de impuestos ante el SRI.<br><br>");
                        mensaje.append("<b>Resumen</b><br>");
                        mensaje.append("<b>No de FACTURA: </b>"+serie_factura+"<br>");
                        mensaje.append("<b>FECHA DE EMISION: </b>"+fecha_emision+"<br>");
                        mensaje.append("<b>VALOR PAGADO: </b>"+total+"<br>");
                        mensaje.append("<b>CLAVE DE ACCESO: </b>"+clave_acceso+"<br><br>");
                        mensaje.append("También puede realizar la impresión de su documento en pdf <a href='http://www.saitel.ec/pag/electronico.html' target='_blank'> www.saitel.ec</a><br><br>");
                        mensaje.append("Atentamente.<br>");
                        mensaje.append("<b>Soluciones Informaticas Avanzadas y Telecomunicaciones SAITEL</b><br>");
                        mensaje.append("<b>IMPORTANTE:</b><br>Este correo es informativo, favor no responder a esta dirección, ya que no se encuentra habilitada para recibir mensajes.<br>");
                        if(Correo.enviar(servidor_mail, "info-tributaria@saitel.ec", email, "info-tributaria@saitel.ec", "", "SAITEL - FACTURA ELECTRONICA DE LA FECHA "+fecha_emision, mensaje, true,adjuntos))
                        {
                            out.println("Enviado Correctamente a: "+nombres+": <b>CORREO:</b>  "+email+"<br>");
                        }
                        else {
                            out.println("<b>ERROR: "+nombres+":  <b>CORREO: </b>  "+email+"</b><br>");
                        }
                    } 
                }catch(Exception e){
                    e.printStackTrace();
                }
            }
            
            //Result set Provedores
            ResultSet rsP = db_isp.consulta("select p.razon_social, rc.ret_num_serie||'-'||rc.ret_num_retencion, rc.ret_fecha_emision, rc.ret_ejercicio_fiscal, rc.ret_impuesto_retenido, "
                    + "rc.ret_ejercicio_fiscal_mes, rc.emision, rc.documento_xml, rc.clave_acceso, p.email "
                    + "from tbl_retencion_compra rc, tbl_factura_compra fc, tbl_proveedor p "
                    + "where estado_documento='a' and rc.id_factura_compra=fc.id_factura_compra and fc.id_proveedor=p.id_proveedor and fecha='"+fecha+"' and p.email not in('')");
            if(db_isp.getFilas(rsP)==0){
                out.print("No hay Facturas que enviar a Provedores<br>");
            }
            else{
                try{
                    while(rsP.next()){
                        String nombres=rsP.getString(1);
                        String serie_factura=rsP.getString(2);
                        String fecha_emision=rsP.getString(3);
                        String retenido=rsP.getString(5);
                        String ejercicio_fiscal=rsP.getString(6)+"-"+rsP.getString(4);
                        String documento_xml=rsP.getString(8);
                        String clave_acceso=rsP.getString(9);

                        String email=rsP.getString(10);

                        Archivo archivo = new Archivo(ip, puerto, db, usuario, clave);
                        String path = String.valueOf(request.getRequestURL());
                        //path = path.substring(0, path.lastIndexOf("/"));
                        documento_xml=pdf.quitarTildes(documento_xml);
                        String xml = dir + archivo.getArchivoXml(dir, "tbl_retencion_compra", clave_acceso, "clave_acceso", documento_xml);
                        archivo.cerrar();

                        pdf.GenerarFactura(xml,dir,clave_acceso);

                        List adjuntos=new ArrayList();
                        adjuntos.add(xml+".xml");
                        adjuntos.add(xml+".pdf");
                        StringBuilder mensaje=new StringBuilder();

                        mensaje.append("<i>Estimado Proveedor.</i><br>");
                        mensaje.append("<b>"+nombres+"</b><br><br>");
                        mensaje.append("Con el propósito de brindarle un mejor servicio, SAITEL cambió sus facturas físicas por electrónicas, lo que le permitirá contar con información inmediata"
                                + " sobre los valores facturados y fechas límites de pago. Con esta medida además, contribuimos a la preservación del medio ambiente.<br>"
                                + "El archivo adjunto corresponde a la Factura Electrónica, tributaria y legalmente válida para las declaraciones de impuestos ante el SRI.<br><br>");
                        mensaje.append("<b>Resumen</b><br>");
                        mensaje.append("<b>No de FACTURA: </b>"+serie_factura+"<br>");
                        mensaje.append("<b>FECHA DE EMISION: </b>"+fecha_emision+"<br>");
                        mensaje.append("<b>RETENCION DEL EJERCICIO FICAL: </b>"+ejercicio_fiscal+"<br>");
                        mensaje.append("<b>IMPUESTO RETENIDO: </b>"+retenido+"<br>");
                        mensaje.append("<b>CLAVE DE ACCESO: </b>"+clave_acceso+"<br><br>");
                        mensaje.append("También puede realizar la impresión de su documento en pdf <a href='http://www.saitel.ec/pag/electronico.html' target='_blank'> www.saitel.ec</a><br><br>");
                        mensaje.append("Atentamente.<br>");
                        mensaje.append("<b>Soluciones Informaticas Avanzadas y Telecomunicaciones SAITEL</b><br>");
                        mensaje.append("<b>IMPORTANTE:</b><br>Este correo es informativo, favor no responder a esta dirección, ya que no se encuentra habilitada para recibir mensajes.<br>");
                        if(Correo.enviar(servidor_mail, "info-tributaria@saitel.ec", email, "info-tributaria@saitel.ec", "", "SAITEL - RETENCION ELECTRONICA DE LA FECHA "+fecha_emision, mensaje, true,adjuntos))
                        {
                            out.println("Enviado Correctamente a: "+nombres+": <b>CORREO:</b>  "+email+"<br>");
                        }
                        else {
                            out.println("<b>ERROR: "+nombres+":  <b>CORREO: </b>  "+email+"</b><br>");
                        }
                    }
                }catch(Exception e){
                    e.printStackTrace();
                }
            }
            
            //Result set Nota de Credito
            ResultSet rsN = db_isp.consulta("select nc.razon_social, c.email,nc.serie_nota||'-'||nc.num_nota, nc.fecha_emision, nc.documento_modifica, nc.concepto, "
                        + "nc.cxp, nc.clave_acceso, nc.documento_xml "
                        + "from tbl_nota_credito_venta nc, tbl_cliente c "
                        + "where nc.estado_documento='a' and c.id_cliente=nc.id_cliente and fecha_emision='"+fecha+"' and c.email not in('')");
                    //+ "where nc.estado_documento='a' and c.id_cliente=nc.id_cliente and fecha_emision='20-05-2015' and c.email not in('') limit 1");
            if(db_isp.getFilas(rsN)==0){
                out.print("No hay Notas de Credito<br>");
            }
            else{
                try{
                    while(rsN.next()){
                        String nombres=rsN.getString(1);
                        String nota_credito=rsN.getString(3);
                        String fecha_emision=rsN.getString(4);
                        String doc_modifica=rsN.getString(5);
                        String concepto=rsN.getString(6);
                        String valor_total=rsN.getString(7);             
                        String clave_acceso=rsN.getString(8);
                        String documento_xml=rsN.getString(9);

                        String email=rsN.getString(2);

                        Archivo archivo = new Archivo(ip, puerto, db, usuario, clave);
                        String path = String.valueOf(request.getRequestURL());
                        //path = path.substring(0, path.lastIndexOf("/"));
                        documento_xml=pdf.quitarTildes(documento_xml);
                        String xml = dir + archivo.getArchivoXml(dir, "tbl_nota_credito_venta", clave_acceso, "clave_acceso", documento_xml);
                        archivo.cerrar();

                        pdf.GenerarFactura(xml,dir,clave_acceso);
                        List adjuntos=new ArrayList();
                        adjuntos.add(xml+".xml");
                        adjuntos.add(xml+".pdf");
                        StringBuilder mensaje=new StringBuilder();

                        mensaje.append("<i>Estimado Cliente.</i><br>");
                        mensaje.append("<b>"+nombres+"</b><br><br>");
                        mensaje.append("Con el propósito de brindarle un mejor servicio, SAITEL cambió sus facturas físicas por electrónicas, lo que le permitirá contar con información inmediata"
                                + " sobre los valores facturados y fechas límites de pago. Con esta medida además, contribuimos a la preservación del medio ambiente.<br>"
                                + "El archivo adjunto corresponde a la Factura Electrónica, tributaria y legalmente válida para las declaraciones de impuestos ante el SRI.<br><br>");
                        mensaje.append("<b>Resumen</b><br>");
                        mensaje.append("<b>No DE NOTA DE CREDITO: </b>"+nota_credito+"<br>");
                        mensaje.append("<b>FECHA DE EMISION: </b>"+fecha_emision+"<br>");
                        mensaje.append("<b>No DOCUMENTO AL QUE MODIFICA: </b>"+doc_modifica+"<br>");
                        mensaje.append("<b>CONCEPTO: </b>"+concepto+"<br>");
                        mensaje.append("<b>VALOR TOTAL: </b>"+valor_total+"<br>");
                        mensaje.append("<b>CLAVE DE ACCESO: </b>"+clave_acceso+"<br><br>");
                        mensaje.append("También puede realizar la impresión de su documento en pdf <a href='http://www.saitel.ec/pag/electronico.html' target='_blank'> www.saitel.ec</a><br><br>");
                        mensaje.append("Atentamente.<br>");
                        mensaje.append("<b>Soluciones Informaticas Avanzadas y Telecomunicaciones SAITEL</b><br>");
                        mensaje.append("<b>IMPORTANTE:</b><br>Este correo es informativo, favor no responder a esta dirección, ya que no se encuentra habilitada para recibir mensajes.<br>");
                        if(Correo.enviar(servidor_mail, "info-tributaria@saitel.ec", email, "info-tributaria@saitel.ec", "", "SAITEL - NOTAS DE CREDITO DE LA FECHA "+fecha_emision, mensaje, true,adjuntos))
                        {
                            out.println("Enviado Correctamente a: "+nombres+": <b>CORREO:</b>  "+email+"<br>");
                        }
                        else {
                            out.println("<b>ERROR: "+nombres+":  <b>CORREO: </b>  "+email+"</b><br>");
                        }
                    } 
                }catch(Exception e){
                    e.printStackTrace();
                }
            }
            
            db_isp.cerrar();
            %>
        
        
    </body>
</html>
