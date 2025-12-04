package com.polygraph.dao;

import com.polygraph.modelo.Progreso;
import com.polygraph.util.ConexionBD;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ProgresoDAO {

    public List<Progreso> listarPorServicio(int idServicio) throws SQLException {
        List<Progreso> lista = new ArrayList<>();
        String sql = """
            SELECT p.Id_Progreso, p.Fecha_Progr, p.Observacion_Ante, 
                   p.Nombre_usuario, tp.Nombre_Progreso
            FROM progreso p
            JOIN tipos_progreso tp ON p.Id_Tipo_Progr = tp.Id_Tipo_Progreso
            WHERE p.Id_Servicio = ?
            ORDER BY p.Fecha_Progr DESC
            """;

        try (Connection c = ConexionBD.getInstancia().getConexion();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, idServicio);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Progreso p = new Progreso();
                    p.setIdProgreso(rs.getInt("Id_Progreso"));
                    p.setFechaProgr(rs.getDate("Fecha_Progr").toLocalDate());
                    p.setObservacionAnte(rs.getString("Observacion_Ante"));
                    p.setNombreUsuario(rs.getString("Nombre_usuario"));
                    p.setTipoProgresoNombre(rs.getString("Nombre_Progreso"));
                    p.setIdServicio(idServicio);
                    lista.add(p);
                }
            }
        }
        return lista;
    }

    public int obtenerIdTipoPorNombre(String nombre) throws SQLException {
        String sql = "SELECT Id_Tipo_Progreso FROM tipos_progreso WHERE Nombre_Progreso = ?";
        try (Connection c = ConexionBD.getInstancia().getConexion();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, nombre);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1);
                throw new SQLException("Tipo de progreso no encontrado: " + nombre);
            }
        }
    }

    public void insertar(Progreso p) throws SQLException {
        String sql = """
            INSERT INTO progreso (Id_Tipo_Progr, Fecha_Progr, Observacion_Ante, Id_Servicio, Nombre_usuario)
            VALUES (?, ?, ?, ?, ?)
            """;
        try (Connection c = ConexionBD.getInstancia().getConexion();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, p.getIdTipoProgr());
            ps.setDate(2, Date.valueOf(p.getFechaProgr()));
            ps.setString(3, p.getObservacionAnte());
            ps.setInt(4, p.getIdServicio());
            ps.setString(5, p.getNombreUsuario());
            ps.executeUpdate();
        }
    }
    
    // === NUEVO MÉTODO EN ProgresoDAO.java ===
    public List<String> listarNombresTiposProgreso(int idServicio) throws SQLException {
        List<String> lista = new ArrayList<>();
        String sql = """
            SELECT tp.Nombre_Progreso
            FROM servicios s
            JOIN procesos p ON s.Id_Proceso = p.Id_Proceso
            JOIN proceso_tipos_progreso ptp ON p.Id_Proceso = ptp.Id_Proceso
            JOIN tipos_progreso tp ON ptp.Id_Tipo_Progreso = tp.Id_Tipo_Progreso
            WHERE ptp.habilitado = TRUE
              AND s.Id_Servicio = ?
            ORDER BY tp.Nombre_Progreso
            """;

        try (Connection c = ConexionBD.getInstancia().getConexion();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, idServicio);  // AQUÍ ESTABA EL ERROR CLAVE

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(rs.getString("Nombre_Progreso")); // o rs.getString(1)
                }
            }
        }
        return lista;
    }
}