package com.example.quizzapplication.miscControllers;

import com.example.quizzapplication.Application;
import com.example.quizzapplication.EmailHelper;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

public class ContactController {
    @FXML
    private Button backButton;

    @FXML
    private Button submitButton;

    @FXML
    private TextField nameField;

    @FXML
    private TextField emailField;

    @FXML
    private TextField numberField;

    @FXML
    private TextArea messageField;

    @FXML
    protected void onBackButtonClick() {
        Application.changeScene("home-view.fxml", backButton.getScene());
    }

    @FXML
    protected void onSubmitButtonClick() {

        if (EmailHelper.sendEmail(messageField.getText(), numberField.getText(), emailField.getText(), nameField.getText()) == 0) {
            nameField.clear();
            emailField.clear();
            numberField.clear();
            messageField.clear();
        }

    }
}
