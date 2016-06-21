import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;
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

        int row = 0;
        submissionPanes = new SubmissionPane[GameState.MAX_RESULTS];
        for(int i = 0; i < submissionPanes.length; i++)
        {
            int col = i / GameState.MAX_RESULTS_HALF;
            row = i % GameState.MAX_RESULTS_HALF;

            submissionPanes[i] = new SubmissionPane(i);
            if(i >= 1)
            {
                submissionPanes[i].setEnabled(false);
            }
            root.add(submissionPanes[i], col, row);
        }

        row++;
        root.add(new SetupPane(), 0, row, 2, 1);


        setScene(new Scene(root, 500, 400));
        show();
    }

    public void onNumResultsChanged()
    {
        for(int i = 0; i < gameState.MAX_RESULTS; i++)
        {
            boolean enabled = i < gameState.getNumResults();
            submissionPanes[i].setEnabled(enabled);
        }
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
            submit.setOnAction(event -> gameState.submitResultText(index, input.getText()));
            add(submit, 1, 0);

        }

        public void setEnabled(boolean enabled)
        {
            input.setEditable(enabled);
            submit.setDisable(!enabled);
        }
    }

    public class SetupPane extends GridPane
    {
        private TextField input;
        private Button submit;

        public SetupPane()
        {
            alignmentProperty().setValue(Pos.CENTER);

            input = new TextField();
            add(input, 0, 0, 2, 1);

            submit = new Button("Set # results (reset)");
            submit.setOnAction(event ->
            {
                String inputText = input.getText();
                try
                {
                    gameState.setNumResults(Integer.parseInt(inputText));
                    onNumResultsChanged();
                }
                catch(Exception e) {}
            });
            add(submit, 3, 0, 2, 1);
        }
    }
}
