/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.subsystem;

import edu.wpi.first.wpilibj.Solenoid;
import edu.wpi.first.wpilibj.Timer;
import frc.robot.IO;

/**
 * Add your docs here.
 */
public class Hatch extends GenericSubsystem {

	private Solenoid flipper;

	private Solenoid shooter;
	
	private Solenoid holder;

	private boolean flipperValue;

	private boolean shooterValue;
	
	private boolean holderValue;
	
	private HatchState state;

	private double time;

	public Hatch() {
		super("Hatch");
	}

	public enum HatchState {
		STANDBY, FLIPPER, HOME, SHOOT_AND_FLIPPER;
	}

	public void init() {
		flipper = new Solenoid(IO.HATCH_SOLENOID_FLIPPER.getPort());
		shooter = new Solenoid(IO.HATCH_SOLENOID_SHOOTER.getPort());
		holder = new Solenoid(IO.HATCH_SOLENOID_HOLDER.getPort());
		flipperValue = false;
		shooterValue = false;
		state = HatchState.STANDBY;
		time = 0;
		holderValue = false;
	}

	public void execute() {
		switch (state) {
		case STANDBY:
			break;
		case FLIPPER:
			holderValue = true;
			flipperValue = true;
			state = HatchState.STANDBY;
			break;
		case SHOOT_AND_FLIPPER:
			holderValue = true;
			shooterValue = true;
			if (Timer.getFPGATimestamp() > time + 0) {
				state = HatchState.FLIPPER;
			}
			break;
		case HOME:
			holderValue = true;
			flipperValue = false;
			shooterValue = false;
			state = HatchState.STANDBY;
		}
		flipper.set(flipperValue);
		shooter.set(shooterValue);
		holder.set(holderValue);
	}
	
	public void toAuto() {
		setHolder(false);
	}
	
	public void toTele() {
		setHolder(true);
	}
	
	public void setHolder(boolean holderValue) {
		this.holderValue = holderValue;
	}

	public void flipperButton() {
		state = HatchState.FLIPPER;
	}

	public void shooterButton() {
		time = Timer.getFPGATimestamp();
		state = HatchState.SHOOT_AND_FLIPPER;
	}

	public void homeButton() {
		state = HatchState.HOME;
	}

	public boolean isDone() {
		return false;
	}

	public long sleepTime() {
		return 20;
	}

	@Override
	public void delayedPrints() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void smartDashboardInit() {
		addToTables(flipper, "Flipper");
		addToTables(shooter, "Shooter");
	}

}