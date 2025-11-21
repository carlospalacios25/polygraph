package com.polygraph.dao;

import com.polygraph.modelo.Visitas;
import com.polygraph.util.ConexionBD;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class VisitasDAO {

    public void insertarVisita(Visitas v) throws SQLException {
        String sql = "INSERT INTO visitas (Id_Servicio, Id_Visitador, Tipo_Prueba, Tipo_Visita, " +
                     "Fecha_Solicitud) " +
                     "VALUES (?, ?, ?, ?, ?)";
        try (Connection c = ConexionBD.getInstancia().getConexion();
             PreparedStatement p = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            p.setInt(1, v.getIdServicio());
            p.setInt(2, v.getIdVisitador());
            p.setString(3, v.getTipo_Prueba());
            p.setString(4, v.getTipo_Visita());
            p.setObject(5, v.getFechaSolicitud()); // LocalDate → se mapea automáticamente en drivers modernos

            p.executeUpdate();

            try (ResultSet rs = p.getGeneratedKeys()) {
                if (rs.next()) {
                    v.setIdVisita(rs.getInt(1));
                }
            }
        }
    }

    public void actualizarVisita(Visitas v) throws SQLException {
        String sql = "UPDATE visitas SET " +
                     "Id_Servicio = ?, Id_Visitador = ?, Tipo_Prueba = ?, Tipo_Visita = ?, " +
                     "Fecha_Solicitud = ?, Fecha_Visita = ?, Hora_Visita = ?, " +
                     "Fecha_Envio_Informe = ?, Novedad_Visita = ? " +
                     "WHERE Id_Visita = ?";

        try (Connection c = ConexionBD.getInstancia().getConexion();
             PreparedStatement p = c.prepareStatement(sql)) {

            p.setInt(1, v.getIdServicio());
            p.setInt(2, v.getIdVisitador());
            p.setString(3, v.getTipo_Prueba());
            p.setString(4, v.getTipo_Visita());

            // Fecha_Solicitud (puede ser NOT NULL en BD, pero igual lo hacemos seguro)
            if (v.getFechaSolicitud() != null) {
                p.setDate(5, Date.valueOf(v.getFechaSolicitud()));
            } else {
                p.setNull(5, Types.DATE);
            }

            // Fecha_Visita (OPCIONAL)
            if (v.getFechaVisita() != null) {
                p.setDate(6, Date.valueOf(v.getFechaVisita()));
            } else {
                p.setNull(6, Types.DATE);
            }

            // Hora_Visita (OPCIONAL)
            if (v.getHoraVisita() != null) {
                p.setTime(7, Time.valueOf(v.getHoraVisita()));
            } else {
                p.setNull(7, Types.TIME);
            }

            // Fecha_Envio_Informe (OPCIONAL)
            if (v.getFechaeInforme() != null) {
                p.setDate(8, Date.valueOf(v.getFechaeInforme()));
            } else {
                p.setNull(8, Types.DATE);
            }

            // Novedad_Visita (OPCIONAL)
            if (v.getNovedadVisita() != null && !v.getNovedadVisita().isBlank()) {
                p.setString(9, v.getNovedadVisita());
            } else {
                p.setNull(9, Types.VARCHAR);
            }

            p.setInt(10, v.getIdVisita());

            p.executeUpdate();
        }
    }

    public List<Visitas> listarVisitas() throws SQLException {
        List<Visitas> lista = new ArrayList<>();
        String sql = """
            SELECT v.*, s.Nombre_Servicio, vis.Nombre_Visitador 
            FROM visitas v
            LEFT JOIN servicios s ON v.Id_Servicio = s.Id_Servicio
            LEFT JOIN visitadores vis ON v.Id_Visitador = vis.Id_Visitador
            ORDER BY v.Fecha_Visita DESC
            """;
        try (Connection c = ConexionBD.getInstancia().getConexion();
             PreparedStatement p = c.prepareStatement(sql);
             ResultSet rs = p.executeQuery()) {

            while (rs.next()) {
                Visitas v = new Visitas(
                    rs.getInt("Id_Visita"),
                    rs.getInt("Id_Servicio"),
                    rs.getInt("Id_Visitador"),
                    rs.getString("Tipo_Prueba"),
                    rs.getString("Tipo_Visita"),
                    toLocalDate(rs.getDate("Fecha_Solicitud")),
                    toLocalDate(rs.getDate("Fecha_Visita")),
                    toLocalTime(rs.getTime("Hora_Visita")),
                    toLocalDate(rs.getDate("Fecha_Envio_Informe")),
                    rs.getString("Novedad_Visita")
                );
                v.setNombreVisitador(rs.getString("Nombre_Visitador"));
                lista.add(v);
            }
        }
        return lista;
    }

    public Visitas obtenerVisitaPorServicio(int idServicio) throws SQLException {
        String sql = "SELECT v.Id_Visita, v.Id_Servicio, v.Id_Visitador, " +
                     " v.Tipo_Prueba, v.Tipo_Visita, " +
                     " v.Fecha_Solicitud, v.Fecha_Visita, v.Hora_Visita, " +
                     " v.Fecha_Envio_Informe, v.Novedad_Visita, " +
                     " vis.Nombre_Visitador " +
                     "FROM visitas AS v " +
                     "JOIN visitadores AS vis ON v.Id_Visitador = vis.Id_Visitador " +
                     "WHERE v.Id_Servicio = ? " +
                     "ORDER BY v.Fecha_Visita DESC " +
                     "LIMIT 1";

        try (Connection c = ConexionBD.getInstancia().getConexion();
             PreparedStatement p = c.prepareStatement(sql)) {

            p.setInt(1, idServicio);
            try (ResultSet rs = p.executeQuery()) {
                if (rs.next()) {
                    Visitas v = new Visitas(
                        rs.getInt("Id_Visita"),
                        rs.getInt("Id_Servicio"),
                        rs.getInt("Id_Visitador"),
                        rs.getString("Tipo_Prueba"),
                        rs.getString("Tipo_Visita"),
                        toLocalDate(rs.getDate("Fecha_Solicitud")),
                        toLocalDate(rs.getDate("Fecha_Visita")),
                        toLocalTime(rs.getTime("Hora_Visita")),
                        toLocalDate(rs.getDate("Fecha_Envio_Informe")),
                        rs.getString("Novedad_Visita")
                    );
                    v.setNombreVisitador(rs.getString("Nombre_Visitador"));
                    return v;
                }
            }
        }
        return null;
    }

    // Convierte java.sql.Date a LocalDate de forma segura
    private LocalDate toLocalDate(java.sql.Date sqlDate) {
        return sqlDate != null ? sqlDate.toLocalDate() : null;
    }

    // Convierte java.sql.Time a LocalTime de forma segura
    private LocalTime toLocalTime(java.sql.Time sqlTime) {
        return sqlTime != null ? sqlTime.toLocalTime() : null;
    }
}
