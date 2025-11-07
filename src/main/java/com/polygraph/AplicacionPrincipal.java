package com.polygraph;

import com.polygraph.util.ConexionBD;
import com.polygraph.controlador.MainController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;

public class AplicacionPrincipal extends Application {

    @Override
    public void start(Stage primaryStage) {
        try {
            // CARGAR LOGIN
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/polygraph/vista/LoginView.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root, 400, 400);
            primaryStage.setTitle("Polygraph - Login");
            primaryStage.setScene(scene);
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

            // GUARDAR EN USERDATA (¡ESTO ES TODO!)
            root.setUserData(mainController);

            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Polygraph - Sistema Principal");
            stage.setMaximized(true);
            stage.show();

            System.out.println("MainController guardado correctamente.");

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