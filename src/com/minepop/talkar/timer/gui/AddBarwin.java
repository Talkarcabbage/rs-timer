package com.minepop.talkar.timer.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.text.NumberFormat;
import java.util.logging.Level;
import java.util.logging.SimpleFormatter;
import java.util.logging.StreamHandler;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;
import javax.swing.border.EmptyBorder;
import javax.swing.text.NumberFormatter;

import com.google.common.base.Throwables;
import com.minepop.talkar.timer.Main;

/*
 * 	TODO this.
 */
public class AddBarwin extends JFrame {

	static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger("Timer");

	
	private static final long serialVersionUID = -7047886515230003423L;
	private JPanel contentPane;
	private JFormattedTextField dayInput;
	private JFormattedTextField hourInput;
	private JFormattedTextField minuteInput;
	private JFormattedTextField secondInput;
	private JButton createButton;
	private JButton cancelButton;
	private JRadioButton standardButton;
	private JRadioButton dailyButton;
	private JRadioButton weeklyButton;
	private JRadioButton monthlyButton;
	private JTextField nameInput;
	private JLabel lblName;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		
		try {
		    for (LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
		        if ("Nimbus".equals(info.getName())) {
		            UIManager.setLookAndFeel(info.getClassName());
		            break;
		        }
		    	
		    }
		} catch (Exception e) {
			logger.warning("Exception occured trying to set the look and feel.");
			logger.warning(Throwables.getStackTraceAsString(e));
		}
		
