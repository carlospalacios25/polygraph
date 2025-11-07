package com.polygraph.modelo;

import java.time.LocalDate;
import java.time.LocalTime;

public class Servicio {

    // === CAMPOS PRINCIPALES (siempre presentes) ===
    private int idServicio;
    private LocalDate fechaSolicitud;
    private LocalTime horaSolicitud;
    private String estado;
    private String resultado;

    // === CAMPOS ADICIONALES (tu base de datos) ===
    private String facturacionServicio;
    private String verificacionServicio;
    private String cargoAutofinanciera;
    private String autofinanciera;
    private String empresasServicio;
    private Integer idSucursal;
    private String centroCosto;
    private LocalDate fechaEntregaEstudio;
    private LocalDate fechaEnvio;

    private String nombreCliente;
    private String nombreCandidato;
    private String apellidoCandidato;
    private String nombreProceso;

    private Long nitCliente;        
    private Long cedulaCandidato;
    private Integer idProceso;

    // ===================================================================
    // CONSTRUCTOR PARA LISTAR (con JOIN) - SIN IDs
    // ===================================================================
    public Servicio(int idServicio, LocalDate fechaSolicitud, LocalTime horaSolicitud,
                    String nombreCliente, String nombreCandidato, String apellidoCandidato,
                    String nombreProceso, String estado, String resultado,
                    String facturacionServicio, String verificacionServicio,
                    String cargoAutofinanciera, String autofinanciera,
                    String empresasServicio, Integer idSucursal,
                    String centroCosto, LocalDate fechaEntregaEstudio, LocalDate fechaEnvio) {

        this.idServicio = idServicio;
        this.fechaSolicitud = fechaSolicitud;
        this.horaSolicitud = horaSolicitud;
        this.nombreCliente = nombreCliente;
        this.nombreCandidato = nombreCandidato;
        this.apellidoCandidato = apellidoCandidato;
        this.nombreProceso = nombreProceso;
        this.estado = estado;
        this.resultado = resultado;
        this.facturacionServicio = facturacionServicio;
        this.verificacionServicio = verificacionServicio;
        this.cargoAutofinanciera = cargoAutofinanciera;
        this.autofinanciera = autofinanciera;
        this.empresasServicio = empresasServicio;
        this.idSucursal = idSucursal;
        this.centroCosto = centroCosto;
        this.fechaEntregaEstudio = fechaEntregaEstudio;
        this.fechaEnvio = fechaEnvio;
    }

    public Servicio(LocalDate fechaSolicitud, LocalTime horaSolicitud, Long nitCliente, Long cedulaCandidato, Integer idProceso) {
        this.fechaSolicitud = fechaSolicitud;
        this.horaSolicitud = horaSolicitud;
        this.nitCliente = nitCliente;
        this.cedulaCandidato = cedulaCandidato;
        this.idProceso =  idProceso;
    }

    public Servicio(int idServicio, LocalDate fechaSolicitud, LocalTime horaSolicitud, String nombreCliente, String nombreCandidato, String apellidoCandidato, String nombreProceso,String estado, String resultado) {
        this.idServicio = idServicio;
        this.fechaSolicitud = fechaSolicitud;
        this.horaSolicitud = horaSolicitud;
        this.estado = estado;
        this.resultado = resultado;
        this.nombreCliente = nombreCliente;
        this.nombreCandidato = nombreCandidato;
        this.apellidoCandidato = apellidoCandidato;
        this.nombreProceso = nombreProceso;
    }

    

    // --- Básicos ---
    public int getIdServicio() { return idServicio; }
    public void setIdServicio(int idServicio) { this.idServicio = idServicio; }

    public LocalDate getFechaSolicitud() { return fechaSolicitud; }
    public void setFechaSolicitud(LocalDate fechaSolicitud) { this.fechaSolicitud = fechaSolicitud; }

    public LocalTime getHoraSolicitud() { return horaSolicitud; }
    public void setHoraSolicitud(LocalTime horaSolicitud) { this.horaSolicitud = horaSolicitud; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public String getResultado() { return resultado; }
    public void setResultado(String resultado) { this.resultado = resultado; }

    // --- Nombres (para mostrar) ---
    public String getNombreCliente() { return nombreCliente; }
    public void setNombreCliente(String nombreCliente) { this.nombreCliente = nombreCliente; }

    public String getNombreCandidato() { return nombreCandidato; }
    public void setNombreCandidato(String nombreCandidato) { this.nombreCandidato = nombreCandidato; }

    public String getApellidoCandidato() { return apellidoCandidato; }
    public void setApellidoCandidato(String apellidoCandidato) { this.apellidoCandidato = apellidoCandidato; }

    public String getNombreProceso() { return nombreProceso; }
    public void setNombreProceso(String nombreProceso) { this.nombreProceso = nombreProceso; }

    // --- Campos adicionales ---
    public String getFacturacionServicio() { return facturacionServicio; }
    public void setFacturacionServicio(String facturacionServicio) { this.facturacionServicio = facturacionServicio; }

    public String getVerificacionServicio() { return verificacionServicio; }
    public void setVerificacionServicio(String verificacionServicio) { this.verificacionServicio = verificacionServicio; }

    public String getCargoAutofinanciera() { return cargoAutofinanciera; }
    public void setCargoAutofinanciera(String cargoAutofinanciera) { this.cargoAutofinanciera = cargoAutofinanciera; }

    public String getAutofinanciera() { return autofinanciera; }
    public void setAutofinanciera(String autofinanciera) { this.autofinanciera = autofinanciera; }

    public String getEmpresasServicio() { return empresasServicio; }
    public void setEmpresasServicio(String empresasServicio) { this.empresasServicio = empresasServicio; }

    public Integer getIdSucursal() { return idSucursal; }
    public void setIdSucursal(Integer idSucursal) { this.idSucursal = idSucursal; }

    public String getCentroCosto() { return centroCosto; }
    public void setCentroCosto(String centroCosto) { this.centroCosto = centroCosto; }

    public LocalDate getFechaEntregaEstudio() { return fechaEntregaEstudio; }
    public void setFechaEntregaEstudio(LocalDate fechaEntregaEstudio) { this.fechaEntregaEstudio = fechaEntregaEstudio; }

    public LocalDate getFechaEnvio() { return fechaEnvio; }
    public void setFechaEnvio(LocalDate fechaEnvio) { this.fechaEnvio = fechaEnvio; }

    public Long getNitCliente() {return nitCliente;}
    public void setNitCliente(Long nitCliente) {this.nitCliente = nitCliente;}

    public Long getCedulaCandidato() {return cedulaCandidato;}
    public void setCedulaCandidato(Long cedulaCandidato) {this.cedulaCandidato = cedulaCandidato;}

    public Integer getIdProceso() {return idProceso;}
    public void setIdProceso(Integer idProceso) {this.idProceso = idProceso;}
    
    
}