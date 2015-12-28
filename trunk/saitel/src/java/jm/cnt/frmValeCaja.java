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

package jm.cnt;

import java.io.*;

import javax.servlet.*;
import javax.servlet.http.*;
import java.sql.ResultSet;
import jm.adm.clas.Sucursal;
import jm.web.DatosDinamicos;
import jm.seg.clas.Rol;
import jm.cnt.clas.ValeCaja;
import jm.web.Fecha;
import jm.cnt.clas.PlanCuenta;
import jm.cnt.clas.ReposicionCajaChica;
import jm.nom.clas.Empleado;

/**
 *
 * @author Jorge
 */
public class frmValeCaja extends HttpServlet {
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
        int id_sucursal = (Integer)sesion.getAttribute("sucursal");
        String usuario = (String)sesion.getAttribute("usuario");
        String clave = (String)sesion.getAttribute("clave");

        response.setContentType("text/html;charset=UTF-8");
        response.setHeader("Pragma", "no-cache");
        response.setHeader("Expires", "Mon, 01 Jan 2001 00:00:01 GMT");
        response.setHeader("Cache-Control", "no-store");
        response.setHeader("Cache-Control", "must-revalidate");
        response.setHeader("Cache-Control", "no-cache");
        PrintWriter out = response.getWriter();

        String id = request.getParameter("id");

        String id_plan_cuenta_caja_chica = "";
        
        Rol objRol = new Rol(this._ip, this._puerto, this._db, usuario, clave);
        ReposicionCajaChica objReposicionCajaChica = new ReposicionCajaChica(this._ip, this._puerto, this._db, usuario, clave);
        ValeCaja objValeCaja = new ValeCaja(this._ip, this._puerto, this._db, usuario, clave);
        Sucursal objSucursal = new Sucursal(this._ip, this._puerto, this._db, usuario, clave);
        
        try{
            ResultSet rsSucursal = objSucursal.getSucursal(String.valueOf(id_sucursal));
            if(rsSucursal.next()){
                id_plan_cuenta_caja_chica = rsSucursal.getString("id_plan_cuenta_caja_chica")!=null ? rsSucursal.getString("id_plan_cuenta_caja_chica") : "";
                rsSucursal.close();
            }
        }catch(Exception e){
            e.printStackTrace();
        }

        
        String saldo_caja = objReposicionCajaChica.getSaldoCajaChica(id_plan_cuenta_caja_chica);

