package com.polygraph;

import com.polygraph.util.ConexionBD;
import java.io.IOException;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class AplicacionPrincipal extends Application {

    @Override
    public void start(Stage primaryStage) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/com/polygraph/vista/LoginView.fxml"));
            Scene scene = new Scene(root, 400, 400); // Tamaño fijo para el login
            primaryStage.setTitle("Aplicación Polygraph - Login");
            primaryStage.setScene(scene);
            primaryStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
    
    @Override
    public void stop() {
        ConexionBD.getInstancia().cerrarConexion();
    }
}