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
import java.sql.ResultSet;
import jm.web.DatosDinamicos;
import jm.seg.clas.Rol;
import jm.fac.clas.ArqueoCaja;
import jm.web.Fecha;
import jm.adm.clas.PuntoEmision;
import jm.cnt.clas.Banco;
import jm.cnt.clas.ComprobanteDiario;
import jm.cnt.clas.ComprobanteIngreso;
import jm.cnt.clas.PlanCuenta;
import jm.fac.clas.FacturaVenta;
import jm.web.Addons;

/**
 *
 * @author Jorge
 */
public class frmArqueo extends HttpServlet {
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

        /*Configuracion conf = new Configuracion(this._ip, this._puerto, this._db, usuario, clave);
        String bloqueo_contabilizacion = "";
        while(true){
            bloqueo_contabilizacion = conf.getValor("bloqueo_contabilizacion");
            if(bloqueo_contabilizacion.compareTo("false")==0) {
                conf.setValor("bloqueo_contabilizacion", "true");
                break;
            }
            try{
                Thread.sleep(500);
            }catch(InterruptedException e){
                e.printStackTrace();
            }
        }
        */
        
        ComprobanteIngreso objComprobanteIngreso = new ComprobanteIngreso(this._ip, this._puerto, this._db, usuario, clave);
        //objComprobanteIngreso.contabilizar(usuario);
        //boolean sinContabilizar = objComprobanteIngreso.sinContabilizar(usuario);
        
        
        PuntoEmision objPuntoEmision = new PuntoEmision(this._ip, this._puerto, this._db, usuario, clave);
        String id_plan_cuenta_caja = objPuntoEmision.getIdCaja(id_punto_emision);
        String id_plan_cuenta_diferencia_caja = objPuntoEmision.getIdDiferenciaCaja(id_punto_emision);

        PlanCuenta objPlanCuenta = new PlanCuenta(this._ip, this._puerto, this._db, usuario, clave);
        ResultSet cuentas = objPlanCuenta.getTablasAsignacion();
        objPlanCuenta.cerrar();

        Banco objBanco = new Banco(this._ip, this._puerto, this._db, usuario, clave);
        String id_plan_cuenta_banco = "0";
        try{
            ResultSet rsBanco = objBanco.getBancosIngresos();
            if(rsBanco.next()){
                id_plan_cuenta_banco = (rsBanco.getString("id_plan_cuenta")!=null) ? rsBanco.getString("id_plan_cuenta") : "0";
                rsBanco.close();
            }
        }catch(Exception e){
            e.printStackTrace();
        }finally{
            objBanco.cerrar();
        }

        ComprobanteDiario objComprobanteDiario = new ComprobanteDiario(this._ip, this._puerto, this._db, usuario, clave);
        
        ArqueoCaja objArqueoCaja = new ArqueoCaja(this._ip, this._puerto, this._db, usuario, clave);
        boolean arqueoPendiente = objArqueoCaja.arqueoPendiente(usuario);
                
        Rol objRol = new Rol(this._ip, this._puerto, this._db, usuario, clave);
        
