package views;

import javafx.animation.Animation;
import javafx.animation.Interpolator;
import javafx.animation.ParallelTransition;
import javafx.animation.TranslateTransition;
import javafx.fxml.FXML;
import javafx.scene.image.ImageView;
import javafx.util.Duration;

public class BackgroundController {

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
        int backgroundHeight = 2000;
        translateTransition.setToY(backgroundHeight);
		translateTransition.setInterpolator(Interpolator.LINEAR);
	
		TranslateTransition translateTransition2 =
            new TranslateTransition(Duration.millis(22500), background2);
		translateTransition2.setFromY(0);
		translateTransition2.setToY(backgroundHeight);
		translateTransition2.setInterpolator(Interpolator.LINEAR);

		parallelTransition = 
			new ParallelTransition( translateTransition, translateTransition2);
		parallelTransition.setCycleCount(Animation.INDEFINITE);
	}
	
	public void startAnimation() {
		parallelTransition.play();
	}
}
