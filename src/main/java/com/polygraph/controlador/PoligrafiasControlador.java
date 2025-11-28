package com.polygraph.controlador;

import com.polygraph.dao.PoligrafiasDAO;
import com.polygraph.dao.ServicioDAO;
import com.polygraph.dao.PoligrafistasDAO;
import com.polygraph.modelo.Poligrafias;
import com.polygraph.modelo.Servicio;
import com.polygraph.modelo.Poligrafistas;
import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.StringConverter;

public class PoligrafiasControlador {

    // === CAMPOS DEL FORMULARIO (coinciden con tu FXML actual) ===
    @FXML private TextField idServicioField;
    @FXML private ComboBox<Poligrafistas> poligrafistaCombo;
    @FXML private DatePicker fechaAsignacionPicker;
    @FXML private TextField horaProgramacionField;
    @FXML private ComboBox<String> asistenciaCombo;
    @FXML private DatePicker fechaEntregaPicker;

    // === BUSCADOR Y CONTENEDOR DE TARJETAS ===
    @FXML private TextField buscadorField;
    @FXML private GridPane poligrafiasContainer;

    // === BOTONES ===
    @FXML private Button btnGuardarPoligrafia;
    @FXML private Button btnLimpiarPoligrafia;

    private final PoligrafiasDAO poligrafiasDAO = new PoligrafiasDAO();
    private final ServicioDAO servicioDAO = new ServicioDAO();
    private final PoligrafistasDAO poligrafistasDAO = new PoligrafistasDAO();

    private Poligrafias poligrafiaActual = null;

    @FXML
    public void initialize() {
        configurarHoraField();
        configurarCombos();
        cargarPoligrafistas();
        configurarEstadoInicialBoton();
        if (poligrafiasContainer != null) {
            actualizarTarjetasServicios();
        }
        configurarBuscador();
    }

    private void configurarEstadoInicialBoton() {
        if (btnGuardarPoligrafia != null) {
            btnGuardarPoligrafia.setText("Crear poligrafía");
        }
    }

    private void configurarHoraField() {
        if (horaProgramacionField == null) return;

        horaProgramacionField.setTextFormatter(new TextFormatter<>(change -> {
            String text = change.getControlNewText();
            if (text.isEmpty()) return change;
            if (text.length() > 5) return null;
            if (!text.matches("\\d*:?\\d*")) return null;
            if (text.length() == 5 && text.matches("\\d{2}:\\d{2}")) {
                String[] p = text.split(":");
                int h = Integer.parseInt(p[0]);
                int m = Integer.parseInt(p[1]);
                if (h >= 0 && h <= 23 && m >= 0 && m <= 59) return change;
            }
            return change;
        }));

        horaProgramacionField.textProperty().addListener((obs, oldV, newV) -> {
            if (newV.length() == 2 && !newV.contains(":")) {
                horaProgramacionField.setText(newV + ":");
                horaProgramacionField.positionCaret(3);
            }
        });
    }

    private void configurarCombos() {
        if (asistenciaCombo != null) {
            asistenciaCombo.setItems(FXCollections.observableArrayList("Si", "No"));
        }
    }

    private void cargarPoligrafistas() {
        if (poligrafistaCombo == null) return;
        try {
            List<Poligrafistas> lista = poligrafistasDAO.obtenerPoligrafistas();
            poligrafistaCombo.setItems(FXCollections.observableArrayList(lista));
            poligrafistaCombo.setConverter(new StringConverter<Poligrafistas>() {
                @Override public String toString(Poligrafistas p) {
                    if (p == null) return "";
                    return p.getNombrePoligrafista() +
                            (p.getSalaEncargada() != null && !p.getSalaEncargada().isBlank()
                                    ? " (" + p.getSalaEncargada() + ")" : "");
                }
                @Override public Poligrafistas fromString(String string) { return null; }
            });
        } catch (SQLException e) {
            showAlert("Error", "Error al cargar poligrafistas: " + e.getMessage());
        }
    }

