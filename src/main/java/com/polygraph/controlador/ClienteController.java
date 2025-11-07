package com.polygraph.controlador;

import com.polygraph.dao.ClienteDAO;
import com.polygraph.modelo.Ciudades;
import com.polygraph.dao.CiudadesDAO;
import com.polygraph.modelo.Clientes;
import java.io.IOException;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.Callback;
import javafx.util.StringConverter;

import java.sql.SQLException;
import java.util.Optional;
import java.util.function.Predicate;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class ClienteController {

    @FXML private TextField nitClienteField;
    @FXML private TextField nombreClieField;
    @FXML private TextField telefonoClieField;
    @FXML private TextField direccionClienField;
    @FXML private ComboBox<Ciudades> cidadesComboBox;
    @FXML private TableView<Clientes> tablaClientes;
    @FXML private TextField buscadorField;
    
    @FXML private TableColumn<Clientes, Long> colNitCliente;
    @FXML private TableColumn<Clientes, String> colNomCliente;
    @FXML private TableColumn<Clientes, String> colTelCliente;
    @FXML private TableColumn<Clientes, String> colCiuCliente;
    @FXML private TableColumn<Clientes, Void> colAcciones;
    
    private ObservableList<Clientes> listaClien = FXCollections.observableArrayList();
    private ObservableList<Ciudades> ciudades = FXCollections.observableArrayList();
    private FilteredList<Clientes> filteredList;
    private boolean isEditing = false;
    private ClienteDAO clienteDAO = new ClienteDAO();
    private CiudadesDAO ciudadesdao = new CiudadesDAO();
    
    @FXML
    public void initialize() {
        cargarCiudades();
        cargarDatosCliente();
        actualizarTabla();
        agregarBotonEditar();
        configurarSeleccionFila();
        configurarBuscador();
    }
    
    @FXML
    private void cargarForCiudades(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/polygraph/vista/CiudadesForm.fxml"));
            Parent root = loader.load();

            // Obtener el controlador
            CiudadesController controller = loader.getController();

            // Pasar el callback: "cuando agregues una ciudad, avísame"
            controller.setOnCiudadAgregadaListener(() -> {
                cargarCiudades();  // ← ¡Actualiza el ComboBox!
            });

            Stage stage = new Stage();
            stage.setTitle("Formulario de Ciudades");
            stage.initModality(Modality.APPLICATION_MODAL); // Bloquea el principal
            stage.setScene(new Scene(root));
            stage.showAndWait(); // ← ¡ESPERA A QUE SE CIERRE!

        } catch (IOException e) {
            showAlert("Error", "No se pudo cargar el formulario de ciudades: " + e.getMessage());
        }
    }
    
    private void cargarDatosCliente() {
        colNitCliente.setCellValueFactory(new PropertyValueFactory<>("nitCliente"));
        colNomCliente.setCellValueFactory(new PropertyValueFactory<>("nombreCliente"));
        colTelCliente.setCellValueFactory(new PropertyValueFactory<>("telefonoCliente"));
        colCiuCliente.setCellValueFactory(new PropertyValueFactory<>("nombreCiudad"));
        
        tablaClientes.setItems(listaClien);
    }
    
    private void actualizarTabla() {
        try {
            listaClien.setAll(clienteDAO.listadoClientes());
            filteredList.setPredicate(cliente -> true); // Resetear filtro
        } catch (Exception e) {
           // showAlert("Error", "Error al cargar clientes: " + e.getMessage());
        }
    }
    
    private void cargarCiudades() {
        
        try {
            ciudades.addAll(ciudadesdao.obtenerCiudades());
            cidadesComboBox.setItems(ciudades);

            cidadesComboBox.setConverter(new StringConverter<Ciudades>() {
                @Override
                public String toString(Ciudades perfil) {
                    return perfil == null ? "" : perfil.getNombreCiudad();
                }

                @Override
                public Ciudades fromString(String string) {
                    return null;
                }
            });
        } catch (SQLException e) {
            showAlert("Error", "Error al cargar ciudades: " + e.getMessage());
        }
    }

    @FXML
    public void insertarCliente(ActionEvent event) {
        try {
            Ciudades ciudadSeleccionada = cidadesComboBox.getSelectionModel().getSelectedItem();
            if (ciudadSeleccionada == null) {
                showAlert("Error", "Por favor, selecciona una ciudad.");
                return;
            }
            
            long nitCliente = Long.parseLong(nitClienteField.getText());
            
            Clientes cliente = new Clientes(
                nitCliente,
                nombreClieField.getText(),
                telefonoClieField.getText(),
                direccionClienField.getText(),
                ciudadSeleccionada.getIdCiudad()
            );

            clienteDAO.insertarUsuario(cliente);

            showAlert("Éxito", "Cliente insertado correctamente.");
            clearFields();
            actualizarTabla();
        } catch (SQLException | NumberFormatException e) {
            showAlert("Error", "Error al insertar: " + e.getMessage());
        }
    }

    private void agregarBotonEditar() {
        colAcciones.setCellFactory(new Callback<TableColumn<Clientes, Void>, TableCell<Clientes, Void>>() {
            @Override
            public TableCell<Clientes, Void> call(final TableColumn<Clientes, Void> param) {
                return new TableCell<Clientes, Void>() {
                    private final Button btn = new Button("Editar");
                    
                    {
                        btn.setStyle("-fx-background-color: #ff4444; -fx-text-fill: white; -fx-font-weight: bold;");
                        
                        btn.setOnAction((ActionEvent event) -> {
                            Clientes cliente = getTableView().getItems().get(getIndex());
                            if (cliente != null) {
                                isEditing = true;
                                abrirFormularioEdicion(cliente.getNitCliente());
                            }
                        });
                    }

                    @Override
                    public void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                        } else {
                            setGraphic(btn);
                            // Verificar si esta fila está seleccionada
                            actualizarEstadoBoton();
                        }
                    }
                    
                    private void actualizarEstadoBoton() {
                        TableRow<Clientes> row = getTableRow();
                        if (row != null && row.isSelected()) {
                            // Fila seleccionada: botón habilitado y con color rojo
                            btn.setDisable(false);
                            btn.setStyle("-fx-background-color: #ff4444; -fx-text-fill: white; -fx-font-weight: bold;");
                        } else {
                            // Fila no seleccionada: botón deshabilitado y gris
                            btn.setDisable(true);
                            btn.setStyle("-fx-background-color: #cccccc; -fx-text-fill: #666666;");
                        }
                    }
                };
            }
        });
        
        // Listener para actualizar botones cuando cambia la selección
        tablaClientes.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            tablaClientes.refresh(); // Refresca toda la tabla para actualizar los botones
        });
    }

    private void configurarSeleccionFila() {
        tablaClientes.getSelectionModel().setSelectionMode(SelectionMode.SINGLE); // Solo una fila seleccionable
        
        tablaClientes.setOnMouseClicked(event -> {
            if (event.getClickCount() == 1) {
                Clientes clienteSeleccionado = tablaClientes.getSelectionModel().getSelectedItem();
                if (clienteSeleccionado != null) {
                    Clientes clienteCompleto = clienteDAO.obtenerClienteCompleto(clienteSeleccionado.getNitCliente());
                    if (clienteCompleto != null) {
                        cargarCampos(clienteCompleto);
                        tablaClientes.refresh(); // Refrescar para actualizar botones
                    } else {
                        showAlert("Error", "No se pudo cargar los datos del cliente seleccionado.");
                    }
                }
            }
        });
    }

    private void configurarBuscador() {
        filteredList = new FilteredList<>(listaClien, p -> true);
        
        SortedList<Clientes> sortedList = new SortedList<>(filteredList);
        sortedList.comparatorProperty().bind(tablaClientes.comparatorProperty());
        tablaClientes.setItems(sortedList);
        
        buscadorField.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredList.setPredicate((Predicate<Clientes>) cliente -> {
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }

                String lowerCaseFilter = newValue.toLowerCase();

                return String.valueOf(cliente.getNitCliente()).contains(lowerCaseFilter) ||
                       (cliente.getNombreCliente() != null && cliente.getNombreCliente().toLowerCase().contains(lowerCaseFilter)) ||
                       (cliente.getTelefonoCliente() != null && cliente.getTelefonoCliente().toLowerCase().contains(lowerCaseFilter)) ||
                       (cliente.getNombreCiudad() != null && cliente.getNombreCiudad().toLowerCase().contains(lowerCaseFilter));
            });
        });
    }

    private void cargarCampos(Clientes cliente) {
        cargarCiudades();
        nitClienteField.setText(String.valueOf(cliente.getNitCliente()));
        nombreClieField.setText(cliente.getNombreCliente() != null ? cliente.getNombreCliente() : "");
        telefonoClieField.setText(cliente.getTelefonoCliente() != null ? cliente.getTelefonoCliente() : "");
        direccionClienField.setText(cliente.getDireccionCliente() != null ? cliente.getDireccionCliente() : "");

        boolean ciudadEncontrada = false;
        for (Ciudades ciudad : cidadesComboBox.getItems()) {
            if (ciudad.getIdCiudad() == cliente.getIdCiudad()) {
                cidadesComboBox.getSelectionModel().select(ciudad);
                ciudadEncontrada = true;
                break;
            }
        }

        if (!ciudadEncontrada) {
            showAlert("Advertencia", "La ciudad del cliente no está disponible en la lista. Selecciona una manualmente.");
            cidadesComboBox.getSelectionModel().clearSelection();
        }
    }

    private void abrirFormularioEdicion(long nitCliente) {
        Clientes clientCompleto = clienteDAO.obtenerClienteCompleto(nitCliente);
        if (clientCompleto == null) {
            showAlert("Error", "No se pudo cargar la información del cliente con NIT: " + nitCliente);
            return;
        }

        if (!isEditing) {
            Dialog<ButtonType> dialog = new Dialog<>();
            dialog.setTitle("Editar Cliente");
            dialog.setHeaderText("Modificar datos del cliente con NIT: " + clientCompleto.getNitCliente());

            ButtonType btnGuardar = new ButtonType("Guardar", ButtonBar.ButtonData.OK_DONE);
            ButtonType btnCancelar = new ButtonType("Cancelar", ButtonBar.ButtonData.CANCEL_CLOSE);
            dialog.getDialogPane().getButtonTypes().addAll(btnGuardar, btnCancelar);

            Optional<ButtonType> result = dialog.showAndWait();

            if (result.isPresent() && result.get() == btnGuardar) {
                guardarCambios(clientCompleto);
            } else {
                clearFields();
            }
        } else {
            guardarCambios(clientCompleto);
            isEditing = false;
        }
    }

    private void guardarCambios(Clientes cliente) {
        try {
            if (nitClienteField.getText().trim().isEmpty()) {
                showAlert("Error", "El NIT del cliente no puede estar vacío.");
                return;
            }

            long nitCliente = Long.parseLong(nitClienteField.getText());
            String nombreCliente = nombreClieField.getText();
            String telefonoCliente = telefonoClieField.getText();
            String direccionCliente = direccionClienField.getText();
            Ciudades ciudadSeleccionada = cidadesComboBox.getSelectionModel().getSelectedItem();
            
            if (ciudadSeleccionada == null) {
                showAlert("Error", "Por favor, selecciona una ciudad.");
                return;
            }

            int idCiudad = ciudadSeleccionada.getIdCiudad();

            cliente.setNitCliente(nitCliente);
            cliente.setNombreCliente(nombreCliente);
            cliente.setTelefonoCliente(telefonoCliente);
            cliente.setDireccionCliente(direccionCliente);
            cliente.setIdCiudad(idCiudad);

            clienteDAO.actualizarCliente(cliente);

            showAlert("Éxito", "Cliente actualizado correctamente.");
            clearFields();
            actualizarTabla();
        } catch (SQLException e) {
            showAlert("Error", "Error al actualizar en la base de datos: " + e.getMessage());
        } catch (NumberFormatException e) {
            showAlert("Error", "El NIT debe ser un número válido.");
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void clearFields() {
        nitClienteField.clear();
        nombreClieField.clear();
        telefonoClieField.clear();
        direccionClienField.clear();
        cidadesComboBox.getSelectionModel().clearSelection();
    }
}