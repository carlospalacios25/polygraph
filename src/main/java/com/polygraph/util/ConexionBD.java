package com.polygraph.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import javafx.scene.control.Alert;
import javafx.stage.Stage;

public class ConexionBD {
    private static ConexionBD instancia;
    private Connection conexion;
    
    private final String URL = "jdbc:mysql://localhost:3306/bdpolyservice";
    private final String USUARIO = "root";
    private final String CONTRASENA = "";
    
    private ConexionBD() {
        try {
            conexion = DriverManager.getConnection(URL, USUARIO, CONTRASENA);
            System.out.println("Conexion Correcta");
        } catch (SQLException e) {
            mostrarError(e);
        }
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
    
    public Connection getConexion() {
        try {
            if (conexion == null || conexion.isClosed()) {
                // Reconectar si está cerrada
                conexion = DriverManager.getConnection(URL, USUARIO, CONTRASENA);
            }
        } catch (SQLException e) {
            mostrarError(e);
        }
        return conexion;
    }
    
    private void mostrarError(SQLException e) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error de Base de Datos");
        alert.setHeaderText("No se pudo conectar a la base de datos");
        alert.setContentText(e.getMessage());
        alert.showAndWait();
    }
    
    // ¡NO USES cerrarConexion() EN LOS DAOs!
    public void cerrarConexion() {
        // Solo para cerrar al final de la app
        try {
            if (conexion != null && !conexion.isClosed()) {
                conexion.close();
            }
        } catch (SQLException e) {
            System.err.println("Error al cerrar: " + e.getMessage());
        }
    }
}