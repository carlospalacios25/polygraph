// src/main/java/com/polygraph/controlador/ProcesoController.java
package com.polygraph.controlador;

import com.polygraph.dao.ProcesosDAO;
import com.polygraph.listeners.ProcesoAgregadoListener;
import com.polygraph.modelo.Procesos;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import java.sql.SQLException;

public class ProcesoController {

    @FXML private TextField nombreProcesoField;

    // ← LISTENER CORRECTO
    private ProcesoAgregadoListener listener;

    // ← MÉTODO CORRECTO
    public void setOnProcesoAgregadoListener(ProcesoAgregadoListener listener) {
        this.listener = listener;
    }

    @FXML
    public void guardarProceso(ActionEvent event) {
        String nombre = nombreProcesoField.getText().trim();
        if (nombre.isEmpty()) {
            mostrarAlerta("Error", "El nombre del proceso es obligatorio.");
            return;
        }

        try {
            Procesos proceso = new Procesos(nombre);
            ProcesosDAO dao = new ProcesosDAO();
            dao.insertar(proceso); // ← ASEGÚRATE QUE EXISTE EN ProcesosDAO

            // ← AVISAR AL PADRE
            if (listener != null) {
                listener.onProcesoAgregado();
            }

            mostrarAlerta("Éxito", "Proceso creado correctamente.");
            cerrarVentana();

        } catch (SQLException e) {
            mostrarAlerta("Error", "Error al guardar: " + e.getMessage());
        }
    }

    private void mostrarAlerta(String titulo, String mensaje) {
        Alert alert = new Alert(titulo.equals("Éxito") ? Alert.AlertType.INFORMATION : Alert.AlertType.ERROR);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    private void cerrarVentana() {
        Stage stage = (Stage) nombreProcesoField.getScene().getWindow();
        stage.close();
    }

    @FXML 
    private void cancelar(ActionEvent e) { 
        cerrarVentana(); 
    }
}