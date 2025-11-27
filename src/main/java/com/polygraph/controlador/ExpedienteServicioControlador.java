package com.polygraph.controlador;

import com.polygraph.dao.ServicioDAO;
import com.polygraph.modelo.Servicio;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.sql.SQLException;
import java.util.List;

public class ExpedienteServicioControlador {

    @FXML private ComboBox<String> estadoCombo;
    @FXML private CheckBox asistioCheck;
    @FXML private TextField direccionField;
    @FXML private Label archivosLabel;

    private Servicio servicio;
    private MainController mainController;
    private final ServicioDAO servicioDAO = new ServicioDAO();
    private List<File> archivosSeleccionados;

    // === ESTADOS POSIBLES ===
    private static final String[] ESTADOS = {
        "Pendiente",
        "En Progreso",
        "Poligrafía Programada",
        "Poligrafía Realizada",
        "Informe Entregado",
        "Finalizado"
    };

    // === REFERENCIA AL MAIN CONTROLLER ===
    public void setMainController(MainController mainController) {
        this.mainController = mainController;
    }

    // === RECIBIR EL SERVICIO A EDITAR ===
    public void setServicio(Servicio servicio) {
        this.servicio = servicio;
        cargarDatosEnFormulario();
    }

    @FXML
    public void initialize() {
        estadoCombo.setItems(FXCollections.observableArrayList(ESTADOS));
        asistioCheck.setSelected(false);
        archivosLabel.setText("Ningún archivo seleccionado");
    }

    private void cargarDatosEnFormulario() {
        if (servicio == null) return;

        // Cargar estado
        estadoCombo.setValue(servicio.getEstado() != null ? servicio.getEstado() : "Pendiente");

        // Cargar asistencia (puedes tener un campo en Servicio o usar lógica)
        asistioCheck.setSelected("Poligrafía Realizada".equalsIgnoreCase(servicio.getEstado()));

        // Cargar dirección (puedes tener un campo en Servicio o en Candidato)
        direccionField.setText("Calle 123, Bogotá"); // <-- Reemplaza con dato real si lo tienes

        // Mostrar documentos ya subidos (opcional)
        archivosLabel.setText("3 documentos adjuntos");
    }

    // === SUBIR DOCUMENTOS ===
    @FXML
    private void subirDocumentos() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Seleccionar Documentos del Candidato");
        fileChooser.getExtensionFilters().addAll(
            new FileChooser.ExtensionFilter("Documentos", "*.pdf", "*.docx", "*.jpg", "*.png"),
            new FileChooser.ExtensionFilter("Todos los archivos", "*.*")
        );

        List<File> seleccionados = fileChooser.showOpenMultipleDialog(getStage());
        if (seleccionados != null && !seleccionados.isEmpty()) {
            this.archivosSeleccionados = seleccionados;
            archivosLabel.setText(seleccionados.size() + " archivo(s) seleccionado(s)");
        }
    }

    // === GUARDAR CAMBIOS ===
   /* @FXML
    private void guardar() {
        if (servicio == null) {
            showAlert("Error", "No hay servicio seleccionado.");
            return;
        }

        try {
            // Actualizar estado
            servicio.setEstado(estadoCombo.getValue());

            // Actualizar asistencia (lógica de ejemplo)
            if (asistioCheck.isSelected() && !"Poligrafía Realizada".equals(estadoCombo.getValue())) {
                servicio.setEstado("Poligrafía Realizada");
            }

            // Guardar en base de datos
            servicioDAO.actualizarEstadoServicio(servicio.getIdServicio(), servicio.getEstado());

            // Guardar documentos en carpeta del proyecto
            if (archivosSeleccionados != null && !archivosSeleccionados.isEmpty()) {
                Path carpeta = Path.of("documentos", "servicio_" + servicio.getIdServicio());
                Files.createDirectories(carpeta);

                for (File archivo : archivosSeleccionados) {
                    Path destino = carpeta.resolve(archivo.getName());
                    Files.copy(archivo.toPath(), destino, StandardCopyOption.REPLACE_EXISTING);
                }
            }

            showAlert("Éxito", "Expediente actualizado correctamente.");
            if (mainController != null) {
                mainController.popBreadcrumb(); // Volver a la lista
            }

        } catch (SQLException | IOException e) {
            showAlert("Error", "No se pudo guardar: " + e.getMessage());
        }
    }*/

    // === CANCELAR ===
    @FXML
    private void cancelar() {
        if (mainController != null) {
            mainController.popBreadcrumb();
        }
    }

    // === ALERTAS ===
    private void showAlert(String titulo, String mensaje) {
        Alert alert = new Alert(
            titulo.equals("Éxito") ? Alert.AlertType.INFORMATION : Alert.AlertType.ERROR
        );
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    // === OBTENER STAGE ===
    private Stage getStage() {
        return (Stage) estadoCombo.getScene().getWindow();
    }
}