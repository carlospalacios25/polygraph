package com.polygraph.modelo;

import java.time.LocalDate;

public class Progreso {
    private int idProgreso;
    private int idTipoProgr;
    private LocalDate fechaProgr;
    private String observacionAnte;
    private int idServicio;
    private String nombreUsuario;
    private String tipoProgresoNombre;

    // ✅ Constructor vacío (necesario para usar new Progreso() en DAO/controlador)
    public Progreso() {
    }

    // ✅ Constructor completo (opcional)
    public Progreso(int idProgreso, int idTipoProgr, LocalDate fechaProgr,
                    String observacionAnte, int idServicio,
                    String nombreUsuario, String tipoProgresoNombre) {
        this.idProgreso = idProgreso;
        this.idTipoProgr = idTipoProgr;
        this.fechaProgr = fechaProgr;
        this.observacionAnte = observacionAnte;
        this.idServicio = idServicio;
        this.nombreUsuario = nombreUsuario;
        this.tipoProgresoNombre = tipoProgresoNombre;
    }

    public int getIdProgreso() {
        return idProgreso;
    }

    public void setIdProgreso(int idProgreso) {
        this.idProgreso = idProgreso;
    }

    public int getIdTipoProgr() {
        return idTipoProgr;
    }

    public void setIdTipoProgr(int idTipoProgr) {
        this.idTipoProgr = idTipoProgr;
    }

    public LocalDate getFechaProgr() {
        return fechaProgr;
    }

    public void setFechaProgr(LocalDate fechaProgr) {
        this.fechaProgr = fechaProgr;
    }

    public String getObservacionAnte() {
        return observacionAnte;
    }

    public void setObservacionAnte(String observacionAnte) {
        this.observacionAnte = observacionAnte;
    }

    public int getIdServicio() {
        return idServicio;
    }

    public void setIdServicio(int idServicio) {
        this.idServicio = idServicio;
    }

    public String getNombreUsuario() {
        return nombreUsuario;
    }

    public void setNombreUsuario(String nombreUsuario) {
        this.nombreUsuario = nombreUsuario;
    }

    public String getTipoProgresoNombre() {
        return tipoProgresoNombre;
    }

    public void setTipoProgresoNombre(String tipoProgresoNombre) {
        this.tipoProgresoNombre = tipoProgresoNombre;
    }
}

