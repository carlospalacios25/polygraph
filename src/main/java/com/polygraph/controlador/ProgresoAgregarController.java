package com.polygraph.controlador;

import com.polygraph.dao.ProgresoDAO;
import com.polygraph.modelo.Progreso;
import com.polygraph.util.UsuarioSesion;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import java.sql.SQLException;
import java.time.LocalDate;
import javafx.collections.FXCollections;

public class ProgresoAgregarController {

    @FXML private ComboBox<String> cmbTipoProgreso;
    @FXML private DatePicker dpFecha;
    @FXML private TextArea txtObservacion;
    @FXML private TextField txtUsuario;
    @FXML private Label errorLabel;

    private int idServicio;
    private Runnable onGuardado;
    private final ProgresoDAO dao = new ProgresoDAO();

    public void setDatos(int idServicio, Runnable callback) {
        this.idServicio = idServicio;
        this.onGuardado = callback;
        inicializar();
    }

    @FXML private void initialize() {
        dpFecha.setValue(LocalDate.now());
    }

    private void inicializar() {
        cargarTiposProgreso();
        mostrarUsuarioActual();
    }

    private void mostrarUsuarioActual() {
        String usuario = UsuarioSesion.getUsuarioActual();
        if (usuario != null && !usuario.isBlank()) {
            txtUsuario.setText(usuario);
        } else {
            txtUsuario.setText("No identificado");
            errorLabel.setText("Error: No hay usuario en sesión");
        }
    }

    private void cargarTiposProgreso() {
        try {
            cmbTipoProgreso.setItems(FXCollections.observableArrayList(
                dao.listarNombresTiposProgreso()
            ));
        } catch (SQLException e) {
            errorLabel.setText("Error al cargar tipos de progreso");
        }
    }

    @FXML
    private void guardar() {
        errorLabel.setText("");

        String tipo = cmbTipoProgreso.getValue();
        LocalDate fecha = dpFecha.getValue();
        String obs = txtObservacion.getText().trim();
        String usuario = UsuarioSesion.getUsuarioActual();

        if (tipo == null) {
            errorLabel.setText("Selecciona un tipo de progreso");
            return;
        }
        if (fecha == null) {
            errorLabel.setText("Selecciona una fecha");
            return;
        }
        if (obs.isEmpty()) {
            errorLabel.setText("Escribe una observación");
            return;
        }
        if (usuario == null || usuario.isBlank()) {
            errorLabel.setText("No hay usuario en sesión");
            return;
        }

        try {
            int idTipoProgr = dao.obtenerIdTipoPorNombre(tipo);

            Progreso p = new Progreso();
            p.setIdServicio(idServicio);
            p.setIdTipoProgr(idTipoProgr);
            p.setFechaProgr(fecha);
            p.setObservacionAnte(obs);
            p.setNombreUsuario(usuario); // ← 100% válido en tabla usuarios

            dao.insertar(p);

            if (onGuardado != null) onGuardado.run();
            cerrar();

        } catch (SQLException e) {
            errorLabel.setText("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML private void cancelar() { cerrar(); }

    private void cerrar() {
        Stage stage = (Stage) txtObservacion.getScene().getWindow();
        stage.close();
    }
}