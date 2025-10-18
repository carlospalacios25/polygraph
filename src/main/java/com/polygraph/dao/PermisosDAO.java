package com.polygraph.dao;

import com.polygraph.modelo.Permisos;
import com.polygraph.util.ConexionBD;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class PermisosDAO {

    public void insertarPerfile(Permisos permisos) throws SQLException, NoSuchAlgorithmException {
        Connection conn = ConexionBD.getInstancia().getConexion();
        String sql = "INSERT INTO permisos (Nombre_Permiso, Descripcion_Permiso) " +
                     "VALUES (?,?)";
        
        
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, permisos.getNombrePermiso());
            pstmt.setString(2, permisos.getDescripcionPermiso());
            pstmt.executeUpdate();
        }
    }
}