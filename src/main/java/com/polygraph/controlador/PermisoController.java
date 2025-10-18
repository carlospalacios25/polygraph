package com.polygraph.controlador;

import com.polygraph.dao.PermisosDAO;
import com.polygraph.modelo.Permisos;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;

public class PermisoController {

    @FXML private TextField permisoField;
    @FXML private TextField descripcionPermiso;


    public void insertarPermiso(ActionEvent event) {
        try {
            Permisos perfiles = new Permisos(
                permisoField.getText(),
                descripcionPermiso.getText()
            );

            PermisosDAO dao = new PermisosDAO();
            dao.insertarPerfile(perfiles);

            showAlert("Éxito", "Permiso Agregado Correctamente.");
            clearFields();
        } catch (SQLException | NoSuchAlgorithmException e) {
            showAlert("Error", "Error al insertar: " + e.getMessage());
        }
    }
    
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void clearFields() {
        permisoField.clear();
        descripcionPermiso.clear();
    }

}