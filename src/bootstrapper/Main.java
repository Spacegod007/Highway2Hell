package bootstrapper;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.util.Duration;
import logic.Score;
import logic.administration.User;
import logic.fontyspublisher.IRemotePublisherForListener;
import logic.fontyspublisher.RemotePublisher;
import logic.game.*;
import logic.remote.method.invocation.IGameAdmin;
import sample.SampleMain;
import views.BackgroundController;
import views.ScoreboardController;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import java.io.File;
import java.rmi.RemoteException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
public class Main extends Application
{
    private static final Logger LOGGER = Logger.getLogger(Main.class.getName());

    private SampleMain application;
    private Scene previousScene;
    //Player images for creating characters for later versions that use sockets.
    private final Image redBarrelImage = new Image("objects/barrel_red_down.png");
    private final Image blueBarrelImage = new Image("objects/barrel_blue_down.png");
    private final Image rockImage = new Image("objects/rock1.png");
    private final IGameAdmin game;
    private final Label distanceLabel = new Label("0");
    private final Map<String, ImageView> mappedPlayerImage = new HashMap<>();
    private final Map<String, PlayerObject> mappedPlayerObject = new HashMap<>();
    private final Map<String, Label> mappedPlayerLabel = new HashMap<>();
    private final Map<Integer, ObstacleObject> mappedObstacleObject = new HashMap<>();
    private final Map<Integer, ImageView> mappedObstacleImage = new HashMap<>();

    private Stage stage;

    //Fail safe for if someone decides to hold in one of the buttons.
    private boolean leftPressed = false;
    private boolean rightPressed = false;

    // game scenes
    private Scene scene;
    private Scene scoreboardScene;

    private Pane gamePane;

    // for countdown
    private Integer seconds = 4;
    private final Label label = new Label();

    // controllers
    private ScoreboardController scoreboardController;
    private BackgroundController backgroundController;

    //david zn shit
    private String playerKey = null;

    private Clip clip;

    public Main(IGameAdmin game, IRemotePublisherForListener rpl, User user)
    {
        this.playerKey = user.getUsername();
        LOGGER.log(Level.INFO, playerKey);
        this.game = game;
        try
        {
            new SubMainRMI(this, rpl);
        }
        catch (RemoteException e)
        {
            LOGGER.log(Level.SEVERE, RemotePublisher.ERROR_MESSAGE, e);
        }
    }

    public void start(Stage primaryStage, List<User> userList, Scene scene, SampleMain application) throws Exception {
        this.application = application;
        previousScene = scene;
        // set primary stage size and name
        primaryStage.setTitle("Game");
        primaryStage.setWidth(1200);
        primaryStage.setHeight(1000);

        // background scroller scene
        FXMLLoader fxmlLoaderBackground = new FXMLLoader(getClass().getResource("/Background.fxml"));
        Parent parent1 = fxmlLoaderBackground.load();
        backgroundController = fxmlLoaderBackground.getController();
        this.scene = new Scene(parent1);
        gamePane = (Pane) parent1.lookup("#gamePane");

        // scoreboard scene
        FXMLLoader fxmlLoaderScoreBoard = new FXMLLoader(getClass().getResource("/views/Scoreboard.fxml"));
        Parent parent2 = fxmlLoaderScoreBoard.load();
        scoreboardController = fxmlLoaderScoreBoard.getController();
        scoreboardScene = new Scene(parent2);

        // countdown scene
        AnchorPane anchorPane = new AnchorPane();
        anchorPane.getChildren().add(label);
        Scene countdownScene = new Scene(anchorPane);
        Font font = new Font("Comic Sans MS", 400);
        label.setTranslateX(310);
        label.setTranslateY(190);
        label.setFont(font);

        primaryStage.setScene(countdownScene);
        primaryStage.show();
        this.stage = primaryStage;
        scoreboardController.setApplication(this);
        doTime(primaryStage);

        String sSound = "asset\\sound\\Game_theme.wav";
        try (AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(new File(sSound)))
        {
            clip = AudioSystem.getClip();
            clip.open(audioInputStream);
        }
    }

