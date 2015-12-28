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
import jm.web.DatosDinamicos;
import jm.web.Fecha;
import jm.adm.clas.Configuracion;
import jm.seg.clas.Rol;
import jm.cnt.clas.PlanCuenta;
import jm.cnt.clas.ComprobanteIngreso;
import jm.fac.clas.FacturaVenta;
import java.sql.ResultSet;
import jm.adm.clas.PuntoEmision;
import jm.adm.clas.TablaImpuesto;
import jm.cnt.clas.Banco;
import jm.inv.clas.BaseImponible;

/**
 *
 * @author Jorge
 */
public class frmComprobanteIngresoCobro extends HttpServlet {
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

        String idFP = request.getParameter("id");
        String id_cliente = request.getParameter("idC");
        int _altBody = 380;
        String id = "-1";

        Configuracion conf = new Configuracion(this._ip, this._puerto, this._db, usuario, clave);
        //String ciudad = conf.getValor("ciudad");
        String conf_banco = conf.getValor("banco");
        String gastos_bancos1 = conf.getValor("gastos_bancos");
        conf.cerrar();


        //String id_plan_cuenta_caja = "";
        PuntoEmision objPuntoEmision = new PuntoEmision(this._ip, this._puerto, this._db, usuario, clave);
        String caja = objPuntoEmision.getIdCaja(id_punto_emision);
        /*try{
            ResultSet rs = objPuntoEmision.getPuntoEmision(String.valueOf(id_punto_emision));
            if(rs.next()){
                id_plan_cuenta_caja = (rs.getString("id_plan_cuenta_caja")!=null) ? rs.getString("id_plan_cuenta_caja") : "";
                rs.close();
            }
        }catch(Exception e){
            e.printStackTrace();
        }*/
        objPuntoEmision.cerrar();

        PlanCuenta objPlanCuenta = new PlanCuenta(this._ip, this._puerto, this._db, usuario, clave);
        ResultSet cuentas = objPlanCuenta.getTablasAsignacion();
        ResultSet bancos = objPlanCuenta.getTablasCajaBancosNotas(4);
        objPlanCuenta.cerrar();

        BaseImponible objBaseImponible = new BaseImponible(this._ip, this._puerto, this._db, usuario, clave);
        
        TablaImpuesto objTablaImpuesto = new TablaImpuesto(this._ip, this._puerto, this._db, usuario, clave);
        
        Rol objRol = new Rol(this._ip, this._puerto, this._db, usuario, clave);

        Banco objBanco = new Banco(this._ip, this._puerto, this._db, usuario, clave);
        ResultSet rsBancos = objBanco.getBancosIngresos();

        ComprobanteIngreso objComprobanteIngreso = new ComprobanteIngreso(this._ip, this._puerto, this._db, usuario, clave);

        FacturaVenta objFacturaVenta = new FacturaVenta(this._ip, this._puerto, this._db, usuario, clave);
        ResultSet rsFactura = objFacturaVenta.getNumsFacturasPendientes(id_cliente);

