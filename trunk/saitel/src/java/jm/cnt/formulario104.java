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
public class formulario104 extends HttpServlet {
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
        response.setHeader("Content-disposition", "attachment; filename="+this._db.replace("db_", "")+"_104ORI_"+Addons.getMesSRI(mes)+anio+".xml;");
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
        ResultSet registros = objDB.consulta("select P.id_plan_cuenta, P.id_padre, P.codigo_cuenta, P.nombre_cuenta, case P.tipo_cuenta when 0 then sum(D.debe)-sum(D.haber) when 1 then sum(D.haber)-sum(D.debe) else 0.00 end, P.campo_form_104 " +
                            "from tbl_plan_cuenta as P left outer join tbl_libro_diario_mayor as D on P.id_plan_cuenta=D.id_plan_cuenta " +
                            "where (P.tipo_cuenta=-1 or D.fecha_asiento between '"+anio+"-"+mes+"-01' and '"+anio+"-"+mes+"-"+fin_mes+"') or P.id_plan_cuenta=1 " +
                            "group by P.codigo_cuenta,P.nombre_cuenta,P.id_plan_cuenta,P.id_padre,P.tipo_cuenta,P.campo_form_104  " +
                            "ORDER BY P.id_padre,P.codigo_cuenta;");

        /*ResultSet registros = objDB.consulta("select P.id_plan_cuenta, P.id_padre, P.codigo_cuenta, P.nombre_cuenta, case P.tipo_cuenta when 0 then D.suma_debe when 1 then D.suma_haber else 0.00 end, P.campo_form_104 " +
        "from tbl_plan_cuenta as P left outer join tbl_libro_diario_mayor as D on P.id_plan_cuenta=D.id_plan_cuenta " +
        "where (P.tipo_cuenta=-1 or (D.fecha_asiento between '"+anio+"-"+mes+"-01' and '"+anio+"-"+mes+"-"+fin_mes+"' and (D.suma_debe>0 or D.suma_haber>0) and " +
        "D.id_libro_diario_mayor in (select max(id_libro_diario_mayor) from vta_libro_diario_mayor where fecha_asiento between '"+anio+"-"+mes+"-01' and '"+anio+"-"+mes+"-"+fin_mes+"' and (suma_debe>0 or suma_haber>0) group by codigo_cuenta))) " +
        " or P.id_plan_cuenta=1 " +
        "ORDER BY P.id_padre,P.codigo_cuenta;");*/

        String datos[][] = Addons.calcularTotalesBalance(registros, 0, 0);

