package io.github.talkarcabbage.rstimer.newtimers

import java.awt.SystemTray
import java.awt.TrayIcon

class Standard : NewTimer {

	var latestReset: Long = 0
		internal set
	var duration: Long = 0
		internal set

	override val timerTypeString: String
		get() = "Standard"

	override val timerDataMap: Map<String, String>
		get() {
			val map = super.timerDataMap.toMutableMap()
			map["latestreset"] = latestReset.toString()
			map["duration"] = duration.toString()
			return map
		}

	override val tooltipText: String
		get() = "Standard Timer\n"+NewTimer.formatTimeRemaining(timeRemaining)

	override val percentageComplete: Int
		get() {
			val rawPercentage = 100*(System.currentTimeMillis()-latestReset)/duration
			return if (rawPercentage >= 100) 100 else rawPercentage.toInt()
		}

	override val timeRemaining: Long
		get() = latestReset+duration-System.currentTimeMillis()

	constructor(name: String, tabID: Int, audio: Boolean, latestReset: Long, duration: Long) : super(name, tabID, audio) {
		this.latestReset = latestReset
		this.duration = duration
	}

	/**
	 * Creates a timer from the given map of timer data. Data unique to all timers will be
	 * initialized via the super constructor.
	 * @param dataMap
	 */
	constructor(dataMap: Map<String, String>) : super(dataMap) {
		duration = 1000 //Arbitrary default values
		for ((key, value) in dataMap) {
			try {
				when (key) {
					MAP_LATEST_RESET -> this.latestReset = java.lang.Long.parseLong(value)
					MAP_DURATION -> this.duration = java.lang.Long.parseLong(value)
				}
			} catch (e: NumberFormatException) {
				NewTimer.logger.severe("Invalid timer number value $value for property $key")
			}
		}
		if (duration == 0L) {
			duration = 1000
		} //Sanitze

		logger.fine("Initialized a timer: ${this.toString()}")

	}

	override fun onTimerComplete() {
		if (audio) {
			SystemTray.getSystemTray().trayIcons[0]?.displayMessage("RS Timer", "The timer ${this.name} just finished!", TrayIcon.MessageType.INFO)
		}
		if (autoreset) {
			val durationsSinceReset = (System.currentTimeMillis()-latestReset)/duration
			this.latestReset += this.duration*durationsSinceReset //More precise autoresets
		}
	}

	override fun resetTimer() {
		this.latestReset = System.currentTimeMillis()
		NewTimer.logger.fine { "Reset standard timer with data: $this" }
	}

	override fun resetTimerComplete() {
		this.latestReset = 0
		NewTimer.logger.fine { "Reset-complete standard timer with data: $this" }
	}

}
