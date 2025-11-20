package com.polygraph.controlador;

import com.polygraph.dao.AnalisisDAO;
import com.polygraph.dao.DocumentoDAO;
import com.polygraph.dao.ProgresoDAO;
import com.polygraph.modelo.*;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.function.Consumer;

/**
 * Controlador principal para la gestión detallada de un servicio.
 * Muestra información del servicio y permite agregar progreso, documentos y análisis
 * mediante ventanas modales limpias y profesionales.
 */
public class GestServicioControlador {

    // === COMPONENTES FXML (UNA POR LÍNEA - BUENA PRÁCTICA) ===
    @FXML private Label lblId1;
    @FXML private Label lblInfoGeneral;

    @FXML private TableView<Progreso> tblProgreso;
    @FXML private TableColumn<Progreso, LocalDate> colProgFecha;
    @FXML private TableColumn<Progreso, String> colProgTipo;
    @FXML private TableColumn<Progreso, String> colProgObservacion;
    @FXML private TableColumn<Progreso, String> colProgUsuario;

    @FXML private TableView<Documentos> tblDocumentos;
    @FXML private TableColumn<Documentos, String> colDocTipo;
    @FXML private TableColumn<Documentos, String> colDocNombre;
    @FXML private TableColumn<Documentos, LocalDate> colDocFecha;
    @FXML private TableColumn<Documentos, String> colDocEstado;

    @FXML private TableView<Analisis> tblAnalisis;
    @FXML private TableColumn<Analisis, String> colAnaTipo;
    @FXML private TableColumn<Analisis, String> colAnaContenido;

    // === DAOs (INYECCIÓN MANUAL - LIMPIO Y CONTROLADO) ===
    private final ProgresoDAO progresoDAO = new ProgresoDAO();
    private final DocumentoDAO documentoDAO = new DocumentoDAO();
    private final AnalisisDAO analisisDAO = new AnalisisDAO();

    // === DATOS DE SESIÓN ===
    private Servicio servicio;
    private MainController mainController;

    // === CONFIGURACIÓN INICIAL ===
    public void setServicio(Servicio servicio) {
        this.servicio = servicio;
        cargarDatos();
        cargarTablas();
    }

    public void setMainController(MainController mainController) {
        this.mainController = mainController;
    }

    @FXML
    private void initialize() {
        configurarTablas();
    }

    private void configurarTablas() {
        colProgFecha.setCellValueFactory(new PropertyValueFactory<>("fechaProgr"));
        colProgTipo.setCellValueFactory(new PropertyValueFactory<>("tipoProgresoNombre"));
        colProgObservacion.setCellValueFactory(new PropertyValueFactory<>("observacionAnte"));
        colProgUsuario.setCellValueFactory(new PropertyValueFactory<>("nombreUsuario"));

        colDocTipo.setCellValueFactory(new PropertyValueFactory<>("tipoDocumento"));
        colDocNombre.setCellValueFactory(new PropertyValueFactory<>("nombreArchivo"));
        colDocFecha.setCellValueFactory(new PropertyValueFactory<>("fechaCarga"));
        colDocEstado.setCellValueFactory(new PropertyValueFactory<>("estadoDocumento"));

        colAnaTipo.setCellValueFactory(new PropertyValueFactory<>("tipoAnalisis"));
        colAnaContenido.setCellValueFactory(new PropertyValueFactory<>("contenido"));
    }

    private void cargarDatos() {
        if (servicio == null) return;

        lblId1.setText("GESTIÓN DEL SERVICIO # " + servicio.getIdServicio());
        lblInfoGeneral.setText(
            "Cliente: " + servicio.getNombreCliente() +
            " • Candidato: " + servicio.getNombreCandidato() + " " + servicio.getApellidoCandidato() +
            " • Proceso: " + servicio.getNombreProceso()
        );
    }

    private void cargarTablas() {
        if (servicio == null) return;

        try {
            tblProgreso.setItems(FXCollections.observableArrayList(
                progresoDAO.listarPorServicio(servicio.getIdServicio())
            ));
            tblDocumentos.setItems(FXCollections.observableArrayList(
                documentoDAO.listarPorServicio(servicio.getIdServicio())
            ));
            tblAnalisis.setItems(FXCollections.observableArrayList(
                analisisDAO.listarPorServicio(servicio.getIdServicio())
            ));
        } catch (SQLException e) {
            mostrarError("Error de base de datos", "No se pudieron cargar los datos del servicio.");
        }
    }

    // === ACCIONES DE MODALES ===
    @FXML
    private void abrirAgregarProgreso() {
        abrirModal("/com/polygraph/vista/ProgresoAgregarView.fxml", "Agregar Progreso",
            c -> ((ProgresoAgregarController) c).setDatos(servicio.getIdServicio(), this::cargarTablas));
    }

    @FXML
    private void abrirAgregarDocumento() {
        abrirModal("/com/polygraph/vista/DocumentoAgregarView.fxml", "Subir Documento",
            c -> ((DocumentoAgregarController) c).setDatos(servicio.getIdServicio(), this::cargarTablas));
    }

    @FXML
    private void abrirAgregarAnalisis() {
        abrirModal("/com/polygraph/vista/AnalisisAgregarView.fxml", "Agregar Análisis",
            c -> ((AnalisisAgregarController) c).setDatos(servicio.getIdServicio(), this::cargarTablas));
    }

    private void abrirModal(String fxmlPath, String titulo, Consumer<Object> configurador) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Stage modal = new Stage();
            modal.setScene(new Scene(loader.load()));
            modal.setTitle(titulo);
            modal.initModality(Modality.WINDOW_MODAL);
            modal.initOwner(lblId1.getScene().getWindow());
            modal.setResizable(false);

            Object controller = loader.getController();
            configurador.accept(controller);

            modal.showAndWait();

        } catch (IOException e) {
            mostrarError("Error", "No se pudo abrir la ventana: " + e.getMessage());
        }
    }

    @FXML
    private void cancelar() {
        if (mainController != null) {
            mainController.popBreadcrumb();
        }
    }

    // === UTILIDADES ===
    private void mostrarError(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}