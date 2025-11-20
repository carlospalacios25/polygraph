package com.polygraph.controlador;

import com.polygraph.dao.ClienteDAO;
import com.polygraph.dao.CiudadesDAO;
import com.polygraph.modelo.Ciudades;
import com.polygraph.modelo.Clientes;

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

public class ClienteController {

    // ==================== CONTROLES FXML ====================
    @FXML private TextField nitClienteField;
    @FXML private TextField nombreClieField;
    @FXML private TextField telefonoClieField;
    @FXML private TextField direccionClienField;
    @FXML private ComboBox<Ciudades> cidadesComboBox;
    @FXML private TableView<Clientes> tablaClientes;
    @FXML private TextField buscadorField;
    @FXML private Button btnCrearUsuario;                // ← Asegúrate de que exista este fx:id en el FXML

    @FXML private TableColumn<Clientes, Long> colNitCliente;
    @FXML private TableColumn<Clientes, String> colNomCliente;
    @FXML private TableColumn<Clientes, String> colTelCliente;
    @FXML private TableColumn<Clientes, String> colCiuCliente;
    @FXML private TableColumn<Clientes, Void> colAcciones;

    // ==================== LISTAS Y DAOs ====================
    private final ObservableList<Clientes> listaClien = FXCollections.observableArrayList();
    private final ObservableList<Ciudades> ciudades = FXCollections.observableArrayList();
    private FilteredList<Clientes> filteredList;

    private final ClienteDAO clienteDAO = new ClienteDAO();
    private final CiudadesDAO ciudadesdao = new CiudadesDAO();

    // ==================== INITIALIZE ====================
    @FXML
    public void initialize() {
        cargarCiudadesUnaSolaVez();
        configurarColumnasTabla();
        actualizarTabla();
        agregarBotonEditar();
        configurarSeleccionFila();
        configurarBuscador();
        configurarBotonCrearLimpiar();
        ajustarAnchoColumnas();
    }

    private void cargarCiudadesUnaSolaVez() {
        try {
            ciudades.setAll(ciudadesdao.obtenerCiudades());
            cidadesComboBox.setItems(ciudades);
            cidadesComboBox.setConverter(new StringConverter<Ciudades>() {
                @Override public String toString(Ciudades c) { return c == null ? "" : c.getNombreCiudad(); }
                @Override public Ciudades fromString(String s) { return null; }
            });
        } catch (SQLException e) {
            showAlert("Error", "Error al cargar ciudades: " + e.getMessage());
        }
    }

    private void configurarColumnasTabla() {
        colNitCliente.setCellValueFactory(new PropertyValueFactory<>("nitCliente"));
        colNomCliente.setCellValueFactory(new PropertyValueFactory<>("nombreCliente"));
        colTelCliente.setCellValueFactory(new PropertyValueFactory<>("telefonoCliente"));
        colCiuCliente.setCellValueFactory(new PropertyValueFactory<>("nombreCiudad"));
        tablaClientes.setItems(listaClien);
    }

    private void ajustarAnchoColumnas() {
        tablaClientes.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        colNomCliente.setPrefWidth(300);
        colNitCliente.setPrefWidth(110);
        colTelCliente.setPrefWidth(130);
        colCiuCliente.setPrefWidth(100);
        colAcciones.setPrefWidth(90);
    }

    private void actualizarTabla() {
        try {
            listaClien.setAll(clienteDAO.listadoClientes());
        } catch (Exception e) {
            showAlert("Error", "Error al cargar clientes: " + e.getMessage());
        }
    }

    // ==================== BOTÓN CREAR / LIMPIAR ====================
    private void configurarBotonCrearLimpiar() {
        actualizarTextoBotonCrear(false);
        tablaClientes.getSelectionModel().selectedItemProperty().addListener((obs, old, nuevo) -> 
            actualizarTextoBotonCrear(nuevo != null)
        );
    }

    private void actualizarTextoBotonCrear(boolean seleccionado) {
        if (seleccionado) {
            btnCrearUsuario.setText("Limpiar");
            btnCrearUsuario.setStyle("-fx-background-color: #ff6666; -fx-text-fill: white; -fx-font-weight: bold;");
        } else {
            btnCrearUsuario.setText("Crear Usuario");
            btnCrearUsuario.setStyle("-fx-background-color: #ff4444; -fx-text-fill: white; -fx-font-weight: bold;");
        }
    }

    // ==================== ACCIÓN DEL BOTÓN ====================
    @FXML
    public void insertarCliente(ActionEvent event) {
        if (tablaClientes.getSelectionModel().getSelectedItem() != null) {
            limpiarFormulario();
            return;
        }

        try {
            if (nitClienteField.getText().trim().isEmpty()) {
                showAlert("Error", "El NIT es obligatorio.");
                return;
            }

            Ciudades ciudad = cidadesComboBox.getSelectionModel().getSelectedItem();
            if (ciudad == null) {
                showAlert("Error", "Selecciona una ciudad.");
                return;
            }

            long nit = Long.parseLong(nitClienteField.getText().trim());

            Clientes nuevo = new Clientes(
                nit,
                nombreClieField.getText().trim(),
                telefonoClieField.getText().trim(),
                direccionClienField.getText().trim(),
                ciudad.getIdCiudad()
            );

            clienteDAO.insertarUsuario(nuevo);
            showAlert("Éxito", "Cliente creado correctamente.");
            limpiarFormulario();
            actualizarTabla();

        } catch (NumberFormatException e) {
            showAlert("Error", "NIT inválido.");
        } catch (SQLException e) {
            showAlert("Error", "Error al guardar: " + e.getMessage());
        }
    }

