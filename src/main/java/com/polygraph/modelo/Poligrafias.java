package com.polygraph.modelo;

import java.time.LocalDate;
import java.time.LocalTime;

public class Poligrafias {
    private int idPoligrafia;
    private int idServicio;
    private int idPoligrafista;
    private LocalDate fechaAsignacion;
    private LocalTime horaProgramacion;
    // En BD es enum('Si','No') -> lo manejamos como String "Si"/"No"
    private String asistencia;
    private LocalDate fechaEntrega;

    // Campos de apoyo para mostrar en UI
    private String nombrePoligrafista;
    private String nombreServicio;

    public Poligrafias(int idPoligrafia,
                       int idServicio,
                       int idPoligrafista,
                       LocalDate fechaAsignacion,
                       LocalTime horaProgramacion,
                       String asistencia,
                       LocalDate fechaEntrega) {
        this.idPoligrafia = idPoligrafia;
        this.idServicio = idServicio;
        this.idPoligrafista = idPoligrafista;
        this.fechaAsignacion = fechaAsignacion;
        this.horaProgramacion = horaProgramacion;
        this.asistencia = asistencia;
        this.fechaEntrega = fechaEntrega;
    }

    public Poligrafias(int idServicio,
                       int idPoligrafista,
                       LocalDate fechaAsignacion,
                       LocalTime horaProgramacion,
                       String asistencia,
                       LocalDate fechaEntrega) {
        this(0, idServicio, idPoligrafista, fechaAsignacion, horaProgramacion, asistencia, fechaEntrega);
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

    public LocalTime getHoraProgramacion() {
        return horaProgramacion;
    }

    public void setHoraProgramacion(LocalTime horaProgramacion) {
        this.horaProgramacion = horaProgramacion;
    }

    public String getAsistencia() {
        return asistencia;
    }

    public void setAsistencia(String asistencia) {
        this.asistencia = asistencia;
    }

    public LocalDate getFechaEntrega() {
        return fechaEntrega;
    }

    public void setFechaEntrega(LocalDate fechaEntrega) {
        this.fechaEntrega = fechaEntrega;
    }

    public String getNombrePoligrafista() {
        return nombrePoligrafista;
    }

    public void setNombrePoligrafista(String nombrePoligrafista) {
        this.nombrePoligrafista = nombrePoligrafista;
    }

    public String getNombreServicio() {
        return nombreServicio;
    }

    public void setNombreServicio(String nombreServicio) {
        this.nombreServicio = nombreServicio;
    }
}
