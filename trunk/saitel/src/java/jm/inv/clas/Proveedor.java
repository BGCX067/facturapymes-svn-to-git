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

package jm.inv.clas;
import java.sql.ResultSet;
import jm.web.DataBase;

/**
 *
 * @author Jorge
 */
public class Proveedor extends DataBase{
    public Proveedor(String m, int p, String db, String u, String c){
        super(m, p, db, u, c);
    }
    public ResultSet getProveedores()
    {
        return this.consulta("SELECT id_proveedor,razon_social FROM vta_proveedor");
    }
    public ResultSet getProveedores(String txt)
    {
        txt = txt.toLowerCase();
        return this.consulta("SELECT id_proveedor, substr(serie_factura, 1, 3) || '-' || ruc,razon_social,serie_factura,aut_factura,toDateSQL(fecha_cad_factura) " +
                "FROM vta_proveedor WHERE lower(ruc) like '"+txt+"%' or lower(razon_social) like '%"+txt+"%' order by razon_social limit 10 offset 0;");
    }
    public ResultSet getProveedor(String id)
    {
        return this.consulta("SELECT * FROM tbl_proveedor where id_proveedor="+id);
    }
    public String getId(String id)
    {
        ResultSet rs = this.consulta("SELECT id_proveedor FROM tbl_proveedor where ruc='"+id+"'");
        String id_proveedor = "-0";
        try{
            if(rs.next()){
                id_proveedor = rs.getString("id_proveedor")!=null ? rs.getString("id_proveedor") : "-0";
                rs.close();
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return id_proveedor;
    }
    public String getNombre(String id)
    {
        ResultSet rs = this.consulta("SELECT * FROM vta_proveedor where id_proveedor="+id);
        String nombre = "";
        try{
            if(rs.next()){
                nombre = rs.getString("razon_social")!=null ? rs.getString("razon_social") : "";
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return nombre;
    }
    public String getCxP(String id)
    {
        ResultSet rs = this.consulta("SELECT * FROM tbl_proveedor where id_proveedor="+id);
        String id_plan_cuenta = "";
        try{
            if(rs.next()){
                id_plan_cuenta = rs.getString("id_plan_cuenta")!=null ? rs.getString("id_plan_cuenta") : "";
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return id_plan_cuenta;
    }
    public String getProveedorJSON()
    {
        ResultSet rs = this.consulta("SELECT id_proveedor,ruc,serie_factura,aut_factura,toDateSQL(fecha_cad_factura),direccion,ciudad,telefono,email,contacto " +
                "FROM vta_proveedor order by id_proveedor;");
        String tbl = this.getJSON(rs);
        try{
            rs.close();
        }catch(Exception e){
            e.printStackTrace();
        }
        return tbl;
    }
    public boolean estaDuplicado(String id, String serie, String ruc)
    {
        try{
            String establecimiento[] = serie.split("-");
            ResultSet res = this.consulta("SELECT * FROM tbl_proveedor where substr(serie_factura, 1, 3)='"+establecimiento[0]+"' and ruc='"+ruc+"' and id_proveedor<>"+id);
            if(this.getFilas(res)>0){
                return true;
            }
            res.close();
        }catch(Exception e){
            e.printStackTrace();
        }
        return false;
    }
    public boolean insertar(int id_sucursal, String tipo_documento, String ruc, String raz, String id_plan_cuenta, String auF, String seF, String caF,
            String dir, String ciu, String pai, String tel, String fax, String mail, String web, String con, String obs)
    {
        caF = caF.compareTo("")!=0 ? "'"+caF+"'" : "NULL";
        return this.ejecutar("INSERT INTO tbl_proveedor(id_sucursal, tipo_documento, ruc, razon_social, id_plan_cuenta, aut_factura, serie_factura, fecha_cad_factura, " +
                "direccion, ciudad, pais, telefono, fax, email, web, contacto, observacion) " +
                "VALUES("+id_sucursal+", '"+tipo_documento+"', '"+ruc+"', '"+raz+"', "+id_plan_cuenta+", '"+auF+"', '"+seF+"', "+caF+", " +
                "'"+dir+"', '"+ciu+"', '"+pai+"', '"+tel+"', '"+fax+"', '"+mail+"', '"+web+"', '"+con+"', '"+obs+"');");
    }
    public boolean actualizar(String id, String tipo_documento, String ruc, String raz, String id_plan_cuenta, String auF, String seF, String caF,
            String dir, String ciu, String pai, String tel, String fax, String mail, String web, String con, String obs)
    {
        caF = caF.compareTo("")!=0 ? "'"+caF+"'" : "NULL";
        return this.ejecutar("UPDATE tbl_proveedor SET tipo_documento='"+tipo_documento+"', ruc='"+ruc+"', razon_social='"+raz+"', id_plan_cuenta='"+id_plan_cuenta+
                "', aut_factura='"+auF+"', serie_factura='"+seF+"', " +
                "fecha_cad_factura="+caF+", direccion='"+dir+"', ciudad='"+ciu+"', pais='"+pai+"', telefono='"+tel+"', fax='"+fax+"', " +
                "email='"+mail+"', web='"+web+"', contacto='"+con+"', observacion='"+obs+"' WHERE id_proveedor="+id+";");
    }
    public boolean actualizar(String id, String auF, String seF, String caF)
    {
        caF = caF.compareTo("")!=0 ? "'"+caF+"'" : "NULL";
        return this.ejecutar("UPDATE tbl_proveedor SET aut_factura='"+auF+"', serie_factura='"+seF+"', " +
                "fecha_cad_factura="+caF+" WHERE id_proveedor="+id+";");
    }

}