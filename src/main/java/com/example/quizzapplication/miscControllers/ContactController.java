package com.example.quizzapplication.miscControllers;

import com.example.quizzapplication.Application;
import com.example.quizzapplication.EmailService;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
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
    private Label statusLabel;

    @FXML
    protected void onBackButtonClick() {
        Application.changeScene("home-view.fxml", backButton.getScene());
    }

    @FXML
    protected void onSubmitButtonClick() {

        EmailService emailService = new EmailService();
        int errorStatus = emailService.sendEmail(messageField.getText(), numberField.getText(), emailField.getText(), nameField.getText());

        if (errorStatus == 0) {
            nameField.clear();
            emailField.clear();
            numberField.clear();
            messageField.clear();

            statusLabel.setText("Message sent successfully!");
            statusLabel.setDisable(false);
        } else {
            statusLabel.setText("Failed to send message. Please try again.");
            statusLabel.setDisable(false);
        }

    }
}
