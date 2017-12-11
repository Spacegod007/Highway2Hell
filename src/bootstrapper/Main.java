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
import logic.Gamerule;
import logic.Score;
import logic.administration.User;
import logic.game.*;
import java.util.ArrayList;
import java.util.List;

public class Main extends Application {
    private PlayerObject PO1 = new PlayerObject(new Point(960, 900), "Player1", Color.BLACK);
    private List<ObstacleObject> obstacleObjects = new ArrayList<>();

    //Playerimages for creating characters for later versions that use sockets.
    private Image playerImage = new Image("characters/character_black_blue.png");
    private Image obstacleImage = new Image("objects/barrel_red_down.png");
    private List<ImageView> playerImageViews = new ArrayList<>();
    private List<ImageView> obstacleImageViews = new ArrayList<>();
    private Game game;
    private Label distanceLabel = new Label("0");
    private List<Label> playerLabels = new ArrayList<>();
    private ObservableList<Label> observablePlayerLabels;

    //Failsafe for if someone decides to hold in one of the buttons.
    private boolean leftPressed = false;
    private boolean rightPressed = false;

    private int playersDead = 0;
    private int players = 0;

    // game scenes
    private Scene scene;
    private Scene scoreboardScene;
    private Scene countdownScene;

    Pane gamePane;

    // for countdown
    private Integer seconds = 4;
    private Label label = new Label();

    // controllers
    ScoreboardController scoreboardController;
    BackgroundController backgroundController;

    public void start(Stage primaryStage, List<User> userList) throws Exception {

        // set primary stage size and name
        primaryStage.setTitle("Game");
        primaryStage.setWidth(1200);
        primaryStage.setHeight(1000);

        // build game
        game = new Game(new ArrayList<>(), new ArrayList<>());


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

        // primaryStage.setScene
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

    private void InitializeGame(Stage primaryStage) {
        // player movement
        playerImageViews.add(addPlayerImageView());

        obstacleImageViews.add(addObstacleImageView());
        obstacleImageViews.add(addObstacleImageView());
        obstacleImageViews.add(addObstacleImageView());
        obstacleImageViews.add(addObstacleImageView());
        obstacleImageViews.add(addObstacleImageView());
        obstacleImageViews.add(addObstacleImageView());
        obstacleImageViews.add(addObstacleImageView());
        obstacleImageViews.add(addObstacleImageView());

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
                        PO1 = game.moveCharacter("Player1", Direction.LEFT);
                        playerImageViews.get(0).setRotate(PO1.getCurrentRotation());
                        playerImageViews.get(0).setX(PO1.getAnchor().getX());
                        playerImageViews.get(0).setY(PO1.getAnchor().getY());
                        leftPressed = true;
                    }
                    break;
                case RIGHT:
                    if (!rightPressed) {
                        PO1 = game.moveCharacter("Player1", Direction.RIGHT);
                        playerImageViews.get(0).setRotate(PO1.getCurrentRotation());
                        playerImageViews.get(0).setX(PO1.getAnchor().getX());
                        playerImageViews.get(0).setY(PO1.getAnchor().getY());
                        rightPressed = true;
                    }
                    break;
            }
        });

        //Initialize first frame
        PO1 = game.moveCharacter("Player1", Direction.RIGHT);
        obstacleObjects.add(new ObstacleObject(70, 48));
        obstacleObjects.add(new ObstacleObject(70, 48));
        obstacleObjects.add(new ObstacleObject(70, 48));
        obstacleObjects.add(new ObstacleObject(70, 48));
        obstacleObjects.add(new ObstacleObject(70, 48));
        obstacleObjects.add(new ObstacleObject(70, 48));
        obstacleObjects.add(new ObstacleObject(70, 48));
        obstacleObjects.add(new ObstacleObject(70, 48));

        // distance labelfor player score
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
                    playerLabels.get(i).setTranslateX(PO1.getAnchor().getX());
                    playerLabels.get(i).setTranslateY(PO1.getAnchor().getY() - 23);
                }

                game.update();
                for (ImageView PI : playerImageViews) {
                    PI.setX(PO1.getAnchor().getX());
                    PI.setY(PO1.getAnchor().getY());
                }

                for (GameObject GO : game.getGameObjects()) {
                    //Check if the game is allowed to end.
                    if (GO.getClass() == PlayerObject.class) {
                        if (((PlayerObject) GO).getisDead()) {
                            playersDead++;
                            if (playersDead == players) {
                                ArrayList<Score> scores = new ArrayList<>();
                                for (GameObject tempGO : game.getGameObjects()) {
                                    if (tempGO.getClass() == PlayerObject.class) {
                                        PlayerObject PO = (PlayerObject) GO;
                                        scores.add(new Score(PO.getName(), (double) PO.getDistance()));
                                    }
                                }
                                scoreboardController.setScore(scores);
                                game.endGame(scoreboardScene, primaryStage);
                                scores.clear();
                            }
                        }
                    }
                }

                obstacleObjects = game.returnObstacleObjects();

                for (int i = 0; i < obstacleObjects.size(); i++) {
                    obstacleImageViews.get(i).setX(obstacleObjects.get(i).getAnchor().getX());
                    obstacleImageViews.get(i).setY(obstacleObjects.get(i).getAnchor().getY());
                }
                distanceLabel.setText("Distance: " + Long.toString(PO1.getDistance()));

                // player name labels
                ArrayList<Label> tempPlayerLabels = new ArrayList<>();
                int index = 0;
                for (PlayerObject player : game.returnPlayerObjects()) {
                    Label tempPlayerLabel = new Label(player.getName());
                    tempPlayerLabel.setTranslateX(player.getAnchor().getX());
                    tempPlayerLabel.setTranslateY(player.getAnchor().getY());
                    tempPlayerLabels.add(index, tempPlayerLabel);
                    index++;
                }
                observablePlayerLabels = FXCollections.observableArrayList(tempPlayerLabels);
            }
        };

        // start animation for background
        backgroundController.startAmination();
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

    private ImageView addObstacleImageView() {
        ImageView imageView = new ImageView();
        imageView.setImage(obstacleImage);
        imageView.setFitWidth(70);
        imageView.setFitHeight(48);
        imageView.setPreserveRatio(true);
        imageView.setSmooth(true);
        imageView.setCache(true);
        return imageView;
    }

    public static void main(String[] args) {
        //Repository repo = new Repository(new DatabaseContext());
        //System.out.println(Boolean.toString(repo.testConnection()));
        launch(args);
        //repo.closeConnection();
    }

    private void getPlayerLabels() {
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
                    InitializeGame(primaryStage);
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
