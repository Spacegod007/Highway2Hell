package bootstrapper;

import javafx.animation.AnimationTimer;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
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
import logic.game.*;
import logic.remote_method_invocation.IGameAdmin;
import views.BackgroundController;
import views.ScoreboardController;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

/**
 * //todo add java docs
 */
public class Main extends Application
{
    //private PlayerObject thisPlayer = new PlayerObject(new Point(960, 900), "Player1", Color.BLACK);
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
    private final List<Label> playerLabels = new ArrayList<>();
    private ObservableList<Label> observablePlayerLabels;

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

    public Main(IGameAdmin game)
    {
        this.game = game;
    }

    public void start(Stage primaryStage, List<User> userList) throws Exception {

        // set primary stage size and name
        primaryStage.setTitle("Game");
        primaryStage.setWidth(1200);
        primaryStage.setHeight(1000);

        //TODO set right player
        thisPlayer = new PlayerObject(new Point(960, 900),new Size(78, 54), userList.get(0).getUsername(), Color.BLACK);

        // build game
//        game = new Game(new ArrayList<>(), userList);


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
        doTime(primaryStage);
    }

    private void InitializeGame(Stage primaryStage) throws RemoteException
    {
        // player movement
        playerImageViews.add(addPlayerImageView());

        for (ObstacleObject OO : game.returnObstacleObjects())
        {
            obstacleObjects.add(OO);
            obstacleImageViews.add(addObstacleImageView(OO));
        }

        for (ImageView player : playerImageViews) {
            gamePane.getChildren().add(player);
        }

        for (ImageView obstacle : obstacleImageViews) {
            gamePane.getChildren().add(obstacle);
        }

        for (GameObject GO : game.getGameObjects()) {
            if (GO.getClass() == PlayerObject.class) {
                players++;
            }
        }

        scene.setOnKeyReleased(event -> {
            switch (event.getCode()) {
                case LEFT:
                    leftPressed = false;
                    break;
                case RIGHT:
                    rightPressed = false;
                    break;
            }
        });

        scene.setOnKeyPressed(event -> {
            switch (event.getCode()) {
                case LEFT:
                    if (!leftPressed) {
                        try
                        {
                            thisPlayer = game.moveCharacter(thisPlayer.getName(), Direction.LEFT);
                            playerImageViews.get(0).setRotate(thisPlayer.getCurrentRotation());
                            playerImageViews.get(0).setX(thisPlayer.getAnchor().getX());
                            playerImageViews.get(0).setY(thisPlayer.getAnchor().getY());
                            leftPressed = true;
                        }
                        catch (RemoteException e)
                        {
                            e.printStackTrace();
                        }

                    }
                    break;
                case RIGHT:
                    if (!rightPressed) {
                        try
                        {
                            thisPlayer = game.moveCharacter(thisPlayer.getName(), Direction.RIGHT);
                            playerImageViews.get(0).setRotate(thisPlayer.getCurrentRotation());
                            playerImageViews.get(0).setX(thisPlayer.getAnchor().getX());
                            playerImageViews.get(0).setY(thisPlayer.getAnchor().getY());
                            rightPressed = true;
                        }
                        catch (RemoteException e)
                        {
                            e.printStackTrace();
                        }
                    }
                    break;
            }
        });

        //Initialize first frame
        thisPlayer = game.moveCharacter(thisPlayer.getName(), Direction.RIGHT);

        // distance label for player score
        distanceLabel.setFont(new Font("Calibri", 22));
        distanceLabel.setTranslateX(6);
        distanceLabel.setTranslateY(3);
        gamePane.getChildren().add(distanceLabel);
        getPlayerLabels();
        observablePlayerLabels = FXCollections.observableArrayList(playerLabels);
        gamePane.getChildren().addAll(observablePlayerLabels);

        //Initiate timer for map scroll.
        AnimationTimer aTimer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                for (int i = 0; i < playerLabels.size(); i++) {
                    playerLabels.get(i).setTranslateX(thisPlayer.getAnchor().getX());
                    playerLabels.get(i).setTranslateY(thisPlayer.getAnchor().getY() - 23);
                }

//                game.update(); todo move to server-side
                for (ImageView PI : playerImageViews) {
                    PI.setX(thisPlayer.getAnchor().getX());
                    PI.setY(thisPlayer.getAnchor().getY());
                }

                try
                {
                    for (GameObject GO : game.getGameObjects()) {
                        //Check if the game is allowed to end.
                        if (GO.getClass() == PlayerObject.class) {
                            if (((PlayerObject) GO).getIsDead()) {
                                playersDead++;
                                if (playersDead == players) {
                                    ArrayList<Score> scores = new ArrayList<>();
                                    try
                                    {
                                        for (GameObject tempGO : game.getGameObjects()) {
                                            if (tempGO.getClass() == PlayerObject.class) {
                                                PlayerObject PO = (PlayerObject) GO;
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
                                        game.endGame(); //(scoreboardScene, primaryStage) todo use return value and show scoreboard scene
                                    } catch (RemoteException e)
                                    {
                                        e.printStackTrace();
                                    }
                                    scores.clear();
                                }
                            }
                        }
                    }
                } catch (RemoteException e)
                {
                    e.printStackTrace();
                }

                try
                {
                    obstacleObjects = game.returnObstacleObjects();
                } catch (RemoteException e)
                {
                    e.printStackTrace();
                }

                for (int i = 0; i < obstacleObjects.size(); i++) {
                    obstacleImageViews.get(i).setX(obstacleObjects.get(i).getAnchor().getX());
                    obstacleImageViews.get(i).setY(obstacleObjects.get(i).getAnchor().getY());
                }
                distanceLabel.setText("Distance: " + Long.toString(thisPlayer.getDistance()));

                // player name labels
                ArrayList<Label> tempPlayerLabels = new ArrayList<>();
                int index = 0;
                try
                {
                    for (PlayerObject player : game.returnPlayerObjects()) {
                        Label tempPlayerLabel = new Label(player.getName());
                        tempPlayerLabel.setTranslateX(player.getAnchor().getX());
                        tempPlayerLabel.setTranslateY(player.getAnchor().getY());
                        tempPlayerLabels.add(index, tempPlayerLabel);
                        index++;
                    }
                } catch (RemoteException e)
                {
                    e.printStackTrace();
                }
                observablePlayerLabels = FXCollections.observableArrayList(tempPlayerLabels);
            }
        };

        // start animation for background
        backgroundController.startAnimation();
        aTimer.start();
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
        switch (cc){
            case black_blue:
                return new Image("characters/character_black_blue.png");
            case black_green:
                return new Image("characters/character_black_green.png");
            case black_red:
                return new Image("characters/character_black_red.png");
            case black_white:
                return new Image("characters/character_black_white.png");
            case blonde_blue:
                return new Image("characters/character_blonde_blue.png");
            case blonde_green:
                return new Image("characters/character_blonde_green.png");
            case blonde_red:
                return new Image("characters/character_blonde_red.png");
            case blonde_white:
                return new Image("characters/character_blonde_white.png");
            case brown_blue:
                return new Image("characters/character_brown_blue.png");
            case brown_green:
                return new Image("characters/character_brown_green.png");
            case brown_red:
                return new Image("characters/character_brown_red.png");
            case brown_white:
                return new Image("characters/character_brown_white.png");
        }
        return image;
    }

    public static void main(String[] args) {
        //Repository repo = new Repository(new DatabaseContext());
        //System.out.println(Boolean.toString(repo.testConnection()));
        launch(args);
        //repo.closeConnection();
    }

    private void getPlayerLabels() throws RemoteException
    {
        int index = 0;
        for (PlayerObject player : game.returnPlayerObjects()) {
            Label tempPlayerLabel = new Label(player.getName());
            tempPlayerLabel.setFont(new Font("Calibri", 22));
            tempPlayerLabel.setTextFill(Color.WHITE);
            tempPlayerLabel.setTranslateX(player.getAnchor().getX());
            tempPlayerLabel.setTranslateY(player.getAnchor().getY());
            playerLabels.add(index, tempPlayerLabel);
            index++;
        }
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
                        InitializeGame(primaryStage);
                        game.startGame();
                    }
                    catch (RemoteException e)
                    {
                        e.printStackTrace();
                    }
                    time.stop();
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
}
