/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.subsystem;

/**
 * Add your docs here.
 */
public abstract class GenericSubsystem extends Thread{

    private String name;
	public GenericSubsystem(String name){
		this(name, Thread.NORM_PRIORITY);
	}
	
	public GenericSubsystem(String name, int priority){
		this.name = name;
		setPriority(priority);
	}


    public abstract void init();

    public abstract void execute();

    public abstract void debug();

    public abstract boolean isDone();

    public abstract long sleepTime();

    protected void log(String message){
		print(message);
	}
	
	protected void print(String message){
		System.out.println(name + ": " + message);
	}

    @Override
	public void run(){
		log("Initializing " + name + "...");
		//init();
		long timeWait = sleepTime();
		log("Starting " + name + "...");
		while(true){
			execute();
			try { Thread.sleep(timeWait); } catch (InterruptedException e) {}
		}
	}

}