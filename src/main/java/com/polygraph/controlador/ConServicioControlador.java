package com.polygraph.controlador;

import com.polygraph.dao.*;
import com.polygraph.modelo.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import javafx.scene.Parent;

public class ConServicioControlador {

    @FXML private ComboBox<Procesos> comboProcesos;
    @FXML private TextField buscadorField;
    @FXML private TableView<TiposProgreso> tablaRelaciones;

    @FXML private TableColumn<TiposProgreso, String> colTipoProgreso;
    @FXML private TableColumn<TiposProgreso, Boolean> colHabilitado;
    @FXML private TableColumn<TiposProgreso, Void> colAcciones;

    private final ProcesosDAO procesosDAO = new ProcesosDAO();
    private final TiposProgresoDAO tiposDAO = new TiposProgresoDAO();
    private final ProcesoTipoProgresoDAO relacionDAO = new ProcesoTipoProgresoDAO();

    private final ObservableList<TiposProgreso> listaTipos = FXCollections.observableArrayList();
    private final Map<Integer, ProcesoTipoProgreso> mapaRelaciones = new HashMap<>();
    private FilteredList<TiposProgreso> filteredList;

    @FXML
    public void initialize() {
        cargarProcesos();
        configurarTabla();
        configurarBuscador();
        configurarCombo();
    }

    private void cargarProcesos() {
        try {
            comboProcesos.setItems(FXCollections.observableArrayList(procesosDAO.obtenerProcesosBox()));
            comboProcesos.setConverter(new javafx.util.StringConverter<Procesos>() {
                @Override public String toString(Procesos p) { return p == null ? "" : p.getNombreProceso(); }
                @Override public Procesos fromString(String s) { return null; }
            });
        } catch (SQLException e) {
            mostrarError("Error al cargar procesos: " + e.getMessage());
        }
    }

    private void cargarRelaciones() {
        Procesos p = comboProcesos.getValue();
        if (p == null) {
            listaTipos.clear();
            mapaRelaciones.clear();
            return;
        }

        try {
            var todosTipos = tiposDAO.obtenerTodos();
            var relaciones = relacionDAO.obtenerPorProceso(p.getIdProceso());

            listaTipos.clear();
            mapaRelaciones.clear();

            for (TiposProgreso tipo : todosTipos) {
                ProcesoTipoProgreso rel = relaciones.stream()
                    .filter(r -> r.getIdTipoProgreso() == tipo.getIdTipoProgreso())
                    .findFirst()
                    .orElse(null);

                if (rel != null) {
                    mapaRelaciones.put(tipo.getIdTipoProgreso(), rel);
                }
                listaTipos.add(tipo);
            }
            filteredList.setPredicate(t -> true);
        } catch (SQLException e) {
            mostrarError("Error al cargar relaciones: " + e.getMessage());
        }
    }

    private void configurarTabla() {
        colTipoProgreso.setCellValueFactory(new PropertyValueFactory<>("nombreProgreso"));

        colHabilitado.setCellFactory(param -> new TableCell<TiposProgreso, Boolean>() {
            private final Button btnToggle = new Button();

            @Override
            protected void updateItem(Boolean item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || getTableRow() == null || getTableRow().getIndex() >= listaTipos.size()) {
                    setGraphic(null);
                    return;
                }

                TiposProgreso t = listaTipos.get(getTableRow().getIndex());
                ProcesoTipoProgreso rel = mapaRelaciones.get(t.getIdTipoProgreso());
                boolean habilitado = rel != null && rel.isHabilitado();

                btnToggle.setText(habilitado ? "Desactivar" : "Activar");
                btnToggle.setStyle(habilitado
                    ? "-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 11px; -fx-padding: 5 10;"
                    : "-fx-background-color: #27ae60; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 11px; -fx-padding: 5 10;");
                btnToggle.setOnAction(e -> toggleHabilitado(t));
                setGraphic(btnToggle);
            }
        });

