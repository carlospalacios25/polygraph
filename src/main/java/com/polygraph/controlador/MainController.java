package com.polygraph.controlador;

import com.polygraph.modelo.Servicio;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class MainController {

    @FXML private ImageView profileImage;
    @FXML private AnchorPane contentArea;
    @FXML private HBox breadcrumbBar;
    @FXML private HBox dynamicCrumbs;
    @FXML private Label lblHome; 
    @FXML private Label lblDashboard;
    @FXML private Label lblSeparator1;
    @FXML private Label lblSeparator2;

    // === HISTORIA DE NAVEGACIÓN ===
    private final List<String> breadcrumbHistory = new ArrayList<>();
    private final Map<Integer, String> vistaPorNivel = new HashMap<>();

    @FXML
    public void initialize() {
        try {
            profileImage.setImage(new Image("/com/polygraph/imgs/profile-placeholder.png"));
        } catch (Exception e) {
            //System.err.println("Imagen no encontrada: " + e.getMessage());
        }

        // INICIAR BREADCRUMB
        breadcrumbHistory.clear();
        breadcrumbHistory.add("Home");
        breadcrumbHistory.add("Dashboard");
   //    vistaPorNivel.put(0, "/com/polygraph/vista/Dashboard.fxml");
   //     vistaPorNivel.put(1, "/com/polygraph/vista/Dashboard.fxml");
        actualizarBreadcrumbVisual();
    }

    // === IR A HOME / DASHBOARD ===
    @FXML public void irAHome() { irANivel(0); }
    @FXML public void irADashboard() { irANivel(1); }

    private void irANivel(int nivel) {
        if (nivel >= breadcrumbHistory.size()) return;
        breadcrumbHistory.subList(nivel + 1, breadcrumbHistory.size()).clear();
        vistaPorNivel.keySet().removeIf(k -> k > nivel);
        actualizarBreadcrumbVisual();
        loadView(vistaPorNivel.getOrDefault(nivel, "/com/polygraph/vista/Dashboard.fxml"), null);
    }

    // === NAVEGACIÓN SIDEBAR ===
    private void navegarConCrumb(String fxmlPath, Node source, String nombre) {
        breadcrumbHistory.subList(2, breadcrumbHistory.size()).clear();
        pushBreadcrumb(nombre, fxmlPath);
        loadView(fxmlPath, source);
    }

    @FXML public void cargarUsuario(ActionEvent e) { navegarConCrumb("/com/polygraph/vista/UsuarioForm.fxml", (Node)e.getSource(), "Usuarios"); }
    @FXML public void cargarCliente(ActionEvent e) { navegarConCrumb("/com/polygraph/vista/ClienteForm.fxml", (Node)e.getSource(), "Cliente"); }
    @FXML public void cargarCandidato(ActionEvent e) { navegarConCrumb("/com/polygraph/vista/CandidatoForm.fxml", (Node)e.getSource(), "Candidato"); }
    @FXML public void cargarServicio(ActionEvent e) { navegarConCrumb("/com/polygraph/vista/ServicioForm.fxml", (Node)e.getSource(), "Servicios"); }
    @FXML public void loadMenus(ActionEvent e) { navegarConCrumb("/com/polygraph/vista/Menus.fxml", (Node)e.getSource(), "Menus"); }
    @FXML public void loadPackages(ActionEvent e) { navegarConCrumb("/com/polygraph/vista/Packages.fxml", (Node)e.getSource(), "Packages"); }
    @FXML public void loadSettings(ActionEvent e) { navegarConCrumb("/com/polygraph/vista/Settings.fxml", (Node)e.getSource(), "Settings"); }
    @FXML public void loadDiscovery(ActionEvent e) { navegarConCrumb("/com/polygraph/vista/VistaServicio.fxml", (Node)e.getSource(), "Discovery"); }
    @FXML public void loadReports(ActionEvent e) { navegarConCrumb("/com/polygraph/vista/Reports.fxml", (Node)e.getSource(), "Reports"); }
    @FXML public void loadNotifications(ActionEvent e) { navegarConCrumb("/com/polygraph/vista/Notifications.fxml", (Node)e.getSource(), "Notifications"); }

    @FXML public void navigateToHome() {
        breadcrumbHistory.subList(1, breadcrumbHistory.size()).clear();
        breadcrumbHistory.add("Home");
        vistaPorNivel.put(1, "/com/polygraph/vista/Dashboard.fxml");
        actualizarBreadcrumbVisual();
        loadView("/com/polygraph/vista/Dashboard.fxml", null);
    }

    // === ABRIR MODIFICAR SERVICIO ===
    public void abrirModificarServicio(Servicio servicio) {
        pushBreadcrumb("Servicio #" + servicio.getIdServicio(), "/com/polygraph/vista/ModificarServicio.fxml");
        loadView("/com/polygraph/vista/ModificarServicio.fxml", null, ctrl -> {
            if (ctrl instanceof ModificarServicioControlador modCtrl) {
                modCtrl.setServicio(servicio);
            }
        });
    }

    // === APILAR BREADCRUMB ===
    public void pushBreadcrumb(String nombre, String fxmlPath) {
        breadcrumbHistory.add(nombre);
        vistaPorNivel.put(breadcrumbHistory.size() - 1, fxmlPath);
        actualizarBreadcrumbVisual();
    }

    // === QUITAR ÚLTIMO ===
    public void popBreadcrumb() {
        if (breadcrumbHistory.size() > 2) {
            breadcrumbHistory.remove(breadcrumbHistory.size() - 1);
            vistaPorNivel.remove(breadcrumbHistory.size());
            actualizarBreadcrumbVisual();
        }
    }

    // === ACTUALIZAR VISUAL ===
    private void actualizarBreadcrumbVisual() {
        dynamicCrumbs.getChildren().clear();
        for (int i = 2; i < breadcrumbHistory.size(); i++) {
            Label crumb = new Label(breadcrumbHistory.get(i));
            crumb.getStyleClass().add("breadcrumb-item");
            if (i == breadcrumbHistory.size() - 1) crumb.getStyleClass().add("active");

            final int nivel = i;
            crumb.setOnMouseClicked(e -> irANivel(nivel));
            crumb.setOnMouseEntered(e -> crumb.setStyle("-fx-text-fill: #0d6efd; -fx-font-weight: 600;"));
            crumb.setOnMouseExited(e -> {
                if (!crumb.getStyleClass().contains("active")) {
                    crumb.setStyle("-fx-text-fill: #6c757d;");
                }
            });

            if (i > 2) {
                Label sep = new Label(" > ");
                sep.getStyleClass().add("breadcrumb-separator");
                dynamicCrumbs.getChildren().addAll(sep, crumb);
            } else {
                dynamicCrumbs.getChildren().add(crumb);
            }
        }
    }

    // === LOADVIEW CON Consumer ===
    private <T> void loadView(String fxmlPath, Node source, Consumer<T> postLoad) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();

            contentArea.getChildren().clear();
            contentArea.getChildren().add(root);
            AnchorPane.setTopAnchor(root, 0.0);
            AnchorPane.setBottomAnchor(root, 0.0);
            AnchorPane.setLeftAnchor(root, 0.0);
            AnchorPane.setRightAnchor(root, 0.0);

            if (source instanceof Button btn) {
                btn.getStyleClass().add("active");
                VBox parent = (VBox) btn.getParent();
                if (parent != null) {
                    parent.getChildren().stream()
                        .filter(n -> n instanceof Button && n != btn)
                        .forEach(n -> n.getStyleClass().remove("active"));
                }
            }

            T ctrl = loader.getController();
            if (ctrl instanceof ServiciosControlador sc) sc.setMainController(this);
            if (ctrl instanceof ModificarServicioControlador mc) mc.setMainController(this);

            if (postLoad != null) postLoad.accept(ctrl);

            String css = getClass().getResource("/com/polygraph/styles.css").toExternalForm();
            if (!contentArea.getScene().getStylesheets().contains(css)) {
                contentArea.getScene().getStylesheets().add(css);
            }

        } catch (IOException e) {
            e.printStackTrace();
            showError("Error", "No se pudo cargar: " + fxmlPath);
        }
    }

    private void loadView(String fxmlPath, Node source) {
        loadView(fxmlPath, source, null);
    }

    private void showError(String title, String msg) {
        Alert a = new Alert(Alert.AlertType.ERROR);
        a.setTitle(title);
        a.setContentText(msg);
        a.show();
    }
}