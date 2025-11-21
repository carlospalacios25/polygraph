package com.polygraph.dao;

import com.polygraph.modelo.Poligrafias;
import com.polygraph.util.ConexionBD;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class PoligrafiasDAO {

    public void insertarPoligrafia(Poligrafias p) throws SQLException {
        String sql = "INSERT INTO poligrafias " +
                "(Id_Servicio, Id_Poligrafista, Fecha_Asignacion, Hora_Programacion, Asistencia, Fecha_Entrega) " +
                "VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection c = ConexionBD.getInstancia().getConexion();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, p.getIdServicio());
            ps.setInt(2, p.getIdPoligrafista());

            // Fecha_Asignacion (obligatoria en tu validación, pero igual null-safe)
            if (p.getFechaAsignacion() != null) {
                ps.setDate(3, Date.valueOf(p.getFechaAsignacion()));
            } else {
                ps.setNull(3, Types.DATE);
            }

            // Hora_Programacion (obligatoria en tu validación, pero null-safe)
            if (p.getHoraProgramacion() != null) {
                ps.setTime(4, Time.valueOf(p.getHoraProgramacion()));
            } else {
                ps.setNull(4, Types.TIME);
            }

            // Asistencia (opcional)
            if (p.getAsistencia() != null && !p.getAsistencia().isBlank()) {
                ps.setString(5, p.getAsistencia());
            } else {
                ps.setNull(5, Types.VARCHAR);
            }

            // Fecha_Entrega (opcional)
            if (p.getFechaEntrega() != null) {
                ps.setDate(6, Date.valueOf(p.getFechaEntrega()));
            } else {
                ps.setNull(6, Types.DATE);
            }

            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    p.setIdPoligrafia(rs.getInt(1));
                }
            }
        }
    }

    public void actualizarPoligrafia(Poligrafias p) throws SQLException {
        String sql = "UPDATE poligrafias SET " +
                "Id_Servicio = ?, Id_Poligrafista = ?, Fecha_Asignacion = ?, " +
                "Hora_Programacion = ?, Asistencia = ?, Fecha_Entrega = ? " +
                "WHERE Id_Poligrafia = ?";

        try (Connection c = ConexionBD.getInstancia().getConexion();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, p.getIdServicio());
            ps.setInt(2, p.getIdPoligrafista());

            // Fecha_Asignacion
            if (p.getFechaAsignacion() != null) {
                ps.setDate(3, Date.valueOf(p.getFechaAsignacion()));
            } else {
                ps.setNull(3, Types.DATE);
            }

            // Hora_Programacion
            if (p.getHoraProgramacion() != null) {
                ps.setTime(4, Time.valueOf(p.getHoraProgramacion()));
            } else {
                ps.setNull(4, Types.TIME);
            }

            // Asistencia (opcional)
            if (p.getAsistencia() != null && !p.getAsistencia().isBlank()) {
                ps.setString(5, p.getAsistencia());
            } else {
                ps.setNull(5, Types.VARCHAR);
            }

            // Fecha_Entrega (opcional)
            if (p.getFechaEntrega() != null) {
                ps.setDate(6, Date.valueOf(p.getFechaEntrega()));
            } else {
                ps.setNull(6, Types.DATE);
            }

            ps.setInt(7, p.getIdPoligrafia());

            ps.executeUpdate();
        }
    }

    public List<Poligrafias> listarPoligrafias() throws SQLException {
        List<Poligrafias> lista = new ArrayList<>();

        String sql = """
                SELECT Id_Poligrafia,
                       Id_Servicio,
                       Id_Poligrafista,
                       Fecha_Asignacion,
                       Hora_Programacion,
                       Asistencia,
                       Fecha_Entrega
                FROM poligrafias
                ORDER BY Fecha_Asignacion DESC, Hora_Programacion DESC
                """;

        try (Connection c = ConexionBD.getInstancia().getConexion();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Poligrafias p = new Poligrafias(
                    rs.getInt("Id_Poligrafia"),
                    rs.getInt("Id_Servicio"),
                    rs.getInt("Id_Poligrafista"),
                    toLocalDate(rs.getDate("Fecha_Asignacion")),
                    toLocalTime(rs.getTime("Hora_Programacion")),
                    rs.getString("Asistencia"),
                    toLocalDate(rs.getDate("Fecha_Entrega"))
                );
                lista.add(p);
            }
        }
        return lista;
    }

    public Poligrafias obtenerPoligrafiaPorServicio(int idServicio) throws SQLException {
        String sql = """
                SELECT  p.Id_Poligrafia,
                        p.Id_Servicio,
                        p.Id_Poligrafista,
                        p.Fecha_Asignacion,
                        p.Hora_Programacion,
                        p.Asistencia,
                        p.Fecha_Entrega,
                        po.Nombre_Poligrafista
                FROM poligrafias p
                LEFT JOIN poligrafistas po ON p.Id_Poligrafista = po.Id_Poligrafista
                WHERE p.Id_Servicio = ?
                ORDER BY p.Fecha_Asignacion DESC, p.Hora_Programacion DESC
                LIMIT 1
                """;

        try (Connection c = ConexionBD.getInstancia().getConexion();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, idServicio);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Poligrafias p = new Poligrafias(
                            rs.getInt("Id_Poligrafia"),
                            rs.getInt("Id_Servicio"),
                            rs.getInt("Id_Poligrafista"),
                            toLocalDate(rs.getDate("Fecha_Asignacion")),
                            toLocalTime(rs.getTime("Hora_Programacion")),
                            rs.getString("Asistencia"),
                            toLocalDate(rs.getDate("Fecha_Entrega"))
                    );
                    p.setNombrePoligrafista(rs.getString("Nombre_Poligrafista"));
                    return p;
                }
            }
        }
        return null;
    }

    /**
     * Valida si existe otra poligrafía para el mismo poligrafista y fecha
     * con menos de 60 minutos de diferencia.
     */
    public boolean hayConflictoHorario(int idPoligrafista,
                                       LocalDate fecha,
                                       LocalTime hora,
                                       int idPoligrafiaActual) throws SQLException {

        // ✅ Si por alguna razón llega fecha u hora null, no hacemos la validación (evitamos Date.valueOf(null))
        if (fecha == null || hora == null) {
            return false;
        }

        String sql = """
                SELECT COUNT(*) AS total
                FROM poligrafias
                WHERE Id_Poligrafista = ?
                  AND Fecha_Asignacion = ?
                  AND Id_Poligrafia <> ?
                  AND ABS(TIMESTAMPDIFF(
                        MINUTE,
                        CONCAT(Fecha_Asignacion, ' ', Hora_Programacion),
                        CONCAT(?, ' ', ?)
                  )) < 60
                """;

        try (Connection c = ConexionBD.getInstancia().getConexion();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, idPoligrafista);
            ps.setDate(2, Date.valueOf(fecha));
            ps.setInt(3, idPoligrafiaActual);
            ps.setDate(4, Date.valueOf(fecha));
            ps.setTime(5, Time.valueOf(hora));

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("total") > 0;
                }
            }
        }
        return false;
    }

    private LocalDate toLocalDate(java.sql.Date d) {
        return d != null ? d.toLocalDate() : null;
    }

    private LocalTime toLocalTime(java.sql.Time t) {
        return t != null ? t.toLocalTime() : null;
    }
}