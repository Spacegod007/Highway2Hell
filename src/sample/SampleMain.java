        package sample;

        import database.Contexts.LocalContext;
import database.Repositories.Repository;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import logic.administration.Administration;
        import logic.administration.InGameAdministration;
        import logic.administration.Lobby;
import logic.administration.User;
import logic.game.CharacterColor;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import java.io.File;
import java.util.List;

public class SampleMain extends Application {

    //region Form controls
    private Application game;
    private Stage stage;
    private Pane titleScreen;
    private Scene titleScene;
    private final ListView<Lobby> lvLobby = new ListView<>();
    private final ListView<User> lvPlayers = new ListView<>();
    private final Label lblEnterLobbyName = new Label();
    private final Label lblLobbiesList = new Label();
    private final Label lblPlayersList = new Label();
    private final Label lblPlayersInLobby = new Label();
    private final Label lblPlayerChooseGame = new Label();
    private final Label lblLobbyName = new Label();
    private final TextField txtLobbyName = new TextField();
    private final Button btnRefresh = new Button();
    private final Button btnHostLobby = new Button();
    private final Button btnJoinLobby = new Button();
    private final Button btnKickPlayer = new Button();
    private final Button btnStartGame = new Button();
    private final Button btnLeaveLobby = new Button();
    private final Button btnAccCharacter = new Button();
    private javafx.scene.image.ImageView imageViewSelectedPlayer = new javafx.scene.image.ImageView();
    private AnchorPane lobbyScreen;
    private Scene lobbyScene;
    private Label lblDoIKnowYou;
    private TextField txtEnterName = new TextField();
    private Button btnLaunchlobbyScreen = new Button();
    private final Label lblErrorMessage = new Label();
    private AnchorPane inLobbyScreen;
    private Scene waitingScene;
    private final Label waitingMessage = new Label();
    private AnchorPane waitingScreen;
    private Scene inLobbyScene;
    private final ListView<User> lvPlayersInLobby = new ListView<>();
    private CharacterColor currentColor = CharacterColor.black_blue;
    //endregion
    private static Administration administration;
    private final int MIN_CHARS_NAME = 4;
    private final int MIN_CHARS_LOBBY_NAME = 4;
    private InGameAdministration ingameAdministration;

    private Clip clip;
    private final static String S_SOUND = "asset\\sound\\Main_Theme.wav";
    public static void launchView(String[] args, Administration admin) {
        administration = admin;
        System.out.println("launching");
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        try(AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(new File(S_SOUND))) {
            Repository repo = new Repository(new LocalContext());
            System.out.println("Connection to database: " + Boolean.toString(repo.testConnection()));
            stage = primaryStage;

            //Initialize Screens
            titleScreen = new AnchorPane();
            lobbyScreen = new AnchorPane();
            inLobbyScreen = new AnchorPane();
            waitingScreen = new AnchorPane();
            //root = FXMLLoader.load(getClass().getResource("main.fxml"));
            //these things like 'root' must be come from a fxml file

            setUpControls();

            waitingScreen.getChildren().addAll(waitingMessage);


            titleScene = new Scene(titleScreen, 750, 750);
            lobbyScene = new Scene(lobbyScreen, 1200, 1000);
            inLobbyScene = new Scene(inLobbyScreen, 1200, 1000);
            waitingScene = new Scene(waitingScreen, 1200, 1000);

            primaryStage.setTitle("Highway to Hell");
            primaryStage.setScene(titleScene);
            primaryStage.show();

            //sound stuff
            clip = AudioSystem.getClip();
            clip.open(audioInputStream);
            clip.start();
            clip.loop(Clip.LOOP_CONTINUOUSLY);


            administration.setSampleMain(this);
        } catch (Exception e /*IOException*/) {
            e.printStackTrace();
        }
    }

