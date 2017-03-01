package com.minepop.talkar.util;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class IOThreadManager {

	public static final IOThreadManager instance = new IOThreadManager();
	ExecutorService exc;
	
	IOThreadManager() {
		exc = Executors.newSingleThreadExecutor( r -> {
			Thread t = Executors.defaultThreadFactory().newThread(r);
			t.setDaemon(true);
			return t;
		});
	}
	
	/**
	 * Schedules the file i/o executor to save the specified contents to file
	 * @param fileName - The file to write to
	 * @param text - The text to write
	 * @param append - Whether or not to append the data to existing contents
	 */
	public void writeFile(String fileName, String text, boolean append) {
		exc.execute( () -> FileManager.writeFile(fileName, text, append));
	}
	
	public Future<IOThreadManager.ReaderCallable> readFile(String fileName) {
		return exc.submit( () -> new ReaderCallable(fileName));
	}
	
	class ReaderCallable implements Callable<String> {

		final String fileName;
		
		public ReaderCallable(String fileName) {
			this.fileName = fileName;
		}
		
		@Override
		public String call() throws Exception {
			return FileManager.readFile(fileName);
		}
		
	}
	
	/**
	 * Invoke a task to be run on the File I/O thread. This will 
	 * prevent other tasks, such as reading, from occurring concurrently.
	 * @param r
	 */
	public void invokeLater(Runnable r) {
		exc.execute(r);
		
	}
	
}

