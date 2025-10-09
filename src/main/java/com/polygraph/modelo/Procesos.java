
package com.polygraph.modelo;

public class Procesos {
    private int idProceso;
    private int nombreProceso;

    public Procesos(int idProceso, int nombreProceso) {
        this.idProceso = idProceso;
        this.nombreProceso = nombreProceso;
    }

    public int getIdProceso() {
        return idProceso;
    }

    public void setIdProceso(int idProceso) {
        this.idProceso = idProceso;
    }

    public int getNombreProceso() {
        return nombreProceso;
    }

    public void setNombreProceso(int nombreProceso) {
        this.nombreProceso = nombreProceso;
    }
    
    
}
