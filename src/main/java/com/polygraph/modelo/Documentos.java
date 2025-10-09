
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
    private LocalDate Fecha_Solicitud;
    private LocalDate Fecha_Recibido;
    private String habesData;
    private String comunicados;

    public Documentos(int idDocumento, int idServicio, String tipoDocumento, String nombreArchivo, LocalDate fechaCarga, String descripcion, long tamanoArchivo, String estadoDocumento, LocalDate Fecha_Solicitud, LocalDate Fecha_Recibido, String habesData, String comunicados) {
        this.idDocumento = idDocumento;
        this.idServicio = idServicio;
        this.tipoDocumento = tipoDocumento;
        this.nombreArchivo = nombreArchivo;
        this.fechaCarga = fechaCarga;
        this.descripcion = descripcion;
        this.tamanoArchivo = tamanoArchivo;
        this.estadoDocumento = estadoDocumento;
        this.Fecha_Solicitud = Fecha_Solicitud;
        this.Fecha_Recibido = Fecha_Recibido;
        this.habesData = habesData;
        this.comunicados = comunicados;
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

    public LocalDate getFecha_Carga() {
        return fechaCarga;
    }

    public void setFecha_Carga(LocalDate fechaCarga) {
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

    public LocalDate getFecha_Solicitud() {
        return Fecha_Solicitud;
    }

    public void setFecha_Solicitud(LocalDate Fecha_Solicitud) {
        this.Fecha_Solicitud = Fecha_Solicitud;
    }

    public LocalDate getFecha_Recibido() {
        return Fecha_Recibido;
    }

    public void setFecha_Recibido(LocalDate Fecha_Recibido) {
        this.Fecha_Recibido = Fecha_Recibido;
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
    
    
}
