package com.example.quizzapplication.miscControllers;

import com.example.quizzapplication.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class QuizHomePageController {
    @FXML private Button backButton;
    @FXML private Button enterQuizButton;
    @FXML private ListView<String> quizListView;
    @FXML private Button adminpanelButton;

    // Connects to Database
    private static final String DB_URL = "jdbc:mysql://localhost:3306/users";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "2iuks3dy";

    // Store the current user's role and ID
    private String currentUserRole = "student";
    private int currentUserId = 1; // Default user ID

    // Store quiz data
    private Map<String, Integer> quizIdMap = new HashMap<>();
    private Map<String, String> quizTitleMap = new HashMap<>();

    @FXML
    public void initialize() {
        // Load quizzes from database
        loadQuizzesFromDatabase();

        // Set up selection listener
        quizListView.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> {
                    enterQuizButton.setDisable(newValue == null);
                }
        );
        enterQuizButton.setDisable(true);

        // Hide admin panel button by default
        adminpanelButton.setVisible(false);
        adminpanelButton.setManaged(false);

        // Check user role and adjust UI accordingly
        checkUserRoleAndAdjustUI();
    }

    // Method to set the current user's role and ID
    public void setCurrentUser(String role, int userId) {
        this.currentUserRole = role;
        this.currentUserId = userId;
        checkUserRoleAndAdjustUI();
    }

    private void checkUserRoleAndAdjustUI() {
        if ("admin".equalsIgnoreCase(currentUserRole)) {
            adminpanelButton.setVisible(true);
            adminpanelButton.setManaged(true);
        } else {
            adminpanelButton.setVisible(false);
            adminpanelButton.setManaged(false);
        }
    }

    private void loadQuizzesFromDatabase() {
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement stmt = conn.prepareStatement("SELECT id, title FROM quizzes WHERE is_active = TRUE");
             ResultSet rs = stmt.executeQuery()) {

            ObservableList<String> quizzes = FXCollections.observableArrayList();
            quizIdMap.clear();
            quizTitleMap.clear();

            while (rs.next()) {
                int quizId = rs.getInt("id");
                String quizTitle = rs.getString("title");

                // Format for display
                String displayText = quizTitle;
                quizzes.add(displayText);

                quizIdMap.put(displayText, quizId);
                quizTitleMap.put(displayText, quizTitle);
            }
            quizListView.setItems(quizzes);

        } catch (SQLException e) {
            showAlert("Database Error", "Failed to load quizzes: " + e.getMessage());
        }
    }

    @FXML
    protected void onBackButtonClick() {
        Application.changeScene("home-view.fxml", backButton.getScene());
    }

    @FXML
    protected void onAdminPanelButtonClick() {
        Application.largeScene("admin-view.fxml", adminpanelButton.getScene());
    }

    @FXML
    protected void onEnterQuizButtonClick() {
        String selectedQuiz = quizListView.getSelectionModel().getSelectedItem();
        if (selectedQuiz != null && !selectedQuiz.isEmpty()) {
            Alert alert = new Alert(AlertType.CONFIRMATION);
            alert.setTitle("Start Quiz");
            alert.setContentText("You are about to start: " + selectedQuiz +
                    "\n\nOnce quiz is started, you cannot pause the quiz.");

            alert.showAndWait().ifPresent(response -> {
                if (response == javafx.scene.control.ButtonType.OK) {
                    startQuiz(selectedQuiz);
                }
            });
        } else {
            Alert alert = new Alert(AlertType.WARNING);
            alert.setTitle("No Quiz Selected");
            alert.setHeaderText("Select a quiz to start");
            alert.showAndWait();
        }
    }

    private void startQuiz(String selectedQuiz) {
        try {
            Integer quizId = quizIdMap.get(selectedQuiz);
            String quizTitle = quizTitleMap.get(selectedQuiz);

            if (quizId != null && quizTitle != null) {
                // Check if user has already taken this quiz
                if (hasUserTakenQuiz(quizId)) {
                    Alert alert = new Alert(AlertType.CONFIRMATION);
                    alert.setTitle("Quiz Already Taken");
                    alert.setContentText("Would you like to retake the quiz?");
                    alert.showAndWait().ifPresent(response -> {
                        if (response == javafx.scene.control.ButtonType.OK) {
                            launchQuizSession(quizId, quizTitle);
                        }
                    });
                } else {
                    launchQuizSession(quizId, quizTitle);
                }
            } else {
                showAlert("Error", "Could not find quiz data. Please try again.");
            }
        } catch (Exception e) {
            showAlert("Error", "Failed to start quiz: " + e.getMessage());
        }
    }

    private boolean hasUserTakenQuiz(int quizId) {
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement stmt = conn.prepareStatement("SELECT COUNT(*) FROM results WHERE user_id = ? AND quiz_id = ?")) {
            stmt.setInt(1, currentUserId);
            stmt.setInt(2, quizId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            System.err.println("Error checking quiz history: " + e.getMessage());
        }
        return false;
    }

    private void launchQuizSession(int quizId, String quizTitle) {
        try {
            // Data holder to pass quiz information
            Map<String, Object> quizData = new HashMap<>();
            quizData.put("quizId", quizId);
            quizData.put("quizTitle", quizTitle);
            quizData.put("userId", currentUserId);

            // Stores the data in Application context for the quiz session to access
            Application.setSessionData("quizData", quizData);
            Application.changeScene("quizSession-view.fxml", enterQuizButton.getScene());

        } catch (Exception e) {
            showAlert("Error", "Failed to launch quiz session: " + e.getMessage());
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}