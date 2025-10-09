package com.polygraph.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import javafx.scene.control.Alert;
import javafx.stage.Stage;

public class ConexionBD {
    private static ConexionBD instancia;
    private Connection conexion;
    
    private final String URL = "jdbc:mysql://localhost:3306/bdpolyservice"; // Cambia si tu puerto/host es diferente
    private final String USUARIO = "root"; // Tu usuario de MySQL
    private final String CONTRASENA = ""; // Cambia por tu contraseña real
    
    private ConexionBD() {
        try {
            conexion = DriverManager.getConnection(URL, USUARIO, CONTRASENA);
        } catch (SQLException e) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Error de conexion");
            alert.setHeaderText(null); // Opcional: texto de encabezado
            alert.setContentText("No se logra establecer la conexion a la Base de datos");
            alert.showAndWait();
        }
    }
    
    public static ConexionBD getInstancia() {
        if (instancia == null) {
            instancia = new ConexionBD();
        }
        return instancia;
    }
    
    public Connection getConexion() {
        return conexion;
    }
    
    public void cerrarConexion() {
        try {
            if (conexion != null && !conexion.isClosed()) {
                conexion.close();
                System.out.println("Conexión cerrada.");
            }
        } catch (SQLException e) {
            System.err.println("Error al cerrar conexión: " + e.getMessage());
        }
    }
}