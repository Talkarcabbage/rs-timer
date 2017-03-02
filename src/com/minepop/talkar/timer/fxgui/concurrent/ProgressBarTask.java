package com.minepop.talkar.timer.fxgui.concurrent;

import com.minepop.talkar.timer.Timer;
import com.minepop.talkar.timer.fxgui.ColorProgressBar;

import javafx.concurrent.Task;

/**
 * A
 * @author Talkarcabbage
 *
 * @param <V>
 */
public class ProgressBarTask extends Task<Integer> {

	final Timer timer;
	final ColorProgressBar progressBar;
	
	public ProgressBarTask(Timer timerIn, ColorProgressBar bar) {
		progressBar = bar;
		timer = timerIn;
	}
	
	@Override
	protected Integer call() throws Exception {
		Integer timerProg = timer.getPercentageComplete();
		this.updateValue(timerProg);
		return timerProg;
	}

}
