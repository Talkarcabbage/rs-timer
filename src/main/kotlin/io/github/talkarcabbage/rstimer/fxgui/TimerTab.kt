package io.github.talkarcabbage.rstimer.fxgui

import javafx.scene.Node
import javafx.scene.control.Tab

/**
 *
 * @author Talkarcabbage
 */
//TODO timertab class
class TimerTab : Tab {

	/**
	 * This is an ID number used to associate timers with this tab. This allows
	 * the order of the tabs to change without screwing up the timer data.
	 */
	internal var tabID: Int = 0

	constructor() {

	}

	constructor(arg0: String) : super(arg0) {

	}

	constructor(arg0: String, arg1: Node) : super(arg0, arg1) {

	}
}