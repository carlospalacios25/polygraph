module com.polygraph {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;   // ← ESTA ES LA QUE FALTABA
    requires java.sql;
    requires jakarta.mail;
    requires jakarta.activation;
   
    exports com.polygraph;
    opens com.polygraph to javafx.fxml;
    opens com.polygraph.controlador to javafx.fxml;
    opens com.polygraph.modelo to javafx.base;
    exports com.polygraph.controlador;
}