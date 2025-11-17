package com.polygraph.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import javafx.application.Platform;
import javafx.scene.control.Alert;

public class ConexionBD {
    private static ConexionBD instancia;
    private Connection conexion;

    private final String URL = "jdbc:mysql://localhost:3306/bdpolyservice?useSSL=false&serverTimezone=UTC";
    private final String USUARIO = "root";
    private final String CONTRASENA = "";

    private ConexionBD() {
        conectar(); // siempre intenta conectar en el constructor
    }

    public static ConexionBD getInstancia() {
        if (instancia == null) {
            synchronized (ConexionBD.class) {
                if (instancia == null) {
                    instancia = new ConexionBD();
                }
            }
        }
        return instancia;
    }

    // Método público para reconectar manualmente si quieres
    public boolean conectar() {
        try {
            if (conexion != null && !conexion.isClosed()) {
                return true;
            }
            conexion = DriverManager.getConnection(URL, USUARIO, CONTRASENA);
            return true;
        } catch (SQLException e) {
            conexion = null;
            mostrarError(e);
            return false;
        }
    }

    public Connection getConexion() {
        try {
            if (conexion == null || conexion.isClosed()) {
                conectar(); // ← ESTO ES LO QUE TE FALTABA
            }
        } catch (SQLException e) {
            mostrarError(e);
        }
        return conexion; // puede ser null, pero ya no explota
    }

    private void mostrarError(SQLException e) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error de Base de Datos");
            alert.setHeaderText("No se pudo conectar a MySQL");
            alert.setContentText("""
                Verifica que:
                • MySQL esté encendido
                • La base de datos 'bdpolyservice' exista
                • El usuario root no tenga contraseña o cámbiala aquí en el código
                                
                Error técnico: %s""".formatted(e.getMessage()));
            alert.showAndWait();
        });
    }

    public void cerrarConexion() {
        try {
            if (conexion != null && !conexion.isClosed()) {
                conexion.close();
                conexion = null;
            }
        } catch (SQLException e) {
            System.err.println("Error al cerrar conexión: " + e.getMessage());
        }
    }
}