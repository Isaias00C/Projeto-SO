import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.Scene;
import ui.GameView;

public class MainFX extends Application {

    @Override
    public void start(Stage primaryStage) {
        GameView gameView = new GameView();
        Scene scene = new Scene(gameView.getRoot(), 1200, 750);
        scene.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());

        primaryStage.setTitle("Producer-Consumer - Crianças e Bolas");
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
