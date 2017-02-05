package com.minepop.talkar.timer;

public class Logger {

	public enum LEVEL {
		INFO, DEBUG, ERROR
	}
	
	public static void log(LEVEL type, String toLog) {
		
		switch (type) {
		case INFO:
			System.out.println("[INFO]\t" + toLog);
		break;
		case DEBUG:
			System.out.println("[DEBUG]\t" + toLog);
		break;
		case ERROR:
		System.err.println("[ERROR]\t" + toLog);
		}

	}
	
	public static void log(String toLog) {
		log(LEVEL.INFO, toLog);
	}
	
	public static void INFO(String toLog) {
		log(LEVEL.INFO, toLog);
	}
	
	public static void DEBUG(String toLog) {
		
	}
	
	public static void ERROR(String toLog) {
		
	}
	
}
