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

package jm.fac;

import java.io.*;
import java.sql.ResultSet;

import javax.servlet.*;
import javax.servlet.http.*;
import jm.adm.clas.Configuracion;
import jm.adm.clas.Sucursal;
import jm.adm.clas.Ubicacion;
import jm.fac.clas.Cliente;
import jm.fac.clas.Instalacion;
import jm.fac.clas.Sector;
import jm.seg.clas.Rol;
import jm.web.DatosDinamicos;

/**
 *
 * @author Jorge
 */
public class getClienteInstalacion extends HttpServlet {
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
        int id_sucursal_sesion = (Integer)sesion.getAttribute("sucursal");
        String id_rol = (String)sesion.getAttribute("id_rol");
        
        response.setContentType("text/html;charset=UTF-8");
        response.setHeader("Pragma", "no-cache");
        response.setHeader("Expires", "Mon, 01 Jan 2001 00:00:01 GMT");
        response.setHeader("Cache-Control", "no-store");
        response.setHeader("Cache-Control", "must-revalidate");
        response.setHeader("Cache-Control", "no-cache");

        PrintWriter out = response.getWriter();

        String estab = request.getParameter("estab");
        String ruc = request.getParameter("ruc");
        //String fun = request.getParameter("fun");
        
        Configuracion conf = new Configuracion(this._ip, this._puerto, this._db, usuario, clave);
        String id_sucursal_matriz = conf.getValor("id_sucursal_matriz");
        conf.cerrar();

        Sucursal objSuc = new Sucursal(this._ip, this._puerto, this._db, usuario, clave);
        ResultSet rsSucursales = objSuc.getSucursales();
        objSuc.cerrar();

        Instalacion objInstalacion = new Instalacion(this._ip, this._puerto, this._db, usuario, clave);
        //String ip = objInstalacion.getSigIpSucursal(id_sucursal_sesion);
        String ip = "192.168.";

        Sector objSector = new Sector(this._ip, this._puerto, this._db, usuario, clave);
        ResultSet rsSectores = objSector.getSectores(id_sucursal_sesion);

        Cliente objCliente = new Cliente(this._ip, this._puerto, this._db, usuario, clave);
        
        Rol objRol = new Rol(this._ip, this._puerto, this._db, usuario, clave);
        
