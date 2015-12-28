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
import jm.web.Addons;
import jm.web.Fecha;
import jm.web.DataBase;

/**
 *
 * @author Jorge
 */
public class REOC extends HttpServlet {
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
        
        int anio = Integer.valueOf(request.getParameter("a"));
        int mes = Integer.valueOf(request.getParameter("m"));
        String mes1 = (mes<10) ? "0"+mes : String.valueOf(mes);
        int fin_mes = Fecha.getUltimoDiaMes(anio, mes);

        response.setContentType("text/xml;");
        response.setHeader("Content-disposition", "attachment; filename="+this._db.replace("db_", "")+"_REOC_"+Addons.getMesSRI(mes)+anio+".xml;");
        response.setHeader("Pragma", "no-cache");
        response.setHeader("Expires", "Mon, 01 Jan 2001 00:00:01 GMT");
        response.setHeader("Cache-Control", "no-store");
        response.setHeader("Cache-Control", "must-revalidate");
        response.setHeader("Cache-Control", "no-cache");
        PrintWriter out = response.getWriter();


        Configuracion conf = new Configuracion(this._ip, this._puerto, this._db, usuario, clave);
        String ruc = conf.getValor("ruc");
        String razon_social = conf.getValor("razon_social").toUpperCase();
        if(razon_social.length()>=60){
            razon_social = razon_social.substring(0, 59);
        }


        DataBase objDB = new DataBase(this._ip, this._puerto, this._db, usuario, clave);

        ResultSet compras = objDB.consulta("select * from vta_factura_compra where fecha_compra " +
                "between '"+anio+"-"+mes+"-01' and '"+anio+"-"+mes+"-"+fin_mes+"' order by razon_social,num_factura;");

