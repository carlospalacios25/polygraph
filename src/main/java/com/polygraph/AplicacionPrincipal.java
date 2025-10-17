package com.polygraph;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class AplicacionPrincipal extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        // Carga el FXML del login
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/polygraph/vista/LoginView.fxml"));
        Parent root = loader.load();
        
        // Aplica el archivo CSS
        Scene scene = new Scene(root, 400, 400); // Ajusté el tamaño para mejor proporción
        scene.getStylesheets().add(getClass().getResource("/com/polygraph/styles.css").toExternalForm());
        
        primaryStage.setTitle("Aplicación Polygraph - Login");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}