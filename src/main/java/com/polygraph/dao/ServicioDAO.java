package com.polygraph.dao;

import com.polygraph.modelo.Servicio;
import com.polygraph.util.ConexionBD;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class ServicioDAO {

    // INSERTAR SERVICIO
    public void insertarServicio(Servicio servicio) throws SQLException {
        String sql = "INSERT INTO servicios (" +
                "Fecha_Solicitud, Hora_Solicitud, Nit_Cliente, Cedula_Candidato, Id_Proceso" +
                ") VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = ConexionBD.getInstancia().getConexion();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setObject(1, servicio.getFechaSolicitud());
            pstmt.setObject(2, servicio.getHoraSolicitud());
            pstmt.setLong(3, servicio.getNitCliente());
            pstmt.setLong(4, servicio.getCedulaCandidato());
            pstmt.setInt(5, servicio.getIdProceso());

            int filas = pstmt.executeUpdate();
            if (filas > 0) {
                try (ResultSet rs = pstmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        servicio.setIdServicio(rs.getInt(1));
                    }
                }
            }

        } catch (SQLException e) {
            throw new SQLException("Error al insertar servicio: " + e.getMessage(), e);
        }
    }

    // LISTAR TODOS LOS SERVICIOS
    public List<Servicio> listarServicios() throws SQLException {
        List<Servicio> servicios = new ArrayList<>();
        String sql = "SELECT * FROM servicios ORDER BY Fecha_Solicitud DESC, Hora_Solicitud DESC";

        try (Connection conn = ConexionBD.getInstancia().getConexion();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                Servicio s = new Servicio(
                    rs.getInt("Id_Servicio"),
                    rs.getObject("Fecha_Solicitud", LocalDate.class),
                    rs.getObject("Hora_Solicitud", LocalTime.class),
                    rs.getLong("Nit_Cliente"),
                    rs.getLong("Cedula_Candidato"),
                    rs.getInt("Id_Proceso"),
                    rs.getString("Estado"),
                    rs.getString("Resultado")
                );
                servicios.add(s);
            }
        }
        return servicios;
    }

    // OBTENER UN SERVICIO POR ID
    public Servicio obtenerServicio(int idServicio) throws SQLException {
        String sql = "SELECT * FROM servicios WHERE Id_Servicio = ?";

        try (Connection conn = ConexionBD.getInstancia().getConexion();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, idServicio);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return new Servicio(
                        rs.getInt("Id_Servicio"),
                        rs.getObject("Fecha_Solicitud", LocalDate.class),
                        rs.getObject("Hora_Solicitud", LocalTime.class),
                        rs.getLong("Nit_Cliente"),
                        rs.getLong("Cedula_Candidato"),
                        rs.getInt("Id_Proceso"),
                        rs.getString("Estado"),
                        rs.getString("Resultado")
                    );
                }
            }
        }
        return null;
    }

    // ACTUALIZAR SERVICIO (OPCIONAL)
    public void actualizarServicio(Servicio servicio) throws SQLException {
        String sql = "UPDATE servicios SET " +
                "Fecha_Solicitud = ?, Hora_Solicitud = ?, Nit_Cliente = ?, " +
                "Cedula_Candidato = ?, Id_Proceso = ?, Estado = ?, Resultado = ? " +
                "WHERE Id_Servicio = ?";

        try (Connection conn = ConexionBD.getInstancia().getConexion();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setObject(1, servicio.getFechaSolicitud());
            pstmt.setObject(2, servicio.getHoraSolicitud());
            pstmt.setLong(3, servicio.getNitCliente());
            pstmt.setLong(4, servicio.getCedulaCandidato());
            pstmt.setInt(5, servicio.getIdProceso());
            pstmt.setString(6, servicio.getEstado());
            pstmt.setString(7, servicio.getResultado());
            pstmt.setInt(8, servicio.getIdServicio());

            pstmt.executeUpdate();
        }
    }

    // ELIMINAR SERVICIO (OPCIONAL)
    public void eliminarServicio(int idServicio) throws SQLException {
        String sql = "DELETE FROM servicios WHERE Id_Servicio = ?";

        try (Connection conn = ConexionBD.getInstancia().getConexion();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, idServicio);
            pstmt.executeUpdate();
        }
    }
}