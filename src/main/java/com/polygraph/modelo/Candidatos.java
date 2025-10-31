
package com.polygraph.modelo;

public class Candidatos {
    private long cedulaCandidato;
    private String nombreCandidato;
    private String apellidoCandidato;
    private String telefonoCandidato;
    private String direccionCandidato;
    private int idCiudad;
    private String nombreCiudad;

    public Candidatos(long cedulaCandidato, String nombreCandidato, String apellidoCandidato, String telefonoCandidato, String direccionCandidato, int idCiudad) {
        this.cedulaCandidato = cedulaCandidato;
        this.nombreCandidato = nombreCandidato;
        this.apellidoCandidato = apellidoCandidato;
        this.telefonoCandidato = telefonoCandidato;
        this.direccionCandidato = direccionCandidato;
        this.idCiudad = idCiudad;
    }

    public Candidatos(long cedulaCandidato, String nombreCandidato, String telefonoCandidato, String direccionCandidato, String nombreCiudad) {
        this.cedulaCandidato = cedulaCandidato;
        this.nombreCandidato = nombreCandidato;
        this.telefonoCandidato = telefonoCandidato;
        this.direccionCandidato = direccionCandidato;
        this.nombreCiudad = nombreCiudad;
    }

    public Candidatos(long cedulaCandidato, String nombreCandidato) {
        this.cedulaCandidato = cedulaCandidato;
        this.nombreCandidato = nombreCandidato;
    }
    
    public long getCedulaCandidato() {
        return cedulaCandidato;
    }

    public void setCedulaCandidato(long cedulaCandidato) {
        this.cedulaCandidato = cedulaCandidato;
    }

    public String getNombreCandidato() {
        return nombreCandidato;
    }

    public void setNombreCandidato(String nombreCandidato) {
        this.nombreCandidato = nombreCandidato;
    }

    public String getApellidoCandidato() {
        return apellidoCandidato;
    }

    public void setApellidoCandidato(String apellidoCandidato) {
        this.apellidoCandidato = apellidoCandidato;
    }

    public String getTelefonoCandidato() {
        return telefonoCandidato;
    }

    public void setTelefonoCandidato(String telefonoCandidato) {
        this.telefonoCandidato = telefonoCandidato;
    }

    public String getDireccionCandidato() {
        return direccionCandidato;
    }

    public void setDireccionCandidato(String direccionCandidato) {
        this.direccionCandidato = direccionCandidato;
    }

    public int getIdCiudad() {
        return idCiudad;
    }

    public void setIdCiudad(int idCiudad) {
        this.idCiudad = idCiudad;
    }

    public String getNombreCiudad() {
        return nombreCiudad;
    }

    public void setNombreCiudad(String nombreCiudad) {
        this.nombreCiudad = nombreCiudad;
    }

     
            
}
