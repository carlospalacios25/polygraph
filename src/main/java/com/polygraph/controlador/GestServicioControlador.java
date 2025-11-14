package com.polygraph.controlador;

import com.polygraph.dao.*;
import com.polygraph.modelo.*;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.sql.SQLException;
import java.time.LocalDate;

public class GestServicioControlador {

    // === INFO GENERAL ===
    @FXML private Label lblId1;
    @FXML private Label lblInfoGeneral;

    // === CAMPOS ===
    @FXML private TextField txtNuevoProgreso;
    @FXML private ComboBox<String> cmbTipoProgreso;
    @FXML private ComboBox<String> cmbTipoDocumento;
    @FXML private ComboBox<String> cmbTipoAnalisis;
    @FXML private TextField txtNuevoAnalisis;

    // === TABLAS ===
    @FXML private TableView<Progreso> tblProgreso;
    @FXML private TableColumn<Progreso, LocalDate> colProgFecha;
    @FXML private TableColumn<Progreso, String> colProgTipo;
    @FXML private TableColumn<Progreso, String> colProgObservacion;
    @FXML private TableColumn<Progreso, String> colProgUsuario;

    @FXML private TableView<Documentos> tblDocumentos;
    @FXML private TableColumn<Documentos, String> colDocTipo;
    @FXML private TableColumn<Documentos, String> colDocNombre;
    @FXML private TableColumn<Documentos, LocalDate> colDocFecha;
    @FXML private TableColumn<Documentos, String> colDocEstado;

    @FXML private TableView<Analisis> tblAnalisis;
    @FXML private TableColumn<Analisis, String> colAnaTipo;
    @FXML private TableColumn<Analisis, String> colAnaContenido;

    // === DAOs ===
    private final ServicioDAO servicioDAO = new ServicioDAO();
    private final ProgresoDAO progresoDAO = new ProgresoDAO();
    private final DocumentoDAO documentoDAO = new DocumentoDAO();
    private final AnalisisDAO analisisDAO = new AnalisisDAO();
   // private final TipoDocumentoDAO tipoDocumentoDAO = new TipoDocumentoDAO();
   // private final TipoAnalisisDAO tipoAnalisisDAO = new TipoAnalisisDAO();

    private Servicio servicio;
    private MainController mainController;

    public void setServicio(Servicio servicio) {
        this.servicio = servicio;
        cargarDatos();
        cargarTablas();
    }

    public void setMainController(MainController mainController) {
        this.mainController = mainController;
    }

    @FXML
    private void initialize() {
        configurarTablas();
        cargarTiposProgreso();
  //      cargarTiposDocumento();
  //      cargarTiposAnalisis();
    }

    private void configurarTablas() {
        colProgFecha.setCellValueFactory(new PropertyValueFactory<>("fechaProgr"));
        colProgTipo.setCellValueFactory(new PropertyValueFactory<>("tipoProgresoNombre"));
        colProgObservacion.setCellValueFactory(new PropertyValueFactory<>("observacionAnte"));
        colProgUsuario.setCellValueFactory(new PropertyValueFactory<>("nombreUsuario"));

        colDocTipo.setCellValueFactory(new PropertyValueFactory<>("tipoDocumento"));
        colDocNombre.setCellValueFactory(new PropertyValueFactory<>("nombreArchivo"));
        colDocFecha.setCellValueFactory(new PropertyValueFactory<>("fechaCarga"));
        colDocEstado.setCellValueFactory(new PropertyValueFactory<>("estadoDocumento"));

        colAnaTipo.setCellValueFactory(new PropertyValueFactory<>("tipoAnalisis"));
        colAnaContenido.setCellValueFactory(new PropertyValueFactory<>("contenido"));
    }

    private void cargarTiposProgreso() {
        try {
            String sql = "SELECT Nombre_Progreso FROM tipos_progreso ORDER BY Nombre_Progreso";
            var lista = new java.util.ArrayList<String>();
            try (var conn = com.polygraph.util.ConexionBD.getInstancia().getConexion();
                 var ps = conn.prepareStatement(sql);
                 var rs = ps.executeQuery()) {
                while (rs.next()) lista.add(rs.getString(1));
            }
            cmbTipoProgreso.setItems(FXCollections.observableArrayList(lista));
        } catch (SQLException e) {
            showAlert("Error", "Error al cargar tipos de progreso: " + e.getMessage());
        }
    }

   /* private void cargarTiposDocumento() {
        try {
            cmbTipoDocumento.setItems(FXCollections.observableArrayList(tipoDocumentoDAO.listarNombres()));
        } catch (SQLException e) {
            showAlert("Error", "Error al cargar tipos de documento: " + e.getMessage());
        }
    }

    private void cargarTiposAnalisis() {
        try {
            cmbTipoAnalisis.setItems(FXCollections.observableArrayList(tipoAnalisisDAO.listarNombres()));
        } catch (SQLException e) {
            showAlert("Error", "Error al cargar tipos de análisis: " + e.getMessage());
        }
    }*/

