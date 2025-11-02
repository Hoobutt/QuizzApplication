module com.example.quizzapplication {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;
    requires java.mail;


    opens com.example.quizzapplication to javafx.fxml;
    exports com.example.quizzapplication;
    exports com.example.quizzapplication.miscControllers;
    opens com.example.quizzapplication.miscControllers to javafx.fxml;
}