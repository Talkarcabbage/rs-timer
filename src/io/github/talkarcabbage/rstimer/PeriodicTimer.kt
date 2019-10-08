package io.github.talkarcabbage.rstimer

import java.util.HashMap
import java.util.logging.Logger

import io.github.talkarcabbage.logger.LoggerManager

/**
 *
 * @author Talkarcabbage
 */
class PeriodicTimer(targetTime: Long, durationTotal: Long, name: String, tab: Int) : Timer(targetTime, durationTotal, name, tab) {

	override val newTimerTypeString: String
		get() = if (duration==Timer.WEEK_LENGTH) "Weekly" else "Daily"

	override val dataMap: Map<String, String>
		get() {
			val map = HashMap<String, String>(8)
			map["name"] = this.name
			map["latestreset"] = this.startingTime.toString()
			map["tab"] = this.tab.toString()
			map["audio"] = "none"
			return map
		}

	init {
		timerType = Timer.TimerType.PERIODIC
	}

	override fun resetTimer() {

		if (duration==Timer.WEEK_LENGTH) {
			setStartingTime((System.currentTimeMillis()+Timer.DAY_LENGTH)/Timer.WEEK_LENGTH*Timer.WEEK_LENGTH-Timer.DAY_LENGTH)
			logger.fine { "Set weekly timer timer with data: $this" }
		} else if (duration==Timer.DAY_LENGTH) {
			setStartingTime(System.currentTimeMillis()/Timer.DAY_LENGTH*Timer.DAY_LENGTH)
			logger.fine { "Set daily timer timer with data: $this" }
		} else {
			logger.severe { "Failed to match timer type for periodic timer: $this" }
		}
	}

	override fun resetTimerComplete() {
		setStartingTime(0)
	}

	companion object {

		private val logger = LoggerManager.getInstance().getLogger("PeriodicTimer")
	}

}