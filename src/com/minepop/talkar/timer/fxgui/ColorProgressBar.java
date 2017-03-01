package com.minepop.talkar.timer.fxgui;

import java.util.logging.Logger;

import com.minepop.talkar.util.logging.LoggerConstructor;

import javafx.scene.control.ProgressBar;

public class ColorProgressBar extends ProgressBar {
	
	static final Logger logger = LoggerConstructor.getLogger("FXColorProgressBar");

	
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
