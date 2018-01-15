package bootstrapper;

import javafx.animation.AnimationTimer;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.util.Duration;
import logic.Score;
import logic.administration.User;
import logic.fontyspublisher.IRemotePropertyListener;
import logic.fontyspublisher.IRemotePublisherForDomain;
import logic.fontyspublisher.IRemotePublisherForListener;
import logic.game.*;
import logic.remote_method_invocation.IGameAdmin;
import views.BackgroundController;
import views.ScoreboardController;

import javax.sound.midi.Soundbank;
import java.beans.PropertyChangeEvent;
import java.io.Serializable;
import java.rmi.RemoteException;
import java.util.*;

/**
 * //todo add java docs
 */
public class Main extends Application
{
    private List<ObstacleObject> obstacleObjects = new ArrayList<>();
    //Player images for creating characters for later versions that use sockets.
    private final Image playerImage = new Image("characters/character_black_blue.png");
    private final Image redBarrelImage = new Image("objects/barrel_red_down.png");
    private final Image blueBarrelImage = new Image("objects/barrel_blue_down.png");
    private final Image rockImage = new Image("objects/rock1.png");
    private final List<ImageView> playerImageViews = new ArrayList<>();
    private final List<ImageView> obstacleImageViews = new ArrayList<>();
    private final IGameAdmin game;
    private final Label distanceLabel = new Label("0");
    private List<Label> playerLabels = new ArrayList<>();
    private ObservableList<Label> observablePlayerLabels;
    private Map<String, ImageView> mappedPlayerImage = new HashMap<>();
    private Map<String, PlayerObject> mappedPlayerObject = new HashMap<>();
    private Map<String, Label> mappedPlayerLabel = new HashMap<>();
    private Map<Integer, ObstacleObject> mappedObstacleObject = new HashMap<>();
    private Map<Integer, ImageView> mappedObstacleImage = new HashMap<>();

    private Stage stage;

    //Fail safe for if someone decides to hold in one of the buttons.
    private boolean leftPressed = false;
    private boolean rightPressed = false;

    private int playersDead = 0;
    private int players = 0;

    // game scenes
    private Scene scene;
    private Scene scoreboardScene;
    private Scene countdownScene;

    private Pane gamePane;

    // for countdown
    private Integer seconds = 4;
    private final Label label = new Label();

    // controllers
    private ScoreboardController scoreboardController;
    private BackgroundController backgroundController;

    //david zn shit
    private PlayerObject thisPlayer = null;

    public Main(IGameAdmin game, IRemotePublisherForListener rpl)
    {
        this.game = game;
        try
        {
            SubMainRMI sub = new SubMainRMI(this, rpl);
        } catch (RemoteException e)
        {
            e.printStackTrace();
        }
    }

    public void start(Stage primaryStage, List<User> userList) throws Exception {
        // set primary stage size and name
        primaryStage.setTitle("Game");
        primaryStage.setWidth(1200);
        primaryStage.setHeight(1000);

        // background scroller scene
        FXMLLoader fxmlLoaderBackground = new FXMLLoader(getClass().getResource("/Background.fxml"));
        Parent parent1 = fxmlLoaderBackground.load();
        backgroundController = fxmlLoaderBackground.getController();
        scene = new Scene(parent1);
        gamePane = (Pane) parent1.lookup("#gamePane");

        // scoreboard scene
        FXMLLoader fxmlLoaderScoreBoard = new FXMLLoader(getClass().getResource("/views/Scoreboard.fxml"));
        Parent parent2 = fxmlLoaderScoreBoard.load();
        scoreboardController = fxmlLoaderScoreBoard.getController();
        scoreboardScene = new Scene(parent2);

        // countdown scene
        AnchorPane anchorPane = new AnchorPane();
        anchorPane.getChildren().add(label);
        countdownScene = new Scene(anchorPane);
        Font font = new Font("Comic Sans MS", 400);
        label.setTranslateX(310);
        label.setTranslateY(190);
        label.setFont(font);

        primaryStage.setScene(countdownScene);
        primaryStage.show();
        this.stage = primaryStage;
        doTime(primaryStage);
    }

