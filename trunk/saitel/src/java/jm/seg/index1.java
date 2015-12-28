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
import jm.seg.clas.Rol;
import jm.seg.clas.Usuario;
import java.sql.ResultSet;
import jm.Robot;
import jm.adm.clas.Configuracion;
import jm.inf.clas.Radius;
import jm.web.Correo;
import jm.web.Fecha;
import jm.web.Matriz;
        
/**
 *
 * @author Jorge
 */
public class index1 extends HttpServlet {
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
        int id_sucursal = (Integer)sesion.getAttribute("sucursal");
        String idRol = (String)sesion.getAttribute("id_rol");
        
        response.setContentType("text/html;charset=UTF-8");
        response.setHeader("Pragma", "no-cache"); 
        response.setHeader("Expires", "Mon, 01 Jan 2001 00:00:01 GMT"); 
        response.setHeader("Cache-Control", "no-store"); 
        response.setHeader("Cache-Control", "must-revalidate"); 
        response.setHeader("Cache-Control", "no-cache");  
        PrintWriter out = response.getWriter();

        Configuracion conf = new Configuracion(this._ip, this._puerto, this._db, usuario, clave);
        String id_sucursal_matriz = conf.getValor("id_sucursal_matriz");
        int dia_cortes = Integer.parseInt(conf.getValor("dia_cortes"));

        int hoy = Fecha.getDia();
        String dia_semana = Fecha.getDiaSemana(dia_cortes);
        if(dia_semana.compareTo("sábado")==0){
            hoy = hoy - 2;
        }
        if(dia_semana.compareTo("domingo")==0){
            hoy--;
        }
        
        
    
        

        Robot objRobot = new Robot(this._ip, this._puerto, this._db, usuario, clave);
        objRobot.generarEnFinMes();
        //objRobot.generarPrefacturas();
        objRobot.generarPrefacturasFaltantes();
        objRobot.generarComisiones();
        objRobot.setInstalacionesPorRetirar(Fecha.getFecha("ISO"));
        
        /*Radius dbRadius = new Radius(this._ip, this._puerto, this._db, usuario, clave);
        ResultSet rsActivados = objRobot.getInstalacionesActivaciones();
        String usuariosActivados[][] = Matriz.ResultSetAMatriz(rsActivados);
        dbRadius.setActivaciones(usuariosActivados);
        if(hoy >= dia_cortes){
            String usuariosCortados = objRobot.getInstalacionesCortar();
            dbRadius.setCortes(usuariosCortados);
        }
        String usuariosSuspendidos = objRobot.getInstalacionesSuspender();
        dbRadius.setSuspenciones(usuariosSuspendidos);*/
        

