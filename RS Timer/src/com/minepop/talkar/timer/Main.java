package com.minepop.talkar.timer;

import java.awt.AWTException;
import java.awt.Color;
import java.awt.Font;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.Toolkit;
import java.awt.TrayIcon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.ArrayList;

import javax.swing.ImageIcon;
import javax.swing.JProgressBar;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;

import com.minepop.talkar.timer.gui.Taskbarwin;
import com.minepop.talkar.util.FileManager;

public class Main {
	
	//I am a potato This is an edit.
	static TrayIcon trayIcon;
	static Taskbarwin mainWin;
	static ArrayList<Timer> timerList = new ArrayList<Timer>();
	static ArrayList<JProgressBar> progressBarList = new ArrayList<JProgressBar>();
	
	static ArrayList<String> cfgList = new ArrayList<String>();
	//static ArrayList<String> commentList = new ArrayList<String>();
	
	public static void main(String[] args) throws InterruptedException {
		System.out.println();
		try {
		    for (LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
		        if ("Nimbus".equals(info.getName())) {
		            UIManager.setLookAndFeel(info.getClassName());
		            break;
		        }
		    }
		} catch (Exception e) {}
		
		mainWin = new Taskbarwin();
		prepareSystemTray();
		mainWin.setVisible(true);
		FileManager.ensureExists("timers.cfg");
		loadTimers();

		
		
		if (timerList.isEmpty()) {
			addTimer(System.currentTimeMillis(),120000, 0, true, "Two Minutes");
			addTimer(System.currentTimeMillis(),3600000, 0, true,  "Sixty Minutes");
			
		}
		
		
		
		saveTimers();
		while (true) {
			tickTimers();
			Thread.sleep(1000);
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
				progressBarList.get(i).setForeground(Color.green);
				progressBarList.get(i).setToolTipText("Timer Complete!");
			} else {
				progressBarList.get(i).setForeground(Color.black);
			}
		}
		mainWin.revalidate();
		mainWin.repaint();
	}
	
	private static void loadTimers() {
		String[] timersStringArray = FileManager.readFileSplit("timers.cfg");
		
		for (String s : timersStringArray) {
			if (s.isEmpty() || s.startsWith("/")) {
				
				continue;
			}
			String[] timerInfo = s.split(",");
			if (timerInfo[0].equals("cfg")) {
				cfgList.add(s);
				if (timerInfo[1].equals("mainTabName")) {
					mainWin.getTabbedPane().setTitleAt(0, timerInfo[2]);
				} else if (timerInfo[1].equals("gridColumns") || timerInfo[1].equals("gridRows")) {
					mainWin.setGridRows(Integer.parseInt(timerInfo[2]));
				} else if (timerInfo[1].equals("winSize")) {
					mainWin.setBounds(Toolkit.getDefaultToolkit().getScreenSize().width-Integer.parseInt(timerInfo[2]), Toolkit.getDefaultToolkit().getScreenSize().height-(Integer.parseInt(timerInfo[3])+40), Integer.parseInt(timerInfo[2]) , Integer.parseInt(timerInfo[3]));
				}
				
			} else if (timerInfo[0].equals("tab")) {
				mainWin.addNewTab(timerInfo[1]);
				
			} else if (timerInfo[0].equals("timer")) {
				addTimer(Double.parseDouble(timerInfo[1]), Double.parseDouble(timerInfo[2]), Integer.parseInt(timerInfo[3]), Boolean.parseBoolean(timerInfo[4]), timerInfo[5]);

			} else {
				System.out.println("Old Data");
				addTimer(Double.parseDouble(timerInfo[0]), Double.parseDouble(timerInfo[1]), 0, true, timerInfo[2]);
			}
			
		}
	}
	
	
	public static void addTimer(double startingTime, double duration, int tab, boolean normalTimer, String name) {
		Timer newTimer = new Timer(startingTime, duration, name, tab, normalTimer);
		timerList.add(newTimer);
		JProgressBar newBar = new JProgressBar();
		newBar.setFont(new Font("SansSerif", Font.BOLD, 12));
		newBar.setStringPainted(true);
		newBar.setString(name);
		newBar.setName(name);
		newBar.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				resetTimer(((JProgressBar)(e.getSource())).getName(), (JProgressBar)e.getSource());
			}
		});
		mainWin.addTimerBar(newBar, newTimer.getTab());
		mainWin.revalidate();
		mainWin.repaint();
		progressBarList.add(newBar);
		
	}
	
	public static void removeTimer(String timerName) {
		for (int i = 0; i < timerList.size(); i++) {
			if (timerList.get(i).getName().equals(timerName)) {
				timerList.remove(i);
				mainWin.removeTimerBar(progressBarList.get(i));
				progressBarList.remove(i);
				mainWin.revalidate();
				mainWin.repaint();
				saveTimers();
			}
			
		}
	}
	
	public static void saveTimers() {
		
		String toSave = "";
		
		for (String s : cfgList){
			toSave = toSave + s + "\n";
		}
		
		if (mainWin.getTabList().size() > 1) {
			for (int i = 1; i < mainWin.getTabList().size(); i++) {
				toSave += "tab," + mainWin.getTabList().get(i).getName() + "\n";
			}
		}
		
		for (Timer t : timerList) {
			toSave = toSave + "timer," + t.startingTime + "," + t.duration + "," + t.tab + "," + t.isNormalTimer + "," + t.name + "\n";
		}
		
		FileManager.writeFile("timers.cfg", toSave);

	}

	private static void resetTimer(String timerName, JProgressBar bar) {
		for (Timer t : timerList) {
			if (t.getName().equals(timerName)) {
				
				if (t.isNormalTimer()){
					t.setStartingTime(System.currentTimeMillis());				
					bar.setForeground(Color.black);
				} else {
					t.setStartingTime(Math.floor(System.currentTimeMillis()/86400000)*86400000);
					t.setDurationTotal(86400000);
				}
				saveTimers();
				break;
			}
		}
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
