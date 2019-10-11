package io.github.talkarcabbage.rstimer.fxgui

import java.util.ArrayList
import java.util.logging.Level

import com.google.common.collect.BiMap
import com.sun.org.apache.bcel.internal.util.SecuritySupport.getResourceAsStream

import io.github.talkarcabbage.logger.LoggerManager
import io.github.talkarcabbage.rstimer.FXController
import io.github.talkarcabbage.rstimer.Timer
import io.github.talkarcabbage.rstimer.newtimers.NewTimer
import io.github.talkarcabbage.rstimer.persistence.ConfigManager
import io.github.talkarcabbage.rstimer.persistence.LoadManager
import io.github.talkarcabbage.rstimer.persistence.SaveManager
import javafx.application.Application
import javafx.application.Platform
import javafx.event.EventHandler
import javafx.geometry.HPos
import javafx.geometry.VPos
import javafx.scene.Scene
import javafx.scene.control.*
import javafx.scene.control.Alert.AlertType
import javafx.scene.control.ButtonBar.ButtonData
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import javafx.scene.input.MouseButton
import javafx.scene.input.MouseDragEvent
import javafx.scene.input.MouseEvent
import javafx.scene.layout.BorderPane
import javafx.scene.layout.ColumnConstraints
import javafx.scene.layout.GridPane
import javafx.scene.layout.Priority
import javafx.scene.layout.RowConstraints
import javafx.stage.Stage
import javafx.stage.StageStyle
import javafx.util.converter.IntegerStringConverter
import java.io.File
import java.util.function.Consumer

private const val mainWinMinWidth = 160.0
private const val mainWinMinHeight = 120.0

/**
 *
 * @author Talkarcabbage
 */
class MainWindow : Application() {

	/**
	 * Returns the tabPane object which stores the tabs.
	 * @return
	 */
	lateinit var tabPane: TabPane
		internal set
	internal var moveXinit: Int = 0
	internal var moveYinit: Int = 0

	internal var resizeXinit: Int = 0
	internal var resizeYinit: Int = 0
	internal var resizeXStartSize: Int = 0
	internal var resizeYStartSize: Int = 0

	internal var xBottomRightDist = 0.0
	internal var yBottomRightDist = 0.0
	internal var isMovingOnCorner = false

	internal lateinit var stage: Stage
	internal var tempList = ArrayList<ProgressPane>()
	internal lateinit var minusButton: ToggleButton
	internal lateinit var pat: ProgressAnimTimer
	internal lateinit var patNew: ProgressAnimNewTimer

	/**
	 * Returns the ObservableList of the tabs of the tabPane.
	 * @return
	 */
	val tabList: MutableList<Tab>
		get() = tabPane.tabs

	/**
	 * Returns the currently selected tab.
	 * @return
	 */
	val currentTab: Int
		get() = this.tabPane.selectionModel.selectedIndex

	var isVisible: Boolean
		get() = stage.isShowing
		set(isVisible) = if (isVisible) {
			stage.show()
		} else {
			stage.hide()
		}

