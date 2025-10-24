package com.polygraph.dao;

import com.polygraph.modelo.Ciudades;
import com.polygraph.util.ConexionBD;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CiudadesDAO {

   /* public void insertarPerfile(Ciudades ciudades) throws SQLException, NoSuchAlgorithmException {
        Connection conn = ConexionBD.getInstancia().getConexion();
            String sql = "INSERT INTO perfiles (Nombre_Perfil, Descripcion) " +
                     "VALUES (?,?)";
        
        
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, ciudades.getNombrePerfil());
            pstmt.setString(2, ciudades.getDescripcion());
            pstmt.executeUpdate();
        }
    }*/
    
    public List<Ciudades> obtenerCiudades() throws SQLException {
        List<Ciudades> ciudades = new ArrayList<>();
        Connection conn = ConexionBD.getInstancia().getConexion();
        String sql = "SELECT Id_Ciudad, Nombre_Ciudad FROM ciudades";

        try (PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                ciudades.add(new Ciudades(
                    rs.getInt("Id_Ciudad"),
                    rs.getString("Nombre_Ciudad")
                ));
            }
        }
        return ciudades;
    }
}