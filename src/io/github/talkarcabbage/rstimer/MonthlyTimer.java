package io.github.talkarcabbage.rstimer;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;
import java.util.logging.Logger;

import io.github.talkarcabbage.logger.LoggerManager;

/**
 * 
 * @author Talkarcabbage
 *
 */
public class MonthlyTimer extends Timer {

	
	@SuppressWarnings("unused")
	private static final Logger logger = LoggerManager.getInstance().getLogger("MonthlyTimer");
	
	public MonthlyTimer(long targetTime, long durationTotal, String name, int tab) {
		super(targetTime, durationTotal, name, tab);
		timerType = TimerType.MONTHLY;
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
	
	@Override
	public String getNewTimerTypeString() {
		return "Monthly";
	}
	
	@Override
	public Map<String, String> getDataMap() {
		HashMap<String, String> map = new HashMap<>(8);
		map.put("name", this.name);
		map.put("latestreset", String.valueOf(this.startingTime));
		map.put("tab", String.valueOf(this.getTab()));
		map.put("audio", "false");
		return map;
	}
	
}
