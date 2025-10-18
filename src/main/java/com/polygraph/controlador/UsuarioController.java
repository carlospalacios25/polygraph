package com.polygraph.controlador;

import com.polygraph.dao.UsuarioDAO;
import com.polygraph.modelo.Usuarios;
import java.io.IOException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
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
    // Nota: Puedes quitar @FXML private AnchorPane contentArea; si ya no lo usas
    
    
    @FXML
    private void cargarPermiso(ActionEvent event) {
        try {
            // Cargar el FXML en una nueva ventana
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/polygraph/vista/PermisoForm.fxml"));
            Parent root = loader.load();

            // Crear una nueva escena
            Scene scene = new Scene(root);

            // Crear un nuevo Stage
            Stage stage = new Stage();
            stage.setTitle("Formulario de Permiso");
            stage.setScene(scene);

            // Mostrar la ventana
            stage.show();

            // Opcional: Cerrar la ventana actual si es necesario
            // ((Node) event.getSource()).getScene().getWindow().hide();

        } catch (IOException e) {
            showAlert("Error", "No se pudo cargar el formulario de Permiso: " + e.getMessage());
        }
    }
    
    @FXML
    private void cargarPerfil(ActionEvent event) {
        try {
            // Cargar el FXML en una nueva ventana
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/polygraph/vista/PerfilForm.fxml"));
            Parent root = loader.load();

            // Crear una nueva escena
            Scene scene = new Scene(root);

            // Crear un nuevo Stage
            Stage stage = new Stage();
            stage.setTitle("Formulario de Perfil");
            stage.setScene(scene);

            // Mostrar la ventana
            stage.show();

            // Opcional: Cerrar la ventana actual si es necesario
            // ((Node) event.getSource()).getScene().getWindow().hide();

        } catch (IOException e) {
            showAlert("Error", "No se pudo cargar el formulario de perfil: " + e.getMessage());
        }
    }

    @FXML
    public void insertarUsuario(ActionEvent event) {
        try {
            Usuarios usuario = new Usuarios(
                nombreField.getText(),
                apellidoField.getText(),
                usernameField.getText(),
                correoField.getText(),
                passwordField.getText(),
                LocalDate.now(),
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
        Alert alert = new Alert(AlertType.INFORMATION);
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

    // Puedes eliminar el método loadView si ya no lo usas
}