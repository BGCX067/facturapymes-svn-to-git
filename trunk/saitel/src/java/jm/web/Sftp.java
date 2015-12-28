
package jm.web;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Properties;

import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;
import java.util.List;
import java.util.Vector;

public class Sftp {

    private JSch jsch;
    private Session session;
    private ChannelSftp sftp;
    private String error;

    public Sftp()
    {}
    
    public boolean conectar(String servidor, int puerto, String usuario, String clave)
    {
        try {
            this.jsch = new JSch();
            this.session = this.jsch.getSession(usuario, servidor, puerto);
            this.session.setPassword(clave);
            Properties prop = new Properties();
            prop.put("StrictHostKeyChecking", "no");
            this.session.setConfig(prop);
            this.session.connect();
            if(this.session.isConnected()){
                this.sftp = (ChannelSftp) this.session.openChannel("sftp");
                this.sftp.connect();
                if(this.sftp.isConnected()){
                    return true;
                }
            }
        } catch (JSchException e) {
            this.error = e.getMessage();
        }
        return false;
    }

    public String pwd()
    {
        String ruta = null;
        try {
            ruta = this.sftp.pwd();
        } catch (SftpException e) {
            this.error = e.getMessage();
        }
        return ruta;
    }

    public boolean cd(String directorio)
    {
        try {
            this.sftp.cd(directorio);
            return true;
        } catch (SftpException e) {
            this.error = e.getMessage();
        }
        return false;
    }
    
    public Vector listar(String directorio)
    {
        try {
            Vector vec = this.sftp.ls(directorio);
            return vec;
        } catch (SftpException e) {
            this.error = e.getMessage();
        }
        return null;
    }

    public boolean subirArchivo(String rutaArchivo, String rutaArchivoRemoto)
    {
        try {
            this.sftp.put(rutaArchivo, rutaArchivoRemoto);
            return true;
        } catch (SftpException e) {
            this.error = e.getMessage();
        }
        return false;
    }

    public boolean descargarArchivo(String rutaArchivo, String rutaArchivoRemoto)
    {
        try {
            OutputStream os = new BufferedOutputStream(new FileOutputStream(rutaArchivo));
            this.sftp.get(rutaArchivoRemoto, os);
            return true;
        } catch (IOException e) {
            this.error = e.getMessage();
        } catch (SftpException e) {
            this.error = e.getMessage();
        }
        return false;
    }

    public boolean borrarArchivo(String rutaArchivoRemoto)
    {
        try {
            this.sftp.rm(rutaArchivoRemoto);
            return true;
        } catch (SftpException e) {
            this.error = e.getMessage();
        }
        return false;
    }

    public void desconectar()
    {
        this.sftp.exit();
        if(this.sftp.isConnected()){
            this.sftp.disconnect();
        }
        if(this.session.isConnected()){
            this.session.disconnect();
        }
    }

    public void setError(String error)
    {
        this.error = error;
    }
    
    public String getError()
    {
        return this.error;
    }
    
}