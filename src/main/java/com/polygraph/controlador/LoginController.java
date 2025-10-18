package com.polygraph.controlador;

import com.polygraph.dao.UsuarioDAO;
import com.polygraph.modelo.Usuarios;
import com.polygraph.util.ConexionBD;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class LoginController {

    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private TextField passwordVisibleField;
    @FXML private CheckBox showPasswordCheck;
    @FXML private Label errorLabel;

    public void login(ActionEvent event) {
        String username = usernameField.getText();
        String password = passwordField.getText(); // Usamos el PasswordField para la autenticación

        if (authenticate(username, password)) {
            try {
                // Cargar el dashboard (MainView.fxml)
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/polygraph/vista/MainView.fxml"));
                Parent root = loader.load();
                Stage stage = (Stage) usernameField.getScene().getWindow();
                stage.setScene(new Scene(root, 800, 600));
                stage.setTitle("Aplicación Polygraph - Dashboard");
                stage.show();
            } catch (IOException e) {
                errorLabel.setText("Error al cargar el dashboard.");
                e.printStackTrace(); // Para depuración
            }
        } else {
            errorLabel.setText("Usuario o contraseña incorrectos.");
        }
    }

    public void togglePasswordVisibility(ActionEvent event) {
        boolean show = showPasswordCheck.isSelected();
        if (show) {
            passwordVisibleField.setText(passwordField.getText());
            passwordVisibleField.setVisible(true);
            passwordVisibleField.setManaged(true);
            passwordField.setVisible(false);
            passwordField.setManaged(false);
        } else {
            passwordField.setText(passwordVisibleField.getText());
            passwordField.setVisible(true);
            passwordField.setManaged(true);
            passwordVisibleField.setVisible(false);
            passwordVisibleField.setManaged(false);
        }
    }

    private boolean authenticate(String username, String password) {
        Connection conn = ConexionBD.getInstancia().getConexion();
        String sql = "SELECT Contrasena_Usu, Activo_Usu FROM usuarios WHERE Nombre_usuario = ?";
        
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                boolean isActive = rs.getBoolean("Activo_Usu");
                if (!isActive) {
                    errorLabel.setText("La cuenta no está activa.");
                    return false;
                }
                String storedHash = rs.getString("Contrasena_Usu");
                String inputHash = hashPassword(password);
                return storedHash.equals(inputHash);
            } else {
                errorLabel.setText("Usuario no encontrado.");
            }
        } catch (SQLException | NoSuchAlgorithmException e) {
            errorLabel.setText("Error en la base de datos: " + e.getMessage());
            e.printStackTrace(); // Para depuración
        }
        return false;
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