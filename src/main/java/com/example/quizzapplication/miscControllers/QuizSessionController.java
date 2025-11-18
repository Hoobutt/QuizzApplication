package com.example.quizzapplication.miscControllers;

import javafx.fxml.FXML;
import javafx.scene.control.RadioButton;
import javafx.scene.layout.GridPane;
import org.w3c.dom.Text;

import java.lang.classfile.Label;
import java.util.Date;
import java.util.List;
import java.util.Timer;

public class QuizSessionController {
    @FXML
    private Label quizid;
    @FXML
    private Text question;
    @FXML
    private Text answer;
    @FXML
    private RadioButton  answer1;
    @FXML
    private RadioButton answer2;
    @FXML
    private RadioButton answer3;
    @FXML
    private RadioButton answer4;
    @FXML
    private GridPane gridPane;
    @FXML
    private Timer timer;

    // Database connection details
    private static final String DB_URL = "jdbc:mysql://localhost:3306/users";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "2iuks3dy";

}
