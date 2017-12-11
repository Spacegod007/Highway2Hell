package bootstrapper;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import logic.Score;
import logic.game.*;

import java.util.ArrayList;
import java.util.List;


public class Main extends Application {

    private PlayerObject PO1 = new PlayerObject(new Point(960, 900),"Player1", Color.BLACK);
    private List<ObstacleObject> obstacleObjects = new ArrayList<>();

    //Playerimages for creating characters for later versions that use sockets.
    private Image playerImage = new Image("characters/character_black_blue.png");

    private Image obstacleImage = new Image("objects/barrel_red_down.png");
    private List<ImageView> playerImageViews = new ArrayList<>();
    private List<ImageView> obstacleImageViews = new ArrayList<>();
    private Game game = new Game(new ArrayList<>(), new ArrayList<>());
    private Scene scoreboardScene;
    private Label distanceLabel = new Label("0");
    private List<Label> playerLabels = new ArrayList<>();
    private ObservableList<Label> observablePlayerLabels;

    //Failsafe for if someone decides to hold in one of the buttons.
    private boolean leftPressed = false;
    private boolean rightPressed = false;

    private int playersDead = 0;
    private int players = 0;

    Scene scene;
    Pane gamePane;
    static Thread thread = new Thread();
    Label label = new Label();
    scoreboardController sbc;

    @Override
    public void start(Stage primaryStage) throws Exception{

        // background scroller
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/Background.fxml") );
        Parent p = fxmlLoader.load();
        BackgroundController c = fxmlLoader.getController();
        scene = new Scene(p);

        fxmlLoader = new FXMLLoader(getClass().getResource("/views/Scoreboard.fxml"));
        Parent root = fxmlLoader.load();
        sbc = fxmlLoader.getController();
        scoreboardScene = new Scene(root);

        primaryStage.setTitle( "Game" );
        primaryStage.setOnShown( (evt) -> c.startAmination() );
        gamePane = (Pane) p.lookup("#gamePane");

        primaryStage.setWidth(1200);
        primaryStage.setHeight(1000);
        primaryStage.setScene(scene);
        primaryStage.show();

        for(int count = 3; count >= 0; count --) {
            thread.sleep(1000);
            System.out.println(count);
            if (count == 0) {
                InitializeGame(primaryStage);
            }
        }
    }

    private void InitializeGame(Stage primaryStage){
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

        for(ImageView player : playerImageViews)
        {
            gamePane.getChildren().add(player);
        }

        for(ImageView obstacle : obstacleImageViews)
        {
            gamePane.getChildren().add(obstacle);
        }

        for(GameObject GO : game.getGameObjects())
        {
            if(GO.getClass() == PlayerObject.class)
            {
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
                    if(!leftPressed)
                    {
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

                //System.out.println(playerLabels.get(0).getTranslateX() + " " + playerLabels.get(0).getTranslateY());

                game.update();
                for(ImageView PI : playerImageViews)
                {
                    PI.setX(PO1.getAnchor().getX());
                    PI.setY(PO1.getAnchor().getY());
                }

                for (GameObject GO : game.getGameObjects()) {
                    //Check if the game is allowed to end.
                    if(GO.getClass() == PlayerObject.class)
                    {
                        if(((PlayerObject) GO).getisDead())
                        {
                            playersDead++;
                            if(playersDead == players)
                            {
                                ArrayList<Score> scores = new ArrayList<>();
                                for(GameObject tempGO : game.getGameObjects())
                                {
                                    if(tempGO.getClass() == PlayerObject.class)
                                    {
                                        PlayerObject PO = (PlayerObject)GO;
                                        scores.add(new Score(PO.getName(), (double)PO.getDistance()));
                                    }
                                }
                                sbc.setScore(scores);
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
}
