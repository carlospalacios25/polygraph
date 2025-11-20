package com.polygraph.controlador;

import com.polygraph.dao.DocumentoDAO;
import com.polygraph.modelo.Documentos;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class DocumentoAgregarController {

    @FXML private ComboBox<String> cmbTipoDocumento;
    @FXML private DatePicker dpFechaSolicitud;
    @FXML private DatePicker dpFechaRecibido;
    @FXML private Label lblArchivo;
    @FXML private TextArea txtDescripcion;
    @FXML private Label errorLabel;
    @FXML private TextField txtHabesData;
    @FXML private TextArea txtComunicados;

    private int idServicio;
    private Runnable onSuccess;
    private File archivoSeleccionado;
    private final DocumentoDAO dao = new DocumentoDAO();

    public void setDatos(int idServicio, Runnable callback) {
        this.idServicio = idServicio;
        this.onSuccess = callback;
        inicializar();
    }

    @FXML
    private void initialize() {
        cmbTipoDocumento.getItems().setAll(
            "Informe Poligrafía",
            "Reporte Visita",
            "Certificado Antecedentes",
            "Otro"
        );
        dpFechaSolicitud.setValue(LocalDate.now()); // Por defecto hoy
    }

    private void inicializar() {
        // Nada más, sin usuario
    }

    @FXML
    private void seleccionarArchivo() {
        FileChooser fc = new FileChooser();
        fc.setTitle("Seleccionar Documento");
        fc.getExtensionFilters().addAll(
            new FileChooser.ExtensionFilter("Documentos", "*.pdf", "*.docx", "*.doc", "*.jpg", "*.png"),
            new FileChooser.ExtensionFilter("Todos los archivos", "*.*")
        );

        archivoSeleccionado = fc.showOpenDialog(lblArchivo.getScene().getWindow());
        if (archivoSeleccionado != null) {
            lblArchivo.setText("Archivo: " + archivoSeleccionado.getName());
            lblArchivo.setStyle("-fx-text-fill: green; -fx-font-weight: bold;");
            lblArchivo.setWrapText(true);
        } else {
            lblArchivo.setText("Ningún archivo seleccionado");
            lblArchivo.setStyle("-fx-text-fill: #666666;");
        }
    }

    @FXML
    private void subir() {
        errorLabel.setText("");

        if (cmbTipoDocumento.getValue() == null) {
            errorLabel.setText("Selecciona un tipo de documento");
            return;
        }
        if (dpFechaSolicitud.getValue() == null) {
            errorLabel.setText("Selecciona la fecha de solicitud");
            return;
        }
        if (archivoSeleccionado == null) {
            errorLabel.setText("Selecciona un archivo");
            return;
        }

        try {
            String carpetaPath = "documentos/servicio_" + idServicio;
            File carpeta = new File(carpetaPath);
            carpeta.mkdirs();

            String nombreFinal = idServicio +"_" + cmbTipoDocumento.getValue() + "_" + archivoSeleccionado.getName();
            File destino = new File(carpeta, nombreFinal);
            Files.copy(archivoSeleccionado.toPath(), destino.toPath(), StandardCopyOption.REPLACE_EXISTING);

            Documentos doc = new Documentos();
            doc.setIdServicio(idServicio);
            doc.setTipoDocumento(cmbTipoDocumento.getValue());
            doc.setNombreArchivo(nombreFinal);
            doc.setFechaCarga(LocalDateTime.now());
            doc.setDescripcion(txtDescripcion.getText().trim().isEmpty() ? null : txtDescripcion.getText().trim());
            doc.setTamanoArchivo(archivoSeleccionado.length());
            doc.setEstadoDocumento("Activo");
            doc.setFechaSolicitud(dpFechaSolicitud.getValue());
            doc.setFechaRecibido(dpFechaRecibido.getValue());
            doc.setHabesData(txtHabesData.getText().trim().isEmpty() ? null : txtHabesData.getText().trim());
            doc.setComunicados(txtComunicados.getText().trim().isEmpty() ? null : txtComunicados.getText().trim());

            dao.insertar(doc);

            if (onSuccess != null) onSuccess.run();
            cerrar();

        } catch (Exception e) {
            errorLabel.setText("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML private void cancelar() { cerrar(); }

    private void cerrar() {
        Stage stage = (Stage) lblArchivo.getScene().getWindow();
        stage.close();
    }
}