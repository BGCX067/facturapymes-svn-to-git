/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jm.fac.clas;

import jm.web.Addons;
import jm.web.Xml;
import org.w3c.dom.Element;

/**
 *
 * @author desarrollo
 */
public class NotaCreditoElectronica extends Xml{
    public NotaCreditoElectronica()
    {
        super();
        this.crear();
    }
    
    public String generarXml(String claveAcceso, String ambiente, String tipoEmision, String razonSocial, String nombreComercial, String ruc, 
            String codDoc, String estab, String ptoEmi, String secuencial, String dirMatriz, String fechaEmision, String dirEstablecimiento, 
            String tipoIdentificacionComprador, String razonSocialComprador, String identificacionComprador, String contribuyenteEspecial, 
            String obligadoContabilidad, String codDocModificado, String numDocModificado, String fechaEmisionDocSustento, 
            String totalSinImpuestos, String valorModificacion, String subtotal_0, String subtotal_2, String iva_2, String motivo, 
            String codigos, String descripciones, String cantidades, String preciosUnitarios, String descuentos, String subtotales, String ivas)
    {
        //String claveAcceso = this.getClaveAcceso(fechaEmision, ruc, "1", estab+ptoEmi, secuencial, "1");
        Element notaCredito = this.setInfoTributaria("notaCredito", ambiente, tipoEmision, razonSocial, nombreComercial, ruc, claveAcceso, 
                                                    codDoc, estab, ptoEmi, secuencial, dirMatriz);
        
        this.nuevoElemento(notaCredito, "infoNotaCredito", false);
        Element infoNotaCredito = this.getElementoActivo();
            this.nuevoElementoInsertar("fechaEmision", fechaEmision);
            this.nuevoElementoInsertar("dirEstablecimiento", dirEstablecimiento);
            this.nuevoElementoInsertar("tipoIdentificacionComprador", tipoIdentificacionComprador);
            this.nuevoElementoInsertar("razonSocialComprador", razonSocialComprador);
            this.nuevoElementoInsertar("identificacionComprador", identificacionComprador);
            this.nuevoElementoInsertar("contribuyenteEspecial", contribuyenteEspecial);
            this.nuevoElementoInsertar("obligadoContabilidad", obligadoContabilidad);
            //this.nuevoElementoInsertar("rise", rise);
            this.nuevoElementoInsertar("codDocModificado", codDocModificado);
            this.nuevoElementoInsertar("numDocModificado", numDocModificado);
            this.nuevoElementoInsertar("fechaEmisionDocSustento", fechaEmisionDocSustento);
            this.nuevoElementoInsertar("totalSinImpuestos", Addons.truncar(totalSinImpuestos) );
            this.nuevoElementoInsertar("valorModificacion", Addons.truncar(valorModificacion) );
            this.nuevoElementoInsertar("moneda", "DOLAR" );
        
            this.nuevoElemento(infoNotaCredito, "totalConImpuestos", false);
                this.nuevoElementoInsertar("totalImpuesto");
                    this.nuevoElementoInsertar("codigo", "2");
                    if(Float.parseFloat(subtotal_0)>0 ){
                        this.nuevoElementoInsertar("codigoPorcentaje", "0");
                        this.nuevoElementoInsertar("baseImponible", Addons.truncar(subtotal_0) );
                        this.nuevoElementoInsertar("valor", "0.00");
                    }
                    if(Float.parseFloat(subtotal_2)>0 ){
                        this.nuevoElementoInsertar("codigoPorcentaje", "2");
                        this.nuevoElementoInsertar("baseImponible", Addons.truncar(subtotal_2) );
                        this.nuevoElementoInsertar("valor", Addons.truncar(iva_2) );
                    }
            
        this.setElementoActivo(infoNotaCredito);
            this.nuevoElementoInsertar("motivo", motivo);
            //this.nuevoElementoInsertar("importeTotal", Addons.truncar(importeTotal) );
            //this.nuevoElementoInsertar("moneda", "DOLAR");
            
                
        this.nuevoElemento(notaCredito, "detalles", false);
        Element detalles = this.getElementoActivo();
        
        String vec_codigo[] = codigos.split(",");
        String vec_descripcion[] = descripciones.split(",");
        String vec_cantidad[] = cantidades.split(",");
        String vec_precioUnitario[] = preciosUnitarios.split(",");
        String vec_descuento[] = descuentos.split(",");
        String vec_subtotal[] = subtotales.split(",");
        String vec_iva[] = ivas.split(",");
        for(int i=0; i<vec_codigo.length;i++){
            this.nuevoElemento(detalles, "detalle", false);
            //this.nuevoElementoInsertar("codigoInterno", vec_codigo[i]);
            this.nuevoElementoInsertar("descripcion", vec_descripcion[i]);
            this.nuevoElementoInsertar("cantidad", Addons.truncar(vec_cantidad[i]) );
            this.nuevoElementoInsertar("precioUnitario", Addons.truncar(vec_precioUnitario[i]) );
            this.nuevoElementoInsertar("descuento", Addons.truncar(vec_descuento[i]) );
            this.nuevoElementoInsertar("precioTotalSinImpuesto", Addons.truncar(vec_subtotal[i]) );
            this.nuevoElementoInsertar("impuestos");
                this.nuevoElementoInsertar("impuesto");
                    this.nuevoElementoInsertar("codigo", "2");
                    this.nuevoElementoInsertar("codigoPorcentaje", ( Float.parseFloat(vec_iva[i])>0 ? "2" : "0" ) );
                    this.nuevoElementoInsertar("tarifa", ( Float.parseFloat(vec_iva[i])>0 ? "12" : "0" ) );
                    this.nuevoElementoInsertar("baseImponible", Addons.truncar(Float.parseFloat(vec_subtotal[i]) - Float.parseFloat(vec_descuento[i])));
                    this.nuevoElementoInsertar("valor", Addons.truncar(vec_iva[i]) );
        }
        
        this.nuevoElemento(notaCredito, "infoAdicional", false);
        Element infoAdicional = this.getElementoActivo();
        this.nuevoElemento(infoAdicional, "campoAdicional", "info@saitel.ec", false);
        this.setAtributo("nombre", "EMAIL");
        
        return this.getXml();
    }
    
    public String getClaveAcceso(String fecha, String tipoComprobante, String ruc, String tipoAmb, String serie, String secuencial, String tipoEmis)
    {
        String axFecha = "";
        if(fecha.indexOf("/")>0){
            axFecha = fecha.replace("/", "");
        }else{
            String vec[] = fecha.split("-");
            axFecha = vec[2]+vec[1]+vec[0];
        }
        String clave = axFecha+tipoComprobante+ruc+tipoAmb+serie+secuencial+"12345678"+tipoEmis;
        clave += this.getDigitoVerificador(clave);
        return clave;
    }
    
    public int getDigitoVerificador(String digitos)
    {
        int digito=0;
        int x=2;
        char vec[] = digitos.toCharArray();
        //int mul[] = new int[vec.length];
        int suma = 0;
        for(int i=47; i>=0; i--){
            if(x==8){
                x=2;
            }
            //mul[i] = vec[i] * x;
            suma += (Integer.parseInt(""+vec[i]) * x);
            x++;
        }
        int mod = suma % 11;
        digito = 11 - mod;
        if(digito==11){
            digito = 0;
        }
        if(digito==10){
            digito = 1;
        }
        return digito;
    }
    
    public boolean guardar(String nombreArchivo)
    {
        return this.salvar(nombreArchivo);
    }
    
}
