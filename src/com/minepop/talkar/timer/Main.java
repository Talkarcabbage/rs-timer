package com.minepop.talkar.timer;

import java.awt.AWTException;
import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.Toolkit;
import java.awt.TrayIcon;
import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Iterator;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.JProgressBar;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;

import com.google.common.base.Throwables;
import com.google.common.collect.HashBiMap;
import com.minepop.talkar.timer.gui.AddBarwin;
import com.minepop.talkar.timer.gui.MainWin;
import com.minepop.talkar.util.ConfigManager;
import com.minepop.talkar.util.FileManager;
import com.minepop.talkar.util.logging.LoggerConstructor;

public class Main {
	
	static final Logger logger = LoggerConstructor.getLogger("Main");
	
	public static final long DAY_LENGTH = 86400000;
	public static final long WEEK_LENGTH = 604800000;
	
	public static final String STANDARDTIMER = "standard";
	public static final String PERIODICTIMER = "periodic";
	public static final String MONTHLYTIMER = "monthly";
	
	public static final String TIMERFILE = "timers.cfg";
	public static final String TASKBARICONFILE = "taskBarIcon.png";

	static TrayIcon trayIcon;
	public static MainWin mainWin;
	public static AddBarwin addBarWin;
	
	static final String NUMBERFORMATCONSTANT = "numberformat=long";
		
	static HashBiMap<JProgressBar, Timer> timerMap = HashBiMap.create(50);
	
	protected static long currentTime;
	
	Main() {}
	
