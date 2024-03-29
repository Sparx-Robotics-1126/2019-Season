/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.subsystem;

import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;

import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.robot.IO;
import frc.util.Logger.LogHolder;
import frc.util.Logger.Loggable;

/**
 * Add your docs here.
 */
public class HAB extends GenericSubsystem implements Loggable{

	// ----------------------------------------Motors/Sensors----------------------------------------

	private WPI_TalonSRX leadScrewMtr;
	
	private WPI_TalonSRX leadScrewMtr2;

	private Encoder leadScrewEncRaw;

	private WPI_TalonSRX habLeft;

	private WPI_TalonSRX habRight;

	// ----------------------------------------Variables---------------------------------------------

	private double wantedSpeedLeft;

	private double wantedSpeedRight;

	private LeadScrewState state;

	// ----------------------------------------Constants---------------------------------------------

	private boolean isDone = false;
	
	private boolean logReady;

	// ------------------------------------------Code-------------------------------------------
	
	public HAB() {
		super("HAB");
	}

	@Override
	public void init() {
		leadScrewMtr = new WPI_TalonSRX(IO.HAB_LEADSCREWMOTOR.port);
		leadScrewMtr2 = new WPI_TalonSRX(IO.HAB_LEADSCREWSECONDMOTOR.port);
		leadScrewEncRaw = new Encoder(IO.HAB_LEADSCREWENCODER_CH1.port, IO.HAB_LEADSCREWENCODER_CH2.port);
		leadScrewEncRaw.setDistancePerPulse(0.0002698035829915821);
//		leadScrewEncRaw.setDistancePerPulse(0.0002278293558123873); //0.0002823311758 //0.0003366589558616
		leadScrewEncRaw.reset();
		habLeft = new WPI_TalonSRX(IO.HAB_LEFTMOTOR.port);
		habRight = new WPI_TalonSRX(IO.HAB_RIGHTMOTOR.port);
		habLeft.setInverted(true);
		habRight.setInverted(true);
		wantedSpeedLeft = 0;
		wantedSpeedRight = 0;
		// bottomSensor = new DigitalInput(14);
		state = LeadScrewState.STANDBY;
		logReady = true;
	}

	public enum LeadScrewState {
		STANDBY, UP, DOWN, PRE_ARMS, LEVELTWO;
	} 

	@Override
	public void execute() {
//		System.out.println("LeadScrewRate: " + leadScrewEncRaw.getRate());
		switch (state) {
		case STANDBY:
			break;
		case UP:
			if (leadScrewEncRaw.getDistance() < -1) {
				leadScrewMtr.set(1);
				leadScrewMtr2.set(1);
			} else {
				System.out.println("HAB up finished");
				stopHab();
			}
			break;
		case DOWN:
			if (leadScrewEncRaw.getDistance() > -21) {
				leadScrewMtr.set(-1);
				leadScrewMtr2.set(-1);
			} else {
				System.out.println("HAB down finished");
				stopHab();
			}
			break;
		case PRE_ARMS:
			if(leadScrewEncRaw.getDistance() > -2.25) {
				leadScrewMtr.set(-1);
				leadScrewMtr2.set(-1);
			} else {
				System.out.println("HAB prearms finished");
				stopHab();
			}
			break;
		case LEVELTWO:
			if(leadScrewEncRaw.getDistance( ) > -7.75) {
				leadScrewMtr.set(-1);
				leadScrewMtr2.set(-1);
			} else {
				System.out.println("HAB level two finished");
				stopHab();
			}
			break;
		}
		habLeft.set(wantedSpeedLeft);
		habRight.set(wantedSpeedRight);
	}
	
	public void setHabScrew(double value) {
		leadScrewMtr.set(value);
		leadScrewMtr2.set(value);
	}
	
	@Override
	public void delayedPrints() {
		System.out.println("Lead screw: " + leadScrewEncRaw.getDistance());
	}
	
	public void stopHab() {
		leadScrewMtr.set(0);
		leadScrewMtr2.set(0);
		isDone = true;
		state = LeadScrewState.STANDBY;
	}

	public void setHabPower(double dbl) {
		if(state == LeadScrewState.STANDBY) {
			leadScrewMtr.set(dbl);
			leadScrewMtr2.set(dbl);
		}
	}
	
	public void ctrlDown() {
		System.out.println("Moving HAB down");
		state = LeadScrewState.DOWN;
		isDone = false;
	}

	public void ctrlUP() {
		System.out.println("Moving HAB to home");
		state = LeadScrewState.UP;
		isDone = false;
	}
	
	public void ctrlLevelTwo() {
		System.out.println("Moving HAB to level two");
		state = LeadScrewState.LEVELTWO;
		isDone = false;
	}
	
	public void ctrlPreArms() {
		System.out.println("Moving HAB to prearms");
		state = LeadScrewState.PRE_ARMS;
		isDone = false;
	}
	
	public void setHabWheelsSpeed(double speed) {
		setHabSpeedLeft(-speed);
		setHabSpeedRight(speed);
	}
	
	public void stopHabWheels() {
		setHabSpeedLeft(0);
		setHabSpeedRight(0);
	}
	
	public boolean onPlatform() {
		return true;
	}

	public void setHabSpeedLeft(double speed) {
		wantedSpeedLeft = speed;
	}

	public void setHabSpeedRight(double speed) {
		wantedSpeedRight = speed;
	}

	@Override
	public boolean isDone() {
		return isDone;
	}

	@Override
	public long sleepTime() {
		return 20;
	}

	@Override
	public void smartDashboardInit() {
		addToTables(leadScrewMtr, "Lead Screw Motor");
		addToTables(leadScrewEncRaw, "Lead Screw Encoder");
		SmartDashboard.putData("Lead Screw Encoder", leadScrewEncRaw);
		addToTables(habLeft, "Arms", "Arms Left Wheels");
		addToTables(habRight, "Arms", "Arms Right Wheels");
	}
	
	public void stopAll() {
		stopHab();
		stopHabWheels();
	}
	
	@Override
	public void logPeriodic(LogHolder lh) {
		lh.updateLogClass("HAB_Periodic");
		lh.logLine("Lead Screw Motor: " + leadScrewMtr.get());
		lh.logLine("Lead Screw Encoder: " + leadScrewEncRaw.getDistance());
	}

	@Override
	public boolean logReady() {
		return logReady;
	}

}