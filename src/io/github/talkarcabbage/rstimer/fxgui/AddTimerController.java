package io.github.talkarcabbage.rstimer.fxgui;

import java.io.IOException;
import java.time.Duration;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import io.github.talkarcabbage.logger.LoggerManager;
import io.github.talkarcabbage.rstimer.FXController;
import io.github.talkarcabbage.rstimer.Timer;
import io.github.talkarcabbage.rstimer.Timer.TimerType;
import io.github.talkarcabbage.rstimer.newtimers.Daily;
import io.github.talkarcabbage.rstimer.newtimers.Hourly;
import io.github.talkarcabbage.rstimer.newtimers.Monthly;
import io.github.talkarcabbage.rstimer.newtimers.NewTimer;
import io.github.talkarcabbage.rstimer.newtimers.Standard;
import io.github.talkarcabbage.rstimer.newtimers.Weekly;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.stage.Stage;
import javafx.util.converter.IntegerStringConverter;

/**
 * 
 * @author Talkarcabbage
 *
 */
public class AddTimerController {
	
	static final Logger logger = LoggerManager.getInstance().getLogger("AddTimerController");

	static AddTimerController instance;
	Stage stage;
	
	@FXML protected TextField nameTextField;
	
	@FXML protected TextField daysTextField;
	@FXML protected TextField hoursTextField;
	@FXML protected TextField minutesTextField;
	@FXML protected TextField secondsTextField;
	
	@FXML protected CheckBox alarmCheckBox;
	@FXML protected CheckBox resetCheckBox;

	@FXML protected Button createButton;
	@FXML protected Button cancelButton;
	
	@FXML protected ComboBox<String> typeComboBox;
	
	// String constants for types box
	public static final String STANDARD = "Standard";
	public static final String DAILY = "Daily";
	public static final String WEEKLY = "Weekly";
	public static final String MONTHLY = "Monthly";
	public static final String HOURLY = "Hourly";

	
	static Parent root;
	
	/**
	 * If this is non-null, the current data in the GUI applies to a specific timer. Otherwise, it is creating a new timer.
	 */
	Timer editedTimer;
	
	/**
	 * New timer
	 */
	NewTimer editedNewTimer; //TODO
	
	/**
	 * This constructor is called internally by the JavaFX loader when createRoot is called and it should not be called manually.
	 * @throws IllegalStageException - If this constructor is called while the instance already exists.
	 */
	public AddTimerController() {
		if (instance != null) {
			throw new IllegalStateException("Cannot initialize this method more than once!");
		}
		logger.fine("Created instance of AddTimerController");
		instance = this; //NOSONAR
	}

	/**
	 * 
	 */
	public static void createRoot() {
		if (instance == null) {
			try {
				root = FXMLLoader.load(AddTimerController.class.getResource("AddTimerFXML.fxml"));
				
				if (instance != null) //NOSONAR
					instance.stage = instance.createStage();
	
			} catch (IOException e) {
				logger.log(Level.SEVERE, "An error occured making the add-gui: ", e);
			}
		} else {
			logger.warning("Tried to call createRoot after the instance has been initialized! This is a bug!");
		}
	}
	

	
	public Stage getStage() {
		return stage;
	}

	Stage createStage() {
		Stage newStage = new Stage();
		
		newStage.setScene(new Scene(root));
		newStage.setTitle("Add Timer");
		daysTextField.setTextFormatter(new TextFormatter<Integer>(new IntegerStringConverter()));
		hoursTextField.setTextFormatter(new TextFormatter<Integer>(new IntegerStringConverter()));
		minutesTextField.setTextFormatter(new TextFormatter<Integer>(new IntegerStringConverter()));
		secondsTextField.setTextFormatter(new TextFormatter<Integer>(new IntegerStringConverter()));

		typeComboBox.setItems(FXCollections.observableArrayList(
				STANDARD,
				DAILY,
				WEEKLY,
				MONTHLY,
				HOURLY));
		
		return newStage;
	}
	
