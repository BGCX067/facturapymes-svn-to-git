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
import java.sql.Connection;

import javax.servlet.*;
import javax.servlet.http.*;
import jm.adm.clas.Configuracion;
import jm.inf.clas.Radius;
import jm.seg.clas.Auditoria;

/**
 *
 * @author Jorge
 */
public class frmConfiguracionGuardar extends HttpServlet {
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
        
        response.setContentType("text/html;charset=UTF-8");
        response.setHeader("Pragma", "no-cache"); 
        response.setHeader("Expires", "Mon, 01 Jan 2001 00:00:01 GMT"); 
        response.setHeader("Cache-Control", "no-store"); 
        response.setHeader("Cache-Control", "must-revalidate"); 
        response.setHeader("Cache-Control", "no-cache");
        
        PrintWriter out = response.getWriter();
        int modulo = Integer.parseInt(request.getParameter("m"));
        Configuracion conf = new Configuracion(this._ip, this._puerto, this._db, usuario, clave);
        String audi = "ACTUALIZACION DE LA INFORMACION DE CONFIGURACION";
        String msg = "Información de configuración guardada satisfactoriamente.";
        switch(modulo){
            case 1:
                conf.setValor("ruc", request.getParameter("ruc").toUpperCase());
                conf.setValor("razon_social", request.getParameter("razon_social").toUpperCase());
                conf.setValor("nombre_comercial", request.getParameter("nombre_comercial").toUpperCase());
                conf.setValor("num_resolucion", request.getParameter("num_resolucion").toUpperCase());
                conf.setValor("oblga_contabilidad", request.getParameter("oblga_contabilidad"));
                conf.setValor("dir_matriz", request.getParameter("dir_matriz").toUpperCase());
                conf.setValor("telf_matriz", request.getParameter("telf_matriz"));
                conf.setValor("fax", request.getParameter("fax"));
                conf.setValor("email", request.getParameter("email"));
                conf.setValor("rep_nombre", request.getParameter("rep_nombre"));
                conf.setValor("rep_cargo", request.getParameter("rep_cargo"));
                conf.setValor("rep_legal", request.getParameter("rep_legal"));
                conf.setValor("num_ident", request.getParameter("num_ident"));
                conf.setValor("ruc_contador", request.getParameter("ruc_contador"));
                conf.setValor("id_sucursal_matriz", request.getParameter("id_sucursal_matriz"));
                conf.setValor("tipo_emision", request.getParameter("tipo_emision"));
                audi += " EN EL MODULO DE FORMULARIOS DIMM";
            break;

            case 2:
                //conf.setValor("p_iva1", request.getParameter("p_iva1"));
                //conf.setValor("p_iva2", request.getParameter("p_iva2"));
                conf.setValor("dia_declaracion", request.getParameter("dia_declaracion"));
                String sec_com_dia = request.getParameter("sec_com_dia");
                String sec_com_ing = request.getParameter("sec_com_ing");
                String sec_com_egr = request.getParameter("sec_com_egr");
                String apr2_nombre = request.getParameter("apr2_nombre");
                String apr2_cargo = request.getParameter("apr2_cargo");
                sec_com_dia = (sec_com_dia.compareTo("")!=0)?sec_com_dia:"1";
                sec_com_ing = (sec_com_ing.compareTo("")!=0)?sec_com_ing:"1";
                sec_com_egr = (sec_com_egr.compareTo("")!=0)?sec_com_egr:"1";
                /*conf.setValor("caja", request.getParameter("caja"));
                conf.setValor("bancos", request.getParameter("bancos"));
                conf.setValor("capital", request.getParameter("capital"));
                conf.setValor("uti_per_ejercicio", request.getParameter("uti_per_ejercicio"));
                conf.setValor("resumen_rent_gast", request.getParameter("resumen_rent_gast"));*/
                conf.setValor("sec_com_dia", sec_com_dia);
                conf.setValor("sec_com_ing", sec_com_ing);
                conf.setValor("sec_com_egr", sec_com_egr);
                conf.setValor("apr2_nombre", apr2_nombre);
                conf.setValor("apr2_cargo", apr2_cargo);
                audi += " EN EL MODULO DE CONTABILIDAD";
            break;

            case 3:
                /*conf.setValor("prov_servicios", request.getParameter("prov_servicios"));*/
                conf.setValor("inventario", request.getParameter("inventario"));
                conf.setValor("costo_ventas", request.getParameter("costo_ventas"));
                conf.setValor("propina", request.getParameter("propina"));
                /*conf.setValor("iva_comp", request.getParameter("iva_comp"));
                conf.setValor("iva_comp_serv", request.getParameter("iva_comp_serv"));
                conf.setValor("iva_comp_bien", request.getParameter("iva_comp_bien"));
                conf.setValor("iva_comp_gasto", request.getParameter("iva_comp_gasto"));*/
                conf.setValor("devo_comp", request.getParameter("devo_comp"));
                conf.setValor("desc_comp", request.getParameter("desc_comp"));
                conf.setValor("cxp", request.getParameter("cxp"));
                conf.setValor("chequeEmitidoNoCobrado", request.getParameter("chequeEmitidoNoCobrado"));
                audi += " EN EL MODULO DE INVENTARIO";
            break;
            
            case 31:
                conf.setValor("importa_transito", request.getParameter("importa_transito"));
                conf.setValor("imp_iva1", request.getParameter("imp_iva1"));
                /*conf.setValor("fob", request.getParameter("fob"));
                conf.setValor("flete", request.getParameter("flete"));
                conf.setValor("seguro", request.getParameter("seguro"));
                conf.setValor("ajuste", request.getParameter("ajuste"));
                conf.setValor("cif", request.getParameter("cif"));
                conf.setValor("valor_aduana", request.getParameter("valor_aduana"));
                conf.setValor("valor_em", request.getParameter("valor_em"));
                conf.setValor("der_especifico", request.getParameter("der_especifico"));
                conf.setValor("imp_ice", request.getParameter("imp_ice"));
                
                conf.setValor("antidumping", request.getParameter("antidumping"));
                conf.setValor("der_consular", request.getParameter("der_consular"));
                conf.setValor("sobretiempo_petr", request.getParameter("sobretiempo_petr"));
                conf.setValor("ad_barril_crudo", request.getParameter("ad_barril_crudo"));
                conf.setValor("tasa_modernizacion", request.getParameter("tasa_modernizacion"));
                conf.setValor("tasa_control", request.getParameter("tasa_control"));
                conf.setValor("tasa_almacenaje", request.getParameter("tasa_almacenaje"));
                conf.setValor("multa", request.getParameter("multa"));
                conf.setValor("interes", request.getParameter("interes"));
                conf.setValor("imp_salida_divisa", request.getParameter("imp_salida_divisa"));
                conf.setValor("fodinfa", request.getParameter("fodinfa"));
                conf.setValor("corpei", request.getParameter("corpei"));
                conf.setValor("otros", request.getParameter("otros"));
                conf.setValor("salvaguarda", request.getParameter("salvaguarda"));*/
                conf.setValor("cxppe", request.getParameter("cxppe"));
                audi += " EN EL MODULO DE IMPORTACIONES";
            break;

            case 4:
                //conf.setValor("ventas", request.getParameter("ventas"));
                conf.setValor("base_gen_prefactura", request.getParameter("base_gen_prefactura"));
                conf.setValor("dia_prefacturas", request.getParameter("dia_prefacturas"));
                conf.setValor("dia_cortes", request.getParameter("dia_cortes"));
                conf.setValor("dia_cortes_creditos", request.getParameter("dia_cortes_creditos"));
                conf.setValor("max_diferencia", request.getParameter("max_diferencia"));
                conf.setValor("instalacion", request.getParameter("instalacion"));
                conf.setValor("plan", request.getParameter("plan"));
                //conf.setValor("croquis", request.getParameter("croquis"));
                conf.setValor("cxc", request.getParameter("cxc"));
                //conf.setValor("iva_cobrado", request.getParameter("iva_cobrado"));
                conf.setValor("desc_venta", request.getParameter("desc_venta"));
                conf.setValor("devo_venta", request.getParameter("devo_venta"));
                conf.setValor("chequeRecibidoNoCobrado", request.getParameter("chequeRecibidoNoCobrado"));
                conf.setValor("gastos_bancos", request.getParameter("gastos_bancos"));
                conf.setValor("uti_per_ejercicio", request.getParameter("uti_per_ejercicio"));
                conf.setValor("uti_venta_activos", request.getParameter("uti_venta_activos"));
                conf.setValor("per_venta_activos", request.getParameter("per_venta_activos"));
                audi += " EN EL MODULO DE FACTURACION";
            break;

            case 5:
                conf.setValor("nom_p_iess", request.getParameter("nom_p_iess"));
                conf.setValor("nom_smvg", request.getParameter("nom_smvg"));
                conf.setValor("nom_gasto_sueldos", request.getParameter("nom_gasto_sueldos"));
                conf.setValor("nom_anticipo_sueldos", request.getParameter("nom_anticipo_sueldos"));
                conf.setValor("nom_bene_sociales", request.getParameter("nom_bene_sociales"));
                conf.setValor("nom_ap_pat_iess", request.getParameter("nom_ap_pat_iess"));
                conf.setValor("nom_HE_50", request.getParameter("nom_HE_50"));
                conf.setValor("nom_HE_100", request.getParameter("nom_HE_100"));
                conf.setValor("nom_rem_x_pagar", request.getParameter("nom_rem_x_pagar"));
                conf.setValor("nom_ap_per_x_pagar", request.getParameter("nom_ap_per_x_pagar"));
                conf.setValor("nom_ap_pat_x_pagar", request.getParameter("nom_ap_pat_x_pagar"));
                conf.setValor("nom_iece", request.getParameter("nom_iece"));
                conf.setValor("nom_secap", request.getParameter("nom_secap"));
                conf.setValor("nom_13_sueldo", request.getParameter("nom_13_sueldo"));
                conf.setValor("nom_14_sueldo", request.getParameter("nom_14_sueldo"));
                conf.setValor("nom_vacaciones", request.getParameter("nom_vacaciones"));
                //conf.setValor("nom_banco", request.getParameter("nom_banco"));
                audi += " EN EL MODULO DE NOMINA";
            break;

            case 7:
                conf.setValor("rep_pie", request.getParameter("rep_pie"));
                audi += " EN EL MODULO DE REPORTES";
            break;

            case 8:
                conf.setValor("path_pg_dump", request.getParameter("path_pg_dump"));
                conf.setValor("path_resguardo", request.getParameter("path_resguardo"));
                audi += " EN EL MODULO DE RESPALDOS DE LA BASE DE DATOS";
            break;

            case 9:
                conf.setValor("mail_svr", request.getParameter("mail_svr"));
                conf.setValor("mail_origen", request.getParameter("mail_origen"));
                audi += " EN EL MODULO DE CORREOS";
            break;

            case 10:
                conf.setValor("rad_db", request.getParameter("rad_db"));
                conf.setValor("rad_ip", request.getParameter("rad_ip"));
                conf.setValor("rad_puerto", request.getParameter("rad_puerto"));
                conf.setValor("rad_usuario", request.getParameter("rad_usuario"));
                conf.setValor("rad_clave", request.getParameter("rad_clave"));
                msg = "Conexión establecida satisfactoriamente.";
                try{
                    String rad_db = conf.getValor("rad_db");
                    String rad_ip = conf.getValor("rad_ip");
                    String rad_puerto = conf.getValor("rad_puerto");
                    String rad_usuario = conf.getValor("rad_usuario");
                    String rad_clave = conf.getValor("rad_clave");
                    Radius dbRadius = new Radius(rad_ip, Integer.parseInt(rad_puerto), rad_db, rad_usuario, rad_clave);
                    Connection con = dbRadius.getConexion();
                    if(con == null){
                        msg = "Error de conexión.";
                    }
                    dbRadius.cerrar();
                }catch(Exception e){
                    msg = "Ha ocurrido un error al tratar de conectarse al FREERADIUS.";
                    e.printStackTrace();
                }
                audi += " EN EL MODULO DEL FREE RADIUS";
            break;

            case 11:
                conf.setValor("airC_ip", request.getParameter("airC_ip"));
                conf.setValor("airC_puerto", request.getParameter("airC_puerto"));
                conf.setValor("airC_usuario", request.getParameter("airC_usuario"));
                conf.setValor("airC_clave", request.getParameter("airC_clave"));
                audi += " EN EL MODULO DEL AIRCONTROL";
            break;
                
            case 12:
                conf.setValor("sftp_ip", request.getParameter("sftp_ip"));
                conf.setValor("sftp_puerto", request.getParameter("sftp_puerto"));
                conf.setValor("sftp_usuario", request.getParameter("sftp_usuario"));
                conf.setValor("sftp_clave", request.getParameter("sftp_clave"));
                audi += " EN EL MODULO DEL SERVIDOR SFTP";
            break;
                    
        }
          
        Auditoria auditoria = new Auditoria(this._ip, this._puerto, this._db, usuario, clave);
        auditoria.setRegistro(request, audi);
        auditoria.cerrar();
            
        try {    
            out.print("msg»"+msg);
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
