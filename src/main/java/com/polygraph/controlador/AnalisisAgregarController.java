package com.polygraph.controlador;

import com.polygraph.dao.AnalisisDAO;
import com.polygraph.listeners.AnalisisAgregadoListener;
import com.polygraph.modelo.Analisis;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.sql.SQLException;

public class AnalisisAgregarController {

    @FXML private ComboBox<String> cmbTipoAnalisis;
    @FXML private TextArea txtContenido;

    private AnalisisAgregadoListener listener;
    private int idServicio;
    private final AnalisisDAO dao = new AnalisisDAO();

    public void setDatos(int idServicio, AnalisisAgregadoListener listener) {
        this.idServicio = idServicio;
        this.listener = listener;
    }

    @FXML
    private void guardar() {
        String tipo = cmbTipoAnalisis.getValue();
        String contenido = txtContenido.getText().trim();

        if (tipo == null || tipo.isEmpty()) {
            alerta("Error", "Selecciona el tipo de análisis.");
            return;
        }
        if (contenido.isEmpty() || contenido.length() < 10) {
            alerta("Error", "El contenido debe tener al menos 10 caracteres.");
            return;
        }

        try {
            Analisis a = new Analisis();
            a.setIdServicio(idServicio);
            a.setTipoAnalisis(tipo);
            a.setContenido(contenido);

            dao.insertar(a);

            mostrarExito("Análisis guardado correctamente");
            if (listener != null) listener.onAnalisisAgregado();
            cerrarVentana();

        } catch (SQLException e) {
            alerta("Error de base de datos", e.getMessage());
        }
    }

    @FXML
    private void cancelar() {
        cerrarVentana();
    }

    private void cerrarVentana() {
        Stage stage = (Stage) txtContenido.getScene().getWindow();
        stage.close();
    }

    private void alerta(String titulo, String mensaje) {
        new Alert(Alert.AlertType.ERROR, mensaje, ButtonType.OK)
            .showAndWait();
    }

    private void mostrarExito(String mensaje) {
        new Alert(Alert.AlertType.INFORMATION, mensaje, ButtonType.OK)
            .showAndWait();
    }
}