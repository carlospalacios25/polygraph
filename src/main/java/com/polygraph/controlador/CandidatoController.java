package com.polygraph.controlador;

import com.polygraph.dao.CandidatoDAO;
import com.polygraph.dao.CiudadesDAO;
import com.polygraph.modelo.Candidatos;
import com.polygraph.modelo.Ciudades;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.StringConverter;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Optional;

public class CandidatoController {

    // ==================== TUS CONTROLES ORIGINALES (SIN CAMBIOS) ====================
    @FXML private TextField cedulaCanField;
    @FXML private TextField nombreCanField;
    @FXML private TextField apellidoCanField;
    @FXML private TextField telefonoCanField;
    @FXML private TextField direccionCanField;
    @FXML private ComboBox<Ciudades> cidadesComboBox;
    @FXML private TableView<Candidatos> tablaCandidato;
    @FXML private TextField buscadorField;
    @FXML private Button btnCrearCandidato;               // ← NUEVO: este fx:id debes ponerlo en el FXML

    @FXML private TableColumn<Candidatos, Long> colCeduCandidato;
    @FXML private TableColumn<Candidatos, String> colNomCandidato;
    @FXML private TableColumn<Candidatos, String> colTelCandidato;
    @FXML private TableColumn<Candidatos, String> colDirCandidato;
    @FXML private TableColumn<Candidatos, String> colCiuCandidato;
    @FXML private TableColumn<Candidatos, Void> colAcciones;

    // ==================== LISTAS Y DAOs ====================
    private final ObservableList<Candidatos> listaCan = FXCollections.observableArrayList();
    private final ObservableList<Ciudades> ciudades = FXCollections.observableArrayList();
    private FilteredList<Candidatos> filteredList;

    private final CandidatoDAO candidatoDAO = new CandidatoDAO();
    private final CiudadesDAO ciudadesDAO = new CiudadesDAO();

    // ==================== INITIALIZE ====================
    @FXML
    public void initialize() {
        cargarCiudades();
        configurarColumnasTabla();
        actualizarTabla();
        agregarBotonEditar();           // ← Solo activo en fila seleccionada
        configurarSeleccionFila();
        configurarBuscador();
        configurarBotonCrearLimpiar();
        ajustarAnchoColumnas();
    }

    // ==================== CIUDADES (SIN DUPLICAR NUNCA) ====================
    private void cargarCiudades() {
        try {
            ciudades.setAll(ciudadesDAO.obtenerCiudades());
            cidadesComboBox.setItems(ciudades);
            cidadesComboBox.setConverter(new StringConverter<Ciudades>() {
                @Override public String toString(Ciudades c) { return c == null ? "" : c.getNombreCiudad(); }
                @Override public Ciudades fromString(String s) { return null; }
            });
        } catch (SQLException e) {
            showAlert("Error", "Error al cargar ciudades: " + e.getMessage());
        }
    }

    // ==================== TABLA (EXACTAMENTE COMO LA TENÍAS) ====================
    private void configurarColumnasTabla() {
        colCeduCandidato.setCellValueFactory(new PropertyValueFactory<>("cedulaCandidato"));
        colNomCandidato.setCellValueFactory(new PropertyValueFactory<>("nombreCandidato"));
        colTelCandidato.setCellValueFactory(new PropertyValueFactory<>("telefonoCandidato"));
        colDirCandidato.setCellValueFactory(new PropertyValueFactory<>("direccionCandidato"));
        colCiuCandidato.setCellValueFactory(new PropertyValueFactory<>("nombreCiudad"));
        tablaCandidato.setItems(listaCan);
    }

    private void ajustarAnchoColumnas() {
        tablaCandidato.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        colNomCandidato.setPrefWidth(250);
        colDirCandidato.setPrefWidth(280);
        colCeduCandidato.setPrefWidth(120);
        colTelCandidato.setPrefWidth(130);
        colCiuCandidato.setPrefWidth(100);
        colAcciones.setPrefWidth(90);
    }

    private void actualizarTabla() {
        try {
            listaCan.setAll(candidatoDAO.listadoCandidatos());
        } catch (Exception e) {
            showAlert("Error", "Error al cargar candidatos: " + e.getMessage());
        }
    }

