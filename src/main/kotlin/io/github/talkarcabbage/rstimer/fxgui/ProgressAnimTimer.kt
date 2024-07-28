package io.github.talkarcabbage.rstimer.fxgui

import com.google.common.collect.BiMap

import io.github.talkarcabbage.rstimer.FXController
import io.github.talkarcabbage.rstimer.Timer
import io.github.talkarcabbage.rstimer.persistence.ConfigManager
import javafx.animation.AnimationTimer

/**
 *
 * @author Talkarcabbage
 */
class ProgressAnimTimer(internal val map: BiMap<ProgressPane, Timer>) : AnimationTimer() {
	internal var animCycle: Int = 0

	init {
		animCycle = 1000000
	}

	override fun handle(now: Long) {
		if (++animCycle >= ConfigManager.framesPerUpdate) {
			animCycle = 0
			map.forEach { (pane, timer) ->
				pane.setProgress(timer.percentageComplete.toLong())
				pane.bar.getTooltip().setText(FXController.formatTime(timer.timeRemaining))
				pane.labelObject.getTooltip().setText(FXController.formatTime(timer.timeRemaining))
			}
		}
	}
}