    // ✅ Validación básica (obligatorios)
    private boolean validarCampos() {
        if (idServicioField == null || idServicioField.getText().isEmpty()) {
            showAlert("Error", "ID Servicio requerido.");
            return false;
        }
        if (poligrafistaCombo == null || poligrafistaCombo.getValue() == null) {
            showAlert("Error", "Selecciona un poligrafista.");
            return false;
        }
        if (fechaAsignacionPicker == null || fechaAsignacionPicker.getValue() == null) {
            showAlert("Error", "Fecha de asignación requerida.");
            return false;
        }
        if (horaProgramacionField == null || !horaProgramacionField.getText().matches("\\d{2}:\\d{2}")) {
            showAlert("Error", "Hora de programación inválida (HH:MM).");
            return false;
        }
        // asistencia y fechaEntrega las dejas opcionales (comentadas)
        return true;
    }

    @FXML
    private void guardarPoligrafia() {
        if (!validarCampos()) return;

        try {
            int idServicio = Integer.parseInt(idServicioField.getText());
            Poligrafistas poligrafistaSel = poligrafistaCombo.getValue();
            LocalDate fechaAsignacion = fechaAsignacionPicker.getValue();           // obligatorio
            LocalTime horaProgramacion = LocalTime.parse(horaProgramacionField.getText()); // obligatorio

            // ✅ Opcionales: pueden ser null
            String asistencia = (asistenciaCombo != null) ? asistenciaCombo.getValue() : null;
            LocalDate fechaEntrega = (fechaEntregaPicker != null) ? fechaEntregaPicker.getValue() : null;

            // Validar que el servicio requiera poligrafía
            boolean requierePoligrafia = servicioDAO.servicioRequierePoligrafia(idServicio);
            if (!requierePoligrafia) {
                showAlert("Error", "El servicio " + idServicio + " no está configurado para realizar poligrafía.");
                return;
            }

            // Validar conflicto horario SOLO si tenemos fecha y hora (por seguridad extra)
            int idActual = (poligrafiaActual != null) ? poligrafiaActual.getIdPoligrafia() : 0;
            boolean conflicto = poligrafiasDAO.hayConflictoHorario(
                    poligrafistaSel.getIdPoligrafista(),
                    fechaAsignacion,
                    horaProgramacion,
                    idActual
            );
            if (conflicto) {
                showAlert("Error", "El poligrafista ya tiene una prueba programada en un horario muy cercano.");
                return;
            }

            if (poligrafiaActual == null) {
                // CREAR
                Poligrafias nueva = new Poligrafias(
                        idServicio,
                        poligrafistaSel.getIdPoligrafista(),
                        fechaAsignacion,
                        horaProgramacion
                );
                // Campos opcionales
                nueva.setAsistencia(asistencia);
                nueva.setFechaEntrega(fechaEntrega);

                poligrafiasDAO.insertarPoligrafia(nueva);
                showAlert("Éxito", "Poligrafía creada con ID: " + nueva.getIdPoligrafia());
            } else {
                // EDITAR
                poligrafiaActual.setIdServicio(idServicio);
                poligrafiaActual.setIdPoligrafista(poligrafistaSel.getIdPoligrafista());
                poligrafiaActual.setFechaAsignacion(fechaAsignacion);
                poligrafiaActual.setHoraProgramacion(horaProgramacion);
                poligrafiaActual.setAsistencia(asistencia);
                poligrafiaActual.setFechaEntrega(fechaEntrega);

                poligrafiasDAO.actualizarPoligrafia(poligrafiaActual);
                showAlert("Éxito", "Poligrafía actualizada correctamente.");
            }

            limpiarCampos();
            poligrafiaActual = null;
            if (btnGuardarPoligrafia != null) btnGuardarPoligrafia.setText("Crear poligrafía");
            actualizarTarjetasServicios();

        } catch (SQLException e) {
            showAlert("Error", "Error en BD: " + e.getMessage());
        } catch (Exception e) {
            showAlert("Error", "Datos inválidos: " + e.getMessage());
        }
    }

    // ================== TARJETAS ==================
    private void actualizarTarjetasServicios() {
        if (poligrafiasContainer == null) return;
        poligrafiasContainer.getChildren().clear();
        try {
            List<Servicio> servicios = servicioDAO.listarServiciosPoligrafia()
                    .stream()
                    .sorted(Comparator.comparingInt(Servicio::getIdServicio))
                    .collect(Collectors.toList());

            int col = 0, row = 0;
            for (Servicio s : servicios) {
                VBox card = crearTarjetaServicio(s);
                poligrafiasContainer.add(card, col, row);
                col++;
                if (col == 4) { col = 0; row++; }
            }
        } catch (SQLException e) {
            showAlert("Error", "No se pudieron cargar los servicios para poligrafía.");
        }
    }

