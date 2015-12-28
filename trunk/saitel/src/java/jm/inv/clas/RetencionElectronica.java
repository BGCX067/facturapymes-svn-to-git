/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jm.inv.clas;

import jm.web.Xml;
import org.w3c.dom.Element;

/**
 *
 * @author desarrollo
 */
public class RetencionElectronica extends Xml{
    public RetencionElectronica()
    {
        super();
        //this.crear();
    }
    
    public String generarXml(String claveAcceso, String ambiente, String tipoEmision, String razonSocial, String nombreComercial, String ruc, 
            String codDoc, String estab, String ptoEmi, String secuencial, String dirMatriz, String fechaEmision, String dirEstablecimiento, 
            String contribuyenteEspecial, String obligadoContabilidad, String tipoIdentificacionSujetoRetenido, 
            String razonSocialSujetoRetenido, String identificacionSujetoRetenido, String periodoFiscal, 
            String codigos, String codsRetencion, String basesImponibles, String porcentajesRet, String valoresRet, 
            String sustentos, String numDocSustento)
    {
        this.crear();
        //String claveAcceso = this.getClaveAcceso(fechaEmision, ruc, "1", estab+ptoEmi, secuencial, "1");
        Element comprobanteRetencion = this.setInfoTributaria("comprobanteRetencion", ambiente, tipoEmision, razonSocial, nombreComercial, ruc, claveAcceso, 
                                                    codDoc, estab, ptoEmi, secuencial, dirMatriz);
        
        this.nuevoElemento(comprobanteRetencion, "infoCompRetencion", false);
        this.nuevoElementoInsertar("fechaEmision", fechaEmision);
        this.nuevoElementoInsertar("dirEstablecimiento", dirEstablecimiento);
        this.nuevoElementoInsertar("contribuyenteEspecial", contribuyenteEspecial);
        this.nuevoElementoInsertar("obligadoContabilidad", obligadoContabilidad);
        this.nuevoElementoInsertar("tipoIdentificacionSujetoRetenido", tipoIdentificacionSujetoRetenido);
        this.nuevoElementoInsertar("razonSocialSujetoRetenido", razonSocialSujetoRetenido);
        this.nuevoElementoInsertar("identificacionSujetoRetenido", identificacionSujetoRetenido);
        this.nuevoElementoInsertar("periodoFiscal", periodoFiscal);
        
        this.nuevoElemento(comprobanteRetencion, "impuestos", false);
        Element impuestos = this.getElementoActivo();
        
        //String vec_codigos[] = codigos.split(",");
        String vec_codsRetencion[] = codsRetencion.split(",");
        String vec_basesImponibles[] = basesImponibles.split(",");
        String vec_porcentajesRet[] = porcentajesRet.split(",");
        String vec_valoresRet[] = valoresRet.split(",");
        String vec_sustentos[] = sustentos.split(",");
        for(int i=0; i<vec_codsRetencion.length;i++){
            this.nuevoElemento(impuestos, "impuesto", false);
            this.nuevoElementoInsertar("codigo", (vec_codsRetencion[i].compareTo("1")==0 || vec_codsRetencion[i].compareTo("2")==0 || vec_codsRetencion[i].compareTo("3")==0 || vec_codsRetencion[i].compareTo("9")==0 || vec_codsRetencion[i].compareTo("10")==0 ? "2" : (vec_codsRetencion[i].compareTo("4580")==0 ? "6" : "1") )  );
            this.nuevoElementoInsertar("codigoRetencion", vec_codsRetencion[i]);
            this.nuevoElementoInsertar("baseImponible", vec_basesImponibles[i]);
            this.nuevoElementoInsertar("porcentajeRetener", vec_porcentajesRet[i]);
            this.nuevoElementoInsertar("valorRetenido", vec_valoresRet[i]);
            this.nuevoElementoInsertar("codDocSustento", vec_sustentos[i]);
            this.nuevoElementoInsertar("numDocSustento", numDocSustento);
        }
        
        this.nuevoElemento(comprobanteRetencion, "infoAdicional", false);
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
