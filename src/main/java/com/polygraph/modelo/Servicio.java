package com.polygraph.modelo;

import java.time.LocalDate;
import java.time.LocalTime;

public class Servicio {
    private int idServicio;
    private LocalDate fechaSolicitud;
    private LocalTime horaSolicitud;
    private long nitCliente;
    private long cedulaCandidato;
    private int idProceso;
    private String estado;
    private String resultado;

    public Servicio(int idServicio, LocalDate fechaSolicitud, LocalTime horaSolicitud, long nitCliente,
                    long cedulaCandidato, int idProceso, String estado, String resultado) {
        this.idServicio = idServicio;
        this.fechaSolicitud = fechaSolicitud;
        this.horaSolicitud = horaSolicitud;
        this.nitCliente = nitCliente;
        this.cedulaCandidato = cedulaCandidato;
        this.idProceso = idProceso;
        this.estado = estado;
        this.resultado = resultado;
    }

    // Getters
    public int getIdServicio() { return idServicio; }
    public LocalDate getFechaSolicitud() { return fechaSolicitud; }
    public LocalTime getHoraSolicitud() { return horaSolicitud; }
    public long getNitCliente() { return nitCliente; }
    public long getCedulaCandidato() { return cedulaCandidato; }
    public int getIdProceso() { return idProceso; }
    public String getEstado() { return estado; }
    public String getResultado() { return resultado; }

    // Setters (opcionales, pero recomendados)
    public void setIdServicio(int idServicio) { this.idServicio = idServicio; }
    public void setFechaSolicitud(LocalDate fechaSolicitud) { this.fechaSolicitud = fechaSolicitud; }
    public void setHoraSolicitud(LocalTime horaSolicitud) { this.horaSolicitud = horaSolicitud; }
    public void setNitCliente(long nitCliente) { this.nitCliente = nitCliente; }
    public void setCedulaCandidato(long cedulaCandidato) { this.cedulaCandidato = cedulaCandidato; }
    public void setIdProceso(int idProceso) { this.idProceso = idProceso; }
    public void setEstado(String estado) { this.estado = estado; }
    public void setResultado(String resultado) { this.resultado = resultado; }
}