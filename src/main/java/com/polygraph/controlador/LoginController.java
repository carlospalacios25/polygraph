package com.polygraph.controlador;

import com.polygraph.dao.UsuarioDAO;
import com.polygraph.modelo.Usuarios;
import com.polygraph.util.ConexionBD;
import com.polygraph.util.EmailUtil;
import com.polygraph.util.UsuarioSesion;
import jakarta.mail.MessagingException;
import javafx.animation.FadeTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.*;

public class LoginController {

    // ----- LOGIN -----
    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private TextField passwordVisibleField;
    @FXML private CheckBox showPasswordCheck;
    @FXML private Label errorLabel;

    // ----- REGISTRO -----
    @FXML private TextField regNombreField;
    @FXML private TextField regApellidoField;
    @FXML private TextField regUsuarioField;
    @FXML private TextField regCorreoField;
    @FXML private TextField regCodigoField;
    @FXML private PasswordField regPasswordField;
    @FXML private PasswordField regConfirmPasswordField;
    @FXML private TextField regPasswordVisibleField;
    @FXML private TextField regConfirmPasswordVisibleField;
    @FXML private CheckBox regShowPasswordCheck;
    @FXML private Label registerErrorLabel;
    @FXML private Button btnRegistroAccion;

    // ----- FORMULARIOS / PESTAÑAS / TÍTULO -----
    @FXML private VBox loginFormBox;
    @FXML private VBox registerFormBox;
    @FXML private StackPane stackForms;
    @FXML private Button btnTabLogin;
    @FXML private Button btnTabRegistro;
    @FXML private VBox loginHeaderBox;   // NUEVO: contenedor de Polygraph / Bienvenido

    private double xOffset = 0;
    private double yOffset = 0;

    // Código generado para el correo
    private String codigoVerificacionGenerado;
    private boolean codigoEnviado = false;

    @FXML
    public void initialize() {
        // Estado inicial: solo LOGIN visible
        if (loginFormBox != null) {
            loginFormBox.setVisible(true);
            loginFormBox.setManaged(true);
            loginFormBox.setOpacity(1.0);
        }

        if (registerFormBox != null) {
            registerFormBox.setVisible(false);
            registerFormBox.setManaged(false);
            registerFormBox.setOpacity(0.0);
        }

        if (loginHeaderBox != null) {
            loginHeaderBox.setVisible(true);
            loginHeaderBox.setManaged(true);
        }

        activarTab(true);
    }

    // ==========================
    //          LOGIN
    // ==========================

