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
import javafx.scene.layout.FlowPane;
import javafx.stage.Stage;
import logic.administration.Administration;
import logic.administration.Lobby;
import logic.administration.User;

public class Main extends Application{

    //region Form controls
    Application game;
    private Stage stage;
    private FlowPane titleScreen;
    private Scene titleScene;
        private ListView<Lobby> listvwLobby = new ListView<>();
        private ListView<User> listvwPlayers = new ListView<>();
        private TextField text = new TextField();
        private Button btnRefresh = new Button();
        private Button btnHostLobby = new Button();
        private Button btnJoinLobby = new Button();
        private Button btnKickPlayer = new Button();
        private Button btnStartGame = new Button();
        private Button btnLeaveLobby = new Button();
    private FlowPane lobbyScreen;
    private Scene lobbyScene;
        private TextField txtEnterName = new TextField();
        private Button btnLaunchlobbyScreen = new Button();
    private FlowPane inLobbyScreen;
    private Scene waitingScene;
    private Label waitingMessage = new Label();
    private FlowPane waitingScreen;
    private Scene inLobbyScene;
        private ListView<User> listvwPlayersInLobby = new ListView<>();
    //endregion
    private static Administration administration;
    private final int minCharsName = 4;
    private final int minCharsLobbyName = 4;

