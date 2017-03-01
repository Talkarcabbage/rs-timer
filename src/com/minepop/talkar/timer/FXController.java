package com.minepop.talkar.timer;

import java.awt.AWTException;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.ImageIcon;
import javax.swing.SwingUtilities;

import com.google.common.base.Throwables;
import com.google.common.collect.HashBiMap;
import com.minepop.talkar.timer.Timer.TimerType;
import com.minepop.talkar.timer.fxgui.MainWindow;
import com.minepop.talkar.timer.fxgui.ProgressPane;
import com.minepop.talkar.util.ConfigManager;
import com.minepop.talkar.util.FileManager;
import com.minepop.talkar.util.IOThreadManager;
import com.minepop.talkar.util.logging.LoggerConstructor;

import javafx.application.Platform;
import javafx.scene.layout.GridPane;

/**
 * The instance of this class controls the underlying logic of the program and 
 * interacts with the FX GUI and file system as necessary.
 * This class implements runnable, to create a separate thread to tick timers.
 * @author Talkarcabbage
 *
 */
public class FXController {

	static final Logger logger = LoggerConstructor.getLogger("FX Controller");

	public static TrayIcon trayIcon;
	
	public final HashBiMap<ProgressPane, Timer> timerMap = HashBiMap.create(50);
	
	public static final FXController instance = new FXController();

	private static final String TASKBARICONFILE = "taskBarIcon.png";

	public static final String TIMERFILE = "timers.cfg";
	static final String NUMBERFORMATCONSTANT = "numberformat=long";

	static final String FXBASEDCONSTANT = "gui,fx";
	
	FXController() {}

	public static void main(String[] args) {
		logger.info("Program starting...");
		logger.config("Loading configuration file");
		ConfigManager.load();
		logger.config("Updating config file");
		ConfigManager.save();
		logger.fine("Attempting tray initialization");
		instance.prepareSystemTray();
		
		MainWindow.launchWrap(args);
	}
	
	void prepareSystemTray() {
		File f = new File(TASKBARICONFILE);
		if (!f.exists()) {
			logger.info("Retrieving assets");
			try {
				FileManager.downloadFile(TASKBARICONFILE, "https://www.dropbox.com/s/th13a1fsf5kj4st/Cabbage.png?dl=1");
			} catch (MalformedURLException e) {
				logger.severe("URL of the icon was invalid");
				logger.severe(Throwables.getStackTraceAsString(e));
			}
		}
		ImageIcon taskBarIcon = new ImageIcon(TASKBARICONFILE);
		try {
			SwingUtilities.invokeAndWait( () -> applyWindowIcon(taskBarIcon));
		} catch (InvocationTargetException | InterruptedException e1) {
			logger.severe("A thread-related exception occured while setting up the tray");
			logger.severe(Throwables.getStackTraceAsString(e1));
		}
	}
	
	private static void applyWindowIcon(ImageIcon taskBarIcon) {
		trayIcon = new TrayIcon(taskBarIcon.getImage());
		try {
			
			SystemTray.getSystemTray().add(trayIcon);
			trayIcon.addActionListener( event -> MainWindow.instance.setVisible(!MainWindow.instance.isVisible()));	
			PopupMenu trayMenu = new PopupMenu("[Insert Creative Name Here]");

			MenuItem toggleVisibilityMenu = new MenuItem("Toggle Window");
			toggleVisibilityMenu.addActionListener( e -> {
				logger.fine("Tray icon toggle visibility event: " + MainWindow.instance.isVisible());
				Platform.runLater( () -> 
					MainWindow.instance.setVisible(!MainWindow.instance.isVisible())
				);
				
			});
			
			trayMenu.add(toggleVisibilityMenu);
			
			MenuItem exitMenu = new MenuItem("Exit");
			exitMenu.addActionListener( e -> System.exit(0));
			trayMenu.add(exitMenu);
			
			trayIcon.setPopupMenu(trayMenu);

		} catch (AWTException e) {
			logger.severe("Error creating tray icon");
			logger.severe(Throwables.getStackTraceAsString(e));
		}
	}

	/**
	 * Adds a timer to the GUI and adds it to the map. Returns the timer for convenience
	 * @param startingTime
	 * @param duration
	 * @param tab
	 * @param timerType
	 * @param name
	 * @return
	 */
	public Timer addTimer(long startingTime, long duration, int tab, TimerType timerType, String name) {
		Timer newTimer;
		switch (timerType) {
		case STANDARD:
			logger.fine("Added a standard timer");
			newTimer = new Timer(startingTime, duration, name, tab);
			break;
		case PERIODIC:
			logger.fine("Added a periodic timer");
			newTimer = new PeriodicTimer(startingTime, duration, name, tab);
			break;
		case MONTHLY:
			logger.fine("Added a monthly timer");
			newTimer = new MonthlyTimer(startingTime, duration, name, tab);
			break;
		default:
			logger.severe("Severe error occured trying to add a timer: the specified timer type was invalid: " + timerType);
			newTimer = new Timer(startingTime, duration, name == null ? "ERROR" : name, (tab >= 0 && tab < MainWindow.instance.getTabList().size()) ? tab : 0);
			break;
		}
		ProgressPane progPane = new ProgressPane();
		progPane.setLabelText(newTimer.getName());
		this.timerMap.put(progPane, newTimer);
		progPane.setOnMouseClicked( event -> {
			MainWindow.instance.onClickTimerBar(progPane, event);
		});
		MainWindow.instance.addTimerBar(progPane, tab);	
		return newTimer;
	}
	