        try {
            String ultima_fecha = objArqueoCaja.getFechaUltimoArqueo(usuario);
            String ultima_hora = objArqueoCaja.getHoraUltimoArqueo(usuario);

            String id = request.getParameter("id");
            int _altBody = 435;
            int i=0;

            String num_documento = "";
            String fecha = Fecha.getFecha("SQL");
            String hora = Fecha.getHora();
            String cajero = usuario;
            String id_comprobante_diario="-1";
            String id_comprobante_diario_cierre="-1";
            double saldo_caja = Addons.redondear(objArqueoCaja.getSaldoCaja(id_plan_cuenta_caja));
            int num_cheques = 0;
            float valor_cheques = 0;
            int num_comp_pagos = 0;
            float valor_comp_pagos = 0;
            int num_retenciones = 0;
            float valor_retenciones = 0;
            String valor_efectivo = "0";
            String total_caja = "0";
            String diferencia = "0";
            String tipo_diferencia = "0";
            boolean cerrado = false;
            boolean anulado = false;

            if(id.compareTo("-1")!=0){
                try{
                    ResultSet rsArqueoCaja = objArqueoCaja.getArqueoCaja(id);
                    if(rsArqueoCaja.next()){
                        num_documento = (rsArqueoCaja.getString("num_documento")!=null) ? rsArqueoCaja.getString("num_documento") : "";
                        fecha = (rsArqueoCaja.getString("fecha")!=null) ? rsArqueoCaja.getString("fecha") : "";
                        hora = (rsArqueoCaja.getString("Hora")!=null) ? rsArqueoCaja.getString("Hora") : "";
                        cajero = (rsArqueoCaja.getString("cajero")!=null) ? rsArqueoCaja.getString("cajero") : "";
                        id_comprobante_diario = (rsArqueoCaja.getString("id_comprobante_diario")!=null) ? rsArqueoCaja.getString("id_comprobante_diario") : "-1";
                        saldo_caja = (rsArqueoCaja.getString("saldo_caja")!=null) ? rsArqueoCaja.getDouble("saldo_caja") : 0;
                        num_cheques = (rsArqueoCaja.getString("num_cheques")!=null) ? rsArqueoCaja.getInt("num_cheques") : 0;
                        valor_cheques = (rsArqueoCaja.getString("valor_cheques")!=null) ? rsArqueoCaja.getFloat("valor_cheques") : 0;
                        num_comp_pagos = (rsArqueoCaja.getString("num_comp_pagos")!=null) ? rsArqueoCaja.getInt("num_comp_pagos") : 0;
                        valor_comp_pagos = (rsArqueoCaja.getString("valor_comp_pagos")!=null) ? rsArqueoCaja.getFloat("valor_comp_pagos") : 0;
                        num_retenciones = (rsArqueoCaja.getString("num_retenciones")!=null) ? rsArqueoCaja.getInt("num_retenciones") : 0;
                        valor_retenciones = (rsArqueoCaja.getString("valor_retenciones")!=null) ? rsArqueoCaja.getFloat("valor_retenciones") : 0;
                        valor_efectivo = (rsArqueoCaja.getString("valor_efectivo")!=null) ? rsArqueoCaja.getString("valor_efectivo") : "0";
                        total_caja = (rsArqueoCaja.getString("total")!=null) ? rsArqueoCaja.getString("total") : "";
                        diferencia = (rsArqueoCaja.getString("diferencia")!=null) ? rsArqueoCaja.getString("diferencia") : "";
                        tipo_diferencia = (rsArqueoCaja.getString("tipo_diferencia")!=null) ? rsArqueoCaja.getString("tipo_diferencia") : "";
                        id_comprobante_diario_cierre = (rsArqueoCaja.getString("id_comprobante_diario_cierre")!=null) ? rsArqueoCaja.getString("id_comprobante_diario_cierre") : "-1";
                        cerrado = (rsArqueoCaja.getString("cerrado")!=null) ? rsArqueoCaja.getBoolean("cerrado") : false;
                        anulado = (rsArqueoCaja.getString("anulado")!=null) ? rsArqueoCaja.getBoolean("anulado") : false;
                        rsArqueoCaja.close();
                    }
                }catch(Exception e){
                    e.printStackTrace();
                }
            }



            String verDiferencia = (objRol.getAcceso(id_rol, "verSaldoCaja")) ? "visible" : "hidden";
            
            String html = "obj»cmp_html^fun»_R('bloq_cmp');_R('cmp');^msg»Por favor, revise que el asiento de cierre de caja esté realizado para poder ingresar un nuevo arqueo de caja.";
            
            if (id.compareTo("-1")!=0 || (id.compareTo("-1")==0 && !arqueoPendiente) ) {
                html = "obj»cmp_html^fun»_objTab.seleccionar(0);^frm»";
                
                html += "<form action='frmArqueoGuardar' onsubmit='return fac_arqueoGuardar(this)' autocomplete='off'>";
                html += "<input type='hidden' id='id' name='id' value='"+id+"' />";
                html += "<input type='hidden' id='imp' name='imp' value='0' />";
                html += "<input type='hidden' id='saldo_caja' name='saldo_caja' size='10' value='"+saldo_caja+"' />";
                html += "<input type='hidden' id='idPCDC' value='"+id_plan_cuenta_diferencia_caja+"' />";
                html += "<input type='hidden' id='idPCC' value='"+id_plan_cuenta_caja+"' />";


                html += "<div class='grupoTab' style='width:760px;'><div id='tabR0'>";
                html += "<a id='tab00' href='javascript:void(0);' class='jm_tabAct' onclick='_objTab.seleccionar(0);'>Arqueo</a>";
                html += "<a id='tab01' href='javascript:void(0);' class='jm_tab' onclick='_objTab.seleccionar(1);'>Cheques</a>";
                html += "<a id='tab02' href='javascript:void(0);' class='jm_tab' onclick='_objTab.seleccionar(2);'>Documentos</a>";
                html += "<a id='tab03' href='javascript:void(0);' class='jm_tab' onclick='_objTab.seleccionar(3);'>Retenciones</a>";
                if(objRol.getAcceso(id_rol, "verSaldoCaja")){
                    html += "<a id='tab04' href='javascript:void(0);' class='jm_tab' onclick='_objTab.seleccionar(4);'>Ajuste</a>";
                }
                if(id.compareTo("-1")!=0){
                    html += "<a id='tab05' href='javascript:void(0);' class='jm_tab' onclick='_objTab.seleccionar(5);'>Cierre de caja</a>";
                }
                html += "</div>";

                // PRIMER TAB
                html += "<div id='tabR1'>";
                html += "<div id='tab10' class='jm_tabHTML' style='display:block;width:750px;height:"+_altBody+"px;'>";

                html += "<table width='420'><tr><td colspan='2' align='right'>Nro. <input id='num_documento' name='num_documento' type='text' size='10' value='"+num_documento+"' readonly /></td></tr>";
                html += "<tr><td>Fecha: <input type='text' id='fecha' name='fecha' value='"+fecha+"' size='10' readOnly /></td>"
                        + "<td>Hora: <input type='text' id='hora' name='hora' value='"+hora+"' size='10' maxlength='5' readOnly /></td></tr></table>";

                /* efectivo */
                html += "<fieldset><legend>Monedas y billetes</legend>"
                        + "<div><a href='javascript:void(0);' onclick=\"fac_addEfectivo();\">Insertar Denominaci&oacute;n</a></div>"
                        + "<table cellpadding='0' cellspacing='0'>" +
                        "<TH class='jm_TH' width='200'>DENOMINACION</TH>" +
                        "<TH class='jm_TH' width='90'>CANTIDAD</TH>" +
                        "<TH class='jm_TH' width='90'>TOTAL</TH>" +
                        "<TH class='jm_TH' width='20'>&nbsp;&nbsp;</TH></tr></table>"+
                        "<TABLE class='jm_tabla' cellspacing='1' cellpadding='0' id='tblEf'>";
                try{
                    //String id_arqueo_caja_cobro_efectivo = "";
                    String denominacion = "";
                    String cantidad = "";
                    String total = "";
                    ResultSet rsArqueoEfectivo = objArqueoCaja.getArqueoCajaEfectivo(id);
                    while(rsArqueoEfectivo.next()){
                        //id_arqueo_caja_cobro_efectivo = (rsArqueoEfectivo.getString("id_arqueo_caja_cobro_efectivo")!=null) ? rsArqueoEfectivo.getString("id_arqueo_caja_cobro_efectivo") : "1";
                        denominacion = (rsArqueoEfectivo.getString("denominacion")!=null) ? rsArqueoEfectivo.getString("denominacion") : "";
                        cantidad = (rsArqueoEfectivo.getString("cantidad")!=null) ? rsArqueoEfectivo.getString("cantidad") : "";
                        total = (rsArqueoEfectivo.getString("total")!=null) ? rsArqueoEfectivo.getString("total") : "";
                        html += "<tr id='re"+i+"' class='jm_filaPar'>";
                        html += "<td style='width:200px'><input type='text' id='den"+i+"' name='den"+i+"' size='34' maxlength='20' value='"+denominacion+"' /></td>";
                        html += "<td style='width:90px'><input id='c"+i+"' name='c"+i+"' type='text' class='inpTbl' size='9' onkeypress=\"_numero(event);\" " +
                                "onfocus=\"this.select();\" value='"+cantidad+"' /></td>";
                        html += "<td style='width:90px'><input id='t"+i+"' name='t"+i+"' type='text' class='inpTbl' size='9' onkeypress=\"_evaluar(event, '0123456789.');\" onkeyup=\"fac_sumArqueo();\" " +
                                "onfocus=\"this.select();\" value='"+total+"' /></td>";
                        html += "<td style='width:20px'>&nbsp;</td>";
                        //html += "<td style='width:20px'><div class='jm_icoCon' style='background-position:-545px -18px;' title='Eliminar' onclick=\"_R('re"+i+"');fac_sumArqueo();\">&nbsp;</div></td>";
                        html += "</tr>";
                        i++;
                    }
                    rsArqueoEfectivo.close();
                }catch(Exception e){}
                if(i==0 && id.compareTo("-1")==0){
                    html += "<tr id='re0' class='jm_filaPar'>";
                    html += "<td style='width:200px'><input type='text' id='den0' name='den0' value='billetes de $ 100' size='34' maxlength='20' /><input type='hidden' id='bill0' value='100' /></td>";
                    html += "<td style='width:90px' align=\"right\"><input id='c0' name='c0' type='text' class='inpTbl' size='9' onkeypress=\"_numero(event);\" " +
                                "onkeyup=\"fac_mulEfectivo(0);fac_sumArqueo();\" onfocus=\"this.select();\" /> </td>";
                    html += "<td style='width:90px'><input id='t0' name='t0' type='text' class='inpTbl' size='9' onkeypress=\"_evaluar(event, '0123456789.');\" onkeyup=\"fac_sumArqueo();\" " +
                                "onfocus=\"this.select();\" /></td>";
                    html += "<td style='width:20px'>&nbsp;</td>";
                    html += "</tr>";
                    html += "<tr id='re1' class='jm_filaPar'>";
                    html += "<td style='width:200px'><input type='text' id='den1' name='den1' value='billetes de $ 50' size='34' maxlength='20' /><input type='hidden' id='bill1' value='50' /></td>";
                    html += "<td style='width:90px' align=\"right\"><input id='c1' name='c1' type='text' class='inpTbl' size='9' onkeypress=\"_numero(event);\" " +
                                "onkeyup=\"fac_mulEfectivo(1);fac_sumArqueo();\" onfocus=\"this.select();\" /> </td>";
                    html += "<td style='width:90px'><input id='t1' name='t1' type='text' class='inpTbl' size='9' onkeypress=\"_evaluar(event, '0123456789.');\" onkeyup=\"fac_sumArqueo();\" " +
                                "onfocus=\"this.select();\" /></td>";
                    html += "<td style='width:20px'>&nbsp;</td>";
                    html += "</tr>";
                    html += "<tr id='re2' class='jm_filaPar'>";
                    html += "<td style='width:200px'><input type='text' id='den2' name='den2' value='billetes de $ 20' size='34' maxlength='20' /><input type='hidden' id='bill2' value='20' /></td>";
                    html += "<td style='width:90px' align=\"right\"><input id='c2' name='c2' type='text' class='inpTbl' size='9' onkeypress=\"_numero(event);\" " +
                                "onkeyup=\"fac_mulEfectivo(2);fac_sumArqueo();\" onfocus=\"this.select();\" /> </td>";
                    html += "<td style='width:90px'><input id='t2' name='t2' type='text' class='inpTbl' size='9' onkeypress=\"_evaluar(event, '0123456789.');\" onkeyup=\"fac_sumArqueo();\" " +
                                "onfocus=\"this.select();\" /></td>";
                    html += "<td style='width:20px'>&nbsp;</td>";
                    html += "</tr>";
                    html += "<tr id='re3' class='jm_filaPar'>";
                    html += "<td style='width:200px'><input type='text' id='den3' name='den3' value='billetes de $ 10' size='34' maxlength='20' /><input type='hidden' id='bill3' value='10' /></td>";
                    html += "<td style='width:90px' align=\"right\"><input id='c3' name='c3' type='text' class='inpTbl' size='9' onkeypress=\"_numero(event);\" " +
                                "onkeyup=\"fac_mulEfectivo(3);fac_sumArqueo();\" onfocus=\"this.select();\" /> </td>";
                    html += "<td style='width:90px'><input id='t3' name='t3' type='text' class='inpTbl' size='9' onkeypress=\"_evaluar(event, '0123456789.');\" onkeyup=\"fac_sumArqueo();\" " +
                                "onfocus=\"this.select();\" /></td>";
                    html += "<td style='width:20px'>&nbsp;</td>";
                    html += "</tr>";
                    html += "<tr id='re4' class='jm_filaPar'>";
                    html += "<td style='width:200px'><input type='text' id='den4' name='den4' value='billetes de $ 5' size='34' maxlength='20' /><input type='hidden' id='bill4' value='5' /></td>";
                    html += "<td style='width:90px' align=\"right\"><input id='c4' name='c4' type='text' class='inpTbl' size='9' onkeypress=\"_numero(event);\" " +
                                "onkeyup=\"fac_mulEfectivo(4);fac_sumArqueo();\" onfocus=\"this.select();\" /> </td>";
                    html += "<td style='width:90px'><input id='t4' name='t4' type='text' class='inpTbl' size='9' onkeypress=\"_evaluar(event, '0123456789.');\" onkeyup=\"fac_sumArqueo();\" " +
                                "onfocus=\"this.select();\" /></td>";
                    html += "<td style='width:20px'>&nbsp;</td>";
                    html += "</tr>";
                    html += "<tr id='re5' class='jm_filaPar'>";
                    html += "<td style='width:200px'><input type='text' id='den5' name='den5' value='billetes de $ 1' size='34' maxlength='20' /><input type='hidden' id='bill5' value='1' /></td>";
                    html += "<td style='width:90px' align=\"right\"><input id='c5' name='c5' type='text' class='inpTbl' size='9' onkeypress=\"_numero(event);\" " +
                                "onkeyup=\"fac_mulEfectivo(5);fac_sumArqueo();\" onfocus=\"this.select();\" /> </td>";
                    html += "<td style='width:90px'><input id='t5' name='t5' type='text' class='inpTbl' size='9' onkeypress=\"_evaluar(event, '0123456789.');\" onkeyup=\"fac_sumArqueo();\" " +
                                "onfocus=\"this.select();\" /></td>";
                    html += "<td style='width:20px'>&nbsp;</td>";
                    html += "</tr>";
                        /*      MONEDAS        */
                    html += "<tr id='re6' class='jm_filaPar'>";
                    html += "<td style='width:200px'><input type='text' id='den6' name='den6' value='monedas de $ 1' size='34' maxlength='20' /><input type='hidden' id='bill6' value='1' /></td>";
                    html += "<td style='width:90px' align=\"right\"><input id='c6' name='c6' type='text' class='inpTbl' size='9' onkeypress=\"_numero(event);\" " +
                                "onkeyup=\"fac_mulEfectivo(6);fac_sumArqueo();\" onfocus=\"this.select();\" /> </td>";
                    html += "<td style='width:90px'><input id='t6' name='t6' type='text' class='inpTbl' size='9' onkeypress=\"_evaluar(event, '0123456789.');\" onkeyup=\"fac_sumArqueo();\" " +
                                "onfocus=\"this.select();\" /></td>";
                    html += "<td style='width:20px'>&nbsp;</td>";
                    html += "</tr>";
                    html += "<tr id='re7' class='jm_filaPar'>";
                    html += "<td style='width:200px'><input type='text' id='den7' name='den7' value='monedas de $ 0.50' size='34' maxlength='20' /><input type='hidden' id='bill7' value='0.5' /></td>";
                    html += "<td style='width:90px' align=\"right\"><input id='c7' name='c7' type='text' class='inpTbl' size='9' onkeypress=\"_numero(event);\" " +
                                "onkeyup=\"fac_mulEfectivo(7);fac_sumArqueo();\" onfocus=\"this.select();\" /> </td>";
                    html += "<td style='width:90px'><input id='t7' name='t7' type='text' class='inpTbl' size='9' onkeypress=\"_evaluar(event, '0123456789.');\" onkeyup=\"fac_sumArqueo();\" " +
                                "onfocus=\"this.select();\" /></td>";
                    html += "<td style='width:20px'>&nbsp;</td>";
                    html += "</tr>";
                    html += "<tr id='re8' class='jm_filaPar'>";
                    html += "<td style='width:200px'><input type='text' id='den8' name='den8' value='monedas de $ 0.25' size='34' maxlength='20' /><input type='hidden' id='bill8' value='0.25' /></td>";
                    html += "<td style='width:90px' align=\"right\"><input id='c8' name='c8' type='text' class='inpTbl' size='9' onkeypress=\"_numero(event);\" " +
                                "onkeyup=\"fac_mulEfectivo(8);fac_sumArqueo();\" onfocus=\"this.select();\" /> </td>";
                    html += "<td style='width:90px'><input id='t8' name='t8' type='text' class='inpTbl' size='9' onkeypress=\"_evaluar(event, '0123456789.');\" onkeyup=\"fac_sumArqueo();\" " +
                                "onfocus=\"this.select();\" /></td>";
                    html += "<td style='width:20px'>&nbsp;</td>";
                    html += "</tr>";
                    html += "<tr id='re9' class='jm_filaPar'>";
                    html += "<td style='width:200px'><input type='text' id='den9' name='den9' value='monedas de $ 0.10' size='34' maxlength='20' /><input type='hidden' id='bill9' value='0.1' /></td>";
                    html += "<td style='width:90px' align=\"right\"><input id='c9' name='c9' type='text' class='inpTbl' size='9' onkeypress=\"_numero(event);\" " +
                                "onkeyup=\"fac_mulEfectivo(9);fac_sumArqueo();\" onfocus=\"this.select();\" /> </td>";
                    html += "<td style='width:90px'><input id='t9' name='t9' type='text' class='inpTbl' size='9' onkeypress=\"_evaluar(event, '0123456789.');\" onkeyup=\"fac_sumArqueo();\" " +
                                "onfocus=\"this.select();\" /></td>";
                    html += "<td style='width:20px'>&nbsp;</td>";
                    html += "</tr>";
                    html += "<tr id='re10' class='jm_filaPar'>";
                    html += "<td style='width:200px'><input type='text' id='den10' name='den10' value='monedas de $ 0.05' size='34' maxlength='20' /><input type='hidden' id='bill10' value='0.05' /></td>";
                    html += "<td style='width:90px' align=\"right\"><input id='c10' name='c10' type='text' class='inpTbl' size='9' onkeypress=\"_numero(event);\" " +
                                "onkeyup=\"fac_mulEfectivo(10);fac_sumArqueo();\" onfocus=\"this.select();\" /> </td>";
                    html += "<td style='width:90px'><input id='t10' name='t10' type='text' class='inpTbl' size='9' onkeypress=\"_evaluar(event, '0123456789.');\" onkeyup=\"fac_sumArqueo();\" " +
                                "onfocus=\"this.select();\" /></td>";
                    html += "<td style='width:20px'>&nbsp;</td>";
                    html += "</tr>";
                    html += "<tr id='re11' class='jm_filaPar'>";
                    html += "<td style='width:200px'><input type='text' id='den11' name='den11' value='monedas de $ 0.01' size='34' maxlength='20' /><input type='hidden' id='bill11' value='0.01' /></td>";
                    html += "<td style='width:90px' align=\"right\"><input id='c11' name='c11' type='text' class='inpTbl' size='9' onkeypress=\"_numero(event);\" " +
                                "onkeyup=\"fac_mulEfectivo(11);fac_sumArqueo();\" onfocus=\"this.select();\" /> </td>";
                    html += "<td style='width:90px'><input id='t11' name='t11' type='text' class='inpTbl' size='9' onkeypress=\"_evaluar(event, '0123456789.');\" onkeyup=\"fac_sumArqueo();\" " +
                                "onfocus=\"this.select();\" /></td>";
                    html += "<td style='width:20px'>&nbsp;</td>";
                    html += "</tr>";
                }

                html += "</table></fieldset>";


                if(id.compareTo("-1")!=0){
                    html += "<table>";
                    if(objRol.getAcceso(id_rol, "verSaldoCaja")){
                        html += "<tr><td>Saldo en caja actual: </td><td>"+saldo_caja+"</td></tr>";
                    }
                    html += "<tr><td>Total monedas y villetes: </td><td><input type='text' id='valor_efectivo' name='valor_efectivo' size='10' value='"+valor_efectivo+"' onkeypress=\"_evaluar(event, '0123456789.');\" /></td></tr>"
                        + "<tr><td>Total cheques (<input type='text' id='num_cheques' name='num_cheques' size='5' value='"+num_cheques+"' onkeypress=\"_numero(event);\" />): </td>"
                            + "<td><input type='text' id='valor_cheques' name='valor_cheques' size='10' value='"+valor_cheques+"' onkeypress=\"_evaluar(event, '0123456789.');\" /></td></tr>"
                        + "<tr><td>Total comprobantes de pago (<input type='text' id='num_comp_pagos' name='num_comp_pagos' size='5' value='"+num_comp_pagos+"' onkeypress=\"_numero(event);\" />): </td>"
                            + "<td><input type='text' id='valor_comp_pagos' name='valor_comp_pagos' size='10' value='"+valor_comp_pagos+"' onkeypress=\"_evaluar(event, '0123456789.');\" /></td></tr>"
                        + "<tr><td>Total retenciones (<input type='text' id='num_retenciones' name='num_retenciones' size='5' value='"+num_retenciones+"' onkeypress=\"_numero(event);\" />): </td>"
                            + "<td><input type='text' id='valor_retenciones' name='valor_retenciones' size='10' value='"+valor_retenciones+"' onkeypress=\"_evaluar(event, '0123456789.');\" /></td></tr>"
                        + "<tr><td>Total en caja: </td><td><input type='text' id='total_caja' name='total_caja' size='10' value='"+total_caja+"' onkeypress=\"_evaluar(event, '0123456789.');\" /></td></tr>"
                        + "<tr style='visibility:"+verDiferencia+"'><td>Diferencia: </td><td><input type='text' id='diferencia' name='diferencia' size='10' value='"+diferencia+"' onkeypress=\"_evaluar(event, '0123456789.-');\" onkeyup=\"fac_diferencia(this.value)\" /> "
                            + " &nbsp; <label><input type='radio' id='d0' name='tipo_diferencia' value='f' "+((tipo_diferencia.compareTo("f")==0) ? "checked" : "")+" /> faltante</label>"
                            + " &nbsp; <label><input type='radio' id='d1' name='tipo_diferencia' value='s' "+((tipo_diferencia.compareTo("s")==0) ? "checked" : "")+" /> sobrante</label> </td></tr>"
                        + "</table>";
                }else{
                    html += "<table>";
                    if(objRol.getAcceso(id_rol, "verSaldoCaja")){
                        html += "<tr><td>Saldo en caja actual: </td><td>"+saldo_caja+"</td></tr>";
                    }
                    html += "<tr><td>Total monedas y billetes: </td><td><input type='text' id='valor_efectivo' name='valor_efectivo' size='10' value='"+valor_efectivo+"' onkeypress=\"_evaluar(event, '0123456789.');\" /></td></tr>"
                        + "<tr><td>Total cheques (<input type='text' id='num_cheques' name='num_cheques' size='5' value='<<num_cheques>>' onkeypress=\"_numero(event);\" />): </td>"
                            + "<td><input type='text' id='valor_cheques' name='valor_cheques' size='10' value='<<valor_cheques>>' onkeypress=\"_evaluar(event, '0123456789.');\" /></td></tr>"
                        + "<tr><td>Total comprobantes de pago (<input type='text' id='num_comp_pagos' name='num_comp_pagos' size='5' value='<<num_comp_pagos>>' onkeypress=\"_numero(event);\" />): </td>"
                            + "<td><input type='text' id='valor_comp_pagos' name='valor_comp_pagos' size='10' value='<<valor_comp_pagos>>' onkeypress=\"_evaluar(event, '0123456789.');\" /></td></tr>"
                        + "<tr><td>Total retenciones (<input type='text' id='num_retenciones' name='num_retenciones' size='5' value='<<num_retenciones>>' onkeypress=\"_numero(event);\" />): </td>"
                            + "<td><input type='text' id='valor_retenciones' name='valor_retenciones' size='10' value='<<valor_retenciones>>' onkeypress=\"_evaluar(event, '0123456789.');\" /></td></tr>"
                        + "<tr><td>Total en caja: </td><td><input type='text' id='total_caja' name='total_caja' size='10' value='<<total_caja>>' onkeypress=\"_evaluar(event, '0123456789.');\" /></td></tr>"
                        + "<tr style='visibility:"+verDiferencia+"'><td>Diferencia: </td><td><input type='text' id='diferencia' name='diferencia' size='10' value='<<diferencia>>' onkeypress=\"_evaluar(event, '0123456789.-');\" onkeyup=\"fac_diferencia(this.value)\" /> "
                            + " &nbsp; <label><input type='radio' id='d0' name='tipo_diferencia' value='f' "+((tipo_diferencia.compareTo("f")==0) ? "checked" : "")+" onclick=\"fac_diferencia()\" /> faltante</label>"
                            + " &nbsp; <label><input type='radio' id='d1' name='tipo_diferencia' value='s' "+((tipo_diferencia.compareTo("s")==0) ? "checked" : "")+" onclick=\"fac_diferencia()\" /> sobrante</label> </td></tr>"
                        + "</table>";
                }

                html += "</div>";





                // SEGUNDO TAB




                html += "<div id='tab11' class='jm_tabHTML' style='display:none;width:750px;height:"+_altBody+"px;'>";

                /* cheques */
                //html += "<div><a href='javascript:void(0);' onclick=\"fac_addCheque();\">Insertar Cheque</a></div>";
                html += "<table cellpadding='0' cellspacing='0'>" +
                        "<TH class='jm_TH' width='200'>BANCO</TH>" +
                        "<TH class='jm_TH' width='90'>Nro. CHEQUE</TH>" +
                        "<TH class='jm_TH' width='90'>VALOR</TH>" +
                        "<TH class='jm_TH' width='100'>EFECTIVIZADO</TH>" +
                        "<TH class='jm_TH' width='20'>&nbsp;&nbsp;</TH></tr></table>"+
                        "<TABLE class='jm_tabla' cellspacing='1' cellpadding='0' id='tblCh'>";
                try{
                    i=0;
                    String banco = "";
                    String num_cheque = "";
                    String valor = "";
                    String efectivizado = "";
                    if(id.compareTo("-1")!=0){
                        ResultSet rsArqueoCheque = objArqueoCaja.getArqueoCajaCheque(id);
                        while(rsArqueoCheque.next()){
                            banco = (rsArqueoCheque.getString("banco")!=null) ? rsArqueoCheque.getString("banco") : "";
                            num_cheque = (rsArqueoCheque.getString("num_cheque")!=null) ? rsArqueoCheque.getString("num_cheque") : "";
                            valor = (rsArqueoCheque.getString("valor")!=null) ? rsArqueoCheque.getString("valor") : "";
                            efectivizado = (rsArqueoCheque.getString("efectivizado")!=null) ? rsArqueoCheque.getString("efectivizado") : "";
                            html += "<tr id='rc"+i+"' class='jm_filaPar'>";
                            html += "<td style='width:200px'><input type='text' id='bc"+i+"' name='bc"+i+"' size='34' maxlength='50' value='"+banco+"' /></td>";
                            html += "<td style='width:90px'><input id='ch"+i+"' name='ch"+i+"' type='text' class='inpTbl' size='9' onkeypress=\"_numero(event);\" onkeyup=\"fac_sumCheques();\" " +
                                    "onfocus=\"this.select();\" value='"+num_cheque+"' /></td>";
                            html += "<td style='width:90px'><input id='v"+i+"' name='v"+i+"' type='text' class='inpTbl' size='9' onkeypress=\"_evaluar(event, '0123456789.');\" onkeyup=\"fac_sumArqueo();\" " +
                                    "onfocus=\"this.select();\" value='"+valor+"' /></td>";
                            html += "<td style='width:100px'><input id='ef"+i+"' name='ef"+i+"' type='text' class='inpTbl' style=\"width:13px\" readonly onkeypress=\"_evaluar(event, '0123456789.');\" onkeyup=\"fac_sumArqueo();\" " +
                                    "onfocus=\"this.select();\" value='"+efectivizado+"' /></td>";
                            html += "<td style='width:20px'>&nbsp;</td>";
                            html += "</tr>";
                            i++;
                        }
                        rsArqueoCheque.close();
                    }else{
                        ResultSet rsArqueoCheque = objComprobanteIngreso.getComprobantesIngresoCheques(ultima_fecha, ultima_hora, cajero);
                        while(rsArqueoCheque.next()){
                            banco = (rsArqueoCheque.getString("banco")!=null) ? rsArqueoCheque.getString("banco") : "";
                            num_cheque = (rsArqueoCheque.getString("num_cheque")!=null) ? rsArqueoCheque.getString("num_cheque") : "";
                            valor = (rsArqueoCheque.getString("valor_son")!=null) ? rsArqueoCheque.getString("valor_son") : "";
                            efectivizado = (rsArqueoCheque.getString("fecha_en_efectivo")!=null) ? rsArqueoCheque.getString("fecha_en_efectivo") : "";
                            banco += ". Fact. " + ((rsArqueoCheque.getString("numero_factura")!=null) ? rsArqueoCheque.getString("numero_factura") : "");
                            if(efectivizado.compareTo("")!=0){
                                num_cheques ++;
                                valor_cheques += Float.parseFloat(valor);
                            }
                            html += "<tr id='rc"+i+"' class='jm_filaPar'>";
                            html += "<td style='width:200px'><input type='text' id='bc"+i+"' name='bc"+i+"' size='34' maxlength='50' value='"+banco+"' /></td>";
                            html += "<td style='width:90px'><input id='ch"+i+"' name='ch"+i+"' type='text' class='inpTbl' size='9' onkeypress=\"_numero(event);\" onkeyup=\"fac_sumCheques();\" " +
                                    "onfocus=\"this.select();\" value='"+num_cheque+"' /></td>";
                            html += "<td style='width:90px'><input id='v"+i+"' name='v"+i+"' type='text' class='inpTbl' size='9' onkeypress=\"_evaluar(event, '0123456789.');\" onkeyup=\"fac_sumArqueo();\" " +
                                    "onfocus=\"this.select();\" value='"+valor+"' /></td>";
                            html += "<td style='width:100px'><input id='ef"+i+"' name='ef"+i+"' type='text' class='inpTbl' style=\"width:13px\" readonly onkeypress=\"_evaluar(event, '0123456789.');\" onkeyup=\"fac_sumArqueo();\" " +
                                    "onfocus=\"this.select();\" value='"+(efectivizado.compareTo("")!=0?"E":"")+"' /></td>";
                            html += "<td style='width:20px'><div class='jm_icoCon' style='background-position:-545px -18px;' title='Eliminar' onclick=\"_R('rc"+i+"');fac_sumArqueo();\">&nbsp;</div></td>";
                            html += "</tr>";
                            i++;
                        }
                        rsArqueoCheque.close();
                    }

                }catch(Exception e){
                    e.printStackTrace();
                }
                html += "</table>";

                html += "</div>";





                // TERCER TAB




                html += "<div id='tab12' class='jm_tabHTML' style='display:none;width:750px;height:"+_altBody+"px;'>";

                /* transferencias - depÃ³sitos - tarjeta de crÃ©dito */
                //html += "<div><a href='javascript:void(0);' onclick=\"fac_addDocPago();\">Insertar Documento (dep&oacute;sito o transferencia) </a></div>";
                html += "<table cellpadding='0' cellspacing='0'>" +
                        "<TH class='jm_TH' width='200'>Nro. COMP. PAGO</TH>" +
                        "<TH class='jm_TH' width='90'>VALOR</TH>" +
                        "<TH class='jm_TH' width='20'>&nbsp;&nbsp;</TH></tr></table>"+
                        "<TABLE class='jm_tabla' cellspacing='1' cellpadding='0' id='tblOtr'>";
                try{
                    i=0;
                    //String id_arqueo_caja_otro = "";
                    String num_comp_pago = "";
                    String valor = "";
                    if(id.compareTo("-1")!=0){
                    ResultSet rsArqueoDocs = objArqueoCaja.getArqueoCajaOtros(id);
                        while(rsArqueoDocs.next()){
                            //id_arqueo_caja_otro = (rsArqueoEfectivo.getString("id_arqueo_caja_otro")!=null) ? rsArqueoEfectivo.getString("id_arqueo_caja_otro") : "1";
                            num_comp_pago = (rsArqueoDocs.getString("num_comp_pago")!=null) ? rsArqueoDocs.getString("num_comp_pago") : "";
                            valor = (rsArqueoDocs.getString("valor")!=null) ? rsArqueoDocs.getString("valor") : "0";
                            valor_cheques += Float.parseFloat(valor);
                            html += "<tr id='ro"+i+"' class='jm_filaPar'>";
                            html += "<td style='width:200px'><input id='doc"+i+"' name='doc"+i+"' type='text' class='inpTbl' style='width:195px' onkeypress=\"_numero(event);\" onkeyup=\"fac_sumDocsPagos();\" " +
                                    "onfocus=\"this.select();\" value='"+num_comp_pago+"' /></td>";
                            html += "<td style='width:90px'><input id='vOtr"+i+"' name='vOtr"+i+"' type='text' class='inpTbl' size='9' onkeypress=\"_evaluar(event, '0123456789.');\" onkeyup=\"fac_sumArqueo();\" " +
                                    "onfocus=\"this.select();\" value='"+valor+"' /></td>";
                            html += "<td style='width:20px'>&nbsp;</td>";
                            html += "</tr>";
                            i++;
                        }
                        rsArqueoDocs.close();
                    } else {
                        ResultSet rsArqueoDocs = objComprobanteIngreso.getComprobantesIngreso("'p', 't', 'j'", ultima_fecha, ultima_hora, cajero);
                        while(rsArqueoDocs.next()){
                            //id_arqueo_caja_otro = (rsArqueoEfectivo.getString("id_arqueo_caja_otro")!=null) ? rsArqueoEfectivo.getString("id_arqueo_caja_otro") : "1";
                            num_comp_pago = (rsArqueoDocs.getString("num_comp_pago")!=null) ? rsArqueoDocs.getString("num_comp_pago") : "";
                            valor = (rsArqueoDocs.getString("valor_son")!=null) ? rsArqueoDocs.getString("valor_son") : "0";
                            num_comp_pago += ". Fact. " + ((rsArqueoDocs.getString("numero_factura")!=null) ? rsArqueoDocs.getString("numero_factura") : "");
                            num_comp_pagos ++;
                            valor_comp_pagos += Float.parseFloat(valor);
                            html += "<tr id='ro"+i+"' class='jm_filaPar'>";
                            html += "<td style='width:200px'><input id='doc"+i+"' name='doc"+i+"' type='text' class='inpTbl' style='width:195px' onkeypress=\"_numero(event);\" onkeyup=\"fac_sumDocsPagos();\" " +
                                    "onfocus=\"this.select();\" value='"+num_comp_pago+"' /></td>";
                            html += "<td style='width:90px'><input id='vOtr"+i+"' name='vOtr"+i+"' type='text' class='inpTbl' size='9' onkeypress=\"_evaluar(event, '0123456789.');\" onkeyup=\"fac_sumArqueo();\" " +
                                    "onfocus=\"this.select();\" value='"+valor+"' /></td>";
                            html += "<td style='width:20px'><div class='jm_icoCon' style='background-position:-545px -18px;' title='Eliminar' onclick=\"_R('rc"+i+"');fac_sumArqueo();\">&nbsp;</div></td>";
                            html += "</tr>";
                            i++;
                        }
                        rsArqueoDocs.close();
                    }
                }catch(Exception e){
                    e.printStackTrace();
                }
                html += "</table>";

                html += "</div>";




                // CUARTO TAB




                html += "<div id='tab13' class='jm_tabHTML' style='display:none;width:750px;height:"+_altBody+"px;'>";

                /* retenciones */
                //html += "<div><a href='javascript:void(0);' onclick=\"fac_addRetencion();\">Insertar retenci&oacute;n </a></div>";
                html += "<table cellpadding='0' cellspacing='0'>" +
                        "<TH class='jm_TH' width='200'>Nro. RETENCION</TH>" +
                        "<TH class='jm_TH' width='90'>VALOR</TH>" +
                        "<TH class='jm_TH' width='90'>PAGO FACT.</TH>" +
                        "<TH class='jm_TH' width='20'>&nbsp;</TH></tr></table>"+
                        "<TABLE class='jm_tabla' cellspacing='1' cellpadding='0' id='tblRet'>";
                try{
                    i=0;
                    //String id_arqueo_caja_otro = "";
                    String num_retencion = "";
                    String valor = "";
                    String tipo_pago = "";
                    String ax_tipo_pago = "";
                    if(id.compareTo("-1")!=0){
                        ResultSet rsArqueoRet = objArqueoCaja.getArqueoCajaRetenciones(id);
                        while(rsArqueoRet.next()){
                            //id_arqueo_caja_otro = (rsArqueoRet.getString("id_arqueo_caja_otro")!=null) ? rsArqueoRet.getString("id_arqueo_caja_otro") : "1";
                            num_retencion = (rsArqueoRet.getString("num_retencion")!=null) ? rsArqueoRet.getString("num_retencion") : "";
                            valor = (rsArqueoRet.getString("valor_retencion")!=null) ? rsArqueoRet.getString("valor_retencion") : "0";
                            tipo_pago = (rsArqueoRet.getString("tipo_pago_factura")!=null) ? rsArqueoRet.getString("tipo_pago_factura") : "";
                            ax_tipo_pago = this.getTipoPago(tipo_pago);
                            html += "<tr id='rr"+i+"' class='jm_filaPar'>";
                            html += "<td style='width:200px'>"
                                    + "<input id='ret"+i+"' name='ret"+i+"' type='text' class='inpTbl' style='width:195px' onkeypress=\"_evaluar(event, '0123456789-');\" onkeyup=\"fac_sumRetenciones();\" " +
                                    "onfocus=\"this.select();\" value='"+num_retencion+"' /></td>";
                            html += "<td style='width:90px'><input id='vRet"+i+"' name='vRet"+i+"' type='text' class='inpTbl' size='9' onkeypress=\"_evaluar(event, '0123456789.');\" onkeyup=\"fac_sumArqueo();\" " +
                                    "onfocus=\"this.select();\" value='"+valor+"' /></td>";
                            html += "<td style='width:90px'>"+ax_tipo_pago+"</td>";
                            html += "<td style='width:20px'>&nbsp;</td>";
                            html += "</tr>";
                            i++;
                        }
                        rsArqueoRet.close();
                    }else{
                        String ret_num_serie = "";
                        FacturaVenta objFactura = new FacturaVenta(this._ip, this._puerto, this._db, usuario, clave);
                        ResultSet rsArqueoRet = objFactura.getRetenciones(id_punto_emision, ultima_fecha, ultima_hora);
                        while(rsArqueoRet.next()){
                            ret_num_serie = (rsArqueoRet.getString("ret_num_serie")!=null) ? rsArqueoRet.getString("ret_num_serie") : "";
                            num_retencion = (rsArqueoRet.getString("ret_num_retencion")!=null) ? rsArqueoRet.getString("ret_num_retencion") : "";
                            valor = (rsArqueoRet.getString("ret_impuesto_retenido")!=null) ? rsArqueoRet.getString("ret_impuesto_retenido") : "0";
                            tipo_pago = (rsArqueoRet.getString("tipo_pago")!=null) ? rsArqueoRet.getString("tipo_pago") : "";
                            num_retenciones++;
                            if(tipo_pago.compareTo("e") == 0){
                                valor_retenciones += Float.parseFloat(valor);
                            }
                            ax_tipo_pago = this.getTipoPago(tipo_pago);
                            html += "<tr id='rr"+i+"' class='jm_filaPar'>";
                            html += "<td style='width:200px'>"
                                    + "<input id='ret"+i+"' name='ret"+i+"' type='text' class='inpTbl' style='width:195px' onkeypress=\"_evaluar(event, '0123456789-');\" onkeyup=\"fac_sumRetenciones();\" " +
                                    "onfocus=\"this.select();\" value='"+ret_num_serie+"-"+num_retencion+"' /></td>";
                            html += "<td style='width:90px'><input id='vRet"+i+"' name='vRet"+i+"' type='text' class='inpTbl' size='9' onkeypress=\"_evaluar(event, '0123456789.');\" onkeyup=\"fac_sumArqueo();\" " +
                                    "onfocus=\"this.select();\" value='"+valor+"' /></td>";
                            html += "<td style='width:90px'><input id='frpg"+i+"' name='frpg"+i+"' type='hidden' value='"+tipo_pago+"' />"+ax_tipo_pago+"</td>";
                            html += "<td style='width:20px'><div class='jm_icoCon' style='background-position:-545px -18px;' title='Eliminar' onclick=\"_R('rr"+i+"');fac_sumArqueo();\">&nbsp;</div></td>";
                            html += "</tr>";
                            i++;
                        }
                        rsArqueoRet.close();
                        objFactura.cerrar();
                    }
                }catch(Exception e){
                    e.printStackTrace();
                }
                html += "</table>";

                html += "</div>";




                //  QUINTO TAB




                html += "<div id='tab14' class='jm_tabHTML' style='display:none;width:750px;height:"+_altBody+"px;'>";


                


                //String fecha = Fecha.getFecha("SQL");
                String num_comprobante = "";
                String fecha_actual = fecha;
                String fecha_proceso = fecha;
                String total = "0";
                String detalle = "";
                ResultSet nota = objComprobanteDiario.getComprobanteDiario(id_comprobante_diario);
                ResultSet notaDetalle = objComprobanteDiario.getComprobanteDiarioDetalle(id_comprobante_diario);
                if(id.compareTo("-1")!=0){
                    try{
                        if(nota.next()){
                            num_comprobante = (nota.getString("num_comprobante")!=null) ? nota.getString("num_comprobante") : num_comprobante;
                            //num_asiento = (nota.getString("num_asiento")!=null) ? nota.getString("num_asiento") : "";
                            fecha_actual = (nota.getString("fecha_actual")!=null) ? Fecha.ISOaSQL(nota.getString("fecha_actual")) : fecha;
                            fecha_proceso = (nota.getString("fecha_proceso")!=null) ? Fecha.ISOaSQL(nota.getString("fecha_proceso")) : fecha;
                            total = (nota.getString("total")!=null) ? nota.getString("total") : "0";
                            detalle = (nota.getString("detalle")!=null) ? nota.getString("detalle") : "";
                            nota.close();
                        }
                        objComprobanteDiario.cerrar();
                    }catch(Exception e){
                        e.printStackTrace();
                    }
                }
                html += "<table width='100%'>" +
                        "<tr><td align='center'><strong>C O M P R O B A N T E &nbsp;&nbsp; D E &nbsp;&nbsp; D I A R I O</strong></td>" +
                        "<td align='right' width='100'>Nro. "+num_comprobante+"</td></tr>" +
                        "</table>" +

                        "<table width='100%'>" +

                        "<tr><td>Fecha actual: "+fecha_actual+"</td>" +
                        "<td align='right'>Fecha de proceso: <input id='crr_fp' name='fp' type='text' size='10' value='"+fecha_proceso+"' readOnly />" +
                        "<input type='button' value='...' onClick=\"SelectorFecha.crear('fp', 'SQL');\" /></td></tr>" +

                        "<tr><td colspan='2'>Diferencia: <span class='marca'>*</span> <input id='va' value='"+Math.abs(Float.parseFloat(diferencia))+"' type='text' size='10' readOnly /></td></tr>" +

                        "<tr><td colspan='2'>Concepto: <span class='marca'>*</span> <input id='dt' name='dt' value='"+detalle+"' type='text' size='80' maxlength='200' /></td></tr>" +

                        "</table>" +


                        "<table cellpadding='0' cellspacing='0'>" +
                        "<TH class='jm_TH' width='200'>CODIGO - CUENTA</TH>" +
                        "<TH class='jm_TH' width='90'>DEBE</TH>" +
                        "<TH class='jm_TH' width='90'>HABER</TH>" +
                        "<TH class='jm_TH' width='20'>&nbsp;&nbsp;</TH></tr></table>" +

                        "<DIV style='overflow:auto;width:420px;height:98px;'>" +
                        "<TABLE class='jm_tabla' cellspacing='1' cellpadding='0' id='tblD'>";
                
                i=0;
                try{
                    String id_plan_cuenta = "";
                    String debe = "";
                    String haber = "";
                    
                    while(notaDetalle.next()){
                        id_plan_cuenta = (notaDetalle.getString("id_plan_cuenta")!=null) ? notaDetalle.getString("id_plan_cuenta") : "1";
                        debe = (notaDetalle.getString("debe")!=null) ? ((notaDetalle.getDouble("debe")>0)?notaDetalle.getString("debe"):"") : "";
                        haber = (notaDetalle.getString("haber")!=null) ? ((notaDetalle.getDouble("haber")>0)?notaDetalle.getString("haber"):"") : "";
                        html += "<tr id='r"+i+"' class='jm_filaPar'>";
                        html += "<td style='width:200px'>" + DatosDinamicos.combo(cuentas, "PC"+i, id_plan_cuenta, "", 190) + "</td>";
                        html += "<td class='columna der' style='width:90px'><input id='d"+i+"' name='d"+i+"' type='text' class='inpTbl' size='9' readonly value='"+debe+"' /></td>";
                        html += "<td class='columna der' style='width:90px'><input id='h"+i+"' name='h"+i+"' type='text' class='inpTbl' size='9' readonly value='"+haber+"' /></td>";
                        html += "<td class='columna caja' style='width:20px'>&nbsp;</td>";
                        html += "</tr>";
                        i++;
                    }
                    
                }catch(Exception e){
                    e.printStackTrace();
                }
                if(i==0){
                    html += "<tr id='r0' class='jm_filaPar'>";
                    html += "<td style='width:200px'>" + DatosDinamicos.combo(cuentas, "PC0", id_plan_cuenta_diferencia_caja, "", 190) + "</td>";
                    html += "<td style='width:90px'><input id='de0' name='de0' type='text' class='inpTbl' size='9' readonly /></td>";
                    html += "<td style='width:90px'><input id='h0' name='h0' type='text' class='inpTbl' size='9' readonly /></td>";
                    html += "<td style='width:20px'>&nbsp;</td>";
                    html += "</tr>";
                    html += "<tr id='r1' class='jm_filaPar'>";
                    html += "<td style='width:200px'>" + DatosDinamicos.combo(cuentas, "PC1", id_plan_cuenta_caja, "", 190) + "</td>";
                    html += "<td style='width:90px'><input id='d1' name='d1' type='text' class='inpTbl' size='9' readonly /></td>";
                    html += "<td style='width:90px'><input id='h1' name='h1' type='text' class='inpTbl' size='9' /></td>";
                    html += "<td style='width:20px'>&nbsp;</td>";
                    html += "</tr>";
                }

                html += "</table></div></div>";




                //  SEXTO TAB




                html += "<div id='tab15' class='jm_tabHTML' style='display:none;width:750px;height:"+_altBody+"px;'>";



                //String fecha = Fecha.getFecha("SQL");
                num_comprobante = "";
                fecha_actual = fecha;
                fecha_proceso = fecha;
                total = "0";
                detalle = "Cierre de caja No. "+num_documento+" ("+cajero+") del día " + Addons.getTextFecha(fecha) + " con depósito No. ";
                nota = objComprobanteDiario.getComprobanteDiario(id_comprobante_diario_cierre);
                notaDetalle = objComprobanteDiario.getComprobanteDiarioDetalle(id_comprobante_diario_cierre);
                if(id.compareTo("-1")!=0){
                    try{
                        if(nota.next()){
                            num_comprobante = (nota.getString("num_comprobante")!=null) ? nota.getString("num_comprobante") : num_comprobante;
                            //num_asiento = (nota.getString("num_asiento")!=null) ? nota.getString("num_asiento") : "";
                            fecha_actual = (nota.getString("fecha_actual")!=null) ? Fecha.ISOaSQL(nota.getString("fecha_actual")) : fecha;
                            fecha_proceso = (nota.getString("fecha_proceso")!=null) ? Fecha.ISOaSQL(nota.getString("fecha_proceso")) : fecha;
                            total = (nota.getString("total")!=null) ? nota.getString("total") : "0";
                            detalle = (nota.getString("detalle")!=null) ? nota.getString("detalle") : "";
                            nota.close();
                        }
                        objComprobanteDiario.cerrar();
                    }catch(Exception e){
                        e.printStackTrace();
                    }
                }

                String id_plan_cuenta_caja_cierre = objPuntoEmision.getIdCaja(cajero);

                html += "<table width='100%'>" +
                        "<tr><td align='center'><strong>C O M P R O B A N T E &nbsp;&nbsp; D E &nbsp;&nbsp; D I A R I O</strong></td>" +
                        "<td align='right' width='100'>Nro. "+num_comprobante+"</td></tr>" +
                        "</table>" +

                        "<table width='100%'>" +

                        "<tr><td>Fecha actual: "+fecha_actual+"</td>" +
                        "<td align='right'>Fecha de proceso: <input id='crr_fp' name='crr_fp' type='text' size='10' value='"+fecha_proceso+"' readOnly />" +
                        "<input type='button' value='...' onClick=\"SelectorFecha.crear('crr_fp', 'SQL');\" /></td></tr>" +

                        "<tr><td colspan='2'>Concepto: <span class='marca'>*</span> <input id='crr_dt' name='crr_dt' value='"+detalle+"' type='text' size='110' maxlength='200' /></td></tr>" +

                        "</table>" +


                        "<table cellpadding='0' cellspacing='0'>" +
                        "<TH class='jm_TH' width='200'>CODIGO - CUENTA</TH>" +
                        "<TH class='jm_TH' width='90'>DEBE</TH>" +
                        "<TH class='jm_TH' width='90'>HABER</TH>" +
                        "<TH class='jm_TH' width='20'>&nbsp;&nbsp;</TH></tr></table>" +

                        "<DIV style='overflow:auto;width:420px;height:98px;'>" +
                        "<TABLE class='jm_tabla' cellspacing='1' cellpadding='0' id='tblD'>";

                i=0;
                try{
                    String id_plan_cuenta = "";
                    String debe = "";
                    String haber = "";
                    while(notaDetalle.next()){
                        id_plan_cuenta = (notaDetalle.getString("id_plan_cuenta")!=null) ? notaDetalle.getString("id_plan_cuenta") : "1";
                        debe = (notaDetalle.getString("debe")!=null) ? ((notaDetalle.getDouble("debe")>0)?notaDetalle.getString("debe"):"") : "";
                        haber = (notaDetalle.getString("haber")!=null) ? ((notaDetalle.getDouble("haber")>0)?notaDetalle.getString("haber"):"") : "";
                        html += "<tr id='r"+i+"' class='jm_filaPar'>";
                        html += "<td style='width:200px'>" + DatosDinamicos.combo(cuentas, "crr_PC"+i, id_plan_cuenta, "", 190) + "</td>";
                        html += "<td class='columna der' style='width:90px'><input id='crr_d"+i+"' name='crr_d"+i+"' type='text' class='inpTbl' size='9' readonly value='"+debe+"' /></td>";
                        html += "<td class='columna der' style='width:90px'><input id='crr_h"+i+"' name='crr_h"+i+"' type='text' class='inpTbl' size='9' readonly value='"+haber+"' /></td>";
                        html += "<td class='columna caja' style='width:20px'>&nbsp;</td>";
                        html += "</tr>";
                        i++;
                    }

                    notaDetalle.close();
                    cuentas.close();
                }catch(Exception e){
                    e.printStackTrace();
                }
                if(i==0){
                    html += "<tr id='r0' class='jm_filaPar'>";
                    html += "<td style='width:200px'>" + DatosDinamicos.combo(cuentas, "crr_PC0", id_plan_cuenta_banco, "", 190) + "</td>";
                    html += "<td style='width:90px'><input id='crr_de0' name='crr_de0' type='text' class='inpTbl' size='9' readonly value='"+total_caja+"' /></td>";
                    html += "<td style='width:90px'><input id='crr_h0' name='crr_h0' type='text' class='inpTbl' size='9' readonly /></td>";
                    html += "<td style='width:20px'>&nbsp;</td>";
                    html += "</tr>";
                    html += "<tr id='r1' class='jm_filaPar'>";
                    html += "<td style='width:200px'>" + DatosDinamicos.combo(cuentas, "crr_PC1", id_plan_cuenta_caja_cierre, "", 190) + "</td>";
                    html += "<td style='width:90px'><input id='crr_d1' name='crr_d1' type='text' class='inpTbl' size='9' readonly /></td>";
                    html += "<td style='width:90px'><input id='crr_h1' name='crr_h1' type='text' class='inpTbl' size='9' readonly value='"+total_caja+"' /></td>";
                    html += "<td style='width:20px'>&nbsp;</td>";
                    html += "</tr>";
                }

                html += "</table></div>";
                if(!anulado && !cerrado){
                    html += "<input type='button' value='Realizar cierre de caja' onclick=\"fac_arqueoCerrar()\" />";
                }
                html += "</div>";





                html += "</div>"; // de los tabs


                if(id.compareTo("-1")!=0){
                    html += "<input type='button' value='Re-imprimir' onclick=\"imprimir('pdfArqueoCaja?id="+id+"');\" />";
                    /*if(objRol.getAcceso(id_rol, "arqueoAnular")){
                        html += " &nbsp; <input type='button' value='Anular' onclick='fac_arqueoAnular("+id+");' />";
                    }*/
                }
                if(!anulado){
                    //if(id.compareTo("-1")==0 || (id.compareTo("-1")!=0 && objRol.getAcceso(id_rol, "arqueoEditar"))){
                    if(id.compareTo("-1")==0){
                        //html += "<input type='submit' value='Guardar' onmousedown=\"_('imp').value=0;\" /> &nbsp; ";
                        html += "<input id='btnGr' type='submit' value='Guardar e Imprimir' onmousedown=\"_('imp').value=1;\" onclick=\"this.disabled=true;\" />";
                    }else{
                        if(objRol.getAcceso(id_rol, "arqueoAnular") && !cerrado){
                            html += "<input type='button' value='Anular' onclick=\"fac_arqueoAnular("+id+")\" />";
                        }
                    }
                }else{
                    html += "<span class='marca'>ARQUEO DE CAJA ANULADO</span>";
                }
                html += "</div></div></form>";


                html = html.replace("<<num_cheques>>", String.valueOf(num_cheques));
                html = html.replace("<<valor_cheques>>", Addons.redondear(String.valueOf(valor_cheques)));
                html = html.replace("<<num_comp_pagos>>", String.valueOf(num_comp_pagos));
                html = html.replace("<<valor_comp_pagos>>", Addons.redondear(String.valueOf(valor_comp_pagos)));
                html = html.replace("<<num_retenciones>>", String.valueOf(num_retenciones));
                html = html.replace("<<valor_retenciones>>", Addons.redondear(String.valueOf(valor_retenciones)));
                html = html.replace("<<total_caja>>", Addons.redondear(String.valueOf(0)));
                html = html.replace("<<diferencia>>", Addons.redondear(String.valueOf(0)));
                //html = html.replace("<<total_caja>>", Addons.redondear(String.valueOf(-valor_retenciones)));
                //html = html.replace("<<diferencia>>", Addons.redondear(String.valueOf(saldo_caja-valor_retenciones)));
            }

            
            out.print(html);

        } finally {
            //conf.setValor("bloqueo_contabilizacion", "false");
            //conf.cerrar();
            objPuntoEmision.cerrar();
            objRol.cerrar();
            objArqueoCaja.cerrar();
            objComprobanteDiario.cerrar();
            objComprobanteIngreso.cerrar();
            out.close();
        }
    }

    public String getTipoPago(String tipo_pago){
        String ret = "";
        switch(tipo_pago.charAt(0)){
            case 'e':
                ret = "efectivo";
            break;
            case 'c':
                ret = "cheque";
            break;
            case 'p':
                ret = "dep&oacute;sito";
            break;
            case 't':
                ret = "transferencia";
            break;
            case 'j':
                ret = "tarjeta";
            break;
            case 'd':
                ret = "cr&eacute;dito";
            break;
        }
        return ret;
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
