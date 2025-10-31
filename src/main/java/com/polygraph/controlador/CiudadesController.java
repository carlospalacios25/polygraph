package com.polygraph.controlador;

import com.polygraph.dao.CiudadesDAO;
import com.polygraph.modelo.Ciudades;
import com.polygraph.listeners.CiudadAgregadaListener;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;

public class CiudadesController {

    @FXML private TextField nomCiudadField;

    // Callback para notificar al padre
    private CiudadAgregadaListener listener;

    // Método para inyectar el listener
    public void setOnCiudadAgregadaListener(CiudadAgregadaListener listener) {
        this.listener = listener;
    }

    @FXML
    public void insertarCiudad(ActionEvent event) {
        String nombre = nomCiudadField.getText().trim();
        if (nombre.isEmpty()) {
            showAlert("Error", "El nombre de la ciudad no puede estar vacío.");
            return;
        }

        try {
            Ciudades ciudades = new Ciudades(nombre);
            CiudadesDAO dao = new CiudadesDAO();
            dao.insertarPerfile(ciudades);  // ← Verifica que este método guarde bien

            showAlert("Éxito", "Ciudad agregada correctamente.");
            clearFields();

            // Notificar al formulario principal
            if (listener != null) {
                listener.onCiudadAgregada();
            }

            // Cerrar ventana
            Stage stage = (Stage) nomCiudadField.getScene().getWindow();
            stage.close();

        } catch (SQLException | NoSuchAlgorithmException e) {
            showAlert("Error", "Error al insertar: " + e.getMessage());
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(
            title.equals("Éxito") ? Alert.AlertType.INFORMATION : Alert.AlertType.ERROR
        );
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void clearFields() {
        nomCiudadField.clear();
    }
}