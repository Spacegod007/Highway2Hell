        package sample;

        import com.sun.deploy.util.SessionState;
        import database.Contexts.LocalContext;
        import database.Repositories.Repository;
        import javafx.application.Application;
        import javafx.application.Platform;
        import javafx.beans.value.ObservableValue;
        import javafx.collections.ObservableList;
        import javafx.event.ActionEvent;
        import javafx.event.EventHandler;
        import javafx.scene.Scene;
        import javafx.scene.control.Button;
        import javafx.scene.control.Label;
        import javafx.scene.control.ListView;
        import javafx.scene.control.TextField;
        import javafx.scene.image.Image;
        import javafx.scene.input.KeyCode;
        import javafx.scene.input.KeyEvent;
        import javafx.scene.input.MouseButton;
        import javafx.scene.input.MouseEvent;
        import javafx.scene.layout.*;
        import javafx.scene.media.Media;
        import javafx.scene.media.MediaPlayer;
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
        import javax.swing.text.html.ImageView;
        import java.util.List;

public class SampleMain extends Application {

    //region Form controls
    private Application game;
    private Stage stage;
    private Pane titleScreen;
    private Scene titleScene;
    private final ListView<Lobby> listvwLobby = new ListView<>();
    private final ListView<User> listvwPlayers = new ListView<>();
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
    private final ListView<User> listvwPlayersInLobby = new ListView<>();
    //endregion
    private static Administration administration;
    private final int minCharsName = 4;
    private final int minCharsLobbyName = 4;
    private InGameAdministration ingameAdministration;

    Clip clip;

    public static void launchView(String[] args, Administration admin) {
        administration = admin;
        System.out.println("launching");
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        try {
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
            String sSound = "asset\\sound\\Main_Theme.wav";
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(new File(sSound));
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

        listvwLobby.setLayoutX(19);
        listvwLobby.setLayoutY(132);
        listvwLobby.setPrefHeight(851);
        listvwLobby.setPrefWidth(306);
        listvwLobby.setOnMouseClicked(this::listvwLobbyClicked);

        listvwPlayers.setLayoutX(332);
        listvwPlayers.setLayoutY(132);
        listvwPlayers.setPrefWidth(306);
        listvwPlayers.setPrefHeight(851);

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

        lobbyScreen.getChildren().addAll(btnHostLobby, btnJoinLobby, txtLobbyName, listvwLobby, listvwPlayers, btnRefresh, lblLobbiesList, lblLobbyName, lblPlayerChooseGame, lblPlayersList);
        //endregion

        //region InLobbyScreen
        lblPlayersInLobby.setLayoutX(7);
        lblPlayersInLobby.setLayoutY(70);
        lblPlayersInLobby.setPrefHeight(45);
        lblPlayersInLobby.setPrefWidth(341);
        lblPlayersInLobby.setTextAlignment(TextAlignment.CENTER);
        lblPlayersInLobby.setText("Players:");
        lblPlayersInLobby.setStyle("-fx-font-size: 28");

        listvwPlayersInLobby.setLayoutX(14);
        listvwPlayersInLobby.setLayoutY(140);
        listvwPlayersInLobby.setPrefHeight(889);
        listvwPlayersInLobby.setPrefWidth(327);

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
        btnAccCharacter.setText("Accept character");

        btnLeaveLobby.setLayoutX(7);
        btnLeaveLobby.setLayoutY(10);
        btnLeaveLobby.setPrefWidth(150);
        btnLeaveLobby.setPrefHeight(30);
        btnLeaveLobby.setText("Leave lobby");
        btnLeaveLobby.setOnAction(event -> leaveLobby());

        javafx.scene.image.ImageView imageViewSelectedPlayer = new javafx.scene.image.ImageView();
        imageViewSelectedPlayer.setLayoutX(740);
        imageViewSelectedPlayer.setLayoutY(200);
        imageViewSelectedPlayer.setFitHeight(36);
        imageViewSelectedPlayer.setFitWidth(53);


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
            imageView.setOnMouseClicked(event -> imageViewSelectedPlayer.setImage(new Image(color.getPath())));
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

        inLobbyScreen.getChildren().addAll(imageViewSelectedPlayer, lblLobbyName, btnAccCharacter, lblPlayersInLobby, btnLeaveLobby, btnKickPlayer, btnStartGame, listvwPlayersInLobby);
        //endregion
    }

    private void listvwLobbyClicked(MouseEvent event)
    {
        if (event.getButton() == MouseButton.PRIMARY)
        {
            Lobby selectedItem = listvwLobby.getSelectionModel().getSelectedItem();

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

    private boolean contains() {
        for (User u : listvwPlayersInLobby.getItems()) {
            if (u.getID() == administration.getUser().getID()) {
                return true;
            }
        }
        return false;
    }

    private void checkForKicked() {
        if (stage.getScene() == inLobbyScene && !contains()) {
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
            lblPlayerChooseGame.setText(administration.getUser().getUsername() + " Choose a lobby!");
        }
    }

    private boolean validUsername(String username) {
        if ((username).trim().length() >= minCharsName) {
            return true;
        } else {
            System.out.println("Enter a name of at least " + minCharsName + " characters");
            return false;
        }
    }

    private void viewLobby(User player) {
        Lobby lobby = listvwLobby.getSelectionModel().getSelectedItem();
        if (lobby != null) {
            listvwPlayers.setItems(lobby.getPlayers());
        }
    }

    private void hostLobby() {
        if (!administration.inLobby()) {
            if ((txtLobbyName.getText()).trim().length() >= minCharsLobbyName) {
                listvwLobby.getSelectionModel().select(administration.hostLobby(txtLobbyName.getText()));
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
            Lobby lobby = listvwLobby.getSelectionModel().getSelectedItem();
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

        }
    }

    private void kickPlayer() {
        try {
            User player = listvwPlayersInLobby.getSelectionModel().getSelectedItem();
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

    public void setListvwLobby(ObservableList<Lobby> lobbies) {
        Lobby selectLobby = null;
        if (listvwLobby.getSelectionModel().getSelectedItem() != null) {
            int selectedId = listvwLobby.getSelectionModel().getSelectedItem().getId();

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
            listvwLobby.setItems(lobbies);
            if (finalSelectLobby != null) {
                listvwLobby.getSelectionModel().select(finalSelectLobby);
                listvwPlayers.setItems(finalSelectLobby.getPlayers());
                listvwPlayersInLobby.setItems(finalSelectLobby.getPlayers());
                checkForKicked();
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
                //TODO: stop the music
                clip.stop();
                System.out.println(((List<User>) obj).size());
                ((bootstrapper.Main) game).start(stage, (List<User>) obj);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}