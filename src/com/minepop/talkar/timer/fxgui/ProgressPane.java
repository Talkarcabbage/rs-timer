package com.minepop.talkar.timer.fxgui;

import java.util.logging.Logger;

import com.minepop.talkar.timer.fxgui.concurrent.ProgressBarTask;
import com.minepop.talkar.util.logging.LoggerConstructor;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;

/**
 * 
 * @author Talkarcabbage
 *
 */
public class ProgressPane extends StackPane { //NOSONAR
	
	static final Logger logger = LoggerConstructor.getLogger("FXWinPPane");

	ColorProgressBar bar;
	Label label;
	ProgressBarTask progressBarTask;
	
	public ProgressPane() {
		bar = new ColorProgressBar();
		label = new Label();
		
		bar.setMaxWidth(Double.MAX_VALUE);
		bar.setMaxHeight(26);
		bar.setMinHeight(20);
		bar.setPrefHeight(26);
		
		label.setMaxHeight(30);
		label.setMaxWidth(Double.MAX_VALUE);
		label.setPrefHeight(30);
		label.setMinHeight(20);
		
		label.setText("Boop");
		label.setAlignment(Pos.CENTER);
		
		GridPane.setHgrow(this, Priority.ALWAYS);
		
		this.getStyleClass().add("progressPane");
		
		this.getChildren().addAll(bar, label);
		initBarStyle();
		
		bar.setTooltip(new Tooltip(""));
		label.setTooltip(new Tooltip(""));
	}

	public ColorProgressBar getBar() {
		return bar;
	}

	public void setBar(ColorProgressBar bar) {
		this.bar = bar;
	}

	public Label getLabelObject() {
		return label;
	}

	public void setLabelObject(Label label) {
		this.label = label;
	}
	
	public void setLabelText(String text) {
		label.setText(text);
	}
	
	public String getLabelText() {
		return label.getText();
	}
	
	/**
	 * Set the value of this progresspane's progress bar. This value should be supplied as a
	 * percentage value between 0-100 inclusive.
	 */
	public void setProgress(long value) {
		double oldProgress = bar.getProgress();
		double newProgress = ((double)value)/100;
		if (oldProgress < 1 && newProgress >= 1) {
			bar.setTrackStyleClass(MainWindow.COMSTRING);
			logger.finer("Progress set from incomplete to complete");
		} else if (oldProgress >= 1 && newProgress < 1) {
			logger.finer("Progress set from complete to incomplete");
			bar.setTrackStyleClass(MainWindow.INCSTRING);
		}
		this.bar.setProgress(newProgress);
	}
	
	void initBarStyle() {
		bar.setTrackStyleClass(MainWindow.INCSTRING);
	}
	
	public void setBarTask(ProgressBarTask task) {
		this.progressBarTask = task;
	}

}