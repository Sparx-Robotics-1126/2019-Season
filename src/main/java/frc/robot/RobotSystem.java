/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot;

import frc.controls.Autonomous;
import edu.wpi.first.wpilibj.Compressor;
import frc.controls.Controls;
import frc.controls.TeleOP;
import frc.subsystem.Drives;
import frc.subsystem.HAB;
import frc.subsystem.Hatch;

/**
 * Add your docs here.
 */
public class RobotSystem extends Thread{

	private RobotState currentState;
	private TeleOP teleop;
	private Autonomous autonomous;
	private Controls currentControl; 
	private Drives drives;
	private HAB hab;
	private Hatch hatch;

	private Compressor compress;

	public RobotSystem(){
		drives = new Drives();
		hab = new HAB();
		hatch = new Hatch();
		teleop = new TeleOP(drives, hab, hatch);
		autonomous = new Autonomous(drives, hatch, hab);
		currentState = RobotState.STANDBY;
		currentControl = teleop;
		compress = new Compressor(IO.ROBOT_COMPRESSOR);
		compress.setClosedLoopControl(true);
	}


	public enum RobotState{
		STANDBY,
		AUTO,
		TELE;
	}

	public void resetVision()
	{
		if(drives != null) {
			drives.resetVision();
		}
	}

	public void teleop() {
		currentControl = teleop;
		currentState = RobotState.TELE;
		drives.toTeleop();
	}

	public void autonomous() {
		System.out.println("Starting autonomous");
		autonomous.reset();
		currentControl = autonomous;
		currentState = RobotState.AUTO;
	}

	public void init(){
		drives.start();
		hab.start();
		hatch.start();
	}

	@Override
	public void run(){
		while(true){
			switch(currentState){
			case STANDBY:
				break;
			case AUTO:
			case TELE:
				currentControl.execute();
			}

			try {
				Thread.sleep(50);
			} catch (InterruptedException e) {
			}
		}
	}

}
