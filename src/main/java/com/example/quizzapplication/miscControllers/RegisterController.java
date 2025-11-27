package com.example.quizzapplication.miscControllers;

import com.example.quizzapplication.Application;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import java.sql.*;

public class RegisterController {
    @FXML private Button backButton;
    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private PasswordField confirmPasswordField;
    @FXML private TextField emailField;
    @FXML private Button registerButton;
    @FXML private CheckBox adminCheckBox;
    @FXML private TextField adminCodeField;

    private static final String DB_URL = "jdbc:postgresql://db.rlhgofxmecvirnwxrfzo.supabase.co:5432/postgres?sslmode=require";
    private static final String DB_USER = "postgres";
    private static final String DB_PASSWORD = "2iuks3dy";
    private static final String ADMIN_REGISTRATION_CODE = "1234";

    @FXML
    public void initialize() {
        // Tests the database connection
        testDatabaseConnection();
        adminCodeField.setVisible(false);

        // Admin Checkbox
        adminCheckBox.selectedProperty().addListener((observable, oldValue, newValue) -> {
            adminCodeField.setVisible(newValue);
            if (!newValue) {
                adminCodeField.clear();
            }
        });
    }

    private void testDatabaseConnection() {
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            System.out.println("Database connection successful!");
        } catch (SQLException e) {
            System.out.println("Database connection failed: " + e.getMessage());
            showAlert("Database Error", "Cannot connect to database. Check MySQL Database Connection");
        }
    }

    @FXML
    protected void onBackButtonClick() {
        Application.changeScene("home-view.fxml", backButton.getScene());
    }

    @FXML
    protected void onRegisterButtonClick() {
        String username = usernameField.getText().trim();
        String password = passwordField.getText();
        String confirmPassword = confirmPasswordField.getText();
        String email = emailField.getText().trim();
        boolean isAdmin = adminCheckBox.isSelected();
        String adminCode = adminCodeField.getText();

        System.out.println("Attempting registration for username: " + username);

        // Validation
        if (username.isEmpty() || password.isEmpty() || email.isEmpty()) {
            showAlert("Error", "Please fill in all fields.");
            return;
        }

        if (!password.equals(confirmPassword)) {
            showAlert("Error", "Passwords do not match.");
            return;
        }

        if (!isValidEmail(email)) {
            showAlert("Error", "Please enter a valid email address.");
            return;
        }

        if (username.length() < 3) {
            showAlert("Error", "Username must be at least 3 characters long.");
            return;
        }

        if (password.length() < 4) {
            showAlert("Error", "Password must be at least 4 characters long.");
            return;
        }

        // Admin registration validation (Admin Code is "1234")
        if (isAdmin) {
            if (adminCode.isEmpty()) {
                showAlert("Error", "Please enter the admin registration code.");
                return;
            }
            if (!adminCode.equals(ADMIN_REGISTRATION_CODE)) {
                showAlert("Error", "Invalid admin registration code.");
                return;
            }
        }

        Boolean usernameTaken = isUsernameTaken(username);
        if (usernameTaken == null) {
            showAlert("Database Error", "Unable to check username availability.");
            return;
        } else if (usernameTaken) {
            showAlert("Error", "Username '" + username + "' already exists. Please choose a different one.");
            return;
        }

        Boolean emailTaken = isEmailTaken(email);
        if (emailTaken == null) {
            showAlert("Database Error", "Unable to check email availability.");
            return;
        } else if (emailTaken) {
            showAlert("Error", "Email '" + email + "' already registered. Please use a different email.");
            return;
        }

        String role = isAdmin ? "admin" : "student";
        if (registerUser(username, password, email, role)) {
            showAlert("Success", "Registration successful! You are now registered as a " + role + ".");

            // Redirect based on role
            if (isAdmin) {
                Application.largeScene("admin-view.fxml", registerButton.getScene());
            } else {
                Application.largeScene("quizHomePage-view.fxml", registerButton.getScene());
            }
        } else {
            showAlert("Error", "Registration failed. Please try again.");
        }
    }

    private Boolean isUsernameTaken(String username) {
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String sql = "SELECT id FROM users WHERE username = ?";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, username);
            ResultSet resultSet = statement.executeQuery();
            boolean taken = resultSet.next();
            System.out.println("Username '" + username + "' taken: " + taken);
            return taken;
        } catch (SQLException e) {
            System.err.println("Error checking username: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    private Boolean isEmailTaken(String email) {
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String sql = "SELECT id FROM users WHERE email = ?";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, email);
            ResultSet resultSet = statement.executeQuery();
            boolean taken = resultSet.next();
            System.out.println("Email '" + email + "' taken: " + taken);
            return taken;
        } catch (SQLException e) {
            System.err.println("Error checking email: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    private boolean registerUser(String username, String password, String email, String role) {
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String sql = "INSERT INTO users (username, password, email, role) VALUES (?, ?, ?, ?)";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, username);
            statement.setString(2, hashPassword(password));
            statement.setString(3, email);
            statement.setString(4, role);

            int rowsAffected = statement.executeUpdate();
            System.out.println("Registration successful for: " + username);
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Error registering user: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    private boolean isValidEmail(String email) {
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$"; // Checks email validity
        return email.matches(emailRegex);
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
            java.security.MessageDigest md = java.security.MessageDigest.getInstance("SHA-256");
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