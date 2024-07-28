package io.github.talkarcabbage.rstimer.fxgui

import io.github.talkarcabbage.logger.LoggerManager
import io.github.talkarcabbage.rstimer.timers.*
import io.github.talkarcabbage.rstimer.toLow
import java.util.logging.Level

/**
 * This class provides an object representation of the data present on an add interface.
 * An instance of this class is populated with the data from the GUI and can be used to construct Timers.
 * This instance should sanitize the bounds of its inputs but specific validity should be verified externally.
 * @author talkar
 */
class NewTimerModel {

	internal var timerType: TimerModelType
	internal var name: String = "[null]"
	internal var duration: Long = 0
	internal var audio: Boolean = false
	internal var alarm = false
	internal var autoReset = false

	enum class TimerModelType {
		NONE, STANDARD, DAILY, WEEKLY, MONTHLY, HOURLY
	}


	/**
	 * Creates a new model with default values, to be populated via the GUI.
	 */
	constructor() {
		timerType = TimerModelType.NONE
		name = "[none]"
		duration = 1
		audio = false
		sanitize()
	}

	/**
	 * Creates a new model and sets the model's values to those from the given Timer.
	 * Method may be removed later if not necessary!
	 * @param timer the timer
	 */
	constructor(timer: BaseTimer) {
		when (timer) {
			is Standard -> {
				timerType = TimerModelType.STANDARD
			}
			is Daily -> {
				timerType = TimerModelType.DAILY
			}
			is Weekly -> {
				timerType = TimerModelType.WEEKLY
			}
			is Monthly -> {
				timerType = TimerModelType.MONTHLY
			}
			is Hourly -> {
				timerType = TimerModelType.HOURLY
			}
			else -> {
				logger.warning("Unknown timer type in model for timer "+timer.name)
				timerType = TimerModelType.NONE
			}
		}
		name = timer.name
		setDurationFromTimer(timer)
		this.alarm = timer.audio
		this.autoReset = timer.autoreset
	}

	/**
	 * Sanitize currently checks the following values and brings them within bounds to a default value if they are invalid:<br></br>
	 * [duration] - Ensures values are greater than 0<br></br>
	 * [name] - Removes newlines, which should never be possible to input anyway and are unlikely to cause problems but are removed for safety purposes
	 */
	fun sanitize() {

		//------------------Duration---------------//
		if (duration <= 0) {
			duration = 1000
		}

		//------------------Name-------------------//
		if (name=="") {
			name = " "
		}
		name = name.replace('\n', ' ')
		name = name.replace('\r', ' ')
		if (name.isEmpty()) {
			name = " "
		}
	}

	/**
	 * Sets the duration value (in milliseconds) of the model based on the input strings of the given time values found on the GUI.
	 * The typical way these Strings are generated is via [javafx.scene.control.TextField.getText]
	 * @param days The days
	 * @param hours The hours
	 * @param minutes The minutes
	 * @param seconds The seconds
	 */
	fun setDurationFromFields(days: String, hours: String, minutes: String, seconds: String) {
		var tempDuration: Long = 0
		tempDuration += getLongForString(seconds)
		tempDuration += getLongForString(minutes)*60
		tempDuration += getLongForString(hours)*3600
		tempDuration += getLongForString(days)*(BaseTimer.DAY_LENGTH_MILLIS/1000)
		tempDuration *= 1000
		this.duration = tempDuration
		sanitize()
	}

	fun setDurationFromTimer(timer: BaseTimer) {
		if (timer is Standard) {
			this.duration = timer.duration
		} else {
			this.duration = 0
		}
	}

	fun setTypeFromTypeString(text: String) {
		val temp = text.toLow()
		when (temp) {
			"standard" -> {
				timerType = TimerModelType.STANDARD
			}
			"daily" -> {
				timerType = TimerModelType.DAILY
			}
			"weekly" -> {
				timerType = TimerModelType.WEEKLY
			}
			"monthly" -> {
				timerType = TimerModelType.MONTHLY
			}
			"hourly" -> {
				timerType = TimerModelType.HOURLY
			}
			else -> {
				logger.warning("Unknown timer type in model for text $text")
				timerType = TimerModelType.NONE
			}
		}
	}

	/**
	 * Returns a long that the field argument represents, or 0 if it is not a valid input.
	 * @param field
	 */
	fun getLongForString(field: String): Long {
		try {
			return java.lang.Long.parseLong(field)
		} catch (e: NumberFormatException) {
			logger.log(Level.SEVERE, "Found invalid data in a model's inputted text field: ", e)
			return 0
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
	fun asDataMap(): Map<String, String> {
		val theMap = HashMap<String, String>()
		theMap[BaseTimer.MAP_NAME] = this.name
		theMap[BaseTimer.MAP_DURATION] = ""+this.duration
		theMap[BaseTimer.MAP_TAB] = ""+MainWindow.instance.currentTab
		theMap[BaseTimer.MAP_AUDIO] = ""+this.alarm
		theMap[BaseTimer.MAP_LATEST_RESET] = ""+0
		theMap[BaseTimer.MAP_AUTO_RESET] = autoReset.toString()
		return theMap
	}

	companion object {

		private val logger = LoggerManager.getInstance().getLogger("TimerModel")

		fun getModelFromControllerData(nameText: String, type: String, days: String, hours: String, minutes: String, seconds: String, alarm: Boolean, autoReset: Boolean): NewTimerModel {
			val model = NewTimerModel()
			model.name = nameText
			model.setTypeFromTypeString(type)
			model.setDurationFromFields(days, hours, minutes, seconds)
			model.alarm = alarm
			model.autoReset = autoReset
			return model
		}

		/**
		 *
		 * @param timer
		 */
		fun getDurationIfFieldExists(timer: BaseTimer): Long {
			if (timer is Standard) {
				return timer.duration
			}
			return 0

		}
	}

}
