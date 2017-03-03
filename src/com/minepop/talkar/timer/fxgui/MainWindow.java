package com.minepop.talkar.timer.fxgui;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.common.collect.BiMap;
import com.minepop.talkar.timer.FXController;
import com.minepop.talkar.timer.Timer;
import com.minepop.talkar.util.ConfigManager;
import com.minepop.talkar.util.logging.LoggerConstructor;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Slider;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextFormatter;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.ToggleButton;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.RowConstraints;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.converter.IntegerStringConverter;

/**
 * 
 * @author Talkarcabbage
 *
 */
public class MainWindow extends Application {

	static final Logger logger = LoggerConstructor.getLogger("FXWin");
	
	TabPane tabPane;
	int moveXinit;
	int moveYinit;
	public static MainWindow instance; //NOSONAR
	Stage stage;
	ArrayList<ProgressPane> tempList = new ArrayList<>();
	ToggleButton minusButton;
	ProgressAnimTimer pat;
	
	//Used to set the style of the ColorProgress for a different completed-style.
	static final String INCSTRING = "progress-bar-incomplete";
	static final String COMSTRING = "progress-bar-complete";
	public static void launchWrap(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) {
		try {
			Platform.setImplicitExit(false);
			primaryStage.setOnCloseRequest( event -> {
				FXController.instance.destroyTrayIcon();
				System.exit(0);
			});
			AddTimerController.createRoot();
			instance = this;
			stage = primaryStage;
			BorderPane rootPane = new BorderPane();
			Scene scene = new Scene(rootPane, ConfigManager.getInstance().getWinWidth() ,ConfigManager.getInstance().getWinHeight());
			scene.getStylesheets().add(getClass().getResource("/css/application.css").toExternalForm());
			primaryStage.setScene(scene);
			primaryStage.initStyle(StageStyle.UNDECORATED);
			rootPane.setId("win");
			rootPane.setOnMouseReleased( event -> {
				if (!event.isConsumed()) {
					minusButton.setSelected(false);
				}
			});
			scene.getRoot().getStyleClass().add("main-root");
			
			GridPane configPane = new GridPane();
			configPane.setHgap(3);
			ColumnConstraints plusButtonColumn1 = new ColumnConstraints(15);
			ColumnConstraints minusButtonColumn2 = new ColumnConstraints(15);
			ColumnConstraints aotCheckboxColumn3 = new ColumnConstraints(15);
			ColumnConstraints aotSliderColumn4 = new ColumnConstraints(20,100,Double.MAX_VALUE);
			aotSliderColumn4.setHgrow(Priority.ALWAYS);
			configPane.getColumnConstraints().addAll(plusButtonColumn1, minusButtonColumn2, aotCheckboxColumn3, aotSliderColumn4);
	        
			tabPane = new TabPane();
			
			rootPane.setBottom(configPane);
			rootPane.setCenter(tabPane);
			
			configPane.getStyleClass().add("configPane");
			
			Button plusButton = new Button();
			plusButton.setId("plusButton");
			minusButton = new ToggleButton();
			minusButton.setId("minusButton");
			ToggleButton aotButton = new ToggleButton();
			aotButton.setId("aotButton");
			Slider transSlider = new Slider(0, 1, 0.5);
			transSlider.setId("transSlider");
			transSlider.setMaxWidth(100);
			
			GridPane.setHalignment(plusButton, HPos.LEFT);
			GridPane.setHalignment(minusButton, HPos.LEFT);
			GridPane.setHalignment(aotButton, HPos.RIGHT);
			GridPane.setHalignment(transSlider, HPos.RIGHT);
			
			configPane.add(plusButton, 0, 0);
			configPane.add(minusButton, 1, 0);
			configPane.add(aotButton, 2, 0);
			configPane.add(transSlider, 3, 0);
			
			plusButton.setOnMouseClicked( this::onPlusClicked);
			plusButton.setOnMouseDragReleased( this::onPlusClicked);
		
			minusButton.setOnMouseReleased( event -> event.consume());
			minusButton.setOnMouseClicked(this::onMinusClicked);
			minusButton.setOnMouseDragReleased(this::onMinusClicked);
			
			aotButton.setOnAction( event -> {
				logger.fine("AOT button event: " + aotButton.isSelected());
				this.toggleAOT(aotButton.isSelected());
			});
			
			transSlider.setOnMouseDragged( event -> {
				setTransparency(transSlider.getValue());
				ConfigManager.getInstance().setTransparency(transSlider.getValue());
			});
			transSlider.setValue(ConfigManager.getInstance().getTransparency());
			setTransparency(transSlider.getValue());
			
			tabPane.setOnMousePressed( event -> {
					moveXinit = (int)event.getX();
					moveYinit = (int)event.getY();
				}
			);
			
			transSlider.setOnMouseReleased( event -> ConfigManager.getInstance().save());
			
			tabPane.setOnMouseDragged(new EventHandler<MouseEvent>() { //NOSONAR
				@Override
				public void handle(MouseEvent event) {
					MainWindow.instance.stage.setX(event.getScreenX()-moveXinit);
					MainWindow.instance.stage.setY(event.getScreenY()-moveYinit);
					
				}
			});
//			tabPane.setOnMouseReleased( event -> {
//				if (!(event.getSource() instanceof ProgressBar || event.getSource() instanceof ProgressPane || event.getSource() instanceof Label)) {
//					minusButton.setSelected(false);
//				}
//			});
						
			primaryStage.show();
			
			FXController.instance.loadTimers();
			
			pat = new ProgressAnimTimer(FXController.instance.timerMap);
			pat.start();
			
		} catch(Exception e) {
			logger.log(Level.SEVERE, "An exception occured while initializing the FXGUI: ", e);
		}
	}

