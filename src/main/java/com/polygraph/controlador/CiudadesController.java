package com.polygraph.controlador;

import com.polygraph.dao.CiudadesDAO;
import com.polygraph.modelo.Ciudades;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;

public class CiudadesController {

    @FXML private TextField nomCiudadField;

    public void insertarCiudad(ActionEvent event) {
        try {
            Ciudades ciudades = new Ciudades(
                nomCiudadField.getText()
            );

            CiudadesDAO dao = new CiudadesDAO();
            dao.insertarPerfile(ciudades);

            showAlert("Éxito", "Ciudad Agregada Correctamente.");
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
        nomCiudadField.clear();
    }

}