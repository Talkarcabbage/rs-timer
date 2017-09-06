package io.github.talkarcabbage.rstimer.persistence;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import io.github.talkarcabbage.logger.LoggerManager;
import io.github.talkarcabbage.rstimer.fxgui.MainWindow;
import io.github.talkarcabbage.rstimer.newtimers.Daily;
import io.github.talkarcabbage.rstimer.newtimers.Monthly;
import io.github.talkarcabbage.rstimer.newtimers.NewTimer;
import io.github.talkarcabbage.rstimer.newtimers.Standard;
import io.github.talkarcabbage.rstimer.newtimers.Weekly;
import javafx.scene.control.Tab;

public class LoadManager {
	
	private static final Logger logger = LoggerManager.getInstance().getLogger("Timer");
	
	/**
	 * Parses the tabs from the given fileInput, a list containing the contents of a save file, and returns the tabs contained in it
	 * @param fileInput the file contents
	 * @return the Tabs in the file
	 */
	public static List<Tab> parseTabsFromFileData(List<String> fileInput) { //NOSONAR Splitting it up would only complicate it at this point.
		
		int index = 0;
		ArrayList<Tab> returnTabList = new ArrayList<>(8);

		while (index < fileInput.size() && !fileInput.get(index).trim().equals("tabs {")) { //Fast-forward to the line with the tabs declaration
			index++;
		}
		index++; //Proceed one line past it to the first tab declaration
		
		if (index >= fileInput.size()) { //If we're at the end of the file somehow already
			logger.warning("No tab data in file!");
			return new ArrayList<>();
		}

		while (index < fileInput.size()) { //Do this until we run out of tabs or out of file
			String newTabName = "MISSING";
			int newTabRows = 0;
			int newTabColumns = 2;
			if (fileInput.get(index).trim().equals("tab {")) {
				while (index < fileInput.size() && !fileInput.get(index).trim().equals("}")) {
					if (fileInput.get(index).trim().indexOf(':')<2) { 
						index++;
						continue;
						//Skip if there's no colon
					}
					
					switch (fileInput.get(index).trim().substring(0, fileInput.get(index).indexOf(':')-2)) {
					case "title":
						newTabName = fileInput.get(index).trim().substring(fileInput.get(index).trim().indexOf(':')+1);
						break;
					case "rows":
						newTabRows = Integer.parseInt(fileInput.get(index).trim().substring(fileInput.get(index).trim().indexOf(':')+1));
						break;
					case "columns":
						newTabColumns = Integer.parseInt(fileInput.get(index).trim().substring(fileInput.get(index).trim().indexOf(':')+1));
						break;
					default:
						logger.warning("Unrecognized tab data: " + fileInput.get(index));
						break;
					}
					
					index++;
				}
				
				returnTabList.add(MainWindow.createTab(newTabRows, newTabColumns, newTabName));
				index++;
			} else if (fileInput.get(index).trim().equals("};")) {
				break;
			}
		}
		
		return returnTabList;
	}
	
	/**
	 * This method takes the base array of lines of the file and returns a list of Timers from that file for addition to the GUI
	 * @param fileInput A list containing the contents of a timer save file separated by line
	 */
	public static List<NewTimer> loadTimersFromFileData(List<String> fileInput) {
		int index = 0;

		while (index < fileInput.size() && !fileInput.get(index).trim().equals("timers {")) { //Fast-forward to the line with the timers declaration
			index++;
		}
		index++; //Proceed one line past it to the first timer(type) declaration
		
		if (index >= fileInput.size()) { //If we're at the end of the file somehow already
			logger.warning("No timer data in file!");
			return new ArrayList<>();
		}
		
		ArrayList<String> dataList = new ArrayList<>(250); //Create a list to store the List of data entries (lines) between "timers {" and "};"

		while (index < fileInput.size()) {
			if (fileInput.get(index).trim().equals("};")) { //End the loop if we reach the marker for the end of the Timers section of the file
				break;
			}
			dataList.add(fileInput.get(index)); //Add the line to the list
			index++;
		}
		return loadTimersFromList(dataList); //Pass the dataList, which excludes lines other than between "timers {" and "};", to the betterLoadNewTimers method
		
	}
	
	/**
	 * This method takes in a List filled with sets of Timer data and returns a list of Timers created from that data.
	 * Each section should be an individual set of lines that would be the results of calling getTimerSaveString on the Timer
	 * Note that the list should not include the final "};"
	 * @param timerSectionList
	 * @return A list of timers created from the timer dataset
	 */
	private static List<NewTimer> loadTimersFromList(List<String> timerSectionList) {
		
		ArrayList<NewTimer> returnList = new ArrayList<>();
		
		for (List<String> list : getSeparateDataStringLists(timerSectionList)) {
			
			NewTimer newTimer = null; //Avoid duplicate variable names
			String timerType = getTimerTypeFromDataString(list.get(0));
			Map<String, String> dataMap = getTimerDataMapFromList(list);
			
			switch (timerType) { //Create a new timer based on the type of timer
			case "Standard":
				newTimer = new Standard(dataMap);
				break;
			case "Daily":
				newTimer = new Daily(dataMap);
				break;
			case "Weekly":
				newTimer = new Weekly(dataMap);				
				break;
			case "Monthly":
				newTimer = new Monthly(dataMap);			
				break;
			default: 
				logger.warning("Unexpected non-timer entry: " + list.get(0));
				break;
			}
			
			if (newTimer != null) {
				returnList.add(newTimer); //Add the fresh oven baked timer to our list of new Timers
			}
			
		}
		
		return returnList;
	}
	
	/**
	 * Takes in a List containing sets of Timer data and returns each (list) entry separated into a list of lists.
	 * @return A list of Timer data lists
	 */
	private static List<List<String>> getSeparateDataStringLists(List<String> originalList) {
		
		ArrayList<List<String>> bigList = new ArrayList<>();
		ArrayList<String> subList; //For efficiency purposes, don't allocate new variables
		
		int index = 0;
		
		while (index < originalList.size()) { //Continue until out of elements in the input
			subList = new ArrayList<>(); //Create a new sublist to store the individual timer dataset into
			while (!originalList.get(index).trim().equals("}")) { //Stop on the lines that mark the end of timers
				subList.add(originalList.get(index)); //Add the line to the sublist
				index++; //Proceed to the next line
			}
			bigList.add(subList);
			index++; //Next "Timer" entry line
		}
		
		return bigList;
	}
	
	
	private static String getTimerTypeFromDataString(String line) {
		return line.trim().substring(0, line.trim().length()-2);
	}
	
	public static Map<String, String> getTimerDataMapFromList(List<String> list) {
		HashMap<String, String> map = new HashMap<>(8);
		String listEntry = "";
		for (int i = 1; i < list.size(); i++) { //NOSONAR Both break and continue are important for flow here. Thanks anyway sonar.
			listEntry = list.get(i);
			if (listEntry.trim().equals("}")) {
				break;
			}
			if (listEntry.indexOf(':') < 0) { //Covers blank lines as well as the first line if it is included.
				continue;
			}
			map.put(listEntry.trim().substring(0, listEntry.indexOf(':')-2), listEntry.trim().substring(listEntry.trim().indexOf(':')+1));
		}
		return map;
	}

	//Because static class
	private LoadManager() {}
	
}
