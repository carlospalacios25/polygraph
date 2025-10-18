package com.polygraph.dao;

import com.polygraph.modelo.Usuarios;
import com.polygraph.util.ConexionBD;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDate;

public class UsuarioDAO {

    public void insertarUsuario(Usuarios usuario) throws SQLException, NoSuchAlgorithmException {
        Connection conn = ConexionBD.getInstancia().getConexion();
        String sql = "INSERT INTO usuarios (Nombre_Emp1, Apellido_Emp1, Nombre_usuario, Correo_Usu, Contrasena_Usu, Fecha_Creacion, Activo_Usu, Id_perfil) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, usuario.getNombreEmp());
            pstmt.setString(2, usuario.getApellidoEmp());
            pstmt.setString(3, usuario.getNombreusuario());
            pstmt.setString(4, usuario.getCorreoUsu());
            // Hash de contraseña (SHA-256 simple; considera algo más seguro en producción)
            String hashedPassword = hashPassword(usuario.getContrasenaUsu());
            pstmt.setString(5, hashedPassword);
            pstmt.setDate(6, Date.valueOf(LocalDate.now())); // Fecha actual
            pstmt.setInt(7, usuario.isActivoUsu() ? 1 : 0);
            pstmt.setInt(8, usuario.getIdPerfil());
            pstmt.executeUpdate();
        }
    }

    private String hashPassword(String password) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        byte[] hash = md.digest(password.getBytes());
        StringBuilder hexString = new StringBuilder();
        for (byte b : hash) {
            hexString.append(String.format("%02x", b));
        }
        return hexString.toString();
    }
}