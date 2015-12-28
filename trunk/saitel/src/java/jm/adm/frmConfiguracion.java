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

package jm.adm;

import java.io.*;

import javax.servlet.*;
import javax.servlet.http.*;
import java.sql.ResultSet;
import jm.adm.clas.Configuracion;
import jm.adm.clas.Sucursal;
import jm.cnt.clas.PlanCuenta;
import jm.web.DatosDinamicos;
import jm.web.Archivo;

/**
 *
 * @author Jorge
 */
public class frmConfiguracion extends HttpServlet {
    private String _ip = null;
    private int _puerto = 5432;
    private String _db = null;
    private String _dir = null;
    
    public void init(ServletConfig config) throws ServletException
    {
        this._ip = config.getServletContext().getInitParameter("_IP");
        this._puerto = Integer.parseInt(config.getServletContext().getInitParameter("_PUERTO"));
        this._db = config.getServletContext().getInitParameter("_DB");
        this._dir = config.getServletContext().getInitParameter("_DIR"); 
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
        
        response.setContentType("text/html;charset=UTF-8");
        response.setHeader("Pragma", "no-cache"); 
        response.setHeader("Expires", "Mon, 01 Jan 2001 00:00:01 GMT"); 
        response.setHeader("Cache-Control", "no-store"); 
        response.setHeader("Cache-Control", "must-revalidate"); 
        response.setHeader("Cache-Control", "no-cache");
        
        PrintWriter out = response.getWriter();
        
        int altoBody = Integer.parseInt(request.getParameter("altB")) - 250;
        int modulo = Integer.parseInt(request.getParameter("m"));
        
        
        PlanCuenta objPlanCuenta = new PlanCuenta(this._ip, this._puerto, this._db, usuario, clave);
        ResultSet cuentas = objPlanCuenta.getTablasAsignacion();
        objPlanCuenta.cerrar();
        
        Configuracion conf = new Configuracion(this._ip, this._puerto, this._db, usuario, clave);
        String html="";
        
        switch(modulo){
            case 1:
                Sucursal objSuc = new Sucursal(this._ip, this._puerto, this._db, usuario, clave);
                ResultSet rsCucursales = objSuc.getSucursales();
                objSuc.cerrar();
                String [][] representante_legal1 = {{"c","C&eacute;dula"},{"p","Pasaporte"}};
                String [][] oblga_contabilidad1 = {{"SI","SI"},{"NO","NO"}};
                String ruc = conf.getValor("ruc");
                String razon_social = conf.getValor("razon_social");
                String nombre_comercial = conf.getValor("nombre_comercial");
                String num_resolucion = conf.getValor("num_resolucion");
                String oblga_contabilidad = conf.getValor("oblga_contabilidad");
                String dir_matriz = conf.getValor("dir_matriz");
                String telf_matriz = conf.getValor("telf_matriz");
                String fax = conf.getValor("fax");
                String email = conf.getValor("email");
                String rep_nombre = conf.getValor("rep_nombre");
                String rep_cargo = conf.getValor("rep_cargo");
                String rep_legal = conf.getValor("rep_legal");
                String num_ident = conf.getValor("num_ident");
                String ruc_contador = conf.getValor("ruc_contador");
                String id_sucursal_matriz = conf.getValor("id_sucursal_matriz");
                String tipo_emision = conf.getValor("tipo_emision");
                html = "<div class='H3'>FORMULARIOS Y/O ANEXOS DIMM</div>" +
                    "<div style='overflow:auto;height:"+altoBody+"px'>" +
                    "<table width='550'>" +
                    "<tr><td>RUC: <span class='marca'>*</span></td>" +
                    "<td><input type='text' id='ruc' name='ruc' size='40' maxlength='13' value='"+ruc+"' class='may' onkeypress=\"_DNI(event);\" /></td></tr>" +
                    "<tr><td>Raz&oacute;n social: <span class='marca'>*</span></td>" +
                    "<td><input type='text' id='razon_social' name='razon_social' class='may' size='60' maxlength='160' value='"+razon_social+"' onblur=\"this.value=this.value._trim();\" /></td></tr>" +
                    "<tr><td>Nombre comercial: <span class='marca'>*</span></td>" +
                    "<td><input type='text' id='nombre_comercial' name='nombre_comercial' class='may' size='60' maxlength='60' value='"+nombre_comercial+"' onblur=\"this.value=this.value._trim();\" /></td></tr>" +
                    "<tr><td>Nro. de resoluci&oacute;n de contribuyente especial: <span class='marca'>*</span></td>" +
                    "<td><input type='text' id='num_resolucion' name='num_resolucion' class='may' size='60' maxlength='5' value='"+num_resolucion+"' onkeypress=\"_numero(event);\" onblur=\"this.value=this.value._trim();\" /></td></tr>" +
                    "<tr><td>Obligado a llevar contabilidad: <span class='marca'>*</span></td>" +
                    "<td>" + DatosDinamicos.combo("oblga_contabilidad", oblga_contabilidad, oblga_contabilidad1, "") + "</td></tr>" +
                        
                    "<tr><td nowrap>Direcci&oacute;n matriz: <span class='marca'>*</span></td>" +
                    "<td><input type='text' id='dir_matriz' name='dir_matriz' class='may' size='60' maxlength='150' value='"+dir_matriz+"' onkeypress='_alfanumerico(event);_mayusculas(event,this);' onblur=\"this.value=this.value._trim();\" /></td></tr>" +
                    "<tr><td>Tel&eacute;fono: <span class='marca'>*</span></td>" +
                    "<td><input type='text' id='telf_matriz' name='telf_matriz' size='40' maxlength='9' value='"+telf_matriz+"' onkeypress=\"_numero(event);\" /></td></tr>" +
                    "<tr><td>Fax: </td>" +
                    "<td><input type='text' id='fax' name='fax' size='40' maxlength='11' value='"+fax+"' onkeypress=\"_numero(event);\" /></td></tr>" +
                    "<tr><td>E-mail: <span class='marca'>*</span></td>" +
                    "<td><input type='text' id='email' name='email' size='40' maxlength='80' value='"+email+"' /></td></tr>" +
                    "<tr><td>Nombre del representante legal: <span class='marca'>*</span></td>" +
                    "<td><input type='text' id='rep_nombre' name='rep_nombre' size='40' maxlength='80' value='"+rep_nombre+"' /></td></tr>" +
                    "<tr><td>Cargo del representante legal: <span class='marca'>*</span></td>" +
                    "<td><input type='text' id='rep_cargo' name='rep_cargo' size='40' maxlength='80' value='"+rep_cargo+"' /></td></tr>" +
                    "<tr><td>Tipo de identificaci&oacute;n del contribuyente o representante legal: <span class='marca'>*</span></td>" +
                    "<td>" + DatosDinamicos.combo("rep_legal", rep_legal, representante_legal1, "") + "</td></tr>" +
                    "<tr><td>N&uacute;mero de identificaci&oacute;n del contribuyente o representante legal: <span class='marca'>*</span></td>" +
                    "<td><input type='text' id='num_ident' name='num_ident' size='40' maxlength='13' class='may' value='"+num_ident+"' onkeypress=\"_numero(event);\" /></td></tr>" +
                    "<tr><td nowrap>RUC del contador: <span class='marca'>*</span></td>" +
                    "<td><input type='text' id='ruc_contador' name='ruc_contador' size='40' maxlength='13' value='"+ruc_contador+"' onkeypress=\"_numero(event);\" /></td></tr>" +
                    "<tr><td>Sucursal matriz: </td>" +
                    "<td>" + DatosDinamicos.combo(rsCucursales, "id_sucursal_matriz", id_sucursal_matriz, "", " SIN ASIGNACION ", 200) + "</td></tr>" +
                    "<tr><td colspan='2'><hr /></td></tr>"+
                        
                    "<tr><td>Tipo de emisi&oacute;n de documentos electr&oacute;nicos: <span class='marca'>*</span></td>"+
                    "<td><label><input type='radio' id='tipo_emision0' name='tipo_emision' value='1' "+((tipo_emision.compareTo("1")==0) ? "checked" : "")+" /> Normal </label><br/>" +
                    "<label><input type='radio' id='tipo_emision1' name='tipo_emision' value='2' "+((tipo_emision.compareTo("2")==0) ? "checked" : "")+" /> Por indisponibilidad del sistema (utilizaci&oacute;n de calves de contingencia) </label></td></tr>" +
            
                    "<tr><td colspan='2'><input type='submit' value='Guardar' /></td></tr></table>"+
                    "</div>";
                try{
                    rsCucursales.close();
                }catch(Exception e){
                    e.printStackTrace();
                }
            break;

            case 2:
                //String p_iva1 = conf.getValor("p_iva1");
                //String p_iva2 = conf.getValor("p_iva2");
                //String capital = conf.getValor("capital");
                //String caja = conf.getValor("caja");
                //String bancos = conf.getValor("bancos");
                //String uti_per_ejercicio = conf.getValor("uti_per_ejercicio");
                //String resumen_rent_gast = conf.getValor("resumen_rent_gast");
                String dia_declaracion = conf.getValor("dia_declaracion");
                String sec_com_dia = conf.getValor("sec_com_dia");
                String sec_com_ing = conf.getValor("sec_com_ing");
                String sec_com_egr = conf.getValor("sec_com_egr");
                String apr2_nombre = conf.getValor("apr2_nombre");
                String apr2_cargo = conf.getValor("apr2_cargo");

                html = "<div class='H3'>MODULO DE CONTABILIDAD</div>" +
                    "<div style='overflow:auto;height:"+altoBody+"px'>" +
                    "<table width='500'>" +
                    /*"<tr><td>Porcentaje del IVA vigente 1: </td>" +
                    "<td><input type='text' id='p_iva1' name='p_iva1' size='5' maxlength='8' value='"+p_iva1+"' onkeypress=\"_evaluar(event, '0123456789.');\" /> %</td></tr>" +
                    "<tr><td>Porcentaje del IVA vigente 2: </td>" +
                    "<td><input type='text' id='p_iva2' name='p_iva2' size='5' maxlength='8' value='"+p_iva2+"' onkeypress=\"_evaluar(event, '0123456789.');\" /> %</td></tr>" +
                    "<tr><td>Cuenta de caja chica: </td>" +
                    "<td>" + DatosDinamicos.combo(cuentas, "caja", caja, "", " SIN ASIGNACION ", 335) + "</td></tr>" +
                    "<tr><td>Cuenta de bancos: </td>" +
                    "<td>" + DatosDinamicos.combo(cuentas, "bancos", bancos, "", " SIN ASIGNACION ", 335) + "</td></tr>" +
                    "<tr><td>Cuenta de capital: </td>" +
                    "<td>" + DatosDinamicos.combo(cuentas, "capital", capital, " SIN ASIGNACION ", 335) + "</td></tr>" +
                    "<tr><td>Utilidad o p&eacute;rdida del ejercicio: </td>" +
                    "<td>" + DatosDinamicos.combo(cuentas, "uti_per_ejercicio", uti_per_ejercicio, "", " SIN ASIGNACION ", 335) + "</td></tr>" +
                    "<tr><td nowrap>Resumen de rentas y gastos: </td>" +
                    "<td>" + DatosDinamicos.combo(cuentas, "resumen_rent_gast", resumen_rent_gast, "", " SIN ASIGNACION ", 335) + "</td></tr>" +*/
                    "<tr><td>Ultimo d&iacute;a para declaraci&oacute;n de formularios al SRI: </td>" +
                    "<td><input type='text' id='dia_declaracion' name='dia_declaracion' size='5' maxlength='2' value='"+dia_declaracion+"' onkeypress=\"_numero(event);\" /></td></tr>" +    
                    "<tr><td>N&uacute;mero de comprobante de diario inicial: </td>" +
                    "<td><input type='text' id='sec_com_dia' name='sec_com_dia' size='5' maxlength='15' value='"+sec_com_dia+"' onkeypress=\"_numero(event);\" /></td></tr>" +
                    "<tr><td>N&uacute;mero de comprobante de ingreso inicial: </td>" +
                    "<td><input type='text' id='sec_com_ing' name='sec_com_ing' size='5' maxlength='15' value='"+sec_com_ing+"' onkeypress=\"_numero(event);\" /></td></tr>" +
                    "<tr><td>N&uacute;mero de comprobante de egreso inicial: </td>" +
                    "<td><input type='text' id='sec_com_egr' name='sec_com_egr' size='5' maxlength='15' value='"+sec_com_egr+"' onkeypress=\"_numero(event);\" /></td></tr>"+
                    "<tr><td>Nombre de qui&eacute;n aprueba comprobantes: <span class='marca'>*</span></td>" +
                    "<td><input type='text' id='apr2_nombre' name='apr2_nombre' size='40' maxlength='80' value='"+apr2_nombre+"' /></td></tr>" +
                    "<tr><td>Cargo de qui&eacute;n aprueba comprobantes: <span class='marca'>*</span></td>" +
                    "<td><input type='text' id='apr2_cargo' name='apr2_cargo' size='40' maxlength='80' value='"+apr2_cargo+"' /></td></tr>" +
                
                    "<tr><td colspan='2'><input type='submit' value='Guardar' /></td></tr></table>" +
                    "</div>";
            break;

            case 3:
                String inventario = conf.getValor("inventario");
                String costo_ventas = conf.getValor("costo_ventas");
                /*String prov_servicios = conf.getValor("prov_servicios");*/
                String propina = conf.getValor("propina");
                String iva_comp = conf.getValor("iva_comp");
                String iva_comp_serv = conf.getValor("iva_comp_serv");
                String iva_comp_bien = conf.getValor("iva_comp_bien");
                String iva_comp_gasto = conf.getValor("iva_comp_gasto");
                String devo_comp = conf.getValor("devo_comp");
                String desc_comp = conf.getValor("desc_comp");
                String cxp = conf.getValor("cxp");
                String chequeEmitidoNoCobrado = conf.getValor("chequeEmitidoNoCobrado");
                html = "<div class='H3'>MODULO DE INVENTARIO Y GASTOS</div>" +
                    "<div style='overflow:auto;height:"+altoBody+"px'>" +
                    "<table width='550'>" +
                    /*
                    "<tr><td nowrap>Proveedores de servicios:</td>" +
                    "<td>" + DatosDinamicos.combo(cuentas, "prov_servicios", prov_servicios, "", " SIN ASIGNACION ", 335) + "</td></tr>" +*/
                    "<tr><td nowrap>Inventarios:</td>" +
                    "<td>" + DatosDinamicos.combo(cuentas, "inventario", inventario, "", " SIN ASIGNACION ", 335) + "</td></tr>" +
                    "<tr><td nowrap>Costo de ventas:</td>" +
                    "<td>" + DatosDinamicos.combo(cuentas, "costo_ventas", costo_ventas, "", " SIN ASIGNACION ", 335) + "</td></tr>" +
                    "<tr><td nowrap>Propinas:</td>" +
                    "<td>" + DatosDinamicos.combo(cuentas, "propina", propina, "", " SIN ASIGNACION ", 335) + "</td></tr>" +
                    /*"<tr><td nowrap>IVA en compras:</td>" +
                    "<td>" + DatosDinamicos.combo(cuentas, "iva_comp", iva_comp, "", " SIN ASIGNACION ", 335) + "</td></tr>" +
                    "<tr><td nowrap>IVA en compra servicios:</td>" +
                    "<td>" + DatosDinamicos.combo(cuentas, "iva_comp_serv", iva_comp_serv, "", " SIN ASIGNACION ", 335) + "</td></tr>" +
                    "<tr><td>IVA en compra Bienes: </td>" +
                    "<td>" + DatosDinamicos.combo(cuentas, "iva_comp_bien", iva_comp_bien, "", " SIN ASIGNACION ", 335) + "</td></tr>" +
                    "<tr><td>IVA en compra Suministros / gastos: </td>" +
                    "<td>" + DatosDinamicos.combo(cuentas, "iva_comp_gasto", iva_comp_gasto, "", " SIN ASIGNACION ", 335) + "</td></tr>" +*/
                    "<tr><td>Devoluciones en compras: </td>" +
                    "<td>" + DatosDinamicos.combo(cuentas, "devo_comp", devo_comp, "", " SIN ASIGNACION ", 335) + "</td></tr>" +
                    "<tr><td>Descuentos en compras: </td>" +
                    "<td>" + DatosDinamicos.combo(cuentas, "desc_comp", desc_comp, "", " SIN ASIGNACION ", 335) + "</td></tr>" +
                    "<tr><td>Cuentas por pagar:</td>" +
                    "<td>" + DatosDinamicos.combo(cuentas, "cxp", cxp, "", " SIN ASIGNACION ", 335) + "</td></tr>" +
                    "<tr><td>Cheques emitidos y a&uacute;n no cobrados:</td>" +
                    "<td>" + DatosDinamicos.combo(cuentas, "chequeEmitidoNoCobrado", chequeEmitidoNoCobrado, "", " SIN ASIGNACION ", 335) + "</td></tr>" +
                    "<tr><td colspan='2'><input type='submit' value='Guardar' /></td></tr></table>" +
                    "</div>";
            break;

            case 31:
                String importa_transito = conf.getValor("importa_transito");
                String imp_iva1 = conf.getValor("imp_iva1");
                /*String fob = conf.getValor("fob");
                String flete = conf.getValor("flete");
                String seguro = conf.getValor("seguro");
                String ajuste = conf.getValor("ajuste");
                String cif = conf.getValor("cif");
                String valor_aduana = conf.getValor("valor_aduana");
                String valor_em = conf.getValor("valor_em");
                String der_especifico = conf.getValor("der_especifico");
                String imp_ice = conf.getValor("imp_ice");
                
                String antidumping = conf.getValor("antidumping");
                String der_consular = conf.getValor("der_consular");
                String sobretiempo_petr = conf.getValor("sobretiempo_petr");
                String ad_barril_crudo = conf.getValor("ad_barril_crudo");
                String tasa_modernizacion = conf.getValor("tasa_modernizacion");
                String tasa_control = conf.getValor("tasa_control");
                String tasa_almacenaje = conf.getValor("tasa_almacenaje");
                String multa = conf.getValor("multa");
                String interes = conf.getValor("interes");
                String imp_salida_divisa = conf.getValor("imp_salida_divisa");
                String fodinfa = conf.getValor("fodinfa");
                String corpei = conf.getValor("corpei");
                String otros = conf.getValor("otros");
                String salvaguarda = conf.getValor("salvaguarda");*/
                String cxppe = conf.getValor("cxppe");
                html = "<div class='H3'>MODULO DE IMPORTACIONES</div>" +
                    "<div style='overflow:auto;height:"+altoBody+"px'>" +
                    "<table width='550'>" +
                    "<tr><td nowrap>Importaciones en tr&aacute;nsito:</td>" +
                    "<td>" + DatosDinamicos.combo(cuentas, "importa_transito", importa_transito, "", " SIN ASIGNACION ", 335) + "</td></tr>" +
                    "<tr><td nowrap>IMP. VALOR AGREGADO (IVA):</td>" +
                    "<td>" + DatosDinamicos.combo(cuentas, "imp_iva1", imp_iva1, "", " SIN ASIGNACION ", 335) + "</td></tr>" +
                    /*"<tr><td nowrap>Valor FOB:</td>" +
                    "<td>" + DatosDinamicos.combo(cuentas, "fob", fob, "", " SIN ASIGNACION ", 335) + "</td></tr>" +
                    "<tr><td nowrap>Valor Flete:</td>" +
                    "<td>" + DatosDinamicos.combo(cuentas, "flete", flete, "", " SIN ASIGNACION ", 335) + "</td></tr>" +
                    "<tr><td nowrap>Valor seguro:</td>" +
                    "<td>" + DatosDinamicos.combo(cuentas, "seguro", seguro, "", " SIN ASIGNACION ", 335) + "</td></tr>" +
                    "<tr><td nowrap>Valor ajuste:</td>" +
                    "<td>" + DatosDinamicos.combo(cuentas, "ajuste", ajuste, "", " SIN ASIGNACION ", 335) + "</td></tr>" +
                    "<tr><td nowrap>Valor CIF:</td>" +
                    "<td>" + DatosDinamicos.combo(cuentas, "cif", cif, "", " SIN ASIGNACION ", 335) + "</td></tr>" +
                    "<tr><td nowrap>Valor aduana:</td>" +
                    "<td>" + DatosDinamicos.combo(cuentas, "valor_aduana", valor_aduana, "", " SIN ASIGNACION ", 335) + "</td></tr>" +
                    "<tr><td nowrap>AD / VALOREM:</td>" +
                    "<td>" + DatosDinamicos.combo(cuentas, "valor_em", valor_em, "", " SIN ASIGNACION ", 335) + "</td></tr>" +
                    "<tr><td nowrap>DER. ESPECIFICO:</td>" +
                    "<td>" + DatosDinamicos.combo(cuentas, "der_especifico", der_especifico, "", " SIN ASIGNACION ", 335) + "</td></tr>" +
                    "<tr><td nowrap>IMP. CONSUMO ESPECIAL (ICE):</td>" +
                    "<td>" + DatosDinamicos.combo(cuentas, "imp_ice", imp_ice, "", " SIN ASIGNACION ", 335) + "</td></tr>" +
                    
                    "<tr><td nowrap>DERECHO ANTIDUMPING:</td>" +
                    "<td>" + DatosDinamicos.combo(cuentas, "antidumping", antidumping, "", " SIN ASIGNACION ", 335) + "</td></tr>" +
                    "<tr><td>DERECHOS CONSULARES: </td>" +
                    "<td>" + DatosDinamicos.combo(cuentas, "der_consular", der_consular, "", " SIN ASIGNACION ", 335) + "</td></tr>" +
                    "<tr><td>SOBRETIEMPO PETROLEO: </td>" +
                    "<td>" + DatosDinamicos.combo(cuentas, "sobretiempo_petr", sobretiempo_petr, "", " SIN ASIGNACION ", 335) + "</td></tr>" +
                    "<tr><td>ADICIONAL POR BARRIL DE CRUDO: </td>" +
                    "<td>" + DatosDinamicos.combo(cuentas, "ad_barril_crudo", ad_barril_crudo, "", " SIN ASIGNACION ", 335) + "</td></tr>" +
                    "<tr><td>TASA MODERNIZACION:</td>" +
                    "<td>" + DatosDinamicos.combo(cuentas, "tasa_modernizacion", tasa_modernizacion, "", " SIN ASIGNACION ", 335) + "</td></tr>" +
                    "<tr><td>TASA DE CONTROL: </td>" +
                    "<td>" + DatosDinamicos.combo(cuentas, "tasa_control", tasa_control, "", " SIN ASIGNACION ", 335) + "</td></tr>" +
                    "<tr><td>TASA ALMACENAJE:</td>" +
                    "<td>" + DatosDinamicos.combo(cuentas, "tasa_almacenaje", tasa_almacenaje, "", " SIN ASIGNACION ", 335) + "</td></tr>" +
                    "<tr><td>MULTAS (ART. 89 Y 91): </td>" +
                    "<td>" + DatosDinamicos.combo(cuentas, "multa", multa, "", " SIN ASIGNACION ", 335) + "</td></tr>" +
                    "<tr><td>INTERESES: </td>" +
                    "<td>" + DatosDinamicos.combo(cuentas, "interes", interes, "", " SIN ASIGNACION ", 335) + "</td></tr>" +
                    "<tr><td>IMP. A LA SALIDA DE DIVISAS: </td>" +
                    "<td>" + DatosDinamicos.combo(cuentas, "imp_salida_divisa", imp_salida_divisa, "", " SIN ASIGNACION ", 335) + "</td></tr>" +
                    "<tr><td>FODINFA: </td>" +
                    "<td>" + DatosDinamicos.combo(cuentas, "fodinfa", fodinfa, "", " SIN ASIGNACION ", 335) + "</td></tr>" +
                    "<tr><td>CORPEI: </td>" +
                    "<td>" + DatosDinamicos.combo(cuentas, "corpei", corpei, "", " SIN ASIGNACION ", 335) + "</td></tr>" +
                    "<tr><td>OTROS: </td>" +
                    "<td>" + DatosDinamicos.combo(cuentas, "otros", otros, "", " SIN ASIGNACION ", 335) + "</td></tr>" +
                    "<tr><td>SALVAGUARDA: </td>" +
                    "<td>" + DatosDinamicos.combo(cuentas, "salvaguarda", salvaguarda, "", " SIN ASIGNACION ", 335) + "</td></tr>" +*/
                    "<tr><td>Cuentas por pagar proveedores extranjeros: </td>" +
                    "<td>" + DatosDinamicos.combo(cuentas, "cxppe", cxppe, "", " SIN ASIGNACION ", 335) + "</td></tr>" +
                    "<tr><td colspan='2'><input type='submit' value='Guardar' /></td></tr></table>" +
                    "</div>";
            break;

            case 4:
                String [][] dia_prefacturas1 = {{"1","1"},{"2","2"},{"3","3"},{"4","4"},
                {"5","5"},{"6","6"},{"7","7"},{"8","8"},{"9","9"},
                {"10","10"},{"11","11"},{"12","12"},{"13","13"},{"14","14"},
                {"15","15"},{"16","16"},{"17","17"},{"18","18"},{"19","19"},
                {"20","20"},{"21","21"},{"22","22"},{"23","23"},{"24","24"},{"25","25"},
                {"26","26"},{"27","27"},{"28","28"},{"32","&Uacute;ltimo d&iacute;a del mes"}};

                //String ventas = conf.getValor("ventas");
                String dia_prefacturas = conf.getValor("dia_prefacturas");
                String base_gen_prefactura = conf.getValor("base_gen_prefactura");
                String dia_cortes = conf.getValor("dia_cortes");
                String dia_cortes_creditos = conf.getValor("dia_cortes_creditos");
                String max_diferencia = conf.getValor("max_diferencia");
                String instalacion = conf.getValor("instalacion");
                String plan = conf.getValor("plan");
                //String croquis = conf.getValor("croquis");
                String cxc = conf.getValor("cxc");
                String iva_cobrado = conf.getValor("iva_cobrado");
                String devo_venta = conf.getValor("devo_venta");
                String desc_venta = conf.getValor("desc_venta");
                String chequeRecibidoNoCobrado = conf.getValor("chequeRecibidoNoCobrado");
                String gastos_bancos = conf.getValor("gastos_bancos");
                String uti_per_ejercicio = conf.getValor("uti_per_ejercicio");
                String uti_venta_activos = conf.getValor("uti_venta_activos");
                String per_venta_activos = conf.getValor("per_venta_activos");
                html = "<div class='H3'>MODULO DE FACTURACION</div>" +
                    "<div style='overflow:auto;height:"+altoBody+"px'>" +
                    "<table width='550'>" +
                    "<tr><td>D&iacute;a de generaci&oacute;n de pre-facturas: <span class='marca'>*</span></td>" +
                    "<td>" + DatosDinamicos.combo("dia_prefacturas", dia_prefacturas, dia_prefacturas1, "") + "</td></tr>" +
                    
                    "<tr><td>Valor m&iacute;nimo para generaci&oacute;n de pre-facturas nuevas(por d&iacute;as de navegaci&oacute;n): <span class='marca'>*</span></td>" +
                    "<td><input type='text' id='base_gen_prefactura' name='base_gen_prefactura' size='10' maxlength='8' value='"+base_gen_prefactura+"' onkeypress=\"_evaluar(event, '0123456789.');\" /></td></tr>" +

                    "<tr><td>D&iacute;a para ejecutar cortes por mora: <span class='marca'>*</span></td>" +
                    "<td>" + DatosDinamicos.combo("dia_cortes", dia_cortes, dia_prefacturas1, "") + "</td></tr>" +
                    
                    "<tr><td>D&iacute;a para ejecutar cortes por mora en cr&eacute;ditos: </td>" +
                    "<td>" + DatosDinamicos.combo("dia_cortes_creditos", dia_cortes_creditos, dia_prefacturas1, "") + "</td></tr>" +
                    "<tr><td>Monto m&aacute;ximo de faltantes en diferencias de caja: </td>" +
                    "<td><input type='text' id='max_diferencia' name='max_diferencia' size='10' maxlength='8' value='"+max_diferencia+"' onkeypress=\"_evaluar(event, '0123456789.');\" /></td></tr>" +
                    /*"<tr><td>Ventas: </td>" +
                    "<td>" + DatosDinamicos.combo(cuentas, "ventas", ventas, "", " SIN ASIGNACION ", 335) + "</td></tr>" +*/
                    "<tr><td>Instalaciones: </td>" +
                    "<td>" + DatosDinamicos.combo(cuentas, "instalacion", instalacion, "", " SIN ASIGNACION ", 335) + "</td></tr>" +
                    "<tr><td>Planes de servicio (ISP): </td>" +
                    "<td>" + DatosDinamicos.combo(cuentas, "plan", plan, "", " SIN ASIGNACION ", 335) + "</td></tr>" +
                    /*"<tr><td>Ruta a grabar los croquis: </td>" +
                    "<td><input type='text' id='croquis' name='croquis' size='60' maxlength='200' value='"+croquis+"' /></td></tr>" +*/
                    "<tr><td>Cuentas por cobrar: </td>" +
                    "<td>" + DatosDinamicos.combo(cuentas, "cxc", cxc, "", " SIN ASIGNACION ", 335) + "</td></tr>" +
                    /*"<tr><td>IVA en ventas: </td>" +
                    "<td>" + DatosDinamicos.combo(cuentas, "iva_cobrado", iva_cobrado, "", " SIN ASIGNACION ", 335) + "</td></tr>" +*/
                    "<tr><td>Descuentos en ventas: </td>" +
                    "<td>" + DatosDinamicos.combo(cuentas, "desc_venta", desc_venta, "", " SIN ASIGNACION ", 335) + "</td></tr>" +
                    "<tr><td>Devoluciones en ventas: </td>" +
                    "<td>" + DatosDinamicos.combo(cuentas, "devo_venta", devo_venta, "", " SIN ASIGNACION ", 335) + "</td></tr>" +
                    "<tr><td>Cheques recibidos y a&uacute;n no cobrados: </td>" +
                    "<td>" + DatosDinamicos.combo(cuentas, "chequeRecibidoNoCobrado", chequeRecibidoNoCobrado, "", " SIN ASIGNACION ", 335) + "</td></tr>" +
                    "<tr><td>Gastos bancarios: </td>" +
                    "<td>" + DatosDinamicos.combo(cuentas, "gastos_bancos", gastos_bancos, "", " SIN ASIGNACION ", 335) + "</td></tr>" +
                    "<tr><td>Utilidad en ventas: </td>" +
                    "<td>" + DatosDinamicos.combo(cuentas, "uti_per_ejercicio", uti_per_ejercicio, "", " SIN ASIGNACION ", 335) + "</td></tr>" +

                    "<tr><td>Utilidad en ventas de activos: </td>" +
                    "<td>" + DatosDinamicos.combo(cuentas, "uti_venta_activos", uti_venta_activos, "", " SIN ASIGNACION ", 335) + "</td></tr>" +
                    "<tr><td>P&eacute;rdida en ventas de activos: </td>" +
                    "<td>" + DatosDinamicos.combo(cuentas, "per_venta_activos", per_venta_activos, "", " SIN ASIGNACION ", 335) + "</td></tr>" +
                    "<tr><td colspan='2'><input type='submit' value='Guardar' /></td></tr></table>" +
                    "</div>";
            break;

            case 5:
                String nom_p_iess = conf.getValor("nom_p_iess");
                String nom_smvg = conf.getValor("nom_smvg");
                String nom_gasto_sueldos = conf.getValor("nom_gasto_sueldos");
                String nom_anticipo_sueldos = conf.getValor("nom_anticipo_sueldos");
                String nom_bene_sociales = conf.getValor("nom_bene_sociales");
                String nom_ap_pat_iess = conf.getValor("nom_ap_pat_iess");
                String nom_HE_50 = conf.getValor("nom_HE_50");
                String nom_HE_100 = conf.getValor("nom_HE_100");
                String nom_rem_x_pagar = conf.getValor("nom_rem_x_pagar");
                String nom_ap_per_x_pagar = conf.getValor("nom_ap_per_x_pagar");
                String nom_ap_pat_x_pagar = conf.getValor("nom_ap_pat_x_pagar");
                String nom_iece = conf.getValor("nom_iece");
                String nom_secap = conf.getValor("nom_secap");
                String nom_13_sueldo = conf.getValor("nom_13_sueldo");
                String nom_14_sueldo = conf.getValor("nom_14_sueldo");
                String nom_vacaciones = conf.getValor("nom_vacaciones");
                //String nom_banco = conf.getValor("nom_banco");
                html = "<div class='H3'>MODULO DE NOMINA</div>" +
                    "<div style='overflow:auto;height:"+altoBody+"px'>" +
                    "<table width='500'>" +
                    "<tr><td>Porcentaje de aporte al IESS: </td>" +
                    "<td><input type='text' id='nom_p_iess' name='nom_p_iess' size='10' maxlength='8' value='"+nom_p_iess+"' onkeypress=\"_evaluar(event, '0123456789.');\" /></td></tr>" +
                    "<tr><td>Remuneraci&oacute;n b&aacute;sica m&iacute;nima unificada general: </td>" +
                    "<td><input type='text' id='nom_smvg' name='nom_smvg' size='10' maxlength='8' value='"+nom_smvg+"' onkeypress=\"_evaluar(event, '0123456789.');\" /></td></tr>" +
                    "<tr><td>Gasto sueldos: </td>" +
                    "<td>" + DatosDinamicos.combo(cuentas, "nom_gasto_sueldos", nom_gasto_sueldos, "", " SIN ASIGNACION ", 335) + "</td></tr>" +
                    "<tr><td>Anticipo de sueldos: </td>" +
                    "<td>" + DatosDinamicos.combo(cuentas, "nom_anticipo_sueldos", nom_anticipo_sueldos, "", " SIN ASIGNACION ", 335) + "</td></tr>" +
                    "<tr><td>Gasto beneficios sociales: </td>" +
                    "<td>" + DatosDinamicos.combo(cuentas, "nom_bene_sociales", nom_bene_sociales, "", " SIN ASIGNACION ", 335) + "</td></tr>" +
                    "<tr><td nowrap>Gasto aportes patronales al IESS: </td>" +
                    "<td>" + DatosDinamicos.combo(cuentas, "nom_ap_pat_iess", nom_ap_pat_iess, "", " SIN ASIGNACION ", 335) + "</td></tr>" +
                    "<tr><td>Horas extras del 50%: </td>" +
                    "<td>" + DatosDinamicos.combo(cuentas, "nom_HE_50", nom_HE_50, "", " SIN ASIGNACION ", 335) + "</td></tr>" +
                    "<tr><td>Horas extras del 100%: </td>" +
                    "<td>" + DatosDinamicos.combo(cuentas, "nom_HE_100", nom_HE_100, "", " SIN ASIGNACION ", 335) + "</td></tr>" +
                    "<tr><td>Remuneraciones por pagar: </td>" +
                    "<td>" + DatosDinamicos.combo(cuentas, "nom_rem_x_pagar", nom_rem_x_pagar, "", " SIN ASIGNACION ", 335) + "</td></tr>" +
                    "<tr><td>Aporte personal por pagar: </td>" +
                    "<td>" + DatosDinamicos.combo(cuentas, "nom_ap_per_x_pagar", nom_ap_per_x_pagar, "", " SIN ASIGNACION ", 335) + "</td></tr>" +
                    "<tr><td>Aporte patronal por pagar: </td>" +
                    "<td>" + DatosDinamicos.combo(cuentas, "nom_ap_pat_x_pagar", nom_ap_pat_x_pagar, "", " SIN ASIGNACION ", 335) + "</td></tr>" +
                    "<tr><td>0,5% I.E.C.E.: </td>" +
                    "<td>" + DatosDinamicos.combo(cuentas, "nom_iece", nom_iece, "", " SIN ASIGNACION ", 335) + "</td></tr>" +
                    "<tr><td>0,5% S.E.C.A.P.: </td>" +
                    "<td>" + DatosDinamicos.combo(cuentas, "nom_secap", nom_secap, "", " SIN ASIGNACION ", 335) + "</td></tr>" +
                    "<tr><td>D&eacute;cimo tercer sueldo: </td>" +
                    "<td>" + DatosDinamicos.combo(cuentas, "nom_13_sueldo", nom_13_sueldo, "", " SIN ASIGNACION ", 335) + "</td></tr>" +
                    "<tr><td>D&eacute;cimo cuarto sueldo: </td>" +
                    "<td>" + DatosDinamicos.combo(cuentas, "nom_14_sueldo", nom_14_sueldo, "", " SIN ASIGNACION ", 335) + "</td></tr>" +
                    "<tr><td>Vacaciones: </td>" +
                    "<td>" + DatosDinamicos.combo(cuentas, "nom_vacaciones", nom_vacaciones, "", " SIN ASIGNACION ", 335) + "</td></tr>" +
                    /*"<tr><td>Banco: </td>" +
                    "<td>" + DatosDinamicos.combo(cuentas, "nom_banco", nom_banco, "", " SIN ASIGNACION ", 335) + "</td></tr>" +*/
                    "<tr><td colspan='2'><input type='submit' value='Guardar' /></td></tr></table>" +
                    "</div>";
            break;

            case 7:
                Archivo archivo = new Archivo(this._ip, this._puerto, this._db, usuario, clave);
                String path = String.valueOf(request.getRequestURL());
                path = path.substring(0, path.lastIndexOf("/"));
                String logo = path + "/img/" + archivo.getArchivo(this._dir, 1);
                archivo.cerrar();
                
                String rep_pie = conf.getValor("rep_pie");
                html = "<div class='H3'>REPORTES</div>" +
                    "<table width='500'>" +
                    "<tr><td>Logotipo:</td><td><div class='grupo' style='width:122px;height:122px;cursor:pointer;' onclick=\"adm_setImagen('tbl_archivo', 'nombre', 'archivo', 1);\">"
                    + "<img id='tagImg' src='"+logo+"' width='120px' height='120px' border='0' /></div></td></tr>" +
                    "<tr><td>Texto para el pie de p&aacute;gina:</td>" +
                    "<td><input type='text' id='rep_pie' name='rep_pie' value='"+rep_pie+"' size='50' maxlength='200' /></td></tr>" +
                    "<tr><td colspan='2'><input type='submit' value='Guardar' /></td></tr></table>";
            break;
            
            case 8:
                String path_pg_dump = conf.getValor("path_pg_dump");
                String path_resguardo = conf.getValor("path_resguardo");
                html = "<div class='H3'>RESPALDOS DE LA BASE DE DATOS</div>" +
                    "<table width='500'>" +
                    "<tr><td>Ruta del archivo ejecutable pg_dump.exe: <span class='marca'>*</span></td>" +
                    "<td><input type='text' id='path_pg_dump' name='path_pg_dump' size='50' maxlength='200' value='"+path_pg_dump+"' /></td></tr>" +
                    "<tr><td>Ruta del directorio donde se almacenar&aacute;n los respaldos: <span class='marca'>*</span></td>" +
                    "<td><input type='text' id='path_resguardo' name='path_resguardo' size='50' maxlength='200' value='"+path_resguardo+"' /></td></tr>" +
                    "<tr><td colspan='2'><input type='submit' value='Guardar' /></td></tr></table>";
            break;

            case 9:
                String mail_svr = conf.getValor("mail_svr");
                String mail_origen = conf.getValor("mail_origen");
                html = "<div class='H3'>CONFIGURACION DEL SERVIDOR DE CORREO</div>" +
                    "<table width='500'>" +
                    "<tr><td>Servidor de correos: <span class='marca'>*</span></td>" +
                    "<td><input type='text' id='mail_svr' name='mail_svr' size='50' maxlength='200' value='"+mail_svr+"' /></td></tr>" +
                    "<tr><td>E-Mail de origen: <span class='marca'>*</span></td>" +
                    "<td><input type='text' id='mail_origen' name='mail_origen' size='50' maxlength='200' value='"+mail_origen+"' /></td></tr>" +
                    "<tr><td colspan='2'><input type='submit' value='Guardar' /></td></tr></table>";
            break;

            case 10:
                String rad_db = conf.getValor("rad_db");
                String rad_ip = conf.getValor("rad_ip");
                String rad_puerto = conf.getValor("rad_puerto");
                String rad_usuario = conf.getValor("rad_usuario");
                String rad_clave = conf.getValor("rad_clave");
                html = "<div class='H3'>CONFIGURACION DEL SERVIDOR RADIUS</div>" +
                    "<table width='500'>" +
                    "<tr><td>Nombre de la base de datos: <span class='marca'>*</span></td>" +
                    "<td><input type='text' id='rad_db' name='rad_db' size='30' maxlength='50' value='"+rad_db+"' /></td></tr>" +
                    "<tr><td>IP del computador donde est&aacute; instalado: <span class='marca'>*</span></td>" +
                    "<td><input type='text' id='rad_ip' name='rad_ip' size='30' maxlength='36' value='"+rad_ip+"' /></td></tr>" +
                    "<tr><td>Puerto de conexi&oacute;n a la base de datos: <span class='marca'>*</span></td>" +
                    "<td><input type='text' id='rad_puerto' name='rad_puerto' size='30' maxlength='6' value='"+rad_puerto+"' onkeypress=\"_numero(event);\" /></td></tr>" +
                    "<tr><td>Usuario de la base de datos: <span class='marca'>*</span></td>" +
                    "<td><input type='text' id='rad_usuario' name='rad_usuario' size='30' maxlength='50' value='"+rad_usuario+"' /></td></tr>" +
                    "<tr><td>Clave del usuario: <span class='marca'>*</span></td>" +
                    "<td><input type='text' id='rad_clave' name='rad_clave' size='30' maxlength='50' value='"+rad_clave+"' /></td></tr>" +
                    "<tr><td colspan='2'><input type='submit' value='Guardar' /></td></tr></table>";
            break;

            case 11:
                String airC_ip = conf.getValor("airC_ip");
                String airC_puerto = conf.getValor("airC_puerto");
                String airC_usuario = conf.getValor("airC_usuario");
                String airC_clave = conf.getValor("airC_clave");
                html = "<div class='H3'>CONFIGURACION DEL SERVIDOR AIRCONTROL</div>" +
                    "<table width='500'>" +
                    "<tr><td>IP del computador donde est&aacute; instalado: <span class='marca'>*</span></td>" +
                    "<td><input type='text' id='airC_ip' name='airC_ip' size='30' maxlength='36' value='"+airC_ip+"' /></td></tr>" +
                    "<tr><td>Puerto de conexi&oacute;n al servidor de Aplicaciones (Tomcat): <span class='marca'>*</span></td>" +
                    "<td><input type='text' id='airC_puerto' name='airC_puerto' size='30' maxlength='6' value='"+airC_puerto+"' onkeypress=\"_numero(event);\" /></td></tr>" +
                    "<tr><td>Usuario de conexi&oacute;n a AirControl: <span class='marca'>*</span></td>" +
                    "<td><input type='text' id='airC_usuario' name='airC_usuario' size='30' maxlength='50' value='"+airC_usuario+"' /></td></tr>" +
                    "<tr><td>Clave de conexi&oacute;n a AirControl: <span class='marca'>*</span></td>" +
                    "<td><input type='text' id='airC_clave' name='airC_clave' size='30' maxlength='50' value='"+airC_clave+"' /></td></tr>" +
                    "<tr><td colspan='2'><input type='submit' value='Guardar' /></td></tr></table>";
            break;
                
            case 12:
                String sftp_ip = conf.getValor("sftp_ip");
                String sftp_puerto = conf.getValor("sftp_puerto");
                String sftp_usuario = conf.getValor("sftp_usuario");
                String sftp_clave = conf.getValor("sftp_clave");
                html = "<div class='H3'>CONFIGURACION DEL SERVIDOR SFTP</div>" +
                    "<table width='500'>" +
                    "<tr><td>Servidor SFTP: <span class='marca'>*</span></td>" +
                    "<td><input type='text' id='sftp_ip' name='sftp_ip' size='30' maxlength='60' value='"+sftp_ip+"' /></td></tr>" +
                    "<tr><td>N&uacute;mero de puerto de conexi&oacute;n: <span class='marca'>*</span></td>" +
                    "<td><input type='text' id='sftp_puerto' name='sftp_puerto' size='30' maxlength='6' value='"+sftp_puerto+"' onkeypress=\"_numero(event);\" /></td></tr>" +
                    "<tr><td>Usuario de conexi&oacute;n: <span class='marca'>*</span></td>" +
                    "<td><input type='text' id='sftp_usuario' name='sftp_usuario' size='30' maxlength='50' value='"+sftp_usuario+"' /></td></tr>" +
                    "<tr><td>Contrase&ntilde;a de conexi&oacute;n: <span class='marca'>*</span></td>" +
                    "<td><input type='text' id='sftp_clave' name='sftp_clave' size='30' maxlength='100' value='"+sftp_clave+"' /></td></tr>" +
                    "<tr><td colspan='2'><input type='submit' value='Guardar' /></td></tr></table>";
            break;
                    
        }
            
        try {
            out.println("obj»d_21^frm»<form action='frmConfiguracionGuardar' onsubmit='return adm_configuracionGuardar(this)' autocomplete='off'>"+
                    "<input type='hidden' id='m' name='m' value='"+modulo+"' />" + html+"</form>");
        } finally { 
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
