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

package jm.inf.clas;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import jm.web.DataBase;

/**
 *
 * @author Jorge
 */
public class Radius extends DataBase{
    public Radius(String m, int p, String db, String u, String c){
        super(m, p, db, u, c);
    }
    public ResultSet getUsuario(String usuario)
    {
        return this.consulta("SELECT * FROM radcheck as C inner join radreply as R on C.username=R.username where C.username='"+usuario+"'");
    }

    public int getNumInstalaciones(String usuario)
    {
        int num_ints = 1;
        try{
            ResultSet rs = this.consulta("select case when max(username)>0 then max(username)+1 else 1 end from radcheck where username='"+usuario+"'");
            if(rs.next()){
                num_ints = rs.getString(1)!=null ? rs.getInt(1) : 1;
                rs.close();
            }
            rs.close();
        }catch(Exception e){
            e.printStackTrace();
        }
        return num_ints;
    }

    public boolean setAtributo(String usuario, String atributo, String operador, String valor)
    {
        try{
            ResultSet rs = this.consulta("select * from radreply where username='"+usuario+"' and attribute='"+atributo+"';");
            if(this.getFilas(rs)>0){
                return this.ejecutar("insert into radreply set attribute='"+atributo+"', op='"+operador+"', value='"+valor+"' where username='"+usuario+"';");
            } else {
                return this.ejecutar("insert into radreply(username, attribute, op, value) values('"+usuario+"', '"+atributo+"', '"+operador+"', '"+valor+"');");
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return false;
    }

    public boolean setRateUsuarios(String usuarios, String valor)
    {
        return this.ejecutar("UPDATE radreply SET value='"+valor+"' WHERE attribute='Mikrotik-Rate-Limit' and username in ("+usuarios+")");
    }


    /////////////////////////////
    public boolean actualizarUsuario(String usuario, String usuario_ant)
    {
        List sql = new ArrayList();
        //sql.add("UPDATE radcheck SET username='"+usuario+"' WHERE username='"+usuario_ant+"';");
        sql.add("UPDATE radusergroup SET username='"+usuario+"' WHERE username='"+usuario_ant+"';");
        //sql.add("UPDATE radreply SET username='"+usuario+"' WHERE username='"+usuario_ant+"';");
        return this.transacciones(sql);
    }

    public boolean setClave(String usuario, String clave)
    {
        return this.ejecutar("UPDATE radcheck SET value='"+clave+"' WHERE username='"+usuario+"'");
    }


    /*   planes      */


    public boolean clientesEnPlan(String plan)
    {
        ResultSet rs = this.consulta("select * from radusergroup where groupname='"+plan+"'");
        if(this.getFilas(rs)>0){
            return true;
        }
        return false;
    }

    public boolean crearPlan(String plan, String rateLimit, String timeout, String loginTime)
    {
        List sql = new ArrayList();
        sql.add("INSERT INTO radgroupreply(groupname, attribute, op, value) VALUES('"+plan+"', 'Mikrotik-Rate-Limit', ':=', '"+rateLimit+"');");
        sql.add("INSERT INTO radgroupreply(groupname, attribute, op, value) VALUES('"+plan+"', 'Session-Timeout', ':=', '"+timeout+"');");
        sql.add("INSERT INTO radgroupreply(groupname, attribute, op, value) VALUES('"+plan+"', 'Framed-Pool', ':=', 'dhcp_clientes');");
        
        sql.add("INSERT INTO radgroupcheck(groupname, attribute, op, value) VALUES('"+plan+"', 'Auth-Type', ':=', 'Local');");
        sql.add("INSERT INTO radgroupcheck(groupname, attribute, op, value) VALUES('"+plan+"', 'Cleartext-Password', ':=', '');");
        if(plan.toUpperCase().indexOf("NOCT")>=0){
            sql.add("INSERT INTO radgroupcheck(groupname, attribute, op, value) VALUES('"+plan+"', 'Login-Time', ':=', '"+loginTime+"');");
        }
        return this.transacciones(sql);
    }

    public boolean actualizarPlan(String plan, String plan_nuevo, String rateLimit, String loginTime)
    {
        List sql = new ArrayList();
        sql.add("UPDATE radgroupreply SET value='"+rateLimit+"' WHERE groupname='"+plan+"' and attribute='Mikrotik-Rate-Limit';");
        sql.add("UPDATE radgroupreply SET groupname='"+plan_nuevo+"' WHERE groupname='"+plan+"';");
        sql.add("UPDATE radgroupcheck SET value='"+loginTime+"' WHERE groupname='"+plan+"' and attribute='Login-Time';");
        sql.add("UPDATE radgroupcheck SET groupname='"+plan_nuevo+"' WHERE groupname='"+plan+"';");
        sql.add("UPDATE radusergroup SET groupname='"+plan_nuevo+"' WHERE groupname='"+plan+"';");
        return this.transacciones(sql);
    }
    public boolean actualizarPlanServicio(String plan, String rateLimit, String loginTime)
    {
        List sql = new ArrayList();
        sql.add("UPDATE radgroupreply SET value='"+rateLimit+"' WHERE groupname='"+plan+"' and attribute='Mikrotik-Rate-Limit';");
        sql.add("UPDATE radgroupcheck SET value='"+loginTime+"' WHERE groupname='"+plan+"' and attribute='Login-Time';");
        return this.transacciones(sql);
    }
    public boolean eliminarPlan(String plan)
    {
        List sql = new ArrayList();
        sql.add("DELETE FROM radgroupcheck WHERE groupname='"+plan+"';");
        sql.add("DELETE FROM radgroupreply WHERE groupname='"+plan+"';");
        //sql.add("DELETE FROM radusergroup WHERE groupname='"+plan+"';");
        return this.transacciones(sql);
    }



    /////////////////////////////////

    public boolean crear(String usuario, String plan)
    {
        return this.ejecutar("insert into radusergroup(username, groupname, priority) values('"+usuario+"', '"+plan+"', 1);");
    }
    public boolean setActivaciones(String activados[][])
    {
        List sql = new ArrayList();
        for(int i=0; i< activados.length; i++){
            sql.add("UPDATE radusergroup SET groupname='"+activados[i][1]+"' WHERE username = '"+activados[i][0]+"';");
        }
        return this.transacciones(sql);
    }
    public boolean setCortes(String usuarios)
    {
        return this.ejecutar("UPDATE radusergroup SET groupname='cortados' WHERE username in ("+usuarios+")");
    }

    public boolean setSuspenciones(String usuario)
    {
        return this.ejecutar("UPDATE radusergroup SET groupname='suspendidos' WHERE username in ("+usuario+")");
    }
    
    public boolean setSuspencion(String usuario)
    {
        return this.ejecutar("UPDATE radusergroup SET groupname='suspendidos' WHERE username = '"+usuario+"'");
    }
    
    public boolean setPlan(String usuario, String plan)
    {
        return this.ejecutar("UPDATE radusergroup SET groupname='"+plan+"' WHERE username in ('"+usuario+"')");
    }

    public boolean cambioRazonSocial(String usuario_ant, String usuario, String plan)
    {
        List sql = new ArrayList();
        sql.add("DELETE FROM radusergroup WHERE username='"+usuario_ant+"' and groupname='"+plan+"';");
        sql.add("insert into radusergroup(username, groupname, priority) values('"+usuario+"', '"+plan+"', 1);");
        return this.transacciones(sql);
    }
    
    public boolean eliminar(String usuario)
    {
        return this.ejecutar("DELETE FROM radusergroup WHERE username='"+usuario+"';");
    }
    public boolean eliminaciones(String usuarios)
    {
        return this.ejecutar("DELETE FROM radusergroup WHERE username in ("+usuarios+");");
    }

    public boolean eliminarTodo(String usuario)
    {
        List sql = new ArrayList();
        sql.add("DELETE FROM radcheck WHERE username='"+usuario+"';");
        sql.add("DELETE FROM radreply WHERE username='"+usuario+"';");
        sql.add("DELETE FROM radusergroup WHERE username='"+usuario+"';");
        return this.transacciones(sql);
    }



    /* RADCHECK */

    public ResultSet getUsuarioRadcheck(String usuario)
    {
        return this.consulta("SELECT * FROM radcheck where username='"+usuario+"'");
    }

    public boolean hayUsuarioRadcheck(String usuario)
    {
        try{
            ResultSet rs = this.consulta("select * from radcheck where username='"+usuario+"';");
            if(this.getFilas(rs)>0){
                return true;
            } 
        }catch(Exception e){
            e.printStackTrace();
        }
        return false;
    }

    public boolean hayAtributoRadcheck(String usuario, String atributo)
    {
        try{
            ResultSet rs = this.consulta("select * from radcheck where username='"+usuario+"' and attribute='"+atributo+"';");
            if(this.getFilas(rs)>0){
                return true;
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return false;
    }

    public String insertarRadcheck(String usuario, String att, String op, String val)
    {
        return this.insert("insert into radcheck(username, attribute, op, value) values('"+usuario+"', '"+att+"', '"+op+"', '"+val+"');");
    }
    public boolean actualizarRadcheck(String id, String att, String op, String val)
    {
        return this.ejecutar("update radcheck set attribute='"+att+"', op='"+op+"', value='"+val+"' where id="+id);
    }

    /* RADREPLY */

    public ResultSet getUsuarioRadreply(String usuario)
    {
        return this.consulta("SELECT * FROM radreply where username='"+usuario+"'");
    }

    public boolean hayUsuarioRadreply(String usuario)
    {
        try{
            ResultSet rs = this.consulta("select * from radreply where username='"+usuario+"';");
            if(this.getFilas(rs)>0){
                return true;
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return false;
    }

    public boolean hayAtributoRadreply(String usuario, String atributo)
    {
        try{
            ResultSet rs = this.consulta("select * from radreply where username='"+usuario+"' and attribute='"+atributo+"';");
            if(this.getFilas(rs)>0){
                return true;
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return false;
    }

    public String insertarRadreply(String usuario, String att, String op, String val)
    {
        return this.insert("insert into radreply(username, attribute, op, value) values('"+usuario+"', '"+att+"', '"+op+"', '"+val+"');");
    }
    public boolean actualizarRadreply(String id, String att, String op, String val)
    {
        return this.ejecutar("update radreply set attribute='"+att+"', op='"+op+"', value='"+val+"' where id="+id);
    }


    /* USERGROUP */

    public ResultSet getUsuarioRadusergroup(String usuario)
    {
        return this.consulta("SELECT * FROM radusergroup where username='"+usuario+"'");
    }
    public ResultSet getUsuariosRadusergroup(String groupname)
    {
        return this.consulta("SELECT * FROM radusergroup where groupname in ("+groupname+");");
    }
    public boolean hayUsuarioRadusergroup(String usuario)
    {
        try{
            ResultSet rs = this.consulta("select * from radusergroup where username='"+usuario+"';");
            if(this.getFilas(rs)>0){
                return true;
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return false;
    }
    public boolean hayGrupoRadusergroup(String usuario)
    {
        try{
            ResultSet rs = this.consulta("select * from radusergroup where username='"+usuario+"';");
            if(this.getFilas(rs)>0){
                return true;
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return false;
    }
    public boolean insertarRadusergroup(String usuario, String grup, String prio)
    {
        return this.ejecutar("insert into radusergroup(username, groupname, priority) values('"+usuario+"', '"+grup+"', "+prio+");");
    }
    public boolean actualizarRadusergroup(String username, String grup, String prio)
    {
        return this.ejecutar("update radusergroup set groupname='"+grup+"', priority="+prio+" where username='"+username+"'");
    }
    public boolean setGrupoRadusergroup(String username, String grup)
    {
        return this.ejecutar("update radusergroup set groupname='"+grup+"' where username='"+username+"'");
    }

    
}