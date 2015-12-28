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
import jm.fac.clas.ArqueoCaja;
import jm.seg.clas.Auditoria;

/**
 *
 * @author Jorge
 */
public class frmArqueoGuardar extends HttpServlet {
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
        int id_punto_emision = (Integer)sesion.getAttribute("id_punto_emision");

        response.setContentType("text/html;charset=UTF-8");
        response.setHeader("Pragma", "no-cache");
        response.setHeader("Expires", "Mon, 01 Jan 2001 00:00:01 GMT");
        response.setHeader("Cache-Control", "no-store");
        response.setHeader("Cache-Control", "must-revalidate");
        response.setHeader("Cache-Control", "no-cache");
        PrintWriter out = response.getWriter();

        ArqueoCaja objArqueoCaja = new ArqueoCaja(this._ip, this._puerto, this._db, usuario, clave);

        try {
            String r = "fun»_('btnGr').disabled=false;^msg»Ha ocurrido un error inesperado, por favor, vuelva a intentarlo más tarde o " +
                "contáctese con el administrador del sistema para mayor información.";

            String WHERE = request.getParameter("WHERE");
            String p = request.getParameter("p") != null ? request.getParameter("p") : "0";
            String id = request.getParameter("id");
            String imprimir = request.getParameter("imp");
            //String id_comprobante_ingreso = request.getParameter("idCI");
            long num_documento = objArqueoCaja.getNumArqueo();
            String fecha = request.getParameter("fecha");
            String hora = request.getParameter("hora");
            String saldo_caja = request.getParameter("saldo_caja");
            String valor_efectivo = request.getParameter("valor_efectivo");
            String num_cheques = request.getParameter("num_cheques");
            String valor_cheques = request.getParameter("valor_cheques");
            String num_comp_pagos = request.getParameter("num_comp_pagos");
            String valor_comp_pagos = request.getParameter("valor_comp_pagos");
            String num_retenciones = request.getParameter("num_retenciones");
            String valor_retenciones = request.getParameter("valor_retenciones");
            String total_caja = request.getParameter("total_caja");
            String diferencia = request.getParameter("diferencia");
            String tipo_diferencia = request.getParameter("tipo_diferencia")!=null ? request.getParameter("tipo_diferencia") : "";

            //String num_comprobante = request.getParameter("nc");
            String fecha_proceso = request.getParameter("fp");
            String descripcion = request.getParameter("dt")+ " según arqueo No." + num_documento;
            String id_plan_cuenta0 = request.getParameter("PC0");
            String id_plan_cuenta1 = request.getParameter("PC1");

            saldo_caja = (saldo_caja.compareTo("")!=0) ? saldo_caja : "0";
            valor_efectivo = (valor_efectivo.compareTo("")!=0) ? valor_efectivo : "0";
            num_cheques = (num_cheques.compareTo("")!=0) ? num_cheques : "0";
            valor_cheques = (valor_cheques.compareTo("")!=0) ? valor_cheques : "0";
            total_caja = (total_caja.compareTo("")!=0) ? total_caja : "0";
            tipo_diferencia = (tipo_diferencia.compareTo("")!=0) ? tipo_diferencia : "0";

            String denominaciones = "";
            String cantidades = "";
            String totales = "";
            String ax_cant = "";
            String ax_total = "";
            for(int i=0; i<100; i++){
                if(request.getParameter("den"+i)!=null){
                    ax_cant = request.getParameter("c"+i);
                    ax_cant = ax_cant.compareTo("")!=0 ? ax_cant : "1";
                    ax_total = request.getParameter("t"+i);
                    if(ax_cant.compareTo("")!=0 && ax_total.compareTo("")!=0){
                        if(Float.parseFloat(ax_cant)>=0 && Float.parseFloat(ax_total)>0){
                            denominaciones += request.getParameter("den"+i) + ",";
                            cantidades += ax_cant + ",";
                            totales += ax_total + ",";
                        }
                    }
                }
            }
            if(denominaciones.compareTo("")!=0){
                denominaciones = denominaciones.substring(0, denominaciones.length()-1);
                cantidades = cantidades.substring(0, cantidades.length()-1);
                totales = totales.substring(0, totales.length()-1);
            }

            String bancos = "";
            String num_cheque = "";
            String valores = "";
            String efectivizado = "";
            String ax_efect = "";
            for(int i=0; i<=1000; i++){
                if(request.getParameter("bc"+i)!=null){
                    bancos += request.getParameter("bc"+i) + ",";
                    num_cheque += request.getParameter("ch"+i) + ",";
                    valores += request.getParameter("v"+i) + ",";
                    ax_efect = request.getParameter("ef"+i);
                    efectivizado += (ax_efect.compareTo("")!=0 ? ax_efect : " ") + ",";
                }
            }
            if(bancos.compareTo("")!=0){
                bancos = bancos.substring(0, bancos.length()-1);
                num_cheque = num_cheque.substring(0, num_cheque.length()-1);
                valores = valores.substring(0, valores.length()-1);
                efectivizado = efectivizado.substring(0, efectivizado.length()-1);
            }


            String num_comps_pagos = "";
            String valores_comps_pagos = "";
            for(int i=0; i<=1000; i++){
                if(request.getParameter("doc"+i)!=null){
                    num_comps_pagos += request.getParameter("doc"+i) + ",";
                    valores_comps_pagos += request.getParameter("vOtr"+i) + ",";
                }
            }
            if(num_comps_pagos.compareTo("")!=0){
                num_comps_pagos = num_comps_pagos.substring(0, num_comps_pagos.length()-1);
                valores_comps_pagos = valores_comps_pagos.substring(0, valores_comps_pagos.length()-1);
            }


            String nums_retenciones = "";
            String valores_retenciones = "";
            String tipo_pago_facturas = "";
            for(int i=0; i<=1000; i++){
                if(request.getParameter("ret"+i)!=null){
                    nums_retenciones += request.getParameter("ret"+i) + ",";
                    valores_retenciones += request.getParameter("vRet"+i) + ",";
                    tipo_pago_facturas += request.getParameter("frpg"+i) + ",";
                }
            }
            if(nums_retenciones.compareTo("")!=0){
                nums_retenciones = nums_retenciones.substring(0, nums_retenciones.length()-1);
                valores_retenciones = valores_retenciones.substring(0, valores_retenciones.length()-1);
                tipo_pago_facturas = tipo_pago_facturas.substring(0, tipo_pago_facturas.length()-1);
            }

            if(id.compareTo("-1")==0){
                r="fun»_('btnGr').disabled=false;^msg»Por favor, ejecute el proceso de contabilización y revise que el asiento de cierre de caja esté realizado para poder ingresar un nuevo arqueo de caja.";
                if(!objArqueoCaja.arqueoPendiente(usuario)){
                    String id_arqueo_id_compDiario = objArqueoCaja.insertar(id_sucursal, id_punto_emision, usuario, num_documento, fecha, hora, saldo_caja,
                            valor_efectivo, num_cheques, valor_cheques, num_comp_pagos, valor_comp_pagos, num_retenciones, valor_retenciones, tipo_pago_facturas,
                            total_caja, diferencia, tipo_diferencia, denominaciones, cantidades, totales, bancos, num_cheque, valores, efectivizado,
                            num_comps_pagos, valores_comps_pagos, nums_retenciones, valores_retenciones, String.valueOf(num_documento), fecha_proceso, descripcion, id_plan_cuenta0, id_plan_cuenta1);
                    if(id_arqueo_id_compDiario.compareTo("-1:-1") != 0){
                        Auditoria auditoria = new Auditoria(this._ip, this._puerto, this._db, usuario, clave);
                        auditoria.setRegistro(request, "REGISTRO DE ARQUEO DE CAJA Nro. "+num_documento);
                        auditoria.cerrar();
                        String vec[] = id_arqueo_id_compDiario.split(":");
                        //String comprobante = vec[1].compareTo("-1")!=0 ? "imprimir('pdfComprobanteDiario?id="+vec[1]+"');" : "";
                        r = "err»0^vta»cmp^fun»" + (imprimir.compareTo("1")==0 ? "imprimir('pdfArqueoCaja?id="+vec[0]+"');" : "")+
                            "^tbl»"+objArqueoCaja.paginar("vta_arqueo_caja", "id_arqueo_caja,cajero,num_documento,toDateSql(fecha),hora,total,diferencia,txt_anulado", WHERE, Integer.parseInt(p), 100);
                    }
                }
            }
            out.print(r);

        } finally {
            objArqueoCaja.cerrar();
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
