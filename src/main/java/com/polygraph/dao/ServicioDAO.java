package com.polygraph.dao;

import com.polygraph.modelo.Servicio;
import com.polygraph.util.ConexionBD;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class ServicioDAO {

    public void insertarServicio(Servicio servicio) throws SQLException {
        String sql = "INSERT INTO servicios (" +
                "Fecha_Solicitud, Hora_Solicitud, Nit_Cliente, Cedula_Candidato, Id_Proceso" +
                ") VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = ConexionBD.getInstancia().getConexion();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setObject(1, servicio.getFechaSolicitud());
            pstmt.setObject(2, servicio.getHoraSolicitud());
            pstmt.setLong(3, servicio.getNitCliente());
            pstmt.setLong(4, servicio.getCedulaCandidato());
            pstmt.setInt(5, servicio.getIdProceso());
            pstmt.executeUpdate();

        } catch (SQLException e) {
            // Re-lanzar con mensaje claro
            throw new SQLException("Error al insertar servicio: " + e.getMessage(), e);
        }
    }
}