	/**
	 * Handles what happens when the <b>create</b> button is clicked on the FXML create-timer gui
	 * TODO: New Timer Models
	 * TODO: Make the controller handle more of this stuff. Too much logic
	 * happening in the creation gui. 
	 * @param event
	 */
	@FXML
	protected void onClickCreateButton(ActionEvent event) { 
		logger.info("OnClickCreateButton fired");
		stage.hide();
		if (editedTimer == null) { //TODO new timers
			if ("".equals(nameTextField.getText())) {
				logger.warning("Timer name must not be empty! Setting to space");
				nameTextField.setText(" ");
			}
			
			NewTimerModel model = NewTimerModel.getModelFromControllerData(this.nameTextField.getText(), this.typeComboBox.getValue(), daysTextField.getText(), hoursTextField.getText(), minutesTextField.getText(), secondsTextField.getText(), alarmCheckBox.isSelected(), resetCheckBox.isSelected());
			
			NewTimer theNewTimer = null;
			Map<String, String> dataMap = model.asDataMap();
			dataMap.put(NewTimer.MAP_TAB, "" + MainWindow.instance.getCurrentTab());
			
			switch (model.timerType) {
			case DAILY:
				theNewTimer = new Daily(dataMap);
				break;
			case HOURLY:
				theNewTimer = new Hourly(dataMap);
				break;
			case MONTHLY:
				theNewTimer = new Monthly(dataMap);
				break;
			case STANDARD:
				theNewTimer = new Standard(dataMap);
				break;
			case WEEKLY:
				theNewTimer = new Weekly(dataMap);
				break;
			default:
				logger.warning(() -> "Encountered nonexisting timer type addition attempt:" + model.timerType);
				break;
			}
			if (theNewTimer != null) {
				FXController.instance.addNewTimer(theNewTimer);
			}
		} else {
			//TODO editing
		}

		if (editedTimer == null) { //Making a new timer
			//Old code can be removed
		} else { //Updating the existing timer			
			switch (editedTimer.getTimerType()) {
			case STANDARD:
				editedTimer.setDuration(1000*(getLongForField(secondsTextField) + 60*getLongForField(minutesTextField) +  3600*getLongForField(hoursTextField) + (Timer.DAY_LENGTH*getLongForField(daysTextField))/1000));
				editedTimer.setName(nameTextField.getText());
				editedTimer.setStartingTime(System.currentTimeMillis());
				editedTimer.resetTimer();
				break;
			case MONTHLY:
			case PERIODIC:
				editedTimer.setName(nameTextField.getText());
				editedTimer.resetTimer();
				break;
			}
			FXController.instance.updateProgressPaneTitle(editedTimer);
			FXController.instance.saveTimers();
		}
	}
	
	long getLongForField(TextField field) {
		try {
			return Long.parseLong(field.getText());
		} catch (NumberFormatException e) {
			logger.log(Level.SEVERE, "Found invalid data in an add-win text field: ", e);
			return 0;
		}
	}
	
	/**
	 * Handles what happens when the <b>cancel</b> button is clicked on the FXML create-timer gui
	 * @param event
	 */
	@FXML
	protected void onClickCancelButton(ActionEvent event) {
		stage.hide();
	}
	
	/**
	 * Fired when the 'standard' radio button is pressed in the timer types
	 * @param event
	 */
	@FXML
	protected void onClickTypeRadioStandard(ActionEvent event) {
		setTimeFieldsEnabled(true);
	}
	
	/**
	 * Fired when any radio button besides 'standard' is pressed in the timer types
	 * @param event
	 */
	@FXML
	protected void onClickTypeRadioOther(ActionEvent event) {
		setTimeFieldsEnabled(false);
	}
	
	/**
	 * Sets the visibility of the time input text fields
	 * @param isEnabled
	 */
	void setTimeFieldsEnabled(boolean isEnabled) {
		secondsTextField.setDisable(!isEnabled);
		minutesTextField.setDisable(!isEnabled);
		hoursTextField.setDisable(!isEnabled);
		daysTextField.setDisable(!isEnabled);
	}
	