        try {
            String custodio = "";
            Empleado objEmpleado = new Empleado(this._ip, this._puerto, this._db, usuario, clave);
            try{
                ResultSet rsEmpleados = objEmpleado.getResponsableCajaChica(id_sucursal);
                if(rsEmpleados.next()){
                    custodio = rsEmpleados.getString("empleado")!=null ? rsEmpleados.getString("empleado") : "";
                    rsEmpleados.close();
                }
            }catch(Exception e){
                e.printStackTrace();
            }finally{
                objEmpleado.cerrar();
            }

            String id_reposicion_caja_chica="-1";
            String num_vale_caja = String.valueOf(objValeCaja.getNumValeCaja(id_sucursal));
            String fecha = Fecha.getFecha("SQL");
            String concepto="";
            String importe = "";
            String la_suma_de = "";
            String solicitante = "";
            String id_plan_cuenta_gasto = "";
            String motivo_anulacion = "";
            boolean anulado = false;

            if(id.compareTo("-1")!=0){
                try{
                    ResultSet rsValeCaja = objValeCaja.getValeCaja(id);
                    if(rsValeCaja.next()){
                        id_reposicion_caja_chica = (rsValeCaja.getString("id_reposicion_caja_chica")!=null) ? rsValeCaja.getString("id_reposicion_caja_chica") : "-1";
                        num_vale_caja = ((rsValeCaja.getString("id_sucursal")!=null) ? rsValeCaja.getString("id_sucursal") : "") + "-" + ((rsValeCaja.getString("num_vale_caja")!=null) ? rsValeCaja.getString("num_vale_caja") : "");
                        fecha = (rsValeCaja.getString("fecha")!=null) ? Fecha.ISOaSQL(rsValeCaja.getString("fecha")) : "";
                        custodio = (rsValeCaja.getString("custodio")!=null) ? rsValeCaja.getString("custodio") : "";
                        concepto = (rsValeCaja.getString("concepto")!=null) ? rsValeCaja.getString("concepto") : "";
                        importe = (rsValeCaja.getString("importe")!=null) ? rsValeCaja.getString("importe") : "0";
                        la_suma_de = (rsValeCaja.getString("la_suma_de")!=null) ? rsValeCaja.getString("la_suma_de") : "";
                        solicitante = (rsValeCaja.getString("solicitante")!=null) ? rsValeCaja.getString("solicitante") : "";
                        id_plan_cuenta_gasto = (rsValeCaja.getString("id_plan_cuenta_gasto")!=null) ? rsValeCaja.getString("id_plan_cuenta_gasto") : "";
                        motivo_anulacion = (rsValeCaja.getString("motivo_anulacion")!=null) ? rsValeCaja.getString("motivo_anulacion") : "";
                        anulado = (rsValeCaja.getString("anulado")!=null) ? rsValeCaja.getBoolean("anulado") : false;
                        rsValeCaja.close();
                    }
                }catch(Exception e){
                    e.printStackTrace();
                }
            }

            PlanCuenta objPlanCuenta = new PlanCuenta(this._ip, this._puerto, this._db, usuario, clave);
            ResultSet cuentas = objPlanCuenta.getTablas("5");
            objPlanCuenta.cerrar();


            String html = "obj»vta1_html^foc»importe^frm»<form action='frmValeCajaGuardar' onsubmit='return cnt_vale_cajaGuardar(this)' autocomplete='off'>"
                    + "<input type='hidden' id='imp' name='imp' value='0' />"
                    + "<input type='hidden' id='id' name='id' value='"+id+"' />"
                    + "<input type='hidden' id='saldo_caja' name='saldo_caja' value='"+saldo_caja+"' />"
                    + "<h3 class='H3'>V A L E &nbsp;&nbsp; D E &nbsp;&nbsp; C A J A &nbsp;&nbsp; C H I C A</h3>"
                    + "<table>"
                    + "<tr><td width='340'>&nbsp;</td><td width='60'>Saldo: </td><td width='140'>"+saldo_caja+"</td></tr>"
                    + "<tr><td>Nro. <span class='marca'>*</span><input id='num_vale_caja' name='num_vale_caja' "+((id.compareTo("-1")!=0)?"readonly":"")+" value='"+num_vale_caja+"' readonly type='text' size='6' onkeypress='_numero(event);' /></td>" +
                    "<td>Importe: $ </td><td><input type='text' id='importe' name='importe' value='"+importe+"' onkeypress=\"_evaluar(event, '0123456789.');\" onkeyup=\"_('la_suma_de').value=FloatToStr(this.value);\" /></td></tr></table>" +

                    "<table width='100%'><tr><td>Fecha: </td><td><input type='text' id='fecha' name='fecha' value='"+fecha+"' readonly /></td></tr>"
                    + "<tr><td>Custodio: </td><td><input type='text' id='custodio' name='custodio' size='60' value='"+custodio+"' readonly /></td></tr>"
                    + "<tr><td>La suma de: </td><td><input type='text' id='la_suma_de' name='la_suma_de' size='80' maxlength='200' value='"+la_suma_de+"' /></td></tr>"
                    + "<tr><td>Sirvase entregar a: </td><td><input type='text' id='solicitante' name='solicitante' size='80' maxlength='60' value='"+solicitante+"' /></td></tr>"
                    + "<tr><td>En concepto de: </td><td><input type='text' id='concepto' name='concepto' size='80' maxlength='200' value='"+concepto+"' /></td></tr>"
                    + "<tr><td>Aplicaci&oacute;n contable: </td><td>" + DatosDinamicos.combo(cuentas, "id_plan_cuenta_gasto", id_plan_cuenta_gasto, "", 425) + "</td></tr>";

            if(!anulado){
                    if(id.compareTo("-1")!=0 && objRol.getAcceso(id_rol, "caja_chicaAnular")){
                        html += "<tr><td colspan='2'><input type='button' value='Re-imprimir' onclick=\"imprimir('pdfValeCaja?id="+id+"');\" />";
                        /*long fecha_sistema = Fecha.getTimeStamp(Fecha.getFecha("SQL"));
                        long fecha_asiento = Fecha.getTimeStamp(fecha);
                        if(fecha_sistema==fecha_asiento){*/
                        if(!objReposicionCajaChica.cerrada(id_reposicion_caja_chica)){
                            html += " &nbsp; <input type='button' value='Anular' id='btn_valAnular' onclick=\"cnt_vale_cajaAnular("+id+");\" />";
                        }
                        html += "</td></tr>";
                    }
                    if(id.compareTo("-1")==0){
                        html += "<tr><td colspan='2'><input type='submit' value='Guardar' onmousedown=\"_('imp').value=0;\" /> &nbsp; ";
                        html += "<input type='submit' value='Guardar e Imprimir' onmousedown=\"_('imp').value=1;\" /></td></tr>";
                    }
            }else{
                html += "<tr><td>Motivo anulaci&oacute;n: </td><td>"+motivo_anulacion+"</td></tr>";
                html += "<tr><td colspan='2'><div class='marca'>VALE DE CAJA CHICA ANULADO</div></td></tr>";
            }
            html += "</table></form>";


            out.print(html);

        } finally {
            objSucursal.cerrar();
            objRol.cerrar();
            objValeCaja.cerrar();
            objReposicionCajaChica.cerrar();
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