    private VBox crearTarjetaServicio(Servicio s) {
        // VBox con menos espacio interno
        VBox card = new VBox(4);               // menos espacio vertical
        card.getStyleClass().add("service-card-modern");
        card.setAlignment(Pos.CENTER_LEFT);    // coherente con el CSS
        card.setPrefWidth(260);
        card.setMaxWidth(260);

        // 👉 SIN IMAGEN: solo título y texto
        Label title = new Label("Servicio #" + s.getIdServicio());
        title.getStyleClass().add("card-title");

        String fechaSol = s.getFechaSolicitud() != null
                ? s.getFechaSolicitud().format(DateTimeFormatter.ofPattern("dd MMM yyyy"))
                : "Sin fecha";
        String horaSol = s.getHoraSolicitud() != null
                ? s.getHoraSolicitud().format(DateTimeFormatter.ofPattern("HH:mm"))
                : "Sin hora";

        // Datos de poligrafía (si existe)
        Poligrafias poli = null;
        String fechaAsigStr = "Sin poligrafía";
        String horaProgStr = "";
        String nombrePoligrafistaTxt = "Sin poligrafía";
        String asistenciaTxt = "";

        try {
            poli = poligrafiasDAO.obtenerPoligrafiaPorServicio(s.getIdServicio());
            if (poli != null) {
                if (poli.getFechaAsignacion() != null) {
                    fechaAsigStr = poli.getFechaAsignacion()
                            .format(DateTimeFormatter.ofPattern("dd MMM yyyy"));
                }
                if (poli.getHoraProgramacion() != null) {
                    horaProgStr = poli.getHoraProgramacion()
                            .format(DateTimeFormatter.ofPattern("HH:mm"));
                }
                nombrePoligrafistaTxt = poli.getNombrePoligrafista() != null
                        ? poli.getNombrePoligrafista()
                        : "N/A";
                asistenciaTxt = poli.getAsistencia() != null ? poli.getAsistencia() : "";
            }
        } catch (SQLException e) {
            // puedes loguear si quieres
        }

        // Texto central de la tarjeta (similar a la cita médica)
        Label subtitle = new Label(
                "Fecha solicitud: " + fechaSol + " a las " + horaSol + "\n" +
                "Cliente: " + (s.getNombreCliente() != null ? s.getNombreCliente() : "N/A") + "\n" +
                "Candidato: " + (s.getNombreCandidato() != null ? s.getNombreCandidato() + " " : "") +
                               (s.getApellidoCandidato() != null ? s.getApellidoCandidato() : "") + "\n" +
                "Fecha asignación: " + fechaAsigStr + (horaProgStr.isEmpty() ? "" : " " + horaProgStr) + "\n" +
                "Poligrafista: " + nombrePoligrafistaTxt + "\n" +
                "Asistencia: " + asistenciaTxt
        );
        subtitle.getStyleClass().add("card-subtitle");
        subtitle.setWrapText(true);
        subtitle.setMaxWidth(280);

        Button btnCargar = new Button("Cargar");
        btnCargar.getStyleClass().addAll("card-button", "btn-detalle");

        btnCargar.setOnAction(e -> {
            limpiarCampos();
            if (idServicioField != null) {
                idServicioField.setText(String.valueOf(s.getIdServicio()));
            }

            if (btnGuardarPoligrafia != null) {
                btnGuardarPoligrafia.setText("Crear poligrafía");
            }

            try {
                Poligrafias p = poligrafiasDAO.obtenerPoligrafiaPorServicio(s.getIdServicio());

                if (p != null) {
                    poligrafiaActual = p;
                    cargarCamposPoligrafia(p);

                    javafx.application.Platform.runLater(() -> {
                        if (btnGuardarPoligrafia != null) {
                            btnGuardarPoligrafia.setText("Guardar cambios");
                        }
                    });

                } else {
                    poligrafiaActual = null;
                    javafx.application.Platform.runLater(() -> {
                        if (btnGuardarPoligrafia != null) {
                            btnGuardarPoligrafia.setText("Crear poligrafía");
                        }
                    });
                    showAlert("Info", "No hay poligrafía registrada para este servicio. Puedes crear una nueva.");
                }

            } catch (SQLException ex) {
                showAlert("Error", "No se pudo cargar la poligrafía: " + ex.getMessage());
                javafx.application.Platform.runLater(() -> {
                    if (btnGuardarPoligrafia != null) {
                        btnGuardarPoligrafia.setText("Crear poligrafía");
                    }
                });
            }
        });

        HBox botones = new HBox(8, btnCargar);
        botones.setAlignment(Pos.CENTER);

        card.getChildren().addAll(title, subtitle, botones);
        return card;
    }


