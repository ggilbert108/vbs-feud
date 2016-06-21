import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

/**
 * Created by programming on 6/21/16.
 */
public class ControlStage extends Stage
{
    private GameState gameState;
    private SubmissionPane[] submissionPanes;

    public ControlStage(GameState gameState)
    {
        this.gameState = gameState;

        GridPane root = new GridPane();
        root.setAlignment(Pos.TOP_CENTER);

        submissionPanes = new SubmissionPane[GameState.MAX_RESULTS];
        for(int i = 0; i < submissionPanes.length; i++)
        {
            int col = i / GameState.MAX_RESULTS_HALF;
            int row = i % GameState.MAX_RESULTS_HALF;

            submissionPanes[i] = new SubmissionPane(i);
            root.add(submissionPanes[i], col, row);
        }

        setScene(new Scene(root, 500, 400));
        show();
    }

    public class SubmissionPane extends GridPane
    {
        private Button submit;
        private TextField input;
        private int index;

        public SubmissionPane(int index)
        {
            this.index = index;

            input = new TextField();
            add(input, 0, 0);

            submit = new Button("flip");
            submit.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    gameState.submitResultText(index, input.getText());
                }
            });
            add(submit, 1, 0);
        }
    }
}
