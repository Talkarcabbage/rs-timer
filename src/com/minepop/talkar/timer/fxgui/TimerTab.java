package com.minepop.talkar.timer.fxgui;

import javafx.scene.Node;
import javafx.scene.control.Tab;

/**
 * 
 * @author Talkarcabbage
 *
 */
public class TimerTab extends Tab {

	/**
	 * This is an ID number used to associate timers with this tab. This allows
	 * the order of the tabs to change without screwing up the timer data.
	 */
	int tabID;
	
	public TimerTab() {
		// TODO Auto-generated constructor stub
	}

	public TimerTab(String arg0) {
		super(arg0);
		// TODO Auto-generated constructor stub
	}

	public TimerTab(String arg0, Node arg1) {
		super(arg0, arg1);
		// TODO Auto-generated constructor stub
	}
}