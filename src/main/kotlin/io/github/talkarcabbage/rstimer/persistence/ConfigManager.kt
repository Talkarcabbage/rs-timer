package io.github.talkarcabbage.rstimer.persistence

import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.util.Properties
import java.util.logging.Level

import com.google.common.base.Throwables

import io.github.talkarcabbage.logger.LoggerManager

/**
 *
 * @author Talkarcabbage
 */
object ConfigManager {

	private var lastSave = 0L
	private const val CONFIGFILENAME = "rs-timer-config.cfg"
	private val logger = LoggerManager.getInstance().getLogger("ConfigManager")

	@Volatile
	var defaultTabName = "Main"
	@Volatile
	var defaultTabColumns = 0
	@Volatile
	var defaultTabRows = 5
	@Volatile
	var winHeight = 250
	@Volatile
	var winWidth = 400
	@Volatile
	var logLevel: Level = Level.INFO
	set(level) {field = level; LoggerManager.getInstance().setGlobalLoggingLevel(level)}
	@Volatile
	var framesPerUpdate = 15
	@Volatile
	var transparency = 1.0
	@Volatile
	var saveGuiResizes = true

	/**
	 * Loads the application's properties from the config file.
	 * This method also sets the logging level to the stored value.
	 */
	@Synchronized
	fun load() {
		if (!File(CONFIGFILENAME).exists()) {
			save()
		}
		val prop = Properties()
		try {
			FileInputStream(CONFIGFILENAME).use { fis ->
				prop.load(fis)

			}
		} catch (e: IOException) {
			logger.severe("Error while loading properties file:")
			logger.severe(Throwables.getStackTraceAsString(e))
		}

		logLevel = getPropsLogLevel(prop, "logLevel", logLevel)
		LoggerManager.getInstance().setGlobalLoggingLevel(logLevel) //To set the logging as early as possible.
		defaultTabName = getPropsString(prop, "defaultTabName", defaultTabName)
		defaultTabColumns = getPropsInt(prop, "defaultTabColumns", defaultTabColumns)
		defaultTabRows = getPropsInt(prop, "defaultTabRows", defaultTabRows)
		winHeight = getPropsInt(prop, "winHeight", winHeight)
		winWidth = getPropsInt(prop, "winWidth", winWidth)
		framesPerUpdate = getPropsInt(prop, "framesPerUpdate", framesPerUpdate)
		transparency = getPropsDouble(prop, "transparency", transparency)
		saveGuiResizes = getPropsBoolean(prop, "saveGuiResizes", saveGuiResizes)
	}

	@Synchronized
	fun save() {
		logger.fine {
			val executingMethod = Exception().stackTrace[5]
			"An attempt to save has been made by $executingMethod"
		}
		if (System.currentTimeMillis() - lastSave < 500) {
			logger.fine{"Something tried to save the configuration too soon! Discarding the save attempt."}
			return
		}
		lastSave = System.currentTimeMillis()
		logger.config("Saving configuration file")
		val prop = Properties()
		prop.setProperty("defaultTabName", defaultTabName)
		prop.setProperty("defaultTabColumns", defaultTabColumns.toString())
		prop.setProperty("defaultTabRows", defaultTabRows.toString())
		prop.setProperty("winHeight", winHeight.toString())
		prop.setProperty("winWidth", winWidth.toString())
		prop.setProperty("logLevel", logLevel.toString())
		prop.setProperty("framesPerUpdate", framesPerUpdate.toString())
		prop.setProperty("transparency", transparency.toString())
		prop.setProperty("saveGuiResizes", saveGuiResizes.toString())
		try {
			FileOutputStream(CONFIGFILENAME).use { fos -> prop.store(fos, "This file stores configuration options for the RS Timer") }
		} catch (e: IOException) {
			logger.severe("Error while saving properties file:")
			logger.severe(Throwables.getStackTraceAsString(e))
		}
	}

	internal fun getPropsString(props: Properties, id: String, def: String): String {
		logger.config("Procesing Configuration: "+id+" with value '"+props.getProperty(id)+"' with default: "+def) //NOSONAR
		return props.getProperty(id, def)
	}

	internal fun getPropsInt(props: Properties, id: String, def: Int): Int {
		try {
			logger.config { "Procesing Configuration: "+id+" with value '"+props.getProperty(id)+"' with default: "+def }
			return Integer.parseInt(props.getProperty(id))
		} catch (e: NumberFormatException) { //NOSONAR
			logger.config("An acceptable Exception was caught while loading integer from config "+id+":"+e.message)
			return def
		} catch (e: NullPointerException) {
			logger.config("An acceptable Exception was caught while loading integer from config "+id+":"+e.message)
			return def
		}

	}

	internal fun getPropsLogLevel(props: Properties, id: String, def: Level): Level {
		try {
			logger.config { "Procesing Configuration: "+id+" with value '"+props.getProperty(id)+"' with default: "+def }
			return Level.parse(props.getProperty(id))
		} catch (e: IllegalArgumentException) { //NOSONAR
			logger.config("An acceptable Exception was caught while loading log level value from config "+id+":"+e.message)
			return def
		} catch (e: NullPointerException) {
			logger.config("An acceptable Exception was caught while loading log level value from config "+id+":"+e.message)
			return def
		}

	}

	internal fun getPropsDouble(props: Properties, id: String, def: Double): Double {
		try {
			logger.config { "Procesing Configuration: "+id+" with value '"+props.getProperty(id)+"' with default: "+def }
			return java.lang.Double.parseDouble(props.getProperty(id))
		} catch (e: NumberFormatException) { //NOSONAR
			logger.config("An acceptable Exception was caught while loading double value from config "+id+":"+e.message)
			return def
		} catch (e: NullPointerException) {
			logger.config("An acceptable Exception was caught while loading double value from config "+id+":"+e.message)
			return def
		}

	}

	internal fun getPropsBoolean(props: Properties, id: String, def: Boolean): Boolean {
		if (props.containsKey(id)) {
			return props.getProperty(id).toBoolean()
		}
		return def
	}
}