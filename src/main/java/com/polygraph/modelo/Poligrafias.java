
package com.polygraph.modelo;

import java.time.LocalDate;

public class Poligrafias {
    private int idPoligrafia;
    private int idServicio;
    private int idPoligrafista;
    private LocalDate fechaAsignacion;
    private boolean asistencia;
    private LocalDate fechaEntrega;

    public Poligrafias(int idPoligrafia, int idServicio, int idPoligrafista, LocalDate fechaAsignacion, boolean asistencia, LocalDate fechaEntrega) {
        this.idPoligrafia = idPoligrafia;
        this.idServicio = idServicio;
        this.idPoligrafista = idPoligrafista;
        this.fechaAsignacion = fechaAsignacion;
        this.asistencia = asistencia;
        this.fechaEntrega = fechaEntrega;
    }

    public int getIdPoligrafia() {
        return idPoligrafia;
    }

    public void setIdPoligrafia(int idPoligrafia) {
        this.idPoligrafia = idPoligrafia;
    }

    public int getIdServicio() {
        return idServicio;
    }

    public void setIdServicio(int idServicio) {
        this.idServicio = idServicio;
    }

    public int getIdPoligrafista() {
        return idPoligrafista;
    }

    public void setIdPoligrafista(int idPoligrafista) {
        this.idPoligrafista = idPoligrafista;
    }

    public LocalDate getFechaAsignacion() {
        return fechaAsignacion;
    }

    public void setFechaAsignacion(LocalDate fechaAsignacion) {
        this.fechaAsignacion = fechaAsignacion;
    }

    public boolean isAsistencia() {
        return asistencia;
    }

    public void setAsistencia(boolean asistencia) {
        this.asistencia = asistencia;
    }

    public LocalDate getFechaEntrega() {
        return fechaEntrega;
    }

    public void setFechaEntrega(LocalDate fechaEntrega) {
        this.fechaEntrega = fechaEntrega;
    }

    
}
