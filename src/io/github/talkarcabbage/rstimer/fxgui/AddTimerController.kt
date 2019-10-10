package io.github.talkarcabbage.rstimer.fxgui

import java.io.IOException
import java.time.Duration
import java.util.logging.Level
import java.util.logging.Logger

import io.github.talkarcabbage.logger.LoggerManager
import io.github.talkarcabbage.rstimer.FXController
import io.github.talkarcabbage.rstimer.Timer
import io.github.talkarcabbage.rstimer.Timer.TimerType
import io.github.talkarcabbage.rstimer.fxgui.NewTimerModel.TimerModelType
import io.github.talkarcabbage.rstimer.newtimers.Daily
import io.github.talkarcabbage.rstimer.newtimers.Hourly
import io.github.talkarcabbage.rstimer.newtimers.Monthly
import io.github.talkarcabbage.rstimer.newtimers.NewTimer
import io.github.talkarcabbage.rstimer.newtimers.Standard
import io.github.talkarcabbage.rstimer.newtimers.Weekly
import javafx.collections.FXCollections
import javafx.event.ActionEvent
import javafx.fxml.FXML
import javafx.fxml.FXMLLoader
import javafx.scene.Parent
import javafx.scene.Scene
import javafx.scene.control.Button
import javafx.scene.control.CheckBox
import javafx.scene.control.ComboBox
import javafx.scene.control.TextField
import javafx.scene.control.TextFormatter
import javafx.stage.Stage
import javafx.util.converter.IntegerStringConverter

/**
 *
 * @author Talkarcabbage
 * TODO determine if we can make this an object instead of a class. Probably fine, but javafx.
 */
class AddTimerController {
	lateinit var stage: Stage
		internal set

	@FXML
	protected var nameTextField: TextField? = null

	@FXML
	protected var daysTextField: TextField? = null
	@FXML
	protected var hoursTextField: TextField? = null
	@FXML
	protected var minutesTextField: TextField? = null
	@FXML
	protected var secondsTextField: TextField? = null

	@FXML
	protected var alarmCheckBox: CheckBox? = null
	@FXML
	protected var resetCheckBox: CheckBox? = null

	@FXML
	protected var createButton: Button? = null
	@FXML
	protected var cancelButton: Button? = null

	@FXML
	protected var typeComboBox: ComboBox<String>? = null

	/**
	 * If this is non-null, the current data in the GUI applies to a specific timer. Otherwise, it is creating a new timer.
	 */
	internal var editedTimer: NewTimer? = null

	/**
	 * New timer
	 */
	internal var editedNewTimer: NewTimer? = null //TODO

	/**
	 * This constructor is called internally by the JavaFX loader when createRoot is called and it should not be called manually.
	 * @throws IllegalStageException - If this constructor is called while the instance already exists.
	 */
	init {
		kotlin.check(instance==null) { "Cannot initialize this method more than once!" }
		logger.fine("Created instance of AddTimerController")
		instance = this //NOSONAR
	}

	internal fun createStage(): Stage {
		val newStage = Stage()

		newStage.scene = Scene(root)
		newStage.title = "Add Timer"
		daysTextField!!.textFormatter = TextFormatter(IntegerStringConverter())
		hoursTextField!!.textFormatter = TextFormatter(IntegerStringConverter())
		minutesTextField!!.textFormatter = TextFormatter(IntegerStringConverter())
		secondsTextField!!.textFormatter = TextFormatter(IntegerStringConverter())

		typeComboBox!!.items = FXCollections.observableArrayList(
				STANDARD,
				DAILY,
				WEEKLY,
				MONTHLY,
				HOURLY)
		typeComboBox!!.value = STANDARD
		return newStage
	}

	/**
	 * Handles what happens when the **create** button is clicked on the FXML create-timer gui
	 * TODO: New Timer Models
	 * TODO: Make the controller handle more of this stuff. Too much logic
	 * happening in the creation gui.
	 * @param event
	 */
	@FXML
	protected fun onClickCreateButton(event: ActionEvent) {
		logger.info("OnClickCreateButton fired")
		stage.hide()
		if (editedTimer==null) { //TODO new timers
			if (""==nameTextField!!.text) {
				logger.warning("Timer name must not be empty! Setting to space")
				nameTextField!!.text = " "
			}

			val theNewTimer = getTimerForModelData()

			if (theNewTimer!=null) {
				logger.info("Added a new newTimer: ${theNewTimer.toString()}")
				FXController.instance.addNewTimer(theNewTimer)
				theNewTimer.resetTimer()
			}
		} else {
			val editedPane = FXController.instance.newTimerMap.inverse()[editedTimer]
			val changedTimer = getTimerForModelData(editedTimer!!)
			if (changedTimer is NewTimer) {
				FXController.instance.newTimerMap[editedPane] = changedTimer
				FXController.instance.updateNewProgressPaneTitle(changedTimer)
				changedTimer.resetTimer()
				FXController.instance.saveTimers()
			} else {
				logger.warning("An error occured during editing: the updated timer was null")
			}
			//TODO editing
		}
	}

