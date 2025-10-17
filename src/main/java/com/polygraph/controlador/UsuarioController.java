package com.polygraph.controlador;

import com.polygraph.dao.UsuarioDAO;
import com.polygraph.modelo.Usuarios;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.CheckBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.time.LocalDate;

public class UsuarioController {

    @FXML private TextField nombreField;
    @FXML private TextField apellidoField;
    @FXML private TextField usernameField;
    @FXML private TextField correoField;
    @FXML private PasswordField passwordField;
    @FXML private TextField perfilIdField;
    @FXML private CheckBox activoCheck;

    public void insertarUsuario(ActionEvent event) {
        try {
            Usuarios usuario = new Usuarios(
                nombreField.getText(),
                apellidoField.getText(),
                usernameField.getText(),
                correoField.getText(),
                passwordField.getText(),
                LocalDate.now(), // No editable
                activoCheck.isSelected(),
                Integer.parseInt(perfilIdField.getText())
            );

            UsuarioDAO dao = new UsuarioDAO();
            dao.insertarUsuario(usuario);

            showAlert("Éxito", "Usuario insertado correctamente.");
            clearFields();
        } catch (NumberFormatException e) {
            showAlert("Error", "ID de perfil debe ser un número.");
        } catch (SQLException | NoSuchAlgorithmException e) {
            showAlert("Error", "Error al insertar: " + e.getMessage());
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void clearFields() {
        nombreField.clear();
        apellidoField.clear();
        usernameField.clear();
        correoField.clear();
        passwordField.clear();
        perfilIdField.clear();
        activoCheck.setSelected(true);
    }
}