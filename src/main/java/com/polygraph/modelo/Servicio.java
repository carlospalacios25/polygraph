package com.polygraph.modelo;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Objects;

public class Servicio {

    // ==================== CAMPOS PRINCIPALES ====================
    private int idServicio;
    private LocalDate fechaSolicitud;
    private LocalTime horaSolicitud;
    private String estado = "Pendiente";  // valor por defecto
    private String resultado;

    // ==================== RELACIONES (IDs) ====================
    private Long nitCliente;
    private Long cedulaCandidato;
    private Integer idProceso;

    // ==================== DATOS DESNORMALIZADOS (para mostrar sin JOIN) ====================
    private String nombreCliente;
    private String nombreCandidato;
    private String apellidoCandidato;
    private String telefonoCandidato;
    private String direccionCandidato;
    private String nombreCiudad;
    private String nombreProceso;

    // ==================== CAMPOS ADICIONALES DEL SERVICIO ====================
    private String facturacionServicio;
    private String verificacionServicio;
    private String cargoAutofinanciera;
    private String autofinanciera;
    private String empresasServicio;
    private Integer idSucursal;
    private String centroCosto;
    private LocalDate fechaEntregaEstudio;
    private LocalDate fechaEnvio;

    // ===================================================================
    // CONSTRUCTORES
    // ===================================================================

    // Constructor vacío (OBLIGATORIO para JavaFX y cuando creas objetos nuevos)
    public Servicio() {}

    // Constructor mínimo para crear un servicio nuevo
    public Servicio(LocalDate fechaSolicitud, LocalTime horaSolicitud,
                    Long nitCliente, Long cedulaCandidato, Integer idProceso) {
        this.fechaSolicitud = fechaSolicitud;
        this.horaSolicitud = horaSolicitud;
        this.nitCliente = nitCliente;
        this.cedulaCandidato = cedulaCandidato;
        this.idProceso = idProceso;
        this.estado = "Pendiente";
    }

    // Constructor completo para cuando haces JOIN en la lista/tarjetas
    public Servicio(int idServicio, LocalDate fechaSolicitud, LocalTime horaSolicitud,
                    String nombreCliente, String nombreCandidato, String apellidoCandidato,
                    String nombreProceso, String estado, String resultado) {
        this.idServicio = idServicio;
        this.fechaSolicitud = fechaSolicitud;
        this.horaSolicitud = horaSolicitud;
        this.nombreCliente = nombreCliente;
        this.nombreCandidato = nombreCandidato;
        this.apellidoCandidato = apellidoCandidato;
        this.nombreProceso = nombreProceso;
        this.estado = estado != null ? estado : "Pendiente";
        this.resultado = resultado;
    }

    // ===================================================================
    // GETTERS Y SETTERS (ordenados y limpios)
    // ===================================================================

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

    // --- Datos del Cliente ---
    public Long getNitCliente() { return nitCliente; }
    public void setNitCliente(Long nitCliente) { this.nitCliente = nitCliente; }

    public String getNombreCliente() { return nombreCliente; }
    public void setNombreCliente(String nombreCliente) { this.nombreCliente = nombreCliente; }

    // --- Datos del Candidato ---
    public Long getCedulaCandidato() { return cedulaCandidato; }
    public void setCedulaCandidato(Long cedulaCandidato) { this.cedulaCandidato = cedulaCandidato; }

    public String getNombreCandidato() { return nombreCandidato; }
    public void setNombreCandidato(String nombreCandidato) { this.nombreCandidato = nombreCandidato; }

    public String getApellidoCandidato() { return apellidoCandidato; }
    public void setApellidoCandidato(String apellidoCandidato) { this.apellidoCandidato = apellidoCandidato; }

    public String getTelefonoCandidato() { return telefonoCandidato; }
    public void setTelefonoCandidato(String telefonoCandidato) { this.telefonoCandidato = telefonoCandidato; }

    public String getDireccionCandidato() { return direccionCandidato; }
    public void setDireccionCandidato(String direccionCandidato) { this.direccionCandidato = direccionCandidato; }

    public String getNombreCiudad() { return nombreCiudad; }
    public void setNombreCiudad(String nombreCiudad) { this.nombreCiudad = nombreCiudad; }

    // --- Proceso ---
    public Integer getIdProceso() { return idProceso; }
    public void setIdProceso(Integer idProceso) { this.idProceso = idProceso; }

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

    // ===================================================================
    // MÉTODOS ÚTILES
    // ===================================================================

    public String getNombreCompletoCandidato() {
        return (nombreCandidato != null ? nombreCandidato : "") +
               (apellidoCandidato != null ? " " + apellidoCandidato : "");
    }

    @Override
    public String toString() {
        return "Servicio #" + idServicio + " - " + getNombreCompletoCandidato() + " (" + nombreCliente + ")";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Servicio servicio = (Servicio) o;
        return idServicio == servicio.idServicio;
    }

    @Override
    public int hashCode() {
        return Objects.hash(idServicio);
    }
}