		EventQueue.invokeLater( () -> {
				try {
					AddBarwin frame = new AddBarwin();
					frame.resetAndDisplayWindow();
				} catch (Exception e) {
					logger.severe("Error while creating add timer GUI");
					logger.severe(Throwables.getStackTraceAsString(e));
					
				}
			
		});
	}

	/**
	 * Create the frame.
	 */
	public AddBarwin() {
		setTitle("Add Timer");
		setResizable(false);
		setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
		setBounds(100, 100, 550, 275);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);
		
		JPanel radioPanel = new JPanel();
		contentPane.add(radioPanel, BorderLayout.WEST);
		GridBagLayout gbl_radioPanel = new GridBagLayout();
		gbl_radioPanel.columnWidths = new int[]{72, 0};
		gbl_radioPanel.rowHeights = new int[] {35, 35, 35, 35};
		gbl_radioPanel.columnWeights = new double[]{0.0, Double.MIN_VALUE};
		gbl_radioPanel.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0};
		radioPanel.setLayout(gbl_radioPanel);
		
		ButtonGroup bg = new ButtonGroup();
		
		standardButton = new JRadioButton("Standard");
		standardButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				enableTextFields();
			}
		});
		GridBagConstraints gbc_standardButton = new GridBagConstraints();
		gbc_standardButton.anchor = GridBagConstraints.WEST;
		gbc_standardButton.insets = new Insets(0, 0, 5, 0);
		gbc_standardButton.gridx = 0;
		gbc_standardButton.gridy = 0;
		radioPanel.add(standardButton, gbc_standardButton);
		bg.add(standardButton);
				
		dailyButton = new JRadioButton("Daily");
		dailyButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				disableTextFields();
			}
		});
		dailyButton.setHorizontalAlignment(SwingConstants.LEFT);
		GridBagConstraints gbc_dailyButton = new GridBagConstraints();
		gbc_dailyButton.anchor = GridBagConstraints.WEST;
		gbc_dailyButton.insets = new Insets(0, 0, 5, 0);
		gbc_dailyButton.gridx = 0;
		gbc_dailyButton.gridy = 1;
		radioPanel.add(dailyButton, gbc_dailyButton);
		bg.add(dailyButton);
				
		
		weeklyButton = new JRadioButton("Weekly");
		weeklyButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				disableTextFields();
			}
		});
		GridBagConstraints gbc_weeklyButton = new GridBagConstraints();
		gbc_weeklyButton.anchor = GridBagConstraints.WEST;
		gbc_weeklyButton.insets = new Insets(0, 0, 5, 0);
		gbc_weeklyButton.gridx = 0;
		gbc_weeklyButton.gridy = 2;
		radioPanel.add(weeklyButton, gbc_weeklyButton);
		bg.add(weeklyButton);
				
						
		monthlyButton = new JRadioButton("Monthly");
		monthlyButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				disableTextFields();
			}
		});
		GridBagConstraints gbc_monthlyButton = new GridBagConstraints();
		gbc_monthlyButton.insets = new Insets(0, 0, 5, 0);
		gbc_monthlyButton.anchor = GridBagConstraints.WEST;
		gbc_monthlyButton.gridx = 0;
		gbc_monthlyButton.gridy = 3;
		radioPanel.add(monthlyButton, gbc_monthlyButton);
		bg.add(monthlyButton);

		
		JPanel buttonPanel = new JPanel();
		contentPane.add(buttonPanel, BorderLayout.EAST);
		GridBagLayout gbl_buttonPanel = new GridBagLayout();
		gbl_buttonPanel.columnWidths = new int[]{67, 0};
		gbl_buttonPanel.rowHeights = new int[]{75, 36, 28, 0};
		gbl_buttonPanel.columnWeights = new double[]{0.0, Double.MIN_VALUE};
		gbl_buttonPanel.rowWeights = new double[]{0.0, 0.0, 0.0, Double.MIN_VALUE};
		buttonPanel.setLayout(gbl_buttonPanel);
		
		createButton = new JButton("Create");
		createButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String name = (nameInput.getText().equals("") ? "Timer" : nameInput.getText());
				if (standardButton.isSelected()) {
					long time = 0;
					time += (Main.DAY_LENGTH * (Integer)dayInput.getValue());
					time += (3600 * (Integer)(hourInput.getValue()))*1000;
					time += (60 * (Integer)minuteInput.getValue())*1000;
					time += (Integer)(secondInput.getValue())*1000;
					logger.fine("Timer duration: " + time);
					if (time == 0) {
						return;
					}
					Main.addTimer(System.currentTimeMillis(), time, Main.mainWin.getCurrentTimerTab(), Main.STANDARDTIMER, name);
				} else if (dailyButton.isSelected()) {
					Main.addTimer(0, Main.DAY_LENGTH, Main.mainWin.getCurrentTimerTab(), Main.PERIODICTIMER, name).resetTimer();
				} else if (weeklyButton.isSelected()) {
					Main.addTimer(0, Main.WEEK_LENGTH, Main.mainWin.getCurrentTimerTab(), Main.PERIODICTIMER, name).resetTimer();
				} else if (monthlyButton.isSelected()) {
					Main.addTimer(0, 1, Main.mainWin.getCurrentTimerTab(), Main.MONTHLYTIMER, name).resetTimer(); //TODO is this safe?
				} else {
					logger.severe("Could not identify the selected radiobox to add a timer.");
					return;
				}
				Main.saveTimers();
				Main.addBarWin.setVisible(false);
			}
		});
		createButton.setPreferredSize(new Dimension(90, 28));
		GridBagConstraints gbc_createButton = new GridBagConstraints();
		gbc_createButton.fill = GridBagConstraints.VERTICAL;
		gbc_createButton.insets = new Insets(0, 0, 5, 0);
		gbc_createButton.gridx = 0;
		gbc_createButton.gridy = 0;
		buttonPanel.add(createButton, gbc_createButton);
		
		cancelButton = new JButton("Cancel");
		cancelButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Main.addBarWin.setVisible(false);
			}
		});
		cancelButton.setPreferredSize(new Dimension(90, 28));
		GridBagConstraints gbc_cancelButton = new GridBagConstraints();
		gbc_cancelButton.gridx = 0;
		gbc_cancelButton.gridy = 2;
		buttonPanel.add(cancelButton, gbc_cancelButton);
		
		JPanel inputPanel = new JPanel();
		contentPane.add(inputPanel, BorderLayout.SOUTH);
		GridBagLayout gbl_inputPanel = new GridBagLayout();
		gbl_inputPanel.columnWidths = new int[] {125, 125, 125, 125};
		gbl_inputPanel.rowHeights = new int[] {20, 20};
		gbl_inputPanel.columnWeights = new double[]{1.0, 1.0, 1.0, 1.0};
		gbl_inputPanel.rowWeights = new double[]{0.0, 0.0};
		inputPanel.setLayout(gbl_inputPanel);
		
		JLabel dayLabel = new JLabel("Days");
		GridBagConstraints gbc_dayLabel = new GridBagConstraints();
		gbc_dayLabel.insets = new Insets(0, 0, 5, 5);
		gbc_dayLabel.gridx = 0;
		gbc_dayLabel.gridy = 0;
		inputPanel.add(dayLabel, gbc_dayLabel);
		
		JLabel hourLabel = new JLabel("Hours");
		GridBagConstraints gbc_hourLabel = new GridBagConstraints();
		gbc_hourLabel.insets = new Insets(0, 0, 5, 5);
		gbc_hourLabel.gridx = 1;
		gbc_hourLabel.gridy = 0;
		inputPanel.add(hourLabel, gbc_hourLabel);
		
		JLabel minuteLabel = new JLabel("Minutes");
		GridBagConstraints gbc_minuteLabel = new GridBagConstraints();
		gbc_minuteLabel.insets = new Insets(0, 0, 5, 5);
		gbc_minuteLabel.gridx = 2;
		gbc_minuteLabel.gridy = 0;
		inputPanel.add(minuteLabel, gbc_minuteLabel);
		
		JLabel secondLabel = new JLabel("Seconds");
		GridBagConstraints gbc_secondLabel = new GridBagConstraints();
		gbc_secondLabel.insets = new Insets(0, 0, 5, 0);
		gbc_secondLabel.gridx = 3;
		gbc_secondLabel.gridy = 0;
		inputPanel.add(secondLabel, gbc_secondLabel);
		
		dayInput = new JFormattedTextField(getNumberFormatter());
		dayInput.addFocusListener(new FocusAdapter() {
			@Override
			public void focusGained(FocusEvent arg0) {
				SwingUtilities.invokeLater(new Runnable() {
					@Override
					public void run() {
						dayInput.selectAll();
					}
				});
			}
		});
		GridBagConstraints gbc_dayInput = new GridBagConstraints();
		gbc_dayInput.insets = new Insets(0, 0, 0, 5);
		gbc_dayInput.fill = GridBagConstraints.HORIZONTAL;
		gbc_dayInput.gridx = 0;
		gbc_dayInput.gridy = 1;
		inputPanel.add(dayInput, gbc_dayInput);
		dayInput.setColumns(10);
		
		
		hourInput = new JFormattedTextField(getNumberFormatter());
		hourInput.addFocusListener(new FocusAdapter() {
			@Override
			public void focusGained(FocusEvent e) {
				SwingUtilities.invokeLater(hourInput::selectAll);
					
			}
		});
		GridBagConstraints gbc_hourInput = new GridBagConstraints();
		gbc_hourInput.insets = new Insets(0, 0, 0, 5);
		gbc_hourInput.fill = GridBagConstraints.HORIZONTAL;
		gbc_hourInput.gridx = 1;
		gbc_hourInput.gridy = 1;
		inputPanel.add(hourInput, gbc_hourInput);
		hourInput.setColumns(10);
		
		minuteInput = new JFormattedTextField(getNumberFormatter());
		minuteInput.addFocusListener(new FocusAdapter() {
			@Override
			public void focusGained(FocusEvent e) {
				SwingUtilities.invokeLater( minuteInput::selectAll);

			}
		});
		GridBagConstraints gbc_minuteInput = new GridBagConstraints();
		gbc_minuteInput.insets = new Insets(0, 0, 0, 5);
		gbc_minuteInput.fill = GridBagConstraints.HORIZONTAL;
		gbc_minuteInput.gridx = 2;
		gbc_minuteInput.gridy = 1;
		inputPanel.add(minuteInput, gbc_minuteInput);
		minuteInput.setColumns(10);
		
		secondInput = new JFormattedTextField(getNumberFormatter());
		secondInput.addFocusListener(new FocusAdapter() {
			@Override
			public void focusGained(FocusEvent e) {
				SwingUtilities.invokeLater(secondInput::selectAll);
			}
		});
		GridBagConstraints gbc_secondInput = new GridBagConstraints();
		gbc_secondInput.fill = GridBagConstraints.HORIZONTAL;
		gbc_secondInput.gridx = 3;
		gbc_secondInput.gridy = 1;
		inputPanel.add(secondInput, gbc_secondInput);
		secondInput.setColumns(10);
		
		JPanel labelPanel = new JPanel();
		contentPane.add(labelPanel, BorderLayout.CENTER);
		
		JLabel lblSelectATimer = new JLabel("<html>Select a timer type and name and, if creating a standard timer, a duration, to add a timer to the current tab (when Create is pressed). Note: timers cannot have 0 duration, attempting such will do nothing. <br><br>\r\n* Standard timers require a duration, input at the bottom. <br>\r\n* Daily timers reset daily at UTC 0:00. <br>\r\n* Weekly timers reset weekly on Wednesday UTC 0:00. <br>\r\n* Monthly timers reset on the first day of the month.</html>");
		lblSelectATimer.setPreferredSize(new Dimension(350, 140));
		labelPanel.add(lblSelectATimer);
		
		lblName = new JLabel("Name");
		labelPanel.add(lblName);
		
		nameInput = new JTextField();
		labelPanel.add(nameInput);
		nameInput.setColumns(10);
		
		NumberFormat dayFormat = NumberFormat.getInstance();
		NumberFormatter dayFormatter = new NumberFormatter(dayFormat);
		dayFormatter.setValueClass(Integer.class);
		dayFormatter.setMinimum(0);
		dayFormatter.setMaximum(Integer.MAX_VALUE);
		dayFormatter.setAllowsInvalid(false);
		dayFormatter.setCommitsOnValidEdit(true);
	}
	
	
	protected void enableTextFields() {
		dayInput.setEnabled(true);
		hourInput.setEnabled(true);
		minuteInput.setEnabled(true);
		secondInput.setEnabled(true);
	}

	protected void disableTextFields() {
		dayInput.setEnabled(false);
		hourInput.setEnabled(false);
		minuteInput.setEnabled(false);
		secondInput.setEnabled(false);
	}

	static NumberFormatter getNumberFormatter() {
		NumberFormat format = NumberFormat.getInstance();
		NumberFormatter formatter = new NumberFormatter(format);
		formatter.setValueClass(Integer.class);
		formatter.setMinimum(0);
		formatter.setMaximum(Integer.MAX_VALUE);
		formatter.setCommitsOnValidEdit(true);
		return formatter;
	}
	
	public void resetAndDisplayWindow() {
		standardButton.doClick();
		dayInput.setText("0");
		hourInput.setText("0");
		minuteInput.setText("0");
		secondInput.setText("0");
		nameInput.setText("");
		this.setVisible(true);
	}

}
