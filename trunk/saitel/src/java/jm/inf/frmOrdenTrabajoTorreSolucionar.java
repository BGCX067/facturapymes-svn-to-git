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

package jm.inf;

import java.io.*;
import java.sql.ResultSet;

import javax.servlet.*;
import javax.servlet.http.*;
import jm.inf.clas.OrdenTrabajo;
import jm.seg.clas.Auditoria;
import jm.web.DataBase;

/**
 *
 * @author Jorge
 */
public class frmOrdenTrabajoTorreSolucionar extends HttpServlet {
    private String _ip = null;
    private int _puerto = 5432;
    private String _db = null;
    private String _usuario = null;
    private String _clave = null;

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
        this._usuario = (String)sesion.getAttribute("usuario");
        this._clave = (String)sesion.getAttribute("clave");

        response.setContentType("text/html;charset=UTF-8");
        response.setHeader("Pragma", "no-cache");
        response.setHeader("Expires", "Mon, 01 Jan 2001 00:00:01 GMT");
        response.setHeader("Cache-Control", "no-store");
        response.setHeader("Cache-Control", "must-revalidate");
        response.setHeader("Cache-Control", "no-cache");
        PrintWriter out = response.getWriter();


//      PARA LA PERSONALIZACIONE DE ACTIVOS
        /*Configuracion conf = new Configuracion(this._ip, this._puerto, this._db, usuario, clave);
        String ruc_empresa = conf.getValor("ruc");
        String empresa = conf.getValor("razon_social");
        conf.cerrar();

        String observacion = "";
        try{
            Documento objDocumento = new Documento(this._ip, this._puerto, this._db, usuario, clave);
            ResultSet rsDoc = objDocumento.getDocumento("p");
            if(rsDoc.next()){
                observacion = rsDoc.getString("documento")!=null ? rsDoc.getString("documento") : "";
                rsDoc.close();
            }
            objDocumento.cerrar();
        }catch(Exception e){}

        Activo objActivo = new Activo(this._ip, this._puerto, this._db, usuario, clave);
        long num_documento = objActivo.getNunDocumento();
        //String num_documento = sec_num_documento);

        Bodega objBodega = new Bodega(this._ip, this._puerto, this._db, usuario, clave);


        Instalacion objInstalacion = new Instalacion(this._ip, this._puerto, this._db, usuario, clave);

        Configuracion conf = new Configuracion(this._ip, this._puerto, this._db, usuario, clave);
        String ruc_empresa = conf.getValor("ruc");
        String empresa = conf.getValor("razon_social");
        conf.cerrar();

        String axInstall = "";

        Cliente objCliente = new Cliente(this._ip, this._puerto, this._db, usuario, clave);
*/
        Auditoria auditoria = new Auditoria(this._ip, this._puerto, this._db, this._usuario, this._clave);
        
        OrdenTrabajo objOrdenTrabajo = new OrdenTrabajo(this._ip, this._puerto, this._db, this._usuario, this._clave);

        String r = "msg»Ha ocurrido un error inesperado, por favor, vuelva a intentarlo más tarde o " +
                    "contáctese con el administrador del sistema para mayor información.";
        try {
            String WHERE = request.getParameter("WHERE");
            String p = request.getParameter("p") != null ? request.getParameter("p") : "0";
            //String pos = request.getParameter("i"); // posicion de la orden de trabjao
            String id_orden_trabajo = request.getParameter("id");
            //String id_instalacion = request.getParameter("idIns");
            //String numero_orden = request.getParameter("num_orden1");
            String fecha_solucion = request.getParameter("fecha_solucion");
            
            int i = 0;
            String id_tareas = "";
            String solucionado = "";
            String observacion = "";
            String macs_nuevas = "";
            String macs_retiradas = "";
            String ax_macs_nuevas = "";
            String sx_macs_retiradas = "";
            while(request.getParameter("id_tarea"+i)!=null){
                id_tareas += request.getParameter("id_tarea"+i) + ";";
                solucionado += request.getParameter("sol"+i) + ";";
                observacion += (request.getParameter("obs"+i).compareTo("")!=0 ? request.getParameter("obs"+i) : "ninguna") + ";";
                ax_macs_nuevas = "";
                sx_macs_retiradas = "";
                for(int j=0; j<20; j++){
                    if(request.getParameter("codAct"+i+j)!=null){
                        ax_macs_nuevas += request.getParameter("codAct"+i+j) + ",";
                    }
                    if(request.getParameter("codActRet"+i+j)!=null){
                        sx_macs_retiradas += request.getParameter("codActRet"+i+j) + ",";
                    }
                }
                if(ax_macs_nuevas.compareTo("")!=0){
                    ax_macs_nuevas = ax_macs_nuevas.substring(0, ax_macs_nuevas.length()-1);
                }
                if(sx_macs_retiradas.compareTo("")!=0){
                    sx_macs_retiradas = sx_macs_retiradas.substring(0, sx_macs_retiradas.length()-1);
                }
                macs_nuevas += (ax_macs_nuevas.compareTo("")!=0 ? ax_macs_nuevas : " " ) + ";";
                macs_retiradas += (sx_macs_retiradas.compareTo("")!=0 ? sx_macs_retiradas : " " ) + ";";
                i++;
            }
            if(id_tareas.compareTo("")!=0){
                id_tareas = id_tareas.substring(0, id_tareas.length()-1);
                solucionado = solucionado.substring(0, solucionado.length()-1);
                observacion = observacion.substring(0, observacion.length()-1);
                macs_nuevas = macs_nuevas.substring(0, macs_nuevas.length()-1);
                macs_retiradas = macs_retiradas.substring(0, macs_retiradas.length()-1);
            }

            String resSol = objOrdenTrabajo.solucionarInfraestructura(id_orden_trabajo, this._usuario, fecha_solucion, id_tareas, solucionado, observacion, macs_nuevas, macs_retiradas);
                
            if(resSol.compareTo("t")==0){
                r = "fun»fac_trabajoBuscar();_R('bloq_otr');_R('otr');^msg»Orden de trabajo solucionada satispaftoriamente";
            }else{
                r = "msg»"+objOrdenTrabajo.getError();
            }
            
        }catch(Exception e){
            e.printStackTrace();
            r = "msg»"+objOrdenTrabajo.getError() + ". " + e.getMessage();
        } finally {
            auditoria.cerrar();
            objOrdenTrabajo.cerrar();
        }
        
        try{
            out.print(r);
        }finally{
            out.close();
        }
        
    }

    public String[] getEmpleado(String id_orden_trabajo)
    {
        String res[] = new String[]{"","",""};
        DataBase objDataBase = new DataBase(this._ip, this._puerto, this._db, this._usuario, this._clave);
        try{
            ResultSet rs = objDataBase.consulta("select dni, nombre || ' ' || apellido as empleado, I.direccion_instalacion "
                    + "from (tbl_empleado as E inner join tbl_orden_trabajo as OT on E.id_empleado=OT.id_empleado) "
                    + "inner join tbl_instalacion as I on I.id_instalacion=OT.id_instalacion "
                    + "where OT.id_orden_trabajo="+id_orden_trabajo);
            if(rs.next()){
                res[0] = rs.getString("dni")!=null ? rs.getString("dni") : "";
                res[1] = rs.getString("empleado")!=null ? rs.getString("empleado") : "";
                res[2] = rs.getString("direccion_instalacion")!=null ? rs.getString("direccion_instalacion") : "";
                res.clone();
            }
        }catch(Exception e){
            e.printStackTrace();
        }finally{
            objDataBase.cerrar();
        }
        return res;
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