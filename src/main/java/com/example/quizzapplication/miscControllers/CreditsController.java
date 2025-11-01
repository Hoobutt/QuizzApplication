package com.example.quizzapplication.miscControllers;

import com.example.quizzapplication.Application;
import javafx.fxml.FXML;
import javafx.scene.control.Button;

public class CreditsController {
    @FXML
    private Button backButton;

    @FXML
    protected void onBackButtonClick() {
        Application.changeScene("home-view.fxml", backButton.getScene());
    }
}
