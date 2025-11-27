package com.example.quizzapplication.miscControllers;

import com.example.quizzapplication.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;

import java.sql.*;
import java.util.Optional;

public class AdminController {
    @FXML private Button backButton;
    @FXML private Button quizHomePageButton;

    // User Display
    @FXML private TableView<User> userTableView;
    @FXML private TableColumn<User, String> userNameColumn;
    @FXML private TableColumn<User, String> userEmailColumn;
    @FXML private TableColumn<User, String> userRoleColumn;

    // Quiz Management
    @FXML private TableView<Quiz> quizTableView;
    @FXML private TableColumn<Quiz, String> quizTitleColumn;

    // Question Management
    @FXML private TableView<Quiz> questionQuizTableView;
    @FXML private TableView<Question> questionTableView;
    @FXML private TableColumn<Question, String> questionTextColumn;
    @FXML private TableColumn<Question, String> correctAnswerColumn;

    // Results Management
    @FXML private TableView<Quiz> resultsQuizTableView;
    @FXML private TableView<Result> resultsTableView;
    @FXML private TableColumn<Result, String> resultUserColumn;
    @FXML private TableColumn<Result, Integer> resultScoreColumn;
    @FXML private TableColumn<Result, Double> resultPercentageColumn;
    @FXML private TableColumn<Result, String> resultTimeColumn;

    private static final String DB_URL = "jdbc:postgresql://db.rlhgofxmecvirnwxrfzo.supabase.co:5432/postgres?sslmode=require";
    private static final String DB_USER = "postgres";
    private static final String DB_PASSWORD = "2iuks3dy";

    private CreateQuestionController createQuestionController;

    @FXML
    public void initialize() {
        createQuestionController = new CreateQuestionController();
        initializeUserManagement();
        initializeQuizManagement();
        initializeQuestionManagement();
        initializeResultsManagement();
        loadInitialData();
    }

    private void initializeUserManagement() {
        userNameColumn.setCellValueFactory(new PropertyValueFactory<>("username"));
        userEmailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
        userRoleColumn.setCellValueFactory(new PropertyValueFactory<>("role"));
        loadUsers();
    }

    private void initializeQuizManagement() {
        quizTitleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
        loadQuizzes();
    }

    private void initializeQuestionManagement() {
        // Left side - Quiz list
        TableColumn<Quiz, String> questionQuizTitleColumn = new TableColumn<>("Quiz Title");
        questionQuizTitleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
        questionQuizTableView.getColumns().add(questionQuizTitleColumn);

        // Right side - Questions list
        questionTextColumn.setCellValueFactory(new PropertyValueFactory<>("questionText"));
        correctAnswerColumn.setCellValueFactory(new PropertyValueFactory<>("correctAnswer"));

        // Add listener for quiz selection
        questionQuizTableView.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> {
                    if (newValue != null) {
                        createQuestionController.setQuizId(newValue.getId());
                        loadQuestionsForQuiz(newValue.getId());
                    }
                }
        );

        // Set refresh callback for question controller
        createQuestionController.setRefreshCallback(() -> {
            Quiz selectedQuiz = questionQuizTableView.getSelectionModel().getSelectedItem();
            if (selectedQuiz != null) {
                loadQuestionsForQuiz(selectedQuiz.getId());
            }
        });