    private void InitializeGame(Stage primaryStage) throws RemoteException
    {
        List<GameObject> gameObjects = game.getGameObjects();
        players = getPlayerObjects(gameObjects).size();

        for(PlayerObject player : getPlayerObjects(gameObjects))
        {
            ImageView img = addPlayerImageView();
            playerImageViews.add(img);
            mappedPlayerImage.put(player.getName(), img);
            mappedPlayerObject.put(player.getName(), player);
        }

        for (ObstacleObject OO : getObstacleObjects(gameObjects))
        {
            obstacleImageViews.add(addObstacleImageView(OO));
            mappedObstacleObject.put(OO.getId(), OO);
            mappedObstacleImage.put(OO.getId(), addObstacleImageView(OO));
        }

        gamePane.getChildren().addAll(playerImageViews);
        gamePane.getChildren().addAll(obstacleImageViews);

        scene.setOnKeyReleased(event ->
        {
            switch (event.getCode())
            {
                case LEFT:
                    leftPressed = false;
                    break;
                case RIGHT:
                    rightPressed = false;
                    break;
            }
        });

        scene.setOnKeyPressed(event ->
        {
            switch (event.getCode())
            {
                case LEFT:
                    movePlayer(thisPlayer, leftPressed, Direction.LEFT);
                    break;
                case RIGHT:
                    movePlayer(thisPlayer, rightPressed, Direction.RIGHT);
                    break;
            }
        });

        //region add labels
        // distance label for player score
        distanceLabel.setFont(new Font("Calibri", 22));
        distanceLabel.setTranslateX(6);
        distanceLabel.setTranslateY(3);
        gamePane.getChildren().add(distanceLabel);
        playerLabels = getPlayerLabels(gameObjects);
        gamePane.getChildren().addAll(FXCollections.observableArrayList(playerLabels));
        //endregion

        // start animation for background
        backgroundController.startAnimation();
        obstacleObjects = getObstacleObjects(gameObjects);
    }

    private PlayerObject getPlayer(PlayerObject po)
    {
        try
        {
            for(GameObject go : game.getGameObjects())
            {
                if(go instanceof PlayerObject)
                {
                    if(po.getName() == ((PlayerObject) go).getName())
                    {
                        return (PlayerObject) go;
                    }
                }
            }
            System.out.println("Player not found");
            return null;
        } catch (RemoteException e)
        {
            e.printStackTrace();
            return null;
        }
    }

    private PlayerObject movePlayer(PlayerObject player, Boolean pressed, Direction dir)
    {
        PlayerObject tempplayer = mappedPlayerObject.get(player.getName());
        if(!pressed)
        {
            try
            {
                ImageView playerView = mappedPlayerImage.get(player.getName());
                System.out.println("pv" + playerView);
                System.out.println("p" + tempplayer);
                System.out.println("rot" + tempplayer.getCurrentRotation());
                tempplayer = game.moveCharacter(tempplayer.getName(), dir);
                playerView.setRotate(tempplayer.getCurrentRotation());
                playerView.setX(tempplayer.getAnchor().getX());
                playerView.setY(tempplayer.getAnchor().getY());
                mappedPlayerImage.replace(player.getName(), playerView);
                mappedPlayerObject.replace(player.getName(), tempplayer);
                leftPressed = true;
            }
            catch (RemoteException e)
            {
                e.printStackTrace();
            }
        }
        return player;
    }

