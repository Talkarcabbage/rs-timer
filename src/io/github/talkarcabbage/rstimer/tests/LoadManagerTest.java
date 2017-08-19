package io.github.talkarcabbage.rstimer.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import io.github.talkarcabbage.rstimer.newtimers.Daily;
import io.github.talkarcabbage.rstimer.newtimers.Monthly;
import io.github.talkarcabbage.rstimer.newtimers.NewTimer;
import io.github.talkarcabbage.rstimer.newtimers.Standard;
import io.github.talkarcabbage.rstimer.newtimers.Weekly;
import io.github.talkarcabbage.rstimer.persistence.LoadManager;
import javafx.scene.control.Tab;
import javafx.scene.layout.GridPane;

public class LoadManagerTest {
	
	@Test
	public void testBetterParseTabs() {
		List<String> tabData = createTestTabData();
		List<Tab> tabList = LoadManager.betterParseTabs(tabData);
		assertEquals(tabList.size(), 5);
		assertEquals(tabList.get(0).getText(), "Main");
		assertEquals(tabList.get(1).getText(), "Iron");
		assertEquals(tabList.get(2).getText(), "Weekly");
		assertEquals(tabList.get(3).getText(), "Monthly");
		assertEquals(tabList.get(4).getText(), "D&D");
		assertTrue(tabList.get(0).getContent() instanceof GridPane);
	}
	
	@Test
	public void testBetterLoadTimers() {
		List<NewTimer> timerList = LoadManager.betterLoadTimers(createSampleTimerData());
		assertEquals(timerList.get(0).getName(), "Wicked Hood");
		assertEquals(timerList.size(), 4);
		assertTrue(timerList.get(0) instanceof Daily);
		assertTrue(timerList.get(1) instanceof Standard);
		assertTrue(timerList.get(2) instanceof Weekly);
		assertTrue(timerList.get(3) instanceof Monthly);
		assertEquals(((Standard)timerList.get(1)).getLatestReset(), 1501987245899L);
	}
	
	public List<String> createSampleTimerData() {
		ArrayList<String> list = new ArrayList<>();
		list.add("timers {");
		list.add("	Daily {");
		list.add("		name:Wicked Hood");
		list.add("		audio:false");
		list.add("		tab:0");
		list.add("		latestreset:1502755200000");
		list.add("	}");
		list.add("	Standard {");
		list.add("		name:Ports");
		list.add("		duration:43200000");
		list.add("		audio:none");
		list.add("		tab:0");
		list.add("		latestreset:1501987245899");
		list.add("	}");
		list.add("	Weekly {");
		list.add("		name:Claim Pig");
		list.add("		audio:false");
		list.add("		tab:2");
		list.add("		latestreset:1501027200000");
		list.add("	}");
		list.add("	Monthly {");
		list.add("		name:God Statues");
		list.add("		audio:false");
		list.add("		tab:3");
		list.add("		latestreset:1501545600000");
		list.add("	}");
		list.add("};");

		return list;
	}
	

	public List<String> createTestTabData() {
		ArrayList<String> list = new ArrayList<>();
		list.add("tabs {\n");
		list.add("	tab {\n");
		list.add("		title:Main\n");
		list.add("		rows:0\n");
		list.add("		columns:2\n");
		list.add("	}\n");
		list.add("	tab {\n");
		list.add("		title:Iron\n");
		list.add("		rows:6\n");
		list.add("		columns:0\n");
		list.add("	}\n");
		list.add("	tab {\n");
		list.add("		title:Weekly\n");
		list.add("		rows:6\n");
		list.add("		columns:0\n");
		list.add("	}\n");
		list.add("	tab {\n");
		list.add("		title:Monthly\n");
		list.add("		rows:6\n");
		list.add("		columns:0\n");
		list.add("	}\n");
		list.add("	tab {\n");
		list.add("		title:D&D\n");
		list.add("		rows:6\n");
		list.add("		columns:0\n");
		list.add("	}\n");
		list.add("};\n");
		return list;
	}
	
}
