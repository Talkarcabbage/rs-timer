package io.github.talkarcabbage.rstimer.newtimers

class Hourly : NewTimer {

	var hourStart: Long = 0
	var hourDelay: Long = 0

	override
	val timerTypeString: String
		get() = "Hourly"

	override
	val tooltipText: String
		get() = "Hourly Timer\n${formatTimeRemaining(timeRemaining)}"

	override
	val percentageComplete: Int
		get() = ((System.currentTimeMillis()-hourStart).toDouble()/(hourDelay * HOUR_LENGTH_MILLIS)*100).toInt()

	override
	val timeRemaining: Long
		get() = 0

	constructor(name: String, tabID: Int, audio: Boolean) : super(name, tabID, audio) {
		throw UnsupportedOperationException("Not yet implemented")
	}

	constructor(dataMap: Map<String, String>) : super(dataMap) {
		throw UnsupportedOperationException("Not yet implemented")
		for ((key, value) in dataMap) {
			when (key) {
				MAP_HOUR_DELAY -> hourDelay = value.toLong()
				MAP_HOUR_START -> hourStart = value.toLong()
			}
		}
	}

	override fun resetTimer() {


	}

	override fun resetTimerComplete() {


	}

	companion object {
		const val MAP_HOUR_DELAY = "HourDelay"
		const val MAP_HOUR_START = "HourStart"
	}

}
