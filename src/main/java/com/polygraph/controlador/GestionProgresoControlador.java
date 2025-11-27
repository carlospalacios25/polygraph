package com.polygraph.controlador;

import com.polygraph.dao.*;
import com.polygraph.modelo.*;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class GestionProgresoControlador {

    // === CAMPOS DEL FXML (solo los que realmente existen) ===
    @FXML private TextField buscadorField;
    @FXML private GridPane serviciosContainer;

    // === DAOs ===
    private final ServicioDAO servicioDAO = new ServicioDAO();

    // === REFERENCIA AL MAIN CONTROLLER (para el botón Editar) ===
    private MainController mainController;

    public void setMainController(MainController mainController) {
        this.mainController = mainController;
    }

    @FXML
    public void initialize() {
        actualizarTarjetasServicios();
        configurarBuscador();
    }

    private void actualizarTarjetasServicios() {
        if (serviciosContainer == null) return;

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
                if (col == 4) {
                    col = 0;
                    row++;
                }
            }
        } catch (SQLException e) {
            showAlert("Error", "No se pudieron cargar los servicios: " + e.getMessage());
        }
    }

    private VBox crearTarjeta(Servicio s) {
        VBox card = new VBox(6);
        card.getStyleClass().add("service-card-modern");
        card.setAlignment(Pos.CENTER_LEFT);
        card.setPrefWidth(260);
        card.setMaxWidth(260);

        // Título
        Label title = new Label("Servicio #" + s.getIdServicio());
        title.getStyleClass().add("card-title");

        // Información
        String fecha = s.getFechaSolicitud() != null
                ? s.getFechaSolicitud().format(DateTimeFormatter.ofPattern("dd MMM yyyy"))
                : "Sin fecha";
        String hora = s.getHoraSolicitud() != null
                ? s.getHoraSolicitud().format(DateTimeFormatter.ofPattern("HH:mm"))
                : "Sin hora";

        Label subtitle = new Label(
            "Fecha: " + fecha + " a las " + hora + "\n" +
            "Cliente: " + (s.getNombreCliente() != null ? s.getNombreCliente() : "N/A") + "\n" +
            "Candidato: " + (s.getNombreCandidato() != null ? s.getNombreCandidato() + " " : "") +
                          (s.getApellidoCandidato() != null ? s.getApellidoCandidato() : "") + "\n" +
            "Proceso: " + (s.getNombreProceso() != null ? s.getNombreProceso() : "N/A") + "\n" +
            "Estado: " + (s.getEstado() != null ? s.getEstado() : "Pendiente")
        );
        subtitle.getStyleClass().add("card-subtitle");
        subtitle.setWrapText(true);
        subtitle.setMaxWidth(260);

        // Botones
        Button btnDetalle = new Button("Detalle");
        btnDetalle.getStyleClass().addAll("card-button", "btn-detalle");
        btnDetalle.setOnAction(e -> mostrarDetalle(s));

        Button btnEditar = new Button("Editar");
        btnEditar.getStyleClass().addAll("card-button", "btn-editar");
        btnEditar.setOnAction(e -> {
            if (mainController != null) {
                mainController.abrirModificarServicio(s);  // ← Ahora SÍ funciona
            } else {
                showAlert("Error", "Sistema principal no disponible.");
            }
        });

        HBox botones = new HBox(12, btnDetalle, btnEditar);
        botones.setAlignment(Pos.CENTER);
        botones.setStyle("-fx-padding: 12 0 0 0;");

        card.getChildren().addAll(title, subtitle, botones);
        return card;
    }

    private void mostrarDetalle(Servicio s) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Servicio #" + s.getIdServicio());
        alert.setHeaderText("Detalles del Servicio");

        VBox content = new VBox(10);
        content.setStyle("-fx-padding: 15; -fx-background-color: #f9f9f9; -fx-background-radius: 8;");

        content.getChildren().addAll(
            crearLabelDetalle("Fecha", s.getFechaSolicitud() != null ? s.getFechaSolicitud().format(DateTimeFormatter.ofPattern("dd MMM yyyy")) : "N/A"),
            crearLabelDetalle("Hora",  s.getHoraSolicitud() != null ? s.getHoraSolicitud().format(DateTimeFormatter.ofPattern("HH:mm")) : "N/A"),
            crearLabelDetalle("Cliente",    s.getNombreCliente() != null ? s.getNombreCliente() : "Desconocido"),
            crearLabelDetalle("Candidato",  (s.getNombreCandidato() != null ? s.getNombreCandidato() + " " : "") + (s.getApellidoCandidato() != null ? s.getApellidoCandidato() : "")),
            crearLabelDetalle("Proceso",    s.getNombreProceso() != null ? s.getNombreProceso() : "N/A"),
            crearLabelDetalle("Estado",     s.getEstado() != null ? s.getEstado() : "Pendiente")
        );

        alert.getDialogPane().setContent(content);
        alert.getDialogPane().setMinWidth(520);
        alert.showAndWait();
    }

    private HBox crearLabelDetalle(String titulo, String valor) {
        Label lblTitulo = new Label(titulo + ":");
        lblTitulo.setStyle("-fx-font-weight: bold; -fx-text-fill: #2c3e50;");
        Label lblValor = new Label(valor);
        lblValor.setStyle("-fx-text-fill: #34495e;");
        return new HBox(10, lblTitulo, lblValor);
    }

    private void configurarBuscador() {
        if (buscadorField == null) return;

        buscadorField.textProperty().addListener((obs, oldValue, nuevo) -> {
            String filtro = nuevo == null ? "" : nuevo.toLowerCase().trim();
            serviciosContainer.getChildren().clear();

            try {
                List<Servicio> filtrados = servicioDAO.listarServicios().stream()
                    .filter(s -> {
                        String texto = String.join(" ",
                            s.getFechaSolicitud() != null ? s.getFechaSolicitud().toString() : "",
                            s.getNombreCliente() != null ? s.getNombreCliente() : "",
                            s.getNombreCandidato() != null ? s.getNombreCandidato() : "",
                            s.getApellidoCandidato() != null ? s.getApellidoCandidato() : "",
                            s.getNombreProceso() != null ? s.getNombreProceso() : "",
                            s.getEstado() != null ? s.getEstado() : ""
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
                showAlert("Error", "Error al filtrar servicios: " + e.getMessage());
            }
        });
    }

    private void showAlert(String titulo, String mensaje) {
        Alert alert = new Alert(titulo.equals("Éxito") ? Alert.AlertType.INFORMATION : Alert.AlertType.ERROR);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    // === MÉTODO PÚBLICO PARA RECARGAR DESDE OTRO CONTROLADOR ===
    public void recargarServicios() {
        actualizarTarjetasServicios();
        if (buscadorField != null) {
            buscadorField.clear(); // opcional: limpia el filtro al recargar
        }
    }
}