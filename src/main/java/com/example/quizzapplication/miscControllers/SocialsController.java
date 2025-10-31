package com.example.quizzapplication.miscControllers;

import com.example.quizzapplication.Application;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import java.awt.Desktop;
import java.net.URI;

public class SocialsController {
    @FXML
    private Button backButton;
    @FXML
    private Button Instagram;
    @FXML
    private Button Discord;
    @FXML
    private Button GitHub;
    @FXML
    private Button LinkedIn;
    @FXML
    private Button Twitter;


    @FXML
    protected void onBackButtonClick() {
        Application.changeScene("home-view.fxml", backButton.getScene());
    }
    @FXML
    protected void GitHub() {
        openWebPage("https://www.instagram.com/groupechoquiz/");
    }
    @FXML
    protected void Twitter() {
        openWebPage("https://x.com/Echo139736");
    }
    @FXML
    protected void LinkedIn() {
        openWebPage("https://www.linkedin.com/in/echo-echo-143927388/");
    }
    @FXML
    protected void Discord() {
        openWebPage("https://discord.com/users/1427094960771043339");
    }
    @FXML
    protected void Instagram() {
        openWebPage("https://www.instagram.com/groupechoquiz/");
    }
    private void openWebPage(String url) {
        try {
            Desktop desktop = Desktop.getDesktop();
            desktop.browse(new URI(url));
        } catch (Exception e) {
            e.printStackTrace();
            javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Cannot open web browser");
            alert.setContentText("Unable to open: " + url);
            alert.showAndWait();
        }
    }
}