        try {
                String id_cliente = "";
                String id_provincia = "210";
                String id_ciudad = "21001";
                String razon_social = "";
                String ciudad = "";
                String telefono = "";
                String movil_claro = "";
                String movil_movistar = "";
                String direccion = "";
                try{
                    ResultSet rs = objCliente.getIdCliente(estab, ruc);
                    if(rs.next()){
                        id_cliente = rs.getString("id_cliente")!=null ? rs.getString("id_cliente") : "";
                        id_provincia = rs.getString("id_provincia")!=null ? rs.getString("id_provincia") : "210";
                        id_ciudad = rs.getString("id_ciudad")!=null ? rs.getString("id_ciudad") : "21001";
                        razon_social = rs.getString("razon_social")!=null ? rs.getString("razon_social") : "";
                        ciudad = rs.getString("ciudad")!=null ? rs.getString("ciudad") : "";
                        telefono = rs.getString("telefono")!=null ? rs.getString("telefono") : "";
                        movil_claro = rs.getString("movil_claro")!=null ? rs.getString("movil_claro") : "";
                        movil_movistar = rs.getString("movil_movistar")!=null ? rs.getString("movil_movistar") : "";
                        direccion = rs.getString("direccion")!=null ? rs.getString("direccion") : "";
                        rs.close();
                    }
                }catch(Exception e){
                    e.printStackTrace();
                }
                
                String [][] es_instalacion1 = {{"t","Instalaci&oacute;n"},{"f","Re-Instalaci&oacute;n"}};
                String [][] cobrar1 = {{"t","SI"},{"f","NO"}};
                String [][] tipo_instalacion1 = {{"a","Antena"},{"f","Fibra"}};
                

        
                Ubicacion objUbicacion = new Ubicacion(this._ip, this._puerto, this._db, usuario, clave);
                ResultSet rsProvincias = objUbicacion.getUbicaciones("1");
                ResultSet rsCiudades = objUbicacion.getUbicaciones(id_provincia);
                ResultSet rsParroquias = objUbicacion.getUbicaciones(id_ciudad);
                
                
                
                out.print("obj»datCliente^fun»fac_setClienteInstall()^frm»");
                
                out.print("<input type='hidden' id='id_cliente' name='id_cliente' value='"+id_cliente+"' />");
                out.print("<input type='hidden' id='axTelf' name='axTelf' value='"+(telefono.compareTo("")!=0 ? telefono : (movil_claro.compareTo("")!=0 ? movil_claro : movil_movistar))+"' />");
                out.print("<table width='100%'><tr><td width='75'>Cliente: </td><td id='axRS'>"+razon_social+"</td>");
                out.print("<td>Cant&oacute;n: </td><td id='axCi'>"+ciudad+"</td></tr>");
                out.print("<tr><td>Tel&eacute;fonos:</td><td id='axTe'>"+telefono+" &nbsp;&nbsp;&nbsp; Claro:"+movil_claro+" &nbsp;&nbsp;&nbsp; Movistar:"+movil_movistar+"</td>");
                out.print("<td>Direcci&oacute;n: </td><td id='axDi'>"+direccion+"</td></tr></table>");

                out.print("<hr />");

                out.print("<table>");
                out.print("<tr><td>Tipo de registro: <span class='marca'>*</span></td>");
                out.print("<td>" + DatosDinamicos.combo("es_instalacion", "", es_instalacion1, "_('bntFat').style.visibility=this.value=='t'?'visible':'hidden'") + "</td></tr>");
                out.print("<tr><td>N&uacute;mero: <span class='marca'>*</span></td><td><span id='auxIdSuc'>"+String.valueOf(id_sucursal_sesion)+"</span>-<input id='num_instalacion' name='num_instalacion' type='text' size='10' value='' readonly onkeypress=\"_numero(event);\" /></td></tr>");

                if(Integer.valueOf(id_sucursal_matriz) == id_sucursal_sesion){
                     out.print("<tr><td>Sucursal: <span class='marca'>*</span></td><td>" + DatosDinamicos.combo(rsSucursales, "id_sucursal", String.valueOf(id_sucursal_sesion), "fac_setInstalacion();fac_setSectores();", "", 100) + "</td></tr>");
                }else{
                    String id_sucursal = String.valueOf(id_sucursal_sesion);
                    out.print("<tr><td>Sucursal: <span class='marca'>*</span></td><td><input type='hidden' id='id_sucursal' name='id_sucursal' value='"+id_sucursal+"' />"+objSuc.getNombre(id_sucursal)+"</td></tr>");
                }
                out.print("<tr><td>Emitir pre-facturas: <span class='marca'>*</span></td>");
                if(objRol.getAcceso(id_rol, "opPrefacturar")){
                    out.print("<td>" + DatosDinamicos.combo("cobrar", "", cobrar1, "_('ax_cobrar').style.display=this.value=='t'?'none':'block'") + "</td></tr>");

                }else{
                    out.print("<td><input type='hidden' id='cobrar' name='cobrar' value='t' />SI</td></tr>");
                }
                out.print("<tr><td colspan='2'>");
                if(objRol.getAcceso(id_rol, "opPrefacturar")){
                    out.print("<table id='ax_cobrar' style=\"display:none\"><tr><td>Motivo por no pre-facturar: <span class='marca'>*</span></td>");
                    out.print("<td><textarea id='motivo_no_cobrar' name='motivo_no_cobrar' rows='2' cols='70'></textarea></td></tr></table>");
                }else{
                    out.print("<input type='hidden' id='motivo_no_cobrar' name='motivo_no_cobrar' value='' />");
                }
                out.print("</td></tr>");


                out.print("<tr><td>Provincia: </td>");
                out.print("<td><div id='cob0' style='display:table-cell'>" + DatosDinamicos.combo(rsProvincias, "prv", id_provincia, "_pX='getUbicacion(:cob2:,:prr:,:ci:,200);';_pY='getUbicacion(:cob2:,:prr:,:ci:,200);';getUbicacion('cob1','ci','prv',200);", "", 231) + "</div></td></tr>");
                out.print("<tr><td>Cant&oacute;n: </td>");
                out.print("<td nowrap><div id='cob1' style='float:left;display:table-cell'>" + DatosDinamicos.combo(rsCiudades, "ci", id_ciudad, "getUbicacion('cob2','prr','ci',200);", "", 200) + "</div></td></tr>");
                out.print("<tr><td>Parroquia: </td>");
                /*out.print("<td nowrap><div id='cob2' style='float:left;display:table-cell'>" + DatosDinamicos.combo(rsParroquias, "prr", "", "", "", 200) + "</div>"
                        + "<div class='jm_icoCon' id='gr2' style='visibility:hidden;background-position: -545px 0px;' onclick=\"guardarUbicacion(null,'cob2','prr','gr2','add2',200,'ci');\" title='guardar'>&nbsp;</div>"
                        + "<div class='jm_icoCon' id='add2' style='background-position:-504px -14px;' onclick=\"insertarUbicacion('cob2','prr','gr2','add2',200,'ci');\" title='Insertar'>&nbsp;</div></td></tr>");*/
                out.print("<td nowrap><div id='cob2' style='float:left;display:table-cell'>" + DatosDinamicos.combo(rsParroquias, "prr", "", "", "", 200) + "</div></td></tr>");


                out.print("<tr><td>Sector: <span class='marca'>*</span></td>");
                out.print("<td id='axIdSec'>" + DatosDinamicos.combo(rsSectores, "id_sector", "", "fac_setPlanes()", " ", 200) + "</td></tr>");
                out.print("<tr><td>Tipo de Instalaci&oacute;n: <span class='marca'>*</span></td>");
                out.print("<td id='axIdSec'>" + DatosDinamicos.combo("tipo_instalacion", "a", tipo_instalacion1, "fac_setPlanes()") + "</td></tr>");
                
                out.print("<tr><td>Costo de instalaci&oacute;n: </td>");
                out.print("<td><input id='costo_instalacion' name='costo_instalacion' type='text' size='15' readonly value='0' /></td></tr>");
                out.print("<tr><td>Plan actual vigente: <span class='marca'>*</span></td>");
                out.print("<td id='auxPlanes'></td></tr>");
                out.print("<tr><td>Direcci&oacute;n de instalaci&oacute;n: <span class='marca'>*</span></td>");
                out.print("<td><input id='direccion_instalacion' name='direccion_instalacion' type='text' class='may' size='80' maxlength='200' value='"+direccion+"' onkeypress=\"_alfanumerico(event);\" onblur=\"this.value=this.value._trim();\" /></td></tr>");
                out.print("<tr><td>Direcci&oacute;n IP: <span class='marca'>*</span></td>");
                out.print("<td><input id='ip' name='ip' type='text' size='15' maxlength='15' value='"+ip+"' onkeyup=\"fac_cmbActivo('ip');fac_getIpsReutilizar();\" onkeypress=\"_evaluar(event, '0123456789.');\" />"
                        + "&nbsp;&nbsp;&nbsp; IP radio: <input id='ip_radio' name='ip_radio' type='text' size='15' maxlength='15' value='' onkeypress=\"_evaluar(event, '0123456789.');\" /></td></tr>");

                /*out.print("<tr><td>Usuario PPOE: </td>");
                out.print("<td>"+radusername+"</td></tr>");
                out.print("<tr><td>Clave PPOE: </td>");
                out.print("<td>"+radclave+"</td></tr>");

                out.print("<tr><td>Estado: <span class='marca'>*</span></td>");
                if(id.compareTo("-1")!=0){
                    if(objRol.getAcceso(id_rol, "setEstadoServicio")){
                        out.print("<td>" + DatosDinamicos.combo("estado_servicio", estado_servicio, estado_servicio1, "") + "</td></tr>");
                    }else{
                        out.print("<td><input type='hidden' id='estado_servicio' name='estado_servicio' value='"+estado_servicio+"' />"+txt_estado_servicio+"</td></tr>");
                    }
                }

                out.print("<tr><td colspan='2'>");

                if(!bloqueado){
                    if(id.compareTo("-1")!=0 && objRol.getAcceso(id_rol, "installImprimir")){
                        out.print(" <input type='button' value='Re-Imprimir Formulario de Instalaci&oacute;n' onclick=\"window.open('pdfInstalacion?id='+_('id').value, '_blank')\" />");
                    }

                    if(id.compareTo("-1")!=0 && objRol.getAcceso(id_rol, "installEditar")){
                        out.print(" &nbsp;&nbsp;&nbsp;&nbsp; <input type='Button' value='Actualizar Informaci&oacute;n' onclick=\"fac_installActualizar(_('formVenta'));\" />");
                    }
                }
                out.print("</td></tr>");*/
                out.print("</table>");
                
                objUbicacion.cerrar();
                
        } finally {
            objRol.cerrar();
            objInstalacion.cerrar();
            objSector.cerrar();
            objCliente.cerrar();
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
