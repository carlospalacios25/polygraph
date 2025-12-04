package com.polygraph.controlador;

import com.polygraph.dao.*;
import com.polygraph.modelo.*;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.util.StringConverter;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;

public class ExpedienteServicioControlador {

    // === ESTADO Y SERVICIO ===
    @FXML private Label lblServicioId;
    @FXML private Label lblEstado;
    @FXML private Label lblStatusPoli;
    @FXML private Label lblStatusVisita;
    @FXML private Circle circleEstado;

    // === DATOS DEL SERVICIO ===
    @FXML private TextField txtCliente;
    @FXML private TextField txtProceso;
    @FXML private TextField txtFechaSolicitud;

    // === DATOS DEL CANDIDATO ===
    @FXML private TextField txtCedula;
    @FXML private TextField txtNombreCand;
    @FXML private TextField txtApellidoCand;
    @FXML private TextField txtTelefono;
    @FXML private TextField txtDireccion;
    @FXML private ComboBox<Ciudades> cbCiudad;

    // === POLIGRAFÍA ===
    @FXML private ComboBox<Poligrafistas> cbPoligrafista;
    @FXML private DatePicker dpFechaPoligrafia;
    @FXML private TextField txtHoraPoligrafia;

    // === VISITA (por ahora vacía) ===
    @FXML private ComboBox<Visitadores> cbVisitador;
    @FXML private ComboBox<String> cbTipoPrueba; 
    @FXML private ComboBox<String> cbTipoVisita;
    @FXML private DatePicker dpFechaVisita;
    @FXML private TextArea txtNovedades;
    @FXML private DatePicker dpFechaSolicitud;

    // === DAOs ===
    private final ServicioDAO servicioDAO = new ServicioDAO();
    private final CandidatoDAO candidatoDAO = new CandidatoDAO();
    private final CiudadesDAO ciudadesDAO = new CiudadesDAO();
    private final PoligrafistasDAO poligrafistasDAO = new PoligrafistasDAO();
    private final VisitadoresDAO visitadoresDAO = new VisitadoresDAO();
    private final PoligrafiasDAO poligrafiasDAO = new PoligrafiasDAO();
    private final VisitasDAO visitasDAO = new VisitasDAO();

    // === DATOS EN MEMORIA ===
    private Servicio servicio;
    private Candidatos candidato;
    private Poligrafias poligrafia;
    private Visitas visitaActual;

    // ==================== INICIALIZACIÓN ====================
    @FXML
    private void initialize() {
        cargarCiudades();
        cargarPoligrafistasYVisitadores();
        configurarCombosFijos();
        configurarHoraField();
    }

    public void setServicio(Servicio s) {
        this.servicio = s;
        cargarDatosServicio();
        cargarCandidatoCompleto();
        cargarPoligrafia();
        cargarVisita();
    }

    // ==================== CARGA DE DATOS ====================
    private void cargarDatosServicio() {
        lblServicioId.setText("Servicio #" + servicio.getIdServicio());
        txtCliente.setText(servicio.getNombreCliente() != null ? servicio.getNombreCliente() : "");
        txtProceso.setText(servicio.getNombreProceso() != null ? servicio.getNombreProceso() : "");
        txtFechaSolicitud.setText(servicio.getFechaSolicitud() != null ? servicio.getFechaSolicitud().toString() : "");
        actualizarEstadoVisual(servicio.getEstado());
    }

