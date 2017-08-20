package io.github.talkarcabbage.rstimer.fxgui;

import java.util.logging.Logger;

import io.github.talkarcabbage.logger.LoggerManager;
import javafx.scene.control.ProgressBar;

/**
 * 
 * @author Talkarcabbage
 *
 */
public class ColorProgressBar extends ProgressBar { //NOSONAR We don't care about number of parent classes
	
	static final Logger logger = LoggerManager.getInstance().getLogger("FXColorProgressBar");

	public ColorProgressBar() {
		super();
	}

	public ColorProgressBar(double arg0) {
		super(arg0);
	}
	
	/**
	 * Set the style class of the progress bar to the specified complete/incomplete identifier
	 * @param styleClass
	 */
	public void setTrackStyleClass(String styleClass) {
		this.getStyleClass().clear();
		this.getStyleClass().add("progress-bar");
		this.getStyleClass().add(styleClass);
		this.applyCss();
	}
}