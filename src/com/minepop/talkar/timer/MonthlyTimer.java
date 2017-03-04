package com.minepop.talkar.timer;

import java.util.Calendar;
import java.util.TimeZone;
import java.util.logging.Logger;

import com.minepop.talkar.util.logging.LoggerConstructor;

/**
 * 
 * @author Talkarcabbage
 *
 */
public class MonthlyTimer extends Timer {

	
	@SuppressWarnings("unused")
	private static final Logger logger = LoggerConstructor.getLogger("MonthlyTimer");
	
	public MonthlyTimer(long targetTime, long durationTotal, String name, int tab) {
		super(targetTime, durationTotal, name, tab);
		timerType = TimerType.MONTHLY;
		//resetTimer();
	}
	
	@Override
	public void resetTimer() {
		Calendar startC = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
		startC.set(Calendar.DATE, 1);
		startC.set(Calendar.HOUR, 0);
		startC.set(Calendar.AM_PM, Calendar.AM);
		startC.set(Calendar.MINUTE, 0);
		startC.set(Calendar.SECOND, 0);
		startC.set(Calendar.MILLISECOND, 0);
		
		Calendar endC = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
		endC.set(Calendar.DATE, 1);
		endC.set(Calendar.HOUR, 0);
		endC.set(Calendar.AM_PM, Calendar.AM);
		endC.set(Calendar.MINUTE, 0);
		endC.set(Calendar.SECOND, 0);
		endC.set(Calendar.MILLISECOND, 0);
		endC.set(Calendar.MONTH, endC.get(Calendar.MONTH)+1);
		
		this.startingTime = startC.getTimeInMillis();
		this.duration = endC.getTimeInMillis() - startC.getTimeInMillis();
	}
	
	@Override
	public void resetTimerComplete() {
		this.startingTime = 0;
	}
}
