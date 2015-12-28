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

package jm.nom;

import java.io.*;
import java.sql.ResultSet;

import javax.servlet.*;
import javax.servlet.http.*;
import jm.nom.clas.permisosLicencias;
import jm.seg.clas.Auditoria;
import jm.web.Cadena;
import jm.web.Correo;

/**
 *
 * @author Jorge
 */
public class frmPermisoGuardar extends HttpServlet {
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
        String servidor_mail = "190.152.90.50";
        
        permisosLicencias objPermisosLicencias = new permisosLicencias(this._ip, this._puerto, this._db, usuario, clave);
        
        try {
                String id=request.getParameter("id");
            String id_tipo=request.getParameter("id_tipo");
            String nDias=request.getParameter("nDias");
            String nHoras=request.getParameter("nHoras");
            String nMinutos=request.getParameter("nMinutos");
            String fecha_permiso=request.getParameter("fecha_permiso");
            String recuperac=request.getParameter("recuperac");
            String hInicial=request.getParameter("hInicial");
            String descripcion=request.getParameter("descripcion");
            String  matr=request.getParameter("matriz");
            String  filas=request.getParameter("filas");
            StringBuilder mensaje=new StringBuilder();
            String [][] matriz= new String [Integer.parseInt(filas)][3];
            String id_empleado=request.getParameter("id_empleado");
            
            String email=" ";
            String emailJefeSucursal=" ";
            String nombre=" ";
            String apellido=" ";
            String nombreJefe=" ";
            
            if(recuperac.compareTo("2")==0){
                String[] numerosComoArray = matr.split(",");
                int limite=0;

                for (int i = 0; i < matriz.length; i++) {
                    for (int j = 0; j < matriz[i].length; j++) {
                        matriz[i][j] = numerosComoArray[limite];
                        limite++;
                    }
                }
            }
            
            String r = "";
            
            String id_retorno=objPermisosLicencias.insertar(id_tipo,nDias,nHoras,nMinutos,fecha_permiso,recuperac,hInicial,descripcion,usuario);
            for(int i=0;i<=1;i++){
                if(id_retorno.compareTo("-1")!=0){
                    if(recuperac.compareTo("2")==0){
                        if(!objPermisosLicencias.insertarTipo(id_retorno, matriz)){
                            objPermisosLicencias.eliminarPermiso(id_retorno);
                            r = "msg»" + objPermisosLicencias.getError();
                            break;
                        }
                    }
                    
                    try{
                            ResultSet rsInfoEmail = objPermisosLicencias.getEmpleadoDetalles(id_empleado);
                            if(rsInfoEmail.next()){
                                email = (rsInfoEmail.getString("email")!=null) ? rsInfoEmail.getString("email") : ""; 
                                emailJefeSucursal = (rsInfoEmail.getString("emailjefe")!=null) ? rsInfoEmail.getString("emailjefe") : "personal@saitel.ec"; 
                                nombre = (rsInfoEmail.getString("nombre")!=null) ? rsInfoEmail.getString("nombre") : ""; 
                                apellido = (rsInfoEmail.getString("apellido")!=null) ? rsInfoEmail.getString("apellido") : ""; 
                                nombreJefe = (rsInfoEmail.getString("nombrejefe")!=null) ? rsInfoEmail.getString("nombrejefe") : "Marco López"; 
                                rsInfoEmail.close();
                            }
                        }catch(Exception e){
                            e.printStackTrace();
                        }
                    
                    nombreJefe=Cadena.capital(nombreJefe);
                    nombre=Cadena.capital(nombre);
                    apellido=Cadena.capital(apellido);
                
                    mensaje.append("Estimado/a: <br>"+nombreJefe+"<br><br>");
                    mensaje.append("A través de la presente me dirijo a usted a fin de solicitarle permiso el día "+fecha_permiso+" desde las "+hInicial+" por un tiempo de "+nDias+" días "+nHoras+":"+nMinutos+" horas "
                            + ". por el siguiente motivo: "+descripcion+
                        "<br>Desde ya muy agradecido por su atención.<br><br>");
                    mensaje.append("Atentamente:<br> "+nombre+" "+apellido);
                    if(Correo.enviar(servidor_mail, email,"marcolopez@saitel.ec", emailJefeSucursal, email, "PEDIDO DE PERMISO PARA "+nombre+" "+apellido, mensaje, true)){
                        r += "msg»Guardado Correctamente.";
                    }
                    
                    Auditoria auditoria = new Auditoria(this._ip, this._puerto, this._db, usuario, clave);
                    auditoria.setRegistro(request, "GUARDADO PERMISO DE: "+usuario );
                    r = "fun»envioId('id',"+id_retorno+")^msg»Guardado Correctamente.";
                    auditoria.cerrar();
                }else{
                    r = "msg»" + objPermisosLicencias.getError();
                }
                break;
            }

            out.print(r);
        } finally {
            objPermisosLicencias.cerrar();
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
