import javafx.application.Application;
import javafx.stage.Stage;

/**
 * Created by gaston on 6/21/2016.
 */
public class MainApp extends Application
{
    public static void main(String[] args)
    {
        launch();
    }

    @Override
    public void start(Stage primaryStage)
    {
        GameState gameState = new GameState();

        new DisplayStage(gameState);
        new ControlStage(gameState);

        gameState.setNumResults(1);
    }
}
