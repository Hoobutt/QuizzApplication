package com.example.quizzapplication;

import com.example.quizzapplication.miscControllers.QuizHomePageController;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.*;

public class HomeController {
    @FXML private Button socialButton;
    @FXML private Button aboutButton;
    @FXML private Button contactButton;
    @FXML private Button creditsButton;
    @FXML private Button loginButton;
    @FXML private Button registerButton;
    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;

    // Database connection details
    private static final String DB_URL = "jdbc:postgresql://db.rlhgofxmecvirnwxrfzo.supabase.co:5432/postgres";
    private static final String DB_USER = "postgres"; // Regular username
    private static final String DB_PASSWORD = "AFxCbFEfDTzvEvew";

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
        Application.largeScene("credits-view.fxml", creditsButton.getScene());
    }

    @FXML
    protected void onLoginButtonClick() {
        String enteredUsername = usernameField.getText().trim();
        String enteredPassword = passwordField.getText();

        if (enteredUsername.isEmpty() || enteredPassword.isEmpty()) {
            showAlert("Error", "Please enter both username and password.");
            return;
        }

        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String sql = "SELECT * FROM users WHERE username = ? AND password = ?";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, enteredUsername);
            statement.setString(2, hashPassword(enteredPassword));

            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                // Login successful
                String userRole = resultSet.getString("role");
                int userId = resultSet.getInt("id"); // Get the user ID

                // Redirect user view based on user role
                if ("admin".equals(userRole)) {
                    Application.largeScene("admin-view.fxml", loginButton.getScene());
                } else {
                    // Load quiz home page and pass the user role and ID
                    loadQuizHomePageWithUser(userRole, userId);
                }
            } else {
                showAlert("Error", "Invalid username or password.");
            }
        } catch (SQLException e) {
            showAlert("Database Error", "Unable to connect to database: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void loadQuizHomePageWithUser(String userRole, int userId) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("quizHomePage-view.fxml"));
            Parent root = loader.load();
            QuizHomePageController controller = loader.getController();
            controller.setCurrentUser(userRole, userId);
            Scene currentScene = loginButton.getScene();
            Scene newScene = new Scene(root, 1361, 873);
            Stage stage = (Stage) currentScene.getWindow();
            stage.setScene(newScene);
            stage.setTitle("Quiz Home Page");
            stage.show();

        } catch (IOException e) {
            showAlert("Error", "Failed to load quiz home page: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    protected void onRegisterButtonClick() {
        Application.changeScene("register-view.fxml", registerButton.getScene());
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private String hashPassword(String password) {
        try {
            java.security.MessageDigest md = java.security.MessageDigest.getInstance("SHA-256"); // Hash Function
            byte[] hash = md.digest(password.getBytes());
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                hexString.append(String.format("%02x", b));
            }
            return hexString.toString();
        } catch (Exception e) {
            return password;
        }
    }
}