    private void setUpControls() {
        //region Titlescreen
        stage.resizableProperty().setValue(false);
        titleScreen.setStyle("-fx-background-color:  #800000");

        txtEnterName = new TextField();
        txtEnterName.setPrefWidth(285);
        txtEnterName.setPrefHeight(51);
        txtEnterName.setLayoutX(315);
        txtEnterName.setLayoutY(205);
        txtEnterName.setOnKeyReleased(event -> {
            if (event.getCode().equals(KeyCode.ENTER) && !event.isShiftDown() && !event.isControlDown() && !event.isAltDown() && !event.isMetaDown() && !event.isShortcutDown())
            {
                launchlobbyScreen(txtEnterName.getText());
            }
        });

        btnLaunchlobbyScreen = new Button();
        btnLaunchlobbyScreen.setLayoutY(202);
        btnLaunchlobbyScreen.setLayoutX(131);
        btnLaunchlobbyScreen.setPrefHeight(51);
        btnLaunchlobbyScreen.setPrefWidth(135);
        btnLaunchlobbyScreen.setOnAction(event -> launchlobbyScreen(txtEnterName.getText()));
        btnLaunchlobbyScreen.setText("Yeah I'm:");

        lblDoIKnowYou = new Label();
        lblDoIKnowYou.setLayoutX(176);
        lblDoIKnowYou.setLayoutY(123);
        lblDoIKnowYou.setPrefWidth(500);
        lblDoIKnowYou.setPrefHeight(51);
        lblDoIKnowYou.setText("Do I Know You?");
        lblDoIKnowYou.setStyle("-fx-font-family: calibri");
        lblDoIKnowYou.setStyle("-fx-font-size: 48");
        lblDoIKnowYou.setTextFill(Color.WHITE);

        titleScreen.getChildren().addAll(txtEnterName, btnLaunchlobbyScreen, lblDoIKnowYou);

        //region lobbyScreen
        btnHostLobby.setLayoutX(718);
        btnHostLobby.setLayoutY(272);
        btnHostLobby.setPrefWidth(167);
        btnHostLobby.setPrefHeight(63);
        btnHostLobby.setText("Host lobby");
        btnHostLobby.setOnAction(event -> hostLobby());

        btnJoinLobby.setLayoutX(718);
        btnJoinLobby.setLayoutY(372);
        btnJoinLobby.setPrefWidth(163);
        btnJoinLobby.setPrefHeight(63);
        btnJoinLobby.setText("Join lobby");
        btnJoinLobby.setOnAction(event -> joinLobby());

        btnKickPlayer.setLayoutX(718);
        btnKickPlayer.setLayoutY(472);
        btnKickPlayer.setPrefWidth(163);
        btnKickPlayer.setPrefHeight(63);
        btnKickPlayer.setText("Kick player");
        btnKickPlayer.setOnAction(event -> kickPlayer());

        btnRefresh.setLayoutX(718);
        btnRefresh.setLayoutY(672);
        btnRefresh.setPrefHeight(63);
        btnRefresh.setPrefWidth(163);
        btnRefresh.setText("Refresh");

        lvLobby.setLayoutX(19);
        lvLobby.setLayoutY(132);
        lvLobby.setPrefHeight(851);
        lvLobby.setPrefWidth(306);
        lvLobby.setOnMouseClicked(this::lvLobbyClicked);

        lvPlayers.setLayoutX(332);
        lvPlayers.setLayoutY(132);
        lvPlayers.setPrefWidth(306);
        lvPlayers.setPrefHeight(851);

        txtLobbyName.setLayoutX(718);
        txtLobbyName.setLayoutY(205);
        txtLobbyName.setPrefHeight(51);
        txtLobbyName.setPrefWidth(412);
        txtLobbyName.setOnKeyReleased(event -> {
            if (event.getCode().equals(KeyCode.ENTER) && !event.isShiftDown() && !event.isControlDown() && !event.isAltDown() && !event.isMetaDown() && !event.isShortcutDown())
            {
                hostLobby();
            }
        });

        lblEnterLobbyName.setLayoutY(150);
        lblEnterLobbyName.setLayoutX(718);
        lblEnterLobbyName.setPrefHeight(31);
        lblEnterLobbyName.setPrefWidth(187);
        lblEnterLobbyName.setText("Enter lobby name: ");

        lblLobbiesList.setLayoutX(19);
        lblLobbiesList.setLayoutY(80);
        lblLobbiesList.setPrefWidth(306);
        lblLobbiesList.setPrefHeight(40);
        lblLobbiesList.setTextAlignment(TextAlignment.CENTER);
        lblLobbiesList.setText("Lobbies:");
        lblLobbiesList.setStyle("-fx-font-size: 18");

        lblPlayersList.setLayoutY(80);
        lblPlayersList.setLayoutX(332);
        lblPlayersList.setPrefHeight(40);
        lblPlayersList.setPrefWidth(306);
        lblPlayersList.setTextAlignment(TextAlignment.CENTER);
        lblPlayersList.setText("Players: ");
        lblPlayersList.setStyle("-fx-font-size: 18");

        lblPlayerChooseGame.setLayoutX(77);
        lblPlayerChooseGame.setLayoutY(20);
        lblPlayerChooseGame.setPrefWidth(1000);
        lblPlayerChooseGame.setPrefHeight(51);
        lblPlayerChooseGame.setTextAlignment(TextAlignment.CENTER);
        lblPlayerChooseGame.setStyle("-fx-font-size: 36");

        lobbyScreen.getChildren().addAll(btnHostLobby, btnJoinLobby, txtLobbyName, lvLobby, lvPlayers, btnRefresh, lblLobbiesList, lblLobbyName, lblPlayerChooseGame, lblPlayersList);
        //endregion

        //region InLobbyScreen
        lblPlayersInLobby.setLayoutX(7);
        lblPlayersInLobby.setLayoutY(70);
        lblPlayersInLobby.setPrefHeight(45);
        lblPlayersInLobby.setPrefWidth(341);
        lblPlayersInLobby.setTextAlignment(TextAlignment.CENTER);
        lblPlayersInLobby.setText("Players:");
        lblPlayersInLobby.setStyle("-fx-font-size: 28");

        lvPlayersInLobby.setLayoutX(14);
        lvPlayersInLobby.setLayoutY(140);
        lvPlayersInLobby.setPrefHeight(889);
        lvPlayersInLobby.setPrefWidth(327);

        lblLobbyName.setLayoutX(600);
        lblLobbyName.setLayoutY(17);
        lblLobbyName.setPrefWidth(246);
        lblLobbyName.setPrefHeight(58);
        lblLobbyName.setStyle("-fx-font-size: 36");

        btnKickPlayer.setLayoutX(501);
        btnKickPlayer.setLayoutY(560);
        btnKickPlayer.setPrefWidth(100);
        btnKickPlayer.setPrefHeight(30);
        btnKickPlayer.setText("Kick player");

        btnStartGame.setLayoutY(525);
        btnStartGame.setLayoutX(501);
        btnStartGame.setPrefHeight(30);
        btnStartGame.setPrefWidth(100);
        btnStartGame.setText("Start game");
        btnStartGame.setOnAction(event -> startGame());

        btnAccCharacter.setLayoutX(501);
        btnAccCharacter.setLayoutY(475);
        btnAccCharacter.setPrefWidth(132);
        btnAccCharacter.setPrefHeight(30);
        btnAccCharacter.setText("Set color");
        btnAccCharacter.setOnAction(event -> pickColor());

        btnLeaveLobby.setLayoutX(7);
        btnLeaveLobby.setLayoutY(10);
        btnLeaveLobby.setPrefWidth(150);
        btnLeaveLobby.setPrefHeight(30);
        btnLeaveLobby.setText("Leave lobby");
        btnLeaveLobby.setOnAction(event -> leaveLobby());

        javafx.scene.image.ImageView imageViewSelectedPlayer = new javafx.scene.image.ImageView();
        imageViewSelectedPlayer.setLayoutX(500);
        imageViewSelectedPlayer.setLayoutY(360);
        imageViewSelectedPlayer.setFitHeight(54);
        imageViewSelectedPlayer.setFitWidth(79);


        int x = 500;
        int y = 200;
        int counter = 0;

        for (CharacterColor color:CharacterColor.values())
        {
            counter++;
            javafx.scene.image.ImageView imageView = new javafx.scene.image.ImageView();
            imageView.setImage(new Image(color.getPath()));
            imageView.setLayoutY(y);
            imageView.setLayoutX(x);
            imageView.setFitWidth(53);
            imageView.setFitHeight(36);
            imageView.setStyle("-fx-background-color: black");
            imageView.setStyle("-fx-padding: 1px");
            imageView.setId(color.toString());
            imageView.setOnMouseClicked(event -> changePlayerImage(color));
            inLobbyScreen.getChildren().add(imageView);

            x = x + 53;

            if(counter >= 4){
                System.out.println(x);
                x = 500;
                y = y + 36;
                counter = 0;
            }

            System.out.println(imageView.getLayoutX() + ";" + imageView.getLayoutY());
        }
        inLobbyScreen.getChildren().addAll(imageViewSelectedPlayer, lblLobbyName, btnAccCharacter, lblPlayersInLobby, btnLeaveLobby, btnKickPlayer, btnStartGame, lvPlayersInLobby);
        //endregion
    }

