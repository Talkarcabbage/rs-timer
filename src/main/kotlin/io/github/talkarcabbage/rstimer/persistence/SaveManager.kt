package io.github.talkarcabbage.rstimer.persistence

import javafx.scene.layout.GridPane
import io.github.talkarcabbage.rstimer.timers.BaseTimer
import javafx.scene.control.Tab

object SaveManager {

	/**
	 * This method returns a String representing the given collection of Timers and Tabs along with a comment header. The String returned is save-file formatted.
	 * This method will be removed in future versions and exists to bridge the legacy and new format.
	 * @param tabs - A list containing the Tabs to be saved
	 * @param timers - A list containing the Timers to be saved
	 * @return The data String
	 */
	fun getSaveDataString(tabs: List<Tab>, timers: Collection<BaseTimer>): String {
		val sb = StringBuilder(16384)

		sb.append("#This is the timers file for Talkar's RS Timer. Manual editing is possible, but invalid modifications may cause freezing or crashing.\n")
		sb.append("#This file is regenerated when timers are saved, which happens when a timer is reset, added, or removed.\n")
		sb.append("#The format of this file is vaguely similar to json+yaml, but simpler. The version specifies the program save/load format.\n")
		sb.append("#Note that comments are not supported within the tab or timer data and that any comments are lost when the file is resaved.\n")

		sb.append(getTabsSaveData(tabs))
		sb.append(getTimersSaveData(timers))

		return sb.toString()
	}

	const val SAVE_FILE_LOCATION = "rs-timers-data.cfg"

	private fun getTimersSaveData(timers: Collection<BaseTimer>): String {
		val sb = StringBuilder()
		sb.append("timers {\n")
		for (t in timers) {
			sb.append(t.timerSaveString)
		}
		sb.append("};\n")
		return sb.toString()
	}

	private fun getTabsSaveData(tabs: List<Tab>): String {
		val sb = StringBuilder()
		sb.append("tabs {\n")
		for (t in tabs) {

			val gp = t.content as GridPane
			val rows = gp.rowConstraints.size
			val columns = gp.columnConstraints.size

			sb.append("\ttab {\n")
			sb.append("\t\t"+"title:"+t.text+"\n")
			sb.append("\t\trows:$rows\n")
			sb.append("\t\tcolumns:$columns\n")
			sb.append("\t}\n")

		}
		sb.append("};\n")
		return sb.toString()
	}
}


