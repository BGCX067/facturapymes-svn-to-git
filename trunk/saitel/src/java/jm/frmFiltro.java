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

package jm;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.ResultSet;
import java.util.Calendar;
import javax.servlet.ServletException;
import javax.servlet.ServletConfig;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import jm.adm.clas.Configuracion;
import jm.adm.clas.Comun;
import jm.adm.clas.Sucursal;
import jm.web.Fecha;
import jm.web.DatosDinamicos;
import jm.cnt.clas.Cierre;
import jm.fac.clas.PlanServicio;
//import jm.fac.clas.Instalacion;
import jm.inf.clas.OrdenTrabajo;
import jm.fac.clas.Sector;
import jm.inf.clas.Antena;
import jm.inv.clas.Proveedor;
import jm.nom.clas.Empleado;
import jm.seg.clas.Rol;

/**
 *
 * @author Jorge
 */
public class frmFiltro extends HttpServlet {
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
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        HttpSession sesion = request.getSession(true);
        String id_rol = (String)sesion.getAttribute("id_rol");
        int id_sucursal = (Integer)sesion.getAttribute("sucursal");
        String usuario = (String)sesion.getAttribute("usuario");
        String clave = (String)sesion.getAttribute("clave");
        String empleado = (String)sesion.getAttribute("empleado");
        
        response.setContentType("text/html;charset=UTF-8");
        response.setHeader("Pragma", "no-cache");
        response.setHeader("Expires", "Mon, 01 Jan 2001 00:00:01 GMT");
        response.setHeader("Cache-Control", "no-store");
        response.setHeader("Cache-Control", "must-revalidate");
        response.setHeader("Cache-Control", "no-cache");
        PrintWriter out = response.getWriter();


        Sucursal objSuc = new Sucursal(this._ip, this._puerto, this._db, usuario, clave);
        ResultSet rsSucursales = objSuc.getSucursales();

        Comun objComun = new Comun(this._ip, this._puerto, this._db, usuario, clave);
        Rol objRol = new Rol(this._ip, this._puerto, this._db, usuario, clave);
        //RolPago objRolPago = new RolPago(this._ip, this._puerto, this._db, usuario, clave);

        
        Empleado objEmpleado = new Empleado(this._ip, this._puerto, this._db, usuario, clave);

        Proveedor objProveedor = new Proveedor(this._ip, this._puerto, this._db, usuario, clave);
        ResultSet rsProveedores = objProveedor.getProveedores();

        Sucursal objSucursal = new Sucursal(this._ip, this._puerto, this._db, usuario, clave);
        ResultSet rsResponsables = objSucursal.getResponsablesCajas();

        Sector objSector = new Sector(this._ip, this._puerto, this._db, usuario, clave);
        ResultSet rsSectores = objSector.getSectores(id_sucursal);
               
        PlanServicio objPlanServicio = new PlanServicio(this._ip, this._puerto, this._db, usuario, clave);
        ResultSet rsPlnSer = objPlanServicio.getPlanesTarifas();

        Antena objAntena = new Antena(this._ip, this._puerto, this._db, usuario, clave);
        ResultSet rsAntenas = objAntena.getAntenas(id_sucursal);
        
        Configuracion conf = new Configuracion(this._ip, this._puerto, this._db, usuario, clave);
        String id_sucursal_matriz = conf.getValor("id_sucursal_matriz");
        String html = "";
        String funcion = "";
        String fecha_uno = "01" + "/" + Fecha.getMes() + "/" + Fecha.getAnio();
        String fecha_actual = Fecha.getFecha("SQL");
        
        String [][] meses = {{"1","enero"},{"2","febrero"},{"3","marzo"},{"4","abril"},{"5","mayo"},{"6","junio"},
                             {"7","julio"},{"8","agosto"},{"9","septiembre"},{"10","octubre"},{"11","noviembre"},{"12","diciembre"}};

        String sel="";
        
