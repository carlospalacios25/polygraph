package com.polygraph.controlador;

import com.polygraph.dao.PoligrafistasDAO;
import com.polygraph.listeners.PoligrafistaAgregadoListener;
import com.polygraph.modelo.Poligrafistas;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.sql.SQLException;

public class PoligrafistasController {

    @FXML private TextField nombrePoligrafistaField;
    @FXML private TextField salaEncargadaField;

    private PoligrafistaAgregadoListener listener;

    public void setOnPoligrafistaAgregadoListener(PoligrafistaAgregadoListener listener) {
        this.listener = listener;
    }

    @FXML
    public void insertarPoligrafista(ActionEvent event) {
        String nombre = nombrePoligrafistaField.getText().trim();
        String sala = salaEncargadaField.getText().trim();

        if (nombre.isEmpty() || sala.isEmpty()) {
            mostrarAlerta("Error", "Todos los campos son obligatorios.");
            return;
        }

        try {
            Poligrafistas poligrafista = new Poligrafistas(nombre, sala);
            PoligrafistasDAO dao = new PoligrafistasDAO();
            dao.insertarPoligrafista(poligrafista);

            mostrarAlerta("Éxito", "Poligrafista agregado correctamente.");
            limpiarCampos();

            // Notificar al formulario padre (ej. para refrescar ComboBox)
            if (listener != null) {
                listener.onPoligrafistaAgregado();
            }

            // Cerrar ventana
            Stage stage = (Stage) nombrePoligrafistaField.getScene().getWindow();
            stage.close();

        } catch (SQLException e) {
            mostrarAlerta("Error de base de datos", "No se pudo guardar: " + e.getMessage());
        }
    }

    private void mostrarAlerta(String titulo, String mensaje) {
        Alert alert = new Alert(
            titulo.contains("Éxito") ? Alert.AlertType.INFORMATION : Alert.AlertType.ERROR
        );
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    private void limpiarCampos() {
        nombrePoligrafistaField.clear();
        salaEncargadaField.clear();
    }
}