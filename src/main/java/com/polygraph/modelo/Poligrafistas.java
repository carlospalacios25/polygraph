
package com.polygraph.modelo;

public class Poligrafistas {
    private int idPoligrafista;
    private String nombrePoligrafista;
    private String salaEncargada;

    public Poligrafistas(int idPoligrafista, String nombrePoligrafista, String salaEncargada) {
        this.idPoligrafista = idPoligrafista;
        this.nombrePoligrafista = nombrePoligrafista;
        this.salaEncargada = salaEncargada;
    }

    public int getIdPoligrafista() {
        return idPoligrafista;
    }

    public void setIdPoligrafista(int idPoligrafista) {
        this.idPoligrafista = idPoligrafista;
    }

    public String getNombrePoligrafista() {
        return nombrePoligrafista;
    }

    public void setNombrePoligrafista(String nombrePoligrafista) {
        this.nombrePoligrafista = nombrePoligrafista;
    }

    public String getSalaEncargada() {
        return salaEncargada;
    }

    public void setSalaEncargada(String salaEncargada) {
        this.salaEncargada = salaEncargada;
    }

    
}
