package com.example.quizzapplication.miscControllers;

import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import java.sql.*;
import java.util.Optional;

public class CreateQuestionController {

    // Connects to Database
    private static final String DB_URL = "jdbc:mysql://localhost:3306/users";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "2iuks3dy";

    private int currentQuizId;
    private Runnable refreshCallback;

    // Question Answer Fields
    private Dialog<QuestionData> dialog;
    private TextField questionField;
    private TextField optionAField;
    private TextField optionBField;
    private TextField optionCField;
    private TextField optionDField;
    private ComboBox<String> correctAnswerComboBox;

    public void setQuizId(int quizId) {
        this.currentQuizId = quizId;
    }

    public void setRefreshCallback(Runnable callback) {
        this.refreshCallback = callback;
    }

    public void showAddQuestionDialog() {
        createDialog("Add Question", null);
        Optional<QuestionData> result = dialog.showAndWait();
        result.ifPresent(questionData -> {
            addQuestionToDatabase(
                    currentQuizId,
                    questionData.getQuestionText(),
                    questionData.getOptionA(),
                    questionData.getOptionB(),
                    questionData.getOptionC(),
                    questionData.getOptionD(),
                    questionData.getCorrectAnswer()
            );
            if (refreshCallback != null) {
                refreshCallback.run();
            }
        });
    }

    public void showEditQuestionDialog(AdminController.Question existingQuestion) {
        QuestionData questionData = new QuestionData(
                existingQuestion.getQuestionText(),
                existingQuestion.getOptionA(),
                existingQuestion.getOptionB(),
                existingQuestion.getOptionC(),
                existingQuestion.getOptionD(),
                existingQuestion.getCorrectAnswer()
        );

        createDialog("Edit Question", questionData);
        Optional<QuestionData> result = dialog.showAndWait();
        result.ifPresent(updatedQuestionData -> {
            updateQuestionInDatabase(
                    existingQuestion.getId(),
                    updatedQuestionData.getQuestionText(),
                    updatedQuestionData.getOptionA(),
                    updatedQuestionData.getOptionB(),
                    updatedQuestionData.getOptionC(),
                    updatedQuestionData.getOptionD(),
                    updatedQuestionData.getCorrectAnswer()
            );
            if (refreshCallback != null) {
                refreshCallback.run();
            }
        });
    }

    private void createDialog(String title, QuestionData existingData) {
        dialog = new Dialog<>();
        dialog.setTitle(title);

        ButtonType saveButtonType = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new javafx.geometry.Insets(20, 150, 10, 10));

        questionField = new TextField();
        optionAField = new TextField();
        optionBField = new TextField();
        optionCField = new TextField();
        optionDField = new TextField();
        correctAnswerComboBox = new ComboBox<>();
        correctAnswerComboBox.getItems().addAll("A", "B", "C", "D");

        grid.add(new Label("Question:"), 0, 0);
        grid.add(questionField, 1, 0);
        grid.add(new Label("Option A:"), 0, 1);
        grid.add(optionAField, 1, 1);
        grid.add(new Label("Option B:"), 0, 2);
        grid.add(optionBField, 1, 2);
        grid.add(new Label("Option C:"), 0, 3);
        grid.add(optionCField, 1, 3);
        grid.add(new Label("Option D:"), 0, 4);
        grid.add(optionDField, 1, 4);
        grid.add(new Label("Correct Answer:"), 0, 5);
        grid.add(correctAnswerComboBox, 1, 5);

        // Populate with existing data if editing
        if (existingData != null) {
            questionField.setText(existingData.getQuestionText());
            optionAField.setText(existingData.getOptionA());
            optionBField.setText(existingData.getOptionB());
            optionCField.setText(existingData.getOptionC());
            optionDField.setText(existingData.getOptionD());
            correctAnswerComboBox.setValue(existingData.getCorrectAnswer());
        } else {
            correctAnswerComboBox.setValue("A");
        }

        dialog.getDialogPane().setContent(grid);

        // Set result converter
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                if (validateForm()) {
                    return new QuestionData(
                            questionField.getText(),
                            optionAField.getText(),
                            optionBField.getText(),
                            optionCField.getText(),
                            optionDField.getText(),
                            correctAnswerComboBox.getValue()
                    );
                }
            }
            return null;
        });
    }

    private boolean validateForm() {
        if (questionField.getText().trim().isEmpty()) {
            showAlert("Validation Error", "Question text is required.");
            return false;
        }
        if (optionAField.getText().trim().isEmpty() ||
                optionBField.getText().trim().isEmpty() ||
                optionCField.getText().trim().isEmpty() ||
                optionDField.getText().trim().isEmpty()) {
            showAlert("Validation Error", "All options (A, B, C, D) are required.");
            return false;
        }
        if (correctAnswerComboBox.getValue() == null) {
            showAlert("Validation Error", "Select a correct answer.");
            return false;
        }
        return true;
    }

    private void addQuestionToDatabase(int quizId, String questionText, String optionA, String optionB, String optionC, String optionD, String correctAnswer) {
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(
                     "INSERT INTO questions (quiz_id, question_text, option_a, option_b, option_c, option_d, correct_answer) VALUES (?, ?, ?, ?, ?, ?, ?)"
             )) {
            stmt.setInt(1, quizId);
            stmt.setString(2, questionText);
            stmt.setString(3, optionA);
            stmt.setString(4, optionB);
            stmt.setString(5, optionC);
            stmt.setString(6, optionD);
            stmt.setString(7, correctAnswer);
            stmt.executeUpdate();
            showAlert("Success", "Question added successfully");
        } catch (SQLException e) {
            showAlert("Database Error", "Failed to add question: " + e.getMessage());
        }
    }

    private void updateQuestionInDatabase(int questionId, String questionText, String optionA, String optionB, String optionC, String optionD, String correctAnswer) {
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(
                     "UPDATE questions SET question_text = ?, option_a = ?, option_b = ?, option_c = ?, option_d = ?, correct_answer = ? WHERE id = ?"
             )) {
            stmt.setString(1, questionText);
            stmt.setString(2, optionA);
            stmt.setString(3, optionB);
            stmt.setString(4, optionC);
            stmt.setString(5, optionD);
            stmt.setString(6, correctAnswer);
            stmt.setInt(7, questionId);
            stmt.executeUpdate();
            showAlert("Success", "Question updated successfully");
        } catch (SQLException e) {
            showAlert("Database Error", "Failed to update question: " + e.getMessage());
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // Data transfer object for question data
    public static class QuestionData {
        private final String questionText;
        private final String optionA;
        private final String optionB;
        private final String optionC;
        private final String optionD;
        private final String correctAnswer;

        public QuestionData(String questionText, String optionA, String optionB, String optionC, String optionD, String correctAnswer) {
            this.questionText = questionText;
            this.optionA = optionA;
            this.optionB = optionB;
            this.optionC = optionC;
            this.optionD = optionD;
            this.correctAnswer = correctAnswer;
        }

        public String getQuestionText() { return questionText; }
        public String getOptionA() { return optionA; }
        public String getOptionB() { return optionB; }
        public String getOptionC() { return optionC; }
        public String getOptionD() { return optionD; }
        public String getCorrectAnswer() { return correctAnswer; }
    }
}