    private void changePlayerImage(CharacterColor color)
    {
        currentColor = color;
        imageViewSelectedPlayer.setImage(new Image(color.getPath()));
    }

    private void pickColor()
    {
        System.out.println("Setting color: " + currentColor);
        administration.setUserColor(currentColor);
    }

    private void lvLobbyClicked(MouseEvent event)
    {
        if (event.getButton() == MouseButton.PRIMARY)
        {
            Lobby selectedItem = lvLobby.getSelectionModel().getSelectedItem();

            if (selectedItem != null)
            {
                if (event.getClickCount() == 2)
                {
                    joinLobby();
                }
                else
                {
                    viewLobby(null);
                }
            }
        }
    }

    private void checkIfInLobby()
    {
        if(stage.getScene() == inLobbyScene && !administration.userInLobby())
        {
            stage.setScene(lobbyScene);
        }
    }

    private void launchlobbyScreen(String username) {
        if (validUsername(username)) {
            if (administration.setUsername(username)) {
                stage.setTitle("Highway to Hell: " + username);
                stage.setScene(lobbyScene);
            } else {
                Platform.runLater(() ->
                {
                    txtEnterName.setText("");
                    lblErrorMessage.setText("Error: username was already taken");
                });
            }
            lblPlayerChooseGame.setText(administration.getUser().getUsername() + ", choose a lobby!");
        }
    }

