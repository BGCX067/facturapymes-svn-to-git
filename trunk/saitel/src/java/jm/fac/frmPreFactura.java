/**
* @version 3.0
* @package YAKUSOFT.
* @author Jorge Washington Mueses Cevallos.
* @copyright Copyright (C) 2010 por Jorge Mueses. Todos los derechos reservados.
* @license http://www.gnu.org/copyleft/gpl.html GNU/GPL.
* YAKUSOFT! es un software de libre distribución, que puede ser
* copiado y distribuido bajo los términos de la Licencia Pública
* General GNU, de acuerdo con la publicada por la Free Software
* Foundation, versión 2 de la licencia o cualquier versión posterior.
*/
package jm.fac;

import java.io.*;

import javax.servlet.*;
import javax.servlet.http.*;
import jm.seg.clas.Rol;
import jm.fac.clas.PreFactura;
import java.sql.ResultSet;
import jm.adm.clas.Configuracion;
import jm.adm.clas.PuntoEmision;
import jm.adm.clas.TablaImpuesto;
import jm.cnt.clas.Banco;
import jm.fac.clas.FacturaVenta;
import jm.fac.clas.Instalacion;
import jm.fac.clas.Sector;
import jm.inv.clas.BaseImponible;
import jm.inv.clas.Producto;
import jm.web.Addons;
import jm.web.DatosDinamicos;
import jm.web.Fecha;

/**
 *
 * @author Jorge
 */
public class frmPreFactura extends HttpServlet {
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
        int id_punto_emision = (Integer)sesion.getAttribute("id_punto_emision");
        String usuario = (String)sesion.getAttribute("usuario");
        String clave = (String)sesion.getAttribute("clave");

        response.setContentType("text/html;charset=UTF-8");
        response.setHeader("Pragma", "no-cache");
        response.setHeader("Expires", "Mon, 01 Jan 2001 00:00:01 GMT");
        response.setHeader("Cache-Control", "no-store");
        response.setHeader("Cache-Control", "must-revalidate");
        response.setHeader("Cache-Control", "no-cache");
        PrintWriter out = response.getWriter();

        String idPreFactura = request.getParameter("id");
        String modulo = request.getParameter("mod");
        //String idDocBancoCashTarCred = request.getParameter("idDocBancoCashTarCred");
        String _alto = request.getParameter("altB");
        int _altBody = Integer.parseInt(_alto) - 290;



        String fac_num_serie = "";
        String fac_sec_desde = "";
        String fac_sec_hasta = "";
        String fac_cad_libretin = "";
        String fac_autorizacion = "";
        String id_plan_cuenta_caja = "";
        PuntoEmision objPuntoEmision = new PuntoEmision(this._ip, this._puerto, this._db, usuario, clave);
        try{
            ResultSet rs = objPuntoEmision.getPuntoEmision(String.valueOf(id_punto_emision));
            if(rs.next()){
                fac_num_serie = (rs.getString("fac_num_serie")!=null) ? rs.getString("fac_num_serie") : "";
                fac_sec_desde = (rs.getString("fac_sec_desde")!=null) ? rs.getString("fac_sec_desde") : "";
                fac_sec_hasta = (rs.getString("fac_sec_hasta")!=null) ? rs.getString("fac_sec_hasta") : "";
                fac_cad_libretin = (rs.getString("fac_cad_facturero")!=null) ? rs.getString("fac_cad_facturero") : "";
                fac_autorizacion = (rs.getString("fac_autorizacion")!=null) ? rs.getString("fac_autorizacion") : "";
                id_plan_cuenta_caja = (rs.getString("id_plan_cuenta_caja")!=null) ? rs.getString("id_plan_cuenta_caja") : "";
                rs.close();
            }
        }catch(Exception e){
            e.printStackTrace();
        }finally{
            objPuntoEmision.cerrar();
        }

        Configuracion conf = new Configuracion(this._ip, this._puerto, this._db, usuario, clave);
        float p_iva0 = Integer.parseInt(conf.getValor("p_iva1"));
        conf.cerrar();
/*
        String json_productos = "";
        Producto objProducto = new Producto(this._ip, this._puerto, this._db, usuario, clave);
        ResultSet rsProductos = objProducto.getProductosVenta(id_sucursal_sesion);
        json_productos = objProducto.getJSON(rsProductos);
        try{
            rsProductos.close();
        }catch(Exception e){
            e.printStackTrace();
        }
        objProducto.cerrar();*/

        Rol objRol = new Rol(this._ip, this._puerto, this._db, usuario, clave);

        Banco objBanco = new Banco(this._ip, this._puerto, this._db, usuario, clave);
        ResultSet rsBancos = objBanco.getBancosIngresos();
        objBanco.cerrar();
        
        BaseImponible objBaseImponible = new BaseImponible(this._ip, this._puerto, this._db, usuario, clave);

        FacturaVenta objFactura = new FacturaVenta(this._ip, this._puerto, this._db, usuario, clave);

        TablaImpuesto objTablaImpuesto = new TablaImpuesto(this._ip, this._puerto, this._db, usuario, clave);

        Sector objSector = new Sector(this._ip, this._puerto, this._db, usuario, clave);
        ResultSet rsSectores = objSector.getSectores(id_sucursal_sesion);

        PreFactura objPreFactura = new PreFactura(this._ip, this._puerto, this._db, usuario, clave);

        String id_instalacion = "";
        String id_cliente = "";
        String periodo_suspension = "";
        
