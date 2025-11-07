package com.polygraph.controlador;

import com.polygraph.dao.*;
import com.polygraph.modelo.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.util.StringConverter;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

public class ServiciosControlador {

    @FXML private DatePicker fechaSolField;
    @FXML private TextField horaSolField;
    @FXML private ComboBox<Clientes> cedulaClieField;
    @FXML private ComboBox<Candidatos> cedulaCanField;
    @FXML private ComboBox<Procesos> procesoField;
    @FXML private TextField buscadorField;
    @FXML private GridPane serviciosContainer;

    private final ServicioDAO servicioDAO = new ServicioDAO();
    private final ClienteDAO clienteDAO = new ClienteDAO();
    private final CandidatoDAO candidatoDAO = new CandidatoDAO();
    private final ProcesosDAO procesoDAO = new ProcesosDAO();

    // Caché de procesos: id → nombre
    private final Map<Integer, String> mapaProcesos = new HashMap<>();

    @FXML
    public void initialize() {
        configurarHoraField();
        cargarClientes();
        cargarCandidatos();
        cargarProcesos();
        cargarMapaProcesos();        // Carga el caché una sola vez
        actualizarTarjetasServicios();
        configurarBuscador();
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
                @Override public String toString(Clientes cliente) {
                    return cliente == null ? "" : cliente.getNombreCliente();
                }
                @Override public Clientes fromString(String string) { return null; }
            });
        } catch (SQLException e) {
            showAlert("Error", "Error al cargar Clientes: " + e.getMessage());
        }
    }

    private void cargarCandidatos() {
        ObservableList<Candidatos> candidatos = FXCollections.observableArrayList();
        try {
            candidatos.addAll(candidatoDAO.obtenerCandidatosBox());
            cedulaCanField.setItems(candidatos);
            cedulaCanField.setConverter(new StringConverter<Candidatos>() {
                @Override public String toString(Candidatos candidato) {
                    return candidato == null ? "" : candidato.getNombreCandidato();
                }
                @Override public Candidatos fromString(String string) { return null; }
            });
        } catch (SQLException e) {
            showAlert("Error", "Error al cargar Candidatos: " + e.getMessage());
        }
    }

    private void cargarProcesos() {
        ObservableList<Procesos> procesos = FXCollections.observableArrayList();
        try {
            procesos.addAll(procesoDAO.obtenerProcesosBox());
            procesoField.setItems(procesos);
            procesoField.setConverter(new StringConverter<Procesos>() {
                @Override public String toString(Procesos proceso) {
                    return proceso == null ? "" : proceso.getNombreProceso();
                }
                @Override public Procesos fromString(String string) { return null; }
            });
        } catch (SQLException e) {
            showAlert("Error", "Error al cargar Procesos: " + e.getMessage());
        }
    }

    // --- CARGAR MAPA DE PROCESOS (caché) ---
    private void cargarMapaProcesos() {
        try {
            mapaProcesos.clear();
            for (Procesos p : procesoDAO.obtenerProcesosBox()) {
                mapaProcesos.put(p.getIdProceso(), p.getNombreProceso());
            }
        } catch (SQLException e) {
            showAlert("Error", "Error al cargar caché de procesos: " + e.getMessage());
        }
    }

    // --- NUEVO: obtener nombre del proceso desde caché ---
    private String getNombreProceso(int id) {
        return mapaProcesos.getOrDefault(id, "Desconocido");
    }

    // --- INSERTAR SERVICIO ---
    @FXML
    private void insertarServicio() {
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
            actualizarTarjetasServicios();
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

    // --- ACTUALIZAR TARJETAS ---
    private void actualizarTarjetasServicios() {
        serviciosContainer.getChildren().clear();
        try {
            List<Servicio> servicios = servicioDAO.listarServicios()
                .stream()
                .sorted(Comparator.comparingInt(Servicio::getIdServicio))
                .collect(Collectors.toList());

            int col = 0, row = 0;
            for (Servicio s : servicios) {
                VBox card = crearTarjeta(s);
                serviciosContainer.add(card, col, row);
                col++;
                if (col == 3) {
                    col = 0;
                    row++;
                }
            }
        } catch (SQLException e) {
            showAlert("Error", "No se pudieron cargar los servicios.");
        }
    }

    // --- CREAR TARJETA ---
    private VBox crearTarjeta(Servicio s) {
        VBox card = new VBox(16);
        card.getStyleClass().add("service-card-modern");

        ImageView icon = new ImageView();
        icon.getStyleClass().add("card-icon");

        Label title = new Label("Servicio #" + s.getIdServicio());
        title.getStyleClass().add("card-title");

        String fecha = s.getFechaSolicitud().format(DateTimeFormatter.ofPattern("dd MMM yyyy"));
        String hora = s.getHoraSolicitud().format(DateTimeFormatter.ofPattern("HH:mm"));

        Label subtitle = new Label(
            "Fecha: " + fecha + " a las " + hora + "\n" +
            "Cliente: NIT " + s.getNitCliente() + "\n" +
            "Candidato: " + s.getCedulaCandidato() + "\n" +
            "Proceso: " + getNombreProceso(s.getIdProceso()) + "\n" +
            "Estado: " + s.getEstado()
        );
        subtitle.getStyleClass().add("card-subtitle");

        Button btn = new Button("Ver Detalle");
        btn.getStyleClass().add("card-button");
        btn.setOnAction(e -> mostrarDetalle(s));

        card.getChildren().addAll(icon, title, subtitle, btn);
        return card;
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
            String filtro = (nuevo == null ? "" : nuevo.toLowerCase());
            serviciosContainer.getChildren().clear();
            try {
                for (Servicio s : servicioDAO.listarServicios()) {
                    String texto = (s.getNitCliente() + " " +
                                  s.getCedulaCandidato() + " " +
                                  getNombreProceso(s.getIdProceso()) + " " +
                                  s.getEstado()).toLowerCase();
                    if (texto.contains(filtro)) {
                        VBox card = crearTarjeta(s);
                        // Mantener disposición en grid
                        int index = serviciosContainer.getChildren().size();
                        int col = index % 3;
                        int row = index / 3;
                        serviciosContainer.add(card, col, row);
                    }
                }
            } catch (SQLException ignored) {}
        });
    }

    // --- ALERTA ---
    private void showAlert(String titulo, String mensaje) {
        Alert a = new Alert(titulo.equals("Éxito") ? Alert.AlertType.INFORMATION : Alert.AlertType.ERROR);
        a.setTitle(titulo);
        a.setContentText(mensaje);
        a.showAndWait();
    }

    // --- LIMPIAR CAMPOS ---
    private void limpiarCampos() {
        fechaSolField.setValue(null);
        horaSolField.clear();
        cedulaClieField.setValue(null);
        cedulaCanField.setValue(null);
        procesoField.setValue(null);
    }

    // --- MÉTODO PÚBLICO PARA RECARGAR PROCESOS (si se crean nuevos) ---
    public void recargarProcesos() {
        cargarProcesos();
        cargarMapaProcesos();
    }
}