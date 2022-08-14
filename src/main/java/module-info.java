module com.climproved {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;
    requires org.json;


    opens com.climproved to javafx.fxml;
    exports com.climproved;
    exports com.climproved.Notifications;
    opens com.climproved.Notifications to javafx.fxml;
}