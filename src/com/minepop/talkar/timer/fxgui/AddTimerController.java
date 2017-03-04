package com.minepop.talkar.timer.fxgui;

import java.io.IOException;
import java.time.Duration;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.minepop.talkar.timer.FXController;
import com.minepop.talkar.timer.Timer;
import com.minepop.talkar.timer.Timer.TimerType;
import com.minepop.talkar.util.logging.LoggerConstructor;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.RadioButton;
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
	
	static final Logger logger = LoggerConstructor.getLogger("AddTimerController");

	static AddTimerController instance;
	Stage stage;
	
	@FXML protected TextField nameTextField;
	
	@FXML protected TextField daysTextField;
	@FXML protected TextField hoursTextField;
	@FXML protected TextField minutesTextField;
	@FXML protected TextField secondsTextField;
	
	@FXML protected RadioButton standardRadioButton;
	@FXML protected RadioButton dailyRadioButton;
	@FXML protected RadioButton weeklyRadioButton;
	@FXML protected RadioButton monthlyRadioButton;

	@FXML protected Button createButton;
	@FXML protected Button cancelButton;
	static Parent root;
	
	/**
	 * If this is non-null, the current data in the GUI applies to a specific timer. Otherwise, it is creating a new timer.
	 */
	Timer editedTimer;
	
	/**
	 * This constructor is called internally by the JavaFX loader when createRoot is called and it should not be called manually.
	 * @throws IllegalStageException - If this constructor is called while the instance already exists.
	 */
	public AddTimerController() {
		if (instance != null) {
			throw new IllegalStateException("Cannot initialize this method more than once!");
		}
		logger.fine("Created instance of AddTimerController");
		instance = this;
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

		return newStage;
	}
	
	/**
	 * Handles what happens when the <b>create</b> button is clicked on the FXML create-timer gui
	 * @param event
	 */
	@FXML
	protected void onClickCreateButton(ActionEvent event) {
		if ("".equals(nameTextField.getText())) {
			logger.warning("Timer name must not be empty! Setting to space");
			nameTextField.setText(" ");
		}
		logger.info("OnClickCreateButton fired");
		stage.hide();
		if (editedTimer == null) { //Making a new timer
			if (MainWindow.instance.getTabList().isEmpty()) {
				MainWindow.instance.addDefaultTab();

			}
			if (standardRadioButton.isSelected()) {
				long time = 0;
				time += getLongForField(secondsTextField);
				time += 60*getLongForField(minutesTextField);
				time += 3600*getLongForField(hoursTextField);
				time += Timer.DAY_LENGTH*getLongForField(daysTextField);
				time *= 1000;
				if (time == 0) {
					time = 1000;
				}
				FXController.instance.addTimer(System.currentTimeMillis(), time, MainWindow.instance.getCurrentTab(), TimerType.STANDARD, nameTextField.getText()).resetTimer();
			} else if (dailyRadioButton.isSelected()) {
				FXController.instance.addTimer(System.currentTimeMillis(), Timer.DAY_LENGTH, MainWindow.instance.getCurrentTab(), TimerType.PERIODIC, nameTextField.getText()).resetTimer();
			} else if (weeklyRadioButton.isSelected()) {
				FXController.instance.addTimer(System.currentTimeMillis(), Timer.WEEK_LENGTH, MainWindow.instance.getCurrentTab(), TimerType.PERIODIC, nameTextField.getText()).resetTimer();
			} else if (monthlyRadioButton.isSelected()) {
				FXController.instance.addTimer(System.currentTimeMillis(), 1, MainWindow.instance.getCurrentTab(), TimerType.MONTHLY, nameTextField.getText()).resetTimer();	
			}
			FXController.instance.saveTimers();
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
		standardRadioButton.setDisable(true);
		dailyRadioButton.setDisable(true);
		weeklyRadioButton.setDisable(true);
		monthlyRadioButton.setDisable(true);
	}
	
	/**
	 * Sets the texts of the window to reflect adding a timer
	 * Should only be called from the FX thread
	 */
	void setControlsCreate() {
		stage.setTitle("Add a timer");
		createButton.setText("Create");
		standardRadioButton.setDisable(false);
		dailyRadioButton.setDisable(false);
		weeklyRadioButton.setDisable(false);
		monthlyRadioButton.setDisable(false);
		standardRadioButton.setSelected(true);
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
		this.standardRadioButton.setSelected(true);
		fillEditFields();
	}
	
	/**
	 * Updates the GUI's editable values to reflect the current editedTimer 
	 * as a standard timer
	 */
	void setEditDailyTimer() {
		setTimeFieldsEnabled(false);
		this.dailyRadioButton.setSelected(true);
		fillEditFields();
	}
	
	/**
	 * Updates the GUI's editable values to reflect the current editedTimer 
	 * as a standard timer
	 */
	void setEditWeeklyTimer() {
		setTimeFieldsEnabled(false);
		this.weeklyRadioButton.setSelected(true);
		fillEditFields();
	}
	
	/**
	 * Updates the GUI's editable values to reflect the current editedTimer 
	 * as a standard timer
	 */
	void setEditMonthlyTimer() {
		setTimeFieldsEnabled(false);
		this.monthlyRadioButton.setSelected(true);
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
}