var _esN = (window.navigator.appName=='Netscape') ? true : false;
var _esIE = (window.navigator.appName=='Microsoft Internet Explorer') ? true : false;
var _esSf=window.navigator.appVersion.toLowerCase().indexOf('safari')>0?true:false;
if(_esN){
    window.moveTo(5,5);
    window.outerHeight = window.screen.availHeight-10;
    window.outerWidth = 750;
}else{
    window.resizeTo(685, 410);
}
window.onload=function(){
    if(documento=='f'){
        _('frt_fac_razon_social').style.left=v_senores[0];
        _('frt_fac_razon_social').style.top=v_senores[1];

        _('frt_fac_ruc').style.left=v_ruc[0];
        _('frt_fac_ruc').style.top=v_ruc[1];

        _('frt_fac_fecha').style.left=v_fech_emi[0];
        _('frt_fac_fecha').style.top=v_fech_emi[1];

        _('frt_fac_telefono').style.left=v_telefono[0];
        _('frt_fac_telefono').style.top=v_telefono[1];

        _('frt_fac_direccion').style.left=v_direccion[0];
        _('frt_fac_direccion').style.top=v_direccion[1];

        _('frt_fac_detalle').style.left=v_detalle[0];
        _('frt_fac_detalle').style.top=v_detalle[1];

        _('frt_fac_subtotal').style.left=v_subtotal[0];
        _('frt_fac_subtotal').style.top=v_subtotal[1];

        _('frt_fac_descuento').style.left=v_descuento[0];
        _('frt_fac_descuento').style.top=v_descuento[1];

        _('frt_fac_subtotal_2').style.left=v_subtotal_2[0];
        _('frt_fac_subtotal_2').style.top=v_subtotal_2[1];
        
        _('frt_fac_subtotal_0').style.left=v_subtotal_0[0];
        _('frt_fac_subtotal_0').style.top=v_subtotal_0[1];

        _('frt_fac_iva_2').style.left=v_iva_2[0];
        _('frt_fac_iva_2').style.top=v_iva_2[1];

        _('frt_fac_total').style.left=v_total[0];
        _('frt_fac_total').style.top=v_total[1];
    }
    if(documento=='r'){
        _('frt_ret_razon_social').style.left=v_senores[0];
        _('frt_ret_razon_social').style.top=v_senores[1];

        _('frt_ret_ruc').style.left=v_ruc[0];
        _('frt_ret_ruc').style.top=v_ruc[1];

        _('frt_ret_fecha').style.left=v_fech_emi[0];
        _('frt_ret_fecha').style.top=v_fech_emi[1];

        _('frt_ret_num_comp').style.left=v_num_comp[0];
        _('frt_ret_num_comp').style.top=v_num_comp[1];

        _('frt_ret_direccion').style.left=v_direccion[0];
        _('frt_ret_direccion').style.top=v_direccion[1];

        _('frt_ret_tipo').style.left=v_tipo[0];
        _('frt_ret_tipo').style.top=v_tipo[1];

        _('frt_ret_detalle').style.left=v_detalle[0];
        _('frt_ret_detalle').style.top=v_detalle[1];

        _('frt_ret_total').style.left=v_total[0];
        _('frt_ret_total').style.top=v_total[1];
    }
    if(documento=='ch'){
        _('chq_orden_de').style.left=v_orden_de[0];
        _('chq_orden_de').style.top=v_orden_de[1];

        _('chq_total').style.left=v_total[0];
        _('chq_total').style.top=v_total[1];

        _('chq_son').style.left=v_son[0];
        _('chq_son').style.top=v_son[1];

        _('chq_lugar_fecha').style.left=v_lugar_fecha[0];
        _('chq_lugar_fecha').style.top=v_lugar_fecha[1];
    }
    if(documento=='l'){
        _('frt_liq_razon_social').style.left=v_senores[0];
        _('frt_liq_razon_social').style.top=v_senores[1];

        _('frt_liq_ci').style.left=v_ci[0];
        _('frt_liq_ci').style.top=v_ci[1];

        _('frt_liq_fecha').style.left=v_fech_emi[0];
        _('frt_liq_fecha').style.top=v_fech_emi[1];

        _('frt_liq_direccion').style.left=v_direccion[0];
        _('frt_liq_direccion').style.top=v_direccion[1];

        _('frt_liq_detalle').style.left=v_detalle[0];
        _('frt_liq_detalle').style.top=v_detalle[1];

        _('frt_liq_subtotal').style.left=v_subtotal[0];
        _('frt_liq_subtotal').style.top=v_subtotal[1];

        _('frt_liq_iva_0').style.left=v_iva_0[0];
        _('frt_liq_iva_0').style.top=v_iva_0[1];

        _('frt_liq_subtotal_2').style.left=v_subtotal_2[0];
        _('frt_liq_subtotal_2').style.top=v_subtotal_2[1];
        
        _('frt_liq_iva_12').style.left=v_iva_12[0];
        _('frt_liq_iva_12').style.top=v_iva_12[1];

        _('frt_liq_total').style.left=v_total[0];
        _('frt_liq_total').style.top=v_total[1];
    }
    if(documento=='c'){ // nota de credito
        _('frt_not_cre_razon_social').style.left=v_senores[0];
        _('frt_not_cre_razon_social').style.top=v_senores[1];

        _('frt_not_cre_ruc').style.left=v_ci[0];
        _('frt_not_cre_ruc').style.top=v_ci[1];

        _('frt_not_cre_fecha').style.left=v_fech_emi[0];
        _('frt_not_cre_fecha').style.top=v_fech_emi[1];

        _('frt_not_cre_modifica').style.left=v_modifica[0];
        _('frt_not_cre_modifica').style.top=v_modifica[1];

        _('frt_not_cre_concepto').style.left=v_concepto[0];
        _('frt_not_cre_concepto').style.top=v_concepto[1];

        _('frt_not_cre_valor').style.left=v_valor[0];
        _('frt_not_cre_valor').style.top=v_valor[1];

        _('frt_not_cre_iva_0').style.left=v_iva_0[0];
        _('frt_not_cre_iva_0').style.top=v_iva_0[1];

        _('frt_not_cre_iva_12').style.left=v_iva_12[0];
        _('frt_not_cre_iva_12').style.top=v_iva_12[1];

        _('frt_not_cre_total').style.left=v_total[0];
        _('frt_not_cre_total').style.top=v_total[1];
    }
    if(documento=='d'){ // nota de debito
        _('frt_not_deb_razon_social').style.left=v_senores[0];
        _('frt_not_deb_razon_social').style.top=v_senores[1];

        _('frt_not_deb_ruc').style.left=v_ci[0];
        _('frt_not_deb_ruc').style.top=v_ci[1];

        _('frt_not_deb_fecha').style.left=v_fech_emi[0];
        _('frt_not_deb_fecha').style.top=v_fech_emi[1];

        _('frt_not_deb_modifica').style.left=v_modifica[0];
        _('frt_not_deb_modifica').style.top=v_modifica[1];

        _('frt_not_deb_concepto').style.left=v_concepto[0];
        _('frt_not_deb_concepto').style.top=v_concepto[1];

        _('frt_not_deb_valor').style.left=v_valor[0];
        _('frt_not_deb_valor').style.top=v_valor[1];

        _('frt_not_deb_iva_0').style.left=v_iva_0[0];
        _('frt_not_deb_iva_0').style.top=v_iva_0[1];

        _('frt_not_deb_iva_12').style.left=v_iva_12[0];
        _('frt_not_deb_iva_12').style.top=v_iva_12[1];

        _('frt_not_deb_total').style.left=v_total[0];
        _('frt_not_deb_total').style.top=v_total[1];
    }
};
document.onmousedown=function(e){
    if(_esN && (e.which==3 || e.which==2)){
        return false;
    } 
    else if(_esIE){
        if(event.button==2){
            return false;
        }
    }
    return true;
};
document.oncontextmenu = new Function("return false;");
function _(o)
{	
    var v = (parseInt(window.navigator.appVersion)==4) ? true : false;
    return (document.getElementById) ? document.getElementById(o) : ((document.all) ? document.all[o] :((_esN && v) ? document.layers[o] : null));
}
function _getTop(ob) 
{
    var o = _(ob);
    if(o == null){
        return 0;
    }else{		
        var obj = o.offsetParent;
        if(o.style.top){
            return o.style.top + _getTop(obj);
        }
        if(o.style.pixelTop){
            return o.style.pixelTop + _getTop(obj);
        }
        if(o.offsetTop){
            return o.offsetTop + _getTop(obj);
        }
        return (null);
    }
}
function _getIzq(ob)
{
    var o = _(ob);
    if(o == null){
        return 0;
    }else{
        var obj = o.offsetParent;
        if(o.style.left){
            return o.style.left + _getIzq(obj);
        }
        if(o.style.pixelLeft){
            return o.style.pixelLeft + _getIzq(obj);
        }
        if(o.offsetLeft){
            return o.offsetLeft + _getIzq(obj);
        }
        return (null);
    }
}
function _T(o, v)
{
    if(o!=null){
        o.style.opacity = v/100;
        if(o != null){
            if(_esN){
                o.style.MozOpacity = v/100;
            }else{
                o.style.filter = "alpha(opacity=" + v + ")";
            }
        }
    }
}
var _obj=null;
var _rataX=null;
var _rataY=null;
var _posX=0;
var _posY=0;
var _wOb=0;
function iniMover(ev, ob)
{
    _obj = ob;
    var e = (_esN) ? ev : window.event;
    _rataY = e.screenY-parseInt(_getTop(_obj));		
    _rataX = e.screenX-parseInt(_getIzq(_obj));
    _T(_(ob), 70);
    crearEvento(mover);
    document.onmouseup=finMover;
}
function mover(ev)
{		
    var e = (_esN) ? ev : window.event;
    _posX = e.screenX - _rataX;
    _posY = e.screenY - _rataY;
    _(_obj).style.top = _posY+'px';
    _(_obj).style.left = _posX+'px';
}
function finMover()
{	
    _T(_(_obj), 100);
    quitarEvento(mover);
    setParametro();
}
function getXMLHTTP(){
    if(typeof window.XMLHttpRequest!='undefined'){
            return new XMLHttpRequest();
        }else{
            try{
                return new ActiveXObject('Microsoft.XMLHTTP');
            }catch(e){
                try{
                    return new ActiveXObject('Msxml2.XMLHTTP');
                }catch(e){
                    return false;
                }
            }
        }
}
function solicitud(p, pr)
{
    var ajax=getXMLHTTP();
    ajax.onreadystatechange=function(){
        if(ajax.readyState==4){
            if(ajax.status==200){
                var res=ajax.responseText;
                if(res!=0){
                    quitarEvento(redimensionar);
                    quitarEvento(mover);
                    alert(res);
                }
            }else{
                alert('Error: '+ajax.status);
            }
        }
    };
    ajax.open("POST", p, true);
    ajax.setRequestHeader("Content-Type","application/x-www-form-urlencoded; charset=UTF-8");
    ajax.send(pr);
}
function setParametro(){
    solicitud('setFormatoPosicion', 'id='+id+'&doc='+documento+'&c='+_obj+'&x='+_posX+'&y='+_posY);
}
function iniRedimensionar(o)
{
    var izq = null;
    var an = null;
    if(documento=='l'){
        izq = _getIzq('frt_liq_detalle');
        an = parseInt(_('frt_liq_detalle').style.width);
    }else if(documento=='f'){
        izq = _getIzq('frt_fac_detalle');
        an = parseInt(_('frt_fac_detalle').style.width);
    }else if(documento=='c'){
        izq = _getIzq('frt_not_cre_concepto');
        an = parseInt(_('frt_not_cre_concepto').style.width);
    }else if(documento=='d'){
        izq = _getIzq('frt_not_deb_concepto');
        an = parseInt(_('frt_not_deb_concepto').style.width);
    }

    _obj=o;
    _rataX = parseInt(_getIzq(o)) + parseInt(izq) + 14;
    _wOb = an - parseInt(_(o).style.width);
    crearEvento(redimensionar);
    document.onmouseup=finRedimensionar;
}
function redimensionar(ev)
{		
    var e = (_esN) ? ev : window.event;
    var det = null; 
    if(documento=='l'){
        det = _('frt_liq_detalle');
    }else if(documento=='f'){
        det = _('frt_fac_detalle');
    }else if(documento=='c'){
        det = _('frt_not_cre_concepto');
    }else if(documento=='d'){
        det = _('frt_not_deb_concepto');
    }
    _posX = e.screenX - _rataX;
    _(_obj).style.width = _posX+'px';
    det.style.width = (_wOb + _posX) + 'px';
}
function finRedimensionar()
{
    quitarEvento(redimensionar);
    solicitud('setFormatoAncho', 'id='+id+'&doc='+documento+'&c='+_obj+'&a='+_posX);
}
function crearEvento(e)
{
    if(_esN){
        document.body.addEventListener("mousemove", e, false);
    }else{
        document.body.attachEvent("onmousemove", e);
    }
}
function quitarEvento(e)
{
    if(_esN){
        document.body.removeEventListener("mousemove", e, false);
    }else{
        document.body.detachEvent("onmousemove", e);
    }
}