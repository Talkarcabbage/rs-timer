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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;

import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.JProgressBar;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;

import com.google.common.collect.HashBiMap;
import com.minepop.talkar.timer.gui.AddBarwin;
import com.minepop.talkar.timer.gui.MainWin;
import com.minepop.talkar.util.FileManager;

public class Main {
	
	public static final long DAY_LENGTH = 86400000;
	public static final long WEEK_LENGTH = 604800000;
	
	public static final String STANDARDTIMER = "standard";
	public static final String PERIODICTIMER = "periodic";
	public static final String MONTHLYTIMER = "monthly";


	
	static TrayIcon trayIcon;
	public static MainWin mainWin;
	public static AddBarwin addBarWin;
	//static ArrayList<Timer> timerList = new ArrayList<Timer>();
	//static ArrayList<JProgressBar> progressBarList = new ArrayList<JProgressBar>();
	//static HashMap<JProgressBar, Timer> barToTimerMap = new HashMap<JProgressBar, Timer>();
	
	static ArrayList<String> cfgList = new ArrayList<String>();
	//static ArrayList<String> commentList = new ArrayList<String>();
	
	static HashBiMap<JProgressBar, Timer> timerMap = HashBiMap.create(50);
	
	static protected long currentTime;
	
	public static void main(String[] args) throws InterruptedException, InvocationTargetException { //TODO
		Logger.INFO("Initializing GUI and tray...");
		try {
		    for (LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
		        if ("Nimbus".equals(info.getName())) {
		            UIManager.setLookAndFeel(info.getClassName());
		            break;
		        }
		    	
		    }
		} catch (Exception e) {}
		
		//timerMap.
		
		UIManager.put("nimbusOrange", new Color(87, 168, 57));
		
		EventQueue.invokeAndWait(new Runnable() {
			public void run() {
				try {
					addBarWin = new AddBarwin();
					mainWin = new MainWin();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		
		prepareSystemTray();
		mainWin.setVisible(true);
		FileManager.ensureExists("timers.cfg");
		
		Logger.INFO("Loading timers...");
		try {
		loadTimers();
		} catch (Exception e) {
			JOptionPane.showMessageDialog(null, "An exception occured while loading timer information. Run the jar using the console for more information.", "Error", JOptionPane.ERROR_MESSAGE);
			System.exit(1);
		}
		Logger.INFO("Timers loaded.");
		
		if (timerMap.isEmpty()) {
			addTimer(System.currentTimeMillis(),120000, 0, STANDARDTIMER, "Sample: Two Minutes");
			addTimer(System.currentTimeMillis(),3600000, 0, STANDARDTIMER,  "Sample: Sixty Minutes");
			
		}
		
		
		//TODO why?
		//saveTimers();
		Logger.INFO("Initializion complete.");
		while (true) {
			tickTimers();
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
				timer.getProgressBar().setForeground(new Color(20, 80, 20)); //TODO color
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
		String[] timersStringArray = FileManager.readFileSplit("timers.cfg");
		
		for (String s : timersStringArray) {
			if (s.isEmpty() || s.startsWith("/")) {
				
				continue;
			}
			String[] timerInfo = s.split(",");
			if (timerInfo[0].equals("cfg")) {
				cfgList.add(s);
				Logger.INFO("cfg: " + timerInfo[1]);
				if (timerInfo[1].equals("mainTabName")) {
					mainWin.getTabbedPane().setTitleAt(0, timerInfo[2]);
				} else if (timerInfo[1].equals("gridColumns")) {
					mainWin.setGridColumns(Integer.parseInt(timerInfo[2]));
				} else if( timerInfo[1].equals("gridRows")) {
					mainWin.setGridRows(Integer.parseInt(timerInfo[2]));
				}
				else if (timerInfo[1].equals("winSize")) {
					mainWin.setBounds(Toolkit.getDefaultToolkit().getScreenSize().width-Integer.parseInt(timerInfo[2]), Toolkit.getDefaultToolkit().getScreenSize().height-(Integer.parseInt(timerInfo[3])+40), Integer.parseInt(timerInfo[2]) , Integer.parseInt(timerInfo[3]));
				}
				
			} else if (timerInfo[0].equals("tab")) {
				if (timerInfo.length < 4) {
					Logger.INFO("Imported old tab data!");
					mainWin.addNewTab(mainWin.getGridRows(), mainWin.getGridColumns(), timerInfo[1]);
					importResave = true;
				} else {
					mainWin.addNewTab(Integer.parseInt(timerInfo[1]), Integer.parseInt(timerInfo[2]), timerInfo[3]);
				}
				
			} else if (timerInfo[0].equals("timer")) {
				Logger.DEBUG("Found a timer");
				if (timerInfo[4].toLowerCase().equals("true")) {
					Logger.INFO("Imported old timer data for standard timer.");
					addTimer(Double.parseDouble(timerInfo[1]), Double.parseDouble(timerInfo[2]), Integer.parseInt(timerInfo[3]), Main.STANDARDTIMER, timerInfo[5]);
				} else if (timerInfo[4].toLowerCase().equals("false")) {
					Logger.INFO("Imported old timer data for periodic timer.");
					addTimer(Double.parseDouble(timerInfo[1]), Double.parseDouble(timerInfo[2]), Integer.parseInt(timerInfo[3]), Main.PERIODICTIMER, timerInfo[5]);
				} else {
					Logger.DEBUG("Up to date timer loaded.");
					addTimer(Double.parseDouble(timerInfo[1]), Double.parseDouble(timerInfo[2]), Integer.parseInt(timerInfo[3]), (timerInfo[4]), timerInfo[5]);
				}
				
			} else {
				Logger.INFO("Imported old timer data!");
				importResave=true;
				addTimer(Double.parseDouble(timerInfo[0]), Double.parseDouble(timerInfo[1]), 0, "standard", timerInfo[2]);
			}
			if (importResave) {
				Main.saveTimers();
			}
		}
	}
	
	
	public static Timer addTimer(double startingTime, double duration, int tab, String timerType, String name) {
		Timer newTimer = null;
		switch (timerType) {
		case STANDARDTIMER:
			newTimer = new Timer(startingTime, duration, name, tab, null);
			break;
		case PERIODICTIMER:
			newTimer = new PeriodicTimer(startingTime, duration, name, tab, null);
			break;
		case MONTHLYTIMER:
			Logger.DEBUG("Added a monthly timer");
			newTimer = new MonthlyTimer(startingTime, duration, name, tab, null);
			break;
		default:
			Logger.ERROR("Severe error occured trying to add a timer: the specified timer type was invalid: " + timerType);
			newTimer = new Timer(startingTime, duration, ((name == null ? "ERROR" : name)), ((tab >= 0 && tab < mainWin.getTabList().size()) ? tab : 0), null);
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
		Logger.DEBUG("Removing a timer with bar named " + bar.getName());
		Timer timer = timerMap.get(bar);
		if (timer == null) {
			Logger.ERROR("Found null when attempting to delete timer. This is a serious error.");
			return;
		}
		
		mainWin.removeTimerBar(bar);
		
		timerMap.remove(bar);
		
		mainWin.revalidate();
		mainWin.repaint();
		
	}
	
	public static void saveTimers() {
		
		String toSave = "";
		
		Logger.DEBUG("Saving timers");
		
		for (String s : cfgList){
			toSave = toSave + s + "\n";
		}
		
		if (mainWin.getTabList().size() > 1) {
			for (int i = 1; i < mainWin.getTabList().size(); i++) {
				toSave += "tab," + ((GridLayout)(mainWin.getTabList().get(i).getLayout())).getRows() + "," + ((GridLayout)(mainWin.getTabList().get(i).getLayout())).getColumns() + "," + mainWin.getTabList().get(i).getName() + "\n";
			}
		}
		
		Iterator<Timer> it = timerMap.values().iterator();
		Timer t;
		while (it.hasNext()) {
			t = it.next();
			toSave = toSave + "timer," + t.startingTime + "," + t.duration + "," + t.tab + "," + t.getTimerType() + "," + t.name + "\n";
		}
		
		FileManager.writeFile("timers.cfg", toSave);

	}

	/**
	 * Resets a timer, setting it to incomplete. For normal timers, this means 0% the way to complete; for daily/weekly/monthly, the next daily/weekly/monthly reset is applied.
	 * @param timerName
	 * @param bar
	 */
	public static void resetTimer(String timerName, JProgressBar bar) {
		Logger.INFO("Attempting timer reset for " + timerName);
		Timer t = timerMap.get(bar);
		
		if (t == null) {
			Logger.ERROR("Found null when attempting to reset timer: " + timerName);
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
		Logger.INFO("Attempting timer completion for " + timerName);
		Timer t = timerMap.get(bar);
		if (t == null) {
			Logger.ERROR("Found null when attempting to reset-complete timer");
			return;
		}
		t.resetTimerComplete();
		saveTimers();
	}
		
	

	static boolean isMainWinVisible = true;
	private static void prepareSystemTray() {
		File f = new File("taskBarIcon.png");
		if (!f.exists()) {
			Logger.INFO("Retrieving assets");
			FileManager.downloadFile("taskBarIcon.png", "https://www.dropbox.com/s/th13a1fsf5kj4st/Cabbage.png?dl=1");
		}
		ImageIcon taskBarIcon = new ImageIcon("taskBarIcon.png");
		mainWin.setIconImage(taskBarIcon.getImage());
		trayIcon = new TrayIcon(taskBarIcon.getImage());
		try {
			SystemTray.getSystemTray().add(trayIcon);
		} catch (AWTException e) {
			Logger.ERROR("Error creating system tray icon:");
			e.printStackTrace();
		}
		
		trayIcon.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				isMainWinVisible = !isMainWinVisible;
				mainWin.setVisible(isMainWinVisible);
			}
		});		
		
		PopupMenu trayMenu = new PopupMenu("[Insert Creative Name Here]");

		MenuItem toggleVisibilityMenu = new MenuItem("Toggle Window");
		toggleVisibilityMenu.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				isMainWinVisible = !isMainWinVisible;
				mainWin.setVisible(isMainWinVisible);
			}
		});
		trayMenu.add(toggleVisibilityMenu);
		
		MenuItem exitMenu = new MenuItem("Exit");
		exitMenu.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
		});
		trayMenu.add(exitMenu);
		
		trayIcon.setPopupMenu(trayMenu);
	}
	
	public static String formatTime(double timeDuration) {
		
		double timeSeconds = Math.ceil(timeDuration/1000);
		return (Math.round(Math.floor(timeSeconds/3600)) + ":" + Math.round((long)Math.floor((timeSeconds%3600)/60)) + ":" + Math.round(timeSeconds%60));
	}
	
	public static Set<Timer> getTimerList() {
		return timerMap.values();
	}
	
}
