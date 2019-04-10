/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot;

import edu.wpi.first.wpilibj.Compressor;
import frc.controls.Autonomous;
import frc.controls.Controls;
import frc.controls.CtrlMap;
import frc.controls.TeleOP;
import frc.subsystem.Drives;
import frc.subsystem.HAB;
import frc.subsystem.Hatch;
import frc.util.DebugTable;
import frc.util.Logger;

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
	
	private Logger logger;
	private DebugTable debugger;


	
	public RobotSystem(){
		drives = new Drives();
		hab = new HAB();
		hatch = new Hatch();
		teleop = new TeleOP(drives, hab, hatch);
		autonomous = new Autonomous(drives, hatch, hab);
		currentState = RobotState.STANDBY;
		currentControl = teleop;
		logger = Logger.getInstance();
		logger.start();
		debugger = new DebugTable();
		debugger.start();
//		CameraServer.getInstance().startAutomaticCapture();
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
		System.out.println("Starting TeleOP");
		currentControl = teleop;
		currentState = RobotState.TELE;
		drives.toTeleop();
//		hatch.toTele();
	}

	public void autonomous() {
		System.out.println("Starting autonomous");
		autonomous.reset();
		hatch.toAuto();
		drives.toAuto();
		currentControl = autonomous;
		currentState = RobotState.AUTO;
	}

	public void init(){
		drives.start();
		hab.start();
		hatch.start();
		logger.addPeriodicLog(drives::logPeriodicReady);
		logger.addPeriodicLog(hab::logPeriodicReady);
		logger.addPeriodicLog(hatch::logPeriodicReady);
	}

	@Override
	public void run(){
		while(true){
			switch(currentState){
			case STANDBY:
				break;
			case AUTO:
				if(teleop.isPressedButton(CtrlMap.XBOXCONTROLLER_MAIN, CtrlMap.XBOX_B)) {
					autonomous.stopAuto();
					System.out.println("Autonomous stopped");
				}
				if(autonomous.isDone()) {
					System.out.println("Autonomous finished, moving to TeleOP");
					teleop();
				}
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
