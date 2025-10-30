package com.polygraph.controlador;

import com.polygraph.dao.CandidatoDAO;
import com.polygraph.modelo.Ciudades;
import com.polygraph.dao.CiudadesDAO;
import com.polygraph.modelo.Candidatos;
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

public class CandidatoController {

    @FXML private TextField cedulaCanField;
    @FXML private TextField nombreCanField;
    @FXML private TextField apellidoCanField;
    @FXML private TextField telefonoCanField;
    @FXML private TextField direccionCanField;
    @FXML private ComboBox<Ciudades> cidadesComboBox;
    @FXML private TableView<Candidatos> tablaCandidato;
    @FXML private TextField buscadorField;
    
    @FXML private TableColumn<Candidatos, Long> colCeduCandidato;
    @FXML private TableColumn<Candidatos, String> colNomCandidato;
    @FXML private TableColumn<Candidatos, String> colTelCandidato;
    @FXML private TableColumn<Candidatos, String> colDirCandidato;
    @FXML private TableColumn<Candidatos, String> colCiuCandidato;
    @FXML private TableColumn<Candidatos, Void> colAcciones;
    
    private ObservableList<Candidatos> listaCan = FXCollections.observableArrayList();
    private FilteredList<Candidatos> filteredList;
    private boolean isEditing = false;
    private CandidatoDAO candidatoDAO = new CandidatoDAO();
    
    @FXML
    public void initialize() {
        cargarCiudades();
        cargarDatosCliente();
        actualizarTabla();
        agregarBotonEditar();
        configurarSeleccionFila();
        configurarBuscador();
    }
    
    private void cargarDatosCliente() {
        colCeduCandidato.setCellValueFactory(new PropertyValueFactory<>("cedulaCandidato"));
        colNomCandidato.setCellValueFactory(new PropertyValueFactory<>("nombreCandidato"));
        colTelCandidato.setCellValueFactory(new PropertyValueFactory<>("telefonoCandidato"));
        colDirCandidato.setCellValueFactory(new PropertyValueFactory<>("direccionCandidato"));
        colCiuCandidato.setCellValueFactory(new PropertyValueFactory<>("nombreCiudad"));
        
        tablaCandidato.setItems(listaCan);
    }
    
    private void actualizarTabla() {
        try {
            listaCan.setAll(candidatoDAO.listadoCandidatos());
            filteredList.setPredicate(cliente -> true); // Resetear filtro
        } catch (Exception e) {
           // showAlert("Error", "Error al cargar clientes: " + e.getMessage());
        }
    }
    