        try {
            objPreFactura.calcularPreFactura(idPreFactura);
            
            long num_factura = objFactura.getNumFactura(id_punto_emision);
            
            String ruc = "";
            String razon_social = "";
            String direccion = "";
            String telefono = "";
            String movil_claro = "";
            String movil_movistar = "";
            String plan = "";
            String desc_3_edad = "0";
            String carne_conadis = "";
            String edad = "";
            String id_sector = "";
            String direccion_instalacion = "";
            String txt_periodo = "";
            String id_producto = "";
            String dias_conexion = "30";
            double iva_internet = 0;
            double subt_internet = 0;
            double valor_internet = 0;
            double subtotal = 0;
            String subtotal_0 = "0";
            double subtotal_2 = 0;
            double iva_2 = 0;
            String descuento = "0";
            String meses_impagos = "0";
            double total_pagar = 0;
            String id_plan_actual = "-1";
            String id_producto_anticipo = "";
            String periodo = "";
            String num_cuenta = "";
            String estado_servicio = "";
            try{
                ResultSet rsPreFactura = objPreFactura.getPreFactura(idPreFactura);
                if(rsPreFactura.next()){
                    id_instalacion = (rsPreFactura.getString("id_instalacion")!=null) ? rsPreFactura.getString("id_instalacion") : "";
                    id_cliente = (rsPreFactura.getString("id_cliente")!=null) ? rsPreFactura.getString("id_cliente") : "";
                    periodo_suspension = (rsPreFactura.getString("periodo_suspension")!=null) ? rsPreFactura.getString("periodo_suspension") : "";
                    txt_periodo = (rsPreFactura.getString("txt_periodo")!=null) ? rsPreFactura.getString("txt_periodo") : "";
                    ruc = (rsPreFactura.getString("ruc")!=null) ? rsPreFactura.getString("ruc") : "";
                    razon_social = (rsPreFactura.getString("razon_social")!=null) ? rsPreFactura.getString("razon_social") : "";
                    desc_3_edad = (rsPreFactura.getString("desc_3_edad")!=null) ? rsPreFactura.getString("desc_3_edad") : "0";
                    carne_conadis = (rsPreFactura.getString("carne_conadis")!=null) ? rsPreFactura.getString("carne_conadis") : "";
                    edad = (rsPreFactura.getString("edad")!=null) ? rsPreFactura.getString("edad") : "";
                    direccion = (rsPreFactura.getString("direccion")!=null) ? rsPreFactura.getString("direccion") : "";
                    telefono = (rsPreFactura.getString("telefono")!=null) ? rsPreFactura.getString("telefono") : "";
                    movil_claro = (rsPreFactura.getString("movil_claro")!=null) ? rsPreFactura.getString("movil_claro") : "";
                    movil_movistar = (rsPreFactura.getString("movil_movistar")!=null) ? rsPreFactura.getString("movil_movistar") : "";
                    plan = (rsPreFactura.getString("plan")!=null) ? rsPreFactura.getString("plan") : "";
                    id_producto_anticipo = (rsPreFactura.getString("id_producto_anticipo")!=null) ? rsPreFactura.getString("id_producto_anticipo") : "";
                    
                    id_sector = (rsPreFactura.getString("id_sector")!=null) ? rsPreFactura.getString("id_sector") : "";
                    //sector = (rsPreFactura.getString("sector")!=null) ? rsPreFactura.getString("sector") : "";
                    direccion_instalacion = (rsPreFactura.getString("direccion_instalacion")!=null) ? rsPreFactura.getString("direccion_instalacion") : "";
                    id_producto = (rsPreFactura.getString("id_producto")!=null) ? rsPreFactura.getString("id_producto") : "";
                    dias_conexion = (rsPreFactura.getString("dias_conexion")!=null) ? rsPreFactura.getString("dias_conexion") : "";
                    valor_internet = (rsPreFactura.getString("valor_internet")!=null) ? rsPreFactura.getDouble("valor_internet") : 0;
                    iva_internet = (rsPreFactura.getString("iva_internet")!=null) ? rsPreFactura.getDouble("iva_internet") : 0;
                    subt_internet = (rsPreFactura.getString("total_internet")!=null) ? rsPreFactura.getDouble("total_internet") : 0;
                    //subt_internet = Addons.redondear(valor_internet / (p_iva1 / 100 + 1));
                    //iva_internet = Addons.redondear(valor_internet - subt_internet);
                    subtotal = (rsPreFactura.getString("subtotal")!=null) ? rsPreFactura.getDouble("subtotal") : 0;
                    subtotal_0 = (rsPreFactura.getString("subtotal_0")!=null) ? rsPreFactura.getString("subtotal_0") : "0";
                    subtotal_2 = (rsPreFactura.getString("subtotal_2")!=null) ? rsPreFactura.getDouble("subtotal_2") : 0;
                    iva_2 = (rsPreFactura.getString("iva_2")!=null) ? rsPreFactura.getDouble("iva_2") : 0;
                    descuento = (rsPreFactura.getString("descuento")!=null) ? rsPreFactura.getString("descuento") : "0";
                    meses_impagos = (rsPreFactura.getString("meses_impagos")!=null) ? rsPreFactura.getString("meses_impagos") : "0";
                    total_pagar = (rsPreFactura.getString("total")!=null) ? rsPreFactura.getDouble("total") : 0;
                    id_plan_actual = (rsPreFactura.getString("id_plan_actual")!=null) ? rsPreFactura.getString("id_plan_actual") : "-1";
                    periodo = (rsPreFactura.getString("periodo")!=null) ? rsPreFactura.getString("periodo") : "";
                    num_cuenta = (rsPreFactura.getString("num_cuenta")!=null) ? rsPreFactura.getString("num_cuenta") : "";
                    estado_servicio = (rsPreFactura.getString("estado_servicio")!=null) ? rsPreFactura.getString("estado_servicio") : "";
                    rsPreFactura.close();
                }
            }catch(Exception e){
                e.printStackTrace();
            }

            float costo_plan = 0;
            float p_iva1 = 0;
            try{
                ResultSet rsPlan = objPreFactura.getPlan(id_plan_actual, periodo);
                if(rsPlan.next()){
                    costo_plan = (rsPlan.getString("costo_plan")!=null) ? rsPlan.getFloat("costo_plan") : 0;
                    p_iva1 = (rsPlan.getString("porcentaje")!=null) ? rsPlan.getFloat("porcentaje") : 0;
                    rsPlan.close();
                }
            }catch(Exception e){
                e.printStackTrace();
            }
            
            double p_u_internet = Addons.redondear(costo_plan / (p_iva1 / 100 + 1) / 30,  4);
            
            
            String [][] forma_pago1 = {{"e","Efectivo"},{"c","Cheque"},{"p","Dep&oacute;sito bancario"},
                {"h","Cash Management Pichincha"},
                {"t","Transferencia bancaria"},{"j","Tarjeta de cr&eacute;dito"},{"d","Cr&eacute;dito"}};

            String codigo = "";
            String stk0 = "0";
            String p_c0 = "0";
            //String p_iva0 = "12";
            String p_v0 = "0";
            String p_des0 = "0";
            String d_srv0 = "~";
            String ut_min0 = "0";
            Producto objProducto = new Producto(this._ip, this._puerto, this._db, usuario, clave);
            ResultSet rsAnticipo = objProducto.getProductoAnticipo(id_producto_anticipo);
            String jsAnticipo = objProducto.getJSON(rsAnticipo);
            try{
                ResultSet rsProd = objProducto.getProductoISP(id_sucursal_sesion, id_producto);
                if(rsProd.next()){
                    codigo = rsProd.getString("codigo")!=null ? rsProd.getString("codigo") : "";
                    stk0 = rsProd.getString("stock_sucursal")!=null ? rsProd.getString("stock_sucursal") : "0";
                    p_c0 = rsProd.getString("precio_costo")!=null ? rsProd.getString("precio_costo") : "0";
                    //t_iva0 = rsProd.getString(5)!=null ? rsProd.getString(5) : "~";
                    p_v0 = rsProd.getString("precio_venta")!=null ? rsProd.getString("precio_venta") : "0";
                    p_des0 = rsProd.getString("descuento")!=null ? rsProd.getString("descuento") : "0";
                    //d_srv0 = rsProd.getString(8)!=null ? rsProd.getString(8) : "~";
                    ut_min0 = rsProd.getString("ut_min")!=null ? rsProd.getString("ut_min") : "0";
                    rsProd.close();
                }
            }catch(Exception e){
                e.printStackTrace();
            }finally{
                objProducto.cerrar();
            }
            
            
            ResultSet rsAbonos = objPreFactura.getAbonos(idPreFactura);
            
            if(objPreFactura.getFilas(rsAbonos)>0){
                double abonos[] = objFactura.getValoresAbono(idPreFactura);
                dias_conexion = "1";
                valor_internet = total_pagar = Addons.redondear(total_pagar - abonos[2]);
                subt_internet = subtotal = subtotal_2 = p_u_internet = subtotal_2 = Addons.redondear( total_pagar / (p_iva0/100+1) );
                //iva_2 = iva_internet = Addons.redondear( subtotal * p_iva0/100 );
                iva_2 = iva_internet = Addons.redondear( valor_internet - subt_internet );
            }
            
            
            

String html = "obj»d_21^fun»_objTab.seleccionar(0);_('id_sector').className='comboSimple';^frm»";

html += "<div class='grupoTab' style='width:540px;'><div id='tabR0'>"
     + "<a id='tab00' href='javascript:void(0);' class='jm_tabAct' onclick='_objTab.seleccionar(0);'>Pre-Factura</a>";
html += "<a id='tab01' href='javascript:void(0);' class='jm_tab' onclick='_objTab.seleccionar(1);'>Retenci&oacute;n</a>";
html += "<a id='tab02' href='javascript:void(0);' class='jm_tab' onclick='_objTab.seleccionar(2);'>Abonos</a>";
html += "</div>";

html += "<form action='frmPreFacturaGuardar' onsubmit='return fac_preFacturaGuardar(this)' autocomplete='off'>";
html += "<input type='hidden' id='idPF' name='idPF' value='"+idPreFactura+"' />";
html += "<input type='hidden' id='idIn' name='idIn' value='"+id_instalacion+"' />";
html += "<input type='hidden' id='id_cliente' name='id_cliente' value='"+id_cliente+"' />";
html += "<input type='hidden' id='secDe' name='secDe' value='"+fac_sec_desde+"' />";
html += "<input type='hidden' id='secHa' name='secHa' value='"+fac_sec_hasta+"' />";
html += "<input type='hidden' id='feCad' name='feCad' value='"+fac_cad_libretin+"' />";
html += "<input type='hidden' id='utSuc' name='utSuc' value='0' />";
html += "<input type='hidden' id='utCli' name='utCli' value='-1' />";
html += "<input type='hidden' id='emitir' name='emitir' value='1' />";
//html += "<input type='hidden' id='p_iva1' value='"+p_iva1+"' />";
//html += "<input type='hidden' id='iv0' name='iv0' value='"+iva_0+"' />";   //  sumatoria de ivas 0
html += "<input type='hidden' id='ctPl' name='ctPl' value='"+costo_plan+"' />";
html += "<input type='hidden' id='son' name='son' value='' />";
html += "<input type='hidden' id='tope' name='tope' value='0' />";
html += "<input type='hidden' id='fe' name='fe' value='"+Fecha.getFecha("ISO")+"' />";
html += "<input type='hidden' id='plan' name='plan' value='"+plan+"' />";
html += "<input type='hidden' id='txtPer' name='txtPer' value='"+txt_periodo+"' />";
html += "<input type='hidden' id='Per' name='Per' value='"+periodo+"' />";
html += "<input type='hidden' id='ruc' name='ruc' value='"+ruc+"' />";
html += "<input type='hidden' id='edad' name='edad' value='"+edad+"' />";
html += "<input type='hidden' id='carne' name='carne' value='"+carne_conadis+"' />";
html += "<input type='hidden' id='saldo_doc' name='saldo_doc' value='0' />";
html += "<input type='hidden' id='num_cuenta' name='num_cuenta' value='"+num_cuenta+"' />";
html += "<input type='hidden' id='anticipo_ini' name='anticipo_ini' value='' />";
html += "<input type='hidden' id='num_meses' name='num_meses' value='0' />";
html += "<input type='hidden' id='idProdAnt' name='idProdAnt' value='"+id_producto_anticipo+"' />";
html += "<input type='hidden' id='modulo' name='modulo' value='"+modulo+"' />";
//html += "<input type='hidden' id='idDocBancoCashTarCred' name='idDocBancoCashTarCred' value='"+idDocBancoCashTarCred+"' />";
html += "<input type='hidden' id='elect' name='elect' value='0' />";
html += "<div id='axAnti' style='display:none'>"+jsAnticipo+"</div>";

html += "<div id='tabR1'>";

            /* primer tab */


html += "<div id='tab10' class='jm_tabHTML' style='display:block;width:530px;height:"+_altBody+"px;'>";

html += "<table cellspacing='0' cellpadding='0'>";
html += "<tr>";
html += "<td colspan='2'>Factura Nro. <input type='text' size='5' maxlength='7' id='sFa' name='sFa' value='"+fac_num_serie+"' readonly onkeypress=\"_evaluar(event, '0123456789-')\" />-" +
    "<input type='text' id='nFa' name='nFa' size='7' maxlength='20' value='"+num_factura+"' onkeypress=\"_numero(event)\" /></td>";
html += "</tr>";
html += "<tr>";
html += "<td>Autorizaci&oacute;n <input type='text' size='10' id='aut' name='aut' value='"+fac_autorizacion+"' readonly onkeypress=\"_numero(event)\" /></td>";
html += "<td>Forma de pago: " + DatosDinamicos.combo("foPa", "", forma_pago1, "cnt_setFormaPago(this);") + "</td>";
html += "</tr>";
html += "<tr><td colspan='2'>"+

    /*"<table cellpadding='0' cellspacing='0' id='pCh' style=\"display:none;\">"+
    "<tr><td width='110'>Cheque #: <span class='marca'>*</span></td><td ><input id='chq' name='chq' type='text' size='20' maxlength='30' onkeypress=\"_numero(event);\"/></td>" +
    "<td width='110'>Banco: <span class='marca'>*</span></td><td ><input id='bco' name='bco' type='text' size='20' maxlength='50' onkeypress=\"_letra(event);\" onblur=\"this.value=this.value._trim();\" /></td>" +
    "</tr>"+
    "</table>"+*/

    "<div class='jm_tabla' style=\"background-color:#FFFFFF;\">"+
        "<div id='pCh' class='jm_fila' style=\"display:none;\">"+
            "<div class='jm_columna' style='width:64px'>Banco: <span class='marca'>*</span></div><div class='jm_columna' style='width:120px'><input id='bc' name='bc' type='text' size='15' maxlength='50' onkeypress=\"_letra(event);\" /></div>" +
            "<div class='jm_columna' style='width:60px'>Cheque #: <span class='marca'>*</span></div><div class='jm_columna'><input id='chq' name='chq' type='text' size='10' maxlength='30' onkeypress=\"_numero(event);\"/></div>" +
        "</div>"+

        /*  cuando la forma de pago es transferencia, deposito  */
        "<div class='jm_fila'>"+
            "<div id='pOtr' class='jm_columna' style=\"display:none;\">No. de comprobante de pago: <span class='marca'>*</span> <input id='ncp' name='ncp' type='text' size='15' maxlength='20' "
            + "onfocus=\"_AX_DET=_('cpt0').value;\" onkeydown=\"_NoE(event)\" onkeypress=\"_numero(event);\" onkeyup=\"var c=(_esN)?event.which:event.keyCode;if(c==13){fac_getSaldoDoc(this.value);}_('cpt0').value=_AX_DET + ' Doc. ' + this.value;\" onblur=\"fac_getSaldoDoc(this.value);\"/></div>" +
            "<div id='pTra' class='jm_columna' style='display:none;'> &nbsp; Gastos bancarios: <span class='marca'>*</span> &nbsp;&nbsp; <input id='gb' name='gb' type='text' size='5' onkeypress=\"_evaluar(event, '0123456789.');\" /></div>"+
        "</div>"+ 
        "<div class='jm_fila'>"
        + "<div class='jm_columna' id='axDocs'></div>"
        + "</div>" +

        "<div id='pBan' class='jm_fila' style='display:none;'>"+
            "<div class='jm_columna' style='width:95px'>Cuenta bancaria:<span class='marca'>*</span> &nbsp;</div><div class='jm_columna'>"+ DatosDinamicos.combo(rsBancos, "idPcBc", "", "")+"</div>"+
        "</div>" +
    "</div>"+
    
"</td></tr>";
html += "</table>";

html += "<br />";
html += "<table width='500' border='1' cellpadding='1' cellspacing='0'>";
html += "  <tr>";
html += "    <td>&nbsp;Fecha de emisi&oacute;n: "+Fecha.getFecha("SQL")+"</td>";
html += "    <td>&nbsp;Per&iacute;odo Facturado: &nbsp;"+txt_periodo+"</td>";
html += "  </tr>";
html += "</table>";
html += "<br>";
html += "<span>INFORMACI&Oacute;N DEL CLIENTE </span>";
html += "<table width='500' border='1' cellpadding='1' cellspacing='0'>";
html += "  <tr>";
html += "    <td width='150'>&nbsp;CI/RUC: "+ruc+"</td>";
html += "    <td>&nbsp;Raz&oacute;n social: "+razon_social+"</td>";
html += "  </tr>";
html += "  <tr>";
html += "    <td width='170'>&nbsp;Tel&eacute;fono: "+telefono+"</td>"
        + "  <td>&nbsp;Claro:<input type=\"text\" id=\"movil_claro\" size=\"10\" maxlength=\"10\" value=\""+movil_claro+"\" onkeypress=\"_numero(event);setMovilCliente(event, 'movil_claro');\" /> &nbsp;&nbsp; ";
html += "    Movistar:<input type=\"text\" id=\"movil_movistar\" size=\"10\" maxlength=\"10\" value=\""+movil_movistar+"\" onkeypress=\"_numero(event);setMovilCliente(event, 'movil_movistar');\" /></td>";
html += "  </tr>";
html += "  <tr>";
html += "  	<td colspan='2'>&nbsp;Direcci&oacute;n: "+direccion+"</td>";
html += "  </tr>";
html += "</table>";

html += "<br>";
html += "<span>INFORMACI&Oacute;N DEL PLAN</span>";
html += "<table width='500' border='1' cellpadding='1' cellspacing='0'>";
html += "  <tr>";
html += "    <td>&nbsp;Plan: "+plan+"</td>";
html += "    <td>&nbsp;Sector: " + DatosDinamicos.combo(rsSectores, "id_sector", id_sector, "fac_setPlan()", "", 150) + "</td>";
html += "  </tr>";
html += "  <tr>";
html += "    <td colspan='3'>&nbsp;Direcci&oacute;n de instalaci&oacute;n: "+direccion_instalacion+"</td>";
html += "  </tr>";
html += "</table>";
html += "<br>";

html += "<div style=\"display:none\" id='ax'></div>"
        //+ "<div id='InAr'>DETALLE DE RUBROS <a href='javascript:void(0);' onclick=\"fac_filtrarRubro();\">Insertar Rubro</a></div>";
        + "<div id='InAr'>DETALLE DE RUBROS &nbsp; <input type='text' id='busProd' onfocus=\"this.value=''\" style='width:280px;border-color:red' onkeypress=\"_NoE(event)\" onkeyup=\"fac_getProductos(event, this.value, 'fac_setTablaMaterial', '', 525);\" /> &nbsp;"
        + "<a href='javascript:void(0)' onclick=\"fac_frmAnticipo()\">Registrar anticipo</a>"
        + "<input type='checkbox' id='lector' style='display:none' /></div>";

html += "<DIV style='overflow:auto;width:500px;height:125px;'>"+
            "<table cellpadding='0' cellspacing='0' width='850'><tr>" +
            "<TH class='jm_TH' width='100'>C&Oacute;DIGO</TH>" +
            "<TH class='jm_TH' width='225'>DESCRIPCI&Oacute;N</TH>" +
            "<TH class='jm_TH' width='60'>CANT.</TH>" +
            "<TH class='jm_TH' width='60'>P./U.</TH>" +
            "<TH class='jm_TH' width='70'>SUBTOT.</TH>" +
            "<TH class='jm_TH' width='60'>DESC.</TH>" +
            "<TH class='jm_TH' width='60'>IVA</TH>" +
            "<TH class='jm_TH' width='80'>TOTAL</TH>" +
            "<TH class='jm_TH' width='100'>PRECIO</TH>" +
            "<TH class='jm_TH' width='20'>&nbsp;&nbsp;</TH></tr></table>" +

            "<DIV id='sll0' style='overflow:auto;width:880px;height:90px;'>" +
            "<div class='jm_tabla' id='tblD'>";

html += "<div class='jm_fila' id='r0'>"
        + "<div class='jm_columna caja' style='width:100px;height:38px;' id='cF00'>"+codigo+"</div>" +
            "<div class='jm_columna caja' style='width:225px;height:38px;'><textarea id='cpt0' name='cpt0' style='width:215px;' rows='1'>SERVICIO DE INTERNET PLAN "+plan+" Mbps PERIODO FACTURADO "+txt_periodo+" ~</textarea>"+
            "<input type='hidden' id='idPr0' name='idPr0' value='"+id_producto+"' />"+
            "<input type='hidden' id='stk0' name='stk0' value='"+stk0+"' />"+
            "<input type='hidden' id='p_c0' name='p_c0' value='"+p_c0+"' />"+
            "<input type='hidden' id='p_iva0' value='"+p_iva0+"' />"+
            "<input type='hidden' id='p_v0' value='"+p_v0+"' />"+
            "<input type='hidden' id='p_des0' value='"+p_des0+"' />"+
            "<input type='hidden' id='d_srv0' value='"+d_srv0+"' />"+
            "<input type='hidden' id='ut_min0' value='"+ut_min0+"' />"+
            "<div id='util0' style='display:none'></div></div>"+
            "<div class='jm_columna caja' style='width:60px;height:38px;'><input type='text' onkeypress=\"_evaluar(event,'0123456789.')\" size='5' id='c0' name='c0' value='"+dias_conexion+"' readonly /></div>"+
            "<div class='jm_columna der' style='width:60px;height:38px;'><input type='text' onkeypress=\"_evaluar(event,'0123456789.')\" size='6' id='pu0' name='pu0' style='text-align:right' value='"+p_u_internet+"' readonly /></div>" +
            "<div class='jm_columna der' style='width:70px;height:38px;'><input type='text' size='5' id='st0' name='st0' readonly style='text-align:right' value='"+subt_internet+"' /></div>" +
            "<div class='jm_columna der' style='width:60px;height:38px;'><input type='text' size='5' id='d0' name='d0' value='"+descuento+"' readonly style='text-align:right' /></div>" +
            "<div class='jm_columna der' style='width:60px;height:38px;'><input type='text' size='5' id='v0' name='v0' value='"+iva_internet+"' readonly style='text-align:right' /></div>" +
            "<div class='jm_columna der' style='width:80px;height:38px;'><input type='text' size='8' id='t0' name='t0' style='text-align:right' value='"+valor_internet+"' readonly /></div>"+
            "<div class='jm_columna der' style='width:100px;height:38px;' id='axLiPr0'></div>"+
            "<div class='jm_columna der' style='width:20px;height:38px;'><div id='e0'>&nbsp;</div></div>"
            + "</div>";

html += "</div></div></div>";
/*html += "<table cellpadding='0' cellspacing='0'>" +
            "<TH class='jm_TH' width='190'>DESCRIPCI&Oacute;N</TH>" +
            "<TH class='jm_TH' width='40'>CANT.</TH>" +
            "<TH class='jm_TH' width='60'>P./U.</TH>" +
            "<TH class='jm_TH' width='80'>TOTAL</TH>" +
            "<TH class='jm_TH' width='20'>&nbsp;&nbsp;</TH></tr></table>";

html += "<TABLE class='jm_tabla' cellspacing='1' cellpadding='0'><tbody id='tblD'>";
html += "      <tr id='r0' class='jm_filaPar'>";
html += "        <td width='191' id='axDescr0'>SERVICIO DE INTERNET PLAN "+plan+" Mbps PERIODO FACTURADO "+txt_periodo+"</td>";
//html += "        <td width='41'><input type='text' size='2' id='ct0' name='ct0' value='"+dias_conexion+"' onkeypress=\"_numero(event);\" onkeyup=\"fac_calcTarifa()\" /></td>";
html += "        <td width='41'><input type='text' size='2' id='ct0' name='ct0' value='"+dias_conexion+"' readonly /></td>";
html += "        <td width='61' align='right'>&nbsp;</td>";
html += "        <td width='81' align='right' id='sbtInt'>"+subt_internet+"</td>";
html += "        <td width='21'><input type='hidden' id='des0' name='des0' value='0' />"
        + "<input type='hidden' id='idPr0' name='idPr0' value='"+id_producto+"' />"
        + "<input type='hidden' id='descr0' name='descr0' value=\"SERVICIO DE INTERNET PLAN "+plan+" Mbps PERIODO FACTURADO "+txt_periodo+"\" />"
        + "<input type='hidden' id='pu0' name='pu0' value='"+p_u_internet+"' />"
        + "<input type='hidden' id='sbt0' name='sbt0' value='"+subt_internet+"' />"
        + "<input type='hidden' id='v0' name='v0' value='"+iva_internet+"' />"
        + "<input type='hidden' id='tt0' name='tt0' value='"+valor_internet+"' /></td>";
html += "      </tr>";

        try{
            ResultSet rsRubro = objPreFactura.getPreFacturaRubros(id_instalacion, periodo);
            int i=1;
            String id_productos = "";
            String cantidades = "1";
            String precios_unitarios = "0";
            String subtotales = "0";
            String descuentos = "0";
            String ivas = "0";
            //String stock = "1";
            String totales = "0";
            while(rsRubro.next()){
                id_productos = (rsRubro.getString("id_producto")!=null) ? rsRubro.getString("id_producto") : "";
                cantidades = (rsRubro.getString("cantidad")!=null) ? rsRubro.getString("cantidad") : "1";
                precios_unitarios = (rsRubro.getString("p_u")!=null) ? rsRubro.getString("p_u") : "0";
                subtotales = (rsRubro.getString("p_st")!=null) ? rsRubro.getString("p_st") : "0";
                descuentos = (rsRubro.getString("descuento")!=null) ? rsRubro.getString("descuento") : "0";
                ivas = (rsRubro.getString("iva")!=null) ? rsRubro.getString("iva") : "0";
                //stock = (rsRubro.getString("descripcion")!=null) ? rsRubro.getString("descripcion") : "1";
                totales = (rsRubro.getString("total")!=null) ? rsRubro.getString("total") : "0";
                html += "<tr id='r"+i+"' class='jm_filaPar'>";
                html += "<td width='191' id='cF"+i+"0'>"+((rsRubro.getString("descripcion")!=null) ? rsRubro.getString("descripcion") : "")+"</td>"+
                "<td width='41'><input type='text' onkeypress='_numero(event)' size='2' id='ct"+i+"' name='ct"+i+"' value='"+cantidades+"' onkeyup='fac_calcTotales("+i+")' /></td>"+
                "<td width='61' align='right'><input type='text' size='5' id='pu"+i+"' name='pu"+i+"' readonly style='text-align:right' value='"+precios_unitarios+"' /></td>" +
                "<td width='81' align='right'><input type='text' size='8' id='sbt"+i+"' name='sbt"+i+"' readonly style='text-align:right' value='"+subtotales+"' /></td>" +
                "<td align='center' width='21'><div class='jm_icoCon' style='background-position:-545px -18px;' title='Eliminar' onclick=\"_R('r"+i+"');fac_sumValores();\">&nbsp;</div>"+
                "<input type='hidden' id='idPr"+i+"' name='idPr"+i+"' value='"+id_productos+"' />"+
                //"<input type='hidden' id='stk"+i+"' name='stk"+i+"' value='"+stock+"' />"+
                "<input type='hidden' id='des"+i+"' name='des"+i+"' value='"+descuentos+"' />"+
                "<input type='hidden' id='v"+i+"' name='v"+i+"' value='"+ivas+"' />"+
                "<input type='hidden' id='tt"+i+"' name='tt"+i+"' value='"+totales+"' /></td>";
                html += "</tr>";
                i++;
            }
            rsRubro.close();
        }catch(Exception e){
            e.printStackTrace();
        }
html += "</tbody></table>";*/




html += "<TABLE width='500' class='jm_tabla' cellspacing='1' cellpadding='0'>";
html += "      <tr class='jm_filaPar'>";
html += "        <td rowspan='6'>&nbsp;Concepto: <br /><textarea id='concepto' name='concepto' rows='5' cols='40'>Emisión de la factura por servicios de Internet Nro. "+fac_num_serie+"-"+num_factura+"</textarea></td>";

html += "        <td colspan='2'>&nbsp;Subtotal:</td>";
html += "        <td align='right'><input type='text' style='text-align:right' size='5' id='subt' name='subt' value='"+subtotal+"' onkeypress=\"_evaluar(event,'0123456789.');\" /></td>";
html += "      </tr>";

html += "      <tr class='jm_filaPar'>";
html += "        <td colspan='2'>&nbsp;Subtotal 12%:</td>";
html += "        <td align='right'><input type='text' style='text-align:right' size='5' id='subt2' name='subt2' value='"+subtotal_2+"' onkeypress=\"_evaluar(event,'0123456789.');\" /></td>";
html += "      </tr>";

html += "      <tr class='jm_filaPar'>";
html += "        <td colspan='2'>&nbsp;Subtotal 0%</td>";
html += "        <td align='right'><input type='text' style='text-align:right' size='5' id='subt0' name='subt0' value='0' value='"+subtotal_0+"' onkeypress=\"_evaluar(event,'0123456789.');\" /></td>";
html += "      </tr>";

html += "      <tr class='jm_filaPar'>";
html += "        <td>&nbsp;Descuento</td>";
html += "        <td><input type='text' size='2' id='diaDes' name='diaDes' value='0' onkeypress=\"_numero(event);\" onkeyup=\"fac_calcTarifaDesc()\" /></td>";
html += "        <td align='right'><input type='text' style='text-align:right' size='5' id='dt' name='dt' value='"+descuento+"' onkeypress=\"_evaluar(event,'0123456789.');\" /></td>";
html += "      </tr>";

html += "      <tr class='jm_filaPar'>";
html += "        <td colspan='2'>&nbsp;I.V.A. 12%</td>";
html += "        <td align='right'><input type='text' style='text-align:right' size='5' id='iva2' name='iva2' value='"+iva_2+"' readonly /></td>";
html += "      </tr>";
if(Double.valueOf(meses_impagos)>0){
    html += "      <tr class='jm_filaPar'>";
    html += "        <td colspan='2'>&nbsp;Meses impagos </td>";
    html += "        <td align='right' id='meIm'><input type='text' style='text-align:right' size='5' id='mip' name='mip' value='"+meses_impagos+"' onkeypress=\"_evaluar(event,'0123456789.');\" /></td>";
    html += "      </tr>";
}    
html += "      <tr class='jm_filaPar'>";
html += "  	<td colspan='2'>TOTAL A PAGAR: </td>";
html += "  	<td align='right'><input type='text' size='5' style='text-align:right' id='tt' name='tt' value='"+total_pagar+"' readonly /></td>";
html += "  </tr>";
html += "</table>";

html += "</div>";



/*  COMPROBANTE DE RETENCION  */



String bancos[][] = DatosDinamicos.ResultSetToMatriz(rsBancos);
int filas = bancos.length+1;
String caja_bancos[][] = new String[filas][2];
caja_bancos[0][0] = "";
caja_bancos[0][1] = "";
if(id_plan_cuenta_caja.compareTo("")!=0){
    caja_bancos[0][0] = id_plan_cuenta_caja;
    caja_bancos[0][1] = "Caja";
}
for(int x=0; x<bancos.length; x++){
    caja_bancos[x+1][0] = bancos[x][0];
    caja_bancos[x+1][1] = bancos[x][1];
}

int mes_actual = Fecha.getMes();
String ret_ejercicio_fiscal_mes = mes_actual<10 ? "0" + mes_actual : String.valueOf(mes_actual);
String [][] ret_ejercicio_fiscal_mes1 = {{"01","Enero"},{"02","Febrero"},{"03","Marzo"},{"04","Abril"},{"05","Mayo"},{"06","Junio"},
                {"07","Julio"},{"08","Agosto"},{"09","Septiembre"},{"10","Octubre"},{"11","Noviembre"},{"12","Diciembre"}};

int i=0;
//if(idFactura.compareTo("-1")!=0 && (ret_num_retencion.compareTo("")==0 && objRol.getAcceso(id_rol, "compRetVentaNueva")) || (ret_num_retencion.compareTo("")!=0 && objRol.getAcceso(id_rol, "compRetVentaEditar"))){

    html += "<div id='tab11' class='jm_tabHTML' style='display:none;width:530px;height:"+_altBody+"px;'>";

    ResultSet rsBasesImponibles = objBaseImponible.getBasesImponibles();
    ResultSet rsTablaImpuestos = objTablaImpuesto.getTablaImpuestosVigente();

    html += "<table width='740' cellpadding='0' cellspacing='0'>" +
            "<tr><td width='300'></td><td colspan='2'><span id='axMsR'></span></td></tr>"+
            "<tr><td align='center' colspan='2' nowrap> C O M P R O B A N T E &nbsp;&nbsp; D E &nbsp;&nbsp; R E T E N C I &Oacute; N</td>" +
            "<td align='right' nowrap> Nro. <input type='text' id='sr' name='sr' size='6' maxlength='7' value='001-001' onkeypress=\"_evaluar(event, '0123456789-');\" />-" +
            "<input type='text' size='6' id='nr' name='nr' value='' onkeypress=\"_numero(event);\" /></td></tr>" +

            "<tr><td>Fecha de Emisi&oacute;n: <input id='fer' name='fer' type='text' size='9' value='"+Fecha.getFecha("SQL")+"' onkeypress=\"_SUP(event);\" />" +
            "<input type='button' value='...' onClick=\"SelectorFecha.crear('fer', 'SQL');\" /></td>" +
            "<td>Ejercicio Fiscal: " + DatosDinamicos.combo("efm", ret_ejercicio_fiscal_mes, ret_ejercicio_fiscal_mes1, "") + " <input id='ef' name='ef' type='text'size='4' maxlength='4' value='"+Fecha.getAnio()+"' onkeypress=\"_numero(event)\" /></td>" +
            "<td align='right' nowrap>Autorizaci&oacute;n: <input type='text' size='10' maxlength='39' id='aur' name='aur' value='' onkeypress=\"_numero(event);\" /></td></tr>" +

            //"<tr><td colspan='3'>Aplicaci&oacute;n contable: "+ DatosDinamicos.combo("idPcCajBans", id_plan_cuenta_caja_banco, caja_bancos, "")+"</td></tr>"+

            "</table>" +

            "<div id='InRe'><a href='javascript:void(0);' style='float:left;' onclick=\"inv_addRetencion(1);\">Insertar Retenci&oacute;n</a></div>" +
            "&nbsp;&nbsp;<div style=\"display:none\" id='axBI'>"+ DatosDinamicos.combo(rsBasesImponibles, "9a0", "9b0", "", "inv_sumRetSubTotales(9d0,1);inv_sumRetTotales();", "inv_sumRetSubTotales(9d0,1);inv_sumRetTotales();", "", 165) +"</div>"
            + "<div style=\"display:none\" id='axCR'>"+ DatosDinamicos.combo(rsTablaImpuestos, "9a0", "9b0", "inv_setMsg('axMsR');", "inv_setConcepto(9c0);inv_sumRetSubTotales(9d0,1);inv_sumRetTotales();", "_('9e0').focus();", "", 285) +"</div>" +
            "<div style=\"display:none\" id='axC'>"+ objTablaImpuesto.getConceptoRetencion() +"</div>" +
            "<table width='680' cellpadding='0' cellspacing='0'>" +
            "<TH class='jm_TH' width='170'>CAMPO BASE IMPONIBLE</TH>" +
            "<TH class='jm_TH' width='290'>IMPUESTO - C&Oacute;DIGO - % DE RETENCI&Oacute;N</TH>" +
            "<TH class='jm_TH' width='100'>BASE IMPONIBLE</TH>" +
            "<TH class='jm_TH' width='100'>VALOR RETENIDO</TH>" +
            "<TH class='jm_TH' width='20'>&nbsp;&nbsp;</TH></tr></table>" +

            "<DIV id='sll1' style='overflow:auto;width:760px;height:155px;'>" +
            "<TABLE class='jm_tabla' cellspacing='1' cellpadding='0' id='tblR'>";


    //out.print(html);


    html += "</table></div><table cellpadding='0' cellspacing='0'>" +
            "<tr><td colspan='2' width='340' align='right'>TOTAL RETENCI&Oacute;N: </td>" +
            "<td align='right'> &nbsp;<input type='text' id='tvr' name='tvr' value='' style='text-align:right' onkeypress=\"_evaluar(event, '0123456789.');\" /></td>" +
            "</tr></table>";

    try{
        rsTablaImpuestos.close();
    }catch(Exception e){
        e.printStackTrace();
    }

    html += "</div>";


    
    
    
    
    
    /*   abonos   */
    
    
    
    
    html += "<div id='tab12' class='jm_tabHTML' style='display:none;width:530px;height:"+_altBody+"px;'>";
    
    html += "<p><a href='javascript:void(0)' onclick='fac_ventaEditar(-1, "+idPreFactura+")' >Registrar abono</a></p>";

    try{
        html += "<table width='100%' cellpadding='3' cellspacing='0' border='1'>";
        html += "<tr>";
        html += "<th>USUARIO</th>";
        html += "<th>NUMERO</th>";
        html += "<th>FECHA</th>";
        html += "<th>SUBTOTAL</th>";
        html += "<th>IVA</th>";
        html += "<th>TOTAL</th>";
        html += "</tr>";
        while(rsAbonos.next()){
            String id_factura_venta = rsAbonos.getString("id_factura_venta")!=null ? rsAbonos.getString("id_factura_venta") : "";
            html += "<tr style='cursor:pointer' onclick='fac_ventaEditar("+id_factura_venta+", "+idPreFactura+")' >";
            html += "<td>"+(rsAbonos.getString("usuario")!=null ? rsAbonos.getString("usuario") : "")+"</td>";
            html += "<td>"+(rsAbonos.getString("numero_factura")!=null ? rsAbonos.getString("numero_factura") : "")+"</td>";
            html += "<td>"+(rsAbonos.getString("fecha_abono")!=null ? rsAbonos.getString("fecha_abono") : "")+"</td>";
            html += "<td align='right'>"+(rsAbonos.getString("subtotal")!=null ? rsAbonos.getString("subtotal") : "")+"</td>";
            html += "<td align='right'>"+(rsAbonos.getString("iva")!=null ? rsAbonos.getString("iva") : "")+"</td>";
            html += "<td align='right'>"+(rsAbonos.getString("total")!=null ? rsAbonos.getString("total") : "")+"</td>";
            html += "</tr>";
        }
        html += "</table>";
        
    }catch(Exception e) {
        e.printStackTrace();
    } 

    html += "</div>";

    
    
    
    
    
    
    

html += "<div>";

Instalacion objInstalacion = new Instalacion(this._ip, this._puerto, this._db, usuario, clave);
String ultimo_periodo = objPreFactura.getUltimoPeriodo();
int anio = Fecha.datePart("anio", ultimo_periodo);
int mes = Fecha.datePart("mes", ultimo_periodo);
String fecha_termino = anio + "-" + mes + "-" + Fecha.getUltimoDiaMes(anio, mes);
if(!objInstalacion.haySuspencion(id_instalacion, ultimo_periodo, fecha_termino) && ultimo_periodo.compareTo(periodo)!=0 ){   /*  periodo_suspension es la fecha de inicio   */
    html += "<input type='button' style='float:left' id='btnGenSus' value='Generar suspenci&oacute;n para: "+Fecha.getTxtPeriodo(ultimo_periodo)+"' onclick=\"fac_preFactSuspensionGuardar('"+ultimo_periodo+"')\" /> &nbsp; ";
}
if(idPreFactura.compareTo("-1")!=0 && (objRol.getAcceso(id_rol, "prefacturaEditar") || objRol.getAcceso(id_rol, "prefacturaEmitir")) ){
    //html += "<td><input type='submit' value='Guardar Prefactura' onclick=\"_('emitir').value=0;\" /></td>";
    if(objPreFactura.activarEmision(id_instalacion, periodo) && objRol.getAcceso(id_rol, "prefacturaEmitir")){
        /*if(estado_servicio.compareTo("r")==0){ 
            html += "<span class='d_notfMsg'>Contin&uacute;a con  el servicio</span><input type='checkbox' id='continua_servicio' name='continua_servicio' />";
        } */      
        html += " &nbsp; <input style='float:right' type='submit' id='btnPreEn' value='Guardar y Emitir Factura' onclick=\"_('emitir').value=1;\" />";  //   "+activado+"
    }
}
objInstalacion.cerrar();
        
html += "</div>";

html += "</div></form></div>";


out.print(html);

        } finally {
            objBaseImponible.cerrar();
            objTablaImpuesto.cerrar();
            objPreFactura.cerrar();
            objFactura.cerrar();
            objRol.cerrar();
            objSector.cerrar();
            out.close();
        }
    }

    // <editor-fold defaultstate='collapsed' desc='HttpServlet methods. Click on the + sign on the left to edit the code.'>
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