    private void cargarCamposPoligrafia(Poligrafias p) {
        if (p == null) return;

        if (idServicioField != null) {
            idServicioField.setText(String.valueOf(p.getIdServicio()));
        }

        if (poligrafistaCombo != null && poligrafistaCombo.getItems() != null) {
            for (Poligrafistas po : poligrafistaCombo.getItems()) {
                if (po.getIdPoligrafista() == p.getIdPoligrafista()) {
                    poligrafistaCombo.getSelectionModel().select(po);
                    break;
                }
            }
        }

        if (fechaAsignacionPicker != null) {
            fechaAsignacionPicker.setValue(p.getFechaAsignacion());
        }

        if (horaProgramacionField != null && p.getHoraProgramacion() != null) {
            horaProgramacionField.setText(p.getHoraProgramacion().format(DateTimeFormatter.ofPattern("HH:mm")));
        }

        if (asistenciaCombo != null) {
            asistenciaCombo.setValue(p.getAsistencia());
        }

        if (fechaEntregaPicker != null) {
            fechaEntregaPicker.setValue(p.getFechaEntrega());
        }
    }

    private void configurarBuscador() {
        if (buscadorField == null || poligrafiasContainer == null) return;

        buscadorField.textProperty().addListener((obs, oldV, newV) -> {
            String filtro = newV == null ? "" : newV.toLowerCase().trim();
            poligrafiasContainer.getChildren().clear();
            try {
                List<Servicio> filtrados = servicioDAO.listarServiciosPoligrafia().stream()
                        .filter(s -> {
                            String texto = (s.getFechaSolicitud() != null ? s.getFechaSolicitud().toString() : "") + " " +
                                    (s.getNombreCliente() != null ? s.getNombreCliente() : "") + " " +
                                    (s.getNombreCandidato() != null ? s.getNombreCandidato() : "") + " " +
                                    (s.getApellidoCandidato() != null ? s.getApellidoCandidato() : "") + " " +
                                    (s.getNombreProceso() != null ? s.getNombreProceso() : "") + " " +
                                    (s.getEstado() != null ? s.getEstado() : "");
                            return texto.toLowerCase().contains(filtro);
                        })
                        .sorted(Comparator.comparingInt(Servicio::getIdServicio))
                        .collect(Collectors.toList());

                int col = 0, row = 0;
                for (Servicio s : filtrados) {
                    poligrafiasContainer.add(crearTarjetaServicio(s), col, row);
                    col++;
                    if (col == 4) { col = 0; row++; }
                }
            } catch (SQLException e) {
                showAlert("Error", "Error al filtrar servicios: " + e.getMessage());
            }
        });
    }

    @FXML
    private void cargarForPoligrafista(ActionEvent e) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/polygraph/vista/PoligrafistasForm.fxml"));
            Parent root = loader.load();
            PoligrafistasController ctrl = loader.getController();
            ctrl.setOnPoligrafistaAgregadoListener(this::cargarPoligrafistas);

            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("Poligrafia");
            stage.setScene(new Scene(root));
            stage.showAndWait();
        } catch (IOException ex) {
            showAlert("Error", "No se pudo abrir formulario de poligrafia.");
        }
    }

    private void showAlert(String titulo, String mensaje) {
        Alert.AlertType tipo = titulo.toLowerCase().contains("error")
                ? Alert.AlertType.ERROR
                : Alert.AlertType.INFORMATION;
        Alert a = new Alert(tipo);
        a.setTitle(titulo);
        a.setContentText(mensaje);
        a.showAndWait();
    }

    @FXML
    private void limpiarCampos() {
        if (idServicioField != null) idServicioField.clear();
        if (poligrafistaCombo != null) poligrafistaCombo.getSelectionModel().clearSelection();
        if (fechaAsignacionPicker != null) fechaAsignacionPicker.setValue(null);
        if (horaProgramacionField != null) horaProgramacionField.clear();
        if (asistenciaCombo != null) asistenciaCombo.setValue(null);
        if (fechaEntregaPicker != null) fechaEntregaPicker.setValue(null);
    }
}