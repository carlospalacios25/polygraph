package com.polygraph.modelo;

import java.time.LocalDate;
import java.time.LocalTime;

public class Visitas {
    private int idVisita;
    private int idServicio;
    private int idVisitador;
    private String Tipo_Prueba;
    private String Tipo_Visita;
    private LocalDate fechaSolicitud;
    private LocalDate fechaVisita;
    private LocalTime horaVisita;
    private LocalDate FechaeInforme;
    private String novedadVisita;

    public Visitas(int idVisita, int idServicio, int idVisitador, String Tipo_Prueba, String Tipo_Visita, LocalDate fechaSolicitud, LocalDate fechaVisita, LocalTime horaVisita, LocalDate FechaeInforme, String novedadVisita) {
        this.idVisita = idVisita;
        this.idServicio = idServicio;
        this.idVisitador = idVisitador;
        this.Tipo_Prueba = Tipo_Prueba;
        this.Tipo_Visita = Tipo_Visita;
        this.fechaSolicitud = fechaSolicitud;
        this.fechaVisita = fechaVisita;
        this.horaVisita = horaVisita;
        this.FechaeInforme = FechaeInforme;
        this.novedadVisita = novedadVisita;
    }

    public int getIdVisita() {
        return idVisita;
    }

    public void setIdVisita(int idVisita) {
        this.idVisita = idVisita;
    }

    public int getIdServicio() {
        return idServicio;
    }

    public void setIdServicio(int idServicio) {
        this.idServicio = idServicio;
    }

    public int getIdVisitador() {
        return idVisitador;
    }

    public void setIdVisitador(int idVisitador) {
        this.idVisitador = idVisitador;
    }

    public String getTipo_Prueba() {
        return Tipo_Prueba;
    }

    public void setTipo_Prueba(String Tipo_Prueba) {
        this.Tipo_Prueba = Tipo_Prueba;
    }

    public String getTipo_Visita() {
        return Tipo_Visita;
    }

    public void setTipo_Visita(String Tipo_Visita) {
        this.Tipo_Visita = Tipo_Visita;
    }

    public LocalDate getFechaSolicitud() {
        return fechaSolicitud;
    }

    public void setFechaSolicitud(LocalDate fechaSolicitud) {
        this.fechaSolicitud = fechaSolicitud;
    }

    public LocalDate getFechaVisita() {
        return fechaVisita;
    }

    public void setFechaVisita(LocalDate fechaVisita) {
        this.fechaVisita = fechaVisita;
    }

    public LocalTime getHoraVisita() {
        return horaVisita;
    }

    public void setHoraVisita(LocalTime horaVisita) {
        this.horaVisita = horaVisita;
    }

    public LocalDate getFechaeInforme() {
        return FechaeInforme;
    }

    public void setFechaeInforme(LocalDate FechaeInforme) {
        this.FechaeInforme = FechaeInforme;
    }

    public String getNovedadVisita() {
        return novedadVisita;
    }

    public void setNovedadVisita(String novedadVisita) {
        this.novedadVisita = novedadVisita;
    }    
    
}
