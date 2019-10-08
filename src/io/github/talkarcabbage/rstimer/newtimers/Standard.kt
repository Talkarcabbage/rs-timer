package io.github.talkarcabbage.rstimer.newtimers

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
	 * Creates a timer from the given map of timer data. Invalid properties will be ignored with a console warning and missing values will be defaulted.
	 * @param dataMap
	 */
	constructor(dataMap: Map<String, String>) : super("MISSING", 0, false) {
		latestReset = 0 //Arbitrary default values
		duration = 1000 //Arbitrary default values
		for ((key, value) in dataMap) {
			try {
				when (key) {
					"name" -> this.name = value
					"audio" -> this.audio = java.lang.Boolean.parseBoolean(value)
					"tab" -> this.tab = Integer.parseInt(value)
					"latestreset" -> this.latestReset = java.lang.Long.parseLong(value)
					"duration" -> this.duration = java.lang.Long.parseLong(value)
					else -> NewTimer.logger.warning("Unknown property type found while parsing Standard timer:$key")
				}
			} catch (e: NumberFormatException) {
				NewTimer.logger.severe("Invalid timer number value $value for property $key")
			}

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
