/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.subsystem;

import edu.wpi.first.wpilibj.Solenoid;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.robot.IO;

/**
 * Add your docs here.
 */
public class Hatch extends GenericSubsystem {

	private Solenoid flipper;

	private Solenoid shooter;

	private boolean flipperValue;

	private boolean shooterValue;

	private HatchState state;

	private double time;

	public Hatch() {
		super("Hatch");
	}

	public enum HatchState {
		STANDBY, FLIPPER, HOME, SHOOT_AND_FLIPPER;
	}

	public void init() {
		flipper = new Solenoid(IO.HATCH_SOLENOID_FLIPPER);
		shooter = new Solenoid(IO.HATCH_SOLENOID_SHOOTER);
		flipperValue = false;
		shooterValue = false;
		state = HatchState.STANDBY;
		time = 0;
	}

	public void execute() {
		switch (state) {
		case STANDBY:
			break;
		case FLIPPER:
			flipperValue = true;
			state = HatchState.STANDBY;
			break;
		case SHOOT_AND_FLIPPER:
			shooterValue = true;
			if (System.currentTimeMillis() > time + 0) {
				state = HatchState.FLIPPER;
			}
			break;
		case HOME:
			flipperValue = false;
			shooterValue = false;
			state = HatchState.STANDBY;
		}
		flipper.set(flipperValue);
		shooter.set(shooterValue);
	}

	public void flipperButton() {
		// System.out.println("FLIPPER");
		state = HatchState.FLIPPER;
	}

	public void shooterButton() {
		// System.out.println("Shooter");
		time = System.currentTimeMillis();
		state = HatchState.SHOOT_AND_FLIPPER;
	}

	public void homeButton() {
		// System.out.println("Home");
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
