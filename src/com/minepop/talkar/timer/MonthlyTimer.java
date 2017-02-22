package com.minepop.talkar.timer;

import java.awt.Color;
import java.util.Calendar;
import java.util.TimeZone;

import javax.swing.JProgressBar;

public class MonthlyTimer extends Timer {
	
	static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger("Monthly-Timer"); //NOSONAR


	public MonthlyTimer(double targetTime, double durationTotal, String name, int tab, JProgressBar bar) {
		super(targetTime, durationTotal, name, tab,  bar);
	}
	
	@Override
	public void resetTimer() {
		this.progressBar.setForeground(Color.black);
		Calendar startC = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
		startC.set(Calendar.DATE, 1);
		startC.set(Calendar.HOUR, 0);
		startC.set(Calendar.AM_PM, Calendar.AM);
		startC.set(Calendar.MINUTE, 0);
		startC.set(Calendar.SECOND, 0);
		startC.set(Calendar.MILLISECOND, 0);
		logger.info(startC.getTime().toString());
		
		
		Calendar endC = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
		endC.set(Calendar.DATE, 1);
		endC.set(Calendar.HOUR, 0);
		endC.set(Calendar.AM_PM, Calendar.AM);
		endC.set(Calendar.MINUTE, 0);
		endC.set(Calendar.SECOND, 0);
		endC.set(Calendar.MILLISECOND, 0);
		endC.set(Calendar.MONTH, endC.get(Calendar.MONTH)+1);
		logger.fine(endC.getTime().toString());
		
		this.startingTime = startC.getTimeInMillis();
		this.duration = (double)endC.getTimeInMillis() - startC.getTimeInMillis();
		
	}
	
	@Override
	public void resetTimerComplete() {
		this.startingTime = 0;
	}
	
	@Override
	public String getTimerType() {
		return Main.MONTHLYTIMER;
	}


	
}
