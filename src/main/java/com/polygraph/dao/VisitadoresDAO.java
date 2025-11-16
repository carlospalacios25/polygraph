package com.polygraph.dao;

import com.polygraph.modelo.Visitadores;
import com.polygraph.util.ConexionBD;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class VisitadoresDAO {

    public void insertarVisitador(Visitadores v) throws SQLException {
        String sql = "INSERT INTO visitadores (Nombre_Visitador, Zonas_Visitador) " +
                     "VALUES (?, ?)";

        try (Connection conn = ConexionBD.getInstancia().getConexion();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, v.getNombreVisitador());
            pstmt.setString(2, v.getZonasVisitador());
            pstmt.executeUpdate();
        }
    }

    public List<Visitadores> obtenerVisitadores() throws SQLException {
        List<Visitadores> visitadores = new ArrayList<>();
        String sql = "SELECT Id_Visitador, Nombre_Visitador, Zonas_Visitador FROM visitadores";

        try (Connection conn = ConexionBD.getInstancia().getConexion();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                visitadores.add(new Visitadores(
                        rs.getInt("Id_Visitador"),
                        rs.getString("Nombre_Visitador"),
                        rs.getString("Zonas_Visitador")
                ));
            }
        }
        return visitadores;
    }
}