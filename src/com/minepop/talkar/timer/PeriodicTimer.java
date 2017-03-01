package com.minepop.talkar.timer;

import java.util.logging.Logger;

import com.minepop.talkar.util.logging.LoggerConstructor;

public class PeriodicTimer extends Timer {
	
	private static final Logger logger = LoggerConstructor.getLogger("PeriodicTimer");


	public PeriodicTimer(long targetTime, long durationTotal, String name, int tab) {
		super(targetTime, durationTotal, name, tab);
		timerType = TimerType.PERIODIC;
	}
	
	
	@Override
	public void resetTimer(){ 

		if ((long)getDuration() == WEEK_LENGTH) {
			setStartingTime((((System.currentTimeMillis()+DAY_LENGTH)/WEEK_LENGTH)*WEEK_LENGTH)-DAY_LENGTH);
			logger.fine("Set weekly timer timer with data: " + this.toString());
		} else if ((long)getDuration() == DAY_LENGTH){
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

}
