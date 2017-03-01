package com.minepop.talkar.util.logging;

import java.io.PrintStream;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.StreamHandler;

/**
 * 
 * @author Talkarcabbage
 *
 */
public class SysoutHandler extends StreamHandler {

	Level minLevel;
	Level maxLevel;
	
	/**
	 * Creates a Console Handler with the specified minimum and maximum levels. The lowest normal level is finest, and the highest is severe. Alternatively use OFF and NONE
	 * @param stream
	 * @param minLevel The least severe Level
	 * @param maxLevel The most severe level.
	 */
	public SysoutHandler(PrintStream stream, Level minLevel, Level maxLevel) {
		super(stream, LogFormatter.getInstance());
		this.minLevel = minLevel;
		this.maxLevel = maxLevel;
		setLevel(Level.ALL);
		setFilter( logRecord -> {//NOSONAR
			return logRecord.getLevel().intValue() >= this.minLevel.intValue() && logRecord.getLevel().intValue() <= this.maxLevel.intValue();
		});
	}
	
	@Override
	public void publish(LogRecord record) {
		super.publish(record);
		flush();
	}
	
	@Override
	public void close() {
		flush();
	}
}