package io.github.talkarcabbage.rstimer.newtimers;

import java.util.Map;

public class Hourly extends NewTimer {
	
	long duration;

	public Hourly(String name, int tabID, boolean audio) {
		super(name, tabID, audio);
		throw new UnsupportedOperationException("Not yet implemented");
	}
	
	public Hourly(Map<String, String> dataMap) {
		super(dataMap);
		throw new UnsupportedOperationException("Not yet implemented");
	}

	@Override
	public void resetTimer() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void resetTimerComplete() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String getTimerTypeString() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getTooltipText() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getPercentageComplete() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public long getTimeRemaining() {
		// TODO Auto-generated method stub
		return 0;
	}
	
	public long getDuration() {
		return duration;
	}

}