    private void cargarCandidatoCompleto() {
        try {
            candidato = candidatoDAO.obtenerCandidatoDesdeServicio(servicio.getIdServicio());
            if (candidato != null) {
                txtCedula.setText(String.valueOf(candidato.getCedulaCandidato()));
                txtNombreCand.setText(candidato.getNombreCandidato() != null ? candidato.getNombreCandidato() : "");
                txtApellidoCand.setText(candidato.getApellidoCandidato() != null ? candidato.getApellidoCandidato() : "");
                txtTelefono.setText(candidato.getTelefonoCandidato() != null ? candidato.getTelefonoCandidato() : "");
                txtDireccion.setText(candidato.getDireccionCandidato() != null ? candidato.getDireccionCandidato() : "");

                if (candidato.getIdCiudad() > 0) {
                    cbCiudad.getItems().stream()
                        .filter(c -> c.getIdCiudad() == candidato.getIdCiudad())
                        .findFirst()
                        .ifPresent(cbCiudad.getSelectionModel()::select);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void cargarCiudades() {
        try {
            cbCiudad.setItems(FXCollections.observableArrayList(ciudadesDAO.obtenerCiudades()));
            cbCiudad.setConverter(new StringConverter<Ciudades>() {
                @Override public String toString(Ciudades c) { return c == null ? "" : c.getNombreCiudad(); }
                @Override public Ciudades fromString(String s) { return null; }
            });
        } catch (SQLException e) {
            mostrarError("Error", "No se pudieron cargar las ciudades");
        }
    }

    private void configurarCombosFijos() {
        cbTipoPrueba.setItems(FXCollections.observableArrayList("VALI. RESI.", "VDV", "VDP"));
        cbTipoVisita.setItems(FXCollections.observableArrayList(
            "PRE EMPLEO", "ENFASIS EN TELETRABAJO", "TIPO OEA", "PRE - ENFASIS VEHICULAR", "RUTINA"
        ));
    }

    private void cargarPoligrafistasYVisitadores() {
        try {
            cbPoligrafista.setItems(FXCollections.observableArrayList(poligrafistasDAO.obtenerPoligrafistas()));
            cbPoligrafista.setConverter(new StringConverter<Poligrafistas>() {
                @Override public String toString(Poligrafistas p) {
                    return p != null ? p.getNombrePoligrafista() + (p.getSalaEncargada() != null ? " (" + p.getSalaEncargada() + ")" : "") : "";
                }
                @Override public Poligrafistas fromString(String s) { return null; }
            });

            cbVisitador.setItems(FXCollections.observableArrayList(visitadoresDAO.obtenerVisitadores()));
            cbVisitador.setConverter(new StringConverter<Visitadores>() {
                @Override public String toString(Visitadores v) {
                    return v != null ? v.getNombreVisitador() + (v.getZonasVisitador() != null ? " (" + v.getZonasVisitador() + ")" : "") : "";
                }
                @Override public Visitadores fromString(String s) { return null; }
            });
        } catch (SQLException e) {
            mostrarError("Error", "Error al cargar poligrafistas/visitadores");
        }
    }

    // ==================== POLIGRAFÍA – CARGA ====================
    private void configurarHoraField() {
        if (txtHoraPoligrafia == null) return;

        txtHoraPoligrafia.setTextFormatter(new TextFormatter<>(change -> {
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

        txtHoraPoligrafia.textProperty().addListener((obs, oldV, newV) -> {
            if (newV.length() == 2 && !newV.contains(":")) {
                txtHoraPoligrafia.setText(newV + ":");
                txtHoraPoligrafia.positionCaret(3);
            }
        });
    }
    
    private LocalTime validarYParsearHora(String horaTexto) {
        if (horaTexto == null || horaTexto.trim().isEmpty()) {
            return null;
        }

        String texto = horaTexto.trim();

        // Acepta formatos: 8:00, 08:00, 8:3, 08:30, etc.
        if (!texto.matches("^([0-1]?[0-9]|2[0-3]):[0-5][0-9]$")) {
            return null;
        }

        try {
            return LocalTime.parse(texto);
        } catch (Exception e) {
            return null;
        }
    }
    
    private void cargarPoligrafia() {
        try {
            poligrafia = poligrafiasDAO.obtenerPoligrafiaPorServicio(servicio.getIdServicio());

            if (poligrafia != null) {
                cbPoligrafista.getSelectionModel().select(
                    cbPoligrafista.getItems().stream()
                        .filter(p -> p.getIdPoligrafista() == poligrafia.getIdPoligrafista())
                        .findFirst().orElse(null)
                );
                dpFechaPoligrafia.setValue(poligrafia.getFechaAsignacion());
                txtHoraPoligrafia.setText(poligrafia.getHoraProgramacion() != null ? poligrafia.getHoraProgramacion().toString() : "");

                lblStatusPoli.setText("Poligrafía agendada: " + poligrafia.getFechaAsignacion() + " " + poligrafia.getHoraProgramacion());
                lblStatusPoli.setStyle("-fx-text-fill: #2e7d32; -fx-font-weight: bold;");
            } else {
                cbPoligrafista.getSelectionModel().clearSelection();
                dpFechaPoligrafia.setValue(null);
                txtHoraPoligrafia.clear();
                lblStatusPoli.setText("Sin poligrafía registrada");
                lblStatusPoli.setStyle("-fx-text-fill: #d32f2f;");
            }
        } catch (SQLException e) {
            mostrarError("Error", "Error al cargar poligrafía: " + e.getMessage());
        }
    }
    
    // ==================== GUARDAR POLIGRAFÍA (CREA O ACTUALIZA) ====================
    @FXML
    private void guardarPoligrafia() {
        try {
            Poligrafistas poli = cbPoligrafista.getValue();
            LocalDate fecha = dpFechaPoligrafia.getValue();
            String horaTexto = txtHoraPoligrafia.getText().trim();

            // === VALIDACIONES BÁSICAS ===
            if (poli == null) {
                mostrarError("Error", "Selecciona un poligrafista");
                return;
            }
            if (fecha == null) {
                mostrarError("Error", "Selecciona la fecha de la poligrafía");
                return;
            }
            if (horaTexto.isEmpty()) {
                mostrarError("Error", "Ingresa la hora programada");
                return;
            }
            
            boolean requierePoligrafia = servicioDAO.servicioRequierePoligrafia(servicio.getIdServicio());
            if (!requierePoligrafia) {
                mostrarError("Error", "El servicio " + servicio.getIdServicio() + " no está configurado para realizar poligrafía.");
                return;
            }
            
            LocalTime hora = validarYParsearHora(horaTexto);
            if (hora == null) {
                txtHoraPoligrafia.setStyle("-fx-border-color: #e74c3c; -fx-background-color: #ffeaea;");
                mostrarError("Formato inválido", "La hora debe estar en formato HH:mm\nEjemplos: 08:30, 14:00, 09:15");
                return;
            } else {
                txtHoraPoligrafia.setStyle(""); // quita rojo
            }

            // === VALIDACIÓN DE CONFLICTO HORARIO ===
            int idPoligrafiaActual = (poligrafia != null) ? poligrafia.getIdPoligrafia() : 0;

            boolean hayConflicto = poligrafiasDAO.hayConflictoHorario(
                    poli.getIdPoligrafista(),
                    fecha,
                    hora,
                    idPoligrafiaActual
            );

            if (hayConflicto) {
                mostrarError("Conflicto de horario",
                        "¡El poligrafista ya tiene otra prueba programada en un horario muy cercano!\n" +
                        "Debe haber al menos 60 minutos de diferencia entre citas.");
                return;
            }

            // === GUARDAR (CREAR O ACTUALIZAR) ===
            if (poligrafia == null) {
                poligrafia = new Poligrafias();
                poligrafia.setIdServicio(servicio.getIdServicio());
            }

            poligrafia.setIdPoligrafista(poli.getIdPoligrafista());
            poligrafia.setFechaAsignacion(fecha);
            poligrafia.setHoraProgramacion(hora);

            if (poligrafia.getIdPoligrafia() == 0) {
                poligrafiasDAO.insertarPoligrafia(poligrafia);
                mostrarExito("Éxito", "Poligrafía creada correctamente");
            } else {
                poligrafiasDAO.actualizarPoligrafia(poligrafia);
                mostrarExito("Éxito", "Poligrafía actualizada correctamente");
            }

            cargarPoligrafia(); // recarga estado visual

        } catch (SQLException e) {
            mostrarError("Error de base de datos", "No se pudo guardar la poligrafía:\n" + e.getMessage());
            e.printStackTrace();
        }
    }

    // ==================== ACTUALIZAR CANDIDATO ====================
    @FXML
    private void actualizarCandidato() {
        try {
            if (candidato == null) {
                mostrarError("Error", "No hay candidato asociado");
                return;
            }
            Ciudades ciudad = cbCiudad.getValue();
            if (ciudad == null) {
                mostrarError("Error", "Selecciona una ciudad");
                return;
            }

            candidato.setNombreCandidato(txtNombreCand.getText().trim());
            candidato.setApellidoCandidato(txtApellidoCand.getText().trim());
            candidato.setTelefonoCandidato(txtTelefono.getText().trim().isEmpty() ? null : txtTelefono.getText().trim());
            candidato.setDireccionCandidato(txtDireccion.getText().trim().isEmpty() ? null : txtDireccion.getText().trim());
            candidato.setIdCiudad(ciudad.getIdCiudad());

            int filas = candidatoDAO.actualizarCandidato(candidato);
            if (filas > 0) {
                mostrarExito("Éxito", "Candidato actualizado correctamente");
            } else {
                mostrarError("Error", "No se pudo actualizar");
            }
        } catch (Exception e) {
            mostrarError("Error", "Error: " + e.getMessage());
        }
    }

    // ==================== VISUAL ====================
    private void actualizarEstadoVisual(String estado) {
        lblEstado.setText(estado);

        circleEstado.getStyleClass().removeAll(
            "estado-pendiente", "estado-agendado", "estado-finalizado"
        );

        Color fillColor = switch (estado) {
            case "Pendiente" -> Color.web("#fca5a5");
            case "Agendado" -> Color.web("#fb923c");
            case "Finalizado", "Entregado" -> Color.web("#4ade80");
            case "Publicado" -> Color.web("#60a5fa");
            case "Cancelado" -> Color.web("#94a3b8");
            default -> Color.web("#fca5a5");
        };

        circleEstado.setStroke(fillColor);  // Solo borde
        circleEstado.getStyleClass().add(switch (estado) {
            case "Pendiente" -> "estado-pendiente";
            case "Agendado" -> "estado-agendado";
            default -> "estado-finalizado";
        });
    }

    private void mostrarError(String titulo, String msg) {
        Alert a = new Alert(Alert.AlertType.ERROR, msg, ButtonType.OK);
        a.setTitle(titulo);
        a.setHeaderText(null);
        a.showAndWait();
    }

    private void mostrarExito(String titulo, String msg) {
        Alert a = new Alert(Alert.AlertType.INFORMATION, msg, ButtonType.OK);
        a.setTitle(titulo);
        a.setHeaderText(null);
        a.show();
    }

    private void cargarVisita() {
        try {
            visitaActual = visitasDAO.obtenerVisitaPorServicio(servicio.getIdServicio());
            if (visitaActual != null) {
                // Cargar visitador
                cbVisitador.getItems().stream()
                    .filter(v -> v.getIdVisitador() == visitaActual.getIdVisitador())
                    .findFirst()
                    .ifPresent(cbVisitador.getSelectionModel()::select);

                cbTipoPrueba.setValue(visitaActual.getTipo_Prueba());
                cbTipoVisita.setValue(visitaActual.getTipo_Visita());
                dpFechaSolicitud.setValue(visitaActual.getFechaSolicitud());

                lblStatusVisita.setText("Visita solicitada el " + visitaActual.getFechaSolicitud());
                lblStatusVisita.setStyle("-fx-text-fill: #2e7d32; -fx-font-weight: bold;");
            } else {
                cbVisitador.getSelectionModel().clearSelection();
                cbTipoPrueba.setValue(null);
                cbTipoVisita.setValue(null);
                dpFechaSolicitud.setValue(null);
                lblStatusVisita.setText("Sin visita registrada");
                lblStatusVisita.setStyle("-fx-text-fill: #d32f2f;");
            }
        } catch (SQLException e) {
            mostrarError("Error", "No se pudo cargar la visita");
        }
    }

    @FXML
    private void guardarVisita() {
        try {
            Visitadores visitador = cbVisitador.getValue();
            String tipoPrueba = cbTipoPrueba.getValue();
            String tipoVisita = cbTipoVisita.getValue();
            LocalDate fechaSolicitud = dpFechaSolicitud.getValue();

            if (visitador == null || tipoPrueba == null || tipoVisita == null || fechaSolicitud == null) {
                mostrarError("Error", "Todos los campos son obligatorios");
                return;
            }
            
            // 1) Validar que el servicio requiera visita
            boolean requiereVisita = servicioDAO.servicioRequiereVisita(servicio.getIdServicio());
            if (!requiereVisita) {
                mostrarError("Error",
                        "El servicio " + servicio.getIdServicio() +
                        " no está configurado para realizar visita.\n" +
                        "No es posible registrar una visita para este servicio.");
                return;
            }

            // Si ya existe → actualizar
            if (visitaActual != null) {
                visitaActual.setIdVisitador(visitador.getIdVisitador());
                visitaActual.setTipo_Prueba(tipoPrueba);
                visitaActual.setTipo_Visita(tipoVisita);
                visitaActual.setFechaSolicitud(fechaSolicitud);

                visitasDAO.actualizarVisita(visitaActual);
                mostrarExito("Éxito", "Visita actualizada");
            }
            // Si no existe → crear
            else {
                Visitas nueva = new Visitas();
                nueva.setIdServicio(servicio.getIdServicio());
                nueva.setIdVisitador(visitador.getIdVisitador());
                nueva.setTipo_Prueba(tipoPrueba);
                nueva.setTipo_Visita(tipoVisita);
                nueva.setFechaSolicitud(fechaSolicitud);

                visitasDAO.insertarVisita(nueva);
                visitaActual = nueva;
                mostrarExito("Éxito", "Visita creada correctamente");
            }

            cargarVisita(); // actualiza el label

        } catch (SQLException e) {
            mostrarError("Error BD", e.getMessage());
        }
    }
}