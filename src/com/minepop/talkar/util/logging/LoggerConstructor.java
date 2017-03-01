package com.minepop.talkar.util.logging;

import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.minepop.talkar.util.ConfigManager;

public class LoggerConstructor {

	static final HashMap<String, Logger> hm = new HashMap<>();

	LoggerConstructor() {}
	
	public static Logger getLogger(String name) {
		if (hm.get(name) == null) {
			Logger logger = Logger.getLogger(name);
			logger.setUseParentHandlers(false);
			logger.setLevel(ConfigManager.logLevel);
			SysoutHandler errPrint = new SysoutHandler(System.err, Level.WARNING, Level.OFF);//NOSONAR
			SysoutHandler outPrint = new SysoutHandler(System.out, Level.ALL, Level.INFO);//NOSONAR
			logger.addHandler(errPrint);
			logger.addHandler(outPrint);
			hm.put(name, logger);
			return logger;
		} else {
			return hm.get(name);
		}

	}
	
	public static void setGlobalLoggingLevel(Level level) {
		for (Logger value : hm.values()) {
			value.setLevel(level);
		}
	}

}
