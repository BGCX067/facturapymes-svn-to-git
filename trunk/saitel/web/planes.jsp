<%-- 
    Document   : index
    Created on : 04/10/2011, 11:35:00 AM
    Author     : Jorge
--%>

<%@page import="jm.fac.clas.PlanServicio"%>
<%@page import="jm.web.Cadena"%>
<%@page import="jm.inf.clas.Radius"%>
<%@page import="jm.adm.clas.Configuracion"%>
<%@ page import = "jm.web.DataBase"%>
<%@ page import = "java.sql.ResultSet"%>


<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
   "http://www.w3.org/TR/html4/loose.dtd">

<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>JSP Page</title>
    </head>
    <body>
<%
try{
    String ip = config.getServletContext().getInitParameter("_IP");
    int puerto = Integer.parseInt(config.getServletContext().getInitParameter("_PUERTO"));
    String db = config.getServletContext().getInitParameter("_DB");
    String usuario = "postgres";
    String clave = "717379saitelSAITEL";

    /*Configuracion conf = new Configuracion(ip, puerto, db, usuario, clave);
    String rad_db = conf.getValor("rad_db");
    String rad_ip = conf.getValor("rad_ip");
    String rad_puerto = conf.getValor("rad_puerto");
    String rad_usuario = conf.getValor("rad_usuario");
    String rad_clave = conf.getValor("rad_clave");
    conf.cerrar();
*/
    DataBase db_isp = new DataBase(ip, puerto, db, usuario, clave);
    Radius db_radius = new Radius(ip, puerto, db, usuario, clave);
    //PlanServicio objPlanServicio = new PlanServicio(ip, puerto, db, usuario, clave);

    ResultSet rs = db_isp.consulta("SELECT substr( plan, 1, 4) || substr( split_part(solo_plan, ' ', 2), 1, 5) || substr( split_part(solo_plan, ' ', 3), 1, 4) || burst_limit::varchar, "
                + "up_max_limit || 'k/' || max_limit || 'k' || ' ' || "
                + "up_burst_limit || 'k/' || burst_limit || 'k' || ' ' || "
                + "up_burst_threshold || 'k/' || burst_threshold || 'k' || ' ' || "
                + "up_burst_time || '/' || burst_time || ' ' || "
                + "comparticion::int || ' ' || "
                + "up_limit_at || 'k/' || limit_at || 'k' from vta_plan_servicio");
    String plan = "";
    String rateLimit = "";
    db_radius.ejecutar("delete from radgroupreply;");
    db_radius.ejecutar("delete from radgroupcheck;");
    db_radius.ejecutar("delete from radusergroup;");

    db_radius.ejecutar("INSERT INTO radgroupcheck (GroupName, Attribute, Value, op) VALUES ('DEFAULT', 'Auth-Type', 'Accept',':=');");
    db_radius.ejecutar("INSERT INTO radgroupreply (GroupName, Attribute, Value, op) VALUES ('DEFAULT', 'Mikrotik-Rate-Limit', '512k/512k',':=');");
    db_radius.ejecutar("INSERT INTO radgroupreply (GroupName, Attribute, Value, op) VALUES ('DEFAULT', 'Session-Timeout', '200',':=');");
    db_radius.ejecutar("INSERT INTO radgroupreply (GroupName, Attribute, Value, op) VALUES ('DEFAULT', 'Framed-Pool', 'metidos',':=');");
    
    db_radius.ejecutar("INSERT INTO radgroupcheck (GroupName, Attribute, Value, op) VALUES ('cortados', 'Auth-Type', 'Accept',':=');");
    db_radius.ejecutar("INSERT INTO radgroupreply (GroupName, Attribute, Value, op) VALUES ('cortados', 'Mikrotik-Rate-Limit', '512k/512k',':=');");
    db_radius.ejecutar("INSERT INTO radgroupreply (GroupName, Attribute, Value, op) VALUES ('cortados', 'Session-Timeout', '200',':=');");
    db_radius.ejecutar("INSERT INTO radgroupreply (GroupName, Attribute, Value, op) VALUES ('cortados', 'Framed-Pool', 'cortados',':=');");
    
    db_radius.ejecutar("INSERT INTO radgroupcheck (GroupName, Attribute, Value, op) VALUES ('suspendidos', 'Auth-Type', 'Accept',':=');");
    db_radius.ejecutar("INSERT INTO radgroupreply (GroupName, Attribute, Value, op) VALUES ('suspendidos', 'Mikrotik-Rate-Limit', '512k/512k',':=');");
    db_radius.ejecutar("INSERT INTO radgroupreply (GroupName, Attribute, Value, op) VALUES ('suspendidos', 'Session-Timeout', '200',':=');");
    db_radius.ejecutar("INSERT INTO radgroupreply (GroupName, Attribute, Value, op) VALUES ('suspendidos', 'Framed-Pool', 'suspendidos',':=');");
    
    db_radius.ejecutar("INSERT INTO radusergroup (Username, Groupname) VALUES ('DEFAULT','DEFAULT');");

    while(rs.next()){
        plan = rs.getString(1)!=null ? rs.getString(1) : "";
        rateLimit = rs.getString(2)!=null ? rs.getString(2) : "";

        db_radius.ejecutar("INSERT INTO radgroupcheck(groupname, attribute, op, value) VALUES('"+plan+"', 'Auth-Type', ':=', 'Local');");
        db_radius.ejecutar("INSERT INTO radgroupcheck(groupname, attribute, op, value) VALUES('"+plan+"', 'Cleartext-Password', ':=', '');");
        if(plan.toUpperCase().indexOf("NOCT")>=0){
            db_radius.ejecutar("INSERT INTO radgroupcheck(groupname, attribute, op, value) VALUES('"+plan+"', 'Login-Time', ':=', '0900-2100');");
        }

        db_radius.ejecutar("INSERT INTO radgroupreply(groupname, attribute, op, value) VALUES('"+plan+"', 'Mikrotik-Rate-Limit', ':=', '"+rateLimit+"');");
        db_radius.ejecutar("INSERT INTO radgroupreply(groupname, attribute, op, value) VALUES('"+plan+"', 'Session-Timeout', ':=', '20000');");
        db_radius.ejecutar("INSERT INTO radgroupreply(groupname, attribute, op, value) VALUES('"+plan+"', 'Framed-Pool', ':=', 'dhcp_clientes');");
    }

    ResultSet rs1 = db_isp.consulta("SELECT substr( plan, 1, 4) || substr( split_part(solo_plan, ' ', 2), 1, 5) || substr( split_part(solo_plan, ' ', 3), 1, 4) || burst_limit::varchar, radusername, I.estado_servicio "
            + "from vta_plan_servicio as P inner join tbl_instalacion as I on I.id_plan_actual=P.id_plan_servicio where estado_servicio not in ('t', 'e')");
    String mac = "";
    String estado = "c";
    while(rs1.next()){
        plan = rs1.getString(1)!=null ? rs1.getString(1) : "";
        mac = rs1.getString("radusername")!=null ? rs1.getString("radusername") : "";
        estado = rs1.getString("estado_servicio")!=null ? rs1.getString("estado_servicio") : "";
        if(estado.compareTo("s")==0){
            db_radius.ejecutar("insert into radusergroup(username, groupname, priority) values('"+mac+"', 'suspendidos', 0);");
        }else if(estado.compareTo("c")==0){
            db_radius.ejecutar("insert into radusergroup(username, groupname, priority) values('"+mac+"', 'cortados', 0);");
        }else{
            db_radius.ejecutar("insert into radusergroup(username, groupname, priority) values('"+mac+"', '"+plan+"', 1);");
        }
    }
    out.print("<h3>CREACION DE PLANES Y REGISTRO DE USUARIOS EN EL FREE RADIUS TERMINADA</h3>");

    try{
        rs.close();
    }catch(Exception ex){}
    //db_isp.cerrar();
    db_radius.cerrar();
}catch(Exception e){
    out.print("<h3>" + e.getMessage() + "</h3>");
}
%>
<script>  window.setTimeout("window.close();", 3000);</script>
    </body>
</html>
