package tests

import org.junit.jupiter.api.Assertions.assertEquals
import java.util.ArrayList

import org.junit.jupiter.api.Test

import io.github.talkarcabbage.rstimer.timers.BaseTimer
import io.github.talkarcabbage.rstimer.timers.Standard
import io.github.talkarcabbage.rstimer.persistence.LoadManager

class TimersTest {

	@Test
	fun testFormatTimeRemaining() {
		assertEquals("Complete!", BaseTimer.formatTimeRemaining(0))
		assertEquals("Complete!", BaseTimer.formatTimeRemaining(java.lang.Long.MIN_VALUE))
		assertEquals("0:0:1", BaseTimer.formatTimeRemaining(1000))
		assertEquals("0:1:0", BaseTimer.formatTimeRemaining(60000))
		assertEquals("1:0:0", BaseTimer.formatTimeRemaining(3600000))
		assertEquals("0:1:1", BaseTimer.formatTimeRemaining(61000))
	}

	@Test
	fun testGetTimerDataMapFromList() { //Necessary for verifying the constructor functionality
		val list = createTimerData("Standard")
		val theMap = LoadManager.getTimerDataMapFromList(list)
		for ((key, value) in theMap) {
			println(key+","+value)
		}
		assertEquals(theMap["name"], "Wicked Hood")
		assertEquals(theMap["audio"], "false")
		assertEquals(theMap["tab"], "0")
		assertEquals(theMap["latestreset"], "1502755200000")
	}

	fun createTimerData(type: String): List<String> {
		val list = ArrayList<String>()
		list.add("\t$type {\n")
		list.add("\t\tname:Wicked Hood\n")
		list.add("\t\taudio:false\n")
		list.add("\t\ttab:0\n")
		list.add("\t\tlatestreset:1502755200000\n")
		list.add("\t\tduration:60000\n")
		list.add("\t}\n")
		return list
	}

	@Test
	@Throws(InterruptedException::class)
	fun testStandardConstructors() {
		createTimerData("Standard")
		val standardTimer = Standard(LoadManager.getTimerDataMapFromList(createTimerData("Standard")))
		assertEquals(standardTimer.name, "Wicked Hood")
		assertEquals(standardTimer.tab, 0)
		assertEquals(standardTimer.percentageComplete, 100)
		assertEquals(standardTimer.timeRemaining/1000, (1502755260000L-System.currentTimeMillis())/1000)
	}


}
