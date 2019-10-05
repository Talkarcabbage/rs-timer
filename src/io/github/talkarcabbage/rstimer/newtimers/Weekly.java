package io.github.talkarcabbage.rstimer.newtimers;

import java.util.Map;

public class Weekly extends NewTimer {
	
	long latestReset;

	public Weekly(String name, int tabID, boolean audio, long latestReset) {
		super(name, tabID, audio);
		this.latestReset = latestReset;
	}
	
	/**
	 * Creates a timer from the given map of timer data. Invalid properties will be ignored with a console warning and missing values will be defaulted.
	 * @param dataMap
	 */
	public Weekly(Map<String, String> dataMap) {
		super("MISSING", 0, false); //Some arbitrary defaults in case of missing data
		latestReset = 0; //Arbitrary default values
		for (Map.Entry<String, String> entry : dataMap.entrySet()) {
			try {
				switch (entry.getKey()) {
				case "name":
					this.name = entry.getValue();
					break;
				case "audio":
					this.audio = Boolean.parseBoolean(entry.getValue());
					break;
				case "tab":
					this.tabID = Integer.parseInt(entry.getValue());
					break;
				case "latestreset":
					this.latestReset = Long.parseLong(entry.getValue());
					break;
				default:
					logger.warning("Unknown property type found while parsing Weekly timer:" + entry.getKey());
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
		latestReset = (((System.currentTimeMillis()+DAY_LENGTH_MILLIS)/WEEK_LENGTH_MILLIS)*WEEK_LENGTH_MILLIS)-DAY_LENGTH_MILLIS;
		logger.fine( () -> ("Reset weekly timer timer with data: " + this.toString()));
		
	}

	@Override
	public void resetTimerComplete() {
		latestReset = 0;
	}

	@Override
	public String getTimerTypeString() {
		return "Daily";
	}

	@Override
	public String getTooltipText() {
		return "Weekly Timer\n" + formatTimeRemaining(getTimeRemaining());
	}

	@Override
	public int getPercentageComplete() {
		long rawPercentage = (100*(System.currentTimeMillis() - latestReset))/(WEEK_LENGTH_MILLIS);
		return rawPercentage >= 100 ? 100 : (int) rawPercentage;
	}

	@Override
	public long getTimeRemaining() {
		return (latestReset + WEEK_LENGTH_MILLIS) - System.currentTimeMillis();
	}
	
	@Override
	public Map<String, String> getTimerDataMap() {
		Map<String, String> map = super.getTimerDataMap();
		map.put("latestreset", String.valueOf(latestReset));
		return map;
	}
	
}