	override fun start(primaryStage: Stage) {
		try {
			Platform.setImplicitExit(false)
			primaryStage.setOnCloseRequest {
				FXController.instance.destroyTrayIcon()
				System.exit(0)
			}

			AddTimerController.createRoot()
			instance = this //NOSONAR
			stage = primaryStage
			val rootPane = BorderPane()
			val scene = Scene(rootPane, ConfigManager.winWidth.toDouble(), ConfigManager.winHeight.toDouble())
			scene.stylesheets.add(javaClass.getResource("/css/application.css").toExternalForm())
			primaryStage.scene = scene
			primaryStage.initStyle(StageStyle.UNDECORATED)
			rootPane.id = "win"

			scene.root.styleClass.add("main-root")

			val configPane = GridPane()
			configPane.hgap = 3.0
			val plusButtonColumn1 = ColumnConstraints(15.0)
			val minusButtonColumn2 = ColumnConstraints(15.0)
			val aotCheckboxColumn3 = ColumnConstraints(15.0)
			val aotSliderColumn4 = ColumnConstraints(20.0, 100.0, java.lang.Double.MAX_VALUE)
			val cornerGripColumn5 = ColumnConstraints(14.0, 14.0, 14.0)
			aotSliderColumn4.hgrow = Priority.ALWAYS
			configPane.columnConstraints.addAll(plusButtonColumn1, minusButtonColumn2, aotCheckboxColumn3, aotSliderColumn4, cornerGripColumn5)

			tabPane = TabPane()

			rootPane.bottom = configPane
			rootPane.center = tabPane

			configPane.styleClass.add("configPane")

			val plusButton = Button()
			plusButton.id = "plusButton"
			minusButton = ToggleButton()
			minusButton.id = "minusButton"
			val aotButton = ToggleButton()
			aotButton.id = "aotButton"
			val transSlider = Slider(0.0, 1.0, 0.5)
			transSlider.id = "transSlider"
			transSlider.maxWidth = 100.0
			val cornerGripImage = Image(javaClass.getResourceAsStream("/images/cornerGrip.gif"))
			val cornerGrip = Label("", ImageView(cornerGripImage))
			cornerGrip.id = "cornerGrip"
			cornerGrip.maxWidth = 14.0

			GridPane.setHalignment(plusButton, HPos.LEFT)
			GridPane.setHalignment(minusButton, HPos.LEFT)
			GridPane.setHalignment(aotButton, HPos.RIGHT)
			GridPane.setHalignment(transSlider, HPos.RIGHT)
			GridPane.setHalignment(cornerGrip, HPos.RIGHT)
			GridPane.setValignment(cornerGrip, VPos.BOTTOM)

			configPane.add(plusButton, 0, 0)
			configPane.add(minusButton, 1, 0)
			configPane.add(aotButton, 2, 0)
			configPane.add(transSlider, 3, 0)
			configPane.add(cornerGrip, 4, 0)

			plusButton.onMouseClicked = EventHandler { this.onPlusClicked(it) }
			plusButton.onMouseDragReleased = EventHandler<MouseDragEvent> { this.onPlusClicked(it) }

			minusButton.onMouseReleased = EventHandler { it.consume() }
			minusButton.onMouseClicked = EventHandler { this.onMinusClicked(it) }
			minusButton.onMouseDragReleased = EventHandler<MouseDragEvent> { this.onMinusClicked(it) }

			aotButton.setOnAction {
				logger.fine("AOT button event: "+aotButton.isSelected)
				this.toggleAOT(aotButton.isSelected)
			}

			transSlider.setOnMouseDragged {
				setTransparency(transSlider.value)
				ConfigManager.transparency = transSlider.value
			}
			transSlider.value = ConfigManager.transparency
			setTransparency(transSlider.value)


			transSlider.style += "-fx-border-insets: 32px; -fx-background-insets: 32px; -fx-padding-right: 32px;"

			tabPane.setOnMousePressed { event ->
				moveXinit = event.x.toInt()+3
				moveYinit = event.y.toInt()+3
			}

			transSlider.setOnMouseReleased { ConfigManager.save() }

			tabPane.onMouseDragged = EventHandler { event ->
				MainWindow.instance.stage.x = event.screenX-moveXinit
				MainWindow.instance.stage.y = event.screenY-moveYinit
			}

			primaryStage.minHeight = 140.0
			primaryStage.minWidth = 250.0

			configPane.setOnMousePressed { event ->
				moveXinit = event.x.toInt()+3
				moveYinit = event.y.toInt()+tabPane.height.toInt()+3
				resizeXinit = event.screenX.toInt()
				resizeYinit = event.screenY.toInt()
				resizeXStartSize = stage.width.toInt()
				resizeYStartSize = stage.height.toInt()
				xBottomRightDist = this.stage.width-(resizeXinit-this.stage.x)
				yBottomRightDist = this.stage.height-(resizeYinit-this.stage.y)
				isMovingOnCorner = (xBottomRightDist<20 && xBottomRightDist>3 && yBottomRightDist<22 && yBottomRightDist>3)
			} //jank

			configPane.onMouseDragged = EventHandler { event ->
				if (isMovingOnCorner) {
					MainWindow.instance.stage.setWidthWithMin = resizeXStartSize+(event.screenX)-resizeXinit.toInt()
					MainWindow.instance.stage.setHeightWithMin = resizeYStartSize+(event.screenY)-resizeYinit.toInt()
				} else {
					MainWindow.instance.stage.x = event.screenX-moveXinit
					MainWindow.instance.stage.y = event.screenY-moveYinit
				}
			}

			configPane.onMouseReleased = EventHandler {
				if (ConfigManager.saveGuiResizes) {
					ConfigManager.winWidth = stage.width.toInt()
					ConfigManager.winHeight = stage.height.toInt()
					ConfigManager.save()
				}
			}

			rootPane.setOnMousePressed { event ->
				xBottomRightDist = this.stage.width-(resizeXinit-this.stage.x)
				yBottomRightDist = this.stage.height-(resizeYinit-this.stage.y)
				isMovingOnCorner = (xBottomRightDist<20 && xBottomRightDist>3 && yBottomRightDist<22 && yBottomRightDist>3)
				if (!isMovingOnCorner) return@setOnMousePressed
				moveXinit = event.x.toInt()+3
				moveYinit = event.y.toInt()+tabPane.height.toInt()+3
				resizeXinit = event.screenX.toInt()
				resizeYinit = event.screenY.toInt()
				resizeXStartSize = stage.width.toInt()
				resizeYStartSize = stage.height.toInt()
				xBottomRightDist = this.stage.width-(resizeXinit-this.stage.x)
				yBottomRightDist = this.stage.height-(resizeYinit-this.stage.y)
				isMovingOnCorner = (xBottomRightDist<20 && xBottomRightDist>3 && yBottomRightDist<22 && yBottomRightDist>3)
			}

			rootPane.onMouseDragged = EventHandler { event ->
				if (isMovingOnCorner) {
					MainWindow.instance.stage.setWidthWithMin = resizeXStartSize+(event.screenX)-resizeXinit.toInt()
					MainWindow.instance.stage.setHeightWithMin = resizeYStartSize+(event.screenY)-resizeYinit.toInt()
				} else {
					/*MainWindow.instance.stage.x = event.screenX-moveXinit
					MainWindow.instance.stage.y = event.screenY-moveYinit
					 */
				}
			}

			rootPane.setOnMouseReleased {
				if (!it.isConsumed) {
					minusButton.isSelected = false
				}
				if (ConfigManager.saveGuiResizes) {
					ConfigManager.winWidth = stage.width.toInt()
					ConfigManager.winHeight = stage.height.toInt()
					ConfigManager.save()
				}
			}


			//			tabPane.setOnMouseReleased( event -> {
			//				if (!(event.getSource() instanceof ProgressBar || event.getSource() instanceof ProgressPane || event.getSource() instanceof Label)) {
			//					minusButton.setSelected(false);
			//				}
			//			});

			primaryStage.show()

			if (!File(SaveManager.SAVE_FILE_LOCATION).exists() && File("timers.cfg").exists()) {
				val alert = Alert(AlertType.INFORMATION, ""+
						"Your old timers are being imported from timers.cfg " +
						"to the new format at ${SaveManager.SAVE_FILE_LOCATION}. Please double check your timers! " +
						"If you need to reimport again later, move or delete the new file.")
				alert.isResizable = true
				alert.headerText = "Converter"
				alert.height = 250.0
				alert.show()
				FXController.instance.loadLegacyTimers()
			} else {
				logger.fine("Found an existing data file for the newer timer format.")
				FXController.instance.loadNewTimers()
			}

			patNew = ProgressAnimNewTimer(FXController.instance.newTimerMap)
			patNew.start()


		} catch (e: Exception) {
			logger.log(Level.SEVERE, "An exception occured while initializing the FXGUI: ", e)
		}

	}

