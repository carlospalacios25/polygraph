package com.polygraph;

import com.polygraph.util.ConexionBD;
import com.polygraph.controlador.MainController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.ServerSocket;

public class AplicacionPrincipal extends Application {

    private static ServerSocket bloqueo;   // 🔒 Para permitir solo una instancia

    @Override
    public void start(Stage primaryStage) {
        try {
            FXMLLoader loader =
                    new FXMLLoader(getClass().getResource("/com/polygraph/vista/LoginView.fxml"));
            Parent root = loader.load();

            Scene scene = new Scene(root);
            primaryStage.setScene(scene);
            primaryStage.setTitle("Polygraph - Login");

            primaryStage.setResizable(true);
            primaryStage.centerOnScreen();
            primaryStage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // ==============================
    // ABRIR MAIN DESDE LOGIN
    // ==============================
    public static void abrirMain(Stage stage) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    AplicacionPrincipal.class.getResource("/com/polygraph/vista/Main.fxml")
            );
            Parent root = loader.load();

            MainController mainController = loader.getController();
            root.setUserData(mainController);

            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Polygraph - Sistema Principal");

            stage.setMaximized(true);
            stage.setIconified(false);
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void stop() {
        ConexionBD.getInstancia().cerrarConexion();
    }

    // ==============================
    //   MAIN → BLOQUEO UNA INSTANCIA
    // ==============================
    public static void main(String[] args) {

        if (!bloquearInstancia()) {
            System.out.println("⚠ La aplicación ya está abierta.");
            return;   // ⛔ NO abrimos otra copia
        }

        launch(args);
    }

    private static boolean bloquearInstancia() {
        try {
            bloqueo = new ServerSocket(45999);  // Puerto libre cualquiera
            return true;  // Primera instancia
        } catch (IOException e) {
            return false;  // Ya hay otra instancia corriendo
        }
    }
}
