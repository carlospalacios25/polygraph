package com.polygraph.dao;

import com.polygraph.modelo.TiposProgreso;
import com.polygraph.util.ConexionBD;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TiposProgresoDAO {

    public void insertar(TiposProgreso tipo) throws SQLException {
        String sql = "INSERT INTO tipos_progreso (Nombre_Progreso) VALUES (?)";
        try (Connection conn = ConexionBD.getInstancia().getConexion();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, tipo.getNombreProgreso());
            pstmt.executeUpdate();
        }
    }

    public List<TiposProgreso> obtenerTodos() throws SQLException {
        List<TiposProgreso> lista = new ArrayList<>();
        String sql = "SELECT Id_Tipo_Progreso, Nombre_Progreso FROM tipos_progreso ORDER BY Nombre_Progreso";

        try (Connection conn = ConexionBD.getInstancia().getConexion();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                lista.add(new TiposProgreso(
                    rs.getInt("Id_Tipo_Progreso"),
                    rs.getString("Nombre_Progreso")
                ));
            }
        }
        return lista;
    }

    public TiposProgreso obtenerPorId(int id) throws SQLException {
        String sql = "SELECT Id_Tipo_Progreso, Nombre_Progreso FROM tipos_progreso WHERE Id_Tipo_Progreso = ?";
        try (Connection conn = ConexionBD.getInstancia().getConexion();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return new TiposProgreso(rs.getInt("Id_Tipo_Progreso"), rs.getString("Nombre_Progreso"));
                }
            }
        }
        return null;
    }
}