package com.polygraph.dao;

import com.polygraph.modelo.Analisis;
import com.polygraph.util.ConexionBD;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AnalisisDAO {

    public List<Analisis> listarPorServicio(int idServicio) throws SQLException {
        List<Analisis> lista = new ArrayList<>();
        String sql = """
            SELECT Id_Analisis, Id_Servicio, Tipo_Analisis, Contenido
            FROM analisis
            WHERE Id_Servicio = ?
            ORDER BY Id_Analisis
            """;
        try (Connection conn = ConexionBD.getInstancia().getConexion();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idServicio);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Analisis a = new Analisis();
                    a.setIdAnalisis(rs.getInt("Id_Analisis"));
                    a.setIdServicio(rs.getInt("Id_Servicio"));
                    a.setTipoAnalisis(rs.getString("Tipo_Analisis"));
                    a.setContenido(rs.getString("Contenido"));
                    lista.add(a);
                }
            }
        }
        return lista;
    }

    public void insertar(Analisis analisis) throws SQLException {
        String sql = """
            INSERT INTO analisis (Id_Servicio, Tipo_Analisis, Contenido)
            VALUES (?, ?, ?)
            """;
        try (Connection conn = ConexionBD.getInstancia().getConexion();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, analisis.getIdServicio());
            ps.setString(2, analisis.getTipoAnalisis());
            ps.setString(3, analisis.getContenido());
            ps.executeUpdate();
        }
    }
}