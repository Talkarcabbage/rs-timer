package io.github.talkarcabbage.rstimer;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import io.github.talkarcabbage.logger.LoggerManager;

/**
 * 
 * @author Talkarcabbage
 *
 */
public class PeriodicTimer extends Timer {
	
	private static final Logger logger = LoggerManager.getInstance().getLogger("PeriodicTimer");

	public PeriodicTimer(long targetTime, long durationTotal, String name, int tab) {
		super(targetTime, durationTotal, name, tab);
		timerType = TimerType.PERIODIC;
	}
	
	@Override
	public void resetTimer(){ 

		if (getDuration() == WEEK_LENGTH) {
			setStartingTime((((System.currentTimeMillis()+DAY_LENGTH)/WEEK_LENGTH)*WEEK_LENGTH)-DAY_LENGTH);
			logger.fine( () -> ("Set weekly timer timer with data: " + this.toString()));
		} else if (getDuration() == DAY_LENGTH){
			setStartingTime((System.currentTimeMillis()/DAY_LENGTH)*DAY_LENGTH);
			logger.fine( () -> ("Set daily timer timer with data: " + this.toString()));
		} else {
			logger.severe( () -> ("Failed to match timer type for periodic timer: " + this.toString()));
		}
	}
	
	@Override
	public void resetTimerComplete() {
		setStartingTime(0);
	}
	
	@Override
	public String getNewTimerTypeString() {
		return getDuration() == WEEK_LENGTH ? "Weekly" : "Daily";
	}
	
	@Override
	public Map<String, String> getDataMap() {
		HashMap<String, String> map = new HashMap<>(8);
		map.put("name", this.name);
		map.put("latestreset", String.valueOf(this.startingTime));
		map.put("tab", String.valueOf(this.getTab()));
		map.put("audio", "none");
		return map;
	}

}