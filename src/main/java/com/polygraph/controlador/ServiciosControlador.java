package com.polygraph.controlador;

import com.polygraph.dao.*;
import com.polygraph.modelo.*;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.util.StringConverter;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import javafx.collections.ObservableList;

public class ServiciosControlador {

    @FXML private DatePicker fechaSolField;
    @FXML private TextField horaSolField;
    @FXML private ComboBox<Clientes> cedulaClieField;
    @FXML private ComboBox<Candidatos> cedulaCanField;
    @FXML private ComboBox<Procesos> procesoField;
    @FXML private TextField buscadorField;
    @FXML private VBox serviciosContainer;

    private final ServicioDAO servicioDAO = new ServicioDAO();
    private final ClienteDAO clienteDAO = new ClienteDAO();
    private final CandidatoDAO candidatoDAO = new CandidatoDAO();
    private final ProcesosDAO procesoDAO = new ProcesosDAO();

    @FXML
    public void initialize() {
        configurarHoraField();
        cargarClientes();
        cargarCandidatos();
        cargarProcesos();
        //actualizarTarjetasServicios();
        //configurarBuscador();
    }

    // --- CONFIGURAR HORA ---
    private void configurarHoraField() {
        horaSolField.setTextFormatter(new TextFormatter<>(change -> {
            String text = change.getControlNewText();
            if (text.isEmpty()) return change;
            if (text.length() > 5) return null;
            if (!text.matches("\\d*:?\\d*")) return null;
            if (text.length() == 5 && text.matches("\\d{2}:\\d{2}")) {
                String[] p = text.split(":");
                int h = Integer.parseInt(p[0]), m = Integer.parseInt(p[1]);
                if (h >= 0 && h <= 23 && m >= 0 && m <= 59) return change;
            }
            return change;
        }));

        horaSolField.textProperty().addListener((obs, old, nuevo) -> {
            if (nuevo.length() == 2 && !nuevo.contains(":")) {
                horaSolField.setText(nuevo + ":");
                horaSolField.positionCaret(3);
            }
        });
    }

    // --- CARGAR COMBOBOX ---
    private void cargarClientes() {
        ObservableList<Clientes> clientes = FXCollections.observableArrayList();
        try {
            clientes.addAll(clienteDAO.obtenerClienteBox());
            cedulaClieField.setItems(clientes);

            cedulaClieField.setConverter(new StringConverter<Clientes>() {
                @Override
                public String toString(Clientes cliente) {
                    return cliente == null ? "" : cliente.getNombreCliente();
                }

                @Override
                public Clientes fromString(String string) {
                    return null;
                }
            });
        } catch (SQLException e) {
            showAlert("Error", "Error al cargar Candidato: " + e.getMessage());
        }
    }
    
    private void cargarCandidatos() {
        ObservableList<Candidatos> candidatos = FXCollections.observableArrayList();
        try {
            candidatos.addAll(candidatoDAO.obtenerCandidatosBox());
            cedulaCanField.setItems(candidatos);

            cedulaCanField.setConverter(new StringConverter<Candidatos>() {
                @Override
                public String toString(Candidatos candidato) {
                    return candidato == null ? "" : candidato.getNombreCandidato();
                }

                @Override
                public Candidatos fromString(String string) {
                    return null;
                }
            });
        } catch (SQLException e) {
            showAlert("Error", "Error al cargar Candidato: " + e.getMessage());
        }
    }

