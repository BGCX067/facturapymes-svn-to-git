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

import javax.servlet.*;
import javax.servlet.http.*;
import jm.seg.clas.Rol;
import jm.fac.clas.Cliente;
import java.sql.ResultSet;
import jm.adm.clas.Configuracion;
import jm.adm.clas.ListaPrecio;
import jm.adm.clas.Sucursal;
import jm.adm.clas.Ubicacion;
import jm.fac.clas.Contrato;
import jm.adm.clas.Documento;
import jm.cnt.clas.PlanCuenta;
import jm.web.DatosDinamicos;
import jm.web.Fecha;

/**
 *
 * @author Jorge
 */
public class frmCliente extends HttpServlet {
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
        String id_rol = (String)sesion.getAttribute("id_rol");
        int id_sucursal_sesion = (Integer)sesion.getAttribute("sucursal");
        String usuario = (String)sesion.getAttribute("usuario");
        String clave = (String)sesion.getAttribute("clave");

        response.setContentType("text/html;charset=UTF-8");
        response.setHeader("Pragma", "no-cache");
        response.setHeader("Expires", "Mon, 01 Jan 2001 00:00:01 GMT");
        response.setHeader("Cache-Control", "no-store");
        response.setHeader("Cache-Control", "must-revalidate");
        response.setHeader("Cache-Control", "no-cache");
        PrintWriter out = response.getWriter();

        ListaPrecio obj_ListaPrecio = new ListaPrecio(this._ip, this._puerto, this._db, usuario, clave);
        ResultSet rsListPrecios = obj_ListaPrecio.getListas();

        Ubicacion objUbicacion = new Ubicacion(this._ip, this._puerto, this._db, usuario, clave);
        ResultSet rsProvincias = objUbicacion.getUbicaciones("1");
        ResultSet rsCiudades = objUbicacion.getUbicaciones("210");
        ResultSet rsParroquias = objUbicacion.getUbicaciones("21001");

        Rol objRol = new Rol(this._ip, this._puerto, this._db, usuario, clave);

        Sucursal objSuc = new Sucursal(this._ip, this._puerto, this._db, usuario, clave);
        ResultSet rsSucursales = objSuc.getSucursales();

        Configuracion conf = new Configuracion(this._ip, this._puerto, this._db, usuario, clave);
        //String rep_ruc = conf.getValor("ruc");
        //String rep_nombre = conf.getValor("rep_nombre");
        //String rep_cargo = conf.getValor("rep_cargo");
        String id_sucursal_matriz = conf.getValor("id_sucursal_matriz");
        String cxc = conf.getValor("cxc");

