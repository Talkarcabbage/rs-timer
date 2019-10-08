package io.github.talkarcabbage.rstimer.newtimers

import java.util.Calendar
import java.util.TimeZone

class Monthly : NewTimer {

	var latestReset: Long = 0
		internal set
	internal var duration: Long = 0

	override val timerTypeString: String
		get() = "Monthly"

	override val tooltipText: String
		get() = "Monthly Timer\n"+NewTimer.formatTimeRemaining(timeRemaining)

	override val percentageComplete: Int
		get() {
			val rawPercentage = 100*(System.currentTimeMillis()-latestReset)/duration
			return if (rawPercentage >= 100) 100 else rawPercentage.toInt()
		}

	override val timeRemaining: Long
		get() = latestReset+duration-System.currentTimeMillis()

	override val timerDataMap: Map<String, String>
		get() {
			val map = super.timerDataMap.toMutableMap()
			map["latestreset"] = latestReset.toString()
			return map
		}


	/**
	 * Returns the duration between the given starting time's start of the month and next month.
	 * This value is based on the current latestReset value.
	 * @return The time difference
	 */
	val durationToNextMonth: Long
		get() {
			val startC = getBeginningOfMonthFor(latestReset)
			val endC = getEndOfMonthFor(latestReset)
			return endC.timeInMillis-startC.timeInMillis
		}

	constructor(name: String, tabID: Int, audio: Boolean, latestReset: Long) : super(name, tabID, audio) {
		this.latestReset = latestReset
		duration = durationToNextMonth
	}

	/**
	 * Creates a timer from the given map of timer data. Invalid properties will be ignored with a console warning and missing values will be defaulted.
	 * @param dataMap
	 */
	constructor(dataMap: Map<String, String>) : super("MISSING", 0, false) {
		latestReset = 0 //Arbitrary default values
		duration = durationToNextMonth //Because daddy doesn't like dividing by zero
		for ((key, value) in dataMap) {
			try {
				when (key) {
					"name" -> this.name = value
					"audio" -> this.audio = java.lang.Boolean.parseBoolean(value)
					"tab" -> this.tab = Integer.parseInt(value)
					"latestreset" -> this.latestReset = java.lang.Long.parseLong(value)
					else -> NewTimer.logger.warning("Unknown property type found while parsing Monthly timer:$key")
				}
			} catch (e: NumberFormatException) {
				NewTimer.logger.severe("Invalid timer number value $value for property $key")
			}

		}
	}//Some arbitrary defaults in case of missing data

	override fun resetTimer() {
		val startC = getBeginningOfMonthFor(System.currentTimeMillis())
		val endC = getEndOfMonthFor(System.currentTimeMillis())

		this.latestReset = startC.timeInMillis
		this.duration = endC.timeInMillis-startC.timeInMillis
	}

	override fun resetTimerComplete() {
		this.latestReset = 0
	}

	companion object {

		/**
		 * Returns a Calendar object representing the 0:00 first day of the month for a given time.
		 * @param time The time
		 * @return The Calendar
		 */
		fun getBeginningOfMonthFor(time: Long): Calendar {
			val startC = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
			startC.timeInMillis = time
			startC.set(Calendar.DATE, 1)
			startC.set(Calendar.HOUR, 0)
			startC.set(Calendar.AM_PM, Calendar.AM)
			startC.set(Calendar.MINUTE, 0)
			startC.set(Calendar.SECOND, 0)
			startC.set(Calendar.MILLISECOND, 0)
			return startC
		}

		/**
		 * Returns a Calendar object representing the 0:00 first day of the **next** month for a given time.
		 * @param time The time
		 * @return The Calendar
		 */
		fun getEndOfMonthFor(time: Long): Calendar {
			val endC = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
			endC.timeInMillis = time
			endC.set(Calendar.DATE, 1)
			endC.set(Calendar.HOUR, 0)
			endC.set(Calendar.AM_PM, Calendar.AM)
			endC.set(Calendar.MINUTE, 0)
			endC.set(Calendar.SECOND, 0)
			endC.set(Calendar.MILLISECOND, 0)
			endC.set(Calendar.MONTH, endC.get(Calendar.MONTH)+1)
			return endC
		}
	}


}
