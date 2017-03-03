package com.minepop.talkar.util.logging;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.logging.Formatter;
import java.util.logging.Level;
import java.util.logging.LogRecord;

import com.google.common.base.Throwables;
import com.minepop.talkar.util.ConfigManager;

/**
 * 
 * @author Talkarcabbage
 *
 */
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
			.append("] ");
		if (ConfigManager.getInstance().getLogLevel().intValue() < Level.INFO.intValue()) {
			sb.append("[")
			.append(record.getLoggerName())
			.append("] ");
		}
		if (ConfigManager.getInstance().getLogLevel().intValue() == Level.ALL.intValue()) {
			sb.append("[");
			try {
				sb.append(Class.forName(record.getSourceClassName()).getSimpleName());
			} 
			catch (ClassNotFoundException e) {} //NOSONAR
			sb.append(".")
			.append(record.getSourceMethodName())
			.append(" thread ")
			.append(record.getThreadID())
			.append("] ");
		}
		sb.append(record.getMessage() == null ? " " : record.getMessage());
		if (record.getThrown() != null) {
			sb.append(Throwables.getStackTraceAsString(record.getThrown()));
		}
			
			sb.append("\n");
		return sb.toString();
	}
	
	public static LogFormatter getInstance() {
		return instance;
	}
}