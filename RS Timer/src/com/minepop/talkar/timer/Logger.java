package com.minepop.talkar.timer;

public class Logger {

	public enum LEVEL {
		INFO, DEBUG, ERROR
	}
	
	public static boolean log(LEVEL type, String toLog) {
		
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
		
		
		return false;
	}
	
	public static boolean log(String toLog) {
		return log(LEVEL.INFO, toLog);
	}
	
	
}
