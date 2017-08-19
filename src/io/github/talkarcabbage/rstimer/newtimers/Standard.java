package io.github.talkarcabbage.rstimer.newtimers;

import java.util.Map;

public class Standard extends NewTimer {
	
	long latestReset;
	long duration;

	public Standard(String name, int tabID, String audio, long latestReset, long duration) {
		super(name, tabID, audio);
		this.latestReset = latestReset;
		this.duration = duration;
	}
	
	/**
	 * Creates a timer from the given map of timer data. Invalid properties will be ignored with a console warning and missing values will be defaulted.
	 * @param dataMap
	 */
	public Standard(Map<String, String> dataMap) {
		super("MISSING", 0, "none"); //Some arbitrary defaults in case of missing data
		latestReset = 0; //Arbitrary default values
		duration = 1000; //Arbitrary default values
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
				case "duration":
					this.duration = Long.parseLong(entry.getValue());
					break;
				default:
					logger.warning("Unknown property type found while parsing Standard timer:" + entry.getKey());
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
		this.latestReset = System.currentTimeMillis();	
		logger.fine( () -> ("Reset standard timer with data: " + this.toString()));
	}

	@Override
	public void resetTimerComplete() {
		this.latestReset = 0;
		logger.fine( () -> ("Reset-complete standard timer with data: " + this.toString()));
	}

	@Override
	public String getTimerTypeString() {
		return "Standard";
	}

	@Override
	public Map<String, String> getTimerDataMap() {
		Map<String, String> map = super.getTimerDataMap();
		map.put("latestreset", String.valueOf(latestReset));
		map.put("duration", String.valueOf(duration));
		return map;
	}

	@Override
	public String getTooltipText() {
		return "Standard Timer\n" + formatTimeRemaining(getTimeRemaining());
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

}
