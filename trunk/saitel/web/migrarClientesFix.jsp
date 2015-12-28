<%-- 
    Document   : migrarClientesFix
    Created on : 28/05/2015, 10:44:15
    Author     : jorge
--%>

<%@page import="java.io.FileReader"%>
<%@page import="java.io.BufferedReader"%>
<%@page import="java.io.File"%>
<%@page import="java.sql.ResultSet"%>
<%@page import="jm.web.DataBase"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>JSP Page</title>
    </head>
    <body>
        <%
            String ip = "127.0.0.1";
            int puerto = 5432;
            String db = "db_isp";
            String usuario = "postgres";
            String clave = "postgres";

            DataBase db_isp = new DataBase(ip, puerto, db, usuario, clave);
            try{
                String id_sucursal = "1";
                String linea = "";
                String ruc="";
                String tipo_documento="";
                String razon_social="";
                String direccion="";
                String telefono="";
                String movil_claro="";
                File file = new File("/home/jorge/", "clientes_fix_ibarra1.csv");
                BufferedReader buffer = new BufferedReader(new FileReader(file));
                while ((linea = buffer.readLine()) != null) {
                    String cliente[] = linea.split(";");
                    ruc = cliente[1].replace("-", "");
                    tipo_documento = ruc.length()==13 ? "04" : (ruc.length()==10 ? "05" : "06");
                    razon_social = cliente[0];
                    direccion = cliente[2];
                    telefono = cliente[4];
                    movil_claro = cliente[3];
                    ResultSet rs = db_isp.consulta("select * from tbl_cliente where ruc='"+ruc+"'");
                    if(db_isp.getFilas(rs)>0){
                        db_isp.ejecutar("update tbl_cliente set id_sucursal="+id_sucursal+", tipo_documento='"+tipo_documento+"', direccion='"+direccion+"', telefono='"+telefono+"', movil_claro='"+movil_claro+"' where ruc='"+ruc+"'");
                        
                        out.print("update tbl_cliente set id_sucursal="+id_sucursal+", tipo_documento='"+tipo_documento+"', direccion='"+direccion+"', telefono='"+telefono+"', movil_claro='"+movil_claro+"' where ruc='"+ruc+"';<br />");
                    }else{
                        db_isp.ejecutar("insert into tbl_cliente(id_sucursal, ruc, tipo_documento, razon_social, direccion, telefono, movil_claro) "
                                + "values("+id_sucursal+", '"+ruc+"', '"+tipo_documento+"', '"+razon_social+"', '"+direccion+"', '"+telefono+"', '"+movil_claro+"')");
                        
                        out.print("insert into tbl_cliente(id_sucursal, ruc, tipo_documento, razon_social, direccion, telefono, movil_claro) "
                                + "values("+id_sucursal+", "+ruc+"', '"+tipo_documento+"', '"+razon_social+"', '"+direccion+"', '"+telefono+"', '"+movil_claro+"');<br />");
                    }
                }
            }catch(Exception e){
                out.print("<h3>" + e.getMessage() + "</h3>");
            }finally{
                db_isp.cerrar();
            }
        %>
    </body>
</html>
