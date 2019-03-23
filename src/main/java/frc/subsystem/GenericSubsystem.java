/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.subsystem;

import edu.wpi.first.wpilibj.Sendable;
import edu.wpi.first.wpilibj.Timer;
import frc.util.Debugger;

/**
 * Add your docs here.
 */
public abstract class GenericSubsystem extends Thread {

	private String name;
	
	
	private double timeToPrint;
	private double lastPrinted;
	
	public GenericSubsystem(String name, int priority) {
		this.name = name;
		timeToPrint = 1;
		lastPrinted = Timer.getFPGATimestamp();
		setPriority(priority);
	}
	
	public GenericSubsystem(String name) {
		this(name, Thread.NORM_PRIORITY);
	}
	
	public static void addToTables(Sendable sendable, String subsystem, String name) {
		//LiveWindow won't update names sometimes for some reason? (but it does sometimes ?????)
		Debugger.addToTable(sendable, name, subsystem);
	}
	
	public void addToTables(Sendable sendable, String name) {
		addToTables(sendable, this.name, name);
	}
	
	public abstract void init();

	public abstract void execute();

	public abstract long sleepTime();
	
	public boolean isDone() {
		return false;
	}
	
	public void delayedPrints() {
		
	}
	
	public void smartDashboardInit() {
		
	}

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