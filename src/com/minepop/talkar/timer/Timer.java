package com.minepop.talkar.timer;

import javax.swing.JProgressBar;

public class Timer {
	double startingTime;
	double duration;
	String name;
	int tab;
	boolean isNormalTimer;
	JProgressBar progressBar;
	


	/**
	 * 
	 * @param startingTime - The system time the timer started at.
	 * @param durationTotal - The total time the timer should run for. This is used to reset the timer and check for completion.
	 * @param name - A name for the timer. This is used as a label in the GUI.
	 */
	public Timer(double targetTime, double durationTotal, String name, int tab, boolean isNormalTimer, JProgressBar bar) {
		this.startingTime = targetTime;
		this.duration = durationTotal;
		this.name = name;
		this.tab = tab;
		this.isNormalTimer = isNormalTimer;
		progressBar = bar;
	}
	
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


	public boolean isNormalTimer() {
		return isNormalTimer;
	}


	public void setNormalTimer(boolean isNormalTimer) {
		this.isNormalTimer = isNormalTimer;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		long temp;
		temp = Double.doubleToLongBits(duration);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		result = prime * result + (isNormalTimer ? 1231 : 1237);
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
		if (isNormalTimer != other.isNormalTimer)
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
				+ ", name=" + name + ", tab=" + tab + ", isNormalTimer="
				+ isNormalTimer + "]";
	}
	
	
	/**
	 * Returns the percentage of this timer's progress toward completion, for setting progress bars.
	 * @return
	 */
	public int getPercentageComplete() {
		
		
		
		return (Math.round(Math.round((100*(System.currentTimeMillis() - startingTime)/(duration)))));
	}
	
	/**
	 * Returns the amount of time in millis until the timer is complete.
	 * @return
	 */
	public double getTimeRemaining() {
		return (startingTime + duration) - System.currentTimeMillis();
	}

	
}
