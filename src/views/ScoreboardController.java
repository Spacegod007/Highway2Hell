package views;

import bootstrapper.Main;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import logic.Score;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import java.util.ArrayList;

//todo move to more logical location, example "view"

public class ScoreboardController {

    private Main application;
    @FXML private TableColumn<Score, String> Player = new TableColumn<>("Player");
    @FXML private TableColumn<Score, Double> Score = new TableColumn<>("Score");
    @FXML private TableView<Score> TableView;

    @FXML
    public void initialize() {
        Player.setCellValueFactory(new PropertyValueFactory<>("name"));

        Score.setCellValueFactory(new PropertyValueFactory<>("score"));
     //   application.setScaling();
    }

    public void setApplication(Main application)
    {
        this.application = application;
    }

    public void setScore(ArrayList<Score> score) {
        ObservableList<logic.Score> scores = FXCollections.observableArrayList(score);
        this.TableView.setItems(scores);
    }

    public void backToLobby(ActionEvent actionEvent)
    {
        application.backToLobby();
    }
}
