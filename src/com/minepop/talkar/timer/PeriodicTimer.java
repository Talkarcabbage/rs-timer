package com.minepop.talkar.timer;

import java.awt.Color;

import javax.swing.JProgressBar;

public class PeriodicTimer extends Timer {
	

	public PeriodicTimer(long targetTime, long durationTotal, String name, int tab, JProgressBar bar) {
		super(targetTime, durationTotal, name, tab,  bar);
		
	}
	
	
	@Override
	public void resetTimer(){ 
		this.progressBar.setForeground(Color.black);

		if ((long)getDurationTotal() == WEEK_LENGTH) {
			setStartingTime((((System.currentTimeMillis()+DAY_LENGTH)/WEEK_LENGTH)*WEEK_LENGTH)-DAY_LENGTH);
			logger.fine("Set weekly timer timer with data: " + this.toString());
		} else if ((long)getDurationTotal() == DAY_LENGTH){
			setStartingTime((System.currentTimeMillis()/DAY_LENGTH)*DAY_LENGTH);
			logger.fine("Set daily timer timer with data: " + this.toString());
		} else {
			logger.severe("Failed to match timer type for periodic timer: " + this.toString());
		}

	}
	
	@Override
	public void resetTimerComplete() {
		setStartingTime(0);
	}
	
	@Override
	public String getTimerType() {
		return Main.PERIODICTIMER;
	}

}
