<%--
    Document   : index
    Created on : 04/10/2011, 11:35:00 AM
    Author     : Jorge
--%>

<%@page import="java.io.InputStream"%>
<%@page import="java.io.InputStreamReader"%>
<%@page import="java.io.BufferedReader"%>
<%@page import="jm.fac.clas.PlanServicio"%>
<%@page import="jm.web.Cadena"%>
<%@page import="jm.fac.clas.Radius"%>
<%@page import="jm.adm.clas.Configuracion"%>
<%@ page import = "jm.web.DataBase"%>
<%@ page import = "java.sql.ResultSet"%>


<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
   "http://www.w3.org/TR/html4/loose.dtd">

<html>
    <head>
        <meta http-equiv="Content-Type" content="application/json">
        <title>JSP Page</title>
    </head>
    <body>

<%
String airC_ip = "";
String airC_puerto = "";
String airC_usuario = "";
String airC_clave = "";
String radusername = "ubnt";
String radclave = "ubnt";
String _DIR = "";
String salida = "";
try{
    String ip = config.getServletContext().getInitParameter("_IP");
    int puerto = Integer.parseInt(config.getServletContext().getInitParameter("_PUERTO"));
    String db = config.getServletContext().getInitParameter("_DB");
    String usuario = config.getServletContext().getInitParameter("_USUARIO");
    String clave = config.getServletContext().getInitParameter("_CLAVE");
    _DIR = config.getServletContext().getInitParameter("_DIR");

    Configuracion conf = new Configuracion(ip, puerto, db, usuario, clave);
    airC_ip = conf.getValor("airC_ip");
    airC_puerto = conf.getValor("airC_puerto");
    airC_usuario = conf.getValor("airC_usuario");
    airC_clave = conf.getValor("airC_clave");
    conf.cerrar();

    try {
        ProcessBuilder pb = new ProcessBuilder("wget",
                "-v",
                "-O",
                _DIR+"ac-response.txt",
                "--post-file",
                _DIR+"ac-request.txt",
                "--header=\"Content-Type: application/json\"",
                "http://"+airC_ip+":"+airC_puerto+"/api/task?auth.user="+radusername+"&auth.password="+radclave
                );
        pb.redirectErrorStream(true);
        Process proceso = pb.start();
        InputStream is = proceso.getInputStream();
        InputStreamReader isr = new InputStreamReader(is);
        BufferedReader br = new BufferedReader(isr);
        String line;
        while ((line = br.readLine()) != null) {
            salida += line + "<br />";
        }
    }catch(Exception e){
        salida = e.getMessage();
        e.printStackTrace();
    }


    /*DataBase db_isp = new DataBase(ip, puerto, db, usuario, clave);
    String id_instalacion = "";
    try{
        ResultSet rs = db_isp.consulta("select id_instalacion, radusername, radclave from tbl_instalacion where id_instalacion="+2255);
        if(rs.next()){
            radusername = rs.getString("radusername")!=null ? rs.getString("radusername") : "";
            radusername = rs.getString("radclave")!=null ? rs.getString("radclave") : "";
        }
        rs.close();
    }catch(Exception ex){}
    db_isp.cerrar();*/

}catch(Exception e){
    out.print("<h3>" + e.getMessage() + "</h3>");
}
%>

<form action='<% out.print("http://"+airC_ip+":"+airC_puerto+"/api/task"); %>' method='post'  enctype='multipart/form-data' target='procesaTransferencia' onsubmit='return iniciaTransferencia();' >
    <input id='auth.user' name='auth.user' type='text' value="<% out.print(radusername); %>" />
    <input id='auth.password' name='auth.password' type='text' value="<% out.print(radclave); %>" />
    <input id='archivo' name='archivo' type='file'  />
    <!-- <input type="button" value="enviar" onclick="ver()" /> -->
    <input type="submit" value="enviar" />
</form>
    
    <% out.print(salida); %>

    </body>
</html>
