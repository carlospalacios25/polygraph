package com.polygraph;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class AplicacionPrincipal extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        // Carga el FXML principal
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/polygraph/vista/VistaServicios.fxml"));
        Parent root = loader.load();
        
        // Aplica el archivo CSS
        Scene scene = new Scene(root, 800, 600);
        scene.getStylesheets().add(getClass().getResource("/com/polygraph/styles.css").toExternalForm());
        
        primaryStage.setTitle("Aplicación Polygraph - Lista de Servicios");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}