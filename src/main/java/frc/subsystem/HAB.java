/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.subsystem;

import frc.robot.IO;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;
import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.DigitalInput;

/**
 * Add your docs here.
 */
public class HAB extends GenericSubsystem {

	// ----------------------------------------Motors/Sensors----------------------------------------

	private WPI_TalonSRX leadScrewMtr;
	private Encoder leadScrewEncRaw;
	private LeadScrewState state;
	private DigitalInput bottomSensor;
	private WPI_TalonSRX habLeft;
	private WPI_TalonSRX habRight;

	// ----------------------------------------Variables---------------------------------------------

	private double wantedSpeedLeft;
	private double wantedSpeedRight;

	// ----------------------------------------Constants---------------------------------------------

	private boolean isDone;
	private boolean runHabWheels;
	
	public HAB() {
		super("Hab");
	}

	@Override
	public void init() {
		leadScrewMtr = new WPI_TalonSRX(IO.HAB_LEADSCREWMOTOR);
		leadScrewEncRaw = new Encoder(IO.HAB_LEADSCREWENCODER_CH1, IO.HAB_LEADSCREWENCODER_CH2);
		leadScrewEncRaw.setDistancePerPulse(.0002823311758);
		leadScrewEncRaw.reset();
		habLeft = new WPI_TalonSRX(IO.HAB_LEFTMOTOR);
		habRight = new WPI_TalonSRX(IO.HAB_RIGHTMOTOR);
		wantedSpeedLeft = 0;
		wantedSpeedRight = 0;
		// bottomSensor = new DigitalInput(14);
		runHabWheels = false;
		state = LeadScrewState.STANDBY;
		isDone = false;
	}

	public enum LeadScrewState {
		STANDBY, UP, DOWN, HOME, PRE_ARMS;
	}

	@Override
	public void execute() {
		switch (state) {
		case STANDBY:
			break;
		case UP:
			if (leadScrewEncRaw.getDistance() < 0) {
				leadScrewMtr.set(0.9);
			} else {
				stopHab();
			}
			break;
		case DOWN:
			if (leadScrewEncRaw.getDistance() > -22) {
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
		case HOME:
			if (bottomSensor.get()) {
				leadScrewMtr.set(0.3);
			} else {
				stopHab();
				leadScrewEncRaw.reset();
			}
			break;
		}
		if(runHabWheels) {
			
		}
		habLeft.set(wantedSpeedLeft);
		habRight.set(wantedSpeedRight);
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
	
	public void ctrlPreArms() {
		state = LeadScrewState.PRE_ARMS;
		isDone = false;
	}
	
	public void setHabWheelsSpeed(double speed) {
		setHabSpeedLeft(speed);
		setHabSpeedRight(speed);
		runHabWheels = true;
	}
	
	public void stopHabWheels() {
		setHabSpeedLeft(0);
		setHabSpeedRight(0);
		runHabWheels = false;
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
	public void debug() {

	}

	@Override
	public boolean isDone() {
		return isDone;
	}

	@Override
	public long sleepTime() {
		return 20;
	}

}