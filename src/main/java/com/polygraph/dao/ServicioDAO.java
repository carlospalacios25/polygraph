package com.polygraph.dao;

import com.polygraph.modelo.Servicio;
import com.polygraph.util.ConexionBD;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class ServicioDAO {
    private final Connection conexion;
    
    public ServicioDAO() {
        this.conexion = ConexionBD.getInstancia().getConexion();
    }
    
    public List<Servicio> obtenerTodosServicios() {
        List<Servicio> servicios = new ArrayList<>();
        String sql = "SELECT * FROM servicios";
        
        try (PreparedStatement stmt = conexion.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                Servicio servicio = new Servicio(
                    rs.getInt("Id_Servicio"),
                    rs.getDate("Fecha_Solicitud").toLocalDate(),
                    rs.getTime("Hora_Solicitud").toLocalTime(),
                    rs.getLong("Nit_Cliente"),
                    rs.getLong("Cedula_Candidato"),
                    rs.getInt("Id_Proceso"),
                    rs.getString("Estado"),
                    rs.getString("Resultado")
                );
                servicios.add(servicio);
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener servicios: " + e.getMessage());
        }
        return servicios;
    }
}
