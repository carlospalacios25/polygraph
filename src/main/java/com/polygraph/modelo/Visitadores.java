package com.polygraph.modelo;

public class Visitadores {
    private int idVisitador;
    private String nombreVisitador;
    private String zonasVisitador;

    public Visitadores(int idVisitador, String nombreVisitador, String zonasVisitador) {
        this.idVisitador = idVisitador;
        this.nombreVisitador = nombreVisitador;
        this.zonasVisitador = zonasVisitador;
    }

    public int getIdVisitador() {
        return idVisitador;
    }

    public void setIdVisitador(int idVisitador) {
        this.idVisitador = idVisitador;
    }

    public String getNombreVisitador() {
        return nombreVisitador;
    }

    public void setNombreVisitador(String nombreVisitador) {
        this.nombreVisitador = nombreVisitador;
    }

    public String getZonasVisitador() {
        return zonasVisitador;
    }

    public void setZonasVisitador(String zonasVisitador) {
        this.zonasVisitador = zonasVisitador;
    }
    
    
}
