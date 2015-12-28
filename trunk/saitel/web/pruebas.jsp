<%-- 
    Document   : pruebas
    Created on : 24/09/2014, 12:55:54 PM
    Author     : desarrollo
--%>
<%@page import="java.util.Date"%>
<%@page import="jm.web.Fecha"%>
<%@ page import = "jm.web.DataBase"%>
<%@ page import = "java.sql.ResultSet"%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>JSP Page</title>
    </head>
    <body>

    <table>
    <%
    String ip = config.getServletContext().getInitParameter("_IP");
    int puerto = Integer.parseInt(config.getServletContext().getInitParameter("_PUERTO"));
    String db = config.getServletContext().getInitParameter("_DB");
    String usuario = "postgres";
    String clave = "GobSt49..";

    DataBase dbIsp = new DataBase(ip, puerto, db, usuario, clave);
    
    try{
        ResultSet rs = dbIsp.consulta("select distinct id_cliente, ruc, razon_social, fecha_instalacion, txt_sucursal, id_sucursal, forma_pago "
                + "from vta_instalacion "
                + "where estado_servicio in ('a', 'c', 's') order by id_sucursal, fecha_instalacion");
        int i=1;
        while(rs.next()){
            String id_cliente = rs.getString("id_cliente")!=null ? rs.getString("id_cliente") : "";
            String forma_pago = rs.getString("forma_pago")!=null ? rs.getString("forma_pago") : "";
            String fecha_instalacion = rs.getString("fecha_instalacion")!=null ? rs.getString("fecha_instalacion") : "2014-09-01";
            long inicio_promocion = Fecha.getTimeStamp("2014-09-01");
            boolean ok =true;
            if(forma_pago.compareTo("TAR")==0 && Fecha.getTimeStamp(fecha_instalacion)>=inicio_promocion){
                ok = false;
            }
            if(ok){
                ResultSet rs2 = dbIsp.consulta("select F.*, D.descripcion_mas from tbl_factura_venta as F inner join tbl_factura_venta_detalle as D on D.id_factura_venta=F.id_factura_venta "
                        + "where id_cliente="+id_cliente+" and anulado=false and (lower(D.descripcion_mas) like '%instalacion%' or lower(D.descripcion_mas) like '%instalación%')");
                if(dbIsp.getFilas(rs2)==0){
                    String ruc = rs.getString("ruc")!=null ? rs.getString("ruc") : "";
                    String razon_social = rs.getString("razon_social")!=null ? rs.getString("razon_social") : "";
                    String txt_sucursal = rs.getString("txt_sucursal")!=null ? rs.getString("txt_sucursal") : "";
                    out.print("<tr><td>"+i+"</td><td>"+txt_sucursal+"</td><td>"+ruc+"</td><td>"+razon_social+"</td><td>"+fecha_instalacion+"</td></tr>");
                    i++;
                }
            }
        }
    } catch (Exception ex) {
	out.print(ex.getMessage());
    } finally{
        dbIsp.cerrar();
    }
    %>
    
    </table>   
    </body>
</html>
