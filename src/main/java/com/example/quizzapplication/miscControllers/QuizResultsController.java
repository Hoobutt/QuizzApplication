package com.example.quizzapplication.miscControllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import com.example.quizzapplication.Application;

import java.util.List;

public class QuizResultsController {
    @FXML private Label scoreLabel;
    @FXML private Label percentageLabel;
    @FXML private ListView<String> resultsListView;
    @FXML private Button homeButton;

    private static List<QuizSessionController.Question> questions;
    private static List<String> userAnswers;
    private static int score;

    @FXML
    public void initialize() {
        if (questions != null && userAnswers != null) {
            displayResults();
        }
    }

    private void displayResults() {
        int totalQuestions = questions.size();
        double percentage = totalQuestions > 0 ? (double) score / totalQuestions * 100 : 0;

        scoreLabel.setText("Score: " + score + "/" + totalQuestions);
        percentageLabel.setText(String.format("Percentage: %.1f%%", percentage));

        // Display each question with result
        for (int i = 0; i < questions.size(); i++) {
            QuizSessionController.Question question = questions.get(i);
            String userAnswer = userAnswers.get(i);
            String correctAnswer = question.getCorrectAnswer();
            boolean isCorrect = userAnswer != null && userAnswer.equals(correctAnswer);

            String resultText = String.format("Q%d: %s\nYour answer: %s | Correct: %s %s",
                    i + 1,
                    question.getQuestionText(),
                    userAnswer != null ? userAnswer : "No answer",
                    correctAnswer,
                    isCorrect ? "✓" : "✗"
            );

            resultsListView.getItems().add(resultText);
        }
    }

    @FXML
    private void onHomeButtonClick() {
        Application.largeScene("quizHomePage-view.fxml", homeButton.getScene());
    }
}