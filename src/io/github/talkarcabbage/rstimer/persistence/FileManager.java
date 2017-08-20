package io.github.talkarcabbage.rstimer.persistence;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.Scanner;
import java.util.logging.Logger;

import com.google.common.base.Throwables;

import io.github.talkarcabbage.logger.LoggerManager;

/**
 * 
 * @author Talkarcabbage <br>
 * I wrote those in case I ended up with a use for simplified file reading and writing.
 * The class handles FileNotFoundExceptions itself.
 */
public class FileManager {
	static final Logger logger = LoggerManager.getInstance().getLogger("FileManager");

	FileManager() {}
	
	/**
	 * 
	 * @param fileName - The file to attempt to read from.
	 * @return - A string containing the contents of the file.
	 */
	public static String readFile(String fileName) {
		
		File toRead = new File(fileName);
		if (!toRead.exists()) {
			logger.info("FileManager: Error reading file " + fileName + " -- The file does not exist or is inaccessible.");
			return null;
		}
		
		StringBuilder stringB = new StringBuilder();
		
		try (Scanner scanIn = new Scanner(toRead)) {
			
			while (scanIn.hasNext()) {
				stringB.append(scanIn.nextLine() + "\n");
			}
			
			scanIn.close();
			
			return stringB.toString();
			
		} catch (FileNotFoundException e) {
			logger.severe("Somehow we made it past the fileExists check!");
			logger.severe(Throwables.getStackTraceAsString(e));
		}
		
		return null;
	}
	
	/**
	 * This method is a convenience way of reading lines from a text file into an array.
	 * @param fileName - Name of the file to attempt to read from.
	 * 
	 * @return An array of each 'line' of the file separated by \n
	 */
	public static String[] readFileSplit(String fileName) {
		String toSplit = readFile(fileName);

		return toSplit == null ? null : toSplit.split("\n");
	}
	
	public static boolean writeFile(String fileName, String text, boolean append) {
		
		File toWrite = new File(fileName);
				
		try (PrintWriter pw = new PrintWriter(toWrite)) {
			if (append) {
				pw.append(text);
			} else {
				pw.write(text);
			}
			pw.close();
			return true;
			
		} catch (FileNotFoundException e) {
			logger.severe("Could not write file: ");
			logger.severe(Throwables.getStackTraceAsString(e));
		} 
		
		return false;
	}
	
	public static boolean writeFile(String fileName, String text) {
		return writeFile(fileName, text, false);
	}
	
	/**
	 * Downloads a file and saves it. Note: Referenced several sources from google on image downloading.
	 * @param url
	 * @param fileName
	 * @return
	 * @throws MalformedURLException If the url is invalid
	 */
	public static boolean downloadFile(String fileName, String url) throws MalformedURLException {
		URL website;
		website = new URL(url);
		try (ReadableByteChannel rbc = Channels.newChannel(website.openStream()); FileOutputStream fos = new FileOutputStream(fileName)) {
			fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
		} catch (IOException e) {
			logger.severe("Error downloading assets");
			logger.severe(Throwables.getStackTraceAsString(e));
		} 
		
		return false;
	}

	/**
	 * Essentially a shortcut to the File.createNewFile(), which creates a blank file if it does not yet exist.
	 * @param string - A file name to create. This can be a local or full path.
	 */
	public static void ensureExists(String string) {
		File f = new File(string);
		
		try {
			f.createNewFile(); //NOSONAR
		} catch (IOException e) {
			logger.severe("Error creating file: " + string);
			logger.severe(Throwables.getStackTraceAsString(e));
		}
	}
}