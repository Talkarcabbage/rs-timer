package com.minepop.talkar.timer.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionAdapter;
import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.SimpleFormatter;
import java.util.logging.StreamHandler;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JSlider;
import javax.swing.JTabbedPane;
import javax.swing.JToggleButton;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

import com.minepop.talkar.timer.Main;
import com.minepop.talkar.timer.Timer;

public class MainWin extends JFrame implements ActionListener, MouseListener { //NOSONAR

	static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger("Timer");

	
	private static final long serialVersionUID = -2445104311740312791L;
	
	/*
	 * This variable handles the 'remove mode' system. Timers and tabs
	 * can be removed by clicking the remove button then clicking
	 * the item to remove.
	 */
	boolean removeModeTimers = false;

	private JPanel contentPane;
	private JSlider slider;
	ArrayList<JPanel> tabList = new ArrayList<>();
	int gridRows = 3;
	int gridColumns = 0;
	


	/**
	 * Launch the application.
	 */


	/**
	 * Create the frame.
	 */
	int moveXinit;
	int moveYinit;
	private JButton addTimerButton;
	private JPanel buttonPanel;
	private JToggleButton removeTimerButton;
	private JTabbedPane tabbedPane;
	private JPanel mainPanel;
	private JButton addTabButton;
	private JButton removeTabButton;
	public JPanel configPanel;
	
	static final String SAN_SERIF = "SansSerif";

