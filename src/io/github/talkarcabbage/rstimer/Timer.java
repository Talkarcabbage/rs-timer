package io.github.talkarcabbage.rstimer;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import com.minepop.talkar.util.logging.LoggerConstructor;

/**
 * 
 * @author Talkarcabbage
 *
 */
public class Timer {
	
	private static final Logger logger = LoggerConstructor.getLogger("Timer");

	public enum TimerType {
		STANDARD,
		PERIODIC,
		MONTHLY
	}
	
	volatile TimerType timerType;
	
	volatile long startingTime;
	volatile long duration;
	volatile String name;
	volatile int tab;
	
	public static final long DAY_LENGTH = 86400000;
	public static final long WEEK_LENGTH = 604800000;

	/**
	 * 
	 * @param startingTime - The system time the timer started at.
	 * @param durationTotal - The total time the timer should run for. This is used to reset the timer and check for completion.
	 * @param name - A name for the timer. This is used as a label in the GUI.
	 */
	public Timer(long targetTime, long durationTotal, String name, int tab) {
		this.startingTime = targetTime;
		this.duration = durationTotal;
		this.name = name;
		this.tab = tab;
		timerType = TimerType.STANDARD;
	}
	
	// Standard getters and setters
	
	public double getStartingTime() {
		return startingTime;
	}

	public void setStartingTime(long startingTime) {
		this.startingTime = startingTime;
	}

	public long getDuration() {
		return duration;
	}

	public void setDuration(long durationTotal) {
		this.duration = durationTotal;
	}
	
	/**
	 * Returns a new Duration object that represents this timer's duration
	 * @return
	 */
	public Duration getDurationObject() {
		return Duration.ofMillis(this.duration);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getTab() {
		return tab;
	}

	public void setTab(int tab) {
		this.tab = tab;
	}
	
	//End standard getters and setters

	@Override
	public boolean equals(Object obj) { //NOSONAR
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Timer other = (Timer) obj;
		if (Double.doubleToLongBits(duration) != Double
				.doubleToLongBits(other.duration))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (Double.doubleToLongBits(startingTime) != Double
				.doubleToLongBits(other.startingTime))
			return false;
		return (tab == other.tab);
	}

	@Override
	public String toString() {
		return "Timer [startingTime=" + startingTime + ", duration=" + duration
				+ ", name=" + name + ", tab=" + tab + "]";
	}
	
	/**
	 * Returns the percentage of this timer's progress toward completion, for setting progress bars.
	 * @return
	 */
	public int getPercentageComplete() {
		long rawPercentage = (100*(System.currentTimeMillis() - startingTime))/(duration);
		return rawPercentage >= 100 ? 100 : (int) rawPercentage;
	}
	
	/**
	 * Returns the amount of time in millis until the timer is complete.
	 * @return
	 */
	public long getTimeRemaining() {
		return (startingTime + duration) - System.currentTimeMillis();
	}
	
	/**
	 * Resets the timer to a status of incomplete. For normal timers, this should be 0% complete.
	 * For abnormal timers, this should set their next completion to the next applicable reset.
	 * This should be overridden.
	 */
	public void resetTimer() {
		setStartingTime(System.currentTimeMillis());	
		logger.fine( () -> ("Set normal timer with data: " + this.toString()));
	}
	
	/**
	 * Resets the timer to a status of complete. This is typically/most easily done by setting the start-time to 0.
	 * This may be overridden.
	 */
	public void resetTimerComplete() {
		setStartingTime(0);
	}

	/**
	 * Returns the type of the timer. 
	 * @return
	 */
	public String getTimerTypeString() {
		return timerType.toString().toLowerCase();
	}
	
	public TimerType getTimerType() {
		return timerType;
	}
	
	/**
	 * This should not be overridden. Override getDataMap and getNewTimerTypeString.
	 * @return
	 */
	public String getNewTimerSaveText() {
		StringBuilder sb = new StringBuilder();
		sb.append("\t" + getNewTimerTypeString() + " {" + "\n");
		Map<String, String> map = getDataMap();
		for (Map.Entry<String, String> entry : map.entrySet()) {
			sb.append("\t\t" + entry.getKey() + ":" + entry.getValue() + "\n");
		}
		sb.append("\t" + "}" + "\n");
		return sb.toString();
	}
	
	/**
	 * This should always be overridden. It defines the way the timer is saved.
	 * @return
	 */
	public Map<String, String> getDataMap() {
		HashMap<String, String> map = new HashMap<>(8);
		map.put("name", this.name);
		map.put("latestreset", String.valueOf(this.startingTime));
		map.put("duration", String.valueOf(this.duration));
		map.put("tab", String.valueOf(this.getTab()));
		map.put("audio", "none");
		return map;
	}
	
	/**
	 * This should always be overridden. It defines the way the timer is saved.
	 * @return
	 */
	public String getNewTimerTypeString() {
		return "Standard"; //NOSONAR
	}
	
}