	public static void main(String[] args) throws InterruptedException, InvocationTargetException {
		logger.info("Loading configuration file");
		ConfigManager.load();
		LoggerConstructor.setGlobalLoggingLevel(ConfigManager.logLevel);
		logger.info("Initializing GUI and tray...");
		try {
			logger.fine("Setting look and feel");
		    for (LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
		        if ("Nimbus".equals(info.getName())) {
		            UIManager.setLookAndFeel(info.getClassName());
		            break;
		        }
		    	
		    }
		} catch (Exception e) {
			logger.warning("Failed to set the look and feel");
			logger.warning(Throwables.getStackTraceAsString(e));
		}
		
		//timerMap.
		logger.finer("Patching Nimbus Orange");
		UIManager.put("nimbusOrange", new Color(87, 168, 57));
		
		EventQueue.invokeAndWait( () -> {
				try {
					logger.fine("Creating create-timer GUI");
					addBarWin = new AddBarwin();
					logger.fine("Creating Main GUI");
					mainWin = new MainWin();
				} catch (Exception e) {
					logger.severe("Error creating the GUIs");
					logger.severe(Throwables.getStackTraceAsString(e));
				}
			}
		);
		
		logger.fine("Preparing system tray");
		prepareSystemTray();
		
		logger.finer("Showing the window");
		mainWin.setVisible(true);
		
		FileManager.ensureExists(TIMERFILE);
		
		ConfigManager.applyConfig();
		
		logger.info("Loading timers...");
		try {
			SwingUtilities.invokeAndWait(Main::loadTimers);
		} catch (Exception e) {
			JOptionPane.showMessageDialog(null, "An exception occured while loading timers: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
			logger.severe("An exception occured while loading timers");
			logger.severe(Throwables.getStackTraceAsString(e));
			System.exit(1);
		}
		logger.info("Timers loaded.");
		
		if (timerMap.isEmpty()) {
			addTimer(System.currentTimeMillis(),120000, 0, STANDARDTIMER, "Sample: Two Minutes");
			addTimer(System.currentTimeMillis(),3600000, 0, STANDARDTIMER,  "Sample: Sixty Minutes");
			saveTimers();
		}
		
		logger.info("Initializion complete.");
		while (true) { //NOSONAR
			SwingUtilities.invokeAndWait(Main::tickTimers);
			Thread.sleep(500);
		}
	}
	

	
	static void tickTimers() {
		
		Timer timer;
		Iterator<Timer> it = timerMap.values().iterator();
		while (it.hasNext()) {
			timer = it.next();
			timer.getProgressBar().setValue(timer.getPercentageComplete());
			if (timer.getPercentageComplete() < 100) {
				timer.getProgressBar().setForeground(Color.black);
				timer.getProgressBar().setToolTipText(formatTime(timer.getTimeRemaining()));
			} else {
				timer.getProgressBar().setForeground(new Color(20, 80, 20)); 
				timer.getProgressBar().setToolTipText("Timer Complete!");
			}
		}
		
		if (mainWin.isAlwaysOnTop() && !mainWin.isFocused()) {
			mainWin.configPanel.setVisible(false);
		} else {
			mainWin.configPanel.setVisible(true);
		}

	}
	
	private static void loadTimers() {
		boolean importResave = false;
		boolean importResaveConfig = false;
		boolean processDoubles = true; //Initialize to true for backwards compat 
		
		String[] timersStringArray = FileManager.readFileSplit(TIMERFILE);
		
		for (String s : timersStringArray) {
			if (s.isEmpty() || s.startsWith("/")) {
				continue;
			}
			if (NUMBERFORMATCONSTANT.equalsIgnoreCase(s)) {
				processDoubles = false; //Verify that our format of number storage is long. 
				continue;
			}
			
			String[] timerInfo = s.split(",");
			if ("cfg".equals(timerInfo[0])) {
				importResaveConfig = true;
				importResave = true; //To make sure our timer file doesn't overwrite our configuration repeatedly
				logger.config("Importing configuration from timers file: " + timerInfo[1]);
				if ("mainTabName".equals(timerInfo[1])) {
					ConfigManager.mainTabName = timerInfo[2];
					mainWin.getTabbedPane().setTitleAt(0, ConfigManager.mainTabName);
				} else if ("gridColumns".equals(timerInfo[1])) {
					ConfigManager.mainTabColumns = Integer.parseInt(timerInfo[2]);
					mainWin.setGridColumns(ConfigManager.mainTabColumns);
				} else if("gridRows".equals(timerInfo[1])) {
					ConfigManager.mainTabRows = Integer.parseInt(timerInfo[2]);
					mainWin.setGridRows(ConfigManager.mainTabRows);
				} else if ("winSize".equals(timerInfo[1])) {
					ConfigManager.winWidth = Integer.parseInt(timerInfo[2]);
					ConfigManager.winHeight = Integer.parseInt(timerInfo[3]);
					mainWin.setBounds(Toolkit.getDefaultToolkit().getScreenSize().width-ConfigManager.winWidth, Toolkit.getDefaultToolkit().getScreenSize().height-(ConfigManager.winHeight+40), ConfigManager.winWidth , ConfigManager.winHeight);
				} else if ("logLevel".equals(timerInfo[1])) {
					ConfigManager.logLevel = Level.parse(timerInfo[2]);
					LoggerConstructor.setGlobalLoggingLevel(ConfigManager.logLevel);
				}
				
			} else if ("tab".equals(timerInfo[0])) {
				if (timerInfo.length < 4) {
					logger.info("Imported old tab data!");
					mainWin.addNewTab(mainWin.getGridRows(), mainWin.getGridColumns(), timerInfo[1]);
					importResave = true;
				} else {
					mainWin.addNewTab(Integer.parseInt(timerInfo[1]), Integer.parseInt(timerInfo[2]), timerInfo[3]);
				}
				
			} else if ("timer".equals(timerInfo[0])) {
				logger.finer("Found a timer entry with size: " + timerInfo.length);
				loadTimerFromArray(timerInfo, processDoubles); //Actually load the timer.
				
			} else {
				logger.info("Imported really old timer data!");
				importResave=true;
				addTimer((long)Double.parseDouble(timerInfo[0]), (long)Double.parseDouble(timerInfo[1]), 0, "standard", timerInfo[2]); //If it's super old data
			}
		}
		if (importResave || processDoubles) {
			Main.saveTimers();
		}
		if (importResaveConfig) {
			ConfigManager.save();
			ConfigManager.applyConfig();
		}
		
	}
	
	/**
	 * Loads a timer via the addTimer method given an array with timer data. 
	 * @param timerInfo An array containing file-loaded timer data.
	 * @param processAsDouble Whether or not to process as a double. This is for backwards compatibility.
	 */
	static void loadTimerFromArray(String[] timerInfo, boolean processAsDouble) {
		
		if (!processAsDouble) { //Importing up to date data type (long)
			logger.finer("Up to date timer loaded: " + timerInfo[1] + " | " + timerInfo[2] + " | " + timerInfo[3] + " | " + timerInfo[4] + " | "+ timerInfo[5] + " | ");
			addTimer(Long.parseLong(timerInfo[1]), Long.parseLong(timerInfo[2]), Integer.parseInt(timerInfo[3]), timerInfo[4], timerInfo[5]);
			
		} else { //Importing old (double) data type
			logger.info("Importing old timer data to long format: " + timerInfo[5]);
			if ("true".equalsIgnoreCase(timerInfo[4])) {
				logger.info("Imported old timer data for standard timer with double type.");
				addTimer((long)Double.parseDouble(timerInfo[1]), (long)Double.parseDouble(timerInfo[2]), Integer.parseInt(timerInfo[3]), Main.STANDARDTIMER, timerInfo[5]);
			} else if ("false".equalsIgnoreCase(timerInfo[4])) {
				logger.info("Imported old timer data for periodic timer.");
				addTimer((long)Double.parseDouble(timerInfo[1]), (long)Double.parseDouble(timerInfo[2]), Integer.parseInt(timerInfo[3]), Main.PERIODICTIMER, timerInfo[5]);
			} else {
				logger.info("Converting double timer: " + timerInfo[1] + " | " + timerInfo[2] + " | " + timerInfo[3] + " | " + timerInfo[4] + " | "+ timerInfo[5] + " | ");
				addTimer((long)Double.parseDouble(timerInfo[1]), (long)Double.parseDouble(timerInfo[2]), Integer.parseInt(timerInfo[3]), timerInfo[4], timerInfo[5]);
			}
		}
	}
	
	
	public static Timer addTimer(long startingTime, long duration, int tab, String timerType, String name) {
		Timer newTimer;
		switch (timerType) {
		case STANDARDTIMER:
			newTimer = new Timer(startingTime, duration, name, tab, null);
			break;
		case PERIODICTIMER:
			newTimer = new PeriodicTimer(startingTime, duration, name, tab, null);
			break;
		case MONTHLYTIMER:
			logger.fine("Added a monthly timer");
			newTimer = new MonthlyTimer(startingTime, duration, name, tab, null);
			break;
		default:
			logger.severe("Severe error occured trying to add a timer: the specified timer type was invalid: " + timerType);
			newTimer = new Timer(startingTime, duration, name == null ? "ERROR" : name, (tab >= 0 && tab < mainWin.getTabList().size()) ? tab : 0, null);
			break;
		}
		JProgressBar newBar = new JProgressBar();
		newBar.setFont(new Font("SansSerif", Font.BOLD, 12));
		newBar.setStringPainted(true);
		newBar.setString(name);
		newBar.setName(name);
		newBar.setForeground(Color.blue);
		
		newBar.addMouseListener(mainWin);
		
		newTimer.setProgressBar(newBar);
		
		mainWin.addTimerBar(newBar, newTimer.getTab()); //TODO what is this todo for?
		mainWin.revalidate();
		mainWin.repaint();
		
		timerMap.put(newBar, newTimer);
		
		return newTimer;
		
	}
	
	public static void removeTimer(JProgressBar bar) {
		logger.fine("Removing a timer with bar named " + bar.getName());
		Timer timer = timerMap.get(bar);
		if (timer == null) {
			logger.severe("Found null when attempting to delete timer. This is a serious error.");
			return;
		}
		
		mainWin.removeTimerBar(bar);
		
		timerMap.remove(bar);
		
		mainWin.revalidate();
		mainWin.repaint();
		
	}
	
	public static void saveTimers() {
		
		StringBuilder toSave = new StringBuilder();
		
		logger.fine("Saving timers");
		
		toSave.append(NUMBERFORMATCONSTANT + "\n");
		
		if (mainWin.getTabList().size() > 1) {
			for (int i = 1; i < mainWin.getTabList().size(); i++) {
				toSave.append("tab," + ((GridLayout)(mainWin.getTabList().get(i).getLayout())).getRows() + "," + ((GridLayout)(mainWin.getTabList().get(i).getLayout())).getColumns() + "," + mainWin.getTabList().get(i).getName() + "\n");
			}
		}
		
		Iterator<Timer> it = timerMap.values().iterator();
		Timer t;
		while (it.hasNext()) {
			t = it.next();
			toSave.append("timer," + t.startingTime + "," + t.duration + "," + t.tab + "," + t.getTimerType() + "," + t.name + "\n");
		}
		
		FileManager.writeFile("timers.cfg", toSave.toString());

	}

	/**
	 * Resets a timer, setting it to incomplete. For normal timers, this means 0% the way to complete; for daily/weekly/monthly, the next daily/weekly/monthly reset is applied.
	 * @param timerName
	 * @param bar
	 */
	public static void resetTimer(String timerName, JProgressBar bar) {
		logger.info("Attempting timer reset for " + timerName);
		Timer t = timerMap.get(bar);
		
		if (t == null) {
			logger.severe("Found null when attempting to reset timer: " + timerName);
			return;
		}
		
		t.resetTimer();
		
		saveTimers();
	}
	
	/**
	 * Resets a timer and sets its time to 0, making it appear complete.
	 * @param timerName
	 * @param bar
	 */
	public static void resetTimerComplete(String timerName, JProgressBar bar) {
		logger.info("Attempting timer completion for " + timerName);
		Timer t = timerMap.get(bar);
		if (t == null) {
			logger.severe("Found null when attempting to reset-complete timer");
			return;
		}
		t.resetTimerComplete();
		saveTimers();
	}
		
	

	private static void prepareSystemTray() {
		File f = new File(TASKBARICONFILE);
		if (!f.exists()) {
			logger.info("Retrieving assets");
			try {
				FileManager.downloadFile(TASKBARICONFILE, "https://www.dropbox.com/s/th13a1fsf5kj4st/Cabbage.png?dl=1");
			} catch (MalformedURLException e) {
				logger.severe("URL of the icno was invalid");
				logger.severe(Throwables.getStackTraceAsString(e));
			}
		}
		ImageIcon taskBarIcon = new ImageIcon(TASKBARICONFILE);
		
		try {
			SwingUtilities.invokeAndWait( () -> applyWindowIcon(taskBarIcon));
		} catch (InvocationTargetException | InterruptedException e1) {
			logger.severe("A thread-related exception occured while setting the GUI");
			logger.severe(Throwables.getStackTraceAsString(e1));
		}
		

	}
	
	/**
	 * Should be used by prepareSystemTray() for its thread-sensitive operations. 
	 * Always call this only from the Swing Event Thread.
	 * @throws AWTException 
	 */
	private static void applyWindowIcon(ImageIcon taskBarIcon) {
		mainWin.setIconImage(taskBarIcon.getImage());
		trayIcon = new TrayIcon(taskBarIcon.getImage());
		try {
			SystemTray.getSystemTray().add(trayIcon);
			trayIcon.addActionListener( event -> mainWin.setVisible(!mainWin.isVisible()));	
			PopupMenu trayMenu = new PopupMenu("[Insert Creative Name Here]");

			MenuItem toggleVisibilityMenu = new MenuItem("Toggle Window");
			toggleVisibilityMenu.addActionListener( e -> mainWin.setVisible(!mainWin.isVisible()));
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
	
	static DateFormat df = new SimpleDateFormat("");
	public static String formatTime(double timeDuration) {
		
		double timeSeconds = Math.ceil(timeDuration/1000);
		return Math.round(Math.floor(timeSeconds/3600)) + ":" + Math.round((long)Math.floor((timeSeconds%3600)/60)) + ":" + Math.round(timeSeconds%60); //NOSONAR
	}
	
	public static Set<Timer> getTimerList() {
		return timerMap.values();
	}
	
}
