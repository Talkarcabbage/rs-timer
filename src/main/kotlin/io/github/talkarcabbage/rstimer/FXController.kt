package io.github.talkarcabbage.rstimer

import com.google.common.base.Throwables
import com.google.common.collect.HashBiMap
import io.github.talkarcabbage.logger.LoggerManager
import io.github.talkarcabbage.rstimer.fxgui.MainWindow
import io.github.talkarcabbage.rstimer.fxgui.ProgressPane
import io.github.talkarcabbage.rstimer.persistence.*
import io.github.talkarcabbage.rstimer.timers.BaseTimer
import io.github.talkarcabbage.rstimer.timers.Standard
import javafx.application.Application
import javafx.application.Platform
import java.awt.*
import java.lang.reflect.InvocationTargetException
import java.util.logging.Level
import javax.swing.ImageIcon
import javax.swing.SwingUtilities
import kotlin.math.floor
import kotlin.system.exitProcess

/**
 * The instance of this class controls the underlying logic of the program and
 * interacts with the FX GUI and file system as necessary.
 * This class implements runnable, to create a separate thread to tick timers.
 * @author Talkarcabbage
 */
class FXController internal constructor() {
	val timerMap: HashBiMap<ProgressPane, BaseTimer> = HashBiMap.create<ProgressPane, BaseTimer>(50)

	internal fun prepareSystemTray() {
		val taskBarIcon = ImageIcon(this.javaClass.getResource(TASKBARICONFILE))
		try {
			SwingUtilities.invokeAndWait { applyWindowIcon(taskBarIcon) }
		} catch (e1: InvocationTargetException) {
			logger.log(Level.SEVERE, "A thread-related exception occurred while setting up the tray: ", e1)
		} catch (e1: InterruptedException) {
			logger.log(Level.SEVERE, "A thread-related exception occurred while setting up the tray: ", e1)
		}

	}

	fun saveTimers() {
		logger.info("Queueing timers to be saved")
		//Save new timers to the new timer file
		IOThreadManager.instance.writeFile(SaveManager.SAVE_FILE_LOCATION, SaveManager.getSaveDataString(MainWindow.instance.tabList, timerMap.values), false)
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
	fun addNewTimer(timer: BaseTimer): BaseTimer {
		addNewTimerNoSave(timer)
		saveTimers()
		return timer
	}

	/**
	 * Mainly for loading in timers. Also used by addNewTimer for less redundancy
	 */
	fun addNewTimerNoSave(timer: BaseTimer): BaseTimer {
		val progPane = ProgressPane()
		progPane.labelText = timer.name
		this.timerMap[progPane] = timer
		progPane.setOnMouseClicked { event -> MainWindow.instance.onClickNewTimerBar(progPane, event) }
		MainWindow.instance.addTimerBar(progPane, timer.tab)
		return timer
	}

	fun resetNewTimer(bar: ProgressPane) {
		logger.info("Attempting timer reset for timer: "+bar.labelText)
		val t = this.timerMap[bar]
		if (t==null) {
			logger.severe("Found null when attempting to reset a timer!")
			return
		}
		logger.fine("Found timer mapping for timer: "+t.name)
		t.resetTimer()
		saveTimers()
	}

	fun resetNewTimerComplete(bar: ProgressPane) {
		logger.info("Attempting timer completion for timer: "+bar.labelText)
		val t = timerMap[bar]
		if (t==null) {
			logger.severe("Found null when attempting to reset-complete timer!")
			return
		}
		logger.fine("Found timer mapping for timer: "+t.name)
		t.resetTimerComplete()
		saveTimers()
	}

	fun updateNewProgressPaneTitle(timer: BaseTimer) {
		if (timerMap.inverse()[timer]==null) {
			logger.warning("Tried to update a new timer that has already been deleted or did not exist!")
			return
		}
		timerMap.inverse()[timer]?.labelText = timer.name
	}

	fun removeNewTimer(pane: ProgressPane) {
		logger.fine("Removing a timer with bar named "+pane.labelText)
		val timer = timerMap[pane]
		if (timer==null) {
			logger.severe("Found null when attempting to delete timer. This is a serious error.")
			return
		}

		MainWindow.instance.removeNewTimerBar(pane, timerMap)

		timerMap.remove(pane)
		saveTimers()
	}

	fun removeNewTimerTab(tabNum: Int) {
		logger.fine("Attempting to remove a timer tab.")
		val it = timerMap.entries.iterator()
		while (it.hasNext()) {
			val timer = it.next().value
			if (timer.tab==tabNum) {
				it.remove()
				continue
			} else if (timer.tab > tabNum) {
				timer.tab -= 1
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
		private const val TASKBARICONFILE = "/images/taskBarIcon.png"
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
				exitMenu.addActionListener { exitProcess(0) }
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
			return Math.round(floor(timeSeconds/3600)).toString()+":"+Math.round(floor(timeSeconds%3600/60).toLong().toFloat())+":"+Math.round(timeSeconds%60) //NOSONAR
		}
	}

	fun addDefaultTimersIfNeeded() {
		if (MainWindow.instance.tabList.isEmpty()) {
			MainWindow.instance.addDefaultTab()
		}
		if (timerMap.isEmpty()) {
			addNewTimerNoSave(Standard("Sample: 2m", 0, false, System.currentTimeMillis(), 120000))
			addNewTimerNoSave(Standard("Sample: 60m", 0, false, System.currentTimeMillis(), 3600000))
		}

	}
}


