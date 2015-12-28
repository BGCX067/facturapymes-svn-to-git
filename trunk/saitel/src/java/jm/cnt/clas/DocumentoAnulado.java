/**
** @version 1.0
** @package FACTURAPYMES.
* @author Jorge Washington Mueses Cevallos.
* @copyright Copyright (C) 2011 por Jorge Mueses. Todos los derechos reservados.
* @license http://www.gnu.org/copyleft/gpl.html GNU/GPL.
** FACTURAPYMES es un software de libre distribución, que puede ser
* copiado y distribuido bajo los términos de la Licencia
* Attribution-NonCommercial-NoDerivs 3.0 Unported,
* de acuerdo con la publicada por la CREATIVE COMMONS CORPORATION.
*/

package jm.cnt.clas;
import java.sql.ResultSet;
import jm.web.DataBase;

public class DocumentoAnulado extends DataBase{
    public DocumentoAnulado(String m, int p, String db, String u, String c){
        super(m, p, db, u, c);
    }
    public ResultSet getDocumentoAnulado(String id)
    {
        return this.consulta("SELECT * FROM tbl_documento_anulado WHERE id_documento_anulado="+id);
    }
    public boolean estaDuplicado(String id, String tipo_comprobante, String serie, String secuencial, String autorizacion)
    {
        ResultSet res = this.consulta("SELECT * FROM tbl_documento_anulado where serie='"+serie+"' and secuencial="+secuencial+
                " and autorizacion='"+autorizacion+"' and tipo_comprobante="+tipo_comprobante+" and id_documento_anulado<>"+id);
        if(this.getFilas(res)>0){
            return true;
        }
        try{
            res.close();
        }catch(Exception e){
            e.printStackTrace();
        }
        return false;
    }
    public boolean insertar(String fecha_emision, String tipo_comprobante, String serie, String secuencial, String autorizacion)
    {
        return this.ejecutar("INSERT INTO tbl_documento_anulado(fecha_emision, tipo_comprobante, serie, secuencial, autorizacion) VALUES"
                + "('"+fecha_emision+"', "+tipo_comprobante+", '"+serie+"', "+secuencial+", '"+autorizacion+"')");
    }
    public boolean actualizar(String id, String fecha_emision, String tipo_comprobante, String serie, String secuencial, String autorizacion)
    {
        return this.ejecutar("UPDATE tbl_documento_anulado SET fecha_emision='"+fecha_emision+"', tipo_comprobante="+tipo_comprobante+
                ", serie='"+serie+"', secuencial="+secuencial+", autorizacion='"+autorizacion+"' WHERE id_documento_anulado="+id);
    }
    public boolean eliminar(String ids)
    {
        return this.ejecutar("delete from  tbl_documento_anulado WHERE id_documento_anulado in ("+ids+")");
    }
}