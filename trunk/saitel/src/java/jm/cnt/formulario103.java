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
public class formulario103 extends HttpServlet {
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
        int fin_mes = Fecha.getUltimoDiaMes(anio, mes);

        response.setContentType("text/xml;");
        response.setHeader("Content-disposition", "attachment; filename="+this._db.replace("db_", "")+"_103ORI_"+Addons.getMesSRI(mes)+anio+".xml;");
        response.setHeader("Pragma", "no-cache");
        response.setHeader("Expires", "Mon, 01 Jan 2001 00:00:01 GMT");
        response.setHeader("Cache-Control", "no-store");
        response.setHeader("Cache-Control", "must-revalidate");
        response.setHeader("Cache-Control", "no-cache");
        PrintWriter out = response.getWriter();


        Configuracion conf = new Configuracion(this._ip, this._puerto, this._db, usuario, clave);
        String razon_social = conf.getValor("razon_social").toUpperCase();
        String ruc = conf.getValor("ruc");
        String num_identificacion = conf.getValor("num_ident");

        DataBase objDB = new DataBase(this._ip, this._puerto, this._db, usuario, clave);
        ResultSet registros = objDB.consulta("select P.id_plan_cuenta, P.id_padre, P.codigo_cuenta, P.nombre_cuenta, case P.tipo_cuenta when 0 then sum(D.debe)-sum(D.haber) when 1 then sum(D.haber)-sum(D.debe) else 0.00 end, P.campo_form_103 " +
                            "from tbl_plan_cuenta as P left outer join tbl_libro_diario_mayor as D on P.id_plan_cuenta=D.id_plan_cuenta " +
                            "where (P.tipo_cuenta=-1 or D.fecha_asiento between '"+anio+"-"+mes+"-01' and '"+anio+"-"+mes+"-"+fin_mes+"') or P.id_plan_cuenta=1 " +
                            "group by P.codigo_cuenta,P.nombre_cuenta,P.id_plan_cuenta,P.id_padre,P.tipo_cuenta,P.campo_form_103  " +
                            "ORDER BY P.id_padre,P.codigo_cuenta;");

        String datos[][] = Addons.calcularTotalesBalance(registros, 0, 0);

