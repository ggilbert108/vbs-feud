import javafx.animation.Interpolator;
import javafx.animation.RotateTransition;
import javafx.animation.SequentialTransition;
import javafx.geometry.BoundingBox;
import javafx.geometry.Bounds;
import javafx.geometry.Rectangle2D;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.PerspectiveCamera;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.transform.Rotate;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.File;

/**
 * Created by gaston on 6/21/2016.
 */
public class DisplayStage extends Stage implements ResultSubmittedListener, ResetListener
{
    private final double RESULT_WIDTH_RATIO = .75;

    private GameState gameState;
    private Group root;
    private Text[] results;
    private ImageView[] numberImages;

    public DisplayStage(GameState gameState)
    {
        this.gameState = gameState;
        gameState.addResultSubmittedListener(this);
        gameState.addResetListener(this);

        results = new Text[GameState.MAX_RESULTS];
        numberImages = new ImageView[6];

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

        reset();
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

        return new Rectangle2D(x, y, width, height);
    }


    private void flipBox(int index, String result)
    {
        Media sound = new Media(new File("right_answer.wav").toURI().toString());
        MediaPlayer mediaPlayer = new MediaPlayer(sound);
        mediaPlayer.play();


        if(results[index] != null)
        {
            root.getChildren().remove(results[index]);
        }
        if(numberImages[index] != null)
        {
            root.getChildren().remove(numberImages[index]);
        }

        ImageView box = new ImageView(
                new Image("result_box.png")
        );
        Rectangle2D resultRect = getResultBox(index);
        setImageviewBounds(box, resultRect);
        root.getChildren().add(box);

        SequentialTransition rotator = createRotator(box);
        rotator.play();
        rotator.onFinishedProperty().setValue(event -> {
            Text text = new Text(result.toUpperCase());
            results[index] = text;
            text.setFill(Color.WHITE);

            final double xOffsetRatio = 0.01;
            double xOffset = xOffsetRatio * getWidth();

            Bounds bounds = new BoundingBox(resultRect.getMinX() + xOffset, resultRect.getMinY(),
                    resultRect.getWidth() * RESULT_WIDTH_RATIO, resultRect.getHeight());
            fitTextInBounds(bounds, text);

            root.getChildren().add(text);

            root.getChildren().remove(box);
            text.toFront();
        });
    }

    private SequentialTransition createRotator(Node card) {
        RotateTransition rotator1 = new RotateTransition(Duration.millis(250), card);
        rotator1.setAxis(Rotate.X_AXIS);
        rotator1.setFromAngle(0);
        rotator1.setToAngle(90);
        rotator1.setInterpolator(Interpolator.LINEAR);
        rotator1.setCycleCount(1);

        rotator1.onFinishedProperty().setValue(event -> card.setScaleY(-1));


        RotateTransition rotator2 = new RotateTransition(Duration.millis(250), card);
        rotator2.setAxis(Rotate.X_AXIS);
        rotator2.setFromAngle(90);
        rotator2.setToAngle(180);
        rotator2.setInterpolator(Interpolator.LINEAR);
        rotator2.setCycleCount(1);

        SequentialTransition sequence = new SequentialTransition();
        sequence.getChildren().addAll(rotator1, rotator2);

        return sequence;
    }

    private void setImageviewBounds(ImageView image, Rectangle2D bounds)
    {
        image.setX(bounds.getMinX());
        image.setY(bounds.getMinY());
        image.setFitWidth(bounds.getWidth());
        image.setFitHeight(bounds.getHeight());
    }

    private void fitTextInBounds(Bounds outer, Text text)
    {
        for(int i = 72; i >= 1; i--)
        {
            text.setFont(new Font(i));
            Bounds textBounds = text.getBoundsInParent();

            double outerCenterX = (outer.getMaxX() + outer.getMinX()) / 2;
            double outerCenterY = (outer.getMaxY() + outer.getMinY()) / 2;

            text.setX(outerCenterX - textBounds.getWidth() / 2);
            text.setY(outerCenterY + textBounds.getHeight() / 4);

            if(outer.contains(textBounds))
                break;
        }
    }

    private ImageView getNumImage(int index)
    {
        return new ImageView(
                new Image("num" + (index + 1) + ".png")
        );
    }

    @Override
    public void resultSubmitted(SubmissionEvent e)
    {
        flipBox(e.index, gameState.getResult(e.index));
    }

    @Override
    public void reset()
    {
        for(int i = 0; i < numberImages.length; i++)
        {
            if(numberImages[i] != null)
            {
                root.getChildren().remove(numberImages[i]);
                numberImages[i] = null;
            }
        }

        for(int i = 0; i < results.length; i++)
        {
            if(results[i] != null)
            {
                root.getChildren().remove(results[i]);
                results[i] = null;
            }
        }

        for(int i = 0; i < gameState.getNumResults(); i++)
        {
            ImageView numberImage = getNumImage(i);
            numberImages[i] = numberImage;

            Rectangle2D bounds = getResultBox(i);

            double boundsCenterX = (bounds.getMinX() + bounds.getMaxX()) / 2;
            double boundsCenterY = (bounds.getMinY() + bounds.getMaxY()) / 2;

            numberImage.setX(boundsCenterX - (numberImage.getBoundsInParent().getWidth() / 2));
            numberImage.setY(boundsCenterY - (numberImage.getBoundsInParent().getHeight() / 2));

            root.getChildren().add(numberImage);
        }
    }
}
