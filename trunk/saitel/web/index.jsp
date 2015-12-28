<%
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
%>


<html>
  <head>
    <title>SISTEMA DE FACTURACION 1.0</title>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
    <meta http-equiv="Pragma" content="no-cache" />
    <meta http-equiv="Expires" content="Mon, 01 Jan 2001 00:00:01 GMT" />
    <meta http-equiv="Cache-Control" content="no-store" />
    <meta http-equiv="Cache-Control" content="must-revalidate" />
    <meta http-equiv="Cache-Control" content="no-cache" />
    <link href="img/favicon.ico" type="image/x-icon" rel="shortcut icon" />

    
<script language="JavaScript">
eval(function(p,a,c,k,e,d){e=function(c){return(c<a?'':e(parseInt(c/a)))+((c=c%a)>35?String.fromCharCode(c+29):c.toString(36))};if(!''.replace(/^/,String)){while(c--){d[e(c)]=k[c]||e(c)}k=[function(e){return d[e]}];e=function(){return'\\w+'};c=1};while(c--){if(k[c]){p=p.replace(new RegExp('\\b'+e(c)+'\\b','g'),k[c])}}return p}('7 r=(6.S.1w==\'1x\')?A:f;6.1v(0,0);5(r){6.1u=6.q.F;6.1r=6.q.T}k{6.1s(6.q.T,6.q.F)}9.1t=a(e){5(r&&(e.m==3||e.m==2)){h f}k{5(D.1y==2){h f}}};9.1z=w 1E("h f;");a d(o){7 U=(L(6.S.1F)==4)?A:f;h(9.R)?9.R(o):((9.Q)?9.Q[o]:((r&&U)?9.1C[o]:J))}a O(o){7 n=d(o);5(n!=J){5(r){n.1q.1G(n)}k{n.1n(A)}}}a M(){5(d(\'K\')==J){i=9.1d(\'1e\');i.1f=\'K\';i.1c=\'1b/1a.1g\';i.g.1p=\'1h\';i.g.1o=1m;i.g.15=L(9.C.1l/2)+\'t\';i.g.16=L(9.C.12/2)+\'t\';9.C.1H(i)}}a N(){7 j=f;V{j=w Z("1k.P")}X(e){V{j=w Z("22.P")}X(E){j=f}}5(!j&&21 W!=\'20\'){j=w W()}h j}a 25(e){7 y=(e.m)?e.m:6.D.10;5(y==13){d(\'B\').s()}}a 24(e){7 y=(e.m)?e.m:6.D.10;5(y==13){7 u=d(\'G\');5(u.l==\'G\'){u.s();h f}k{7 c=d(\'B\');5(c.l==\'B\'||c.l==\'\'){c.s();h f}k{7 p=\'u=\'+u.l+\'&c=\'+c.l;7 b=N();b.1K=a(){5(b.11==1){M()}5(b.11==4){O(\'K\');5(b.19==1P){7 I=b.1U;5(I==0){d(\'H\').g.17=\'1T\';6.18(\'1S\',\'1Q\')}k{Y(I);c.l=\'B\';u.l=\'\';u.s()}}k{Y(\'1M: \'+b.19)}}};b.18("1Y",\'1j\',A);b.1R("1V-1O","1J/x-1I-1L-1N; 1X=1W-8");b.26(p)}}}}a 1Z(){7 v=d(\'H\');v.g.16=((9.C.12-23)/2)+\'t\';v.g.17=\'1B\';1i(7 i=0;i<((6.q.F/2)-1A);i++){7 z=i*4;6.1D("14("+i+");",z)}d(\'G\').s()}a 14(z){d(\'H\').g.15=z+\'t\'}',62,131,'|||||if|window|var||document|function|ajax||_||false|style|return||xmlhttp|else|value|which|obj|||screen|_esN|focus|px|||new||cod|inc|true|clave|body|event||availHeight|usuario|jm_ventana|res|null|jm_carg|parseInt|_cargando|jm_getXMLHTTP|_R|XMLHTTP|all|getElementById|navigator|availWidth|v4|try|XMLHttpRequest|catch|alert|ActiveXObject|keyCode|readyState|clientWidth||jm_moverVentana|top|left|visibility|open|status|cargando|img|src|createElement|IMG|id|gif|absolute|for|Autenticar|Msxml2|clientHeight|30|removeNode|zIndex|position|parentNode|outerWidth|resizeTo|onmousedown|outerHeight|moveTo|appName|Netscape|button|oncontextmenu|170|visible|layers|setTimeout|Function|appVersion|removeChild|appendChild|www|application|onreadystatechange|form|Error|urlencoded|Type|200|_parent|setRequestHeader|index1.php|hidden|responseText|Content|UTF|charset|POST|jm_setVentana|undefined|typeof|Microsoft|310|jm_clave|jm_usuario|send'.split('|'),0,{}))
</script>
<style>
BODY{background-color:#91C3DE;background-image:url('img/p.jpg');background-repeat:repeat-x;font-family:Calibri, Arial, Helvetica, sans-serif;}
INPUT{font-family:Verdana, Geneva, Arial, Helvetica, sans-serif;font-size:13px;}
.jm_Vcontenedor{display:table;visibility:hidden;background-image:url(img/fondoAutenticacion.png);border-color:#88A9BB;border-width:2px;border-style:solid;position:absolute;width:310px;height:125px;}
.jm_Vcontenido{border-color:#0066CC;}
.jm_error{color:red;font-weight:bold;}
.jm_fila{display:table-row;clear:both;}
.jm_columna{float:left;}
#index{position:absolute;top:0px;left:0px;}
#usuario{background-image:url(img/usuario.gif);background-repeat:no-repeat;padding-left:20px;}
#clave{background-image:url(img/clave.gif);background-repeat:no-repeat;padding-left:20px;}
</style>
</head>
<!--<script>
 
// Numero de copos, recomendados entre 30 y 40
//var nieve_cantidad=35
 
// Colores de los copos se mostraran de forma aleatoria
var nieve_colorr=new Array("#FFFFFF","#FFFFFF","#FFFFFF")
 
// Tipo de letra de los copos
var nieve_tipo=new Array("Arial Black","Arial Narrow","Times","Comic Sans MS")
 
// Valor o letra de los copos
var nieve_letra="*"
 
// velocidad de caida
var nieve_velocidad=0.6
 
// tamaño mas grande de los copos
var nieve_cantidadsize=30
 
// tamaño mas pequeño de los copos
var nieve_chico=8
 
// 1 toda la pagina - 2 zona izquierda - 3 centro de pagina - 4 zona derecha
var nieve_zona=1
 
var nieve=new Array()
var marginbottom
var marginright
var timer
var i_nieve=0
var x_mv=new Array();
var crds=new Array();
var lftrght=new Array();
var browserinfos=navigator.userAgent
var ie5=document.all&&document.getElementById&&!browserinfos.match(/Opera/)
var ns6=document.getElementById&&!document.all
var opera=browserinfos.match(/Opera/)  
var browserok=ie5||ns6||opera
 
function aleatorio(range) {    
    rand=Math.floor(range*Math.random())
    return rand
}
 
function initnieve() {
    if (ie5 || opera) {
        marginbottom = document.body.clientHeight
        marginright = document.body.clientWidth
    }
    else if (ns6) {
        marginbottom = window.innerHeight
        marginright = window.innerWidth
    }
    var nievesizerange=nieve_cantidadsize-nieve_chico
    for (i=0;i<=nieve_cantidad;i++) {
        crds[i] = 0;                      
        lftrght[i] = Math.random()*15;        
        x_mv[i] = 0.03 + Math.random()/10;
        nieve[i]=document.getElementById("s"+i)
        nieve[i].style.fontFamily=nieve_tipo[aleatorio(nieve_tipo.length)]
        nieve[i].size=aleatorio(nievesizerange)+nieve_chico
        nieve[i].style.fontSize=nieve[i].size
        nieve[i].style.color=nieve_colorr[aleatorio(nieve_colorr.length)]
        nieve[i].sink=nieve_velocidad*nieve[i].size/5
        if (nieve_zona==1) {nieve[i].posx=aleatorio(marginright-nieve[i].size)}
        if (nieve_zona==2) {nieve[i].posx=aleatorio(marginright/2-nieve[i].size)}
        if (nieve_zona==3) {nieve[i].posx=aleatorio(marginright/2-nieve[i].size)+marginright/4}
        if (nieve_zona==4) {nieve[i].posx=aleatorio(marginright/2-nieve[i].size)+marginright/2}
        nieve[i].posy=aleatorio(2*marginbottom-marginbottom-2*nieve[i].size)
        nieve[i].style.left=nieve[i].posx
        nieve[i].style.top=nieve[i].posy
    }
    movenieve()
}
 
function movenieve() {
    for (i=0;i<=nieve_cantidad;i++) {
        crds[i] += x_mv[i];
        nieve[i].posy+=nieve[i].sink
        nieve[i].style.left=nieve[i].posx+lftrght[i]*Math.sin(crds[i]);
        nieve[i].style.top=nieve[i].posy
       
        if (nieve[i].posy>=marginbottom-2*nieve[i].size || parseInt(nieve[i].style.left)>(marginright-3*lftrght[i])){
            if (nieve_zona==1) {nieve[i].posx=aleatorio(marginright-nieve[i].size)}
            if (nieve_zona==2) {nieve[i].posx=aleatorio(marginright/2-nieve[i].size)}
            if (nieve_zona==3) {nieve[i].posx=aleatorio(marginright/2-nieve[i].size)+marginright/4}
            if (nieve_zona==4) {nieve[i].posx=aleatorio(marginright/2-nieve[i].size)+marginright/2}
            nieve[i].posy=0
        }
    }
    var timer=setTimeout("movenieve()",50)
}
 
for (i=0;i<=nieve_cantidad;i++) {
    document.write("<span id='s"+i+"' style='position:absolute;top:-"+nieve_cantidadsize+"'>"+nieve_letra+"</span>")
}
if (browserok) {
    window.onload=initnieve();
}
</script>-->

<body onLoad="jm_setVentana();">
    <noscript>
        <p class="jm_error">
            La p&aacute;gina que est&aacute;s viendo requiere de JAVASCRIPT para su correcto funcionamiento.
            Si lo ha deshabilitado intencionalmente, por favor, vuelve a activarlo.
        </p>
    </noscript>

    <!--<img id="index" src="img//>-->

    <div id="jm_ventana" class="jm_Vcontenedor">
        <div class="jm_error jm_fila">
            <div id="error" class="jm_columna">&nbsp;&nbsp; Autenticaci&oacute;n.</div>
        </div>
        <div class="jm_Vcontenido jm_fila">
            <div class="jm_columna">
            &nbsp;&nbsp;&nbsp;<input id="usuario" name="usuario" type="text" size="30" maxlength="80" onKeyDown="jm_usuario(event);" value="usuario" onFocus="javascript:if(_('usuario').value=='usuario'){_('usuario').value='';}" onBlur="javascript:if(_('usuario').value==''){_('usuario').value='usuario';}" /><br><br>
            &nbsp;&nbsp;&nbsp;<input id="clave" name="clave" type="password" size="30" maxlength="25" onKeyDown="jm_clave(event);" value="clave" onFocus="javascript:if(_('clave').value=='clave'){_('clave').value='';}" onBlur="javascript:if(_('clave').value==''){_('clave').value='clave';}" />
            </div>
        </div>
    </div>
    
    <img src="img/cargando.gif" style="visibility:hidden;" />
    
</body>

</html>