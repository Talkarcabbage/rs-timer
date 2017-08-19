package io.github.talkarcabbage.rstimer.tests;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.junit.Test;

import io.github.talkarcabbage.rstimer.newtimers.NewTimer;
import io.github.talkarcabbage.rstimer.newtimers.Standard;
import io.github.talkarcabbage.rstimer.persistence.LoadManager;

public class TimersTest {
	
	@Test
	public void testFormatTimeRemaining() {
		assertEquals("Complete!", NewTimer.formatTimeRemaining(0));
		assertEquals("Complete!", NewTimer.formatTimeRemaining(Long.MIN_VALUE));
		assertEquals("0:0:1", NewTimer.formatTimeRemaining(1000));
		assertEquals("0:1:0", NewTimer.formatTimeRemaining(60000));
		assertEquals("1:0:0", NewTimer.formatTimeRemaining(3600000));
		assertEquals("0:1:1", NewTimer.formatTimeRemaining(61000));
	}
	
	@Test
	public void testGetTimerDataMapFromList() { //Necessary for verifying the constructor functionality
		List<String> list = createTimerData("Standard");
		Map<String, String> theMap = LoadManager.getTimerDataMapFromList(list);
		for (Entry<String, String> entry : theMap.entrySet()) {
			System.out.println(entry.getKey() + "," + entry.getValue());
		}
		assertEquals(theMap.get("name"), "Wicked Hood");
		assertEquals(theMap.get("audio"), "false");
		assertEquals(theMap.get("tab"), "0");
		assertEquals(theMap.get("latestreset"), "1502755200000");
	}
	
	public List<String> createTimerData(String type) {
		ArrayList<String> list = new ArrayList<>();
		list.add("\t" + type + " {\n");
		list.add("\t\tname:Wicked Hood\n");
		list.add("\t\taudio:false\n");
		list.add("\t\ttab:0\n");
		list.add("\t\tlatestreset:1502755200000\n");
		list.add("\t\tduration:60000\n");
		list.add("\t}\n");
		return list;
	}
	
	@Test
	public void testStandardConstructors() throws InterruptedException {
		createTimerData("Standard");
		Standard standardTimer = new Standard(LoadManager.getTimerDataMapFromList(createTimerData("Standard")));
		assertEquals(standardTimer.getName(), "Wicked Hood");
		assertEquals(standardTimer.getTab(), 0);
		assertEquals(standardTimer.getPercentageComplete(), 100);
		assertEquals(standardTimer.getTimeRemaining()/1000, ((1502755260000L)-System.currentTimeMillis())/1000);
	}
	
	

}
