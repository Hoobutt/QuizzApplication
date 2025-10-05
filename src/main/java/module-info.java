module com.example.quizzapplication {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.quizzapplication to javafx.fxml;
    exports com.example.quizzapplication;
}