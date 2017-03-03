package com.minepop.talkar.timer.fxgui;

import com.google.common.collect.BiMap;
import com.minepop.talkar.timer.FXController;
import com.minepop.talkar.timer.Timer;
import com.minepop.talkar.util.ConfigManager;

import javafx.animation.AnimationTimer;

/**
 * 
 * @author Talkarcabbage
 *
 */
public class ProgressAnimTimer extends AnimationTimer {

	final BiMap<ProgressPane, Timer> map;
	int animCycle;
	
	public ProgressAnimTimer(BiMap<ProgressPane, Timer> biMap) {
		map = biMap;
		animCycle = 1000000;
	}

	@Override
	public void handle(long now) {
		if (++animCycle >= ConfigManager.getInstance().getFramesPerUpdate()) {
			animCycle = 0;
			map.forEach( (pane, timer) -> {
				pane.setProgress(timer.getPercentageComplete());
				pane.bar.getTooltip().setText(FXController.formatTime(timer.getTimeRemaining()));
				pane.label.getTooltip().setText(FXController.formatTime(timer.getTimeRemaining()));
			});	
		}
	}
}