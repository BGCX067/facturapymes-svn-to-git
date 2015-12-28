/**
** @version 1.0
** @package FACTURAPYMES.
* @author Jorge Washington Mueses Cevallos.
* @copyright Copyright (C) 2011 por Jorge Mueses. Todos los derechos reservados.
* @license http://www.gnu.org/copyleft/gpl.html GNU/GPL.
** FACTURAPYMES es un software de libre distribuciÃ³n, que puede ser
* copiado y distribuido bajo los tÃ©rminos de la Licencia
* Attribution-NonCommercial-NoDerivs 3.0 Unported,
* de acuerdo con la publicada por la CREATIVE COMMONS CORPORATION.
*/

package jm.fac.xls;

import java.io.*;

import javax.servlet.*;
import javax.servlet.http.*;
import java.sql.ResultSet;
import jm.web.Addons;

import jm.web.DataBase;
import jm.web.Fecha;

/**
 *
 * @author Jorge
 */
public class GenerarDebitosTarjetas extends HttpServlet {
    private String _ip = null;
    private int _puerto = 5432;
    private String _db = null;
    private String _ftp = null;

    public void init(ServletConfig config) throws ServletException
    {
        this._ip = config.getServletContext().getInitParameter("_IP");
        this._puerto = Integer.parseInt(config.getServletContext().getInitParameter("_PUERTO"));
        this._db = config.getServletContext().getInitParameter("_DB");
        this._ftp = config.getServletContext().getInitParameter("_FTP_PICHINCHA");  
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
        
        String comision = request.getParameter("com");
        int anio = Integer.parseInt(request.getParameter("anio"));
        int mes = Integer.parseInt(request.getParameter("mes"));
        String fecha_ini = anio + "-" + mes + "-01";
        String fecha_fin = anio + "-" + mes + "-" + Fecha.getUltimoDiaMes(anio, mes);

        DataBase objDB = new DataBase(this._ip, this._puerto, this._db, usuario, clave);

        //this._ftp = "/home/jorge/Documentos/FTP/";
        
        try{
            objDB.consulta("select proc_calcularprefacturaspendientes();");
            
            String hoy = Fecha.getFecha("ISO");
            StringBuilder xls = new StringBuilder();
            
            // PARA TARJETAS DINERS Y DISCOVER
            try{
                ResultSet registros = objDB.consulta("select C.tarjeta_credito_caduca, num_cuenta, "
                        + "F.id_sucursal::varchar || id_instalacion::varchar as id_instalacion, "
                        + "subtotal-descuento as subtotal, iva_2, total + "+comision+" as total, tipo_tarjeta_credito "
                        + "from tbl_cliente as C inner join tbl_factura_venta as F on C.id_cliente=F.id_cliente "
                        + "where fecha_emision between '"+fecha_ini+"' and '"+fecha_fin+"' and F.deuda>0 and num_cuenta <>'' "
                        + "and C.forma_pago='TAR' and C.tarjeta_credito_caduca>now()::date and F.anulado=false "
                        + "and tipo_tarjeta_credito in('1', '2') "
                        + "order by tipo_tarjeta_credito");

                //String tarjeta_credito_caduca = "";
                String num_cuenta = "";
                String id_instalacion = "";
                String subtotal = "";
                String iva_2 = "";
                String total = "";
                String tipo_tarjeta_credito = "";
                String ax_tipo_tarjeta_credito = "";
                
                int i = 0;
                float sum_subtotal = 0;
                float sum_iva_2 = 0;
                float sum_total = 0;
                boolean bandera = true;
                while(registros.next()){
                    //tarjeta_credito_caduca = registros.getString("tarjeta_credito_caduca")!=null ? registros.getString("tarjeta_credito_caduca") : hoy;
                    num_cuenta = registros.getString("num_cuenta")!=null ? registros.getString("num_cuenta") : "0";
                    id_instalacion = registros.getString("id_instalacion")!=null ? registros.getString("id_instalacion") : "0";
                    subtotal = registros.getString("subtotal")!=null ? registros.getString("subtotal") : "0.00";
                    iva_2 = registros.getString("iva_2")!=null ? registros.getString("iva_2") : "0";
                    total = registros.getString("total")!=null ? registros.getString("total") : "0";
                    tipo_tarjeta_credito = registros.getString("tipo_tarjeta_credito")!=null ? registros.getString("tipo_tarjeta_credito") : "1";
                    if(ax_tipo_tarjeta_credito.compareTo("")==0){
                        ax_tipo_tarjeta_credito = tipo_tarjeta_credito;
                    }

                    if(tipo_tarjeta_credito.compareTo(ax_tipo_tarjeta_credito)!=0 && i>0){
                        xls.append("300003449563104"); //  TIPO DE REGISTRO  -   CUENTA
                        xls.append(hoy.replace("-", ""));   //  FECHA (AAAAMMDD)
                        xls.append(Addons.rellenarCeros(i, 5)); //  TOTAL REGISTROS 
                        xls.append(Addons.rellenarCeros( Addons.truncar(sum_subtotal).replace(".", ""), 12));   //  VALOR TOTAL DEL CONSUMO 
                        xls.append(Addons.rellenarCeros( Addons.truncar(sum_iva_2).replace(".", ""), 9));   //  VALOR TOTAL IVA 
                        xls.append("000000000");    //  VALOR TOTAL ICE 
                        xls.append(Addons.rellenarCeros( Addons.truncar(sum_total).replace(".", ""), 12));  //  VALOR TOTAL TOTAL
                        
                        try{
                            String nombre = this.getNombreTarjeta(ax_tipo_tarjeta_credito) + "_" + hoy + ".txt";
                            FileWriter fichero = new FileWriter(this._ftp + nombre);
                            PrintWriter pw = new PrintWriter(fichero);
                            pw.print(xls);
                            if(pw != null){
                                pw.close();
                            }
                            if (fichero != null){
                               fichero.close();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        } 
                        
                        xls.delete(0, xls.length());
                        i=0;
                        ax_tipo_tarjeta_credito = tipo_tarjeta_credito;
                        sum_subtotal = 0;
                        sum_iva_2 = 0;
                        sum_total = 0;
                        bandera = true;
                    }
                    
                    if(bandera){
                        xls.append("10000000000");  //  TIPO DE REGISTRO  -  BANCO - SUCURSAL BANCARIA
                        xls.append(hoy.replace("-", ""));   // FECHA DE ENVIO
                        xls.append("0001533522\n"); //  CODIGO DE ESTABLECIMIENTO
                        bandera = false;
                    }
                    
                    xls.append("2");    //  TIPO DE REGISTRO
                    xls.append(Addons.rellenarCeros(num_cuenta, 20));   //  NUMERO DE TARJETA
                    xls.append(Addons.rellenarCeros(id_instalacion, 10));   //  NUMERO DE CONTRATO - CODIGO UNICO
                    xls.append(Addons.rellenarCeros(subtotal.replace(".", ""), 12));    //  CONSUMO
                    xls.append(Addons.rellenarCeros(iva_2.replace(".", ""), 6));    //  IVA
                    xls.append("000000");   //  ICE
                    xls.append(Addons.rellenarCeros(total.replace(".", ""), 12));   //  TOTAL
                    xls.append("000");  //  CODIGO DE PROCESO
                    xls.append(Addons.rellenarCeros(subtotal.replace(".", ""), 14));    //  Capital 12%
                    xls.append("00000000000000\n"); //  Capital 0% - campo opcional
                    
                    sum_subtotal += Float.parseFloat(subtotal);
                    sum_iva_2 += Float.parseFloat(iva_2);
                    sum_total += Float.parseFloat(total);
                    i++;
                }
                
                if(i>0){
                    xls.append("300003449563104 "); //  TIPO DE REGISTRO  -   CUENTA
                    xls.append(hoy.replace("-", ""));   //  FECHA (AAAAMMDD)
                    xls.append(Addons.rellenarCeros(i, 5)); //  TOTAL REGISTROS 
                    xls.append(Addons.rellenarCeros( Addons.truncar(sum_subtotal).replace(".", ""), 12));   //  VALOR TOTAL DEL CONSUMO 
                    xls.append(Addons.rellenarCeros( Addons.truncar(sum_iva_2).replace(".", ""), 9));   //  VALOR TOTAL IVA 
                    xls.append("000000000");    //  VALOR TOTAL ICE 
                    xls.append(Addons.rellenarCeros( Addons.truncar(sum_total).replace(".", ""), 12));  //  VALOR TOTAL TOTAL

                    try{
                        String nombre = this.getNombreTarjeta(tipo_tarjeta_credito) + "_" + hoy + ".txt";
                        FileWriter fichero = new FileWriter(this._ftp + nombre);
                        PrintWriter pw = new PrintWriter(fichero);
                        pw.print(xls);
                        if(pw != null){
                            pw.close();
                        }
                        if (fichero != null){
                           fichero.close();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    } 
                }
                registros.close();
            }catch(Exception e){
                e.printStackTrace();
            }
            
            
            
            
            
            
            
            
            //  Para visa y mastercard
            
            
            
            try{
                ResultSet registros = objDB.consulta("select C.tarjeta_credito_caduca, num_cuenta, "
                        + "F.id_sucursal::varchar || id_instalacion::varchar as id_instalacion, "
                        + "subtotal-descuento as subtotal, iva_2, total + "+comision+" as total, tipo_tarjeta_credito, "
                        + "fecha_emision "
                        + "from tbl_cliente as C inner join tbl_factura_venta as F on C.id_cliente=F.id_cliente "
                        + "where fecha_emision between '"+fecha_ini+"' and '"+fecha_fin+"' and F.deuda>0 and num_cuenta <>'' "
                        + "and C.forma_pago='TAR' and C.tarjeta_credito_caduca>now()::date and F.anulado=false "
                        + "and tipo_tarjeta_credito in('3', '4') "
                        + "order by tipo_tarjeta_credito");

                String tarjeta_credito_caduca = "";
                String num_cuenta = "";
                String fecha_emision = "";
                String subtotal = "";
                String iva_2 = "";
                String total = "";
                String tipo_tarjeta_credito = "";
                String ax_tipo_tarjeta_credito = "";
                
                xls.delete(0, xls.length());
                int i = 0;
                while(registros.next()){
                    tarjeta_credito_caduca = registros.getString("tarjeta_credito_caduca")!=null ? registros.getString("tarjeta_credito_caduca") : hoy;
                    num_cuenta = registros.getString("num_cuenta")!=null ? registros.getString("num_cuenta") : "0";
                    fecha_emision = registros.getString("fecha_emision")!=null ? registros.getString("fecha_emision") : "00000000";
                    subtotal = registros.getString("subtotal")!=null ? registros.getString("subtotal") : "0.00";
                    iva_2 = registros.getString("iva_2")!=null ? registros.getString("iva_2") : "0";
                    total = registros.getString("total")!=null ? registros.getString("total") : "0";
                    tipo_tarjeta_credito = registros.getString("tipo_tarjeta_credito")!=null ? registros.getString("tipo_tarjeta_credito") : "1";
                    if(ax_tipo_tarjeta_credito.compareTo("")==0){
                        ax_tipo_tarjeta_credito = tipo_tarjeta_credito;
                    }
                    
                    if(tipo_tarjeta_credito.compareTo(ax_tipo_tarjeta_credito)!=0 && i>0){
                        try{
                            String nombre = this.getNombreTarjeta(ax_tipo_tarjeta_credito) + "_" + hoy + ".txt";
                            FileWriter fichero = new FileWriter(this._ftp + nombre);
                            PrintWriter pw = new PrintWriter(fichero);
                            pw.print(xls);
                            if(pw != null){
                                pw.close();
                            }
                            if (fichero != null){
                               fichero.close();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        } 
                        
                        xls.delete(0, xls.length());
                        i=0;
                        ax_tipo_tarjeta_credito = tipo_tarjeta_credito;
                    }
                    
                    xls.append(Addons.rellenarCeros(num_cuenta, 16));       //  NUMERO TARJETA
                    xls.append("01533522");                                 //  CODIGO ESTABLECIMIENTO
                    xls.append(fecha_emision.replace("-", ""));             //  FECHA CONSUMO
                    xls.append(Addons.rellenarCeros( Addons.truncar(total).replace(".", ""), 17));  //  VALOR CONSUMO
                    xls.append("00000000000000000");                        //  VALOR DIFERIDO
                    xls.append("202");                                      //  TIPO DEL MOVIMIENTO
                    xls.append("000000");                                   //  NUMERO DE AUTORIZACION
                    xls.append("00");                                       //  NUMERO DE CUOTAS
                    xls.append("000000");                                   //  MOTIVO DEL DEBITO
                    xls.append(num_cuenta.substring(0, 6));   //  BIN CAPTURADOR (PRIMEROS 6 DIGITOS TARJETA)
                    xls.append(tarjeta_credito_caduca.replace("-", "").substring(0, 6));    //  FECHA CADUCIDAD TARJETA
                    xls.append(Addons.rellenarCeros( Addons.truncar(iva_2).replace(".", ""), 17));  //  IVA
                    xls.append("00");                                       //  TIPO DIFERIDO
                    xls.append("D");                                        //  MONEDA
                    xls.append("00000");                                    //  ESPACIOS LIBRES
                    xls.append(Addons.rellenarCeros( Addons.truncar(subtotal).replace(".", ""), 14));   //  Base IVA 12%
                    xls.append("00000000000000");                           //  Base IVA 0%            
                    xls.append("00000000000000\n");                           //  IVA 0%
                    i++;
                }
                
                if(i>0){
                    try{
                        String nombre = this.getNombreTarjeta(tipo_tarjeta_credito) + "_" + hoy + ".txt";
                        FileWriter fichero = new FileWriter(this._ftp + nombre);
                        PrintWriter pw = new PrintWriter(fichero);
                        pw.print(xls);
                        if(pw != null){
                            pw.close();
                        }
                        if (fichero != null){
                           fichero.close();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    } 
                }
                
                registros.close();
            }catch(Exception e){
                e.printStackTrace();
            }
            
            
            
            
            out.print("^fun»fac_ftpLocalVer();");
            
        }catch(Exception e){
            e.printStackTrace();
        }finally{
            out.close();
            objDB.cerrar();
        }
    }


    private String getNombreTarjeta(String num_tarjeta)
    {
        switch(Integer.parseInt(num_tarjeta)){
            case 1: return "Diners"; 
            case 2: return "Discover"; 
            case 3: return "Mastercard"; 
            case 4: return "Visa";  
        }
        return "";
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