	/**
	 * Adds a tab to the GUI with the specified gridpane information and title.
	 * @param gridColumns
	 * @param gridRows
	 * @param name
	 * @return
	 */
	fun addTab(gridRows: Int, gridColumns: Int, name: String): Tab {
		val tab = TimerTab(name)
		tab.isClosable = false
		val gp = GridPane()
		gp.hgap = 8.0
		gp.vgap = 8.0
		gp.styleClass.add(GRID_PANE_CSS_CLASS)

		for (i in 0 until gridColumns) {
			val cc = ColumnConstraints()
			cc.hgrow = Priority.ALWAYS
			gp.columnConstraints.add(cc)
		}
		for (i in 0 until gridRows) {
			val rc = RowConstraints()
			rc.vgrow = Priority.NEVER
			gp.rowConstraints.add(rc)
		}

		tab.content = gp
		tabPane.tabs.add(tab)
		gp.children.clear()
		return tab
	}

	fun addTabNew(tab: Tab) {
		tabPane.tabs.add(tab)
	}

	fun addDefaultTab(): Tab {
		val tab = TimerTab(ConfigManager.defaultTabName)
		tab.isClosable = false
		val gp = GridPane()
		gp.hgap = 8.0
		gp.vgap = 8.0
		gp.styleClass.add(GRID_PANE_CSS_CLASS)

		for (i in 0 until ConfigManager.defaultTabColumns) {
			val cc = ColumnConstraints()
			cc.hgrow = Priority.ALWAYS
			gp.columnConstraints.add(cc)
		}
		for (i in 0 until ConfigManager.defaultTabRows) {
			val rc = RowConstraints()
			rc.vgrow = Priority.NEVER
			gp.rowConstraints.add(rc)
		}

		tab.content = gp
		tabPane.tabs.add(tab)
		gp.children.clear()
		return tab
	}

