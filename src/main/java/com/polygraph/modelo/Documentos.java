package com.polygraph.modelo;

import java.time.LocalDate;

public class Documentos {
    private int idDocumento;
    private int idServicio;
    private String tipoDocumento;
    private String nombreArchivo;
    private LocalDate fechaCarga;
    private String descripcion;
    private long tamanoArchivo;
    private String estadoDocumento;
    private LocalDate fechaSolicitud;
    private LocalDate fechaRecibido;
    private String habesData;
    private String comunicados;
    private String rutaArchivo;   

    
    public Documentos() {
    }

    // ✅ Constructor completo (opcional, si lo quieres usar)
    public Documentos(int idDocumento, int idServicio, String tipoDocumento, String nombreArchivo,
                      LocalDate fechaCarga, String descripcion, long tamanoArchivo,
                      String estadoDocumento, LocalDate fechaSolicitud, LocalDate fechaRecibido,
                      String habesData, String comunicados, String rutaArchivo) {
        this.idDocumento = idDocumento;
        this.idServicio = idServicio;
        this.tipoDocumento = tipoDocumento;
        this.nombreArchivo = nombreArchivo;
        this.fechaCarga = fechaCarga;
        this.descripcion = descripcion;
        this.tamanoArchivo = tamanoArchivo;
        this.estadoDocumento = estadoDocumento;
        this.fechaSolicitud = fechaSolicitud;
        this.fechaRecibido = fechaRecibido;
        this.habesData = habesData;
        this.comunicados = comunicados;
        this.rutaArchivo = rutaArchivo;
    }

    public int getIdDocumento() {
        return idDocumento;
    }

    public void setIdDocumento(int idDocumento) {
        this.idDocumento = idDocumento;
    }

    public int getIdServicio() {
        return idServicio;
    }

    public void setIdServicio(int idServicio) {
        this.idServicio = idServicio;
    }

    public String getTipoDocumento() {
        return tipoDocumento;
    }

    public void setTipoDocumento(String tipoDocumento) {
        this.tipoDocumento = tipoDocumento;
    }

    public String getNombreArchivo() {
        return nombreArchivo;
    }

    public void setNombreArchivo(String nombreArchivo) {
        this.nombreArchivo = nombreArchivo;
    }

    public LocalDate getFechaCarga() {          
        return fechaCarga;
    }

    public void setFechaCarga(LocalDate fechaCarga) {   
        this.fechaCarga = fechaCarga;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public long getTamanoArchivo() {
        return tamanoArchivo;
    }

    public void setTamanoArchivo(long tamanoArchivo) {
        this.tamanoArchivo = tamanoArchivo;
    }

    public String getEstadoDocumento() {
        return estadoDocumento;
    }

    public void setEstadoDocumento(String estadoDocumento) {
        this.estadoDocumento = estadoDocumento;
    }

    public LocalDate getFechaSolicitud() {
        return fechaSolicitud;
    }

    public void setFechaSolicitud(LocalDate fechaSolicitud) {
        this.fechaSolicitud = fechaSolicitud;
    }

    public LocalDate getFechaRecibido() {
        return fechaRecibido;
    }

    public void setFechaRecibido(LocalDate fechaRecibido) {
        this.fechaRecibido = fechaRecibido;
    }

    public String getHabesData() {
        return habesData;
    }

    public void setHabesData(String habesData) {
        this.habesData = habesData;
    }

    public String getComunicados() {
        return comunicados;
    }

    public void setComunicados(String comunicados) {
        this.comunicados = comunicados;
    }

    public String getRutaArchivo() {          
        return rutaArchivo;
    }

    public void setRutaArchivo(String rutaArchivo) {  
        this.rutaArchivo = rutaArchivo;
    }
}
