
package com.polygraph.modelo;

public class Clientes {
    private long nitCliente;
    private String nombreCliente;
    private String telefonoCliente;
    private String direccionCliente;
    private int idCiudad;
    private String nombreCiudad;

    public Clientes(long nitCliente, String nombreCliente, String telefonoCliente, String direccionCliente, int idCiudad) {
        this.nitCliente = nitCliente;
        this.nombreCliente = nombreCliente;
        this.telefonoCliente = telefonoCliente;
        this.direccionCliente = direccionCliente;
        this.idCiudad = idCiudad;
    }

    public Clientes(long nitCliente, String nombreCliente, String telefonoCliente, String nombreCiudad) {
        this.nitCliente = nitCliente;
        this.nombreCliente = nombreCliente;
        this.telefonoCliente = telefonoCliente;
        this.nombreCiudad = nombreCiudad;
    }

    public Clientes(long nitCliente, String nombreCliente) {
        this.nitCliente = nitCliente;
        this.nombreCliente = nombreCliente;
    }
    
    public long getNitCliente() {
        return nitCliente;
    }

    public void setNitCliente(long nitCliente) {
        this.nitCliente = nitCliente;
    }

    public String getNombreCliente() {
        return nombreCliente;
    }

    public void setNombreCliente(String nombreCliente) {
        this.nombreCliente = nombreCliente;
    }

    public String getTelefonoCliente() {
        return telefonoCliente;
    }

    public void setTelefonoCliente(String telefonoCliente) {
        this.telefonoCliente = telefonoCliente;
    }

    public String getDireccionCliente() {
        return direccionCliente;
    }

    public void setDireccionCliente(String direccionCliente) {
        this.direccionCliente = direccionCliente;
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
