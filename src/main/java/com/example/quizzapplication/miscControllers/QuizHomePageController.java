package com.example.quizzapplication.miscControllers; //Quiz Home Page

import com.example.quizzapplication.Application;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;

public class QuizHomePageController {
    @FXML
    private Button backButton;
    @FXML
    private Button enterQuizButton;
    @FXML
    private ListView<String> quizListView;
    @FXML
    private Button searchButton;
    @FXML
    private ScrollPane scrollPane;



    @FXML
    protected void onBackButtonClick() {
        Application.changeScene("home-view.fxml", backButton.getScene());
    }
    @FXML
    protected void onEnterQuizButtonClick() {
        Application.changeScene("home-view.fxml", enterQuizButton.getScene());
    }
    @FXML
    protected void onSearchButtonClick() {

    }

}
