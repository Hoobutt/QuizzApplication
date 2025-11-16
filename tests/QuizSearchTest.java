import com.example.quizzapplication.Application;
import com.example.quizzapplication.miscControllers.QuizHomePageController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import org.testfx.framework.junit5.ApplicationTest;

import java.io.IOException;

public class QuizSearchTest extends ApplicationTest {
    Pane mainroot;
    Stage mainstage;
    private QuizHomePageController controller;

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(getClass().getClassLoader().getResource("quizPage-view.fxml"));
        this.mainroot = fxmlLoader.load();
        this.mainstage = stage;
        this.controller = fxmlLoader.getController();
        stage.setScene(new Scene(mainroot));
        stage.show();
        stage.toFront();
    }

    @Test
    public void emptySearchTest() {
        String serarchText = "";
        clickOn("#searchButton");
        assert (mainroot.lookupAll(".quiz-item").size() == controller.AvailableQuizesHook());
    }
}
