var _esSf=window.navigator.appVersion.toLowerCase().indexOf('safari')>0?true:false;
var _esWin = navigator.appVersion.toLowerCase().indexOf("windows")>=0 ? true : false;
var _altBody = 0;
var _altEdi = 0;
var _anchBody = 0;
var _centroX = 0;
var _centroY = 0;
var _OBJ = null;
var _pX=0;
var _pY=0;
var _wOb=0;
var _AUX='';
var _AX_DET='';
var _SBM=false;
var btn = new Boton();
var _AJAX = new Ajax();
var axPg=0;
var axNpg = 0;

window.onload=function(){
    _anchBody = document.body.clientWidth;
    _altBody = _getTop(_('pie'));
    _altEdi = _altBody - 230;
    _centroX = _anchBody/2;
    _centroY = _altBody/2;

    _('d_titulo').innerHTML = _TIT_;
    _('d_usuario').innerHTML = 'USUARIO : ' + _USR_;
    _('celda01').style.width=(_anchBody-170)+'px';
    _B('d_filtro', 4);
    _('d_menu').style.height=_('gr_menu').style.height=(_altBody-115)+'px';
    _('celda12').style.width=(_anchBody-178)+'px';
    _B('d_menu', 4);
    _B('celda12', 4);
    _('d_cuerpo').style.width=(_anchBody-195)+'px';
    _('d_cuerpo').style.height=(parseInt(_('d_menu').style.height)-65)+'px';
    _('d_fila').style.width=(_anchBody-200)+'px';
    _('d_1').style.height=(parseInt(_('d_menu').style.height)-83) + 'px';
    _('d_1').style.width=_('d_11').style.width=_('d_2').style.width=_('d_21').style.width=parseInt(((_anchBody-260)/2))+'px';
    _('celda41').style.width=((_anchBody-195)/2)+'px';
    _B('barra0', 4);
    _S('barra0');
    _('celda11').onclick=function(){
        var d=_('gr_menu').style.display || 'table-cell';
        if(d=='table-cell'){
           _('celda11').innerHTML='►';
           _('gr_menu').style.display = 'none';
           _('d_cuerpo').style.width=(_anchBody-26)+'px';
        }else{
            _('celda11').innerHTML='◄';
            _('gr_menu').style.display = 'table-cell';
            _('d_cuerpo').style.width=(_anchBody-195)+'px';
        }
    };
    _('d_1').onmouseover = function(){_('d_cuerpo').scrollLeft = 0;}
    _('d_2').onmouseover = function(){_('d_cuerpo').scrollLeft = parseInt(_('d_11').style.width);}
    _('gr_ayuda').style.left=_X_+'px';
    _('gr_ayuda').style.top=_Y_+'px';
    _('des_ayuda').innerHTML = _DA_ ? ' ▲' : ' ▼ ';
    _('gr_despliegue').style.display = _DA_ ? 'block' : 'none';
    _B('gr_ayuda', 4);
    _S('gr_ayuda');

    btn.icono(-167, 3, 34, 34, 'Cambiar mi Contrase&ntilde;a', "cambiarClave();", 'barra0');
    btn.separador('barra0');
    if(_MENUS_._enArreglo("respaldar")>=0){
        btn.icono(-199, 2, 34, 34, 'Ejecutar resguardo de la base de datos', "_AJAX.solicitud('Resguardo', '', true, 'body');", 'barra0');
    }
    btn.icono(-231, 3, 34, 34, 'Papelera de reciclaje', "papelera();", 'barra0');
    btn.icono(-263, 2, 34, 34, 'Ayuda', "window.open('manualUsuario.pdf', '_blank', 'top=50,left=50,width=750,height=500');", 'barra0');
    btn.icono(-296, 2, 34, 34, 'Acerca de...', "creditos();", 'barra0');
    btn.icono(-327, 2, 34, 34, 'Cerrar sesi&oacute;n', "window.open('Salir','_self');", 'barra0');

    if(_MENUS_._enArreglo("auditoria")>=0 || _MENUS_._enArreglo("roles")>=0 || _MENUS_._enArreglo("usuarios")>=0){
        btn.modulo(-165,-35, 28, 23, 'SEGURIDAD', 'd_menu', 1);
        _('d_menu').innerHTML += "<div id=\"d_menu1\" style=\"display:none\"></div>";
        if(_MENUS_._enArreglo("auditoria")>=0){
            btn.menu('Saud', -188, -32, 30, 25, 'Auditor&iacute;a', "seg_auditoria();", 'd_menu1');
        }
        if(_MENUS_._enArreglo("roles")>=0){
            btn.menu('Srol', -215, -33, 28, 25, 'Roles', "seg_roles();", 'd_menu1');
        }
        if(_MENUS_._enArreglo("usuarios")>=0){
            btn.menu('Susr', -242, -34, 28, 25, 'Usuarios', "seg_usuarios();", 'd_menu1');
        }
    }

    if(_MENUS_._enArreglo("configuracion")>=0 || _MENUS_._enArreglo("impuestos")>=0 || _MENUS_._enArreglo("precios")>=0 || 
        _MENUS_._enArreglo("sucursales")>=0 || _MENUS_._enArreglo("depreciaciones")>=0 || _MENUS_._enArreglo("ivas")>=0 || 
        _MENUS_._enArreglo("bodegas")>=0 || _MENUS_._enArreglo("ubicaciones")>=0){
        btn.modulo(-165,-35, 28, 23, 'ADMINISTRACI&Oacute;N', 'd_menu', 2);
        _('d_menu').innerHTML += "<div id=\"d_menu2\" style=\"display:none\"></div>";
        if(_MENUS_._enArreglo("configuracion")>=0){
            btn.menu('Acon', -270, -33, 28, 25, 'Configuraci&oacute;n', "adm_configuracion();", 'd_menu2');
        }
        if(_MENUS_._enArreglo("sucursales")>=0){
            btn.menu('Asuc', -329, -33, 28, 25, 'Sucursales', "adm_sucursales();", 'd_menu2');
        }
        if(_MENUS_._enArreglo("bodegas")>=0){
            btn.menu('Abod', -357, -164, 28, 25, 'Bodegas', "adm_bodegas();", 'd_menu2');
        }
        /*if(_MENUS_._enArreglo("ubicaciones")>=0){
            btn.menu('Apre', -357, -33, 28, 25, 'Ubicaciones (provincias - cantones - parroquias)', "adm_ubicaciones();", 'd_menu2');
        }*/
        if(_MENUS_._enArreglo("impuestos")>=0){
            btn.menu('AtIm', -300, -33, 28, 25, 'Tabla de impuestos (RENTA - IVA)', "adm_impuestos();", 'd_menu2');
        }
        if(_MENUS_._enArreglo("ivas")>=0){
            btn.menu('AIva', -538, -207, 28, 25, 'IVA', "adm_ivas();", 'd_menu2');
        }
        if(_MENUS_._enArreglo("depreciaciones")>=0){
            btn.menu('Adep', -427, -140, 28, 25, 'Tabla de depreciaciones', "adm_depreciaciones();", 'd_menu2');
        }
        if(_MENUS_._enArreglo("ACCESO A HORARIOS")>=0){
            btn.menu('Ahor', -357, -164, 28, 25, 'Horarios', "adm_horarios();", 'd_menu2');
        }
        
    }
    
    if(_MENUS_._enArreglo("areas")>=0 || _MENUS_._enArreglo("cargos")>=0 || _MENUS_._enArreglo("empleados")>=0 || 
        _MENUS_._enArreglo("permisos")>=0 || _MENUS_._enArreglo("asistencias")>=0){
        btn.modulo(-165,-35, 28, 23, 'NOMINA', 'd_menu', 3);
        _('d_menu').innerHTML += "<div id=\"d_menu3\" style=\"display:none\"></div>";
        if(_MENUS_._enArreglo("areas")>=0){
            btn.menu('Nare', -537, -237, 28, 25, 'Areas - departamentos', "nom_areas();", 'd_menu3');
        }
        if(_MENUS_._enArreglo("cargos")>=0){
            btn.menu('Ncar', -504, -237, 28, 25, 'Cargos', "nom_cargos();", 'd_menu3');
        }
        if(_MENUS_._enArreglo("empleados")>=0){
            btn.menu('Nemp', -451, -60, 28, 25, 'Empleados', "nom_empleados();", 'd_menu3');
        }
        /*if(_MENUS_._enArreglo("permisos")>=0){
            btn.menu('Nvac', -477, -208, 28, 25, 'Permisos', "nom_permisos();", 'd_menu3');
        }*/
        if(_MENUS_._enArreglo("asistencias")>=0){
            btn.menu('Nasi', -509, -207, 28, 25, 'Control Biometrico', "nom_asistencia();", 'd_menu3');
        }
    }
    
    if(_MENUS_._enArreglo("ACCESO A LICENCIAS ADMIN")>=0 || _MENUS_._enArreglo("ACCESO A LICENCIAS CLIENTE")>=0 || _MENUS_._enArreglo("ACCESO A PERMISOS")>=0 || 
        _MENUS_._enArreglo("ACCESO A TIPO PERMISOS")>=0 || _MENUS_._enArreglo("ACCESO A VACACIONES")>=0|| _MENUS_._enArreglo("ACCESO A VACACIONES CLIENTE")>=0|| 
        _MENUS_._enArreglo("ACCESO A TIPO PERMISOS")>=0 || _MENUS_._enArreglo("permisoNuevo")>=0){
        btn.modulo(-165,-35, 28, 23, 'PERM. Y LICENCIAS', 'd_menu', 9);
        _('d_menu').innerHTML += "<div id=\"d_menu9\" style=\"display:none\"></div>";
        if(_MENUS_._enArreglo("permisoNuevo")>=0){
            btn.menu('Pperm', -451, -60, 28, 25, 'Permisos', "pyl_permisos();", 'd_menu9');
        }
        if(_MENUS_._enArreglo("ACCESO A TIPO PERMISOS")>=0){
            btn.menu('Ptperm', -477, -208, 28, 25, 'Tipo de Permisos', "pyl_tipoPermisos();", 'd_menu9');
        } 
       if(_MENUS_._enArreglo("ACCESO ADMIN PERMISOS" )>=0){
            btn.menu('Padmperm', -509, -207, 28, 25, 'Adm. de Permisos', "pyl_admPermiso();", 'd_menu9');
        }
        /*if(_MENUS_._enArreglo("ACCESO A LICENCIAS ADMIN")>=0){
            btn.menu('PadmLic', -537, -237, 28, 25, 'Adm. de Licencias', "nom_areas();", 'd_menu9');
        }
        if(_MENUS_._enArreglo("ACCESO A LICENCIAS CLIENTE")>=0){
            btn.menu('Plic', -504, -237, 28, 25, 'Licencias', "nom_cargos();", 'd_menu9');
        }*/
        if(_MENUS_._enArreglo("ACCESO A VACACIONES" )>=0){
            btn.menu('Pdvac', -509, -207, 28, 25, 'Dias de Vacaciones', "pyl_diasVacaciones();", 'd_menu9');
        }
        if(_MENUS_._enArreglo("ACCESO A VACACIONES CLIENTE" )>=0){
            btn.menu('Pvac', -509, -207, 28, 25, 'Vacaciones', "pyl_vacaciones();", 'd_menu9');
        }
        if(_MENUS_._enArreglo("ADMINISTRACION DE VACACIONES" )>=0){
            btn.menu('Padmvac', -509, -207, 28, 25, 'Adm. Vacaciones', "pyl_admVacaciones();", 'd_menu9');
        }
    }

    if(_MENUS_._enArreglo("plan_cuentas")>=0 || _MENUS_._enArreglo("bancos")>=0 || _MENUS_._enArreglo("diario")>=0 ||
        _MENUS_._enArreglo("mayor")>=0 || _MENUS_._enArreglo("comprobantes")>=0 ||
        _MENUS_._enArreglo("bancos_pagos")>=0 || _MENUS_._enArreglo("anulados")>=0 || _MENUS_._enArreglo("bancos_cobros")>=0 ||
        _MENUS_._enArreglo("sri")>=0 || _MENUS_._enArreglo("clavesSRI")>=0 || _MENUS_._enArreglo("estados")>=0){
        btn.modulo(-165,-35, 28, 23, 'CONTABILIDAD', 'd_menu', 4);
        _('d_menu').innerHTML += "<div id=\"d_menu4\" style=\"display:none\"></div>";
        if(_MENUS_._enArreglo("plan_cuentas")>=0){
            btn.menu('CplC', -357, -33, 28, 25, 'Plan de cuentas', "cnt_plan_cuentas();", 'd_menu4');
        }
        if(_MENUS_._enArreglo("bancos")>=0){
            btn.menu('Cban', -28, -192, 28, 25, 'Cuentas Bancarias', "cnt_bancos();", 'd_menu4');
        }
        if(_MENUS_._enArreglo("diario")>=0){
            btn.menu('Cdro', -386, -33, 28, 25, 'libro diario', "cnt_diario();", 'd_menu4');
        }
        if(_MENUS_._enArreglo("mayor")>=0){
            btn.menu('Cmay', -415, -32, 28, 25, 'Libro mayor', "cnt_mayor();", 'd_menu4');
        }
        if(_MENUS_._enArreglo("comprobantes")>=0){
            btn.menu('Ccom', -441, -33, 28, 25, 'Comprobantes', "cnt_comprobantes();", 'd_menu4');
        }
        if(_MENUS_._enArreglo("caja_chica")>=0){
            btn.menu('Ccch', -86, -193, 28, 25, 'Caja Chica', "cnt_caja_chica();", 'd_menu4');
        }
        if(_MENUS_._enArreglo("bancos_pagos")>=0 || _MENUS_._enArreglo("bancos_cobros")>=0){
            btn.menu('CCnB', -306, -59, 28, 25, 'Conciliaci&oacute;n bancaria', "cnt_conciliacion();", 'd_menu4');
        }
        if(_MENUS_._enArreglo("anulados")>=0){
            btn.menu('CdcA', -144, -192, 28, 25, 'Documentos Anulados', "cnt_anulados();", 'd_menu4');
        }
        if(_MENUS_._enArreglo("clavesSRI")>=0){
            btn.menu('Cclv', -470, -33, 28, 25, 'Claves de contingencia del SRI', "cnt_claves();", 'd_menu4');
        }
        if(_MENUS_._enArreglo("autorizacionesSRI")>=0){
            btn.menu('Caut', -470, -33, 28, 25, 'Env&iacute;o y autorizaciones de documentos electr&oacute;nicos', "cnt_autorizaciones();", 'd_menu4');
        }
        if(_MENUS_._enArreglo("sri")>=0){
            btn.menu('Csri', -470, -33, 28, 25, 'SRI', "cnt_sri();", 'd_menu4');
        }
        if(_MENUS_._enArreglo("estados")>=0){
            btn.menu('Cest', -501, -33, 28, 25, 'Estados financieros', "cnt_estados();", 'd_menu4');
        }
    }


    if(_MENUS_._enArreglo("proveedores")>=0 || _MENUS_._enArreglo("productos")>=0 || _MENUS_._enArreglo("kardex")>=0 
        || _MENUS_._enArreglo("compras")>=0 || _MENUS_._enArreglo("importaciones")>=0 || _MENUS_._enArreglo("pagos")>=0
        || _MENUS_._enArreglo("traspasos")>=0 || _MENUS_._enArreglo("pedidos")>=0){
        btn.modulo(-165,-35, 28, 23, 'COMPRAS', 'd_menu', 5);
        _('d_menu').innerHTML += "<div id=\"d_menu5\" style=\"display:none\"></div>";
        if(_MENUS_._enArreglo("proveedores")>=0){
            btn.menu('Iprv', -530, -34, 28, 25, 'Proveedores', "inv_proveedores();", 'd_menu5');
        }
        if(_MENUS_._enArreglo("productos")>=0){
            btn.menu('Iprd', -168, -60, 28, 25, 'Productos', "inv_productos();", 'd_menu5');
        }
        if(_MENUS_._enArreglo("kardex")>=0){
            btn.menu('Ikdx', -196, -60, 28, 25, 'K&aacute;rdex', "inv_kardex();", 'd_menu5');
        }
        if(_MENUS_._enArreglo("compras")>=0){
            btn.menu('Icpr', -224, -60, 28, 25, 'Facturas de compras y gastos', "inv_compras();", 'd_menu5');
        }
        if(_MENUS_._enArreglo("liquidaciones")>=0){
            btn.menu('Iliq', -386, -165, 28, 25, 'Liquidaciones de compras y servicios', "inv_liquidaciones();", 'd_menu5');
        }
        if(_MENUS_._enArreglo("notasVenta")>=0){
            btn.menu('INoV', -281, -193, 28, 25, 'Notas de Venta (RISE)', "inv_notas_venta();", 'd_menu5');
        }
        /*if(_MENUS_._enArreglo("importaciones")>=0){
            btn.menu('Iimp', -533, -60, 28, 25, 'Importaciones', "inv_importaciones();", 'd_menu5');
        }*/
        if(_MENUS_._enArreglo("pagos")>=0){
            btn.menu('Ipag', -280, -60, 28, 25, 'Pagos', "inv_pagos();", 'd_menu5');
        }        
        if(_MENUS_._enArreglo("pedidos")>=0){
            btn.menu('Iped', -423, -60, 28, 25, 'Pedidos', "inv_pedidos();", 'd_menu5');
        }
        if(_MENUS_._enArreglo("traspasos")>=0){
            btn.menu('Itra', -489, -114, 28, 25, 'Traspasos', "inv_traspasos();", 'd_menu5');
        }
    }


    if(_MENUS_._enArreglo("compras")>=0 || _MENUS_._enArreglo("activos")>=0 || _MENUS_._enArreglo("personalizacion")>=0 || _MENUS_._enArreglo("ventas")>=0
        || _MENUS_._enArreglo("perdida_activos")>=0){
        btn.modulo(-165,-35, 28, 23, 'ACTIVOS FIJOS', 'd_menu', 6);
        _('d_menu').innerHTML += "<div id=\"d_menu6\" style=\"display:none\"></div>";
        if(_MENUS_._enArreglo("compras")>=0){
            btn.menu('Acpr', -224, -60, 28, 25, 'Compra de activos', "act_compras();", 'd_menu6');
        }
        if(_MENUS_._enArreglo("activos")>=0){
            btn.menu('Aact', -397, -140, 28, 25, 'Activos fijos', "act_activos();", 'd_menu6');
        }
        if(_MENUS_._enArreglo("kardexActivos")>=0){
            btn.menu('Akdx', -196, -60, 28, 25, 'K&aacute;rdex', "act_kardex();", 'd_menu6');
        }
        if(_MENUS_._enArreglo("personalizacion")>=0){
            btn.menu('Aper', -494, -143, 28, 25, 'Personalizaci&oacute;n de activos fijos', "act_personalizacion();", 'd_menu6');
        }
        if(_MENUS_._enArreglo("ventas")>=0){
            btn.menu('Aven', -363, -60, 28, 25, 'Venta de activos', "act_ventas();", 'd_menu6');
        }
        if(_MENUS_._enArreglo("perdida_activos")>=0){
            btn.menu('Avaj', -206, -193, 28, 25, 'P&eacute;rdida de activos', "act_perdidas();", 'd_menu6');
        }
    }
    
    if(_MENUS_._enArreglo("planes")>=0 || _MENUS_._enArreglo("sectores")>=0 || _MENUS_._enArreglo("clientes")>=0
        || _MENUS_._enArreglo("prefacturacion")>=0 || _MENUS_._enArreglo("ventas")>=0 || _MENUS_._enArreglo("cobros")>=0){
        btn.modulo(-165,-35, 28, 23, 'FACTURACI&Oacute;N', 'd_menu', 7);
        _('d_menu').innerHTML += "<div id=\"d_menu7\" style=\"display:none\"></div>";
        if(_MENUS_._enArreglo("planes")>=0){
            btn.menu('Fpln', -524, -114, 28, 25, 'Planes de servicio', "fac_planes();", 'd_menu7');
        }
        if(_MENUS_._enArreglo("sectores")>=0){
            btn.menu('Fsec', -329, -33, 28, 25, 'Sectores', "fac_sectores();", 'd_menu7');
        }
        if(_MENUS_._enArreglo("clientes")>=0){
            btn.menu('Fcli', -335, -60, 28, 25, 'Clientes', "fac_clientes();", 'd_menu7');
        }
        if(_MENUS_._enArreglo("docsbancos")>=0){
            btn.menu('Fdoc', -145, -193, 28, 25, 'Comprobantes de cobros', "fac_documentos();", 'd_menu7');
        }
        if(_MENUS_._enArreglo("docsBancosTmp")>=0){
            btn.menu('Fmov', -316, -191, 28, 25, 'Documentos - movimientos de cuenta', "fac_movimientos();", 'd_menu7');
        }
        if(_MENUS_._enArreglo("cash_pichincha")>=0 || _MENUS_._enArreglo("deb_tarj_pichincha")>=0){
            btn.menu('FcaPi', -69, -232, 28, 25, 'Cash Management Pichincha', "fac_pichinchaCash();", 'd_menu7');
        }
        if(_MENUS_._enArreglo("cash_produbanco")>=0){
            btn.menu('FcaPr', -453, -238, 28, 25, 'Cash Management Produbanco', "fac_produbancoCash();", 'd_menu7');
        }
        /*if(_MENUS_._enArreglo("deb_tarj_pichincha")>=0){
            btn.menu('FtcPi', -109, -233, 28, 25, 'D&eacute;bitos de trjetas de cr&eacute;dito Pichincha', "fac_PichinchaDebitoTarjetas();", 'd_menu7');
        }*/
        if(_MENUS_._enArreglo("docsBancosDebitos")>=0){
            btn.menu('Fdeb', -349, -191, 28, 25, 'Debitos bancarios', "fac_debitos();", 'd_menu7');
        }
        if(_MENUS_._enArreglo("prefacturacion")>=0){
            btn.menu('Ffac', -367, -139, 28, 25, 'Pre-facturaci&oacute;n tarifada', "fac_preFacturas();", 'd_menu7');
        }
        if(_MENUS_._enArreglo("ventas")>=0){
            btn.menu('Fven', -363, -60, 28, 25, 'Ventas', "fac_ventas();", 'd_menu7');
        }
        if(_MENUS_._enArreglo("cobros")>=0){
            btn.menu('Fcob', -280, -60, 28, 25, 'Cobros', "fac_cobros();", 'd_menu7');
        }
        if(_MENUS_._enArreglo("comisiones")>=0){
            btn.menu('Fcom', -416, -233, 28, 25, 'Comisiones vendedores', "fac_comisiones();", 'd_menu7');
        }
        if(_MENUS_._enArreglo("arqueo")>=0){
            btn.menu('Farc', -114, -193, 28, 25, 'Arqueo de Caja', "fac_arqueos();", 'd_menu7');
        }
    }
    
    if(_MENUS_._enArreglo("nodos")>=0 || _MENUS_._enArreglo("antenas")>=0 || _MENUS_._enArreglo("materiales")>=0 || _MENUS_._enArreglo("instalacion")>=0 
         || _MENUS_._enArreglo("soporte")>=0 || _MENUS_._enArreglo("trabajo")>=0 || _MENUS_._enArreglo("hojaRuta")>=0 ){
        btn.modulo(-165,-35, 28, 23, 'RED E INFRAESTRUCTURA', 'd_menu', 8);
        _('d_menu').innerHTML += "<div id=\"d_menu8\" style=\"display:none\"></div>";
        if(_MENUS_._enArreglo("nodos")>=0){
            btn.menu('Rnod', -59, -192, 28, 25, 'Nodos', "inf_nodos();", 'd_menu8');
        }
        if(_MENUS_._enArreglo("antenas")>=0){
            btn.menu('Rant', 0, -192, 28, 25, 'Antenas acopladas', "inf_antenas();", 'd_menu8');
        }
        if(_MENUS_._enArreglo("materiales")>=0){
            btn.menu('Rmat', -514, -174, 28, 25, 'Materiales', "fac_materiales();", 'd_menu8');
        }
        if(_MENUS_._enArreglo("instalacion")>=0){
            btn.menu('Rins', -338, -140, 28, 25, 'Instalaciones / re-instalaciones', "fac_instalacion();", 'd_menu8');
        }
        if(_MENUS_._enArreglo("servidores_ftp")>=0){
            btn.menu('Rftp', 0, -568, 28, 25, 'Servidores de cortes', "inf_servidoresCortes();", 'd_menu8');
        }
        if(_MENUS_._enArreglo("soporte")>=0){
            btn.menu('Rsop', -415, -165, 28, 25, 'Soporte del servicio', "fac_soporte();", 'd_menu8');
        }
        if(_MENUS_._enArreglo("trabajo")>=0){
            btn.menu('Rtra', -385, -165, 28, 25, 'Ordenes de trabajo', "fac_trabajo();", 'd_menu8');
        }
        if(_MENUS_._enArreglo("hojaRuta")>=0){
            btn.menu('Rhor', -176, -194, 28, 25, 'Hojas de rutas (tareas)', "fac_tarea();", 'd_menu8');
        }
    }
    
    /*if(_MENUS_._enArreglo("riesgos")>=0){
        btn.modulo(-165,-35, 28, 23, 'JURIDICO', 'd_menu', 9);
        _('d_menu').innerHTML += "<div id=\"d_menu9\" style=\"display:none\"></div>";
        if(_MENUS_._enArreglo("riesgos")>=0){
            btn.menu('Jcer', -386, -190, 28, 25, 'Central de riesgos', "jur_riesgos();", 'd_menu9');
        }
    }*/
    
    if(_MENUS_._enArreglo("ventas")<0 && _MENUS_._enArreglo("instalacion")<0){
        not_getNotificaciones();
    }
};


/*
var ancho=10;
var alto=10;
var sep=1;
creaPaleta=function(element,callBack){
    var clip,col,top,left;
    var aColors=["00","33","66","99","CC","FF"];
    for(var x=0;x<6;x++){
        for(var y=0;y<6;y++){
            for(var z=0,d=0;z<6;z++,d++){
                col="#"+aColors[z]+aColors[x]+aColors[y];

                clip=document.createElement("a");
                clip.href="Color: "+col;
                clip.funcio=callBack;
                clip.onclick=function(){
                    this.funcio(this.color);
                    return false;
                }
                cs=clip.style;
                cs.backgroundColor=clip.color=col;
                top=sep+y*(alto+sep);
                cs.top=top+"px";
                left=sep+(z*((sep+ancho)*6))+(x*(ancho+sep));
                cs.left=left+"px";
                cs.width=ancho+"px";
                cs.height=alto+"px";
                cs.position="absolute";
                cs.overflow="hidden";

                element.appendChild(clip);
            }
        }
    }
    element.style.position="relative";
    element.style.height=(6*(alto+sep)+1)+"px";
    element.style.width=(6*6*(ancho+sep)+1)+"px";
}
function funcionRetorno(e){alert(e);}
creaPaleta(document.getElementById("paleta"),funcionRetorno);
*/

function esNumero(n) {
    return !isNaN(parseFloat(n)) && isFinite(n);
}
function noAutocompletar(el)
{
    _(el).setAttribute("autocomplete", "off");
}
function _SUP(e)
{
    var c = _getKeyCode(e);
    if(c!=0 && c!=8 && c!=13) {
        _inhibir(e);
    }
}
function _DNI(e)
{
    var c = _getKeyCode(e);
    if((c<48 || c>57 && c<65 || c>90 && c<97 || c>122) && c!=0 && c!=8) {
        _inhibir(e);
    }
}
function _sinEspeciales(e)
{
    var c = _getKeyCode(e);
    if( (c==34 || c==39 || c==124 || c==92) && c!=0 && c!=8) {
        _inhibir(e);
    }
}
function _NoE(e)
{
    if(_getKeyCode(e)==13) {
        _inhibir(e);
    }
}
function iniRedimensionarW(o)
{
    _OBJ=o;
    var d=_('gr_menu').style.display || 'table-cell';
    _rX = (parseInt(_getIzq(_(o))) || ((d=='table-cell') ? 168 : 15)) + (_esSf||_esIE?12:20);
    if(!_esWin){
        _rX -= 6;
    }
    _wOb = parseInt(_('d_fila').style.width) - parseInt(_(o).style.width);
    _NE('mousemove', redimensionarW);
    document.onmouseup = function(){_RE('mousemove', redimensionarW);};
}
function redimensionarW(ev)
{
    var e = ev || window.event;
    _pX = e.screenX - _rX;
    _(_OBJ).style.width = _(_OBJ+'1').style.width = _pX+'px';
    _('d_fila').style.width = (_wOb + _pX) + 'px';
}
function redimensionarH(s)
{
    if(_OBJ){
        _('d_1').style.height = (s=='+' ? (parseInt(_('d_1').style.height) + 1) : (parseInt(_('d_1').style.height) - 1)) + 'px';
        _('d_cuerpo').scrollTop=1000;
        window.setTimeout("redimensionarH('"+s+"')", 10);
    }
}
function setAnchoPanel(p, a)
{
    _('d_'+p+'1').style.width = a+'px';
    _('d_fila').style.width = (parseInt(_('d_11').style.width) + parseInt(_('d_21').style.width) + 60) + 'px';
}
function desplegar()
{
    _DA_ = !_DA_;
    _('gr_despliegue').style.display = _DA_ ? 'block' : 'none';
    _('des_ayuda').innerHTML = _DA_ ? ' ▲ ' : ' ▼ ';
    _AJAX.solicitud('setParametroUsuario', 'p=d&d='+_DA_, false, null);
}
function iniMover(ev)
{
    var e = (_esN) ? ev : event;
    _rY = e.screenY-parseInt(_getTop(_('gr_ayuda')));       
    _rX = e.screenX-parseInt(_getIzq(_('gr_ayuda')));
    _T('gr_ayuda', 70);
    _NE('mousemove', mover);
    document.onmouseup=finMover;
}
function mover(ev)
{       
    var e = (_esN) ? ev : event;
    _pX = e.screenX - _rX;
    _pY = e.screenY - _rY;
    _('gr_ayuda').style.left = _pX+'px';
    _('gr_ayuda').style.top = _pY+'px';
}
function finMover()
{   
    _T('gr_ayuda', 100);    
    _RE('mousemove', mover);
    _AJAX.solicitud('setParametroUsuario', 'p=p&x='+_pX+'&y='+_pY, false, null);
}
function insertarComun(o,cb,g,i,w,gr)
{
    _AUX = _(o).innerHTML;
    _(o).innerHTML='<input type="text" id="comun" style="width:'+w+'px;" onkeypress="_alfanumerico(event)" onkeyup="guardarComun(event, \''+o+'\', \''+cb+'\', \''+g+'\', \''+i+'\', '+w+', \''+gr+'\')" />';
    _(g).style.visibility='visible';
    _(i).style.visibility='hidden';
    _('comun').focus();
}
function guardarComun(e,o,cb,g,i,w,gr)
{
    var c = e==null ? 13 : e.which || event.keyCode;
    if(c==13){
        if(_('comun').value!=''){
            _AJAX.solicitud("insertarComun", "obj="+o+"&gr="+gr+"&an="+w+'&comb='+cb+'&nom='+_('comun').value._trim()._codificarURI(), false, o);
            _(g).style.visibility='hidden';
            _(i).style.visibility='visible';
        }else{
            _MS('Debe llenar la cailla de texto antes de guardar', _('comun'));
        }
        _inhibir(window.event);
    }
    if(c==27){
        _(o).innerHTML = _AUX;
        _(g).style.visibility='hidden';
        _(i).style.visibility='visible';
    }
}
function insertarUbicacion(o,cb,g,i,w,pdr)
{
    _AUX = _(o).innerHTML;
    _(o).innerHTML='<input type="text" id="comun" style="text-transform:uppercase;width:'+w+'px;" onkeypress="_alfanumerico(event)" onkeyup="guardarComun(event, \''+o+'\', \''+cb+'\', \''+g+'\', \''+i+'\', '+w+', \''+pdr+'\')" />';
    _(g).style.visibility='visible';
    _(i).style.visibility='hidden';
    _('comun').focus();
}
function guardarUbicacion(e,o,cb,g,i,w,pdr)
{
    var c = e==null ? 13 : e.which || event.keyCode;
    if(c==13){
        if(_('comun').value!=''){
            if(_pX!='' || _pX!=0){
                _pX = _pX.replace(/:/g, "'");
            }
            if(_pY!='' || _pY!=0){
                _pY = _pY.replace(/:/g, "'");
            }
            var idP = _(pdr)!=null ? _(pdr).value : 1;
            _AJAX.solicitud("setUbicacion", "obj="+o+"&pdr="+idP+"&an="+w+'&comb='+cb+'&nom='+_('comun').value._trim()._codificarURI()+'&onCh='+_pX+'&fun='+_pY, false, o);
            _(g).style.visibility='hidden';
            _(i).style.visibility='visible';
        }else{
            _MS('Debe llenar la casilla de texto antes de guardar', _('comun'));
        }
        _inhibir(window.event);
    }
    if(c==27){
        _(o).innerHTML = _AUX;
        _(g).style.visibility='hidden';
        _(i).style.visibility='visible';
    }
    _pX=0;
    _pY=0;
}
function getUbicacion(o,cb,p,w)
{
    var idP = _(p)!=null ? _(p).value : 1;
    if(_pX!='' || _pX!=0){
        _pX = _pX.replace(/:/g, "'");
    }
    if(_pY!='' || _pY!=0){
        _pY = _pY.replace(/:/g, "'");
    }
    _AJAX.solicitud("getUbicacion", "obj="+o+'&comb='+cb+"&pdr="+idP+"&an="+w+'&onCh='+_pX+'&fun='+_pY, false, o);
    _pX=0;
    _pY=0;
}
function encerar()
{
    fA=-1;
    _FS=-1;
    _WR='';
    _AUX='';
    _('d_11').innerHTML=_('d_21').innerHTML=_('d_filtro').innerHTML='&nbsp;';
    _('d_herra').innerHTML='<div class="amague">.</div>';
    _('d_ayuda').innerHTML = 'Por favor, haga clic en uno de los registros de la tabla para poder editar.<br />'+
    'Tambi&eacute;n puede hacer uso de las opciones que se encuentran an la barra de herramientas.';
}
function msg()
{
    return 'Los campos marcados con un * son obligatorios.';
}
function setFecha(k, fn)
{
    var f = new Date();
    var h = f.getDate() + '/' + (f.getMonth()+1) + '/' + f.getFullYear();
    return "<input type='text' id='"+k+"' name='"+k+"' value='"+h+"' size='10' readOnly><input type='button' value='...' onClick=\"SelectorFecha.crear('"+k+"', 'SQL', '"+fn+"');\" />";
}
function setFechas(fn, or)
{
    o = or || 'v';
    var f = new Date();
    var h = f.getDate() + '/' + (f.getMonth()+1) + '/' + f.getFullYear();
    return "<label id='lbfi'>Desde la fecha: <input type='text' id='fi' name='fi' value='"+h+"' size='10' readOnly><input type='button' value='...' onClick=\"SelectorFecha.crear('fi', 'SQL', '"+fn+"');\" /></label>"+
        (o=='h' ? ' &nbsp;&nbsp;&nbsp; ' : '<br />') + 
    "<label id='lbff'>Hasta la fecha: &nbsp;<input type='text' id='ff' name='ff' value='"+h+"' size='10' readOnly><input type='button' value='...' onClick=\"SelectorFecha.crear('ff', 'SQL', '"+fn+"');\" /></label>";
}
function setCombo(k, j, f)
{
    var js = eval('(' + j + ')');
    var h = "<select id='"+k+"' name='"+k+"' onchange=\""+f+"\">";
    for(var i=0; i<js.tbl.length; i++){
        h += "<option value='"+js.tbl[i][0]+"'>"+js.tbl[i][1]+"</option>";
    }
    h+="</select>";
    return h;
}
function setRadio(k, j, f)
{
    var js = eval('(' + j + ')');
    var h ='';
    for(var i=0; i<js.tbl.length; i++){
        h += "<label onclick=\""+f+"\"><input type='radio' id='"+k+i+"' name='"+k+"' "+(i==0?'checked':'')+" value='"+js.tbl[i][0]+"' />"+js.tbl[i][1]+"</label><br />";
    }
    return h;
}
function setCaja(k, f)
{
    return "<input type='text' id='"+k+"' name='"+k+"' onkeypress=\"if(_getKeyCode(event)==13){"+f+"}\" />";
}
function setOculto(k, v)
{
    return "<input type='hidden' id='"+k+"' name='"+k+"' value=\""+v+"\" />";
}
function setBoton(t, v, f)
{
    return " &nbsp;<input type='"+(t || 'submit')+"' value='"+v+"' onclick='"+f+"'>";
}
function setPieReporte()
{
    return '<hr />' + setRadio('x', '{tbl:[{0:"pdf",1:"PDF"},{0:"xls",1:"EXCEL"}]}') + '<br />' + setBoton('submit', 'Imprimir');
}
function getMarcado(id)
{
    var d = id || 'jmTbl';
    var i=0;
    var o = _('chT_'+d+i);
    var k='';
    while(o!=null){
        if(o.checked){
            k = _(d+i+'0').value;
            break;
        }
        i++;
        o = _('chT_'+d+i);
    }
    if(k==''){
        alert('Debe marcar un registro para poder realizar la acción.');
        return false;
    }
    return k;
}
function getMarcados(id)
{
    var d = id || 'jmTbl';
    var i=0;
    var o = _('chT_'+d+i);
    var k='';
    while(o!=null){
        if(o.checked){
            k += _(d+i+'0').value + ',';
        }
        i++;
        o = _('chT_'+d+i);
    }
    if(k==''){
        alert('Debe marcar por lo menos un registro para poder realizar la acción.');
        return false;
    }
    k = k.substr(0, k.length-1);
    return k;
}
function esDocumento(ob) 
{              
  var obj = document.getElementById(ob);
  if(obj.value=='') {
      return false;
  }  
  numero = obj.value;  
  var suma = 0;      
  var residuo = 0;      
  var pri = false;      
  var pub = false;            
  var nat = false;      
  var numeroProvincias = 22;                  
  var modulo = 11;
  var ok=1;
  for (i=0; i<numero.length && ok==1 ; i++){
     var n = parseInt(numero.charAt(i));
     if (isNaN(n)) ok=0;
  }
  if (ok==0){
     _MS('Debe ingresar solo caracteres numéricos', obj);
     return false;
  }
              
  if (numero.length < 10 ){              
     _MS('El número de documento ingresado no es válido', obj);
     return false;
  }
  provincia = numero.substr(0,2);      
  if (provincia < 1 || provincia > numeroProvincias){           
     _MS('El código de la provincia (dos primeros dígitos) del número de documento es inválido', obj);
     return false;       
  }
  d1  = numero.substr(0,1);         
  d2  = numero.substr(1,1);         
  d3  = numero.substr(2,1);         
  d4  = numero.substr(3,1);         
  d5  = numero.substr(4,1);         
  d6  = numero.substr(5,1);         
  d7  = numero.substr(6,1);         
  d8  = numero.substr(7,1);         
  d9  = numero.substr(8,1);         
  d10 = numero.substr(9,1);                
  if (d3==7 || d3==8){           
     _MS('El tercer dígito del número de documento ingresado es inválido', obj);
     return false;
  }         
  if (d3 < 6){           
     nat = true;            
     p1 = d1 * 2;if (p1 >= 10) p1 -= 9;
     p2 = d2 * 1;if (p2 >= 10) p2 -= 9;
     p3 = d3 * 2;if (p3 >= 10) p3 -= 9;
     p4 = d4 * 1;if (p4 >= 10) p4 -= 9;
     p5 = d5 * 2;if (p5 >= 10) p5 -= 9;
     p6 = d6 * 1;if (p6 >= 10) p6 -= 9; 
     p7 = d7 * 2;if (p7 >= 10) p7 -= 9;
     p8 = d8 * 1;if (p8 >= 10) p8 -= 9;
     p9 = d9 * 2;if (p9 >= 10) p9 -= 9;             
     modulo = 10;
  }         
  else if(d3 == 6){           
     pub = true;             
     p1 = d1 * 3;
     p2 = d2 * 2;
     p3 = d3 * 7;
     p4 = d4 * 6;
     p5 = d5 * 5;
     p6 = d6 * 4;
     p7 = d7 * 3;
     p8 = d8 * 2;            
     p9 = 0;            
  }         
  else if(d3 == 9) {           
     pri = true;                                   
     p1 = d1 * 4;
     p2 = d2 * 3;
     p3 = d3 * 2;
     p4 = d4 * 7;
     p5 = d5 * 6;
     p6 = d6 * 5;
     p7 = d7 * 4;
     p8 = d8 * 3;
     p9 = d9 * 2;            
  }
  suma = p1 + p2 + p3 + p4 + p5 + p6 + p7 + p8 + p9;                
  residuo = suma % modulo;                                         
  digitoVerificador = residuo==0 ? 0: modulo - residuo;                
  if (pub==true){           
     if (digitoVerificador != d9){                          
        _MS('El ruc de la empresa del sector público es incorrecto.', obj);
        return false;
     }                  
     if ( numero.substr(9,4) != '0001' ){                    
        _MS('El ruc de la empresa del sector público debe terminar con 0001', obj);
        return false;
     }
  }         
  else if(pri == true){         
     if (digitoVerificador != d10){                          
        _MS('El ruc de la empresa del sector privado es incorrecto.', obj);
        return false;
     }         
     if ( numero.substr(10,3) != '001' ){                    
        _MS('El ruc de la empresa del sector privado debe terminar con 001', obj);
        return false;
     }
  }      

  else if(nat == true){         
     if (digitoVerificador != d10){                          
        _MS('El número de cédula o ruc de la persona natural es incorrecto.', obj);
        return false;
     }         
     if (numero.length >10 && numero.substr(10,3) != '001' ){                    
        _MS('El ruc de la persona natural debe terminar con 001', obj);
        return false;
     }
  }      
  return true;   
}    
function _enMatrizJSON(v, x, p)
{
    for(var i=0;i<v.tbl.length;i++){
        if(v.tbl[i][p]==x){
            return i;
        }
    }
    return -1;
}
function cambiarClave()
{
    Ventana.crear('vta_clave', 'Cambiar mi Contrase&ntilde;a', "ancho=380,alto=150,modal=true,cerrar=true", ''); 
    _AJAX.solicitud('cambiarClave', '', false, 'vta_clave_html');
}
function establecerClave(f)
{
    var c = _('r_clave');
    var c2 = _('r_clave2');
    if(c.value=='' || c2.value==''){
        alert('Debe ingresar una nueva contraseña y confirmarla antes de guardar.');
        _('r_clave').focus();
        return false;
    }       
    if(c.value!=c2.value){              
        c2.value = c.value = '';
        alert('Confirmación de nueva contraseña no coincide.');
        c.focus();
        return false;
    }
    return _AJAX.enviarForm(f);
}
function creditos()
{
    Ventana.crear('credito', 'Acerca de...', "ancho=400,alto=360,modal=true,cerrar=true", '');
    _AJAX.solicitud('Creditos', '', false, 'credito_html');
}
function imprimir(p)
{
    window.open(p, '_blank', 'top=50,left=50,width=750,height=500');
}
function imprimirReporte()
{
    var fi = (_('fi')!=null) ? _('fi').value : '';
    var ff = (_('ff')!=null) ? _('ff').value : '';
    var bfi = (_('bfi')!=null) ? _('bfi').value : '';
    var bff = (_('bff')!=null) ? _('bff').value : '';
    var idC = (_('idCmb')!=null) ? _('idCmb').value : '';
    var suc = _('idSuc')!=null ? _('idSuc').value : '';
    var an = _('an')!=null ? _('an').value : '2011';
    var ms = _('ms')!=null ? _('ms').value : '1';
    var pd = _('pd')!=null ? _('pd').value : '';
    var pd1 = _('pd1')!=null ? _('pd1').value : '';
    var i=0;
    while(_('z'+i)!=null){
        if(_('z'+i).checked){
            break;
        }
        i++;
    }
    var p = 'op='+_('z'+i).value+'&fi='+fi+'&ff='+ff+'&bfi='+bfi+'&bff='+bff+'&idSuc='+suc+'&an='+an+'&ms='+ms+'&pd='+pd+'&pd1='+pd1+'&idCmb='+idC+'&WH='+_WR;
    var a = (_('x').checked) ? 'impReportePdf?' : 'impReporteXls?';
    imprimir(a+p);
    _R('bloq_vta_im');
    _R('vta_im');
}
function imprimirReporte2()
{
    var fi = (_('bfi')!=null) ? _('bfi').value : '';
    var ff = (_('bff')!=null) ? _('bff').value : '';
    var suc = _('idSuc')!=null ? _('idSuc').value : '';
    var an = _('an')!=null ? _('an').value : '2011';
    var i=0;
    while(_('z'+i)!=null){
        if(_('z'+i).checked){
            break;
        }
        i++;
    }
    var p = 'op='+_('z'+i).value+'&fi='+fi+'&ff='+ff+'&op2=0&idSuc='+suc+'&an='+an+'&ms=1&idS=1&WH='+_WR; 
    var a = (_('x').checked) ? 'impReportePdf?' : 'impReporteXls?';
    imprimir(a+p);
    _R('bloq_vta_im');
    _R('vta_im');
}
function reporte(f)
{
    var p = 'WHERE='+_WR+'&';
    var i=0;
    var pg = '';
    var tp = '';
    while(f.elements[i]!=null){
        if(f.elements[i].type!='reset' && f.elements[i].type!='submit' && f.elements[i].type!='button' && f.elements[i].type!='image' && f.elements[i].type!=undefined && f.elements[i].type!='fieldset'){
            if(f.elements[i].type=='checkbox'){
                p += f.elements[i].name + '=' + f.elements[i].checked + '&';
            }else if(f.elements[i].type=='radio'){
                      if(f.elements[i].checked && f.elements[i].name=='z'){
                          pg = f.elements[i].value._trim();
                      }if(f.elements[i].checked && f.elements[i].name=='x'){
                          tp = f.elements[i].value._trim();
                      }
                      if(f.elements[i].name!='x' && f.elements[i].name!='z'){
                          p += f.elements[i].name + '=' + f.elements[i].value._trim() + '&';
                      }
                  }else{
                      p += f.elements[i].name + '=' + f.elements[i].value._trim() + '&';
                  }
        }
        i++;
    }
    p = p.substr(0, p.length-1);
    imprimir(tp+pg+'?'+p);
    return false;
}
function htmlToEspecial(t)
{
    return t.replace(/&amp;/g, '&');
}
/*  NOTIFICACIONES  */
function not_getNotificaciones()
{
    _AJAX.solicitud('getNotificaciones', '', false, 'd_notfCuerpo');
    window.setTimeout("not_getNotificaciones()", 600000);
}
function not_mostrar(r)
{
    if(_('d_notf')==null){
        var d = document.createElement('DIV');
        d.id = 'd_notf';
        d.className='notificacion';
        d.innerHTML = '<div style="text-align:right"><img src="img/cerrar.gif" style="cursor:pointer" onclick="_R(\'d_notf\')" title="cerrar" alt="cerrar" /></div>'+
                      '<div id="d_notfCuerpo"><div id="d_notfCuerpo" class="jm_tabla">'+r+'</div></div>';
        d.onmouseover = function(){_Fch = false;_T('d_notf', 100);};
        d.onmouseout = function(){_Fch = true;window.setTimeout("not_desvanecer(100)", 10000);};
        document.body.appendChild(d);
        not_desplegar(-200);
        window.setTimeout("not_desvanecer(100)", 20000);
    }
}
function not_desplegar(i)
{
    if(i<=0){
        _('d_notf').style.bottom= i + 'px';
        window.setTimeout("not_desplegar("+(i+2)+")", 10);
    }
}
function not_desvanecer(i)
{
    if(_Fch){
        if(i>=0){
            _T('d_notf', i);
            window.setTimeout("not_desvanecer("+(i-1)+")", 40);
        }else{
            _R('d_notf');
        }
    }
}
function not_ocultar(k)
{
    _AJAX.solicitud('ocultarNotificacion', 'id='+k);
}
/* BASURERA */
function papelera()
{
    encerar();
    h = 'Registros de: <select id="obPap" onchange="adm_papeleraFiltrar();">';
    if(_MENUS_._enArreglo("roles")>=0){
        h += '<option value="tbl_rol">ROLES</option>';
    }
    if(_MENUS_._enArreglo("usuarios")>=0){
        h += '<option value="tbl_usuario">USUARIOS</option>';
    }
    if(_MENUS_._enArreglo("impuestos")>=0){
        h += '<option value="tbl_tabla_impuesto">TABLA DE IMPUESTOS</option>';
    }
    if(_MENUS_._enArreglo("precios")>=0){
        h += '<option value="tbl_lista_precio">LISTAS DE PRECIOS</option>';
    }
    if(_MENUS_._enArreglo("sucursales")>=0){
        h += '<option value="tbl_sucursal">SUCURSALES</option>';
    }
    if(_MENUS_._enArreglo("depreciaciones")>=0){
        h += '<option value="tbl_tabla_depreciacion">TABLA DE DEPRECIACIONES</option>';
    }
    if(_MENUS_._enArreglo("plan_cuentas")>=0){
        h += '<option value="tbl_plan_cuenta">PLAN DE CUENTAS</option>';
    }
    if(_MENUS_._enArreglo("proveedores")>=0){
        h += '<option value="tbl_proveedor">PROVEEDORES</option>';
    }
    if(_MENUS_._enArreglo("productos")>=0){
        h += '<option value="tbl_producto">PRODUCTOS</option>';
    }
    if(_MENUS_._enArreglo("traspasos")>=0){
        h += '<option value="tbl_traspaso">TRASPASOS</option>';
    }
    if(_MENUS_._enArreglo("pedidos")>=0){
        h += '<option value="tbl_pedido">PEDIDOS</option>';
    }
    if(_MENUS_._enArreglo("clientes")>=0){
        h += '<option value="tbl_cliente">CLIENTES</option>';
    }
    if(_MENUS_._enArreglo("empleados")>=0){
        h += '<option value="tbl_empleado">EMPLEADOS</option>';
    }
    if(_MENUS_._enArreglo("planes")>=0){
        h += '<option value="tbl_plan_servicio">PLANES DEL SERVICIO DE INTERNET </option>';
    }
    if(_MENUS_._enArreglo("sectores")>=0){
        h += '<option value="tbl_sector">SECTORES </option>';
    }
    h += '</select>';
    _('d_filtro').innerHTML=h;
    btn.icono(-455, 3, 34, 34, 'Restaurar', 'adm_restaurar();', 'd_herra');
    _('d_ayuda').innerHTML = 'Selecciones los registros y luego pulse en el &iacute;cono RESTAURAR.';
    adm_papeleraFiltrar();
    setAnchoPanel(2, 20);
}
function adm_papeleraFiltrar()
{   
    var w = "where eliminado=TRUE";
    w = encodeURI(w);
    var p = _('obPap').value;
    if(p=='tbl_rol'){
        new Tabla('d_11', 'jmTbl', '', 'tbl_rol', 'ROL', 'id_rol,rol', '0,200', _altEdi, '', w);
    }
    if(p=='tbl_usuario'){
        new Tabla('d_11', 'jmTbl', '', 'tbl_usuario', 'USUARIO', 'alias,alias', '0,200', _altEdi, '', w);
    }
    if(p=='tbl_tabla_impuesto'){
        new Tabla('d_11', 'jmTbl', '', 'tbl_tabla_vigencia_impuesto', 'VIGENTE DESDE,VIGENTE HASTA', 'id_tabla_vigencia_impuesto,vigente_desde,vigente_hasta', '0,75,75', _altEdi, '', w);
    }
    if(p=='tbl_lista_precio'){
        new Tabla('d_11', 'jmTbl', '', 'tbl_lista_precio', 'NOMBRE,% UTILIDAD', 'id_lista_precio,lista,utilidad', '0,100,100', _altEdi, '', w);
    }
    if(p=='tbl_sucursal'){
        new Tabla('d_11', 'jmTbl', '', 'tbl_sucursal', 'SUCURSAL,UBICACION,COMPUTADORES,ESTADO', 'id_sucursal,sucursal,ubicacion,ip_computador', '0,100,100,200', _altEdi, '', w);
    }
    if(p=='tbl_tabla_depreciacion'){
        new Tabla('d_11', 'jmTbl', '', 'tbl_tabla_depreciacion', 'GRUPO,Nro. A&Ntilde;OS,% DEP.', 'id_tabla_depreciacion,depreciacion,num_anios,porcentaje', '0,200,80,80', _altEdi, '', w);
    }
    if(p=='tbl_plan_cuenta'){
        new Tabla('d_11', 'jmTbl', '', 'tbl_plan_cuenta', 'CODIGO,CUENTA,TIPO', 'id_plan_cuenta,codigo_cuenta,nombre_cuenta', '0,150,300', _altEdi, '', w);
    }
    if(p=='tbl_proveedor'){
        new Tabla('d_11', 'jmTbl', '', 'tbl_proveedor', 'RUC,RAZ&Oacute;N SOCIAL', 'id_proveedor,ruc,razon_social', '0,100,250,250,90', _altEdi, '', w);
    }
    if(p=='tbl_producto'){
        new Tabla('d_11', 'jmTbl', '', 'tbl_producto', 'CODIGO,DESCRIPCION,STOCK MIN,STOCK,COSTO', 'id_producto,codigo,descripcion,stock_min,stock,precio_costo', '0,90,200,80,80,80', _altEdi, '', w);
    }
    if(p=='tbl_traspaso'){
        new Tabla('d_11', 'jmTbl1', '', 'tbl_traspaso', '# TRAS.,USUARIO ODIGEN,FECHA ENVIO,USUARIO RECEP.,FECHA RECEP.', 'id_traspaso,num_traspaso,usuario_origen,fecha_envio,usuario_recepcion,fecha_recepcion', '0,50,90,90,90,90', _altEdi-10, '', w);
    }
    if(p=='tbl_pedido'){
        new Tabla('d_11', 'jmTbl1', '', 'tbl_pedido', '# PEDI.,FECHA PEDIDO,USUARIO', 'id_pedido,num_pedido,fecha_pedido,usuario', '0,50,90,90', _altEdi-10, '', w);
    }
    if(p=='tbl_cliente'){
        new Tabla('d_11', 'jmTbl', '', 'tbl_cliente', 'RUC,RAZ&Oacute;N SOCIAL,DIRECCI&Oacute;N,TELEFONO', 'id_cliente,ruc,razon_social,direccion,telefono', '0,100,250,250,90', _altEdi, '', w);
    }
    if(p=='tbl_empleado'){
        new Tabla('d_11', 'jmTbl', '', 'tbl_empleado', 'IDENTI.,NOMBRES,APELLIDOS,EDAD,CAllE,NUM.,NACIONALIDAD,F. INGRESO', 'id_empleado,dni,nombre,apellido,getEdad(fecha_nac),calle,numero,nacionalidad,toDateSQL(fecha_ingreso)', '0,80,150,150,40,90,80,110,90', _altEdi, '', w);
    }
    if(p=='tbl_plan_servicio'){
        new Tabla('d_11', 'jmTbl', '', 'tbl_plan_servicio', 'PLAN', 'id_plan_servicio,plan', '0,250', _altEdi, '', w);
    }
    if(p=='tbl_sector'){
        new Tabla('d_11', 'jmTbl', '', 'tbl_sector', 'SECTOR,COST. INSTALACION', 'id_sector,sector,costo_instalacion', '0,150,100', _altEdi, '', w);
    }
}
function adm_restaurar(d)
{
    var k = getMarcado();
    if(!k){
        return false;
    }
    _AJAX.solicitud('Restaurar', 't='+_('obPap').value+'&c='+getMarcados()+'&f=adm_papeleraFiltrar();', true, null);
}
/* AUDITORIA */
function seg_auditoria()
{
    encerar();
    var d = new Date();
    _('d_filtro').innerHTML = 'Desde la fecha: <input type="text" id="fi" value="'+d._fecha('SQL')+'" readonly><input type="button" value="..." onClick="SelectorFecha.crear(\'fi\', \'SQL\');" /><br>'+
    'Hasta la fecha: <input type="text" id="ff" value="'+d._fecha('SQL')+'" readonly><input type="button" value="..." onClick="SelectorFecha.crear(\'ff\', \'SQL\');" /><br>'+
    'Acci&oacute;n: <input type="text" id="txt" onkeypress="var c=event.which || event.keyCode;if(c==13){seg_auditoriaFiltrar();}" >' +
    '&nbsp;<input type="button" value="Filtrar" onclick="seg_auditoriaFiltrar();">';
    _('d_ayuda').innerHTML = 'Para buscar dentro de la columna ACCION, escriba una palabra o frase dentro de la caja de texto de la barra de filto.';
    seg_auditoriaFiltrar();
    setAnchoPanel(2, 20);
}
function seg_auditoriaFiltrar()
{
    var w = "where fecha between '"+_('fi').value+"' and '"+_('ff').value+"'";
    if(_('txt').value!=''){
        w += " and lower(transaccion) like '%"+_('txt').value.toLowerCase()+"%'";
    }
    w = encodeURI(w);
    new Tabla('d_11', 'jmTbl', '', 'tbl_auditoria', 'USUARIO,IP,FECHA,HORA,ACCION', 'id_auditoria,alias,ip_maquina,toDateSQL(fecha),hora,transaccion', '0,110,100,85,75,340', _altEdi, '', w+" order by fecha,hora");
}
/* ROLES */
function seg_roles()
{
    encerar();
    _('d_filtro').innerHTML = 'Roles: <input type="text" id="t_b" onkeyup="seg_buscarRol();">';
    btn.icono(-360, 2, 34, 34, 'Nuevo Rol', 'seg_rolEditar(-1);', 'd_herra');
    btn.icono(-391, 3, 34, 34, 'Eliminar', "_objT.eliminar('Se eliminarán también los usuarios que hacen referencia a los roles marcados para eliminarse.');", 'd_herra');
    if(_MENUS_._enArreglo("privilegios")>=0){
        btn.separador('d_herra');
        btn.icono(-424, 2, 34, 34, 'Privilegios', "seg_rolPrivilegios();", 'd_herra');
    }
    seg_buscarRol();
}
function seg_rolEditar(k)
{    
    if(k==1){
        alert('El rol administradores no puede ser editado.');
        return false;
    }
    _('d_ayuda').innerHTML = 'Por favor, no utilize caracteres especiales como: t&iacute;ldes, &ntilde; o s&iacute;mbolos especiales (&, $, etc).'+msg();
    _AJAX.solicitud('frmRol', 'id='+k, false, 'd_21');
    setAnchoPanel(2, 300);
}
function seg_rolGuardar(f)
{
    if(f.r.value==''){
        _MS('Por favor, ingrese el nombre del rol (grupo de usuarios).', f.r);
        return false;
    }
    return _AJAX.enviarForm(f);
}
function seg_buscarRol()
{
    var w='';
    var a=_('t_b').value;
    if(a!=''){
        w = "where lower(rol) like '%"+a.toLowerCase()+"%'";
    }
    _WR = encodeURI(w);
    new Tabla('d_11', 'jmTbl', '', 'vta_rol', 'ROL', 'id_rol,rol', '0,250', _altEdi, 'seg_rolEditar(^);', _WR);
}
function seg_rolPrivilegios()
{
    var k = getMarcado();
    if(!k){
        return false;
    }
    if(k==1){
        alert('Los privilegios del rol administradores no pueden ser editados.');
        return false;
    }
    _('d_ayuda').innerHTML = 'Por favor, seleccione los niveles de acceso al sistema, luego proceda a conceder o revocar los privilegios.';
    _AJAX.solicitud('frmPrivilegios', 'id='+k+'&altB='+_altBody, false, 'd_21');
    setAnchoPanel(2, 650);
}
function seg_setPrivilegio(ac)
{    
    var i=0;
    var j=0;
    var p='ac='+ac+'&id_rol='+_('idR').value;
    if(ac==1){
        while(_('ch_p'+i)!=null){
            if(_('ch_p'+i).checked){
                p += '&id_p'+j+'='+_('id_p'+i).value;
                j++;
            }
            i++;
        }
        if(j==0){
            alert('Debe seleccionar por lo menos un ítem de la lista de privilegios para poder conceder.');
            return false;
        }
    }else{
        while(_('ch_p_as'+i)!=null){
            if(_('ch_p_as'+i).checked){
                p += '&id_p_as'+j+'='+_('id_p_as'+i).value;
                j++;
            }
            i++;
        }
        if(j==0){
            alert('Debe seleccionar por lo menos un ítem de la lista de privilegios concedidos para poder revocar.');
            return false;
        }
    }
    _AJAX.solicitud('frmPrivilegiosGuardar', p, true, 'body');
}
/* USUARIOS */
function seg_usuarios()
{
    encerar();
    _('d_filtro').innerHTML = 'Usuarios: <input type="text" id="t_b" onkeyup="seg_buscarUsuario();">';
    btn.icono(-360, 2, 34, 34, 'Nuevo Usuario', "seg_usuarioEditar('-1');", 'd_herra');
    btn.icono(-391, 3, 34, 34, 'Eliminar', "_objT.eliminar();", 'd_herra');
    seg_buscarUsuario();
    setAnchoPanel(2, 460);
}
function seg_usuarioEditar(k)
{         
    if(k.toLowerCase()=='administrador'){
        alert('El usuario administrador no puede ser editado.');
        return false;
    }
    _('d_ayuda').innerHTML = 'Por favor, no utilize caracteres especiales como: t&iacute;ldes, &ntilde; o s&iacute;mbolos especiales (&, $, etc).<br/>'+
                             msg()+
                            '<br />En el campo [Pc\' desde donde puede autenticarse], debe mantenerse las direcciones [ 127.0.0.1-0:0:0:0:0:0:0:1 ] '+
                            'para autenticarse desde el equipo donde se encuentra instalado el sistema.'+
                            '<br />Para especificar mas de una direcci&oacute;n IP separe con &nbsp;  -  &nbsp; cada direcci&oacute;n.';
    _AJAX.solicitud('frmUsuario', 'id='+k, false, 'd_21');
}
function seg_usuarioGuardar(f)
{
    if(f.a.value==''){
        _MS('Por favor, ingrese el nombre del usuario.', f.a);
        return false;
    }
    if((f.c.value=='' || f.c2.value=='') && f.id.value=='-1'){
        f.c.value=f.c2.value='';
        _MS('Por favor, ingrese una contraseña inicial.', f.c);
        return false;
    }           
    if(f.c.value!=f.c2.value){
        f.c.value=f.c2.value='';
        _MS('Confirmación de contrasña no coincide.', f.c);
        return false;       
    }
    return _AJAX.enviarForm(f);
}
function seg_buscarUsuario()
{
    var w='';
    var a=_('t_b').value;
    if(a!=''){
        w = "where lower(alias) like '%"+a.toLowerCase()+"%'";
    }
    _WR = encodeURI(w)+" order by rol,alias";
    new Tabla('d_11', 'jmTbl', '', 'vta_usuario_rol', 'USUARIO,ROL,PC', 'alias,alias,rol,autenticacion_ip', '0,150,220,200', _altEdi, 'seg_usuarioEditar(^);', _WR);
}
/* CONFIGURACION */
function adm_configuracion()
{
    encerar();
    _('d_ayuda').innerHTML = 'Haga clic en el nombre del m&oacute;dulo para poder editar su confguraci&oacute;n.';
    _('d_11').innerHTML = "<div class='H3' align='center'>CONFIGURACIONES</div>"+
    "<div style='width:250px;'><ol class='inTxt'>"+
    "<li><a href='javascript:void(0)' onclick='adm_frmConfiguracion(1)'>FORMULARIOS DIMM</a></li>"+
    "<li><a href='javascript:void(0)' onclick='adm_frmConfiguracion(2)'>MODULO DE CONTABILIDAD</a></li>"+
    "<li><a href='javascript:void(0)' onclick='adm_frmConfiguracion(3)'>MODULO DE INVENTARIO Y GASTOS</a></li>"+
    /*"<li><a href='javascript:void(0)' onclick='adm_frmConfiguracion(31)'>MODULO DE IMPORTACIONES</a></li>"+*/
    "<li><a href='javascript:void(0)' onclick='adm_frmConfiguracion(4)'>MODULO DE FACTURACION</a></li>"+
    /*"<li><a href='javascript:void(0)' onclick='adm_frmConfiguracion(5)'>MODULO DE NOMINA</a></li>"+*/
    "<li><a href='javascript:void(0)' onclick='adm_frmConfiguracion(7)'>REPORTES</a></li>"+
    "<li><a href='javascript:void(0)' onclick='adm_frmConfiguracion(8)'>RESPALDOS DE LA BASE DE DATOS</a></li>"+
    /*"<li><a href='javascript:void(0)' onclick='adm_frmConfiguracion(9)'>CONFIGURACION DEL SERVIDOR DE CORREOS</a></li>"+*/
    "<li><a href='javascript:void(0)' onclick='adm_frmConfiguracion(10)'>CONFIGURACION DEL SERVIDOR RADIUS</a></li>"+
    "<li><a href='javascript:void(0)' onclick='adm_frmConfiguracion(11)'>CONFIGURACION DEL SERVIDOR AIRCONTROL</a></li>"+
    "<li><a href='javascript:void(0)' onclick='adm_frmConfiguracion(12)'>CONFIGURACION DEL SERVIDOR SFTP</a></li>"+
    "<li><a href='javascript:void(0)' onclick=\"imprimir('formatoCheque');\">CONFIGURAR FORMATO DE CHEQUES</a></li>"+
    "</ol></div>";
    adm_frmConfiguracion(1);
    setAnchoPanel(1, 250);
    setAnchoPanel(2, 600);
}
function adm_frmConfiguracion(m)
{
    _AJAX.solicitud('frmConfiguracion', 'altB='+_altBody+'&m='+m, false, 'd_21');
    _('d_ayuda').innerHTML = '';
    if(m==7){
         _('d_ayuda').innerHTML = 'Para cambiar el logotipo haga clic en la imagen.<br/>';
    }
    _('d_ayuda').innerHTML += 'Nota.- Los valores establecidos en la secci&oacute;n de configuraci&oacute;n afectar&aacute;n a todos los m&oacute;dulos del sistema.';
}
function adm_setImagen(t, cN, cB, pk)
{    
    Ventana.crear('vta_subir', 'Transferencia de Archivos', "ancho=440,alto=190,modal=true,cerrar=true", ''); 
    _AJAX.solicitud('TransferirArchivo', 't='+t+'&cN='+cN+'&cB='+cB+'&pk='+pk, true, 'vta_subir_html')
}
function adm_setImagenPermiso(t, cN, cB, pk)
{    
    Ventana.crear('vta_subir', 'Transferencia de Archivos', "ancho=440,alto=190,modal=true,cerrar=true", ''); 
    _AJAX.solicitud('TransferirArchivo', 't='+t+'&cN='+cN+'&cB='+cB+'&pk='+_('id').value, true, 'vta_subir_html')
}
function iniciaTransferencia()
{
    if(_('archivo').value==''){
        alert('Por favor, seleccione un archivo antes de subirlo al servidor.');
        return false;
    }
    _('cargando').style.display = 'block';
    _('frmArchivo').style.display = 'none';
    return true;
}
function finTransferencia(m, a)
{
    if(m==0){
        _('tagImg').src = 'img/'+a;
        _R('bloq_vta_subir');   
        _R('vta_subir');
    }else{
        alert(m);
    }
    _('cargando').style.display = 'none';
    _('frmArchivo').style.display = 'block'; 
    _('archivo').value='';
    return true;   
}
function adm_configuracionGuardar(f)
{
    switch(_('m').value){
        case 1:
            if(f.ruc.value=='' || f.razon_social.value=='' || f.dir_matriz.value=='' || f.telf_matriz.value=='' || f.email.value=='' || f.num_ident.value=='' || f.ruc_contador.value==''){
                alert('Los campos marcados con un  *  son bligatorios.');
                return false;
            }
            if(f.ruc.value.length!=13 || f.ruc_contador.value.length!=13){
                alert('El número del ruc debe contener trece números');
                return false;
            }
            if(!esDocumento('ruc') || !esDocumento('ruc_contador')){
                return false;
            }
        break;
        case 8:
            if(f.path_pg_dump.value=='' || f.path_resguardo.value==''){
                alert('Los campos marcados con un  *  son bligatorios.');
                return false;
            }
        break;
        case 9:
            if(f.mail_svr.value=='' || f.mail_svr.value==''){
                alert('Los campos marcados con un  *  son bligatorios.');
                return false;
            }
        break;
    }
    if(confirm('Los valores establecidos en la sección de configuración afectarán a los módulos del sistema. ¿Está seguro de guardar los cambios?')){
        _AJAX.enviarForm(f);
    }
    return false;
}
/* IMPUESTOS (RENTA - IVA) */
function adm_impuestos()
{
    encerar();
    btn.icono(-360, 2, 34, 34, 'Nueva Tabla de Impuestos', 'adm_impuestoEditar(-1);', 'd_herra');
    adm_impuestoBuscar();
    setAnchoPanel(2, 680);
}
function adm_impuestoEditar(k)
{
    _('d_ayuda').innerHTML = 'Si es una nueva tabla de impuestos, guarde la fecha de vigencia de inicio para poder ingresar la tabla de retenciones.' + msg();
    _AJAX.solicitud('frmTablaImpuestos', 'id='+k+'&altB='+_altBody, false, 'd_21');
}
function adm_impuestoGuardar(f)
{
    if(f.fi.value==''){
        alert('Por favor. La fecha de vigencia de inicio es obligatoria.');
        return false;
    }
    return _AJAX.enviarForm(f);
}
function adm_impuestoBuscar()
{
    new Tabla('d_11', 'jmTbl', '', 'vta_tabla_vigencia_impuesto', 'VIGENTE DESDE,VIGENTE HASTA', 'id_tabla_vigencia_impuesto,toDateSQL(vigente_desde),toDateSQL(vigente_hasta)', '0,75,75', _altEdi, 'adm_impuestoEditar(^);', '');
}
function adm_retencionNueva()
{
    var i=0;
    while(_('rTI'+i)!=null){
        i++;
    }
    var tr = document.createElement("tr");
    tr.id='rTI'+i;
    tr.className='jm_filaPar';
    tr.style.verticalAlign='top';
    var cl = _('ax').cloneNode(true);
    cl.innerHTML = cl.innerHTML.replace(/_Xx_/g, 'idPcC'+i);
    var clv = _('ax').cloneNode(true);
    clv.innerHTML = clv.innerHTML.replace(/_Xx_/g, 'idPcV'+i);
    _('tblI').appendChild(tr);
    tr.innerHTML="<td width='80'><select id='i"+i+"' onchange='adm_retencionEditar(event, "+i+");'><option value='RENTA'>RENTA</option><option value='IVA'>IVA</option></select></td>"+
                "<td width='70'><input id='c"+i+"' type='text' style='border:0px;' size='7' maxlength='5' onkeypress=\"_evaluar(event, '0123456789ABCDEFGHIJKLMNÑOPQRSTUVWXYZ');\" onkeyup=\"adm_retencionEditar(event, "+i+");\"></td>"+
                "<td width='160'><textarea id='d"+i+"' rows='1' style='width:155px;border:0px;' onkeyup=\"adm_retencionEditar(event, "+i+");\" ></textarea></td>"+
                "<td align='right' width='60'><input id='p"+i+"' type='text' class='inpTbl' size='6' maxlength='5' onkeypress=\"_evaluar(event, '0123456789.');\" onkeyup=\"adm_retencionEditar(event, "+i+");\"></td>"+
                "<td width='100'>"+cl.innerHTML+"</td>"+
                "<td width='100'>"+clv.innerHTML+"</td>"+
                "<td align='center' width='70'><input type='hidden' id='idR"+i+"' value='-1'>"+
                "<div class='jm_icoCon' id='g"+i+"' style='background-position:-545px 0px;' onclick='adm_retencionGuardar("+i+");'>&nbsp;</div>"+
                "<div class='jm_icoCon' id='e"+i+"' style='visibility:hidden;background-position:-545px -18px;' onclick='adm_retencionEliminar("+i+");'>&nbsp;</div></td>";
    _('inRe').style.visibility = 'hidden';
    _('c'+i).focus();
    _('sll').scrollTop = 10000;
}
function adm_retencionEditar(e, i)
{
    if(e.type=='change'){
        _('g'+i).style.visibility = 'visible';
    }else{
       var c = e.which || event.keyCode;
        if((c >= 48 && c <= 57) || (c >= 65 && c <= 90) || (c >= 96 && c <= 105) || c==8 || c==32 || c==46 || c==110 || c==190){
            _('g'+i).style.visibility = 'visible';
        }
    }
}
function adm_retencionGuardar(i)
{
    var c = _('c'+i).value;
    var d = _('d'+i).value;
    var p = _('p'+i).value;
    if(c=='' || d=='' || p==''){
        alert('Por favor, todos los valores para la retención son obligatorios. Cuanto el impuesto es IVA por favor, ponga un  - .');
        return false;
    }
    _AJAX.solicitud("frmRetencionGuardar", 'id='+_('id').value+'&idR='+_('idR'+i).value+'&i='+_('i'+i).value+'&c='+c+'&d='+d._codificarURI()
        +'&idPcC='+_('idPcC'+i).value+'&idPcV='+_('idPcV'+i).value+'&p='+p+'&obj='+'idR'+i, true, 'body');
    _('g'+i).style.visibility = 'hidden';
    _('inRe').style.visibility = _('e'+i).style.visibility = 'visible';
}
function adm_retencionEliminar(i)
{
    if(confirm('¿Está seguro de querer eliminar de forma permanente la retención?')){
        _AJAX.solicitud("EliminarImpuesto", 'idTabla='+_('id').value+'&c='+_('idR'+i).value, false, null);
    }
}
/* SUCURSALES */
function adm_sucursales()
{
    encerar();
    _('d_filtro').innerHTML = 'Sucursales: <input type="text" id="t_b" onkeyup="adm_buscarSucursal();">';
    btn.icono(-360, 2, 34, 34, 'Nueva Sucursal', "adm_sucursalEditar('-1');", 'd_herra');
    btn.icono(-391, 3, 34, 34, 'Eliminar', "_objT.eliminar();", 'd_herra');
    adm_buscarSucursal();
    setAnchoPanel(2, 540);
}
function adm_sucursalEditar(k)
{
    _('d_ayuda').innerHTML = 'Por favor, no utilize caracteres especiales como: t&iacute;ldes, &ntilde; o s&iacute;mbolos especiales (&, $, etc).<br />'+
                             msg()+
                            '<br />En el campo [Direcciones IP\'s de los computadores que se encuentran en la sucursal], el formato de la direcci&oacute;n IP es 000.000.000.000 por ej: 192.168.1.1 '+
                            '<br />Para especificar mas de una direcci&oacute;n IP separe con &nbsp;  -  &nbsp; cada direcci&oacute;n.';
    new Tab('d_21', 530, 385);
    _AJAX.solicitud('frmSucursal', 'id='+k, false, 'd_21');
}
function adm_sucursalGuardar(f)
{
    if(f.liq_num_serie.value!='' || f.liq_sec_desde.value!='' || f.liq_sec_hasta.value!='' || f.liq_cad_libretin.value!='' || f.liq_autorizacion.value!=''){
        if(f.sucursal.value=='' || f.ubicacion.value=='' || f.liq_num_serie.value=='' || f.liq_sec_desde.value=='' || f.liq_sec_hasta.value==''
            || f.liq_cad_libretin.value=='' || f.liq_autorizacion.value==''  || f.ip_inicio.value==''){
            _MS('Si va a ingresar información para las liquidaciones de compras y servicios, entonces, todos los campos para las retenciones son obligatorios..');
            return false;
        }
        if(parseInt(f.liq_sec_desde.value) >= parseInt(f.liq_sec_hasta.value)){
            _MS('El número de secuencial hasta debe ser mayor que el número de secuencial desde para las liquidaciones.');
            return false;
        }
    }
    if(f.ret_num_serie.value!='' || f.ret_sec_desde.value!='' || f.ret_sec_hasta.value!='' || f.ret_cad_libretin.value!='' || f.ret_autorizacion.value!=''){
        if(f.ret_num_serie.value=='' || f.ret_sec_desde.value=='' || f.ret_sec_hasta.value=='' || f.ret_cad_libretin.value=='' || f.ret_autorizacion.value==''){
            _MS('Si va a ingresar información para las retenciones, entonces, todos los campos para las retenciones son obligatorios.');
            return false;
        }
        if(parseInt(f.ret_sec_desde.value) >= parseInt(f.ret_sec_hasta.value)){
            _MS('El número de secuencial hasta debe ser mayor que el número de secuencial desde para las retenciones.');
            return false;
        }
    }
    return _AJAX.enviarForm(f);
}
function adm_buscarSucursal()
{
    var w='';
    var a=_('t_b').value;
    if(a!=''){
        w = "where lower(sucursal) like '%"+a.toLowerCase()+"%'";
    }
    _WR = encodeURI(w);
    new Tabla('d_11', 'jmTbl', '', 'vta_sucursal', 'SUCURSAL,UBICACION,COMPUTADORES,ESTADO', 'id_sucursal,sucursal,ubicacion,ips_computador,txt_estado', '0,100,100,200,80', _altEdi, 'adm_sucursalEditar(^);', _WR);
}
function adm_PuntoEmisionEditar(k)
{
    _('d_ayuda').innerHTML = msg();
    Ventana.crear('cmp', 'PUNTO DE EMISION', "ancho=600,alto=320,modal=true,cerrar=true", '');
    _AJAX.solicitud('frmPuntoEmision', 'id='+k+'&idSuc='+_('id').value, false, 'cmp_html');
}
function adm_puntoEmisionGuardar(f)
{
    if(f.punto_emision.value==''){
        _MS('Los campos marcados con un  *  son obligatorios.');
        return false;
    }
    if(f.fac_num_serie.value!='' || f.fac_sec_desde.value!='' || f.fac_sec_hasta.value!='' || f.fac_cad_facturero.value!='' || f.fac_autorizacion.value!=''){
        if(f.fac_num_serie.value=='' || f.fac_sec_desde.value=='' || f.fac_sec_hasta.value=='' || f.fac_cad_facturero.value=='' || f.fac_autorizacion.value==''){
            _MS('Los campos marcados con un  *  son obligatorios.');
            return false;
        }
        if(parseInt(f.fac_sec_desde.value) >= parseInt(f.fac_sec_hasta.value)){
            _MS('El número de secuencial hasta debe ser mayor que el número de secuencial desde.');
            return false;
        }
    }
    return _AJAX.enviarForm(f);
}
/*Horarios*/
function adm_horarios()
{
    encerar();
    if(_MENUS_._enArreglo("CREAR HORARIOS")>=0){
        btn.icono(-360, 2, 34, 34, 'Nuevo Horario', "adm_horarioEditar('-1');", 'd_herra');
    }
    /*if(_MENUS_._enArreglo("ELIMINAR HORARIOS")>=0){
        btn.icono(-391, 3, 34, 34, 'Eliminar', "_objT.eliminar();", 'd_herra');
    }*/
    btn.icono(-455, -114, 34, 34, 'Imprimir', "nom_impHorario();", 'd_herra');
    _AJAX.solicitud('frmFiltro', 'op=32&fn=nom_buscarHorario();', false, 'd_filtro');
    setAnchoPanel(2, 540);
}
function nom_impHorario()
{
    var h = "<form onsubmit='return reporte(this)' autocomplete='off'>" + setRadio('z', '{tbl:[{0:"Horario",1:"Horarios en General"},{0:"HorarioEmpleado",1:"Horarios con Empleado"}]}') + setPieReporte() +"</form>";
    Ventana.crear('vta_im', 'Impresi&oacute;n de Areas', "ancho=280,alto=160,modal=true,cerrar=true", h);
}

function nom_buscarHorario()
{
    var w = 'where';
    if(_('idSuc').value!='-0'){
        w += w=='where' ? " id_sucursal='"+_('idSuc').value+"'" : " and id_sucursal='"+_('idSuc').value+"'";
    }

    if(w == 'where'){
        w = '';
    }
     /*var w = 'where id_sucursal='+_('idSuc').value;*/
    _WR = encodeURI(w); 
    _('idSuc').value;
    new Tabla('d_11', 'jmTbl', '', 'vta_horario', 'HORARIO,MODALIDAD,SUCURSAL,ESTADO', 'id_horario,nombre,modal,sucursal,estado_horario', '0,150,140,120,150', _altEdi, 'adm_horarioEditar(^);', _WR);
}
function adm_horarioEditar(k)
{
    _('d_ayuda').innerHTML = 'Por favor, no utilize caracteres especiales como: t&iacute;ldes, &ntilde; o s&iacute;mbolos especiales (&, $, etc).<br />'+msg();
    new Tab('d_21', 530, 385);
    _AJAX.solicitud('frmHorarios', 'id='+k, false, 'd_21');
    axPg=0;
    axNpg = 0;
}
function adm_setHorario(id)
{
    _('id_detalle').value=id;
    var dia=_('adm_idDet'+id).value;
    
    var hem=_('adm_hem'+id).value;  var hem1=_('adm_hem'+id).value;
    hem=hem.substr(0,2);                 hem1=hem1.substr(3,2);
    var hsm=_('adm_hsm'+id).value;  var hsm1=_('adm_hsm'+id).value;
    hsm=hsm.substr(0,2);                 hsm1=hsm1.substr(3,2);
    
    var het=_('adm_het'+id).value;  var het1=_('adm_het'+id).value;
    het=het.substr(0,2);                 het1=het1.substr(3,2);
    var hst=_('adm_hst'+id).value;  var hst1=_('adm_hst'+id).value;
    hst=hst.substr(0,2);                 hst1=hst1.substr(3,2);
    
    var hen=_('adm_hen'+id).value;  var hen1=_('adm_hen'+id).value;
    hen=hen.substr(0,2);                 hen1=hen1.substr(3,2);
    var hsn=_('adm_hsn'+id).value;  var hsn1=_('adm_hsn'+id).value;
    hsn=hsn.substr(0,2);                 hsn1=hsn1.substr(3,2);
    
    
    _('hem').value=hem;    _('hem1').value=hem1;
    _('hsm').value=hsm;    _('hsm1').value=hsm1;
    
    _('het').value=het;    _('het1').value=het1;
    _('hst').value=hst;    _('hst1').value=hst1;
    
    _('hen').value=hen;    _('hen1').value=hen1;
    _('hsn').value=hsn;    _('hsn1').value=hsn1;
    
    var elementos = document.getElementsByName("dia");
    for (var x=0;x<elementos.length;x++)
    {
        if(x==(dia-1)){
            elementos[x].checked=true;
        }
        else{
            elementos[x].checked=false;
        }
    } 
    
    /*var elementos = document.getElementsByName("dia");
    var dias = "";
    for (x=0;x<elementos.length;x++)
    {
        if(elementos[x].checked)
        {
            dias =  dias + elementos[x].value + ",";
        }
    }    
    dias=dias.substr(0,dias.length-1);*/
    //_AJAX.solicitud('frmEmpleadoGuardarCarnet', 'id='+k+'&texto='+texto+'&empleado='+empleado, false, 'd_21');
}
function adm_guardarHorario()
{
    var elementos = document.getElementsByName("dia");
    var id_horario=_('id_horario').value;
    var dias = "";
    var modalidad="";
    var estado="";
    var cont=0;
    if(_('n_horario').value==""){
        alert("El campo nombre esta vacio");
        cont++;
    }
    for (var x=0;x<elementos.length;x++)
    {
        if(elementos[x].checked) {
            dias =  dias + elementos[x].value + ",";
        }
    }
    if(dias.length==0&&cont>0){
        alert("Marque por lo menos un dia por favor");
        cont++;
    }
    
    if(_("modalidad0").checked)
    {
        modalidad=_("modalidad0").value;
    }
    if(_("modalidad1").checked)
    {
        modalidad=_("modalidad1").value;
    }
    
     if(_("estado0").checked)
    {
        estado=_("estado0").value;
    }
    if(_("estado1").checked)
    {
        estado=_("estado1").value;
    }
    dias=dias.substr(0,dias.length-1);
    if(cont==0){
        _AJAX.solicitud('frmHorarioGuardar', 'idHorario='+id_horario+'&idDetalle='+_('id_detalle').value+'&sucursal='+_('id_sucursal').value+'&nombre_horario='+_('n_horario').value
        +'&horario_antes='+_('n_horario_antes').value+'&modalidad='+modalidad+'&em='+(_('hem').value+":"+_('hem1').value)+'&sm='+(_('hsm').value+":"+_('hsm1').value)+'&et='+(_('het').value+":"+_('het1').value)
        +'&st='+(_('hst').value+":"+_('hst1').value)+'&en='+(_('hen').value+":"+_('hen1').value)+'&sn='+(_('hsn').value+":"+_('hsn1').value)+'&dias='+dias+'&estado='+estado, false, 'd_21');
    }    
}
function adm_setHorarioEliminar(k)
{
    var horario=_('n_horario').value;
    var dia=_('adm_idDet'+k).value;
    confirmar=confirm("¿Seguro que desea Eliminar el Dia del Horario?"); 
    if (confirmar){
        _AJAX.solicitud('frmHorarioEliminar', 'id='+k+'&horario='+horario+'&dia='+dia, false, 'd_21');
    }    
}
/* BODEGAS */
function adm_bodegas()
{
    encerar();
    //_('d_filtro').innerHTML = 'Bodegas: <input type="text" id="t_b" onkeyup="adm_buscarBodega();">';
    btn.icono(-360, 2, 34, 34, 'Nueva Bodega', "adm_bodegaEditar('-1');", 'd_herra');
    btn.icono(-391, 3, 34, 34, 'Eliminar', "_objT.eliminar();", 'd_herra');
    btn.icono(-455, -114, 34, 34, 'Imprimir', "adm_impCierrePeriodo();", 'd_herra');

    _AJAX.solicitud('frmFiltro', 'op=13&fn=adm_buscarBodega()', false, 'd_filtro');
    setAnchoPanel(2, 540);
}
function adm_bodegaEditar(k)
{
    _('d_ayuda').innerHTML = 'Por favor, no utilize caracteres especiales como: t&iacute;ldes, &ntilde; o s&iacute;mbolos especiales (&, $, etc).<br />'+
                             msg();
    new Tab('d_21', 530, 385);
    _AJAX.solicitud('frmBodega', 'id='+k, false, 'd_21');
    axPg=0;
    axNpg = 0;
}
function adm_bodegaGuardar(f)
{
    if(f.bodega.value=='' || f.id_responsable.value=='' || f.ubicacion.value==''){
        _MS('Todos los campos son obligatorios.');
        return false;
    }
    return _AJAX.enviarForm(f);
}
function adm_buscarBodega()
{
    var w='where id_sucursal='+_('idSuc').value;
    var a=_('t_b').value;
    if(a!=''){
        w += " and (lower(bodega) like '%"+a.toLowerCase()+"%' or lower(responsable) like '%"+a.toLowerCase()+"%')";
    }
    _WR = encodeURI(w); 
    new Tabla('d_11', 'jmTbl', '', 'vta_bodega', 'BODEGA,UBICACION,RESPONSABLE,ESTADO', 'id_bodega,bodega,ubicacion,responsable,txt_estado', '0,100,100,200,80', _altEdi, 'adm_bodegaEditar(^);', _WR);
}
function adm_setNavBodega(k)
{
    btn.icono(-455, -114, 34, 34, 'Imprimir', "adm_bodegaArticulosImp("+k+");", 'axHerraBod');
    btn.separador('axHerraBod');
    btn.icono(-443, -171, 34, 34, 'Anterior', "if(parseInt(axPg)>0){axPg--;_('axd_nav_pag').value=axPg;adm_getArticulos();}", 'axHerraBod');
    _('axHerraBod').innerHTML+='<div class="d_nav"> &nbsp; <input id="axd_nav_pag" value="0" onfocus="this.select();" onkeypress="_numero(event);if(_getKeyCode(event)==13){axPg=parseInt(this.value);adm_getArticulos();}" onkeydown="_NoE(event)" /> / <span id="axd_nav_pags" ></span> &nbsp; </div>';
    btn.icono(-477, -171, 34, 34, 'Siguiente', "if( parseInt(axPg) < parseInt(axNpg) ){axPg++;_('axd_nav_pag').value=axPg;adm_getArticulos();}", 'axHerraBod');
}
function adm_getArticulos()
{
    _AJAX.solicitud('tblArticulosBodega', 'idB='+_('id').value+'&p='+axPg, false, 'sl2');
}
function adm_estanteriaEditar(k)
{
    _('d_ayuda').innerHTML = msg();
    Ventana.crear('cmp', 'ESTANTERIA', "ancho=400,alto=220,modal=true,cerrar=true", '');
    _AJAX.solicitud('frmEstanteria', 'id='+k+'&idB='+_('id').value, false, 'cmp_html');
}
function adm_estanteriaGuardar(f)
{
    if(f.estanteria.value=='' || f.fila.value=='' || f.columna.value=='' || f.num_nivel.value==''){
        _MS('Los campos marcados con un  *  son obligatorios.');
        return false;
    }
    return _AJAX.enviarForm(f);
}
function adm_bodegaArticulosImp(k)
{
    imprimir('pdfBodegaArticulos?id='+k);
}

function adm_impCierrePeriodo()
{
    Ventana.crear('vta_im', 'Cierre de Caja', "ancho=600,alto=280,modal=true,cerrar=true");
    _AJAX.solicitud('frmFiltro', 'op=34', false, 'vta_im_html');
}
function cargaBodega()
{    
    _AJAX.solicitud('frmFiltro', 'op=34', false, 'vta_im_html');
}
/* UBICACIONES */
function adm_ubicaciones()
{
    encerar();
    _('d_filtro').innerHTML = 'Ubicaci&oacute;n: <input type="text" id="t_b" onkeyup="adm_ubicacionBuscar();">';
    //if(_MENUS_._enArreglo("planCuentaNueva")>=0){
        btn.icono(-360, 2, 34, 34, 'Nueva ubicaci&oacute;n', "adm_ubicacionEditar(-1);", 'd_herra');
    //}
    //if(_MENUS_._enArreglo("planCuentaEliminar")>=0){
        //btn.icono(-391, 3, 34, 34, 'Eliminar', "_objT.eliminar();", 'd_herra');
    //}
    adm_ubicacionBuscar();
    setAnchoPanel(2, 420);
}
function adm_ubicacionEditar(k)
{
    _('d_ayuda').innerHTML = 'Por favor, no utilize caracteres especiales como: t&iacute;ldes, &ntilde; o s&iacute;mbolos especiales (&, $, etc).<br />'+ msg();
    _AJAX.solicitud('frmUbicacion', 'id='+k, false, 'd_21');
}
function adm_ubicacionGuardar(f)
{
    if(f.ubicacion.value==''){
        _MS('Los campos marcados con un  *  son obligatorios.');
        return false;
    }
    return _AJAX.enviarForm(f);
}
function adm_ubicacionBuscar()
{
    var w='where id_padre>0';
    var a=_('t_b').value;
    if(a!=''){
        w += " and lower(ubicacion) like '"+a.toLowerCase()+"%'";
    }
    _WR = encodeURI(w + ' order by id_ubicacion'); 
    new Tabla('d_11', 'jmTbl', '', 'tbl_ubicacion', 'UBICACION', 'id_ubicacion,ubicacion', '0,200', _altEdi, 'adm_ubicacionEditar(^);', _WR);
}
/* IVA */
function adm_ivas()
{
    encerar();
    _('d_filtro').innerHTML = 'Concepto: <input type="text" id="t_b" onkeyup="adm_ivaBuscar();">';
    btn.icono(-360, 2, 34, 34, 'Nuevo grupo de depreciaci&oacute;n', "adm_ivaEditar('-1');", 'd_herra');
    btn.icono(-391, 3, 34, 34, 'Eliminar', "_objT.eliminar('', 'frmIvaEliminar');", 'd_herra');
    adm_ivaBuscar();
    setAnchoPanel(2, 450);
}
function adm_ivaEditar(k)
{
    _('d_ayuda').innerHTML = msg();
    _AJAX.solicitud('frmIva', 'id='+k, false, 'd_21');
}
function adm_ivaGuardar(f)
{
    if(f.codigo.value=='' || f.concepto.value=='' || f.porcentaje.value==''){
        _MS('Los campos marcados con un  *  son obligatorios.');
        return false;
    }
    return _AJAX.enviarForm(f);
}
function adm_ivaBuscar()
{
    var w='';
    var a=_('t_b').value;
    if(a!=''){
        w = "where lower(concepto) like '%"+a.toLowerCase()+"%'";
    }
    _WR = encodeURI(w) + ' order by codigo';
    new Tabla('d_11', 'jmTbl', '', 'tbl_iva', 'CODIGO,CONCEPTO,%', 'id_iva, codigo, concepto, porcentaje', '0,80,160,50', _altEdi, 'adm_ivaEditar(^);', _WR);
}
/* TABLA DE DEPRESIACIONES */
function adm_depreciaciones()
{
    encerar();
    _('d_filtro').innerHTML = 'Depreciaci&oacute;n: <input type="text" id="t_b" onkeyup="adm_buscarDepreciacion();">';
    btn.icono(-360, 2, 34, 34, 'Nuevo grupo de depreciaci&oacute;n', "adm_depreciacionEditar('-1');", 'd_herra');
    btn.icono(-391, 3, 34, 34, 'Eliminar', "_objT.eliminar();", 'd_herra');
    adm_buscarDepreciacion();
    setAnchoPanel(2, 450);
}
function adm_depreciacionEditar(k)
{
    _('d_ayuda').innerHTML = 'Por favor, no utilize caracteres especiales como: t&iacute;ldes, &ntilde; o s&iacute;mbolos especiales (&, $, etc).<br />'+msg();
    _AJAX.solicitud('frmTablaDepreciacion', 'id='+k, false, 'd_21');
}
function adm_setAniosDepreciacion()
{
    switch(parseInt(_('porcentaje').value)){
        case 5:
            _('num_anios').value = 20;
        break;
        case 10:
            _('num_anios').value = 10;
        break;
        case 20:
            _('num_anios').value = 5;
        break;
        case 33:
            _('num_anios').value = 3;
        break;
        default:_('num_anios').value = 0;
    }
}
function adm_depreciacionGuardar(f)
{
    if(f.depreciacion.value=='' || f.porcentaje.value=='' || f.num_anios.value==''){
        _MS('Los campos marcados con un  *  son obligatorios.');
        return false;
    }
    return _AJAX.enviarForm(f);
}
function adm_buscarDepreciacion()
{
    var w='';
    var a=_('t_b').value;
    if(a!=''){
        w = "where lower(depreciacion) like '%"+a.toLowerCase()+"%'";
    }
    _WR = encodeURI(w);
    new Tabla('d_11', 'jmTbl', '', 'vta_tabla_depreciacion', 'GRUPO,Nro. A&Ntilde;OS,% DEP.', 'id_tabla_depreciacion,depreciacion,num_anios,porcentaje', '0,200,80,80', _altEdi, 'adm_depreciacionEditar(^);', _WR);
}
/* PLAN DE CUENTAS */
function cnt_plan_cuentas()
{
    encerar();
    _('d_filtro').innerHTML = 'C&oacute;digo, nombre: <input type="text" id="t_b" onkeyup="cnt_buscarPlanCuenta();">';
    if(_MENUS_._enArreglo("planCuentaNueva")>=0){
        btn.icono(-360, 2, 34, 34, 'Nueva Tabla del Plan de Cuentas', "cnt_planCuentaEditar(-1);", 'd_herra');
    }
    if(_MENUS_._enArreglo("planCuentaEliminar")>=0){
        btn.icono(-391, 3, 34, 34, 'Eliminar', "_objT.eliminar();", 'd_herra');
    }
    if(_MENUS_._enArreglo("planCuentaImprimir")>=0){
        btn.icono(-455, -114, 34, 34, 'Imprimir', "imprimir('pdfPlanCuentas')", 'd_herra');
    }
    cnt_buscarPlanCuenta();
    setAnchoPanel(2, 420);
}
function cnt_planCuentaEditar(k)
{
    _('d_ayuda').innerHTML = 'Por favor, no utilize caracteres especiales como: t&iacute;ldes, &ntilde; o s&iacute;mbolos especiales (&, $, etc).<br />'+ msg();
    _AJAX.solicitud('frmPlanCuenta', 'id='+k, false, 'd_21');
}
function cnt_planCuentaGuardar(f)
{
    if(f.codigo_cuenta.value=='' || f.nombre_cuenta.value==''){
        _MS('Los campos marcados con un  *  son obligatorios.');
        return false;
    }
    return _AJAX.enviarForm(f);
}
function cnt_buscarPlanCuenta()
{
    var w='where id_plan_cuenta>1';
    var a=_('t_b').value;
    if(a!=''){
        w += " and (codigo_cuenta like '"+a.toLowerCase()+"%' or lower(nombre_cuenta) like '%"+a.toLowerCase()+"%')";
    }
    _WR = encodeURI(w); 
    new Tabla('d_11', 'jmTbl', '', 'vta_plan_cuenta', 'CODIGO,CUENTA,TIPO', 'id_plan_cuenta,codigo_cuenta,nombre_cuenta,txt_tipo_cuenta', '0,150,300,80', _altEdi, 'cnt_planCuentaEditar(^);', _WR);
}
function cnt_setCodigo()
{
    if(_('codigo_cuenta').value==''){
        var tp = _('id_padre');
        var c = tp.options[tp.selectedIndex].text.split(' ');
        _('codigo_cuenta').value=c[0];
    }
    _('codigo_cuenta').focus();
}
/* CUENTAS BANCARIAS */
function cnt_bancos()
{
    encerar();
    _('d_filtro').innerHTML = 'Banco, cuenta: <input type="text" id="t_b" onkeyup="cnt_bancoBuscar();">';
    if(_MENUS_._enArreglo("bancoNuevo")>=0){
        btn.icono(-360, 2, 34, 34, 'Nueva Cuenta Bancaria', "cnt_bancoEditar(-1);", 'd_herra');
    }
    if(_MENUS_._enArreglo("bancoEliminar")>=0){
        btn.icono(-391, 3, 34, 34, 'Eliminar', "_objT.eliminar();", 'd_herra');
    }
    cnt_bancoBuscar();
    setAnchoPanel(2, 440);
}
function cnt_bancoEditar(k)
{
    _('d_ayuda').innerHTML = msg();
    _AJAX.solicitud('frmBanco', 'id='+k+'&altB='+_altBody, false, 'd_21');
}
function cnt_bancoGuardar(f)
{
    if(f.banco.value=='' || f.num_cuenta.value==''){
        _MS('Los campos marcados con un  *  son obligatorios.');
        return false;
    }
    return _AJAX.enviarForm(f);
}
function cnt_bancoBuscar()
{
    var w='';
    var a=_('t_b').value.toLowerCase();
    if(a!=''){
        w += "lower(banco) like '%"+a+"%' or num_cuenta like '"+a+"%'";
    }
    _WR = w;
    new Tabla('d_11', 'jmTbl', '', 'vta_banco', 'BANCO,CUENTA,Nro. CHEQUE', 'id_banco,banco,num_cuenta,num_cheque', '0,200,90,80', _altEdi, 'cnt_bancoEditar(^);', _WR);
}
/* LIBRO DIARIO */
function cnt_diario()
{
    encerar();
    _('d_ayuda').innerHTML = 'Puede imprimir los datos del libro diario haciendo clic en el &iacute;cono de la impresora de la barra de herramientas.';
    if(_MENUS_._enArreglo("contabilizarVentas")>=0){
        btn.icono(-375, -229, 34, 34, 'Contabilizar movimientos de ventas', "cnt_contabilizarVentas();", 'd_herra');
    }
    if(_MENUS_._enArreglo("diarioImprimir")>=0){
        btn.separador('d_herra');
        btn.icono(-455, -114, 34, 34, 'Imprimir', "cnt_impDiario();", 'd_herra');
    }
    _AJAX.solicitud('frmFiltro', 'op=1&fn=cnt_filtrarDiario()', false, 'd_filtro');
    setAnchoPanel(1, 690);
    setAnchoPanel(2, 20);
    _objT = _AUX = 0;
}
function cnt_filtrarDiario()
{
    _AJAX.solicitud('tblLibroDiario', 'fi='+_('fi').value+'&ff='+_('ff').value+'&aB='+_altBody+'&idSuc='+_('idSuc').value+'&p='+_objT, false, 'd_11');
}
function cnt_setNavegacion(np)
{
    _AUX = np;
    if(np>0 && _('d_nav_pag')==null){
        btn.separador('d_herra');
        btn.icono(-443, -171, 34, 34, 'Anterior', "if(parseInt(_objT)>0){_objT--;_('d_nav_pag').value=_objT;cnt_filtrarDiario();}", 'd_herra');
        _('d_herra').innerHTML+='<div class="d_nav"> &nbsp; <input id="d_nav_pag" value="0" onfocus="this.select();" onkeypress="_numero(event);if(_getKeyCode(event)==13){_objT=parseInt(this.value);cnt_filtrarDiario();}" /> / <span id="d_nav_pags">'+( parseInt(np) )+'</span> &nbsp; </div>';
        btn.icono(-477, -171, 34, 34, 'Siguiente', "if(parseInt(_objT)<_AUX){_objT++;_('d_nav_pag').value=_objT;cnt_filtrarDiario();}", 'd_herra');
    }else if(np>=0 && _('d_nav_pag')!=null){
               _('d_nav_pags').innerHTML = np;
               if(_objT>np){
                   _objT = _('d_nav_pag').value = 0;
               }
    }
}
function cnt_contabilizarVentas()
{
    _AJAX.solicitud('ContabilizarVentas', '', true, 'body');
}
function cnt_impDiario()
{
    imprimir('ContabilizarVentas?fi='+_('fi').value+'&ff='+_('ff').value+'&idSuc='+_('idSuc').value);
}
/* LIBRO MAYOR */
function cnt_mayor()
{
    encerar();
    _('d_ayuda').innerHTML = "<input type='hidden' id='idPC' value='' />Puede imprimir los datos del libro mayor haciendo clic en el &iacute;cono de la impresora de la barra de herramientas.<br />"+
        "Luego de seleccionar los par&aacute;metros en la barra de filtro haga clic en la cuenta del plan de cuentas para actualizar.";
    if(_MENUS_._enArreglo("mayorImprimir")>=0){
        btn.icono(-455, -114, 34, 34, 'Imprimir', "cnt_impMayor();", 'd_herra');
    }
    _AJAX.solicitud('frmFiltro', 'op=1&fn=', false, 'd_filtro');
    new Tabla('d_11', 'jmTbl', '', 'vta_plan_cuenta', 'CODIGO,CUENTA', 'id_plan_cuenta,codigo_cuenta,nombre_cuenta', '0,150,300', _altEdi, 'cnt_filtrarMayor(^);', 'where id_plan_cuenta>1');
    setAnchoPanel(2, 740);
    
    _('d_21').innerHTML = '<div id="axHerraLibMay" style="height:40px"></div><div id="axLibMay"></div>';

    btn.icono(-443, -171, 34, 34, 'Anterior', "if(parseInt(axPg)>0){axPg--;_('axd_nav_pag').value=axPg;cnt_getMayor();}", 'axHerraLibMay');
    _('axHerraLibMay').innerHTML+='<div class="d_nav"> &nbsp; <input id="axd_nav_pag" value="0" onfocus="this.select();" onkeypress="_numero(event);if(_getKeyCode(event)==13){axPg=parseInt(this.value);cnt_getMayor();}" onkeydown="_NoE(event)" /> / <span id="axd_nav_pags" ></span> &nbsp; </div>';
    btn.icono(-477, -171, 34, 34, 'Siguiente', "if( parseInt(axPg) < parseInt(axNpg) ){axPg++;_('axd_nav_pag').value=axPg;cnt_getMayor();}", 'axHerraLibMay');
    axPg=0;
    axNpg = 0;
}
function cnt_filtrarMayor(id)
{
    if(id!=undefined){
        _('idPC').value=id;
        _AJAX.solicitud('tblLibroMayor', 'idPC='+id+'&aB='+_altBody, false, 'axLibMay');
    }
}
function cnt_getMayor()
{
    _AJAX.solicitud('tblPagLibroMayor', 'idPC='+_('idPlanCuenta').value+'&fi='+_('fi').value+'&ff='+_('ff').value+'&idSuc='+_('idSuc').value+'&p='+axPg, false, 'axDatMay');
}
function cnt_impMayor()
{
    if(_('idPC').value==''){
        alert('Debe seleccionar una cuenta de movimiento del plan de cuentas para poder imprimir');
        return false;
    }
    imprimir('pdfLibroMayor?idPC='+_('idPC').value+'&fi='+_('fi').value+'&ff='+_('ff').value+'&idSuc='+_('idSuc').value);
}

/* FUNCIONES COMPROBANTES */
function cnt_addCuenta(fl,ed)
{
    var f = fl || 't';
    var e = ed || 'e';
    var i=0;
    while(_('r'+i)!=null){
        i++;
    } 
    var tr = document.createElement("tr");
    tr.id='r'+i;
    tr.className='jm_filaPar';
    _('tblD').appendChild(tr);
    var cl = _('ax').cloneNode(true);
    cl.innerHTML = cl.innerHTML.replace(/_Xx_/g, 'PC'+i);
    var w = _('typ').value=='d' ? 350 : 550;
    var o = (_('typ').value=='e')? 'th' : 'td';
    if(e == 'e'){
        tr.innerHTML = "<td id='f0"+i+"' width='"+w+"'>"+cl.innerHTML+"</td>"+
                "<td align='right' width='90'><input id='d"+i+"' name='d"+i+"' type='text' class='inpTbl' size='9' onkeypress=\"_evaluar(event, '0123456789.');_('h"+i+"').value='';\" onfocus='this.select();' onkeyup=\"cnt_sumTotales();cnt_setSon('"+o+"');\" /></td>"+
                "<td align='right' width='90'><input id='h"+i+"' name='h"+i+"' type='text' class='inpTbl' size='9' onkeypress=\"_evaluar(event, '0123456789.');_('d"+i+"').value='';\" onfocus='this.select();' onkeyup=\"cnt_sumTotales();cnt_setSon('"+o+"');\" /></td>"+
                "<td align='center' width='20'><div class='jm_icoCon' style='background-position:-545px -18px;' title='Eliminar' onclick=\"_R('r"+i+"');cnt_sumTotales();cnt_setSon('"+o+"');\">&nbsp;</div></td>";
    }else{
        tr.innerHTML = "<td id='f0"+i+"' width='"+w+"'>"+cl.innerHTML+"</td>"+
                "<td align='right' width='90'><input id='d"+i+"' name='d"+i+"' type='text' class='inpTbl' size='9' readonly /></td>"+
                "<td align='right' width='90'><input id='h"+i+"' name='h"+i+"' type='text' class='inpTbl' size='9' readonly /></td>"+
                "<td align='center' width='20'>&nbsp;</div></td>";
    }
    if(f=='t'){
        var t = cnt_getTotales();
        var di = parseFloat(t[0]) - parseFloat(t[1]);
        var o1 = (di>0)? 'h'+i : 'd'+i;
        _(o1).value = _RD(Math.abs(di));
        cnt_sumTotales();
    }
    _('di').innerHTML = 'Debe - Haber = 0';
    if(_('sll0')!=null){
        _('sll0').scrollTop = 10000;
    }
    return i;
}
function cnt_sumTotales()
{
    var t = cnt_getTotales();
    _('td').innerHTML = _RD(t[0]);
    _('th').innerHTML = _RD(t[1]);
    _('di').innerHTML = 'Debe - Haber = '+ _RD(parseFloat(t[0]) - parseFloat(t[1]));
}
function cnt_getTotales()
{
    var t = Array(0,0);
    for(var i=0; i<200; i++){
        if(_('d'+i)!=null){
            t[0] += (_('d'+i).value!='') ? _RD(parseFloat(_('d'+i).value)) : 0;
            t[1] += (_('h'+i).value!='') ? _RD(parseFloat(_('h'+i).value)) : 0;
        }
    }
    return t;
}
function cnt_verificarCuentas(c)
{
    var v = c.split(',');
    for(i=0; i<v.length-1; i++){
        for(j=i+1; j<v.length; j++){
            if(v[i]==v[j]){
                return false;
            }
        }
    }
    return true;
}
function cnt_setCuenta()
{
    var x = '';
    if(_('e0').checked){
        _('pCh').style.display='none';
        x='caja';
        if(_('idNC')!=null){
            _('idNC').style.visibility='hidden';
        }
    }else if(_('e1').checked){
            _('pCh').style.display='block';
            x='banco';
            if(_('idNC')!=null){
                _('idNC').style.visibility='hidden';
            }
    }else if(_('e2').checked){
            _('pCh').style.display='none';        
            x='devoluci';
            if(_('idNC')!=null){
                _('idNC').style.visibility='visible';
            }
    }
    var i=0;
    while(i<_('PC0').options.length && _('PC0').options[i].text.toLowerCase().indexOf(x)==-1){
        i++;
    }
    _('PC0').value = _('PC0').options[i].value;
}
function cnt_setBanco(e)
{
    var tp = e || 0; 
    if(tp==1){
        if(_('foPa').value=='e'){
            var cl = _('ax').cloneNode(true);
            cl.innerHTML = cl.innerHTML.replace(/_Xx_/g, 'PC0');
           _('f00').innerHTML = cl.innerHTML;
        }else{
           _('f00').innerHTML = _('axb').innerHTML.replace(/_Xx_/g, 'PC0');
           if(_('foPa').value=='t' || _('foPa').value=='j'){
                if(_('f01')==null){
                    cnt_addCuenta();
                }
                cl = _('ax').cloneNode(true);
                cl.innerHTML = cl.innerHTML.replace(/_Xx_/g, 'PC1');
                _('f01').innerHTML = cl.innerHTML;
                _('PC1').value = _('gas_ban').value;
           }
        }
    }else{
        _('bc').value=_('ch').value=_('ct').value=_('lu').value='';
        var i=50;
        while(i>=0 && _('PC'+i)==null){
            i--;
        }
        if(_('e1').checked){
            var jS = eval('(' + _('axBc').innerHTML + ')');
            var p = _enMatrizJSON(jS, _('bc1').value, 0);
            _('PC'+i).value = jS.tbl[p][1];
            _('bc').value=jS.tbl[p][2];
            _('ct').value=jS.tbl[p][3];
            _('ch').value=jS.tbl[p][4];
            _('lu').value=jS.tbl[p][5];
        }else{
            _('PC'+i).value = _('caj').value;
        }
    }
}
function cnt_setFormaPago(o, pf)
{
    var nd = pf || '';
    if(_('tab01')!=null || _('tab04')!=null){
        var ob = _('tab04')!=null ? 'tab04' : 'tab01';
        _(ob).style.visibility = _('foPa').value=='d' ? 'hidden' : 'visible';
    }
    switch(o.value){
        case 'c':
            _(nd+'pCh').style.display='block';
            _(nd+'pOtr').style.display='none';
            _(nd+'pTra').style.display='none';
        break;
        case 'p':
            _(nd+'pCh').style.display='none';
            _(nd+'pOtr').style.display='block';
            _(nd+'pTra').style.display='none';
        break;
        case 't':
        case 'j':
        case 'n':
            _(nd+'pCh').style.display='none';
            _(nd+'pOtr').style.display='block';
            _(nd+'pTra').style.display='block';
        break;
        default:
            _(nd+'pCh').style.display=_(nd+'pOtr').style.display=_(nd+'pTra').style.display='none';
        break;
    }
    if(_(nd+'pBan')!=null){
        _(nd+'pBan').style.display=(o.value=='c' || o.value=='p' || o.value=='h' || o.value=='t' || o.value=='j' || o.value=='n')?'block':'none';
    }
}
var Son = new Object();
Son = function(){
    this.flag;
    this.numero;
    this.num;
    this.num_letra;
    this.num_letram;
    this.num_letradm;
    this.num_letracm;
    this.num_letramm;
    this.num_letradmm;
};
Son.prototype = {
    unidad : function(numero){
            var n = parseInt(numero); 
            switch (n){
            case 9:
                    this.num = "nueve";
                    break;
            case 8:
                    this.num = "ocho";
                    break;
            case 7:
                    this.num = "siete";
                    break;
            case 6:
                    this.num = "seis";
                    break;
            case 5:
                    this.num = "cinco";
                    break;
            case 4:
                    this.num = "cuatro";
                    break;
            case 3:
                    this.num = "tres";
                    break;
            case 2:
                    this.num = "dos";
                    break;
            case 1:
                    this.num = (this.flag == 0) ? "un" : "uno";
                    break;
            default:
                    this.num = "";
            }
            return this.num;
    },
    decena : function(numero){
        if (numero >= 90 && numero <= 99){
            this.num_letra = "noventa ";
            if (numero > 90){
                this.num_letra += "y " + this.unidad(numero - 90);
            }
        }else if (numero >= 80 && numero <= 89){
                this.num_letra = "ochenta ";
                if (numero > 80){
                    this.num_letra += "y " + this.unidad(numero - 80);
                }
        }else if (numero >= 70 && numero <= 79){
                this.num_letra = "setenta ";
                if (numero > 70){
                    this.num_letra += "y " + this.unidad(numero - 70);
                }
        }else if (numero >= 60 && numero <= 69){
                this.num_letra = "sesenta ";
                if (numero > 60){
                    this.num_letra += "y " + this.unidad(numero - 60);
                }
        }else if (numero >= 50 && numero <= 59){
                this.num_letra = "cincuenta ";
                if (numero > 50){
                    this.num_letra += "y " + this.unidad(numero - 50);
                }
        }else if (numero >= 40 && numero <= 49){
                this.num_letra = "cuarenta ";
                if (numero > 40){
                    this.num_letra += "y " + this.unidad(numero - 40);
                }
        }else if (numero >= 30 && numero <= 39){
                this.num_letra = "treinta ";
                if (numero > 30){
                    this.num_letra += "y " + this.unidad(numero - 30);
                }
        }else if (numero >= 20 && numero <= 29){
                if (numero == 20){
                    this.num_letra = "veinte ";
                }else{
                    this.num_letra = "veinti" + this.unidad(numero - 20);
                }
        }else if (numero >= 10 && numero <= 19){
                var n = parseInt(numero);
                switch (n){
                    case 10:
                            this.num_letra = "diez ";
                            break;
                    case 11:
                            this.num_letra = "once ";
                            break;
                    case 12:
                            this.num_letra = "doce ";
                            break;
                    case 13:
                            this.num_letra = "trece ";
                            break;
                    case 14:
                            this.num_letra = "catorce ";
                            break;
                    case 15:
                            this.num_letra = "quince ";
                            break;
                    case 16:
                            this.num_letra = "dieciseis ";
                            break;
                    case 17:
                            this.num_letra = "diecisiete ";
                            break;
                    case 18:
                            this.num_letra = "dieciocho ";
                            break;
                    case 19:
                            this.num_letra = "diecinueve ";
                            break;
                }   
            }else{
                this.num_letra = this.unidad(numero);
            }
    return this.num_letra;
    },
    centena : function(numero){
        if (numero >= 100){
            if (numero >= 900 && numero <= 999){
                this.num_letra = "novecientos ";
                if (numero > 900){
                    this.num_letra += this.decena(numero - 900);
                }
            }else if (numero >= 800 && numero <= 899){
                    this.num_letra = "ochocientos ";
                    if (numero > 800){
                        this.num_letra += this.decena(numero - 800);
                    }
            }else if (numero >= 700 && numero <= 799){
                    this.num_letra = "setecientos ";
                    if (numero > 700){
                        this.num_letra += this.decena(numero - 700);
                    }
            }else if (numero >= 600 && numero <= 699){
                    this.num_letra = "seiscientos ";
                    if (numero > 600){
                        this.num_letra += this.decena(numero - 600);
                    }
            }else if (numero >= 500 && numero <= 599){
                    this.num_letra = "quinientos ";
                    if (numero > 500){
                        this.num_letra += this.decena(numero - 500);
                    }
            }else if (numero >= 400 && numero <= 499){
                    this.num_letra = "cuatrocientos ";
                    if (numero > 400){
                        this.num_letra += this.decena(numero - 400);
                    }
            }else if (numero >= 300 && numero <= 399){
                    this.num_letra = "trescientos ";
                    if (numero > 300){
                        this.num_letra += this.decena(numero - 300);
                    }
            }else if (numero >= 200 && numero <= 299){
                    this.num_letra = "doscientos ";
                    if (numero > 200){
                        this.num_letra += this.decena(numero - 200);
                    }
            }else if (numero >= 100 && numero <= 199){
                    if (numero == 100){
                        this.num_letra = "cien ";
                    }else{
                        this.num_letra = "ciento " + this.decena(numero - 100);
                    }
            }
        }else{
            this.num_letra = this.decena(numero);
        }
        return this.num_letra;  
    },  
    miles : function (numero){
        if (numero >= 1000 && numero <2000){
            this.num_letram = "mil " + this.centena(numero%1000);
        }
        if (numero >= 2000 && numero <10000){
            this.flag=1;
            this.num_letram = this.unidad(numero/1000) + " mil " + this.centena(numero%1000);
        }
        if (numero < 1000){
            this.num_letram = this.centena(numero);
        }    
        return this.num_letram;
    },      
    decmiles : function(numero){
        if (numero == 10000){
            this.num_letradm = "diez mil";
        }
        if (numero > 10000 && numero <20000){
            this.flag=1;
            this.num_letradm = this.decena(numero/1000) + "mil " + this.centena(numero%1000);       
        }
        if (numero >= 20000 && numero <100000){
            this.flag=1;
            this.num_letradm = this.decena(numero/1000) + " mil " + this.miles(numero%1000);        
        }
        if (numero < 10000){
            this.num_letradm = this.miles(numero);
        }
        return this.num_letradm;
    },      
    cienmiles : function(numero){
        if (numero == 100000){
            this.num_letracm = "cien mil";
        }
        if (numero >= 100000 && numero <1000000){
            this.flag=1;
            this.num_letracm = this.centena(numero/1000) + " mil " + this.centena(numero%1000);     
        }
        if (numero < 100000){
            this.num_letracm = this.decmiles(numero);
        }
        return this.num_letracm;
    },      
    millon : function(numero){
        if (numero >= 1000000 && numero <2000000){
            this.flag=1;
            this.num_letramm = "un millon " + this.cienmiles(numero%1000000);
        }
        if (numero >= 2000000 && numero <10000000){
            this.flag=1;
            this.num_letramm = this.unidad(numero/1000000) + " millones " + this.cienmiles(numero%1000000);
        }
        if (numero < 1000000){
            this.num_letramm = this.cienmiles(numero);
        }
        return this.num_letramm;
    },      
    decmillon : function(numero){
        if (numero == 10000000){
            this.num_letradmm = "diez millones";
        }
        if (numero > 10000000 && numero <20000000){
            this.flag=1;
            this.num_letradmm = this.decena(numero/1000000) + "millones " + this.cienmiles(numero%1000000);     
        }
        if (numero >= 20000000 && numero <100000000){
            this.flag=1;
            this.num_letradmm = this.decena(numero/1000000) + " milllones " + this.millon(numero%1000000);      
        }
        if (numero < 10000000){
            this.num_letradmm = this.millon(numero);
        }
        return this.num_letradmm;
    }       
};
function FloatToStr(n)
{
    var en = '';
    var de = '';
    var objSon = new Son();
    n = n  + '';  /* para convertir a cadena */
    if(n.indexOf(".") != -1){
        var v = n.split(".");
        en = objSon.decmillon(v[0]);
        v[1] = (v[1] != "") ? ((v[1].length == 1) ? v[1] + '0' : v[1]) : "00";
        de = " " + v[1] + "/100";
    }else{
        en = objSon.decmillon(n);
        de = " 00/100";
    }
    return (en + de);
}
function cnt_setSon(o)
{
    if(_('typ').value!='d'){
        var sn = '0';
        if(o=='td'){  /* ingresos */
            if(_('sn')!=null && _('PC0')!=null){
                sn = _('d0').value!='' ? _('d0').value : '0';
            }
        }
        if(o=='th'){  /* egresos */
            sn = _(o).innerHTML;
            var jS = eval('(' + _('axBc').innerHTML + ')');
            for(var i=0; i<50; i++){
                if(_('PC'+i)!=null){
                    var p = _enMatrizJSON(jS, _('bc1').value, 0);
                    if(_('PC'+i).value == jS.tbl[p][1]){
                        sn = _('h'+i).value!='' ? _RD(_('h'+i).value) : '0';
                        break;
                    }
                }
            }
        }
        _('sn').value = FloatToStr(sn);
    }
}
/* COMPROBANTES */
function cnt_comprobantes()
{
    encerar();
    _('d_ayuda').innerHTML = 'Puede revisar el tipo de comprobante cambiando desde la barra de filtro.';
    if(_MENUS_._enArreglo("comprobanteNuevo")>=0){
        btn.icono(-360, 2, 34, 34, 'Nuevo Comprobante', "cnt_comprobanteEditar(-1);", 'd_herra');
    }
    btn.icono(-455, -114, 34, 34, 'Imprimir', "cnt_impIngEgr()", 'd_herra');
    _AJAX.solicitud('frmFiltro', 'op=2', false, 'd_filtro');
    setAnchoPanel(2, 20);
}
function cnt_filtrarComprobantes()
{
    var w = "where fecha_actual between '"+_('fi').value+"' and '"+_('ff').value+"'";
    if(_('idSuc').value!='-0'){
        w += " and id_sucursal="+_('idSuc').value;
    }
    var v = 'vta_comprobante_diario';
    var k = 'id_comprobante_diario';
    var t = _('typ').value;
    _('axNumDoc').style.display='none';
    if(t=='i'){
        v = 'vta_comprobante_ingreso';
        k = 'id_comprobante_ingreso';
        _('axNumDoc').style.display='block';
        _('t_b').focus();
        if(_('t_b').value!=''){
            w += " and num_comp_pago='"+_('t_b').value+"'";
        }
    }
    if(t=='e'){
        v = 'vta_comprobante_egreso';
        k = 'id_comprobante_egreso';
    } 
    _WR = encodeURI(w + ' order by fecha_actual, num_comprobante');
    new Tabla('d_11', 'jmTbl', '', v, '# COMP,FECHA P.,CONCEPTO,MOV.', k+',num_comprobante,toDateSQL(fecha_proceso),detalle,total', '0,70,80,300,70', _altEdi, 'cnt_comprobanteEditar(^)', _WR);
}
function cnt_comprobanteEditar(k)
{
    _('d_ayuda').innerHTML = msg();
    var t=_('typ').value;
    var f = 'frmComprobanteDiario';
    var c = t=='d' ? "ancho=590,alto=430" : "ancho=790,alto=500";
    if(t=='i'){
        f = 'frmComprobanteIngreso';
    }
    if(t=='e'){
        f = 'frmComprobanteEgreso';
    }
    Ventana.crear('cmp', 'COMPROBANTE', c+",modal=true,cerrar=true", '');
    _AJAX.solicitud(f, 'id='+k, false, 'cmp_html');
}
function cnt_anularDocumento(t,i)
{
    _AJAX.solicitud('reversarDocumento', 't='+t+'&id='+i, true, 'body');
}
function cnt_comprobanteDiarioGuardar(g, k)
{    
    var c = _('dt').value;
    if(c==''){
        alert('Por favor, los campos marcados con un  *  son obligatorios.');
        _('dt').focus();
        return false;
    }
    var td = _('td').innerHTML;
    var th = _('th').innerHTML;
    var p = 'id='+k+'&c='+c._codificarURI()+'&f='+_('fp').value+'&t='+td+'&g='+g;
    var j = 0;
    var pc = '';
    var d = '';
    var h = '';
    for(i=0; i<200; i++){
        if(_('d'+i)!=null){
            pc += _('PC'+i).value + ',';
            d += ((_('d'+i).value!='')?_('d'+i).value:'0') + ',';
            h += ((_('h'+i).value!='')?_('h'+i).value:'0') + ',';
            j++;
        }
    }
    if(j < 2){
        alert('Por favor, ingrese por lo menos dos cuentas para que la transacción cumpla con la partida doble.');
        return false;
    }
    if(td=='0' || th=='0'){
        alert('Por favor, debe ingresar los valores correspondientes de cada cuenta sea del debe o del haber.');
        return false;
    }
    if(!cnt_verificarCuentas(pc)){
        alert('Por favor, las cuentas no deben repetirse.');
        return false;
    }
    if(parseFloat(td) != parseFloat(th)){
        alert('Por favor, los totales deben ser iguales.');
        return false;
    }
    p += '&pc='+pc.substr(0, pc.length-1)+'&d='+d.substr(0, d.length-1)+'&h='+h.substr(0, h.length-1);
    _AJAX.solicitud('frmComprobanteDiarioGuardar', p, true, 'body');
}
function cnt_comprobanteIngresoGuardar(g, k)
{
    var nb = _('nb').value;
    var c = _('dt').value;
    var ch = _('ch').value;
    var bc = _('bc').value;
    if(c=='' || nb==''){
        alert('Por favor, los campos marcados con un  *  son obligatorios.');
        return false;
    }
    if(_('foPa').value=='c'){
        if(ch=='' || bc==''){
            alert('Por favor, los campos marcados con un  *  son obligatorios.');
            return false;
        }
        if(_('fe').value!=''){
            var F = new Date();
            var hoy = F.getFullYear()+'-'+(F.getMonth()+1)+'-'+F.getDate();
            if(SelectorFecha.getTimestamp(_('fp').value) > SelectorFecha.getTimestamp(_('fe').value) || SelectorFecha.getTimestamp(_('fe').value) > SelectorFecha.getTimestamp(hoy)){
                alert('La fecha de efectivización debe encontrarse entre la fecha del formulario y la fecha actual.');
                return false;
            }
        }
    }
    if(_('foPa').value=='p' || _('foPa').value=='t'){
        if(_('ncp').value==''){
            alert('Por favor, los campos marcados con un  *  son obligatorios.');
            return false;
        }
        if((_('foPa').value=='t' || _('foPa').value=='j') && _('gb').value!=''){
            if(parseFloat(_('gb').value) != parseFloat(_('d1').value)){
                alert('El valor de los gastos bancarios no coinciden con la segunda cuenta del asiento');
                return false;
            }
        }
    }
    var td = _('td').innerHTML;
    var th = _('th').innerHTML;
    var p = 'id='+k+'&c='+c._trim()+'&f='+_('fp').value+'&t='+td+'&vs='+_('d0').value+'&g='+g+'&tp='+_('foPa').value+'&nb='+nb._trim();
        p += '&ch='+ch+'&bc='+bc._trim()+'&sn='+_('sn').value+'&fe='+_('fe').value+'&ncp='+_('ncp').value+'&gb='+_('gb').value;
    var j = 0;
    var pc = '';
    var d = '';
    var h = '';
    for(i=0; i<50; i++){
        if(_('d'+i)!=null){
            pc += _('PC'+i).value + ',';
            d += ((_('d'+i).value!='')?_('d'+i).value:'0') + ',';
            h += ((_('h'+i).value!='')?_('h'+i).value:'0') + ',';
            j++;
        }
    }
    if(j < 2){
        alert('Por favor, ingrese por lo menos dos cuentas para que la transacción cumpla con la partida doble.');
        return false;
    }
    if(td=='0' || th=='0'){
        alert('Por favor, debe ingresar los valores correspondientes de cada cuenta sea del debe o del haber.');
        return false;
    }
    if(!cnt_verificarCuentas(pc)){
        alert('Por favor, las cuentas no deben repetirse.');
        return false;
    }
    if(parseFloat(td) != parseFloat(th)){
        alert('Por favor, los totales deben ser iguales.');
        return false;
    }

    var foPa = _('foPa').value;
    var saldo = parseFloat(_('saldo_doc').value);
    if(foPa=='p' || foPa=='t' || foPa=='j'){
        var tt = parseFloat(td);
        if(tt > saldo){
            alert('El saldo $ '+saldo+' del documento ingresado no cubre el monto de la cuenta por cobrar.');
            return false;
        }
    }
    
    p += '&pc='+pc.substr(0, pc.length-1)+'&d='+d.substr(0, d.length-1)+'&h='+h.substr(0, h.length-1)+'&saldo_doc='+saldo;
    _AJAX.solicitud('frmComprobanteIngresoGuardar', p, true, 'body');
}
function cnt_comprobanteEgresoGuardar(g, k)
{
    var nb = _('nb').value;
    var ru = _('ru').value;
    var bc = _('bc').value;
    var ct = _('ct').value;
    var ch = _('ch').value;
    var lu = _('lu').value;
    var c = _('dt').value;
    var sn = _('sn').value;
    if(nb=='' || ru=='' || c=='' || sn==''){
        alert('Por favor, los campos marcados con un  *  son obligatorios.');
        return false;
    }

    /*if(!esDocumento('ru')){
        return false;
    }*/
    if(_('e1').checked){
        if(ch=='' || bc=='' || lu==''){
            alert('Por favor, los campos marcados con un  *  son obligatorios.');
            return false;
        }        
    }

    var td = _('td').innerHTML;
    var th = _('th').innerHTML;

    /* para sacar el total del cheque */
    var jS = eval('(' + _('axBc').innerHTML + ')');
    for(var i=0; i<50; i++){
        if(_('PC'+i)!=null){
            var x = _enMatrizJSON(jS, _('bc1').value, 0);
            if(_('PC'+i).value == jS.tbl[x][1]){
                break;
            }
        }
    }
    var tt = _('h'+i)!=null ? _('h'+i).value : td;

    var p = 'id='+k+'&c='+c._codificarURI()+'&f='+_('fp').value+'&tp='+_('e0').checked+'&t='+td+'&vs='+tt+'&g='+g+'&nb='+nb._codificarURI();
        p += '&ru='+ru+'&bc='+bc._codificarURI()+'&ct='+ct+'&ch='+ch+'&lu='+lu._codificarURI()+'&sn='+sn+'&fe='+_('fe').value+'&id_bc='+_('bc1').value;
    var j = 0;
    var pc = '';
    var d = '';
    var h = '';
    for(i=0; i<50; i++){
        if(_('d'+i)!=null){
            pc += _('PC'+i).value + ',';
            d += ((_('d'+i).value!='')?_('d'+i).value:'0') + ',';
            h += ((_('h'+i).value!='')?_('h'+i).value:'0') + ',';
            j++;
        }
    }
    if(j < 2){
        alert('Por favor, ingrese por lo menos dos cuentas para que la transacción cumpla con la partida doble.');
        return false;
    }
    if(td=='0' || th=='0'){
        alert('Por favor, debe ingresar los valores correspondientes de cada cuenta sea del debe o del haber.');
        return false;
    }
    if(!cnt_verificarCuentas(pc)){
        alert('Por favor, las cuentas no deben repetirse.');
        return false;
    }
    if(parseFloat(td) != parseFloat(th)){
        alert('Por favor, los totales deben ser iguales.');
        return false;
    }
    p += '&pc='+pc.substr(0, pc.length-1)+'&d='+d.substr(0, d.length-1)+'&h='+h.substr(0, h.length-1);
    _AJAX.solicitud('frmComprobanteEgresoGuardar', p, true, 'body');
}
function cnt_getProveedores()
{
    _AJAX.solicitud('getProveedoresEgresos', 'txt='+_('nb').value);
}
function cnt_impIngEgr()
{
    var F = new Date();
    var hoy = F.getDate()+'/'+(F.getMonth()+1)+'/'+F.getFullYear();
    var h = "<input type='radio' id='z0' name='z' checked='checked' value='42' /> <span onclick=\"_('z0').checked='true'\">Ingresos y egresos del d&iacute;a <input type='text' id='idCmb' value='"+hoy+"' size='9' readOnly><input type='button' value='...' onClick=\"SelectorFecha.crear('idCmb', 'SQL');\" /> </span>" +
        "<hr><input type='radio' id='x' name='x' checked /> PDF &nbsp;&nbsp;&nbsp;&nbsp;&nbsp; <input type='radio' id='x2' name='x' /> Excel "+
        "<br/><br/><input type='button' value='Imprimir' onclick='imprimirReporte()'>";
    Ventana.crear('vta_im', 'Reportes', "ancho=400,alto=180,modal=true,cerrar=true", h);
}
function cnt_comprobanteSetNumCheque(k)
{
    var ch = _('ch').value;
    if(ch!='' && ch!='0'){
        _AJAX.solicitud('frmSetNumCheque', 'id='+k+'&num='+_('ch').value+'&bc1='+_('bc1').value);
    }
}
/* CAJA CHICA */
function cnt_caja_chica()
{
    encerar();
    _('d_filtro').innerHTML = 'Documento: &nbsp;' + setCombo('typ', '{tbl:[{0:"v",1:"Vale de caja chica"},{0:"r",1:"Reposici&oacute;n de caja chica"}]}', 'cnt_caja_chicaFiltrar()') +
        '<br />' + setFechas() + setBoton('button', 'Filtrar', 'cnt_caja_chicaFiltrar()');
    _('d_ayuda').innerHTML = 'Puede revisar los diferentes documentos cambiando desde la barra de filtro.';    
    cnt_caja_chicaFiltrar();
    var aj = new Ajax();
    aj.solicitud('frmAlertaMinCajaChica');
    setAnchoPanel(2, 20);
}
function cnt_caja_chicaFiltrar()
{
    _('d_herra').innerHTML='<div class="amague">.</div>';
    if(_('typ').value=='v'){
        if(_MENUS_._enArreglo("caja_chicaNuevo")>=0){
            btn.icono(-360, 2, 34, 34, 'Nuevo Vale de Caja Chica', "cnt_caja_chicaEditar(-1);", 'd_herra');
        }
    }

    var w = '';
    _WR = w;
    switch(_('typ').value){
        case 'v':
            w = "where id_sucursal="+_ID_SUC_+" and fecha between '"+_('fi').value+"' and '"+_('ff').value+"' order by id_vale_caja";
            _WR = encodeURI(w); 
            new Tabla('d_11', 'jmTbl', '', 'tbl_vale_caja', '# VALE,FECHA,CONCEPTO,IMPORTE,SOLICITANTE', "id_vale_caja,id_sucursal || '-' || num_vale_caja,toDateSQL(fecha),concepto,importe,solicitante", '0,70,80,300,70,200', _altEdi, 'cnt_caja_chicaEditar(^)', _WR);
        break;
        case 'r':
            var aw = '';
            if(_ID_SUC_MAT_ != _ID_SUC_){
                aw = "id_sucursal="+_ID_SUC_+" and";
            }
            w = "where "+aw+" periodo_desde between '"+_('fi').value+"' and '"+_('ff').value+"' order by periodo_desde desc";
            _WR = encodeURI(w); 
            new Tabla('d_11', 'jmTbl', '', 'vta_reposicion_caja_chica', '# DOC,PERD. DESDE,PERD. HASTA,T. IMPORTES', 'id_reposicion_caja_chica,num_reposicion,toDateSQL(periodo_desde),toDateSQL(periodo_hasta),total_importe', '0,70,80,80,80', _altEdi, 'cnt_rep_caja_chicaEditar(^)', _WR);
        break;
    }
}
function cnt_caja_chicaEditar(k)
{   
    if(k==-1){
        var aj = new Ajax();
        aj.solicitud('frmAlertaMinCajaChica');
    }
    _('d_ayuda').innerHTML = msg();
    Ventana.crear('vta1', 'VALE DE CAJA CHICA', "ancho=590,alto=330,modal=true,cerrar=true");
    _AJAX.solicitud('frmValeCaja', 'id='+k, false, 'vta1_html');
}
function cnt_vale_cajaGuardar(f)
{
    if(f.importe.value=='' || f.concepto.value=='' || f.la_suma_de.value=='' || f.solicitante.value==''){
        alert('todos los campos son obligatorios');
        return false;
    }
    if(parseFloat(f.importe.value) > parseFloat(f.saldo_caja.value) ){
        alert('Importe exede al saldo en caja chica.');
        return false;
    }
    if(parseFloat(f.importe.value) > 50){
        alert('El monto máximo de un vale de caja chica es de $50.');
        return false;
    }
    return _AJAX.enviarForm(f);
}
function cnt_vale_cajaAnular(k)
{
    if(confirm('Está seguro(a) de anular el vale de caja chica.')){
        _AJAX.solicitud('frmValeCajaAnular', 'id='+k, true, 'body');
        _('btn_valAnular').disabled=true;
    }
}
function cnt_rep_caja_chicaEditar(k)
{
    _('d_ayuda').innerHTML = msg();
    Ventana.crear('vta1', 'REPOSICION DE CAJA CHICA', "ancho=780,alto=500,modal=true,cerrar=true");
    new Tab('vta1_html');
    _AJAX.solicitud('frmReposicionCajaChica', 'id='+k, false, 'vta1_html');
}
function cnt_rep_caja_chicaAprobar(f)
{
    var i=0;
    while(_('ap'+i)!=null){
        if(!_('ap'+i).checked && !_('apb'+i).checked){
            alert('Debe aprobar o des-aprobar todos los vales de caja');
            return false;
        }
        i++;
    }
    if(confirm("Se anularán los vales de caja chica y los pagos simples (no de varios documentos) realizados en efectivo de liquidaciones de compras, notas de créditos y facturas no aprobadas.\n¿Esta seguro(a) de registrar la aprobación de reposición de caja chica?")){
        f.action="frmReposicionCajaChicaAprobar";
        return _AJAX.enviarForm(f);
        _('btApr').disabled=true;
    }
    return false;
}
function cnt_rep_caja_chicaGuardar(f)
{
    if(f.nb.value=='' || f.ru.value=='' || f.dt.value=='' || f.ch.value=='' || f.lu.value==''){
        alert('todos los campos marcados con un  *  son obligatorios');
        return false;
    }
    if(parseFloat(_('td').innerHTML) != parseFloat(_('th').innerHTML)){
        alert('Los valores de los totales en el comprobante de egreso no coinciden.');
        return false;
    }
    if(confirm("Una vez registrado el formulario de reposición de caja chica ya no podrá anularlo.\n¿Esta seguro(a) de registrar el formulario de reposición?")){
        return _AJAX.enviarForm(f);
    }
    return false;
}
/* CONCILIACION BANCARIA */
function cnt_conciliacion()
{
    encerar();
    var h = 'Cheques: <select id="tpCH" onchange="cnt_conciliacion_seleccion()">';
    if(_MENUS_._enArreglo("bancos_pagos")>=0){
        h += '<option value="e">Emitidos</option>';
    }
    if(_MENUS_._enArreglo("bancos_cobros")>=0){
        h += '<option value="r">Recibidos</option>';
    }
    _('d_filtro').innerHTML = h + '</select>';
    cnt_conciliacion_seleccion();
    setAnchoPanel(2, 20);
}
function cnt_conciliacion_seleccion()
{
    if(_('tpCH').value=='e'){
        btn.icono(-455, -114, 34, 34, 'Imprimir', "cnt_impCheques();", 'd_herra');
        cnt_bancos_pagos();
        setAnchoPanel(1, 830);
    }else{
        _('d_herra').innerHTML='<div class="amague">.</div>';
        cnt_bancos_cobros();
        setAnchoPanel(1, 630);
    }
}
function cnt_bancos_pagos()
{
    _('d_ayuda').innerHTML = 'Para confirmar el pago de/los cheques, ingrese la fecha de cobro de/los cheques emitidos.';
    _AJAX.solicitud('tblChequesPago', 'altB='+_altBody, false, 'd_11');
}
function cnt_chequesBancoGuardar(f)
{
    var i=0;
    var ok=false;
    while(_('fe'+i)!=null){
        if(_('fe'+i).value != ''){
            ok=true;
            if(SelectorFecha.getTimestamp(_('hoy').value) < SelectorFecha.getTimestamp(_('fe'+i).value)){
                alert('La fecha de efectivización debe ser menor o igual a la fecha actual.');
                return false;
            }
        }
        i++;
    }
    if(!ok){
        alert('Por, favor debe ingresar la fecha de efectivización de por lo menos uno de los cheques para poder confirmar la efectivización de los pagos.');
        return false;
    }
    return _AJAX.enviarForm(f);
}
function cnt_bancos_cobros()
{
    _('d_ayuda').innerHTML = 'Para confirmar el cobro de/los cheques, ingrese la fecha de efectivizaci&oacute;n de/los cheques recibidos.';
    _AJAX.solicitud('tblChequesCobro', 'altB='+_altBody, false, 'd_11');
}
function cnt_chequesBancoGuardar(f)
{
    var i=0;
    var ok=false;
    while(_('fe'+i)!=null){
        if(_('fe'+i).value != ''){
            ok=true;
            if(SelectorFecha.getTimestamp(_('hoy').value) < SelectorFecha.getTimestamp(_('fe'+i).value)){
                alert('La fecha de efectivización debe ser menor o igual a la fecha actual.');
                return false;
            }
        }
        i++;
    }
    if(!ok){
        alert('Por, favor debe ingresar la fecha de efectivización de por lo menos uno de los cheques para poder confirmar la efectivización de los cobros.');
        return false;
    }
    return _AJAX.enviarForm(f);
}
function cnt_impCheques()
{
    var h = "<form onsubmit='return reporte(this)' autocomplete='off'>" + setRadio('z', '{tbl:[{0:"ChequesEmitidos",1:"Cheques Emitidos"},'+
                '{0:"ChequesConciliados",1:"Cheques Conciliados"}]}', "") +
        '<div id="grFch"><hr />Desde: '+setFecha('fini')+'<br />Hasta: '+setFecha('ffin')+'</div>' +
        setPieReporte() +"</form>";
    Ventana.crear('vta_im', 'CONCILIACION BANCARIA', "ancho=280,alto=240,modal=true,cerrar=true", h);
}
/* DOCUMENTOS ANULADOS */
function cnt_anulados()
{
    encerar();
    _('d_filtro').innerHTML = '<form onsubmit="return reporte(this)" autocomplete="off">' +
      'Documentos: ' + setCombo('z', '{tbl:[{0:"0",1:"TODOS"},{0:"1",1:"Facturas"},{0:"3",1:"Liquidaciones de compras/servicios"},{0:"4",1:"Notas de c&eacute;dito"},{0:"5",1:"Notas de d&eacute;bito"},{0:"7",1:"Retenciones"}]}', 'cnt_anuladoBuscar()') +
      '<br /><label id="lbfei">Desde la fecha: ' + setFecha('fei') + '<br /></label>Hasta la fecha: &nbsp;<label id="lbfef">' + setFecha('fef') + '</label>'+setBoton('button', 'Filtrar', "cnt_anuladoBuscar()")+'</form>';
    if(_MENUS_._enArreglo("anuladoNuevo")>=0){
        btn.icono(-360, 2, 34, 34, 'Nuevo documento anulado', "cnt_anuladoEditar(-1);", 'd_herra');
    }
    if(_MENUS_._enArreglo("anuladoEliminar")>=0){
        btn.icono(-391, 3, 34, 34, 'Eliminar documento anulado', "cnt_anuladoEliminar();", 'd_herra');
    }
    cnt_anuladoBuscar();
    setAnchoPanel(2, 380);
}
function cnt_anuladoEditar(k)
{
    _('d_ayuda').innerHTML = msg();
    _AJAX.solicitud('frmDocumentoAnulado', 'id='+k, false, 'd_21');
}
function cnt_anuladoGuardar(f)
{
    if(f.serie.value=='' || f.secuencial.value=='' || f.autorizacion.value==''){
        _MS('Los campos marcados con un  *  son obligatorios.');
        return false;
    }
    return _AJAX.enviarForm(f);
}
function cnt_anuladoBuscar()
{
    var w="where fecha_emision between'"+_('fei').value+"' and '"+_('fef').value+"'";
    if(_('z').value!='0'){
        w += " and tipo_comprobante="+_('z').value;
    }
    _WR = w;
    new Tabla('d_11', 'jmTbl', '', 'vta_documento_anulado', 'F. EMISION,SERIE,SECUENCIAL,AUTORIZACION,DOCUMENTO', 'id_documento_anulado,fecha_emision,serie,secuencial,autorizacion,txt_tipo_comprobante', '0,80,50,80,80,200', _altEdi, 'cnt_anuladoEditar(^);', _WR);
}
function cnt_anuladoEliminar()
{
    var k = getMarcados();
    if(!k){
        return false;
    }
    if(confirm('¿Está seguro(a) de eliminar el/los documentos anulados?')){
        _AJAX.solicitud('frmDocumentoEliminar', 'ids='+k+'&WHERE='+_WR+'&p='+(_objT!=null?_objT.pg:0), false, 'd_21');
    }
}
/*  claves acceso SRI   */
function cnt_claves()
{
    encerar();    
    if(_MENUS_._enArreglo("anuladoNuevo")>=0){
        btn.icono(-209, -156, 34, 34, 'Cargar archivo de claves de contingencia del SRI', "fac_setClavesSRI()", 'd_herra');
    }
    fac_clavesSRIBuscar();
    setAnchoPanel(2, 10);
}
function fac_setClavesSRI()
{
    Ventana.crear('vta_subir', 'Transferencia de Archivos', "ancho=440,alto=110,modal=true,cerrar=true", '');
    _AJAX.solicitud('SubirArchivo', 'pr=ProcesaArchivoClavesContingencia', true, 'vta_subir_html');
}
function fac_clavesSRIBuscar()
{
    var w = 'where consumida=false';
    _WR = encodeURI(w); 
    new Tabla('d_11', 'jmTbl', '', 'tbl_clave_contingencia', 'CLAVE', 'clave,clave', '0,300', _altEdi, '', _WR);
}

/* DOCUMENTOS ELECTRONICOS */
function cnt_autorizaciones()
{
    encerar();
    _('d_filtro').innerHTML = '' +
        'Documentos: ' + setCombo('d', '{tbl:[{0:"f",1:"Facturas"},{0:"r",1:"Retenciones"},{0:"c",1:"Notas de c&eacute;dito"}]}', 'cnt_autorizacionesBuscar()') +
        '<br />Estado: ' + setCombo('e', '{tbl:[{0:"f",1:"Por enviar"},{0:"r",1:"Pendientes de autorizar"},{0:"n",1:"Rechazados"}]}', 'cnt_setEstado();cnt_autorizacionesBuscar();') +
        setBoton('button', 'Filtrar', "cnt_autorizacionesBuscar()")+'</form>';

    cnt_setEstado();
    
    cnt_autorizacionesBuscar();
    setAnchoPanel(2, 320);
}
function cnt_autorizacionesBuscar()
{
    var w = "where estado_documento='"+_('e').value+"' and anulado=false";
    _WR = encodeURI(w); 
    if(_('d').value=='f'){
        new Tabla('d_11', 'jmTbl', '', 'vta_factura_venta', 'No. DOC,EMISION,CLAVE DE ACCESO,MENSAJE', 'clave_acceso,numero_factura,fecha_emision,clave_acceso as clave_acceso1,mensaje', '0,120,80,330,400', _altEdi, '', _WR);
    } 
    if(_('d').value=='r'){
        new Tabla('d_11', 'jmTbl', '', 'tbl_retencion_compra', 'No. DOC, EMISION,CLAVE DE ACCESO,MENSAJE', "clave_acceso,ret_num_serie || '-' || ret_num_retencion,ret_fecha_emision,clave_acceso as clave_acceso1,mensaje", '0,120,80,330,400', _altEdi, '', _WR);
    } 
    if(_('d').value=='c'){
        new Tabla('d_11', 'jmTbl', '', 'vta_nota_credito_venta', 'No. DOC, EMISION,CLAVE DE ACCESO,MENSAJE', 'clave_acceso,numero_nota,fecha_emision,clave_acceso as clave_acceso1,mensaje', '0,120,80,330,400', _altEdi, '', _WR);
    } 
}
function cnt_setEstado()
{
    _('d_herra').innerHTML='<div class="amague">.</div>';
    if(_('e').value=='f'){
        btn.icono(-209, -156, 34, 34, 'Enviar documentos al SRI', "cnt_enviarDocs();", 'd_herra');
        btn.separador('d_herra');
        btn.icono(-177, -226, 34, 34, 'Generar nuevas claves de acceso y enviar documentos al SRI', "cnt_setClavesEnviarDocs();", 'd_herra');
    }
    if(_('e').value=='r'){
        btn.icono(-144, -226, 34, 34, 'Consultar autorizaciones de documentos', "cnt_getAutorizados();", 'd_herra');
        btn.separador('d_herra');
        btn.icono(-218, -228, 34, 34, 'Enviar correos de notificaciones', "cnt_setFechaMail()", 'd_herra');
        btn.separador('d_herra');
        btn.icono(-209, -156, 34, 34, 'Re-enviar documentos al SRI', "cnt_enviarDocs();", 'd_herra');
        btn.separador('d_herra');
        btn.icono(-177, -226, 34, 34, 'Generar nuevas claves de acceso y enviar documentos al SRI', "cnt_setClavesEnviarDocs();", 'd_herra');
    }
    if(_('e').value=='n'){
        btn.icono(-177, -226, 34, 34, 'Generar nuevas claves de acceso y enviar documentos al SRI', "cnt_setClavesEnviarDocs();", 'd_herra');
    }
}
function cnt_enviarDocs()
{
    var pks = getMarcados();
    _AJAX.solicitud('frmSriEnviarDocs', 'td='+_('d').value+'&pks='+pks, true, 'body');
}
function cnt_getAutorizados()
{
    var pks = getMarcados();
    _AJAX.solicitud('frmSriGetAutorizados', 'td='+_('d').value+'&pks='+pks, true, 'body');
}
function cnt_setClavesEnviarDocs()
{
    var pks = getMarcados();
    _AJAX.solicitud('frmSriSetClavesEnviarDocs', 'td='+_('d').value+'&pks='+pks, true, 'body');
}
function cnt_setFechaMail()
{
    var f = new Date();
    var fi = _('fi')!=null ? _('fi').value : f._fecha();
    var h='<p style="text-align:center">Fecha de documentos electr&oacute;nicos a enviar e-mail: <input type="text" id="fchMail" size="10" value="'+fi+'" /><input type="button" value="..." onClick="SelectorFecha.crear(\'fchMail\', \'ISO\');" /> &nbsp; <input type="button" value="enviar" onclick="cnt_enviarMails()" /></p>';
    Ventana.crear('vta', 'EMAIL', "ancho=300,alto=110,modal=true,cerrar=true", h);
}
function cnt_enviarMails()
{
    window.open('http://localhost:8080/saitel/correo.jsp?fe='+_('fchMail').value, '_blank');
}

/* SRI */
function cnt_sri()
{
    Ventana.crear('vta_im', 'Formularios y Anexo SRI', "ancho=250,alto=210,modal=true,cerrar=true", '');
    _AJAX.solicitud('frmFiltro', 'op=3', false, 'vta_im_html');
}
function cnt_exportarForm()
{
    /*if(_('z0').checked){
        window.open('formulario103?a='+_('a').value+'&m='+_('m').value, '_blank', '');
    }*/
    if(_('z1').checked){
        window.open('formulario104?a='+_('a').value+'&m='+_('m').value, '_blank', '');
    }
    if(_('z2').checked){
        window.open('AnexoTransaccional?a='+_('a').value+'&m='+_('m').value, '_blank', '');
    }
    /*if(_('z3').checked){
        window.open('REOC?a='+_('a').value+'&m='+_('m').value, '_blank', '');
    }*/
}
/* ESTADOS */
function cnt_estados()
{
    Ventana.crear('vta_im', 'Impresi&oacute;n de Estados', "ancho=300,alto=260,modal=true,cerrar=true", '');
    _AJAX.solicitud('frmFiltro', 'op=4', false, 'vta_im_html');
}
function cnt_imprimirBalance()
{
    var fi = (_('bfi')!=null) ? _('bfi').value : '';
    var ff = (_('bff')!=null) ? _('bff').value : '';
    var suc = _('idSuc')!=null ? _('idSuc').value : '';
    var i=0;
    while(_('z'+i)!=null){
        if(_('z'+i).checked){
            break;
        }
        i++;
    }
    var p = 'op='+_('z'+i).value+'&fi='+fi+'&ff='+ff+'&op2=0&idSuc='+suc+'&an=2010&ms=1&idS=1&WH='+_WR;
    var a = (_('x').checked) ? 'impReportePdf?' : 'impReporteXls?';
    imprimir(a+p);
    _R('bloq_vta_im');
    _R('vta_im');
}
/*  CENTRAL DE RIESGOS  */
function jur_riesgos()
{
    encerar();
    if(_MENUS_._enArreglo("riesgoNuevo")>=0){
        btn.icono(-360, 2, 34, 34, 'Registrar cliente en la central de riesgo', "jur_riesgoEditar('-1');", 'd_herra');
    }
    if(_MENUS_._enArreglo("riesgoEliminar")>=0){
        btn.icono(-391, 3, 34, 34, 'Eliminar', "_objT.eliminar();", 'd_herra');
    }
    if(_MENUS_._enArreglo("riesgoImprimir")>=0){
        btn.icono(-455, -114, 34, 34, 'Imprimir', "jur_riesgoImp();", 'd_herra');
    }
    _('d_filtro').innerHTML = '<div>'+setFechas()+'</div>Cliente: <input type="text" id="t_b" onkeypress="var c=(_esN)?event.which:event.keyCode;if(c==13){jur_riesgoBuscar();}">'+
        '&nbsp;<input type="button" value="Filtrar" onclick="jur_riesgoBuscar();">';
    setAnchoPanel(2, 440);
}
function jur_riesgoEditar(k)
{
    _('d_ayuda').innerHTML = msg();
    _AJAX.solicitud('frmCentralRiesgo', 'id='+k, false, 'd_21');
}
function jur_riesgoGuardar(f)
{
    var ru = _('ru').value;
    var rs = _('rs').value;
    if(ru=='' || rs==''){
        alert('Todos los campos marcados con  *  son obligatorios.');
        return false;
    }
    return _AJAX.enviarForm(f);
}
function jur_riesgoBuscar()
{
    var w = "where fecha_registro between '"+_('fi').value+"' and '"+_('ff').value+"'";
    var t = _('t_b').value.toLowerCase();
    if(t!=''){
        w += " and lower(razon_social) like '%"+t+"%' or lower(ruc) like '"+t+"%'";
    }
    _WR = encodeURI(w);
    new Tabla('d_11', 'jmTbl', '', 'vta_central_riesgo', 'RUC,RAZ&Oacute;N SOCIAL,F.REGISTRO', 'id_central_riesgo,ruc,razon_social,fecha_registro', '0,100,250,80', _altEdi, 'jur_riesgoEditar(^);', _WR);
}
function jur_riesgoImp()
{
    var h = "<input type='radio' id='z0' style='display:none' checked value='5' /><input type='radio' id='x' name='x' checked /> PDF &nbsp;&nbsp;&nbsp;&nbsp;&nbsp; <input type='radio' id='x2' name='x' /> Excel "+
            "<br/><br/><input type='button' value='Imprimir' onclick='imprimirReporte()'>";
    Ventana.crear('vta_im', 'Impresi&oacute;n de Proveedores', "ancho=250,alto=120,modal=true,cerrar=true", h);
}
/* PROVEEDORES */
function inv_proveedores()
{
    encerar();
    if(_MENUS_._enArreglo("proveedorNuevo")>=0){
        btn.icono(-360, 2, 34, 34, 'Nuevo Proveedor', "inv_proveedorEditar('-1');", 'd_herra');
    }
    if(_MENUS_._enArreglo("proveedorEliminar")>=0){
        btn.icono(-391, 3, 34, 34, 'Eliminar', "_objT.eliminar();", 'd_herra');
    }
    if(_MENUS_._enArreglo("proveedorImprimir")>=0){
        btn.icono(-455, -114, 34, 34, 'Imprimir', "inv_impProveedores();", 'd_herra');
    }
    _AJAX.solicitud('frmFiltro', 'op=26&fn=inv_buscarProveedor()', false, 'd_filtro');
    setAnchoPanel(2, 440);
}
function inv_proveedorEditar(k)
{
    _('d_ayuda').innerHTML = msg();
    _AJAX.solicitud('frmProveedor', 'id='+k+'&act=frmProveedorGuardar', false, 'd_21');
}
function inv_proveedorGuardar(f)
{
    var td = _('tipo_documento').value;
    var ru = _('ru').value;
    var rs = _('rs').value;
    var di = _('di').value;
    var ci = _('ci').value;
    var pa = _('pa').value;
    if(ru=='' || rs=='' || ci=='' || pa=='' || di==''){
        alert('Todos los campos marcados con  *  son obligatorios.');
        return false;
    }
    if(td!='03'){
        if(td=='01'){
            if(ru.length!=13){
                alert('El número del RUC debe tener 13 dígitos');
                return false;
            }
        }
        if(td=='02'){
            if(ru.length!=10){
                alert('El número de cédula debe tener 10 dígitos');
                return false;
            }
        }
        if(!esDocumento('ru')){
            return false;
        }
    }
    if(_('ma').value!=''){
        if(!_esMail('ma')){
            return false;
        }
    }
    return _AJAX.enviarForm(f);
}
function inv_buscarProveedor()
{
    var w='where id_sucursal='+_('idSuc').value;
    if(_('t_b')!=null){
        var a=_('t_b').value;
        if(a!=''){
            w += " and (lower(ruc) like '"+a.toLowerCase()+"%' or lower(razon_social) like '%"+a.toLowerCase()+"%')";
        }
    }
    _WR = encodeURI(w);
    new Tabla('d_11', 'jmTbl', '', 'vta_proveedor', 'RUC,RAZ&Oacute;N SOCIAL', 'id_proveedor,ruc,razon_social', '0,100,250', _altEdi, 'inv_proveedorEditar(^);', _WR);
}
function inv_impProveedores()
{
    var h = "<input type='radio' id='z0' style='display:none' checked value='5' /><input type='radio' id='x' name='x' checked /> PDF &nbsp;&nbsp;&nbsp;&nbsp;&nbsp; <input type='radio' id='x2' name='x' /> Excel "+
            "<br/><br/><input type='button' value='Imprimir' onclick='imprimirReporte()'>";
    Ventana.crear('vta_im', 'Impresi&oacute;n de Proveedores', "ancho=250,alto=120,modal=true,cerrar=true", h);
}
/* PRODUCTOS */
function inv_productos()
{
    encerar();
    if(_MENUS_._enArreglo("productoNuevo")>=0){
        btn.icono(-360, 2, 34, 34, 'Nuevo Producto', "inv_productoEditar('-1');", 'd_herra');
    }
    if(_MENUS_._enArreglo("productoEliminar")>=0){
        btn.icono(-391, 3, 34, 34, 'Eliminar', "_objT.eliminar();", 'd_herra');
    }
    if(_MENUS_._enArreglo("productoDescuento")>=0){
        btn.icono(2, -156, 34, 34, 'Descuentos', "inv_productoDescuento();", 'd_herra');
    }
    if(_MENUS_._enArreglo("productoImprimir")>=0){
        btn.icono(-455, -114, 34, 34, 'Imprimir', "inv_impProductos();", 'd_herra');
    }
    if(_MENUS_._enArreglo("inventarioInicial")>=0 && _INV_INI_){
        btn.separador('d_herra');
        btn.icono(-33, -157, 34, 34, 'Inventario inicical', "inv_inventarioInicial('');", 'd_herra');
    }
    _AJAX.solicitud('frmFiltro', 'op=5&fn=inv_buscarProducto()', false, 'd_filtro');
    setAnchoPanel(2, 500);
}
function inv_productoEditar(k)
{
    _('d_ayuda').innerHTML = msg() + '<br /> Por favor no utilice comas simples en los campos de texto.';
    new Tab('d_21', 450, _altBody-310);
    _AJAX.solicitud('frmProducto', 'id='+k+'&altB='+(_altBody-310), false, 'd_21');
}
function inv_productoGuardar(f)
{
    if(_('idC')==null){
        return false;
    }
    var c = _('c').value;
    var de = _('de').value;
    var um = _('um').value;
    var idC = _('idC').value;
    var stm = _('stm').value;
    var st = _('st').value;
    var utmi = _('utilidad_min').value;
    if(c=='' || de=='' || um=='' || idC=='' || stm=='' || st=='' || utmi==''){
        alert('Todos los campos marcados con  *  son obligatorios.');
        return false;
    }
    var i=0;
    var suc=false;
    while(_('s_ch'+i)!=null){
        if(_('s_ch'+i).checked){
            suc=true;
            break;
        }
        i++;
    }
    if(!suc){
        alert('Debe seleccionar por lo menos una sucursal antes de guardar el producto.');
        return false;
    }
    return _AJAX.enviarForm(f);
}
function inv_buscarProducto()
{
    var w = 'where';
    var e = true;
    if(_('idSuc').value!='-0'){
        w += ' id_sucursal='+_('idSuc').value;
        e = false;
    }
    if(_('idCat').value!='-0'){
        w += w=='where' ? " id_categoria='"+_('idCat').value+"'" : " and id_categoria='"+_('idCat').value+"'";
    }
    var a=_('t_b').value;
    if(a!=''){
        w += w=='where' ? " lower(codigo) like '"+a.toLowerCase()+"%' or lower(descripcion) like '%"+a.toLowerCase()+"%'" : " and (lower(codigo) like '"+a.toLowerCase()+"%' or lower(descripcion) like '%"+a.toLowerCase()+"%')";
    }
    if(w == 'where'){
        w = '';
    }
    _WR = encodeURI(w);
    if(e){
        new Tabla('d_11', 'jmTbl', '', 'vta_producto', 'CODIGO,CATEGORIA,DESCRIPCION,STOCK MIN,STOCK,COSTO', 'id_producto,codigo,categoria,descripcion,stock_min,stock,precio_costo', '0,145,100,200,80,80,80', _altEdi, 'inv_productoEditar(^)', _WR);
    }else{
        new Tabla('d_11', 'jmTbl', '', 'vta_sucursal_producto', 'CODIGO,CATEGORIA,DESCRIPCION,STOCK,P./V.,DESC.', 'id_producto,codigo,categoria,descripcion,stock_sucursal,precio_venta,descuento', '0,145,100,200,80,80,80,80', _altEdi, 'inv_productoEditar(^)', _WR);
    }
}
function inv_impProductos()
{
    var h = "<input type='radio' id='z0' style='display:none' checked value='6' /><input type='hidden' id='fi' value='"+(_('idSuc').value!='-0' ? 'vta_sucursal_producto' : 'vta_producto')+"' />"+
            "<input type='radio' id='x' name='x' checked /> PDF &nbsp;&nbsp;&nbsp;&nbsp;&nbsp; <input type='radio' id='x2' name='x' /> Excel "+
            "<br/><br/><input type='button' value='Imprimir' onclick='imprimirReporte()'>";
    Ventana.crear('vta_im', 'Impresi&oacute;n de Productos', "ancho=250,alto=120,modal=true,cerrar=true", h);
}
function inv_setServicio()
{
    if(_('t').value=='s'){
        _('st').value=1;
        _('axPV').style.visibility='visible';
        _('utilidad_min').value='0';
    }else{
        _('st').value=_('pv').value=0;
        _('axPV').style.visibility='hidden';
        _('utilidad_min').value='5';
    }
    var i=0;
    while(_('skSc'+i)!=null){
        _('s_ch'+i).checked = _('t').value=='s' ? true : false;
        _('skSc'+i).value = _('t').value=='s' ? 1 : 0;
        _('skSc'+i).readOnly = (_('t').value=='p') ? true : false;
        i++;
    }
}
function inv_insertarUbicacion()
{
    var h = "Nueva ubicaci&oacute;n: <input type='text' id='nubi' onkeypress='_alfanumerico(event);' /> "+
            "<br/><br/><input type='button' value='Guardar' onclick='inv_insertarUbicacionGuardar()'>";
    Ventana.crear('vta_im', 'Ubicaci&oacute;n', "ancho=250,alto=120,modal=true,cerrar=true", h);
    _('nubi').focus();
}
function inv_insertarUbicacionGuardar()
{
    if(_('nubi').value==''){
        _MS('Debe llenar la cacilla de texto antes de guardar', _('nubi'));
        return false;
    }
    _AJAX.solicitud('insertarUbicacion', 'id='+_('id').value+'&ub='+_('nubi').value, false, 'tab11');
}
function inv_productoDescuento()
{
    var k = getMarcado();
    if(!k){
        return false;
    }
    var h = "% de descuento: <input type='text' id='des' onkeypress=\"_evaluar(event,'0123456789.');\" /> "+
            "<br/><br/><input type='button' value='Aplicar Descuento' onclick='inv_productoDescuentoGuardar()'>";
    Ventana.crear('vta_im', 'Descuentos', "ancho=250,alto=120,modal=true,cerrar=true", h);
    _('des').focus();
}
function inv_productoDescuentoGuardar()
{
    if(_('des').value==''){
        _MS('Debe ingresar el porcentaje de descuento en la cacilla de texto antes de Aplicar', _('des'));
        return false;
    }
    _AJAX.solicitud('descuentosGuardar', 'idSuc='+_('idSuc').value+'&idPds='+getMarcados()+'&des='+_('des').value, true, 'BODY');
}
function inv_inventarioInicial(a)
{
    _('d_ayuda').innerHTML = 'Los productos que no tienen asignada una cantidad o un costo unitario no ser&aacute;n ingresados.<br />Una vez ingresado el inventario inicial ya no podrá modificarlo.';
    _AJAX.solicitud('frmInventarioInicial', 'arch='+a+'&altB='+_altBody, false, 'd_11');
}
function inv_setArchivoCVS()
{
    Ventana.crear('vta_subir', 'Transferencia de Archivos', "ancho=440,alto=110,modal=true,cerrar=true", '');
    _AJAX.solicitud('SubirArchivo', 'pr=ProcesaTransferirArchivoCVS', true, 'vta_subir_html')
}
function inv_finTransferencia(m, a)
{
    if(m==0){
        _R('bloq_vta_subir');
        _R('vta_subir');
        inv_inventarioInicial(a);
    }else{
        alert(m);
    }
    _('cargando').style.display = 'none';
    _('frmArchivo').style.display = 'block';
    _('archivo').value='';
    return true;
}
function inv_setTotal(p)
{
    var c = _('c'+p).value || 0;
    var vu = _('vu'+p).value || 0;
    _('vt'+p).innerHTML = _RD(parseInt(c) * parseFloat(vu));

    var i=0;
    var t=0;
    var tt=0;
    while(_('c'+i)!=null){
        t = _('vt'+i).innerHTML || 0;
        tt += parseFloat(t);
        i++;
    }
    _('t').innerHTML = _RD(tt);
}
function inv_invInicialGuardar()
{
    var p='inv='+_('t').innerHTML+'&f='+_('f').value;
    var id = '';
    var c = '';
    var vu = '';
    var vt = '';
    var i=0;
    while(_('c'+i)!=null){
        if(_('c'+i).value!='' && _('vu'+i).value!=''){
            if(parseInt(_('c'+i).value)>0 && parseFloat(_('vu'+i).value)>0){
                id += _('id'+i).value + ',';
                c += _('c'+i).value + ',';
                vu += _('vu'+i).value + ',';
                vt += _('vt'+i).innerHTML + ',';
            }
        }
        i++;
    }
    if(i>0){
        id = id.substring(0, id.length - 1);
        c = c.substring(0, c.length - 1);
        vu = vu.substring(0, vu.length - 1);
        vt = vt.substring(0, vt.length - 1);
        p += '&id='+id+'&c='+c+'&vu='+vu+'&vt='+vt;
        if(confirm("Una vez ingresado el inventario inicial ya no podrá modificarlo.\n ¿Está seguro(a) de ingresar el inventario inicial?")){
            return _AJAX.solicitud('frmInventarioInicialGuardar', p, true, 'body');
        }
    }
}
function inv_productoPrecio(k)
{
    Ventana.crear('vta1', 'LISTA DE PRECIO', "ancho=320,alto=180,modal=true,cerrar=true");
    new Tab('vta1_html');
    _AJAX.solicitud('frmPrecioProducto', 'id='+k+'&idProd='+_('id').value, false, 'vta1_html');
}
function inv_productoPrecioGuardar(f)
{
    if(f.lista.value=='' || f.utilidad.value==''){
        alert('Todos los campos son obligatorios');
        return false;
    }
    return _AJAX.enviarForm(f);
}
function inv_productoPrecioEliminar(k, i)
{
    return _AJAX.solicitud('frmPrecioEliminar', 'id='+k+'&i='+i);
}
/* KARDEX */
function inv_kardex()
{
    encerar();
    _AJAX.solicitud('frmFiltro', 'op=5&fn=inv_filtrarKardex()', false, 'd_filtro');
    setAnchoPanel(2, 870);
}
function inv_filtrarKardex()
{
    var w = "where tipo='p'";
    var v = 'vta_producto';
    if(_('idSuc').value!='-0'){
        w += ' and id_sucursal='+_('idSuc').value;
        v = 'vta_sucursal_producto';
    }
    if(_('idCat').value!='-0'){
        w += " and id_categoria='"+_('idCat').value+"'";
    }
    var a=_('t_b').value;
    if(a!=''){
        w += " and (lower(codigo) like '"+a.toLowerCase()+"%' or lower(descripcion) like '%"+a.toLowerCase()+"%')";
    }
    _WR = encodeURI(w);
    new Tabla('d_11', 'jmTbl', '', v, 'CODIGO,CATEGORIA,DESCRIPCION', 'id_producto,codigo,categoria,descripcion', '0,145,90,200', _altEdi, 'inv_verKardex(^)', _WR);
}
function inv_verKardex(k)
{
    var f = new Date();
    var fi = _('fi')!=null ? _('fi').value : f._fecha();
    var ff = _('ff')!=null ? _('ff').value : f._fecha();
    _AJAX.solicitud('frmKardex', 'id='+k+'&altB='+_altBody+'&idSuc='+_('idSuc').value+'&fi='+fi+'&ff='+ff, false, 'd_21');
}
function inv_impKardex(k)
{
    imprimir('pdfKardex?id='+k+'&idSuc='+_('idSuc').value+'&fi='+_('fi').value+'&ff='+_('ff').value);
}
function inv_kardexAjuste(k)
{
    _('d_ayuda').innerHTML = msg();
    Ventana.crear('cmp', 'AJUSTE DE KARDEX', "ancho=590,alto=400,modal=true,cerrar=true", '');
    _AJAX.solicitud('frmKardexAjuste', 'id='+k+'&altB='+_altBody, false, 'cmp_html');
}
function inv_kardexAjusteGuardar(f)
{
    var nc = _('nc').value;
    var c = _('dt').value;
    if(nc=='' || c==''){
        alert('Por favor, los campos marcados con un  *  son obligatorios.');
        _('dt').focus();
        return false;
    }
    var td = _('td').innerHTML;
    var th = _('th').innerHTML;
    var j = 0;
    var pc = '';
    for(var i=0; i<50; i++){
        if(_('d'+i)!=null){
            pc += _('PC'+i).value + ',';
            j++;
        }
    }
    if(j < 2){
        alert('Por favor, ingrese por lo menos dos cuentas para que la transacción cumpla con la partida doble.');
        return false;
    }
    if(td=='0' || th=='0'){
        alert('Por favor, debe ingresar los valores correspondientes de cada cuenta sea del debe o del haber.');
        return false;
    }
    if(!cnt_verificarCuentas(pc)){
        alert('Por favor, las cuentas no deben repetirse.');
        return false;
    }
    if(parseFloat(td) != parseFloat(th)){
        alert('Por favor, los totales deben ser iguales.');
        return false;
    }
    if(parseFloat(td) != parseFloat(_('vt').value)){
        alert('Por favor, el valor total debe ser igual a los totales.');
        return false;
    }
    return _AJAX.enviarForm(f);
}
/* ESTRUCTURA NODOS */
function inf_nodos()
{
    encerar();
    _('d_filtro').innerHTML = 'Nodo: <input type="text" id="t_b" onkeypress="var c=(_esN)?event.which:event.keyCode;if(c==13){inf_nodoBuscar();}">'+
        '&nbsp;<input type="button" value="Filtrar" onclick="inf_nodoBuscar();">';
    if(_MENUS_._enArreglo("nodoNuevo")>=0){
        btn.icono(-360, 2, 34, 34, 'Nuevo Nodo', "inf_nodoEditar('-1');", 'd_herra');
    }
    if(_MENUS_._enArreglo("nodoEliminar")>=0){
        btn.icono(-391, 3, 34, 34, 'Eliminar', "_objT.eliminar();", 'd_herra');
    }
    _AJAX.solicitud('frmFiltro', 'op=27&fn=inf_nodoBuscar()', false, 'd_filtro');
    setAnchoPanel(2, 20);
}
function inf_nodoEditar(k)
{
    _('d_ayuda').innerHTML = msg();
    Ventana.crear('vta1', 'NODOS', "ancho=800,alto=560,modal=true,cerrar=true");
    new Tab('vta1_html', 820, 545);
    _AJAX.solicitud('frmNodo', 'id='+k, false, 'vta1_html');
}
function inf_setTipo()
{
    if(_('tipo').value=='Otras (Adjuntar breve descripción)'){
        _('tipo_otra').style.display='block';
        _('tipo_otra').focus();
    }else{
        _('tipo_otra').value='';
        _('tipo_otra').style.display='none';
    }
}
function inf_nodoGuardar(f)
{
    if(f.nodo.value=='' || f.altura_msnm.value=='' || f.cod_reg.value=='' || f.altura_estr.value=='' || f.localidad.value==''
            || f.lat_h.value=='' || f.lat_m.value=='' || f.lat_s.value=='' || f.lon_h.value=='' || f.lon_m.value==''
            || f.lon_s.value=='' || f.propietario.value==''){
        alert('Por favor ingrese toda la información del formulario.');
        return false;
    }
    if(_('vf').checked){
        if(_('ci').value!=''){
            if(!esDocumento('ci')){
                return false;
            }
        }
    }
    return _AJAX.enviarForm(f);
}
function inf_nodoBuscar()
{
    var w='where id_sucursal='+_('idSuc').value;
    if(_('t_b')!=null){
        var a=_('t_b').value;
        if(a!=''){
            w += " and lower(nodo) like '%"+a.toLowerCase()+"%'";
        }
    }
    _WR = encodeURI(w);
    new Tabla('d_11', 'jmTbl', '', 'vta_nodo', 'NODO,ESTRUCTURA', 'id_nodo,nodo,cod_reg', '0,200,90', _altEdi, 'inf_nodoEditar(^);', _WR);
}
/* ANTENAS ACOPLADAS */
function inf_antenas()
{
    encerar();
    _('d_filtro').innerHTML = 'Antena: <input type="text" id="t_b" onkeypress="var c=(_esN)?event.which:event.keyCode;if(c==13){inf_antenaBuscar();}">'+
        '&nbsp;<input type="button" value="Filtrar" onclick="inf_antenaBuscar();">';
    if(_MENUS_._enArreglo("antenaNuevo")>=0){
        btn.icono(-360, 2, 34, 34, 'Nueva Antena', "inf_antenaEditar('-1');", 'd_herra');
    }
    if(_MENUS_._enArreglo("antenaEliminar")>=0){
        btn.icono(-391, 3, 34, 34, 'Eliminar', "_objT.eliminar();", 'd_herra');
    }
    inf_antenaBuscar();
    setAnchoPanel(2, 400);
}
function inf_antenaEditar(k)
{
    _('d_ayuda').innerHTML = msg();
    _AJAX.solicitud('frmAntena', 'id='+k+'&altB='+_altBody, false, 'd_21');
}
function inf_antenaGuardar(f)
{
    if(f.antena_acoplada.value==''){
        alert('Todos los campos marcados con  *  son obligatorios.');
        return false;
    }
    return _AJAX.enviarForm(f);
}
function inf_antenaBuscar()
{
    var w='';
    if(_('t_b')!=null){
        var a=_('t_b').value;
        if(a!=''){
            w += "where lower(antena_acoplada) like '%"+a.toLowerCase()+"%'";
        }
    }
    _WR = encodeURI(w);
    new Tabla('d_11', 'jmTbl', '', 'vta_antena', 'ANTENA', 'id_antena_acoplada,antena_acoplada', '0,200', _altEdi, 'inf_antenaEditar(^);', _WR);
}
/* COMPRAS DE ACTIVOS */
function act_compras()
{
    encerar();
    if(_MENUS_._enArreglo("comprarActivosNuevo")>=0){
        btn.icono(-360, 2, 34, 34, 'Nueva Factura de Compra', "act_compraEditar('-1');", 'd_herra');
    }
    _AJAX.solicitud('frmFiltro', 'op=6&fn=act_compraBuscar()', false, 'd_filtro');
    setAnchoPanel(2, 20);
}
function act_compraEditar(k)
{
    _('d_ayuda').innerHTML = msg() + '<br/> Para poder emitir la retenc&oacute;n primero debe guardar la factura.';
    Ventana.crear('vta1', 'COMPRA DE ACTIVOS', "ancho=820,alto=560,modal=true,cerrar=true");
    new Tab('vta1_html');
    _AJAX.solicitud('frmFacturaActivo', 'id='+k, false, 'vta1_html');
}
function act_compraGuardar(f)
{
    var auf = _('auf').value;
    var sf = _('sf').value;
    var nf = _('nf').value;
    var idP = _('coP').value;
    var fc = _('fc').value;
    var fv = _('fv').value;
    var ob = _('ob').value;
    var stt = _('subt').value;
    var tt = _('tt').value;
    if(auf=='' || sf=='' || nf=='' || idP=='0' || fc=='' || fv=='' || ob=='' || stt=='' || tt==''){
        alert('Todos los campos marcados con  *  son obligatorios.');
        return false;
    }
    if(SelectorFecha.getTimestamp(fc) > SelectorFecha.getTimestamp(fv)){
        alert('La fecha de compra (emisión) debe ser menor o igual a la fecha de validez de la factura.');
        return false;
    }
    var fl = false;
    var lm = _('tblD').childNodes.length>0 ? parseInt(_('tblD').childNodes.item(_('tblD').childNodes.length-1).id.replace('r', ''))+1 : 0;
    _('topeP').value = lm;
    var lm1 = _('tblA').childNodes.length>0 ? parseInt(_('tblA').childNodes.item(_('tblA').childNodes.length-1).id.replace('Ar', ''))+1 : 0;
    _('topeA').value = lm1;
    var i=0;
    for(i=0; i<=lm; i++){
        if(_('cod'+i)!=null){
            if(_('cod'+i).value=='' || _('des'+i).value==''){
                alert('Por favor ingrese todos los datos de los activos.');
                return false;
            }
            fl = true;
        }
    }
    /*if(!fl){
        alert('Por favor, ingrese el detalle de la factura.');
        return false;
    }*/
    var idR = '';
    i=0;
    fl = false;
    while(i<10){
        if(_('idR'+i)!=null){
            idR += _('idR'+i).value+',';
            fl = true;
        }
        i++;
    }
    if(fl){
        var sr = _('sr').value;
        //var autr = _('autr').value;
        var nr = _('nr').value;
        var fre = _('fre').value;
        var ef = _('ef').value;
        if(sr=='' || nr=='' || fre=='' || ef=='' || _('tvr').value==''){
            alert('Por favor, ingrese todos los datos para la retención.');
            return false;
        }
        if(SelectorFecha.getTimestamp(_('fre').value) < SelectorFecha.getTimestamp(_('fc').value)){
            alert('La fecha de emisión de la retención debe ser mayor o igual a la fecha de compra (emisión) de la factura.');
            return false;
        }
        nr = parseFloat(nr);
        if(nr!=0 && (nr < parseFloat(_('secDe').value) || nr > parseFloat(_('secHa').value)) ){
            alert("Por favor revise el número secuencial de la retención.");
            return false;
        }
        /*if(SelectorFecha.getTimestamp(_('fre').value) > SelectorFecha.getTimestamp(_('feCad').value)){
            alert("Por favor revise la fecha de caducidad del libretín de retenciones.");
            return false;
        }*/
    }
    return _AJAX.enviarForm(f);
}
function act_compraBuscar()
{
    var w = "where id_sucursal="+_ID_SUC_+" and de_activo=true and fecha_compra between '"+_('fi').value+"' and '"+_('ff').value+"'";
    if(_('idPrv').value!='-0'){
        w += " and id_proveedor="+_('idPrv').value;
    }
    _WR = encodeURI(w);    
    new Tabla('d_11', 'jmTbl', '', 'vta_factura_compra_retencion', 'PROVEEDOR,# FACTURA,F. COMPRA,TOTAL COMPRA,ESTADO RETENCION,MENSAJE', 'id_factura_compra,razon_social,numero_factura,toDateSQL(fecha_compra),total_compra,txt_estado_documento,mensaje', '0,200,100,80,80,80,200', _altEdi, 'act_compraEditar(^);', _WR);
}
function act_compraAnular(i)
{
    if(confirm('Se anularán también todos los documentos asociados a la factura tales como: retenciones y comprobantes de pagos.')){
        _AJAX.solicitud('frmFacturaActivoAnular', 'id='+i, true, 'body');
    }
}
function act_addActivos()
{
    var i = _('tblA').childNodes.length>0 ? parseInt(_('tblA').childNodes.item(_('tblA').childNodes.length-1).id.replace('Ar', ''))+1 : 0;
    var tr = document.createElement("div");
    tr.id='Ar'+i;
    tr.className='jm_fila';
    _('tblA').appendChild(tr);
    var dep = _('axDep').cloneNode(true);
    dep.innerHTML = dep.innerHTML.replace(/_Xx_/g, 'idDep'+i);
    var cat = _('axCat').cloneNode(true);
    cat.innerHTML = cat.innerHTML.replace(/_Xx_/g, 'idCat'+i);
    var ubi = _('axUbi').cloneNode(true);
    ubi.innerHTML = ubi.innerHTML.replace(/_Xx_/g, 'ub'+i);
    var piva = _('axPIva').cloneNode(true);
    piva.innerHTML = piva.innerHTML.replace(/_Xx_/g, 'Api'+i).replace(/_POS_/, i);
    var h ="<div class='jm_columna caja' style='width:223px'><textarea id='cod"+i+"' name='cod"+i+"' style='width:215px;height:28px' ondblclick='act_frmCodigos("+i+")'></textarea></div>"+
    "<div class='jm_columna caja' style='width:228px' id='axDes"+i+"'><input id='des"+i+"' name='des"+i+"' type='text' style='width:220px;' value='' onkeyup=\"act_getDescripcion(this.value, "+i+");\" /></div>"+
    "<div class='jm_columna caja' style='width:158px'>"+dep.innerHTML+"</div>"+
    "<div class='jm_columna caja' style='width:98px'>"+cat.innerHTML+"</div>"+
    "<div class='jm_columna caja' style='width:98px'>"+ubi.innerHTML+"</div>"+
    "<div class='jm_columna der' style='width:68px'><input id='Ac"+i+"' name='Ac"+i+"' readonly type='text' size='7' value='1' onkeyup=\"inv_sumSubTotal('A', "+i+");act_sumTotales('');\" /></div>"+
    "<div class='jm_columna der' style='width:80px'><input id='Apu"+i+"' name='Apu"+i+"' type='text' size='9' onkeypress=\"_evaluar(event, '0123456789.');\" onfocus='this.select();' onkeyup=\"inv_sumSubTotal('A', "+i+");act_sumTotales('A');\" /></div>"+
    "<div class='jm_columna der' style='width:80px'><input id='Ast"+i+"' name='Ast"+i+"' type='text' size='9' onkeypress=\"_evaluar(event, '0123456789.');\" onfocus='this.select();' onkeyup=\"act_sumTotales('A');\" /></div>"+
    "<div class='jm_columna der' style='width:70px'><input id='Ad"+i+"' name='Ad"+i+"' type='text' size='7' value='0' onkeypress=\"_evaluar(event, '0123456789.');\" onfocus='this.select();' onkeyup=\"inv_sumSubTotal('A', "+i+");act_sumTotales('A');\" /></div>"+
    "<div class='jm_columna caja' style='width:98px'>"+piva.innerHTML+"</div>"+
    "<div class='jm_columna der' style='width:68px'><input id='Av"+i+"' name='Av"+i+"' type='text' size='9' onkeypress=\"_evaluar(event, '0123456789.');\" onfocus='this.select();' onkeyup=\"act_setIva("+i+");act_sumTotales('A');\" /></div>"+
    "<div class='jm_columna der' style='width:88px'><input id='At"+i+"' name='At"+i+"' type='text' size='9' onkeypress=\"_evaluar(event, '0123456789.');\" onfocus='this.select();' onkeyup=\"act_sumTotales('A');\" /></div>"+
    "<div class='jm_columna der' style='width:20px'><div class='jm_icoCon' style='background-position:-545px -18px;float:right;' title='Eliminar' onclick=\"_R('Ar"+i+"');act_sumTotales('A');\">&nbsp;</div></div>";
    tr.innerHTML = h;
    _('sllA').scrollTop = 10000;
    act_frmCodigos(i);
}
function act_setIva(i)
{
    var c = (_('Ac'+i).value!='') ? _('Ac'+i).value : 0;
    var pu = (_('Apu'+i).value!='') ? _('Apu'+i).value : 0;
    var st = _RD(parseInt(c)*parseFloat(pu));
    var d = (_('Ad'+i).value!='') ? parseFloat(_('Ad'+i).value) : 0;
    var iv = (_('Av'+i).value!='') ? parseFloat(_('Av'+i).value) : 0;
    _('At'+i).value = _RD(st+iv-d);
}
function act_tblProductos(r, cod)
{
    _R('sonido');
    if(r != '{tbl:]}'){
        _('ax').innerHTML=r;
        setDiv('dv', 690, 195, 'sll0');
        _T('dv', 90);
        var jS = eval('(' + r + ')');
        var h = '<table cellpadding="4" cellspacing="0" border="0" width="100%">';
        for(var i=0; i<jS.tbl.length; i++){
            h += '<tr valign="top" onmouseover="this.className=\'jm_filaSobre\';this.style.cursor=\'pointer\'" onmouseout="this.className=\'\'" onclick="act_addProducto(\'\', '+i+')"><td>'+jS.tbl[i][1]+'</td><td>'+jS.tbl[i][2]+'</td></tr>';
        }
        h += '<table>';
        _('dv').innerHTML = h;
        /*if(cod == 13){
            inv_addProducto('', 0);
        }*/
    }else{
        alerta();
        _R('dv');
        _('busProd').value='';
    }
}
function act_addProducto(pf, x)
{
    _R('sonido');
    if(_('ax').innerHTML!='' && _('ax').innerHTML!='{tbl:]}'){
        var jS = eval('(' + _('ax').innerHTML + ')'); 
        var p = pf || '';
        var i = _(p+'tblD').childNodes.length>0 ? parseInt(_(p+'tblD').childNodes.item(_(p+'tblD').childNodes.length-1).id.replace(p+'r', ''))+1 : 0;
        var tr = document.createElement("tr");
        tr.id=p+'r'+i;
        tr.className='jm_filaPar';
        _(p+'tblD').appendChild(tr);  
        var h = "<td width='230' id='codP"+i+"'><input type='hidden' id='"+p+"idA"+i+"' name='"+p+"idA"+i+"' value='"+jS.tbl[x][0]+"'/>"+
                    "<input type='hidden' id='"+p+"pi"+i+"' name='"+p+"pi"+i+"' value='"+jS.tbl[x][3]+"'/>"+jS.tbl[x][1]+" &nbsp; "+jS.tbl[x][2]+" "+jS.tbl[x][3]+"</td>"+
                    "<td align='right' width='50'><input id='"+p+"c"+i+"' name='"+p+"c"+i+"' type='text' class='inpTbl' size='5' value='1' onkeypress=\"_numero(event);\" onfocus='this.select();' onkeyup=\"inv_sumSubTotal('"+p+"', "+i+");act_sumTotales('"+p+"');\" /></td>"+
                    "<td align='right' width='81'><input id='"+p+"pu"+i+"' name='"+p+"pu"+i+"' type='text' class='inpTbl' size='9' onkeypress=\"_evaluar(event, '0123456789.');\" onfocus='this.select();' onkeyup=\"inv_sumSubTotal('"+p+"', "+i+");act_sumTotales('"+p+"');\" /></td>"+
                    "<td align='right' width='81'><input id='"+p+"st"+i+"' name='"+p+"st"+i+"' type='text' class='inpTbl' size='9' onkeypress=\"_evaluar(event, '0123456789.');\" onfocus='this.select();' onkeyup=\"act_sumTotales('"+p+"');\" /></td>";
        h += "<div class='columna der' style='width:71px'><input id='"+p+"d"+i+"' "+(p=='nd' ? 'readonly' : '')+" name='"+p+"d"+i+"' type='text' class='inpTbl' size='9' value='0' onkeypress=\"_evaluar(event, '0123456789.');\" onfocus='this.select();' onkeyup=\"inv_sumSubTotal('"+p+"', "+i+");act_sumTotales('"+p+"');\" /></div>";
        h += "<td align='right' width='71'><input id='"+p+"v"+i+"' name='"+p+"v"+i+"' type='text' class='inpTbl' size='9' onkeypress=\"_evaluar(event, '0123456789.');\" onfocus='this.select();' onkeyup=\"act_sumTotales('"+p+"');\" /></td>"+
                    "<td align='right' width='91'><input id='"+p+"t"+i+"' name='"+p+"t"+i+"' type='text' class='inpTbl' size='9' onkeypress=\"_evaluar(event, '0123456789.');\" onfocus='this.select();' onkeyup=\"act_sumTotales('"+p+"');\" /></td>"+
                    "<td align='center' width='21'><div class='jm_icoCon' style='background-position:-545px -18px;float:right;' title='Eliminar' onclick=\"_R('"+p+"r"+i+"');act_sumTotales('"+p+"');\">&nbsp;</div></td>";
        tr.innerHTML = h;
        _(p+'sll0').scrollTop = 10000;
        _R('dv');
        _('busProd').value='';
    }else{
        alerta();
        _R('dv');
        _('busProd').value='';
    }
}
function act_sumTotales(p)
{
    var i=0;
    var st = 0;
    var st0 = 0;
    var st2 = 0;
    var iv=0;
    var iv2=0;
    var d=0;
    var t = 0;
    var lm = _('tblD').childNodes.length>0 ? parseInt(_('tblD').childNodes.item(_('tblD').childNodes.length-1).id.replace('r', ''))+1 : 0;
    while(i<=lm){
        if(_('c'+i)!=null){
            st += parseFloat(_('st'+i).value) || 0;
            if(parseFloat(_('pi'+i).value)==0){
                st0 += parseFloat(_('st'+i).value) || 0;
            }
            if(parseFloat(_('pi'+i).value)==12){
                st2 += parseFloat(_('st'+i).value) || 0;
                if(_('v'+i)!=null){
                    iv2 += parseFloat(_('v'+i).value) || 0;
                }
            }
            
            /*st += parseFloat(_('st'+i).value) || 0;
            if(_('codP'+i)!=null){
                if(_('codP'+i).innerHTML.indexOf('~')==-1){
                    iv0 += parseFloat(_('st'+i).value) || 0;
                }
            }*/
            if(_('d'+i)!=null){
                d += parseFloat(_('d'+i).value) || 0;
            }
            iv += parseFloat(_('v'+i).value) || 0;
            t += parseFloat(_('t'+i).value) || 0;
        }
        i++;
    }
    i = 0;
    var lm1 = _('tblA').childNodes.length>0 ? parseInt(_('tblA').childNodes.item(_('tblA').childNodes.length-1).id.replace('Ar', ''))+1 : 0;
    while(i<=lm1){
        if(_('Ac'+i)!=null){
            st += parseFloat(_('Ast'+i).value) || 0;
            if(parseFloat(_('Api'+i).value)==0){
                st0 += parseFloat(_('Ast'+i).value) || 0;
            }
            if(parseFloat(_('Api'+i).value)==12){
                st2 += parseFloat(_('Ast'+i).value) || 0;
                if(_('Av'+i)!=null){
                    iv2 += parseFloat(_('Av'+i).value) || 0;
                }
            }
            if(_('Ad'+i)!=null){
                d += parseFloat(_('Ad'+i).value) || 0;
            }
            iv += parseFloat(_('Av'+i).value) || 0;
            t += parseFloat(_('At'+i).value) || 0;
        }
        i++;
    }
    if(_('subt')!=null){
        _('subt').value = _RD(st);
    }
    if(_('dt')!=null){
        _('dt').value = _RD(d);
    }
    if(_('subt0')!=null){
        _('subt0').value = _RD(st0);
    }
    if(_('subt2')!=null){
        _('subt2').value = _RD(st2);
    }
    if(_('iva2')!=null){
        _('iva2').value = _RD(iv2);
    }
    var prp = _('propina')!=null ? _('propina').value : 0;
    _('tt').value = _RD( t + parseFloat(prp) );

    _('InRe').style.visibility = (t>0) ? 'visible' : 'hidden';
}
function act_getDescripcion(t, i)
{
    _AJAX.solicitud('getDescripciones', 'txt='+t+'&i='+i, false, null);
}
function act_tblDescripciones(r, x)
{
    if(r!='' && r!='{tbl:]}'){
        setDiv('dv', 250, 250, 'axDes'+x);
        var jS = eval('(' + r + ')');
        var h = '<table cellpadding="4" cellspacing="0" border="0" width="100%">';
        for(var i=0; i<jS.tbl.length; i++){
            h += '<tr valign="top" onmouseover="this.className=\'jm_filaSobre\';this.style.cursor=\'pointer\'" onmouseout="this.className=\'\'" onclick="_(\'des'+x+'\').value=\''+jS.tbl[i][0]+'\';_R(\'dv\');"><td>'+jS.tbl[i][0]+'</td></tr>';
        }
        h += '<table>';
        _('dv').innerHTML = h;
    }else{
        _R('dv');
    }
}
function act_frmCodigos(i)
{
    var h = '<table><tr><td width="90">C&oacute;digo - MAC:</td><td id="axSecCodigos"><input type="text" id="codAct" size="40" onkeypress="var c=(_esN)?event.which:event.keyCode;if(c==13){_(\'serAct\').focus()}" onkeyup="act_getSecCodigo(\'codAct\');" /></td></tr>'+
        '<tr><td>Serie:</td><td><input type="text" id="serAct" size="40" onkeypress="var c=(_esN)?event.which:event.keyCode;if(c==13){act_setCodigos('+i+')}" /></td></tr>'+
        '<tr><td>Son materiales:</td><td><input type="checkbox" id="sonMat" onclick="_(\'ax_cant_act\').style.display=this.checked ? \'block\' :\'none\'" /></td></tr>' +
        '<tr><td colspan="2">' +
            '<table id="ax_cant_act" style="display:none"><tr><td width="90">Cantidad:</td><td><input type="text" id="cant_act" size="40" maxlength="5" onkeypress="_numero(event)" /></td></tr></table>' +
        '</td></tr>' +
        '<tr><td><input type="button" value="Insertar" onclick="act_setCodigos('+i+')" /></td><td align="right"><input type="button" value="Cerrar" onclick="_R(\'bloq_vta2\');_R(\'vta2\');" /></td></tr>'+
        '</table>';
    Ventana.crear('vta2', 'CODIGOS - SERIES', "ancho=400,alto=180,modal=true,cerrar=true", h);
    _('codAct').focus();
}
function act_getSecCodigo(ob)
{
    _AJAX.solicitud('getSecCodigo', 'codAct='+_(ob).value+'&obj='+ob, false, 'dv');
    setDiv('dv', 250, 50, 'axSecCodigos');
}
function act_setCodigos(i)
{
    if(_('codAct').value!=''){
        if(_('sonMat').checked){
            _AJAX.solicitud('getMaxSecCodActivo?codAct='+_('codAct').value+'&i='+i, 'id='+i, true, 'body');
        }else{
            _('cod'+i).value += _('codAct').value + '|' + _('serAct').value + ';';
            var v = _('cod'+i).value.split(";");
            _('Ac'+i).value = v.length-1;
            _('codAct').value = _('serAct').value = '';
        }
    }
    _('codAct').focus();
}
function act_setCodigosMateriales(s, i)
{
    var num = parseInt( _('cant_act').value );
    for(j=s; j<parseInt(s)+num; j++){
        _('cod'+i).value += _('codAct').value + j + '|' + ';';
    }
    _('Ac'+i).value = num;
    _('codAct').value = _('serAct').value = _('cant_act').value = '';
}
function act_frmCategoria()
{
    var h = '<table><tr><td nowrap>Nueva categor&iacute;a:</td><td><input type="text" id="nCat" size="35" onkeypress="var c=(_esN)?event.which:event.keyCode;if(c==13){act_setCategoria()}" /></td></tr>'+
        '<tr><td><input type="button" value="Insertar" onclick="act_setCategoria()" /></td><td align="right"><input type="button" value="Cerrar" onclick="_R(\'bloq_vta2\');_R(\'vta2\');" /></td></tr></table>';
    Ventana.crear('vta2', 'NUEVA CATEGORIA', "ancho=340,alto=130,modal=true,cerrar=true", h);
    _('nCat').focus();
}
function act_setCategoria()
{
    _AJAX.solicitud('frmCategoriaGuardar', 'nCat='+_('nCat').value, true, 'body');
}
function act_getEstanterias(k, idN)
{
    var idN1 = idN || '_Xx_';
    _AJAX.solicitud('getUbicaciones', 'id='+k+'&obj=axUbi&idName='+idN1, false, null);
}
/* ACTIVOS FIJOS */
function act_activos()
{
    encerar();
    if(_MENUS_._enArreglo("activoNuevo")>=0){
        btn.icono(-360, 2, 34, 34, 'Nuevo Activo', "act_activoEditar('-1');", 'd_herra');
    }
    if(_MENUS_._enArreglo("activoEliminar")>=0){
        btn.icono(-391, 3, 34, 34, 'Eliminar', "_objT.eliminar('Serán reversadas todas las depresiaciones de el/los activos a eliminar.', 'EliminarActivo');", 'd_herra');
    }
    if(_MENUS_._enArreglo("activoImprimir")>=0){
        btn.icono(-455, -114, 34, 34, 'Imprimir', "act_opImpActivos();", 'd_herra');
    }
    _AJAX.solicitud('frmFiltro', 'op=5&fn=act_buscarActivo()', false, 'd_filtro');
    setAnchoPanel(2, 480);
}
function act_activoEditar(k)
{
    _('d_ayuda').innerHTML = msg()+"<br />La fecha de compra es la fecha de inicio para empezar a realizar la depreciaci&oacute;n del activo.";
    new Tab('d_21', 380, _altBody-310);
    _AJAX.solicitud('frmActivo', 'id='+k+'&obj=d_21&altB='+(_altBody-310), false, 'd_21');
}
function act_activoGuardar(f)
{
    if(_('ubicacion')==null){
        alert('Debe seleccionar una ubicación.');
        return false;
    }
    if(f.codigo.value=='' || f.descripcion.value=='' || f.ubicacion.value=='' || f.fecha_compra.value=='' || f.valor_compra.value==''){
        alert('Todos los campos marcados con  *  son obligatorios.');
        return false;
    }
    return _AJAX.enviarForm(f);
}
function act_buscarActivo()
{
    var w = 'where';
    /*if(_('idSuc').value!='-0'){
        w += ' id_sucursal='+_('idSuc').value;
    }*/
    if(_('idCat').value!='-0'){
        w += w=='where' ? " id_categoria='"+_('idCat').value+"'" : " and id_categoria='"+_('idCat').value+"'";
    }
    var a=_('t_b').value;
    if(a!=''){
        w += w=='where' ? " lower(codigo_activo) like '"+a.toLowerCase()+"%' or lower(descripcion) like '%"+a.toLowerCase()+"%'" : " and (lower(codigo_activo) like '"+a.toLowerCase()+"%' or lower(descripcion) like '%"+a.toLowerCase()+"%')";
    }
    if(w == 'where'){
        w = '';
    } 
    _WR = encodeURI(w);
    new Tabla('d_11', 'jmTbl', '', 'vta_activo', 'CODIGO,CATEGORIA,DESCRIPCION,COSTO,DEPRECIADO', 'id_activo,codigo_activo,categoria,descripcion,valor_compra,valor_depreciado', '0,130,90,200,80,80', _altEdi, 'act_activoEditar(^)', _WR);
}
function act_opImpActivos()
{
    Ventana.crear('vta_im', 'Impresi&oacute;n de Activos', "ancho=600,alto=300,modal=true,cerrar=true");
    _AJAX.solicitud('frmFiltro', 'op=23', false, 'vta_im_html');
}
function pdfImpCodigoBarras()
{
    window.open('pdfCodigoBarrasActivos?dni='+_('an').value, '_blank');
}
function act_verKardex(k)
{
    Ventana.crear('cmp', 'KARDEX DE ACTIVO', "ancho=850,alto=450,modal=true,cerrar=true", '');
    _AJAX.solicitud('frmKardexActivo', 'id='+k, false, 'cmp_html');
}
function act_calcDepresiado()
{
    var val_com = parseFloat(_('valor_compra').value);
    var jS = eval('(' + _('axDep').innerHTML + ')');
    var i = _enMatrizJSON(jS, _('id_tabla_depreciacion').value, 0);
    var p = parseFloat(jS.tbl[i][2]);
    var val_dep = val_com * p / 100 / 12; 
    var F = new Date();
    var hoy = F.getDate()+'/'+(F.getMonth()+1)+'/'+F.getFullYear();
    var fch_hoy = SelectorFecha.getTimestamp(hoy);
    var fch_comp = SelectorFecha.getTimestamp(_('fecha_compra').value);
    var dif = fch_hoy - fch_comp;
    var num_mes = parseInt(dif / 30 / 24 / 60 / 60 / 1000);
    _('num_meses').value = num_mes;
    _('valor_depreciado').value = _RD(val_dep * num_mes);
}
function act_getCodActivo(t, i)
{
    _AJAX.solicitud('getCodigosActivos', 'txt='+t+'&i='+i, false, null);
}
function act_tblCodsActivos(r, x)
{
    if(r!='' && r!='{tbl:]}'){
        setDiv('dv', 250, 250, 'axDes'+x);
        var jS = eval('(' + r + ')');
        var h = '<table cellpadding="4" cellspacing="0" border="0" width="100%">';
        for(var i=0; i<jS.tbl.length; i++){
            h += '<tr valign="top" onmouseover="this.className=\'jm_filaSobre\';this.style.cursor=\'pointer\'" onmouseout="this.className=\'\'" onclick="_(\'des'+x+'\').value=\''+jS.tbl[i][0]+'\';_R(\'dv\');"><td>'+jS.tbl[i][0]+'</td></tr>';
        }
        h += '<table>';
        _('dv').innerHTML = h;
    }else{
        _R('dv');
    }
}


/* KARDEX */
function act_kardex()
{
    encerar();
    _AJAX.solicitud('frmFiltro', 'op=5&fn=act_filtrarKardex()', false, 'd_filtro');
    setAnchoPanel(2, 850);
}
function act_filtrarKardex()
{
    var w = 'where';
    if(_('idSuc').value!='-0'){
        w += ' id_sucursal='+_('idSuc').value;
    }
    if(_('idCat').value!='-0'){
        w += w=='where' ? " id_categoria='"+_('idCat').value+"'" : " and id_categoria='"+_('idCat').value+"'";
    }
    var a=_('t_b').value;
    if(a!=''){
        w += w=='where' ? " lower(codigo_activo) like '"+a.toLowerCase()+"%' or lower(descripcion) like '%"+a.toLowerCase()+"%'" : " and (lower(codigo_activo) like '"+a.toLowerCase()+"%' or lower(descripcion) like '%"+a.toLowerCase()+"%')";
    }
    if(w == 'where'){
        w = '';
    }
    _WR = encodeURI(w);
    new Tabla('d_11', 'jmTbl', '', 'vta_activo', 'CODIGO,CATEGORIA,DESCRIPCION,COSTO,DEPRECIADO', 'id_activo,codigo_activo,categoria,descripcion,valor_compra,valor_depreciado', '0,130,90,200,80,80', _altEdi, 'act_verKardex(^)', _WR);
}
function act_verKardex(k)
{
    var f = new Date();
    var fi = _('fi')!=null ? _('fi').value : f._fecha();
    var ff = _('ff')!=null ? _('ff').value : f._fecha();
    _AJAX.solicitud('frmKardexActivo', 'id='+k+'&altB='+_altBody+'&fi='+fi+'&ff='+ff, false, 'd_21');
}
function act_impKardex(k)
{
    imprimir('pdfKardexActivo?id='+k+'&fi='+_('fi').value+'&ff='+_('ff').value);
}
/* PERSONALIZACION */
function act_personalizacion()
{
    encerar();
    if(_MENUS_._enArreglo("personalNuevo")>=0){
        btn.icono(-360, 2, 34, 34, 'Nueva Personalizaci&oacute;n', "act_personalizacionEditar('-1');", 'd_herra');
    }
    btn.icono(-455, -114, 34, 34, 'Imprimir', "act_impPersonalizacion();", 'd_herra');
    _AJAX.solicitud('frmFiltro', 'op=19&fn=act_buscarPersonalizacion()', false, 'd_filtro');
    setAnchoPanel(2, 20);
}
function act_personalizacionEditar(k)
{
    _('d_ayuda').innerHTML = msg();
    Ventana.crear('cmp', 'PERSONALIZACION DE ACTIVOS', "ancho=820,alto=500,modal=true,cerrar=true", '');
    _AJAX.solicitud('frmPersonalizacion', 'id='+k, false, 'cmp_html');
}
function act_setTipoMov()
{
    _('bodega_entrega').value =  _('dni_ent').value = _('perEnt').value = 
        _('bodega_recibe').value = _('dni_rec').value = _('perRec').value = '';
    _('dni_ent').readOnly = false;
    var t = _('tpyMov').value;
    _('axBod').style.display = t=='2' ? 'none' : 'block';
    if(t=='1'){
        _('dni_ent').value = _('rucEmp').value;
        _('perEnt').value = _('raSoEmp').value;
        _('dni_ent').readOnly = true;
    }
}
function act_getBodega(t, ob)
{
    _AJAX.solicitud('getBodegas', 'txt='+t._trim()+'&fun=act_tblBodegas&obj='+ob, false, null);
}
function act_tblBodegas(r, ob)
{
    if(r != '{tbl:]}'){
        _('ax').innerHTML=r;
        setDiv('dv', 300, 195, 'ax_'+ob);
        _T('dv', 90);
        var jS = eval('(' + r + ')');
        var h = '<table cellpadding="4" cellspacing="0" border="0" width="100%">';
        for(var i=0; i<jS.tbl.length; i++){
            h += '<tr valign="top" onmouseover="this.className=\'jm_filaSobre\';this.style.cursor=\'pointer\'" onmouseout="this.className=\'\'" onclick="act_addBodega(\''+ob+'\', '+i+')"><td>'+jS.tbl[i][1]+'</td><td>'+jS.tbl[i][3]+'</td></tr>';
        }
        h += '<table>';
        _('dv').innerHTML = h;
    }else{
        _R('dv');        
    }
}
function act_addBodega(ob, p)
{
    var jS = eval('(' + _('ax').innerHTML + ')');
    _(ob).value = htmlToEspecial(jS.tbl[p][1]);
    var o = 'dni_rec';
    var o0 = 'idBodRec';
    var o1 = 'perRec';
    var ub = 'axUbRec';
    var idUb = 'ubic_recibe';
    if(ob.indexOf('entrega')>0){
        o = 'dni_ent';
        o0 = 'idBodEnt';
        o1 = 'perEnt';
        ub = 'axUbEnt';
        idUb = 'ubic_entrega';
    }
    _(o0).value = jS.tbl[p][0];
    _(o).value = jS.tbl[p][2];
    _(o1).value = htmlToEspecial(jS.tbl[p][3]);
    _AJAX.solicitud('getUbicaciones', 'id='+jS.tbl[p][0]+'&obj='+ub+'&idName='+idUb, false, null);
    _R('dv');
}
function act_getEmpleado(t, ob)
{
    _AJAX.solicitud('getEmpleadosPersonalizacion', 'txt='+t._trim()+'&fun=act_tblEmpleados&obj='+ob+'&tpy='+_('tpyMov').value, false, null);
}
function act_tblEmpleados(r, ob)
{
    if(r != '{tbl:]}'){
        _('ax').innerHTML=r;
        setDiv('dv', 300, 195, 'ax_'+ob);
        _T('dv', 90);
        var jS = eval('(' + r + ')');
        var h = '<table cellpadding="4" cellspacing="0" border="0" width="100%">';
        for(var i=0; i<jS.tbl.length; i++){
            h += '<tr valign="top" onmouseover="this.className=\'jm_filaSobre\';this.style.cursor=\'pointer\'" onmouseout="this.className=\'\'" onclick="act_addEmpleado(\''+ob+'\', '+i+')"><td>'+jS.tbl[i][0]+'</td><td>'+jS.tbl[i][1]+'</td></tr>';
        }
        h += '<table>';
        _('dv').innerHTML = h;
    }else{
        _R('dv');
    }
}
function act_addEmpleado(ob, p)
{
    var jS = eval('(' + _('ax').innerHTML + ')');
    _(ob).value = jS.tbl[p][0];
    var o1 = ob.indexOf('ent')>0 ? 'perEnt' : 'perRec';
    _(o1).value = htmlToEspecial(jS.tbl[p][1]);
    _R('dv');
}
function act_getActivos(event, t, f, pf)
{
    c = (_esN)?event.which:event.keyCode; 
    pf = pf || ''; 
    var i=0;
    var idActs = '';
    while(_('idAc'+i)!=null){
        idActs += _('idAc'+i).value + ',';
        i++;
    }
    if(idActs!=''){
        idActs = idActs.substring(0, idActs.length-1);
    }
    var idBodEnt = _('idBodEnt')!=null ? _('idBodEnt').value : "";
    if(_('lector').checked){
        if(c==13){
            _AJAX.solicitud('getActivos', 'txt='+t._trim()+'&fun='+f+'&ent='+c+'&pf='+pf+'&idBodEnt='+idBodEnt+"&idActs="+idActs, false, null);
        }
    }else{
        _AJAX.solicitud('getActivos', 'txt='+t._trim()+'&fun='+f+'&ent='+c+'&pf='+pf+'&idBodEnt='+idBodEnt+"&idActs="+idActs, false, null);
    }
}
function act_tblActivos(r, cod, pf)
{
    _R('sonido'); 
    if(r != '' && r != '{tbl:]}'){
        _('ax').innerHTML=r;
        setDiv('dv', 502, 195, pf+'cab0');
        _T('dv', 90);
        var jS = eval('(' + r + ')');
        var h = '<table cellpadding="4" cellspacing="0" width="100%" border="0">';
        for(var i=0; i<jS.tbl.length; i++){
            h += '<tr valign="top" onmouseover="this.className=\'jm_filaSobre\';this.style.cursor=\'pointer\'" onmouseout="this.className=\'\'" onclick="act_addActivo(\''+pf+'\', '+i+')"><td id="axCodP'+i+'">'+jS.tbl[i][1]+'</td><td>'+jS.tbl[i][2]+'</td><td>'+jS.tbl[i][3]+'</td></tr>';
        }
        h += '<table>';
        _('dv').innerHTML = h;
        if(cod == 13){
            act_addActivo(pf, 0);
        }
    }else{
        alerta();
        _R('dv');
        _('busProd').value='';
    }
}
function act_addActivo(pf, x)
{
    _R('sonido');  
    if(_('ax').innerHTML!='' && _('ax').innerHTML!='{tbl:]}'){
        var jS = eval('(' + _('ax').innerHTML + ')');
        var p = pf || '';
        var i = _(p+'tblD').childNodes.length>0 ? parseInt(_(p+'tblD').childNodes.item(_(p+'tblD').childNodes.length-1).id.replace(p+'r', ''))+1 : 0;
        var tr = document.createElement("div");
        tr.id=p+'r'+i;
        tr.className='jm_fila';
        _(p+'tblD').appendChild(tr);
        var h = "<div class='jm_columna caja' style='width:150px' id='cF"+i+"0'>"+jS.tbl[x][1]+"</div>"+
               "<div class='jm_columna caja' style='width:298px'>"+jS.tbl[x][2]+"<input type='hidden' id='idAc"+i+"' name='idAc"+i+"' value='"+jS.tbl[x][0]+"' /></div>";
       if(jS.tbl[x][6]=='t')
       {
           h += "<div class='jm_columna caja' style='width:70px'>"+jS.tbl[x][3]+"<input type='hidden' id='idCom"+i+"' name='idCom"+i+"' value='"+jS.tbl[x][1]+"' /></div>"+
               "<div class='jm_columna caja' style='width:70px'>"+(jS.tbl[x][3]*0.12).toFixed(2)+"</div>"+
               "<div class='jm_columna caja' style='width:70px'>"+((jS.tbl[x][3]*0.12)+(jS.tbl[x][3]*1)).toFixed(2)+"</div>";
       }
       else
       {
           h += "<div class='jm_columna caja' style='width:70px'>"+jS.tbl[x][3]+"<input type='hidden' id='idCom"+i+"' name='idCom"+i+"' value='"+jS.tbl[x][1]+"' /></div>"+
               "<div class='jm_columna caja' style='width:70px'>0.0</div>"+
               "<div class='jm_columna caja' style='width:70px'>"+jS.tbl[x][3]+"</div>";
       }
        
        h += "<div class='jm_columna caja' style='width:70px'>"+jS.tbl[x][5]+"<input type='hidden' id='idDep"+i+"' name='idDep"+i+"' value='"+jS.tbl[x][2]+"' /></div>";
        h += "<div class='jm_columna der' style='width:21px'><div class='jm_icoCon' style='background-position:-545px -18px;' title='Eliminar' onclick=\"_R('r"+i+"');\">&nbsp;</div></div>";
        var iv=0;
        if(_('p_iva1')!=null){
            iv = parseFloat(jS.tbl[x][5]) * _('p_iva1').value / 100;
            h = "<div class='jm_columna caja' style='width:160px' id='"+p+"cF"+i+"0'>"+jS.tbl[x][1]+"</div>" +
                "<div class='jm_columna caja' style='width:226px'><textarea id='"+p+"cpt"+i+"' name='"+p+"cpt"+i+"' style='width:210px;height:26px' onblur=\"this.value=this.value._trim();\">"+jS.tbl[x][2]+"</textarea>"+
                "<input type='hidden' id='"+p+"idPr"+i+"' name='"+p+"idPr"+i+"' value='"+jS.tbl[x][0]+"' />"+
                "<input type='hidden' id='"+p+"idAc"+i+"' name='"+p+"idAc"+i+"' value='"+jS.tbl[x][0]+"' />"+
                "<input type='hidden' id='"+p+"p_iva"+i+"' value='"+_('p_iva1').value+"' />"+
                "<input type='hidden' id='"+p+"p_c"+i+"' name='"+p+"p_c"+i+"' value='"+jS.tbl[x][5]+"' />"+
                "</div>"+
                "<div class='jm_columna caja' style='width:40px'><input type='text' size='2' id='"+p+"c"+i+"' name='"+p+"c"+i+"' value='1' readonly /></div>"+
                "<div class='jm_columna caja' style='width:60px'><input type='text' onkeypress=\"_evaluar(event,'0123456789.')\" value='"+jS.tbl[x][3]+"' size='5' id='"+p+"pu"+i+"' name='"+p+"pu"+i+"' style='text-align:right' onkeyup=\"act_calcTotales('"+p+"', "+i+");fac_sumValores('"+p+"');\" onblur=\"act_calcTotales('"+p+"', "+i+");\" /></div>" +
                "<div class='jm_columna der' style='width:70px'><input type='text' size='5' id='"+p+"st"+i+"' name='"+p+"st"+i+"'  value='"+jS.tbl[x][3]+"' readonly style='text-align:right' /></div>" +
                //"<div class='jm_columna der' style='width:60px'><input type='text' size='5' id='"+p+"d"+i+"' name='"+p+"d"+i+"' value='0' style='text-align:right' onkeyup=\"act_calcTotales('"+p+"', "+i+")\" /></div>" +
                //"<div class='jm_columna der' style='width:60px'><input type='text' size='5' id='"+p+"v"+i+"' name='"+p+"v"+i+"' value='"+iv+"' style='text-align:right' onkeyup=\"act_calcIva('"+p+"', "+i+");fac_sumValores('"+p+"');\" /></div>" +
                "<div class='jm_columna der' style='width:60px'><input type='text' size='5' id='"+p+"v"+i+"' name='"+p+"v"+i+"' value='"+iv+"' style='text-align:right' readonly /></div>" +
                "<div class='jm_columna der' style='width:80px'><input type='text' size='8' id='"+p+"t"+i+"' name='"+p+"t"+i+"'  value='"+_RD(parseFloat(jS.tbl[x][3]) + iv)+"' readonly style='text-align:right' /></div>" +
                "<div class='jm_columna der' style='width:20px' align='center'><div class='jm_icoCon' style='background-position:-545px -18px;' title='Eliminar' onclick=\"_R('"+p+"r"+i+"');fac_sumValores('"+p+"');\">&nbsp;</div></div>";
        }
        tr.innerHTML = h;
        _(p+'sll0').scrollTop = 10000;
        if(_('p_iva1')!=null){
            fac_sumValores('');
        }
    }else{
        alerta();
    }
    _R('dv');
    _('busProd').value='';
    _('busProd').focus();
}
function act_buscarPersonalizacion()
{
    var w = "where fecha between '"+_('fi').value+"' and '"+_('ff').value+"'";
    //if(_('TMov').value!='t'){
        w += " and tipo_movimiento='"+_('TMov').value+"'";
    //}
    var a=_('t_b').value;
    if(a!=''){
        w += " and ( lower(dni_entrega) like '"+a.toLowerCase()+"%' or lower(persona_entrega) like '%"+a.toLowerCase()+"%' or lower(dni_recibe) like '"+a.toLowerCase()+"%' or lower(persona_recibe) like '%"+a.toLowerCase()+"%' )";
    }
    if(_('Acep').value != ''){
        w += " and aceptada="+_('Acep').value;
    }
    _WR = encodeURI(w);
    new Tabla('d_11', 'jmTbl', '', 'vta_activo_personalizacion', 'NRO. DOC.,ENTREGA,RECEPCION,FECHA,MOVIMIENTO', 'id_activo_personalizacion,num_documento,persona_entrega,persona_recibe,fecha,txt_tipo_movimiento', '0,80,200,200,80,200', _altEdi, 'act_personalizacionEditar(^)', _WR);
}
function act_personalizacionGuardar(f)
{
    if(f.nd.value=='' || f.perEnt.value=='' || f.perRec.value==''){
        alert('Todos los campos marcados con un  *  son obligatorios.');
        return false;
    }
    if(f.dni_ent.value == f.dni_rec.value){
        alert('La entrega con la recepción no tienen que ser iguales.');
        return false;
    }
    _('tope').value = _('tblD').childNodes.length>0 ? parseInt(_('tblD').childNodes.item(_('tblD').childNodes.length-1).id.replace('r', ''))+1 : 0;
    if(_('tope').value==0){
        alert('Ingrese por lo menos un activo antes de enviar el formulario.');
        return false;
    }
    return _AJAX.enviarForm(f);
}
function act_personalizacionAceptar(k)
{
    _AJAX.solicitud('frmPersonalizacionAceptar', 'WHERE='+_WR+'&p='+(_objT!=null?_objT.pg:0)+'&id='+k+'&nd='+_('nd').value, false, "body");
}
function act_impPersonalizacion()
{
    var h = "<input type='radio' id='z0' style='display:none' checked value='16' /><input type='radio' id='x' name='x' checked /> PDF &nbsp;&nbsp;&nbsp;&nbsp;&nbsp; <input type='radio' id='x2' name='x' /> Excel "+
            "<br/><br/><input type='button' value='Imprimir' onclick='imprimirReporte()'>";
    Ventana.crear('vta_im', 'Impresi&oacute;n de Movimientos', "ancho=280,alto=100,modal=true,cerrar=true", h);
}
function act_anularPersonalizacion(k)
{
    if(confirm('¿Está seguro(a) de querer anular el formulario de personalización de activos?')){
        _AJAX.solicitud('anularPersonalizacion', 'id='+k, false, 'body');
    }
}
/* VENTA DE ACTIVOS */
function act_ventas()
{
    encerar();
    if(_MENUS_._enArreglo("ventaNueva")>=0){
        btn.icono(-360, 2, 34, 34, 'Nueva Factura de Venta', "act_ventaEditar('-1');", 'd_herra');
    }
    /*if(_MENUS_._enArreglo("ventasImprimir")>=0){
        btn.icono(-455, -114, 34, 34, 'Imprimir', "fac_impVentas()", 'd_herra');
    }*/
    _AJAX.solicitud('frmFiltro', 'op=10&fn=act_buscarVenta()', false, 'd_filtro');
    setAnchoPanel(2, 20);
}
function act_ventaEditar(k)
{
    _('d_ayuda').innerHTML = msg();
    Ventana.crear('cmp', 'VENTA DE ACTIVOS', "ancho=820,alto=550,modal=true,cerrar=true", '');
    new Tab('cmp_html', 750, 445);
    _AJAX.solicitud('frmFacturaVentaActivo', 'id='+k+'&altB=445', false, 'cmp_html');
}
function act_tblActivosVenta(r, cod, pf)
{
    _R('sonido');
    if(r != '' && r != '{tbl:]}'){
        _('ax').innerHTML=r;
        setDiv('dv', 720, 195, pf+'cab0');
        _T('dv', 90);
        var jS = eval('(' + r + ')');
        var h = '<table cellpadding="4" cellspacing="0" width="100%" border="0">'+
            '<tr><th>CODIGO</th><th>DESCRIPCION</th><th>VALOR COMPRA</th><th>VALOR DEPRECIADO</th><th>VALOR VENTA</th></tr>';
        for(var i=0; i<jS.tbl.length; i++){
            h += '<tr valign="top" onmouseover="this.className=\'jm_filaSobre\';this.style.cursor=\'pointer\'" onmouseout="this.className=\'\'" onclick="act_addActivo(\''+pf+'\', '+i+')"><td id="axCodP'+i+'">'+jS.tbl[i][1]+'</td><td>'+jS.tbl[i][2]+'</td><td>'+jS.tbl[i][3]+'</td><td>'+jS.tbl[i][4]+'</td><td>'+jS.tbl[i][5]+'</td></tr>';
        }
        h += '<table>';
        _('dv').innerHTML = h;
        if(cod == 13){
            act_addActivo(pf, 0);
        }
    }else{
        alerta();
        _R('dv');
        _('busProd').value='';
    }
}
function act_calcTotales(pf, i)
{
    var pu = parseFloat(_(pf+'pu'+i).value);
    //var d = parseFloat(_(pf+'des'+i).value);
    var iv = _RD((pu) * parseFloat(_('p_iva1').value) / 100);
    var t = pu+iv;
    _(pf+'st'+i).value = pu;
    _(pf+'v'+i).value = iv;
    _(pf+'t'+i).value = t;
}
/*function act_calcIva(pf, i)
{
    var pu = parseFloat(_(pf+'pu'+i).value);
    //var d = parseFloat(_(pf+'des'+i).value);
    var axiv = _(pf+'v'+i).value || 0;
    var iv = parseFloat(axiv);
    _(pf+'t'+i).value = pu+iv;
}*/
function act_buscarVenta()
{
    var w = "where fecha_emision between '"+_('fi').value+"' and '"+_('ff').value+"' and de_activo=true";
    if(_('idSuc').value!='-0'){
        w += " and id_sucursal="+_('idSuc').value;
    }
    if(_('t_b').value!=''){
        w += " and (num_factura::varchar like '"+_('t_b').value+"%' or lower(ruc) like '"+_('t_b').value.toLowerCase()+"%' or lower(razon_social) like '%"+_('t_b').value.toLowerCase()+"%')";
    }
    _WR = encodeURI(w);
    new Tabla('d_11', 'jmTbl', '', 'vta_factura_venta', 'RUC,CLIENTE,# FACTURA,F. EMISION,TOTAL VENTA,ANULADA,ESTADO SRI', 'id_factura_venta,ruc,razon_social,numero_factura,toDateSQL(fecha_emision),total,txt_anulado,txt_estado_documento', '0,90,200,100,80,80,80,110', _altEdi, 'act_ventaEditar(^);', _WR);
}
function act_ventaAnular(i)
{
    if(confirm('Se anularán también todos los documentos asociados a la factura tales como: retenciones y comprobantes de cobros.')){
        _AJAX.solicitud('frmFacturaVentaActivoAnular', 'id='+i+'&ser='+_('sf').value+'&num='+_('nf').value, true, 'body');
    }
}
/*   PERDIDA DE ACTIVOS   */
function act_perdidas()
{
    encerar();
    if(_MENUS_._enArreglo("perdida_activo_registrar")>=0){
        btn.icono(-360, 2, 34, 34, 'Registrar p&eacute;rdida de activo', "act_perdidaEditar('-1');", 'd_herra');
    }
    if(_MENUS_._enArreglo("perdida_activo_contabilizar")>=0){
        btn.separador('d_herra');
        btn.icono(-240, -189, 34, 34, 'Contabilizar p&eacute;rdida de activos', "act_perdidaEditar('-1');", 'd_herra');
    }
    _AJAX.solicitud('frmFiltro', 'op=31&fn=act_perdidaBuscar()', false, 'd_filtro');
    setAnchoPanel(2, 20);
}
function act_tblActivoPerdida(r, cod)
{
    _R('sonido');
    if(r != '' && r != '{tbl:]}'){
        _('ax').innerHTML=r;
        setDiv('dv', 470, 195, 'InAr');
        _T('dv', 90);
        var jS = eval('(' + r + ')');
        var h = '<table cellpadding="4" cellspacing="0" width="100%" border="0">'+
            '<tr><th>CODIGO</th><th>DESCRIPCION</th><th>VALOR COMPRA</th><th>VALOR DEPRECIADO</th></tr>';
        for(var i=0; i<jS.tbl.length; i++){
            h += '<tr valign="top" onmouseover="this.className=\'jm_filaSobre\';this.style.cursor=\'pointer\'" onmouseout="this.className=\'\'" onclick="act_setActivoPerdida('+i+')"><td id="axCodP'+i+'">'+jS.tbl[i][1]+'</td><td>'+jS.tbl[i][2]+'</td><td>'+jS.tbl[i][3]+'</td><td>'+jS.tbl[i][4]+'</td></tr>';
        }
        h += '<table>';
        _('dv').innerHTML = h;
        if(cod == 13){
            act_setActivoPerdida(0);
        }
    }else{
        alerta();
        _R('dv');
        _('busProd').value='';
    }
}
function act_setActivoPerdida(x)
{
    if(_('ax').innerHTML!='' && _('ax').innerHTML!='{tbl:]}'){
        var jS = eval('(' + _('ax').innerHTML + ')');
        _('id_activo').value = jS.tbl[x][0];
        _('descripcion').value = jS.tbl[x][2];
        _('perdida').value = parseFloat(jS.tbl[x][3]) - parseFloat(jS.tbl[x][4]);
        _('axAct').innerHTML = "<table><tr><td>CODIGO: </td><td>"+jS.tbl[x][1]+"</td></tr>"+
            "<tr><td>DESCRIPCION: </td><td>"+jS.tbl[x][2]+"</td></tr>"+
            "<tr><td>VALOR DE LA COMPRA: </td><td>"+jS.tbl[x][3]+"</td></tr>"+
            "<tr><td>VALOR DEPRECIADO: </td><td>"+jS.tbl[x][4]+"</td></tr>"+
            "<tr><td>VALOR PERDIDA: </td><td>"+_RD(_('perdida').value)+"</td></tr>"+
            "<tr><td>MOTIVO: </td><td><textarea id='motivo' name='motivo' style='width:340px;height:35px'></textarea></td></tr></table>";
        _('motivo').focus();
    }
    _R('dv');
}
function act_perdidaEditar(k)
{
    _('d_ayuda').innerHTML = msg();
    Ventana.crear('cmp', 'PERDIDA DE ACTIVOS', "ancho=510,alto=280,modal=true,cerrar=true", '');
    _AJAX.solicitud('frmPerdidaActivo', 'id='+k+'&altB=445', false, 'cmp_html');
}
function act_perdidaGuardar(f)
{
    var id = _('id_activo').value;
    if(id=='-1' || id=='' || _('motivo').value==''){
        alert('Debe seleccionar un activo e ingresar el motivo de la vaja antes de registrar el formulario.');
        return false;
    }
    if(confirm('Está seguro(a) de dar de vaja el activo?')){
        return _AJAX.enviarForm(f);
    }
    return false;
}
function act_perdidaBuscar()
{
    var w = "where fecha_registro between '"+_('fi').value+"' and '"+_('ff').value+"'";
    if(_('idSuc').value!='-0'){
        w += ' and id_sucursal='+_('idSuc').value;
    }
    if(_('idCat').value!='-0'){
        w += " and id_categoria='"+_('idCat').value+"'";
    }
    var a=_('t_b').value;
    if(a!=''){
        w += " and (lower(codigo_activo) like '"+a.toLowerCase()+"%' or lower(descripcion) like '%"+a.toLowerCase()+"%')";
    }
    _WR = encodeURI(w); 
    new Tabla('d_11', 'jmTbl', '', 'vta_activo_perdida', 'CODIGO,CATEGORIA,DESCRIPCION,F. PERDIDA,MONTO', 'id_activo_perdida,codigo_activo,categoria,descripcion,toDateSQL(fecha_registro),valor_perdida', '0,150,100,200,80,60', _altEdi, 'act_perdidaEditar(^);', _WR);
}
function act_perdidaAnular(k)
{
    _AJAX.solicitud('frmPerdidaActivoAnular', 'id='+k, false, 'body');
}

/* FUNCIONES COMPRAS */
function setDiv(id, an, al, ob)
{
    if(_(id)==null){
        var d = document.createElement('DIV');
        d.id = id;
        d.className='msg';
        d.style.position='absolute';
        d.style.zIndex=999;
        d.style.overflow='auto';
        d.style.width=an+'px';
        d.style.height=al+'px';
        d.onmouseover = function(){_Fch = false;};
        d.onmouseout = function(){_Fch = true;};
        document.body.onclick=function(){
            if(_Fch){
                _R(id);
            }
        };
        ob = ob || '';
        if(ob!=''){
            _(ob).appendChild(d);
        }else{
            document.body.appendChild(d);
        }
    }
}
function alerta()
{
    if(_esIE){
        var objecto = document.createElement('bgsound');
        objecto.id='sonido';
        objecto.src='error.mp3';
    }else{
        var objecto = document.createElement('div');
        objecto.id='sonido';
        objecto.style.position='absolute';
        objecto.style.display='none';
        var pam = '<object><param name="hidden" value="true">';
        pam += '<param name="loop" value="false">';
        pam += '<param name="autostart" value="true">';
        pam += '<param name="src" value="error.mp3">';
        pam += '<embed src="error.mp3" autostart="true" loop="false" hidden="true"></object>';
        objecto.innerHTML = pam;
    }
    document.body.appendChild(objecto);
}
function inv_setLector()
{
    _AJAX.solicitud('setLector', 'l='+_('lector').checked, false, null);
}
function inv_setMsg(o)
{
    if(_('mSg')==null){
        var d = document.createElement('DIV');
        d.id = 'mSg';
        d.className='msg';
        d.style.visibility='hidden';
        if(_esIE){
            var r = _(o).size;
            var x = (r=='' || r==0) ? 160 : parseInt(r + (Math.E  * r * 2) / 0.79);
            document.body.appendChild(d);
            d.style.top=event.y + 10;
            d.style.left=event.x - x;
        }else{
            _(o).parentNode.appendChild(d);
        }
    }
}
function inv_setProveedor(p)
{
    var jS = eval('(' + _('axp').innerHTML + ')');
    _('coP').value = jS.tbl[p][0];
    _('busProv').value = htmlToEspecial(jS.tbl[p][2]);
    if(_('tpyDoc').value=='f'){
        _('sf').value = jS.tbl[p][3];
        _('auf').value = jS.tbl[p][4];
        _('fv').value = jS.tbl[p][5];
    }
    _R('dv');
    inv_setDetalleCompra();
    if(_('InAr')!=null){
        _('InAr').style.visibility='visible';
        _('busProd').focus();
    }
}
function inv_getProveedores(t)
{
    _AJAX.solicitud('getProveedores', 'txt='+t, false, null);
}
function inv_tblProveedor(r)
{
    if(r!='' && r!='{tbl:]}'){
        _('axp').innerHTML=r;
        setDiv('dv', 250, 250, 'aux_coP');
        var jS = eval('(' + r + ')');
        var h = '<table cellpadding="4" cellspacing="0" border="0" width="100%">';
        for(var i=0; i<jS.tbl.length; i++){
            h += '<tr valign="top" onmouseover="this.className=\'jm_filaSobre\';this.style.cursor=\'pointer\'" onmouseout="this.className=\'\'" onclick="inv_setProveedor('+i+')"><td>'+jS.tbl[i][1]+'</td><td>'+jS.tbl[i][2]+'</td></tr>';
        }
        h += '<table>';
        _('dv').innerHTML = h;
    }else{
        _R('dv');
    }
}
function inv_setDetalleCompra()
{
    var tp = _('tpyDoc').value;
    if(tp=='f'){
        _('ob').value = "Registro de compromiso de compra Factura No. " + _('sf').value + "-" + _('nf').value + " de "+_('busProv').value+".";
    }else if(tp=='l'){
            _('ob').value = "Registro de Liquidación de compra o servicio No. " + _('sf').value + "-" + _('nf').value + " de "+_('busProv').value+".";
    }else if(tp=='v'){
        _('ob').value = "Registro de nota de venta de compra No. " + _('sf').value + "-" + _('nf').value + " de "+_('busProv').value+".";
    }
}
function inv_getProductos(event, t, f)
{
    var c = _getKeyCode(event);
    _AJAX.solicitud('getProductos', 'txt='+t._trim()+'&fun='+f+'&ent='+c, false, null);
}
function inv_tblProductos(r, cod)
{
    _R('sonido');
    if(r != '{tbl:]}'){
        _('ax').innerHTML=r;
        setDiv('dv', 690, 195, 'sll0');
        _T('dv', 90);
        var jS = eval('(' + r + ')');
        var h = '<table cellpadding="4" cellspacing="0" border="0" width="100%">';
        for(var i=0; i<jS.tbl.length; i++){
            h += '<tr valign="top" onmouseover="this.className=\'jm_filaSobre\';this.style.cursor=\'pointer\'" onmouseout="this.className=\'\'" onclick="inv_addProducto(\'\', '+i+')"><td>'+jS.tbl[i][1]+'</td><td>'+jS.tbl[i][2]+'</td></tr>';
        }
        h += '<table>';
        _('dv').innerHTML = h;
        /*if(cod == 13){
            inv_addProducto('', 0);
        }*/
    }else{
        alerta();
        _R('dv');
        _('busProd').value='';
    }
}
function inv_addProducto(pf, x)
{
    _R('sonido');
    if(_('ax').innerHTML!='' && _('ax').innerHTML!='{tbl:]}'){
        var jS = eval('(' + _('ax').innerHTML + ')');
        var p = pf || '';
        var i = _(p+'tblD').childNodes.length>0 ? parseInt(_(p+'tblD').childNodes.item(_(p+'tblD').childNodes.length-1).id.replace(p+'r', ''))+1 : 0;
        var tr = document.createElement("tr");
        tr.id=p+'r'+i;
        tr.className='jm_filaPar';
        _(p+'tblD').appendChild(tr);
        /*var cl = _('ax').cloneNode(true);
        cl.innerHTML = cl.innerHTML.replace(/_Xx_/g, p+'idA'+i);
        cl.innerHTML = cl.innerHTML.replace(/_Zz_/g, "'"+p+"c"+i+"'");
        var h = "<td width='230'>"+cl.innerHTML+"</td>"+*/ 
        var h = "<td width='230' id='codP"+i+"'><input type='hidden' id='"+p+"idA"+i+"' name='"+p+"idA"+i+"' value='"+jS.tbl[x][0]+"'/>"+
                    "<input type='hidden' id='"+p+"pi"+i+"' name='"+p+"pi"+i+"' value='"+jS.tbl[x][3]+"'/>"+jS.tbl[x][1]+" &nbsp; "+jS.tbl[x][2]+"</td>"+
                    "<td align='right' width='50'><input id='"+p+"c"+i+"' name='"+p+"c"+i+"' type='text' class='inpTbl' size='5' value='1' onkeypress=\"_numero(event);\" onfocus='this.select();' onkeyup=\"inv_sumSubTotal('"+p+"', "+i+");inv_sumTotales('"+p+"');\" /></td>"+
                    "<td align='right' width='81'><input id='"+p+"pu"+i+"' name='"+p+"pu"+i+"' type='text' class='inpTbl' size='9' onkeypress=\"_evaluar(event, '0123456789.');\" onfocus='this.select();' onkeyup=\"inv_sumSubTotal('"+p+"', "+i+");inv_sumTotales('"+p+"');\" /></td>"+
                    "<td align='right' width='81'><input id='"+p+"st"+i+"' name='"+p+"st"+i+"' type='text' class='inpTbl' size='9' onkeypress=\"_evaluar(event, '0123456789.');\" onfocus='this.select();' onkeyup=\"inv_sumTotales('"+p+"');\" /></td>";
        if(_('cabDes')!=null){
            h += "<div class='columna der' style='width:71px'><input id='"+p+"d"+i+"' "+(p=='nd' ? 'readonly' : '')+" name='"+p+"d"+i+"' type='text' class='inpTbl' size='9' value='0' onkeypress=\"_evaluar(event, '0123456789.');\" onfocus='this.select();' onkeyup=\"inv_sumSubTotal('"+p+"', "+i+");inv_sumTotales('"+p+"');\" /></div>";
        }
        h += "<td align='right' width='71'><input id='"+p+"v"+i+"' name='"+p+"v"+i+"' type='text' class='inpTbl' size='9' onkeypress=\"_evaluar(event, '0123456789.');\" onfocus='this.select();' onkeyup=\"inv_sumTotales('"+p+"');\" /></td>"+
                    "<td align='right' width='91'><input id='"+p+"t"+i+"' name='"+p+"t"+i+"' type='text' class='inpTbl' size='9' onkeypress=\"_evaluar(event, '0123456789.');\" onfocus='this.select();' onkeyup=\"inv_sumTotales('"+p+"');\" /></td>"+
                    "<td align='center' width='21'><div class='jm_icoCon' style='background-position:-545px -18px;float:right;' title='Eliminar' onclick=\"_R('"+p+"r"+i+"');inv_sumTotales('"+p+"');\">&nbsp;</div></td>";
        tr.innerHTML = h;
        _(p+'sll0').scrollTop = 10000;
        _R('dv');
        _('busProd').value='';
    }else{
        alerta();
        _R('dv');
        _('busProd').value='';
    }
}
function inv_sumSubTotal(p, i)
{
    var c = (_(p+'c'+i).value!='') ? _(p+'c'+i).value : 0;
    var pu = (_(p+'pu'+i).value!='') ? _(p+'pu'+i).value : 0;
    var st = _RD(parseInt(c)*parseFloat(pu));
    var d = 0;
    if(_(p+'d'+i)!=null){
        d = (_(p+'d'+i).value!='') ? parseFloat(_(p+'d'+i).value) : 0;
    }
    //var iv = (_(p+'idA'+i).options[_(p+'idA'+i).selectedIndex].text.indexOf('~')>0) ? _RD((st-d)*12/100) : 0;
    var iv = _RD((st-d)*parseFloat(_(p+'pi'+i).value)/100);
    /*if(p=='A'){subtotal_
        iv = _RD((st-d)*parseFloat(_('p_iva1').value)/100);
    }*/
    if(_('tpyDoc').value=='v'){
        iv=0;
    }
    _(p+'st'+i).value = st;
    _(p+'v'+i).value = iv;
    _(p+'t'+i).value = _RD(st+iv-d);
}
function inv_sumTotales(p)
{ 
    var i=0;
    var st = 0;
    var st0 = 0;
    var st2 = 0;
    var iv2=0;
    var d=0;
    var t = 0;
    while(i<50){
        if(_(p+'c'+i)!=null){
            st += parseFloat(_(p+'st'+i).value) || 0;
            if(parseFloat(_('pi'+i).value)==0){
                st0 += parseFloat(_(p+'st'+i).value) || 0;
            }
            if(parseFloat(_('pi'+i).value)==12){
                st2 += parseFloat(_(p+'st'+i).value) || 0;
                if(_(p+'v'+i)!=null){
                    iv2 += parseFloat(_(p+'v'+i).value) || 0;
                }
            }
            if(_(p+'d'+i)!=null){
                d += parseFloat(_(p+'d'+i).value) || 0;
            }
            t += parseFloat(_(p+'t'+i).value) || 0;
        }
        i++;
    }
    if(_(p+'subt')!=null){
        _(p+'subt').value = _RD(st);
    }
    if(_(p+'dt')!=null){
        _(p+'dt').value = _RD(d);
    }
    if(_(p+'subt0')!=null){
        _(p+'subt0').value = _RD(st0);
    }
    if(_(p+'subt2')!=null){
        _(p+'subt2').value = _RD(st2);
    }
    if(_(p+'iva2')!=null){
        _(p+'iva2').value = _RD(iv2);
    }
    var prp = _('propina')!=null ? _('propina').value : 0;
    _(p+'tt').value = _RD( t + parseFloat(prp) );
    _('InRe').style.visibility = (t>0) ? 'visible' : 'hidden';
}
function inv_addRetencion(p, d)
{
    var pf = p || 0;
    var dt = d || '';
    var i=0;
    while(_('rRe'+i)!=null){
        i++;
    }
    var tr = document.createElement("tr");
    tr.id='rRe'+i;
    tr.className='jm_filaPar';
    _('tblR').appendChild(tr);
    var bi = _('axBI').cloneNode(true);
    bi.innerHTML = bi.innerHTML.replace(/9a0/g, 'codi'+i);
    bi.innerHTML = bi.innerHTML.replace(/9b0/g, '');
    bi.innerHTML = bi.innerHTML.replace(/9c0/g, i);
    bi.innerHTML = bi.innerHTML.replace(/9d0/g, i);
    bi.innerHTML = bi.innerHTML.replace(/9e0/g, 'bi'+i);
    var cl = _('axCR').cloneNode(true);
    cl.innerHTML = cl.innerHTML.replace(/9a0/g, 'idR'+i);
    cl.innerHTML = cl.innerHTML.replace(/9b0/g, '');
    cl.innerHTML = cl.innerHTML.replace(/9c0/g, i);
    cl.innerHTML = cl.innerHTML.replace(/9d0/g, i);
    cl.innerHTML = cl.innerHTML.replace(/9e0/g, 'bi'+i);
    tr.innerHTML = "<td width='170'>"+bi.innerHTML+"</td>"+
                "<td width='"+(pf==0?'291':'290')+"'>"+cl.innerHTML+"</td>"+
                "<td align='right' width='"+(pf==0?'121':'90')+"'><input id='bi"+i+"' name='bi"+i+"' type='text' class='inpTbl' size='"+(pf==0?'13':'9')+"' onkeypress=\"_evaluar(event, '0123456789.');\" onfocus='this.select();' onkeyup=\"inv_sumRetSubTotales("+i+",0);inv_sumRetTotales();"+dt+"\" /></td>"+
                "<td align='right' width='"+(pf==0?'121':'90')+"'><input id='vr"+i+"' name='vr"+i+"' type='text' class='inpTbl' size='"+(pf==0?'13':'9')+"' onkeypress=\"_evaluar(event, '0123456789.');\" onfocus='this.select();' onkeyup=\"inv_sumRetTotales();"+dt+"\" /></td>"+
                "<td align='center' width='21'><div class='jm_icoCon' style='background-position:-545px -18px;float:right;' title='Eliminar' onclick=\"_R('rRe"+i+"');inv_sumRetTotales();"+dt+"\">&nbsp;</div></td>";
                inv_sumRetSubTotales(i,1);
                inv_sumRetTotales();
    _('sll1').scrollTop = 500;
}
function inv_sumRetSubTotales(i,o)
{
    var b = 0;
    var p = 0;
    var r = 0;
    if(_('bi'+i)!=null){
        /*if(o==1){
            if(_('iva2') != null && _('subt2')!=null){
                b = (_('idR'+i).options[_('idR'+i).selectedIndex].text.indexOf('IVA')>=0) ? parseFloat(_('iva2').value) : parseFloat(_('subt').value);
                _('bi'+i).value = b;
            }else{
                if(_('tpyDoc')!=null){
                    if(_('tpyDoc').value=='v'){
                        _('bi'+i).value = b = parseFloat(_('tt').value);
                    }
                }
            }
        }else{
            b = _('bi'+i).value;
        }*/
        if(o==1){
            if(_('subt2')!=null){
                switch( parseInt(_('codi'+i).value) ){
                    case 1:
                        b = parseFloat( _('subt2').value );
                    break;
                    case 2:
                        b = parseFloat( _('subt0').value );
                    break;
                    case 3:
                        if(_('subt6')!=null){
                            b = parseFloat( _('subt6').value );
                        }
                    break;
                    case 4:
                        b = _RD(parseFloat( _('subt').value ) - parseFloat( _('dt').value ));
                    break;
                    case 5:
                        b = parseFloat( _('dt').value );
                    break;
                    case 6:
                        b = 0;   //  ICE
                    break; 
                    case 7:
                        b = parseFloat( _('iva2').value );
                    break;
                    case 8:
                        b = parseFloat( _('propina').value );
                    break;
                    default:
                        b = parseFloat( _('tt').value );
                }
                _('bi'+i).value = b;
            }
        }else{
            b = parseFloat(_('bi'+i).value);
        }
        var vp = _('idR'+i).options[_('idR'+i).selectedIndex].text.split("/");
        p = parseFloat(vp[2]._trim());
        r = _RD(b*p/100); 
        _('vr'+i).value = r;
    }
}
function inv_sumRetTotales()
{
    var i=0;
    var vr = 0;
    while(i<10){
        if(_('vr'+i)!=null){
            vr += _RD(_('vr'+i).value);
        }
        i++;
    }
    _('tvr').value = _RD(vr);
}
function inv_setConcepto(i)
{
    var d = _('mSg');
    d.style.visibility='visible';
    var jS = eval('(' + _('axC').innerHTML + ')');
    var p = _enMatrizJSON(jS, _('idR'+i).value, 0);
    d.innerHTML = (p>=0) ? jS.tbl[p][1] : '';
}
/* COMPRAS */
function inv_compras()
{
    encerar();
    if(_MENUS_._enArreglo("compraNueva")>=0){
        btn.icono(-360, 2, 34, 34, 'Registrar Nueva Factura de Compra', "inv_compraEditar('-1');", 'd_herra');
    }
    btn.icono(-455, -114, 34, 34, 'Imprimir', "inv_impCompras();", 'd_herra');
    _AJAX.solicitud('frmFiltro', 'op=6&fn=inv_buscarCompra()', false, 'd_filtro');
    setAnchoPanel(2, 20);
}
function inv_compraEditar(k)
{
    _('d_ayuda').innerHTML = msg() + '<br/> Para poder emitir la retenc&oacute;n primero debe guardar la factura.';
    Ventana.crear('cmp', 'COMPRAS - GASTOS', "ancho=820,alto=550,modal=true,cerrar=true", '');
    new Tab('cmp_html', 750, 445);
    _AJAX.solicitud('frmFacturaCompra', 'id='+k+'&act=frmFacturaCompraGuardar', false, 'cmp_html');
}
function inv_compraGuardar(f)
{
    var auf = _('auf').value;
    var sf = _('sf').value;
    var nf = _('nf').value;
    var idP = _('coP').value;
    var fc = _('fc').value;
    var fv = _('fv').value;
    var ob = _('ob').value;
    var stt = _('subt').value;
    var tt = _('tt').value;
    if(auf=='' || sf=='' || nf=='' || idP=='-0' || fc=='' || fv=='' || ob=='' || stt=='' || tt==''){
        alert('Todos los campos marcados con  *  son obligatorios.');
        _('btn1').disabled=_('btn2').disabled=false;
        return false;
    }
    if(SelectorFecha.getTimestamp(fc) > SelectorFecha.getTimestamp(fv)){
        alert('La fecha de compra (emisión) debe ser menor o igual a la fecha de validez de la factura.');
        _('btn1').disabled=_('btn2').disabled=false;
        return false;
    }
    var idA = '';
    var c = '';
    var pu = '';
    var st = '';
    var d = '';
    var v = '';
    var t = '';
    var fl = false;  
    var lm = _('tblD').childNodes.length>0 ? parseInt(_('tblD').childNodes.item(_('tblD').childNodes.length-1).id.replace('r', ''))+1 : 0;
    _('tope').value = lm; 
    var i=0;
    for(i=0; i<=lm; i++){
        if(_('c'+i)!=null){
            idA += _('idA'+i).value+',';
            c += _('c'+i).value+',';
            pu += _('pu'+i).value+',';
            st += _('st'+i).value+',';
            d += _('d'+i).value+',';
            v += _('v'+i).value+',';
            t += _('t'+i).value+',';
            fl = true;
        }
    }
    if(!fl){
        alert('Por favor, ingrese el detalle de la factura.');
        _('btn1').disabled=_('btn2').disabled=false;
        return false;
    }
    if(fl){
        idA = idA.substring(0, idA.length-1);
        c = c.substring(0, c.length-1);
        pu = pu.substring(0, pu.length-1);
        st = st.substring(0, st.length-1);
        d = d.substring(0, d.length-1);
        v = v.substring(0, v.length-1);
        t = t.substring(0, t.length-1);
    }
    var idR = '';
    var bi = '';
    var vr ='';
    i=0;
    fl = false;
    while(i<10){
        if(_('idR'+i)!=null){
            idR += _('idR'+i).value+',';
            bi += _('bi'+i).value+',';
            vr += _('vr'+i).value+',';
            fl = true;
        }
        i++;
    }
    if(fl){
        var sr = _('sr').value;
        //var autr = _('autr').value;
        var nr = _('nr').value;
        var fre = _('fre').value;
        var ef = _('ef').value;
        if(sr=='' || nr=='' || fre=='' || ef=='' || _('tvr').value==''){
            alert('Por favor, ingrese todos los datos para la retención.');
            //_('btn1').disabled=_('btn2').disabled=false;
            _('btn1').disabled=false;
            return false;
        }
        if(SelectorFecha.getTimestamp(_('fre').value) < SelectorFecha.getTimestamp(_('fc').value)){
            alert('La fecha de emisión de la retención debe ser mayor o igual a la fecha de compra (emisión) de la factura.');
            //_('btn1').disabled=_('btn2').disabled=false;
            _('btn1').disabled=false;
            return false;
        }
        idR = idR.substring(0, idR.length-1);
        bi = bi.substring(0, bi.length-1);
        vr = vr.substring(0, vr.length-1);
        /*if(SelectorFecha.getTimestamp(_('fre').value) > SelectorFecha.getTimestamp(_('feCad').value)){
            alert("Por favor revise la fecha de caducidad del libretín de retenciones.");
            _('btn1').disabled=_('btn2').disabled=false;
            return false;
        } */   
        if( nr!=0 && (parseInt(nr) < parseInt(_('secDe').value) || parseInt(nr) > parseInt(_('secHa').value)) ){
            alert("Por favor revise el número secuencial de la retención.");
            //_('btn1').disabled=_('btn2').disabled=false;
            _('btn1').disabled=false;
            return false;
        } 
    }
    return _AJAX.enviarForm(f);
}
function inv_buscarCompra()
{
    var w = "where de_activo=false and id_factura_compra not in(select distinct id_factura_compra from tbl_factura_importacion) and fecha_compra between '"+_('fi').value+"' and '"+_('ff').value+"'";
    if(_('idPrv').value!='-0'){
        w += " and id_proveedor="+_('idPrv').value;
    }
    _WR = encodeURI(w); 
    new Tabla('d_11', 'jmTbl', '', 'vta_factura_compra_retencion', 'PROVEEDOR,# FACTURA,F. COMPRA,TOTAL COMPRA,ESTADO,ESTADO RETENCION,MENSAJE', 'id_factura_compra,razon_social,numero_factura,toDateSQL(fecha_compra),total_compra,txt_anulado,txt_estado_documento,mensaje', '0,200,100,80,80,80,80,200', _altEdi, 'inv_compraEditar(^);', _WR);
}
function inv_compraAnular(i, k)
{
    if(confirm('Se anularán también todos los documentos asociados a la factura tales como: retenciones y comprobantes de pagos.')){
        _AJAX.solicitud('frmFacturaCompraAnular', 'id='+i+'&deKa='+k, true, 'body');
    }
}
function inv_retencionAnular(k, i)
{
    if(confirm('¿Está seguro de anular el comprobante de retención?')){
        _AJAX.solicitud('retencionEmitidaAnular', 'idFact='+k+'&id='+i, true, 'body');
    }
}
function inv_getFrmProveedor(d)
{
    _('d_ayuda').innerHTML = msg();
    Ventana.crear('vtPrv', 'PROVEEDOR', "ancho=480,alto=480,modal=true,cerrar=true", '');
    _AJAX.solicitud('frmProveedor', 'id=-1&act=frmProveedorFactGuardar&doc='+d, false, 'vtPrv_html');
}
function inv_getFrmProducto()
{
    if(_('coP').value=='-0'){
        alert('Debe seleccionar un proveedor para poder ingresar un nuevo producto.');
        return false;
    }
    _('d_ayuda').innerHTML = msg();
    Ventana.crear('vtPrd', 'PRODUCTO', "ancho=380,alto=410,modal=true,cerrar=true", '');
    _AJAX.solicitud('frmProductoFact', 'id=-1&idP='+_('coP').value, false, 'vtPrd_html');
}
function inv_frmProductoGuardar(f)
{
    if(_('idC')==null){
        return false;
    }
    var c = _('c').value;
    var de = _('de').value;
    var um = _('um').value;
    var idC = _('idC').value;
    var stm = _('stm').value;
    if(c=='' || de=='' || um=='' || idC=='' || stm==''){
        alert('Todos los campos marcados con  *  son obligatorios.');
        return false;
    }
    return _AJAX.enviarForm(f);
}
function inv_subirFacturaProveedor()
{
    Ventana.crear('vta_subir', 'Factura electr&oacute;nica', "ancho=440,alto=110,modal=true,cerrar=true", '');
    _AJAX.solicitud('SubirArchivo', 'pr=ProcesaFacturaCompra', true, 'vta_subir_html');
}
function inv_setFacturaProveedor(m, xml)
{
    if(m==0){
        _R('bloq_vta_subir');
        _R('vta_subir');
        _AJAX.solicitud('frmFacturaCompra', 'id=-1&act=frmFacturaCompraGuardar&xml='+xml, false, 'cmp_html');
    }else{
        alert(m);
    }
    _('cargando').style.display = 'none';
    _('frmArchivo').style.display = 'block';
    _('archivo').value='';
    return true;
}
function inv_impCompras()
{
    var h = "<form onsubmit='return reporte(this)' autocomplete='off'>" + setRadio('z', '{tbl:[{0:"Compras",1:"Compras"}]}') +
        //'<hr /><div id="grFch">Desde: '+setFecha('fini')+'<br />Hasta: '+setFecha('ffin')+'</div>' +
        setOculto('fini', _('fi').value) + setOculto('ffin', _('ff').value) + setOculto('id_prov', _('idPrv').value) +
        setPieReporte() +"</form>";
    Ventana.crear('vta_im', 'REPORTES DE COMPRAS', "ancho=280,alto=180,modal=true,cerrar=true", h);
}
/* NOTA DE CREDITO EN COMPRA */
function inv_notaCredito(f)
{
    var sr = _('ncSerie').value;
    var nu = _('ncNum').value;
    var au = _('ncAut').value;
    var rm = _('ncRaMo').value;
    var vm = _('ncsubt').value;
    var tt = _('nctt').value;
    if(sr=='' || nu=='' || au=='' || rm=='' || vm=='' || tt=='' || parseFloat(tt)<=0){
        alert('Todos los campos son obligatorios.');
        return false;
    }
    _('nctope').value = _('nctblD').childNodes.length>0 ? parseInt(_('nctblD').childNodes.item(_('nctblD').childNodes.length-1).id.replace('ncr', ''))+1 : 0;
    f.action = 'frmNotaCreditoGuardar';
    return _AJAX.enviarForm(f);
}
/* NOTA DE CREDITO EN COMPRA */
function fac_notaDebito(f)
{
    var sr = _('ndSerie').value;
    var nu = _('ndNum').value;
    var au = _('ndAut').value;
    var rm = _('ndRaMo').value;
    var vm = _('ndst').value;
    var tt = _('ndtt').value;
    if(sr=='' || nu=='' || au=='' || rm=='' || vm=='' || tt=='' || parseFloat(tt)<=0){
        alert('Todos los campos son obligatorios.');
        return false;
    }
    _('ndtope').value = _('ndtblD').childNodes.length>0 ? parseInt(_('ndtblD').childNodes.item(_('ndtblD').childNodes.length-1).id.replace('ndr', ''))+1 : 0;
    f.action = 'frmNotaDebitoGuardar';
    return _AJAX.enviarForm(f);
}
/* LIQUIDACIONES */
function inv_liquidaciones()
{
    encerar();
    if(_MENUS_._enArreglo("liquidacionNueva")>=0){
        btn.icono(-360, 2, 34, 34, 'Nueva Liquidaci&oacute;n de compra o servicio', "inv_liquidacionEditar('-1');", 'd_herra');
    }
    btn.icono(-455, -114, 34, 34, 'Imprimir', "inv_impLiquidaciones();", 'd_herra');
    _AJAX.solicitud('frmFiltro', 'op=6&fn=inv_liquidacionBuscar()', false, 'd_filtro');
    setAnchoPanel(2, 20);
}
function inv_liquidacionEditar(k)
{
    _('d_ayuda').innerHTML = msg();    
    Ventana.crear('vta1', 'LIQUIDACION DE COMPRAS O SERVICIOS', "ancho=800,alto=500,modal=true,cerrar=true");
    new Tab('vta1_html', 820, 545);
    _AJAX.solicitud('frmLiquidacionCompra', 'id='+k, false, 'vta1_html');
}
function inv_liquidacionGuardar(f)
{
    var auf = _('auf').value;
    var sf = _('sf').value;
    var nf = _('nf').value;
    var idP = _('coP').value;
    var fc = _('fem').value;
    var ob = _('ob').value;
    var stt = _('subt').value;
    var tt = _('tt').value;
    if(auf=='' || sf=='' || nf=='' || idP=='0' || fc=='' || ob=='' || stt=='' || tt==''){
        alert('Todos los campos marcados con  *  son obligatorios.');
        return false;
    }
    var c = '';
    var fl = false;
    var lm = _('tblD').childNodes.length>0 ? parseInt(_('tblD').childNodes.item(_('tblD').childNodes.length-1).id.replace('r', ''))+1 : 0;
    _('tope').value = lm;
    var i=0;
    for(i=0; i<=lm; i++){
        if(_('c'+i)!=null){
            fl = true;
        }
    }
    if(!fl && _('id').value==-1){
        alert('Por favor, ingrese el detalle de la liquidación.');
        return false;
    }
    /*if(SelectorFecha.getTimestamp(fc) > SelectorFecha.getTimestamp(_('feCad').value) ){
        alert("Por favor revise la fecha de caducidad del libretín de liquidaciones.");
        return false;
    }*/
    if(_('id').value=='-1'){
        if(parseInt(nf) < parseInt(_('secDes').value) || parseInt(nf) > parseInt(_('secHas').value)){
            alert("Por favor revise el número secuencial de la liquidación.");
            return false;
        }
    }

    var idR = '';
    var bi = '';
    var vr ='';
    i=0;
    fl = false;
    while(i<10){
        if(_('idR'+i)!=null){
            idR += _('idR'+i).value+',';
            bi += _('bi'+i).value+',';
            vr += _('vr'+i).value+',';
            fl = true;
        }
        i++;
    }
    if(fl){
        var sr = _('sr').value;
        //var autr = _('autr').value;
        var nr = _('nr').value;
        var fre = _('fre').value;
        var ef = _('ef').value;
        if(sr=='' || nr=='' || fre=='' || ef=='' || _('tvr').value==''){
            alert('Por favor, ingrese todos los datos para la retención.');
            return false;
        }
        if(SelectorFecha.getTimestamp(_('fre').value) < SelectorFecha.getTimestamp(_('fem').value)){
            alert('La fecha de emisión de la retención debe ser mayor o igual a la fecha de compra (emisión) de la liquidación.');
            return false;
        }
        if(SelectorFecha.getTimestamp(_('fre').value) > SelectorFecha.getTimestamp(_('retFeCad').value)){
            alert("Por favor revise la fecha de caducidad del libretín de la retención.");
            return false;
        }
        if(f.id.value==-1){
            if(parseInt(nr) < parseInt(_('retSecDes').value) || parseInt(nr) > parseInt(_('retSecHas').value)){
                alert("Por favor revise el número secuencial de la retención.");
                return false;
            }
        }
        return _AJAX.enviarForm(f);
    }

    return _AJAX.enviarForm(f);
}
function inv_liquidacionBuscar()
{
    var w = "where fecha_emision between '"+_('fi').value+"' and '"+_('ff').value+"'";
    if(_('idSuc').value!='-0'){
        w += " and id_sucursal="+_('idSuc').value;
    }
    if(_('idPrv').value!='-0'){
        w += " and id_proveedor="+_('idPrv').value;
    }
    _WR = w;
    new Tabla('d_11', 'jmTbl', '', 'vta_liquidacion_compra', 'PROVEEDOR,# LIQUIDACION,F. EMISION,TOTAL,ESTADO,ESTADO RETENCION,MENSAJE', 'id_liquidacion_compra,razon_social,numero_liquidacion,toDateSQL(fecha_emision),total_compra,txt_anulado,txt_estado_documento,mensaje', '0,200,100,80,80,80,80,200', _altEdi, 'inv_liquidacionEditar(^)', _WR);
}
function inv_liquidacionAnular(k)
{
    if(confirm('Se anularán también todos los documentos asociados a la liquidación de compra o servicio tales como: comprobantes de pagos.')){
        _AJAX.solicitud('frmLiquidacionCompraAnular', 'id='+k, true, 'body');
    }
}
function inv_impLiquidaciones()
{
    var h = "<form onsubmit='return reporte(this)' autocomplete='off'>" + setRadio('z', '{tbl:[{0:"Liquidaciones",1:"Liquidaciones de Compras y Servicios"}]}') +
        //'<hr /><div id="grFch">Desde: '+setFecha('fini')+'<br />Hasta: '+setFecha('ffin')+'</div>' +
        setOculto('fini', _('fi').value) + setOculto('ffin', _('ff').value) + setOculto('id_prov', _('idPrv').value) +
        setPieReporte() +"</form>";
    Ventana.crear('vta_im', 'REPORTES DE LIQUIDACIONES', "ancho=280,alto=180,modal=true,cerrar=true", h);
}
/* NOTAS DE VENTA */
function inv_notas_venta()
{
    encerar();
    if(_MENUS_._enArreglo("notaVentaNueva")>=0){
        btn.icono(-360, 2, 34, 34, 'Nueva nota de venta (RISE)', "inv_nota_ventaEditar('-1');", 'd_herra');
    }
    btn.icono(-455, -114, 34, 34, 'Imprimir', "inv_impNotasVenta();", 'd_herra');
    _AJAX.solicitud('frmFiltro', 'op=6&fn=inv_nota_ventaBuscar()', false, 'd_filtro');
    setAnchoPanel(2, 20);
}
function inv_nota_ventaEditar(k)
{
    _('d_ayuda').innerHTML = msg();
    Ventana.crear('vta1', 'NOTA DE VENTA (RISE)', "ancho=810,alto=470,modal=true,cerrar=true");
    new Tab('vta1_html', 820, 545);
    _AJAX.solicitud('frmNotaVenta', 'id='+k, false, 'vta1_html');
}
function inv_nota_ventaGuardar(f)
{
    var auf = _('auf').value;
    var sf = _('sf').value;
    var nf = _('nf').value;
    var idP = _('coP').value;
    var fc = _('fc').value;
    var fv = _('fv').value;
    var ob = _('ob').value;
    var tt = _('tt').value;
    if(auf=='' || sf=='' || nf=='' || idP=='0' || fc=='' || fv=='' || ob=='' || tt==''){
        alert('Todos los campos marcados con  *  son obligatorios.');
        return false;
    }
    var c = '';
    var fl = false;
    var lm = _('tblD').childNodes.length>0 ? parseInt(_('tblD').childNodes.item(_('tblD').childNodes.length-1).id.replace('r', ''))+1 : 0;
    _('tope').value = lm;
    var i=0;
    for(i=0; i<=lm; i++){
        if(_('c'+i)!=null){
            fl = true;
        }
    }
    if(!fl){
        alert('Por favor, ingrese el detalle de la nota de venta.');
        return false;
    }

    if (fl){
        var idR = '';
        var bi = '';
        var vr ='';
        i=0;
        fl = false;
        while(i<10){
            if(_('idR'+i)!=null){
                idR += _('idR'+i).value+',';
                bi += _('bi'+i).value+',';
                vr += _('vr'+i).value+',';
                fl = true;
            }
            i++;
        }
        if(fl){
            var sr = _('sr').value;
            var autr = _('autr').value;
            var nr = _('nr').value;
            var fre = _('fre').value;
            var ef = _('ef').value;
            if(sr=='' || autr=='' || nr=='' || fre=='' || ef=='' || _('tvr').value==''){
                alert('Por favor, ingrese todos los datos para la retención.');
                return false;
            }
            if(SelectorFecha.getTimestamp(_('fre').value) < SelectorFecha.getTimestamp(_('fc').value)){
                alert('La fecha de emisión de la retención debe ser mayor o igual a la fecha de compra (emisión) de la liquidación.');
                return false;
            }
            if(_('retFeCad').value!='' && _('retSecDes').value!='' && _('retSecHas').value!=''){
                if(SelectorFecha.getTimestamp(_('fre').value) > SelectorFecha.getTimestamp(_('retFeCad').value) || (parseInt(nr) < parseInt(_('retSecDes').value) || parseInt(nr) > parseInt(_('retSecHas').value)) ){
                    if(confirm("Por favor revise la fecha de caducidad del libretín o el número secuencial de la retención. \n Si los datos son correctos pulse ACEPTAR para poder guardar la retención.")){
                        return _AJAX.enviarForm(f);
                    }
                    return false;
                }else{
                    return _AJAX.enviarForm(f);
                }
            }
        }

    }
    return _AJAX.enviarForm(f);
}
function inv_nota_ventaBuscar()
{
    var w = "where id_sucursal="+_ID_SUC_+" and fecha_compra between '"+_('fi').value+"' and '"+_('ff').value+"'";
    if(_('idPrv').value!='-0'){
        w += " and id_proveedor="+_('idPrv').value;
    }
    _WR = w; 
    new Tabla('d_11', 'jmTbl', '', 'vta_nota_venta_compra', 'PROVEEDOR,# NOTA,F. EMISION,TOTAL,ANULADA', 'id_nota_venta_compra,razon_social,numero_nota_venta,toDateSQL(fecha_compra),total_compra,txt_anulado', '0,200,100,80,80,70', _altEdi, 'inv_nota_ventaEditar(^)', _WR);
}
function inv_nota_ventaAnular(k)
{
    if(confirm('Se anularán también todos los documentos asociados a la nota de venta tales como: comprobantes de pagos.')){
        _AJAX.solicitud('frmLNotaVentaCompraAnular', 'id='+k, true, 'body');
    }
}
function inv_impNotasVenta()
{
    var h = "<form onsubmit='return reporte(this)' autocomplete='off'>" + setRadio('z', '{tbl:[{0:"NotasVenta",1:"Notas de venta"}]}') +
        //'<hr /><div id="grFch">Desde: '+setFecha('fini')+'<br />Hasta: '+setFecha('ffin')+'</div>' +
        setOculto('fini', _('fi').value) + setOculto('ffin', _('ff').value) + setOculto('id_prov', _('idPrv').value) +
        setPieReporte() +"</form>";
    Ventana.crear('vta_im', 'REPORTES DE NOTAS DE VENTA', "ancho=280,alto=180,modal=true,cerrar=true", h);
}
/* IMPORTACIONES */
function inv_importaciones()
{
    encerar();
    if(_MENUS_._enArreglo("importacionNueva")>=0){
        btn.icono(-360, 2, 34, 34, 'Nueva Importaci&oacute;n', "inv_importacionEditar(-1);", 'd_herra');
    }
    if(_MENUS_._enArreglo("importacionImprimir")>=0){
        btn.icono(-455, -114, 34, 34, 'Imprimir', "imprimirReporte();", 'd_herra');
    }
    _AJAX.solicitud('frmFiltro', 'op=12', false, 'd_filtro');
}
function inv_importacionBuscar()
{
    var w = "where fecha_tx between '"+_('fi').value+"' and '"+_('ff').value+"'";
    if(_('t_b').value!=''){
        w += " and num_dau="+_('t_b').value;
    }
    _WR = encodeURI(w);
    new Tabla('d_11', 'jmTbl', '', 'tbl_dau', 'NRO. DAU,FECHA TX,HORA TX,TOTAL COSTOS', 'id_dau,num_dau,toDateSQL(fecha_tx),hora_tx,costo_total_importacion', '0,100,100,100,80,80', _altEdi, 'inv_importacionFacts(^);', _WR);
}
function inv_importacionEditar(k)
{
    _('d_ayuda').innerHTML = msg() + '<br/> Para poder ingresar el detalle de productos importados, primero debe guardar el documento (formulario).';
    Ventana.crear('cmp', 'IMPORTACION (DAU)', "ancho=800,alto=500,modal=true,cerrar=true", '');
    new Tab('cmp_html', 750, 385);
    _AJAX.solicitud('frmDau', 'id='+k+'&altB=385', false, 'cmp_html');
}
function inv_importacionGuardar(f)
{
    if(_('num_dau').value=='' || _('tipo_dau').value=='' || _('num_orden').value=='' || _('aduana').value=='' || _('codigo_02').value=='' 
    || _('regimen').value=='' || _('codigo_03').value=='' || _('hora_tx').value=='' || _('importador').value=='' || _('tipo').value==''
    || _('doc_id').value=='' || _('direccion').value=='' || _('direccion').value=='' || _('declarante').value=='' || _('codigo_12').value=='' ||
    _('almacen_deposito').value=='' || _('codigo_13').value==''|| _('valor_em').value=='' || _('iva').value=='' ||
    _('total_autoliquidacion').value=='' || _('observacion').value=='' ){
        alert('Los campos marcados con * son obligatorios');
        return false;
    }
    if(!_esHora('hora_tx')){
        return false;
    }
    return _AJAX.enviarForm(f);
}
function inv_importacionFacts(k)
{
    _('d_ayuda').innerHTML = 'Puede ingresar nuevas facturas de gastos adicionales relacionados con la importaci&oacute;n o editar el documento de importaci&oacute;n haciendo clic en los enlaces. <br /> '+
        'Puede editar las facturas de gastos haciendo clic en el registro de la factura.';
    _AJAX.solicitud('tblFacturasImportacion', 'id='+k+'&altB='+_altBody, false, 'd_21');
}
function inv_importaFactEditar(k, D)
{
    _('d_ayuda').innerHTML = msg() + '<br/> Para poder emitir la retenc&oacute;n primero debe guardar la factura.';
    Ventana.crear('cmp', 'COSTOS - IMPORTACION', "ancho=800,alto=500,modal=true,cerrar=true", '');
    new Tab('cmp_html', 750, 385);
    _AJAX.solicitud('frmFacturaCompra', 'id='+k+'&idDau='+D+'&act=frmFacturaImportacionGuardar&altB=385', false, 'cmp_html');
}
function inv_filtrarProducto()
{
    var h = '<table width="100%"><tr><td><input type="radio" id="t0" name="t" checked onclick="_(\'txt_fil\').value=\'\';_(\'txt_fil\').focus();" /> '+
        '<span onclick="_(\'t0\').checked=true;_(\'txt_fil\').value=\'\';_(\'txt_fil\').focus();">Filtrado manual por c&oacute;digo o descripci&oacute;n</span></td>'+
        '<td><input type="radio" id="t1" name="t" onclick="_(\'txt_fil\').value=\'\';_(\'txt_fil\').focus();" /> '+
        '<span onclick="_(\'t1\').checked=true;_(\'txt_fil\').value=\'\';_(\'txt_fil\').focus();">Utilizar lector de c&oacute;digo de barras</span></td></tr>'+
        '<tr><td colspan="2"><input type="text" id="txt_fil" style="width:500px" onkeypress="if(_(\'t1\').checked){var c=(_esN)?event.which:event.keyCode;if(c==13){inv_addProductoImp(this.value);}}" onkeyup="if(_(\'t0\').checked){inv_setTablaProducto();}" /></td></tr></table>'+
        '<table cellpadding="0" cellspacing="0"><tr><TH class="jm_TH" width="120">CODIGO</TH><TH class="jm_TH" width="300">DESCRIPCION</TH><TH class="jm_TH" width="70">P.C.</TH></tr></table>'+
        '<DIV id="tblFil" style="overflow:auto;width:513px;height:130px;"></div>';
    Ventana.crear('vtFilt', 'FILTRO DE PRODUCTOS', "ancho=540,alto=250,modal=true,cerrar=true", h);
    _('txt_fil').focus();
}
function inv_setTablaProducto()
{
    var t=_('txt_fil').value.toLowerCase();
    var jS = eval('(' + _('ax').innerHTML + ')');
    var fi = '';
    var j=0;
    var h = "<TABLE class='jm_tabla' cellspacing='1' cellpadding='2'>";
    for(var i=0; i<jS.tbl.length; i++){
        if(jS.tbl[i][1].toLowerCase().indexOf(t)==0 || jS.tbl[i][2].toLowerCase().indexOf(t)>=0){
            fi = (j%2==0) ? 'jm_filaPar' : 'jm_filaImp';
            h += "<tr class='"+fi+"' style='cursor:default;' title='Insertar en formulario' onclick=\"inv_addProductoImp('"+jS.tbl[i][1]+"')\"><td width='118'>"+jS.tbl[i][1]+"</td><td width='298'>"+jS.tbl[i][2]+"</td><td width='68'>"+jS.tbl[i][3]+"</td></tr>";
            j++;
        }
    }
    h += "</table>";
    _('tblFil').innerHTML = h;
}
function inv_addProductoImp(c)
{
    var jS = eval('(' + _('ax').innerHTML + ')');
    var pj = _enMatrizJSON(jS, c, 1);
    var pf = enFormulario('', c);
    var i =0;
    if(pf!=-1){
        i=pf;
        _('ct'+pf).value = parseInt(_('ct'+pf).value) + 1;
    }else{
        if(pj!=-1){
            i = _('tblD').childNodes.length>0 ? parseInt(_('tblD').childNodes.item(_('tblD').childNodes.length-1).id.replace('r', ''))+1 : 0;
            var tr = document.createElement("tr");
            tr.id='r'+i;
            tr.className=(i%2==0) ? 'jm_filaPar' : 'jm_filaImp';
            _('tblD').appendChild(tr);
            var h = "<td id='cF"+i+"0' width='100'>"+c+"</td>" +
            "<td width='226'>"+jS.tbl[pj][2]+"<input type='hidden' id='idPr"+i+"' name='idPr"+i+"' value='"+jS.tbl[pj][0]+"' /></td>"+
            "<td width='71'>"+jS.tbl[pj][3]+"</td>"+
            "<td width='61'><input type='text' onkeypress=\"_evaluar(event, '0123456789.')\" onkeyup='inv_calcAsignacion("+i+", 0)' size='5' id='pim"+i+"' name='pim"+i+"' style='text-align:right' /></td>" +
            "<td width='71'><input type='text' onkeypress=\"_evaluar(event, '0123456789.')\" onkeyup='inv_calcAsignacion("+i+", 1)' size='5' id='vim"+i+"' name='vim"+i+"' style='text-align:right' /></td>" +
            "<td width='61'><input type='text' onkeypress='_numero(event)' size='5' id='can"+i+"' name='can"+i+"' value='1' onkeyup='inv_calcPrecios("+i+")' style='text-align:right' /></td>" +
            "<td width='71'><input type='text' size='5' id='pci"+i+"' name='pci"+i+"' readonly style='text-align:right' /></td>" +
            "<td align='center' width='21'><div class='jm_icoCon' style='background-position:-545px -18px;' title='Eliminar' onclick=\"_R('r"+i+"');inv_sumPrecios();\">&nbsp;</div></td>";
            tr.innerHTML = h;
        }
    }
}
function inv_calcAsignacion(i, o)
{
    var t=parseFloat(_('totImp').value) || 0;
    if(o==0){
        if(_('pim'+i).value!=''){
            _('vim'+i).value = _RD(t * parseFloat(_('pim'+i).value) / 100);
        }
    }else{
        if(_('vim'+i).value!=''){
            _('pim'+i).value = _RD(100 * parseFloat(_('vim'+i).value) / t);
        }
    }
    inv_calcPrecios(i);
}
function inv_calcPrecios(i)
{
    var c = parseInt(_('can'+i).value) || 0;
    if(_('vim'+i).value!=''){
        _('pci'+i).value = _RD(parseFloat(_('vim'+i).value) / c);
    }
    inv_sumPrecios();
}
function inv_calcSUBT(p, i)
{
    var c = _(p+'c'+i).value;
    c = c!='' ? parseFloat(c) : 1;
    var t = _(p+'t'+i).value;
    t = t!='' ? parseFloat(t) : 0;
    var d = 0;
    if(_(p+'dt')!=null){
        d = (_(p+'d'+i).value!='') ? parseFloat(_(p+'d'+i).value) : 0;
    }
    var st = _('codP'+i)!=null ? ( (_('codP'+i).innerHTML.indexOf('~')>0) ? _RD((t+d) / (parseFloat(_('p_iva1').value) /100 + 1)) : t+d ) :
                            ( _('t_iva'+i).value=='~' ? _RD((t+d) / (parseFloat(_('p_iva1').value) /100 + 1)) : _RD(t+d) );
    var pu = _RD( (st / c), 4 );

    _(p+'pu'+i).value = pu;
    _(p+'v'+i).value = _RD(t + d - st);
    _(p+'st'+i).value = _RD(st);
}
function inv_sumPrecios()
{
    var l = _('tblD').childNodes.length>0 ? parseInt(_('tblD').childNodes.item(_('tblD').childNodes.length-1).id.replace('r', ''))+1 : 0;
    var p = 0;
    var v = 0;
    for(var i=0; i<=l; i++){
        if(_('pim'+i)!=null){
            p += parseFloat(_('pim'+i).value) || 0;
            v += parseFloat(_('vim'+i).value) || 0;
        }
    }
    _('sumP').innerHTML = _RD(p);
    _('sumV').innerHTML = _RD(v);
}
function inv_listaProdsGuardar(k)
{
    var p = 'idDau='+k;
    var iPr = '';
    var pI = '';
    var pV = '';
    var c = '';
    var cu = '';
    var l = _('tblD').childNodes.length>0 ? parseInt(_('tblD').childNodes.item(_('tblD').childNodes.length-1).id.replace('r', ''))+1 : 0;
    for(var i=0; i<=l; i++){
        if(_('pim'+i)!=null){
            if(_('pci'+i).value==''){
                alert('Por favor, llene todos los datos de los productos');
                return false;
            }
            iPr += _('idPr'+i).value+',';
            pI += _('pim'+i).value+',';
            pV += _('vim'+i).value+',';
            c += _('can'+i).value+',';
            cu += _('pci'+i).value+',';
        }
    }
    if(pI!=''){
        iPr = iPr.substring(0, iPr.length - 1);
        pI = pI.substring(0, pI.length - 1);
        pV = pV.substring(0, pV.length - 1);
        c = c.substring(0, c.length - 1);
        cu = cu.substring(0, cu.length - 1);
    }
    p += '&idPr='+iPr+'&pI='+pI+'&pV='+pV+'&c='+c+'&cu='+cu;
    _AJAX.solicitud('frmDauDetalleGuardar', p, false, 'body');
}
function inv_importacionCerrar(k)
{
    if(confirm("Si cierra la importación ya no podrá modificar el documento de Importación así como ingresar y/o editar facturas y retenciones de costos relacionadas con la importación.\n"+
        "¿Está seguro(a) de cerrar toda la documentación de importación?")){
        _AJAX.solicitud('importacionCerrar', 'idDau='+k, false, 'body');
    }
}
function inv_importacionAnular(k)
{
    if(confirm("Se anularán también todos los documentos asociados a la importación tales como: facturas, retenciones y/o comprobantes de pagos.\n"+
        "¿Está seguro(a) de anular toda la documentación de importación?")){
        _AJAX.solicitud('importacionAnular', 'idDau='+k, false, 'body');
    }
}
/* PAGOS */
function inv_pagos()
{
    encerar();
    btn.icono(-455, -114, 34, 34, 'Imprimir', "fac_impPagos();", 'd_herra');
    _AJAX.solicitud('frmFiltro', 'op=7&fn=inv_buscarPagos()', false, 'd_filtro');
    setAnchoPanel(2, 610);
}
function inv_buscarPagos()
{
    if(_('typ')==null){
       _('d_filtro').innerHTML += "<input type='hidden' id='typ' value='e' />";
    }
    _('d_21').innerHTML='&nbsp;';
    var w = "";
    if(_('pgs').value=='p'){
        _('grFch').style.visibility='hidden';
        w = "where credito>0 and (id_proveedor in(select distinct id_proveedor from tbl_factura_compra where id_sucursal="+_('idSuc').value+" and deuda>0 and anulado=false) "+
            "or id_proveedor in(select distinct id_proveedor from tbl_nota_venta_compra where id_sucursal="+_('idSuc').value+" and deuda>0 and anulado=false)"+
            "or id_proveedor in(select distinct id_proveedor from tbl_liquidacion_compra where id_sucursal="+_('idSuc').value+" and deuda>0 and anulado=false)"+
            "or id_proveedor in(select distinct id_proveedor from tbl_nota_credito_venta where id_sucursal="+_('idSuc').value+" and deuda>0 and anulado=false)) ";
            //"or id_proveedor in(select distinct id_proveedor from tbl_nota_debito_compra where id_sucursal="+_('idSuc').value+" and deuda>0 and anulado=false))";
        if(_('idPrv').value!='-0'){
            w += " and id_proveedor="+_('idPrv').value;
        }
        _WR = encodeURI(w); 
        new Tabla('d_11', 'jmTbl', '', 'vta_proveedor', 'PROVEEDOR,DEUDA', 'id_proveedor,razon_social,credito', '0,200,80', _altEdi, 'inv_proveedorPagar(^);', w);
    }
    if(_('pgs').value=='f'){
        _('grFch').style.visibility='visible';
        w = "where fecha_pago between '"+_('fi').value+"' and '"+_('ff').value+"' and factura_anulada=false and id_sucursal="+_('idSuc').value;
        if(_('idPrv').value!='-0'){
            w += " and id_proveedor="+_('idPrv').value;
        }
        _WR = encodeURI(w);
        new Tabla('d_11', 'jmTbl1', '', 'vta_factura_compra_pago', 'PROVEEDOR,# FACTURA,# CHEQUE,F. PAGO,ABONO,FORMA P.', 'id_factura_compra_pago,razon_social,numero_factura,txt_num_cheque,toDateSQL(fecha_pago),abono,txt_tipo_pago', '0,130,110,90,90,90,80,80', _altEdi, 'inv_facturaPagoEditar(^);', _WR);
    }
}
function inv_facturaPagoEditar(id)
{
    Ventana.crear('vta_nc', 'Comprobante de Egreso', "ancho=780,alto=500,modal=true,cerrar=true", '');
    _AJAX.solicitud('frmComprobanteEgresoPago', 'id='+id, false, 'vta_nc_html');
}
function inv_proveedorPagar(k)
{
    new Ajax('frmProveedorPago', 'id='+k+'&aB='+_altBody, false, 'd_21');
}
function inv_sumTotalPago()
{
    var i=0;
    var t = 0;
    var ts = 0;
    while(_('ab'+i)!=null){
        t += _RD(parseFloat(_('ab'+i).value));
        _('sa'+i).value = _RD(parseFloat(_('de'+i).value) - parseFloat(_('ab'+i).value));
        ts += _RD(_('sa'+i).value);
        i++;
    }
    _('tp').innerHTML = _RD(t);
    _('ts').innerHTML = _RD(ts);
}
function inv_getDocumentoPago()
{
    if(parseFloat(_('tp').innerHTML)<=0){
        alert('No se puede realizar un pago con un valor de 0.');
        return false;
    }
    Ventana.crear('vta_nc', 'Comprobante de Egreso', "ancho=800,alto=500,modal=true,cerrar=true", '');
    _AJAX.solicitud('frmComprobanteEgresoPago', 'id=-1&idP='+_('coP').value, false, 'vta_nc_html');
}
function inv_setDocumentoPago()
{
    _('dt').value = 'Pago en efectivo, de la compra efectuada a '+_('ras').innerHTML;
    cnt_addCuenta('t', 'n');
    _('PC0').value = _('cxp').value;
    _('PC0').disabled = 'disabled';
    _('d0').value = _('td').innerHTML = _('tp').innerHTML;
    cnt_addCuenta('t', 'n');
    _('PC1').value = _('caj').value; 
    _('PC1').disabled = 'disabled';
    _('h1').value = _('tp').innerHTML;
    _('nb').value = _('ras').innerHTML;
    _('ru').value = _('ruc').innerHTML;
    _('sn').value = FloatToStr(_('tp').innerHTML);
}
function inv_setDetalle()
{
    cnt_setBanco();
    if(_('e0').checked){
        _('dt').value = 'Pago en efectivo, de la compra efectuada a '+_('ras').innerHTML;
        _('PC1').value = _('caj').value;
        _('pCh').style.display='none';
    }else if(_('e1').checked){
            _('dt').value = 'Pago con cheque, de la compra efectuada a '+_('ras').innerHTML;
            _('pCh').style.display='block';
    }
}
function inv_setEfectivo(o)
{
    _('PC1').value = o==0 ? _('chEnC').value : _('banc').value;
}
function inv_sumTotalPagoEd()
{
    var i=0;
    var t = 0;
    while(_('ab'+i)!=null){
        t += _RD(parseFloat(_('ab'+i).value));
        _('sa'+i).value = _RD(parseFloat(_('de'+i).value) - parseFloat(_('ab'+i).value));
        i++;
    }
    _('tp').innerHTML = _('td').innerHTML = _('th').innerHTML = _('h1').value = _('d0').value = _RD(t);
}
function inv_pagoGuardar(g, k)
{
    var idF = '';
    var idFP = '';
    var ab = '';
    var doc = '';
    var i=0;
    while(_('idF'+i)!=null){
        if(k==-1){
            if(_('ab'+i).value!='' && _('ab'+i).value!='0'){
                if(_('idFP'+i)!=null){
                    idFP += _('idFP'+i).value + ',';
                }
            }
        }else{
            if(_('ab'+i).value=='' || _('ab'+i).value=='0' || _('ab'+i).value=='0.0'){
                alert('En la edición todos los valores de pago deben ser mayor que 0.');
                return false;
            }
            if(_('idFP'+i)!=null){
                idFP += _('idFP'+i).value + ',';
            }
        }
        idF += _('idF'+i).value + ',';
        ab += _('ab'+i).value + ',';
        doc += _('doc'+i).value + ',';
        i++;
    }
    var nb = _('nb').value;
    var ru = _('ru').value;
    var bc = _('bc').value;
    var ct = _('ct').value;
    var ch = _('ch').value;
    var lu = _('lu').value;
    var c = _('dt').value;
    if(nb=='' || ru=='' || c==''){
        alert('Por favor, los campos marcados con un  *  son obligatorios.');
        return false;
    }
    /*if(!esDocumento('ru')){
        return false;
    }*/
    if(_('e1').checked){
        if(ch=='' || bc=='' || lu==''){
            alert('Por favor, los campos marcados con un  *  son obligatorios.');
            return false;
        }
    }
    var td = _('td').innerHTML;
    var th = _('th').innerHTML;
    var fp = (_('e0').checked) ? 'e' : ((_('e1').checked) ? 'c' : 'n');
    var p = 'id='+k+'&c='+c._codificarURI()+'&f='+_('fp').value+'&t='+td+'&g='+g+'&nb='+nb._codificarURI();
        p += '&ru='+ru+'&bc='+bc._codificarURI()+'&ct='+ct+'&ch='+ch+'&lu='+lu._codificarURI()+'&sn='+_('sn').value+'&tip=ch'+
             '&idF='+idF.substr(0, idF.length-1)+'&ab='+ab.substr(0, ab.length-1)+'&doc='+doc.substr(0, doc.length-1)+
             '&tp='+_('tp').innerHTML+'&idFP='+idFP+'&fe='+_('fe').value+'&fp='+fp+'&coP='+_('coP').value+'&id_bc='+_('bc1').value+
             '&id_sucursal='+_('id_sucursal').value;
    var j = 0;
    var pc = '';
    var d = '';
    var h = '';
    for(i=0; i<50; i++){
        if(_('d'+i)!=null){
            pc += _('PC'+i).value + ',';
            d += ((_('d'+i).value!='')?_('d'+i).value:'0') + ',';
            h += ((_('h'+i).value!='')?_('h'+i).value:'0') + ',';
            j++;
        }
    }
    if(j < 2){
        alert('Por favor, ingrese por lo menos dos cuentas para que la transacción cumpla con la partida doble.');
        return false;
    }
    if(td=='0' || th=='0'){
        alert('Por favor, debe ingresar los valores correspondientes de cada cuenta sea del debe o del haber.');
        return false;
    }
    if(!cnt_verificarCuentas(pc)){
        alert('Por favor, las cuentas no deben repetirse.');
        return false;
    }
    if(parseFloat(td) != parseFloat(th)){
        alert('Por favor, los totales deben ser iguales.');
        return false;
    } 
    p += '&pc='+pc.substr(0, pc.length-1)+'&d='+d.substr(0, d.length-1)+'&h='+h.substr(0, h.length-1);
    _AJAX.solicitud('frmComprobanteEgresoPagoGuardar', p, true, 'body');
}
function fac_impPagos()
{
    var F = new Date();
    var hoy = F.getDate()+'/'+(F.getMonth()+1)+'/'+F.getFullYear();
    var h = "<input type='radio' id='z0' name='z' checked='checked' value='40' /> <span onclick=\"_('z0').checked='true'\">Pagos realizados desde <input type='text' id='bfi' value='"+hoy+"' size='9' readOnly><input type='button' value='...' onClick=\"SelectorFecha.crear('bfi', 'SQL');\" /> &nbsp; hasta <input type='text' id='bff' value='"+hoy+"' size='9' readOnly><input type='button' value='...' onClick=\"SelectorFecha.crear('bff', 'SQL');\" /> </span>";
    h += "<br><input type='radio' id='z1' name='z' value='32' /> <span onclick=\"_('z1').checked='true'\">Pagos pendientes</span>" +
        "<hr><input type='radio' id='x' name='x' checked /> PDF &nbsp;&nbsp;&nbsp;&nbsp;&nbsp; <input type='radio' id='x2' name='x' /> Excel "+
        "<br/><br/><input type='button' value='Imprimir' onclick='imprimirReporte2()'>";
    Ventana.crear('vta_im', 'Impresi&oacute;n de Pagos', "ancho=440,alto=180,modal=true,cerrar=true", h);
}
/* TRASPASOS */
function inv_traspasos()
{
    encerar();
    if(_MENUS_._enArreglo("traspasoNuevo")>=0){
        btn.icono(-360, 2, 34, 34, 'Nuevo traspaso', "inv_traspasoEditar('-1');", 'd_herra');
    }
    if(_MENUS_._enArreglo("traspasoEliminar")>=0){
        btn.icono(-391, 3, 34, 34, 'Eliminar', "_objT.eliminar();", 'd_herra');
    }
    _AJAX.solicitud('frmFiltro', 'op=8&fn=inv_buscarTraspasos()', false, 'd_filtro');
    setAnchoPanel(2, 20);
}
function inv_buscarTraspasos()
{
    var w='';
    if(_('tras')!=null){
        w="where fecha_envio between '"+_('fi').value+"' and '"+_('ff').value+"'";
        w += (_('tras').value=='e') ? " and (usuario_recepcion is null or usuario_recepcion='')" : " and usuario_recepcion<>''";
        if(_('idSuc').value!='-0'){
            w += " and id_suc_origen="+_('idSuc').value;
        }
    }else{
        w="where id_suc_recepcion='"+_('idSuc').value+"' and (usuario_recepcion is null or usuario_recepcion='')";
    }
    _WR = encodeURI(w);
    new Tabla('d_11', 'jmTbl1', '', 'vta_traspaso', '# TRAS.,ORIGEN,USUARIO ODIGEN,FECHA ENVIO,RECEPCION,USUARIO RECEP.,FECHA RECEP.', 'id_traspaso,num_traspaso,origen,usuario_origen,toDateSQL(fecha_envio),recepcion,usuario_recepcion,toDateSQL(fecha_recepcion)', '0,50,110,90,90,110,90,90', _altEdi-10, 'inv_traspasoEditar(^)', _WR);
}
function inv_traspasoEditar(k)
{
    _('d_ayuda').innerHTML = msg();
    Ventana.crear('cmp', 'TRASPASOS', "ancho=590,alto=400,modal=true,cerrar=true", '');
    _AJAX.solicitud('frmTraspaso', 'id='+k+'&altB='+_altBody, false, 'cmp_html');
}
function inv_traspasoGuardar(f)
{
    if(_('nt').value==''){
        _MS('Falta el número de traspaso.', _('nt'));
        return false;
    }
    if(_('or').value==_('re').value){
        alert('La sucursal de recepción no debe ser la misma que la sucursal de origen.');
        return false;
    }
    if(_('tblD').childNodes.length<1){
        alert('Debe ingresar por lo menos un producto para poder enviar el formulario.');
        return false;
    }
    var mId = _('tblD').childNodes.item(_('tblD').childNodes.length-1).id.replace('r', '');
    for(var i=0; i<=parseInt(mId); i++){
        if(_('r'+i)!=null){
            if(_('ctEnv'+i).value==''){
                alert('Falta la cantidad a enviar del producto de código  '+ _('cF'+i+'0').innerHTML);
                return false;
            }
            if(parseInt(_('ctEnv'+i).value) > parseInt(_('stk'+i).value)){
                alert('La ingresar la cantidad a enviar del producto de código  '+ _('cF'+i+'0').innerHTML + '  exede el valor del stock de ' + _('stk'+i).value);
                return false;
            }
        }
    }
    if(confirm("Una vez enviado el formulario de traspasos no podrá modificarlo o eliminarlo.\n¿Está seguro(a) de enviar el formulario de traspaso de mercadería?")){
        _('numProd').value = mId;
        return _AJAX.enviarForm(f);
    }
    return false;
}
function inv_traspasoRecibir(k)
{
    var p = 'id='+k+'&usRe='+_('usRe').value+'&feRe='+_('feRe').value+'&WHERE='+_WR;
    var i=0;
    while(_('ctRec'+i)!=null){
        if(_('ctRec'+i).value==''){
            alert('Falta ingresar la cantidad recibida del producto de código  '+ _('cF'+i+'0').innerHTML);
            return false;
        }
        p += '&idTrD'+i+'='+_('idTrD'+i).value+'&ctRec'+i+'='+_('ctRec'+i).value;
        i++;
    }
    _AJAX.solicitud('frmTraspasoRecibir', p, false, 'body');
}
/* PEDIDOS */
function inv_pedidos()
{
    encerar();
    if(_MENUS_._enArreglo("pedidoNuevo")>=0){
        btn.icono(-360, 2, 34, 34, 'Nuevo Pedido Activos', "inv_pedidoEditar('-1');", 'd_herra');
    }
    if(_MENUS_._enArreglo("pedidoEliminar")>=0){
        btn.icono(-391, 3, 34, 34, 'Eliminar', "_objT.eliminar();", 'd_herra');
    }
    _AJAX.solicitud('frmFiltro', 'op=9&fn=inv_buscarPedidos()', false, 'd_filtro');
    setAnchoPanel(2, 20);
}
function inv_buscarPedidos()
{
    var w='';
    if(_('ped')!=null){
        w="where fecha_pedido between '"+_('fi').value+"' and '"+_('ff').value+"' and revisado="+_('ped').value;
        if(_('idSuc').value!='-0'){
            w += " and id_sucursal="+_('idSuc').value;
        }
    }else{
        w="where id_sucursal='"+_('idSuc').value+"' and revisado=false";
    }
    _WR = encodeURI(w);
    new Tabla('d_11', 'jmTbl1', '', 'vta_pedido', '# PEDI.,SUCURSAL,FECHA PEDIDO,USUARIO', 'id_pedido,num_pedido,sucursal,toDateSQL(fecha_pedido),usuario', '0,50,110,90,90', _altEdi-10, 'inv_pedidoEditar(^)', _WR);
}
function inv_pedidoEditar(k)
{
    _('d_ayuda').innerHTML = msg();
    Ventana.crear('cmp', 'PEDIDOS ACTIVOS', "ancho=550,alto=400,modal=true,cerrar=true", '');
    _AJAX.solicitud('frmPedidoActivo', 'id='+k+'&altB='+_altBody, false, 'cmp_html');
}
function inv_pedidoGuardar(f){
    if(_('np').value==''){
        _MS('Falta el número de pedido.', _('np'));
        return false;
    }
    if(_('tblD').childNodes.length<1){
        alert('Debe ingresar por lo menos un producto para poder enviar el formulario.');
        return false;
    }
    var mId = _('tblD').childNodes.item(_('tblD').childNodes.length-1).id.replace('r', ''); 
    for(var i=0; i<=parseInt(mId); i++){
        if(_('r'+i)!=null){
            if(_('ctEnv'+i).value==''){
                alert('Falta la cantidad a enviar del producto de código  '+ _('cF'+i+'0').innerHTML);
                return false;
            }
        }
    }
    if(confirm("Una vez enviado el formulario de pedido no podrá modificarlo o eliminarlo.\n¿Está seguro(a) de enviar el formulario de pedido de mercadería?")){
        _('numProd').value = mId;
        return _AJAX.enviarForm(f);
    }
    return false;
}
/* FUNCIONES COMPARTIDAS */
function filtrarProducto()
{
    var h = '<table width="100%"><tr><td><input type="radio" id="t0" name="t" checked onclick="_(\'txt_fil\').value=\'\';_(\'txt_fil\').focus();" /> '+
    '<span onclick="_(\'t0\').checked=true;_(\'txt_fil\').value=\'\';_(\'txt_fil\').focus();">Filtrado manual por c&oacute;digo o descripci&oacute;n</span></td>'+
    '<td><input type="radio" id="t1" name="t" onclick="_(\'txt_fil\').value=\'\';_(\'txt_fil\').focus();" /> '+
    '<span onclick="_(\'t1\').checked=true;_(\'txt_fil\').value=\'\';_(\'txt_fil\').focus();">Utilizar lector de c&oacute;digo de barras</span></td></tr>'+
    '<tr><td colspan="2"><input type="text" id="txt_fil" style="width:470px" onkeypress="if(_(\'t1\').checked){var c=(_esN)?event.which:event.keyCode;if(c==13){addProducto(this.value);this.value=\'\';}}" onkeyup="if(_(\'t0\').checked){setTablaProducto();}" /></td></tr></table>'+
        '<table cellpadding="0" cellspacing="0"><tr><TH class="jm_TH" width="110">CODIGO</TH><TH class="jm_TH" width="255">DESCRIPCION</TH><TH class="jm_TH" width="50">STOCK</TH><TH class="jm_TH" width="60">PRECIO</TH></tr></table>'+
    '<DIV id="tblFil" style="overflow:auto;width:500px;height:130px;"></div>';
    Ventana.crear('vtFilt', 'FILTRO DE MERCADERIA', "ancho=520,alto=250,modal=true,cerrar=true", h);
    _('txt_fil').focus();
}
function filtrarActivo()
{
    var h = '<div id="tblFilAct" style="float:left; overflow:auto; width:405px;height:160px; border: 1px dashed #00A3FF;">\n\
               </div>';
    //new Tabla('tblFilAct', 'jmTbl', '', 'vta_activo_arbol', 'CODIGO,CUENTA', 'id_comun,codigo,nombre', '0,80,260', _altEdi, '', '');
    Ventana.crear('ad', 'FILTRO DE ACTIVOS', "ancho=420,alto=200,modal=true,cerrar=true", h);
    _AJAX.solicitud('tblArbolActivos', '', false, 'tblFilAct');
    
    //_('txt_fil').focus();
}

function PedidoPersonalizacion(id)
{
    _('d_ayuda').innerHTML = msg();
    _R('cmp');
    _R('bloq_cmp');
    Ventana.crear('cmp', 'PERSONALIZACION DE ACTIVOS', "ancho=820,alto=500,modal=true,cerrar=true", '');
    _AJAX.solicitud('frmPedidoPersonalizacion', 'id='+id+'', false, 'cmp_html');
}


function setTablaProducto()
{
    var t=_('txt_fil').value.toLowerCase();
    var jS = eval('(' + _('ax').innerHTML + ')');  
    var fi = '';
    var j=0;
    var h = "<TABLE class='jm_tabla' cellspacing='1' cellpadding='2'>"; 
    for(var i=0; i<jS.tbl.length; i++){
        if(jS.tbl[i][1].toLowerCase().indexOf(t)==0 || jS.tbl[i][2].toLowerCase().indexOf(t)>=0){
            fi = (j%2==0) ? 'jm_filaPar' : 'jm_filaImp';
            h += "<tr class='"+fi+"' style='cursor:default;' title='Insertar en formulario' onclick=\"addProducto('"+jS.tbl[i][1]+"')\"><td width='108'>"+jS.tbl[i][1]+"</td><td width='298'>"+jS.tbl[i][2]+"</td><td width='63'>"+jS.tbl[i][3]+"</td><td width='63'>"+jS.tbl[i][4]+"</td></tr>";
            j++;
        }
    }
    h += "</table>";
    _('tblFil').innerHTML = h;
}
function setTablaActivo()
{
    var t=_('txt_fil').value.toLowerCase();
    var jS = eval('(' + _('ax').innerHTML + ')');  
    var fi = '';
    var j=0;
    var h = "<TABLE class='jm_tabla' cellspacing='1' cellpadding='2'>"; 
    for(var i=0; i<jS.tbl.length; i++){
        if(jS.tbl[i][1].toLowerCase().indexOf(t)==0 || jS.tbl[i][2].toLowerCase().indexOf(t)>=0){
            fi = (j%2==0) ? 'jm_filaPar' : 'jm_filaImp';
            h += "<tr class='"+fi+"' style='cursor:default;' title='Insertar en formulario' onclick=\"addProducto('"+jS.tbl[i][1]+"')\"><td width='100'>"+jS.tbl[i][1]+"</td><td width='200'>"+jS.tbl[i][2]+"</td><td width='60'>"+jS.tbl[i][3]+"</td><td width='60'>"+jS.tbl[i][4]+"</td></tr>";
            j++;
        }
    }
    h += "</table>";
    _('tblFil').innerHTML = h;
}
function enFormulario(pf, c)
{
    var p = pf || '';
    var mId = _(p+'tblD').childNodes.length>0 ? parseInt(_(p+'tblD').childNodes.item(_(p+'tblD').childNodes.length-1).id.replace(p+'r', ''))+1 : 0;
    for(var i=0; i<=parseInt(mId); i++){
        if(_(p+'r'+i)!=null){
            if(_(p+'cF'+i+'0').innerHTML==c){
                return i;
            }
            i++;
        }
    }
    return -1;
}
function addActivo(id,codi,nom,ele)
{
    var pf = enFormulario('', codi);
    var mu=1;
    if(pf!=-1){
        _('ctEnv'+pf).value = parseInt(_('ctEnv'+pf).value) + 1;
         _('valo'+pf).value = parseInt(_('valo'+pf).value) + 1;
        //document.getElementById("val").innerHTML=(parseInt(_('ctEnv'+pf).value))*document.getElementById("val").value;
        //document.getElementById("val"+pf+"").innerHTML=parseInt(document.getElementById("can"+pf+"").value)+1;
    }else{
        //var jS = eval('(' + _('ax').innerHTML + ')');
        //var pj = _enMatrizJSON(jS, c, 1);
            var i = _('tblD').childNodes.length>0 ? parseInt(_('tblD').childNodes.item(_('tblD').childNodes.length-1).id.replace('r', ''))+1 : 0;
            var tr = document.createElement("tr");
            tr.id='r'+i;
            tr.className=(i%2==0) ? 'jm_filaPar' : 'jm_filaImp';
            _('tblD').appendChild(tr);
            var h = "";
            var o = _('frm').value;
            if(o=='t'){
                h = "<td id='cF"+i+"0' width='110'>"+codi+"</td>" +
                "<td width='271'>"+nom+"</td>"+
                "<td width='61'><input type='text' onkeypress='_numero(event)' size='5' id='ctEnv"+i+"' name='ctEnv"+i+"' value='1' /></td>"+
                "<td width='60' id='can"+i+"' name='can"+i+"'>"+ele+"</td>"+
                "<td><input type='hidden' id='idPr"+i+"' name='idPr"+i+"' value='"+id+"' />"+
                "<td align='center' width='21'><div class='jm_icoCon' style='background-position:-545px -18px;' title='Eliminar' onclick=\"_R('r"+i+"');\">&nbsp;</div></td>";
            }
            if(o=='p'){
                h = "<td id='cF"+i+"0' width='110'>"+codi+"</td>" +
                "<td width='271'>"+nom+"</td>"+
                "<td width='61'><input type='text' onkeypress='_numero(event)' size='5' id='ctEnv"+i+"' name='ctEnv"+i+"' value='1' /></td>"+
                "<td width='60' id='can"+i+"' name='can"+i+"'>"+ele+"</td>"+
                "<td><input type='hidden' id='idPr"+i+"' name='idPr"+i+"' value='"+id+"' />"+
                "<td align='center' width='21'><div class='jm_icoCon' style='background-position:-545px -18px;' title='Eliminar' onclick=\"_R('r"+i+"');\">&nbsp;</div></td>";
            }
            tr.innerHTML = h;
    }
}
function addProducto(c)
{
    var pf = enFormulario('', c);
    var mu=1;
    if(pf!=-1){
        _('ctEnv'+pf).value = parseInt(_('ctEnv'+pf).value) + 1;
         _('valo'+pf).value = parseInt(_('valo'+pf).value) + 1;
        //document.getElementById("val").innerHTML=(parseInt(_('ctEnv'+pf).value))*document.getElementById("val").value;
        //document.getElementById("val"+pf+"").innerHTML=parseInt(document.getElementById("can"+pf+"").value)+1;
    }else{
        var jS = eval('(' + _('ax').innerHTML + ')');
        var pj = _enMatrizJSON(jS, c, 1);
        if(pj!=-1){
            var i = _('tblD').childNodes.length>0 ? parseInt(_('tblD').childNodes.item(_('tblD').childNodes.length-1).id.replace('r', ''))+1 : 0;
            var tr = document.createElement("tr");
            tr.id='r'+i;
            tr.className=(i%2==0) ? 'jm_filaPar' : 'jm_filaImp';
            _('tblD').appendChild(tr);
            var h = "";
            var o = _('frm').value;
            if(o=='t'){
                h = "<td id='cF"+i+"0' width='110'>"+jS.tbl[pj][1]+"</td>" +
                "<td width='301'>"+jS.tbl[pj][2]+"</td>"+
                "<td width='61'><input type='text' onkeypress='_numero(event)' size='5' id='ctEnv"+i+"' name='ctEnv"+i+"' value='1' /></td>"+
                "<td width='60' id='can"+i+"' name='can"+i+"'>"+jS.tbl[pj][4]+"</td>"+
                "<td><input type='hidden' id='idPr"+i+"' name='idPr"+i+"' value='"+jS.tbl[pj][0]+"' />"+
                "<input type='hidden' id='stk"+i+"' name='stk"+i+"' value='"+jS.tbl[pj][3]+"' /></td>" +
                "<td align='center' width='21'><div class='jm_icoCon' style='background-position:-545px -18px;' title='Eliminar' onclick=\"_R('r"+i+"');\">&nbsp;</div></td>";
            }
            if(o=='p'){
                h = "<td id='cF"+i+"0' width='110'>"+jS.tbl[pj][1]+"</td>" +
                "<td width='300'>"+jS.tbl[pj][2]+"</td>"+
                "<td width='60'><input type='text' onkeypress='_numero(event)' size='5' id='ctEnv"+i+"' name='ctEnv"+i+"' value='1' />"+
                "<td width='60' id='can1"+i+"' name='can1"+i+"'>"+jS.tbl[pj][4]+"</td>"+
                "<input type='hidden' id='idPr"+i+"' name='idPr"+i+"' value='"+jS.tbl[pj][0]+"' />"+
                "<input type='hidden' id='stk"+i+"' name='stk"+i+"' value='"+jS.tbl[pj][3]+"' /></td>" +
                "<td align='center' width='21'><div class='jm_icoCon' style='background-position:-545px -18px;' title='Eliminar' onclick=\"_R('r"+i+"');\">&nbsp;</div></td>";
            }
            tr.innerHTML = h;
        }
    }
}
/* PLANES DE SERVICIO */
function fac_planes()
{
    encerar();
    _('d_filtro').innerHTML = 'Plan o descripci&oacute;n: <input type="text" id="t_b" onkeypress="var c=(_esN)?event.which:event.keyCode;if(c==13){fac_planBuscar();}">'+
        '&nbsp;<input type="button" value="Filtrar" onclick="fac_planBuscar();">';
    if(_MENUS_._enArreglo("planNuevo")>=0){
        btn.icono(-360, 2, 34, 34, 'Nuevo Plan de servicio', "fac_planEditar('-1');", 'd_herra');
    }
    if(_MENUS_._enArreglo("planEliminar")>=0){
        btn.icono(-391, 3, 34, 34, 'Eliminar', "_objT.eliminar();", 'd_herra');
    }
    if(_MENUS_._enArreglo("planImprimir")>=0){
        btn.icono(-455, -114, 34, 34, 'Imprimir', "fac_impPlanes();", 'd_herra');
    }
    btn.separador('d_herra');
    btn.icono(-455, 3, 34, 34, 'Restablecer todos los planes y clientes en el FREERADIUS', "imprimir('planes.jsp');", 'd_herra');
    fac_planBuscar();
    setAnchoPanel(2, 500);
}
function fac_planEditar(k)
{
    _('d_ayuda').innerHTML = 'Los valores de los planes de servicios ya incluyen el IVA. <br />'+msg();
    new Tab('d_21', 450, _altBody-310);
    _AJAX.solicitud('frmPlanServicio', 'id='+k+'&altB='+(_altBody-310), false, 'd_21');
}
function fac_planGuardar(f)
{
    if(f.plan.value=='' || f.id_producto.value=='' || f.div_max_limit.value=='' || f.div_burst_threshold.value=='' || f.burst_time.value=='' || f.sim_subida.value==''){
        alert('Todos los campos marcados con  *  son obligatorios.');
        return false;
    }
    if(f.id.value != '-1'){
        if( !confirm('Si cambia de nombre es probable que se pierdan las referencias de los clientes para las diferentes velocidades del plan') ){
            return false;
        }
    }
    return _AJAX.enviarForm(f);
}
function fac_planBuscar()
{
    var w='';
    if(_('t_b')!=null){
        var a=_('t_b').value;
        if(a!=''){
            w += "where lower(plan) like '%"+a.toLowerCase()+"%'";
        }
    }
    _WR = encodeURI(w);
    new Tabla('d_11', 'jmTbl', '', 'vta_plan_isp', 'PLAN,COMP.,DIV. MAX LIMIT,DIV. BURST THRESHOLD,BUSRT TIME,SIM. SUBIDA,NIVEL SOPORTE', 'id_plan_isp,plan,comparticion,div_max_limit,div_burst_threshold,burst_time,sim_subida,id_nivel_soporte', '0,250,50,80,80,80,80,80', _altEdi, 'fac_planEditar(^);', _WR);
}
function fac_impPlanes()
{
    var h = "<input type='radio' id='z0' style='display:none' checked value='17' /><input type='radio' id='x' name='x' checked /> PDF &nbsp;&nbsp;&nbsp;&nbsp;&nbsp; <input type='radio' id='x2' name='x' /> Excel "+
            "<br/><br/><input type='button' value='Imprimir' onclick='imprimirReporte()'>";
    Ventana.crear('vta_im', 'Impresi&oacute;n de Planes', "ancho=250,alto=120,modal=true,cerrar=true", h);
}
/* VELOCIDADES */
function fac_velocidadNueva()
{
    var i=0;
    while(_('rTI'+i)!=null){
        i++;
    }
    var tr = document.createElement("tr");
    tr.id='rTI'+i;
    tr.className='jm_filaPar';
    tr.style.cursor='pointer';
    tr.style.verticalAlign='top';
    _('tblI').appendChild(tr);
    tr.innerHTML= "<td width='71'><input id='brLi"+i+"' type='text' size='5' maxlength='6' onkeypress=\"_numero(event);_NoE(event);\" onkeyup=\"fac_velocidadEditar(event, "+i+");\" /></td>"+
        "<td width='71' id='mxLi"+i+"' onclick=\"adm_tarifaVer("+i+")\" >&nbsp;</td>"+
        "<td width='81' id='brTh"+i+"' onclick=\"adm_tarifaVer("+i+")\" >&nbsp;</td>"+
        "<td width='61' id='brTi"+i+"' onclick=\"adm_tarifaVer("+i+")\" >&nbsp;</td>"+
        "<td width='71' id='lmAt"+i+"' onclick=\"adm_tarifaVer("+i+")\" >&nbsp;</td>"+
        "<td align='center' width='60'><input type='hidden' id='idPS"+i+"' value='-1' />" +
        "<div class='jm_icoCon' id='g"+i+"' style='visibility:hidden;background-position: -545px 0px;' onclick='fac_velocidadGuardar("+i+");' title='guardar'>&nbsp;</div>" +
        "<div class='jm_icoCon' id='e"+i+"' style='background-position:-545px -18px;' onclick='fac_velocidadEliminar("+i+");' title='eliminar'>&nbsp;</div></td>";
    _('inRe').style.visibility = 'hidden';
    _('sll').scrollTop = 10000;
    _('brLi'+i).focus();
}
function fac_velocidadEditar(e, i)
{
    var c = e.which || event.keyCode;
    if((c >= 48 && c <= 57) || (c >= 65 && c <= 90) || (c >= 96 && c <= 105) || c==8 || c==32 || c==46 || c==110 || c==190){
        _('g'+i).style.visibility = 'visible';
    }
}
function fac_velocidadGuardar(i)
{
    var brLi = _('brLi'+i).value;
    if(brLi==''){
        alert('Por favor, todos los campos son obligatorios.');
        return false;
    }
    _AJAX.solicitud("frmVelocidadGuardar", 'id='+_('id').value+'&idPS='+_('idPS'+i).value+'&brLi='+brLi+'&i='+i, true, 'body');
    _('g'+i).style.visibility = 'hidden';
    _('inRe').style.visibility = _('e'+i).style.visibility = 'visible';
}
function fac_postVelocidadGuardar(r, i)
{
    var jS = eval('(' + r + ')');
    _('mxLi'+i).innerHTML = jS.tbl[0][1];
    _('brTh'+i).innerHTML = jS.tbl[0][2];
    _('brTi'+i).innerHTML = jS.tbl[0][3];
    _('lmAt'+i).innerHTML = jS.tbl[0][4];
}
function fac_velocidadEliminar(i)
{
    if(confirm('¿Está seguro de querer eliminar el Busrt Limit del Plan de Internet?')){
        _AJAX.solicitud("EliminarVelocidad", 'id='+_('id').value+'&idPS='+_('idPS'+i).value+'&brLi='+_('brLi'+i).value, false, null);
    }
}
/* TARIFAS */
function adm_tarifaVer(i)
{
    Ventana.crear('cmp', 'COSTOS DE TARIFAS DE SERVICIO', "ancho=430,alto=320,modal=true,cerrar=true", '');
    _AJAX.solicitud('frmTarifa', 'idPS='+_('idPS'+i).value, false, 'cmp_html');
}
function fac_tarifaNueva()
{
    var i=0;
    while(_('TrTI'+i)!=null){
        i++;
    }
    var tr = document.createElement("tr");
    tr.id='TrTI'+i;
    tr.className='jm_filaPar';
    tr.style.verticalAlign='top';
    _('TtblI').appendChild(tr);
    tr.innerHTML= "<td width='120'><input id='fIni"+i+"' type='text' size='9' readonly />"
        + "<input type='button' value='...' onClick=\"SelectorFecha.crear('fIni"+i+"', 'SQL', 'fac_tarifaEditar(event, "+i+");');\" /></td>"+
        "<td width='120'><input id='fFin"+i+"' type='text' size='9' onkeypress=\"_SUP(event);\" onkeyup=\"fac_tarifaEditar(event, "+i+");\" />"
        + "<input type='button' value='...' onClick=\"SelectorFecha.crear('fFin"+i+"', 'SQL', 'fac_tarifaEditar(event, "+i+");');\" /></td>"+
        "<td width='60'><input id='tar"+i+"' type='text' size='6' onkeypress=\"_evaluar(event, '0123456789.');\" onkeyup=\"fac_tarifaEditar(event, "+i+");\" /></td>"+
        "<td align='center' width='70'><input type='hidden' id='idT"+i+"' value='-1' />" +
        "<div class='jm_icoCon' id='Tg"+i+"' style='visibility:hidden;background-position: -545px 0px;' onclick='fac_tarifaGuardar("+i+");' title='guardar'>&nbsp;</div>" +
        "<div class='jm_icoCon' id='Te"+i+"' style='background-position:-545px -18px;' onclick='fac_tarifaEliminar("+i+");' title='eliminar'>&nbsp;</div></td>";
    _('TinRe').style.visibility = 'hidden';
    _('Tsll').scrollTop = 10000;
}
function fac_tarifaEditar(e, i)
{
    if(e.type=='click'){
        _('Tg'+i).style.visibility = 'visible';
    }else{
       var c = e.which || event.keyCode;
        if((c >= 48 && c <= 57) || (c >= 65 && c <= 90) || (c >= 96 && c <= 105) || c==8 || c==32 || c==46 || c==110 || c==190){
            _('Tg'+i).style.visibility = 'visible';
        }
    }
}
function fac_tarifaGuardar(i)
{
    var fi = _('fIni'+i).value;
    var ff = _('fFin'+i).value;
    var t = _('tar'+i).value;
    if(fi=='' || ff=='' || t==''){
        alert('Por favor, todos los campos son obligatorios.');
        return false;
    }
    if(SelectorFecha.getTimestamp(fi) >= SelectorFecha.getTimestamp(ff)){
        alert('La fecha de vigencia hasta, debe ser mayor a la fecha de vigencia desde.');
        return false;
    }
    _AJAX.solicitud("frmTarifaGuardar", 'id='+_('idPlanServicio').value+'&idT='+_('idT'+i).value+'&fi='+fi+'&ff='+ff+'&t='+t+'&obj='+'idT'+i, true, 'body');
    _('Tg'+i).style.visibility = 'hidden';
    _('TinRe').style.visibility = _('Te'+i).style.visibility = 'visible';
}
function fac_tarifaEliminar(i)
{
    if(confirm('¿Está seguro de querer eliminar de forma permanente la tarifa?')){
        _AJAX.solicitud("EliminarTarifa", 'idPlanServicio='+_('idPlanServicio').value+'&c='+_('idT'+i).value, false, null);
    }
}
/* SECTORES */
function fac_sectores()
{
    encerar();
    /*_('d_filtro').innerHTML = 'Sector: <input type="text" id="t_b" onkeypress="var c=(_esN)?event.which:event.keyCode;if(c==13){fac_sectorBuscar();}">'+
        '&nbsp;<input type="button" value="Filtrar" onclick="fac_sectorBuscar();">';*/
    if(_MENUS_._enArreglo("sectorNuevo")>=0){
        btn.icono(-360, 2, 34, 34, 'Nuevo Sector', "fac_sectorEditar('-1');", 'd_herra');
    }
    if(_MENUS_._enArreglo("sectorEliminar")>=0){
        btn.icono(-391, 3, 34, 34, 'Eliminar', "_objT.eliminar();", 'd_herra');
    }
    if(_MENUS_._enArreglo("sectorImprimir")>=0){
        btn.icono(-455, -114, 34, 34, 'Imprimir', "fac_impSectores();", 'd_herra');
    }
    _AJAX.solicitud('frmFiltro', 'op=28&fn=fac_sectorBuscar()', false, 'd_filtro');
    //fac_sectorBuscar();
    setAnchoPanel(2, 450);
}
function fac_sectorEditar(k)
{
    _('d_ayuda').innerHTML = 'El valor del costo de instalaci&oacute;n ya incluyen el IVA. <br />'+msg();
    _AJAX.solicitud('frmSector', 'id='+k+'&altB='+_altBody, false, 'd_21');
}
function fac_sectorGuardar(f)
{
    if(f.sector.value=='' || f.costo_instalacion.value=='' || f.costo_instalacion_fibra.value=='' || f.id_producto.value==''){
        alert('Todos los campos marcados con  *  son obligatorios.');
        return false;
    }
    return _AJAX.enviarForm(f);
}
function fac_sectorBuscar()
{
    var w='where id_sucursal='+_('idSuc').value;
    if(_('t_b')!=null){
        var a=_('t_b').value;
        if(a!=''){
            w += " and lower(sector) like '%"+a.toLowerCase()+"%'";
        }
    }
    _WR = encodeURI(w);
    new Tabla('d_11', 'jmTbl', '', 'vta_sector', 'SECTOR,COST. INSTALACION,COST. INST. FIBRA', 'id_sector,sector,costo_instalacion,costo_instalacion_fibra', '0,200,100,100', _altEdi, 'fac_sectorEditar(^);', _WR);
}
function fac_impSectores()
{
    var h = "<input type='radio' id='z0' style='display:none' checked value='18' /><input type='radio' id='x' name='x' checked /> PDF &nbsp;&nbsp;&nbsp;&nbsp;&nbsp; <input type='radio' id='x2' name='x' /> Excel "+
            "<br/><br/><input type='button' value='Imprimir' onclick='imprimirReporte()'>";
    Ventana.crear('vta_im', 'Impresi&oacute;n de Sectores', "ancho=250,alto=120,modal=true,cerrar=true", h);
}
/* MATERIALES */
function fac_materiales()
{
    encerar();
    if(_MENUS_._enArreglo("materialesNuevo")>=0){
        btn.icono(-360, 2, 34, 34, 'Nuevo Sector', "fac_materialEditar('-1');", 'd_herra');
    }
    if(_MENUS_._enArreglo("materialesQuitar")>=0){
        btn.icono(-391, 3, 34, 34, 'Eliminar', "_objT.eliminar('', 'Eliminar');", 'd_herra');
    }
    _AJAX.solicitud('frmFiltro', 'op=30&fn=fac_materialBuscar()', false, 'd_filtro');
    setAnchoPanel(2, 400);
}
function fac_getMateriales(f)
{
    fac_cmbActivo('axMat');
    _AJAX.solicitud('getProductosMateriales', 't='+_('txtBus').value+'&d=axMat&o=Fech_&f='+f);
}
function fac_putProductos(i)
{
    _('id_producto').value=_('idPr'+i).value;
    _('txtBus').value=_('desPr'+i).innerHTML;
    _Fch=true;
    fac_cmbActivoRem();
    
}
function fac_materialEditar(k)
{
    _('d_ayuda').innerHTML = msg();
    _AJAX.solicitud('frmMaterial', 'id='+k+'&altB='+_altBody, false, 'd_21');
}
function fac_materialGuardar(f)
{
    if(f.id_sucursal.value=='' || f.id_producto.value=='' || f.id_plan_cuenta_gasto.value==''){
        alert('Todos los campos marcados con  *  son obligatorios.');
        return false;
    }
    return _AJAX.enviarForm(f);
}
function fac_materialBuscar()
{
    var w='where id_sucursal='+_('idSuc').value;
    _WR = encodeURI(w);
    new Tabla('d_11', 'jmTbl', '', 'vta_material', 'CODIGO,DESCRIPCION', 'id_material,codigo,descripcion', '0,200,300', _altEdi, 'fac_materialEditar(^);', _WR);
}
/* CLIENTES */
function fac_clientes()
{
    encerar();
    if(_MENUS_._enArreglo("clienteNuevo")>=0){
        btn.icono(-360, 2, 34, 34, 'Nuevo Cliente', "fac_clienteEditar('-1');", 'd_herra');
    }
    if(_MENUS_._enArreglo("clienteEliminar")>=0){
        btn.icono(-391, 3, 34, 34, 'Eliminar', "_objT.eliminar();", 'd_herra');
    }
    if(_MENUS_._enArreglo("clienteImprimir")>=0){
        btn.icono(-455, -114, 34, 34, 'Imprimir', "fac_impClientes();", 'd_herra');
    }
    _AJAX.solicitud('frmFiltro', 'op=25&fn=fac_buscarCliente()', false, 'd_filtro');
    setAnchoPanel(2, 20);
}
function fac_clienteEditar(k)
{
    _('d_ayuda').innerHTML = msg();
    Ventana.crear('cmp', 'CLIENTE', "ancho=750,alto=500,modal=true,cerrar=true", '');
    new Tab('cmp_html', 730, 385);
    _AJAX.solicitud('frmCliente', 'id='+k+'&altB=385', false, 'cmp_html');
}
function fac_setFormaP()
{
    var fp = _('forma_pago').value;
    _('axsilCta').style.display = (fp=='CTA' ? 'block' : 'none');
    _('axTipTar').style.display = (fp=='TAR' ? 'block' : 'none');
    if(fp==''){
        _('tipo_cuenta').value=_('tipo_tarjeta_credito').value=_('tarjeta_credito_caduca').value='';
    }
    if(fp=='CTA'){
        _('tipo_tarjeta_credito').value=_('tarjeta_credito_caduca').value='';
    }
    if(fp=='TAR'){
        _('tipo_cuenta').value='';
    }
}
function fac_clienteGuardar(f)
{
    var td = _('tipo_documento').value;
    var ru = _('ru').value;
    var rs = _('rs').value;
    var di = _('di').value;
    if(ru=='' || rs=='' || di==''){
        alert('Todos los campos marcados con  *  son obligatorios.');
        return false;
    }
    if(td!='06'){
        if(td=='04'){
            if(ru.length!=13){
                alert('El número del RUC debe tener 13 dígitos');
                return false;
            }
        }
        if(td=='05'){
            if(ru.length!=10){
                alert('El número de cédula debe tener 10 dígitos');
                return false;
            }
        }
        if(td!='06'){
            if(!esDocumento('ru')){
                return false;
            }
        }
    }
    if(_('ma').value!=''){
        if(!_esMail('ma')){
            return false;
        }
    }
    var doc = _('documento').value;
    var clDe = _('cliente_debito').value;
    var nuCu = _('num_cuenta').value;
    if(doc!='' || clDe!='' || nuCu!=''){
        if(_('forma_pago').value == 'CTA'){
            if(doc=='' || clDe=='' || nuCu=='' || _('tipo_doc_debito').value=='' || _('forma_pago').value=='' || _('tipo_cuenta').value==''){
                alert('Todos los datos para el convenio de débito son obligatorios.');
                return false;
            }
        }
        if(_('forma_pago').value == 'TAR'){
            if(f.tipo_tarjeta_credito.value=='' || f.tarjeta_credito_caduca.value=='') {
                alert('Por favor, ingrese los datos de la tarjeta de crédito.');
                return false;
            }
        }
    }
    return _AJAX.enviarForm(f);
}
function fac_buscarCliente()
{
    var w='where';
    if(_('idSuc').value!='-0'){
        w+=" id_cliente in (select distinct id_cliente from tbl_instalacion where anulado=false and estado_servicio not in ('t','e') and id_sucursal="+_('idSuc').value+" )";
    }
    var a=_('t_b').value;
    if(a!=''){
        var ax = esNumero(a) ? "or id_cliente in (select id_cliente from tbl_contrato where num_contrato="+a+" and anulado=false and terminado=false)" : "";
        w = "where (lower(ruc) like '"+a.toLowerCase()+"%' or lower(razon_social) like '%"+a.toLowerCase()+"%' "+ax+")";
    }
    if(w=='where'){
        w='';
    }
    _WR = encodeURI(w);   
    new Tabla('d_11', 'jmTbl', '', 'vta_cliente', 'RUC,RAZ&Oacute;N SOCIAL', 'id_cliente,ruc,razon_social', '0,100,250', _altEdi, 'fac_clienteEditar(^);', _WR);
}
function fac_impClientes()
{
    var h = "<input type='radio' id='z0' name='z' checked='checked' value='7' /> <span onclick=\"_('z0').checked='true'\">Clientes </span>";
    h += "<br><input type='radio' id='z1' name='z' value='19' /> <span onclick=\"_('z1').checked='true'\">Contratos por terminar vigencia</span>";
    h += "<br><input type='radio' id='z2' name='z' value='52' /> <span onclick=\"_('z2').checked='true'\">Clientes con convenio de d&eacute;bito</span>";
    h += "<br><input type='radio' id='z3' name='z' value='53' /> <span onclick=\"_('z3').checked='true'\">Clientes con capacidades especiales y tercera edad</span>";
    h += "<hr><input type='radio' id='x' name='x' checked /> PDF &nbsp;&nbsp;&nbsp;&nbsp;&nbsp; <input type='radio' id='x2' name='x' /> Excel";
    h += "<br><br><input type='button' value='Imprimir' onclick='imprimirReporte2();' />";
    Ventana.crear('vta_im', 'Impresi&oacute;n de Clientes', "ancho=320,alto=220,modal=true,cerrar=true", h);
}
/* CONTRATOS */
function fac_setContrato()
{
    var idS = _('idSucur').value;
    var jS = eval('(' + _('JSN').value + ')');
    var ps = _enMatrizJSON(jS, idS, 0);
    var sec= jS.tbl[ps][1];
    _('numCont').value = sec;
    _('auxCont').style.display='block';
    _('btnCont').style.display=_('btnImp').style.display='none';
    var c = _('borrContrato').value.replace(/<<numero>>/g, idS + '-' + sec);
    c = c.replace(/<<fecha_inicio>>/g, _('fechaCont').value);
    _('contrato').value=c;
}
function fac_contratoGuardar(f)
{
    if(f.numCont.value=='' || f.contrato.value==''){
        alert('Todos los campos son obligatorios.');
        return false;
    }
    if(f.ruc_representante.value=='' && f.representante.value==''){
    }else{
        if(f.ruc_representante.value=='' || f.representante.value==''){
            alert('Si ingresa un representante debe ingresar la CI/RUC y el nombre.');
            return false;
        }
    }
    return _AJAX.enviarForm(f);
}
/* INSTALACIONES */
function fac_instalacion()
{
    encerar();
    if(_MENUS_._enArreglo("installNueva")>=0){
        btn.icono(-360, 2, 34, 34, 'Nueva Instalaci&oacute;n', "fac_installEditar('-1');", 'd_herra');
    }
    //if(_MENUS_._enArreglo("installImprimir")>=0){
        btn.icono(-455, -114, 34, 34, 'Imprimir', "fac_impInstalls()", 'd_herra');
    //}
    if(_MENUS_._enArreglo("installDescargar")>=0 || _MENUS_._enArreglo("installCargar")>=0){
        btn.separador('d_herra');
    }
    if(_MENUS_._enArreglo("installDescargar")>=0){
        //btn.icono(-175, -156, 34, 34, 'Descargar archivo de suspensiones para el servidor', "imprimir('Suspensiones');", 'd_herra');
        //btn.icono(-175, -156, 34, 34, 'Descargar archivo de suspensiones para el servidor por listas de cortes', "imprimir('SuspensionesListas');", 'd_herra');
        btn.icono(-413, -193, 34, 34, 'Descargar archivo de activaciones para el servidor por listas de activos', "fac_impCortes()", 'd_herra');
        btn.icono(-175, -156, 34, 34, 'Descargar archivo de colas de planes del servidors', "fac_impArchivoPlanes()", 'd_herra');
        btn.icono(-175, -156, 34, 34, 'Descargar archivo de colas de planes del servidors pooll', "fac_impArchivoPlanesPooll()", 'd_herra');
        btn.icono(-175, -156, 34, 34, 'Descargar archivo de activaciones para el servidor por listas por planes', "fac_activacionesListasPlanes()", 'd_herra');
    }
    if(_MENUS_._enArreglo("installCargar")>=0){
        btn.separador('d_herra');
        //btn.icono(-209, -156, 34, 34, 'Cargar archivo de planes del servidor', "fac_setArchivoPlanes('ProcesaArchivoPlanes')", 'd_herra');
        btn.icono(-209, -156, 34, 34, 'Cargar archivo del AirControl', "fac_setArchivoPlanes('airControlUpdate')", 'd_herra');
    }
    _AJAX.solicitud('frmFiltro', 'op=17&fn=fac_installBuscar()', false, 'd_filtro');
    setAnchoPanel(2, 20);
}
function fac_installEditar(k)
{
    _('d_ayuda').innerHTML = msg();
    Ventana.crear('cmp', 'INSTALACION', "ancho=800,alto=550,modal=true,cerrar=true", '');
    new Tab('cmp_html', 750, 420);
    _AJAX.solicitud('frmInstalacion', 'id='+k+'&altB=420', false, 'cmp_html');
}
function fac_setInstalacion()
{
    var idS = _('id_sucursal').value;
    _('auxIdSuc').innerHTML = idS;
    var jS = eval('(' + _('borrInstall').value + ')'); 
    var i = _enMatrizJSON(jS, idS, 0);
    _('num_instalacion').value = jS.tbl[i][1];
    /*jS = eval('(' + _('borrIPs').value + ')');
    i = _enMatrizJSON(jS, idS, 0);
    _('ip').value = jS.tbl[i][2]!='' ? jS.tbl[i][2] : jS.tbl[i][1];*/
}
function fac_setSectores()
{
    _AJAX.solicitud('getSectores', 'id='+_('id_sucursal').value, false, 'axIdSec');
}
function fac_setPlanes()
{
    var idS = _('id_sector').value;
    if(idS!='-0'){
        var jS = eval('(' + _('borrSect').value + ')'); 
        var i = _enMatrizJSON(jS, idS, 0); 
        var cIns = jS.tbl[i][2];
        if(_('tipo_instalacion').value=='f'){
            cIns = jS.tbl[i][7];
        }
        var st = _RD(cIns / 1.12);
        _('auxIdSuc').innerHTML = _('id_sucursal').value;
        _('FidPr0').value = jS.tbl[i][3];
        _('cD00').innerHTML = jS.tbl[i][4];
        _('dSF01').innerHTML = jS.tbl[i][5];
        _('Fpu0').value = _('Fsbt0').value = _('subt').value =  _('subt2').value = st;
        _('Fv0').value = _('iva2').value = _RD(cIns - st);
        _('Ftt0').value = _('costo_instalacion').value = _('tt').value = cIns;
        _AJAX.solicitud('getPlanes', 'id='+idS+'&obj=auxPlanes', false, 'auxPlanes');
    }
}
function fac_getClienteInstall()
{
    if(_('ruc').value!=''){
        _AJAX.solicitud('getClienteInstalacion', 'estab='+_('estab').value+'&ruc='+_('ruc').value, true, '');
    }
}
function fac_setClienteInstall()
{
    if(_('id_cliente').value!=''){
        _('rucf').value=_('ruc').value;
        _('raSo').value=_('axRS').innerHTML;
        _('tel').value=_('axTelf').value;
        _('dir').value=_('direccion_instalacion').value;
    }else{
        _('datCliente').innerHTML = '';
        alert('Cliente no registrado o no tiene un contrato vigente por el servicio de Internet.');
    }
    fac_setDetalleInstall();
}
function fac_setDetalleInstall()
{
    _('ob').value = "Ingresos por instalación Factura Nro. " + _('sf').value + "-" + _('nf').value + ".";
}
function fac_installGuardar(f)
{
    f.action='frmInstalacionGuardar';
    var nf = f.nf.value;
    if(f.id_cliente.value=='' || f.id_sucursal.value=='' || f.id_sector.value=='' || f.id_sector.value=='-0' || 
        f.id_plan_contratado.value=='' || f.id_plan_contratado.value=='-0' || f.direccion_instalacion.value=='' || 
        f.sf.value=='' || nf=='' || f.auf.value=='' || f.rucf.value=='' || f.prr.value==''){
        alert('Todos los campos marcados con  *  son obligatorios.');
        return false;
    }
    /*var ip = f.ip.value.split('.');
    if(ip.length!=4){
        alert('Dirección IP inválida.');
        return false;
    }
    for(var i=0; i<4; i++){
        var n = parseInt(ip[i]);
        if(n >= 255 ){
            alert('Dirección IP inválida. Los octetos deben estar en el rango de 1 - 254.');
            return false;
        }
    }*/
    if(f.id==-1){
        if(_('pCh').style.display=='block'){
            if( _('chq').value=='' || _('bco').value==''){
                alert('Todos los campos marcados con  *  son obligatorios.');
                return false;
            }else{
                _('son').value=FloatToStr(_('tt').value);
            }
        }else{
            _('son').value='';
        }

        //    retencion

        var sr = _('sr').value;
        var nr = _('nr').value;
        var aur = _('aur').value;
        var fer = _('fer').value;
        var ef = _('ef').value;
        var tvr = _('tvr').value;
        if(tvr!='' && tvr!='0'){
            if(sr=='' || aur=='' || nr=='' || fer=='' || ef==''){
                alert('Por favor, ingrese todos los datos para la retención.');
                return false;
            }
            var idR = '';
            var bi = '';
            var vr ='';
            i=0;
            while(i<10){
                if(_('idR'+i)!=null){
                    idR += _('idR'+i).value+',';
                    bi += _('bi'+i).value+',';
                    vr += _('vr'+i).value+',';
                }
                i++;
            }
            if(i==0){
                alert('Por favor, falta ingresar el detalle de la retención.');
                return false;
            }
        }

        if(SelectorFecha.getTimestamp(_('fe').value) > SelectorFecha.getTimestamp(_('feCad').value) || (nf < _('secDe').value || nf > _('secHa').value) ){
            if(confirm("Por favor revise la fecha de caducidad del facturero o el número secuencial de la factura. \n Si los datos son correctos pulse ACEPTAR para poder guardar la factura.")){
                if(confirm("Una vez registrada la instalación ya no podrá modificar la infortmación referente a la factura. \n¿Está seguro(a) de guardar el formulario de instalación?")){
                    return _AJAX.enviarForm(f);
                }
            }
        }else{
            if(confirm("Una vez registrada la instalación ya no podrá modificar la infortmación referente a la factura. \n¿Está seguro(a) de guardar el formulario de instalación?")){
                return _AJAX.enviarForm(f);
            }
        }
    }else{
        return _AJAX.enviarForm(f);
    }
    return false;
}
function fac_installActualizar(f)
{
    f.action='frmInstalacionGuardar';
    if(f.id_cliente.value=='' || f.num_instalacion.value=='' || f.id_sucursal.value=='' || f.id_sector.value=='' || f.id_plan_contratado.value==''
        || f.direccion_instalacion.value=='' || f.ip.value==''){
        alert('Todos los campos marcados con  *  son obligatorios.');
        return false;
    }
    return _AJAX.enviarForm(f);
}
function fac_userActualizar()
{
    if(confirm('¿Esta seguro(a) de actualizar el usuario en el FREERADIUS?')){
        _AJAX.solicitud('radActualizarUsuario', 'id='+_('id').value+'&user='+_('radusername').value+'&user_ant='+_('radusername_ant').value);
    }
}
function fac_setArchivoPlanes(p)
{
    Ventana.crear('vta_subir', 'Transferencia de Archivos', "ancho=440,alto=110,modal=true,cerrar=true", '');
    _AJAX.solicitud('SubirArchivo', 'pr='+p, true, 'vta_subir_html')
}
function fac_finTransferencia(e,m)
{
    if(e==0){
        _R('bloq_vta_subir');
        _R('vta_subir');
    }
    alert(m);
    if(_('cargando')!=null){
        _('cargando').style.display = 'none';
    }
    if(_('frmArchivo')!=null){
        _('frmArchivo').style.display = 'block';
    }
    if(_('archivo')!=null){
        _('archivo').value='';
    }
    return false;
}
function fac_installBuscar()
{
    var w = "where fecha_registro between '"+_('fi').value+"' and '"+_('ff').value+"'";
    if(_('idSuc').value!='-0'){
        w += " and id_sucursal="+_('idSuc').value;
    }
    
    if(_('idSec').value!='-0'){
        w += " and id_sector="+_('idSec').value;
    }
    if(_('idAnt').value!='-0'){
        w += " and antena_acoplada='"+_('idAnt').value+"'";
    }
    if(_('idPlSe').value!='-0'){
        w += " and id_plan_actual="+_('idPlSe').value;
    }
    
    if(_('esSer').value!='-0'){
        w += " and estado_servicio='"+_('esSer').value+"'";
    }
    var b = _('t_b').value.toLowerCase();
    if(b!=''){
        w = "where id_sucursal="+_('idSuc').value + " and (ip::varchar like '"+b+"%' or lower(ruc) like '"+b+"%' or lower(razon_social) like '%"+b+"%' or lower(mac) like '%"+b+"%' or lower(radusername) like '"+b+"%')";
    }  
    _WR = encodeURI(w); 
    new Tabla('d_11', 'jmTbl', '', 'vta_instalacion', 'RUC,CLIENTE,IP,ESTADO', 'id_instalacion,ruc,razon_social,ip,txt_estado_servicio', '0,90,200,100,100', _altEdi, 'fac_installEditar(^);', _WR);
}
function fac_postInstallGuardar()
{
    var f = _('formVenta');
    if(f.receptor.value=='' || f.porcentaje_senal.value=='' || f.antena_acoplada.value==''){
        alert('Todos los campos son obligatorios');
        return false;
    }
    if(f.lat_h.value!='' || f.lat_m.value!='' || f.lat_s.value!='' || f.lon_h.value!='' || f.lon_m.value!='' || f.lon_s.value!=''){
        if(f.lat_h.value=='' || f.lat_m.value=='' || f.lat_s.value=='' || f.lon_h.value=='' || f.lon_m.value=='' || f.lon_s.value==''){
            alert('Por favor, ingrese todos los datos para las coordenadas GPS latitud y longitud');
            return false;
        }
        if(parseInt(f.lat_h.value)>=90 || parseInt(f.lon_h.value)>=180 || parseInt(f.lat_m.value)>60 || parseInt(f.lon_m.value)>60 || parseFloat(f.lat_s.value)>60 || parseFloat(f.lon_s.value)>60){
            alert('Por favor, verifique las coordenadas GPS latitud y longitud');
            return false;
        }
    }
    _('tope').value = _('tblD').childNodes.length>0 ? parseInt(_('tblD').childNodes.item(_('tblD').childNodes.length-1).id.replace('r', ''))+1 : 0;
    f.action='frmPostInstalacionGuardar';
    return _AJAX.enviarForm(f);
}
function fac_instalacionAnular(i)
{
    if(confirm('Se anularán también todos los documentos asociados al documento de inatalación tales como: factura, retención, comprobante de diario y comprobantes de cobros.')){
        _AJAX.solicitud('frmInstalacionAnular', 'id='+i+'&WHERE='+_WR, true, 'body');
    }
}
function fac_impInstalls()
{
    var F = new Date();
    var hoy = F.getDate()+'/'+(F.getMonth()+1)+'/'+F.getFullYear();
    var h = "<input type='radio' id='z0' name='z' checked='checked' value='20' /> <span onclick=\"_('z0').checked='true'\">Instalaciones y clientes </span>";
    h += "<br><input type='radio' id='z1' name='z' value='21' /> <span onclick=\"_('z1').checked='true'\">Instalaciones pendientes </span>";
    h += "<br><input type='radio' id='z2' name='z' value='22' /> <span onclick=\"_('z2').checked='true'\">Conformidad de instalaciones </span>";
    h += "<br><input type='radio' id='z3' name='z' value='23' /> <span onclick=\"_('z3').checked='true'\">Listado de clientes por retirar equipos instalados </span>";
    h += "<br><input type='radio' id='z4' name='z' value='34' /> <span onclick=\"_('z4').checked='true'\">Planes de servicio de Internet </span>";
    h += "<br><input type='radio' id='z5' name='z' value='25' /> <span onclick=\"_('z5').checked='true'\">Cadena de correos electr&oacute;nicos </span>";
    h += "<br><input type='radio' id='z6' name='z' value='31' /> <span onclick=\"_('z6').checked='true'\">Direcciones IPs a reutilizar </span>";
    h += "<br><input type='radio' id='z7' name='z' value='48' /> <span onclick=\"_('z7').checked='true'\">Instalaciones terminadas desde la fecha: <input type='text' id='bfi' value='"+hoy+"' size='9' readOnly><input type='button' value='...' onClick=\"SelectorFecha.crear('bfi', 'SQL');\" /></span>";
    h += "<br><input type='radio' id='z8' name='z' value='49' /> <span onclick=\"_('z8').checked='true'\">Direcciones IPs no pre-facturbles </span>";
    h += "<br><input type='radio' id='z9' name='z' value='50' /> <span onclick=\"_('z9').checked='true'\">Post-instalaciones pendientes </span>";
    
    h += "<br><input type='radio' id='z10' name='z' value='54' /> <span onclick=\"_('z10').checked='true'\">Instalaciones por estados</span>";
    
    h += "<hr><input type='radio' id='x' name='x' checked /> PDF &nbsp;&nbsp;&nbsp;&nbsp;&nbsp; <input type='radio' id='x2' name='x' /> Excel";
    h += "<br><br><input type='button' value='Imprimir' onclick='imprimirReporte2();' />";
    Ventana.crear('vta_im', 'Impresi&oacute;n de Instalaciones', "ancho=390,alto=380,modal=true,cerrar=true", h);
}
function fac_cambioClienteEditar(k)
{
    Ventana.crear('cmp1', 'CAMBIO DE RAZON SOCIAL', "ancho=360,alto=200,modal=true,cerrar=true", '');
    _AJAX.solicitud('frmCambioCliente', 'id='+k, false, 'cmp1_html');
}
function fac_getClienteCambio()
{
    _AJAX.solicitud('getCliente', 'estab='+_('estab').value+'&ruc='+_('caRuc').value+'&fun=fac_setCambioCliente()', false, 'body');
}
function fac_setCambioCliente()
{
    var c = _('borrCliente').innerHTML;
    var vec = c.split('|');
    if(vec[0] !='' && vec[0]!='{tbl:]}'){
        var jS = eval('(' + vec[0] + ')');
        _('idCliNue').value=jS.tbl[0][0];
        _('caRaZo').innerHTML=jS.tbl[0][2];
        _('caTel').innerHTML=jS.tbl[0][4];
        _('caDir').innerHTML=jS.tbl[0][5];
    }else{
        _('idCliNue').value='';
        _('caDir').innerHTML=_('caTel').innerHTML=_('caRaZo').innerHTML=_('caRuc').value='';
        alert('Cliente no registrado.');
    }
}
function fac_clienteCambioGuardar(f)
{
    if(f.idCliNue.value=='' || _('id').value=='' || _('id').value=='-1'){
        return false;
    }
    _('idIns').value=_('id').value;
    if(confirm('Una vez registrado el cambio de razón social (cliente) ya no podrá modificarlo.')){
        return _AJAX.enviarForm(f);
    }
    return false;
}
function fac_cambioPlanNuevo()
{
    var i=0;
    while(_('rCP'+i)!=null){
        i++;
    }
    var tr = document.createElement("tr");
    tr.id='rCP'+i;
    tr.className='jm_filaPar';
    _('tblCP').appendChild(tr);
    var F = new Date();
    var a = F.getFullYear();
    var m = F.getMonth()+1;
    /*if(m>12){
        a++;
        m=1
    }*/
    var fi = '1/'+m+'/'+a;
    var cl = _('ax0').cloneNode(true);
    cl.innerHTML = cl.innerHTML.replace(/_Xx_/g, 'idPlSvr'+i);
    cl.innerHTML = cl.innerHTML.replace(/_Ii_/g, i);
    var ht = "<td width='200'><input type='hidden' id='idCP"+i+"' value='-1'>"+cl.innerHTML+"</td>"+
                "<td width='151'><input id='fchC"+i+"' type='text' size='10' maxlength='10' value='"+fi+"' readonly />"+ 
                //"<input type='button' value='...' onClick=\"SelectorFecha.crear('fchC"+i+"', 'SQL', 'fac_cambioPlanEditar("+i+");');\" /></td>"+
                "<td width='61'>";
    if(_MENUS_._enArreglo("cambioPlanNuevo")>=0 || _MENUS_._enArreglo("cambioPlanEditar")>=0){
        ht += "<div class='jm_icoCon' id='cg"+i+"' style='background-position:-545px 0px;' title='guardar' onclick='fac_cambioPlanGuardar("+i+");'>&nbsp;</div>";
    }
    ht += "<div class='jm_icoCon' id='ci"+i+"' style='visibility:hidden;background-position: -314px -159px;' onclick='fac_cambioPlanImprimir("+i+");' title='imprimir solicitud'>&nbsp;</div>";
    if(_MENUS_._enArreglo("cambioPlanEliminar")>=0){
        ht += "<div class='jm_icoCon' id='ce"+i+"' style='visibility:hidden;background-position:-545px -18px;' onclick='fac_cambioPlanEliminar("+i+");'>&nbsp;</div>";
    }
    ht += "</td>";
    tr.innerHTML=ht;
    _('inCP').style.visibility = 'hidden';
    _('sllCP').scrollTop = 10000;
}
function fac_cambioPlanEditar(i)
{
    _('cg'+i).style.visibility = 'visible';
}
function fac_cambioPlanGuardar(i)
{
    _AJAX.solicitud("frmCambioPlanGuardar", 'id='+_('id').value+'&idCP='+_('idCP'+i).value+'&fchC='+_('fchC'+i).value+'&idPlSvr='+_('idPlSvr'+i).value+'&i='+i, true, 'body');
    _('cg'+i).style.visibility = 'hidden';
    _('idCP').style.visibility = _('ce'+i).style.visibility = _('ci'+i).style.visibility = 'visible';
}
function fac_cambioPlanEliminar(i)
{
    if(confirm('¿Está seguro de querer eliminar la solicitud de cambio de plan?')){
        _AJAX.solicitud("EliminarCambioPlan", 'c='+_('idCP'+i).value+'&i='+i, false, null);
    }
}
function fac_cambioPlanImprimir(i)
{
    imprimir('pdfCambioPlan?id='+_('idCP'+i).value);
}
function fac_suspensionNueva()
{
    var i=0;
    while(_('rS'+i)!=null){
        i++;
    }
    var tr = document.createElement("tr");
    tr.id='rS'+i;
    tr.className='jm_filaPar';
    _('tblS').appendChild(tr);
    var F = new Date();
    var a = F.getFullYear();
    var m = F.getMonth()+2;
    if(m>12){
        a++;
        m=1
    }
    var fi = '1/'+m+'/'+a;
    //var ht = "<td width='150'><input type='hidden' id='idSus"+i+"' value='-1'><select id='tipo"+i+"' onchange='fac_suspensionEditar("+i+");'><option value='t'>Temporal</option><option value='d'>Definitiva</option></select></dt>"+
    var ht = "<td width='101'>&nbsp;</td><td width='150'><input type='hidden' id='idSus"+i+"' value='-1'><input id='fchI"+i+"' type='text' size='10' maxlength='10' value='"+fi+"' readonly />"+
            "<input type='button' value='...' onClick=\"SelectorFecha.crear('fchI"+i+"', 'SQL', 'fac_suspensionEditar("+i+");');\" /></td>"+
            "<td width='101'><select id='tmp"+i+"' onchange='fac_suspensionEditar("+i+");'><option value='0'>un mes</option><option value='1'>dos meses</option><option value='2'>tres meses</option><option value='-1'>definitiva</option></select></td>"+
            "<td width='151'>&nbsp;</td><td width='101'>&nbsp;</td><td width='91'>";
    if(_MENUS_._enArreglo("suspensionNueva")>=0 || _MENUS_._enArreglo("suspensionEditar")>=0){        
        ht += "<div class='jm_icoCon' id='g"+i+"' style='background-position:-545px 0px;' title='guardar' onclick='fac_suspensionGuardar("+i+");'>&nbsp;</div>";
    }
    ht += "<div class='jm_icoCon' id='i"+i+"' style='visibility:hidden;background-position: -314px -159px;' onclick='fac_suspensionImprimir("+i+");' title='imprimir solicitud'>&nbsp;</div>";
    if(_MENUS_._enArreglo("suspensionEliminar")>=0){
        ht += "<div class='jm_icoCon' id='e"+i+"' style='visibility:hidden;background-position:-545px -18px;' onclick='fac_suspensionEliminar("+i+");'>&nbsp;</div>";
    }
    ht += "</td>";
    tr.innerHTML=ht;
    _('inSus').style.visibility = 'hidden';
    _('sll').scrollTop = 10000;
}
function fac_suspensionEditar(i)
{
    _('g'+i).style.visibility = 'visible';
}
function fac_suspensionAnular(i)
{
    _('a'+i).style.visibility = 'visible';
}
function fac_suspensionGuardar(i)
{
    var ty = _('tmp'+i).value=='-1' ? 'd' : 't';
    _AJAX.solicitud("frmSuspensionGuardar", 'id='+_('id').value+'&idSus='+_('idSus'+i).value+'&tipo='+ty+'&fchI='+_('fchI'+i).value+'&tmp='+_('tmp'+i).value+'&i='+i+'&imp=0', true, 'body');
    _('g'+i).style.visibility = 'hidden';
    _('inSus').style.visibility = _('e'+i).style.visibility = _('i'+i).style.visibility = 'visible';
}
function fac_suspensionAnularGuarar(i)
{
    var ty = _('tmp'+i).value=='-1' ? 'd' : 't';
    _AJAX.solicitud("frmSuspensionAnular", 'id='+_('id').value+'&idSus='+_('idSus'+i).value+'&fchI='+_('fchI'+i).value+'&fchR='+_('fchR'+i).value+'&i='+i+'&imp=0', true, 'body');
    _('a'+i).style.visibility = 'hidden';
    _('inSus').style.visibility = _('e'+i).style.visibility = _('i'+i).style.visibility = 'visible';
}
function fac_suspensionEliminar(i)
{
    if(confirm('¿Está seguro de querer eliminar la solicitud de suspensión?')){
        _AJAX.solicitud("EliminarSuspension", 'c='+_('idSus'+i).value+'&i='+i, false, null);
    }
}
function fac_suspensionImprimir(i)
{
    imprimir('pdfSuspension?id='+_('idSus'+i).value);
}
function fac_suspensionAnulacionImprimir(i)
{
    imprimir('pdfSuspensionAnulacion?id='+_('idSus'+i).value);
}
function fac_desInstallGuardar()
{
    var f = _('formVenta');
    if(f.num_desinstalacion.value=='' || f.motivo_desinstalacion.value==''){
        alert('Todos los campos son obligatorios');
        return false;
    }
    f.action='frmDesInstalacionGuardar';
    if(confirm("Una vez registrada la des-instalación ya no podrá des-hacer la operación. \n¿Está seguro(a) de registrar la des-instalación?")){
        return _AJAX.enviarForm(f);
    }
    return false;
}
function fac_desInstallTerminar()
{
    var f = _('formVenta');
    if(f.observacion_terminado.value==''){
        alert('Todos los campos son obligatorios');
        return false;
    }
    f.action='frmDesInstalacionTerminar';
    return _AJAX.enviarForm(f);
}
function fac_getReceptor(x, f)
{
    var af = f || 0;
    var pf = af==1 ? '_ant' : (af==2 ? '_act' : '');
    var i= !isNaN( parseInt(x) ) ? x : ''; 
    _AJAX.solicitud('getReceptor', 'mac='+_('mac'+pf+i).value+'&i='+i+'&pf='+pf, false, '');
}
function inf_getMacsComplemento(x, f)
{
    var af = f || 0;
    var pf = af==1 ? '_ant' : (af==2 ? '_act' : '');
    var i= !isNaN( parseInt(x) ) ? x : ''; 
    var AJAX = new Ajax();
    AJAX.solicitud('getMacsComplemento', 'mac='+_('mac'+pf+i).value+'&i='+i+'&pf='+pf, false, '');
}
function fac_cmbActivo(ob, an)
{
    var o = _(ob);
    _Fch = true;
    _NE('mousedown', fac_cmbActivoRem);
    if(_('Fech_')==null){
        var d = document.createElement('div');
        d.id = 'Fech_';
        d.onmouseover = function(){_Fch = false;};
        d.onmouseout = function(){_Fch = true;};
        d.className = 'jm_selectorFecha';
        d.style.width = (an || '226') + 'px';
        if(_esIE){
            var r = _(this.r).size;
            var x = (r=='' || r==0) ? 160 : parseInt(r + (Math.E  * r * 2) / 0.79);
            document.body.appendChild(d);
            d.style.top=event.y + 10;
            d.style.left=event.x - x;
        }else{
            o.parentNode.appendChild(d);
        }
    }
}
function fac_cmbActivoRem()
{
    if(_Fch){
        _R('Fech_');
        _RE('mousedown', fac_cmbActivoRem);
    }
}
function fac_getActivosBodega(o, f, mv)
{
    /*var i=0;
    var idActs = '';
    while(_('idAc'+i)!=null){
        idActs += _('idAc'+i).value + ',';
        i++;
    }
    if(idActs!=''){
        idActs = idActs.substring(0, idActs.length-1);
    }*/
    var idB = _('id_bodega')!=null ? _('id_bodega').value : "1";
    var vec = _(o).value.split("\n");
    _AJAX.solicitud('getActivosBodega', 't='+vec[vec.length-1]+'&d=Fech_&o='+o+'&f='+f+'&idBod='+idB+'&mov='+mv);
}

function fac_setActivosBodega(obj, act)
{
    //var vec = _(obj).value.split("\n");
    
    _(obj).value=act.innerHTML;
}
function fac_getAntenas(i)
{
    fac_cmbActivo('antena_acoplada'+i);
    _AJAX.solicitud('getAntenas', 't='+_('antena_acoplada'+i).value+'&d=Fech_'+'&i='+i);
}
function fac_impCortes()
{
    imprimir("ActivacionesListas?idSuc="+_('idSuc').value);
}
function fac_impArchivoPlanes()
{
    imprimir("ColasListas?idSuc="+_('idSuc').value);
}
function fac_impArchivoPlanesPooll()
{
    imprimir("ColasListasPooll?idSuc="+_('idSuc').value);
}
function fac_activacionesListasPlanes()
{
    imprimir("ActivacionesListasPlanes?idSuc="+_('idSuc').value);
}
function fac_no_instalacion(k)
{
    if(_('motivo_no_instalacion').value==''){
        alert('Ingrese el motivo de la no instalación');
        return false;
    }
    window.open('pdfCertTerminacion?id='+k+'&Fch='+_('fecha_visita_instalacion').value
        +'&mot='+_('motivo_no_instalacion').value, '_blank' );
}
function set_deviceClave(k)
{
    _AJAX.solicitud('setDeviceClave', 'id='+k);
}
function fac_getIpsReutilizar()
{
    //console.log(_('id_sucursal').value);
    _AJAX.solicitud('getIpsReutilizar', 'idSuc='+_('id_sucursal').value);
}

/* SERVIDORES DE CORTES  */
function inf_servidoresCortes()
{
    encerar();
    if(_MENUS_._enArreglo("servidores_ftp_nuevo")>=0){
        btn.icono(-360, 2, 34, 34, 'Nuevo servidor', "inf_servidoresCorteEditar('-1');", 'd_herra');
    }
    if(_MENUS_._enArreglo("servidores_ftp_subir")>=0){
        btn.separador('d_herra');
        btn.icono(-209, -156, 34, 34, 'Cargar archivos a los servidores', "inf_setArchivoPlanes()", 'd_herra');
    }
    _AJAX.solicitud('frmFiltro', 'op=32&fn=inf_servidoresCortesBuscar()', false, 'd_filtro');
    setAnchoPanel(1, 680);
    setAnchoPanel(2, 410);
}
function inf_servidoresCortesBuscar()
{
    var w = "";
    if(_('idSuc').value!='-0'){
        w += "where id_sucursal="+_('idSuc').value;
    }
    _WR = encodeURI(w); 
    new Tabla('d_11', 'jmTbl', '', 'vta_servidor_ftp', 'SUCURSAL,WAN,LANs', 'id_servidor_ftp,sucursal,servidor,subredes', 
    '0,120,100,400', _altEdi, 'inf_servidoresCorteEditar(^);', _WR);
}
function inf_servidoresCorteEditar(k)
{
    _AJAX.solicitud('frmServidorCorte', 'id='+k, false, 'd_21');
}
function inf_servidoresCorteGuardar(f)
{
    if(f.servidor.value=='' || f.puerto.value=='' || f.subredes.value=='' || f.usuario.value=='' || f.clave.value==''){
        alert('Todos los campos marcados con un  *  son obligatorios');
        return false;
    }
    return _AJAX.enviarForm(f);
}
function inf_setArchivoPlanes()
{
    _AJAX.solicitud('setArchivoFtpServidorCorte', '', true, 'body');
}
/* ORDENES DE TRABAJO  */
function fac_trabajo()
{
    encerar();
    /*if(_MENUS_._enArreglo("trabajoNuevo")>=0){
        btn.icono(-360, 2, 34, 34, 'Nueva Orden de Trabajo', "fac_trabajoEditar('-1');", 'd_herra');
    }*/
    if(_MENUS_._enArreglo("trabajoImprimir")>=0){
        //btn.separador('d_herra');
        btn.icono(-455, -114, 34, 34, 'Imprimir', "fac_impTrabajo();", 'd_herra');
    }
    _AJAX.solicitud('frmFiltro', 'op=20&fn=fac_trabajoBuscar()', false, 'd_filtro');
    setAnchoPanel(1, 680);
    setAnchoPanel(2, 410);
}
function fac_trabajoNuevo(k)
{
    _('d_ayuda').innerHTML = msg();
    Ventana.crear('otr', 'ORDEN DE TRABAJO', "ancho=750,alto=500,modal=true,cerrar=true", '');
    new Tab('otr_html', 730, 380);
    _AJAX.solicitud('frmOrdenTrabajo', 'id='+k+'&altB=380', false, 'otr_html');
}
function fac_trabajoEditar(k, pg)
{
    var pg1 = pg || 'frmOrdenTrabajoEditar';
    _('d_ayuda').innerHTML = msg();
    Ventana.crear('otr', 'ORDEN DE TRABAJO', "ancho=750,alto=500,modal=true,cerrar=true", '');
    new Tab('otr_html', 730, 380);
    var AJAX = new Ajax();
    AJAX.solicitud(pg1, 'id='+k+'&altB=380', false, 'otr_html');
}
function fac_trabajoValidar(f)
{
    if(f.diagnostico_tecnico.value==''){
        alert('Todos los campos son obligatorios.');
        return false;
    }
    if(f.hora_cliente.value!=''){
        if(f.fecha_cliente.value==''){
            alert('Si ingresa la hora, debe ingresar la fecha también.');
            return false;
        }
        if(!_esHora('hora_cliente')){
            return false;
        }
    }
    if(f.fecha_cliente.value!=''){
        var F = new Date();
        var hoy = F.getFullYear()+'-'+(F.getMonth()+1)+'-'+F.getDate();
        var hora = F.getHours()+':'+F.getMinutes();
        if( SelectorFecha.getTimestamp(f.fecha_cliente.value, f.hora_cliente.value) < SelectorFecha.getTimestamp(hoy, hora) ){
            alert('La fecha sugerida por el cliente debe ser mayor a la fecha actual.');
            return false;
        }
    }
    return true;
}
function fac_trabajoGuardar(f)
{
    if(!fac_trabajoValidar(f)){
        return false;
    }
    return _AJAX.enviarForm(f);
}
function fac_trabajoTorreEditar(k)
{
    _('d_ayuda').innerHTML = msg();
    Ventana.crear('otr', 'ORDEN DE TRABAJO', "ancho=750,alto=500,modal=true,cerrar=true", '');
    new Tab('otr_html', 730, 380);
    var AJAX = new Ajax();
    AJAX.solicitud('frmOrdenTrabajoTorreEditar', 'id='+k+'&altB=380', false, 'otr_html');
}
function fac_addInstalacion(i)
{
    _AJAX.solicitud('getCroquis', 'id='+_('ax_idIns'+i).innerHTML, false, 'body');
    _R('bloq_vtFilt');_R('vtFilt');
}
function fac_setInstallCroquis()
{
    var vec = _('ax').innerHTML.split('|');
    _('tagImg').innerHTML = vec[1];
    var jS = eval('(' + vec[0] + ')');
    _('idIn').value = jS.tbl[0][0];
    _('ruc').innerHTML = jS.tbl[0][1];
    _('raSo').innerHTML = jS.tbl[0][2];
    _('sec').innerHTML = jS.tbl[0][3];
    _('ciu').innerHTML = jS.tbl[0][4];
    _('dir').innerHTML = jS.tbl[0][5];
    _('tel').innerHTML = jS.tbl[0][6];
    _('dirIns').innerHTML = jS.tbl[0][7];
    _('plan').innerHTML = jS.tbl[0][8];
    _('com').innerHTML = jS.tbl[0][9];
    _('ip').innerHTML = jS.tbl[0][10] + "&nbsp;&nbsp; IP Radio: " +jS.tbl[0][19];  
    _('rec').innerHTML = jS.tbl[0][11] + '<br />Clave: ' + jS.tbl[0][17] + ' &nbsp;&nbsp; ' + 
        "<label onclick=\"set_deviceClave("+jS.tbl[0][0]+")\">Registrada<input type='checkbox' id='set_deviceclave' name='set_deviceclave' "+(jS.tbl[0][18]=='t'?"checked":"")+" /></label><br />";
    _('anAc').innerHTML = jS.tbl[0][12];
    _('esSe').innerHTML = jS.tbl[0][13];
    if(_('movil_claro')!=null){
        _('movil_claro').value = jS.tbl[0][15];
        _('movil_movistar').value = jS.tbl[0][16];
    }
    if(_('quien_llama')!=null){
        _('quien_llama').value = jS.tbl[0][2];
        _('telefono_llama').value = jS.tbl[0][6];
    }
}
function fac_trabajoSolucionar(f)
{
    if(f.recomendacion.value==''){
        alert('Los campos marcados con  *  son obligatorios.');
        return false;
    }
    if(f.antena_acoplada0.value==f.antena_acoplada.value){
        if(!confirm('Por favor, verifique que la antena acoplada esta asignada correctamente')){
            return false;
        }
    }
    return _AJAX.enviarForm(f);
}
function fac_trabajoBuscar()
{
    var w="where id_sucursal='"+_('idSuc').value+"'";
    if(_('fi').value!='' && _('ff').value!=''){
        w+=" and fecha_reporte between '"+_('fi').value+"' and '"+_('ff').value+"'";
    }
    if(_('fi').value!='' && _('ff').value==''){
        w+=" and fecha_reporte >= '"+_('fi').value+"'";
    }
    if(_('tip').value!='-0'){
        w+=" and tipo_trabajo='"+_('tip').value+"'";
    }
    var a=_('t_b').value;
    if(a!=''){
        w += " and (num_orden::varchar like '"+a.toLowerCase()+"%' or lower(razon_social) like '%"+a.toLowerCase()+"%')";
    }else{
        if(_('est').value!='-0'){
            w+=" and estado='"+_('est').value+"'";
        }
    }
    _WR = encodeURI(w); 
    if(_('tip').value == 9){
        new Tabla('d_11', 'jmTbl', '', 'vta_orden_trabajo_infra', 'No.,RESPONSABLE,F. REPORTE,DIAGNOSTICO,ESTADO', 'id_orden_trabajo,numero_orden,responsable,toDateSQL(fecha_reporte),diagnostico_tecnico,txt_estado', '0,90,160,80,280,70', _altEdi, "fac_trabajoEditar(^, 'frmOrdenTrabajoTorreEditar');", _WR);
    }else{
        new Tabla('d_11', 'jmTbl', '', 'vta_orden_trabajo', 'No.,TIPO,CLIENTE,F. REPORTE,SECTOR,DIRECCION,DIAGNOSTICO,ESTADO', 'id_orden_trabajo,numero_orden,txt_tipo_trabajo,razon_social,toDateSQL(fecha_reporte),sector,direccion_instalacion,diagnostico_tecnico,txt_estado', '0,90,100,150,80,90,150,250,70', _altEdi, 'fac_trabajoEditar(^);', _WR);
    }

}
function fac_trabajoBuscarInst()
{
    var w="where id_sucursal='"+_('idSuc').value+"'";
    var a=_('t_b1').value;
    if(a!=''){
        w += " and estado_servicio in ('a', 'r')";
        w += " and (lower(ruc) like '"+a.toLowerCase()+"%' or ip::varchar like '"+a+"%' or lower(razon_social) like '%"+a.toLowerCase()+"%')";
        _WR = encodeURI(w);
        new Tabla('d_11', 'jmTbl', '', 'vta_instalacion', 'CI,CLIENTE,SECTOR,DIRECCION,IP', 'id_instalacion,ruc,razon_social,sector,direccion_instalacion,ip', '0,90,150,90,200,90', _altEdi, 'fac_trabajoNuevo(^);', _WR);
    }
}
function fac_impTrabajo()
{
    var h = "<input type='radio' id='z0' style='display:none' checked value='29' /><input type='radio' id='x' name='x' checked /> PDF &nbsp;&nbsp;&nbsp;&nbsp;&nbsp; <input type='radio' id='x2' name='x' /> Excel "+
            "<br/><br/><input type='button' value='Imprimir' onclick='imprimirReporte()'>";
    Ventana.crear('vta_im', 'Impresi&oacute;n de Ordenes de trabajo', "ancho=300,alto=120,modal=true,cerrar=true", h);
}
function fac_noSolucionar()
{
    var ots='';
    var rg = '';
    var i=0;
    while(_('ch_ots'+i)!=null){
        if(!_('ch_ots'+i).checked){
            ots += _('id_ots'+i).value + ',';
            rg += i + ',';
        }
        i++;
    }
    if(ots!=''){
        ots = ots.substr(0, ots.length-1);
        rg = rg.substr(0, rg.length-1);
        _AJAX.solicitud('frmOrdenTrabajoNoSolucionar', 'ids='+ots+'&regs='+rg, false, 'body');
    }
}
function inf_getTareas(k, idT)
{
    if(_(idT).innerHTML == ''){
        _AJAX.solicitud('getTareaInfraestructura', 'id='+k+'&idTa='+idT+'&idNodo='+_('id_nodo').value+'&idBod=', false, k);
    }else{
        _(idT).innerHTML = '';
    }
}
function inf_getEquiposBodega(m, i, ob, mv)
{
    var mov = mv || 0;
    _AJAX.solicitud('getEquiposBodega', 'mac='+m+'&d=Fech_&i='+i+'&idEm='+_('id_empleado').value+'&coEqs='+ob+'&mov='+mov);
}
function inf_setEquiposBodega(cod, i, prf, obj)
{
    var j=0;
    while(_('grMac'+i+j)!=null){
        j++;
    }
    _(obj).innerHTML += '<div class="grMac" id="grMac'+i+j+'"><input type="text" id="'+prf+i+j+'" name="'+prf+i+j+'" class="cajMac" value="'+cod+'" />'+
        '<div class="jm_columna"><div class="elim" onclick="_R(\'grMac'+i+j+'\')" alt="quitar" title="quitar">&nbsp;</div></div></div>';
}
function inf_trabajoInfraSolucionar(f)
{
    if(f.fecha_solucion.value==''){
        alert('Ingrese la fecha de solución.');
        return false;
    }
    var F = new Date();
    var hoy = F.getFullYear()+'-'+(F.getMonth()+1)+'-'+F.getDate();
    if(SelectorFecha.getTimestamp(f.fecha_solucion.value) > SelectorFecha.getTimestamp(hoy)){
        alert('La fecha de solución no puede ser mayor a la fecha actual.');
        return false;
    }
    var i=0;
    while(_('sol0'+i)!=null){
        if(!_('sol0'+i).checked && !_('sol1'+i).checked){
            alert('Debe registrar todas las soluciones.');
            return false;
        }
        i++;
    }
    
    return _AJAX.enviarForm(f);
}

/* hoja de ruta */
function fac_tarea()
{
    encerar();
    if(_MENUS_._enArreglo("hojaRutaNueva")>=0){
        btn.icono(-360, 2, 34, 34, 'Nueva Hoja de Ruta', "fac_trabajoTarea('-1');", 'd_herra');
    }
    _AJAX.solicitud('frmFiltro', 'op=29&fn=fac_trabajoTareaBuscar()', false, 'd_filtro');
    //setAnchoPanel(1, 680);
    setAnchoPanel(2, 20);
}
function fac_trabajoTarea(k)
{
    Ventana.crear('cmp', 'HOJA DE RUTA', "ancho="+_anchBody+",alto=500,modal=true,cerrar=true", '');
    new Tab('cmp_html');
    _AJAX.solicitud('frmHojaRuta', 'id='+k+'&est='+_('est').value+'&anch='+_anchBody, false, 'cmp_html');
}
function fac_setOrdenesRuta(op)
{
    var i=0;
    if(op==1){
        while(_('ch_ot'+i)!=null){
            if(_('ch_ot'+i).checked){
                ob = _('rot'+i).cloneNode(true);
                _R('rot'+i);
                _('tblAs').appendChild(ob);
                _('asg'+i).value=1;
                _('ch_ot'+i).checked=false;
            }
            i++;
        }
    }else{
        while(_('ch_ot'+i)!=null){
            if(_('ch_ot'+i).checked){
                ob = _('rot'+i).cloneNode(true);
                _R('rot'+i);
                _('tblPe').appendChild(ob);
                _('asg'+i).value=0;
                _('ch_ot'+i).checked=false;
            }
            i++;
        }
    }
}
function fac_setOrden(i)
{
    setDiv('dv', 450, 100, 'axtpo'+i);
    _AJAX.solicitud('getOrdenTrabajo', 'id='+_('id_ot'+i).value, false);
}
function fac_setMotivo(i)
{
    _('mot'+i).style.visibility = _('rev'+i).value==1 || _('rev'+i).value==3 ? 'visible' : 'hidden';
    if(_('rev'+i).value==1 || _('rev'+i).value==3){
        _('mot'+i).focus();
    }
}
function fac_trabajoTareaGuardar(f)
{
    if(f.id_tecnico_resp.value=='' || f.observacion.value==''){
        alert('Debe seleccionar un técnico e ingresar una observación para el técnico responsable.');
        return false;
    }
    var i=0;
    var ok=false;
    while(_('asg'+i)!=null){
        if(_('asg'+i).value==1){
            ok=true;
            break;
        }
        i++;
    }
    if(!ok){
        alert('Debe seleccionar por lo menos una orden de trabajo para poder guardar la hoja de ruta.');
        return false;
    }
    return _AJAX.enviarForm(f);
}
function fac_trabajoTareaBuscar()
{
    if(_('est').value==3){
        _('d_11').innerHTML = '';
        fac_trabajoTarea(-2);
    }else{
        var w="where estado_hoja='"+(_('est').value!='0' ? _('est').value : 1)+"'";
        if(_('idSuc').value!='-0'){
            w+=" and id_sucursal='"+_('idSuc').value+"'";
        }
        if(_('resp').value!='' && _('resp').value!='-0'){
            w+=" and id_tecnico_resp='"+_('resp').value+"'";
        }
        _WR = encodeURI(w); 
        new Tabla('d_11', 'jmTbl', '', 'vta_hoja_ruta', 'No. HOJA,FECHA,TECNICO,OBSERVACION', 'id_hoja_ruta,num_hoja_ruta,toDateSQL(fecha),tecnico,observacion', '0,80,90,150,200', _altEdi, 'fac_trabajoTarea(^);', _WR);
    }
}
function fac_trabajoTareaSolucionar(f)
{
    var i=0;
    while(_('id_ots'+i)!=null){
        if(_('tip_tra'+i).value==3){
            var m = _('mac_act'+i).value;
            var mp = _('mac_actp'+i).value;
            var mg = _('mac_actg'+i).value;
            if(m=='' || _('senal'+i).value=='' || _('antena_acoplada'+i).value=='-0'){
                alert('En instalaciones la MAC, el porcentaje de señal y la antena acoplada son obligatorios.');
                return false;
            }
            //alert(m + ' / ' + mp + ' / ' + mg)
            if( (m==mp && m!='' && mp!='') || (m==mg && m!='' && mg!='') || (mp==mg && mp!='' && mg!='') ){
                alert('Una de las MACs de una orden de trabajo de instalación esta repetida.');
                return false;
            }
        }
        if(_('tip_tra'+i).value==4){
            m = _('mac_ant'+i).value;
            mp = _('mac_antp'+i).value;
            mg = _('mac_antg'+i).value;
            /*if(m==''){
                alert('En des-instalaciones la MAC es obligatoria.');
                return false;
            }*/
            if( (m==mp && m!='' && mp!='') || (m==mg && m!='' && mg!='') || (mp==mg && mp!='' && mg!='') ){
                alert('Una de las MACs de una orden de trabajo de des-instalación esta repetida.');
                return false;
            }
        }
        i++;
    }
    return _AJAX.enviarForm(f);
}
function fac_filOrdener(k,i)
{
    _AJAX.solicitud('getOrdenesTrabajo', 'id='+k+'&tp_tr='+_('fil_tipo_trabajo').value+'&fecha='+_('fil_fecha_reporte').value+'&i='+i, false);
}
function inf_ordenTrabajoSolucionar(idOrTr, i)
{
    popup('Orden de Trabajo', 760, _altBody);
    _AJAX.solicitud('frmHR_OrdenTrabajo', 'idOrTr='+idOrTr+'&i='+i, false, 'ppp_html');
}
function fac_ordenTrabajoGuardar(f)
{
    if(f.solucionado0.checked){
        if(_('tip_tra').value==3){
                var m = _('mac_act0').value;
                if(m=='' || _('senal').value=='' || _('antena_acoplada').value=='-0' || _('fecha_instalacion')==''){
                    alert('En instalaciones la MAC NUEVA, el porcentaje de señal, la antena acoplada y la fecha de instalación son obligatorios.');
                    return false;
                }
                if(!_esFecha('fecha_instalacion')){
                    return false;
                }
            }
        /*if(_('tip_tra').value==4){
            m = _('mac_ant0').value;
            if(m==''){
                alert('En des-instalaciones la MAC ANTERIOR es obligatoria.');
                return false;
            }
        }*/
        for(i=0; i<49; i++){
            if( _('codAct0'+i)!=null ){
                if(_('codAct0'+i).value!=''){
                    for(j=i+1; j<50; j++){
                        if( _('codAct0'+j)!=null ){
                            if(_('codAct0'+j).value!=''){
                                if(_('codAct0'+i).value == _('codAct0'+j).value){
                                    alert('Una de las MACs nuevas está repetida.');
                                    return false;
                                }
                            }
                        }
                    }
                }
            }
        }
        for(i=0; i<49; i++){
            if( _('codActRet0'+i)!=null ){
                if(_('codActRet0'+i).value!=''){
                    for(j=i+1; j<50; j++){
                        if( _('codActRet0'+j)!=null ){
                            if(_('codActRet0'+i).value!='' && _('codActRet0'+j).value!=''){
                                if(_('codActRet0'+i).value == _('codActRet0'+j).value){
                                    alert('Una de las MACs retiradas está repetida.');
                                    return false;
                                }
                            }
                        }
                    }
                }
            }
        }

        if(f.fecha_instalacion!=null){
            var F = new Date();
            var hoy = F.getFullYear()+'-'+(F.getMonth()+1)+'-'+F.getDate();
            if(SelectorFecha.getTimestamp(f.fecha_instalacion.value) > SelectorFecha.getTimestamp(hoy)){
                alert('La fecha de instalación debe ser menor a la fecha de actual');
                return false;
            }
        }
    }
    return _AJAX.enviarForm(f);
    
    /*var mats='';
    var cats='';
    var j=0;
    while(_('id_material'+i+j)!=null){
        mats += '&id_material'+j+'=' + _('id_material'+i+j).value;
        cats += '&cantidad'+j+'=' + _('cantidad'+i+j).value;
        j++;
    }

    var p='i='+i+'&id_tecnico_resp1='+_('id_tecnico_resp1').value+'&numero_orden='+_('numero_orden'+i).value+'&id_ots='+_('id_ots'+i).value+'&id_ints='+_('id_ints'+i).value+
        '&tip_tra='+_('tip_tra'+i).value+'&ruc='+_('ruc'+i).value+'&raso='+_('raso'+i).value+'&solucionado='+(_('solucionado0'+i).checked ? 'true' : 'false')+'&conformidad='+_('conformidad'+i).value+'&'+
        'atencion='+_('atencion'+i).value+'&recomendacion='+_('recomendacion'+i).value+'&id_sector='+_('id_sector'+i).value+'&ip='+_('ip'+i).value+'&id_plan_actual='+_('id_plan_actual'+i).value+
        '&direccion='+_('direccion'+i).value+'&mac_ant='+_('mac_ant'+i).value+'&mac_antp='+_('mac_antp'+i).value+'&mac_antg='+_('mac_antg'+i).value+'&'+
        'receptor_ant='+_('receptor_ant'+i).value+'&mac_act='+_('mac_act'+i).value+'&mac_actp='+_('mac_actp'+i).value+'&mac_actg='+_('mac_actg'+i).value+'&receptor_act='+_('receptor_act'+i).value+
        '&senal='+_('senal'+i).value+'&antena_acoplada='+_('antena_acoplada'+i).value+'&lat_h='+_('lat_h'+i).value+'&lat_m='+_('lat_m'+i).value+'&'+
        'lat_s='+_('lat_s'+i).value+'&lat_o='+_('lat_o'+i).value+'&lon_h='+_('lon_h'+i).value+'&lon_m='+_('lon_m'+i).value+'&lon_s='+_('lon_s'+i).value+
        '&lon_o='+_('lon_o'+i).value+'&altura='+_('altura'+i).value+'&altura_antena='+_('altura_antena'+i).value+ins+mats+cats;
    _AJAX.solicitud('frmHojaRutaSolucionIndGuardar', p, true, 'body');
    */
}
function fac_addMaterial(i)
{
    
}
function inf_getActivo()
{
    var a = _('mac_ant0').value;
    if(a==''){
        alert('ingrese una MAC para poder verificar');
        return false;
    }
    
    var v = document.createElement("DIV");
    v.id = 'ppp1';
    v.className='jm_ventana';
    var tp = (_getTop(_('pie'))-500)/2;
    var iz = (document.body.clientWidth - 454)/2;
    v.style.top= (tp>0?tp:0) + 'px';
    v.style.left= (iz>0?iz:0) + 'px';
    v.style.width='454px';
    v.style.height='500px';
    v.style.zIndex = 101;
    document.body.appendChild(v);
    _AJAX.solicitud('frmActivoInfraestructura', 'mac='+a+'&obj=ppp1&idIns='+_('id_ints').value, false, 'ppp1');
}

/* SOPORTE DEL SERVICIO  */
function fac_soporte()
{
    encerar();
    if(_MENUS_._enArreglo("soporteImprimir")>=0){
        btn.icono(-455, -114, 34, 34, 'Imprimir', "fac_impSoporte();", 'd_herra');
    }
    _AJAX.solicitud('frmFiltro', 'op=21&fn=fac_tblSoporte()', false, 'd_filtro');
    var h = '<table cellpadding="0" cellspacing="0"><tr><TH class="jm_TH" width="90">CEDULA</TH><TH class="jm_TH" width="160">CLIENTE</TH><TH class="jm_TH" width="90">SECTOR</TH><TH class="jm_TH" width="130">TELEFONO</TH><TH class="jm_TH" width="115">URL</TH><TH class="jm_TH" width="115">URL RADIO</TH><TH class="jm_TH" width="85">PING</TH></tr></table>'+
        '<DIV id="tblFil" style="overflow:auto;width:830px;height:'+_altEdi+'px;"></div>';
    _('d_11').innerHTML = h;
    setAnchoPanel(1, 845);
    setAnchoPanel(2, 410);
}
function fac_tblSoporte()
{
    var s = _('idSuc').value;
    var t=_('t_b').value.toLowerCase();
    //if(t!='' && _('est').value=='-0'){ 
        _AJAX.solicitud('getTblSoporte', 's='+s+'&t='+t+'&e='+_('est').value, false, 'tblFil');
    //}
}
function fac_setTblSoporte()
{
    _('d_21').innerHTML='';
    if(_('ax').innerHTML!='{tbl:]}'){
        var jS = eval('(' + _('ax').innerHTML + ')');
        var j=0;
        var h = "<TABLE class='jm_tabla' cellspacing='1' cellpadding='2'>";
        for(var i=0; i<jS.tbl.length; i++){
            h += "<tr id='flIl"+j+"' class='jm_filaPar' style='cursor:pointer;'><td width='88' onclick=\"fac_soporteBuscar("+jS.tbl[i][0]+", "+j+")\"><span id='ax_idIns"+j+"' style='display:none'>"+jS.tbl[i][0]+"</span>"+jS.tbl[i][1]+"</td><td width='158' onclick=\"fac_soporteBuscar("+jS.tbl[i][0]+", "+j+")\">"+jS.tbl[i][2]+"</td><td width='88' onclick=\"fac_soporteBuscar("+jS.tbl[i][0]+", "+j+")\">"+jS.tbl[i][3]+
                "</td><td width='128' onclick=\"fac_soporteBuscar("+jS.tbl[i][0]+", "+j+")\">"+jS.tbl[i][4]+"</td><td width='113'><a href='http://"+jS.tbl[i][5]+"' target='_blank'>http://"+jS.tbl[i][5]+"</a></td><td width='113'><a href='http://"+jS.tbl[i][6]+"' target='_blank'>http://"+jS.tbl[i][6]+"</a></td><td width='83'><a href='javascript:void(0)' onclick=\"ping_ip('"+jS.tbl[i][5]+"')\">"+jS.tbl[i][5]+"</a></td></tr>";
            j++;
        }
        h += "</table>";
        _('tblFil').innerHTML = h;
    }
}
function ping_ip(ip)
{
    Ventana.crear('cmp', 'PING '+ip, "ancho=400,alto=240,modal=true,cerrar=true", '');
    _AJAX.solicitud('Ping', 'ip='+ip+'&obj=cmp_html', false, 'cmp_html');
}
function fac_soporteEditar(k)
{
    _('d_ayuda').innerHTML = msg();
    Ventana.crear('cmp', 'SOPORTE', "ancho=750,alto=500,modal=true,cerrar=true", '');
    new Tab('cmp_html', 730, 380);
    _AJAX.solicitud('frmSoporte', 'id='+k+'&altB=380&fun=fac_addInstalacion('+fA+');', false, 'cmp_html');
}
function fac_soporteGuardar(f)
{
    if(f.quien_llama.value=='' || f.telefono_llama.value=='' || f.problema.value=='' || f.idIn.value==''){
        alert('Todos los campos con * son obligatorios.');
        return false;
    }
    if(f.diagnostico_ant.value==f.diagnostico.value){
        alert('Por favor, ingrese un comentario para el diagnóstico técnico');
        return false;
    }
    return _AJAX.enviarForm(f);
}
function fac_soporteSolucionar(f)
{
    if(f.recomendacion.value==''){
        alert('Los campos marcados con  *  son obligatorios.');
        return false;
    }
    if(f.antena_acoplada0.value==f.antena_acoplada.value){
        if(!confirm("Por favor, verifique que la antena acoplada esta asignada correctamente.\nSi la asignación es correcta pulse aceptar")){
            return false;
        }
    }
    if(f.odTra.checked){
        if(!fac_trabajoValidar(f)){
            return false;
        }
    }
    return _AJAX.enviarForm(f);
}
var fA=-1;
function fac_soporteBuscar(k, i)
{
    _('d_21').innerHTML='';
    if(_MENUS_._enArreglo("soporteNuevo")>=0 && fA==-1 && _('tblFil').innerHTML!=''){
        btn.separador('d_herra');
        btn.icono(-360, 2, 34, 34, 'Nuevo Soporte de Servicio', "fac_soporteEditar('-1');", 'd_herra');
    }
    var p = i!=undefined ? i : fA;
    if(p!=-1){
        _('flIl'+p).className='jm_filaSel';
        if(fA!=-1 && _('flIl'+fA)!=null){
            _('flIl'+fA).className='jm_filaPar';
        }
        fA=p;
    }
    var c = k || _('idIll').value;
    _('idIll').value = c;
    var w="where fecha_llamada between '"+_('fi').value+"' and '"+_('ff').value+"' and id_instalacion="+c;
    if(_('est').value!='-0'){
        w+=" and estado='"+_('est').value+"'";
    }
    /*if(_('idSec').value!='-0'){
        w += " and id_sector="+_('idSec').value;
    }
    if(_('idAnt').value!='-0'){
        w += " and antena_acoplada='"+_('idAnt').value+"'";
    }
    if(_('idPlSe').value!='-0'){
        w += " and id_plan_actual="+_('idPlSe').value;
    }*/
    _WR = encodeURI(w + " order by estado,fecha_llamada desc,hora_llamada desc"); 
    new Tabla('d_21', 'jmTbl', '', 'vta_soporte', 'FECHA LLAMADA,FECHA SOLUCION,ESTADO', 'id_soporte,txt_fecha_llamada,txt_fecha_solucion,txt_estado', '0,130,130,90', _altEdi, 'fac_soporteEditar(^);', _WR);
}
function fac_impSoporte()
{
    var h = "<input type='radio' id='z0' name='z' checked='checked' value='35' /> <span onclick=\"_('z0').checked='true'\">Listado de soportes del servicio de Internet </span>";
    if(fA!=-1){
        h += "<br><input type='radio' id='z1' name='z' value='30' /> <span onclick=\"_('z1').checked='true'\">Listado de soportes del servicio de Internet por Instalaci&oacute;n </span>";
    }
    h += "<hr><input type='radio' id='x' name='x' checked /> PDF &nbsp;&nbsp;&nbsp;&nbsp;&nbsp; <input type='radio' id='x2' name='x' /> Excel";
    h += "<br/><br/><input type='button' value='Imprimir' onclick='fac_impSoporte1()'>";
    Ventana.crear('vta_im', 'Impresi&oacute;n de Soportes', "ancho=330,alto=160,modal=true,cerrar=true", h);
}
function fac_impSoporte1()
{
    var i=0;
    while(_('z'+i)!=null){
        if(_('z'+i).checked){
            break;
        }
        i++;
    }
    var p = 'op='+_('z'+i).value+'&fi='+ _('fi').value+'&ff='+_('ff').value+'&idSuc='+_('idSuc').value+'&est='+_('est').value+'&idSec=&idAnt=&idPlSe=&WH='+_WR;
    var a = (_('x').checked) ? 'impReportePdf?' : 'impReporteXls?';
    imprimir(a+p);
    _R('bloq_vta_im');
    _R('vta_im');
}    
function fac_genOrdenTrabajo()
{
    if(_('odTra').checked){
        _('axTOT').style.display='block';
        _('recomendacion').value='Se emitirá una orden de trabajo.';
    }else{
        _('axTOT').style.display='none';
        _('recomendacion').value='';
    }
}
/* DOCUMENTOS DE BANCO */
function fac_documentos()
{
    encerar();
    _('d_filtro').innerHTML = '<div>'+setFechas()+'</div>N&uacute;mero de documento: <input type="text" id="t_b" onkeypress="var c=(_esN)?event.which:event.keyCode;if(c==13){fac_docsBuscar();}">'+
        '&nbsp;<input type="button" value="Filtrar" onclick="fac_docsBuscar();">';
    fac_docsBuscar();
    setAnchoPanel(2, 20);
}
function fac_getSaldoDoc(v)
{
    if(v!=''){
        var c = _('num_cuenta')!=null ? _('num_cuenta').value : '';
        _AJAX.solicitud('getDocumentoBanco', 'doc='+v+'&foPa='+_('foPa').value+'&cta='+c, false, 'body');
    }
}
function fac_postGetSaldoDoc(s, m)
{
    if(s==-1){
        /*var h = '<div>Por favor ingrese el monto total del valor del comprobante</div><br />'+
            '<div style="text-align:center"><input id="val_com" type="text" size="10" onkeypress="_evaluar(event, \'0123456789.\')" onkeyup="var c=(_esN)?event.which:event.keyCode;if(c==13){_(\'saldo_doc\').value=_(\'val_com\').value;_R(\'sdo\');_R(\'bloq_sdo\');}"/>'+
            '<input type="button" value="ingresar" onclick="_(\'saldo_doc\').value=_(\'val_com\').value;_R(\'sdo\');_R(\'bloq_sdo\');" /></div>';
        Ventana.crear('sdo', 'SOPORTE', "ancho=300,alto=100,modal=true,cerrar=true", h);*/
        alert('Insuficiencia de fondos, monto del documento ' + m);
    }else{
        _('saldo_doc').value=s;
    }
}
function fac_docsBuscar()
{
    var w = "where fecha between '"+_('fi').value+"' and '"+_('ff').value+"'";
    if(_('t_b').value!=''){
        w += " and num_documento like '"+_('t_b').value+"%'";
    }
    _WR = encodeURI(w);
    new Tabla('d_11', 'jmTbl', '', 'tbl_documento_banco', 'Nro. Doc.,FECHA,MONTO,SALDO', 'id_documento_banco,num_documento,toDateSQL(fecha),monto,saldo', '0,100,70,70,80', _altEdi, '', _WR);
}
/* Cash Management Pichincha */
function fac_pichinchaCash()
{
    encerar();
    if(_MENUS_._enArreglo("cash_pichincha_bajar")>=0){
        btn.icono(-175, -156, 34, 34, 'Descargar archivo para cobros a traves del cash management', "fac_frmComisionPichinchaCash();", 'd_herra');
    }
    if(_MENUS_._enArreglo("cash_pichincha_subir")>=0){
        btn.icono(-209, -156, 34, 34, 'Cargar archivo de cobros a traves del cash management', "fac_setCashManagementPichincha()", 'd_herra');
    }
    _AJAX.solicitud('frmFiltro', 'op=32&fn=fac_PichinchaCashBuscar();', false, 'd_filtro');
    setAnchoPanel(2, 580);
}
function fac_frmComisionPichinchaCash()
{
    var h = '<p>Comisi&oacute;n: <input type="text" id="com" size="6" maxlength="6" value="0.45" onkeypress="_evaluar(event, \'0123456789.\')" />'+
        '<input type="button" value="descargar archivo" onclick="fac_genComisionPichinchaCash()" /></p>';
    Ventana.crear('cmp', 'CASH MANAGEMENT PICHINCHA', "ancho=290,alto=100,modal=true,cerrar=true", h);
}
function fac_genComisionPichinchaCash()
{
    imprimir('PichinchaCash?comision='+_('com').value);
    _R('bloq_cmp');
    _R('cmp');
}
function fac_PichinchaCashBuscar()
{
    var w = "where banco='PICHINCHA' and id_sucursal="+_('idSuc').value;
    _WR = encodeURI(w); 
    new Tabla('d_11', 'jmTbl', '', 'vta_documento_banco_cash', 'RUC,CLIENTE,PERIODO,FECHA,HORA,TOTAL', 'id_prefactura,ruc,razon_social,txt_periodo,fecha_pago,hora_pago,total', '0,70,200,80,60,60,60', _altEdi, 'fac_preFacturaEditar(^, 1)', _WR);
}
function fac_setCashManagementPichincha()
{
    Ventana.crear('vta_subir', 'Transferencia de Archivos', "ancho=440,alto=110,modal=true,cerrar=true", '');
    _AJAX.solicitud('SubirArchivo', 'pr=ProcesaArchivoCashManagementPichincha', true, 'vta_subir_html');
}

/* Cash Management produbanco */
function fac_produbancoCash()
{
    encerar();
    if(_MENUS_._enArreglo("cash_produbanco_bajar")>=0){
        btn.icono(-175, -156, 34, 34, 'Descargar archivo para cobros a traves del cash management', "fac_frmComisionProdubancoCash();", 'd_herra');
    }
    if(_MENUS_._enArreglo("cash_produbanco_subir")>=0){
        btn.icono(-209, -156, 34, 34, 'Cargar archivo de cobros a traves del cash management', "fac_setCashManagementProdubanco()", 'd_herra');
    }
    _AJAX.solicitud('frmFiltro', 'op=32&fn=fac_ProdubancoCashBuscar();', false, 'd_filtro');
    setAnchoPanel(2, 580);
}
function fac_frmComisionProdubancoCash()
{
    var h = '<p>Comisi&oacute;n: <input type="text" id="com" size="6" maxlength="6" value="0.45" onkeypress="_evaluar(event, \'0123456789.\')" />'+
        '<input type="button" value="descargar archivo" onclick="fac_genComisionProdubancoCash()" /></p>';
    Ventana.crear('cmp', 'CASH MANAGEMENT PICHINCHA', "ancho=290,alto=100,modal=true,cerrar=true", h);
}
function fac_genComisionProdubancoCash()
{
    imprimir('ProdubancoCash?comision='+_('com').value);
    _R('bloq_cmp');
    _R('cmp');
}
function fac_ProdubancoCashBuscar()
{
    var w = "where banco='PRODUBANCO' and id_sucursal="+_('idSuc').value;
    _WR = encodeURI(w); 
    new Tabla('d_11', 'jmTbl', '', 'vta_documento_banco_cash', 'RUC,CLIENTE,PERIODO,FECHA,HORA,TOTAL', 'id_prefactura,ruc,razon_social,txt_periodo,fecha_pago,hora_pago,total', '0,70,200,80,60,60,60', _altEdi, 'fac_preFacturaEditar(^, 1)', _WR);
}
function fac_setCashManagementProdubanco()
{
    Ventana.crear('vta_subir', 'Transferencia de Archivos', "ancho=440,alto=110,modal=true,cerrar=true", '');
    _AJAX.solicitud('SubirArchivo', 'pr=ProcesaArchivoCashManagementProdubanco', true, 'vta_subir_html');
}


/* CONVENIO DE DEBITOS DE BANCO */
function fac_debitos()
{
    encerar();
    if(_MENUS_._enArreglo("docBancoDebitosBajar")>=0){
        btn.icono(-175, -156, 34, 34, 'Descargar archivo de d&eacute;bitos de clientes para el banco', "fac_getArchivoDebitosBanco();", 'd_herra');
    }
    if(_MENUS_._enArreglo("docBancoDebitosSubir")>=0){
        btn.separador('d_herra');
        btn.icono(-209, -156, 34, 34, 'Cargar archivo de d&eacute;bitos del banco', "fac_setArchivoDebitosBanco()", 'd_herra');
    }
    if(_MENUS_._enArreglo("deb_tarj_pichincha_subir")>=0 || _MENUS_._enArreglo("deb_tarj_pichincha_bajar")>=0){
        btn.separador('d_herra');
        btn.icono(-109, -226, 34, 34, 'Ftp', "fac_ftp();", 'd_herra');
    }
    _('d_filtro').innerHTML = '<div>D&eacute;bitos: ' + setCombo('debitos', '{tbl:[{0:"p",1:"Cuentas Pichincha"},{0:"t",1:"Tarjetas de cr&eacute;dito"}]}', 'fac_setGrDebitos();fac_debitosBuscar();') + '</div>' +
        '<div id="axGrDeb"><div>'+setFechas('', 'h')+'</div>N&uacute;mero de documento, concepto: <input type="text" id="t_b" onkeypress="var c=(_esN)?event.which:event.keyCode;if(c==13){fac_debitosBuscar();}" /></div>'+
        '&nbsp;<input type="button" value="Filtrar" onclick="fac_debitosBuscar();">';
    var F = new Date();
    _('fi').value = '01/' + (F.getMonth()+1) + '/' + F.getFullYear();
    setAnchoPanel(2, 20);
    fac_debitosBuscar();
}fac_produbancoCash
function fac_setGrDebitos()
{
    if(_('debitos').value=='p'){
        _('axGrDeb').style.display = 'block';
        setAnchoPanel(2, 20);
    }else{
        _('axGrDeb').style.display = 'none';
        setAnchoPanel(2, 580);
    }
}
function fac_getArchivoDebitosBanco()
{
    var F = new Date();
    var m = new Array('enero','febrero','marzo','abril','mayo','junio','julio','agosto','septiembre','octubre','noviembre','diciembre');
    var h = '<div>A&ntilde;o: <input type="text" id="anio" size="6" maxlength="6" value="'+F.getFullYear()+'" onkeypress="_numero(event)" /></div>'+
        '<div>Mes: <select id="mes">';
        var ms = F.getMonth() + 1;
        for(var i=1; i<=12; i++){
            var se = (i==ms) ? 'selected' : '';
            h += '<option '+se+' value="'+(i)+'">'+m[i-1]+'</option>';
        }
        h += '</select></div>';
    h += '<div>Comisi&oacute;n: <input type="text" id="com" size="6" maxlength="6" value="0.60" onkeypress="_evaluar(event, \'0123456789.\')" /></div>'+
        '<div><input type="button" value="descargar archivo" onclick="fac_genArchivoDebitosBanco()" /></div>';
    Ventana.crear('cmp', 'DEBITOS', "ancho=250,alto=150,modal=true,cerrar=true", h);
}
function fac_genArchivoDebitosBanco()
{
    imprimir('Debitos?anio='+_('anio').value+'&mes='+_('mes').value+'&comision='+_('com').value);
    _R('bloq_cmp');
    _R('cmp');
}
function fac_setArchivoDebitosBanco()
{
    Ventana.crear('vta_subir', 'Transferencia de Archivos', "ancho=440,alto=110,modal=true,cerrar=true", '');
    _AJAX.solicitud('SubirArchivo', 'pr=ProcesaArchivoDevsBanco', true, 'vta_subir_html')
}
function fac_debitosBuscar()
{
    var w = "where fecha between '"+_('fi').value+"' and '"+_('ff').value+"'";
    var t = _('t_b').value.toLowerCase();
    if(t!=''){
        w += " and lower(referencia) like '%"+t+"%' or lower(cuenta) like '"+t+"%'";
    }
    _WR = encodeURI(w);
    var d = _('debitos').value; 
    if(d=='t'){
        new Tabla('d_11', 'jmTbl', '', 'vta_documento_banco_tarjeta_credito', 'RUC,CLIENTE,NUM. TARJETA,FECHA DEBITO,FECHA,TOTAL,TOTAL TARJETA', 'id_prefactura,ruc,razon_social,num_tarjeta,txt_periodo,fecha_pago,total,total_tarjeta', '0,90,200,160,80,70,60,60', _altEdi, 'fac_preFacturaEditar(^, 2)', '');
    }
    if(d=='p'){
        new Tabla('d_11', 'jmTbl', '', 'tbl_documento_banco_debito', 'FECHA,No. DOC.,FORMA PAGO,TIPO,CUENTA,REFERENCIA,VALOR,ESTADO', 'id_documento_banco_debito,toDateSQL(fecha),item,forma_pago,tipo_cuenta,cuenta,referencia,valor,estado', '0,70,90,60,50,80,200,70,150', _altEdi, '', _WR);
    }
}
/* debitos de tarjetas de credito Pichincha */
function fac_ftp()
{
    Ventana.crear('cmp', 'FTP PICHINCHA', "ancho=710,alto=330,modal=true,cerrar=true", '');
    var F = new Date();
    var m = new Array('enero','febrero','marzo','abril','mayo','junio','julio','agosto','septiembre','octubre','noviembre','diciembre');
    var h = '<p><div>A&ntilde;o: <input type="text" id="anio" size="6" maxlength="6" value="'+F.getFullYear()+'" onkeypress="_numero(event)" /> &nbsp;&nbsp; '+
        'Mes: <select id="mes"> ';
    var ms = F.getMonth() + 1;
        for(var i=1; i<=12; i++){
            var se = (i==ms) ? 'selected' : '';
            h += '<option '+se+' value="'+(i)+'">'+m[i-1]+'</option>';
        }
    h += '</select> &nbsp;&nbsp; '+ 
        'Comisi&oacute;n: <input type="text" id="com" size="6" maxlength="6" value="0" onkeypress="_evaluar(event, \'0123456789.\')" /> &nbsp;&nbsp; ' +
        '<a class="botonMenu" href="javascript:void(0)" onclick="fac_genDebitosTarjetas()">Generar archivos</a></div></p>'+
        '<table width="98%" height="80%">'+
        '<tr align="center" height="20"><td><h4>Archivos locales</h4></td><td>&nbsp;</td><td><h4>Archivos FTP Pichincha</h4></td></tr>'+
        '<tr valign="top"><td><div id="axFtpLoc" style="border:1px dotted #666666;overflow:auto;width:300px;height:190px"></div></td>'+
        '<td width="80" align="center"><br /><a href="javascript:void(0)" class="botonMenu" onclick="fac_subirFicheros()">Cargar</a> <p>&nbsp;</p> <a href="javascript:void(0)" class="botonMenu" onclick="fac_vajarFicheros()">Descargar</a></td>'+
        '<td><div id="axFtpRem" style="border:1px dotted #666666;overflow:auto;width:300px;height:190px"></div></td></tr>'+
        '</table>';
    _('cmp_html').innerHTML = h;
    fac_ftpLocalVer();
    fac_ftpRemotoVer();
}
function fac_genDebitosTarjetas()
{
    _AJAX.solicitud('GenerarDebitosTarjetas', 'anio='+_('anio').value+'&mes='+_('mes').value+'&com='+_('com').value, false, 'body');
}
function fac_ftpLocalVer()
{
    AJAX = new Ajax();
    AJAX.solicitud('verFtpLocal', '', false, 'axFtpLoc');
}
function fac_ftpRemotoVer()
{
    AJAX = new Ajax();
    AJAX.solicitud('verFtpRemoto', '', false, 'axFtpRem');
}
function fac_genDebitosTarjetas()
{
    _AJAX.solicitud('GenerarDebitosTarjetas', 'anio='+_('anio').value+'&mes='+_('mes').value+'&com='+_('com').value, false, 'body');
}
function fac_subirFicheros()
{
    var i=0;
    var fichs = '';
    while(_('chArchLoc'+i) != null){
        if(_('chArchLoc'+i).checked){
            fichs += _('archLoc'+i).value + ',';
        }
        i++;
    }
    if(fichs != ''){
        _AJAX.solicitud('subirFicherosFtp', 'fichs='+(fichs.substr(0, fichs.length-1)), false, 'axFtpRem');
    }
}
function fac_vajarFicheros()
{
    var i=0;
    var fichs = '';
    while(_('chArchRem'+i) != null){
        if(_('chArchRem'+i).checked){
            fichs += _('archRem'+i).value + ',';
        }
        i++;
    }
    if(fichs != ''){ 
        _AJAX.solicitud('vajarFicherosFtp', 'fichs='+(fichs.substr(0, fichs.length-1)), false, 'axFtpLoc');
    }
}
function fac_remFicheroLocal(k)
{
    _AJAX.solicitud('remFicheroLocal', 'k='+k, false, 'axFtpLoc');
}
function fac_remFicheroRemoto(k)
{
    _AJAX.solicitud('remFicheroRemoto', 'k='+k, false, 'axFtpRem');
}

/* MOVIMIENTOS DE BANCO */
function fac_movimientos()
{
    encerar();
    if(_MENUS_._enArreglo("docBancoMovNuevo")>=0){
        btn.icono(-360, 2, 34, 34, 'Nuevo documento de movimiento', "fac_movEditar(-1)", 'd_herra');
    }
    if(_MENUS_._enArreglo("docBancoSubir")>=0){
        btn.icono(-209, -156, 34, 34, 'Cargar archivo de movimientos del banco', "fac_setArchivoMovsBanco()", 'd_herra');
    }
    _('d_filtro').innerHTML = 'N&uacute;mero de documento, concepto: <input type="text" id="t_b" onkeypress="var c=(_esN)?event.which:event.keyCode;if(c==13){fac_movsBuscar();}">'+
        '&nbsp;<input type="button" value="Filtrar" onclick="fac_movsBuscar();">';
    fac_movsBuscar();
    setAnchoPanel(2, 320);
}
function fac_movEditar(k)
{
    _('d_ayuda').innerHTML = msg();
    _AJAX.solicitud('frmMovimiento', 'id='+k, false, 'd_21');
}
function fac_movGuardar(f)
{
    if(f.fecha.value=='' || f.concepto.value=='' || f.documento.value=='' || f.monto.value==''){
        _MS('Los campos marcados con un  *  son obligatorios.');
        return false;
    }
    return _AJAX.enviarForm(f);
}
function fac_setArchivoMovsBanco()
{
    Ventana.crear('vta_subir', 'Transferencia de Archivos', "ancho=440,alto=110,modal=true,cerrar=true", '');
    _AJAX.solicitud('SubirArchivo', 'pr=ProcesaArchivoMovsBanco', true, 'vta_subir_html');
}
function fac_movsBuscar()
{
    var w = "";
    var t = _('t_b').value.toLowerCase();
    if(t!=''){
        w += "where lower(concepto) like '%"+t+"%' or lower(documento) like '"+t+"%'";
    }
    _WR = encodeURI(w);
    new Tabla('d_11', 'jmTbl', '', 'tbl_documento_banco_tmp', 'FECHA,CONCEPTO,DOCUMENTO,MONTO', 'id_documento_banco_tmp,toDateSQL(fecha),concepto,documento,monto', '0,70,200,80,80', _altEdi, 'fac_movEditar(^)', _WR);
}
/* PRE-FACTURACION */
function fac_preFacturas()
{
    encerar();
    if(_MENUS_._enArreglo("prefacturaNueva")>=0){
        btn.icono(-360, 2, 34, 34, 'Nueva Pre-factura', "fac_preFacturaNueva();", 'd_herra');
    }
    if(_MENUS_._enArreglo("prefacturaEliminar")>=0){
        btn.icono(-391, 3, 34, 34, 'Eliminar', "_objT.eliminar('La/las pre-facturas serán eliminadas de forma permanentemente.', 'Eliminar');", 'd_herra');
    }
    if(_MENUS_._enArreglo("prefacturaImprimir")>=0){
        btn.icono(-455, -114, 34, 34, 'Imprimir', "fac_impPreFacturas()", 'd_herra');
    }
    if(_MENUS_._enArreglo("prefacturaAnticipo")>=0){
        btn.separador('d_herra');
        btn.icono(-245, -157, 34, 34, 'Anticipos', "fac_preFactAnticipo()", 'd_herra');
    }
    if(_MENUS_._enArreglo("installDescargar")>=0){
        btn.separador('d_herra');
        btn.icono(-175, -156, 34, 34, 'Descargar archivo de suspensiones para el servidor', "imprimir('Suspensiones');", 'd_herra');
        btn.icono(-175, -156, 34, 34, 'Descargar archivo de suspensiones para el servidor por listas de cortes', "imprimir('SuspensionesListas');", 'd_herra');
        btn.icono(-175, -156, 34, 34, 'Descargar archivo de activaciones para el servidor por listas de activos', "fac_impCortes()", 'd_herra');
    }
    _AJAX.solicitud('frmFiltro', 'op=18&fn=fac_preFacturaBuscar()', false, 'd_filtro');
    setAnchoPanel(2, 580);
}
function fac_preFacturaNueva()
{
    Ventana.crear('cmp', 'NUEVA PRE-FACTURA', "ancho=710,alto=330,modal=true,cerrar=true", '');
    _AJAX.solicitud('frmPreFacturaNueva', '', false, 'cmp_html');
}
function fac_getInstalls()
{
    _AJAX.solicitud('getInstalls', 'a='+_('anio').value+'&m='+_('mes').value, false, 'axIns');
}
function fac_PreFacturaNuevaGuardar(f)
{
    if(f.id_instalacion.value==''){
        alert('Todos los campos son obligatorios.');
        return false;
    }
    var k='';
    for(var i=0; i<f.id_instalacion.options.length; i++){
        if(f.id_instalacion.options[i].selected){
            k += f.id_instalacion.options[i].value + ',';
        }
    }
    _('id_instalaciones').value = k.substr(0, k.length-1);
    return _AJAX.enviarForm(f);
}
function fac_preFacturaEditar(k, m)
{
    _('d_ayuda').innerHTML = msg();
    new Tab('d_21');
    _AJAX.solicitud('frmPreFactura', 'id='+k+'&altB='+_altBody+'&mod='+m, false, 'd_21');
}
function fac_preFacturaBuscar()
{
    var w = 'where id_sucursal='+_('idSuc').value;
    w += _('idSec').value!='-0' ? " and id_sector="+_('idSec').value : "";
    var b = _('t_b').value;
    if(b!=''){
        w += " and (ip::varchar like '"+b+"%' or lower(ruc) like '"+b.toLowerCase()+"%' or lower(razon_social) like '%"+b.toLowerCase()+"%')";
    }
    _WR = encodeURI(w) + " order by razon_social, id_prefactura"; 
    new Tabla('d_11', 'jmTbl', '', 'vta_prefactura', 'RUC,CLIENTE,IP,PERIODO', 'id_prefactura,ruc,razon_social,ip,txt_periodo', '0,90,200,100,90', _altEdi, 'fac_preFacturaEditar(^, 0);', _WR);
}
function fac_setTablaMaterial(r, cod, pf, an)
{
    an = an || 690;
    _R('sonido');
    if(r != '{tbl:]}'){
        var t = 0;
        _('ax').innerHTML=r;
        setDiv('dv', an, 130, pf+'InAr');
        _T('dv', 90);
        var jS = eval('(' + r + ')');
        var h = '<table cellpadding="4" width="100%" cellspacing="0" border="0">';
        h += '<tr><th>CODIGO</th><th>DESCRIPCION</th><th>STOCK</th><th>P.V.</th><th>% DES.</th><th>TOTAL</th></tr>';
        for(var i=0; i<jS.tbl.length; i++){
            t = _RD(parseFloat(jS.tbl[i][6]) + (parseFloat(jS.tbl[i][6]) * parseFloat(jS.tbl[i][5]) / 100) );
            h += '<tr valign="top" onmouseover="this.className=\'jm_filaSobre\';this.style.cursor=\'pointer\'" onmouseout="this.className=\'\'" onclick="fac_addRubro(\''+pf+'\', '+i+')"><td id="axCodP'+i+'">'+jS.tbl[i][1]+'</td><td>'+jS.tbl[i][2]+' '+jS.tbl[i][5]+'</td><td>'+jS.tbl[i][3]+'</td><td>'+jS.tbl[i][6]+'</td><td>'+jS.tbl[i][7]+'</td><td>'+t+'</td></tr>';
        }
        h += '<table>';
        _('dv').innerHTML = h;
        if(cod == 13){
            fac_addRubro('', 0);
        }
    }else{
        //alerta();
        _R('dv');
        _(pf+'busProd').value='';
    }
}
function fac_addRubro(pf, x)
{
    if(_('ax').innerHTML!='' && _('ax').innerHTML!='{tbl:]}'){
        var jS = eval('(' + _('ax').innerHTML + ')');
        if(parseFloat(jS.tbl[x][3]) > 0){
            var p = pf || '';
            var ps = enFormulario(pf, _('axCodP'+x).innerHTML);
            if(ps!=-1){
                _(p+"c"+ps).value = parseFloat(_(p+"c"+ps).value) + 1;
            }else{
                var i = _(p+'tblD').childNodes.length>0 ? parseInt(_(p+'tblD').childNodes.item(_(p+'tblD').childNodes.length-1).id.replace(p+'r', ''))+1 : 0;
                _AJAX.solicitud('getProductoPrecios', 'idPr='+jS.tbl[x][0]+'&pf='+p+'&i='+i, false, 'body');
                var tr = document.createElement("div");
                tr.id=p+'r'+i;
                tr.className='jm_fila';
                _(p+'tblD').appendChild(tr);
                var h = "<div class='jm_columna caja' style='width:100px;height:38px;' id='"+p+"cF"+i+"0'>"+jS.tbl[x][1]+"</div>" +
                        "<div class='jm_columna caja' style='width:225px;height:38px;'><textarea id='"+p+"cpt"+i+"' name='"+p+"cpt"+i+"' style='width:215px;' rows='1'>"+jS.tbl[x][2]+" "+jS.tbl[x][5]+"</textarea>"+
                        "<input type='hidden' id='"+p+"idPr"+i+"' name='"+p+"idPr"+i+"' value='"+jS.tbl[x][0]+"' />"+
                        "<input type='hidden' id='"+p+"stk"+i+"' name='"+p+"stk"+i+"' value='"+jS.tbl[x][3]+"' />"+
                        "<input type='hidden' id='"+p+"p_c"+i+"' name='"+p+"p_c"+i+"' value='"+jS.tbl[x][4]+"' />"+
                        "<input type='hidden' id='"+p+"p_iva"+i+"' value='"+jS.tbl[x][5]+"' />"+
                        "<input type='hidden' id='"+p+"p_v"+i+"' value='"+jS.tbl[x][6]+"' />"+
                        "<input type='hidden' id='"+p+"p_des"+i+"' value='"+jS.tbl[x][7]+"' />"+
                        "<input type='hidden' id='"+p+"d_srv"+i+"' value='"+jS.tbl[x][8]+"' />"+
                        "<input type='hidden' id='"+p+"ut_min"+i+"' value='"+jS.tbl[x][9]+"' />"+
                        "<div id='"+p+"util"+i+"' style='display:none'></div></div>"+
                        "<div class='jm_columna caja' style='width:60px;height:38px;'><input type='text' onkeypress=\"_evaluar(event,'0123456789.')\" size='5' id='"+p+"c"+i+"' name='"+p+"c"+i+"' value='1' onkeyup=\"fac_calcTotales('"+p+"', "+i+")\" /></div>"+
                        "<div class='jm_columna der' style='width:60px;height:38px;'><input type='text' onkeypress=\"_evaluar(event,'0123456789.')\" size='6' id='"+p+"pu"+i+"' name='"+p+"pu"+i+"' style='text-align:right' onkeyup=\"fac_calcTotales('"+p+"', "+i+", 1)\" onblur=\"fac_calcTotales('"+p+"', "+i+", 2)\" /></div>" +
                        "<div class='jm_columna der' style='width:70px;height:38px;'><input type='text' size='5' id='"+p+"st"+i+"' name='"+p+"st"+i+"' readonly style='text-align:right' /></div>" +
                        "<div class='jm_columna der' style='width:60px;height:38px;'><input type='text' size='5' id='"+p+"d"+i+"' name='"+p+"d"+i+"' value='0' readonly style='text-align:right' /></div>" +
                        "<div class='jm_columna der' style='width:60px;height:38px;'><input type='text' size='5' id='"+p+"v"+i+"' name='"+p+"v"+i+"' readonly style='text-align:right' /></div>" +
                        "<div class='jm_columna der' style='width:80px;height:38px;'><input type='text' size='8' id='"+p+"t"+i+"' name='"+p+"t"+i+"' style='text-align:right' onkeyup=\"inv_calcSUBT('"+p+"', "+i+");fac_sumValores('"+p+"', "+i+");\" onblur=\"fac_verifUtil('"+p+"', "+i+")\" /></div>";
                        if(p==''){
                            h += "<div class='jm_columna der' style='width:100px;height:38px;' id='"+p+"axLiPr"+i+"'></div>";
                        }
                        h += "<div class='jm_columna der' style='width:20px;height:38px;'><div class='jm_icoCon' id='e"+i+"' style='background-position:-545px -18px;' onclick=\"_R('"+p+"r"+i+"');fac_sumValores('"+p+"');\" title='eliminar'>&nbsp;</div></div>";
                tr.innerHTML = h;
                _(p+'sll0').scrollTop = 10000;
                //fac_calcTotales(p, i);
            }
        }else{
            alert('Stock insuficiente para el producto seleccionado');
        }
    }
    _R('dv');
    _(p+'busProd').value='';
    _(p+'busProd').focus();
}
function fac_calcTarifa()
{
    var d = parseInt(_('ct0').value);
    var c = parseFloat(_('ctPl').value); 
    c = (d<=27 || d>30) ? ((c / 30) * d) : c;
    var st = _RD( c / (parseInt(_('p_iva1').value) / 100 + 1) );
    _('sbtInt').innerHTML = _('sbt0').value = st;
    _('v0').value = _RD(c - st);
    _('tt0').value = _RD(c); 
    fac_sumValores('');
}
function fac_calcTarifaDesc()
{
    var di = parseInt(_('diaDes').value); // dias descuento
    var ct = parseFloat(_('ctPl').value); // costo plan
    var st = parseFloat(_('subt').value); // subtotal no se modifica
    var plan = _('plan').value.toUpperCase();
    //var st = _RD( ct / (parseInt(_('p_iva1').value) / 100 + 1) );
    var pu = st / parseInt(_('c0').value);
    var dc = 0;
    var dc3 = 0;
    var dt = "SERVICIO DE INTERNET PLAN "+plan+" Mbps PERIODO FACTURADO "+_('txtPer').value;
    if(di>2 && di<29){
        dc = _RD(pu * di);
        dt += ". MENOS DESCUENTO APLICADO DE "+di+" DIAS POR INCONVENIENTES EN LA CONEXION DEL SERVICIO";
    }
    if((_('carne').value!='') &&
        (plan.indexOf("RESIDENCIAL")>=0 || plan.indexOf("NOCTURNO")>=0)){
        dc3 = _RD(st / 2);
        dt += ". MENOS DESCUENTO POR CAPACIDAD ESPECIAL";
    }
    _('cpt0').value = dt;

    _('d0').value = _RD(dc + dc3);
    //_('dt').value = _RD(dc);
    var iv = _RD( parseFloat( st - dc - dc3) * parseFloat(_('p_iva0').value) / 100 );
    _('st0').value = st;
    _('v0').value = iv;
    _('t0').value = _RD(st+iv-dc-dc3);
    fac_sumValores('');
}
function fac_preFacturaGuardar(f)
{
    var nf = _('nFa').value;
    if(f.sFa.value=='' || nf=='' || f.aut.value=='' || f.concepto.value==''){
        alert('Todos los campos son obligatorios.');
        return false;
    }
    if(_('pCh').style.display=='block'){
        if( _('chq').value=='' || _('bc').value==''){
            alert('Todos los campos marcados con  *  son obligatorios.');
            return false;
        }else{
            _('son').value=FloatToStr(_('tt').value);
        }
    }else{
        _('son').value='';
    }
    _('tope').value = _('tblD').childNodes.length>0 ? parseInt(_('tblD').childNodes.item(_('tblD').childNodes.length-1).id.replace('r', ''))+1 : 0;
    
    //    retencion

    var sr = _('sr').value;
    var nr = _('nr').value;
    var aur = _('aur').value;
    var fer = _('fer').value;
    var ef = _('ef').value;
    var tvr = _('tvr').value;
    if(tvr!='' && tvr!='0'){
        if(sr=='' || aur=='' || nr=='' || fer=='' || ef==''){
            alert('Por favor, ingrese todos los datos para la retención.');
            return false;
        }
        var idR = '';
        var bi = '';
        var vr ='';
        var i=0;
        while(i<10){
            if(_('idR'+i)!=null){
                idR += _('idR'+i).value+',';
                bi += _('bi'+i).value+',';
                vr += _('vr'+i).value+',';
            }
            i++;
        }
        if(i==0){
            alert('Por favor, falta ingresar el detalle de la retención.');
            return false;
        }
    }

    var foPa = _('foPa').value;
    if(foPa=='p' || foPa=='t' || foPa=='j'){
        var tt = parseFloat(_('tt').value);
        var saldo = parseFloat(_('saldo_doc').value);  /* por los 4 digitos */
        tvr = tvr!='' ? parseFloat(tvr) : 0;
        if((tt-tvr) > saldo){
            alert('El saldo $ '+saldo+' del documento ingresado no cubre el monto de la cuenta por cobrar.');
            return false;
        }
    }

    if((parseInt(nf) < parseInt(_('secDe').value) || parseInt(nf) > parseInt(_('secHa').value)) ){
        alert("Revise el número secuencial de la factura.");
        return false;
    }
    /*if(SelectorFecha.getTimestamp(_('fe').value) > SelectorFecha.getTimestamp(_('feCad').value)){
        alert("Revise la fecha de caducidad del facturero.");
        return false;
    }*/
    _('btnPreEn').disabled=true;    
    return _AJAX.enviarForm(f);
}
function fac_impPreFacturas()
{
    Ventana.crear('vta_im', 'Impresi&oacute;n de Reportes', "ancho=530,alto=300,modal=true,cerrar=true", "");
    _AJAX.solicitud('frmFiltro', 'op=22', false, 'vta_im_html');
}
function fac_preFactAnticipo()
{
    Ventana.crear('cmp', 'ANTICIPOS', "ancho=450,alto=250,modal=true,cerrar=true", '');
    _AJAX.solicitud('frmAnticipoInternetNuevo', '', false, 'cmp_html');
}
function fac_frmAnticipoInternetNuevoGuardar(f)
{
    if(f.id_instalacion.value=='' || f.fecha_ini.value=='' || f.fecha_fin.value==''){
        alert('Todos los campos son obligatorios.');
        return false;
    }
    return _AJAX.enviarForm(f);
}
function fac_getIblInstalls()
{
    _AJAX.solicitud('getTblInstalaciones', 'txt='+_('txtCli').value+'&fun=fac_setInstalAnticipo', false, 'axTb');
}
function fac_setInstalAnticipo(i)
{
    _('id_instalacion').value=_('ax_idIns'+i).innerHTML;
    _('axCi').innerHTML=_('ax_ci'+i).innerHTML;
    _('axCl').innerHTML=_('ax_cl'+i).innerHTML;
    _('axIP').innerHTML=_('ax_ip'+i).innerHTML;
    _R('ax');
}
function fac_preFactSuspensionGuardar(f)
{
    _AJAX.solicitud("frmSuspensionGuardar", 'id='+_('idIn').value+'&idSus=-1&fchI='+f+'&tmp=0&tipo=t&i=-1&imp=1', true, 'body');
    _('btnGenSus').disabled=_('btnPreEn').disabled=true;
}
function setMovilCliente(e, ob)
{
    var c = _getKeyCode(e);
    if(c==13) {
        _AJAX.solicitud("setMovilCliente", 'idInst='+_('idIn').value+'&campo='+ob+'&num='+_(ob).value, true, 'body'); 
        _inhibir(e);
    }
}
function fac_setPlan()
{
   _AJAX.solicitud("setSectorInstalacion", 'idInst='+_('idIn').value+'&idSec='+_('id_sector').value, true, 'body'); 
}
/*function diasDeMes(anio, mes){
    dias=[31,29,31,30,31,30,31,31,30,31,30,31];
    ultimo=0;
    if (mes==1){
        fecha=new Date(anio,1,29)
        vermes=fecha.getMonth();
        if(vermes!=mes){
            ultimo=28;
        }
    }
    if(ultimo==0){
        ultimo=dias[mes];
    }
    return ultimo;
}*/
function fac_frmAnticipo()
{
    var F = new Date();
    var ini = '01/'+(F.getMonth()+1)+'/'+F.getFullYear();
    var h = "<table width='100%'>";
    h += "<tr><td>Desde el per&iacute;odo: <span class='marca'>*</span></td>";
    h += "<td><input type='text' id='ant_fecha_ini' name='ant_fecha_ini' value='"+ini+"' size='10' readOnly /><input type='button' value='...' onClick=\"SelectorFecha.crear('ant_fecha_ini', 'SQL');\" /></td></tr>";
    h += "<tr><td>N&uacute;mero de meses: <span class='marca'>*</span></td>";
    h += "<td><input type='text' id='ant_num_mes' name='ant_num_mes' value='1' size='10' onkeypress='_numero(event)' /></td></tr>";
    h += "<tr><td colspan='2'><input type='button' value='Registrar Anticipo' onclick='fac_setAnticipo()' /></td></tr></table>";
    Ventana.crear('cmp', 'ANTICIPO', "ancho=300,alto=150,modal=true,cerrar=true", h);
}
function fac_setAnticipo()
{
    _('anticipo_ini').value=_('ant_fecha_ini').value;
    _('num_meses').value=_('ant_num_mes').value;
    
    var jS = eval('(' + _('axAnti').innerHTML + ')'); 
    if(parseFloat(jS.tbl[0][3]) > 0){
        var p = '';
        var ps = enFormulario( '', jS.tbl[0][1] );
        if(ps!=-1){
            _(p+"c"+ps).value = parseFloat(_(p+"c"+ps).value) + 1;
        }else{
            var i = _(p+'tblD').childNodes.length>0 ? parseInt(_(p+'tblD').childNodes.item(_(p+'tblD').childNodes.length-1).id.replace(p+'r', ''))+1 : 0;
            _AJAX.solicitud('getProductoPrecios', 'idPr='+jS.tbl[0][0]+'&pf='+p+'&i='+i, false, 'body');
            var tr = document.createElement("div");
            tr.id=p+'r'+i;
            tr.className='jm_fila';
            _(p+'tblD').appendChild(tr);
            var h = "<div class='jm_columna caja' style='width:100px;height:38px;' id='"+p+"cF"+i+"0'>"+jS.tbl[0][1]+"</div>" +
                    "<div class='jm_columna caja' style='width:225px;height:38px;'><textarea id='"+p+"cpt"+i+"' name='"+p+"cpt"+i+"' style='width:215px;' rows='1'>"+jS.tbl[0][2]+" "+jS.tbl[0][5]+"</textarea>"+
                    "<input type='hidden' id='"+p+"idPr"+i+"' name='"+p+"idPr"+i+"' value='"+jS.tbl[0][0]+"' />"+
                    "<input type='hidden' id='"+p+"stk"+i+"' name='"+p+"stk"+i+"' value='"+jS.tbl[0][3]+"' />"+
                    "<input type='hidden' id='"+p+"p_c"+i+"' name='"+p+"p_c"+i+"' value='"+jS.tbl[0][4]+"' />"+
                    "<input type='hidden' id='"+p+"p_iva"+i+"' value='"+jS.tbl[0][5]+"' />"+
                    "<input type='hidden' id='"+p+"p_v"+i+"' value='"+jS.tbl[0][6]+"' />"+
                    "<input type='hidden' id='"+p+"p_des"+i+"' value='"+jS.tbl[0][7]+"' />"+
                    "<input type='hidden' id='"+p+"d_srv"+i+"' value='"+jS.tbl[0][8]+"' />"+
                    "<input type='hidden' id='"+p+"ut_min"+i+"' value='"+jS.tbl[0][9]+"' />"+
                    "<div id='"+p+"util"+i+"' style='display:none'></div></div>"+
                    "<div class='jm_columna caja' style='width:60px;height:38px;'><input type='text' onkeypress=\"_evaluar(event,'0123456789.')\" size='5' id='"+p+"c"+i+"' name='"+p+"c"+i+"' value='1' onkeyup=\"fac_calcTotales('"+p+"', "+i+");_('num_meses').value=this.value;fac_setMeses("+i+");\" /></div>"+
                    "<div class='jm_columna der' style='width:60px;height:38px;'><input type='text' onkeypress=\"_evaluar(event,'0123456789.')\" size='6' id='"+p+"pu"+i+"' name='"+p+"pu"+i+"' style='text-align:right' onkeyup=\"fac_calcTotales('"+p+"', "+i+", 1)\" onblur=\"fac_calcTotales('"+p+"', "+i+", 2)\" /></div>" +
                    "<div class='jm_columna der' style='width:70px;height:38px;'><input type='text' size='5' id='"+p+"st"+i+"' name='"+p+"st"+i+"' readonly style='text-align:right' /></div>" +
                    "<div class='jm_columna der' style='width:60px;height:38px;'><input type='text' size='5' id='"+p+"d"+i+"' name='"+p+"d"+i+"' value='0' readonly style='text-align:right' /></div>" +
                    "<div class='jm_columna der' style='width:60px;height:38px;'><input type='text' size='5' id='"+p+"v"+i+"' name='"+p+"v"+i+"' readonly style='text-align:right' /></div>" +
                    "<div class='jm_columna der' style='width:80px;height:38px;'><input type='text' size='8' id='"+p+"t"+i+"' name='"+p+"t"+i+"' style='text-align:right' onkeyup=\"inv_calcSUBT('"+p+"', "+i+");fac_sumValores('"+p+"', "+i+");\" onblur=\"fac_verifUtil('"+p+"', "+i+")\" /></div>";
                    if(p==''){
                        h += "<div class='jm_columna der' style='width:100px;height:38px;' id='"+p+"axLiPr"+i+"'></div>";
                    }
                    h += "<div class='jm_columna der' style='width:20px;height:38px;'><div class='jm_icoCon' id='e"+i+"' style='background-position:-545px -18px;' onclick=\"_R('"+p+"r"+i+"');fac_sumValores('"+p+"');\" title='eliminar'>&nbsp;</div></div>";
            tr.innerHTML = h;
            _(p+'sll0').scrollTop = 10000;
            _('c'+i).value = _('ant_num_mes').value;
            _('pu'+i).value = _('st0').value;
            
            fac_setMeses(i);
            
            _R('bloq_cmp');
            _R('cmp');
    
            fac_calcTotales('', i);
        }
    }
}
function fac_setMeses(pos)
{
    var m = 1;
    var o = _('ant_fecha_ini').value;
    var n = _('ant_num_mes').value;
    if(o.indexOf('/')>0){
        var f = o.split('/');
    }else{
        f = o.split('-');
    }
    m = parseInt(f[1], 10);
    var res = '';
    var ms = new Array('enero', 'febrero', 'marzo', 'abril', 'mayo', 'junio', 'julio', 'agosto', 'septiembre', 'octubre', 'noviembre', 'diciembre');
    for(i=m; i<m+parseInt(n); i++){
        var j=i-1;
        if(i>12){
            j = i-13;
        }
        res += ms[j] + ' - ';
    }
    _('cpt'+pos).value = 'Anticipo del servicio de Internet para los meses de '+res.substr(0, res.length-2)+' ~';
}
/* VENTAS */
function fac_ventas()
{
    encerar();
    if(_MENUS_._enArreglo("ventaNueva")>=0){
        btn.icono(-360, 2, 34, 34, 'Nueva Factura de Venta', "fac_ventaEditar('-1');", 'd_herra');
    }
    if(_MENUS_._enArreglo("ventasImprimir")>=0){
        btn.icono(-455, -114, 34, 34, 'Imprimir', "fac_impVentas()", 'd_herra');
    }
    _AJAX.solicitud('frmFiltro', 'op=10&fn=fac_buscarVenta()', false, 'd_filtro');
    setAnchoPanel(2, 20);
}
function fac_ventaEditar(k, kPrFa)
{
    _('d_ayuda').innerHTML = msg();
    _('d_21').innerHTML = '';
    var f = kPrFa!=undefined ? "fac_preFacturaEditar("+kPrFa+", 0);" : ''; 
    Ventana.crear('cmp', 'VENTAS', "ancho=820,alto=550,modal=true,cerrar=true", '', f);
    new Tab('cmp_html', 750, 445);
    _AJAX.solicitud('frmFacturaVenta', 'id='+k+'&altB=445&kPrFa='+kPrFa, false, 'cmp_html');
}
function fac_setDetalle()
{
    _('ob').value = "Venta de mercadería Fc. " + _('sf').value + "-" + _('nf').value + " a "+_('raSo').value.toUpperCase()+".";
}
function fac_getFrmCliente()
{
    fac_addCliente(true);
    var h = '<br />CC/RUC: <input type="text" id="estab" value="001" onkeypress="_numero(event)" size="3" maxlength="3" />-'+
        '<input size="13" maxlength="13" type="text" id="txt_fil" onkeypress="var c=(_esN)?event.which:event.keyCode;if(c==13){fac_getCliente();}" />'+
        ' <input type="button" value="Buscar" onclick="fac_getCliente()" />';
    Ventana.crear('vtFilt', 'BUSQUEDA DE CLIENTES', "ancho=280,alto=100,modal=true,cerrar=true", h);
    _('txt_fil').focus();
}
function fac_getCliente()
{
    var t=_('txt_fil').value;
    if(t!=''){
        _AJAX.solicitud('getCliente', 'estab='+_('estab').value+'&ruc='+t+'&fun=fac_setCliente()', false, 'body');
    }
}
function fac_setConFinal()
{
    _('ruc').value='9999999999999';
    _('raSo').value='CONSUMIDOR FINAL';
    _('tel').value=_('dir').value='';
    //_('utCli').value='-1';
    _('ruc').readOnly=_('raSo').readOnly=_('dir').readOnly=_('tel').readOnly = true;
    fac_setDetalle();
}
function fac_setCliente()
{
    var r =_('axCl').innerHTML;
    if(r=='{tbl:]}' || r==''){
        if(confirm("El cliente con número de cédula o RUC "+_('txt_fil').value+" no se encuentra registrado.\n\nPara ingresar los datos de un nuevo cliente pulse ACEPTAR.\nO, para dejar los datos del CONSUMIDOR FINAL pulse CANCELAR.")){
            fac_addCliente(false);
            _('idCl').value='-1';
            _('ruc').value=_('raSo').value=_('tel').value=_('dir').value='';
            _('utCli').value='-1';
            _('ruc').focus();
        }else{
            fac_setConFinal();
        }
    }else{
        var jS = eval('(' + r + ')');
        _('idCl').value=jS.tbl[0][0];
        _('ruc').value=jS.tbl[0][1];
        _('raSo').value=htmlToEspecial(jS.tbl[0][2]);
        _('dir').value=jS.tbl[0][5];
        _('tel').value=jS.tbl[0][4];
        //_('utCli').value=jS.tbl[0][5];
        fac_setDetalle();
        _('dir').readOnly=_('tel').readOnly = (_('ruc').value=='9999999999999') ? true : false;
    }
    _R('vtFilt');
    _R('bloq_vtFilt');
}
function fac_addCliente(b)
{
    if(!b){
        _('idCl').value='-1';
        _('ruc').value=_('raSo').value=_('tel').value=_('dir').value='';
        _('ruc').focus();
    }
    _('ruc').readOnly=_('raSo').readOnly=b;
    _('dir').readOnly=_('tel').readOnly = (_('ruc').value=='9999999999999') ? true : false;
}
function fac_getProductos(event, t, f, pf, an)
{
    pf = pf || '';
    var c = _getKeyCode(event);
    if(_('lector').checked){
        if(c==13){
            _AJAX.solicitud('getProductosSucursal', 'txt='+t._trim()+'&fun='+f+'&ent='+c+'&pf='+pf+'&an='+an, false, null);
        }
    }else{
        _AJAX.solicitud('getProductosSucursal', 'txt='+t._trim()+'&fun='+f+'&ent='+c+'&pf='+pf+'&an='+an, false, null);
    }
}
function enFormulario(pf, c)
{
    var mId = _(pf+'tblD').childNodes.length>0 ? parseInt(_(pf+'tblD').childNodes.item(_(pf+'tblD').childNodes.length-1).id.replace(pf+'r', ''))+1 : 0;
    for(var i=0; i<=parseInt(mId); i++){
        if(_(pf+'r'+i)!=null){
            if(_(pf+'cF'+i+'0').innerHTML==c){
                return i;
            }
        }
    }
    return -1;
}
function fac_tblProductos(r, cod, pf, an)
{
    an = an || 690;
    _R('sonido');
    if(r != '{tbl:]}'){
        var t = 0;
        _('ax').innerHTML=r;
        setDiv('dv', an, 195, pf+'InAr');
        _T('dv', 90);
        var jS = eval('(' + r + ')');
        var h = '<table cellpadding="4" width="100%" cellspacing="0" border="0">';
        h += '<tr><th>CODIGO</th><th>DESCRIPCION</th><th>STOCK</th><th>P.V.</th><th>% DES.</th><th>TOTAL</th></tr>';
        for(var i=0; i<jS.tbl.length; i++){
            t = _RD(parseFloat(jS.tbl[i][6]) + (parseFloat(jS.tbl[i][6]) * parseFloat(jS.tbl[i][5]) / 100) );
            h += '<tr valign="top" onmouseover="this.className=\'jm_filaSobre\';this.style.cursor=\'pointer\'" onmouseout="this.className=\'\'" onclick="fac_addProducto(\''+pf+'\', '+i+')"><td id="axCodP'+i+'">'+jS.tbl[i][1]+'</td><td>'+jS.tbl[i][2]+' '+jS.tbl[i][5]+'</td><td>'+jS.tbl[i][3]+'</td><td>'+jS.tbl[i][6]+'</td><td>'+jS.tbl[i][7]+'</td><td>'+t+'</td></tr>';
        }
        h += '<table>';
        _('dv').innerHTML = h;
        if(cod == 13){
            fac_addProducto('', 0);
        }
    }else{
        alerta();
        _R('dv');
        _(pf+'busProd').value='';
    }
}
function fac_addProducto(pf, x)
{
    //_R('sonido');
    if(_('ax').innerHTML!='' && _('ax').innerHTML!='{tbl:]}'){
        var jS = eval('(' + _('ax').innerHTML + ')');
        if(parseFloat(jS.tbl[x][3]) > 0){
            var p = pf || '';
            var ps = enFormulario(pf, _('axCodP'+x).innerHTML);
            if(ps!=-1){
                _(p+"c"+ps).value = parseFloat(_(p+"c"+ps).value) + 1;
            }else{
                var i = _(p+'tblD').childNodes.length>0 ? parseInt(_(p+'tblD').childNodes.item(_(p+'tblD').childNodes.length-1).id.replace(p+'r', ''))+1 : 0;
                _AJAX.solicitud('getProductoPrecios', 'idPr='+jS.tbl[x][0]+'&pf='+p+'&i='+i, false, 'body');
                var tr = document.createElement("div");
                tr.id=p+'r'+i;
                tr.className='jm_fila';
                _(p+'tblD').appendChild(tr);
                var h = "<div class='jm_columna caja' style='width:100px;height:38px;' id='"+p+"cF"+i+"0'>"+jS.tbl[x][1]+"</div>" +
                        "<div class='jm_columna caja' style='width:225px;height:38px;'><textarea id='"+p+"cpt"+i+"' name='"+p+"cpt"+i+"' style='width:215px;' rows='1'>"+jS.tbl[x][2]+" "+jS.tbl[x][5]+"</textarea>"+
                        "<input type='hidden' id='"+p+"idPr"+i+"' name='"+p+"idPr"+i+"' value='"+jS.tbl[x][0]+"' />"+
                        "<input type='hidden' id='"+p+"stk"+i+"' name='"+p+"stk"+i+"' value='"+jS.tbl[x][3]+"' />"+
                        "<input type='hidden' id='"+p+"p_c"+i+"' name='"+p+"p_c"+i+"' value='"+jS.tbl[x][4]+"' />"+
                        "<input type='hidden' id='"+p+"p_iva"+i+"' value='"+jS.tbl[x][5]+"' />"+
                        "<input type='hidden' id='"+p+"p_v"+i+"' value='"+jS.tbl[x][6]+"' />"+
                        "<input type='hidden' id='"+p+"p_des"+i+"' value='"+jS.tbl[x][7]+"' />"+
                        "<input type='hidden' id='"+p+"d_srv"+i+"' value='"+jS.tbl[x][8]+"' />"+
                        "<input type='hidden' id='"+p+"ut_min"+i+"' value='"+jS.tbl[x][9]+"' />"+
                        "<div id='"+p+"util"+i+"' style='display:none'></div></div>"+
                        "<div class='jm_columna caja' style='width:60px;height:38px;'><input type='text' onkeypress=\"_evaluar(event,'0123456789.')\" size='5' id='"+p+"c"+i+"' name='"+p+"c"+i+"' value='1' onkeyup=\"fac_calcTotales('"+p+"', "+i+")\" /></div>"+
                        //"<div class='jm_columna der' style='width:60px;height:38px;'><input type='text' onkeypress=\"_evaluar(event,'0123456789.')\" size='6' id='"+p+"pu"+i+"' name='"+p+"pu"+i+"' style='text-align:right' /></div>" +
                        "<div class='jm_columna der' style='width:60px;height:38px;'><input type='text' onkeypress=\"_evaluar(event,'0123456789.')\" size='6' id='"+p+"pu"+i+"' name='"+p+"pu"+i+"' style='text-align:right' onkeyup=\"fac_calcTotales('"+p+"', "+i+", 1)\" onblur=\"fac_calcTotales('"+p+"', "+i+", 2)\" /></div>" +
                        "<div class='jm_columna der' style='width:70px;height:38px;'><input type='text' size='5' id='"+p+"st"+i+"' name='"+p+"st"+i+"' readonly style='text-align:right' /></div>" +
                        "<div class='jm_columna der' style='width:60px;height:38px;'><input type='text' size='5' id='"+p+"d"+i+"' name='"+p+"d"+i+"' value='0' style='text-align:right' onkeyup=\"fac_setDescuento('"+p+"', "+i+")\" /></div>" +
                        "<div class='jm_columna der' style='width:60px;height:38px;'><input type='text' size='5' id='"+p+"v"+i+"' name='"+p+"v"+i+"' readonly style='text-align:right' /></div>" +
                        "<div class='jm_columna der' style='width:80px;height:38px;'><input type='text' size='8' id='"+p+"t"+i+"' name='"+p+"t"+i+"' style='text-align:right' onkeyup=\"inv_calcSUBT('"+p+"', "+i+");fac_sumValores('"+p+"', "+i+");\" onblur=\"fac_verifUtil('"+p+"', "+i+")\" /></div>";
                        if(p==''){
                            h += "<div class='jm_columna der' style='width:100px;height:38px;' id='"+p+"axLiPr"+i+"'></div>";
                        }
                        h += "<div class='jm_columna der' style='width:20px;height:38px;'><div class='jm_icoCon' id='e"+i+"' style='background-position:-545px -18px;' onclick=\"_R('"+p+"r"+i+"');fac_sumValores('"+p+"');\" title='eliminar'>&nbsp;</div></div>";
                tr.innerHTML = h;
                _(p+'sll0').scrollTop = 10000;
                //fac_calcTotales(p, i);
            }
        }else{
            alert('Stock insuficiente para el producto seleccionado');
        }
    }
    _R('dv');
    _(p+'busProd').value='';
    _(p+'busProd').focus();
}
function fac_setProductoPrecios(r, p, i)
{
    if(p==''){
        _(p+'util'+i).innerHTML = r;
        var js = eval('(' + r + ')');
        var h = "<select id='"+p+"LiPr"+i+"' name='"+p+"LiPr"+i+"' style='width:96px' onchange=\"fac_calcTotales('"+p+"', "+i+")\">";
        for(var x=0; x<js.tbl.length; x++){
            h += "<option value='"+js.tbl[x][0]+"'>"+js.tbl[x][1]+"</option>";
        }
        h+="</select>";
        _(p+"axLiPr"+i).innerHTML = h;
    }
    fac_calcTotales(p, i);
}
function fac_calcTotales(nd, i, o)
{
    var dig = o || 0;
    var p_utl = 0;
    if(_(nd+'util'+i)==null){
        return false;
    }
    if(nd==''){
        if(_(nd+'util'+i).innerHTML  != ''){
            var jS = eval('(' + _(nd+'util'+i).innerHTML + ')');
            var x = _enMatrizJSON(jS, _(nd+'LiPr'+i).value, 0);
            p_utl = jS.tbl[x][2];
        }
    }
    var c = parseFloat(_(nd+'c'+i).value) || 1;
    var pc = parseFloat(_(nd+'p_c'+i).value);
    var ut = _RD((pc * p_utl / 100), 4);
    var pu = parseFloat(_(nd+'p_v'+i).value);
    var st = 0;
    var ds = 0;
    var iv = 0;
    var t = 0;
    if(_(nd+'d_srv'+i).value!='~'){  // producto
        if(dig==1 || dig==2){
            pu = _(nd+'pu'+i).value || 0;
            if(parseFloat(_(nd+'ut_min'+i).value) > parseFloat(pu) && dig==2){ // costo es mayor que lo que se ingresa
                alert('El precio de venta ingresado es menor al precio mínimo de utilidad.');
                fac_calcTotales(nd, i);
                return false;
            }
        }else{
            pu = _RD(pc + ut, 4);
        }
    }else{
        pu = _(nd+'pu'+i).value || 0;
    }
    st = _RD( c * parseFloat(pu) );
    if(_(nd+'t'+i)!=null){
        if(parseFloat(ds) == 0){
            var p_ds = _(nd+'p_des'+i).value || 0;
            ds = _RD( st * parseFloat(p_ds) / 100);
        }
        iv = _RD( (st - ds) * parseFloat(_(nd+'p_iva'+i).value) / 100 );
        t = _RD(st + iv - ds );
    }
    
    _(nd+'pu'+i).value = pu;
    _(nd+'st'+i).value = st;
    _(nd+'d'+i).value = ds;
    _(nd+'v'+i).value = iv;
    _(nd+'t'+i).value = t;
    fac_sumValores(nd);
}
function fac_setDescuento(p, i)
{
    var st = parseFloat(_(p+'st'+i).value);
    var ds = parseFloat(_(p+'d'+i).value);
    var iv = _RD( (st - ds) * parseFloat(_(p+'p_iva'+i).value) / 100 );
    _(p+'v'+i).value = iv;
    _(p+'t'+i).value = _RD(st + iv - ds );
    fac_sumValores(p);
}
function fac_verifUtil(nd, i)
{
    if(parseFloat(_(nd+'ut_min'+i).value) > parseFloat(_(nd+'pu'+i).value)){ // costo es mayor que lo que se ingresa
        alert('El precio de venta ingresado es menor al precio manimo de utilidad.');
        fac_calcTotales(nd, i);
        return false;
    }
}
function fac_sumValores(nd)
{
    var st = 0;
    var st0 = 0;
    var st2 = 0;
    var dt = 0;
    var iv2 = 0;
    var tt = 0;
    var mId = _(nd+'tblD').childNodes.length>0 ? _(nd+'tblD').childNodes.item(_(nd+'tblD').childNodes.length-1).id.replace(nd+'r', '') : 0;
    for(var i=0; i<=parseInt(mId); i++){
        if(_(nd+'r'+i)!=null){
            st += parseFloat(_(nd+'st'+i).value || 0);
            if(_(nd+'d'+i)!=null){
                dt += parseFloat(_(nd+'d'+i).value || 0);
            }
            if(_(nd+'p_iva'+i).value==12){
                st2 += parseFloat(_(nd+'st'+i).value || 0);
                iv2 += parseFloat(_(nd+'v'+i).value || 0);
            }
            if(_(nd+'p_iva'+i).value==0){
                st0 += parseFloat(_(nd+'st'+i).value || 0);
            }
            tt += parseFloat(_(nd+'t'+i).value || 0);
        }
    }
    _('subt').value = _RD(st);
    if(_('subt2')!=null){
        _('subt2').value = _RD(st2);
    }
    if(_('subt0')!=null){
        _('subt0').value = _RD(st0);
    }
    if(_('dt')!=null){
        _('dt').value = _RD(dt);
    }
    if(_('iva2')!=null){
        _('iva2').value = _RD(iv2);
    }
    _('tt').value = _RD(tt);
}
function fac_ventaGuardar(f)
{
    //var auf = _('auf').value;
    var sf = _('sf').value;
    var nf = _('nf').value;
    var ob = _('ob').value;
    var tt = _('tt').value;
    if(sf=='' || nf=='' || ob=='' || tt=='' || tt<=0){
        alert('Todos los campos marcados con  *  son obligatorios.');
        return false;
    }
    if(_('ruc').value=='9999999999999' && _('foPa').value!='e'){
        alert('La forma de pago para el CONSUMIDOR FINAL solo puede ser en EFECTIVO.');
        return false;
    }
    /*if(_('ruc').value!='9999999999999'){
        if(!esDocumento('ruc')){
            return false;
        }
    }*/
    if(_('pCh').style.display=='block'){
        if( _('chq').value=='' || _('bco').value==''){
            alert('Todos los campos marcados con  *  son obligatorios.');
            return false;
        }else{
            _('son').value=FloatToStr(_('tt').value);
        }
    }else{
        _('son').value='';
    }
    _('tope').value = _('tblD').childNodes.length>0 ? parseInt(_('tblD').childNodes.item(_('tblD').childNodes.length-1).id.replace('r', ''))+1 : 0;

    if(parseFloat(_('tope').value)==0){
        alert('Ingrese el detalle de la factura');
        return false;
    }

    //    retencion
    
    var sr = _('sr').value;
    var nr = _('nr').value;
    var aur = _('aur').value;
    var fer = _('fer').value;
    var ef = _('ef').value;
    var tvr = _('tvr').value;
    if(tvr!='' && tvr!='0'){
        if(_('ruc').value=='9999999999999'){
            alert('No se puede registrar una retención de consumidor final');
            return false;
        }
        if(sr=='' || aur=='' || nr=='' || fer=='' || ef==''){
            alert('Por favor, ingrese todos los datos para la retención.');
            return false;
        }
        var idR = '';
        var bi = '';
        var vr ='';
        var i=0;
        while(i<10){
            if(_('idR'+i)!=null){
                idR += _('idR'+i).value+',';
                bi += _('bi'+i).value+',';
                vr += _('vr'+i).value+',';
            }
            i++;
        }
        if(i==0){
            alert('Por favor, falta ingresar el detalle de la retención.');
            return false;
        }
    }

    var foPa = _('foPa').value;
    if(foPa=='p' || foPa=='t' || foPa=='j'){
        tt = parseFloat(tt);
        var saldo = parseFloat(_('saldo_doc').value) + 0.01;
        tvr = tvr!='' ? parseFloat(tvr) : 0;
        if((tt-tvr) > saldo){
            alert('El saldo $ '+saldo+' del documento ingresado no no cubre el monto de la cuenta por cobrar.');
            return false;
        }
    }
    
    if(parseInt(nf) < parseInt(_('secDe').value) || parseInt(nf) > parseInt(_('secHa').value)) {
        alert("Revise el número secuencial de la factura.");
        return false;
    }
    /*if(SelectorFecha.getTimestamp(_('fe').value) > SelectorFecha.getTimestamp(_('feCad').value)){
        alert("Revise la fecha de caducidad del facturero.");
        return false;
    }*/
    return _AJAX.enviarForm(f);
}
function fac_buscarVenta()
{
    var w = "where fecha_emision between '"+_('fi').value+"' and '"+_('ff').value+"' and de_activo=false";
    if(_('estDoc').value!=''){
        w = "where estado_documento='"+_('estDoc').value+"' and de_activo=false and anulado=false";
    }
    if(_('idSuc').value!='-0'){
        w += " and id_sucursal="+_('idSuc').value;
    }
    if(_('t_b').value!=''){
        w += " and (num_factura::varchar like '"+_('t_b').value+"%' or lower(ruc) like '"+_('t_b').value.toLowerCase()+"%' or lower(razon_social) like '%"+_('t_b').value.toLowerCase()+"%')";
    }
    _WR = encodeURI(w); 
    new Tabla('d_11', 'jmTbl', '', 'vta_factura_venta', 'RUC,CLIENTE,# FACTURA,F. EMISION,TOTAL VENTA,IP,ANULADA,ESTADO SRI,MENSAJE', 'id_factura_venta,ruc,razon_social,numero_factura,toDateSQL(fecha_emision),total,ip,txt_anulado,txt_estado_documento,mensaje', '0,90,200,100,80,80,100,80,110,200', _altEdi, 'fac_ventaEditar(^);', _WR);
}
function fac_ventaAnular(i)
{
    if(confirm('Se anularán también todos los documentos asociados a la factura tales como: retenciones y comprobantes de cobros.')){
        _AJAX.solicitud('frmFacturaVentaAnular', 'id='+i, true, 'body');
    }
}
function fac_impVentas()
{
    var F = new Date();
    var hoy = F.getDate()+'/'+(F.getMonth()+1)+'/'+F.getFullYear();
    var h = "<input type='radio' id='z1' name='z' value='8' checked='checked' /> <span onclick=\"_('z1').checked='true'\">Facturas de ventas </span>";
    h += "<br /><input type='radio' id='z0' name='z' checked='checked' value='41' /> <span onclick=\"_('z0').checked='true'\">Cierre de caja del d&iacute;a <input type='text' id='idCmb' value='"+hoy+"' size='9' readOnly><input type='button' value='...' onClick=\"SelectorFecha.crear('idCmb', 'SQL');\" /> </span>";
    h += "<br /><input type='radio' id='z2' name='z' value='47' /> <span onclick=\"_('z2').checked='true'\">Consolidado de instalaciones, servicio de Internet y otros ingresos desde <input type='text' id='bfi' value='01/01/"+F.getFullYear()+"' size='9' readOnly><input type='button' value='...' onClick=\"SelectorFecha.crear('bfi', 'SQL');\" /></span>";
    h += "<br /><input type='radio' id='z3' name='z' checked='checked' value='51' /> <span onclick=\"_('z3').checked='true'\">Notas de cr&eacute;dito <input type='text' id='pd' value='"+hoy+"' size='9' readOnly><input type='button' value='...' onClick=\"SelectorFecha.crear('pd', 'SQL');\" /> "+
        "hasta <input type='text' id='bff' value='"+hoy+"' size='9' readOnly><input type='button' value='...' onClick=\"SelectorFecha.crear('bff', 'SQL');\" /> </span>" +
        "<hr /><input type='radio' id='x' name='x' checked /> PDF &nbsp;&nbsp;&nbsp;&nbsp;&nbsp; <input type='radio' id='x2' name='x' /> Excel "+
        "<br/><br/><input type='button' value='Imprimir' onclick='imprimirReporte()'>";
    Ventana.crear('vta_im', 'Reportes de ventas', "ancho=560,alto=240,modal=true,cerrar=true", h);
}
/* RETENCION */
function fac_retencionGardar()
{
    var sr = _('sr').value;
    var nr = _('nr').value;
    var aur = _('aur').value;
    var fer = _('fer').value;
    var ef = _('ef').value;
    if(sr=='' || aur=='' || nr=='' || fer=='' || ef=='' || _('tvr').value==''){
        alert('Por favor, ingrese todos los datos para la retención.');
        return false;
    }
    var idR = '';
    var bi = '';
    var vr ='';
    var i=0;
    while(i<10){
        if(_('idR'+i)!=null){
            idR += _('idR'+i).value+',';
            bi += _('bi'+i).value+',';
            vr += _('vr'+i).value+',';
        }
        i++;
    }
    if(i==0){
        alert('Por favor, falta ingresar el detalle de la retención.');
        return false;
    }
    _('formVenta').action='frmRetencionVentaGuardar';
    return _AJAX.enviarForm(_('formVenta'));
}
/* NOTAS DE CREDITO */
function fac_setDetNotaCredito()
{
    var l = _('nctblD').childNodes.length>0 ? parseInt(_('nctblD').childNodes.item(_('nctblD').childNodes.length-1).id.replace('ncr', ''))+1 : 0;
    var c = 0;
    var vm = 0;
    var d = 0;
    var iv =0;
    var t=0;
    for(i=0; i<l; i++){        
        if(_('chProd'+i).checked){
            c = parseFloat(_('axNcC'+i).value);
            if(parseFloat(_('axNcC'+i).value) < parseFloat(_('ncC'+i).value)){
                _('ncC'+i).value = _('axNcC'+i).value;
            }
            axST = _RD((parseFloat(_('axncST'+i).value) / c) * parseFloat(_('ncC'+i).value));
            axDs = _RD((parseFloat(_('axncDes'+i).value) / c) * parseFloat(_('ncC'+i).value));
            axIv = _RD((parseFloat(_('axncIva'+i).value) / c) * parseFloat(_('ncC'+i).value));
            axT = _RD((parseFloat(_('axncT'+i).value) / c) * parseFloat(_('ncC'+i).value));
            _('ncST'+i).value = axST;
            _('ncDes'+i).value = axDs;
            _('ncIv'+i).value = axIv;
            _('ncT'+i).value = axT;
            vm += axST;
            d += axDs;
            iv += axIv;
            t += axT;
        }else{
            _('ncST'+i).value = _('axNcC'+i).value;
            _('ncST'+i).value = _('axncST'+i).value;
            _('ncDes'+i).value = _('axncDes'+i).value;
            _('ncIv'+i).value = _('axncIva'+i).value;
            _('ncT'+i).value = _('axncT'+i).value;
        }
    }
    _('ncsubt').value = _RD(vm);
    _('ncdt').value = _RD(d);
    _('nciva2').value = _RD(iv);
    //var r = _('ncIr')!=null ? parseFloat(_('ncIr').value) : 0;
    _('nctt').value = _RD(t);
}
function fac_notaCreditoEmitir(f)
{
    var sr = _('ncSerie').value;
    var nu = _('ncNum').value;
    var au = _('ncAut').value;
    var rm = _('ncRaMo').value;
    var vm = _('ncsubt').value;
    var tt = _('nctt').value;
    if(sr=='' || nu=='' || au=='' || rm=='' || vm=='' || tt=='' || parseFloat(tt)<=0){
        alert('Todos los campos son obligatorios.');
        return false;
    }
    if(parseInt(nu) < parseInt(_('ncSecDe').value) || parseInt(nu) > parseInt(_('ncSecHa').value)){
        alert("Revise el número secuencial de la nota de crédito.");
        return false;
    }
    /*if(SelectorFecha.getTimestamp(_('ncFeEm').value) > SelectorFecha.getTimestamp(_('ncFeCad').value)){
        alert("Revise la fecha de caducidad del libretín de nota de crébito.");
        return false;
    }*/
    _('nctope').value = _('nctblD').childNodes.length>0 ? parseInt(_('nctblD').childNodes.item(_('nctblD').childNodes.length-1).id.replace('ncr', ''))+1 : 0;
    f.action = 'frmNotaCreditoEmitir';
    return _AJAX.enviarForm(f);
}
function fac_setNumNotaCredito(k)
{
    var num = _('ncNum').value;
    if(num!='' && num!='0'){
        _AJAX.solicitud('frmSetNumNotaCredito', 'id='+k+'&ncSerie='+_('ncSerie').value+'&num='+num);
    }
}
function fac_notaCreditoAnular(k)
{
    if(confirm('Está seguro(a) de querer anular la nota de crédito.')){
        _AJAX.solicitud('frmNotaCreditoVentaAnular', 'id='+k, true, 'body');
    }
}
/* NOTAS DE DEBITO */
function fac_notaDebitoEmitir(f)
{
    var sr = _('ndSerie').value;
    var nu = _('ndNum').value;
    var au = _('ndAut').value;
    var rm = _('ndRaMo').value;
    var vm = _('ndVaMo').value;
    var tt = _('ndTotal').value;
    if(sr=='' || nu=='' || au=='' || rm=='' || vm=='' || tt=='' || parseFloat(tt)<=0){
        alert('Todos los campos son obligatorios.');
        return false;
    }
    if(_('ndpCh').style.display=='block'){
        if(_('ndchq').value=='' || _('ndbco').value==''){
            alert('Todos los campos de la forma de pago marcados con  *  son obligatorios.');
            return false;
        }else{
            _('ndson').value=FloatToStr(_('ndTotal').value);
        }
    }else{
        _('son').value='';
    }
    if(parseInt(nu) < parseInt(_('ndSecDe').value) || parseInt(nu) > parseInt(_('ndSecHa').value)){
        alert("Revise el número secuencial de la nota de dédito.");
        return false;
    }
    if(SelectorFecha.getTimestamp(_('ndFeEm').value) > SelectorFecha.getTimestamp(_('ndFeCad').value)){
        alert("Revise la fecha de caducidad del libretín de nota de débito.");
        return false;
    }
    _('ndtope').value = _('ndtblD').childNodes.length>0 ? parseInt(_('ndtblD').childNodes.item(_('ndtblD').childNodes.length-1).id.replace('ndr', ''))+1 : 0;
    f.action = 'frmNotaDebitoEmitir';
    return _AJAX.enviarForm(f);
}
/* COBROS */
function fac_cobros()
{
    encerar();
    btn.icono(-455, -114, 34, 34, 'Imprimir', "fac_impCobros();", 'd_herra');
    _AJAX.solicitud('frmFiltro', 'op=11&fn=fac_buscarCobros()', false, 'd_filtro');
    setAnchoPanel(2, 620);
}
function fac_buscarCobros()
{
    if(_('typ')==null){
       _('d_filtro').innerHTML += "<input type='hidden' id='typ' value='i' />";
    }
    _('d_21').innerHTML='&nbsp;';
    var w = "";
    var t = _('t_b').value.toLowerCase();
    if(_('cbs').value=='p'){
        _('grFch').style.visibility='hidden';
        w = "where credito>0";
        if(t!=''){
            w += " and (ruc like '"+t+"%' or lower(razon_social) like '%"+t+"%')";
        }
        _WR = encodeURI(w); 
        new Tabla('d_11', 'jmTbl', '', 'vta_cliente', 'CLIENTE,DEUDA', 'id_cliente,razon_social,credito', '0,200,80', _altEdi, 'fac_clienteCobrar(^);', _WR);
    }
    if(_('cbs').value=='f'){
        _('grFch').style.visibility='visible';
        w = "where fecha_pago between '"+_('fi').value+"' and '"+_('ff').value+"' and factura_anulada=false and id_sucursal="+_('idSuc').value;
        if(t!=''){
            w += " and (ruc like '"+t+"%' or lower(razon_social) like '%"+t+"%')";
        }
        _WR = encodeURI(w); 
        new Tabla('d_11', 'jmTbl1', '', 'vta_factura_venta_pago', 'CLIENTE,# FACTURA,# CHEQUE,F. COBRO,ABONO,FORMA P.', 'id_factura_venta_cobro,razon_social,numero_factura,txt_num_cheque,toDateSQL(fecha_pago),abono,txt_tipo_pago', '0,130,110,90,90,90,80,80', _altEdi, 'fac_facturaCobroEditar(^);', _WR);
    }
}
function fac_facturaCobroEditar(id)
{
    Ventana.crear('vta_nc', 'COMPROBANTE DE INGRESO', "ancho=820,alto=500,modal=true,cerrar=true", '');
    new Tab('vta_nc_html');
    _AJAX.solicitud('frmComprobanteIngresoCobro', 'id='+id, false, 'vta_nc_html');
}
function fac_clienteCobrar(k)
{
    new Ajax('frmClienteCobro', 'id='+k+'&aB='+_altBody, false, 'd_21');
}
function fac_getDocumentoCobro()
{
    if(parseFloat(_('tp').innerHTML)<=0){
        alert('No se puede realizar un pago con un valor de 0.');
        return false;
    }
    Ventana.crear('vta_nc', 'Comprobante de Ingreso', "ancho=820,alto=500,modal=true,cerrar=true", '');
    new Tab('vta_nc_html');
    _AJAX.solicitud('frmComprobanteIngresoCobro', 'id=-1&idC='+_('coC').value, false, 'vta_nc_html');
}
function fac_setDocumentoCobro()
{
    _('dt').value = 'Pago en efectivo, de la venta efectuada a '+_('ras').innerHTML;
    cnt_addCuenta('t', 'f');
    _('PC0').value = _('caj').value;
    _('d0').value = _('td').innerHTML = _('tp').innerHTML;
    cnt_addCuenta('t', 'f');
    _('PC1').value = _('cxc').value;
    _('axtp').value = _('h1').value = _('tp').innerHTML;
    _('nb').value = _('ras').innerHTML;
    _('sn').value = FloatToStr(_('tp').innerHTML);

    if(_('id').value == '-1'){
        var jS = eval('(' + _('axFVSR').innerHTML + ')');
        var h = '<select id="id_factura_venta" name="id_factura_venta">';
        var i=0;
        while(_('ab'+i)!=null){
            var x = _enMatrizJSON(jS, _('idF'+i).value, 0);
            if(_('ab'+i).value!='' && _('ab'+i).value!='0' && x>=0){
                h += '<option value="'+_('idF'+i).value+'">'+_('axSNFact'+i).innerHTML+'</option>';
            }
            i++;
        }
        h += '</select>';
        _('axIdFVA').innerHTML = h;
    }
}
function fac_setDetalleCobro()
{
    var ax = _('PC0').value;
    for(var i=0; i<20; i++){
        _R('r'+i);
    }
    var sg=0;
    var p = cnt_addCuenta('t', 'f');
    if(_('foPa').value == 'e'){
        _('dt').value = 'Pago en efectivo, de la venta efectuada a '+_('ras').innerHTML;
        _('PC'+p).value = _('caj').value;
    }else{
        _('f00').innerHTML = _('axb').innerHTML.replace(/_Xx_/g, 'PC0');
        _('PC0').value = ax;
    }
    switch(_('foPa').value){
        case 'c':
            _('dt').value = 'Pago con cheque, de la venta efectuada a '+_('ras').innerHTML;
        break;
        case 'p':
            _('dt').value = 'Pago con depósito, de la venta efectuada a '+_('ras').innerHTML;
        break;
        case 't':
            _('dt').value = 'Pago con transferencia, de la venta efectuada a '+_('ras').innerHTML;
            var a = cnt_addCuenta('t', 'f');
            _('PC'+a).value = _('gas_ban').value;
            _('d1').value=_('gb').value;
        break;
        case 'j':
            _('dt').value = 'Pago con tarjeta de crédito, de la venta efectuada a '+_('ras').innerHTML;
            a = cnt_addCuenta('t', 'f');
            _('PC'+a).value = _('gas_ban').value;
            _('d1').value=_('gb').value;
        break;
        case 'n':
            _('dt').value = 'Pago con convenio de dédito, de la venta efectuada a '+_('ras').innerHTML;
            a = cnt_addCuenta('t', 'f');
            _('PC'+a).value = _('gas_ban').value;
            _('d1').value=_('gb').value;
        break;
    }
    var jS = eval('(' + _('axPCV').innerHTML + ')');

    for(i=0; i<20; i++){
        if(_('idR'+i)!=null){
            var y = cnt_addCuenta('t', 'f');
            var x = _enMatrizJSON(jS, _('idR'+i).value, 0);
            _('PC'+y).value = jS.tbl[x][1];
            _('d'+y).value = _('vr'+i).value;
            _('h'+y).value = '';
            sg += parseFloat(_('vr'+i).value);
        }
    }
    y = cnt_addCuenta('t', 'f');
    _('PC'+y).value = _('cxc').value;
    _('d'+y).value = '';
    _('h'+y).value = _('tp').innerHTML;
    _('d'+p).value = _RD(parseFloat(_('h'+y).value) - sg);
    _('sn').value = FloatToStr(_('d'+p).value);
    fac_setGastosBancos();
}
function fac_setGastosBancos()
{
    var ax=_RD(parseFloat(_('axtp').value));
    var sg=0;
    for(var i=0; i<20; i++){
        if(_('idR'+i)!=null){
            sg += parseFloat(_('vr'+i).value);
        }
    }
    if(_('foPa').value=='n'){
        i=0;
        while(_('h'+i)!= null){
            i++;
        }
        ax = _RD(parseFloat(_('axtp').value) + parseFloat(_('gb').value));
        _('h'+(i-1)).value = ax;
    }else{
        _('d0').value = _RD(parseFloat(_('axtp').value) - sg - parseFloat(_('gb').value));
    }
    _('sn').value = FloatToStr(_('d0').value);
    _('td').innerHTML = _('th').innerHTML = _('tp').innerHTML = ax;
}
function fac_cobroGuardar(f)
{
    var idF = '';
    var idFP = '';
    var ab = '';
    var doc = '';
    var i=0;
    var k=f.id.value;
    while(_('idF'+i)!=null){
        if(k==-1){
            if(_('ab'+i).value!='' && _('ab'+i).value!='0'){
                if(_('idFP'+i)!=null){
                    idFP += _('idFP'+i).value + ',';
                }
                idF += _('idF'+i).value + ',';
                ab += _('ab'+i).value + ',';
                doc += _('doc'+i).value + ',';
            }
        }else{
            if(_('ab'+i).value=='' || _('ab'+i).value=='0' || _('ab'+i).value=='0.0'){
                alert('En la edición todos los valores de pago deben ser mayor que 0.');
                return false;
            }
            if(_('idFP'+i)!=null){
                idFP += _('idFP'+i).value + ',';
            }
            idF += _('idF'+i).value + ',';
            ab += _('ab'+i).value + ',';
            doc += _('doc'+i).value + ',';
        }
        i++;
    }

    var nb = _('nb').value;
    var bc = _('bc').value;
    var ch = _('ch').value;
    var c = _('dt').value;
    if(nb=='' || c==''){
        alert('Por favor, los campos marcados con un  *  son obligatorios.');
        return false;
    }
    if(_('foPa').value=='c'){
        if(ch=='' || bc==''){
            alert('Por favor, los campos marcados con un  *  son obligatorios.');
            return false;
        }
        if(_('fe').value!=''){
            var F = new Date();
            var hoy = F.getFullYear()+'-'+(F.getMonth()+1)+'-'+F.getDate();
            if(SelectorFecha.getTimestamp(_('fp').value) > SelectorFecha.getTimestamp(_('fe').value) || SelectorFecha.getTimestamp(_('fe').value) > SelectorFecha.getTimestamp(hoy)){
                alert('La fecha de efectivización debe encontrarse entre la fecha del formulario y la fecha actual.');
                return false;
            }
        }
    }
    if(_('foPa').value=='p' || _('foPa').value=='t'){
        if(_('ncp').value==''){
            alert('Por favor, los campos marcados con un  *  son obligatorios.');
            return false;
        }
        if((_('foPa').value=='t' || _('foPa').value=='j') && _('gb').value!=''){
            if(parseFloat(_('gb').value) != parseFloat(_('d1').value)){
                alert('El valor de los gastos bancarios no coinciden con la segunda cuenta del asiento');
                return false;
            }
        }
    }
    var td = _('td').innerHTML;
    var th = _('th').innerHTML;
    //var fp = (_('e0').checked) ? 'e' : ((_('e1').checked) ? 'c' : 'n');
    /*var p = 'id='+k+'&c='+c._codificarURI()+'&n='+_('nc').value+'&f='+_('fp').value+'&t='+td+'&g='+g+'&nb='+nb._codificarURI();
        p += '&bc='+bc._codificarURI()+'&ch='+ch+'&sn='+_('sn').value+'&tip=ch&ncp='+_('ncp').value+'&gb='+_('gb').value+
             '&idF='+idF.substr(0, idF.length-1)+'&ab='+ab.substr(0, ab.length-1)+'&tp='+_('tp').innerHTML+'&idFP='+idFP+
             '&fe='+_('fe').value+'&fp='+_('foPa').value+'&coC='+_('coC').value;*/
    var j = 0;
    var pc = '';
    var d = '';
    var h = '';
    for(i=0; i<50; i++){
        if(_('d'+i)!=null){
            pc += _('PC'+i).value + ',';
            d += ((_('d'+i).value!='')?_('d'+i).value:'0') + ',';
            h += ((_('h'+i).value!='')?_('h'+i).value:'0') + ',';
            j++;
        }
    }
    if(j < 2){
        alert('Por favor, ingrese por lo menos dos cuentas para que la transacción cumpla con la partida doble.');
        return false;
    }
    if(td=='0' || th=='0'){
        alert('Por favor, debe ingresar los valores correspondientes de cada cuenta sea del debe o del haber.');
        return false;
    }
    /*if(!cnt_verificarCuentas(pc)){
        alert('Por favor, las cuentas no deben repetirse.');
        return false;
    }*/
    if(parseFloat(td) != parseFloat(th)){
        alert('Por favor, los totales deben ser iguales.');
        return false;
    }
    //    retencion

    var sr = _('sr').value;
    var nr = _('nr').value;
    var aur = _('aur').value;
    var fer = _('fer').value;
    var ef = _('ef').value;
    var tvr = _('tvr').value;
    if(tvr!='' && tvr!='0'){
        if(_('ruc').value=='9999999999999'){
            alert('No se puede registrar una retención de consumidor final');
            return false;
        }
        if(sr=='' || aur=='' || nr=='' || fer=='' || ef==''){
            alert('Por favor, ingrese todos los datos para la retención.');
            return false;
        }
        var idR = '';
        var bi = '';
        var vr ='';
        i=0;
        while(i<10){
            if(_('idR'+i)!=null){
                idR += _('idR'+i).value+',';
                bi += _('bi'+i).value+',';
                vr += _('vr'+i).value+',';
            }
            i++;
        }
        if(idR==''){
            alert('Por favor, falta ingresar el detalle de la retención.');
            return false;
        }
    }
    if(_('id_factura_venta').value=='' && tvr!='' && tvr!='0'){
        alert('Si no hay una factura a la cual afecta la retención, por favor, limpie los datos de la retención.');
        return false;
    }
    _('idFs').value=idF.substr(0, idF.length-1);
    _('abs').value=ab.substr(0, ab.length-1);
    _('doc').value=doc.substr(0, doc.length-1);
    _('pcs').value=pc.substr(0, pc.length-1);
    _('ds').value=d.substr(0, d.length-1);
    _('hs').value=h.substr(0, h.length-1);
    _('vs').value=_('d0').value;

    var foPa = _('foPa').value;
    //if(foPa=='p' || foPa=='t' || foPa=='j'){
    if(foPa=='p' || foPa=='t'){
        var tt = parseFloat(_('d0').value);
        var saldo = parseFloat(_('saldo_doc').value);
        tvr = tvr!='' ? parseFloat(tvr) : 0;
        if(tt > saldo){
            alert('El saldo $ '+saldo+' del documento ingresado no cubre el monto de la cuenta por cobrar.');
            return false;
        }
    }

    return _AJAX.enviarForm(f);
    /*p += '&pc='+pc.substr(0, pc.length-1)+'&d='+d.substr(0, d.length-1)+'&h='+h.substr(0, h.length-1);
    _AJAX.solicitud('frmComprobanteIngresoCobroGuardar', p, true, 'body');*/
}
function fac_impCobros()
{
    var h = "<input type='radio' id='z0' style='display:none' checked value='33' /><input type='radio' id='x' name='x' checked /> PDF &nbsp;&nbsp;&nbsp;&nbsp;&nbsp; <input type='radio' id='x2' name='x' /> Excel "+
            "<br/><br/><input type='button' value='Imprimir' onclick='imprimirReporte()'>";
    Ventana.crear('vta_im', 'Impresi&oacute;n de Cobros', "ancho=250,alto=120,modal=true,cerrar=true", h);
}

/* comisiones */
function fac_comisiones()
{
    encerar();
    //btn.icono(-455, -114, 34, 34, 'Imprimir', "fac_impComisiones()", 'd_herra');
    _AJAX.solicitud('frmFiltro', 'op=34', false, 'd_filtro');
    setAnchoPanel(2, 900);
}
function fac_comisionBuscar()
{
    var w="where fecha_inicio between '"+_('fi').value+"' and '"+_('ff').value+"' ";
    var s = _('idSuc').value;
    if(s!='-0'){
        w = "and id_sucursal='"+s+"'";
    }
    _WR = w; 
    new Tabla('d_11', 'jmTbl', '', 'vta_instalacion_comision', 'SUCURSAL,F. DESDE,F. HASTA,RANKING,VENTAS,COMISION', 
    'id_instalacion_comision,sucursal,toDateSql(fecha_inicio),toDateSql(fecha_termino),ventas_promedio,ventas_actuales,comision', 
    '0,120,70,70,70,70,70', _altEdi, 'fac_comisionEditar(^);', _WR);
}
function fac_comisionEditar(k)
{
    _AJAX.solicitud('frmComision', 'id='+k+'&altB='+(_altBody), false, 'd_21');
}
/* ARQUEOS DE CAJA */
function fac_arqueos()
{
    encerar();
    _('d_filtro').innerHTML = setFechas() + '<br /> Responsable: ' + setCaja('t_b', 'fac_arqueoBuscar()')+ setBoton('button', 'Filtrar', 'fac_arqueoBuscar()');
    if(_MENUS_._enArreglo("arqueoNuevo")>=0){
        btn.icono(-360, 2, 34, 34, 'Nuevo Arqueo de Caja', "fac_arqueoEditar('-1');", 'd_herra');
    }
    btn.icono(-455, -114, 34, 34, 'Imprimir', "fac_impArqueos()", 'd_herra');
    _AJAX.solicitud('frmFiltro', 'op=24&fn=', false, 'd_filtro');
    setAnchoPanel(2, 20);
}
function fac_arqueoEditar(k)
{
    _('d_ayuda').innerHTML = msg();
    Ventana.crear('cmp', 'ARQUEO DE CAJA', "ancho=800,alto=550,modal=true,cerrar=true", '');
    new Tab('cmp_html', 750, 420);
    _AJAX.solicitud('frmArqueo', 'id='+k, false, 'cmp_html');
}
function fac_addEfectivo()
{
    var i=0;
    while(_('re'+i)!=null){
        i++;
    }
    var tr = document.createElement("tr");
    tr.id='re'+i;
    tr.className='jm_filaPar';
    _('tblEf').appendChild(tr);
    tr.innerHTML = "<td style='width:200px'><input type='text' id='den"+i+"' name='den"+i+"' value='' size='34' maxlength='20' /></td>"+
            "<td style='width:90px'><input id='c"+i+"' name='c"+i+"' type='text' value='1' class='inpTbl' size='9' onkeypress=\"_evaluar(event, '0123456789.');\" onfocus=\"this.select();\" /></td>"+
            "<td style='width:90px'><input id='t"+i+"' name='t"+i+"' type='text' class='inpTbl' size='9' onkeypress=\"_evaluar(event, '0123456789.');\" onkeyup=\"fac_sumArqueo();\" onfocus=\"this.select();\" /></td>"+
            "<td style='width:20px'><div class='jm_icoCon' style='background-position:-545px -18px;' title='Eliminar' onclick=\"_R('re"+i+"');fac_sumArqueo();\">&nbsp;</div></td>";
    _('den'+i).focus();
}
function fac_mulEfectivo(i)
{
    _('t'+i).value = _('c'+i).value!='' ? _RD(parseFloat(_('bill'+i).value) * parseFloat(_('c'+i).value)) : '';
}
function fac_addCheque()
{
    var i=0;
    while(_('rc'+i)!=null){
        i++;
    }
    var tr = document.createElement("tr");
    tr.id='rc'+i;
    tr.className='jm_filaPar';
    _('tblCh').appendChild(tr);
    tr.innerHTML = "<td style='width:200px'><input type='text' id='bc"+i+"' name='bc"+i+"' size='34' maxlength='50' /></td>"+
            "<td style='width:90px'><input id='ch"+i+"' name='ch"+i+"' type='text' class='inpTbl' size='9' onkeypress=\"_numero(event);\" onfocus=\"this.select();\" onkeyup=\"fac_sumCheques();\" /></td>"+
            "<td style='width:90px'><input id='v"+i+"' name='v"+i+"' type='text' class='inpTbl' size='9' onkeypress=\"_evaluar(event, '0123456789.');\" onkeyup=\"fac_sumArqueo();\" onfocus=\"this.select();\" /></td>"+
            "<td style='width:100px'><select id='ef"+i+"' name='ef"+i+"' onchange=\"fac_sumCheques();fac_sumArqueo();\"><option value=''> </option><option value='E'>E</option></select></td>"+
            "<td style='width:20px'><div class='jm_icoCon' style='background-position:-545px -18px;' title='Eliminar' onclick=\"_R('rc"+i+"');fac_sumArqueo();\">&nbsp;</div></td>";
    _('bc'+i).focus();
}
function fac_sumCheques()
{
    var n = 0;
    for(var i=0; i<1000; i++){
        if(_('bc'+i)!=null){
            if(_('ef'+i).value=='E'){
                n++;
            }
        }
    }
    _('num_cheques').value = n;
}
function fac_addDocPago()
{
    var i=0;
    while(_('ro'+i)!=null){
        i++;
    }
    var tr = document.createElement("tr");
    tr.id='ro'+i;
    tr.className='jm_filaPar';
    _('tblOtr').appendChild(tr);
    tr.innerHTML = "<td style='width:200px'><input id='doc"+i+"' name='doc"+i+"' type='text' style='width:195px' onkeypress=\"_numero(event);\" onfocus=\"this.select();\" onkeyup=\"fac_sumDocsPagos();\" /></td>"+
            "<td style='width:90px'><input id='vOtr"+i+"' name='vOtr"+i+"' type='text' class='inpTbl' size='9' onkeypress=\"_evaluar(event, '0123456789.');\" onkeyup=\"fac_sumArqueo();\" onfocus=\"this.select();\" /></td>"+
            "<td style='width:20px'><div class='jm_icoCon' style='background-position:-545px -18px;' title='Eliminar' onclick=\"_R('ro"+i+"');fac_sumArqueo();\">&nbsp;</div></td>";
    _('doc'+i).focus();
}
function fac_sumDocsPagos()
{
    var n = 0;
    for(var i=0; i<1000; i++){
        if(_('doc'+i)!=null){
            n ++;
        }
    }
    _('num_comp_pagos').value = n;
}
function fac_addRetencion()
{
    var i=0;
    while(_('rr'+i)!=null){
        i++;
    }
    var tr = document.createElement("tr");
    tr.id='rr'+i;
    tr.className='jm_filaPar';
    _('tblRet').appendChild(tr);
    tr.innerHTML = "<td style='width:200px'><input id='ret"+i+"' name='ret"+i+"' type='text' style='width:195px' onkeypress=\"_evaluar(event, '0123456789-');\" onfocus=\"this.select();\" onkeyup=\"fac_sumRetenciones();\" value=\"001-001-\" /></td>"+
            "<td style='width:90px'><input id='vRet"+i+"' name='vRet"+i+"' type='text' class='inpTbl' size='9' onkeypress=\"_evaluar(event, '0123456789.');\" onkeyup=\"fac_sumArqueo();\" onfocus=\"this.select();\" /></td>"+
            "<td style='width:90px'><select id='frpg"+i+"' name='frpg"+i+"' onchange=\"fac_sumRetenciones();fac_sumArqueo();\"><option value='e'>efectivo</option><option value='c'>cheque</option><option value='p'>dep&oacute;sito</option><option value='t'>transferencia</option><option value='j'>tarjeta</option><option value='d'>cr&eacute;dito</option></select></td>"+
            "<td style='width:20px'><div class='jm_icoCon' style='background-position:-545px -18px;' title='Eliminar' onclick=\"_R('rr"+i+"');fac_sumArqueo();\">&nbsp;</div></td>";
    _('ret'+i).focus();
}
function fac_sumRetenciones()
{
    var n = 0;
    for(var i=0; i<1000; i++){
        if(_('ret'+i)!=null){
            n ++;
        }
    }
    _('num_retenciones').value = n;
}
function fac_sumArqueo()
{
    var t = 0;
    for(var i=0; i<20; i++){
        if(_('t'+i)!=null){
            t += parseFloat(_('t'+i).value!='' ? _('t'+i).value : 0);
        }
    }
    _('valor_efectivo').value = _RD(t);
    var v = 0;
    for(i=0; i<1000; i++){
        if(_('v'+i)!=null){
            if(_('ef'+i).value=='E'){
                v += parseFloat(_('v'+i).value!='' ? _('v'+i).value : 0);
            }
        }
    }
    _('valor_cheques').value = _RD(v);
    var vo = 0;
    for(i=0; i<1000; i++){
        if(_('vOtr'+i)!=null){
            vo += parseFloat(_('vOtr'+i).value!='' ? _('vOtr'+i).value : 0);
        }
    }
    _('valor_comp_pagos').value = _RD(vo);
    var vr = 0;
    for(i=0; i<1000; i++){
        if(_('vRet'+i)!=null){
            if(_('frpg'+i).value=='e'){
                vr += parseFloat(_('vRet'+i).value!='' ? _('vRet'+i).value : 0);
            }
        }
    }
    _('valor_retenciones').value = _RD(vr);
    _('total_caja').value = _RD(t);
    var d = _RD(parseFloat(_('saldo_caja').value) - parseFloat(_('total_caja').value));
    //_('diferencia').value = d;
    _('dt').value = (d>0 ? 'Faltante' : 'Sobrante');
    _('diferencia').value = _('va').value = _('de0').value = _('h1').value = Math.abs(d);
    fac_diferencia();
}
function fac_diferencia()
{
    var d = _RD(parseFloat(_('saldo_caja').value) - parseFloat(_('total_caja').value));
    if(d==0){
        _('d0').checked=_('d1').checked=false;
    }else{
        if(d>0){
            _('d0').checked=true;
            _('PC0').value = _('idPCDC').value;
            _('PC1').value = _('idPCC').value;
        }else{
            _('d1').checked=true;
            _('PC0').value = _('idPCC').value;
            _('PC1').value = _('idPCDC').value;
        }
    }    
}
function fac_arqueoGuardar(f)
{
    var t = _('total_caja').value;
    if(t=='' || t=='0'){
        alert('Por favor, registre los importes de la caja.');
        return false;
    }
    /*if(!_esHora('hora')){
        return false;
    }*/
    if(confirm("Al registrar el arqueo de caja ya no podrá modificarlo o anularlo.\n¿Está seguro(a) de realizar el arqueo de caja?")){
        return _AJAX.enviarForm(f);
    }
    return false;
}
function fac_arqueoAnular(k)
{
    _AJAX.solicitud('frmArqueoAnular', 'id='+k+'&num='+_('num_documento').value, false, 'cmp_html');
}
function fac_arqueoBuscar()
{
    var w="where cajero='"+_USR_+"' and ";
    if(_('adm').value==1){
        w = "where ";
    }
    var a = _('resp').value;
    if(a!='-0'){
        w = "where cajero='"+a+"' and ";
    }
    _WR = w+"fecha between '"+_('fi').value+"' and '"+_('ff').value+"' order by fecha desc, hora desc"; 
    new Tabla('d_11', 'jmTbl', '', 'vta_arqueo_caja', 'RESPONSABLE,Nro. DOC.,FECHA,HORA,VENTAS,DIF.,ANULADO', 'id_arqueo_caja,cajero,num_documento,toDateSql(fecha),hora,total,diferencia,txt_anulado', '0,110,80,70,60,60,60,70', _altEdi, 'fac_arqueoEditar(^);', _WR);
}
function fac_arqueoCerrar(){
    
    if(_('crr_dt.value')==''){
        alert('Por favor, ingrese el detalle.');
        return false;
    }
    _AJAX.solicitud('frmArqueoCerrar', 'id='+_('id').value+'&fp='+_('crr_fp').value+'&crr_dt='+_('crr_dt').value+
        '&crr_PC0='+_('crr_PC0').value+'&crr_PC1='+_('crr_PC1').value+'&crr_de0='+_('crr_de0').value);
    return false;
}
function fac_impArqueos()
{
    var h = "<form onsubmit='return reporte(this)' autocomplete='off'>" + setRadio('z', '{tbl:[{0:"ArqueosCaja",1:"Arqueos de caja"}]}') + setPieReporte() +"</form>";
    Ventana.crear('vta_im', 'Impresi&oacute;n de Arqueos', "ancho=280,alto=160,modal=true,cerrar=true", h);
}
/* AREAS */
function nom_areas()
{
    encerar();
    _('d_filtro').innerHTML = 'Area: <input type="text" id="t_b" onkeypress="var c=(_esN)?event.which:event.keyCode;if(c==13){nom_areaBuscar();}">'+
        '&nbsp;<input type="button" value="Filtrar" onclick="nom_areaBuscar();">';
    if(_MENUS_._enArreglo("areaNuevo")>=0){
        btn.icono(-360, 2, 34, 34, 'Nueva Area', "nom_areaEditar('-1');", 'd_herra');
    }
    if(_MENUS_._enArreglo("areaEliminar")>=0){
        btn.icono(-391, 3, 34, 34, 'Eliminar', "_objT.eliminar();", 'd_herra');
    }
    btn.icono(-455, -114, 34, 34, 'Imprimir', "nom_impAreas();", 'd_herra');
    nom_areaBuscar();
    setAnchoPanel(2, 470);
}
function nom_areaBuscar()
{
    var w='where';
    if(_('t_b')!=null){
        var a=_('t_b').value;
        if(a!=''){
            w += " lower(area) like '"+a.toLowerCase()+"%' and ";
        }
    }
    _WR = encodeURI(w + ' eliminado=false');
    new Tabla('d_11', 'jmTbl', '', 'tbl_area', 'AREA', 'id_area,area', '0,150', _altEdi, 'nom_areaEditar(^);', _WR);
}
function nom_areaEditar(k)
{
    //_('d_ayuda').innerHTML = msg();
    _AJAX.solicitud('frmArea', 'id='+k+'&altB='+(_altBody-310), false, 'd_21');
}
function nom_areaGuardar(f)
{
    if(f.area.value==''){
        alert('Por favor, ingrese el nombre del área o departamento');
        return false;
    }
    return _AJAX.enviarForm(f);
}
function nom_impAreas()
{
    var h = "<form onsubmit='return reporte(this)' autocomplete='off'>" + setRadio('z', '{tbl:[{0:"Areas",1:"Areas / departamentos"}]}') + setPieReporte() +"</form>";
    Ventana.crear('vta_im', 'Impresi&oacute;n de Areas', "ancho=280,alto=160,modal=true,cerrar=true", h);
}

/*  cargos  */
function nom_cargos()
{
    encerar();
    _('d_filtro').innerHTML = 'Cargo: <input type="text" id="t_b" onkeypress="var c=(_esN)?event.which:event.keyCode;if(c==13){nom_cargoBuscar();}">'+
        '&nbsp;<input type="button" value="Filtrar" onclick="nom_cargoBuscar();">';
    if(_MENUS_._enArreglo("cargoNuevo")>=0){
        btn.icono(-360, 2, 34, 34, 'Nuevo Cargo', "nom_cargoEditar('-1');", 'd_herra');
        btn.icono(-261, -227, 34, 34, 'Incremento de sueldos', "nom_cargoSetSueldos();", 'd_herra');
    }
    if(_MENUS_._enArreglo("cargoEliminar")>=0){
        btn.icono(-391, 3, 34, 34, 'Eliminar', "_objT.eliminar();", 'd_herra');
    }
    btn.icono(-455, -114, 34, 34, 'Imprimir', "nom_impCargos();", 'd_herra');
    nom_cargoBuscar();
    setAnchoPanel(2, 560);
}
function nom_cargoBuscar()
{
    var w='where';
    if(_('t_b')!=null){
        var a=_('t_b').value;
        if(a!=''){
            w += " lower(cargo) like '"+a.toLowerCase()+"%' and ";
        }
    }
    _WR = encodeURI(w + ' eliminado=false');
    new Tabla('d_11', 'jmTbl', '', 'vta_cargo', 'AREA / DEPARTAMENTO,CARGO', 'id_cargo,area,cargo,sueldo', '0,150,150,70', _altEdi, 'nom_cargoEditar(^);', _WR);
}
function nom_cargoEditar(k)
{
    //_('d_ayuda').innerHTML = msg();
    _AJAX.solicitud('frmCargo', 'id='+k+'&altB='+(_altBody-380), false, 'd_21');
}
function nom_cargoGuardar(f)
{
    if(f.cargo.value==''){
        alert('Por favor, ingrese el nombre del cargo');
        return false;
    }
    return _AJAX.enviarForm(f);
}
function nom_cargoSetSueldos()
{
    _AJAX.solicitud('frmCargoSetSueldos', '&altB='+(_altBody-326), false, 'd_21');
}
function nom_cargoSetIncSueldos()
{
    var i=0;
    var inc = _('porInc').value;
    while(_('sueldo'+i)!=null){
        var s = parseFloat(_('sueldo_ant'+i).value);
        _('sueldo'+i).value = _RD( s + (s*inc/100) );
        i++;
    }
}
function nom_cargoSetSueldosGuardar(f)
{
    var i=0;
    while(_('sueldo'+i)!=null){
        if(parseFloat(_('sueldo_ant'+i).value) == parseFloat(_('sueldo'+i).value)){
            return false;
        }
        i++;
    }
    if(confirm('Va a realizar un incremento de sueldos, no podrá deshacer esta acción, desea continuar?')){
        return _AJAX.enviarForm(f);
    }
    return false;
}
function nom_impCargos()
{
    var h = "<form onsubmit='return reporte(this)' autocomplete='off'>" + setRadio('z', '{tbl:[{0:"Cargos",1:"Cargos"}]}') + setPieReporte() +"</form>";
    Ventana.crear('vta_im', 'Impresi&oacute;n de Cargos', "ancho=280,alto=160,modal=true,cerrar=true", h);
}

/* EMPLEADOS */
function nom_empleados()
{
    encerar();
    setAnchoPanel(2, 570);
    _AJAX.solicitud('frmFiltro', 'op=33&fn=nom_buscarEmpleado()', false, 'd_filtro');
    /*_('d_filtro').innerHTML = 'C&eacute;dula, nombres: <input type="text" id="t_b" onkeypress="var c=(_esN)?event.which:event.keyCode;if(c==13){nom_buscarEmpleado();}">'+
        '&nbsp;<input type="button" value="Filtrar" onclick="nom_buscarEmpleado();">';*/
    if(_MENUS_._enArreglo("empleadoNuevo")>=0){
        btn.icono(-360, 2, 34, 34, 'Nuevo Empleado', "nom_empleadoEditar('-1');", 'd_herra');
    }
    if(_MENUS_._enArreglo("empleadoEliminar")>=0){
        btn.icono(-391, 3, 34, 34, 'Eliminar', "_objT.eliminar();", 'd_herra');
    }
    if(_MENUS_._enArreglo("empleadoImprimir")>=0){
        btn.icono(-455, -114, 34, 34, 'Imprimir', "nom_impEmpleados();", 'd_herra');
    }
    if(_MENUS_._enArreglo("empleadoImprimir")>=0){
        //x, y, ancho, alto
        btn.icono(-337, -225, 34, 34, 'Imprimir Carnets', "nom_impEmpleadosCarnets();", 'd_herra');
    }
    nom_buscarEmpleado();
}
function nom_empleadoEditar(k)
{
    _('d_ayuda').innerHTML = msg() + '.<br />La fecha de ingreso ser&aacute; tomada como referencia para el pago de las vacaciones anuales.';
    new Tab('d_21', 390, _altBody-310);
    _AJAX.solicitud('frmEmpleado', 'id='+k+'&altB='+(_altBody-310), false, 'd_21');
}
function nom_setUbicacion(id, idObj, obj, fun)
{
    if(fun=='1'){
        fun = "nom_setUbicacion(this.value, 'id_parroquia', 'auxParr', '')";
        _('auxParr').innerHTML='';
    }
    _AJAX.solicitud('getCiudades', 'id='+idObj+'&idP='+id+'&obj='+obj+'&fun='+fun, false, obj);
}
function nom_empleadoFamiliaEliminar(k)
{
    var empleado=_('apellido').value;
    var familia=_('nom_fami'+k).value;
    confirmar=confirm("¿Seguro que desea Eliminar al Familiar?"); 
    if (confirmar){
        _AJAX.solicitud('frmEmpleadoFamiliaEliminar', 'id='+k+'&familia='+familia+'&empleado='+empleado, false, 'd_21');
    }    
}
function nom_empleadoFormacionEliminar(k)
{
    var empleado=_('apellido').value;
    var formacion=_('nom_ins'+k).value;
    confirmar=confirm("¿Seguro que desea Eliminar la Formacion?"); 
    if (confirmar){
        _AJAX.solicitud('frmEmpleadoFormacionEliminar', 'id='+k+'&formacion='+formacion+'&empleado='+empleado, false, 'd_21');
    }    
}
function nom_guardarfamilia()
{
    if(_('padre_nombre').value=='')
    {
        alert('Debe llenar los campos con *');
    }
}
function nom_nuevoCarnet(k)
{
    var empleado=_('apellido').value;
    var elementos = document.getElementsByName("identificacion");
    var texto = "";
    for (x=0;x<elementos.length;x++)
    {
        if(elementos[x].checked)
        {
            texto =  texto + elementos[x].value + ",";
        }
    }    
    texto=texto.substr(0,texto.length-1);
    _AJAX.solicitud('frmEmpleadoGuardarCarnet', 'id='+k+'&texto='+texto+'&empleado='+empleado, false, 'd_21');
}
function nom_nuevofamilia()
{
    _('est').value='-1';
    _('padre_nombre').value='';
    _('padre_dir_trabajo').value='';
    _('padre_telefono').value='';
    _('fecha_nac_familia').value='';
    _('carne_conadis_familia').value='';
    _('discapacidad_familia').value='';
}
function nom_nuevoFormacion()
{
    _('estado_formacion').value='-2';
    _('nombre_institucion').value='';
    _('titulo_ob').value='';
    _('fecha_for').value='';
}
function nom_setFormacion(id)
{
    _('id_formacion').value=id;
    _('nombre_institucion').value=_('nom_ins'+id).value;
    _('titulo_ob').value=_('titu'+id).value;
    _('fecha_for').value=_('fecha_titulo'+id).value;
    _('estado_formacion').value='2';
    
    var combo = _("instruccion");
    var cantidad = combo.children.length;

    for (i = 0; i < cantidad; i++) {
       if (combo[i].value == _('instrucci'+id).value) {
          combo[i].selected = true;
       }   
    }
    //_('id_parentesco').value=_('nom_pare'+id).value;
    //_AJAX.solicitud('getHorario', 'id='+idObj+'&idP='+id+'&obj='+obj+'&fun='+fun, false, obj);
}
function nom_setfamilia(id)
{
    _('id_fa').value=id;
    _('padre_nombre').value=_('nom_fami'+id).value;
    _('padre_dir_trabajo').value=_('nom_dir'+id).value;
    _('padre_telefono').value=_('nom_telf'+id).value;
    _('fecha_nac_familia').value=_('nom_nac'+id).value;
    _('carne_conadis_familia').value=_('nom_carnet'+id).value;
    _('discapacidad_familia').value=_('nom_disca'+id).value;
    _('est').value='1';
    if(_('nom_vive'+id).value=='t')
    {
        _('vive0').checked=true;
    }
    if(_('nom_vive'+id).value=='f')
    {
        _('vive1').checked=true;
    }
    
    var combo = _("id_parentesco");
    var cantidad = combo.children.length;
    for (i = 0; i < cantidad; i++) {
       if (combo[i].value == _('nom_pare'+id).value) {
          combo[i].selected = true;
       }   
    }
    //_('id_parentesco').value=_('nom_pare'+id).value;
    //_AJAX.solicitud('getHorario', 'id='+idObj+'&idP='+id+'&obj='+obj+'&fun='+fun, false, obj);
}
function nom_setHorario(id, idObj, obj, fun)
{
    _AJAX.solicitud('getHorario', 'id='+idObj+'&idP='+id+'&obj='+obj+'&fun='+fun, false, obj);
}
function nom_empleadoGuardar(f)
{
    if(_('id_cargo')==null){
        return false;
    }
    var dn = _('dni').value;
    var no = _('nombre').value;
    var ap = _('apellido').value;
    var te = _('telefono').value;
    var ca = _('calle').value;
    var nu = _('numero').value;
    if(dn=='' || no=='' || ap=='' || ca=='' || nu=='' || _('fecha_ing').value=='' || f.ac_no.value=='' || f.tipo_sangre.value=='' 
         || f.id_cargo.value=='-0'){
        alert('Todos los campos marcados con  *  son obligatorios.');
        return false;
    }
    if(_('vf').checked){
        if(!esDocumento('dni')){
            return false;
        }
    }
    if(_('email').value!=''){
        if(!_esMail('email')){
            return false;
        }
    }
    return _AJAX.enviarForm(f);
}

function nom_usuario()
{
    var primer=_('nombre').value.charAt(0);
    var segundo=_('apellido').value;
    var apellido='';
    var total=segundo.indexOf(' ');
    if(total!=('-1'))
    {
        apellido=segundo.substring(0, total);
    }
    _('alias').value=primer.toLowerCase()+apellido.toLowerCase();
    
}

function nom_buscarEmpleado()
{
    var w = 'where';
    if(_('idSuc').value!='-0'){
        w += w=='where' ? " id_sucursal="+_('idSuc').value+"" : " and id_sucursal="+_('idSuc').value+"";
    }
    if(_('id_area').value!='-0'){
        w += w=='where' ? " id_area="+_('id_area').value+"" : " and id_area="+_('id_area').value+"";
    }
     if(_('id_cargo').value!='-0'){
        w += w=='where' ? " id_cargo="+_('id_cargo').value+"" : " and id_cargo="+_('id_cargo').value+"";
    }
    var a=_('t_b').value;
    if(a!=''){
        w += w=='where' ? " lower(dni) like '"+a.toLowerCase()+"%' or lower(empleado) like '%"+a.toLowerCase()+"%'" : " and (lower(dni) like '"+a.toLowerCase()+"%' or lower(empleado) like '%"+a.toLowerCase()+"%')";
    }
    if(w == 'where'){
        w = '';
    }

    /*var w='';
    if(_('t_b')!=null){
        var a=_('t_b').value;
        if(a!=''){
            w = "where lower(dni) like '"+a.toLowerCase()+"%' or lower(empleado) like '%"+a.toLowerCase()+"%'";
        }
    }*/
    _WR = encodeURI(w);
    new Tabla('d_11', 'jmTbl', '', 'vta_empleado', 'DEPARTAMENTO,CARGO,IDENTI.,EMPLEADO', 'id_empleado,departamento,rol,dni,empleado', '0,110,150,120,250', _altEdi, 'nom_empleadoEditar(^);', _WR);
}
function cargarCargo(){
    nom_buscarEmpleado();
    _AJAX.solicitud('getCargo', 'id='+_('id_area').value+'&obj='+'id_car'+'&fun=nom_buscarEmpleado()', false, 'id_car');
}
function nom_impEmpleadosCarnets()
{
    var k = getMarcados();
    if(!k){
        return false;
    }
    imprimir('pdfEmpleadosCarnets?id='+getMarcados());
    /*var h = '<form onsubmit="return reporte(this)" autocomplete="off">' +
        setRadio('z', '{tbl:[{0:"Empleados",1:"Listado de empleados"},{0:"EmpleadosVacaciones",1:"Empleados pr&oacute;ximos a salida a vacaciones"},{0:"EmpleadosCarnets",1:"Imprimir Carnets de Identificacion"}]}', "") +
        setPieReporte() + '</form>';
    Ventana.crear('vta_im', 'Impresi&oacute;n de Empleados', "ancho=300,alto=180,modal=true,cerrar=true", h);*/
}
function nom_impEmpleados()
{
    var h = '<form onsubmit="return reporte(this)" autocomplete="off">' +
        setRadio('z', '{tbl:[{0:"Empleados",1:"Listado de empleados"},{0:"EmpleadosVacaciones",1:"Empleados pr&oacute;ximos a salida a vacaciones"}]}', "") +
        setPieReporte() + '</form>';
    Ventana.crear('vta_im', 'Impresi&oacute;n de Empleados', "ancho=300,alto=180,modal=true,cerrar=true", h);
}
function nom_getEmpleados(idS)
{
    var s = idS || '0';
    _AJAX.solicitud('getEmpleados', 'idS='+s+'&obj=axRes&codE=id_responsable', false, 'axRes');
}
function nom_setArchivoBiometrico()
{
    Ventana.crear('vta_subir', 'Transferencia de Archivos', "ancho=440,alto=110,modal=true,cerrar=true", '');
    _AJAX.solicitud('SubirArchivo', 'pr=ProcesaArchivoBiometrico', true, 'vta_subir_html');
}
/* Permisos y licencias*/
function pyl_permisos()
{
    encerar();
    /*_('d_filtro').innerHTML = 'Tipo: &nbsp;' + setCombo('typ', '{tbl:[{0:"Vacaciones",1:"Vacaciones"},{0:"Tiempo libre con cargo a vacaciones",1:"Tiempo libre con cargo a vacaciones"},{0:"Enfermedad",1:"Enfermedad"},'+
        '{0:"Maternidad",1:"Maternidad"},{0:"Paternidad",1:"Paternidad"},{0:"Adopción",1:"Adopci&oacute;n"},{0:"Defunción",1:"Defunci&oacute;n"},{0:"Miembro de jurado",1:"Miembro de jurado"},'+
        '{0:"Beca de estudio",1:"Beca de estudio"},{0:"Servicio Militar",1:"Servicio Militar"}]}', 'nom_permisoBuscar()') +
        '<br />' + setFechas(); */
    _('d_filtro').innerHTML = setFechas();
    _('fi').value = _ANIO_ + '01-01';
    /*if(_MENUS_._enArreglo("permisoGestion")>=0){    
        _('d_filtro').innerHTML += '<br />C&eacute;dula, nombres: <input type="text" id="t_b" onkeypress="var c=(_esN)?event.which:event.keyCode;if(c==13){nom_permisoBuscar();}">';
    }*/
    _('d_filtro').innerHTML += '&nbsp;<input type="button" value="Filtrar" onclick="nom_permisoBuscar();">';
    
    if(_MENUS_._enArreglo("permisoNuevo")>=0){
        btn.icono(5, -228, 34, 34, 'Nuevo Permiso', "nom_permisoEditar('-1');", 'd_herra');
    }
    if(_MENUS_._enArreglo("permisoImprimir")>=0){
        btn.icono(-455, -114, 34, 34, 'Imprimir', "nom_PermisoImp();", 'd_herra');
    }
    nom_permisoBuscar();
    setAnchoPanel(2, 470);
}
function nom_permisoEditar(k)
{
    _('d_ayuda').innerHTML = msg();
    new Tab('d_21', 390, _altBody-310);
    _AJAX.solicitud('frmPermiso', 'id='+k+'&altB='+(_altBody-310), false, 'd_21');
}
function desabilitarCheckbox(nombre,estado){
    _(nombre).disabled=estado;
}
function desabilitarPermiso(nombre,estado1,estado2){
    _(nombre).disabled=estado1;
    _('nDias').disabled=estado2;_('nDias').value=0;
    _('nHoras').disabled=estado2;_('nHoras').value=0;
    _('nMinutos').disabled=estado2;_('nMinutos').value=0;
}
function divVisible(activo,inactivo, inactivo1){
    _(activo).style.display = "block";
    _(inactivo).style.display = "none";
    _(inactivo1).style.display = "none";
}
function verificarPermiso(num){
   if(_('permiso0').checked&&_('id').value=="-1"){
       if(_('fecha_permiso').value==''||((parseInt(_('mInicial').value)+parseInt(_('hInicial').value))<1)){
        alert("Los Campos con * habilitados son necesarios");
        return false;
    }
   }
   if(_('permiso1').checked){
       if(_('fecha_permiso').value==''||(parseInt(_('nDias').value)+parseInt((_('nMinutos').value)+parseInt(_('nHoras').value))<1)||((parseInt(_('mInicial').value)+parseInt(_('hInicial').value))<1)&&_('id').value=="-1"){
        alert("Los Campos con * habilitados son necesarios");
        return false;
    }
   }

    _objTab.seleccionar(num);
    var totalVacaciones = parseInt(_('totalVacaciones').value)-1;
    var diasPedidos = parseInt(_('diasPedidos').value)+parseInt(_('nDias').value);
    var horasPedidos = (parseInt(_('horasPedidos').value))+parseInt(_('nHoras').value);
    var MinutosPedidos = parseInt(_('minutosPedidos').value)+parseInt(_('nMinutos').value);
    if(MinutosPedidos>60){
        horasPedidos=parseInt(horasPedidos)+parseInt(parseInt(MinutosPedidos)/60);
        MinutosPedidos=parseInt(MinutosPedidos)%60;
    }
    if(horasPedidos>7){
        diasPedidos=parseInt(diasPedidos)+parseInt(parseInt(horasPedidos)/7);
        horasPedidos=parseInt(horasPedidos)%7;
    }    
    _('diasSolicitud').innerHTML="Nº Dias de Solicitud de Permiso: "+_('nDias').value+" Dias - "+_('nHoras').value+" Horas - "+_('nMinutos').value+" Minutos";    
    _('totalDiasVacaciones').innerHTML="Total de Dias de Vacaciones (Si es aprobado): "+(parseInt(totalVacaciones)-parseInt(diasPedidos))+" Dias - "+(7-parseInt(horasPedidos))+" Horas - "+(60-parseInt(MinutosPedidos))+" Minutos";
}
function rrecorrerTabla(tabla,columnas){
    var matriz=new Array(columnas);
    for (i = 0; i < _(tabla).rows.length; i++){
        matriz[i]=new Array(columnas);
    } 
    for (var i=0;i<_(tabla).rows.length;i++) {
        for (var j=0;j <_(tabla).rows[i].cells.length-1;j++){
            matriz[i][j]=_(tabla).rows[i].cells[j].innerHTML;
            //alert(_(tabla).rows[i].cells[j].innerHTML);
        }
        //textos = textos + '\n';
    }
    return matriz;    
}
function verificarMatriz(){
    var matr=rrecorrerTabla('tblPE',3);
    alert(matr);
    /*for (var i=0;i<matr.length;i++) {
        for (var j=0;j <3;j++){
            alert(matr[i][j]);
            //alert(_(tabla).rows[i].cells[j].innerHTML);
        }
        //textos = textos + '\n';
    }*/
}
function agregarFila(){
    var t=_('sll').innerHTML;
    var p=_('tblPE').rows.length;
    p++;
    t=t.substring(0,(t.length-8));
    t+="<tr id='rTI"+p+"' valign='top' class='jm_filaPar' style='cursor:pointer' onmouseover=\"this.className='jm_filaSobre'\" onmouseout=\"this.className='jm_filaPar'\">\n\
    <td width='101' style='cursor:pointer'>"+_('fechaRecuperacion').value+"</td>\n\
    <td width='101' style='cursor:pointer'>"+_('hInicialRecuperacion').value+":"+_('mInicialRecuperacion').value+"</td>\n\
    <td width='101' style='cursor:pointer'>"+_('hFinalRecuperacion').value+":"+_('mFinalRecuperacion').value+"</td>\n\
    <td width='21' align='center'><div class='jm_icoCon' style='background-position:-545px -18px;' title='Eliminar' onclick=\"recuperacionEliminar("+p+");\">&nbsp;</td>\n\
    </tr>";
    _('sll').innerHTML=t; 
}
function recuperacionEliminar(k)
{
    document.getElementById('rTI'+k).remove();
}
var dates = {
    convert:function(d) {
        // Converts the date in d to a date-object. The input can be:
        //   a date object: returned without modification
        //  an array      : Interpreted as [year,month,day]. NOTE: month is 0-11.
        //   a number     : Interpreted as number of milliseconds
        //                  since 1 Jan 1970 (a timestamp) 
        //   a string     : Any format supported by the javascript engine, like
        //                  "YYYY/MM/DD", "MM/DD/YYYY", "Jan 31 2009" etc.
        //  an object     : Interpreted as an object with year, month and date
        //                  attributes.  **NOTE** month is 0-11.
        return (
            d.constructor === Date ? d :
            d.constructor === Array ? new Date(d[0],d[1],d[2]) :
            d.constructor === Number ? new Date(d) :
            d.constructor === String ? new Date(d) :
            typeof d === "object" ? new Date(d.year,d.month,d.date) :
            NaN
        );
    },
    compare:function(a,b) {
        // Compare two dates (could be of any type supported by the convert
        // function above) and returns:
        //  -1 : if a < b
        //   0 : if a = b
        //   1 : if a > b
        // NaN : if a or b is an illegal date
        // NOTE: The code inside isFinite does an assignment (=).
        return (
            isFinite(a=this.convert(a).valueOf()) &&
            isFinite(b=this.convert(b).valueOf()) ?
            (a>b)-(a<b) :
            NaN
        );
    },
    inRange:function(d,start,end) {
        // Checks if date in d is between dates in start and end.
        // Returns a boolean or NaN:
        //    true  : if d is between start and end (inclusive)
        //    false : if d is before start or after end
        //    NaN   : if one or more of the dates is illegal.
        // NOTE: The code inside isFinite does an assignment (=).
       return (
            isFinite(d=this.convert(d).valueOf()) &&
            isFinite(start=this.convert(start).valueOf()) &&
            isFinite(end=this.convert(end).valueOf()) ?
            start <= d && d <= end :
            NaN
        );
    }
}
function GuardarPermiso(){    
    var f = new Date();
    var fechaSistema=f.getDate() + "/" + (f.getMonth() +1) + "/" + f.getFullYear();
    var horaSistema=f.getHours()+":"+f.getMinutes();
    if(_('id').value!='-1'){
        _objTab.seleccionar(3);
        return false;
    }
    var matr=rrecorrerTabla('tblPE',3);
    if(_('recuperacion1').checked&&_('tblPE').rows.length==0){
        alert("Debe de añadir dias de recuperacion");
        return false;
    }
    var id=_('id').value;
    var id_empleado=_('id_empleado').value;
    var id_tipo=_('id_tipo').value;
    var nDias=_('nDias').value;
    var nHoras=_('nHoras').value;
    var nMinutos=_('nMinutos').value;
    var fecha_permiso=_('fecha_permiso').value;
    var recuperac="0";
    var hInicial=_('hInicial').value+":"+_('mInicial').value;
    var descripcion=_('descripcion').value;
    
    var fInicial=SelectorFecha.getTimestamp(fecha_permiso,hInicial);
    var fActual=SelectorFecha.getTimestamp(fechaSistema, horaSistema);
    if( fInicial <= fActual){
        alert("La fecha y Hora debe de ser mayor a la actual");
        return false;
    }
    //alert(Date.parse(fecha_permiso+" "+hInicial));
    //alert(fechaSistema);
    /*if((fecha_permiso+" "+hInicial)<fechaSistema){
        alert("La fecha y Hora debe de ser mayor a la actual");
        return false;
    }*/
    
    for(var i=0; i<document.formPermisos.recuperacion.length;i++){
        if (document.formPermisos.recuperacion[i].checked){
            recuperac=i+1;
        }
    }
    confirmar=confirm("Para adjuntar el archivo usted necesita Guardar la informacion:\n\
VERIFIQUE LA INFORMACION PARA CONTINUAR?"); 
    if (confirmar){
        _AJAX.solicitud('frmPermisoGuardar', 'id='+id+'&id_tipo='+id_tipo+'&nDias='+nDias+'&nHoras='+nHoras+'&nMinutos='+nMinutos+'\
&fecha_permiso='+fecha_permiso+'&recuperac='+recuperac+'&hInicial='+hInicial+'&descripcion='+descripcion+'&id_empleado='+id_empleado+'&matriz='+matr+'&filas='+_('tblPE').rows.length, false, 'body');
        _objTab.seleccionar(3);
    } 
}
function envioId(id, valor){
    _(id).value=valor;
}
function nom_permisoBuscar(){
    var w="where alias = '"+_USR_+"' order by fecha_pedido desc";
    if(_('fi').value!=_('ff').value){
        w = "where alias = '"+_USR_+"' and fechainicio between '"+_('fi').value+"' and '"+_('ff').value+"' order by fecha_pedido desc";
    }
    _WR = encodeURI(w);
    //new Tabla('d_11', 'jmTbl', '', 'vta_empleado', 'DEPARTAMENTO,CARGO,IDENTI.,EMPLEADO', 'id_empleado,departamento,rol,dni,empleado', '0,110,150,120,250', _altEdi, 'nom_empleadoEditar(^);', _WR);
    new Tabla('d_11', 'jmTbl', '', 'vta_empleado_permiso', 'EMPLEADO,F. RECUPERACION,FECHA PEDIDO,DESCRIPCION,ESTADO,OBSERVACION', 'id_permiso,nombre,formarecuperacion,fecha_pedido,descripcion,estadoPermiso,observaciones', '0,150,120,100,150,70,150', _altEdi, 'nom_permisoEditar(^);', _WR);
}
function nom_PermisoImp()
{
    //Ventana.crear('vta_im', 'Impresi&oacute;n de Empleados', "ancho=250,alto=120,modal=true,cerrar=true", h);
}

/* asistencias */
function nom_asistencia()
{
    encerar();
    _('d_filtro').innerHTML = 'C&eacute;dula, nombres: <input type="text" id="t_b" onkeypress="var c=(_esN)?event.which:event.keyCode;if(c==13){nom_buscarAsistencia();}">'+
         '<br />' + setFechas() +
        '&nbsp;<input type="button" value="Filtrar" onclick="nom_buscarAsistencia();">';
    if(_MENUS_._enArreglo("docBancoSubir")>=0){
        btn.icono(-209, -156, 34, 34, 'Cargar archivo de control de asistencias del dispositivo biom&eacute;trico', "nom_setArchivoBiometrico()", 'd_herra');
    }
    nom_buscarAsistencia();
    setAnchoPanel(2, 470);
}
function nom_buscarAsistencia()
{
    var w = "where dia between '"+_('fi').value+"' and '"+_('ff').value+"'";
    if(_('t_b')!=null){
        var a=_('t_b').value;
        if(a!=''){
            w = " and (lower(dni) like '"+a.toLowerCase()+"%' or lower(empleado) like '%"+a.toLowerCase()+"%')";
        }
    }
    _WR = encodeURI(w);
    new Tabla('d_11', 'jmTbl', '', 'vta_empleado_asistencia', 'DEPARTAMENTO,CARGO,EMPLEADO,HORARIO,DIA,H.ENT.,H. SAL.,MAR. ENT.,MAR. SAL.,NORMAL,# DIAS,FIN SEMANA,FERIADO,ASISTENCIA', 'id_empleado,departamento,rol,empleado,horario,dia,hora_ent,hora_sal,marc_ent,marc_sal,normal,n_dias,fin_semana,feriado,tiem_asist', '0,110,180,250,100,70,60,60,60,60,50,50,50,50,50', _altEdi, '', _WR);
}
/*Tipo de Permisos*/
function pyl_tipoPermisos()
{
    encerar();
    _('d_filtro').innerHTML = 'Tipo de Permiso: <input type="text" id="t_b" onkeypress="var c=(_esN)?event.which:event.keyCode;if(c==13){pyl_tipoPermisosBuscar();}">'+
        '&nbsp;<input type="button" value="Filtrar" onclick="pyl_tipoPermisosBuscar();">';
    if(_MENUS_._enArreglo("CREAR TIPO PERMISOS")>=0){
        btn.icono(-360, 2, 34, 34, 'Nuevo Cargo', "pyl_tipoPermisosEditar('-1');", 'd_herra');
    }
    if(_MENUS_._enArreglo("ELIMINAR TIPO PERMISOS")>=0){
        btn.icono(-391, 3, 34, 34, 'Eliminar', "_objT.eliminar();", 'd_herra');
    }
    btn.icono(-455, -114, 34, 34, 'Imprimir', "nom_impCargos();", 'd_herra');
    pyl_tipoPermisosBuscar();
    setAnchoPanel(2, 460);
}
function pyl_tipoPermisosBuscar()
{
    var w='where';
    if(_('t_b')!=null){
        var a=_('t_b').value;
        if(a!=''){
            w += " lower(nombre) like '%"+a.toLowerCase()+"%' ";
        }
    }
    if(w == 'where'){
        w = '';
    }

    _WR = encodeURI(w);
    new Tabla('d_11', 'jmTbl', '', 'vta_tipoPermiso', 'NOMBRE,DESCRIPCION,DIAS,ESTADO', 'id_tipo,nombre,descripcion,numdias,estado', '0,150,150,70,70', _altEdi, 'pyl_tipoPermisosEditar(^);', _WR);
}
function pyl_tipoPermisosEditar(k)
{
    //_('d_ayuda').innerHTML = msg();
    _AJAX.solicitud('frmTipoPermiso', 'id='+k+'&altB='+(_altBody-380), false, 'd_21');
}
function pyl_tipoPermisosGuardar(f)
{
    if(f.nombre.value==''){
        alert('Por favor, ingrese el nombre del cargo');
        return false;
    }
    return _AJAX.enviarForm(f);
}

/*Dias Vacaciones*/
function pyl_diasVacacionesBuscar()
{
    var w='where';
    if(_('t_b')!=null){
        var a=_('t_b').value;
        if(a!=''){
            w += " lower(empleado) like '%"+a.toLowerCase()+"%' order by id_sucursal,empleado";
        }
    }
    if(w == 'where'){
        w = 'order by id_sucursal,empleado ';
    }

    _WR = encodeURI(w);
    new Tabla('d_11', 'jmTbl', '', 'vta_vacaciones_total', 'SUCURSAL,EMPLEADO,FECHA INGRESO,TOTAL VACACIONES, INGRESADOS AL SISTEMA', 'id_empleado,sucursal,empleado,fecha_ingreso,total_vac,diasvacaciones', '0,50,250,100,100,100', _altEdi, 'pyl_tipoPermisosEditar(^);', _WR);
}

function pyl_diasVacaciones()
{
    encerar();
    _AJAX.solicitud('frmFiltro', 'op=33&fn=pyl_diasVacacionesBuscar()', false, 'd_filtro');
    btn.icono(-455, -114, 34, 34, 'Imprimir', "nom_impEmpleados();", 'd_herra');
    pyl_diasVacacionesBuscar();
    setAnchoPanel(2, 570);
}
function pyl_diasVacacionesBuscar()
{
    var w = 'where';
    if(_('idSuc').value!='-0'){
        w += w=='where' ? " id_sucursal="+_('idSuc').value+"" : " and id_sucursal="+_('idSuc').value+"";
    }
    if(_('id_area').value!='-0'){
        w += w=='where' ? " id_area="+_('id_area').value+"" : " and id_area="+_('id_area').value+"";
    }
     if(_('id_cargo').value!='-0'){
        w += w=='where' ? " id_cargo="+_('id_cargo').value+"" : " and id_cargo="+_('id_cargo').value+"";
    }
    var a=_('t_b').value;
    if(a!=''){
        w += w=='where' ? " lower(dni) like '"+a.toLowerCase()+"%' or lower(empleado) like '%"+a.toLowerCase()+"%'" : " and (lower(dni) like '"+a.toLowerCase()+"%' or lower(empleado) like '%"+a.toLowerCase()+"%')";
    }
    if(w == 'where'){
        w = '';
    }
    _WR = encodeURI(w);
    new Tabla('d_11', 'jmTbl', '', 'vta_empleado', 'DEPARTAMENTO,CARGO,IDENTI.,EMPLEADO', 'id_empleado,departamento,rol,dni,empleado', '0,110,150,120,250', _altEdi, 'pyl_diasVacacionesEditar(^);', _WR);
}
function pyl_diasVacacionesEditar(k)
{
    _('d_ayuda').innerHTML = msg() + '.<br />La fecha de ingreso ser&aacute; tomada como referencia para el pago de las vacaciones anuales.';
    new Tab('d_21', 390, _altBody-310);
    _AJAX.solicitud('frmDiasVacaciones', 'id='+k+'&altB='+(_altBody-310), false, 'd_21');
}
/*Administracion de Permisos*/
function pyl_admPermiso()
{
    encerar();
    _('d_filtro').innerHTML = "<td width='80'>Tipo de Permiso<select id='estado' onchange='pyl_admPermisoBuscar();'><option value='-0'>Todos</option><option value='a'>Aprobados</option>\n\
        <option value='s'>Solicitados</option><option value='r'>Rechazados</option></select></td><br>"+
    'Tipo de Permiso: <input type="text" id="t_b" onkeypress="var c=(_esN)?event.which:event.keyCode;if(c==13){pyl_admPermisoBuscar();}">'+
        '&nbsp;<input type="button" value="Filtrar" onclick="pyl_admPermisoBuscar();">';
    btn.icono(-455, -114, 34, 34, 'Imprimir', "nom_impEmpleados();", 'd_herra');
    pyl_admPermisoBuscar();
    setAnchoPanel(2, 500);
}
function pyl_admPermisoBuscar()
{
    var w = 'where';
    if(_('estado').value!='-0'){
        w += w=='where' ? " permisoestado='"+_('estado').value+"'" : " and permisoestado='"+_('estado').value+"'";
    }
    var a=_('t_b').value;
    if(a!=''){
        w += w=='where' ? " lower(dni) like '"+a.toLowerCase()+"%' or lower(empleado) like '%"+a.toLowerCase()+"%'" : " and (lower(dni) like '"+a.toLowerCase()+"%' or lower(empleado) like '%"+a.toLowerCase()+"%')";
    }
    if(w == 'where'){
        w = '';
    }
    _WR = encodeURI(w);
    new Tabla('d_11', 'jmTbl', '', 'vta_empleado_permiso', 'CARGO,EMPLEADO,TIPO,F.RECUPER.,F.PEDIDO', 'id_permiso,rol,empleado,nombretp,formarecuperacion,fecha_pedido', '0,160,200,100,100,100', _altEdi, 'pyl_admPermisoEditar(^);', _WR+" order by fecha_pedido desc");
}
function pyl_admPermisoEditar(k)
{
    _('d_ayuda').innerHTML = msg() + '.<br />La fecha de ingreso ser&aacute; tomada como referencia para el pago de las vacaciones anuales.';
    new Tab('d_21', 300, _altBody-310);
    _AJAX.solicitud('frmPermisoAdm', 'id='+k+'&altB='+(_altBody-310), false, 'd_21');
}
function pyl_admPermisoGuardar(f)
{
    if(f.permiso.value=='r'&&f.descripcion.value==''){
        alert('Por favor, ingrese el motivo del rechazo');
        return false;
    }
    return _AJAX.enviarForm(f);
}

/*Vacaciones*/
function pyl_vacaciones()
{
    encerar();
    if(_MENUS_._enArreglo("ACCESO A VACACIONES CLIENTE")>=0){
        btn.icono(-360, 2, 34, 34, 'Pedir Vacaciones', "pyl_VacacionesEditar('-1');", 'd_herra');
    }
    btn.icono(-455, -114, 34, 34, 'Imprimir', "nom_impCargos();", 'd_herra');
    pyl_vacacionesBuscar();
    setAnchoPanel(2, 460);
}
function pyl_vacacionesBuscar()
{
    var w="where alias = '"+_USR_+"' order by fecha_pedido desc";
    _WR = encodeURI(w);
    new Tabla('d_11', 'jmTbl', '', 'vta_empleado_vacaciones', 'F. PEDIDO,DIAS,ESTADO,DESCRIPCION,F.RESPUESTA,USUARIO', 'id_empleado_vacaciones,fecha_pedido,numdias,estado_solicitud,observacion,fecha_respuesta,usuario_respuesta', '0,100,70,100,160,100,70', _altEdi, 'pyl_VacacionesEditar(^);',_WR);
}
function pyl_VacacionesEditar(k)
{
    //_('d_ayuda').innerHTML = msg();
    new Tab('d_21', 300, _altBody-310);
    _AJAX.solicitud('frmVacaciones', 'id='+k+'&altB='+(_altBody-380), false, 'd_21');
}
function pyl_VacacionesGuardar(f)
{
    var t = new Date();
    var fechaSistema=t.getDate() + "/" + (t.getMonth()+1) + "/" + t.getFullYear();
    
    var fInicial=SelectorFecha.getTimestamp(f.fecha_permiso.value);
    var fActual=SelectorFecha.getTimestamp(fechaSistema)+172800000;
    if( fInicial <= fActual){
        alert("LA SOLICITUD PARA EL PERMISO DE VACACIONES DEBE DE SER MINIMO 2 DIAS DE ANTICIPACIÓN");
        return false;
    }
    if(f.fecha_permiso.value==''||f.nDias.value==''){
        alert('Por favor, revise los campos que esten bien ingresados');
        return false;
    }
    return _AJAX.enviarForm(f);
}

/*Administracion de Vacaciones*/
function pyl_admVacaciones()
{
    encerar();
    _('d_filtro').innerHTML = "<td width='80'>Tipo de Vacaciones<select id='estado' onchange='pyl_admVacacionesBuscar();'><option value='-0'>Todos</option><option value='a'>Aprobados</option>\n\
        <option value='s'>Solicitados</option><option value='r'>Rechazados</option></select></td><br>"+
    'Tipo de Permiso: <input type="text" id="t_b" onkeypress="var c=(_esN)?event.which:event.keyCode;if(c==13){pyl_admVacacionesBuscar();}">'+
        '&nbsp;<input type="button" value="Filtrar" onclick="pyl_admVacacionesBuscar();">';
    btn.icono(-455, -114, 34, 34, 'Imprimir', "nom_impEmpleados();", 'd_herra');
    pyl_admVacacionesBuscar();
    setAnchoPanel(2, 500);
}
function pyl_admVacacionesBuscar()
{
    var w = 'where';
    if(_('estado').value!='-0'){
        w += w=='where' ? " estado='"+_('estado').value+"'" : " and estado='"+_('estado').value+"'";
    }
    var a=_('t_b').value;
    if(a!=''){
        w += w=='where' ? " lower(dni) like '"+a.toLowerCase()+"%' or lower(empleado) like '%"+a.toLowerCase()+"%'" : " and (lower(dni) like '"+a.toLowerCase()+"%' or lower(empleado) like '%"+a.toLowerCase()+"%')";
    }
    if(w == 'where'){
        w = '';
    }
    _WR = encodeURI(w);
    new Tabla('d_11', 'jmTbl', '', 'vta_empleado_vacaciones', 'CARGO,EMPLEADO,F.PEDIDO,F.INICIO,DIAS,ESTADO', 'id_empleado_vacaciones,rol,empleado,fecha_pedido,fecha_inicio,numdias,estado_solicitud', '0,160,200,100,100,100,100', _altEdi, 'pyl_admVacacionesEditar(^);', _WR+" order by fecha_pedido desc");
}
function pyl_admVacacionesEditar(k)
{
    _('d_ayuda').innerHTML = msg() + '.<br />La fecha de ingreso ser&aacute; tomada como referencia para el pago de las vacaciones anuales.';
    new Tab('d_21', 300, _altBody-310);
    _AJAX.solicitud('frmVacacionesAdm', 'id='+k+'&altB='+(_altBody-310), false, 'd_21');
}
function pyl_admVacacionesGuardar(f)
{
    if(f.permiso.value=='r'&&f.descripcion.value==''){
        alert('Por favor, ingrese el motivo del rechazo');
        return false;
    }
    return _AJAX.enviarForm(f);
}