package com.minepop.talkar.timer;

import java.awt.Color;

import javax.swing.JProgressBar;

public class Timer {
	double startingTime;
	double duration;
	String name;
	int tab;
	JProgressBar progressBar;
	
	public static final long DAY_LENGTH = 86400000;
	public static final long WEEK_LENGTH = 604800000;

	/**
	 * 
	 * @param startingTime - The system time the timer started at.
	 * @param durationTotal - The total time the timer should run for. This is used to reset the timer and check for completion.
	 * @param name - A name for the timer. This is used as a label in the GUI.
	 */
	public Timer(double targetTime, double durationTotal, String name, int tab, JProgressBar bar) {
		this.startingTime = targetTime;
		this.duration = durationTotal;
		this.name = name;
		this.tab = tab;
		progressBar = bar;
	}
	
	// Standard getters and setters
	
	public void setProgressBar(JProgressBar bar) {
		progressBar = bar;
	}
	
	public JProgressBar getProgressBar() {
		return progressBar;
	}

	public double getStartingTime() {
		return startingTime;
	}


	public void setStartingTime(double startingTime) {
		this.startingTime = startingTime;
	}


	public double getDurationTotal() {
		return duration;
	}


	public void setDurationTotal(double durationTotal) {
		this.duration = durationTotal;
	}


	public String getName() {
		return name;
	}


	public void setName(String name) {
		this.name = name;
	}

	
	public int getTab() {
		return tab;
	}


	public void setTab(int tab) {
		this.tab = tab;
	}
	
	//End standard getters and setters

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		long temp;
		temp = Double.doubleToLongBits(duration);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		temp = Double.doubleToLongBits(startingTime);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		result = prime * result + tab;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Timer other = (Timer) obj;
		if (Double.doubleToLongBits(duration) != Double
				.doubleToLongBits(other.duration))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (Double.doubleToLongBits(startingTime) != Double
				.doubleToLongBits(other.startingTime))
			return false;
		if (tab != other.tab)
			return false;
		if (progressBar != other.progressBar) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return "Timer [startingTime=" + startingTime + ", duration=" + duration
				+ ", name=" + name + ", tab=" + tab + "]";
	}
	
	
	/**
	 * Returns the percentage of this timer's progress toward completion, for setting progress bars.
	 * @return
	 */
	public int getPercentageComplete() {
		long rawPercentage = (Math.round(Math.floor((100*(System.currentTimeMillis() - startingTime)/(duration)))));
		return rawPercentage >= 100 ? 100 : (int) rawPercentage;
	}
	
	/**
	 * Returns the amount of time in millis until the timer is complete.
	 * @return
	 */
	public double getTimeRemaining() {
		System.out.println(startingTime + " " + duration + " " + System.currentTimeMillis());
		return (startingTime + duration) - System.currentTimeMillis();
	}
	
	/**
	 * Resets the timer to a status of incomplete. For normal timers, this should be 0% complete.
	 * For abnormal timers, this should set their next completion to the next applicable reset.
	 * This should be overridden.
	 */
	public void resetTimer() {
		setStartingTime(System.currentTimeMillis());	
		this.progressBar.setForeground(Color.black);
		Logger.DEBUG("Set normal timer with data: " + this.toString());

	}
	
	/**
	 * Resets the timer to a status of complete. This is typically/most easily done by setting the start-time to 0.
	 * This may be overridden.
	 */
	public void resetTimerComplete() {
		setStartingTime(0);
	}

	/**
	 * Returns the type of the timer. THIS MUST BE OVERRIDEN BY ANY SUBCLASS FOR PROPER BEHAVIOR.
	 * @return
	 */
	public String getTimerType() {
		return Main.STANDARDTIMER;
	}
	
	
	
}
