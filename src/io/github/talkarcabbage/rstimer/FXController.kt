package io.github.talkarcabbage.rstimer

import java.awt.AWTException
import java.awt.MenuItem
import java.awt.PopupMenu
import java.awt.SystemTray
import java.awt.TrayIcon
import java.io.File
import java.lang.reflect.InvocationTargetException
import java.net.MalformedURLException
import java.util.logging.Level

import javax.swing.ImageIcon
import javax.swing.SwingUtilities

import com.google.common.base.Throwables
import com.google.common.collect.HashBiMap

import io.github.talkarcabbage.logger.LoggerManager
import io.github.talkarcabbage.rstimer.Timer.TimerType
import io.github.talkarcabbage.rstimer.fxgui.MainWindow
import io.github.talkarcabbage.rstimer.fxgui.ProgressPane
import io.github.talkarcabbage.rstimer.newtimers.*
import io.github.talkarcabbage.rstimer.persistence.*
import javafx.application.Application
import javafx.application.Platform

/**
 * The instance of this class controls the underlying logic of the program and
 * interacts with the FX GUI and file system as necessary.
 * This class implements runnable, to create a separate thread to tick timers.
 * @author Talkarcabbage
 */
class FXController internal constructor() {

	//val timerMap = HashBiMap.create<ProgressPane, Timer>(50)

	/**
	 * New Timers
	 */
	val newTimerMap = HashBiMap.create<ProgressPane, NewTimer>(50)

	internal fun prepareSystemTray() {
		val f = File(TASKBARICONFILE)
		if (!f.exists()) {
			logger.info("Retrieving assets")
			try {
				FileManager.downloadFile(TASKBARICONFILE, "https://www.dropbox.com/s/th13a1fsf5kj4st/Cabbage.png?dl=1")
			} catch (e: MalformedURLException) {
				logger.severe("URL of the icon was invalid")
				logger.severe(Throwables.getStackTraceAsString(e))
			}

		}
		val taskBarIcon = ImageIcon(TASKBARICONFILE)
		try {
			SwingUtilities.invokeAndWait { applyWindowIcon(taskBarIcon) }
		} catch (e1: InvocationTargetException) {
			logger.log(Level.SEVERE, "A thread-related exception occured while setting up the tray: ", e1)
		} catch (e1: InterruptedException) {
			logger.log(Level.SEVERE, "A thread-related exception occured while setting up the tray: ", e1)
		}

	}

	/**
	 * Converts a legacy timer to a new timer and adds it to the new timers list.
	 * Does not save!
	 * @param startingTime
	 * @param duration
	 * @param tab
	 * @param timerType
	 * @param name
	 * @return
	 */
	fun addLegacyTimer(startingTime: Long, duration: Long, tab: Int, timerType: TimerType, name: String) {
		val legacyTimer: Timer
		logger.info("Adding a legacy imported timer")
		when (timerType) {
			Timer.TimerType.STANDARD -> {
				logger.fine("Added a standard timer")
				legacyTimer = Timer(startingTime, duration, name, tab)
			}
			Timer.TimerType.PERIODIC -> {
				logger.fine("Added a periodic timer")
				legacyTimer = PeriodicTimer(startingTime, duration, name, tab)
			}
			Timer.TimerType.MONTHLY -> {
				logger.fine("Added a monthly timer")
				legacyTimer = MonthlyTimer(startingTime, duration, name, tab)
			}
		}

		var theNewTimer: NewTimer? = null
		when (legacyTimer.newTimerTypeString) {
			"Daily" -> theNewTimer = Daily(legacyTimer.dataMap)
			"Monthly" -> theNewTimer = Monthly(legacyTimer.dataMap)
			"Standard" -> theNewTimer = Standard(legacyTimer.dataMap)
			"Weekly" -> theNewTimer = Weekly(legacyTimer.dataMap)
			else -> logger.warning { "Encountered nonexisting timer type conversion attempt:"+legacyTimer.newTimerTypeString }
		}
		if (theNewTimer is NewTimer) {
			val progPane = ProgressPane()
			progPane.labelText = theNewTimer.name
			this.newTimerMap[progPane] = theNewTimer
			progPane.setOnMouseClicked { event -> MainWindow.instance.onClickNewTimerBar(progPane, event) }
			MainWindow.instance.addTimerBar(progPane, tab)
		} else {
			logger.warning("Could not add a new timer from legacy data as it was null!")
		}
	}