	internal fun getTimerForModelData(): NewTimer? {
		val model = NewTimerModel.getModelFromControllerData(this.nameTextField!!.text, this.typeComboBox!!.value, daysTextField!!.text, hoursTextField!!.text, minutesTextField!!.text, secondsTextField!!.text, alarmCheckBox!!.isSelected, resetCheckBox!!.isSelected)
		var theNewTimer: NewTimer? = null
		val dataMap = model.asDataMap().toMutableMap()
		dataMap[NewTimer.MAP_TAB] = ""+MainWindow.instance.currentTab //TODO find a more reliable way to get this or modalblock

		when (model.timerType) {
			TimerModelType.DAILY -> theNewTimer = Daily(dataMap)
			TimerModelType.HOURLY -> theNewTimer = Hourly(dataMap)
			TimerModelType.MONTHLY -> theNewTimer = Monthly(dataMap)
			TimerModelType.STANDARD -> theNewTimer = Standard(dataMap)
			TimerModelType.WEEKLY -> theNewTimer = Weekly(dataMap)
			else -> logger.warning { "Encountered nonexisting timer type addition attempt:"+model.timerType }
		}
		return theNewTimer
	}


	internal fun getTimerForModelData(old: NewTimer): NewTimer? {
		val dataMap = old.timerDataMap.toMutableMap()
		val model = NewTimerModel.getModelFromControllerData(this.nameTextField!!.text, this.typeComboBox!!.value, daysTextField!!.text, hoursTextField!!.text, minutesTextField!!.text, secondsTextField!!.text, alarmCheckBox!!.isSelected, resetCheckBox!!.isSelected)
		var theNewTimer: NewTimer? = null
		dataMap.putAll(model.asDataMap())

		when (model.timerType) {
			TimerModelType.DAILY -> theNewTimer = Daily(dataMap)
			TimerModelType.HOURLY -> theNewTimer = Hourly(dataMap)
			TimerModelType.MONTHLY -> theNewTimer = Monthly(dataMap)
			TimerModelType.STANDARD -> theNewTimer = Standard(dataMap)
			TimerModelType.WEEKLY -> theNewTimer = Weekly(dataMap)
			else -> logger.warning { "Encountered nonexisting timer type addition attempt:"+model.timerType }
		}
		return theNewTimer
	}


	internal fun getLongForField(field: TextField?): Long {
		try {
			return java.lang.Long.parseLong(field!!.text)
		} catch (e: NumberFormatException) {
			logger.log(Level.SEVERE, "Found invalid data in an add-win text field: ", e)
			return 0
		}

	}

	/**
	 * Handles what happens when the **cancel** button is clicked on the FXML create-timer gui
	 * @param event
	 */
	@FXML
	protected fun onClickCancelButton(event: ActionEvent) {
		stage.hide()
	}

	@FXML
	protected fun typeComboChanged(event: ActionEvent) {
		if (typeComboBox?.value==STANDARD) {
			setTimeFieldsEnabled(true)
		} else {
			setTimeFieldsEnabled(false)
		}
	}

	/**
	 * Sets the visibility of the time input text fields
	 * @param isEnabled
	 */
	internal fun setTimeFieldsEnabled(isEnabled: Boolean) {
		secondsTextField!!.isDisable = !isEnabled
		minutesTextField!!.isDisable = !isEnabled
		hoursTextField!!.isDisable = !isEnabled
		daysTextField!!.isDisable = !isEnabled
	}

	/**
	 * Shows the window and clears the existing information.<br></br>
	 * This is used to create a new timer using the GUI.
	 */
	fun showCreateWindow() {
		setControlsCreate()
		this.editedTimer = null
		stage.show()
	}

	/**
	 * Shows the window and sets the information to the data in the timer.
	 * Should only be called from the FX thread
	 * @param timer The timer to display to edit. Should not be null.
	 */
	fun showEditWindow(timer: NewTimer) {
		setControlsEdit()
		this.editedTimer = timer
		val newModel = NewTimerModel(timer)
		fillEditFields()
		when (newModel.timerType) {
			TimerModelType.STANDARD -> setEditStandardTimer()
			TimerModelType.DAILY -> setEditDailyTimer()
			TimerModelType.WEEKLY -> setEditWeeklyTimer()
			TimerModelType.MONTHLY -> setEditMonthlyTimer()
			TimerModelType.HOURLY -> TODO()
			else -> logger.warning("Unknown timer type edited!")
		}

		/*
		when (timer.timerType) {
			Timer.TimerType.STANDARD -> setEditStandardTimer()
			Timer.TimerType.PERIODIC //NOSONAR
			-> if (timer.duration==Timer.DAY_LENGTH) {
				setEditDailyTimer()
			} else if (timer.duration==Timer.WEEK_LENGTH) {
				setEditWeeklyTimer()
			} else {
				logger.severe("Encountered an unrecognized periodic duration trying to initialize timer edit GUI values: "+timer.duration)
			}
			Timer.TimerType.MONTHLY -> setEditMonthlyTimer()

		}
		*/
		stage.show()
	}

