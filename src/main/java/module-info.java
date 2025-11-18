module com.example.quizzapplication {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;
    requires java.sql;


    opens com.example.quizzapplication to javafx.fxml;
    exports com.example.quizzapplication;
    exports com.example.quizzapplication.miscControllers;
    opens com.example.quizzapplication.miscControllers to javafx.fxml;
    exports com.example.quizzapplication.manager;
    opens com.example.quizzapplication.manager to javafx.fxml;
}