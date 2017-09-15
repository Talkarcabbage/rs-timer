package io.github.talkarcabbage.rstimer.fxgui;

import java.util.logging.Level;
import java.util.logging.Logger;

import io.github.talkarcabbage.logger.LoggerManager;
import io.github.talkarcabbage.rstimer.newtimers.Daily;
import io.github.talkarcabbage.rstimer.newtimers.Monthly;
import io.github.talkarcabbage.rstimer.newtimers.NewTimer;
import io.github.talkarcabbage.rstimer.newtimers.Repeating;
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
		NONE, STANDARD, DAILY, WEEKLY, MONTHLY, REPEATING
	}
	
	TimerModelType timerType;
	String name;
	long duration;
	String audioString;
	
	
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
	 * Method may be removed later if not necessary!
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
		} else if (timer instanceof Repeating) {
			timerType = TimerModelType.REPEATING;
		} else {
			logger.warning("Unknown timer type in model for timer " + timer.getName());
			timerType = TimerModelType.NONE;
		}
		name = timer.getName();
		

	}
	
	/**
	 * 
	 * @param timer
	 */
	public static long getDurationIfFieldExists(NewTimer timer) {
		if (timer instanceof Standard) {
			return ((Standard)timer).getDuration();
		} else if (timer instanceof Repeating) {
			return ((Repeating)timer).getDuration();
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
		tempDuration += getLongForString(days)*NewTimer.DAY_LENGTH;
		tempDuration *= 1000;

		this.duration = tempDuration;
		sanitize();
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
	
}