	/**
	 * Adds a tab to the GUI with the specified gridpane information and title.
	 * @param gridColumns
	 * @param gridRows
	 * @param name
	 * @return
	 */
	public Tab addTab(int gridRows, int gridColumns, String name) {
		TimerTab tab = new TimerTab(name);
		tab.setClosable(false);
		GridPane gp = new GridPane();
		gp.setHgap(8);
		gp.setVgap(8);
		gp.getStyleClass().add("gridPane");
		
		for (int i = 0; i < gridColumns; i++) {
			ColumnConstraints cc = new ColumnConstraints();
			cc.setHgrow(Priority.ALWAYS);
			gp.getColumnConstraints().add(cc);
		}
		for (int i = 0; i < gridRows; i++) {
			RowConstraints rc = new RowConstraints();
			rc.setVgrow(Priority.NEVER);
			gp.getRowConstraints().add(rc);
		}
		
		tab.setContent(gp);
		tabPane.getTabs().add(tab);
		gp.getChildren().clear();
		return tab;
	}
	
	public Tab addDefaultTab() {
		TimerTab tab = new TimerTab(ConfigManager.getInstance().getDefaultTabName());
		tab.setClosable(false);
		GridPane gp = new GridPane();
		gp.setHgap(8);
		gp.setVgap(8);
		gp.getStyleClass().add("gridPane");
		
		for (int i = 0; i < ConfigManager.getInstance().getDefaultTabColumns(); i++) {
			ColumnConstraints cc = new ColumnConstraints();
			cc.setHgrow(Priority.ALWAYS);
			gp.getColumnConstraints().add(cc);
		}
		for (int i = 0; i < ConfigManager.getInstance().getDefaultTabRows(); i++) {
			RowConstraints rc = new RowConstraints();
			rc.setVgrow(Priority.NEVER);
			gp.getRowConstraints().add(rc);
		}
		
		tab.setContent(gp);
		tabPane.getTabs().add(tab);
		gp.getChildren().clear();
		return tab;
	}
	
