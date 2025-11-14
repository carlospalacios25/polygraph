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
import javafx.stage.Screen;
import javafx.stage.Stage;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;

public class LoginController {

    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private TextField passwordVisibleField;
    @FXML private CheckBox showPasswordCheck;
    @FXML private Label errorLabel;
    
    private double xOffset = 0;
    private double yOffset = 0;

    
    @FXML
    public void login(ActionEvent event) {
        String username = usernameField.getText();
        String password = passwordField.isVisible() ? passwordField.getText() : passwordVisibleField.getText();

        if (authenticate(username, password)) {
            try {
                // CARGAR MAIN
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/polygraph/vista/MainView.fxml"));
                Parent root = loader.load();

                // OBTENER CONTROLADOR
                MainController mainController = loader.getController();

                // GUARDAR EN USERDATA (ESTO ES TODO!)
                root.setUserData(mainController);

                // PANTALLA
                double screenWidth = Screen.getPrimary().getVisualBounds().getWidth();
                double screenHeight = Screen.getPrimary().getVisualBounds().getHeight();

                Stage mainStage = new Stage();
                mainStage.setTitle("Aplicación Polygraph - Dashboard");
                mainStage.setScene(new Scene(root, screenWidth * 0.9, screenHeight * 0.9));
                mainStage.show();

                // CERRAR LOGIN
                Stage loginStage = (Stage) usernameField.getScene().getWindow();
                loginStage.close();

            } catch (IOException e) {
                errorLabel.setText("Error al cargar el dashboard: " + e.getMessage());
                e.printStackTrace();
            }
        } else {
            errorLabel.setText("Usuario o contraseña incorrectos.");
        }
    }
    
    @FXML
    private void iniciarArrastre(MouseEvent event) {
        xOffset = event.getSceneX();
        yOffset = event.getSceneY();
    }

    @FXML
    private void arrastrarVentana(MouseEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setX(event.getScreenX() - xOffset);
        stage.setY(event.getScreenY() - yOffset);
    }
    
    @FXML
    private void cerrar(ActionEvent event) {
        Stage stage = (Stage) usernameField.getScene().getWindow();
        stage.close();
    }

    @FXML
    private void cancelar(ActionEvent event) {
        usernameField.clear();
        passwordField.clear();
        passwordVisibleField.clear();
        showPasswordCheck.setSelected(false);
        errorLabel.setText("");
    }

    @FXML
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
            e.printStackTrace();
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