	/**
	 * Adds the specified progress pane to the specified tab. Handles placement but does not add to map.
	 * @param progPane
	 * @param tab
	 * @return
	 */
	fun addTimerBar(progPane: ProgressPane, tab: Int): ProgressPane {
		var theTab = tab

		if (tabPane.tabs.isEmpty()) {
			addTab(ConfigManager.defaultTabRows, ConfigManager.defaultTabColumns, ConfigManager.defaultTabName)
		}
		if (theTab < 0) {
			theTab = 0
		}

		val numChildren = (tabPane.tabs[theTab].content as GridPane).children.size
		val numRows = (tabPane.tabs[theTab].content as GridPane).rowConstraints.size
		val numColumns = (tabPane.tabs[theTab].content as GridPane).columnConstraints.size

		if (numRows==0) {
			val posX = numChildren/numColumns
			val posY = numChildren%numColumns
			(tabPane.tabs[theTab].content as GridPane).add(progPane, posY, posX)
		} else if (numColumns==0) {
			val posX = numChildren%numRows
			val posY = numChildren/numRows
			(tabPane.tabs[theTab].content as GridPane).add(progPane, posY, posX)
		} else {
			val posX = numChildren%numRows
			val posY = numChildren/numRows
			(tabPane.tabs[theTab].content as GridPane).add(progPane, posY, posX)
			logger.warning("You may encounter unexpected behavior by specifying rows and columns!")
		}
		return progPane
	}

	/**
	 * Removes the timer bar and recalculates the appearance of the window. This will NOT remove the
	 * bar-timer set from the map and it must be called before removing the timer.
	 * This method will determine the tab that needs to be reorganized and do so.
	 * @param pane - The ProgressPane to be removed
	 * @param biMap - The bidirectional map of ProgressPanes and Timers
	 */
	fun removeTimerBar(pane: ProgressPane, biMap: BiMap<ProgressPane, Timer>) {
		val timer = biMap[pane]
		if (timer is Timer) {
			val tabNum = timer.tab
			val tab = this.tabPane.tabs[tabNum]

			val gp = tab.content as GridPane
			gp.children.clear()
			biMap.forEach { (key, value) ->
				if (value.tab==tabNum && key!==pane) {
					addTimerBar(key, tabNum)
				}
			}
		}
	}

