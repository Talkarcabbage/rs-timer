package io.github.talkarcabbage.rstimer.fxgui

import javafx.event.EventHandler
import javafx.geometry.HPos
import javafx.geometry.VPos
import javafx.scene.Node
import javafx.scene.Parent
import javafx.scene.Scene
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import javafx.scene.layout.ColumnConstraints
import javafx.scene.layout.GridPane
import javafx.scene.layout.Priority
import javafx.scene.layout.Region.USE_PREF_SIZE
import javafx.stage.Stage
import javafx.stage.StageStyle
import tornadofx.*

class MainWindowApp : App(MainWindowTornado::class) {
	override fun start(stage: Stage) {
		stage.initStyle(StageStyle.UNDECORATED)
		super.start(stage)
	}
}

class MainWindowTornado : View("My View") {
	override val root = borderpane {
		stylesheets += MainWindow::class.java.getResource("/css/application.css").toExternalForm()
		prefWidth = 600.0
		prefHeight = 400.0
		styleClass.add("main-root")
		bottom = gridpane { //configpane
			hgap = 3.0
			styleClass.add("configPane")
			row {
				this += button {
					id = "plusButton"
					gridpaneColumnConstraints {
						fixedWidth = 15.0
						halignment = HPos.LEFT
					}
					onMouseClicked = EventHandler {
						//TODO
					}
				}
				this += togglebutton("") {
					id = "minusButton"
					isSelected = false
					gridpaneColumnConstraints {
						fixedWidth = 15.0
						halignment = HPos.LEFT
					}
					onMouseClicked = EventHandler {
						//TODO
					}
				}
				this += togglebutton("") {
					id = "aotButton"
					isSelected = false
					gridpaneColumnConstraints {
						fixedWidth = 15.0
						halignment = HPos.RIGHT
					}
					onMouseClicked = EventHandler {
						if (currentStage is Stage)
							currentStage?.isAlwaysOnTop = !((currentStage as Stage).isAlwaysOnTop)
					}
				}
				this += slider(0.0, 1.0, 1.0) {
					id="transSlider"
					maxWidth = 100.0
					gridpaneColumnConstraints {
						minWidth = 20.0
						prefWidth = 100.0
						maxWidth = Double.MAX_VALUE
						halignment = HPos.RIGHT
						hgrow = Priority.ALWAYS
						println("Yes")
					}
					//this.style += "-fx-border-insets: 32px; -fx-background-insets: 32px; -fx-padding-right: 32px;"
					//style(true) {
						//borderInsets = multi(box(32.px))
						//backgroundInsets = multi(box(32.px))
						//padding = box(0.px,48.px,0.px,0.px)
					//}

				}
				this += label(graphic=ImageView(Image(MainWindow::class.java.getResourceAsStream("/images/cornerGrip.gif")))) {
					id="cornerGrip"
					maxWidth = 14.0
					gridpaneColumnConstraints {
						fixedWidth = 14.0
						halignment = HPos.RIGHT
					}
					gridpaneConstraints {
						vAlignment = VPos.BOTTOM
					}
				}
			}
		}
		center = tabpane {

		}
	}
}

fun ColumnConstraints.setHgrowReturn(value: Priority): ColumnConstraints {
	this.hgrow = value
	return this
}

fun Parent.columConstraintsWidth(width: Double): ColumnConstraints {
	return ColumnConstraints(width)
}

var ColumnConstraints.fixedWidth: Double
	get() = this.prefWidth
	set(width) {
		this.minWidth = USE_PREF_SIZE
		this.prefWidth = width
		this.maxWidth = USE_PREF_SIZE
	}