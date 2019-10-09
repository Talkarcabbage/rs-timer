package io.github.talkarcabbage.rstimer.newtimers

class Daily : NewTimer {

	var latestReset: Long = 0
		internal set

	override val timerTypeString: String
		get() = "Daily"

	override val tooltipText: String
		get() = "Daily Timer\n"+NewTimer.formatTimeRemaining(timeRemaining)

	override val percentageComplete: Int
		get() {
			val rawPercentage = 100*(System.currentTimeMillis()-latestReset)/NewTimer.DAY_LENGTH_MILLIS
			return if (rawPercentage >= 100) 100 else rawPercentage.toInt()
		}

	override val timeRemaining: Long
		get() = latestReset+NewTimer.DAY_LENGTH_MILLIS-System.currentTimeMillis()

	override val timerDataMap: Map<String, String>
		get() {
			val map = super.timerDataMap.toMutableMap()
			map["latestreset"] = latestReset.toString()
			return map
		}

	constructor(name: String, tabID: Int, audio: Boolean, latestReset: Long) : super(name, tabID, audio) {
		this.latestReset = latestReset
	}

	/**
	 * Creates a timer from the given map of timer data. Invalid properties will be ignored with a console warning and missing values will be defaulted.
	 * @param dataMap
	 */
	constructor(dataMap: Map<String, String>) : super(dataMap) {
		for ((key, value) in dataMap) {
			try {
				when (key) {
					MAP_LATEST_RESET -> this.latestReset = java.lang.Long.parseLong(value)
				}
			} catch (e: NumberFormatException) {
				NewTimer.logger.severe("Invalid timer number value $value for property $key")
			}

		}
	}

	override fun resetTimer() {
		latestReset = System.currentTimeMillis()/NewTimer.DAY_LENGTH_MILLIS*NewTimer.DAY_LENGTH_MILLIS
		NewTimer.logger.fine { "Reset daily timer timer with data: $this" }
	}

	override fun resetTimerComplete() {
		this.latestReset = 0

	}


}
