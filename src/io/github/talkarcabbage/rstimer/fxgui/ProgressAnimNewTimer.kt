package io.github.talkarcabbage.rstimer.fxgui

import com.google.common.collect.BiMap

import io.github.talkarcabbage.rstimer.FXController
import io.github.talkarcabbage.rstimer.Timer
import io.github.talkarcabbage.rstimer.newtimers.NewTimer
import io.github.talkarcabbage.rstimer.persistence.ConfigManager
import javafx.animation.AnimationTimer

/**
 *
 * @author Talkarcabbage
 */
class ProgressAnimNewTimer(internal val map: BiMap<ProgressPane, NewTimer>) : AnimationTimer() {
	internal var animCycle: Int = 1000000

	override fun handle(now: Long) {
		if (++animCycle >= ConfigManager.framesPerUpdate) {
			animCycle = 0
			map.forEach { (pane, timer) ->
				if (pane.bar.progress<1 && timer.percentageComplete>=100) {
					timer.onTimerComplete()
				}
				pane.setProgress(timer.percentageComplete.toLong())
				pane.bar.getTooltip().setText(timer.tooltipText)
				pane.labelObject.getTooltip().setText(timer.tooltipText)
			}
		}
	}
}