package com.polygraph.controlador;

import com.polygraph.dao.ServicioDAO;
import com.polygraph.modelo.Servicio;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.net.URL;
import java.util.ResourceBundle;

public class ControladorServicios implements Initializable {

    @FXML
    private TableView<Servicio> tablaServicios;
    
    @FXML
    private TableColumn<Servicio, Integer> colId;
    @FXML
    private TableColumn<Servicio, String> colFechaSolicitud;
    @FXML
    private TableColumn<Servicio, String> colHoraSolicitud;
    @FXML
    private TableColumn<Servicio, Long> colNitCliente;
    @FXML
    private TableColumn<Servicio, Long> colCedulaCandidato;
    @FXML
    private TableColumn<Servicio, Integer> colIdProceso;
    @FXML
    private TableColumn<Servicio, String> colEstado;
    @FXML
    private TableColumn<Servicio, String> colResultado;
    
    private final ObservableList<Servicio> listaServicios = FXCollections.observableArrayList();
    private final ServicioDAO servicioDAO = new ServicioDAO();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        configurarTabla();
        cargarDatos();
    }
    
    private void configurarTabla() {
        colId.setCellValueFactory(new PropertyValueFactory<>("idServicio"));
        colFechaSolicitud.setCellValueFactory(new PropertyValueFactory<>("fechaSolicitud"));
        colHoraSolicitud.setCellValueFactory(new PropertyValueFactory<>("horaSolicitud"));
        colNitCliente.setCellValueFactory(new PropertyValueFactory<>("nitCliente"));
        colCedulaCandidato.setCellValueFactory(new PropertyValueFactory<>("cedulaCandidato"));
        colIdProceso.setCellValueFactory(new PropertyValueFactory<>("idProceso"));
        colEstado.setCellValueFactory(new PropertyValueFactory<>("estado"));
        colResultado.setCellValueFactory(new PropertyValueFactory<>("resultado"));
        
        tablaServicios.setItems(listaServicios);
    }
    
    private void cargarDatos() {
        listaServicios.addAll(servicioDAO.obtenerTodosServicios());
        System.out.println("Número de servicios cargados en la TableView: " + listaServicios.size());
        for (Servicio s : listaServicios) {
            System.out.println("Servicio: " + s.getIdServicio() + ", " + s.getFechaSolicitud() + ", " + s.getEstado());
        }
    }
}
