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

    public List<Servicio> listarServicios() throws SQLException {
        List<Servicio> servicios = new ArrayList<>();

        String sql = """
            SELECT 
                s.Id_Servicio,
                s.Fecha_Solicitud,
                s.Hora_Solicitud,
                cli.Nombre_Cliente,
                can.Nombre_Candidato,
                can.Apellido_Candidato,
                p.Nombre_Proceso,
                s.Estado,
                s.Resultado
            FROM servicios s
            LEFT JOIN clientes cli     ON s.Nit_Cliente      = cli.Nit_Cliente
            LEFT JOIN candidatos can   ON s.Cedula_Candidato = can.Cedula_Candidato
            LEFT JOIN procesos p       ON s.Id_Proceso       = p.Id_Proceso
            ORDER BY s.Fecha_Solicitud ASC, s.Hora_Solicitud ASC
            """;

        try (Connection conn = ConexionBD.getInstancia().getConexion();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                Servicio s = new Servicio(
                    rs.getInt("Id_Servicio"),
                    rs.getObject("Fecha_Solicitud", LocalDate.class),
                    rs.getObject("Hora_Solicitud", LocalTime.class),
                    rs.getString("Nombre_Cliente"),
                    rs.getString("Nombre_Candidato"),
                    rs.getString("Apellido_Candidato"),
                    rs.getString("Nombre_Proceso"),
                    rs.getString("Estado"),
                    rs.getString("Resultado")
                );
                servicios.add(s);
            }
        }
        return servicios;
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
    
}