    private void cargarDatos() {
        if (servicio == null) {
            lblId1.setText("GESTIÓN DEL SERVICIO # —");
            lblInfoGeneral.setText("Información General: Cliente: [Sin datos] Candidato: [Sin datos] Proceso: [Sin datos]");
            return;
        }
        lblId1.setText("GESTIÓN DEL SERVICIO # " + servicio.getIdServicio());
        String info = "Información General : " +
                "Cliente: " + (servicio.getNombreCliente() != null ? servicio.getNombreCliente() : "N/A") +
                " Candidato: " + (servicio.getNombreCandidato() != null ? servicio.getNombreCandidato() : "N/A") + " " +
                (servicio.getApellidoCandidato() != null ? servicio.getApellidoCandidato() : "") +
                " Proceso: " + (servicio.getNombreProceso() != null ? servicio.getNombreProceso() : "N/A");
        lblInfoGeneral.setText(info);
    }

    private void cargarTablas() {
        if (servicio == null) return;
        try {
            tblProgreso.setItems(FXCollections.observableArrayList(progresoDAO.listarPorServicio(servicio.getIdServicio())));
            tblDocumentos.setItems(FXCollections.observableArrayList(documentoDAO.listarPorServicio(servicio.getIdServicio())));
            tblAnalisis.setItems(FXCollections.observableArrayList(analisisDAO.listarPorServicio(servicio.getIdServicio())));
        } catch (SQLException e) {
            showAlert("Error", "Error al cargar datos: " + e.getMessage());
        }
    }

    @FXML
    private void agregarProgresoDirecto() {
        String obs = txtNuevoProgreso.getText().trim();
        String tipo = cmbTipoProgreso.getValue();
        if (obs.isEmpty() || tipo == null) {
            showAlert("Error", "Completa observación y tipo.");
            return;
        }
        try {
            Progreso p = new Progreso();
            p.setIdServicio(servicio.getIdServicio());
            p.setFechaProgr(LocalDate.now());
            p.setIdTipoProgr(progresoDAO.obtenerIdTipoPorNombre(tipo));
            p.setObservacionAnte(obs);
            p.setNombreUsuario("UsuarioActual");
            progresoDAO.insertar(p);
            txtNuevoProgreso.clear();
            cargarTablas();
            showAlert("Éxito", "Progreso agregado.");
        } catch (SQLException e) {
            showAlert("Error", "Error al guardar el progreso: " + e.getMessage());
        }
    }

    @FXML
    private void subirDocumentoDirecto() {
        String tipoDoc = cmbTipoDocumento.getValue();
        if (tipoDoc == null) {
            showAlert("Error", "Selecciona un tipo de documento.");
            return;
        }

        FileChooser fc = new FileChooser();
        fc.setTitle("Seleccionar Documento");
        Stage stage = (Stage) tblDocumentos.getScene().getWindow();
        File fileOrigen = fc.showOpenDialog(stage);
        if (fileOrigen == null) return;

        try {
            String basePath = System.getProperty("user.dir") + "/documentos/" + servicio.getIdServicio();
            File carpeta = new File(basePath);
            if (!carpeta.exists()) carpeta.mkdirs();
            File fileDestino = new File(carpeta, fileOrigen.getName());
            Files.copy(fileOrigen.toPath(), fileDestino.toPath(), StandardCopyOption.REPLACE_EXISTING);

            Documentos d = new Documentos();
            d.setIdServicio(servicio.getIdServicio());
         //   d.setIdTipoDocumento(tipoDocumentoDAO.obtenerIdPorNombre(tipoDoc));
            d.setNombreArchivo(fileOrigen.getName());
            d.setFechaCarga(LocalDate.now());
            d.setEstadoDocumento("Activo");
            d.setRutaArchivo(fileDestino.getAbsolutePath());
            d.setTamanoArchivo(fileOrigen.length());

            documentoDAO.insertar(d);
            cargarTablas();
            showAlert("Éxito", "Documento guardado en:\n" + fileDestino.getAbsolutePath());
        } catch (Exception e) {
            showAlert("Error", "No se pudo guardar el documento:\n" + e.getMessage());
        }
    }

    @FXML
    private void agregarAnalisisDirecto() {
        String tipo = cmbTipoAnalisis.getValue();
        String contenido = txtNuevoAnalisis.getText().trim();
        if (tipo == null || contenido.isEmpty()) {
            showAlert("Error", "Completa tipo y contenido.");
            return;
        }
        try {
            Analisis a = new Analisis();
            a.setIdServicio(servicio.getIdServicio());
            a.setTipoAnalisis(tipo);
            a.setContenido(contenido);
            analisisDAO.insertar(a);
            txtNuevoAnalisis.clear();
            cargarTablas();
            showAlert("Éxito", "Análisis agregado.");
        } catch (SQLException e) {
            showAlert("Error", "Error al guardar análisis: " + e.getMessage());
        }
    }

    @FXML
    private void cancelar() {
        if (mainController != null) {
            mainController.popBreadcrumb();
            mainController.cargarServicio(null);
        }
    }

    private void showAlert(String titulo, String mensaje) {
        Alert alert = new Alert(titulo.contains("Éxito") ? Alert.AlertType.INFORMATION : Alert.AlertType.ERROR);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}