	/**
	 * Adds the specified progress pane to the specified tab. Handles placement but does not add to map.
	 * @param progPane
	 * @param tab
	 * @return
	 */
	public ProgressPane addTimerBar(ProgressPane progPane, int tab) {
		
		if (tabPane.getTabs().isEmpty()) {
			addTab(ConfigManager.getInstance().getDefaultTabRows(), ConfigManager.getInstance().getDefaultTabColumns(), ConfigManager.getInstance().getDefaultTabName());
		}
		if (tab < 0) {
			tab = 0; //NOSONAR
		}
		
		int numChildren = ((GridPane) tabPane.getTabs().get(tab).getContent()).getChildren().size(); 
		int numRows = ((GridPane) tabPane.getTabs().get(tab).getContent()).getRowConstraints().size();
		int numColumns = ((GridPane) tabPane.getTabs().get(tab).getContent()).getColumnConstraints().size();
				
		if (numRows == 0) {
			int posX = numChildren/numColumns;
			int posY = numChildren%numColumns;
			((GridPane) tabPane.getTabs().get(tab).getContent()).add(progPane, posY, posX);
		} else if (numColumns == 0) {
			int posX = numChildren%numRows;
			int posY = numChildren/numRows;
			((GridPane) tabPane.getTabs().get(tab).getContent()).add(progPane, posY, posX);
		} else {
			int posX = numChildren%numRows;
			int posY = numChildren/numRows;
			((GridPane) tabPane.getTabs().get(tab).getContent()).add(progPane, posY, posX);
			logger.warning("You may encounter unexpected behavior by specifying rows and columns!");
		}
		return progPane;
	}
	
	/**
	 * Removes the timer bar and recalculates the appearance of the window. This will NOT remove the 
	 * bar-timer set from the map and it must be called before removing the timer.
	 * This method will determine the tab that needs to be reorganized and do so.
	 * @param pane - The ProgressPane to be removed
	 * @param biMap - The bidirectional map of ProgressPanes and Timers
	 */
	public void removeTimerBar(ProgressPane pane, BiMap<ProgressPane, Timer> biMap) {
		int tabNum = biMap.get(pane).getTab();
		Tab tab = this.tabPane.getTabs().get(tabNum);

		GridPane gp = (GridPane)tab.getContent();
		gp.getChildren().clear();
		biMap.forEach( (key, value) -> {
			if (value.getTab() == tabNum && key != pane) {
				addTimerBar(key, tabNum);
			}
		});
	}
	
	public void toggleAOT(boolean isOnTop) {
		this.stage.setAlwaysOnTop(isOnTop);
	}
	
	/**
	 * Returns the ObservableList of the tabs of the tabPane.
	 * @return
	 */
	public List<Tab> getTabList() {
		return tabPane.getTabs();
	}
	
	/**
	 * Returns the currently selected tab.
	 * @return
	 */
	public int getCurrentTab() {
		return this.tabPane.getSelectionModel().getSelectedIndex();
	}
	
	/**
	 * Set the transparency of the window to the specified value, 0-1
	 */
	public void setTransparency(double trans) {
		if (trans < 0.05)
			this.stage.setOpacity(0.05);
		else 
			this.stage.setOpacity(trans);
	}
	
	/**
	 * Sets position of the window
	 * @param x Horizontal position
	 * @param y Vertical position
	 */
	public void setWinPosition(int x, int y) {
		this.stage.setX(x);
		this.stage.setY(y);
	}
	
	/**
	 * Returns the tabPane object which stores the tabs.
	 * @return
	 */
	public TabPane getTabPane() {
		return this.tabPane;
	}
	
	public void onClickTimerBar(ProgressPane pane, MouseEvent event) {
		logger.fine("OnClickTimerBar fired");
		if (!minusButton.isSelected() && event.isShiftDown()) {
			AddTimerController.instance.showEditWindow(FXController.instance.timerMap.get(pane));
		} else if (minusButton.isSelected() && event.getButton()==MouseButton.PRIMARY) { //Remove timer
			FXController.instance.removeTimer(pane); 
		} else if (event.getButton()==MouseButton.SECONDARY) { //Reset timer as complete
			FXController.instance.resetTimerComplete(pane);
		} else { //Reset timer as incomplete
			FXController.instance.resetTimer(pane); 
		}
		minusButton.setSelected(false);
	}
	
