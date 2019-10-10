package io.github.talkarcabbage.rstimer.newtimers

import java.util.HashMap
import java.util.logging.Logger

import io.github.talkarcabbage.logger.LoggerManager
import io.github.talkarcabbage.rstimer.FXController
import java.awt.SystemTray
import java.awt.TrayIcon

abstract class NewTimer {

	var audio = false
	@Volatile
	var name: String
	var tab = 0
	var autoreset = false

	/**
	 * Returns the pseudojsonyaml representation of this timer, for saving.
	 * This method does not need to be overridden typically.
	 * @return The string representation of this Timer, for saving.
	 */
	val timerSaveString: String
		get() {
			val sb = StringBuilder()
			sb.append("\t$timerTypeString {\n")
			val map = timerDataMap
			for ((key, value) in map) {
				sb.append("\t\t$key:$value\n")
			}
			sb.append("\t"+"}"+"\n")
			return sb.toString()
		}

	/**
	 * Returns a String representing the timer's type for generating save-file entries.
	 * @return The type String
	 */
	abstract val timerTypeString: String

	/**
	 * Returns a map containing the data associated with this timer.
	 * This is used for mapping the data to a save-file entry.
	 * This should be overridden and super()'d plus any additional data.
	 * @return The Map of data in the format variable,value
	 */
	open val timerDataMap: Map<String, String>
		get() {
			val map = HashMap<String, String>(8)
			map[MAP_NAME] = this.name
			map[MAP_TAB] = this.tab.toString()
			map[MAP_AUDIO] = this.audio.toString()
			map[MAP_AUTO_RESET] = this.autoreset.toString()
			return map
		}


	/**
	 * Returns the tooltip text for this timer, for display on the GUI.
	 * @return
	 */
	abstract val tooltipText: String

	/**
	 * Used to fill in the progress bar
	 * @return Progress percentage
	 */
	abstract val percentageComplete: Int

	/**
	 * Used to get the time remaining for the tooltip
	 * @return The remaining time.
	 */
	abstract val timeRemaining: Long

	constructor(name: String, tabID: Int, audio: Boolean) { //TODO consider if worth to add autoreset since method mostly unused
		this.name = name
		this.tab = tabID
		this.audio = audio
	}

	constructor(dataMap: Map<String, String>) {
		this.name = "MISSING" //Some arbitrary defaults in case of missing data
		for ((key, value) in dataMap) {
			when (key) {
				//NOSONAR we don't handle all cases here on purpose, thanks
				MAP_NAME -> this.name = value
				MAP_TAB -> this.tab = Integer.parseInt(value)
				MAP_AUDIO -> this.audio = java.lang.Boolean.parseBoolean(value)
				MAP_AUTO_RESET -> this.autoreset = value.toBoolean()
			}
		}
	}

	/**
	 * Used by timers that require special actions when the GUI ticks.
	 * Not currently in substantial use
	 */
	fun tickTimer() {

	}

	/**
	 * Called by the GUI when it detects a timer has gone from incomplete to complete
	 * Normally results in the gui changing the progress bar to orange. Used for
	 * autoreset and etc.
	 */
	open fun onTimerComplete() {
		if (audio) {
			SystemTray.getSystemTray().trayIcons[0]?.displayMessage("RS Timer", "The timer ${this.name} just finished!", TrayIcon.MessageType.INFO)
		}
		if (autoreset) {
			this.resetTimer()
		}
	}

	/**
	 * Called when the timer needs to be reset.
	 */
	abstract fun resetTimer()

	/**
	 * Called when the timer needs to be set as complete.
	 */
	abstract fun resetTimerComplete()

	override fun toString(): String {
		return "${this.javaClass.simpleName}(audio=$audio, name='$name', tab=$tab, timerTypeString='$timerTypeString', tooltipText='$tooltipText', percentageComplete=$percentageComplete, timeRemaining=$timeRemaining)"
	}

	companion object {

		val DAY_LENGTH_MILLIS: Long = 86400000
		val WEEK_LENGTH_MILLIS: Long = 604800000
		internal var logger = LoggerManager.getInstance().getLogger("Timer")

		const val MAP_NAME = "name"
		const val MAP_AUDIO = "audio"
		const val MAP_TAB = "tab"
		const val MAP_DURATION = "duration"
		const val MAP_LATEST_RESET = "latestreset"
		const val MAP_AUTO_RESET = "autoreset"

		/**
		 * Format the given time value in ms for display in hh:mm:ss format on the GUI tooltips.
		 * @param time Time in ms
		 * @return The formatted time string
		 */
		fun formatTimeRemaining(time: Long): String {
			if (time <= 0) {
				return "Complete!"
			} else {
				val timeSeconds = time/1000
				if (time> DAY_LENGTH_MILLIS) {
					return (timeSeconds/86400).toString()+"d "+Math.floor((timeSeconds%86400).toDouble()/3600).toLong()+":"+Math.floor(timeSeconds.toDouble()%3600/60).toLong()+":"+timeSeconds%60
				} else {
					return Math.round(Math.floor(timeSeconds.toDouble()/3600)).toString()+":"+Math.floor(timeSeconds.toDouble()%3600/60).toLong()+":"+timeSeconds%60
				}
			}
		}

		fun sanitizeValueRangeMin(min: Long, value: Long): Long {
			return if (value > min) value else min
		}

		fun sanitizeValueRangeMax(max: Long, value: Long): Long {
			return if (value < max) value else max
		}
	}

}
