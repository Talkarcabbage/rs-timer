package tests

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

import java.util.ArrayList

import io.github.talkarcabbage.rstimer.timers.Daily
import io.github.talkarcabbage.rstimer.timers.Monthly
import io.github.talkarcabbage.rstimer.timers.Standard
import io.github.talkarcabbage.rstimer.timers.Weekly
import io.github.talkarcabbage.rstimer.persistence.LoadManager
import javafx.scene.layout.GridPane

class LoadManagerTest {

	@Test
	fun testBetterParseTabs() {
		val tabData = createTestTabData()
		val tabList = LoadManager.parseTabsFromFileData(tabData)
		assertEquals(tabList.size, 5)
		assertEquals(tabList[0].text, "Main")
		assertEquals(tabList[1].text, "Iron")
		assertEquals(tabList[2].text, "Weekly")
		assertEquals(tabList[3].text, "Monthly")
		assertEquals(tabList[4].text, "D&D")
		assertTrue(tabList[0].content is GridPane)
	}

	@Test
	fun testBetterLoadTimers() {
		val timerList = LoadManager.loadTimersFromFileData(createSampleTimerData())
		assertEquals(timerList[0].name, "Wicked Hood")
		assertEquals(timerList.size, 4)
		assertTrue(timerList[0] is Daily)
		assertTrue(timerList[1] is Standard)
		assertTrue(timerList[2] is Weekly)
		assertTrue(timerList[3] is Monthly)
		assertEquals((timerList[1] as Standard).latestReset, 1501987245899L)
	}

	fun createSampleTimerData(): List<String> {
		val list = ArrayList<String>()
		list.add("timers {")
		list.add("	Daily {")
		list.add("		name:Wicked Hood")
		list.add("		audio:false")
		list.add("		tab:0")
		list.add("		latestreset:1502755200000")
		list.add("	}")
		list.add("	Standard {")
		list.add("		name:Ports")
		list.add("		duration:43200000")
		list.add("		audio:none")
		list.add("		tab:0")
		list.add("		latestreset:1501987245899")
		list.add("	}")
		list.add("	Weekly {")
		list.add("		name:Claim Pig")
		list.add("		audio:false")
		list.add("		tab:2")
		list.add("		latestreset:1501027200000")
		list.add("	}")
		list.add("	Monthly {")
		list.add("		name:God Statues")
		list.add("		audio:false")
		list.add("		tab:3")
		list.add("		latestreset:1501545600000")
		list.add("	}")
		list.add("};")

		return list
	}


	fun createTestTabData(): List<String> {
		val list = ArrayList<String>()
		list.add("tabs {\n")
		list.add("	tab {\n")
		list.add("		title:Main\n")
		list.add("		rows:0\n")
		list.add("		columns:2\n")
		list.add("	}\n")
		list.add("	tab {\n")
		list.add("		title:Iron\n")
		list.add("		rows:6\n")
		list.add("		columns:0\n")
		list.add("	}\n")
		list.add("	tab {\n")
		list.add("		title:Weekly\n")
		list.add("		rows:6\n")
		list.add("		columns:0\n")
		list.add("	}\n")
		list.add("	tab {\n")
		list.add("		title:Monthly\n")
		list.add("		rows:6\n")
		list.add("		columns:0\n")
		list.add("	}\n")
		list.add("	tab {\n")
		list.add("		title:D&D\n")
		list.add("		rows:6\n")
		list.add("		columns:0\n")
		list.add("	}\n")
		list.add("};\n")
		return list
	}

}