    private ImageView addPlayerImageView() {
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

    private ImageView addObstacleImageView(ObstacleObject OO) {
        ImageView imageView = new ImageView();
        System.out.println("Obstacle Added: " + OO.getType() + " " + OO.getWidth() + " " + OO.getHeight());
        imageView.setFitWidth(OO.getWidth());
        imageView.setFitHeight(OO.getHeight());
        switch(OO.getType())
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

    private List<Label> getPlayerLabels(List<GameObject> gameObjects) throws RemoteException
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
        if(time != null){
            time.stop();
        }
        KeyFrame frame = new KeyFrame(Duration.seconds(1),new EventHandler<ActionEvent>(){
            @Override
            public void handle(ActionEvent event){
                seconds --;
                label.setText(" " + seconds.toString());
                if(seconds <= 0){
                    primaryStage.setScene(scene);
                    try
                    {
                        System.out.println("Initialize game");
                        time.stop();
                        InitializeGame(primaryStage);
                        game.startGame();
                    }
                    catch (RemoteException e)
                    {
                        e.printStackTrace();
                    }
                }
            }
        });
        time.getKeyFrames().add(frame);
        time.playFromStart();
    }

    @Override
    public void start(Stage primaryStage) throws Exception
    {

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
     * This is called by the propertychange, and executes the update code
     * @param gameObjects are the new gameobjects, according to the server
     */
    public void update(List<GameObject> gameObjects)
    {

        System.out.println("updating");
        List<GameObject> tempGameObjects;
        try
        {
            tempGameObjects = game.getGameObjects();
        } catch (RemoteException e)
        {
            e.printStackTrace();
            return;
        }
        List<PlayerObject> playerObjects = getPlayerObjects(tempGameObjects);
        List<ObstacleObject> obstacleObjects = getObstacleObjects(tempGameObjects);

        System.out.println(playerObjects.size());
        for(PlayerObject po : playerObjects)
        {
            mappedPlayerObject.replace(po.getName(), po);
            System.out.println(po.getAnchor().getX());
            System.out.println(po.getAnchor().getY());
            PlayerObject tempPlayer = mappedPlayerObject.get(po.getName());
            System.out.println(tempPlayer.getAnchor().getX());
            System.out.println(tempPlayer.getAnchor().getY());
            Label tempLabel = mappedPlayerLabel.get(po.getName());
            tempLabel.setTranslateX(tempPlayer.getAnchor().getX());
            tempLabel.setTranslateY(tempPlayer.getAnchor().getX()-23);
            mappedPlayerLabel.replace(po.getName(), tempLabel);

            ImageView img = mappedPlayerImage.get(po.getName());
            img.setX(tempPlayer.getAnchor().getX());
            img.setY(tempPlayer.getAnchor().getY());

            if(tempPlayer.getIsDead())
            {
                playersDead++;
                if(playersDead == playerObjects.size())
                {
                    setScores();
                }
            }
            playersDead = 0;
        }
        for (ObstacleObject obstacleObject : obstacleObjects)
        {
            ObstacleObject tempObstacle = mappedObstacleObject.get(obstacleObject.getId());
            ImageView img = mappedObstacleImage.get(obstacleObject.getId());
            img.setX(tempObstacle.getAnchor().getX());
            img.setY(tempObstacle.getAnchor().getY());
        }
       // distanceLabel.setText("Distance: " + Long.toString(thisPlayer.getDistance()));

        // player name labels
        observablePlayerLabels = FXCollections.observableArrayList(setTempPlayerLabels());
    }

    private List<Label> setTempPlayerLabels()
    {
        ArrayList<Label> tempPlayerLabels = new ArrayList<>();
        try
        {
            for (PlayerObject player : game.returnPlayerObjects())
            {
                Label tempPlayerLabel = new Label(player.getName());
                tempPlayerLabel.setTranslateX(player.getAnchor().getX());
                tempPlayerLabel.setTranslateY(player.getAnchor().getY());
                tempPlayerLabels.add(tempPlayerLabel);
            }
        } catch (RemoteException e)
        {
            e.printStackTrace();
        }
        return tempPlayerLabels;
    }
    private void setScores()
    {
        ArrayList<Score> scores = new ArrayList<>();
        try
        {
            for (GameObject tempGO : game.getGameObjects()) {
                if (tempGO.getClass() == PlayerObject.class) {
                    PlayerObject PO = (PlayerObject) tempGO;
                    scores.add(new Score(PO.getName(), (double) PO.getDistance()));
                }
            }
        } catch (RemoteException e)
        {
            e.printStackTrace();
        }
        scoreboardController.setScore(scores);
        try
        {
            Platform.runLater(() -> stage.setScene(scoreboardScene));
            List<PlayerObject> players = game.endGame(); //(scoreboardScene, primaryStage) todo use returnvalue and show scoreboard scene
        } catch (RemoteException e)
        {
            e.printStackTrace();
        }
        scores.clear();
    }
}
