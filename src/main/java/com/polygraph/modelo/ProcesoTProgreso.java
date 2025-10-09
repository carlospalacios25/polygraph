
package com.polygraph.modelo;

public class ProcesoTProgreso {
   private int id;
   private int idProceso;
   private int idTipoProgreso;
   private boolean habilitado;

    public ProcesoTProgreso(int id, int idProceso, int idTipoProgreso, boolean habilitado) {
        this.id = id;
        this.idProceso = idProceso;
        this.idTipoProgreso = idTipoProgreso;
        this.habilitado = habilitado;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getIdProceso() {
        return idProceso;
    }

    public void setIdProceso(int idProceso) {
        this.idProceso = idProceso;
    }

    public int getIdTipoProgreso() {
        return idTipoProgreso;
    }

    public void setIdTipoProgreso(int idTipoProgreso) {
        this.idTipoProgreso = idTipoProgreso;
    }

    public boolean isHabilitado() {
        return habilitado;
    }

    public void setHabilitado(boolean habilitado) {
        this.habilitado = habilitado;
    }
   
   
}
