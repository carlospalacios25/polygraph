package com.polygraph.controlador;

import com.polygraph.dao.ServicioDAO;
import com.polygraph.dao.VisitasDAO;
import com.polygraph.dao.VisitadoresDAO;
import com.polygraph.modelo.Servicio;
import com.polygraph.modelo.Visitas;
import com.polygraph.modelo.Visitadores;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.util.StringConverter;

import java.sql.SQLException;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class VisitasControlador {

    @FXML private TextField idServicioField;
    @FXML private ComboBox<Visitadores> visitadorCombo;
    @FXML private ComboBox<String> tipoPruebaCombo;
    @FXML private ComboBox<String> tipoVisitaCombo;
    @FXML private DatePicker fechaSolicitudPicker;
    @FXML private DatePicker fechaVisitaPicker;
    @FXML private TextField horaVisitaField;
    @FXML private DatePicker fechaEnvioInformePicker;
    @FXML private TextField novedadField;
    @FXML private TextField buscadorField;
    @FXML private GridPane serviciosContainer;
    @FXML private Button btnGuardarVisita;   // 🔹 Botón del formulario (Crear / Guardar cambios)

    private final VisitasDAO visitasDAO = new VisitasDAO();
    private final ServicioDAO servicioDAO = new ServicioDAO();
    private final VisitadoresDAO visitadoresDAO = new VisitadoresDAO();

    // 🔹 Referencia a la visita que se está editando (null si es nueva)
    private Visitas visitaActual = null;

    @FXML
    public void initialize() {
        configurarHoraField();
        configurarCombos();
        cargarVisitadores();
        actualizarTarjetasServicios();
        configurarBuscador();
        configurarEstadoInicialBoton();
    }

    private void configurarEstadoInicialBoton() {
        if (btnGuardarVisita != null) {
            btnGuardarVisita.setText("Crear visita");
        }
    }

    private void configurarHoraField() {
        horaVisitaField.setTextFormatter(new TextFormatter<>(change -> {
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
        horaVisitaField.textProperty().addListener((obs, old, nuevo) -> {
            if (nuevo.length() == 2 && !nuevo.contains(":")) {
                horaVisitaField.setText(nuevo + ":");
                horaVisitaField.positionCaret(3);
            }
        });
    }

    private void configurarCombos() {
        tipoPruebaCombo.setItems(FXCollections.observableArrayList("VALI. RESI.", "VDV", "VDP"));
        tipoVisitaCombo.setItems(FXCollections.observableArrayList(
                "PRE EMPLEO", "ENFASIS EN TELETRABAJO", "TIPO OEA",
                "PRE - ENFASIS VEHICULAR", "RUTINA"
        ));
    }

    private void cargarVisitadores() {
        try {
            List<Visitadores> visitadores = visitadoresDAO.obtenerVisitadores();
            visitadorCombo.setItems(FXCollections.observableArrayList(visitadores));

            visitadorCombo.setConverter(new StringConverter<Visitadores>() {
                @Override
                public String toString(Visitadores v) {
                    if (v == null) return "";
                    return v.getNombreVisitador() +
                           (v.getZonasVisitador() != null && !v.getZonasVisitador().isBlank()
                                   ? " (" + v.getZonasVisitador() + ")"
                                   : "");
                }

                @Override
                public Visitadores fromString(String string) {
                    return null;
                }
            });
        } catch (SQLException e) {
            showAlert("Error", "Error al cargar visitadores: " + e.getMessage());
        }
    }

    private boolean validarCampos() {
        if (idServicioField.getText().isEmpty()) { showAlert("Error", "ID Servicio requerido."); return false; }
        if (visitadorCombo.getValue() == null) { showAlert("Error", "Selecciona un visitador."); return false; }
        if (tipoPruebaCombo.getValue() == null) { showAlert("Error", "Selecciona tipo de prueba."); return false; }
        if (tipoVisitaCombo.getValue() == null) { showAlert("Error", "Selecciona tipo de visita."); return false; }
        if (fechaSolicitudPicker.getValue() == null) { showAlert("Error", "Fecha solicitud requerida."); return false; }
        //if (fechaVisitaPicker.getValue() == null) { showAlert("Error", "Fecha visita requerida."); return false; }
        //if (!horaVisitaField.getText().matches("\\d{2}:\\d{2}")) { showAlert("Error", "Hora inválida (HH:MM)."); return false; }
        //if (fechaEnvioInformePicker.getValue() == null) { showAlert("Error", "Fecha envío informe requerida."); return false; }
        return true;
    }

    // 🔹 ÚNICO método público para el botón del formulario
    @FXML
    private void guardarVisita() {
        if (!validarCampos()) return;

        try {
            int idServicio = Integer.parseInt(idServicioField.getText());
            Visitadores visitadorSel = visitadorCombo.getValue();

            // 1️⃣ VALIDAR QUE EL SERVICIO REQUIERA VISITA
            boolean requiereVisita = servicioDAO.servicioRequiereVisita(idServicio);
            if (!requiereVisita) {
                showAlert("Error",
                        "El servicio " + idServicio +
                        " no está configurado para realizar visita.\n" +
                        "No es posible registrar una visita para este servicio.");
                return;
            }

            // 2️⃣ VALIDAR QUE NO EXISTA YA UNA VISITA PARA ESTE SERVICIO (si no estamos editando)
            if (visitaActual == null) {
                Visitas existente = visitasDAO.obtenerVisitaPorServicio(idServicio);
                if (existente != null) {
                    showAlert("Error", "Ya existe una visita para este servicio. Solo puedes modificarla.");
                    // Cargarla al formulario para que la edites
                    cargarCamposVisita(existente);
                    visitaActual = existente;
                    if (btnGuardarVisita != null) btnGuardarVisita.setText("Guardar cambios");
                    return;
                }
            }

            if (visitaActual == null) {
                // 👉 CREAR
                Visitas nueva = new Visitas(
                        0,
                        idServicio,
                        visitadorSel.getIdVisitador(),
                        tipoPruebaCombo.getValue(),
                        tipoVisitaCombo.getValue(),
                        fechaSolicitudPicker.getValue()
                );
                visitasDAO.insertarVisita(nueva);
                showAlert("Éxito", "Visita creada con ID: " + nueva.getIdVisita());
            } else {
                // 👉 EDITAR (UPDATE)
                visitaActual.setIdServicio(idServicio);
                visitaActual.setIdVisitador(visitadorSel.getIdVisitador());
                visitaActual.setTipo_Prueba(tipoPruebaCombo.getValue());
                visitaActual.setTipo_Visita(tipoVisitaCombo.getValue());
                visitaActual.setFechaSolicitud(fechaSolicitudPicker.getValue());
                visitaActual.setFechaVisita(fechaVisitaPicker.getValue());
                visitaActual.setHoraVisita(LocalTime.parse(horaVisitaField.getText()));
                visitaActual.setFechaeInforme(fechaEnvioInformePicker.getValue());
                visitaActual.setNovedadVisita(novedadField.getText());

                visitasDAO.actualizarVisita(visitaActual);
                showAlert("Éxito", "Visita actualizada correctamente.");
            }

            // Reset
            limpiarCampos();
            visitaActual = null;
            if (btnGuardarVisita != null) btnGuardarVisita.setText("Crear visita");
            actualizarTarjetasServicios();

        } catch (SQLException e) {
            showAlert("Error", "Error en BD: " + e.getMessage());
        } catch (Exception e) {
            showAlert("Error", "Datos inválidos: " + e.getMessage());
        }
    }

    // ================== TARJETAS ==================

    private void actualizarTarjetasServicios() {
        serviciosContainer.getChildren().clear();
        try {
            List<Servicio> servicios = servicioDAO.listarServiciosVisita()
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
        VBox card = new VBox(12);
        card.getStyleClass().add("service-card-modern");
        card.setStyle("-fx-padding: 16; -fx-background-radius: 12;");

        ImageView icon = new ImageView();
        icon.getStyleClass().add("card-icon");
        Image img = new Image(getClass().getResourceAsStream("/com/polygraph/imgs/incos-service.png"));
        if (!img.isError()) {
            icon.setImage(img);
            icon.setFitHeight(36);
            icon.setFitWidth(36);
            icon.setPreserveRatio(true);
            icon.setSmooth(true);
        }

        Label title = new Label("Servicio #" + s.getIdServicio());
        title.getStyleClass().add("card-title");

        String fechaSolicitud = s.getFechaSolicitud() != null
                ? s.getFechaSolicitud().format(DateTimeFormatter.ofPattern("dd MMM yyyy"))
                : "Sin fecha";
        String horaSolicitud = s.getHoraSolicitud() != null
                ? s.getHoraSolicitud().format(DateTimeFormatter.ofPattern("HH:mm"))
                : "Sin hora";

        // Datos de la visita (si existe)
        Visitas visita = null;
        String fechaVisitaStr = "Sin visita";
        String nombreVisitadorTxt = "Sin visita";
        String tipoPruebaTxt = "Sin visita";

        try {
            visita = visitasDAO.obtenerVisitaPorServicio(s.getIdServicio());
            if (visita != null) {
                if (visita.getFechaVisita() != null) {
                    fechaVisitaStr = visita.getFechaVisita()
                            .format(DateTimeFormatter.ofPattern("dd MMM yyyy"));
                }
                tipoPruebaTxt = visita.getTipo_Prueba() != null ? visita.getTipo_Prueba() : "N/A";

                // Buscar el nombre del visitador a partir del Id_Visitador
                int idVisitador = visita.getIdVisitador();
                if (visitadorCombo != null && visitadorCombo.getItems() != null) {
                    for (Visitadores vis : visitadorCombo.getItems()) {
                        if (vis.getIdVisitador() == idVisitador) {
                            nombreVisitadorTxt = vis.getNombreVisitador();
                            break;
                        }
                    }
                }
            }
        } catch (SQLException e) {
            // podrías loguear si quieres
        }

        Label subtitle = new Label(
                "Fecha solicitud: " + fechaSolicitud + " a las " + horaSolicitud + "\n" +
                "Cliente: " + (s.getNombreCliente() != null ? s.getNombreCliente() : "N/A") + "\n" +
                "Candidato: " + (s.getNombreCandidato() != null ? s.getNombreCandidato() + " " : "") +
                               (s.getApellidoCandidato() != null ? s.getApellidoCandidato() : "") + "\n" +
                "Fecha visita: " + fechaVisitaStr + "\n" +
                "Visitador: " + nombreVisitadorTxt + "\n" +
                "Tipo de prueba: " + tipoPruebaTxt
        );
        subtitle.getStyleClass().add("card-subtitle");
        subtitle.setWrapText(true);

        Button btnDetalle = new Button("Cargue");
        btnDetalle.getStyleClass().addAll("card-button", "btn-detalle");

        // Cargar al formulario en modo edición / creación según corresponda
        btnDetalle.setOnAction(e -> {
            try {
                limpiarCampos();

                Visitas v = visitasDAO.obtenerVisitaPorServicio(s.getIdServicio());
                if (v != null) {
                    // Modo edición
                    visitaActual = v;
                    cargarCamposVisita(v);
                    if (btnGuardarVisita != null) btnGuardarVisita.setText("Guardar cambios");
                } else {
                    // Modo creación para ese servicio
                    visitaActual = null;
                    idServicioField.setText(String.valueOf(s.getIdServicio()));
                    if (btnGuardarVisita != null) btnGuardarVisita.setText("Crear visita");
                    showAlert("Info", "No hay visita registrada para este servicio. Puedes crear una nueva.");
                }
            } catch (SQLException ex) {
                showAlert("Error", "No se pudo cargar la visita: " + ex.getMessage());
            }
        });

        HBox botones = new HBox(10, btnDetalle);
        botones.setAlignment(Pos.CENTER_RIGHT);
        botones.setStyle("-fx-padding: 8 0 0 0;");

        card.getChildren().addAll(icon, title, subtitle, botones);
        return card;
    }

    private void cargarCamposVisita(Visitas v) {
        if (v == null) return;

        idServicioField.setText(String.valueOf(v.getIdServicio()));

        // Seleccionar visitador en combo
        if (visitadorCombo.getItems() != null) {
            for (Visitadores vis : visitadorCombo.getItems()) {
                if (vis.getIdVisitador() == v.getIdVisitador()) {
                    visitadorCombo.getSelectionModel().select(vis);
                    break;
                }
            }
        }

        tipoPruebaCombo.setValue(v.getTipo_Prueba());
        tipoVisitaCombo.setValue(v.getTipo_Visita());
        fechaSolicitudPicker.setValue(v.getFechaSolicitud());
        fechaVisitaPicker.setValue(v.getFechaVisita());

        if (v.getHoraVisita() != null) {
            horaVisitaField.setText(
                    v.getHoraVisita().format(DateTimeFormatter.ofPattern("HH:mm"))
            );
        } else {
            horaVisitaField.clear();
        }

        fechaEnvioInformePicker.setValue(v.getFechaeInforme());
        novedadField.setText(v.getNovedadVisita() != null ? v.getNovedadVisita() : "");
    }

    private void configurarBuscador() {
        buscadorField.textProperty().addListener((obs, old, nuevo) -> {
            String filtro = nuevo == null ? "" : nuevo.toLowerCase().trim();
            serviciosContainer.getChildren().clear();

            try {
                List<Servicio> filtrados = servicioDAO.listarServiciosVisita().stream()
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
        Alert.AlertType tipo = titulo.equalsIgnoreCase("Error")
                ? Alert.AlertType.ERROR
                : Alert.AlertType.INFORMATION;
        Alert a = new Alert(tipo);
        a.setTitle(titulo);
        a.setContentText(mensaje);
        a.showAndWait();
    }

    @FXML
    private void limpiarCampos() {
        idServicioField.clear();
        if (visitadorCombo != null) {
            visitadorCombo.getSelectionModel().clearSelection();
        }
        tipoPruebaCombo.setValue(null);
        tipoVisitaCombo.setValue(null);
        fechaSolicitudPicker.setValue(null);
        fechaVisitaPicker.setValue(null);
        horaVisitaField.clear();
        fechaEnvioInformePicker.setValue(null);
        novedadField.clear();
    }
}

