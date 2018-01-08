package views;

import javafx.animation.Animation;
import javafx.animation.Interpolator;
import javafx.animation.ParallelTransition;
import javafx.animation.TranslateTransition;
import javafx.fxml.FXML;
import javafx.scene.image.ImageView;
import javafx.util.Duration;

//todo move to more logical location, example "view"

public class BackgroundController {
	
	private int backgroundHeight = 2000;
	
	@FXML
	ImageView background1;
	@FXML
	ImageView background2;

	private ParallelTransition parallelTransition;
	
	@FXML
	public void initialize() {
		
		TranslateTransition translateTransition =
				new TranslateTransition(Duration.millis(22500), background1);
		translateTransition.setFromY(0);
		translateTransition.setToY(1 * backgroundHeight);
		translateTransition.setInterpolator(Interpolator.LINEAR);
	
		TranslateTransition translateTransition2 =
            new TranslateTransition(Duration.millis(22500), background2);
		translateTransition2.setFromY(0);
		translateTransition2.setToY(1 * backgroundHeight);
		translateTransition2.setInterpolator(Interpolator.LINEAR);

		parallelTransition = 
			new ParallelTransition( translateTransition, translateTransition2);
		parallelTransition.setCycleCount(Animation.INDEFINITE);
	}
	
	public void startAmination() {
		parallelTransition.play();
	}
}
