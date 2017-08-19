package com.minepop.talkar.timer.newtimers;

import java.util.Calendar;
import java.util.Map;
import java.util.TimeZone;

public class Monthly extends NewTimer {
	
	long latestReset;
	long duration;

	public Monthly(String name, int tabID, String audio, long latestReset) {
		super(name, tabID, audio);
		this.latestReset = latestReset;
		duration = getDurationToNextMonth();
	}
	
	/**
	 * Creates a timer from the given map of timer data. Invalid properties will be ignored with a console warning and missing values will be defaulted.
	 * @param dataMap
	 */
	public Monthly(Map<String, String> dataMap) {
		super("MISSING", 0, "none"); //Some arbitrary defaults in case of missing data
		latestReset = 0; //Arbitrary default values
		for (Map.Entry<String, String> entry : dataMap.entrySet()) {
			try {
				switch (entry.getKey()) {
				case "name":
					this.name = entry.getValue();
					break;
				case "audio":
					this.audio = entry.getValue();
					break;
				case "tab":
					this.tabID = Integer.parseInt(entry.getValue());
					break;
				case "latestreset":
					this.latestReset = Long.parseLong(entry.getValue());
					break;
				default:
					logger.warning("Unknown property type found while parsing Monthly timer:" + entry.getKey());
					break;
				}
			} catch (NumberFormatException e) {
				logger.severe("Invalid timer number value " + entry.getValue() + " for property " + entry.getKey());
			}
		}
	}
	
	
	public long getLatestReset() {
		return this.latestReset;
	}

	@Override
	public void resetTimer() {
		Calendar startC = getBeginningOfMonthFor(System.currentTimeMillis());
		Calendar endC = getEndOfMonthFor(System.currentTimeMillis());
		
		this.latestReset = startC.getTimeInMillis();
		this.duration = endC.getTimeInMillis() - startC.getTimeInMillis();
	}

	@Override
	public void resetTimerComplete() {
		this.latestReset = 0;
	}

	@Override
	public String getTimerTypeString() {
		return "Monthly";
	}

	@Override
	public String getTooltipText() {
		return "Monthly Timer\n" + formatTimeRemaining(getTimeRemaining());
	}

	@Override
	public int getPercentageComplete() {
		long rawPercentage = (100*(System.currentTimeMillis() - latestReset))/(duration);
		return rawPercentage >= 100 ? 100 : (int) rawPercentage;
	}

	@Override
	public long getTimeRemaining() {
		return (latestReset + duration) - System.currentTimeMillis();
	}
	
	@Override
	public Map<String, String> getTimerDataMap() {
		Map<String, String> map = super.getTimerDataMap();
		map.put("latestreset", String.valueOf(latestReset));
		return map;
	}
	
	
	/**
	 * Returns the duration between the given starting time's start of the month and next month.
	 * This value is based on the current latestReset value.
	 * @return The time difference
	 */
	public long getDurationToNextMonth() {
		Calendar startC = getBeginningOfMonthFor(latestReset);
		Calendar endC = getEndOfMonthFor(latestReset);
		return endC.getTimeInMillis() - startC.getTimeInMillis();
	}
	
	/**
	 * Returns a Calendar object representing the 0:00 first day of the month for a given time.
	 * @param time The time
	 * @return The Calendar
	 */
	public static Calendar getBeginningOfMonthFor(long time) {
		Calendar startC = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
		startC.setTimeInMillis(time);
		startC.set(Calendar.DATE, 1);
		startC.set(Calendar.HOUR, 0);
		startC.set(Calendar.AM_PM, Calendar.AM);
		startC.set(Calendar.MINUTE, 0);
		startC.set(Calendar.SECOND, 0);
		startC.set(Calendar.MILLISECOND, 0);
		return startC;
	}
	
	/**
	 * Returns a Calendar object representing the 0:00 first day of the <b>next</b> month for a given time.
	 * @param time The time
	 * @return The Calendar
	 */
	public static Calendar getEndOfMonthFor(long time) {		
		Calendar endC = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
		endC.setTimeInMillis(time);
		endC.set(Calendar.DATE, 1);
		endC.set(Calendar.HOUR, 0);
		endC.set(Calendar.AM_PM, Calendar.AM);
		endC.set(Calendar.MINUTE, 0);
		endC.set(Calendar.SECOND, 0);
		endC.set(Calendar.MILLISECOND, 0);
		endC.set(Calendar.MONTH, endC.get(Calendar.MONTH)+1);
		return endC;
	}
	

}
