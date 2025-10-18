package com.polygraph.dao;

import com.polygraph.modelo.Perfiles;
import com.polygraph.util.ConexionBD;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class PerfilesDAO {

    public void insertarPerfile(Perfiles perfiles) throws SQLException, NoSuchAlgorithmException {
        Connection conn = ConexionBD.getInstancia().getConexion();
        String sql = "INSERT INTO perfiles (Nombre_Perfil, Descripcion) " +
                     "VALUES (?,?)";
        
        
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, perfiles.getNombrePerfil());
            pstmt.setString(2, perfiles.getDescripcion());
            pstmt.executeUpdate();
        }
    }
}