        try {
            int iva = 12;

            float _401 = Float.valueOf(Addons.getValorCampoSRI(datos,"401"));
            float _402 = Float.valueOf(Addons.getValorCampoSRI(datos,"402"));
            float _403 = Float.valueOf(Addons.getValorCampoSRI(datos,"403"));
            float _404 = Float.valueOf(Addons.getValorCampoSRI(datos,"404"));
            float _405 = Float.valueOf(Addons.getValorCampoSRI(datos,"405"));
            float _406 = Float.valueOf(Addons.getValorCampoSRI(datos,"406"));
            float _407 = Float.valueOf(Addons.getValorCampoSRI(datos,"407"));
            float _408 = Float.valueOf(Addons.getValorCampoSRI(datos,"408"));
            double _409 = Addons.redondear(_401 + _402 + _403 + _404 + _405 + _406 + _407 + _408);

            float _411 = Float.valueOf(Addons.getValorCampoSRI(datos,"411"));
            float _412 = Float.valueOf(Addons.getValorCampoSRI(datos,"411"));
            float _413 = Float.valueOf(Addons.getValorCampoSRI(datos,"413"));
            float _414 = Float.valueOf(Addons.getValorCampoSRI(datos,"414"));
            float _415 = Float.valueOf(Addons.getValorCampoSRI(datos,"415"));
            float _416 = Float.valueOf(Addons.getValorCampoSRI(datos,"416"));
            float _417 = Float.valueOf(Addons.getValorCampoSRI(datos,"417"));
            float _418 = Float.valueOf(Addons.getValorCampoSRI(datos,"418"));
            double _419 = Addons.redondear(_411 + _412 + _413 + _414 + _415 + _416 + _417 + _418);

            float _483 = Float.valueOf(Addons.getValorCampoSRI(datos,"483"));

            double _421 = Addons.redondear(_411 * iva / 100);
            double _422 = Addons.redondear(_412 * iva / 100);
            double _429 = Addons.redondear(_421 + _422);
            float _431 = Float.valueOf(Addons.getValorCampoSRI(datos,"431"));
            float _434 = Float.valueOf(Addons.getValorCampoSRI(datos,"434"));
            float _441 = Float.valueOf(Addons.getValorCampoSRI(datos,"441"));
            float _442 = Float.valueOf(Addons.getValorCampoSRI(datos,"442"));
            float _443 = Float.valueOf(Addons.getValorCampoSRI(datos,"443"));
            float _444 = Float.valueOf(Addons.getValorCampoSRI(datos,"444"));
            float _453 = Float.valueOf(Addons.getValorCampoSRI(datos,"453"));
            float _454 = Float.valueOf(Addons.getValorCampoSRI(datos,"454"));
            double _480 = Addons.redondear(_411 + _412);
            float _481 = Float.valueOf(Addons.getValorCampoSRI(datos,"481"));
            double _485 = Addons.redondear(_429 - _480);
            double _499 = Addons.redondear(_483 + _429);        /* campo 484 = 429 */

            float _500 = Float.valueOf(Addons.getValorCampoSRI(datos,"500"));
            float _501 = Float.valueOf(Addons.getValorCampoSRI(datos,"501"));
            float _502 = Float.valueOf(Addons.getValorCampoSRI(datos,"502"));
            float _503 = Float.valueOf(Addons.getValorCampoSRI(datos,"503"));
            float _504 = Float.valueOf(Addons.getValorCampoSRI(datos,"504"));
            float _505 = Float.valueOf(Addons.getValorCampoSRI(datos,"505"));
            float _506 = Float.valueOf(Addons.getValorCampoSRI(datos,"506"));
            float _507 = Float.valueOf(Addons.getValorCampoSRI(datos,"507"));
            float _508 = Float.valueOf(Addons.getValorCampoSRI(datos,"508"));
            double _509 = Addons.redondear(_500 + _501 + _502 + _503 + _504 + _505 + _506 + _507 + _508);

            float _510 = Float.valueOf(Addons.getValorCampoSRI(datos,"510"));
            float _511 = Float.valueOf(Addons.getValorCampoSRI(datos,"511"));
            float _512 = Float.valueOf(Addons.getValorCampoSRI(datos,"512"));
            float _513 = Float.valueOf(Addons.getValorCampoSRI(datos,"513"));
            float _514 = Float.valueOf(Addons.getValorCampoSRI(datos,"514"));
            float _515 = Float.valueOf(Addons.getValorCampoSRI(datos,"515"));
            float _516 = Float.valueOf(Addons.getValorCampoSRI(datos,"516"));
            float _517 = Float.valueOf(Addons.getValorCampoSRI(datos,"517"));
            float _518 = Float.valueOf(Addons.getValorCampoSRI(datos,"518"));
            double _519 = Addons.redondear(_510 + _511 + _512 + _513 + _514 + _515 + _516 + _517 + _518);

            double _520 = Addons.redondear(_510 * iva / 100);
            double _521 = Addons.redondear(_511 * iva / 100);
            double _522 = Addons.redondear(_512 * iva / 100);
            double _523 = Addons.redondear(_513 * iva / 100);
            double _524 = Addons.redondear(_514 * iva / 100);
            double _525 = Addons.redondear(_515 * iva / 100);
            double _529 = Addons.redondear(_520 + _521 + _522 + _523 + _524 + _525);
            
            float _531 = Float.valueOf(Addons.getValorCampoSRI(datos,"531"));
            float _532 = Float.valueOf(Addons.getValorCampoSRI(datos,"532"));
            float _535 = Float.valueOf(Addons.getValorCampoSRI(datos,"535"));
            float _541 = Float.valueOf(Addons.getValorCampoSRI(datos,"541"));
            float _452 = Float.valueOf(Addons.getValorCampoSRI(datos,"452"));
            float _543 = Float.valueOf(Addons.getValorCampoSRI(datos,"543"));
            float _545 = Float.valueOf(Addons.getValorCampoSRI(datos,"545"));
            
            //double _553 = Addons.redondear((_411 + _412 + _415 + _416 + _417 + _418) / _419, 4);
            float _544 = Float.valueOf(Addons.getValorCampoSRI(datos,"544"));
            double _554 = Addons.redondear(_544 * iva / 100);
            float _555 = Float.valueOf(Addons.getValorCampoSRI(datos,"555"));
            double _563 = (_411 + _412 + _415 + _416 + _417 + _418) / _419;
            double _564 = (_520 + _521 + _523 + _524 + _525) * _563;

            double aux = Addons.redondear(_499 - _564);
            double _601 = (aux>0)?aux:0;   
            double _602 = (aux<0)?aux:0;
            float _605 = Float.valueOf(Addons.getValorCampoSRI(datos,"605"));
            float _607 = Float.valueOf(Addons.getValorCampoSRI(datos,"607"));
            float _609 = Float.valueOf(Addons.getValorCampoSRI(datos,"609"));
            float _611 = Float.valueOf(Addons.getValorCampoSRI(datos,"611"));
            float _612 = Float.valueOf(Addons.getValorCampoSRI(datos,"612"));
            float _613 = Float.valueOf(Addons.getValorCampoSRI(datos,"613"));
            float _615 = Float.valueOf(Addons.getValorCampoSRI(datos,"615"));
            float _617 = Float.valueOf(Addons.getValorCampoSRI(datos,"617"));
            double _619 = _601 - _602 - _605 - _607 - _609 + _611 + _612 + _613;
            _619 = (_619>0)?_619:0;
            float _621 = Float.valueOf(Addons.getValorCampoSRI(datos,"621"));
            double _699 = _619 + _621;

            float _721 = Float.valueOf(Addons.getValorCampoSRI(datos,"721"));
            float _723 = Float.valueOf(Addons.getValorCampoSRI(datos,"723"));
            float _725 = Float.valueOf(Addons.getValorCampoSRI(datos,"725"));
            double _799 = _721 + _723 + _725;
            double _859 = _699 + _799;
            
            float _880 = Float.valueOf(Addons.getValorCampoSRI(datos,"880"));
            float _890 = Float.valueOf(Addons.getValorCampoSRI(datos,"890"));
            float _897 = Float.valueOf(Addons.getValorCampoSRI(datos,"897"));
            float _898 = Float.valueOf(Addons.getValorCampoSRI(datos,"898"));
            float _899 = Float.valueOf(Addons.getValorCampoSRI(datos,"899"));

            double _902 = _859 + _898;
            float _903 = Float.valueOf(Addons.getValorCampoSRI(datos,"903"));
            float _904 = Float.valueOf(Addons.getValorCampoSRI(datos,"904"));
            //float _905 = 0;
            float _906 = Float.valueOf(Addons.getValorCampoSRI(datos,"906"));
            float _907 = Float.valueOf(Addons.getValorCampoSRI(datos,"907"));
            float _909 = Float.valueOf(Addons.getValorCampoSRI(datos,"909"));
            float _911 = Float.valueOf(Addons.getValorCampoSRI(datos,"911"));
            float _913 = Float.valueOf(Addons.getValorCampoSRI(datos,"913"));
            float _915 = Float.valueOf(Addons.getValorCampoSRI(datos,"915"));
            float _917 = Float.valueOf(Addons.getValorCampoSRI(datos,"917"));
            float _919 = Float.valueOf(Addons.getValorCampoSRI(datos,"919"));

            String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>"+
"<formulario version=\"0.2\">"+
"<cabecera>"+
"<codigo_version_formulario>04201401</codigo_version_formulario>"+
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
//"<campo numero=\"202\"><![CDATA["+razon_social+"]]></campo>"+
"<campo numero=\"202\">"+razon_social.toUpperCase()+"</campo>"+
"<campo numero=\"401\">"+Addons.truncar(_401)+"</campo>"+
"<campo numero=\"402\">"+Addons.truncar(_402)+"</campo>"+
"<campo numero=\"403\">"+Addons.truncar(_403)+"</campo>"+
"<campo numero=\"404\">"+Addons.truncar(_404)+"</campo>"+
"<campo numero=\"405\">"+Addons.truncar(_405)+"</campo>"+
"<campo numero=\"406\">"+Addons.truncar(_406)+"</campo>"+
"<campo numero=\"407\">"+Addons.truncar(_407)+"</campo>"+
"<campo numero=\"408\">"+Addons.truncar(_408)+"</campo>"+
"<campo numero=\"409\">"+Addons.truncar(_409)+"</campo>"+
"<campo numero=\"411\">"+Addons.truncar(_411)+"</campo>"+
"<campo numero=\"412\">"+Addons.truncar(_412)+"</campo>"+
"<campo numero=\"413\">"+Addons.truncar(_413)+"</campo>"+
"<campo numero=\"414\">"+Addons.truncar(_414)+"</campo>"+
"<campo numero=\"415\">"+Addons.truncar(_415)+"</campo>"+
"<campo numero=\"416\">"+Addons.truncar(_416)+"</campo>"+
"<campo numero=\"417\">"+Addons.truncar(_417)+"</campo>"+
"<campo numero=\"418\">"+Addons.truncar(_418)+"</campo>"+
"<campo numero=\"419\">"+Addons.truncar(_419)+"</campo>"+
"<campo numero=\"421\">"+Addons.truncar(_421)+"</campo>"+
"<campo numero=\"422\">"+Addons.truncar(_422)+"</campo>"+
"<campo numero=\"429\">"+Addons.truncar(_429)+"</campo>"+
"<campo numero=\"431\">"+Addons.truncar(_431)+"</campo>"+
//"<campo numero=\"432\">"+Addons.truncar(_432)+"</campo>"+
//"<campo numero=\"433\">"+Addons.truncar(_433)+"</campo>"+
"<campo numero=\"434\">"+Addons.truncar(_434)+"</campo>"+
"<campo numero=\"441\">"+Addons.truncar(_441)+"</campo>"+
"<campo numero=\"442\">"+Addons.truncar(_442)+"</campo>"+                    
"<campo numero=\"443\">"+Addons.truncar(_443)+"</campo>"+
"<campo numero=\"444\">"+Addons.truncar(_444)+"</campo>"+
"<campo numero=\"453\">"+Addons.truncar(_453)+"</campo>"+
"<campo numero=\"454\">"+Addons.truncar(_454)+"</campo>"+                    
"<campo numero=\"480\">"+Addons.truncar(_480)+"</campo>"+
"<campo numero=\"481\">"+Addons.truncar(_481)+"</campo>"+
"<campo numero=\"482\">"+Addons.truncar(_429)+"</campo>"+
"<campo numero=\"483\">0.00</campo>"+
"<campo numero=\"484\">"+Addons.truncar(_480)+"</campo>"+
"<campo numero=\"485\">"+Addons.truncar(_485)+"</campo>"+
"<campo numero=\"499\">"+Addons.truncar(_499)+"</campo>"+
"<campo numero=\"500\">"+Addons.truncar(_500)+"</campo>"+                   
"<campo numero=\"501\">"+Addons.truncar(_501)+"</campo>"+
"<campo numero=\"502\">"+Addons.truncar(_502)+"</campo>"+
"<campo numero=\"503\">"+Addons.truncar(_503)+"</campo>"+
"<campo numero=\"504\">"+Addons.truncar(_504)+"</campo>"+
"<campo numero=\"505\">"+Addons.truncar(_505)+"</campo>"+
"<campo numero=\"506\">"+Addons.truncar(_506)+"</campo>"+
"<campo numero=\"507\">"+Addons.truncar(_507)+"</campo>"+
"<campo numero=\"508\">"+Addons.truncar(_508)+"</campo>"+                    
"<campo numero=\"509\">"+Addons.truncar(_509)+"</campo>"+
"<campo numero=\"510\">"+Addons.truncar(_510)+"</campo>"+                     
"<campo numero=\"511\">"+Addons.truncar(_511)+"</campo>"+
"<campo numero=\"512\">"+Addons.truncar(_512)+"</campo>"+
"<campo numero=\"513\">"+Addons.truncar(_513)+"</campo>"+
"<campo numero=\"514\">"+Addons.truncar(_514)+"</campo>"+
"<campo numero=\"515\">"+Addons.truncar(_515)+"</campo>"+
"<campo numero=\"516\">"+Addons.truncar(_516)+"</campo>"+
"<campo numero=\"517\">"+Addons.truncar(_517)+"</campo>"+
"<campo numero=\"518\">"+Addons.truncar(_518)+"</campo>"+
"<campo numero=\"519\">"+Addons.truncar(_519)+"</campo>"+
"<campo numero=\"520\">"+Addons.truncar(_520)+"</campo>"+                     
"<campo numero=\"521\">"+Addons.truncar(_521)+"</campo>"+
"<campo numero=\"522\">"+Addons.truncar(_522)+"</campo>"+
"<campo numero=\"523\">"+Addons.truncar(_523)+"</campo>"+
"<campo numero=\"522\">"+Addons.truncar(_524)+"</campo>"+
"<campo numero=\"523\">"+Addons.truncar(_525)+"</campo>"+
"<campo numero=\"529\">"+Addons.truncar(_529)+"</campo>"+
"<campo numero=\"531\">"+Addons.truncar(_531)+"</campo>"+
"<campo numero=\"532\">"+Addons.truncar(_532)+"</campo>"+
//"<campo numero=\"533\">"+Addons.truncar(_533)+"</campo>"+
//"<campo numero=\"534\">"+Addons.truncar(_534)+"</campo>"+
"<campo numero=\"535\">"+Addons.truncar(_535)+"</campo>"+
"<campo numero=\"541\">"+Addons.truncar(_541)+"</campo>"+ 
"<campo numero=\"542\">"+Addons.truncar(_452)+"</campo>"+                     
"<campo numero=\"543\">"+Addons.truncar(_543)+"</campo>"+
"<campo numero=\"544\">"+Addons.truncar(_544)+"</campo>"+
"<campo numero=\"545\">"+Addons.truncar(_545)+"</campo>"+
//"<campo numero=\"553\">"+Addons.truncar(_553, 4)+"</campo>"+
"<campo numero=\"554\">"+Addons.truncar(_554)+"</campo>"+
"<campo numero=\"555\">"+Addons.truncar(_555)+"</campo>"+  
"<campo numero=\"563\">"+Addons.truncar(_563)+"</campo>"+ 
"<campo numero=\"564\">"+Addons.truncar(_564)+"</campo>"+                     
"<campo numero=\"601\">"+Addons.truncar(_601)+"</campo>"+
"<campo numero=\"602\">"+Addons.truncar(_602)+"</campo>"+
"<campo numero=\"605\">"+Addons.truncar(_605)+"</campo>"+
"<campo numero=\"607\">"+Addons.truncar(_607)+"</campo>"+
"<campo numero=\"609\">"+Addons.truncar(_609)+"</campo>"+
"<campo numero=\"611\">"+Addons.truncar(_611)+"</campo>"+
"<campo numero=\"612\">"+Addons.truncar(_612)+"</campo>"+                     
"<campo numero=\"613\">"+Addons.truncar(_613)+"</campo>"+
"<campo numero=\"615\">"+Addons.truncar(_615)+"</campo>"+
"<campo numero=\"617\">"+Addons.truncar(_617)+"</campo>"+
"<campo numero=\"619\">"+Addons.truncar(_619)+"</campo>"+
"<campo numero=\"621\">"+Addons.truncar(_621)+"</campo>"+
"<campo numero=\"699\">"+Addons.truncar(_699)+"</campo>"+
"<campo numero=\"721\">"+Addons.truncar(_721)+"</campo>"+
"<campo numero=\"723\">"+Addons.truncar(_723)+"</campo>"+
"<campo numero=\"725\">"+Addons.truncar(_725)+"</campo>"+
"<campo numero=\"799\">"+Addons.truncar(_799)+"</campo>"+
"<campo numero=\"859\">"+Addons.truncar(_859)+"</campo>"+
"<campo numero=\"880\">"+Addons.truncar(_880)+"</campo>"+
"<campo numero=\"890\">"+Addons.truncar(_890)+"</campo>"+
"<campo numero=\"897\">"+Addons.truncar(_897)+"</campo>"+
"<campo numero=\"898\">"+Addons.truncar(_898)+"</campo>"+
"<campo numero=\"899\">"+Addons.truncar(_899)+"</campo>"+
"<campo numero=\"902\">"+Addons.truncar(_902)+"</campo>"+
"<campo numero=\"903\">"+Addons.truncar(_903)+"</campo>"+
"<campo numero=\"904\">"+Addons.truncar(_904)+"</campo>"+
"<campo numero=\"905\">"+Addons.truncar(_699)+"</campo>"+
"<campo numero=\"906\">"+Addons.truncar(_906)+"</campo>"+
"<campo numero=\"907\">"+Addons.truncar(_907)+"</campo>"+
"<campo numero=\"908\"></campo>"+
"<campo numero=\"909\">"+Addons.truncar(_909)+"</campo>"+
"<campo numero=\"910\"></campo>"+
"<campo numero=\"911\">"+Addons.truncar(_911)+"</campo>"+
"<campo numero=\"912\"></campo>"+
"<campo numero=\"913\">"+Addons.truncar(_913)+"</campo>"+
//"<campo numero=\"914\"></campo>"+
"<campo numero=\"915\">"+Addons.truncar(_915)+"</campo>"+
"<campo numero=\"916\"></campo>"+
"<campo numero=\"917\">"+Addons.truncar(_917)+"</campo>"+
"<campo numero=\"918\"></campo>"+
"<campo numero=\"919\">"+Addons.truncar(_919)+"</campo>"+
"<campo numero=\"922\">89</campo>"+
"<campo numero=\"999\">"+Addons.truncar(_699)+"</campo>"+
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
