package com.minepop.talkar.timer;

import java.awt.Color;

import javax.swing.JProgressBar;

public class PeriodicTimer extends Timer {

	public PeriodicTimer(double targetTime, double durationTotal, String name, int tab, JProgressBar bar) {
		super(targetTime, durationTotal, name, tab,  bar);
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public void resetTimer(){ 
		this.progressBar.setForeground(Color.black);

		if (getDurationTotal() == WEEK_LENGTH) {
			setStartingTime((((Math.floor((System.currentTimeMillis()+DAY_LENGTH)/WEEK_LENGTH))*WEEK_LENGTH)-DAY_LENGTH));
			Logger.DEBUG("Set weekly timer timer with data: " + this.toString());
		} else if (getDurationTotal() == DAY_LENGTH){
			setStartingTime(Math.floor(System.currentTimeMillis()/DAY_LENGTH)*DAY_LENGTH);
			Logger.DEBUG("Set daily timer timer with data: " + this.toString());
		} else {
			Logger.ERROR("Failed to match timer type for periodic timer: " + this.toString());
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