	/**
	 * Sets the texts of the window to reflect editing a timer
	 * Should only be called from the FX thread
	 */
	internal fun setControlsEdit() {
		stage.title = "Edit Timer"
		createButton!!.text = "Update"
		//typeComboBox!!.isDisable = true
	}

	/**
	 * Sets the texts of the window to reflect adding a timer
	 * Should only be called from the FX thread
	 */
	internal fun setControlsCreate() {
		stage.title = "Add a timer"
		createButton!!.text = "Create"
		typeComboBox!!.isDisable = false
		daysTextField!!.text = "0"
		minutesTextField!!.text = "0"
		hoursTextField!!.text = "0"
		secondsTextField!!.text = "0"
		nameTextField!!.text = ""
		setTimeFieldsEnabled(true)
	}

	/**
	 * Updates the GUI's editable values to reflect the current editedTimer
	 * as a standard timer
	 */
	internal fun setEditStandardTimer() {
		setTimeFieldsEnabled(true)
		typeComboBox!!.value = STANDARD
		//this.standardRadioButton.setSelected(true);
		fillEditFields()
	}

	/**
	 * Updates the GUI's editable values to reflect the current editedTimer
	 * as a standard timer
	 */
	internal fun setEditDailyTimer() {
		setTimeFieldsEnabled(false)
		typeComboBox!!.value = DAILY
		//this.dailyRadioButton.setSelected(true);
		fillEditFields()
	}

	/**
	 * Updates the GUI's editable values to reflect the current editedTimer
	 * as a standard timer
	 */
	internal fun setEditWeeklyTimer() {
		setTimeFieldsEnabled(false)
		typeComboBox!!.value = WEEKLY
		//this.weeklyRadioButton.setSelected(true);
		fillEditFields()
	}

	/**
	 * Updates the GUI's editable values to reflect the current editedTimer
	 * as a standard timer
	 */
	internal fun setEditMonthlyTimer() {
		setTimeFieldsEnabled(false)
		typeComboBox!!.value = MONTHLY
		//this.monthlyRadioButton.setSelected(true);
		fillEditFields()
	}

	/**
	 * Sets the non-radio button field values to the edited timer values
	 */
	internal fun fillEditFields() {
		this.nameTextField!!.text = editedTimer!!.name
		this.resetCheckBox?.isSelected = editedTimer?.autoreset!!
		this.alarmCheckBox?.isSelected = editedTimer?.audio!!
		if (editedTimer is Standard) {
			val dur = Duration.ofMillis((editedTimer as Standard).duration)
			val secondsDur = dur.seconds%60
			val minutesDur = dur.toMinutes()%60
			val hoursDur = dur.toHours()%24
			val daysDur = dur.toDays()
			this.daysTextField!!.text = java.lang.Long.toString(daysDur)
			this.hoursTextField!!.text = java.lang.Long.toString(hoursDur)
			this.minutesTextField!!.text = java.lang.Long.toString(minutesDur)
			this.secondsTextField!!.text = java.lang.Long.toString(secondsDur)
		} else {
			this.daysTextField!!.text = 0.toString()
			this.hoursTextField!!.text = 0.toString()
			this.minutesTextField!!.text = 0.toString()
			this.secondsTextField!!.text = 0.toString()
		}
	}

	companion object {

		internal val logger = LoggerManager.getInstance().getLogger("AddTimerController")

		internal var instance: AddTimerController? = null

		// String constants for types box
		val STANDARD = "Standard"
		val DAILY = "Daily"
		val WEEKLY = "Weekly"
		val MONTHLY = "Monthly"
		val HOURLY = "Hourly"


		internal lateinit var root: Parent

		/**
		 *
		 */
		fun createRoot() {
			if (instance==null) {
				try {
					val resource = AddTimerController::class.java.getResource("AddTimerFXML.fxml")
					if (resource == null) logger.warning("The FXML file was null!")
					root = FXMLLoader.load(resource)

					if (instance!=null)
					//NOSONAR
						instance!!.stage = instance!!.createStage()

				} catch (e: IOException) {
					logger.log(Level.SEVERE, "An error occured making the add-gui: ", e)
				}

			} else {
				logger.warning("Tried to call createRoot after the instance has been initialized! This is a bug!")
			}
		}

		/**
		 *
		 */
		fun getUpdatedModel() {
			val model = NewTimerModel()
			//TODO idk what this method is for
		}
	}

}