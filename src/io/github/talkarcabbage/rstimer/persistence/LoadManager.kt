package io.github.talkarcabbage.rstimer.persistence

import java.util.ArrayList
import java.util.HashMap
import java.util.logging.Logger

import io.github.talkarcabbage.logger.LoggerManager
import io.github.talkarcabbage.rstimer.fxgui.MainWindow
import io.github.talkarcabbage.rstimer.newtimers.Daily
import io.github.talkarcabbage.rstimer.newtimers.Monthly
import io.github.talkarcabbage.rstimer.newtimers.NewTimer
import io.github.talkarcabbage.rstimer.newtimers.Standard
import io.github.talkarcabbage.rstimer.newtimers.Weekly
import javafx.scene.control.Tab

object LoadManager {

	private val logger = LoggerManager.getInstance().getLogger("Timer")

	/**
	 * Parses the tabs from the given fileInput, a list containing the contents of a save file, and returns the tabs contained in it
	 * @param fileInput the file contents
	 * @return the Tabs in the file
	 */
	fun parseTabsFromFileData(fileInput: List<String>): List<Tab> { //NOSONAR Splitting it up would only complicate it at this point.

		var index = 0
		val returnTabList = ArrayList<Tab>(8)

		while (index < fileInput.size && fileInput[index].trim { it <= ' ' }!="tabs {") { //Fast-forward to the line with the tabs declaration
			index++
		}
		index++ //Proceed one line past it to the first tab declaration

		if (index >= fileInput.size) { //If we're at the end of the file somehow already
			logger.warning("No tab data in file!")
			return ArrayList()
		}

		while (index < fileInput.size) { //Do this until we run out of tabs or out of file
			var newTabName = "MISSING"
			var newTabRows = 0
			var newTabColumns = 2
			if (fileInput[index].trim { it <= ' ' }=="tab {") {
				while (index < fileInput.size && fileInput[index].trim { it <= ' ' }!="}") {
					if (fileInput[index].trim { it <= ' ' }.indexOf(':') < 2) {
						index++
						continue
						//Skip if there's no colon
					}

					when (fileInput[index].trim { it <= ' ' }.substring(0, fileInput[index].indexOf(':')-2)) {
						"title" -> newTabName = fileInput[index].trim { it <= ' ' }.substring(fileInput[index].trim { it <= ' ' }.indexOf(':')+1)
						"rows" -> newTabRows = Integer.parseInt(fileInput[index].trim { it <= ' ' }.substring(fileInput[index].trim { it <= ' ' }.indexOf(':')+1))
						"columns" -> newTabColumns = Integer.parseInt(fileInput[index].trim { it <= ' ' }.substring(fileInput[index].trim { it <= ' ' }.indexOf(':')+1))
						else -> logger.warning("Unrecognized tab data: "+fileInput[index])
					}

					index++
				}

				returnTabList.add(MainWindow.createTab(newTabRows, newTabColumns, newTabName))
				index++
			} else if (fileInput[index].trim { it <= ' ' }=="};") {
				break
			}
		}

		return returnTabList
	}

	/**
	 * This method takes the base array of lines of the file and returns a list of Timers from that file for addition to the GUI
	 * @param fileInput A list containing the contents of a timer save file separated by line
	 */
	fun loadTimersFromFileData(fileInput: List<String>): List<NewTimer> {
		var index = 0

		while (index < fileInput.size && fileInput[index].trim { it <= ' ' }!="timers {") { //Fast-forward to the line with the timers declaration
			index++
		}
		index++ //Proceed one line past it to the first timer(type) declaration

		if (index >= fileInput.size) { //If we're at the end of the file somehow already
			logger.warning("No timer data in file!")
			return ArrayList()
		}

		val dataList = ArrayList<String>(250) //Create a list to store the List of data entries (lines) between "timers {" and "};"

		while (index < fileInput.size) {
			if (fileInput[index].trim { it <= ' ' }=="};") { //End the loop if we reach the marker for the end of the Timers section of the file
				break
			}
			dataList.add(fileInput[index]) //Add the line to the list
			index++
		}
		return loadTimersFromList(dataList) //Pass the dataList, which excludes lines other than between "timers {" and "};", to the betterLoadNewTimers method

	}

	/**
	 * This method takes in a List filled with sets of Timer data and returns a list of Timers created from that data.
	 * Each section should be an individual set of lines that would be the results of calling getTimerSaveString on the Timer
	 * Note that the list should not include the final "};"
	 * @param timerSectionList
	 * @return A list of timers created from the timer dataset
	 */
	private fun loadTimersFromList(timerSectionList: List<String>): List<NewTimer> {

		val returnList = ArrayList<NewTimer>()

		for (list in getSeparateDataStringLists(timerSectionList)) {

			var newTimer: NewTimer? = null //Avoid duplicate variable names
			val timerType = getTimerTypeFromDataString(list[0])
			val dataMap = getTimerDataMapFromList(list)

			when (timerType) {
				//Create a new timer based on the type of timer
				"Standard" -> newTimer = Standard(dataMap)
				"Daily" -> newTimer = Daily(dataMap)
				"Weekly" -> newTimer = Weekly(dataMap)
				"Monthly" -> newTimer = Monthly(dataMap)
				else -> logger.warning("Unexpected non-timer entry: "+list[0])
			}

			if (newTimer!=null) {
				returnList.add(newTimer) //Add the fresh oven baked timer to our list of new Timers
			}

		}

		return returnList
	}

	/**
	 * Takes in a List containing sets of Timer data and returns each (list) entry separated into a list of lists.
	 * @return A list of Timer data lists
	 */
	private fun getSeparateDataStringLists(originalList: List<String>): List<List<String>> {

		val bigList = ArrayList<List<String>>()
		var subList: ArrayList<String> //For efficiency purposes, don't allocate new variables

		var index = 0

		while (index < originalList.size) { //Continue until out of elements in the input
			subList = ArrayList() //Create a new sublist to store the individual timer dataset into
			while (originalList[index].trim { it <= ' ' }!="}") { //Stop on the lines that mark the end of timers
				subList.add(originalList[index]) //Add the line to the sublist
				index++ //Proceed to the next line
			}
			bigList.add(subList)
			index++ //Next "Timer" entry line
		}

		return bigList
	}


	private fun getTimerTypeFromDataString(line: String): String {
		return line.trim { it <= ' ' }.substring(0, line.trim { it <= ' ' }.length-2)
	}

	fun getTimerDataMapFromList(list: List<String>): Map<String, String> {
		val map = HashMap<String, String>(8)
		var listEntry = ""
		for (i in 1 until list.size) { //NOSONAR Both break and continue are important for flow here. Thanks anyway sonar.
			listEntry = list[i]
			if (listEntry.trim { it <= ' ' }=="}") {
				break
			}
			if (listEntry.indexOf(':') < 0) { //Covers blank lines as well as the first line if it is included.
				continue
			}
			map[listEntry.trim { it <= ' ' }.substring(0, listEntry.indexOf(':')-2)] = listEntry.trim { it <= ' ' }.substring(listEntry.trim { it <= ' ' }.indexOf(':')+1)
		}
		return map
	}

}//Because static class