        try {
            out.print("<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">");
            out.print("<html xmlns=\"http://www.w3.org/1999/xhtml\">");
            /*out.print("<html>");*/
            out.print("<head>");
            out.print("<title>SISTEMA DE FACTURACION 1.0</title>");
            out.print("<link href=\"img/favicon.ico\" type=\"image/x-icon\" rel=\"shortcut icon\" />");
            out.print("<link type=\"text/css\" rel=\"stylesheet\" href=\"nucleo/Nucleo.css\">");
            out.print("<SCRIPT type=\"text/javascript\" src=\"nucleo/Nucleo.js\"></SCRIPT>");
            out.print("<SCRIPT type=\"text/javascript\" src=\"js.js\"></SCRIPT>");
            
            out.print("<script language='javascript'>");
            out.print("var _USR_ = '" + usuario + "';");
            out.print("var _ROL_ = " + idRol + ";");
            out.print("var _TIT_ = '" + (String)sesion.getAttribute("titulo") + "';");
            out.print("var _FECHA_ = '" + Fecha.getFecha("ISO") + "';");
            out.print("var _ANIO_ = '" + Fecha.getAnio() + "';");
            out.print("var _MENUS_=Array();");
            out.print("var _INV_INI_=false;");
            out.print("var _ID_SUC_="+id_sucursal+";");
            out.print("var _ID_SUC_MAT_="+id_sucursal_matriz+";");
            String cedula = "";
            Usuario usr = new Usuario(this._ip, this._puerto, this._db, usuario, clave);
            try{
                String pos_ayuda = usr.getPosAyuda(usuario);
                boolean display_ayuda = usr.getDisplayAyuda(usuario);
                String vec_pos_ayuda[] = pos_ayuda.split(":");
                out.print("var _X_="+vec_pos_ayuda[0]+";");
                out.print("var _Y_="+vec_pos_ayuda[1]+";");
                out.print("var _DA_="+display_ayuda+";");
                
                ResultSet rs = usr.consulta("select dni from tbl_empleado where alias='" + usuario + "'");
                if(rs.next()){
                    cedula = rs.getString("dni")!=null ? rs.getString("dni") : "";
                    rs.close();
                }
            }catch(Exception e){
                out.print("var _X_=769;");
                out.print("var _Y_=72;");
                out.print("var _DA_=true;");
                e.printStackTrace();
            }finally{
                usr.cerrar();
            }
            out.print("var _CEDULA_ = '" + cedula + "';");
            
            try{
                out.print("_INV_INI_="+conf.getValor("setInventarioInicial")+";");
            }catch(Exception e){}
            
            Rol rol = new Rol(this._ip, this._puerto, this._db, usuario, clave);
            try{
                ResultSet rs = rol.getAccesos(idRol);
                while(rs.next()){
                    out.print("_MENUS_.push('" + ((rs.getString(1)!=null)?rs.getString(1):"") + "');");
                }
            }catch(Exception e){
                e.printStackTrace();
            } finally {
                rol.cerrar();
            }
            
            out.print("</script>");
            out.print("</head>");

out.print("<body topmargin=\"0\" leftmargin=\"0\" rightmargin=\"0\">");

out.print("<noscript>"+
"<p class=\"nota\">"+
"La p&aacute;gina que est&aacute;s viendo requiere de JAVASCRIPT para su correcto funcionamiento."+
"Si lo ha deshabilitado intencionalmente, por favor, vuelve a activarlo."+
"</p>"+
"</noscript>");

out.print("<div class=\"barra0\">"
        + "<div id=\"barra0\"></div>"
        + "<div id=\"d_usuario\">usuario</div>"
        + "</div>");

out.print("<div id=\"gr_ayuda\" class=\"gr_ayuda\">" +
"<div id=\"barr_tit_ayuda\">"+
"<div id=\"tit_ayuda\" class=\"celda\" onmousedown=\"iniMover(event)\">AYUDA</div>"+
"<div id=\"des_ayuda\" class=\"celda\" onclick=\"desplegar();\"> ▼ </div>"+
"</div>" +
"<div id=\"gr_despliegue\">"+
"<div id=\"d_ayuda\" align=\"justify\">Por favor, haga clic en uno de los &iacute;tems de la parte izquierda para poder empezar.</div>" +
"<textarea id=\"aux_ayuda\" rows=\"3\"></textarea>"+
"</div>" +
"</div>");
 
out.print("<div>"+
"<div id=\"celda00\" class=\"celda\">"+
"<div id=\"d_logo\">&nbsp;</div>"+
"</div>"+
"<div id=\"celda01\" class=\"celda\">"+
"<marquee id=\"d_titulo\">&nbsp;</marquee>"+
"<div id=\"d_filtro\" class=\"grupo\">&nbsp;</div>"+
"</div>"+
"</div>");

out.print("<div>"+
"<div id=\"gr_menu\" class=\"celda grupo\"><div id=\"d_menu\"></div></div>"+
"<div id=\"celda11\" class=\"celda indicador\">◄</div>"+
"<div id=\"celda12\" class=\"celda grupo\">"+
"<div id=\"d_herra\"><div class=\"amague\">.</div></div>"+
"<div id=\"d_cuerpo\">"+
"<div id=\"d_fila\" class=\"d_fila\">"+
"<div id=\"d_1\" class=\"celda columna\">"+
"<div id=\"d_11\" class=\"columna1\"></div>"+
"</div>"+
"<div class=\"celda indicador1\" onmousedown=\"iniRedimensionarW('d_1')\">•</div>"+
"<div id=\"d_2\" class=\"celda columna\">"+
"<div id=\"d_21\" class=\"columna1\">&nbsp;</div>"+
"</div>"+
"<div class=\"celda indicador1\" onmousedown=\"iniRedimensionarW('d_2')\">•</div>"+
/*"<div id=\"d_3\" class=\"celda columna\">"+
"<div id=\"d_31\" class=\"columna1\">&nbsp;</div>"+
"</div>"+
"<div class=\"celda indicador1\" onmousedown=\"iniRedimensionarW('d_3')\" onmouseup=\"_RE(redimensionarW)\">•</div>"+*/
"</div>"+
"</div>"+
"<div>"+
"<div id=\"celda41\" class=\"celda\">&nbsp;&nbsp;&nbsp;</div>"+
"<div class=\"celda indicador\" onmousedown=\"_OBJ=true;redimensionarH('+');\" onmouseup=\"_OBJ=false;\">▼</div>"+
"<div class=\"celda\">&nbsp;&nbsp;&nbsp;</div>"+
"<div class=\"celda indicador\" onmousedown=\"_OBJ=true;redimensionarH('-');\" onmouseup=\"_OBJ=false;\">▲</div>"+
"</div>"+
"</div>"+
"</div>");

out.print("<div id='pie'>&nbsp;</div>");              
  
            out.print("</body>");
            out.print("</html>");
        } finally {
            //dbRadius.cerrar();
            objRobot.cerrar();
            conf.cerrar();
            out.close();
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