        try {
            String altB = request.getParameter("altB");
            int _altBody = Integer.parseInt(altB);
            String id = request.getParameter("id");
            String establecimiento = "001";
            String ruc = "";
            String tipo_documento = "05";
            String razon_social = "";
            String fecha_nacimiento = "";
            String edad = "";
            String id_plan_cuenta = cxc;
            //String id_lista_precio = "-0";
            String direccion = "";
            String id_provincia = "210";
            String id_ciudad = "21001";
            String id_parroquia = "";
            String pais = "ECUADOR";
            String carne_conadis = "";
            String telefono = "";
            String movil_claro = "";
            String movil_movistar = "";
            String fax = "";
            String email = "";
            String web = "";
            String contacto = "";
            String observacion = "";
            
            String tipo_doc_debito = "";
            String documento = "";
            String cliente_debito = "";
            String forma_pago = "";
            String tipo_cuenta = "";
            String tipo_tarjeta_credito = "";
            String tarjeta_credito_caduca = "";
            String num_cuenta = "";

            if(id.compareTo("-1")!=0){
                try{
                    Cliente objCliente = new Cliente(this._ip, this._puerto, this._db, usuario, clave);
                    ResultSet rsCliente = objCliente.getCliente(id);
                    if(rsCliente.next()){
                        tipo_documento = (rsCliente.getString("tipo_documento")!=null) ? rsCliente.getString("tipo_documento") : "";
                        establecimiento = (rsCliente.getString("establecimiento")!=null) ? rsCliente.getString("establecimiento") : "";
                        ruc = (rsCliente.getString("ruc")!=null) ? rsCliente.getString("ruc") : "";
                        razon_social = (rsCliente.getString("razon_social")!=null) ? rsCliente.getString("razon_social") : "";
                        fecha_nacimiento = (rsCliente.getString("fecha_nacimiento")!=null) ? Fecha.ISOaSQL(rsCliente.getString("fecha_nacimiento")) : "";
                        edad = (rsCliente.getString("edad")!=null) ? rsCliente.getString("edad") : "";
                        id_plan_cuenta = (rsCliente.getString("id_plan_cuenta")!=null) ? rsCliente.getString("id_plan_cuenta") : cxc;
                        //id_lista_precio = (rsCliente.getString("id_lista_precio")!=null) ? rsCliente.getString("id_lista_precio") : "";
                        direccion = (rsCliente.getString("direccion")!=null) ? rsCliente.getString("direccion") : "";
                        id_provincia = (rsCliente.getString("id_provincia")!=null) ? rsCliente.getString("id_provincia") : "";
                        id_ciudad = (rsCliente.getString("id_ciudad")!=null) ? rsCliente.getString("id_ciudad") : "";
                        //id_parroquia = (rsCliente.getString("id_parroquia")!=null) ? rsCliente.getString("id_parroquia") : "";
                        pais = (rsCliente.getString("pais")!=null) ? rsCliente.getString("pais") : "Ecuador";
                        carne_conadis = (rsCliente.getString("carne_conadis")!=null) ? rsCliente.getString("carne_conadis") : "";
                        telefono = (rsCliente.getString("telefono")!=null) ? rsCliente.getString("telefono") : "";
                        movil_claro = (rsCliente.getString("movil_claro")!=null) ? rsCliente.getString("movil_claro") : "";
                        movil_movistar = (rsCliente.getString("movil_movistar")!=null) ? rsCliente.getString("movil_movistar") : "";
                        fax = (rsCliente.getString("fax")!=null) ? rsCliente.getString("fax") : "";
                        email = (rsCliente.getString("email")!=null) ? rsCliente.getString("email") : "";
                        web = (rsCliente.getString("web")!=null) ? rsCliente.getString("web") : "";
                        contacto = (rsCliente.getString("contacto")!=null) ? rsCliente.getString("contacto") : "";
                        observacion = (rsCliente.getString("observacion")!=null) ? rsCliente.getString("observacion") : "";
                        
                        tipo_doc_debito = (rsCliente.getString("tipo_doc_debito")!=null) ? rsCliente.getString("tipo_doc_debito") : "";
                        documento = (rsCliente.getString("documento")!=null) ? rsCliente.getString("documento") : "";
                        cliente_debito = (rsCliente.getString("cliente_debito")!=null) ? rsCliente.getString("cliente_debito") : "";
                        forma_pago = (rsCliente.getString("forma_pago")!=null) ? rsCliente.getString("forma_pago") : "CTA";
                        tipo_cuenta = (rsCliente.getString("tipo_cuenta")!=null) ? rsCliente.getString("tipo_cuenta") : "";
                        tipo_tarjeta_credito = (rsCliente.getString("tipo_tarjeta_credito")!=null) ? rsCliente.getString("tipo_tarjeta_credito") : "";
                        tarjeta_credito_caduca = (rsCliente.getString("tarjeta_credito_caduca")!=null) ? rsCliente.getString("tarjeta_credito_caduca") : "";
                        num_cuenta = (rsCliente.getString("num_cuenta")!=null) ? rsCliente.getString("num_cuenta") : "";
                        rsCliente.close();
                    }
                    objCliente.cerrar();
                }catch(Exception e){
                    e.printStackTrace();
                }
            }


            rsCiudades = objUbicacion.getUbicaciones(id_provincia);
            rsParroquias = objUbicacion.getUbicaciones(id_ciudad);

            PlanCuenta objPlanCuenta = new PlanCuenta(this._ip, this._puerto, this._db, usuario, clave);
            ResultSet rsCuentas = objPlanCuenta.getTablasAsignacion();
            objPlanCuenta.cerrar();

            String [][] tipo_documento1 = {{"04","RUC"},{"05","C&eacute;dula"},{"06","Pasaporte"}};
            String [][] tipo_doc_debito1 = {{"","SIN ASIGNACION"},{"R","RUC"},{"C","C&eacute;dula"},{"P","Pasaporte"}};
            String [][] forma_pago1 = {{"","SIN ASIGNACION"},{"CTA","Cuenta bancaria"},{"TAR","Tarjeta"}};
            String [][] tipo_cuenta1 = {{"","SIN ASIGNACION"},{"AHO","Ahorros"},{"CTE","Corriente"}};
            String [][] tipo_tarjeta_credito1 = {{"0","SIN ASIGNACION"},{"1","Diners Club"},{"2","Discover"},{"3","Mastercard"},{"4","Visa"}};

            String html = "obj»cmp_html^foc»ru^fun»_objTab.seleccionar(0);^frm»";
            html += "<div class='H3' style='width:730px'>Formulario de Cliente</div>";

            html += "<div class='grupoTab' style='width:710px;'><div id='tabR0'>"
                    + "<a id='tab00' href='javascript:void(0);' class='jm_tabAct' onclick='_objTab.seleccionar(0);'>Cliente</a>";
            if(id.compareTo("-1")!=0){
                html += "<a id='tab01' href='javascript:void(0);' class='jm_tab' onclick='_objTab.seleccionar(1);'>Contrato</a>";
            }
            html += "</div>";

            /* PRIMER TAB */
            html += "<form action='frmClienteGuardar' onsubmit='return fac_clienteGuardar(this)' autocomplete='off'>";

            html += "<div id='tabR1'>";
            html += "<div id='tab10' class='jm_tabHTML' style='display:block;width:700px;height:"+_altBody+"px;'>";

            html += "<input type='hidden' id='id' name='id' value='"+id+"' />";

            html += "<table><tr><td>Establecimiento: <span class='marca'>*</span></td>";
            html += "<td><input id='establecimiento' name='establecimiento' type='text' size='40' maxlength='3' value='"+establecimiento+"' onkeypress=\"_numero(event);\" /></td></tr>";

            html += "<tr><td>Tipo de documento: <span class='marca'>*</span></td>";
            html += "<td>" + DatosDinamicos.combo("tipo_documento", tipo_documento, tipo_documento1, "") + "</td></tr>";

            html += "<tr><td>C&eacute;dula o RUC: <span class='marca'>*</span></td>";
            html += "<td nowrap><input id='ru' name='ru' type='text' size='25' maxlength='13' class='may' value='"+ruc+"' onkeypress=\"_DNI(event)\" /> " +
                    " <input type='checkbox' id='vf' checked='checked' disabled /> verificar</td></tr>";
            html += "<tr><td>Raz&oacute;n Social: <span class='marca'>*</span></td>";
            html += "<td><input id='rs' name='rs' class='may' type='text' size='40' maxlength='100' value='"+razon_social+"' onkeypress=\"_sinEspeciales(event)\" onblur=\"this.value=this.value._trim();\" /></td></tr>";

            html += "<tr><td>Fecha de nacimiento: </td>";
            html += "<td><input id='fecha_nacimiento' name='fecha_nacimiento' type='text' size='10' value='"+fecha_nacimiento+"' onkeypress=\"_evaluar(event, '0123456789/');\" /> "
                    + "<input type='button' value='...' onClick=\"SelectorFecha.crear('fecha_nacimiento', 'SQL');\" /></td></tr>";

            html += "<tr><td>Edad: </td>";
            html += "<td>"+edad+" a&ntilde;os</td></tr>";

            html += "<tr><td>Cuenta por cobrar: <span class='marca'>*</span></td>";
            html += "<td>"+DatosDinamicos.combo(rsCuentas, "idPC", id_plan_cuenta, "", 231)+"</td></tr>";

            /*html += "<tr><td>Lista de precios: <span class='marca'>*</span></td>";
            html += "<td>" + DatosDinamicos.combo(rsListPrecios, "liPr", id_lista_precio, "", " TOMAR LISTA DE LA SUCURSAL ", 231) + "</td></tr>";
*/
            html += "<tr><td>Direcci&oacute;n: <span class='marca'>*</span></td>";
            html += "<td><input id='di' name='di' type='text' class='may' size='40' maxlength='80' value='"+direccion+"' onkeypress=\"_alfanumerico(event);\" onblur=\"this.value=this.value._trim();\" /></td></tr>";

            html += "<tr><td>Pa&iacute;s: </td>";
            html += "<td><input id='pa' name='pa' class='may' type='text' size='40' maxlength='30' value='"+pais+"' onkeypress=\"_letra(event);\" onblur=\"this.value=this.value._trim();\" /></td></tr>";
            html += "<tr><td>Provincia: </td>";
            html += "<td><div id='cob0' style='display:table-cell'>" + DatosDinamicos.combo(rsProvincias, "prv", id_provincia, "_pX='getUbicacion(:cob2:,:prr:,:ci:,200);';_pY='getUbicacion(:cob2:,:prr:,:ci:,200);';getUbicacion('cob1','ci','prv',200);", "", 231) + "</div></td></tr>";
            html += "<tr><td>Cant&oacute;n: </td>";
            html += "<td nowrap><div id='cob1' style='float:left;display:table-cell'>" + DatosDinamicos.combo(rsCiudades, "ci", id_ciudad, "", "", 200) + "</div></td></tr>";
            /*html += "<td nowrap><div id='cob1' style='float:left;display:table-cell'>" + DatosDinamicos.combo(rsCiudades, "ci", id_ciudad, "", "", 200) + "</div>"
                    + "<div class='jm_icoCon' id='gr1' style='visibility:hidden;background-position: -545px 0px;' onclick=\"guardarUbicacion(null,'cob1','ci','gr1','add1',200,'prv');\" title='guardar'>&nbsp;</div>"
                    + "<div class='jm_icoCon' id='add1' style='background-position:-504px -14px;' onclick=\"insertarUbicacion('cob1','ci','gr1','add1',200,'prv');_pX='getUbicacion(:cob2:,:prr:,:ci:,200);';_pY='getUbicacion(:cob2:,:prr:,:ci:,200);';\" title='Insertar'>&nbsp;</div></td></tr>";
            html += "<tr><td>Parroquia: </td>";
            html += "<td nowrap><div id='cob2' style='float:left;display:table-cell'>" + DatosDinamicos.combo(rsParroquias, "prr", id_parroquia, "", "", 200) + "</div>"
                    + "<div class='jm_icoCon' id='gr2' style='visibility:hidden;background-position: -545px 0px;' onclick=\"guardarUbicacion(null,'cob2','prr','gr2','add2',200,'ci');\" title='guardar'>&nbsp;</div>"
                    + "<div class='jm_icoCon' id='add2' style='background-position:-504px -14px;' onclick=\"insertarUbicacion('cob2','prr','gr2','add2',200,'ci');\" title='Insertar'>&nbsp;</div></td></tr>";
*/
            html += "<tr><td>Tel&eacute;fonos:</td>";
            html += "<td><input id='te' name='te' type='text' size='40' maxlength='30' value='"+telefono+"' onkeypress=\"_evaluar(event, '0123456789-/ ');\" onblur=\"this.value=this.value._trim();\" /></td></tr>";
            html += "<tr><td>M&oacute;vil claro:</td>";
            html += "<td><input id='te_cl' name='te_cl' type='text' size='40' maxlength='10' value='"+movil_claro+"' onkeypress=\"_numero(event);\" /></td></tr>";
            html += "<tr><td>M&oacute;vil movistar:</td>";
            html += "<td><input id='te_mo' name='te_mo' type='text' size='40' maxlength='10' value='"+movil_movistar+"' onkeypress=\"_numero(event);\" /></td></tr>";
            html += "<tr><td>Fax:</td>";
            html += "<td><input id='fa' name='fa' type='text' size='40' maxlength='30' value='"+fax+"' onkeypress=\"_evaluar(event, '0123456789-/ ');\" onblur=\"this.value=this.value._trim();\" /></td></tr>";
            html += "<tr><td>E-Mail:</td>";
            html += "<td><input id='ma' name='ma' type='text' size='40' maxlength='50' value='"+email+"' onblur=\"this.value=this.value._trim();\" /></td></tr>";
            html += "<tr><td>Web:</td>";
            html += "<td><input id='we' name='we' type='text' size='40' maxlength='50' value='"+web+"' onblur=\"this.value=this.value._trim();\" /></td></tr>";
            html += "<tr><td>Contacto:</td>";
            html += "<td><input id='co' name='co' type='text' size='40' maxlength='70' value='"+contacto+"' onkeypress=\"_letra(event);\" onblur=\"this.value=this.value._trim();\" /></td></tr>";
            html += "<tr><td>Observaci&oacute;n:</td>";
            html += "<td><textarea id='ob' name='ob' rows='2' cols='25' onblur=\"this.value=this.value._trim();\" onkeypress=\"_alfanumerico(event);\">"+observacion+"</textarea></td></tr>";
            html += "</table>";

            html += "<fieldset style='background-color:#E2EDF3'><legend style='background-color:#E2EDF3'>PERSONAS CON CAPACIDADES ESPECIALES</legend><table>";
            html += "<tr><td>Carné CONADIS:</td>";
            html += "<td><input id='carne_conadis' name='carne_conadis' type='text' size='40' maxlength='30' value='"+carne_conadis+"' onblur=\"this.value=this.value._trim();\" /></td></tr>";
            html += "</table></fieldset>";

            html += "<fieldset style='background-color:#CCEBFF'><legend style='background-color:#CCEBFF'>CONVENIO DE DEBITO BANCO PICHINCHA</legend><table>";
            html += "<tr><td>Tipo de documento: <span class='marca'>*</span></td>";
            html += "<td>" + DatosDinamicos.combo("tipo_doc_debito", tipo_doc_debito, tipo_doc_debito1, "") + "</td></tr>";
            html += "<tr><td>Documento de identificaci&oacute;n: <span class='marca'>*</span></td>";
            html += "<td nowrap><input id='documento' name='documento' type='text' size='25' maxlength='13' class='may' value='"+documento+"' onkeypress=\"_DNI(event)\" /></td></tr>";
            html += "<tr><td>Nombre del propietario de la cuenta: <span class='marca'>*</span></td>";
            html += "<td><input id='cliente_debito' name='cliente_debito' class='may' type='text' size='40' maxlength='100' value='"+cliente_debito+"' onkeypress=\"_letra(event);\" onblur=\"this.value=this.value._trim();\" /></td></tr>";
            html += "<tr><td>Forma de pago: <span class='marca'>*</span></td>";
            html += "<td>" + DatosDinamicos.combo("forma_pago", forma_pago, forma_pago1, "fac_setFormaP()") + "</td></tr>";
            
            html += "<tr><td colspan='2'>";
            html += "<table id='axsilCta' style=\"display:"+(forma_pago.compareTo("CTA")==0 ? "block" : "none")+"\">";
            html += "<tr><td width='190'>Tipo de cuenta: <span class='marca'>*</span></td>";
            html += "<td>" + DatosDinamicos.combo("tipo_cuenta", tipo_cuenta, tipo_cuenta1, "") + "</td></tr>";
            html += "</table>";
            html += "</td></tr>";
            
            html += "<tr><td colspan='2'>";
            html += "<table id='axTipTar' style=\"display:"+(forma_pago.compareTo("TAR")==0 ? "block" : "none")+"\">";
            html += "<tr><td  width='190'>Tipo de tarjeta de cr&eacute;dito: <span class='marca'>*</span></td>";
            html += "<td>" + DatosDinamicos.combo("tipo_tarjeta_credito", tipo_tarjeta_credito, tipo_tarjeta_credito1, "") + "</td></tr>";
            html += "<tr><td >Fecha de caducidad de la tarjeta: <span class='marca'>*</span></td>";
            html += "<td nowrap><input id='tarjeta_credito_caduca' name='tarjeta_credito_caduca' type='text' size='10' value='"+tarjeta_credito_caduca+"' onkeypress=\"_evaluar(event, '0123456789/');\" /> "
                    + "<input type='button' value='...' onClick=\"SelectorFecha.crear('tarjeta_credito_caduca', 'SQL');\" /></td></tr>";
            html += "</table>";
            html += "</td></tr>";
            
            html += "<tr><td>N&uacute;mero de cuenta o tarjeta de cr&eacute;dito: <span class='marca'>*</span></td>";
            html += "<td><input id='num_cuenta' name='num_cuenta' type='text' size='40' maxlength='16' value='"+num_cuenta+"' onkeypress=\"_evaluar(event, '0123456789-');\" /> &nbsp; ";
            
            if(id.compareTo("-1")!=0){
                html += "<input type='button' value='Imprimir autorizaci&oacute;n' onclick=\"imprimir('pdfAutorizacion?id="+id+"')\" />";
            }
            html += "</td></tr></table></fieldset>";
            
            
            
            if(id.compareTo("-1")==0 || (id.compareTo("-1")!=0 && objRol.getAcceso(id_rol, "clienteEditar"))){
                html += "<input type='submit' value='Guardar' />";
            }
            
            html += "</div>";

            html += "</form>";



            
            /* SEGUNDO TAB */




            String id_contrato = "-1";
            String num_contrato = "";
            String id_sucursal = id_sucursal_matriz;
            String fecha_contrato = Fecha.getFecha("SQL");
            String fecha_termino = Fecha.getDia() + "/" +Fecha.getMes() + "/" + (Fecha.getAnio() + 1);
            String ruc_representante = "";
            String representante = "";
            String contrato = "";
            String borradorContrato = "";
            String numContratos = "";
            //boolean anulado = false;
            try{
                Contrato objContrato = new Contrato(this._ip, this._puerto, this._db, usuario, clave);
                numContratos = objContrato.getNumContratosJSON();
                ResultSet rsContrato = objContrato.getContratoCliente(id);
                if(rsContrato.next()){
                    id_contrato = rsContrato.getString("id_contrato")!=null ? rsContrato.getString("id_contrato") : "-1";
                    num_contrato = rsContrato.getString("num_contrato")!=null ? rsContrato.getString("num_contrato") : "";
                    id_sucursal = rsContrato.getString("id_sucursal")!=null ? rsContrato.getString("id_sucursal") : "";
                    fecha_contrato = rsContrato.getString("fecha_contrato")!=null ? rsContrato.getString("fecha_contrato") : "";
                    fecha_termino = rsContrato.getString("fecha_termino")!=null ? rsContrato.getString("fecha_termino") : "";
                    ruc_representante = rsContrato.getString("ruc_representante")!=null ? rsContrato.getString("ruc_representante") : "";
                    representante = rsContrato.getString("representante")!=null ? rsContrato.getString("representante") : "";
                    contrato = rsContrato.getString("contrato")!=null ? rsContrato.getString("contrato") : "";
                    //anulado = (rsContrato.getString("anulado")!=null) ? rsContrato.getBoolean("anulado") : false;
                    rsContrato.close();
                }
                objContrato.cerrar();
            }catch(Exception e){}

            Documento objDocumento = new Documento(this._ip, this._puerto, this._db, usuario, clave);
            try{
                ResultSet rsDoc = objDocumento.getDocumento("c");
                if(rsDoc.next()){
                    borradorContrato = rsDoc.getString("documento")!=null ? rsDoc.getString("documento") : "";
                    rsDoc.close();
                }
                objDocumento.cerrar();
            }catch(Exception e){
                e.printStackTrace();
            }finally{
                objDocumento.cerrar();
            }

            String oculto = id_contrato.compareTo("-1")==0 ? "style='display:none'" : "style='display:block'";


            html += "<div id='tab11' class='jm_tabHTML' style='display:none;width:700px;height:"+_altBody+"px;'>";

            html += "<form id='frm2' action='frmContratoGuardar' onsubmit=\"return fac_contratoGuardar(_('frm2'))\" autocomplete='off'>";
            html += "<input type='hidden' id='idCli' name='idCli' value='"+id+"' />";
            html += "<input type='hidden' id='idCont' name='idCont' value='"+id_contrato+"' />";
            html += "<input type='hidden' id='JSN' value='"+numContratos+"' />";
            //html += "<input type='hidden' id='rep_ruc' value='"+rep_ruc+"' />";
            //html += "<input type='hidden' id='rep_nombre' value='"+rep_nombre+"' />";
            //html += "<input type='hidden' id='rep_cargo' value='"+rep_cargo+"' />";
            html += "<input type='hidden' id='borrContrato'  value='"+borradorContrato+"' />";
            if(id_contrato.compareTo("-1")==0){
                html += "<input type='button' id='btnCont' value='Ingresar contrato' onclick=\"fac_setContrato()\" />";
            }

            html += "<table id='auxCont' "+oculto+" width='100%'>"
                    + "<tr><td>Contrato Nro.: <input id='numCont' name='numCont' type='text' size='6' value='"+num_contrato+"' onkeypress=\"_numero(event);\" /></td>";
            if(Integer.valueOf(id_sucursal_matriz) == id_sucursal_sesion){
                 html += "<td>Sucursal: " + DatosDinamicos.combo(rsSucursales, "idSucur", id_sucursal, "fac_setContrato()", "", 100) + "</td>";
            }else{
                //if(id.compareTo("-1")==0){
                    id_sucursal = String.valueOf(id_sucursal_sesion);
                //}
                html += "<td>Sucursal: <input type='hidden' id='idSucur' name='idSucur' value='"+id_sucursal+"' /> "+objSuc.getNombre(id_sucursal)+"</td>";
            }

            html += "<td>Fecha inicio: <input type='text' id='fechaCont' name='fechaCont' value='"+fecha_contrato+"' size='8' readOnly>"
                    + "<input type='button' value='...' onClick=\"SelectorFecha.crear('fechaCont', 'SQL', 'fac_setContrato()');\" /></td>"
                    + "<td>Fecha t&eacute;rmino: <input type='text' id='fechaTer' name='fechaTer' value='"+fecha_termino+"' size='8' readOnly>"
                    + "<input type='button' value='...' onClick=\"SelectorFecha.crear('fechaTer', 'SQL', 'fac_setContrato()');\" /></td></tr>"
                    + "<tr><td colspan='4'>R.U.C. / C.I. del representante: <input type='text' id='ruc_representante' name='ruc_representante' size='13' maxlenth='13' value='"+ruc_representante+"' /></td></tr>"
                    + "<tr><td colspan='4'>Representante: <input type='text' id='representante' name='representante' size='100' maxlenth='100' value='"+representante+"' /></td></tr>"
                    + "<tr><td colspan='4'><hr /></td></tr>"
                    + "<tr><td colspan='4'><textarea id='contrato' name='contrato' readonly style='width:670px;height:"+(_altBody-140)+"px;'>"+contrato+"</textarea></td></tr>";

            //if(!anulado){
                if(id.compareTo("-1")!=0 && objRol.getAcceso(id_rol, "contratoEditar")){
                    html += "<tr><td><input type='submit' value='Guardar' /></td>"
                            + "<td align='center' colspan='2'><input type='button' id='btnImp' value='Imprimir' onclick=\"window.open('pdfContrato?id='+_('idCont').value, '_blank')\" /></td>";
                    if(id_contrato.compareTo("-1")!=0 && objRol.getAcceso(id_rol, "contratoAnular")){
                        html += "<td align='right'><input type='button' value='Anular' onclick=\"if(confirm('¿Está seguro(a) de anular el documento de contrato?')){cnt_anularDocumento('tbl_contrato', '"+id_contrato+"');}\" />";
                    }
                    html += "</tr>";
                }
            /*}else{
                out.println("<td><span class='marca'>DOCUMENTO DE CONTRATO ANULADO</span></td>");
            }*/
            html += "</table></form>";

            html += "</div></div></div>";

            out.println(html);


            try{
                rsListPrecios.close();
            }catch(Exception e){
                e.printStackTrace();
            }

        } finally {
            conf.cerrar();
            objSuc.cerrar();
            objRol.cerrar();
            objUbicacion.cerrar();
            obj_ListaPrecio.cerrar();
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