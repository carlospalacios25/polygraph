
package com.polygraph.modelo;

public class Sucursales {
    private int idSucursal;
    private String nombreSucursal;
    private int idCiudad;

    public Sucursales(int idSucursal, String nombreSucursal, int idCiudad) {
        this.idSucursal = idSucursal;
        this.nombreSucursal = nombreSucursal;
        this.idCiudad = idCiudad;
    }

    public int getIdSucursal() {
        return idSucursal;
    }

    public void setIdSucursal(int idSucursal) {
        this.idSucursal = idSucursal;
    }

    public String getNombreSucursal() {
        return nombreSucursal;
    }

    public void setNombreSucursal(String nombreSucursal) {
        this.nombreSucursal = nombreSucursal;
    }

    public int getIdCiudad() {
        return idCiudad;
    }

    public void setIdCiudad(int idCiudad) {
        this.idCiudad = idCiudad;
    }
    
    
}
