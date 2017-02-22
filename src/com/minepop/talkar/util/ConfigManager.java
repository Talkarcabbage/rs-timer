package com.minepop.talkar.util;

import java.awt.Toolkit;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.SwingUtilities;

import com.google.common.base.Throwables;
import com.minepop.talkar.timer.Main;
import com.minepop.talkar.util.logging.LoggerConstructor;

public class ConfigManager {
	
	static final Logger logger = LoggerConstructor.getLogger("ConfigManager");
	
	static String configFileName = "rs-timer-config.cfg";
	
	public static String mainTabName = "Main";
	public static int mainTabColumns = 0;
	public static int mainTabRows = 5;
	public static int winHeight = 205;
	public static int winWidth = 400;
	public static Level logLevel = Level.INFO;

	ConfigManager() {}
	

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
		mainTabName = getPropsString(prop, "mainTabName", mainTabName);
		mainTabColumns = getPropsInt(prop, "mainTabColumns", mainTabColumns);
		mainTabRows = getPropsInt(prop, "mainTabRows", mainTabRows);
		winHeight = getPropsInt(prop, "winHeight", winHeight);
		winWidth = getPropsInt(prop, "winWidth", winWidth);
		
		
	}
	
	public static void save() {
		Properties prop = new Properties();
		prop.setProperty("mainTabName", mainTabName);
		prop.setProperty("mainTabColumns", Integer.toString(mainTabColumns));
		prop.setProperty("mainTabRows", Integer.toString(mainTabRows));
		prop.setProperty("winHeight", Integer.toString(winHeight));
		prop.setProperty("winWidth", Integer.toString(winWidth));
		prop.setProperty("logLevel", logLevel.toString());
		
		try (FileOutputStream fos = new FileOutputStream(configFileName)) {
			prop.store(fos, "This file stores configuration options for the RS Timer");
		} catch (IOException e) {
			logger.severe("Error while saving properties file:");
			logger.severe(Throwables.getStackTraceAsString(e));
		}
	}
	
	/**
	 * Applies the configuration settings where they are relevant.
	 */
	public static void applyConfig() {
		LoggerConstructor.setGlobalLoggingLevel(logLevel);
		logger.fine("Applying configuration settings");
		if (Main.mainWin != null) {
			SwingUtilities.invokeLater( () -> {
				Main.mainWin.setBounds(Toolkit.getDefaultToolkit().getScreenSize().width-ConfigManager.winWidth, 
						Toolkit.getDefaultToolkit().getScreenSize().height-(ConfigManager.winHeight+40), ConfigManager.winWidth , ConfigManager.winHeight);
				Main.mainWin.setGridRows(ConfigManager.mainTabRows);
				Main.mainWin.setGridColumns(ConfigManager.mainTabColumns);
				Main.mainWin.getTabbedPane().setTitleAt(0, ConfigManager.mainTabName);
			});
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
			logger.config("An Exception was caught while loading " + id + ":" + e.getMessage());
			return def;
		}
	}
	static Level getPropsLogLevel(Properties props, String id, Level def) {
		try {
			logger.config("Procesing Configuration: " + id + " with value '" + props.getProperty(id) + "' with default: " + def);
			return Level.parse(props.getProperty(id));
		} catch (IllegalArgumentException | NullPointerException e) { //NOSONAR
			logger.config("An Exception was caught while loading " + id + ":" + e.getMessage());
			return def;
		}
		
	}
	
}