	//This is the old loading system. Has a lot of legacy in it.
	fun loadLegacyTimers() {
		if (File(SaveManager.SAVE_FILE_LOCATION).exists()) {
			logger.info("Found an existing new timer file, skipping conversion!")
			//return
		}
		var importResaveConfig = false
		var processDoubles = true
		var isFXBased = false //If true, do not make a new first tab. Set this to true if a tab is made or if the value is found
		FileManager.ensureExists(TIMERFILE)
		val timersStringArray = FileManager.readFileSplit(TIMERFILE)

		for (s in timersStringArray!!) { //NOSONAR
			if (s.isEmpty() || s.startsWith("/")) {
				continue
			}
			if (NUMBERFORMATCONSTANT.equals(s, ignoreCase = true)) {
				processDoubles = false //Verify that our format of number storage is long.
				continue
			}
			if (FXBASEDCONSTANT.equals(s, ignoreCase = true)) { //We don't need to make a new first-tab
				isFXBased = true
				continue
			}
			val timerInfo = s.split(",".toRegex()).dropLastWhile({ it.isEmpty() }).toTypedArray()

			if ("cfg"==timerInfo[0]) {
				importResaveConfig = true
				logger.config { "Importing configuration from timers file: "+timerInfo[1] }
				if ("mainTabName"==timerInfo[1]) {
					ConfigManager.defaultTabName = timerInfo[2] //Deprecated
				} else if ("gridColumns"==timerInfo[1]) { //Deprecated
					ConfigManager.defaultTabColumns = Integer.parseInt(timerInfo[2])
				} else if ("gridRows"==timerInfo[1]) { //Deprecated
					ConfigManager.defaultTabRows = Integer.parseInt(timerInfo[2])
				} else if ("winSize"==timerInfo[1]) { //Deprecated
					ConfigManager.winWidth = Integer.parseInt(timerInfo[2])
					ConfigManager.winHeight = Integer.parseInt(timerInfo[3])
				} else if ("logLevel"==timerInfo[1]) {
					ConfigManager.logLevel = Level.parse(timerInfo[2])
				}
			}
			if (!isFXBased) { //Add a new tab and set isFXBased and importResave to true
				logger.info("Creating a new first-tab based on imported swing data")
				MainWindow.instance.addDefaultTab()
				isFXBased = true
			}
			if ("tab"==timerInfo[0]) {
				if (timerInfo.size < 4) {
					logger.info("Imported old tab data!")
					MainWindow.instance.addTab(0, 3, timerInfo[1])
				} else {
					MainWindow.instance.addTab(Integer.parseInt(timerInfo[1]), Integer.parseInt(timerInfo[2]), timerInfo[3])
				}
			} else if ("timer"==timerInfo[0]) {
				logger.finer { "Found a timer entry with size: "+timerInfo.size }
				loadTimerFromArray(timerInfo, processDoubles) //Actually load the timer.

			} else if (timerInfo[0].startsWith("1")) {
				logger.info("Imported really old timer data!")
				addLegacyTimer(java.lang.Double.parseDouble(timerInfo[0]).toLong(), java.lang.Double.parseDouble(timerInfo[1]).toLong(), 0, TimerType.STANDARD, timerInfo[2]) //If it's super old data
			}
		}
		if (importResaveConfig) {
			ConfigManager.save()
		}
		if (MainWindow.instance.tabList.isEmpty()) {
			logger.info("Loaded no tabs! Adding a default first tab")
			MainWindow.instance.addDefaultTab()
		}
		this.saveTimers()
	}

	/*fun updateProgressPaneTitle(timer: Timer) {
		if (timerMap.inverse()[timer]==null) {
			logger.warning("Tried to update a timer that has already been deleted or did not exist!")
			return
		}
		timerMap.inverse()[timer]?.labelText = timer.name.toString()
	}
	 */