	public void loadTimers() {
		boolean importResave = false;
		boolean importResaveConfig = false;
		boolean processDoubles = true;
		boolean isFXBased = false; //If true, do not make a new first tab. Set this to true if a tab is made or if the value is found
		FileManager.ensureExists(TIMERFILE);
		String[] timersStringArray = FileManager.readFileSplit(TIMERFILE);
		
		for (String s : timersStringArray) { //NOSONAR
			if (s.isEmpty() || s.startsWith("/")) {
				continue;
			}
			if (NUMBERFORMATCONSTANT.equalsIgnoreCase(s)) {
				processDoubles = false; //Verify that our format of number storage is long. 
				continue;
			}
			if (FXBASEDCONSTANT.equalsIgnoreCase(s)) { //We don't need to make a new first-tab
				isFXBased = true;
				continue;
			}
			String[] timerInfo = s.split(",");
			
			if ("cfg".equals(timerInfo[0])) {
				importResaveConfig = true;
				importResave = true; //To make sure our timer file doesn't overwrite our configuration repeatedly
				logger.config("Importing configuration from timers file: " + timerInfo[1]);
				if ("mainTabName".equals(timerInfo[1])) {
					ConfigManager.defaultTabName = timerInfo[2]; //Deprecated
				} else if ("gridColumns".equals(timerInfo[1])) { //Deprecated
					ConfigManager.defaultTabColumns = Integer.parseInt(timerInfo[2]);
				} else if("gridRows".equals(timerInfo[1])) { //Deprecated
					ConfigManager.defaultTabRows = Integer.parseInt(timerInfo[2]);
				} else if ("winSize".equals(timerInfo[1])) { //Deprecated
					ConfigManager.winWidth = Integer.parseInt(timerInfo[2]);
					ConfigManager.winHeight = Integer.parseInt(timerInfo[3]);
				} else if ("logLevel".equals(timerInfo[1])) {
					ConfigManager.logLevel = Level.parse(timerInfo[2]);
					LoggerConstructor.setGlobalLoggingLevel(ConfigManager.logLevel);
				}
			} 
			if (!isFXBased) { //Add a new tab and set isFXBased and importResave to true
				logger.info("Creating a new first-tab based on imported swing data");
				MainWindow.instance.addTab( ConfigManager.defaultTabRows, ConfigManager.defaultTabColumns, ConfigManager.defaultTabName);
				isFXBased = true;
				importResave = true;
			} 
			if ("tab".equals(timerInfo[0])) {
				if (timerInfo.length < 4) {
					logger.info("Imported old tab data!");
					MainWindow.instance.addTab( 0, 3, timerInfo[1]);
					importResave = true;
				} else {
					MainWindow.instance.addTab(Integer.parseInt(timerInfo[1]), Integer.parseInt(timerInfo[2]), timerInfo[3]);
				}
			} else if ("timer".equals(timerInfo[0])) {
				logger.finer("Found a timer entry with size: " + timerInfo.length);
				loadTimerFromArray(timerInfo, processDoubles); //Actually load the timer.
				
			} else if (timerInfo[0].startsWith("1")){
				logger.info("Imported really old timer data!");
				importResave=true;
				addTimer((long)Double.parseDouble(timerInfo[0]), (long)Double.parseDouble(timerInfo[1]), 0, TimerType.STANDARD, timerInfo[2]); //If it's super old data
			}
		}
		if (importResave || processDoubles) {
			this.saveTimers();
		}
		if (importResaveConfig) {
			ConfigManager.save();
		}
		if (MainWindow.instance.getTabList().isEmpty()) {
			logger.info("Loaded no tabs! Adding a default first tab");
			MainWindow.instance.addTab(ConfigManager.defaultTabRows, ConfigManager.defaultTabColumns, ConfigManager.defaultTabName);
		}
		if (timerMap.isEmpty()) {
			addTimer(System.currentTimeMillis(),120000, 0, TimerType.STANDARD, "Sample: Two Minutes");
			addTimer(System.currentTimeMillis(),3600000, 0, TimerType.STANDARD,  "Sample: Sixty Minutes");
			saveTimers();
		}
	}
	
	public void updateProgressPaneTitle(Timer timer) {
		if (timerMap.inverse().get(timer) == null) {
			logger.warning("Tried to update a timer that has already been deleted or did not exist!");
			return;
		}
		timerMap.inverse().get(timer).setLabelText(timer.getName());
	}

