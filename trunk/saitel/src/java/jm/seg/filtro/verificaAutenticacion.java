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

package jm.seg.filtro;

import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;
import jm.seg.clas.Usuario;

public class verificaAutenticacion implements Filter{
    private FilterConfig config;
    private String urlAutenticar; 
    
    private String _ip = null;
    private int _puerto = 5432;
    private String _db = null;
    
    public void init(FilterConfig config) throws ServletException
    {    
        this._ip = config.getServletContext().getInitParameter("_IP");
        this._puerto = Integer.parseInt(config.getServletContext().getInitParameter("_PUERTO"));
        this._db = config.getServletContext().getInitParameter("_DB");
        
        this.config = config;
        //Tambien se pueden cargar los parametros que configura la url de salida
        this.urlAutenticar = config.getInitParameter("Salir");
        if(urlAutenticar == null || urlAutenticar.trim().length() == 0) {
            //Error al cargar la página
            throw new ServletException("El tiempo de la sesión ha caducado, por favor, cierre el sistema y vuelva a ingresar.");
        }
    }
    
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws ServletException, IOException
    {
        HttpServletRequest miReq = (HttpServletRequest)req;
        HttpSession sesion = miReq.getSession(true);
        String usuario = (String)sesion.getAttribute("usuario");
        String clave = (String)sesion.getAttribute("clave");
        usuario = (usuario!=null) ? usuario : "";
        if(usuario.compareTo("")!=0){
            Usuario us = new Usuario(this._ip, this._puerto, this._db, usuario, clave);
            if(sesion.getId().compareTo(us.getSesion(usuario))==0){
                chain.doFilter(req, res);
            }else{
                //RequestDispatcher dispatcher = req.getRequestDispatcher("/"+urlAutenticar);
                //dispatcher.forward(req, res);
                ((HttpServletResponse)res).sendRedirect(((HttpServletRequest)req).getContextPath() + "/" + urlAutenticar);
            }
            us.cerrar();
        }else{
            ((HttpServletResponse)res).sendRedirect(((HttpServletRequest)req).getContextPath() + "/" + urlAutenticar);
        }
    }
    
    public void destroy() {
        config = null;
    }
}
