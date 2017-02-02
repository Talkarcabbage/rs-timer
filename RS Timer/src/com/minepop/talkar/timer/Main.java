package com.minepop.talkar.timer;

import java.awt.AWTException;
import java.awt.Color;
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
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.JProgressBar;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;

import com.minepop.talkar.timer.Logger.LEVEL;
import com.minepop.talkar.timer.gui.MainWin;
import com.minepop.talkar.util.FileManager;

public class Main {
	
	public static final long DAY_LENGTH = 86400000;
	public static final long WEEK_LENGTH = 604800000;
	
	static TrayIcon trayIcon;
	static MainWin mainWin;
	static ArrayList<Timer> timerList = new ArrayList<Timer>();
	static ArrayList<JProgressBar> progressBarList = new ArrayList<JProgressBar>();
	static HashMap<JProgressBar, Timer> barToTimerMap = new HashMap<JProgressBar, Timer>();
	
	static ArrayList<String> cfgList = new ArrayList<String>();
	//static ArrayList<String> commentList = new ArrayList<String>();
	
	public static void main(String[] args) throws InterruptedException { //TODO
		System.out.println("Initializing GUI and tray...");
		try {
		    for (LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
		        if ("Nimbus".equals(info.getName())) {
		            UIManager.setLookAndFeel(info.getClassName());
		            break;
		        }
		    	
		    }
		} catch (Exception e) {}
		
		UIManager.put("nimbusOrange", new Color(87, 168, 57));
		
		mainWin = new MainWin();
		prepareSystemTray();
		mainWin.setVisible(true);
		FileManager.ensureExists("timers.cfg");
		
		System.out.println("Loading timers...");
		try {
		loadTimers();
		} catch (Exception e) {
			JOptionPane.showMessageDialog(null, "An exception occured while loading timer information. Run the jar using the console for more information.", "Error", JOptionPane.ERROR_MESSAGE);
			System.exit(1);
		}
		System.out.println("Timers loaded.");
		
		if (timerList.isEmpty()) {
			addTimer(System.currentTimeMillis(),120000, 0, true, "Sample: Two Minutes");
			addTimer(System.currentTimeMillis(),3600000, 0, true,  "Sample: Sixty Minutes");
			
		}
		
		
		//TODO why?
		//saveTimers();
		System.out.println("Initializion complete.");
		while (true) {
			tickTimers();
			Thread.sleep(500);
		}
	}
	
