package bootstrapper;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import logic.Score;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import java.util.ArrayList;

public class scoreboardController {

    private ObservableList<Score> scores;

    @FXML private TableColumn<Score, String> Player = new TableColumn<>("Player");
    @FXML private TableColumn<Score, Double> Score = new TableColumn<>("Score");
    @FXML private TableView<Score> TableView;

    @FXML
    public void initialize() {
        Player.setCellValueFactory(new PropertyValueFactory<>("name"));

        Score.setCellValueFactory(new PropertyValueFactory<>("score"));
    }

    public void setScore(ArrayList<Score> score) {
        scores = FXCollections.observableArrayList(score);
        this.TableView.setItems(scores);
    }
}
