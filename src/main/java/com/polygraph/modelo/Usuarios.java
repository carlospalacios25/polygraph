
package com.polygraph.modelo;

import java.time.LocalDate;


public class Usuarios {
    private String nombreEmp;
    private String apellidoEmp;
    private String nombreusuario;
    private String correoUsu;
    private String contrasenaUsu;
    private LocalDate fechaCreacion;
    private boolean activoUsu;
    private int idPerfil;

    public Usuarios(String nombreEmp, String apellidoEmp, String nombreusuario, String correoUsu, String contrasenaUsu, LocalDate fechaCreacion, boolean activoUsu, int idPerfil) {
        this.nombreEmp = nombreEmp;
        this.apellidoEmp = apellidoEmp;
        this.nombreusuario = nombreusuario;
        this.correoUsu = correoUsu;
        this.contrasenaUsu = contrasenaUsu;
        this.fechaCreacion = fechaCreacion;
        this.activoUsu = activoUsu;
        this.idPerfil = idPerfil;
    }

    public String getNombreEmp() {
        return nombreEmp;
    }

    public void setNombreEmp(String nombreEmp) {
        this.nombreEmp = nombreEmp;
    }

    public String getApellidoEmp() {
        return apellidoEmp;
    }

    public void setApellidoEmp(String apellidoEmp) {
        this.apellidoEmp = apellidoEmp;
    }

    public String getNombreusuario() {
        return nombreusuario;
    }

    public void setNombreusuario(String nombreusuario) {
        this.nombreusuario = nombreusuario;
    }

    public String getCorreoUsu() {
        return correoUsu;
    }

    public void setCorreoUsu(String correoUsu) {
        this.correoUsu = correoUsu;
    }

    public String getContrasenaUsu() {
        return contrasenaUsu;
    }

    public void setContrasenaUsu(String contrasenaUsu) {
        this.contrasenaUsu = contrasenaUsu;
    }

    public LocalDate getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(LocalDate fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public boolean isActivoUsu() {
        return activoUsu;
    }

    public void setActivoUsu(boolean activoUsu) {
        this.activoUsu = activoUsu;
    }

    public int getIdPerfil() {
        return idPerfil;
    }

    public void setIdPerfil(int idPerfil) {
        this.idPerfil = idPerfil;
    }
      
    
}
