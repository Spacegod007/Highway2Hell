package views;

import bootstrapper.Main;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import logic.Score;

import java.util.List;

public class ScoreboardController {

    private Main application;
    @FXML private TableColumn<Score, String> player = new TableColumn<>("player");
    @FXML private TableColumn<Score, Double> score = new TableColumn<>("score");
    @FXML private TableView<Score> tableView;

    @FXML
    public void initialize() {
        player.setCellValueFactory(new PropertyValueFactory<>("name"));

        score.setCellValueFactory(new PropertyValueFactory<>("score"));
    }

    public void setApplication(Main application)
    {
        this.application = application;
    }

    public void setScore(List<Score> score) {
        ObservableList<logic.Score> scores = FXCollections.observableArrayList(score);
        this.tableView.setItems(scores);
    }

    public void backToLobby()
    {
        application.backToLobby();
    }
}
