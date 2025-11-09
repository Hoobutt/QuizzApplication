package com.example.quizzapplication;

import javafx.fxml.FXML;
import javafx.scene.control.Button;

public class HomeController {
    @FXML
    private Button socialButton;
    @FXML
    private Button aboutButton;
    @FXML
    private Button contactButton;
    @FXML
    private Button creditsButton;
    @FXML
    private Button loginButton;
    @FXML
    private Button registerButton;

    // Event Handlers for each button
    @FXML
    protected void onSocialButtonClick() {
        Application.changeScene("socials-view.fxml", socialButton.getScene());
    }
    @FXML
    protected void onAboutButtonClick() {
        Application.largeScene("about-view.fxml", aboutButton.getScene());
    }
    @FXML
    protected void onContactButtonClick() {
        Application.changeScene("contact-view.fxml", contactButton.getScene());
    }
    @FXML
    protected void onCreditsButtonClick() {
        Application.changeScene("credits-view.fxml", creditsButton.getScene());
    }
    @FXML
    protected void onLoginButtonClick(){
        Application.largeScene("quizPage-view.fxml", loginButton.getScene()); //Goes to quizhomeview
    }
    @FXML
    protected void onRegisterButtonClick(){
        Application.changeScene("register-view.fxml", registerButton.getScene());
    }
}
