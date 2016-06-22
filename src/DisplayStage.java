import javafx.animation.*;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
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
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.File;

public class DisplayStage extends Stage implements ResultSubmittedListener, ResetListener, WrongAnswerListener
{
    private final double RESULT_WIDTH_RATIO = .75;

    private GameState gameState;
    private Group root;
    private Text[] results;
    private Text[] responses;
    private ImageView[] numberImages;
    private ImageView background;
    private boolean showingX;

    public DisplayStage(GameState gameState)
    {
        ObservableList<Screen> screens = Screen.getScreens();

        Rectangle2D bounds = screens.get(0).getVisualBounds();
        if(screens.size() > 1)
        {
            bounds = screens.get(1).getVisualBounds();
        }

        setX(bounds.getMinX());
        setY(bounds.getMinY());
        setWidth(bounds.getWidth());
        setHeight(bounds.getHeight());

        this.gameState = gameState;
        gameState.addResultSubmittedListener(this);
        gameState.addResetListener(this);
        gameState.addWrongAnswerListener(this);

        results = new Text[GameState.MAX_RESULTS];
        responses = new Text[GameState.MAX_RESULTS];
        numberImages = new ImageView[6];

        final int initWidth = 800;
        final int initHeight = 680;

        root = new Group();

        Scene scene = new Scene(root, initWidth, initHeight);
        scene.setCamera(new PerspectiveCamera());
        setScene(scene);

        background = new ImageView(
                new Image(getClass().getResourceAsStream("results.png"))
        );
        setImageviewBounds(background, new Rectangle2D(0, 0, getWidth(), getHeight()));
        root.getChildren().add(background);

        widthProperty().addListener((observable, oldValue, newValue) -> {
            OnResize();
        });

        heightProperty().addListener((observable, oldValue, newValue) -> {
            OnResize();
        });

        reset();
        show();
    }

    private void OnResize()
    {
        Rectangle2D rect = new Rectangle2D(0, 0, getWidth(), getHeight());

        setImageviewBounds(background, rect);
    }

    private Rectangle2D getResultBox(int index)
    {
        final double baseXRatio = (index < GameState.MAX_RESULTS_HALF) ? .17 : .505;
        final double baseYRatio = .28;
        final double widthRatio = .326;
        final double heightRatio = .15;

        final double deltaY = .16;

        double sceneWidth = getWidth();
        double sceneHeight = getHeight();

        int col = index / GameState.MAX_RESULTS_HALF;
        int row = index % GameState.MAX_RESULTS_HALF;

        double x = baseXRatio * sceneWidth;
        double y = (baseYRatio + row * deltaY) * sceneHeight;
        double width = widthRatio * sceneWidth;
        double height = heightRatio * sceneHeight;

        return new Rectangle2D(x, y, width, height);
    }


    private void flipBox(int index, String result, int numResponses)
    {
        try
        {
            Media sound = new Media(getClass().getResource("right_answer.wav").toURI().toString());
            MediaPlayer mediaPlayer = new MediaPlayer(sound);
            mediaPlayer.play();
        }
        catch (Exception e) {
            System.out.println("somethingelse");
        }


        if(results[index] != null)
        {
            root.getChildren().remove(results[index]);
        }
        if(numberImages[index] != null)
        {
            root.getChildren().remove(numberImages[index]);
        }
        if(responses[index] != null)
        {
            root.getChildren().remove(responses[index]);
        }

        ImageView box = new ImageView(
                new Image(getClass().getResourceAsStream("result_box.png"))
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

            Text response = new Text("" + numResponses);
            responses[index] = response;
            response.setFill(Color.WHITE);


            final double xOffsetRatio = 0.01;
            double xOffset = xOffsetRatio * getWidth();

            Bounds resultBounds = new BoundingBox(resultRect.getMinX() + xOffset, resultRect.getMinY(),
                    resultRect.getWidth() * RESULT_WIDTH_RATIO, resultRect.getHeight());


            fitTextInBounds(resultBounds, text);

            Bounds responseBounds = new BoundingBox(resultBounds.getMaxX(), resultRect.getMinY(),
                    resultRect.getWidth() * (1 - RESULT_WIDTH_RATIO), resultRect.getHeight());

            fitTextInBounds(responseBounds, response);

            root.getChildren().addAll(text, response);

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
                new Image(getClass().getResourceAsStream("num" + (index + 1) + ".png"))
        );
    }

    @Override
    public void resultSubmitted(SubmissionEvent e)
    {
        flipBox(e.index, gameState.getResult(e.index), gameState.getResponses(e.index));
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
            if(responses[i] != null)
            {
                root.getChildren().remove(responses[i]);
                responses[i] = null;
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

    @Override
    public void wrongAnswer(int index)
    {
        if(showingX)
            return;

        try
        {
            Media sound = new Media(getClass().getResource("buzzer.wav").toURI().toString());
            MediaPlayer mediaPlayer = new MediaPlayer(sound);
            mediaPlayer.play();
        }
        catch(Exception e)
        {
            System.out.println("something");
        }

        ImageView xImage = new ImageView(
            new Image("x" + index + ".png")
        );

        double centerX = getWidth() / 2;
        double centerY = getHeight() / 2;

        setImageviewBounds(xImage, new Rectangle2D(
                centerX - xImage.getBoundsInParent().getWidth() / 2,
                centerY - xImage.getBoundsInParent().getHeight() / 2,
                xImage.getBoundsInParent().getWidth(),
                xImage.getBoundsInParent().getHeight()));

        root.getChildren().add(xImage);
        showingX = true;

        Timeline xTimeline = new Timeline(new KeyFrame(Duration.seconds(1), new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                root.getChildren().remove(xImage);
                showingX = false;
            }
        }));

        xTimeline.play();
    }
}
