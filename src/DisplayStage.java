import javafx.animation.Interpolator;
import javafx.animation.RotateTransition;
import javafx.geometry.Rectangle2D;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.PerspectiveCamera;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.shape.Rectangle;
import javafx.scene.transform.Rotate;
import javafx.stage.Stage;
import javafx.util.Duration;

/**
 * Created by gaston on 6/21/2016.
 */
public class DisplayStage extends Stage
{
    private Group root;
    private Rectangle2D[] resultBoxes;

    public DisplayStage()
    {
        final int initWidth = 800;
        final int initHeight = 680;

        root = new Group();

        Scene scene = new Scene(root, initWidth, initHeight);
        scene.setCamera(new PerspectiveCamera());
        setScene(scene);

        ImageView background = new ImageView(
                new Image("results.png")
        );
        background.setViewport(new Rectangle2D(0, 0, initWidth, initHeight));
        root.getChildren().add(background);

        show();
    }

    private void initResultsBoxes()
    {
        resultBoxes = new Rectangle2D[GameState.MAX_RESULTS];
    }

    private void flipBox()
    {
        ImageView box = new ImageView(
                new Image("result_box.png")
        );
        root.getChildren().add(box);

        RotateTransition rotator = createRotator(box);
        rotator.play();
    }

    private RotateTransition createRotator(Node card) {
        RotateTransition rotator = new RotateTransition(Duration.millis(500), card);
        rotator.setAxis(Rotate.X_AXIS);
        rotator.setFromAngle(0);
        rotator.setToAngle(360);
        rotator.setInterpolator(Interpolator.LINEAR);
        rotator.setCycleCount(1);

        return rotator;
    }

}
