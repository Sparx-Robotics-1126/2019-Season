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
		state = LeadScrewState.STANDBY;
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
				leadScrewMtr.set(0.75);
			} else {
				leadScrewMtr.set(0.0);
				state = LeadScrewState.STANDBY;
			}
			break;
		case DOWN:
			if (leadScrewEncRaw.getDistance() > -21) {
				leadScrewMtr.set(-1);
			} else {
				leadScrewMtr.set(0.0);
				state = LeadScrewState.STANDBY;
			}
			break;
		case PRE_ARMS:
			if(leadScrewEncRaw.getDistance() > -2.5) {
				leadScrewMtr.set(-1);
			} else {
				leadScrewMtr.set(0);
				state = LeadScrewState.STANDBY;
			}
		case HOME:
			if (bottomSensor.get()) {
				leadScrewMtr.set(0.3);
			} else {
				leadScrewMtr.set(0.0);
				leadScrewEncRaw.reset();
				state = LeadScrewState.STANDBY;
			}
			break;
		}
		habLeft.set(wantedSpeedLeft);
		habRight.set(wantedSpeedRight);
//		System.out.println("Lead screw: " + leadScrewEncRaw.getDistance());
	}

	public void ctrlDown() {
		state = LeadScrewState.DOWN;
	}

	public void ctrlUP() {
		state = LeadScrewState.UP;
	}
	
	public void ctrlPreArms() {
		state = LeadScrewState.PRE_ARMS;
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
		return false;
	}

	@Override
	public long sleepTime() {
		return 20;
	}

}