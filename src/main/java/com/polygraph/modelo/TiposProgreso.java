
package com.polygraph.modelo;

public class TiposProgreso {
   private int idTipoProgreso;
   private String nombreProgreso;

    public TiposProgreso(int idTipoProgreso, String nombreProgreso) {
        this.idTipoProgreso = idTipoProgreso;
        this.nombreProgreso = nombreProgreso;
    }

    public int getIdTipoProgreso() {
        return idTipoProgreso;
    }

    public void setIdTipoProgreso(int idTipoProgreso) {
        this.idTipoProgreso = idTipoProgreso;
    }

    public String getNombreProgreso() {
        return nombreProgreso;
    }

    public void setNombreProgreso(String nombreProgreso) {
        this.nombreProgreso = nombreProgreso;
    }
   
   
}
