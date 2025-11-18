package com.example.quizzapplication.miscControllers;

import com.example.quizzapplication.Application;
import javafx.fxml.FXML;
import javafx.scene.control.Button;

public class AdminController {
    @FXML
    private Button backButton;

    // Database connection details
    private static final String DB_URL = "jdbc:mysql://localhost:3306/users";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "2iuks3dy";

    @FXML
    protected void onBackButtonClick() {
        Application.changeScene("home-view.fxml", backButton.getScene());
    }
}
