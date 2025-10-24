 package com.polygraph.controlador;

import com.polygraph.dao.PerfilesDAO;
import com.polygraph.dao.UsuarioDAO;
import com.polygraph.modelo.Perfiles;
import com.polygraph.modelo.Usuarios;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

public class UsuarioController {

    @FXML private TextField nombreField;
    @FXML private TextField apellidoField;
    @FXML private TextField usernameField;
    @FXML private TextField correoField;
    @FXML private PasswordField passwordField;
    @FXML private CheckBox activoCheck;
    @FXML private ComboBox<Perfiles> perfilComboBox; // ComboBox para perfiles

    @FXML
    public void initialize() {
        // Cargar los perfiles en el ComboBox al inicializar
        cargarPerfiles();
    }

    private void cargarPerfiles() {
        ObservableList<Perfiles> perfiles = FXCollections.observableArrayList();
        PerfilesDAO dao = new PerfilesDAO();
        try {
            perfiles.addAll(dao.obtenerTodosPerfiles());
            perfilComboBox.setItems(perfiles);

            // Personalizar cómo se muestra el texto en el ComboBox
            perfilComboBox.setConverter(new StringConverter<Perfiles>() {
                @Override
                public String toString(Perfiles perfil) {
                    return perfil == null ? "" : perfil.getNombrePerfil();
                }

                @Override
                public Perfiles fromString(String string) {
                    return null; // No se necesita para este caso
                }
            });
        } catch (SQLException e) {
            showAlert("Error", "Error al cargar perfiles: " + e.getMessage());
        }
    }

    @FXML
    private void cargarPermiso(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/polygraph/vista/PermisoForm.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root);
            Stage stage = new Stage();
            stage.setTitle("Formulario de Permiso");
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            showAlert("Error", "No se pudo cargar el formulario de Permiso: " + e.getMessage());
        }
    }

    @FXML
    private void cargarPerfil(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/polygraph/vista/PerfilForm.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root);
            Stage stage = new Stage();
            stage.setTitle("Formulario de Perfil");
            stage.setScene(scene);
            stage.show();
            // Recargar perfiles después de añadir uno nuevo
            stage.setOnHidden(e -> cargarPerfiles());
        } catch (IOException e) {
            showAlert("Error", "No se pudo cargar el formulario de perfil: " + e.getMessage());
        }
    }

    @FXML
    public void insertarUsuario(ActionEvent event) {
        try {
            Perfiles perfilSeleccionado = perfilComboBox.getSelectionModel().getSelectedItem();
            if (perfilSeleccionado == null) {
                showAlert("Error", "Por favor, selecciona un perfil.");
                return;
            }

            Usuarios usuario = new Usuarios(
                nombreField.getText(),
                apellidoField.getText(),
                usernameField.getText(),
                correoField.getText(),
                passwordField.getText(),
                LocalDate.now(),
                activoCheck.isSelected(),
                perfilSeleccionado.getIdPerfil()// Obtener el ID del perfil seleccionado
            );

            UsuarioDAO dao = new UsuarioDAO();
            dao.insertarUsuario(usuario);

            showAlert("Éxito", "Usuario insertado correctamente.");
            clearFields();
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
        perfilComboBox.getSelectionModel().clearSelection();
        activoCheck.setSelected(true);
    }
}