	/**
	 * Removes the timer bar and recalculates the appearance of the window. This will NOT remove the
	 * bar-timer set from the map and it must be called before removing the timer.
	 * This method will determine the tab that needs to be reorganized and do so.
	 * @param pane - The ProgressPane to be removed
	 * @param biMap - The bidirectional map of ProgressPanes and Timers
	 */
	fun removeNewTimerBar(pane: ProgressPane, biMap: BiMap<ProgressPane, NewTimer>) {
		val timer = biMap[pane]
		if (timer is NewTimer) {
			val tabNum = timer.tab
			val tab = this.tabPane.tabs[tabNum]

			val gp = tab.content as GridPane
			gp.children.clear()
			biMap.forEach { (key, value) ->
				if (value.tab==tabNum && key!==pane) {
					addTimerBar(key, tabNum)
				}
			}
		}
	}

	fun toggleAOT(isOnTop: Boolean) {
		this.stage.isAlwaysOnTop = isOnTop
	}

	/**
	 * Set the transparency of the window to the specified value, 0-1
	 */
	fun setTransparency(trans: Double) {
		if (trans < 0.05)
			this.stage.opacity = 0.05
		else
			this.stage.opacity = trans
	}

	/**
	 * Sets position of the window
	 * @param x Horizontal position
	 * @param y Vertical position
	 */
	fun setWinPosition(x: Int, y: Int) {
		this.stage.x = x.toDouble()
		this.stage.y = y.toDouble()
	}

	fun onClickTimerBar(pane: ProgressPane, event: MouseEvent) {
		logger.fine("OnClickTimerBar fired")
		if (!minusButton.isSelected && event.isShiftDown) {
			AddTimerController.instance?.showEditWindow(FXController.instance.newTimerMap[pane]!!)
		} else if (minusButton.isSelected && event.button==MouseButton.PRIMARY) { //Remove timer
			FXController.instance.removeNewTimer(pane)
		} else if (event.button==MouseButton.SECONDARY) { //Reset timer as complete
			FXController.instance.resetNewTimerComplete(pane)
		} else { //Reset timer as incomplete
			FXController.instance.resetNewTimer(pane)
		}
		minusButton.isSelected = false
	}

	internal fun onPlusClicked(event: MouseEvent) {
		logger.fine("PlusButton MouseEvent fired")
		if (event.isShiftDown) {
			showNewTabNameDialog()
		} else {
			AddTimerController.instance!!.showCreateWindow()
		}
	}

	internal fun onMinusClicked(event: MouseEvent) {
		logger.fine("MinusButton MouseEvent fired")
		if (event.isShiftDown) {
			showConfirmTabDeleteDialog()
			minusButton.isSelected = false
		}
	}

	/**
	 * The first entry of three dialogs to add a new tab.
	 */
	internal fun showNewTabNameDialog() {
		val inputDialog = TextInputDialog()
		inputDialog.contentText = "Enter a name for the new tab"
		inputDialog.headerText = null
		inputDialog.title = "New Tab Name"
		inputDialog.showAndWait()
				.filter { response -> ""!=response }
				.ifPresent(Consumer<String> { this.showNewTabRowsDialog(it) })
	}

	/**
	 * Second entry. This if it returns a value forwards to the third entry.
	 */
	internal fun showNewTabRowsDialog(name: String) {
		logger.info { "showNewTabRowsDialog Was called with the string: $name" }
		val inputDialog = TextInputDialog()
		inputDialog.editor.textFormatter = TextFormatter(IntegerStringConverter())
		inputDialog.contentText = "Enter the number of rows, or 0 to choose columns instead"
		inputDialog.editor.text = "0"
		inputDialog.headerText = null
		inputDialog.title = "New Tab Rows"
		inputDialog.showAndWait()
				.filter { response -> ""!=response }
				.ifPresent { response -> showNewTabColumnsDialog(name, Integer.parseInt(response)) }
	}