        configurarColumnaAcciones();
        tablaRelaciones.setItems(listaTipos);
    }

    private void toggleHabilitado(TiposProgreso tipo) {
        ProcesoTipoProgreso rel = mapaRelaciones.get(tipo.getIdTipoProgreso());
        boolean nuevoEstado = rel == null || !rel.isHabilitado();

        try {
            if (rel == null) {
                ProcesoTipoProgreso nueva = new ProcesoTipoProgreso(
                    comboProcesos.getValue().getIdProceso(),
                    tipo.getIdTipoProgreso(),
                    nuevoEstado
                );
                relacionDAO.insertar(nueva);
            } else {
                relacionDAO.actualizarHabilitado(rel.getId(), nuevoEstado);
            }
            cargarRelaciones();
            mostrarExito("Estado actualizado correctamente");
        } catch (SQLException ex) {
            mostrarError("No se pudo actualizar: " + ex.getMessage());
        }
    }

    private void configurarColumnaAcciones() {
        colAcciones.setCellFactory(param -> new TableCell<TiposProgreso, Void>() {
            private final Button btnEliminar = new Button("Eliminar");

            {
                btnEliminar.setStyle("-fx-background-color: #95a5a6; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 11px; -fx-padding: 5 10;");
                btnEliminar.setOnAction(e -> eliminarRelacion());
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || getTableRow() == null || getTableRow().getIndex() >= listaTipos.size()) {
                    setGraphic(null);
                    return;
                }

                TiposProgreso t = listaTipos.get(getTableRow().getIndex());
                ProcesoTipoProgreso rel = mapaRelaciones.get(t.getIdTipoProgreso());
                setGraphic(rel != null ? btnEliminar : null);
            }
        });
    }

    private void eliminarRelacion() {
        int index = tablaRelaciones.getSelectionModel().getSelectedIndex();
        if (index < 0 || index >= listaTipos.size()) return;

        TiposProgreso t = listaTipos.get(index);
        ProcesoTipoProgreso rel = mapaRelaciones.get(t.getIdTipoProgreso());
        if (rel == null) return;

        if (!mostrarConfirmacion("Eliminar relación", "¿Estás seguro de eliminar esta relación?")) return;

        try {
            relacionDAO.eliminar(rel.getId());
            cargarRelaciones();
            mostrarExito("Relación eliminada correctamente");
        } catch (SQLException ex) {
            mostrarError("No se pudo eliminar: " + ex.getMessage());
        }
    }

    private void configurarBuscador() {
        filteredList = new FilteredList<>(listaTipos, p -> true);
        SortedList<TiposProgreso> sorted = new SortedList<>(filteredList);
        sorted.comparatorProperty().bind(tablaRelaciones.comparatorProperty());
        tablaRelaciones.setItems(sorted);

        buscadorField.textProperty().addListener((obs, old, nuevo) -> {
            filteredList.setPredicate(tipo -> {
                if (nuevo == null || nuevo.isEmpty()) return true;
                return tipo.getNombreProgreso().toLowerCase().contains(nuevo.toLowerCase());
            });
        });
    }

    private void configurarCombo() {
        comboProcesos.valueProperty().addListener((obs, old, nuevo) -> {
            if (nuevo != null) cargarRelaciones();
        });
    }


    @FXML
    public void abrirCrearProceso() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/polygraph/vista/ProcesoForm.fxml"));
            Parent root = loader.load();

            // ← CONTROLADOR CORRECTO
            ProcesoController controller = loader.getController();

            // ← LISTENER CORRECTO
            controller.setOnProcesoAgregadoListener(() -> {
                cargarProcesos(); // ← RECARGA EL COMBO
                cargarRelaciones(); // ← RECARGA LA TABLA
                mostrarExito("Proceso creado correctamente");
            });

            Stage stage = new Stage();
            stage.setTitle("Nuevo Proceso");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.initOwner(tablaRelaciones.getScene().getWindow());
            stage.setScene(new Scene(root));
            stage.showAndWait();

        } catch (IOException e) {
            mostrarError("No se pudo abrir el formulario: " + e.getMessage());
        }
    }
    
    @FXML
    public void abrirCrearTipoProgreso() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/polygraph/vista/TipoProgresoForm.fxml"));
            Parent root = loader.load();

            // ← OBTENER CONTROLADOR
            TipoProgresoController controller = loader.getController();

            // ← PASAR CALLBACK
            controller.setOnTipoProgresoAgregadoListener(() -> {
                cargarRelaciones();
                mostrarExito("Tipo de progreso creado correctamente");
            });

            Stage stage = new Stage();
            stage.setTitle("Nuevo Tipo de Progreso");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.initOwner(tablaRelaciones.getScene().getWindow());
            stage.setScene(new Scene(root));
            stage.showAndWait();

        } catch (IOException e) {
            mostrarError("No se pudo abrir el formulario: " + e.getMessage());
        }
    }
    
    @FXML public void actualizarRelaciones() {
        cargarRelaciones();
        mostrarExito("Datos actualizados correctamente");
    }

    private void abrirForm(String fxml, String titulo, Runnable post) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxml));
            Stage stage = new Stage();
            stage.setScene(new Scene(loader.load()));
            stage.setTitle(titulo);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.initOwner(tablaRelaciones.getScene().getWindow());
            stage.showAndWait();
            post.run();
        } catch (IOException e) {
            mostrarError("No se pudo abrir el formulario: " + e.getMessage());
        }
    }

    // MENSAJES BONITOS
    private void mostrarExito(String mensaje) {
        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.setTitle("Éxito");
        a.setHeaderText(null);
        a.setContentText(mensaje);
        a.showAndWait();
    }

    private void mostrarError(String mensaje) {
        Alert a = new Alert(Alert.AlertType.ERROR);
        a.setTitle("Error");
        a.setHeaderText(null);
        a.setContentText(mensaje);
        a.showAndWait();
    }

    private boolean mostrarConfirmacion(String titulo, String mensaje) {
        Alert a = new Alert(Alert.AlertType.CONFIRMATION);
        a.setTitle(titulo);
        a.setHeaderText(null);
        a.setContentText(mensaje);
        return a.showAndWait().filter(r -> r == ButtonType.OK).isPresent();
    }
}