	public void prepareTimerAnimation(BiMap<ProgressPane, Timer> map) {
		pat = new ProgressAnimTimer(map);
		pat.start();
	}
	
	public void setVisible(boolean isVisible) {
		if (isVisible) {
			stage.show();
		} else {
			stage.hide();
		}
	}
	
	public boolean isVisible() {
		return stage.isShowing();
	}
	
	void onPlusClicked(MouseEvent event) {
		logger.fine("PlusButton MouseEvent fired");
		if (event.isShiftDown()) {
			showNewTabNameDialog();
		} else {
			AddTimerController.instance.showCreateWindow();
		}
	}
	
	void onMinusClicked(MouseEvent event) {
		logger.fine("MinusButton MouseEvent fired");
		if (event.isShiftDown()) {
			showConfirmTabDeleteDialog();
			minusButton.setSelected(false);
		} 
	}
	
	/**
	 * The first entry of three dialogs to add a new tab.
	 */
	void showNewTabNameDialog() {
		TextInputDialog inputDialog = new TextInputDialog();
		inputDialog.setContentText("Enter a name for the new tab");
		inputDialog.setHeaderText(null);
		inputDialog.setTitle("New Tab Name");
		inputDialog.showAndWait()
			.filter(response -> !"".equals(response))
			.ifPresent( response -> showNewTabRowsDialog(response)); //NOSONAR
	}
	
	/**
	 * Second entry. This if it returns a value forwards to the third entry.
	 */
	void showNewTabRowsDialog(String name) {
		logger.info("showNewTabRowsDialog Was called with the string: " + name);
		TextInputDialog inputDialog = new TextInputDialog();
		inputDialog.getEditor().setTextFormatter(new TextFormatter<Integer>(new IntegerStringConverter()));
		inputDialog.setContentText("Enter the number of rows, or 0 to choose columns instead");
		inputDialog.getEditor().setText("0");
		inputDialog.setHeaderText(null);
		inputDialog.setTitle("New Tab Rows");
		inputDialog.showAndWait()
			.filter(response -> !"".equals(response))
			.ifPresent( response -> showNewTabColumnsDialog(name, Integer.parseInt(response))); //NOSONAR
	}
	
	/**
	 * Third entry. This if it returns a value actually creates the tab
	 */
	void showNewTabColumnsDialog(String name, int rows) {
		logger.info("showNewTabColumnsDialog was called with the data: " + name + " & " + rows);
		if (rows > 0) {
			finishNewTabDialogs(rows, 0, name);
			return;
		}
		TextInputDialog inputDialog = new TextInputDialog();
		inputDialog.setContentText("Enter the number of columns to apply to the tab");
		inputDialog.getEditor().setTextFormatter(new TextFormatter<Integer>(new IntegerStringConverter()));
		inputDialog.getEditor().setText("0");
		inputDialog.setHeaderText(null);
		inputDialog.setTitle("New Tab Columns");
		inputDialog.showAndWait()
			.filter(response -> !"".equals(response))
			.ifPresent( response -> this.finishNewTabDialogs(rows, Integer.parseInt(response), name)); 
	}
	
	void finishNewTabDialogs(int rows, int columns, String name) {
		if (rows == 0 && columns == 0) {
			addTab(columns, 4, name);
		} else {
			addTab(columns, rows, name);
		}
		FXController.instance.saveTimers();
	}
	
	void showConfirmTabDeleteDialog() {
		Dialog<ButtonType> dialog = new Dialog<>();
		dialog.getDialogPane().getButtonTypes().add(new ButtonType("Yes", ButtonData.OK_DONE));
		dialog.getDialogPane().getButtonTypes().add(new ButtonType("Cancel", ButtonData.CANCEL_CLOSE));
		dialog.setContentText("Are you sure you want to delete the current tab?");
		dialog.showAndWait()
			.filter(response -> response.getButtonData() == ButtonType.OK.getButtonData())
			.ifPresent( response -> FXController.instance.removeTimerTab(this.getCurrentTab()));
	}
}