    private boolean validUsername(String username) {
        if ((username).trim().length() >= MIN_CHARS_NAME) {
            return true;
        } else {
            System.out.println("Enter a name of at least " + MIN_CHARS_NAME + " characters");
            return false;
        }
    }

    private void viewLobby(User player) {
        Lobby lobby = lvLobby.getSelectionModel().getSelectedItem();
        if (lobby != null) {
            lvPlayers.setItems(lobby.getPlayers());
        }
    }

    private void hostLobby() {
        if (!administration.inLobby()) {
            if ((txtLobbyName.getText()).trim().length() >= MIN_CHARS_LOBBY_NAME) {
                lvLobby.getSelectionModel().select(administration.hostLobby(txtLobbyName.getText()));
                lblLobbyName.setText(txtLobbyName.getText());

                txtLobbyName.clear();
                stage.setScene(inLobbyScene);
            } else {
                System.out.println("Enter a name of at least 4 characters");
            }
        } else {
            System.out.println("Please leave your current lobby before hosting a new one");
        }
    }

    private void joinLobby() {
        try {
            Lobby lobby = lvLobby.getSelectionModel().getSelectedItem();
            if (lobby != null) {
                if (administration.inLobby()) {
                    administration.leaveLobby();
                }
                if (administration.joinLobby(lobby)) {
                    stage.setScene(inLobbyScene);
                    btnStartGame.setDisable(true);
                    lblLobbyName.setText(txtLobbyName.getText());
                } else {
                    //fail join lobby
                    System.out.println("Failed join");
                }
            } else {
                System.out.println("No lobby selected");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void leaveLobby() {
        try {
            administration.leaveLobby();
        } catch (Exception ignored) {
            ignored.printStackTrace();
        }
    }

    private void kickPlayer() {
        try {
            User player = lvPlayersInLobby.getSelectionModel().getSelectedItem();
            if (player != null) {
                int id = player.getID();
                if (id != administration.getUser().getID()) {
                    administration.leaveLobby(id);
                    viewLobby(null);
                } else {
                    System.out.println("Can't kick self");
                }
            } else {
                System.out.println("No player selected");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // TODO: 4-12-2017 implement starting the game
    private void startGame() {
        administration.startConnectingToGame();
    }

    public void setWaitingScreen() {
        if (stage.getScene() != waitingScene) {
            Platform.runLater(() -> stage.setScene(waitingScene));
        }
    }

    /**
     * @param x is the amount of players not yet connected
     */
    public void setWaitingPlayers(int x) {
        Platform.runLater(() -> waitingMessage.setText("Waiting for " + x + " players"));
    }

    public void setLvLobby(ObservableList<Lobby> lobbies) {
        Lobby selectLobby = null;
        if (lvLobby.getSelectionModel().getSelectedItem() != null) {
            int selectedId = lvLobby.getSelectionModel().getSelectedItem().getId();

            for (Lobby lobby : lobbies) {
                if (lobby.getId() == selectedId) {
                    selectLobby = lobby;
                    break;
                }
            }
        }
        Lobby finalSelectLobby = selectLobby;
        Platform.runLater(() ->
        {
            lvLobby.setItems(lobbies);
            if (finalSelectLobby != null) {
                lvLobby.getSelectionModel().select(finalSelectLobby);
                lvPlayers.setItems(finalSelectLobby.getPlayers());
                lvPlayersInLobby.setItems(finalSelectLobby.getPlayers());
                checkIfInLobby();
            }
        });
    }

    /**
     * Observer pattern update
     * Is called by RMIGameClient when the game starts
     * //TODO its a tryout to safely start the game
     *
     * @param obj the stage of the Game
     */
    public void update(Object obj) {
        Platform.runLater(() ->
        {
            game = new bootstrapper.Main(administration.getGameAdmin(), administration.getRpl(), administration.getUser());
            try {
                clip.stop();
                System.out.println(((List<User>) obj).size());
                ((bootstrapper.Main) game).start(stage, (List<User>) obj);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}