	private fun loadTimerFromArray(timerInfo: Array<String>, processAsDouble: Boolean) {
		if (!processAsDouble) { //Importing up to date data type (long)
			logger.finer { "Up to date timer loaded: "+timerInfo[1]+" | "+timerInfo[2]+" | "+timerInfo[3]+" | "+timerInfo[4]+" | "+timerInfo[5]+" | " }
			addLegacyTimer(java.lang.Long.parseLong(timerInfo[1]), java.lang.Long.parseLong(timerInfo[2]), Integer.parseInt(timerInfo[3]), TimerType.valueOf(timerInfo[4].toUpperCase()), timerInfo[5])
		} else { //Importing old (double) data type
			logger.info { "Importing old timer data to long format: "+timerInfo[5] }
			if ("true".equals(timerInfo[4], ignoreCase = true)) {
				logger.info("Imported old timer data for standard timer with double type.")
				addLegacyTimer(java.lang.Double.parseDouble(timerInfo[1]).toLong(), java.lang.Double.parseDouble(timerInfo[2]).toLong(), Integer.parseInt(timerInfo[3]), TimerType.STANDARD, timerInfo[5])
			} else if ("false".equals(timerInfo[4], ignoreCase = true)) {
				logger.info("Imported old timer data for periodic timer.")
				addLegacyTimer(java.lang.Double.parseDouble(timerInfo[1]).toLong(), java.lang.Double.parseDouble(timerInfo[2]).toLong(), Integer.parseInt(timerInfo[3]), TimerType.PERIODIC, timerInfo[5])
			} else {
				logger.info { "Converting double timer: "+timerInfo[1]+" | "+timerInfo[2]+" | "+timerInfo[3]+" | "+timerInfo[4]+" | "+timerInfo[5]+" | " }
				addLegacyTimer(java.lang.Double.parseDouble(timerInfo[1]).toLong(), java.lang.Double.parseDouble(timerInfo[2]).toLong(), Integer.parseInt(timerInfo[3]), TimerType.valueOf(timerInfo[4].toUpperCase()), timerInfo[5])
			}
		}
	}

	fun saveTimers() {
		logger.info("Queueing timers to be saved")

		//Save new timers to the new timer file
		IOThreadManager.instance.writeFile(SaveManager.SAVE_FILE_LOCATION, SaveManager.getSaveDataString(MainWindow.instance.tabList, newTimerMap.values), false)

		/*
		val it = timerMap.values.iterator()
		var t: Timer
		while (it.hasNext()) {
			t = it.next()
			toSave.append("timer,"+t.startingTime+","+t.duration+","+t.tab+","+t.timerTypeString+","+t.name+"\n")
		}
		IOThreadManager.instance.writeFile(TIMERFILE, toSave.toString(), false)
		//Writing the old timer data to the file apparently?
		IOThreadManager.instance.writeFile(SaveManager.SAVE_FILE_LOCATION, SaveManager.getLegacySaveDataString(MainWindow.instance.tabList, timerMap.values), false)
		 */

	}

	fun destroyTrayIcon() {
		SystemTray.getSystemTray().remove(trayIcon)
	}

	/*
	 * BEGIN NEW TIMER METHODS HERE
	 */

	/**
	 * Adds a timer to the GUI and adds it to the map. Returns the timer for convenience.
	 *
	 * @param timer The timer to add. The timer data should already be set, including tab.
	 * @return The newly added Timer
	 */
	fun addNewTimer(timer: NewTimer): NewTimer {
		addNewTimerNoSave(timer)
		saveTimers()
		return timer
	}

	/**
	 * Mainly for loading in timers. Also used by addNewTimer for less redundancy
	 */
	fun addNewTimerNoSave(timer: NewTimer): NewTimer {
		val progPane = ProgressPane()
		progPane.labelText = timer.name
		this.newTimerMap[progPane] = timer
		progPane.setOnMouseClicked { event -> MainWindow.instance.onClickNewTimerBar(progPane, event) }
		MainWindow.instance.addTimerBar(progPane, timer.tab)
		return timer
	}

	fun resetNewTimer(bar: ProgressPane) {
		logger.info("Attempting timer reset for newtimer: "+bar.labelText)
		val t = this.newTimerMap[bar]
		if (t==null) {
			logger.severe("Found null when attempting to reset a timer!")
			return
		}
		logger.fine("Found timer mapping for timer: "+t.name)
		t.resetTimer()
		saveTimers()
	}

	fun resetNewTimerComplete(bar: ProgressPane) {
		logger.info("Attempting timer completion for newtimer: "+bar.labelText)
		val t = newTimerMap[bar]
		if (t==null) {
			logger.severe("Found null when attempting to reset-complete timer!")
			return
		}
		logger.fine("Found timer mapping for timer: "+t.name)
		t.resetTimerComplete()
		saveTimers()
	}

	fun updateNewProgressPaneTitle(timer: NewTimer) {
		if (newTimerMap.inverse()[timer]==null) {
			logger.warning("Tried to update a new timer that has already been deleted or did not exist!")
			return
		}
		newTimerMap.inverse()[timer]?.labelText = timer.name.toString()
	}

