package com.polygraph.controlador;

import com.polygraph.dao.UsuarioDAO;
import com.polygraph.util.EmailUtil;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;

public class RecuperarContrasenaController {

    @FXML private TextField txtCorreo;
    @FXML private TextField txtCodigo;
    @FXML private PasswordField txtNuevaPass;
    @FXML private PasswordField txtConfirmPass;
    @FXML private CheckBox chkMostrarPass;
    @FXML private Button btnAccion;
    @FXML private Label lblMensaje;

    private final UsuarioDAO usuarioDAO = new UsuarioDAO();
    private String codigoGenerado;
    private boolean codigoEnviado = false;

    @FXML
    private void accionRecuperar() {
        lblMensaje.setText("");

        if (!codigoEnviado) {
            enviarCodigo();
        } else {
            cambiarContrasena();
        }
    }

    private void enviarCodigo() {
        String correo = txtCorreo.getText() != null ? txtCorreo.getText().trim() : "";

        if (correo.isEmpty()) {
            lblMensaje.setText("Ingrese el correo registrado.");
            return;
        }

        try {
            if (!usuarioDAO.existeCorreo(correo)) {
                lblMensaje.setText("No existe un usuario con ese correo.");
                return;
            }

            // Generar código
            codigoGenerado = String.valueOf((int) (Math.random() * 900000) + 100000);

            EmailUtil.enviarCodigoVerificacion(correo, codigoGenerado);

            codigoEnviado = true;
            btnAccion.setText("Cambiar contraseña");

            // Habilitar campos
            txtCodigo.setDisable(false);
            txtNuevaPass.setDisable(false);
            txtConfirmPass.setDisable(false);

            lblMensaje.setText("Código enviado. Revisa tu correo.");

        } catch (SQLException e) {
            e.printStackTrace();
            lblMensaje.setText("Error en BD: " + e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            lblMensaje.setText("No se pudo enviar el correo.");
        }
    }

    private void cambiarContrasena() {
        String correo = txtCorreo.getText() != null ? txtCorreo.getText().trim() : "";
        String codigoIngresado = txtCodigo.getText() != null ? txtCodigo.getText().trim() : "";
        String nueva = txtNuevaPass.getText() != null ? txtNuevaPass.getText() : "";
        String confirm = txtConfirmPass.getText() != null ? txtConfirmPass.getText() : "";

        if (correo.isEmpty() || codigoIngresado.isEmpty()
                || nueva.isEmpty() || confirm.isEmpty()) {
            lblMensaje.setText("Complete todos los campos.");
            return;
        }

        if (!codigoIngresado.equals(codigoGenerado)) {
            lblMensaje.setText("Código de verificación incorrecto.");
            return;
        }

        if (!nueva.equals(confirm)) {
            lblMensaje.setText("Las contraseñas no coinciden.");
            return;
        }

        try {
            String hash = hashPassword(nueva);
            usuarioDAO.actualizarContrasenaPorCorreo(correo, hash);
            lblMensaje.setText("Contraseña actualizada correctamente.");

            // Cerrar después de éxito
            Stage stage = (Stage) lblMensaje.getScene().getWindow();
            stage.close();

        } catch (SQLException | NoSuchAlgorithmException e) {
            e.printStackTrace();
            lblMensaje.setText("Error al actualizar: " + e.getMessage());
        }
    }

    @FXML
    private void toggleMostrarContrasenas() {
        // Si quieres mostrar en campos de texto, aquí podrías implementarlo
        // Por ahora solo dejamos los PasswordField (simple).
    }

    @FXML
    private void cerrar() {
        Stage stage = (Stage) lblMensaje.getScene().getWindow();
        stage.close();
    }

    private String hashPassword(String password) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        byte[] hash = md.digest(password.getBytes());
        StringBuilder hexString = new StringBuilder();
        for (byte b : hash) {
            hexString.append(String.format("%02x", b));
        }
        return hexString.toString();
    }
}
