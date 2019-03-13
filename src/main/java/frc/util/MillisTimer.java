package frc.util;

public class MillisTimer {

	private double startingTime;
	private double passedTime;
	private boolean isRunning;
	private double tempVariable;
	private StringBuilder strBuilder;
	
	public MillisTimer() {
		strBuilder = new StringBuilder();
		reset();
	}
	
	public double get() {
		return passedTime + ((System.currentTimeMillis() /1000.0) - startingTime);
	}
	
	public String getHMS() {
		tempVariable = get();
		strBuilder.setLength(0);
		strBuilder.append((int) (tempVariable / 3600) + ":");
		tempVariable %= 3600;
		strBuilder.append((int) (tempVariable / 60) + ":");
		tempVariable = tempVariable % 60;
		strBuilder.append(((int)(tempVariable*1000))/1000.0);
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
