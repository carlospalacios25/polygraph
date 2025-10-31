
package com.polygraph.modelo;

public class Procesos {
    private int idProceso;
    private String nombreProceso;

    public Procesos(int idProceso, String nombreProceso) {
        this.idProceso = idProceso;
        this.nombreProceso = nombreProceso;
    }

    public int getIdProceso() {
        return idProceso;
    }

    public void setIdProceso(int idProceso) {
        this.idProceso = idProceso;
    }

    public String getNombreProceso() {
        return nombreProceso;
    }

    public void setNombreProceso(String nombreProceso) {
        this.nombreProceso = nombreProceso;
    }
    
    
}
