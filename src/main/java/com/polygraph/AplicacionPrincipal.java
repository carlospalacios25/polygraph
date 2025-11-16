package com.polygraph;

import com.polygraph.util.ConexionBD;
import com.polygraph.controlador.MainController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;
import javafx.scene.input.KeyCombination;
import javafx.stage.StageStyle;

public class AplicacionPrincipal extends Application {

    @Override
    public void start(Stage primaryStage) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/polygraph/vista/LoginView.fxml"));
            Parent root = loader.load();

            Scene scene = new Scene(root);
            primaryStage.setScene(scene);
            primaryStage.setTitle("Polygraph - Login");

            // QUITA ESTO:
            // primaryStage.initStyle(StageStyle.UNDECORATED);

            primaryStage.setResizable(true); // Permite maximizar
            primaryStage.centerOnScreen();
            primaryStage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
        // MÉTODO PÚBLICO PARA ABRIR MAIN DESDE LOGIN
    public static void abrirMain(Stage stage) {
        try {
            FXMLLoader loader = new FXMLLoader(
                AplicacionPrincipal.class.getResource("/com/polygraph/vista/Main.fxml")
            );
            Parent root = loader.load();
            MainController mainController = loader.getController();

            // GUARDAR EN USERDATA
            root.setUserData(mainController);

            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Polygraph - Sistema Principal");

            // OCUPA TODA LA PANTALLA (CON BARRA DE TÍTULO)
            stage.setMaximized(true);

            // ASEGURAR QUE NO ESTÉ MINIMIZADA
            stage.setIconified(false);

            // MOSTRAR
            stage.show();


        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void stop() {
        ConexionBD.getInstancia().cerrarConexion();
    }

    public static void main(String[] args) {
        launch(args);
    }
}