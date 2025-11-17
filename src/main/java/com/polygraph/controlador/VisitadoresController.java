package com.polygraph.controlador;

import com.polygraph.dao.VisitadoresDAO;
import com.polygraph.listeners.VisitadorAgregadoListener;
import com.polygraph.modelo.Visitadores;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.sql.SQLException;

public class VisitadoresController {

    @FXML private TextField nombreVisitadorField;
    @FXML private TextField zonasVisitadorField;
    @FXML private javafx.scene.control.Label errorLabel;

    // Listener para notificar al formulario padre (por ejemplo la lista de visitadores)
    private VisitadorAgregadoListener listener;

    public void setOnVisitadorAgregadoListener(VisitadorAgregadoListener listener) {
        this.listener = listener;
    }

    @FXML
    public void insertarVisitador(ActionEvent event) {
        String nombre = nombreVisitadorField.getText().trim();
        String zonas = zonasVisitadorField.getText().trim();

        if (nombre.isEmpty() || zonas.isEmpty()) {
            mostrarAlerta("Error", "Todos los campos son obligatorios.", Alert.AlertType.ERROR);
            return;
        }

        try {
            Visitadores visitador = new Visitadores(0, nombre, zonas); // id = 0 porque es autoincremental
            VisitadoresDAO dao = new VisitadoresDAO();
            dao.insertarVisitador(visitador);

            mostrarAlerta("Éxito", "Visitador creado correctamente.", Alert.AlertType.INFORMATION);
            limpiarCampos();

            // Notificar al padre que se agregó uno nuevo
            if (listener != null) {
                listener.onVisitadorAgregado();
            }

            // Cerrar la ventana
            Stage stage = (Stage) nombreVisitadorField.getScene().getWindow();
            stage.close();

        } catch (SQLException e) {
            mostrarAlerta("Error de base de datos", "No se pudo guardar el visitador: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }
    
    
    private void limpiarCampos() {
        nombreVisitadorField.clear();
        zonasVisitadorField.clear();
        errorLabel.setText("");
    }

    private void mostrarAlerta(String titulo, String mensaje, Alert.AlertType tipo) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}