	/**
	 * Shows the window and clears the existing information.<br>
	 * This is used to create a new timer using the GUI.
	 */
	public void showCreateWindow() {
		setControlsCreate();
		this.editedTimer = null;
		stage.show();
	}
	
	/**
	 * Shows the window and sets the information to the data in the timer.
	 * Should only be called from the FX thread
	 * @param timer The timer to display to edit. Should not be null.
	 */
	public void showEditWindow(Timer timer) {
		setControlsEdit();
		this.editedTimer = timer;
		switch (timer.getTimerType()) {
		case STANDARD:
			setEditStandardTimer();
			break;
		case PERIODIC: //NOSONAR
			if (timer.getDuration() == Timer.DAY_LENGTH) { 
				setEditDailyTimer();
			} else if (timer.getDuration() == Timer.WEEK_LENGTH) {
				setEditWeeklyTimer();
			} else {
				logger.severe("Encountered an unrecognized periodic duration trying to initialize timer edit GUI values: " + timer.getDuration());
			}
			break;
		case MONTHLY:
			setEditMonthlyTimer();
			break;
		}
		stage.show();
	}
	
	/**
	 * Sets the texts of the window to reflect editing a timer
	 * Should only be called from the FX thread
	 */
	void setControlsEdit() {
		stage.setTitle("Edit Timer");
		createButton.setText("Update");
		typeComboBox.setDisable(true);
	}
	
	/**
	 * Sets the texts of the window to reflect adding a timer
	 * Should only be called from the FX thread
	 */
	void setControlsCreate() {
		stage.setTitle("Add a timer");
		createButton.setText("Create");
		typeComboBox.setDisable(false);
		daysTextField.setText("0");
		minutesTextField.setText("0");
		hoursTextField.setText("0");
		secondsTextField.setText("0");
		nameTextField.setText("");
		setTimeFieldsEnabled(true);
	}
	
	/**
	 * Updates the GUI's editable values to reflect the current editedTimer 
	 * as a standard timer
	 */
	void setEditStandardTimer() {
		setTimeFieldsEnabled(true);
		typeComboBox.setValue(STANDARD);
		//this.standardRadioButton.setSelected(true);
		fillEditFields();
	}
	
	/**
	 * Updates the GUI's editable values to reflect the current editedTimer 
	 * as a standard timer
	 */
	void setEditDailyTimer() {
		setTimeFieldsEnabled(false);
		typeComboBox.setValue(DAILY);
		//this.dailyRadioButton.setSelected(true);
		fillEditFields();
	}
	
	/**
	 * Updates the GUI's editable values to reflect the current editedTimer 
	 * as a standard timer
	 */
	void setEditWeeklyTimer() {
		setTimeFieldsEnabled(false);
		typeComboBox.setValue(WEEKLY);
		//this.weeklyRadioButton.setSelected(true);
		fillEditFields();
	}
	
	/**
	 * Updates the GUI's editable values to reflect the current editedTimer 
	 * as a standard timer
	 */
	void setEditMonthlyTimer() {
		setTimeFieldsEnabled(false);
		typeComboBox.setValue(MONTHLY);
		//this.monthlyRadioButton.setSelected(true);
		fillEditFields();
	}
	
	/**
	 * Sets the non-radio button field values to the edited timer values
	 */
	void fillEditFields() {
		this.nameTextField.setText(editedTimer.getName());
		Duration dur = editedTimer.getDurationObject();
		long secondsDur = dur.getSeconds()%60;
		long minutesDur = dur.toMinutes()%60;
		long hoursDur = dur.toHours()%24;
		long daysDur = dur.toDays();
		this.daysTextField.setText(Long.toString(daysDur));
		this.hoursTextField.setText(Long.toString(hoursDur));
		this.minutesTextField.setText(Long.toString(minutesDur));
		this.secondsTextField.setText(Long.toString(secondsDur));
	}
	
	/**
	 * 
	 */
	public static void getUpdatedModel() {
		NewTimerModel model = new NewTimerModel();
		//TODO idk what this method is for
	}
	
}