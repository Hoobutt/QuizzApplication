package com.example.quizzapplication.miscControllers;

import com.example.quizzapplication.Application;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

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
    private TextField searchField;

    private String[] availableQuizzes = { //Sample Quizzes
            "Math - Algebra Basics",
            "History - World War II",
            "Basketball - Championships Starting from 2000's",
            "NFL Football - Championships Starting from 2000's",

    };

    @FXML
    public void initialize() {
        populateQuizList();
        quizListView.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> {
                    enterQuizButton.setDisable(newValue == null);
                }
        );
        enterQuizButton.setDisable(true); //Disables the enter quiz button until a quiz is selected
    }

    private void populateQuizList() {
        quizListView.getItems().clear();
        quizListView.getItems().addAll(availableQuizzes);
    }

    @FXML
    protected void onBackButtonClick() {
        Application.changeScene("home-view.fxml", backButton.getScene());
    }

    @FXML
    protected void onEnterQuizButtonClick() {
        String selectedQuiz = quizListView.getSelectionModel().getSelectedItem();
        if (selectedQuiz != null && !selectedQuiz.isEmpty()) {
            Alert alert = new Alert(AlertType.CONFIRMATION); //Alert before quiz is entered.
            alert.setTitle("Start Quiz");
            alert.setHeaderText("Ready?");
            alert.setContentText("You are about to start: " + selectedQuiz + "\n\nYou have 9000 hours to complete this quiz."); //Change time depending on quiz when you create teh quiz function.

            //Goes to quiz taking scene quiz-taking-view.fxml after pressing enterQuizButton.

            Alert successAlert = new Alert(AlertType.INFORMATION); //Testing Quiz Alert
            successAlert.setTitle("Quiz Started");
            successAlert.setHeaderText("Quiz Started Successfully");
            successAlert.showAndWait();
        } else {
            Alert alert = new Alert(AlertType.WARNING);
            alert.setTitle("No Quiz Selected");
            alert.setHeaderText("Please select a quiz");
            alert.showAndWait();
        }
    }

    @FXML
    protected void onSearchButtonClick() {
        String searchText = searchField.getText().toLowerCase().trim();
        if (searchText.isEmpty()) {
            populateQuizList(); //If search is empty, it will show all the quizzes
        } else { //Quiz filter on search
            quizListView.getItems().clear();
            for (String quiz : availableQuizzes) {
                if (quiz.toLowerCase().contains(searchText)) {
                    quizListView.getItems().add(quiz);
                }
            }
            if (quizListView.getItems().isEmpty()) {
                Alert alert = new Alert(AlertType.INFORMATION);
                alert.setTitle("No Results");
                alert.setHeaderText("No quizzes found");
                alert.showAndWait();
            }
        }
    }
}