    private void cargarCiudades() {
        ObservableList<Ciudades> ciudades = FXCollections.observableArrayList();
        CiudadesDAO dao = new CiudadesDAO();
        try {
            ciudades.addAll(dao.obtenerCiudades());
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
    public void insertarCandidato(ActionEvent event) {
        try {
            Ciudades ciudadSeleccionada = cidadesComboBox.getSelectionModel().getSelectedItem();
            if (ciudadSeleccionada == null) {
                showAlert("Error", "Por favor, selecciona una ciudad.");
                return;
            }
            
            long cedulaCandidato = Long.parseLong(cedulaCanField.getText());
            
            Candidatos datoCandidato = new Candidatos(
                cedulaCandidato,
                nombreCanField.getText(),
                apellidoCanField.getText(),
                telefonoCanField.getText(),
                direccionCanField.getText(),
                ciudadSeleccionada.getIdCiudad()
            );

            candidatoDAO.insertarCandidato(datoCandidato);

            showAlert("Éxito", "Candidato insertado correctamente.");
            clearFields();
            actualizarTabla();
        } catch (SQLException | NumberFormatException e) {
            showAlert("Error", "Error al insertar: " + e.getMessage());
        }
    }
    
    private void showAlert(String title, String message) {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    private void clearFields() {
        cedulaCanField.clear();
        nombreCanField.clear();
        apellidoCanField.clear();
        telefonoCanField.clear();
        direccionCanField.clear();
        cidadesComboBox.getSelectionModel().clearSelection();
    }

    private void agregarBotonEditar() {
        colAcciones.setCellFactory(new Callback<TableColumn<Candidatos, Void>, TableCell<Candidatos, Void>>() {
            @Override
            public TableCell<Candidatos, Void> call(final TableColumn<Candidatos, Void> param) {
                return new TableCell<Candidatos, Void>() {
                    private final Button btn = new Button("Editar");
                    
                    {
                        btn.setStyle("-fx-background-color: #ff4444; -fx-text-fill: white; -fx-font-weight: bold;");
                        
                        btn.setOnAction((ActionEvent event) -> {
                            Candidatos candidato = getTableView().getItems().get(getIndex());
                            if (candidato != null) {
                                isEditing = true;
                                abrirFormularioEdicion(candidato.getCedulaCandidato());
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
                        TableRow<Candidatos> row = getTableRow();
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
        tablaCandidato.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            tablaCandidato.refresh(); // Refresca toda la tabla para actualizar los botones
        });
    }
    
    private void abrirFormularioEdicion(long nitCliente) {
        Candidatos candidatoCompleto = candidatoDAO.obtenerCandidatoCompleto(nitCliente);
        if (candidatoCompleto == null) {
            showAlert("Error", "No se pudo cargar la información del cliente con NIT: " + nitCliente);
            return;
        }

        if (!isEditing) {
            Dialog<ButtonType> dialog = new Dialog<>();
            dialog.setTitle("Editar Cliente");
            dialog.setHeaderText("Modificar datos del candidato con Cedula: " + candidatoCompleto.getCedulaCandidato());

            ButtonType btnGuardar = new ButtonType("Guardar", ButtonBar.ButtonData.OK_DONE);
            ButtonType btnCancelar = new ButtonType("Cancelar", ButtonBar.ButtonData.CANCEL_CLOSE);
            dialog.getDialogPane().getButtonTypes().addAll(btnGuardar, btnCancelar);

            Optional<ButtonType> result = dialog.showAndWait();

            if (result.isPresent() && result.get() == btnGuardar) {
                guardarCambios(candidatoCompleto);
            } else {
                clearFields();
            }
        } else {
            guardarCambios(candidatoCompleto);
            isEditing = false;
        }
    }
    private void guardarCambios(Candidatos candidatos) {
        try {
            if (cedulaCanField.getText().trim().isEmpty()) {
                showAlert("Error", "La deula del candidato no puede estar vacío.");
                return;
            }

            long idenCandidato = Long.parseLong(cedulaCanField.getText());
            String nombreCandidato = nombreCanField.getText();
            String apellidoCandidato = apellidoCanField.getText();
            String telefonoCandidato = telefonoCanField.getText();
            String direccionCandidato = direccionCanField.getText();
            Ciudades ciudadSeleccionada = cidadesComboBox.getSelectionModel().getSelectedItem();
            
            if (ciudadSeleccionada == null) {
                showAlert("Error", "Por favor, selecciona una ciudad.");
                return;
            }

            int idCiudad = ciudadSeleccionada.getIdCiudad();

            candidatos.setCedulaCandidato(idenCandidato);
            candidatos.setNombreCandidato(nombreCandidato);
            candidatos.setApellidoCandidato(apellidoCandidato);
            candidatos.setTelefonoCandidato(telefonoCandidato);
            candidatos.setDireccionCandidato(direccionCandidato);
            candidatos.setIdCiudad(idCiudad);

            candidatoDAO.actualizarCandidato(candidatos);

            showAlert("Éxito", "Candidato actualizado correctamente.");
            clearFields();
            actualizarTabla();
        } catch (SQLException e) {
            showAlert("Error", "Error al actualizar en la base de datos: " + e.getMessage());
        } catch (NumberFormatException e) {
            showAlert("Error", "El NIT debe ser un número válido.");
        }
    }
    
    
    private void configurarSeleccionFila() {
        tablaCandidato.getSelectionModel().setSelectionMode(SelectionMode.SINGLE); // Solo una fila seleccionable
        
        tablaCandidato.setOnMouseClicked(event -> {
            if (event.getClickCount() == 1) {
                Candidatos candidatoActual = tablaCandidato.getSelectionModel().getSelectedItem();
                if (candidatoActual != null) {
                    Candidatos candidatoCompleto = candidatoDAO.obtenerCandidatoCompleto(candidatoActual.getCedulaCandidato());
                    if (candidatoCompleto != null) {
                        cargarCampos(candidatoCompleto);
                        tablaCandidato.refresh(); // Refrescar para actualizar botones
                    } else {
                        showAlert("Error", "No se pudo cargar los datos del cliente seleccionado.");
                    }
                }
            }
        });
    }
    private void cargarCampos(Candidatos candidatos) {
        cargarCiudades();
        cedulaCanField.setText(String.valueOf(candidatos.getCedulaCandidato()));
        nombreCanField.setText(candidatos.getNombreCandidato()!= null ? candidatos.getNombreCandidato(): "");
        apellidoCanField.setText(candidatos.getApellidoCandidato()!= null ? candidatos.getApellidoCandidato(): "");
        telefonoCanField.setText(candidatos.getTelefonoCandidato()!= null ? candidatos.getTelefonoCandidato() : "");
        direccionCanField.setText(candidatos.getDireccionCandidato()!= null ? candidatos.getDireccionCandidato() : "");

        boolean ciudadEncontrada = false;
        for (Ciudades ciudad : cidadesComboBox.getItems()) {
            if (ciudad.getIdCiudad() == candidatos.getIdCiudad()) {
                cidadesComboBox.getSelectionModel().select(ciudad);
                ciudadEncontrada = true;
                break;
            }
        }

        if (!ciudadEncontrada) {
            showAlert("Advertencia", "La ciudad del candidatos no está disponible en la lista. Selecciona una manualmente.");
            cidadesComboBox.getSelectionModel().clearSelection();
        }
    }
    
    private void configurarBuscador() {
        filteredList = new FilteredList<>(listaCan, p -> true);
        
        SortedList<Candidatos> sortedList = new SortedList<>(filteredList);
        sortedList.comparatorProperty().bind(tablaCandidato.comparatorProperty());
        tablaCandidato.setItems(sortedList);
        
        buscadorField.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredList.setPredicate((Predicate<Candidatos>) candidato -> {
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }

                String lowerCaseFilter = newValue.toLowerCase();

                return String.valueOf(candidato.getCedulaCandidato()).contains(lowerCaseFilter) ||
                       (candidato.getNombreCandidato()!= null && candidato.getNombreCandidato().toLowerCase().contains(lowerCaseFilter)) ||
                       (candidato.getApellidoCandidato()!= null && candidato.getApellidoCandidato().toLowerCase().contains(lowerCaseFilter)) ||
                       (candidato.getTelefonoCandidato()!= null && candidato.getTelefonoCandidato().toLowerCase().contains(lowerCaseFilter));
            });
        });
    }
}