// src/main/java/com/polygraph/controlador/TipoProgresoController.java
package com.polygraph.controlador;

import com.polygraph.dao.TiposProgresoDAO;
import com.polygraph.modelo.TiposProgreso;
import com.polygraph.listeners.TipoProgresoAgregadoListener;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import java.sql.SQLException;

public class TipoProgresoController {

    @FXML private TextField nombreProgresoField;

    // ← LISTENER PARA AVISAR AL PADRE
    private TipoProgresoAgregadoListener listener;

    // ← MÉTODO PARA RECIBIR EL LISTENER
    public void setOnTipoProgresoAgregadoListener(TipoProgresoAgregadoListener listener) {
        this.listener = listener;
    }

    @FXML
    public void guardar(ActionEvent event) {
        String nombre = nombreProgresoField.getText().trim();
        if (nombre.isEmpty()) {
            mostrarAlerta("Error", "El nombre del tipo de progreso es obligatorio.");
            return;
        }

        try {
            TiposProgreso tipo = new TiposProgreso(nombre);
            new TiposProgresoDAO().insertar(tipo);

            // ← AVISAR AL PADRE QUE SE CREÓ
            if (listener != null) {
                listener.onTipoProgresoAgregado();
            }

            mostrarAlerta("Éxito", "Tipo de progreso creado.");
            cerrar();

        } catch (SQLException e) {
            mostrarAlerta("Error", "Error: " + e.getMessage());
        }
    }

    private void mostrarAlerta(String titulo, String mensaje) {
        Alert alert = new Alert(titulo.equals("Éxito") ? Alert.AlertType.INFORMATION : Alert.AlertType.ERROR);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    private void cerrar() {
        Stage stage = (Stage) nombreProgresoField.getScene().getWindow();
        stage.close();
    }

    @FXML 
    private void cancelar(ActionEvent e) { 
        cerrar(); 
    }
}