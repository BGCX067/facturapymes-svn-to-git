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

package jm.seg;

import java.io.*;

import javax.servlet.*;
import javax.servlet.http.*;
import java.sql.ResultSet;
import java.sql.SQLException;
import jm.seg.clas.*;
import jm.adm.clas.Configuracion;
import jm.adm.clas.PuntoEmision;
import jm.adm.clas.Sucursal;
import jm.nom.clas.Empleado;


public class Autenticar extends HttpServlet {   
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
    * Procesa peticiones HTTP del m�todo <code>POST</code>.
    * @param request petici�n al servlet
    * @param response respuesta del servlet
    */
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        HttpSession sesion = request.getSession(true);
        
        String msg = "Usuario o Contraseña incorrectos.";
        String usuario = request.getParameter("u");
        String clave = request.getParameter("c");
        
        Auditoria auditoria = new Auditoria(this._ip, this._puerto, this._db, usuario, clave);
        Usuario us = new Usuario(this._ip, this._puerto, this._db, usuario, clave);
        Configuracion conf = new Configuracion(this._ip, this._puerto, this._db, usuario, clave);
        
        ResultSet id_r = us.autenticar(usuario, clave);
        try{
            if(id_r != null){
                if(id_r.next()){
                    String id_rol = (id_r.getString("id_rol")!=null) ? id_r.getString("id_rol") : "";
                    String ips = (id_r.getString("autenticacion_ip")!=null) ? id_r.getString("autenticacion_ip") : "";
                    boolean estado = (id_r.getString("estado")!=null) ? id_r.getBoolean("estado") : false;
                    
                    msg = "No tiene permiso para autenticarse ya que está inactivo. Por favor, contáctese con el administrador del sistema para que le habilite como un usuario activo.";
                    if(estado){          
                        String ip = request.getRemoteAddr();
                        msg = "No tiene autorización para autenticarse desde éste computador: " + ip;
                        if(ips.indexOf(ip)>=0){
                            Sucursal objSucursal = new Sucursal(this._ip, this._puerto, this._db, usuario, clave);
                            int id_sucursal = objSucursal.getIdSucursal(ip);
                            objSucursal.cerrar();

                            msg = "El computador "+ip+" no pertenece a ninguna sucursal. Por favor, contáctese con el administrador del sistema para mayor información.";
                            if(id_sucursal!=-1){
                                String razon_social = conf.getValor("razon_social");
                                String usr = id_r.getString("alias");

                                msg = "Ha ocurrido un error al cargar algunos valores de configuración. Por favor, vuelva a intentarlo más tarde o contáctese con el administrador del sistema para mayor información.";
                                if(usr.compareTo("")!=0 && razon_social.compareTo("")!=0){
                                    /* GUARDO LA NUEVA SESION */
                                    us.setSesion(usr, sesion.getId());

                                    PuntoEmision objPuntoEmision = new PuntoEmision(this._ip, this._puerto, this._db, usuario, clave);
                                    int id_punto_emision = objPuntoEmision.getIdPuntoEmision(usr);
                                    objPuntoEmision.cerrar();

                                    Empleado objEmpleado = new Empleado(this._ip, this._puerto, this._db, usuario, clave);
                                    String empleado = objEmpleado.getNombres(usr);
                                    objEmpleado.cerrar();

                                    //ip = "201.219.44.50";
                                    //id_sucursal = 5;
                                    //id_punto_emision = 20;
                                    
                                    sesion.setAttribute("usuario", usr);
                                    sesion.setAttribute("clave", clave);
                                    sesion.setAttribute("id_rol", id_rol);
                                    sesion.setAttribute("titulo", razon_social);
                                    sesion.setAttribute("ip", ip);
                                    sesion.setAttribute("sucursal", id_sucursal);
                                    sesion.setAttribute("id_punto_emision", id_punto_emision);
                                    sesion.setAttribute("empleado", empleado);


                                    auditoria.setRegistro(usr, ip, "AUTENTICACION DEL USUARIO: "+usr);

                                    msg = "0";
                                }
                            }
                             /*else{       para pruebas  
                                 msg = "0";
                            }*/

                        }
                        /*else{            para pruebas  
                             msg = "0";
                        }*/
                    }
                    id_r.close();
                }
            }
        }catch(SQLException e){
            e.printStackTrace();
        }finally{
            conf.cerrar();
            auditoria.cerrar();
            us.cerrar();
        }
        
        response.setContentType("text/html;charset=UTF-8");
        response.setHeader("Pragma", "no-cache"); 
        response.setHeader("Expires", "Mon, 01 Jan 2001 00:00:01 GMT"); 
        response.setHeader("Cache-Control", "no-store"); 
        response.setHeader("Cache-Control", "must-revalidate"); 
        response.setHeader("Cache-Control", "no-cache");        
        PrintWriter out = response.getWriter();
        try {
            out.print(msg);
        } finally { 
            out.close();
        }
    }
    
}