	public MainWin() {
		this.setUndecorated(true);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(Toolkit.getDefaultToolkit().getScreenSize().width-350, Toolkit.getDefaultToolkit().getScreenSize().height-220, 350, 180);
		contentPane = new JPanel();


		contentPane.setFont(new Font("Arial", Font.BOLD, 11));
		contentPane.setBorder(null);
		setContentPane(contentPane);
		contentPane.setLayout(new BorderLayout(0, 0));
		
		
		configPanel = new JPanel();
		configPanel.setBackground(Color.GRAY);
		contentPane.add(configPanel, BorderLayout.EAST);
		
		JCheckBox chckbxAot = new JCheckBox("AOT");
		chckbxAot.addActionListener(this);
		chckbxAot.setToolTipText("Makes the timer window always visible, and causes the configuration bar to hide when the window is not focused.");
		configPanel.setLayout(new BorderLayout(0, 0));
		
		configPanel.add(chckbxAot, BorderLayout.NORTH);
		
		slider = new JSlider();
		slider.setValue(100);
		slider.setMinimum(5);
		slider.setToolTipText("Sets the transparency of the window.");
		
		slider.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent arg0) {
				setTrans(((JSlider)arg0.getComponent()).getValue());
			}
		});
		slider.setOrientation(SwingConstants.VERTICAL);
		configPanel.add(slider);
		
		buttonPanel = new JPanel();
		buttonPanel.setBackground(Color.GRAY);
		buttonPanel.setBorder(null);
		configPanel.add(buttonPanel, BorderLayout.SOUTH);
		buttonPanel.setLayout(new BorderLayout(0, 0));
		
		addTimerButton = new JButton("+");
		addTimerButton.setToolTipText("Adds a new timer to the current tab.");
		addTimerButton.setName("addTimerButton");
		buttonPanel.add(addTimerButton, BorderLayout.NORTH);
		addTimerButton.addActionListener(this);
		addTimerButton.setFont(new Font(SAN_SERIF, Font.BOLD, 12));
		addTimerButton.setForeground(Color.GREEN);
		addTimerButton.setBackground(Color.GRAY);
		
		removeTimerButton = new JToggleButton("-");
		removeTimerButton.setName("removeTimerButton");
		removeTimerButton.setToolTipText("Click to toggle remove mode: clicking a timer will remove it.");
		removeTimerButton.addActionListener(this);
		
		addTabButton = new JButton("T");
		addTabButton.setToolTipText("Adds a new tab.");
		addTabButton.addActionListener(this);
		addTabButton.setForeground(Color.GREEN);
		addTabButton.setFont(new Font(SAN_SERIF, Font.BOLD, 12));
		addTabButton.setBackground(Color.GRAY);
		addTabButton.setName("addTabButton");
		buttonPanel.add(addTabButton, BorderLayout.WEST);
		removeTimerButton.setForeground(Color.RED);
		removeTimerButton.setFont(new Font(SAN_SERIF, Font.BOLD, 12));
		removeTimerButton.setBackground(Color.GRAY);
		buttonPanel.add(removeTimerButton, BorderLayout.SOUTH);
		
		removeTabButton = new JButton("T");
		removeTabButton.setToolTipText("Removes the current tab. Does not work on the main tab.");
		removeTabButton.addActionListener(this);
		removeTabButton.setBackground(Color.GRAY);
		removeTabButton.setForeground(Color.RED);
		removeTabButton.setName("removeTabButton");
		buttonPanel.add(removeTabButton, BorderLayout.EAST);
		
		tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		tabbedPane.setBorder(null);
		contentPane.add(tabbedPane, BorderLayout.CENTER);
		
		tabbedPane.addMouseMotionListener(new MouseMotionAdapter() {
			@Override
			public void mouseDragged(MouseEvent arg0) {
				setWinPosition(arg0.getXOnScreen()-moveXinit, arg0.getYOnScreen()-moveYinit);
			}
		});
		tabbedPane.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				moveXinit = e.getX();
				moveYinit = e.getY();
			}
			
		});
		
		mainPanel = new JPanel();
		tabbedPane.addTab("Main", null, mainPanel, null);
		mainPanel.setBorder(null);
		tabList.add(mainPanel);
		mainPanel.setLayout(new GridLayout(gridRows, gridColumns, 2, 2));
		
	}
	
	public ArrayList<JPanel> getTabList() {
		return tabList;
	}

	
	public void setTabList(ArrayList<JPanel> tabList) {
		this.tabList = tabList;
	}
	
	public void toggleAOT(boolean isOnTop) {
		this.setAlwaysOnTop(isOnTop);
	}
	
	public void addTimerBar(JProgressBar bar, int tab) {
		tabList.get(tab).add(bar);
	}
	
	public void removeTimerBar(JProgressBar bar) {
		bar.getParent().remove(bar);
	}
	
	public int getCurrentTimerTab() {
		return this.tabbedPane.getSelectedIndex();
	}
	
	public void setTrans(int transparency) {
		this.setOpacity(((float)transparency)/100.0f);
	}
	
	public void setWinPosition(int x, int y) {
		this.setBounds(x, y, this.getWidth(), this.getHeight());
	}
	
	public JTabbedPane getTabbedPane() {
		return tabbedPane;
	}

	public int getGridRows() {
		return gridRows;
	}
	
	public int getGridColumns() {
		return gridColumns;
	}

	public void setGridRows(int gridRows) {
		this.gridRows = gridRows;
		if (gridColumns == 0 && gridRows == 0) {
			setGridColumns(2);
		}
		resetGridLayout();
	}
	
	public void setGridColumns(int gridColumns) {
		this.gridColumns = gridColumns;
		if (gridRows == 0 && gridColumns == 0) {
			setGridRows(3);
		}
		resetGridLayout();
	}
	
	public void resetGridLayout() {
		for (JPanel p : this.getTabList()) {
			GridLayout panelLayout = (GridLayout)(p.getLayout());
			if (gridRows == 0) {
				panelLayout.setColumns(gridColumns);
			}
			panelLayout.setRows(gridRows);
			panelLayout.setColumns(gridColumns);
		}
		this.revalidate();
		this.repaint();
		logger.fine("Rows/Columns cfg modified :: Rows: " + gridRows + " | " + "Columns: " + gridColumns);
		
	}
	
	public void addNewTab(int gridRows, int gridColumns, String name) {
		JPanel newPanel = new JPanel();
		newPanel.setBorder(null);
		newPanel.setLayout(new GridLayout(gridRows, gridColumns, 2, 2));
		newPanel.setName(name);
		tabList.add(newPanel);
		tabbedPane.addTab(name, newPanel);
	}

	//*******************************************Event Code****************************************
	
	/**
	 * Handles all of MainWin's event listening.
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		logger.fine("Event: Generic Action Performed/Main Event Listener");
		Object o = e.getSource();
		
		if (o instanceof JProgressBar) {
			
		} else if (o instanceof JCheckBox) {
			toggleAOT(((JCheckBox)e.getSource()).isSelected());
		} else if (o instanceof JToggleButton) { 
			onToggleRemove();
			return;
			
		} else if (o instanceof JButton) {
			if ("addTimerButton".equals(((JButton) o).getName())) {
				
				Main.addBarWin.resetAndDisplayWindow();				
			
			} else if("removeTabButton".equals(((JButton) o).getName())) {
				
				int toRemove = this.getCurrentTimerTab();
				if (toRemove > 0 && toRemove < tabList.size()) {
					int doRemove = JOptionPane.showConfirmDialog(this, "Are you sure you want to remove the current tab?", "Confirm", JOptionPane.OK_CANCEL_OPTION);
					logger.fine("JOptionpane result: " + doRemove);
					
					if (doRemove == JOptionPane.OK_OPTION) {
						removeTimerTab(toRemove);
						Main.saveTimers();
					}
				}
			} else if ("addTabButton".equals(((JButton) o).getName())) {
				String tabName = JOptionPane.showInputDialog(null, "Input a name for the tab.");
				int tabRows = Integer.parseInt(JOptionPane.showInputDialog("Enter the number of rows for the tab.", this.getGridRows()));
				int tabColumns = Integer.parseInt(JOptionPane.showInputDialog("Enter the number of columns for the tab.", this.getGridColumns()));
				addNewTab(tabRows, tabColumns, tabName);
				Main.saveTimers();
			}
		}
		this.disableRemoveBar();
		
	}
	
	public void removeTimerTab(int tabNum) {
		Iterator<Timer> it = Main.getTimerList().iterator();
		while (it.hasNext()) {
			Timer timer = it.next();
			if (timer.getTab() == tabNum) {
				it.remove();
				continue;
			} else if (timer.getTab() > tabNum) {
				timer.setTab(timer.getTab()-1);
			}
		}
		getTabList().get(tabNum).getParent().remove(getTabList().get(tabNum));
		getTabList().remove(tabNum);
		
	}
	
	public void onClickTimerBar(JProgressBar b, boolean setComplete) {
		logger.fine("Event: onClickTimerBar");
		if (removeModeTimers) {
			Main.removeTimer(b);
			this.disableRemoveBar();
			Main.saveTimers();
		} else {
			if (!setComplete) {
				Main.resetTimer(b.getName(), b);
			} else {
				Main.resetTimerComplete(b.getName(), b);
			}
		}
	}
	
	public void onToggleRemove() {
		logger.fine("onToggleRemove: " + removeTimerButton.isSelected());
		this.removeModeTimers = this.removeTimerButton.isSelected();
	}
	
	public void disableRemoveBar() {
		this.removeModeTimers = false;
		this.removeTimerButton.setSelected(false);

	}
	
	public JToggleButton getRemoveTimerButton() {
		return removeTimerButton;
	}

	//*********************MOUSE EVENT CODE***********************
	@Override
	public void mouseClicked(MouseEvent arg0) {
		if (arg0.getSource() instanceof JProgressBar) {
			if (SwingUtilities.isRightMouseButton(arg0)) {
				logger.fine("Resetting a timer to complete status:" + ((JProgressBar)arg0.getSource()).getString());
				onClickTimerBar((JProgressBar)arg0.getSource(), true);
			} else {
				logger.fine("Resetting a timer to incomplete status:" + ((JProgressBar)arg0.getSource()).getString());
				onClickTimerBar((JProgressBar)arg0.getSource(), false);
			}
		}
		
	}

	@Override
	public void mouseEntered(MouseEvent arg0) {
		//Unnecessary
	
	}
	@Override
	public void mouseExited(MouseEvent arg0) {
		//Unnecessary
	}
	@Override
	public void mousePressed(MouseEvent arg0) {
		//Unnecessary
	}
	@Override
	public void mouseReleased(MouseEvent arg0) {
		//Unnecessary
	}
}