        loadQuizzesForQuestions();
    }

    private void initializeResultsManagement() {
        // Left side - Quiz list
        TableColumn<Quiz, String> resultsQuizTitleColumn = new TableColumn<>("Quiz Title");
        resultsQuizTitleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
        resultsQuizTableView.getColumns().add(resultsQuizTitleColumn);

        // Right side - Results list
        resultUserColumn.setCellValueFactory(new PropertyValueFactory<>("username"));
        resultScoreColumn.setCellValueFactory(new PropertyValueFactory<>("score"));
        resultPercentageColumn.setCellValueFactory(new PropertyValueFactory<>("percentage"));
        resultTimeColumn.setCellValueFactory(new PropertyValueFactory<>("timeUsedFormatted"));

        // Listener for Quiz Section
        resultsQuizTableView.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> {
                    if (newValue != null) {
                        loadResultsForQuiz(newValue.getId());
                    }
                }
        );

        loadQuizzesForResults();
    }

    private void loadInitialData() {
        loadUsers();
        loadQuizzes();
    }

    // Quiz Management Methods
    @FXML
    private void onCreateQuizClick() {
        Dialog<QuizData> dialog = createQuizDialog("Create Quiz", null);
        Optional<QuizData> result = dialog.showAndWait();
        result.ifPresent(quizData -> {
            addQuizToDatabase(quizData.getTitle());
        });
    }

    @FXML
    private void onEditQuizClick() {
        Quiz selectedQuiz = quizTableView.getSelectionModel().getSelectedItem();
        if (selectedQuiz != null) {
            Dialog<QuizData> dialog = createQuizDialog("Edit Quiz", selectedQuiz);
            Optional<QuizData> result = dialog.showAndWait();
            result.ifPresent(quizData -> {
                updateQuizInDatabase(selectedQuiz.getId(), quizData.getTitle());
            });
        } else {
            showAlert("Selection Error", "Select a quiz to edit.");
        }
    }

    @FXML
    private void onDeleteQuizClick() {
        Quiz selectedQuiz = quizTableView.getSelectionModel().getSelectedItem();
        if (selectedQuiz != null) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Delete Quiz");
            alert.setHeaderText("Are you sure you want to delete quiz: " + selectedQuiz.getTitle() + "?");
            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                deleteQuizFromDatabase(selectedQuiz.getId());
            }
        } else {
            showAlert("Selection Error", "Select a quiz to delete.");
        }
    }

    // Question Management Methods
    @FXML
    private void onAddQuestionClick() {
        Quiz selectedQuiz = questionQuizTableView.getSelectionModel().getSelectedItem();
        if (selectedQuiz != null) {
            createQuestionController.showAddQuestionDialog();
        } else {
            showAlert("Selection Error", "Select a quiz first.");
        }
    }

    @FXML
    private void onEditQuestionClick() {
        Question selectedQuestion = questionTableView.getSelectionModel().getSelectedItem();
        if (selectedQuestion != null) {
            createQuestionController.showEditQuestionDialog(selectedQuestion);
        } else {
            showAlert("Selection Error", "Select a question to edit.");
        }
    }

    @FXML
    private void onDeleteQuestionClick() {
        Question selectedQuestion = questionTableView.getSelectionModel().getSelectedItem();
        if (selectedQuestion != null) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Delete Question");
            alert.setHeaderText("Are you sure you want to delete this question?");
            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                deleteQuestionFromDatabase(selectedQuestion.getId());
            }
        } else {
            showAlert("Selection Error", "Select a question to delete.");
        }
    }

    // Data Loading Methods
    private void loadUsers() {
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement stmt = conn.prepareStatement("SELECT * FROM users");
             ResultSet rs = stmt.executeQuery()) {

            ObservableList<User> users = FXCollections.observableArrayList();
            while (rs.next()) {
                users.add(new User(
                        rs.getInt("id"),
                        rs.getString("username"),
                        rs.getString("email"),
                        rs.getString("role")
                ));
            }
            userTableView.setItems(users);
        } catch (SQLException e) {
            showAlert("Database Error", "Failed to load users: " + e.getMessage());
        }
    }

    private void loadQuizzes() {
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement stmt = conn.prepareStatement("SELECT * FROM quizzes WHERE is_active = TRUE");
             ResultSet rs = stmt.executeQuery()) {

            ObservableList<Quiz> quizzes = FXCollections.observableArrayList();
            while (rs.next()) {
                quizzes.add(new Quiz(
                        rs.getInt("id"),
                        rs.getString("title"),
                        rs.getString("description")
                ));
            }
            quizTableView.setItems(quizzes);
        } catch (SQLException e) {
            showAlert("Database Error", "Failed to load quizzes: " + e.getMessage());
        }
    }

    private void loadQuizzesForQuestions() {
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement stmt = conn.prepareStatement("SELECT * FROM quizzes WHERE is_active = TRUE");
             ResultSet rs = stmt.executeQuery()) {

            ObservableList<Quiz> quizzes = FXCollections.observableArrayList();
            while (rs.next()) {
                quizzes.add(new Quiz(
                        rs.getInt("id"),
                        rs.getString("title"),
                        rs.getString("description")
                ));
            }
            questionQuizTableView.setItems(quizzes);
        } catch (SQLException e) {
            showAlert("Database Error", "Failed to load quizzes: " + e.getMessage());
        }
    }

    private void loadQuestionsForQuiz(int quizId) {
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement stmt = conn.prepareStatement("SELECT * FROM questions WHERE quiz_id = ?");
        ) {
            stmt.setInt(1, quizId);
            ResultSet rs = stmt.executeQuery();

            ObservableList<Question> questions = FXCollections.observableArrayList();
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
            }
            questionTableView.setItems(questions);
        } catch (SQLException e) {
            showAlert("Database Error", "Failed to load questions: " + e.getMessage());
        }
    }

    private void loadQuizzesForResults() {
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement stmt = conn.prepareStatement("SELECT * FROM quizzes WHERE is_active = TRUE");
             ResultSet rs = stmt.executeQuery()) {

            ObservableList<Quiz> quizzes = FXCollections.observableArrayList();
            while (rs.next()) {
                quizzes.add(new Quiz(
                        rs.getInt("id"),
                        rs.getString("title"),
                        rs.getString("description")
                ));
            }
            resultsQuizTableView.setItems(quizzes);
        } catch (SQLException e) {
            showAlert("Database Error", "Failed to load quizzes: " + e.getMessage());
        }
    }

    private void loadResultsForQuiz(int quizId) {
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(
                     "SELECT r.*, u.username FROM results r " +
                             "JOIN users u ON r.user_id = u.id " +
                             "WHERE r.quiz_id = ? ORDER BY r.percentage DESC"
             );
        ) {
            stmt.setInt(1, quizId);
            ResultSet rs = stmt.executeQuery();

            ObservableList<Result> results = FXCollections.observableArrayList();
            while (rs.next()) {
                results.add(new Result(
                        rs.getInt("id"),
                        rs.getInt("user_id"),
                        rs.getInt("quiz_id"),
                        rs.getString("username"),
                        rs.getInt("score"),
                        rs.getInt("total_questions"),
                        rs.getDouble("percentage"),
                        rs.getInt("time_used")
                ));
            }
            resultsTableView.setItems(results);
        } catch (SQLException e) {
            showAlert("Database Error", "Failed to load results: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Database Operation Methods
    private void addQuizToDatabase(String title) {
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            // Find an admin user from the database
            int adminId = findAdminUserId(conn);
            if (adminId == -1) {
                showAlert("Error", "No admin user found in database. Create an admin user first.");
                return;
            }

            // Insert the quiz with the found admin ID
            try (PreparedStatement stmt = conn.prepareStatement(
                    "INSERT INTO quizzes (title, created_by) VALUES (?, ?)"
            )) {
                stmt.setString(1, title);
                stmt.setInt(2, adminId);
                stmt.executeUpdate();
                showAlert("Success", "Quiz created successfully!");
                loadQuizzes();
                loadQuizzesForQuestions();
                loadQuizzesForResults();
            }
        } catch (SQLException e) {
            showAlert("Database Error", "Failed to add quiz: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private int findAdminUserId(Connection conn) throws SQLException {
        try (PreparedStatement stmt = conn.prepareStatement(
                "SELECT id FROM users WHERE role = 'admin' LIMIT 1"
        );
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                return rs.getInt("id");
            }
        }
        return -1; // No admin was found
    }

    private void updateQuizInDatabase(int quizId, String newTitle) {
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(
                     "UPDATE quizzes SET title = ? WHERE id = ?"
             )) {
            stmt.setString(1, newTitle);
            stmt.setInt(2, quizId);
            stmt.executeUpdate();
            loadQuizzes();
            loadQuizzesForQuestions();
            loadQuizzesForResults();
        } catch (SQLException e) {
            showAlert("Database Error", "Failed to update quiz: " + e.getMessage());
        }
    }

    private void deleteQuizFromDatabase(int quizId) {
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement stmt = conn.prepareStatement("UPDATE quizzes SET is_active = FALSE WHERE id = ?")) {
            stmt.setInt(1, quizId);
            stmt.executeUpdate();
            loadQuizzes();
            loadQuizzesForQuestions();
            loadQuizzesForResults();
        } catch (SQLException e) {
            showAlert("Database Error", "Failed to delete quiz: " + e.getMessage());
        }
    }

    private void deleteQuestionFromDatabase(int questionId) {
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement stmt = conn.prepareStatement("DELETE FROM questions WHERE id = ?")) {
            stmt.setInt(1, questionId);
            stmt.executeUpdate();
            Quiz selectedQuiz = questionQuizTableView.getSelectionModel().getSelectedItem();
            if (selectedQuiz != null) {
                loadQuestionsForQuiz(selectedQuiz.getId());
            }
        } catch (SQLException e) {
            showAlert("Database Error", "Failed to delete question: " + e.getMessage());
        }
    }

    // Helper methods
    private Dialog<QuizData> createQuizDialog(String title, Quiz existingQuiz) {
        Dialog<QuizData> dialog = new Dialog<>();
        dialog.setTitle(title);

        ButtonType saveButtonType = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new javafx.geometry.Insets(20, 150, 10, 10));

        TextField titleField = new TextField();
        TextArea descriptionArea = new TextArea();
        descriptionArea.setPrefRowCount(3);
        descriptionArea.setWrapText(true);

        grid.add(new Label("Quiz Title:"), 0, 0);
        grid.add(titleField, 1, 0);

        if (existingQuiz != null) {
            titleField.setText(existingQuiz.getTitle());
        }

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                return new QuizData(titleField.getText(), descriptionArea.getText());
            }
            return null;
        });

        return dialog;
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    protected void onBackButtonClick() {
        Application.changeScene("home-view.fxml", backButton.getScene());
    }

    @FXML
    protected void quizHomePageButtonClick() {
        Application.largeScene("quizHomePage-view.fxml", quizHomePageButton.getScene());
    }

    // Data Model Class for Users
    public static class User {
        private final int id;
        private final String username;
        private final String email;
        private final String role;

        public User(int id, String username, String email, String role) {
            this.id = id;
            this.username = username;
            this.email = email;
            this.role = role;
        }

        public int getId() { return id; }
        public String getUsername() { return username; }
        public String getEmail() { return email; }
        public String getRole() { return role; }
    }

    // Data Model Class for Quiz
    public static class Quiz {
        private final int id;
        private final String title;

        public Quiz(int id, String title, String description) {
            this.id = id;
            this.title = title;
        }

        public int getId() { return id; }
        public String getTitle() { return title; }
    }

    // Data transfer object for quiz dialog
    public static class QuizData {
        private final String title;
        public QuizData(String title, String description) {
            this.title = title;
        }

        public String getTitle() { return title; }
    }

    // Data Model Class for Questions
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

    // Data Model Class for Results
    public static class Result {
        private final int id;
        private final int userId;
        private final int quizId;
        private final String username;
        private final int score;
        private final int totalQuestions;
        private final double percentage;
        private final int timeUsed; // in seconds

        public Result(int id, int userId, int quizId, String username, int score, int totalQuestions, double percentage, int timeUsed) {
            this.id = id;
            this.userId = userId;
            this.quizId = quizId;
            this.username = username;
            this.score = score;
            this.totalQuestions = totalQuestions;
            this.percentage = percentage;
            this.timeUsed = timeUsed;
        }

        // Getters for the results tab
        public int getId() { return id; }
        public int getUserId() { return userId; }
        public int getQuizId() { return quizId; }
        public String getUsername() { return username; }
        public int getScore() { return score; }
        public int getTotalQuestions() { return totalQuestions; }
        public double getPercentage() { return percentage; }
        public int getTimeUsed() { return timeUsed; }

        // User time used for quiz
        public String getTimeUsedFormatted() {
            int minutes = timeUsed / 60;
            int seconds = timeUsed % 60;
            return String.format("%02d:%02d", minutes, seconds);
        }
    }
}