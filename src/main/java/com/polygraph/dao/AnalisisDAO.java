package com.polygraph.dao;

import com.polygraph.modelo.Analisis;
import com.polygraph.util.ConexionBD;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AnalisisDAO {

    public List<Analisis> listarPorServicio(int idServicio) throws SQLException {
        List<Analisis> lista = new ArrayList<>();
        String sql = "SELECT Id_Analisis, Tipo_Analisis, Contenido FROM analisis WHERE Id_Servicio = ?";

        try (Connection c = ConexionBD.getInstancia().getConexion();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, idServicio);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Analisis a = new Analisis();
                    a.setIdAnalisis(rs.getInt("Id_Analisis"));
                    a.setTipoAnalisis(rs.getString("Tipo_Analisis"));
                    a.setContenido(rs.getString("Contenido"));
                    a.setIdServicio(idServicio);
                    lista.add(a);
                }
            }
        }
        return lista;
    }

    public void insertar(Analisis a) throws SQLException {
        String sql = "INSERT INTO analisis (Id_Servicio, Tipo_Analisis, Contenido) VALUES (?, ?, ?)";
        try (Connection c = ConexionBD.getInstancia().getConexion();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, a.getIdServicio());
            ps.setString(2, a.getTipoAnalisis());
            ps.setString(3, a.getContenido());
            ps.executeUpdate();
        }
    }
}