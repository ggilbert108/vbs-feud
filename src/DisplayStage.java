import javafx.animation.Interpolator;
import javafx.animation.RotateTransition;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
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
        setImageviewBounds(background, new Rectangle2D(0, 0, initWidth, initHeight));
        root.getChildren().add(background);

        flipBox(5);
        show();
    }

    private Rectangle2D getResultBox(int index)
    {
        final double baseXRatio = (index < GameState.MAX_RESULTS_HALF) ? .17 : .505;
        final double baseYRatio = .28;
        final double widthRatio = .326;
        final double heightRatio = .15;

        final double deltaY = .16;

        double sceneWidth = getScene().getWidth();
        double sceneHeight = getScene().getHeight();

        int col = index / GameState.MAX_RESULTS_HALF;
        int row = index % GameState.MAX_RESULTS_HALF;

        double x = baseXRatio * sceneWidth;
        double y = (baseYRatio + row * deltaY) * sceneHeight;
        double width = widthRatio * sceneWidth;
        double height = heightRatio * sceneHeight;

        System.out.println(x);

        return new Rectangle2D(x, y, width, height);
    }


    private void flipBox(int index)
    {
        ImageView box = new ImageView(
                new Image("result_box.png")
        );
        setImageviewBounds(box, getResultBox(index));
        root.getChildren().add(box);

        RotateTransition rotator = createRotator(box);
        rotator.play();
        rotator.onFinishedProperty().setValue(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                root.getChildren().remove(box);
            }
        });
    }

    private RotateTransition createRotator(Node card) {
        RotateTransition rotator = new RotateTransition(Duration.millis(1000), card);
        rotator.setAxis(Rotate.X_AXIS);
        rotator.setFromAngle(0);
        rotator.setToAngle(360);
        rotator.setInterpolator(Interpolator.LINEAR);
        rotator.setCycleCount(1);

        return rotator;
    }

    private void setImageviewBounds(ImageView image, Rectangle2D bounds)
    {
        image.setX(bounds.getMinX());
        image.setY(bounds.getMinY());
        image.setFitWidth(bounds.getWidth());
        image.setFitHeight(bounds.getHeight());
    }

}