	private void loadTimerFromArray(String[] timerInfo, boolean processAsDouble) {
		if (!processAsDouble) { //Importing up to date data type (long)
			logger.finer("Up to date timer loaded: " + timerInfo[1] + " | " + timerInfo[2] + " | " + timerInfo[3] + " | " + timerInfo[4] + " | "+ timerInfo[5] + " | ");
			addTimer(Long.parseLong(timerInfo[1]), Long.parseLong(timerInfo[2]), Integer.parseInt(timerInfo[3]), TimerType.valueOf(timerInfo[4].toUpperCase()), timerInfo[5]);
		} else { //Importing old (double) data type
			logger.info("Importing old timer data to long format: " + timerInfo[5]);
			if ("true".equalsIgnoreCase(timerInfo[4])) {
				logger.info("Imported old timer data for standard timer with double type.");
				addTimer((long)Double.parseDouble(timerInfo[1]), (long)Double.parseDouble(timerInfo[2]), Integer.parseInt(timerInfo[3]), TimerType.STANDARD, timerInfo[5]);
			} else if ("false".equalsIgnoreCase(timerInfo[4])) {
				logger.info("Imported old timer data for periodic timer.");
				addTimer((long)Double.parseDouble(timerInfo[1]), (long)Double.parseDouble(timerInfo[2]), Integer.parseInt(timerInfo[3]), TimerType.PERIODIC, timerInfo[5]);
			} else {
				logger.info("Converting double timer: " + timerInfo[1] + " | " + timerInfo[2] + " | " + timerInfo[3] + " | " + timerInfo[4] + " | "+ timerInfo[5] + " | ");
				addTimer((long)Double.parseDouble(timerInfo[1]), (long)Double.parseDouble(timerInfo[2]), Integer.parseInt(timerInfo[3]), TimerType.valueOf(timerInfo[4].toUpperCase()), timerInfo[5]);
			}
		}
	}

	public void saveTimers() {
		logger.fine("Queueing timers to be saved");

		IOThreadManager.instance.invokeLater( () -> { //NOSONAR
			StringBuilder toSave = new StringBuilder();
			
			toSave.append(NUMBERFORMATCONSTANT + "\n");
			toSave.append(FXBASEDCONSTANT + "\n");
			
			if (!MainWindow.instance.getTabList().isEmpty()) {
				for (int i = 0; i < MainWindow.instance.getTabList().size(); i++) {
					GridPane gp = (GridPane)(MainWindow.instance.getTabList().get(i).getContent());
					int rows = gp.getRowConstraints().size();
					int columns = gp.getColumnConstraints().size();
					toSave.append("tab," + rows + "," + columns + "," + MainWindow.instance.getTabList().get(i).getText() + "\n");
				}
			}
			
			Iterator<Timer> it = timerMap.values().iterator();
			Timer t;
			while (it.hasNext()) {
				t = it.next();
				toSave.append("timer," + t.startingTime + "," + t.duration + "," + t.tab + "," + t.getTimerTypeString() + "," + t.name + "\n");
			}
			IOThreadManager.instance.writeFile(TIMERFILE, toSave.toString(), false);
		});
	}
		
	public void resetTimer(ProgressPane bar) {
		logger.info("Attempting timer reset for: " + bar.getLabelText());
		Timer t = this.timerMap.get(bar);
		
		if (t == null) {
			logger.severe("Found null when attempting to reset a timer!");
			return;
		}
		logger.fine("Found timer mapping for timer: " + t.getName());
		t.resetTimer();	
		saveTimers();
	}
	
	public void resetTimerComplete(ProgressPane bar) {
		logger.info("Attempting timer completion for: " + bar.getLabelText());
		Timer t = timerMap.get(bar);
		if (t == null) {
			logger.severe("Found null when attempting to reset-complete timer!");
			return;
		}
		logger.fine("Found timer mapping for timer: " + t.getName());
		t.resetTimerComplete();
		saveTimers();
	}
	
	public void removeTimer(ProgressPane pane) {
		logger.fine("Removing a timer with bar named " + pane.getLabelText());
		Timer timer = timerMap.get(pane);
		if (timer == null) {
			logger.severe("Found null when attempting to delete timer. This is a serious error.");
			return;
		}
		
		MainWindow.instance.removeTimerBar(pane, timerMap);

		timerMap.remove(pane);
		saveTimers();
		
	}
	
	/**
	 * Formats the time for display in a tooltip. Returns "Complete!" if the value is negative
	 * @param timeDuration
	 * @return
	 */
	public static String formatTime(long timeDuration) {
		if (timeDuration < 0) {
			return "Complete!";
		}
		double timeSeconds = timeDuration/1000D;
		return Math.round(Math.floor(timeSeconds/3600)) + ":" + Math.round((long)Math.floor((timeSeconds%3600)/60)) + ":" + Math.round(timeSeconds%60); //NOSONAR
	}
	
	public void removeTimerTab(int tabNum) {
		logger.fine("Attempting to remove a timer tab.");
		Iterator<Entry<ProgressPane, Timer>> it = timerMap.entrySet().iterator();
		while (it.hasNext()) {
			Timer timer = it.next().getValue();
			if (timer.getTab() == tabNum) {
				it.remove();
				continue;
			} else if (timer.getTab() > tabNum) {
				timer.setTab(timer.getTab()-1);
			}
		}
		MainWindow.instance.getTabList().remove(tabNum);
		saveTimers();
	}
	
}


