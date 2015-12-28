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
import java.sql.ResultSet;

import javax.servlet.*;
import javax.servlet.http.*;
import jm.adm.clas.Documento;
import jm.fac.clas.Cliente;
import jm.fac.clas.Contrato;
import jm.seg.clas.Auditoria;
import jm.web.Cadena;

/**
 *
 * @author Jorge
 */
public class frmContratoGuardar extends HttpServlet {
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
        
        Contrato objContrato = new Contrato(this._ip, this._puerto, this._db, usuario, clave);
        
        try {
            String r = "msg»Ha ocurrido un error inesperado, por favor, vuelva a intentarlo más tarde o " +
                "contáctese con el administrador del sistema para mayor información.";
            //String WHERE = request.getParameter("WHERE");
            String id = request.getParameter("idCont");
            String num_contrato = request.getParameter("numCont");
            String id_cliente = request.getParameter("idCli");
            String id_sucursal = request.getParameter("idSucur");
            String fecha_contrato = request.getParameter("fechaCont");
            String fecha_termino = request.getParameter("fechaTer");
            String ruc_representante = request.getParameter("ruc_representante");
            String representante = request.getParameter("representante");
            //String contrato = request.getParameter("contrato");
            String contrato = "";

            Documento objDocumento = new Documento(this._ip, this._puerto, this._db, usuario, clave);
            try{
                ResultSet rsDoc = objDocumento.getDocumento("c");
                if(rsDoc.next()){
                    contrato = rsDoc.getString("documento")!=null ? rsDoc.getString("documento") : "";
                    rsDoc.close();
                }
            }catch(Exception e){
                e.printStackTrace();
            }finally{
                objDocumento.cerrar();
            }

            contrato = contrato.replace("<<numero>>", id_sucursal + "-" + num_contrato);
            contrato = contrato.replace("<<fecha_inicio>>", fecha_contrato);

            if(ruc_representante.compareTo("")!=0 && representante.compareTo("")!=0){
                Cliente objCliente = new Cliente(this._ip, this._puerto, this._db, usuario, clave);
                ResultSet rsCliente = objCliente.getCliente(id_cliente);
                String razon_social = "";
                try{
                    if(rsCliente.next()){
                        razon_social = (rsCliente.getString("razon_social")!=null) ? rsCliente.getString("razon_social") : "";
                        rsCliente.close();
                    }
                }catch(Exception ex){
                    ex.printStackTrace();
                }finally{
                    objCliente.cerrar();
                }
                contrato = contrato.replace("el SUBSCRIPTOR", "el/la Sr(a). "+Cadena.capital(representante)+
                        " en calidad de Representante de " + razon_social.toUpperCase() + " con " + (ruc_representante.length()==13 ? "R.U.C." : "C.I.") + " Nro. " + ruc_representante);
            }

            if(id.compareTo("-1")==0){
                String idContrato = objContrato.insertar(num_contrato, id_cliente, id_sucursal, fecha_contrato, fecha_termino, ruc_representante, representante, contrato);
                if(idContrato.compareTo("-1")!=0){
                    Auditoria auditoria = new Auditoria(this._ip, this._puerto, this._db, usuario, clave);
                    auditoria.setRegistro(request, "INGRESO DEL NUEVO CONTRATO Nro. "+id_sucursal+"-"+num_contrato);
                    auditoria.cerrar();
                    r = "fun»_('idCont').value="+idContrato+";_('btnImp').style.display='block';^msg»Información ingresada satisfactoriamente.";
                }
            }else{
                if(objContrato.actualizar(id, num_contrato, id_cliente, id_sucursal, fecha_contrato, fecha_termino, ruc_representante, representante, contrato)){
                    Auditoria auditoria = new Auditoria(this._ip, this._puerto, this._db, usuario, clave);
                    auditoria.setRegistro(request, "ACTUALIZACION DE LA INFORMACION DEL CONTRATO Nro. "+id_sucursal+"-"+num_contrato);
                    auditoria.cerrar();
                    r = "msg»Información guardada satisfactoriamente.";
                }
            }

            out.print(r);
        } finally {
            objContrato.cerrar();
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