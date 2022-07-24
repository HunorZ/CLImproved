module com.climproved {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;
    requires org.json;


    opens com.climproved to javafx.fxml;
    exports com.climproved;
}