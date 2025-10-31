package com.polygraph.controlador;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import java.io.IOException;

public class MainController {

    @FXML private ImageView profileImage;
    @FXML private AnchorPane contentArea;
    @FXML private Label homeCrumb;
    @FXML private Label currentCrumb;

    @FXML
    public void initialize() {
        try {
            profileImage.setImage(new Image("/com/polygraph/imgs/profile-placeholder.png"));
            // Set default breadcrumb state
            homeCrumb.setText("Home");
            currentCrumb.setText("Dashboard");
        } catch (IllegalArgumentException e) {
            //System.err.println("No se pudo cargar la imagen de perfil: " + e.getMessage());
        }
    }

    @FXML
    public void navigateToHome() {
        loadView("/com/polygraph/vista/Dashboard.fxml", null);
        currentCrumb.setText("Dashboard");
    }

    @FXML
    public void cargarUsuario(ActionEvent event) {
        loadView("/com/polygraph/vista/UsuarioForm.fxml", (Node) event.getSource());
        currentCrumb.setText("Usuarios");
    }

    @FXML
    public void cargarCliente(ActionEvent event) {
        loadView("/com/polygraph/vista/ClienteForm.fxml", (Node) event.getSource());
        currentCrumb.setText("Cliente");
    }

    @FXML
    public void cargarCandidato(ActionEvent event) {
        loadView("/com/polygraph/vista/CandidatoForm.fxml", (Node) event.getSource());
        currentCrumb.setText("Candidato");
    }

    @FXML
    public void loadMenus(ActionEvent event) {
        loadView("/com/polygraph/vista/Menus.fxml", (Node) event.getSource());
        currentCrumb.setText("Menus");
    }

    @FXML
    public void loadPackages(ActionEvent event) {
        loadView("/com/polygraph/vista/Packages.fxml", (Node) event.getSource());
        currentCrumb.setText("Packages");
    }

    @FXML
    public void loadSettings(ActionEvent event) {
        loadView("/com/polygraph/vista/Settings.fxml", (Node) event.getSource());
        currentCrumb.setText("Settings");
    }

    @FXML
    public void loadDiscovery(ActionEvent event) {
        loadView("/com/polygraph/vista/VistaServicio.fxml", (Node) event.getSource());
        currentCrumb.setText("Discovery");
    }

    @FXML
    public void loadReports(ActionEvent event) {
        loadView("/com/polygraph/vista/Reports.fxml", (Node) event.getSource());
        currentCrumb.setText("Reports");
    }

    @FXML
    public void cargarServicio(ActionEvent event) {
        loadView("/com/polygraph/vista/ServicioForm.fxml", (Node) event.getSource());
        currentCrumb.setText("Servicio");
    }

    @FXML
    public void loadNotifications(ActionEvent event) {
        loadView("/com/polygraph/vista/Notifications.fxml", (Node) event.getSource());
        currentCrumb.setText("Notifications");
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

            if (contentArea.getScene() != null) {
                contentArea.getScene().getStylesheets().add(getClass().getResource("/com/polygraph/styles.css").toExternalForm());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}