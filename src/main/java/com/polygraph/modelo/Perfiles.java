
package com.polygraph.modelo;

public class Perfiles {
    private int idPerfil;
    private String nombrePerfil;
    private String descripcion;

    public Perfiles(int idPerfil, String nombrePerfil, String descripcion) {
        this.idPerfil = idPerfil;
        this.nombrePerfil = nombrePerfil;
        this.descripcion = descripcion;
    }

    public int getIdPerfil() {
        return idPerfil;
    }

    public void setIdPerfil(int idPerfil) {
        this.idPerfil = idPerfil;
    }

    public String getNombrePerfil() {
        return nombrePerfil;
    }

    public void setNombrePerfil(String nombrePerfil) {
        this.nombrePerfil = nombrePerfil;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }
    
    
            
}