    private void initializeGame() throws RemoteException
    {
        List<GameObject> gameObjects = game.getGameObjects();
        for(PlayerObject player : getPlayerObjects(gameObjects))
        {
            String playerColorMessage = player.getColor().toString();
            LOGGER.log(Level.INFO, playerColorMessage);
            ImageView img = addPlayerImageView(new Image(player.getColor().getPath()));
            mappedPlayerImage.put(player.getName(), img);
            mappedPlayerObject.put(player.getName(), player);
        }

        for (ObstacleObject OO : getObstacleObjects(gameObjects))
        {
            ImageView img = addObstacleImageView(OO);
            mappedObstacleObject.put(OO.getId(), OO);
            mappedObstacleImage.put(OO.getId(), img);
        }

        mappedPlayerImage.forEach((k, v) -> gamePane.getChildren().add(v));
        mappedObstacleImage.forEach((k, v) -> gamePane.getChildren().add(v));

        scene.setOnKeyReleased(event ->
        {
            if (event.getCode() == KeyCode.LEFT)
            {
                leftPressed = false;
            }
            else if (event.getCode() == KeyCode.RIGHT)
            {
                rightPressed = false;
            }
        });

        scene.setOnKeyPressed(event ->
        {
            if (event.getCode() == KeyCode.LEFT)
            {
                movePlayer(leftPressed, Direction.LEFT);
                leftPressed = true;
            }
            else if (event.getCode() == KeyCode.RIGHT)
            {
                movePlayer(rightPressed, Direction.RIGHT);
                rightPressed = true;
            }
        });

        //region add labels
        // distance label for player score
        distanceLabel.setFont(new Font("Calibri", 22));
        distanceLabel.setTranslateX(6);
        distanceLabel.setTranslateY(3);
        gamePane.getChildren().add(distanceLabel);
        List<Label> playerLabels = getPlayerLabels(gameObjects);
        gamePane.getChildren().addAll(FXCollections.observableArrayList(playerLabels));
        //endregion

        // start animation for background
        backgroundController.startAnimation();
        getObstacleObjects(gameObjects);
    }

    private void movePlayer(Boolean pressed, Direction dir)
    {
        if(!pressed)
        {
            try
            {
                mappedPlayerObject.replace(playerKey, game.moveCharacter(playerKey, dir));
                PlayerObject tempPlayer = mappedPlayerObject.get(playerKey);
                ImageView playerView = mappedPlayerImage.get(playerKey);
                playerView.setRotate(tempPlayer.getCurrentRotation());
                playerView.setX(tempPlayer.getAnchor().getX());
                playerView.setY(tempPlayer.getAnchor().getY());
                mappedPlayerImage.replace(playerKey, playerView);
            }
            catch (RemoteException e)
            {
                LOGGER.log(Level.SEVERE, RemotePublisher.ERROR_MESSAGE, e);
            }
        }
    }

    private ImageView addPlayerImageView(Image playerImage) {
        ImageView imageView = new ImageView();
        imageView.setImage(playerImage);
        imageView.setFitWidth(78);
        imageView.setFitHeight(54);
        imageView.setPreserveRatio(true);
        imageView.setSmooth(true);
        imageView.setCache(true);
        imageView.setRotate(180d);
        return imageView;
    }

    private ImageView addObstacleImageView(ObstacleObject obstacleObject) {
        ImageView imageView = new ImageView();
        String obstacleAddedMessage = String.format("Obstacle added: %s %s %s", obstacleObject.getType(), obstacleObject.getWidth(), obstacleObject.getHeight());
        LOGGER.log(Level.INFO, obstacleAddedMessage);
        imageView.setFitWidth(obstacleObject.getWidth());
        imageView.setFitHeight(obstacleObject.getHeight());
        switch(obstacleObject.getType())
        {
            case RED_BARREL:
                imageView.setImage(redBarrelImage);
                break;
            case BLUE_BARREL:
                imageView.setImage(blueBarrelImage);
                break;
            case ROCK:
                imageView.setImage(rockImage);
                break;
            default:
                break;
        }

        imageView.setPreserveRatio(true);
        imageView.setSmooth(true);
        imageView.setCache(true);
        return imageView;
    }

    public Image ChangePlayerImage(Image image, CharacterColor cc)
    {
        return new Image("characters/character_" + cc.name() + ".png");
    }

    public static void main(String[] args) {
        launch(args);
    }

    private List<Label> getPlayerLabels(List<GameObject> gameObjects)
    {
        List<Label> list = new ArrayList<>();
        for (PlayerObject player : getPlayerObjects(gameObjects)) {
            Label tempPlayerLabel = new Label(player.getName());
            tempPlayerLabel.setFont(new Font("Calibri", 22));
            tempPlayerLabel.setTextFill(Color.WHITE);
            tempPlayerLabel.setTranslateX(player.getAnchor().getX());
            tempPlayerLabel.setTranslateY(player.getAnchor().getY());
            list.add(tempPlayerLabel);
            mappedPlayerLabel.put(player.getName(), tempPlayerLabel);
        }
        return list;
    }

