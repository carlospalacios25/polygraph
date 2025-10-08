module com.polygraph.polygraph {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;

    opens com.polygraph to javafx.fxml;          // Para AplicacionPrincipal
    opens com.polygraph.controlador to javafx.fxml; // Para ControladorServicios
    opens com.polygraph.modelo to javafx.base;   // Para permitir acceso a Servicio por TableView
    exports com.polygraph;                       // Para hacer visible AplicacionPrincipal
    exports com.polygraph.controlador;           // Para hacer visible el controlador
}