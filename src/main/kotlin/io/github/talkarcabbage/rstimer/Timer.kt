package io.github.talkarcabbage.rstimer

import java.time.Duration
import java.util.HashMap
import java.util.logging.Logger

import io.github.talkarcabbage.logger.LoggerManager

/**
 *
 * @author Talkarcabbage
 */
open class Timer
/**
 *
 * @param startingTime - The system time the timer started at.
 * @param durationTotal - The total time the timer should run for. This is used to reset the timer and check for completion.
 * @param name - A name for the timer. This is used as a label in the GUI.
 */
(@field:Volatile internal var startingTime: Long, @field:Volatile var duration: Long, @field:Volatile var name: String, @field:Volatile var tab: Int) {

	@Volatile
	var timerType: TimerType
		internal set

	/**
	 * Returns a new Duration object that represents this timer's duration
	 * @return
	 */
	val durationObject: Duration
		get() = Duration.ofMillis(this.duration)

	/**
	 * Returns the percentage of this timer's progress toward completion, for setting progress bars.
	 * @return
	 */
	val percentageComplete: Int
		get() {
			val rawPercentage = 100*(System.currentTimeMillis()-startingTime)/duration
			return if (rawPercentage >= 100) 100 else rawPercentage.toInt()
		}

	/**
	 * Returns the amount of time in millis until the timer is complete.
	 * @return
	 */
	val timeRemaining: Long
		get() = startingTime+duration-System.currentTimeMillis()

	/**
	 * Returns the type of the timer.
	 * @return
	 */
	val timerTypeString: String
		get() = timerType.toString().toLow()

	/**
	 * This should not be overridden. Override getDataMap and getNewTimerTypeString.
	 * @return
	 */
	val newTimerSaveText: String
		get() {
			val sb = StringBuilder()
			sb.append("\t$newTimerTypeString {\n")
			val map = dataMap
			for ((key, value) in map) {
				sb.append("\t\t$key:$value\n")
			}
			sb.append("\t"+"}"+"\n")
			return sb.toString()
		}

	/**
	 * This should always be overridden. It defines the way the timer is saved.
	 * @return
	 */
	open val dataMap: Map<String, String>
		get() {
			val map = HashMap<String, String>(8)
			map["name"] = this.name
			map["latestreset"] = this.startingTime.toString()
			map["duration"] = this.duration.toString()
			map["tab"] = this.tab.toString()
			map["audio"] = "none"
			return map
		}

	/**
	 * This should always be overridden. It defines the way the timer is saved.
	 * @return
	 */
	open//NOSONAR
	val newTimerTypeString: String
		get() = "Standard"

	enum class TimerType {
		STANDARD,
		PERIODIC,
		MONTHLY
	}

	init {
		timerType = TimerType.STANDARD
	}

	// Standard getters and setters

	fun getStartingTime(): Double {
		return startingTime.toDouble()
	}

	fun setStartingTime(startingTime: Long) {
		this.startingTime = startingTime
	}

	//End standard getters and setters

	override fun equals(obj: Any?): Boolean { //NOSONAR
		if (this===obj)
			return true
		if (obj==null)
			return false
		if (javaClass!=obj.javaClass)
			return false
		val other = obj as Timer?
		if (java.lang.Double.doubleToLongBits(duration.toDouble())!=java.lang.Double
						.doubleToLongBits(other!!.duration.toDouble()))
			return false
		if (name==null) {
			if (other.name!=null)
				return false
		} else if (name!=other.name)
			return false
		return if (java.lang.Double.doubleToLongBits(startingTime.toDouble())!=java.lang.Double
						.doubleToLongBits(other.startingTime.toDouble())) false else tab==other.tab
	}

	override fun toString(): String {
		return ("Timer [startingTime="+startingTime+", duration="+duration
				+", name="+name+", tab="+tab+"]")
	}

	/**
	 * Resets the timer to a status of incomplete. For normal timers, this should be 0% complete.
	 * For abnormal timers, this should set their next completion to the next applicable reset.
	 * This should be overridden.
	 */
	open fun resetTimer() {
		setStartingTime(System.currentTimeMillis())
		logger.fine { "Set normal timer with data: $this" }
	}

	/**
	 * Resets the timer to a status of complete. This is typically/most easily done by setting the start-time to 0.
	 * This may be overridden.
	 */
	open fun resetTimerComplete() {
		setStartingTime(0)
	}

	companion object {

		private val logger = LoggerManager.getInstance().getLogger("Timer")

		val DAY_LENGTH: Long = 86400000
		val WEEK_LENGTH: Long = 604800000
	}

}