    public static void launchView(String[] args, Administration admin)
    {
        administration = admin;
        System.out.println("launching");
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception{
        try
        {
            Repository repo = new Repository(new LocalContext());
            System.out.println("Connection to database: " + Boolean.toString(repo.testConnection()));
            stage = primaryStage;
            setUpControls();
            titleScreen = new FlowPane();
            lobbyScreen = new FlowPane();
            inLobbyScreen = new FlowPane();
            waitingScreen = new FlowPane();
            //root = FXMLLoader.load(getClass().getResource("main.fxml"));
            //deze dingen moeten zoals 'root' allebei uit een fxml komen

            titleScreen.getChildren().addAll(txtEnterName, btnLaunchlobbyScreen);
            lobbyScreen.getChildren().addAll(btnHostLobby, btnJoinLobby, text, listvwLobby, listvwPlayers, btnRefresh);
            inLobbyScreen.getChildren().addAll(btnLeaveLobby, btnKickPlayer,  btnStartGame, listvwPlayersInLobby);
            waitingScreen.getChildren().addAll(waitingMessage);

            titleScene = new Scene(titleScreen, 700, 600);
            lobbyScene = new Scene(lobbyScreen, 700, 600);
            inLobbyScene = new Scene(inLobbyScreen, 700, 600);
            waitingScene = new Scene(waitingScreen, 700,600);

            primaryStage.setTitle("Highway to Hell");
            primaryStage.setScene(titleScene);
            primaryStage.show();
            administration.setMain(this);
        }
        catch(Exception e /*IOException*/)
        {
            e.printStackTrace();
        }
    }

    private void setUpControls()
    {
        //region lobbyScreen
        btnHostLobby.setLayoutX(150);
        btnHostLobby.setLayoutY(0);
        btnHostLobby.setPrefWidth(150);
        btnHostLobby.setText("Host lobby");
        btnHostLobby.setOnAction(event -> hostLobby());

        btnJoinLobby.setLayoutX(300);
        btnJoinLobby.setLayoutY(0);
        btnJoinLobby.setPrefWidth(150);
        btnJoinLobby.setText("Join lobby");
        btnJoinLobby.setOnAction(event -> joinLobby());

        btnKickPlayer.setLayoutX(0);
        btnKickPlayer.setLayoutY(550);
        btnKickPlayer.setPrefWidth(100);
        btnKickPlayer.setText("Kick player");
        btnKickPlayer.setOnAction(event -> kickPlayer());

        btnStartGame.setLayoutX(100);
        btnStartGame.setLayoutY(550);
        btnStartGame.setPrefWidth(100);
        btnStartGame.setText("Start game");
        btnStartGame.setOnAction(event -> startGame());

        btnRefresh.setLayoutX(0);
        btnRefresh.setLayoutY(50);
        btnRefresh.setText("Refresh");
        btnRefresh.setPrefWidth(100);

        listvwLobby.setLayoutX(50);
        listvwLobby.setLayoutY(150);
        listvwLobby.getSelectionModel().selectedItemProperty().addListener(event -> viewLobby(null));

        listvwPlayers.setLayoutX(300);
        listvwPlayers.setLayoutY(150);

        text.setLayoutX(0);
        text.setLayoutY(0);
        //endregion
        //region TitleScreen
        btnLaunchlobbyScreen.setOnAction(event -> launchlobbyScreen(txtEnterName.getText()));
        //endregion
        //region InLobbyScreen
        btnLeaveLobby.setLayoutX(450);
        btnLeaveLobby.setLayoutY(0);
        btnLeaveLobby.setPrefWidth(150);
        btnLeaveLobby.setText("Leave lobby");
        btnLeaveLobby.setOnAction(event -> leaveLobby());
        //endregion
    }

    private boolean contains()
    {
        for(User u : listvwPlayersInLobby.getItems())
        {
            if (u.getID() == administration.getUser().getID())
            {
                return true;
            }
        }
        return false;
    }
    private void checkForKicked()
    {
        if(stage.getScene() == inLobbyScene && !contains())
        {
            stage.setScene(lobbyScene);
            System.out.println("left like this");
        }

    }

    private void launchlobbyScreen(String username)
    {
        if(validUsername(username))
        {
            administration.setUsername(username);
            stage.setTitle("Highway to Hell: " + username );
            stage.setScene(lobbyScene);
        }
    }

    private boolean validUsername(String username)
    {
        if((username).trim().length()>=minCharsName)
        {
            return true;
        }
        else
        {
            System.out.println("Enter a name of at least " +  minCharsName + " characters");
            return false;
        }
    }

    private void viewLobby(User player){
        Lobby lobby = listvwLobby.getSelectionModel().getSelectedItem();
        if(lobby != null){
            listvwPlayers.setItems(lobby.getPlayers());
            if(player != null)
            {
                for(User p : listvwPlayers.getItems())
                {
                    if(p.getID() == player.getID()){
                        listvwPlayers.getSelectionModel().select(p);
                    }
                }
            }
        }
    }

    private void hostLobby(){
        if(!administration.inLobby())
        {
            if((text.getText()).trim().length()>=minCharsLobbyName)
            {
                listvwLobby.getSelectionModel().select(administration.hostLobby(text.getText()));
                text.clear();
                stage.setScene(inLobbyScene);
            }
            else
            {
                System.out.println("Enter a name of at least 4 characters");
            }
        }
        else
        {
            System.out.println("Please leave your current lobby before hosting a new one");
        }
    }

    private void joinLobby()
    {
        try
        {
            Lobby lobby = listvwLobby.getSelectionModel().getSelectedItem();
            if(lobby != null)
            {
                if(administration.inLobby())
                {
                    administration.leaveLobby();
                }
                if(administration.joinLobby(lobby))
                {
                    stage.setScene(inLobbyScene);
                    btnStartGame.setDisable(true);
                }
                else
                {
                    //fail join lobby
                    System.out.println("Failed join");
                }
            }
            else
            {
                System.out.println("No lobby selected");
            }
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }

    private void leaveLobby()
    {
        try
        {
            administration.leaveLobby();
        }
        catch(Exception ignored)
        {

        }
    }

    private void kickPlayer()
    {
        try
        {
            User player = listvwPlayers.getSelectionModel().getSelectedItem();
            if(player != null)
            {
                int id = listvwPlayers.getSelectionModel().getSelectedItem().getID();
                if(id != administration.getUser().getID())
                {
                    administration.leaveLobby(id);
                    viewLobby(null); //hier nog naar kijken
                }
                else
                {
                    System.out.println("Can't kick self");
                }
            }
            else
            {
                System.out.println("No player selected");
            }
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }

    // TODO: 4-12-2017 implement starting the game
    private void startGame(){
        administration.startConnectingToGame();
    }

    public void setWaitingScreen()
    {
        if(stage.getScene() != waitingScene)
        {
            Platform.runLater(() ->
            {
                stage.setScene(waitingScene);
            });
        }
    }

    /**
     *
     * @param x is the amount of players not yet connected
     */
    public void setWaitingPlayers(int x)
    {
        Platform.runLater(()->
        {
            waitingMessage.setText("Waiting for " + x + " players");
        });
    }

    public void setListvwLobby(ObservableList<Lobby> lobbies)
    {
        Lobby selectLobby = null;
        if(listvwLobby.getSelectionModel().getSelectedItem() != null)
        {
            int selectedId = listvwLobby.getSelectionModel().getSelectedItem().getId();

            for (Lobby lobby : lobbies)
            {
                if (lobby.getId() == selectedId)
                {
                    selectLobby = lobby;
                    break;
                }
            }
        }
        Lobby finalSelectLobby = selectLobby;
        Platform.runLater(() ->
        {
            listvwLobby.setItems(lobbies);
            if(finalSelectLobby != null)
            {
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
     * @param obj the stage of the Game
     */
    public void update(Object obj)
    {
        System.out.println("updated");
        Platform.runLater(() ->
        {
            game = new bootstrapper.Main();
            try
            {
                game.start(new Stage());
            } catch (Exception e)
            {
                e.printStackTrace();
            }
        });
    }
}
