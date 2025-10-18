package com.polygraph.controlador;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import java.io.IOException;

public class MainController {

    @FXML private ImageView profileImage;
    @FXML private AnchorPane contentArea;

    @FXML
    public void initialize() {
        try {
            profileImage.setImage(new Image("/com/polygraph/imgs/profile-placeholder.png"));
        } catch (IllegalArgumentException e) {
            System.err.println("No se pudo cargar la imagen de perfil: " + e.getMessage());
           // profileImage.setImage(new Image("/com/polygraph/imgs/default-profile.png"));
        }
    }
    
    /*    @FXML
    private void cargarPerfil(ActionEvent event) {
        try {
            loadView("/com/polygraph/vista/PerfilForm.fxml", (Node) event.getSource());
        } catch (Exception e) {
            showAlert("Error", "" + e.getMessage());
        }
    }*/
    public void loadOverview(ActionEvent event) {
        loadView("/com/polygraph/vista/UsuarioForm.fxml", (Node) event.getSource());
    }

    public void loadOrders(ActionEvent event) {
        loadView("/com/polygraph/vista/VistaServicios.fxml", (Node) event.getSource());
    }

    public void loadCustomers(ActionEvent event) {
        loadView("/com/polygraph/vista/Customers.fxml", (Node) event.getSource());
    }

    public void loadMenus(ActionEvent event) {
        loadView("/com/polygraph/vista/Menus.fxml", (Node) event.getSource());
    }

    public void loadPackages(ActionEvent event) {
        loadView("/com/polygraph/vista/Packages.fxml", (Node) event.getSource());
    }

    public void loadSettings(ActionEvent event) {
        loadView("/com/polygraph/vista/Settings.fxml", (Node) event.getSource());
    }

    public void loadDiscovery(ActionEvent event) {
        loadView("/com/polygraph/vista/Discovery.fxml", (Node) event.getSource());
    }

    public void loadReports(ActionEvent event) {
        loadView("/com/polygraph/vista/Reports.fxml", (Node) event.getSource());
    }

    public void loadAnalytics(ActionEvent event) {
        loadView("/com/polygraph/vista/Analytics.fxml", (Node) event.getSource());
    }

    public void loadNotifications(ActionEvent event) {
        loadView("/com/polygraph/vista/Notifications.fxml", (Node) event.getSource());
    }

    public void signOut(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/polygraph/vista/LoginView.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root, 400, 400));
            stage.setTitle("Aplicación Polygraph - Login");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadView(String fxmlPath, Node source) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();
            contentArea.getChildren().clear();
            contentArea.getChildren().add(root);
            AnchorPane.setTopAnchor(root, 0.0);
            AnchorPane.setBottomAnchor(root, 0.0);
            AnchorPane.setLeftAnchor(root, 0.0);
            AnchorPane.setRightAnchor(root, 0.0);

            if (source instanceof Button) {
                Button button = (Button) source;
                button.getStyleClass().add("active");
                for (Node node : ((VBox) button.getParent()).getChildren()) {
                    if (node instanceof Button && node != button) {
                        node.getStyleClass().remove("active");
                    }
                }
            }

            // Cargar el CSS solo si la escena existe
            if (contentArea.getScene() != null) {
                contentArea.getScene().getStylesheets().add(getClass().getResource("/com/polygraph/styles.css").toExternalForm());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}