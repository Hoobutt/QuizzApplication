package com.example.quizzapplication.miscControllers;

import com.example.quizzapplication.Application;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.text.Text;
import javafx.util.Duration;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class QuizSessionController {
    @FXML private Label quizTitleLabel;
    @FXML private Label questionNumberLabel;
    @FXML private Text questionText;
    @FXML private RadioButton answer1;
    @FXML private RadioButton answer2;
    @FXML private RadioButton answer3;
    @FXML private RadioButton answer4;
    @FXML private Label timerLabel;
    @FXML private ProgressBar progressBar;
    @FXML private Button nextButton;
    @FXML private Button previousButton;
    @FXML private Button backButton;

    private Timeline timerTimeline;
    private List<Question> questions;
    private List<String> userAnswers;
    private int currentQuestionIndex = 0;
    private int quizId;
    private int userId;
    private int timeRemaining;
    private static final int QUIZ_DURATION = 600;

    private static final String DB_URL = "jdbc:mysql://localhost:3306/users";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "2iuks3dy";

    public static class Question {
        private final int id;
        private final int quizId;
        private final String questionText;
        private final String optionA;
        private final String optionB;
        private final String optionC;
        private final String optionD;
        private final String correctAnswer;

        public Question(int id, int quizId, String questionText, String optionA, String optionB,
                        String optionC, String optionD, String correctAnswer) {
            this.id = id;
            this.quizId = quizId;
            this.questionText = questionText;
            this.optionA = optionA;
            this.optionB = optionB;
            this.optionC = optionC;
            this.optionD = optionD;
            this.correctAnswer = correctAnswer;
        }

        public int getId() { return id; }
        public int getQuizId() { return quizId; }
        public String getQuestionText() { return questionText; }
        public String getOptionA() { return optionA; }
        public String getOptionB() { return optionB; }
        public String getOptionC() { return optionC; }
        public String getOptionD() { return optionD; }
        public String getCorrectAnswer() { return correctAnswer; }
    }

    @FXML
    public void initialize() {
        System.out.println("QuizSessionController initialized");
        try {
            setupAnswerToggleGroup();
            initializeWithQuizData();
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Initialization Error", "Failed to initialize quiz session: " + e.getMessage());
            Application.changeScene("quizHomePage-view.fxml", backButton.getScene());
        }
    }

    private void initializeWithQuizData() {
        try {
            Map<String, Object> quizData = (Map<String, Object>) Application.getSessionData("quizData");
            if (quizData != null) {
                int quizId = (int) quizData.get("quizId");
                String quizTitle = (String) quizData.get("quizTitle");
                int userId = (int) quizData.get("userId");

                setQuizData(quizId, userId, quizTitle);
                Application.clearSessionData("quizData");
            } else {
                throw new Exception("No quiz data found in session");
            }
        } catch (Exception e) {
            showAlert("Error", "Failed to load quiz data: " + e.getMessage());
            Application.changeScene("quizHomePage-view.fxml", backButton.getScene());
        }
    }

    public void setQuizData(int quizId, int userId, String quizTitle) {
        this.quizId = quizId;
        this.userId = userId;
        quizTitleLabel.setText(quizTitle);
        loadQuestions();
    }

    private void setupAnswerToggleGroup() {
        ToggleGroup answerGroup = new ToggleGroup();
        answer1.setToggleGroup(answerGroup);
        answer2.setToggleGroup(answerGroup);
        answer3.setToggleGroup(answerGroup);
        answer4.setToggleGroup(answerGroup);
    }

    private void loadQuestions() {
        try {
            questions = new ArrayList<>();
            userAnswers = new ArrayList<>();

            Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            PreparedStatement stmt = conn.prepareStatement("SELECT * FROM questions WHERE quiz_id = ? ORDER BY id");
            stmt.setInt(1, quizId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                questions.add(new Question(
                        rs.getInt("id"),
                        rs.getInt("quiz_id"),
                        rs.getString("question_text"),
                        rs.getString("option_a"),
                        rs.getString("option_b"),
                        rs.getString("option_c"),
                        rs.getString("option_d"),
                        rs.getString("correct_answer")
                ));
                userAnswers.add(null);
            }

            rs.close();
            stmt.close();
            conn.close();

            if (questions.isEmpty()) {
                throw new Exception("No questions found for this quiz");
            }

            initializeQuizSession();
        } catch (Exception e) {
            showAlert("Error", "Failed to load questions: " + e.getMessage());
            Application.changeScene("quizHomePage-view.fxml", backButton.getScene());
        }
    }

    private void initializeQuizSession() {
        startTimer();
        displayQuestion(0);
        updateNavigationButtons();
    }

    private void startTimer() {
        timeRemaining = QUIZ_DURATION;
        updateTimerDisplay();

        timerTimeline = new Timeline(new KeyFrame(Duration.seconds(1), e -> {
            timeRemaining--;
            updateTimerDisplay();
            if (timeRemaining <= 0) {
                timeUp();
            }
        }));
        timerTimeline.setCycleCount(Timeline.INDEFINITE);
        timerTimeline.play();
    }

    private void updateTimerDisplay() {
        int minutes = timeRemaining / 60;
        int seconds = timeRemaining % 60;
        timerLabel.setText(String.format("%02d:%02d", minutes, seconds));

        timerLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");

        if (timeRemaining <= 600) { // 10 Minutes
            timerLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: red;");
        } else {
            timerLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: white;");
        }
    }

    private void displayQuestion(int index) {
        if (index < 0 || index >= questions.size()) return;

        currentQuestionIndex = index;
        Question question = questions.get(currentQuestionIndex);

        questionNumberLabel.setText("Question " + (index + 1) + " of " + questions.size());
        questionText.setText(question.getQuestionText());

        answer1.setText("A: " + question.getOptionA());
        answer2.setText("B: " + question.getOptionB());
        answer3.setText("C: " + question.getOptionC());
        answer4.setText("D: " + question.getOptionD());

        answer1.setSelected(false);
        answer2.setSelected(false);
        answer3.setSelected(false);
        answer4.setSelected(false);

        String previousAnswer = userAnswers.get(index);
        if (previousAnswer != null) {
            switch (previousAnswer) {
                case "A": answer1.setSelected(true); break;
                case "B": answer2.setSelected(true); break;
                case "C": answer3.setSelected(true); break;
                case "D": answer4.setSelected(true); break;
            }
        }

        progressBar.setProgress((double) (index + 1) / questions.size());
        nextButton.setText(index == questions.size() - 1 ? "Finish Quiz" : "Next Question");
        updateNavigationButtons();
    }

    private void updateNavigationButtons() {
        previousButton.setDisable(currentQuestionIndex == 0);
    }

    private void saveCurrentAnswer() {
        String selectedAnswer = null;
        if (answer1.isSelected()) selectedAnswer = "A";
        else if (answer2.isSelected()) selectedAnswer = "B";
        else if (answer3.isSelected()) selectedAnswer = "C";
        else if (answer4.isSelected()) selectedAnswer = "D";

        userAnswers.set(currentQuestionIndex, selectedAnswer);
    }

    @FXML
    private void onNextButtonClick() {
        saveCurrentAnswer();
        if (currentQuestionIndex < questions.size() - 1) {
            displayQuestion(currentQuestionIndex + 1);
        } else {
            finishQuiz();
        }
    }

    @FXML
    private void onPreviousButtonClick() {
        saveCurrentAnswer();
        if (currentQuestionIndex > 0) {
            displayQuestion(currentQuestionIndex - 1);
        }
    }

    @FXML
    private void onBackButtonClick() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Leave Quiz");
        alert.setHeaderText("Are you sure you want to leave the quiz?");
        alert.setContentText("Your progress will be lost if you leave now.");

        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                stopTimer();
                Application.changeScene("quizHomePage-view.fxml", backButton.getScene());
            }
        });
    }

    private void timeUp() {
        stopTimer();
        saveCurrentAnswer();
        showAlert("Time's Up!", "Your time has expired. The quiz will be submitted automatically.");
        finishQuiz();
    }

    private void stopTimer() {
        if (timerTimeline != null) {
            timerTimeline.stop();
        }
    }

    private void finishQuiz() {
        stopTimer();
        int timeUsed = QUIZ_DURATION - timeRemaining;
        saveQuizResults(timeUsed);
        showResults();
    }

    private void saveQuizResults(int timeUsed) {
        try {
            int score = calculateScore();
            int totalQuestions = questions.size();
            double percentage = totalQuestions > 0 ? (double) score / totalQuestions * 100 : 0;

            Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            PreparedStatement stmt = conn.prepareStatement(
                    "INSERT INTO results (user_id, quiz_id, score, total_questions, percentage, time_used) VALUES (?, ?, ?, ?, ?, ?)");

            stmt.setInt(1, userId);
            stmt.setInt(2, quizId);
            stmt.setInt(3, score);
            stmt.setInt(4, totalQuestions);
            stmt.setDouble(5, percentage);
            stmt.setInt(6, timeUsed);
            stmt.executeUpdate();

            stmt.close();
            conn.close();
        } catch (SQLException e) {
            System.err.println("Failed to save results: " + e.getMessage());
        }
    }

    private int calculateScore() {
        int score = 0;
        for (int i = 0; i < questions.size(); i++) {
            String userAnswer = userAnswers.get(i);
            String correctAnswer = questions.get(i).getCorrectAnswer();
            if (userAnswer != null && userAnswer.equals(correctAnswer)) {
                score++;
            }
        }
        return score;
    }

    private void showResults() {
        int score = calculateScore();
        int totalQuestions = questions.size();
        double percentage = totalQuestions > 0 ? (double) score / totalQuestions * 100 : 0;
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Quiz Completed");
        alert.setHeaderText("Your Quiz Results");
        alert.setContentText(String.format("Score: %d/%d (%.1f%%)", score, totalQuestions, percentage));
        alert.showAndWait().ifPresent(response -> {
            Application.largeScene("quizHomePage-view.fxml", backButton.getScene());
        });
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}