    private void cargarProcesos() {
        ObservableList<Procesos> procesos = FXCollections.observableArrayList();
        try {
            procesos.addAll(procesoDAO.obtenerProcesosBox());
            procesoField.setItems(procesos);

            procesoField.setConverter(new StringConverter<Procesos>() {
                @Override
                public String toString(Procesos proceso) {
                    return proceso == null ? "" : proceso.getNombreProceso();
                }

                @Override
                public Procesos fromString(String string) {
                    return null;
                }
            });
        } catch (SQLException e) {
            showAlert("Error", "Error al cargar Procesos: " + e.getMessage());
        }
    }
/*
    // --- INSERTAR SERVICIO (COMO TÚ LO PIDES) ---
    @FXML
    private void insertarCandidato() {
        try {
            LocalDate fecha = fechaSolField.getValue();
            if (fecha == null) {
                showAlert("Error", "Por favor, selecciona una fecha de solicitud.");
                return;
            }

            String horaText = horaSolField.getText().trim();
            if (horaText.isEmpty() || !horaText.matches("\\d{2}:\\d{2}")) {
                showAlert("Error", "Por favor, ingresa una hora válida (HH:MM).");
                return;
            }
            LocalTime hora = LocalTime.parse(horaText);

            Clientes cliente = cedulaClieField.getSelectionModel().getSelectedItem();
            if (cliente == null) {
                showAlert("Error", "Por favor, selecciona un cliente (NIT).");
                return;
            }

            Candidatos candidato = cedulaCanField.getSelectionModel().getSelectedItem();
            if (candidato == null) {
                showAlert("Error", "Por favor, selecciona un candidato (Cédula).");
                return;
            }

            Procesos proceso = procesoField.getSelectionModel().getSelectedItem();
            if (proceso == null) {
                showAlert("Error", "Por favor, selecciona un proceso.");
                return;
            }

            Servicio servicio = new Servicio(
                fecha,
                hora,
                cliente.getNitCliente(),
                candidato.getCedulaCandidato(),
                proceso.getIdProceso()
            );

            servicioDAO.insertarServicio(servicio);

            showAlert("Éxito", "Servicio creado correctamente con ID: " + servicio.getIdServicio());
            limpiarCampos();
           // actualizarTarjetasServicios();

        } catch (SQLException e) {
            if (e.getErrorCode() == 1452) {
                showAlert("Error", "Datos no válidos: Cliente, Candidato o Proceso no existen.");
            } else {
                showAlert("Error", "Error al insertar: " + e.getMessage());
            }
        } catch (NumberFormatException e) {
            showAlert("Error", "Formato inválido en algún campo.");
        }
    }
    

    /*
    // --- ACTUALIZAR TARJETAS ---
    private void actualizarTarjetasServicios() {
        serviciosContainer.getChildren().clear();
        try {
            for (Servicio s : servicioDAO.listarServicios()) {
                serviciosContainer.getChildren().add(crearTarjeta(s));
            }
        } catch (SQLException e) {
            showAlert("Error", "No se pudieron cargar servicios.");
        }
    }

    // --- CREAR TARJETA ---
    private VBox crearTarjeta(Servicio s) {
        VBox card = new VBox(10);
        card.getStyleClass().add("service-card");

        Label fechaHora = new Label(
            s.getFechaSolicitud().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) +
            " a las " + s.getHoraSolicitud().format(DateTimeFormatter.ofPattern("HH:mm"))
        );
        fechaHora.getStyleClass().add("card-title");

        HBox fila1 = new HBox(30);
        fila1.getChildren().addAll(
            label("NIT:", String.valueOf(s.getNitCliente()), "card-label", "card-value"),
            label("Cédula:", String.valueOf(s.getCedulaCandidato()), "card-label", "card-value")
        );

        HBox fila2 = new HBox(30);
        fila2.getChildren().addAll(
            label("Proceso:", getNombreProceso(s.getIdProceso()), "card-label", "card-value"),
            label("Estado:", s.getEstado(), "card-label", "card-value")
        );

        Button btnDetalle = new Button("Ver Detalle");
        btnDetalle.getStyleClass().add("card-button");
        btnDetalle.setOnAction(e -> mostrarDetalle(s));

        card.getChildren().addAll(fechaHora, fila1, fila2, btnDetalle);
        return card;
    }

    private String getNombreProceso(int id) {
        try {
            Procesos p = procesoDAO.obtenerProceso(id);
            return p != null ? p.getNombre() : "Desconocido";
        } catch (SQLException e) {
            return "Error";
        }
    }

    private HBox label(String titulo, String valor, String... clases) {
        Label l1 = new Label(titulo); l1.getStyleClass().addAll(clases[0], "bold");
        Label l2 = new Label(valor); l2.getStyleClass().add(clases[1]);
        return new HBox(8, l1, l2);
    }

    // --- DETALLE ---
    private void mostrarDetalle(Servicio s) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Servicio #" + s.getIdServicio());
        alert.setHeaderText("Detalles del Servicio");

        VBox content = new VBox(8);
        content.getChildren().addAll(
            new Label("Fecha: " + s.getFechaSolicitud()),
            new Label("Hora: " + s.getHoraSolicitud()),
            new Label("NIT Cliente: " + s.getNitCliente()),
            new Label("Cédula Candidato: " + s.getCedulaCandidato()),
            new Label("Proceso: " + getNombreProceso(s.getIdProceso())),
            new Label("Estado: " + s.getEstado()),
            new Label("Resultado: " + s.getResultado())
        );
        alert.getDialogPane().setContent(content);
        alert.showAndWait();
    }

    // --- BUSCADOR ---
    private void configurarBuscador() {
        buscadorField.textProperty().addListener((obs, old, nuevo) -> {
            String filtro = nuevo == null ? "" : nuevo.toLowerCase();
            serviciosContainer.getChildren().clear();
            try {
                for (Servicio s : servicioDAO.listarServicios()) {
                    String texto = (s.getNitCliente() + " " + s.getCedulaCandidato() + " " +
                                  getNombreProceso(s.getIdProceso()) + " " + s.getEstado()).toLowerCase();
                    if (texto.contains(filtro)) {
                        serviciosContainer.getChildren().add(crearTarjeta(s));
                    }
                }
            } catch (SQLException ignored) {}
        });
    }
    
    */
            // --- ALERTA ---
    private void showAlert(String titulo, String mensaje) {
        Alert a = new Alert(titulo.equals("Éxito") ? Alert.AlertType.INFORMATION : Alert.AlertType.ERROR);
        a.setTitle(titulo);
        a.setContentText(mensaje);
        a.showAndWait();
    }
    
        // --- LIMPIAR ---
    private void limpiarCampos() {
        fechaSolField.setValue(null);
        horaSolField.clear();
        cedulaClieField.setValue(null);
        cedulaCanField.setValue(null);
        procesoField.setValue(null);
    }
}