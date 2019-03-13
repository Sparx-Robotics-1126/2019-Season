package frc.util;

public class MillisTimer {

	private double startingTime;
	private double passedTime;
	private boolean isRunning;
	private double tempDouble;
	private String tempString;
	private StringBuilder strBuilder;
	
	public MillisTimer() {
		strBuilder = new StringBuilder();
		reset();
	}
	
	public double get() {
		return passedTime + ((System.currentTimeMillis() /1000.0) - startingTime);
	}
	
	public String getHMS() {
		tempDouble = get();
		strBuilder.setLength(0);
		tempString = (int) (tempDouble / 3600) + ":";
		if(tempString.length() < 3) {
			tempString = "0" + tempString;
		}
		strBuilder.append(tempString);
		tempDouble %= 3600;
		tempString = (int) (tempDouble / 60) + ":";
		if(tempString.length() == 2) {
			tempString = "0" + tempString;
		}
		strBuilder.append(tempString);
		tempDouble = tempDouble % 60;
		strBuilder.append(((int)(tempDouble*1000))/1000.0);
		return strBuilder.toString();
	}
	
	public void start() {
		if(!isRunning) {
			isRunning = true;
			startingTime = System.currentTimeMillis() / 1000.0;
		} else {
			System.out.println("Timer already running!");
		}
	}
	
	public void stop() {
		if(isRunning) {
			isRunning = false;
			passedTime += ((System.currentTimeMillis() /1000.0) - startingTime);
		} else {
			System.out.println("Timer already stopped!");
		}
	}
	
	public void reset() {
		startingTime = System.currentTimeMillis() / 1000.0;
		isRunning = false;
		passedTime = 0;
	}
	
	
}