    // ==================== SELECCIÓN DE FILA ====================
    private void configurarSeleccionFila() {
        tablaClientes.setOnMouseClicked(e -> {
            if (e.getClickCount() == 1) {
                Clientes sel = tablaClientes.getSelectionModel().getSelectedItem();
                if (sel != null) {
                    Clientes completo = clienteDAO.obtenerClienteCompleto(sel.getNitCliente());
                    if (completo != null) cargarCampos(completo);
                }
            }
        });
    }

    private void cargarCampos(Clientes c) {
        nitClienteField.setText(String.valueOf(c.getNitCliente()));
        nombreClieField.setText(c.getNombreCliente() != null ? c.getNombreCliente() : "");
        telefonoClieField.setText(c.getTelefonoCliente() != null ? c.getTelefonoCliente() : "");
        direccionClienField.setText(c.getDireccionCliente() != null ? c.getDireccionCliente() : "");

        cidadesComboBox.getSelectionModel().select(
            ciudades.stream()
                    .filter(ciudad -> ciudad.getIdCiudad() == c.getIdCiudad())
                    .findFirst()
                    .orElse(null)
        );
    }

    // ==================== BOTÓN EDITAR EN TABLA (SOLO ACTIVO EN FILA SELECCIONADA) ====================
    private void agregarBotonEditar() {
        colAcciones.setCellFactory(tc -> new TableCell<Clientes, Void>() {
            private final Button btn = new Button("Editar");

            {
                // Estilo base
                actualizarEstadoBoton(false);

                btn.setOnAction(e -> {
                    Clientes cliente = getTableView().getItems().get(getIndex());
                    abrirFormularioEdicion(cliente.getNitCliente());
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || getTableRow() == null) {
                    setGraphic(null);
                } else {
                    setGraphic(btn);
                    // Actualizamos el estado del botón según si esta fila está seleccionada
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

        // Este listener es clave: refresca los botones cuando cambia la selección
        tablaClientes.getSelectionModel().selectedItemProperty().addListener((obs, viejo, nuevo) -> {
            tablaClientes.refresh();
        });
    }
    
    private void abrirFormularioEdicion(long nit) {
        Clientes c = clienteDAO.obtenerClienteCompleto(nit);
        if (c == null) {
            showAlert("Error", "Cliente no encontrado.");
            return;
        }

        Dialog<ButtonType> d = new Dialog<>();
        d.setTitle("Editar Cliente");
        d.setHeaderText("Editar cliente: " + nit);
        d.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        d.showAndWait().ifPresent(res -> {
            if (res == ButtonType.OK) guardarCambios(c);
        });
    }

    private void guardarCambios(Clientes c) {
        try {
            Ciudades ciudad = cidadesComboBox.getSelectionModel().getSelectedItem();
            if (ciudad == null) {
                showAlert("Error", "Selecciona una ciudad.");
                return;
            }

            c.setNitCliente(Long.parseLong(nitClienteField.getText().trim()));
            c.setNombreCliente(nombreClieField.getText().trim());
            c.setTelefonoCliente(telefonoClieField.getText().trim());
            c.setDireccionCliente(direccionClienField.getText().trim());
            c.setIdCiudad(ciudad.getIdCiudad());

            clienteDAO.actualizarCliente(c);
            showAlert("Éxito", "Cliente actualizado.");
            limpiarFormulario();
            actualizarTabla();
        } catch (Exception e) {
            showAlert("Error", "Error al actualizar: " + e.getMessage());
        }
    }

    // ==================== BUSCADOR ====================
    private void configurarBuscador() {
        filteredList = new FilteredList<>(listaClien, p -> true);
        SortedList<Clientes> sorted = new SortedList<>(filteredList);
        sorted.comparatorProperty().bind(tablaClientes.comparatorProperty());
        tablaClientes.setItems(sorted);

        buscadorField.textProperty().addListener((obs, o, n) -> {
            filteredList.setPredicate(cliente -> {
                if (n == null || n.isEmpty()) return true;
                String f = n.toLowerCase();
                return String.valueOf(cliente.getNitCliente()).contains(f) ||
                       (cliente.getNombreCliente() != null && cliente.getNombreCliente().toLowerCase().contains(f)) ||
                       (cliente.getTelefonoCliente() != null && cliente.getTelefonoCliente().contains(f)) ||
                       (cliente.getNombreCiudad() != null && cliente.getNombreCiudad().toLowerCase().contains(f));
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
            ctrl.setOnCiudadAgregadaListener(this::cargarCiudadesUnaSolaVez);

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
        tablaClientes.getSelectionModel().clearSelection();
    }

    private void clearFields() {
        nitClienteField.clear();
        nombreClieField.clear();
        telefonoClieField.clear();
        direccionClienField.clear();
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