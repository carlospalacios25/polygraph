package com.polygraph.controlador;

import com.polygraph.dao.*;
import com.polygraph.modelo.*;
import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.util.StringConverter;

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
    private final Map<Integer, String> mapaProcesos = new HashMap<>();

    // === REFERENCIA AL MAIN CONTROLLER ===
    private MainController mainController;

    public void setMainController(MainController mainController) {
        this.mainController = mainController;
    }

    @FXML
    public void initialize() {
        configurarHoraField();
        cargarClientes();
        cargarCandidatos();
        cargarProcesos();
        cargarMapaProcesos();
        actualizarTarjetasServicios();
        configurarBuscador();
    }

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

    private void cargarClientes() {
        try {
            ObservableList<Clientes> clientes = FXCollections.observableArrayList(clienteDAO.obtenerClienteBox());
            cedulaClieField.setItems(clientes);
            cedulaClieField.setConverter(new StringConverter<Clientes>() {
                @Override public String toString(Clientes c) { return c == null ? "" : c.getNombreCliente(); }
                @Override public Clientes fromString(String s) { return null; }
            });
        } catch (SQLException e) {
            showAlert("Error", "Error al cargar clientes: " + e.getMessage());
        }
    }

    private void cargarCandidatos() {
        try {
            ObservableList<Candidatos> candidatos = FXCollections.observableArrayList(candidatoDAO.obtenerCandidatosBox());
            cedulaCanField.setItems(candidatos);
            cedulaCanField.setConverter(new StringConverter<Candidatos>() {
                @Override public String toString(Candidatos c) { return c == null ? "" : c.getNombreCandidato(); }
                @Override public Candidatos fromString(String s) { return null; }
            });
        } catch (SQLException e) {
            showAlert("Error", "Error al cargar candidatos: " + e.getMessage());
        }
    }

    private void cargarProcesos() {
        try {
            ObservableList<Procesos> procesos = FXCollections.observableArrayList(procesoDAO.obtenerProcesosBox());
            procesoField.setItems(procesos);
            procesoField.setConverter(new StringConverter<Procesos>() {
                @Override public String toString(Procesos p) { return p == null ? "" : p.getNombreProceso(); }
                @Override public Procesos fromString(String s) { return null; }
            });
        } catch (SQLException e) {
            showAlert("Error", "Error al cargar procesos: " + e.getMessage());
        }
    }

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

    private boolean validarCampos() {
        if (fechaSolField.getValue() == null) { showAlert("Error", "Selecciona una fecha."); return false; }
        if (!horaSolField.getText().matches("\\d{2}:\\d{2}")) { showAlert("Error", "Hora inválida (HH:MM)."); return false; }
        if (cedulaClieField.getValue() == null) { showAlert("Error", "Selecciona un cliente."); return false; }
        if (cedulaCanField.getValue() == null) { showAlert("Error", "Selecciona un candidato."); return false; }
        if (procesoField.getValue() == null) { showAlert("Error", "Selecciona un proceso."); return false; }
        return true;
    }

    @FXML
    private void insertarServicio() {
        if (!validarCampos()) return;
        LocalDate fecha = fechaSolField.getValue();
        LocalTime hora = LocalTime.parse(horaSolField.getText());
        Clientes cliente = cedulaClieField.getValue();
        Candidatos candidato = cedulaCanField.getValue();
        Procesos proceso = procesoField.getValue();

        try {
            Servicio servicio = new Servicio(fecha, hora, cliente.getNitCliente(), candidato.getCedulaCandidato(), proceso.getIdProceso());
            servicioDAO.insertarServicio(servicio);
            showAlert("Éxito", "Servicio creado con ID: " + servicio.getIdServicio());
            limpiarCampos();
            actualizarTarjetasServicios();
        } catch (SQLException e) {
            showAlert("Error", e.getErrorCode() == 1452 ? "Cliente, candidato o proceso no válido." : "Error: " + e.getMessage());
        }
    }

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
                if (col == 4) { col = 0; row++; }
            }
        } catch (SQLException e) {
            showAlert("Error", "No se pudieron cargar los servicios.");
        }
    }

    private VBox crearTarjeta(Servicio s) {
        VBox card = new VBox(8);
        card.setPrefWidth(280);
        card.setMaxWidth(280);
        card.getStyleClass().add("service-card");

        // Color según estado
        String estado = s.getEstado() != null ? s.getEstado().toLowerCase() : "pendiente";
        switch (estado) {
            case "finalizado", "entregado" -> card.getStyleClass().add("card-success");
            case "poligrafía realizada", "realizada" -> card.getStyleClass().add("card-primary");
            case "en progreso", "programada" -> card.getStyleClass().add("card-warning");
            default -> card.getStyleClass().add("card-pending");
        }

        Label title = new Label("Servicio #" + s.getIdServicio());
        title.getStyleClass().add("card-title");

        String fecha = s.getFechaSolicitud() != null 
            ? s.getFechaSolicitud().format(DateTimeFormatter.ofPattern("dd MMM yyyy")) 
            : "Sin fecha";

        Label info = new Label("""
            Cliente: %s
            Candidato: %s %s
            Proceso: %s
            Fecha: %s
            Estado: %s
            """.formatted(
                s.getNombreCliente() != null ? s.getNombreCliente() : "N/A",
                s.getNombreCandidato() != null ? s.getNombreCandidato() : "",
                s.getApellidoCandidato() != null ? s.getApellidoCandidato() : "",
                s.getNombreProceso() != null ? s.getNombreProceso() : "N/A",
                fecha,
                s.getEstado() != null ? s.getEstado() : "Pendiente"
            ));
        info.getStyleClass().add("card-text");

        Button btnEditar = new Button("Abrir Expediente");
        btnEditar.getStyleClass().add("btn-primary");
        btnEditar.setOnAction(e -> mainController.abrirExpedienteServicio(s));

        card.getChildren().addAll(title, info, btnEditar);
        return card;
    }


    private void mostrarDetalle(Servicio s) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Servicio #" + s.getIdServicio());
        alert.setHeaderText("Detalles del Servicio");

        VBox content = new VBox(10);
        content.setStyle("-fx-padding: 10; -fx-background-color: #f9f9f9; -fx-background-radius: 8;");

        content.getChildren().addAll(
            crearLabelDetalle("Fecha", s.getFechaSolicitud() != null ? s.getFechaSolicitud().format(DateTimeFormatter.ofPattern("dd MMM yyyy")) : "N/A"),
            crearLabelDetalle("Hora", s.getHoraSolicitud() != null ? s.getHoraSolicitud().format(DateTimeFormatter.ofPattern("HH:mm")) : "N/A"),
            crearLabelDetalle("Cliente", s.getNombreCliente() != null ? s.getNombreCliente() : "Desconocido"),
            crearLabelDetalle("Candidato", (s.getNombreCandidato() != null ? s.getNombreCandidato() + " " : "") + (s.getApellidoCandidato() != null ? s.getApellidoCandidato() : "")),
            crearLabelDetalle("Proceso", s.getNombreProceso() != null ? s.getNombreProceso() : "N/A"),
            crearLabelDetalle("Estado", s.getEstado() != null ? s.getEstado() : "Pendiente")
        );

        alert.getDialogPane().setContent(content);
        alert.getDialogPane().setMinWidth(500);
        alert.showAndWait();
    }

    private HBox crearLabelDetalle(String titulo, String valor) {
        Label lblTitulo = new Label(titulo + ":");
        lblTitulo.setStyle("-fx-font-weight: bold; -fx-text-fill: #2c3e50;");
        Label lblValor = new Label(valor);
        lblValor.setStyle("-fx-text-fill: #34495e; -fx-wrap-text: true;");
        return new HBox(10, lblTitulo, lblValor);
    }

    private void configurarBuscador() {
        buscadorField.textProperty().addListener((obs, old, nuevo) -> {
            String filtro = nuevo == null ? "" : nuevo.toLowerCase().trim();
            serviciosContainer.getChildren().clear();

            try {
                List<Servicio> filtrados = servicioDAO.listarServicios().stream()
                    .filter(s -> {
                        String texto = (
                            (s.getFechaSolicitud() != null ? s.getFechaSolicitud().toString() : "") + " " +
                            (s.getNombreCliente() != null ? s.getNombreCliente() : "") + " " +
                            (s.getNombreCandidato() != null ? s.getNombreCandidato() : "") + " " +
                            (s.getApellidoCandidato() != null ? s.getApellidoCandidato() : "") + " " +
                            (s.getNombreProceso() != null ? s.getNombreProceso() : "") + " " +
                            (s.getEstado() != null ? s.getEstado() : "")
                        ).toLowerCase();
                        return texto.contains(filtro);
                    })
                    .sorted(Comparator.comparingInt(Servicio::getIdServicio))
                    .collect(Collectors.toList());

                int col = 0, row = 0;
                for (Servicio s : filtrados) {
                    serviciosContainer.add(crearTarjeta(s), col, row);
                    col++;
                    if (col == 4) { col = 0; row++; }
                }
            } catch (SQLException e) {
                showAlert("Error", "Error al filtrar: " + e.getMessage());
            }
        });
    }

    private void showAlert(String titulo, String mensaje) {
        Alert a = new Alert(titulo.equals("Éxito") ? Alert.AlertType.INFORMATION : Alert.AlertType.ERROR);
        a.setTitle(titulo);
        a.setContentText(mensaje);
        a.showAndWait();
    }

    private void limpiarCampos() {
        fechaSolField.setValue(null);
        horaSolField.clear();
        cedulaClieField.setValue(null);
        cedulaCanField.setValue(null);
        procesoField.setValue(null);
    }

    public void recargarProcesos() {
        cargarProcesos();
        cargarMapaProcesos();
    }
}