    private void doTime(Stage primaryStage) {
        Timeline time = new Timeline();
        time.setCycleCount(Timeline.INDEFINITE);
        time.stop();
        KeyFrame frame = new KeyFrame(Duration.seconds(1), event -> {
            seconds --;
            label.setText(" " + seconds.toString());
            if(seconds <= 0){
                primaryStage.setScene(scene);
                try
                {
                    LOGGER.log(Level.INFO, "initialize game");
                    time.stop();
                    initializeGame();
                    game.startGame();
                    clip.start();
                    clip.loop(Clip.LOOP_CONTINUOUSLY);
                }
                catch (RemoteException e)
                {
                    LOGGER.log(Level.SEVERE, RemotePublisher.ERROR_MESSAGE, e);
                }
            }
        });
        time.getKeyFrames().add(frame);
        time.playFromStart();
    }

    @Override
    public void start(Stage primaryStage) throws Exception
    {
        //method is empty and i don't know why
    }

    private List<PlayerObject> getPlayerObjects(List<GameObject> gameObjects)
    {
        List<PlayerObject> playerObjects = new ArrayList<>();
        for(GameObject go : gameObjects)
        {
            if(go instanceof PlayerObject)
            {
                playerObjects.add((PlayerObject)go);
            }
        }
        return playerObjects;
    }

    private List<ObstacleObject> getObstacleObjects(List<GameObject> gameObjects)
    {
        List<ObstacleObject> obstacleObjects = new ArrayList<>();
        for(GameObject go : gameObjects)
        {
            if(go instanceof ObstacleObject)
            {
                obstacleObjects.add((ObstacleObject)go);
            }
        }
        return obstacleObjects;
    }

    /**
     * This is called by the property change, and executes the update code
     * @param gameObjects are the new gameobjects, according to the server
     */
    public void update(List<GameObject> gameObjects)
    {
        List<GameObject> tempGameObjects;
        try
        {
            tempGameObjects = game.getGameObjects();
        } catch (RemoteException e)
        {
            LOGGER.log(Level.SEVERE, RemotePublisher.ERROR_MESSAGE, e);
            return;
        }
        List<PlayerObject> playerObjects = getPlayerObjects(tempGameObjects);
        List<ObstacleObject> obstacleObjects = getObstacleObjects(tempGameObjects);
        for(PlayerObject po : playerObjects)
        {
            mappedPlayerObject.replace(po.getName(), po);
            PlayerObject tempPlayer = mappedPlayerObject.get(po.getName());
            Label tempLabel = mappedPlayerLabel.get(po.getName());
            tempLabel.setTranslateX(tempPlayer.getAnchor().getX());
            tempLabel.setTranslateY(tempPlayer.getAnchor().getY()-23);
            mappedPlayerLabel.replace(po.getName(), tempLabel);

            ImageView img = mappedPlayerImage.get(po.getName());
            img.setRotate(po.getCurrentRotation());
            img.setX(tempPlayer.getAnchor().getX());
            img.setY(tempPlayer.getAnchor().getY());
        }
        for (ObstacleObject obstacleObject : obstacleObjects)
        {
            mappedObstacleObject.replace(obstacleObject.getId(), obstacleObject);
            ObstacleObject tempObstacle = mappedObstacleObject.get(obstacleObject.getId());
            ImageView img = mappedObstacleImage.get(obstacleObject.getId());
            img.setX(tempObstacle.getAnchor().getX());
            img.setY(tempObstacle.getAnchor().getY());
        }
       distanceLabel.setText("Distance: " + Long.toString(mappedPlayerObject.get(playerKey).getDistance()));
    }

    /**
     * Called when all players are dead
     */
    public void setScores()
    {
        ArrayList<Score> scores = new ArrayList<>();
        try
        {
            for (GameObject tempGO : game.getGameObjects()) {
                if (tempGO.getClass() == PlayerObject.class) {
                    PlayerObject playerObject = (PlayerObject) tempGO;
                    scores.add(new Score(playerObject.getName(), (double) playerObject.getDistance()));
                }
            }
        } catch (RemoteException e)
        {
            LOGGER.log(Level.SEVERE, RemotePublisher.ERROR_MESSAGE, e);
        }
        scoreboardController.setScore(scores);
        try
        {
            clip.stop();
            Platform.runLater(() -> stage.setScene(scoreboardScene));
            game.endGame();
        } catch (RemoteException e)
        {
            LOGGER.log(Level.SEVERE, RemotePublisher.ERROR_MESSAGE, e);
        }
        scores.clear();
    }

    public void backToLobby()
    {
        application.endGame();
        stage.setScene(previousScene);
    }
}
