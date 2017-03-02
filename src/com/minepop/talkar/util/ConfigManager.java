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
	
	static final Logger logger = LoggerConstructor.getLogger("ConfigManager");
	
	static String configFileName = "rs-timer-config.cfg";
	
	public static String defaultTabName = "Main";
	public static int defaultTabColumns = 0;
	public static int defaultTabRows = 5;
	public static int winHeight = 250;
	public static int winWidth = 400;
	public static Level logLevel = Level.INFO;
	public static int framesPerUpdate = 15;
	public static double transparency = 1;

	ConfigManager() {}
	
	/**
	 * Loads the application's properties from the config file.
	 * This method also sets the logging level to the stored value.
	 */
	public static void load() {
		if (!(new File(configFileName).exists())) {
			save();
		}
		Properties prop = new Properties();
		try (FileInputStream fis = new FileInputStream(configFileName)) {
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
	
	public static void save() {
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
		
		try (FileOutputStream fos = new FileOutputStream(configFileName)) {
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
			logger.config("An acceptable Exception was caught while loading config " + id + ":" + e.getMessage());
			return def;
		}
	}
	static Level getPropsLogLevel(Properties props, String id, Level def) {
		try {
			logger.config("Procesing Configuration: " + id + " with value '" + props.getProperty(id) + "' with default: " + def);
			return Level.parse(props.getProperty(id));
		} catch (IllegalArgumentException | NullPointerException e) { //NOSONAR
			logger.config("An acceptable Exception was caught while loading config " + id + ":" + e.getMessage());
			return def;
		}
	}
	static double getPropsDouble(Properties props, String id, double def) {
		try {
			logger.config("Procesing Configuration: " + id + " with value '" + props.getProperty(id) + "' with default: " + def);
			return Double.parseDouble(props.getProperty(id));
		} catch (NumberFormatException | NullPointerException e) { //NOSONAR
			logger.config("An acceptable Exception was caught while loading config " + id + ":" + e.getMessage());
			return def;
		}
	}
}