package com.polygraph.modelo;

public class Analisis {
    private int idAnalisis;
    private int idServicio;
    private String tipoAnalisis;
    private String contenido;

    // Constructor vacío (necesario para JavaFX, DAO, etc.)
    public Analisis() {
    }

    // Constructor completo
    public Analisis(int idAnalisis, int idServicio, String tipoAnalisis, String contenido) {
        this.idAnalisis = idAnalisis;
        this.idServicio = idServicio;
        this.tipoAnalisis = tipoAnalisis;
        this.contenido = contenido;
    }

    public int getIdAnalisis() {
        return idAnalisis;
    }

    public void setIdAnalisis(int idAnalisis) {
        this.idAnalisis = idAnalisis;
    }

    public int getIdServicio() {
        return idServicio;
    }

    public void setIdServicio(int idServicio) {
        this.idServicio = idServicio;
    }

    public String getTipoAnalisis() {
        return tipoAnalisis;
    }

    public void setTipoAnalisis(String tipoAnalisis) {
        this.tipoAnalisis = tipoAnalisis;
    }

    public String getContenido() {
        return contenido;
    }

    public void setContenido(String contenido) {
        this.contenido = contenido;
    }
}
