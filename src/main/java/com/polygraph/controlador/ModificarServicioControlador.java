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

    @FXML private Label lblId;
    @FXML private TextField txtCliente;
    @FXML private TextField txtCandidato;
    @FXML private ComboBox<Procesos> cmbProceso;
    @FXML private ComboBox<String> cmbEstado;
    @FXML private TextArea txtResultado;

    private Servicio servicio;
    private MainController mainController;
    private final ServicioDAO servicioDAO = new ServicioDAO();
    private final ProcesosDAO procesosDAO = new ProcesosDAO();

    public void setServicio(Servicio servicio) {
        this.servicio = servicio;
        cargarDatos();
    }

    public void setMainController(MainController mainController) {
        this.mainController = mainController;
    }

    @FXML
    private void initialize() {
        cargarProcesos();
        cmbEstado.setItems(FXCollections.observableArrayList(
            "Pendiente", "En Proceso", "Completado", "Cancelado"
        ));
    }

    private void cargarDatos() {
        if (servicio == null) return;

        lblId.setText("Servicio #" + servicio.getIdServicio());
        txtCliente.setText(servicio.getNombreCliente() != null ? servicio.getNombreCliente() : "");
        txtCandidato.setText(
            (servicio.getNombreCandidato() != null ? servicio.getNombreCandidato() + " " : "") +
            (servicio.getApellidoCandidato() != null ? servicio.getApellidoCandidato() : "")
        );

        if (servicio.getIdProceso() != null) {
            cmbProceso.getSelectionModel().select(
                cmbProceso.getItems().stream()
                    .filter(p -> p.getIdProceso() == servicio.getIdProceso())
                    .findFirst()
                    .orElse(null)
            );
        }

        if (servicio.getEstado() != null) {
            cmbEstado.setValue(servicio.getEstado());
        }

        txtResultado.setText(servicio.getResultado() != null ? servicio.getResultado() : "");
    }

    private void cargarProcesos() {
        try {
            List<Procesos> procesos = procesosDAO.obtenerProcesosBox();
            cmbProceso.setItems(FXCollections.observableArrayList(procesos));
            cmbProceso.setConverter(new javafx.util.StringConverter<Procesos>() {
                @Override public String toString(Procesos p) { return p == null ? "" : p.getNombreProceso(); }
                @Override public Procesos fromString(String s) { return null; }
            });
        } catch (SQLException e) {
            showAlert("Error", "No se pudieron cargar procesos: " + e.getMessage());
        }
    }

    @FXML
    private void guardar() {
        if (servicio == null) return;

        servicio.setIdProceso(cmbProceso.getValue() != null ? cmbProceso.getValue().getIdProceso() : null);
        servicio.setEstado(cmbEstado.getValue());
        servicio.setResultado(txtResultado.getText().trim().isEmpty() ? null : txtResultado.getText().trim());

        try {
            servicioDAO.actualizarServicio(servicio);
            showAlert("Éxito", "Servicio actualizado.");
            volverALaLista();
        } catch (SQLException e) {
            showAlert("Error", "Error al guardar: " + e.getMessage());
        }
    }

    @FXML
    private void cancelar() {
        volverALaLista();
    }

    private void volverALaLista() {
        if (mainController != null) {
            mainController.popBreadcrumb(); // ← AHORA SÍ EXISTE EN MainController
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