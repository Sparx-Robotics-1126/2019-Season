/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.subsystem;

import edu.wpi.first.wpilibj.Sendable;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.livewindow.LiveWindow;

/**
 * Add your docs here.
 */
public abstract class GenericSubsystem extends Thread {

	private String name;
	
	private double timeToPrint;
	private double lastPrinted;
	
	public GenericSubsystem(String name) {
		this.name = name;
		timeToPrint = 1;
		lastPrinted = Timer.getFPGATimestamp();
		setPriority(Thread.NORM_PRIORITY);
	}
	
	public static void addToTables(Sendable sendable, String subsystem, String name) {
		LiveWindow.add(sendable);
		sendable.setName(subsystem, name);
	}
	
	public void addToTables(Sendable sendable, String name) {
		addToTables(sendable, this.name, name);
	}
	
	public abstract void init();

	public abstract void execute();

	public abstract boolean isDone();

	public abstract long sleepTime();
	
	public abstract void delayedPrints();
	
	public abstract void smartDashboardInit();

	protected void log(String message) {
		print(message);
	}

	protected void print(String message) {
		System.out.println(name + ": " + message);
	}

	@Override
	public void run() {
		log("Initializing " + name + "...");
		init();
		smartDashboardInit();
		long timeWait = sleepTime();
		log("Starting " + name + "...");
		while (true) {
			execute();
			if(timeToPrint + lastPrinted < Timer.getFPGATimestamp()) {
				delayedPrints();
				lastPrinted = Timer.getFPGATimestamp();
			}
			try {
				Thread.sleep(timeWait);
			} catch (InterruptedException e) {
			}
		}
	}

}