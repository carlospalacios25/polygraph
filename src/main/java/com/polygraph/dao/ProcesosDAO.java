package com.polygraph.dao;


import com.polygraph.modelo.Procesos;
import com.polygraph.util.ConexionBD;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ProcesosDAO {

   /* public void insertarPerfile(Ciudades ciudades) throws SQLException, NoSuchAlgorithmException {
        Connection conn = ConexionBD.getInstancia().getConexion();
            String sql = "INSERT INTO ciudades (Nombre_Ciudad) " +
                     "VALUES (?)";
        
        
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, ciudades.getNombreCiudad());
            pstmt.executeUpdate();
        }
    }*/
    
    public List<Procesos> obtenerProcesosBox() throws SQLException {
        List<Procesos> procesos = new ArrayList<>();
        Connection conn = ConexionBD.getInstancia().getConexion();
        String sql = "SELECT Id_Proceso, Nombre_Proceso FROM procesos";

        try (PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                procesos.add(new Procesos(
                    rs.getInt("Id_Proceso"),
                    rs.getString("Nombre_Proceso")
                ));
            }
        }
        return procesos;
    }
    
    // MÉTODO NUEVO: OBTENER UN PROCESO POR ID
    public Procesos obtenerProceso(int idProceso) throws SQLException {
        String sql = "SELECT Id_Proceso, Nombre_Proceso FROM procesos WHERE Id_Proceso = ?";

        try (Connection conn = ConexionBD.getInstancia().getConexion();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, idProceso);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return new Procesos(
                        rs.getInt("Id_Proceso"),
                        rs.getString("Nombre_Proceso")
                    );
                }
            }
        }
        return null; // No encontrado
    }
    
}