package io.github.talkarcabbage.rstimer.fxgui

import java.util.logging.Logger

import io.github.talkarcabbage.logger.LoggerManager
import javafx.geometry.Pos
import javafx.scene.control.Label
import javafx.scene.control.Tooltip
import javafx.scene.layout.GridPane
import javafx.scene.layout.Priority
import javafx.scene.layout.StackPane

/**
 *
 * @author Talkarcabbage
 */
class ProgressPane : StackPane() { //NOSONAR

	var bar: ColorProgressBar
	var labelObject: Label

	var labelText: String
		get() = labelObject.text
		set(text) {
			labelObject.text = text
		}

	init {
		bar = ColorProgressBar()
		labelObject = Label()

		bar.maxWidth = java.lang.Double.MAX_VALUE
		bar.maxHeight = 26.0
		bar.minHeight = 20.0
		bar.prefHeight = 26.0

		labelObject.maxHeight = 30.0
		labelObject.maxWidth = java.lang.Double.MAX_VALUE
		labelObject.prefHeight = 30.0
		labelObject.minHeight = 20.0

		labelObject.text = "Boop"
		labelObject.alignment = Pos.CENTER

		GridPane.setHgrow(this, Priority.ALWAYS)

		this.styleClass.add("progressPane")

		this.children.addAll(bar, labelObject)
		initBarStyle()

		bar.tooltip = Tooltip("")
		labelObject.tooltip = Tooltip("")
	}

	/**
	 * Set the value of this progresspane's progress bar. This value should be supplied as a
	 * percentage value between 0-100 inclusive.
	 */
	fun setProgress(value: Long) {
		val oldProgress = bar.progress
		val newProgress = value.toDouble()/100
		if (oldProgress < 1 && newProgress >= 1) { //Timer just completed
			bar.setTrackStyleClass(MainWindow.COMSTRING)
			logger.finer("Progress set from incomplete to complete")
		} else if (oldProgress >= 1 && newProgress < 1) {
			logger.finer("Progress set from complete to incomplete")
			bar.setTrackStyleClass(MainWindow.INCSTRING)
		}
		this.bar.progress = newProgress
	}

	internal fun initBarStyle() {
		bar.setTrackStyleClass(MainWindow.INCSTRING)
	}

	companion object {

		internal val logger = LoggerManager.getInstance().getLogger("FXWinPPane")
	}

}