        try {
            String fecha = Fecha.getFecha("SQL");
            String num_compIngr = "";
            String num_cheque = "";
            String fecha_actual = fecha;
            String fecha_proceso = fecha;
            //String ci_ruc = "";
            String orden_de = "";
            String tipo_cobro = "e";
            //String cuenta = conf_cuenta;
            String banco = conf_banco;
            String num_comp_pago = "";
            String gastos_bancos = "0";
            String total = "0";
            String son = "";
            String detalle = "";
            //String lugar = ciudad;
            String fecha_en_efectivo = "";
            boolean anulado = false;
            String id_documento = "";

            String id_retencion_venta = "-1";
            String ret_num_serie = "001-001";
            String ret_autorizacion = "";
            String ret_num_retencion = "";
            String ret_fecha_emision = Fecha.getFecha("SQL");
            String ret_ejercicio_fiscal = String.valueOf(Fecha.getAnio());
            int mes = Fecha.getMes();
            String ret_ejercicio_fiscal_mes = mes<10 ? "0" + mes : String.valueOf(mes);
            String ret_impuesto_retenido = "";
            String id_factura_venta_cobro = "";
            boolean ret_anulado = false;

            String num_factura = "";
            String forma_pago = "";


            out.print("obj»vta_nc_html^foc»nb"+(idFP.compareTo("-1")==0?"^fun»_objTab.seleccionar(0);fac_setDocumentoCobro()":"")+";^frm»");

            out.print("<form action='frmComprobanteIngresoCobroGuardar' onsubmit='return fac_cobroGuardar(this)' autocomplete='off'>");
            
            out.print("<div><div class='grupoTab' style='width:780px;'><div id='tabR0'>"
                    + "<a id='tab00' href='javascript:void(0);' class='jm_tabAct' onclick='_objTab.seleccionar(0);'>Comprobante</a>");

            //if(idFactura.compareTo("-1")!=0 && (ret_num_retencion.compareTo("")==0 && objRol.getAcceso(id_rol, "compRetVentaNueva")) || (ret_num_retencion.compareTo("")!=0 && objRol.getAcceso(id_rol, "compRetVentaEditar"))){
            if(tipo_cobro.compareTo("d")!=0){
                out.print("<a id='tab01' href='javascript:void(0);' class='jm_tab' onclick='_objTab.seleccionar(1);'>Retenci&oacute;n</a>");
            }
            out.print("</div>");


            out.print("<div id='tabR1'>");

            /* primer tab */


            out.print("<div id='tab10' class='jm_tabHTML' style='display:block;width:770px;height:"+_altBody+"px;'>");


            
            if(idFP.compareTo("-1")!=0){
                try{
                    String id_factura_venta = "";

                    ResultSet rsPago = objFacturaVenta.getCobro(idFP);
                    if(rsPago.next()){
                        id = (rsPago.getString("id_comprobante_ingreso")!=null) ? rsPago.getString("id_comprobante_ingreso") : "-1";
                        id_documento = (rsPago.getString("id_comprobante_ingreso")!=null) ? rsPago.getString("id_comprobante_ingreso") : "";
                        id_factura_venta = (rsPago.getString("id_factura_venta")!=null) ? rsPago.getString("id_factura_venta") : "";
                        rsPago.close();
                    }

                    ResultSet rsFacturaVenta = objFacturaVenta.getFactura(id_factura_venta);
                    if(rsFacturaVenta.next()){
                        forma_pago = rsFacturaVenta.getString("forma_pago")!=null ? rsFacturaVenta.getString("forma_pago") : "";
                        num_factura = (rsFacturaVenta.getString("serie_factura")!=null ? rsFacturaVenta.getString("serie_factura") : "") +
                        "-" + (rsFacturaVenta.getString("num_factura")!=null ? rsFacturaVenta.getString("num_factura") : "");
                        rsFacturaVenta.close();
                    }

                    try{
                        ResultSet rsRetencion = objFacturaVenta.getRetencion(id_factura_venta);
                        if(rsRetencion.next()){
                            //ret_anulado = (rsRetencion.getString("anulado")!=null) ? rsRetencion.getBoolean("anulado") : false;
                            //if(!ret_anulado){
                                id_retencion_venta = (rsRetencion.getString("id_retencion_venta")!=null) ? rsRetencion.getString("id_retencion_venta") : "-1";
                                ret_num_serie = (rsRetencion.getString("ret_num_serie")!=null) ? rsRetencion.getString("ret_num_serie") : "001-001";
                                ret_autorizacion = (rsRetencion.getString("ret_autorizacion")!=null) ? rsRetencion.getString("ret_autorizacion") : "";
                                ret_num_retencion = (rsRetencion.getString("ret_num_retencion")!=null) ? rsRetencion.getString("ret_num_retencion") : "";
                                ret_fecha_emision = (rsRetencion.getString("ret_fecha_emision")!=null) ? Fecha.ISOaSQL(rsRetencion.getString("ret_fecha_emision")) : Fecha.getFecha("SQL");
                                ret_ejercicio_fiscal_mes = (rsRetencion.getString("ret_ejercicio_fiscal_mes")!=null) ? rsRetencion.getString("ret_ejercicio_fiscal_mes") : ret_ejercicio_fiscal_mes;
                                ret_ejercicio_fiscal = (rsRetencion.getString("ret_ejercicio_fiscal")!=null) ? rsRetencion.getString("ret_ejercicio_fiscal") : ret_ejercicio_fiscal;
                                ret_impuesto_retenido = (rsRetencion.getString("ret_impuesto_retenido")!=null) ? rsRetencion.getString("ret_impuesto_retenido") : "";
                                id_factura_venta_cobro = (rsRetencion.getString("id_factura_venta_cobro")!=null) ? rsRetencion.getString("id_factura_venta_cobro") : "";
                            //}
                            rsRetencion.close();
                        }
                    }catch(Exception e){
                        e.printStackTrace();
                    }

                    ResultSet compIngr = objComprobanteIngreso.getComprobanteIngreso(id);
                    if(compIngr.next()){
                        num_compIngr = (compIngr.getString("num_comprobante")!=null) ? compIngr.getString("num_comprobante") : "";
                        fecha_actual = (compIngr.getString("fecha_actual")!=null) ? Fecha.ISOaSQL(compIngr.getString("fecha_actual")) : fecha;
                        fecha_proceso = (compIngr.getString("fecha_proceso")!=null) ? Fecha.ISOaSQL(compIngr.getString("fecha_proceso")) : fecha;
                        fecha_en_efectivo = (compIngr.getString("fecha_en_efectivo")!=null) ? Fecha.ISOaSQL(compIngr.getString("fecha_en_efectivo")) : "";
                        orden_de = (compIngr.getString("nombre")!=null) ? compIngr.getString("nombre") : "";
                        tipo_cobro = (compIngr.getString("tipo_cobro")!=null) ? compIngr.getString("tipo_cobro") : "e";
                        num_cheque = (compIngr.getString("num_cheque")!=null) ? compIngr.getString("num_cheque") : "";
                        banco = (compIngr.getString("banco")!=null) ? compIngr.getString("banco") : "";
                        num_comp_pago = (compIngr.getString("num_comp_pago")!=null) ? compIngr.getString("num_comp_pago") : "";
                        gastos_bancos = (compIngr.getString("gastos_bancos")!=null) ? compIngr.getString("gastos_bancos") : "";
                        total = (compIngr.getString("total")!=null) ? compIngr.getString("total") : "0";
                        son = (compIngr.getString("son")!=null) ? compIngr.getString("son") : "";
                        detalle = (compIngr.getString("detalle")!=null) ? compIngr.getString("detalle") : "";
                        anulado = (compIngr.getString("anulado")!=null) ? compIngr.getBoolean("anulado") : false;
                        compIngr.close();
                    }


                    try{
                        out.println("<table cellpadding='0' cellspacing='0'>" +
                            "<TH class='jm_TH' width='110'># FACTURA</TH>" +
                            "<TH class='jm_TH' width='80'>F. COMPRA</TH>" +
                            "<TH class='jm_TH' width='90'>CxC</TH>" +
                            "<TH class='jm_TH' width='90'>DEUDA</TH>" +
                            "<TH class='jm_TH' width='90'>PAGO</TH>" +
                            "<TH class='jm_TH' width='90'>SALDO</TH></tr></table>" +

                            "<DIV style='overflow:auto;width:570px;height:85px;'>" +
                            "<TABLE class='jm_tabla' cellspacing='1' cellpadding='0'>");
                        ResultSet rsPagos = objFacturaVenta.getPagos(id_documento);
                        int i=0;
                        float deuda=0;
                        float abono=0;
                        double deuda_ant = 0;
                        while(rsPagos.next()){
                            deuda = (rsPagos.getString("deuda")!=null) ? rsPagos.getFloat("deuda") : 0;
                            abono = (rsPagos.getString("abono")!=null) ? rsPagos.getFloat("abono") : 0;
                            deuda_ant =  Math.round((deuda+abono) * Math.pow(10, 2)) / Math.pow(10, 2);
                            out.println("<tr class='jm_filaPar'>");
                            out.println("<td width='110'><input type='hidden' id='idF"+i+"' value='" + ((rsPagos.getString("id_factura_venta")!=null) ? rsPagos.getString("id_factura_venta") : "") + "' />" +
                                    "<input type='hidden' id='idFP"+i+"' value='" + ((rsPagos.getString("id_factura_venta_cobro")!=null) ? rsPagos.getString("id_factura_venta_cobro") : "") + "' />" +
                                    ((rsPagos.getString("numero_factura")!=null) ? rsPagos.getString("numero_factura") : "") + "</td>");
                            out.println("<td width='81'>" + ((rsPagos.getString("fecha_emision")!=null) ? Fecha.ISOaSQL(rsPagos.getString("fecha_emision")) : "") + "</td>");
                            out.println("<td width='91' align='right'>" + ((rsPagos.getString("cxc")!=null) ? rsPagos.getString("cxc") : "") + "</td>");
                            out.println("<td width='91' align='right'><input id='de"+i+"' type='text' class='inpTbl' size='9' readonly value='"+deuda_ant+"' /></td>");
                            out.println("<td width='91' align='right'><input id='ab"+i+"' type='text' class='inpTbl' size='9' onkeypress=\"_evaluar(event, '0123456789.');\" onkeyup=\"inv_sumTotalPagoEd();\" " +
                                    "onfocus=\"this.select();\" value='"+abono+"' /></td>");
                            out.println("<td width='91' align='right'><input id='sa"+i+"' type='text' class='inpTbl' size='9' readonly value='"+deuda+"' /></td>");
                            out.println("</tr>");
                            //total += deuda;
                            i++;
                        }
                        out.println("</table></div><hr>");
                    }catch(Exception e){
                        out.println("</table></div><hr>");
                        e.printStackTrace();
                    }
                }catch(Exception e){
                    e.printStackTrace();
                }
            }

            String cobro_cheque = (tipo_cobro.compareTo("c")==0) ? "block" : "none";
            String cobro_dep_tran = (tipo_cobro.compareTo("p")==0 || tipo_cobro.compareTo("t")==0 || tipo_cobro.compareTo("j")==0 || tipo_cobro.compareTo("n")==0) ? "block" : "none";
            String cobro_trans = (tipo_cobro.compareTo("t")==0 || tipo_cobro.compareTo("j")==0 || tipo_cobro.compareTo("n")==0) ? "block" : "none";

            String [][] forma_pago1 = {{"e","Efectivo"},{"c","Cheque"},{"p","Dep&oacute;sito bancario"},{"t","Transferencia bancaria"},{"j","Tarjeta de cr&eacute;dito"},{"n","Convenio de d&eacute;bito"}};



            out.println("<input type='hidden' id='fe' name='fe' value='"+fecha_en_efectivo+"' />"+
                    "<input type='hidden' id='id' name='id' value='"+id+"' />" +
                    "<input type='hidden' id='id_cli' name='id_cli' value='"+id_cliente+"' />" +
                    "<input type='hidden' id='caj' value='"+caja+"' />" +
                    "<input type='hidden' id='gas_ban' name='gas_ban' value='"+gastos_bancos1+"' />" +
                    "<input type='hidden' id='imp' name='imp' value='"+gastos_bancos1+"' />" +
                    "<input type='hidden' id='axtp' name='axtp' value='0' />" +
                    "<input type='hidden' id='toPa' name='toPa' value='"+total+"' />" +
                    "<input type='hidden' id='idFs' name='idFs' value='' />" +
                    "<input type='hidden' id='abs' name='abs' value='' />" +
                    "<input type='hidden' id='doc' name='doc' value='' />" +
                    "<input type='hidden' id='pcs' name='pcs' value='' />" +
                    "<input type='hidden' id='ds' name='ds' value='' />" +
                    "<input type='hidden' id='hs' name='hs' value='' />" +
                    "<input type='hidden' id='saldo_doc' name='saldo_doc' value='0' />"+ 
                    "<input type='hidden' id='vs' name='vs' value='"+total+"' />");
           out.println("<table width='750'>" +
                    "<tr><td align='center'><strong>C O M P R O B A N T E &nbsp;&nbsp; D E &nbsp;&nbsp; I N G R E S O</strong></td>" +
                    "<td align='right' width='100'>Nro. "+num_compIngr+"</td></tr>" +
                    "</table>" +

                    "<table width='100%'>" +

                    "<tr><td>Nombre: <span class='marca'>*</span> &nbsp;&nbsp; <input id='nb' name='nb' value='"+orden_de+"' type='text' size='60' maxlength='80' onblur=\"this.value=this.value._trim();\" /></td>" +
                    "<td colspan='2'>Fecha: &nbsp;&nbsp;&nbsp;<input id='fp' name='fp' type='text'size='10' value='"+fecha_proceso+"' readOnly />"
                    + "<input type='button' value='...' onClick=\"SelectorFecha.crear('fp', 'SQL');\" /></td></tr>" +

                    "<tr><td colspan='3'>Concepto: <span class='marca'>*</span> <input id='dt' name='dt' value='"+detalle+"' type='text' size='100' maxlength='200' onblur=\"this.value=this.value._trim();\" /></td></tr>" +

                    //"<tr><td colspan='2'>Forma de pago: " + DatosDinamicos.combo("foPa", tipo_cobro, forma_pago1, "cnt_setFormaPago(this);cnt_setBanco(1);fac_setDetalleCobro();") + "</td></tr>");
                    "<tr><td colspan='2'>Forma de pago: " + DatosDinamicos.combo("foPa", tipo_cobro, forma_pago1, "cnt_setFormaPago(this);fac_setDetalleCobro();") + "</td></tr>");

                    out.println("</table>" +


                    /*  cuando la forma de pago es cheque  */
                    "<table width='100%' id='pCh' style=\"display:"+cobro_cheque+";\" >" +
                    "<tr><td width='250'>Banco: <span class='marca'>*</span> <input id='bc' name='bc' type='text' size='20' maxlength='50' value='"+banco+"' onkeypress=\"_letra(event);\" /></td>" +
                    "<td width='250'>Cheque #: <span class='marca'>*</span> <input id='ch' name='ch' value='"+num_cheque+"' type='text' size='20' maxlength='30' onkeypress=\"_numero(event);\"/></td></tr>"+
                    "</table>" +
                    /*  cuando la forma de pago es transferencia, deposito  */
                    "<table id='pOtr' width='100%' style=\"display:"+cobro_dep_tran+";\" >" +
                    "<tr><td style='white-space:nowrap'>No. de comprobante de pago: <span class='marca'>*</span> <input id='ncp' name='ncp' type='text' size='20' maxlength='20' value='"+num_comp_pago+"' "
                            + "onkeydown=\"_NoE()\" onkeypress=\"_numero(event);\" onkeyup=\"var c=(_esN)?event.which:event.keyCode;if(c==13){fac_getSaldoDoc(this.value);}\" onblur=\"fac_getSaldoDoc(this.value);\" /></td>" +
                    "<td id='pTra' style='white-space:nowrap;display:"+cobro_trans+";'>Gastos bancarios: <span class='marca'>*</span> <input id='gb' name='gb' type='text' size='20' value='"+gastos_bancos+"' onkeypress=\"_evaluar(event, '0123456789.');\" onkeyup=\"_('d1').value=this.value;fac_setGastosBancos();cnt_sumTotales();cnt_setSon('td');\" /></td></tr>" +
                    "</table>" +

                    /*"<div><a href='javascript:void(0);' style='float:left;' onclick=\"con_addCuenta();con_setSon('th');\">Insertar Cuenta</a> " +*/
                    "<div>" +
                    "<span style='float:right;' class='marca' id='di'></span></div>" +
                    "&nbsp;&nbsp;<div style=\"display:none\" id='ax'>"+ DatosDinamicos.combo(cuentas, "_Xx_", "", "", 535)+"</div>" +
                    "&nbsp;&nbsp;<div style=\"display:none\" id='axb'>"+ DatosDinamicos.combo(rsBancos, "_Xx_", "", "", 535)+"</div>" +
                    "<table cellpadding='0' cellspacing='0'>" +
                    "<TH class='jm_TH' width='550'>CODIGO - CUENTA</TH>" +
                    "<TH class='jm_TH' width='90'>DEBE</TH>" +
                    "<TH class='jm_TH' width='90'>HABER</TH>" +
                    "<TH class='jm_TH' width='20'>&nbsp;&nbsp;</TH></tr></table>" +

                    "<DIV style='overflow:auto;width:760px;height:125px;'>" +
                    "<TABLE class='jm_tabla' cellspacing='1' cellpadding='0' id='tblD'>");

            try{
                String id_plan_cuenta = "";
                String debe = "";
                String haber = "";
                int i=0;
                ResultSet compIngrDetalle = objComprobanteIngreso.getComprobanteIngresoDetalle(id);
                while(compIngrDetalle.next()){
                    id_plan_cuenta = (compIngrDetalle.getString("id_plan_cuenta")!=null) ? compIngrDetalle.getString("id_plan_cuenta") : "1";
                    debe = (compIngrDetalle.getString("debe")!=null) ? ((compIngrDetalle.getDouble("debe")>0)?compIngrDetalle.getString("debe"):"") : "";
                    haber = (compIngrDetalle.getString("haber")!=null) ? ((compIngrDetalle.getDouble("haber")>0)?compIngrDetalle.getString("haber"):"") : "";
                    out.println("<tr id='r"+i+"' class='jm_filaPar'>");
                    out.println("<td width='550'>" + DatosDinamicos.combo(cuentas, "PC"+i, id_plan_cuenta, "", 535, "disabled='disabled'") + "</td>");
                    out.println("<td width='90' align='right'><input id='d"+i+"' name='d"+i+"' type='text' class='inpTbl' size='9' onkeypress=\"_evaluar(event, '0123456789.');\" onkeyup=\"con_sumTotales();con_setSon('th');\" " +
                            "onfocus=\"this.select();\" value='"+debe+"' radonly /></td>");
                    out.println("<td width='90' align='right'><input id='h"+i+"' name='h"+i+"' type='text' class='inpTbl' size='9' onkeypress=\"_evaluar(event, '0123456789.');\" onkeyup=\"con_sumTotales();con_setSon('th');\" " +
                            "onfocus=\"this.select();\" value='"+haber+"' readonly /></td>");

                    out.println("<td width='20' align='center'>&nbsp;</td>");
                    /*out.println("<td width='20' align='center'><div class='jm_icoCon' style='background-position:-17px -210px;float:right;' alt='Eliminar' title='Eliminar' onclick=\"_R('r"+i+"');con_sumTotales();con_setSon('th');\">&nbsp;</div></td>");*/

                    out.println("</tr>");
                    i++;
                }

                compIngrDetalle.close();
                cuentas.close();
                bancos.close();
            }catch(Exception e){
                e.printStackTrace();
            }

            out.println("</table></div>" +
                    "<table cellpadding='0' cellspacing='0'>" +
                    "<tr><td align='right' width='550' class='marca'>Son: <input id='sn' name='sn' Style='width:450px;' value='"+son+"' type='text' maxlength='200' /> &nbsp;" +
                    "TOTALES: &nbsp;</td>" +
                    "<td id='td' align='right' width='90' class='marca'> "+total+" </td>" +
                    "<td id='th' align='right' width='90' class='marca'> "+total+" </td>" +
                    "<td width='20'>&nbsp;&nbsp;</td></tr></table></div>");


            
            

            /*  COMPROBANTE DE RETENCION  */



            
            
            
            String [][] ret_ejercicio_fiscal_mes1 = {{"01","Enero"},{"02","Febrero"},{"03","Marzo"},{"04","Abril"},{"05","Mayo"},{"06","Junio"},
                {"07","Julio"},{"08","Agosto"},{"09","Septiembre"},{"10","Octubre"},{"11","Noviembre"},{"12","Diciembre"}};
            
            int i=0;
            //if(idFactura.compareTo("-1")!=0 && (ret_num_retencion.compareTo("")==0 && objRol.getAcceso(id_rol, "compRetVentaNueva")) || (ret_num_retencion.compareTo("")!=0 && objRol.getAcceso(id_rol, "compRetVentaEditar"))){

                out.println("<div id='tab11' class='jm_tabHTML' style='display:none;width:770px;height:"+_altBody+"px;'>");

                ResultSet rsBasesImponibles = objBaseImponible.getBasesImponibles();
                ResultSet rsTablaImpuestos = objTablaImpuesto.getTablaImpuestosVigente();
                ResultSet rsTablaImpuestosPlanCuentasVentas = objTablaImpuesto.getTablaImpuestosVigentePlanCuantasVentas();

                out.println("<table width='740' cellpadding='0' cellspacing='0'>" +
                        "<tr><td width='300'></td><td colspan='2'><span id='axMsR'></span></td></tr>"+
                        "<tr><td align='center' colspan='2'> C O M P R O B A N T E &nbsp;&nbsp; D E &nbsp;&nbsp; R E T E N C I &Oacute; N</td>" +
                        "<td align='right'> Nro. <input type='text' id='sr' name='sr' size='6' maxlength='7' value='"+ret_num_serie+"' onkeypress=\"_evaluar(event, '0123456789-');\" />-" +
                        "<input type='text' size='6' id='nr' name='nr' value='"+ret_num_retencion+"' onkeypress=\"_numero(event);\" /></td></tr>" +

                        "<tr><td>Fecha de Emisi&oacute;n: <input id='fer' name='fer' type='text' size='9' value='"+ret_fecha_emision+"' onkeypress=\"_SUP(event);\" />" +
                        "<input type='button' value='...' onClick=\"SelectorFecha.crear('fer', 'SQL');\" /></td>" +
                        "<td>Ejercicio Fiscal: " + DatosDinamicos.combo("efm", ret_ejercicio_fiscal_mes, ret_ejercicio_fiscal_mes1, "") + "<input id='ef' name='ef' type='text'size='4' maxlength='4' value='"+ret_ejercicio_fiscal+"' onkeypress=\"_numero(event)\" /></td>" +
                        "<td align='right'>Autorizaci&oacute;n: <input type='text' size='10' maxlength='39' id='aur' name='aur' value='"+ret_autorizacion+"' onkeypress=\"_numero(event);\" /></td></tr>" +

                        "<tr><td colspan='3'>Factura a la que afecta: <span id='axIdFVA'>"+ (idFP.compareTo("-1")!=0 ? num_factura : DatosDinamicos.combo(rsFactura, "id_factura_venta", id_factura_venta_cobro, "")) +"</span></td></tr>"+

                        "</table>" +

                        "<div id='InRe'><a href='javascript:void(0);' style='float:left;' onclick=\"inv_addRetencion(0, 'fac_setDetalleCobro();');fac_setDetalleCobro();\">Insertar Retenci&oacute;n</a></div>" +
                        "&nbsp;&nbsp;<div style=\"display:none\" id='axBI'>"+ DatosDinamicos.combo(rsBasesImponibles, "9a0", "9b0", "", "inv_sumRetSubTotales(9d0,1);inv_sumRetTotales();", "inv_sumRetSubTotales(9d0,1);inv_sumRetTotales();", "", 165) +"</div>"
                        + "<div style=\"display:none\" id='axCR'>"+ DatosDinamicos.combo(rsTablaImpuestos, "9a0", "9b0", "inv_setMsg('axMsR');", "inv_setConcepto(9c0);inv_sumRetSubTotales(9d0,1);inv_sumRetTotales();", "_('9e0').focus();", "", 295) +"</div>" +
                        "<div style=\"display:none\" id='axC'>"+ objTablaImpuesto.getConceptoRetencion() +"</div>" +
                        "<div style=\"display:none\" id='axPCV'>"+ objTablaImpuesto.getJSON(rsTablaImpuestosPlanCuentasVentas) +"</div>" +
                        "<div style=\"display:none\" id='axFVSR'>"+ objFacturaVenta.getJSON(rsFactura) +"</div>" +
                        "<table cellpadding='0' cellspacing='0'>" +
                        "<TH class='jm_TH' width='170'>CAMPO BASE IMPONIBLE</TH>" +
                        "<TH class='jm_TH' width='300'>IMPUESTO - C&Oacute;DIGO - % DE RETENCI&Oacute;N</TH>" +
                        "<TH class='jm_TH' width='120'>BASE IMPONIBLE</TH>" +
                        "<TH class='jm_TH' width='120'>VALOR RETENIDO</TH>" +
                        "<TH class='jm_TH' width='20'>&nbsp;&nbsp;</TH></tr></table>" +

                        "<DIV id='sll1' style='overflow:auto;width:760px;height:155px;'>" +
                        "<TABLE class='jm_tabla' cellspacing='1' cellpadding='0' id='tblR'>");

                String html = "";
                try{
                    ResultSet retencionDetalle = objFacturaVenta.getRetencionDetalle(id_retencion_venta);
                    String id_tabla_impuesto = "";
                    while(retencionDetalle.next()){
                        id_tabla_impuesto = (retencionDetalle.getString("id_tabla_impuesto")!=null) ? retencionDetalle.getString("id_tabla_impuesto") : "1";
                        html += "<tr id='rRe"+i+"' class='jm_filaPar'>";
                        html += "<td width='300'>" + DatosDinamicos.combo(rsTablaImpuestos, "idR"+i, id_tabla_impuesto, "inv_setMsg('axMsR');", "inv_setConcepto("+String.valueOf(i)+");", "inv_sumRetSubTotales("+i+");inv_sumRetTotales();_('bi"+i+"').focus();", "", 295) + ""
                                + "<input type='hidden'  id='idPCV"+i+"' value='"+((retencionDetalle.getString("id_plan_cuenta_venta")!=null) ? retencionDetalle.getString("id_plan_cuenta_venta") : "")+"' /></td>";
                        html += "<td width='151' align='right'><input id='bi"+i+"' name='bi"+i+"' type='text' class='inpTbl' size='20' onkeypress=\"_evaluar(event, '0123456789.');\" onkeyup=\"inv_sumRetSubTotales("+i+");inv_sumRetTotales();\" " +
                                "onfocus=\"this.select();\" value='"+((retencionDetalle.getString("base_imponible")!=null) ? retencionDetalle.getString("base_imponible") : "")+"' /></td>";
                        html += "<td width='151' align='right'><input id='vr"+i+"' name='vr"+i+"' type='text' class='inpTbl' size='20' onkeypress=\"_evaluar(event, '0123456789.');\" onkeyup=\"inv_sumRetTotales();\" " +
                                "onfocus=\"this.select();\" value='"+((retencionDetalle.getString("valor_retenido")!=null) ? retencionDetalle.getString("valor_retenido") : "")+"' /></td>";
                        html += "<td width='21' align='center'><div class='jm_icoCon' style='background-position:-545px -16px;float:right;' title='Eliminar' onclick=\"_R('rRe"+i+"');inv_sumRetTotales();\">&nbsp;</div></td>";
                        html += "</tr>";
                        i++;
                    }
                    retencionDetalle.close();
                }catch(Exception e){
                    e.printStackTrace();
                }

                out.print(html);


                out.println("</table></div><table cellpadding='0' cellspacing='0'>" +
                        "<tr><td colspan='2' width='454' align='right'>TOTAL RETENCI&Oacute;N: </td>" +
                        "<td align='right'> &nbsp;<input type='text' id='tvr' name='tvr' value='"+ret_impuesto_retenido+"' style='text-align:right' onkeypress=\"_evaluar(event, '0123456789.');\" /></td>" +
                        "</tr></table>");

                try{
                    rsTablaImpuestos.close();
                }catch(Exception e){
                    e.printStackTrace();
                }

                out.print("</div>");





            out.println("<table class='jm_tabla' width='100%' cellspacing='0' cellpadding='0'><tr>");
            if(!anulado){
                long fecha_sistema = Fecha.getTimeStamp(Fecha.getFecha("SQL"));
                long fecha_asiento = Fecha.getTimeStamp(fecha_actual);
                //if(fecha_en_efectivo.compareTo("")==0){
                    if(id.compareTo("-1")!=0 && objRol.getAcceso(id_rol, "cobroAnular") && forma_pago.compareTo("d")==0 && fecha_sistema==fecha_asiento){
                        out.println("<td><input type='button' value='Anular Comprobante' onclick=\"if(confirm('¿Está seguro de anular el pago?')){cnt_anularDocumento('tbl_factura_venta_cobro', '"+idFP+"&idC="+id+"&p=c');}\" /></td>");
                    }
                    //if(id.compareTo("-1")==0 || (id.compareTo("-1")!=0 && objRol.getAcceso(id_rol, "pagoEditar"))){
                    if(id.compareTo("-1")==0){
                        out.println("<td><input type='submit' style='float:left;' value='Guardar' onmousedown=\"_('imp').value=0;_('toPa').value=_('tp').innerHTML;\" onclick=\"this.disabled=true;\" /></td>");
                        out.println("<td align='right'> <input type='submit' value='Guardar e Imprimir' onmousedown=\"_('imp').value=1;_('toPa').value=_('tp').innerHTML;\" onclick=\"this.disabled=true;\" /></td>");
                    }
                //}
            }else{
                out.println("<td><span class='marca'>COMPROBANTE DE EGRESO ANULADO</span></td>");
            }
            out.println("</tr></table>");



            out.print("</div></div></form>");

        } finally {
            objBaseImponible.cerrar();
            objComprobanteIngreso.cerrar();
            objTablaImpuesto.cerrar();
            objFacturaVenta.cerrar();
            objRol.cerrar();
            objBanco.cerrar();
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
