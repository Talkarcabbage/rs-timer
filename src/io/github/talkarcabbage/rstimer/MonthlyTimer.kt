package io.github.talkarcabbage.rstimer

import java.util.Calendar
import java.util.HashMap
import java.util.TimeZone
import java.util.logging.Logger

import io.github.talkarcabbage.logger.LoggerManager

/**
 *
 * @author Talkarcabbage
 */
class MonthlyTimer(targetTime: Long, durationTotal: Long, name: String, tab: Int) : Timer(targetTime, durationTotal, name, tab) {

	override val newTimerTypeString: String
		get() = "Monthly"

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
		timerType = Timer.TimerType.MONTHLY
	}

	override fun resetTimer() {
		val startC = Calendar.getInstance(TimeZone.getTimeZone("GMT"))
		startC.set(Calendar.DATE, 1)
		startC.set(Calendar.HOUR, 0)
		startC.set(Calendar.AM_PM, Calendar.AM)
		startC.set(Calendar.MINUTE, 0)
		startC.set(Calendar.SECOND, 0)
		startC.set(Calendar.MILLISECOND, 0)

		val endC = Calendar.getInstance(TimeZone.getTimeZone("GMT"))
		endC.set(Calendar.DATE, 1)
		endC.set(Calendar.HOUR, 0)
		endC.set(Calendar.AM_PM, Calendar.AM)
		endC.set(Calendar.MINUTE, 0)
		endC.set(Calendar.SECOND, 0)
		endC.set(Calendar.MILLISECOND, 0)
		endC.set(Calendar.MONTH, endC.get(Calendar.MONTH)+1)

		this.startingTime = startC.timeInMillis
		this.duration = endC.timeInMillis-startC.timeInMillis
	}

	override fun resetTimerComplete() {
		this.startingTime = 0
	}

	companion object {


		private val logger = LoggerManager.getInstance().getLogger("MonthlyTimer")
	}

}
