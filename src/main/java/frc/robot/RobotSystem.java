/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot;

import frc.controls.Controls;
import frc.controls.TeleOP;
import frc.subsystem.Drives;
import frc.subsystem.HAB;

/**
 * Add your docs here.
 */
public class RobotSystem extends Thread{

    private RobotState currentState;
    private Controls teleop;
    private Controls currentControl; 
    private Drives drives;
    private HAB hab;

    public RobotSystem(){
        drives = new Drives();
        drives.init();
        hab = new HAB();
        hab.init();
        teleop = new TeleOP(drives, hab);
        currentState = RobotState.STANDBY;
        currentControl = teleop;
    }

    
    public enum RobotState{
		STANDBY,
		AUTO,
		TELE;
    }

    public void teleop() {
    	currentControl = teleop;
        currentState = RobotState.TELE;
        drives.toTeleop();
    }
    
    public void init(){
        drives.start();
         hab.start();
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
