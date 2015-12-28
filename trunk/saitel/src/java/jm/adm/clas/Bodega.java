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

package jm.adm.clas;
import java.sql.ResultSet;
import jm.web.DataBase;

public class Bodega extends DataBase{
    public Bodega(String m, int p, String db, String u, String c){
        super(m, p, db, u, c);
    }
    public ResultSet getBodega(String id)
    {
        return this.consulta("SELECT * from tbl_bodega WHERE id_bodega="+id);
    }
    public ResultSet getBodegas()
    {
        return this.consulta("SELECT id_bodega, bodega from vta_bodega");
    }
    public ResultSet getBodegasPersonalizacion(String txt)
    {
        return this.consulta("SELECT id_bodega, bodega, E.dni, E.empleado from vta_bodega as B inner join vta_empleado as E on B.id_responsable=E.id_empleado "
                + "where lower(bodega) like '%"+txt+"%' or lower(E.dni) like '"+txt+"%' or lower(E.empleado) like '%"+txt+"%' order by bodega limit 10 offset 0");
    }
    public ResultSet getBodegas(int id_sucursal)
    {
        return this.consulta("SELECT id_bodega, bodega from vta_bodega where id_sucursal="+id_sucursal);
    }
    public ResultSet getBodegasResponsables()
    {
        return this.consulta("SELECT B.id_bodega,B.bodega || ' -> ' || E.empleado FROM vta_bodega as B inner join vta_empleado as E on B.id_responsable=E.id_empleado order by B.bodega,E.empleado;");
    }
    public String[] getBodegaResponsable(String id_empleado)
    {
        String res[] = new String[]{"", "", "", "", ""};
        try{
            ResultSet rs = this.consulta("SELECT B.id_bodega,B.bodega,B.ubicacion, E.dni, E.empleado FROM vta_bodega as B inner join vta_empleado as E on B.id_responsable=E.id_empleado "
                    + "where B.id_responsable="+id_empleado);
            if(rs.next()){
                res[0] = rs.getString("id_bodega")!=null ? rs.getString("id_bodega") : "";
                res[1] = rs.getString("bodega")!=null ? rs.getString("bodega") : "";
                res[2] = rs.getString("ubicacion")!=null ? rs.getString("ubicacion") : "";
                res[3] = rs.getString("dni")!=null ? rs.getString("dni") : "";
                res[4] = rs.getString("empleado")!=null ? rs.getString("empleado") : "";
                rs.close();
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return res;
    }
    public String getIdBodega(String id_empleado)
    {
        String id_bodega = "";
        try{
            ResultSet r = this.consulta("SELECT * FROM tbl_bodega where id_responsable="+id_empleado);
            if(r.next()){
                id_bodega = (r.getString("id_bodega")!=null) ? r.getString("id_bodega") : "";
                r.close();
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return id_bodega;
    }
    public ResultSet getBodegaUsuario(String usuario)
    {
        return this.consulta("SELECT B.*, E.dni, E.nombre || ' ' || E.apellido as empleado FROM tbl_bodega as B inner join tbl_empleado as E on B.id_responsable=E.id_empleado where E.alias='"+usuario+"'");
    }
    public String getNombre(String id)
    {
        String bodega1 = "";
        if(id.compareTo("-0")!=0){
            try{
                ResultSet r = this.consulta("SELECT * FROM tbl_bodega where id_bodega="+id);
                if(r.next()){
                    bodega1 = (r.getString("bodega")!=null) ? r.getString("bodega") : "";
                    r.close();
                }
            }catch(Exception e){
                e.printStackTrace();
            }
        }
        return bodega1;
    }
    public String getResponsable(String id)
    {
        String empleado = "";
        if(id.compareTo("-0")!=0){
            try{
                ResultSet r = this.consulta("SELECT E.empleado FROM tbl_bodega as B inner join vta_empleado as E on B.id_responsable=E.id_empleado where B.id_bodega="+id);
                if(r.next()){
                    empleado = (r.getString("empleado")!=null) ? r.getString("empleado") : "";
                    r.close();
                }
            }catch(Exception e){
                e.printStackTrace();
            }
        }
        return empleado;
    }
    public boolean estaDuplicado(String id, String bodega)
    {
        ResultSet res = this.consulta("SELECT * FROM tbl_bodega where lower(bodega)='"+bodega.toLowerCase()+"' and id_bodega<>"+id);
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
    public String insertar(String id_sucursal, String bodega, String id_responsable, String coresponsable, 
            String ubicacion, String estado)
    {
        String pk = this.insert("INSERT INTO tbl_bodega(id_sucursal,bodega,id_responsable,coresponsable,ubicacion,estado) VALUES(" +
                id_sucursal+", '"+bodega+"', "+id_responsable+", '"+coresponsable+"', '"+ubicacion+"', "+estado+")");
        
        this.ejecutar("INSERT INTO tbl_estanteria(id_bodega,estanteria,fila,columna,num_nivel) VALUES("+pk+", '"+this.getNombreEstanteria(bodega)+"', 1, 1, 1)");
        
        return pk;
    }
    
    public boolean actualizar(String id, String id_sucursal, String bodega, String id_responsable, String coresponsable,
            String ubicacion, String estado)
    {
        return this.ejecutar("UPDATE tbl_bodega SET id_sucursal="+id_sucursal+", bodega='"+bodega+"', id_responsable="+id_responsable+", "
                + "coresponsable='"+coresponsable+"', ubicacion='"+ubicacion+"', estado="+estado+" WHERE id_bodega="+id);
    }

    
    
    
    /*   ESTANTERIAS   */

    public String getNombreEstanteria(String bodega)
    {
        String estanteria = "EST";
        String vec[] = bodega.split(" ");
        for(int i=0; i<vec.length; i++){
            int lim = vec[i].length()>3 ? 3 : vec[i].length(); 
            estanteria += vec[i].substring(0, lim);
        }
        return estanteria + "001";
    }
    
    public ResultSet getEstanteria(String id_estanteria)
    {
        return this.consulta("SELECT * from tbl_estanteria where id_estanteria="+id_estanteria);
    }

    public ResultSet getEstanterias(String id_bodega)
    {
        return this.consulta("SELECT * from tbl_estanteria where id_bodega="+id_bodega + " and eliminado=false");
    }

    public ResultSet getEstanterias(int id_bodega)
    {
        return this.consulta("SELECT estanteria, estanteria from tbl_estanteria where id_bodega="+id_bodega+" and eliminado=false");
    }

    public ResultSet getUbicaciones(int id_bodega)
    {
        return this.consulta("SELECT estanteria, estanteria from tbl_estanteria where id_bodega="+id_bodega+" and eliminado=false");
    }

    public String tblEstanterias(String id_bodega)
    {
        String html = "<TABLE class='jm_tabla' cellspacing='1' cellpadding='0' id='tblPE'>";
            try{
                int i=0;
                String id_estanteria = "";
                String estanteria = "";
                String fila = "";
                String columna = "";
                String num_nivel = "";
                ResultSet rsEstanterias = this.getEstanterias(id_bodega);
                while(rsEstanterias.next()){
                    id_estanteria = (rsEstanterias.getString("id_estanteria")!=null) ? rsEstanterias.getString("id_estanteria") : "";
                    estanteria = (rsEstanterias.getString("estanteria")!=null) ? rsEstanterias.getString("estanteria") : "";
                    fila = (rsEstanterias.getString("fila")!=null) ? rsEstanterias.getString("fila") : "";
                    columna = (rsEstanterias.getString("columna")!=null) ? rsEstanterias.getString("columna") : "";
                    num_nivel = (rsEstanterias.getString("num_nivel")!=null) ? rsEstanterias.getString("num_nivel") : "";
                    html += "<tr id='rTI"+i+"' valign='top' class='jm_filaPar' onclick=\"adm_estanteriaEditar("+id_estanteria+");\" style='cursor:pointer' onmouseover=\"this.className='jm_filaSobre'\" onmouseout=\"this.className='jm_filaPar'\">";
                    html += "<td width='121' style='cursor:pointer'>"+estanteria+"</td>";
                    html += "<td width='81' style='cursor:pointer'>"+fila+"</td>";
                    html += "<td width='81' style='cursor:pointer'>"+columna+"</td>";
                    html += "<td width='81' style='cursor:pointer'>"+num_nivel+"</td>";
                    html += "<td align='center' width='21'><input type='hidden' id='idE"+i+"' value='"+id_estanteria+"' />&nbsp;</td></tr>";
                    i++;
                }
            }catch(Exception e){e.printStackTrace();}

        html += "</table>";

        return html;
    }

    public boolean estaDuplicadaEstanteria(String id, String estanteria)
    {
        ResultSet res = this.consulta("SELECT * FROM tbl_estanteria where lower(estanteria)='"+estanteria.toLowerCase()+"' and id_estanteria<>"+id);
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
    
    public String insertarEstanteria(String id_bodega, String estanteria, String fila, String columna, String num_nivel)
    {
        return this.insert("INSERT INTO tbl_estanteria(id_bodega,estanteria,fila,columna,num_nivel) VALUES(" +
                id_bodega+", '"+estanteria+"', "+fila+", "+columna+", "+num_nivel+")");
    }

    public boolean actualizarEstanteria(String id, String estanteria, String fila, String columna, String num_nivel)
    {
        return this.ejecutar("UPDATE tbl_estanteria SET estanteria='"+estanteria+"', fila="+fila+", "
                + "columna="+columna+", num_nivel="+num_nivel+" WHERE id_estanteria="+id);
    }

    /*  ARTICULOS DE LA BODEGA  */

    public ResultSet getArticulos(String id_bodega)
    {
        return this.consulta("SELECT BA.ubicacion, A.codigo_activo, A.descripcion from tbl_bodega_activo as BA inner join tbl_activo as A on BA.id_activo=A.id_activo "
                + "where BA.id_bodega="+id_bodega+" and A.eliminado=false and A.id_activo not in (select id_activo from tbl_activo_perdida) order by A.codigo_activo");
    }

}