    // ==================== BOTÓN CREAR / LIMPIAR (NUEVA MEJORA) ====================
    private void configurarBotonCrearLimpiar() {
        actualizarTextoBotonCrear(false);
        tablaCandidato.getSelectionModel().selectedItemProperty().addListener((obs, old, nuevo) ->
                actualizarTextoBotonCrear(nuevo != null)
        );
    }

    private void actualizarTextoBotonCrear(boolean seleccionado) {
        if (seleccionado) {
            btnCrearCandidato.setText("Limpiar");
           // btnCrearCandidato.setStyle("-fx-background-color: #ff6666; -fx-text-fill: white; -fx-font-weight: bold;");
        } else {
            btnCrearCandidato.setText("Crear Candidato");
           // btnCrearCandidato.setStyle("-fx-background-color: #ff4444; -fx-text-fill: white; -fx-font-weight: bold;");
        }
    }

    // ==================== INSERTAR O LIMPIAR ====================
    @FXML
    public void insertarCandidato(ActionEvent event) {
        if (tablaCandidato.getSelectionModel().getSelectedItem() != null) {
            limpiarFormulario();
            return;
        }

        try {
            if (cedulaCanField.getText().trim().isEmpty()) {
                showAlert("Error", "La cédula es obligatoria.");
                return;
            }
            Ciudades ciudad = cidadesComboBox.getSelectionModel().getSelectedItem();
            if (ciudad == null) {
                showAlert("Error", "Selecciona una ciudad.");
                return;
            }

            long cedula = Long.parseLong(cedulaCanField.getText().trim());

            Candidatos nuevo = new Candidatos(
                    cedula,
                    nombreCanField.getText().trim(),
                    apellidoCanField.getText().trim(),
                    telefonoCanField.getText().trim(),
                    direccionCanField.getText().trim(),
                    ciudad.getIdCiudad()
            );

            candidatoDAO.insertarCandidato(nuevo);
            showAlert("Éxito", "Candidato creado correctamente.");
            limpiarFormulario();
            actualizarTabla();

        } catch (NumberFormatException e) {
            showAlert("Error", "Cédula inválida.");
        } catch (SQLException e) {
            showAlert("Error", "Error al guardar: " + e.getMessage());
        }
    }

    // ==================== SELECCIÓN DE FILA ====================
    private void configurarSeleccionFila() {
        tablaCandidato.setOnMouseClicked(e -> {
            if (e.getClickCount() == 1) {
                Candidatos sel = tablaCandidato.getSelectionModel().getSelectedItem();
                if (sel != null) {
                    Candidatos completo = candidatoDAO.obtenerCandidatoCompleto(sel.getCedulaCandidato());
                    if (completo != null) cargarCampos(completo);
                }
            }
        });
    }

    private void cargarCampos(Candidatos c) {
        cedulaCanField.setText(String.valueOf(c.getCedulaCandidato()));
        nombreCanField.setText(c.getNombreCandidato() != null ? c.getNombreCandidato() : "");
        apellidoCanField.setText(c.getApellidoCandidato() != null ? c.getApellidoCandidato() : "");
        telefonoCanField.setText(c.getTelefonoCandidato() != null ? c.getTelefonoCandidato() : "");
        direccionCanField.setText(c.getDireccionCandidato() != null ? c.getDireccionCandidato() : "");

        cidadesComboBox.getSelectionModel().select(
                ciudades.stream()
                        .filter(ciudad -> ciudad.getIdCiudad() == c.getIdCiudad())
                        .findFirst()
                        .orElse(null)
        );
    }