	/**
	 * Third entry. This if it returns a value actually creates the tab
	 */
	internal fun showNewTabColumnsDialog(name: String, rows: Int) {
		logger.info { "showNewTabColumnsDialog was called with the data: $name & $rows" }
		if (rows > 0) {
			finishNewTabDialogs(rows, 0, name)
			return
		}
		val inputDialog = TextInputDialog()
		inputDialog.contentText = "Enter the number of columns to apply to the tab"
		inputDialog.editor.textFormatter = TextFormatter(IntegerStringConverter())
		inputDialog.editor.text = "0"
		inputDialog.headerText = null
		inputDialog.title = "New Tab Columns"
		inputDialog.showAndWait()
				.filter { response -> ""!=response }
				.ifPresent { response -> this.finishNewTabDialogs(rows, Integer.parseInt(response), name) }
	}

	internal fun finishNewTabDialogs(rows: Int, columns: Int, name: String) {
		if (rows==0 && columns==0) {
			addTab(columns, 4, name)
		} else {
			addTab(columns, rows, name)
		}
		FXController.instance.saveTimers()
	}

	internal fun showConfirmTabDeleteDialog() {
		val dialog = Dialog<ButtonType>()
		dialog.dialogPane.buttonTypes.add(ButtonType("Yes", ButtonData.OK_DONE))
		dialog.dialogPane.buttonTypes.add(ButtonType("Cancel", ButtonData.CANCEL_CLOSE))
		dialog.contentText = "Are you sure you want to delete the current tab?"
		dialog.showAndWait()
				.filter { response -> response.buttonData==ButtonType.OK.buttonData }
				.ifPresent { FXController.instance.removeNewTimerTab(this.currentTab) }
	}

	fun onClickNewTimerBar(pane: ProgressPane, event: MouseEvent) {
		logger.fine("OnClickTimerBar fired (new Timers")
		if (!minusButton.isSelected && event.isShiftDown) {
			AddTimerController.instance!!.showEditWindow(FXController.instance.newTimerMap[pane]!!);
		} else if (minusButton.isSelected && event.button==MouseButton.PRIMARY) { //Remove timer
			FXController.instance.removeNewTimer(pane)
			minusButton.isSelected = false
			//TODO new remove method for new timers and decouple : FXController.instance.removeTimer(pane);
		} else if (event.button==MouseButton.SECONDARY) { //Reset timer as complete
			FXController.instance.resetNewTimerComplete(pane)
		} else { //Reset timer as incomplete
			FXController.instance.resetNewTimer(pane)
		}
	}

	companion object {

		internal val logger = LoggerManager.getInstance().getLogger("FXWin")

		internal val GRID_PANE_CSS_CLASS = "gridPane"
		lateinit var instance: MainWindow //NOSONAR

		//Used to set the style of the ColorProgress for a different completed-style.
		internal val INCSTRING = "progress-bar-incomplete"
		internal val COMSTRING = "progress-bar-complete"
		@JvmStatic
		fun launchWrap(args: Array<String>) {
			Application.launch(MainWindow::class.java, *args)
		}

		/**
		 * Creates and returns a tab to add to the GUI with the specified gridpane information and title
		 * @param gridRows
		 * @param gridColumns
		 * @param name
		 * @return
		 */
		fun createTab(gridRows: Int, gridColumns: Int, name: String): Tab {
			val tab = TimerTab(name)
			tab.isClosable = false
			val gp = GridPane()
			gp.hgap = 8.0
			gp.vgap = 8.0
			gp.styleClass.add(GRID_PANE_CSS_CLASS)

			for (i in 0 until gridColumns) {
				val cc = ColumnConstraints()
				cc.hgrow = Priority.ALWAYS
				gp.columnConstraints.add(cc)
			}
			for (i in 0 until gridRows) {
				val rc = RowConstraints()
				rc.vgrow = Priority.NEVER
				gp.rowConstraints.add(rc)
			}

			tab.content = gp
			gp.children.clear()
			return tab

		}
	}

}

var Stage.setWidthWithMin: Double
	get() {return this.width}
	set(it: Double) {
		if (it >= mainWinMinWidth)
			this.width = it
		else
			this.width = mainWinMinWidth
	}

var Stage.setHeightWithMin: Double
	get() {return this.height}
	set(it: Double) {
		if (it >= mainWinMinHeight)
			this.height = it
		else
			this.height = mainWinMinHeight
	}