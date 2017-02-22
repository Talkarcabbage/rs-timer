package com.minepop.talkar.util.logging;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;

public class LogFormatter extends Formatter {

	protected final DateFormat format = new SimpleDateFormat("MMM dd yyyy HH:mm:ss");
	public static final LogFormatter instance;
	static {
		instance = new LogFormatter();
	}
	
	@Override
	public String format(LogRecord record) {
		StringBuilder sb = new StringBuilder();
		sb
			.append("[")
			.append(format.format(record.getMillis()))
			.append("] [")
			.append(record.getLevel())
			.append("] ")
			.append(record.getMessage() == null ? " " : record.getMessage())
			.append("\n");
		return sb.toString();
	}
	
	public static LogFormatter getInstance() {
		return instance;
	}

}
