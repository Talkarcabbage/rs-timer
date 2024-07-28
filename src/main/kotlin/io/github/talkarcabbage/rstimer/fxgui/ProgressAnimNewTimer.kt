package io.github.talkarcabbage.rstimer.fxgui

import com.google.common.collect.BiMap

import io.github.talkarcabbage.rstimer.timers.BaseTimer
import io.github.talkarcabbage.rstimer.persistence.ConfigManager
import javafx.animation.AnimationTimer

/**
 *
 * @author Talkarcabbage
 */
class ProgressAnimNewTimer(internal val map: BiMap<ProgressPane, BaseTimer>) : AnimationTimer() {
	internal var animCycle: Int = 1000000

	private var firstFrame = true

	override fun handle(now: Long) {
		if (++animCycle >= ConfigManager.framesPerUpdate) {
			animCycle = 0
			map.forEach { (pane, timer) ->
				if (pane.bar.progress<1 && timer.percentageComplete>=100) {
					if (!firstFrame) timer.onTimerComplete()
				}
				pane.setProgress(timer.percentageComplete.toLong())
				pane.bar.tooltip.text = timer.tooltipText
				pane.labelObject.tooltip.text = timer.tooltipText
			}
		}
		firstFrame = false
	}
}