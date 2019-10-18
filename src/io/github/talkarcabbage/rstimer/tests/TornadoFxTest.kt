package io.github.talkarcabbage.rstimer.tests

import io.github.talkarcabbage.rstimer.fxgui.MainWindowApp
import io.github.talkarcabbage.rstimer.fxgui.MainWindowTornado
import org.junit.Test
import tornadofx.*

class TornadoFxTest {
	@Test
	fun testTornadoWindow() {
		launch<MainWindowApp>()
	}
}