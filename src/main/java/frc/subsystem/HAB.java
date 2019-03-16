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

	private Encoder leadScrewEncRaw;

	private WPI_TalonSRX habLeft;

	private WPI_TalonSRX habRight;

	// ----------------------------------------Variables---------------------------------------------

	private double wantedSpeedLeft;

	private double wantedSpeedRight;

	private LeadScrewState state;

	// ----------------------------------------Constants---------------------------------------------

	private boolean isDone = false;

	// ------------------------------------------Code-------------------------------------------
	
	public HAB() {
		super("HAB");
	}

	@Override
	public void init() {
		leadScrewMtr = new WPI_TalonSRX(IO.HAB_LEADSCREWMOTOR);
		leadScrewEncRaw = new Encoder(IO.HAB_LEADSCREWENCODER_CH1, IO.HAB_LEADSCREWENCODER_CH2);
		leadScrewEncRaw.setDistancePerPulse(0.0002698035829915821);
//		leadScrewEncRaw.setDistancePerPulse(0.0002278293558123873); //0.0002823311758 //0.0003366589558616
		leadScrewEncRaw.reset();
		habLeft = new WPI_TalonSRX(IO.HAB_LEFTMOTOR);
		habRight = new WPI_TalonSRX(IO.HAB_RIGHTMOTOR);
		wantedSpeedLeft = 0;
		wantedSpeedRight = 0;
		// bottomSensor = new DigitalInput(14);
		state = LeadScrewState.STANDBY;
	}

	public enum LeadScrewState {
		STANDBY, UP, DOWN, PRE_ARMS, LEVELTWO;
	} 

	@Override
	public void execute() {
		switch (state) {
		case STANDBY:
			break;
		case UP:
			if (leadScrewEncRaw.getDistance() < -1) {
				leadScrewMtr.set(1);
			} else {
				stopHab();
			}
			break;
		case DOWN:
			if (leadScrewEncRaw.getDistance() > -21) {
				leadScrewMtr.set(-1);
			} else {
				stopHab();
			}
			break;
		case PRE_ARMS:
			if(leadScrewEncRaw.getDistance() > -2.5) {
				leadScrewMtr.set(-1);
			} else {
				stopHab();
			}
			break;
		case LEVELTWO:
			if(leadScrewEncRaw.getDistance() > -7.25) {
				leadScrewMtr.set(-1);
			} else {
				stopHab();
			}
			break;
		}
		habLeft.set(wantedSpeedLeft);
		habRight.set(wantedSpeedRight);
	}
	
	@Override
	public void delayedPrints() {
		System.out.println("Lead screw: " + leadScrewEncRaw.getDistance());
	}
	
	public void stopHab() {
		leadScrewMtr.set(0);
		isDone = true;
		state = LeadScrewState.STANDBY;
	}

	public void ctrlDown() {
		state = LeadScrewState.DOWN;
		isDone = false;
	}

	public void ctrlUP() {
		state = LeadScrewState.UP;
		isDone = false;
	}
	
	public void ctrlLevelTwo() {
		state = LeadScrewState.LEVELTWO;
		isDone = false;
	}
	
	public void ctrlPreArms() {
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

}