        try {
            float _302 = Float.valueOf(Addons.getValorCampoSRI(datos,"302"));
            float _303 = Float.valueOf(Addons.getValorCampoSRI(datos,"303"));
            float _304 = Float.valueOf(Addons.getValorCampoSRI(datos,"304"));
            float _307 = Float.valueOf(Addons.getValorCampoSRI(datos,"307"));
            float _308 = Float.valueOf(Addons.getValorCampoSRI(datos,"308"));
            float _309 = Float.valueOf(Addons.getValorCampoSRI(datos,"309"));
            float _310 = Float.valueOf(Addons.getValorCampoSRI(datos,"310"));
            float _312 = Float.valueOf(Addons.getValorCampoSRI(datos,"312"));
            float _319 = Float.valueOf(Addons.getValorCampoSRI(datos,"319"));
            float _320 = Float.valueOf(Addons.getValorCampoSRI(datos,"320"));
            float _322 = Float.valueOf(Addons.getValorCampoSRI(datos,"322"));
            float _323 = Float.valueOf(Addons.getValorCampoSRI(datos,"323"));
            float _325 = Float.valueOf(Addons.getValorCampoSRI(datos,"325"));
            float _327 = Float.valueOf(Addons.getValorCampoSRI(datos,"327"));
            float _328 = Float.valueOf(Addons.getValorCampoSRI(datos,"328"));
            float _332 = Float.valueOf(Addons.getValorCampoSRI(datos,"332"));
            float _340 = Float.valueOf(Addons.getValorCampoSRI(datos,"340"));
            float _341 = Float.valueOf(Addons.getValorCampoSRI(datos,"341"));
            float _342 = Float.valueOf(Addons.getValorCampoSRI(datos,"342"));
            float _343 = Float.valueOf(Addons.getValorCampoSRI(datos,"343"));
            float _344 = Float.valueOf(Addons.getValorCampoSRI(datos,"344"));
            double _349 = Addons.redondear(_302 + _303 + _304 + _307 + _308 + _309 + _310 + _312 +
                    _319 + _320 + _322 + _323 + _325 + _327 + _328 + _332 + _340 + _341 + _342 + _343 + _344);

            double _353 = Addons.redondear(_303 * 10 / 100);
            double _354 = Addons.redondear(_304 * 8 / 100);
            double _357 = Addons.redondear(_307 * 2 / 100);
            double _358 = Addons.redondear(_308 * 2 / 100);
            double _359 = Addons.redondear(_309 * 1 / 100);
            double _360 = Addons.redondear(_310 * 1 / 100);
            double _362 = Addons.redondear(_312 * 1 / 100);
            double _369 = Addons.redondear(_319 * 1 / 100);
            double _370 = Addons.redondear(_320 * 8 / 100);
            double _372 = Addons.redondear(_322 * 1 / 100);
            double _373 = Addons.redondear(_323 * 2 / 100);
            double _375 = Addons.redondear(_325 * 15 / 100);
            double _377 = Addons.redondear(_327 * 0.2 / 100);
            double _378 = Addons.redondear(_328 * 0.3 / 100);
            double _390 = Addons.redondear(_340 * 1 / 100);
            double _391 = Addons.redondear(_341 * 2 / 100);
            double _392 = Addons.redondear(_342 * 8 / 100);
            double _393 = Addons.redondear(_343 * 23 / 100);
            double _394 = Float.valueOf(Addons.getValorCampoSRI(datos,"394"));
            double _399 = Addons.redondear(_353 + _354 + _357 + _358 + _359 + _360 + _362 + _369 +
                    _370 + _372 + _373 + _375 + _377 + _378 + _390 + _391 + _392 + _393 + _394);              /* 499 = 902 = 999 = 905 = 399 */


            float _401 = Float.valueOf(Addons.getValorCampoSRI(datos,"401"));
            float _403 = Float.valueOf(Addons.getValorCampoSRI(datos,"403"));
            float _405 = Float.valueOf(Addons.getValorCampoSRI(datos,"405"));
            float _421 = Float.valueOf(Addons.getValorCampoSRI(datos,"421"));
            float _427 = Float.valueOf(Addons.getValorCampoSRI(datos,"427"));
            double _429 = _401 + _403 + _405 + _421 + _427;

            double _453 = Addons.redondear(_403 * 23 / 100);
            double _455 = Addons.redondear(_405 * 23 / 100);
            double _498 = _453 + _455;

            double _499 = _399 + _498;
            float _898 = Float.valueOf(Addons.getValorCampoSRI(datos,"898"));
            double _902 = _499 + _898;
            float _903 = Float.valueOf(Addons.getValorCampoSRI(datos,"903"));
            float _904 = Float.valueOf(Addons.getValorCampoSRI(datos,"904"));
            double _999 = _902 + _903 + _904;


            String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"+
"<formulario version=\"0.2\">"+
"<cabecera>"+
"<codigo_version_formulario>03201201</codigo_version_formulario>"+
"<ruc>"+ruc+"</ruc>"+
"<codigo_moneda>1</codigo_moneda>"+
"</cabecera>"+
"<detalle>"+
"<campo numero=\"31\">O</campo>"+
"<campo numero=\"101\">"+mes+"</campo>"+
"<campo numero=\"102\">"+anio+"</campo>"+
"<campo numero=\"104\"></campo>"+
"<campo numero=\"198\">"+num_identificacion+"</campo>"+
"<campo numero=\"199\"></campo>"+
"<campo numero=\"201\">"+ruc+"</campo>"+
"<campo numero=\"202\"><![CDATA["+razon_social+"]]></campo>"+
"<campo numero=\"302\">"+Addons.truncar(_302)+"</campo>"+
"<campo numero=\"303\">"+Addons.truncar(_303)+"</campo>"+
"<campo numero=\"304\">"+Addons.truncar(_304)+"</campo>"+
"<campo numero=\"307\">"+Addons.truncar(_307)+"</campo>"+
"<campo numero=\"308\">"+Addons.truncar(_308)+"</campo>"+
"<campo numero=\"309\">"+Addons.truncar(_309)+"</campo>"+
"<campo numero=\"310\">"+Addons.truncar(_310)+"</campo>"+
"<campo numero=\"312\">"+Addons.truncar(_312)+"</campo>"+
"<campo numero=\"319\">"+Addons.truncar(_319)+"</campo>"+
"<campo numero=\"320\">"+Addons.truncar(_320)+"</campo>"+
"<campo numero=\"322\">"+Addons.truncar(_322)+"</campo>"+
"<campo numero=\"323\">"+Addons.truncar(_323)+"</campo>"+
"<campo numero=\"325\">"+Addons.truncar(_325)+"</campo>"+
"<campo numero=\"327\">"+Addons.truncar(_327)+"</campo>"+
"<campo numero=\"328\">"+Addons.truncar(_328)+"</campo>"+
"<campo numero=\"332\">"+Addons.truncar(_332)+"</campo>"+
"<campo numero=\"340\">"+Addons.truncar(_340)+"</campo>"+
"<campo numero=\"341\">"+Addons.truncar(_341)+"</campo>"+
"<campo numero=\"342\">"+Addons.truncar(_342)+"</campo>"+
"<campo numero=\"343\">"+Addons.truncar(_343)+"</campo>"+
"<campo numero=\"344\">"+Addons.truncar(_344)+"</campo>"+
"<campo numero=\"349\">"+Addons.truncar(_349)+"</campo>"+
"<campo numero=\"352\">"+Addons.getValorCampoSRI(datos,"352")+"</campo>"+
"<campo numero=\"353\">"+Addons.truncar(_353)+"</campo>"+
"<campo numero=\"354\">"+Addons.truncar(_354)+"</campo>"+
"<campo numero=\"357\">"+Addons.truncar(_357)+"</campo>"+
"<campo numero=\"358\">"+Addons.truncar(_358)+"</campo>"+
"<campo numero=\"359\">"+Addons.truncar(_359)+"</campo>"+
"<campo numero=\"360\">"+Addons.truncar(_360)+"</campo>"+
"<campo numero=\"362\">"+Addons.truncar(_362)+"</campo>"+
"<campo numero=\"369\">"+Addons.truncar(_369)+"</campo>"+
"<campo numero=\"370\">"+Addons.truncar(_370)+"</campo>"+
"<campo numero=\"372\">"+Addons.truncar(_372)+"</campo>"+
"<campo numero=\"373\">"+Addons.truncar(_373)+"</campo>"+
"<campo numero=\"375\">"+Addons.truncar(_375)+"</campo>"+
"<campo numero=\"377\">"+Addons.truncar(_377)+"</campo>"+
"<campo numero=\"378\">"+Addons.truncar(_378)+"</campo>"+
"<campo numero=\"390\">"+Addons.truncar(_390)+"</campo>"+
"<campo numero=\"391\">"+Addons.truncar(_391)+"</campo>"+
"<campo numero=\"392\">"+Addons.truncar(_392)+"</campo>"+
"<campo numero=\"393\">"+Addons.truncar(_393)+"</campo>"+
"<campo numero=\"394\">"+Addons.truncar(_394)+"</campo>"+
"<campo numero=\"399\">"+Addons.truncar(_399)+"</campo>"+
"<campo numero=\"401\">"+Addons.truncar(_401)+"</campo>"+
"<campo numero=\"403\">"+Addons.truncar(_403)+"</campo>"+
"<campo numero=\"405\">"+Addons.truncar(_405)+"</campo>"+
"<campo numero=\"421\">"+Addons.truncar(_421)+"</campo>"+
"<campo numero=\"427\">"+Addons.truncar(_427)+"</campo>"+
"<campo numero=\"429\">"+Addons.truncar(_429)+"</campo>"+
"<campo numero=\"451\">"+Addons.getValorCampoSRI(datos,"451")+"</campo>"+
"<campo numero=\"453\">"+Addons.truncar(_453)+"</campo>"+
"<campo numero=\"455\">"+Addons.truncar(_455)+"</campo>"+
"<campo numero=\"471\">"+Addons.getValorCampoSRI(datos,"471")+"</campo>"+
"<campo numero=\"498\">"+Addons.truncar(_498)+"</campo>"+
"<campo numero=\"499\">"+Addons.truncar(_499)+"</campo>"+
"<campo numero=\"880\">"+Addons.getValorCampoSRI(datos,"880")+"</campo>"+
"<campo numero=\"890\">"+Addons.getValorCampoSRI(datos,"890")+"</campo>"+
"<campo numero=\"897\">"+Addons.getValorCampoSRI(datos,"897")+"</campo>"+
"<campo numero=\"898\">"+Addons.getValorCampoSRI(datos,"898")+"</campo>"+
"<campo numero=\"899\">"+Addons.getValorCampoSRI(datos,"899")+"</campo>"+
"<campo numero=\"902\">"+Addons.truncar(_902)+"</campo>"+
"<campo numero=\"903\">"+Addons.truncar(_903)+"</campo>"+
"<campo numero=\"904\">"+Addons.truncar(_904)+"</campo>"+
"<campo numero=\"905\">"+Addons.truncar(_999)+"</campo>"+
"<campo numero=\"907\">"+Addons.getValorCampoSRI(datos,"907")+"</campo>"+
"<campo numero=\"908\"></campo>"+
"<campo numero=\"909\">"+Addons.getValorCampoSRI(datos,"909")+"</campo>"+
"<campo numero=\"910\"></campo>"+
"<campo numero=\"911\">"+Addons.getValorCampoSRI(datos,"911")+"</campo>"+
"<campo numero=\"912\"></campo>"+
"<campo numero=\"913\">"+Addons.getValorCampoSRI(datos,"913")+"</campo>"+
//"<campo numero=\"914\"></campo>"+
"<campo numero=\"915\">"+Addons.getValorCampoSRI(datos,"915")+"</campo>"+
"<campo numero=\"922\">89</campo>"+
"<campo numero=\"999\">"+Addons.truncar(_999)+"</campo>"+
"</detalle>"+
"</formulario>";


            out.print(xml);
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
