package io.github.talkarcabbage.rstimer.timers

class Weekly : BaseTimer {

	var latestReset: Long = 0
		internal set

	override val timerTypeString: String
		get() = "Weekly"

	override val tooltipText: String
		get() = "Weekly Timer\n"+formatTimeRemaining(timeRemaining)

	override val percentageComplete: Int
		get() {
			val rawPercentage = 100*(System.currentTimeMillis()-latestReset)/WEEK_LENGTH_MILLIS
			return if (rawPercentage >= 100) 100 else rawPercentage.toInt()
		}

	override val timeRemaining: Long
		get() = latestReset+WEEK_LENGTH_MILLIS-System.currentTimeMillis()

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
	 * Creates a timer from the given map of timer data.
	 * @param dataMap
	 */
	constructor(dataMap: Map<String, String>) : super(dataMap) {
		for ((key, value) in dataMap) {
			try {
				when (key) {
					MAP_LATEST_RESET -> this.latestReset = java.lang.Long.parseLong(value)
				}
			} catch (e: NumberFormatException) {
				logger.severe("Invalid timer number value $value for property $key")
			}
		}
	}

	override fun resetTimer() {
		latestReset = (System.currentTimeMillis()+DAY_LENGTH_MILLIS)/WEEK_LENGTH_MILLIS*WEEK_LENGTH_MILLIS-DAY_LENGTH_MILLIS
		logger.fine { "Reset weekly timer timer with data: $this" }

	}

	override fun resetTimerComplete() {
		latestReset = 0
	}

}
