package com.polygraph.controlador;

import com.polygraph.dao.ProcesosDAO;
import com.polygraph.dao.ServicioDAO;
import com.polygraph.modelo.Procesos;
import com.polygraph.modelo.Servicio;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import java.sql.SQLException;
import java.util.List;

public class ModificarServicioControlador {

    // === SOLO LOS ELEMENTOS DEL FORMULARIO 1 (los que están en tu FXML) ===
    @FXML private Label lblId1;
    @FXML private TextField txtCliente1;
    @FXML private TextField txtCandidato1;
    @FXML private ComboBox<Procesos> cmbProceso1;   // fx:id="cmbProceso1"
    @FXML private ComboBox<String> cmbEstado1;      // fx:id="cmbEstado1"
    @FXML private TextArea txtResultado1;

    private Servicio servicio;
    private MainController mainController;
    private final ServicioDAO servicioDAO = new ServicioDAO();
    private final ProcesosDAO procesosDAO = new ProcesosDAO();

    // === SETTERS ===
    public void setServicio(Servicio servicio) {
        this.servicio = servicio;
        cargarDatos(); // Carga datos cuando se asigna el servicio
    }

    public void setMainController(MainController mainController) {
        this.mainController = mainController;
    }

    // === INICIALIZACIÓN ===
    @FXML
    private void initialize() {
        cargarProcesos();
        cargarEstados();
    }

    private void cargarProcesos() {
        try {
            List<Procesos> procesos = procesosDAO.obtenerProcesosBox();
            cmbProceso1.setItems(FXCollections.observableArrayList(procesos));
            cmbProceso1.setConverter(new javafx.util.StringConverter<Procesos>() {
                @Override public String toString(Procesos p) {
                    return p == null ? "" : p.getNombreProceso();
                }
                @Override public Procesos fromString(String s) { return null; }
            });
        } catch (SQLException e) {
            showAlert("Error", "No se pudieron cargar procesos: " + e.getMessage());
        }
    }

    private void cargarEstados() {
        cmbEstado1.setItems(FXCollections.observableArrayList(
            "Pendiente", "En Proceso", "Completado", "Cancelado"
        ));
    }

    // === CARGAR DATOS DEL SERVICIO ===
    private void cargarDatos() {
        if (servicio == null) {
            lblId1.setText("Servicio #—");
            return;
        }

        lblId1.setText("Servicio #" + servicio.getIdServicio());
        txtCliente1.setText(servicio.getNombreCliente() != null ? servicio.getNombreCliente() : "");
        txtCandidato1.setText(
            (servicio.getNombreCandidato() != null ? servicio.getNombreCandidato() + " " : "") +
            (servicio.getApellidoCandidato() != null ? servicio.getApellidoCandidato() : "")
        );

        if (servicio.getIdProceso() != null) {
            Procesos seleccionado = cmbProceso1.getItems().stream()
                .filter(p -> p.getIdProceso() == servicio.getIdProceso())
                .findFirst()
                .orElse(null);
            cmbProceso1.setValue(seleccionado);
        }

        cmbEstado1.setValue(servicio.getEstado());
        txtResultado1.setText(servicio.getResultado() != null ? servicio.getResultado() : "");
    }

    // === GUARDAR ===
    @FXML
    private void guardar() {
        if (servicio == null) return;

        servicio.setIdProceso(cmbProceso1.getValue() != null ? cmbProceso1.getValue().getIdProceso() : null);
        servicio.setEstado(cmbEstado1.getValue());
        servicio.setResultado(txtResultado1.getText().trim().isEmpty() ? null : txtResultado1.getText().trim());

        try {
            servicioDAO.actualizarServicio(servicio);
            showAlert("Éxito", "Servicio actualizado correctamente.");
            volverALaLista();
        } catch (SQLException e) {
            showAlert("Error", "Error al guardar: " + e.getMessage());
        }
    }

    // === CANCELAR ===
    @FXML
    private void cancelar() {
        volverALaLista();
    }

    private void volverALaLista() {
        if (mainController != null) {
            mainController.popBreadcrumb();
            mainController.cargarServicio(null);
        }
    }

    private void showAlert(String titulo, String mensaje) {
        Alert a = new Alert(titulo.equals("Éxito") ? Alert.AlertType.INFORMATION : Alert.AlertType.ERROR);
        a.setTitle(titulo);
        a.setHeaderText(null);
        a.setContentText(mensaje);
        a.showAndWait();
    }
}