    @FXML
    public void login(ActionEvent event) {
        String username = usernameField.getText();
        String password = passwordField.isVisible()
                ? passwordField.getText()
                : passwordVisibleField.getText();

        if (username == null || username.isBlank() || password == null || password.isBlank()) {
            errorLabel.setText("Ingrese usuario y contraseña.");
            return;
        }

        if (authenticate(username, password)) {
            UsuarioSesion.setUsuarioActual(username);
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/polygraph/vista/MainView.fxml"));
                Parent root = loader.load();
                MainController mainController = loader.getController();
                root.setUserData(mainController);

                Stage stage = (Stage) usernameField.getScene().getWindow();
                stage.setScene(new Scene(root));
                stage.setTitle("Polygraph - Sistema Principal");
                stage.setMaximized(true);
                stage.show();

            } catch (IOException e) {
                errorLabel.setText("Error: " + e.getMessage());
                e.printStackTrace();
            }
        } else {
            if (errorLabel.getText() == null || errorLabel.getText().isBlank()) {
                errorLabel.setText("Usuario o contraseña incorrectos.");
            }
        }
    }

    private boolean authenticate(String username, String password) {
        Connection conn = ConexionBD.getInstancia().getConexion();
        String sql = "SELECT Contrasena_Usu, Activo_Usu FROM usuarios WHERE Nombre_usuario = ?";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                boolean isActive = rs.getBoolean("Activo_Usu");
                if (!isActive) {
                    errorLabel.setText("La cuenta no está activa.");
                    return false;
                }
                String storedHash = rs.getString("Contrasena_Usu");
                String inputHash = hashPassword(password);
                return storedHash.equals(inputHash);
            } else {
                errorLabel.setText("Usuario no encontrado.");
            }
        } catch (SQLException | NoSuchAlgorithmException e) {
            errorLabel.setText("Error en la base de datos: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    // ==========================
    //       REGISTRO
    // ==========================

    @FXML
    private void accionRegistro(ActionEvent event) {
        registerErrorLabel.setText("");

        if (!codigoEnviado) {
            if (enviarCodigoCorreo()) {
                codigoEnviado = true;
                btnRegistroAccion.setText("Registrar");
                registerErrorLabel.setText("Código enviado. Revise su correo.");
            }
        } else {
            registrarUsuario(event);
        }
    }

    private boolean enviarCodigoCorreo() {
        String correo = regCorreoField.getText();

        if (correo == null || correo.isBlank()) {
            registerErrorLabel.setText("Ingrese un correo electrónico.");
            return false;
        }

        registerErrorLabel.setText("Enviando código, por favor espera...");

        codigoVerificacionGenerado = String.valueOf((int) (Math.random() * 900000) + 100000);

        try {
            EmailUtil.enviarCodigoVerificacion(correo, codigoVerificacionGenerado);
            registerErrorLabel.setText("Código enviado. Revisa tu correo.");
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            registerErrorLabel.setText("No se pudo enviar el correo. Revisa la configuración SMTP.");
            return false;
        }
    }

    @FXML
    private void registrarUsuario(ActionEvent event) {
        registerErrorLabel.setText("");

        String nombre = regNombreField.getText();
        String apellido = regApellidoField.getText();
        String usuario = regUsuarioField.getText();
        String correo = regCorreoField.getText();
        String codigoIngresado = regCodigoField.getText();

        String pass = regPasswordField.isVisible()
                ? regPasswordField.getText()
                : regPasswordVisibleField.getText();

        String passConfirm = regConfirmPasswordField.isVisible()
                ? regConfirmPasswordField.getText()
                : regConfirmPasswordVisibleField.getText();

        if (nombre == null || nombre.isBlank() ||
                apellido == null || apellido.isBlank() ||
                usuario == null || usuario.isBlank() ||
                correo == null || correo.isBlank() ||
                pass == null || pass.isBlank() ||
                passConfirm == null || passConfirm.isBlank() ||
                codigoIngresado == null || codigoIngresado.isBlank()) {
            registerErrorLabel.setText("Complete todos los campos.");
            return;
        }

        if (!pass.equals(passConfirm)) {
            registerErrorLabel.setText("Las contraseñas no coinciden.");
            return;
        }

        if (codigoVerificacionGenerado == null) {
            registerErrorLabel.setText("Debe solicitar el código de verificación.");
            return;
        }

        if (!codigoVerificacionGenerado.equals(codigoIngresado.trim())) {
            registerErrorLabel.setText("Código de verificación incorrecto.");
            return;
        }

        if (existeUsuario(usuario)) {
            registerErrorLabel.setText("El nombre de usuario ya existe.");
            return;
        }

        Usuarios nuevo = new Usuarios();
        nuevo.setNombreEmp(nombre);
        nuevo.setApellidoEmp(apellido);
        nuevo.setNombreusuario(usuario);
        nuevo.setCorreoUsu(correo);
        nuevo.setContrasenaUsu(pass);
        nuevo.setActivoUsu(true);
        nuevo.setIdPerfil(4);

        UsuarioDAO dao = new UsuarioDAO();
        try {
            dao.insertarUsuario(nuevo);
            registerErrorLabel.setText("Usuario registrado correctamente.");

            limpiarFormularioRegistro();
            usernameField.setText(usuario);
            mostrarLogin(null);

        } catch (SQLException | NoSuchAlgorithmException e) {
            e.printStackTrace();
            registerErrorLabel.setText("Error al registrar: " + e.getMessage());
        }
    }

    private void limpiarFormularioRegistro() {
        regNombreField.clear();
        regApellidoField.clear();
        regUsuarioField.clear();
        regCorreoField.clear();
        regCodigoField.clear();
        regPasswordField.clear();
        regConfirmPasswordField.clear();
        regPasswordVisibleField.clear();
        regConfirmPasswordVisibleField.clear();
        regShowPasswordCheck.setSelected(false);
        codigoVerificacionGenerado = null;
        codigoEnviado = false;
        if (btnRegistroAccion != null) {
            btnRegistroAccion.setText("Enviar código");
        }
    }

    private boolean existeUsuario(String username) {
        Connection conn = ConexionBD.getInstancia().getConexion();
        String sql = "SELECT 1 FROM usuarios WHERE Nombre_usuario = ?";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            e.printStackTrace();
            return true;
        }
    }

    @FXML
    private void cancelarRegistro(ActionEvent event) {
        limpiarFormularioRegistro();
        registerErrorLabel.setText("");
    }

    // ==========================
    //   VISIBILIDAD CONTRASEÑAS
    // ==========================

    @FXML
    public void togglePasswordVisibility(ActionEvent event) {
        boolean show = showPasswordCheck.isSelected();
        if (show) {
            passwordVisibleField.setText(passwordField.getText());
            passwordVisibleField.setVisible(true);
            passwordVisibleField.setManaged(true);
            passwordField.setVisible(false);
            passwordField.setManaged(false);
        } else {
            passwordField.setText(passwordVisibleField.getText());
            passwordField.setVisible(true);
            passwordField.setManaged(true);
            passwordVisibleField.setVisible(false);
            passwordVisibleField.setManaged(false);
        }
    }

    @FXML
    public void toggleRegistroPasswordVisibility(ActionEvent event) {
        boolean show = regShowPasswordCheck.isSelected();

        if (show) {
            regPasswordVisibleField.setText(regPasswordField.getText());
            regPasswordVisibleField.setVisible(true);
            regPasswordVisibleField.setManaged(true);
            regPasswordField.setVisible(false);
            regPasswordField.setManaged(false);

            regConfirmPasswordVisibleField.setText(regConfirmPasswordField.getText());
            regConfirmPasswordVisibleField.setVisible(true);
            regConfirmPasswordVisibleField.setManaged(true);
            regConfirmPasswordField.setVisible(false);
            regConfirmPasswordField.setManaged(false);
        } else {
            regPasswordField.setText(regPasswordVisibleField.getText());
            regPasswordField.setVisible(true);
            regPasswordField.setManaged(true);
            regPasswordVisibleField.setVisible(false);
            regPasswordVisibleField.setManaged(false);

            regConfirmPasswordField.setText(regConfirmPasswordVisibleField.getText());
            regConfirmPasswordField.setVisible(true);
            regConfirmPasswordField.setManaged(true);
            regConfirmPasswordVisibleField.setVisible(false);
            regConfirmPasswordVisibleField.setManaged(false);
        }
    }

    // ==========================
    //      PESTAÑAS / TRANSICIÓN
    // ==========================

    @FXML
    public void mostrarLogin(ActionEvent event) {
        activarTab(true);
        cambiarFormulario(loginFormBox, registerFormBox);

        if (loginHeaderBox != null) {
            loginHeaderBox.setVisible(true);
            loginHeaderBox.setManaged(true);
        }

        errorLabel.setText("");
    }

    @FXML
    public void mostrarRegistro(ActionEvent event) {
        activarTab(false);
        cambiarFormulario(registerFormBox, loginFormBox);

        if (loginHeaderBox != null) {
            loginHeaderBox.setVisible(false);
            loginHeaderBox.setManaged(false);
        }

        registerErrorLabel.setText("");
    }

    private void activarTab(boolean loginActivo) {
        if (btnTabLogin != null && btnTabRegistro != null) {
            btnTabLogin.setDisable(loginActivo);
            btnTabRegistro.setDisable(!loginActivo);
        }
    }

    private void cambiarFormulario(VBox mostrar, VBox ocultar) {
        if (mostrar == null || ocultar == null) return;

        // preparar el formulario que entra
        mostrar.setVisible(true);
        mostrar.setManaged(true);

        FadeTransition fadeOut = new FadeTransition(Duration.millis(200), ocultar);
        fadeOut.setFromValue(1.0);
        fadeOut.setToValue(0.0);

        FadeTransition fadeIn = new FadeTransition(Duration.millis(200), mostrar);
        fadeIn.setFromValue(0.0);
        fadeIn.setToValue(1.0);

        fadeOut.setOnFinished(e -> {
            ocultar.setVisible(false);
            ocultar.setManaged(false);
        });

        fadeOut.play();
        fadeIn.play();
    }

    // ==========================
    //    VENTANA / UTILITARIOS
    // ==========================

    @FXML
    private void iniciarArrastre(MouseEvent event) {
        xOffset = event.getSceneX();
        yOffset = event.getSceneY();
    }

    @FXML
    private void arrastrarVentana(MouseEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setX(event.getScreenX() - xOffset);
        stage.setY(event.getScreenY() - yOffset);
    }

    @FXML
    private void cerrar(ActionEvent event) {
        Stage stage = (Stage) usernameField.getScene().getWindow();
        stage.close();
    }

    @FXML
    private void cancelar(ActionEvent event) {
        usernameField.clear();
        passwordField.clear();
        passwordVisibleField.clear();
        showPasswordCheck.setSelected(false);
        errorLabel.setText("");
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