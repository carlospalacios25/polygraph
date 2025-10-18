package com.polygraph.controlador;

import com.polygraph.dao.PerfilesDAO;
import com.polygraph.modelo.Perfiles;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import javafx.scene.Node;

public class PerfilController {

    @FXML private TextField perfilField;
    @FXML private TextField descripcionPerfil;


    public void insertarPerfil(ActionEvent event) {
        try {
            Perfiles perfiles = new Perfiles(
                perfilField.getText(),
                descripcionPerfil.getText()
            );

            PerfilesDAO dao = new PerfilesDAO();
            dao.insertarPerfile(perfiles);

            showAlert("Éxito", "Perfil Agregado Correctamente.");
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
        perfilField.clear();
        descripcionPerfil.clear();
    }

}