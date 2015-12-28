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

package jm.act;

import java.io.*;
import java.sql.ResultSet;

import javax.servlet.*;
import javax.servlet.http.*;
import jm.act.clas.Activo;
import jm.adm.clas.Bodega;
import jm.adm.clas.Configuracion;
import jm.adm.clas.Documento;
import jm.fac.clas.Instalacion;
import jm.seg.clas.Auditoria;
import jm.web.Fecha;

/**
 *
 * @author Jorge
 */
public class frmActivoGuardar extends HttpServlet {
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
        
        Activo objActivo = new Activo(this._ip, this._puerto, this._db, usuario, clave);
        
        try {
            String r = "msg»El código del activo ya existe.";
            String WHERE = request.getParameter("WHERE");
            String p = request.getParameter("p") != null ? request.getParameter("p") : "0";
            String id = request.getParameter("id");
            String codigo_activo = request.getParameter("codigo").toUpperCase();
            String id_tabla_depreciacion = request.getParameter("id_tabla_depreciacion");
            String descripcion = request.getParameter("descripcion");
            String id_sucursal = request.getParameter("id_sucursal");
            String id_categoria = request.getParameter("id_categoria");
            String id_bodega = request.getParameter("id_bodega");
            String ubicacion = request.getParameter("ubicacion");
            String fecha_compra = request.getParameter("fecha_compra");
            String marca = request.getParameter("marca");
            String serie = request.getParameter("serie");
            String modelo = request.getParameter("modelo");
            String num_partes = request.getParameter("num_partes");
            String color = request.getParameter("color");
            String valor_compra = request.getParameter("valor_compra");
            String valor_depreciado = request.getParameter("valor_depreciado");
            String num_meses = request.getParameter("num_meses");
            String estado = request.getParameter("estado");
            String observacion = request.getParameter("observacion");
            String proveedor = request.getParameter("proveedor");
            String tipo_pers = request.getParameter("tipo_pers");

            if(!objActivo.estaDuplicado(id, codigo_activo)){
                r = "msg»Ha ocurrido un error inesperado, por favor, vuelva a intentarlo más tarde o " +
                    "contáctese con el administrador del sistema para mayor información.";
                if(id.compareTo("-1")==0){
                    String id_activo = objActivo.insertar(codigo_activo, id_tabla_depreciacion, descripcion, id_sucursal, id_categoria, id_bodega, ubicacion, fecha_compra,
                            marca, serie, modelo, num_partes, color, valor_compra, valor_depreciado, num_meses, estado, observacion, proveedor);
                    if(id_activo.compareTo("-1")!=0){
                        
                        String documento = "";
                        try{
                            Documento objDocumento = new Documento(this._ip, this._puerto, this._db, usuario, clave);
                            ResultSet rsDoc = objDocumento.getDocumento("p");
                            if(rsDoc.next()){
                                documento = rsDoc.getString("documento")!=null ? rsDoc.getString("documento") : "";
                                rsDoc.close();
                            }
                            objDocumento.cerrar();
                        }catch(Exception e){}

                        Configuracion conf = new Configuracion(this._ip, this._puerto, this._db, usuario, clave);
                        String ruc_empresa = conf.getValor("ruc");
                        String empresa = conf.getValor("razon_social");
                        conf.cerrar();
        
                        long num_documento = objActivo.getNunDocumento();
                        
                        String idBodRec = "";
                        String bodegaRec = "";
                        String ubicacion_recibe = "";
                        String ci_recibe = "";
                        String nombre_recibe = "";
                        if(tipo_pers.compareTo("1")==0){
                            
                            Bodega objBodega = new Bodega(this._ip, this._puerto, this._db, usuario, clave);
                            //bodegaRec = objBodega.getNombre(idBodRec);
                            try{
                                ResultSet rs = objBodega.getBodegaUsuario(usuario);
                                if(rs.next()){
                                    idBodRec = rs.getString("id_bodega")!=null ? rs.getString("id_bodega") : "";
                                    bodegaRec = rs.getString("bodega")!=null ? rs.getString("bodega") : "";
                                    ubicacion_recibe = rs.getString("ubicacion")!=null ? rs.getString("ubicacion") : "";
                                    ci_recibe = rs.getString("dni")!=null ? rs.getString("dni") : "";
                                    nombre_recibe = rs.getString("empleado")!=null ? rs.getString("empleado") : "";
                                    rs.close();
                                }
                            }catch(Exception ex){
                                ex.printStackTrace();
                            }finally{
                                objBodega.cerrar();
                            }
                            
                        }else{
                            
                            Instalacion objInstalacion = new Instalacion(this._ip, this._puerto, this._db, usuario, clave);
                            String id_instalacion = request.getParameter("idIns");
                            try{
                                ResultSet rs = objInstalacion.getInstalacion(id_instalacion);
                                if(rs.next()){
                                    ubicacion_recibe = rs.getString("direccion_instalacion")!=null ? rs.getString("direccion_instalacion") : "";
                                    ci_recibe = rs.getString("ruc")!=null ? rs.getString("ruc") : "";
                                    nombre_recibe = rs.getString("razon_social")!=null ? rs.getString("razon_social") : "";
                                    rs.close();
                                }
                            }catch(Exception ex){
                                ex.printStackTrace();
                            }
                            objInstalacion.setEquipo(id_instalacion, codigo_activo, descripcion);
                            objInstalacion.cerrar();
                        }
                         
                        String idActivo = objActivo.getIdActivo(codigo_activo);
                        String id_personalizacion = objActivo.insertarPersonalizacion(id_sucursal_sesion, String.valueOf(num_documento), tipo_pers, Fecha.getFecha("ISO"),
                        "", idBodRec, empresa, bodegaRec, "", ubicacion_recibe, ruc_empresa, ci_recibe, empresa, nombre_recibe, documento, idActivo);
                        
                        Auditoria auditoria = new Auditoria(this._ip, this._puerto, this._db, usuario, clave);
                        auditoria.setRegistro(request, "INGRESO DE NUEVO ACTIVO: "+descripcion+ (id_personalizacion.compareTo("-1")==0?". ":"") );
                        auditoria.cerrar();
                        //r = "fun»_R('ppp1');^tbl»"+objActivo.paginar("vta_activo", "id_activo,codigo_activo,categoria,descripcion,valor_compra,valor_depreciado", WHERE, Integer.parseInt(p), 100)+"^msg»Información ingresada satisfactoriamente.";
                        r = "fun»_('id').value="+id_activo+";^msg»Información ingresada satisfactoriamente.";
                    }
                }else{
                    if(objActivo.actualizar(id, codigo_activo, id_tabla_depreciacion, descripcion, id_sucursal, id_categoria, id_bodega, ubicacion, fecha_compra,
                            marca, serie, modelo, num_partes, color, valor_compra, valor_depreciado, num_meses, estado, observacion, proveedor)){
                        Auditoria auditoria = new Auditoria(this._ip, this._puerto, this._db, usuario, clave);
                        auditoria.setRegistro(request, "ACTUALIZACION DE LA INFORMACION DEL ACTIVO: "+descripcion);
                        auditoria.cerrar();
                        //r = "fun»_R('ppp1');^tbl»"+objActivo.paginar("vta_activo", "id_activo,codigo_activo,categoria,descripcion,valor_compra,valor_depreciado", WHERE, Integer.parseInt(p), 100)+"^msg»Información guardada satisfactoriamente.";
                        r = "msg»Información guardada satisfactoriamente.";
                    }
                }
            }
            
            out.print(r);
        } finally {
            objActivo.cerrar();
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