    // ==================== BOTÓN EDITAR (SOLO ACTIVO EN FILA SELECCIONADA) ====================
    private void agregarBotonEditar() {
        colAcciones.setCellFactory(tc -> new TableCell<Candidatos, Void>() {
            private final Button btn = new Button("Editar");

            {
                actualizarEstadoBoton(false);
                btn.setOnAction(e -> {
                    Candidatos c = getTableView().getItems().get(getIndex());
                    abrirFormularioEdicion(c.getCedulaCandidato());
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || getTableRow() == null) {
                    setGraphic(null);
                } else {
                    setGraphic(btn);
                    boolean seleccionado = getTableRow().isSelected();
                    actualizarEstadoBoton(seleccionado);
                }
            }

            private void actualizarEstadoBoton(boolean seleccionado) {
                if (seleccionado) {
                    btn.setDisable(false);
                    btn.setStyle("-fx-background-color: #ff4444; -fx-text-fill: white; -fx-font-weight: bold;");
                } else {
                    btn.setDisable(true);
                    btn.setStyle("-fx-background-color: #cccccc; -fx-text-fill: #666666;");
                }
            }
        });

        tablaCandidato.getSelectionModel().selectedItemProperty().addListener((obs, old, nuevo) ->
                tablaCandidato.refresh()
        );
    }

    private void abrirFormularioEdicion(long cedula) {
        Candidatos c = candidatoDAO.obtenerCandidatoCompleto(cedula);
        if (c == null) {
            showAlert("Error", "Candidato no encontrado.");
            return;
        }

        Dialog<ButtonType> d = new Dialog<>();
        d.setTitle("Editar Candidato");
        d.setHeaderText("Modificar candidato: " + cedula);
        d.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        d.showAndWait().ifPresent(res -> {
            if (res == ButtonType.OK) guardarCambios(c);
        });
    }

    private void guardarCambios(Candidatos c) {
        try {
            Ciudades ciudad = cidadesComboBox.getSelectionModel().getSelectedItem();
            if (ciudad == null) {
                showAlert("Error", "Selecciona una ciudad.");
                return;
            }

            c.setCedulaCandidato(Long.parseLong(cedulaCanField.getText().trim()));
            c.setNombreCandidato(nombreCanField.getText().trim());
            c.setApellidoCandidato(apellidoCanField.getText().trim());
            c.setTelefonoCandidato(telefonoCanField.getText().trim());
            c.setDireccionCandidato(direccionCanField.getText().trim());
            c.setIdCiudad(ciudad.getIdCiudad());

            candidatoDAO.actualizarCandidato(c);
            showAlert("Éxito", "Candidato actualizado.");
            limpiarFormulario();
            actualizarTabla();
        } catch (Exception e) {
            showAlert("Error", "Error al actualizar: " + e.getMessage());
        }
    }

    // ==================== BUSCADOR ====================
    private void configurarBuscador() {
        filteredList = new FilteredList<>(listaCan, p -> true);
        SortedList<Candidatos> sorted = new SortedList<>(filteredList);
        sorted.comparatorProperty().bind(tablaCandidato.comparatorProperty());
        tablaCandidato.setItems(sorted);

        buscadorField.textProperty().addListener((obs, o, n) -> {
            filteredList.setPredicate(can -> {
                if (n == null || n.isEmpty()) return true;
                String f = n.toLowerCase();
                return String.valueOf(can.getCedulaCandidato()).contains(f) ||
                       (can.getNombreCandidato() != null && can.getNombreCandidato().toLowerCase().contains(f)) ||
                       (can.getApellidoCandidato() != null && can.getApellidoCandidato().toLowerCase().contains(f)) ||
                       (can.getTelefonoCandidato() != null && can.getTelefonoCandidato().contains(f));
            });
        });
    }

    // ==================== FORMULARIO CIUDADES ====================
    @FXML
    private void cargarForCiudades(ActionEvent e) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/polygraph/vista/CiudadesForm.fxml"));
            Parent root = loader.load();
            CiudadesController ctrl = loader.getController();
            ctrl.setOnCiudadAgregadaListener(this::cargarCiudades);

            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("Ciudades");
            stage.setScene(new Scene(root));
            stage.showAndWait();
        } catch (IOException ex) {
            showAlert("Error", "No se pudo abrir formulario de ciudades.");
        }
    }

    // ==================== UTILIDADES ====================
    private void limpiarFormulario() {
        clearFields();
        tablaCandidato.getSelectionModel().clearSelection();
    }

    private void clearFields() {
        cedulaCanField.clear();
        nombreCanField.clear();
        apellidoCanField.clear();
        telefonoCanField.clear();
        direccionCanField.clear();
        cidadesComboBox.getSelectionModel().clearSelection();
    }

    private void showAlert(String titulo, String msg) {
        Alert a = new Alert(titulo.contains("Éxito") ? Alert.AlertType.INFORMATION : Alert.AlertType.ERROR);
        a.setTitle(titulo);
        a.setHeaderText(null);
        a.setContentText(msg);
        a.showAndWait();
    }
}