        int op = Integer.valueOf(request.getParameter("op"));
        try {
            switch(op){
                case 1:
                    funcion = request.getParameter("fn");
                    html = "obj»d_filtro^fun»"+funcion+"^frm»"+
                    "Desde la fecha: <input type='text' id='fi' value='"+fecha_actual+"' size='10' readOnly><input type='button' value='...' onClick=\"SelectorFecha.crear('fi', 'SQL');\" /><br />"+
                    "Hasta la fecha: <input type='text' id='ff' value='"+fecha_actual+"' size='10' readOnly><input type='button' value='...' onClick=\"SelectorFecha.crear('ff', 'SQL');\" />";
                    if(Integer.valueOf(id_sucursal_matriz) == id_sucursal){
                        html += "<br />Sucursal: " + DatosDinamicos.combo(rsSucursales, "idSuc", "", funcion, " EMPRESA ", 140);
                    }else{
                        html += "<input type='hidden' id='idSuc' value='"+id_sucursal+"' />";
                    }
                    if(funcion.compareTo("")!=0){
                        html += " &nbsp;<input type='button' value='Filtrar' onclick='"+funcion+"'>";
                    }
                break;
                
                case 2:
                    String [][] tipo_comprobante = {{"d","Diario"},{"i","Ingresos"},{"e","Egresos"}};
                    html = "obj»d_filtro^fun»cnt_filtrarComprobantes()^frm»"+
                    "<div>Comprobantes: " + DatosDinamicos.combo("typ", "", tipo_comprobante, "cnt_filtrarComprobantes()")+"</div>";
                    html += "<div id='axNumDoc'>No. documento: <input type='text' id='t_b' size='10'value='' /></div>";        
                    if(Integer.valueOf(id_sucursal_matriz) == id_sucursal){
                        html += "<div>Sucursal: " + DatosDinamicos.combo(rsSucursales, "idSuc", "", "cnt_filtrarComprobantes()", " EMPRESA ", 140)+"</div>";
                    }else{
                        html += "<input type='hidden' id='idSuc' value='"+id_sucursal+"' />";
                    }
                    html += "<div>Desde: <input type='text' id='fi' value='"+fecha_actual+"' size='10' readOnly><input type='button' value='...' onClick=\"SelectorFecha.crear('fi', 'SQL');\" /> &nbsp;&nbsp; "+
                    "Hasta: <input type='text' id='ff' value='"+fecha_actual+"' size='10' readOnly><input type='button' value='...' onClick=\"SelectorFecha.crear('ff', 'SQL');\" />";
                    
                    html += " &nbsp;<input type='button' value='Filtrar' onclick='cnt_filtrarComprobantes()'></div>";
                break;

                case 3:
                    int mes = Fecha.getMes()-1;

                    Cierre cierre = new Cierre(this._ip, this._puerto, this._db, usuario, clave);
                    String fecha = cierre.getPrimerCierre();
                    cierre.cerrar();

                    int anio_fin = Fecha.getAnio();
                    int anio_ini = anio_fin;
                    int anio_declaracion = anio_fin;
                    if(fecha.compareTo("")!=0){
                        String vecFecha [] = fecha.split("-");
                        anio_ini = Integer.valueOf(vecFecha[0]);
                    }
                    if(mes<=0){
                        anio_declaracion--;
                        mes = 12;
                    }
                    String cad_anio="<select id=\"a\">";
                    for(int i=anio_ini; i<=anio_fin; i++){
                        sel = (i==anio_declaracion) ? "selected" : "";
                        cad_anio+="<option "+sel+" value=\"" + i + "\">" + i + "</option>";
                    }
                    cad_anio+="</select>";

                    html += "obj»vta_im_html^frm»A&ntilde;o: "+cad_anio;
                    html += "<br>Mes: "+DatosDinamicos.combo("m", String.valueOf(mes), meses, "");
                    //html += "<br><input type='radio' id='z0' name='z' checked='checked' value='103' /> <span onclick=\"_('z0').checked='true'\">Formulario 103 </span>";
                    html += "<br><input type='radio' id='z1' name='z' value='104' /> <span onclick=\"_('z1').checked='true'\">Formulario 104</span>";
                    html += "<br><input type='radio' id='z2' name='z' value='at' /> <span onclick=\"_('z2').checked='true'\">Anexo Transaccional</span>";
                    //html += "<br><input type='radio' id='z3' name='z' value='reoc' /> <span onclick=\"_('z3').checked='true'\">REOC</span>";
                    html += "<br><br><input type='button' value='Generar Archivo' onclick='cnt_exportarForm();' />";
                break;

                case 4:
                    html = "obj»vta_im_html^frm»";
                    if(Integer.valueOf(id_sucursal_matriz) == id_sucursal){
                        html += "Sucursal: " + DatosDinamicos.combo(rsSucursales, "idSuc", "", "", " EMPRESA ", 140) + "<hr />";
                    }else{
                        html += "<input type='hidden' id='idSuc' name='idSuc' value='"+id_sucursal+"' />";
                    }
                    html += "<input type='radio' id='z0' name='z' checked='checked' value='1' onclick=\"_('EB0').style.display='block';\" /> "
                            + "<span onclick=\"_('z0').checked='true';_('EB0').style.display='block';\">Balance de Comprobaci&oacute;n</span>"+
                    "<br><input type='radio' id='z1' name='z' value='2' onclick=\"_('EB0').style.display='none';\" /> "
                            + "<span onclick=\"_('z1').checked='true';_('EB0').style.display='none';\">Balance General</span>"+
                    "<br><input type='radio' id='z2' name='z' value='3' onclick=\"_('EB0').style.display='block';\" /> "
                            + "<span onclick=\"_('z2').checked='true';_('EB0').style.display='block';\">Estado de Resultados</span><hr />";
                    
                    html += "<div id='EB0' style='display:block;'>Desde la fecha: <input type='text' id='bfi' value='"+fecha_uno+"' size='10' readOnly><input type='button' value='...' onClick=\"SelectorFecha.crear('bfi', 'SQL');\" /><br /></div>"+
                    "Hasta la fecha: <input type='text' id='bff' value='"+fecha_actual+"' size='10' readOnly><input type='button' value='...' onClick=\"SelectorFecha.crear('bff', 'SQL');\" />";
                    
                    html += "<hr /><input type='radio' id='x' name='x' checked='checked' /> PDF &nbsp;&nbsp;&nbsp;&nbsp;&nbsp; <input type='radio' id='x2' name='x' /> Excel";

                    html += " &nbsp;<input style='float:right;' type='button' value='Imprimir' onclick='cnt_imprimirBalance()'>";
                break;

                case 5:
                    ResultSet rsCategorias = objComun.getGrupoComun("CAT-");
                    String marca = "";
                    try{
                        if(rsCategorias.next()){
                            marca = (rsCategorias.getString("id_comun")!=null) ? rsCategorias.getString("id_comun") : "";
                            rsCategorias.beforeFirst();
                        }
                    }catch(Exception e){
                        e.printStackTrace();
                    }

                    funcion = request.getParameter("fn");
                    html = "obj»d_filtro^fun»"+funcion+"^frm»";
                    if(Integer.valueOf(id_sucursal_matriz) == id_sucursal){
                        html += "Sucursal: " + DatosDinamicos.combo(rsSucursales, "idSuc", "", funcion, " EMPRESA ", 140);
                    }else{
                        html += "<input type='hidden' id='idSuc' value='"+id_sucursal+"' />";
                    }
                    html += "<br />Categor&iacute;a: " + DatosDinamicos.combo(rsCategorias, "idCat", marca, funcion, " TODAS ", 140);
                    html += "<br />C&oacute;digo o descripci&oacute;n: <input type='text' id='t_b' onkeypress=\"var c=(_esN)?event.which:event.keyCode;if(c==13){"+funcion+";}\" />";
                    html += " &nbsp;<input type='button' value='Filtrar' onclick='"+funcion+"'>";
                    
                    try{
                        rsCategorias.close();
                    }catch(Exception e){
                        e.printStackTrace();
                    }
                break;

                case 6:
                    funcion = request.getParameter("fn");
                    html = "obj»d_filtro^fun»"+funcion+"^frm»";
                    if(Integer.valueOf(id_sucursal_matriz) == id_sucursal){
                        html += "Sucursal: " + DatosDinamicos.combo(rsSucursales, "idSuc", id_sucursal_matriz, funcion, " EMPRESA ", 140) + "<br />";
                    }else{
                        html += "<input type='hidden' id='idSuc' value='"+id_sucursal+"' />";
                    }
                    html += "Proveedor: " + DatosDinamicos.combo(rsProveedores, "idPrv", "", funcion, " TODOS ", 140);
                    html += "<br />Desde la fecha: <input type='text' id='fi' value='"+Fecha.getFecha("SQL", Calendar.DAY_OF_MONTH, -10)+"' size='10' readOnly><input type='button' value='...' onClick=\"SelectorFecha.crear('fi', 'SQL');\" />"+
                    "<br />Hasta la fecha: <input type='text' id='ff' value='"+fecha_actual+"' size='10' readOnly><input type='button' value='...' onClick=\"SelectorFecha.crear('ff', 'SQL');\" />";
                    html += " &nbsp;<input type='button' value='Filtrar' onclick='"+funcion+"'>";
                break;

                case 7:
                    String [][] pagos = {{"p","pagos pendiantes"},{"f","pagos de facturas"}};
                    funcion = request.getParameter("fn");
                    html = "obj»d_filtro^fun»"+funcion+"^frm»";
                    if(Integer.valueOf(id_sucursal_matriz) == id_sucursal){
                        html += "Sucursal: " + DatosDinamicos.combo(rsSucursales, "idSuc", "", funcion, "", 140) + "<br />";
                    }else{
                        html += "<input type='hidden' id='idSuc' value='"+String.valueOf(id_sucursal)+"' />";
                    }
                    html += "Proveedor: " + DatosDinamicos.combo(rsProveedores, "idPrv", "", funcion, " TODOS ", 140);
                    html += "<br />Ver: " + DatosDinamicos.combo("pgs", "", pagos, funcion);
                    html += "<div id='grFch' style='visibility:hidden'>Desde: <input type='text' id='fi' value='"+Fecha.getFecha("SQL", Calendar.DAY_OF_MONTH, -10)+"' size='10' readOnly><input type='button' value='...' onClick=\"SelectorFecha.crear('fi', 'SQL');\" />"+
                    " &nbsp; Hasta: <input type='text' id='ff' value='"+fecha_actual+"' size='10' readOnly><input type='button' value='...' onClick=\"SelectorFecha.crear('ff', 'SQL');\" />";
                    html += " &nbsp;<input type='button' value='Filtrar' onclick='"+funcion+"'></div>";
                break;

                case 8:
                    funcion = request.getParameter("fn");
                    html = "obj»d_filtro^fun»"+funcion+"^frm»";
                    if(Integer.valueOf(id_sucursal_matriz) == id_sucursal){
                        String [][] traspaso = {{"e","enviados"},{"r","recibidos"}};
                        html += "Env&iacute;o desde: <input type='text' id='fi' value='"+Fecha.getFecha("SQL", Calendar.DAY_OF_MONTH, -10)+"' size='10' readOnly><input type='button' value='...' onClick=\"SelectorFecha.crear('fi', 'SQL');\" />"+
                        "<br />Hasta la fecha: <input type='text' id='ff' value='"+fecha_actual+"' size='10' readOnly><input type='button' value='...' onClick=\"SelectorFecha.crear('ff', 'SQL');\" />";
                        html += "<br />Sucursal origen: " + DatosDinamicos.combo(rsSucursales, "idSuc", id_sucursal_matriz, funcion, "", 140);
                        html += "<br />Traspasos: " + DatosDinamicos.combo("tras", "", traspaso, funcion);
                        if(funcion.compareTo("")!=0){
                            html += " &nbsp;<input type='button' value='Filtrar' onclick='"+funcion+"'>";
                        }
                    }else{
                        html += "<input type='hidden' id='idSuc' value='"+String.valueOf(id_sucursal)+"' />";
                    }
                break;

                case 9:
                    funcion = request.getParameter("fn");
                    html = "obj»d_filtro^fun»"+funcion+"^frm»";
                    if(Integer.valueOf(id_sucursal_matriz) == id_sucursal){
                        String [][] pedidos = {{"false","Sin revisar"},{"true","Revisados"}};
                        html += "Env&iacute;o desde: <input type='text' id='fi' value='"+Fecha.getFecha("SQL", Calendar.DAY_OF_MONTH, -10)+"' size='10' readOnly><input type='button' value='...' onClick=\"SelectorFecha.crear('fi', 'SQL');\" />"+
                        "<br />Hasta la fecha: <input type='text' id='ff' value='"+fecha_actual+"' size='10' readOnly><input type='button' value='...' onClick=\"SelectorFecha.crear('ff', 'SQL');\" />";
                        html += "<br />Sucursal: " + DatosDinamicos.combo(rsSucursales, "idSuc", id_sucursal_matriz, funcion, "", 140);
                        html += "<br />Pedidos: " + DatosDinamicos.combo("ped", "", pedidos, funcion);
                        if(funcion.compareTo("")!=0){
                            html += " &nbsp;<input type='button' value='Filtrar' onclick='"+funcion+"'>";
                        }
                    }else{
                        html += "<input type='hidden' id='idSuc' value='"+String.valueOf(id_sucursal)+"' />";
                    }
                break;

                case 10:
                    funcion = request.getParameter("fn");
                    String [][] estado_doc = {{""," TODOS "},{"f","firmadas"},{"r","recibidas",},{"a","autorizadas"},{"n","rechazadas"}};
                    html = "obj»d_filtro^fun»"+funcion+"^frm»";
                    html += "Desde la fecha: <input type='text' id='fi' value='"+fecha_actual+"' size='10' readOnly><input type='button' value='...' onClick=\"SelectorFecha.crear('fi', 'SQL');\" />"+
                        " &nbsp;&nbsp;&nbsp; Hasta la fecha: <input type='text' id='ff' value='"+fecha_actual+"' size='10' readOnly><input type='button' value='...' onClick=\"SelectorFecha.crear('ff', 'SQL');\" />";
                    if(Integer.valueOf(id_sucursal_matriz) == id_sucursal){
                        html += "<br />Sucursal: " + DatosDinamicos.combo(rsSucursales, "idSuc", "", funcion, "", 140);
                    }else{
                        html += "<input type='hidden' id='idSuc' value='"+String.valueOf(id_sucursal)+"' />";
                    }
                    html += "<br />Estado documento: " + DatosDinamicos.combo("estDoc", "", estado_doc, funcion);
                    html += "<br />Nro. de factura, ruc, cliente: <input type='text' id='t_b' onkeypress=\"var c=(_esN)?event.which:event.keyCode;if(c==13){"+funcion+";}\" />";
                    if(funcion.compareTo("")!=0){
                        html += " &nbsp;<input type='button' value='Filtrar' onclick='"+funcion+"'>";
                    }
                break;

                case 11:
                    String [][] cobros = {{"p","cobros pendiantes"},{"f","cobros de facturas"}};
                    funcion = request.getParameter("fn");
                    html = "obj»d_filtro^fun»"+funcion+"^frm»";
                    if(Integer.valueOf(id_sucursal_matriz) == id_sucursal){
                        html += "Sucursal: " + DatosDinamicos.combo(rsSucursales, "idSuc", "", funcion, "", 140) + "<br />";
                    }else{
                        html += "<input type='hidden' id='idSuc' value='"+String.valueOf(id_sucursal)+"' />";
                    }
                    html += "ruc, cliente: <input type='text' id='t_b' onkeypress=\"var c=(_esN)?event.which:event.keyCode;if(c==13){"+funcion+";}\" />";
                    html += "<br />Ver: " + DatosDinamicos.combo("cbs", "", cobros, funcion);
                    html += "<div id='grFch' style='visibility:hidden'>Desde: <input type='text' id='fi' value='"+fecha_actual+"' size='10' readOnly><input type='button' value='...' onClick=\"SelectorFecha.crear('fi', 'SQL');\" /> &nbsp;&nbsp;&nbsp;&nbsp; "+
                    "hasta: <input type='text' id='ff' value='"+fecha_actual+"' size='10' readOnly><input type='button' value='...' onClick=\"SelectorFecha.crear('ff', 'SQL');\" />";
                    html += " &nbsp;<input type='button' value='Filtrar' onclick='"+funcion+"'></div>";
                break;

                case 12:
                    html = "obj»d_filtro^fun»inv_importacionBuscar()^frm»";
                    html += "Importaciones desde: <input type='text' id='fi' value='"+fecha_uno+"' size='10' readOnly><input type='button' value='...' onClick=\"SelectorFecha.crear('fi', 'SQL');\" />"+
                        "<br />Hasta la fecha: <input type='text' id='ff' value='"+fecha_actual+"' size='10' readOnly><input type='button' value='...' onClick=\"SelectorFecha.crear('ff', 'SQL');\" />";
                    html += "<br />N&uacute;mero del DAU: <input type='text' id='t_b' onkeypress=\"_numero(event);var c=(_esN)?event.which:event.keyCode;if(c==13){inv_importacionBuscar();}\" />";
                    html += " &nbsp;<input type='button' value='Filtrar' onclick='inv_importacionBuscar()'></div>"
                            + "<input type='radio' id='z0' style='display:none' checked value='9' /><input type='radio' id='x' name='x' checked style='visibility:hidden' />";
                break;

                case 13:
                    funcion = request.getParameter("fn");
                    html = "obj»d_filtro^fun»"+funcion+"^frm»";
                    if(Integer.valueOf(id_sucursal_matriz) == id_sucursal){
                        html += "<br />Sucursal: " + DatosDinamicos.combo(rsSucursales, "idSuc", "", funcion, "", 140);
                    }else{
                        html += "<input type='hidden' id='idSuc' value='"+String.valueOf(id_sucursal)+"' />";
                    }
                    html += "<br />Bodega, responsable: &nbsp; <input type='text' id='t_b' onkeypress=\"var c=(_esN)?event.which:event.keyCode;if(c==13){"+funcion+";}\" />";
                    if(funcion.compareTo("")!=0){
                        html += " &nbsp;<input type='button' value='Filtrar' onclick='"+funcion+"'>";
                    }
                break;

                case 14:
                    funcion = request.getParameter("fn");
                    html = "obj»d_filtro^fun»"+funcion+"^frm»";
                    html += "Desde la fecha: <input type='text' id='fi' value='"+fecha_uno+"' size='10' readOnly><input type='button' value='...' onClick=\"SelectorFecha.crear('fi', 'SQL');\" />"+
                        "<br />Hasta la fecha: <input type='text' id='ff' value='"+fecha_actual+"' size='10' readOnly><input type='button' value='...' onClick=\"SelectorFecha.crear('ff', 'SQL');\" />";
                    if(Integer.valueOf(id_sucursal_matriz) == id_sucursal){
                        html += "<br />Sucursal: " + DatosDinamicos.combo(rsSucursales, "idSuc", "", funcion, "", 140);
                    }else{
                        html += "<input type='hidden' id='idSuc' value='"+String.valueOf(id_sucursal)+"' />";
                    }
                    html += "<br />DNI, empleado: <input type='text' id='t_b' onkeypress=\"var c=(_esN)?event.which:event.keyCode;if(c==13){"+funcion+";}\" />";
                    if(funcion.compareTo("")!=0){
                        html += " &nbsp;<input type='button' value='Filtrar' onclick='"+funcion+"'>";
                    }
                break;

                case 15:
                    funcion = request.getParameter("fn");
                    html = "obj»d_filtro^fun»"+funcion+"^frm»";
                    html += "Desde la fecha: <input type='text' id='fi' value='"+Fecha.getFecha("SQL", Calendar.DAY_OF_MONTH, -20)+"' size='10' readOnly><input type='button' value='...' onClick=\"SelectorFecha.crear('fi', 'SQL');\" />"+
                        "<br />Hasta la fecha: <input type='text' id='ff' value='"+fecha_actual+"' size='10' readOnly><input type='button' value='...' onClick=\"SelectorFecha.crear('ff', 'SQL');\" />";
                    if(Integer.valueOf(id_sucursal_matriz) == id_sucursal){
                        html += "<br />Sucursal: " + DatosDinamicos.combo(rsSucursales, "idSuc", "", funcion, "", 140);
                    }else{
                        html += "<input type='hidden' id='idSuc' value='"+String.valueOf(id_sucursal)+"' />";
                    }
                    html += "<br />DNI, empleado: <input type='text' id='t_b' onkeypress=\"var c=(_esN)?event.which:event.keyCode;if(c==13){"+funcion+";}\" />";
                    if(funcion.compareTo("")!=0){
                        html += " &nbsp;<input type='button' value='Filtrar' onclick='"+funcion+"'>";
                    }
                break;

                case 16:
                    /*int anio_min = objRolPago.getMinPeriodo("anio");
                    int mes_min = objRolPago.getMinPeriodo("mes");
                    int anio_actual = Fecha.getAnio();
                    int anio_max_periodo = objRolPago.getMaxPeriodo("anio");
                    int mes_max_periodo = objRolPago.getMaxPeriodo("mes");

                    Empleado objEmpleado = new Empleado(this._ip, this._puerto, this._db, usuario, clave);
                    ResultSet rsEmpleados = objEmpleado.getEmpleados(anio_max_periodo, mes_max_periodo);
                    
                    html = "obj»cmp_html^frm»";
                    html += "<form action='frmRolPagoNuevoGuardar' onsubmit='return nom_rolPagoNuevoGuardar(this)' autocomplete='off'>"
                            + "<input type='hidden' id='pMin' value='"+(anio_min+"-"+mes_min+"-1")+"' />" +
                    "<input type='hidden' id='pMax' value='"+(anio_max_periodo+"-"+mes_max_periodo+"-2")+"' />" +
                    "<table>";
                    html += "<tr><td width='100'>A&ntilde;o: <span class='marca'>*</span></td>";
                    html += "<td width='300'><select id='an' name='an' onchange='nom_traerEmpleados();'>";
                    for(int i=anio_min; i<=anio_actual; i++){
                        sel = (i==anio_max_periodo) ? "selected" : "";
                        html += "<option "+sel+" value=\"" + i + "\">" + i + "</option>";
                    }
                    html += "</select></td></tr>";
                    html += "<tr><td>Mes: <span class='marca'>*</span></td>";
                    html += "<td>" + DatosDinamicos.combo("ms", String.valueOf(mes_max_periodo), meses, "nom_traerEmpleados()") + "</td></tr>";
                    html += "<tr><td>Empleado: <span class='marca'>*</span></td>" +
                            "<td id='auxEmp'>"+DatosDinamicos.combo(rsEmpleados, "idE", "", "", 250)+"</td></tr>";
                    html += "<tr><td colspan='2' align='center'><input type='submit' value='Generar Rol de Pago' /></td></tr>";
                    html += "</table></form>";

                    try{
                        rsEmpleados.close();
                    }catch(Exception e){
                        e.printStackTrace();
                    }
                    
                    objEmpleado.cerrar();*/
                break;

                case 17:
                    String [][] estado_servicio1 = {{"p", "Por instalar"},{"a","Activo"},{"c","Cortado"},{"s","Suspendido"},{"r", "Por retirar"},{"e", "Equipos devueltos"},{"d","Saldado"},{"t", "Terminado y saldado"},{"n","Central de riesgo"}};
                    funcion = request.getParameter("fn");
                    html = "obj»d_filtro^fun»"+funcion+"^frm»";
                    html += "<table cellspacing='0' cellpadding='0'><tr>"
                            + "<td>Desde: </td><td><div style='display:table-cell'><input type='text' id='fi' value='"+Fecha.getFecha("SQL", Calendar.DAY_OF_MONTH, -20)+"' size='10' readOnly><input type='button' value='...' onClick=\"SelectorFecha.crear('fi', 'SQL');\" /></div>"+
                        "<div style='display:table-cell'>&nbsp;&nbsp; Hasta: <input type='text' id='ff' value='"+fecha_actual+"' size='10' readOnly><input type='button' value='...' onClick=\"SelectorFecha.crear('ff', 'SQL');\" /></div></td>"
                            + "<td>Sector: </td><td>" + DatosDinamicos.combo(rsSectores, "idSec", "", funcion, " TODOS ", 200) + "</td></tr>";
                    
                    html += "<tr>";
                    if(Integer.valueOf(id_sucursal_matriz) == id_sucursal){
                        html += "<td>Sucursal: </td><td>" + DatosDinamicos.combo(rsSucursales, "idSuc", "", funcion, "", 140) + "</td>";
                    }else{
                        html += "<td>Sucursal: </td><td><input type='hidden' id='idSuc' value='"+String.valueOf(id_sucursal)+"' />"+objSucursal.getNombre(String.valueOf(id_sucursal)) +"</td>";
                    }
                    html += "<td>Antena: </td><td>" + DatosDinamicos.combo(rsAntenas, "idAnt", "", funcion, " TODAS ", 100) + "</td></tr>";
                    
                    html += "<tr><td>Estado del servicio:</td><td> " + DatosDinamicos.combo("esSer", "", estado_servicio1, funcion, " TODOS ") + "</td>";
                    html += "<td>Plan: </td><td>" + DatosDinamicos.combo(rsPlnSer, "idPlSe", "", funcion, " TODOS ", 200) + "</td></tr>";
                    
                    html += "<tr><td>RUC, cliente, IP, MAC, usuario ppoe: </td><td colspan='3'><input type='text' id='t_b' size='50' onkeypress=\"var c=(_esN)?event.which:event.keyCode;if(c==13){"+funcion+";}\" />";
                    html += "&nbsp;<input type='button' value='Filtrar' onclick='"+funcion+"'></td></tr>";
                break;

                case 18:
                    //conf.consulta("select proc_generarPreFacturas();");
                    
                    funcion = request.getParameter("fn");
                    html = "obj»d_filtro^fun»_('t_b').focus();^frm»";
                    if(Integer.valueOf(id_sucursal_matriz) == id_sucursal){
                        html += "Sucursal: " + DatosDinamicos.combo(rsSucursales, "idSuc", String.valueOf(id_sucursal), funcion, "", 140) + "<br />";
                    }else{
                        html += "<input type='hidden' id='idSuc' value='"+String.valueOf(id_sucursal)+"' />";
                    }
                    html += "<span>Sector: </span><span id='auxSec'>" + DatosDinamicos.combo(rsSectores, "idSec", "", funcion, " TODOS ", 140) + "</span>";
                    html += "<br />RUC, cliente, IP: <input type='text' id='t_b' onkeypress=\"var c=(_esN)?event.which:event.keyCode;if(c==13){"+funcion+";}\" />";
                    if(funcion.compareTo("")!=0){
                        html += " &nbsp;<input type='button' value='Filtrar' onclick='"+funcion+"'>";
                    }
                break;

                case 19:
                    String [][] tipo_movimiento1 = {{"1","Compra"},{"2","Interpersonal"},{"3","Entre bodegas"},{"4","De bodega a personal"},
                                            {"5","De personal a bodega"},{"6","De bodega a cliente"},{"7","De cliente a bodega"},{"8","Venta"},
                                            {"9","De bodega a infraestructura"}, {"10","De infraestructura a bodega"}};
                    String [][] aceptadas = {{"","TODAS"},{"false","Pendientes de aceptar"},{"true","Aceptadas"}};
                    funcion = request.getParameter("fn");
                    html = "obj»d_filtro^fun»"+funcion+"^frm»";
                    html += "Rango de fechas: <input type='text' id='fi' value='"+Fecha.getFecha("SQL", Calendar.DAY_OF_MONTH, -10)+"' size='10' readOnly><input type='button' value='...' onClick=\"SelectorFecha.crear('fi', 'SQL');\" />"+
                        " &nbsp;&nbsp; hasta &nbsp;&nbsp; <input type='text' id='ff' value='"+fecha_actual+"' size='10' readOnly><input type='button' value='...' onClick=\"SelectorFecha.crear('ff', 'SQL');\" />";
                    html += "<br />Personalizaciones: " + DatosDinamicos.combo("Acep", "false", aceptadas, funcion);
                    html += "<br />Tipo de movimiento: " + DatosDinamicos.combo("TMov", "3", tipo_movimiento1, funcion);
                    if(objRol.getAcceso(id_rol, "personalizacion_ver")){
                        html += "<br />RUC, CUSTODIO: <input type='text' id='t_b' value='a' onkeypress=\"var c=(_esN)?event.which:event.keyCode;if(c==13){"+funcion+";}\" />";
                    }else{
                        html += "<input type='hidden' id='t_b' value='"+empleado+"' />";
                    }
                    if(funcion.compareTo("")!=0){
                        html += " &nbsp;<input type='button' value='Filtrar' onclick='"+funcion+"'>";
                    }
                break;

                case 20:
                    OrdenTrabajo objOrdenTrabajo = new OrdenTrabajo(this._ip, this._puerto, this._db, usuario, clave);
                    ResultSet rsOrdenTrabajo = objOrdenTrabajo.getTipos();
                    String [][] estado1 = {{"1","Reportado"},{"3","Pendientes"},{"2","En proceso"},{"9","Solucionado"}};
                    funcion = request.getParameter("fn");
                    html = "obj»d_filtro^foc»t_b^fun»"+funcion+"^frm»";
                    /*html += "<div class='filtro_mas' onmouseover=\"this.style.backgroundColor='#F2E1C6'\" onmouseout=\"this.style.backgroundColor='#F9F1E6'\">"
                            + "<img src='img/filtro+.png' onmouseover=\"fac_trabajoMenu()\" /></div>";*/
                    html += "<table cellspacing='0' cellpadding='0' style='float:left;'>";
                    if(Integer.valueOf(id_sucursal_matriz) == id_sucursal){
                        html += "<tr><td>Sucursal: </td><td colspan='3'>" + DatosDinamicos.combo(rsSucursales, "idSuc", "", funcion, "") + "</td></tr>";
                    }else{
                        html += "<input type='hidden' id='idSuc' value='"+String.valueOf(id_sucursal)+"' />";
                    }
                    html += "<tr><td width='50'>Estado: </td><td width='150'>" + DatosDinamicos.combo("est", "1", estado1, funcion, "") + "</td>"
                            + "<td width='40'>Tipo: </td><td width='150'>" + DatosDinamicos.combo(rsOrdenTrabajo, "tip", "", funcion, " TODOS ", 113) + "</td>"
                            + "</tr>";
                    html += "<tr><td>Desde: </td><td><input type='text' id='fi' size='10' onkeypress=\"_SUP(event)\"><input type='button' value='...' onClick=\"SelectorFecha.crear('fi', 'SQL');\" /></td>"+
                        "<td>Hasta: </td><td><input type='text' id='ff' size='10' onkeypress=\"_SUP(event)\"><input type='button' value='...' onClick=\"SelectorFecha.crear('ff', 'SQL');\" /></td>"
                        + "</tr>";
                    html += "<tr><td colspan='4'>Nro., cliente: <input type='text' id='t_b' size='33' onkeypress=\"var c=(_esN)?event.which:event.keyCode;if(c==13){"+funcion+";}\" />";
                    html += "&nbsp;<input type='button' value='Filtrar ordenes' onclick='"+funcion+"' /></td></tr></table>";
                    html += "<fieldset style='background-color:#f1b351;float:left;'><legend style='background-color:#f1b351;'>Generar Ordenes de Trabajo</legend>"
                            + "<div><input type='button' value='Nueva orden de trabajo para infraestructura' onclick=\"fac_trabajoTorreEditar(-1)\" /></div>"
                            + "<hr />"
                            + "C.I., cliente o IP: <input type='text' id='t_b1' size='20' onkeypress=\"var c=(_esN)?event.which:event.keyCode;if(c==13){fac_trabajoBuscarInst();}\" />";
                    html += "&nbsp;<input type='button' value='Filtrar instalaciones' onclick='fac_trabajoBuscarInst()' /></fieldset>";
                    objOrdenTrabajo.cerrar();
                break;

                case 21:
                    String [][] estado11 = {{"r","Reportado"},{"s","Solucionado"}};
                    funcion = request.getParameter("fn");
                    html = "obj»d_filtro^frm»";
                    //Instalacion objInstalacion = new Instalacion(this._ip, this._puerto, this._db, usuario, clave);
                    html += "<input type='hidden' id='idIll' />";
                    //html += "<div id='ax' style='display:none'>"+objInstalacion.getInstalacionesJSON()+"</div>";
                    html += "<div id='ax' style='display:none'></div>";
                    //objInstalacion.cerrar();
                    
                    html += "<table cellspacing='1' cellpadding='0'><tr>";
                    if(Integer.valueOf(id_sucursal_matriz) == id_sucursal){
                        html += "<td>Sucursal: </td><td>" + DatosDinamicos.combo(rsSucursales, "idSuc", id_sucursal_matriz, funcion, "", 140) + "</td>";
                        html += "<td width='50'>&nbsp;</td>";
                    }else{
                        html += "<td colspan='2'><input type='hidden' id='idSuc' value='"+String.valueOf(id_sucursal)+"' /></td>";
                        html += "<td width='50'>&nbsp;</td>";
                    }
                    html += "<td>Desde: </td><td><div style='display:table-cell'><input type='text' id='fi' value='"+Fecha.getFecha("SQL", Calendar.DAY_OF_MONTH, -10)+"' size='10' readOnly><input type='button' value='...' onClick=\"SelectorFecha.crear('fi', 'SQL');\" /></div>"+
                        "<div style='display:table-cell'>&nbsp;&nbsp; Hasta: <input type='text' id='ff' value='"+fecha_actual+"' size='10' readOnly><input type='button' value='...' onClick=\"SelectorFecha.crear('ff', 'SQL');\" /></div></td>";
                    html += "</tr>";

                    html += "<tr>";
                    html += "<td>C&eacute;dula, cliente, IP: </td><td><input type='text' id='t_b' style='width:136px' onkeypress=\"var c=(_esN)?event.which:event.keyCode;if(c==13){"+funcion+";}\" /></td>";
                    html += "<td>&nbsp;</td>";
                    html += "<td>Estado: </td><td>" + DatosDinamicos.combo("est", "", estado11, funcion, " TODOS ")+"</td>";
                    /*html += "<td>Estado: </td><td>" + DatosDinamicos.combo("est", "", estado11, funcion, " TODOS ")+" "
                            + " &nbsp;&nbsp;&nbsp;&nbsp; Sector: " + DatosDinamicos.combo(rsSectores, "idSec", "", funcion, " TODOS ", 200) + "</td>";*/
                    html += "</tr>";
                    
                    html += "<tr>";
                    html += "<td colspan='2' align='right'><input type='button' value='Filtrar clientes' onclick='"+funcion+"' /></td>";
                    /*html += "<td>&nbsp;</td>";
                    html += "<td colspan='2' align='right'>Antena: " + DatosDinamicos.combo(rsAntenas, "idAnt", "", funcion, " TODAS ", 100)
                            + " &nbsp;&nbsp;&nbsp;&nbsp; Plan: " + DatosDinamicos.combo(rsPlnSer, "idPlSe", "", funcion, " TODOS ", 200) 
                            + " &nbsp;&nbsp;&nbsp;&nbsp; <input type='button' value='Filtrar soportes' onclick='fac_soporteBuscar()'></td>";*/
                    html += "<td colspan='3' align='right'><input type='button' value='Filtrar soportes' onclick='fac_soporteBuscar()'></td>";
                    html += "</tr></table>";

                break;

                case 22:
                    mes = Fecha.getMes()-1;

                    cierre = new Cierre(this._ip, this._puerto, this._db, usuario, clave);
                    fecha = cierre.getPrimerCierre();
                    cierre.cerrar();

                    anio_fin = Fecha.getAnio();
                    anio_ini = anio_fin;
                    anio_declaracion = anio_fin;
                    if(fecha.compareTo("")!=0){
                        String vecFecha [] = fecha.split("-");
                        anio_ini = Integer.valueOf(vecFecha[0]);
                    }
                    if(mes<=0){
                        anio_declaracion--;
                        mes = 12;
                    }
                    cad_anio="<select id=\"an\">";
                    for(int i=anio_ini; i<=anio_fin; i++){
                        sel = (i==anio_declaracion) ? "selected" : "";
                        cad_anio+="<option "+sel+" value=\"" + i + "\">" + i + "</option>";
                    }
                    cad_anio+="</select>";

                    String hoy = Fecha.getFecha("SQL");

                    String [][] orden = {{"razon_social","NOMBRES"},{"ip","IP"}};

                    html += "obj»vta_im_html^frm»";
                    html += "<input type='radio' id='z0' name='z' checked='checked' value='26' /> <span onclick=\"_('z0').checked='true'\">Recaudo desde <input type='text' id='fi' value='"+hoy+"' size='9' readOnly><input type='button' value='...' onClick=\"SelectorFecha.crear('fi', 'SQL');\" /> &nbsp; hasta <input type='text' id='ff' value='"+hoy+"' size='9' readOnly><input type='button' value='...' onClick=\"SelectorFecha.crear('ff', 'SQL');\" /></span>";
                    html += "<br><input type='radio' id='z2' name='z' value='36' /> <span onclick=\"_('z2').checked='true'\">Recaudaci&oacute;n per&iacute;odo: A&ntilde;o"+cad_anio+" &nbsp; mes"+DatosDinamicos.combo("ms", String.valueOf(mes), meses, "")+"  &nbsp;&nbsp;&nbsp; Ordenado por: "+DatosDinamicos.combo("idCmb", "", orden, "", "")+"</span>";
                    html += "<br><input type='radio' id='z5' name='z' value='38' /> <span onclick=\"_('z5').checked='true'\">Recaudaci&oacute;n del &uacute;ltimo per&iacute;odo</span>";
                    html += "<br><input type='radio' id='z1' name='z' value='27' /> <span onclick=\"_('z1').checked='true'\">Valores por recaudar desde <input type='text' id='bfi' value='01/"+ Fecha.getMes() + "/" + Fecha.getAnio() + "' size='9' readOnly><input type='button' value='...' onClick=\"SelectorFecha.crear('bfi', 'SQL');\" /> &nbsp; hasta <input type='text' id='bff' value='"+hoy+"' size='9' readOnly><input type='button' value='...' onClick=\"SelectorFecha.crear('bff', 'SQL');\" /></span>";
                    html += "<br><input type='radio' id='z3' name='z' value='28' /> <span onclick=\"_('z3').checked='true'\">Clientes a suspender el servicio por mora </span>";
                    html += "<br><input type='radio' id='z4' name='z' value='37' /> <span onclick=\"_('z4').checked='true'\">Servicios de Internet con pago anticipado para el per&iacute;odo: <input type='text' id='pd' value='"+hoy+"' size='9' readOnly><input type='button' value='...' onClick=\"SelectorFecha.crear('pd', 'SQL');\" /></span>";
                    html += "<br><input type='radio' id='z6' name='z' value='56' /> <span onclick=\"_('z6').checked='true'\">Servicios de Internet suspendido para el per&iacute;odo: <input type='text' id='pd1' value='"+hoy+"' size='9' readOnly><input type='button' value='...' onClick=\"SelectorFecha.crear('pd1', 'SQL');\" /></span>";
                    
                    html += "<hr><input type='radio' id='x' name='x' checked /> PDF &nbsp;&nbsp;&nbsp;&nbsp;&nbsp; <input type='radio' id='x2' name='x' /> Excel";
                    html += "<br><br><input type='button' value='Imprimir' onclick='imprimirReporte();' />";
                break;

                case 23:
                    ResultSet rsEmpleados = objEmpleado.getEmpleadosActivos();

                    String c = "<select id='_ax_'><option value='c'>Clientes</option><option value='s'>SAITEL</option></select>";
                    
                    html = "obj»vta_im_html^frm»";
                    html += "<input type='radio' id='z0' name='z' checked='checked' value='14' /> <span onclick=\'_('z0').checked='true'\'>Activos fijos </span>";
                    html += "<br><input type='radio' id='z1' name='z' value='15' /> <span onclick=\'_('z1').checked='true'\'>Depreciaciones </span>";
                    html += "<br><input type='radio' id='z2' name='z' value='39' /> <span onclick=\'_('z2').checked='true'\'>Activos en: "+c.replace("_ax_", "bfi")+"</span>";
                    html += "<br><input type='radio' id='z3' name='z' value='43' /> <span onclick=\'_('z3').checked='true'\'>Activos consolidado en: "+c.replace("_ax_", "bff")+"</span>";
                    html += "<br><input type='radio' id='z4' name='z' value='45' /> <span onclick=\'_('z4').checked='true'\'>Activos en custodio: "+DatosDinamicos.combo(rsEmpleados, "an", "", "", 280)+" &nbsp; <input type='button' onclick='pdfImpCodigoBarras();' value='imprimir c&oacute;digo de barras' /></span>";
                    html += "<br><input type='radio' id='z5' name='z' value='55' /> <span onclick=\'_('z5').checked='true'\'>Equipos con &uacute;ltimas personalizaciones en clientes </span>";
                    html += "<hr><input type='radio' id='x' name='x' checked /> PDF &nbsp;&nbsp;&nbsp;&nbsp;&nbsp; <input type='radio' id='x2' name='x' /> Excel";
                    html += "<br><br><input type='button' value='Imprimir' onclick='imprimirReporte2();' />";
                    
                    
                break;

                case 24:
                    funcion = "fac_arqueoBuscar()";
                    html = "obj»d_filtro^fun»"+funcion+"^frm»";
                    html += "Desde la fecha: <input type='text' id='fi' value='"+fecha_uno+"' size='10' readOnly><input type='button' value='...' onClick=\"SelectorFecha.crear('fi', 'SQL');\" />"+
                        "<br />Hasta la fecha: <input type='text' id='ff' value='"+fecha_actual+"' size='10' readOnly><input type='button' value='...' onClick=\"SelectorFecha.crear('ff', 'SQL');\" />";
                    if(!objSucursal.enResponsablesCajas(usuario)){
                        html += "<br />Responsable: " + DatosDinamicos.combo(rsResponsables, "resp", usuario, funcion, " - TODOS - ")+"<input type='hidden' id='adm' value='1' />";
                    }else{
                        html += "<input type='hidden' id='resp' value='"+usuario+"' /><input type='hidden' id='adm' value='0' />";
                    }                    
                    if(funcion.compareTo("")!=0){
                        html += " &nbsp;<input type='button' value='Filtrar' onclick='"+funcion+"'>";
                    }
                break;

                case 25:
                    funcion = request.getParameter("fn");
                    html = "obj»d_filtro^fun»"+funcion+";_('t_b').focus();^frm»";
                    if(Integer.valueOf(id_sucursal_matriz) == id_sucursal){
                        html += "Sucursal: " + DatosDinamicos.combo(rsSucursales, "idSuc", String.valueOf(id_sucursal), funcion, " - TODAS - ", 140) + "<br />";
                    }else{
                        html += "<input type='hidden' id='idSuc' value='"+String.valueOf(id_sucursal)+"' />";
                    }
                    //html += "Clientes: <select id='grp' onchange='"+funcion+"'><option value='1'>Todos</option><option value='0'>Del Internet</option></select>";
                    html += "RUC, raz&oacute;n social, Nro. contrato: <input type='text' id='t_b' onkeypress=\"var c=(_esN)?event.which:event.keyCode;if(c==13){"+funcion+";}\" />";
                    if(funcion.compareTo("")!=0){
                        html += " &nbsp;<input type='button' value='Filtrar' onclick='"+funcion+"'>";
                    }
                break;

                case 26:
                    funcion = request.getParameter("fn");
                    html = "obj»d_filtro^fun»"+funcion+";_('t_b').focus();^frm»";
                    if(Integer.valueOf(id_sucursal_matriz) == id_sucursal){
                        html += "Sucursal: " + DatosDinamicos.combo(rsSucursales, "idSuc", String.valueOf(id_sucursal), funcion, "", 140) + "<br />";
                    }else{
                        html += "<input type='hidden' id='idSuc' value='"+String.valueOf(id_sucursal)+"' />";
                    }
                    html += "RUC, raz&oacute;n social: <input type='text' id='t_b' onkeypress=\"var c=(_esN)?event.which:event.keyCode;if(c==13){"+funcion+";}\" />";
                    if(funcion.compareTo("")!=0){
                        html += " &nbsp;<input type='button' value='Filtrar' onclick='"+funcion+"'>";
                    }
                break;

                case 27:
                    funcion = request.getParameter("fn");
                    html = "obj»d_filtro^fun»"+funcion+";_('t_b').focus();^frm»";
                    if(Integer.valueOf(id_sucursal_matriz) == id_sucursal){
                        html += "Sucursal: " + DatosDinamicos.combo(rsSucursales, "idSuc", String.valueOf(id_sucursal), funcion, "", 140) + "<br />";
                    }else{
                        html += "<input type='hidden' id='idSuc' value='"+String.valueOf(id_sucursal)+"' />";
                    }
                    html += "Nodo: <input type='text' id='t_b' onkeypress=\"var c=(_esN)?event.which:event.keyCode;if(c==13){"+funcion+";}\" />";
                    if(funcion.compareTo("")!=0){
                        html += " &nbsp;<input type='button' value='Filtrar' onclick='"+funcion+"'>";
                    }
                break;

                case 28:
                    funcion = request.getParameter("fn");
                    html = "obj»d_filtro^fun»"+funcion+";_('t_b').focus();^frm»";
                    if(Integer.valueOf(id_sucursal_matriz) == id_sucursal){
                        html += "Sucursal: " + DatosDinamicos.combo(rsSucursales, "idSuc", String.valueOf(id_sucursal), funcion, "", 140) + "<br />";
                    }else{
                        html += "<input type='hidden' id='idSuc' value='"+String.valueOf(id_sucursal)+"' />";
                    }
                    html += "Sector: <input type='text' id='t_b' onkeypress=\"var c=(_esN)?event.which:event.keyCode;if(c==13){"+funcion+";}\" />";
                    if(funcion.compareTo("")!=0){
                        html += " &nbsp;<input type='button' value='Filtrar' onclick='"+funcion+"'>";
                    }
                break;

                case 29:
                    rsEmpleados = objEmpleado.getEmpleadosTareas(id_sucursal);
                    String [][] pendiente1 = {{"0","Generadas"},{"1","Revisar"},{"3","En observaci&oacute;n"},{"2","Solucionar"},{"9","Solucionadas"}};

                    funcion = request.getParameter("fn");
                    html = "obj»d_filtro^foc»t_b^fun»"+funcion+"^frm»";
                    /*html += "<div class='filtro_mas' onmouseover=\"this.style.backgroundColor='#F2E1C6'\" onmouseout=\"this.style.backgroundColor='#F9F1E6'\">"
                            + "<img src='img/filtro+.png' onmouseover=\"fac_trabajoMenu()\" /></div>";*/
                    html += "<table cellspacing='0' cellpadding='0'>";
                    if( Integer.valueOf(id_sucursal_matriz)==id_sucursal && !objRol.getAcceso(id_rol, "hojaRutaImprimir") ){
                        html += "<tr><td>Sucursal: </td><td colspan='3'>" + DatosDinamicos.combo(rsSucursales, "idSuc", String.valueOf(id_sucursal), funcion, "", 140) + "</td></tr>";
                    }else{
                        html += "<input type='hidden' id='idSuc' value='"+String.valueOf(id_sucursal)+"' />";
                    }
                    if(!objRol.getAcceso(id_rol, "hojaRutaImprimir")){
                        html += "<tr><td>Estado: </td><td colspan='3'>" + DatosDinamicos.combo("est", "0", pendiente1, funcion, "")+"</td></tr>";
                        html += "<tr><td>T&eacute;cnico responsable: </td><td>"+DatosDinamicos.combo(rsEmpleados, "resp", "", funcion, " TODOS ")+" ";
                    }else{
                        html += "<input type='hidden' id='est' value='2' />";
                        html += "<input type='hidden' id='pend' value='true' />";
                        html += "<input type='hidden' id='resp' value='"+objEmpleado.getIdEmpleado(usuario)+"' />";
                    }
                    html += "</table>";

                break;

                case 30:
                    funcion = request.getParameter("fn");
                    html = "obj»d_filtro^fun»"+funcion+"^frm»";
                    if(Integer.valueOf(id_sucursal_matriz) == id_sucursal){
                        html += "<br />Sucursal: " + DatosDinamicos.combo(rsSucursales, "idSuc", "", funcion, "", 140);
                    }else{
                        html += "<input type='hidden' id='idSuc' value='"+String.valueOf(id_sucursal)+"' />";
                    }
                    html += "<br />MATERIAL: &nbsp; <input type='text' id='t_b' onkeypress=\"var c=(_esN)?event.which:event.keyCode;if(c==13){"+funcion+";}\" />";
                    if(funcion.compareTo("")!=0){
                        html += " &nbsp;<input type='button' value='Filtrar' onclick='"+funcion+"'>";
                    }
                break;

                case 31:
                    rsCategorias = objComun.getComun("CAT-");
                    marca = "";
                    try{
                        if(rsCategorias.next()){
                            marca = (rsCategorias.getString("id_comun")!=null) ? rsCategorias.getString("id_comun") : "";
                            rsCategorias.beforeFirst();
                        }
                    }catch(Exception e){
                        e.printStackTrace();
                    }

                    funcion = request.getParameter("fn");
                    html = "obj»d_filtro^fun»"+funcion+"^frm»";
                    if(Integer.valueOf(id_sucursal_matriz) == id_sucursal){
                        html += "Sucursal: " + DatosDinamicos.combo(rsSucursales, "idSuc", "", funcion, " EMPRESA ", 140);
                    }else{
                        html += "<input type='hidden' id='idSuc' value='"+id_sucursal+"' />";
                    }
                    html += "<br />Categor&iacute;a: " + DatosDinamicos.combo(rsCategorias, "idCat", marca, funcion, " TODAS ", 140);
                    html += "<br />Desde:<input type='text' id='fi' value='"+fecha_uno+"' size='10' readOnly><input type='button' value='...' onClick=\"SelectorFecha.crear('fi', 'SQL');\" />"+
                        " &nbsp; hasta:<input type='text' id='ff' value='"+fecha_actual+"' size='10' readOnly><input type='button' value='...' onClick=\"SelectorFecha.crear('ff', 'SQL');\" />";
                    html += "<br />C&oacute;digo o descripci&oacute;n: <input type='text' id='t_b' onkeypress=\"var c=(_esN)?event.which:event.keyCode;if(c==13){"+funcion+";}\" />";
                    html += " &nbsp;<input type='button' value='Filtrar' onclick='"+funcion+"'>";

                    try{
                        rsCategorias.close();
                    }catch(Exception e){
                        e.printStackTrace();
                    }
                break;

                case 32:
                    funcion = request.getParameter("fn");
                    html = "obj»d_filtro^fun»"+funcion+"^frm»";
                    html += "<div id='axSucur'>Sucursal: ";
                    if(Integer.valueOf(id_sucursal_matriz) == id_sucursal){
                        html += DatosDinamicos.combo(rsSucursales, "idSuc", "", funcion, " EMPRESA ", 140);
                    }else{
                        html += "<input type='hidden' id='idSuc' value='"+String.valueOf(id_sucursal)+"' />";
                    }
                    html += " &nbsp;<input type='button' value='Filtrar' onclick='"+funcion+"'>";
                    html += "</div>";
                break;
                    
                case 33:
                    ResultSet rsEmpleado = objEmpleado.getareas("false");
                    ResultSet rsEmpleadoCargo = objEmpleado.getCargos("false");
                    funcion = request.getParameter("fn");
                    html = "obj»d_filtro^fun»"+funcion+"^frm»";
                    html += "<div id='axSucur'>Sucursal: ";
                    if(Integer.valueOf(id_sucursal_matriz) == id_sucursal){
                        html += "Sucursal: " + DatosDinamicos.combo(rsSucursales, "idSuc", "", funcion, " EMPRESA ", 140);
                    }else{
                        html += "<input type='hidden' id='idSuc' value='"+id_sucursal+"' />";
                    }
                    html += "<br />Departamento: " + DatosDinamicos.combo(rsEmpleado, "id_area", "", "cargarCargo()", " TODAS ", 140);
                    html += "<br /><div id='id_car'>Cargo: " + DatosDinamicos.combo(rsEmpleadoCargo, "id_cargo", "", funcion, " TODAS ", 140)+"</div>";
                    html += "<br />C&eacute;dula, nombres: <input type='text' id='t_b' onkeypress=\"var c=(_esN)?event.which:event.keyCode;if(c==13){"+funcion+";}\" />";
                    html += " &nbsp;<input type='button' value='Filtrar' onclick='"+funcion+"'>";
                    
                    try{
                        rsEmpleado.close();
                        rsEmpleadoCargo.close();
                    }catch(Exception e){
                        e.printStackTrace();
                    }
                break;
                    
                case 34:
                    html = "obj»d_filtro^fun»fac_comisionBuscar()^frm»";
                    html += "<div>Desde:<input type='text' id='fi' value='"+fecha_uno+"' size='10' readOnly><input type='button' value='...' onClick=\"SelectorFecha.crear('fi', 'SQL');\" />"+
                        " &nbsp; hasta:<input type='text' id='ff' value='"+fecha_actual+"' size='10' readOnly><input type='button' value='...' onClick=\"SelectorFecha.crear('ff', 'SQL');\" />";
                    html += "</div>";
                    
                    html += "<div id='axSucur'>Sucursal: ";
                    if(Integer.valueOf(id_sucursal_matriz) == id_sucursal){
                        html += DatosDinamicos.combo(rsSucursales, "idSuc", "", "fac_comisionBuscar()", " EMPRESA ", 200);
                    }else{
                        html += "<input type='hidden' id='idSuc' value='"+String.valueOf(id_sucursal)+"' />";
                    }
                    html += " &nbsp; <input type='button' value='Filtrar' onclick='fac_comisionBuscar()' /></div>";
                break;
                    
            }

            try{
                rsProveedores.close();
                rsSucursales.close();
            }catch(Exception e){
                e.printStackTrace();
            }

            out.print(html);
            
        } finally {
            objPlanServicio.cerrar();
            objEmpleado.cerrar();
            objComun.cerrar();
            objSuc.cerrar();
            objSucursal.cerrar();
            objSector.cerrar();
            objProveedor.cerrar();
            objAntena.cerrar();
            conf.cerrar();
            objRol.cerrar();
            
            out.close();
        }
    } 

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /** 
     * Handles the HTTP <code>GET</code> method.
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        processRequest(request, response);
    } 

    /** 
     * Handles the HTTP <code>POST</code> method.
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        processRequest(request, response);
    }

    /** 
     * Returns a short description of the servlet.
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}