	static void tickTimers() {
		double startTime = 0;
		double duration = 0;
		double endTime = 0;
		double currentTime = System.currentTimeMillis();
		for (int i = 0; i < timerList.size(); i++) {
			
			startTime = timerList.get(i).getStartingTime();
			duration = timerList.get(i).getDurationTotal();
			endTime = startTime + duration;
			
			progressBarList.get(i).setToolTipText((formatTime((long)(endTime-currentTime))) + " (" + (Math.round(Math.round((100*(currentTime-startTime)/(duration)))))+ "%)");
			
			progressBarList.get(i).setValue(Math.round(Math.round((100*(currentTime-startTime)/(duration)))));
			if (currentTime > endTime) {
				progressBarList.get(i).setForeground(new Color(160, 215, 140));
				progressBarList.get(i).setToolTipText("Timer Complete!");
			} else {
				progressBarList.get(i).setForeground(Color.black);
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
				System.out.println("cfg: " + timerInfo[1]);
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
					System.out.println("Imported old tab data!");
					mainWin.addNewTab(mainWin.getGridRows(), mainWin.getGridColumns(), timerInfo[1]);
					importResave = true;
				} else {
					mainWin.addNewTab(Integer.parseInt(timerInfo[1]), Integer.parseInt(timerInfo[2]), timerInfo[3]);
				}
				
			} else if (timerInfo[0].equals("timer")) {
				addTimer(Double.parseDouble(timerInfo[1]), Double.parseDouble(timerInfo[2]), Integer.parseInt(timerInfo[3]), Boolean.parseBoolean(timerInfo[4]), timerInfo[5]);

			} else {
				System.out.println("Imported old timer data!");
				importResave=true;
				addTimer(Double.parseDouble(timerInfo[0]), Double.parseDouble(timerInfo[1]), 0, true, timerInfo[2]);
			}
			if (importResave) {
				Main.saveTimers();
			}
		}
	}
	
	
	public static void addTimer(double startingTime, double duration, int tab, boolean normalTimer, String name) {
		Timer newTimer = new Timer(startingTime, duration, name, tab, normalTimer, null);
		timerList.add(newTimer);
		JProgressBar newBar = new JProgressBar();
		newBar.setFont(new Font("SansSerif", Font.BOLD, 12));
		newBar.setStringPainted(true);
		newBar.setString(name);
		newBar.setName(name);
		newBar.setForeground(Color.blue);
		
		newBar.addMouseListener(mainWin);
		
		newTimer.SetProgressBar(newBar);
		
		mainWin.addTimerBar(newBar, newTimer.getTab()); //TODO
		mainWin.revalidate();
		mainWin.repaint();
		progressBarList.add(newBar);
		
		barToTimerMap.put(newBar, newTimer);
		
	}
	
	public static void removeTimer(JProgressBar bar) {
		Logger.log(LEVEL.DEBUG, "Removing a timer with bar named " + bar.getName());
		Timer timer = barToTimerMap.get(bar);
		if (timer == null) {
			Logger.log(LEVEL.ERROR, "Found null when attempting to delete timer. This is a serious error.");
			return;
		}
		timerList.remove(timer);
		mainWin.removeTimerBar(bar);
		progressBarList.remove(bar);
		barToTimerMap.remove(bar);
		
		mainWin.revalidate();
		mainWin.repaint();
		
	}
	
	public static void saveTimers() {
		
		String toSave = "";
		
		Logger.log(LEVEL.DEBUG, "Saving timers");
		
		for (String s : cfgList){
			toSave = toSave + s + "\n";
		}
		
		if (mainWin.getTabList().size() > 1) {
			for (int i = 1; i < mainWin.getTabList().size(); i++) {
				toSave += "tab," + ((GridLayout)(mainWin.getTabList().get(i).getLayout())).getRows() + "," + ((GridLayout)(mainWin.getTabList().get(i).getLayout())).getColumns() + "," + mainWin.getTabList().get(i).getName() + "\n";
			}
		}
		
		for (Timer t : timerList) {
			toSave = toSave + "timer," + t.startingTime + "," + t.duration + "," + t.tab + "," + t.isNormalTimer + "," + t.name + "\n";
		}
		
		FileManager.writeFile("timers.cfg", toSave);

	}

	public static void resetTimer(String timerName, JProgressBar bar) {
		Logger.log("Attempting timer reset for " + timerName);
		Timer t = barToTimerMap.get(bar);
		if (t == null) {
			Logger.log(LEVEL.ERROR, "Found null when attempting to reset timer");
		}
		if (t.isNormalTimer()){
			t.setStartingTime(System.currentTimeMillis());				
			bar.setForeground(Color.black);
			Logger.log(LEVEL.DEBUG, "Set normal timer with data: " + timerName + " | Starting time:" + t.getStartingTime() + " | Duration:" + t.getDurationTotal() + " | Tab: " + t.getTab());
		} else if (t.getDurationTotal() == DAY_LENGTH){
			t.setStartingTime(Math.floor(System.currentTimeMillis()/DAY_LENGTH)*DAY_LENGTH);
			Logger.log(LEVEL.DEBUG, "Set daily timer timer with data: " + timerName + " | Starting time:" + t.getStartingTime() + " | Duration:" + t.getDurationTotal() + " | Tab: " + t.getTab());
		} else if (t.getDurationTotal() == WEEK_LENGTH){
			t.setStartingTime((((Math.floor((System.currentTimeMillis()+DAY_LENGTH)/WEEK_LENGTH))*WEEK_LENGTH)-DAY_LENGTH));
			Logger.log(LEVEL.DEBUG, "Set weekly timer with data: " + timerName + " | Starting time:" + t.getStartingTime() + " | Duration:" + t.getDurationTotal() + " | Tab: " + t.getTab());

			}
		else {
			System.out.println("Failed to match timer type " + t.getName() + " with time:" + t.getDurationTotal() + " | Tab: " + t.getTab());
		}
		saveTimers();
	}
		
	

	static boolean isMainWinVisible = true;
	private static void prepareSystemTray() {
		File f = new File("taskBarIcon.png");
		if (!f.exists()) {
			FileManager.downloadFile("taskBarIcon.png", "https://www.dropbox.com/s/th13a1fsf5kj4st/Cabbage.png?dl=1");
		}
		ImageIcon taskBarIcon = new ImageIcon("taskBarIcon.png");
		trayIcon = new TrayIcon(taskBarIcon.getImage());
		try {
			SystemTray.getSystemTray().add(trayIcon);
		} catch (AWTException e) {
			System.err.println("Error creating system tray icon:");
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

	public static ArrayList<Timer> getTimerList() {
		return timerList;
	}

	public static void setTimerList(ArrayList<Timer> timerList) {
		Main.timerList = timerList;
	}

	public static ArrayList<JProgressBar> getProgressBarList() {
		return progressBarList;
	}

	public static void setProgressBarList(ArrayList<JProgressBar> progressBarList) {
		Main.progressBarList = progressBarList;
	}
	
	public static String formatTime(long timeDuration) {
		
		long timeSeconds = (long)Math.ceil(timeDuration/1000);
		return (Math.round(Math.floor(timeSeconds/3600)) + ":" + Math.round((long)Math.floor((timeSeconds%3600)/60)) + ":" + Math.round(timeSeconds%60));
	}
	
}
