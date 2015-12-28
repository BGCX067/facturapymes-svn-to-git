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
import jm.adm.clas.Configuracion;
import jm.cnt.clas.LibroDiario;
import jm.seg.clas.Auditoria;
import jm.cnt.clas.ComprobanteDiario;
import jm.cnt.clas.ComprobanteIngreso;
import jm.cnt.clas.ComprobanteEgreso;
import jm.fac.clas.Contrato;
import jm.fac.clas.Instalacion;
//import jm.inv.clas.NotaCreditoCompra;

/**
 *
 * @author Jorge
 */
public class reversarDocumento extends HttpServlet {
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
        
        Configuracion conf = new Configuracion(this._ip, this._puerto, this._db, usuario, clave);
        String bloqueo_libros = "";
        while(true){
            bloqueo_libros = conf.getValor("bloqueo_libros");
            if(bloqueo_libros.compareTo("false")==0) {
                conf.setValor("bloqueo_libros", "true");
                break;
            }
            try{
                Thread.sleep(500);
            }catch(InterruptedException e){
                e.printStackTrace();
            }
        }

        String tabla = request.getParameter("t");
        String id = request.getParameter("id");

        String campo = tabla.replace("tbl_", "id_");

        LibroDiario objLibroDiario = new LibroDiario(this._ip, this._puerto, this._db, usuario, clave);
        String r = "msg»No ha sido posible anular el documento, puede que el documento ya está anulado.";
        String aux_r = "";
        String num_asiento = "";
        String txtAuditoria = "";
        boolean reversion = false;
        boolean anulado = false;
        try {
            ResultSet consulta = objLibroDiario.consulta("SELECT anulado FROM " + tabla + " WHERE " + campo + "=" + id);
            try{
                if(consulta.next()){
                    anulado = (consulta.getString("anulado")!=null) ? consulta.getBoolean("anulado") : false;
                    consulta.close();
                }
            }catch(Exception e){e.printStackTrace();}

            if(!anulado){
                r = "msg»Ha ocurrido un error inesperado, por favor inténtelo más tarde o contáctese con el administrador del sistema.";
                ResultSet registro = null;
                if(tabla.compareTo("tbl_comprobante_diario")==0){
                    ComprobanteDiario objComprobanteDiario = new ComprobanteDiario(this._ip, this._puerto, this._db, usuario, clave);
                    registro = objComprobanteDiario.getComprobanteDiario(id);
                    try{
                        if(registro.next()){
                            num_asiento = ((registro.getString("num_asiento")!=null) ? registro.getString("num_asiento") : "-1");
                            aux_r = "vta»cmp^msg»Se ha anulado el comprobante de diario No. " + ((registro.getString("num_comprobante")!=null) ? registro.getString("num_comprobante") : "");
                            txtAuditoria = "ANULACION DEL COMPROBANTE DE DIARIO No. " + ((registro.getString("num_comprobante")!=null) ? registro.getString("num_comprobante") : "");
                            registro.close();
                        }
                    }catch(Exception e){e.printStackTrace();}
                    objComprobanteDiario.cerrar();
                    if(objLibroDiario.reversarAsiento(num_asiento, tabla, campo, id)){
                        reversion=true;
                    }
                }else if(tabla.compareTo("tbl_comprobante_ingreso")==0){
                        ComprobanteIngreso objComprobanteIngreso = new ComprobanteIngreso(this._ip, this._puerto, this._db, usuario, clave);
                        registro = objComprobanteIngreso.getComprobanteIngreso(id);
                        try{
                            if(registro.next()){
                                num_asiento = ((registro.getString("num_asiento")!=null) ? registro.getString("num_asiento") : "-1");
                                aux_r = "vta»cmp^msg»Se ha anulado el comprobante de ingreso No. " + ((registro.getString("num_comprobante")!=null) ? registro.getString("num_comprobante") : "");
                                txtAuditoria = "ANULACION DEL COMPROBANTE DE INGRESO No. " + ((registro.getString("num_comprobante")!=null) ? registro.getString("num_comprobante") : "");
                                registro.close();
                            }
                        }catch(Exception e){e.printStackTrace();}
                        if(objComprobanteIngreso.anular(id)){
                            reversion=true;
                        }
                        objComprobanteIngreso.cerrar();
                }else if(tabla.compareTo("tbl_comprobante_egreso")==0){
                        ComprobanteEgreso objComprobanteEgreso = new ComprobanteEgreso(this._ip, this._puerto, this._db, usuario, clave);
                        registro = objComprobanteEgreso.getComprobanteEgreso(id);
                        try{
                            if(registro.next()){
                                num_asiento = ((registro.getString("num_asiento")!=null) ? registro.getString("num_asiento") : "-1");
                                aux_r = "vta»cmp^msg»Se ha anulado el comprobante de egreso No. " + ((registro.getString("num_comprobante")!=null) ? registro.getString("num_comprobante") : "");
                                txtAuditoria = "ANULACION DEL COMPROBANTE DE EGRESO No. " + ((registro.getString("num_comprobante")!=null) ? registro.getString("num_comprobante") : "");
                                registro.close();
                            }
                        }catch(Exception e){e.printStackTrace();}
                        if(objComprobanteEgreso.anular(id)){
                            reversion=true;
                        }
                        objComprobanteEgreso.cerrar();
                /*}else if(tabla.compareTo("tbl_nota_credito_compra")==0){
                        NotaCreditoCompra objNotaCreditoCompra = new NotaCreditoCompra(this._ip, this._puerto, this._db, usuario, clave);
                        registro = objNotaCreditoCompra.getNotaCredito(id);
                        try{
                            if(registro.next()){
                                num_asiento = ((registro.getString("id_nota_contabilidad")!=null) ? registro.getString("id_nota_contabilidad") : "-1");
                                aux_r = "Se ha anulado la nota de débito No. " + ((registro.getString("num_nota_credito_compra")!=null) ? registro.getString("num_nota_credito_compra") : "");
                                txtAuditoria = "ANULACION DE LA NOTA DE DEBITO EN COMPRA No. " + ((registro.getString("num_nota_credito_compra")!=null) ? registro.getString("num_nota_credito_compra") : "");
                                registro.close();
                            }
                        }catch(Exception e){e.printStackTrace();}
                        if(objNotaCreditoCompra.reversar(id)){
                            reversion=true;
                        }
                        objNotaCreditoCompra.cerrar();*/
                }else if(tabla.compareTo("tbl_factura_compra_pago")==0){
                        String idComprobante = request.getParameter("idC");
                        ComprobanteEgreso objComprobanteEgreso = new ComprobanteEgreso(this._ip, this._puerto, this._db, usuario, clave);
                        registro = objComprobanteEgreso.getComprobanteEgreso(idComprobante);
                        try{
                            if(registro.next()){
                                aux_r = "vta»vta_nc^msg»Se ha anulado el comprobante de egreso de pago No. " + ((registro.getString("num_comprobante")!=null) ? registro.getString("num_comprobante") : "");
                                txtAuditoria = "ANULACION DEL COMPROBANTE DE EGRESO No. " + ((registro.getString("num_comprobante")!=null) ? registro.getString("num_comprobante") : "");
                                registro.close();
                            }
                        }catch(Exception e){e.printStackTrace();}
                        if(objComprobanteEgreso.anularPago(id)){
                            reversion=true;
                        }
                        objComprobanteEgreso.cerrar();
                }else if(tabla.compareTo("tbl_factura_venta_cobro")==0){
                        String idComprobante = request.getParameter("idC");
                        ComprobanteIngreso objComprobanteIngreso = new ComprobanteIngreso(this._ip, this._puerto, this._db, usuario, clave);
                        registro = objComprobanteIngreso.getComprobanteIngreso(idComprobante);
                        try{
                            if(registro.next()){
                                aux_r = "vta»vta_nc^msg»Se ha anulado el comprobante de ingreso de pago No. " + ((registro.getString("num_comprobante")!=null) ? registro.getString("num_comprobante") : "");
                                txtAuditoria = "ANULACION DEL COMPROBANTE DE INGRESO No. " + ((registro.getString("num_comprobante")!=null) ? registro.getString("num_comprobante") : "");
                                registro.close();
                            }
                        }catch(Exception e){e.printStackTrace();}
                        if(objComprobanteIngreso.anularCobro(id)){
                            reversion=true;
                        }
                        objComprobanteIngreso.cerrar();
                }else if(tabla.compareTo("tbl_rol_pago_rubro_empleado")==0){
                        String idComprobante = request.getParameter("idC");
                        ComprobanteEgreso objComprobanteEgreso = new ComprobanteEgreso(this._ip, this._puerto, this._db, usuario, clave);
                        registro = objComprobanteEgreso.getComprobanteEgreso(idComprobante);
                        try{
                            if(registro.next()){
                                aux_r = "vta»cmp^msg»Se ha anulado el comprobante de egreso de pago No. " + ((registro.getString("num_comprobante")!=null) ? registro.getString("num_comprobante") : "");
                                txtAuditoria = "ANULACION DEL COMPROBANTE DE EGRESO No. " + ((registro.getString("num_comprobante")!=null) ? registro.getString("num_comprobante") : "");
                                registro.close();
                            }
                        }catch(Exception e){e.printStackTrace();}
                        if(objComprobanteEgreso.anularAnticipoRolPago(id)){
                            reversion=true;
                        }
                        objComprobanteEgreso.cerrar();
                }else if(tabla.compareTo("tbl_contrato")==0){
                        Contrato objContrato = new Contrato(this._ip, this._puerto, this._db, usuario, clave);
                        registro = objContrato.getContrato(id);
                        try{
                            if(registro.next()){
                                String id_cliente = registro.getString("id_cliente")!=null ? registro.getString("id_cliente") : "";
                                String id_sucursal = registro.getString("id_sucursal")!=null ? registro.getString("id_sucursal") : "";
                                String num = registro.getString("num_contrato")!=null ? registro.getString("num_contrato") : "";
                                aux_r = "fun»fac_clienteEditar("+id_cliente+");^msg»Se ha anulado el contrato No. " + id_sucursal + "-" + num;
                                txtAuditoria = "ANULACION DEL CONTRATO No. " + id_sucursal + "-" + num;
                                registro.close();
                            }
                        }catch(Exception e){e.printStackTrace();}
                        if(objContrato.anular(id)){
                            reversion=true;
                        }
                        objContrato.cerrar();
                }else if(tabla.compareTo("tbl_instalacion")==0){
                        Instalacion objInstalacion = new Instalacion(this._ip, this._puerto, this._db, usuario, clave);
                        registro = objInstalacion.getInstalacion(id);
                        try{
                            if(registro.next()){
                                String id_sucursal = registro.getString("id_sucursal")!=null ? registro.getString("id_sucursal") : "";
                                String num = registro.getString("num_instalacion")!=null ? registro.getString("num_instalacion") : "";
                                aux_r = "err»0^vta»cmp^msg»Se ha anulado el documento de instalación No. " + id_sucursal + "-" + num;
                                txtAuditoria = "ANULACION DEL DOCUMENTO DE INSTALACION No. " + id_sucursal + "-" + num;
                                registro.close();
                            }
                        }catch(Exception e){e.printStackTrace();}
                        if(objInstalacion.anular(id)){
                            reversion=true;
                        }
                        objInstalacion.cerrar();
                }


                if(reversion){
                    Auditoria auditoria = new Auditoria(this._ip, this._puerto, this._db, usuario, clave);
                    auditoria.setRegistro(request, txtAuditoria);
                    auditoria.cerrar();
                    r = "err»0^"+aux_r;
                }
            }

            out.print(r);

        } finally {
            conf.setValor("bloqueo_libros", "false");
            conf.cerrar();
            objLibroDiario.cerrar();
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
