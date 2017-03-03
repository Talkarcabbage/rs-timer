package com.minepop.talkar.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.common.base.Throwables;
import com.minepop.talkar.util.logging.LoggerConstructor;

/**
 * 
 * @author Talkarcabbage
 *
 */
public class ConfigManager {

	private static final Logger logger = LoggerConstructor.getLogger("ConfigManager");

	private static ConfigManager instance;
	
	private static final String CONFIGFILENAME = "rs-timer-config.cfg";
	
	private volatile String defaultTabName = "Main";
	private volatile int defaultTabColumns = 0;
	private volatile int defaultTabRows = 5;
	private volatile int winHeight = 250;
	private volatile int winWidth = 400;
	private volatile Level logLevel = Level.INFO;
	private volatile int framesPerUpdate = 15;
	private volatile double transparency = 1;

	ConfigManager() {}
	
	public String getDefaultTabName() {
		return defaultTabName;
	}

	public void setDefaultTabName(String defaultTabName) {
		this.defaultTabName = defaultTabName;
	}

	public int getDefaultTabColumns() {
		return defaultTabColumns;
	}

	public void setDefaultTabColumns(int defaultTabColumns) {
		this.defaultTabColumns = defaultTabColumns;
	}

	public int getDefaultTabRows() {
		return defaultTabRows;
	}

	public void setDefaultTabRows(int defaultTabRows) {
		this.defaultTabRows = defaultTabRows;
	}

	public int getWinHeight() {
		return winHeight;
	}

	public void setWinHeight(int winHeight) {
		this.winHeight = winHeight;
	}

	public int getWinWidth() {
		return winWidth;
	}

	public void setWinWidth(int winWidth) {
		this.winWidth = winWidth;
	}

	public Level getLogLevel() {
		return logLevel;
	}

	public void setLogLevel(Level logLevel) {
		this.logLevel = logLevel;
		LoggerConstructor.setGlobalLoggingLevel(logLevel);
	}

	public int getFramesPerUpdate() {
		return framesPerUpdate;
	}

	public void setFramesPerUpdate(int framesPerUpdate) {
		this.framesPerUpdate = framesPerUpdate;
	}

	public double getTransparency() {
		return transparency;
	}

	public void setTransparency(double transparency) {
		this.transparency = transparency;
	}

	public static ConfigManager getInstance() {
		return instance == null ? (instance = new ConfigManager()) : instance;
	}
	
	/**
	 * Loads the application's properties from the config file.
	 * This method also sets the logging level to the stored value.
	 */
	public synchronized void load() {
		if (!(new File(CONFIGFILENAME).exists())) {
			save();
		}
		Properties prop = new Properties();
		try (FileInputStream fis = new FileInputStream(CONFIGFILENAME)) {
			prop.load(fis);
			
		} catch (IOException e) {
			logger.severe("Error while loading properties file:");
			logger.severe(Throwables.getStackTraceAsString(e));
		} 
		
		logLevel = getPropsLogLevel(prop, "logLevel", logLevel);
		LoggerConstructor.setGlobalLoggingLevel(logLevel); //To set the logging as early as possible.
		defaultTabName = getPropsString(prop, "defaultTabName", defaultTabName);
		defaultTabColumns = getPropsInt(prop, "defaultTabColumns", defaultTabColumns);
		defaultTabRows = getPropsInt(prop, "defaultTabRows", defaultTabRows);
		winHeight = getPropsInt(prop, "winHeight", winHeight);
		winWidth = getPropsInt(prop, "winWidth", winWidth);
		framesPerUpdate = getPropsInt(prop, "framesPerUpdate", framesPerUpdate);
		transparency = getPropsDouble(prop, "transparency", transparency);
	}
	
	public synchronized void save() {
		logger.config("Saving configuration file");
		Properties prop = new Properties();
		prop.setProperty("defaultTabName", defaultTabName);
		prop.setProperty("defaultTabColumns", Integer.toString(defaultTabColumns));
		prop.setProperty("defaultTabRows", Integer.toString(defaultTabRows));
		prop.setProperty("winHeight", Integer.toString(winHeight));
		prop.setProperty("winWidth", Integer.toString(winWidth));
		prop.setProperty("logLevel", logLevel.toString());
		prop.setProperty("framesPerUpdate", Integer.toString(framesPerUpdate));
		prop.setProperty("transparency", Double.toString(transparency));
		
		try (FileOutputStream fos = new FileOutputStream(CONFIGFILENAME)) {
			prop.store(fos, "This file stores configuration options for the RS Timer");
		} catch (IOException e) {
			logger.severe("Error while saving properties file:");
			logger.severe(Throwables.getStackTraceAsString(e));
		}
	}

	static String getPropsString(Properties props, String id, String def) {
		logger.config("Procesing Configuration: " + id + " with value '" + props.getProperty(id) + "' with default: " + def); //NOSONAR
		return props.getProperty(id, def);
	}
	
	static int getPropsInt(Properties props, String id, int def) {
		try {
			logger.config("Procesing Configuration: " + id + " with value '" + props.getProperty(id) + "' with default: " + def);
			return Integer.parseInt(props.getProperty(id));
		} catch (NumberFormatException | NullPointerException e) { //NOSONAR
			logger.config("An acceptable Exception was caught while loading integer from config " + id + ":" + e.getMessage());
			return def;
		}
	}
	
	static Level getPropsLogLevel(Properties props, String id, Level def) {
		try {
			logger.config("Procesing Configuration: " + id + " with value '" + props.getProperty(id) + "' with default: " + def);
			return Level.parse(props.getProperty(id));
		} catch (IllegalArgumentException | NullPointerException e) { //NOSONAR
			logger.config("An acceptable Exception was caught while loading log level value from config " + id + ":" + e.getMessage());
			return def;
		}
	}
	
	static double getPropsDouble(Properties props, String id, double def) {
		try {
			logger.config("Procesing Configuration: " + id + " with value '" + props.getProperty(id) + "' with default: " + def);
			return Double.parseDouble(props.getProperty(id));
		} catch (NumberFormatException | NullPointerException e) { //NOSONAR
			logger.config("An acceptable Exception was caught while loading double value from config " + id + ":" + e.getMessage());
			return def;
		}
	}
}