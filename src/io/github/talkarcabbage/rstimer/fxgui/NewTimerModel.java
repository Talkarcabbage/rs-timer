package io.github.talkarcabbage.rstimer.fxgui;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import io.github.talkarcabbage.logger.LoggerManager;
import io.github.talkarcabbage.rstimer.newtimers.Daily;
import io.github.talkarcabbage.rstimer.newtimers.Monthly;
import io.github.talkarcabbage.rstimer.newtimers.NewTimer;
import io.github.talkarcabbage.rstimer.newtimers.Hourly;
import io.github.talkarcabbage.rstimer.newtimers.Standard;
import io.github.talkarcabbage.rstimer.newtimers.Weekly;

/**
 * This class provides an object representation of the data present on an add interface.
 * An instance of this class is populated with the data from the GUI and can be used to construct Timers.
 * This instance should sanitize the bounds of its inputs but specific validity should be verified externally.
 * @author talkar
 *
 */
public class NewTimerModel {
	
	private static final Logger logger = LoggerManager.getInstance().getLogger("TimerModel");

	public enum TimerModelType {
		NONE, STANDARD, DAILY, WEEKLY, MONTHLY, HOURLY
	}
	
	TimerModelType timerType;
	String name;
	long duration;
	String audioString;
	boolean alarm = false;
	boolean autoReset = false;
	
	
	/**
	 * Creates a new model with default values, to be populated via the GUI.
	 */
	public NewTimerModel() {
		timerType = TimerModelType.NONE;
		name = "[none]";
		duration = 1;
		audioString = "none";
		sanitize();
	}
	
	/**
	 * Creates a new model and sets the model's values to those from the given Timer.
	 * Method may be removed later if not necessary! //TODO: incomplete implementation
	 * @param timer the timer
	 */
	public NewTimerModel(NewTimer timer) {
		if (timer instanceof Standard) {
			timerType = TimerModelType.STANDARD;
		} else if (timer instanceof Daily) {
			timerType = TimerModelType.DAILY;
		} else if (timer instanceof Weekly) {
			timerType = TimerModelType.WEEKLY;
		} else if (timer instanceof Monthly) {
			timerType = TimerModelType.MONTHLY;
		} else if (timer instanceof Hourly) {
			timerType = TimerModelType.HOURLY;
		} else {
			logger.warning("Unknown timer type in model for timer " + timer.getName());
			timerType = TimerModelType.NONE;
		}
		name = timer.getName();
		

	}
	
	//TODO Custom audio options?
	public static NewTimerModel getModelFromControllerData(String nameText, String type, String days, String hours, String minutes, String seconds,  boolean alarm, boolean autoReset) {
		NewTimerModel model = new NewTimerModel();
		model.name=nameText;
		model.setTypeFromTypeString(type);
		model.setDurationFromFields(days, hours, minutes, seconds);
		model.alarm=alarm;
		model.autoReset=autoReset;
		return model;
	}
	
	
	
	/**
	 * 
	 * @param timer
	 */
	public static long getDurationIfFieldExists(NewTimer timer) {
		if (timer instanceof Standard) {
			return ((Standard)timer).getDuration();
		} else if (timer instanceof Hourly) {
			return ((Hourly)timer).getDuration();
		}
		return 0;
		
	}
	
	/**
	 * Sanitize currently checks the following values and brings them within bounds to a default value if they are invalid:<br />
	 * {@link duration} - Ensures values are greater than 0<br />
	 * {@link name} - Removes newlines, which should never be possible to input anyway and are unlikely to cause problems but are removed for safety purposes
	 */
	public void sanitize() {
		
		//------------------Duration---------------//
		if (duration <= 0) {
			duration = 1;
		}
		
		//------------------Name-------------------//
		if (name == null) {
			name = " ";
		}
		name = name.replace('\n', ' ');
		name = name.replace('\r', ' ');
		if (name.length() < 1) {
			name = " ";
		}
		
	}
	
	/**
	 * Sets the duration value (in milliseconds) of the model based on the input strings of the given time values found on the GUI.
	 * The typical way these Strings are generated is via {@link javafx.scene.control.TextField#getText}
	 * @param days The days
	 * @param hours The hours
	 * @param minutes The minutes
	 * @param seconds The seconds
	 */
	public void setDurationFromFields(String days, String hours, String minutes, String seconds) {
		long tempDuration = 0;
		tempDuration += getLongForString(seconds);
		tempDuration += getLongForString(minutes)*60;
		tempDuration += getLongForString(hours)*3600;
		tempDuration += getLongForString(days)*(NewTimer.DAY_LENGTH_MILLIS/1000);
		tempDuration *= 1000;

		this.duration = tempDuration;
		sanitize();
	}
	
	public void setTypeFromTypeString(String text) {
		String temp = text.toLowerCase();
		if (temp.equals("standard")) {
			timerType = TimerModelType.STANDARD;
		} else if (temp.equals("daily")) {
			timerType = TimerModelType.DAILY;
		} else if (temp.equals("weekly")) {
			timerType = TimerModelType.WEEKLY;
		} else if (temp.equals("monthly")) {
			timerType = TimerModelType.MONTHLY;
		} else if (temp.equals("hourly")) {
			timerType = TimerModelType.HOURLY;
		} else {
			logger.warning("Unknown timer type in model for text " + text);
			timerType = TimerModelType.NONE;
		}
	}
	
	/**
	 * Returns a long that the field argument represents, or 0 if it is not a valid input.
	 * @param field
	 */
	public long getLongForString(String field) {
		try {
			return Long.parseLong(field);
		} catch (NumberFormatException e) {
			logger.log(Level.SEVERE, "Found invalid data in a model's inputted text field: ", e);
			return 0;
		}
	}
	
	/**
	 * Returns a representation of this timer model as a data map, for easier storage or 
	 * timer construction.
	 * Note that the type is not included in the map as it is 
	 * generally accessible elsewhere and isn't usually part of the map.
	 * Currently the tab is set via the currently selected tab. 
	 * Overwrite it manually if necessary. 
	 * Finally, the latest reset is not set. Set it manually. 
	 * @return
	 */
	public Map<String, String> asDataMap() {
		Map<String, String> theMap = new HashMap<>();
		theMap.put(NewTimer.MAP_NAME, this.name);
		theMap.put(NewTimer.MAP_DURATION, "" + this.duration);
		theMap.put(NewTimer.MAP_TAB, "" + MainWindow.instance.getCurrentTab());
		theMap.put(NewTimer.MAP_AUDIO, "" + this.alarm);
		theMap.put(NewTimer.MAP_LATEST_RESET, "" + 0);
		return theMap;
	}
	
}
