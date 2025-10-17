
package com.polygraph.modelo;

public class PerfilesPermisos {
    private int idPerfil;
    private int idPermiso;

    public PerfilesPermisos(int idPerfil, int idPermiso) {
        this.idPerfil = idPerfil;
        this.idPermiso = idPermiso;
    }

    public int getIdPerfil() {
        return idPerfil;
    }

    public void setIdPerfil(int idPerfil) {
        this.idPerfil = idPerfil;
    }

    public int getIdPermiso() {
        return idPermiso;
    }

    public void setIdPermiso(int idPermiso) {
        this.idPermiso = idPermiso;
    }
    
}