	fun removeNewTimer(pane: ProgressPane) {
		logger.fine("Removing a timer with bar named "+pane.labelText)
		val timer = newTimerMap[pane]
		if (timer==null) {
			logger.severe("Found null when attempting to delete timer. This is a serious error.")
			return
		}

		MainWindow.instance.removeNewTimerBar(pane, newTimerMap)

		newTimerMap.remove(pane)
		saveTimers()
	}

	fun removeNewTimerTab(tabNum: Int) {
		logger.fine("Attempting to remove a timer tab.")
		val it = newTimerMap.entries.iterator()
		while (it.hasNext()) {
			val timer = it.next().value
			if (timer.tab==tabNum) {
				it.remove()
				continue
			} else if (timer.tab > tabNum) {
				timer.tab = timer.tab-1
			}
		}
		MainWindow.instance.tabList.removeAt(tabNum)
		saveTimers()
	}

	fun loadNewTimers() {
		LoadManager.parseTabsFromFileData(FileManager.readFileSplitList(SaveManager.SAVE_FILE_LOCATION)).forEach {
			MainWindow.instance.addTabNew(it)
		}
		val newTimers = LoadManager.loadTimersFromFileData(FileManager.readFileSplitList(SaveManager.SAVE_FILE_LOCATION))
		newTimers.forEach {
			it.onLoad()
			addNewTimerNoSave(it)
		}
	}

	companion object {

		internal val logger = LoggerManager.getInstance().getLogger("FX Controller")

		internal var trayIcon: TrayIcon? = null

		val instance = FXController()

		private val TASKBARICONFILE = "taskBarIcon.png"

		val TIMERFILE = "timers.cfg"
		internal val NUMBERFORMATCONSTANT = "numberformat=long"

		internal val FXBASEDCONSTANT = "gui,fx"

		@JvmStatic
		fun main(args: Array<String>) {
			logger.info("Program starting...")
			logger.config("Loading configuration file")
			ConfigManager.load()
			logger.config("Updating config file")
			ConfigManager.save()
			logger.fine("Attempting tray initialization")
			instance.prepareSystemTray()
			Application.launch(MainWindow::class.java, *args)
		}

		private fun applyWindowIcon(taskBarIcon: ImageIcon) {
			trayIcon = TrayIcon(taskBarIcon.image)
			try {
				SystemTray.getSystemTray().add(trayIcon)
				trayIcon?.addActionListener { Platform.runLater { MainWindow.instance.isVisible = !MainWindow.instance.isVisible } }
				val trayMenu = PopupMenu("RS Timer")

				val toggleVisibilityMenu = MenuItem("Toggle Window")
				toggleVisibilityMenu.addActionListener {
					logger.fine("Tray icon toggle visibility event: "+MainWindow.instance.isVisible)
					Platform.runLater { MainWindow.instance.isVisible = !MainWindow.instance.isVisible }
				}

				trayMenu.add(toggleVisibilityMenu)

				val exitMenu = MenuItem("Exit")
				exitMenu.addActionListener { System.exit(0) }
				trayMenu.add(exitMenu)

				trayIcon?.popupMenu = trayMenu

			} catch (e: AWTException) {
				logger.severe("Error creating tray icon")
				logger.severe(Throwables.getStackTraceAsString(e))
			}

		}

		/**
		 * Formats the time for display in a tooltip. Returns "Complete!" if the value is negative
		 * @param timeDuration
		 * @return
		 */
		fun formatTime(timeDuration: Long): String {
			if (timeDuration < 0) {
				return "Complete!"
			}
			val timeSeconds = timeDuration/1000.0
			return Math.round(Math.floor(timeSeconds/3600)).toString()+":"+Math.round(Math.floor(timeSeconds%3600/60).toLong().toFloat())+":"+Math.round(timeSeconds%60) //NOSONAR
		}
	}

	fun addDefaultTimersIfNeeded() {
		if (MainWindow.instance.tabList.isEmpty()) {
			MainWindow.instance.addDefaultTab()
		}
		if (newTimerMap.isEmpty()) {
			addNewTimerNoSave(Standard("Sample: 2m", 0, false, System.currentTimeMillis(), 120000))
			addNewTimerNoSave(Standard("Sample: 60m", 0, false, System.currentTimeMillis(), 3600000))
		}

	}
}


