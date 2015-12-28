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

package jm.inv;

import java.io.*;
import java.sql.ResultSet;

import javax.servlet.*;
import javax.servlet.http.*;
import jm.adm.clas.Comun;
import jm.adm.clas.ListaPrecio;
import jm.adm.clas.Sucursal;
import jm.inv.clas.Producto;
import jm.web.DatosDinamicos;
import jm.seg.clas.Auditoria;

/**
 *
 * @author Jorge
 */
public class insertarUbicacion extends HttpServlet {
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

        Comun objComun = new Comun(this._ip, this._puerto, this._db, usuario, clave);
        try {
            String r = "msg»El nuevo ítem ya existe en la lista.";
            String id = request.getParameter("id");
            String ubicacion = request.getParameter("ub");
            String grupo = "UIP";
            
            if(!objComun.estaDuplicado(grupo,ubicacion)){
                r = "msg»Ha ocurrido un error inesperado, por favor, vuelva a intentarlo más tarde o " +
                        "contáctese con el administrador del sistema para mayor información.";
                String grupo1 = grupo + "-" + objComun.getSecuancia(grupo);
                if(objComun.insertar(grupo1,ubicacion)){
                    Auditoria auditoria = new Auditoria(this._ip, this._puerto, this._db, usuario, clave);
                    auditoria.setRegistro(request, "INGRESO DE UN NUEVO ITEM: "+ubicacion+" EN LA LISTA COMUN");
                    auditoria.cerrar();

                    r = "obj»tab11^err»0^vta»vta_im^frm»<TABLE cellspacing='0' cellpadding='0' width='425'><TR>" +
                            "<TH class='jm_TH' width='23'>&nbsp;</TH>" +
                            "<TH class='jm_TH' width='118'>SUCURSAL</TH>" +
                            "<TH class='jm_TH' width='73'>P.V.</TH>"+
                            "<TH class='jm_TH' width='63'>% DESC.</TH>" +
                            "<TH class='jm_TH' width='73'><div style='display:table-cell'>UBIC. </div> <div class='jm_icoCon' style='background-position:-504px -14px;' onclick=\"inv_insertarUbicacion();\" title='Insertar ubicaci&oacute;n'>&nbsp;</div></TH>" +
                            "<TH class='jm_TH' width='63'>STOCK</TH></TR></TABLE>";

                    r += "<table cellspacing='0' cellpadding='0' width='425'>";

                    Producto objproducto = new Producto(this._ip, this._puerto, this._db, usuario, clave);
                    ResultSet rsCodigos = objproducto.getCodigoSucursales(id);

                    ListaPrecio obj_ListaPrecio = new ListaPrecio(this._ip, this._puerto, this._db, usuario, clave);
                    ResultSet rsListas = obj_ListaPrecio.getListas();

                    ResultSet rsUbicaciones = objComun.getComun("UIP-");

                    Sucursal objSuc = new Sucursal(this._ip, this._puerto, this._db, usuario, clave);
                    ResultSet rsSucursales = objSuc.getSucursales();
                    try{
                        String tblCodigos [][] = DatosDinamicos.ResultSetToMatriz(rsCodigos);
                        String id_sucursal = "";
                        String sucursal = "";
                        int i=0;
                        int p = 0;
                        while(rsSucursales.next()){
                            p = DatosDinamicos.enMatriz(tblCodigos, id_sucursal, 0);
                            id_sucursal = (rsSucursales.getString("id_sucursal")!=null) ? rsSucursales.getString("id_sucursal") : "";
                            sucursal = (rsSucursales.getString("sucursal")!=null) ? rsSucursales.getString("sucursal") : "";
                            r += "<tr valign='top'><td width='25'><input id='s_ch"+i+"' name='s_ch"+i+"' type='checkbox' "+((p>=0)?"checked":"")+" /></td>" +
                                    "<td width='120'><input type='hidden' id='s_idS"+i+"' name='s_idS"+i+"' value='"+id_sucursal+"' />"+sucursal+"</td>"+
                                    "<td width='75'>"+DatosDinamicos.combo(rsListas, "idLP"+i, ((p>=0)?tblCodigos[p][1]:""), "", "", 70)+"</td>"+
                                    "<td width='65'><input type='text' id='des"+i+"' name='des"+i+"' size='5' maxlenght='5' onkeypress=\"_evaluar(event,'0123456789.');\" value='"+((p>=0)?tblCodigos[p][2]:"0")+"' /></td>"+
                                    "<td width='75'><div id='cobu"+i+"' style='display:table-cell'>"+DatosDinamicos.combo(rsUbicaciones, "idUI"+i, ((p>=0)?tblCodigos[p][3]:""), "", " NO DISPONIBLE ", 70)+"</div></td>"+
                                    "<td width='65'><input type='text' id='skSc"+i+"' name='skSc"+i+"' size='5' maxlenght='6' onkeypress='_numero(event)' value='"+((p>=0)?tblCodigos[p][4]:"0")+"' /></td></tr>";
                            i++;
                        }
                        rsCodigos.close();
                        rsSucursales.close();
                        rsListas.close();
                    }catch(Exception e){
                        e.printStackTrace();
                    }finally{
                        obj_ListaPrecio.cerrar();
                        objproducto.cerrar();
                        objSuc.cerrar();
                    }
                    r += "</table>";
                    
                }
            }

            out.print(r);
        } finally {
            objComun.cerrar();
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
