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
            SELECT p.Id_Progreso, p.Id_Tipo_Progr, p.Fecha_Progr, p.Observacion_Ante,
                   p.Id_Servicio, p.Nombre_usuario, tp.Nombre_Progreso
            FROM progreso p
            JOIN tipos_progreso tp ON p.Id_Tipo_Progr = tp.Id_Tipo_Progreso
            WHERE p.Id_Servicio = ?
            ORDER BY p.Fecha_Progr DESC
            """;
        try (Connection conn = ConexionBD.getInstancia().getConexion();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idServicio);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Progreso p = new Progreso();
                    p.setIdProgreso(rs.getInt("Id_Progreso"));
                    p.setIdTipoProgr(rs.getInt("Id_Tipo_Progr"));
                    Date fecha = rs.getDate("Fecha_Progr");
                    p.setFechaProgr(fecha != null ? fecha.toLocalDate() : null);
                    p.setObservacionAnte(rs.getString("Observacion_Ante"));
                    p.setIdServicio(rs.getInt("Id_Servicio"));
                    p.setNombreUsuario(rs.getString("Nombre_usuario"));
                    p.setTipoProgresoNombre(rs.getString("Nombre_Progreso"));
                    lista.add(p);
                }
            }
        }
        return lista;
    }

    public int obtenerIdTipoPorNombre(String nombreProgreso) throws SQLException {
        String sql = "SELECT Id_Tipo_Progreso FROM tipos_progreso WHERE Nombre_Progreso = ?";
        try (Connection conn = ConexionBD.getInstancia().getConexion();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, nombreProgreso);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("Id_Tipo_Progreso");
                } else {
                    throw new SQLException("No existe tipo de progreso: " + nombreProgreso);
                }
            }
        }
    }

    public void insertar(Progreso p) throws SQLException {
        String sql = """
            INSERT INTO progreso
              (Id_Tipo_Progr, Fecha_Progr, Observacion_Ante, Id_Servicio, Nombre_usuario)
            VALUES (?, ?, ?, ?, ?)
            """;
        try (Connection conn = ConexionBD.getInstancia().getConexion();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, p.getIdTipoProgr());
            ps.setDate(2, p.getFechaProgr() != null ? Date.valueOf(p.getFechaProgr()) : null);
            ps.setString(3, p.getObservacionAnte());
            ps.setInt(4, p.getIdServicio());
            ps.setString(5, p.getNombreUsuario());
            ps.executeUpdate();
        }
    }
}