        try {
            out.println("<?xml version=\"1.0\" encoding=\"ISO-8859-1\" standalone=\"yes\"?>");
            out.println("<reoc>");
            out.println("<numeroRuc>"+ruc+"</numeroRuc>");
            out.println("<anio>"+anio+"</anio>");
            out.println("<mes>"+mes1+"</mes>");

            /*     C O M P R A S  */
            out.println("<compras>");
            try{
                String id_factura_compra = "0";
                String serie = "-";
                String iva_0 = "0.00";
                ResultSet retenciones = null;
                ResultSet retencionesDetalle = null;
                if(compras.next()){
                    compras.beforeFirst();
                    
                    while(compras.next()){
                        id_factura_compra = (compras.getString("id_factura_compra")!=null)?compras.getString("id_factura_compra"):"0";
                        serie = (compras.getString("serie_factura")!=null)?compras.getString("serie_factura"):"-";
                        iva_0 = (compras.getString("iva_0")!=null)?compras.getString("iva_0"):"0.00";
                        String vec_serie [] = serie.split("-");
                        out.println("<detalleCompras>");
                        out.println("<tpIdProv>01</tpIdProv>");
                        out.println("<idProv>"+((compras.getString("ruc")!=null)?compras.getString("ruc"):"")+"</idProv>");
                        out.println("<tipoComp>1</tipoComp>");
                        out.println("<aut>"+((compras.getString("autorizacion")!=null)?compras.getString("autorizacion"):"")+"</aut>");
                        out.println("<estab>"+((vec_serie[0].compareTo("")!=0)?vec_serie[0]:"000")+"</estab>");
                        if(vec_serie.length>1){
                            out.println("<ptoEmi>"+((vec_serie[1].compareTo("")!=0)?vec_serie[1]:"000")+"</ptoEmi>");
                        }else{
                            out.println("<ptoEmi>000</ptoEmi>");
                        }
                        out.println("<sec>"+((compras.getString("num_factura")!=null)?compras.getString("num_factura"):"")+"</sec>");
                        out.println("<fechaEmiCom>"+Addons.toFechaSQL((compras.getString("fecha_compra")!=null)?compras.getString("fecha_compra"):"")+"</fechaEmiCom>");
                        
                        String id_retencion_compra = "-1";
                        String ret_autorizacion = "000";
                        String ret_num_serie = "-";
                        String ret_num_retencion = "0";
                        String ret_fecha_emision = "0000-00-00";
                        try{
                            retenciones = objDB.consulta("select * from tbl_retencion_compra where anulado=false and id_factura_compra="+id_factura_compra+";");
                            if(retenciones.next()){
                                id_retencion_compra = (retenciones.getString("id_retencion_compra")!=null)?retenciones.getString("id_retencion_compra"):"-1";
                                ret_autorizacion = (retenciones.getString("ret_autorizacion")!=null)?retenciones.getString("ret_autorizacion"):"000";
                                ret_num_serie = (retenciones.getString("ret_num_serie")!=null)?retenciones.getString("ret_num_serie"):"-";
                                ret_num_retencion = (retenciones.getString("ret_num_retencion")!=null)?retenciones.getString("ret_num_retencion"):"0";
                                ret_fecha_emision = Addons.toFechaSQL((retenciones.getString("ret_fecha_emision")!=null)?retenciones.getString("ret_fecha_emision"):"0000-00-00");
                                retenciones.close();
                            }
                        }catch(Exception e){
                            e.printStackTrace();
                        }
                        
                        if(id_retencion_compra.compareTo("-1")!=0){
                            int count = 0;
                            out.println("<air>");
                            try{
                                retencionesDetalle = objDB.consulta("select TR.*, R.id_retencion_compra_detalle, R.id_retencion_compra, R.base_imponible, R.valor_retenido " +
                                                        "from tbl_retencion_compra_detalle as R inner join tbl_tabla_impuesto as TR on TR.id_tabla_impuesto=R.id_tabla_impuesto " +
                                                        "where TR.impuesto='RENTA' and R.id_retencion_compra="+id_retencion_compra+";");
                                count = objDB.getFilas(retencionesDetalle);
                                while(retencionesDetalle.next()){
                                    out.println("<detalleAir>");
                                    out.println("<codRetAir>"+((retencionesDetalle.getString("codigo_impuesto")!=null)?retencionesDetalle.getString("codigo_impuesto"):"")+"</codRetAir>");
                                    out.println("<porcentaje>"+((retencionesDetalle.getString("porcentaje_retencion")!=null)?retencionesDetalle.getString("porcentaje_retencion"):"")+"</porcentaje>");
                                    out.println("<base0>"+iva_0+"</base0>");

                                    out.println("<baseGrav>"+((retencionesDetalle.getString("base_imponible")!=null)?retencionesDetalle.getString("base_imponible"):"")+"</baseGrav>");
                                    out.println("<baseNoGrav>0.00</baseNoGrav>");

                                    out.println("<valRetAir>"+((retencionesDetalle.getString("valor_retenido")!=null)?retencionesDetalle.getString("valor_retenido"):"")+"</valRetAir>");
                                    out.println("</detalleAir>");
                                }
                                retencionesDetalle.close();
                            }catch(Exception e){
                                e.printStackTrace();
                            }
                            out.println("</air>");

                            if(count>0){
                                String vec_serie_ret [] = ret_num_serie.split("-");
                                out.println("<autRet>"+ret_autorizacion+"</autRet>");
                                out.println("<estabRet>"+((vec_serie_ret[0].compareTo("")!=0)?vec_serie_ret[0]:"000")+"</estabRet>");
                                if(vec_serie_ret.length>1){
                                    out.println("<ptoEmiRet>"+((vec_serie_ret[1].compareTo("")!=0)?vec_serie_ret[1]:"000")+"</ptoEmiRet>");
                                }else{
                                    out.println("<ptoEmiRet>000</ptoEmiRet>");
                                }
                                out.println("<secRet>"+ret_num_retencion+"</secRet>");
                                out.println("<fechaEmiRet>"+ret_fecha_emision+"</fechaEmiRet>");
                            }
                        }
                        out.println("</detalleCompras>");
                    }
                }
                compras.close();
                
            }catch(Exception e){
                e.printStackTrace();
            }
            out.println("</compras>");
            out.print("</reoc>");

        } finally {
            conf.cerrar();
            objDB.cerrar();
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
