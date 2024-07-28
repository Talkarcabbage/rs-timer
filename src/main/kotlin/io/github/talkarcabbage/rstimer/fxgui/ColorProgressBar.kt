package io.github.talkarcabbage.rstimer.fxgui

import java.util.logging.Logger

import io.github.talkarcabbage.logger.LoggerManager
import javafx.scene.control.ProgressBar

/**
 *
 * @author Talkarcabbage
 */
class ColorProgressBar : ProgressBar { //NOSONAR We don't care about number of parent classes

	constructor() : super() {}

	constructor(arg0: Double) : super(arg0) {}

	/**
	 * Set the style class of the progress bar to the specified complete/incomplete identifier
	 * @param styleClass
	 */
	fun setTrackStyleClass(styleClass: String) {
		this.styleClass.clear()
		this.styleClass.add("progress-bar")
		this.styleClass.add(styleClass)
		this.applyCss()
	}

	companion object {

		internal val logger = LoggerManager.getInstance().getLogger("FXColorProgressBar")
	}
}