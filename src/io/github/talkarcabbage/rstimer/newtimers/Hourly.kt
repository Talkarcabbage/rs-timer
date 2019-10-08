package io.github.talkarcabbage.rstimer.newtimers

class Hourly : NewTimer {

	var duration: Long = 0
		internal set

	override// TODO Auto-generated method stub
	val timerTypeString: String
		get() = ""

	override// TODO Auto-generated method stub
	val tooltipText: String
		get() = ""

	override// TODO Auto-generated method stub
	val percentageComplete: Int
		get() = 0

	override// TODO Auto-generated method stub
	val timeRemaining: Long
		get() = 0

	constructor(name: String, tabID: Int, audio: Boolean) : super(name, tabID, audio) {
		throw UnsupportedOperationException("Not yet implemented")
	}

	constructor(dataMap: Map<String, String>) : super(dataMap) {
		throw UnsupportedOperationException("Not yet implemented")
	}

	override fun resetTimer() {
		// TODO Auto-generated method stub

	}

	override fun resetTimerComplete() {
		// TODO Auto-generated method stub

	}

}
