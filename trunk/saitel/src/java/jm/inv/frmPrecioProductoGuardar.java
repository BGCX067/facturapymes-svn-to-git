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

package jm.inv;

import java.io.*;

import javax.servlet.*;
import javax.servlet.http.*;
import jm.adm.clas.ListaPrecio;
import jm.seg.clas.Auditoria;
import java.sql.ResultSet;

/**
 *
 * @author Jorge
 */
public class frmPrecioProductoGuardar extends HttpServlet {
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
        
        ListaPrecio objListaPrecio = new ListaPrecio(this._ip, this._puerto, this._db, usuario, clave);
        
        try {
            String r = "msg»La utilidad a registrar es inferior a la utilidad mínima permitida.";
            //String WHERE = request.getParameter("WHERE");
            String id = request.getParameter("idL");
            String id_producto = request.getParameter("idProd");
            String lista = request.getParameter("lista");
            String utilidad = request.getParameter("utilidad");

            if(!objListaPrecio.bajoUtilidadMinima(id_producto, Float.parseFloat(utilidad))){
                r = "msg»El porcentaje de utilidad ya existe.";
                if(!objListaPrecio.utilidadDuplicada(id, id_producto, utilidad)){
                    r = "msg»Ha ocurrido un error inesperado, por favor, vuelva a intentarlo más tarde o " +
                            "contáctese con el administrador del sistema para mayor informacián.";
                    if(id.compareTo("-1")==0){
                        if(objListaPrecio.insertar(id_producto, lista, utilidad)){
                            Auditoria auditoria = new Auditoria(this._ip, this._puerto, this._db, usuario, clave);
                            auditoria.setRegistro(request, "INGRESO DE LA NUEVA LISTA: "+lista);
                            auditoria.cerrar();
                            r = "msg»Información ingresada satisfactoriamente.";
                        }
                    }else{
                        if(objListaPrecio.actualizar(id, lista, utilidad)){
                            Auditoria auditoria = new Auditoria(this._ip, this._puerto, this._db, usuario, clave);
                            auditoria.setRegistro(request, "ACTUALIZACION DE LA INFORMACION DE LA LISTA: "+lista);
                            auditoria.cerrar();
                            r = "msg»Información guardada satisfactoriamente.";
                        }
                    }

                    r += "^err»0^vta»vta1^obj»axTblLP^frm»<table cellspacing='0' cellpadding='0'>";
                    try{
                        ResultSet rsPrecios = objListaPrecio.getPrecios(id_producto);
                        String id_lista_precio = "";
                        int i=0;
                        while(rsPrecios.next()){
                            id_lista_precio = (rsPrecios.getString("id_lista_precio")!=null) ? rsPrecios.getString("id_lista_precio") : "";
                            lista = (rsPrecios.getString("lista")!=null) ? rsPrecios.getString("lista") : "";
                            utilidad = (rsPrecios.getString("utilidad")!=null) ? rsPrecios.getString("utilidad") : "";
                            r += "<tr id='rLP"+i+"' valign='top' style='cursor:pointer'>" +
                                    "<td width='200' onclick=\"inv_productoPrecio("+id_lista_precio+")\"><input type='hidden' id='idLP"+i+"' name='idLP"+i+"' value='"+id_lista_precio+"' />"+
                                    "<input type='text' id='lista"+i+"' name='lista"+i+"' size='35' maxlenght='30' value='"+lista+"' /></td>"+
                                    "<td width='80' onclick=\"inv_productoPrecio("+id_lista_precio+")\"><input type='text' id='utilidad"+i+"' name='utilidad"+i+"' size='7' maxlenght='7' onkeypress=\"_evaluar(event,'0123456789.');\" value='"+utilidad+"' /></td>"+
                                    "<td width='20'><div class='jm_icoCon' id='e"+i+"' style='background-position:-545px -18px;' onclick=\"inv_productoPrecioEliminar("+id_lista_precio+", "+i+");\" title='eliminar'>&nbsp;</div></td></tr>";
                            i++;
                        }
                        rsPrecios.close();
                    }catch(Exception e){
                        e.printStackTrace();
                    }
                    r += "</table>";
                }
            }
            out.print(r);

            
        } finally {
            objListaPrecio.cerrar();
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
